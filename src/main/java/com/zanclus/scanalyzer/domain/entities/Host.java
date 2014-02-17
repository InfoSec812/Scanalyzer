package com.zanclus.scanalyzer.domain.entities;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PostPersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import com.zanclus.scanalyzer.ScanRunner;
import com.zanclus.scanalyzer.listeners.WebContext;
import com.zanclus.scanalyzer.serialization.DateAdapter;
import com.zanclus.scanalyzer.serialization.JacksonDateSerializer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

// Lombok saves me from boilerplate hell!
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="HOSTS")
@XmlRootElement(name="host")
@JsonRootName("host")
@ApiModel(value="A host represents a single address which is accessible either on the local network or the Internet")
public class Host implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4830208317277697554L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id ;

	@Column(unique=true)
	@ApiModelProperty(value="The Internet address for this host", required=true)
	private byte[] address = new byte[128] ;

	@Column(nullable=false, updatable=false)
	@Temporal(TemporalType.TIMESTAMP)
	@ApiModelProperty(value="The date that this host was added", required=true)
	private Date added = new Date() ;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	@ApiModelProperty(value="The date/time at which this host was last scanned", required=false)
	private Date lastScanned = null ;

	@Column(nullable=false)
	@ApiModelProperty(value="Is periodic scanning enabled for this host?", required=true)
	private Boolean active = Boolean.TRUE ;

	@ManyToOne
	private User owner ;

	@Column(nullable=true)
	@ApiModelProperty(value="The operating system as detected by NMAP.", required=false)
	private String operatingSystem ;

	@OneToMany(mappedBy="target", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@ApiModelProperty(value="The scan history associated with this host", required=false)
	private List<Scan> scans = new ArrayList<>() ;

	@OneToMany(mappedBy="host", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@OrderBy("scanTime ASC")
	@ApiModelProperty(value="The ports history associated with this host", required=false)
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


	@XmlTransient
	public List<Scan> getScans() {
		return scans ;
	}

	@XmlTransient
	public List<Ports> getPortHistory() {
		return portHistory ;
	}

	@XmlElement(name="scans")
	public String getScanReference() {
		return "/rest/hosts/id/"+this.id+"/scans" ;
	}

	@XmlElement(name="portHistory")
	public String getPortsReference() {
		return "/rest/hosts/id/"+this.id+"/portHistory" ;
	}

	public void setAddress(String address) throws UnknownHostException {
		this.address = InetAddress.getByName(address).getAddress();
	}

	/**
	 * Whenever a new host is added to the database, immediately queue that host for scanning.
	 */
	@PostPersist
	public void queueForScan() {
		if (this.active) {
			WebContext.addScanToQueue(new ScanRunner(this)) ;
		}
	}
}
