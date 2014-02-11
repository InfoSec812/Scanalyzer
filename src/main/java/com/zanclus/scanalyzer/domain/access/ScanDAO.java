/**
 * 
 */
package com.zanclus.scanalyzer.domain.access;

import java.util.List;

import com.zanclus.scanalyzer.domain.entities.Host;
import com.zanclus.scanalyzer.domain.entities.Scan;

/**
 * A Data Access Object for CCRUD operations on Scan entities
 * 
 * @author <a href="mailto: ***REMOVED***">Deven Phillips</a>
 *
 */
public class ScanDAO extends GenericDAO<Scan, Long> {

	/**
	 * Get the paginated scans history associated with a given hostId
	 * @param hostId The id which identifies the {@link Host}
	 * @param limit 
	 * @param offset
	 * @return
	 */
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
