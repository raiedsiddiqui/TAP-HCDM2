package org.tapestry.utils;

import java.io.BufferedReader;
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
						
			if (isAvailable(availableTime, time)&&(!aManager.hasAppointmentByVolunteer(v.getVolunteerId(), dateTime)))
				vList.add(v);
		}
		return vList;
	}
	
	public static boolean isAvailable(String availableTime, String time)
	{
		boolean available = false;
		String[] strArray = availableTime.split(",");
		
		for (int i=0; i<strArray.length; i++)
		{
			if (strArray[i].equals(time))
				available=true;
		}		
		return available;
		
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
   	
   	/**
   	 * Add new survey script
   	 * @param surveyId
   	 * @param allSurveyTemplates
   	 * @param selectedSurveyTemplates
   	 */
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
   	////
  	/**
   	 * 
   	 * @param selectSurveyTemplats
   	 * @param volunteerIds
   	 * @param request
   	 * @param model
   	 * @param surveyManager
   	 * @throws JAXBException
   	 * @throws DatatypeConfigurationException
   	 * @throws Exception
   	 */
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
   	////
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
	
	public static ModelAndView execute(HttpServletRequest request, String documentId, TapestryPHRSurvey currentSurvey, PHRSurvey templateSurvey) throws Exception
	{
		ModelAndView m = new ModelAndView();
		final String questionId = request.getParameter("questionid");
		String direction = request.getParameter("direction");		
		String observerNotes = request.getParameter("observernote");	
		
		if (direction == null)
			direction = "forward";

		if (documentId == null)
		{
			m.setViewName("failed");
			return m;
		}

		String[] answerStrs = request.getParameterValues("answer");
		
		String nextQuestionId = questionId;
		//if requested survey does not exist
		if (currentSurvey == null)
		{
			m.setViewName("failed");
			return m;
		}
				
		boolean saved = false;

		//if starting/continuing survey, clear session
		if (questionId == null)
		{		
			//if just starting/continuing(from before) the survey, direct to last question
			String lastQuestionId;

			if (currentSurvey.getQuestions().size() == 0)
			{
				boolean moreQuestions = addNextQuestion(null, currentSurvey, templateSurvey);
				if (!moreQuestions)
				{
					m.setViewName("failed");
					return m;
				}
			}

			if (currentSurvey.isComplete())
			{ //if complete show first question				
				lastQuestionId = currentSurvey.getQuestions().get(0).getId();				
				m.addObject("hideObservernote", true);
			}
			else
			{ //if not complete show next question
				lastQuestionId = currentSurvey.getQuestions().get(currentSurvey.getQuestions().size() - 1).getId();
				//logic for displaying Observer Notes button
				if (isFirstQuestionId(lastQuestionId, '0'))
					m.addObject("hideObservernote", true);
				else
					m.addObject("hideObservernote", false);				
			}		
			m.addObject("survey", currentSurvey);
			m.addObject("templateSurvey", templateSurvey);
			m.addObject("questionid", lastQuestionId);
			m.addObject("resultid", documentId);
			
			m.setViewName("/volunteer/ubc/show_volunteerSurvey");
			
			return m;
		}//end of questionId == null;

		String errMsg = null;

		//if continuing survey (just submitted an answer)
		if (questionId != null && direction.equalsIgnoreCase("forward"))
		{				
			if (currentSurvey.getQuestionById(questionId).getQuestionType().equals(SurveyQuestion.ANSWER_CHECK) && answerStrs == null)
				answerStrs = new String[0];
			
			if (answerStrs != null && (currentSurvey.getQuestionById(questionId).getQuestionType().equals(SurveyQuestion.ANSWER_CHECK) || !answerStrs[0].equals("")))
			{						
				SurveyQuestion question = currentSurvey.getQuestionById(questionId);					
				String questionText = question.getQuestionText();
				
				//append observernote to question text
				if (!Utils.isNullOrEmpty(questionText))
				{
					String separator = "/observernote/ ";
					StringBuffer sb = new StringBuffer();
					sb.append(questionText);
					sb.append(separator);
					sb.append(observerNotes);
					
					questionText = sb.toString();
					question.setQuestionText(questionText);
				}				
				ArrayList<SurveyAnswer> answers = convertToSurveyAnswers(answerStrs, question);		
				
				boolean goodAnswerFormat = true;
				if (answers == null)
					goodAnswerFormat = false;
				
				//check each answer for validation					
				if (goodAnswerFormat && question.validateAnswers(answers))	
				{
					boolean moreQuestions;
					//see if the user went back (if current question the last question in user's question profile)
					if (!currentSurvey.getQuestions().get(currentSurvey.getQuestions().size() - 1).equals(question))
					{
						ArrayList<SurveyAnswer> existingAnswers = currentSurvey.getQuestionById(questionId).getAnswers();
						//if user hit back, and then forward, and answer wasn't changed
						if (org.apache.commons.lang.StringUtils.join(answerStrs, ", ").equals(org.apache.commons.lang.StringUtils.join(existingAnswers, ", ")) || currentSurvey.isComplete())
							moreQuestions = true;
						else
						{
							ArrayList<SurveyQuestion> tempquestions = new ArrayList<SurveyQuestion>(); //Create a temp array list to transfer answered questions

							//remove all future answers								
							//clear all questions following it
							int currentSurveySize = currentSurvey.getQuestions().size(); //stores number of questions
							int currentQuestionIndex = currentSurvey.getQuestions().indexOf(question); //gets the current question index
																					
							for (int i = currentQuestionIndex +1; i < currentSurveySize; i++)
							{
								tempquestions.add(currentSurvey.getQuestions().get(currentQuestionIndex +1));
								currentSurvey.getQuestions().remove(currentQuestionIndex + 1);  //goes through quesitons list and removes each question after it
							}							
							//save answers modified/input by user into question
							question.setAnswers(answers);								
							saved = true;
							//add new question
							moreQuestions = addNextQuestion(questionId, currentSurvey, templateSurvey);

							//check if old index and new index contain same questions in the same list
							int sizeofcurrentquestionslist = currentSurvey.getQuestions().size(); //Size of new getQuestions aftre removing future questions

							if (currentSurvey.getQuestions().get(sizeofcurrentquestionslist-1).getId().equals(tempquestions.get(0).getId()))
							{
								currentSurvey.getQuestions().remove(sizeofcurrentquestionslist-1);
								for (int y=0;y<tempquestions.size();y++) 
									currentSurvey.getQuestions().add(tempquestions.get(y));
								moreQuestions = addNextQuestion(questionId, currentSurvey, templateSurvey);
							}								
								//if same then replace temp list with new list
								//if not then add the one new item.
						}
						//if user didn't go back, and requesting the next question
					}
					else
					{
						question.setAnswers(answers);
						saved = true;
						moreQuestions = addNextQuestion(questionId, currentSurvey, templateSurvey);						
					}
					//finished survey
					if (!moreQuestions)
					{
						if (!currentSurvey.isComplete()){							
							SurveyAction.updateSurveyResult(currentSurvey);
							
							m.addObject("survey_completed", true);
							m.addObject("survey", currentSurvey);
							m.addObject("templateSurvey", templateSurvey);
							m.addObject("questionid", questionId);
							m.addObject("resultid", documentId);
							m.addObject("message", "SURVEY FINISHED - Please click SUBMIT");
							m.addObject("hideObservernote", false);
							m.setViewName("/volunteer/ubc/show_volunteerSurvey");
							return m;
						} 
						else {									
							m.addObject("survey", currentSurvey);
							m.addObject("templateSurvey", templateSurvey);
							m.addObject("questionid", questionId);
							m.addObject("resultid", documentId);
							m.addObject("message", "End of Survey");
							m.addObject("hideObservernote", false);
							m.setViewName("/volunteer/ubc/show_volunteerSurvey");
							return m;
						}
					}
					int questionIndex = currentSurvey.getQuestionIndexbyId(questionId);
					nextQuestionId = currentSurvey.getQuestions().get(questionIndex + 1).getId();
				

					//save to indivo
					if (saved && questionIndex % 4 == 0 && !currentSurvey.isComplete()) 
						SurveyAction.updateSurveyResult(currentSurvey);

					//if answer fails validation
				}// end of validation answers
				else {						
					m.addObject("survey", currentSurvey);
					m.addObject("templateSurvey", templateSurvey);
					m.addObject("questionid", questionId);
					m.addObject("resultid", documentId);
					
					if (question.getRestriction() != null && question.getRestriction().getInstruction() != null)
						m.addObject("message", question.getRestriction().getInstruction());
					m.addObject("hideObservernote", false);
					m.setViewName("/volunteer/ubc/show_volunteerSurvey");
					return m;
				}
				//if answer not specified, and hit forward
			}
			else 
				errMsg = "You must supply an answer";
		}//end of forward action
		else if (direction.equalsIgnoreCase("backward"))
		{
			int questionIndex = currentSurvey.getQuestionIndexbyId(questionId);
			if (questionIndex > 0) 
				nextQuestionId = currentSurvey.getQuestions().get(questionIndex - 1).getId();
		}
		
		//backward to the description page(before the first qustion)
		if ((questionId != null) && ("backward".equals(direction)) && (isFirstQuestionId(questionId, '0')))
			m.addObject("hideObservernote", true);
		else
			m.addObject("hideObservernote", false);
		
		m.addObject("survey", currentSurvey);
		m.addObject("templateSurvey", templateSurvey);
		m.addObject("questionid", nextQuestionId);
		m.addObject("resultid", documentId);
		if (errMsg != null) m.addObject("message", errMsg);	

		m.setViewName("/volunteer/ubc/show_volunteerSurvey");
		return m;
	}
	
	private static boolean isFirstQuestionId(String str, char c){
		boolean isFirst = false;
		int length = str.length();
		
		//'1' is only digital in string for backward direction, and '0' for forward direction
		if ((str.charAt(length - 1) == c) && Character.isLetter(str.charAt(length - 2)))
			isFirst = true;
		
		return isFirst;
	}

	private static boolean addNextQuestion(String currentQuestionId, TapestryPHRSurvey currentSurvey, PHRSurvey templateSurvey) throws SurveyException
	{
		SurveyQuestion nextQuestion;
		if (currentQuestionId == null)
		{
			if (templateSurvey.getQuestions().size() == 0) return false;
			nextQuestion = templateSurvey.getQuestions().get(0);
		}
		else
		{
			String nextQuestionId = currentSurvey.getNextQuestionId(currentQuestionId);
			if (nextQuestionId == null) return false;		
			
			nextQuestion = templateSurvey.getQuestionById(nextQuestionId);

		}
		currentSurvey.getQuestions().add(nextQuestion);
		return true;
	}

	private static ArrayList<SurveyAnswer> convertToSurveyAnswers(String[] answers, SurveyQuestion question) throws SurveyParseException
	{
		ArrayList<SurveyAnswer> surveyAnswers = new ArrayList<SurveyAnswer>();
		SurveyAnswerFactory answerFactory = new SurveyAnswerFactory();
		SurveyAnswer answerObj;
		for (String answer : answers)
		{
			answerObj = answerFactory.getSurveyAnswer(question.getQuestionType(), answer);			
			
			if (answerObj == null) 
				return null;				
			else
				surveyAnswers.add(answerObj);		
		}
		return surveyAnswers;
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
	//		document.add(new Phrase("    "));   
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
			
			document.open(); 
			//set main part of report
			buildMainPartReportPDF(report, response, document);
			//set volunteer information
			buildVolunteerPartReportPDF(report, document);
			
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		
	}
	
	private static void buildMainPartReportPDF (Report report, HttpServletResponse response, Document document)
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

	//======================Research Data Download===================================//
	public static List<ResearchData> getResearchDatas(PatientManager patientManager, SurveyManager surveyManager, int siteId)
	{
		List<Patient> patients = patientManager.getPatientsBySite(siteId);	
		SurveyResult sr;
		List<ResearchData> researchDatas = new ArrayList<ResearchData>();
		ResearchData rData;
		int patientId, researchId, size;
		String xml, observerNote, strResearchId;
		LinkedHashMap<String, String> res;
		List<DisplayedSurveyResult> displayedResults;
		StringBuffer sb;
		String[] goalsArray;
			
		for (int i = 0; i < patients.size(); i++)
		{
			rData = new ResearchData();
			patientId = patients.get(i).getPatientID();
			strResearchId = patients.get(i).getResearchID();
			rData.setResearchId(strResearchId);
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
							sb.append(".");		
				   		}
				   	}										
					rData.setdSS_notes_TO(sb.toString());
				}									
			}		
			//EQ5D
			try{
				sr = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "3. Quality of Life");
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
				{//if answer is empty, format it to 0
					displayedResults = formatEmptyResultAnswerToInt(displayedResults);
					
					rData.setQol1(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
					rData.setQol2(Integer.parseInt(displayedResults.get(1).getQuestionAnswer()));
					rData.setQol3(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));
					rData.setQol4(Integer.parseInt(displayedResults.get(3).getQuestionAnswer()));
					rData.setQol5(Integer.parseInt(displayedResults.get(4).getQuestionAnswer()));
					rData.setQol6(Integer.parseInt(displayedResults.get(5).getQuestionAnswer()));
				}
			}
			//Goals
			try{
				sr = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "Goals");
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
			//RAPA
			try{
				sr = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "Physical Activity");
			}catch (Exception e) {
				System.out.println("throws exception on Physical Activity === patient id == " + patientId);
				sr = null;
			}
			
			if (sr != null)
			{				
				try{
					xml = new String(sr.getResults(), "UTF-8");
			   	} catch (Exception e) {
			   		xml = "";
			   	}
				res = ResultParser.getResults(xml);
				displayedResults = ResultParser.getDisplayedSurveyResults(res);		
				
				if (!displayedResults.isEmpty())
				{
					//if answer is empty, format it to 0
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
			//Advance directive
			try{
				sr = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "Advance Directives");
			}catch (Exception e) {
				System.out.println("throws exception on Advance Directives=== patient id == " + patientId);
				sr = null;
			}
			
			if (sr != null)
			{
				try{
					xml = new String(sr.getResults(), "UTF-8");
			   	} catch (Exception e) {
			   		xml = "";
			   	}
				res = ResultParser.getResults(xml);
				displayedResults = ResultParser.getDisplayedSurveyResults(res);	
				
				if (!displayedResults.isEmpty())
				{
					//if answer is empty, format it to 0
					displayedResults = formatEmptyResultAnswerToInt(displayedResults);
					
					rData.setAd1(Integer.parseInt(displayedResults.get(0).getQuestionAnswer()));
					rData.setAd2(Integer.parseInt(displayedResults.get(1).getQuestionAnswer()));
					rData.setAd3(Integer.parseInt(displayedResults.get(2).getQuestionAnswer()));							
				}
			}
			//memory
			try{
				sr = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "Memory");
			}catch (Exception e) {
				System.out.println("throws exception on Memory === patient id == " + patientId);
				sr = null;
			}
			
			if (sr != null)
			{	
				try{
					xml = new String(sr.getResults(), "UTF-8");
			   	} catch (Exception e) {
			   		xml = "";
			   	}
				res = ResultParser.getResults(xml);
				displayedResults = ResultParser.getDisplayedSurveyResults(res);		
				
				if (!displayedResults.isEmpty())
				{
					int sizeOfMemory = displayedResults.size();
					DisplayedSurveyResult result;
					
					result = displayedResults.get(0);
					result = formatEmptyResultAnswerToInt(result);
					rData.setMem1(Integer.parseInt(result.getQuestionAnswer()));		
		
					if (sizeOfMemory == 2)
					{
						rData.setMem2("");
						result = displayedResults.get(1);
						result = formatEmptyResultAnswerToInt(result);
						rData.setMem1(Integer.parseInt(result.getQuestionAnswer()));
						rData.setMem4("");
					}
					else if (sizeOfMemory == 3)
					{
						if (Utils.isNumeric(displayedResults.get(1).getQuestionAnswer()))
						{
							rData.setMem2("");
							result = displayedResults.get(1);
							result = formatEmptyResultAnswerToInt(result);
							rData.setMem1(Integer.parseInt(result.getQuestionAnswer()));
							rData.setMem4(displayedResults.get(2).getQuestionAnswer());		
						}
					}
					else
					{
						rData.setMem2(displayedResults.get(1).getQuestionAnswer());
						result = displayedResults.get(2);
						result = formatEmptyResultAnswerToInt(result);
						rData.setMem1(Integer.parseInt(result.getQuestionAnswer()));
						rData.setMem4(displayedResults.get(3).getQuestionAnswer());				
					}
				}
			}
			//General health
			try{
				sr = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "General Health");
			}catch (Exception e) {
				System.out.println("throws exception on General Health === patient id == " + patientId);
				sr = null;
			}
			
			if (sr != null)
			{
				try{
					xml = new String(sr.getResults(), "UTF-8");
			   	} catch (Exception e) {
			   		xml = "";
			   	}
				res = ResultParser.getResults(xml);
				displayedResults = ResultParser.getDisplayedSurveyResults(res);	
				
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
			//Mobility
			try{
				sr = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "Mobility");
			}catch (Exception e) {
				System.out.println("throws exception on Mobility === patient id == " + patientId);
				sr = null;
			}
			
			if (sr != null)
			{
				try{
					xml = new String(sr.getResults(), "UTF-8");
			   	} catch (Exception e) {
			   		xml = "";
			   	}
				res = ResultParser.getResults(xml);
				displayedResults = ResultParser.getDisplayedSurveyResults(res);		
				List<Integer> aList;
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
								aList = makeMobilityMultipleAnswerList(answer);
								rData.setMob21(aList.get(0));
								rData.setMob22(aList.get(1));
								rData.setMob23(aList.get(2));
								rData.setMob24(aList.get(3));
								rData.setMob25(aList.get(4));
								rData.setMob26(aList.get(5));
							}
							else
								rData.setMob21(Integer.parseInt(answer));
						}
						
						if (qId.equals("a3a"))
							rData.setMob3(Integer.parseInt(answer));
						if (qId.equals("a3b"))
						{
							if (answer.contains(","))
							{								
								aList = makeMobilityMultipleAnswerList(answer);
								rData.setMob41(aList.get(0));
								rData.setMob42(aList.get(1));
								rData.setMob43(aList.get(2));
								rData.setMob44(aList.get(3));
								rData.setMob45(aList.get(4));
								rData.setMob46(aList.get(5));
							}
							else
								rData.setMob41(Integer.parseInt(answer));
						}
						
						if (qId.equals("a4a"))
							rData.setMob5(Integer.parseInt(answer));
						if (qId.equals("a4b"))
						{
							if (answer.contains(","))
							{								
								aList = makeMobilityMultipleAnswerList(answer);							 
								rData.setMob61(aList.get(0));
								rData.setMob62(aList.get(1));
								rData.setMob63(aList.get(2));
								rData.setMob64(aList.get(3));
								rData.setMob65(aList.get(4));
								rData.setMob66(aList.get(5));
							}
							else
								rData.setMob61(Integer.parseInt(answer));
						}
						
					}
				}
			}
			//Nutrition
			try{
				sr = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "Nutrition");
			}catch (Exception e) {
				System.out.println("throws exception on Nutrition === patient id == " + patientId);
				sr = null;
			}
			
			if (sr != null)
			{
				try{
					xml = new String(sr.getResults(), "UTF-8");
			   	} catch (Exception e) {
			   		xml = "";
			   	}
				res = ResultParser.getResults(xml);
				displayedResults = ResultParser.getDisplayedSurveyResults(res);		
				
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
			//3 month follow up
			try{
				sr = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "3 Month Follow Up");
			}catch (Exception e) {
				System.out.println("throws exception on 3 Month Follow Up === patient id == " + patientId);
				sr = null;
			}			
			if (sr != null)
			{			
				try{
					xml = new String(sr.getResults(), "UTF-8");
			   	} catch (Exception e) {
			   		xml = "";
			   	}
				res = ResultParser.getResults(xml);
				displayedResults = ResultParser.getDisplayedSurveyResults(res);		
							
				if (!displayedResults.isEmpty())
				{//if answer is empty, format it to 0
					displayedResults = formatEmptyResultAnswerToInt(displayedResults);
					size = displayedResults.size();				
					String questionId, answer;
					answer = displayedResults.get(0).getQuestionAnswer();
					rData.setFu1(Integer.parseInt(answer));
					
					for (int k =1; k < size; k++)
					{
						questionId = displayedResults.get(k).getQuestionId();
						answer = displayedResults.get(k).getQuestionAnswer();
												
						if (questionId.equals("CFQ2"))
							rData.setFu2(Integer.valueOf(answer));
						
						if (questionId.equals("CFQ3"))
							rData.setFu3(answer);
							
						if (questionId.equals("PHR1"))
							rData.setFu5(Integer.valueOf(answer));
						
						if (questionId.equals("PHR1a"))
							rData.setFu6(Integer.valueOf(answer));
						
						if (questionId.equals("PHR1ao"))
							rData.setFu7(answer);
							
						if (questionId.equals("PHR1b"))
							rData.setFu8(Integer.valueOf(answer));
						
						if (questionId.equals("PHR1bo"))
							rData.setFu9(answer);
						
						if (questionId.equals("GFU1"))
							rData.setFu11(Integer.valueOf(answer));
						
						if (questionId.equals("GFU2"))
							rData.setFu12(answer);
						
						if (questionId.equals("GFU3"))
							rData.setFu13(answer);
						
						if (questionId.equals("GFU4"))
							rData.setFu14(answer);
						
						if (questionId.equals("GFU5"))
							rData.setFu15(Integer.valueOf(answer));
						
						if (questionId.equals("GFU6"))
							rData.setFu16(answer);
						
						if (questionId.equals("GFU7"))
							rData.setFu17(answer);
						
						if (questionId.equals("GFU8"))
							rData.setFu18(answer);
						
						if (questionId.equals("GFU9"))
							rData.setFu19(Integer.valueOf(answer));
						
						if (questionId.equals("GFU10"))
							rData.setFu20(answer);
						
						if (questionId.equals("GFU11"))
							rData.setFu21(answer);
						
						if (questionId.equals("GFU12"))
							rData.setFu22(answer);							
					}
				}
			}			
			//Daily Life Activity
			try{
				sr = surveyManager.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, "1. Daily Life Activities");
			}catch (Exception e) {
				System.out.println("throws exception on 1. Daily Life Activities === patient id == " + patientId);
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
					size = displayedResults.size();
					switch (size) {
						case 1: rData.setDla1(displayedResults.get(0).getQuestionAnswer());			
					 			break;
					 	case 2: rData.setDla1(displayedResults.get(0).getQuestionAnswer());
					 			rData.setDla2(displayedResults.get(1).getQuestionAnswer());
					 			break;
					 	case 3: rData.setDla1(displayedResults.get(0).getQuestionAnswer());
								rData.setDla2(displayedResults.get(1).getQuestionAnswer());
								rData.setDla3(displayedResults.get(2).getQuestionAnswer());
					 			break;
					 	case 4: rData.setDla1(displayedResults.get(0).getQuestionAnswer());
								rData.setDla2(displayedResults.get(1).getQuestionAnswer());
								rData.setDla3(displayedResults.get(2).getQuestionAnswer());
								rData.setDla4(displayedResults.get(3).getQuestionAnswer());
					 			break;	
					 	case 5: rData.setDla1(displayedResults.get(0).getQuestionAnswer());
								rData.setDla2(displayedResults.get(1).getQuestionAnswer());
								rData.setDla3(displayedResults.get(2).getQuestionAnswer());
								rData.setDla4(displayedResults.get(3).getQuestionAnswer());
								rData.setDla5(displayedResults.get(4).getQuestionAnswer());
					 			break;	
					 	case 6: rData.setDla1(displayedResults.get(0).getQuestionAnswer());
								rData.setDla2(displayedResults.get(1).getQuestionAnswer());
								rData.setDla3(displayedResults.get(2).getQuestionAnswer());
								rData.setDla4(displayedResults.get(3).getQuestionAnswer());
								rData.setDla5(displayedResults.get(4).getQuestionAnswer());
								rData.setDla6(displayedResults.get(5).getQuestionAnswer());
					 			break;
					 	case 7: rData.setDla1(displayedResults.get(0).getQuestionAnswer());
								rData.setDla2(displayedResults.get(1).getQuestionAnswer());
								rData.setDla3(displayedResults.get(2).getQuestionAnswer());
								rData.setDla4(displayedResults.get(3).getQuestionAnswer());
								rData.setDla5(displayedResults.get(4).getQuestionAnswer());
								rData.setDla6(displayedResults.get(5).getQuestionAnswer());
								rData.setDla7(displayedResults.get(6).getQuestionAnswer());
					 			break;
					 	case 8: rData.setDla1(displayedResults.get(0).getQuestionAnswer());
								rData.setDla2(displayedResults.get(1).getQuestionAnswer());
								rData.setDla3(displayedResults.get(2).getQuestionAnswer());
								rData.setDla4(displayedResults.get(3).getQuestionAnswer());
								rData.setDla5(displayedResults.get(4).getQuestionAnswer());
								rData.setDla6(displayedResults.get(5).getQuestionAnswer());
								rData.setDla7(displayedResults.get(6).getQuestionAnswer());
								rData.setDla7a(displayedResults.get(7).getQuestionAnswer());
					 			break;
					 }	
				}
			}			
			researchDatas.add(rData);
		}		
		return researchDatas;
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
			///
			
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
}
