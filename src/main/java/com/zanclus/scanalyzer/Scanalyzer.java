/**
 * 
 */
package com.zanclus.scanalyzer ;

import java.util.HashMap;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.wordnik.swagger.jersey.config.JerseyJaxrsConfig;
import com.zanclus.scanalyzer.listeners.WebContext;

/**
 * The applications invocation class.
 * @author <a href="mailto:***REMOVED***">Deven Phillips</a>
 *
 */
public class Scanalyzer {

	private static HashMap<String, String> config ;

	/**
	 * Start the scAnalyzer application.
	 * @param args The command-line arguments
	 * @throws Exception If there is a problem starting up the embedded Jetty servlet container
	 */
	public static void main(String[] args) throws Exception {

		// Parse the command-line arguments
		Init init = new Init(args) ;
		config = init.getConfig() ;

		// Start up the embedded Jetty Servlet container
		Server server = new Server(Integer.parseInt(config.get("scanalyzer.port"))) ;
		ServletContextHandler context = new ServletContextHandler() ;
		context.setContextPath("/") ;
		context.setAttribute("config", config);
		server.setHandler(context) ;

		// Add a ServletContextListener for some shared resources which are expensive to generate
		context.addEventListener(new WebContext());

		context.addServlet(createJerseyServlet(), "/rest/*") ;

		context.addServlet(createSwaggerServlet(), "/api/*") ;

		server.start() ;

		// This MUST be run AFTER the Jetty server starts because it needs access 
		// to the ServletContextListener's state information
		startPollingScheduler();

		server.join() ;
	}

	/**
	 * Starts the Quartz scheduler and sets it running the poller job every 1 minute
	 * @throws SchedulerException
	 */
	private static void startPollingScheduler() throws SchedulerException {
		SchedulerFactory sf = new StdSchedulerFactory() ;
		Scheduler sched = sf.getScheduler() ;
		sched.start() ;
		JobDetail jd = JobBuilder.newJob(ScanPoller.class).build() ;
		Trigger trigger = TriggerBuilder
								.newTrigger()
								.withIdentity("pollTrigger")
								.withSchedule(
										SimpleScheduleBuilder
											.simpleSchedule()
											.withIntervalInMinutes(1)
											.repeatForever())
								.build() ;
		sched.scheduleJob(jd, trigger) ;
	}

	/**
	 * Creates a servlet holder containing the Swagger servlet instance
	 * @return An instance of {@link ServletHolder} containing the Swagger servlet instance
	 */
	private static ServletHolder createSwaggerServlet() {
		ServletHolder swagger = new ServletHolder() ;
		swagger.setInitParameter("api.version", "1.0.0") ;
		swagger.setInitParameter("swagger.api.basepath", "http://localhost:8002/api") ;
		swagger.setInitOrder(2) ;
		swagger.setServlet(new JerseyJaxrsConfig()) ;
		return swagger;
	}

	/**
	 * Creates a servlet holder containing the Jersey servlet instance
	 * @return An instance of {@link ServletHolder} containing the Jersey servlet instance
	 */
	private static ServletHolder createJerseyServlet() {
		ServletHolder restServlet = new ServletHolder() ;
		restServlet.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
		restServlet.setInitParameter("com.sun.jersey.config.property.packages", "com.zanclus.scanalyzer.services;org.codehaus.jackson.jaxrs;com.wordnik.swagger.jersey.listing"); 
		restServlet.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true") ;
		restServlet.setInitOrder(1) ;
		restServlet.setServlet(new ServletContainer()) ;
		return restServlet;
	}

}
