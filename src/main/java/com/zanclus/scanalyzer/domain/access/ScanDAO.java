/**
 * 
 */
package com.zanclus.scanalyzer.domain.access;

import java.util.List;
import com.zanclus.scanalyzer.domain.entities.Scan;

/**
 * @author <a href="mailto: ***REMOVED***">Deven Phillips</a>
 *
 */
public class ScanDAO extends GenericDAO<Scan, Long> {

	public List<Scan> getPagedScansByHostId(Long hostId, int limit, int offset) {
		em.getTransaction().begin() ;
		List<Scan> retVal = em.createQuery("FROM Scan s WHERE s.target.id=:hostId", Scan.class)
								.setParameter("hostId", hostId)
								.setFirstResult(offset)
								.setMaxResults(limit)
								.getResultList() ;
		em.getTransaction().commit() ;
		
		return retVal ;
	}
}
