package com.ezeeshipping.eficaa.oms.outage.test;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ezeeshipping.eficaa.oms.commons.vo.OutageSearchVO;
import com.ezeeshipping.eficaa.oms.core.utils.DateUtil;
import com.ezeeshipping.eficaa.oms.outage.dashBoardServices.DashBoardService;
import com.ezeeshipping.eficaa.oms.outage.model.Outage;
import com.ezeeshipping.eficaa.oms.outage.repository.OutageRepository;
import com.ezeeshipping.eficaa.oms.outage.services.OutageException;
import com.ezeeshipping.eficaa.oms.outage.services.OutageService;
import com.ezeeshipping.eficaa.oms.outage.vo.CrewTaskVO;
import com.ezeeshipping.eficaa.oms.outage.vo.DashBoardVO;
import com.ezeeshipping.eficaa.oms.outage.vo.IndicesVO;
import com.ezeeshipping.eficaa.oms.outage.vo.LatestOutagesVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OnGoingAffectedCustomerVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageCountVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageDetailResponseV2VO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageExtensionRequestVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageLiveGraphVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageResponseVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageV2VO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jdk.nashorn.internal.ir.annotations.Ignore;

//@RunWith
@SpringBootTest
public class OmsOutageServiceApplicationTests {

	@Autowired
	OutageService outageService;
	@Autowired
	DashBoardService dashBoardService;
	@Ignore
	public void search()  {
		
	}
	@Ignore
	public void searchOutage() throws JsonProcessingException  {
		OutageDetailResponseV2VO objectvo = new OutageDetailResponseV2VO();
		objectvo.setOutageName("remote");
		
		OutageSearchVO outageSearchVO = new OutageSearchVO();
//		objectvo.setId(0);
		outageSearchVO.setTenantid(1);
		//outageSearchVO.setProposeClosure(true);
		outageSearchVO.setIsPlanned(Short.valueOf("1"));
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			List<OutageDetailResponseV2VO> list = outageService.searchOutageV2(outageSearchVO);
			System.out.println("list" + list.size());
			String jsonStr = objectMapper.writeValueAsString(list);
			System.out.println("jsonStr-->"+jsonStr);
		} catch (OutageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		repository.save(user);
		System.out.println("objectvo.getid"+objectvo.getId());
	}
	
	@Ignore
	public void searchOutageExtension()  {
		
		
	OutageExtensionRequestVO extensionRequestVO = new OutageExtensionRequestVO();
//		objectvo.setId(0);
	extensionRequestVO.setTenantid(1);
	
		try {
			List<OutageExtensionRequestVO> list = outageService.getOutageExtensionRequest(extensionRequestVO);
			System.out.println("list" + list.size());
			//objectvo = list.get(0);
			
			
		} catch (OutageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Ignore
	public void saveOutage()  {
		try {
			OutageResponseVO  outageResponseVO = outageService.saveOutageV2(new OutageV2VO("UO_342131913903594078", "Unplanned Outage for RAMAKRISHNA NAGAR ANNEX SSII", "This alert is triggered when a", 1, "TEYNAMPET JE", "883ea738-a93d-11ed-9e43-a3adb571024d", Short.valueOf("1"), false, "UNPLANNED", "distribution_transformer", "distribution_transformer_0400.920806107", "Updated", new Date(), new Date(), "Created", "", "7", ""));
			System.out.println("outageResponseVO-->"+outageResponseVO.getOutageId());
		} catch (OutageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void getOutageCount()  {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			OutageCountVO outageCountVO=new OutageCountVO();
			 List<OutageCountVO> outageCountVOs =dashBoardService.getOutageCount(outageCountVO);
			String jsonStr = objectMapper.writeValueAsString(outageCountVOs);
			System.out.println("dashBoardVOs-->"+jsonStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Ignore
	public void getIndices()  {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			IndicesVO indicesVO=new IndicesVO();
			 List<IndicesVO> indicesVOs =dashBoardService.getIndices(indicesVO);
			String jsonStr = objectMapper.writeValueAsString(indicesVOs);
			System.out.println("dashBoardVOs-->"+jsonStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Ignore
	public void onGoingAffectedCustomer()  {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			OnGoingAffectedCustomerVO onGoingAffectedCustomerVO=new OnGoingAffectedCustomerVO();
			 List<OnGoingAffectedCustomerVO> onGoingAffectedCustomerVOs =dashBoardService.onGoingAffectedCustomer(onGoingAffectedCustomerVO);
			String jsonStr = objectMapper.writeValueAsString(onGoingAffectedCustomerVOs);
			System.out.println("onGoingAffectedCustomerVOs-->"+jsonStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Ignore
	public void getLatestOutageDetails()  {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			LatestOutagesVO latestOutagesVO=new LatestOutagesVO();
			 List<LatestOutagesVO> onGoingAffectedCustomerVOs =dashBoardService.getLatestOutageDetails(latestOutagesVO);
			String jsonStr = objectMapper.writeValueAsString(onGoingAffectedCustomerVOs);
			System.out.println("getLatestOutageDetails-->"+jsonStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Ignore
	public void confirmOutage()  {
		OutageVO objectvo = new OutageVO();
		objectvo.setId(19);
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		outageSearchVO.setOutageid(616);
		//LocalDateTime dateTime = LocalDateTime.parse("2018-05-05T11:50:55");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		Date d = new Date();
		OutageV2VO outageVO = new OutageV2VO();
		try {
			/*String s = "2023-02-13T17:57";
			System.out.println("dats");
			LocalDateTime dateTime = LocalDateTime.parse("2023-01-27T11:23");
			 Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
                     .parse(s);

			System.out.println("TTTT"+date);*/
			outageVO.setOutageId("PO_326720168595716407");
			outageVO.setStatus("Rescheduled");
			outageVO.setIsPlanned(true);
			outageVO.setUserName("jaganTest");
			//outageVO.setApproverRemarks("Test");
			outageVO.setTenantId(1);
			outageVO.setProcessId("sdsdsdfsdf");
			//outageService.approvedOutageV2(outageVO);
			//outageService.confirmOutageV2(outageVO);
			//outageService.rescheduletOutageV2(outageVO);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

	}
	
	@Ignore
	public void approveOutage()  {
		OutageV2VO outageVO = new OutageV2VO();
		outageVO.setId(19);
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		outageSearchVO.setOutageid(19);
		
		try {
			outageVO.setOutageId("PO_322471547285013763");
			outageVO.setStatus("Approved");
			outageVO.setIsPlanned(true);
			outageVO.setUserName("jaganTest");
			//outageVO.setApproverRemarks("Test");
			outageVO.setTenantId(1);
			outageVO.setProcessId("sdsdsdfsdf");
			outageService.approvedOutageV2(outageVO);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

	}
	@Ignore
	public void confirmExtensionRequest()  {
		OutageExtensionRequestVO outageExtensionRequestVO = new OutageExtensionRequestVO();
		
		try {
			outageExtensionRequestVO = outageService.findOutageExtensionRequest(10);
			outageExtensionRequestVO.setStatus("EXTENSION_APPROVED");
			outageService.approveOutageExtensionRequest(outageExtensionRequestVO);
			
		} catch (OutageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Ignore
	public void getOutageExtensionRequest()  {
		OutageExtensionRequestVO outageExtensionRequestVO = new OutageExtensionRequestVO();
		
		try {
			List<OutageExtensionRequestVO> outageExtensionRequestVOs = outageService.getOutageExtensionRequest(outageExtensionRequestVO);
			System.out.println("outageExtensionRequestVOs" + outageExtensionRequestVOs.size());
			outageExtensionRequestVO = outageExtensionRequestVOs.get(0);
			
			
		} catch (OutageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Ignore
	public void searchCrewTask() throws JsonProcessingException  {
		CrewTaskVO crewTaskVO = new CrewTaskVO();
		crewTaskVO.setTenantid(1);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			List<CrewTaskVO> crewTaskVOs = outageService.searchCrewTask(crewTaskVO);
			String jsonStr = objectMapper.writeValueAsString(crewTaskVOs);
			System.out.println("jsonStr-->"+jsonStr);
		} catch (OutageException e) {
			e.printStackTrace();
		}

																																							
	}
	

}
