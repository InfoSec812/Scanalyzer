package com.zanclus.scanalyzer.domain.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public abstract class IndexedEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected Long id ;

	public IndexedEntity() {
		super() ;
	}

	public Long getId() {
		return this.id ;
	} ;

}
