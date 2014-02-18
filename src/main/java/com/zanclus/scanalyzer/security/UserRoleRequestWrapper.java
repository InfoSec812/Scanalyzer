package com.zanclus.scanalyzer.security;

import java.security.Principal;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zanclus.scanalyzer.domain.entities.User;
import com.zanclus.scanalyzer.listeners.WebContext;

/**
 * An extension for the HTTPServletRequest that overrides the getUserPrincipal()
 * and isUserInRole(). We supply these implementations here, where they are not
 * normally populated unless we are going through the facility provided by the
 * container.
 * <p>
 * If he user or roles are null on this wrapper, the parent request is consulted
 * to try to fetch what ever the container has set for us. This is intended to
 * be created and used by the UserRoleFilter.
 * 
 * @author thein
 * 
 */
public class UserRoleRequestWrapper extends HttpServletRequestWrapper {

	private User user;
	private HttpServletRequest realRequest;
	private static final Logger LOG = LoggerFactory.getLogger(UserRoleRequestWrapper.class) ;

	public UserRoleRequestWrapper(String token, HttpServletRequest request) {
		super(request);

		if (token!=null && token.trim().length()>0) {
			EntityManager em = WebContext.getEntityManager() ;
			try {
				em.getTransaction().begin() ;
				user = em.createQuery("SELECT u FROM Token t INNER JOIN t.user u WHERE t.token=:token AND u.active=true AND u.enabled=true", User.class)
					.setParameter("token", token)
					.getSingleResult() ;
				em.getTransaction().commit() ;
				em.close() ;
			} catch (Exception e) {
				LOG.info("Unable to map token to user.", e) ;
			}
		}
		this.realRequest = request;
	}

	public UserRoleRequestWrapper(String login, String password, HttpServletRequest request) {
		super(request);
		LOG = LoggerFactory.getLogger(UserRoleRequestWrapper.class) ;
		
		if (login!=null && login.trim().length()>0 && password!=null && password.trim().length()>0) {
			EntityManager em = WebContext.getEntityManager() ;
			try {
				em.getTransaction().begin() ;
				user = em.createQuery("FROM User u WHERE u.login=:login AND u.active=true AND u.enabled=true", User.class)
					.setParameter("login", login)
					.getSingleResult() ;
				em.getTransaction().commit() ;
				em.close() ;
				
				if (!user.validatePassword(password)) {
					user = null ;
				}
			} catch (Exception e) {
				LOG.info("Unable to map token to user.", e) ;
			}
		}
		this.realRequest = request;
	}

	@Override
	public boolean isUserInRole(String role) {
		boolean retVal = false ;
		if (user!=null) {
			if (role.toLowerCase().contentEquals("admin") && user.getAdmin()) {
				retVal = true ;
			} else if (role.toLowerCase().contentEquals("user")) {
				retVal = true ;
			}
		}
		return retVal ;
	}

	@Override
	public Principal getUserPrincipal() {
		if (this.user == null) {
			return realRequest.getUserPrincipal();
		}

		// make an anonymous implementation to just return our user
		return new ScanalyzerUserPrincipal(user) ;
	}
}
