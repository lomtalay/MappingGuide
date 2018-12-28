import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.lomtalay.mappingguide.MappingUtil;
import tester.pojo.SoapResult01;
import tester.pojo.SoapResult02;
import tester.pojo.SoapResult03;
import tester.pojo.SoapResult04;
import tester.pojo.SoapResult05;
import tester.pojo.PersonInfo;
import tester.pojo.Pojo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

public class MappingUitlTest {

	protected Logger logger = LoggerFactory.getLogger(MappingUtil.class);

	@Ignore
	@Test
	public void test_001() {
		
		System.out.println(logger.getClass().getName());
		
		logger.trace("TRACE");
		logger.debug("DEBUG");
		logger.info("INFO");
		logger.warn("WARN");
		logger.error("ERROR");
		
		
		
		PersonInfo person = new PersonInfo();
		
		SoapResult01 dataPart1 = new SoapResult01();
		dataPart1.setFST_NM("lomtalay");
		dataPart1.setSUR_NM("project");
		

		SoapResult02 dataPart2 = new SoapResult02();
		dataPart2.setBirthDate(Date.valueOf("1978-12-14"));
		dataPart2.setProvince("bangkok");
		dataPart2.setCountry("thailand");
		dataPart2.setDonateUrl("bit.ly/2RjjWk3");
		

		SoapResult03 dataPart3 = new SoapResult03();
		dataPart3.setPSN_FST_NM("oldname");
		dataPart3.setPSN_LST_NM("p.sherman");
		dataPart3.setSYS_CDE(19781214);
		dataPart3.setADR_ROAD("42 rock wallaby way");
		dataPart3.setADR_DSCT("blaxland");
		dataPart3.setADR_PRVN("sidney");
		dataPart3.setADR_CTRY("australia");
		dataPart3.setFMLY_SIZE(3);
		

		SoapResult04 dataPart4 = new SoapResult04();
		dataPart4.setFavorite("instrument music, amusement park");
		dataPart4.setHobby("Fostering Animals, playing games");
		

		logger.info("\n\nfillBean  SOAP-01");
		MappingUtil.fillBean("SOAP-01", person, dataPart1);
		logger.info("\n\nfillBean  SOAP-02");
		MappingUtil.fillBean("SOAP-02", person, dataPart2);
		logger.info("\n\nfillBean  SOAP-03");
		MappingUtil.fillBean("SOAP-03", person, dataPart3);
		logger.info("\n\nfillBean  SOAP-04");
		MappingUtil.fillBean("SOAP-04", person, dataPart4);
		
		
		assertThat(person, hasProperty("firstName", equalTo("lomtalay")));
		assertThat(person, hasProperty("lastName", equalTo("project")));
		assertThat(person, hasProperty("country", equalTo("thailand")));
		assertThat(person, hasProperty("birthDate", equalTo(Date.valueOf("1978-12-14"))));
		assertThat(person, hasProperty("donate", equalTo("bit.ly/2RjjWk3")));
		assertThat(person, hasProperty("codeInt", equalTo(19781214)));
		assertThat(person, hasProperty("workAddress", equalTo("42 rock wallaby way")));
		assertThat(person, hasProperty("workDistrict", equalTo("blaxland")));
		assertThat(person, hasProperty("workProvince", equalTo("sidney")));
		assertThat(person, hasProperty("workCountry", equalTo("australia")));
		assertThat(person, hasProperty("familySize", equalTo(3)));
		assertThat(person, hasProperty("extraInfo1", equalTo("instrument music, amusement park")));
		assertThat(person, hasProperty("extraInfo2", equalTo("Fostering Animals, playing games")));
		
		//============
		
		logger.info("\n\n## Mapping bean with some null  ##\n\n");
		
		
		dataPart3 = new SoapResult03();
		dataPart4 = new SoapResult04();
		
		logger.info("\n\nfillBean  SOAP-03 with null");
		MappingUtil.fillBean("SOAP-03", person, dataPart3);
		logger.info("\n\nfillBean  SOAP-04 with null");
		MappingUtil.fillBean("SOAP-04", person, dataPart4);
		
		assertThat(person, hasProperty("workAddress", equalTo("42 rock wallaby way")));
		assertThat(person, hasProperty("workDistrict", equalTo("blaxland")));
		assertThat(person, hasProperty("workProvince", equalTo("sidney")));
		assertThat(person, hasProperty("workCountry", equalTo("australia")));
		assertThat(person, hasProperty("familySize", equalTo(3)));
		assertThat(person, hasProperty("extraInfo1", equalTo(null)));
		assertThat(person, hasProperty("extraInfo2", equalTo(null)));
		
	}
	

	@Test
	public void test_002() {
		
		Pojo pojo = new Pojo();
		
		SoapResult05 dataPart5 = new SoapResult05();
		dataPart5.setSampleBigInteger(new BigInteger("20181228"));
		dataPart5.setSampleInteger(new Integer(28122018));
		dataPart5.setSampleBigDecimal(new BigDecimal("2812.2018"));

		
		logger.info("fillBean  SOAP-05");
		MappingUtil.fillBean("SOAP-05", pojo, dataPart5);
		
		

		assertThat(pojo, hasProperty("sampleDoubleValue", equalTo(new Double("20181228"))));
		assertThat(pojo, hasProperty("samplePremitiveDouble", equalTo(20181228d)));
		assertThat(pojo, hasProperty("samplePremitiveInt", equalTo(28122018)));
		assertThat(pojo, hasProperty("sampleDoubleValue2", equalTo(new Double("2812.2018"))));
		assertThat(pojo, hasProperty("samplePremitiveDouble2", equalTo(2812.2018d)));
		
	}
}
