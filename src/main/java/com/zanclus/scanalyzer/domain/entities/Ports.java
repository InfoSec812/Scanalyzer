package com.zanclus.scanalyzer.domain.entities;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zanclus.scanalyzer.domain.entities.Host;
import com.zanclus.scanalyzer.serialization.DateAdapter;
import com.zanclus.scanalyzer.serialization.JacksonDateSerializer;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
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
public class Ports implements Serializable {

	   
	/**
	 * 
	 */
	private static final long serialVersionUID = -6613411551243528421L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String portStatus;
	private Date scanTime;

	@ManyToOne(cascade=CascadeType.ALL, optional=false)
	private Host host;

	@XmlJavaTypeAdapter(DateAdapter.class)
	@JsonValue
	@JsonSerialize(using=JacksonDateSerializer.class)
	public Date getScanTime() {
		return scanTime ;
	}
}
