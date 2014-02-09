/**
 * 
 */
package com.zanclus.scanalyzer.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;
import com.zanclus.scanalyzer.domain.access.HostDAO;
import com.zanclus.scanalyzer.domain.entities.Host;
import com.zanclus.scanalyzer.domain.entities.Scan;

/**
 * The JAX-RS enpoints related to Hosts
 * 
 * @author <a href="mailto: deven.phillips@gmail.com">Deven Phillips</a>
 * 
 */
@Path("/host")
public class HostService {

	@Context
	private Request request;

	@Context
	UriInfo url;

	private Logger log = null;

	public HostService() {
		super();
		log = LoggerFactory.getLogger(this.getClass());
	}

	@GET
	@Path("/id/{id : ([^/]*)}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Host getHostById(@PathParam("id") Long hostId) {
		log.info("Processing GET request for host ID '" + hostId + "'");
		HostDAO dao = new HostDAO();
		return dao.getHostById(hostId);
	}

	@GET
	@Path("/id/{id : ([^/]*)}/scans")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<Scan> getHostScans(@PathParam("id") Long id) {
		HostDAO dao = new HostDAO() ;
		return dao.getHostScans(id) ;
	}

	@GET
	@Path("/address/{address : (.*)$}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Host getHostByAddress(@PathParam("address") String address) {
		log.info("Processing GET request for address '" + address + "'");
		byte[] inetAddress = null;
		try {
			inetAddress = InetAddress.getByName(address).getAddress();
		} catch (UnknownHostException e) {
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}

		HostDAO dao = new HostDAO();
		return dao.getHostByAddress(inetAddress);
	}

	@POST
	@Path("/address/{address : (.*)$}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Host addHostByAddress(@PathParam("address") String address) {
		log.info("Request URL: "+url.getPath());
		log.info("Processing POST request for address '" + address + "'");
		if (address == null) {
			log.error("Address is NULL");
		}
		byte[] inetAddress = null;
		try {
			inetAddress = InetAddress.getByName(address).getAddress();
		} catch (UnknownHostException e) {
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}

		HostDAO dao = new HostDAO();
		Host retVal = null;
		try {
			retVal = dao.addHost(inetAddress);
		} catch (Exception e) {
			throw new WebApplicationException(e, Status.CONFLICT);
		}
		return retVal;
	}

	@DELETE
	@Path("/id/{id : (.*)$}")
	public Response deleteHost(@PathParam("id") Long id) throws WebApplicationException {
		try {
			HostDAO dao = new HostDAO() ;
			dao.deleteHost(id) ;
		} catch (Throwable t) {
			throw new WebApplicationException(t, Status.NOT_FOUND) ;
		}
		
		Response retVal = new ResponseBuilderImpl().status(202).build() ;
		return retVal ;
	}

	@PUT
	@Path("/id/{id : (.*)$}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response updateHost(Host updates) {
		log.info("Request URL: "+url.getPath());
		log.info("Processing PUT request for host ID '" + updates.getId() + "'");
		HostDAO dao = new HostDAO() ;
		Host updatedHost = dao.updateHost(updates) ;
		
		Response retVal = new ResponseBuilderImpl().entity(updatedHost).status(204).build() ;
		return retVal ;
	}
}