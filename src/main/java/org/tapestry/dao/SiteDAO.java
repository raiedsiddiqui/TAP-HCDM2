package org.tapestry.dao;

import java.util.List;

import org.tapestry.objects.Site;

/**
 * Defines DAO operations for the site model.
 * 
 * @author lxie 
 */

public interface SiteDAO {
	/** 
	 * @return all sites
	 */
	public List<Site> getAllSites();
			
	/**
	 * 
	 * @param siteId
	 * @return
	 */
	public Site getSiteById(int siteId);
	
		
	/**
	 * 
	 * @param site
	 */
	public void addSite(Site site);
	
	/**
	 * 
	 * @param site
	 */
	public void modifySite(Site site);
	
	/**
	 * 
	 * @param siteId
	 */
	public void deleteSite(int siteId);

}
