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
	* List all the patients group by volunteer's organization in the database
	* @return a List of Patient objects
	*/
    public List<Patient> getPatientsByGroup(int organizationId);
    
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
	
	
	
}
