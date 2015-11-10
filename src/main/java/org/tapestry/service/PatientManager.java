package org.tapestry.service;

import java.util.List;

import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tapestry.objects.Patient;

/**
 * service for Model Patient
 * @author lxie *
 */
@Service
public interface PatientManager {
	/**
	* Returns the patient with the given ID
	* @param id The ID of the patient to find
	* @return A Patient object representing the result
	*/
	@Transactional
	public Patient getPatientByID(int id);
	
	/** 
	 * @param request
	 * @return a patient with full info from session
	 */
	@Transactional
	public Patient getClientFromSession(SecurityContextHolderAwareRequestWrapper request, int patientId);
	
	/**
	* List all the patients in the database
	* @return An ArrayList of Patient objects
	*/
	@Transactional
    public List<Patient> getAllPatients();
    
    /**
	* Returns a list of patients assigned to the specified volunteer
	* @param volunteer The ID of the volunteer
	* @return An ArrayList of Patient objects
	*/
	@Transactional
    public List<Patient> getPatientsForVolunteer(int volunteer);
    
    /**
	 * search a group of patients by partial name
	 * get all patients with partialName in firstname or lastname
	 * @param partialName
	 * @return
	 */
	@Transactional
	public List<Patient> getPatientsByPartialName(String partialName);
	/**
	 * 
	 * @param partialName
	 * @param organizationId
	 * @return
	 */
	@Transactional
	public List<Patient> getGroupedPatientsByName(String partialName, int organizationId);
	
	/**
	 * 
	 * @param siteId
	 * @return
	 */
	@Transactional
	public List<Patient> getPatientsBySite(int siteId);
		
	/**
	 * Create a patient in the database
	 * @param p
	 * @return new patient ID
	 */
	@Transactional
    public int createPatient(Patient p);
    
    /**
     * Changes a patient in the database
     * @param p The Patient object containing the new data (should have the same ID as the patient to replace)
     */
	@Transactional
    public void updatePatient(Patient p);
    
    /**
	* delete the specified patient from the database
	* @param id The ID of the patient to remove
	*/
	@Transactional
	public void deletePatientWithId(int id);
	
	/**
	 * authenticate PHR for patient
	 */
	@Transactional
	public void authenticatePHRPatientByID(int id);	
	
     /**
     * Create key Observation for a patient
     * @param int id patientId,  String keyObservation
     * @return if it is successful for creating new record in database
     */
	@Transactional
    public boolean addKeyObservations(int id, String keyObservations);
	
	   /**
     * @param id patienttId
     * @return String Plan for a patient
     */
	@Transactional
    public String getPlanByPatientId(int id);
    
    /**
     * Create Plan for a patient
     * @param int id patientId, String plan
     * @return if it is successful for creating new record in database
     */
	@Transactional
    public boolean addPlans(int id, String plan);
	
	/**
	 * Disable a patient by id
	 * @param id
	 */
	@Transactional
	public void disablePatientWithID(int id);
	
	/**
	 * Enable a patient by id
	 * @param id
	 */
	@Transactional
	public void enablePatientWithID(int id);
	
	/**
	 * 
	 * @param id
	 * @param notes
	 */
	@Transactional
	public void disablePatient(int id, String notes);
	
	/**
	 * Retrieve all exist research IDs
	 * @return a list of ids
	 */
	@Transactional
	public List<String> getResearchIds(int siteId);
	
	/**
	 * 
	 * @param id
	 * @param notes
	 */
	@Transactional
	public void updatePatientNote(int id, String notes);
	
	/**
	 * 
	 * @return
	 */
	@Transactional
	public List<Patient> getAllDisabledPatients();
	
	/**
	 * 
	 * @param siteId
	 */
	@Transactional
	public List<Patient> getAllDisabledPatients(int siteId);
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	@Transactional
	public Patient getPatientByUserId(int userId);
	
	/**
	 * 
	 * @param patientId
	 * @param volunteerId
	 * @param partnerId
	 */
	@Transactional
	public void updatePatientVolunteers(int patientId, int volunteerId, int partnerId);
	
	/**
	 * 
	 * @param patientId
	 * @return
	 */
	@Transactional
	public int getSiteByPatientId(int patientId);
	
	public void setDefaultUsernameAndPassword();
	
}
