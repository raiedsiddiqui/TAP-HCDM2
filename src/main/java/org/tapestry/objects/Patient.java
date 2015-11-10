package org.tapestry.objects;

public class Patient{
	private int patientID;
	private String firstName;
	private String lastName;
	private String preferredName;
	private String gender;
	private String email;
	private int volunteer;
	private String volunteerName;
	private String notes;	
	private String bod;// birth of date
	private int age;
	private int clinic;
	private int mrp; // family doctor
	private String mrpFirstName;
	private String mrpLastName;
	private String city;
	private String homePhone;	
	private String alerts;
	private String myoscarVerified;
	private int partner; // another volunteer(two volunteers should be assigned per visit)
	private String partnerName;
	private String myOscarAuthentication;
	private String clinicName;
	private String address;
	private String userName; //registered for MyOscar account
	private int group; //based on volunteer's organization	
	private String streetAddress;
	private String provice;
	private String postalCode;
	private String researchID;
	private boolean enabled;
	private String tapUsername;
	private String tapPassword;
	
	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	/**
	* Empty constructor
	*/
	public Patient(){
	}
	
	public String getMyoscarVerified() {
		return myoscarVerified;
	}

	public void setMyoscarVerified(String myoscarVerified) {
		this.myoscarVerified = myoscarVerified;
	}
	
	public String getAlerts() {
		return alerts;
	}

	public void setAlerts(String alerts) {
		this.alerts = alerts;
	}

	public int getPartner() {
		return partner;
	}

	public void setPartner(int partner) {
		this.partner = partner;
	}
	
	public String getBod() {
		return bod;
	}

	public void setBod(String bod) {
		this.bod = bod;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getClinic() {
		return clinic;
	}

	public void setClinic(int clinic) {
		this.clinic = clinic;
	}

	public int getMrp() {
		return mrp;
	}

	public void setMrp(int mrp) {
		this.mrp = mrp;
	}

	public String getMrpFirstName() {
		return mrpFirstName;
	}

	public void setMrpFirstName(String mrpFirstName) {
		this.mrpFirstName = mrpFirstName;
	}

	public String getMrpLastName() {
		return mrpLastName;
	}

	public void setMrpLastName(String mrpLastName) {
		this.mrpLastName = mrpLastName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}
	
	public String getResearchID() {
		return researchID;
	}

	public void setResearchID(String researchID) {
		this.researchID = researchID;
	}
	
	//Accessors
	/**
	*@return The ID of the patient
	*/
	public int getPatientID(){
		return patientID;
	}

	/**
	*@return The patient's first name
	*/
    public String getFirstName(){
        return firstName;
    }

	/**
	*@return The patient's last name
	*/
    public String getLastName(){
        return lastName;
    }
    
    /**
     * @return The patient's preferred name, defaulting to "First L."
     */
    public String getPreferredName(){
    	return preferredName;
    }

	/**
	*@return The first letter of the patient's last name
	*/
    public String getDisplayName(){
		return firstName + " " + lastName.substring(0, 1) + ".";
    }

	/**
	*@return The patient's gender
	*/
    public String getGender(){
        return gender;
    }

	/**
	*@return The patient's email address
	*/
    public String getEmail(){
        return email;
    }
    	
    /**
     * This should really only be used for displaying info in the interface,
     * use the volunteer (integer) version for referencing the user assigned
     * to the patient (since names are mutable and IDs are not)
     * @return The name of the volunteer responsible for the patient
     */
    public String getVolunteerName(){
    	return volunteerName;
    }

	/**
	*@return The name of the volunteer assigned to the patient
	*/
    public int getVolunteer(){
        return volunteer;
    }
    
    /**
     * @return The notes for the patient
     */
    public String getNotes(){
    	return notes;
    }

	//Mutators
	/**
	*@param id The new ID of the patient
	*/
    public void setPatientID(int id){
        this.patientID = id;
    }

	/**
	*@param firstName The new first name of the patient
	*/
    public void setFirstName(String firstName){
        this.firstName = firstName;
   	}

	/**
	*@param lastName The new last name of the patient
	*/
    public void setLastName(String lastName){
        this.lastName = lastName;
    }
    
    /**
     * @param preferredName The new preferred name of the patient
     */
    public void setPreferredName(String preferredName){
    	this.preferredName = preferredName;
    }

	/**
	*@param gender The new gender of the patient
	*/
    public void setGender(String gender){
        this.gender = gender;
    }

	/**
	*@param email The new email address of the patient
	*/
    public void setEmail(String email){
        this.email = email;
    }

    /**
     * @param name The name of the volunteer assigned to the patient
     */
    public void setVolunteerName(String name){
    	this.volunteerName = name;
    }
    	
	/**
	*@param volunteer The id of the volunteer assigned to the patient
	*/
    public void setVolunteer(int volunteer){
        this.volunteer = volunteer;
    }
    
    /**	
     * @param warnings The notes for the patient
     */
    public void setNotes(String notes){
    	this.notes = notes;
    }
    
    public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}
	
	public String getMyOscarAuthentication() {
		return myOscarAuthentication;
	}

	public void setMyOscarAuthentication(String myOscarAuthentication) {
		this.myOscarAuthentication = myOscarAuthentication;
	}
    
	public String getClinicName() {
		return clinicName;
	}

	public void setClinicName(String clinicName) {
		this.clinicName = clinicName;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getProvice() {
		return provice;
	}

	public void setProvice(String provice) {
		this.provice = provice;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	/**
	*@return enabled Whether the patient's account is active
	*/
	public boolean isEnabled(){
		return enabled;
	}
	
	/**
	* @param enabled The new status for the patient
	*/
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}

	public String getTapUsername() {
		return tapUsername;
	}

	public void setTapUsername(String tapUsername) {
		this.tapUsername = tapUsername;
	}

	public String getTapPassword() {
		return tapPassword;
	}

	public void setTapPassword(String tapPassword) {
		this.tapPassword = tapPassword;
	}
	
	
}
