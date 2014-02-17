/**
 * 
 */
package com.zanclus.scanalyzer.listeners;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zanclus.scanalyzer.ScanRunner;
import com.zanclus.scanalyzer.domain.entities.User;

/**
 * The is a ServletContextListener which set up and stores references to expensive resources like the
 * application's parsed configuration and Java Persistence {@link EntityManagerFactory}
 * 
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public class WebContext implements ServletContextListener {

	private static EntityManagerFactory emf = null ;

	private static HashMap<String, String> config = null ;

	private static ExecutorService scanPool = null ;

	private static Logger log = null ;

	public WebContext() {
		super() ;
		log = LoggerFactory.getLogger(WebContext.class) ;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@SuppressWarnings("unchecked")   // Because Java 7 still has no ability to check parameterized types.... Booo!!
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("ServletContextListener loading.") ;
		config = (HashMap<String, String>) sce.getServletContext().getAttribute("config") ;
		emf = Persistence.createEntityManagerFactory("scanalyzer", config) ;

		// For in-memory databases which are not persistent, create a default admin account...
		EntityManager em = emf.createEntityManager();
		
		em.getTransaction().begin();
		int count = em.createQuery("FROM User u WHERE u.admin=true", User.class).getResultList().size() ;
		if (count==0) {
			log.info("No admin accounts currently exist, we'll create one for you.") ;
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
		log.info("Default admin account created");

		scanPool = Executors.newFixedThreadPool(Integer.parseInt(config.get("scanalyzer.threads"))) ;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		emf.close() ;
	}

	public static EntityManager getEntityManager() {
		if (emf == null) {
			throw new IllegalStateException("ServletContext has not yet been initialized!") ;
		}
		return emf.createEntityManager() ;
	}

	public static HashMap<String, String> getConfig() {
		if (config==null) {
			config = new HashMap<>() ;
		}
		return config ;
	}

	public static String getProp(String key) {
		if (config==null) {
			config = new HashMap<>() ;
		}
		return config.get(key) ;
	}

	public static String getProp(String key, String defaultVal) {
		if (config==null) {
			config = new HashMap<>() ;
		}
		return config.get(key)==null?defaultVal:config.get(key) ;
	}

	public static void setProp(String key, String value) {
		if (config==null) {
			config = new HashMap<>() ;
		}
		config.put(key, value) ;
	}

	/**
	 * Add a job to the {@link ExecutorService} queue to be executed when a thread is available.
	 * @param job The pre-defined {@link ScanRunner} instance to be run inside of the thread pool
	 */
	public static void addScanToQueue(ScanRunner job) {
		scanPool.submit(job) ;
	}
}
