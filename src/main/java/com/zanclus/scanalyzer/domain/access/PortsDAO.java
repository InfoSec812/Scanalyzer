/**
 * 
 */
package com.zanclus.scanalyzer.domain.access;

import java.util.List;

import com.zanclus.scanalyzer.domain.entities.Host;
import com.zanclus.scanalyzer.domain.entities.Ports;

/**
 * A Data Access Object for CCRUD operations on Ports entities
 * 
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public class PortsDAO extends GenericDAO<Ports, Long> {

	/**
	 * Get the paginated ports history associated with a given hostId
	 * @param hostId The id which identifies the {@link Host}
	 * @param limit 
	 * @param offset
	 * @return
	 */
	public List<Ports> getPagedPortsHistoryByHostId(Long hostId, int limit, int offset) {
		em.getTransaction().begin() ;
		List<Ports> retVal = em.createQuery("FROM Ports p WHERE p.host.id=:hostId ORDER BY p.scanTime DESC", Ports.class)
								.setParameter("hostId", hostId)
								.setFirstResult(offset)
								.setMaxResults(limit)
								.getResultList() ;
		em.getTransaction().commit() ;
		
		return retVal ;
	}
}
