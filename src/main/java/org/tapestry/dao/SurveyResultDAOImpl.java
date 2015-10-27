package org.tapestry.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import org.tapestry.objects.SurveyResult;

/**
 * An implementation of the SurveyResultDAO interface.
 * 
 * lxie
 */

@Repository
public class SurveyResultDAOImpl extends JdbcDaoSupport implements SurveyResultDAO {
	@Autowired
	public SurveyResultDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
    }
	
	@Override
	public List<SurveyResult> getSurveysByPatientID(int patientId) {
		String sql = "SELECT survey_results.*, surveys.title, surveys.description, patients.firstname, patients.lastname FROM survey_results"
				+ " INNER JOIN surveys ON survey_results.survey_ID = surveys.survey_ID INNER JOIN patients"
				+ " ON survey_results.patient_ID=patients.patient_ID WHERE survey_results.patient_ID=?"
				+ " ORDER BY survey_results.startDate ";
		List<SurveyResult> results = getJdbcTemplate().query(sql, new Object[]{patientId}, new SurveyResultMapper());
		
		return results;
	}

	@Override
	public List<SurveyResult> getCompletedSurveysByPatientID(int patientId) {
		String sql = "SELECT survey_results.*, surveys.title, surveys.description, patients.firstname, patients.lastname "
				+ "FROM survey_results INNER JOIN surveys ON survey_results.survey_ID = surveys.survey_ID INNER JOIN patients"
				+ " ON survey_results.patient_ID=patients.patient_ID WHERE survey_results.patient_ID=? AND"
				+ " survey_results.completed = 1 ORDER BY survey_results.survey_ID ";
		List<SurveyResult> results = getJdbcTemplate().query(sql, new Object[]{patientId}, new SurveyResultMapper());
		
		return results;
	}

	@Override
	public List<SurveyResult> getIncompleteSurveysByPatientID(int patientId) {
		String sql = "SELECT survey_results.*, surveys.title, surveys.description, patients.firstname, patients.lastname "
				+ "FROM survey_results INNER JOIN surveys ON survey_results.survey_ID = surveys.survey_ID INNER JOIN patients"
				+ " ON survey_results.patient_ID=patients.patient_ID WHERE survey_results.patient_ID=? AND"
				+ " survey_results.completed = 0 ORDER BY survey_results.startDate ";
		List<SurveyResult> results = getJdbcTemplate().query(sql, new Object[]{patientId}, new SurveyResultMapper());
		
		return results;
	}

	@Override
	public SurveyResult getSurveyResultByID(int resultId) {
		String sql = "SELECT survey_results.*, surveys.title, surveys.description, patients.firstname, patients.lastname "
				+ "FROM survey_results INNER JOIN surveys ON survey_results.survey_ID = surveys.survey_ID INNER JOIN patients"
				+ " ON survey_results.patient_ID=patients.patient_ID WHERE survey_results.result_ID=? "
				+ " ORDER BY survey_results.startDate ";
		
		return getJdbcTemplate().queryForObject(sql, new Object[]{resultId}, new SurveyResultMapper());
	}

	@Override
	public List<SurveyResult> getAllSurveyResults() {
		String sql = "SELECT survey_results.*, surveys.title, surveys.description, patients.firstname, patients.lastname FROM survey_results"
				+ " INNER JOIN surveys ON survey_results.survey_ID = surveys.survey_ID INNER JOIN patients"
				+ " ON survey_results.patient_ID=patients.patient_ID ORDER BY survey_results.startDate ";
		List<SurveyResult> results = getJdbcTemplate().query(sql, new SurveyResultMapper());
		
		return results;
	}

	@Override
	public List<SurveyResult> getAllSurveyResultsBySite(int siteId) {
		String sql = "SELECT survey_results.*, surveys.title, surveys.description, patients.firstname, patients.lastname FROM survey_results"
				+ " INNER JOIN surveys ON survey_results.survey_ID = surveys.survey_ID INNER JOIN patients"
				+ " ON survey_results.patient_ID=patients.patient_ID WHERE surveys.site=? ORDER BY survey_results.startDate ";
		List<SurveyResult> results = getJdbcTemplate().query(sql, new Object[]{siteId},  new SurveyResultMapper());
		
		return results;
	}


	@Override
	public List<SurveyResult> getAllSurveyResultsBySurveyId(int surveyId) {
		String sql = "SELECT survey_results.*, surveys.title, surveys.description, patients.firstname, patients.lastname FROM survey_results"
				+ " INNER JOIN surveys ON survey_results.survey_ID = surveys.survey_ID INNER JOIN patients"
				+ " ON survey_results.patient_ID=patients.patient_ID WHERE survey_results.survey_ID=?"
				+ " ORDER BY survey_results.startDate ";
		List<SurveyResult> results = getJdbcTemplate().query(sql, new Object[]{surveyId}, new SurveyResultMapper());
		
		return results;
	}

	@Override
	public SurveyResult getSurveyResultByPatientAndSurveyId(int patientId, int surveyId) {
		String sql = "SELECT survey_results.*, surveys.title, surveys.description, patients.firstname, patients.lastname FROM survey_results"
				+ " INNER JOIN surveys ON survey_results.survey_ID = surveys.survey_ID INNER JOIN patients"
				+ " ON survey_results.patient_ID=patients.patient_ID WHERE survey_results.patient_ID=? AND survey_results.survey_ID=?"
				+ " ORDER BY survey_results.startDate ";
		SurveyResult result = getJdbcTemplate().queryForObject(sql, new Object[]{patientId,surveyId}, new SurveyResultMapper());
		
		return result;
	}
	
	@Override
	public SurveyResult getCompletedSurveyResultByPatientAndSurveyTitle(int patientId, String surveyTitle) {
		String sql = "SELECT survey_results.*, surveys.title, surveys.description, patients.firstname, patients.lastname FROM survey_results"
				+ " INNER JOIN surveys ON survey_results.survey_ID = surveys.survey_ID INNER JOIN patients"
				+ " ON survey_results.patient_ID=patients.patient_ID WHERE survey_results.patient_ID=? AND surveys.title=? "
				+ "AND survey_results.completed=1 ORDER BY survey_results.startDate ";
		SurveyResult result = getJdbcTemplate().queryForObject(sql, new Object[]{patientId,surveyTitle}, new SurveyResultMapper());
		
		return result;
	}

	@Override
	public String assignSurvey(final SurveyResult sr) {						
		final String sql = "INSERT INTO survey_results (patient_ID, survey_ID, data, startDate) values (?,?,?,?)";
    	KeyHolder keyHolder = new GeneratedKeyHolder();
    	getJdbcTemplate().update(
    	    new PreparedStatementCreator() {
    	        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
    	            PreparedStatement pst =
    	                con.prepareStatement(sql, new String[] {"id"});
    	            pst.setInt(1, sr.getPatientID());
    	            pst.setInt(2, sr.getSurveyID());
    	            pst.setBytes(3, sr.getResults());
    	            pst.setString(4, sr.getStartDate());
    	            
    	            return pst;
    	        }
    	    },
    	    keyHolder);
    	return String.valueOf((Long)keyHolder.getKey());
	}

	@Override
	public void deleteSurvey(int id) {
		String sql = "DELETE FROM survey_results WHERE result_ID=?";
		getJdbcTemplate().update(sql, id);
	}

	@Override
	public void markAsComplete(int id) {
		String sql = "UPDATE survey_results SET completed=1 WHERE result_ID=?";
		getJdbcTemplate().update(sql, id);
	}

	@Override
	public void updateSurveyResults(int id, byte[] data) {		
		String sql = "UPDATE survey_results SET data=?, editDate=now() WHERE result_ID=?";
		getJdbcTemplate().update(sql, data,id);
	}

	@Override
	public void updateStartDate(int id) {
		String sql = "UPDATE survey_results SET startDate=now() WHERE result_ID=?";
		getJdbcTemplate().update(sql, id);
	}

	@Override
	public int countCompletedSurveys(int patientId) {
		String sql = "SELECT COUNT(Distinct survey_ID) as c FROM survey_results WHERE (patient_ID=?) AND (completed=1)";
		return getJdbcTemplate().queryForInt(sql, new Object[]{patientId});
	}		

	@Override
	public int countUnCompletedSurveys(int patientId) {
		String sql = "SELECT COUNT(Distinct survey_ID) as c FROM survey_results WHERE (patient_ID=?) AND (completed=0)";
		return getJdbcTemplate().queryForInt(sql, new Object[]{patientId});
	}
	
	public boolean hasCompleteSurvey(int surveyId, int patientId){
		boolean hasCompleted = false;		
		String sql = "SELECT COUNT(*) as c FROM survey_results WHERE (patient_ID=?) AND (survey_ID=?) AND (completed=1)";
		int count = getJdbcTemplate().queryForInt(sql, new Object[]{patientId, surveyId});
		
		if (count == 1)
			hasCompleted = true;
		
		return hasCompleted;
	}
	
	@Override
	public int countSurveysBySurveyTemplateId(int surveyTemplateId) {
		String sql = "SELECT COUNT(*) as c FROM survey_results WHERE (survey_ID=?)";
		return getJdbcTemplate().queryForInt(sql, new Object[]{surveyTemplateId});
	}
	
	@Override
	public int countSurveysBySurveyTemplateIdAndSite(int surveyTemplateId, int siteId) {
		String sql = "SELECT COUNT(*) as c FROM survey_results INNER JOIN surveys ON survey_results.survey_ID = surveys.survey_ID "
				+ "WHERE (surveys.survey_ID=?) AND (surveys.site=?)";
		return getJdbcTemplate().queryForInt(sql, new Object[]{surveyTemplateId, siteId});
	}
		
	class SurveyResultMapper implements RowMapper<SurveyResult> {
		public SurveyResult mapRow(ResultSet rs, int rowNum) throws SQLException{
			SurveyResult sr = new SurveyResult();
			
			sr.setResultID(rs.getInt("result_ID"));
			sr.setSurveyID(rs.getInt("survey_ID"));
			sr.setSurveyTitle(rs.getString("title"));
			sr.setDescription(rs.getString("description"));
			sr.setPatientID(rs.getInt("patient_ID"));
			sr.setPatientName(rs.getString("firstname") + " " + rs.getString("lastname"));
			
			boolean completed = rs.getBoolean("completed");
   			sr.setCompleted(completed);
   			if (completed)
   				sr.setStrCompleted("COMPLETED");
   			else
   				sr.setStrCompleted("INCOMPLETED"); 
	     
   			sr.setStartDate(rs.getString("startDate"));   
   			String editDate = rs.getString("editDate");
   			if (editDate != null)
   				sr.setEditDate(editDate.substring(0, 10));
   			sr.setResults(rs.getBytes("data"));
	            
			return sr;
		}
	}
	
	public void archiveSurveyResult(SurveyResult sr, String patient, String deletedBy){
		String sql = "INSERT INTO survey_results_archive (patient, survey_ID, data, startDate, deleted_result_ID, "
				+ "deleted_by) values (?,?,?,?,?,?)";
		getJdbcTemplate().update(sql, patient, sr.getSurveyID(), sr.getResults(), sr.getStartDate(), sr.getResultID()
				, deletedBy);
	}

	@Override
	public List<SurveyResult> getAllVolunteerSurveyResults() {
		String sql = "SELECT vsr.*, vs.title, vs.description, v.firstname, v.lastname FROM volunteer_survey_results AS vsr"
				+ " INNER JOIN volunteer_surveys AS vs ON vsr.volunteer_survey_ID = vs.survey_ID INNER JOIN volunteers AS v"
				+ " ON vsr.volunteer_ID=v.volunteer_ID ORDER BY vsr.startDate ";
		List<SurveyResult> results = getJdbcTemplate().query(sql, new VolunteerSurveyResultMapper());
		
		return results;
	}
	
	@Override
	public String assignVolunteerSurvey(final SurveyResult sr) {						
		final String sql = "INSERT INTO volunteer_survey_results (volunteer_ID, volunteer_survey_ID, data, startDate) values (?,?,?,?)";
    	KeyHolder keyHolder = new GeneratedKeyHolder();
    	getJdbcTemplate().update(
    	    new PreparedStatementCreator() {
    	        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
    	            PreparedStatement pst =
    	                con.prepareStatement(sql, new String[] {"id"});
    	            pst.setInt(1, sr.getVolunteerID());
    	            pst.setInt(2, sr.getSurveyID());
    	            pst.setBytes(3, sr.getResults());
    	            pst.setString(4, sr.getStartDate());
    	            
    	            return pst;
    	        }
    	    },
    	    keyHolder);
    	return String.valueOf((Long)keyHolder.getKey());
	}
	
	@Override
	public List<SurveyResult> getCompletedVolunteerSurveys(int volunteerId) {
		String sql = "SELECT vsr.*, vs.title, vs.description, v.firstname, v.lastname FROM volunteer_survey_results AS vsr "
				+ "INNER JOIN volunteer_surveys AS vs ON vsr.volunteer_survey_ID = vs.survey_ID INNER JOIN volunteers AS v "
				+ "ON vsr.volunteer_ID=v.volunteer_ID WHERE vsr.volunteer_ID=? AND vsr.completed = 1 ORDER BY vsr.startDate ";
		List<SurveyResult> results = getJdbcTemplate().query(sql, new Object[]{volunteerId}, new VolunteerSurveyResultMapper());
		
		return results;
	}

	@Override
	public List<SurveyResult> getIncompleteVolunteerSurveys(int volunteerId) {
		String sql = "SELECT vsr.*, vs.title, vs.description, v.firstname, v.lastname FROM volunteer_survey_results AS vsr "
				+ "INNER JOIN volunteer_surveys AS vs ON vsr.volunteer_survey_ID = vs.survey_ID INNER JOIN volunteers AS v "
				+ "ON vsr.volunteer_ID=v.volunteer_ID WHERE vsr.volunteer_ID=? AND vsr.completed = 0 ORDER BY vsr.startDate ";
		List<SurveyResult> results = getJdbcTemplate().query(sql, new Object[]{volunteerId}, new VolunteerSurveyResultMapper());
		
		return results;
	}
	
	@Override
	public SurveyResult getVolunteerSurveyResultByID(int resultId) {
		String sql = "SELECT vsr.*, vs.title, vs.description, v.firstname, v.lastname FROM volunteer_survey_results AS vsr "
				+ "INNER JOIN volunteer_surveys AS vs ON vsr.volunteer_survey_ID = vs.survey_ID INNER JOIN volunteers AS v"
				+ " ON vsr.volunteer_ID=v.volunteer_ID WHERE vsr.result_ID=? "
				+ " ORDER BY vsr.startDate ";
		
		return getJdbcTemplate().queryForObject(sql, new Object[]{resultId}, new VolunteerSurveyResultMapper());
	}
	

	@Override
	public void updateVolunteerSurveyResults(int id, byte[] data) {
		String sql = "UPDATE volunteer_survey_results SET data=?, editDate=now() WHERE result_ID=?";
		getJdbcTemplate().update(sql, data,id);		
	}

	@Override
	public void markAsCompleteForVolunteerSurvey(int id) {
		String sql = "UPDATE volunteer_survey_results SET completed=1 WHERE result_ID=?";
		getJdbcTemplate().update(sql, id);
		
	}

	@Override
	public List<SurveyResult> getVolunteerSurveyResultsByVolunteerId(int volunteerId) {
		String sql = "SELECT vsr.*, vs.title, vs.description, v.firstname, v.lastname FROM volunteer_survey_results AS vsr"
				+ " INNER JOIN volunteer_surveys AS vs ON vsr.volunteer_survey_ID = vs.survey_ID INNER JOIN volunteers AS v"
				+ " ON vsr.volunteer_ID=v.volunteer_ID WHERE vsr.volunteer_ID=? ORDER BY vsr.startDate ";
		List<SurveyResult> results = getJdbcTemplate().query(sql, new Object[]{volunteerId}, new VolunteerSurveyResultMapper());
		
		return results;
	}
	
	@Override
	public void deleteVolunteerSurvey(int id) {
		String sql = "DELETE FROM volunteer_survey_results WHERE result_ID=?";
		getJdbcTemplate().update(sql, id);
	}

	@Override
	public int getSiteBySurveyResultID(int surveyResultId) {
		String sql = "SELECT st.site from survey_results As sr INNER JOIN surveys AS st ON st.survey_ID= sr.survey_ID WHERE sr.result_ID=?";
		return getJdbcTemplate().queryForInt(sql, new Object[]{surveyResultId});
	}


	class VolunteerSurveyResultMapper implements RowMapper<SurveyResult> {
		public SurveyResult mapRow(ResultSet rs, int rowNum) throws SQLException{
			SurveyResult sr = new SurveyResult();
			
			sr.setResultID(rs.getInt("result_ID"));
			sr.setSurveyID(rs.getInt("volunteer_survey_ID"));
			sr.setSurveyTitle(rs.getString("title"));
			sr.setDescription(rs.getString("description"));
			sr.setVolunteerID(rs.getInt("volunteer_ID"));
			sr.setVolunteerName(rs.getString("firstname") + " " + rs.getString("lastname"));
						
			boolean completed = rs.getBoolean("completed");
   			sr.setCompleted(completed);
   			if (completed)
   				sr.setStrCompleted("COMPLETED");
   			else
   				sr.setStrCompleted("INCOMPLETED"); 
	     
   			sr.setStartDate(rs.getString("startDate"));            
   			String editDate = rs.getString("editDate");
   			if (editDate != null)
   				sr.setEditDate(editDate.substring(0, 10));
   			sr.setResults(rs.getBytes("data"));
	            
			return sr;
		}
	}

}
