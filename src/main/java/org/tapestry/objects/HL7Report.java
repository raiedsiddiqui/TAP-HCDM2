package org.tapestry.objects;

import java.util.List;

import org.tapestry.report.ScoresInReport;

public class HL7Report {

	private List<String> alerts; //alert:consider case review with IP-team
	private List<String> dailyActivities;//tapestry questions
	private List<String> volunteerInformations;//volunteer information and notes
	private List<String> additionalInfos;//additional information
	private Patient patient;//patient information,name...
	private Appointment appointment;//visit,plan, key observation
	private ScoresInReport scores;//summary of tapestry tools
	private List<String> patientGoals; // patient goal(s)
	private User user; //order provider
	private List<String> healthGoals;
	private List<String> lifeGoals;

	public HL7Report(){}

	public List<String> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<String> alerts) {
		this.alerts = alerts;
	}

	public List<String> getDailyActivities() {
		return dailyActivities;
	}

	public void setDailyActivities(List<String> dailyActivities) {
		this.dailyActivities = dailyActivities;
	}

	public List<String> getVolunteerInformations() {
		return volunteerInformations;
	}

	public void setVolunteerInformations(List<String> volunteerInformations) {
		this.volunteerInformations = volunteerInformations;
	}

	public List<String> getAdditionalInfos() {
		return additionalInfos;
	}

	public void setAdditionalInfos(List<String> additionalInfos) {
		this.additionalInfos = additionalInfos;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Appointment getAppointment() {
		return appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

	public ScoresInReport getScores() {
		return scores;
	}

	public void setScores(ScoresInReport scores) {
		this.scores = scores;
	}

	public List<String> getPatientGoals() {
		return patientGoals;
	}

	public void setPatientGoals(List<String> patientGoals) {
		this.patientGoals = patientGoals;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public List<String> getHealthGoals() {
		return healthGoals;
	}


	public void setHealthGoals(List<String> healthGoals) {
		this.healthGoals = healthGoals;
	}


	public List<String> getLifeGoals() {
		return lifeGoals;
	}


	public void setLifeGoals(List<String> lifeGoals) {
		this.lifeGoals = lifeGoals;
	}
	
}
