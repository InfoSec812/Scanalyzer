package com.zanclus.scanalyzer ;

import java.util.EnumSet;
import java.util.Map;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
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
import com.zanclus.scanalyzer.filters.AuthFilter;
import com.zanclus.scanalyzer.listeners.WebContext;
import com.zanclus.scanalyzer.servlets.StaticContentServlet;

/**
 * The application's invocation class.
 * @author <a href="mailto:deven.phillips@gmail.com">Deven Phillips</a>
 *
 */
public class Scanalyzer {

	private static Map<String, String> config ;

	/**
	 * Start the scAnalyzer application.
	 * @param args The command-line arguments
	 * @throws Exception If there is a problem starting up the embedded Jetty servlet container
	 */
	public static void main(String[] args) throws Exception {

		// Parse the command-line arguments
		Init init = new Init(args) ;
		config = init.getConfig() ;

		if (!init.shouldExit()) {
			// Start up the embedded Jetty Servlet container
			Server server = new Server(Integer.parseInt(config.get("scanalyzer.port")));
			ServletContextHandler context = new ServletContextHandler();
			context.setContextPath("/*");
			context.setAttribute("config", config);
			server.setHandler(context);
			// Add a ServletContextListener for some shared resources which are expensive to generate
			context.addEventListener(new WebContext());
			// Add an instance of the Jersey servlet for ReST
			context.addServlet(createJerseyServlet(), "/rest/*");
			// Add an instance of the Swagger servlet to provide a nice UI for the API
			context.addServlet(createSwaggerServlet(), "/api/*");
			// Add a static content server so that we can include the Swagger UI in the executable JAR
			context.addServlet(createStaticServlet(), "/static/*");
			// Add a filter to handle attaching a user Principal to authenticated requests
			context.addFilter(createAuthFilter(), "/rest/*",
					EnumSet.of(DispatcherType.REQUEST));
			// Add a CORS filter to allow Swagger UI to work when NOT accessed inside of the application
			FilterHolder corsFilter = new FilterHolder(new CrossOriginFilter());
			corsFilter.setInitParameter("allowedOrigins", "*");
			corsFilter
					.setInitParameter("allowedMethods", "GET,POST,PUT,DELETE");
			context.addFilter(corsFilter, "/*",
					EnumSet.of(DispatcherType.REQUEST));
			context.addFilter(createAuthFilter(), "/*",
					EnumSet.of(DispatcherType.REQUEST));
			// Start Jetty embedded
			server.start();
			// This MUST be run AFTER the Jetty server starts because it needs access 
			// to the ServletContextListener's state information
			// Start the scanning poller
			startPollingScheduler();
			// Join the Jetty master thread and wait for it to exit.
			server.join();
		}
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
		swagger.setInitParameter("swagger.api.basepath", "http://localhost:8080/rest") ;
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
		restServlet.setInitParameter("com.sun.jersey.config.feature.Trace", config.get("com.sun.jersey.config.feature.Trace")==null?"false":config.get("com.sun.jersey.config.feature.Trace")) ;
		restServlet.setInitOrder(1) ;
		restServlet.setServlet(new ServletContainer()) ;
		return restServlet;
	}

	private static ServletHolder createStaticServlet() {
		ServletHolder staticServlet = new ServletHolder() ;
		staticServlet.setServlet(new StaticContentServlet());
		return staticServlet ;
	}

	private static FilterHolder createAuthFilter() {
		FilterHolder fh = new FilterHolder() ;
		fh.setFilter(new AuthFilter()) ;
		return fh ;
	}
}
