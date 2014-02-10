package com.zanclus.scanalyzer.domain.entities;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PostPersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zanclus.scanalyzer.ScanRunner;
import com.zanclus.scanalyzer.listeners.WebContext;
import com.zanclus.scanalyzer.serialization.DateAdapter;
import com.zanclus.scanalyzer.serialization.JacksonDateSerializer;

import lombok.Data;

// Lombok saves me from boilerplate hell!
@Data
@Entity
@Table(name="HOSTS")
@XmlRootElement(name="host")
@JsonRootName("host")
public class Host {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id ;

	@Column(unique=true)
	private byte[] address = new byte[128] ;

	@Column(nullable=false, updatable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date added = new Date() ;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastScanned = null ;

	@Column(nullable=false)
	private Boolean active = Boolean.TRUE ;

	@OneToMany(mappedBy="target", fetch=FetchType.LAZY)
	private List<Scan> scans = new ArrayList<>() ;

	@OneToMany(mappedBy="host", fetch=FetchType.LAZY)
	@OrderBy("scanTime ASC")
	private List<Ports> portHistory = new ArrayList<>() ;

	@XmlAttribute(name="id")
	@JsonValue
	public Long getId() {
		return id ;
	}

	@XmlElement(name="address")
	@JsonValue
	public String getAddress() {
		InetAddress retVal = null ;
		try {
			retVal = InetAddress.getByAddress(address) ;
			return retVal.getHostAddress();
		} catch (UnknownHostException e) {
			return null;
		}
	}

	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
	@JsonValue
	@JsonSerialize(using=JacksonDateSerializer.class)
	public Date getAdded() {
		return added ;
	}

	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
	@JsonValue
	@JsonSerialize(using=JacksonDateSerializer.class)
	public Date getLastScanned() {
		return lastScanned ;
	}

	@XmlElementWrapper(name="scans")
	@XmlElement(name="scan")
	@JsonManagedReference("SCANS")
	public List<Scan> getScans() {
		return scans ;
	}

	@XmlElementWrapper(name="portHistory")
	@XmlElement(name="ports")
	@JsonManagedReference("PORTS")
	public List<Ports> getPortHistory() {
		return portHistory ;
	}

	/**
	 * Whenever a new host is added to the database, immediately queue that host for scanning.
	 */
	@PostPersist
	public void queueForScan() {
		WebContext.addScanToQueue(new ScanRunner(this)) ;
	}
}
