package com.zanclus.scanalyzer.domain.access;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zanclus.scanalyzer.domain.entities.Token;
import com.zanclus.scanalyzer.domain.entities.User;
import com.zanclus.scanalyzer.listeners.WebContext;

/**
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public class UserDAO extends GenericDAO<User, Long> {

	private User user ;
	private static final Logger LOG = LoggerFactory.getLogger(UserDAO.class) ;

	public UserDAO(User user) {
		super() ;
		this.user = user ;
	}

	@Override
	public User findById(Long id) {
		if (user.getAdmin()) {
			return super.findById(id) ;
		} else {
			User retVal = super.findById(id) ;
			if (retVal.getId().equals(user.getId())) {
				throw new WebApplicationException(Status.NOT_FOUND) ;
			} else {
				return retVal ;
			}
		}
	}

	@Override
	public User update(User entity) {
		if (user.getAdmin()) {
			return super.update(entity) ;
		} else {
			User retVal = super.findById(entity.getId()) ;
			if (retVal.getId().equals(user.getId())) {
				throw new WebApplicationException(Status.NOT_FOUND) ;
			} else {
				return super.update(entity) ;
			}
		}
	}

	/**
	 * Create and associate a new token with the specified user account IF the credentials are correct.
	 * @param id The {@link Long} value representing the primary key on the users table.
	 * @param login The login (username) ID
	 * @param password The password for the account
	 * @return A {@link Token} which contains a randomly generated UUID string to be used for API authentication
	 */
	public Token getNewTokenForUser(Long id) {
		em = WebContext.getEntityManager() ;
		em.getTransaction().begin() ;
		if (user.getAdmin()) {
			user = em.find(User.class, id) ;
		} else {
			user = em.find(User.class, user.getId()) ;
		}
		Token newToken = new Token() ;
		newToken.setUser(user) ;
		em.persist(newToken) ;
		em.getTransaction().commit() ;
		em.close();
		LOG.debug("Committed token transaction: "+user.getTokens().size()) ;
		if (user.getActive()) {
			return newToken ;
		} else {
			throw new WebApplicationException(Status.UNAUTHORIZED) ;
		}
	}
}
