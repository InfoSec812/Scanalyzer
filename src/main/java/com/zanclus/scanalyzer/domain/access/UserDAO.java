/**
 * 
 */
package com.zanclus.scanalyzer.domain.access;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zanclus.scanalyzer.domain.entities.Token;
import com.zanclus.scanalyzer.domain.entities.User;

/**
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public class UserDAO extends GenericDAO<User, Long> {

	private Logger log ;

	public UserDAO() {
		super() ;
		log = LoggerFactory.getLogger(UserDAO.class) ;
	}

	public Token getNewTokenForUser(Long id, String login, String password) {
		em.getTransaction().begin() ;
		User user = em.find(User.class, id) ;
		Token newToken = new Token() ;
		em.persist(newToken) ;
		user.getTokens().add(newToken) ;
		em.getTransaction().commit() ;
		log.debug("Committed token transaction") ;
		if (user.getLogin().contentEquals(login) && user.validatePassword(password)) {
			return newToken ;
		} else {
			throw new WebApplicationException(Status.UNAUTHORIZED) ;
		}
	}
}
