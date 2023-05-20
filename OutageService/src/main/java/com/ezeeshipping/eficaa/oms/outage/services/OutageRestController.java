package com.ezeeshipping.eficaa.oms.outage.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.ezeeshipping.eficaa.oms.admin.vo.TransformerVO;
import com.ezeeshipping.eficaa.oms.commons.vo.OutageSearchVO;
import com.ezeeshipping.eficaa.oms.core.BaseRestController;
import com.ezeeshipping.eficaa.oms.core.logging.AppLogger;
import com.ezeeshipping.eficaa.oms.core.utils.StringUtil;
import com.ezeeshipping.eficaa.oms.outage.vo.CrewTaskVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageDetailResponseV2VO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageExtensionRequestVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageHistoryVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageResponseVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageV2VO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageVO;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class OutageRestController extends BaseRestController {

	private OutageService outageService;
	private static final AppLogger logger = AppLogger.getLogger(OutageRestController.class);

	@Autowired
	public void setOutageService(OutageService outageService) {
		this.outageService = outageService;
	}

//	private final AtomicLong counter = new AtomicLong();

	@PostMapping("/oms/api/createOutage")
	public ResponseEntity<OutageVO> saveOutage(@RequestBody OutageVO outageVO) throws Exception {

		try {
			outageVO = outageService.saveOutage(outageVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<OutageVO>(outageVO, HttpStatus.CREATED);
	}

	@PostMapping("/oms/api/approvedOutage")
	public ResponseEntity<OutageVO> approvedOutage(@RequestBody OutageVO outageVO) throws Exception {

		try {
			outageVO = outageService.approvedOutage(outageVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<OutageVO>(outageVO, HttpStatus.CREATED);
	}

	@PostMapping("/oms/api/searchOutage")
	public ResponseEntity<List<OutageVO>> searchOutage(@RequestBody OutageSearchVO outageSearchVO) throws Exception {
		List<OutageVO> outageVOs = null;
		try {
			outageVOs = outageService.searchOutage(outageSearchVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<List<OutageVO>>(outageVOs, HttpStatus.CREATED);
	}

	@PostMapping("/oms/api/confirmOutage")
	public ResponseEntity<OutageVO> confirmOutage(@RequestBody OutageVO outageVO) throws Exception {

		try {
			outageVO = outageService.confirmOutage(outageVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<OutageVO>(outageVO, HttpStatus.CREATED);
	}

	@PostMapping("/oms/api/rejectOutage")
	public ResponseEntity<OutageVO> rejectOutage(@RequestBody OutageVO outageVO) throws Exception {

		try {
			outageVO = outageService.rejectOutage(outageVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<OutageVO>(outageVO, HttpStatus.CREATED);
	}

	@PostMapping("/oms/api/updatePlannedOutage")
	public ResponseEntity<OutageVO> updatePlannedOutage(@RequestBody OutageSearchVO outageSearchVO) throws Exception {
		OutageVO outageVO = new OutageVO();
		try {
			outageService.outagePlanned(outageSearchVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<OutageVO>(outageVO, HttpStatus.CREATED);
	}

	@PostMapping("/oms/api/workinProgressOutage")
	public ResponseEntity<OutageVO> workinProgressOutage(@RequestBody OutageSearchVO outageSearchVO) throws Exception {
		OutageVO outageVO = new OutageVO();
		try {
			outageService.workinProgressOutage(outageSearchVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<OutageVO>(outageVO, HttpStatus.CREATED);
	}

	@PostMapping("/oms/api/updateCompleteOutage")
	public ResponseEntity<OutageVO> updateCompleteOutage(@RequestBody OutageSearchVO outageSearchVO) throws Exception {
		OutageVO outageVO = new OutageVO();
		try {
			outageService.outageComplete(outageSearchVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<OutageVO>(outageVO, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/oms/api/outageHistorys", method = RequestMethod.GET)
	public ResponseEntity<List<OutageHistoryVO>> searchOutageHistorys(@RequestParam Map<String, String> qparams)
			throws Exception {
		List<OutageHistoryVO> outageHistoryVOs = null;
		OutageHistoryVO outageHistoryVO = new OutageHistoryVO();

		try {
			System.out.println("qparams.size" + qparams.size());
			if (qparams != null && qparams.size() > 0) {
				qparams.forEach((a, b) -> {
					String key = String.format(a);
					String val = String.format(b);

					if (StringUtil.isNotNullOrEmpty(a)) {

						if (key.equalsIgnoreCase("outageid")) {
							outageHistoryVO.setOutageid(Integer.valueOf(val));
						} else if (key.equalsIgnoreCase("tenantid")) {
							outageHistoryVO.setTenantid(Integer.valueOf(val));
						}
					}

				});

			}
			outageHistoryVOs = outageService.searchOutageHistory(outageHistoryVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<List<OutageHistoryVO>>(outageHistoryVOs, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/oms/api/crewtasks", method = RequestMethod.GET)
	public ResponseEntity<List<CrewTaskVO>> searchCrewTask(@RequestParam Map<String, String> qparams) throws Exception {
		List<CrewTaskVO> crewTaskVOs = new ArrayList<CrewTaskVO>();
		CrewTaskVO crewTaskVO = new CrewTaskVO();

		try {
			if (qparams != null && qparams.size() > 0) {
				qparams.forEach((a, b) -> {
					String key = String.format(a);
					String val = String.format(b);
					if (StringUtil.isNotNullOrEmpty(a)) {
						if (key.equalsIgnoreCase("tenantid")) {
							crewTaskVO.setTenantid(Integer.valueOf(val));
						} else if (key.equalsIgnoreCase("id")) {
							crewTaskVO.setId(Integer.valueOf(val));
						}else if (key.equalsIgnoreCase("substation")) {
							crewTaskVO.setSubstation(Integer.valueOf(val));
						} else if (key.equalsIgnoreCase("division")) {
							crewTaskVO.setDivision(Integer.valueOf(val));
						} else if (key.equalsIgnoreCase("subdivision")) {
							crewTaskVO.setSubdivision(Integer.valueOf(val));
						} else if (key.equalsIgnoreCase("crewid")) {
							crewTaskVO.setCrewid(Integer.valueOf(val));
						}
					}
				});
			}

			crewTaskVOs = outageService.searchCrewTask(crewTaskVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<List<CrewTaskVO>>(crewTaskVOs, HttpStatus.CREATED);
	}

	/**
	 * 
	 * @param crewTaskVO
	 * @return
	 * @throws Exception
	 */

	@RequestMapping(value = "/oms/api/crewtasks", method = RequestMethod.POST)
	public ResponseEntity<CrewTaskVO> saveCrewTask(@RequestBody CrewTaskVO crewTaskVO) throws Exception {

		try {
			crewTaskVO = outageService.saveCrewTask(crewTaskVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<CrewTaskVO>(crewTaskVO, HttpStatus.CREATED);
	}

	/**
	 * 
	 * @param crewTaskVO
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/oms/api/completedJobOrder")
	public ResponseEntity<CrewTaskVO> completedCrewTask(@RequestBody CrewTaskVO crewTaskVO) throws Exception {

		try {
			crewTaskVO = outageService.completedCrewTask(crewTaskVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<CrewTaskVO>(crewTaskVO, HttpStatus.CREATED);
	}

	/**
	 * 
	 * @param crewTaskVO
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/oms/api/workinProgressJobOrder")
	public ResponseEntity<CrewTaskVO> workinProgressCrewTask(@RequestBody CrewTaskVO crewTaskVO) throws Exception {

		try {
			crewTaskVO = outageService.workinProgressCrewTask(crewTaskVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<CrewTaskVO>(crewTaskVO, HttpStatus.CREATED);
	}

	/**
	 * 
	 * @param crewTaskVO
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/oms/api/plannedJobOrder")
	public ResponseEntity<CrewTaskVO> plannedCrewTask(@RequestBody CrewTaskVO crewTaskVO) throws Exception {

		try {
			crewTaskVO = outageService.plannedCrewTask(crewTaskVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<CrewTaskVO>(crewTaskVO, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/oms/api/crewtasks/{id}", method = RequestMethod.GET)
	public ResponseEntity<CrewTaskVO> findCrewTask(@PathVariable("id") int id) throws Exception {
		CrewTaskVO crewTaskVO = new CrewTaskVO();
		try {
			crewTaskVO = outageService.findCrewTask(id);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<CrewTaskVO>(crewTaskVO, HttpStatus.OK);
	}

	@RequestMapping(value = "/oms/api/crewtasks/{id}", method = RequestMethod.PUT)
	public ResponseEntity<CrewTaskVO> updateCrewTask(@PathVariable("id") String id, @RequestBody CrewTaskVO crewTaskVO)
			throws Exception {
		try {

			crewTaskVO = outageService.saveCrewTask(crewTaskVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<CrewTaskVO>(crewTaskVO, HttpStatus.OK);
	}

	@RequestMapping(value = "/oms/api/outageExtensionRequest/{id}", method = RequestMethod.GET)
	public ResponseEntity<OutageExtensionRequestVO> findOutageExtensionRequest(@PathVariable("id") int id)
			throws Exception {
		OutageExtensionRequestVO outageExtensionRequestVO = new OutageExtensionRequestVO();
		try {
			outageExtensionRequestVO = outageService.findOutageExtensionRequest(id);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<OutageExtensionRequestVO>(outageExtensionRequestVO, HttpStatus.OK);
	}

	@RequestMapping(value = "/oms/api/outageExtensionRequest", method = RequestMethod.POST)
	public ResponseEntity<OutageExtensionRequestVO> saveOutageExtensionRequest(
			@RequestBody OutageExtensionRequestVO outageExtensionRequestVO) throws Exception {
		try {

			outageExtensionRequestVO = outageService.saveOutageExtensionRequest(outageExtensionRequestVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<OutageExtensionRequestVO>(outageExtensionRequestVO, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/oms/api/outageExtensionRequest", method = RequestMethod.GET)
	public ResponseEntity<List<OutageExtensionRequestVO>> searchOutageExtensionRequests(
			@RequestParam Map<String, String> qparams) throws Exception {
		List<OutageExtensionRequestVO> outageExtensionRequestVOs = null;
		OutageExtensionRequestVO outageExtensionRequestVO = new OutageExtensionRequestVO();

		try {
			System.out.println("qparams.size" + qparams.size());
			if (qparams != null && qparams.size() > 0) {
				qparams.forEach((a, b) -> {
					String key = String.format(a);
					String val = String.format(b);

					if (StringUtil.isNotNullOrEmpty(a)) {
						if (key.equalsIgnoreCase("id")) {
							outageExtensionRequestVO.setId(Integer.valueOf(val));
						} else if (key.equalsIgnoreCase("outageId")) {
							outageExtensionRequestVO.setOutageId(Integer.valueOf(val));
						} else if (key.equalsIgnoreCase("tenantid")) {
							outageExtensionRequestVO.setTenantid(Integer.valueOf(val));
						} else if (key.equalsIgnoreCase("reasonType")) {
							outageExtensionRequestVO.setReasonType(val);
						} else if (key.equalsIgnoreCase("status")) {
							outageExtensionRequestVO.setStatus(val);
						} else if (key.equalsIgnoreCase("taskId")) {
							outageExtensionRequestVO.setTaskId(Integer.valueOf(val));
						} else if (key.equalsIgnoreCase("outageId")) {
							outageExtensionRequestVO.setOutageId(Integer.valueOf(val));
						} else if (key.equalsIgnoreCase("areatype")) {
							outageExtensionRequestVO.setAreatype(val);
						} else if (key.equalsIgnoreCase("areaid")) {
							outageExtensionRequestVO.setAreaid(Integer.valueOf(val));
						}
					}

				});

			}
			outageExtensionRequestVOs = outageService.getOutageExtensionRequest(outageExtensionRequestVO);

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<List<OutageExtensionRequestVO>>(outageExtensionRequestVOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/oms/api/outageExtensionRequest/{id}", method = RequestMethod.PUT)
	public ResponseEntity<OutageExtensionRequestVO> updateOutageExtensionRequest(@PathVariable("id") String id,
			@RequestBody OutageExtensionRequestVO outageExtensionRequestVO) throws Exception {
		try {
			if (outageExtensionRequestVO.getStatus().equalsIgnoreCase("Approved")) {
				outageExtensionRequestVO.setStatus("EXTENSION_APPROVED");
				outageExtensionRequestVO = outageService.approveOutageExtensionRequest(outageExtensionRequestVO);
			} else if (outageExtensionRequestVO.getStatus().equalsIgnoreCase("Rejected")) {
				outageExtensionRequestVO.setStatus("EXTENSION_REJECTED");
				outageExtensionRequestVO = outageService.rejectOutageExtensionRequest(outageExtensionRequestVO);
			} else {
				outageExtensionRequestVO = outageService.saveOutageExtensionRequest(outageExtensionRequestVO);
			}

		} catch (Exception e) {
			logger.error(e);
			throw new Exception(e);
		}

		return new ResponseEntity<OutageExtensionRequestVO>(outageExtensionRequestVO, HttpStatus.OK);
	}
}
