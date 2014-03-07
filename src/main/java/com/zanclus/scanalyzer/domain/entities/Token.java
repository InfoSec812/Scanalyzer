package com.zanclus.scanalyzer.domain.entities;

import java.io.Serializable;
import java.nio.file.attribute.UserPrincipal;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
@Entity
@Table(name="tokens")
@Data
public class Token implements Serializable, AccessControlledEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6758602935988065968L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id ;

	private String token = UUID.randomUUID().toString() ;

	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private User user ;

	public String getUser() {
		return "/rest/user/"+user.getId() ;
	}

	@Override
	public boolean canRead(UserPrincipal principal) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canWrite(UserPrincipal principal) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canModify(UserPrincipal principal) {
		// TODO Auto-generated method stub
		return false;
	}
}
