/**
 * 
 */
package com.zanclus.scanalyzer.domain.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.zanclus.scanalyzer.listeners.WebContext;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
@Entity
@Table(name="groups")
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@XmlRootElement(name="group")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Group extends IndexedEntity implements Serializable, RightsHolder {

	private static final long serialVersionUID = 7313120316934000672L;

	private String name ;
	private boolean enabled ;

	@ManyToMany
	private List<User> members = new ArrayList<>() ;

	@ManyToMany
	private List<IndexedEntity> rights ;

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

	@XmlTransient
	@Override
	public boolean isAuthorized(IndexedEntity entity) {
		boolean retVal = false ;
		String entityType = entity.getClass().getName() ;
		EntityManager em = WebContext.getEntityManager() ;
		em.getTransaction().begin() ;
		int returnedRows = em.createQuery("FROM Group g LEFT JOIN g.rights r LEFT JOIN r.entities e WHERE r.type=:entityType AND e.id=:entityId")
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