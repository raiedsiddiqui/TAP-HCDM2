package org.tapestry.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.tapestry.objects.Preference;

@Repository
public class PreferenceDAOImpl extends JdbcDaoSupport implements PreferenceDAO {
	
	@Autowired
	public PreferenceDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
    }

	@Override
	public Preference getPreferenceBySite(int site) {
		String sql = "SELECT * FROM site_preferences WHERE site_ID = ?";
		
		return getJdbcTemplate().queryForObject(sql, new Object[]{site}, new PreferenceMapper());
	}
	
	@Override
	public void addPreference(Preference preference) {
		String sql = "INSERT INTO site_preferences (site_ID, sos_receiver, elder_abuse_button,elder_abuse_content,"
				+ "self_harm_button, self_harm_content, crisis_lines_button, crisis_lines_content, sos_button) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
		getJdbcTemplate().update(sql, preference.getSiteId(), preference.getSosReceiver(), preference.getElderAbuseButton(), 
				preference.getElderAbuseContent(), preference.getSelfHarmButton(), preference.getSelfHarmContent(), 
				preference.getCrisisLinesButton(), preference.getCrisisLinesContent(), preference.getSosButton());				
	
	}
	
	@Override
	public void updatePreference(Preference preference) {
		String sql = "UPDATE site_preferences SET sos_receiver=?, elder_abuse_button=?, elder_abuse_content=?,"
				+ " self_harm_button=?, self_harm_content=?, crisis_lines_button=?, crisis_lines_content=?, "
				+ "sos_button=? WHERE site_ID=?";
		getJdbcTemplate().update(sql, preference.getSosReceiver(), preference.getElderAbuseButton(), 
				preference.getElderAbuseContent(), preference.getSelfHarmButton(), preference.getSelfHarmContent(), 
				preference.getCrisisLinesButton(), preference.getCrisisLinesContent(), preference.getSosButton(),
				preference.getSiteId());			
	}
	
	//RowMapper
	class PreferenceMapper implements RowMapper<Preference> {
		public Preference mapRow(ResultSet rs, int rowNum) throws SQLException{
			Preference preference = new Preference();
			preference.setSiteId(rs.getInt("site_ID"));
			preference.setSosButton(rs.getInt("sos_button"));
			preference.setSosReceiver(rs.getString("sos_receiver"));
			preference.setElderAbuseButton(rs.getString("elder_abuse_button"));
			preference.setElderAbuseContent(rs.getString("elder_abuse_content"));
			preference.setSelfHarmButton(rs.getString("self_harm_button"));
			preference.setSelfHarmContent(rs.getString("self_harm_content"));
			preference.setCrisisLinesButton(rs.getString("crisis_lines_button"));
			preference.setCrisisLinesContent(rs.getString("crisis_lines_content"));
				
			return preference;			
		}
	}


}
