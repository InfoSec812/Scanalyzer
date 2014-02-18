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
import com.zanclus.scanalyzer.domain.entities.AuditEntry;
import com.zanclus.scanalyzer.domain.entities.User;
import com.zanclus.scanalyzer.listeners.WebContext;

/**
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public class Auditor {

	private Auditor() {
		super() ;
	}

	private static final Logger LOG = LoggerFactory.getLogger(Auditor.class) ;

	public static <T> void writeAuditEntry(User user, String operation, Class<T> clazz, T entity) {
		EntityManager em = WebContext.getEntityManager() ;
		ObjectMapper mapper = new ObjectMapper() ;
		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
		mapper.setAnnotationIntrospector(introspector) ;
		String content = null ;
		try {
			content = mapper.writeValueAsString(entity);
		} catch (JsonProcessingException e) {
			LOG.error("Unable to serialize audit entity", e) ;
		}
		LOG.debug(content) ;
		em.getTransaction().begin() ;
		User validUser = em.find(User.class, user.getId()) ;
		AuditEntry newEntry = AuditEntry.builder()
									.type(entity.getClass().getCanonicalName())
									.user(validUser)
									.operation(operation)
									.content(content)
									.build() ;
		em.persist(newEntry) ;
		em.getTransaction().commit() ;
		em.close() ;
	}
}
