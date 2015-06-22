package org.tapestry.dao;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Test;
import org.tapestry.objects.Patient;

/**
* This class tests the PatientDao to see if an object can be created,
* edited, saved, and retrieved.
*/
public class PatientDaoTest{

	private final String DB = "jdbc:mysql://localhost/tapestry_db";
	private final String UN = "root";
	private final String PW = "root";

	@Test
	public void testGetById(){
		PatientDao dao = new PatientDao(DB, UN, PW);
		assertNotNull("DAO is null", dao);
		Patient p = dao.getPatientByID(1);
		assertNotNull("Patient is null", p);
		System.out.println("Patient with ID 2 is: " + p.getFirstName() + " " + p.getLastName());
	}

	@Test
	public void testGetAllPatients(){
		PatientDao dao = new PatientDao(DB, UN, PW);
		assertNotNull("DAO is null", dao);
		ArrayList<Patient> patients = dao.getAllPatients();
		assertNotNull("No patients returned", patients);
		System.out.println("Patients:");
		for (Patient p : patients){
		//	System.out.println("| " + p.getPatientID() + " | "  + p.getFirstName() + " | " + p.getLastName() + " | " + p.getColor() + " |");
			System.out.println("| " + p.getPatientID() + " | "  + p.getFirstName() + " | " + p.getLastName() + " | " );
		}
	}
}
