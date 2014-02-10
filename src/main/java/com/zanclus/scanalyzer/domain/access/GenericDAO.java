package com.zanclus.scanalyzer.domain.access;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import javax.persistence.EntityManager;
import com.zanclus.scanalyzer.listeners.WebContext;

/**
 * @author <a href="mailto: deven.phillips@gmail.com">Deven Phillips</a>
 *
 */
public class GenericDAO<T, PK extends Serializable> {

	protected EntityManager em ;
	protected Class<T> entityClass ;

	@SuppressWarnings("unchecked")
	public GenericDAO() {
		super() ;
		em = WebContext.getEntityManager() ;
		ParameterizedType genericSuperClass = (ParameterizedType) getClass().getGenericSuperclass() ;
		entityClass = (Class<T>) genericSuperClass.getActualTypeArguments()[0] ;
	}

	public T findById(PK id) {
		em.getTransaction().begin() ;
		T retVal = em.find(entityClass, id) ;
		em.getTransaction().commit() ;
		return retVal ;
	}

	public T update(T entity) {
		em.getTransaction().begin() ;
		T retVal = em.merge(entity) ;
		em.getTransaction().commit() ;
		return retVal ;
	}

	public void delete(PK id) {
		em.getTransaction().begin() ;
		em.remove(em.find(entityClass, id)) ;
		em.getTransaction().commit() ;
	}

	public T create(T entity) {
		em.getTransaction().begin() ;
		em.persist(entity) ;
		em.getTransaction().commit() ;

		return entity ;
	}
}
