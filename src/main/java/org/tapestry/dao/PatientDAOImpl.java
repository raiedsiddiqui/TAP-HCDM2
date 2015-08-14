package org.tapestry.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.tapestry.objects.Patient;

/**
 * An implementation of the PatientDAO interface.
 * 
 * lxie
 */
@Repository
public class PatientDAOImpl extends NamedParameterJdbcDaoSupport implements PatientDAO {
	@Autowired
	public PatientDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
    }
	
	@Override
	public Patient getPatientByID(int id) {
		String sql = "SELECT p.*, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, v1.organization, "
				+ "c.clinic_name FROM patients AS p INNER JOIN volunteers AS v1 ON p.volunteer=v1.volunteer_ID "
				+ "INNER JOIN volunteers AS v2 ON p.volunteer2=v2.volunteer_ID INNER JOIN clinics AS c "
				+ "ON p.clinic=c.clinic_ID WHERE p.patient_ID=? AND p.enabled=1";
		
		return getJdbcTemplate().queryForObject(sql, new Object[]{id}, new PatientMapper());
	}

	@Override
	public List<Patient> getAllPatients() {
		String sql = "SELECT p.*, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, v1.organization, v1.organization, "
				+ "c.clinic_name FROM patients AS p INNER JOIN volunteers AS v1 ON p.volunteer=v1.volunteer_ID INNER JOIN "
				+ "volunteers AS v2 ON p.volunteer2=v2.volunteer_ID INNER JOIN clinics AS c ON p.clinic=c.clinic_ID WHERE p.enabled=1";
		
		return getJdbcTemplate().query(sql, new PatientMapper());
	}

	@Override
	public List<Patient> getPatientsForVolunteer(int volunteerId) {		
		String sql = "SELECT p.*, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, v1.organization, c.clinic_name FROM patients "
				+ "AS p INNER JOIN volunteers AS v1 ON p.volunteer=v1.volunteer_ID INNER JOIN "
				+ "volunteers AS v2 ON p.volunteer2=v2.volunteer_ID INNER JOIN clinics AS c ON p.clinic=c.clinic_ID "
				+ "WHERE (p.volunteer=? OR p.volunteer2=?) AND p.enabled=1 ";
		
		return getJdbcTemplate().query(sql, new Object[]{volunteerId, volunteerId}, new PatientMapper());
	}
	
	@Override
	public List<Patient> getPatientsByPartialName(String partialName) {	
		String sql = "SELECT p.*, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, v1.organization, c.clinic_name FROM patients "
				+ "AS p INNER JOIN volunteers AS v1 ON p.volunteer=v1.volunteer_ID INNER JOIN "
				+ "volunteers AS v2 ON p.volunteer2=v2.volunteer_ID INNER JOIN clinics AS c ON p.clinic=c.clinic_ID WHERE (UPPER(p.firstname) "
				+ "LIKE UPPER('%" + partialName + "%') OR UPPER(p.lastname) LIKE UPPER('%" + partialName + "%')) AND p.enabled=1 ";
		
		return getJdbcTemplate().query(sql, new PatientMapper());
	}

	@Override
	public List<Patient> getGroupedPatientsByName(String partialName, int organizationId) {
		String sql = "SELECT p.*, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, v1.organization, c.clinic_name FROM patients "
				+ "AS p INNER JOIN volunteers AS v1 ON p.volunteer=v1.volunteer_ID INNER JOIN "
				+ "volunteers AS v2 ON p.volunteer2=v2.volunteer_ID INNER JOIN clinics AS c ON p.clinic=c.clinic_ID WHERE (UPPER(p.firstname) "
				+ "LIKE UPPER('%" + partialName + "%') OR UPPER(p.lastname) LIKE UPPER('%" + partialName + "%')) "
				+ "AND v1.organization =? AND p.enabled=1";
		
		return getJdbcTemplate().query(sql, new Object[]{organizationId}, new PatientMapper());
	}

	@Override
	public List<Patient> getPatientsBySite(int siteId) {
		String sql = "SELECT p.*, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname, v1.organization, c.clinic_name FROM patients "
				+ "AS p INNER JOIN volunteers AS v1 ON p.volunteer=v1.volunteer_ID INNER JOIN "
				+ "volunteers AS v2 ON p.volunteer2=v2.volunteer_ID INNER JOIN clinics AS c ON p.clinic=c.clinic_ID "
				+ "WHERE c.site_ID =? AND p.enabled=1";
		
		return getJdbcTemplate().query(sql, new Object[]{siteId}, new PatientMapper());
	}
	
	@Override
	public int createPatient(final Patient p) {	
		 final String sql = "INSERT INTO patients (firstname, lastname, preferredname, volunteer,"
					+ " gender, notes, volunteer2, alerts, myoscar_verified, clinic, username, mrp, "
					+ "mrp_firstname, mrp_lastname, research_ID, enabled) VALUES (?, ?, ?, ?, ?, ?, ?,"
					+ " ?, ?, ?, ?, ?, ?, ?, ?, 1)";
		  
		 JdbcTemplate jdbcTemplate = getJdbcTemplate();  
		 KeyHolder keyHolder = new GeneratedKeyHolder();  
		  
		 jdbcTemplate.update(new PreparedStatementCreator() {  
			 public PreparedStatement createPreparedStatement(Connection con) throws SQLException {  
				 PreparedStatement ps = con.prepareStatement(sql, new String[]{"patient_ID"});  
				 
				 ps.setString(1, p.getFirstName());  
				 ps.setString(2, p.getLastName());  
				 ps.setString(3, p.getPreferredName());
				 ps.setInt(4, p.getVolunteer());
				 ps.setString(5, p.getGender());
				 ps.setString(6, p.getNotes());
				 ps.setInt(7, p.getPartner());
				 ps.setString(8, p.getAlerts());
				 ps.setString(9, p.getMyoscarVerified());
				 ps.setInt(10,  p.getClinic());
				 ps.setString(11, p.getUserName());//user name in myoscar
				 ps.setInt(12, p.getMrp());
				 ps.setString(13,  p.getMrpFirstName());
				 ps.setString(14, p.getMrpLastName());
				 ps.setString(15, p.getResearchID());
			        
				 return ps;  
			 }  
		 }, keyHolder);  		  
		 return keyHolder.getKey().intValue();  
	}

	@Override
	public void updatePatient(Patient p) {
		String sql = "UPDATE patients SET firstname=?, lastname=?, preferredname=?, volunteer=?, "
				+ "gender=?, notes=?, clinic=?, myoscar_verified=?, alerts=?, volunteer2=?, mrp=?, "
				+ "mrp_firstname=?, mrp_lastname=?, research_ID=? WHERE patient_ID=?";
		getJdbcTemplate().update(sql, p.getFirstName(),  p.getLastName(), p.getPreferredName(), p.getVolunteer(), 
				p.getGender(), p.getNotes(), p.getClinic(), p.getMyoscarVerified(), p.getAlerts(), p.getPartner(), 
				p.getMrp(), p.getMrpFirstName(), p.getMrpLastName(), p.getResearchID(), p.getPatientID());

	}
	
	@Override
	public void authenticatePHRPatientByID(int id) {
		String sql = "UPDATE patients SET myoscar_verified=? WHERE patient_ID=? ";
		getJdbcTemplate().update(sql, "1", id);
		
	}

	@Override
	public void deletePatientWithId(int id) {
		String sql = "DELETE FROM patients WHERE patient_ID=?";
		getJdbcTemplate().update(sql, id);
	}
	
	@Override
	public boolean addKeyObservations(int id, String keyObservations) {
		String sql = "UPDATE patients SET key_observations=? WHERE patient_ID=? ";
		getJdbcTemplate().update(sql, keyObservations, id);
		return true;
	}

	@Override
	public String getPlanByPatientId(int id) {
		String sql = "SELECT plan FROM patients WHERE patient_ID=? ";		
		List<String> sList = getJdbcTemplate().queryForList(sql, new Object[]{id}, String.class);
		
		if (sList.isEmpty())
			return null;
		else
			return sList.get(0);
	}

	@Override
	public boolean addPlans(int id, String plan) {
		String sql = "UPDATE patients SET plan=? WHERE patient_ID=? ";
		getJdbcTemplate().update(sql, plan, id);
		
		return true;
	}
	
	@Override
	public void disablePatientWithID(int id){
		String sql = "UPDATE patients SET enabled=0 WHERE patient_ID=?";
		getJdbcTemplate().update(sql, id);
	}
	
	@Override
	public void enablePatientWithID(int id){
		String sql = "UPDATE patients SET enabled=1 WHERE patient_ID=?";
		getJdbcTemplate().update(sql, id);
	}

	@Override
	public List<String> getResearchIds() {
		String sql = "SELECT research_ID FROM patients";
		return getJdbcTemplate().queryForList(sql, String.class);		 
	}

	class PatientMapper implements RowMapper<Patient> {
		public Patient mapRow(ResultSet rs, int rowNum) throws SQLException{
			Patient patient = new Patient();			
			
			patient.setPatientID(rs.getInt("patient_ID"));
			patient.setFirstName(rs.getString("firstname"));
			patient.setLastName(rs.getString("lastname"));
			patient.setPreferredName(rs.getString("preferredname"));
			//set gender
			String gender = rs.getString("gender");
			if ("M".equals(gender))
				patient.setGender("Male");
			else if ("F".equals(gender))
				patient.setGender("Female");
			else
				patient.setGender("Other");
			//set clinic			
			patient.setClinic(rs.getInt("clinic"));
			patient.setClinicName(rs.getString("clinic_name"));			
			patient.setVolunteer(rs.getInt("volunteer"));
			patient.setNotes(rs.getString("notes"));
			patient.setAlerts(rs.getString("alerts"));			
			patient.setMrp(rs.getInt("mrp"));
			patient.setMrpFirstName(rs.getString("mrp_firstname"));
			patient.setMrpLastName(rs.getString("mrp_lastname"));
			patient.setResearchID(rs.getString("research_ID"));			
			
			String myOscarVerfied = rs.getString("myoscar_verified");
			patient.setMyoscarVerified(myOscarVerfied);    
			//set myoscar authentication for display in client's detail page
			if ("1".equals(myOscarVerfied))
				patient.setMyOscarAuthentication("Authenticated");
			else
				patient.setMyOscarAuthentication("Not Authenticated");
			patient.setPartner(rs.getInt("volunteer2"));
			//Set volunteer name and partner name
			StringBuffer sb = new StringBuffer();
			sb.append(rs.getString("v1_firstname"));
			sb.append(" ");
			sb.append(rs.getString("v1_lastname"));
			patient.setVolunteerName(sb.toString());
			sb = new StringBuffer();
			sb.append(rs.getString("v2_firstname"));
			sb.append(" ");
			sb.append(rs.getString("v2_lastname"));
			patient.setPartnerName(sb.toString());
			patient.setUserName(rs.getString("username"));//user name in MyOscar		
			patient.setGroup(rs.getInt("organization")); //group by volunteer's organization			
			if (rs.getString("enabled").equals("1"))
				patient.setEnabled(true);
			else
				patient.setEnabled(false);
			
			return patient;
		}
	}


}
