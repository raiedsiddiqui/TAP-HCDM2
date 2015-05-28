package org.tapestry.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

import org.oscarehr.myoscar_server.ws.PersonTransfer3;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.survey_component.actions.SurveyAction;
import org.survey_component.data.SurveyQuestion;
import org.tapestry.utils.Utils;
//import org.tapestry.utils.MisUtils.ReportHeader;
import org.tapestry.myoscar.utils.ClientManager;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Availability;
import org.tapestry.objects.DisplayedSurveyResult;
import org.tapestry.objects.HL7Report;
import org.tapestry.objects.Message;
import org.tapestry.objects.Patient;
import org.tapestry.objects.Report;
import org.tapestry.objects.ResearchData;
import org.tapestry.objects.Site;
import org.tapestry.objects.SurveyResult;
import org.tapestry.objects.SurveyTemplate;
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
		try{			
			String userName = p.getUserName();			//username in MyOscar			
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
		userManager.setPasswordForUser(user.getUserID(), volunteer.getPassword());		
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
	
		//convert arrayList to string for matching data type in DB
		if (availability != null)
			strAvailableTime=StringUtils.collectionToCommaDelimitedString(availability);	
		
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
	 * @param time
	 * @return
	 */
	public static List<Volunteer> getAllMatchedVolunteers(List<Volunteer> list, String time){		
		List<Volunteer> vList = new ArrayList<Volunteer>();		
		String availableTime;
				
		for (Volunteer v: list)
		{	//get volunteer's available time
			availableTime = v.getAvailability();
			
			if (availableTime.contains(time))
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
		
		model.addAttribute("monAvailability", lMonday);		
		model.addAttribute("tueAvailability", lTuesday);
		model.addAttribute("wedAvailability", lWednesday);
		model.addAttribute("thuAvailability", lThursday);
		model.addAttribute("friAvailability", lFriday);		
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
		
		if ("Experienced".equals(v1Type) || "Experienced".equals(v2Type) || "E".equals(v1Type) || "E".equals(v2Type)){
			matched = true;
		}
		else if (( "Intermediate".equals(v1Type) && "Intermediate".equals(v2Type)) ||("I".equals(v1Type) && "I".equals(v2Type)))
		{
			matched = true;
		}
		
		return matched;
	}
	
	public static void showVolunteerAvailability(Volunteer volunteer, SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		String strAvailibilities = volunteer.getAvailability();
		boolean mondayNull = false;
		boolean tuesdayNull = false;
		boolean wednesdayNull = false;
		boolean thursdayNull = false;
		boolean fridayNull = false;
		
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
		
		String[] arrayAvailibilities = strAvailibilities.split(",");
		
		Utils.getPosition("1","monDropPosition",arrayAvailibilities,mondayNull, model);
		Utils.getPosition("2","tueDropPosition",arrayAvailibilities,tuesdayNull, model);
		Utils.getPosition("3","wedDropPosition",arrayAvailibilities,wednesdayNull, model);
		Utils.getPosition("4","thuDropPosition",arrayAvailibilities,thursdayNull, model);
		Utils.getPosition("5","friDropPosition",arrayAvailibilities,fridayNull, model);
		
		model.addAttribute("volunteer", volunteer);
		model.addAttribute("mondayNull", mondayNull);
		model.addAttribute("tuesdayNull", tuesdayNull);
		model.addAttribute("wednesdayNull", wednesdayNull);
		model.addAttribute("thursdayNull", thursdayNull);
		model.addAttribute("fridayNull", fridayNull);
//		model.addAttribute("organizations", organizations);		
		
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
	public static List<SurveyTemplate> getSurveyTemplates(HttpServletRequest request, SurveyManager surveyManager){
		HttpSession session = request.getSession();		
		List<SurveyTemplate> surveyTemplateList;
		if (session.getAttribute("survey_template_list") == null)
		{			
			if (request.isUserInRole("ROLE_ADMIN"))//central admin 
				surveyTemplateList = surveyManager.getSurveyTemplatesWithCanDelete(0);
			else //local admin/site admin
				surveyTemplateList = surveyManager.getSurveyTemplatesWithCanDelete(getLoggedInUser(request).getSite());
			//save in the session
			if (surveyTemplateList != null && surveyTemplateList.size()>0)
				session.setAttribute("survey_template_list", surveyTemplateList);
		}
		else
			surveyTemplateList = (List<SurveyTemplate>)session.getAttribute("survey_template_list");
		
		return surveyTemplateList;
	}
	/**
	 * Check if survey result exist in DB
	 * @param surveyResults
	 * @param surveyTemplateId
	 * @param patientId
	 * @return
	 */
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
   	/**
   	 * Assign selected surveys to selected clients
   	 * @param surveyTemplates
   	 * @param patientIds
   	 * @param request
   	 * @param surveyManager
   	 * @throws JAXBException
   	 * @throws DatatypeConfigurationException
   	 * @throws Exception
   	 */
   	public static void assignSurveysToClient(List<SurveyTemplate> surveyTemplates, int[] patientIds, 
   			SecurityContextHolderAwareRequestWrapper request, SurveyManager surveyManager) 
   					throws JAXBException, DatatypeConfigurationException, Exception{
		
		List<SurveyResult> surveyResults = surveyManager.getAllSurveyResults();
		
   		TapestrySurveyMap surveys = DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResults, surveyTemplates);
   		SurveyResult sr;
   		
   		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
   		String startDate = sdf.format(new Date());   
 	
   		for(SurveyTemplate st: surveyTemplates) 
   		{
			List<TapestryPHRSurvey> specificSurveys = surveys.getSurveyListById(Integer.toString(st.getSurveyID()));
			
			SurveyFactory surveyFactory = new SurveyFactory();
			TapestryPHRSurvey template = surveyFactory.getSurveyTemplate(st);
			sr = new SurveyResult();
				
			for (int i = 0; i < patientIds.length; i++){
				sr.setPatientID(patientIds[i]);
				sr.setSurveyID(st.getSurveyID());
	            	
				//set today as startDate
				sr.setStartDate(startDate);	            	
				//if requested survey that's already done--removed if condition check, since a survey can be re-assign to a patient
//				if (specificSurveys.size() < template.getMaxInstances() && 
//						!isExistInSurveyResultList(surveyResults,st.getSurveyID(), patientIds[i]))
//				{		    	
					TapestryPHRSurvey blankSurvey = template;
					blankSurvey.setQuestions(new ArrayList<SurveyQuestion>());// make blank survey
					sr.setResults(SurveyAction.updateSurveyResult(blankSurvey));
					String documentId = surveyManager.assignSurvey(sr);
					
					blankSurvey.setDocumentId(documentId);
					surveys.addSurvey(blankSurvey);
					specificSurveys = surveys.getSurveyListById(Integer.toString(st.getSurveyID())); //reload
	//	    	}
				
			}   			
		}
   	}
   	/**
   	 * Add new survey script
   	 * @param surveyId
   	 * @param allSurveyTemplates
   	 * @param selectedSurveyTemplates
   	 */
  	public static void addSurveyTemplate(String[] surveyId,List<SurveyTemplate> allSurveyTemplates, 
   			List<SurveyTemplate> selectedSurveyTemplates){
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
   	/**
   	 * 
   	 * @param selectSurveyTemplats
   	 * @param patientIds
   	 * @param request
   	 * @param model
   	 * @param surveyManager
   	 * @throws JAXBException
   	 * @throws DatatypeConfigurationException
   	 * @throws Exception
   	 */
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
   	/**
   	 * check if all surveys have been finished
   	 * @param patientId
   	 * @param surveyManager
   	 * @return
   	 */
   	public static boolean completedAllSurveys(int patientId, SurveyManager surveyManager){
   		boolean completed = false;
   		
   		int count = surveyManager.countSurveyTemplate();
   		List<SurveyResult> completedSurveys = surveyManager.getCompletedSurveysByPatientID(patientId);
   		
   		if (count == completedSurveys.size())
   			completed = true;
   		
   		return completed;
   	}
   	/**
   	 * Remove obsererNote from notes
   	 * @param questionText
   	 * @return
   	 */
   	public static String removeObserverNotes(String questionText)
	{		
		//remove /observernotes/ from question text
		int index = questionText.indexOf("/observernote/");
	    	
	    if (index > 0)
	    	questionText = questionText.substring(0, index);
	    
	    return questionText;
	}

	
	// ===================== Mis =================================//
   	/**
   	 * Add patients and volunteer info in the ModelMap
   	 * @param model
   	 * @param volunteerManager
   	 * @param patientManager
   	 */
	public static void loadPatientsAndVolunteers(ModelMap model, VolunteerManager volunteerManager,
			PatientManager patientManager, SecurityContextHolderAwareRequestWrapper request ){
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
	
		model.addAttribute("volunteers", volunteers);	  
        model.addAttribute("patients", patientList);
	}
	
	/**
	 * hard coded clinics in a map, when it grows, will store them in the DB.
	 * @return
	 */
	public static Map<String, String> getClinics(){
		Map<String, String> clinics = new HashMap<String, String>();
		
		clinics.put("1", "McMaster Family Practice");
		clinics.put("2", "Stonechurch Family Health Center");
		
		return clinics;		
	}
	
	public static String getClinicName(String code){
		Map<String, String> clinics = getClinics();
		
		return clinics.get(code);		
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
//			List<Image> imageHeader = new ArrayList<Image>();      	
	            
//			Image imageLogo = Image.getInstance("webapps/tapestry/resources/images/logo.png"); 
//			imageLogo.scalePercent(25f);
//			imageHeader.add(imageLogo);			
//				            
//			Image imageDegroote = Image.getInstance("webapps/tapestry/resources/images/degroote.png");
//			imageDegroote.scalePercent(25f);
//			imageHeader.add(imageDegroote);	
//			
//			Image imageFhs = Image.getInstance("webapps/tapestry/resources/images/fhs.png");
//			imageFhs.scalePercent(25f);	
//			imageHeader.add(imageFhs);
						
//			ReportHeader event = new ReportHeader();
	//		event.setHeader(imageHeader);
	//		writer.setPageEvent(event);			
			
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
	            
			cell = new PdfPCell(new Phrase("PATIENT GOAL(S)", wbLargeFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			
			List<String> patientGoals = report.getPatientGoals();
			Phrase pp = new Phrase();
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
//			sb.append("Clock drawing test: ");
//			sb.append(report.getScores().getClockDrawingTest());
//			sb.append("\n");
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
   		    	
//   		    	//remove observer notes
//   		    	index = question.indexOf("/observernote/");
//   		    	
//   		    	if (index > 0)
//   		    		question = question.substring(0, index);   		    	
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
   		    	
//   		    	index = question.indexOf("/observernote/");
//   		    	
//   		    	if (index > 0)
//   		    		question = question.substring(0, index);
   		    	
   		    	if (!question.equals("-"))
   		    		qMap.put(key, question);    	
   		    }
   		}
		return qMap;
	}
	/*
	public static HL7Report generateHL7Report(Patient p, Appointment a, SurveyManager surveyManager){		
		HL7Report report = new HL7Report();		
		ScoresInReport scores = new ScoresInReport();
		    	
		report.setPatient(p);
		report.setAppointment(a);
		
		//Survey---  goals setting
		List<SurveyResult> surveyResultList = surveyManager.getCompletedSurveysByPatientID(p.getPatientID());
		SurveyResult dailyLifeActivitySurvey = new SurveyResult();	
		SurveyResult nutritionSurvey = new SurveyResult();
		SurveyResult rAPASurvey = new SurveyResult();
		SurveyResult mobilitySurvey = new SurveyResult();
		SurveyResult socialLifeSurvey = new SurveyResult();
		SurveyResult generalHealthySurvey = new SurveyResult();
		SurveyResult memorySurvey = new SurveyResult();
		SurveyResult carePlanSurvey = new SurveyResult();
		SurveyResult goals = new SurveyResult();		
		
		for(SurveyResult survey: surveyResultList){			
			String title = survey.getSurveyTitle();

			if (title.equalsIgnoreCase("1. Daily Life Activities"))//Daily life activity survey
				dailyLifeActivitySurvey = survey;
			
			if (title.equalsIgnoreCase("Nutrition"))//Nutrition
				nutritionSurvey = survey;
			
			if (title.equalsIgnoreCase("Physical Activity"))//RAPA survey
				rAPASurvey = survey;
			
			if (title.equalsIgnoreCase("Mobility"))//Mobility survey
				mobilitySurvey = survey;
			
			if (title.equalsIgnoreCase("Social Life")) //Social Life(Duke Index of Social Support)
				socialLifeSurvey = survey;
			
			if (title.equalsIgnoreCase("General Health")) //General Health(Edmonton Frail Scale)
				generalHealthySurvey = survey;
			
			if (title.equalsIgnoreCase("Memory")) //Memory Survey
				memorySurvey = survey;
			
			if (title.equalsIgnoreCase("Advance Directives")) //Care Plan/Advanced_Directive survey
				carePlanSurvey = survey;
			
			if (title.equalsIgnoreCase("2. Goals"))
				goals = survey;	
		}
		
		String xml;
		List<String> qList = new ArrayList<String>();
   	   		   		
   		//Additional Information
  		//Memory
   		try{
   			xml = new String(memorySurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mSurvey = ResultParser.getResults(xml);
   		qList = TapestryHelper.getQuestionListForMemorySurvey(mSurvey);   
   		
   		//Care Plan/Advanced_Directive
   		try{
   			xml = new String(carePlanSurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
	   	}
 
   		mSurvey = ResultParser.getResults(xml);
   		qList.addAll(TapestryHelper.getQuestionList(mSurvey)); 
   		
   		report.setAdditionalInfos(qList);
   		
   		//daily activity -- tapestry questions
   		try{
   			xml = new String(dailyLifeActivitySurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		qList = new ArrayList<String>();   
   		qList = TapestryHelper.getQuestionList(ResultParser.getResults(xml));
   		//last question in Daily life activity survey is about falling stuff
   		List<String> lAlert = new ArrayList<String>();
   		String fallingQA = qList.get(qList.size() -1);
   		if (fallingQA.startsWith("yes")||fallingQA.startsWith("Yes"))
   			lAlert.add(AlertsInReport.DAILY_ACTIVITY_ALERT);  
   		
   		//combine Q2 and Q3
   		StringBuffer sb = new StringBuffer();
   		sb.append(qList.get(1));
   		sb.append("; ");
   		sb.append(qList.get(2));
   		qList.set(1, sb.toString());
   		qList.remove(2);
   		report.setDailyActivities(qList);
   		report.setDailyActivities(qList);
   		
   		//alerts   		
   		try{
   			xml = new String(generalHealthySurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}	
   		//get answer list
   		qList = new ArrayList<String>();   
		qList = TapestryHelper.getQuestionList(ResultParser.getResults(xml));
		
		//get score info for Summary of tapestry tools
		if ((qList != null)&&(qList.size()>10))
		{
			if ("1".equals(qList.get(0))) 
				scores.setClockDrawingTest("No errors");
			else if ("2".equals(qList.get(0))) 
				scores.setClockDrawingTest("Minor spacing errors");
			else if ("3".equals(qList.get(0))) 
				scores.setClockDrawingTest("Other errors");
			else 
				scores.setClockDrawingTest("Not done");
			
			if ("1".equals(qList.get(10))) 
				scores.setTimeUpGoTest("1 (0-10s)");
			else if ("2".equals(qList.get(10))) 
				scores.setTimeUpGoTest("2 (11-20s)");
			else if ("3".equals(qList.get(10))) 
				scores.setTimeUpGoTest("3 (More than 20s)");
			else if ("4".equals(qList.get(10))) 
				scores.setTimeUpGoTest("4 (Patient required assistance)");
			else 
				scores.setTimeUpGoTest("5 (Patient is unwilling)");
		}		
		int generalHealthyScore = CalculationManager.getGeneralHealthyScaleScore(qList);
		lAlert = AlertManager.getGeneralHealthyAlerts(generalHealthyScore, lAlert, qList);
		
		if (generalHealthyScore < 5)
			scores.setEdmontonFrailScale(String.valueOf(generalHealthyScore) + " (Robust)");
		else if (generalHealthyScore < 7)
			scores.setEdmontonFrailScale(String.valueOf(generalHealthyScore) + " (Apparently Vulnerable)");
		else
			scores.setEdmontonFrailScale(String.valueOf(generalHealthyScore) + " (Frail)");		
	
		//Social Life Alert
		try{
   			xml = new String(socialLifeSurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}		
		qList = new ArrayList<String>();   		
   		//get answer list
		qList = TapestryHelper.getQuestionList(ResultParser.getResults(xml));
		
		int socialLifeScore = CalculationManager.getScoreByQuestionsList(qList);
		lAlert = AlertManager.getSocialLifeAlerts(socialLifeScore, lAlert);
		
		//summary tools for social supports
		if ((qList != null)&&(qList.size()>0))
		{
			int satisfactionScore = CalculationManager.getScoreByQuestionsList(qList.subList(0, 6));
			scores.setSocialSatisfication(satisfactionScore);
			int networkScore = CalculationManager.getScoreByQuestionsList(qList.subList(7, 10));
			scores.setSocialNetwork(networkScore);
		}
		   		   		
		//Nutrition Alerts   		
		try{
			xml = new String(nutritionSurvey.getResults(), "UTF-8");
		} catch (Exception e) {
			xml = "";
		}		   		
		qList = new ArrayList<String>();   		
		qList = TapestryHelper.getQuestionList(ResultParser.getResults(xml));  
		
		//get scores for nutrition survey based on answer list
		if ((qList != null)&&(qList.size()>0))
		{
			int nutritionScore = CalculationManager.getNutritionScore(qList);
			scores.setNutritionScreen(nutritionScore);
			
			//high nutrition risk alert
			Map<String, String> nAlert = new TreeMap<String, String>();
			lAlert = AlertManager.getNutritionAlerts(nutritionScore, lAlert, qList);
					
			//set alerts in report bean
			if (nAlert != null && nAlert.size()>0)	
				report.setAlerts(lAlert);
			else
				report.setAlerts(null);
		}
				
		//RAPA Alert
		try{
			xml = new String(rAPASurvey.getResults(), "UTF-8");
		} catch (Exception e) {
			xml = "";
		}
				
		qList = new ArrayList<String>();   		
		qList = TapestryHelper.getQuestionList(ResultParser.getResults(xml));  		

		int rAPAScore = CalculationManager.getAScoreForRAPA(qList);
		int sFPAScore = CalculationManager.getSFScoreForRAPA(qList);
		String aerobicMsg = CalculationManager.getAerobicMsg(rAPAScore);
		
		if (rAPAScore < 6)
			lAlert.add(AlertsInReport.PHYSICAL_ACTIVITY_ALERT);
				
		scores.setpAAerobic(rAPAScore);
		scores.setpAStrengthAndFlexibility(sFPAScore);
		scores.setAerobicMessage(aerobicMsg);
						
		//Mobility Alerts
		try{
			xml = new String(mobilitySurvey.getResults(), "UTF-8");
		} catch (Exception e) {
			xml = "";
		}				
		
		Map<String, String> qMap = TapestryHelper.getQuestionMap(ResultParser.getResults(xml));  
		   		   		
		lAlert = AlertManager.getMobilityAlerts(qMap, lAlert);    		
		
		//summary tools for Mobility
		scores = CalculationManager.getMobilityScore(qMap, scores);
//		for (int i = 0; i < lAlert.size(); i++)
//		{
//			if (lAlert.get(i).contains("2.0"))
//				scores.setMobilityWalking2(lAlert.get(i));
//		   			
//			if (lAlert.get(i).contains("0.5"))
//				scores.setMobilityWalkingHalf(lAlert.get(i));
//		   			
//			if (lAlert.get(i).contains("climbing"))
//				scores.setMobilityClimbing(lAlert.get(i));   			
//		}
		   		
		String noLimitation = "No Limitation";   		
		if (Utils.isNullOrEmpty(scores.getMobilityWalking2()))
			scores.setMobilityWalking2(noLimitation);		   		
		if (Utils.isNullOrEmpty(scores.getMobilityWalkingHalf()))
			scores.setMobilityWalkingHalf(noLimitation);
		   		
		if (Utils.isNullOrEmpty(scores.getMobilityClimbing()))
			scores.setMobilityClimbing(noLimitation);		   		
		report.setScores(scores);
		report.setAlerts(lAlert);
		//end of alert
		
		//Patient Goals -- life goals, health goals
		try{
   			xml = new String(goals.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}   	   		
   		//get answer list
		qList = new ArrayList<String>();
		qList = TapestryHelper.getQuestionList(ResultParser.getResults(xml));   
		
		List<String> gAS = new ArrayList<String>();
		if ((qList != null) && (qList.size()>0))
		{
			// for the patient goals on the top of report from Q8
			String patientGoals = CalculationManager.getPatientGoalsMsg(Integer.valueOf(qList.get(7)), qList);			
			gAS.add(patientGoals);
			// three goals above tapestry questions from Q3
			CalculationManager.setPatientGoalsMsg(qList.get(2), gAS);			
			report.setPatientGoals(gAS);
		}
				
		//get volunteer information
		List<String> volunteerInfos = new ArrayList<String>();
		volunteerInfos.add(a.getVolunteer());
		volunteerInfos.add(a.getPartner());
		volunteerInfos.add(a.getComments());
		
		report.setVolunteerInformations(volunteerInfos);		
		
		return report;
	}*/
	
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
	
	private static String getFullDescriptionForMobility(String questionId, String answer)
	{
		String fullDesAnswer="";
		int ind = Integer.valueOf(answer);
		
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
	public static void sendMessageToMyOscar()
	{
		//send message to MyOscar test
		try{
			Long lll = ClientManager.sentMessageToPatientInMyOscar(new Long(15231), "Message From Tapestry", "Hello");			
			
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

	//======================test===================================//
	public static boolean hasSurveyResultByPatient(int patientId, SurveyManager surveyManager)
	{
		boolean hasResult = false;
		int surveyId = surveyManager.getSurveyIdByTitle("4. Social Life");
		
		if (surveyManager.hasCompleteSurvey(surveyId, patientId))
			hasResult = true;
		else
		{
			surveyId = surveyManager.getSurveyIdByTitle("2. Goals");
			
			if (surveyManager.hasCompleteSurvey(surveyId, patientId))
				hasResult = true;
			else
			{
				surveyId = surveyManager.getSurveyIdByTitle("EQ5D");
				
				if (surveyManager.hasCompleteSurvey(surveyId, patientId))
					hasResult = true;
			}
		}		
		return hasResult;
	}
	
	public static List<ResearchData> getResearchDatas(PatientManager patientManager, SurveyManager surveyManager, int siteId)
	{
		List<Patient> patients;
		if (siteId == 0)//for central admin
			patients = patientManager.getAllPatients();	
		else
			patients = patientManager.getPatientsBySite(siteId);
	
		SurveyResult sr;
		List<ResearchData> researchDatas = new ArrayList<ResearchData>();
		ResearchData rData;
		int patientId, size;
		String xml, observerNote;
		LinkedHashMap<String, String> res;
		List<DisplayedSurveyResult> displayedResults;
		StringBuffer sb;
		String[] goalsArray;
		
	
		for (int i = 0; i < patients.size(); i++)
		{
			rData = new ResearchData();
			patientId = patients.get(i).getPatientID();
			
			if (!hasSurveyResultByPatient(patientId, surveyManager))
				continue;
			rData.setPatientId(patientId);
		
			//Social life
			try{
				sr = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "4. Social Life");
			}catch (Exception e) {
				System.out.println("throws exception on Social life === patient id == " + patientId);
				sr = null;
			}
			
			if (sr != null)
			{
				sb = new StringBuffer();
				
				try{
					xml = new String(sr.getResults(), "UTF-8");
			   	} catch (Exception e) {
			   		xml = "";
			   	}
				res = ResultParser.getResults(xml);
				displayedResults = ResultParser.getDisplayedSurveyResults(res);	
				
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
							sb.append("\n");
				   		}
				   	}
					rData.setdSS_notes_TO(sb.toString());
				}									
			}		
			//EQ5D
			try{
				sr = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "EQ5D");
			}catch (Exception e) {
				System.out.println("throws exception on EQ5D=== patient id == " + patientId);
				sr = null;
			}
			
			if (sr != null)
			{
				sb = new StringBuffer();
				
				try{
					xml = new String(sr.getResults(), "UTF-8");
			   	} catch (Exception e) {
			   		xml = "";
			   	}
				res = ResultParser.getResults(xml);
				displayedResults = ResultParser.getDisplayedSurveyResults(res);		
				
				if (!displayedResults.isEmpty())
				{
					rData.seteQ5D1_Mobility_TO(displayedResults.get(0).getQuestionAnswer());
					rData.seteQ5D2_2Selfcare_TO(displayedResults.get(1).getQuestionAnswer());
					rData.seteQ5D3_Usualact_TO(displayedResults.get(2).getQuestionAnswer());
					rData.seteQ5D4_Pain_TO(displayedResults.get(3).getQuestionAnswer());
					rData.seteQ5D5_Anxdep_TO(displayedResults.get(4).getQuestionAnswer());
					rData.seteQ5D6_Healthstate_TO(displayedResults.get(5).getQuestionAnswer());				
					
					for (int j=0; j<displayedResults.size()-1; j++)
					{
						observerNote = displayedResults.get(j).getObserverNotes();
						if (!Utils.isNullOrEmpty(observerNote))
						{
							sb.append(displayedResults.get(j).getObserverNotes());
							sb.append("\n");
				   		}
				   	}
					rData.seteQ5D5_notes_TO(sb.toString());
					rData.seteQ5D6_Healthstate_notes_TO(displayedResults.get(5).getObserverNotes());	
				}
			}
			//Goals
			try{
				sr = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "2. Goals");
			}catch (Exception e) {
				System.out.println("throws exception on Goals === patient id == " + patientId);
				sr = null;
			}
			
			if (sr != null)
			{
				sb = new StringBuffer();
				
				try{
					xml = new String(sr.getResults(), "UTF-8");
			   	} catch (Exception e) {
			   		xml = "";
			   	}
				res = ResultParser.getResults(xml);
				displayedResults = ResultParser.getDisplayedSurveyResults(res);		
				
				if (!displayedResults.isEmpty())
				{
					rData.setGoals1Matter_TO(displayedResults.get(0).getQuestionAnswer());
					rData.setGoals2Life_TO(displayedResults.get(1).getQuestionAnswer());
					rData.setGoals3Health_TO(displayedResults.get(2).getQuestionAnswer());
					rData.setGoals4List_TO(displayedResults.get(3).getQuestionAnswer());
													
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
					rData.setGoals8pPriority_TO(displayedResults.get(7).getQuestionAnswer());
					
					for (int j=0; j<displayedResults.size(); j++)
					{
						observerNote = displayedResults.get(j).getObserverNotes();
						if (!Utils.isNullOrEmpty(observerNote))
						{
							sb.append(displayedResults.get(j).getObserverNotes());
							sb.append("\n");
				   		}
				   	}
					rData.setGoalsDiscussion_notes_TO(sb.toString());			
				}			
				researchDatas.add(rData);
			}
		}		
		return researchDatas;
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

}
