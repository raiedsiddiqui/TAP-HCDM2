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
	 * @return a list of volunteers whose name contain partialName and belong to an organization
	 */
	public List<Patient> getGroupedPatientsByName(String partialName, int organizationId);
	
	/**
	* List all the patients group by volunteer's organization in the database
	* @return An ArrayList of Patient objects
	*/
    public List<Patient> getPatientsByGroup(int organizationId);
	
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
}
