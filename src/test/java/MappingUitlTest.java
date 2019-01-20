import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import io.github.lomtalay.logger.LocalLogger;
import io.github.lomtalay.mappingguide.MappingUtil;
import tester.pojo.SoapResult01;
import tester.pojo.SoapResult02;
import tester.pojo.SoapResult03;
import tester.pojo.SoapResult04;
import tester.pojo.SoapResult05;
import tester.pojo.SoapResult06;
import tester.pojo.PersonInfo;
import tester.pojo.Pojo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public class MappingUitlTest {

	protected LocalLogger logger = LocalLogger.getLogger(MappingUtil.class);
	
	private PersonInfo person1 = new PersonInfo();
	
	@BeforeClass
	public static void beforeClassJul() {

		LogManager logManager = LogManager.getLogManager();
		Logger LOGGER = Logger.getLogger("TesterLogger");


		try {
			logManager.readConfiguration(ClassLoader.getSystemResourceAsStream("simplelogger.properties"));
		} catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Error in loading configuration",exception);
		}
		
		LocalLogger localLog = LocalLogger.getLogger(MappingUtil.class);
		
		LOGGER.log(Level.INFO, "MappingUtil.logger.level = " + localLog.getLevel());
	}

	
	
	@Ignore
	@Test
	public void test_001() {
		
		System.out.println(logger.getClass().getName());
		
		logger.trace("TRACE");
		logger.debug("DEBUG");
		logger.info("INFO");
		logger.warn("WARN");
		logger.error("ERROR");
		
		
		
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
		MappingUtil.fillBean("SOAP-01", person1, dataPart1);
		logger.info("\n\nfillBean  SOAP-02");
		MappingUtil.fillBean("SOAP-02", person1, dataPart2);
		logger.info("\n\nfillBean  SOAP-03");
		MappingUtil.fillBean("SOAP-03", person1, dataPart3);
		logger.info("\n\nfillBean  SOAP-04");
		MappingUtil.fillBean("SOAP-04", person1, dataPart4);
		
		
		assertThat(person1, hasProperty("firstName", equalTo("lomtalay")));
		assertThat(person1, hasProperty("lastName", equalTo("project")));
		assertThat(person1, hasProperty("country", equalTo("thailand")));
		assertThat(person1, hasProperty("birthDate", equalTo(Date.valueOf("1978-12-14"))));
		assertThat(person1, hasProperty("donate", equalTo("bit.ly/2RjjWk3")));
		assertThat(person1, hasProperty("codeInt", equalTo(19781214)));
		assertThat(person1, hasProperty("workAddress", equalTo("42 rock wallaby way")));
		assertThat(person1, hasProperty("workDistrict", equalTo("blaxland")));
		assertThat(person1, hasProperty("workProvince", equalTo("sidney")));
		assertThat(person1, hasProperty("workCountry", equalTo("australia")));
		assertThat(person1, hasProperty("familySize", equalTo(3)));
		assertThat(person1, hasProperty("extraInfo1", equalTo("instrument music, amusement park")));
		assertThat(person1, hasProperty("extraInfo2", equalTo("Fostering Animals, playing games")));
		
		//============
		
		logger.info("\n\n## Mapping bean with some null  ##\n\n");
		
		
		dataPart3 = new SoapResult03();
		dataPart4 = new SoapResult04();
		
		logger.info("\n\nfillBean  SOAP-03 with null");
		MappingUtil.fillBean("SOAP-03", person1, dataPart3);
		logger.info("\n\nfillBean  SOAP-04 with null");
		MappingUtil.fillBean("SOAP-04", person1, dataPart4);
		

		assertThat(person1, hasProperty("codeInt", equalTo(0)));
		assertThat(person1, hasProperty("workAddress", equalTo("42 rock wallaby way")));
		assertThat(person1, hasProperty("workDistrict", equalTo("blaxland")));
		assertThat(person1, hasProperty("workProvince", equalTo("sidney")));
		assertThat(person1, hasProperty("workCountry", equalTo("australia")));
		assertThat(person1, hasProperty("familySize", equalTo(3)));
		assertThat(person1, hasProperty("extraInfo1", equalTo(null)));
		assertThat(person1, hasProperty("extraInfo2", equalTo(null)));
		
	}
	

	@Ignore
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
	
	@Ignore
	@Test
	public void test_003() {
		
		test_001();
				

		PersonInfo person2 = new PersonInfo();
		
		logger.info("fillBean  persion1 to person2");
		MappingUtil.fillBean(null, person2, person1);


		assertThat(person2, hasProperty("firstName", equalTo("lomtalay")));
		assertThat(person2, hasProperty("lastName", equalTo("project")));
		assertThat(person2, hasProperty("country", equalTo("thailand")));
		assertThat(person2, hasProperty("birthDate", equalTo(Date.valueOf("1978-12-14"))));
		assertThat(person2, hasProperty("donate", equalTo("bit.ly/2RjjWk3")));
		assertThat(person2, hasProperty("codeInt", equalTo(0)));
		assertThat(person2, hasProperty("workAddress", equalTo("42 rock wallaby way")));
		assertThat(person2, hasProperty("workDistrict", equalTo("blaxland")));
		assertThat(person2, hasProperty("workProvince", equalTo("sidney")));
		assertThat(person2, hasProperty("workCountry", equalTo("australia")));
		assertThat(person2, hasProperty("familySize", equalTo(3)));
		assertThat(person2, hasProperty("extraInfo1", equalTo(null)));
		assertThat(person2, hasProperty("extraInfo2", equalTo(null)));
	}
	
	@Test
	public void test_004() {
		
		
		PersonInfo person = new PersonInfo();
		SoapResult06 soap06 = new SoapResult06();
		
		soap06.setAddress1("p.sherman");
		soap06.setAddress2("42 valabyway");
		soap06.setAddress3("sidney");
		
		logger.info("fillBean  SoapResult06 to persion");
		MappingUtil.fillBean("SOAP-06", person, soap06);

		assertThat(person, hasProperty("extraInfo6x1", equalTo("{p.sherman} {42 valabyway} {sidney}")));
		assertThat(person, hasProperty("extraInfo6x2", equalTo("p.sherman 42 valabyway sidney")));
		
		assertThat(person, hasProperty("extraInfo6x3", equalTo("p.sherman")));

		assertThat(person, hasProperty("extraInfo6x4", equalTo("{} {}")));
		assertThat(person, hasProperty("extraInfo6x5", equalTo("")));
		
		

		logger.info("fillBean  SoapResult06 to persion  using category as [SOAP-06-ERR1]");
		try {
			MappingUtil.fillBean("SOAP-06-ERR1", person, soap06);
		} catch(Exception e) {
			
			assertThat("Got UnsupportedOperationException when try annotate to merge to int", UnsupportedOperationException.class.equals(e.getCause().getClass()));
		}
	}
	

	
	@Test
	public void test_005() {
		
		Pojo pojo = new Pojo();
		SoapResult01 soap01 = new SoapResult01();
		SoapResult02 soap02 = new SoapResult02();
		SoapResult03 soap03 = new SoapResult03();
		SoapResult04 soap04 = new SoapResult04();
		
		soap01.setProp1("what does the fox say");
		soap01.setProp2(2019);
		soap01.setSoap02(soap02);
		
		soap02.setProvince("ALHUMBRA");
		soap02.setCountry("SPAIN"); 
		soap02.setSoap03(soap03);
		
		soap03.setADR_CTRY("Australia");
		soap03.setADR_PRVN("Sidney");
		soap03.setADR_DSCT("Blaxland");
		soap03.setADR_ROAD("42 rock wallaby way");
		
		
		logger.info("fillBean  soap01 to persion  using category as [TEST_005]");
		MappingUtil.fillBean("TEST_005", pojo, soap01);

		assertThat(pojo, hasProperty("testMerge01", equalTo("what does the fox say")));
		assertThat(pojo, hasProperty("testMerge02", equalTo("2019 SPAIN ALHUMBRA")));
		assertThat(pojo, hasProperty("testMerge03", equalTo("2019:42 rock wallaby way:Blaxland:Sidney:Australia")));
	}

	
	@Test
	public void test_006() {
		
		Pojo pojo = new Pojo();
		Map soap01 = new HashMap();
		Map soap02 = new HashMap();
		Map soap03 = new HashMap();
		
		
		soap01.put("prop1", "what does the fox say");
		soap01.put("prop2", 2019);
		soap01.put("soap02", soap02);
		
		soap02.put("province", "ALHUMBRA");
		soap02.put("country", "SPAIN"); 
		soap02.put("soap03", soap03);
		
		soap03.put("ADR_CTRY", "Australia");
		soap03.put("ADR_PRVN", "Sidney");
		soap03.put("ADR_DSCT", "Blaxland");
		soap03.put("ADR_ROAD", "42 rock wallaby way");
		
		
		logger.info("fillBean  soap01 to persion  using category as [TEST_005]");
		MappingUtil.fillBean("TEST_005", pojo, soap01);

		assertThat(pojo, hasProperty("testMerge01", equalTo("what does the fox say")));
		assertThat(pojo, hasProperty("testMerge02", equalTo("2019 SPAIN ALHUMBRA")));
		assertThat(pojo, hasProperty("testMerge03", equalTo("2019:42 rock wallaby way:Blaxland:Sidney:Australia")));
	}
	
}
