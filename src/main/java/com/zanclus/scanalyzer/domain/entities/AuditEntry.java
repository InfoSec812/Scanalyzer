package com.zanclus.scanalyzer.domain.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
	private User user ;
	private String type ;
	private String operation ;
	private String content ;
}
