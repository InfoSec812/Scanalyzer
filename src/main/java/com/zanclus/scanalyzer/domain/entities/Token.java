package com.zanclus.scanalyzer.domain.entities;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
@Entity
@Table(name="tokens")
@Data
@EqualsAndHashCode(callSuper=true)
public class Token extends IndexedEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6758602935988065968L;

	private String token = UUID.randomUUID().toString() ;
}
