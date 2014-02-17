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
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String token = ((HttpServletRequest)request).getHeader("api_key") ;
		chain.doFilter(new UserRoleRequestWrapper(token, ((HttpServletRequest)request)), response) ;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}