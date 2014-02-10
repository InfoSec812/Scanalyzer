package com.zanclus.scanalyzer.domain.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Stupid collection serialization DTO - This is 2014 already, why do we need this shit!
 * @author <a href="mailto: ***REMOVED***">Deven Phillips</a>
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name="portsHistory")
@XmlAccessorType(XmlAccessType.FIELD)
public class PortsCollectionWrapper {
	@XmlElement(name="ports", type=Ports.class)
	private List<Ports> ports ;
}
