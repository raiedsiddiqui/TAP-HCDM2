package org.tapestry.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Test;
import org.tapestry.objects.User;

/**
* This class tests the UserDao to see if an object can be created,
* edited, saved, and retrieved.
*/
public class UserDaoTest{

	private final String DB = "jdbc:mysql://localhost/tapestry_db";
	private final String UN = "root";
	private final String PW = "root";

	@Test
	public void testGetById(){
		UserDao dao = new UserDao(DB, UN, PW);
		assertNotNull("DAO is null", dao);
		User u = dao.getUserByID(1);		
		assertNotNull("User is null", u);
		assertEquals("Username is not correct", "admin", u.getUsername());
	}

	@Test
	public void testGetByUsername(){
		UserDao dao = new UserDao(DB, UN, PW);
		assertNotNull("DAO is null", dao);
		User u = dao.getUserByUsername("admin");
		assertNotNull("User is null", u);
		assertEquals("Username is not correct", "admin", u.getUsername());
	}

	@Test
	public void testGetAllUsers(){
		UserDao dao = new UserDao(DB, UN, PW);
		assertNotNull("DAO is null", dao);
		ArrayList<User> users = dao.getAllUsers();
		assertNotNull("No users returned", users);
		System.out.println("Users:");
		for (User u : users){
			System.out.println("| " + u.getUserID() + " | "  + u.getName() + " | " + u.getUsername() + " | " + u.getEmail() + " | " + u.getRole() + " | " + u.isEnabled() + " | " +  u.getPassword() +" |");
		}
	}

	@Test
	public void testGetByRole(){
		UserDao dao = new UserDao(DB, UN, PW);
		assertNotNull("DAO is null", dao);
		ArrayList<User> volunteers = dao.getAllUsersWithRole("ROLE_USER");
		assertNotNull("No users returned", volunteers);
		System.out.println("Volunteers:");
		for (User u : volunteers){
			System.out.println(u.getName());
		}
	}

}
