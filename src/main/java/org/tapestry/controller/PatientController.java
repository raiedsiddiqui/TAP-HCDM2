package org.tapestry.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.survey_component.actions.SurveyAction;
import org.survey_component.data.SurveyQuestion;
import org.tapestry.controller.utils.MisUtils;
import org.tapestry.dao.ActivityDao;
import org.tapestry.dao.AppointmentDao;
import org.tapestry.dao.MessageDao;
import org.tapestry.dao.PatientDao;
//import org.tapestry.dao.PictureDao;
import org.tapestry.dao.SurveyResultDao;
import org.tapestry.dao.SurveyTemplateDao;
import org.tapestry.dao.UserDao;
import org.tapestry.dao.VolunteerDao;
//import org.tapestry.myoscar.utils.ClientManager;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Patient;
//import org.tapestry.objects.Picture;
import org.tapestry.objects.SurveyResult;
import org.tapestry.objects.SurveyTemplate;
import org.tapestry.objects.User;
import org.tapestry.objects.Volunteer;
import org.tapestry.surveys.DoSurveyAction;
import org.tapestry.surveys.SurveyFactory;
import org.tapestry.surveys.TapestryPHRSurvey;
import org.tapestry.surveys.TapestrySurveyMap;
import org.yaml.snakeyaml.Yaml;


@Controller
public class PatientController {
protected static Logger logger = Logger.getLogger(AppointmentController.class);

	private ClassPathResource dbConfigFile;
	private Map<String, String> config;
	private Yaml yaml;

	private PatientDao patientDao;
	private AppointmentDao appointmentDao;
	private ActivityDao activityDao;
	private VolunteerDao volunteerDao;
	private MessageDao messageDao;
	private SurveyTemplateDao surveyTemplateDao;
   	private SurveyResultDao surveyResultDao;
	
   	//Mail-related settings;
   	private Properties props;
   	private String mailAddress = "";
   	private Session session;
	
	/**
   	 * Reads the file /WEB-INF/classes/db.yaml and gets the values contained therein
   	 */
   	@PostConstruct
   	public void readDatabaseConfig(){
   		String DB = "";
   		String UN = "";
   		String PW = "";
   		String mailHost = "";
   		String mailUser = "";
   		String mailPassword = "";
   		String mailPort = "";
   		String useTLS = "";
   		String useAuth = "";
		try{
			dbConfigFile = new ClassPathResource("tapestry.yaml");
			yaml = new Yaml();
			config = (Map<String, String>) yaml.load(dbConfigFile.getInputStream());
			DB = config.get("url");
			UN = config.get("username");
			PW = config.get("password");
			mailHost = config.get("mailHost");
			mailUser = config.get("mailUser");
			mailPassword = config.get("mailPassword");
			mailAddress = config.get("mailFrom");
			mailPort = config.get("mailPort");
			useTLS = config.get("mailUsesTLS");
			useAuth = config.get("mailRequiresAuth");
		} catch (IOException e) {
			System.out.println("Error reading from config file");
			System.out.println(e.toString());
		}

		patientDao = new PatientDao(DB, UN, PW);
		appointmentDao = new AppointmentDao(DB, UN, PW);
		activityDao = new ActivityDao(DB, UN, PW);
		volunteerDao = new VolunteerDao(DB, UN, PW);
		messageDao = new MessageDao(DB, UN, PW);
		surveyTemplateDao = new SurveyTemplateDao(DB, UN, PW);
		surveyResultDao = new SurveyResultDao(DB, UN, PW);

		//Mail-related settings
		final String username = mailUser;
		final String password = mailPassword;
		props = System.getProperties();
		session = Session.getDefaultInstance(props, 
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
   	
   	@RequestMapping(value="/manage_patients", method=RequestMethod.GET)
	public String managePatients(ModelMap model, SecurityContextHolderAwareRequestWrapper request){
   		User loggedInUser = Utils.getLoggedInUser(request);
   		HttpSession session = request.getSession();
   		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		else
		{
			int unreadMessages = messageDao.countUnreadMessagesForRecipient(loggedInUser.getUserID());
			model.addAttribute("unread", unreadMessages);
		}	
		loadPatientsAndVolunteers(model);

		return "admin/manage_patients";
	}
   	
   	@RequestMapping(value="/add_patient", method=RequestMethod.POST)
	public String addPatient(SecurityContextHolderAwareRequestWrapper request, ModelMap model) 
			throws JAXBException, DatatypeConfigurationException, Exception{
		//Add a new patient
		Patient p = new Patient();
		
		int vId1 = Integer.parseInt(request.getParameter("volunteer1"));
		int vId2 = Integer.parseInt(request.getParameter("volunteer2"));
		Volunteer v1 = volunteerDao.getVolunteerById(vId1);
		Volunteer v2 = volunteerDao.getVolunteerById(vId2);
		
		if (Utils.isMatchVolunteer(v1, v2))
		{
			p.setFirstName(request.getParameter("firstname").trim());
			p.setLastName(request.getParameter("lastname").trim());
			if(request.getParameter("preferredname") != "") {
				p.setPreferredName(request.getParameter("preferredname").trim());
			}		
			p.setVolunteer(vId1);
			p.setPartner(vId2);		
			p.setMyoscarVerified(request.getParameter("myoscar_verified"));		
			p.setGender(request.getParameter("gender"));
			p.setNotes(request.getParameter("notes"));
			p.setAlerts(request.getParameter("alerts"));
			p.setClinic(request.getParameter("clinic"));
			
//			String availableTime = Utils.getAvailableTime(request);		
//			p.setAvailability(availableTime);
			
			patientDao.createPatient(p);
			
			Patient newPatient = patientDao.getNewestPatient();
			
			//Auto assign all existing surveys
			ArrayList<SurveyResult> surveyResults = surveyResultDao.getAllSurveyResults();
	   		ArrayList<SurveyTemplate> surveyTemplates = surveyTemplateDao.getAllSurveyTemplates();
	   		TapestrySurveyMap surveys = DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResults, surveyTemplates);
	   		
	   		for(SurveyTemplate st: surveyTemplates) 
	   		{
				List<TapestryPHRSurvey> specificSurveys = surveys.getSurveyListById(Integer.toString(st.getSurveyID()));
				
				SurveyFactory surveyFactory = new SurveyFactory();
				TapestryPHRSurvey template = surveyFactory.getSurveyTemplate(st);
					SurveyResult sr = new SurveyResult();
		            sr.setSurveyID(st.getSurveyID());
		            sr.setPatientID(newPatient.getPatientID());
		            
		            //set today as startDate
		            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		        	String startDate = sdf.format(new Date());            
		            
		            sr.setStartDate(startDate);
		          //if requested survey that's already done
		    		if (specificSurveys.size() < template.getMaxInstances())
		    		{
		    			TapestryPHRSurvey blankSurvey = template;
		    			blankSurvey.setQuestions(new ArrayList<SurveyQuestion>());// make blank survey
		    			sr.setResults(SurveyAction.updateSurveyResult(blankSurvey));
		    			String documentId = surveyResultDao.assignSurvey(sr);
		    			blankSurvey.setDocumentId(documentId);
		    			surveys.addSurvey(blankSurvey);
		    			specificSurveys = surveys.getSurveyListById(Integer.toString(st.getSurveyID())); //reload
		    		}
		    		else
		    		{
		    			return "redirect:/manage_patients";
		    		}
			}
	   		model.addAttribute("createPatientSuccessfully",true);
	   		loadPatientsAndVolunteers(model);
	   		
	        return "admin/manage_patients";
		}
		else
		{			
			model.addAttribute("misMatchedVolunteer",true);
			loadPatientsAndVolunteers(model);
			
			return "admin/manage_patients";
		}
	}
   	
   	@RequestMapping(value="/edit_patient/{id}", method=RequestMethod.GET)
	public String editPatientForm(@PathVariable("id") int patientID,SecurityContextHolderAwareRequestWrapper request, ModelMap model){   		
		Patient p = patientDao.getPatientByID(patientID);		
		model.addAttribute("patient", p);		
		
		List<Volunteer> volunteers = volunteerDao.getAllVolunteers();			
		model.addAttribute("volunteers", volunteers);	
		
		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)		
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "/admin/edit_patient"; //Why this one requires a slash when none of the others do, I do not know.
	}
   	@RequestMapping(value="/submit_edit_patient/{id}", method=RequestMethod.POST)
	public String modifyPatient(@PathVariable("id") int patientID, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model){
   		int vId1 = Integer.parseInt(request.getParameter("volunteer1"));
		int vId2 = Integer.parseInt(request.getParameter("volunteer2"));
		Volunteer v1 = volunteerDao.getVolunteerById(vId1);
		Volunteer v2 = volunteerDao.getVolunteerById(vId2);
		
		if (Utils.isMatchVolunteer(v1, v2)){
			Patient p = new Patient();
			p.setPatientID(patientID);
			p.setFirstName(request.getParameter("firstname"));
			p.setLastName(request.getParameter("lastname"));
			p.setPreferredName(request.getParameter("preferredname"));		
			p.setVolunteer(vId1);
			p.setPartner(vId2);
			p.setGender(request.getParameter("gender"));
			p.setNotes(request.getParameter("notes"));
			p.setClinic(request.getParameter("clinic"));
			p.setAlerts(request.getParameter("alerts"));
			p.setMyoscarVerified(request.getParameter("myoscar_verified"));
			
//			String strAvailableTime = Utils.getAvailableTime(request);
//			p.setAvailability(strAvailableTime);
			
			patientDao.updatePatient(p);
			model.addAttribute("updatePatientSuccessfully",true);
		}
		else
			model.addAttribute("misMatchedVolunteer",true);		
		
		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)		
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
        loadPatientsAndVolunteers(model);
        
		return "/admin/manage_patients";
	}
   	
   	
   	@RequestMapping(value="/remove_patient/{patient_id}", method=RequestMethod.GET)
	public String removePatient(@PathVariable("patient_id") int id){
		patientDao.removePatientWithId(id);
		return "redirect:/manage_patients";
	}

	@RequestMapping(value="/patient/{patient_id}", method=RequestMethod.GET)
	public String viewPatient(@PathVariable("patient_id") int id, @RequestParam(value="complete", required=false)
					String completedSurvey, @RequestParam(value="aborted", required=false) String inProgressSurvey, 
					@RequestParam(value="appointmentId", required=false) Integer appointmentId, 
					SecurityContextHolderAwareRequestWrapper request, ModelMap model){
				
		Patient patient = patientDao.getPatientByID(id);
		//Find the name of the current user
		User u = Utils.getLoggedInUser(request);
		HttpSession session = request.getSession();
		
		int volunteerId =0;
		if (session.getAttribute("logged_in_volunteer") != null)
			volunteerId = Integer.parseInt(session.getAttribute("logged_in_volunteer").toString());
		else
			volunteerId = Utils.getVolunteerByLoginUser(request, volunteerDao);		
		//Make sure that the user is actually responsible for the patient in question
		int volunteerForPatient = patient.getVolunteer();
		if (!(volunteerId == patient.getVolunteer()) && !(volunteerId == patient.getPartner()))
		{
			String loggedInUser = u.getName();
			model.addAttribute("loggedIn", loggedInUser);
			model.addAttribute("patientOwner", volunteerForPatient);
			return "redirect:/403";
		}
		model.addAttribute("patient", patient);
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));		

		ArrayList<SurveyResult> completedSurveyResultList = surveyResultDao.getCompletedSurveysByPatientID(id);
		ArrayList<SurveyResult> incompleteSurveyResultList = surveyResultDao.getIncompleteSurveysByPatientID(id);
		Collections.sort(completedSurveyResultList);
		Collections.sort(incompleteSurveyResultList);
		ArrayList<SurveyTemplate> surveyList = surveyTemplateDao.getAllSurveyTemplates();		
	
		ArrayList<Patient> patientsForUser = patientDao.getPatientsForVolunteer(volunteerId);						
		Appointment appointment = appointmentDao.getAppointmentById(appointmentId);
		
		model.addAttribute("appointment", appointment);
		model.addAttribute("patients", patientsForUser);
		model.addAttribute("completedSurveys", completedSurveyResultList);
		model.addAttribute("inProgressSurveys", incompleteSurveyResultList);
		model.addAttribute("surveys", surveyList);
//		ArrayList<Picture> pics = pictureDao.getPicturesForPatient(id);
//		model.addAttribute("pictures", pics);
		
		//user logs
		StringBuffer sb = new StringBuffer();
		sb.append(u.getName());
		sb.append(" viewing patient: ");
		
		if(patient.getPreferredName() != null && patient.getPreferredName() != "")
			sb.append(patient.getPreferredName());
		else 
			sb.append(patient.getDisplayName());
		
		activityDao.addUserLog(sb.toString(), u);					
		//save selected appointmentId in the session for other screen, like narrative		
		session.setAttribute("appointmentId", appointmentId);
		session.setAttribute("patientId", id);
				
		return "/patient";
	}
	
	@RequestMapping(value="/view_clients_admin", method=RequestMethod.GET)
	public String viewPatientsFromAdmin(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		HttpSession session = request.getSession();
		
		List<Patient> patients = MisUtils.getAllPatientsWithFullInfos(patientDao, request);
		model.addAttribute("patients", patients);
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "/admin/view_clients";
	}
	
	//display all patients by search name
	@RequestMapping(value="/view_clients_admin", method=RequestMethod.POST)
	public String viewPatientsBySelectedName(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		String name = request.getParameter("searchName");
		HttpSession session = request.getSession();
		List<Patient> patients = new ArrayList<Patient>();
		
		patients = patientDao.getPatientssByPartialName(name);			
		model.addAttribute("searchName", name);	 
		model.addAttribute("patients", patients);
		
		if (session.getAttribute("unread_messages") != null)		
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		return "/admin/view_clients";		
	}
	
	@RequestMapping(value="/display_client/{patient_id}",method=RequestMethod.GET)
	public String displayPatientDetails(@PathVariable("patient_id") int id, SecurityContextHolderAwareRequestWrapper
			request, ModelMap model){
		Patient patient = new Patient();
		List<Patient> patients  = MisUtils.getAllPatientsWithFullInfos(patientDao, request);
		for (Patient p: patients)
		{
			if (id == p.getPatientID())
			{
				patient = p;
				break;
			}
		}				
		model.addAttribute("patient", patient);
		
		int totalSurveys = surveyTemplateDao.countSurveyTemplate();
		int totalCompletedSurveys = surveyResultDao.countCompletedSurveys(id);
		
		if (totalSurveys == totalCompletedSurveys)
			model.addAttribute("showReport", true);

		String volunteer1Name = volunteerDao.getVolunteerById(patient.getVolunteer()).getDisplayName();
		String volunteer2Name = volunteerDao.getVolunteerById(patient.getPartner()).getDisplayName();
		
		model.addAttribute("volunteer1", volunteer1Name);
		model.addAttribute("volunteer2", volunteer2Name);
				
		List<Appointment> appointments = new ArrayList<Appointment>();
		appointments = appointmentDao.getAllUpcommingAppointmentForPatient(id);
				
		model.addAttribute("upcomingVisits", appointments);
		
		appointments = new ArrayList<Appointment>();		
		appointments = appointmentDao.getAllCompletedAppointmentsForPatient(id);
		model.addAttribute("completedVisits", appointments);
		
		List<SurveyResult> surveys = surveyResultDao.getSurveysByPatientID(id);
		model.addAttribute("surveys", surveys);
		
		HttpSession session = request.getSession();				
 		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));				
		return "/admin/display_client";
	}
	
	private List<Patient> getAllPatients(){
		List<Patient> patients = new ArrayList<Patient>();
		patients = patientDao.getAllPatients();
		
		return patients;
	}
	
	private void loadPatientsAndVolunteers(ModelMap model){
		List<Volunteer> volunteers = volunteerDao.getAllVolunteers();		
		model.addAttribute("volunteers", volunteers);
		
	    List<Patient> patientList = getAllPatients();
        model.addAttribute("patients", patientList);
	}

}
