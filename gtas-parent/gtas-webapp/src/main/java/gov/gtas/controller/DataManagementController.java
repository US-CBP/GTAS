package gov.gtas.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import gov.gtas.model.Role;
import gov.gtas.model.User;
import gov.gtas.repository.DataManagementRepositoryImpl.DataTruncationType;
import gov.gtas.services.DataManagementService;
import gov.gtas.services.security.UserService;


@Controller
public class DataManagementController 
{
	
  @Autowired
  private DataManagementService dataManagementService;
  
  @Autowired
  private UserService userService;
  
  private String dataManagementPage = "/dataManagement/dataManagement.html";
  private String noPermissionPage = "/dataManagement/dmNoPermission.html";
  private String outcomePage = "/dataManagement/dmOutcome.html";
  
  
  @RequestMapping(method = RequestMethod.GET, value = "/datamanagement")
  public String manageGtasData()
  {
	  String returnStr = noPermissionPage;

	  User currentUser = fetchCurrentUser();
	  Set<Role> roles = currentUser.getRoles();

	  Optional<Role> sysAdminRole = null;
	  
	  if (!roles.isEmpty())
	  {
		  sysAdminRole =  roles.stream().filter(r -> (r.getRoleId() == 6)).findAny();
	  }
	  
	  if (sysAdminRole.isPresent())
	  {
		 returnStr = dataManagementPage;
     
	  }
	  
	  return returnStr;
	  
  }
  
  @RequestMapping(method = RequestMethod.POST, value = "/dmcapabilities/process")
  public ModelAndView processDataTruncation(@RequestParam(value = "date", required = true) String date, @RequestParam(value = "truncationType", required = true) String truncationType)
  {
      String message = "Successfully truncated all message related data before the selected date.View logs to get info on number of rows deleted.";
      
      User currentUser = fetchCurrentUser();
      
	  try
	  {	  
		  String[] dateArray = date.split("-");
		  int year = Integer.parseInt(dateArray[0]);
		  int month = Integer.parseInt(dateArray[1]);
		  int day = Integer.parseInt(dateArray[2]);
		  
		  LocalDate localDate = LocalDate.of(year, month, day);
	  
		  URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
                  
                  DataTruncationType type = null;
                  
                  if (truncationType.equals("ALL"))
                  {
                    type = DataTruncationType.ALL;  
                  }
                  else if (truncationType.equals("APIS"))
                  {
                      type = DataTruncationType.APIS_ONLY;
                  }
                  else if (truncationType.equals("PNR"))
                  {
                      type = DataTruncationType.PNR_ONLY;
                  }
		  
	      dataManagementService.truncateAllMessageDataByDate(localDate, currentUser, type);
	  }
	  catch (Exception ex)
	  {
		  System.out.println("Exception happened while deleting data: " + ex.getMessage()); 
		  message = "Exception happened while deleting data: " + ex.getMessage();
		  try
		  {
		      URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
		  }
		  catch (Exception ex2)
		  {
			  // not gonna get here
		  }
	  }
	  
		ModelMap model = new ModelMap();
		model.addAttribute("message", message);
		
		return new ModelAndView("redirect:/dataManagement/dmOutcome.html", model);

  }
  
  private User fetchCurrentUser()
  {
	  String username = "";
	  
	  Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	  if (principal instanceof UserDetails) {

	    username = ((UserDetails)principal).getUsername();

	  } else {

	    username = principal.toString();

	  }

	  User currentUser = userService.fetchUser(username);	  
	  
	  return currentUser;
  }
  
}
