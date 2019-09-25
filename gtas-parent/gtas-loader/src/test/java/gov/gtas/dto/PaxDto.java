/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.dto;

public class PaxDto {
	private String firstName;
	private String lastName;
	private String middleName;
	private String embark;
	private String debark;
	private String dob;
	private int docNumber;
	private String gender;
	private FlightDto flight;
	private int id;
	private int age;

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public FlightDto getFlight() {
		return flight;
	}

	public void setFlight(FlightDto flight) {
		this.flight = flight;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmbark() {
		return embark;
	}

	public void setEmbark(String embark) {
		this.embark = embark;
	}

	public String getDebark() {
		return debark;
	}

	public void setDebark(String debark) {
		this.debark = debark;
	}

	public int getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(int docNumber) {
		this.docNumber = docNumber;
	}

}
