package com.zanclus.scanalyzer;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * A singleton to store the application state and to act as the repository for costly dependencies which should only be created once.
 * 
 * @author <a href="mailto: deven.phillips@gmail.com">Deven Phillips</a>
 *
 */
public class ApplicationState {

	private static ApplicationState instance = null ;

	private HashMap<String, String> config = null ;

	private EntityManagerFactory emf = null ;

	private ExecutorService scanPool ;

	private ApplicationState(HashMap<String, String> config) {
		super() ;
		this.config = config ;
		emf = Persistence.createEntityManagerFactory("scanalyzer", this.config) ;
		scanPool = Executors.newFixedThreadPool(Integer.parseInt(config.get("scanalyzer.threads"))) ;
	}

	public static ApplicationState getInstance() {
		if (instance == null) {
			Init init = new Init(new String[0]) ;
			HashMap<String, String> configHolder = init.getConfig() ;
			return new ApplicationState(configHolder) ;
		} else {
			return instance ;
		}
	}

	public static ApplicationState getInstance(HashMap<String, String> config) {
		if (instance==null) {
			instance = new ApplicationState(config) ;
		}
		
		return instance ;
	}

	public EntityManager getEntityManager() {
		return emf.createEntityManager() ;
	}

	public HashMap<String, String> getConfig() {
		if (config==null) {
			config = new HashMap<>() ;
		}
		return config ;
	}

	public String getProp(String key) {
		if (config==null) {
			config = new HashMap<>() ;
		}
		return config.get(key) ;
	}

	public String getProp(String key, String defaultVal) {
		if (config==null) {
			config = new HashMap<>() ;
		}
		return config.get(key)==null?defaultVal:config.get(key) ;
	}

	public void setProp(String key, String value) {
		if (config==null) {
			config = new HashMap<>() ;
		}
		config.put(key, value) ;
	}

	/**
	 * Add a job to the {@link ExecutorService} queue to be executed when a thread is available.
	 * @param job The pre-defined {@link ScanRunner} instance to be run inside of the thread pool
	 */
	public void addScanToQueue(ScanRunner job) {
		scanPool.submit(job) ;
	}
}