package com.zanclus.scanalyzer.domain.entities;

/**
 * A wrapper around the User and Group entities so that they can be used interchangeably in authorization code
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public interface RightsHolder {

	public boolean isAuthorized(IndexedEntity entity) ;
}
