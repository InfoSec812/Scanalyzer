/**
 * 
 */
package com.zanclus.scanalyzer.domain.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
@Entity
@Table(name="groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@XmlRootElement(name="group")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Group implements Serializable {

	private static final long serialVersionUID = 7313120316934000672L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id ;

	private String name ;
	private boolean enabled ;

	@ManyToMany
	private List<User> members = new ArrayList<>() ;

	@XmlAttribute(name="id")
	public Long getId() {
		return id ;
	}

	@XmlTransient
	public List<User> getMembers() {
		return members ;
	}

	@XmlElement(name="memberReference")
	public String getMemberReference() {
		return "/rest/group/"+id+"/members" ;
	}
}