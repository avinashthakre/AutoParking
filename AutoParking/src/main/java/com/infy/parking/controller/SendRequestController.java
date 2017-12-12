package com.infy.parking.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.infy.parking.models.BuildingDetails;
import com.infy.parking.models.SlotDetails;
import com.infy.parking.models.UserDetails;
import com.infy.parking.service.Buildingservice;
import com.infy.parking.service.SlotsService;
import com.infy.parking.service.UserService;
import com.infy.parking.utilities.CSVReader;


//@RestController
@Controller
@RequestMapping("/client")
public class SendRequestController {


	@Autowired
	private BuildingDetails buildingDetails;
	@Autowired
	private Buildingservice buildingService;
	@Autowired
	private SlotDetails slotDetails;
	@Autowired
	private SlotsService slotsService;
	@Autowired
	private UserDetails userDetails;
	@Autowired
	private UserService userService;

	@RequestMapping(value = "/getClient", method = RequestMethod.GET)
	public String getClient(ModelMap model) {
		System.out.println("Invoking REST Client ...");
		return "Home";
	}

	@RequestMapping(value = "/sendrequest", method = RequestMethod.GET )
	public String sendRequest(HttpServletRequest request, Model model) {



		model.addAttribute("message","Please wait, parking slot is getting book...");
		return "list";
	}

	//for add building view
	@RequestMapping(value = "/addBuilding", method = RequestMethod.GET)
	public String addBuilding(ModelMap model,HttpServletRequest request) {
		try {
			if(validateSession(request))
				return "addBuilding";
			else {
				return "adminLogin";
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "adminLogin";
	}


	//for add building persist

	@RequestMapping(value = "/addBuildingRequest", method = RequestMethod.POST )
	public String addBuildingRequest(ModelMap model,@RequestParam("buildingId") String bId,@RequestParam("buildingName") String bName,HttpServletRequest request) {
		try {
			if(validateSession(request)) { 
				buildingDetails.setBuildingId(bId);
				buildingDetails.setBuildingName(bName);
				buildingService.persistBuildingDetails(buildingDetails);
				model.addAttribute("message","Building Details added successfully");
				return "addBuilding";
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message",e.getMessage());
		}

		return "adminLogin";
	}

	//for add slot view
	@RequestMapping(value = "/addSlot", method = RequestMethod.GET)
	public String addSlot(ModelMap model,HttpServletRequest request) {
		try {
			if(validateSession(request)) { 
				List<BuildingDetails> buildingList = buildingService.getBuildingDetails();
				model.addAttribute("buildingList",buildingList);
				return "addSlot";
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message",e.getMessage());
		}
		return "adminLogin";
	}


	//for add slot persist
	@RequestMapping(value = "/addSlotRequest", method = RequestMethod.POST)
	public String addSlotRequest(ModelMap model,@RequestParam(value="buildingId", defaultValue = "MLPL1") String bId,@RequestParam(value="floorId", defaultValue = "F00") String fId,
			@RequestParam(value="slotId", defaultValue = "anonymous") String slotId,@RequestParam(value="slotFile", required=false) MultipartFile file,HttpServletRequest request) {

		if(validateSession(request)) {
			if(file!=null ) {
				System.out.println("file name "+file.getName());
				CSVReader csvReader =new CSVReader();
				List<SlotDetails> list =csvReader.readSlotDetails(file);
				try {
					slotsService.persistSlotsDetailsList(list);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				System.out.println("buildingId "+bId);
				System.out.println("floor Id "+fId);
				System.out.println("slot ID "+slotId);
				try {
					String slotFullId =bId+"_"+fId+"_SL"+slotId;
					System.out.println(slotFullId);
					slotDetails.setBuildingId(bId);
					slotDetails.setSlotId(slotFullId);
					slotsService.persistSlotsDetails(slotDetails);

					List<BuildingDetails> buildingList = buildingService.getBuildingDetails();
					model.addAttribute("buildingList",buildingList);
					model.addAttribute("message","Slot Details added successfully");
				}
				catch (Exception e) {
					e.printStackTrace();
					model.addAttribute("message",e.getMessage());
				}
			}
			return "addSlot";
		}
		else
			return "adminLogin";

	}

	@RequestMapping(value = "/getRegistration", method = RequestMethod.GET)
	public String getRegistration(ModelMap model, HttpServletRequest request) {
		System.out.println("Invoking REST Client for Registration Page ...");
		if(validateSession(request))
			return "Registration";
		else 
			return "adminLogin";
	}

	@RequestMapping(value = "/validateRegistration", method = RequestMethod.GET)
	public String validateRegistration(HttpServletRequest request, Model model) {
		if(validateSession(request)) {
			String errorMessage = request.getParameter("error");
			String empId = request.getParameter("empId");
			String email = request.getParameter("emailAddress");
			String password = request.getParameter("password");
			System.out.println("Email : " + email);
			System.out.println("password : " + password);		
			System.out.println("Error Message : '" + errorMessage+"'");

			if (errorMessage == null) {
				try {
					System.out.println("User Service : "+userService);

					userDetails.setEmail(email);
					userDetails.setEmployeeId(Integer.valueOf(empId));
					userDetails.setPassword(password);
					System.out.println(userDetails.toString());
					userService.persistUsersDetails(userDetails);
					System.out.println("Person::" + userDetails);
					model.addAttribute("message", "Hi " + email + " , you have entered password as " + password);
				} catch (Exception e) {
					e.getStackTrace();
					System.out.println(e.getStackTrace());
				}			
				return "list";
			}
			return "Registration";	
		}
		else
			return "adminLogin";
	}

	private boolean validateSession(HttpServletRequest request) {
		try {
			HttpSession hSession= request.getSession();

			if(hSession.getAttribute("user")!=null && hSession.getAttribute("user").toString().equals("admin")&&hSession.getAttribute("isValid").toString().equals("true")) {
				return true;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


}