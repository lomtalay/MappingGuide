package io.github.lomtalay.mappingguide;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.lomtalay.mappingguide.annotation.MappingGuide;
import io.github.lomtalay.mappingguide.annotation.MappingGuide.NamingStrictLevel;;

public class ValueExtractorDefaultImpl implements ValueExtractor {
	
	protected Logger logger = LoggerFactory.getLogger(MappingUtil.class);
	private static final String VALID_FIELD_PATTERN_REXP = "[a-zA-Z0-9_]+(\\[[0-9]*\\])?([.][a-zA-Z0-9_]+(\\[[0-9]*\\])?)*";
	
	
	public String[] tokenizeMappingKey(String mappingKey) {
		
		List<String> fieldPattern = new ArrayList<String>();
		Pattern pattern = Pattern.compile(VALID_FIELD_PATTERN_REXP);
		Matcher matcher = pattern.matcher(mappingKey);
		
		while(matcher.find()) {
			String found = mappingKey.substring(matcher.start(), matcher.end());
            fieldPattern.add(found);
        }
		
		return (String[])fieldPattern.toArray(new String[fieldPattern.size()]);
	}
	
	
	public Object extractFieldValue(String patternFieldName, Map sourceObj, MappingGuide.NamingStrictLevel strictLevel, String extraAnnotate) throws IllegalArgumentException, IllegalAccessException {
		int namingStrictLevelCode = 0;
		
		if(NamingStrictLevel.STRICT.equals(strictLevel)) {
			namingStrictLevelCode = 3;
		} else
		if(NamingStrictLevel.IGNORE_CASE.equals(strictLevel)) {
			namingStrictLevelCode = 2;
		}
		
		Iterator keySet = sourceObj.keySet().iterator();
		Object result = null;
		

		List<String> fieldPattern = new ArrayList<String>();
		Pattern pattern = Pattern.compile("[a-zA-Z0-9_]+(\\[[0-9]*\\])?");
		Matcher matcher = pattern.matcher(patternFieldName);
		String fieldName = null;
		String nextLevelFieldName = null;
		if(matcher.find()) {
			fieldName = patternFieldName.substring(matcher.start(), matcher.end());
			nextLevelFieldName = patternFieldName.substring(matcher.end());
		} else {
			throw new IllegalAccessException("Unsupport fieldName pattern |"+patternFieldName+"|");
		}
		
		if(nextLevelFieldName.trim().length() == 0) {
			nextLevelFieldName = null;
		}
		
		while(keySet.hasNext()) {
			Object key = keySet.next();
			
			String checkingFieldName = (String)key;
			
			if(fieldName.equals(checkingFieldName)) {
				
				result = sourceObj.get(key);
				break;
			} else 
			if(namingStrictLevelCode < 3 && fieldName.equalsIgnoreCase(checkingFieldName)) {

				result = sourceObj.get(key);
				break;
			} else 
			if(namingStrictLevelCode < 2) {
				
				String targetFieldName = fieldName.replaceAll("_", "");
				String checkingFieldName_ = checkingFieldName.replaceAll("_", "");
				
				if(logger.isTraceEnabled()) {
					logger.trace("targetFieldName : " + targetFieldName);
					logger.trace("checkingFieldName_ : " + checkingFieldName_);
				}
				
				if(targetFieldName
						.equalsIgnoreCase(checkingFieldName_)) {
					
					result = sourceObj.get(key);
					break;	
				}
				
			}
		}
		

		if(	result != null 
			&& nextLevelFieldName != null) {
			result = extractFieldValue(nextLevelFieldName, (Object)result, strictLevel, extraAnnotate);
		}
		
		if(logger.isDebugEnabled()) {
			if(	logger.isTraceEnabled() || 
				nextLevelFieldName == null) {
				
				logger.debug("extractFieldValue("+patternFieldName+", "+sourceObj.getClass().getName() + ", " + strictLevel + ") = " + result );
			}
		}
		
		return result;
	}

	public Object extractFieldValue(String patternFieldName, Object sourceObj, MappingGuide.NamingStrictLevel strictLevel, String extraAnnotate) throws IllegalArgumentException, IllegalAccessException {
		
		
		if(sourceObj instanceof Map) {
			return extractFieldValue(patternFieldName, (Map)sourceObj, strictLevel, extraAnnotate);
		}
		
		int namingStrictLevelCode = 0;
		
		if(NamingStrictLevel.STRICT.equals(strictLevel)) {
			namingStrictLevelCode = 3;
		} else
		if(NamingStrictLevel.IGNORE_CASE.equals(strictLevel)) {
			namingStrictLevelCode = 2;
		}
		
		Class cls = sourceObj.getClass();
		Field fields[] = cls.getDeclaredFields();
		Object result = null;
		

		List<String> fieldPattern = new ArrayList<String>();
		Pattern pattern = Pattern.compile("[a-zA-Z0-9_]+(\\[[0-9]*\\])?");
		Matcher matcher = pattern.matcher(patternFieldName);
		String fieldName = null;
		String nextLevelFieldName = null;
		if(matcher.find()) {
			fieldName = patternFieldName.substring(matcher.start(), matcher.end());
			nextLevelFieldName = patternFieldName.substring(matcher.end());
		} else {
			throw new IllegalAccessException("Unsupport fieldName pattern |"+patternFieldName+"|");
		}
		
		if(nextLevelFieldName.trim().length() == 0) {
			nextLevelFieldName = null;
		}
		
		for(int i=0;i<fields.length;i++) {
			String checkingFieldName = fields[i].getName();
			
			if(fieldName.equals(checkingFieldName)) {
				fields[i].setAccessible(true);
				
				result = fields[i].get(sourceObj);
				break;
			} else 
			if(namingStrictLevelCode < 3 && fieldName.equalsIgnoreCase(checkingFieldName)) {
				fields[i].setAccessible(true);
				
				result = fields[i].get(sourceObj);
				break;
			} else 
			if(namingStrictLevelCode < 2) {
				
				String targetFieldName = fieldName.replaceAll("_", "");
				String checkingFieldName_ = checkingFieldName.replaceAll("_", "");
				
				if(logger.isTraceEnabled()) {
					logger.trace("targetFieldName : " + targetFieldName);
					logger.trace("checkingFieldName_ : " + checkingFieldName_);
				}
				
				if(targetFieldName
						.equalsIgnoreCase(checkingFieldName_)) {
					
					fields[i].setAccessible(true);
					
					result = fields[i].get(sourceObj);
					break;	
				}
				
			}
		}
		

		if(	result != null 
			&& nextLevelFieldName != null) {
			result = extractFieldValue(nextLevelFieldName, result, strictLevel, extraAnnotate);
		}
		
		if(logger.isDebugEnabled()) {
			if(	logger.isTraceEnabled() || 
				nextLevelFieldName == null) {
				
				logger.debug("extractFieldValue("+patternFieldName+", "+sourceObj.getClass().getName() + ", " + strictLevel + ") = " + result );
			}
		}
		
		return result;
	}

}
