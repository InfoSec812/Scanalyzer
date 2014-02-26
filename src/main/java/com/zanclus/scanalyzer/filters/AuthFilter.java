package com.zanclus.scanalyzer.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.zanclus.scanalyzer.security.UserRoleRequestWrapper;

public class AuthFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Intentionally left blank
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException {
		String token = ((HttpServletRequest)request).getHeader("api_key") ;
		UserRoleRequestWrapper wrapper = null ;
		if (token==null || token.trim().length()<1) {
			String login = ((HttpServletRequest)request).getHeader("login") ;
			String password = ((HttpServletRequest)request).getHeader("password") ;
			wrapper = new UserRoleRequestWrapper(login, password, (HttpServletRequest)request) ;
		} else {
			wrapper = new UserRoleRequestWrapper(token, (HttpServletRequest)request) ;
		}
		try {
			chain.doFilter(wrapper, response);
		} catch (IOException e) {
			throw new ServletException(e) ;
		}
	}

	@Override
	public void destroy() {
		// Intentionally left blank
	}

}