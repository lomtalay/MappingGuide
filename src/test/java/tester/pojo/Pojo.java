package tester.pojo;

import io.github.lomtalay.mappingguide.MappingGuideSupported;
import io.github.lomtalay.mappingguide.annotation.MappingGuide;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Pojo implements MappingGuideSupported {

	@MappingGuide(category="SOAP-05", key="sampleBigInteger")
	private Double sampleDoubleValue;
	
	@MappingGuide(category="SOAP-05", key="sampleBigInteger")
	private double samplePremitiveDouble;
	
	@MappingGuide(category="SOAP-05", key="sampleInteger")
	private int samplePremitiveInt;
	
	@MappingGuide(category="SOAP-05", key="sampleBigDecimal")
	private Double sampleDoubleValue2;

	@MappingGuide(category="SOAP-05", key="sampleBigDecimal")
	private double samplePremitiveDouble2;
}
