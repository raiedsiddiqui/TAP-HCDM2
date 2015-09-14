package org.tapestry.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tapestry.dao.ActivityDAO;
import org.tapestry.dao.AppointmentDAO;
import org.tapestry.dao.NarrativeDAO;
import org.tapestry.dao.PatientDAO;
import org.tapestry.dao.VolunteerDAO;
import org.tapestry.objects.Activity;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Narrative;
import org.tapestry.objects.Organization;
import org.tapestry.objects.Patient;
import org.tapestry.objects.Volunteer;
/**
 * Implementation for service VolunteerManager
 * @author lxie 
 */
@Service
public class VolunteerManagerImpl implements VolunteerManager {
	@Autowired
	private VolunteerDAO volunteerDao;
	@Autowired
	private ActivityDAO activityDAO;
	@Autowired
	private NarrativeDAO narrativeDao;
	@Autowired
	private AppointmentDAO appointmentDao;
	@Autowired
	private PatientDAO patientDao;
	
	@Override
	public List<Volunteer> getAllVolunteers() {
		return volunteerDao.getAllVolunteers();
	}

	@Override
	public List<Volunteer> getAllVolunteersWithCanDelete() {
		List<Volunteer> volunteers = getAllVolunteers();
		List<Appointment> appointments = new ArrayList<Appointment>();
		List<Patient> patients = new ArrayList<Patient>();
		int vId;
		
		for (Volunteer v: volunteers)
		{
			vId = v.getVolunteerId();
			appointments = appointmentDao.getAllUpcomingAppointmentsForVolunteer(vId);
			patients = patientDao.getPatientsForVolunteer(vId);
			
			if (((appointments != null) && (appointments.size()>0)) || ((patients != null)
					&& (patients.size()>0)))
				v.setShowDelete(true);
			else
				v.setShowDelete(false);			
		}		
		return volunteers;
	}

	@Override
	public List<Volunteer> getAllVolunteersWithCanDeleteByOrganization(int id) {
		List<Volunteer> volunteers = getAllVolunteersByOrganization(id);
		List<Appointment> appointments = new ArrayList<Appointment>();
		List<Patient> patients = new ArrayList<Patient>();
		int vId;
		
		for (Volunteer v: volunteers)
		{
			vId = v.getVolunteerId();
			appointments = appointmentDao.getAllUpcomingAppointmentsForVolunteer(vId);
			patients = patientDao.getPatientsForVolunteer(vId);
			
			
			if (((appointments != null) && (appointments.size()>0)) || ((patients != null)
					&& (patients.size()>0)))
				v.setShowDelete(true);
			else
				v.setShowDelete(false);			
		}
		
		return volunteers;
	}
	
	@Override
	public List<Volunteer> getAllVolunteersWithCanDeleteBySite(int id) {
		List<Volunteer> volunteers = getAllVolunteersByOrganization(id);
		List<Appointment> appointments = new ArrayList<Appointment>();
		List<Patient> patients = new ArrayList<Patient>();
		int vId;
		
		for (Volunteer v: volunteers)
		{
			vId = v.getVolunteerId();
			appointments = appointmentDao.getAllUpcomingAppointmentsForVolunteer(vId);
			patients = patientDao.getPatientsForVolunteer(vId);
			
			
			if (((appointments != null) && (appointments.size()>0)) || ((patients != null)
					&& (patients.size()>0)))
				v.setShowDelete(true);
			else
				v.setShowDelete(false);			
		}
		
		return volunteers;
	}

	@Override
	public List<Volunteer> getVolunteersWithAvailability() {
		return volunteerDao.getVolunteersWithAvailability();
	}

	@Override
	public List<Volunteer> getVolunteersByName(String partialName) {
		return volunteerDao.getVolunteersByName(partialName);
	}

	@Override
	public List<Volunteer> getAllVolunteersByOrganization(int id) {
		return volunteerDao.getAllVolunteersByOrganization(id);
	}

	@Override
	public Volunteer getVolunteerById(int id) {
		return volunteerDao.getVolunteerById(id);
	}

	@Override
	public int getVolunteerIdByUsername(String username) {
		return volunteerDao.getVolunteerIdByUsername(username);
	}

	@Override
	public List<String> getAllExistUsernames() {
		return volunteerDao.getAllExistUsernames();
	}

	@Override
	public boolean addVolunteer(Volunteer volunteer) {
		return volunteerDao.addVolunteer(volunteer);
	}

	@Override
	public void updateVolunteer(Volunteer volunteer) {
		volunteerDao.updateVolunteer(volunteer);
	}
	
	@Override
	public void updateVolunteerAvalability(int volunteerId, String availability) {
		volunteerDao.updateVolunteerAvalability(volunteerId, availability);
		
	}

	@Override
	public void deleteVolunteerById(int id) {
		volunteerDao.deleteVolunteerById(id);
	}

	@Override
	public int countAllVolunteers() {
		return volunteerDao.countAllVolunteers();
	}

	@Override
	public String getVolunteerNameById(int volunteerId) {
		Volunteer volunteer = getVolunteerById(volunteerId);		
		return volunteer.getDisplayName();
	}

	@Override
	public int getUserIdByVolunteerId(int volunteerId) {
		return volunteerDao.getUserIdByVolunteerId(volunteerId);
	}
	
	@Override
	public List<Volunteer> getVolunteersByAvailibility(String time, int organization) {
		List<Volunteer> volunteers;
//		List<Volunteer>  new_volunteer_list = new ArrayList<Volunteer>();
//		String availibility;
		if (organization == 0)
			volunteers = this.getAllVolunteers();
		else
			volunteers = this.getAllVolunteersByOrganization(organization);
		
//		for (Volunteer v: volunteers)
//		{
//			availibility = v.getAvailability();
//			if (availibility.contains(time))
//				new_volunteer_list.add(v);			
//		}
//		
//		return new_volunteer_list;
		
		return getVolunteersByAvailibility(time, volunteers);
	}
	

	@Override
	public List<Volunteer> getVolunteersByAvailibility(String time,	List<Volunteer> volunteers) {
		List<Volunteer>  new_volunteer_list = new ArrayList<Volunteer>();
		String availibility;
		for (Volunteer v: volunteers)
		{
			availibility = v.getAvailability();
			if (availibility.contains(time))
				new_volunteer_list.add(v);			
		}
		
		return new_volunteer_list;
	}


	@Override
	public List<Organization> getAllOrganizations() {			
		return volunteerDao.getAllOrganizations();
	}
	
	@Override
	public List<Organization> getOrganizations() {
		List<Volunteer> vols = new ArrayList<Volunteer>();
		List<Organization> organizations = getAllOrganizations();
		int id;
		
		for(Organization o: organizations){
			id = o.getOrganizationId();
			vols = volunteerDao.getAllVolunteersByOrganization(id);
			if ((vols != null) && (vols.size()>0))
				o.setHasVolunteer(true);
			else
				o.setHasVolunteer(false);
		}		
		return organizations;
	}


	@Override
	public Organization getOrganizationById(int id) {
		return volunteerDao.getOrganizationById(id);
	}

	@Override
	public List<Organization> getOrganizationsByName(String partialName) {
		return volunteerDao.getOrganizationsByName(partialName);
	}
	
	@Override
	public List<Volunteer> getGroupedVolunteersByName(String partialName,int organizationId) {
		return volunteerDao.getGroupedVolunteersByName(partialName, organizationId);
	}

	@Override
	public boolean addOrganization(Organization organization) {
		return volunteerDao.addOrganization(organization);
	}

	@Override
	public void updateOrganization(Organization organization) {
		volunteerDao.updateOrganization(organization);
	}

	@Override
	public void deleteOrganizationById(int id) {
		volunteerDao.deleteOrganizationById(id);
	}

	@Override
	public void archiveOrganization(Organization organization, String deletedBy) {
		volunteerDao.archiveOrganization(organization, deletedBy);
		
	}
	
	//Activity
	@Override
	public List<Activity> getActivitiesForVolunteer(int volunteer) {		
		return activityDAO.getAllActivitiesForVolunteer(volunteer);
	}

	@Override
	public List<Activity> getActivitiesForLocalAdmin(int organizationId) {		
		return activityDAO.getAllActivitiesForLocalAdmin(organizationId);
	}

	@Override
	public List<Activity> getActivitiesForAdmin() {
		return activityDAO.getAllActivitiesForAdmin();
	}

	@Override
	public void logActivity(String description, int volunteer) {
		activityDAO.logActivity(description, volunteer);
	}

	@Override
	public void logActivity(Activity activity) {
		activityDAO.logActivity(activity);
	}

	@Override
	public void updateActivity(Activity activity) {
		activityDAO.updateActivity(activity);
	}

//	@Override
//	public List<Activity> getActivities(int patientId, int appointmentId) {
//		return activityDAO.getDetailedLog(patientId, appointmentId);
//	}

	@Override
	public void deleteActivity(int id) {
		activityDAO.deleteActivityById(id);

	}

	@Override
	public List<Activity> getPage(int start, int n) {		
		return activityDAO.getPage(n, n);
	}

	@Override
	public Activity getActivity(int activityId) {		
		return activityDAO.getActivityLogById(activityId);
	}
	
	@Override
	public void archivedActivity(Activity activity, String deletedBy, String volunteer) {
		activityDAO.archivedActivity(activity, deletedBy, volunteer);
	}
	
	//=============Narrative ======//
	@Override
	public List<Narrative> getAllNarrativesByUser(int volunteerId) {
		return narrativeDao.getAllNarrativesByUser(volunteerId);
	}

	@Override
	public List<Narrative> getNarrativesByVolunteer(int volunteerId,int patientId, int appointmentId) {
		return narrativeDao.getNarrativesByVolunteer(volunteerId, patientId, appointmentId);
	}

	@Override
	public Narrative getNarrativeById(int narrativeId) {
		return narrativeDao.getNarrativeById(narrativeId);
	}

	@Override
	public void addNarrative(Narrative narrative) {
		narrativeDao.addNarrative(narrative);
	}

	@Override
	public void updateNarrative(Narrative narrative) {
		narrativeDao.updateNarrative(narrative);
	}

	@Override
	public void deleteNarrativeById(int narrativeId) {
		narrativeDao.deleteNarrativeById(narrativeId);
	}	

	@Override
	public void archiveNarrative(Narrative n, String updatedBy,	String whatAction) {
		narrativeDao.archiveNarrative(n, updatedBy, whatAction);		
	}


	@Override
	public void archiveVolunteer(Volunteer volunteer, String deletedBy) {
		volunteerDao.archiveVolunteer(volunteer, deletedBy);
	}

	@Override
	public Narrative getNarrativeByAppointmentId(int appointmentId) {
		return narrativeDao.getNarrativeByAppointmentId(appointmentId);
	}

}
