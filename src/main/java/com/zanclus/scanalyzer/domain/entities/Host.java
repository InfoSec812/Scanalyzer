package com.zanclus.scanalyzer.domain.entities;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zanclus.scanalyzer.ApplicationState;
import com.zanclus.scanalyzer.ScanRunner;

import lombok.Data;

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

	@OneToMany(mappedBy="target")
	private List<Scan> scans = new ArrayList<>() ;

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
	@JsonValue
	public String getAdded() {
		return added==null?null:SimpleDateFormat.getDateTimeInstance().format(added) ;
	}

	@XmlElement
	@JsonValue
	public String getLastScanned() {
		return lastScanned==null?null:SimpleDateFormat.getDateTimeInstance().format(lastScanned) ;
	}

	@XmlElementWrapper(name="scans")
	@XmlElement(name="scan")
	public List<Scan> getScans() {
		return this.scans ;
	}

	/**
	 * Whenever a new host is added to the database, immediately queue that host for scanning.
	 */
	@PostPersist
	public void queueForScan() {
		ApplicationState.getInstance().addScanToQueue(new ScanRunner(this)) ;
	}
}
