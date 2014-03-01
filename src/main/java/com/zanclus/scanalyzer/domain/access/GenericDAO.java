package com.zanclus.scanalyzer.domain.access;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import javax.persistence.EntityManager;
import com.zanclus.scanalyzer.listeners.WebContext;

/**
 * A parameterized GenericDAO class which handles all of the common actions needed for any entity
 * 
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public class GenericDAO<T, K extends Serializable> {

	protected EntityManager em ;
	protected Class<T> entityClass ;

	@SuppressWarnings("unchecked")
	public GenericDAO() {
		super() ;
		ParameterizedType genericSuperClass = (ParameterizedType) getClass().getGenericSuperclass() ;
		entityClass = (Class<T>) genericSuperClass.getActualTypeArguments()[0] ;
	}

	public T findById(K id) {
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

	public void delete(K id) {
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
