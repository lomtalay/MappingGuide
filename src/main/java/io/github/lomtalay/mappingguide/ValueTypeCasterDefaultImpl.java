package io.github.lomtalay.mappingguide;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValueTypeCasterDefaultImpl implements ValueTypeCaster {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public Object cast(Object source, Class targetClass) {
		
		if(targetClass == null) return null;
		if(source == null) return null;
		
		Class sourceType = source.getClass();
		
		if(	targetClass.equals(Double.class) ||
			targetClass.equals(Double.TYPE)) {
			
			if(sourceType.equals(BigInteger.class)) {
				return ((BigInteger)source).doubleValue();
			} else
			if(sourceType.equals(BigDecimal.class)) {
				return ((BigDecimal)source).doubleValue();
			}
		} else
		if(	targetClass.equals(Integer.class) ||
			targetClass.equals(Integer.TYPE)) {
			
			if(sourceType.equals(BigInteger.class)) {
				return ((BigInteger)source).intValue();
			} else
			if(sourceType.equals(BigDecimal.class)) {
				return ((BigDecimal)source).intValue();
			}
		} else
		if(	targetClass.equals(Date.class)) {
			
			if(	sourceType.equals(Long.class) ||
				sourceType.equals(Long.TYPE)) {
				
				return new Date((Long)source);
			}
		} 
		
		throw new RuntimeException("Unsupport to cast value from <"+sourceType.getName()+"> to <"+targetClass.getName()+">");
	}

}
