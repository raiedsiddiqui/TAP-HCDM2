package org.tapestry.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.oscarehr.myoscar_server.ws.PersonTransfer3;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.survey_component.actions.SurveyAction;
import org.survey_component.data.PHRSurvey;
import org.survey_component.data.SurveyException;
import org.survey_component.data.SurveyQuestion;
import org.survey_component.data.answer.SurveyAnswer;
import org.survey_component.data.answer.SurveyAnswerFactory;
import org.survey_component.source.SurveyParseException;
import org.tapestry.utils.Utils;
//import org.apache.commons.lang.StringUtils;
import org.tapestry.myoscar.utils.ClientManager;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Availability;
import org.tapestry.objects.CareGiverResearchData;
import org.tapestry.objects.Clinic;
import org.tapestry.objects.DisplayedSurveyResult;
import org.tapestry.objects.HL7Report;
import org.tapestry.objects.Message;
import org.tapestry.objects.Patient;
import org.tapestry.objects.Report;
import org.tapestry.objects.ResearchData;
import org.tapestry.objects.Site;
import org.tapestry.objects.SurveyResult;
import org.tapestry.objects.SurveyTemplate;
import org.tapestry.objects.UBCClientResearchData;
import org.tapestry.objects.User;
import org.tapestry.objects.Volunteer;
import org.tapestry.report.AlertManager;
import org.tapestry.report.AlertsInReport;
import org.tapestry.report.CalculationManager;
import org.tapestry.report.ScoresInReport;
import org.tapestry.service.AppointmentManager;
import org.tapestry.service.MessageManager;
import org.tapestry.service.OrganizationManager;
import org.tapestry.service.PatientManager;
import org.tapestry.service.SurveyManager;
import org.tapestry.service.UserManager;
import org.tapestry.service.VolunteerManager;
import org.tapestry.surveys.DoSurveyAction;
import org.tapestry.surveys.ResultParser;
import org.tapestry.surveys.SurveyActionMumps;
import org.tapestry.surveys.SurveyFactory;
import org.tapestry.surveys.TapestryPHRSurvey;
import org.tapestry.surveys.TapestrySurveyMap;
import org.yaml.snakeyaml.Yaml;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
/**
 * all help functions in Controller for TAP-OA
 * @author lxie
 *
 */
public class TapestryHelper {	
	//Mail-related settings
   	private static String mailAddress = "";
   	private static Session session;
   	//Survey's title in the mumps script
   	public static final String titleOfDailyLifeActivities = "Daily Life Activities";
	public static final String titleOfGeneralHealth = "General Health";//EFS, Edminton Frail Scale
	public static final String titleOfSocialLife = "Social Life"; //Duke_Social_Support_Index
	public static final String titleOfNutrition = "Nutrition"; //Screen II
	public static final String titleOfAdvancedCarePlanning = "Advanced Care Planning"; //Advance Directives
	public static final String titleOfMemory = "Your Memory"; //Memory
	public static final String titleOfRAPA = "RAPA (Rapid Assessment of Physical Activity)";
	public static final String titleOfMobility = "Mobility Survey";
	public static final String titleOfGoals = "Goals";
	public static final String titleOfEQ5D = "EQ5D";
   	/**
   	 * Reads the file /WEB-INF/classes/db.yaml and gets the values contained therein
   	 * Set up for mail system
   	 */
   	
   	public static void readConfig(){
   		//Mail-related settings;
   		final Map<String, String> config;
   	   	final Yaml yaml;
   		
   		String mailHost = "";
   		String mailUser = "";
   		String mailPassword = "";
   		String mailPort = "";
   		String useTLS = "";
   		String useAuth = "";
		try{
			ClassPathResource dbConfigFile = new ClassPathResource("tapestry.yaml");
			yaml = new Yaml();
			config = (Map<String, String>) yaml.load(dbConfigFile.getInputStream());
		
			mailHost = config.get("mailHost");
			mailUser = config.get("mailUser");
			mailPassword = config.get("mailPassword");
			String mailAddress = config.get("mailFrom");
			mailPort = config.get("mailPort");
			useTLS = config.get("mailUsesTLS");
			useAuth = config.get("mailRequiresAuth");
			
		} catch (IOException e) {
			System.out.println("Error reading from config file");
			System.out.println(e.toString());			
			e.printStackTrace();
		}
		
		//Mail-related settings
		final String username = mailUser;
		final String password = mailPassword;
		Properties props = System.getProperties();
		Session session = Session.getDefaultInstance(props, 
				 new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
		  		});
		props.setProperty("mail.smtp.host", mailHost);
		props.setProperty("mail.smtp.socketFactory.port", mailPort);
		props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.smtp.auth", useAuth);
		props.setProperty("mail.smtp.starttls.enable", useTLS);
		props.setProperty("mail.user", mailUser);
		props.setProperty("mail.password", mailPassword);
   	}    	
   	//=================== Client(Patient) ===========================//
	/** 
	 * @param patientManager
	 * @param request
	 * @return a list of Patient Object which contain all patient's infos, including those 
	 * Tapestry DB don't have, for example Age, City and HomePhone, need to get from MyOscar(PHR)
	 */
	public static List<Patient> getAllPatientsWithFullInfos(PatientManager patientManager, 
			SecurityContextHolderAwareRequestWrapper request){			
		User user = getLoggedInUser(request);
		List<Patient> patients;
		HttpSession session = request.getSession();
		StringBuffer sb;
		String street1, street2, city, province;
		
		if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole()))
			patients = patientManager.getAllPatients();	//For central Admin		
		else
			patients = patientManager.getPatientsBySite(user.getSite());

		
		if (session.getAttribute("allPatientWithFullInfos") != null)
			patients = (List<Patient>)session.getAttribute("allPatientWithFullInfos");
		else
		{
			int age;				
			try {			
				List<PersonTransfer3> patientsInMyOscar = ClientManager.getClients();
				
				for(PersonTransfer3 person: patientsInMyOscar)
				{	
					age = Utils.getAgeByBirthDate(person.getBirthDate());
					
					for(Patient p: patients)
					{
						if (person.getUserName().equals(p.getUserName()))
						{
							Calendar birthDate = person.getBirthDate();						
							if (birthDate != null)
								p.setBod(Utils.getDateByCalendar(birthDate));
							
							p.setAge(age);
							
							sb = new StringBuffer();
							street1 = person.getStreetAddress1();
							street2 = person.getStreetAddress2();
							city = person.getCity();
							province = person.getProvince();
							p.setCity(city);					
	
							if (!Utils.isNullOrEmpty(street1))
							{
								sb.append(street1);	
								sb.append(", ");
							}
							else if ((!Utils.isNullOrEmpty(street2)))
							{
								sb.append(street2);	
								sb.append(", ");
							}
							
							if (!Utils.isNullOrEmpty(city))
							{
								sb.append(city);
								sb.append(", ");
							}
							
							if (!Utils.isNullOrEmpty(province))
								sb.append(province);
							
							p.setAddress(sb.toString());
							
							break;
						}//end of match patient's username 
					}//end of loop of all patients in Tapestry
				}//end of loop of all patient in MyOscar				
			} catch (Exception e) {
				System.out.println("something wrong when calling myoscar server...getClientList");			
				e.printStackTrace();
			}			
			session.setAttribute("allPatientWithFullInfos", patients);
		}		
		return patients;
	}

	public static Patient getPatientWithFullInfos(Patient p){		
		System.out.println("hillll");
		
		try{			
			String userName = p.getUserName();	
			System.out.println("usrname="+ userName);
			
			//username in MyOscar			
			if (!Utils.isNullOrEmpty(userName))
			{
				PersonTransfer3 person = ClientManager.getClientByUsername(userName);				
				int age = Utils.getAgeByBirthDate(person.getBirthDate());
				Calendar birthDate = person.getBirthDate();						
				if (birthDate != null)
					p.setBod(Utils.getDateByCalendar(birthDate));								
				p.setAge(age);
				if (person.getStreetAddress1() != null)						
					p.setStreetAddress(person.getStreetAddress1());
				else if(person.getStreetAddress2() != null)
					p.setStreetAddress(person.getStreetAddress2());				
				p.setCity(person.getCity());		
				p.setProvice(person.getProvince());
				p.setPostalCode(person.getPostalCode());				
		//		p.setHomePhone(person.getPhone1());
			}
			else
				System.out.println("Patient's username is empty...");				
		} catch (Exception e) {
			System.out.println("something wrong when calling myoscar server...getClientByUsername...");			
			e.printStackTrace();
		}			
		return p;
	}
	/**
	 * get patients from session or from DB
	 * @param request
	 * @param patientManager
	 * @return a list of Patient
	 */
   	public static List<Patient> getPatients(SecurityContextHolderAwareRequestWrapper request, PatientManager patientManager )
   	{
   		HttpSession session = request.getSession();		
		List<Patient> patients;
		if (session.getAttribute("patient_list") == null)
		{
			patients = patientManager.getAllPatients();			
			//save in the session
			if (patients != null && patients.size()>0)
				session.setAttribute("patient_list", patients);
		}
		else
			patients = (List<Patient>)session.getAttribute("patient_list");
		
		return patients;	
   	}
   	
	/**
	 * get patients from session or from DB
	 * @param request
	 * @param organizationId
	 * @param patientManager
	 * @return a list of Patient
	 */
   	public static List<Patient> getPatientsBySite(SecurityContextHolderAwareRequestWrapper request, 
   			PatientManager patientManager, int siteId )
   	{   		
   		HttpSession session = request.getSession();		
		List<Patient> patients;
		if (session.getAttribute("grouped_patient_list") == null)
		{
			patients = patientManager.getPatientsBySite(siteId);	
			//save in the session
			if (patients != null && patients.size()>0)
				session.setAttribute("grouped_patient_list", patients);
		}
		else
			patients = (List<Patient>)session.getAttribute("grouped_patient_list");
		
		return patients;	
   	}
   	
   	/**
   	 * get selected patient id which is stored in the session
	 * when a patient is selected from clients list in the main page
   	 * @param request
   	 * @return patient ID
   	 */
	public static int getPatientId(SecurityContextHolderAwareRequestWrapper request){
		int patientId = 0;	
		HttpSession session = request.getSession();
		if (session.getAttribute("patientId") != null){			
			patientId = Integer.parseInt(session.getAttribute("patientId").toString());
		}		
		return patientId;
	}
	
	/**
	 * get selected appointment id which is stored in the session
	 *  when an appointment is selected in the main page
	 * @param request
	 * @return
	 */
	public static int getAppointmentId(SecurityContextHolderAwareRequestWrapper request){
		int appointmentId = 0;		
		HttpSession session = request.getSession();
		
		if (session.getAttribute("appointmentId") != null){			
			appointmentId = Integer.parseInt(session.getAttribute("appointmentId").toString());
		}		
		return appointmentId;
	}
	// ======================User/Admin/Volunteer ==============================//
	/**
	 * 
	 * @param request
	 * @return login volunteer's ID
	 */
	public static int getLoggedInVolunteerId(SecurityContextHolderAwareRequestWrapper request){
		int volunteerId = 0;
		HttpSession session = request.getSession();
		
		if (session.getAttribute("logged_in_volunteer") != null){			
			volunteerId = Integer.parseInt(session.getAttribute("logged_in_volunteer").toString());
		}
		
		return volunteerId;
	}
	/**
	 * 
	 * @param request
	 * @param userManager
	 * @return logged in user
	 */
	public static User getLoggedInUser(SecurityContextHolderAwareRequestWrapper request, UserManager userManager){
		HttpSession session = request.getSession();
		String name = null;		
		User loggedInUser = null;
		//check if loggedInUserId is in the session
		if (session.getAttribute("loggedInUser") != null) //get loggedInUser from session			
			loggedInUser = (User)session.getAttribute("loggedInUser");		
		else if (request.getUserPrincipal() != null){		
			//get loggedInUser from request
			name = request.getUserPrincipal().getName();	
					
			if (name != null){			
				loggedInUser = userManager.getUserByUsername(name);								
				session.setAttribute("loggedInUser", loggedInUser);	
			}
		}
		return loggedInUser;
	}

	/**
	 * get logged in user from session
	 * @param request
	 * @return
	 */
	public static User getLoggedInUser(SecurityContextHolderAwareRequestWrapper request){
		HttpSession session = request.getSession();		
		return (User)session.getAttribute("loggedInUser");
	}
	
	/**
	 * 
	 * @param request
	 * @return login user
	 */
	public static User getLoggedInUser(HttpServletRequest request){
		HttpSession session = request.getSession();		
		return (User)session.getAttribute("loggedInUser");
	}
	
	/**
	 * get logged in user from session
	 * @param request
	 * @return
	 */
	public static User getLoggedInUser(MultipartHttpServletRequest request){
		HttpSession session = request.getSession();		
		return (User)session.getAttribute("loggedInUser");		
	}
	
	/**
	 * maintain user table as well
	 * @param volunteer
	 * @param userManager
	 */
	public static void modifyUser(Volunteer volunteer, UserManager userManager){				
		User user = userManager.getUserByUsername(volunteer.getUserName());
		
		StringBuffer sb = new StringBuffer();
		sb.append(volunteer.getFirstName());
		sb.append(" ");
		sb.append(volunteer.getLastName());
		user.setName(sb.toString());
			
		user.setEmail(volunteer.getEmail());
		userManager.modifyUser(user);
		
		//update password 
//		userManager.setPasswordForUser(user.getUserID(), volunteer.getPassword());		removed changing password from this page
	}
	/**
	 * retrieve all volunteers who has availability set up 
	 * @return
	 */
	public static List<Volunteer> getAllVolunteers(VolunteerManager volunteerManager){		
		return volunteerManager.getVolunteersWithAvailability();
	}
	/**
	 * 
	 * @param request
	 * @return volunteer's availability
	 */
	public static String getAvailableTime(SecurityContextHolderAwareRequestWrapper request)
	{			
		String strAvailableTime = "";
		List<String> availability = new ArrayList<String>();
		String mondayNull = request.getParameter("mondayNull");
		String tuesdayNull = request.getParameter("tuesdayNull");
		String wednesdayNull = request.getParameter("wednesdayNull");
		String thursdayNull = request.getParameter("thursdayNull");
		String fridayNull = request.getParameter("fridayNull");		
		String saturdayNull = request.getParameter("saturdayNull");
		String sundayNull = request.getParameter("sundayNull");
		String from1, from2, to1, to2;		
		
		//get availability for Monday
		if (!"non".equals(mondayNull))
		{
			from1 = request.getParameter("monFrom1");		
			from2 = request.getParameter("monFrom2");
			to1 = request.getParameter("monTo1");
			to2 = request.getParameter("monTo2");
			
			if ((from1.equals(from2))&&(from1.equals("0")))
				availability.add("1non");	
			else
			{
				availability = Utils.getAvailablePeriod(from1, to1, availability);
				availability = Utils.getAvailablePeriod(from2, to2, availability);
			}
		}
		else
			availability.add("1non");		
		
		//get availability for Tuesday
		if(!"non".equals(tuesdayNull))
		{
			from1 = request.getParameter("tueFrom1");
			from2 = request.getParameter("tueFrom2");
			to1 = request.getParameter("tueTo1");
			to2 = request.getParameter("tueTo2");		
			
			if ((from1.equals(from2))&&(from1.equals("0")))
				availability.add("2non");	
			else
			{
				availability = Utils.getAvailablePeriod(from1, to1, availability);				
				availability = Utils.getAvailablePeriod(from2, to2, availability);
			}
		}
		else
			availability.add("2non");
		
		//get availability for Wednesday
		if (!"non".equals(wednesdayNull))
		{
			from1 = request.getParameter("wedFrom1");
			from2 = request.getParameter("wedFrom2");
			to1 = request.getParameter("wedTo1");
			to2 = request.getParameter("wedTo2");
			
			if ((from1.equals(from2))&&(from1.equals("0")))
				availability.add("3non");	
			else
			{
				availability = Utils.getAvailablePeriod(from1, to1, availability);
				availability = Utils.getAvailablePeriod(from2, to2, availability);					
			}
		}
		else
			availability.add("3non");
		
		//get availability for Thursday
		if (!"non".equals(thursdayNull))
		{
			from1 = request.getParameter("thuFrom1");
			from2 = request.getParameter("thuFrom2");
			to1 = request.getParameter("thuTo1");
			to2 = request.getParameter("thuTo2");
			
			if ((from1.equals(from2))&&(from1.equals("0")))
				availability.add("4non");	
			else
			{
				availability = Utils.getAvailablePeriod(from1, to1, availability);
				availability = Utils.getAvailablePeriod(from2, to2, availability);	
			}
		}
		else
			availability.add("4non");
		
		//get availability for Friday
		if(!"non".equals(fridayNull))
		{			
			from1 = request.getParameter("friFrom1");
			from2 = request.getParameter("friFrom2");
			to1 = request.getParameter("friTo1");
			to2 = request.getParameter("friTo2");	
			
			if ((from1.equals(from2))&&(from1.equals("0")))
				availability.add("5non");	
			else
			{
				availability = Utils.getAvailablePeriod(from1, to1, availability);
				availability = Utils.getAvailablePeriod(from2, to2, availability);
			}
		}
		else
			availability.add("5non");
		
		//get availability for Saturday
		if(!"non".equals(saturdayNull))
		{			
			from1 = request.getParameter("satFrom1");
			from2 = request.getParameter("satFrom2");
			to1 = request.getParameter("satTo1");
			to2 = request.getParameter("satTo2");	
			
			if ((from1.equals(from2))&&(from1.equals("0")))
				availability.add("6non");	
			else
			{
				availability = Utils.getAvailablePeriod(from1, to1, availability);
				availability = Utils.getAvailablePeriod(from2, to2, availability);
			}
		}
		else
			availability.add("6non");
		
		//get availability for Sunday
		if(!"non".equals(sundayNull))
		{			
			from1 = request.getParameter("sunFrom1");
			from2 = request.getParameter("sunFrom2");
			to1 = request.getParameter("sunTo1");
			to2 = request.getParameter("sunTo2");	
			
			if ((from1.equals(from2))&&(from1.equals("0")))
				availability.add("7non");	
			else
			{
				availability = Utils.getAvailablePeriod(from1, to1, availability);
				availability = Utils.getAvailablePeriod(from2, to2, availability);
			}
		}
		else
			availability.add("7non");
	
		//convert arrayList to string for matching data type in DB
		if (availability != null)
			strAvailableTime= StringUtils.collectionToCommaDelimitedString(availability);	
		
		return strAvailableTime;
	}
	/**
	 * Pair two volunteers
	 * @param level1
	 * @param level2
	 * @return
	 */
	public static boolean isMatched(String level1, String level2){
		boolean matched = false;
		if (level1.equals("E") || level2.equals("E"))		
			matched = true;				
		else if (level1.equals("I") && level2.equals("I"))
			matched = true;
		
		return matched;
	}
	
	/**
	 * 
	 * @param list
	 * @param time
	 * @param day
	 * @return a list of Availability at time on the day
	 */
	public static List<Availability> getAllAvailablilities(List<Volunteer> list, String time, String day)
	{		
		Availability availability;
		List<Availability> aList = new ArrayList<Availability>();
		
		for (Volunteer v: list)
		{
			for (Volunteer p: list)
			{
				if( (!(v.getVolunteerId()==p.getVolunteerId())) && 
						(isMatchVolunteer(v, p)) && ((!isExist(aList,v, p))))
				{
					availability = new Availability();
					availability.setvDisplayName(v.getDisplayName());
					availability.setvPhone(v.getHomePhone());
					availability.setvEmail(v.getEmail());
					availability.setpDisplayName(p.getDisplayName());
					availability.setpPhone(p.getHomePhone());
					availability.setpEmail(p.getEmail());		
					availability.setMatchedTime(formatdateTime(time, day));
					availability.setvId(v.getVolunteerId());
					availability.setpId(p.getVolunteerId());
				      	
					aList.add(availability);	
				}
			}
		}
		return aList;
	}
	/**
	 * Get all volunteer who has availability on selected time
	 * @param list
	 * @param time -- time slot
	 * @param dTime -- date + time
	 * @return
	 */
	public static List<Volunteer> getAllMatchedVolunteers(List<Volunteer> list, String time, String dateTime, AppointmentManager aManager){		
		List<Volunteer> vList = new ArrayList<Volunteer>();		
		String availableTime;
		
		for (Volunteer v: list)
		{
			availableTime = v.getAvailability();		
			if ((availableTime.contains(time))&&(!aManager.hasAppointmentByVolunteer(v.getVolunteerId(), dateTime)))
				vList.add(v);
		}
		return vList;
	}

	/**
	 * 
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static List<Availability> getAllMatchedPairs(List<Volunteer> list1, List<Volunteer> list2){
		String availability1, availability2;
		String[] aSet1, aSet2;
		List<Availability> aList = new ArrayList<Availability>();
		Availability availability;
		
		for (Volunteer v1: list1)
		{
			availability1 = v1.getAvailability();				
			aSet1 = availability1.split(",");	
			
			for (Volunteer v2: list2)
			{
				if (v1.getVolunteerId()!= v2.getVolunteerId())
				{		
					availability2 = v2.getAvailability();		
					
					if (isMatchVolunteer(v1, v2))						
					{					
						aSet2  = availability2.split(",");		
						
						//find match availability					
						for(int i = 0; i < aSet1.length; i++)
						{								
							for(int j = 0; j <aSet2.length; j++)//
							{  	//same time, no duplicated
								if ((!aSet1[i].toString().contains("non")) 
										&&  (aSet1[i].toString().equals(aSet2[j].toString()))
										&&	(!isExist(aList, aSet1[i].toString(), v1, v2)))								
								{	
									availability = new Availability();
									availability.setvDisplayName(v1.getDisplayName());
									availability.setvPhone(v1.getHomePhone());
									availability.setvEmail(v1.getEmail());
									availability.setpDisplayName(v2.getDisplayName());
									availability.setpPhone(v2.getHomePhone());
									availability.setpEmail(v2.getEmail());								  
									availability.setMatchedTime(formatMatchTime(aSet1[i].toString()));
									availability.setvId(v1.getVolunteerId());
									availability.setpId(v2.getVolunteerId());
								      	
									aList.add(availability);	
								}
							}
						}
					}
				}		
			}			
		}	
		
		return aList;
	}
	
	/**
	 * avoid duplicated element
	 * @param list
	 * @param time
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static boolean isExist(List<Availability> list, String time, Volunteer v1, Volunteer v2){
		Availability a = new Availability();
		boolean exist = false;
		time = formatMatchTime(time);		
		String v1Name = v1.getDisplayName();
		String v2Name = v2.getDisplayName();
		
		for (int i =0; i<list.size(); i++){
			a = list.get(i);
			
			if ((time.equals(a.getMatchedTime())) && ((v1Name.equals(a.getpDisplayName())) && (v2Name.equals(a.getvDisplayName())) ))
				return true;
		}
		return exist;
	}
	/**
	 * avoid duplicate element
	 * @param list
	 * @param v1
	 * @param v2
	 * @return
	 */	
	public static boolean isExist(List<Availability> list, Volunteer v1, Volunteer v2){
		Availability a = new Availability();
		boolean exist = false;
		
		String v1Name = v1.getDisplayName();
		String v2Name = v2.getDisplayName();
		
		for (int i =0; i<list.size(); i++){
			a = list.get(i);
			
			if ((v1Name.equals(a.getpDisplayName())) && (v2Name.equals(a.getvDisplayName())))			
				return true;
		}
		return exist;
	}
	/**
	 * 
	 * @param time
	 * @return
	 */
	public static String formatMatchTime(String time){
		StringBuffer sb = new StringBuffer();
		
		Map<String, String> dayMap = new HashMap<String, String>();
		dayMap.put("1", "Monday");
		dayMap.put("2", "Tuesday");
		dayMap.put("3", "Wednesday");
		dayMap.put("4", "Thursday");
		dayMap.put("5", "Friday");
		
		Map<String, String> timePeriodMap = getAvailabilityMap();
		
		sb.append(dayMap.get(time.substring(0,1)));
		sb.append("--");
		sb.append(timePeriodMap.get(time.substring(1)));
		
		return sb.toString();
	}
	/**
	 * 
	 * @param time
	 * @param day
	 * @return
	 */
	public static String formatdateTime(String time, String day){			
		Map<String, String> timePeriodMap = getAvailabilityMap();
		
		StringBuffer sb = new StringBuffer();
		sb.append(day);
		sb.append(" ");
		sb.append(timePeriodMap.get(time.substring(1)));
		
		return sb.toString();
	}
	/**
	 * 
	 * @param appointmentTime
	 * @param volunteerAvailability
	 * @return
	 */
	public static boolean isAvailableForVolunteer(String appointmentTime, String volunteerAvailability){
		boolean available = false;
		String[] availabilityArray = volunteerAvailability.split(",");
		
		for(String s: availabilityArray){
			if (appointmentTime.equals(s))
				return true;
		}
		return available;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Map<String, String> getAvailabilityMap(){
		Map<String, String> map = new HashMap<String, String>();
		
		String[] displayTime = {"08:00:00 AM", "08:30:00 AM", "09:00:00 AM","09:30:00 AM", "10:00:00 AM", "10:30:00 AM",
				"11:00:00 AM","11:30:00 AM", "12:00:00 PM", "12:30:00 PM",  "13:00:00 PM", "13:30:00 PM", "14:00:00 PM", 
				"14:30:00 PM", "15:00:00 PM", "15:30:00 PM", "16:00:00 PM", "16:30:00 PM", "17:00:00 PM", "17:30:00 PM", "18:00:00 PM"};
		
		for (int i= 1; i <= displayTime.length; i++){
			map.put(String.valueOf(i), displayTime[i-1]);
		}
		
		return map;
	}
	
	public static void saveAvailability(String availability, ModelMap model){
		List<String> aList = new ArrayList<String>();		
		List<String> lMonday = new ArrayList<String>();
		List<String> lTuesday = new ArrayList<String>();
		List<String> lWednesday = new ArrayList<String>();
		List<String> lThursday = new ArrayList<String>();
		List<String> lFriday = new ArrayList<String>();	
		List<String> lSaturday = new ArrayList<String>();
		List<String> lSunday = new ArrayList<String>();
			
		Map<String, String> showAvailableTime = getAvailabilityMap();		
		aList = Arrays.asList(availability.split(","));		
	
		for (String l : aList){			
			if (l.startsWith("1"))
				lMonday = getFormatedTimeList(l, showAvailableTime, lMonday);									
			
			if (l.startsWith("2"))
				lTuesday = getFormatedTimeList(l, showAvailableTime, lTuesday);									

			if (l.startsWith("3"))
				lWednesday = getFormatedTimeList(l, showAvailableTime, lWednesday);					
			
			if (l.startsWith("4"))
				lThursday = getFormatedTimeList(l, showAvailableTime, lThursday);	
						
			if (l.startsWith("5"))
				lFriday = getFormatedTimeList(l, showAvailableTime, lFriday);		
			
			if (l.startsWith("6"))
				lSaturday = getFormatedTimeList(l, showAvailableTime, lSaturday);	
						
			if (l.startsWith("7"))
				lSunday = getFormatedTimeList(l, showAvailableTime, lSunday);		
		}	
		
		if ((lMonday == null)||(lMonday.size() == 0))
			lMonday.add("1non");	
		if ((lTuesday == null)||(lTuesday.size() == 0))
			lTuesday.add("2non");
		if ((lWednesday == null)||(lWednesday.size() == 0))
			lWednesday.add("3non");
		if ((lThursday == null)||(lThursday.size() == 0))
			lThursday.add("4non");
		if ((lFriday == null)||(lFriday.size() == 0))
			lFriday.add("5non");
		if ((lSaturday == null)||(lSaturday.size() == 0))
			lSaturday.add("6non");
		if ((lSunday == null)||(lSunday.size() == 0))
			lSunday.add("7non");
		
		model.addAttribute("monAvailability", lMonday);		
		model.addAttribute("tueAvailability", lTuesday);
		model.addAttribute("wedAvailability", lWednesday);
		model.addAttribute("thuAvailability", lThursday);
		model.addAttribute("friAvailability", lFriday);		
		model.addAttribute("satAvailability", lSaturday);
		model.addAttribute("sunAvailability", lSunday);	
	}
	
	public static List<String> getFormatedTimeList(String str, Map<String, String> map, List<String> list){
		String key;
		key = str.substring(1);
		
		if (!key.equals("non"))
		{
			list.add(map.get(key));
			Utils.sortList(list);
		}	
		
		return list;
	}
	
	public static boolean isMatchVolunteer(Volunteer v1, Volunteer v2){
		String v1Type = v1.getExperienceLevel();
		String v2Type = v2.getExperienceLevel();	
		
		boolean matched = false;		
		
		if ("Experienced".equals(v1Type) || "Experienced".equals(v2Type) || "E".equals(v1Type) || "E".equals(v2Type))
			matched = true;
		else if (( "Intermediate".equals(v1Type) && "Intermediate".equals(v2Type)) ||("I".equals(v1Type) && "I".equals(v2Type)))
			matched = true;
		
		return matched;
	}
	
	public static void showVolunteerAvailability(Volunteer volunteer, SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		String strAvailibilities = volunteer.getAvailability();				
		boolean mondayNull = false;
		boolean tuesdayNull = false;
		boolean wednesdayNull = false;
		boolean thursdayNull = false;
		boolean fridayNull = false;
		boolean saturdayNull = false;
		boolean sundayNull = false;
		
		if (strAvailibilities.contains("1non"))
			mondayNull = true;
		if (strAvailibilities.contains("2non"))
			tuesdayNull = true;
		if (strAvailibilities.contains("3non"))
			wednesdayNull = true;
		if (strAvailibilities.contains("4non"))
			thursdayNull = true;
		if (strAvailibilities.contains("5non"))
			fridayNull = true;
		if (strAvailibilities.contains("6non"))
			saturdayNull = true;
		if (strAvailibilities.contains("7non"))
			sundayNull = true;
		
		String[] arrayAvailibilities = strAvailibilities.split(",");
		
		Utils.getPosition("1","monDropPosition",arrayAvailibilities,mondayNull, model);
		Utils.getPosition("2","tueDropPosition",arrayAvailibilities,tuesdayNull, model);
		Utils.getPosition("3","wedDropPosition",arrayAvailibilities,wednesdayNull, model);
		Utils.getPosition("4","thuDropPosition",arrayAvailibilities,thursdayNull, model);
		Utils.getPosition("5","friDropPosition",arrayAvailibilities,fridayNull, model);
		Utils.getPosition("6","satDropPosition",arrayAvailibilities,saturdayNull, model);
		Utils.getPosition("7","sunDropPosition",arrayAvailibilities,sundayNull, model);
		
		model.addAttribute("volunteer", volunteer);
		model.addAttribute("mondayNull", mondayNull);
		model.addAttribute("tuesdayNull", tuesdayNull);
		model.addAttribute("wednesdayNull", wednesdayNull);
		model.addAttribute("thursdayNull", thursdayNull);
		model.addAttribute("fridayNull", fridayNull);	
		model.addAttribute("satursdayNull", saturdayNull);
		model.addAttribute("sundayNull", sundayNull);
		
		if (request.getSession().getAttribute("unread_messages") != null)
			model.addAttribute("unread", request.getSession().getAttribute("unread_messages"));				
	}
	
	// ==================== Survey =================================//
	/**
	 * 
	 * @param request
	 * @param surveyManager
	 * @return a list of survey template
	 */
	public static List<SurveyTemplate> getSurveyTemplatesWithCanDelete(HttpServletRequest request, SurveyManager surveyManager){
		HttpSession session = request.getSession();		
		List<SurveyTemplate> surveyTemplateList;
		if (session.getAttribute("survey_template_withCanDelete_list") == null)
		{			
			if (request.isUserInRole("ROLE_ADMIN"))//central admin 
				surveyTemplateList = surveyManager.getSurveyTemplatesWithCanDelete(0);
			else //local admin/site admin
				surveyTemplateList = surveyManager.getSurveyTemplatesWithCanDelete(getLoggedInUser(request).getSite());
			//save in the session
			if (surveyTemplateList != null && surveyTemplateList.size()>0)
				session.setAttribute("survey_template_withCanDelete_list", surveyTemplateList);
		}
		else
			surveyTemplateList = (List<SurveyTemplate>)session.getAttribute("survey_template_withCanDelete_list");
		
		return surveyTemplateList;
	}
	
	/**
	 * 
	 * @param request
	 * @param surveyManager
	 * @return a list of survey template
	 */
	public static List<SurveyTemplate> getSurveyTemplates(HttpServletRequest request, SurveyManager surveyManager){
		HttpSession session = request.getSession();		
		List<SurveyTemplate> surveyTemplateList;
		if (session.getAttribute("survey_template_list") == null)
			surveyTemplateList = updateSurveyTemplates(request, surveyManager);
		else
			surveyTemplateList = (List<SurveyTemplate>)session.getAttribute("survey_template_list");
		
		return surveyTemplateList;
	}
	
	public static List<SurveyTemplate> getDefaultSurveyTemplates(HttpServletRequest request, SurveyManager surveyManager){
		List<SurveyTemplate> surveyTemplateList;
		if (request.isUserInRole("ROLE_ADMIN"))//central admin 
			surveyTemplateList = surveyManager.getDefaultSurveyTemplates();
		else //local admin/site admin
			surveyTemplateList = surveyManager.getDefaultSurveyTemplatesBySite(getLoggedInUser(request).getSite());
		
		return surveyTemplateList;
	}
	
	public static List<SurveyTemplate> updateSurveyTemplates(HttpServletRequest request, SurveyManager surveyManager)
	{
		List<SurveyTemplate> surveyTemplateList;
		HttpSession session = request.getSession();
		if (request.isUserInRole("ROLE_ADMIN"))//central admin 
			surveyTemplateList = surveyManager.getAllSurveyTemplates();
		else //local admin/site admin
			surveyTemplateList = surveyManager.getSurveyTemplatesBySite(getLoggedInUser(request).getSite());
		//save in the session
		if (surveyTemplateList != null && surveyTemplateList.size()>0)
			session.setAttribute("survey_template_list", surveyTemplateList);		
		return surveyTemplateList;
	}	
	
   	public static boolean isExistInSurveyResultList(List<SurveyResult> surveyResults, int surveyTemplateId, int patientId){
   		boolean exist = false;
   		int sId = 0;
   		int pId = 0;
   		for (SurveyResult sr : surveyResults){
   			sId = sr.getSurveyID();
   			pId = sr.getPatientID();
   			
   			if (surveyTemplateId == sId && patientId == pId)
   				exist = true;
   		}
   		return exist;
   	}
   	
   	public static void assignSurveysToClient(List<SurveyTemplate> surveyTemplates, int[] patientIds, 
   			SecurityContextHolderAwareRequestWrapper request, SurveyManager surveyManager) 
   					throws JAXBException, DatatypeConfigurationException, Exception{
  
   		SurveyResult sr;
   		String lastAssignSurveyTo = "";
   		
   		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
   		String startDate = sdf.format(new Date());   
   		HttpSession session = request.getSession();
   		
   		if (session.getAttribute("assignSurveyTo") != null)
   			lastAssignSurveyTo = session.getAttribute("assignSurveyTo").toString();
   		for(SurveyTemplate st: surveyTemplates) 
   		{
			SurveyFactory surveyFactory = new SurveyFactory();
			TapestryPHRSurvey template = surveyFactory.getSurveyTemplate(st);
			
			//
		
			if ("V".equalsIgnoreCase(lastAssignSurveyTo))
			{
				surveyFactory.reloadSurveyTemplate(st);
				template = surveyFactory.getSurveyTemplate(st);
			}			
			sr = new SurveyResult();
				
			for (int i = 0; i < patientIds.length; i++)
			{
				sr.setPatientID(patientIds[i]);
				sr.setSurveyID(st.getSurveyID());
	            	
				//set today as startDate
				sr.setStartDate(startDate);	            	
					    	
				TapestryPHRSurvey blankSurvey = template;
				blankSurvey.setQuestions(new ArrayList<SurveyQuestion>());// make blank survey
				sr.setResults(SurveyAction.updateSurveyResult(blankSurvey));
				surveyManager.assignSurvey(sr);				
			}   			
		}   		
   		session.setAttribute("assignSurveyTo", "C");
   	}   	
   
  	public static void addSurveyTemplate(String[] surveyId,List<SurveyTemplate> allSurveyTemplates, List<SurveyTemplate> selectedSurveyTemplates){
   		int surveyTemplateId;
   		for (int i = 0; i < surveyId.length; i ++)
   		{  						
   			surveyTemplateId = Integer.parseInt(surveyId[i]);
  				
  			for (SurveyTemplate st: allSurveyTemplates){
  				if (surveyTemplateId == st.getSurveyID())
  					selectedSurveyTemplates.add(st);
  	   		}
   		}
   	}
   
   	public static void assignSurveysToClient(List<SurveyTemplate> selectSurveyTemplats, int[] patientIds,
   			SecurityContextHolderAwareRequestWrapper request,ModelMap model, SurveyManager surveyManager) 
   					throws JAXBException, DatatypeConfigurationException, Exception{
   		try
   		{   			
   			assignSurveysToClient(selectSurveyTemplats, patientIds, request, surveyManager);			
   			model.addAttribute("successful", true);
  		}catch (Exception e){
  			System.out.println("something wrong with assingn survey to client === " + e.getMessage());
  		} 
   	}
   	
   	public static void assignSurveysToVolunteer(List<SurveyTemplate> surveyTemplates, int[] volunteerIds, 
   			SecurityContextHolderAwareRequestWrapper request, SurveyManager surveyManager) 
   					throws JAXBException, DatatypeConfigurationException, Exception{   		
   		SurveyResult sr;   		
   		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
   		String startDate = sdf.format(new Date());  
   		HttpSession session = request.getSession();
   		String lastAssignSurveyTo="";
   		if (session.getAttribute("assignSurveyTo") != null)
   			lastAssignSurveyTo = session.getAttribute("assignSurveyTo").toString();
   		
   		for(SurveyTemplate st: surveyTemplates) 
   		{			
			SurveyFactory surveyFactory = new SurveyFactory();
			TapestryPHRSurvey template = surveyFactory.getSurveyTemplate(st);
			
			if ("C".equalsIgnoreCase(lastAssignSurveyTo))
			{
				surveyFactory.reloadSurveyTemplate(st);
				template = surveyFactory.getSurveyTemplate(st);
			}			
			sr = new SurveyResult();			
			for (int i = 0; i < volunteerIds.length; i++)
			{
				sr.setVolunteerID(volunteerIds[i]);
				sr.setSurveyID(st.getSurveyID());
	            	
				//set today as startDate
				sr.setStartDate(startDate);	            	
			    	
				TapestryPHRSurvey blankSurvey = template;
				blankSurvey.setQuestions(new ArrayList<SurveyQuestion>());// make blank survey
				sr.setResults(SurveyAction.updateSurveyResult(blankSurvey));
				surveyManager.assignVolunteerSurvey(sr);
			}   			
		}   		
   		session.setAttribute("assignSurveyTo", "V");   		
   	}
   
   	public static void assignSurveysToVolunteer(List<SurveyTemplate> selectSurveyTemplats, int[] volunteerIds,
   			SecurityContextHolderAwareRequestWrapper request,ModelMap model, SurveyManager surveyManager) 
   					throws JAXBException, DatatypeConfigurationException, Exception{
   		try
   		{    		
   			assignSurveysToVolunteer(selectSurveyTemplats, volunteerIds, request, surveyManager);
   			model.addAttribute("successful", true);
  		}catch (Exception e){
  			System.out.println("something wrong with assingn survey to volunteer === " + e.getMessage());
  		} 
   	}
   
   	public static boolean completedAllSurveys(int patientId, SurveyManager surveyManager){
   		boolean completed = false;
   		
   		int count = surveyManager.countSurveyTemplate();
   		List<SurveyResult> completedSurveys = surveyManager.getCompletedSurveysByPatientID(patientId);
   		
   		if (count == completedSurveys.size())
   			completed = true;
   		
   		return completed;
   	}
  
   	public static String removeObserverNotes(String questionText)
	{		
		//remove /observernotes/ from question text
		int index = questionText.indexOf("/observernote/");
	    	
	    if (index > 0)
	    	questionText = questionText.substring(0, index);
	    
	    return questionText;
	}
   	
   	public static TapestrySurveyMap getSurveyMap(HttpServletRequest request)
	{
		TapestrySurveyMap userSurveys = (TapestrySurveyMap) request.getSession().getAttribute("session_survey_list");
		return(userSurveys);
	}
   	
   	public static TapestrySurveyMap getVolunteerSurveyMap(HttpServletRequest request)
	{
		TapestrySurveyMap userSurveys = (TapestrySurveyMap) request.getSession().getAttribute("session_volunteer_survey_list");
		return(userSurveys);
	}
   	
   	public static TapestrySurveyMap storeSurveyMapInSession(HttpServletRequest request, List<SurveyResult> surveyResults, List<SurveyTemplate> surveyTemplates)
	{	
 //  		request.getSession().removeAttribute("session_survey_list");
		TapestrySurveyMap userSurveys = new TapestrySurveyMap(getSurveyResultsList(surveyResults, surveyTemplates));
		request.getSession().setAttribute("session_survey_list", userSurveys);		
		return userSurveys;
	}
   	
   	public static TapestrySurveyMap getUserSurveys(List<SurveyResult> surveyResults, List<SurveyTemplate> surveyTemplates)
   	{
   		return new TapestrySurveyMap(getSurveyResultsList(surveyResults, surveyTemplates));
   	}
   	
   	public static TapestrySurveyMap storeVolunteerSurveyMapInSession(HttpServletRequest request, List<SurveyResult> surveyResults, List<SurveyTemplate> surveyTemplates)
	{
		TapestrySurveyMap userSurveys = new TapestrySurveyMap(getSurveyResultsList(surveyResults, surveyTemplates));
		request.getSession().setAttribute("session_volunteer_survey_list", userSurveys);
		
		return userSurveys;
	}
   	
	public static List<TapestryPHRSurvey> getSurveyResultsList(List<SurveyResult> surveyResults, List<SurveyTemplate> surveyTemplates)
	{
		List<TapestryPHRSurvey> results = new ArrayList<TapestryPHRSurvey>();

		for (SurveyResult tempResult : surveyResults)
		{
			try
			{
				tempResult.processMumpsResults(tempResult);
				TapestryPHRSurvey temp = SurveyActionMumps.toPhrSurvey(surveyTemplates, tempResult);
				results.add(temp);
			}
			catch (Exception e)
			{
				System.out.println("Error" + e);
			}
		}
		return(results);
	}	
	// ===================== Mis =================================//
   	/**
   	 * Add patients and volunteer info in the ModelMap
   	 * @param model
   	 * @param volunteerManager
   	 * @param patientManager
   	 */
	public static void loadPatientsAndVolunteers(ModelMap model, VolunteerManager volunteerManager,
			PatientManager patientManager, OrganizationManager organizationManager, 
			SecurityContextHolderAwareRequestWrapper request ){
		User user = getLoggedInUser(request);
		List<Volunteer> volunteers;
		List<Patient> patientList;
		int organizationId = user.getOrganization();
		
		if (request.isUserInRole("ROLE_ADMIN"))
		{//For central Admin	
			volunteers = volunteerManager.getAllVolunteers();	
			patientList = patientManager.getAllPatients();
		}				
		else	
		{// For local Admin/VC
			volunteers = volunteerManager.getAllVolunteersByOrganization(organizationId);			
			patientList = patientManager.getPatientsBySite(user.getSite());			
		}		
		//clinic
		List<Clinic> clinics;
		if (request.isUserInRole("ROLE_ADMIN"))//central admin
			clinics = organizationManager.getAllClinics();
		else
			clinics = organizationManager.getClinicsBySite(user.getSite());
		
		model.addAttribute("clinics", clinics);
	
		model.addAttribute("volunteers", volunteers);	  
        model.addAttribute("patients", patientList);
	}
	
	// =========================== report generator =========================//
	/**
	 * Generate PDF format Report in Tapestry
	 * @param report
	 * @param response
	 */
	
	public static void buildPDF(Report report, HttpServletResponse response){			
		String patientName = report.getPatient().getFirstName() + " " + report.getPatient().getLastName();
		String orignalFileName= patientName +"_report.pdf";
		try {
			Document document = new Document();
			document.setPageSize(PageSize.A4);
			document.setMargins(36, 36, 60, 36);
			document.setMarginMirroring(true);
			response.setHeader("Content-Disposition", "outline;filename=\"" +orignalFileName+ "\"");
			PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
			//Font setup
			//white font			
			Font wbLargeFont = new Font(Font.FontFamily.HELVETICA  , 20, Font.BOLD);
			wbLargeFont.setColor(BaseColor.WHITE);
			Font wMediumFont = new Font(Font.FontFamily.HELVETICA , 16, Font.BOLD);
			wMediumFont.setColor(BaseColor.WHITE);
			//red font
			Font rbFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
			rbFont.setColor(BaseColor.RED);			
			Font rmFont = new Font(Font.FontFamily.HELVETICA, 16);
			rmFont.setColor(BaseColor.RED);			
			Font rFont = new Font(Font.FontFamily.HELVETICA, 20);
			rFont.setColor(BaseColor.RED);		        
			Font rMediumFont = new Font(Font.FontFamily.HELVETICA, 12);
			rMediumFont.setColor(BaseColor.RED);		        
			Font rSmallFont = new Font(Font.FontFamily.HELVETICA, 8);
			rSmallFont.setColor(BaseColor.RED);
			//blue font
			Font gbMediumFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
			gbMediumFont.setColor(BaseColor.BLUE);
			Font gbSmallFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
			gbSmallFont.setColor(BaseColor.BLUE);

			//black font
			Font sFont = new Font(Font.FontFamily.HELVETICA, 9);	
			Font sbFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);	
			Font iSmallFont = new Font(Font.FontFamily.HELVETICA , 9, Font.ITALIC );
			Font mFont = new Font(Font.FontFamily.HELVETICA, 12);		
			Font bmFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
			Font imFont = new Font(Font.FontFamily.HELVETICA , 12, Font.ITALIC );
			Font ibMediumFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLDITALIC);
			Font bMediumFont = new Font(Font.FontFamily.HELVETICA , 16, Font.BOLD);	
			Font blFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);	
			//set multiple images as header
			List<Image> imageHeader = new ArrayList<Image>();      	
	            
			Image imageLogo = Image.getInstance("webapps/tapestry/resources/images/logo.png"); 
			imageLogo.scalePercent(25f);
			imageHeader.add(imageLogo);			
				            
			Image imageDegroote = Image.getInstance("webapps/tapestry/resources/images/degroote.png");
			imageDegroote.scalePercent(25f);
			imageHeader.add(imageDegroote);	
			
			Image imageFhs = Image.getInstance("webapps/tapestry/resources/images/fhs.png");
			imageFhs.scalePercent(25f);	
			imageHeader.add(imageFhs);
						
			ReportHeader event = new ReportHeader();
			event.setHeader(imageHeader);
			writer.setPageEvent(event);			
			
			document.open(); 
			//Patient info
			PdfPTable table = new PdfPTable(2);
			table.setWidthPercentage(100);
			table.setWidths(new float[]{1f, 2f});
			
			PdfPCell cell = new PdfPCell(new Phrase("Patient: " + patientName, sbFont));
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthLeft(1f);
			cell.setBorderWidthBottom(0);
			cell.setBorderWidthRight(0);		
			cell.setPadding(5);
			table.addCell(cell);
	            
			String address = report.getPatient().getAddress();
			if (address == null)
				address = "";
			cell = new PdfPCell(new Phrase("Address: " + address, sbFont));
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthRight(1f);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(0);	 
			cell.setPadding(5);
			table.addCell(cell);
		     
			String mrpName = report.getPatient().getMrpLastName();
			cell = new PdfPCell(new Phrase("MRP: Dr." + mrpName, sbFont));
			cell.setBorderWidthLeft(1f);		        
			cell.setBorderWidthTop(0);	          
			cell.setBorderWidthBottom(0);
			cell.setBorderWidthRight(0);
			cell.setPadding(5);
			table.addCell(cell);
		        
			cell = new PdfPCell( new Phrase("Date of visit: " + report.getAppointment().getDate(), sbFont));
			cell.setBorderWidthRight(1f);		        
			cell.setBorderWidthTop(0);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(0);
			cell.setPadding(5);
			table.addCell(cell);
		        
			cell = new PdfPCell(new Phrase("Time: " + report.getAppointment().getTime(), sbFont));
			cell.setBorderWidthLeft(1f);
			cell.setBorderWidthBottom(1f);
			cell.setBorderWidthTop(0);
			cell.setBorderWidthRight(0);	
			cell.setPadding(5);
			table.addCell(cell);
		        
			cell = new PdfPCell(new Phrase("", sbFont));
			cell.setBorderWidthRight(1f);
			cell.setBorderWidthBottom(1f);
			cell.setBorderWidthTop(0);
			cell.setBorderWidthLeft(0);	  
			cell.setPadding(5);
			table.addCell(cell);
	        
			document.add(table);		   	        
			//Patient Info	
			table = new PdfPTable(1);
			table.setWidthPercentage(100);
			String birthDate = report.getPatient().getBod();
			if (birthDate == null)
				birthDate = "";
			cell = new PdfPCell(new Phrase("TAPESTRY REPORT: " + patientName + " " + birthDate, blFont));
			cell.setBorder(0);
			table.addCell(cell);
			document.add(table);
			
			//message
			table = new PdfPTable(1);
			table.setWidthPercentage(100);
			
			Phrase pp = new Phrase();	
			String msg0 = "READ THIS MESSAGE FIRST";			
			pp.add(new Chunk(msg0, blFont));
			
			String msg1 = "\n\nDear MRP or Resident: After review, please comment with relevant background information or a "
					+ "suggested course of action. \n \nThe allied health team will review and discuss this report and will make contact with your for next steps.\n \n";
			pp.add(new Chunk(msg1, mFont));
			
			String msg2 = "Please do not schedule a patient visit without consulting with the allied health team first. Thank you.\n";			
			pp.add(new Chunk(msg2, bmFont));
		
			cell = new PdfPCell(pp);
			table.addCell(cell);			
			document.add(table);
		
			table = new PdfPTable(1);
			table.setWidthPercentage(100);
			cell = new PdfPCell(new Phrase("PATIENT GOAL(S)", wbLargeFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			
			List<String> patientGoals = report.getPatientGoals();
					
			pp = new Phrase();
			Chunk c = new Chunk("What Matters Most To Me: ", bmFont);			 
			pp.add(c);
			pp.add(new Chunk(patientGoals.get(0) + "\n", mFont));
			
			c = new Chunk("GOAL 1  ", bmFont);			 
			pp.add(c);
			pp.add(new Chunk(patientGoals.get(1)+ "\n", mFont));
			
			c = new Chunk("GOAL 2  ", bmFont);			 
			pp.add(c);
			pp.add(new Chunk(patientGoals.get(2)+ "\n", mFont));

			c = new Chunk("GOAL 3  ", bmFont);			 
			pp.add(c);
			pp.add(new Chunk(patientGoals.get(3)+ "\n", mFont));

			cell = new PdfPCell(pp);			
			table.addCell(cell);		
			document.add(table);	
			
			//alerts
			table = new PdfPTable(1);
			table.setWidthPercentage(100);
			float[] cWidths = {1f, 18f};
		            
			cell = new PdfPCell(new Phrase("Key Information", wbLargeFont));
			cell.setBackgroundColor(BaseColor.BLACK);	           
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			List<String> alerts = report.getAlerts(); 
			
			StringBuffer sb = new StringBuffer();
			for (int i =0; i<alerts.size(); i++)
			{
				sb.append(".");
				sb.append(alerts.get(i));
				sb.append("\n");
			}
			
			cell = new PdfPCell(new Phrase(sb.toString(), rmFont));		
			cell.setPadding(5);
			table.addCell(cell);			
			document.add(table);
			document.add(new Phrase("    "));   
			//Key observation
			table = new PdfPTable(1);
			table.setWidthPercentage(100);

			cell = new PdfPCell(new Phrase("Social Context", wbLargeFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	 
	            
			table.addCell(cell);
			
			String keyObservation = report.getAppointment().getKeyObservation();
			if (keyObservation == null || keyObservation.equals(""))
				cell = new PdfPCell(new Phrase(" "));
			else
				cell = new PdfPCell(new Phrase(keyObservation));
			table.addCell(cell);
			document.add(table);
			
			//Memory Screen
			table = new PdfPTable(2);
			table.setWidthPercentage(100);
			cell = new PdfPCell(new Phrase("Memory Screen", wbLargeFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setColspan(2);
			table.addCell(cell);
			
			for (Map.Entry<String, String> entry : report.getMemory().entrySet()) {
				if ("YES".equalsIgnoreCase(entry.getValue())){	            		
					cell = new PdfPCell(new Phrase(entry.getKey(), rMediumFont));		            	
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
					cell.setPaddingBottom(5);
					table.addCell(cell);	            	
		            	
					cell = new PdfPCell(new Phrase(entry.getValue(), rMediumFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPaddingBottom(5);
					table.addCell(cell); 
				}
				else{
					cell = new PdfPCell(new Phrase(entry.getKey(), mFont));		            	
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPaddingBottom(5);
					table.addCell(cell);	            	
		            	
					cell = new PdfPCell(new Phrase(entry.getValue(), mFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPaddingBottom(5);
					table.addCell(cell); 
					}           		            	
			}
			float[] aWidths = {24f, 3f};
			table.setWidths(aWidths);
			document.add(table);		
		
			//Advance Directives/Care plan
			table = new PdfPTable(2);
			table.setWidthPercentage(100);
			cell = new PdfPCell(new Phrase("Advance Directives", wbLargeFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setColspan(2);
			table.addCell(cell);
			
			String cQuestionText = "Are you interested in";
			for (Map.Entry<String, String> entry : report.getCaringPlan().entrySet()) {
				if (entry.getKey().contains(cQuestionText)){
					if ("YES".equalsIgnoreCase(entry.getValue())){
						cell = new PdfPCell(new Phrase(entry.getKey(), rMediumFont));		            	
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setPaddingBottom(5);
						table.addCell(cell);	            	
			            	
						cell = new PdfPCell(new Phrase(entry.getValue(), rMediumFont));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setPaddingBottom(5);
						table.addCell(cell); 
					}
					else
					{
						cell = new PdfPCell(new Phrase(entry.getKey(), mFont));		            	
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
						cell.setPaddingBottom(5);
						table.addCell(cell);	            	
			            	
						cell = new PdfPCell(new Phrase(entry.getValue(), mFont));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setPaddingBottom(5);
						table.addCell(cell); 
					}
				}
				else
				{
					if ("YES".equalsIgnoreCase(entry.getValue())){	            		
						cell = new PdfPCell(new Phrase(entry.getKey(), mFont));		            	
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
						cell.setPaddingBottom(5);
						table.addCell(cell);	            	
			            	
						cell = new PdfPCell(new Phrase(entry.getValue(), mFont));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setPaddingBottom(5);
						table.addCell(cell); 
					}
					else 
					{
						cell = new PdfPCell(new Phrase(entry.getKey(), rMediumFont));		            	
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setPaddingBottom(5);
						table.addCell(cell);	            	
			            	
						cell = new PdfPCell(new Phrase(entry.getValue(), rMediumFont));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setPaddingBottom(5);
						table.addCell(cell); 
					}
				}				
			}
			float[] aaWidths = {24f, 3f};
			table.setWidths(aaWidths);
			document.add(table);
			document.newPage();	
			
			//Summary of Tapestry tools
			table = new PdfPTable(3);
			table.setWidthPercentage(110);
			table.setWidths(new float[]{1.2f, 2f, 2f});
			cell = new PdfPCell(new Phrase("Summary of TAPESTRY Tools", wbLargeFont));
			cell.setBackgroundColor(BaseColor.GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			cell.setColspan(3);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("DOMAIN", wMediumFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("SCORE", wMediumFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("DESCRIPTION", wMediumFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Functional Status", mFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	           
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setMinimumHeight(45f);
			table.addCell(cell);	            
	           
			Phrase p = new Phrase();
			sb = new StringBuffer();
			sb.append("Timed up-and-go test score = ");
			sb.append(report.getScores().getTimeUpGoTest());
			sb.append("\n");
			sb.append("Edmonton Frail Scale score = ");
			sb.append(report.getScores().getEdmontonFrailScale());	
			sb.append("\n");
			p.add(new Chunk(sb.toString(), imFont));
			p.add(new Chunk("(Add 1 to this score if there are minor spacing errors in the clock and add 2 if there are other errors in the clock.)", iSmallFont));

			cell = new PdfPCell(p);            
//			cell = new PdfPCell(new Phrase(sb.toString(), imFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	    
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			p = new Phrase();
			Chunk underline = new Chunk("Edmonton Frail Scale (Score Key):", mFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	 
			p.add(underline);
	            
			sb = new StringBuffer();	           
			sb.append(" ");
			sb.append("\n");
			sb.append("Robust: 0-4");
			sb.append("\n");
			sb.append("Apparently Vulnerable: 5-6");
			sb.append("\n");
			sb.append("Frail: 7-17");
			sb.append("\n");

			p.add(new Chunk(sb.toString(), sFont));

			cell = new PdfPCell(p);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	    
			cell.setNoWrap(false);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase("Nutritional Status", mFont));	            	           
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setMinimumHeight(35f);
			table.addCell(cell);            
	           
			sb = new StringBuffer();
			sb.append("Screen II score = ");
			sb.append(report.getScores().getNutritionScreen());
			sb.append("\n");	           
			sb.append("\n");	            
			sb.append("\n");
	            
			cell = new PdfPCell(new Phrase(sb.toString(), imFont));
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			p = new Phrase();
			underline = new Chunk("Screen II Nutrition Screening Tool:", mFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	  
			p.add(underline);
	           
			sb = new StringBuffer();
			sb.append(" ");
			sb.append("\n");
			sb.append("Max Score = 64");
			sb.append("\n");
			sb.append("High Risk < 50");
			sb.append("\n");
			p.add(new Chunk(sb.toString(), sFont));
	            
			cell = new PdfPCell(p);
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Social Support", mFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	      
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setMinimumHeight(55f);
			table.addCell(cell);            
	           
			sb = new StringBuffer();
			sb.append("Satisfaction score =  ");
			sb.append(report.getScores().getSocialSatisfication());
			sb.append("\n");	
			sb.append("Network score = ");
			sb.append(report.getScores().getSocialNetwork());
			sb.append("\n");	            
			sb.append("\n");
	            
			cell = new PdfPCell(new Phrase(sb.toString(), imFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			p = new Phrase();	   
			underline = new Chunk("Duke Social Support Index", mFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	  
			p.add(underline); 	     
			p.add(new Chunk("\n"));
			p.add(new Chunk("(Score < 10 risk cut off), ranges from 6-18", sFont));
	            
			sb = new StringBuffer();
			sb.append(" ");	            	            
			sb.append("\n");
			sb.append("Perceived satisfaction with behavioural or");
			sb.append("\n");
			sb.append("emotional support obtained from this network");
			sb.append("\n");
			p.add(new Chunk(sb.toString(), sFont));
	            
			underline = new Chunk("Network score range : 4-12", sFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location
			p.add(underline);
	            	            
			sb = new StringBuffer();
			sb.append("\n");
			sb.append("Size and structure of social network");
			sb.append("\n");	
			p.add(new Chunk(sb.toString(), sFont));
	            
			cell = new PdfPCell(p);
			cell.setNoWrap(false);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
			table.addCell(cell);
	            
			//Mobility
			PdfPTable nest_table1 = new PdfPTable(1);			
			cell = new PdfPCell(new Phrase("Mobility ", mFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);		         
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(1f);
			cell.setBorderWidthTop(0);
			cell.setBorderWidthRight(0);	
			nest_table1.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Walking 2.0 km ", mFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);	   
			cell.setBorder(0);	            
			nest_table1.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Walking 0.5 km ", mFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(0);	           
			cell.setBorderWidthRight(0);	
			nest_table1.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Climbing Stairs ", mFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);	 
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(0);	            
			cell.setBorderWidthRight(0);	
			nest_table1.addCell(cell);
	            
			PdfPTable nest_table2 = new PdfPTable(1);	            
			cell = new PdfPCell(new Phrase(" ", mFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(1f);
			cell.setBorderWidthTop(0);
			cell.setBorderWidthRight(0);	
			nest_table2.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getScores().getMobilityWalking2(), iSmallFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);	
			cell.setBorder(0);	            	
			nest_table2.addCell(cell);
			
			cell = new PdfPCell(new Phrase(report.getScores().getMobilityWalkingHalf(), iSmallFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);	
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(0);	           
			cell.setBorderWidthRight(0);
			nest_table2.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getScores().getMobilityClimbing(), iSmallFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);	   
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(0);	            
			cell.setBorderWidthRight(0);	
			nest_table2.addCell(cell);
	            
			table.addCell(nest_table1);
			table.addCell(nest_table2);
	            	            	     
			p = new Phrase();
			underline = new Chunk("Manty et al Mobility Measure-Categories:", mFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	 
			p.add(underline);
			
			sb = new StringBuffer();			
			sb.append("\n");
			sb.append("No Limitation");
			sb.append("\n");
			sb.append("Preclinical Limitation");
			sb.append("\n");
			sb.append("Minor Manifest Limitation");
			sb.append("\n");
			sb.append("Major Manifest Limitation");
			sb.append("\n");	          
			sb.append("\n");	            
	            
			p.add(new Chunk(sb.toString(), sFont));
			cell = new PdfPCell(p);
			cell.setNoWrap(false);	         
			table.addCell(cell);   
	            
			//RAPA	            
			cell = new PdfPCell(new Phrase("Physical Activity", mFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	      
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setMinimumHeight(45f);
			table.addCell(cell);            
	           
			sb = new StringBuffer();
			sb.append("Aerobic Score =  ");
			sb.append(report.getScores().getAerobicMessage());
			sb.append("\n");
			sb.append("Strength & Flexibility Score = ");
			sb.append(report.getScores().getpAStrengthAndFlexibility());        
			sb.append("\n");
	            
			cell = new PdfPCell(new Phrase(sb.toString(), imFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			p = new Phrase();
			underline = new Chunk("Rapid Assessment of Physical Activity(RAPA)", mFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	 
			p.add(underline);
			
			sb = new StringBuffer();			
			sb.append("\n");
			sb.append("Aerobic:ranges from 1-7(< 6 Suboptimal Activity)");
			sb.append("\n");	
			sb.append("Strength & Flexibility: ranges from 0-3");
			sb.append("\n");	            
	            
			p.add(new Chunk(sb.toString(), sFont));		
			cell = new PdfPCell(p);
			cell.setNoWrap(false);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
			table.addCell(cell);
	            
			document.add(table);
			document.add(new Phrase("    "));	
			document.newPage();
			//Goals	
			table = new PdfPTable(1);
			table.setWidthPercentage(100);
			cell = new PdfPCell(new Phrase("WHAT MATTERS TO ME", bMediumFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);		
			table.addCell(cell);
			//life goals
			cell = new PdfPCell(new Phrase("LIFE GOALS:", bmFont));
			cell.setBorderWidthBottom(0);
			table.addCell(cell);
			List<String> lifeGoals = report.getLifeGoals();
			sb = new StringBuffer();
			for (int i = 0; i < lifeGoals.size(); i++)
			{				
				sb.append(lifeGoals.get(i));
				sb.append("\n");
			}		
			sb.append("\n");
			cell = new PdfPCell(new Phrase(sb.toString(), mFont));
			cell.setBorderWidthTop(0);
			table.addCell(cell);
			//health goals
			cell = new PdfPCell(new Phrase("HEALTH GOALS:", bmFont));
			cell.setBorderWidthBottom(0);
			table.addCell(cell);
			List<String> healthGoals = report.getHealthGoals();
			sb = new StringBuffer();
			for (int i = 0; i < healthGoals.size(); i++)
			{
				sb.append(healthGoals.get(i));
				sb.append("\n");
			}	
			sb.append("\n");
			cell = new PdfPCell(new Phrase(sb.toString(), mFont));
			cell.setBorderWidthTop(0);
			table.addCell(cell);	
			document.add(table);			
		
			//Tapestry Questions
			table = new PdfPTable(2);
			table.setWidthPercentage(100);
			cell = new PdfPCell(new Phrase("TAPESTRY QUESTIONS", bMediumFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
			cell.setColspan(2);
			table.addCell(cell);
	            
			String tQuestionText, tQuestionAnswer,value;
			int index;
			Phrase comb;
			
			for (Map.Entry<String, String> entry : report.getDailyActivities().entrySet()) {
				cell = new PdfPCell(new Phrase(entry.getKey(), sFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);	            	
	            
				value = entry.getValue().toString();						
				index = value.indexOf("|");				
				tQuestionText = value.substring(0, index);				
				tQuestionAnswer = value.substring(index+1);
				
				comb = new Phrase(); 
				comb.add(new Phrase(tQuestionText, bmFont));
				comb.add(new Phrase(tQuestionAnswer, mFont));	    			
				cell.addElement(comb);	
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				
				table.addCell(cell); 
			}	           
			table.setWidths(cWidths);
			document.add(table);
			//Volunteer Information
			table = new PdfPTable(2);
			table.setWidthPercentage(100);
			
			cell = new PdfPCell(new Phrase("VOLUNTEER INFORMATION & NOTES", bMediumFont)); 
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
			cell.setColspan(2);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Volunteer 1", bmFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getAppointment().getVolunteer(), ibMediumFont));
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Volunteer 2", bmFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getAppointment().getPartner(), ibMediumFont));
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Volunteer Notes", gbMediumFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getAppointment().getComments(), gbMediumFont));
			cell.setPaddingBottom(10);
			table.addCell(cell);
	            
			float[] dWidths = {6f, 27f};
			table.setWidths(dWidths);
			document.add(table);
	            
			document.close();
		} catch (Exception e) {
			e.printStackTrace();			
		}			
	}
	
	public static void buildMcGillPDFReport(Report report, Map<String, String> tMap, HttpServletResponse response)
	{	
		String patientName = report.getPatient().getFirstName() + " " + report.getPatient().getLastName();
		String orignalFileName= patientName +"_report.pdf";
		try {
			Document document = new Document();
			document.setPageSize(PageSize.A4);
			document.setMargins(36, 36, 80, 36);
			document.setMarginMirroring(true);
			response.setHeader("Content-Disposition", "outline;filename=\"" +orignalFileName+ "\"");
			PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
			//Font setup
			//white font			
			Font wbLargeFont = new Font(Font.FontFamily.HELVETICA  , 20, Font.BOLD);
			wbLargeFont.setColor(BaseColor.WHITE);
			Font wMediumFont = new Font(Font.FontFamily.HELVETICA , 16, Font.BOLD);
			wMediumFont.setColor(BaseColor.WHITE);
			//red font
			Font rbFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
			rbFont.setColor(BaseColor.RED);			
			Font rmFont = new Font(Font.FontFamily.HELVETICA, 16);
			rmFont.setColor(BaseColor.RED);			
			Font rFont = new Font(Font.FontFamily.HELVETICA, 20);
			rFont.setColor(BaseColor.RED);		        
			Font rMediumFont = new Font(Font.FontFamily.HELVETICA, 12);
			rMediumFont.setColor(BaseColor.RED);		        
			Font rSmallFont = new Font(Font.FontFamily.HELVETICA, 8);
			rSmallFont.setColor(BaseColor.RED);
			//blue font
			Font gbMediumFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
			gbMediumFont.setColor(BaseColor.BLUE);
			Font gbSmallFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
			gbSmallFont.setColor(BaseColor.BLUE);

			//black font
			Font sFont = new Font(Font.FontFamily.HELVETICA, 9);	
			Font sbFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);	
			Font iSmallFont = new Font(Font.FontFamily.HELVETICA , 9, Font.ITALIC );
			Font mFont = new Font(Font.FontFamily.HELVETICA, 12);		
			Font bmFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
			Font imFont = new Font(Font.FontFamily.HELVETICA , 12, Font.ITALIC );
			Font ibMediumFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLDITALIC);
			Font bMediumFont = new Font(Font.FontFamily.HELVETICA , 16, Font.BOLD);	
			Font blFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);	
			//set multiple images as header
			List<Image> imageHeader = new ArrayList<Image>();  		
	            
			Image imageLogo = Image.getInstance("webapps/tapestry/resources/images/logo.png");	
	//		imageLogo.setAlignment(Element.ALIGN_TOP);  				
			imageHeader.add(imageLogo);			
			
			Image imageBlank = Image.getInstance("webapps/tapestry/resources/images/blank_spacer.png");
			imageHeader.add(imageBlank);
			
			Image mgGillLogo = Image.getInstance("webapps/tapestry/resources/images/mcGill_logo.png"); 
//			mgGillLogo.scalePercent(30f);
			imageHeader.add(mgGillLogo);			
						
			ReportHeader event = new ReportHeader();
			event.setHeader(imageHeader);
			writer.setPageEvent(event);				
			document.open(); 
	//		document.add(new Phrase("    ")); 
			//Patient info
			PdfPTable table = new PdfPTable(2);
			table.setWidthPercentage(100);
			table.setWidths(new float[]{1f, 2f});
			
			PdfPCell cell = new PdfPCell(new Phrase("Patient: " + patientName, sbFont));
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthLeft(1f);
			cell.setBorderWidthBottom(0);
			cell.setBorderWidthRight(0);		
			cell.setPadding(5);
			table.addCell(cell);
	            
			String address = report.getPatient().getAddress();
			if (address == null)
				address = "";
			cell = new PdfPCell(new Phrase("Address: " + address, sbFont));
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthRight(1f);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(0);	 
			cell.setPadding(5);
			table.addCell(cell);
		     
			String mrpName = report.getPatient().getMrpLastName();
			cell = new PdfPCell(new Phrase("MRP: Dr." + mrpName, sbFont));
			cell.setBorderWidthLeft(1f);		        
			cell.setBorderWidthTop(0);	
			cell.setBorderWidthBottom(1f);			
			cell.setBorderWidthRight(0);
			cell.setPadding(5);
			table.addCell(cell);
		        
			String dateTime = report.getAppointment().getDate() + " " + report.getAppointment().getTime();
			cell = new PdfPCell( new Phrase("Date/Time of visit: " + dateTime, sbFont));
			cell.setBorderWidthRight(1f);		        
			cell.setBorderWidthTop(0);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(1f);
			cell.setPadding(5);
			table.addCell(cell);	        
			document.add(table);
			document.add(new Phrase("    ")); 
			//Patient Info	
			table = new PdfPTable(1);
			table.setWidthPercentage(100);
			String birthDate = report.getPatient().getBod();
			if (birthDate == null)
				birthDate = "";
			cell = new PdfPCell(new Phrase("TAPESTRY REPORT: " + patientName, blFont));
			cell.setBorder(0);
			table.addCell(cell);
			document.add(table);
		//	document.add(new Phrase("    ")); 
			//message
			table = new PdfPTable(1);
			table.setWidthPercentage(100);
			
			Phrase pp = new Phrase();	
			String msg0 = "READ THIS MESSAGE FIRST";			
			pp.add(new Chunk(msg0, blFont));
			
			String msg1 = "\n\nAfter review, please provide relevant background information or a suggested course of action by attaching a comments.\n \n";
			pp.add(new Chunk(msg1, mFont));
								
			cell = new PdfPCell(pp);
			table.addCell(cell);			
			document.add(table);
		
			table = new PdfPTable(1);
			table.setWidthPercentage(100);
			cell = new PdfPCell(new Phrase("PATIENT GOAL(S)", wbLargeFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			
			List<String> patientGoals = report.getPatientGoals();
					
			pp = new Phrase();
			Chunk c = new Chunk("What Matters Most To Me: ", bmFont);			 
			pp.add(c);
			pp.add(new Chunk(patientGoals.get(0) + "\n", mFont));
			
			c = new Chunk("GOAL 1  ", bmFont);			 
			pp.add(c);
			pp.add(new Chunk(patientGoals.get(1)+ "\n", mFont));
			
			c = new Chunk("GOAL 2  ", bmFont);			 
			pp.add(c);
			pp.add(new Chunk(patientGoals.get(2)+ "\n", mFont));

			c = new Chunk("GOAL 3  ", bmFont);			 
			pp.add(c);
			pp.add(new Chunk(patientGoals.get(3)+ "\n", mFont));

			cell = new PdfPCell(pp);			
			table.addCell(cell);		
			document.add(table);	
			
			//alerts
			table = new PdfPTable(1);
			table.setWidthPercentage(100);
			float[] cWidths = {1f, 18f};
		            
			cell = new PdfPCell(new Phrase("Key Information", wbLargeFont));
			cell.setBackgroundColor(BaseColor.BLACK);	           
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			List<String> alerts = report.getAlerts(); 
			
			StringBuffer sb = new StringBuffer(); 
			if (alerts != null)
			{
				for (int i =0; i<alerts.size(); i++)
				{
					sb.append(".");
					sb.append(alerts.get(i));
					sb.append("\n");
				}
				
				cell = new PdfPCell(new Phrase(sb.toString(), rmFont));		
				cell.setPadding(5);
				table.addCell(cell);	
			}
					
			document.add(table);
			//Key observation
			table = new PdfPTable(1);
			table.setWidthPercentage(100);

			cell = new PdfPCell(new Phrase("Social Context", wbLargeFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	 
	            
			table.addCell(cell);
			
			String keyObservation = report.getAppointment().getKeyObservation();
			if (keyObservation == null || keyObservation.equals(""))
				cell = new PdfPCell(new Phrase(" "));
			else
				cell = new PdfPCell(new Phrase(keyObservation));
			table.addCell(cell);
			document.add(table);
			document.newPage();	
			
			//Summary of Tapestry tools
			table = new PdfPTable(3);
			table.setWidthPercentage(110);
			table.setWidths(new float[]{1.2f, 2f, 2f});
			cell = new PdfPCell(new Phrase("Summary of TAPESTRY Tools", wbLargeFont));
			cell.setBackgroundColor(BaseColor.GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			cell.setColspan(3);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("DOMAIN", wMediumFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("SCORE", wMediumFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("DESCRIPTION", wMediumFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase("Nutritional Status", mFont));	            	           
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setMinimumHeight(35f);
			table.addCell(cell);            
	           
			sb = new StringBuffer();
			sb.append("Screen II score = ");
			sb.append(report.getScores().getNutritionScreen());
			sb.append("\n");	           
			sb.append("\n");	            
			sb.append("\n");
	            
			cell = new PdfPCell(new Phrase(sb.toString(), imFont));
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			Phrase p = new Phrase();
			Chunk underline = new Chunk("Screen II Nutrition Screening Tool:", mFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	  
			p.add(underline);
	           
			sb = new StringBuffer();
			sb.append(" ");
			sb.append("\n");
			sb.append("Max Score = 64");
			sb.append("\n");
			sb.append("High Risk < 50");
			sb.append("\n");
			p.add(new Chunk(sb.toString(), sFont));
	            
			cell = new PdfPCell(p);
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Social Support", mFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	      
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setMinimumHeight(55f);
			table.addCell(cell);            
	           
			sb = new StringBuffer();
			sb.append("Satisfaction score =  ");
			sb.append(report.getScores().getSocialSatisfication());
			sb.append("\n");	
			sb.append("Network score = ");
			sb.append(report.getScores().getSocialNetwork());
			sb.append("\n");	            
			sb.append("\n");
	            
			cell = new PdfPCell(new Phrase(sb.toString(), imFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			p = new Phrase();	   
			underline = new Chunk("Duke Social Support Index", mFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	  
			p.add(underline); 	     
			p.add(new Chunk("\n"));
			p.add(new Chunk("(Score < 10 risk cut off), ranges from 6-18", sFont));
	            
			sb = new StringBuffer();
			sb.append(" ");	            	            
			sb.append("\n");
			sb.append("Perceived satisfaction with behavioural or");
			sb.append("\n");
			sb.append("emotional support obtained from this network");
			sb.append("\n");
			p.add(new Chunk(sb.toString(), sFont));
	            
			underline = new Chunk("Network score range : 4-12", sFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location
			p.add(underline);
	            	            
			sb = new StringBuffer();
			sb.append("\n");
			sb.append("Size and structure of social network");
			sb.append("\n");	
			p.add(new Chunk(sb.toString(), sFont));
	            
			cell = new PdfPCell(p);
			cell.setNoWrap(false);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
			table.addCell(cell);
	            
			//RAPA	            
			cell = new PdfPCell(new Phrase("Physical Activity", mFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	      
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setMinimumHeight(45f);
			table.addCell(cell);            
	           
			sb = new StringBuffer();
			sb.append("Aerobic Score =  ");
			sb.append(report.getScores().getAerobicMessage());
			sb.append("\n");
			sb.append("Strength & Flexibility Score = ");
			sb.append(report.getScores().getpAStrengthAndFlexibility());        
			sb.append("\n");
	            
			cell = new PdfPCell(new Phrase(sb.toString(), imFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			p = new Phrase();
			underline = new Chunk("Rapid Assessment of Physical Activity(RAPA)", mFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	 
			p.add(underline);
			
			sb = new StringBuffer();			
			sb.append("\n");
			sb.append("Aerobic:ranges from 1-7(< 6 Suboptimal Activity)");
			sb.append("\n");	
			sb.append("Strength & Flexibility: ranges from 0-3");
			sb.append("\n");	            
	            
			p.add(new Chunk(sb.toString(), sFont));		
			cell = new PdfPCell(p);
			cell.setNoWrap(false);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
			table.addCell(cell);
	            
			document.add(table);
			document.add(new Phrase("    "));	
			document.newPage();
			//Goals	
			table = new PdfPTable(1);
			table.setWidthPercentage(100);
			cell = new PdfPCell(new Phrase("WHAT MATTERS TO ME", bMediumFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);		
			table.addCell(cell);
			//life goals
			cell = new PdfPCell(new Phrase("LIFE GOALS:", bmFont));
			cell.setBorderWidthBottom(0);
			table.addCell(cell);
			List<String> lifeGoals = report.getLifeGoals();
			sb = new StringBuffer();
			for (int i = 0; i < lifeGoals.size(); i++)
			{				
				sb.append(lifeGoals.get(i));
				sb.append("\n");
			}		
			sb.append("\n");
			cell = new PdfPCell(new Phrase(sb.toString(), mFont));
			cell.setBorderWidthTop(0);
			table.addCell(cell);
			//health goals
			cell = new PdfPCell(new Phrase("HEALTH GOALS:", bmFont));
			cell.setBorderWidthBottom(0);
			table.addCell(cell);
			List<String> healthGoals = report.getHealthGoals();
			sb = new StringBuffer();
			for (int i = 0; i < healthGoals.size(); i++)
			{
				sb.append(healthGoals.get(i));
				sb.append("\n");
			}	
			sb.append("\n");
			cell = new PdfPCell(new Phrase(sb.toString(), mFont));
			cell.setBorderWidthTop(0);
			table.addCell(cell);	
			document.add(table);			
	
			Iterator iterator = tMap.entrySet().iterator();
			String key, value;
	   		while (iterator.hasNext()) {
	   			Map.Entry mapEntry = (Map.Entry) iterator.next();
	   			
	   			key = mapEntry.getKey().toString();
	   			value = mapEntry.getValue().toString();
	   			
	   			table = new PdfPTable(2);
	   			table.setWidthPercentage(100);
	   			if (key.startsWith("SurveyTitle "))
	   			{
	   				cell = new PdfPCell(new Phrase(value, wbLargeFont));
	   				cell.setBackgroundColor(BaseColor.BLACK);	   
	   				cell.setColspan(2);
	   				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	   				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
	   				cell.setPaddingBottom(5);
		   			table.addCell(cell);
	   			}
	   			else
	   			{
	   				cell = new PdfPCell(new Phrase(key, mFont));		            	
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPaddingBottom(5);
					table.addCell(cell);	            	
		            	
					cell = new PdfPCell(new Phrase(value, mFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPaddingBottom(5);
					table.addCell(cell); 
	   			}		 
	   			document.add(table);
	   		}
			
			//Volunteer Information
			table = new PdfPTable(2);
			table.setWidthPercentage(100);
			
			cell = new PdfPCell(new Phrase("VOLUNTEER INFORMATION & NOTES", bMediumFont)); 
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
			cell.setColspan(2);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Volunteer 1", bmFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getAppointment().getVolunteer(), ibMediumFont));
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Volunteer 2", bmFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getAppointment().getPartner(), ibMediumFont));
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Volunteer Notes", gbMediumFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getAppointment().getComments(), gbMediumFont));
			cell.setPaddingBottom(10);
			table.addCell(cell);
	            
			float[] dWidths = {6f, 27f};
			table.setWidths(dWidths);
			document.add(table);
	            
			document.close();
		} catch (Exception e) {
			e.printStackTrace();			
		}			
	}
	public static void buildMcMasterPDFReport(Report report, HttpServletResponse response)
	{
		String patientName = report.getPatient().getFirstName() + " " + report.getPatient().getLastName();
		String orignalFileName= patientName +"_report.pdf";
		try {
			Document document = new Document();
			document.setPageSize(PageSize.A4);
			document.setMargins(36, 36, 60, 36);
			document.setMarginMirroring(true);
			response.setHeader("Content-Disposition", "outline;filename=\"" +orignalFileName+ "\"");
			PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());		
			
			//////////////////////////////////
			//set multiple images as header
			List<Image> imageHeader = new ArrayList<Image>();      	
	            
			Image imageLogo = Image.getInstance("webapps/tapestry/resources/images/logo.png"); 
			imageLogo.scalePercent(25f);
			imageHeader.add(imageLogo);			
				            
			Image imageDegroote = Image.getInstance("webapps/tapestry/resources/images/degroote.png");
			imageDegroote.scalePercent(25f);
			imageHeader.add(imageDegroote);	
			
			Image imageFhs = Image.getInstance("webapps/tapestry/resources/images/fhs.png");
			imageFhs.scalePercent(25f);	
			imageHeader.add(imageFhs);
						
			ReportHeader event = new ReportHeader();
			event.setHeader(imageHeader);
			writer.setPageEvent(event);			
			
			document.open(); 
			//set main part of report	
			buildMainPartReportPDF(report, document);
			//set volunteer information
			buildVolunteerPartReportPDF(report, document);
			
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		
	}
	
	private static void buildMainPartReportPDF (Report report, Document document)
	{
		String patientName = report.getPatient().getFirstName() + " " + report.getPatient().getLastName();
		try{
		//Font setup
		//white font			
		Font wbLargeFont = new Font(Font.FontFamily.HELVETICA  , 20, Font.BOLD);
		wbLargeFont.setColor(BaseColor.WHITE);
		Font wMediumFont = new Font(Font.FontFamily.HELVETICA , 16, Font.BOLD);
		wMediumFont.setColor(BaseColor.WHITE);
		//red font
		Font rbFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
		rbFont.setColor(BaseColor.RED);			
		Font rmFont = new Font(Font.FontFamily.HELVETICA, 16);
		rmFont.setColor(BaseColor.RED);			
		Font rFont = new Font(Font.FontFamily.HELVETICA, 20);
		rFont.setColor(BaseColor.RED);		        
		Font rMediumFont = new Font(Font.FontFamily.HELVETICA, 12);
		rMediumFont.setColor(BaseColor.RED);		        
		Font rSmallFont = new Font(Font.FontFamily.HELVETICA, 8);
		rSmallFont.setColor(BaseColor.RED);
		//blue font
		Font gbMediumFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
		gbMediumFont.setColor(BaseColor.BLUE);
		Font gbSmallFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
		gbSmallFont.setColor(BaseColor.BLUE);

		//black font
		Font sFont = new Font(Font.FontFamily.HELVETICA, 9);	
		Font sbFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);	
		Font iSmallFont = new Font(Font.FontFamily.HELVETICA , 9, Font.ITALIC );
		Font mFont = new Font(Font.FontFamily.HELVETICA, 12);		
		Font bmFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
		Font imFont = new Font(Font.FontFamily.HELVETICA , 12, Font.ITALIC );
		Font ibMediumFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLDITALIC);
		Font bMediumFont = new Font(Font.FontFamily.HELVETICA , 16, Font.BOLD);	
		Font blFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);	
		
//		document.open(); 
		//Patient info
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		table.setWidths(new float[]{1f, 2f});
		
		PdfPCell cell = new PdfPCell(new Phrase("Patient: " + patientName, sbFont));
		cell.setBorderWidthTop(1f);
		cell.setBorderWidthLeft(1f);
		cell.setBorderWidthBottom(0);
		cell.setBorderWidthRight(0);		
		cell.setPadding(5);
		table.addCell(cell);
            
		String address = report.getPatient().getAddress();
		if (address == null)
			address = "";
		cell = new PdfPCell(new Phrase("Address: " + address, sbFont));
		cell.setBorderWidthTop(1f);
		cell.setBorderWidthRight(1f);
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthBottom(0);	 
		cell.setPadding(5);
		table.addCell(cell);
	     
		String mrpName = report.getPatient().getMrpLastName();
		cell = new PdfPCell(new Phrase("MRP: Dr." + mrpName, sbFont));
		cell.setBorderWidthLeft(1f);		        
		cell.setBorderWidthTop(0);	          
		cell.setBorderWidthBottom(0);
		cell.setBorderWidthRight(0);
		cell.setPadding(5);
		table.addCell(cell);
	        
		cell = new PdfPCell( new Phrase("Date of visit: " + report.getAppointment().getDate(), sbFont));
		cell.setBorderWidthRight(1f);		        
		cell.setBorderWidthTop(0);
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthBottom(0);
		cell.setPadding(5);
		table.addCell(cell);
	        
		cell = new PdfPCell(new Phrase("Time: " + report.getAppointment().getTime(), sbFont));
		cell.setBorderWidthLeft(1f);
		cell.setBorderWidthBottom(1f);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthRight(0);	
		cell.setPadding(5);
		table.addCell(cell);
	        
		cell = new PdfPCell(new Phrase("", sbFont));
		cell.setBorderWidthRight(1f);
		cell.setBorderWidthBottom(1f);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthLeft(0);	  
		cell.setPadding(5);
		table.addCell(cell);
        
		document.add(table);		   	        
		//Patient Info	
		table = new PdfPTable(1);
		table.setWidthPercentage(100);
		String birthDate = report.getPatient().getBod();
		if (birthDate == null)
			birthDate = "";
		cell = new PdfPCell(new Phrase("TAPESTRY REPORT: " + patientName + " " + birthDate, blFont));
		cell.setBorder(0);
		table.addCell(cell);
		document.add(table);
		
		//message
		table = new PdfPTable(1);
		table.setWidthPercentage(100);
		
		Phrase pp = new Phrase();	
		String msg0 = "READ THIS MESSAGE FIRST";			
		pp.add(new Chunk(msg0, blFont));
		
		String msg1 = "\n\nDear MRP or Resident: After review, please comment with relevant background information or a "
				+ "suggested course of action. \n \nThe allied health team will review and discuss this report and will make contact with your for next steps.\n \n";
		pp.add(new Chunk(msg1, mFont));
		
		String msg2 = "Please do not schedule a patient visit without consulting with the allied health team first. Thank you.\n";			
		pp.add(new Chunk(msg2, bmFont));
	
		cell = new PdfPCell(pp);
		table.addCell(cell);			
		document.add(table);
	
		table = new PdfPTable(1);
		table.setWidthPercentage(100);
		cell = new PdfPCell(new Phrase("PATIENT GOAL(S)", wbLargeFont));
		cell.setBackgroundColor(BaseColor.BLACK);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		List<String> patientGoals = report.getPatientGoals();
				
		pp = new Phrase();
		Chunk c = new Chunk("What Matters Most To Me: ", bmFont);			 
		pp.add(c);
		pp.add(new Chunk(patientGoals.get(0) + "\n", mFont));
		
		c = new Chunk("GOAL 1  ", bmFont);			 
		pp.add(c);
		pp.add(new Chunk(patientGoals.get(1)+ "\n", mFont));
		
		c = new Chunk("GOAL 2  ", bmFont);			 
		pp.add(c);
		pp.add(new Chunk(patientGoals.get(2)+ "\n", mFont));

		c = new Chunk("GOAL 3  ", bmFont);			 
		pp.add(c);
		pp.add(new Chunk(patientGoals.get(3)+ "\n", mFont));

		cell = new PdfPCell(pp);			
		table.addCell(cell);		
		document.add(table);	
		
		//alerts
		table = new PdfPTable(1);
		table.setWidthPercentage(100);
		float[] cWidths = {1f, 18f};
	            
		cell = new PdfPCell(new Phrase("Key Information", wbLargeFont));
		cell.setBackgroundColor(BaseColor.BLACK);	           
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		List<String> alerts = report.getAlerts(); 
		
		StringBuffer sb = new StringBuffer();
		for (int i =0; i<alerts.size(); i++)
		{
			sb.append(".");
			sb.append(alerts.get(i));
			sb.append("\n");
		}
		
		cell = new PdfPCell(new Phrase(sb.toString(), rmFont));		
		cell.setPadding(5);
		table.addCell(cell);			
		document.add(table);
		document.add(new Phrase("    "));   
		//Key observation
		table = new PdfPTable(1);
		table.setWidthPercentage(100);

		cell = new PdfPCell(new Phrase("Social Context", wbLargeFont));
		cell.setBackgroundColor(BaseColor.BLACK);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	 
            
		table.addCell(cell);
		
		String keyObservation = report.getAppointment().getKeyObservation();
		if (keyObservation == null || keyObservation.equals(""))
			cell = new PdfPCell(new Phrase(" "));
		else
			cell = new PdfPCell(new Phrase(keyObservation));
		table.addCell(cell);
		document.add(table);
		
		//Memory Screen
		table = new PdfPTable(2);
		table.setWidthPercentage(100);
		cell = new PdfPCell(new Phrase("Memory Screen", wbLargeFont));
		cell.setBackgroundColor(BaseColor.BLACK);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setColspan(2);
		table.addCell(cell);
		
		for (Map.Entry<String, String> entry : report.getMemory().entrySet()) {
			if ("YES".equalsIgnoreCase(entry.getValue())){	            		
				cell = new PdfPCell(new Phrase(entry.getKey(), rMediumFont));		            	
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
				cell.setPaddingBottom(5);
				table.addCell(cell);	            	
	            	
				cell = new PdfPCell(new Phrase(entry.getValue(), rMediumFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setPaddingBottom(5);
				table.addCell(cell); 
			}
			else{
				cell = new PdfPCell(new Phrase(entry.getKey(), mFont));		            	
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setPaddingBottom(5);
				table.addCell(cell);	            	
	            	
				cell = new PdfPCell(new Phrase(entry.getValue(), mFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setPaddingBottom(5);
				table.addCell(cell); 
				}           		            	
		}
		float[] aWidths = {24f, 3f};
		table.setWidths(aWidths);
		document.add(table);		
	
		//Advance Directives/Care plan
		table = new PdfPTable(2);
		table.setWidthPercentage(100);
		cell = new PdfPCell(new Phrase("Advance Directives", wbLargeFont));
		cell.setBackgroundColor(BaseColor.BLACK);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setColspan(2);
		table.addCell(cell);
		
		String cQuestionText = "Are you interested in";
		for (Map.Entry<String, String> entry : report.getCaringPlan().entrySet()) {
			if (entry.getKey().contains(cQuestionText)){
				if ("YES".equalsIgnoreCase(entry.getValue())){
					cell = new PdfPCell(new Phrase(entry.getKey(), rMediumFont));		            	
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPaddingBottom(5);
					table.addCell(cell);	            	
		            	
					cell = new PdfPCell(new Phrase(entry.getValue(), rMediumFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPaddingBottom(5);
					table.addCell(cell); 
				}
				else
				{
					cell = new PdfPCell(new Phrase(entry.getKey(), mFont));		            	
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
					cell.setPaddingBottom(5);
					table.addCell(cell);	            	
		            	
					cell = new PdfPCell(new Phrase(entry.getValue(), mFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPaddingBottom(5);
					table.addCell(cell); 
				}
			}
			else
			{
				if ("YES".equalsIgnoreCase(entry.getValue())){	            		
					cell = new PdfPCell(new Phrase(entry.getKey(), mFont));		            	
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
					cell.setPaddingBottom(5);
					table.addCell(cell);	            	
		            	
					cell = new PdfPCell(new Phrase(entry.getValue(), mFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPaddingBottom(5);
					table.addCell(cell); 
				}
				else 
				{
					cell = new PdfPCell(new Phrase(entry.getKey(), rMediumFont));		            	
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPaddingBottom(5);
					table.addCell(cell);	            	
		            	
					cell = new PdfPCell(new Phrase(entry.getValue(), rMediumFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPaddingBottom(5);
					table.addCell(cell); 
				}
			}				
		}
		float[] aaWidths = {24f, 3f};
		table.setWidths(aaWidths);
		document.add(table);
		document.newPage();	
		
		//Summary of Tapestry tools
		table = new PdfPTable(3);
		table.setWidthPercentage(110);
		table.setWidths(new float[]{1.2f, 2f, 2f});
		cell = new PdfPCell(new Phrase("Summary of TAPESTRY Tools", wbLargeFont));
		cell.setBackgroundColor(BaseColor.GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setFixedHeight(28f);
		cell.setColspan(3);
		table.addCell(cell);
            
		cell = new PdfPCell(new Phrase("DOMAIN", wMediumFont));
		cell.setBackgroundColor(BaseColor.BLACK);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setFixedHeight(28f);
		table.addCell(cell);
            
		cell = new PdfPCell(new Phrase("SCORE", wMediumFont));
		cell.setBackgroundColor(BaseColor.BLACK);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setFixedHeight(28f);
		table.addCell(cell);
            
		cell = new PdfPCell(new Phrase("DESCRIPTION", wMediumFont));
		cell.setBackgroundColor(BaseColor.BLACK);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setFixedHeight(28f);
		table.addCell(cell);
            
		cell = new PdfPCell(new Phrase("Functional Status", mFont));
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	           
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setMinimumHeight(45f);
		table.addCell(cell);	            
           
		Phrase p = new Phrase();
		sb = new StringBuffer();
		sb.append("Timed up-and-go test score = ");
		sb.append(report.getScores().getTimeUpGoTest());
		sb.append("\n");
		sb.append("Edmonton Frail Scale score = ");
		sb.append(report.getScores().getEdmontonFrailScale());	
		sb.append("\n");
		p.add(new Chunk(sb.toString(), imFont));
		p.add(new Chunk("(Add 1 to this score if there are minor spacing errors in the clock and add 2 if there are other errors in the clock.)", iSmallFont));

		cell = new PdfPCell(p);            
//		cell = new PdfPCell(new Phrase(sb.toString(), imFont));
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	    
		cell.setNoWrap(false);
		table.addCell(cell);
            
		p = new Phrase();
		Chunk underline = new Chunk("Edmonton Frail Scale (Score Key):", mFont);
		underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	 
		p.add(underline);
            
		sb = new StringBuffer();	           
		sb.append(" ");
		sb.append("\n");
		sb.append("Robust: 0-4");
		sb.append("\n");
		sb.append("Apparently Vulnerable: 5-6");
		sb.append("\n");
		sb.append("Frail: 7-17");
		sb.append("\n");

		p.add(new Chunk(sb.toString(), sFont));

		cell = new PdfPCell(p);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	    
		cell.setNoWrap(false);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Nutritional Status", mFont));	            	           
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setMinimumHeight(35f);
		table.addCell(cell);            
           
		sb = new StringBuffer();
		sb.append("Screen II score = ");
		sb.append(report.getScores().getNutritionScreen());
		sb.append("\n");	           
		sb.append("\n");	            
		sb.append("\n");
            
		cell = new PdfPCell(new Phrase(sb.toString(), imFont));
		cell.setNoWrap(false);
		table.addCell(cell);
            
		p = new Phrase();
		underline = new Chunk("Screen II Nutrition Screening Tool:", mFont);
		underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	  
		p.add(underline);
           
		sb = new StringBuffer();
		sb.append(" ");
		sb.append("\n");
		sb.append("Max Score = 64");
		sb.append("\n");
		sb.append("High Risk < 50");
		sb.append("\n");
		p.add(new Chunk(sb.toString(), sFont));
            
		cell = new PdfPCell(p);
		cell.setNoWrap(false);
		table.addCell(cell);
            
		cell = new PdfPCell(new Phrase("Social Support", mFont));
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	      
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setMinimumHeight(55f);
		table.addCell(cell);            
           
		sb = new StringBuffer();
		sb.append("Satisfaction score =  ");
		sb.append(report.getScores().getSocialSatisfication());
		sb.append("\n");	
		sb.append("Network score = ");
		sb.append(report.getScores().getSocialNetwork());
		sb.append("\n");	            
		sb.append("\n");
            
		cell = new PdfPCell(new Phrase(sb.toString(), imFont));
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
		cell.setNoWrap(false);
		table.addCell(cell);
            
		p = new Phrase();	   
		underline = new Chunk("Duke Social Support Index", mFont);
		underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	  
		p.add(underline); 	     
		p.add(new Chunk("\n"));
		p.add(new Chunk("(Score < 10 risk cut off), ranges from 6-18", sFont));
            
		sb = new StringBuffer();
		sb.append(" ");	            	            
		sb.append("\n");
		sb.append("Perceived satisfaction with behavioural or");
		sb.append("\n");
		sb.append("emotional support obtained from this network");
		sb.append("\n");
		p.add(new Chunk(sb.toString(), sFont));
            
		underline = new Chunk("Network score range : 4-12", sFont);
		underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location
		p.add(underline);
            	            
		sb = new StringBuffer();
		sb.append("\n");
		sb.append("Size and structure of social network");
		sb.append("\n");	
		p.add(new Chunk(sb.toString(), sFont));
            
		cell = new PdfPCell(p);
		cell.setNoWrap(false);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
		table.addCell(cell);
            
		//Mobility
		PdfPTable nest_table1 = new PdfPTable(1);			
		cell = new PdfPCell(new Phrase("Mobility ", mFont));	               
		cell.setVerticalAlignment(Element.ALIGN_TOP);		         
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthBottom(1f);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthRight(0);	
		nest_table1.addCell(cell);
            
		cell = new PdfPCell(new Phrase("Walking 2.0 km ", mFont));	               
		cell.setVerticalAlignment(Element.ALIGN_TOP);	   
		cell.setBorder(0);	            
		nest_table1.addCell(cell);
            
		cell = new PdfPCell(new Phrase("Walking 0.5 km ", mFont));	               
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setBorderWidthTop(1f);
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthBottom(0);	           
		cell.setBorderWidthRight(0);	
		nest_table1.addCell(cell);
            
		cell = new PdfPCell(new Phrase("Climbing Stairs ", mFont));	               
		cell.setVerticalAlignment(Element.ALIGN_TOP);	 
		cell.setBorderWidthTop(1f);
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthBottom(0);	            
		cell.setBorderWidthRight(0);	
		nest_table1.addCell(cell);
            
		PdfPTable nest_table2 = new PdfPTable(1);	            
		cell = new PdfPCell(new Phrase(" ", mFont));	               
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthBottom(1f);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthRight(0);	
		nest_table2.addCell(cell);
            
		cell = new PdfPCell(new Phrase(report.getScores().getMobilityWalking2(), iSmallFont));	               
		cell.setVerticalAlignment(Element.ALIGN_TOP);	
		cell.setBorder(0);	            	
		nest_table2.addCell(cell);
		
		cell = new PdfPCell(new Phrase(report.getScores().getMobilityWalkingHalf(), iSmallFont));	               
		cell.setVerticalAlignment(Element.ALIGN_TOP);	
		cell.setBorderWidthTop(1f);
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthBottom(0);	           
		cell.setBorderWidthRight(0);
		nest_table2.addCell(cell);
            
		cell = new PdfPCell(new Phrase(report.getScores().getMobilityClimbing(), iSmallFont));	               
		cell.setVerticalAlignment(Element.ALIGN_TOP);	   
		cell.setBorderWidthTop(1f);
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthBottom(0);	            
		cell.setBorderWidthRight(0);	
		nest_table2.addCell(cell);
            
		table.addCell(nest_table1);
		table.addCell(nest_table2);
            	            	     
		p = new Phrase();
		underline = new Chunk("Manty et al Mobility Measure-Categories:", mFont);
		underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	 
		p.add(underline);
		
		sb = new StringBuffer();			
		sb.append("\n");
		sb.append("No Limitation");
		sb.append("\n");
		sb.append("Preclinical Limitation");
		sb.append("\n");
		sb.append("Minor Manifest Limitation");
		sb.append("\n");
		sb.append("Major Manifest Limitation");
		sb.append("\n");	          
		sb.append("\n");	            
            
		p.add(new Chunk(sb.toString(), sFont));
		cell = new PdfPCell(p);
		cell.setNoWrap(false);	         
		table.addCell(cell);   
            
		//RAPA	            
		cell = new PdfPCell(new Phrase("Physical Activity", mFont));
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	      
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setMinimumHeight(45f);
		table.addCell(cell);            
           
		sb = new StringBuffer();
		sb.append("Aerobic Score =  ");
		sb.append(report.getScores().getAerobicMessage());
		sb.append("\n");
		sb.append("Strength & Flexibility Score = ");
		sb.append(report.getScores().getpAStrengthAndFlexibility());        
		sb.append("\n");
            
		cell = new PdfPCell(new Phrase(sb.toString(), imFont));
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
		cell.setNoWrap(false);
		table.addCell(cell);
            
		p = new Phrase();
		underline = new Chunk("Rapid Assessment of Physical Activity(RAPA)", mFont);
		underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	 
		p.add(underline);
		
		sb = new StringBuffer();			
		sb.append("\n");
		sb.append("Aerobic:ranges from 1-7(< 6 Suboptimal Activity)");
		sb.append("\n");	
		sb.append("Strength & Flexibility: ranges from 0-3");
		sb.append("\n");	            
            
		p.add(new Chunk(sb.toString(), sFont));		
		cell = new PdfPCell(p);
		cell.setNoWrap(false);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
		table.addCell(cell);
            
		document.add(table);
		document.add(new Phrase("    "));	
		document.newPage();
		//Goals	
		table = new PdfPTable(1);
		table.setWidthPercentage(100);
		cell = new PdfPCell(new Phrase("WHAT MATTERS TO ME", bMediumFont));
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);		
		table.addCell(cell);
		//life goals
		cell = new PdfPCell(new Phrase("LIFE GOALS:", bmFont));
		cell.setBorderWidthBottom(0);
		table.addCell(cell);
		List<String> lifeGoals = report.getLifeGoals();
		sb = new StringBuffer();
		for (int i = 0; i < lifeGoals.size(); i++)
		{				
			sb.append(lifeGoals.get(i));
			sb.append("\n");
		}		
		sb.append("\n");
		cell = new PdfPCell(new Phrase(sb.toString(), mFont));
		cell.setBorderWidthTop(0);
		table.addCell(cell);
		//health goals
		cell = new PdfPCell(new Phrase("HEALTH GOALS:", bmFont));
		cell.setBorderWidthBottom(0);
		table.addCell(cell);
		List<String> healthGoals = report.getHealthGoals();
		sb = new StringBuffer();
		for (int i = 0; i < healthGoals.size(); i++)
		{
			sb.append(healthGoals.get(i));
			sb.append("\n");
		}	
		sb.append("\n");
		cell = new PdfPCell(new Phrase(sb.toString(), mFont));
		cell.setBorderWidthTop(0);
		table.addCell(cell);	
		document.add(table);			
	
		//Tapestry Questions
		table = new PdfPTable(2);
		table.setWidthPercentage(100);
		cell = new PdfPCell(new Phrase("TAPESTRY QUESTIONS", bMediumFont));
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
		cell.setColspan(2);
		table.addCell(cell);
            
		String tQuestionText, tQuestionAnswer,value;
		int index;
		Phrase comb;
		
		for (Map.Entry<String, String> entry : report.getDailyActivities().entrySet()) {
			cell = new PdfPCell(new Phrase(entry.getKey(), sFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);	            	
            
			value = entry.getValue().toString();						
			index = value.indexOf("|");				
			tQuestionText = value.substring(0, index);				
			tQuestionAnswer = value.substring(index+1);
			
			comb = new Phrase(); 
			comb.add(new Phrase(tQuestionText, bmFont));
			comb.add(new Phrase(tQuestionAnswer, mFont));	    			
			cell.addElement(comb);	
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			
			table.addCell(cell); 
		}	           
		table.setWidths(cWidths);
		document.add(table);
		} catch (Exception e) {
			e.printStackTrace();			
		}
	}
	
	
	private static void buildVolunteerPartReportPDF(Report report, Document document)
	{
		Font gbMediumFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
		gbMediumFont.setColor(BaseColor.BLUE);
		Font bmFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
		Font ibMediumFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLDITALIC);
		Font bMediumFont = new Font(Font.FontFamily.HELVETICA , 16, Font.BOLD);	
		try{
		//Volunteer Information
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		
		PdfPCell cell = new PdfPCell(new Phrase("VOLUNTEER INFORMATION & NOTES", bMediumFont)); 
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
		cell.setColspan(2);
		table.addCell(cell);
            
		cell = new PdfPCell(new Phrase("Volunteer 1", bmFont));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
            
		cell = new PdfPCell(new Phrase(report.getAppointment().getVolunteer(), ibMediumFont));
		table.addCell(cell);
            
		cell = new PdfPCell(new Phrase("Volunteer 2", bmFont));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
            
		cell = new PdfPCell(new Phrase(report.getAppointment().getPartner(), ibMediumFont));
		table.addCell(cell);
            
		cell = new PdfPCell(new Phrase("Volunteer Notes", gbMediumFont));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
            
		cell = new PdfPCell(new Phrase(report.getAppointment().getComments(), gbMediumFont));
		cell.setPaddingBottom(10);
		table.addCell(cell);
            
		float[] dWidths = {6f, 27f};
		table.setWidths(dWidths);
		document.add(table);
		} catch (Exception e) {
			e.printStackTrace();			
		}
	}
	
	static class ReportHeader extends PdfPageEventHelper {
		List<Image> header;
		PdfTemplate total;
		
		public void setHeader(List<Image> header){
			this.header = header;
		}
		
		public void onOpenDocument(PdfWriter writer, Document document){
			total = writer.getDirectContent().createAppearance(10, 16);
		}
		
		public void onEndPage(PdfWriter writer, Document document){
			PdfPTable table = new PdfPTable(3);
            try
            { 
            	table.setTotalWidth(527);
                table.setLockedWidth(true);
                table.getDefaultCell().setFixedHeight(header.get(2).getScaledHeight());
                table.getDefaultCell().setBorder(0);
                
                table.addCell(header.get(0));
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);                
                table.addCell(header.get(1));
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(header.get(2));
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                //set page number
//                table.addCell(String.format("Page %d of", writer.getPageNumber()));
//                PdfPCell cell = new PdfPCell(Image.getInstance(total));
//                cell.setBorder(0);    
//                table.addCell(cell);
                table.writeSelectedRows(0, -1, 34, 823, writer.getDirectContent());  
            }
            catch (Exception e){
            	System.out.println(e.getStackTrace());
            }
		}
		
		public void onCloseDocument(PdfWriter writer, Document document){
			 ColumnText.showTextAligned(total, Element.ALIGN_LEFT,
	                    new Phrase(String.valueOf(writer.getPageNumber() - 1)),
	                    2, 2, 0);
		}
	}
	
	/**
	 * 
	 * @param questionTextList
	 * @param questionAnswerList
	 * @return Map survey question text and answer
	 */
	public static Map<String, String> getSurveyContentMapForDailyLife(List<String> questionTextList, List<String> questionAnswerList){		
		Map<String, String> content = new TreeMap<String, String>(); ;
		String text;

		StringBuffer sb = new StringBuffer();
		//remove the first element which is description about whole survey
		questionTextList.remove(0);
		List<String> questions = modifyDailyLifeActivityQuestions(questionTextList);
				
		if ((questions != null && questions.size() > 0)&&(questionAnswerList != null && questionAnswerList.size() > 0))
		{
			for (int i = 0; i < questionAnswerList.size(); i++)
			{
   	   			text = questions.get(i);	   	   			
	   	   		sb = new StringBuffer();	   	  
	   	   		sb.append(text);	 
	   	   		sb.append("|"); //add seperator for displaying in different font
	   	   		sb.append("\n");	
	   	   		sb.append(questionAnswerList.get(i));
	
	   	   		content.put(String.valueOf(i + 1), sb.toString());   	   			
			}
		}
		else
   			System.out.println("Bad thing happens, no question text/answer found for this Goal Setting survey!");
		
		return content;

	}
	
	/**
	 * need to combine Q2 with Q3
	 * @param questions
	 * @return
	 */
	public static List<String> modifyDailyLifeActivityQuestions(List<String> questions)
	{
		int index;
		String text;
	
		for (int i = 0; i < questions.size(); i++)
		{
			text = questions.get(i);
			//remove Question * of *
			if (text.startsWith("Question "))
				text = text.substring(16);
			//remove observer notes
	    	index = text.indexOf("/observernote/");	   		    	
	    	
	    	if (index > 0)
	    		text = text.substring(0, index);   
	    	//remove prompt... from question text
	    	index = text.indexOf("(Prompt:");
	    	
	    	if (index > 0)
	    		text = text.substring(0, index);
	    	else
	    	{//todo:should be removed after script is changed for consistency
	    		index = text.indexOf("Prompt:");
	    		if (index > 0)
	    			text = text.substring(0, index);
	    	}
	    	questions.set(i, text);
		}
			    	
	    //combine Q2 and Q3 question text
	    StringBuffer sb = new StringBuffer();
	    sb.append(questions.get(1));
	    sb.append("; ");
	    sb.append(questions.get(2));
	    questions.set(1, sb.toString());	    
	    questions.remove(2);	
	    	    	
		return questions;
	}
	
	/**
	 * 
	 * @param questionTextList
	 * @param questionAnswerList
	 * @return Map survey question text and answer for Memory Survey
	 */
	public static Map<String, String> getSurveyContentMapForMemorySurvey(List<String> questionTextList, List<String> questionAnswerList){
		Map<String, String> displayContent = new TreeMap<String, String>();
		int size = questionTextList.size();
		Object answer;
		String questionText;
		
		if (questionAnswerList.size() == size)
		{
			for (int i = 0; i < size; i++)
			{
				questionText = questionTextList.get(i).toString();
				
				answer = questionAnswerList.get(i);
				if ((answer != null) && (answer.toString().equals("1")))
					displayContent.put(questionText, "YES");					
				else
					displayContent.put(questionText, "NO");			
			}
			
			return displayContent;
		}
		else
			System.out.println("Bad thing happens");
		
		return null;   	
	}
	
	
	/**
	 * 
	 * @param list
	 * @param redundantStr
	 * @return a list of Survey question text without no-meaningful string
	 */
	public static List<String> removeRedundantFromQuestionText(List<String> list, String redundantStr){
		String str;
		int index;		
		
		for (int i = 0 ; i < list.size(); i ++)
		{
			str = list.get(i).toString();
			index = str.indexOf(redundantStr);
			if (index > 0)
			{
				str = str.substring(index + 4);
				list.set(i, str);
			}
		}
		
		return list;
	}
	
	/**
	 * remove question text and observer notes from answer
	 * @param questionMap
	 * @return a list of survey question text
	 */
	public static List<String> getQuestionList(LinkedHashMap<String, String> questionMap) {
		List<String> qList = new ArrayList<String>();
		String question;
		int index;
		
		for (Map.Entry<String, String> entry : questionMap.entrySet()) {
   		    String key = entry.getKey();
   		    
   		    if (!key.equalsIgnoreCase("title") && !key.equalsIgnoreCase("date") && !key.equalsIgnoreCase("surveyId"))
   		    {
   		    	Object value = entry.getValue();
   		    	question = value.toString();   		    	
   		    	index = question.indexOf("/answer/");
   		    	
   		    	if (index > 0)
   		    		question = question.substring(index + 8);//length of /answer/ is 8
   		    	
   		    	if (!question.equals("-"))
   		    		qList.add(question);   		    	
   		    }
   		}		
				
		return qList;
	}
	
	/**
	 * 
	 * @param questionMap
	 * @return a list of survey question which only need to be displayed on report
	 */
	public static List<String> getQuestionListForMemorySurvey(LinkedHashMap<String, String> questionMap){
		List<String> qList = new ArrayList<String>();
		String question;
		int index;
		
		for (Map.Entry<String, String> entry : questionMap.entrySet()) {
   		    String key = entry.getKey();
   		
   		    if ((key.equalsIgnoreCase("YM1"))||(key.equalsIgnoreCase("YM2")))
   		    {
   		    	Object value = entry.getValue();
   		    	question = value.toString();
   		    	index = question.indexOf("/answer/");
		    	
		    	if (index > 0)
		    		question = question.substring(index + 8);//length of /answer/ is 8
   		    	qList.add(question); 
   		    }   		   
   		}		
		return qList;
	}
	
	//for Social life survey
	public static String getDetailedAnswersForSocialLifeSurvey(String answer, int index)
	{
		String[] answers = {"Hardly ever", "Some of the time", "Most of the time", "Very dissatisfied", 
				"Somewhat dissatisfied", "Satisfied"};		
		String detailedAnswer = "";
		
		if ((index >= 0)&&(index < 7))
		{
			if("1".equals(answer))
				detailedAnswer = answers[0];				
			else if ("2".equals(answer))
				detailedAnswer = answers[1];
			else
				detailedAnswer = answers[2];
		}
		else if (index == 7)
		{
			if ("1".equals(answer))
				detailedAnswer = answers[3];
			else if ("2".equals(answer))
				detailedAnswer = answers[4];
			else
				detailedAnswer = answers[5];
		}
		else
			detailedAnswer = answer;
				
		return detailedAnswer;
	}
	//apply on Physical Activity/RAPA, Memory, Advanced Directive
	public static String getDetailedAnswersForSurvey(String answer)
	{
		String[] answers = {"Yes", "No"};		
		String detailedAnswer = "";
					
		if("1".equals(answer))
			detailedAnswer = answers[0];
		else if ("2".equals(answer))
			detailedAnswer = answers[1];	
		else
			detailedAnswer = answer;
		
		return detailedAnswer;
	}
	
	
	/**
	 * Refine question map, remove observer notes and other not related to question/answer 
	 * @param questions
	 * @return
	 */
	public static Map<String, String> getQuestionMap(LinkedHashMap<String, String> questions){
		Map<String, String> qMap = new LinkedHashMap<String, String>();		
		String question;
		int index;
		
		for (Map.Entry<String, String> entry : questions.entrySet()) {
   		    String key = entry.getKey();
   		    
   		    if (!key.equalsIgnoreCase("title") && !key.equalsIgnoreCase("date") && !key.equalsIgnoreCase("surveyId"))
   		    {
   		    	Object value = entry.getValue();
   		    	question = value.toString();
   		    	
   		    	index = question.indexOf("/answer/");
		    	
		    	if (index > 0)
		    		question = question.substring(index + 8);//length of /answer/ is 8
  		    	
   		    	if (!question.equals("-"))
   		    		qMap.put(key, question);    	
   		    }
   		}
		return qMap;
	}
	
	public static List<DisplayedSurveyResult> detailedResult(List<DisplayedSurveyResult> completedDisplayedResults)
	{
		String surveyTitle;	
		String answer;
		DisplayedSurveyResult dr;		
		int g = 0; //indicator for social life
		int j=0;// for goals
		int k=0; //for daily activities
		int l = 0; // indicator for general health
		int m = 0; // indicator for EQ5D
		int n = 0; // indicator for Nutrition
		
		for (int i=0; i<completedDisplayedResults.size(); i++){
			dr = completedDisplayedResults.get(i);
			answer = dr.getQuestionAnswer();
			surveyTitle = dr.getTitle();			
			//since title of survey could be different between the one in the survey and the one displayed
			//RAPA, Memory, Advance Care
			if (surveyTitle.contains(titleOfRAPA) || surveyTitle.contains(titleOfMemory) || surveyTitle.contains(titleOfAdvancedCarePlanning))
				dr.setQuestionAnswer(getDetailedAnswersForSurvey(answer));
			
			if (surveyTitle.contains(titleOfSocialLife))// Social Life
			{
				g++;				
				dr.setQuestionAnswer(getDetailedAnswersForSocialLifeSurvey(answer, g));
			}
			
			if (surveyTitle.contains(titleOfGoals))// Goals
			{ 
				j++;
				//get last question/answer in the Goals survey
				if (j == 8)
				{					
					String lastAns = completedDisplayedResults.get(i).getQuestionAnswer();
					
					if (lastAns.equals("1"))
						lastAns = completedDisplayedResults.get(i-3).getQuestionAnswer();
					else if (lastAns.equals("2"))
						lastAns = completedDisplayedResults.get(i-2).getQuestionAnswer();
					else
						lastAns = completedDisplayedResults.get(i-1).getQuestionAnswer();					
					//between "goal*a" and "<br>"
					if (lastAns.indexOf("<br>") != -1)
						lastAns = lastAns.substring(0,lastAns.indexOf("<br>"));
					completedDisplayedResults.get(i).setQuestionAnswer(lastAns);
				}
			}
			
			if (surveyTitle.contains(titleOfDailyLifeActivities)) //Daily Life Activity
			{
				k++;
				//get the seventh question about falling
				if (k==7)
					dr.setQuestionAnswer(getDetailedAnswersForSurvey(answer));
			}
			
			if (surveyTitle.contains(titleOfGeneralHealth))
			{
				l++;
				dr.setQuestionAnswer(getFullDescriptionForGeneralHealth(l, answer));				
			}
			
			if (surveyTitle.contains(titleOfEQ5D))
			{
				m++;
				dr.setQuestionAnswer(getFullDescriptionForEQ5D(m, answer));				
			}
			
			if (surveyTitle.contains(titleOfNutrition))
			{
				n++;
				dr.setQuestionAnswer(getFullDescriptionForNutrition(n, answer));
			}
			
			if (surveyTitle.contains(titleOfMobility))
				dr.setQuestionAnswer(getFullDescriptionForMobility(dr.getQuestionId(), answer));
		}
		return completedDisplayedResults;
	}
	
	private static String getFullDesc(String questionId, String answer)
	{
		String fullDesAnswer="";		
		int ind = Integer.valueOf(answer.replaceAll("\\s+",""));
		
		if (questionId.equals("a2a") || questionId.equals("a3a") || questionId.equals("a4a"))
		{
			 switch (ind) {
			 	case 1: fullDesAnswer = "Able to manage without difficulty";			
			 			break;
			 	case 2: fullDesAnswer = "Able to manage with some difficulty";
			 			break;
			 	case 3: fullDesAnswer = "Able to manage with a great deal of difficulty";
			 			break;
			 	case 4: fullDesAnswer = "Able to manage with the help of another person";
			 			break;	
			 	case 5: fullDesAnswer = "Unable to manage even with help";
			 			break;	
			 }
		}
		
		if (questionId.equals("a2b"))
		{
			 switch (ind) {
			 	case 1: fullDesAnswer = "I am able to walk 2 km the same way I always have";			
			 			break;
			 	case 2: fullDesAnswer = "I need to rest in the middle of the walk";
			 			break;
			 	case 3: fullDesAnswer = "I need to use a walking aid (cane/crutch/walker)";
			 			break;
			 	case 4: fullDesAnswer = "I walk 2 km less often than I used to";
			 			break;	
			 	case 5: fullDesAnswer = "It takes me longer to walk 2 km than it used to";			 	
			 			break;	
			 	case 6: fullDesAnswer = "I am more tired after walking 2 km than I used to be";
			 			break;
			 }
		}
		
		if (questionId.equals("a3b"))
		{
			 switch (ind) {
			 	case 1: fullDesAnswer = "I am able to walk 0.5 km the same way I always have";			
			 			break;
			 	case 2: fullDesAnswer = "I need to rest in the middle of the walk";
			 			break;
			 	case 3: fullDesAnswer = "I need to use a walking aid (cane/crutch/walker)";
			 			break;
			 	case 4: fullDesAnswer = "I walk 0.5 km less often than I used to";
			 			break;	
			 	case 5: fullDesAnswer = "It takes me longer to walk 0.5 km than it used to";			 	
			 			break;	
			 	case 6: fullDesAnswer = "I am more tired after walking 0.5 km than I used to be";
			 			break;
			 }
		}
		
		if (questionId.equals("a4b"))
		{
			 switch (ind) {
			 	case 1: fullDesAnswer = "I am able to climb the stairs the same way I always have";			
			 			break;
			 	case 2: fullDesAnswer = "I need to rest in the middle of the flight of stairs";
			 			break;
			 	case 3: fullDesAnswer = "I need to use a walking aid (cane/crutch/walker)";
			 			break;
			 	case 4: fullDesAnswer = "I need support from the handrails";
			 			break;	
			 	case 5: fullDesAnswer = "I climb the stairs less often than I used to";			 	
			 			break;	
			 	case 6: fullDesAnswer = "It takes me longer to climb the stairs than it used to";
			 			break;
			 }
		}
		return fullDesAnswer;		
	}
	
	private static String getFullDescriptionForMobility(String questionId, String answer)
	{		
		String[] ansList;		
		StringBuffer sb;
		
		if (answer.equals(""))
			return 	"No data";
		
		if (answer.contains(","))
		{
			sb = new StringBuffer();
			ansList = answer.split(",");
			for (int i = 0; i < ansList.length; i++)
			{
				sb.append(getFullDesc(questionId, ansList[i]));
				sb.append("<br/>");
			}
			
			return sb.toString();			
		}
		else
			return getFullDesc(questionId, answer);		

	}
	
	private static String getFullDescriptionForGeneralHealth(int ind, String answer)
	{
		String fullDesAnswer="";
        switch (ind) {
            case 1: if (answer.equals("1"))
            			fullDesAnswer = "No errors";
            		else if(answer.equals("2"))
            			fullDesAnswer = "Minor spacing errors";
            		else
            			fullDesAnswer = "Other errors";
                    break;
            case 2: if (answer.equals("1"))
            			fullDesAnswer = "0";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "1-2";
		    		else
		    			fullDesAnswer = "More than 2";
                    break;
            case 3: if (answer.equals("1"))
		    			fullDesAnswer = "Excellent";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "Very good";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "Good";
		    		else if(answer.equals("4"))
		    			fullDesAnswer = "Fair";
		    		else
		    			fullDesAnswer = "Poor";
            		break;
            case 4: if (answer.equals("1"))
		    			fullDesAnswer = "0-1";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "1-2";
		    		else
		    			fullDesAnswer = "5-8";
		            break;	
            case 5: if (answer.equals("1"))
		    			fullDesAnswer = "Always";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "Sometimes";
		    		else
		    			fullDesAnswer = "Never";
		            break;	
            case 6: case 7: case 8: case 9: case 10:
            		if (answer.equals("1"))
            			fullDesAnswer = "No";    		
            		else
            			fullDesAnswer = "Yes";
            		break;	
            case 11:if (answer.equals("1"))
		    			fullDesAnswer = "0-10s";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "11-20s";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "More than 20s";
		    		else if(answer.equals("4"))
		    			fullDesAnswer = "Patient requires assistance";
		    		else
		    			fullDesAnswer = "Patient is unwilling";
		            break;	
        }		
		return fullDesAnswer;
	}
	
	private static String getFullDescriptionForEQ5D(int ind, String answer)
	{
		String fullDesAnswer="";
        switch (ind) {          
            case 1: if (answer.equals("1"))
		    			fullDesAnswer = "I have no problems in walking about";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "I have slight problems in walking about";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "I have moderate problems in walking about";
		    		else if(answer.equals("4"))
		    			fullDesAnswer = "I have severe problems in walking about";
		    		else
		    			fullDesAnswer = "I am unable to walk about";
            		break;
            case 2: if (answer.equals("1"))
		    			fullDesAnswer = "I have no problems washing or dressing myself";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "I have slight problems washing or dressing myself";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "I have moderate problems washing or dressing myself";
		    		else if(answer.equals("4"))
		    			fullDesAnswer = "I have severe problems washing or dressing myself";
		    		else
		    			fullDesAnswer = "I am unable to wash or dress myself";
		    		break;
            case 3: if (answer.equals("1"))
		    			fullDesAnswer = "I have no problems doing my usual activities";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "I have slight problems doing my usual activities";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "I have moderate problems doing my usual activities";
		    		else if(answer.equals("4"))
		    			fullDesAnswer = "I have severe problems doing my usual activities";
		    		else
		    			fullDesAnswer = "I am unable to do my usual activities";
		    		break;	
            case 4: if (answer.equals("1"))
		    			fullDesAnswer = "I have no pain or discomfort";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "I have slight pain or discomfort";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "I have moderate pain or discomfort";
		    		else if(answer.equals("4"))
		    			fullDesAnswer = "I have severe pain or discomfort";
		    		else
		    			fullDesAnswer = "I have extreme pain or discomfort";
		    		break;	
            case 5:if (answer.equals("1"))
		    			fullDesAnswer = "I am not anxious or depressed";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "I am slightly anxious or depressed";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "I am moderately anxious or depressed";
		    		else if(answer.equals("4"))
		    			fullDesAnswer = "I am severely anxious or depressed";
		    		else
		    			fullDesAnswer = "I am extremely anxious or depressed";
		    		break;	
            case 6:
	            	fullDesAnswer = answer;
	            	break;
        }
		
		return fullDesAnswer;
	}
	
	private static String getFullDescriptionForNutrition(int ind, String answer)
	{
		String fullDesAnswer="";
        switch (ind) {          
            case 1: if (answer.equals("1"))
		    			fullDesAnswer = "No, my weight stayed within a few pounds";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "I don't know how much I weigh or if my weight has changed";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "Yes, I gained more than 10 pounds";
		    		else if(answer.equals("4"))
		    			fullDesAnswer = "Yes, I gained 6 to 10 pounds";
		    		else if(answer.equals("5"))
		    			fullDesAnswer = "Yes, I gained about 5 pounds";
		    		else if(answer.equals("6"))
		    			fullDesAnswer = "Yes, I lost more than 10 pounds";
		    		else if(answer.equals("7"))
		    			fullDesAnswer = "Yes, I lost 6 to 10 pounds";
		    		else 
		    			fullDesAnswer = "Yes, I lost about 5 pounds";
            			break;
            case 2: if (answer.equals("1"))
		    			fullDesAnswer = "Yes";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "No";
		    		else
		    			fullDesAnswer = "No, but it changed anyway";
		    		break;
            case 3: if (answer.equals("1"))
		    			fullDesAnswer = "more than it should be";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "just right";
		    		else
		    			fullDesAnswer = "less than it should be";
		    		break;	
            case 4: if (answer.equals("1"))
		    			fullDesAnswer = "Never or rarely";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "Sometimes";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "Often";
		    		else 
		    			fullDesAnswer = "Almost everyday";
		    		break;	
            case 5: if (answer.equals("1"))
		    			fullDesAnswer = "I eat most foods";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "I limit some foods and I am managing fine";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "I limit some foods and I am finding it difficult to manage";	    		
		    		break;
            case 6:if (answer.equals("1"))
		    			fullDesAnswer = "Very good";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "Good";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "Fair";		    		
		    		else
		    			fullDesAnswer = "Poor";
		    		break;	
           case 7:if (answer.equals("1"))
		    			fullDesAnswer = "Five or more";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "Four";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "Three";
		    		else if(answer.equals("4"))
		    			fullDesAnswer = "Two";
		    		else
		    			fullDesAnswer = "Less than two";
		    		break;	
            case 8:if (answer.equals("1"))
		    			fullDesAnswer = "Two or more times a day";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "One to two times a day";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "Once a day";		    		
		    		else
		    			fullDesAnswer = "Less than once a day";
		    		break;
            case 9:if (answer.equals("1"))
		    			fullDesAnswer = "Three or more times a day";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "Two to three times a day";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "One to two times a day";
		    		else if(answer.equals("4"))
		    			fullDesAnswer = "Usually once a day";
		    		else
		    			fullDesAnswer = "Less than once a day";
		    		break;
            case 10:if (answer.equals("1"))
		    			fullDesAnswer = "Eight or more cups";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "Five to seven cups";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "Three to four cups";
		    		else if(answer.equals("4"))
		    			fullDesAnswer = "About two cups";
		    		else
		    			fullDesAnswer = "Less than two cups";
		    		break;
            case 11: case 12:if (answer.equals("1"))
		    			fullDesAnswer = "Never";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "Rarely";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "Sometimes";		    		
		    		else
		    			fullDesAnswer = "Often or always";
		    		break;
            case 13:if (answer.equals("1"))
		    			fullDesAnswer = "Never or rarely";    		
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "Sometimes";		    		
		    		else
		    			fullDesAnswer = "Often or always";
		    		break;
            case 14: if (answer.equals("1"))
		    			fullDesAnswer = "Never or rarely";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "Sometimes";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "Often";		    		
		    		else
		    			fullDesAnswer = "Almost always";
		    		break;
            case 15: if (answer.equals("1"))
		    			fullDesAnswer = "I do";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "I share my cooking with someone else";
		    		else
		    			fullDesAnswer = "Someone else cooks most of my meals";
		    		break;
            case 16:if (answer.equals("1"))
		    			fullDesAnswer = "I enjoy cooking most of my meals";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "I sometimes find cooking a chore";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "I usually find cooking a chore";
		    		else if(answer.equals("4"))
		    			fullDesAnswer = "I'm satisfied with the quality of food prepared by others";
		    		else
		    			fullDesAnswer = "I'm not satisfied with the quality of food prepared by others";
		    		break;
            case 17: if (answer.equals("1"))
		    			fullDesAnswer = "Never or rarely";
		    		else if(answer.equals("2"))
		    			fullDesAnswer = "Sometimes";
		    		else if(answer.equals("3"))
		    			fullDesAnswer = "Often";		    		
		    		else
		    			fullDesAnswer = "Always";
		    		break;
        }
		
		return fullDesAnswer;
	}
	
	  /**
     * Concatenate a LinkedHashMap of results with the specified characters
     * @param results The results list returned by getResults()
     * @param join The character(s) to use to separate the question ID from the answer
     * @return The results
     */
    private static String joinResults(LinkedHashMap<String, String> results, String join){
		String ret = "";
		String separator1 = "/observernote/";
		String separator2 = "/answer/";
		String key, value;
		String observerNotes = "";
		int index1, index2;
		StringBuffer sb = new StringBuffer();
		StringBuffer sb1 = new StringBuffer();
		
		for (Map.Entry<String, String> r : results.entrySet())
		{
			key = r.getKey();
			value = r.getValue();
			if (!key.contains("surveyId") && !key.equals("date") && !key.equals("title"))
			{			
				index2 = value.indexOf(separator2);
				if (!value.startsWith("-"))//remove first non-question-answer pair, only information
    			{
					index1 = value.indexOf(separator1);					
					
    				if ((index1 != -1) && (index1 + separator1.length() != index2))// has /observernote/...
    				{
    					observerNotes = value.substring(index1 + separator1.length(), index2);     
    					sb1.append(observerNotes);
    				//	sb1.append(";");    					
    				}
    			}				
				if (index2 != -1)
					value = value.substring(index2 + separator2.length());				
			
				if (!value.startsWith("-"))
				{
					sb.append(value);
					sb.append(join);
				}	
			}
			else
				continue;			
		}
		sb1.append(join);
		sb.append(sb1.toString());
		ret = sb.toString();
					
		System.out.println("ret == "+ ret);
		return ret;
	}
    
    /**
     * Converts a result set to a series of comma-separated values that
     * can then be loaded into a spreadsheet or something.
     * @param results The LinkedHashMap returned by getResults()
     * @return The results as comma-separated values
     */
    public static String resultsAsExcel(LinkedHashMap<String, String> results){
		return joinResults(results, ";");
	}
	
	
	//=========================== Message ==================================//
	/**
	 * Set unRead number of message
	 * @param request
	 * @param model
	 * @param messageManager
	 */
	public static void setUnreadMessage(SecurityContextHolderAwareRequestWrapper request, ModelMap model, MessageManager messageManager ){
		HttpSession session = request.getSession();
		User loggedInUser = getLoggedInUser(request);
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		else
		{
			int unreadMessages = messageManager.countUnreadMessagesForRecipient(loggedInUser.getUserID());
			model.addAttribute("unread", unreadMessages);
		}
	}
	/**
	 * Send message to Inbox
	 * @param subject
	 * @param msg
	 * @param sender
	 * @param recipient
	 * @param messageManager
	 */
	public static void sendMessageToInbox(String subject, String msg, int sender, int recipient, MessageManager messageManager){	
		Message m = new Message();		
		
		m.setRecipient(recipient);
		m.setSenderID(sender);
		m.setText(msg);
		m.setSubject(subject);
		messageManager.sendMessage(m);		
	}
	/**
	 * Send message to Inbox
	 * @param msg
	 * @param sender
	 * @param recipient
	 * @param messageManager
	 */
	public static void sendMessageToInbox(String msg, int sender, int recipient, MessageManager messageManager){		
		sendMessageToInbox("New Appointment", msg, sender, recipient, messageManager);
	}
	
	
	/**
	 * Send message to client's account in MyOscar(PHR)
	 */
	public static void sendMessageToMyOscar(String msg)
	{
		//send message to MyOscar test
		try{
			Long lll = ClientManager.sentMessageToPatientInMyOscar(new Long(15231), "Message From Tapestry", msg);			
			
		} catch (Exception e){
			System.out.println("something wrong with myoscar server");
			e.printStackTrace();
		}
	}
	
	/**
	 * Set unread message count
	 * @param request
	 * @param model
	 * @param messageDao
	 */
	public static void setUnreadMsg(SecurityContextHolderAwareRequestWrapper request, 
			ModelMap model, MessageManager messageDao)
	{	
		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		else
		{
			User loggedInUser = getLoggedInUser(request);
			int unreadMessages = messageDao.countUnreadMessagesForRecipient(loggedInUser.getUserID());
			model.addAttribute("unread", unreadMessages);
		}
	}	
	
	public static void sendMessageByEmail(User user, String subject, String msg){
		try{
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(mailAddress));
			message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(user.getEmail()));

			message.setSubject(subject);
			message.setText(msg);

			System.out.println(msg);
			System.out.println("Sending...");
			Transport.send(message);
			System.out.println("Email sent containing credentials to " + user.getEmail());
		} catch (MessagingException e) {
			System.out.println("Error: Could not send email");
			System.out.println(e.toString());			
		}
	}
	
	public static void updateUnReadMsg(SecurityContextHolderAwareRequestWrapper request, 
			ModelMap model, int diff)
	{
		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
		{				
			int iUnRead = Integer.parseInt(session.getAttribute("unread_messages").toString());
			iUnRead = iUnRead + diff;
			
			session.setAttribute("unread_messages", iUnRead);
			model.addAttribute("unread", iUnRead);
		}
	}
	
	// ================= Appointment ====================//
	public static boolean hasVisit(int patientId, AppointmentManager appointmentManager){
		boolean isFirst = true;
		List<Appointment> appointments = appointmentManager.getAllApprovedAppointmentsForPatient(patientId);
		
		if ((appointments != null)&& (appointments.size()>0))
			isFirst = false;		
		return isFirst;
	}
	
	public static boolean isFirstVisit(int appointmentId, AppointmentManager appointmentManager){
		boolean isFirst = false;
		Appointment appointment = appointmentManager.getAppointmentById(appointmentId);
		
		if (appointment.getType() == 0)
			isFirst = true;
		return isFirst;
	}

	//======================Research Data Download===================================//
	//fill in Care QoL for UBC research data dump baseline period
	private static void setUBCCareQoLT0(List<SurveyResult> srList, CareGiverResearchData rData){
		SurveyResult sr	= srList.get(0);
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		
		if (!displayedResults.isEmpty())
		{
			rData.setCql1_sleep_T0(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
			rData.setCql2_inc0n_T0(Integer.parseInt(displayedResults.get(1).getQuestionAnswer()));
			rData.setCql3_appre_T0(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));
			rData.setCql4_phystr_T0(Integer.parseInt(displayedResults.get(3).getQuestionAnswer()));
			rData.setCql5_confin_T0(Integer.parseInt(displayedResults.get(4).getQuestionAnswer()));
			rData.setCql6_time_T0(Integer.parseInt(displayedResults.get(5).getQuestionAnswer()));
			rData.setCql7_famad_T0(Integer.parseInt(displayedResults.get(6).getQuestionAnswer()));
			rData.setCql8_persolan_T0(Integer.parseInt(displayedResults.get(7).getQuestionAnswer()));
			rData.setCql9_demtime_T0(Integer.parseInt(displayedResults.get(8).getQuestionAnswer()));
			rData.setCql10_emad_T0(Integer.parseInt(displayedResults.get(9).getQuestionAnswer()));
			rData.setCql11_handcare_T0(Integer.parseInt(displayedResults.get(10).getQuestionAnswer()));
			rData.setCql12_behupset_T0(Integer.parseInt(displayedResults.get(11).getQuestionAnswer()));
			rData.setCql13_change_T0(Integer.parseInt(displayedResults.get(12).getQuestionAnswer()));
			rData.setCql14_hapcare_T0(Integer.parseInt(displayedResults.get(13).getQuestionAnswer()));
			rData.setCql15_workad_T0(Integer.parseInt(displayedResults.get(14).getQuestionAnswer()));
			rData.setCql16_comover_T0(Integer.parseInt(displayedResults.get(15).getQuestionAnswer()));
			rData.setCql17_finstrain_T0(Integer.parseInt(displayedResults.get(16).getQuestionAnswer()));
			rData.setCql18_import_T0(Integer.parseInt(displayedResults.get(17).getQuestionAnswer()));
			rData.setCql19_fulfil_T0(Integer.parseInt(displayedResults.get(18).getQuestionAnswer()));
			rData.setCql20_relprob_T0(Integer.parseInt(displayedResults.get(19).getQuestionAnswer()));
			rData.setCql21_menh_T0(Integer.parseInt(displayedResults.get(20).getQuestionAnswer()));
			rData.setCql22_ownday_T0(Integer.parseInt(displayedResults.get(21).getQuestionAnswer()));
			rData.setCql23_finpro_T0(Integer.parseInt(displayedResults.get(22).getQuestionAnswer()));
			rData.setCql24_support_T0(Integer.parseInt(displayedResults.get(23).getQuestionAnswer()));
			rData.setCql25_physhel_T0(Integer.parseInt(displayedResults.get(24).getQuestionAnswer()));
			rData.setCql26_happy_T0(Integer.parseInt(displayedResults.get(25).getQuestionAnswer()));		
		}
	}
	
	//fill in Care Qol survey for UBC research data dump for both T0 and T1 period
	private static void setUBCCareQoLT1(List<SurveyResult> srList, CareGiverResearchData rData){
		setUBCCareQoLT0(srList, rData);
		
		SurveyResult sr	= srList.get(1);
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{
			rData.setCql1_sleep_T1(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
			rData.setCql2_inc0n_T1(Integer.parseInt(displayedResults.get(1).getQuestionAnswer()));
			rData.setCql3_appre_T1(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));
			rData.setCql4_phystr_T1(Integer.parseInt(displayedResults.get(3).getQuestionAnswer()));
			rData.setCql5_confin_T1(Integer.parseInt(displayedResults.get(4).getQuestionAnswer()));
			rData.setCql6_time_T1(Integer.parseInt(displayedResults.get(5).getQuestionAnswer()));
			rData.setCql7_famad_T1(Integer.parseInt(displayedResults.get(6).getQuestionAnswer()));
			rData.setCql8_persolan_T1(Integer.parseInt(displayedResults.get(7).getQuestionAnswer()));
			rData.setCql9_demtime_T1(Integer.parseInt(displayedResults.get(8).getQuestionAnswer()));
			rData.setCql10_emad_T1(Integer.parseInt(displayedResults.get(9).getQuestionAnswer()));
			rData.setCql11_handcare_T1(Integer.parseInt(displayedResults.get(10).getQuestionAnswer()));
			rData.setCql12_behupset_T1(Integer.parseInt(displayedResults.get(11).getQuestionAnswer()));
			rData.setCql13_change_T1(Integer.parseInt(displayedResults.get(12).getQuestionAnswer()));
			rData.setCql14_hapcare_T1(Integer.parseInt(displayedResults.get(13).getQuestionAnswer()));
			rData.setCql15_workad_T1(Integer.parseInt(displayedResults.get(14).getQuestionAnswer()));
			rData.setCql16_comover_T1(Integer.parseInt(displayedResults.get(15).getQuestionAnswer()));
			rData.setCql17_finstrain_T1(Integer.parseInt(displayedResults.get(16).getQuestionAnswer()));
			rData.setCql18_import_T1(Integer.parseInt(displayedResults.get(17).getQuestionAnswer()));
			rData.setCql19_fulfil_T1(Integer.parseInt(displayedResults.get(18).getQuestionAnswer()));
			rData.setCql20_relprob_T1(Integer.parseInt(displayedResults.get(19).getQuestionAnswer()));
			rData.setCql21_menh_T1(Integer.parseInt(displayedResults.get(20).getQuestionAnswer()));
			rData.setCql22_ownday_T1(Integer.parseInt(displayedResults.get(21).getQuestionAnswer()));
			rData.setCql23_finpro_T1(Integer.parseInt(displayedResults.get(22).getQuestionAnswer()));
			rData.setCql24_support_T1(Integer.parseInt(displayedResults.get(23).getQuestionAnswer()));
			rData.setCql25_physhel_T1(Integer.parseInt(displayedResults.get(24).getQuestionAnswer()));
			rData.setCql26_happy_T1(Integer.parseInt(displayedResults.get(25).getQuestionAnswer()));	
		}
	}
	
	//fill in Care Qol survey for UBC research data dump all 3(T0/T1/T2) period
	private static void setUBCCareQoLT2(List<SurveyResult> srList, CareGiverResearchData rData){
		setUBCCareQoLT1(srList, rData);
		
		SurveyResult sr	= srList.get(2);
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{		
			rData.setCql1_sleep_T2(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
			rData.setCql2_inc0n_T2(Integer.parseInt(displayedResults.get(1).getQuestionAnswer()));
			rData.setCql3_appre_T2(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));
			rData.setCql4_phystr_T2(Integer.parseInt(displayedResults.get(3).getQuestionAnswer()));
			rData.setCql5_confin_T2(Integer.parseInt(displayedResults.get(4).getQuestionAnswer()));
			rData.setCql6_time_T2(Integer.parseInt(displayedResults.get(5).getQuestionAnswer()));
			rData.setCql7_famad_T2(Integer.parseInt(displayedResults.get(6).getQuestionAnswer()));
			rData.setCql8_persolan_T2(Integer.parseInt(displayedResults.get(7).getQuestionAnswer()));
			rData.setCql9_demtime_T2(Integer.parseInt(displayedResults.get(8).getQuestionAnswer()));
			rData.setCql10_emad_T2(Integer.parseInt(displayedResults.get(9).getQuestionAnswer()));
			rData.setCql11_handcare_T2(Integer.parseInt(displayedResults.get(10).getQuestionAnswer()));
			rData.setCql12_behupset_T2(Integer.parseInt(displayedResults.get(11).getQuestionAnswer()));
			rData.setCql13_change_T2(Integer.parseInt(displayedResults.get(12).getQuestionAnswer()));
			rData.setCql14_hapcare_T2(Integer.parseInt(displayedResults.get(13).getQuestionAnswer()));
			rData.setCql15_workad_T2(Integer.parseInt(displayedResults.get(14).getQuestionAnswer()));
			rData.setCql16_comover_T2(Integer.parseInt(displayedResults.get(15).getQuestionAnswer()));
			rData.setCql17_finstrain_T2(Integer.parseInt(displayedResults.get(16).getQuestionAnswer()));
			rData.setCql18_import_T2(Integer.parseInt(displayedResults.get(17).getQuestionAnswer()));
			rData.setCql19_fulfil_T2(Integer.parseInt(displayedResults.get(18).getQuestionAnswer()));
			rData.setCql20_relprob_T2(Integer.parseInt(displayedResults.get(19).getQuestionAnswer()));
			rData.setCql21_menh_T2(Integer.parseInt(displayedResults.get(20).getQuestionAnswer()));
			rData.setCql22_ownday_T2(Integer.parseInt(displayedResults.get(21).getQuestionAnswer()));
			rData.setCql23_finpro_T2(Integer.parseInt(displayedResults.get(22).getQuestionAnswer()));
			rData.setCql24_support_T2(Integer.parseInt(displayedResults.get(23).getQuestionAnswer()));
			rData.setCql25_physhel_T2(Integer.parseInt(displayedResults.get(24).getQuestionAnswer()));
			rData.setCql26_happy_T2(Integer.parseInt(displayedResults.get(25).getQuestionAnswer()));	
		}
	}
	
	//fill in Zarit  survey for UBC research data dump on baseline period
	private static void setUBCZaritT0(List<SurveyResult> srList, CareGiverResearchData rData)
	{
		SurveyResult sr = srList.get(0);	
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{
			rData.setZarit1_time_T0(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
			rData.setZarit2_stress_T0(Integer.parseInt(displayedResults.get(1).getQuestionAnswer()));
			rData.setZarit3_angry_T0(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));
			rData.setZarit4_other_T0(Integer.parseInt(displayedResults.get(3).getQuestionAnswer()));
			rData.setZarit5_strain_T0(Integer.parseInt(displayedResults.get(4).getQuestionAnswer()));
			rData.setZarit6_health_T0(Integer.parseInt(displayedResults.get(5).getQuestionAnswer()));
			rData.setZarit7_priv_T0(Integer.parseInt(displayedResults.get(6).getQuestionAnswer()));				
			rData.setZarit8_social_T0(Integer.parseInt(displayedResults.get(7).getQuestionAnswer()));
			rData.setZarit9_control_T0(Integer.parseInt(displayedResults.get(8).getQuestionAnswer()));
			rData.setZarit10_uncert_T0(Integer.parseInt(displayedResults.get(9).getQuestionAnswer()));
			rData.setZarit11_more_T0(Integer.parseInt(displayedResults.get(10).getQuestionAnswer()));		
			rData.setZarit12_better_T0(Integer.parseInt(displayedResults.get(11).getQuestionAnswer()));
		}
	}
	
	//fill in Zarit  survey for UBC research data dump on both baseline and T1 period
	private static void setUBCZaritT1(List<SurveyResult> srList, CareGiverResearchData rData)
	{
		setUBCZaritT0(srList, rData);
		
		SurveyResult sr = srList.get(1);
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{
			rData.setZarit1_time_T1(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
			rData.setZarit2_stress_T1(Integer.parseInt(displayedResults.get(1).getQuestionAnswer()));
			rData.setZarit3_angry_T1(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));
			rData.setZarit4_other_T1(Integer.parseInt(displayedResults.get(3).getQuestionAnswer()));
			rData.setZarit5_strain_T1(Integer.parseInt(displayedResults.get(4).getQuestionAnswer()));
			rData.setZarit6_health_T1(Integer.parseInt(displayedResults.get(5).getQuestionAnswer()));
			rData.setZarit7_priv_T1(Integer.parseInt(displayedResults.get(6).getQuestionAnswer()));				
			rData.setZarit8_social_T1(Integer.parseInt(displayedResults.get(7).getQuestionAnswer()));
			rData.setZarit9_control_T1(Integer.parseInt(displayedResults.get(8).getQuestionAnswer()));
			rData.setZarit10_uncert_T1(Integer.parseInt(displayedResults.get(9).getQuestionAnswer()));
			rData.setZarit11_more_T1(Integer.parseInt(displayedResults.get(10).getQuestionAnswer()));		
			rData.setZarit12_better_T1(Integer.parseInt(displayedResults.get(11).getQuestionAnswer()));
		}
	}
	
	//fill in Zarit  survey for UBC research data dump on all 3 (T0/T1/T2 period
	private static void setUBCZaritT2(List<SurveyResult> srList, CareGiverResearchData rData)
	{
		setUBCZaritT1(srList, rData);		
		
		SurveyResult sr = srList.get(2);	
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);		
		
		if (!displayedResults.isEmpty())
		{
			rData.setZarit1_time_T2(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
			rData.setZarit2_stress_T2(Integer.parseInt(displayedResults.get(1).getQuestionAnswer()));
			rData.setZarit3_angry_T2(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));
			rData.setZarit4_other_T2(Integer.parseInt(displayedResults.get(3).getQuestionAnswer()));
			rData.setZarit5_strain_T2(Integer.parseInt(displayedResults.get(4).getQuestionAnswer()));
			rData.setZarit6_health_T2(Integer.parseInt(displayedResults.get(5).getQuestionAnswer()));
			rData.setZarit7_priv_T2(Integer.parseInt(displayedResults.get(6).getQuestionAnswer()));				
			rData.setZarit8_social_T2(Integer.parseInt(displayedResults.get(7).getQuestionAnswer()));
			rData.setZarit9_control_T2(Integer.parseInt(displayedResults.get(8).getQuestionAnswer()));
			rData.setZarit10_uncert_T2(Integer.parseInt(displayedResults.get(9).getQuestionAnswer()));
			rData.setZarit11_more_T2(Integer.parseInt(displayedResults.get(10).getQuestionAnswer()));		
			rData.setZarit12_better_T2(Integer.parseInt(displayedResults.get(11).getQuestionAnswer()));
		}
	}
	//fill in Background survey for UBC reasearch data dump
	private static void setUBCBackground(List<SurveyResult> srList, CareGiverResearchData rData)
	{
		SurveyResult sr = srList.get(0);
		Map<String, String> mResults = new HashMap<String, String>();		
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);	
		if (!displayedResults.isEmpty())
		{
			mResults = mapSurveyResults(displayedResults);			
			if (mResults.get("CB1") == null)
				rData.setCb1_ed_T0(Integer.parseInt(mResults.get("CBack0_Edu_T0")));
			else
				rData.setCb1_ed_T0(Integer.parseInt(mResults.get("CB1")));
			
			if (mResults.get("CB1a") == null)
			{
				if (mResults.get("CBack1_EduSpecify_T0") != null)
					rData.setCb1a_ed_T0(mResults.get("CBack1_EduSpecify_T0"));
			}
			else
				rData.setCb1a_ed_T0(mResults.get("CB1a"));
			
			if (mResults.get("CB2") == null)
				rData.setCb2_empl_T0(Integer.parseInt(mResults.get("CBack2_Employ_T0")));
			else
				rData.setCb2_empl_T0(Integer.parseInt(mResults.get("CB2")));
			
			if (mResults.get("CB3") == null)
				rData.setCb3_livewith_T0(Integer.parseInt(mResults.get("CBack3_LiveWith_T0")));
			else
				rData.setCb3_livewith_T0(Integer.parseInt(mResults.get("CB3")));
			
			if (mResults.get("CB4") == null)
				rData.setCb4_children_T0(Integer.parseInt(mResults.get("CBack4_Children_T0")));
			else
				rData.setCb4_children_T0(Integer.parseInt(mResults.get("CB4")));
			
			if (mResults.get("CB4a") == null)
			{
				if (mResults.get("CBack5_Home_T0") != null)
					rData.setCb4a_T0(Integer.parseInt(mResults.get("CBack5_Home_T0")));
			}
			else
				rData.setCb4a_T0(Integer.parseInt(mResults.get("CB4a")));
			
			if (mResults.get("CB5") == null)
				rData.setCb5_partner_T0(Integer.parseInt(mResults.get("CBack6_Partner_T0")));
			else
				rData.setCb5_partner_T0(Integer.parseInt(mResults.get("CB5")));
			
			if (mResults.get("CB6") == null)
				rData.setCb6_agegr_T0(Integer.parseInt(mResults.get("CBack7_Age_T0")));
			else
				rData.setCb6_agegr_T0(Integer.parseInt(mResults.get("CB6")));
			
			if (mResults.get("CB7") == null)
				rData.setCb7_gender_T0(Integer.parseInt(mResults.get("CBack8_Gender_T0")));
			else
				rData.setCb7_gender_T0(Integer.parseInt(mResults.get("CB7")));
			
			if (mResults.get("CB7a") == null)
			{
				if (mResults.get("CBack9_GenSpecify_T0") != null)
					rData.setCb7a_T0((mResults.get("CBack9_GenSpecify_T0")));
			}
			else
				rData.setCb7a_T0((mResults.get("CB7a")));
			
			if (mResults.get("CB8") == null)
				rData.setCb8_lang_T0(mResults.get("CBack10_LangHome_T0"));
			else
				rData.setCb8_lang_T0(mResults.get("CB8"));
			
			if (mResults.get("CB9") == null)
				rData.setCb9_born_T0(mResults.get("CBack11_BornCan_T0"));
			else
				rData.setCb9_born_T0(mResults.get("CB9"));		
			
			if (mResults.get("CB10") == null)
				rData.setCb10_ethnic_T0(mResults.get("CBack12_Ethinic_T0"));
			else
				rData.setCb10_ethnic_T0(mResults.get("CB10"));
			
			if (mResults.get("CB10b") == null)
			{
				if (mResults.get("CBack13_EthincSpecify_T0") != null)
					rData.setCb10a(mResults.get("CBack13_EthincSpecify_T0"));
			}
			else
				rData.setCb10a(mResults.get("CB10b"));
			
			if (mResults.get("CB11") == null)
				rData.setCb11_relation_T0(Integer.parseInt(mResults.get("CBack14_Relation_T0")));
			else
				rData.setCb11_relation_T0(Integer.parseInt(mResults.get("CB11")));
			
			if (mResults.get("CB11b") == null)
			{
				if (mResults.get("CBack15_RelationSpecify_T0") != null)
					rData.setCb11a_T0(mResults.get("CBack15_RelationSpecify_T0"));
			}
			else
				rData.setCb11a_T0(mResults.get("CB11b"));
			
			if (mResults.get("CB12") == null)
				rData.setCb12_startcg_T0(mResults.get("CBack16_Start_T0"));
			else
				rData.setCb12_startcg_T0(mResults.get("CB12"));
			
			if (mResults.get("CB13") == null)
				rData.setCb13_physcare_T0(Integer.parseInt(mResults.get("CBack17_HoursPhy_T0")));
			else
				rData.setCb13_physcare_T0(Integer.parseInt(mResults.get("CB13")));
			
			if (mResults.get("CB14") == null)
				rData.setCb14_emot_T0(Integer.parseInt(mResults.get("CBack18_HoursSocial_T0")));
			else
				rData.setCb14_emot_T0(Integer.parseInt(mResults.get("CB14")));
			
			if (mResults.get("CB15") == null)
				rData.setCb15_other_T0(Integer.parseInt(mResults.get("CBack19_HoursOther_T0")));
			else
				rData.setCb15_other_T0(Integer.parseInt(mResults.get("CB15")));
			
			if (mResults.get("CB16") == null)
				rData.setCb16_howlong_T0(mResults.get("CBack20_HowLong_T0"));
			else
				rData.setCb16_howlong_T0(mResults.get("CB16"));
			
			if (mResults.get("CB17") == null)
				rData.setCb17_onlycg_T0(Integer.parseInt(mResults.get("CBack21_Only_T0")));
			else
				rData.setCb17_onlycg_T0(Integer.parseInt(mResults.get("CB17")));
			
			if (mResults.get("CB17c") == null)
			{
				if (mResults.get("CBack22_NumOtherPeople_T0") != null)
					rData.setCb17a_T0(mResults.get("CBack22_NumOtherPeople_T0"));
			}
			else
				rData.setCb17a_T0(mResults.get("CB17c"));
			
			if (mResults.get("CB17d") == null)
				rData.setCb17b_T0((mResults.get("CBack23_OtherHelpers_T0")));
			else
				rData.setCb17b_T0((mResults.get("CB17d")));
			
			if (mResults.get("CB18") == null)
				rData.setCb18_healthcon_T0(mResults.get("CBack24_MainConcern_T0"));
			else
				rData.setCb18_healthcon_T0(mResults.get("CB18"));
			
			if (mResults.get("CB19") == null)
				rData.setCb19_cgotherppl_T0(mResults.get("CBack25_Experince_T0"));
			else
				rData.setCb19_cgotherppl_T0(mResults.get("CB19"));
					
			if (mResults.get("CB21") == null)
				rData.setCb20_yourhealth_T0(mResults.get("CBack26_Moment_T0"));
			else
				rData.setCb20_yourhealth_T0(mResults.get("CB21"));
		}
	}
	
	//fill in Background Follow up survey for UBC research data dump on T1 period
	private static void setUBCBackgroundFollowupT1(List<SurveyResult> srList, CareGiverResearchData rData)
	{
		SurveyResult sr = srList.get(0);
		Map<String, String> mResults = new HashMap<String, String>();
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{
			mResults = mapSurveyResults(displayedResults);
			if (mResults.get("CB1") == null)
				rData.setCb2_empl_T1(Integer.parseInt(mResults.get("CFolllow0_EmployentStat_T0")));
			else
				rData.setCb2_empl_T1(Integer.parseInt(mResults.get("CB1")));
			
			if (mResults.get("CB2") == null)
				rData.setCb3_livewith_T1(Integer.parseInt(mResults.get("CFollow1_LiveWith_T0")));
			else
				rData.setCb3_livewith_T1(Integer.parseInt(mResults.get("CB2")));
			
			if (mResults.get("CB3") == null)
				rData.setCb4_children_T1(Integer.parseInt(mResults.get("CFollow2_Children_T0")));
			else
				rData.setCb4_children_T1(Integer.parseInt(mResults.get("CB3")));
			
			if (mResults.get("CB3a") != null)
				rData.setCb4a_T1(Integer.parseInt(mResults.get("CB3a")));
			else if (mResults.get("CFollow3_Home_T0") != null)
				rData.setCb4a_T1(Integer.parseInt(mResults.get("CFollow3_Home_T0")));
				
			if (mResults.get("CB4") == null)
				rData.setCb5_partner_T1(Integer.parseInt(mResults.get("CFollow4_Partner_T0")));
			else
				rData.setCb5_partner_T1(Integer.parseInt(mResults.get("CB4")));
			
			if (mResults.get("CB5a") == null)
				rData.setCb13_physcare_T1(Integer.parseInt(mResults.get("CFollow5_HoursPhy_T0")));
			else
				rData.setCb13_physcare_T1(Integer.parseInt(mResults.get("CB5a")));
			
			if (mResults.get("CB5b") == null)
				rData.setCb14_emot_T1(Integer.parseInt(mResults.get("CFollow5_HoursSocial_T0")));
			else
				rData.setCb14_emot_T1(Integer.parseInt(mResults.get("CB5b")));
			
			if (mResults.get("CB5c") == null)			
				rData.setCb15_other_T1(Integer.parseInt(mResults.get("CFollow6_HoursOther_T0")));
			else
				rData.setCb15_other_T1(Integer.parseInt(mResults.get("CB5c")));
			
			if (mResults.get("CB6") != null)
				rData.setCb16_howlong_T1(mResults.get("CB6"));
			else if (mResults.get("CFollow7_HowLong_T0") != null)
				rData.setCb16_howlong_T1(mResults.get("CFollow7_HowLong_T0"));
			
			if (mResults.get("CB6a") == null)
				rData.setCb12_startcg_T1(mResults.get("CFollow8_Only_T0"));
			else
				rData.setCb12_startcg_T1(mResults.get("CB6a"));				
			
			if (mResults.get("CB7") != null)
				rData.setCb17_onlycg_T1(Integer.parseInt(mResults.get("CB7")));
			else if (mResults.get("CFollow9_Only_T0") != null)
				rData.setCb17_onlycg_T1(Integer.parseInt(mResults.get("CFollow9_Only_T0")));			
			
			if (mResults.get("CB7b") != null)
				rData.setCb17a_T1(mResults.get("CB7b"));
			else if (mResults.get("CFollow10_NumOtherPeople_T0") != null)
				rData.setCb17a_T1(mResults.get("CFollow10_NumOtherPeople_T0"));
			
			if (mResults.get("CB7c") != null)
				rData.setCb17b_T1(mResults.get("CB7c"));
			else if (mResults.get("CFollow11_OtherHelpers_T0") != null)
				rData.setCb17b_T1(mResults.get("CFollow11_OtherHelpers_T0"));
			
			if (mResults.get("CB8") == null)
				rData.setCb18_healthcon_T1(mResults.get("CFollow12_MainConcern_T0"));
			else
				rData.setCb18_healthcon_T1(mResults.get("CB8"));
			
			if (mResults.get("CB9") == null)
				rData.setCb19_cgotherppl_T1(mResults.get("CFollow13_Experince_T0"));				
			else
				rData.setCb19_cgotherppl_T1(mResults.get("CB9"));			
			
			if (mResults.get("CB10") == null)
				rData.setCb20_yourhealth_T1(mResults.get("CFollow14_Moment_T0"));	
			else
				rData.setCb20_yourhealth_T1(mResults.get("CB10"));				
		}
	}	
	//fill in Background Fowllow up  survey for UBC research data dump on T1/T2 period
	private static void setUBCBackgroundFollowupT2(List<SurveyResult> srList, CareGiverResearchData rData)
	{
		setUBCBackgroundFollowupT1(srList, rData);
		
		SurveyResult sr = srList.get(1);	
		Map<String, String> mResults = new HashMap<String, String>();		
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{
			mResults = mapSurveyResults(displayedResults);	
			
			if (mResults.get("CB1") == null)
				rData.setCb2_empl_T2(Integer.parseInt(mResults.get("CFolllow0_EmployentStat_T0")));
			else
				rData.setCb2_empl_T2(Integer.parseInt(mResults.get("CB1")));
			
			if (mResults.get("CB2") == null)
				rData.setCb3_livewith_T2(Integer.parseInt(mResults.get("CFollow1_LiveWith_T0")));
			else
				rData.setCb3_livewith_T2(Integer.parseInt(mResults.get("CB2")));
			
			if (mResults.get("CB3") == null)
				rData.setCb4_children_T2(Integer.parseInt(mResults.get("CFollow2_Children_T0")));
			else
				rData.setCb4_children_T2(Integer.parseInt(mResults.get("CB3")));
			
			if (mResults.get("CB3a") != null)
				rData.setCb4a_T2(Integer.parseInt(mResults.get("CB3a")));
			else if (mResults.get("CFollow3_Home_T0") != null)
				rData.setCb4a_T2(Integer.parseInt(mResults.get("CFollow3_Home_T0")));
				
			if (mResults.get("CB4") == null)
				rData.setCb5_partner_T2(Integer.parseInt(mResults.get("CFollow4_Partner_T0")));
			else
				rData.setCb5_partner_T2(Integer.parseInt(mResults.get("CB4")));
			
			if (mResults.get("CB5a") == null)
				rData.setCb13_physcare_T2(Integer.parseInt(mResults.get("CFollow5_HoursPhy_T0")));
			else
				rData.setCb13_physcare_T2(Integer.parseInt(mResults.get("CB5a")));
			
			if (mResults.get("CB5b") == null)
				rData.setCb14_emot_T2(Integer.parseInt(mResults.get("CFollow5_HoursSocial_T0")));
			else
				rData.setCb14_emot_T2(Integer.parseInt(mResults.get("CB5b")));
			
			if (mResults.get("CB5c") == null)			
				rData.setCb15_other_T2(Integer.parseInt(mResults.get("CFollow6_HoursOther_T0")));
			else
				rData.setCb15_other_T2(Integer.parseInt(mResults.get("CB5c")));
			
			if (mResults.get("CB6") != null)
				rData.setCb16_howlong_T2(mResults.get("CB6"));
			else if (mResults.get("CFollow7_HowLong_T0") != null)
				rData.setCb16_howlong_T2(mResults.get("CFollow7_HowLong_T0"));
						
			if (mResults.get("CB6a") == null)
				rData.setCb12_startcg_T2(mResults.get("CFollow8_Only_T0"));
			else
				rData.setCb12_startcg_T2(mResults.get("CB6a"));				
						
			if (mResults.get("CB7") != null)
				rData.setCb17_onlycg_T2(Integer.parseInt(mResults.get("CB7")));
			else if (mResults.get("CFollow9_Only_T0") != null)
				rData.setCb17_onlycg_T2(Integer.parseInt(mResults.get("CFollow9_Only_T0")));		
			
			if (mResults.get("CB7b") != null)
				rData.setCb17a_T2(mResults.get("CB7b"));
			else if (mResults.get("CFollow10_NumOtherPeople_T0") != null)
				rData.setCb17a_T2(mResults.get("CFollow10_NumOtherPeople_T0"));
			
			if (mResults.get("CB7c") != null)
				rData.setCb17b_T2(mResults.get("CB7c"));
			else if (mResults.get("CFollow11_OtherHelpers_T0") != null)
				rData.setCb17b_T2(mResults.get("CFollow11_OtherHelpers_T0"));
			
			if (mResults.get("CB8") == null)
				rData.setCb18_healthcon_T2(mResults.get("CFollow12_MainConcern_T0"));
			else
				rData.setCb18_healthcon_T2(mResults.get("CB8"));
			
			if (mResults.get("CB9") == null)
				rData.setCb19_cgotherppl_T2(mResults.get("CFollow13_Experince_T0"));				
			else
				rData.setCb19_cgotherppl_T2(mResults.get("CB9"));			
			
			if (mResults.get("CB10") == null)
				rData.setCb20_yourhealth_T2(mResults.get("CFollow14_Moment_T0"));	
			else
				rData.setCb20_yourhealth_T2(mResults.get("CB10"));		
		}
	}

	public static List<CareGiverResearchData> getCareGiverResearchData(SurveyManager surveyManager,List<Volunteer> volunteers)
	{		
		List<CareGiverResearchData> researchDatas = new ArrayList<CareGiverResearchData>();
		CareGiverResearchData rData;
		int vId, size;
		List<SurveyResult> srList = new ArrayList<SurveyResult>();
		List<SurveyResult> srList1 = new ArrayList<SurveyResult>();
		StringBuffer sb;
		Volunteer vol;
		Map<String, String> mResults = new HashMap<String, String>();
		
		for (int i = 0; i<volunteers.size(); i++ )
		{
			vol = volunteers.get(i);		
			vId = vol.getVolunteerId();
			rData = new CareGiverResearchData();			
			rData.setVolunteerId(vId);
			rData.setName(vol.getFirstName() + " " + vol.getLastName());			
			//Care_giver background
			srList = surveyManager.getCompletedSurveyResultByVolunteertAndTitle(vId, "Caregiver Background");	
			srList1 = surveyManager.getCompletedSurveyResultByVolunteertAndTitle(vId, "Caregiver Fixed");	
			srList.addAll(srList1);
			if (srList.size() >0)
				setUBCBackground(srList, rData);			
			//Caregiver background Follow up	
			srList = surveyManager.getCompletedSurveyResultByVolunteertAndTitle(vId, "Caregiver Follow Up");	
			srList1 = surveyManager.getCompletedSurveyResultByVolunteertAndTitle(vId, "Caregiver Followup Fixed");
			srList.addAll(srList1);

			size = srList.size();
			switch (size) {
				case 0: break;
				case 1: setUBCBackgroundFollowupT1(srList, rData);			
			 			break;
			 	case 2: setUBCBackgroundFollowupT2(srList, rData);		
			 			break;				 
			}			
			//Care QoL
			srList = surveyManager.getCompletedSurveyResultByVolunteertAndTitle(vId, "CarerQol");
			srList1 = surveyManager.getCompletedSurveyResultByVolunteertAndTitle(vId, "Caregiver QoL fixed");						
			srList.addAll(srList1);			
			size = srList.size();
			switch (size) {
				case 0: break;
				case 1: setUBCCareQoLT0(srList, rData);			
						break;
				case 2: setUBCCareQoLT1(srList, rData);		
				 		break;
				 case 3: setUBCCareQoLT2(srList, rData);		
				 		break;
			}						
			//Zarit			
			srList = surveyManager.getCompletedSurveyResultByVolunteertAndTitle(vId, "Zarit");					
			size = srList.size();
			switch (size) {
				case 0: break;
				case 1: setUBCZaritT0(srList, rData);			
					break;
				case 2: setUBCZaritT1(srList, rData);		
					break;
				case 3: setUBCZaritT2(srList, rData);		
					break;
			}							
			researchDatas.add(rData);
		}		
		return researchDatas;
	}
	
	private static Map<String, String> mapSurveyResults(List<DisplayedSurveyResult>  results)
	{		//set questionId as key, and answer as value in the map
		Map<String, String> mResults = new HashMap<String, String>();
		for (int i=0; i<results.size(); i++)
		{
			mResults.put(results.get(i).getQuestionId(), results.get(i).getQuestionAnswer());			
		}		
		return mResults;
	}	
	
	public static XSSFSheet fillSheet(Map<Integer, Object[]> data, XSSFSheet sheet, XSSFWorkbook book)
	{		//Iterate over data and write to sheet
		Set<Integer> keyset = data.keySet();
		int rownum = 0;
		int cellnum;  
		Row row;
		Object [] objArr;
		XSSFCellStyle cStyle = book.createCellStyle();		
		cStyle.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
		
		for (Integer key : keyset)
		{
			row = sheet.createRow(rownum++);           
			objArr = data.get(key);   
			
			cellnum = 0;
			for (Object obj : objArr)
			{
				Cell cell = row.createCell(cellnum++);               
				if(obj instanceof String)
				{
					cell.setCellValue((String)obj);
					cell.setCellStyle(cStyle);
				}
				else if(obj instanceof Integer)
				{					
					if ((Integer)obj == 0 )
						cell.setCellValue(999);
					else if ((Integer)obj == 888)
						cell.setCellValue(0);
					else
						cell.setCellValue((Integer)obj);    
				}				
			}
		}   
		//Adjusts the each column width to fit the contents
		for (int c=1; c<=250; c++)
			sheet.autoSizeColumn(c);
		
		return sheet;
	}
	
	private static void setSocialLife(SurveyResult sr, ResearchData rData)
	{		
		String observerNote;		
		int size;
		StringBuffer sb = new StringBuffer();
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		
		if (!displayedResults.isEmpty())
		{
			rData.setdSS1_role_TO(displayedResults.get(0).getQuestionAnswer());
			rData.setdSS2_under_TO(displayedResults.get(1).getQuestionAnswer());
			rData.setdSS3_useful_TO(displayedResults.get(2).getQuestionAnswer());
			rData.setdSS4_listen_TO(displayedResults.get(3).getQuestionAnswer());
			rData.setdSS5_happen_TO(displayedResults.get(4).getQuestionAnswer());
			rData.setdSS6_talk_TO(displayedResults.get(5).getQuestionAnswer());
			rData.setdSS7_satisfied_TO(displayedResults.get(6).getQuestionAnswer());
			rData.setdSS8_nofam_TO(displayedResults.get(7).getQuestionAnswer());		   		
			rData.setdSS9_timesnotliving_TO(displayedResults.get(8).getQuestionAnswer());
			rData.setdSS10_timesphone_TO(displayedResults.get(9).getQuestionAnswer());	
			size = displayedResults.size();
			if (size == 11)
				rData.setdSS11_timesclubs_TO(displayedResults.get(10).getQuestionAnswer());
		   		
			for (int j=0; j<size; j++)
			{
				observerNote = displayedResults.get(j).getObserverNotes();
				if (!Utils.isNullOrEmpty(observerNote))
				{
					sb.append(displayedResults.get(j).getObserverNotes());	
					sb.append(".");		
		   		}
		   	}										
			rData.setdSS_notes_TO(sb.toString());
		}		
	}
	
	private static void setQualityOfLife(SurveyResult sr, ResearchData rData)
	{		
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		
		if (!displayedResults.isEmpty())
		{			
			rData.setQol1(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
			rData.setQol2(Integer.parseInt(displayedResults.get(1).getQuestionAnswer()));
			rData.setQol3(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));
			rData.setQol4(Integer.parseInt(displayedResults.get(3).getQuestionAnswer()));
			rData.setQol5(Integer.parseInt(displayedResults.get(4).getQuestionAnswer()));
			rData.setQol6(Integer.parseInt(displayedResults.get(5).getQuestionAnswer()));
		}
	}
	
	private static void setGoals(SurveyResult sr, ResearchData rData)
	{
		String  observerNote;
		int size;
		String[] goalsArray;
		StringBuffer sb = new StringBuffer();						
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);	
		
		if (!displayedResults.isEmpty())					
		{
			rData.setGoals1Matter_TO(displayedResults.get(0).getQuestionAnswer());													
			rData.setGoals2Life_TO(trimGoalMsg(displayedResults.get(1).getQuestionAnswer()));
			rData.setGoals3Health_TO(trimGoalMsg(displayedResults.get(2).getQuestionAnswer()));
			rData.setGoals4List_TO(trimGoalMsg(displayedResults.get(3).getQuestionAnswer()));
											
			goalsArray = displayedResults.get(4).getQuestionAnswer().split("<br>");
			size = goalsArray.length;
			if (size == 3)
			{
				rData.setGoals5FirstSpecific_TO(goalsArray[0]);
				rData.setGoals6FirstBaseline_TO(goalsArray[1]);
				rData.setGoals7FirstTaget_TO(goalsArray[2]);
			}				
						
			goalsArray = displayedResults.get(5).getQuestionAnswer().split("<br>");
			size = goalsArray.length;
			if (size == 3)
			{
				rData.setGoals5SecondSpecific_TO(goalsArray[0]);
				rData.setGoals6SecondBaseline_TO(goalsArray[1]);
				rData.setGoals7SecondTaget_TO(goalsArray[2]);
			}
			
			goalsArray = displayedResults.get(6).getQuestionAnswer().split("<br>");
			size = goalsArray.length;
			if (size == 3)
			{
				rData.setGoals5ThirdSpecific_TO(goalsArray[0]);
				rData.setGoals6ThirdBaseline_TO(goalsArray[1]);	
				rData.setGoals7ThirdTaget_TO(goalsArray[2]);
			}			
			
			if (displayedResults.size() == 8)
				rData.setGoals8Priority_TO(displayedResults.get(7).getQuestionAnswer());
			
			for (int j=0; j<displayedResults.size(); j++)
			{
				observerNote = displayedResults.get(j).getObserverNotes();
				if (!Utils.isNullOrEmpty(observerNote))
				{
					sb.append(displayedResults.get(j).getObserverNotes());
					sb.append(".");			
		   		}
		   	}
			rData.setGoalsDiscussion_notes_TO(sb.toString());			
		}	
	}
	
	private static void setRAPA(SurveyResult sr, ResearchData rData)
	{	
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);				
		if (!displayedResults.isEmpty())
		{			//if answer is empty, format it to 0
			displayedResults = formatEmptyResultAnswerToInt(displayedResults);				
			if (displayedResults.size() == 9)
			{//new survey with 9 questions
				rData.setRapa1(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
				rData.setRapa2(Integer.parseInt(displayedResults.get(1).getQuestionAnswer()));
				rData.setRapa3(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));
				rData.setRapa4(Integer.parseInt(displayedResults.get(3).getQuestionAnswer()));
				rData.setRapa5(Integer.parseInt(displayedResults.get(4).getQuestionAnswer()));
				rData.setRapa6(Integer.parseInt(displayedResults.get(5).getQuestionAnswer()));
				rData.setRapa7(Integer.parseInt(displayedResults.get(6).getQuestionAnswer()));
				rData.setRapa8(Integer.parseInt(displayedResults.get(7).getQuestionAnswer()));
				rData.setRapa9(Integer.parseInt(displayedResults.get(8).getQuestionAnswer()));			
			}
			else
			{// old survey with 8 questions
				rData.setRapa1(0);		
				rData.setRapa2(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
				rData.setRapa3(Integer.parseInt(displayedResults.get(1).getQuestionAnswer()));
				rData.setRapa4(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));
				rData.setRapa5(Integer.parseInt(displayedResults.get(3).getQuestionAnswer()));
				rData.setRapa6(Integer.parseInt(displayedResults.get(4).getQuestionAnswer()));
				rData.setRapa7(Integer.parseInt(displayedResults.get(5).getQuestionAnswer()));
				rData.setRapa8(Integer.parseInt(displayedResults.get(6).getQuestionAnswer()));
				rData.setRapa9(Integer.parseInt(displayedResults.get(7).getQuestionAnswer()));									
			}
		}		
	}
	
	private static void setAdvanceDirective(SurveyResult sr, ResearchData rData)
	{
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{			//if answer is empty, format it to 0
			displayedResults = formatEmptyResultAnswerToInt(displayedResults);			
			rData.setAd1(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
			rData.setAd2(Integer.parseInt(displayedResults.get(1).getQuestionAnswer()));
			rData.setAd3(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));							
		}
	}
	
	private static void setGeneralHealth(SurveyResult sr, ResearchData rData)
	{	
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{//if answer is empty, format it to 0
			displayedResults = formatEmptyResultAnswerToInt(displayedResults);
			
			rData.setGh1(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
			rData.setGh2(Integer.parseInt(displayedResults.get(1).getQuestionAnswer()));
			rData.setGh3(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));
			rData.setGh4(Integer.parseInt(displayedResults.get(3).getQuestionAnswer()));
			rData.setGh5(Integer.parseInt(displayedResults.get(4).getQuestionAnswer()));
			rData.setGh6(Integer.parseInt(displayedResults.get(5).getQuestionAnswer()));
			rData.setGh7(Integer.parseInt(displayedResults.get(6).getQuestionAnswer()));
			rData.setGh8(Integer.parseInt(displayedResults.get(7).getQuestionAnswer()));
			rData.setGh9(Integer.parseInt(displayedResults.get(8).getQuestionAnswer()));	
			rData.setGh10(Integer.parseInt(displayedResults.get(9).getQuestionAnswer()));
			rData.setGh11(Integer.parseInt(displayedResults.get(10).getQuestionAnswer()));
		}
	}
	
	private static List<DisplayedSurveyResult> getSurveyResults(SurveyResult sr)
	{
		String xml;
		try{
			xml = new String(sr.getResults(), "UTF-8");
	   	} catch (Exception e) {
	   		xml = "";
	   	}		
		return ResultParser.getDisplayedSurveyResults(ResultParser.getResults(xml));
	}
	
	private static void setMemory(SurveyResult sr, ResearchData rData)
	{
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{
			Map<String, String> mResults = mapSurveyResults(displayedResults);
			
			rData.setMem1(Integer.parseInt(mResults.get("YM1")));	
			if (mResults.get("YM1a") != null)
				rData.setMem2(mResults.get("YM1a"));
			rData.setMem3(Integer.parseInt(mResults.get("YM2")));
			if (mResults.get("YM2a") != null)
				rData.setMem4(mResults.get("YM2a"));
		}	
	}
	
	private static void  setMob2(int val, ResearchData rData){
		switch (val)
		{
			case 1: rData.setMob21(1);
					break;
			case 2: rData.setMob22(1);
					break;
			case 3: rData.setMob23(1);
					break;
			case 4: rData.setMob24(1);
					break;
			case 5: rData.setMob25(1);
					break;
			case 6: rData.setMob26(1);
					break;
		}
	}
	
	private static void setMob4(int val, ResearchData rData){
		switch (val)
		{
			case 1: rData.setMob41(1);
					break;
			case 2: rData.setMob42(1);
					break;
			case 3: rData.setMob43(1);
					break;
			case 4: rData.setMob44(1);
					break;
			case 5: rData.setMob45(1);
					break;
			case 6: rData.setMob46(1);
					break;
		}
	}
	
	private static void setMob6(int val, ResearchData rData){
		switch (val)
		{
			case 1: rData.setMob61(1);
					break;
			case 2: rData.setMob62(1);
					break;
			case 3: rData.setMob63(1);
					break;
			case 4: rData.setMob64(1);
					break;
			case 5: rData.setMob65(1);
					break;
			case 6: rData.setMob66(1);
					break;
		}
	}
	
	private static void setMobility(SurveyResult sr, ResearchData rData)
	{
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		List<Integer> aList;
		List<String> sList;
		
		if (!displayedResults.isEmpty())
		{
			//if answer is empty, format it to 0
			displayedResults = formatEmptyResultAnswerToInt(displayedResults);							
			String qId,answer;
			
			for (DisplayedSurveyResult dsr: displayedResults)
			{
				qId = dsr.getQuestionId();
				answer = dsr.getQuestionAnswer();
				
				if (qId.equals("a2a"))
					rData.setMob1(Integer.parseInt(answer));
				if (qId.equals("a2b"))
				{
					if (answer.contains(","))
					{		
						sList = new ArrayList<String>(Arrays.asList(answer.split(",")));		
						for (int j=0; j< sList.size(); j++)
						{			
							setMob2(Integer.valueOf(sList.get(j).trim()), rData);
						}
					}
					else
						setMob2(Integer.parseInt(answer), rData);
					
					if (rData.getMob21() == 0)
						rData.setMob21(888);
					if (rData.getMob22() == 0)
						rData.setMob22(888);
					if (rData.getMob23() == 0)
						rData.setMob23(888);
					if (rData.getMob24() == 0)
						rData.setMob24(888);
					if (rData.getMob25() == 0)
						rData.setMob25(888);
					if (rData.getMob26() == 0)
						rData.setMob26(888);
				}
				
				if (qId.equals("a3a"))
					rData.setMob3(Integer.parseInt(answer));
				if (qId.equals("a3b"))
				{
					if (answer.contains(","))
					{		
						sList = new ArrayList<String>(Arrays.asList(answer.split(",")));		
						for (int j=0; j< sList.size(); j++)
						{							
							setMob4(Integer.valueOf(sList.get(j).trim()), rData);
						}						
					}
					else
						setMob4(Integer.parseInt(answer), rData);
					
					if (rData.getMob41() == 0)
						rData.setMob41(888);
					if (rData.getMob42() == 0)
						rData.setMob42(888);
					if (rData.getMob43() == 0)
						rData.setMob43(888);
					if (rData.getMob44() == 0)
						rData.setMob44(888);
					if (rData.getMob45() == 0)
						rData.setMob45(888);
					if (rData.getMob46() == 0)
						rData.setMob46(888);
				}
				
				if (qId.equals("a4a"))
					rData.setMob5(Integer.parseInt(answer));
				if (qId.equals("a4b"))
				{
					if (answer.contains(","))
					{		
						sList = new ArrayList<String>(Arrays.asList(answer.split(",")));		
						for (int j=0; j< sList.size(); j++)
						{	
							setMob6(Integer.valueOf(sList.get(j).trim()), rData);
						}
					}
					else
						setMob6(Integer.parseInt(answer), rData);
					
					if (rData.getMob61() == 0)
						rData.setMob61(888);
					if (rData.getMob62() == 0)
						rData.setMob62(888);
					if (rData.getMob63() == 0)
						rData.setMob63(888);
					if (rData.getMob64() == 0)
						rData.setMob64(888);
					if (rData.getMob65() == 0)
						rData.setMob65(888);
					if (rData.getMob66() == 0)
						rData.setMob66(888);
				}						
			}
		}	
	}	
	
	private static void setNutrition(SurveyResult sr, ResearchData rData)
	{
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{//if answer is empty, format it to 0
			displayedResults = formatEmptyResultAnswerToInt(displayedResults);
			
			rData.setNut1(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
			rData.setNut2(Integer.parseInt(displayedResults.get(1).getQuestionAnswer()));
			rData.setNut3(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));
			rData.setNut4(Integer.parseInt(displayedResults.get(3).getQuestionAnswer()));
			rData.setNut5(Integer.parseInt(displayedResults.get(4).getQuestionAnswer()));
			rData.setNut6(Integer.parseInt(displayedResults.get(5).getQuestionAnswer()));
			rData.setNut7(Integer.parseInt(displayedResults.get(6).getQuestionAnswer()));
			rData.setNut8(Integer.parseInt(displayedResults.get(7).getQuestionAnswer()));
			rData.setNut9(Integer.parseInt(displayedResults.get(8).getQuestionAnswer()));	
			rData.setNut10(Integer.parseInt(displayedResults.get(9).getQuestionAnswer()));
			rData.setNut11(Integer.parseInt(displayedResults.get(10).getQuestionAnswer()));
			rData.setNut12(Integer.parseInt(displayedResults.get(11).getQuestionAnswer()));
			rData.setNut13(Integer.parseInt(displayedResults.get(12).getQuestionAnswer()));
			rData.setNut14(Integer.parseInt(displayedResults.get(13).getQuestionAnswer()));
			rData.setNut15(Integer.parseInt(displayedResults.get(14).getQuestionAnswer()));
			rData.setNut16(Integer.parseInt(displayedResults.get(15).getQuestionAnswer()));
			rData.setNut17(Integer.parseInt(displayedResults.get(16).getQuestionAnswer()));
		}
	}
	
	private static void set3MonthFollowUp(SurveyResult sr, ResearchData rData)
	{
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		
		if (!displayedResults.isEmpty())
		{
			Map<String, String> mResults = mapSurveyResults(displayedResults);
	
			if (mResults.get("CFQ1") != null)
			 rData.setFu1(Integer.parseInt(mResults.get("CFQ1")));
			
			if (mResults.get("CFQ2") != null)			
				rData.setFu2(Integer.valueOf(mResults.get("CFQ2")));
			rData.setFu3(mResults.get("CFQ3"));
			
			if (mResults.get("PHR1") != null)
				rData.setFu5(Integer.valueOf(mResults.get("PHR1")));
			
			if (mResults.get("PHR1a") != null)
				rData.setFu6(Integer.valueOf(mResults.get("PHR1a")));
			rData.setFu7(mResults.get("PHR1ao"));
			
			if (mResults.get("PHR1b") != null)
				rData.setFu8(Integer.valueOf(mResults.get("PHR1b")));
			rData.setFu9(mResults.get("PHR1bo"));
			
			if (mResults.get("GFU1") != null)
				rData.setFu11(Integer.valueOf(mResults.get("GFU1")));
			rData.setFu12(mResults.get("GFU2"));
			rData.setFu13(mResults.get("GFU3"));
			rData.setFu14(mResults.get("GFU4"));
			
			if (mResults.get("GFU5") != null)
				rData.setFu15(Integer.valueOf(mResults.get("GFU5")));
			rData.setFu16(mResults.get("GFU6"));
			rData.setFu17(mResults.get("GFU7"));
			rData.setFu18(mResults.get("GFU8"));
			
			if (mResults.get("GFU9") != null)
				rData.setFu19(Integer.valueOf(mResults.get("GFU9")));
			rData.setFu20(mResults.get("GFU10"));
			rData.setFu21(mResults.get("GFU11"));
			rData.setFu22(mResults.get("GFU12"));
		}
	}	
	private static void setDailyLife(SurveyResult sr, ResearchData rData)
	{		
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{
			Map<String, String> mResults = mapSurveyResults(displayedResults);			
			rData.setDla1(mResults.get("TV1"));
			rData.setDla2(mResults.get("TV2"));
			rData.setDla3(mResults.get("TV3"));
			rData.setDla4(mResults.get("TV4"));
			rData.setDla5(mResults.get("TV5"));
			rData.setDla6(mResults.get("TV6"));
			rData.setDla7(mResults.get("TV7"));
			rData.setDla7a(mResults.get("TV7a"));
		}		
	}
	public static List<ResearchData> getResearchDatas(PatientManager patientManager, SurveyManager surveyManager, int siteId)
	{
		List<Patient> patients = patientManager.getPatientsBySite(siteId);	
		SurveyResult sr = new SurveyResult();
		List<SurveyResult> srList = new ArrayList<SurveyResult>();
		List<ResearchData> researchDatas = new ArrayList<ResearchData>();
		ResearchData rData;
		int patientId;
			
		for (int i = 0; i < patients.size(); i++)		
		{			
			rData = new ResearchData();
			patientId = patients.get(i).getPatientID();		
			rData.setResearchId(patients.get(i).getResearchID());
			//Social life
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "4. Social Life");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setSocialLife(sr, rData);
			}			
			//EQ5D			
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "3. Quality of Life");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setQualityOfLife(sr, rData);
			}
			//Goals
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "Goals");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setGoals(sr, rData);
			}
			//RAPA
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "Physical Activity");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setRAPA(sr, rData);
			}
			//Advance directive
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "Advance Directives");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setAdvanceDirective(sr, rData);
			}			
			//memory			
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "Memory");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setMemory(sr, rData);
			}
			//General health			
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "General Health");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setGeneralHealth(sr, rData);
			}
			//Mobility
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "Mobility");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setMobility(sr, rData);
			}
			//Nutrition			
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "Nutrition");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setNutrition(sr, rData);
			}			
			//3 month follow up			
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "3 Month Follow Up");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				set3MonthFollowUp(sr, rData);
			}					
			//Daily Life Activity			
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "1. Daily Life Activities");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setDailyLife(sr, rData);
			}					
			researchDatas.add(rData);
		}		
		return researchDatas;
	}	
	private static void setUBCGoals(SurveyResult sr, UBCClientResearchData rData)
	{
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{
			rData.setGoal1(displayedResults.get(0).getQuestionAnswer());
			rData.setGoal2(displayedResults.get(1).getQuestionAnswer());
			rData.setGoal3(displayedResults.get(2).getQuestionAnswer());
			rData.setGoal4(displayedResults.get(3).getQuestionAnswer());
			rData.setGoal5(displayedResults.get(4).getQuestionAnswer());
		}		
	}	
	private static void setUBCQualityLife(SurveyResult sr, UBCClientResearchData rData)
	{
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{
			rData.setqOL1(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
			rData.setqOL2(Integer.parseInt(displayedResults.get(1).getQuestionAnswer()));
			rData.setqOL3(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));
			rData.setqOL4(Integer.parseInt(displayedResults.get(3).getQuestionAnswer()));
			rData.setqOL5(Integer.parseInt(displayedResults.get(4).getQuestionAnswer()));
			//in case last quesetion has no value
			if (Utils.isNullOrEmpty(displayedResults.get(5).getQuestionAnswer()))
					rData.setqOL6(999);
			else
				rData.setqOL6(Integer.parseInt(displayedResults.get(5).getQuestionAnswer()));
		}		
	}	
	private static void setUBCDailyLife(SurveyResult sr, UBCClientResearchData rData)
	{
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{
			rData.setdLA1(displayedResults.get(0).getQuestionAnswer());
			rData.setdLA2(displayedResults.get(1).getQuestionAnswer());
			rData.setdLA3(displayedResults.get(2).getQuestionAnswer());
			rData.setdLA4(displayedResults.get(3).getQuestionAnswer());
			rData.setdLA5(displayedResults.get(4).getQuestionAnswer());
			rData.setdLA6(displayedResults.get(5).getQuestionAnswer());
		}		
	}
	private static void setUBCGG(SurveyResult sr, UBCClientResearchData rData)
	{
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{
			if (Utils.isNullOrEmpty(displayedResults.get(0).getQuestionAnswer()))
				rData.setgH1(999);
			else
				rData.setgH1(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));	
		}		
	}
	private static void setUBCSocialLife(SurveyResult sr, UBCClientResearchData rData)
	{
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{
			rData.setsL1(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
			rData.setsL2(Integer.parseInt(displayedResults.get(1).getQuestionAnswer()));
			rData.setsL3(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));
			rData.setsL4(Integer.parseInt(displayedResults.get(3).getQuestionAnswer()));
			rData.setsL5(Integer.parseInt(displayedResults.get(4).getQuestionAnswer()));
			rData.setsL6(Integer.parseInt(displayedResults.get(5).getQuestionAnswer()));
			rData.setsL7(Integer.parseInt(displayedResults.get(6).getQuestionAnswer()));
			rData.setsL8(Integer.parseInt(displayedResults.get(7).getQuestionAnswer()));
			rData.setsL9(Integer.parseInt(displayedResults.get(8).getQuestionAnswer()));
			rData.setsL10(Integer.parseInt(displayedResults.get(9).getQuestionAnswer()));
			if (Utils.isNullOrEmpty(displayedResults.get(10).getQuestionAnswer()))
				rData.setsL11(999);
			else				
				rData.setsL11(Integer.parseInt(displayedResults.get(10).getQuestionAnswer()));
		}		
	}
	private static void setUBCNutrition(SurveyResult sr, UBCClientResearchData rData)
	{
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{
			rData.setNut1(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
			rData.setNut2(Integer.parseInt(displayedResults.get(1).getQuestionAnswer()));				
			//in case last question has no value 
			if (Utils.isNullOrEmpty(displayedResults.get(2).getQuestionAnswer()))
				rData.setNut3(999);
			else
				rData.setNut3(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));
		}		
	}
	private static void setUBCAdvanceDirective(SurveyResult sr, UBCClientResearchData rData)
	{
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{
			rData.setaCP1(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
			rData.setaCP2(Integer.parseInt(displayedResults.get(1).getQuestionAnswer()));
			rData.setaCP3(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));
			rData.setaCP4(Integer.parseInt(displayedResults.get(3).getQuestionAnswer()));
			rData.setaCP5(displayedResults.get(4).getQuestionAnswer());
		}		
	}
	private static void setUBCMemory(SurveyResult sr, UBCClientResearchData rData)
	{
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{
			Map<String, String> mResults = mapSurveyResults(displayedResults);
			
			rData.setMem1(Integer.parseInt(mResults.get("Mem1_worse")));	
			if (mResults.get("Mem2_explain") != null)
				rData.setMem2(mResults.get("Mem2_explain"));
			else
				rData.setMem2("");
			rData.setMem3(Integer.parseInt(mResults.get("Mem3_worry")));
			if (mResults.get("Mem4_whywor") != null)
				rData.setMem4(mResults.get("Mem4_whywor"));
			else
				rData.setMem4("");		
		}		
	}
	private static void setUBCPain(SurveyResult sr, UBCClientResearchData rData)
	{
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{
			if (Utils.isNullOrEmpty(displayedResults.get(0).getQuestionAnswer()))
				rData.setPain1(999);
			else
				rData.setPain1(Float.parseFloat(displayedResults.get(0).getQuestionAnswer()));		
		}		
	}
	private static void setUBCMobility(SurveyResult sr, UBCClientResearchData rData)
	{
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{
			rData.setMob1(displayedResults.get(0).getQuestionAnswer());		
			rData.setMob2(displayedResults.get(1).getQuestionAnswer());	
			rData.setMob3(displayedResults.get(2).getQuestionAnswer());
			rData.setMob4(Integer.parseInt(displayedResults.get(3).getQuestionAnswer()));		
			rData.setMob5(Integer.parseInt(displayedResults.get(4).getQuestionAnswer()));	
			rData.setMob6(Integer.parseInt(displayedResults.get(5).getQuestionAnswer()));	
			rData.setMob7(displayedResults.get(6).getQuestionAnswer());
			rData.setMob8(Integer.parseInt(displayedResults.get(7).getQuestionAnswer()));	
			rData.setMob9(displayedResults.get(8).getQuestionAnswer());	
		}		
	}
	private static void setUBCMedicationUse(SurveyResult sr, UBCClientResearchData rData)
	{
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
		{
			rData.setmU1(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
			rData.setmU2(displayedResults.get(1).getQuestionAnswer());
			rData.setmU3(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));
			rData.setmU4(Integer.parseInt(displayedResults.get(3).getQuestionAnswer()));
			if (Utils.isNullOrEmpty(displayedResults.get(4).getQuestionAnswer()))
				rData.setmU5(999);
			else
				rData.setmU5(Integer.parseInt(displayedResults.get(4).getQuestionAnswer()));
		}		
	}
	private static void setUBCLastQuestion(SurveyResult sr, UBCClientResearchData rData)
	{
		List<DisplayedSurveyResult> displayedResults = getSurveyResults(sr);
		if (!displayedResults.isEmpty())
			rData.setlQ(displayedResults.get(0).getQuestionAnswer());	
	}
	private static List<UBCClientResearchData> getUBCClientResearchDatas(List<Patient> patients, SurveyManager surveyManager){
		List<UBCClientResearchData> researchDatas = new ArrayList<UBCClientResearchData>();
		UBCClientResearchData rData;
		SurveyResult sr = new SurveyResult();
		int patientId;
		List<SurveyResult> srList = new ArrayList<SurveyResult>();
		Patient p;	
		
		for (int i = 0; i<patients.size(); i++ )
		{			
			p = patients.get(i);
			patientId = p.getPatientID();
			rData = new UBCClientResearchData();			
			rData.setResearchId(p.getResearchID());			
			
			//Goals
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "1. Goals");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setUBCGoals(sr, rData);
			}
			//EQ5D
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "2. Quality of Life");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setUBCQualityLife(sr, rData);
			}
			//Daily Life Activity
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "3. Daily Life Activities");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setUBCDailyLife(sr, rData);
			}			
			//General Health/4. Get Up & Go
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "4. Get Up & Go");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setUBCGG(sr, rData);
			}
			//Social life
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "5. Social Life");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setUBCSocialLife(sr, rData);
			}
			//Nutrition
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "6. Nutrition");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setUBCNutrition(sr, rData);
			}
			//Advance Care Planning
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "7. Advance Care Planning");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setUBCAdvanceDirective(sr, rData);
			}
			//Memory
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "8. Your Memory");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setUBCMemory(sr, rData);
			}
			//Pain
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "9. Pain");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setUBCPain(sr, rData);
			}
			//Mobility
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "10. Mobility");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setUBCMobility(sr, rData);
			}
			//Medication Use
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "11. Medication Use");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setUBCMedicationUse(sr, rData);
			}
			//Last Question
			srList = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "12. Last Question");
			if (srList.size()>0)
			{
				sr = srList.get(0);
				setUBCLastQuestion(sr, rData);
			}
			researchDatas.add(rData);
		}		
		return researchDatas;	
	}
	
	public static void downloadUBCClientDatas(List<Patient> patients, SurveyManager surveyManager, HttpServletResponse response ){
		//This data needs to be written (Object[])
        List<UBCClientResearchData> results = getUBCClientResearchDatas(patients, surveyManager);   
        //Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();         
        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("UBC Client Research Data");   
              
        Map<Integer, Object[]> data = new TreeMap<Integer, Object[]>();
        data.put(1, new Object[] {"Research_ID","Goal1_matter", "Goal2_stop", "Goal3_help", "Goal4_afraid", "Goal5_joy", "EQ5D1_Mobil", 
        		"EQ5D2_SelCar", "EQ5D3_UsAct", "EQ5D4_Pain", "EQ5D5_AnxDep", "EQ5D6_HeSt", "DLA1_TyDay", "DLA2_Counton", "DLA3_Cope", 
        		"DLA4_PressHe", "DLA5_Fall", "DLA6_Bowel", "GH11_TUG", "DSS1_role", "DSS2_under", "DSS3_useful", "DSS4_listen", 
        		"DSS5_happen", "DSS6_talk", "DSS7_satisfied", "DSS8_nofam", "DSS9_timesnotiving", "DSS10_timesphone", "DSS11_timesclubs", 
        		"Nut1_app", "Nut2_choke", "Nut3_enough", "ACD1_have", "ACD2_spoke", "ACD3_interest", "ACD4_import", "ACD5_decis", 
        		"Mem1_worse", "Mem2_explain", "Mem3_worry", "Mem4_whywor", "Pain1_level", "Mob1_leave", "Mob2_howfar", "Mob3_aid", 
        		"Mob4_difwalk", "Mob5_difclimb", "Mob6_difPA", "Mob7_limits", "Mob8_PA", "Mob9_PADes", "Med1_preswhat", "Med2_healthcon", 
        		"Med3_famwhat", "Med4_sideff", "Med5_conmed", "LastQue"});
        UBCClientResearchData r;
        for (int i=0; i< results.size(); i++)
        {        	
        	r = results.get(i);      	
        	data.put(Integer.valueOf(i+2), new Object[]{r.getResearchId(), r.getGoal1(), r.getGoal2(), r.getGoal3(), r.getGoal4(), 
        		r.getGoal5(), r.getqOL1(), r.getqOL2(), r.getqOL3(), r.getqOL4(), r.getqOL5(), r.getqOL6(), r.getdLA1(), r.getdLA2(),
        		r.getdLA3(), r.getdLA4(), r.getdLA5(), r.getdLA6(), r.getgH1(), r.getsL1(), r.getsL2(), r.getsL3(), r.getsL4(), 
        		r.getsL5(), r.getsL6(), r.getsL7(), r.getsL8(), r.getsL9(), r.getsL10(), r.getsL11(), r.getNut1(), r.getNut2(), 
        		r.getNut3(), r.getaCP1(), r.getaCP2(), r.getaCP3(), r.getaCP4(), r.getaCP5(), r.getMem1(), r.getMem2(), r.getMem3(), 
        		r.getMem4() ,r.getPain1(), r.getMob1(), r.getMob2(), r.getMob3(), r.getMob4(), r.getMob5(), r.getMob6(), r.getMob7(),
        		r.getMob8(), r.getMob9() ,r.getmU1(), r.getmU2(),r.getmU3(), r.getmU4(), r.getmU5(), r.getlQ()});
        } 
        fillSheet(data, sheet, workbook);
        
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"UBC_client_results.xlsx\"");     
        try{// Write workbook to response.
            workbook.write(response.getOutputStream()); 
            response.getOutputStream().close();
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
	}
		
	public static List<DisplayedSurveyResult> formatEmptyResultAnswerToInt(List<DisplayedSurveyResult> results)
	{
		for (int i =0; i<results.size(); i++)
		{
			if (Utils.isNullOrEmpty(results.get(i).getQuestionAnswer()))
				results.get(i).setQuestionAnswer("000");
		}		
		return results;
	}
	
	public static DisplayedSurveyResult formatEmptyResultAnswerToInt(DisplayedSurveyResult result)
	{
		if (Utils.isNullOrEmpty(result.getQuestionAnswer()))
			result.setQuestionAnswer("000");		
		return result;
	}	
	
	private static List<Integer> makeMobilityMultipleAnswerList(String answer)
	{
		List<String> sList = new ArrayList<String>(Arrays.asList(answer.split(",")));			
		List<Integer> iList = new ArrayList<Integer>();
		int size = sList.size();
		
		for (int i=0; i<size; i++)
			iList.add(Integer.valueOf(sList.get(i).trim()));
		 
		 while (size < 6)
		 {
			 iList.add(0);
			 size++;
		 }		 
		 return iList;
	}
	
	public static List<Site> getSites(SecurityContextHolderAwareRequestWrapper request, OrganizationManager organizationManager)
	{
		HttpSession session = request.getSession();
		List<Site> sites = new ArrayList<Site>();
		
		if (session.getAttribute("sites") != null)
			sites = (List<Site>)session.getAttribute("sites");
		else
		{
			sites = organizationManager.getAllSites();
			session.setAttribute("sites", sites);
		}		
		return sites;
	}
	//============================== Report ===================================
	public static void generateVolunteerSurveyReport(int volunteerId, SurveyManager surveyManager, 
			HttpServletResponse response, String name )
	{	
		String xml;
		List<String> qList;
   		List<String> questionTextList;
   		LinkedHashMap<String, String> mSurvey;
		Map<String, String> surveyResultMap = new LinkedHashMap<String, String>();
		//Survey---  		
		List<SurveyResult> surveyResultList = surveyManager.getCompletedVolunteerSurveys(volunteerId);
		if (surveyResultList.size() == 0)
		{
			buildEmptyReport(response, name);
			return;
		}
		SurveyResult sr;
		String qText;
		for (int i = 0; i < surveyResultList.size(); i++)
		{
			sr = new SurveyResult();
			sr = surveyResultList.get(i);
			String title = sr.getSurveyTitle();
			surveyResultMap.put("SurveyTitle " + (i+1), title);
			try{
	   			xml = new String(sr.getResults(), "UTF-8");
	   		} catch (Exception e) {
	   			xml = "";
	   		}
			mSurvey = ResultParser.getResults(xml);
				
			qList = new ArrayList<String>();
			qList = TapestryHelper.getQuestionList(mSurvey);
	   		questionTextList = new ArrayList<String>();	   		
	   		questionTextList = ResultParser.getSurveyQuestions(xml);  
	   		
	   		questionTextList.remove(0);
	   			   		
	   		int qSize = questionTextList.size();
	   		for (int j=0; j<qSize; j++)
	   		{
	   			qText = questionTextList.get(j);
	   			if (qText.contains("Press NEXT to save"))
	   			{
	   				questionTextList.remove(j);
	   				break;
	   			}
	   			qText = removeObserverNotes(qText);
	   			questionTextList.set(j,qText);	 
	   		}
	   		qSize = questionTextList.size();
	   		if (qSize == qList.size())
	   		{
	   			for (int m=0; m<qSize; m++)
		   		{
	   				surveyResultMap.put(questionTextList.get(m), qList.get(m));
		   		}
	   		}
	   		else
	   			System.out.println("Please check survey result, number of question text is not match with answer");	
		}
		buildPDFReport(surveyResultMap, response, name);
	}
	
	static void buildEmptyReport(HttpServletResponse response, String displayName)
	{
		String orignalFileName= displayName +"_report.pdf";
		try {
			Document document = new Document();
			document.setPageSize(PageSize.A4);
			document.setMargins(36, 36, 60, 36);
			document.setMarginMirroring(true);
			response.setHeader("Content-Disposition", "outline;filename=\"" +orignalFileName+ "\"");
			PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
			//Font setup		
			Font mFont = new Font(Font.FontFamily.HELVETICA, 12);		
			Font blFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);	
				
			document.open(); 
			//Volunteer info
			PdfPTable table = new PdfPTable(2);
			table.setWidthPercentage(100);
			table.setWidths(new float[]{1f, 2f});
			
			PdfPCell cell = new PdfPCell(new Phrase("TAPESTRY REPORT: " + displayName, blFont));	
			cell.setPadding(5);
			cell.setColspan(2);
			table.addCell(cell);			
			document.add(table);	
			
			table = new PdfPTable(1);
   			table.setWidthPercentage(100);
   			cell = new PdfPCell(new Phrase("No Data", mFont));
   			table.addCell(cell);	
   			document.add(table);
	   	
			document.close();
		} catch (Exception e) {
			e.printStackTrace();			
		}				
	}
	public static void buildPDFReport(Map report, HttpServletResponse response, String displayName)
	{				
		String orignalFileName= displayName +"_report.pdf";
		String key, value;
		try {
			Document document = new Document();
			document.setPageSize(PageSize.A4);
			document.setMargins(36, 36, 60, 36);
			document.setMarginMirroring(true);
			response.setHeader("Content-Disposition", "outline;filename=\"" +orignalFileName+ "\"");
			PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
			//Font setup		
			Font mFont = new Font(Font.FontFamily.HELVETICA, 12);		
			Font blFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);	
			//white font			
			Font wbLargeFont = new Font(Font.FontFamily.HELVETICA  , 20, Font.BOLD);
			wbLargeFont.setColor(BaseColor.WHITE);
			Font wMediumFont = new Font(Font.FontFamily.HELVETICA , 16, Font.BOLD);
			wMediumFont.setColor(BaseColor.WHITE);
			
			document.open(); 
			//Volunteer info
			PdfPTable table = new PdfPTable(2);
			table.setWidthPercentage(100);
			table.setWidths(new float[]{1f, 2f});
			
			PdfPCell cell = new PdfPCell(new Phrase("TAPESTRY REPORT: " + displayName, blFont));	
			cell.setPadding(5);
			cell.setColspan(2);
			table.addCell(cell);
			
			document.add(table);	
			
	   		Iterator iterator = report.entrySet().iterator();
	   		while (iterator.hasNext()) {
	   			Map.Entry mapEntry = (Map.Entry) iterator.next();
	   			
	   			key = mapEntry.getKey().toString();
	   			value = mapEntry.getValue().toString();
	   			
	   			table = new PdfPTable(2);
	   			table.setWidthPercentage(100);
	   			if (key.startsWith("SurveyTitle "))
	   			{
	   				cell = new PdfPCell(new Phrase(value, wbLargeFont));
	   				cell.setBackgroundColor(BaseColor.BLACK);	   
	   				cell.setColspan(2);
	   				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	   				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
	   				cell.setPaddingBottom(5);
		   			table.addCell(cell);
	   			}
	   			else
	   			{
	   				cell = new PdfPCell(new Phrase(key, mFont));		            	
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPaddingBottom(5);
					table.addCell(cell);	            	
		            	
					cell = new PdfPCell(new Phrase(value, mFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPaddingBottom(5);
					table.addCell(cell); 
	   			}		 
	   			document.add(table);
	   		}
			document.close();
		} catch (Exception e) {
			e.printStackTrace();			
		}				
	}

	public static void generateClientSurveyReport(int patientId, SurveyManager surveyManager, 
			HttpServletResponse response, String name, int site )
	{
		String xml;
   		LinkedHashMap<String, String> mSurvey;	
		Map<String, String> tMap = new LinkedHashMap<String, String>(); 		
		List<SurveyResult> surveyResultList = surveyManager.getCompletedSurveysByPatientID(patientId);	
		
		if (surveyResultList.size() == 0)
		{
			buildEmptyReport(response, name);
			return;
		}
		SurveyResult sr;
		DisplayedSurveyResult dsr;
					
		for (int i = 0; i < surveyResultList.size(); i++)
		{
			sr = new SurveyResult();
			sr = surveyResultList.get(i);
			String title = sr.getSurveyTitle();
			tMap.put("SurveyTitle " + (i+1), title);
			try{
	   			xml = new String(sr.getResults(), "UTF-8");
	   		} catch (Exception e) {
	   			xml = "";
	   		}
			mSurvey = ResultParser.getResults(xml);
							
			List<DisplayedSurveyResult> displayedResults = ResultParser.getDisplayedSurveyResults(mSurvey);
	   		
			if (site==1) //McMaster
				displayedResults = getDetailedAnswerForSurvey(displayedResults, "mainSurveys.properties");			
			else //McGill
				displayedResults = getDetailedAnswerForSurvey(displayedResults, "mgSurveys.properties");
			
			for (int j =0; j<displayedResults.size(); j++)
			{
				dsr = new DisplayedSurveyResult();
				dsr = displayedResults.get(j);	
				
				tMap.put(dsr.getQuestionText(), dsr.getQuestionAnswer());
			}
		}	
		buildPDFReport(tMap, response, name);
	}
	static void buildPDFUBCReport(Map tMap, HttpServletResponse response, String displayName, int sScore, int nScore){
		String orignalFileName= displayName +"_report.pdf";
		String key, value;
		try {
			Document document = new Document();
			document.setPageSize(PageSize.A4);
			document.setMargins(36, 36, 60, 36);
			document.setMarginMirroring(true);
			response.setHeader("Content-Disposition", "outline;filename=\"" +orignalFileName+ "\"");
			PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
			//Font setup		
			Font mFont = new Font(Font.FontFamily.HELVETICA, 12);		
			Font blFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
			Font sFont = new Font(Font.FontFamily.HELVETICA, 9);	
			Font imFont = new Font(Font.FontFamily.HELVETICA , 12, Font.ITALIC );		
			//white font			
			Font wbLargeFont = new Font(Font.FontFamily.HELVETICA  , 20, Font.BOLD);
			wbLargeFont.setColor(BaseColor.WHITE);
			Font wMediumFont = new Font(Font.FontFamily.HELVETICA , 16, Font.BOLD);
			wMediumFont.setColor(BaseColor.WHITE);
			
			document.open(); 
			
			PdfPTable table = new PdfPTable(2);
			table.setWidthPercentage(110);			
			table.setWidths(new float[]{1f, 2f});
			
			PdfPCell cell = new PdfPCell(new Phrase("TAPESTRY REPORT: " + displayName, blFont));	
			cell.setPadding(5);
			cell.setColspan(2);
			table.addCell(cell);			
			document.add(table);
			////
			//Sumamary 
			table = new PdfPTable(3);
			table.setWidthPercentage(110);
			table.setWidths(new float[]{1.2f, 2f, 2f});
			cell = new PdfPCell(new Phrase("Summary of TAPESTRY Tools", wbLargeFont));
			cell.setBackgroundColor(BaseColor.GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			cell.setColspan(3);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("DOMAIN", wMediumFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("SCORE", wMediumFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("DESCRIPTION", wMediumFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			table.addCell(cell);
			            
			cell = new PdfPCell(new Phrase("Social Support", mFont));	      
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setMinimumHeight(55f);
			table.addCell(cell);            
	           
			StringBuffer sb = new StringBuffer();
			sb.append("Satisfaction score =  ");
			if (sScore == 0)
				sb.append("No Data");
			else
				sb.append(sScore);
			sb.append("\n");	
			sb.append("Network score = ");
			if (nScore == 0)
				sb.append("No Data");
			else
				sb.append(nScore);
			sb.append("\n");	            
			sb.append("\n");
	            
			cell = new PdfPCell(new Phrase(sb.toString(), imFont));
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			Phrase p = new Phrase();	   
			Chunk underline = new Chunk("Duke Social Support Index", mFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	  
			p.add(underline); 	     
			p.add(new Chunk("\n"));
			p.add(new Chunk("(Score < 10 risk cut off), ranges from 6-18", sFont));
	            
			sb = new StringBuffer();
			sb.append(" ");	            	            
			sb.append("\n");
			sb.append("Perceived satisfaction with behavioural or");
			sb.append("\n");
			sb.append("emotional support obtained from this network");
			sb.append("\n");
			p.add(new Chunk(sb.toString(), sFont));
	            
			underline = new Chunk("Network score range : 4-12", sFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location
			p.add(underline);
	            	            
			sb = new StringBuffer();
			sb.append("\n");
			sb.append("Size and structure of social network");
			sb.append("\n");	
			p.add(new Chunk(sb.toString(), sFont));
	            
			cell = new PdfPCell(p);
			cell.setNoWrap(false);		
			table.addCell(cell);	                  
			document.add(table);
			
	   		Iterator iterator = tMap.entrySet().iterator();
	   		while (iterator.hasNext()) {
	   			Map.Entry mapEntry = (Map.Entry) iterator.next();
	   			
	   			key = mapEntry.getKey().toString();
	   			value = mapEntry.getValue().toString();
	   			
	   			table = new PdfPTable(2);
	   			table.setWidthPercentage(110);
	   			if (key.startsWith("SurveyTitle "))
	   			{
	   				cell = new PdfPCell(new Phrase(value, wbLargeFont));
	   				cell.setBackgroundColor(BaseColor.BLACK);	   
	   				cell.setColspan(2);
	   				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	   				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
	   				cell.setPaddingBottom(5);
		   			table.addCell(cell);
	   			}
	   			else
	   			{
	   				cell = new PdfPCell(new Phrase(key, mFont));		            	
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPaddingBottom(5);
					table.addCell(cell);	            	
		            	
					cell = new PdfPCell(new Phrase(value, mFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPaddingBottom(5);
					table.addCell(cell); 
	   			}		 
	   			document.add(table);
	   		}	   		
			document.close();
		} catch (Exception e) {
			e.printStackTrace();			
		}	
	}

	public static void generateClientReportForUBC(int patientId, SurveyManager surveyManager, 
			HttpServletResponse response, String name, boolean hasObservernotes)
	{
		String xml, socialLifeTitle="";
		List<String> qList = new ArrayList<String>();
   		LinkedHashMap<String, String> mSurvey;	
   		List<DisplayedSurveyResult> displayedResults;
		Map<String, String> tMap = new LinkedHashMap<String, String>(); 		
		List<SurveyResult> surveyResultList = surveyManager.getCompletedSurveysByPatientID(patientId);	
		if (surveyResultList.size() == 0)
		{
			buildEmptyReport(response, name);
			return;
		}
		SurveyResult sr;
		int satisfactionScore=0,  networkScore=0;
		//get social life survey title from properties file
		try{
			socialLifeTitle = getProperties("ubcSurveys.properties", "socialLife_survey");
		}catch (Exception e)
		{
			System.out.println("===========Has problem to read ubcSurveys.properties file============");
		}		
		for (int i = 0; i < surveyResultList.size(); i++)
		{
			sr = new SurveyResult();
			sr = surveyResultList.get(i);
			String title = sr.getSurveyTitle();
			tMap.put("SurveyTitle " + (i+1), title);
			try{
	   			xml = new String(sr.getResults(), "UTF-8");
	   		} catch (Exception e) {
	   			xml = "";
	   		}
			mSurvey = ResultParser.getResults(xml);
			
			if (title.equals(socialLifeTitle))
				qList = getQuestionList(mSurvey);
				
			displayedResults = ResultParser.getDisplayedSurveyResults(mSurvey);	   		
			displayedResults = getDetailedAnswerForUBCSurveys(displayedResults);			
			DisplayedSurveyResult dsr;
			for (int j =0; j<displayedResults.size(); j++)
			{
				dsr = new DisplayedSurveyResult();
				dsr = displayedResults.get(j);	
				
				if (hasObservernotes)
				{
					tMap.put(dsr.getQuestionText(), dsr.getQuestionAnswer()+ "||"+dsr.getObserverNotes());
					System.out.println("value= "+ dsr.getQuestionAnswer()+ "||"+dsr.getObserverNotes());
				}
				else
					tMap.put(dsr.getQuestionText(), dsr.getQuestionAnswer());
			}
		}		
		if (qList != null && qList.size()>0)
		{
			int socialSupportSize = qList.size();
			if ((qList != null)&&(socialSupportSize>0))
			{
				satisfactionScore = CalculationManager.getScoreByQuestionsList(qList.subList(0, 6));				
				networkScore = CalculationManager.getSocialSupportNetworkScore(qList.subList(7, socialSupportSize));
			}
		}
		if (hasObservernotes)
			buildPDFUBCReport(tMap, response, name, satisfactionScore, networkScore, true);
		else
			buildPDFUBCReport(tMap, response, name, satisfactionScore, networkScore, false);
	}
	
	static void buildPDFUBCReport(Map tMap, HttpServletResponse response, String displayName, int sScore, int nScore, boolean hasObservernotes)
	{
		String orignalFileName;
		if(hasObservernotes)
			orignalFileName= displayName +"_withObserverNotes_report.pdf";
		else
			orignalFileName= displayName +"_report.pdf";
		
		String key, value;
		try {
			Document document = new Document();
			document.setPageSize(PageSize.A4);
			document.setMargins(36, 36, 60, 36);
			document.setMarginMirroring(true);
			response.setHeader("Content-Disposition", "outline;filename=\"" +orignalFileName+ "\"");
			PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
			//Font setup		
			Font mFont = new Font(Font.FontFamily.HELVETICA, 12);		
			Font blFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
			Font sFont = new Font(Font.FontFamily.HELVETICA, 9);	
			Font imFont = new Font(Font.FontFamily.HELVETICA , 12, Font.ITALIC );		
			//white font			
			Font wbLargeFont = new Font(Font.FontFamily.HELVETICA  , 20, Font.BOLD);
			wbLargeFont.setColor(BaseColor.WHITE);
			Font wMediumFont = new Font(Font.FontFamily.HELVETICA , 16, Font.BOLD);
			wMediumFont.setColor(BaseColor.WHITE);
			
			document.open(); 
			
			PdfPTable table = new PdfPTable(2);
			table.setWidthPercentage(110);			
			table.setWidths(new float[]{1f, 2f});
			
			PdfPCell cell = new PdfPCell(new Phrase("TAPESTRY REPORT: " + displayName, blFont));	
			cell.setPadding(5);
			cell.setColspan(2);
			table.addCell(cell);			
			document.add(table);			
			//Sumamary 
			table = new PdfPTable(3);
			table.setWidthPercentage(110);
			table.setWidths(new float[]{1.2f, 2f, 2f});
			cell = new PdfPCell(new Phrase("Summary of TAPESTRY Tools", wbLargeFont));
			cell.setBackgroundColor(BaseColor.GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			cell.setColspan(3);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("DOMAIN", wMediumFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("SCORE", wMediumFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("DESCRIPTION", wMediumFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			table.addCell(cell);
			            
			cell = new PdfPCell(new Phrase("Social Support", mFont));     
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setMinimumHeight(55f);
			table.addCell(cell);            
	    		
			StringBuffer sb = new StringBuffer();
			sb.append("Satisfaction score =  ");
			if (sScore == 0)
				sb.append("No Data");
			else
				sb.append(sScore);
			sb.append("\n");	
			sb.append("Network score = ");
			if (nScore == 0)
				sb.append("No Data");
			else
				sb.append(nScore);
			sb.append("\n");	            
			sb.append("\n");
	            
			cell = new PdfPCell(new Phrase(sb.toString(), imFont));
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			Phrase p = new Phrase();	   
			Chunk underline = new Chunk("Duke Social Support Index", mFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	  
			p.add(underline); 	     
			p.add(new Chunk("\n"));
			p.add(new Chunk("(Score < 10 risk cut off), ranges from 6-18", sFont));
	            
			sb = new StringBuffer();
			sb.append(" ");	            	            
			sb.append("\n");
			sb.append("Perceived satisfaction with behavioural or");
			sb.append("\n");
			sb.append("emotional support obtained from this network");
			sb.append("\n");
			p.add(new Chunk(sb.toString(), sFont));
	            
			underline = new Chunk("Network score range : 4-12", sFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location
			p.add(underline);
	            	            
			sb = new StringBuffer();
			sb.append("\n");
			sb.append("Size and structure of social network");
			sb.append("\n");	
			p.add(new Chunk(sb.toString(), sFont));
	            
			cell = new PdfPCell(p);
			cell.setNoWrap(false);		
			table.addCell(cell);	                  
			document.add(table);
			///
			String answer, observernotes;
			int index;
	   		Iterator iterator = tMap.entrySet().iterator();
	   		while (iterator.hasNext()) {
	   			Map.Entry mapEntry = (Map.Entry) iterator.next();
	   			
	   			key = mapEntry.getKey().toString();
	   			value = mapEntry.getValue().toString();
	   			if(hasObservernotes)
	   			{
	   				table = new PdfPTable(3);
	   				table.setWidthPercentage(110);
		   			if (key.startsWith("SurveyTitle "))
		   			{
		   				cell = new PdfPCell(new Phrase(value, wbLargeFont));
		   				cell.setBackgroundColor(BaseColor.BLACK);	   
		   				cell.setColspan(3);
		   				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		   				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
		   				cell.setPaddingBottom(5);
			   			table.addCell(cell);
		   			}
		   			else
		   			{
		   				index = value.indexOf("||");
		   				answer = value.substring(0, index);
		   				observernotes = value.substring(index +2);
		   				if (Utils.isNullOrEmpty(observernotes))
		   					observernotes = "No Observer notes";
		   				cell = new PdfPCell(new Phrase(key, mFont));		            	
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setPaddingBottom(5);
						table.addCell(cell);
						
		   				cell = new PdfPCell(new Phrase(answer, mFont));		            	
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setPaddingBottom(5);
						table.addCell(cell);	            	
			            	
						cell = new PdfPCell(new Phrase(observernotes, mFont));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setPaddingBottom(5);
						table.addCell(cell); 
		   			}	
	   			}
	   			else
	   			{
	   				table = new PdfPTable(2);
	   				table.setWidthPercentage(110);
		   			if (key.startsWith("SurveyTitle "))
		   			{
		   				cell = new PdfPCell(new Phrase(value, wbLargeFont));
		   				cell.setBackgroundColor(BaseColor.BLACK);	   
		   				cell.setColspan(2);
		   				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		   				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
		   				cell.setPaddingBottom(5);
			   			table.addCell(cell);
		   			}
		   			else
		   			{
		   				cell = new PdfPCell(new Phrase(key, mFont));		            	
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setPaddingBottom(5);
						table.addCell(cell);	            	
			            	
						cell = new PdfPCell(new Phrase(value, mFont));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setPaddingBottom(5);
						table.addCell(cell); 
		   			}	
	   			}	   				 
	   			document.add(table);}
			document.close();
		} catch (Exception e) {
			e.printStackTrace();			
		}			
	}
	
	public static LinkedHashMap<String, String> getSurveyResultMap(SurveyResult sr){
		String xml;
		try{
   			xml = new String(sr.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}   		
   		return ResultParser.getResults(xml);
	}	
	
	public static String removeString(String strSource, String strBeRemoved){		
		int index = strSource.indexOf(strBeRemoved);
		if (index != (-1))
			strSource = strSource.substring(0, index); 
		return strSource;
	}
	
	public static String replaceString(String strSource, String strBeReplaced)
	{		
		if (strSource.indexOf(strBeReplaced) != (-1))
			strSource = strSource.replaceAll(strBeReplaced, ""); 
				
		return strSource;
	}
	
	private static String replaceString(String strSource, String strBeReplaced, String placeHolder)
	{		
		if (strSource.indexOf(strBeReplaced) != (-1))
			strSource = strSource.replaceAll(strBeReplaced, placeHolder); 				
		return strSource;
	}
	
	private static String trimGoalMsg(String goalsMsg)
	{
		if (goalsMsg.contains("-------<br>"))
			goalsMsg = replaceString(goalsMsg, "-------<br>", ",");
		if (goalsMsg.contains("<br>"))
			goalsMsg = replaceString(goalsMsg, "<br>", ",");		
		return goalsMsg;
	}
	
	public void readProperties() throws Exception{			      
		Properties props = new Properties();
		try{
			props.load(TapestryHelper.class.getClassLoader().getResourceAsStream("tapestry.properties"));
			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static String getProperties(String key) throws Exception{
		Properties props = new Properties();
		String value = "";
		try{
			props.load(TapestryHelper.class.getClassLoader().getResourceAsStream("tapestry.properties"));
			value = props.getProperty(key);			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		return value;
	}
	
	public static String getProperties(String file, String key) throws Exception{
		Properties props = new Properties();
		String value = "";
		try{
			props.load(TapestryHelper.class.getClassLoader().getResourceAsStream(file));
			value = props.getProperty(key);			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		return value;
	}	
	
	public static void setProperties(String file, String key, String value) throws Exception{		
		Properties props = new Properties();		
		String dir = "webapp/WEB-INF/classes/" + file;	
		String dir1 ="/var/lib/tomcat7/webapps/tapestry/" + file;
		try{
			props.setProperty(key, value);		
			props.store(new FileOutputStream(dir1), "it works!");	
			
		}catch(IOException e)
		{
			e.printStackTrace();
		}	
	}
	
	public static List<Patient> getPatientsByPartialName(List<Patient> patients, String partialName)
	{
		List<Patient> pList = new ArrayList<Patient>();
		Patient p;
		for (int i = 0; i < patients.size(); i++)
		{
			p = patients.get(i);
			if (p.getFirstName().contains(partialName)||p.getLastName().contains(partialName))
				pList.add(p);			
		}		
		return pList;
	}
	
	public static List<DisplayedSurveyResult> getDetailedAnswerForUBCSurveys(List<DisplayedSurveyResult> displayedResults)
	{
		String qId, answer, key;
		for (int i = 0; i< displayedResults.size(); i++)
		{
			qId = displayedResults.get(i).getQuestionId();

			//caregiver background and caregiver follow up have same questionIds, have to make them different for reading value from properties file
			if (displayedResults.get(i).getTitle().equals("Caregiver_FollowUp"))
				qId = qId.replace("B","F");

			answer = displayedResults.get(i).getQuestionAnswer();
			key = qId+answer;
			try{
				answer = TapestryHelper.getProperties("ubcSurveys.properties", key);
									
				if (!Utils.isNullOrEmpty(answer))
					displayedResults.get(i).setQuestionAnswer(answer);
			}catch (Exception e)
			{
				System.out.println("===========Has problem to read ubcSurveys.properties file============");
			}
		}		
		return displayedResults;
	}
	
	public static List<DisplayedSurveyResult> getDetailedAnswerForSurveys(List<DisplayedSurveyResult> displayedResults, int site)
	{
		List<DisplayedSurveyResult> sList = new ArrayList<DisplayedSurveyResult>();
		switch (site){
			case 1: sList = getDetailedAnswerForSurvey(displayedResults, "mainSurveys.properties"); //McMaster
					break;
			case 2: sList = getDetailedAnswerForSurvey(displayedResults, "mgSurveys.properties"); //McGill
					break;
			case 3: sList = getDetailedAnswerForUBCSurveys(displayedResults); //UBC
					break;				
		}
		return sList;
	}
	
	static List<DisplayedSurveyResult> getDetailedAnswerForSurvey(List<DisplayedSurveyResult> displayedResults, String propertyFile)
	{
		String qId, answer, key, key1, qText;
		DisplayedSurveyResult sr;
		for (int i = 0; i< displayedResults.size(); i++)
		{
			sr = displayedResults.get(i);			
			qId = sr.getQuestionId();			
			answer = sr.getQuestionAnswer();	
			
			if (answer.contains(",") && Utils.onlyDigitInString(answer.replaceAll(", ", "")))			
			{
				String[] ans = answer.split(",");
				StringBuffer sb = new StringBuffer();
				
				int length = ans.length;
				for(int j=0; j< length; j++)
				{
					key = qId + ans[j].trim();
					
					try{
						answer = TapestryHelper.getProperties(propertyFile, key);
						sb.append(answer);
						
						if (j < length-1)
							sb.append("<br>");
					}catch (Exception e)
					{
						System.out.println("===========Has problem to read " + propertyFile + " file============");
					}
				}				
				if (!Utils.isNullOrEmpty(sb.toString()))
					sr.setQuestionAnswer(sb.toString());
			}
			else
			{
				key = qId+answer;
				try{
					answer = TapestryHelper.getProperties(propertyFile, key);
										
					if (!Utils.isNullOrEmpty(answer))
						sr.setQuestionAnswer(answer);
					
					if (propertyFile.contains("mainSurveys.properties")&&(sr.getTitle().contains("3 month follow up")))
					{
						key1 = qId + "QuestionText";					
						qText = TapestryHelper.getProperties(propertyFile, key1);
						
						if (!Utils.isNullOrEmpty(qText))
							sr.setQuestionText(qText);
					}
				}catch (Exception e)
				{
					System.out.println("===========Has problem to read " + propertyFile + " file============");
				}
			}			
		}		
		return displayedResults;
	}
	static void buildSummaryInReport(Document document, int sScore, int nScore)
	{
		try{
		Font sFont = new Font(Font.FontFamily.HELVETICA, 9);	
		Font imFont = new Font(Font.FontFamily.HELVETICA , 12, Font.ITALIC );		
		Font mFont = new Font(Font.FontFamily.HELVETICA, 12);	
		Font wbLargeFont = new Font(Font.FontFamily.HELVETICA  , 20, Font.BOLD);
		wbLargeFont.setColor(BaseColor.WHITE);
		Font wMediumFont = new Font(Font.FontFamily.HELVETICA , 16, Font.BOLD);
		wMediumFont.setColor(BaseColor.WHITE);	
		
		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(110);
		table.setWidths(new float[]{1.2f, 2f, 2f});
		PdfPCell cell = new PdfPCell(new Phrase("Summary of TAPESTRY Tools", wbLargeFont));
		cell.setBackgroundColor(BaseColor.GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setFixedHeight(28f);
		cell.setColspan(3);
		table.addCell(cell);
            
		cell = new PdfPCell(new Phrase("DOMAIN", wMediumFont));
		cell.setBackgroundColor(BaseColor.BLACK);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setFixedHeight(28f);
		table.addCell(cell);
            
		cell = new PdfPCell(new Phrase("SCORE", wMediumFont));
		cell.setBackgroundColor(BaseColor.BLACK);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setFixedHeight(28f);
		table.addCell(cell);
            
		cell = new PdfPCell(new Phrase("DESCRIPTION", wMediumFont));
		cell.setBackgroundColor(BaseColor.BLACK);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setFixedHeight(28f);
		table.addCell(cell);
		            
		cell = new PdfPCell(new Phrase("Social Support", mFont));
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	      
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setMinimumHeight(55f);
		table.addCell(cell);            
           
		StringBuffer sb = new StringBuffer();
		sb.append("Satisfaction score =  ");
		sb.append(sScore);
		sb.append("\n");	
		sb.append("Network score = ");
		sb.append(nScore);
		sb.append("\n");	            
		sb.append("\n");
            
		cell = new PdfPCell(new Phrase(sb.toString(), imFont));
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
		cell.setNoWrap(false);
		table.addCell(cell);
            
		Phrase p = new Phrase();	   
		Chunk underline = new Chunk("Duke Social Support Index", mFont);
		underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	  
		p.add(underline); 	     
		p.add(new Chunk("\n"));
		p.add(new Chunk("(Score < 10 risk cut off), ranges from 6-18", sFont));
            
		sb = new StringBuffer();
		sb.append(" ");	            	            
		sb.append("\n");
		sb.append("Perceived satisfaction with behavioural or");
		sb.append("\n");
		sb.append("emotional support obtained from this network");
		sb.append("\n");
		p.add(new Chunk(sb.toString(), sFont));
            
		underline = new Chunk("Network score range : 4-12", sFont);
		underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location
		p.add(underline);
            	            
		sb = new StringBuffer();
		sb.append("\n");
		sb.append("Size and structure of social network");
		sb.append("\n");	
		p.add(new Chunk(sb.toString(), sFont));
            
		cell = new PdfPCell(p);
		cell.setNoWrap(false);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
		table.addCell(cell);
                  
		document.add(table);
		document.add(new Phrase("    "));	
		document.newPage();
		}
		catch (Exception e) {
			e.printStackTrace();			
		}	
	}
	
	public static boolean checkAvailability(String dTime, int id, String type, AppointmentManager aManager)
	{
		boolean available = false;
		
		if (type == "V")//volunteer
			available = aManager.hasAppointmentByVolunteer(id, dTime);
		else
			available = aManager.hasAppointmentByPatient(id, dTime);		
		return available;		
	}
	
	public static String getValuesFromCheckboxList(String[] strArray)
	{		
		String str;
		if (strArray != null)
		{
			str = Arrays.toString(strArray);
			//remove "[" and "]"
			str = str.replace("[", "");
			str = str.replace("]", "");
			return str;
		}
		else
			return "999";	
	}
}
