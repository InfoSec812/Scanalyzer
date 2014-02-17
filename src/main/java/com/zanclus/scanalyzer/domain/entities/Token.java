package com.zanclus.scanalyzer.domain.entities;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
@Entity
@Table(name="tokens")
@Data
public class Token {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id ;

	private String token = UUID.randomUUID().toString() ;
}
