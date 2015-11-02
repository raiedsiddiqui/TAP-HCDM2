package org.tapestry.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.tapestry.dao.ActivityDAO;
import org.tapestry.dao.AdminActivityDAO;
import org.tapestry.dao.UserDAO;
import org.tapestry.objects.AdminActivity;
import org.tapestry.objects.User;
import org.tapestry.objects.UserLog;

/**
 * Implementation for service UserManager
 * @author lxie 
 */
@Service
public class UserManagerImpl implements UserManager {
	@Autowired
	private UserDAO userDao;
	@Autowired
	private ActivityDAO activityDAO;
	@Autowired
	private AdminActivityDAO adminActivityDAO;
	
	@Override
	public int countActiveUsers() {
		return userDao.countActiveUsers();
	}

	@Override
	public int countAdministrators() {
		return userDao.countAdministrators();
	}

	@Override
	public int countAllUsers() {		
		return userDao.countAllUsers();
	}

	@Override
	public User getUserByID(int id) {		
		return userDao.getUserByID(id);
	}

	@Override
	public User getUserByUsername(String username) {		
		return userDao.getUserByUsername(username);
	}

	@Override
	public boolean createUser(User u) {		
		return userDao.createUser(u);
	}

	@Override
	public void modifyUser(User u) {
		userDao.modifyUser(u);
	}

	@Override
	public void removeUserWithID(int id) {
		userDao.removeUserWithID(id);
	}

	@Override
	public void removeUserByUsername(String username) {
		userDao.removeUserByUsername(username);		
	}

	@Override
	public void archiveUser(User user, String deletedBy) {
		userDao.archiveUser(user, deletedBy);
	}
	
	@Override
	public void disableUserWithID(int id) {
		userDao.disableUserWithID(id);
	}

	@Override
	public void enableUserWithID(int id) {
		userDao.enableUserWithID(id);
	}

	@Override
	public List<User> getAllUsers() {		
		return userDao.getAllUsers();
	}

	@Override
	public List<User> getUsersByPartialName(String partialName) {		
		return userDao.getUsersByPartialName(partialName);
	}

	@Override
	public List<User> getAllUsersWithRole(String role) {		
		return userDao.getAllUsersWithRole(role);
	}

	@Override
	public List<User> getAllActiveUsersWithRole(String role) {		
		return userDao.getAllActiveUsersWithRole(role);
	}

	@Override
	public List<User> getUsersByGroup(int organizationId) {
		return userDao.getUsersByGroup(organizationId);
	}
	

	@Override
	public List<User> getGroupedUsersByRole(int organizationId, String role) {
		return userDao.getGroupedUsersByRole(organizationId, role);
	}
	
	@Override
	public boolean userHasPassword(int id, String pwd) {		
		return userDao.userHasPassword(id, pwd);
	}

	@Override
	public void setPasswordForUser(int id, String pwd) {
		userDao.setPasswordForUser(id, pwd);
	}

	@Override
	public List<User> getVolunteerCoordinatorByOrganizationId(int id) {		
		return userDao.getVolunteerCoordinatorByOrganizationId(id);
	}

	@Override
	public List<User> getLocalAdminBySite(int id) {
		return userDao.getLocalAdminBySite(id);
	}
	
	@Override
	public List<User> getUserBySIte(int id) {
		return userDao.getUserBySIte(id);
	}
	
	@Override
	public List<UserLog> getUserLogs(int start, int n) {		
		return activityDAO.getUserLogsPage(start, n);
	}

	@Override
	public void addUserLog(String description, User user) {		
		activityDAO.addUserLog(description, user);		
	}

	@Override
	public List<UserLog> getUserLogsByPartialName(String partialName) {		
		return activityDAO.getUserLogsByPartialName(partialName);
	}

	@Override
	public int count() {		
		return activityDAO.countEntries();
	}
	
	@Override
	public int countEntriesByGroup(int organizationId) {
		return activityDAO.countEntriesByGroup(organizationId);
	}

	@Override
	public List<UserLog> getUserLogsPageByGroup(int start, int n, int organizationId) {
		return activityDAO.getUserLogsPageByGroup(start, n, organizationId);
	}

	@Override
	public List<UserLog> getGroupedUserLogssByPartialName(String partialName, int organizationId) {
		return activityDAO.getGroupedUserLogssByPartialName(partialName, organizationId);
	}

	@Override
	public List<AdminActivity> getAllAdminActivities(int userId) {
		return adminActivityDAO.getAllAdminActivities(userId);
	}

	@Override
	public List<AdminActivity> getAllAdminActivities() {
		return adminActivityDAO.getAllAdminActivities();
	}

	@Override
	public List<AdminActivity> getAllActivitiesForLocalAdmin(int siteId) {
		return adminActivityDAO.getAllActivitiesForLocalAdmin(siteId);
	}

	@Override
	public void logAdminActivity(String description, int userId) {
		adminActivityDAO.logAdminActivity(description, userId);		
	}

	@Override
	public void logAdminActivity(AdminActivity activity) {
		adminActivityDAO.logAdminActivity(activity);
	}

	@Override
	public void updateActivity(AdminActivity activity) {
		adminActivityDAO.updateActivity(activity);		
	}

	@Override
	public AdminActivity getAdminActivityById(int activityId) {
		return adminActivityDAO.getAdminActivityById(activityId);
	}

	@Override
	public void deleteAdminActivityById(int id) {
		adminActivityDAO.deleteAdminActivityById(id);
	}

	@Override
	public void archiveAdmindActivity(AdminActivity activity, String deletedBy) {
		adminActivityDAO.archiveAdmindActivity(activity, deletedBy);		
	}

	

}
