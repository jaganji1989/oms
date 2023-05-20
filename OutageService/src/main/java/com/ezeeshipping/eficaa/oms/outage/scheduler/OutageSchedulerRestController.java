/**
 * 
 */
package com.ezeeshipping.eficaa.oms.outage.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ezeeshipping.eficaa.oms.constants.IOmsConstants;
import com.ezeeshipping.eficaa.oms.core.logging.AppLogger;
import com.ezeeshipping.eficaa.oms.outage.services.OutageService;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageResponseVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageV2VO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Dell
 *
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/oms/api/outages/notifications")
@RestController
public class OutageSchedulerRestController {
	@Autowired
	private OutageService outageService;
	private static final AppLogger logger = AppLogger.getLogger(OutageSchedulerRestController.class);
	
	
	@PostMapping("/confirmoutagemail")
	public ResponseEntity<Boolean> confirmOutageMailScheduler(@RequestBody String jsonData) throws Exception {
		
		try {
				outageService.confirmOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_MAIL);	

		} catch (Exception e) {
			logger.error(e);

			throw new Exception(e);
		}

		return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);
	}
	@PostMapping("/confirmoutagesms")
	public ResponseEntity<Boolean> confirmOutageSmsScheduler(@RequestBody String jsonData) throws Exception {
		
		try {
			Boolean  b =outageService.confirmOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_SMS);

		} catch (Exception e) {
			logger.error(e);

			throw new Exception(e);
		}

		return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);
	}
	@PostMapping("/approveoutagesms")
	public ResponseEntity<Boolean> approveOutageSmsScheduler(@RequestBody String jsonData) throws Exception {
		
		try {
			
			Boolean  b =	outageService.approveOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_SMS);
		} catch (Exception e) {
			logger.error(e);

			throw new Exception(e);
		}

		return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);
	}
	@PostMapping("/approveoutagemail")
	public ResponseEntity<Boolean> approveOutageMailScheduler(@RequestBody String jsonData) throws Exception {
		
		try {
			
			Boolean  b =	outageService.approveOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_MAIL);
		} catch (Exception e) {
			logger.error(e);

			throw new Exception(e);
		}

		return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);
	}
	@PostMapping("/rejectOutageMail")
	public ResponseEntity<Boolean> rejectOutageMailScheduler(@RequestBody String jsonData) throws Exception {
		
		try {
			Boolean  b =outageService.rejectOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_MAIL);

		} catch (Exception e) {
			logger.error(e);

			throw new Exception(e);
		}

		return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);
	}
	@PostMapping("/rejectOutageSms")
	public ResponseEntity<Boolean> rejectOutageSmsScheduler(@RequestBody String jsonData) throws Exception {
		
		try {
			Boolean  b =	outageService.rejectOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_SMS);

		} catch (Exception e) {
			logger.error(e);

			throw new Exception(e);
		}

		return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);
	}
	@PostMapping("/rescheduleOutageSms")
	public ResponseEntity<Boolean> rescheduleOutageSmsScheduler(@RequestBody String jsonData) throws Exception {
		
		try {
			Boolean  b =	outageService.rescheduleOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_SMS);

		} catch (Exception e) {
			logger.error(e);

			throw new Exception(e);
		}

		return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);
	}
	@PostMapping("/rescheduleOutageMail")
	public ResponseEntity<Boolean> rescheduleOutageMailScheduler(@RequestBody String jsonData) throws Exception {
		
		try {
			Boolean  b =	outageService.rescheduleOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_MAIL);

		} catch (Exception e) {
			logger.error(e);

			throw new Exception(e);
		}

		return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);
	}
	@PostMapping("/completeOutageMail")
	public ResponseEntity<Boolean> completeOutageMailScheduler(@RequestBody String jsonData) throws Exception {
		
		try {
			
			Boolean  b =	outageService.completeOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_MAIL);
		} catch (Exception e) {
			logger.error(e);

			throw new Exception(e);
		}

		return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);
	}
	@PostMapping("/completeOutageSms")
	public ResponseEntity<Boolean> completeOutageSmsScheduler(@RequestBody String jsonData) throws Exception {
		
		try {
			Boolean  b =	outageService.completeOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_SMS);

		} catch (Exception e) {
			logger.error(e);

			throw new Exception(e);
		}

		return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);
	}
	@PostMapping("/crewPlannedMail")
	public ResponseEntity<Boolean> crewPlannedMailScheduler(@RequestBody String jsonData) throws Exception {
		
		try {
			Boolean  b =	outageService.crewPlannedScheduler(IOmsConstants.NOTIFICATION_TYPE_MAIL);

		} catch (Exception e) {
			logger.error(e);

			throw new Exception(e);
		}

		return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);
	}
	@PostMapping("/crewPlannedSms")
	public ResponseEntity<Boolean> crewPlannedSmsScheduler(@RequestBody String jsonData) throws Exception {
		
		try {
			Boolean  b =	outageService.crewPlannedScheduler(IOmsConstants.NOTIFICATION_TYPE_SMS);

		} catch (Exception e) {
			logger.error(e);

			throw new Exception(e);
		}

		return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);
	}
}
