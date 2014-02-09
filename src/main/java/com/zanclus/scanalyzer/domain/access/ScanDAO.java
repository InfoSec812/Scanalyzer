/**
 * 
 */
package com.zanclus.scanalyzer.domain.access;

import java.util.List;

import javax.persistence.EntityManager;

import com.zanclus.scanalyzer.ApplicationState;
import com.zanclus.scanalyzer.domain.entities.Scan;

/**
 * @author <a href="mailto: ***REMOVED***">Deven Phillips</a>
 *
 */
public class ScanDAO {
	private ApplicationState state = null ;

	public ScanDAO() {
		super() ;
		state = ApplicationState.getInstance() ;
	}

	public Scan getScanById(Long id) {
		EntityManager em = state.getEntityManager() ;
		em.getTransaction().begin() ;
		Scan retVal = em.find(Scan.class, id) ;
		em.getTransaction().commit() ;
	
		return retVal ;
	}

	public Scan addScan(Scan newScan) {
		try {
			EntityManager em = state.getEntityManager() ;
			em.getTransaction().begin() ;
			em.persist(newScan) ;
			em.getTransaction().commit() ;
		} catch (Exception e) {
			throw e ;
		}
		
		return newScan ;
	}

	public List<Scan> getScansByHostId(Long id) {
		try {
			EntityManager em = state.getEntityManager() ;
			em.getTransaction().begin() ;
			List<Scan> retVal = em.createQuery("FROM Scan s WHERE s.target.id=:id", Scan.class).setParameter("id", id).getResultList() ;
			em.getTransaction().commit() ;
			return retVal ;
		} catch (Exception e) {
			throw e ;
		}
	}
}
