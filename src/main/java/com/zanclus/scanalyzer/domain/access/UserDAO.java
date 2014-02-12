/**
 * 
 */
package com.zanclus.scanalyzer.domain.access;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.zanclus.scanalyzer.domain.entities.Token;
import com.zanclus.scanalyzer.domain.entities.User;

/**
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public class UserDAO extends GenericDAO<User, Long> {

	public Token getNewTokenForUser(Long id, String login, String password) {
		em.getTransaction().begin() ;
		User user = em.find(User.class, id) ;
		Token newToken = new Token() ;
		em.persist(newToken) ;
		user.getTokens().add(newToken) ;
		em.refresh(newToken) ;
		em.getTransaction().commit() ;
		if (user.getLogin().contentEquals(login) && user.validatePassword(password)) {
			return newToken ;
		} else {
			throw new WebApplicationException(Status.UNAUTHORIZED) ;
		}
	}
}
