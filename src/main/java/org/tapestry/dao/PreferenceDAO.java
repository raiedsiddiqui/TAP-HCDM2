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

}
