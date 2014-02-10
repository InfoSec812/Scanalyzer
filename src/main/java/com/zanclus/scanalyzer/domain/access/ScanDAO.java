/**
 * 
 */
package com.zanclus.scanalyzer.domain.access;

import java.util.List;

import javax.persistence.EntityManager;
import com.zanclus.scanalyzer.domain.entities.Scan;
import com.zanclus.scanalyzer.listeners.WebContext;

/**
 * @author <a href="mailto: deven.phillips@gmail.com">Deven Phillips</a>
 *
 */
public class ScanDAO extends GenericDAO<Scan, Long> {

	public List<Scan> getScansByHostId(Long id) {
		try {
			EntityManager em = WebContext.getEntityManager() ;
			em.getTransaction().begin() ;
			List<Scan> retVal = em.createQuery("FROM Scan s WHERE s.target.id=:id", Scan.class).setParameter("id", id).getResultList() ;
			em.getTransaction().commit() ;
			return retVal ;
		} catch (Exception e) {
			throw e ;
		}
	}
}
