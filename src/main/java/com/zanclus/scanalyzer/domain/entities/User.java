package com.zanclus.scanalyzer.domain.entities;

import java.io.Serializable;
import java.lang.Long;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mindrot.jbcrypt.BCrypt;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

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
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@XmlAttribute(name="id")
	private Long id;

	private String givenName ;
	private String familyName ;
	private String login ;
	private String password ;
	private Boolean enabled = Boolean.TRUE ;
	private Boolean active = Boolean.TRUE ;

	public User(Long id, String givenName, String familyName, String login,
			String password, Boolean enabled, Boolean active) {
		super();
		this.id = id ;
		this.givenName = givenName ;
		this.familyName = familyName ;
		this.login = login ;
		this.setPassword(password) ;
		this.enabled = enabled ;
		this.active = active ;
	}

	public void setPassword(String password) {
		this.password = BCrypt.hashpw(password, BCrypt.gensalt(4)) ;
	}

	public boolean validatePassword(String password) {
		return BCrypt.checkpw(password, this.password) ;
	}
}