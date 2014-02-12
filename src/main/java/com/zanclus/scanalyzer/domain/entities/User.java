package com.zanclus.scanalyzer.domain.entities;

import java.io.Serializable;
import java.lang.Long;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.mindrot.jbcrypt.BCrypt;

import com.sun.xml.txw2.annotation.XmlElement;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;
import static javax.persistence.CascadeType.ALL;

/**
 * Entity implementation class for Entity: User
 *
 */
@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@Builder
@XmlRootElement(name="user")
@ApiModel(value="A User of this service.")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@ApiModelProperty("The unique ID of this user's account record.")
	private Long id;

	private String givenName ;
	private String familyName ;

	@Column(nullable=false, unique=true)
	@ApiModelProperty("The username that this user authenticates with")
	private String login ;

	@ApiModelProperty("The user's password, stored as a BCrypt hash with salt")
	private String password ;

	@Column(unique=true)
	@ApiModelProperty("The user's e-mail address")
	private String email ;

	@ApiModelProperty("Is this user's account enabled (not locked out for security reasons)?")
	private Boolean enabled = Boolean.TRUE ;

	@ApiModelProperty("Is this user's account active (not disabled for administrative purposes)?")
	private Boolean active = Boolean.TRUE ;
	
	@OneToMany(cascade = ALL, orphanRemoval = true)
	@JoinColumn(name="hostId", referencedColumnName="id")
	private List<Token> tokens = new ArrayList<>() ;

	@ManyToMany(mappedBy="members")
	private List<Group> groups = new ArrayList<>() ;

	public User(Long id, String givenName, String familyName, String login,
			String password, String email, Boolean enabled, Boolean active, 
			List<Token> tokens, List<Group> groups) {
		super();
		this.id = id ;
		this.givenName = givenName ;
		this.familyName = familyName ;
		this.login = login ;
		this.setPassword(password) ;
		this.email = email ;
		this.enabled = enabled ;
		this.active = active ;
	}

	public void setPassword(String password) {
		this.password = BCrypt.hashpw(password, BCrypt.gensalt(4)) ;
	}

	public boolean validatePassword(String password) {
		return BCrypt.checkpw(password, this.password) ;
	}

	@XmlAttribute(name="id")
	public Long getId() {
		return id ;
	}

	@XmlElement("groupsReference")
	public String getGroupReference() {
		return "/rest/user/"+id+"/groups" ;
	}

	@XmlTransient
	public String getPassword() {
		return null ;
	}

	@XmlTransient
	public List<Group> getGroups() {
		return groups ;
	}

	@XmlTransient
	public List<Token> getTokens() {
		return null ;
	}
}