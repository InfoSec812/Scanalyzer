package com.zanclus.scanalyzer.services;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.zanclus.scanalyzer.domain.access.HostDAO;
import com.zanclus.scanalyzer.domain.access.PortsDAO;
import com.zanclus.scanalyzer.domain.access.ScanDAO;
import com.zanclus.scanalyzer.domain.entities.Host;
import com.zanclus.scanalyzer.domain.entities.PortsCollectionWrapper;
import com.zanclus.scanalyzer.domain.entities.ScanCollectionWrapper;

/**
 * The JAX-RS endpoints related to Hosts and their associated scans and port histories.
 * 
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 * 
 */
@Path("/host")
@Api(value="/host", description="Operations on hosts")
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
	@Path("/{id : ([0-9]*)}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value="Find a Host by ID")
	@ApiResponses(value = {
			@ApiResponse(code=404, message="Host not found"), 
			@ApiResponse(code=400, message="Invalid ID supplied")})
	public Host getHostById(
			@ApiParam(value="The ID of the host to fetch", required=true) @PathParam("id") Long hostId) {
		log.info("Processing GET request for host ID '" + hostId + "'");
		HostDAO dao = new HostDAO();
		return dao.findById(hostId);
	}

	@GET
	@Path("/{id : ([0-9]*)}/scans")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value="Get the scans for a given host", notes="This is a paginated endpoint which defaults to grabbing only the first 20 records for a given host.")
	@ApiResponses(value = {
			@ApiResponse(code=404, message="Host not found"), 
			@ApiResponse(code=400, message="Invalid ID supplied")})
	public ScanCollectionWrapper getHostScans(
			@ApiParam(value="The ID of the host to get the scans for", required=true) @PathParam("id") Long id, 
			@ApiParam(value="The maximum number of records to return", required=false) @QueryParam("limit") @DefaultValue("20") int limit, 
			@ApiParam(value="The offset at which to start returning the requested records", required=false) @QueryParam("offset") @DefaultValue("0") int offset) {
		ScanDAO dao = new ScanDAO() ;
		return new ScanCollectionWrapper(dao.getPagedScansByHostId(id, limit, offset)) ;
	}

	@GET
	@Path("/{id : ([0-9]*)}/portHistory")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value="Get the ports history for a given host", notes="This is a paginated endpoint which defaults to grabbing only the first 20 records for a given host.")
	@ApiResponses(value = {
			@ApiResponse(code=404, message="Host not found"), 
			@ApiResponse(code=400, message="Invalid ID supplied")})
	public PortsCollectionWrapper getHostPortHistory(
			@ApiParam(value="The ID of the host to get the scans for", required=true) @PathParam("id") Long id, 
			@ApiParam(value="The maximum number of records to return", required=false) @QueryParam("limit") @DefaultValue("20") int limit, 
			@ApiParam(value="The offset at which to start returning the requested records", required=false) @QueryParam("offset") @DefaultValue("0") int offset) {
		PortsDAO dao = new PortsDAO() ;
		return new PortsCollectionWrapper(dao.getPagedPortsHistoryByHostId(id, limit, offset)) ;
	}

	@GET
	@Path("/address/{address : (.*)$}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value="Find a Host by Internet address")
	@ApiResponses(value = {
			@ApiResponse(code=404, message="Host not found"), 
			@ApiResponse(code=400, message="Invalid address supplied")})
	public Host getHostByAddress(
			@ApiParam(value="The Internet address of the host to get", required=true) @PathParam("address") String address) {
		log.info("Processing GET request for address '" + address + "'") ;
		byte[] inetAddress = null;
		try {
			inetAddress = InetAddress.getByName(address).getAddress() ;
		} catch (UnknownHostException e) {
			throw new WebApplicationException(e, Status.BAD_REQUEST) ;
		}

		HostDAO dao = new HostDAO();
		return dao.getHostByAddress(inetAddress) ;
	}

	@POST
	@Path("/address/{address : (.*)$}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value="Add a new host to the database using it's Internet address")
	@ApiResponses(value = {
			@ApiResponse(code=409, message="Host with that address already exists in the database.")})
	public Host addHostByAddress(
			@ApiParam(value="The Internet address of the host to create", required=true) @PathParam("address") String address) {
		log.info("Request URL: "+url.getPath());
		log.info("Processing POST request for address '" + address + "'");
		if (address == null) {
			log.error("Address is NULL");
		}

		HostDAO dao = new HostDAO();
		Host retVal = null;
		try {
			retVal = dao.addHost(address) ;
		} catch (Exception e) {
			throw new WebApplicationException(e, Status.CONFLICT);
		}
		return retVal;
	}

	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value="Add a new host to the database using it's Internet address")
	@ApiResponses(value = {
			@ApiResponse(code=409, message="Host with that address already exists in the database.")})
	public Host addHost(Host host) {
		HostDAO dao = new HostDAO() ;
		return dao.create(host) ;
	}

	@DELETE
	@Path("/{id : ([0-9]*)$}")
	@ApiOperation(value="Delete the host identified by the given ID")
	@ApiResponses(value = {
			@ApiResponse(code=202, message="Host was successfully deleted."),
			@ApiResponse(code=404, message="Host with the given ID was not found")})
	public Response deleteHost(
			@ApiParam(value="The ID of the host to delete", required=true) @PathParam("id") Long id) throws WebApplicationException {
		try {
			HostDAO dao = new HostDAO() ;
			dao.delete(id) ;
		} catch (Throwable t) {
			throw new WebApplicationException(t, Status.NOT_FOUND) ;
		}
		
		Response retVal = new ResponseBuilderImpl().status(202).build() ;
		return retVal ;
	}

	@PUT
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@ApiOperation(value="Update an existing host's record.")
	@ApiResponses(value = {
			@ApiResponse(code=204, message="Host was successfully updated."),
			@ApiResponse(code=404, message="Host with the given ID was not found")})
	public Response updateHost(Host updates) {
		log.info("Request URL: "+url.getPath());
		log.info("Processing PUT request for host ID '" + updates.getId() + "'");
		HostDAO dao = new HostDAO() ;
		Host updatedHost = dao.update(updates) ;
		
		Response retVal = new ResponseBuilderImpl().entity(updatedHost).status(204).build() ;
		return retVal ;
	}
}
