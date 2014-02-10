package com.zanclus.scanalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zanclus.scanalyzer.domain.access.HostDAO;
import com.zanclus.scanalyzer.domain.access.PortsDAO;
import com.zanclus.scanalyzer.domain.access.ScanDAO;
import com.zanclus.scanalyzer.domain.entities.Host;
import com.zanclus.scanalyzer.domain.entities.Ports;
import com.zanclus.scanalyzer.domain.entities.Scan;
import com.zanclus.scanalyzer.listeners.WebContext;

/**
 * This is the base class to run an NMAP scan and return the results
 * @author <a href="mailto: deven.phillips@gmail.com">Deven Phillips</a>
 *
 */
public class ScanRunner extends Thread {

	/**
	 * This class captures the STDOUT from the NMAP command in a separate thread and persists it to the database.
	 * @author <a href="mailto: deven.phillips@gmail.com">Deven Phillips</a>
	 *
	 */
	private class StreamGobbler extends Thread {
		private InputStream is ;

		private Host target ;

		private boolean persist ;

		private Logger log = LoggerFactory.getLogger(ScanRunner.StreamGobbler.class) ;

		StreamGobbler(Host target, InputStream is, boolean persist) {
			this.target = target ;
			this.is = is ;
			this.persist = persist ;
		}

		public void run() {
			StringBuilder nmapOutput = new StringBuilder() ;
			StringBuilder ports = null ;
			if (persist) {
				ports = new StringBuilder() ;
			}

			// try/with - Such awesome, very technology
			try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
				String line = null ;
				while ((line = br.readLine())!=null) {
					nmapOutput.append(line) ;
					nmapOutput.append("\n") ;
					if (persist && line.matches("^[0-9]{1,3}/")) {
						ports.append(line) ;
						ports.append("\n") ;
					}
				}
				if (persist) {
					Scan scanResults = Scan.builder()
							.scanResults(nmapOutput.toString())
							.scanTime(new Date())
							.build();
					HostDAO hDao = new HostDAO() ;
					Host updated = hDao.findById(target.getId()) ;
					updated.setLastScanned(scanResults.getScanTime()) ;
					updated = hDao.update(updated) ;
					scanResults.setTarget(updated) ;
					Ports portSet = Ports.builder().host(updated).scanTime(scanResults.getScanTime()).portStatus(ports.toString()).build() ;
					PortsDAO pDao = new PortsDAO() ;
					pDao.create(portSet) ;
					ScanDAO sDao = new ScanDAO() ;
					log.info("\n\nNMAP OUTPUT:\n\n" + scanResults.getScanResults() + "\n\n");
					scanResults = sDao.create(scanResults) ;
				} else {
					log.error("\n\nNMAP ERROR:\n\n"
							+ nmapOutput.toString() + "\n\n") ;
				}
			} catch (IOException ioe) {
				log.error(ioe.getLocalizedMessage(), ioe) ;
			}
		}
	}

	private Host target ;

	private Logger log ;

	public ScanRunner(Host host) {
		super() ;
		log = LoggerFactory.getLogger(ScanRunner.class) ;
		this.target = host ;
	}

	public void run() {

		StreamGobbler stdOut ;
		StreamGobbler stdErr ;

		try {
			Runtime rt = Runtime.getRuntime() ;
			Process proc = rt.exec(WebContext.getProp("scanalyzer.nmap.path")+" -sT -O -P0 "+target.getAddress()) ;

			stdOut = new StreamGobbler(target, proc.getInputStream(), true) ;
			stdErr = new StreamGobbler(target, proc.getErrorStream(), false) ;
			
			stdOut.start() ;
			stdErr.start() ;

			int exitVal = proc.waitFor() ;
			if (exitVal>0) {
				log.error("NMAP exited with a non-zero status scanning host '"+target.getAddress()+"'") ;
			}
		} catch (Throwable t) {
			log.error(t.getLocalizedMessage(), t) ;
		}
	}
}
