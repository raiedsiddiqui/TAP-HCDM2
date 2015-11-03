package org.tapestry.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.tapestry.dao.ActivityDAOImpl.ActivityMapper;
import org.tapestry.dao.ActivityDAOImpl.DetailedActivityMapper;
import org.tapestry.objects.Activity;
import org.tapestry.objects.AdminActivity;
import org.tapestry.utils.Utils;
/**
 * An implementation of the ActivityDAO interface.
 * 
 * lxie
 */
@Repository
public class AdminActivityDAOImpl extends JdbcDaoSupport implements AdminActivityDAO {

	@Autowired
	public AdminActivityDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
    }
	@Override
	public List<AdminActivity> getAllAdminActivities(int userId) {
		String sql = "SELECT DATE(activity.event_timestamp) AS date, activity.description, "
				+ "TIME(activity.start_Time) AS sTime, TIME(activity.end_Time) AS eTime, "
				+ "activity.siteId, activity.event_ID, activity.userId,"
				+ "users.name FROM activities_admin AS activity "
				+ "INNER JOIN users ON activity.userId=users.user_ID"
				+ " WHERE activity.userId=?";
		
		return getJdbcTemplate().query(sql, new Object[]{userId}, new AdminActivityMapper());
	}

	@Override
	public List<AdminActivity> getAllAdminActivities() {
		String sql = "SELECT DATE(activity.event_timestamp) AS date, activity.description, "
				+ "TIME(activity.start_Time) AS sTime, TIME(activity.end_Time) AS eTime, "
				+ "activity.siteId, activity.event_ID, activity.userId,"
				+ "users.firstname, users.lastname FROM activities_admin AS activity "
				+ "INNER JOIN users ON activity.userId=users.user_ID";
					
		return getJdbcTemplate().query(sql, new AdminActivityMapper());
	}

	@Override
	public List<AdminActivity> getAllActivitiesForLocalAdmin(int siteId) {
		String sql = "SELECT DATE(activity.event_timestamp) AS date, activity.description, "
				+ "TIME(activity.start_Time) AS sTime, TIME(activity.end_Time) AS eTime, "
				+ "activity.siteId, activity.event_ID, activity.userId, "
				+ "users.name FROM activities_admin AS activity "
				+ "INNER JOIN users ON activity.userId=users.user_ID"
				+ " WHERE activity.siteId=?";
		
		return getJdbcTemplate().query(sql, new Object[]{siteId}, new AdminActivityMapper());
	}

	@Override
	public void logAdminActivity(String description, int userId) {
		//admin add activity
		String sql = "INSERT INTO activities_admin (description,userId) VALUES (?, ?)";
		getJdbcTemplate().update(sql,description, userId);		

	}

	@Override
	public void logAdminActivity(AdminActivity activity) {
		String sql = "INSERT INTO activities_admin (description,userId, event_timestamp,start_Time,end_Time, "
				+ " siteId) VALUES (?, ?, ?, ?, ?, ?)";
		getJdbcTemplate().update(sql, activity.getDescription(), activity.getUser(), activity.getDate(), 
				activity.getStartTime(), activity.getEndTime(), activity.getSiteId());	

	}

	@Override
	public void updateActivity(AdminActivity activity) {
		String sql = "UPDATE activities_admin SET event_timestamp=?,description=?, start_Time=?, end_Time=? WHERE event_ID=?";
		getJdbcTemplate().update(sql, activity.getDate(), activity.getDescription(),activity.getStartTime(), 
				activity.getEndTime(), activity.getActivityId());	

	}

	@Override
	public AdminActivity getAdminActivityById(int activityId) {
		String sql = "SELECT DATE(activity.event_timestamp) AS date, activity.description, "
				+ "TIME(activity.start_Time) AS sTime, TIME(activity.end_Time) AS eTime, "
				+ "activity.siteId, activity.event_ID, activity.userId, "
				+ "users.name FROM activities_admin AS activity "
				+ "INNER JOIN users ON activity.userId=users.user_ID"
				+ " WHERE event_ID = ?";
		
		return getJdbcTemplate().queryForObject(sql, new Object[]{activityId}, new AdminActivityMapper());
	}

	@Override
	public void deleteAdminActivityById(int id) {
		String sql = "DELETE FROM activities_admin WHERE event_ID=?";
	    getJdbcTemplate().update(sql, id);
	}

	@Override
	public void archiveAdmindActivity(AdminActivity activity, String deletedBy) {
		String sql = "INSERT INTO activities_admin_archive (deleted_event_ID, description,userId, start_Time,end_Time, "
				+ " siteId, deleted_by) VALUES (?, ?, ?, ?, ?, ?, ?)";
		getJdbcTemplate().update(sql, activity.getActivityId(), activity.getDescription(), activity.getUser(), 
				activity.getStartTime(), activity.getEndTime(), activity.getSiteId(), deletedBy);
	}
	
	//RowMapper
	class AdminActivityMapper implements RowMapper<AdminActivity> {
		public AdminActivity mapRow(ResultSet rs, int rowNum) throws SQLException{
			AdminActivity activity = new AdminActivity();
			activity.setActivityId(rs.getInt("event_ID"));
			activity.setDate(rs.getString("date"));
			activity.setDescription(rs.getString("description"));			
			String sTime = rs.getString("sTime");
			activity.setStartTime(sTime);
			
			sTime = sTime.substring(0, sTime.length() - 3);
			String eTime = rs.getString("eTime");
			activity.setEndTime(eTime);
			eTime = eTime.substring(0, eTime.length() - 3);
			
			StringBuffer sb = new StringBuffer();
			sb.append(sTime);
			sb.append("--");
			sb.append(eTime);
			activity.setTime(sb.toString());				
			activity.setUserName(rs.getString("name"));
			activity.setUser(rs.getInt("userId"));
			
			return activity;			
		}
	}

}
