/**
 * 
 */
package com.zanclus.scanalyzer.domain.entities;

import java.nio.file.attribute.UserPrincipal;

/**
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public interface AccessControlledEntity {

	public boolean canRead(UserPrincipal principal) ;

	public boolean canWrite(UserPrincipal principal) ;

	public boolean canModify(UserPrincipal principal) ;
}
