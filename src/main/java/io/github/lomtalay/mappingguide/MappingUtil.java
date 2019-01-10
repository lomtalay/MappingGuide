package io.github.lomtalay.mappingguide;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.lomtalay.mappingguide.annotation.MappingGuide;
import io.github.lomtalay.mappingguide.annotation.MappingGuides;
import io.github.lomtalay.mappingguide.annotation.MappingGuide.FillCondition;
import io.github.lomtalay.mappingguide.annotation.MappingGuide.NamingStrictLevel;



public class MappingUtil {

	protected static Logger logger = LoggerFactory.getLogger(MappingUtil.class);
	private static ValueTypeCaster defaultValueTypeCaster = new ValueTypeCasterDefaultImpl();
	
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
		
		Annotation annotations[] = currentField.getDeclaredAnnotations();
		
		if(logger.isTraceEnabled()) {
			logger.trace("found " + annotations.length + " annotation(s).");
		}
		
		for(int i=0;i<annotations.length;i++) {
			if(annotations[i] instanceof MappingGuide) {
				if(!targetCategory
						.equals(
								((MappingGuide)annotations[i])
									.category())) {

					if(logger.isTraceEnabled()) {
						logger.trace( 
								"[" +
								((MappingGuide)annotations[i])
									.category() + 
								"] is not matched with target category ["+targetCategory+"]");
					}
					
					continue;
				}
				if(logger.isTraceEnabled()) {
					logger.trace("matched category ["+targetCategory+"] guide");
				}
				
				
				return (MappingGuide)annotations[i];	
			} else 
			if(annotations[i] instanceof MappingGuides) {
				MappingGuide mappingGuides[] = ((MappingGuides)annotations[i]).value();
				
				for(int j=0;j<mappingGuides.length;j++) {
					if(!targetCategory
							.equals(mappingGuides[j]
										.category())) {
						
						if(logger.isTraceEnabled()) {
							logger.trace( 
									"<" + i +">" + 
									"[" +
									mappingGuides[j]
											.category() + 
									"] is not matched with target category ["+targetCategory+"]");
						}
						
						continue;
					}
					if(logger.isTraceEnabled()) {
						logger.trace("<" + i +">" + "matched category ["+targetCategory+"] guide");
					}
					
					return mappingGuides[j];
				}	
			} else {
				if(logger.isTraceEnabled()) {
					logger.trace(annotations[i].getClass().getName() + " is not support for MappingGuide");
				}
			}
		}
		
		return null;
	}
	
	@SuppressWarnings({ "rawtypes" })
	private static Enum<?> enumPick(Class<? extends Enum> enumType, Object value) {
		Object enumSet[] = enumType.getEnumConstants();
		for(int i=0;i<enumSet.length;i++) {
			if(enumSet[i].toString().equals(value)) {
				return (Enum<?>) enumSet[i]; 
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
			}};
	}
	
	//Copy field value follow MappingGuide
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void fillField(String targetCategory, Field currentField, Object destBean, Object sourceBean, ValueTypeCaster valueTypeCaster) {
		
		MappingGuide mappingGuide;
		
		if(targetCategory != null) {
			mappingGuide = extractMappingGuide(targetCategory, currentField);
		} else {
			mappingGuide = getDefaultAnnotationInstance(currentField);
		}
		
		if(mappingGuide != null) {
			try {
				currentField.setAccessible(true);
				String targetFieldName = mappingGuide.key();
				FillCondition targetFillCondition = mappingGuide.condition();
				NamingStrictLevel namingStrictLevel = mappingGuide.namingStrict();
				
				int namingStrictLevelCode = 0;
				
				if(NamingStrictLevel.STRICT.equals(namingStrictLevel)) {
					namingStrictLevelCode = 3;
				} else
				if(NamingStrictLevel.IGNORE_CASE.equals(namingStrictLevel)) {
					namingStrictLevelCode = 2;
				}
				
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
				
				Object value = extractFieldValue(targetFieldName, sourceBean, namingStrictLevelCode);
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
							
//							Class fieldType = currentField.getType();
//							Class valueType = value.getClass();
//							if(fieldType.equals(Double.class)) {
//								if(valueType.equals(BigInteger.class)) {
//									currentField.set(
//											destBean, 
//											((BigInteger)value).doubleValue());
//								}
//							}
						}
					}
				}
				
				
				
				if(logger.isTraceEnabled()) {
					Object tmp = currentField.get(destBean);
					logger.trace(" finally value of field {" +currentField.getName() + "} = " + tmp);
				}
				
			} catch (Exception e) {
				//IllegalArgumentException, IllegalAccessException 
				throw new RuntimeException(e);
			}
		} 
		
	}
	
	public static ValueTypeCaster getDefaultValueTypeCaster() {
		return defaultValueTypeCaster;
	}
	
	@SuppressWarnings("rawtypes")
	public static void fillBean(String targetCategory, MappingGuideSupported destBean, Object sourceBean) {
		fillBean(targetCategory, destBean, sourceBean, null);
	}
	
	@SuppressWarnings("rawtypes")
	public static void fillBean(String targetCategory, MappingGuideSupported destBean, Object sourceBean, ValueTypeCaster valueTypeCaster) {
		
		if(valueTypeCaster == null) {
			valueTypeCaster = defaultValueTypeCaster;
		}
		Class destClass = destBean.getClass();
		Field fields[] = destClass.getDeclaredFields();
		
		for(int i=0;i<fields.length;i++) {
			
			if(logger.isTraceEnabled()) {
				logger.trace(" process field " + fields[i].getName() + "... ");
			}
			
			fillField(targetCategory, fields[i], destBean, sourceBean, valueTypeCaster);
		}
	}
}