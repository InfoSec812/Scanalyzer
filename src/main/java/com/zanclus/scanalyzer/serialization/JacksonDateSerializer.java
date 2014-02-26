package com.zanclus.scanalyzer.serialization;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public class JacksonDateSerializer extends JsonSerializer<Date> {

	/* (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.JsonSerializer#serialize(java.lang.Object, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
	 */
	@Override
	public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZ") ;
		jgen.writeString(sdf.format(value)) ;
	}

}
