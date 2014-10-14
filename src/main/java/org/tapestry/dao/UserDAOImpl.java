package org.tapestry.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.tapestry.controller.Utils;
import org.tapestry.objects.User;

/**
 * An implementation of the UserDAO interface.
 * 
 * lxie
 */

@Repository
public class UserDAOImpl extends JdbcDaoSupport implements UserDAO {
	@Autowired
	public UserDAOImpl(DataSource dataSource) {
 		setDataSource(dataSource);
    }

	@Override
	public int countActiveUsers() {
		String sql = "SELECT COUNT(*) as c FROM users WHERE enabled=1";		
		return getJdbcTemplate().queryForInt(sql);	
	}

	@Override
	public int countAdministrators() {
		String sql = "SELECT COUNT(*) as c FROM users WHERE role=ROLE_ADMIN";		
		return getJdbcTemplate().queryForInt(sql);
	}

	@Override
	public int countAllUsers() {
		String sql = "SELECT COUNT(*) as c FROM users";		
		return getJdbcTemplate().queryForInt(sql);
	}
	
	/**
	* Selects a user based off the user ID
	* @param id The ID of the user to search for
	* @return A User object representing the person
	*/
	@Override
	public User getUserByID(int id) {
		String sql = "SELECT * FROM users WHERE user_ID=?";		
		return getJdbcTemplate().queryForObject(sql, new Object[]{id}, new UserMapper());	
	}

	/**
	* Selects a user based off the username
	* @param username The username to search for
	* @return A User object representing the person, or null if the request fails
	*/
	@Override
	public User getUserByUsername(String username) {		
		String sql = "SELECT * FROM users WHERE username=?";
		return getJdbcTemplate().queryForObject(sql, new Object[]{username}, new UserMapper());	
	}

	/**
	* Stores a user in the database
	* @param u The User object to store
	*/
	@Override
	public boolean createUser(User u) {
		boolean success = false;	
		String sql = "SELECT * FROM users WHERE UPPER(username) LIKE UPPER(?)";
		
		List<User> user = getJdbcTemplate().query(sql, new Object[]{u.getUsername()}, new UserMapper());
		
		if (user.size() == 0)
		{
			sql = "INSERT INTO users (username, name, password, role, email,"
					+ " phone_number, site, organization, enabled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)";
			getJdbcTemplate().update(sql, u.getUsername(), u.getName(), u.getPassword(), u.getRole(), u.getEmail(),
				u.getPhoneNumber(), u.getSite(), u.getOrganization());
		
			success = true;
		}
		
		return success;
	}

	/**
	 * Modifies a user in the database
	 * @param u The User object containing the new data (the user_ID should match the object to update)
	 */
	@Override
	public void modifyUser(User u) {
		String sql = "UPDATE users SET username=?, name=?, email=? WHERE user_ID=?";
		getJdbcTemplate().update(sql,u.getUsername(), u.getName(), u.getEmail(), u.getUserID());
	}

	/**
	* Removes a user from the database, do 
	* @param id The ID of the user to remove
	*/
	@Override
	public void removeUserWithID(int id) {
		String sql = "DELETE FROM users WHERE user_ID=?";
		getJdbcTemplate().update(sql, id);

	}
	
	/**
	 * Disables a user
	 * Useful for closing access to an account without losing data associated with it
	 * @param id The ID of the user to disable
	 */
	@Override
	public void disableUserWithID(int id) {
		String sql = "UPDATE users SET enabled=0 WHERE user_ID=?";
		getJdbcTemplate().update(sql, id);
	}

	/**
	 * Enables a user
	 * Logically follows from above
	 * @param id The ID of the user to disable
	 */
	@Override
	public void enableUserWithID(int id) {
		String sql = "UPDATE users SET enabled=1 WHERE user_ID=?";
		getJdbcTemplate().update(sql, id);

	}

	/**
	* Lists all the users in the database
	* @return a List of User objects, or null if the request fails
	*/
	@Override
	public List<User> getAllUsers() {
		String sql = "SELECT * FROM users";
		List <User> users = getJdbcTemplate().query(sql, new UserMapper());
		
		return users;
	}
	
	/**
	 * List all the users which name contain 'partialName'
	 * @return a List of User objects, or null if the request fails
	 */
	@Override
	public List<User> getUsersByPartialName(String partialName) {
		String sql = "SELECT * FROM users WHERE UPPER(name) LIKE UPPER('%" + partialName + "%')";
		List<User> users = getJdbcTemplate().query(sql, new UserMapper());
		
		return users;
	}
	
	/**
	* Lists all the users in the database with the specified role
	* @param role The role to search for ("ROLE_USER" or "ROLE_ADMIN")
	* @return An ArrayList of User objects, or null if the request fails
	*/
	@Override
	public List<User> getAllUsersWithRole(String role) {
		String sql = "SELECT * FROM users WHERE role=?";		
		List<User> users = getJdbcTemplate().query(sql, new Object[]{role}, new UserMapper());
		
		return users;
	}

	/**
	* Lists all the active users in the database with the specified role
	* @param role The role to search for ("ROLE_USER" or "ROLE_ADMIN")
	* @return An ArrayList of User objects, or null if the request fails
	*/
	@Override
	public List<User> getAllActiveUsersWithRole(String role) {
		String sql = "SELECT * FROM users WHERE role=? AND enabled=1";
		List<User> users = getJdbcTemplate().query(sql, new Object[]{role}, new UserMapper());
		
		return users;
	}

	/**
	 * Checks if the specified user has the given password
	 * @param id The ID of the user to check
	 * @param pwd The password to check (plaintext)
	 */
	@Override
	public boolean userHasPassword(int id, String pwd) {
		ShaPasswordEncoder enc = new ShaPasswordEncoder();
		String hashedPassword = enc.encodePassword(pwd, null);
		
		String sql = "SELECT password FROM users WHERE user_ID=?";
		User user = getJdbcTemplate().queryForObject(sql, new Object[]{id}, new RowMapper<User>() {
			 
            @Override
            public User mapRow(ResultSet rs, int rowNumber) throws SQLException {
                User user = new User();
                user.setPassword(rs.getString("password"));
                return user;
            }             
        });
		
        return hashedPassword.equals(user.getPassword());		
	}
	
	/**
	* Set password for a User 
	* @param userId and password	
	*/
	@Override
	public void setPasswordForUser(int id, String pwd) {
		String sql = "UPDATE users SET password=? WHERE user_ID=?";
		getJdbcTemplate().update(sql, pwd, id);

	}

	/**
	* Creates a User object from the result of executing an SQL query
	* @param result The ResultSet returned by calling PreparedStatement.executeQuery()
	* @return A User object representing the person
	*/
	@Override
	public List<Integer> getVolunteerCoordinatorByOrganizationId(int id) {
		String sql = "SELECT * FROM users WHERE organization=? AND role ='ROLE_LOCAL_ADMIN'";
		List<User> users = getJdbcTemplate().query(sql, new Object[]{id}, new UserMapper());
		
		List<Integer> coordinatorIds = new ArrayList<Integer>();
		for(User u: users)
			coordinatorIds.add(u.getUserID());
		
		return coordinatorIds;
	}
	
	class UserMapper implements RowMapper<User> {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException{
			User u = new User();
			u.setName(rs.getString("name"));
			u.setUserID(rs.getInt("user_ID"));
			u.setRole(rs.getString("role"));
			u.setUsername(rs.getString("username"));
			u.setEmail(rs.getString("email"));
			u.setPassword(rs.getString("password"));
			u.setOrganization(rs.getInt("organization"));
			
			if (!Utils.isNullOrEmpty(rs.getString("site")))
				u.setSite(rs.getString("site"));
			if (!Utils.isNullOrEmpty(rs.getString("phone_number")))
				u.setPhoneNumber(rs.getString("phone_number"));
			
			if (rs.getString("enabled").equals("1"))
				u.setEnabled(true);
			else
				u.setEnabled(false);
			return u;
		}
	}

}
