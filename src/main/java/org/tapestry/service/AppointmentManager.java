package org.tapestry.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tapestry.objects.Appointment;

/**
 * service for Model Appointment
 * @author lxie *
 */
@Service
public interface AppointmentManager {
	/**
     * @return all appointments in a list
     */
	@Transactional
    public List<Appointment> getAllAppointments();
	
	/**
     * @return all Upcoming appointments in a list
     */
	@Transactional
    public List<Appointment> getAllUpcomingAppointments();
	
	
	/**
     * @return all requested volunteer's appointments in a list 
     */
	@Transactional
	public List<Appointment> getAllAppointmentsByVolunteer(int id);
    
    /**
     * @return all past appointments in a list
     */
	@Transactional
    public List<Appointment> getAllPastAppointments();
    
    /**
     * all awaiting approved appointments 
     * @param patientId
     * @return a List of Appointment object
     */
	@Transactional
    public List<Appointment> getAllPendingAppointments();   
    
    /**
     * get all approved and not completed appointments filtered by volunteer
     * @param volunteerId
     * @return a List of Appointment object
     */    
	@Transactional
    public List<Appointment> getAllApprovedAppointmentsForVolunteer(int volunteerId);    
    
    /**
     * all awaiting approve appointments filtered by volunteer
     * @param volunteerId
     * @return a List of Appointment object
     */
	@Transactional
    public List<Appointment> getAllPendingAppointmentsForVolunteer(int volunteerId);
    
    /**
     * all declined appointments filtered by volunteer
     * @param volunteerId
     * @return a List of Appointment object
     */   
	@Transactional
    public List<Appointment> getAllDeclinedAppointmentsForVolunteer(int volunteerId);
    
    /**
     * all completed appointments filtered by volunteer
     * @param volunteerId
     * @return a List of Appointment object
     */ 
	@Transactional
    public List<Appointment> getAllCompletedAppointmentsForVolunteer(int volunteerId);
    
    /**
     * all uncompleted appointments filtered by volunteer
     * @param volunteerId
     * @return a List of Appointment object
     */ 
	@Transactional
    public List<Appointment> getAllUpcomingAppointmentsForVolunteer(int volunteerId);

    /**
     * all approved appointments filtered by patient
     * @param patientId
     * @return a List of Appointment object
     */  
	@Transactional
    public List<Appointment> getAllApprovedAppointmentsForPatient(int patientId);
    
    /**
     * all approved appointments filtered by patient, volunteer
     * @param patientId
     * @return a List of Appointment object
     */ 
	@Transactional
    public List<Appointment> getAllApprovedAppointmentsForPatient(int patientId, int volunteerId);
    
    /**
     * all awaiting approved appointments filtered by volunteer
     * @param patientId
     * @return a List of Appointment object
     */
	@Transactional
    public List<Appointment> getAllPendingAppointmentsForPatient(int patientId);
    
    /**
     * all awaiting approved appointments filtered by volunteer, patient
     * @param patientId
     * @return a List of Appointment object
     */
	@Transactional
    public List<Appointment> getAllPendingAppointmentsForPatient(int patientId, int volunteerId);
    
    /**
     * all declined appointments filtered by patient
     * @param patientId
     * @return a List of Appointment object
     */
	@Transactional
    public List<Appointment> getAllDeclinedAppointmentsForPatient(int patientId);
    
    /**
     * all declined appointments filtered by patient, volunteer
     * @param patientId
     * @return a List of Appointment object
     */
	@Transactional
    public List<Appointment> getAllDeclinedAppointmentsForPatient(int patientId, int volunteerId);
    
    /**
     * all approved and not completed appointments filtered by patient
     * @param patientId
     * @return a List of Appointment object
     */
	@Transactional
    public List<Appointment> getAllUpcommingAppointmentForPatient(int patientId);
    
    /**
     * get all completed appointment filtered by patient
     * @param patientId
     * @return a List of Appointment object
     */
	@Transactional
    public List<Appointment> getAllCompletedAppointmentsForPatient(int patientId);
	
	/**
     * get all uncompleted appointment filtered by patient
     * @param patientId
     * @return a List of Appointment object
     */
	@Transactional
	public List<Appointment> getAllUnCompletedAppointmentsForPatient(int patientId);
    
    /**
     * recent approved, not completed appointments filtered by patient
     * @param patientId
     * @return an Object of Appointment
     */
	@Transactional
    public Appointment getAppointmentByMostRecentIncomplete(int patientId);
    
    /**     
     * @param patientId
     * @return an Object of Appointment
     */
	@Transactional
    public Appointment getAppointmentById(int appointmentId);
    
    /**
     * 
     * @param role admin = 0
     * @param diff -2, two days later than now
     * @return
     */
	@Transactional
    public List<Appointment> getRemindingAppointmentList(int id, int diff);
	
	/**
	 * Group appointment by volunteer's organization
	 * @param organizationId
	 * @return
	 */
	@Transactional
	public List<Appointment> getAppointmentsGroupByOrganization(int organizationId);

	/**
	 * Group Upcoming appointment by volunteer's organization
	 * @param organizationId
	 * @return
	 */
	@Transactional
	public List<Appointment> getUpcomingAppointmentsGroupByOrganization(int organizationId);

	
	
	/**
	 * Group past appointment by volunteer's organization
	 * @param organizationId
	 * @return
	 */
	@Transactional
	public List<Appointment> getPastAppointmentsGroupByOrganization(int organizationId);
	
	/**
	 * Group pending appointment by volunteer's organization
	 * @param organizationId
	 * @return
	 */
	@Transactional
	public List<Appointment> getPendingAppointmentsGroupByOrganization(int organizationId);
	
	/**
     * Set an appointment status as completed 
     * @param id appointmentId
     */
	@Transactional
	public void completeAppointment(int id);
	
    /**
     * Set an appointment status as completed and add comments, set if contacted admin
     * @param id appointmentId
     * @param comments
     * @param contacedAdmin    
     */
	@Transactional
    public void completeAppointment(int id, String comments, boolean contactedAdmin);
    
    /**
     * Set an appointment status as completed and add comments 
     * @param id appointmentId
     * @param comments 
     */
	@Transactional
    public void completeAppointment(int appointmentId, String comments);
    
    /**
     * Create an appointment in database
     * @param appointment   
     */
	@Transactional
    public boolean createAppointment(Appointment a);
    
    /**
     * Set an appointment's status as approved
     * @param id appointmentId
     */
	@Transactional
    public void approveAppointment(int id);
    
    /**
     * Set an appointment's status as decline
     * @param id appointmentId
     */
	@Transactional
    public void declineAppointment(int id);
    
    /**
     * Delete an appointment from database
     * @param id appointmentId
     */
	@Transactional
    public void deleteAppointment(int id);
	
	/**
	 * Save a copy of deleted appointment
	 * @param a
	 * @param deletedBy
	 */
	@Transactional
	public void archiveAppointment(Appointment a, String deletedBy);
        
    /**
     * Set an appointment's narrative has been finished
     * @param id
     */
	@Transactional
    public void completeNarrative(int id);
	
	/**
     * @param id appointmentId
     * @return String KeyObservation for an appointment
     */
	@Transactional
    public String getKeyObservationByAppointmentId(int id);
    
    /**
     * Create alerts and key Observation for an appointment
     * @param int id appointmentId, String alerts, and String keyObservation
     * @return if it is successful for creating new record in database
     */
	@Transactional
    public boolean addAlertsAndKeyObservations(int id, String alerts, String keyObservations);
    
    /**
     * @param id appointmentId
     * @return String Plan for an appointment
     */
	@Transactional
    public String getPlanByAppointmentId(int id);
    
    /**
     * Create Plan for an appointment
     * @param int id appointmentId, String plan
     * @return if it is successful for creating new record in database
     */
	@Transactional
    public boolean addPlans(int id, String plan);
	
	/**
     * those completed appointments, but volunteer forgets clicking on Complete Visit button to set complete status 
     * @param patientId
     * @return a List of Appointment object
     */ 
	@Transactional
    public List<Appointment> getUnCompleteAppointments(int patientId);
	
	/**
	 * 
	 * @param volunteerId
	 * @param time
	 * @return
	 */
	@Transactional
	public boolean hasAppointmentByVolunteer(int volunteerId, String time);
	
	/**
	 * 
	 * @param patientId
	 * @param time
	 * @return
	 */
	@Transactional
	public boolean hasAppointmentByPatient(int patientId, String time);
	
}
