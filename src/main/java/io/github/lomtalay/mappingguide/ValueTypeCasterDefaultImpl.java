package io.github.lomtalay.mappingguide;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

import io.github.lomtalay.logger.LocalLogger;



public class ValueTypeCasterDefaultImpl implements ValueTypeCaster {
	
	protected LocalLogger logger = LocalLogger.getLogger(getClass());

	public Object cast(Object source, Class targetClass) {
		
		if(logger.isTraceEnabled()) {
			logger.trace(" cast value from "
					+ " [" + source + ((source!=null)?"<"+source.getClass().getName()+">":"") + "] to "
					+ " <"+((targetClass!=null)?targetClass.getName():null)+">");

		}
		
		if(targetClass == null) return null;
		if( targetClass.isEnum() ) {
			Class<? extends Enum> enumType = (Class<Enum>)targetClass;
			return enumPick(enumType, source);
		}
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

	private Enum<?> enumPick(Class<? extends Enum> enumType, Object value) {
		
		if(logger.isTraceEnabled()) {
			logger.trace(" enumPick <"+enumType.getName()+"> , value |" + value + "| ");
		}
		
		Object enumSet[] = enumType.getEnumConstants();
		for(int i=0;i<enumSet.length;i++) {
			
			if(logger.isTraceEnabled()) {
				logger.trace(" enumSet["+i+"] is " + enumSet[i] + ", checking value is " + value);
			}

			
			if(	enumSet[i].toString() != null ) {
				if( enumSet[i].toString()
								.equals(value)) {
					return (Enum<?>) enumSet[i];
				}
			} else {
				if(value == null) {
					return (Enum<?>) enumSet[i];
				}
			}
		}

		throw new RuntimeException("Value ["+value+"] is not compatible with enum <"+enumType.getName()+">");
	}
}
