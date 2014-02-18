package com.zanclus.scanalyzer.domain.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonValue;
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

	/**
	 * 
	 */
	private static final long serialVersionUID = 7044515514440326568L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id ;

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

	@OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "user", fetch=FetchType.EAGER)
	private List<Token> tokens = new ArrayList<>() ;

	@OneToMany(cascade=ALL, orphanRemoval=true)
	private List<Host> hosts = new ArrayList<>() ;

	@Transient
	private static final Logger LOG = LoggerFactory.getLogger(User.class) ;

	public User(Long id, String givenName, String familyName, String login,
			String pass, String email, Boolean enabled, Boolean active, Boolean admin, 
			List<Token> tokens, List<Host> hosts) {
		super();
		this.id = id ;
		this.givenName = givenName ;
		this.familyName = familyName ;
		this.login = login ;
		this.password = BCrypt.hashpw(pass, BCrypt.gensalt(4)) ;
		this.email = email ;
		this.enabled = enabled ;
		this.active = active ;
		this.admin = admin ;
	}

	public void setPassword(String password) {
		this.password = BCrypt.hashpw(password, BCrypt.gensalt(4)) ;
	}

	public boolean validatePassword(String plaintext) {
		if (this.password==null) {
			LOG.warn("Persisted password value is null.") ;
			return false ;
		} else {
			return BCrypt.checkpw(plaintext, this.password) ;
		}
	}

	public String getHosts() {
		return "/rest/user/"+this.id+"/hosts" ;
	}

	@XmlTransient
	public Logger getLog() {
		return null ;
	}

	@XmlAttribute(name="id")
	public Long getId() {
		return id ;
	}

	@XmlElement(nillable=true)
	@JsonValue
	public String getPassword() {
		return null ;
	}

	@XmlTransient
	public List<Token> getTokens() {
		return tokens ;
	}
}