package org.tapestry.dao;

import org.tapestry.objects.Preference;

/**
 * Defines DAO operations for the Preference model.
 * 
 * @author lxie 
*/
public interface PreferenceDAO {
	
	/**
	 * 
	 * @param site
	 * @return an object of Preference
	 */
	public Preference getPreferenceBySite(int site);
	
	/**
	 * add new preference setting for a site
	 * @param preference
	 */
	public void addPreference(Preference preference);
	
	/**
	 * update site's preference
	 * @param preference
	 */
	public void updatePreference(Preference preference);
	
	/**	
	 * @param site
	 * @return
	 */
	public String getSosReciversBySite(int site);
	
	/** 
	 * @param site
	 * @return
	 */
	public String getApptNotiReceiversBySite(int site);
	
	/**
	 * @param site
	 * @return
	 */
	public String getReportNotiReceiversBySite(int site);
	
	/**
	 * @param site
	 * @return
	 */
	public String getSocialContextOnReportBySite(int site);
	
	/**
	 * @param site
	 * @return
	 */
	public String getSocialContextTemplateBySite(int site);
	
	/**
	 * @param site
	 * @return
	 */
	public String getAlertsOnReportBySite(int site);
	
	/**
	 * @param site
	 * @return
	 */
	public String getAlertsContentBySite(int site);

}
