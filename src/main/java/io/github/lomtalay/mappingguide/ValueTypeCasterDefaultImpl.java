package io.github.lomtalay.mappingguide;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValueTypeCasterDefaultImpl implements ValueTypeCaster {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public Object cast(Object source, Class targetClass) {
		
		if(logger.isTraceEnabled()) {
			logger.trace(" cast value from "
					+ " [" + source + ((source!=null)?"<"+source.getClass().getName()+">":"") + " ] to "
					+ " <"+((targetClass!=null)?targetClass.getName():null)+">");
		}
		
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
		} else
		if(	targetClass.equals(String.class)) {
			
			return String.valueOf(source);
		} 
		
		throw new RuntimeException("Unsupport to cast value from <"+sourceType.getName()+"> to <"+targetClass.getName()+">");
	}

	public Object merge(Map<String, Object> sourceValues, String untokenizedKey, Class targetClass) {
		
		Object result = null;
		if(targetClass.equals(String.class)) {
			
			String valResult = untokenizedKey;
			
			for(String key : sourceValues.keySet()) {
				Object val = sourceValues.get(key);
				String strVal = "";
				if(val != null) {
					strVal = String.valueOf(sourceValues.get(key));
				}
				
				valResult = valResult.replaceAll(key, strVal);
			}
			result = valResult.trim();
			
		} else {
			logger.trace("Unsupport to merge to <"+targetClass.getName()+">");
		}
		
		if(logger.isTraceEnabled()) {
			logger.trace("Merge result is " + result + ((result!=null)?"<"+result.getClass().getName()+">":""));
		}
		
		return result;
	}

	public boolean isSupportMerge(Class targetClass) {
		
		if(targetClass.equals(String.class)) {
			return true;
		}

		return false;
	}

}
