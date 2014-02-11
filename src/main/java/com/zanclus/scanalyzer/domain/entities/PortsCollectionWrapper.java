package com.zanclus.scanalyzer.domain.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Stupid collection serialization DTO - This is 2014 already, why do we need this shit!
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name="portsHistory")
@XmlAccessorType(XmlAccessType.FIELD)
@ApiModel(value="A wrapper for collections of ports entries.")
public class PortsCollectionWrapper {
	@XmlElement(name="ports", type=Ports.class)
	@ApiModelProperty(value="A list of ports entries")
	private List<Ports> ports ;
}
