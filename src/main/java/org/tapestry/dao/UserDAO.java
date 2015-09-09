package org.tapestry.dao;

import java.util.List;

import org.springframework.stereotype.Service;
import org.tapestry.objects.User;

/**
 * Defines DAO operations for the User model.
 * 
 * @author lxie 
*/
@Service
public interface UserDAO {
	/**
	 * Count all active users
	 * @return
	 */
	public int countActiveUsers();
	/**
	 * Count all Admins
	 * @return
	 */
	public int countAdministrators();
	/**	 * 
	 * @return number of Users
	 */
	public int countAllUsers();
	/**
	 * 
	 * @param id
	 * @return a user by id
	 */
	public User getUserByID(int id);
	/**
	 * 
	 * @param username
	 * @return a user by username
	 */
	public User getUserByUsername(String username);
	/**
	 * Add a new user
	 * @param u
	 * @return
	 */
	public boolean createUser(User u);
	/**
	 * Modify a user
	 * @param u
	 */
	public void modifyUser(User u);
	/**
	 * Delete a use by id
	 * @param id
	 */
	public void removeUserWithID(int id);
	
	/**
	 * Delete a user by username
	 * @param username
	 */
	public void removeUserByUsername(String username);
	/**
	 * Disable a user by id
	 * @param id
	 */
	public void disableUserWithID(int id);
	/**
	 * Enable a user by id
	 * @param id
	 */
	public void enableUserWithID(int id);
	/**
	 * 
	 * @return a list of Users
	 */
	public List<User> getAllUsers();
	
	/**
	 * 
	 * @param organizationId
	 * @return a list of User who belong to same organization
	 */
	public List<User> getUsersByGroup(int organizationId);
	
	/**
	 * 
	 * @param organizationId
	 * @param role
	 * @return
	 */
	public List<User> getGroupedUsersByRole(int organizationId, String role);
	
	/**
	 * 
	 * @param partialName
	 * @return a list of users who's name contains partialName
	 */
	public List<User> getUsersByPartialName(String partialName);
	/**
	 * 
	 * @param role
	 * @return a list of user by selected role
	 */
	public List<User> getAllUsersWithRole(String role);
	/**
	 * 
	 * @param role
	 * @return a list of active user by selected role
	 */
	public List<User> getAllActiveUsersWithRole(String role);
	/**
	 * Check if user has password
	 * @param id
	 * @param pwd
	 * @return
	 */
	public boolean userHasPassword(int id, String pwd);
	/**
	 * Set password for a user
	 * @param id
	 * @param pwd
	 */
	public void setPasswordForUser(int id, String pwd);
	/**
	 * 
	 * @param id
	 * @return a list of central Admin by selected organization
	 */
	public List<User> getVolunteerCoordinatorByOrganizationId(int id);
	
	/**
	 * Group user by site
	 * @param id
	 * @return
	 */
	public List<User> getLocalAdminBySite(int id);
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public List<User> getUserBySIte(int id);
	
	/**
	 * Save a copy of deleted user
	 * @param user
	 * @param deletedBy
	 */
	public void archiveUser(User user, String deletedBy);
	
}
