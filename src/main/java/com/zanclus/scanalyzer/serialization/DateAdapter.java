package com.zanclus.scanalyzer.serialization;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author <a href="https://github.com/InfoSec812">Deven Phillips</a>
 *
 */
public class DateAdapter extends XmlAdapter<String,Date> {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZ") ;

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Date unmarshal(String v) throws ParseException {
		return sdf.parse(v) ;
	}

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(Date v) throws ParseException {
		return sdf.format(v) ;
	}

}
