/**
 * 
 */
package com.zanclus.scanalyzer;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.google.gson.Gson;
import com.zanclus.scanalyzer.domain.entities.AuditEntry;
import com.zanclus.scanalyzer.domain.entities.User;
import com.zanclus.scanalyzer.listeners.WebContext;

/**
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public class Auditor {

	public static <T> void writeAuditEntry(User user, String operation, Class<T> clazz, T entity) {
		Logger log = LoggerFactory.getLogger(Auditor.class) ;
		EntityManager em = WebContext.getEntityManager() ;
		ObjectMapper mapper = new ObjectMapper() ;
		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
		mapper.setAnnotationIntrospector(introspector) ;
		String content = null ;
		try {
			content = mapper.writeValueAsString(entity);
		} catch (JsonProcessingException e) {
			log.error("Unable to serialize audit entity", e) ;
		}
		log.debug(content) ;
		em.getTransaction().begin() ;
		user = em.find(User.class, user.getId()) ;
		AuditEntry newEntry = AuditEntry.builder()
									.type(entity.getClass().getCanonicalName())
									.user(user)
									.operation(operation)
									.content(content)
									.build() ;
		em.persist(newEntry) ;
		em.getTransaction().commit() ;
		em.close() ;
	}
}
