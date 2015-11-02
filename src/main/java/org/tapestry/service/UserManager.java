package org.tapestry.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.tapestry.objects.AdminActivity;
import org.tapestry.objects.User;
import org.tapestry.objects.UserLog;

/**
 * service for Model User, Activity
 * @author lxie *
 */
public interface UserManager {
	/**
	 * 
	 * @return number of active users
	 */
	@Transactional
	public int countActiveUsers();
	
	/**
	 * 
	 * @return numbers of central admins
	 */
	@Transactional
	public int countAdministrators();
	
	/**
	 * 
	 * @return numbers of users
	 */
	@Transactional
	public int countAllUsers();
	
	/**
	 * 
	 * @param id
	 * @return 
	 */
	@Transactional
	public User getUserByID(int id);
	/**
	 * 
	 * @param organizationId
	 * @return a list of User who belong to same organization
	 */
	@Transactional
	public List<User> getUsersByGroup(int organizationId);
	
	/**
	 * search a user by username
	 * @param username
	 * @return
	 */
	@Transactional
	public User getUserByUsername(String username);
	
	/**
	 * create user
	 * @param u
	 * @return
	 */
	@Transactional
	public boolean createUser(User u);
	
	/**
	 * modify user
	 * @param u
	 */
	@Transactional
	public void modifyUser(User u);
	
	/**
	 * delete a user
	 * @param id
	 */
	@Transactional
	public void removeUserWithID(int id);
	
	/**
	 * 
	 * @param username
	 */
	@Transactional
	public void removeUserByUsername(String username);
	
	/**
	 * Save a copy of a user
	 * @param user
	 * @param deletedBy
	 */
	@Transactional
	public void archiveUser(User user, String deletedBy);
	
	/**
	 * disable a user
	 * @param id
	 */
	@Transactional
	public void disableUserWithID(int id);
	
	/**
	 * enable a user
	 * @param id
	 */
	@Transactional
	public void enableUserWithID(int id);
	
	/**
	 * 
	 * @return a list of users
	 */
	@Transactional
	public List<User> getAllUsers();
	
	/**
	 * search user by partial name
	 * @param partialName
	 * @return
	 */
	@Transactional
	public List<User> getUsersByPartialName(String partialName);
	
	/**
	 * search user by role
	 * @param role
	 * @return
	 */
	@Transactional
	public List<User> getAllUsersWithRole(String role);
	
	/**
	 * 
	 * @param organizationId
	 * @param role
	 * @return
	 */
	@Transactional
	public List<User> getGroupedUsersByRole(int organizationId, String role);
	
	/**
	 * search active user by role
	 * @param role
	 * @return
	 */
	@Transactional
	public List<User> getAllActiveUsersWithRole(String role);
	
	/**
	 * check id a user has password
	 * @param id
	 * @param pwd
	 * @return
	 */
	@Transactional
	public boolean userHasPassword(int id, String pwd);
	
	/**
	 * set password for a user
	 * @param id
	 * @param pwd
	 */
	@Transactional
	public void setPasswordForUser(int id, String pwd);
	
	
	@Transactional
	public List<User> getVolunteerCoordinatorByOrganizationId(int id);
	
	/**
	 * 
	 * @param start
	 * @param n
	 * @return a list of userLogs
	 */
	@Transactional
	public List<UserLog> getUserLogs(int start, int n);
	/**
	 * log user activity with description
	 * @param description
	 * @param user
	 */
	@Transactional
	public void addUserLog(String description, User user);
	/**
	 * 
	 * @param partialName
	 * @return a list of userLogs 
	 */
	@Transactional
	public List<UserLog> getUserLogsByPartialName(String partialName);
	/**
	 * 
	 * @return number of user logs
	 */
	@Transactional
	public int count();
	
	/**
	 * 
	 * @param organizationId
	 * @return number of user logs
	 */
	@Transactional
	public int countEntriesByGroup(int organizationId);

	/**
	 * 
	 * @param start
	 * @param n
	 * @param organizationId
	 * @return
	 */
	@Transactional
	public List<UserLog> getUserLogsPageByGroup(int start, int n, int organizationId);
	
	/**
	 * 
	 * @param partialName
	 * @param organizationId
	 * @return
	 */
	@Transactional
	public List<UserLog> getGroupedUserLogssByPartialName(String partialName, int organizationId);	
	
	/**
	 * 
	 * @param id
	 * @return list of local admins from a site
	 */
	@Transactional
	public List<User> getLocalAdminBySite(int id);	
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional
	public List<User> getUserBySIte(int id);
	
	//admin journal/activity
	@Transactional
	public List<AdminActivity> getAllAdminActivities(int userId);
	
	@Transactional
	public List<AdminActivity> getAllAdminActivities();
	/**
	 * 
	 * @param id siteId
	 * @return a list of admin activities by central Admin 
	 */
	@Transactional
	public List<AdminActivity> getAllActivitiesForLocalAdmin(int siteId);	
	/**
	 * Log admin activity with description by admin
	 * @param description
	 * @param user
	 */
	@Transactional
	public void logAdminActivity(String description, int userId);
	
	/**
	 * Log activity
	 * @param activity
	 */	
	@Transactional
	public void logAdminActivity(AdminActivity activity);
	/**
	 * update activity
	 * @param activity
	 */
	@Transactional
	public void updateActivity(AdminActivity activity);	
	
	/**
	 * 
	 * @param activityId
	 * @return an activity by id
	 */
	@Transactional
	public AdminActivity getAdminActivityById(int activityId);
	
	/**
	 * delete activity by id
	 * @param id
	 */
	@Transactional
	public void deleteAdminActivityById(int id);		
	
	/**
	 * Save a copy of deleted activity
	 * @param activity
	 * @param deletedBy
	 * @param user
	 */
	@Transactional
	public void archiveAdmindActivity(AdminActivity activity, String deletedBy);
}
