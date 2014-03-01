package com.zanclus.scanalyzer.domain.access;

import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import com.zanclus.scanalyzer.domain.entities.Host;
import com.zanclus.scanalyzer.domain.entities.Ports;
import com.zanclus.scanalyzer.domain.entities.User;
import com.zanclus.scanalyzer.listeners.WebContext;

/**
 * A Data Access Object for CCRUD operations on Ports entities
 * 
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public class PortsDAO extends GenericDAO<Ports, Long> {

	private User user ;

	public PortsDAO() {
		super() ;
	}

	public PortsDAO(User user) {
		super() ;
		this.user = user ;
	}

	@Override
	public Ports findById(Long id) {
		if (this.user==null) {
			throw new WebApplicationException(Status.UNAUTHORIZED) ;
		} else {
			em = WebContext.getEntityManager() ;
			em.getTransaction().begin() ;
			Ports retVal = em.createQuery("FROM Ports p WHERE p.id=:portsId AND p.target.owner.id=:userId", Ports.class)
					.setParameter("portsId", id)
					.setParameter("userId", user.getId())
					.getSingleResult() ;
			em.getTransaction().commit() ;
			em.close() ;
			
			if (retVal==null) {
				throw new WebApplicationException(Status.NOT_FOUND) ;
			}
			return retVal ;
		}
	}

	/**
	 * Get the paginated ports history associated with a given hostId
	 * @param hostId The id which identifies the {@link Host}
	 * @param limit 
	 * @param offset
	 * @return
	 */
	public List<Ports> getPagedPortsHistoryByHostId(Long hostId, int limit, int offset) {
		if (this.user==null) {
			throw new WebApplicationException(Status.UNAUTHORIZED) ;
		} else {
			em = WebContext.getEntityManager() ;
			em.getTransaction().begin() ;
			List<Ports> retVal = em.createQuery("FROM Ports p WHERE p.host.id=:hostId AND p.host.owner.id=:userId ORDER BY p.scanTime DESC", Ports.class)
									.setParameter("hostId", hostId)
									.setParameter("userId", user.getId())
									.setFirstResult(offset)
									.setMaxResults(limit)
									.getResultList() ;
			em.getTransaction().commit() ;
			em.close();
			
			return retVal ;
		}
	}

	public List<Ports> getLastTwoScans(Long hostId) {
		em = WebContext.getEntityManager() ;
		em.getTransaction().begin() ;
		List<Ports> retVal = em.createQuery("FROM Ports p WHERE p.host.id=:hostId ORDER BY p.scanTime DESC", Ports.class)
								.setParameter("hostId", hostId)
								.setFirstResult(0)
								.setMaxResults(1)
								.getResultList() ;
		em.getTransaction().commit() ;
		em.close();
		
		return retVal ;
	}
}
