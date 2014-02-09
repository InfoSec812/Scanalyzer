package com.zanclus.scanalyzer.domain.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name="scans")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScanCollectionWrapper {
	@XmlElement(name="scan", type=Scan.class)
	private List<Scan> scans ;
}
