package org.tapestry.dao;

import java.util.List;

import org.tapestry.objects.AdminActivity;

/**
 * Defines DAO operations for the AdminActivity model.
 * 
 * @author lxie 
*/
public interface AdminActivityDAO {
	/**
	 * 
	 * @param volunteer
	 * @return a list of AdminActivities by selected user(central admin/local admin)
	 */
	public List<AdminActivity> getAllAdminActivities(int userId);
	/**
	 * 
	 * @return a list of all admin activities 
	 */
	public List<AdminActivity> getAllAdminActivities();
	/**
	 * 
	 * @param id siteId
	 * @return a list of admin activities by central Admin 
	 */
	public List<AdminActivity> getAllActivitiesForLocalAdmin(int siteId);	
	/**
	 * Log admin activity with description by admin
	 * @param description
	 * @param user
	 */
	public void logAdminActivity(String description, int userId);
	
	/**
	 * Log activity
	 * @param activity
	 */			
	public void logAdminActivity(AdminActivity activity);
	/**
	 * update activity
	 * @param activity
	 */
	public void updateActivity(AdminActivity activity);	
	
	/**
	 * 
	 * @param activityId
	 * @return an activity by id
	 */
	public AdminActivity getAdminActivityById(int activityId);
	
	/**
	 * delete activity by id
	 * @param id
	 */
	public void deleteAdminActivityById(int id);		
	
	/**
	 * Save a copy of deleted activity
	 * @param activity
	 * @param deletedBy
	 * @param user
	 */
	public void archiveAdmindActivity(AdminActivity activity, String deletedBy);

}
