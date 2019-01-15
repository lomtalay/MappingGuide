package io.github.lomtalay.mappingguide;

import java.lang.reflect.Field;

import io.github.lomtalay.mappingguide.annotation.MappingGuide;

public interface AnnotationBorrower {
	MappingGuide getSupportedAnnotation(String currentCategory, Field field);
}
