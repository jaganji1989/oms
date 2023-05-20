package com.ezeeshipping.eficaa.oms.outage.services;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.tomcat.jni.File;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ezeeshipping.eficaa.oms.admin.model.Crew;
import com.ezeeshipping.eficaa.oms.admin.model.CrewMember;
import com.ezeeshipping.eficaa.oms.admin.model.Role;
import com.ezeeshipping.eficaa.oms.admin.vo.CrewMemberVO;
import com.ezeeshipping.eficaa.oms.admin.vo.CrewVO;
import com.ezeeshipping.eficaa.oms.common.util.HTTPClientUtil;
import com.ezeeshipping.eficaa.oms.common.util.SMSManager;
import com.ezeeshipping.eficaa.oms.commons.vo.ApiAuditVO;
import com.ezeeshipping.eficaa.oms.commons.vo.EmailVO;
import com.ezeeshipping.eficaa.oms.commons.vo.OutageSearchVO;
import com.ezeeshipping.eficaa.oms.commons.vo.SMSVO;
import com.ezeeshipping.eficaa.oms.commons.vo.TransformerDetailResponseVO;
import com.ezeeshipping.eficaa.oms.commons.vo.TransformerResponseVO;
import com.ezeeshipping.eficaa.oms.constants.ApiPortConstant;
import com.ezeeshipping.eficaa.oms.constants.IOmsConstants;
import com.ezeeshipping.eficaa.oms.constants.IReportConstants;
import com.ezeeshipping.eficaa.oms.constants.SMSTemplates;
import com.ezeeshipping.eficaa.oms.core.BaseServiceImpl;
import com.ezeeshipping.eficaa.oms.core.logging.AppLogger;
import com.ezeeshipping.eficaa.oms.core.utils.DateUtil;
import com.ezeeshipping.eficaa.oms.core.utils.NumberUtil;
import com.ezeeshipping.eficaa.oms.core.utils.StringUtil;
import com.ezeeshipping.eficaa.oms.outage.model.CrewTask;
import com.ezeeshipping.eficaa.oms.outage.model.Outage;
import com.ezeeshipping.eficaa.oms.outage.model.OutageExtensionRequest;
import com.ezeeshipping.eficaa.oms.outage.model.OutageHistory;
import com.ezeeshipping.eficaa.oms.outage.model.OutageV2;
import com.ezeeshipping.eficaa.oms.outage.repository.TaskRepository;
import com.ezeeshipping.eficaa.oms.outage.repository.OutageExtensionRepository;
import com.ezeeshipping.eficaa.oms.outage.repository.OutageHistoryRepository;
import com.ezeeshipping.eficaa.oms.outage.repository.OutageRepository;
import com.ezeeshipping.eficaa.oms.outage.repository.OutageRepositoryV2;
import com.ezeeshipping.eficaa.oms.outage.vo.CrewTaskVO;
import com.ezeeshipping.eficaa.oms.outage.vo.DashBoardVO;
import com.ezeeshipping.eficaa.oms.outage.vo.IndicesVO;
import com.ezeeshipping.eficaa.oms.outage.vo.LatestOutagesVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OnGoingAffectedCustomerVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageCountVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageDetailResponseV2VO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageExtensionRequestVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageHistoryVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageLiveGraphVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageNotificationVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageResponseVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageV2VO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageVO;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional(rollbackOn = { Exception.class })
public class OutageServiceImpl extends BaseServiceImpl implements OutageService {

	private OutageDao outageDao;
	private OutageServiceHelper outageServiceHelper;
	private OutageRepository outageRepository;
	private OutageRepositoryV2 outageRepositoryV2;
	private OutageHistoryRepository outageHistoryRepository;
	private TaskRepository taskRepository;
	private OutageExtensionRepository outageExtensionRepository;

	@Autowired
	EntityManagerFactory emf;

	private static final AppLogger logger = AppLogger.getLogger(OutageServiceImpl.class);

	@Autowired
	public OutageServiceImpl(OutageDao outageDao, OutageExtensionRepository outageExtensionRepository,
			OutageServiceHelper outageServiceHelper, OutageRepository outageRepository,
			OutageRepositoryV2 outageRepositoryV2, OutageHistoryRepository outageHistoryRepository,
			TaskRepository taskRepository) {

		this.outageExtensionRepository = outageExtensionRepository;
		this.outageDao = outageDao;
		this.outageServiceHelper = outageServiceHelper;
		this.outageHistoryRepository = outageHistoryRepository;
		this.outageRepository = outageRepository;
		this.outageRepositoryV2 = outageRepositoryV2;
		this.taskRepository = taskRepository;
//2ad5rrvs		this.userLoginAuditRepository = userLoginAuditRepository;
	}

	public List<OutageVO> searchOutage(OutageSearchVO outageSearchVO) throws OutageException {
		Outage object = new Outage();
		List<OutageVO> objectVOs = new ArrayList<>();
		List<Outage> objects = new ArrayList<>();
		OutageVO objectvo = null;
		try {
			logger.debug("searchOutage(OutageVO outageSearchVO) - start : ", outageSearchVO);
			objects = outageDao.searchOutage(outageSearchVO);
			if (objects != null && objects.size() > 0) {
				for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
					Outage object2 = (Outage) iterator.next();
					objectvo = new OutageVO();
					BeanUtils.copyProperties(object2, objectvo);
					if (object2.getIsPlanned() == 1) {
						objectvo.setIsPlanned(true);
					} else if (object2.getIsPlanned() == 0) {
						objectvo.setIsPlanned(false);
					}
					objectVOs.add(objectvo);
				}
			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("searchOutage(OutageVO outageVO) - end : ", objectVOs);
		return objectVOs;
	}

	public OutageVO saveOutage(OutageVO outageVO) throws OutageException {
		try {

			logger.debug("saveOutage(OutageVO outageVO) - start : ", outageVO);
			Outage outage = outageServiceHelper.copyDataFromOutageVOToUserModel(outageVO);
			if (!NumberUtil.isNotNullOrZero(outageVO.getId())) {
				outage.setStatus(IOmsConstants.DRAFT_STATUS_DOMAIN);
				// outage.setCreatedby(outageVO.getCreatedby());
				// outage.setCreateddate(new date);
				OutageHistoryVO outageHistoryVO = new OutageHistoryVO();
				outageHistoryVO.setDescription("Updated By " + outageVO.getUserName());
				outageHistoryVO.setTenantid(outageVO.getTenantId());
				outageHistoryVO.setCreateddate(new Date());
				outageHistoryVO.setOutageid(outageVO.getId());
				outageHistoryVO = saveOutageHistory(outageHistoryVO);
			} else {
				outage = outageRepository.save(outage);
				outageVO = outageServiceHelper.copyDataFromModelToVO(outage);
				OutageHistoryVO outageHistoryVO = new OutageHistoryVO();
				outageHistoryVO.setDescription("Created By " + outageVO.getUserName());
				outageHistoryVO.setTenantid(outageVO.getTenantId());
				outageHistoryVO.setCreateddate(new Date());
				outageHistoryVO.setOutageid(outageVO.getId());
				outageHistoryVO = saveOutageHistory(outageHistoryVO);
			}

		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		logger.debug("saveOutage(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	@Override
	public OutageVO confirmOutage(OutageVO outageVO) throws OutageException {
		try {
			logger.debug("confirmOutage(OutageVO outageVO) - start : ", outageVO);
			outageVO.setStatus(IOmsConstants.CONFIRM_STATUS_DOMAIN);
			try {
				isCriticalOutageCheck(outageVO);
			} catch (Exception e) {
				logger.error(e);
				// TODO: handle exception
				throw new OutageException(e);
			}
			Outage outage = outageServiceHelper.copyDataFromOutageVOToUserModel(outageVO);
			outage = outageRepository.save(outage);
			outageVO = outageServiceHelper.copyDataFromModelToVO(outage);
			OutageHistoryVO outageHistoryVO = new OutageHistoryVO();
			outageHistoryVO.setDescription("Confirmed By " + outageVO.getUserName());
			outageHistoryVO.setTenantid(outageVO.getTenantId());
			outageHistoryVO.setCreateddate(new Date());
			outageHistoryVO.setOutageid(outageVO.getId());
			outageHistoryVO = saveOutageHistory(outageHistoryVO);
			try {
				confirmOutagesendSms(outageVO);

			} catch (Exception e) {

			}
			try {
				confirmOutagesendEmails(outageVO);

			} catch (Exception e) {

			}

		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		logger.debug("confirmOutage(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public String getValidationSaveUpdate(OutageV2VO outageVO) throws OutageException {
		String error = "";
		try {
			if (!StringUtil.isNotNullOrEmpty(outageVO.getOutageId())) {
				if (!StringUtil.isNullOrEmpty(outageVO.getOutageId())) {
					error += "OutageId,";
				}

			}
			if (!NumberUtil.isNotNullOrZero(outageVO.getTenantId())) {
				error += "TenantId,";
			}
			/*
			 * if(!StringUtil.isNotNullOrEmpty(outageVO.getAction())) { error +="Action,"; }
			 */
			if (!StringUtil.isNotNullOrEmpty(outageVO.getNetworkElementType())) {
				error += "NetWorkElemenType,";
			}
			if (!StringUtil.isNotNullOrEmpty(outageVO.getNetworkElementUID())) {
				error += "networkElementUID,";
			}
			if (!StringUtil.isNotNullOrEmpty(outageVO.getOutageName())) {
				error += "OutageName,";
			}
			if (!StringUtil.isNotNullOrEmpty(outageVO.getStatus())) {
				error += "Status,";
			}
			if (!StringUtil.isNotNullOrEmpty(outageVO.getProcessId())) {
				error += "ProcessId,";
			}

			if (outageVO.getIsPlanned() == true || outageVO.getIsPlanned() == false) {

			} else {
				error += "IsPlanned is Invalid Data";
			}

			if (!StringUtil.isNotNullOrEmpty(outageVO.getUserName())) {
				error += "UserName,";
			}

			if (!NumberUtil.isNotNull(outageVO.getSourceSystem())) {
				error += "SourceSystem,";
			}
			if (outageVO.getOutageStartTime() == null) {
				error += "OutageStartTime,";
			}
			if (!StringUtil.isNotNullOrEmpty(outageVO.getOutageName())) {
				error += "OutageName,";
			}
			if (!StringUtil.isNotNullOrEmpty(outageVO.getReasonType())) {
				error += "ReasonType,";
			}
		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		return error;
	}

	public void getFieldDataValidation(OutageV2VO outageVO) throws OutageException {

		try {

			if (StringUtil.isNotNullOrEmpty(outageVO.getOutageId())) {
				if (outageVO.getOutageId().length() < 3) {
					throw new OutageException("OutageId should have at least 3 characters");
				}
				if (outageVO.getOutageId().length() > 100) {
					throw new OutageException("OutageId maximum 100 characters only allowed");
				}
			}

			if (StringUtil.isNotNullOrEmpty(outageVO.getOutageName())) {

				if (outageVO.getOutageName().length() > 500) {
					throw new OutageException("OutageName maximum 500 characters only allowed");
				}
			}
			if (StringUtil.isNotNullOrEmpty(outageVO.getUserName())) {

				if (outageVO.getUserName().length() > 250) {
					throw new OutageException("UserName maximum 250 characters only allowed");
				}
			}
			if (StringUtil.isNotNullOrEmpty(outageVO.getReason())) {

				if (outageVO.getReason().length() > 2000) {
					throw new OutageException("Reason maximum 2000 characters only allowed");
				}
			}
			if (StringUtil.isNotNullOrEmpty(outageVO.getReasonType())) {

				if (outageVO.getReasonType().length() > 250) {
					throw new OutageException("ReasonType maximum 250 characters only allowed");
				}
			}
			if (StringUtil.isNotNullOrEmpty(outageVO.getProcessId())) {

				if (outageVO.getProcessId().length() > 250) {
					throw new OutageException("ProcessId maximum 250 characters only allowed");
				}
			}
			if (StringUtil.isNotNullOrEmpty(outageVO.getNetworkElementType())) {

				if (outageVO.getNetworkElementType().length() > 250) {
					throw new OutageException("NetworkElementType maximum 250 characters only allowed");
				}
			}
			if (StringUtil.isNotNullOrEmpty(outageVO.getNetworkElementUID())) {

				if (outageVO.getNetworkElementUID().length() > 250) {
					throw new OutageException("NetworkElementUID maximum 250 characters only allowed");
				}
			}
			if (StringUtil.isNotNullOrEmpty(outageVO.getApproverremarks())) {

				if (outageVO.getApproverremarks().length() > 250) {
					throw new OutageException("ApproverRemarks maximum 250 characters only allowed");
				}
			}
			if (outageVO.getIsPlanned() == true || outageVO.getIsPlanned() == false) {

			} else {

				throw new OutageException("IsPlanned is Invalid Data");
			}

			if (!NumberUtil.isNotNull(outageVO.getSourceSystem())) {

			}
			if (outageVO.getOutageStartTime() != null && outageVO.getOutageEndTime() != null) {
				if (outageVO.getOutageEndTime().before(outageVO.getOutageStartTime())) {
					throw new OutageException("OutageEndTime is less than OutageStartTime");
				}
			}
			if (!StringUtil.isNotNullOrEmpty(outageVO.getOutageName())) {

			}
			if (!StringUtil.isNotNullOrEmpty(outageVO.getReasonType())) {

			}
		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}

	}

	public String getValidation(OutageV2VO outageVO) throws OutageException {
		String error = "";
		try {
			if (!StringUtil.isNotNullOrEmpty(outageVO.getOutageId())) {
				error += "OutageId,";
			}
			if (!NumberUtil.isNotNullOrZero(outageVO.getTenantId())) {
				error += "TenantId,";
			}

			if (!StringUtil.isNotNullOrEmpty(outageVO.getStatus())) {
				error += "Status,";
			}
			if (!StringUtil.isNotNullOrEmpty(outageVO.getProcessId())) {
				error += "ProcessId,";
			}

			/*
			 * if(!NumberUtil.isNotNull(outageVO.getUserId())) { error +="UserId,"; }
			 */
			if (outageVO.getIsPlanned() == true || outageVO.getIsPlanned() == false) {

			} else {
				error += "IsPlanned is Invalid Data";
			}
			if (!StringUtil.isNotNullOrEmpty(outageVO.getUserName())) {
				error += "UserName,";
			}

			if (!StringUtil.isNotNullOrEmpty(outageVO.getStatus())) {
				error += "Status,";
			} else {
				if (outageVO.getStatus().equalsIgnoreCase("Approve")) {
					if (!StringUtil.isNotNullOrEmpty(outageVO.getApproverremarks())) {
						error += "ApproveRemarks,";
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		return error;
	}

	public Boolean isCriticalOutageCheck(OutageVO outageVO) throws OutageException {
		Boolean isvalid = false;
		ObjectMapper objectMapper = new ObjectMapper();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		try {

			if (outageVO.getStartDateTime() != null) {
				long dateBeforeInMs = new Date().getTime();
				long dateAfterInMs = outageVO.getStartDateTime().getTime();

				long timeDiff = Math.abs(dateAfterInMs - dateBeforeInMs);

				long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);

				// System.out.println("daysDiff"+daysDiff);

				if (daysDiff < 10) {
					// System.out.println("daysDiff####"+daysDiff);
					BeanUtils.copyProperties(outageVO, outageSearchVO);
					outageSearchVO.setIscritical("Y");
					String jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.GET_CONNECTIONINFO);
					List<OutageSearchVO> details = objectMapper.readValue(jsondata,
							objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
					String connections = "";
					;
					if (details != null && details.size() > 0) {
						for (Iterator iterator = details.iterator(); iterator.hasNext();) {
							OutageSearchVO outageSearchVO2 = (OutageSearchVO) iterator.next();
							connections += outageSearchVO2.getCustomerslno() + ",";
						}
						String errormsg = outageServiceHelper.getReplaceCriticalErrorMsg(outageVO, connections);
						isvalid = true;
						throw new OutageException(errormsg);
					}
				} else {
					isvalid = false;
				}
			} else {
				isvalid = true;
				throw new OutageException("OUTAGE START AND END DATE IS INVALID");
			}

		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		return isvalid;
	}

	@Override
	public OutageVO approvedOutage(OutageVO outageVO) throws OutageException {
		try {
			logger.debug("approvedOutage(OutageVO outageVO) - start : ", outageVO);
			outageVO.setStatus(IOmsConstants.APPROVE_STATUS_DOMAIN);
			outageVO.setApprovedDate(new Date());
			Outage outage = outageServiceHelper.copyDataFromOutageVOToUserModel(outageVO);
			outage = outageRepository.save(outage);
			outageVO = outageServiceHelper.copyDataFromModelToVO(outage);
			OutageHistoryVO outageHistoryVO = new OutageHistoryVO();
			outageHistoryVO.setDescription("Approved By " + outageVO.getUserName());
			outageHistoryVO.setTenantid(outageVO.getTenantId());
			outageHistoryVO.setCreateddate(new Date());
			outageHistoryVO.setOutageid(outageVO.getId());
			outageHistoryVO = saveOutageHistory(outageHistoryVO);
			// System.out.println("$$$$$"+outageVO.getId());
			try {
				approvedOutagesendSms(outageVO);

			} catch (Exception e) {

			}
			try {
				approveOutagesendEmails(outageVO);
			} catch (Exception e) {

			}

		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		logger.debug("approvedOutage(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	@Override
	public OutageVO rejectOutage(OutageVO outageVO) throws OutageException {
		try {
			logger.debug("rejectOutage(OutageVO outageVO) - start : ", outageVO);
			outageVO.setStatus(IOmsConstants.REJECT_STATUS_DOMAIN);
			Outage outage = outageServiceHelper.copyDataFromOutageVOToUserModel(outageVO);
			outage = outageRepository.save(outage);
			outageVO = outageServiceHelper.copyDataFromModelToVO(outage);
			OutageHistoryVO outageHistoryVO = new OutageHistoryVO();
			outageHistoryVO.setDescription("Rejected By " + outageVO.getUserName());
			outageHistoryVO.setTenantid(outageVO.getTenantId());
			outageHistoryVO.setCreateddate(new Date());
			outageHistoryVO.setOutageid(outageVO.getId());
			outageHistoryVO = saveOutageHistory(outageHistoryVO);
			try {
				rejectOutagesendSms(outageVO);

			} catch (Exception e) {

			}
			try {
				rejectOutagesendEmails(outageVO);
			} catch (Exception e) {

			}

		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		logger.debug("rejectOutage(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public OutageVO rejectOutagesendEmails(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<EmailVO> emailVOs = new ArrayList<>();
		try {
			logger.debug("rejectOutagesendEmails(OutageVO outageVO) - start : ", outageVO);
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			// STAFF

			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.OUTAGE_REJECT);
			jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.AE_JE_DETAILS);
			List<OutageSearchVO> details = objectMapper.readValue(jsondata,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			if (details != null && details.size() > 0) {
//				emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details,outageVO,IReportConstants.OUTAGE_REJECT);
				outageSearchVO = new OutageSearchVO();
				if (outageVO.isRescheduled() == true) {
					outageSearchVO.setDisplayname(IReportConstants.OUTAGE_RESCHEDULE_REJECTION);
				} else {
					outageSearchVO.setDisplayname(IReportConstants.OUTAGE_REJECT);
				}

				outageSearchVO.setTenantid(outageVO.getTenantId());
				jsondata = httpClientUtil.postRequest(outageSearchVO,
						ApiPortConstant.GET_EMAIL_TEMPLATE_FROM_ADMIN_SERVICE);

				OutageSearchVO outageSearchVOEmail = objectMapper.readValue(jsondata, OutageSearchVO.class);
				if (StringUtil.isNotNullOrEmpty(outageSearchVOEmail.getMessage())) {
					emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details, outageVO,
							IReportConstants.OUTAGE_REJECT, outageSearchVOEmail.getSubject(),
							outageSearchVOEmail.getMessage());
					httpClientUtil = new HTTPClientUtil();
					jsondata = httpClientUtil.postRequest(emailVOs, ApiPortConstant.SENDMAIL);
				} else {
					throw new Exception("Email Template Configuration Missing " + IReportConstants.OUTAGE_REJECT);
				}

				httpClientUtil = new HTTPClientUtil();
				jsondata = httpClientUtil.postRequest(emailVOs, ApiPortConstant.SENDMAIL);

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("rejectOutagesendEmails(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	@Override
	public OutageVO outagePlanned(OutageSearchVO outageSearchVO) throws OutageException {
		OutageVO outageVO = new OutageVO();
		try {
			logger.debug("outagePlanned(OutageSearchVO outageSearchVO) - start : ", outageSearchVO);
			Outage outage = outageRepository.findById(outageSearchVO.getOutageid());
			// outage.setStatus("PLANNED");
			// logger.debug("outageoutageoutage.getid" + outage.getId());
			// outage = outageRepository.save(outage);
			outageVO = outageServiceHelper.copyDataFromModelToVO(outage);
			OutageHistoryVO outageHistoryVO = new OutageHistoryVO();
			outageHistoryVO.setDescription("Planned By " + outageVO.getUserName());
			outageHistoryVO.setTenantid(outageVO.getTenantId());
			outageHistoryVO.setCreateddate(new Date());
			outageHistoryVO.setOutageid(outageVO.getId());
			outageHistoryVO = saveOutageHistory(outageHistoryVO);
			try {
				plannedOutageCrewssendEmails(outageVO, outageSearchVO);
			} catch (Exception e) {
				// TODO: handle exception
			}
		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("outagePlanned(OutageSearchVO outageSearchVO) - end : ", outageSearchVO);
		return outageVO;
	}

	@Override
	public OutageVO outageComplete(OutageSearchVO outageSearchVO) throws OutageException {
		OutageVO outageVO = new OutageVO();
		try {
			logger.debug("outageComplete(OutageSearchVO outageSearchVO) - start : ", outageSearchVO);
			Outage outage = outageRepository.findById(outageSearchVO.getOutageid());
			outage.setStatus(IOmsConstants.COMPLETE_STATUS_DOMAIN);
			outage = outageRepository.save(outage);
			outageVO = outageServiceHelper.copyDataFromModelToVO(outage);
			saveOutageHistory(new OutageHistoryVO(outage.getId(), "", "Outage is Completed", outageVO.getTenantId(),
					new Date(), outageVO.getUserName()));

			try {
				completeOutagesendEmails(outageVO);

			} catch (Exception e) {

			}

			try {
				completeOutagesendSms(outageVO);

			} catch (Exception e) {

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("outageComplete(OutageSearchVO outageSearchVO) - end : ", outageSearchVO);
		return outageVO;
	}

	public OutageVO plannedOutageCrewssendEmails(OutageVO outageVO, OutageSearchVO outageSearchVO)
			throws OutageException {

		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		OutageSearchVO searchVO = new OutageSearchVO();
		String jsondata = "";
		List<EmailVO> emailVOs = new ArrayList<>();
		try {
			logger.debug("plannedOutageCrewssendEmails(OutageVO outageVO,OutageSearchVO outageSearchVO) - start : ",
					outageSearchVO);
			BeanUtils.copyProperties(outageVO, searchVO);
			searchVO.setOutagetype(IReportConstants.CREW_TASK_ASSIGNED);
			searchVO.setCrewids(outageSearchVO.getCrewids());
			System.out.println("plannedOutageCrewssendEmails");
			System.out.println("searchVO.setCrewids" + searchVO.getOutagetype());
			System.out.println("searchVO.setOutagetype" + searchVO.getCrewids());
			jsondata = httpClientUtil.postRequest(searchVO, ApiPortConstant.GET_CREWS_DETAILS);
			System.out.println("GET_CREWS_DETAILS jsondata" + jsondata);
			List<OutageSearchVO> details = objectMapper.readValue(jsondata,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			logger.debug("GET_CREWS_DETAILS details.size" + details.size());
			if (details != null && details.size() > 0) {
//				emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details,outageVO,IReportConstants.CREW_TASK_ASSIGNED);
				outageSearchVO = new OutageSearchVO();
				outageSearchVO.setDisplayname(IReportConstants.CREW_TASK_ASSIGNED);
				outageSearchVO.setTenantid(outageVO.getTenantId());
				jsondata = httpClientUtil.postRequest(outageSearchVO,
						ApiPortConstant.GET_EMAIL_TEMPLATE_FROM_ADMIN_SERVICE);

				OutageSearchVO outageSearchVOEmail = objectMapper.readValue(jsondata, OutageSearchVO.class);
				if (StringUtil.isNotNullOrEmpty(outageSearchVOEmail.getMessage())) {
					emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details, outageVO,
							IReportConstants.CREW_TASK_ASSIGNED, outageSearchVOEmail.getSubject(),
							outageSearchVOEmail.getMessage());
					httpClientUtil = new HTTPClientUtil();
					jsondata = httpClientUtil.postRequest(emailVOs, ApiPortConstant.SENDMAIL);
				} else {
					throw new Exception("Email Template Configuration Missing " + IReportConstants.CREW_TASK_ASSIGNED);
				}

				httpClientUtil = new HTTPClientUtil();
				jsondata = httpClientUtil.postRequest(emailVOs, ApiPortConstant.SENDMAIL);

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("plannedOutageCrewssendEmails(OutageVO outageVO,OutageSearchVO outageSearchVO) - end : ",
				outageSearchVO);
		return outageVO;
	}

	public OutageVO confirmOutagesendEmails(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<EmailVO> emailVOs = new ArrayList<>();
		String emailJsonData = "";
		try {
			logger.debug("confirmOutagesendEmails(OutageVO outageVO) - start : ", outageVO);
			System.out.println("confirmOutagesendEmails");
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.OUTAGE_CONFIRM);
			jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.AE_JE_DETAILS);
			System.out.println("confirmOutagesendEmails" + jsondata);
			List<OutageSearchVO> details = objectMapper.readValue(jsondata,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			System.out.println("confirmOutagesendEmails details" + details.size());
			if (details != null && details.size() > 0) {
				try {
					outageSearchVO = new OutageSearchVO();
					outageSearchVO.setDisplayname(IReportConstants.OUTAGE_CONFIRM);
					httpClientUtil = new HTTPClientUtil();
					outageSearchVO.setTenantid(outageVO.getTenantId());
					emailJsonData = httpClientUtil.postRequest(outageSearchVO,
							ApiPortConstant.GET_EMAIL_TEMPLATE_FROM_ADMIN_SERVICE);
					System.out.println("emailJsonData" + emailJsonData);
				} catch (Exception e) {
					// TODO: handle exception
				}

				OutageSearchVO outageSearchVOEmail = objectMapper.readValue(emailJsonData, OutageSearchVO.class);
				System.out.println("outageSearchVOEmail" + outageSearchVOEmail.getMessage());
				if (StringUtil.isNotNullOrEmpty(outageSearchVOEmail.getMessage())) {
					emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details, outageVO,
							IReportConstants.OUTAGE_CONFIRM, outageSearchVOEmail.getSubject(),
							outageSearchVOEmail.getMessage());
					System.out.println("emailVOs.size" + emailVOs.size());
					httpClientUtil = new HTTPClientUtil();
					jsondata = httpClientUtil.postRequest(emailVOs, ApiPortConstant.SENDMAIL);
				} else {
					throw new Exception("Email Template Configuration Missing " + IReportConstants.OUTAGE_CONFIRM);
				}

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("confirmOutagesendEmails(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public OutageVO rescheduleOutagesendEmails(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<EmailVO> emailVOs = new ArrayList<>();
		String emailJsonData = "";
		try {
			logger.debug("rescheduleOutagesendEmails(OutageVO outageVO) - start : ", outageVO);
			System.out.println("confirmOutagesendEmails");
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.RESCHEDULE_OUTAGE);
			jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.AE_JE_DETAILS);
			System.out.println("rescheduleOutagesendEmails" + jsondata);
			List<OutageSearchVO> details = objectMapper.readValue(jsondata,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			System.out.println("rescheduleOutagesendEmails details" + details.size());
			if (details != null && details.size() > 0) {
				try {
					outageSearchVO = new OutageSearchVO();
					outageSearchVO.setDisplayname(IReportConstants.RESCHEDULE_OUTAGE);
					httpClientUtil = new HTTPClientUtil();
					outageSearchVO.setTenantid(outageVO.getTenantId());
					emailJsonData = httpClientUtil.postRequest(outageSearchVO,
							ApiPortConstant.GET_EMAIL_TEMPLATE_FROM_ADMIN_SERVICE);
					System.out.println("emailJsonData" + emailJsonData);
				} catch (Exception e) {
					// TODO: handle exception
				}

				OutageSearchVO outageSearchVOEmail = objectMapper.readValue(emailJsonData, OutageSearchVO.class);
				System.out.println("outageSearchVOEmail" + outageSearchVOEmail.getMessage());
				if (StringUtil.isNotNullOrEmpty(outageSearchVOEmail.getMessage())) {
					emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details, outageVO,
							IReportConstants.RESCHEDULE_OUTAGE, outageSearchVOEmail.getSubject(),
							outageSearchVOEmail.getMessage());
					System.out.println("emailVOs.size" + emailVOs.size());
					httpClientUtil = new HTTPClientUtil();
					jsondata = httpClientUtil.postRequest(emailVOs, ApiPortConstant.SENDMAIL);
				} else {
					throw new Exception("Email Template Configuration Missing " + IReportConstants.OUTAGE_CONFIRM);
				}

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("rescheduleOutagesendEmails(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public OutageVO approveOutagesendEmails(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<EmailVO> emailVOs = new ArrayList<>();
		try {
			logger.debug("approveOutagesendEmails(OutageVO outageVO) - start : ", outageVO);
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.APPROVAL_COMPLETE);
			jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.AE_JE_DETAILS);
			List<OutageSearchVO> details = objectMapper.readValue(jsondata,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			if (details != null && details.size() > 0) {
//				emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details,outageVO,IReportConstants.APPROVAL_COMPLETE);
				// System.out.println("outagesss$$");
				httpClientUtil = new HTTPClientUtil();
				outageSearchVO = new OutageSearchVO();
				outageSearchVO.setDisplayname(IReportConstants.APPROVAL_COMPLETE);
				outageSearchVO.setTenantid(outageVO.getTenantId());
				String emailJsonData = httpClientUtil.postRequest(outageSearchVO,
						ApiPortConstant.GET_EMAIL_TEMPLATE_FROM_ADMIN_SERVICE);

				OutageSearchVO outageSearchVOEmail = objectMapper.readValue(emailJsonData, OutageSearchVO.class);
				if (StringUtil.isNotNullOrEmpty(outageSearchVOEmail.getMessage())) {
					emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details, outageVO,
							IReportConstants.APPROVAL_COMPLETE, outageSearchVOEmail.getSubject(),
							outageSearchVOEmail.getMessage());
					httpClientUtil = new HTTPClientUtil();
					jsondata = httpClientUtil.postRequest(emailVOs, ApiPortConstant.SENDMAIL);
				} else {
					throw new Exception("Email Template Configuration Missing" + IReportConstants.APPROVAL_COMPLETE);
				}

			}

			// CUSTOMER
			// System.out.println("outagesss");
			httpClientUtil = new HTTPClientUtil();
			String jsondataString = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.GET_CONNECTIONINFO);
			details = objectMapper.readValue(jsondataString,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			// System.out.println("jsondataString-->"+jsondataString);
			// System.out.println("details-->"+details.size());
			if (details != null && details.size() > 0) {
//				emailVOs = outageServiceHelper.getConnectionEmailVOforCustomer(details,outageVO,IReportConstants.APPROVAL_COMPLETE);
				httpClientUtil = new HTTPClientUtil();
				outageSearchVO = new OutageSearchVO();
				outageSearchVO.setDisplayname(IReportConstants.APPROVAL_COMPLETE_CUSTOMER);
				outageSearchVO.setTenantid(outageVO.getTenantId());
				String emailJsonData = httpClientUtil.postRequest(outageSearchVO,
						ApiPortConstant.GET_EMAIL_TEMPLATE_FROM_ADMIN_SERVICE);

				OutageSearchVO outageSearchVOEmail = objectMapper.readValue(emailJsonData, OutageSearchVO.class);
				if (StringUtil.isNotNullOrEmpty(outageSearchVOEmail.getMessage())) {
					emailVOs = outageServiceHelper.getConnectionEmailVOforCustomer(details, outageVO,
							IReportConstants.APPROVAL_COMPLETE, outageSearchVOEmail.getSubject(),
							outageSearchVOEmail.getMessage());
					httpClientUtil = new HTTPClientUtil();
					jsondata = httpClientUtil.postRequest(emailVOs, ApiPortConstant.SENDMAIL);
				} else {
					throw new Exception("Email Template Configuration Missing" + IReportConstants.APPROVAL_COMPLETE);
				}

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("approveOutagesendEmails(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public OutageVO completeOutagesendEmails(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<EmailVO> emailVOs = new ArrayList<>();
		try {
			logger.debug("completeOutagesendEmails(OutageVO outageVO) - start : ", outageVO);
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.CREW_TASK_COMPLETED);
			String jsondataString = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.AE_JE_DETAILS);
			List<OutageSearchVO> details = objectMapper.readValue(jsondataString,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			if (details != null && details.size() > 0) {
//				emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details,outageVO,IReportConstants.CREW_TASK_COMPLETED);
				httpClientUtil = new HTTPClientUtil();
				outageSearchVO = new OutageSearchVO();
				outageSearchVO.setDisplayname(IReportConstants.CREW_TASK_COMPLETED);
				outageSearchVO.setTenantid(outageVO.getTenantId());
				String emailJsonData = httpClientUtil.postRequest(outageSearchVO,
						ApiPortConstant.GET_EMAIL_TEMPLATE_FROM_ADMIN_SERVICE);

				OutageSearchVO outageSearchVOEmail = objectMapper.readValue(emailJsonData, OutageSearchVO.class);
				if (StringUtil.isNotNullOrEmpty(outageSearchVOEmail.getMessage())) {
					emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details, outageVO,
							IReportConstants.CREW_TASK_COMPLETED, outageSearchVOEmail.getSubject(),
							outageSearchVOEmail.getMessage());
					httpClientUtil = new HTTPClientUtil();
					jsondata = httpClientUtil.postRequest(emailVOs, ApiPortConstant.SENDMAIL);
				} else {
					throw new Exception("Email Template Configuration Missing " + IReportConstants.CREW_TASK_COMPLETED);
				}

			}

			// CUSTOMER
			// System.out.println("CUSTOMER--jsondata");
			httpClientUtil = new HTTPClientUtil();
			String jsString = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.GET_CONNECTIONINFO);
			// System.out.println("jsString--jsString"+jsString);
			details = objectMapper.readValue(jsString,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			if (details != null && details.size() > 0) {
//				emailVOs = outageServiceHelper.getConnectionEmailVOforCustomer(details,outageVO,IReportConstants.CREW_TASK_COMPLETED);
				httpClientUtil = new HTTPClientUtil();
				outageSearchVO = new OutageSearchVO();
				outageSearchVO.setDisplayname(IReportConstants.CREW_TASK_COMPLETED_CUSTOMER);
				outageSearchVO.setTenantid(outageVO.getTenantId());
				String emailJsonData = httpClientUtil.postRequest(outageSearchVO,
						ApiPortConstant.GET_EMAIL_TEMPLATE_FROM_ADMIN_SERVICE);

				OutageSearchVO outageSearchVOEmail = objectMapper.readValue(emailJsonData,
						objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
				if (StringUtil.isNotNullOrEmpty(outageSearchVOEmail.getMessage())) {
					emailVOs = outageServiceHelper.getConnectionEmailVOforCustomer(details, outageVO,
							IReportConstants.CREW_TASK_COMPLETED, outageSearchVOEmail.getSubject(),
							outageSearchVOEmail.getMessage());
					httpClientUtil = new HTTPClientUtil();
					jsondata = httpClientUtil.postRequest(emailVOs, ApiPortConstant.SENDMAIL);
				} else {
					throw new Exception(
							"Email Template Configuration Missing" + IReportConstants.CREW_TASK_COMPLETED_CUSTOMER);
				}

				// System.out.println("complete--jsondata1"+jsondata1);

			}

		} catch (Exception e) {

		}
		logger.debug("completeOutagesendEmails(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public OutageVO confirmOutagesendSms(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<SMSVO> smsVOs = new ArrayList<>();
		try {

			logger.debug("confirmOutagesendSms(OutageVO outageVO) - start : ", outageVO);
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.OUTAGE_CONFIRM);
			jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.AE_JE_DETAILS);
			List<OutageSearchVO> details = objectMapper.readValue(jsondata,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			// System.out.println("details..sms"+details.size());
			if (details != null && details.size() > 0) {

				smsVOs = outageServiceHelper.getConnectionSMSVOforStaff(details, outageVO,
						IReportConstants.OUTAGE_CONFIRM);
				httpClientUtil = new HTTPClientUtil();
				jsondata = httpClientUtil.postSMSRequest(smsVOs, ApiPortConstant.SENDSMS);

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("confirmOutagesendSms(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public OutageVO rescheduledsendSms(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<SMSVO> smsVOs = new ArrayList<>();
		try {

			logger.debug("confirmOutagesendSms(OutageVO outageVO) - start : ", outageVO);
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.RESCHEDULE_OUTAGE);
			jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.AE_JE_DETAILS);
			List<OutageSearchVO> details = objectMapper.readValue(jsondata,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			// System.out.println("details..sms"+details.size());
			if (details != null && details.size() > 0) {

				smsVOs = outageServiceHelper.getConnectionSMSVOforStaff(details, outageVO,
						IReportConstants.OUTAGE_CONFIRM);
				httpClientUtil = new HTTPClientUtil();
				jsondata = httpClientUtil.postSMSRequest(smsVOs, ApiPortConstant.SENDSMS);

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("confirmOutagesendSms(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public OutageVO completeOutagesendSms(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<SMSVO> smsVOs = new ArrayList<>();
		try {
			logger.debug("completeOutagesendSms(OutageVO outageVO) - start : ", outageVO);
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.CREW_TASK_COMPLETED);
			jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.GET_CONNECTIONINFO);
			List<OutageSearchVO> details = objectMapper.readValue(jsondata,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			// System.out.println("details..sms"+details.size());
			if (details != null && details.size() > 0) {

				smsVOs = outageServiceHelper.getConnectionSMSVOforCustomer(details, outageVO,
						IReportConstants.CREW_TASK_COMPLETED);
				httpClientUtil = new HTTPClientUtil();
				String jsondataString = httpClientUtil.postSMSRequest(smsVOs, ApiPortConstant.SENDSMS);

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("completeOutagesendSms(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public OutageVO approvedOutagesendSms(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<SMSVO> smsVOs = new ArrayList<>();
		try {
			logger.debug("approvedOutagesendSms(OutageVO outageVO) - start : ", outageVO);
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.APPROVAL_COMPLETE);
			jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.AE_JE_DETAILS);
			List<OutageSearchVO> details = objectMapper.readValue(jsondata,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			if (details != null && details.size() > 0) {

				smsVOs = outageServiceHelper.getConnectionSMSVOforStaff(details, outageVO,
						IReportConstants.APPROVAL_COMPLETE);
				httpClientUtil = new HTTPClientUtil();
				jsondata = httpClientUtil.postSMSRequest(smsVOs, ApiPortConstant.SENDSMS);

			}

			// CUSTOMER
			httpClientUtil = new HTTPClientUtil();
			String jsondataString = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.GET_CONNECTIONINFO);
			details = objectMapper.readValue(jsondataString,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			// System.out.println("jsondataString-->"+jsondataString);
			// System.out.println("details-->"+details.size());
			if (details != null && details.size() > 0) {

				smsVOs = outageServiceHelper.getConnectionSMSVOforCustomer(details, outageVO,
						IReportConstants.APPROVAL_COMPLETE);
				httpClientUtil = new HTTPClientUtil();
				jsondata = httpClientUtil.postSMSRequest(smsVOs, ApiPortConstant.SENDSMS);

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("approvedOutagesendSms(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public OutageVO rejectOutagesendSms(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<SMSVO> smsVOs = new ArrayList<>();
		try {
			logger.debug("rejectOutagesendSms(OutageVO outageVO) - start : ", outageVO);
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			// STAFF

			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.OUTAGE_REJECT);
			jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.AE_JE_DETAILS);
			List<OutageSearchVO> details = objectMapper.readValue(jsondata,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			if (details != null && details.size() > 0) {

				smsVOs = outageServiceHelper.getConnectionSMSVOforStaff(details, outageVO,
						IReportConstants.OUTAGE_REJECT);
				httpClientUtil = new HTTPClientUtil();
				jsondata = httpClientUtil.postSMSRequest(smsVOs, ApiPortConstant.SENDSMS);

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("rejectOutagesendSms(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public OutageVO plannedOutagesendSms(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<SMSVO> smsVOs = new ArrayList<>();
		try {
			logger.debug("plannedOutagesendSms(OutageVO outageVO) - start : ", outageVO);
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			// STAFF

			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.OUTAGE_REJECT);
			jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.AE_JE_DETAILS);
			List<OutageSearchVO> details = objectMapper.readValue(jsondata,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			if (details != null && details.size() > 0) {

				smsVOs = outageServiceHelper.getConnectionSMSVOforStaff(details, outageVO,
						IReportConstants.OUTAGE_REJECT);
				httpClientUtil = new HTTPClientUtil();
				jsondata = httpClientUtil.postSMSRequest(smsVOs, ApiPortConstant.SENDSMS);

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("plannedOutagesendSms(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	@Override
	public OutageVO workinProgressOutage(OutageSearchVO outageSearchVO) throws OutageException {
		OutageVO outageVO = new OutageVO();
		try {
			logger.debug("workinProgressOutage(OutageSearchVO outageSearchVO) - start : ", outageSearchVO);
			// System.out.println("workinProgressOutage--"+outageSearchVO.getOutageid());
			if (NumberUtil.isNotNullOrZero(outageSearchVO.getOutageid())) {
				Outage outage = outageRepository.findById(outageSearchVO.getOutageid());
				outageVO = outageServiceHelper.copyDataFromModelToVO(outage);

				try {
					workinProgressOutagesendEmail(outageVO);
				} catch (Exception e) {

				}
			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("workinProgressOutage(OutageSearchVO outageSearchVO) - end : ", outageVO);
		return outageVO;

	}

	public OutageVO workinProgressOutagesendEmail(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<EmailVO> emailVOs = new ArrayList<>();
		try {
			logger.debug("workinProgressOutagesendEmail(OutageVO outageVO) - start : ", outageVO);
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			// STAFF
			outageSearchVO.setOutagetype(IReportConstants.CREW_TASK_STARTED);
			jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.AE_JE_DETAILS);
			// System.out.println("workinProgressOutagesendEmail--jsondata"+jsondata);
			List<OutageSearchVO> details = objectMapper.readValue(jsondata,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			// System.out.println("details"+details.size());
			if (details != null && details.size() > 0) {
				httpClientUtil = new HTTPClientUtil();
//				emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details,outageVO,IReportConstants.CREW_TASK_STARTED);
				outageSearchVO = new OutageSearchVO();
				outageSearchVO.setDisplayname(IReportConstants.CREW_TASK_STARTED);
				outageSearchVO.setTenantid(outageVO.getTenantId());
				String emailJsonData = httpClientUtil.postRequest(outageSearchVO,
						ApiPortConstant.GET_EMAIL_TEMPLATE_FROM_ADMIN_SERVICE);

				OutageSearchVO outageSearchVOEmail = objectMapper.readValue(emailJsonData, OutageSearchVO.class);
				if (StringUtil.isNotNullOrEmpty(outageSearchVOEmail.getMessage())) {
					emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details, outageVO,
							IReportConstants.CREW_TASK_STARTED, outageSearchVOEmail.getSubject(),
							outageSearchVOEmail.getMessage());
					httpClientUtil = new HTTPClientUtil();
					jsondata = httpClientUtil.postRequest(emailVOs, ApiPortConstant.SENDMAIL);
				} else {
					throw new Exception("Email Template Configuration Missing " + IReportConstants.CREW_TASK_STARTED);
				}

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("workinProgressOutagesendEmail(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	@Override
	public List<OutageVO> findAllOutage() throws OutageException {
		List<OutageVO> outageVOs = new ArrayList<>();
		OutageVO objectvo = null;
		try {
			List<Outage> outages = outageRepository.findAll();
			if (outages != null && outages.size() > 0) {
				for (Iterator iterator = outages.iterator(); iterator.hasNext();) {
					Outage object2 = (Outage) iterator.next();
					objectvo = new OutageVO();
					BeanUtils.copyProperties(object2, objectvo);
					outageVOs.add(objectvo);
				}
			}
		} catch (Exception e) {
			;
			logger.error(e);
			throw new OutageException(e);
		}
		return outageVOs;
	}

	@Override
	public OutageVO findOutagev1(int id) throws OutageException {
		OutageVO outageVO = null;
		try {
			Outage outage = outageRepository.findById(id);
			outageVO = outageServiceHelper.copyDataFromModelToVO(outage);
		} catch (Exception e) {
			;
			logger.error(e);
			throw new OutageException(e);
		}
		return outageVO;
	}

	@Override
	public OutageDetailResponseV2VO findOutageV2(String outageId) throws OutageException {
		OutageDetailResponseV2VO outageV2VO = null;
		OutageV2 outage = null;
		try {
			List<OutageV2> outagelist = outageRepositoryV2.fetchOutagesByOutageId(outageId);
			if (outagelist != null && outagelist.size() > 0) {
				if (outagelist.size() > 1) {
					throw new OutageException("Duplicate OutageId Found");
				} else {
					for (OutageV2 outageV2 : outagelist) {
						outage = outageV2;
					}
				}
			}
			outageV2VO = outageServiceHelper.copyDataFromModelToVO(outage);
			outageV2VO.setApproverremarks(outage.getApproverRemarks());
			try {
				CrewTaskVO crewTaskVO = new CrewTaskVO();
				crewTaskVO.setOutageid(outage.getId());
				List<CrewTaskVO> crewTaskVOs = searchCrewTask(crewTaskVO);
				if (crewTaskVOs != null && crewTaskVOs.size() > 0) {
					outageV2VO.getCrewTaskVOs().addAll(crewTaskVOs);
				}
			} catch (Exception e) {
				logger.error(e);
			}
		} catch (Exception e) {
			;
			logger.error(e);
			throw new OutageException(e);
		}
		return outageV2VO;
	}

	public List<OutageDetailResponseV2VO> searchOutageV2(OutageSearchVO outageSearchVO) throws OutageException {
		OutageDetailResponseV2VO outageV2 = new OutageDetailResponseV2VO();
		List<OutageDetailResponseV2VO> outageV2VOs = new ArrayList<>();
		List<OutageV2> outageV2s = new ArrayList<>();
		List<OutageV2> v2s = new ArrayList<>();
		OutageDetailResponseV2VO outageV2VO = null;
		try {
			logger.debug("searchOutage(OutageSearchVO outageSearchVO) - start : ", outageSearchVO);
			System.out.println("outageSearchVO" + outageSearchVO.getIsPlanned());
			outageV2s = outageDao.searchOutageV2ByQuery(outageSearchVO);

			if (outageV2s != null && outageV2s.size() > 0) {
				for (Iterator iterator = outageV2s.iterator(); iterator.hasNext();) {
					OutageV2 outageV22 = (OutageV2) iterator.next();
					outageV2VO = new OutageDetailResponseV2VO();
					BeanUtils.copyProperties(outageV22, outageV2VO);
					if (outageV22.getIsPlanned() == 1) {
						outageV2VO.setIsPlanned(true);
					} else if (outageV22.getIsPlanned() == 0) {
						outageV2VO.setIsPlanned(false);
					}
					if (outageV2VO.getOutageStartTime() != null) {
						Date d = outageV2VO.getOutageStartTime();
						Date newDate = DateUtil.addHoursToJavaUtilDate(d, 4);
						newDate = DateUtil.addMinutesToJavaUtilDate(newDate, 30);
						// Date newDate1 = DateUtils.addMinutes(newDate, 30);
						System.out.println("d" + d);
						System.out.println("newDate" + newDate);
						// System.out.println("newDate1"+newDate1);
						outageV2VO.setOutageEndTime(newDate);
					}
					outageV2VOs.add(outageV2VO);
				}
			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("searchOutage(OutageVO outageVO) - end : ", outageV2VOs);
		return outageV2VOs;
	}

	public TransformerDetailResponseVO getTransformerDetails(String uid) throws OutageException {
		TransformerDetailResponseVO transformerResponseVO = null;
		ObjectMapper objectMapper = new ObjectMapper();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		try {
			System.out.println("uid" + uid);
			outageSearchVO.setTransformeruid(uid);
			String jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.FIND_TRANSFORMERBY_UID);
			System.out.println("tran-->jsondata" + jsondata);
			transformerResponseVO = new TransformerDetailResponseVO();
			transformerResponseVO = objectMapper.readValue(jsondata, TransformerDetailResponseVO.class);

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		return transformerResponseVO;
	}

	public OutageResponseVO saveOutageV2(OutageV2VO outageV2VO) throws OutageException {
		OutageResponseVO v2vo = null;
		Integer id = 0;
		String sc = "";
		String scd = "";
		ApiAuditVO apiAuditVO = new ApiAuditVO();
		ObjectMapper Obj = new ObjectMapper();
		/*
		 * long a = 0; long a1 = 0; long b = 0; long b1 = 0; long c = 0; long c1 = 0;
		 * long d = 0; long d1 = 0; long e = 0; long e1 = 0; long f = 0; long f1 = 0;
		 */
		try {
			logger.debug("start1");
			// a = System.currentTimeMillis();
			// logger.debug("saveOutage(OutageVO outageVO) - start : ", outageV2VO);
			this.getFieldDataValidation(outageV2VO);
			if (StringUtil.isNotNullOrEmpty(this.getValidationSaveUpdate(outageV2VO))) {
				throw new OutageException(this.getValidationSaveUpdate(outageV2VO) + " Fields is Required");
			}
			// List<OutageV2> outagelist =
			// outageRepositoryV2.fetchOutagesByOutageId(outageV2VO.getOutageId());
			// if (outagelist != null && outagelist.size() > 0) {
			// throw new OutageException("Duplicate OutageId Found");
			// }

			// a1 = System.currentTimeMillis();
			// logger.debug(" validation and fetch outageByOutageId:" + a1 + " " + a + " \n"
			// + (a1 - a));
			/*
			 * logger.debug("start2"); new Thread(new Runnable() {
			 * 
			 * @Override public void run() { for (int i = 0; i < 1000; i++) {
			 * System.out.println("New thread created");
			 * 
			 * }
			 * 
			 * } }).start();
			 */
			OutageV2 outage = outageServiceHelper.copyDataFromOutageVOToUserModel(outageV2VO);
			// b = System.currentTimeMillis();
			if (StringUtil.isNotNullOrEmpty(outageV2VO.getNetworkElementUID())) {
				try {
					TransformerDetailResponseVO responseVO = getTransformerDetails(outageV2VO.getNetworkElementUID());
					if (responseVO != null) {
						outage.setDivisionname(responseVO.getDivisionname());
						outage.setDivision(responseVO.getDivisionid());
						outage.setFeeder(responseVO.getFeederid());
						outage.setSubdivision(responseVO.getSubdivisionid());
						outage.setSubstation(responseVO.getSubstationid());
						outage.setTransformer(responseVO.getId());
						outage.setFeedername(responseVO.getFeederName());
						outage.setSubdivisionname(responseVO.getSubdivisionname());
						outage.setTransformername(responseVO.getName());
						outage.setSubstationname(responseVO.getSubstationname());
						System.out.println("responseVO.getName()" + responseVO.getName());
					}
				} catch (Exception ex) {
					// TODO: handle exception
				}
			}
			logger.debug("start3");
			if (!NumberUtil.isNotNullOrZero(id)) {
				// outage.setCreatedby(outageV2VO.getCreatedBy());
				outage.setCreateddate(new Date());
				sc = "C";
				scd = "Outage Created Successfully";
			}
			if (outage.getOutageStartTime() != null) {
				// Date dt = outage.getOutageStartTime();
				// Date newDate = DateUtil.addHoursToJavaUtilDate(dt, 4);
				// newDate = DateUtil.addMinutesToJavaUtilDate(newDate, 30);
				// Date newDate1 = DateUtils.addMinutes(newDate, 30);
				// System.out.println("d" + d);
				// System.out.println("newDate" + newDate);
				// System.out.println("newDate1"+newDate1);
				// outage.setOutageEndTime(newDate);
			}
			logger.debug("start4");
			// b1 = System.currentTimeMillis();
			// logger.debug(" getfromtransformerdetails:" + b1 + " " + b + " \n" + (b1 -
			// b));
			// c = System.currentTimeMillis();
			outage = outageRepositoryV2.save(outage);
			// c1 = System.currentTimeMillis();

			// logger.debug(" saveOutage:" + c1 + " " + c + " \n" + (c1 - c));
			// d = System.currentTimeMillis();
			v2vo = outageServiceHelper.copyDataFromOutageResponseModelToVO(outage);
			v2vo.setStatusCode(sc);
			v2vo.setStatusDesc(scd);

			// onCallAsync(outageV2VO, outage, v2vo);

			logger.debug("start5");
			/*
			 * OutageHistoryVO outageHistoryVO = new OutageHistoryVO();
			 * outageHistoryVO.setDescription("Created By " + outage.getUserName());
			 * outageHistoryVO.setTenantid(outage.getTenantId());
			 * outageHistoryVO.setCreateddate(new Date());
			 * outageHistoryVO.setOutageid(outage.getId());
			 */
			saveOutageHistory(new OutageHistoryVO(outage.getId(), "", "Outage is Created", outageV2VO.getTenantId(),
					new Date(), outageV2VO.getUserName()));

			// d1 = System.currentTimeMillis();
			// logger.debug(" saveOutageHistory:" + d1 + " " + d + " \n" + (d1 - d));
			logger.debug("break");
			logger.debug("start6");
			try {
				logger.debug("start7");
				/*
				 * Executors.newCachedThreadPool().submit(() -> { Thread.sleep(1);
				 * System.out.println("*&**&(*(*(*"); CompletableFuture.complete("Hello");
				 * return null; });
				 */
				/*
				 * new Thread(new Runnable() {
				 * 
				 * @Override public void run() { System.out.println("New thread created"); }
				 * }).start();
				 */
				logger.debug("start8");
				// e = System.currentTimeMillis();
				// CompletableFuture<Boolean> aduitr = CompletableFuture.supplyAsync(()->{
				// return saveApiAudit(outageV2VO.getJsonData(), Obj.writeValueAsString(v2vo),
				// outageV2VO.getTenantId(),outageV2VO.getOutageId());
				// },executor);
				saveApiAudit(outageV2VO.getJsonData(), Obj.writeValueAsString(v2vo), outageV2VO.getTenantId(),
						outageV2VO.getOutageId());
				// e1 = System.currentTimeMillis();
				// logger.debug(" saveApiAudit:" + e1 + " " + e + " \n" + (e1 - e));
			} catch (Exception ex) {
				// TODO: handle exception
			}

			// logger.debug("endssdfsdfsdfsdfsdfsdf");
		} catch (Exception ex) {
			logger.error(ex);
			// TODO: handle exception
			throw new OutageException(ex);
		}
		// logger.debug(" saveOutage ENd:" + e1 + " " + a + " \n" + (e1 - a));
		// f = System.currentTimeMillis();
		logger.debug("saveOutage(OutageVO outageVO) - end : ", outageV2VO);
		// f1 = System.currentTimeMillis();
		// logger.debug(" outageResponse:" + f1 + " " + f + " \n" + (f1 - f));
		return v2vo;
	}

	@Async
	public void onCallAsync(OutageV2VO outageV2VO, OutageV2 outage, OutageResponseVO v2vo) throws OutageException {
		ObjectMapper Obj = new ObjectMapper();
		try {
			/*
			 * logger.debug("thread -- 5000-before"); Thread.sleep(25000);
			 * logger.debug("thread -- 5000-after");
			 */
			// logger.debug(" saveOutage:" + c1 + " " + c + " \n" + (c1 - c));
			// d1 = System.currentTimeMillis();
			OutageHistoryVO outageHistoryVO = new OutageHistoryVO();
			outageHistoryVO.setDescription("Created By " + outage.getUserName());
			outageHistoryVO.setTenantid(outage.getTenantId());
			outageHistoryVO.setCreateddate(new Date());
			outageHistoryVO.setOutageid(outage.getId());
			outageHistoryVO = saveOutageHistory(outageHistoryVO);

			try {
				saveApiAudit(outageV2VO.getJsonData(), Obj.writeValueAsString(v2vo), outage.getTenantId(),
						outage.getOutageId());
			} catch (Exception e) {
				// TODO: handle exception
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public OutageResponseVO updateOutageV2(OutageV2VO outageV2VO) throws OutageException {
		OutageResponseVO v2vo = null;
		Integer id = 0;
		String sc = "";
		String scd = "";
		String status = "";
		ObjectMapper Obj = new ObjectMapper();
		try {
			logger.debug("saveOutage(OutageVO outageVO) - start : ", outageV2VO);
			this.getFieldDataValidation(outageV2VO);
			if (StringUtil.isNotNullOrEmpty(this.getValidationSaveUpdate(outageV2VO))) {
				throw new OutageException(this.getValidationSaveUpdate(outageV2VO) + " Fields is Required");
			}
			List<OutageV2> outagelist = outageRepositoryV2.fetchOutagesByOutageId(outageV2VO.getOutageId());
			if (outagelist != null && outagelist.size() > 0) {
				if (outagelist.size() > 1) {
					throw new OutageException("Duplicate OutageId Found");
				} else {
					for (OutageV2 outageV2 : outagelist) {
						id = outageV2.getId();
						status = outageV2.getStatus();
					}
				}
			} else {
				throw new OutageException("OutageId is Invalid");
			}

			OutageV2 outage = outageServiceHelper.copyDataFromOutageVOToUserModel(outageV2VO);
			if (StringUtil.isNotNullOrEmpty(outageV2VO.getNetworkElementUID())) {
				try {
					TransformerDetailResponseVO responseVO = getTransformerDetails(outageV2VO.getNetworkElementUID());
					if (responseVO != null) {
						outage.setDivisionname(responseVO.getDivisionname());
						outage.setDivision(responseVO.getDivisionid());
						// outageV2VO.setFeeder(responseVO.getFeederid());
						outage.setFeeder(responseVO.getFeederid());
						outage.setSubdivision(responseVO.getSubdivisionid());
						outage.setSubstation(responseVO.getSubstationid());
						outage.setTransformer(responseVO.getId());
						outage.setFeedername(responseVO.getFeederName());
						// outage.setSectionname(responseVO.getSectionname());
						outage.setSubdivisionname(responseVO.getSubdivisionname());
						outage.setTransformername(responseVO.getName());
						outage.setSubstationname(responseVO.getSubstationname());
						// System.out.println("responseVO.getFeederid()"+responseVO.getFeederid());
						System.out.println("responseVO.getName()" + responseVO.getName());
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			if (NumberUtil.isNotNullOrZero(id)) {
				outage.setLastupdateddate(new Date());
				outage.setId(id);
				outage.setStatus(status);
				sc = "U";
				scd = "Outage Updated Successfully";
			}
			outage = outageRepositoryV2.save(outage);
			v2vo = outageServiceHelper.copyDataFromOutageResponseModelToVO(outage);
			v2vo.setStatusCode(sc);
			v2vo.setStatusDesc(scd);
			// v2vo.setUserId(outageV2VO.getUserId());
			// v2vo.setAction(outageV2VO.getAction());
			/*
			 * OutageHistoryVO outageHistoryVO = new OutageHistoryVO();
			 * outageHistoryVO.setDescription("Updated By " + outage.getUserName());
			 * outageHistoryVO.setTenantid(outage.getTenantId());
			 * outageHistoryVO.setCreateddate(new Date());
			 * outageHistoryVO.setOutageid(outage.getId()); outageHistoryVO =
			 * saveOutageHistory(outageHistoryVO);
			 */
			try {
				saveOutageHistory(new OutageHistoryVO(outage.getId(), "", "Outage is Updated", outageV2VO.getTenantId(),
						new Date(), outageV2VO.getUserName()));
			} catch (Exception e) {
				logger.debug(e);
			}
			try {
				saveApiAudit(outageV2VO.getJsonData(), Obj.writeValueAsString(v2vo), outage.getTenantId(),
						outage.getOutageId());
			} catch (Exception e) {
				// TODO: handle exception
			}
		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		logger.debug("saveOutage(OutageVO outageVO) - end : ", outageV2VO);
		return v2vo;
	}

	@Override
	public OutageResponseVO confirmOutageV2(OutageV2VO outageVO) throws OutageException {
		OutageResponseVO v2vo = null;
		OutageV2 outage = null;
		OutageNotificationVO outageNotificationVO = null;
		ObjectMapper Obj = new ObjectMapper();
		/*
		 * long a = 0; long a1 = 0; long b = 0; long b1 = 0; long c = 0; long c1 = 0;
		 * long d = 0; long d1 = 0; long e = 0; long e1 = 0; long f = 0; long f1 = 0;
		 */
		try {
			// a = System.currentTimeMillis();
			// logger.debug("confirmOutage(OutageVO outageVO) - start : ", outageVO);
			this.getFieldDataValidation(outageVO);
			if (StringUtil.isNotNullOrEmpty(this.getValidation(outageVO))) {
				throw new OutageException(this.getValidation(outageVO) + " Fields is Required");
			}
			List<OutageV2> outagelist = outageRepositoryV2.fetchOutagesByOutageId(outageVO.getOutageId());
			if (outagelist != null && outagelist.size() > 0) {
				if (outagelist.size() > 1) {
					throw new OutageException("Duplicate OutageId Found");
				} else {
					for (OutageV2 outageV2 : outagelist) {
						outage = outageV2;
					}
				}
			}
			outage.setStatus(IOmsConstants.CONFIRM_STATUS_DOMAIN);
			// a1 = System.currentTimeMillis();
			// logger.debug(" validation and fetch outageByOutageId:" + a1 + " " + a + " \n"
			// + (a1 - a));
			// outage.setStatus("CONFIRMED");
			/*
			 * try { isCriticalOutageCheck(outageVO); } catch (Exception e) {
			 * logger.error(e); // TODO: handle exception throw new OutageException(e); }
			 */
			// outage.setLastupdatedby(outageVO.getUserId());
			outage.setLastupdateddate(new Date());
			// OutageV2 outage =
			// outageServiceHelper.copyDataFromOutageVOToUserModel(outageVO);
			/*
			 * if(!NumberUtil.isNotNullOrZero(outageVO.getId())) {
			 * outage.setCreatedby(outageVO.getUserId()); outage.setCreateddate(new Date());
			 * }else { outage.setLastupdatedby(outageVO.getUserId());
			 * outage.setLastupdateddate(new Date()); }
			 */
			// b = System.currentTimeMillis();
			outage = outageRepositoryV2.save(outage);
			// b1 = System.currentTimeMillis();
			// logger.debug(" saveoutage:" + b1 + " " + b + " \n" + (b1 - b));
			v2vo = outageServiceHelper.copyDataFromOutageResponseModelToVO(outage);
			v2vo.setStatusCode("CC");
			v2vo.setStatusDesc("Outage Confirmed Successfully");
			// v2vo.setUserId(outageVO.getUserId());
			// v2vo.setAction(outageVO.getAction());

			/*
			 * if (StringUtil.isNotNullOrEmpty(outage.getOutageId())) {
			 * 
			 * outageNotificationVO = new OutageNotificationVO();
			 * outageNotificationVO.setStatus(IOmsConstants.CONFIRM_STATUS_BPMN);
			 * outageNotificationVO.setOutageId(outage.getOutageId());
			 * outageNotificationVO.setNetworkElementType(outage.getNetworkElementType());
			 * outageNotificationVO.setNetworkElementUID(outage.getNetworkElementUID());
			 * 
			 * try { // c = System.currentTimeMillis();
			 * sendMailToStaff(outageNotificationVO); // c1 = System.currentTimeMillis(); //
			 * logger.debug(" sendMailToStaff:" + c1 + "  " + c + "  \n" + (c1 - c)); }
			 * catch (Exception ex) {
			 * 
			 * }
			 * 
			 * try { d = System.currentTimeMillis(); sendSmsToStaff(outageNotificationVO);
			 * d1 = System.currentTimeMillis(); logger.debug(" sendSmsToStaff:" + d1 + "  "
			 * + d + "  \n" + (d1 - d));
			 * 
			 * } catch (Exception ex) {
			 * 
			 * } }
			 */
			/*
			 * OutageVO outageVO2 = this.getFindOutageById(outageVO.getOutageId()); try {
			 * if(outageVO2 !=null) { confirmOutagesendSms(outageVO2); }
			 * 
			 * 
			 * }catch (Exception e) {
			 * 
			 * } try { if(outageVO2 !=null) { confirmOutagesendEmails(outageVO2); }
			 * 
			 * 
			 * }catch (Exception e) {
			 * 
			 * }
			 */
			// e = System.currentTimeMillis();
			/*
			 * OutageHistoryVO outageHistoryVO = new OutageHistoryVO();
			 * outageHistoryVO.setDescription("Confirmed By " + outage.getUserName());
			 * outageHistoryVO.setTenantid(outage.getTenantId());
			 * outageHistoryVO.setCreateddate(new Date());
			 * outageHistoryVO.setOutageid(outage.getId()); outageHistoryVO =
			 * saveOutageHistory(outageHistoryVO);
			 */
			try {
				saveOutageHistory(new OutageHistoryVO(outage.getId(), "", "Outage is Confirmed", outage.getTenantId(),
						new Date(), outageVO.getUserName()));
			} catch (Exception e) {
				logger.debug(e);
			}
			// e1 = System.currentTimeMillis();
			// logger.debug(" saveOutageHistory:" + e1 + " " + e + " \n" + (e1 - e));

			try {
				// f = System.currentTimeMillis();
				saveApiAudit(outageVO.getJsonData(), Obj.writeValueAsString(v2vo), outage.getTenantId(),
						outage.getOutageId());
				// f1 = System.currentTimeMillis();
				// logger.debug(" saveApiAudit:" + f1 + " " + f + " \n" + (f1 - f));

			} catch (Exception ex) {
				// TODO: handle exception
			}

		} catch (Exception ex) {

			logger.error(ex);
			// TODO: handle exception
			throw new OutageException(ex);
		}
		// logger.debug(" End ConfirmOutage:" + f1 + " " + a + " \n" + (f1 - a));
		logger.debug("confirmOutage(OutageVO outageVO) - end : ", outageVO);
		return v2vo;
	}

	@Override
	public OutageResponseVO cancellOutageV2(OutageV2VO outageVO) throws OutageException {
		OutageResponseVO v2vo = null;
		OutageV2 outage = null;
		OutageNotificationVO outageNotificationVO = null;
		ObjectMapper Obj = new ObjectMapper();
		try {

			logger.debug("cancellOutageV2(OutageVO outageVO) - start : ", outageVO);
			this.getFieldDataValidation(outageVO);
			if (StringUtil.isNotNullOrEmpty(this.getValidation(outageVO))) {
				throw new OutageException(this.getValidation(outageVO) + " Fields is Required");
			}
			List<OutageV2> outagelist = outageRepositoryV2.fetchOutagesByOutageId(outageVO.getOutageId());
			if (outagelist != null && outagelist.size() > 0) {
				if (outagelist.size() > 1) {
					throw new OutageException("Duplicate OutageId Found");
				} else {
					for (OutageV2 outageV2 : outagelist) {
						outage = outageV2;
					}
				}
			}
			outage.setStatus(IOmsConstants.CANCELL_STATUS_DOMAIN);

			outage.setLastupdateddate(new Date());
			outage = outageRepositoryV2.save(outage);
			v2vo = outageServiceHelper.copyDataFromOutageResponseModelToVO(outage);
			v2vo.setStatusCode("CC");
			v2vo.setStatusDesc("Outage Cancelled Successfully");

//			OutageHistoryVO outageHistoryVO = new OutageHistoryVO();
//			outageHistoryVO.setDescription("Cancelled By " + outage.getUserName());
//			outageHistoryVO.setTenantid(outage.getTenantId());
//			outageHistoryVO.setCreateddate(new Date());
//			outageHistoryVO.setOutageid(outage.getId());
//			outageHistoryVO = saveOutageHistory(outageHistoryVO);
			try {
				saveOutageHistory(new OutageHistoryVO(outage.getId(), "", "Outage is Cancelled", outage.getTenantId(),
						new Date(), outageVO.getUserName()));
			} catch (Exception e) {
				logger.debug(e);
			}
			try {
				saveApiAudit(outageVO.getJsonData(), Obj.writeValueAsString(v2vo), outage.getTenantId(),
						outage.getOutageId());
			} catch (Exception e) {
				// TODO: handle exception
			}

		} catch (Exception e) {

			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		logger.debug("confirmOutage(OutageVO outageVO) - end : ", outageVO);
		return v2vo;
	}

	@Override
	public OutageResponseVO approvedOutageV2(OutageV2VO outageVO) throws OutageException {
		OutageResponseVO v2vo = null;
		OutageV2 outage = null;
		OutageNotificationVO outageNotificationVO = null;
		ObjectMapper Obj = new ObjectMapper();
		try {
			logger.debug("approvedOutage(OutageVO outageVO) - start : ", outageVO);
			this.getFieldDataValidation(outageVO);
			if (StringUtil.isNotNullOrEmpty(this.getValidation(outageVO))) {
				throw new OutageException(this.getValidation(outageVO) + " Fields is Required");
			}
			List<OutageV2> outagelist = outageRepositoryV2.fetchOutagesByOutageId(outageVO.getOutageId());
			if (outagelist != null && outagelist.size() > 0) {
				if (outagelist.size() > 1) {
					throw new OutageException("Duplicate OutageId Found");
				} else {
					for (OutageV2 outageV2 : outagelist) {
						outage = outageV2;
						logger.debug("outage-->id" + outage.getCrewid());
					}
				}
			}
			outage.setStatus(IOmsConstants.APPROVE_STATUS_DOMAIN);
			outage.setApproverRemarks(outageVO.getApproverremarks());
			outage.setApprovedDate(new Date());
			if (NumberUtil.isNotNullOrZero(outageVO.getId())) {
				// outage.setLastupdatedby(outageVO.getUserId());
				outage.setLastupdateddate(new Date());
			}
			outage = outageRepositoryV2.save(outage);
			v2vo = outageServiceHelper.copyDataFromOutageResponseModelToVO(outage);
			v2vo.setStatusCode("A");
			v2vo.setStatusDesc("Outage Approved Successfully");
			System.out.println("outage.getOutageId()" + outage.getOutageId());
			/*
			 * if (StringUtil.isNotNullOrEmpty(outage.getOutageId())) { outageNotificationVO
			 * = new OutageNotificationVO();
			 * outageNotificationVO.setStatus(IOmsConstants.APPROVE_STATUS_BPMN);
			 * outageNotificationVO.setOutageId(outage.getOutageId());
			 * outageNotificationVO.setNetworkElementType(outage.getNetworkElementType());
			 * outageNotificationVO.setNetworkElementUID(outage.getNetworkElementUID());
			 * outageNotificationVO.setRescheduled(outageVO.isRescheduled()); try {
			 * sendMailToStaff(outageNotificationVO); } catch (Exception e) {
			 * 
			 * }
			 * 
			 * try { sendSmsToStaff(outageNotificationVO); } catch (Exception e) {
			 * 
			 * }
			 * 
			 * try { sendMailToCustomer(outageNotificationVO); } catch (Exception e) {
			 * 
			 * }
			 * 
			 * try { sendSmsToCustomer(outageNotificationVO); } catch (Exception e) {
			 * 
			 * } }
			 */
			// v2vo.setUserId(outageVO.getUserId());
			// v2vo.setAction(outageVO.getAction());
			// System.out.println("$$$$$"+outageVO.getId());
			/*
			 * try { approvedOutagesendSms(outageVO);
			 * 
			 * }catch (Exception e) {
			 * 
			 * } try { approveOutagesendEmails(outageVO); }catch (Exception e) {
			 * 
			 * }
			 */
//			OutageHistoryVO outageHistoryVO = new OutageHistoryVO();
//			outageHistoryVO.setDescription("Approved By " + outage.getUserName());
//			outageHistoryVO.setTenantid(outage.getTenantId());
//			outageHistoryVO.setCreateddate(new Date());
//			outageHistoryVO.setOutageid(outage.getId());
//			outageHistoryVO = saveOutageHistory(outageHistoryVO);
			try {
				String desc = "";
				if (outageVO.isRescheduled()) {
					desc = "Rescheduled Outage is Approved and Crew Task is Updated";
				} else {
					desc = "Outage is Approved and Crew Task is Created";
				}
				saveOutageHistory(new OutageHistoryVO(outage.getId(), "", desc, outage.getTenantId(), new Date(),
						outageVO.getUserName()));
			} catch (Exception e) {
				logger.debug(e);
			}
			try {
				saveApiAudit(outageVO.getJsonData(), Obj.writeValueAsString(v2vo), outage.getTenantId(),
						outage.getOutageId());
			} catch (Exception e) {
				// TODO: handle exception
			}

			// Approved followed by creating JobOrder Creating

			try {
				CrewTaskVO crewTaskVO = new CrewTaskVO();
				crewTaskVO.setPlannedStartDate(outage.getOutageStartTime());
				crewTaskVO.setPlannedEndDate(outage.getOutageEndTime());
				crewTaskVO.setStatus(IOmsConstants.PLANNED_STATUS_DOMAIN);
				crewTaskVO.setJobDetails(outage.getReason());
				crewTaskVO.setTenantid(outage.getTenantId());
				crewTaskVO.setOutageid(outage.getId());
				crewTaskVO.setCrewid(outage.getCrewid());
				crewTaskVO.setRescheduled(outageVO.isRescheduled());
				crewTaskVO = plannedCrewTask(crewTaskVO);
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e);
			}

		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		logger.debug("approvedOutage(OutageVO outageVO) - end : ", outageVO);
		return v2vo;
	}

	@Override
	public OutageResponseVO rejectOutageV2(OutageV2VO outageVO) throws OutageException {
		OutageResponseVO v2vo = null;
		OutageV2 outage = null;
		OutageNotificationVO outageNotificationVO = null;
		ObjectMapper Obj = new ObjectMapper();
		try {
			logger.debug("rejectOutage(OutageVO outageVO) - start : ", outageVO);
			this.getFieldDataValidation(outageVO);
			List<OutageV2> outagelist = outageRepositoryV2.fetchOutagesByOutageId(outageVO.getOutageId());
			if (outagelist != null && outagelist.size() > 0) {
				if (outagelist.size() > 1) {
					throw new OutageException("Duplicate OutageId Found");
				} else {
					for (OutageV2 outageV2 : outagelist) {
						outage = outageV2;
					}
				}
			}
			// OutageV2 outage =
			// outageServiceHelper.copyDataFromOutageVOToUserModel(outageVO);
			if (NumberUtil.isNotNullOrZero(outageVO.getId())) {
				// outage.setLastupdatedby(outageVO.getUserId());
				outage.setLastupdateddate(new Date());
			}
			outage.setStatus(IOmsConstants.REJECT_STATUS_DOMAIN);
			outage = outageRepositoryV2.save(outage);
			v2vo = outageServiceHelper.copyDataFromOutageResponseModelToVO(outage);
			v2vo.setStatusCode("R");
			v2vo.setStatusDesc("Outage Rejected Successfully");
			// v2vo.setUserId(outageVO.getUserId());
			// v2vo.setAction(outageVO.getAction());
			/*
			 * try { rejectOutagesendSms(outageVO);
			 * 
			 * }catch (Exception e) {
			 * 
			 * } try { rejectOutagesendEmails(outageVO); }catch (Exception e) {
			 * 
			 * }
			 */

			/*
			 * if (StringUtil.isNotNullOrEmpty(outage.getOutageId())) {
			 * 
			 * outageNotificationVO = new OutageNotificationVO();
			 * outageNotificationVO.setStatus(IOmsConstants.REJECT_STATUS_BPMN);
			 * outageNotificationVO.setOutageId(outage.getOutageId());
			 * outageNotificationVO.setNetworkElementType(outage.getNetworkElementType());
			 * outageNotificationVO.setNetworkElementUID(outage.getNetworkElementUID());
			 * outageNotificationVO.setRescheduled(outageVO.isRescheduled()); try {
			 * sendMailToStaff(outageNotificationVO); } catch (Exception e) {
			 * 
			 * }
			 * 
			 * try { sendSmsToStaff(outageNotificationVO); } catch (Exception e) {
			 * 
			 * } }
			 */
//			OutageHistoryVO outageHistoryVO = new OutageHistoryVO();
//			outageHistoryVO.setDescription("Rejected By " + outage.getUserName());
//			outageHistoryVO.setTenantid(outage.getTenantId());
//			outageHistoryVO.setCreateddate(new Date());
//			outageHistoryVO.setOutageid(outage.getId());
//			outageHistoryVO = saveOutageHistory(outageHistoryVO);
			try {
				String desc = "";
				if (outageVO.isRescheduled()) {
					desc = "Rescheduled Outage is Rejected";
				} else {
					desc = "Outage is Rejected";
				}
				saveOutageHistory(new OutageHistoryVO(outage.getId(), "", desc, outage.getTenantId(), new Date(),
						outageVO.getUserName()));
			} catch (Exception e) {
				logger.debug(e);
			}
			try {
				saveApiAudit(outageVO.getJsonData(), Obj.writeValueAsString(v2vo), outage.getTenantId(),
						outage.getOutageId());
			} catch (Exception e) {
				// TODO: handle exception
			}
		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		logger.debug("rejectOutage(OutageVO outageVO) - end : ", outageVO);
		return v2vo;
	}

	public OutageVO getFindOutageById(String outageId) throws OutageException {
		OutageVO outageVO = null;
		Outage outagemodel = null;
		try {
			List<Outage> outages = outageRepository.fetchOutagesByOutageId(outageId);
			if (outages != null && outages.size() > 0) {
				for (Outage outage : outages) {
					outagemodel = outage;
				}

			} else {
				throw new OutageException("outageId is Invalid");
			}

			outageVO = outageServiceHelper.copyDataFromModelToVO(outagemodel);
		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		return outageVO;
	}

	@Override
	public OutageResponseVO sendMailToStaff(OutageNotificationVO outageNotificationVO) throws OutageException {
		OutageResponseVO outageResponseVO = null;
		try {

			logger.debug("sendMailToStaff(OutageNotificationVO outageNotificationVO) - start : ", outageNotificationVO);
			this.getEmailSmsValidation(outageNotificationVO);
			// OutageVO outageVO =
			// this.getFindOutageById(outageNotificationVO.getOutageId());
			// outageVO.setRescheduled(outageNotificationVO.isRescheduled());
			/*
			 * if (StringUtil.isNotNullOrEmpty(outageVO.getNetworkElementUID())) { try {
			 * TransformerDetailResponseVO responseVO =
			 * getTransformerDetails(outageVO.getNetworkElementUID()); if (responseVO !=
			 * null) { //// outageVO.setDivisioname(responseVO.getDivisionname());
			 * outageVO.setFeedername(responseVO.getFeederName());
			 * outageVO.setSectionname(responseVO.getSectionname());
			 * outageVO.setSubdivisionname(responseVO.getSubdivisionname());
			 * outageVO.setTransformername(responseVO.getName());
			 * outageVO.setSubstationname(responseVO.getSubstationname());
			 * outageVO.setTransformer(responseVO.getId());
			 * outageVO.setSubstation(responseVO.getSubstationid());
			 * System.out.println("responseVO.getId()" + responseVO.getId());
			 * System.out.println("responseVO.getSubstationid()" +
			 * responseVO.getSubstationid()); System.out.println("responseVO.getName()" +
			 * responseVO.getName()); } } catch (Exception e) { // TODO: handle exception }
			 * }
			 */
			if (outageNotificationVO.getStatus().equalsIgnoreCase(IOmsConstants.CONFIRM_STATUS_DOMAIN)) {
				try {
					confirmOutagesendEmails(outageNotificationVO.getOutageVO());
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else if (outageNotificationVO.getStatus().equalsIgnoreCase(IOmsConstants.RESCHEDULE_STATUS_DOMAIN)) {
				try {
					rescheduleOutagesendEmails(outageNotificationVO.getOutageVO());
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else if (outageNotificationVO.getStatus().equalsIgnoreCase(IOmsConstants.APPROVE_STATUS_DOMAIN)) {
				try {
					approveOutagesendEmailStaff(outageNotificationVO.getOutageVO());
				} catch (Exception e) {
					// TODO: handle exception
				}

			} else if (outageNotificationVO.getStatus().equalsIgnoreCase(IOmsConstants.REJECT_STATUS_DOMAIN)) {
				try {
					rejectOutagesendEmails(outageNotificationVO.getOutageVO());
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else if (outageNotificationVO.getStatus().equalsIgnoreCase(IOmsConstants.COMPLETE_STATUS_DOMAIN)) {
				try {
					completeOutagesendEmailstoStaff(outageNotificationVO.getOutageVO());
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					completeOutagesendEmailstoCustomer(outageNotificationVO.getOutageVO());
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			logger.debug("sendMailToStaff(OutageNotificationVO outageNotificationVO) - end : ", outageResponseVO);
		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		return outageResponseVO;
	}

	@Override
	public OutageResponseVO sendMailToCustomer(OutageNotificationVO outageNotificationVO) throws OutageException {
		OutageResponseVO outageResponseVO = null;
		try {

			logger.debug("sendMailToCustomer(OutageNotificationVO outageNotificationVO) - start : ",
					outageNotificationVO);
			this.getEmailSmsValidation(outageNotificationVO);
			// OutageVO outageVO =
			// this.getFindOutageById(outageNotificationVO.getOutageId());
			// outageVO.setRescheduled(outageNotificationVO.isRescheduled());
			if (outageNotificationVO.getOutageVO().getStartDateTime() != null) {
				Date d = outageNotificationVO.getOutageVO().getStartDateTime();
				Date newDate = DateUtil.addHoursToJavaUtilDate(d, 4);
				newDate = DateUtil.addMinutesToJavaUtilDate(newDate, 30);
				// Date newDate1 = DateUtils.addMinutes(newDate, 30);
				System.out.println("d" + d);
				System.out.println("newDate" + newDate);
				// System.out.println("newDate1"+newDate1);
				outageNotificationVO.getOutageVO().setEndDateTime(newDate);
			}
			/*
			 * if (StringUtil.isNotNullOrEmpty(outageVO.getNetworkElementUID())) { try {
			 * TransformerDetailResponseVO responseVO =
			 * getTransformerDetails(outageVO.getNetworkElementUID()); if (responseVO !=
			 * null) { //// outageVO.setDivisioname(responseVO.getDivisionname());
			 * outageVO.setFeedername(responseVO.getFeederName());
			 * outageVO.setSectionname(responseVO.getSectionname());
			 * outageVO.setSubdivisionname(responseVO.getSubdivisionname());
			 * outageVO.setTransformername(responseVO.getName());
			 * outageVO.setSubstationname(responseVO.getSubstationname());
			 * outageVO.setTransformer(responseVO.getId());
			 * outageVO.setSubstation(responseVO.getSubstationid());
			 * System.out.println("responseVO.getName()" + responseVO.getId());
			 * System.out.println("responseVO.getName()" + responseVO.getName()); } } catch
			 * (Exception e) { // TODO: handle exception } }
			 */
			if (outageNotificationVO.getStatus().equalsIgnoreCase(IOmsConstants.CONFIRM_STATUS_DOMAIN)) {
				try {
					confirmOutagesendEmails(outageNotificationVO.getOutageVO());
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else if (outageNotificationVO.getStatus().equalsIgnoreCase(IOmsConstants.APPROVE_STATUS_DOMAIN)) {
				try {
					approveOutagesendEmailsCustomer(outageNotificationVO.getOutageVO());
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					// approveOutagesendEmailsCustomer(outageNotificationVO.getOutageVO());
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else if (outageNotificationVO.getStatus().equalsIgnoreCase(IOmsConstants.REJECT_STATUS_DOMAIN)) {
				try {
					rejectOutagesendEmails(outageNotificationVO.getOutageVO());
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else if (outageNotificationVO.getStatus().equalsIgnoreCase(IOmsConstants.COMPLETE_STATUS_DOMAIN)) {
				try {
					completeOutagesendEmailstoStaff(outageNotificationVO.getOutageVO());
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					completeOutagesendEmailstoCustomer(outageNotificationVO.getOutageVO());
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			logger.debug("sendMailToCustomer(OutageNotificationVO outageNotificationVO) - end : ", outageResponseVO);
		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		return outageResponseVO;
	}

	@Override
	public OutageResponseVO sendSmsToStaff(OutageNotificationVO outageNotificationVO) throws OutageException {
		OutageResponseVO outageResponseVO = null;
		try {
			logger.debug("sendSmsToStaff(OutageNotificationVO outageNotificationVO) - start : ", outageNotificationVO);
			this.getEmailSmsValidation(outageNotificationVO);

			// OutageVO outageVO =
			// this.getFindOutageById(outageNotificationVO.getOutageId());
			if (outageNotificationVO.getOutageVO().getStartDateTime() != null) {
				Date d = outageNotificationVO.getOutageVO().getStartDateTime();
				Date newDate = DateUtil.addHoursToJavaUtilDate(d, 4);
				newDate = DateUtil.addMinutesToJavaUtilDate(newDate, 30);
				// Date newDate1 = DateUtils.addMinutes(newDate, 30);

				// System.out.println("newDate1"+newDate1);
				outageNotificationVO.getOutageVO().setEndDateTime(newDate);
			}
			/*
			 * if (StringUtil.isNotNullOrEmpty(outageVO.getNetworkElementUID())) { try {
			 * 
			 * TransformerDetailResponseVO responseVO =
			 * getTransformerDetails(outageVO.getNetworkElementUID()); if (responseVO !=
			 * null) { //// outageVO.setDivisioname(responseVO.getDivisionname());
			 * outageVO.setFeedername(responseVO.getFeederName());
			 * outageVO.setSectionname(responseVO.getSectionname());
			 * outageVO.setSubdivisionname(responseVO.getSubdivisionname());
			 * outageVO.setTransformername(responseVO.getName());
			 * outageVO.setSubstationname(responseVO.getSubstationname());
			 * outageVO.setTransformer(responseVO.getId());
			 * outageVO.setSubstation(responseVO.getSubstationid());
			 * System.out.println("responseVO.getName()" + responseVO.getName()); } } catch
			 * (Exception e) { // TODO: handle exception } }
			 */
			if (outageNotificationVO.getStatus().equalsIgnoreCase(IOmsConstants.CONFIRM_STATUS_DOMAIN)) {
				try {
					confirmOutagesendSms(outageNotificationVO.getOutageVO());
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else if (outageNotificationVO.getStatus().equalsIgnoreCase(IOmsConstants.RESCHEDULE_STATUS_DOMAIN)) {
				try {
					rescheduledsendSms(outageNotificationVO.getOutageVO());
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else if (outageNotificationVO.getStatus().equalsIgnoreCase(IOmsConstants.APPROVE_STATUS_DOMAIN)) {
				try {
					approvedOutagesendSmsStaff(outageNotificationVO.getOutageVO());
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					approvedOutagesendSmsCustomer(outageNotificationVO.getOutageVO());
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else if (outageNotificationVO.getStatus().equalsIgnoreCase(IOmsConstants.REJECT_STATUS_DOMAIN)) {
				try {
					rejectOutagesendSms(outageNotificationVO.getOutageVO());
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			logger.debug("sendSmsToStaff(OutageNotificationVO outageNotificationVO) - end : ", outageResponseVO);
		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		return outageResponseVO;
	}

	@Override
	public OutageResponseVO sendSmsToCustomer(OutageNotificationVO outageNotificationVO) throws OutageException {
		OutageResponseVO outageResponseVO = null;
		try {

			logger.debug("sendSmsToCustomer(OutageNotificationVO outageNotificationVO) - start : ",
					outageNotificationVO);
			this.getEmailSmsValidation(outageNotificationVO);
			OutageVO outageVO = this.getFindOutageById(outageNotificationVO.getOutageId());
			if (outageVO.getStartDateTime() != null) {
				Date d = outageVO.getStartDateTime();
				Date newDate = DateUtil.addHoursToJavaUtilDate(d, 4);
				newDate = DateUtil.addMinutesToJavaUtilDate(newDate, 30);
				// Date newDate1 = DateUtils.addMinutes(newDate, 30);
				System.out.println("d" + d);
				System.out.println("newDate" + newDate);
				// System.out.println("newDate1"+newDate1);
				outageVO.setEndDateTime(newDate);
			}
			if (StringUtil.isNotNullOrEmpty(outageVO.getNetworkElementUID())) {
				try {
					TransformerDetailResponseVO responseVO = getTransformerDetails(outageVO.getNetworkElementUID());
					if (responseVO != null) {
						//// outageVO.setDivisioname(responseVO.getDivisionname());
						outageVO.setFeedername(responseVO.getFeederName());
						outageVO.setSectionname(responseVO.getSectionname());
						outageVO.setSubdivisionname(responseVO.getSubdivisionname());
						outageVO.setTransformername(responseVO.getName());
						outageVO.setSubstationname(responseVO.getSubstationname());
						outageVO.setTransformer(responseVO.getId());
						outageVO.setSubstation(responseVO.getSubstationid());
						System.out.println("responseVO.getName()" + responseVO.getName());
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			if (outageNotificationVO.getStatus().equalsIgnoreCase("Confirmed")) {
				try {
					confirmOutagesendSms(outageVO);
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else if (outageNotificationVO.getStatus().equalsIgnoreCase("Approved")) {
				try {
					approvedOutagesendSms(outageVO);
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else if (outageNotificationVO.getStatus().equalsIgnoreCase("Completed")) {
				try {
					completeOutagesendSmstoCustomer(outageVO);
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
			logger.debug("sendSmsToCustomer(OutageNotificationVO outageNotificationVO) - end : ", outageResponseVO);
		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		return outageResponseVO;
	}

	public void getEmailSmsValidation(OutageNotificationVO notificationVO) throws OutageException {

		try {

			if (!StringUtil.isNotNullOrEmpty(notificationVO.getNetworkElementType())) {
				throw new OutageException("NetworkElementType is Required");
			}
			if (!StringUtil.isNotNullOrEmpty(notificationVO.getNetworkElementUID())) {
				throw new OutageException("NetworkElementUID is Required");
			}
			if (!StringUtil.isNotNullOrEmpty(notificationVO.getOutageId())) {
				throw new OutageException("OutageId is Required");
			}
			if (!StringUtil.isNotNullOrEmpty(notificationVO.getStatus())) {
				throw new OutageException("Status is Required");
			} else {
				if (notificationVO.getStatus().equalsIgnoreCase("Confirmed")
						|| notificationVO.getStatus().equalsIgnoreCase("Approved")
						|| notificationVO.getStatus().equalsIgnoreCase("Rejected")
						|| notificationVO.getStatus().equalsIgnoreCase("Rescheduled")) {

				} else {
					throw new OutageException("Status is Invalid");
				}
			}

		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}

	}

	public OutageVO approveOutagesendEmailStaff(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<EmailVO> emailVOs = new ArrayList<>();
		try {
			logger.debug("approveOutagesendEmails(OutageVO outageVO) - start : ", outageVO);
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.APPROVAL_COMPLETE);
			jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.AE_JE_DETAILS);
			List<OutageSearchVO> details = objectMapper.readValue(jsondata,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			if (details != null && details.size() > 0) {
//				emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details,outageVO,IReportConstants.APPROVAL_COMPLETE);
				// System.out.println("outagesss$$");
				httpClientUtil = new HTTPClientUtil();
				outageSearchVO = new OutageSearchVO();
				logger.debug("outageVO.isRescheduled()"+outageVO.isRescheduled());
				if (outageVO.isRescheduled() == true) {
					outageSearchVO.setDisplayname(IReportConstants.OUTAGE_RESCHEDULE_APPROVAL);
				} else {
					outageSearchVO.setDisplayname(IReportConstants.APPROVAL_COMPLETE_STAFF);
				}
				outageSearchVO.setTenantid(outageVO.getTenantId());
				String emailJsonData = httpClientUtil.postRequest(outageSearchVO,
						ApiPortConstant.GET_EMAIL_TEMPLATE_FROM_ADMIN_SERVICE);

				OutageSearchVO outageSearchVOEmail = objectMapper.readValue(emailJsonData, OutageSearchVO.class);
				logger.debug("outageSearchVOEmail.getMessage()"+outageSearchVOEmail.getMessage());
				if (StringUtil.isNotNullOrEmpty(outageSearchVOEmail.getMessage())) {
					emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details, outageVO,
							IReportConstants.APPROVAL_COMPLETE, outageSearchVOEmail.getSubject(),
							outageSearchVOEmail.getMessage());
					httpClientUtil = new HTTPClientUtil();
					jsondata = httpClientUtil.postRequest(emailVOs, ApiPortConstant.SENDMAIL);
				} else {
					throw new Exception("Email Template Configuration Missing" + IReportConstants.APPROVAL_COMPLETE);
				}

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("approveOutagesendEmails(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public OutageVO approveOutagesendEmailsCustomer(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<EmailVO> emailVOs = new ArrayList<>();
		List<OutageSearchVO> details = null;
		try {
			logger.debug("approveOutagesendEmails(OutageVO outageVO) - start : ", outageVO);
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.APPROVAL_COMPLETE);
			outageSearchVO.setTransformer(outageVO.getTransformer());
			System.out.println("outageVO.getTransformer()-->" + outageVO.getTransformer());

			// CUSTOMER
			// System.out.println("outagesss");
			httpClientUtil = new HTTPClientUtil();
			String jsondataString = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.GET_CONNECTIONINFO);
			details = objectMapper.readValue(jsondataString,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			System.out.println("jsondataString-->" + jsondataString);
			System.out.println("details-->" + details.size());
			if (details != null && details.size() > 0) {
//				emailVOs = outageServiceHelper.getConnectionEmailVOforCustomer(details,outageVO,IReportConstants.APPROVAL_COMPLETE);
				httpClientUtil = new HTTPClientUtil();
				outageSearchVO = new OutageSearchVO();
				if (outageVO.isRescheduled() == true) {
					outageSearchVO.setDisplayname(IReportConstants.OUTAGE_RESCHEDULE_APPROVAL_CUSTOMER);
				} else {
					outageSearchVO.setDisplayname(IReportConstants.APPROVAL_COMPLETE_CUSTOMER);
				}

				outageSearchVO.setTenantid(outageVO.getTenantId());
				String emailJsonData = httpClientUtil.postRequest(outageSearchVO,
						ApiPortConstant.GET_EMAIL_TEMPLATE_FROM_ADMIN_SERVICE);

				OutageSearchVO outageSearchVOEmail = objectMapper.readValue(emailJsonData, OutageSearchVO.class);
				if (StringUtil.isNotNullOrEmpty(outageSearchVOEmail.getMessage())) {
					emailVOs = outageServiceHelper.getConnectionEmailVOforCustomer(details, outageVO,
							IReportConstants.APPROVAL_COMPLETE, outageSearchVOEmail.getSubject(),
							outageSearchVOEmail.getMessage());
					httpClientUtil = new HTTPClientUtil();
					jsondata = httpClientUtil.postRequest(emailVOs, ApiPortConstant.SENDMAIL);
				} else {
					throw new Exception("Email Template Configuration Missing" + IReportConstants.APPROVAL_COMPLETE);
				}

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("approveOutagesendEmails(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public OutageVO approvedOutagesendSmsStaff(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<SMSVO> smsVOs = new ArrayList<>();
		try {
			logger.debug("approvedOutagesendSms(OutageVO outageVO) - start : ", outageVO);
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.APPROVAL_COMPLETE);
			jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.AE_JE_DETAILS);
			List<OutageSearchVO> details = objectMapper.readValue(jsondata,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			if (details != null && details.size() > 0) {

				smsVOs = outageServiceHelper.getConnectionSMSVOforStaff(details, outageVO,
						IReportConstants.APPROVAL_COMPLETE);
				httpClientUtil = new HTTPClientUtil();
				jsondata = httpClientUtil.postSMSRequest(smsVOs, ApiPortConstant.SENDSMS);

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("approvedOutagesendSms(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public OutageVO approvedOutagesendSmsCustomer(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<OutageSearchVO> details = null;
		List<SMSVO> smsVOs = new ArrayList<>();
		try {
			logger.debug("approvedOutagesendSms(OutageVO outageVO) - start : ", outageVO);
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.APPROVAL_COMPLETE);

			// CUSTOMER
			httpClientUtil = new HTTPClientUtil();
			String jsondataString = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.GET_CONNECTIONINFO);
			details = objectMapper.readValue(jsondataString,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			// System.out.println("jsondataString-->"+jsondataString);
			// System.out.println("details-->"+details.size());
			if (details != null && details.size() > 0) {

				smsVOs = outageServiceHelper.getConnectionSMSVOforCustomer(details, outageVO,
						IReportConstants.APPROVAL_COMPLETE);
				httpClientUtil = new HTTPClientUtil();
				jsondata = httpClientUtil.postSMSRequest(smsVOs, ApiPortConstant.SENDSMS);

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("approvedOutagesendSms(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	@Override
	public OutageResponseVO outageCompleteV2(OutageV2VO outageVO) throws OutageException {
		OutageResponseVO v2vo = null;
		OutageV2 outage = null;
		OutageNotificationVO outageNotificationVO = null;
		try {
			logger.debug("rejectOutage(OutageVO outageVO) - start : ", outageVO);
			this.getFieldDataValidation(outageVO);
			List<OutageV2> outagelist = outageRepositoryV2.fetchOutagesByOutageId(outageVO.getOutageId());
			if (outagelist != null && outagelist.size() > 0) {
				if (outagelist.size() > 1) {
					throw new OutageException("Duplicate OutageId Found");
				} else {
					for (OutageV2 outageV2 : outagelist) {
						outage = outageV2;
					}
				}
			}
			// OutageV2 outage =
			// outageServiceHelper.copyDataFromOutageVOToUserModel(outageVO);
			if (NumberUtil.isNotNullOrZero(outageVO.getId())) {
				// outage.setLastupdatedby(outageVO.getUserId());
				outage.setLastupdateddate(new Date());
			}
			outage.setStatus(IOmsConstants.COMPLETE_STATUS_DOMAIN);
			outage = outageRepositoryV2.save(outage);
			v2vo = outageServiceHelper.copyDataFromOutageResponseModelToVO(outage);
			v2vo.setStatusCode("CC");
			v2vo.setStatusDesc("Outage Completed Successfully");

			/*
			 * if (StringUtil.isNotNullOrEmpty(outage.getOutageId())) {
			 * 
			 * outageNotificationVO = new OutageNotificationVO();
			 * outageNotificationVO.setStatus(IOmsConstants.COMPLETE_STATUS_BPMN);
			 * outageNotificationVO.setOutageId(outage.getOutageId());
			 * outageNotificationVO.setNetworkElementType(outage.getNetworkElementType());
			 * outageNotificationVO.setNetworkElementUID(outage.getNetworkElementUID());
			 * 
			 * try { sendMailToStaff(outageNotificationVO); } catch (Exception e) {
			 * 
			 * }
			 * 
			 * try { sendMailToCustomer(outageNotificationVO); } catch (Exception e) {
			 * 
			 * }
			 * 
			 * try { sendSmsToStaff(outageNotificationVO); } catch (Exception e) {
			 * 
			 * } try { sendSmsToCustomer(outageNotificationVO); } catch (Exception e) {
			 * 
			 * }
			 * 
			 * }
			 */

			try {
				saveOutageHistory(new OutageHistoryVO(outage.getId(), "", "Outage is Completed", outage.getTenantId(),
						new Date(), outageVO.getUserName()));
			} catch (Exception e) {
				logger.debug(e);
			}
		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		logger.debug("rejectOutage(OutageVO outageVO) - end : ", outageVO);
		return v2vo;
	}

	public OutageVO completeOutagesendEmailstoStaff(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<EmailVO> emailVOs = new ArrayList<>();
		try {
			logger.debug("completeOutagesendEmails(OutageVO outageVO) - start : ", outageVO);
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.CREW_TASK_COMPLETED);
			String jsondataString = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.AE_JE_DETAILS);
			List<OutageSearchVO> details = objectMapper.readValue(jsondataString,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			if (details != null && details.size() > 0) {
//				emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details,outageVO,IReportConstants.CREW_TASK_COMPLETED);
				httpClientUtil = new HTTPClientUtil();
				outageSearchVO = new OutageSearchVO();
				outageSearchVO.setDisplayname(IReportConstants.CREW_TASK_COMPLETED);
				outageSearchVO.setTenantid(outageVO.getTenantId());
				String emailJsonData = httpClientUtil.postRequest(outageSearchVO,
						ApiPortConstant.GET_EMAIL_TEMPLATE_FROM_ADMIN_SERVICE);

				OutageSearchVO outageSearchVOEmail = objectMapper.readValue(emailJsonData, OutageSearchVO.class);
				if (StringUtil.isNotNullOrEmpty(outageSearchVOEmail.getMessage())) {
					emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details, outageVO,
							IReportConstants.CREW_TASK_COMPLETED, outageSearchVOEmail.getSubject(),
							outageSearchVOEmail.getMessage());
					httpClientUtil = new HTTPClientUtil();
					jsondata = httpClientUtil.postRequest(emailVOs, ApiPortConstant.SENDMAIL);
				} else {
					throw new Exception("Email Template Configuration Missing " + IReportConstants.CREW_TASK_COMPLETED);
				}

			}

		} catch (Exception e) {

		}
		logger.debug("completeOutagesendEmails(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public OutageVO completeOutagesendEmailstoCustomer(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<EmailVO> emailVOs = new ArrayList<>();
		try {
			logger.debug("completeOutagesendEmails(OutageVO outageVO) - start : ", outageVO);
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.CREW_TASK_COMPLETED);
			String jsondataString = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.AE_JE_DETAILS);
			List<OutageSearchVO> details = objectMapper.readValue(jsondataString,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));

			// CUSTOMER
			// System.out.println("CUSTOMER--jsondata");
			httpClientUtil = new HTTPClientUtil();
			String jsString = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.GET_CONNECTIONINFO);
			// System.out.println("jsString--jsString"+jsString);
			details = objectMapper.readValue(jsString,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			if (details != null && details.size() > 0) {
//				emailVOs = outageServiceHelper.getConnectionEmailVOforCustomer(details,outageVO,IReportConstants.CREW_TASK_COMPLETED);
				httpClientUtil = new HTTPClientUtil();
				outageSearchVO = new OutageSearchVO();
				outageSearchVO.setDisplayname(IReportConstants.CREW_TASK_COMPLETED_CUSTOMER);
				outageSearchVO.setTenantid(outageVO.getTenantId());
				String emailJsonData = httpClientUtil.postRequest(outageSearchVO,
						ApiPortConstant.GET_EMAIL_TEMPLATE_FROM_ADMIN_SERVICE);

				OutageSearchVO outageSearchVOEmail = objectMapper.readValue(emailJsonData,
						objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
				if (StringUtil.isNotNullOrEmpty(outageSearchVOEmail.getMessage())) {
					emailVOs = outageServiceHelper.getConnectionEmailVOforCustomer(details, outageVO,
							IReportConstants.CREW_TASK_COMPLETED, outageSearchVOEmail.getSubject(),
							outageSearchVOEmail.getMessage());
					httpClientUtil = new HTTPClientUtil();
					jsondata = httpClientUtil.postRequest(emailVOs, ApiPortConstant.SENDMAIL);
				} else {
					throw new Exception(
							"Email Template Configuration Missing" + IReportConstants.CREW_TASK_COMPLETED_CUSTOMER);
				}

				// System.out.println("complete--jsondata1"+jsondata1);

			}

		} catch (Exception e) {

		}
		logger.debug("completeOutagesendEmails(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public OutageVO completeOutagesendSmstoCustomer(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<SMSVO> smsVOs = new ArrayList<>();
		try {
			logger.debug("completeOutagesendSms(OutageVO outageVO) - start : ", outageVO);
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.CREW_TASK_COMPLETED);
			jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.GET_CONNECTIONINFO);
			List<OutageSearchVO> details = objectMapper.readValue(jsondata,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			// System.out.println("details..sms"+details.size());
			if (details != null && details.size() > 0) {

				smsVOs = outageServiceHelper.getConnectionSMSVOforCustomer(details, outageVO,
						IReportConstants.CREW_TASK_COMPLETED);
				httpClientUtil = new HTTPClientUtil();
				String jsondataString = httpClientUtil.postSMSRequest(smsVOs, ApiPortConstant.SENDSMS);

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("completeOutagesendSms(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public List<OutageHistoryVO> searchOutageHistory(OutageHistoryVO outageHistoryVO) throws OutageException {
		Outage object = new Outage();
		List<OutageHistoryVO> outageHistoryVOs = new ArrayList<>();
		List<OutageHistory> outageHistories = new ArrayList<>();
		try {
			logger.debug("searchOutageHistory(OutageHistoryVO outageHistoryVO) - start : ", outageHistoryVO);
			OutageHistory outageHistory = outageServiceHelper.copyOutageHistoryFromVOToModel(outageHistoryVO);
			outageHistories = outageDao.getOutageHistory(outageHistory);
			outageHistoryVOs = outageServiceHelper.copyOutageHistoryDataFromModelToVOs(outageHistories);

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("searchOutageHistory(OutageHistoryVO outageHistoryVO) - end : ", outageHistoryVOs);
		return outageHistoryVOs;
	}

	public OutageHistoryVO saveOutageHistory(OutageHistoryVO outageHistoryVO) throws OutageException {
		try {

			logger.debug("saveOutageHistory(OutageHistoryVO outageHistoryVO) - start : ", outageHistoryVO);
			OutageHistory outageHistory = outageServiceHelper.copyOutageHistoryFromVOToModel(outageHistoryVO);

			outageHistory = outageHistoryRepository.save(outageHistory);
			outageHistoryVO = outageServiceHelper.copyOutageHistoryFromModelToVO(outageHistory);
		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		logger.debug("saveOutageHistory(OutageHistoryVO outageHistoryVO) - end : ", outageHistoryVO);
		return outageHistoryVO;
	}

	public boolean saveApiAudit(String request, String response, int tenantid, String outageid) throws OutageException {
		ApiAuditVO apiAuditVO = new ApiAuditVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		try {
			System.out.println("asyncrynos call");
			apiAuditVO.setRequest(request);
			apiAuditVO.setResponse(response);
			apiAuditVO.setTenantId(tenantid);
			apiAuditVO.setOutageId(outageid);
			apiAuditVO.setCreateddate(new Date());
			apiAuditVO.setLastupdateddate(new Date());

			httpClientUtil = new HTTPClientUtil();
			String jsondataString = httpClientUtil.postApiAuditRequest(apiAuditVO, ApiPortConstant.SAVE_API_AUDIT);

		} catch (Exception e) {
			// TODO: handle exception
		}

		return true;

	}

	public CrewTaskVO saveCrewTask(CrewTaskVO crewTaskVO) throws OutageException {
		try {
			logger.debug("saveJobOrder(JobOrderVO jobOrderVO) - start : ", crewTaskVO);
			CrewTask crewTask = outageServiceHelper.copyDataFromJobOrderVOToJobOrder(crewTaskVO);
			crewTask = taskRepository.save(crewTask);
			crewTaskVO = outageServiceHelper.copyDataFromJobOrderToJobOrderVO(crewTask);
			
			try {
				if(crewTaskVO.getStatus().equalsIgnoreCase("PROPOSED_CLOSURE")) {
					
				}
			} catch (Exception e) {
				logger.error(e);
			}
		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		logger.debug("saveJobOrder(JobOrderVO jobOrderVO) - end : ", crewTaskVO);
		return crewTaskVO;
	}

	public CrewTaskVO completedCrewTask(CrewTaskVO crewTaskVO) throws OutageException {
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		try {
			logger.debug("completedJobOrder(JobOrderVO jobOrderVO) - start : ", crewTaskVO);
			CrewTask order = taskRepository.findById(crewTaskVO.getId());
			if (NumberUtil.isNotNullOrZero(order.getMdmscheck())) {
				crewTaskVO.setStatus(IOmsConstants.COMPLETE_STATUS_DOMAIN);
				CrewTask crewTask = outageServiceHelper.copyDataFromJobOrderVOToJobOrder(crewTaskVO);

				crewTask = taskRepository.save(crewTask);

				crewTaskVO = outageServiceHelper.copyDataFromJobOrderToJobOrderVO(crewTask);
				try {
					if (NumberUtil.isNotNullOrZero(crewTaskVO.getOutageid())) {
						OutageSearchVO outageSearchVO = new OutageSearchVO();
						outageSearchVO.setOutageid(crewTaskVO.getOutageid());
						outageComplete(outageSearchVO);
						// String jsondata = httpClientUtil.postRequest(outageSearchVO,
						// ApiPortConstant.UPDATE_COMPLETE_OUTAGE);

					}
				} catch (Exception e) {

				}

			} else {
				throw new OutageException(
						"METER DATA CHECK FAILED. JOB NOT MARKED COMPLETE AS OUTAGE IS YET TO BE RESOLVED.");
			}

		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		logger.debug("completedJobOrder(JobOrderVO jobOrderVO) - end : ", crewTaskVO);
		return crewTaskVO;
	}

	public CrewTaskVO workinProgressCrewTask(CrewTaskVO crewTaskVO) throws OutageException {
		try {
			logger.debug("workinProgressJobOrder(JobOrderVO jobOrderVO) - start : ", crewTaskVO);
			crewTaskVO.setStatus("WORK IN PROGRESS");

			CrewTask crewTask = outageServiceHelper.copyDataFromJobOrderVOToJobOrder(crewTaskVO);

			crewTask = taskRepository.save(crewTask);

			crewTaskVO = outageServiceHelper.copyDataFromJobOrderToJobOrderVO(crewTask);
			try {
				crewStartedSendMail(crewTaskVO);
			} catch (Exception e) {
				// TODO: handle exception
			}

		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		logger.debug("workinProgressJobOrder(JobOrderVO jobOrderVO) - end : ", crewTaskVO);
		return crewTaskVO;
	}

	public CrewTaskVO plannedCrewTask(CrewTaskVO crewTaskVO) throws OutageException {
		String outageId = "";
		try {

			logger.debug("plannedJobOrder(JobOrderVO jobOrderVO) - start : ", crewTaskVO);
			if (crewTaskVO.isRescheduled()) {
				List<CrewTask> crewTasks = taskRepository.findCrewTaskByOutageId(crewTaskVO.getOutageid());
				if (!crewTasks.isEmpty()) {
					CrewTask crewTask = crewTasks.get(0);
					crewTask.setPlannedStartDate(crewTaskVO.getPlannedStartDate());
					crewTask.setPlannedEndDate(crewTaskVO.getPlannedEndDate());
					crewTask.setJobDetails(crewTaskVO.getJobDetails());
					crewTask = taskRepository.save(crewTask);
				}

			} else {
				crewTaskVO.setStatus(IOmsConstants.PLANNED_STATUS_DOMAIN);
				CrewTask crewTask = outageServiceHelper.copyDataFromJobOrderVOToJobOrder(crewTaskVO);
				crewTask = taskRepository.save(crewTask);
				crewTaskVO = outageServiceHelper.copyDataFromJobOrderToJobOrderVO(crewTask);
			}

			// planndedCrewSendMail(crewTaskVO, outageId);

		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		logger.debug("plannedJobOrder(JobOrderVO jobOrderVO) - end : ", crewTaskVO);
		return crewTaskVO;
	}

	public void crewStartedSendMail(CrewTaskVO crewTaskVO) throws OutageException {
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		String emails = "";
		EmailVO emailVO = new EmailVO();
		List<EmailVO> emailVOs = new ArrayList<>();
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			try {
				logger.debug("crewStartedSendMail(JobOrderVO jobOrderVO) - start : ", crewTaskVO);
				outageSearchVO.setOutageid(crewTaskVO.getOutageid());
				try {
					workinProgressOutage(outageSearchVO);
					// String jsondataString = httpClientUtil.postRequest(outageSearchVO,
					// ApiPortConstant.UPDATE_WORK_IN_PROGRESS_OUTAGE);
				} catch (Exception e) {
					// TODO: handle exception
				}

			} catch (Exception e) {
				logger.error(e);
				// TODO: handle exception
				throw new OutageException(e);
			}
		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
	}

	public void planndedCrewSendMail(CrewTaskVO crewTaskVO, String outageId) throws OutageException {

		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		String userids = "";
		EmailVO emailVO = new EmailVO();
		List<EmailVO> emailVOs = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			try {
				logger.debug("planndedCrewSendMail(JobOrderVO jobOrderVO) - start : ", crewTaskVO);
				if (NumberUtil.isNotNullOrZero(crewTaskVO.getOutageid())) {
					// outage status set as planned

					int crewid = crewTaskVO.getCrewid();
					String jsondata = httpClientUtil
							.getRequest(ApiPortConstant.FIND_CREWS + "/" + crewTaskVO.getCrewid());
					System.out.println("jsondata" + jsondata);
					// CrewVO crewVOs = objectMapper.readValue(jsondata,
					// objectMapper.getTypeFactory().constructCollectionType(List.class,
					// CrewVO.class));

					CrewVO crewVO = objectMapper.readValue(jsondata, CrewVO.class);
					if (crewVO != null) {
						if (!crewVO.getCrewMemberVOs().isEmpty()) {
							for (Iterator iterator = crewVO.getCrewMemberVOs().iterator(); iterator.hasNext();) {
								CrewMemberVO crewMemberVO = (CrewMemberVO) iterator.next();
								if (NumberUtil.isNotNullOrZero(crewMemberVO.getUserid())) {
									userids = userids + crewMemberVO.getUserid() + ",";
								}
							}
						}
					}
					// Crew crew = crewRepository.findById(crewid);
					/*
					 * if (!crew.getCrewMembers().isEmpty()) { for (Iterator iterator =
					 * crew.getCrewMembers().iterator(); iterator.hasNext();) { CrewMember
					 * crewMember = (CrewMember) iterator.next(); if
					 * (StringUtil.isNotNullOrEmpty(crewMember.getEmailid())) { userids = userids +
					 * crewMember.getUserid() + ","; } } }
					 */
					System.out.println("userids" + userids);
					OutageSearchVO outageSearchVO = new OutageSearchVO();
					if (StringUtil.isNotNullOrEmpty(userids)) {
						outageSearchVO.setCrewids(userids.substring(0, userids.length() - 1));
					}
					logger.debug("outageSearchVO.getCrewids" + outageSearchVO.getCrewids());
					outageSearchVO.setOutageid(crewTaskVO.getOutageid());
					try {
						outagePlanned(outageSearchVO);
						// String jsondataString = httpClientUtil.postRequest(outageSearchVO,
						// ApiPortConstant.UPDATE_PLANNED_OUTAGE);
					} catch (Exception e) {
					}
				}
			} catch (Exception e) {
				logger.error(e);
				// TODO: handle exception
				throw new OutageException(e);
			}
		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}

	}

	public List<CrewTaskVO> searchCrewTask(CrewTaskVO crewTaskVO) throws OutageException {
		List<CrewTaskVO> crewTaskVOs = new ArrayList<>();
		try {
			logger.debug("searchJobOrder(JobOrderVO jobOrderVO) - start : ", crewTaskVO);
			CrewTask crewTask = outageServiceHelper.copyDataFromJobOrderVOToJobOrder(crewTaskVO);
			// List<CrewTask> crewTasks = outageDao.searchJobOrder(crewTask);
			List<CrewTask> crewTasks = outageDao.searchCrewTaskByQuery(crewTask);
			crewTaskVOs = outageServiceHelper.copyDataFromJobOrdersToJobOrderVOS(crewTasks);

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("searchJobOrder(JobOrderVO jobOrderVO) - end : ", crewTaskVOs);
		return crewTaskVOs;
	}

	public CrewTaskVO findCrewTask(int id) throws OutageException {
		CrewTaskVO crewTaskVO = null;
		try {
			logger.debug("findJobOrder(int id) - start : ", id);
			CrewTask crewTask = taskRepository.findById(id);

			crewTaskVO = outageServiceHelper.copyDataFromJobOrderToJobOrderVO(crewTask);
			logger.debug("findJobOrder(int id) - end : ", crewTaskVO);

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		return crewTaskVO;
	}

	//

	public OutageExtensionRequestVO saveOutageExtensionRequest(OutageExtensionRequestVO outageExtensionRequestVO)
			throws OutageException {
		try {
			logger.debug("saveOutageExtensionRequest(OutageExtensionRequestVO outageExtensionRequestVO) - start : ",
					outageExtensionRequestVO);
			OutageExtensionRequest outageExtensionRequest = outageServiceHelper
					.copyOutageExtensionRequestDataFromVOToModel(outageExtensionRequestVO);

			outageExtensionRequest = outageExtensionRepository.save(outageExtensionRequest);

			outageExtensionRequestVO = outageServiceHelper
					.copyOutageExtensionRequestDataFromModelToVO(outageExtensionRequest);
			try {
				int outageid = outageExtensionRequest.getOutageId();
				Outage outage = outageRepository.findById(outageid);
				logger.debug("outage.getsubstation"+outage.getSubstation());
				logger.debug("outage.getsubstation"+outage.toString());
				OutageVO outageVO = outageServiceHelper.copyDataFromModelToVO(outage);
				logger.debug("outageVO.getsubstation"+outageVO.getSubstation());
				logger.debug("outageVO.getsubstation"+outageVO.toString());
				outageExtensionRequestEmails(outageVO);
			} catch (Exception e) {
				logger.error(e);
			}
		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		logger.debug("saveOutageExtensionRequest(OutageExtensionRequestVO outageExtensionRequestVO) - end : ",
				outageExtensionRequestVO);
		return outageExtensionRequestVO;
	}

	public List<OutageExtensionRequestVO> getOutageExtensionRequest(OutageExtensionRequestVO outageExtensionRequestVO)
			throws OutageException {
		List<OutageExtensionRequestVO> outageExtensionRequestVOs = new ArrayList<>();
		try {
			logger.debug("getOutageExtensionRequest(JobOrderVO jobOrderVO) - start : ", outageExtensionRequestVO);
			OutageExtensionRequest outageExtensionRequest = outageServiceHelper
					.copyOutageExtensionRequestDataFromVOToModel(outageExtensionRequestVO);
			List<OutageExtensionRequest> outageExtensionRequests = outageDao
					.searchOutageExtensionRequestByQuery(outageExtensionRequest);
			outageExtensionRequestVOs = outageServiceHelper
					.copyOutageExtensionRequestDataFromModelsToVOS(outageExtensionRequests);

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("getOutageExtensionRequest(OutageExtensionRequestVO outageExtensionRequestVO) - end : ",
				outageExtensionRequestVOs);
		return outageExtensionRequestVOs;
	}

	public OutageExtensionRequestVO findOutageExtensionRequest(int id) throws OutageException {
		OutageExtensionRequestVO outageExtensionRequestVO = null;
		try {
			logger.debug("findOutageExtensionRequest(int id) - start : ", id);
			OutageExtensionRequest outageExtensionRequest = outageExtensionRepository.findById(id);

			outageExtensionRequestVO = outageServiceHelper
					.copyOutageExtensionRequestDataFromModelToVO(outageExtensionRequest);
			logger.debug("findOutageExtensionRequest(int id) - end : ", outageExtensionRequestVO);
			if (NumberUtil.isNotNullOrZero(outageExtensionRequestVO.getOutageId())) {
				OutageVO outageVO = new OutageVO();
				OutageSearchVO outageSearchVO = new OutageSearchVO();
				outageSearchVO.setId(outageExtensionRequestVO.getOutageId());
				List<OutageVO> outageVOs = searchOutage(outageSearchVO);
				outageVO = outageVOs.get(0);
				outageExtensionRequestVO.setOutageVO(outageVO);
			}
			if (NumberUtil.isNotNullOrZero(outageExtensionRequestVO.getTaskId())) {
				CrewTaskVO taskVO = findCrewTask(outageExtensionRequestVO.getTaskId());
				outageExtensionRequestVO.setTaskVO(taskVO);
			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		return outageExtensionRequestVO;
	}

	public OutageExtensionRequestVO approveOutageExtensionRequest(OutageExtensionRequestVO outageExtensionRequestVO)
			throws OutageException {
		try {
			logger.debug("saveOutageExtensionRequest(OutageExtensionRequestVO outageExtensionRequestVO) - start : ",
					outageExtensionRequestVO);
			OutageExtensionRequest outageExtensionRequest = outageServiceHelper
					.copyOutageExtensionRequestDataFromVOToModel(outageExtensionRequestVO);

			outageExtensionRequest = outageExtensionRepository.save(outageExtensionRequest);

			outageServiceHelper.copyOutageExtensionRequestDataFromModelToVO(outageExtensionRequest);

			try {
				updateApprovedExtensionRequest(outageExtensionRequestVO);
			} catch (Exception e) {
				logger.error(e);
			}
			try {
				int outageid = outageExtensionRequest.getOutageId();
				Outage outage = outageRepository.findById(outageid);
				OutageVO outageVO = outageServiceHelper.copyDataFromModelToVO(outage);
				outageExtensionApprovalRequestEmails(outageVO);
			} catch (Exception e) {
				logger.error(e);
			}
		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		logger.debug("saveOutageExtensionRequest(OutageExtensionRequestVO outageExtensionRequestVO) - end : ",
				outageExtensionRequestVO);
		return outageExtensionRequestVO;
	}

	public OutageExtensionRequestVO rejectOutageExtensionRequest(OutageExtensionRequestVO outageExtensionRequestVO)
			throws OutageException {
		try {
			logger.debug("saveOutageExtensionRequest(OutageExtensionRequestVO outageExtensionRequestVO) - start : ",
					outageExtensionRequestVO);
			OutageExtensionRequest outageExtensionRequest = outageServiceHelper
					.copyOutageExtensionRequestDataFromVOToModel(outageExtensionRequestVO);

			outageExtensionRequest = outageExtensionRepository.save(outageExtensionRequest);

			outageExtensionRequestVO = outageServiceHelper
					.copyOutageExtensionRequestDataFromModelToVO(outageExtensionRequest);
			try {
				int outageid = outageExtensionRequest.getOutageId();
				Outage outage = outageRepository.findById(outageid);
				OutageVO outageVO = outageServiceHelper.copyDataFromModelToVO(outage);
				outageExtensionRejectRequestEmails(outageVO);
			} catch (Exception e) {
				logger.error(e);
			}
		} catch (Exception e) {
			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		logger.debug("saveOutageExtensionRequest(OutageExtensionRequestVO outageExtensionRequestVO) - end : ",
				outageExtensionRequestVO);
		return outageExtensionRequestVO;
	}

	@Override
	public OutageResponseVO rescheduletOutageV2(OutageV2VO outageVO) throws OutageException {
		OutageResponseVO v2vo = null;
		OutageV2 outage = null;
		OutageNotificationVO outageNotificationVO = null;
		ObjectMapper Obj = new ObjectMapper();
		try {

			logger.debug("confirmOutage(OutageVO outageVO) - start : ", outageVO);
			logger.debug("confirmOutage(OutageVO outageVO) - outageVO.getStatus() : ", outageVO.getStatus());
			this.getFieldDataValidation(outageVO);
			if (StringUtil.isNotNullOrEmpty(this.getValidation(outageVO))) {
				throw new OutageException(this.getValidation(outageVO) + " Fields is Required");
			}
			List<OutageV2> outagelist = outageRepositoryV2.fetchOutagesByOutageId(outageVO.getOutageId());
			if (outagelist != null && outagelist.size() > 0) {
				if (outagelist.size() > 1) {
					throw new OutageException("Duplicate OutageId Found");
				} else {
					for (OutageV2 outageV2 : outagelist) {
						outage = outageV2;
					}
				}
			}
			outage.setStatus(IOmsConstants.RESCHEDULE_STATUS_DOMAIN);
			outage.setLastupdateddate(new Date());
			outage.setIsRescheduled(1);
			outage.setOutageStartTime(outageVO.getOutageStartTime());
			outage.setOutageEndTime(outageVO.getOutageEndTime());
			outage.setReason(outageVO.getReason());
			outage.setReasonType(outageVO.getReasonType());
			outage = outageRepositoryV2.save(outage);
			outageRepositoryV2.updateRescheduleRequest(outage.getId());
			v2vo = outageServiceHelper.copyDataFromOutageResponseModelToVO(outage);
			v2vo.setStatusCode("CC");
			v2vo.setStatusDesc("Outage Rescheduled Successfully");
			// v2vo.setUserId(outageVO.getUserId());
			// v2vo.setAction(outageVO.getAction());
			logger.debug("outage.getOutageId()##", outage.getOutageId());
			/*
			 * if (StringUtil.isNotNullOrEmpty(outage.getOutageId())) {
			 * logger.debug("outage.getOutageId()", outage.getOutageId());
			 * outageNotificationVO = new OutageNotificationVO();
			 * outageNotificationVO.setStatus(IOmsConstants.RESCHEDULE_STATUS_BPMN);
			 * outageNotificationVO.setOutageId(outage.getOutageId());
			 * outageNotificationVO.setNetworkElementType(outage.getNetworkElementType());
			 * outageNotificationVO.setNetworkElementUID(outage.getNetworkElementUID());
			 * 
			 * try { sendMailToStaff(outageNotificationVO); } catch (Exception e) {
			 * 
			 * }
			 * 
			 * try { sendSmsToStaff(outageNotificationVO); } catch (Exception e) {
			 * 
			 * } }
			 */

			try {
				saveOutageHistory(new OutageHistoryVO(outage.getId(), "", "Outage is Rescheduled", outage.getTenantId(),
						new Date(), outageVO.getUserName()));
			} catch (Exception e) {
				logger.debug(e);
			}
			try {
				saveApiAudit(outageVO.getJsonData(), Obj.writeValueAsString(v2vo), outage.getTenantId(),
						outage.getOutageId());
			} catch (Exception e) {
				// TODO: handle exception
			}

		} catch (Exception e) {

			logger.error(e);
			// TODO: handle exception
			throw new OutageException(e);
		}
		logger.debug("confirmOutage(OutageVO outageVO) - end : ", outageVO);
		return v2vo;
	}

	public void updateApprovedExtensionRequest(OutageExtensionRequestVO outageExtensionRequestVO)
			throws OutageException {
		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		try {
			logger.debug("updateApprovedExtensionRequest");
			logger.debug("outageExtensionRequestVO.getRevisedEnd()" + outageExtensionRequestVO.getRevisedEnd());
			logger.debug("outageExtensionRequestVO.getOutageId()" + outageExtensionRequestVO.getOutageId());
			if (DateUtil.isNotNull(outageExtensionRequestVO.getRevisedEnd())) {
				if (NumberUtil.isNotNullOrZero(outageExtensionRequestVO.getOutageId())) {
					int id = outageExtensionRequestVO.getOutageId();
					OutageHistoryVO outageHistoryVO = new OutageHistoryVO();
					OutageV2 outageV2 = outageRepositoryV2.findById(id);
					logger.debug("outageV2" + outageV2.getId());
					outageHistoryVO
							.setDescription("Outage End Date Has been Extended From " + outageV2.getOutageEndTime()
									+ " To " + simpleDateFormat.format(outageExtensionRequestVO.getRevisedEnd()));
					outageV2.setOutageEndTime(outageExtensionRequestVO.getRevisedEnd());
					outageRepositoryV2.save(outageV2);
					outageHistoryVO.setTenantid(outageV2.getTenantId());
					outageHistoryVO.setCreateddate(new Date());
					outageHistoryVO.setOutageid(outageV2.getId());
					outageHistoryVO.setCreatedbyname(outageExtensionRequestVO.getOutageVO().getUserName());
					outageHistoryVO = saveOutageHistory(outageHistoryVO);
					logger.debug("outageHistoryVO" + outageHistoryVO.getId());
				}
				if (NumberUtil.isNotNullOrZero(outageExtensionRequestVO.getTaskId())) {
					int id = outageExtensionRequestVO.getTaskId();
					CrewTask crewTask = taskRepository.findById(id);
					crewTask.setPlannedEndDate(outageExtensionRequestVO.getRevisedEnd());
					taskRepository.save(crewTask);
				}
			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
	}

	@Override
	public Boolean confirmOutageMailSend(OutageExtensionRequestVO outageExtensionRequestVO) throws OutageException {
		// TODO Auto-generated method stub
		return null;
	}

	public OutageVO outageExtensionRequestEmails(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<EmailVO> emailVOs = new ArrayList<>();
		String emailJsonData = "";
		try {
			logger.debug("confirmOutagesendEmails(OutageVO outageVO) - start : ", outageVO);
			System.out.println("confirmOutagesendEmails");
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.OUTAGE_EXTENSION_REQUEST);
			jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.AE_JE_DETAILS);
			System.out.println("confirmOutagesendEmails" + jsondata);
			List<OutageSearchVO> details = objectMapper.readValue(jsondata,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			System.out.println("confirmOutagesendEmails details" + details.size());
			if (details != null && details.size() > 0) {
				try {
					outageSearchVO = new OutageSearchVO();
					outageSearchVO.setDisplayname(IReportConstants.OUTAGE_EXTENSION_REQUEST);
					httpClientUtil = new HTTPClientUtil();
					outageSearchVO.setTenantid(outageVO.getTenantId());
					emailJsonData = httpClientUtil.postRequest(outageSearchVO,
							ApiPortConstant.GET_EMAIL_TEMPLATE_FROM_ADMIN_SERVICE);
					System.out.println("emailJsonData" + emailJsonData);
				} catch (Exception e) {
					// TODO: handle exception
				}

				OutageSearchVO outageSearchVOEmail = objectMapper.readValue(emailJsonData, OutageSearchVO.class);
				System.out.println("outageSearchVOEmail" + outageSearchVOEmail.getMessage());
				if (StringUtil.isNotNullOrEmpty(outageSearchVOEmail.getMessage())) {
					emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details, outageVO,
							IReportConstants.OUTAGE_EXTENSION_REQUEST, outageSearchVOEmail.getSubject(),
							outageSearchVOEmail.getMessage());
					System.out.println("emailVOs.size" + emailVOs.size());
					httpClientUtil = new HTTPClientUtil();
					jsondata = httpClientUtil.postRequest(emailVOs, ApiPortConstant.SENDMAIL);
				} else {
					throw new Exception(
							"Email Template Configuration Missing " + IReportConstants.OUTAGE_EXTENSION_REQUEST);
				}

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("confirmOutagesendEmails(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public OutageVO outageExtensionApprovalRequestEmails(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<EmailVO> emailVOs = new ArrayList<>();
		String emailJsonData = "";
		try {
			logger.debug("confirmOutagesendEmails(OutageVO outageVO) - start : ", outageVO);
			System.out.println("confirmOutagesendEmails");
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.OUTAGE_EXTENSION_REQUEST_APPROVAL);
			jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.AE_JE_DETAILS);
			System.out.println("confirmOutagesendEmails" + jsondata);
			List<OutageSearchVO> details = objectMapper.readValue(jsondata,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			System.out.println("confirmOutagesendEmails details" + details.size());
			if (details != null && details.size() > 0) {
				try {
					outageSearchVO = new OutageSearchVO();
					outageSearchVO.setDisplayname(IReportConstants.OUTAGE_EXTENSION_REQUEST_APPROVAL);
					httpClientUtil = new HTTPClientUtil();
					outageSearchVO.setTenantid(outageVO.getTenantId());
					emailJsonData = httpClientUtil.postRequest(outageSearchVO,
							ApiPortConstant.GET_EMAIL_TEMPLATE_FROM_ADMIN_SERVICE);
					System.out.println("emailJsonData" + emailJsonData);
				} catch (Exception e) {
					// TODO: handle exception
				}

				OutageSearchVO outageSearchVOEmail = objectMapper.readValue(emailJsonData, OutageSearchVO.class);
				System.out.println("outageSearchVOEmail" + outageSearchVOEmail.getMessage());
				if (StringUtil.isNotNullOrEmpty(outageSearchVOEmail.getMessage())) {
					emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details, outageVO,
							IReportConstants.OUTAGE_EXTENSION_REQUEST_APPROVAL, outageSearchVOEmail.getSubject(),
							outageSearchVOEmail.getMessage());
					System.out.println("emailVOs.size" + emailVOs.size());
					httpClientUtil = new HTTPClientUtil();
					jsondata = httpClientUtil.postRequest(emailVOs, ApiPortConstant.SENDMAIL);
				} else {
					throw new Exception("Email Template Configuration Missing "
							+ IReportConstants.OUTAGE_EXTENSION_REQUEST_APPROVAL);
				}

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("confirmOutagesendEmails(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	public OutageVO outageExtensionRejectRequestEmails(OutageVO outageVO) throws OutageException {
		OutageSearchVO outageSearchVO = new OutageSearchVO();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsondata = "";
		List<EmailVO> emailVOs = new ArrayList<>();
		String emailJsonData = "";
		try {
			logger.debug("confirmOutagesendEmails(OutageVO outageVO) - start : ", outageVO);
			System.out.println("confirmOutagesendEmails");
			BeanUtils.copyProperties(outageVO, outageSearchVO);
			outageSearchVO.setOutagetype(IReportConstants.OUTAGE_EXTENSION_REQUEST_REJECTION);
			jsondata = httpClientUtil.postRequest(outageSearchVO, ApiPortConstant.AE_JE_DETAILS);
			System.out.println("confirmOutagesendEmails" + jsondata);
			List<OutageSearchVO> details = objectMapper.readValue(jsondata,
					objectMapper.getTypeFactory().constructCollectionType(List.class, OutageSearchVO.class));
			System.out.println("confirmOutagesendEmails details" + details.size());
			if (details != null && details.size() > 0) {
				try {
					outageSearchVO = new OutageSearchVO();
					outageSearchVO.setDisplayname(IReportConstants.OUTAGE_EXTENSION_REQUEST_REJECTION);
					httpClientUtil = new HTTPClientUtil();
					outageSearchVO.setTenantid(outageVO.getTenantId());
					emailJsonData = httpClientUtil.postRequest(outageSearchVO,
							ApiPortConstant.GET_EMAIL_TEMPLATE_FROM_ADMIN_SERVICE);
					System.out.println("emailJsonData" + emailJsonData);
				} catch (Exception e) {
					// TODO: handle exception
				}

				OutageSearchVO outageSearchVOEmail = objectMapper.readValue(emailJsonData, OutageSearchVO.class);
				System.out.println("outageSearchVOEmail" + outageSearchVOEmail.getMessage());
				if (StringUtil.isNotNullOrEmpty(outageSearchVOEmail.getMessage())) {
					emailVOs = outageServiceHelper.getConnectionEmailVOforStaff(details, outageVO,
							IReportConstants.OUTAGE_EXTENSION_REQUEST_REJECTION, outageSearchVOEmail.getSubject(),
							outageSearchVOEmail.getMessage());
					System.out.println("emailVOs.size" + emailVOs.size());
					httpClientUtil = new HTTPClientUtil();
					jsondata = httpClientUtil.postRequest(emailVOs, ApiPortConstant.SENDMAIL);
				} else {
					throw new Exception("Email Template Configuration Missing "
							+ IReportConstants.OUTAGE_EXTENSION_REQUEST_REJECTION);
				}

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		logger.debug("confirmOutagesendEmails(OutageVO outageVO) - end : ", outageVO);
		return outageVO;
	}

	@Override
	public Boolean approveOutageScheduler(String notificationtype) throws OutageException {
		try {
			List<OutageNotificationVO> notificationVOs = outageDao
					.searchOutageSchedulerByQuery(IOmsConstants.APPROVE_STATUS_DOMAIN, notificationtype);
			for (OutageNotificationVO outageNotificationVO : notificationVOs) {
				if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_MAIL)) {
					try {
						sendMailToStaff(outageNotificationVO);
					} catch (Exception e) {

					}

					try {
						sendMailToCustomer(outageNotificationVO);
					} catch (Exception e) {

					}
					outageRepositoryV2.updateApprovemailts(new Date(), outageNotificationVO.getOutageVO().getId());
				} else if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_SMS)) {
					try {
						sendSmsToStaff(outageNotificationVO);
					} catch (Exception e) {

					}
					try {
						sendSmsToCustomer(outageNotificationVO);
					} catch (Exception e) {

					}
					outageRepositoryV2.updateApprovesmsts(new Date(), outageNotificationVO.getOutageVO().getId());
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return true;
	}

	@Override
	public Boolean rejectOutageScheduler(String notificationtype) throws OutageException {
		try {
			List<OutageNotificationVO> notificationVOs = outageDao
					.searchOutageSchedulerByQuery(IOmsConstants.REJECT_STATUS_DOMAIN, notificationtype);
			for (OutageNotificationVO outageNotificationVO : notificationVOs) {
				if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_MAIL)) {
					sendMailToStaff(outageNotificationVO);
					outageRepositoryV2.updateRejectmailts(new Date(), outageNotificationVO.getOutageVO().getId());
				} else if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_SMS)) {
					sendSmsToStaff(outageNotificationVO);
					outageRepositoryV2.updateRejectsmsts(new Date(), outageNotificationVO.getOutageVO().getId());
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return true;
	}

	@Override
	public Boolean completeOutageScheduler(String notificationtype) throws OutageException {
		try {
			List<OutageNotificationVO> notificationVOs = outageDao
					.searchOutageSchedulerByQuery(IOmsConstants.COMPLETE_STATUS_DOMAIN, notificationtype);
			for (OutageNotificationVO outageNotificationVO : notificationVOs) {
				if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_MAIL)) {
					try {
						sendMailToStaff(outageNotificationVO);
					} catch (Exception e) {

					}

					try {
						sendMailToCustomer(outageNotificationVO);
					} catch (Exception e) {

					}
					outageRepositoryV2.updateCompletemailts(new Date(), outageNotificationVO.getOutageVO().getId());
				} else if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_SMS)) {
					try {
						sendSmsToStaff(outageNotificationVO);
					} catch (Exception e) {

					}
					try {
						sendSmsToCustomer(outageNotificationVO);
					} catch (Exception e) {

					}
					outageRepositoryV2.updateCompletesmsts(new Date(), outageNotificationVO.getOutageVO().getId());
				}

			}
		} catch (Exception e) {
			logger.error(e);
		}
		return true;
	}

	@Override
	public Boolean confirmOutageScheduler(String notificationtype) throws OutageException {
		try {
			List<OutageNotificationVO> notificationVOs = outageDao
					.searchOutageSchedulerByQuery(IOmsConstants.CONFIRM_STATUS_DOMAIN, notificationtype);
			logger.debug("notificationVOs" + notificationVOs.size());
			for (OutageNotificationVO outageNotificationVO : notificationVOs) {
				if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_MAIL)) {
					sendMailToStaff(outageNotificationVO);
					outageRepositoryV2.updateConfirmmailts(new Date(), outageNotificationVO.getOutageVO().getId());
				} else if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_SMS)) {
					sendSmsToStaff(outageNotificationVO);
					outageRepositoryV2.updateConfirmsmsts(new Date(), outageNotificationVO.getOutageVO().getId());
				}

			}
		} catch (Exception e) {
			logger.error(e);
		}
		return true;
	}

	@Override
	public Boolean rescheduleOutageScheduler(String notificationtype) throws OutageException {
		try {
			List<OutageNotificationVO> notificationVOs = outageDao
					.searchOutageSchedulerByQuery(IOmsConstants.RESCHEDULE_STATUS_DOMAIN, notificationtype);
			for (OutageNotificationVO outageNotificationVO : notificationVOs) {
				if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_MAIL)) {
					sendMailToStaff(outageNotificationVO);
					outageRepositoryV2.updateReschedulemailts(new Date(), outageNotificationVO.getOutageVO().getId());
				} else if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_SMS)) {
					sendSmsToStaff(outageNotificationVO);
					outageRepositoryV2.updateReschedulesmsts(new Date(), outageNotificationVO.getOutageVO().getId());
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return true;
	}

	@Override
	public Boolean crewPlannedScheduler(String notificationtype) throws OutageException {
		try {
			List<OutageNotificationVO> notificationVOs = outageDao
					.searchOutageSchedulerByQuery(IOmsConstants.CREWASSIGNED_STATUS_DOMAIN, notificationtype);
			for (OutageNotificationVO outageNotificationVO : notificationVOs) {
				if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_MAIL)) {
					Integer outageid = outageNotificationVO.getOutageVO().getId();
					List<CrewTask> crewTasks = taskRepository.findCrewTaskByOutageId(outageid);
					if (crewTasks != null && crewTasks.size() > 0) {
						CrewTask crewTask = crewTasks.get(0);

						CrewTaskVO crewTaskVO = outageServiceHelper.copyDataFromJobOrderToJobOrderVO(crewTask);
						planndedCrewSendMail(crewTaskVO, outageNotificationVO.getOutageVO().getOutageId());
						outageRepositoryV2.updateReschedulemailts(new Date(),
								outageNotificationVO.getOutageVO().getId());
					}
				} /*
					 * else if
					 * (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_SMS)) {
					 * sendSmsToStaff(outageNotificationVO);
					 * outageRepositoryV2.updateReschedulesmsts(new Date(),
					 * outageNotificationVO.getOutageVO().getId()); }
					 */
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return true;
	}

}
