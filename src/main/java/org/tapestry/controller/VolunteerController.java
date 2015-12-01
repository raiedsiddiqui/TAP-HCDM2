package org.tapestry.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tapestry.utils.TapestryHelper;
import org.tapestry.utils.Utils;
import org.tapestry.hl7.Hl7Utils;
import org.tapestry.objects.Activity;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Availability;
import org.tapestry.objects.DisplayedSurveyResult;
import org.tapestry.objects.HL7Report;
import org.tapestry.objects.Message;
import org.tapestry.objects.Narrative;
import org.tapestry.objects.Patient;
import org.tapestry.objects.Site;
import org.tapestry.objects.SurveyResult;
import org.tapestry.objects.SurveyTemplate;
import org.tapestry.objects.User;
import org.tapestry.objects.Volunteer;
import org.tapestry.objects.Organization;
import org.tapestry.service.AppointmentManager;
import org.tapestry.service.MessageManager;
import org.tapestry.service.OrganizationManager;
import org.tapestry.service.PatientManager;
import org.tapestry.service.SurveyManager;
import org.tapestry.service.UserManager;
import org.tapestry.service.VolunteerManager;
import org.tapestry.surveys.ResultParser;

import ca.uhn.hl7v2.HL7Exception;

@Controller
public class VolunteerController {
	protected static Logger logger = Logger.getLogger(VolunteerController.class);		
	@Autowired
   	private UserManager userManager;
	@Autowired
	private MessageManager messageManager;
	@Autowired
	private AppointmentManager appointmentManager;
	@Autowired
	private VolunteerManager volunteerManager;
	@Autowired
	private PatientManager patientManager;
	@Autowired
	private SurveyManager surveyManager;
	@Autowired
	private OrganizationManager organizationManager;
   	
	//display all volunteers
	@RequestMapping(value="/view_volunteers", method=RequestMethod.GET)
	public String getAllVolunteers(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{		
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		HttpSession  session = request.getSession();	
		User user = TapestryHelper.getLoggedInUser(request, userManager);
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
//		if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole()))
//			volunteers = volunteerManager.getAllVolunteersWithCanDelete();	//For central Admin		
//		else	
//			volunteers = volunteerManager.getAllVolunteersWithCanDeleteByOrganization(user.getOrganization());	// for local Admin
		
		if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole()))
			volunteers = volunteerManager.getAllVolunteers();
		else
			volunteers = volunteerManager.getAllVolunteersByOrganization(user.getOrganization());	// for local Admin
		
		model.addAttribute("volunteers", volunteers);	
		
		if (session.getAttribute("volunteerMessage") != null)
		{
			String message = session.getAttribute("volunteerMessage").toString();
			
			if ("C".equals(message)){
				model.addAttribute("volunteerCreated", true);
				session.removeAttribute("volunteerMessage");
			}
			else if ("D".equals(message)){
				model.addAttribute("volunteerDeleted", true);
				session.removeAttribute("volunteerMessage");
			}
			else if ("U".equals(message)){
				model.addAttribute("volunteerUpdate", true);
				session.removeAttribute("volunteerMessage");
			}			
		}		
		return "/admin/view_volunteers";
	}
	
	//display all volunteers with search criteria
	@RequestMapping(value="/view_volunteers", method=RequestMethod.POST)
	public String viewFilteredVolunteers(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		User user = TapestryHelper.getLoggedInUser(request, userManager);		
		String name = request.getParameter("searchName");
		
		if(!Utils.isNullOrEmpty(name)) {
			if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole()))
				volunteers = volunteerManager.getVolunteersByName(name);	//For central Admin		
			else		
				volunteers = volunteerManager.getGroupedVolunteersByName(name, user.getOrganization());		//local Admin/VC			
		} 		
		
		model.addAttribute("searchName", name);
		model.addAttribute("volunteers", volunteers);
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		return "/admin/view_volunteers";
	}
	
	@RequestMapping(value="/new_volunteer", method=RequestMethod.GET)
	public String newVolunteer(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		//save exist user names for validating on UI
		List<String> existUserNames = new ArrayList<String>();
		existUserNames = volunteerManager.getAllExistUsernames();
		model.addAttribute("existUserNames", existUserNames);
		
		List<Organization> organizations = new ArrayList<Organization>();
		HttpSession session = request.getSession();
		User logginUser = TapestryHelper.getLoggedInUser(request);
		
		if (session.getAttribute("organizations") != null)
			organizations = (List<Organization>) session.getAttribute("organizations");		
		else 
		{
			if (request.isUserInRole("ROLE_ADMIN"))
				organizations = volunteerManager.getAllOrganizations();
			else
			{
				int organizationId = logginUser.getOrganization();
				Organization organization = volunteerManager.getOrganizationById(organizationId);
				organizations.add(organization);
			}				
			session.setAttribute("organizations", organizations);
		}
		model.addAttribute("organizations", organizations);
		
		List<Site> sites = organizationManager.getAllSites();
		model.addAttribute("sites", sites);
		
		if (request.isUserInRole("ROLE_ADMIN"))		
			model.addAttribute("showSites", true);
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "/admin/add_volunteer";
	}
		
	//display detail of a volunteer
	@RequestMapping(value="/display_volunteer/{volunteerId}", method=RequestMethod.GET)
	public String displayVolunteer(SecurityContextHolderAwareRequestWrapper request, 
				@PathVariable("volunteerId") int id, ModelMap model)
	{		
		//get volunteer by id
		Volunteer volunteer = new Volunteer();
		volunteer = volunteerManager.getVolunteerById(id);
		
		//set address
		StringBuffer sb = new StringBuffer();
		if (!Utils.isNullOrEmpty(volunteer.getAptNumber()))
		{
			sb.append("# ");
			sb.append(volunteer.getAptNumber());
			sb.append(" ,");
		}
			
		if (!Utils.isNullOrEmpty(volunteer.getStreetNumber()))
			sb.append(volunteer.getStreetNumber());
		sb.append(" ");
		
		if (!Utils.isNullOrEmpty(volunteer.getStreet()))
			sb.append(volunteer.getStreet());
		sb.append(",");
		
		volunteer.setAddress(sb.toString());				
		model.addAttribute("volunteer", volunteer);
		
		//set availability
		if (!Utils.isNullOrEmpty(volunteer.getAvailability()))
			TapestryHelper.saveAvailability(volunteer.getAvailability(),model);		
		
		//get all completed appointments
		List<Appointment> appointments = new ArrayList<Appointment>();		
		appointments = appointmentManager.getAllCompletedAppointmentsForVolunteer(id);	
		model.addAttribute("completedVisits", appointments);
		
		//get all upcoming appointments
		appointments = new ArrayList<Appointment>();		
		appointments = appointmentManager.getAllUpcomingAppointmentsForVolunteer(id);
		
		if (appointments.size() > 0)
			model.addAttribute("upcomingVisits", appointments);
		else
			model.addAttribute("hasNoUpComingVisits", true);
		
		List<SurveyResult> surveys = surveyManager.getVolunteerSurveyResultsByVolunteerId(id);
		model.addAttribute("surveys", surveys);
		
		//get all activities
		List<Activity> activities = volunteerManager.getActivitiesForVolunteer(id);	
		if (activities != null && activities.size() > 0)
		{
			model.addAttribute("showActivityLogs", true);
			model.addAttribute("activityLogs", activities);
		}
		
		//get all messages		
		List<Message> messages = messageManager.getAllMessagesForRecipient(TapestryHelper.getLoggedInUser(request).getUserID());
		if (messages != null && messages.size() > 0)
		{
			model.addAttribute("showMessage", true);
			model.addAttribute("messages", messages);	
		}
		
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "/admin/display_volunteer";
	}	
	
	//create a new volunteer and save in the table of volunteers and users in the DB
	@RequestMapping(value="/add_volunteer", method=RequestMethod.POST)
	public String addVolunteer(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		String username = request.getParameter("username").trim();		
		List<String> uList = volunteerManager.getAllExistUsernames();
		
		if (uList.contains(username))
		{
			model.addAttribute("userNameExist", true);			
			return "/admin/add_volunteer";						
		}
		else
		{			
			Volunteer volunteer = new Volunteer();			
			volunteer.setFirstName(request.getParameter("firstname").trim());
			volunteer.setLastName(request.getParameter("lastname").trim());
			volunteer.setGender(request.getParameter("gender"));
			volunteer.setEmail(request.getParameter("email").trim());				
	
			//set experience level based on score calculation
			String score = request.getParameter("totalcalculated");
			if (!Utils.isNullOrEmpty(score))
			{
				double iScore = Double.parseDouble(score);		
				if (iScore >= 0.85)
					volunteer.setExperienceLevel("E");	
				else if (iScore <0.85 && iScore >= 0.55)
					volunteer.setExperienceLevel("I");	
				else
					volunteer.setExperienceLevel("B");	
			}else
				volunteer.setExperienceLevel("B");	
			
			volunteer.setTotalVLCScore(Double.parseDouble(request.getParameter("totalVLCScore")));
			volunteer.setAvailabilityPerMonth(Double.parseDouble(request.getParameter("availabilityPerMonthe")));
			volunteer.setNumYearsOfExperience(Double.parseDouble(request.getParameter("numberYearsOfExperience")));
			volunteer.setTechnologySkillsScore(Double.parseDouble(request.getParameter("technologySkillsScore")));
			volunteer.setPerceptionOfOlderAdultsScore(Double.parseDouble(request.getParameter("perceptionOfOlderAdultScore")));				
			volunteer.setProvince(request.getParameter("province"));
			volunteer.setCountry(request.getParameter("country"));	
			volunteer.setAptNumber(request.getParameter("aptnum"));
			volunteer.setStreet(request.getParameter("street"));
			volunteer.setStreetNumber(request.getParameter("streetnum"));
			volunteer.setCity(request.getParameter("city").trim());		
			volunteer.setHomePhone(request.getParameter("homephone").trim());
			volunteer.setCellPhone(request.getParameter("cellphone"));
			volunteer.setEmergencyContact(request.getParameter("emergencycontact").trim());
			volunteer.setEmergencyPhone(request.getParameter("emergencyphone").trim());
			volunteer.setPostalCode(request.getParameter("postalcode"));
			volunteer.setNotes(request.getParameter("notes"));
			volunteer.setOrganizationId(Integer.valueOf(request.getParameter("organization")));					
			volunteer.setvLCID(Integer.valueOf(request.getParameter("vlcId")));	
			
			volunteer.setBackground(request.getParameter("background"));
			volunteer.setVolExperience(request.getParameter("volExperience"));
			volunteer.setTechComfort(Integer.valueOf(request.getParameter("techComfort")));
			volunteer.setLanguage(Integer.valueOf(request.getParameter("language")));
			volunteer.setApplicationCompleted(Integer.valueOf(request.getParameter("applicationCompleted")));
			volunteer.setInterviewCompleted(Integer.valueOf(request.getParameter("interviewCompleted")));
			volunteer.setReferenceCompleted(Integer.valueOf(request.getParameter("referenceCompleted")));
			volunteer.setPoliceCompleted(Integer.valueOf(request.getParameter("policeCompleted")));
			volunteer.setTbCompleted(Integer.valueOf(request.getParameter("tbCompleted")));
			volunteer.setVlcCompleted(Integer.valueOf(request.getParameter("vlcCompleted")));
			volunteer.setConfidentialityCompleted(Integer.valueOf(request.getParameter("confidentialityCompleted")));
			volunteer.setVolagreementCompleted(Integer.valueOf(request.getParameter("volagreementCompleted")));
			volunteer.setPhotoReceived(Integer.valueOf(request.getParameter("photoReceived")));
			volunteer.setCurrentStatus(Integer.valueOf(request.getParameter("currentStatus")));
			volunteer.setReasonInactivity(request.getParameter("reasonInactivity"));
			
			
			volunteer.setUserName(request.getParameter("username").trim());		
			ShaPasswordEncoder enc = new ShaPasswordEncoder();
			String hashedPassword = enc.encodePassword(request.getParameter("password"), null);			
			volunteer.setPassword(hashedPassword);
						
			String date = request.getParameter("interviewDate");			
			if (Utils.isNullOrEmpty(date))				
				volunteer.setInterviewDate("1900-01-01");
			else
				volunteer.setInterviewDate(date);
			
			volunteer.setStatus(request.getParameter("status"));
			
			date = request.getParameter("dob");
			if (Utils.isNullOrEmpty(date))				
				volunteer.setdOB("1900-01-01");
			else
				volunteer.setdOB(date);
			
			volunteer.setReference1(request.getParameter("reference1"));
			volunteer.setReference2(request.getParameter("reference2"));
			
			date = request.getParameter("rDate");
			if (Utils.isNullOrEmpty(date))				
				volunteer.setReferenceCheckDate("1900-01-01");
			else
				volunteer.setReferenceCheckDate(date);
			
			date = request.getParameter("pCheckDate");
			if (Utils.isNullOrEmpty(date))				
				volunteer.setPoliceCheckDate("1900-01-01");
			else
				volunteer.setPoliceCheckDate(date);
			
			date = request.getParameter("tbTDate");
			if (Utils.isNullOrEmpty(date))				
				volunteer.settBTestDate("1900-01-01");
			else
				volunteer.settBTestDate(date);
			
			date = request.getParameter("vCDate");
			if (Utils.isNullOrEmpty(date))				
				volunteer.setvLCCompletionDate("1900-01-01");
			else
				volunteer.setvLCCompletionDate(date);
			
			date = request.getParameter("cAgreementDate");
			if (Utils.isNullOrEmpty(date))				
				volunteer.setcAgreementDate("1900-01-01");
			else
				volunteer.setcAgreementDate(date);
			
			date = request.getParameter("vAgreementDate");
			if (Utils.isNullOrEmpty(date))				
				volunteer.setvAgreementDate("1900-01-01");
			else
				volunteer.setvAgreementDate(date);
			
			date = request.getParameter("pDate");
			if (Utils.isNullOrEmpty(date))				
				volunteer.setPhotoDate("1900-01-01");
			else
				volunteer.setPhotoDate(date);
			volunteer.setSource(request.getParameter("source"));				
			
			date = request.getParameter("aCompleteDate");
			if (Utils.isNullOrEmpty(date))				
				volunteer.setApplicationFormCompletionDate("1900-01-01");
			else
				volunteer.setApplicationFormCompletionDate(date);
			
			String strAvailableTime = TapestryHelper.getAvailableTime(request);
			volunteer.setAvailability(strAvailableTime);
			//save a volunteer in the table volunteers
			boolean success = volunteerManager.addVolunteer(volunteer);				
			
	   		User loggedInUser = TapestryHelper.getLoggedInUser(request);
			StringBuffer sb = new StringBuffer();
			sb.append(loggedInUser.getName());
			sb.append(" has added an new volunteer, ");
			sb.append(volunteer.getFirstName());
			sb.append(" ");
			sb.append(volunteer.getLastName());
			userManager.addUserLog(sb.toString(), loggedInUser);
			//save in the Users table
			if (success)
			{
				User user = new User();
				sb = new StringBuffer();
				sb.append(volunteer.getFirstName());
				sb.append(" ");
				sb.append(volunteer.getLastName());
				user.setName(sb.toString());
				user.setUsername(volunteer.getUserName());
				user.setRole("ROLE_USER");
						
				user.setPassword(volunteer.getPassword());
				user.setEmail(volunteer.getEmail());
				user.setOrganization(volunteer.getOrganizationId());
				int site = loggedInUser.getSite();
				if (site == 0)//login user is central admin
					site = Integer.valueOf(request.getParameter("site"));
				System.out.println("site = " +site);
				user.setSite(site);
//				user.setSite(Integer.valueOf(request.getParameter("site")));
				
				success = userManager.createUser(user);				
			}
			else{
				model.addAttribute("volunteerExist", true);
				return "/admin/add_volunteer";	
			}			
			//set displayed message information 
			HttpSession session = request.getSession();
			session.setAttribute("volunteerMessage", "C");
			
			return "redirect:/view_volunteers";
		}
	}	
	
	@RequestMapping(value="/modify_volunteer/{volunteerId}", method=RequestMethod.GET)
	public String modifyVolunteer(SecurityContextHolderAwareRequestWrapper request, 
			@PathVariable("volunteerId") int id, ModelMap model)
	{		
		Volunteer volunteer = volunteerManager.getVolunteerById(id);		
				
		List<Organization> organizations = new ArrayList<Organization>();
		HttpSession session = request.getSession();
		
		if (session.getAttribute("organizations") != null)
			organizations = (List<Organization>) session.getAttribute("organizations");
		else 
		{
			if (request.isUserInRole("ROLE_ADMIN"))
				organizations = volunteerManager.getAllOrganizations();
			else
			{
				int organizationId = TapestryHelper.getLoggedInUser(request, userManager).getOrganization();
				Organization organization = volunteerManager.getOrganizationById(organizationId);
				organizations.add(organization);
			}				
			session.setAttribute("organizations", organizations);
		}
		model.addAttribute("organizations", organizations);	
		TapestryHelper.showVolunteerAvailability(volunteer, request, model);

		return "/admin/modify_volunteer";
	}
	
	@RequestMapping(value="/update_volunteer/{volunteerId}", method=RequestMethod.POST)
	public String updateVolunteer(SecurityContextHolderAwareRequestWrapper request, 
			@PathVariable("volunteerId") int id, ModelMap model)
	{		
		HttpSession  session = request.getSession();
		Volunteer volunteer;			
		volunteer = volunteerManager.getVolunteerById(id);				
		
		if (!Utils.isNullOrEmpty(request.getParameter("firstname")))
			volunteer.setFirstName(request.getParameter("firstname"));
		
		if (!Utils.isNullOrEmpty(request.getParameter("lastname")))
			volunteer.setLastName(request.getParameter("lastname"));			
		//set encoded password for security
//		ShaPasswordEncoder enc = new ShaPasswordEncoder();
//		String hashedPassword = enc.encodePassword(request.getParameter("password"), null);		
//		volunteer.setPassword(hashedPassword);
		volunteer.setEmail(request.getParameter("email"));	
//		volunteer.setExperienceLevel((request.getParameter("level")));	
		volunteer.setStreet(request.getParameter("street"));
		volunteer.setCity(request.getParameter("city"));
		volunteer.setProvince(request.getParameter("province"));
		volunteer.setCountry(request.getParameter("country"));
		volunteer.setStreetNumber(request.getParameter("streetnum"));
		volunteer.setAptNumber(request.getParameter("aptnum"));
		volunteer.setPostalCode(request.getParameter("postalcode"));
		volunteer.setHomePhone(request.getParameter("homephone"));
		volunteer.setCellPhone(request.getParameter("cellphone"));
		volunteer.setEmergencyContact(request.getParameter("emergencycontact"));
		volunteer.setEmergencyPhone(request.getParameter("emergencyphone"));
		volunteer.setOrganizationId(Integer.valueOf(request.getParameter("organization")));
		volunteer.setvLCID(Integer.valueOf(request.getParameter("vlcId")));
		volunteer.setNotes(request.getParameter("notes"));
		volunteer.setGender(request.getParameter("gender"));
		volunteer.setTotalVLCScore(Double.parseDouble(request.getParameter("totalVLCScore")));
		volunteer.setNumYearsOfExperience(Double.parseDouble(request.getParameter("numberYearsOfExperience")));
		volunteer.setAvailabilityPerMonth(Double.parseDouble(request.getParameter("availabilityPerMonthe")));
		volunteer.setTechnologySkillsScore(Double.parseDouble(request.getParameter("technologySkillsScore")));
		volunteer.setPerceptionOfOlderAdultsScore(Double.parseDouble(request.getParameter("perceptionOfOlderAdultScore")));
		
		volunteer.setBackground(request.getParameter("background"));
		volunteer.setVolExperience(request.getParameter("volExperience"));
		volunteer.setTechComfort(Integer.valueOf(request.getParameter("techComfort")));
		
		volunteer.setLanguage(Integer.valueOf(request.getParameter("language")));
		volunteer.setApplicationCompleted(Integer.valueOf(request.getParameter("applicationCompleted")));
		volunteer.setInterviewCompleted(Integer.valueOf(request.getParameter("interviewCompleted")));
		
		volunteer.setReferenceCompleted(Integer.valueOf(request.getParameter("referenceCompleted")));
		volunteer.setPoliceCompleted(Integer.valueOf(request.getParameter("policeCompleted")));
		volunteer.setTbCompleted(Integer.valueOf(request.getParameter("tbCompleted")));
		
		volunteer.setVlcCompleted(Integer.valueOf(request.getParameter("vlcCompleted")));
		volunteer.setConfidentialityCompleted(Integer.valueOf(request.getParameter("confidentialityCompleted")));
		volunteer.setVolagreementCompleted(Integer.valueOf(request.getParameter("volagreementCompleted")));
		
		volunteer.setPhotoReceived(Integer.valueOf(request.getParameter("photoReceived")));
		volunteer.setCurrentStatus(Integer.valueOf(request.getParameter("currentStatus")));
		volunteer.setReasonInactivity(request.getParameter("reasonInactivity"));
		
		//set experience level based on score calculation
		String score = request.getParameter("totalcalculated");		
		if (!Utils.isNullOrEmpty(score))
		{
			double iScore = Double.parseDouble(score);		
			if (iScore >= 0.85)
				volunteer.setExperienceLevel("E");	
			else if (iScore <0.85 && iScore >= 0.55)
				volunteer.setExperienceLevel("I");	
			else
				volunteer.setExperienceLevel("B");	
		}
		else 
		{			
			String experienceLeve = request.getParameter("experience_level");
			
			if ("Beginer".equals(experienceLeve))
				volunteer.setExperienceLevel("B");	
			else if ("Intermediate".equals(experienceLeve))
				volunteer.setExperienceLevel("I");	
			else
				volunteer.setExperienceLevel("E");
		}
				
		String date = request.getParameter("interviewDate");
		
		if (Utils.isNullOrEmpty(date))				
			volunteer.setInterviewDate("1900-01-01");
		else
			volunteer.setInterviewDate(date);
		
		volunteer.setStatus(request.getParameter("status"));
		
		date = request.getParameter("dob");
		if (Utils.isNullOrEmpty(date))				
			volunteer.setdOB("1900-01-01");
		else
			volunteer.setdOB(date);
		
		volunteer.setReference1(request.getParameter("reference1"));
		volunteer.setReference2(request.getParameter("reference2"));
		
		date = request.getParameter("rDate");
		if (Utils.isNullOrEmpty(date))				
			volunteer.setReferenceCheckDate("1900-01-01");
		else
			volunteer.setReferenceCheckDate(date);
		
		date = request.getParameter("pCheckDate");
		if (Utils.isNullOrEmpty(date))				
			volunteer.setPoliceCheckDate("1900-01-01");
		else
			volunteer.setPoliceCheckDate(date);
		
		date = request.getParameter("tbTDate");
		if (Utils.isNullOrEmpty(date))				
			volunteer.settBTestDate("1900-01-01");
		else
			volunteer.settBTestDate(date);
		
		date = request.getParameter("vCDate");
		if (Utils.isNullOrEmpty(date))				
			volunteer.setvLCCompletionDate("1900-01-01");
		else
			volunteer.setvLCCompletionDate(date);
		
		date = request.getParameter("cAgreementDate");
		if (Utils.isNullOrEmpty(date))				
			volunteer.setcAgreementDate("1900-01-01");
		else
			volunteer.setcAgreementDate(date);
		
		date = request.getParameter("vAgreementDate");
		if (Utils.isNullOrEmpty(date))				
			volunteer.setvAgreementDate("1900-01-01");
		else
			volunteer.setvAgreementDate(date);
		
		date = request.getParameter("pDate");
		if (Utils.isNullOrEmpty(date))				
			volunteer.setPhotoDate("1900-01-01");
		else
			volunteer.setPhotoDate(date);
		volunteer.setSource(request.getParameter("source"));				
		
		date = request.getParameter("aCompleteDate");
		if (Utils.isNullOrEmpty(date))				
			volunteer.setApplicationFormCompletionDate("1900-01-01");
		else
			volunteer.setApplicationFormCompletionDate(date);
		
		String strAvailableTime = TapestryHelper.getAvailableTime(request);				
		volunteer.setAvailability(strAvailableTime);
			
		volunteerManager.updateVolunteer(volunteer);
			
		//update users table as well
		TapestryHelper.modifyUser(volunteer, userManager);
		//add logs
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has modified the volunteer, ");
		sb.append(volunteer.getFirstName());
		sb.append(" ");
		sb.append(volunteer.getLastName());
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		session.setAttribute("volunteerMessage","U");
		return "redirect:/view_volunteers";	
	}
	
	@RequestMapping(value="/delete_volunteer/{volunteerId}", method=RequestMethod.GET)
	public String deleteVolunteerById(@PathVariable("volunteerId") int id, 
				SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{		
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		Volunteer volunteer = volunteerManager.getVolunteerById(id);
		String username = volunteer.getUserName();
		User deletedVolunteer = userManager.getUserByUsername(username);
		volunteerManager.deleteVolunteerById(id);
		userManager.removeUserByUsername(username);
				
		//archive deleted volunteer
		String loggedInUserName = loggedInUser.getName();
		volunteerManager.archiveVolunteer(volunteer, loggedInUserName);
		userManager.archiveUser(deletedVolunteer, loggedInUserName);
				
		//add logs		
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has deleted the volunteer, ");
		sb.append(volunteer.getFirstName());	
		sb.append(" ");
		sb.append(volunteer.getLastName());
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		HttpSession  session = request.getSession();		
		session.setAttribute("volunteerMessage", "D");		
	
		return "redirect:/view_volunteers";		
	}
	
	@RequestMapping(value="/profile", method=RequestMethod.GET)
	public String viewProfile(@RequestParam(value="error", required=false) String errorsPresent, 
			@RequestParam(value="success", required=false) String success, @RequestParam(value="availability", required=false) 
			String a, SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{	
		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
		model.addAttribute("loggedInUserId", loggedInUser.getUserID());
		TapestryHelper.setUnreadMessage(request, model, messageManager);
	
		Volunteer volunteer = volunteerManager.getVolunteerById(TapestryHelper.getLoggedInVolunteerId(request));
		TapestryHelper.showVolunteerAvailability(volunteer, request, model);
		
		if (errorsPresent != null)
			model.addAttribute("errors", errorsPresent);
		if(success != null)
			model.addAttribute("success", true);
		if (a != null)
			model.addAttribute("availability", true);
		
//		List<Picture> pics = pictureManager.getPicturesForUser(loggedInUser.getUserID());
//		model.addAttribute("pictures", pics);
		return "/volunteer/profile";
	}
	
	@RequestMapping(value="/updateVolunteerAvailability/{volunteerId}", method=RequestMethod.POST)
	public String testingDoubleForm(@PathVariable("volunteerId") int id,SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{		
		String strAvailableTime = TapestryHelper.getAvailableTime(request);
		volunteerManager.updateVolunteerAvalability(id, strAvailableTime);
		
		//add log
		StringBuffer sb = new StringBuffer();
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		sb.append(loggedInUser.getName());
		sb.append(" has updated their availability");		
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		return "redirect:/profile?availability=true";
	}
	
	@RequestMapping(value="/volunteerList.html")
	@ResponseBody
	public List<Volunteer> getVolunteerByOrganization(@RequestParam(value="volunteerId") int vId)
	{
		Volunteer volunteer = volunteerManager.getVolunteerById(vId);	
		List<Volunteer> vl = volunteerManager.getAllVolunteersByOrganization(volunteer.getOrganizationId());
		
		for (Volunteer v: vl)
		{
			if (vId == v.getVolunteerId())
			{
				vl.remove(v);
				break;
			}
		}		
		return vl;
	}
		
	@RequestMapping(value="/authenticatePHRAjax")
	@ResponseBody
	public String authenticate111PHR(SecurityContextHolderAwareRequestWrapper request){		
				
		User user = TapestryHelper.getLoggedInUser(request, userManager);
   		int volId = TapestryHelper.getLoggedInVolunteerId(request);
   		int organizationId = user.getOrganization();
   		int patientId = TapestryHelper.getPatientId(request);
   		
   		//authenticate PHR for patient, set oscar_verified = 1
   		patientManager.authenticatePHRPatientByID(patientId);  
   		
   		//send message to local admin
   		List<User> coordinators = userManager.getVolunteerCoordinatorByOrganizationId(organizationId);   		
   		StringBuffer sb = new StringBuffer();
   		sb.append("[");
   		sb.append(volunteerManager.getVolunteerNameById(volId));
   		sb.append("] has verified [");
   		if (patientManager.getPatientByID(patientId) != null)
   			sb.append(patientManager.getPatientByID(patientId).getFirstName());
   			sb.append(' ');
   			sb.append(patientManager.getPatientByID(patientId).getLastName());
   		sb.append("] on [");
   		
   		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
   		sb.append(sdf.format(new Date()));
   		sb.append("] with a photo ID.");
   					
		if (coordinators != null)
		{	//send message to all coordinators in the organization						
			for (int i = 0; i<coordinators.size(); i++)		
				TapestryHelper.sendMessageToInbox("Authenticate PHR", sb.toString(), user.getUserID(), coordinators.get(i).getUserID(), messageManager);	
		}
		else{
			System.out.println("Can't find any coordinator in organization id# " + organizationId);
			logger.error("Can't find any coordinator in organization id# " + organizationId);
		}	
		
		return "send message to VC...";
	}
	
	//Activity in Volunteer
	//display all activity input by volunteers
	@RequestMapping(value="/view_activity_admin", method=RequestMethod.GET)
	public String viewActivityByAdmin(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{	
		List<Activity> activities = new ArrayList<Activity>();
		List<Volunteer> volunteers = new ArrayList<Volunteer>();		
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		
		if(request.isUserInRole("ROLE_ADMIN")) {
			activities = volunteerManager.getActivitiesForAdmin();
			volunteers = volunteerManager.getAllVolunteers();
		}
		else if (request.isUserInRole("ROLE_LOCAL_ADMIN"))
		{
			int organizationId = loggedInUser.getOrganization();
			activities = volunteerManager.getActivitiesForLocalAdmin(organizationId);			
			volunteers = volunteerManager.getAllVolunteersByOrganization(organizationId);
		}
			
		if (activities.size() == 0 )  
			model.addAttribute("emptyActivityLogs", true);			
		
		model.addAttribute("activityLogs", activities);	
		model.addAttribute("volunteers", volunteers);	
		
		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));		
			
		return "/admin/view_activityLogs";
	}
		
	//display all activity logs for selected volunteer
	@RequestMapping(value="/view_activity_admin", method=RequestMethod.POST)
	public String viewActivityBySelectedVolunteerByAdmin( SecurityContextHolderAwareRequestWrapper request,
			ModelMap model)
	{		
		String strVId = request.getParameter("search_volunteer");		
		List<Activity> activities = new ArrayList<Activity>();
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		
		if (!Utils.isNullOrEmpty(strVId))
		{
			activities = volunteerManager.getActivitiesForVolunteer(Integer.parseInt(strVId));					
			if (activities.size() == 0 )  
				model.addAttribute("emptyActivityLogs", true);				
		}		
		
		if(request.isUserInRole("ROLE_ADMIN")) 
			volunteers = volunteerManager.getAllVolunteers();
		else
		{
			User loggedInUser = TapestryHelper.getLoggedInUser(request);
			volunteers = volunteerManager.getAllVolunteersByOrganization(loggedInUser.getOrganization());
		}
			
		model.addAttribute("activityLogs", activities);	
		model.addAttribute("volunteers", volunteers);	
		model.addAttribute("selectedVolunteer", strVId);
		
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
			
		return "/admin/view_activityLogs";
	}
		
	@RequestMapping(value="/view_activity", method=RequestMethod.GET)
	public String viewActivityByVolunteer( SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		HttpSession  session = request.getSession();		
		int volunteerId = TapestryHelper.getLoggedInVolunteerId(request);	
		List<Activity> activities = new ArrayList<Activity>();
		activities = volunteerManager.getActivitiesForVolunteer(volunteerId);
					
		//check if there is message should be displayed
		if (session.getAttribute("ActivityMessage") != null)
		{
			String message = session.getAttribute("ActivityMessage").toString();
				
			if ("C".equals(message)){
				model.addAttribute("activityCreated", true);
				session.removeAttribute("ActivityMessage");
			}
			else if ("D".equals(message)){
				model.addAttribute("activityDeleted", true);
				session.removeAttribute("ActivityMessage");
			}
			else if ("U".equals(message)){
				model.addAttribute("activityUpdate", true);
				session.removeAttribute("ActivityMessage");
			}			
		}
		model.addAttribute("activityLogs", activities);	
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
			
		return "/volunteer/view_activityLogs";
	}
		
	@RequestMapping(value="/new_activity", method=RequestMethod.GET)
	public String newActivityByVolunteer(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{		
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		return "/volunteer/new_activityLogs";
	}
		
	@RequestMapping(value="/add_activity", method=RequestMethod.POST)
	public String addActivityByVolunteer(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{		
		HttpSession  session = request.getSession();		
		int volunteerId = TapestryHelper.getLoggedInVolunteerId(request);	
			
		Volunteer volunteer = volunteerManager.getVolunteerById(volunteerId);
		int organizationId = volunteer.getOrganizationId();
			
		String date = request.getParameter("activityDate");
		String startTime = request.getParameter("activityStartTime");	
		String endTime = request.getParameter("activityEndTime");	
		String description = request.getParameter("activityDesc");
			
		Activity activity = new Activity();
		activity.setDescription(description);
		activity.setVolunteer(String.valueOf(volunteerId));				
		activity.setDate(date);		
		activity.setOrganizationId(organizationId);
			
		//format start_Time and end_Time to match data type in DB
		StringBuffer sb = new StringBuffer();
		sb.append(date);
		sb.append(" ");
		sb.append(startTime);
		startTime = sb.toString();
		activity.setStartTime(startTime);
			
		sb = new StringBuffer();
		sb.append(date);
		sb.append(" ");
		sb.append(endTime);
		endTime = sb.toString();			
		activity.setEndTime(endTime);
			
		volunteerManager.logActivity(activity);
		//add log
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has added an activity ");		
		userManager.addUserLog(sb.toString(), loggedInUser);
			
		//update view activity page with new record		
		session.setAttribute("ActivityMessage", "C");		
		
		return "redirect:/view_activity";
	}	
		
	@RequestMapping(value="/delete_activity/{activityId}", method=RequestMethod.GET)
	public String deleteActivityById(@PathVariable("activityId") int id, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{			
		User loggedInUser = TapestryHelper.getLoggedInUser(request);		
		Activity activity = volunteerManager.getActivity(id);		
		volunteerManager.deleteActivity(id);	
		//archive acitvity		
		int volunteerId = Integer.valueOf(activity.getVolunteer());
		String volunteerName = volunteerManager.getVolunteerNameById(volunteerId);
		
		StringBuffer sb = new StringBuffer();
		String date = activity.getDate();
		sb.append(date);
		sb.append(" ");
		sb.append(activity.getStartTime());
		sb.append(":00");
		activity.setStartTime(sb.toString());
		
		sb = new StringBuffer();
		sb.append(date);
		sb.append(" ");
		sb.append(activity.getEndTime());
		sb.append(":00");
		activity.setEndTime(sb.toString());
				
		volunteerManager.archivedActivity(activity, loggedInUser.getName(), volunteerName);		
		//add logs		
		sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has deleted the acitiviy #  ");
		sb.append(id);		
		userManager.addUserLog(sb.toString(), loggedInUser);
					
		HttpSession  session = request.getSession();		
		session.setAttribute("ActivityMessage", "D");		
		
		return "redirect:/view_activity";		
	}
		
	@RequestMapping(value="/modify_activity/{activityId}", method=RequestMethod.GET)
	public String modifyActivityLog(SecurityContextHolderAwareRequestWrapper request, 
			@PathVariable("activityId") int id, ModelMap model)
	{
		Activity activity = new Activity();
		activity = volunteerManager.getActivity(id);			
		model.addAttribute("activityLog", activity);
		
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
			
		return "/volunteer/modify_activityLog";
	}
		
	@RequestMapping(value="/update_activity", method=RequestMethod.POST)
	public String updateActivityById(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		String activityId = null;
		int iActivityId = 0;		
		Activity activity = new Activity();
			
		HttpSession  session = request.getSession();		
		int volunteerId = TapestryHelper.getLoggedInVolunteerId(request);	
			
		if (!Utils.isNullOrEmpty(request.getParameter("activityId")))
		{
			activityId = request.getParameter("activityId");
			iActivityId = Integer.parseInt(activityId);
				
			activity = volunteerManager.getActivity(iActivityId);
			activity.setVolunteer(String.valueOf(volunteerId));
				
			String date = null;
			if(!Utils.isNullOrEmpty(request.getParameter("activityDate")))
				date = request.getParameter("activityDate");
			activity.setDate(date + " 00:00:00");
				
			String startTime = null;
			if (!Utils.isNullOrEmpty(request.getParameter("activityStartTime")))
				startTime = request.getParameter("activityStartTime");
									
			if (startTime.length() < 6)//format 
				activity.setStartTime(date +" " + startTime + ":00"); 
			else
				activity.setStartTime(date +" " + startTime); 
				
			String endTime = null;
			if (!Utils.isNullOrEmpty(request.getParameter("activityEndTime")))
				endTime = request.getParameter("activityEndTime");
							
			if (endTime.length() < 6)//format
				activity.setEndTime(date + " " + endTime + ":00");
			else
				activity.setEndTime(date + " " + endTime);
			
			String desc = null;
			desc = request.getParameter("activityDesc");
			if (!Utils.isNullOrEmpty(desc))
				activity.setDescription(desc);						
		}			
		volunteerManager.updateActivity(activity);
		
		//add logs
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has modified the acitiviy #  ");
		sb.append(activity.getActivityId());				
		userManager.addUserLog(sb.toString(), loggedInUser);
			
		session.setAttribute("ActivityMessage","U");
		return "redirect:/view_activity";	
	}
	
	//Organizations		
	@RequestMapping(value="/view_organizations", method=RequestMethod.GET)
	public String getOganizations(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{		
		List<Organization> organizations = new ArrayList<Organization>();
		HttpSession  session = request.getSession();	
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		organizations = volunteerManager.getOrganizations();		
		model.addAttribute("organizations", organizations);	
				
		if (session.getAttribute("organizatioMessage") != null)
		{
			String message = session.getAttribute("organizatioMessage").toString();
			
			if ("C".equals(message)){
				model.addAttribute("organizationCreated", true);
				session.removeAttribute("organizatioMessage");
			}	
			else if ("U".equals(message))
			{
				model.addAttribute("organizationUpdated", true);
				session.removeAttribute("organizatioMessage");
			}
			else if ("D".equals(message))
			{
				model.addAttribute("organizationDeleted", true);
				session.removeAttribute("organizatioMessage");
			}
			
		}		
		return "/admin/view_organizations";
	}

	//display all volunteers with search criteria
	@RequestMapping(value="/view_organizations", method=RequestMethod.POST)
	public String viewFilteredOrganizations(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		List<Organization> organizations = new ArrayList<Organization>();
		
		String name = request.getParameter("searchName");
		if(!Utils.isNullOrEmpty(name)) {
			organizations = volunteerManager.getOrganizationsByName(name);			
		} 		
		model.addAttribute("searchName", name);
		model.addAttribute("organizations", organizations);	
		
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		return "/admin/view_organizations";
	}
	
	@RequestMapping(value="/new_organization", method=RequestMethod.GET)
	public String newOrganization(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{	
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		return "/admin/add_organization";
	}
	
	//create a new volunteer and save in the table of volunteers and users in the DB
	@RequestMapping(value="/add_organization", method=RequestMethod.POST)
	public String addOrganization(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		Organization organization = new Organization();
			
		organization.setName(request.getParameter("name").trim());
		organization.setCity(request.getParameter("city"));
		organization.setPrimaryContact(request.getParameter("primaryContact"));
		organization.setPrimaryPhone(request.getParameter("primaryPhone"));
		organization.setSecondaryContact(request.getParameter("secondaryContact"));
		organization.setSecondaryPhone(request.getParameter("secondaryPhone"));
		organization.setStreetNumber(request.getParameter("streetNumber"));
		organization.setStreetName(request.getParameter("streetName"));
		organization.setPostCode(request.getParameter("postCode"));
		organization.setProvince(request.getParameter("province"));		
		organization.setCountry(request.getParameter("country"));
			
		HttpSession  session = request.getSession();
		if (volunteerManager.addOrganization(organization))
		{
			//add logs
			User loggedInUser = TapestryHelper.getLoggedInUser(request);
			StringBuffer sb = new StringBuffer();
			sb.append(loggedInUser.getName());
			sb.append(" has added a new Organization,  ");
			sb.append(request.getParameter("name"));				
			userManager.addUserLog(sb.toString(), loggedInUser);
						
			session.setAttribute("organizatioMessage", "C");							
		}
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		return "redirect:/view_organizations";			
	}	
	
	@RequestMapping(value="/modify_organization/{organizationId}", method=RequestMethod.GET)
	public String modifyOrganization(@PathVariable("organizationId") int id, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		Organization organization = volunteerManager.getOrganizationById(id);
		model.addAttribute("o", organization);
		
		return "/admin/modify_organization";
	}
	@RequestMapping(value="/update_organization/{organizationId}", method=RequestMethod.POST)
	public String updateOrganization(@PathVariable("organizationId") int id, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		Organization organization = volunteerManager.getOrganizationById(id);
		
		organization.setName(request.getParameter("name").trim());
		organization.setCity(request.getParameter("city"));
		organization.setPrimaryContact(request.getParameter("primaryContact"));
		organization.setPrimaryPhone(request.getParameter("primaryPhone"));
		organization.setSecondaryContact(request.getParameter("secondaryContact"));
		organization.setSecondaryPhone(request.getParameter("secondaryPhone"));
		organization.setStreetNumber(request.getParameter("streetNumber"));
		organization.setStreetName(request.getParameter("streetName"));
		organization.setPostCode(request.getParameter("postCode"));
		organization.setProvince(request.getParameter("province"));		
		organization.setCountry(request.getParameter("country"));
			
		HttpSession  session = request.getSession();
		
		volunteerManager.updateOrganization(organization);
		//add logs
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has modified the organization, ");
		sb.append(organization.getName());				
		userManager.addUserLog(sb.toString(), loggedInUser);
						
		session.setAttribute("organizatioMessage", "U");			
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "redirect:/view_organizations";			
	}
	
	@RequestMapping(value="/delete_organization/{organizationId}", method=RequestMethod.GET)
	public String deleteOrganizationById(@PathVariable("organizationId") int id, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		String loggedInUserName = loggedInUser.getName();
		Organization organization = volunteerManager.getOrganizationById(id);
		volunteerManager.deleteOrganizationById(id);
		
		//archive deleted organization
		volunteerManager.archiveOrganization(organization, loggedInUserName);
		
		HttpSession  session = request.getSession();		
		session.setAttribute("organizatioMessage", "D");	
		
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUserName);
		sb.append(" has deleted the organization, ");
		sb.append(organization.getName());	
		sb.append("at ");
		java.util.Date date= new java.util.Date();		
		sb.append(new Timestamp(date.getTime()));
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "redirect:/view_organizations";	
	}
	
	//==================== Appointment ==============================//
	@RequestMapping(value="/", method=RequestMethod.GET)
	//Note that messageSent is Boolean, not boolean, to allow it to be null
	public String welcome(@RequestParam(value="booked", required=false) Boolean booked, 
			@RequestParam(value="noMachedTime", required=false) String noMachedTime,
			@RequestParam(value="patientId", required=false) Integer patientId, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		int unreadMessages;			
		List<Appointment> remindingAppointments = new ArrayList<Appointment>();
		HttpSession session = request.getSession();
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
							
		if (request.isUserInRole("ROLE_USER"))
		{
			String username = loggedInUser.getUsername();			
			int userId = loggedInUser.getUserID();			
			//get volunteer Id from login user
			int volunteerId = volunteerManager.getVolunteerIdByUsername(username);		
		
			session.setAttribute("logged_in_volunteer", volunteerId);
			List<Patient> patientsForUser = patientManager.getPatientsForVolunteer(volunteerId);
			List<Message> announcements = messageManager.getAnnouncementsForUser(userId);
			
			List<Appointment> approvedAppointments = new ArrayList<Appointment>();
			List<Appointment> pendingAppointments = new ArrayList<Appointment>();
			List<Appointment> declinedAppointments = new ArrayList<Appointment>();
			if(patientId != null) 
			{				
				approvedAppointments = appointmentManager.getAllApprovedAppointmentsForPatient(patientId, volunteerId);
				pendingAppointments = appointmentManager.getAllPendingAppointmentsForPatient(patientId, volunteerId);
				declinedAppointments = appointmentManager.getAllDeclinedAppointmentsForPatient(patientId, volunteerId);
				
				Patient patient = patientManager.getPatientByID(patientId);
				model.addAttribute("patient", patient);				
				//set patientId in the session for other screen, like narratives 
				session.setAttribute("patientId", patientId);				
			} 
			else 
			{
				approvedAppointments = appointmentManager.getAllApprovedAppointmentsForVolunteer(volunteerId);				
				pendingAppointments = appointmentManager.getAllPendingAppointmentsForVolunteer(volunteerId);
				declinedAppointments = appointmentManager.getAllDeclinedAppointmentsForVolunteer(volunteerId);						
			}
			
			model.addAttribute("approved_appointments", approvedAppointments);
			model.addAttribute("pending_appointments", pendingAppointments);
			model.addAttribute("declined_appointments", declinedAppointments);
			model.addAttribute("name", loggedInUser.getName());
			model.addAttribute("patients", patientsForUser);
			
			unreadMessages = messageManager.countUnreadMessagesForRecipient(userId);
			model.addAttribute("unread", unreadMessages);	
			//save unreadMessages in sesion
			session.setAttribute("unread_messages", unreadMessages);
			
			model.addAttribute("announcements", announcements);
			if (booked != null)
				model.addAttribute("booked", booked);
			
			if (noMachedTime != null)
				model.addAttribute("noMachedTime", noMachedTime);
			
			remindingAppointments = appointmentManager.getRemindingAppointmentList(volunteerId, -2);			
			model.addAttribute("reminding_appointments", remindingAppointments);
				
			return "volunteer/index";
		}
		else if (request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_LOCAL_ADMIN"))
		{			
			unreadMessages = messageManager.countUnreadMessagesForRecipient(loggedInUser.getUserID());
			model.addAttribute("unread", unreadMessages);
			//save unreadMessages in sesion
			session.setAttribute("unread_messages", unreadMessages);
			
			model.addAttribute("name", loggedInUser.getName());
			
			remindingAppointments = appointmentManager.getRemindingAppointmentList(0, -2);			
			model.addAttribute("appointments", remindingAppointments);

			return "admin/index";
		}
		else if (request.isUserInRole("ROLE_CLIENT"))
		{
			Patient patient = patientManager.getPatientByUserId(loggedInUser.getUserID());
			model.addAttribute("patient", patient);
			
			int pId = patient.getPatientID();
			List<SurveyResult> completedSurveyResultList = surveyManager.getCompletedSurveysByPatientID(pId);
			List<SurveyResult> incompleteSurveyResultList = surveyManager.getIncompleteSurveysByPatientID(pId);
			
			Collections.sort(completedSurveyResultList);
			Collections.sort(incompleteSurveyResultList);		
		
			//get display survey result for displaying question text and answer
			String xml;
			LinkedHashMap<String, String> res;
			List<DisplayedSurveyResult> displayedResults;
			List<DisplayedSurveyResult> completedDisplayedResults = new ArrayList<DisplayedSurveyResult>();
		
			for (SurveyResult sr: completedSurveyResultList)
			{
		   		try{
		   			xml = new String(sr.getResults(), "UTF-8");
		   		} catch (Exception e) {
		   			xml = "";
		   		}
		   		res = ResultParser.getResults(xml);
		   		displayedResults = ResultParser.getDisplayedSurveyResults(res);
		   		completedDisplayedResults.addAll(displayedResults);
			}
			//translate answers with full detailed information
			int site = loggedInUser.getSite();
			completedDisplayedResults = TapestryHelper.getDetailedAnswerForSurveys(completedDisplayedResults, site);
			
			model.addAttribute("completedSurveys", completedSurveyResultList);
			model.addAttribute("inProgressSurveys", incompleteSurveyResultList);
			model.addAttribute("displayResults", completedDisplayedResults);
			return "client/client-surveys";
		}
//		else if (request.isUserInRole("ROLE_CLINICIAN"))
//		{
//			return "redirect:/manage_surveys";
//		}
		else
		{ //This should not happen, but catch any unforseen behavior and logout			
			return "redirect:/login";
		}
	}
   	
   	@RequestMapping(value="/manage_appointments", method=RequestMethod.GET)
   	public String manageAppointments(@RequestParam(value="success", required=false) Boolean appointmentBooked,
   			@RequestParam(value="noMachedTime", required=false) String noMatchedMsg,
   			SecurityContextHolderAwareRequestWrapper request, ModelMap model)
   	{
   		User user = TapestryHelper.getLoggedInUser(request, userManager);
   		List<Appointment> allUpcomingAppointments, allPastAppointments, allPendingAppointments;
   		List<Patient> allPatients;
   		if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole())){//For central Admin
   			allUpcomingAppointments = appointmentManager.getAllUpcomingAppointments(); 
   			allPatients = patientManager.getAllPatients(); 
   			allPastAppointments = appointmentManager.getAllPastAppointments();   			   			
   			allPendingAppointments = appointmentManager.getAllPendingAppointments();   			
   		}
   		else {//For local Admin
   			int organizationId = user.getOrganization();     			
   			allUpcomingAppointments = appointmentManager.getUpcomingAppointmentsGroupByOrganization(organizationId);     			 			
   			allPastAppointments = appointmentManager.getPastAppointmentsGroupByOrganization(organizationId);
   			allPendingAppointments = appointmentManager.getPendingAppointmentsGroupByOrganization(organizationId);   			
   			allPatients = patientManager.getPatientsBySite(user.getSite());   			   	
   		}
   
		model.addAttribute("upcomingAppointments", allUpcomingAppointments);
   		model.addAttribute("pastAppointments", allPastAppointments);   		
   		model.addAttribute("pendingAppointments", allPendingAppointments);   		
   		model.addAttribute("patients", allPatients);
   		
   		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
   		
   		if(appointmentBooked != null)
   			model.addAttribute("success", appointmentBooked);
   		
   		if (noMatchedMsg != null)
   			model.addAttribute("noMachedTime", noMatchedMsg); 
   		
   		return "admin/manage_appointments";
   	}
   	
   	@RequestMapping(value="/authenticatePHR", method=RequestMethod.GET)
   	public String authenticatePHR(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
   	{
   		User user = TapestryHelper.getLoggedInUser(request, userManager);
   		int volId = TapestryHelper.getLoggedInVolunteerId(request);
   		int organizationId = user.getOrganization();
   		int patientId = TapestryHelper.getPatientId(request);
   		
   		//authenticate PHR for patient, set oscar_verified = 1
   		patientManager.authenticatePHRPatientByID(patientId);  
   		
   		//send message to local admin
   		List<User> coordinators = userManager.getVolunteerCoordinatorByOrganizationId(organizationId);   		
   		StringBuffer sb = new StringBuffer();
   		sb.append("[");
   		sb.append(volunteerManager.getVolunteerNameById(volId));
   		sb.append("] has verified [");
   		String patientName = patientManager.getPatientByID(patientId).getFirstName();
   		if (patientManager.getPatientByID(patientId) != null)
   			sb.append(patientName);
   			sb.append(' ');
			sb.append(patientManager.getPatientByID(patientId).getLastName());
   		sb.append("] on [");
   		
   		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
   		sb.append(sdf.format(new Date()));
   		sb.append("] with a photo ID.");
   					
		if (coordinators != null)
		{	//send message to all coordinators in the organization						
			for (int i = 0; i<coordinators.size(); i++)		
				TapestryHelper.sendMessageToInbox("Authenticate PHR", sb.toString(), user.getUserID(), coordinators.get(i).getUserID(), messageManager);	
		}
		else{
			System.out.println("Can't find any coordinator in organization id# " + organizationId);
			logger.error("Can't find any coordinator in organization id# " + organizationId);
		}
		//add logs
	
		sb = new StringBuffer();
		sb.append(user.getName());
		sb.append(" has authenticate PHR for patient:  ");
		sb.append(patientName);		
		sb.append(" on ");
		sb.append(sdf.format(new Date()));
		userManager.addUserLog(sb.toString(), user);
		
		return "redirect:/patient/" + patientId + "?appointmentId=" + TapestryHelper.getAppointmentId(request) +"&showAuthenticationMsg=true";
   	}
     
   	@RequestMapping(value="/authenticate_myoscar/{volunteerId}", method=RequestMethod.POST)
   	public String authenticateMyOscar(@PathVariable("volunteerId") int id, @RequestParam(value="patientId", required=true) int patientId,
   			SecurityContextHolderAwareRequestWrapper request, ModelMap model)
   	{
   		int centralAdminId = 1;
 
   		String clientFirstName = request.getParameter("client_first_name");
   		String clientLastName = request.getParameter("client_last_name");
   		String volunteerFirstName = request.getParameter("volunteer_first_name");
   		String volunteerLastName = request.getParameter("volunteer_last_name");
   		int volunteerUserId = volunteerManager.getUserIdByVolunteerId(id);
   		
   		StringBuffer sb = new StringBuffer();
   		String subject = "MyOscar Authentication";   		
   		
   		sb.append("Please authenticate ");
   		sb.append(clientFirstName);
   		sb.append(" ");
   		sb.append(clientLastName);
   		sb.append(" to level 3 -- PHR.");
   		sb.append("/n");
   		sb.append("Send by Volunteer: ");
   		sb.append(volunteerFirstName);
   		sb.append(" ");
   		sb.append(volunteerLastName);
   		String message = sb.toString();
   		
   		//send message to Central Admin
   		TapestryHelper.sendMessageToInbox(subject, message, volunteerUserId, centralAdminId, messageManager);
   		
   		//add logs
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has authenticate PHR for patient#  ");
		sb.append(patientId);				
		userManager.addUserLog(sb.toString(), loggedInUser);
   		
   		HttpSession session = request.getSession();
   		if (session.getAttribute("appointmentId") != null)
   		{
   			String appointmentId = session.getAttribute("appointmentId").toString();   			
   			return "redirect:/patient/" + patientId + "?appointmentId=" + appointmentId;
   		}
   		else
   			return "redirect:/?patientId=" + patientId;   		
   	}
   	
   	@RequestMapping(value="/display_appointment/{appointmentID}", method=RequestMethod.GET)
   	public String displayAppointment(@PathVariable("appointmentID") int id, 
   			SecurityContextHolderAwareRequestWrapper request, ModelMap model)
   	{   		
   		Appointment appointment = appointmentManager.getAppointmentById(id);
   		model.addAttribute("appointment", appointment);
   		
   		int patientId = appointment.getPatientID();
   		Patient p = patientManager.getPatientByID(patientId);
   		String alerts = p.getAlerts();
   		
   		if (!Utils.isNullOrEmpty(alerts))
   			model.addAttribute("alerts", alerts);
   		
   		int volunteer1Id = appointment.getVolunteerID();
   		int volunteer2Id = appointment.getPartnerId();
   		int appointmentId = appointment.getAppointmentID();
   		
   		List<Narrative> v1Narratives = volunteerManager.getNarrativesByVolunteer(volunteer1Id, patientId, appointmentId);
   		if (v1Narratives.size() > 0)
   			model.addAttribute("narratives1", v1Narratives);
   		
   		List<Narrative> v2Narratives = volunteerManager.getNarrativesByVolunteer(volunteer2Id, patientId, appointmentId); 
   		if (v2Narratives.size() > 0)
   			model.addAttribute("narratives2", v2Narratives);   	
   		   		
//   		List<Activity> activities = volunteerManager.getActivities(patientId, appointmentId);   		   		   		   		
//   		model.addAttribute("activities", activities);
   		   		
		if (request.isUserInRole("ROLE_ADMIN"))
			model.addAttribute("isCentralAdmin", true);   	
		
   		return "/admin/display_appointment";
   	}
   	
   	
   	@RequestMapping(value="/book_appointment/{patientId}", method=RequestMethod.GET)
   	public String bookAppointment(@PathVariable("patientId") int id,SecurityContextHolderAwareRequestWrapper request, ModelMap model)
   	{
   		List<Patient> patients = new ArrayList<Patient>();
   		HttpSession  session = request.getSession();
   		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
   		Patient patient = new Patient();
   		if (id != 0)
   			patient = patientManager.getPatientByID(id);
   		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
   		
   		if (request.isUserInRole("ROLE_USER"))
   		{// for volunteer   			
   			int loggedInVolunteer = TapestryHelper.getLoggedInVolunteerId(request);
   			patients = patientManager.getPatientsForVolunteer(loggedInVolunteer);   			
   			
   			model.addAttribute("patients", patients);
   			return "/volunteer/volunteer_book_appointment";
   		}
   		else if (request.isUserInRole("ROLE_ADMIN"))// for central admin
   		{
   			if (id == 0)//Manage Appointment
   				patients = TapestryHelper.getPatients(request, patientManager);
   			else   			
   				patients.add(patient);
   	   		model.addAttribute("patients", patients);
   	   		
   			return "/admin/admin_book_appointment";	 
   		}
   		else
   		{//for local admin/VC
   			if (id==0)
   				patients = TapestryHelper.getPatientsBySite(request, patientManager, loggedInUser.getSite());   			
   			else
   				patients.add(patient);
   	   		model.addAttribute("patients", patients);
   	   		
   			return "/admin/admin_book_appointment";	
   		}
   	}  
   	
   	@RequestMapping(value="/out_book_appointment", method=RequestMethod.GET)
	public String outAppointment(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
   	{   		
   		if (request.isUserInRole("ROLE_USER"))
   			return "redirect:/";
   		else
   			return "redirect:/manage_appointments";	   		
   	}
   	
	@RequestMapping(value="/book_appointment", method=RequestMethod.POST)
	public String addAppointment(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{	
		User user = TapestryHelper.getLoggedInUser(request, userManager);			
		int patientId = Integer.parseInt(request.getParameter("patient"));	
		Patient p = patientManager.getPatientByID(patientId);
		String noMatchedMsg="";	
		String notAvailableMsg = " is not available during this time slot, please check his/her availability";
				
		Volunteer volunteer1 = volunteerManager.getVolunteerById(p.getVolunteer());
		Volunteer volunteer2 = volunteerManager.getVolunteerById(p.getPartner());
		String vAvailability = volunteer1.getAvailability();
		String pAvailability = volunteer2.getAvailability();
		//set appointment
		Appointment a = new Appointment();
		String date = request.getParameter("appointmentDate");			
		String time = request.getParameter("appointmentTime");
		
		//format the date from yyyy/MM/dd to yyyy-MM-dd
		if (date.contains("/"))
			date = date.replace("/", "-");
		
		StringBuffer sb = new StringBuffer();
		sb.append(date);
		sb.append(" ");
		sb.append(time);
		
		if((TapestryHelper.checkAvailability(sb.toString(), p.getPatientID(), "P", appointmentManager))
				||(TapestryHelper.checkAvailability(sb.toString(), volunteer1.getVolunteerId(), "V", appointmentManager))
				||(TapestryHelper.checkAvailability(sb.toString(), volunteer2.getVolunteerId(), "V", appointmentManager)))
		{
			String msg = "Client or volunteer is not available at selected time.";
			
			if ("ROLE_USER".equalsIgnoreCase(user.getRole()))
				return "redirect:/?noMachedTime=" + msg;	
			else
				return "redirect:/manage_appointments?noMachedTime=" +msg;
		}
				
		int dayOfWeek = Utils.getDayOfWeekByDate(date);
		sb = new StringBuffer();
		sb.append(String.valueOf(dayOfWeek - 1));
								
		Map<String, String> m = TapestryHelper.getAvailabilityMap();
				
		Iterator iterator = m.entrySet().iterator();
		while (iterator.hasNext()) 
		{
			Map.Entry mapEntry = (Map.Entry) iterator.next();
					
			if (mapEntry.getValue().toString().contains(time))
				sb.append(mapEntry.getKey());
		}
		String availability = sb.toString();
		
		a.setVolunteerID(p.getVolunteer());
		a.setPartnerId(p.getPartner());
		a.setPatientID(p.getPatientID());
		a.setPatient(p.getFirstName() + " " + p.getLastName());
		a.setDate(date);
		a.setTime(time);
		
		if (TapestryHelper.hasVisit(patientId, appointmentManager))
			a.setType(0);//first visit
		else
			a.setType(1);//follow up
		
		String logMsg = "";
		String inboxMsg = "";
		//log message
		sb = new StringBuffer();
		sb.append(user.getName());
		sb.append(" has booked an appointment for client ");
		sb.append(a.getPatient());
		sb.append(" on ");
		sb.append(a.getDate());
		logMsg = sb.toString();		
		//Inbox message
		sb = new StringBuffer();
		sb.append(user.getName());
		sb.append(" has booked an appointment for ");
		sb.append(p.getFirstName());
		sb.append(" ");
		sb.append(p.getLastName());
		sb.append( " at ");
		sb.append(time);
		sb.append(" on ");
		sb.append(date);
		sb.append(".\n");
		inboxMsg = sb.toString();
		
		int userId = user.getUserID();	
		int volunteer1UserId = volunteerManager.getUserIdByVolunteerId(p.getVolunteer());	
		int volunteer2UserId = volunteerManager.getUserIdByVolunteerId(p.getPartner());
		String volunteer1Name = p.getVolunteerName();
		String volunteer2Name = p.getPartnerName();
		
		if ("ROLE_USER".equalsIgnoreCase(user.getRole()))
		{
			a.setStatus("Awaiting Approval");
			appointmentManager.createAppointment(a);
			
			if ((TapestryHelper.isAvailableForVolunteer(availability, pAvailability)) && 
					(TapestryHelper.isAvailableForVolunteer(availability, vAvailability)))
			{
	//			a.setStatus("Awaiting Approval");
//				if (appointmentManager.createAppointment(a))
//				{					
					//send message to local admin and volunteers
					int organizationId = volunteer1.getOrganizationId();
					List<User> coordinators = userManager.getVolunteerCoordinatorByOrganizationId(organizationId);
					
					sb = new StringBuffer();
					sb.append(inboxMsg);
					sb.append("This appointment is awaiting for confirmation.");
					inboxMsg = sb.toString();
					
					if (coordinators != null)
					{	//send message to all coordinators in the organization						
						for (int i = 0; i<coordinators.size(); i++)		
							TapestryHelper.sendMessageToInbox(inboxMsg, volunteer1UserId, coordinators.get(i).getUserID(), messageManager);			
					}
					else
					{
						System.out.println("Can't find any coordinator in organization id# " + organizationId);
						logger.error("Can't find any coordinator in organization id# " + organizationId);
					}
					TapestryHelper.sendMessageToInbox(inboxMsg, userId, volunteer1UserId, messageManager);//send message to volunteer1 
					TapestryHelper.sendMessageToInbox(inboxMsg, userId, volunteer2UserId, messageManager);//send message to volunteer2
					
					//log
					userManager.addUserLog(logMsg, user);
					return "redirect:/?booked=true";	
//				}// failed to create an appointment in DB
//				else
//				{
//					System.out.println("Can not create an appointment in DB");
//					return "redirect:/?booked=false";
//				}
			}
			else // another volunteer's availability does not match
			{
				sb = new StringBuffer();				
				sb.append(volunteer2Name);
				sb.append(notAvailableMsg);
				noMatchedMsg = sb.toString();
				
				return "redirect:/?noMachedTime=" + noMatchedMsg;		
			}			
		}
		else //login as admin
		{
			a.setStatus("Approved");
			appointmentManager.createAppointment(a);
			if ((TapestryHelper.isAvailableForVolunteer(availability, vAvailability)) && 
					(TapestryHelper.isAvailableForVolunteer(availability, pAvailability)))
			{
		//		a.setStatus("Approved");
//				if (appointmentManager.createAppointment(a))
//				{
//					TapestryHelper.sendMessageToInbox(inboxMsg, userId, userId, messageManager);//send message to login user 
//					TapestryHelper.sendMessageToInbox(inboxMsg, userId, volunteer1UserId, messageManager);//send message to volunteer1 
//					TapestryHelper.sendMessageToInbox(inboxMsg, userId, volunteer2UserId, messageManager);//send message to volunteer2
//					return "redirect:/manage_appointments?success=true";	
//				}
//				else
//				{
//					System.out.println("Can not create an appointment in DB");
//					return "redirect:/manage_appointments?success=false";
//				}	
				return "redirect:/manage_appointments?success=true";
			}
			else // one of volunteer's availability does not match
			{										
				String displayedVolunteerName = "";
				
				if(!(TapestryHelper.isAvailableForVolunteer(availability, vAvailability)))
					displayedVolunteerName = volunteer1Name;
				else
					displayedVolunteerName = volunteer2Name;
				sb = new StringBuffer();
				sb.append("Warning: [ ");
				sb.append(displayedVolunteerName);
				sb.append("] is not available during this time slot, however the appointment has been booked. "
						+ "Please ask the volunteer to update his/her availability.");
									
				noMatchedMsg = sb.toString();		
				
				return "redirect:/manage_appointments?noMachedTime=" + noMatchedMsg;
			}
		/*
			////////////////////////////////
			a.setStatus("Approved");
			if (appointmentManager.createAppointment(a))
			{
				//log
				userManager.addUserLog(logMsg, user);
				
				if ((TapestryHelper.isAvailableForVolunteer(availability, vAvailability)) && 
						(TapestryHelper.isAvailableForVolunteer(availability, pAvailability)))
				{
					TapestryHelper.sendMessageToInbox(inboxMsg, userId, userId, messageManager);//send message to login user 					
					return "redirect:/manage_appointments?success=true";					
				}
				else // one of volunteer's availability does not match
				{										
					String displayedVolunteerName = "";
					
					if(!(TapestryHelper.isAvailableForVolunteer(availability, vAvailability)))
						displayedVolunteerName = volunteer1Name;
					else
						displayedVolunteerName = volunteer2Name;
					sb = new StringBuffer();
					sb.append("Warning: [ ");
					sb.append(displayedVolunteerName);
					sb.append("] is not available during this time slot, however the appointment has been booked. "
							+ "Please ask the volunteer to update his/her availability.");
										
					noMatchedMsg = sb.toString();		
					
					return "redirect:/manage_appointments?noMachedTime=" + noMatchedMsg;
				}
			}
			else
			{
				System.out.println("Can not create an appointment in DB");
				return "redirect:/manage_appointments?success=false";
			}
			
			////////////////////  */
		}
	}	
	
	@RequestMapping(value="/delete_appointment/{appointmentID}", method=RequestMethod.GET)
	public String deleteAppointment(@PathVariable("appointmentID") int id, SecurityContextHolderAwareRequestWrapper request)
	{
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		Appointment a = appointmentManager.getAppointmentById(id);		
		appointmentManager.deleteAppointment(id);		
		//archive deleted appointment
		appointmentManager.archiveAppointment(a, loggedInUser.getName());
		//add logs		
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has deleted the appointment #  ");
		sb.append(id);				
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		if(request.isUserInRole("ROLE_USER")) {
			return "redirect:/";
		} else {
			return "redirect:/manage_appointments";
		}
	}
	
	@RequestMapping(value="/approve_appointment/{appointmentID}", method=RequestMethod.GET)
	public String approveAppointment(@PathVariable("appointmentID") int id, SecurityContextHolderAwareRequestWrapper request)
	{
		appointmentManager.approveAppointment(id);
		
		//send message to volunteer1 and volunteer2
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		
		Appointment appt = appointmentManager.getAppointmentById(id);	
		int v1UserId = volunteerManager.getUserIdByVolunteerId(appt.getVolunteerID());				
		int v2UserId = volunteerManager.getUserIdByVolunteerId(appt.getPartnerId());
		
		String subject = "Appointment has been approved";
		StringBuffer sb = new StringBuffer();
		sb.append("The appointment which is on ");
		sb.append(appt.getDate());
		sb.append(" at ");
		sb.append(appt.getTime());
		sb.append(" has been approved by ");
		sb.append(loggedInUser.getName());
		
		TapestryHelper.sendMessageToInbox(subject, sb.toString(), loggedInUser.getUserID(), v1UserId, messageManager);
		TapestryHelper.sendMessageToInbox(subject, sb.toString(), loggedInUser.getUserID(), v2UserId, messageManager);
		//add logs
		
		sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has approved the appointment #  ");
		sb.append(id);				
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		return "redirect:/manage_appointments";
	}
	
	@RequestMapping(value="/decline_appointment/{appointmentID}", method=RequestMethod.GET)
	public String unapproveAppointment(@PathVariable("appointmentID") int id, SecurityContextHolderAwareRequestWrapper request)
	{
		appointmentManager.declineAppointment(id);
		
		//add logs
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has declined the appointment #  ");
		sb.append(id);				
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		return "redirect:/manage_appointments";
	}
	
	//view_appointments
	@RequestMapping(value="/view_appointments", method=RequestMethod.GET)
	public String viewAppointmentByAdmin( SecurityContextHolderAwareRequestWrapper request)
	{
		
		return "/admin/view_appointments";
	}
	
	@RequestMapping(value="/go_scheduler", method=RequestMethod.GET)
	public String goScheduler( SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "/admin/go_scheduler";
	}
	
	//display scheduler page
	@RequestMapping(value="/view_scheduler", method=RequestMethod.GET)
	public String viewScheduler( SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		List<Patient> patients = TapestryHelper.getPatients(request, patientManager);	
		List<Volunteer> allVolunteers = TapestryHelper.getAllVolunteers(volunteerManager);
		
		List<Availability> matchList = TapestryHelper.getAllMatchedPairs(allVolunteers, allVolunteers);
		
		model.addAttribute("patients",patients);
		model.addAttribute("allvolunteers",allVolunteers);		
		model.addAttribute("matcheList", matchList);
		
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
	
		return "/admin/view_scheduler";
	}
	
	//Open add new appointment from scheduler
	@RequestMapping(value="/add_appointment/{volunteerId}", method=RequestMethod.GET)
	public String addAppointmentFromSecheduler(@PathVariable("volunteerId") int volunteerId, 
			@RequestParam(value="vId", required=false) int partnerId, 			
			@RequestParam(value="time", required=false) String time, 			
			SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{		
		List<Patient> patients = TapestryHelper.getPatients(request, patientManager);		
	
		//get volunteers's name		
		String vName = volunteerManager.getVolunteerNameById(volunteerId);
		String pName = volunteerManager.getVolunteerNameById(partnerId);
			
		String date = time.substring(0,10);		
		time = time.substring(11);
				
		model.addAttribute("patients",patients);		
		model.addAttribute("selectedVolunteer",vName);
		model.addAttribute("selectedPartner",pName);
		model.addAttribute("selectedTime", time);
		model.addAttribute("selectedDate", date);
		
		//save volunteers id in the session
		HttpSession  session = request.getSession();	
		session.setAttribute("volunteerId", volunteerId);
		session.setAttribute("partnerId", partnerId);				
	
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));

		return "/admin/schedule_appointment";
	}
	
	//load match time for both selected volunteers  /view_matchTime
	@RequestMapping(value="/view_matchTime", method=RequestMethod.POST)
	public String viewMatchAvailablities( SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{		
		List<Patient> patients = TapestryHelper.getPatients(request, patientManager);		
		List<Volunteer> volunteers = TapestryHelper.getAllVolunteers(volunteerManager);
		
		String patientId = request.getParameter("patient");
		String volunteerId1 = request.getParameter("volunteer1");		
		String volunteerId2 = request.getParameter("volunteer2");		

		Volunteer v1 = volunteerManager.getVolunteerById(Integer.parseInt(volunteerId1));
		Volunteer v2 = volunteerManager.getVolunteerById(Integer.parseInt(volunteerId2));
		
		Patient p = patientManager.getPatientByID(Integer.parseInt(patientId));
		
		//check if two volunteers are same persons
		if (volunteerId1.equals(volunteerId2))
			model.addAttribute("sameVolunteer",true);
		else 
		{
			String v1Level = v1.getExperienceLevel();
			String v2Level = v2.getExperienceLevel();
				
			// matching rule is Beginner can only be paired with Experienced
			if(!TapestryHelper.isMatched(v1Level, v2Level))
				model.addAttribute("misMatchedVolunteer",true);
			else
			{
				String[] aVolunteer1, aVolunteer2;
				Availability availability;
					
				aVolunteer1= v1.getAvailability().split(",");							
				aVolunteer2 = v2.getAvailability().split(",");				
				List<Availability> matchList = new ArrayList<Availability>();
								
				for( String a1: aVolunteer1 )
				{
					for(String a2 : aVolunteer2)
					{
						if ( (!a1.contains("non")) && (a1.equals(a2)) )
						{//find matched available time for both volunteers
							availability = new Availability();								
							availability.setvDisplayName(v1.getDisplayName());
							availability.setvPhone(v1.getHomePhone());
							availability.setvEmail(v1.getEmail());
							availability.setpDisplayName(v2.getDisplayName());
							availability.setpPhone(v2.getHomePhone());
							availability.setpEmail(v2.getEmail());				
							availability.setMatchedTime(TapestryHelper.formatMatchTime(a1));
							availability.setvId(Integer.parseInt(volunteerId1));
							availability.setpId(Integer.parseInt(volunteerId2));
							availability.setPatientId(Integer.parseInt(patientId));
							availability.setPatientName(p.getDisplayName());
							matchList.add(availability);
						}
					}
				}					
				if (matchList.size() == 0)
					model.addAttribute("noMatchTime",true);
				else
					model.addAttribute("matcheList",matchList);		
			}			
		}		
		model.addAttribute("allvolunteers", volunteers);
		model.addAttribute("volunteerOne",v1);
		model.addAttribute("volunteerTwo",v2);
		model.addAttribute("patients", patients);
		model.addAttribute("selectedPatient", patientId);
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "/admin/manage_appointments";		
	}
	
	@RequestMapping(value="/find_volunteers", method=RequestMethod.POST)
	public String getPairedVolunteers(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{	
		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		
		//get Date and time for appointment		
		String date = request.getParameter("appointmentDate");
		String time = request.getParameter("appointmentTime");	
		//when date pick up from calendar, format is different, need to change from yyyy/mm/dd to yyyy-MM-dd
		date = date.replace("/", "-");
		
		int dayOfWeek = Utils.getDayOfWeekByDate(date);
		
		Map<String, String> map = TapestryHelper.getAvailabilityMap();
		String fullDateTime = map.get(time);
		fullDateTime = fullDateTime.substring(0, 8);
		
		StringBuffer sb = new StringBuffer();
		sb.append(date);
		sb.append(" ");
		sb.append(fullDateTime);
		fullDateTime = sb.toString();
		
		sb = new StringBuffer();
		sb.append(String.valueOf(dayOfWeek -1));
		sb.append(time);
		String timeSlot = sb.toString();
		
		if (request.isUserInRole("ROLE_ADMIN"))//for central admin
			volunteers = TapestryHelper.getAllVolunteers(volunteerManager);
		else // local admin/VC
			volunteers = volunteerManager.getAllVolunteersByOrganization(loggedInUser.getOrganization());	
		
		String action = request.getParameter("hhAction");		
		if ("Find Avalable Volunteers".equals(action))
		{
				
			volunteers = volunteerManager.getVolunteersByAvailibility(timeSlot, volunteers);
			model.addAttribute("showVolunteers", true);		
			model.addAttribute("volunteers", volunteers);	
			return "/admin/go_scheduler";
		}
		else
			return getVolunteerByScheduler(request, model, volunteers, timeSlot, fullDateTime);
//		
//		if (volunteers.size() == 0)
//			model.addAttribute("noAvailableTime",true);	
//		else
//		{
//			List<Volunteer> availableVolunteers = TapestryHelper.getAllMatchedVolunteers(volunteers, timeSlot);
//			if (availableVolunteers.size() == 0)
//				model.addAttribute("noAvailableVolunteers",true);	
//			else
//			{
//				List<Availability> matchList = TapestryHelper.getAllAvailablilities(availableVolunteers, timeSlot, day);
//				if (matchList.size() == 0)
//					model.addAttribute("noFound",true);	
//				else
//					model.addAttribute("matcheList",matchList);	
//			}
//		}		
//		HttpSession  session = request.getSession();
//		if (session.getAttribute("unread_messages") != null)
//			model.addAttribute("unread", session.getAttribute("unread_messages"));
//		
//		return "/admin/view_scheduler";
	}
	private String getVolunteerByScheduler(SecurityContextHolderAwareRequestWrapper request, ModelMap model, 
			List<Volunteer> volunteers, String timeSlot, String dateTime)
	{
//		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
//		List<Volunteer> volunteers = new ArrayList<Volunteer>();
//		
//		//get Date and time for appointment		
//		String day = request.getParameter("appointmentDate");
//		//when date pick up from calendar, format is different, need to change from yyyy/mm/dd to yyyy-MM-dd
//		day = day.replace("/", "-");
//		
//		int dayOfWeek = Utils.getDayOfWeekByDate(day);
//		String time = request.getParameter("appointmentTime");				
//		
//		StringBuffer sb = new StringBuffer();
//		sb.append(String.valueOf(dayOfWeek -1));
//		sb.append(time);
//		String date_time = sb.toString();
//		
//		if (request.isUserInRole("ROLE_ADMIN"))//for central admin
//			volunteers = TapestryHelper.getAllVolunteers(volunteerManager);
//		else // local admin/VC
//			volunteers = volunteerManager.getAllVolunteersByOrganization(loggedInUser.getOrganization());	
		String day = request.getParameter("appointmentDate");
		//when date pick up from calendar, format is different, need to change from yyyy/mm/dd to yyyy-MM-dd
		day = day.replace("/", "-");
		
		if (volunteers.size() == 0)
			model.addAttribute("noAvailableTime",true);	
		else
		{
			List<Volunteer> availableVolunteers = TapestryHelper.getAllMatchedVolunteers(volunteers, timeSlot, dateTime, appointmentManager);
			if (availableVolunteers.size() == 0)
				model.addAttribute("noAvailableVolunteers",true);	
			else
			{
				List<Availability> matchList = TapestryHelper.getAllAvailablilities(availableVolunteers, timeSlot, day);
				if (matchList.size() == 0)
					model.addAttribute("noFound",true);	
				else
					model.addAttribute("matcheList",matchList);	
			}
		}		
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "/admin/view_scheduler";
	}
	
	@RequestMapping(value="/schedule_appointment", method=RequestMethod.POST)
	public String createAppointment(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{		
		StringBuffer sb = new StringBuffer();	
		//set up appointment
		Appointment appointment = new Appointment();		
		int patientId = Integer.parseInt(request.getParameter("patient"));
		User user = TapestryHelper.getLoggedInUser(request, userManager);	
		
		//get Date and time for appointment		
		String date = request.getParameter("appointmentDate");
		String time = request.getParameter("appointmentTime");
		
		sb.append(date);
		sb.append(" ");
		sb.append(time);
	
		if(TapestryHelper.checkAvailability(sb.toString(), patientId, "P", appointmentManager))
		{
			String msg = "Client is not available at selected time.";
			return "redirect:/manage_appointments?noMachedTime=" +msg;
		}
		
		//retrieve volunteers id from session
		HttpSession  session = request.getSession();			
		Integer vId = (Integer)session.getAttribute("volunteerId");
		int volunteerId = vId.intValue();		
		Integer pId = (Integer)session.getAttribute("partnerId");
		int partnerId = pId.intValue();
		
		appointment.setVolunteerID(volunteerId);
		appointment.setPatientID(patientId);
		appointment.setPartnerId(partnerId);	
		
		//format time, remove AM/PM
		time = time.substring(0,8);		
		appointment.setDate(date);
		appointment.setTime(time);		
		
		//set up Type
		if (TapestryHelper.hasVisit(patientId, appointmentManager))
			appointment.setType(0);//first visit
		else
			appointment.setType(1);//follow up
		//set up Status
		if ("ROLE_USER".equalsIgnoreCase(user.getRole()))
			appointment.setStatus("Awaiting Approval");
		else
			appointment.setStatus("Approved");
				
		//save new appointment in DB and send message 
		if (appointmentManager.createAppointment(appointment))
		{
			Patient patient = patientManager.getPatientByID(patientId);						
			int userId = user.getUserID();
			int volunteer1UserId = volunteerManager.getUserIdByVolunteerId(volunteerId);	
			int volunteer2UserId = volunteerManager.getUserIdByVolunteerId(partnerId);
			
			//update volunteer and partner for patient if choose different from scheduler
			if ((volunteerId != patient.getVolunteer()) || (partnerId != patient.getPartner()))
				patientManager.updatePatientVolunteers(patientId, volunteerId, partnerId);
						
			//send message to both volunteers					
			sb.append(user.getName());
			sb.append(" has booked an appointment for ");
			sb.append(patient.getDisplayName());
			sb.append( " at ");
			sb.append(time);
			sb.append(" on ");
			sb.append(date);
			sb.append(".\n");			
						
			String msg = sb.toString();
			
			TapestryHelper.sendMessageToInbox(msg, userId, volunteer1UserId, messageManager); //send message to volunteer
			TapestryHelper.sendMessageToInbox(msg, userId, volunteer2UserId, messageManager); //send message to volunteer
			TapestryHelper.sendMessageToInbox(msg, userId, userId, messageManager); //send message to admin her/his self	
			model.addAttribute("successToCreateAppointment",true);
			//log activity
			userManager.addUserLog(msg, user);
		}
		else
			model.addAttribute("failedToCreateAppointment",true);
		return "redirect:/manage_appointments";		
	}
	
	@RequestMapping(value="/open_alerts_keyObservations/{appointmentId}", method=RequestMethod.GET)
	public String openAlertsAndKeyObservations(@PathVariable("appointmentId") int id, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{			
		Appointment appt = appointmentManager.getAppointmentById(id);
		
		int patientId = appt.getPatientID();
		Patient patient = patientManager.getPatientByID(patientId);
				
		HttpSession session = request.getSession();
		if (session.getAttribute("narrativeMessage") != null)
		{
			String message = session.getAttribute("narrativeMessage").toString();
					
			if ("C".equals(message)){
				model.addAttribute("NarrativeCreated", true);
				session.removeAttribute("narrativeMessage");
			}
		}			
		model.addAttribute("appointment", appt);
		model.addAttribute("patient", patient);	
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "/volunteer/add_alerts_keyObservation";
	}
	
	@RequestMapping(value="/saveAlertsAndKeyObservations", method=RequestMethod.POST)
	public String saveAlertsAndKeyObservations(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{	
		int appointmentId = TapestryHelper.getAppointmentId(request);	
		int patientId = TapestryHelper.getPatientId(request);

		String keyObservations = request.getParameter("keyObservations");
		appointmentManager.addAlertsAndKeyObservations(appointmentId, "", keyObservations);
		//add logs
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has added alerts and key observation for patient #  ");
		sb.append(patientId);				
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		Appointment appointment = appointmentManager.getAppointmentById(appointmentId);
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		Patient patient = patientManager.getPatientByID(appointment.getPatientID());
		model.addAttribute("appointment", appointment);
		model.addAttribute("patient", patient);
		return "/volunteer/visit_complete";
	
	}
	
	@RequestMapping(value="/open_plan/{appointmentId}", method=RequestMethod.GET)
	public String openPlan(@PathVariable("appointmentId") int id, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{	
		List<String> planDef = Utils.getPlanDefinition();				
		Appointment appt = appointmentManager.getAppointmentById(id);
		
		int patientId = appt.getPatientID();
		Patient patient = patientManager.getPatientByID(patientId);
		
		model.addAttribute("appointment", appt);
		model.addAttribute("patient", patient);
		model.addAttribute("plans", planDef);
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		return "/volunteer/add_plan";
	}
	
	@RequestMapping(value="/savePlans", method=RequestMethod.POST)
	public String savePlans(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{	
		int appointmentId = TapestryHelper.getAppointmentId(request);
		int patientId = TapestryHelper.getPatientId(request);
		String seperator = ";";
		
		StringBuffer sb = new StringBuffer();		
		sb.append(request.getParameter("plan1"));
		sb.append(seperator);
		sb.append(request.getParameter("plan2"));
		sb.append(seperator);
		sb.append(request.getParameter("plan3"));
		sb.append(seperator);
		sb.append(request.getParameter("plan4"));
		sb.append(seperator);
		sb.append(request.getParameter("plan5"));
		if (!Utils.isNullOrEmpty(request.getParameter("planSpecify")))
		{
			sb.append(seperator);
			sb.append(request.getParameter("planSpecify"));
		}	
		String plans = sb.toString();
		appointmentManager.addPlans(appointmentId, plans);

		//add logs
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has added plans for patient #  ");
		sb.append(patientId);				
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		//for temporary use, send a message to coordinator, hl7 report is ready to donwload on Admin side
		///////////////
		User user = TapestryHelper.getLoggedInUser(request, userManager);
		int userId = user.getUserID();	

		Appointment a = appointmentManager.getAppointmentById(appointmentId);
		Patient p;
		if (patientId != 0)
			p = patientManager.getPatientByID(patientId);
		else
			p = patientManager.getPatientByID(a.getPatientID());
		
		String patientName = p.getDisplayName();
		int organizationId = a.getGroup();		
		
		List<User> coordinators = userManager.getVolunteerCoordinatorByOrganizationId(organizationId);
						
		sb = new StringBuffer();
		sb.append(patientName);
		sb.append("'s hl7/PDF report is ready to be downloaded. ");				
		
		if (coordinators != null)			
		{	//send message to all coordinators in the organization					
			for (int i = 0; i<coordinators.size(); i++)		
				TapestryHelper.sendMessageToInbox("PDF/HL7 Report", sb.toString(), userId, coordinators.get(i).getUserID(), messageManager);			
		}
		else{
			System.out.println("Can't find any coordinator in organization id# " + organizationId);
			logger.error("Can't find any coordinator in organization id# " + organizationId);
		}
		return "redirect:/";
	}
	
	@RequestMapping(value="/goMyOscarAuthenticate/{appointmentId}", method=RequestMethod.GET)
	public String openMyOscarAuthenticate(@PathVariable("appointmentId") int id, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{			
		HttpSession session = request.getSession();
		session.setAttribute("appointmentId", id);
		
		Appointment appt = appointmentManager.getAppointmentById(id);
		
		int patientId = appt.getPatientID();
		Patient patient = patientManager.getPatientByID(patientId);
		
		String vName = patient.getVolunteerName();
		
		int index = vName.indexOf(" ");
		String firstName = vName.substring(0, index);
		String lastName = vName.substring(index);
		
		model.addAttribute("patient", patient);
		model.addAttribute("vFirstName", firstName);
		model.addAttribute("vLastName", lastName);
	
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "/volunteer/client_myoscar_authentication";
	}
	
	@RequestMapping(value="/visit_complete/{appointment_id}", method=RequestMethod.GET)
	public String viewVisitComplete(@PathVariable("appointment_id") int id, SecurityContextHolderAwareRequestWrapper request, 
			ModelMap model) 
	{		
		if (TapestryHelper.isFirstVisit(id, appointmentManager))// for first visit, go to Social Context page which is only for first visit
			return "redirect:/open_alerts_keyObservations/" + id;	
		else
		{
			Appointment appointment = appointmentManager.getAppointmentById(id);
			HttpSession  session = request.getSession();
			if (session.getAttribute("unread_messages") != null)
				model.addAttribute("unread", session.getAttribute("unread_messages"));
			
			Patient patient = patientManager.getPatientByID(appointment.getPatientID());
			model.addAttribute("appointment", appointment);
			model.addAttribute("patient", patient);
			return "/volunteer/visit_complete";
		}		
	}
	
	@RequestMapping(value="/complete_visit_byAdmin/{appointmentId}", method=RequestMethod.GET)
	public String completeVisitByAdmin(@PathVariable("appointmentId") int id, SecurityContextHolderAwareRequestWrapper request, 
			@RequestParam(value="patientId", required=true) int patientId, ModelMap model) 
	{
		appointmentManager.completeAppointment(id);
		
   		User loggedInUser = TapestryHelper.getLoggedInUser(request);
   		StringBuffer sb = new StringBuffer();
   		sb.append(loggedInUser.getName());
   		sb.append(" has completed Appointment # ");
   		sb.append(id);
   		userManager.addUserLog(sb.toString(), loggedInUser);
			
   		return "redirect:/display_client/" + patientId;		
	}
	
	@RequestMapping(value="/complete_visit/{appointment_id}", method=RequestMethod.POST)
	public String completeVisit(@PathVariable("appointment_id") int id, SecurityContextHolderAwareRequestWrapper request, 
			ModelMap model) 
	{
		String visitAlert = request.getParameter("visitAlerts");
		Appointment appt = appointmentManager.getAppointmentById(id);	
		int patientId = appt.getPatientID();
		Patient patient = patientManager.getPatientByID(patientId);
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		StringBuffer sb = new StringBuffer();

		boolean completed = false;
   		int siteId = loggedInUser.getSite();
   		int count = surveyManager.countSurveyTemplateBySite(siteId);
   		List<SurveyResult> completedSurveys = surveyManager.getCompletedSurveysByPatientID(patientId);
   		
   		if (count == completedSurveys.size())
   			completed = true;
		
		if (!Utils.isNullOrEmpty(visitAlert))		
		{
			//set visit alert as comments in DB
			appointmentManager.completeAppointment(id, visitAlert);
			//add logs				
			sb.append(loggedInUser.getName());
			sb.append(" has set the appointment #  ");
			sb.append(id);		
			sb.append("'s status as completed");
			userManager.addUserLog(sb.toString(), loggedInUser);
			
			if (completed)
			{	//for temporary use, send a message to coordinator, hl7 report is ready to donwload on Admin side					
				String patientName = patient.getDisplayName();
				List<User> coordinators = userManager.getLocalAdminBySite(siteId);
								
				sb = new StringBuffer();
				sb.append(patientName);
				sb.append("'s hl7/PDF report is ready to be downloaded. ");			
				                                                  
				//send message to all coordinators in the site					
				for (int i = 0; i<coordinators.size(); i++)	
					TapestryHelper.sendMessageToInbox("PDF/HL7 Report", sb.toString(), loggedInUser.getUserID(),
							coordinators.get(i).getUserID(), messageManager);

			}			
		}
		return "redirect:/";
	}
	
	//narrative 
	@RequestMapping(value="/view_narratives", method=RequestMethod.GET)
	public String getNarrativesByUser(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{			
		int volunteerId = TapestryHelper.getLoggedInVolunteerId(request);
		List<Patient> patients = patientManager.getPatientsForVolunteer(volunteerId);
		List<Appointment> appointments = appointmentManager.getAllAppointmentsByVolunteer(volunteerId);
			
		model.addAttribute("patients", patients);	
		model.addAttribute("appointments", appointments);	
		
		//check if there is message should be displayed
		HttpSession  session = request.getSession();
		if (session.getAttribute("narrativeMessage") != null)
		{
			String message = session.getAttribute("narrativeMessage").toString();
					
			if ("D".equals(message)){
				model.addAttribute("narrativeDeleted", true);
				session.removeAttribute("narrativeMessage");
			}
			else if ("U".equals(message)){
				model.addAttribute("narrativeUpdate", true);
				session.removeAttribute("narrativeMessage");
			}	
			else if ("C".equals(message)){
				model.addAttribute("NarrativeCreated", true);
				session.removeAttribute("narrativeMessage");
			}
		}			
		return "/volunteer/narratives";	
	}
	
	//loading a existing narrative to view detail or make a change
	@RequestMapping(value="/modify_narrative/{narrativeId}", method=RequestMethod.GET)
	public String modifyNarrative(SecurityContextHolderAwareRequestWrapper request, 
				@PathVariable("narrativeId") int id, ModelMap model)
	{		
		Narrative narrative = volunteerManager.getNarrativeById(id);				
		//set Date format for editDate
		String editDate = narrative.getEditDate();		
		if(!Utils.isNullOrEmpty(editDate))
			editDate = editDate.substring(0,10);		
		narrative.setEditDate(editDate);
		
		model.addAttribute("narrative", narrative);			
		TapestryHelper.setUnreadMsg(request, model, messageManager);
		return "/volunteer/modify_narrative";
	}
	
	//loading a existing narrative to view detail or make a change
	@RequestMapping(value="/edit_narrative/{appointmentId}", method=RequestMethod.GET)
	public String editNarrative(SecurityContextHolderAwareRequestWrapper request, 
			@PathVariable("appointmentId") int appointmentId, ModelMap model)
	{		
		Narrative narrative = volunteerManager.getNarrativeByAppointmentId(appointmentId);				
		//set Date format for editDate
		String editDate = narrative.getEditDate();		
		if(!Utils.isNullOrEmpty(editDate))
			editDate = editDate.substring(0,10);		
		narrative.setEditDate(editDate);
			
		model.addAttribute("narrative", narrative);			
		TapestryHelper.setUnreadMsg(request, model, messageManager);
		return "/volunteer/modify_narrative";
	}
	
	@RequestMapping(value="/add_narrative", method=RequestMethod.GET)
	public String newNarrative(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{				
		int appointmentId = TapestryHelper.getAppointmentId(request);
		
		if (appointmentId == 0) //Narratives==>New Narrative
			return "/volunteer/add_narrative";
		else
		{//Appointment ==> visit complete ==> social context ==> New Narrative
			Appointment appt = appointmentManager.getAppointmentById(appointmentId);
			
			int patientId = appt.getPatientID();
			Patient patient = patientManager.getPatientByID(patientId);
			
			model.addAttribute("appointment", appt);
			model.addAttribute("patient", patient);
			
			return "/volunteer/new_narrative";
		}
	}
	
	@RequestMapping(value="/add_narrative/{appointment_id}", method=RequestMethod.GET)
	public String addNarrative(@PathVariable("appointment_id") int id,SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{		
		Appointment appt = appointmentManager.getAppointmentById(id);
		
		int patientId = appt.getPatientID();
		Patient patient = patientManager.getPatientByID(patientId);
		
		model.addAttribute("appointment", appt);
		model.addAttribute("patientDisplayName", patient.getDisplayName());
		return "/volunteer/add_narrative";
	}
	
	//Modify a narrative and save the change in the DB
	@RequestMapping(value="/update_narrative", method=RequestMethod.POST)
	public String updateNarrative(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{		
		String narrativeId = null;
		int iNarrativeId;	
		Narrative narrative = new Narrative();		
		HttpSession  session = request.getSession();	
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
					
		if (request.getParameter("narrativeId") != null){
			narrativeId = request.getParameter("narrativeId").toString();	
			iNarrativeId = Integer.parseInt(narrativeId);
			
			narrative = volunteerManager.getNarrativeById(iNarrativeId);
			//archive narrative
			volunteerManager.archiveNarrative(narrative, loggedInUser.getName(), "update");
						
			String title = null;
			if (request.getParameter("mNarrativeTitle") != null){
				title = request.getParameter("mNarrativeTitle").toString();
				narrative.setTitle(title);
			}
			
			String editDate = null;
			//convert current date to the format matched in DB
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			editDate = sdf.format(new Date()); 
			narrative.setEditDate(editDate);
			
			String contents = null;
			if (request.getParameter("mNarrativeContent") != null){
				contents = request.getParameter("mNarrativeContent").toString();
				narrative.setContents(contents);			
			}
			else{
				logger.error("Narrative ID can not be null");
			}
			
			volunteerManager.updateNarrative(narrative);			
			//add logs			
			StringBuffer sb = new StringBuffer();
			sb.append(loggedInUser.getName());
			sb.append(" has modified the narrative # ");
			sb.append(narrative.getNarrativeId());				
			userManager.addUserLog(sb.toString(), loggedInUser);
			
			session.setAttribute("narrativeMessage","U");
		}				
		return "redirect:/view_narratives";
	}
			
	//create a new narrative and save it in DB
	@RequestMapping(value="/add_narrative/{appointmentId}", method=RequestMethod.POST)
	public String createNarrative(@PathVariable("appointmentId") int appointmentId, @RequestParam(value="flag", required=false) int flag,
			SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{			
		int loggedInVolunteerId  = TapestryHelper.getLoggedInVolunteerId(request);		
		int patientId = appointmentManager.getAppointmentById(appointmentId).getPatientID();				
		String title = request.getParameter("narrativeTitle");
		String content = request.getParameter("narrativeContent");	
		
		Narrative narrative = new Narrative();
		//convert current date to the format matched in DB
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = sdf.format(new Date()); 
		
		String editDate = date.toString();		
		
		narrative.setVolunteerId(loggedInVolunteerId);
		narrative.setContents(content);
		narrative.setTitle(title);
		narrative.setEditDate(editDate);
		narrative.setPatientId(patientId);
		narrative.setAppointmentId(appointmentId);
		
		//add new narrative in narrative table in DB
		volunteerManager.addNarrative(narrative);
		//set complete narrative in Appointment table in DB
		appointmentManager.completeNarrative(appointmentId);	
		
		//add logs
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has added the narrative for the appointment # ");
		sb.append(appointmentId);			
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		HttpSession session = request.getSession();
		session.setAttribute("narrativeMessage","C");
		
		if (flag == 1)
			return "redirect:/open_alerts_keyObservations/" + appointmentId;		
		else
			return  "redirect:/view_narratives";	
	}
	
	@RequestMapping(value="/delete_narrative/{narrativeId}", method=RequestMethod.GET)
	public String deleteNarrativeById(@PathVariable("narrativeId") int id, 
				SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{		
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		Narrative narrative = volunteerManager.getNarrativeById(id);
		//arvhice narrative
		volunteerManager.archiveNarrative(narrative, loggedInUser.getName(), "delete");
		//delete
		volunteerManager.deleteNarrativeById(id);	
		
		//add logs
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has deleted the narrative # ");
		sb.append(id);			
		userManager.addUserLog(sb.toString(), loggedInUser);
				
		HttpSession  session = request.getSession();		
		session.setAttribute("narrativeMessage","D");
				
		return "redirect:/view_narratives";
	}	

}
