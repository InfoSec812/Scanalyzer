/**
 * 
 */
package com.zanclus.scanalyzer.services;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.zanclus.scanalyzer.Auditor;
import com.zanclus.scanalyzer.domain.access.UserDAO;
import com.zanclus.scanalyzer.domain.entities.Token;
import com.zanclus.scanalyzer.domain.entities.User;
import com.zanclus.scanalyzer.security.ScanalyzerUserPrincipal;

/**
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
@Path("/user")
@Api(value="/user", description="ReSTful enpoints related to user management")
public class UserService {

	private Logger log ;

	public UserService() {
		super() ;
		log = LoggerFactory.getLogger(UserService.class) ;
	}

	@POST
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@ApiOperation("Create a new User entity")
	@ApiResponses({
		@ApiResponse(code=202, message="Successfully created the requested user account"),
		@ApiResponse(code=401, message="Not authorized"),
		@ApiResponse(code=404, message="Not found")
	})
	public User createUser(User newUser,
			@Context HttpServletRequest request) {
		ScanalyzerUserPrincipal up = (ScanalyzerUserPrincipal)request.getUserPrincipal() ;
		if ((up==null) || (up.getUser()==null)) {
			log.warn("Security context did not have a valid user attached.") ;
			throw new WebApplicationException(Status.UNAUTHORIZED) ;
		} else {
			if (up.getUser().getAdmin()) {
				UserDAO dao = new UserDAO(up.getUser());
				User retVal = dao.create(newUser);
				Auditor.writeAuditEntry(up.getUser(), "create", User.class, retVal) ;
				return retVal ;
			} else {
				throw new WebApplicationException(Status.UNAUTHORIZED) ;
			}
		}
	}

	@DELETE
	@Path("/{id : ([0-9]*)}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@ApiOperation("Create a new User entity")
	@ApiResponses({
		@ApiResponse(code=204, message="Successfully deleted the specified user account"),
		@ApiResponse(code=401, message="Not authorized"),
		@ApiResponse(code=404, message="Not found")
	})
	public Response deleteUser(
			@ApiParam(value="The ID of the user account to delete", name="id") @PathParam("id") Long id,
			@Context HttpServletRequest request) {
		ScanalyzerUserPrincipal up = (ScanalyzerUserPrincipal)request.getUserPrincipal() ;
		if ((up==null) || (up.getUser()==null)) {
			log.warn("Security context did not have a valid user attached.") ;
			throw new WebApplicationException(Status.UNAUTHORIZED) ;
		} else {
			if (up.getUser().getAdmin()) {
				UserDAO dao = new UserDAO(up.getUser());
				User target = dao.findById(id) ;
				Auditor.writeAuditEntry(up.getUser(), "delete", User.class, target) ;
				dao.delete(id) ;
				
				Response retVal = new ResponseBuilderImpl().status(202).build() ;
				return retVal ;
			} else {
				throw new WebApplicationException(Status.UNAUTHORIZED) ;
			}
		}
	}

	@PUT
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@ApiOperation("Update a User entity")
	@ApiResponses({
		@ApiResponse(code=202, message="Successfully updated the specified user account"),
		@ApiResponse(code=401, message="Not authorized"),
		@ApiResponse(code=404, message="Not found")
	})
	public User updateUser(User updateUser,
			@Context HttpServletRequest request) {
		ScanalyzerUserPrincipal up = (ScanalyzerUserPrincipal)request.getUserPrincipal() ;
		if ((up==null) || (up.getUser()==null)) {
			log.warn("Security context did not have a valid user attached.") ;
			throw new WebApplicationException(Status.UNAUTHORIZED) ;
		} else {
			UserDAO dao = new UserDAO(up.getUser()) ;
			User retVal = dao.update(updateUser) ;
			Auditor.writeAuditEntry(up.getUser(), "update", User.class, retVal) ;
			return retVal ;
		}
	}

	@GET
	@Path("/{id : [0-9]*}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@ApiOperation("Get a user record based on provided ID")
	@ApiResponses({
		@ApiResponse(code=200, message="Successfully retrieved the requested user account"),
		@ApiResponse(code=401, message="Not authorized"),
		@ApiResponse(code=404, message="Not found")
	})
	public User getUserById(
			@ApiParam(name="id") @PathParam("id") Long id,
			@Context HttpServletRequest request) {
		ScanalyzerUserPrincipal up = (ScanalyzerUserPrincipal)request.getUserPrincipal() ;
		UserDAO dao = new UserDAO(up.getUser()) ;
		return dao.findById(id) ;
	}

	@POST
	@Path("/{id : [0-9]*}/token")
	@Consumes({"*/*"})
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	@ApiOperation("Produces a new token and associates it with the indicated user account")
	@ApiResponses({
		@ApiResponse(code=200, message="Successfully created and retrieved a new token for the given user account"),
		@ApiResponse(code=401, message="Not authorized"),
		@ApiResponse(code=404, message="Not found")
	})
	public Token getNewUserToken(
			@ApiParam(value="The ID of the user", name="id") @PathParam("id") Long id,
			@ApiParam(value="The login ID for the user", name="login", required=false) @HeaderParam("login") String login,
			@ApiParam(value="The password for the user", name="password", required=false) @HeaderParam("password") String password,
			@Context HttpServletRequest request) {
		ScanalyzerUserPrincipal up = (ScanalyzerUserPrincipal)request.getUserPrincipal() ;
		UserDAO dao = new UserDAO(up.getUser()) ;
		log.debug("Preparing to create new Token and associate it with user: "+login+":"+password+":"+id) ;
		Token retVal = dao.getNewTokenForUser(id, login, password) ;

		Auditor.writeAuditEntry(up.getUser(), "create", Token.class, retVal) ;
		return retVal ;
	}
}
