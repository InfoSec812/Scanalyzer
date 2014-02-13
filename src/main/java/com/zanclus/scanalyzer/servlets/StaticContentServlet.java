/**
 * 
 */
package com.zanclus.scanalyzer.servlets;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Servlet to send out static content for Swagger UI
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public class StaticContentServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5548898183035046413L;

	private Logger log = LoggerFactory.getLogger(this.getClass()) ;

	public StaticContentServlet() {
		super() ;
		
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo() ;
		if (path==null || path.contentEquals("/")) {
			path = "/index.html" ;
			resp.setHeader("Location", req.getContextPath()+path) ;
		}
		
		String fileName = path.substring(1) ;
		
		try (InputStream contentInputStream = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
			BufferedInputStream reader = new BufferedInputStream(contentInputStream) ;
			byte[] buffer = new byte[1024] ;
			int numRead = 0 ;
			int byteCount = 0 ;
			OutputStream out = resp.getOutputStream() ;
			while ((numRead = reader.read(buffer)) != -1) {
				out.write(buffer, 0, numRead) ;
				byteCount+=numRead ;
			}
			reader.close() ;
			resp.setContentLengthLong(byteCount) ;
			out.close() ;
		}
	}
}
