package org.tapestry.dao;

import java.util.List;
import org.tapestry.objects.Appointment;

/**
 * Defines DAO operations for the Appointment model.
 * 
 * @author lxie 
*/
public interface AppointmentDAO {
	
	/**
     * @return all appointments in a list
     */
    public List<Appointment> getAllAppointments();
    
	/**
     * @return all Upcoming Appointments in a list
     */
    public List<Appointment> getAllUpcomingAppointments();
    
    /**     * 
     * @param id
     * @return all uncompleted appointments
     */
    public List<Appointment> getAllUnCompletedAppointmentsForPatient(int id);
    
    /**
     * 
     * @param id
     * @return all appointments in a list for a volunteer
     */
    public List<Appointment> getAllAppointmentsByVolunteer(int id);
    
    /**
     * @return all past appointments in a list
     */
    
    public List<Appointment> getAllPastAppointments();
    
    /**
     * all awaiting approved appointments 
     * @param patientId
     * @return a List of Appointment object
     */
    public List<Appointment> getAllPendingAppointments();   
    
    /**
     * get all approved and not completed appointments filtered by volunteer
     * @param volunteerId
     * @return a List of Appointment object
     */    
    public List<Appointment> getAllApprovedAppointmentsForVolunteer(int volunteerId);    
    
    /**
     * all awaiting approve appointments filtered by volunteer
     * @param volunteerId
     * @return a List of Appointment object
     */
    public List<Appointment> getAllPendingAppointmentsForVolunteer(int volunteerId);
    
    /**
     * all declined appointments filtered by volunteer
     * @param volunteerId
     * @return a List of Appointment object
     */    
    public List<Appointment> getAllDeclinedAppointmentsForVolunteer(int volunteerId);
    
    /**
     * all completed appointments filtered by volunteer
     * @param volunteerId
     * @return a List of Appointment object
     */ 
    public List<Appointment> getAllCompletedAppointmentsForVolunteer(int volunteerId);
    
    /**
     * all uncompleted appointments filtered by volunteer
     * @param volunteerId
     * @return a List of Appointment object
     */ 
    public List<Appointment> getAllUpcomingAppointmentsForVolunteer(int volunteerId);

    /**
     * those completed appointments, but volunteer forgets clicking on Complete Visit button to set complete status 
     * @param patientId
     * @return a List of Appointment object
     */ 
    public List<Appointment> getUnCompleteAppointments(int patientId);
    /**
     * all approved appointments filtered by patient
     * @param patientId
     * @return a List of Appointment object
     */    
    public List<Appointment> getAllApprovedAppointmentsForPatient(int patientId);
    
    /**
     * all approved appointments filtered by patient, volunteer
     * @param patientId
     * @return a List of Appointment object
     */    
    public List<Appointment> getAllApprovedAppointmentsForPatient(int patientId, int volunteerId);
    
    /**
     * all awaiting approved appointments filtered by volunteer
     * @param patientId
     * @return a List of Appointment object
     */
    public List<Appointment> getAllPendingAppointmentsForPatient(int patientId);
    
    /**
     * all awaiting approved appointments filtered by volunteer, patient
     * @param patientId
     * @return a List of Appointment object
     */
    public List<Appointment> getAllPendingAppointmentsForPatient(int patientId, int volunteerId);
    
    /**
     * all declined appointments filtered by patient
     * @param patientId
     * @return a List of Appointment object
     */
    public List<Appointment> getAllDeclinedAppointmentsForPatient(int patientId);
    
    /**
     * all declined appointments filtered by patient, volunteer
     * @param patientId
     * @return a List of Appointment object
     */
    public List<Appointment> getAllDeclinedAppointmentsForPatient(int patientId, int volunteerId);
    
    /**
     * all approved and not completed appointments filtered by patient
     * @param patientId
     * @return a List of Appointment object
     */
    public List<Appointment> getAllUpcommingAppointmentForPatient(int patientId);
    
    /**
     * get all completed appointment filtered by patient
     * @param patientId
     * @return a List of Appointment object
     */
    public List<Appointment> getAllCompletedAppointmentsForPatient(int patientId);
    
    /**
     * recent approved, not completed appointments filtered by patient
     * @param patientId
     * @return an Object of Appointment
     */
    public Appointment getAppointmentByMostRecentIncomplete(int patientId);
    
    /**     
     * @param patientId
     * @return an Object of Appointment
     */
    public Appointment getAppointmentById(int appointmentId);
    
    /**
     * 
     * @param role admin = 0
     * @param diff -2, two days later than now
     * @return
     */
    public List<Appointment> getRemindingAppointmentList(int id, int diff);
    
    /**
     * Group appointment by volunteer's organization
     * @param organizationId
     * @return
     */
    public List<Appointment> getAppointmentsGroupByOrganization(int organizationId);

    /**
     * Group Upcoming appointment by volunteer's organization
     * @param organizationId
     * @return
     */
    public List<Appointment> getUpcomingAppointmentsGroupByOrganization(int organizationId);

    
    /**
     * Group past appointment by volunteer's organization
     * @param organizationId
     * @return
     */
    public List<Appointment> getPastAppointmentsGroupByOrganization(int organizationId);
    
    /**
     * Group pending appointment by volunteer's organization
     * @param organizationId
     * @return
     */
    public List<Appointment> getPendingAppointmentsGroupByOrganization(int organizationId);
    
    /**
     * Set an appointment status as completed and add comments, set if contacted admin
     * @param id appointmentId
     * @param comments
     * @param contacedAdmin    
     */
    public void completeAppointment(int id, String comments, boolean contactedAdmin);
    
    /**
     * Set an appointment status as completed 
     * @param id appointmentId     
     */
    public void completeAppointment(int id);
    
    /**
     * Set an appointment status as completed and add comments 
     * @param id appointmentId
     * @param comments 
     */
    public void completeAppointment(int appointmentId, String comments);
    
    /**
     * Create an appointment in database
     * @param appointment   
     */
    public boolean createAppointment(Appointment a);
    
    /**
     * Set an appointment's status as approved
     * @param id appointmentId
     */
    public void approveAppointment(int id);
    
    /**
     * Set an appointment's status as decline
     * @param id appointmentId
     */
    public void declineAppointment(int id);
    
    /**
     * Delete an appointment from database
     * @param id appointmentId
     */
    public void deleteAppointment(int id);
    
    /**
     * Set an appointment's narrative has been finished
     * @param id
     */
    public void completeNarrative(int id);
    
    /**
     * Save a copy of deleted appointment
     * @param appointment
     * @param deletedBy
     */
    public void archiveAppointment(Appointment appointment, String deletedBy);
    
    /**
     * @param id appointmentId
     * @return String KeyObservation for an appointment
     */
    public String getKeyObservationByAppointmentId(int id);
    
    /**
     * Create alerts and key Observation for an appointment
     * @param int id appointmentId, String alerts, and String keyObservation
     * @return if it is successful for creating new record in database
     */
    public boolean addAlertsAndKeyObservations(int id, String alerts, String keyObservations);
    
    /**
     * @param id appointmentId
     * @return String Plan for an appointment
     */
    public String getPlanByAppointmentId(int id);
    
    /**
     * Create Plan for an appointment
     * @param int id appointmentId, String plan
     * @return if it is successful for creating new record in database
     */
    public boolean addPlans(int id, String plan);
    
    /**
     * 
     * @param volunteerId
     * @param time
     * @return
     */
    public boolean hasAppointmentByVolunteer(int volunteerId, String time);
    
    public boolean hasAppointmentByPatient(int patientId, String time);

}
