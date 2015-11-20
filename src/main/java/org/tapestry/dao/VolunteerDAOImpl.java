package org.tapestry.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.tapestry.objects.Organization;
import org.tapestry.objects.Volunteer;

/**
 * An implementation of the VolunteerDAO interface.
 * 
 * lxie
 */
@Repository
public class VolunteerDAOImpl extends JdbcDaoSupport implements VolunteerDAO {
	@Autowired
	public VolunteerDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
    }

	@Override
	public List<Volunteer> getAllVolunteers() {
		String sql = "SELECT v.*, o.name FROM volunteers AS v INNER JOIN organizations AS o "
				+ "ON v.organization=o.organization_ID ORDER BY v.firstname DESC";
		return getJdbcTemplate().query(sql, new VolunteerMapper());
	}

	@Override
	public List<Volunteer> getVolunteersWithAvailability() {
		String sql = "SELECT v.*, o.name FROM volunteers AS v INNER JOIN organizations AS o "
				+ "ON v.organization=o.organization_ID WHERE "
				+ "v.availability <> '1non,2non,3non,4non,5non' ORDER BY v.firstname DESC";
		return getJdbcTemplate().query(sql, new VolunteerMapper());
	}

	@Override
	public List<Volunteer> getVolunteersByName(String partialName) {	
		String sql = "SELECT v.*, o.name FROM volunteers AS v INNER JOIN organizations AS o ON v.organization=o.organization_ID WHERE "
				+ "UPPER(v.firstname) LIKE UPPER('%" + partialName + "%') OR UPPER(v.lastname) LIKE UPPER('%" + partialName + "%') "
				+ "OR UPPER(v.preferredname) LIKE UPPER('%" + partialName + "%') ORDER BY v.firstname DESC";
		return getJdbcTemplate().query(sql, new VolunteerMapper());
	}

	@Override
	public List<Volunteer> getAllVolunteersByOrganization(int id) {
		String sql = "SELECT v.*, o.name FROM volunteers AS v INNER JOIN organizations AS o "
				+ "ON v.organization=o.organization_ID WHERE v.organization=? ORDER BY v.firstname DESC";
		return getJdbcTemplate().query(sql, new Object[]{id}, new VolunteerMapper());
	}
	
	@Override
	public List<Volunteer> getGroupedVolunteersByName(String partialName, int organizationId) {
		String sql = "SELECT v.*, o.name FROM volunteers AS v INNER JOIN organizations AS o "
				+ "ON v.organization=o.organization_ID WHERE v.organization=? AND UPPER(v.firstname) LIKE UPPER('%" + partialName + "%') "
				+ "OR UPPER(v.lastname) LIKE UPPER('%" + partialName + "%') OR UPPER(v.preferredname) LIKE UPPER('%" + partialName + "%') "
				+"ORDER BY v.firstname DESC";
		return getJdbcTemplate().query(sql, new Object[]{organizationId}, new VolunteerMapper());
	}


	@Override
	public Volunteer getVolunteerById(int id) {
		String sql = "SELECT v.*, o.name FROM volunteers AS v INNER JOIN organizations AS o "
				+ "ON v.organization=o.organization_ID WHERE v.volunteer_ID = ?";
		return getJdbcTemplate().queryForObject(sql, new Object[]{id}, new VolunteerMapper());
	}

	@Override
	public int getVolunteerIdByUsername(String username) {
		String sql = "SELECT v.*, o.name FROM volunteers AS v INNER JOIN organizations AS o "
				+ "ON v.organization=o.organization_ID WHERE v.username = ? ORDER BY v.firstname DESC";
		Volunteer vol = getJdbcTemplate().queryForObject(sql, new Object[]{username}, new VolunteerMapper());
		return vol.getVolunteerId();
	}

	@Override
	public List<String> getAllExistUsernames() {
		String sql = "SELECT username FROM volunteers";
		return  getJdbcTemplate().queryForList(sql, String.class);
	}

	@Override
	public boolean addVolunteer(Volunteer v) {
		String sql = "INSERT INTO volunteers (firstname, lastname, street,"
				+ "username, email, experience_level, city, province, home_phone, cell_phone,"
				+ "postal_code, country, emergency_contact, emergency_phone, appartment, notes,"
				+ " availability, street_number, password, organization, gender, total_vlc_score, "
				+ "number_years_experience, availability_per_month, technology_skills_score, "
				+ "perception_older_adult_score, vlc_ID, "
				+ "interview_date, date_of_birth, mature_or_student, reference1, "
				+ "reference2, reference_check_date, police_check_received_date, tb_test_received_date,vlc_completion_date, "
				+ "confidentiality_agreement_signed_date, volunteer_agreement_signed_date, photo_received_date, source, "
				+ "application_form_completion_date, background_info, vol_experience, tech_comfort, language_info, "
				+ "application_completed, interview_completed, reference_completed, police_completed, "
				+ "tb_completed, vlc_completed, confidentiality_completed, volagreement_completed, photo_received, "
				+ "current_status, reason_inactivity) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
				+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		getJdbcTemplate().update(sql, v.getFirstName(), v.getLastName(), v.getStreet(), 
				v.getUserName(),	v.getEmail(),v.getExperienceLevel(), v.getCity(), v.getProvince(), v.getHomePhone(),	v.getCellPhone(), 
				v.getPostalCode(), v.getCountry(), v.getEmergencyContact(), v.getEmergencyPhone(), v.getAptNumber(), v.getNotes(), 
				v.getAvailability(), 	v.getStreetNumber(), v.getPassword(), v.getOrganizationId(), v.getGender(), v.getTotalVLCScore(), 
				v.getNumYearsOfExperience(), v.getAvailabilityPerMonth(), v.getTechnologySkillsScore(), 
				v.getPerceptionOfOlderAdultsScore(), v.getvLCID(),				
				v.getInterviewDate(), v.getdOB(),v.getStatus(),  
				v.getReference1(), v.getReference2(), v.getReferenceCheckDate(), v.getPoliceCheckDate(),v.gettBTestDate(), 
				v.getvLCCompletionDate(), v.getcAgreementDate(), v.getvAgreementDate(), v.getPhotoDate(), v.getSource(), 
				v.getApplicationFormCompletionDate(), v.getBackground(), v.getVolExperience(), v.getTechComfort(), v.getLanguage(),
				v.getApplicationCompleted(), v.getInterviewCompleted(), v.getReferenceCompleted(), v.getPoliceCompleted(),
				v.getTbCompleted(), v.getVlcCompleted(), v.getConfidentialityCompleted(), v.getVolagreementCompleted(),
				v.getPhotoReceived(), v.getCurrentStatus(), v.getReasonInactivity());
		
		return true;
	}

	@Override
	public void updateVolunteer(Volunteer v) {
		String sql = "UPDATE volunteers SET firstname=?,lastname=?, username=?, street=?,"
				+ "email=?, experience_level=?, city=?, province=?, home_phone=?, cell_phone=?,"
				+ "postal_code=?, country=?, emergency_contact=?, emergency_phone=?, appartment=?, "
				+ "notes=?, availability=?, street_number=?, organization=?, gender=?, "
				+ "total_vlc_score=?, number_years_experience=?, availability_per_month=?, "
				+ "technology_skills_score=?, perception_older_adult_score=?, vlc_ID=?, interview_date=?, "
				+ "date_of_birth=?, mature_or_student=?, reference1=?, reference2=?, reference_check_date=?, "
				+ "police_check_received_date=?, tb_test_received_date=?, vlc_completion_date=?, "
				+ "confidentiality_agreement_signed_date=?, volunteer_agreement_signed_date=?, "
				+ "photo_received_date=?, source=?, application_form_completion_date=?, "
				+ "background_info=?, vol_experience=?, tech_comfort=?, language_info=?, "
				+ "application_completed=?, interview_completed=?, reference_completed=?, police_completed=?, "
				+ "tb_completed=?, vlc_completed=?, confidentiality_completed=?, volagreement_completed=?, "
				+ "photo_received=?, current_status=?, reason_inactivity=? WHERE volunteer_ID=?";
		
		getJdbcTemplate().update(sql, v.getFirstName(), v.getLastName(), v.getUserName(), v.getStreet(), 
				v.getEmail(), v.getExperienceLevel(), v.getCity(), v.getProvince(), v.getHomePhone(),
				v.getCellPhone(), v.getPostalCode(), v.getCountry(), v.getEmergencyContact(), 
				v.getEmergencyPhone(), v.getAptNumber(), v.getNotes(), v.getAvailability(), v.getStreetNumber(), 				
				v.getOrganizationId(), v.getGender(), v.getTotalVLCScore(), v.getNumYearsOfExperience(),
				v.getAvailabilityPerMonth(), v.getTechnologySkillsScore(), v.getPerceptionOfOlderAdultsScore(),
				v.getvLCID(), v.getInterviewDate(), v.getdOB(), v.getStatus(), v.getReference1(), v.getReference2(), 
				v.getReferenceCheckDate(), v.getPoliceCheckDate(),v.gettBTestDate(), v.getvLCCompletionDate(), 
				v.getcAgreementDate(), v.getvAgreementDate(), v.getPhotoDate(), v.getSource(), 
				v.getApplicationFormCompletionDate(), 
				v.getBackground(), v.getVolExperience(), v.getTechComfort(), v.getLanguage(),
				v.getApplicationCompleted(), v.getInterviewCompleted(), v.getReferenceCompleted(), v.getPoliceCompleted(),
				v.getTbCompleted(), v.getVlcCompleted(), v.getConfidentialityCompleted(), v.getVolagreementCompleted(),
				v.getPhotoReceived(), v.getCurrentStatus(), v.getReasonInactivity(), v.getVolunteerId());		
	}
	
	@Override
	public void updateVolunteerAvalability(int volunteerId, String availablitly) {
		String sql = "UPDATE volunteers SET availability=? WHERE volunteer_ID=?";
		getJdbcTemplate().update(sql, availablitly, volunteerId);		
	}	

	@Override
	public void deleteVolunteerById(int id) {
		String sql = "DELETE FROM volunteers WHERE volunteer_ID=?";
		getJdbcTemplate().update(sql, new Object[]{id});
	}

	@Override
	public int countAllVolunteers() {
		String sql = "SELECT COUNT(*) as c FROM volunteers";
		return getJdbcTemplate().queryForInt(sql);
	}

//	@Override
//	public String getVolunteerNameById(int volunteerId) {
//		Volunteer volunteer = getVolunteerById(volunteerId);		
//		return volunteer.getDisplayName();
//	}

	@Override
	public int getUserIdByVolunteerId(int volunteerId) {
		String sql = "SELECT users.user_ID FROM users INNER JOIN volunteers ON "
				+ "users.username = volunteers.username WHERE volunteers.volunteer_ID = ?";
		
		return getJdbcTemplate().queryForInt(sql, new Object[]{volunteerId});		 
	}

	@Override
	public List<Organization> getAllOrganizations() {
		String sql = "SELECT * FROM organizations ORDER BY name DESC ";
		return getJdbcTemplate().query(sql, new OrganizationMapper());
	}

	@Override
	public Organization getOrganizationById(int id) {
		String sql = "SELECT * FROM organizations WHERE organization_ID = ? ORDER BY name DESC ";
		return getJdbcTemplate().queryForObject(sql, new Object[]{id}, new OrganizationMapper());
	}

	@Override
	public List<Organization> getOrganizationsByName(String partialName) {
		String sql = "SELECT * FROM organizations WHERE UPPER(name) LIKE UPPER('%" + partialName + "%')";
		return getJdbcTemplate().query(sql, new OrganizationMapper());
	}

	@Override
	public boolean addOrganization(Organization organization) {
		String sql = "INSERT INTO organizations (name, street_number, street, city, province,"
				+ "country, postal_code, primary_contact, primary_phone, secondary_contact, secondary_phone) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		getJdbcTemplate().update(sql, organization.getName(), organization.getStreetNumber(), organization.getStreetName(),
				organization.getCity(), organization.getProvince(), organization.getCountry(), organization.getPostCode(), 
				organization.getPrimaryContact(), organization.getPrimaryPhone(), organization.getSecondaryContact(),
				organization.getSecondaryPhone());
	
		return true;
	}

	@Override
	public void updateOrganization(Organization organization) {
		String sql = "UPDATE organizations SET name=?, street_number=?, street=?, city=?, "
				+ "province=?,country=?, postal_code=?, primary_contact=?, primary_phone=?, secondary_contact=?, "
				+ "secondary_phone=? WHERE organization_ID=?";
		getJdbcTemplate().update(sql, organization.getName(), organization.getStreetNumber(), organization.getStreetName(),
				organization.getCity(), organization.getProvince(), organization.getCountry(), organization.getPostCode(), 
				organization.getPrimaryContact(), organization.getPrimaryPhone(), organization.getSecondaryContact(),
				organization.getSecondaryPhone(), organization.getOrganizationId());
	}

	@Override
	public void deleteOrganizationById(int id) {
		String sql = "DELETE FROM organizations WHERE organization_ID=?";
		getJdbcTemplate().update(sql, id);

	}
	
	@Override
	public void archiveVolunteer(Volunteer volunteer, String deletedBy) {
		String sql = "INSERT INTO volunteers_archive (firstname, lastname, street,"
				+ "username, email, experience_level, city, province, home_phone, cell_phone,"
				+ "postal_code, country, emergency_contact, emergency_phone, appartment,"
				+ " street_number, organization, vlc_ID, deleted_by, deleted_volunteer_ID) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		getJdbcTemplate().update(sql, volunteer.getFirstName(), volunteer.getLastName(), volunteer.getStreet(), volunteer.getUserName(),
				volunteer.getEmail(),volunteer.getExperienceLevel(), volunteer.getCity(), volunteer.getProvince(), volunteer.getHomePhone(),
				volunteer.getCellPhone(), volunteer.getPostalCode(), volunteer.getCountry(), volunteer.getEmergencyContact(), 
				volunteer.getEmergencyPhone(), volunteer.getAptNumber(), volunteer.getStreetNumber(), volunteer.getOrganizationId(), 
				volunteer.getvLCID(), deletedBy, volunteer.getVolunteerId());		
	}	

	@Override
	public void archiveOrganization(Organization organization, String deletedBy) {
		String sql = "INSERT INTO organizations_archive (name, street_number, street, city, province,"
				+ "country, postal_code, primary_contact, primary_phone, secondary_contact, secondary_phone, "
				+ "deleted_by, deleted_organization_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		getJdbcTemplate().update(sql, organization.getName(), organization.getStreetNumber(), organization.getStreetName(),
				organization.getCity(), organization.getProvince(), organization.getCountry(), organization.getPostCode(), 
				organization.getPrimaryContact(), organization.getPrimaryPhone(), organization.getSecondaryContact(),
				organization.getSecondaryPhone(), deletedBy, organization.getOrganizationId());
		
	}
	

	class VolunteerMapper implements RowMapper<Volunteer>{
		public Volunteer mapRow(ResultSet rs, int rowNum) throws SQLException{
			
			Volunteer vol = new Volunteer();
			
			vol.setVolunteerId(rs.getInt("volunteer_ID"));
			
			String firstName = rs.getString("firstname");
			vol.setFirstName(firstName);
			String lastName = rs.getString("lastname");
			vol.setLastName(lastName);
			
			String level = rs.getString("experience_level");
			
			StringBuffer sb = new StringBuffer();
			sb.append(firstName);
			sb.append(" ");
			sb.append(lastName);
			sb.append("(");
			sb.append(level);
			sb.append(")");
			
			vol.setDisplayName(sb.toString());
			vol.setUserName(rs.getString("username"));
			vol.setEmail(rs.getString("email"));
			if (level.equals("E"))
				vol.setExperienceLevel("Experienced");
			else if (level.equals("B"))
				vol.setExperienceLevel("Beginer");
			else if (level.equals("I"))
				vol.setExperienceLevel("Intermediate");
			
			vol.setCity(rs.getString("city"));
			vol.setProvince(rs.getString("province"));
			vol.setHomePhone(rs.getString("home_phone"));
			vol.setCellPhone(rs.getString("cell_phone"));
			vol.setStreet(rs.getString("street"));
			vol.setStreetNumber(rs.getString("street_number"));
			vol.setAptNumber(rs.getString("appartment"));
			vol.setCountry(rs.getString("country"));
			vol.setEmergencyContact(rs.getString("emergency_contact"));
			vol.setEmergencyPhone(rs.getString("emergency_phone"));
			vol.setPostalCode(rs.getString("postal_code"));
			vol.setNotes(rs.getString("notes"));
			vol.setAvailability(rs.getString("availability"));
			vol.setOrganizationId(rs.getInt("organization"));
			vol.setOrganization(rs.getString("name"));
			vol.setGender(rs.getString("gender"));
			vol.setTechnologySkillsScore(rs.getDouble("technology_skills_score"));			
			vol.setTotalVLCScore(rs.getDouble("total_vlc_score"));
			vol.setPerceptionOfOlderAdultsScore(rs.getDouble("perception_older_adult_score"));
			vol.setAvailabilityPerMonth(rs.getDouble("availability_per_month"));
			vol.setNumYearsOfExperience(rs.getDouble("number_years_experience"));
			vol.setvLCID(rs.getInt("vlc_ID"));
			
			vol.setBackground(rs.getString("background_info"));
			vol.setVolExperience(rs.getString("vol_experience"));
			vol.setTechComfort(rs.getInt("tech_comfort"));
			vol.setLanguage(rs.getInt("language_info"));
			vol.setApplicationCompleted(rs.getInt("application_completed"));
			vol.setInterviewCompleted(rs.getInt("interview_completed"));
			vol.setReferenceCompleted(rs.getInt("reference_completed"));
			vol.setPoliceCompleted(rs.getInt("police_completed"));
			vol.setTbCompleted(rs.getInt("tb_completed"));
			vol.setVlcCompleted(rs.getInt("vlc_completed"));
			vol.setConfidentialityCompleted(rs.getInt("confidentiality_completed"));
			vol.setVolagreementCompleted(rs.getInt("volagreement_completed"));
			vol.setPhotoReceived(rs.getInt("photo_received"));
			vol.setCurrentStatus(rs.getInt("current_status"));
			vol.setReasonInactivity(rs.getString("reason_inactivity"));
			
			String date = rs.getString("interview_date");
			if (date != null)
				vol.setInterviewDate(date);
			vol.setStatus(rs.getString("mature_or_student"));
			
			date = rs.getString("date_of_birth");
			if (date != null)
				vol.setdOB(date);
			vol.setReference1(rs.getString("reference1"));
			vol.setReference2(rs.getString("reference2"));
			
			date = rs.getString("reference_check_date");
			if (date != null)
				vol.setReferenceCheckDate(date);
			
			date = rs.getString("police_check_received_date");
			if (date != null)
				vol.setPoliceCheckDate(date);
			
			date = rs.getString("tb_test_received_date");
			if (date != null)
				vol.settBTestDate(date);
			
			date = rs.getString("vlc_completion_date");
			if (date != null)
				vol.setvLCCompletionDate(date);
			
			date = rs.getString("confidentiality_agreement_signed_date");
			if (date != null)
				vol.setcAgreementDate(date);
			
			date = rs.getString("volunteer_agreement_signed_date");
			if (date != null)
				vol.setvAgreementDate(date);
			
			date = rs.getString("photo_received_date");
			if (date != null)
				vol.setPhotoDate(date);
			
			date = rs.getString("application_form_completion_date");
			if (date != null)
				vol.setApplicationFormCompletionDate(date);					
						
			vol.setSource(rs.getString("source"));
			
			return vol;
			
		}
	}
	
	class OrganizationMapper implements RowMapper<Organization>{
		public Organization mapRow(ResultSet rs, int rowNum) throws SQLException{
			
			Organization organization = new Organization();
			
			organization.setOrganizationId(rs.getInt("organization_ID"));
			organization.setName(rs.getString("name"));
			organization.setStreetNumber(rs.getString("street_number"));
			organization.setStreetName(rs.getString("street"));
			organization.setCity(rs.getString("city"));
			organization.setProvince(rs.getString("province"));
			organization.setPostCode(rs.getString("postal_code"));
			organization.setPrimaryContact(rs.getString("primary_contact"));
			organization.setPrimaryPhone(rs.getString("primary_phone"));
			organization.setSecondaryContact(rs.getString("secondary_contact"));
			organization.setSecondaryPhone(rs.getString("secondary_phone"));
			
			return organization;
			
		}
	}

	
//	private String truncateDate(String date)
//	{
//		if (date != null)
//			date = date.substring(0,10);
//		return date;				
//	}




}
