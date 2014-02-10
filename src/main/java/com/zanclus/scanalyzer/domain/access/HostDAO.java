/**
 * 
 */
package com.zanclus.scanalyzer.domain.access;

import java.util.Date;
import java.util.List;
import javax.persistence.NoResultException;
import com.zanclus.scanalyzer.domain.entities.Host;
import com.zanclus.scanalyzer.domain.entities.Scan;

/**
 * @author <a href="mailto: ***REMOVED***">Deven Phillips</a>
 *
 */
public class HostDAO extends GenericDAO<Host, Long> {

	public Host getHostByAddress(byte[] address) {
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
			em.getTransaction().begin() ;
			em.persist(newHost) ;
			em.getTransaction().commit() ;
		} catch (Exception e) {
			throw e ;
		}
		
		return newHost ;
	}

	public List<Scan> getHostScans(Long id) {
		em.getTransaction().begin() ;
		List<Scan> retVal = em.find(Host.class, id).getScans() ;
		em.getTransaction().commit() ;
	
		return retVal ;
	}
}
