package com.zanclus.scanalyzer.domain.entities;

import java.io.Serializable;
import java.lang.Long;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.txw2.annotation.XmlElement;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import com.zanclus.scanalyzer.listeners.WebContext;

import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@Builder
@XmlRootElement(name="user")
@ApiModel(value="A User of this service.")
public class User extends IndexedEntity implements Serializable, RightsHolder {

	private static final long serialVersionUID = 1L;

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

	@ApiModelProperty("Is this user's account allowed admin privileges?")
	private Boolean admin = Boolean.FALSE ;
	
	@OneToMany(cascade = ALL, orphanRemoval = true)
	@JoinColumn(name="hostId", referencedColumnName="id")
	private List<Token> tokens = new ArrayList<>() ;

	@ManyToMany(mappedBy="members")
	private List<Group> groups = new ArrayList<>() ;

	@ManyToMany
	private List<IndexedEntity> rights = new ArrayList<>() ;

	@Transient
	private Logger log = LoggerFactory.getLogger(User.class) ;

	public User(String givenName, String familyName, String login,
			String password, String email, Boolean enabled, Boolean active, Boolean admin, 
			List<Token> tokens, List<Group> groups, List<IndexedEntity> rights, Logger log) {
		super();
		this.givenName = givenName ;
		this.familyName = familyName ;
		this.login = login ;
		this.setPassword(this.password) ;
		this.email = email ;
		this.enabled = enabled ;
		this.active = active ;
	}

	public void setPassword(String password) {
		this.password = BCrypt.hashpw(password, BCrypt.gensalt(4)) ;
	}

	public boolean validatePassword(String password) {
		if (this.password==null) {
			log.warn("Persisted password value is null.") ;
			return false ;
		} else {
			return BCrypt.checkpw(password, this.password) ;
		}
	}

	@XmlTransient
	public Logger getLog() {
		return null ;
	}

	@XmlAttribute(name="id")
	public Long getId() {
		return id ;
	}

	@XmlElement("groupsReference")
	public String getGroupReference() {
		return "/rest/user/"+id+"/groups" ;
	}

	public String getPassword() {
		return null ;
	}

	@XmlTransient
	public List<Group> getGroups() {
		return groups ;
	}

	@XmlTransient
	public List<Token> getTokens() {
		return tokens ;
	}

	@XmlTransient
	@Override
	public boolean isAuthorized(IndexedEntity entity) {
		boolean retVal = false ;
		String entityType = entity.getClass().getName() ;
		EntityManager em = WebContext.getEntityManager() ;
		em.getTransaction().begin() ;
		int returnedRows = em.createQuery("FROM User u LEFT JOIN u.rights r LEFT JOIN r.entities e WHERE r.type=:entityType AND e.id=:entityId")
			.setParameter("entityType", entityType)
			.setParameter("entityId", entity.getId())
			.getResultList().size() ;
		em.getTransaction().commit() ;
		if (returnedRows>0) {
			retVal = true ;
		}
		return retVal ;
	}
}