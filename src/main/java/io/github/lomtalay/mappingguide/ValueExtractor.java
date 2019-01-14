package io.github.lomtalay.mappingguide;

import io.github.lomtalay.mappingguide.annotation.MappingGuide;

public interface ValueExtractor {

	String[] tokenizeMappingKey(String mappingKey);
	Object extractFieldValue(String fieldName, Object sourceObj, MappingGuide.NamingStrictLevel strictLevel, String extraAnnotate) throws IllegalArgumentException, IllegalAccessException;
}
