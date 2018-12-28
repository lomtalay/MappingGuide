package io.github.lomtalay.mappingguide.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MappingGuide {

	public static enum FillCondition {
		REPLACE_ALWAY, 
		ONLY_IF_NULL,			//Prevent to fill value to existing value.
		SKIP_NULL_REPLACEMENT	//Prevent to fill null to existing value. 
	}
	
	public static enum NamingStrictLevel {
		STRICT,
		IGNORE_CASE,
		IGNORE_CASE_AND_UNDERSCORE
	}
	
	public String category() default "";
	public String key() default "";
	public NamingStrictLevel namingStrict() default NamingStrictLevel.IGNORE_CASE_AND_UNDERSCORE;
	public FillCondition condition() default FillCondition.REPLACE_ALWAY;
}
