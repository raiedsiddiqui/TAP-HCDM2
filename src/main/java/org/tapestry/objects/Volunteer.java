package org.tapestry.objects;


public class Volunteer {
	private int volunteerId;
	private String firstName;
	private String lastName;
	private String password;	
	private String preferredName;
	private String displayName;	
	private String streetNumber;	
	private String gender;
	private String email;
	private String experienceLevel;
	private String city;
	private String province;
	private String homePhone;
	private String cellPhone;
	private String userName;
	private String street;
	private String aptNumber;
	private String country;
	private String emergencyContact;
	private String emergencyPhone;
	private String postalCode;
	private String notes;
	private String availability;	
	private String address;
	private String organization;
	private int organizationId;
	private double totalVLCScore;
	private double numYearsOfExperience;
	private double availabilityPerMonth;
	private double technologySkillsScore;
	private double perceptionOfOlderAdultsScore;
	private boolean showDelete;
	private int vLCID;
	private String interviewDate;
	private String dOB;
	private String status; //Mature or Student
	private String reference1;
	private String reference2;
	private String referenceCheckDate;
	private String policeCheckDate;
	private String tBTestDate;
	private String vLCCompletionDate;
	private String cAgreementDate;
	private String vAgreementDate;	
	private String photoDate;
	private String source;
	private String applicationFormCompletionDate;

	public Volunteer(){
		
	}
	
	public int getVolunteerId() {
		return volunteerId;
	}
	
	public void setVolunteerId(int volunteerId) {
		this.volunteerId = volunteerId;
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
	
	public String getPreferredName() {
		return preferredName;
	}
	
	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String name) {
		this.displayName = name;
	}
	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getExperienceLevel() {
		return experienceLevel;
	}
	
	public void setExperienceLevel(String experienceLevel) {
		this.experienceLevel = experienceLevel;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getProvince() {
		return province;
	}
	
	public void setProvince(String province) {
		this.province = province;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getAptNumber() {
		return aptNumber;
	}

	public void setAptNumber(String aptNumber) {
		this.aptNumber = aptNumber;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmergencyContact() {
		return emergencyContact;
	}

	public void setEmergencyContact(String emergencyContact) {
		this.emergencyContact = emergencyContact;
	}

	public String getEmergencyPhone() {
		return emergencyPhone;
	}

	public void setEmergencyPhone(String emergencyPhone) {
		this.emergencyPhone = emergencyPhone;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}	
	
	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public int getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(int organizationId) {
		this.organizationId = organizationId;
	}
	public double getTotalVLCScore() {
		return totalVLCScore;
	}

	public void setTotalVLCScore(double totalVLCScore) {
		this.totalVLCScore = totalVLCScore;
	}

	public double getNumYearsOfExperience() {
		return numYearsOfExperience;
	}

	public void setNumYearsOfExperience(double numYearsOfExperience) {
		this.numYearsOfExperience = numYearsOfExperience;
	}

	public double getAvailabilityPerMonth() {
		return availabilityPerMonth;
	}

	public void setAvailabilityPerMonth(double availabilityPerMonth) {
		this.availabilityPerMonth = availabilityPerMonth;
	}

	public double getTechnologySkillsScore() {
		return technologySkillsScore;
	}

	public void setTechnologySkillsScore(double technologySkillsScore) {
		this.technologySkillsScore = technologySkillsScore;
	}

	public double getPerceptionOfOlderAdultsScore() {
		return perceptionOfOlderAdultsScore;
	}

	public void setPerceptionOfOlderAdultsScore(double perceptionOfOlderAdultsScore) {
		this.perceptionOfOlderAdultsScore = perceptionOfOlderAdultsScore;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isShowDelete() {
		return showDelete;
	}

	public void setShowDelete(boolean showDelete) {
		this.showDelete = showDelete;
	}

	public int getvLCID() {
		return vLCID;
	}

	public void setvLCID(int vLCID) {
		this.vLCID = vLCID;
	}
	
	public String getInterviewDate() {
		return interviewDate;
	}

	public void setInterviewDate(String interviewDate) {
		this.interviewDate = interviewDate;
	}

	public String getdOB() {
		return dOB;
	}

	public void setdOB(String dOB) {
		this.dOB = dOB;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReference1() {
		return reference1;
	}

	public void setReference1(String reference1) {
		this.reference1 = reference1;
	}

	public String getReference2() {
		return reference2;
	}

	public void setReference2(String reference2) {
		this.reference2 = reference2;
	}

	public String getReferenceCheckDate() {
		return referenceCheckDate;
	}

	public void setReferenceCheckDate(String referenceCheckDate) {
		this.referenceCheckDate = referenceCheckDate;
	}

	public String getPoliceCheckDate() {
		return policeCheckDate;
	}

	public void setPoliceCheckDate(String policeCheckDate) {
		this.policeCheckDate = policeCheckDate;
	}

	public String gettBTestDate() {
		return tBTestDate;
	}

	public void settBTestDate(String tBTestDate) {
		this.tBTestDate = tBTestDate;
	}

	public String getvLCCompletionDate() {
		return vLCCompletionDate;
	}

	public void setvLCCompletionDate(String vLCCompletionDate) {
		this.vLCCompletionDate = vLCCompletionDate;
	}

	public String getcAgreementDate() {
		return cAgreementDate;
	}

	public void setcAgreementDate(String cAgreementDate) {
		this.cAgreementDate = cAgreementDate;
	}

	public String getvAgreementDate() {
		return vAgreementDate;
	}

	public void setvAgreementDate(String vAgreementDate) {
		this.vAgreementDate = vAgreementDate;
	}

	public String getPhotoDate() {
		return photoDate;
	}

	public void setPhotoDate(String photoDate) {
		this.photoDate = photoDate;
	}

	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getApplicationFormCompletionDate() {
		return applicationFormCompletionDate;
	}

	public void setApplicationFormCompletionDate(
			String applicationFormCompletionDate) {
		this.applicationFormCompletionDate = applicationFormCompletionDate;
	}

	
}
