package org.tapestry.myoscar.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

import org.oscarehr.myoscar.client.ws_manager.AccountManager;
import org.oscarehr.myoscar.client.ws_manager.GroupManager;
import org.oscarehr.myoscar.client.ws_manager.RoleRelationshipManager;
import org.oscarehr.myoscar.client.ws_manager.MessageManager;
import org.oscarehr.myoscar_server.ws.LoginResultTransfer3;
import org.oscarehr.myoscar_server.ws.PersonTransfer3;
import org.oscarehr.myoscar_server.ws.RelationshipTransfer6;
import org.oscarehr.util.MiscUtils;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

public class ClientManager {
	private static final String MYOSCAR_URL = "myoscar_url";
	private static final String MYOSCAR_USERNAME = "myoscar_username";
	private static final String MYOSCAR_PASSWORD = "myoscar_password";
	private static final String TAPESTRY_GROUP_ID = "tapestry_groupId";
	
	private static final Map<String, String> intiMyOscarWS = readMyOscarWSConfig();
	private static final String serverUrl = intiMyOscarWS.get(MYOSCAR_URL);	
	private static final String user = intiMyOscarWS.get(MYOSCAR_USERNAME);	
	private static final String password = intiMyOscarWS.get(MYOSCAR_PASSWORD);
	private static final String groupId = intiMyOscarWS.get(TAPESTRY_GROUP_ID);
	
	private static ClassPathResource dbConfigFile;
	private static Map<String, String> config;
	private static Yaml yaml;	
	
	public static PersonTransfer3 getClientByUsername(String userName) throws Exception
	{			
		MiscUtils.setJvmDefaultSSLSocketFactoryAllowAllCertificates();
				
		LoginResultTransfer3 loginResultTransfer = AccountManager.login(serverUrl, user, password);		
		
		MyOscarCredentialsImpl credentials=new MyOscarCredentialsImpl(serverUrl, loginResultTransfer.getPerson().getId(), 
				loginResultTransfer.getSecurityTokenKey(), "fake sessionId, not from browser", Locale.ENGLISH);		
		
		//--- this will retrieve an account and print some of the patients demographic information ---		
		PersonTransfer3 client = AccountManager.getPerson(credentials, userName);	
		
		return client;
	}
	
	public static PersonTransfer3 getClientById(Long id) throws Exception
	{			
		MiscUtils.setJvmDefaultSSLSocketFactoryAllowAllCertificates();
				
		LoginResultTransfer3 loginResultTransfer = AccountManager.login(serverUrl, user, password);		
		
		MyOscarCredentialsImpl credentials=new MyOscarCredentialsImpl(serverUrl, loginResultTransfer.getPerson().getId(), 
				loginResultTransfer.getSecurityTokenKey(), "fake sessionId, not from browser", Locale.ENGLISH);		
		
		//--- this will retrieve an account and print some of the patients demographic information ---
		
		PersonTransfer3 client = AccountManager.getPerson(credentials, id);	
		
		return client;
	}
	
	public static List<PersonTransfer3> getClients() throws Exception
	{		
		MiscUtils.setJvmDefaultSSLSocketFactoryAllowAllCertificates();	
		Long lGroupId  = new Long(groupId);		
		List<PersonTransfer3> patients = new ArrayList<PersonTransfer3>();
		try{
			LoginResultTransfer3 loginResultTransfer = AccountManager.login(serverUrl, user, password);		
			
			MyOscarCredentialsImpl credentials=new MyOscarCredentialsImpl(serverUrl, loginResultTransfer.getPerson().getId(), 
					loginResultTransfer.getSecurityTokenKey(), "fake sessionId, not from browser", Locale.ENGLISH);	
			
			List<Long> patientIds = GroupManager.getMembersByPeopleGroupId(credentials, lGroupId, 0, 100);			
			PersonTransfer3 person;
			
			if (patientIds != null)
			{
				 for(Long id: patientIds)
				 {
					 person = new PersonTransfer3();
					 person = AccountManager.getPerson(credentials, id);
					 
					 patients.add(person);					 
				 }
			}
			else
				System.out.println("There not any client in tapestry group" );			
			
		}catch(Exception e)
		{
			e.getMessage();
			System.out.println(e.getMessage());
		}
		return patients;
	}	
	
	public static List<RelationshipTransfer6> getMRPs(Long patientId) throws Exception {
		MiscUtils.setJvmDefaultSSLSocketFactoryAllowAllCertificates();
		
		LoginResultTransfer3 loginResultTransfer = AccountManager.login(serverUrl, user, password);		
		
		MyOscarCredentialsImpl credentials=new MyOscarCredentialsImpl(serverUrl, loginResultTransfer.getPerson().getId(), 
				loginResultTransfer.getSecurityTokenKey(), "fake sessionId, not from browser", Locale.ENGLISH);
		
		List<RelationshipTransfer6> primaryCareProviders = RoleRelationshipManager.getRelationshipsByPersonId(credentials,patientId, 0, 100);
		
		if (primaryCareProviders != null)
			System.out.println("size of PHR is === "+ primaryCareProviders.size());
		else
			System.out.println("size of PHR is === null");
		
		return  primaryCareProviders;
	}
	
	public static Long sentMessageToPatientInMyOscar(Long patientId, String subject, String contents) throws Exception{
		MiscUtils.setJvmDefaultSSLSocketFactoryAllowAllCertificates();
		LoginResultTransfer3 loginResultTransfer = AccountManager.login(serverUrl, user, password);	
		
		MyOscarCredentialsImpl credentials=new MyOscarCredentialsImpl(serverUrl, loginResultTransfer.getPerson().getId(), 
				loginResultTransfer.getSecurityTokenKey(), "fake sessionId, not from browser", Locale.ENGLISH);
		
		Long success = MessageManager.sendMessage(credentials, patientId, subject, contents);
		return success;
	}	
	
	private static Map<String, String> readMyOscarWSConfig()
	{
		Map<String, String> myOscarWSInit = new HashMap<String, String>();
		try{
			dbConfigFile = new ClassPathResource("tapestry.yaml");
			yaml = new Yaml();
			config = (Map<String, String>) yaml.load(dbConfigFile.getInputStream());
			
			String myOscarServerBaseUrl = config.get(MYOSCAR_URL);			
			String myOscarUserName = config.get(MYOSCAR_USERNAME);
			String myOscarPwd = config.get(MYOSCAR_PASSWORD);
			String strGroupId = config.get(TAPESTRY_GROUP_ID);
			
			myOscarWSInit.put(MYOSCAR_URL, myOscarServerBaseUrl);
			myOscarWSInit.put(MYOSCAR_USERNAME, myOscarUserName);
			myOscarWSInit.put(MYOSCAR_PASSWORD, myOscarPwd);
			myOscarWSInit.put(TAPESTRY_GROUP_ID, strGroupId);
			
		} catch (IOException e) {
			System.out.println("Error reading from config file");
			System.out.println(e.toString());
		}
		
		return myOscarWSInit;
	}

}
