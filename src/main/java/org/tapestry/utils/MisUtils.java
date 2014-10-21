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
import org.tapestry.myoscar.utils.ClientManager;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Availability;
import org.tapestry.objects.Message;
import org.tapestry.objects.Patient;
import org.tapestry.objects.Report;
import org.tapestry.objects.SurveyResult;
import org.tapestry.objects.SurveyTemplate;
import org.tapestry.objects.User;
import org.tapestry.objects.Volunteer;
import org.tapestry.service.AppointmentManager;
import org.tapestry.service.MessageManager;
import org.tapestry.service.PatientManager;
import org.tapestry.service.SurveyManager;
import org.tapestry.service.UserManager;
import org.tapestry.service.VolunteerManager;
import org.tapestry.surveys.DoSurveyAction;
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

public class MisUtils {			    	

	//Mail-related settings
   	private static String mailAddress = "";
   	private static Session session;
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
		
		if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole()))
			patients = patientManager.getAllPatients();	//For central Admin		
		else		
			patients = patientManager.getPatientsByGroup(user.getOrganization());		
		
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
							p.setCity(person.getCity());					
							p.setHomePhone(person.getPhone1());		
							if (person.getStreetAddress1() != null)
								p.setAddress(person.getStreetAddress1());
							else if(person.getStreetAddress2() != null)
								p.setAddress(person.getStreetAddress2());
							
							break;
						}//end of match patient's username 
					}//end of loop of all patients in Tapestry
				}//end of loop of all patient in MyOscar				
			} catch (Exception e) {
				System.out.println("something wrong when calling myoscar server...");			
				e.printStackTrace();
			}
			
			session.setAttribute("allPatientWithFullInfos", patients);
		}		
		return patients;
	}
	/**
	 * get patients from session or from DB
	 * @param request
	 * @param patientManager
	 * @return a list of Patient
	 */
   	public static List<Patient> getPatients(SecurityContextHolderAwareRequestWrapper request, PatientManager patientManager ){
   		
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
						(Utils.isMatchVolunteer(v, p)) && ((!isExist(aList,v, p))))
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
					
					if (Utils.isMatchVolunteer(v1, v2))						
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
		
		Map<String, String> timePeriodMap = Utils.getAvailabilityMap();
		
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
		Map<String, String> timePeriodMap = Utils.getAvailabilityMap();
		
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
			surveyTemplateList = surveyManager.getAllSurveyTemplates();
			
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
				//if requested survey that's already done
				if (specificSurveys.size() < template.getMaxInstances() && 
						!isExistInSurveyResultList(surveyResults,st.getSurveyID(), patientIds[i]))
				{		    		
					TapestryPHRSurvey blankSurvey = template;
					blankSurvey.setQuestions(new ArrayList<SurveyQuestion>());// make blank survey
					sr.setResults(SurveyAction.updateSurveyResult(blankSurvey));
					String documentId = surveyManager.assignSurvey(sr);
					
					blankSurvey.setDocumentId(documentId);
					surveys.addSurvey(blankSurvey);
					specificSurveys = surveys.getSurveyListById(Integer.toString(st.getSurveyID())); //reload
		    	}
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
  			System.out.println("something wrong");
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
			patientList = patientManager.getPatientsByGroup(organizationId);
		}
	
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
			//green font
			Font gbMediumFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
			gbMediumFont.setColor(BaseColor.GREEN);
			Font gbSmallFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
			gbSmallFont.setColor(BaseColor.GREEN);
			//black font
			Font sFont = new Font(Font.FontFamily.HELVETICA, 9);	
			Font sbFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);	
			Font mFont = new Font(Font.FontFamily.HELVETICA, 12);		        
			Font bMediumFont = new Font(Font.FontFamily.HELVETICA , 16, Font.BOLD);		        
			Font iSmallFont = new Font(Font.FontFamily.HELVETICA , 9, Font.ITALIC );
			Font ibMediumFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLDITALIC);
			Font bmFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
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
			
			PdfPCell cell = new PdfPCell(new Phrase(patientName, sbFont));
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthLeft(1f);
			cell.setBorderWidthBottom(0);
			cell.setBorderWidthRight(0);		
			cell.setPadding(5);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Address: 11 hunter Street S, Hamilton, On" + report.getPatient().getAddress(), sbFont));
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthRight(1f);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(0);	 
			cell.setPadding(5);
			table.addCell(cell);
		     
			cell = new PdfPCell(new Phrase("MRP: David Chan", sbFont));
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
		        
			cell = new PdfPCell(new Phrase("Visit: " + report.getAppointment().getStrType(), sbFont));
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
//			cell = new PdfPCell(new Phrase("TAPESTRY REPORT: --- " + report.getPatient().getBod(), blFont));
			cell = new PdfPCell(new Phrase("TAPESTRY REPORT: --- (0000-00-00)", blFont));
			cell.setBorder(0);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("PATIENT GOAL(S)", wbLargeFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	            
	            
			table.addCell(cell);
	            
			for (int i = 0; i < report.getPatientGoals().size(); i++){
				cell = new PdfPCell(new Phrase(report.getPatientGoals().get(i).toString()));
				table.addCell(cell);
			}	            
			document.add(table);			
			//alert
			table = new PdfPTable(1);
			table.setWidthPercentage(100);
			float[] cWidths = {1f, 18f};
		            
			Phrase comb = new Phrase(); 
			comb.add(new Phrase("     ALERT :", rbFont));
			comb.add(new Phrase(" Consider Case Review wirh IP-TEAM", wbLargeFont));	    
			cell.addElement(comb);	
			cell.setBackgroundColor(BaseColor.BLACK);	           
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			List<String> alerts = report.getAlerts(); 
	         	            
			for (int i =0; i<alerts.size(); i++){
				cell = new PdfPCell(new Phrase(" . " + alerts.get(i).toString(), rmFont));		
				cell.setPadding(3);
				table.addCell(cell);
			}
			document.add(table);
			document.add(new Phrase("    "));   
			//Key observation
			table = new PdfPTable(1);
			table.setWidthPercentage(100);
	            
			cell = new PdfPCell(new Phrase("KEY OBSERVATIONS by Volunteer", wbLargeFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	 
	            
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getAppointment().getKeyObservation()));
			table.addCell(cell);
			document.add(table);
			//Plan
			table = new PdfPTable(2);
			table.setWidthPercentage(100);
			cell = new PdfPCell(new Phrase("PLAN", wbLargeFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
			cell.setColspan(2);
			table.addCell(cell);
	            
			List<String> pList = new ArrayList<String>();
			if (!Utils.isNullOrEmpty(report.getAppointment().getPlans()))
				pList = Arrays.asList(report.getAppointment().getPlans().split(","));		
	    		
			Map<String, String> pMap = new TreeMap<String, String>();
	    		
			for (int i = 1; i<= pList.size(); i++){
				pMap.put(String.valueOf(i), pList.get(i-1));
			}
			for (Map.Entry<String, String> entry : pMap.entrySet()) {
				cell = new PdfPCell(new Phrase(entry.getKey()));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);	            	
	            	
				cell = new PdfPCell(new Phrase(entry.getValue()));
				table.addCell(cell); 
			}			
			table.setWidths(new float[]{1f, 18f});
			document.add(table);
			document.add(new Phrase("    "));	           
			//Additional Information
			table = new PdfPTable(2);
			table.setWidthPercentage(100);
			cell = new PdfPCell(new Phrase("ADDITIONAL INFORMATION", wbLargeFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setColspan(2);
			table.addCell(cell);
	            
			for (Map.Entry<String, String> entry : report.getAdditionalInfos().entrySet()) {
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
			document.newPage();
			//Summary of Tapestry tools
			table = new PdfPTable(3);
			table.setWidthPercentage(100);
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
	           
			StringBuffer sb = new StringBuffer();
			sb.append("Clock drawing test: ");
			sb.append(report.getScores().getClockDrawingTest());
			sb.append("\n");
			sb.append("Timed up-and-go test score = ");
			sb.append(report.getScores().getTimeUpGoTest());
			sb.append("\n");
			sb.append("Edmonton Frail Scale sore = ");
			sb.append(report.getScores().getEdmontonFrailScale());	
			sb.append("\n");
	            
			cell = new PdfPCell(new Phrase(sb.toString(), iSmallFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	    
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			Phrase p = new Phrase();
			Chunk underline = new Chunk("Edmonton Frail Scale (Score Key):", sFont);
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
			sb.append("Screen II score : ");
			sb.append(report.getScores().getNutritionScreen());
			sb.append("\n");	           
			sb.append("\n");	            
			sb.append("\n");
	            
			cell = new PdfPCell(new Phrase(sb.toString(), iSmallFont));
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			p = new Phrase();
			underline = new Chunk("Screen II Nutrition Screening Tool:", sFont);
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
	            
			cell = new PdfPCell(new Phrase(sb.toString(), iSmallFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			p = new Phrase();	   
			underline = new Chunk("Screen II Nutrition Screening Tool:", sFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	  
			p.add(underline); 	     
			p.add(new Chunk("\n"));
			p.add(new Chunk("(Score < 10 risk cut off)", iSmallFont));
	            
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
	            
			////Mobility
			PdfPTable nest_table1 = new PdfPTable(1);			
			cell = new PdfPCell(new Phrase("Mobility ", mFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);		         
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(1f);
			cell.setBorderWidthTop(0);
			cell.setBorderWidthRight(0);	
			nest_table1.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Walking 2.0 km ", sFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);	   
			cell.setBorder(0);	            
			nest_table1.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Walking 0.5 km ", sFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(0);	           
			cell.setBorderWidthRight(0);	
			nest_table1.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Climbing Stairs ", sFont));	               
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
	            	            	            
			sb = new StringBuffer();
			sb.append("MANTY:");
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
	            
			cell = new PdfPCell(new Phrase(sb.toString(), sFont));
			cell.setNoWrap(false);	         
			table.addCell(cell);   
	            
			//RAPA	            
			cell = new PdfPCell(new Phrase("Physical Activity", mFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	      
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setMinimumHeight(45f);
			table.addCell(cell);            
	           
			sb = new StringBuffer();
			sb.append("Score =  ");
			sb.append(report.getScores().getPhysicalActivity());
			sb.append("\n");	
			sb.append("\n");	            
			sb.append("\n");
	            
			cell = new PdfPCell(new Phrase(sb.toString(), iSmallFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			sb = new StringBuffer();
			sb.append("Rapid Assessment of Physical Activity(RAPA)");
			sb.append("\n");
			sb.append("Score range : 1-7");
			sb.append("\n");
			sb.append("Score < 6 Suboptimal Activity(Aerobic)");
			sb.append("\n");	            
			sb.append("\n");	            
	            
			cell = new PdfPCell(new Phrase(sb.toString(), sFont));
			cell.setNoWrap(false);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
			table.addCell(cell);
	            
			document.add(table);
			document.add(new Phrase("    "));	            
			//Tapestry Questions
			table = new PdfPTable(2);
			table.setWidthPercentage(100);
			cell = new PdfPCell(new Phrase("TAPESTRY QUESTIONS", bMediumFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
			cell.setColspan(2);
			table.addCell(cell);
	            
			for (Map.Entry<String, String> entry : report.getDailyActivities().entrySet()) {
				cell = new PdfPCell(new Phrase(entry.getKey(), sFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);	            	
	            
				cell = new PdfPCell(new Phrase(entry.getValue(), sFont));
				table.addCell(cell); 
			}	           
			table.setWidths(cWidths);
			document.add(table);
			//Volunteer Information
			table = new PdfPTable(2);
			table.setWidthPercentage(100);
			cell = new PdfPCell(new Phrase("VOLUNTEER INFORMATION & NOTES", gbMediumFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
			cell.setColspan(2);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("1", bmFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getAppointment().getVolunteer(), ibMediumFont));
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("2", bmFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getAppointment().getPartner(), ibMediumFont));
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("V", gbMediumFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getAppointment().getComments(), gbMediumFont));
			cell.setPaddingBottom(10);
			table.addCell(cell);
	            
			table.setWidths(cWidths);
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
	public static Map<String, String> getSurveyContentMap(List<String> questionTextList, List<String> questionAnswerList){
		Map<String, String> content;
		
		if (questionTextList != null && questionTextList.size() > 0)
   		{//remove the first element which is description about whole survey
   			questionTextList.remove(0);
   			
   	   		if (questionAnswerList != null && questionAnswerList.size() > 0)
   	   		{//remove the first element which is empty or "-"
	   	   		questionAnswerList.remove(0);
	   	   			
	   	   		content = new TreeMap<String, String>(); 
		   	   	StringBuffer sb;
	   	   		
	   	   		for (int i = 0; i < questionAnswerList.size(); i++){
	   	   			sb = new StringBuffer();
	   	   			sb.append(questionTextList.get(i));
	   	   			sb.append("<br/><br/>");
	   	   			sb.append("\"");
	   	   			sb.append(questionAnswerList.get(i));
	   	   			sb.append("\"");
	   	   			content.put(String.valueOf(i + 1), sb.toString());
	   	   		}	   	   		
	   	   		return content;
   	   		}
   	   		else
   	   			System.out.println("All answers in Goal Setting survey are empty!");   	   			
   		}   			
   		else
   			System.out.println("Bad thing happens, no question text found for this Goal Setting survey!");
		
		return null;   	
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
	 * remove observer notes from answer
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
   		    	
   		    	index = question.indexOf("/observernote/");
   		    	
   		    	if (index > 0)
   		    		question = question.substring(0, index);
   		    	
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
   		    	
   		    	//remove observer notes
   		    	index = question.indexOf("/observernote/");
   		    	
   		    	if (index > 0)
   		    		question = question.substring(0, index);   		    	
   		    	qList.add(question); 
   		    }   		   
   		}		
		return qList;
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
   		    	
   		    	index = question.indexOf("/observernote/");
   		    	
   		    	if (index > 0)
   		    		question = question.substring(0, index);
   		    	
   		    	if (!question.equals("-"))
   		    		qMap.put(key, question);    	
   		    }
   		}
		return qMap;
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
	 * @param session
	 * @param request
	 * @param model
	 * @param messageDao
	 */
	public static void setUnreadMsg(HttpSession session, SecurityContextHolderAwareRequestWrapper request, 
			ModelMap model, MessageManager messageDao){		
		if (session == null)
			session = request.getSession();
		
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
	
	// ================= Appointment ====================//
	public static boolean isFirstVisit(int patientId, AppointmentManager appointmentManager){
		boolean isFirst = true;
		List<Appointment> appointments = appointmentManager.getAllApprovedAppointmentsForPatient(patientId);	
		
		if ((appointments != null)&& (appointments.size()>0))
			isFirst = false;
		
		return isFirst;
		
	}

		
	
	
}
