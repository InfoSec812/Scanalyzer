/**
 * 
 */
package com.zanclus.scanalyzer.domain.access;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.zanclus.scanalyzer.ApplicationState;
import com.zanclus.scanalyzer.domain.entities.Host;
import com.zanclus.scanalyzer.domain.entities.Scan;

/**
 * @author <a href="mailto: deven.phillips@gmail.com">Deven Phillips</a>
 *
 */
public class HostDAO {

	private ApplicationState state = null ;

	public HostDAO() {
		super() ;
		state = ApplicationState.getInstance() ;
	}

	public Host getHostById(Long id) {
		EntityManager em = state.getEntityManager() ;
		em.getTransaction().begin() ;
		Host retVal = em.find(Host.class, id) ;
		em.getTransaction().commit() ;
	
		return retVal ;
	}

	public Host getHostByAddress(byte[] address) {
		EntityManager em = state.getEntityManager() ;
		em.getTransaction().begin() ;
		Host retVal;
		try {
			retVal = em.createQuery("FROM Host WHERE address=:address", Host.class).setParameter("address", address).getSingleResult();
		} catch (NoResultException nre) {
			return null ;
		}
		em.getTransaction().commit() ;
	
		return retVal ;
	}

	public Host addHost(byte[] address) {
		Host newHost = new Host() ;
		newHost.setActive(true) ;
		newHost.setAdded(new Date()) ;
		newHost.setAddress(address) ;

		try {
			EntityManager em = state.getEntityManager() ;
			em.getTransaction().begin() ;
			em.persist(newHost) ;
			em.getTransaction().commit() ;
		} catch (Exception e) {
			throw e ;
		}
		
		return newHost ;
	}

	public Host updateHost(Host updated) {
		EntityManager em = state.getEntityManager() ;
		em.getTransaction().begin() ;
		Host retVal = em.find(Host.class, updated.getId()) ;
		if (updated.getActive()!=null) {
			retVal.setActive(updated.getActive()) ;
		}
		em.getTransaction().commit() ;
		
		return retVal  ;
	}

	public void deleteHost(Long id) {
		EntityManager em = state.getEntityManager() ;
		em.getTransaction().begin() ;
		em.remove(em.find(Host.class, id)) ;
		em.getTransaction().commit() ;
	}

	public List<Scan> getHostScans(Long id) {
		EntityManager em = state.getEntityManager() ;
		em.getTransaction().begin() ;
		List<Scan> retVal = em.find(Host.class, id).getScans() ;
		em.getTransaction().commit() ;
	
		return retVal ;
	}
}
