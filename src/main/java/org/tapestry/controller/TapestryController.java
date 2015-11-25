package org.tapestry.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.survey_component.actions.SurveyAction;
import org.survey_component.data.PHRSurvey;
import org.survey_component.data.SurveyQuestion;
import org.tapestry.utils.TapestryHelper;
import org.tapestry.utils.Utils;
import org.tapestry.hl7.Hl7Utils;
import org.tapestry.objects.Activity;
import org.tapestry.objects.AdminActivity;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Clinic;
import org.tapestry.objects.DisplayedSurveyResult;
import org.tapestry.objects.HL7Report;
import org.tapestry.objects.Message;
import org.tapestry.objects.Organization;
import org.tapestry.objects.Patient;
//import org.tapestry.objects.Picture;
import org.tapestry.objects.Report;
import org.tapestry.objects.ResearchData;
import org.tapestry.objects.Site;
import org.tapestry.objects.SurveyResult;
import org.tapestry.objects.SurveyTemplate;
import org.tapestry.objects.User;
import org.tapestry.objects.UserLog;
import org.tapestry.objects.Volunteer;
import org.tapestry.report.AlertManager;
import org.tapestry.report.AlertsInReport;
import org.tapestry.report.CalculationManager;
import org.tapestry.report.ScoresInReport;
import org.tapestry.service.AppointmentManager;
import org.tapestry.service.MessageManager;
import org.tapestry.service.PatientManager;
import org.tapestry.service.PictureManager;
import org.tapestry.service.SurveyManager;
import org.tapestry.service.UserManager;
import org.tapestry.service.VolunteerManager;
import org.tapestry.service.OrganizationManager;
import org.tapestry.surveys.DoSurveyAction;
import org.tapestry.surveys.ResultParser;
import org.tapestry.surveys.SurveyFactory;
import org.tapestry.surveys.TapestryPHRSurvey;
import org.tapestry.surveys.TapestrySurveyMap;
import org.apache.commons.lang.StringUtils; 
/**
* Main controller class
* This class is responsible for interpreting URLs and returning the appropriate pages.
* It is the 'brain' of the application. Each function is tagged with @RequestMapping and
* one of either RequestMethod.GET or RequestMethod.POST, which determines which requests
* the function will be triggered in response to.
* The function returns a string, which is the name of a web page to render. For example,
* the login() function returns "login" when an HTTP request like "HTTP 1.1 GET /login"
* is received. The application then loads the page "login.jsp" (the extension is added
* automatically).
*/
@Controller
public class TapestryController{
	protected static Logger logger = Logger.getLogger(TapestryController.class);	
   	@Autowired
   	private UserManager userManager;
   	@Autowired
	private MessageManager messageManager;
   	@Autowired
   	private PictureManager pictureManager;
   	@Autowired
   	private PatientManager patientManager;
   	@Autowired
	private VolunteerManager volunteerManager;
   	@Autowired
   	private SurveyManager surveyManager;
   	@Autowired 
   	private AppointmentManager appointmentManager;
   	@Autowired 
   	private OrganizationManager organizationManager;
   	
   	@PostConstruct
   	public void readConfig(){
   		TapestryHelper.readConfig();
   	}
   	
   	//Everything below this point is a RequestMapping
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login(@RequestParam(value="usernameChanged", required=false) Boolean usernameChanged, ModelMap model)
	{		
		if (usernameChanged != null)
			model.addAttribute("usernameChanged", usernameChanged);
		return "login";
	}
	
	@RequestMapping(value="/loginsuccess", method=RequestMethod.GET)
	public String loginSuccess(SecurityContextHolderAwareRequestWrapper request)
	{
		HttpSession session = request.getSession();				
		User loggedInUser = null;
		String name = request.getUserPrincipal().getName();	
		if (name != null){			
			loggedInUser = userManager.getUserByUsername(name);								
			session.setAttribute("loggedInUser", loggedInUser);	
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" logged in");		
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		return "redirect:/";
	}

	@RequestMapping(value="/loginfailed", method=RequestMethod.GET)
	public String failed(ModelMap model)
	{
		model.addAttribute("error", "true");
		return "login";
	}

	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logout(SecurityContextHolderAwareRequestWrapper request)
	{
		User u = TapestryHelper.getLoggedInUser(request, userManager);		
		StringBuffer sb = new StringBuffer();
		sb.append(u.getName());
		sb.append(" logged out");
		userManager.addUserLog(sb.toString(), u);
		
		return "redirect:/j_spring_security_logout";
//		return "confirm-logout";
	}
	
	@RequestMapping(value="/client", method=RequestMethod.GET)
	public String getClients(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{	
		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
		//get volunteer Id from login user		
		int volunteerId= volunteerManager.getVolunteerIdByUsername(loggedInUser.getUsername());			
		List<Patient> clients = patientManager.getPatientsForVolunteer(volunteerId);	
		model.addAttribute("clients", clients);
		
		TapestryHelper.setUnreadMessage(request, model, messageManager);		
		return "/volunteer/client";
	}
	
	@RequestMapping(value="/manage_users", method=RequestMethod.GET)
	public String manageUsers(@RequestParam(value="failed", required=false) Boolean failed, ModelMap model,
			SecurityContextHolderAwareRequestWrapper request)
	{
		TapestryHelper.setUnreadMessage(request, model, messageManager);
		HttpSession session = request.getSession();
		List<User> userList;
		
		User user = TapestryHelper.getLoggedInUser(request);
		
		if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole()))//for central admin
		{
			List<Site> sites = TapestryHelper.getSites(request, organizationManager);		
			model.addAttribute("sites", sites);			
			userList = userManager.getAllUsers();
		}
		else
			userList = userManager.getUserBySIte(user.getSite());
		
		model.addAttribute("users", userList);
//		model.addAttribute("active", userDao.countActiveUsers());
//		model.addAttribute("total", userDao.countAllUsers());
		if(failed != null) 
			model.addAttribute("failed", true);
		
		List<Organization> organizations;		
		if (session.getAttribute("organizations") != null)
			organizations = (List<Organization>) session.getAttribute("organizations");
		else 
		{
			organizations = volunteerManager.getAllOrganizations();
			session.setAttribute("organizations", organizations);
		}
		
		return "/admin/manage_users";
	}
	
	
	@RequestMapping(value="/manage_users", method=RequestMethod.POST)
	public String searchOnUsers(@RequestParam(value="failed", required=false) Boolean failed, ModelMap model,
			SecurityContextHolderAwareRequestWrapper request)
	{			
		String name = request.getParameter("searchName");		
		List<User> userList = userManager.getUsersByPartialName(name);		
		model.addAttribute("users", userList);
	
		if(failed != null) {
			model.addAttribute("failed", true);
		}		
		model.addAttribute("searchName", name);
		
		TapestryHelper.setUnreadMessage(request, model, messageManager);
		
		return "admin/manage_users";
	}

	@RequestMapping(value="/add_user", method=RequestMethod.POST)
	public String addUser(SecurityContextHolderAwareRequestWrapper request)
	{
		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
		//Add a new user
		User u = new User();		
		//set name with firstname + lastname
		StringBuffer sb = new StringBuffer();
		sb.append(request.getParameter("firstname").trim());
		sb.append(" ");
		sb.append(request.getParameter("lastname").trim());
		u.setName(sb.toString());

		u.setUsername(request.getParameter("username").trim());
		String role = request.getParameter("role");
		u.setRole(role);		
		u.setOrganization(Integer.valueOf(request.getParameter("organization")));
		
		ShaPasswordEncoder enc = new ShaPasswordEncoder();
		String hashedPassword = enc.encodePassword(request.getParameter("password"), null); 
	
		u.setPassword(hashedPassword);
		u.setEmail(request.getParameter("email").trim());
		u.setPhoneNumber(request.getParameter("phonenumber"));
		if (role.equals("ROLE_ADMIN"))
			u.setSite(0);
		else
			u.setSite(Integer.parseInt(request.getParameter("site")));		
		
		if (userManager.createUser(u))
		{
			sb = new StringBuffer();
			sb.append("Thank you for volunteering with Tapestry. Your account has been successfully created.\n");
			sb.append("Your username and password are as follows:\n");
			sb.append("Username: ");
			sb.append(u.getUsername());
			sb.append("\n");
			sb.append("Password: password\n\n");
			sb.append("We recommend that you change your password as soon as possible due to security reasons");
			
			TapestryHelper.sendMessageByEmail(u, "Welcome to Tapestry", sb.toString());
			
			//log creating new user
			sb = new StringBuffer();
			sb.append(loggedInUser.getName());
			sb.append("has created a new user, whose name is ");
			sb.append(u.getName());
			userManager.addUserLog(sb.toString(), loggedInUser);
		}
		else
			return "redirect:/manage_users?failed=true";
		//Display page again
		return "redirect:/manage_users";
	}

	@RequestMapping(value="/remove_user/{user_id}", method=RequestMethod.GET)
	public String removeUser(SecurityContextHolderAwareRequestWrapper request, @PathVariable("user_id") int id)
	{
		userManager.removeUserWithID(id);
		
		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has removed the user, ");
		sb.append(userManager.getUserByID(id).getName());
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		return "redirect:/manage_users";
	}
	
	@RequestMapping(value="/disable_user/{user_id}", method=RequestMethod.GET)
	public String disableUser(@PathVariable("user_id") int id, SecurityContextHolderAwareRequestWrapper request)
	{
		userManager.disableUserWithID(id);
		
		User u = userManager.getUserByID(id);
		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" disabled ");
		sb.append(u.getName());
		userManager.addUserLog(sb.toString(), loggedInUser);		
		
		return "redirect:/manage_users";
	}

	@RequestMapping(value="/enable_user/{user_id}", method=RequestMethod.GET)
	public String enableUser(@PathVariable("user_id") int id, SecurityContextHolderAwareRequestWrapper request)
	{
		userManager.enableUserWithID(id);
		
		User u = userManager.getUserByID(id);
		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" enable ");
		sb.append(u.getName());
		userManager.addUserLog(sb.toString(), loggedInUser);		
		
		return "redirect:/manage_users";
	}
	
	@RequestMapping(value="/inbox", method=RequestMethod.GET)
	public String viewInbox(@RequestParam(value="success", required=false) Boolean messageSent,@RequestParam(value="failure",
			required=false) Boolean messageFailed, SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
		int userId = loggedInUser.getUserID();
		List<Message> messages;		
		
		if (messageSent != null)
			model.addAttribute("success", messageSent);
		if (messageFailed != null)
			model.addAttribute("failure", messageFailed);
		
		messages = messageManager.getAllMessagesForRecipient(userId);
		model.addAttribute("messages", messages);		
		TapestryHelper.setUnreadMessage(request, model, messageManager);
			
		List<User> receivers = new ArrayList<User>();
		if (request.isUserInRole("ROLE_USER"))
		{// for volunteer			
			List<User> administrators = userManager.getVolunteerCoordinatorByOrganizationId(loggedInUser.getOrganization());
			model.addAttribute("administrators", administrators);
			
			return "/volunteer/inbox";
		} 
		else if (request.isUserInRole("ROLE_LOCAL_ADMIN"))
		{// local admin/VC			
			List<User> centralAdmin = userManager.getAllActiveUsersWithRole("ROLE_ADMIN");
			receivers.addAll(centralAdmin);
			
			List<User> volunteers = userManager.getGroupedUsersByRole(loggedInUser.getOrganization(), "ROLE_USER");	
			receivers.addAll(volunteers);
			
			model.addAttribute("volunteers", receivers);
		}
		else
		{// central admin	
			List<User> localAdmins = userManager.getAllActiveUsersWithRole("ROLE_LOCAL_ADMIN");
			receivers.addAll(localAdmins);
			
			List<User> volunteers = userManager.getAllUsersWithRole("ROLE_USER");	
			receivers.addAll(volunteers);			
			
			model.addAttribute("volunteers", receivers);	
		}		
		return "/admin/inbox";
	}
	
	@RequestMapping(value="/view_message/{msgID}", method=RequestMethod.GET)
	public String viewMessage(@PathVariable("msgID") int id, SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
		int userId = loggedInUser.getUserID();		
		Message m;		
		m = messageManager.getMessageByID(id);		
		
		if (!(m.getRecipient() == userId))
			return "redirect:/403";
		
		if (!(m.isRead()))
		{
			HttpSession session = request.getSession();
			if (session.getAttribute("unread_messages") != null)
			{	//mark the mail as read
				messageManager.markAsRead(id);
				//update indicator of unread 
				int iUnRead = Integer.parseInt(session.getAttribute("unread_messages").toString());
				iUnRead = iUnRead - 1;
				
				session.setAttribute("unread_messages", iUnRead);
				model.addAttribute("unread", iUnRead);
			}			
		}
		model.addAttribute("message", m);
		TapestryHelper.setUnreadMessage(request, model, messageManager);
		
		if (request.isUserInRole("ROLE_USER"))
			return "/volunteer/view_message";
		else
			return "/admin/view_message";
	}
	
	@RequestMapping(value="/dismiss/{announcement}", method=RequestMethod.GET)
	public String dismissAnnouncement(@PathVariable("announcement") int id, SecurityContextHolderAwareRequestWrapper request)
	{	
		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
		int userId = loggedInUser.getUserID();
		Message m;		
		m = messageManager.getMessageByID(id);		

		if (!(m.getRecipient() == userId))
			return "redirect:/403";
		
		messageManager.markAsRead(id);
		
		return "redirect:/";
	}

	@RequestMapping(value="/send_message", method=RequestMethod.POST)
	public String sendMessage(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{			
		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
		Message m = new Message();
		String subject = request.getParameter("msgSubject");
		String[] recipients = request.getParameterValues("recipient");
//		User recipient; //for sending email to 
		
		m.setSender(loggedInUser.getName());
		m.setSenderID(loggedInUser.getUserID());
		m.setText(request.getParameter("msgBody"));
				
		if (request.isUserInRole("ROLE_USER"))//login user is volunteer			
		{	
			if(recipients != null) {System.out.println("recipients are not null");
				for (String recipientIDAsString: recipients)
				{ //Annoyingly, the request comes as strings
					int recipientID = Integer.parseInt(recipientIDAsString);
					m.setRecipient(recipientID);
					m.setSubject(subject);
					messageManager.sendMessage(m);
					
//					recipient = userManager.getUserByID(recipientID);
//					TapestryHelper.sendMessageByEmail(recipient, "Tapestry: New message notification", 
//							"You have received a message. To review it, log into Tapestry and open your inbox.");				
				}//end of for loop
			} //end of if(recipients != null)
			else 
				return "redirect:/inbox?failure=true";
		}//end of if ("ROLE_USER".equals(role))
		else// login as Admin/central Admin
		{
			if (request.getParameter("isAnnouncement") != null && request.getParameter("isAnnouncement").equals("true"))
			{ //Sound to all volunteers
				List<User> volunteers = userManager.getAllUsersWithRole("ROLE_USER");
				
				for (User u: volunteers){
					subject = "ANNOUNCEMENT: " + subject;
					m.setSubject(subject);
					m.setRecipient(u.getUserID());
					messageManager.sendMessage(m);
					
//					recipient = userManager.getUserByID(u.getUserID());
//					TapestryHelper.sendMessageByEmail(recipient, "Tapestry: New message notification", 
//							"You have received an announcement. To review it, log into Tapestry and open your inbox.");					
				}					
			}
			else
			{
				if(recipients != null) {
					for (String recipientIDAsString: recipients)
					{ //Annoyingly, the request comes as strings
						int recipientID = Integer.parseInt(recipientIDAsString);
						m.setRecipient(recipientID);
						m.setSubject(subject);
						messageManager.sendMessage(m);
						
//						recipient = userManager.getUserByID(recipientID);
//						TapestryHelper.sendMessageByEmail(recipient, "Tapestry: New message notification", 
//								"You have received an announcement. To review it, log into Tapestry and open your inbox.");				
					}//end of for loop
				} //end of if(recipients != null)
				else 
					return "redirect:/inbox?failure=true";
			}			
		}
		return "redirect:/inbox?success=true";	
	}
	
	@RequestMapping(value="/delete_message/{msgID}", method=RequestMethod.GET)
	public String deleteMessage(SecurityContextHolderAwareRequestWrapper request, 
			@RequestParam(value="isRead", required=true) boolean isRead, @PathVariable("msgID") int id, ModelMap model)
	{//if an unread message is deleted, unread indicator should be modified				
		if (!isRead){
			HttpSession session = request.getSession();
			if (session.getAttribute("unread_messages") != null)
			{				
				int iUnRead = Integer.parseInt(session.getAttribute("unread_messages").toString());
				iUnRead = iUnRead - 1;
				
				session.setAttribute("unread_messages", iUnRead);
				model.addAttribute("unread", iUnRead);
			}
		}
		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
		String loggedInUserName = loggedInUser.getName();
		Message message = messageManager.getMessageByID(id);
		messageManager.deleteMessage(id);
		//archive deleted message
		messageManager.archiveMessage(message, loggedInUserName);
		
		//add log
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUserName);
		sb.append(" deleted a message ");		
		userManager.addUserLog(sb.toString(), loggedInUser);	
		
		return "redirect:/inbox";
	}
	
	@RequestMapping(value="/reply_to/{msgID}", method=RequestMethod.POST)
	public String replyToMessage(@PathVariable("msgID") int id, ModelMap model, SecurityContextHolderAwareRequestWrapper request)
	{
		Message oldMsg = messageManager.getMessageByID(id);
		Message newMsg = new Message();
		//Reverse sender and recipient
		int newRecipient = userManager.getUserByID(oldMsg.getRecipient()).getUserID();		
		
		newMsg.setSenderID(newRecipient);
		newMsg.setRecipient(oldMsg.getSenderID());
		newMsg.setText(request.getParameter("msgBody"));
		newMsg.setSubject("RE: " + oldMsg.getSubject());
		
		messageManager.sendMessage(newMsg);

		return "redirect:/inbox";
	}

	//Error pages
	@RequestMapping(value="/403", method=RequestMethod.GET)
	public String forbiddenError()
	{
		return "error-forbidden";
	}
	
	@RequestMapping(value="/update_user", method=RequestMethod.POST)
	public String updateUser(SecurityContextHolderAwareRequestWrapper request)
	{
		String currentUsername = request.getUserPrincipal().getName();

		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		User u = new User();
		u.setUserID(loggedInUser.getUserID());
		u.setUsername(request.getParameter("volUsername"));
		u.setName(request.getParameter("volName"));
		u.setEmail(request.getParameter("volEmail"));
		userManager.modifyUser(u);
				
		if (!(currentUsername.equals(u.getUsername())))
			return "redirect:/login?usernameChanged=true";
		else
			return "redirect:/profile";
	}
	
	@RequestMapping(value="/change_password/{id}", method=RequestMethod.POST)
	public String changePassword(@PathVariable("id") int userID, SecurityContextHolderAwareRequestWrapper request)
	{
		String newPassword;
		String target;
		if (request.isUserInRole("ROLE_USER"))
		{
			String currentPassword = request.getParameter("currentPassword");
			newPassword = request.getParameter("newPassword");
			String confirmPassword = request.getParameter("confirmPassword");
			if (!newPassword.equals(confirmPassword)){
				return "redirect:/profile?error=confirm";
			}
			if (!userManager.userHasPassword(userID, currentPassword)){
				return "redirect:/profile?error=current";
			}
			target = "redirect:/profile?success=true";
		} 
		else 
		{			
			newPassword = request.getParameter("newPassword");
			target = "redirect:/manage_users";
		}
		
		if (!request.getParameter("newPassword").equals(""))
		{
			ShaPasswordEncoder enc = new ShaPasswordEncoder();
			String hashedPassword = enc.encodePassword(newPassword, null);
			userManager.setPasswordForUser(userID, hashedPassword);
			
			User whosePwdChanged = userManager.getUserByID(userID);
			User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
			StringBuffer sb = new StringBuffer();
			sb.append(loggedInUser.getName());
			sb.append(" has changed password for ");
			sb.append(whosePwdChanged.getName());
			userManager.addUserLog(sb.toString(), loggedInUser);	
		}
	
		return target;
	}
	
	@RequestMapping(value="/remove_picture/{id}", method=RequestMethod.GET)
	public String removePicture(@PathVariable("id") int pictureID)
	{
		pictureManager.removePicture(pictureID);
		return "redirect:/profile";
	}
	
	@RequestMapping(value="/user_logs/{page}", method=RequestMethod.GET)
	public String viewUserLogs(@PathVariable("page") int page, SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
		int organizationId = loggedInUser.getOrganization();
		
		List<UserLog> logs = new ArrayList<UserLog>();
		int count = 0;
		
		if (request.isUserInRole("ROLE_ADMIN"))
		{
			logs = userManager.getUserLogs((page - 1) * 100, 100);
			count = userManager.count();
		}
		else
		{
			logs = userManager.getUserLogsPageByGroup((page - 1) * 100, 100, organizationId);			
			count = userManager.countEntriesByGroup(organizationId);
		}
				
		model.addAttribute("numPages", count / 100 + 1);
		model.addAttribute("logs", logs);
		
		TapestryHelper.setUnreadMessage(request, model, messageManager);
		
		return "/admin/user_logs";
	}
	
	@RequestMapping(value="/user_logs/{page}", method=RequestMethod.POST)
	public String viewFilteredUserLogs(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		List<UserLog> logs = new ArrayList<UserLog>();
		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
		String name = request.getParameter("name");
		
		if (request.isUserInRole("ROLE_ADMIN"))
			logs = userManager.getUserLogsByPartialName(name);
		else
			logs = userManager.getGroupedUserLogssByPartialName(name, loggedInUser.getOrganization());

		model.addAttribute("logs", logs);
		
		TapestryHelper.setUnreadMessage(request, model, messageManager);
		
		return "/admin/user_logs";
	}
	
	@RequestMapping(value="/upload_picture_to_profile", method=RequestMethod.POST)
	public String uploadPicture(MultipartHttpServletRequest request)
	{
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		MultipartFile pic = request.getFile("pic");
		
		pictureManager.uploadPicture(pic, loggedInUser.getUserID(), true);
		userManager.addUserLog(loggedInUser.getName() +" uploaded picture for profile", loggedInUser);
		
		return "redirect:/profile";
	}

	@RequestMapping(value="/upload_picture_for_patient/{patientID}", method=RequestMethod.POST)
	public String uploadPicture(@PathVariable("patientID") int id, MultipartHttpServletRequest request)
	{
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		MultipartFile pic = request.getFile("pic");
		
		Patient p = patientManager.getPatientByID(id);
		pictureManager.uploadPicture(pic, id, false);

		userManager.addUserLog(loggedInUser.getName() + " uploaded picture for " + p.getDisplayName(), loggedInUser);
		
		return "redirect:/patient/" + id;
	}
			
	//===================== Client(patient) OLD_NEEDS TO BE REMOVED  =============================//
   	@RequestMapping(value="/manage_patients", method=RequestMethod.GET)
	public String managePatients(ModelMap model, SecurityContextHolderAwareRequestWrapper request)
   	{   		
   		User loggedInUser = TapestryHelper.getLoggedInUser(request);
   		HttpSession session = request.getSession();
   		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		else
		{
			int unreadMessages = messageManager.countUnreadMessagesForRecipient(loggedInUser.getUserID());
			model.addAttribute("unread", unreadMessages);
		}	
		TapestryHelper.loadPatientsAndVolunteers(model, volunteerManager, patientManager, organizationManager, request);
		//clinic
		List<Clinic> clinics;
		if (request.isUserInRole("ROLE_ADMIN"))//central admin
			clinics = organizationManager.getAllClinics();
		else
			clinics = organizationManager.getClinicsBySite(loggedInUser.getSite());
		
		model.addAttribute("clinics", clinics);

		return "admin/manage_patients";
	}

	//===================== ADD CLIENT  =============================//
   	@RequestMapping(value="/add_client", method=RequestMethod.GET)
	public String addClient(ModelMap model, SecurityContextHolderAwareRequestWrapper request)
   	{   		
   		User loggedInUser = TapestryHelper.getLoggedInUser(request);
   		HttpSession session = request.getSession();
   		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		else
		{
			int unreadMessages = messageManager.countUnreadMessagesForRecipient(loggedInUser.getUserID());
			model.addAttribute("unread", unreadMessages);
		}	
		TapestryHelper.loadPatientsAndVolunteers(model, volunteerManager, patientManager, organizationManager, request);
		//clinic and site
		List<Clinic> clinics;
		if (request.isUserInRole("ROLE_ADMIN"))//central admin
		{
			clinics = organizationManager.getAllClinics();
			List<Site> sites = organizationManager.getAllSites();
			model.addAttribute("sites", sites);
		}
		else
			clinics = organizationManager.getClinicsBySite(loggedInUser.getSite());			
		model.addAttribute("clinics", clinics);
		
		List<String> existResearchIds = patientManager.getResearchIds(loggedInUser.getSite());
						
		String ids = existResearchIds.toString().trim();
		model.addAttribute("researchIds", ids);

		return "admin/add_client";
	}
   	
   	@RequestMapping(value="/search_patient", method=RequestMethod.POST)
   	public String searchPatientByVolunteer(ModelMap model, SecurityContextHolderAwareRequestWrapper request)
   	{
   		String strVId = request.getParameter("search_volunteer_forPatient");		
		List<Patient> patients = new ArrayList<Patient>();
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		
		if (!Utils.isNullOrEmpty(strVId))
		{
			patients = patientManager.getPatientsForVolunteer(Integer.parseInt(strVId));
							
			if (patients.size() == 0 )  
				model.addAttribute("emptyPatients", true);				
		}		
		
		if(request.isUserInRole("ROLE_ADMIN")) 
			volunteers = volunteerManager.getAllVolunteers();
		else
		{
			User loggedInUser = TapestryHelper.getLoggedInUser(request);
			volunteers = volunteerManager.getAllVolunteersByOrganization(loggedInUser.getOrganization());
		}
			
		model.addAttribute("patients", patients);	
		model.addAttribute("volunteers", volunteers);	
		model.addAttribute("selectedVolunteer", strVId);
		
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
			
		return "/admin/manage_patients";   		
   	}
   	
   	@RequestMapping(value="/add_patient", method=RequestMethod.POST)
	public String addPatient(SecurityContextHolderAwareRequestWrapper request, ModelMap model) 
			throws JAXBException, DatatypeConfigurationException, Exception
	{   //Add a new patient   		
		Patient p = new Patient();		
		int vId1 = Integer.parseInt(request.getParameter("volunteer1"));
		int vId2 = Integer.parseInt(request.getParameter("volunteer2"));
		if (vId1 == vId2)
		{
			model.addAttribute("sameVolunteer",true);
			TapestryHelper.loadPatientsAndVolunteers(model, volunteerManager, patientManager,  organizationManager,request);
			
			return "admin/view_clients";
		}		
		Volunteer v1 = volunteerManager.getVolunteerById(vId1);
		Volunteer v2 = volunteerManager.getVolunteerById(vId2);
			
		String firstName = request.getParameter("firstname").trim();
		String lastName = request.getParameter("lastname").trim();
		p.setFirstName(firstName);
		p.setLastName(lastName);
		if(request.getParameter("preferredname") != "") 
			p.setPreferredName(request.getParameter("preferredname").trim());
		p.setVolunteer(vId1);
		p.setPartner(vId2);		
		p.setMyoscarVerified(request.getParameter("myoscar_verified"));		
		p.setGender(request.getParameter("gender"));
		p.setNotes(request.getParameter("notes"));
		p.setAlerts(request.getParameter("alerts"));
		int clinic = Integer.parseInt(request.getParameter("clinic"));
		p.setClinic(clinic);
		p.setUserName(request.getParameter("username_myoscar"));
		String tapUserName;
		if (request.getParameter("tap_username") == null)
			tapUserName = " ";	
		else
			tapUserName = request.getParameter("tap_username");
		p.setTapUsername(tapUserName);			
		ShaPasswordEncoder enc = new ShaPasswordEncoder();
		String tapPassword = enc.encodePassword(request.getParameter("tap_password"), null);
		p.setTapPassword(tapPassword);
			
		//If the string is blank, save as 0;
		p.setMrp(StringUtils.isNotBlank(request.getParameter("mrp")) ? Integer.parseInt(request.getParameter("mrp")) : 0);
		p.setMrpFirstName(request.getParameter("mrp_firstname"));
		p.setMrpLastName(request.getParameter("mrp_lastname"));
		//Get Research ID from JSP and set it. 
		p.setResearchID(request.getParameter("researchid"));
			
		int newPatientID = patientManager.createPatient(p);	
		int site = organizationManager.getSiteByClinic(clinic);
		
		StringBuffer sb = new StringBuffer();
		
		//add new client to User table 
		if (newPatientID != 0)
		{
			User user = new User();
			sb = new StringBuffer();
			sb.append(firstName);
			sb.append(" ");
			sb.append(lastName);
			user.setName(sb.toString());
			user.setUsername(tapUserName);
			user.setRole("ROLE_CLIENT");						
			user.setPassword(tapPassword);
			user.setSite(site);
			user.setEmail(" ");	
			user.setOrganization(0);
			
			userManager.createUser(user);	
		}		
		//add logs
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
			
		sb.append(loggedInUser.getName());
		sb.append(" has added a new patient ");
		sb.append(p.getFirstName());
		sb.append(" ");
		sb.append(p.getLastName());
		userManager.addUserLog(sb.toString(), loggedInUser);
			
		//Auto assign all existing surveys			
		List<SurveyTemplate> surveyTemplates = surveyManager.getDefaultSurveyTemplatesBySite(site);
	   	
	   	for(SurveyTemplate st: surveyTemplates) 
	   	{
	   		SurveyResult sr = new SurveyResult();
	   		sr.setSurveyID(st.getSurveyID());
	   		sr.setPatientID(newPatientID);		            
	   		//set today as startDate
	   		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");		        	
	   		sr.setStartDate(sdf.format(new Date()));
	   		
	   		TapestryPHRSurvey blankSurvey = new SurveyFactory().getSurveyTemplate(st);
	   		blankSurvey.setQuestions(new ArrayList<SurveyQuestion>());// make blank survey
	   		sr.setResults(SurveyAction.updateSurveyResult(blankSurvey));
	   		String documentId = surveyManager.assignSurvey(sr);
	   		blankSurvey.setDocumentId(documentId);   
	   	}

	   	model.addAttribute("createPatientSuccessfully",true);
   		TapestryHelper.loadPatientsAndVolunteers(model, volunteerManager, patientManager,  organizationManager,request);
//	   	if (TapestryHelper.isMatchVolunteer(v1, v2))
//		{
//	   		model.addAttribute("createPatientSuccessfully",true);
//	   		TapestryHelper.loadPatientsAndVolunteers(model, volunteerManager, patientManager,  organizationManager,request);
//	   	}	
//	        
//	   	//If there is a mismatch in Volunteer experience level, send a warning message. 
//	    if (!TapestryHelper.isMatchVolunteer(v1, v2))
//		{
//	    	model.addAttribute("misMatchedVolunteer",true);
//			TapestryHelper.loadPatientsAndVolunteers(model, volunteerManager, patientManager,  organizationManager,request);
//		}

		return "admin/view_clients";
	}
   	
   	@RequestMapping(value="/edit_patient/{id}", method=RequestMethod.GET)
	public String editPatientForm(@PathVariable("id") int patientID,SecurityContextHolderAwareRequestWrapper request, ModelMap model)
   	{   
		Patient p = patientManager.getPatientByID(patientID);		
		if("Male".equalsIgnoreCase(p.getGender()))
			p.setGender("M");
		else if ("Female".equalsIgnoreCase(p.getGender()))
			p.setGender("F");
		else 
			p.setGender("O");		
		model.addAttribute("patient", p);
		
		List<Volunteer> volunteers;
		if (request.isUserInRole("ROLE_ADMIN"))
			volunteers = volunteerManager.getAllVolunteers();				
		else	
			volunteers = volunteerManager.getAllVolunteersByOrganization(TapestryHelper.getLoggedInUser(request).getOrganization());	
		model.addAttribute("volunteers", volunteers);	
		
		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)		
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		//clinic
		List<Clinic> clinics;
		if (request.isUserInRole("ROLE_ADMIN"))//central admin
			clinics = organizationManager.getAllClinics();
		else
			clinics = organizationManager.getClinicsBySite(TapestryHelper.getLoggedInUser(request).getSite());
				
		model.addAttribute("clinics", clinics);
		
		return "/admin/edit_patient"; //Why this one requires a slash when none of the others do, I do not know.
	}
   	@RequestMapping(value="/submit_edit_patient/{id}", method=RequestMethod.POST)
	public String modifyPatient(@PathVariable("id") int patientID, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model)
   	{
   		int vId1 = Integer.parseInt(request.getParameter("volunteer1"));
		int vId2 = Integer.parseInt(request.getParameter("volunteer2"));
		Volunteer v1 = volunteerManager.getVolunteerById(vId1);
		Volunteer v2 = volunteerManager.getVolunteerById(vId2);
		
		if (TapestryHelper.isMatchVolunteer(v1, v2)){
			Patient p = new Patient();
			p.setPatientID(patientID);
			p.setFirstName(request.getParameter("firstname"));
			p.setLastName(request.getParameter("lastname"));
			p.setPreferredName(request.getParameter("preferredname"));		
			p.setVolunteer(vId1);
			p.setPartner(vId2);
			p.setGender(request.getParameter("gender"));
			p.setNotes(request.getParameter("notes"));
			p.setClinic(Integer.parseInt(request.getParameter("clinic")));
			p.setAlerts(request.getParameter("alerts"));
			p.setMyoscarVerified(request.getParameter("myoscar_verified"));
			p.setUserName(request.getParameter("username_myoscar"));
			p.setMrp(Integer.parseInt(request.getParameter("mrp")));
			p.setMrpFirstName(request.getParameter("mrp_firstname"));
			p.setMrpLastName(request.getParameter("mrp_lastname"));
			p.setResearchID(request.getParameter("researchid"));
						
			if (request.getParameter("tap_username") == null)
				p.setTapUsername(" ");		
			else
				p.setTapUsername(request.getParameter("tap_username"));	
			
			patientManager.updatePatient(p);
			model.addAttribute("updatePatientSuccessfully",true);
			
			User loggedInUser = TapestryHelper.getLoggedInUser(request);
			StringBuffer sb = new StringBuffer();
			sb.append(loggedInUser.getName());
			sb.append(" has modified the patient ");
			sb.append(p.getFirstName());
			sb.append(" ");
			sb.append(p.getLastName());
			userManager.addUserLog(sb.toString(), loggedInUser);
		}
		else
			model.addAttribute("misMatchedVolunteer",true);		
		
		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)		
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		TapestryHelper.loadPatientsAndVolunteers(model, volunteerManager, patientManager,  organizationManager,request);
        
		return "/admin/view_clients";
	}
   	
   	@RequestMapping(value="/remove_patient/{patient_id}", method=RequestMethod.GET)
	public String removePatient(@PathVariable("patient_id") int id, SecurityContextHolderAwareRequestWrapper request)
   	{
   		Patient p = patientManager.getPatientByID(id);   		
   		patientManager.deletePatientWithId(id);
   		
   		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has deleted the patient ");
		sb.append(p.getFirstName());
		sb.append(" ");
		sb.append(p.getLastName());
		userManager.addUserLog(sb.toString(), loggedInUser);
   		
		return "redirect:/manage_patients";
	}

	@RequestMapping(value="/patient/{patient_id}", method=RequestMethod.GET)
	public String viewPatient(@PathVariable("patient_id") int id, @RequestParam(value="complete", required=false)
					String completedSurvey, @RequestParam(value="aborted", required=false) String inProgressSurvey, 
					@RequestParam(value="appointmentId", required=false) Integer appointmentId, 
					@RequestParam(value="showAuthenticationMsg", required=false) boolean showAuthenticationMsg,
					@RequestParam(value="goalsMsg", required=false) boolean goalsMsg,
					SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{	
		Patient patient = patientManager.getPatientByID(id);
		//Find the name of the current user
		User u = TapestryHelper.getLoggedInUser(request);
		HttpSession session = request.getSession();
		
		int volunteerId =0;
		if (session.getAttribute("logged_in_volunteer") != null)
			volunteerId = Integer.parseInt(session.getAttribute("logged_in_volunteer").toString());
		
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

		List<SurveyResult> completedSurveyResultList = surveyManager.getCompletedSurveysByPatientID(id);
		List<SurveyResult> incompleteSurveyResultList = surveyManager.getIncompleteSurveysByPatientID(id);
		
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
		int site = u.getSite();		
		completedDisplayedResults = TapestryHelper.getDetailedAnswerForSurveys(completedDisplayedResults, site);

		List<Patient> patientsForUser = patientManager.getPatientsForVolunteer(volunteerId);	
		
		if (appointmentId == null)
		{
			appointmentId = TapestryHelper.getAppointmentId(request);
		}
		Appointment appointment = appointmentManager.getAppointmentById(appointmentId);
		
		model.addAttribute("appointment", appointment);
		model.addAttribute("patients", patientsForUser);
		model.addAttribute("completedSurveys", completedSurveyResultList);
		model.addAttribute("inProgressSurveys", incompleteSurveyResultList);
		model.addAttribute("displayResults", completedDisplayedResults);
		
//		model.addAttribute("surveys", surveyList);
		if (showAuthenticationMsg)
			model.addAttribute("showAuthenticationMsg", true);
		if (goalsMsg)
			model.addAttribute("goalsMsg", goalsMsg);
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
		
		userManager.addUserLog(sb.toString(), u);					
		//save selected appointmentId in the session for other screen, like narrative		
		session.setAttribute("appointmentId", appointmentId);
		session.setAttribute("patientId", id);
				
		return "/patient";
	}
	
	@RequestMapping(value="/view_clients_admin", method=RequestMethod.GET)
	public String viewPatientsFromAdmin(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
//		User loggedInUser = TapestryHelper.getLoggedInUser(request);
//		HttpSession session = request.getSession();
//		List<Patient> patients = TapestryHelper.getAllPatientsWithFullInfos(patientManager, request);	
//		try{
//			TapestryHelper th = new TapestryHelper();
//			th.readProperties();
//			
//			String adminTest = TapestryHelper.getProperties("ubcSurveys.properties", "Admin");
//		
//		}catch (Exception e)
//		{
//			System.out.println("sss");
//		}
		
		List<Patient> patients, disabledPatients;
		User user = TapestryHelper.getLoggedInUser(request);
		HttpSession session = request.getSession();
		int siteId = user.getSite();
		if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole()))
		{
			patients = patientManager.getAllPatients();	//For central Admin		
			disabledPatients = patientManager.getAllDisabledPatients();
		}
		else
		{
			patients = patientManager.getPatientsBySite(siteId);	
			disabledPatients = patientManager.getAllDisabledPatients(siteId);
		}		
		model.addAttribute("patients", patients);	
		
		if ((disabledPatients != null) && (disabledPatients.size() > 0))
			model.addAttribute("disabledPatients", disabledPatients);	
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
	
		return "/admin/view_clients";
	}
	
	//display all patients by search name
	@RequestMapping(value="/view_clients_admin", method=RequestMethod.POST)
	public String viewPatientsBySelectedName(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		String name = request.getParameter("searchName");
		HttpSession session = request.getSession();
		List<Patient> patients = new ArrayList<Patient>();
		User user = TapestryHelper.getLoggedInUser(request, userManager);	
		
		if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole()))// for central Admin
			patients = patientManager.getPatientsByPartialName(name);	
		else
			patients = patientManager.getGroupedPatientsByName(name, user.getOrganization());
	
		model.addAttribute("searchName", name);	 
		model.addAttribute("patients", patients);
		
		if (session.getAttribute("unread_messages") != null)		
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		return "/admin/view_clients";		
	}
	
	@RequestMapping(value="/display_client/{patient_id}",method=RequestMethod.GET)
	public String displayPatientDetails(@PathVariable("patient_id") int id, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		Patient patient = patientManager.getPatientByID(id);
		model.addAttribute("patient", patient);

		String volunteer1Name = volunteerManager.getVolunteerById(patient.getVolunteer()).getDisplayName();
		String volunteer2Name = volunteerManager.getVolunteerById(patient.getPartner()).getDisplayName();
		
		model.addAttribute("volunteer1", volunteer1Name);
		model.addAttribute("volunteer2", volunteer2Name);
				
		List<Appointment> appointments = new ArrayList<Appointment>();
		appointments = appointmentManager.getAllUpcommingAppointmentForPatient(id);
				
		model.addAttribute("upcomingVisits", appointments);
		
		appointments = new ArrayList<Appointment>();		
		appointments = appointmentManager.getAllCompletedAppointmentsForPatient(id);
		model.addAttribute("completedVisits", appointments);
		
		appointments = new ArrayList<Appointment>();
		appointments = appointmentManager.getAllUnCompletedAppointmentsForPatient(id);
		model.addAttribute("unCompletedVisits", appointments);
		
		List<SurveyResult> surveys = surveyManager.getSurveysByPatientID(id);
		model.addAttribute("surveys", surveys);
		
		int site = organizationManager.getSiteByClinic(patient.getClinic());
		model.addAttribute("site", site);
	
		if (site!=3 && surveyManager.hasCompletedAllSurveysForReport(id, site))
			model.addAttribute("showReport", true);
		
		HttpSession session = request.getSession();				
 		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));	
 		
		return "/admin/display_client";
	}
	
	@RequestMapping(value="/disable_patient/{patient_id}",method=RequestMethod.POST)
	public String disablePatient(@PathVariable("patient_id") int id, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{		
		String notes = request.getParameter("noteBody");
		patientManager.disablePatient(id, notes);
		
		//add log
		User u = TapestryHelper.getLoggedInUser(request, userManager);		
		StringBuffer sb = new StringBuffer();
		sb.append(u.getName());
		sb.append(" has disabled patient #");
		sb.append(id);
		userManager.addUserLog(sb.toString(), u);
		
		return "redirect:/view_clients_admin"; 
	}

	@RequestMapping(value="/enable_patient/{patientId}",method=RequestMethod.GET)
	public String enablePatient(SecurityContextHolderAwareRequestWrapper request, @PathVariable("patientId") int id, 
			ModelMap model)
	{
		patientManager.enablePatientWithID(id);
		
		//add log
		User u = TapestryHelper.getLoggedInUser(request, userManager);		
		StringBuffer sb = new StringBuffer();
		sb.append(u.getName());
		sb.append(" has enabled patient #");
		sb.append(id);
		userManager.addUserLog(sb.toString(), u);
		
		return "redirect:/view_clients_admin"; 
	}
	
	@RequestMapping(value="/call_coordinator_SOS/{patientName}", method=RequestMethod.GET)
	public String callCoordinatorForSOSAlert (SecurityContextHolderAwareRequestWrapper request, ModelMap model, 
			@PathVariable("patientName") String patientName, @RequestParam(value="type", required=true) int type)
	{
		User loggedInUser = TapestryHelper.getLoggedInUser(request);		
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" is reporting of risk for ");
		if (type ==1)
			sb.append("ELDER ABUSE");
		else if (type == 2)
			sb.append("SELF HARM");
		else
			sb.append("CRISIS LINES");
		sb.append(" for patient ");
		sb.append(patientName);
		
		//send message to Marianne Hannon and Katharine May, their's userId are stored in tapestry.properties 
		try
		{
			String sosReceiver = TapestryHelper.getProperties("SOSReceiver");
						
			List<String> receivers = new ArrayList<String>(Arrays.asList(sosReceiver.split(",")));
			int rID;
			for (int i=0; i< receivers.size(); i++)
			{
				rID = Integer.valueOf(receivers.get(i));
				TapestryHelper.sendMessageToInbox("SOS alert", sb.toString(), loggedInUser.getUserID(), rID, messageManager);			
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		//log
		userManager.addUserLog(sb.toString(), loggedInUser);
//		return "redirect:/";
		
		int patientId = TapestryHelper.getPatientId(request);
		int appointmentId = TapestryHelper.getAppointmentId(request);
		return "redirect:/patient/" + patientId + "?appointmentId=" + appointmentId;
	}
	
	//============report======================
	@RequestMapping(value="/generate_report_hl7/{patientID}", method=RequestMethod.GET)
	@ResponseBody
	public String generateHL7Report (@PathVariable("patientID") int id,
			@RequestParam(value="appointmentId", required=true) int appointmentId, 	
			ModelMap model, HttpServletResponse response, SecurityContextHolderAwareRequestWrapper request) throws Exception
	{	
		Patient patient = patientManager.getPatientByID(id);		
		Appointment appointment = appointmentManager.getAppointmentById(appointmentId);
		HL7Report report = new HL7Report();		
		ScoresInReport scores = new ScoresInReport();
		
		HttpSession session = request.getSession();
//		List<Patient> patients = new ArrayList<Patient>();
		report.setPatient(patient);
		
		// Key Observations
		String keyObservation = appointmentManager.getKeyObservationByAppointmentId(appointmentId);
		report.setPatient(patient);
		appointment.setKeyObservation(keyObservation);				
		report.setAppointment(appointment);
		
		//set user
		User loginUser = TapestryHelper.getLoggedInUser(request);		
		report.setUser(loginUser);
		
		//Survey---  goals setting
		List<SurveyResult> surveyResultList = surveyManager.getCompletedSurveysByPatientID(id);
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
			//added second condition survey.getResultId == 0, for each survey, a patient could have multiple survey result
			// only the first time is used for report
			if (title.equalsIgnoreCase("1. Daily Life Activities") && (dailyLifeActivitySurvey.getResultID()==0))//Daily life activity survey
				dailyLifeActivitySurvey = survey;
			
			if (title.equalsIgnoreCase("Nutrition") && (nutritionSurvey.getResultID()==0))//Nutrition
				nutritionSurvey = survey;
			
			if (title.equalsIgnoreCase("Physical Activity") && (rAPASurvey.getResultID()==0))//RAPA survey
				rAPASurvey = survey;
			
			if (title.equalsIgnoreCase("Mobility") && (mobilitySurvey.getResultID()==0))//Mobility survey
				mobilitySurvey = survey;
			
			if (title.equalsIgnoreCase("4. Social Life") && (socialLifeSurvey.getResultID()==0)) //Social Life(Duke Index of Social Support)
				socialLifeSurvey = survey;
			
			if (title.equalsIgnoreCase("General Health") && (generalHealthySurvey.getResultID()==0)) //General Health(Edmonton Frail Scale)
				generalHealthySurvey = survey;
			
			if (title.equalsIgnoreCase("Memory") && (memorySurvey.getResultID()==0)) //Memory Survey
				memorySurvey = survey;
			
			if (title.equalsIgnoreCase("Advance Directives") && (carePlanSurvey.getResultID()==0)) //Care Plan/Advanced_Directive survey
				carePlanSurvey = survey;
			
			if (title.equalsIgnoreCase("Goals") && (goals.getResultID()==0))
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
   		int size = qList.size();
   		if (size ==7) //No for fall question 
   			qList.set(6, "No"); //translate
   		else
   		{
   			qList.set(6, "Yes" + ". " + qList.get(7));
   			qList.remove(7);
   			lAlert.add(AlertsInReport.DAILY_ACTIVITY_ALERT); //set alert      			
   		}   		
   		//combine Q2 and Q3
   		StringBuffer sb = new StringBuffer();
   		sb.append(qList.get(1));
   		sb.append("; ");
   		sb.append(qList.get(2));
   		qList.set(1, sb.toString());
   		qList.remove(2);
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
		int socialSupportSize = qList.size();
		if ((qList != null)&&(socialSupportSize>0))
		{
			int satisfactionScore = CalculationManager.getScoreByQuestionsList(qList.subList(0, 6));
			scores.setSocialSatisfication(satisfactionScore);
			
			int networkScore = CalculationManager.getSocialSupportNetworkScore(qList.subList(7, socialSupportSize));			
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
		scores.setAerobicMessage(aerobicMsg);
		scores.setpAStrengthAndFlexibility(sFPAScore);
						
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
		
		if ((qList != null) && (qList.size()>0))
		{					
			report.setPatientGoals(CalculationManager.getPatientGoals(qList));
			report.setLifeGoals(CalculationManager.getLifeOrHealthGoals(qList, 1));
			report.setHealthGoals(CalculationManager.getLifeOrHealthGoals(qList, 2));
		}
		//get volunteer information
		List<String> volunteerInfos = new ArrayList<String>();
		volunteerInfos.add(appointment.getVolunteer());
		volunteerInfos.add(appointment.getPartner());
		volunteerInfos.add(appointment.getComments());
		
		report.setVolunteerInformations(volunteerInfos);		
		//populate hl7 message		
		String messageText = Hl7Utils.populateORUMessage(report);   
		//////////////////////////////////
		
//		Hl7Utils myTest = new Hl7Utils();
//		myTest.test();
		
		///////////////////////////////////
		//save hl7 message in a file store on tomcat/webapps/hl7
//		StringBuffer sb = new StringBuffer();
//		sb.append(String.valueOf(patient.getPatientID()));		
	//	Hl7Utils.save(messageText, sb.toString());
		
		//save or open hl7 message in local file system
		sb = new StringBuffer();
		sb.append("TR");
		sb.append(patient.getPatientID());
		sb.append(".hl7");
		String filename= sb.toString();
		response.setContentType("text/hl7");
   		response.setContentLength(messageText.length());
   		response.setHeader("Content-Disposition", "attachment; filename=" + filename);  
   		try{
   			PrintWriter pw = new PrintWriter(response.getOutputStream());
   			pw.write(messageText);
   		} catch (Exception e) {
   			e.printStackTrace();
   		}   	
   		
   		//add log
   		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
   		sb = new StringBuffer();
   		sb.append(loggedInUser.getName());
   		sb.append(" download ");
   		sb.append(patient.getDisplayName());
   		sb.append(" report at ");		
   		java.util.Date date= new java.util.Date();		
   		sb.append(new Timestamp(date.getTime()));
   		userManager.addUserLog(sb.toString(), loggedInUser);	
   		
   		return messageText;
	}
	@RequestMapping(value="/download_mgReport/{patientID}", method=RequestMethod.GET)
	public String downloadMgReport(@PathVariable("patientID") int id,
			@RequestParam(value="appointmentId", required=true) int appointmentId, 	
			ModelMap model, HttpServletResponse response, SecurityContextHolderAwareRequestWrapper request)
	{	
		Appointment appointment = appointmentManager.getAppointmentById(appointmentId);
		Report report = new Report();		
		ScoresInReport scores = new ScoresInReport();	
		Patient patient = patientManager.getPatientByID(id);
		List<String> lAlert = new ArrayList<String>(); 
		
		report.setPatient(patient);
		
		//Key Observations
		String keyObservation = appointmentManager.getKeyObservationByAppointmentId(appointmentId);
		appointment.setKeyObservation(keyObservation);
		report.setAppointment(appointment);

		//Survey---  
		List<SurveyResult> surveyResultList = surveyManager.getCompletedSurveysByPatientID(id);
			
		SurveyResult ssSurvey = new SurveyResult();	// mgDuke Social Support Index
		SurveyResult nutritionSurvey = new SurveyResult(); //nutrition 
		SurveyResult rAPASurvey = new SurveyResult(); // RAPA
		SurveyResult eq5dSurvey = new SurveyResult();  //EQ5D
		SurveyResult goalsSurvey = new SurveyResult(); //Goals
		SurveyResult shSurvey = new SurveyResult(); //satisfactionHealthcare
		SurveyResult pcSurvey = new SurveyResult(); //patient Centeredness 
					
		String ssTitle="";
		String nutritionTitle = "";
		String rAPATitle = "";
		String eq5dTitle =""; 
		String goalsTitle = "";
		String shTitle = "";
		String pcTitle="";
		try{
			ssTitle = TapestryHelper.getProperties("mgSurveys.properties", "socialLife_survey");
			nutritionTitle = TapestryHelper.getProperties("mgSurveys.properties", "nutrition_survey");
			rAPATitle = TapestryHelper.getProperties("mgSurveys.properties", "rapa_survey");
			eq5dTitle = TapestryHelper.getProperties("mgSurveys.properties", "qualityOfLife_survey");
			goalsTitle = TapestryHelper.getProperties("mgSurveys.properties", "goals_survey");
			shTitle = TapestryHelper.getProperties("mgSurveys.properties", "satisfactionHealthcare_survey");
			pcTitle = TapestryHelper.getProperties("mgSurveys.properties", "patientCenteredness_survey");
		}catch (Exception e)
		{
			System.out.println("===========Has problem to read mgSurveys.properties file============");
		}	
		
		List<String> displayedSurveyTitles = new ArrayList<String>();
		displayedSurveyTitles.add(shTitle);
		displayedSurveyTitles.add(eq5dTitle);
		displayedSurveyTitles.add(rAPATitle);
		displayedSurveyTitles.add(nutritionTitle);
		displayedSurveyTitles.add(pcTitle);
		displayedSurveyTitles.add(ssTitle);
		
		for(SurveyResult survey: surveyResultList){			
			String title = survey.getSurveyTitle();
			
			//added second condition survey.getResultId == 0, for each survey, a patient could have multiple survey result
			// only the first time is used for report
			if (title.equalsIgnoreCase(ssTitle) && (ssSurvey.getResultID()==0))//Social Life(Duke Index of Social Support)
				ssSurvey = survey;
					
			if (title.equalsIgnoreCase(nutritionTitle) && (nutritionSurvey.getResultID()==0))//Nutrition
			{System.out.println("n survye...");
				nutritionSurvey = survey;
			}
					
			if (title.equalsIgnoreCase(rAPATitle) && (rAPASurvey.getResultID()==0))//RAPA survey
				rAPASurvey = survey;
					
			if (title.equalsIgnoreCase(eq5dTitle) && (eq5dSurvey.getResultID()==0))//EQ5D
				eq5dSurvey = survey;
					
			if (title.equalsIgnoreCase(goalsTitle) && (goalsSurvey.getResultID()==0)) //Goals
				goalsSurvey = survey;
					
			if (title.equalsIgnoreCase(shTitle) && (shSurvey.getResultID()==0)) //satisfactionHealthcare
				shSurvey = survey;
					
			if (title.equalsIgnoreCase(pcTitle) && (pcSurvey.getResultID()==0)) //patient centeredness Survey
				pcSurvey = survey;
		}
		
		String xml;
		List<String> qList = new ArrayList<String>();
   		List<String> questionTextList = new ArrayList<String>();
   		Map<String, String> sMap = new TreeMap<String, String>();
			
		//Social Life
		try{
   			xml = new String(ssSurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}		
		LinkedHashMap<String, String> mSocialLifeSurvey = ResultParser.getResults(xml);
		qList = new ArrayList<String>();   		
   		//get answer list
		qList = TapestryHelper.getQuestionList(mSocialLifeSurvey);
			
		
			
		//summary tools for social supports
		int socialSupportSize = qList.size();
		if ((qList != null)&&(socialSupportSize>0))
		{
			int socialLifeScore = CalculationManager.getScoreByQuestionsList(qList);
			int satisfactionScore = CalculationManager.getScoreByQuestionsList(qList.subList(0, 6));
			scores.setSocialSatisfication(satisfactionScore);
			lAlert = AlertManager.getSocialLifeAlerts(socialLifeScore, lAlert);
			
			int networkScore = CalculationManager.getSocialSupportNetworkScore(qList.subList(7, socialSupportSize));
	   		scores.setSocialNetwork(networkScore);
		}		  		   		
   		//Nutrition 			
   		try{
   			xml = new String(nutritionSurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}   		
   		LinkedHashMap<String, String> mNutritionSurvey = ResultParser.getResults(xml);
   		qList = new ArrayList<String>();   		
   		//get answer list
		qList = TapestryHelper.getQuestionList(mNutritionSurvey);  

		//get scores for nutrition survey based on answer list
		if ((qList != null)&&(qList.size()>0))
		{
			int nutritionScore = CalculationManager.getNutritionScore(qList);
			scores.setNutritionScreen(nutritionScore);			
			//high nutrition risk alert			
			lAlert = AlertManager.getNutritionAlerts(nutritionScore, lAlert, qList);
		}
		
		//RAPA 
		try{
   			xml = new String(rAPASurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mRAPASurvey = ResultParser.getResults(xml);
   		qList = new ArrayList<String>();   		
   		//get answer list
		qList = TapestryHelper.getQuestionList(mRAPASurvey);  		

		int rAPAScore = CalculationManager.getAScoreForRAPA(qList);
		int sFPAScore = CalculationManager.getSFScoreForRAPA(qList);
				
		String aerobicMsg = CalculationManager.getAerobicMsg(rAPAScore);
				
		scores.setpAAerobic(rAPAScore);
		scores.setpAStrengthAndFlexibility(sFPAScore);		
		scores.setAerobicMessage(aerobicMsg);		
		
		if (rAPAScore < 6)
			lAlert.add(AlertsInReport.PHYSICAL_ACTIVITY_ALERT);
		
		//set alerts in report bean
		report.setScores(scores);
		report.setAlerts(lAlert);
				
		//Goals
		try{
   			xml = new String(goalsSurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mGoals = ResultParser.getResults(xml);
   		questionTextList = new ArrayList<String>();
   		questionTextList = ResultParser.getSurveyQuestions(xml);
   	
   		//get answer list
		qList = TapestryHelper.getQuestionList(mGoals);   	
							
		if ((qList != null) && (qList.size()>0))
		{					
			report.setPatientGoals(CalculationManager.getPatientGoals(qList));
			report.setLifeGoals(CalculationManager.getLifeOrHealthGoals(qList, 1));
			report.setHealthGoals(CalculationManager.getLifeOrHealthGoals(qList, 2));
		}
		
		//get volunteer information
		String volunteer = appointment.getVolunteer();
		String partner = appointment.getPartner();
		String comments = appointment.getComments();
				
		Map<String, String> vMap = new TreeMap<String, String>();
		
		if (!Utils.isNullOrEmpty(volunteer))
			vMap.put(" Volunteer 1", volunteer);
		else
			vMap.put(" Volunteer 1", "");
		
		if (!Utils.isNullOrEmpty(partner))
			vMap.put(" Volunteer 2", partner);
		else
			vMap.put(" Volunteer2", "");
		
		if (!Utils.isNullOrEmpty(comments))
			vMap.put(" Volunteer Notes", comments);					
		else
			vMap.put(" Volunteer Notes", " ");
		
		report.setVolunteerInformations(vMap);	
	
   		LinkedHashMap<String, String> mSurvey;	
		Map<String, String> tMap = new LinkedHashMap<String, String>(); 		
				
		SurveyResult sr;
		DisplayedSurveyResult dsr;
		List<DisplayedSurveyResult> displayedResults;
		String title;
					
		for (int i = 0; i < surveyResultList.size(); i++)
		{
			sr = new SurveyResult();
			sr = surveyResultList.get(i);
			title = sr.getSurveyTitle();
			if (!displayedSurveyTitles.contains(title))
				continue;
			tMap.put("SurveyTitle " + (i+1), title);
			try{
	   			xml = new String(sr.getResults(), "UTF-8");
	   		} catch (Exception e) {
	   			xml = "";
	   		}
			mSurvey = ResultParser.getResults(xml);
							
			displayedResults = ResultParser.getDisplayedSurveyResults(mSurvey);	   		
			displayedResults = TapestryHelper.getDetailedAnswerForSurveys(displayedResults, 2);			
											  
			for (int j =0; j<displayedResults.size(); j++)
			{
				dsr = new DisplayedSurveyResult();
				dsr = displayedResults.get(j);	
				
				tMap.put(dsr.getQuestionText(), dsr.getQuestionAnswer());
			}
		}	
		
		TapestryHelper.buildMcGillPDFReport(report, tMap, response);
		//add log
		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" download/view ");
		sb.append(patient.getFirstName());
		sb.append(" ");
		sb.append(patient.getLastName());
		sb.append(" report at");		
		java.util.Date date= new java.util.Date();		
		sb.append(new Timestamp(date.getTime()));
		userManager.addUserLog(sb.toString(), loggedInUser);	
			
		return null;
	}
	
	@RequestMapping(value="/download_report/{patientID}", method=RequestMethod.GET)
	public String downloadReport(@PathVariable("patientID") int id,
			@RequestParam(value="appointmentId", required=true) int appointmentId, 	
			ModelMap model, HttpServletResponse response, SecurityContextHolderAwareRequestWrapper request)
	{	
		Appointment appointment = appointmentManager.getAppointmentById(appointmentId);
		Report report = new Report();		
		ScoresInReport scores = new ScoresInReport();	
		Patient patient = patientManager.getPatientByID(id);
		
		int site = patientManager.getSiteByPatientId(id);
		//call web service to get patient info from myoscar
//		HttpSession session = request.getSession();
//		List<Patient> patients = new ArrayList<Patient>();
		
		report.setPatient(patient);
		
		//Key Observations
		String keyObservation = appointmentManager.getKeyObservationByAppointmentId(appointmentId);
		appointment.setKeyObservation(keyObservation);
		report.setAppointment(appointment);

		//Survey---  
		List<SurveyResult> surveyResultList = surveyManager.getCompletedSurveysByPatientID(id);
	
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
					
			//added second condition survey.getResultId == 0, for each survey, a patient could have multiple survey result
			// only the first time is used for report
			if (title.equalsIgnoreCase("1. Daily Life Activities") && (dailyLifeActivitySurvey.getResultID()==0))//Daily life activity survey
				dailyLifeActivitySurvey = survey;
			
			if (title.equalsIgnoreCase("Nutrition") && (nutritionSurvey.getResultID()==0))//Nutrition
				nutritionSurvey = survey;
			
			if (title.equalsIgnoreCase("Physical Activity") && (rAPASurvey.getResultID()==0))//RAPA survey
				rAPASurvey = survey;
			
			if (title.equalsIgnoreCase("Mobility") && (mobilitySurvey.getResultID()==0))//Mobility survey
				mobilitySurvey = survey;
			
			if (title.equalsIgnoreCase("4. Social Life") && (socialLifeSurvey.getResultID()==0)) //Social Life(Duke Index of Social Support)
				socialLifeSurvey = survey;
			
			if (title.equalsIgnoreCase("General Health") && (generalHealthySurvey.getResultID()==0)) //General Health(Edmonton Frail Scale)
				generalHealthySurvey = survey;
			
			if (title.equalsIgnoreCase("Memory") && (memorySurvey.getResultID()==0)) //Memory Survey
				memorySurvey = survey;
			
			if (title.equalsIgnoreCase("Advance Directives") && (carePlanSurvey.getResultID()==0)) //Care Plan/Advanced_Directive survey
				carePlanSurvey = survey;
			
			if (title.equalsIgnoreCase("Goals") && (goals.getResultID()==0))
				goals = survey;	
		}
		
		String xml;
		List<String> qList = new ArrayList<String>();
   		List<String> questionTextList = new ArrayList<String>();
   		Map<String, String> sMap = new TreeMap<String, String>();
		
   		//Additional Information
  		//Memory
   		try{
   			xml = new String(memorySurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mMemorySurvey = ResultParser.getResults(xml);
   		qList = new ArrayList<String>();
   		questionTextList = new ArrayList<String>();
   		questionTextList = ResultParser.getSurveyQuestions(xml);     		
   		
   		List<String> displayQuestionTextList = new ArrayList<String>();
   		if ((questionTextList != null) && (questionTextList.size() > 0))
   		{   			
   	   		displayQuestionTextList.add(TapestryHelper.removeObserverNotes(questionTextList.get(1)));
   	   		
   	   		//get answer list
   			qList = TapestryHelper.getQuestionListForMemorySurvey(mMemorySurvey);   
   			
   			if (qList.get(0) != null && qList.get(0).equals("1") )   				
   				displayQuestionTextList.add(TapestryHelper.removeObserverNotes(questionTextList.get(3)));
   			else
   				displayQuestionTextList.add(TapestryHelper.removeObserverNotes(questionTextList.get(2)));
   			//remove redundant info 
   	   		displayQuestionTextList = TapestryHelper.removeRedundantFromQuestionText(displayQuestionTextList, "of 2");
   			
   			//make map for memory survey
   	   		sMap = new TreeMap<String, String>(); 	
   			sMap = TapestryHelper.getSurveyContentMapForMemorySurvey(displayQuestionTextList, qList);
   			 
   			report.setMemory(sMap);   		
   		}   	   		
   		//Care Plan/Advanced_Directive
   		try{
   			xml = new String(carePlanSurvey.getResults(), "UTF-8");
   	   	} catch (Exception e) {
   	   		xml = "";
   	   	}
   	   	displayQuestionTextList = new ArrayList<String>();
   	   	qList = new ArrayList<String>();
   	   	LinkedHashMap<String, String> mCarePlanSurvey = ResultParser.getResults(xml);

   	   	questionTextList = new ArrayList<String>();
   	   	questionTextList = ResultParser.getSurveyQuestions(xml);   	   		
   	   		
   	   	//take 3 question text from the list
   	   	if ((questionTextList != null)&&(questionTextList.size() > 0))
   	   	{
   	   		for (int i = 1; i <= 3; i++)
   	   			displayQuestionTextList.add(TapestryHelper.removeObserverNotes(questionTextList.get(i)));	   	   		
	   	   	displayQuestionTextList = TapestryHelper.removeRedundantFromQuestionText(displayQuestionTextList, "of 3");	
	   	   		
	   	   	//get answer list   	   	  	   	
	   	   	qList = TapestryHelper.getQuestionList(mCarePlanSurvey);	   	   		
	   	   	sMap = TapestryHelper.getSurveyContentMapForMemorySurvey(displayQuestionTextList, qList);	   	   			   	   		
	   	   	report.setCaringPlan(sMap);
   	   	}      
   	   	
   		//Daily Life Activities---Tapestry Questions
   		try{
   			xml = new String(dailyLifeActivitySurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mDailyLifeActivitySurvey = ResultParser.getResults(xml);
   		questionTextList = new ArrayList<String>();
   		questionTextList = ResultParser.getSurveyQuestions(xml);   
   	   		
   		qList = new ArrayList<String>();
   		qList = TapestryHelper.getQuestionList(mDailyLifeActivitySurvey);
   		   	   		
   		//last question in Daily life activity survey is about falling stuff
   		List<String> lAlert = new ArrayList<String>();   		
   		int size = qList.size();
   		   		
   		if (size ==7) //No for fall question 
   		{
   			qList.set(6, "No"); //translate
   			if (questionTextList.size() == 9)//for new script of daily life survey
   				questionTextList.remove(8);//remove last question text
   		}
   		else
   		{
   			qList.set(6, "Yes" + ". " + qList.get(7));
   			qList.remove(7);
   			lAlert.add(AlertsInReport.DAILY_ACTIVITY_ALERT); //set alert   		
   			
   			if (questionTextList.size() == 9)//for new script of daily life survey
   			{
	   			questionTextList.remove(9);
	   			questionTextList.remove(8);
   			}
   		}   		   		
   		//combine Q2 and Q3 answer
   		StringBuffer sb = new StringBuffer();
   		sb.append(qList.get(1));
   		sb.append("; ");
   		sb.append(qList.get(2));
   		qList.set(1, sb.toString());
   		qList.remove(2);
   		   		
   		sMap = new TreeMap<String, String>();
   		sMap = TapestryHelper.getSurveyContentMapForDailyLife(questionTextList, qList);   		
   		report.setDailyActivities(sMap);   		
   		
 		//General Healthy Alert
   		try{
   			xml = new String(generalHealthySurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
		
		LinkedHashMap<String, String> mGeneralHealthySurvey = ResultParser.getResults(xml);
		qList = new ArrayList<String>();   		
   		//get answer list
		qList = TapestryHelper.getQuestionList(mGeneralHealthySurvey);
		
		//get score info for Summary of tapestry tools
		if ((qList != null)&&(qList.size()>10))
		{		
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
		
		LinkedHashMap<String, String> mSocialLifeSurvey = ResultParser.getResults(xml);
		qList = new ArrayList<String>();   		
   		//get answer list
		qList = TapestryHelper.getQuestionList(mSocialLifeSurvey);
			
		int socialLifeScore = CalculationManager.getScoreByQuestionsList(qList);
		lAlert = AlertManager.getSocialLifeAlerts(socialLifeScore, lAlert);
		
		//summary tools for social supports
		int socialSupportSize = qList.size();
		if ((qList != null)&&(socialSupportSize>0))
		{
			int satisfactionScore = CalculationManager.getScoreByQuestionsList(qList.subList(0, 6));
			scores.setSocialSatisfication(satisfactionScore);
			
			int networkScore = CalculationManager.getSocialSupportNetworkScore(qList.subList(7, socialSupportSize));
	   		scores.setSocialNetwork(networkScore);
		}		  		   		
   		//Nutrition Alerts   		
   		try{
   			xml = new String(nutritionSurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}   		
   		LinkedHashMap<String, String> mNutritionSurvey = ResultParser.getResults(xml);
   		qList = new ArrayList<String>();   		
   		//get answer list
		qList = TapestryHelper.getQuestionList(mNutritionSurvey);  

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
   		
   		LinkedHashMap<String, String> mRAPASurvey = ResultParser.getResults(xml);
   		qList = new ArrayList<String>();   		
   		//get answer list
		qList = TapestryHelper.getQuestionList(mRAPASurvey);  		

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
		
		LinkedHashMap<String, String> mMobilitySurvey = ResultParser.getResults(xml);
   		Map<String, String> qMap = TapestryHelper.getQuestionMap(mMobilitySurvey);  
   		   		
   		lAlert = AlertManager.getMobilityAlerts(qMap, lAlert);    		
   		
   		//summary tools for Mobility
   		scores = CalculationManager.getMobilityScore(qMap, scores);
   		
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
		//===================
		try{
   			xml = new String(goals.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mGoals = ResultParser.getResults(xml);
   		questionTextList = new ArrayList<String>();
   		questionTextList = ResultParser.getSurveyQuestions(xml);
   	
   		//get answer list
		qList = TapestryHelper.getQuestionList(mGoals);   	
							
		if ((qList != null) && (qList.size()>0))
		{					
			report.setPatientGoals(CalculationManager.getPatientGoals(qList));
			report.setLifeGoals(CalculationManager.getLifeOrHealthGoals(qList, 1));
			report.setHealthGoals(CalculationManager.getLifeOrHealthGoals(qList, 2));
		}
		
		//get volunteer information
		String volunteer = appointment.getVolunteer();
		String partner = appointment.getPartner();
		String comments = appointment.getComments();
				
		Map<String, String> vMap = new TreeMap<String, String>();
		
		if (!Utils.isNullOrEmpty(volunteer))
			vMap.put(" Volunteer 1", volunteer);
		else
			vMap.put(" Volunteer 1", "");
		
		if (!Utils.isNullOrEmpty(partner))
			vMap.put(" Volunteer 2", partner);
		else
			vMap.put(" Volunteer2", "");
		
		if (!Utils.isNullOrEmpty(comments))
			vMap.put(" Volunteer Notes", comments);					
		else
			vMap.put(" Volunteer Notes", " ");
		
		report.setVolunteerInformations(vMap);
		
		TapestryHelper.buildMcMasterPDFReport(report, response);
//			TapestryHelper.buildPDF(report, response);
		
		//add log
		User loggedInUser = TapestryHelper.getLoggedInUser(request, userManager);
		sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" download/view ");
		sb.append(patient.getFirstName());
		sb.append(" ");
		sb.append(patient.getLastName());
		sb.append(" report at");		
		java.util.Date date= new java.util.Date();		
		sb.append(new Timestamp(date.getTime()));
		userManager.addUserLog(sb.toString(), loggedInUser);	
	
		return null;
	}
	
	//====================== Survey ===================================//	
	@RequestMapping(value="/manage_survey", method=RequestMethod.GET)
	public String manageSurvey(@RequestParam(value="failed", required=false) String failed, Boolean deleteFailed, 
			ModelMap model, SecurityContextHolderAwareRequestWrapper request){
		HttpSession session = request.getSession();
		List<SurveyTemplate>  surveyTemplateList = TapestryHelper.getSurveyTemplatesWithCanDelete(request, surveyManager);	
				
		model.addAttribute("survey_templates", surveyTemplateList);
		
		if (deleteFailed != null)
			model.addAttribute("failed", deleteFailed);
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		if (session.getAttribute("surveyTemplateMessage") != null)
		{
			String message = session.getAttribute("surveyTemplateMessage").toString();
			
			if ("C".equals(message)){
				model.addAttribute("surveyTemplateCreated", true);
				session.removeAttribute("surveyTemplateMessage");
			}	
			else if ("U".equals(message))
			{
				model.addAttribute("surveyTemplateUpdated", true);
				session.removeAttribute("surveyTemplateMessage");
			}
			else if ("D".equals(message))
			{
				model.addAttribute("surveyTemplateDeleted", true);
				session.removeAttribute("surveyTemplateMessage");
			}			
		}
		
		//Get Site
		User user = TapestryHelper.getLoggedInUser(request);
		
		if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole()))//for central admin
		{
			List<Site> sites = TapestryHelper.getSites(request, organizationManager);		
			model.addAttribute("sites", sites);
		}
		return "admin/manage_survey";
	}	

	@RequestMapping(value="/search_survey", method=RequestMethod.POST)
	public String searchSurvey(@RequestParam(value="failed", required=false) Boolean failed, ModelMap model, 
			SecurityContextHolderAwareRequestWrapper request)
	{		
		String title = request.getParameter("searchTitle");		
		List<SurveyTemplate>  surveyTemplateList = surveyManager.getSurveyTemplatesByPartialTitle(title);
		
		model.addAttribute("survey_templates", surveyTemplateList);
	
		if(failed != null) {
			model.addAttribute("failed", true);
		}		 
		
		model.addAttribute("searchTitle", title);
		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "admin/manage_survey";
	}
 
	
   	@RequestMapping(value="/manage_surveys", method=RequestMethod.GET)
	public String manageSurveys(@RequestParam(value="failed", required=false) String failed, ModelMap model, 
			SecurityContextHolderAwareRequestWrapper request)
   	{
   		User loggedInUser = TapestryHelper.getLoggedInUser(request);
   		
   		List<Patient> patientList = new ArrayList<Patient>();
   		List<SurveyResult> surveyResultList = new ArrayList<SurveyResult>();
   		List<SurveyTemplate> surveyTemplateList = new ArrayList<SurveyTemplate>(); 
//   		if (request.isUserInRole("ROLE_ADMIN")||request.isUserInRole("ROLE_CLINICIAN"))//central admin and clinician
//   		{
//   			patientList = patientManager.getAllPatients();
//   			surveyResultList = surveyManager.getAllSurveyResults();
//   			surveyTemplateList = surveyManager.getAllSurveyTemplates();
//   		}
//   		else //local admin/site admin
//   		{
//   			int siteId = loggedInUser.getSite();
//   			patientList = patientManager.getPatientsBySite(siteId);
//   			surveyResultList = surveyManager.getAllSurveyResultsBySite(siteId);	
//   			surveyTemplateList = surveyManager.getSurveyTemplatesBySite(siteId);
//   		}   
   		if (request.isUserInRole("ROLE_CLINICIAN"))
   		{
   			int siteId = loggedInUser.getSite();
			patientList = patientManager.getPatientsBySite(siteId);
			surveyResultList = surveyManager.getAllSurveyResultsBySite(siteId);	
			surveyTemplateList = surveyManager.getSurveyTemplatesBySite(siteId);
   		}
		model.addAttribute("surveys", surveyResultList);		
		model.addAttribute("survey_templates", surveyTemplateList);
		model.addAttribute("patients", patientList);
		DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResultList, surveyTemplateList);	    
        
        if(failed != null) {
        	model.addAttribute("failed", true);
        }
        HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
        
		return "admin/manage_surveys";
	}
   	
   	@RequestMapping(value="/go_assign_survey/{patientId}", method=RequestMethod.GET)
	public String goAssignSurvey(@PathVariable("patientId") int id, SecurityContextHolderAwareRequestWrapper request, 
			ModelMap model)
   	{    		
   		User loggedInUser = TapestryHelper.getLoggedInUser(request);
   		List<SurveyTemplate> surveyTemplates = new ArrayList<SurveyTemplate>();
   		String role = loggedInUser.getRole();
   		int siteId;
   		
   		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		if (id == 0)//manage survey
		{
			if(role.equals("ROLE_ADMIN"))//central admin
			{
				session.removeAttribute("selectedSite");
				List<Site> sites = organizationManager.getAllSites();
	   			
	   			model.addAttribute("sites", organizationManager.getAllSites());
	   			model.addAttribute("showSites", true);
			}
			else //local admin
			{
				siteId = loggedInUser.getSite();
				surveyTemplates = surveyManager.getSurveyTemplatesBySite(siteId);
				List<Patient> patients = patientManager.getPatientsBySite(siteId);		
				
				model.addAttribute("patients", patients);
   	   			model.addAttribute("surveyTemplates", surveyTemplates);
			}
		}
		else //display client(Assign survey)
		{
			if(role.equals("ROLE_ADMIN"))//central admin
				siteId = patientManager.getSiteByPatientId(id);
			else
				siteId = loggedInUser.getSite();
				   			
			surveyTemplates = surveyManager.getSurveyTemplatesBySite(siteId);
			
			model.addAttribute("patient", id);   			
			model.addAttribute("surveyTemplates", surveyTemplates);
			model.addAttribute("hideClients", true);
		}
		return "/admin/assign_survey";
	} 
   	
   	@RequestMapping(value="/assign_selectedSurvey", method=RequestMethod.POST)
	public String assignSurvey(SecurityContextHolderAwareRequestWrapper request, ModelMap model) 
			throws JAXBException, DatatypeConfigurationException, Exception
	{ 
   		List<SurveyTemplate> sTemplates;
		List<Patient> patients;
		int siteId;
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		String role = loggedInUser.getRole();
		HttpSession session = request.getSession();
		String hPatient = request.getParameter("patient");   	
		String action = request.getParameter("hActionAssignSurvey");
		
		if ((request.getParameterValues("sites") != null)&&(Utils.isNullOrEmpty(action)))//login as central admin, select site from sites dropdown
		{
			String[] sites = request.getParameterValues("sites"); 
   			siteId = Integer.valueOf(sites[0]);
   			session.setAttribute("selectedSite", siteId);
   			
   			sTemplates = surveyManager.getSurveyTemplatesBySite(siteId);   			
   	   		patients = patientManager.getPatientsBySite(siteId);
   	   		
	   	   	model.addAttribute("surveyTemplates", sTemplates);
	   		model.addAttribute("patients", patients);
	   		model.addAttribute("showSites", true);
	   		model.addAttribute("selectedSite", siteId);	   		
	   		
			if (session.getAttribute("unread_messages") != null)
				model.addAttribute("unread", session.getAttribute("unread_messages"));						
			
			return "admin/assign_survey";
		}
		else // assign survey by central/local admin
		{
			siteId = loggedInUser.getSite();	
			
			if (siteId == 0)//logged in user is central admin
			{
				if (session.getAttribute("selectedSite") != null) //central admin(manage survey==> assign survey)
					siteId = Integer.valueOf(session.getAttribute("selectedSite").toString());
				else
					siteId = patientManager.getSiteByPatientId(Integer.valueOf(hPatient));
			}
			sTemplates = surveyManager.getSurveyTemplatesBySite(siteId);
			ArrayList<SurveyTemplate> selectSurveyTemplats = new ArrayList<SurveyTemplate>();	   		
	   		String[] surveyTemplateIds = request.getParameterValues("surveyTemplates"); 
	   		int[] patientIds;
	   		
		   	//add logs	
			StringBuffer sb = new StringBuffer();
			sb.append(loggedInUser.getName());
			sb.append(" has assigned surveys to patients");
			String logDes = sb.toString();
			
	   		if (!Utils.isNullOrEmpty(hPatient))
	   		{      		
	   			if(request.getParameter("assignSurvey") != null)//assign selected surveys to selected patients
	   	   		{  
	   				if (surveyTemplateIds != null && surveyTemplateIds.length > 0)
	   				{
	   					TapestryHelper.addSurveyTemplate(surveyTemplateIds,sTemplates, selectSurveyTemplats);     					
	   	   	   			patientIds = new int[] {Integer.valueOf(hPatient)};
	   	   	   			
	   	   	   			TapestryHelper.assignSurveysToClient(selectSurveyTemplats, patientIds, request, model, surveyManager);   	
	   	   	   			userManager.addUserLog(logDes, loggedInUser);
	   	   	   		}
	   	   	   		else//no survey template has been selected
	   	   	   		{
	   	   	   			model.addAttribute("no_survey_selected", true);   	
	   	   	   			return "admin/assign_survey";
	   	   	   		}
	   				return "redirect:/display_client/" + hPatient;  
	   	   		}
	   		}
	   		else//user select SurveyManagement/Assign Survey
	   		{ 	
	   			patients = patientManager.getPatientsBySite(siteId);	   			

	   			if ("Search".equals(action))
		   		{
		   			String name = request.getParameter("searchPatientName");   
		   			patients = TapestryHelper.getPatientsByPartialName(patients, name);
		   			
		   			model.addAttribute("searchPatientName", name);	 
		   			model.addAttribute("showSites", true);
			   		model.addAttribute("selectedSite", siteId);	   		
		   			
		   		}
		   		else if(request.getParameter("assignSurvey") != null)//assign selected surveys to selected patients
		   		{    	   		
		   	   		String[] selectedPatientIds = request.getParameterValues("patientId");
		   	   		String assignToAll = request.getParameter("assignAllClinets");	   	   		   	   		
		   	   		
		   	   		//get survey template list 
		   	   		if (surveyTemplateIds != null && surveyTemplateIds.length > 0)
		   	   		{
		   	   			TapestryHelper.addSurveyTemplate(surveyTemplateIds,sTemplates, selectSurveyTemplats);  
		   	   			
		   	   			if ("true".equalsIgnoreCase(assignToAll))
			   	   		{//for assign to all clients   			
			   	   			Patient patient;   			
			   	   			patientIds = new int[patients.size()];
			   	   			
			   	   			for(int i = 0; i < patients.size(); i++){
			   	   				patient = new Patient();
			   	   				patient = patients.get(i);
			   	   				patientIds[i] = patient.getPatientID();
			   	   			}		   	   			
			   	   			TapestryHelper.assignSurveysToClient(selectSurveyTemplats, patientIds, request, model, surveyManager);		
			   	   			userManager.addUserLog(logDes, loggedInUser);
			   	   		}
			   	   		else
			   	   		{//for selected patients, convert String[] to int[]   			
			   	   			if (selectedPatientIds == null || selectedPatientIds.length == 0)
			   	   				model.addAttribute("no_patient_selected", true);
			   	   			else
			   	   			{
			   	   				int[] iSelectedPatientIds = new int[selectedPatientIds.length];
			   	   	   			for (int j = 0; j < selectedPatientIds.length; j++){
			   	   	   				iSelectedPatientIds[j] = Integer.parseInt(selectedPatientIds[j]);
			   	   				}
			   	   	   			TapestryHelper.assignSurveysToClient(selectSurveyTemplats, iSelectedPatientIds, request, model, surveyManager);
			   	   	   			userManager.addUserLog(logDes, loggedInUser);
			   	   			}   			
			   	   		} 
		   	   		}
		   	   		else//no survey template has been selected
		   	   			model.addAttribute("no_survey_selected", true);
		   		}
		   		model.addAttribute("surveyTemplates", sTemplates);
		   		model.addAttribute("patients", patients);
	   		}
	   		return "admin/assign_survey";
	   	}
	}   	
	
 	@RequestMapping(value = "/upload_survey_template", method=RequestMethod.POST)
	public String addSurveyTemplate(HttpServletRequest request) throws Exception
	{
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute("loggedInUser");
		
   		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
   		MultipartFile multipartFile = multipartRequest.getFile("file");
   		
		//Add a new survey template
		SurveyTemplate st = new SurveyTemplate();
		st.setTitle(request.getParameter("title"));
		st.setType(request.getParameter("type"));
		st.setDescription(request.getParameter("desc"));
		int p = Integer.parseInt(request.getParameter("priority"));
		st.setPriority(p);
		st.setContents(multipartFile.getBytes());
		if (request.isUserInRole("ROLE_ADMIN"))
			st.setSite(Integer.parseInt(request.getParameter("site")));
		else
			st.setSite(loggedInUser.getSite());
		
		if ("1".equals(request.getParameter("default_survey")))
			st.setDefaultSurvey(true);
		else
			st.setDefaultSurvey(false);
		surveyManager.uploadSurveyTemplate(st);
		
		session.setAttribute("surveyTemplateMessage", "C");
		//update survey template in the session
		session.removeAttribute("survey_template_list");
		session.setAttribute("survey_template_list", TapestryHelper.getSurveyTemplates(request, surveyManager));
		
		session.removeAttribute("survey_template_withCanDelete_list");
		session.setAttribute("survey_template_withCanDelete_list", TapestryHelper.getSurveyTemplatesWithCanDelete(request, surveyManager));
				
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has uploaded survey template ");
		sb.append(request.getParameter("title"));
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		return "redirect:/manage_survey";
	}
 	
   	@RequestMapping(value="/delete_survey_template/{surveyID}", method=RequestMethod.GET)
   	public String deleteSurveyTemplate(@PathVariable("surveyID") int id, ModelMap model, 
   			SecurityContextHolderAwareRequestWrapper request)
   	{ 	
   		SurveyTemplate st = surveyManager.getSurveyTemplateByID(id);
		surveyManager.deleteSurveyTemplate(id);
		
		HttpSession session = request.getSession();
		//update survey template in the session  
		session.removeAttribute("survey_template_list");		
		session.setAttribute("survey_template_list", TapestryHelper.getSurveyTemplates(request, surveyManager));
		
		session.removeAttribute("survey_template_withCanDelete_list");		
		session.setAttribute("survey_template_withCanDelete_list", TapestryHelper.getSurveyTemplatesWithCanDelete(request, surveyManager));
		session.setAttribute("surveyTemplateMessage", "D");
		
		//archive the record
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		surveyManager.archiveSurveyTemplate(st, loggedInUser.getName());
		//user logs   			
		StringBuffer sb  = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" deleted survey template # ");
		sb.append(id);   			   		
		userManager.addUserLog(sb.toString(), loggedInUser);
			
		return "redirect:/manage_survey";
   	}
	
	@RequestMapping(value="open_survey/{resultID}", method=RequestMethod.GET)
	public String openSurvey(@PathVariable("resultID") int id, HttpServletRequest request) 
	{		//move logging down to /show_survey/{resultID}
		return "redirect:/show_survey/" + id;
	}
	
   	@RequestMapping(value="/show_survey/{resultID}", method=RequestMethod.GET)
   	public ModelAndView showSurvey(@PathVariable("resultID") int id, HttpServletRequest request)
   	{    		
   		ModelAndView redirectAction = null;
   		List<SurveyResult> surveyResults;
		List<SurveyTemplate> surveyTemplates;
					
		HttpSession session = request.getSession();
		User u = (User)session.getAttribute("loggedInUser");
		String name = u.getName();		
		int siteId = u.getSite();		
		int pIdInSession;
		
		TapestrySurveyMap userSurveys = TapestryHelper.getSurveyMap(request);		
		SurveyResult surveyResult = surveyManager.getSurveyResultByID(id);
		int patientId = surveyResult.getPatientID();
		
		if (session.getAttribute("selectedPatient") != null)
		{// to view survey result for other patient when login user is admin
			pIdInSession = Integer.valueOf(session.getAttribute("selectedPatient").toString());
					
			if (patientId != pIdInSession)
			{
				session.setAttribute("selectedPatient", patientId);
				userSurveys = null;					
			}	
		}
		else if (!request.isUserInRole("ROLE_USER"))
			session.setAttribute("selectedPatient", patientId);
		
		if (userSurveys == null)
		{
			if (request.isUserInRole("ROLE_ADMIN"))//central admin 
				surveyTemplates = surveyManager.getAllSurveyTemplates();
	   		else //local admin/site admin
	   			surveyTemplates = surveyManager.getSurveyTemplatesBySite(siteId);
			
			surveyResults = surveyManager.getSurveysByPatientID(patientId);
			userSurveys = TapestryHelper.storeSurveyMapInSession(request, surveyResults, surveyTemplates);			
		}			
		
		SurveyTemplate surveyTemplate = surveyManager.getSurveyTemplateByID(surveyResult.getSurveyID());
	
		//user logs
		Patient p = patientManager.getPatientByID(patientId);
		StringBuffer sb  = new StringBuffer();
		sb.append(name);
		sb.append(" opened survey ");
		sb.append(surveyResult.getSurveyTitle());
		sb.append(" for patient ");
		if(p.getPreferredName() != null && p.getPreferredName() != "")
			sb.append(p.getPreferredName());
		else 
			sb.append(p.getDisplayName());
			
		userManager.addUserLog(sb.toString(), u);		
				
		//all survey results stored in map	
		TapestryPHRSurvey currentSurvey = userSurveys.getSurvey(Integer.toString(id));
		
		try {
			SurveyFactory surveyFactory = new SurveyFactory();
			PHRSurvey templateSurvey = surveyFactory.getSurveyTemplate(surveyTemplate);	
			
			redirectAction = DoSurveyAction.execute(request, Integer.toString(id), currentSurvey, templateSurvey);
		} catch (Exception e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
		}
		
		if (redirectAction == null){ //Assuming we've completed the survey
			System.out.println("Something bad happened");
		}
		
			
   		if (request.isUserInRole("ROLE_USER") && redirectAction.getViewName() == "failed")
   			redirectAction.setViewName("redirect:/");	
   		else if (request.isUserInRole("ROLE_ADMIN") && redirectAction.getViewName() == "failed")    
   			redirectAction.setViewName("redirect:/display_client/" + patientId);
  
   		return redirectAction;
   	}
   	  
   	@RequestMapping(value="/save_survey/{resultID}", method=RequestMethod.GET)
   	public String saveAndExit(@PathVariable("resultID") int id, HttpServletRequest request) throws Exception
	{
   		boolean isComplete = Boolean.parseBoolean(request.getParameter("survey_completed"));
   		List<SurveyResult> surveyResults ;
		List<SurveyTemplate> surveyTemplates;
		
		//For activity logging purposes
		HttpSession session = request.getSession();
		User currentUser = (User)session.getAttribute("loggedInUser");	
		int siteId = currentUser.getSite();
		if (currentUser.getRole().equals("ROLE_ADMIN"))//central admin 
			surveyTemplates = surveyManager.getAllSurveyTemplates();
   		else //local admin/site admin
   			surveyTemplates = surveyManager.getSurveyTemplatesBySite(siteId);
		SurveyResult surveyResult = surveyManager.getSurveyResultByID(id);
		surveyResults = surveyManager.getSurveysByPatientID(surveyResult.getPatientID());
		TapestrySurveyMap surveys = DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResults, surveyTemplates);
		PHRSurvey currentSurvey = surveys.getSurvey(Integer.toString(id));		
		
		Patient currentPatient = patientManager.getPatientByID(surveyResult.getPatientID());	
		
		StringBuffer sb;
		if (isComplete) {
			byte[] data = null;
				try {
				data = SurveyAction.updateSurveyResult(currentSurvey);
				currentSurvey.setComplete(true);
			} catch (Exception e) {
				System.out.println("Failed to convert PHRSurvey into a byte array");
				e.printStackTrace();
			}
			surveyManager.updateSurveyResults(id, data);
			surveyManager.markAsComplete(id);
			
			//user logs
			sb  = new StringBuffer();
			sb.append(currentUser.getName());
			sb.append(" completed survey ");
			sb.append(surveyResult.getSurveyTitle());
			sb.append(" for patient ");
			
			if(currentPatient.getPreferredName() != null && currentPatient.getPreferredName() != "") 
				sb.append( currentPatient.getPreferredName());
			else
				sb.append( currentPatient.getDisplayName());
			
			userManager.addUserLog(sb.toString(), currentUser);
		}
		
		if (!currentSurvey.isComplete())
		{
			byte[] data = SurveyAction.updateSurveyResult(currentSurvey);
			surveyManager.updateSurveyResults(id, data);
			
			//user logs
			sb  = new StringBuffer();
			sb.append(currentUser.getName());
			sb.append(" saved incomplete survey ");
			sb.append(surveyResult.getSurveyTitle());
			sb.append(" for patient ");
			
			if(currentPatient.getPreferredName() != null && currentPatient.getPreferredName() != "") 
				sb.append( currentPatient.getPreferredName());
			else 
				sb.append( currentPatient.getDisplayName());
		
			userManager.addUserLog(sb.toString(), currentUser);
		}
				
		if (request.isUserInRole("ROLE_CLIENT"))
			return "redirect:/";
		
		if (!request.isUserInRole("ROLE_USER")){
   			return "redirect:/display_client/"+surveyResult.getPatientID();
   		}		
		else
		{
   			Appointment appointment = appointmentManager.getAppointmentByMostRecentIncomplete(currentPatient.getPatientID());
   			
   			if (isComplete) {
   				return "redirect:/patient/" + currentPatient.getPatientID() + "?complete=" + surveyResult.getSurveyTitle() 
   						+ "&appointmentId=" + appointment.getAppointmentID();
   			} else {
   				return "redirect:/patient/" + currentPatient.getPatientID() + "?aborted=" + surveyResult.getSurveyTitle() 
   						+ "&appointmentId=" + appointment.getAppointmentID();
   			}
   		}
	}
  
   	@RequestMapping(value="/delete_survey/{resultID}", method=RequestMethod.GET)
   	public String deleteSurvey(@PathVariable("resultID") int id, HttpServletRequest request)
   	{
   		SurveyResult sr = surveyManager.getSurveyResultByID(id);   		
   		surveyManager.deleteSurvey(id);
   		
   		List<SurveyResult> surveyResults;
   		List<SurveyTemplate> surveyTemplates;
   		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute("loggedInUser");	
		int siteId = loggedInUser.getSite();
		int patientId = sr.getPatientID();
		if (loggedInUser.getRole().equals("ROLE_ADMIN"))//central admin 
			surveyTemplates = surveyManager.getAllSurveyTemplates();
   		else //local admin/site admin
   			surveyTemplates = surveyManager.getSurveyTemplatesBySite(siteId);
		surveyResults = surveyManager.getSurveysByPatientID(patientId);
   		DoSurveyAction.updateSurveyMapInSession(request, surveyResults, surveyTemplates);
   		
   		//archive the deleted survey result
   		Patient patient = patientManager.getPatientByID(patientId);
   		StringBuffer sb = new StringBuffer();
   		sb.append(patient.getFirstName());
   		sb.append(" ");
   		sb.append(patient.getLastName());   		
   		surveyManager.archiveSurveyResult(sr, sb.toString(), loggedInUser.getName());
   		//add logs
		sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has deleted survey result # ");
		sb.append(id);
		userManager.addUserLog(sb.toString(), loggedInUser);
   		
   		return "redirect:/display_client/" + sr.getPatientID();
   	}
   	
   	@RequestMapping(value="/complete_survey_results/{resultID}", method=RequestMethod.GET)
   	public String completeSurveyResults(@PathVariable("resultID") int id, 
   			@RequestParam(value="patientId", required=true) int patientId, HttpServletRequest request, ModelMap model)
   	{
   		surveyManager.markAsComplete(id);
   	
   		User loggedInUser = TapestryHelper.getLoggedInUser(request);
   		StringBuffer sb = new StringBuffer();
   		sb.append(loggedInUser.getName());
   		sb.append(" has completed survey result # ");
   		sb.append(id);
   		userManager.addUserLog(sb.toString(), loggedInUser);
			
   		return "redirect:/display_client/" + patientId;
   	}
   	
/**
   	@RequestMapping(value="/complete_survey_results/{patientId}", method=RequestMethod.POST)
   	public String compSurveyResults(@PathVariable("patientId") int id, 	HttpServletRequest request, ModelMap model)
   	{
		if (request.getParameter("hSurveyResultId") != null)
		{
			int surveyResultId = Integer.valueOf(request.getParameter("hSurveyResultId"));
			surveyManager.markAsComplete(surveyResultId);
			
			String adminNote = request.getParameter("noteBody");
	   		   	
	   		User loggedInUser = TapestryHelper.getLoggedInUser(request);
	   		StringBuffer sb = new StringBuffer();
	   		sb.append(loggedInUser.getName());
	   		sb.append(" has completed survey result # ");
	   		sb.append(surveyResultId);
	   		userManager.addUserLog(sb.toString(), loggedInUser);
		}
			
   		return "redirect:/display_client/" + id;
   	}*/
   	
   	
   	@RequestMapping(value="/view_survey_results/{resultID}", method=RequestMethod.GET)
   	public String viewSurveyResults(@PathVariable("resultID") int id, HttpServletRequest request, ModelMap model)
   	{
   		User loggedInUser = TapestryHelper.getLoggedInUser(request);
   		SurveyResult r = surveyManager.getSurveyResultByID(id); 		
   		
   		String xml;
   		try{
   			xml = new String(r.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> res = ResultParser.getResults(xml);
   		List<DisplayedSurveyResult> displayedResults = ResultParser.getDisplayedSurveyResults(res);
   		
   		int site = surveyManager.getSiteBySurveyResultID(id);   		
   		//Substitute numbers with actual text answers   	
   		displayedResults = TapestryHelper.getDetailedAnswerForSurveys(displayedResults, site);

   		model.addAttribute("results", displayedResults);
   		model.addAttribute("id", id);
   		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		//add logs   		
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has viewed survey # ");
		sb.append(id);
		userManager.addUserLog(sb.toString(), loggedInUser);
   		
   		return "/admin/view_survey_results";
   	}
   	
   	@RequestMapping(value="/modify_surveyTemplate/{surveyID}", method=RequestMethod.GET)
   	public String modifySurveyTemplate(@PathVariable("surveyID") int id, HttpServletRequest request, ModelMap model)
	{   		
	//	SurveyTemplate st = surveyManager.getSurveyTemplateByID(id);
		List<SurveyTemplate> surveyTemplates = TapestryHelper.getSurveyTemplates(request, surveyManager);
				
		for (SurveyTemplate st: surveyTemplates)
		{
			if (id == st.getSurveyID())
			{
				model.addAttribute("surveyTemplate", st);
				break;
			}
		}		
		return "/admin/edit_survey_template";
	}
   	
	@RequestMapping(value="/edit_surveyTemplate/{surveyID}", method=RequestMethod.POST)
   	public String editSurveyTemplate(@PathVariable("surveyID") int id, HttpServletRequest request)
	{		
		List<SurveyTemplate> surveyTemplates;
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute("loggedInUser");		
						
		SurveyTemplate st = new SurveyTemplate();
		st.setSurveyID(id);
		st.setTitle(request.getParameter("title"));		
		st.setDescription(request.getParameter("description"));				

		surveyManager.updateSurveyTemplate(st);
		if (request.isUserInRole("ROLE_ADMIN"))//central admin 
		{
			surveyTemplates = surveyManager.getSurveyTemplatesWithCanDelete(0);
			session.setAttribute("survey_template_withCanDelete_list", surveyTemplates);
			
			surveyTemplates = surveyManager.getAllSurveyTemplates();
			session.setAttribute("survey_template_list", surveyTemplates);
		}
		else //local admin/site admin
		{
			int site = loggedInUser.getSite();
			surveyTemplates = surveyManager.getSurveyTemplatesWithCanDelete(site);
			session.setAttribute("survey_template_withCanDelete_list", surveyTemplates);
			
			surveyTemplates = surveyManager.getSurveyTemplatesBySite(site);
			session.setAttribute("survey_template_list", surveyTemplates);
		}
		
		session.setAttribute("surveyTemplateMessage", "U");
		
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has modified survey template # ");
		sb.append(id);
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		return "redirect:/manage_survey";			
	}
	
   	@RequestMapping(value="/export_csv/{resultID}", method=RequestMethod.GET)
   	@ResponseBody
   	public String downloadCSV(@PathVariable("resultID") int id, HttpServletRequest request, HttpServletResponse response)
   	{
   		SurveyResult r = surveyManager.getSurveyResultByID(id);
   		
   		String xml;
   		try{
   			xml = new String(r.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		String res = ResultParser.resultsAsCSV(ResultParser.getResults(xml));   
   		
   		response.setContentType("text/csv");   
   		response.setContentLength(res.length());
   		response.setHeader("Content-Disposition", "attachment; filename=\"result.csv\"");
 
   		try{
   			PrintWriter pw = new PrintWriter(response.getOutputStream());
   			pw.write(res);
   		} catch (Exception e) {
   			e.printStackTrace();
   		}   		
   		
   		//add logs
   		HttpSession session = request.getSession();
   		User loggedInUser = (User)session.getAttribute("loggedInUser");
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has downloaded survey # ");
		sb.append(id);
		userManager.addUserLog(sb.toString(), loggedInUser);
   		
   		return res;
   	}
   	
   	@RequestMapping(value="/download_survey_template/{surveyID}", method=RequestMethod.GET)
   	@ResponseBody
   	public String downloadSurveyTemplate(@PathVariable("surveyID") int id, HttpServletResponse response)
   	{   		   		
   		SurveyTemplate sTemplate = surveyManager.getSurveyTemplateByID(id);
   		String fileName = sTemplate.getTitle();
   		byte[] bContent = sTemplate.getContents();
   		response.setContentType("text");
   		response.setHeader("Content-Disposition", "attachment; filename=\""+ fileName + ".text\"");
   	
   		try{
   			ServletOutputStream output = response.getOutputStream();
   			output.write(bContent);   			
   		} catch (Exception e) {
   			e.printStackTrace();
   		}   		   		
   		return "admin/manage_survey";
   	}   
   	   
	@RequestMapping(value="/view_research_data", method=RequestMethod.GET)
	public String getResearchDataDownload( SecurityContextHolderAwareRequestWrapper request, ModelMap model)
   	{ 		
		List<Site> sites;
		User user = TapestryHelper.getLoggedInUser(request);
		if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole()))// for central admin
			sites = organizationManager.getAllSites();
		else
		{
			sites = new ArrayList<Site>();
			sites.add(organizationManager.getSiteById(user.getSite()));
		}						
		model.addAttribute("sites", sites);

		return "admin/manage_research_data";
   	}
	
	@RequestMapping(value="/download_researchData/{siteID}", method=RequestMethod.GET)
	public String downLoadResearchDataBySite(@PathVariable("siteID") int id, HttpServletRequest request,  HttpServletResponse response)
	{//This data needs to be written (Object[])
        List<ResearchData> results = TapestryHelper.getResearchDatas(patientManager, surveyManager, id);   
        //Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();         
        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Research Data");   
              
        Map<Integer, Object[]> data = new TreeMap<Integer, Object[]>();
        data.put(1, new Object[] {"Research_ID","QOL1_mobility_T0", "QOL2_selfcare_T0", "QOL3_usualact_T0", 
        		"QOL4_paindis_T0", "QOL5_anxdepr_T0", "QOL6_scale_T0", "DSS1_role_T0",	"DSS2_under_T0","DSS3_useful_T0",
        		"DSS4_listen_T0","DSS5_happen_T0","DSS6_talk_T0", "DSS7_satisfied_T0", "DSS8_nofam_T0","DSS9_timesnotliving_T0",
        		"DSS10_timesphone_T0","DSS11_timesclubs_T0", "DSS_notes_T0", "Goals1matter_T0", "Goals2life_T0", "Goals3health_T0", 
        		"Goals4list_T0", "Goals5firstspecific_T0", "Goals6firstbaseline_T0", "Goals7firsttarget_T0", "Goals5secondspecific_T0", 
        		"Goals6secondbaseline_T0", "Goals7secondtarget_T0", "Goals5thirdspecific_T0", "Goals6thirdbaseline_T0", "Goals7thirdtarget_T0",
        		"Goals8priority_T0", "Goalsdiscussion_notes_T0", "DLA1_typday_T0","DLA2_goto_T0","DLA3_gotowhy_T0","DLA4_goodday_T0",
        		"DLA5_managingconcerns_T0","DLA6_diffactivPressneed_T0","DLA7_fallinyear_T0","DLA7a_fallwhy_T0", "RAPA1_neverpa_T0", 
        		"RAPA2_moderpa_T0", "RAPA3_lightpa_T0", "RAPA4_moderpaless30_T0", "RAPA5_vigpaless20_T0", "RAPA6_moderpamore30_T0", 
        		"RAPA7_vigopamore20_T0", "RAPA8_lift_T0","RAPA9_stretch_T0", "AD1_written_T0", "AD2_doctor_T0", "AD3_interest_T0",
        		"Mem1_worst_T0", "Mem1a_worseexplain_T0", "Mem2_worry_T0", "Mem2a_worrywhy_T0", "GH1_clockcomplt_T0","GH2_hosptadmins_T0", 
        		"GH3_genhealth_T0", "GH4_helpactivs_T0", "GH5_relihelp_T0", "GH6_prescrip_T0", "GH7_forgetprescrip_T0", "GH8_lostwr_T0",
        		"GH9_depr_T0", "GH10_bladdercontr_T0", "GH11_walktime_T0", "Mob1_diffwalking_T0", "Mob21_modwalking_T0","Mob22_modwalking_T0",
        		"Mob23_modwalking_T0","Mob24_modwalking_T0","Mob25_modwalking_T0","Mob26_modwalking_T0","Mob3_diffwalkingp5_T0",
        		"Mob41_modwalkingp5_T0", "Mob42_modwalkingp5_T0", "Mob43_modwalkingp5_T0", "Mob44_modwalkingp5_T0", "Mob45_modwalkingp5_T0",
        		"Mob46_modwalkingp5_T0", "Mob5_diffstairs_T0", "Mob61_modstaris_T0", "Mob62_modstaris_T0", "Mob63_modstaris_T0", "Mob64_modstaris_T0",
        		"Mob65_modstaris_T0", "Mob66_modstaris_T0", "Nut1_wtchg_T0", "Nut2_intentwtchg_T0", "Nut3_rightwt_T0",
        		"Nut4_skipmeals_T0", "Nut5_avoidfoods_T0", "Nut6_apetit_T0", "Nut7_servfrtveg_T0", "Nut8_eggsmeat_T0", "Nut9_dairy_T0",
        		"Nut10_fluid_T0", "Nut11_painswallow_T0", "Nut12_diffchew_T0", "Nut13_mealreplace_T0","Nut14_mealcompany_T0","Nut15_mealaprep_T0",
        		"Nut16_mealprepstate_T0","Nut17_groceries_T0", "FU1_cliniccontact_T3", "FU2_cliniccontacthow_T3", "FU3_cliniccontactother_T3", 
        		"FU5_loggedin_T3", "FU6_PHRuse_T3", "FU7_PHRuseother_T3", "FU8_PHRwhynot_T3", "FU9_PHRwhynotother_T3", "FU11_goal1activ_T3", "FU12_goal1enablers_T3", 
        		"FU13_goal1barriers_T3", "FU14_goal1nextstep_T3", "FU15_goal2activ_T3", "FU16_goal2enablers_T3", "FU17_goal2barriers_T3", "FU18_goal2nextstep_T3", 
        		"FU19_goal3activ_T3", "FU20_goal3enablers_T3", "FU21_goal3barriers_T3", "FU22_goal3nextstep_T3"});
        ResearchData r;
        for (int i=0; i< results.size(); i++)
        {
        	r = results.get(i);        	
        	data.put(Integer.valueOf(i+2), new Object[]{r.getResearchId(),r.getQol1(), r.getQol2(),r.getQol3(), r.getQol4(),
        		r.getQol5(), r.getQol6(), r.getdSS1_role_TO(), r.getdSS2_under_TO(), 
        		r.getdSS3_useful_TO(), r.getdSS4_listen_TO(), r.getdSS5_happen_TO(), r.getdSS6_talk_TO(), 
        		r.getdSS7_satisfied_TO(), r.getdSS8_nofam_TO(), r.getdSS9_timesnotliving_TO(), r.getdSS10_timesphone_TO(), 
        		r.getdSS11_timesclubs_TO(), r.getdSS_notes_TO(), r.getGoals1Matter_TO(), r.getGoals2Life_TO(), 
        		r.getGoals3Health_TO(), r.getGoals4List_TO(), r.getGoals5FirstSpecific_TO(), r.getGoals6FirstBaseline_TO(), 
        		r.getGoals7FirstTaget_TO(), r.getGoals5SecondSpecific_TO(), r.getGoals6SecondBaseline_TO(), 
        		r.getGoals7SecondTaget_TO(), r.getGoals5ThirdSpecific_TO(), r.getGoals6ThirdBaseline_TO(), 
        		r.getGoals7ThirdTaget_TO(), r.getGoals8Priority_TO(), r.getGoalsDiscussion_notes_TO(), r.getDla1(), r.getDla2(), 
        		r.getDla3(), r.getDla4(), r.getDla5(), r.getDla6(), r.getDla7(), r.getDla7a(), r.getRapa1(), r.getRapa2(), r.getRapa3(),
        		r.getRapa4(), r.getRapa5(), r.getRapa6(), r.getRapa7(), r.getRapa8(), r.getRapa9(), r.getAd1(), r.getAd2(), r.getAd3(),
        		r.getMem1(), r.getMem2(), r.getMem3(), r.getMem4(), r.getGh1(), r.getGh2(), r.getGh3(), r.getGh4(), r.getGh5(),
        		r.getGh6(), r.getGh7(), r.getGh8(), r.getGh9(), r.getGh10(), r.getGh11(), r.getMob1(), r.getMob21(),r.getMob22(),
        		r.getMob23(),r.getMob24(),r.getMob25(),r.getMob26(),r.getMob3(),r.getMob41(), r.getMob42(), r.getMob43(), r.getMob44(), 
        		r.getMob45(), r.getMob46(), r.getMob5(), r.getMob61(), r.getMob62(), r.getMob63(), r.getMob64(), r.getMob65(), r.getMob66(), 
        		r.getNut1(), r.getNut2(), r.getNut3(), r.getNut4(), r.getNut5(), r.getNut6(), r.getNut7(), r.getNut8(), r.getNut9(),
        		r.getNut10(), r.getNut11(), r.getNut12(), r.getNut13(), r.getNut14(), r.getNut15(),	r.getNut16(), r.getNut17(), r.getFu1(), 
        		r.getFu2(), r.getFu3(), r.getFu5(), r.getFu6(), r.getFu7(), r.getFu8(), r.getFu9(), r.getFu11(), r.getFu12(), r.getFu13(), 
        		r.getFu14(), r.getFu15(), r.getFu16(), r.getFu17(), r.getFu18(), r.getFu19(), r.getFu20(), 
        		r.getFu21(), r.getFu22()});
        }          
        //Iterate over data and write to sheet
        Set<Integer> keyset = data.keySet();
        int rownum = 0;
        int cellnum;  
        Row row;
        Object [] objArr;
        for (Integer key : keyset)
        {
            row = sheet.createRow(rownum++);           
            objArr = data.get(key);            
            cellnum = 0;
            for (Object obj : objArr)
            {
               Cell cell = row.createCell(cellnum++);               
               if(obj instanceof String)
                    cell.setCellValue((String)obj);
                else if(obj instanceof Integer)
                    cell.setCellValue((Integer)obj);              
            }
        }   
        //Adjusts the each column width to fit the contents
        for (int c=1; c<=120; c++)
        	sheet.autoSizeColumn(c);
       
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"result.xlsx\"");
     
        try{// Write workbook to response.
            workbook.write(response.getOutputStream()); 
            response.getOutputStream().close();
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
        
        StringBuffer sb = new StringBuffer();
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		sb.append(loggedInUser.getName());
		sb.append("has Downloaded Research Data");		
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		return null;
	}

	
	//============================ Clinic ==================================
	
	@RequestMapping(value="/manage_clinics", method=RequestMethod.GET)
	public String getClinics( SecurityContextHolderAwareRequestWrapper request, ModelMap model)
   	{ 
		User user = TapestryHelper.getLoggedInUser(request);
		List<Clinic> clinics;
		if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole()))// for central admin
			clinics = organizationManager.getAllClinics();		
		else
			clinics = organizationManager.getClinicsBySite(user.getSite());
		
		model.addAttribute("clinics", clinics);
		return "/admin/manage_clinics";
   	}
	
	@RequestMapping(value="/new_clinic", method=RequestMethod.GET)
	public String addClinics( SecurityContextHolderAwareRequestWrapper request, ModelMap model)
   	{ 
		User user = TapestryHelper.getLoggedInUser(request);
		
		if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole()))//for central admin
		{
			List<Site> sites = TapestryHelper.getSites(request, organizationManager);		
			model.addAttribute("sites", sites);
		}
		return "admin/add_clinic";
	//	return "/admin/add_clinic";
   	}
	
	@RequestMapping(value="/add_clinic", method=RequestMethod.POST)
	public String newClinics( SecurityContextHolderAwareRequestWrapper request, ModelMap model)
   	{ 
		User user = TapestryHelper.getLoggedInUser(request);
		Clinic clinic =  new Clinic();
		clinic.setAddress(request.getParameter("address"));
		clinic.setClinicName(request.getParameter("name"));
		
		if (request.getParameter("phone") != null)
			clinic.setPhone(request.getParameter("phone"));
				
		if (request.getParameter("site") != null)
			clinic.setSiteId(Integer.parseInt(request.getParameter("site")));
		else
			clinic.setSiteId(user.getSite());
		
		organizationManager.addClinic(clinic);
		
		if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole()))//for central admin
			model.addAttribute("clinics", organizationManager.getAllClinics());
		else
			model.addAttribute("clinics", organizationManager.getClinicsBySite(user.getSite()));		
		model.addAttribute("clinicCreated", true);
		
		//add logs
   		HttpSession session = request.getSession();
   		User loggedInUser = (User)session.getAttribute("loggedInUser");
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has added a new clinic ");
		sb.append(request.getParameter("name"));
		
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		return "/admin/manage_clinics";
   	}
	
	@RequestMapping(value="/edit_clinic/{clinicId}", method=RequestMethod.GET)
	public String editClinics(@PathVariable("clinicId") int id, SecurityContextHolderAwareRequestWrapper request, ModelMap model)
   	{ 
		User user = TapestryHelper.getLoggedInUser(request);
		
		if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole()))//for central admin
		{
			List<Site> sites = TapestryHelper.getSites(request, organizationManager);		
			model.addAttribute("sites", sites);
		}		
		Clinic clinic = organizationManager.getClinicById(id);		
		model.addAttribute("clinic", clinic);
		return "/admin/edit_clinic";
   	}
	
	@RequestMapping(value="/modify_clinic/{clinicId}", method=RequestMethod.POST)
	public String modifyClinics(@PathVariable("clinicId") int id, SecurityContextHolderAwareRequestWrapper request, ModelMap model)
   	{ 
		Clinic clinic = organizationManager.getClinicById(id);
		
		if (request.getParameter("name") != null)
			clinic.setClinicName(request.getParameter("name"));
		
		if (request.getParameter("address") != null)
			clinic.setAddress(request.getParameter("address"));
		
		if (request.getParameter("phone") != null)
			clinic.setPhone(request.getParameter("phone"));
		
		if (request.getParameter("site") != null)
			clinic.setSiteId(Integer.parseInt(request.getParameter("site")));
		
		organizationManager.modifyClinic(clinic);
		
		User user = TapestryHelper.getLoggedInUser(request);
		if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole()))//for central admin
			model.addAttribute("clinics", organizationManager.getAllClinics());
		else
			model.addAttribute("clinics", organizationManager.getClinicsBySite(user.getSite()));
		model.addAttribute("clinicUpdated", true);
		
		//add logs
   		HttpSession session = request.getSession();
   		User loggedInUser = (User)session.getAttribute("loggedInUser");
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has modified the clinic ");
		sb.append(request.getParameter("name"));
		
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		return "/admin/manage_clinics";
   	}
	
	//============================== UBC =====================================
	@RequestMapping(value="/manage_volunteer_survey", method=RequestMethod.GET)
	public String manageVolunteerSurveyTemplates(@RequestParam(value="failed", required=false) Boolean failed, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{			
		List<SurveyTemplate> surveyTemplateList;
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		if (loggedInUser.getRole().equals("ROLE_ADMIN"))
			surveyTemplateList = surveyManager.getAllVolunteerSurveyTemplates();		
		else
			surveyTemplateList = surveyManager.getVolunteerSurveyTemplatesBySite(loggedInUser.getSite());
		
		model.addAttribute("volunteer_survey_templates", surveyTemplateList);
		
		if ("ROLE_ADMIN".equalsIgnoreCase(loggedInUser.getRole()))//for central admin
		{
			List<Site> sites = TapestryHelper.getSites(request, organizationManager);		
			model.addAttribute("sites", sites);
		}
		
		if (failed != null)
			model.addAttribute("failed", failed);	
			
		HttpSession session = request.getSession();		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));		
		if (session.getAttribute("noDelete") != null)
		{
			model.addAttribute("noDelete", true); 
			session.removeAttribute("noDelete");
		}		
		return "admin/ubc/manage_volunteerSurvey";
	}
	
	@RequestMapping(value = "/upload_volunteer_survey_template", method=RequestMethod.POST)
	public String addVolunteerSurveyTemplate(HttpServletRequest request) throws Exception
	{
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute("loggedInUser");
		int siteId = loggedInUser.getSite();
   		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
   		MultipartFile multipartFile = multipartRequest.getFile("file");
   		
		//Add a new survey template
		SurveyTemplate st = new SurveyTemplate();
		st.setTitle(request.getParameter("title"));
		st.setType(request.getParameter("type"));
		st.setDescription(request.getParameter("desc"));
		int p = Integer.parseInt(request.getParameter("priority"));
		st.setPriority(p); 
		st.setContents(multipartFile.getBytes());
		if (request.isUserInRole("ROLE_ADMIN"))
			st.setSite(Integer.parseInt(request.getParameter("site")));
		else
			st.setSite(siteId);
		
		if ("1".equals(request.getParameter("default_volunteer_survey")))
			st.setDefaultSurvey(true);
		else
			st.setDefaultSurvey(false);
		
		surveyManager.uploadVolunteerSurveyTemplate(st);
		
//		session.setAttribute("volunteerSurveyTemplateMessage", "C");
		//update survey template in the session
		session.removeAttribute("volunteerSurvey_template_list");
		List<SurveyTemplate> volunteerSurveyTemplates;
		if (request.isUserInRole("ROLE_ADMIN"))
			volunteerSurveyTemplates = surveyManager.getAllVolunteerSurveyTemplates();
		else
			volunteerSurveyTemplates = surveyManager.getVolunteerSurveyTemplatesBySite(siteId);
		session.setAttribute("volunteerSurvey_template_list", volunteerSurveyTemplates);		
					
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has uploaded volunteer survey template ");
		sb.append(request.getParameter("title"));
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		return "redirect:/manage_volunteer_survey";
	}
	
	@RequestMapping(value="/search_volunteer_survey", method=RequestMethod.POST)
	public String searchVolunteerSurvey(@RequestParam(value="failed", required=false) Boolean failed, ModelMap model, 
			SecurityContextHolderAwareRequestWrapper request)
	{	
		String title = request.getParameter("searchTitle");		
		List<SurveyTemplate>  surveyTemplateList = surveyManager.getVolunteerSurveyTemplatesByPartialTitle(title);
		
		model.addAttribute("volunteer_survey_templates", surveyTemplateList);
	
		if(failed != null) {
			model.addAttribute("failed", true);
		}		 
		
		model.addAttribute("searchTitle", title);
		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "admin/ubc/manage_volunteerSurvey";	
	}
	
	@RequestMapping(value="/modify_volunteerSurveyTemplate/{surveyID}", method=RequestMethod.GET)
   	public String modifyVolunteerSurveyTemplate(@PathVariable("surveyID") int id, HttpServletRequest request, ModelMap model)
	{ 	
		List<SurveyTemplate> volunteerSurveyTemplates;
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		
		if (request.isUserInRole("ROLE_ADMIN"))//central admin
			volunteerSurveyTemplates = surveyManager.getAllVolunteerSurveyTemplates();
		else //local admin
			volunteerSurveyTemplates = surveyManager.getVolunteerSurveyTemplatesBySite(loggedInUser.getSite());
		List<SurveyTemplate> surveyTemplates = surveyManager.getAllVolunteerSurveyTemplates();
				
		for (SurveyTemplate st: volunteerSurveyTemplates)
		{
			if (id == st.getSurveyID())
			{
				model.addAttribute("volunteerSurveyTemplate", st);
				break;
			}
		}		
		return "/admin/ubc/edit_volunteer_surveyTemplate";
	}
	
	@RequestMapping(value="/edit_volunteersurveyTemplate/{surveyID}", method=RequestMethod.POST)
   	public String editVolunteerSurveyTemplate(@PathVariable("surveyID") int id, HttpServletRequest request)
	{		
		List<SurveyTemplate> volunteerSurveyTemplates;
		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute("loggedInUser");
						
		SurveyTemplate st = new SurveyTemplate();
		st.setSurveyID(id);
		st.setTitle(request.getParameter("title"));		
		st.setDescription(request.getParameter("description"));		
		
		surveyManager.updateVolunteerSurveyTemplate(st);
		
		if (loggedInUser.getRole().equals("ROLE_ADMIN"))//central admin
			volunteerSurveyTemplates = surveyManager.getAllVolunteerSurveyTemplates();
		else //local admin
			volunteerSurveyTemplates = surveyManager.getVolunteerSurveyTemplatesBySite(loggedInUser.getSite());		
		
		//update survey template in the session
		session.removeAttribute("volunteerSurvey_template_list");
		session.setAttribute("volunteerSurvey_template_list", volunteerSurveyTemplates);
//		session.setAttribute("surveyTemplateMessage", "U");
		
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has modified volunteer survey template # ");
		sb.append(id);
		userManager.addUserLog(sb.toString(), loggedInUser);
		
		return "redirect:/manage_volunteer_survey";
	}
	
 	@RequestMapping(value="/assign_volunteerSurvey", method=RequestMethod.GET)
	public String getSurveyForVolunteer( SecurityContextHolderAwareRequestWrapper request, 	ModelMap model)
   	{ 
   		List<SurveyTemplate> surveyTemplates;
   		User loginUser = TapestryHelper.getLoggedInUser(request); 
   		
   		if (loginUser.getRole().equals("ROLE_ADMIN"))//central admin
   			surveyTemplates = surveyManager.getAllVolunteerSurveyTemplates();
		else //local admin
			surveyTemplates = surveyManager.getVolunteerSurveyTemplatesBySite(loginUser.getSite());
   		if (surveyTemplates == null)
				return "redirect:/manage_volunteer_survey?failed=true";   		
   		
   		List<Volunteer> volunteers;
   		int organizationId = loginUser.getOrganization();
   		if (organizationId == 0)//central admin
   			volunteers = volunteerManager.getAllVolunteers();
   		else
   			volunteers = volunteerManager.getAllVolunteersByOrganization(organizationId);   		
   		
   		model.addAttribute("volunteers", volunteers);
   		model.addAttribute("volunteerSurveyTemplates", surveyTemplates);   		

   		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
   		
		return "/admin/ubc/assign_volunteerSurvey";
	} 
 	
   	@RequestMapping(value="/assign_volunteerSurvey", method=RequestMethod.POST)
	public String assignVolunteerSurvey(SecurityContextHolderAwareRequestWrapper request, ModelMap model) 
			throws JAXBException, DatatypeConfigurationException, Exception
	{   
   		List<SurveyTemplate> sTemplates = surveyManager.getAllVolunteerSurveyTemplates();	
   		
   		ArrayList<SurveyTemplate> selectSurveyTemplats = new ArrayList<SurveyTemplate>();
   		User loginUser = TapestryHelper.getLoggedInUser(request);   		
   		String[] surveyTemplateIds = request.getParameterValues("volunteerSurveyTemplates");    		
   		int[] volunteerIds;    		
   		List<Volunteer> volunteers; 
   		
   		int organizationId = loginUser.getOrganization();
   		if (organizationId == 0)//central admin
   			volunteers = volunteerManager.getAllVolunteers();
   		else
   			volunteers = volunteerManager.getAllVolunteersByOrganization(organizationId); 
   	
   		if (request.getParameter("searchVolunteer") != null && request.getParameter("searchVolunteerName") !=null )
   		{//search volunteer by name
   			String name = request.getParameter("searchVolunteerName");   		   			
   			volunteers = volunteerManager.getVolunteersByName(name);			
   			model.addAttribute("searchVolunteerName", name);	 	   			
	   	}
   		else if(request.getParameter("assignVolunteerSurvey") != null)//assign selected surveys to selected volunteer
   		{       		
   			String[] selectedVolunteerIds = request.getParameterValues("volunteerId");
   			String assignToAll = request.getParameter("assignAllVolunteers");	
   			//get survey template list 
   			if (surveyTemplateIds != null && surveyTemplateIds.length > 0)
   			{
   				int stId;
   				
   				for (int i = 0; i < surveyTemplateIds.length; i ++)
   		   		{  						
   					stId = Integer.parseInt(surveyTemplateIds[i]);
   		  				
   		  			for (SurveyTemplate st: sTemplates){
   		  				if (stId == st.getSurveyID())
   		  				selectSurveyTemplats.add(st);
   		  	   		}
   		   		}	
   				StringBuffer sb = new StringBuffer();
   				sb.append(loginUser.getName());
   				sb.append(" has assigned surveys to volunteer");
   				String logDes = sb.toString();
   				
   				if ("true".equalsIgnoreCase(assignToAll))
   				{//for assign to all volunteers   			
   					Volunteer volunteer;   			
		   	   		volunteerIds = new int[volunteers.size()];
		   	   			
		   	   		for(int i = 0; i < volunteers.size(); i++)
		   	   		{
		   	   			volunteer = new Volunteer();
		   	   			volunteer = volunteers.get(i);
		   	   			volunteerIds[i] = volunteer.getVolunteerId();
		   	   		}		   	   		
		   	   
		   	   		TapestryHelper.assignSurveysToVolunteer(selectSurveyTemplats, volunteerIds, request, model, surveyManager);		
		   	   		userManager.addUserLog(logDes, loginUser);
   				}
   				else
   	   	   		{//for selected patients, convert String[] to int[]   			
   	   	   			if (selectedVolunteerIds == null || selectedVolunteerIds.length == 0)
   	   	   				model.addAttribute("no_volunteer_selected", true);
   	   	   			else
   	   	   			{
   	   	   				int[] iSelectedVolunteerIds = new int[selectedVolunteerIds.length];
   	   	   	   			for (int j = 0; j < selectedVolunteerIds.length; j++){
   	   	   	   				iSelectedVolunteerIds[j] = Integer.parseInt(selectedVolunteerIds[j]);
   	   	   				}  
   	   	   	   			TapestryHelper.assignSurveysToVolunteer(selectSurveyTemplats, iSelectedVolunteerIds, request, model, surveyManager);
   	   	   	   			userManager.addUserLog(logDes, loginUser);
   	   	   			}   			
   	   	   		} 		 
   			}
   			else//no survey template has been selected
   				model.addAttribute("no_survey_selected", true);
   		}
   		model.addAttribute("volunteerSurveyTemplates", sTemplates);
   		model.addAttribute("volunteers", volunteers);
   		
   		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "admin/ubc/assign_volunteerSurvey";
	}
   	
   	@RequestMapping(value="/view_mySurveys", method=RequestMethod.GET)
	public String viewMySurveys(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{	
   		//Find the name of the current user
		User loginUser = TapestryHelper.getLoggedInUser(request);
		HttpSession session = request.getSession();
		
		int volunteerId = TapestryHelper.getLoggedInVolunteerId(request);
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));		

		List<SurveyResult> completedSurveyResultList = surveyManager.getCompletedVolunteerSurveys(volunteerId);
		List<SurveyResult> incompleteSurveyResultList = surveyManager.getIncompleteVolunteerSurveys(volunteerId);
		Collections.sort(completedSurveyResultList);
		Collections.sort(incompleteSurveyResultList);
		
		//get display survey result for displaying question text and answer
		String xml;
		LinkedHashMap<String, String> res;
		List<DisplayedSurveyResult> displayedResults;
		List<DisplayedSurveyResult> completedDisplayedResults = new ArrayList<DisplayedSurveyResult>();
	
		if (completedSurveyResultList.size() > 0)
		{
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
		}
		//replace answers with full detailed information
		completedDisplayedResults = TapestryHelper.getDetailedAnswerForUBCSurveys(completedDisplayedResults);
		model.addAttribute("completedVolunteerSurveys", completedSurveyResultList);
		model.addAttribute("inProgressVolunteerSurveys", incompleteSurveyResultList);
		model.addAttribute("displayResults", completedDisplayedResults);
		model.addAttribute("volunteerName", loginUser.getName());
		
		return "/volunteer/ubc/view_mySurveys";
//		return "volunteer/ubc/view_mySurveys";
	}
   	
	@RequestMapping(value="/open_volunteerSurvey/{resultID}", method=RequestMethod.GET)
	public String openVolunteerSurvey(@PathVariable("resultID") int id, HttpServletRequest request) 
	{		//move logging down to /show_survey/{resultID}
		return "redirect:/show_volunteerSurvey/" + id;
	}
	
   	@RequestMapping(value="/show_volunteerSurvey/{resultID}", method=RequestMethod.GET)
   	public ModelAndView showVolunteerSurvey(@PathVariable("resultID") int id, HttpServletRequest request)
   	{   	   		
   		ModelAndView redirectAction = null;
   		List<SurveyResult> volunteerSurveyResults;
		List<SurveyTemplate> volunteerSurveyTemplates;
		
		SurveyResult surveyResult = surveyManager.getVolunteerSurveyResultByID(id);				
		TapestrySurveyMap userSurveys = TapestryHelper.getVolunteerSurveyMap(request);
		
		if ((userSurveys == null)||(userSurveys.getSurvey(String.valueOf(id)) == null))
		{	
   			volunteerSurveyTemplates = surveyManager.getAllVolunteerSurveyTemplates();
			volunteerSurveyResults = surveyManager.getVolunteerSurveyResultsByVolunteerId(surveyResult.getVolunteerID());
			
			userSurveys = TapestryHelper.storeVolunteerSurveyMapInSession(request, volunteerSurveyResults, volunteerSurveyTemplates);
		}
		
		SurveyTemplate surveyTemplate = surveyManager.getVolunteerSurveyTemplateByID(surveyResult.getSurveyID());
		TapestryPHRSurvey currentSurvey = userSurveys.getSurvey(Integer.toString(id));		
	
		try {
			SurveyFactory surveyFactory = new SurveyFactory();
			PHRSurvey templateSurvey = surveyFactory.getSurveyTemplate(surveyTemplate);	
			
			redirectAction = TapestryHelper.execute(request, Integer.toString(id), currentSurvey, templateSurvey);
		} catch (Exception e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
		}
		
		if (redirectAction == null){ //Assuming we've completed the survey
			System.out.println("Something bad happened");
		}
				
   		if (request.isUserInRole("ROLE_USER") && redirectAction.getViewName() == "failed")
   			redirectAction.setViewName("redirect:/");	
   
   		return redirectAction;
   	}
   	
  	@RequestMapping(value="/save_volunteerSurvey/{resultID}", method=RequestMethod.GET)
   	public String saveVolunteerSurveyAndExit(@PathVariable("resultID") int id, HttpServletRequest request) throws Exception
	{
   		boolean isComplete = Boolean.parseBoolean(request.getParameter("survey_completed"));   		
		//For activity logging purposes	
		TapestrySurveyMap surveys = TapestryHelper.getVolunteerSurveyMap(request);		
		PHRSurvey currentSurvey = surveys.getSurvey(Integer.toString(id));
		
		byte[] data = null;
		try {
			data = SurveyAction.updateSurveyResult(currentSurvey);	
			surveyManager.updateVolunteerSurveyResults(id, data);
		} catch (Exception e) {
			System.out.println("Failed to convert PHRSurvey into a byte array");
			e.printStackTrace();
		}
		
		if (isComplete) {
			currentSurvey.setComplete(true);			
			surveyManager.markAsCompleteForVolunteerSurvey(id);			
		}
		
		if (request.isUserInRole("ROLE_USER"))
			return "redirect:/view_mySurveys";
		else
		{
			SurveyResult sr = surveyManager.getVolunteerSurveyResultByID(id);
			int volunteerId = sr.getVolunteerID();
			return "redirect:/display_volunteer/" + volunteerId;
		}
	}
  	
   	@RequestMapping(value="/view_volunteer_survey_results/{resultID}", method=RequestMethod.GET)
   	public String viewVolunteerSurveyResults(@PathVariable("resultID") int id, HttpServletRequest request, ModelMap model)
   	{
   		SurveyResult r = surveyManager.getVolunteerSurveyResultByID(id); 		
   		
   		String xml;
   		try{
   			xml = new String(r.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> res = ResultParser.getResults(xml);
   		List<DisplayedSurveyResult> displayedResults = ResultParser.getDisplayedSurveyResults(res);   		
   		displayedResults = TapestryHelper.getDetailedAnswerForUBCSurveys(displayedResults); 

   		model.addAttribute("results", displayedResults);
   		model.addAttribute("id", id);
   		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		//add logs
   		User loggedInUser = (User)session.getAttribute("loggedInUser");
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has viewed volunteer survey : ");
		sb.append(r.getSurveyTitle());
		userManager.addUserLog(sb.toString(), loggedInUser);
   		
   		return "/admin/view_survey_results";
   	}
   	
   	@RequestMapping(value="/delete_volunteerSurvey/{resultID}", method=RequestMethod.GET)
   	public String deleteVolunteerSurvey(@PathVariable("resultID") int id, HttpServletRequest request)
   	{
   		
   		SurveyResult sr = surveyManager.getVolunteerSurveyResultByID(id);   		
   		surveyManager.deleteVolunteerSurvey(id);
   		
   		List<SurveyResult> surveyResults;
   		List<SurveyTemplate> surveyTemplates;
   		HttpSession session = request.getSession();
		User loggedInUser = (User)session.getAttribute("loggedInUser");	
		int siteId = loggedInUser.getSite();
		int volunteerId = sr.getVolunteerID();
		
		surveyResults = surveyManager.getVolunteerSurveyResultsByVolunteerId(volunteerId);
		if (loggedInUser.getRole().equals("ROLE_ADMIN"))//central admin 
			surveyTemplates = surveyManager.getAllSurveyTemplates();
   		else //local admin/site admin
   			surveyTemplates = surveyManager.getSurveyTemplatesBySite(siteId);
		
   		DoSurveyAction.updateSurveyMapInSession(request, surveyResults, surveyTemplates);
   		
   		//archive the deleted survey result
   		String volunteerName = volunteerManager.getVolunteerNameById(volunteerId);
   		   		
   		surveyManager.archiveSurveyResult(sr, volunteerManager.getVolunteerNameById(volunteerId), loggedInUser.getName());
   		//add logs   		
   		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has deleted volunteer survey result # ");
		sb.append(id);
		userManager.addUserLog(sb.toString(), loggedInUser);
   		
   		return "redirect:/display_volunteer/" + sr.getVolunteerID();
   	}
   	
	@RequestMapping(value="/download_volunteerSurveyReport/{volunteerId}", method=RequestMethod.GET)
   	public String downloadVolunteerSurveyReport(@PathVariable("volunteerId") int id, @RequestParam(value="name", required=false) String name, 
   			HttpServletRequest request, HttpServletResponse response)
   	{
		TapestryHelper.generateVolunteerSurveyReport(id, surveyManager, response, name);		
		return null;   	
   	}
	
	@RequestMapping(value="/download_clientSurveyReport/{patientId}", method=RequestMethod.GET)
   	public String downloadClinetSurveyReport(@PathVariable("patientId") int id, @RequestParam(value="name", required=false) String name, 
   			@RequestParam(value="hasObservernotes", required=false) boolean hasObservernotes, HttpServletRequest request, HttpServletResponse response)
   	{	
   		int site = patientManager.getSiteByPatientId(id);   		
   		if (site == 3)//for UBC
   			TapestryHelper.generateClientReportForUBC(id, surveyManager, response, name, hasObservernotes);
   		else if (site == 1)		//McMaster
   			TapestryHelper.generateClientSurveyReport(id, surveyManager, response, name, 1); 	
   		else  // McGill
   			TapestryHelper.generateClientSurveyReport(id, surveyManager, response, name, 2); 	
 
		return null;   	
   	}
	
	@RequestMapping(value="/delete_volunteer_survey_template/{surveyID}", method=RequestMethod.GET)
   	public String deleteVolunteerSurveyTemplate(@PathVariable("surveyID") int id, ModelMap model, 
   			SecurityContextHolderAwareRequestWrapper request)
   	{ 	
		HttpSession session = request.getSession();
		int count = surveyManager.countVolunteerSurveyResultsBySurveyId(id);
		
		if (count>0)
			session.setAttribute("noDelete", true);
		else
		{
			SurveyTemplate st = surveyManager.getVolunteerSurveyTemplateByID(id);
			surveyManager.deleteVolunteerSurveyTemplate(id);
			
			List<SurveyTemplate> volunteerSurveyTemplates;
			User loggedInUser = TapestryHelper.getLoggedInUser(request);
			
			
			if (loggedInUser.getRole().equals("ROLE_ADMIN"))//central admin
				volunteerSurveyTemplates = surveyManager.getAllVolunteerSurveyTemplates();
			else //local admin
				volunteerSurveyTemplates = surveyManager.getVolunteerSurveyTemplatesBySite(loggedInUser.getSite());
			//update survey template in the session  
			session.setAttribute("volunteerSurvey_template_list", volunteerSurveyTemplates);		
			session.setAttribute("surveyTemplateMessage", "D");
			
			//archive the record		
			surveyManager.archiveSurveyTemplate(st, loggedInUser.getName());
			//user logs   			
			StringBuffer sb  = new StringBuffer();
			sb.append(loggedInUser.getName());
			sb.append(" deleted volunteer survey template # ");
			sb.append(id);   			   		
			userManager.addUserLog(sb.toString(), loggedInUser);
			
		}
		return "redirect:/manage_volunteer_survey";		
   	}
	
	@RequestMapping(value="/set_default_survey_templates", method=RequestMethod.GET)
   	public String getDefaultSurveyTemplates(HttpServletRequest request, ModelMap model)
   	{		
		List<SurveyTemplate> surveyTemplates = TapestryHelper.getSurveyTemplates(request, surveyManager);	
		
		model.addAttribute("survey_templates", surveyTemplates);
		return "/admin/default_survey_template";   	
   	}
	
	@RequestMapping(value="/set_defaultSurveys", method=RequestMethod.POST)
   	public String setDefaultSurveyTemplates(HttpServletRequest request, ModelMap model)
   	{		
		String action = request.getParameter("hAction");
		String[] surveyTemplateIds = request.getParameterValues("survey_template"); 	
		
		if (surveyTemplateIds == null)
			System.out.println("surveyTemplateIds is null");
		else
		{
			if ("Set Default".equals(action))
				surveyManager.setDefaultSurveyTemplate(surveyTemplateIds);
			else
				surveyManager.removeDefaultSurveyTemplateSetting(surveyTemplateIds);
			
			model.addAttribute("emptyMsg", true);
		}
		List<SurveyTemplate> surveyTemplates = TapestryHelper.updateSurveyTemplates(request, surveyManager);	
		model.addAttribute("survey_templates", surveyTemplates);
		
		return "/admin/default_survey_template";   	
   	}
	
	@RequestMapping(value="/myAjaxTest.html", method = RequestMethod.POST)
	@ResponseBody	
	public String getSurveyTemplateBySite(@RequestParam(value="site") int site)
	{System.out.println("hi surveyTemplate ajax");
		List<SurveyTemplate> surTemplates = surveyManager.getSurveyTemplatesBySite(site);
		
		String surTitles;
		StringBuffer sb = new StringBuffer();
		
		for(SurveyTemplate st: surTemplates)
		{
			sb.append(st.getTitle());
			sb.append(";");
			
		}
		
		return sb.toString();
	}
	
	@RequestMapping(value="/view_adminActivities", method=RequestMethod.GET)
	public String viewAdminActivity(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{	
		List<AdminActivity> activities = new ArrayList<AdminActivity>();				
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		activities = userManager.getAllAdminActivities(loggedInUser.getUserID());
			
		if (activities.size() == 0 )  
			model.addAttribute("emptyActivityLogs", true);			
		
		model.addAttribute("activityLogs", activities);	
	
		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));		
			
		return "/admin/view_adminActivities";
	}
	
	@RequestMapping(value="/new_AdminActivity", method=RequestMethod.GET)
	public String newAdminActivity(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{		
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		return "/admin/new_adminActivity";
	}
	
	@RequestMapping(value="/add_adminActivity", method=RequestMethod.POST)
	public String addAdminActivity(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{		
		HttpSession  session = request.getSession();		
		User loggedInUser = TapestryHelper.getLoggedInUser(request);	
			
		String date = request.getParameter("activityDate");
		String startTime = request.getParameter("activityStartTime");	
		String endTime = request.getParameter("activityEndTime");	
		String description = request.getParameter("activityDesc");
			
		AdminActivity activity = new AdminActivity();
		activity.setDescription(description);
		activity.setUser(loggedInUser.getUserID());		
		activity.setSiteId(loggedInUser.getSite());
		activity.setDate(date);				
			
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
		userManager.logAdminActivity(activity);
		
		//add log
		sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has added an activity ");		
		userManager.addUserLog(sb.toString(), loggedInUser);
			
		//update view activity page with new record		
		session.setAttribute("ActivityMessage", "C");		
		
		return "redirect:/view_adminActivities";
	}	
	
	@RequestMapping(value="/delete_adminActivity/{activityId}", method=RequestMethod.GET)
	public String deleteActivityById(@PathVariable("activityId") int id, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{			
		User loggedInUser = TapestryHelper.getLoggedInUser(request);		
		AdminActivity activity = userManager.getAdminActivityById(id);		
		userManager.deleteAdminActivityById(id);
		userManager.archiveAdmindActivity(activity, loggedInUser.getName());
		
		//add logs		
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has deleted the admin acitiviy #  ");
		sb.append(id);		
		userManager.addUserLog(sb.toString(), loggedInUser);
					
		HttpSession  session = request.getSession();		
		session.setAttribute("ActivityMessage", "D");		
		
		return "redirect:/view_adminActivities";		
	}
		
	@RequestMapping(value="/modify_adminActivity/{activityId}", method=RequestMethod.GET)
	public String modifyAdminActivityLog(SecurityContextHolderAwareRequestWrapper request, 
			@PathVariable("activityId") int id, ModelMap model)
	{
		AdminActivity activity = new AdminActivity();
		activity = userManager.getAdminActivityById(id);
		model.addAttribute("activityLog", activity);
		
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));	
		
		return "/admin/modify_adminActivity";
	}
		
	@RequestMapping(value="/update_adminActivity", method=RequestMethod.POST)
	public String updateAdminActivityById(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		String activityId = null;
		int iActivityId = 0;		
		AdminActivity activity = new AdminActivity();			
		HttpSession  session = request.getSession();		
			
		activityId = request.getParameter("activityId");
		iActivityId = Integer.parseInt(activityId);
			
		activity = userManager.getAdminActivityById(iActivityId);			
			
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
		userManager.updateActivity(activity);
		
		//add logs
		User loggedInUser = TapestryHelper.getLoggedInUser(request);
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" has modified the  admin acitiviy #  ");
		sb.append(activityId);				
		userManager.addUserLog(sb.toString(), loggedInUser);
			
		session.setAttribute("ActivityMessage","U");
		return "redirect:/view_adminActivities";	
	}
	
	@RequestMapping(value="/setDefault_existingClient", method=RequestMethod.GET)
	public String updateExistingClient(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{
		List<Patient> patients = patientManager.getAllPatients();
		patientManager.setDefaultUsernameAndPassword();
		User user;
		int site;
		
		ShaPasswordEncoder enc = new ShaPasswordEncoder();		
		String tapPassword = enc.encodePassword("tap_client", null);
		
		for (Patient p: patients)
		{
			site = patientManager.getSiteByPatientId(p.getPatientID());
			user = new User();
			
			user.setName(p.getFirstName() + " " + p.getLastName());
			user.setUsername(p.getTapUsername());
			user.setRole("ROLE_CLIENT");	
			user.setPassword(tapPassword);
			user.setSite(site);
			user.setEmail(" ");	
			user.setOrganization(0);
			
			userManager.createUser(user);	
		}
		
		return "redirect:/";
	}
	
}
