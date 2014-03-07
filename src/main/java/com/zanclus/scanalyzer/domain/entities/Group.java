package com.zanclus.scanalyzer.domain.entities;

import java.io.Serializable;
import java.lang.Long;
import java.util.List;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * Entity implementation class for Entity: Group
 *
 */
@Entity
@Table(name="groups")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group implements Serializable {

	   
	@Id
	private Long id ;

	private String name ;
	
	private String description ;
	
	@ManyToMany
	@JoinTable(name="user_groups")
	private List<User> members ;

	private static final long serialVersionUID = 1L ;
}
