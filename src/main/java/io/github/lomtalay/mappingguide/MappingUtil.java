package io.github.lomtalay.mappingguide;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import io.github.lomtalay.logger.LocalLogger;
import io.github.lomtalay.logger.LocalLogger.LocalLogLevel;
import io.github.lomtalay.mappingguide.annotation.MappingGuide;
import io.github.lomtalay.mappingguide.annotation.MappingGuides;
import io.github.lomtalay.mappingguide.annotation.MappingGuide.FillCondition;
import io.github.lomtalay.mappingguide.annotation.MappingGuide.NamingStrictLevel;



public class MappingUtil {

	protected static LocalLogger logger = LocalLogger.getLogger(MappingUtil.class);
	private static final ValueTypeCaster defaultValueTypeCaster = new ValueTypeCasterDefaultImpl();
	private static final ValueExtractor defaultValueExtractor = new ValueExtractorDefaultImpl();
	private static ValueTypeCaster globalValueTypeCaster = defaultValueTypeCaster;
	private static ValueExtractor globalValueExtractor = defaultValueExtractor;
	private static final HashMap<String, AnnotationBorrower> registeredAnnotationBorrower = new HashMap<String, AnnotationBorrower>();
	private static final AnnotationBorrower annotationPicker = new AnnotationPicker();
	
	public static void forceLogLevel(String levelName) {
		LocalLogger.setForceLevel(levelName);
	}
	
	/**
	 * This method use for add other annotation that will allow to borrow to use for mapping.
	 * AnnotationBorrower will be object to cast source annotation to MappingAnnotation  
	 * 
	 * @param category
	 * @param annotationBorrower
	 * @param replace (flag to force replace existing registered)
	 */
	public static void registerAnnotationBorrower(String category, AnnotationBorrower annotationBorrower, boolean replace) {
		AnnotationBorrower tmp = registeredAnnotationBorrower.get(category);
		if(	tmp != null) {
			if(!replace) {
				throw new IllegalAccessError("category |"+category+"| is already registered for |"+tmp.getClass().getName()+"|");
			}
		}
		
		registeredAnnotationBorrower.put(category, annotationBorrower);
	}
	
	public static void setGlobalValueExtractor(ValueExtractor newValueExtractor) {
		if(newValueExtractor == null) {
			logger.warn("reset globalValueExtractor to default");
			globalValueExtractor = defaultValueExtractor;
		} else {
			globalValueExtractor = newValueExtractor;
		}
	}

	public static void setGlobalTypeCaster(ValueTypeCaster newValueTypeCaster) {
		if(newValueTypeCaster == null) {
			logger.warn("reset globalValueTypeCaster to default");
			globalValueTypeCaster = defaultValueTypeCaster;
		} else {
			globalValueTypeCaster = newValueTypeCaster;
		}
	}

	public static ValueTypeCaster getGlobalValueTypeCaster() {
		return globalValueTypeCaster;
	}
	public static ValueExtractor getGlobalValueExtractor() {
		return globalValueExtractor;
	}
	
	public static Object extractFieldValue(String fieldName, Object sourceObj) throws IllegalArgumentException, IllegalAccessException {
		return extractFieldValue(fieldName, sourceObj, 0);
	}
	
	@SuppressWarnings("rawtypes")
	public static Object extractFieldValue(String fieldName, Object sourceObj, int strictLevel) throws IllegalArgumentException, IllegalAccessException {
		Class cls = sourceObj.getClass();
		Field fields[] = cls.getDeclaredFields();
		Object result = null;
		
		for(int i=0;i<fields.length;i++) {
			String checkingFieldName = fields[i].getName();
			if(fieldName.equals(checkingFieldName)) {
				fields[i].setAccessible(true);
				
				result = fields[i].get(sourceObj);
				break;
			} else 
			if(strictLevel < 3 && fieldName.equalsIgnoreCase(checkingFieldName)) {
				fields[i].setAccessible(true);
				
				result = fields[i].get(sourceObj);
				break;
			} else 
			if(strictLevel < 2) {
				
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
		
		if(logger.isDebugEnabled()) {
			logger.debug("extractFieldValue("+fieldName+", "+sourceObj.getClass().getName() + ", " + strictLevel + ") = " + result );
		}
		
		return result;
	}
	
	private static MappingGuide extractMappingGuide(String targetCategory, Field currentField) {
		
		if(targetCategory == null) {
			logger.trace("use default <null> category");
			targetCategory = "";
		}
		
		if(	registeredAnnotationBorrower.keySet().size() > 0 )
		{
			AnnotationBorrower annoBorrower = registeredAnnotationBorrower.get(targetCategory);
			
			if(annoBorrower != null) {
				MappingGuide borrowAnnotated = annoBorrower.getSupportedAnnotation(targetCategory, currentField);

				if(logger.isTraceEnabled()) {
					logger.trace("use MappingGuide from <"+annoBorrower.getClass().getName()+">");
				}
				
				return borrowAnnotated;
			}
		}
		
		return annotationPicker.getSupportedAnnotation(targetCategory, currentField);
	}
	
	@SuppressWarnings({ "rawtypes" })
	private static Enum<?> enumPick(Class<? extends Enum> enumType, Object value) {
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
	

	private static MappingGuide getDefaultAnnotationInstance(final Field currentField) {
		
		return new MappingGuide() {

			public Class<? extends Annotation> annotationType() {
				return this.getClass();
			}

			public String category() {
				return null;
			}

			public String key() {
				return currentField.getName();
			}

			public NamingStrictLevel namingStrict() {
				return NamingStrictLevel.STRICT;
			}

			public FillCondition condition() {
				return FillCondition.SKIP_NULL_REPLACEMENT;
			}

			public Class typecaster() {
				return null;
			}

			public Class valueExtractor() {
				return null;
			}

			public String extra() {
				return null;
			}};
	}
	
	
	private static void fillValue(Field currentField, Object destBean, Object value, FillCondition targetFillCondition, ValueTypeCaster valueTypeCaster) throws IllegalArgumentException, IllegalAccessException {
		

		if(targetFillCondition.equals(FillCondition.SKIP_NULL_REPLACEMENT)) {
			if(value == null) {
				if(logger.isTraceEnabled()) {
					logger.trace(" skip to fill null to field ");
				}
				return;
			}
		}
		
		if(currentField.getType().isEnum()) {
			
			Class<? extends Enum> enumType = (Class<Enum>)currentField.getType();
			currentField.set(destBean, enumPick(enumType, value));
			
		} else {
			try {
				currentField.set(destBean, value);
			} catch(IllegalArgumentException ee) {
				
				if(logger.isTraceEnabled()) {
					logger.trace(" fail : " + ee.getMessage() + " :: retry using type casting  ");
				}
				
				if(value != null) {
					
					Class fieldType = currentField.getType();
					
					currentField.set(
							destBean, 
							valueTypeCaster.cast(value, fieldType));
					
				}
			}
		}
		
	}
	
	
	//Copy field value follow MappingGuide
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void fillField(String targetCategory, Field currentField, Object destBean, Object sourceBean, ValueTypeCaster valueTypeCaster) {
		
		MappingGuide mappingGuide;
		
		
		
		int fieldModifiers = currentField.getModifiers();
	    if(	Modifier.isStatic(fieldModifiers) ) {
	    	logger.debug("Skip static field.");
	    	return;
	    }
	    if(	Modifier.isFinal(fieldModifiers)) {
	    	logger.debug("Skip final field.");
	    	return;
	    }
		
		
		if(targetCategory != null) {
			mappingGuide = extractMappingGuide(targetCategory, currentField);
			if(mappingGuide == null) {
				logger.trace("Not found compatible MappingGuide annotation for field ["+currentField.getName()+"] of class <"+ destBean.getClass().getName() +"> , using default mapping.");
				mappingGuide = getDefaultAnnotationInstance(currentField);
			}
		} else {
			mappingGuide = getDefaultAnnotationInstance(currentField);
		}
		
		if(mappingGuide != null) {
			try {
				currentField.setAccessible(true);
				String targetFieldName = mappingGuide.key();
				FillCondition targetFillCondition = mappingGuide.condition();
				NamingStrictLevel namingStrictLevel = mappingGuide.namingStrict();
				Class expectTypeCasterCls = mappingGuide.typecaster();
				Class valueExtracterCls = mappingGuide.valueExtractor();
				ValueExtractor valueExtractor = null;
				String extraAnnotate = mappingGuide.extra();
				
				if(	expectTypeCasterCls != null && 
					!expectTypeCasterCls.isInterface()) {
					
					logger.debug("Expect to use ValueTypeCaster as <"+expectTypeCasterCls.getName()+">");
					try {
						Object overrideValueTypeCaster = mappingGuide.typecaster().newInstance();
						if(overrideValueTypeCaster instanceof ValueTypeCaster) {
							valueTypeCaster = (ValueTypeCaster)overrideValueTypeCaster;
						} else {
							
							throw new IllegalArgumentException("Invalid typecaster in @MappingGuide annotation of field {"+currentField.getName()+"} of class <"+currentField.getDeclaringClass().getName()+">" );
						}
					} catch (Exception e) {
						logger.debug("Fail to use expect ValueTypeCaster as <"+expectTypeCasterCls.getName()+"> ", e);
					}
				}
				
				if(	valueExtracterCls != null && 
					!valueExtracterCls.isInterface()) {
						
					logger.debug("Expect to use ValueTypeCaster as <"+expectTypeCasterCls.getName()+">");
					try {
						Object tmpValueExtractor = mappingGuide.valueExtractor().newInstance();
						if(tmpValueExtractor instanceof ValueExtractor) {
							valueExtractor = (ValueExtractor)tmpValueExtractor;
						} else {
							throw new IllegalArgumentException("Invalid valueExtractor in @MappingGuide annotation of field {"+currentField.getName()+"} of class <"+currentField.getDeclaringClass().getName()+">" );
						}
					} catch (Exception e) {
						logger.debug("Fail to use expect ValueTypeCaster as <"+expectTypeCasterCls.getName()+"> ", e);
					}
				}
				if(valueExtractor == null) {
					valueExtractor = globalValueExtractor;
				}
				
				
//				int namingStrictLevelCode = 0;
//				
//				if(NamingStrictLevel.STRICT.equals(namingStrictLevel)) {
//					namingStrictLevelCode = 3;
//				} else
//				if(NamingStrictLevel.IGNORE_CASE.equals(namingStrictLevel)) {
//					namingStrictLevelCode = 2;
//				}
				
				if(logger.isTraceEnabled()) {
					logger.trace(" target fill condition " + targetFillCondition);
				}
				
				if(targetFillCondition.equals(FillCondition.ONLY_IF_NULL)) {
					Object tmp = currentField.get(destBean);
					
					if(logger.isTraceEnabled()) {
						logger.trace(" current field value is " + tmp);
					}
					if(tmp != null) {
						if(logger.isTraceEnabled()) {
							logger.trace(" field value is already set (not null) ");
						}
						return;
					}
				} 
				
				Object value;
				
				
				String fieldNames[] = valueExtractor.tokenizeMappingKey(targetFieldName); 
				
				if(fieldNames.length > 1) {
					
					if(!valueTypeCaster.isSupportMerge(currentField.getType())) {
						throw new UnsupportedOperationException("Unsupport to merge multivalue to type <"+currentField.getType().getName()+">");
					}
					
					HashMap<String, Object> values = new HashMap<String, Object>();
					
					//Prepare for remove duplicate name.
					for(String fieldName : fieldNames) {
						values.put(fieldName, null);
					}
					//Put all extract values.
					for(String fieldName : values.keySet()) {
						
						Object subValue = valueExtractor.extractFieldValue(fieldName, sourceBean, namingStrictLevel, extraAnnotate);
						//extractFieldValue(fieldName, sourceBean, namingStrictLevel);
						values.put(fieldName, subValue);
					}
					
					value = valueTypeCaster.merge(values, targetFieldName, currentField.getType());
					fillValue(currentField, destBean, value, targetFillCondition, valueTypeCaster);
				} else {
					value = valueExtractor.extractFieldValue(targetFieldName, sourceBean, namingStrictLevel, extraAnnotate); 
					//extractFieldValue(targetFieldName, sourceBean, namingStrictLevel);
					fillValue(currentField, destBean, value, targetFillCondition, valueTypeCaster);
				}
				
//				{
//					if(targetFillCondition.equals(FillCondition.SKIP_NULL_REPLACEMENT)) {
//						if(value == null) {
//							if(logger.isTraceEnabled()) {
//								logger.trace(" skip to fill null to field ");
//							}
//							return;
//						}
//					}
//					
//					if(currentField.getType().isEnum()) {
//						
//						Class<? extends Enum> enumType = (Class<Enum>)currentField.getType();
//						currentField.set(destBean, enumPick(enumType, value));
//						
//					} else {
//						try {
//							currentField.set(destBean, value);
//						} catch(IllegalArgumentException ee) {
//							
//							if(logger.isTraceEnabled()) {
//								logger.trace(" fail : " + ee.getMessage() + " :: retry using type casting  ");
//							}
//							
//							if(value != null) {
//								
//								Class fieldType = currentField.getType();
//								
//								currentField.set(
//										destBean, 
//										valueTypeCaster.cast(value, fieldType));
//								
//	//							Class fieldType = currentField.getType();
//	//							Class valueType = value.getClass();
//	//							if(fieldType.equals(Double.class)) {
//	//								if(valueType.equals(BigInteger.class)) {
//	//									currentField.set(
//	//											destBean, 
//	//											((BigInteger)value).doubleValue());
//	//								}
//	//							}
//							}
//						}
//					}
//				}
				
				
				
				if(logger.isTraceEnabled()) {
					Object tmp = currentField.get(destBean);
					logger.trace(" finally value of field {" +currentField.getName() + "} = " + tmp);
				}
				
			} catch (Exception e) {
				//IllegalArgumentException, IllegalAccessException 
				throw new RuntimeException("Error at field {"+currentField.getName()+"} : " + e.getMessage(), e);
			}
		} 
		
	}
	
	/**
	 * change to use getGlobalValueTypeCaster() instead
	 * @return
	 */
	@Deprecated
	public static ValueTypeCaster getDefaultValueTypeCaster() {
		return globalValueTypeCaster;
	}
	
	@SuppressWarnings("rawtypes")
	public static void fillBean(String targetCategory, MappingGuideSupported destBean, Object sourceBean) {
		fillBean(targetCategory, destBean, sourceBean, null);
	}
	
	@SuppressWarnings("rawtypes")
	public static void fillBean(String targetCategory, MappingGuideSupported destBean, Object sourceBean, ValueTypeCaster valueTypeCaster) {
		
		if(logger.isTraceEnabled()) {
			logger.trace(" fillBean invoked ");
		}
		
		if(valueTypeCaster == null) {
			valueTypeCaster = globalValueTypeCaster;
		}
		Class destClass = destBean.getClass();
		Field fields[] = destClass.getDeclaredFields();
		
		if(sourceBean instanceof Collection) {
			logger.warn("Source object<"+sourceBean.getClass().getName()+"> is implementation of collection, it's not compatible for bean structure.");	
		}
		
		for(int i=0;i<fields.length;i++) {
			
			if(logger.isTraceEnabled()) {
				logger.trace(" process field " + fields[i].getName() + "... ");
			}	
			
			fillField(targetCategory, fields[i], destBean, sourceBean, valueTypeCaster);
		}
	}
}
