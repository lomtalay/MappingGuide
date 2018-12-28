package io.github.lomtalay.mappingguide;

public interface ValueTypeCaster {
	public Object cast(Object source, Class targetClass);
}
