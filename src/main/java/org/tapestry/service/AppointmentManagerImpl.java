package org.tapestry.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tapestry.dao.AppointmentDAO;
import org.tapestry.objects.Appointment;
/**
 * Implementation for service AppointmentManager
 * @author lxie 
 */
@Service
public class AppointmentManagerImpl implements AppointmentManager {
	@Autowired
	private AppointmentDAO appointmentDao;
	
	@Override
	public List<Appointment> getAllAppointments() {
		return appointmentDao.getAllAppointments();
	}
	
	@Override
	public List<Appointment> getAllUpcomingAppointments() {
		return appointmentDao.getAllUpcomingAppointments();
	}

	@Override
	public List<Appointment> getAllPastAppointments() {
		return appointmentDao.getAllPastAppointments();
	}

	@Override
	public List<Appointment> getAllPendingAppointments() {
		return appointmentDao.getAllPendingAppointments();
	}

	@Override
	public List<Appointment> getAllApprovedAppointmentsForVolunteer(int volunteerId) {
		return appointmentDao.getAllApprovedAppointmentsForVolunteer(volunteerId);
	}

	@Override
	public List<Appointment> getAllPendingAppointmentsForVolunteer(int volunteerId) {
		return appointmentDao.getAllPendingAppointmentsForVolunteer(volunteerId);
	}

	@Override
	public List<Appointment> getAllDeclinedAppointmentsForVolunteer(int volunteerId) {
		return appointmentDao.getAllDeclinedAppointmentsForVolunteer(volunteerId);
	}

	@Override
	public List<Appointment> getAllCompletedAppointmentsForVolunteer(int volunteerId) {
		return appointmentDao.getAllCompletedAppointmentsForVolunteer(volunteerId);
	}

	@Override
	public List<Appointment> getAllUpcomingAppointmentsForVolunteer(int volunteerId) {
		return appointmentDao.getAllUpcomingAppointmentsForVolunteer(volunteerId);
	}

	@Override
	public List<Appointment> getAllApprovedAppointmentsForPatient(int patientId) {
		return appointmentDao.getAllApprovedAppointmentsForPatient(patientId);
	}

	@Override
	public List<Appointment> getAllApprovedAppointmentsForPatient(int patientId, int volunteerId) {
		return appointmentDao.getAllApprovedAppointmentsForPatient(patientId, volunteerId);
	}

	@Override
	public List<Appointment> getAllPendingAppointmentsForPatient(int patientId) {
		return appointmentDao.getAllPendingAppointmentsForPatient(patientId);
	}

	@Override
	public List<Appointment> getAllPendingAppointmentsForPatient(int patientId,	int volunteerId) {
		return appointmentDao.getAllPendingAppointmentsForPatient(patientId, volunteerId);
	}

	@Override
	public List<Appointment> getAllDeclinedAppointmentsForPatient(int patientId) {
		return appointmentDao.getAllDeclinedAppointmentsForPatient(patientId);
	}

	@Override
	public List<Appointment> getAllDeclinedAppointmentsForPatient(int patientId, int volunteerId) {
		return appointmentDao.getAllDeclinedAppointmentsForPatient(patientId, volunteerId);
	}

	@Override
	public List<Appointment> getAllUpcommingAppointmentForPatient(int patientId) {
		return appointmentDao.getAllUpcommingAppointmentForPatient(patientId);
	}

	@Override
	public List<Appointment> getAllCompletedAppointmentsForPatient(int patientId) {
		return appointmentDao.getAllCompletedAppointmentsForPatient(patientId);
	}

	@Override
	public Appointment getAppointmentByMostRecentIncomplete(int patientId) {
		return appointmentDao.getAppointmentByMostRecentIncomplete(patientId);
	}

	@Override
	public Appointment getAppointmentById(int appointmentId) {
		return appointmentDao.getAppointmentById(appointmentId);
	}

	@Override
	public List<Appointment> getRemindingAppointmentList(int id, int diff) {
		return appointmentDao.getRemindingAppointmentList(id, diff);
	}

	@Override
	public void completeAppointment(int id, String comments, boolean contactedAdmin) {
		appointmentDao.completeAppointment(id, comments, contactedAdmin);
	}

	@Override
	public void completeAppointment(int appointmentId, String comments) {
		appointmentDao.completeAppointment(appointmentId, comments);
	}

	@Override
	public boolean createAppointment(Appointment a) {
		return appointmentDao.createAppointment(a);
	}

	@Override
	public void approveAppointment(int id) {
		appointmentDao.approveAppointment(id);
	}

	@Override
	public void declineAppointment(int id) {
		appointmentDao.declineAppointment(id);
	}

	@Override
	public void deleteAppointment(int id) {
		appointmentDao.deleteAppointment(id);
	}

	@Override
	public void completeNarrative(int id) {
		appointmentDao.completeNarrative(id);
	}

	@Override
	public List<Appointment> getAppointmentsGroupByOrganization(int organizationId) {
		return appointmentDao.getAppointmentsGroupByOrganization(organizationId);
	}
	
	@Override
	public List<Appointment> getUpcomingAppointmentsGroupByOrganization(int organizationId) {
		return appointmentDao.getUpcomingAppointmentsGroupByOrganization(organizationId);
	}

	@Override
	public List<Appointment> getPastAppointmentsGroupByOrganization(int organizationId){
			return appointmentDao.getPastAppointmentsGroupByOrganization(organizationId);
	}

	@Override
	public List<Appointment> getPendingAppointmentsGroupByOrganization(int organizationId) {
		return appointmentDao.getPendingAppointmentsGroupByOrganization(organizationId);
	}

	@Override
	public void archiveAppointment(Appointment a, String deletedBy) {
		appointmentDao.archiveAppointment(a, deletedBy);				
	}
	
	@Override
	public String getKeyObservationByAppointmentId(int id) {
		return appointmentDao.getKeyObservationByAppointmentId(id);
	}

	@Override
	public boolean addAlertsAndKeyObservations(int id, String alerts, String keyObservations) {
		return appointmentDao.addAlertsAndKeyObservations(id, alerts, keyObservations);
	}

	@Override
	public String getPlanByAppointmentId(int id) {
		return appointmentDao.getPlanByAppointmentId(id);
	}
	
	@Override
	public boolean addPlans(int id, String plan) {
		return appointmentDao.addPlans(id, plan);
	}

	@Override
	public List<Appointment> getAllAppointmentsByVolunteer(int id) {		
		return appointmentDao.getAllAppointmentsByVolunteer(id);
	}

	@Override
	public void completeAppointment(int id) {
		appointmentDao.completeAppointment(id);		
	}

	@Override
	public List<Appointment> getUnCompleteAppointments(int patientId) {
		return appointmentDao.getUnCompleteAppointments(patientId);
	}

	@Override
	public List<Appointment> getAllUnCompletedAppointmentsForPatient(int patientId) {
		return appointmentDao.getAllUnCompletedAppointmentsForPatient(patientId);
	}

	@Override
	public boolean hasAppointmentByVolunteer(int volunteerId, String time) {
		return appointmentDao.hasAppointmentByVolunteer(volunteerId, time);
	}
	
	@Override
	public boolean hasAppointmentByPatient(int patientId, String time) {
		return appointmentDao.hasAppointmentByPatient(patientId, time);
	}

}
