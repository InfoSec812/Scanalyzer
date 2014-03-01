package com.zanclus.scanalyzer.domain.access;

import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import com.zanclus.scanalyzer.domain.entities.Host;
import com.zanclus.scanalyzer.domain.entities.Scan;
import com.zanclus.scanalyzer.domain.entities.User;
import com.zanclus.scanalyzer.listeners.WebContext;

/**
 * A Data Access Object for CCRUD operations on Scan entities
 * 
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public class ScanDAO extends GenericDAO<Scan, Long> {

	private User user ;

	public ScanDAO() {
		super() ;
	}

	public ScanDAO(User user) {
		super() ;
		this.user = user ;
	}

	@Override
	public Scan findById(Long id) {
		if (this.user==null) {
			throw new WebApplicationException(Status.UNAUTHORIZED) ;
		} else {
			em = WebContext.getEntityManager() ;
			em.getTransaction().begin() ;
			Scan retVal = em.createQuery("FROM Scan s WHERE s.id=:scanId AND s.target.owner.id=:userId", Scan.class)
					.setParameter("scanId", id)
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
	 * Get the paginated scans history associated with a given hostId
	 * @param hostId The id which identifies the {@link Host}
	 * @param limit 
	 * @param offset
	 * @return
	 */
	public List<Scan> getPagedScansByHostId(Long hostId, int limit, int offset) {
		if (this.user==null) {
			throw new WebApplicationException(Status.UNAUTHORIZED) ;
		} else {
			em = WebContext.getEntityManager() ;
			em.getTransaction().begin() ;
			List<Scan> retVal = em.createQuery("FROM Scan s WHERE s.target.id=:hostId AND s.target.owner.id=:userId", Scan.class)
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

	public List<Scan> getLastTwoScans(Long hostId) {
		em = WebContext.getEntityManager() ;
		em.getTransaction().begin() ;
		List<Scan> retVal = em.createQuery("FROM Scan s WHERE s.target.id=:hostId AND s.target.owner=:userId", Scan.class)
								.setParameter("hostId", hostId)
								.setParameter("userId", user.getId())
								.setFirstResult(0)
								.setMaxResults(1)
								.getResultList() ;
		em.getTransaction().commit() ;
		em.close();
		
		return retVal ;
	}
}
