package com.zanclus.scanalyzer.domain.access;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import javax.persistence.NoResultException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.zanclus.scanalyzer.domain.entities.Host;
import com.zanclus.scanalyzer.domain.entities.User;
import com.zanclus.scanalyzer.listeners.WebContext;

/**
 * A Data Access Object for CCRUD operations on Host entities
 * 
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public class HostDAO extends GenericDAO<Host, Long> {
	private User user ;

	public HostDAO() {
		super() ;
	}

	public HostDAO(User user) {
		super() ;
		this.user = user ;
	}

	public Host adminFindById(Long id) {
		return super.findById(id) ;
	}

	public Host adminUpdate(Host entity) {
		return super.update(entity) ;
	}

	@Override
	public Host findById(Long id) {
		if (this.user==null) {
			throw new WebApplicationException(Status.UNAUTHORIZED) ;
		} else {
			em = WebContext.getEntityManager() ;
			em.getTransaction().begin() ;
			Host retVal = null ;
			try {
				retVal = em.createQuery("SELECT h FROM Host h WHERE h.owner.id=:id", Host.class).setParameter("id", user.getId()).getSingleResult() ;
			} catch (NoResultException nre) {
				throw new WebApplicationException(nre, Status.NOT_FOUND) ;
			} catch (Exception e) {
				LOG.error(e.getLocalizedMessage(), e) ;
				throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR) ;
			}
			em.getTransaction().commit() ;
			em.close() ;
			return retVal ;
		}
	}

	/**
	 * Get the {@link Host} entity by it's unique IP address
	 * @param address A {@link String} representation of either an IPv4 or IPv6 address
	 * @return The {@link Host} entity which has that address associated with it.
	 */
	public Host getHostByAddress(byte[] address) {
		if (this.user==null) {
			throw new WebApplicationException(Status.UNAUTHORIZED) ;
		} else {
			em = WebContext.getEntityManager() ;
			em.getTransaction().begin() ;
			Host retVal;
			try {
				retVal = em.createQuery("FROM Host h WHERE h.address=:address AND h.owner.id=:id", Host.class).setParameter("id", user.getId()).setParameter("address", address).getSingleResult();
			} catch (NoResultException nre) {
				throw new WebApplicationException(nre, Status.NOT_FOUND) ;
			}
			em.getTransaction().commit() ;
			em.close() ;
			
			return retVal ;
		}
	}

	@Override
	public void delete(Long id) {
		if (this.user==null) {
			throw new WebApplicationException(Status.UNAUTHORIZED) ;
		} else {
			int rowCount = 0 ;
			em = WebContext.getEntityManager();
			em.getTransaction().begin();
			rowCount = em.createQuery("DELETE FROM Host h WHERE h.id=:hostId AND h.owner.id=:userId").setParameter("hostId", id).setParameter("userId", user.getId()).executeUpdate();
			em.getTransaction().commit();
			em.close();

			if (rowCount!=1) {
				throw new WebApplicationException(Status.NOT_FOUND) ;
			}
		} 
	}

	@Override
	public Host update(Host entity) {
		if (this.user==null) {
			throw new WebApplicationException(Status.UNAUTHORIZED) ;
		} else {
			em = WebContext.getEntityManager();
			em.getTransaction().begin();
			Host h = em.find(Host.class, entity.getId()) ;
			if (h.getOwner().getId().equals(user.getId())) {
				h = em.merge(entity) ;
			} else {
				throw new WebApplicationException(Status.NOT_FOUND) ;
			}
			em.getTransaction().commit() ;
			em.close() ;
			return h ;
		} 
	}

	/**
	 * Add a new {@link Host} entity by it's unique IP address
	 * @param address A {@link String} representation of either an IPv4 or IPv6 address
	 * @return The {@link Host} entity which has that address associated with it.
	 * @throws UnknownHostException 
	 */
	public Host addHost(String address) throws UnknownHostException {
		Host newHost = null ;
		try {
			em = WebContext.getEntityManager() ;
			em.getTransaction().begin() ;
			newHost = Host.builder()
							.active(true)
							.added(new Date())
							.owner(user)
							.address(InetAddress.getByName(address).getAddress())
							.build() ;
			em.persist(newHost) ;
			em.getTransaction().commit() ;
			em.close() ;
		} catch (Exception e) {
			LOG.error(e.getLocalizedMessage(), e);
			throw e ;
		}
		
		return newHost ;
	}
}
