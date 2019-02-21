package tester.pojo;

import io.github.lomtalay.mappingguide.MappingGuideSupported;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PojoWithEnum implements MappingGuideSupported {

	private String name;
	private CategoryType category;
	private String remark;
	
	
	//===============================

	public static enum CategoryType {
		NA(null),			
		Cate_A("A"),	
		Cate_B("B"),	
		Cate_C("C");		
		
		private final String name;
		CategoryType(String name) {
			this.name = name;
		}
		@Override
		public String toString() {
			return name;
		}
	}
}
