/**
 * 
 */
package com.zanclus.scanalyzer.security;

import java.security.Principal;

import com.zanclus.scanalyzer.domain.entities.User;

/**
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public class ScanalyzerUserPrincipal implements Principal {

	private User user ;

	public ScanalyzerUserPrincipal(User user) {
		this.user = user ;
	}

	/* (non-Javadoc)
	 * @see java.security.Principal#getName()
	 */
	@Override
	public String getName() {
		return user.getLogin() ;
	}

	public User getUser() {
		return this.user ;
	}
}
