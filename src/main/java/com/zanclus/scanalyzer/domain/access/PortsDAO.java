/**
 * 
 */
package com.zanclus.scanalyzer.domain.access;

import java.util.List;

import com.zanclus.scanalyzer.domain.entities.Ports;

/**
 * @author <a href="mailto: deven.phillips@gmail.com">Deven Phillips</a>
 *
 */
public class PortsDAO extends GenericDAO<Ports, Long> {

	public List<Ports> getPagedPortsHistoryByHostId(Long hostId, int limit, int offset) {
		em.getTransaction().begin() ;
		List<Ports> retVal = em.createQuery("FROM Ports p WHERE p.host.id=:hostId", Ports.class)
								.setParameter("hostId", hostId)
								.setFirstResult(offset)
								.setMaxResults(limit)
								.getResultList() ;
		em.getTransaction().commit() ;
		
		return retVal ;
	}
}
