package com.zanclus.scanalyzer.domain.entities;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zanclus.scanalyzer.serialization.DateAdapter;
import com.zanclus.scanalyzer.serialization.JacksonDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * @author <a href="mailto: ***REMOVED***">Deven Phillips</a>
 *
 */ 
@Entity
@Table(name="SCANS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name="scan")
@XmlAccessorType(XmlAccessType.FIELD)
public class Scan {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id ;

	@Column(name="scan_time", nullable=false, updatable=false, insertable=true)
	@XmlJavaTypeAdapter(DateAdapter.class)
	@JsonSerialize(using=JacksonDateSerializer.class)
	private Date scanTime = new Date() ;

	@Column(name="scan_results", length=1000000, nullable=false, updatable=false, insertable=true)
	private String scanResults = null ;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JsonBackReference("SCANS")
	@XmlTransient
	private Host target ;
}
