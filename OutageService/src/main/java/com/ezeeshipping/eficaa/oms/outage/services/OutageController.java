/**
 * 
 */
package com.ezeeshipping.eficaa.oms.outage.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.crypto.Data;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ezeeshipping.eficaa.oms.commons.vo.OutageSearchVO;
import com.ezeeshipping.eficaa.oms.constants.IOmsConstants;
import com.ezeeshipping.eficaa.oms.core.BaseRestController;
import com.ezeeshipping.eficaa.oms.core.logging.AppLogger;
import com.ezeeshipping.eficaa.oms.core.utils.DateUtil;
import com.ezeeshipping.eficaa.oms.core.utils.NumberUtil;
import com.ezeeshipping.eficaa.oms.core.utils.StringUtil;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageDetailResponseV2VO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageNotificationVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageResponseVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageV2VO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageVO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Dell
 *
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/oms/api/outages")
@RestController
public class OutageController extends BaseRestController {
	@Autowired
	private OutageService outageService;
	private static final AppLogger logger = AppLogger.getLogger(OutageRestController.class);

	// @RequestMapping(value = "/oms/api/outages", method = RequestMethod.POST)
	@PostMapping
	public ResponseEntity<OutageResponseVO> saveOutage(@RequestBody String jsonData) throws Exception {
		OutageResponseVO responseVO = new OutageResponseVO();
		OutageV2VO outageV2VO = new OutageV2VO();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			outageV2VO = objectMapper.readValue(jsonData, OutageV2VO.class);
			outageV2VO.setJsonData(jsonData);
			outageV2VO.setStatus("DRAFT");
			if (outageV2VO.getIsPlanned() == false) {
				outageV2VO.setReasonType("UNPLANNED");
				//outageV2VO.setDuration("4:30");
			} else {
				outageV2VO.setReasonType("OTHERS");
			}
			responseVO = outageService.saveOutageV2(outageV2VO);

		} catch (Exception e) {
			logger.error(e);

			throw new Exception(e);
		}

		return new ResponseEntity<OutageResponseVO>(responseVO, HttpStatus.CREATED);
	}

	// @RequestMapping(value = "/oms/api/outages/{outageId}", method =
	// RequestMethod.PUT)
	@PutMapping("/{outageId}")
	public ResponseEntity<OutageResponseVO> updateOutage(@PathVariable("outageId") String outageId,
			@RequestBody String jsonData) throws Exception {
		OutageResponseVO responseVO = null;
		OutageV2VO outageV2VO = new OutageV2VO();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			outageV2VO = objectMapper.readValue(jsonData, OutageV2VO.class);
			outageV2VO.setJsonData(jsonData);
			if (outageV2VO.getIsPlanned() == false) {
				outageV2VO.setReasonType("UNPLANNED");
				outageV2VO.setDuration("4:30");
			} else {
				outageV2VO.setReasonType("OTHERS");
			}
			if (StringUtil.isNotNullOrEmpty(outageV2VO.getStatus())) {
				outageV2VO.setOutageId(outageId);
				if (outageV2VO.getStatus().equalsIgnoreCase(IOmsConstants.CONFIRM_STATUS_BPMN)) {
					outageV2VO.setStatus("CONFIRMED");
					responseVO = outageService.confirmOutageV2(outageV2VO);
				} else if (outageV2VO.getStatus().equalsIgnoreCase(IOmsConstants.APPROVE_STATUS_BPMN)
						|| outageV2VO.getStatus().equalsIgnoreCase(IOmsConstants.CREWASSIGNED_STATUS_BPMN)) {
					responseVO = outageService.approvedOutageV2(outageV2VO);
				} else if (outageV2VO.getStatus().equalsIgnoreCase(IOmsConstants.REJECT_STATUS_BPMN)) {
					responseVO = outageService.rejectOutageV2(outageV2VO);
				} else if (outageV2VO.getStatus().equalsIgnoreCase(IOmsConstants.RESCHEDULE_STATUS_BPMN)) {
					outageV2VO.setStatus("RESCHEDULED");
					responseVO = outageService.rescheduletOutageV2(outageV2VO);
				} else if (outageV2VO.getStatus().equalsIgnoreCase(IOmsConstants.COMPLETE_STATUS_BPMN)) {
					responseVO = outageService.outageCompleteV2(outageV2VO);
				} else if (outageV2VO.getStatus().equalsIgnoreCase(IOmsConstants.CANCELL_STATUS_BPMN)) {
					responseVO = outageService.cancellOutageV2(outageV2VO);
				} else {
					responseVO = outageService.updateOutageV2(outageV2VO);
				}
			} else {
				throw new Exception("Outage Status is Required");
			}

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<OutageResponseVO>(responseVO, HttpStatus.OK);
	}

	/*
	 * @RequestMapping(value = "/oms/api/outages1" , method = RequestMethod.GET)
	 * public ResponseEntity<List<OutageVO>> getOutageAll() throws Exception {
	 * List<OutageVO> outageVOs = new ArrayList<>(); try { outageVOs =
	 * outageService.findAllOutage();
	 * 
	 * } catch (Exception e) { logger.error(e); throw new Exception(e); }
	 * 
	 * return new ResponseEntity<List<OutageVO>>(outageVOs, HttpStatus.OK); }
	 */

	// @RequestMapping(value = "/oms/api/outages/{outageId}", method =
	// RequestMethod.GET)
	@GetMapping("/{outageId}")
	public ResponseEntity<OutageDetailResponseV2VO> findOutage(@PathVariable("outageId") String outageId)
			throws Exception {
		OutageDetailResponseV2VO outageVO = null;
		try {
			outageVO = outageService.findOutageV2(outageId);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<OutageDetailResponseV2VO>(outageVO, HttpStatus.OK);
	}

	// @RequestMapping(value = "/oms/api/outages", method = RequestMethod.GET)
	@GetMapping
	public ResponseEntity<List<OutageDetailResponseV2VO>> searchOutage(@RequestParam Map<String, String> qparams)
			throws Exception {
		List<OutageDetailResponseV2VO> outageVOs = null;
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		try {
			outageSearchVO.setIsPlanned(Short.valueOf("2"));
			if (qparams != null && qparams.size() > 0) {
				qparams.forEach((a, b) -> {
					String key = String.format(a);
					String val = String.format(b);
					logger.debug("val****"+val);
					System.out.println("key****"+key);
					if (StringUtil.isNotNullOrEmpty(a)) {
						if (key.equalsIgnoreCase("outageName")) {
							outageSearchVO.setName(val);
						} else if (key.equalsIgnoreCase("outageId")) {
							outageSearchVO.setOutageId(val);
						} else if (key.equalsIgnoreCase("startDateTimeFrom") && StringUtil.isNotNullOrEmpty(val)) {
							try {
								outageSearchVO
										.setStartDateTimeFrom(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(val));
							} catch (ParseException e) {
								e.printStackTrace();
							}

						} else if (key.equalsIgnoreCase("startDateTimeTo") && StringUtil.isNotNullOrEmpty(val)) {
							try {
								outageSearchVO
										.setStartDateTimeTo(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(val));
							} catch (ParseException e) {
								e.printStackTrace();
							}

						} else if (key.equalsIgnoreCase("endDateTimeFrom") && StringUtil.isNotNullOrEmpty(val)) {
							try {
								outageSearchVO
										.setEndDateTimeFrom(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(val));
							} catch (ParseException e) {
								e.printStackTrace();
							}

						} else if (key.equalsIgnoreCase("endDateTimeTo") && StringUtil.isNotNullOrEmpty(val)) {
							try {
								outageSearchVO.setEndDateTimeTo(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(val));
							} catch (ParseException e) {
								e.printStackTrace();
							}

						} else if (key.equalsIgnoreCase("isPlanned")) {
							if (val.equalsIgnoreCase("true")) {
								outageSearchVO.setIsPlanned(Short.valueOf("1"));
							} else if (val.equalsIgnoreCase("false")) {
								outageSearchVO.setIsPlanned(Short.valueOf("0"));
							} else {
								outageSearchVO.setIsPlanned(Short.valueOf("2"));
							}
						} else if (key.equalsIgnoreCase("status")) {
							outageSearchVO.setStatus(val);
						} else if (key.equalsIgnoreCase("tenantId")) {
							outageSearchVO.setTenantid(Integer.valueOf(val));
						} else if (key.equalsIgnoreCase("substation")) {
							outageSearchVO.setSubstation(Integer.valueOf(val));
						} else if (key.equalsIgnoreCase("division")) {
							outageSearchVO.setDivision(Integer.valueOf(val));
						} else if (key.equalsIgnoreCase("subdivision")) {
							outageSearchVO.setSubdivision(Integer.valueOf(val));
						} else if (key.equalsIgnoreCase("isProposeClosure")) { 
							logger.debug("isProposeClosure****"+val);
							System.out.println("isProposeClosure****"+val);
							if(val.equalsIgnoreCase("true")) {
								System.out.println("inside-->val");
								outageSearchVO.setProposeClosure(true);	
							}else
							{
								outageSearchVO.setProposeClosure(false);	
							}
							
							System.out.println("outageSearchVO.isProposeClosure" + outageSearchVO.isProposeClosure());
						}
					}

				});

			}
			outageVOs = outageService.searchOutageV2(outageSearchVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<List<OutageDetailResponseV2VO>>(outageVOs, HttpStatus.OK);
	}

	// @RequestMapping(value = "/oms/api/outages/sendmailtostaff" , method =
	// RequestMethod.POST)
	@PostMapping("/sendmailtostaff")
	public ResponseEntity<OutageResponseVO> sendMailToStaff(@RequestBody OutageNotificationVO notificationVO)
			throws Exception {
		OutageResponseVO outageResponseVO = null;
		try {
			outageResponseVO = outageService.sendMailToStaff(notificationVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<OutageResponseVO>(outageResponseVO, HttpStatus.OK);
	}

	// @RequestMapping(value = "/oms/api/outages/sendmailtocustomer" , method =
	// RequestMethod.POST)
	@PostMapping("/sendmailtocustomer")
	public ResponseEntity<OutageResponseVO> sendMailToCustomer(@RequestBody OutageNotificationVO notificationVO)
			throws Exception {
		OutageResponseVO outageResponseVO = null;
		try {
			outageResponseVO = outageService.sendMailToCustomer(notificationVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<OutageResponseVO>(outageResponseVO, HttpStatus.OK);
	}

	// @RequestMapping(value = "/oms/api/outages/sendsmstostaff" , method =
	// RequestMethod.POST)
	@PostMapping("/sendsmstostaff")
	public ResponseEntity<OutageResponseVO> sendSmsToStaff(@RequestBody OutageNotificationVO notificationVO)
			throws Exception {
		OutageResponseVO outageResponseVO = null;
		try {
			outageResponseVO = outageService.sendSmsToStaff(notificationVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<OutageResponseVO>(outageResponseVO, HttpStatus.OK);
	}

	// @RequestMapping(value = "/oms/api/outages/sendsmstocustomer" , method =
	// RequestMethod.POST)
	@PostMapping("/sendsmstocustomer")
	public ResponseEntity<OutageResponseVO> sendSmsToCustomer(@RequestBody OutageNotificationVO notificationVO)
			throws Exception {
		OutageResponseVO outageResponseVO = null;
		try {
			outageResponseVO = outageService.sendSmsToCustomer(notificationVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<OutageResponseVO>(outageResponseVO, HttpStatus.OK);
	}

}
