/**
 * 
 */
package com.zanclus.scanalyzer.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.zanclus.scanalyzer.domain.access.UserDAO;
import com.zanclus.scanalyzer.domain.entities.User;

/**
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
@Path("/user")
@ApiModel("ReSTful enpoints related to user management")
public class UserService {

	@POST
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@ApiOperation("Create a new User entity")
	public User createUser(User newUser) {
		UserDAO dao = new UserDAO() ;
		return dao.create(newUser) ;
	}

	@GET
	@Path("/{id : [0-9]*")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public User getUserById(
			@ApiParam(name="id") @PathParam("id") Long id) {
		UserDAO dao = new UserDAO() ;
		return dao.findById(id) ;
	}

	@POST
	@Path("/{id : [0-9]*/token")
	@Produces({MediaType.TEXT_PLAIN})
	@ApiOperation("Produces a new token and associates it with the indicated user account")
	public String getNewUserToken(
			@ApiParam(value="The ID of the user", name="id") @PathParam("id") Long id,
			@ApiParam(value="The login ID for the user", name="login", required=true) @QueryParam("login") String login,
			@ApiParam(value="The password for the user", name="password", required=true) @QueryParam("password") String password) {
		UserDAO dao = new UserDAO() ;
		return dao.getNewTokenForUser(id, login, password).getToken() ;
	}
}
