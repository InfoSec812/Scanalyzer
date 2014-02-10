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

import com.zanclus.scanalyzer.Init;
import com.zanclus.scanalyzer.ScanRunner;

/**
 * @author <a href="mailto: deven.phillips@gmail.com">Deven Phillips</a>
 *
 */
public class WebContext implements ServletContextListener {

	private static EntityManagerFactory emf = null ;

	private static HashMap<String, String> config = null ;

	private static ExecutorService scanPool = null ;

	private static Logger log = null ;

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log = LoggerFactory.getLogger(WebContext.class) ;
		log.info("ServletContextListener loaded.") ;
		Init init = new Init((String[]) sce.getServletContext().getAttribute("args")) ;
		config = init.getConfig() ;
		emf = Persistence.createEntityManagerFactory("scanalyzer", config) ;
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
