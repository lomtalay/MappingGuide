package tester.pojo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

import java.sql.Date;

import io.github.lomtalay.mappingguide.MappingGuideSupported;
import io.github.lomtalay.mappingguide.annotation.MappingGuide;
import io.github.lomtalay.mappingguide.annotation.MappingGuide.FillCondition;
import io.github.lomtalay.mappingguide.annotation.MappingGuides;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PersonInfo implements MappingGuideSupported {


	private String id;
	@MappingGuides({
		@MappingGuide(category="MAIN_SECTION", key="firstName"),
		@MappingGuide(category="SOAP-01", key="FST_NM"),
		@MappingGuide(category="SOAP-03", key="PSN_FST_NM", condition=FillCondition.ONLY_IF_NULL)
	})
	private String firstName;

	@MappingGuides({
		@MappingGuide(category="MAIN_SECTION", key="lastName"),
		@MappingGuide(category="SOAP-01", key="SUR_NM"),
		@MappingGuide(category="SOAP-03", key="PSN_LST_NM", condition=FillCondition.ONLY_IF_NULL)
	})
	private String lastName;

	@MappingGuide(category="SOAP-02", key="country")
	private String country;
	@MappingGuide(category="SOAP-02", key="birthDate")
	private Date birthDate;
	@MappingGuide(category="SOAP-02", key="donateUrl")
	private String donate;
	
	@MappingGuide(category="SOAP-03", key="SYS_CDE")
	private int codeInt;
	@MappingGuide(category="SOAP-03", key="ADR_ROAD", condition=FillCondition.SKIP_NULL_REPLACEMENT)
	private String workAddress;
	@MappingGuide(category="SOAP-03", key="ADR_CTRY", condition=FillCondition.SKIP_NULL_REPLACEMENT)
	private String workCountry;
	@MappingGuide(category="SOAP-03", key="ADR_PRVN", condition=FillCondition.SKIP_NULL_REPLACEMENT)
	private String workProvince;
	@MappingGuide(category="SOAP-03", key="ADR_DSCT", condition=FillCondition.SKIP_NULL_REPLACEMENT)
	private String workDistrict;
	@MappingGuide(category="SOAP-03", key="FMLY_SIZE", condition=FillCondition.ONLY_IF_NULL)
	private Integer familySize;
	
	
	@MappingGuide(category="SOAP-04", key="favorite")
	private String extraInfo1;
	@MappingGuide(category="SOAP-04", key="hobby")
	private String extraInfo2;
	

	
	
	
//	assertThat(person, hasProperty("firstName", equalTo("lomtalay")));
//	assertThat(person, hasProperty("lastName", equalTo("project")));
//	assertThat(person, hasProperty("country", equalTo("thailand")));
//	assertThat(person, hasProperty("birthDate", equalTo(Date.valueOf("1978-12-14"))));
//	assertThat(person, hasProperty("donate", equalTo("bit.ly/2RjjWk3")));
//	assertThat(person, hasProperty("codeInt", equalTo(19781214)));
}
