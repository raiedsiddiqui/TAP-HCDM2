package org.tapestry.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tapestry.objects.Activity;
import org.tapestry.objects.Narrative;
import org.tapestry.objects.Organization;
import org.tapestry.objects.Volunteer;

/**
 * service for Model Volunteer, Activity
 * @author lxie 
 */
@Service
public interface VolunteerManager {
	/**
     * @return all volunteers in a list
     */
	@Transactional
	public List<Volunteer> getAllVolunteers();
	
	/**
	 * get a list of volunteer, each volunteer has info about if it can be delete or not
	 * @return
	 */
	@Transactional
	public List<Volunteer> getAllVolunteersWithCanDelete();
	
	/**
     * @return all volunteers who has availability set up in a list
     */
	@Transactional
	public List<Volunteer> getVolunteersWithAvailability();
	
	/**	 * 
	 * @param partialName
	 * @return a list of volunteers whose name contain partialName
	 */
	@Transactional
	public List<Volunteer> getVolunteersByName(String partialName);
	
	/**
	 * 
	 * @param id
	 * @return a list of volunteer who belongs to an organization
	 */
	@Transactional
	public List<Volunteer> getAllVolunteersByOrganization(int id);
	
	/**
	 * 
	 * @param id
	 * @return a list of volunteer, each volunteer who belongs to an organization 
	 * has info about if it can be delete or not
	 */
	@Transactional
	public List<Volunteer> getAllVolunteersWithCanDeleteByOrganization(int id);
	
		
	/**
	 * 
	 * @param id
	 * @return a list of volunteer
	 * has info about if it can be delete or not
	 */
	@Transactional
	public List<Volunteer> getAllVolunteersWithCanDeleteBySite(int id);
	
	/**
	 * search by name for a grouped volunteer
	 * @param partialName
	 * @param organizationId
	 * @return a list of volunteers whose name contain partialName and belong to an organization
	 */
	@Transactional
	public List<Volunteer> getGroupedVolunteersByName(String partialName, int organizationId);
	
	/**
	 * 
	 * @param id
	 * @return a volunteer, whose id is same as given id
	 */
	@Transactional
	public Volunteer getVolunteerById(int id);
	
	/**
	 * 
	 * @param username
	 * @return volunteer Id whose user name is username
	 */
	@Transactional
	public int getVolunteerIdByUsername(String username);
	
	/**
	 * 
	 * @return a list all volunteer's user name
	 */
	@Transactional
	public List<String> getAllExistUsernames();
	
	/**
	 * Create a new volunteer
	 * @param volunteer
	 * @return true if success
	 */
	@Transactional
	public boolean addVolunteer(Volunteer volunteer);
	
	/**
	 * Modify volunteer
	 * @param volunteer
	 */
	@Transactional
	public void updateVolunteer(Volunteer volunteer);
	
	/**
	 * Modify volunteer availability
	 * @param volunteerId and availability
	 */
	@Transactional
	public void updateVolunteerAvalability(int volunteerId, String availability);
	
	/**
	 * Delete a volunteer by giving ID
	 * @param id
	 */
	@Transactional
	public void deleteVolunteerById(int id);
	
	/**
	 * 
	 * @param volunteer
	 * @param deleteBy
	 */
	@Transactional
	public void archiveVolunteer(Volunteer volunteer, String deletedBy);
	
	/**
	 * Count all volunteers
	 * @return number of volunteers
	 */
	@Transactional
	public int countAllVolunteers();

	/**
	 * get volunteer's name
	 * @param volunteerId
	 * @return
	 */
	@Transactional
	public String getVolunteerNameById(int volunteerId);
	
	/**
	 * @param volunteerId
	 * @return volunteer's user ID
	 */
	@Transactional
	public int getUserIdByVolunteerId(int volunteerId);
	
	/**
	 * @param organization
	 * @param time period
	 * @return list of volunteers who are available at that time
	 */
	@Transactional
	public List<Volunteer> getVolunteersByAvailibility(String time, int organization);
	
	/**
	 * @param time period
	 * @return list of volunteers who are available at that time
	 */
	@Transactional
	public List<Volunteer> getVolunteersByAvailibility(String time, List<Volunteer> volunteers);
	
	
	/** 
	 * @return a list of all organizations
	 */
	@Transactional
	public List<Organization> getAllOrganizations();
	
	
	/**
	 * Get a list of Organization containing 'is has volunteer' info for show/hide Delete button
	 * @return
	 */
	@Transactional
	public List<Organization> getOrganizations();
	
	/**
	 * @param id
	 * @return an organization object by giving ID
	 */
	@Transactional
	public Organization getOrganizationById(int id);
	
	/**
	 * 
	 * @param partialName
	 * @return a list of organizations which name contains partialName
	 */
	@Transactional
	public List<Organization> getOrganizationsByName(String partialName);
	
	/**
	 * 
	 * @param organization
	 * @return true if create an organization successfully
	 */	
	@Transactional
	public boolean addOrganization(Organization organization);
	
	/**
	 * Modify orgnaization
	 * @param organization
	 */
	@Transactional
	public void updateOrganization(Organization organization);
	
	/**
	 * Delete an organization 
	 * @param id
	 */
	@Transactional
	public void deleteOrganizationById(int id);
	
	/**
	 * Save a copy of deleted organization
	 * @param organization
	 * @param deletedBy
	 */
	@Transactional
	public void archiveOrganization(Organization organization, String deletedBy);
	
	//Activity----
	/**	 
	 * @param volunteer
	 * @return a list of Activities for avselected volunteer
	 */
	@Transactional
	public List<Activity> getActivitiesForVolunteer(int volunteer);
	/**
	 * @param id
	 * @return a list of Activities for local admin
	 */
	@Transactional
	public List<Activity> getActivitiesForLocalAdmin(int id);
	/**
	 * 
	 * @return a list of Activities for central admin
	 */
	@Transactional
	public List<Activity> getActivitiesForAdmin();
	/**
	 * log activity with description by volunteer
	 * @param description
	 * @param volunteer
	 */
	@Transactional
	public void logActivity(String description, int volunteer);

	/**
	 * log activity
	 * @param activity
	 */
	@Transactional
	public void logActivity(Activity activity);
	/**
	 * update activity
	 * @param activity
	 */
	@Transactional
	public void updateActivity(Activity activity);	
	/**
	 * 
	 * @param patientId
	 * @param appointmentId
	 * @return a list of activities for patient on appointment
	 */
//	@Transactional
//	public List<Activity> getActivities(int patientId, int appointmentId);
	/**
	 * delete activity
	 * @param id
	 */
	@Transactional
	public void deleteActivity(int id);	
	/**
	 * 
	 * @param start
	 * @param n
	 * @return a list of activities for a page
	 */
	@Transactional
	public List<Activity> getPage(int start, int n);
	/**
	 * 
	 * @param activityId
	 * @return an activity
	 */
	@Transactional
	public Activity getActivity(int activityId);	
	
	/**
	 * 
	 * @param activity
	 * @param deletedBy
	 * @param volunteer
	 */
	@Transactional
	public void archivedActivity(Activity activity, String deletedBy, String volunteer);
	
	//==========Narrative=====//
	/**
	 * 
	 * @param volunteerId
	 * @return
	 */			
	@Transactional
	public List<Narrative> getAllNarrativesByUser(int volunteerId);
	
	/**
	 * 
	 * @param volunteerId
	 * @param patientId
	 * @param appointmentId
	 * @return
	 */
	@Transactional
	public List<Narrative> getNarrativesByVolunteer(int volunteerId, int patientId, int appointmentId);
	
	/**
	 * 
	 * @param narrativeId
	 * @return
	 */
	@Transactional
	public Narrative getNarrativeById(int narrativeId);
	
	/**
	 * 
	 * @param appointmentId
	 * @return
	 */
	@Transactional
	public Narrative getNarrativeByAppointmentId(int appointmentId);
	
	/**
	 * 
	 * @param narrative
	 */
	@Transactional
	public void addNarrative(Narrative narrative);
	
	/**
	 * 
	 * @param narrative
	 */
	@Transactional
	public void updateNarrative(Narrative narrative);
	
	/**
	 * 
	 * @param narrativeId
	 */
	@Transactional
	public void deleteNarrativeById(int narrativeId);
	
	/**
	 * Save a copy of updated narrative
	 * @param n
	 * @param updatedBy
	 * @param whatAction
	 */
	@Transactional
	public void archiveNarrative(Narrative n, String updatedBy,	String whatAction);
}
