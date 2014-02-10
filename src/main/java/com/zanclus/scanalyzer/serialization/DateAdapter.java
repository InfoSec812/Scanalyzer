/**
 * 
 */
package com.zanclus.scanalyzer.serialization;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author <a href="mailto: ***REMOVED***">Deven Phillips</a>
 *
 */
public class DateAdapter extends XmlAdapter<String,Date> {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZ") ;

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Date unmarshal(String v) throws Exception {
		// TODO Auto-generated method stub
		return sdf.parse(v) ;
	}

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(Date v) throws Exception {
		// TODO Auto-generated method stub
		return sdf.format(v) ;
	}

}
