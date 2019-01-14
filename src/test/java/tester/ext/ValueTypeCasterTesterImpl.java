package tester.ext;

import java.util.Map;

import io.github.lomtalay.mappingguide.ValueTypeCasterDefaultImpl;

public class ValueTypeCasterTesterImpl extends ValueTypeCasterDefaultImpl {
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object merge(Map<String, Object> sourceValues, String untokenizedKey, Class targetClass) {

		Object result = null;
		if(targetClass.equals(String.class)) {
			
			String valResult = untokenizedKey;
			
			for(String key : sourceValues.keySet()) {
				Object val = sourceValues.get(key);
				String strVal = "";
				if(val != null) {
					strVal = String.valueOf(sourceValues.get(key));
					
				}
				
				valResult = valResult.replaceAll("[{]"+key+"[}]", strVal);
			}
			
			result = valResult.trim();
		} else {
			logger.trace("Unsupport to merge to <"+targetClass.getName()+">");
		}
		
		if(logger.isTraceEnabled()) {
			logger.trace("Merge result is " + result + ((result!=null)?"<"+result.getClass().getName()+">":""));
		}
		
		return result;
	}
}
