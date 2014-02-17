package com.zanclus.scanalyzer.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * AuditEntry
 */
@Entity
@Table(name="auditlog")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@Expose(serialize = false)
	private User user ;
	private String type ;
	private String operation ;

	@Column(length=1000000)
	private String content ;
}
