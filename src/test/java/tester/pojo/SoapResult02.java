package tester.pojo;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SoapResult02 {

	private String province;
	private String country;
	private Date birthDate;
	private String donateUrl; 

	private SoapResult03 soap03;
	private SoapResult04 soap04_arr[];
	private List<SoapResult04> soap04_lst;
}
