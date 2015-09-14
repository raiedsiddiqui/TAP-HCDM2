package org.tapestry.dao;

import java.util.List;

import org.tapestry.objects.Organization;
import org.tapestry.objects.Volunteer;

/**
 * Defines DAO operations for the Volunteer model.
 * 
 * @author lxie 
*/
public interface VolunteerDAO {

	/**
     * @return all volunteers in a list
     */
	public List<Volunteer> getAllVolunteers();
	
	/**
     * @return all volunteers who has availability set up in a list
     */
	public List<Volunteer> getVolunteersWithAvailability();
	
	/**	 
	 * search by name
	 * @param partialName
	 * @return a list of volunteers whose name contain partialName
	 */
	public List<Volunteer> getVolunteersByName(String partialName);
	
	/**
	 * search by name for a grouped volunteer
	 * @param partialName
	 * @param organizationId
	 * @return a list of volunteers whose name contain partialName and belong to an organization
	 */
	public List<Volunteer> getGroupedVolunteersByName(String partialName, int organizationId);
	
	/**
	 * 
	 * @param id
	 * @return a list of volunteer who belong to a organization
	 */
	public List<Volunteer> getAllVolunteersByOrganization(int id);	
		
	/**
	 * 
	 * @param id
	 * @return a volunteer, whose id is same as given id
	 */
	public Volunteer getVolunteerById(int id);
	
	/**
	 * 
	 * @param username
	 * @return volunteer Id whose user name is username
	 */
	public int getVolunteerIdByUsername(String username);
	
	/**
	 * 
	 * @return a list all volunteer's user name
	 */
	public List<String> getAllExistUsernames();
	
	/**
	 * Create a new volunteer
	 * @param volunteer
	 * @return true if success
	 */
	public boolean addVolunteer(Volunteer volunteer);
	
	/**
	 * Modify volunteer
	 * @param volunteer
	 */
	public void updateVolunteer(Volunteer volunteer);
	
	/**
	 * Modify volunteer
	 * @param volunteer
	 */
	public void updateVolunteerAvalability(int volunteerId, String availablitly);
	
	/**
	 * Delete a volunteer by giving ID
	 * @param id
	 */
	public void deleteVolunteerById(int id);
	
	/**
	 * Count all volunteers
	 * @return number of volunteers
	 */
	public int countAllVolunteers();

//	/**
//	 * get volunteer's name
//	 * @param volunteerId
//	 * @return
//	 */
//	public String getVolunteerNameById(int volunteerId);
	
	/**
	 * @param volunteerId
	 * @return volunteer's user ID
	 */
	public int getUserIdByVolunteerId(int volunteerId);
	
	/** 
	 * @return a list of all organizations
	 */
	public List<Organization> getAllOrganizations();
	
	/**
	 * @param id
	 * @return an organization object by giving ID
	 */
	public Organization getOrganizationById(int id);
	
	/**
	 * 
	 * @param partialName
	 * @return a list of organizations which name contains partialName
	 */
	public List<Organization> getOrganizationsByName(String partialName);
	
	/**
	 * 
	 * @param organization
	 * @return true if create an organization successfully
	 */	 
	public boolean addOrganization(Organization organization);
	
	/**
	 * Modify orgnaization
	 * @param organization
	 */
	public void updateOrganization(Organization organization);
	
	/**
	 * Delete an organization 
	 * @param id
	 */
	public void deleteOrganizationById(int id);	
	
	/**
	 * save a copy of deleted organization
	 * @param organization
	 * @param deletedBy
	 */
	public void archiveOrganization(Organization organization, String deletedBy);
	
	/**
	 * save a copy of deleted volunteer
	 * @param volunteer
	 * @param deletedBy
	 */
	public void archiveVolunteer(Volunteer volunteer, String deletedBy);
	
	
}
