/**
 * 
 */
package com.zanclus.scanalyzer.security;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import com.zanclus.scanalyzer.domain.entities.AccessControlledEntity;
import com.zanclus.scanalyzer.domain.entities.Ports;
import com.zanclus.scanalyzer.domain.entities.PortsCollectionWrapper;
import com.zanclus.scanalyzer.domain.entities.ScanCollectionWrapper;

/**
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public abstract class AccessControlJsonBodyWriter implements MessageBodyWriter<AccessControlledEntity> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.isInstance(AccessControlledEntity.class);
	}

	@Override
	public long getSize(AccessControlledEntity t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeTo(AccessControlledEntity t, Class<?> type,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		// TODO Auto-generated method stub
		if (type.isInstance(PortsCollectionWrapper.class)) {
			PortsCollectionWrapper newResult = new PortsCollectionWrapper() ;
			for (Ports ports: ((PortsCollectionWrapper)t).getPorts()) {
				
			}
		} else if (type.isInstance(ScanCollectionWrapper.class)){
			
		} else {
			
		}
	}

	public abstract String serialize(Serializable entity) ;
}
