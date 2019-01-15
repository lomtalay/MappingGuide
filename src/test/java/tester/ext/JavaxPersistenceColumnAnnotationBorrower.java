package tester.ext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.persistence.Column;

import io.github.lomtalay.mappingguide.AnnotationBorrower;
import io.github.lomtalay.mappingguide.annotation.MappingGuide;

public class JavaxPersistenceColumnAnnotationBorrower implements AnnotationBorrower {
	
	public MappingGuide getSupportedAnnotation(String currentMappingCategory, Field targetField) {
		
		Annotation annotations[] = targetField.getDeclaredAnnotations();
		
		for(int i=0;i<annotations.length;i++) {
			if(annotations[i] instanceof Column) {
				return castToMappingGuide((Column)annotations[i], currentMappingCategory);
			}
		}
		return null;
	}

	private MappingGuide castToMappingGuide(final Column annotation, final String currentCategory) {
		
		
		return new MappingGuide() {

			public Class<? extends Annotation> annotationType() {
				return this.getClass();
			}

			public String category() {
				return currentCategory;
			}

			public String key() {
				return annotation.name();
			}

			public NamingStrictLevel namingStrict() {
				return NamingStrictLevel.STRICT;
			}

			public FillCondition condition() {
				return FillCondition.REPLACE_ALWAY;
			}

			public Class typecaster() {
				//use default
				return null;
			}

			public Class valueExtractor() {
				//use default
				return null;
			}

			public String extra() {
				//use default
				return null;
			}
			
		};

	}

}
