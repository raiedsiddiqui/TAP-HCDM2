package org.tapestry.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Service;
import org.tapestry.dao.ClinicDAO;
import org.tapestry.dao.PatientDAO;
import org.tapestry.objects.Patient;
/**
 * Implementation for service PatientManager
 * @author lxie *
 */
@Service
public class PatientManagerImpl implements PatientManager {
	@Autowired
	private PatientDAO patientDao;
	@Autowired
	private ClinicDAO clinicDao;

	@Override
	public Patient getPatientByID(int id) {
		return patientDao.getPatientByID(id);
	}

	@Override
	public List<Patient> getAllPatients() {
		return patientDao.getAllPatients();
	}

	@Override
	public List<Patient> getPatientsForVolunteer(int volunteer) {
		return patientDao.getPatientsForVolunteer(volunteer);
	}

	@Override
	public List<Patient> getPatientsByPartialName(String partialName) {
		return patientDao.getPatientsByPartialName(partialName);
	}
	
	@Override
	public List<Patient> getPatientsBySite(int siteId) {		
		return patientDao.getPatientsBySite(siteId);
	}

	@Override
	public int createPatient(Patient p) {
		return patientDao.createPatient(p);
	}

	@Override
	public void updatePatient(Patient p) {
		patientDao.updatePatient(p);
	}

	@Override
	public void deletePatientWithId(int id) {
		patientDao.deletePatientWithId(id);
	}

	@Override
	public void authenticatePHRPatientByID(int id) {
		patientDao.authenticatePHRPatientByID(id);
		
	}

	@Override
	public List<Patient> getGroupedPatientsByName(String partialName, int organizationId) {
		return patientDao.getGroupedPatientsByName(partialName, organizationId);
	}
	
	@Override
	public boolean addKeyObservations(int id, String keyObservations) {
		return patientDao.addKeyObservations(id, keyObservations);
	}

	@Override
	public String getPlanByPatientId(int id) {
		return patientDao.getPlanByPatientId(id);
	}

	@Override
	public boolean addPlans(int id, String plan) {
		return patientDao.addPlans(id, plan);
	}
	

	@Override
	public Patient getClientFromSession(SecurityContextHolderAwareRequestWrapper request, int patientId) {	
		HttpSession session = request.getSession();
		List<Patient> patients = (List<Patient>)session.getAttribute("allPatientWithFullInfos");
		
		for (Patient patient: patients)
		{
			if (patientId == patient.getPatientID())
				return patient;
		}
		return null;
	}

	@Override
	public void disablePatientWithID(int id) {
		patientDao.disablePatientWithID(id);		
	}

	@Override
	public void enablePatientWithID(int id) {
		patientDao.enablePatientWithID(id);
	}

	@Override
	public List<String> getResearchIds(int siteId) {
		return patientDao.getResearchIds(siteId);
	}

	@Override
	public void updatePatientNote(int id, String notes) {		
		StringBuffer sb = new StringBuffer();
		sb.append(patientDao.getPatientNote(id));
		sb.append("-- Disable note from Admin ---");
		sb.append(notes);
		
		patientDao.updatePatientNote(id, sb.toString());	
	}

	@Override
	public void disablePatient(int id, String notes) {
		disablePatientWithID(id);
		updatePatientNote(id,notes);
	}

	@Override
	public List<Patient> getAllDisabledPatients() {
		return patientDao.getAllDisabledPatients();
	}

	@Override
	public List<Patient> getAllDisabledPatients(int siteId) {
		return patientDao.getAllDisabledPatients(siteId);		
	}
	
	@Override
	public void updatePatientVolunteers(int patientId, int volunteerId,	int partnerId) {
		patientDao.updatePatientVolunteers(patientId, volunteerId, partnerId);		
	}

	@Override
	public int getSiteByPatientId(int patientId) {
		Patient p = getPatientByID(patientId);
		return clinicDao.getSiteByClinic(p.getClinic());
	}

	@Override
	public Patient getPatientByUserId(int userId) {
		return patientDao.getPatientByUserId(userId);
	}

	@Override
	public void setDefaultUsernameAndPassword() 
	{
		List<Patient> patients = getAllPatients();
		int patientId;
		String tapUsername;
		
		for (Patient p: patients)
		{
			patientId = p.getPatientID();
			tapUsername = p.getFirstName() + "."+ p.getLastName();
			patientDao.setDefaultUsernameAndPassword(patientId, tapUsername);
		}		
	}

}
