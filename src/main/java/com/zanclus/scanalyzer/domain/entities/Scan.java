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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder={"id", "hostId", "scanTime", "scanResults"})
public class Scan {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id ;

	@Column(name="scan_time", nullable=false, updatable=false, insertable=true)
	private Date scanTime = new Date() ;

	@Column(name="scan_results", length=1000000, nullable=false, updatable=false, insertable=true)
	private String scanResults = null ;

	@ManyToOne(cascade = CascadeType.MERGE)
	@XmlTransient
	private Host target ;

	@XmlAttribute(name="id")
	public Long getId() {
		return id ;
	}

	@XmlAttribute(name="scanTime")
	@XmlJavaTypeAdapter(DateAdapter.class)
	@JsonSerialize(using=JacksonDateSerializer.class)
	public Date getScanTime() {
		return scanTime ;
	}

	@XmlValue
	public String getScanResults() {
		return scanResults ;
	}

	@JsonProperty(value="hostId")
	@XmlAttribute(name="hostId")
	public Long getHostId() {
		return target.getId() ;
	}

	@XmlTransient
	@JsonIgnore
	public Host getTarget() {
		return target ;
	}
}
