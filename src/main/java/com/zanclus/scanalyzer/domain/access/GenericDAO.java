package com.zanclus.scanalyzer.domain.access;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zanclus.scanalyzer.listeners.WebContext;

/**
 * A parameterized GenericDAO class which handles all of the common actions needed for any entity
 * 
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public class GenericDAO<T, PK extends Serializable> {

	protected EntityManager em ;
	protected Class<T> entityClass ;
	protected Logger log = null ;

	@SuppressWarnings("unchecked")
	public GenericDAO() {
		super() ;
		ParameterizedType genericSuperClass = (ParameterizedType) getClass().getGenericSuperclass() ;
		entityClass = (Class<T>) genericSuperClass.getActualTypeArguments()[0] ;
		log = LoggerFactory.getLogger(entityClass) ;
	}

	public T findById(PK id) {
		em = WebContext.getEntityManager() ;
		em.getTransaction().begin() ;
		T retVal = em.find(entityClass, id) ;
		em.getTransaction().commit() ;
		em.close();
		return retVal ;
	}

	public T update(T entity) {
		em = WebContext.getEntityManager() ;
		em.getTransaction().begin() ;
		T retVal = em.merge(entity) ;
		em.getTransaction().commit() ;
		em.close();
		return retVal ;
	}

	public void delete(PK id) {
		em = WebContext.getEntityManager() ;
		em.getTransaction().begin() ;
		em.remove(em.find(entityClass, id)) ;
		em.getTransaction().commit() ;
		em.close();
	}

	public T create(T entity) {
		em = WebContext.getEntityManager() ;
		em.getTransaction().begin() ;
		em.persist(entity) ;
		em.getTransaction().commit() ;
		em.close();

		return entity ;
	}
}
