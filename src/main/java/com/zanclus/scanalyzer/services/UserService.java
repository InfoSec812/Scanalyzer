/**
 * 
 */
package com.zanclus.scanalyzer.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.zanclus.scanalyzer.domain.access.UserDAO;
import com.zanclus.scanalyzer.domain.entities.Token;
import com.zanclus.scanalyzer.domain.entities.User;

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
	public User createUser(User newUser) {
		UserDAO dao = new UserDAO() ;
		return dao.create(newUser) ;
	}

	@PUT
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@ApiOperation("Update a User entity")
	public User updateUser(User newUser) {
		UserDAO dao = new UserDAO() ;
		return dao.update(newUser) ;
	}

	@GET
	@Path("/{id : [0-9]*}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@ApiOperation("Get a user record based on provided ID")
	public User getUserById(
			@ApiParam(name="id") @PathParam("id") Long id) {
		UserDAO dao = new UserDAO() ;
		return dao.findById(id) ;
	}

	@POST
	@Path("/{id : [0-9]*}/token")
	@Consumes({"*/*"})
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	@ApiOperation("Produces a new token and associates it with the indicated user account")
	public Token getNewUserToken(
			@ApiParam(value="The ID of the user", name="id") @PathParam("id") Long id,
			@ApiParam(value="The login ID for the user", name="login", required=false) @HeaderParam("login") String login,
			@ApiParam(value="The password for the user", name="password", required=false) @HeaderParam("password") String password) {
		UserDAO dao = new UserDAO() ;
		log.debug("Preparing to create new Token and associate it with user: "+login+":"+password+":"+id) ;
		return dao.getNewTokenForUser(id, login, password) ;
	}
}
