package tester.pojo;

import java.math.BigInteger;

import io.github.lomtalay.mappingguide.annotation.MappingGuide;
import io.github.lomtalay.mappingguide.annotation.MappingGuides;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SoapResult01 {
	
	private String FST_NM;
	private String SUR_NM;
	private String prop1;
	
	private int prop2;
	private BigInteger prop3; 
}
