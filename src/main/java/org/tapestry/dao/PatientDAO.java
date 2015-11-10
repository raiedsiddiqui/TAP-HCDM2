package org.tapestry.dao;

import java.util.List;

import org.tapestry.objects.Patient;

/**
 * Defines DAO operations for the Patient model.
 * 
 * @author lxie 
*/

public interface PatientDAO {
	/**
	* Returns the patient with the given ID
	* @param id The ID of the patient to find
	* @return A Patient object representing the result
	*/
	public Patient getPatientByID(int id);

	/**
	* List all the patients in the database
	* @return An ArrayList of Patient objects
	*/
    public List<Patient> getAllPatients();
    
    /**
	* Returns a list of patients assigned to the specified volunteer
	* @param volunteer The ID of the volunteer
	* @return An ArrayList of Patient objects
	*/
    public List<Patient> getPatientsForVolunteer(int volunteer);
    
    /**
	 * 
	 * get all patients with partialName in firstname or lastname
	 * @param partialName
	 * @return
	 */
	public List<Patient> getPatientsByPartialName(String partialName);
	
	/**
	 * search by name for a grouped patients
	 * @param partialName
	 * @param organizationId
	 * @return a list of volunteers whose name contain partialName and belong to a site
	 */
	public List<Patient> getGroupedPatientsByName(String partialName, int siteId);
		    
    /**
     * 
     * @param siteId
     * @return all patients belong to  same site(siteId)
     */
    public List<Patient> getPatientsBySite(int siteId);
	
    /**
     * 
     * @return
     */
    public List<Patient> getAllDisabledPatients();
    
    /**
     * 
     * @param site
     * @return
     */
    public List<Patient> getAllDisabledPatients(int site);
	/**
	* create a patient in the database
	* @param p The Patient object to save
	* @return new patient ID
	*/
    public int createPatient(Patient p);
    
    /**
     * Changes a patient in the database
     * @param p The Patient object containing the new data (should have the same ID as the patient to replace)
     */
    public void updatePatient(Patient p);
    
    /**
	* delete the specified patient from the database
	* @param id The ID of the patient to remove
	*/
	public void deletePatientWithId(int id);
	
	/**
	 * verify patient authentication PHR
	 * @param id patient ID
	 */
	public void authenticatePHRPatientByID(int id);
   
    /**
     * Create key Observation for a patient
     * @param int id patientId, String keyObservation
     * @return if it is successful for creating new record in database
     */
    public boolean addKeyObservations(int id, String keyObservations);
    
    /**
     * @param id patientId
     * @return String Plan for a patient
     */
    public String getPlanByPatientId(int id);
    
    /**
     * @param id userId
     * @return Patient 
     */
    public Patient getPatientByUserId(int userId);
    
    /**
     * Create Plan for a patient
     * @param int id patientId, String plan
     * @return if it is successful for creating new record in database
     */
    public boolean addPlans(int id, String plan);
    
    /**
	 * Disable a patient by id
	 * @param id
	 */
	public void disablePatientWithID(int id);
	
	/**
	 * Enable a patient by id
	 * @param id
	 */
	public void enablePatientWithID(int id);
	
	/**
	 * Retrieve all exist research IDs
	 * @param siteId 
	 * @return ids
	 */
	public List<String> getResearchIds(int siteId);
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public String getPatientNote(int id);
	
	/**
	 * 
	 * @param id
	 */
	public void updatePatientNote(int id, String notes);
	
	/**
	 * 
	 * @param id
	 * @param volunteer
	 * @param partner
	 */
	public void updatePatientVolunteers(int id, int volunteer, int partner);
	
	public void setDefaultUsernameAndPassword(int patientId, String username);
	
}
