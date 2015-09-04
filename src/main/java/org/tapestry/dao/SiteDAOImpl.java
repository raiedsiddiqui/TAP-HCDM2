package org.tapestry.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.tapestry.objects.Site;

@Repository
public class SiteDAOImpl extends JdbcDaoSupport implements SiteDAO {

	@Autowired
	public SiteDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
    }

	@Override
	public List<Site> getAllSites() {
		String sql = "SELECT sites.* FROM sites ORDER BY sites.site_ID";				
		return getJdbcTemplate().query(sql, new SiteMapper());
	}

	@Override
	public Site getSiteById(int siteId) {
		String sql = "SELECT sites.* FROM sites WHERE site_ID = ? ORDER BY site_ID";				
		return getJdbcTemplate().queryForObject(sql, new Object[]{siteId}, new SiteMapper());		
	}	

	@Override
	public void addSite(Site site) {
		String sql = "INSERT INTO sites (site_name) VALUES (?)";
		getJdbcTemplate().update(sql, site.getName());	
	}

	@Override
	public void modifySite(Site site) {
		String sql = "UPDATE sites SET site_name=? WHERE site_ID=?";
		getJdbcTemplate().update(sql, site.getName(), site.getSiteId());	
	}

	@Override
	public void deleteSite(int siteId) {
		String sql = "DELETE FROM sites WHERE site_ID=?";
	    getJdbcTemplate().update(sql, siteId);
	}
	
	//RowMapper
	class SiteMapper implements RowMapper<Site> {
		public Site mapRow(ResultSet rs, int rowNum) throws SQLException{
			Site site = new Site();
				
			site.setSiteId(rs.getInt("site_ID"));
			site.setName(rs.getString("site_name"));
			
			return site;		
			}
		}


}
