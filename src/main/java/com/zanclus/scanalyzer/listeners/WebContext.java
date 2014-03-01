package com.zanclus.scanalyzer.listeners;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.gescobar.jmx.Management;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zanclus.scanalyzer.ScanRunner;
import com.zanclus.scanalyzer.domain.access.HostDAO;
import com.zanclus.scanalyzer.domain.entities.User;
import com.zanclus.scanalyzer.managment.ScanPool;

/**
 * The is a ServletContextListener which set up and stores references to expensive resources like the
 * application's parsed configuration and Java Persistence {@link EntityManagerFactory}
 * 
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public class WebContext implements ServletContextListener {

	private static EntityManagerFactory emf = null ;

	private static Map<String, String> config = null ;

	private static ThreadPoolExecutor scanPool = null ;

	private static final Logger LOG = LoggerFactory.getLogger(WebContext.class) ;

	public WebContext() {
		super() ;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@SuppressWarnings("unchecked")   // Because Java 7 still has no ability to check parameterized types.... Booo!!
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		LOG.info("ServletContextListener loading.") ;
		WebContext.config = (Map<String, String>) sce.getServletContext().getAttribute("config") ;
		if (WebContext.config==null) {
			WebContext.config = new ConcurrentHashMap<>();
		}
		WebContext.emf = Persistence.createEntityManagerFactory("scanalyzer", WebContext.config) ;

		try {
			Management.register(new ScanPool(), "com.zanclus.scanalyzer:type=ScanPool") ;
			Management.register(new HostDAO(), "com.zanclus.scanalyzer:type=HostDAO") ;
		} catch (Exception e) {
			LOG.warn("Unable to register management bean for ScanPool", e) ;
		}

		// For instances where the database does not already contain an admin account, we create one!
		EntityManager em = WebContext.emf.createEntityManager();
		
		em.getTransaction().begin();
		int count = em.createQuery("FROM User u WHERE u.admin=true", User.class).getResultList().size() ;
		if (count==0) {
			LOG.info("No admin accounts currently exist, we'll create one for you.") ;
			User adminUser = User.builder()
					.familyName("Administrator")
					.givenName("Systems")
					.login("admin")
					.password("changeme")
					.active(true)
					.admin(true)
					.enabled(true)
					.build() ;
			em.persist(adminUser);
		}
		em.getTransaction().commit();
		em.close() ;
		LOG.info("Default admin account created");

		WebContext.scanPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Integer.parseInt(config.get("scanalyzer.threads"))) ;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		WebContext.emf.close() ;
	}

	public static EntityManager getEntityManager() {
		if (WebContext.emf == null) {
			throw new IllegalStateException("ServletContext has not yet been initialized!") ;
		}
		return WebContext.emf.createEntityManager() ;
	}

	public static Map<String, String> getConfig() {
		return WebContext.config ;
	}

	public static String getProp(String key) {
		return WebContext.config.get(key) ;
	}

	public static String getProp(String key, String defaultVal) {
		return WebContext.config.get(key)==null?defaultVal:config.get(key) ;
	}

	public static void setProp(String key, String value) {
		WebContext.config.put(key, value) ;
	}

	/**
	 * Add a job to the {@link ExecutorService} queue to be executed when a thread is available.
	 * @param job The pre-defined {@link ScanRunner} instance to be run inside of the thread pool
	 */
	public static void addScanToQueue(ScanRunner job) {
		WebContext.scanPool.submit(job) ;
	}

	public static void setThreadPoolSize(int size) {
		WebContext.scanPool.setCorePoolSize(size) ;
	}

	public static int getThreadPoolSize() {
		return WebContext.scanPool.getCorePoolSize() ;
	}
}
