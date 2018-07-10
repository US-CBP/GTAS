package gov.gtas.rest.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Passenger extends BaseModel {

	private Long id;
	private String firstName;
	private String lastName;
	private String title;
	private String suffix;
	private Date dob;
	private String gender;
	private Integer age;
	private  String citizenshipCountry;
	private String residencyCountry;
	private List<PassengerTravelDetail> travelDetail;
	private List<Document> documents;
	private List<HitsSummary> hitsSummary;
	private List<Cases> cases;

	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getCitizenshipCountry() {
		return citizenshipCountry;
	}
	public void setCitizenshipCountry(String citizenshipCountry) {
		this.citizenshipCountry = citizenshipCountry;
	}
	public String getResidencyCountry() {
		return residencyCountry;
	}
	public void setResidencyCountry(String residencyCountry) {
		this.residencyCountry = residencyCountry;
	}
	public List<Document> getDocuments() {
		return documents;
	}
	
	
	public List<PassengerTravelDetail> getTravelDetail() {
		return travelDetail;
	}
	public void setTravelDetail(List<PassengerTravelDetail> travelDetail) {
		this.travelDetail = travelDetail;
	}
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	public List<HitsSummary> getHitsSummary() {
		return hitsSummary;
	}
	public void setHitsSummary(List<HitsSummary> hitsSummary) {
		this.hitsSummary = hitsSummary;
	}
	public List<Cases> getCases() {
		return cases;
	}
	public void setCases(List<Cases> cases) {
		this.cases = cases;
	}

	
	
	
	
	
	
	
	
	
	
	
	
}
