package org.tapestry.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.tapestry.objects.Clinic;

@Repository
public class ClinicDAOImpl extends JdbcDaoSupport implements ClinicDAO {

	@Autowired
	public ClinicDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
    }
	@Override
	public List<Clinic> getAllClinics() {
		String sql = "SELECT clinics.*, sites.site_name FROM clinics INNER JOIN sites ON clinics.site_ID = sites.site_ID "
				+ "ORDER BY clinics.site_ID";
				
		return getJdbcTemplate().query(sql, new ClinicMapper());
	}

	@Override
	public List<Clinic> getClinicsBySite(int siteId) {
		String sql = "SELECT clinics.*, sites.site_name FROM clinics INNER JOIN sites ON clinics.site_ID = sites.site_ID "
				+ "WHERE clinics.site_ID = ? ORDER BY clinics.site_ID";
				
		return getJdbcTemplate().query(sql, new Object[]{siteId}, new ClinicMapper());
	}

	@Override
	public Clinic getClinicById(int clinicId) {
		String sql = "SELECT clinics.*, sites.site_name FROM clinics INNER JOIN sites ON clinics.site_ID = sites.site_ID "
				+ "WHERE clinics.clinic_ID = ? ORDER BY clinics.site_ID";
				
		return getJdbcTemplate().queryForObject(sql, new Object[]{clinicId}, new ClinicMapper());		
	}
	

	@Override
	public int getSiteByClinic(int clinicId) {		
		String sql = "SELECT site_ID FROM clinics WHERE clinic_ID = ?";				
		return getJdbcTemplate().queryForInt(sql, new Object[]{clinicId});
	}


	@Override
	public void addClinic(Clinic clinic) {
		String sql = "INSERT INTO clinics (clinic_name, address, phone, site_ID) VALUES (?, ?, ?, ?)";
		getJdbcTemplate().update(sql, clinic.getClinicName(), clinic.getAddress(), clinic.getPhone(), clinic.getSiteId());		
	}

	@Override
	public void modifyClinic(Clinic clinic) {
		String sql = "UPDATE clinics SET clinic_name=?, address=?, phone=?, site_ID=? WHERE clinic_ID=?";
		getJdbcTemplate().update(sql, clinic.getClinicName(), clinic.getAddress(), clinic.getPhone(), clinic.getSiteId(),
				clinic.getClinicId());	
	}

	@Override
	public void deleteClinic(int clinicId) {
		String sql = "DELETE FROM clinics WHERE clinic_ID=?";
	    getJdbcTemplate().update(sql, clinicId);
		
	}
	
	//RowMapper
	class ClinicMapper implements RowMapper<Clinic> {
		public Clinic mapRow(ResultSet rs, int rowNum) throws SQLException{
			Clinic clinic = new Clinic();
			
			clinic.setClinicId(rs.getInt("clinic_ID"));
			clinic.setAddress(rs.getString("address"));
			clinic.setClinicName(rs.getString("clinic_name"));
			clinic.setPhone(rs.getString("phone"));
			clinic.setSiteId(rs.getInt("site_ID"));
			clinic.setSiteName(rs.getString("site_name"));
			
			return clinic;		
		}
	}

}
