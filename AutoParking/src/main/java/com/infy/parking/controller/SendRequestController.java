package com.infy.parking.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.infy.parking.models.BuildingDetails;
import com.infy.parking.models.SlotDetails;
import com.infy.parking.service.Buildingservice;
import com.infy.parking.service.SlotsService;



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
	public String addBuilding(ModelMap model) {
		System.out.println("Invoking REST Client ...");
		return "addBuilding";
	}


	//for add building persist
	@RequestMapping(value = "/addBuildingRequest", method = RequestMethod.POST)
	public String addBuildingRequest(ModelMap model,@RequestParam("buildingId") String bId,@RequestParam("buildingName") String bName) {
		System.out.println("buildingId "+bId);
		System.out.println("buildingName "+bName);
		try {
			buildingDetails.setBuildingId(bId);
			buildingDetails.setBuildingName(bName);
			buildingService.persistBuildingDetails(buildingDetails);
			model.addAttribute("message","Building Details added successfully");
		}
		catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message",e.getMessage());
		}

		return "addBuilding";
	}

	//for add slot view
	@RequestMapping(value = "/addSlot", method = RequestMethod.GET)
	public String addSlot(ModelMap model) {
		try {
			List<BuildingDetails> buildingList = buildingService.getBuildingDetails();
			System.out.println("list size "+buildingList.size());

			model.addAttribute("buildingList",buildingList);
		}
		catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message",e.getMessage());
		}
		return "addSlot";
	}


	//for add slot persist
	@RequestMapping(value = "/addSlotRequest", method = RequestMethod.POST)
	public String addSlotRequest(ModelMap model,@RequestParam("buildingId") String bId,@RequestParam("floorId") String fId,
			@RequestParam("slotId") String slotId,@RequestParam("fileName") MultipartFile file) {

		if(file!=null) {
			System.out.println("file name "+file.getName());
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


}