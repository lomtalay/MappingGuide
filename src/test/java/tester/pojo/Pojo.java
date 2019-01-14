package tester.pojo;

import io.github.lomtalay.mappingguide.MappingGuideSupported;
import io.github.lomtalay.mappingguide.annotation.MappingGuide;
import lombok.Getter;
import lombok.Setter;
import tester.ext.ValueTypeCasterTesterImpl;

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
	
	
	@MappingGuide(category="TEST_005", key="prop1")
	private String testMerge01;
	@MappingGuide(category="TEST_005", key="prop2 soap02.country soap02.province")
	private String testMerge02;
	@MappingGuide(category="TEST_005", key="prop2:soap02.soap03.ADR_ROAD:soap02.soap03.ADR_DSCT:soap02.soap03.ADR_PRVN:soap02.soap03.ADR_CTRY")
	private String testMerge03;
	@MappingGuide(category="TEST_005", key="{prop1}:{prop2}", typecaster=ValueTypeCasterTesterImpl.class)
	private String testMerge04;
}
