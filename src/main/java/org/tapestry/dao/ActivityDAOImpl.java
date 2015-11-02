package org.tapestry.dao;

import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.tapestry.utils.Utils;
import org.tapestry.objects.Activity;
import org.tapestry.objects.User;
import org.tapestry.objects.UserLog;

/**
 * An implementation of the ActivityDAO interface.
 * 
 * lxie
 */
@Repository
public class ActivityDAOImpl extends JdbcDaoSupport implements ActivityDAO {
	@Autowired
	public ActivityDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
    }
 
	@Override
	public List<Activity> getAllActivitiesForVolunteer(int volunteer) {			
		String sql = "SELECT DATE(activities.event_timestamp) AS date, activities.description, "
				+ "TIME(activities.start_Time) AS sTime, TIME(activities.end_Time) AS eTime, "
				+ "activities.organization, activities.event_ID, "
				+ "volunteers.firstname, volunteers.lastname FROM activities "
				+ "INNER JOIN volunteers ON activities.volunteer=volunteers.volunteer_ID"
				+ " WHERE volunteer=?";
		
		return getJdbcTemplate().query(sql, new Object[]{volunteer}, new ActivityMapper());
	}

	@Override
	public List<Activity> getAllActivitiesForAdmin() {		
		String sql = "SELECT DATE(activities.event_timestamp) AS date, activities.description, "
				+ "TIME(activities.start_Time) AS sTime, TIME(activities.end_Time) AS eTime,"
				+ " activities.organization, activities.event_ID,"
				+ " volunteers.firstname, volunteers.lastname  FROM activities "
				+ "INNER JOIN volunteers ON activities.volunteer=volunteers.volunteer_ID"
				+ " ORDER BY event_timestamp";
		
		return getJdbcTemplate().query(sql, new ActivityMapper());
	}

	@Override
	public List<Activity> getAllActivitiesForLocalAdmin(int id) {		
		String sql = "SELECT DATE(activities.event_timestamp) AS date, activities.description, "
				+ "TIME(activities.start_Time) AS sTime, TIME(activities.end_Time) AS eTime, "
				+ "activities.organization, activities.event_ID, "
				+ "volunteers.firstname, volunteers.lastname  FROM activities "
				+ "INNER JOIN volunteers ON activities.volunteer=volunteers.volunteer_ID"
				+ " WHERE activities.organization = ? ORDER BY event_timestamp";
		
		return getJdbcTemplate().query(sql, new Object[]{id}, new ActivityMapper());	
	}

	@Override
	public void logActivity(String description, int volunteer) {
		//volunteer add activity
		String sql = "INSERT INTO activities (description,volunteer) VALUES (?, ?)";
		getJdbcTemplate().update(sql,description, volunteer);		
	}

	@Override
	public void logActivity(Activity activity) {		
		String sql = "INSERT INTO activities (description,volunteer, event_timestamp,start_Time,end_Time, "
				+ " organization) VALUES (?, ?, ?, ?, ?, ?)";
		getJdbcTemplate().update(sql, activity.getDescription(), activity.getVolunteer(), activity.getDate(), 
				activity.getStartTime(), activity.getEndTime(), activity.getOrganizationId());		
	}

	@Override
	public void updateActivity(Activity activity) {	
		String sql = "UPDATE activities SET event_timestamp=?,description=?, start_Time=?, end_Time=? WHERE event_ID=?";
		getJdbcTemplate().update(sql, activity.getDate(), activity.getDescription(),activity.getStartTime(), 
				activity.getEndTime(), activity.getActivityId());	
	}
	
	@Override
	public List<Activity> getPage(int start, int n) {
		//Returns a list of n items starting at start
		String sql = "SELECT DATE(activities.event_timestamp) AS date, activities.description, activities.organization,"
				+ " activities.event_ID, volunteers.firstname, volunteers.lastname ,"
				+ " FROM activities INNER JOIN volunteers ON activities.volunteer=volunteers.volunteer_ID"
				+ " ORDER BY event_timestamp DESC LIMIT ?,?";
		
		return getJdbcTemplate().query(sql, new Object[]{start,n}, new ActivityMapper());		
	}

	@Override
	public Activity getActivityLogById(int activityId) {
		String sql = "SELECT DATE(event_timestamp) AS date, description, event_ID, volunteer,"
				+ " TIME(start_Time) AS sTime, TIME(end_Time) AS eTime, organization FROM activities WHERE event_ID = ?";
		
		return getJdbcTemplate().queryForObject(sql, new Object[]{activityId}, new DetailedActivityMapper());		
	}
	
	@Override
	public void deleteActivityById(int id) {		
		String sql = "DELETE FROM activities WHERE event_ID=?";
	    getJdbcTemplate().update(sql, id);

	}
	
	@Override
	public int countEntries() {
		String sql = "SELECT COUNT(log_ID) AS c FROM user_logs";
		
		return getJdbcTemplate().queryForInt(sql);				
	}
	
	@Override
	public int countEntriesByGroup(int organizationId) {
		String sql = "SELECT COUNT(log_ID) AS c FROM user_logs AS ul INNER JOIN users AS u ON ul.user=u.user_ID WHERE u.organization=?";
		return getJdbcTemplate().queryForInt(sql, new Object[]{organizationId});	
	}


	@Override
	public List<UserLog> getUserLogsPage(int start, int n) {
		String sql = "SELECT ul.*, u.name FROM user_logs AS ul INNER JOIN users AS u ON ul.user=u.user_ID "
				+ "ORDER BY ul.event_timestamp DESC LIMIT ?,?";
		return getJdbcTemplate().query(sql, new Object[]{start,n}, new UserLogMapper());
	}

	@Override
	public void addUserLog(String description, User user) {		
		String sql = "INSERT INTO user_logs (description,user) VALUES (?, ?)";	
		getJdbcTemplate().update(sql,description, user.getUserID());
	}

	@Override
	public List<UserLog> getUserLogsByPartialName(String partialName) {			
//		String sql = "SELECT ul.event_timestamp, ul.user, ul.description, u.name FROM user_logs AS ul "
//				+ "INNER JOIN users AS u ON ul.user=u.user_ID WHERE UPPER(u.name) LIKE UPPER('%" + partialName + "%') "
//				+ "ORDER BY ul.event_timestamp";
		String sql = "SELECT ul.event_timestamp, ul.user, ul.description, u.name FROM user_logs AS ul "
				+ "INNER JOIN users AS u ON ul.user=u.user_ID WHERE UPPER(u.name) LIKE UPPER('%" + partialName + "%') "
				+ "ORDER BY ul.event_timestamp";
		return getJdbcTemplate().query(sql, new UserLogMapper());
	}

	@Override
	public List<UserLog> getUserLogsPageByGroup(int start, int n, int organizationId) {
		String sql = "SELECT ul.*, u.name FROM user_logs AS ul INNER JOIN users AS u ON ul.user=u.user_ID "
				+ "WHERE u.organization=? ORDER BY ul.event_timestamp DESC LIMIT ?,?";
		return getJdbcTemplate().query(sql, new Object[]{organizationId, start, n}, new UserLogMapper());
	}

	@Override
	public List<UserLog> getGroupedUserLogssByPartialName(String partialName, int organizationId) {
		String sql = "SELECT ul.event_timestamp, ul.user, ul.description, u.name FROM user_logs AS ul "
				+ "INNER JOIN users AS u ON ul.user=u.user_ID WHERE UPPER(u.name) LIKE UPPER('%" + partialName + "%') "
				+ "AND u.organization=? ORDER BY ul.event_timestamp";
		return getJdbcTemplate().query(sql, new Object[]{organizationId}, new UserLogMapper());
	}
	
	//RowMapper
	class ActivityMapper implements RowMapper<Activity> {
		public Activity mapRow(ResultSet rs, int rowNum) throws SQLException{
			Activity activity = new Activity();
			activity.setActivityId(rs.getInt("event_ID"));
			activity.setDate(rs.getString("date"));
			activity.setDescription(rs.getString("description"));			
			String sTime = rs.getString("sTime");
			sTime = sTime.substring(0, sTime.length() - 3);
			String eTime = rs.getString("eTime");
			eTime = eTime.substring(0, eTime.length() - 3);
			
			StringBuffer sb = new StringBuffer();
			sb.append(sTime);
			sb.append("--");
			sb.append(eTime);
			activity.setTime(sb.toString());
			
			if (!Utils.isNullOrEmpty(rs.getString("firstname")) && !Utils.isNullOrEmpty(rs.getString("lastname")))
			{
				sb = new StringBuffer();
				sb.append(rs.getString("firstname"));
				sb.append(" ");
				sb.append(rs.getString("lastname"));
				
				activity.setVolunteerName(sb.toString());	
			}		
			
			return activity;			
		}
	}
	
	class DetailedActivityMapper implements RowMapper<Activity> {
		public Activity mapRow(ResultSet rs, int rowNum) throws SQLException{
			Activity activity = new Activity();
			activity.setActivityId(rs.getInt("event_ID"));
			activity.setDate(rs.getString("date"));
			activity.setDescription(rs.getString("description"));
			String time = rs.getString("stime");
			activity.setStartTime(time.substring(0, time.length() - 3));			
			time = rs.getString("eTime");
			activity.setEndTime(time.substring(0, time.length() - 3));
			activity.setVolunteer(rs.getString("volunteer"));
			activity.setOrganizationId(rs.getInt("organization"));
			
			return activity;			
		}
	}
		
	class UserLogMapper implements RowMapper<UserLog> {
		public UserLog mapRow(ResultSet rs, int rowNum) throws SQLException{
			UserLog log = new UserLog();			
			log.setDate(rs.getString("event_timestamp"));
			log.setDescription(rs.getString("description"));
			log.setUserId(rs.getInt("user"));
			log.setUserName(rs.getString("name"));
			
			return log;
		}
	}

	@Override
	public void archivedActivity(Activity activity, String deletedBy, String volunteer) {
		String sql = "INSERT INTO activities_archive (deleted_event_ID, description,volunteer, start_Time,end_Time, "
				+ " organization, deleted_by) VALUES (?, ?, ?, ?, ?, ?, ?)";
		getJdbcTemplate().update(sql, activity.getActivityId(), activity.getDescription(), volunteer, 
				activity.getStartTime(), activity.getEndTime(), activity.getOrganizationId(), deletedBy);
		
	}

}
