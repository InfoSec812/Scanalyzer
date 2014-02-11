package com.zanclus.scanalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

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
 * This is the base class to run an NMAP scan and store the results
 * 
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public class ScanRunner extends Thread {

	/**
	 * This class captures the STDOUT from the NMAP command in a separate thread and persists it to the database.
	 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
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
			ArrayList<String> ports = null ;
			if (persist) {
				ports = new ArrayList<>() ;
			}

			// try/with - Such awesome, very technology
			try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
				String line = null ;
				while ((line = br.readLine())!=null) {
					nmapOutput.append(line) ;
					nmapOutput.append("\n") ;
					if (persist && line.matches("^[0-9]{1,5}+/(tcp|udp).*$")) {
						if (line.trim().length()>0) {
							ports.add(line) ;
						}
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
					hDao.update(updated) ;
					scanResults.setTarget(updated) ;
					
					// Sort the port lines for later comparison
					// This would be a great place for Java 8's Lambdas!!!
					Collections.sort(ports) ;
					StringBuilder sb = new StringBuilder() ;
					for (String portLine: ports) {
						sb.append(portLine) ;
						sb.append("\n") ;
					}

					// Save the ports lines
					Ports portSet = Ports.builder()
									.scanTime(scanResults.getScanTime())
									.portStatus(sb.toString())
									.build() ;
					PortsDAO pDao = new PortsDAO() ;
					portSet.setHost(updated) ;
					pDao.create(portSet) ;
					ScanDAO sDao = new ScanDAO() ;
					log.info("\n\nNMAP OUTPUT:\n\n" + scanResults.getScanResults() + "\n\n");
					scanResults = sDao.create(scanResults) ;

					// Check to see if this latest scan matches the most recent previous scan
					// If they do not match, send a warning e-mail.
					List<Ports> lastTwoScans = pDao.getPagedPortsHistoryByHostId(updated.getId(), 2, 0) ;
					if (!(lastTwoScans.get(0).getPortStatus().toLowerCase().contentEquals(lastTwoScans.get(1).getPortStatus().toLowerCase()))) {
						// The last 2 scans had different port states, so we need to send an alert!
						
						Properties props = new Properties() ;
						props.setProperty("mail.smtp.auth", WebContext.getProp("mail.smtp.auth")) ;
						props.setProperty("mail.smtp.starttls.enable", WebContext.getProp("mail.smtp.starttls.enable")) ;
						props.setProperty("mail.smtp.host", WebContext.getProp("mail.smtp.host")) ;
						props.setProperty("mail.smtp.port", WebContext.getProp("mail.smtp.port")) ;

						Session session = null ;
						if (WebContext.getProp("mail.smtp.auth").contentEquals("true")) {
							session = Session.getInstance(props, new Authenticator() {
								protected PasswordAuthentication getPasswordAuthentication() {
									return new PasswordAuthentication(WebContext.getProp("mail.username"), WebContext.getProp("mail.password")) ;
								}
							});
						} else {
							session = Session.getInstance(props) ;
						}

						Message msg = new MimeMessage(session) ;
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
						try {
							msg.setFrom(new InternetAddress(WebContext.getProp("mail.from.address"))) ;
							msg.setRecipient(RecipientType.TO, new InternetAddress(WebContext.getProp("mail.to.address"))) ;
							msg.setSubject("Scanalyzer: '"+updated.getAddress()+"' has changed it's available services.") ;
							msg.setText("To whom it may concern,\n\n\tThe host "+updated.getAddress()+" had a change" + 
									"in the services which were detected when scanned with NMAP. This could be an "+
									"expected change or it could mean that something is misconfigured. Regardless, we thought that "+
									"you would like to know:\n\n\n" +
									"Scan Results at "+sdf.format(lastTwoScans.get(0).getScanTime())+":\n\n"+lastTwoScans.get(0).getPortStatus()+"\n\n\n" +
									"Scan Results at "+sdf.format(lastTwoScans.get(1).getScanTime())+":\n\n"+lastTwoScans.get(1).getPortStatus()+"\n\n\n") ;
							Transport.send(msg) ;
							log.error("Warning message sent.") ;
						} catch (MessagingException e) {
							log.error(e.getLocalizedMessage(), e) ;
						}
					}
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
