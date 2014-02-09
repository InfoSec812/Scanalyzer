/**
 * 
 */
package com.zanclus.scanalyzer.domain.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;
import static javax.persistence.CascadeType.PERSIST;


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
public class Scan {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id ;

	@Column(name="scan_time", nullable=false, updatable=false, insertable=true)
	private Date scanTime = new Date() ;

	@Column(name="scan_results", columnDefinition="CLOB", nullable=false, updatable=false, insertable=true)
	private String scanResults = null ;

	@ManyToOne(cascade = PERSIST)
	private Host target ;
}
