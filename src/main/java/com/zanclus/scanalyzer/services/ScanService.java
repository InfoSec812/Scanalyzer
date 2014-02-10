/**
 * 
 */
package com.zanclus.scanalyzer.services;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.zanclus.scanalyzer.domain.access.HostDAO;
import com.zanclus.scanalyzer.domain.access.ScanDAO;
import com.zanclus.scanalyzer.domain.entities.Scan;
import com.zanclus.scanalyzer.domain.entities.ScanCollectionWrapper;

/**
 * @author <a href="mailto: deven.phillips@gmail.com">Deven Phillips</a>
 *
 */
@Path("/scan")
public class ScanService {

	@Path("/id/{id}")
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Scan getScanById(@PathParam("id") Long id) {
		ScanDAO sDao = new ScanDAO() ;
		return sDao.findById(id) ;
	}

	@GET
	@Path("/host/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ScanCollectionWrapper getScansByHostId(@PathParam("id") Long id) {
		List<Scan> scans = (new HostDAO()).findById(id).getScans() ;
		
		return new ScanCollectionWrapper(scans) ;
	}
}
