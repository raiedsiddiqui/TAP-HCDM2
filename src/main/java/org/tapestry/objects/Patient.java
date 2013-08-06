package org.tapestry.objects;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.Years;
import org.joda.time.LocalDate;
import org.joda.time.Period;

public class Patient{
	private int patientID;
	private String firstName;
	private String lastName;
	private String gender;
	private String birthdate;
	private String email;
	private int volunteer;
	private String volunteerName;
	private String color;
	private String warnings;

	/**
	* Empty constructor
	*/
	public Patient(){
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
	*@return The patient's age
	*/
    	public int getAge(){
    		DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-DD");
    		DateTime bd = format.parseDateTime(this.birthdate);
    		int age = new Period(new LocalDate(bd), new LocalDate()).getYears();
    		return age;
    	}

    	public String getBirthdate(){
    		return birthdate;
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
     * @return The background color for this patient
     */
    	public String getColor(){
    		return color;
    	}
    	
    	public String getWarnings(){
    		return warnings;
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
	*@param gender The new gender of the patient
	*/
    	public void setGender(String gender){
        	this.gender = gender;
    	}

	/**
	*@param age The new age of the patient
	*/
    	public void setBirthdate(String birthdate){
        	this.birthdate = birthdate;
    	}

	/**
	*@param email The new email address of the patient
	*/
    	public void setEmail(String email){
        	this.email = email;
    	}

    	public void setVolunteerName(String name){
    		this.volunteerName = name;
    	}
    	
	/**
	*@param volunteer The name of the volunteer assigned to the patient
	*/
    	public void setVolunteer(int volunteer){
        	this.volunteer = volunteer;
    	}
    	
    /**
     * @param color The color to use as the background for the patient's page
     */
    	public void setColor(String color){
    		this.color = color;
    	}
    	
    	public void setWarnings(String warnings){
    		this.warnings = warnings;
    	}
}
