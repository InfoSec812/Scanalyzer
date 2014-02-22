package com.zanclus.scanalyzer.managment;

import net.gescobar.jmx.annotation.Description;
import net.gescobar.jmx.annotation.ManagedAttribute;

import com.zanclus.scanalyzer.listeners.WebContext;

/**
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
@Description("Management operations and attributes for controlling the ThreadPool for the scanner")
public class ScanPool {

	@ManagedAttribute(description="The number of threads allowed for the scanning thread pool.", readable=true, writable=true)
	public void setThreadPoolSize(int size) {
		WebContext.setThreadPoolSize(size) ;
	}

	public int getThreadPoolSize() {
		return WebContext.getThreadPoolSize() ;
	}
}
