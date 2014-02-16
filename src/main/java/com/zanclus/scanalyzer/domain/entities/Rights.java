/**
 * 
 */
package com.zanclus.scanalyzer.domain.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * 
 * Store a set of rights assigned from a User/Group to other entities.
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
@Entity
@Table(name="rights")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rights {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id ;
}
