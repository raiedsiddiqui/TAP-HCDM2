package org.tapestry.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Service;
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
	public List<Patient> getPatientsByGroup(int organizationId) {		
		return patientDao.getPatientsByGroup(organizationId);
	}

	@Override
	public List<Patient> getGroupedPatientsByName(String partialName, int organizationId) {
		return patientDao.getGroupedPatientsByName(partialName, organizationId);
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

}
