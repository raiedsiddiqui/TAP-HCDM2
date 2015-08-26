package org.tapestry.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.tapestry.objects.Narrative;
/**
 * An implementation of the NarrativeDAO interface.
 * 
 * lxie
 */
@Repository
public class NarrativeDAOImpl extends JdbcDaoSupport implements NarrativeDAO {
	
	@Autowired
	public NarrativeDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
    }

	@Override
	/**
	 * List all narratives which is created by volunteerId
	* @param volunteerId
	* @return A list of Narrative
	 */
	public List<Narrative> getAllNarrativesByUser(int volunteerId) {
		String sql = "SELECT narratives.*, patients.firstname, patients.lastname FROM narratives INNER JOIN patients "
				+ "ON narratives.patient_ID = patients.patient_ID WHERE narratives.volunteer=? AND patients.enabled=1 "
				+ "ORDER BY narratives.edit_Date DESC ";
		List<Narrative> narratives = getJdbcTemplate().query(sql, new Object[]{volunteerId}, new NarrativeMapper());
		
		return narratives;
	}

	/**
	 * List all narratives 
	* @param volunteerId, patientId, appointmentId
	* @return A list of Narrative Objects
	 */
	@Override
	public List<Narrative> getNarrativesByVolunteer(int volunteerId, int patientId, int appointmentId) {
		String sql = "SELECT narratives.*, patients.firstname, patients.lastname FROM narratives INNER JOIN patients "
				+ "ON narratives.patient_ID = patients.patient_ID WHERE narratives.volunteer=? AND patients.enabled=1 "
				+ "AND narratives.patient_ID=? AND narratives.appointment=? ORDER BY narratives.edit_Date DESC ";
		List<Narrative> narratives = getJdbcTemplate().query(sql, new Object[]{volunteerId, patientId, appointmentId}, new NarrativeMapper());	
		return narratives;
	}
	
	/**
	 * A Narrative 
	* @param volunteerId
	* @return A Narrative Object
	 */

	@Override
	public Narrative getNarrativeById(int narrativeId) {
		String sql = "SELECT n.*, p.firstname, p.lastname FROM narratives AS n INNER JOIN patients AS p "
				+ "ON n.patient_ID = p.patient_ID WHERE n.narrative_ID=? AND p.enabled=1 ";
		
		return getJdbcTemplate().queryForObject(sql, new Object[]{narrativeId}, new NarrativeMapper());
	}
	
	
	@Override
	public Narrative getNarrativeByAppointmentId(int appointmentId) {
		String sql = "SELECT n.*, p.firstname, p.lastname FROM narratives AS n INNER JOIN patients AS p "
				+ "ON n.patient_ID = p.patient_ID WHERE n.appointment=? AND p.enabled=1";
		
		return getJdbcTemplate().queryForObject(sql, new Object[]{appointmentId}, new NarrativeMapper());
	}
	

	@Override
	public void addNarrative(Narrative narrative) {		
		String sql = "INSERT INTO narratives (title, contents, edit_Date, patient_ID, appointment, "
				+ "volunteer) VALUES (?, ?, ?, ?, ?, ?)";
		
		getJdbcTemplate().update(sql, narrative.getTitle(), narrative.getContents(), narrative.getEditDate(), 
				narrative.getPatientId(), narrative.getAppointmentId(), narrative.getVolunteerId());
	}

	@Override
	public void updateNarrative(Narrative narrative) {		
		String sql = "UPDATE narratives SET title=?,contents=?,edit_Date=? WHERE narrative_ID=?";
		
		getJdbcTemplate().update(sql, narrative.getTitle(), narrative.getContents(), narrative.getEditDate()
				,narrative.getNarrativeId());

	}

	@Override
	public void deleteNarrativeById(int narrativeId) {
		String sql = "DELETE FROM narratives WHERE narrative_ID=?";
		getJdbcTemplate().update(sql, narrativeId);

	}
	
	@Override
	public void archiveNarrative(Narrative n, String updatedBy,	String whatAction) {
		String sql = "INSERT INTO narratives_archive (title, contents, last_edit, patient_ID, appointment_ID, "
				+ "volunteer_ID, updated_narrative_ID, update_action, updated_by) VALUES (?,?,?,?,?,?,?,?,?)";
		
		getJdbcTemplate().update(sql, n.getTitle(), n.getContents(), n.getEditDate(), n.getPatientId(), 
				n.getAppointmentId(), n.getVolunteerId(), n.getNarrativeId(), whatAction, updatedBy);
	}
	
	class NarrativeMapper implements RowMapper<Narrative> {
		public Narrative mapRow(ResultSet rs, int rowNum) throws SQLException{
			Narrative narrative = new Narrative();
		
			narrative.setNarrativeId(rs.getInt("narrative_ID"));			
			narrative.setTitle(rs.getString("title"));
			narrative.setContents(rs.getString("contents"));
			
			if (rs.getString("edit_Date") != null)
				narrative.setEditDate(rs.getString("edit_Date").substring(0,10));				
			
			narrative.setPatientId(rs.getInt("patient_ID"));			
			
			StringBuffer sb = new StringBuffer();
			sb.append(rs.getString("firstname"));
			sb.append(" ");
			sb.append(rs.getString("lastname"));
			narrative.setPatientName(sb.toString());			
			
			narrative.setAppointmentId(rs.getInt("appointment"));
			narrative.setVolunteerId(rs.getInt("volunteer"));
			return narrative;
		}
	}



}
