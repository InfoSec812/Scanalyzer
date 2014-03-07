package com.zanclus.scanalyzer.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import com.zanclus.scanalyzer.domain.entities.Host;
import com.zanclus.scanalyzer.serialization.DateAdapter;
import com.zanclus.scanalyzer.serialization.JacksonDateSerializer;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;
import java.nio.file.attribute.UserPrincipal;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * Entity implementation class for Entity: Ports
 *
 */
@Entity
@Table(name="ports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@XmlRootElement(name="ports")
@XmlType(propOrder={"id", "hostId", "scanTime", "portStatus"})
@ApiModel(value="A text list of ports which were detected for a given scanning run for a given host.")
public class Ports implements Serializable, AccessControlledEntity {

	private static final long serialVersionUID = -6613411551243528421L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id ;

	@Column(length=1000000)
	@ApiModelProperty(value="The text list of ports detected for a given scan run", required=true)
	private String portStatus;

	@ApiModelProperty(value="The date/time of the scan run", required=true)
	private Date scanTime;

	@ManyToOne(cascade=CascadeType.MERGE)
	@ApiModelProperty(value="The host associated with this scan", required=true)
	private Host host;

	@XmlAttribute(name="id")
	public Long getId() {
		return id ;
	}

	@XmlAttribute(name="timestamp")
	@XmlJavaTypeAdapter(DateAdapter.class)
	@JsonValue
	@JsonSerialize(using=JacksonDateSerializer.class)
	public Date getScanTime() {
		return scanTime ;
	}

	@XmlTransient
	@JsonIgnore
	public Host getHost() {
		return host ;
	}

	@XmlValue
	public String getPortStatus() {
		return portStatus ;
	}

	@XmlAttribute(name="hostId")
	@JsonProperty(value="hostId")
	public Long getHostId() {
		return host.getId() ;
	}

	@Override
	public boolean canRead(UserPrincipal principal) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canWrite(UserPrincipal principal) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canModify(UserPrincipal principal) {
		// TODO Auto-generated method stub
		return false;
	}
}