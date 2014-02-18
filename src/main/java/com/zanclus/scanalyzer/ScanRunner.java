package com.zanclus.scanalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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

	private Host target ;

	private static final Logger LOG = LoggerFactory.getLogger(ScanRunner.class) ;

	/**
	 * This class captures the STDOUT from the NMAP command in a separate thread and persists it to the database.
	 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
	 *
	 */
	private static class StreamGobbler extends Thread {
		private InputStream is ;

		private Host target ;

		private boolean persist ;

		StreamGobbler(Host target, InputStream is, boolean persist) {
			this.target = target ;
			this.is = is ;
			this.persist = persist ;
		}

		public void run() {
			StringBuilder nmapOutput = new StringBuilder() ;
			List<String> ports = null ;
			if (persist) {
				ports = new ArrayList<>() ;
			}

			String operatingSystem = null ;

			// try/with - Such awesome, very technology
			try (BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")))) {
				operatingSystem = parseNmapOutput(nmapOutput, ports, operatingSystem, br);
				if (persist) {
					PortsDAO pDao = new PortsDAO() ;

					Host updatedHost = persistScanResults(nmapOutput, ports, operatingSystem, pDao);

					// Check to see if this latest scan matches the most recent previous scan
					// If they do not match, send a warning e-mail.
					List<Ports> lastTwoScans = pDao.getLastTwoScans(updatedHost.getId()) ;
					String scan1 = lastTwoScans.get(0).getPortStatus().toLowerCase() ;
					String scan2 = lastTwoScans.get(1).getPortStatus().toLowerCase() ;
					
					// If scan1 and scan2 results are not the same, then something changed and 
					// we need to send out an alert e-mail!
					if ((lastTwoScans.size()==2) &&	(!(scan1.contentEquals(scan2)))) {
						sendWarningMail(updatedHost, lastTwoScans);
					}
				} else {
					LOG.error("\n\nNMAP ERROR:\n\n" + nmapOutput.toString() + "\n\n") ;
				}
			} catch (IOException ioe) {
				LOG.error(ioe.getLocalizedMessage(), ioe) ;
			}
		}

		/**
		 * @param nmapOutput
		 * @param ports
		 * @param operatingSystem
		 * @param br
		 * @return
		 * @throws IOException
		 */
		private String parseNmapOutput(StringBuilder nmapOutput,
				List<String> ports, String operatingSystem, BufferedReader br)
				throws IOException {
			String line;
			while ((line = br.readLine())!=null) {
				nmapOutput.append(line) ;
				nmapOutput.append("\n") ;
				if (persist && line.matches("^[0-9]{1,5}+/(tcp|udp).*$")) {
					if (line.trim().length()>0) {
						ports.add(line) ;
					}
				} else if (line.startsWith("OS details: ")) {
					operatingSystem = line.split(": ")[1] ;
				}
			}
			return operatingSystem;
		}

		/**
		 * @param nmapOutput
		 * @param ports
		 * @param operatingSystem
		 * @param pDao
		 * @return
		 */
		private Host persistScanResults(StringBuilder nmapOutput,
				List<String> ports, String operatingSystem, PortsDAO pDao) {
			Scan scanResults = Scan.builder()
					.scanResults(nmapOutput.toString())
					.scanTime(new Date())
					.build();
			ScanDAO sDao = new ScanDAO() ;
			LOG.info("\n\nNMAP OUTPUT:\n\n" + scanResults.getScanResults() + "\n\n");
			scanResults = sDao.create(scanResults) ;

			HostDAO hDao = new HostDAO() ;
			Host updatedHost = hDao.adminFindById(target.getId()) ;
			updatedHost.setLastScanned(scanResults.getScanTime()) ;
			if (operatingSystem!=null) {
				updatedHost.setOperatingSystem(operatingSystem) ;
			}
			hDao.adminUpdate(updatedHost) ;
			
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
			portSet.setHost(updatedHost) ;
			pDao.create(portSet) ;

			scanResults.setTarget(updatedHost) ;
			sDao.update(scanResults) ;
			return updatedHost;
		}

		/**
		 * @param updated
		 * @param lastTwoScans
		 */
		private void sendWarningMail(Host updated, List<Ports> lastTwoScans) {
			Properties props = configureMailProperties();

			Session session = buildMailSession(props);

			Message msg = new MimeMessage(session);
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			try {
				msg.setFrom(new InternetAddress(WebContext
						.getProp("mail.from.address")));
				msg.setRecipient(
						RecipientType.TO,
						new InternetAddress(WebContext
								.getProp("mail.to.address")));
				msg.setSubject("Scanalyzer: '"
						+ updated.getAddress()
						+ "' has changed it's available services.");
				msg.setText("To whom it may concern,\n\n\tThe host "
						+ updated.getAddress()
						+ " had a change"
						+ "in the services which were detected when scanned with NMAP. This could be an "
						+ "expected change or it could mean that something is misconfigured. Regardless, we thought that "
						+ "you would like to know:\n\n\n"
						+ "Scan Results at "
						+ sdf.format(lastTwoScans.get(0)
								.getScanTime())
						+ ":\n\n"
						+ lastTwoScans.get(0).getPortStatus()
						+ "\n\n\n"
						+ "Scan Results at "
						+ sdf.format(lastTwoScans.get(1)
								.getScanTime())
						+ ":\n\n"
						+ lastTwoScans.get(1).getPortStatus()
						+ "\n\n\n");
				Transport.send(msg);
				LOG.error("Warning message sent.");
			} catch (MessagingException e) {
				LOG.error(e.getLocalizedMessage(), e);
			}
		}

		/**
		 * @param props
		 * @return
		 */
		private Session buildMailSession(Properties props) {
			Session session = null;
			if (WebContext.getProp("mail.smtp.auth")
					.contentEquals("true")) {
				session = Session.getInstance(props,
						new Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(
										WebContext
												.getProp("mail.username"),
										WebContext
												.getProp("mail.password"));
							}
						});
			} else {
				session = Session.getInstance(props);
			}
			return session;
		}

		/**
		 * @return
		 */
		private Properties configureMailProperties() {
			Properties props = new Properties();
			props.setProperty("mail.smtp.auth",
					WebContext.getProp("mail.smtp.auth"));
			props.setProperty(
					"mail.smtp.starttls.enable",
					WebContext
							.getProp("mail.smtp.starttls.enable"));
			props.setProperty("mail.smtp.host",
					WebContext.getProp("mail.smtp.host"));
			props.setProperty("mail.smtp.port",
					WebContext.getProp("mail.smtp.port"));
			return props;
		}
	}

	public ScanRunner(Host host) {
		super() ;
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
				LOG.error("NMAP exited with a non-zero status scanning host '"+target.getAddress()+"'") ;
			}
		} catch (Exception t) {
			LOG.error(t.getLocalizedMessage(), t) ;
		}
	}
}
