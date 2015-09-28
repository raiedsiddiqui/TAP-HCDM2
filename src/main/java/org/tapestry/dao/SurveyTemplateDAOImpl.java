package org.tapestry.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.tapestry.objects.SurveyTemplate;

/**
 * An implementation of the SurveyTemplateDAO interface.
 * 
 * lxie
 */

@Repository
public class SurveyTemplateDAOImpl extends JdbcDaoSupport implements SurveyTemplateDAO {
	@Autowired
	public SurveyTemplateDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
    }

	@Override
	public SurveyTemplate getSurveyTemplateByID(int id) {
		String sql = "SELECT sur.*, st.site_name AS site_name FROM surveys AS sur INNER JOIN sites AS st "
				+ "ON sur.site = st.site_ID WHERE survey_ID=?";
		return getJdbcTemplate().queryForObject(sql, new Object[]{id}, new SurveyTemplateMapper());
	}

	@Override
	public List<SurveyTemplate> getAllSurveyTemplates() {
		String sql = "SELECT sur.*, st.site_name AS site_name FROM surveys AS sur INNER JOIN sites AS st "
				+ "ON sur.site = st.site_ID ORDER BY site DESC";
		List<SurveyTemplate> surveyTemplates = getJdbcTemplate().query(sql, new SurveyTemplateMapper());
		
		return surveyTemplates;
	}
	
	@Override
	public List<SurveyTemplate> getDefaultSurveyTemplates() {
		String sql = "SELECT sur.*, st.site_name AS site_name FROM surveys AS sur INNER JOIN sites AS st "
				+ "ON sur.site = st.site_ID WHERE sur.isDefault = 1 ORDER BY priority DESC";
		List<SurveyTemplate> surveyTemplates = getJdbcTemplate().query(sql, new SurveyTemplateMapper());
		
		return surveyTemplates;
	}
	
	@Override
	public List<SurveyTemplate> getDefaultSurveyTemplatesBySite(int siteId) {
		String sql = "SELECT sur.*, st.site_name AS site_name FROM surveys AS sur INNER JOIN sites AS st "
				+ "ON sur.site = st.site_ID WHERE sur.isDefault = 1 AND site=? ORDER BY priority DESC";
		List<SurveyTemplate> surveyTemplates = getJdbcTemplate().query(sql, new Object[]{siteId}, new SurveyTemplateMapper());
		
		return surveyTemplates;
	}
	
	@Override
	public List<SurveyTemplate> getSurveyTemplatesBySite(int siteId) {		
		String sql = "SELECT sur.*, st.site_name AS site_name FROM surveys AS sur INNER JOIN sites AS st "
				+ "ON sur.site = st.site_ID WHERE site=? ORDER BY priority DESC";
		List<SurveyTemplate> surveyTemplates = getJdbcTemplate().query(sql,new Object[]{siteId}, new SurveyTemplateMapper());
		
		return surveyTemplates;
	}

	@Override
	public List<SurveyTemplate> getSurveyTemplatesByPartialTitle(String partialTitle) {
		String sql = "SELECT sur.*, st.site_name AS site_name FROM surveys AS sur INNER JOIN sites AS st "
				+ "ON sur.site = st.site_ID WHERE UPPER(title) LIKE UPPER('%" + partialTitle + "%')";
		List<SurveyTemplate> surveyTemplates = getJdbcTemplate().query(sql, new SurveyTemplateMapper());
		
		return surveyTemplates;
	}
	
	@Override
	public int getSurveyIdByTitle(String title){
		String sql = "SELECT survey_ID FROM surveys WHERE title=?";
		return getJdbcTemplate().queryForInt(sql, new Object[] {title});
	}

	@Override
	public int getSurveyIdByTitle(String title, int site) {
		String sql = "SELECT survey_ID FROM surveys WHERE title=? AND site=?";
		return getJdbcTemplate().queryForInt(sql, new Object[] {title, site});
	}

	@Override
	public void uploadSurveyTemplate(SurveyTemplate st) {
		String sql = "INSERT INTO surveys (title, type, contents, priority, description, site, isDefault) values (?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql, st.getTitle(), st.getType(), st.getContents(), st.getPriority(), st.getDescription(), 
				st.getSite(), st.isDefaultSurvey());
	}	

	@Override
	public void updateSurveyTemplate(SurveyTemplate st) {
		String sql = "UPDATE surveys SET title=?, description=? WHERE survey_ID=?";
		getJdbcTemplate().update(sql, st.getTitle(), st.getDescription(), st.getSurveyID());		
	}

	@Override
	public void deleteSurveyTemplate(int id) {
		String sql = "DELETE FROM surveys WHERE survey_ID=?";
		getJdbcTemplate().update(sql, id);
	}

	@Override
	public int countSurveyTemplate() {
		String sql = "SELECT COUNT(*) as c FROM surveys";
		return getJdbcTemplate().queryForInt(sql);
	}
	
	@Override
	public int countSurveyTemplateBySite(int site) {
		String sql = "SELECT COUNT(*) as c FROM surveys WHERE site=? ";
		return getJdbcTemplate().queryForInt(sql, new Object[] {site});
	}
	
	@Override
	public List<String> getSurveyTemplateTitlesBySite(int siteId) {
		String sql = "SELECT title FROM surveys";
		return getJdbcTemplate().queryForList(sql, String.class);	
	}

	@Override
	public void setDefaultSurveyTemplate(String[] surveyIds) {
		int size = surveyIds.length;
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE surveys SET isDefault='1' WHERE survey_ID IN (");
		
		for (int i = 0; i < size; i++)
		{
			sb.append(surveyIds[i]);
			if (i != size - 1)
				sb.append(",");
		}
		sb.append(")");
		
		getJdbcTemplate().update(sb.toString());
	}
	
	@Override  
	public void removeDefaultSurveyTemplateSetting(String[] surveyIds) {
		int size = surveyIds.length;
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE surveys SET isDefault='0' WHERE survey_ID IN (");
		
		for (int i = 0; i < size; i++)
		{
			sb.append(surveyIds[i]);
			if (i != size - 1)
				sb.append(",");
		}
		sb.append(")");
		
		getJdbcTemplate().update(sb.toString());
	}

	
	class SurveyTemplateMapper implements RowMapper<SurveyTemplate> {
		public SurveyTemplate mapRow(ResultSet rs, int rowNum) throws SQLException{
			SurveyTemplate st = new SurveyTemplate();
		
			st.setSurveyID(rs.getInt("survey_ID"));
            st.setTitle(rs.getString("title"));
            st.setType(rs.getString("type"));
            st.setContents(rs.getBytes("contents"));
            st.setPriority(rs.getInt("priority"));
            st.setDescription(rs.getString("description"));
            st.setSite(rs.getInt("site"));
            st.setSiteName(rs.getString("site_name"));          
            st.setDefaultSurvey(rs.getBoolean("isDefault"));
            //format date, remove time
            String date = rs.getString("last_modified");
            date = date.substring(0, 10);
            st.setCreatedDate(date);
			return st;
		}
	}

	@Override
	public void archiveSurveyTemplate(SurveyTemplate st, String deletedBy) {
		String sql = "INSERT INTO surveys_archive (deleted_survey_ID, title, type, contents, priority, description, deleted_by, site) "
				+ "values (?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql, st.getSurveyID(),st.getTitle(), st.getType(), st.getContents(), st.getPriority(), 
				st.getDescription(), deletedBy, st.getSite());
		
	}
	
//	=============================  Volunteer Survey Template ====================

	@Override
	public List<SurveyTemplate> getAllVolunteerSurveyTemplates() {		
		String sql = "SELECT sur.*, st.site_name AS site_name FROM volunteer_surveys AS sur INNER JOIN sites AS st "
				+ "ON sur.site = st.site_ID ORDER BY site DESC";
		List<SurveyTemplate> surveyTemplates = getJdbcTemplate().query(sql, new SurveyTemplateMapper());
		
		return surveyTemplates;
	}
	
	@Override
	public List<SurveyTemplate> getVolunteerSurveyTemplatesBySite(int siteId) {
		String sql = "SELECT sur.*, st.site_name AS site_name FROM volunteer_surveys AS sur INNER JOIN sites AS st "
				+ "ON sur.site = st.site_ID WHERE sur.isDefault = 1 AND site=? ORDER BY priority DESC";
		List<SurveyTemplate> surveyTemplates = getJdbcTemplate().query(sql, new Object[]{siteId}, new SurveyTemplateMapper());
		
		return surveyTemplates;
	}
	
	@Override
	public void uploadVolunteerSurveyTemplate(SurveyTemplate st) {
		String sql = "INSERT INTO volunteer_surveys (title, type, contents, priority, description, site, isDefault) "
				+ "values (?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql, st.getTitle(), st.getType(), st.getContents(), st.getPriority(), st.getDescription(), 
				st.getSite(), st.isDefaultSurvey());
		
	}

	@Override
	public List<SurveyTemplate> getVolunteerSurveyTemplatesByPartialTitle(String partialTitle) {
		String sql = "SELECT sur.*, st.site_name AS site_name FROM volunteer_surveys AS sur INNER JOIN sites AS st "
				+ "ON sur.site = st.site_ID WHERE UPPER(title) LIKE UPPER('%" + partialTitle + "%')";
		return getJdbcTemplate().query(sql, new SurveyTemplateMapper());
	}

	@Override
	public void updateVolunteerSurveyTemplate(SurveyTemplate st) {
		String sql = "UPDATE volunteer_surveys SET title=?, description=?WHERE survey_ID=?";		
		getJdbcTemplate().update(sql, st.getTitle(), st.getDescription(), st.getSurveyID());	
		
	}

	@Override
	public SurveyTemplate getVolunteerSurveyTemplateByID(int id) {
		String sql = "SELECT sur.*, st.site_name AS site_name FROM volunteer_surveys AS sur INNER JOIN sites AS st "
				+ "ON sur.site = st.site_ID WHERE survey_ID=?";
		return getJdbcTemplate().queryForObject(sql, new Object[]{id}, new SurveyTemplateMapper());
	}
	
	@Override
	public List<String> getVolunteerSurveyTemplateTitlesBySite(int siteId) {
		String sql = "SELECT title FROM volunteer_surveys";
		return getJdbcTemplate().queryForList(sql, String.class);	
	}

	@Override
	public void setDefaultVolunteerSurveyTemplate(String[] surveyIds) {
		int size = surveyIds.length;
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE volunteer_surveys SET isDefault='1' WHERE survey_ID IN (");
		
		for (int i = 0; i < size; i++)
		{
			sb.append(surveyIds[i]);
			if (i != size - 1)
				sb.append(",");
		}
		sb.append(")");
		
		getJdbcTemplate().update(sb.toString());
	}
	
	@Override  
	public void removeDefaultVolunteerSurveyTemplateSetting(String[] surveyIds) {
		int size = surveyIds.length;
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE volunteer_surveys SET isDefault='0' WHERE survey_ID IN (");
		
		for (int i = 0; i < size; i++)
		{
			sb.append(surveyIds[i]);
			if (i != size - 1)
				sb.append(",");
		}
		sb.append(")");
		
		getJdbcTemplate().update(sb.toString());
	}

	@Override
	public void deleteVolunteerSurveyTemplate(int id) {
		String sql = "DELETE FROM volunteer_surveys WHERE survey_ID=?";
		getJdbcTemplate().update(sql, id);
		
	}

	@Override
	public int countVolunteerSurveyResultsBySurveyId(int surveyId) {
		String sql = "SELECT COUNT(*) as c FROM volunteer_survey_results WHERE volunteer_survey_ID=? ";
		return getJdbcTemplate().queryForInt(sql, new Object[]{surveyId});
	}



}
