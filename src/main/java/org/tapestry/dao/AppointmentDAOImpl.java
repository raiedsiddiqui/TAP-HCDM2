package org.tapestry.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import org.tapestry.objects.Appointment;

/**
 * An implementation of the AppointmentDAO interface.
 * 
 * lxie
 */
public class AppointmentDAOImpl implements AppointmentDAO {
	private JdbcTemplate jdbcTemplate;
	
	public AppointmentDAOImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
	@Override
	public List<Appointment> getAllAppointments() {
		String sql = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
    				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
    				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
    				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
    				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
    				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
    				+ " ORDER BY a.date_time DESC";
		List<Appointment> appointments = jdbcTemplate.query(sql, new AppointmentMapper());
		return appointments;
	}

	@Override
	public List<Appointment> getAllPastAppointments() {
		String sql = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE a.date_time < CURDATE() ORDER BY a.date_time DESC";
		List<Appointment> appointments = jdbcTemplate.query(sql, new AppointmentMapper());
		return appointments;
	}

	@Override
	public List<Appointment> getAllPendingAppointments() {
		String sql = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE a.date_time>=CURDATE() AND a.completed=0 AND a.status='Awaiting Approval' ORDER BY a.date_time DESC";
		List<Appointment> appointments = jdbcTemplate.query(sql, new AppointmentMapper());
		return appointments;
	}

	@Override
	public List<Appointment> getAllApprovedAppointmentsForVolunteer(int volunteerId) {
		String sql = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE (a.volunteer=? OR a.partner=?) AND a.date_time>=CURDATE() AND a.completed=0 AND a.status='Approved' "
				+ "ORDER BY a.date_time DESC";
		List<Appointment> appointments = jdbcTemplate.query(sql, new Object[]{volunteerId, volunteerId}, new AppointmentMapper());
		return appointments;
	}

	@Override
	public List<Appointment> getAllPendingAppointmentsForVolunteer(int volunteerId) {
		String sql = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE (a.volunteer=? OR a.partner=?) AND a.date_time>=CURDATE() AND a.completed=0 AND a.status='Awaiting Approval' "
				+ "ORDER BY a.date_time DESC";
		List<Appointment> appointments = jdbcTemplate.query(sql, new Object[]{volunteerId, volunteerId}, new AppointmentMapper());
		return appointments;
	}

	@Override
	public List<Appointment> getAllDeclinedAppointmentsForVolunteer(int volunteerId) {
		String sql = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE (a.volunteer=? OR a.partner=?) AND a.date_time>=CURDATE() AND a.completed=0 AND a.status='Declined' "
				+ "ORDER BY a.date_time DESC";
		List<Appointment> appointments = jdbcTemplate.query(sql, new Object[]{volunteerId, volunteerId}, new AppointmentMapper());
		return appointments;
	}

	@Override
	public List<Appointment> getAllCompletedAppointmentsForVolunteer(int volunteerId) {
		String sql = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE (a.volunteer=? OR a.partner=?) AND a.completed=1 ORDER BY a.date_time DESC";
		List<Appointment> appointments = jdbcTemplate.query(sql, new Object[]{volunteerId, volunteerId}, new AppointmentMapper());
		return appointments;
	}

	@Override
	public List<Appointment> getAllUpcomingAppointmentsForVolunteer(int volunteerId) {
		String sql = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE (a.volunteer=? OR a.partner=?) AND a.date_time>=CURDATE() AND a.completed=0 ORDER BY a.date_time DESC";
		List<Appointment> appointments = jdbcTemplate.query(sql, new Object[]{volunteerId, volunteerId}, new AppointmentMapper());
		return appointments;
	}

	@Override
	public List<Appointment> getAllApprovedAppointmentsForPatient(int patientId) {
		String sql = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE a.patient=? AND a.date_time>=CURDATE() AND a.completed=1 ORDER BY a.date_time DESC";
		List<Appointment> appointments = jdbcTemplate.query(sql, new Object[]{patientId}, new AppointmentMapper());
		return appointments;
	}

	@Override
	public List<Appointment> getAllApprovedAppointmentsForPatient(int patientId, int volunteerId) {
		String sql = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE a.patient=? AND (a.volunteer=? OR a.partner=?) AND a.date_time>=CURDATE() AND a.completed=0"
				+ " AND a.status='Approved' ORDER BY a.date_time DESC";
		List<Appointment> appointments = jdbcTemplate.query(sql, new Object[]{patientId,volunteerId,volunteerId}, new AppointmentMapper());
		return appointments;
	}

	@Override
	public List<Appointment> getAllPendingAppointmentsForPatient(int patientId) {
		String sql = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE a.patient=? AND a.date_time>=CURDATE() AND a.completed=0 AND a.status='Awaiting Approval'"
				+ " ORDER BY a.date_time DESC";
		List<Appointment> appointments = jdbcTemplate.query(sql, new Object[]{patientId}, new AppointmentMapper());
		return appointments;
	}

	@Override
	public List<Appointment> getAllPendingAppointmentsForPatient(int patientId,	int volunteerId) {
		String sql = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE a.patient=? AND (a.volunteer=? OR a.partner=?) AND a.date_time>=CURDATE() AND a.completed=0"
				+ " AND a.status='Awaiting Approval' ORDER BY a.date_time DESC";
		List<Appointment> appointments = jdbcTemplate.query(sql, new Object[]{patientId,volunteerId,volunteerId}, new AppointmentMapper());
		return appointments;
	}

	@Override
	public List<Appointment> getAllDeclinedAppointmentsForPatient(int patientId) {
		String sql = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE a.patient=? AND a.date_time>=CURDATE() AND a.completed=0 AND a.status='Declined'"
				+ " ORDER BY a.date_time DESC";
		List<Appointment> appointments = jdbcTemplate.query(sql, new Object[]{patientId}, new AppointmentMapper());
		return appointments;
	}

	@Override
	public List<Appointment> getAllDeclinedAppointmentsForPatient(int patientId, int volunteerId) {
		String sql = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE a.patient=? AND (a.volunteer=? OR a.partner=?) AND a.date_time>=CURDATE() AND a.completed=0 "
				+ "AND a.status='Declined' ORDER BY a.date_time DESC";
		List<Appointment> appointments = jdbcTemplate.query(sql, new Object[]{patientId,volunteerId,volunteerId}, new AppointmentMapper());
		return appointments;
	}

	@Override
	public List<Appointment> getAllUpcommingAppointmentForPatient(int patientId) {
		String sql = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE a.patient=? AND a.date_time>=CURDATE() AND a.completed=0  ORDER BY a.date_time DESC";
		List<Appointment> appointments = jdbcTemplate.query(sql, new Object[]{patientId}, new AppointmentMapper());
		return appointments;
	}

	@Override
	public List<Appointment> getAllCompletedAppointmentsForPatient(int patientId) {
		String sql = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE a.patient=? AND a.completed=1  ORDER BY a.date_time DESC";
		List<Appointment> appointments = jdbcTemplate.query(sql, new Object[]{patientId}, new AppointmentMapper());
		return appointments;
	}

	@Override
	public Appointment getAppointmentByMostRecentIncomplete(int patientId) {
		String sql = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE a.patient=? AND a.completed=0 AND a.status='Approved' ORDER BY a.date_time ASC LIMIT 1";
		return jdbcTemplate.queryForObject(sql, new Object[]{patientId}, new AppointmentMapper());		
	}

	@Override
	public Appointment getAppointmentById(int appointmentId) {
		String sql = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE a.appointment_ID=? ORDER BY date_time DESC";
		return jdbcTemplate.queryForObject(sql, new Object[]{appointmentId}, new AppointmentMapper());		
	}

	@Override
	public List<Appointment> getRemindingAppointmentList(int id, int diff) {		
		String sql_admin = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE TIMESTAMPDIFF(DAY, a.date_time, NOW())=? ORDER BY a.date_time DESC";
		String sql_volunteer = "SELECT a.appointment_ID, a.volunteer, a.patient, DATE(a.date_time) AS appt_date,"
				+ "TIME(a.date_time) AS appt_time, a.comments, a.status, a.completed, a.contactedAdmin, "
				+ "a.partner, a.hasNarrative, a.type, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, p.firstname AS p_firstname, "
				+ "p.lastname AS p_lastname FROM appointments AS a INNER JOIN volunteers AS v1 ON a.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON a.partner=v2.volunteer_ID INNER JOIN patients AS p ON a.patient=p.patient_ID"
				+ " WHERE TIMESTAMPDIFF(DAY, a.date_time, NOW())=? AND (a.volunteer=? OR a.partner=?) AND a.status='Approved' "
				+ "ORDER BY a.date_time DESC";
		
		List<Appointment> appointments = new ArrayList<Appointment>();
		if (id == 0)//for admin
			appointments = jdbcTemplate.query(sql_admin,  new Object[]{diff}, new AppointmentMapper());
		else //volunteer
			appointments = jdbcTemplate.query(sql_volunteer,  new Object[]{diff, id, id}, new AppointmentMapper());
		
		
		return appointments;
	}

	@Override
	public void completeAppointment(int id, String comments, boolean contactedAdmin) {
		String sql = "UPDATE appointments SET comments=?, completed=1, contactedAdmin=? WHERE appointment_ID=? ";
		jdbcTemplate.update(sql, comments, contactedAdmin, id);

	}

	@Override
	public void completeAppointment(int appointmentId, String comments) {
		String sql = "UPDATE appointments SET comments=?, completed=1 WHERE appointment_ID=? ";
		jdbcTemplate.update(sql, comments, appointmentId);

	}

	@Override
	public boolean createAppointment(Appointment a) {		
		String sql = "INSERT INTO appointments (volunteer, patient, date_time, partner, status, type) values (?,?,?,?,?, ?)";
		jdbcTemplate.update(sql, a.getVolunteerID(),a.getPatientID(), a.getDate() + " " + a.getTime(), a.getPartnerId(), 
				"Awaiting Approval", a.getType());
		
		return true;
	}

	@Override
	public void approveAppointment(int id) {
		String sql = "UPDATE appointments SET status='Approved' WHERE appointment_ID=?";
		jdbcTemplate.update(sql, id);
	}

	@Override
	public void declineAppointment(int id) {
		String sql = "UPDATE appointments SET status='Declined' WHERE appointment_ID=?";
		jdbcTemplate.update(sql, id);
	}

	@Override
	public void deleteAppointment(int id) {
		String sql = "DELETE FROM appointments WHERE appointment_ID=?";
		jdbcTemplate.update(sql, id);
	}

	@Override
	public String getKeyObservationByAppointmentId(int id) {
		String sql = "SELECT key_observations FROM appointments WHERE appointment_ID=? ORDER BY date_time ASC";
		List<String> sList = jdbcTemplate.queryForList(sql, new Object[]{id}, String.class);
		
		if (sList.isEmpty())
			return null;
		else
			return sList.get(0);
	}

	@Override
	public boolean addAlertsAndKeyObservations(int id, String alerts,String keyObservations) {
		String sql = "UPDATE appointments SET alerts=?, key_observations=? WHERE appointment_ID=? ";
		jdbcTemplate.update(sql, alerts, keyObservations, id);
		return true;
	}

	@Override
	public String getPlanByAppointmentId(int id) {
		String sql = "SELECT plan FROM appointments WHERE appointment_ID=? ORDER BY date_time ASC";		
		List<String> sList = jdbcTemplate.queryForList(sql, new Object[]{id}, String.class);
		
		if (sList.isEmpty())
			return null;
		else
			return sList.get(0);
	}

	@Override
	public boolean addPlans(int id, String plan) {
		String sql = "UPDATE appointments SET plan=? WHERE appointment_ID=? ";
		jdbcTemplate.update(sql, plan, id);
		
		return true;
	}

	@Override
	public void completeNarrative(int id) {
		String sql = "UPDATE appointments SET hasNarrative=1 WHERE appointment_ID=? ";
		jdbcTemplate.update(sql, id);
	}
	
	class AppointmentMapper implements RowMapper<Appointment> {
		public Appointment mapRow(ResultSet rs, int rowNum) throws SQLException{
			Appointment appointment = new Appointment();
			
			appointment.setAppointmentID(rs.getInt("appointment_ID"));
			appointment.setVolunteerID(rs.getInt("volunteer"));
			
			StringBuffer sb = new StringBuffer();
			sb.append(rs.getString("v1_firstname"));
			sb.append(" ");
			sb.append(rs.getString("v1_lastname"));
			appointment.setVolunteer( sb.toString());
			
			appointment.setPartnerId(rs.getInt("partner"));
			
			sb = new StringBuffer();
			sb.append(rs.getString("v2_firstname"));
			sb.append(" ");
			sb.append(rs.getString("v2_lastname"));			
			appointment.setPartner(sb.toString());
			
			appointment.setPatientID(rs.getInt("patient"));
			
			sb = new StringBuffer();
			sb.append(rs.getString("p_firstname"));
			sb.append(" ");
			sb.append(rs.getString("p_lastname"));
			appointment.setPatient(sb.toString());
			
			appointment.setDate(rs.getString("appt_date"));
			
			String time = rs.getString("appt_time");
			appointment.setTime(time.substring(0, time.length() - 3));
			
			appointment.setComments(rs.getString("comments"));
			appointment.setStatus(rs.getString("status"));
			appointment.setCompleted(rs.getBoolean("completed"));
			appointment.setContactedAdmin(rs.getBoolean("contactedAdmin"));
			appointment.setHasNarrative(rs.getBoolean("hasNarrative")); 
			int type = rs.getInt("type");
			appointment.setType(type);
    		
    		if (type == 0)
    			appointment.setStrType("First Visit");
    		else
    			appointment.setStrType("Follow up Visit");
		
			
			return appointment;
		}
	}
	

}