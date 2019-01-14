package io.github.lomtalay.mappingguide;

import java.util.Map;

public interface ValueTypeCaster {
	public Object cast(Object source, Class targetClass);
	public Object merge(Map<String, Object> sourceValues, String untokenizedKey, Class targetClass);
	public boolean isSupportMerge(Class targetClass);
}
