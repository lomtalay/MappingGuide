package io.github.lomtalay.mappingguide;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import io.github.lomtalay.logger.LocalLogger;
import io.github.lomtalay.mappingguide.annotation.MappingGuide;
import io.github.lomtalay.mappingguide.annotation.MappingGuides;


@SuppressWarnings("rawtypes")
public class AnnotationPicker implements AnnotationBorrower {
	
	protected LocalLogger logger = LocalLogger.getLogger(getClass());


	private Annotation unwrapFromProxy(Proxy proxy) throws Throwable {
		
		try {
			MappingGuide chk = (MappingGuide)proxy;
			return chk;
		} catch(ClassCastException e) {
			logger.trace("Unable to unwrap from |"+proxy.getClass().getName()+"| as |"+MappingGuide.class.getName()+"|");
		}
		
		try {
			MappingGuides chk = (MappingGuides)proxy;
			return chk;
		} catch(ClassCastException e) {
			logger.trace("Unable to unwrap from |"+proxy.getClass().getName()+"| as |"+MappingGuides.class.getName()+"|");
		}
		
		return null;
	}
	
	private MappingGuide extractAnnotation(String targetCategory, Object annotation, String logMsg) {
		
		if(annotation == null) { 
			return null;
		}
		
		if(annotation instanceof MappingGuide) {
			if(!targetCategory
					.equals(
							((MappingGuide)annotation)
								.category())) {

				if(logger.isTraceEnabled()) {
					logger.trace( 
							"[" +
							((MappingGuide)annotation)
								.category() + 
							"] is not matched with target category ["+targetCategory+"]");
				}
				
				return null;
				
			}
			if(logger.isTraceEnabled()) {
				logger.trace("matched category ["+targetCategory+"] guide");
			}
			
			
			return (MappingGuide)annotation;	
		} else 
		if(annotation instanceof MappingGuides) {
			MappingGuide mappingGuides[] = ((MappingGuides)annotation).value();
			
			for(int j=0;j<mappingGuides.length;j++) {
				if(!targetCategory
						.equals(mappingGuides[j]
									.category())) {
					
					if(logger.isTraceEnabled()) {
						logger.trace( 
								logMsg + 
								"[" +
								mappingGuides[j]
										.category() + 
								"] is not matched with target category ["+targetCategory+"]");
					}
					
					continue;
				}
				if(logger.isTraceEnabled()) {
					logger.trace(logMsg + "matched category ["+targetCategory+"] guide");
				}
				
				return mappingGuides[j];
			}	
		} else 
		if(annotation instanceof Proxy) {
			Annotation rawTargetAnnotation;
			
			try {
				rawTargetAnnotation = unwrapFromProxy((Proxy)annotation);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
			
			return extractAnnotation(targetCategory, rawTargetAnnotation, logMsg + " (unwrap proxy)  ");
		} else {
			if(logger.isTraceEnabled()) {
				logger.trace("Annotation " + annotation.getClass().getName() + " is not support for MappingGuide");
			}
		}
		
		return null;
	}
	
	public MappingGuide getSupportedAnnotation(String currentCategory, Field currentField) {
		
		Annotation annotations[] = currentField.getDeclaredAnnotations();
		
		if(logger.isTraceEnabled()) {
			logger.trace("found " + annotations.length + " annotation(s).");
		}
		
		for(int i=0;i<annotations.length;i++) {
			MappingGuide res = extractAnnotation(currentCategory, annotations[i], "<"+i+">");
			if(res != null) return res;
		}
		
		return null;
	}

}
