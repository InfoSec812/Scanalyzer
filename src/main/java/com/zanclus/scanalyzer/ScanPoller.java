package com.zanclus.scanalyzer;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zanclus.scanalyzer.domain.entities.Host;
import com.zanclus.scanalyzer.listeners.WebContext;

/**
 * A {@link Job} scheduled in Quartz to run every minute to check for hosts which need to be scanned
 * 
 * @author <a href="mailto: deven.phillips@gmail.com">Deven Phillips</a>
 *
 */
public class ScanPoller implements Job {

	Logger log = null ;

	public ScanPoller() {
		super() ;
		log = LoggerFactory.getLogger(ScanPoller.class) ;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		EntityManager em = WebContext.getEntityManager() ;

		int interval = 0 ;
		try {
			interval = Integer.parseInt(WebContext.getProp("scanalyzer.interval", "3600")) ;
		} catch (NumberFormatException nfe) {
			interval = 3600 ;
			
		}

		Calendar cal = new GregorianCalendar() ;
		cal.add(GregorianCalendar.SECOND, (0-interval)) ;

		em.getTransaction().begin() ;
		List<Host> hostList = em.createQuery("FROM Host WHERE lastScanned<=:cutoff", Host.class).setParameter("cutoff", cal.getTime()).getResultList() ;
		em.getTransaction().commit() ;

		for (Host host: hostList) {
			WebContext.addScanToQueue(new ScanRunner(host)) ;
		}
	}
}