package com.ezeeshipping.eficaa.oms.outage.services;

import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;

import com.ezeeshipping.eficaa.oms.commons.vo.EmailVO;
import com.ezeeshipping.eficaa.oms.commons.vo.OutageSearchVO;
import com.ezeeshipping.eficaa.oms.commons.vo.SMSVO;
import com.ezeeshipping.eficaa.oms.constants.EmailTemplateConstants;
import com.ezeeshipping.eficaa.oms.constants.IReportConstants;
import com.ezeeshipping.eficaa.oms.constants.SMSTemplates;
import com.ezeeshipping.eficaa.oms.core.BaseServiceHelper;
import com.ezeeshipping.eficaa.oms.core.logging.AppLogger;
import com.ezeeshipping.eficaa.oms.core.utils.DateUtil;
import com.ezeeshipping.eficaa.oms.core.utils.NumberUtil;
import com.ezeeshipping.eficaa.oms.core.utils.StringUtil;
import com.ezeeshipping.eficaa.oms.outage.model.CrewTask;
import com.ezeeshipping.eficaa.oms.outage.model.Outage;
import com.ezeeshipping.eficaa.oms.outage.model.OutageExtensionRequest;
import com.ezeeshipping.eficaa.oms.outage.model.OutageHistory;
import com.ezeeshipping.eficaa.oms.outage.model.OutageV2;
import com.ezeeshipping.eficaa.oms.outage.vo.CrewTaskVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageDetailResponseV2VO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageExtensionRequestVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageHistoryVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageResponseVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageV2VO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageVO;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class OutageServiceHelper extends BaseServiceHelper {

	private static final AppLogger logger = AppLogger.getLogger(OutageServiceHelper.class);

	public Outage copyDataFromOutageVOToUserModel(OutageVO outageVO) throws OutageException {

		String methodName = "copyDataFromOutageVOToUserModel";

		Outage outage = new Outage();
		try {
			copyObject(outageVO, outage, methodName, new OutageException(), logger, true);

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		return outage;
	}

	public OutageV2 copyDataFromOutageVOToUserModel(OutageV2VO outageVO) throws OutageException {

		String methodName = "copyDataFromOutageVOToUserModel";

		OutageV2 outage = new OutageV2();
		try {
			copyObject(outageVO, outage, methodName, new OutageException(), logger, true);
			if (outageVO.getIsPlanned() == true) {
				outage.setIsPlanned(Short.valueOf("1"));
			} else if (outageVO.getIsPlanned() == false) {
				outage.setIsPlanned(Short.valueOf("0"));
			}
		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		return outage;
	}

	public OutageVO copyDataFromModelToVO(Outage outage) throws OutageException {

		String methodName = "copyDataFromModelToVO";

		OutageVO outageVO = new OutageVO();
		try {
			copyObject(outage, outageVO, methodName, new OutageException(), logger, true);
			if (outage.getIsPlanned() == 1) {
				outageVO.setIsPlanned(true);
			} else if (outage.getIsPlanned() == 0) {
				outageVO.setIsPlanned(false);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}

		return outageVO;
	}

	public OutageDetailResponseV2VO copyDataFromModelToVO(OutageV2 outage) throws OutageException {
		String methodName = "copyDataFromModelToVO";
		OutageDetailResponseV2VO outageVO = new OutageDetailResponseV2VO();
		try {
			copyObject(outage, outageVO, methodName, new OutageException(), logger, true);
			if (outage.getIsPlanned() == 1) {
				outageVO.setIsPlanned(true);
			} else if (outage.getIsPlanned() == 0) {
				outageVO.setIsPlanned(false);
			}
			if (NumberUtil.isNotNullOrZero(outage.getIsRescheduled())) {
				if (outage.getIsRescheduled() == 1) {
					outageVO.setRescheduled(true);
				} else if (outage.getIsRescheduled() == 0) {
					outageVO.setRescheduled(false);
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}

		return outageVO;
	}

	public OutageResponseVO copyDataFromOutageResponseModelToVO(OutageV2 outage) throws OutageException {

		String methodName = "copyDataFromOutageResponseModelToVO";

		OutageResponseVO outageVO = new OutageResponseVO();
		try {
			copyObject(outage, outageVO, methodName, new OutageException(), logger, true);
			if (outage.getIsPlanned() == 1) {
				outageVO.setIsPlanned(true);
			} else if (outage.getIsPlanned() == 0) {
				outageVO.setIsPlanned(false);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}

		return outageVO;
	}

	public List<EmailVO> getConnectionEmailVOforStaff(List<OutageSearchVO> outageSearchVOs, OutageVO outageVO,
			String type, String subject, String message) throws OutageException {
		List<EmailVO> emailVOs = new ArrayList<>();
		EmailVO emailVO = new EmailVO();

		try {
			for (Iterator iterator = outageSearchVOs.iterator(); iterator.hasNext();) {
				OutageSearchVO outageSearchVO = (OutageSearchVO) iterator.next();
				if (StringUtil.isNotNullOrEmpty(outageSearchVO.getEmail())) {

					if (type.equalsIgnoreCase(IReportConstants.OUTAGE_CONFIRM)) {
						String temp = message;
						String sub = subject;
//						String temp = EmailTemplateConstants.OUTAGE_CONFIRM_TEMPLATE_AE;
						emailVO = new EmailVO();
						emailVO.setToemail(outageSearchVO.getEmail());
						temp = replace(temp, "$AREA$", outageVO.getSubstationname());
						temp = replace(temp, "$OUTAGEID$", outageVO.getOutageId());
						temp = replace(temp, "$FROM_DATE$", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						if (outageVO.getEndDateTime() != null) {
							temp = replace(temp, "$TO_DATE$", DateUtil.getDateInString(
									DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
						} else {
							temp = replace(temp, "$TO_DATE$", "");
						}

						temp = replace(temp, "$HOURS$", outageVO.getDuration());
						emailVO.setMsgbody(temp);
						emailVO.setSubject(sub);
//						emailVO.setSubject("OUTAGE CONFIRM");
						emailVOs.add(emailVO);
					}else if (type.equalsIgnoreCase(IReportConstants.OUTAGE_EXTENSION_REQUEST)) {
						String temp = message;
						String sub = subject;
//						String temp = EmailTemplateConstants.OUTAGE_CONFIRM_TEMPLATE_AE;
						emailVO = new EmailVO();
						emailVO.setToemail(outageSearchVO.getEmail());
						temp = replace(temp, "$AREA$", outageVO.getSubstationname());
						temp = replace(temp, "$OUTAGEID$", outageVO.getOutageId());
						temp = replace(temp, "$FROM_DATE$", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						if (outageVO.getEndDateTime() != null) {
							temp = replace(temp, "$TO_DATE$", DateUtil.getDateInString(
									DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
						} else {
							temp = replace(temp, "$TO_DATE$", "");
						}

						temp = replace(temp, "$HOURS$", outageVO.getDuration());
						emailVO.setMsgbody(temp);
						emailVO.setSubject(sub);
//						emailVO.setSubject("OUTAGE CONFIRM");
						emailVOs.add(emailVO);
					}else if (type.equalsIgnoreCase(IReportConstants.OUTAGE_EXTENSION_REQUEST_APPROVAL)) {
						String temp = message;
						String sub = subject;
//						String temp = EmailTemplateConstants.OUTAGE_CONFIRM_TEMPLATE_AE;
						emailVO = new EmailVO();
						emailVO.setToemail(outageSearchVO.getEmail());
						temp = replace(temp, "$AREA$", outageVO.getSubstationname());
						temp = replace(temp, "$OUTAGEID$", outageVO.getOutageId());
						temp = replace(temp, "$FROM_DATE$", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						if (outageVO.getEndDateTime() != null) {
							temp = replace(temp, "$TO_DATE$", DateUtil.getDateInString(
									DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
						} else {
							temp = replace(temp, "$TO_DATE$", "");
						}

						temp = replace(temp, "$HOURS$", outageVO.getDuration());
						emailVO.setMsgbody(temp);
						emailVO.setSubject(sub);
//						emailVO.setSubject("OUTAGE CONFIRM");
						emailVOs.add(emailVO);
					}else if (type.equalsIgnoreCase(IReportConstants.OUTAGE_EXTENSION_REQUEST_APPROVAL)) {
						String temp = message;
						String sub = subject;
//						String temp = EmailTemplateConstants.OUTAGE_CONFIRM_TEMPLATE_AE;
						emailVO = new EmailVO();
						emailVO.setToemail(outageSearchVO.getEmail());
						temp = replace(temp, "$AREA$", outageVO.getSubstationname());
						temp = replace(temp, "$OUTAGEID$", outageVO.getOutageId());
						temp = replace(temp, "$FROM_DATE$", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						if (outageVO.getEndDateTime() != null) {
							temp = replace(temp, "$TO_DATE$", DateUtil.getDateInString(
									DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
						} else {
							temp = replace(temp, "$TO_DATE$", "");
						}

						temp = replace(temp, "$HOURS$", outageVO.getDuration());
						emailVO.setMsgbody(temp);
						emailVO.setSubject(sub);
//						emailVO.setSubject("OUTAGE CONFIRM");
						emailVOs.add(emailVO);
					} else if (type.equalsIgnoreCase(IReportConstants.OUTAGE_EXTENSION_REQUEST_REJECTION)) {
						String temp = message;
						String sub = subject;
//						String temp = EmailTemplateConstants.OUTAGE_CONFIRM_TEMPLATE_AE;
						emailVO = new EmailVO();
						emailVO.setToemail(outageSearchVO.getEmail());
						temp = replace(temp, "$AREA$", outageVO.getSubstationname());
						temp = replace(temp, "$OUTAGEID$", outageVO.getOutageId());
						temp = replace(temp, "$FROM_DATE$", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						if (outageVO.getEndDateTime() != null) {
							temp = replace(temp, "$TO_DATE$", DateUtil.getDateInString(
									DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
						} else {
							temp = replace(temp, "$TO_DATE$", "");
						}

						temp = replace(temp, "$HOURS$", outageVO.getDuration());
						emailVO.setMsgbody(temp);
						emailVO.setSubject(sub);
//						emailVO.setSubject("OUTAGE CONFIRM");
						emailVOs.add(emailVO);
					} else if (type.equalsIgnoreCase(IReportConstants.RESCHEDULE_OUTAGE)) {
						String temp = message;
						String sub = subject;
//						String temp = EmailTemplateConstants.OUTAGE_APPROVED_TEMPLATE_JE;
						emailVO = new EmailVO();
						emailVO.setToemail(outageSearchVO.getEmail());
						temp = replace(temp, "$AREA$", outageVO.getSubstationname());
						temp = replace(temp, "$OUTAGEID$", outageVO.getOutageId());
						temp = replace(temp, "$FROM_DATE$", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						// temp = replace(temp, "$TO_DATE$",
						// DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
						// outageVO.getEndDateTime()));
						if (outageVO.getEndDateTime() != null) {
							temp = replace(temp, "$TO_DATE$", DateUtil.getDateInString(
									DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
						} else {
							temp = replace(temp, "$TO_DATE$", "");
						}
						temp = replace(temp, "$HOURS$", outageVO.getDuration().toString());
						emailVO.setMsgbody(temp);
						emailVO.setSubject(sub);
						emailVOs.add(emailVO);

					} else if (type.equalsIgnoreCase(IReportConstants.OUTAGE_RESCHEDULE_APPROVAL)) {
						String temp = message;
						String sub = subject;
//						String temp = EmailTemplateConstants.OUTAGE_APPROVED_TEMPLATE_JE;
						emailVO = new EmailVO();
						emailVO.setToemail(outageSearchVO.getEmail());
						temp = replace(temp, "$AREA$", outageVO.getSubstationname());
						temp = replace(temp, "$OUTAGEID$", outageVO.getOutageId());
						temp = replace(temp, "$FROM_DATE$", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						// temp = replace(temp, "$TO_DATE$",
						// DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
						// outageVO.getEndDateTime()));
						if (outageVO.getEndDateTime() != null) {
							temp = replace(temp, "$TO_DATE$", DateUtil.getDateInString(
									DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
						} else {
							temp = replace(temp, "$TO_DATE$", "");
						}
						temp = replace(temp, "$HOURS$", outageVO.getDuration().toString());
						emailVO.setMsgbody(temp);
						emailVO.setSubject(sub);
						emailVOs.add(emailVO);

					} else if (type.equalsIgnoreCase(IReportConstants.OUTAGE_RESCHEDULE_REJECTION)) {
//						String temp = EmailTemplateConstants.OUTAGE_REJECT_TEMPLATE_JE;
						String temp = message;
						String sub = subject;
						emailVO = new EmailVO();
						emailVO.setToemail(outageSearchVO.getEmail());
						temp = replace(temp, "$AREA$", outageVO.getSubstationname());
						temp = replace(temp, "$OUTAGEID$", outageVO.getOutageId());
						temp = replace(temp, "$FROM_DATE$", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						// temp = replace(temp, "$TO_DATE$",
						// DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
						// outageVO.getEndDateTime()));
						if (outageVO.getEndDateTime() != null) {
							temp = replace(temp, "$TO_DATE$", DateUtil.getDateInString(
									DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
						} else {
							temp = replace(temp, "$TO_DATE$", "");
						}
						temp = replace(temp, "$HOURS$", outageVO.getDuration().toString());
						emailVO.setMsgbody(temp);
						emailVO.setSubject(sub);
						emailVOs.add(emailVO);
					} else if (type.equalsIgnoreCase(IReportConstants.APPROVAL_COMPLETE)) {
						String temp = message;
						String sub = subject;
//						String temp = EmailTemplateConstants.OUTAGE_APPROVED_TEMPLATE_JE;
						emailVO = new EmailVO();
						emailVO.setToemail(outageSearchVO.getEmail());
						temp = replace(temp, "$AREA$", outageVO.getSubstationname());
						temp = replace(temp, "$OUTAGEID$", outageVO.getOutageId());
						temp = replace(temp, "$FROM_DATE$", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						// temp = replace(temp, "$TO_DATE$",
						// DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
						// outageVO.getEndDateTime()));
						if (outageVO.getEndDateTime() != null) {
							temp = replace(temp, "$TO_DATE$", DateUtil.getDateInString(
									DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
						} else {
							temp = replace(temp, "$TO_DATE$", "");
						}
						temp = replace(temp, "$HOURS$", outageVO.getDuration().toString());
						emailVO.setMsgbody(temp);
						emailVO.setSubject(sub);
						emailVOs.add(emailVO);

					} else if (type.equalsIgnoreCase(IReportConstants.OUTAGE_REJECT)) {
//						String temp = EmailTemplateConstants.OUTAGE_REJECT_TEMPLATE_JE;
						String temp = message;
						String sub = subject;
						emailVO = new EmailVO();
						emailVO.setToemail(outageSearchVO.getEmail());
						temp = replace(temp, "$AREA$", outageVO.getSubstationname());
						temp = replace(temp, "$OUTAGEID$", outageVO.getOutageId());
						temp = replace(temp, "$FROM_DATE$", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						// temp = replace(temp, "$TO_DATE$",
						// DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
						// outageVO.getEndDateTime()));
						if (outageVO.getEndDateTime() != null) {
							temp = replace(temp, "$TO_DATE$", DateUtil.getDateInString(
									DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
						} else {
							temp = replace(temp, "$TO_DATE$", "");
						}
						temp = replace(temp, "$HOURS$", outageVO.getDuration().toString());
						emailVO.setMsgbody(temp);
						emailVO.setSubject(sub);
						emailVOs.add(emailVO);
					} else if (type.equalsIgnoreCase(IReportConstants.CREW_TASK_COMPLETED)) {
//						String temp = EmailTemplateConstants.CREW_TASK_COMPLETED_TEMPLATE_AE_JE;
						String temp = message;
						String sub = subject;
						emailVO = new EmailVO();
						emailVO.setToemail(outageSearchVO.getEmail());
						temp = replace(temp, "$AREA$", outageVO.getSubstationname());
						temp = replace(temp, "$OUTAGEID$", outageVO.getOutageId());
						temp = replace(temp, "$FROM_DATE$", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						if (outageVO.getEndDateTime() != null) {
							temp = replace(temp, "$TO_DATE$", DateUtil.getDateInString(
									DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
						} else {
							temp = replace(temp, "$TO_DATE$", "");
						}

						temp = replace(temp, "$HOURS$", outageVO.getDuration().toString());
						emailVO.setMsgbody(temp);
						emailVO.setSubject(sub);
						emailVOs.add(emailVO);

					} else if (type.equalsIgnoreCase(IReportConstants.CREW_TASK_ASSIGNED)) {
//						String temp = EmailTemplateConstants.CREW_TASK_ASSIGNED_TEMPLATE;
						String temp = message;
						String sub = subject;
						emailVO = new EmailVO();
						emailVO.setToemail(outageSearchVO.getEmail());
						temp = replace(temp, "$AREA$", outageVO.getSubstationname());
						temp = replace(temp, "$OUTAGEID$", outageVO.getOutageId());
						temp = replace(temp, "$FROM_DATE$", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						// temp = replace(temp, "$TO_DATE$",
						// DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
						// outageVO.getEndDateTime()));
						if (outageVO.getEndDateTime() != null) {
							temp = replace(temp, "$TO_DATE$", DateUtil.getDateInString(
									DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
						} else {
							temp = replace(temp, "$TO_DATE$", "");
						}
						temp = replace(temp, "$HOURS$", outageVO.getDuration().toString());
						emailVO.setMsgbody(temp);
						emailVO.setSubject(sub);
						emailVOs.add(emailVO);

					} else if (type.equalsIgnoreCase(IReportConstants.CREW_TASK_STARTED)) {
//						String temp = EmailTemplateConstants.CREW_TASK_STARTED_TEMPLATE_JE;
						String temp = message;
						String sub = subject;
						emailVO = new EmailVO();
						emailVO.setToemail(outageSearchVO.getEmail());
						temp = replace(temp, "$AREA$", outageVO.getSubstationname());
						temp = replace(temp, "$OUTAGEID$", outageVO.getOutageId());
						temp = replace(temp, "$FROM_DATE$", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						// temp = replace(temp, "$TO_DATE$",
						// DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
						// outageVO.getEndDateTime()));
						if (outageVO.getEndDateTime() != null) {
							temp = replace(temp, "$TO_DATE$", DateUtil.getDateInString(
									DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
						} else {
							temp = replace(temp, "$TO_DATE$", "");
						}
						temp = replace(temp, "$HOURS$", outageVO.getDuration().toString());
						emailVO.setMsgbody(temp);
						emailVO.setSubject(sub);
						emailVOs.add(emailVO);
					}
				}

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}

		return emailVOs;
	}

	public List<SMSVO> getConnectionSMSVOforStaff(List<OutageSearchVO> outageSearchVOs, OutageVO outageVO, String type)
			throws OutageException {
		List<SMSVO> smsvos = new ArrayList<>();
		SMSVO smsvo = new SMSVO();

		try {
			// System.out.println("getConnectionSMSVOforStaff");
			for (Iterator iterator = outageSearchVOs.iterator(); iterator.hasNext();) {
				OutageSearchVO outageSearchVO = (OutageSearchVO) iterator.next();
				// System.out.println("outageSearchVO.getMobileno()"+outageSearchVO.getMobileno());
				if (StringUtil.isNotNullOrEmpty(outageSearchVO.getMobileno())) {

					if (type.equalsIgnoreCase(IReportConstants.OUTAGE_CONFIRM)) {
						String temp = SMSTemplates.OUTAGE_CONFIRM_TEMPLATE;
						smsvo = new SMSVO();
						smsvo.setMobileno(outageSearchVO.getMobileno());
						temp = replace(temp, "{AREA}", outageVO.getSubstationname());
						temp = replace(temp, "{START_DT}", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						temp = replace(temp, "{END_DT}", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
						temp = replace(temp, "{HOURS}", outageVO.getDuration().toString());
						smsvo.setMessage(temp);
						smsvos.add(smsvo);
					} else if (type.equalsIgnoreCase(IReportConstants.APPROVAL_COMPLETE)) {
						String temp = SMSTemplates.OUTAGE_APPROVED_TEMPLATE;
						smsvo = new SMSVO();
						smsvo.setMobileno(outageSearchVO.getMobileno());
						temp = replace(temp, "{AREA}", outageVO.getSubstationname());
						temp = replace(temp, "{START_DT}", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						temp = replace(temp, "{END_DT}", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
						temp = replace(temp, "{HOURS}", outageVO.getDuration().toString());
						smsvo.setMessage(temp);
						smsvos.add(smsvo);

						// System.out.println("APPROVED COMPLETE"+smsvo.getMobileno());

					} else if (type.equalsIgnoreCase(IReportConstants.OUTAGE_REJECT)) {
						String temp = SMSTemplates.OUTAGE_REJECT_TEMPLATE;
						smsvo = new SMSVO();
						smsvo.setMobileno(outageSearchVO.getMobileno());
						temp = replace(temp, "{AREA}", outageVO.getSubstationname());
						temp = replace(temp, "{START_DT}", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						temp = replace(temp, "{END_DT}", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
						temp = replace(temp, "{HOURS}", outageVO.getDuration().toString());
						smsvo.setMessage(temp);
						smsvos.add(smsvo);

					}
				}

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}

		return smsvos;
	}

	public List<SMSVO> getConnectionSMSVOforCustomer(List<OutageSearchVO> outageSearchVOs, OutageVO outageVO,
			String type) throws OutageException {
		List<SMSVO> smsvos = new ArrayList<>();
		SMSVO smsvo = new SMSVO();

		try {
			// System.out.println("getConnectionSMSVOforStaff");
			for (Iterator iterator = outageSearchVOs.iterator(); iterator.hasNext();) {
				OutageSearchVO outageSearchVO = (OutageSearchVO) iterator.next();
				// System.out.println("outageSearchVO.getMobileno()"+outageSearchVO.getMobileno());
				if (StringUtil.isNotNullOrEmpty(outageSearchVO.getMobileno())) {

					if (type.equalsIgnoreCase(IReportConstants.APPROVAL_COMPLETE)) {
						String temp = SMSTemplates.OUTAGE_APPROVED_TEMPLATE_CUSTOMER;
						smsvo = new SMSVO();
						smsvo.setMobileno(outageSearchVO.getMobileno());
						temp = replace(temp, "{CONNECTION_NO}", outageSearchVO.getCustomerslno());
						temp = replace(temp, "{START_DT}", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						temp = replace(temp, "{HOURS}", outageVO.getDuration().toString());
						smsvo.setMessage(temp);
						smsvos.add(smsvo);
					} else if (type.equalsIgnoreCase(IReportConstants.CREW_TASK_COMPLETED)) {
						String temp = SMSTemplates.OUTAGE_COMPLETED_TEMPLATE_CUSTOMER;
						smsvo = new SMSVO();
						smsvo.setMobileno(outageSearchVO.getMobileno());
						temp = replace(temp, "{CONNECTION_NO}", outageSearchVO.getCustomerslno());
						temp = replace(temp, "{START_DT}", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						temp = replace(temp, "{HOURS}", outageVO.getDuration().toString());
						smsvo.setMessage(temp);
						smsvos.add(smsvo);

					}
				}

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}

		return smsvos;
	}

	public List<EmailVO> getConnectionEmailVOforCustomer(List<OutageSearchVO> outageSearchVOs, OutageVO outageVO,
			String type, String subject, String message) throws OutageException {
		List<EmailVO> emailVOs = new ArrayList<>();
		EmailVO emailVO = new EmailVO();

		try {
			for (Iterator iterator = outageSearchVOs.iterator(); iterator.hasNext();) {
				OutageSearchVO outageSearchVO = (OutageSearchVO) iterator.next();
				if (StringUtil.isNotNullOrEmpty(outageSearchVO.getEmail())) {

					if (type.equalsIgnoreCase(IReportConstants.APPROVAL_COMPLETE)) {
						String temp = message;
						String sub = subject;
//						String temp = EmailTemplateConstants.OUTAGE_APPROVED_TEMPLATE_CUSTOMER;
						emailVO = new EmailVO();
						emailVO.setToemail(outageSearchVO.getEmail());
						/// temp = replace(temp, "$AREA$", outageVO.getSubstationname());
						// temp = replace(temp, "$FROM_DATE$",
						/// DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
						/// outageVO.getStartDateTime()));
						// temp = replace(temp, "$TO_DATE$",
						/// DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
						/// outageVO.getEndDateTime()));
						temp = replace(temp, "$HOURS$", outageVO.getDuration().toString());
						temp = replace(temp, "$CONNECTIONNO$", outageSearchVO.getCustomerslno());
						temp = replace(temp, "$DATE$", DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
								outageVO.getStartDateTime()));
						temp = replace(temp, "$DATE$", DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
								outageVO.getStartDateTime()));
						
						temp = replace(temp, "$AREA$", outageVO.getSubstationname());
						temp = replace(temp, "$OUTAGEID$", outageVO.getOutageId());
						temp = replace(temp, "$FROM_DATE$", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						// temp = replace(temp, "$TO_DATE$",
						// DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
						// outageVO.getEndDateTime()));
						if (outageVO.getEndDateTime() != null) {
							temp = replace(temp, "$TO_DATE$", DateUtil.getDateInString(
									DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
						} else {
							temp = replace(temp, "$TO_DATE$", "");
						}
						temp = replace(temp, "$HOURS$", outageVO.getDuration().toString());
						
						emailVO.setMsgbody(temp);
						emailVO.setSubject(sub);
						emailVOs.add(emailVO);

					} else if (type.equalsIgnoreCase(IReportConstants.CREW_TASK_COMPLETED)) {
//						 String temp = EmailTemplateConstants.OUTAGE_COMPLETED_TEMPLATE;
						String temp = message;
						String sub = subject;
						emailVO = new EmailVO();
						emailVO.setToemail(outageSearchVO.getEmail());
						temp = replace(temp, "$CONNECTIONNO$", outageSearchVO.getCustomerslno());
						temp = replace(temp, "$AREA$", outageVO.getSubstationname());
						temp = replace(temp, "$OUTAGEID$", outageVO.getOutageId());
						temp = replace(temp, "$FROM_DATE$", DateUtil
								.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
						// temp = replace(temp, "$TO_DATE$",
						// DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
						// outageVO.getEndDateTime()));
						if (outageVO.getEndDateTime() != null) {
							temp = replace(temp, "$TO_DATE$", DateUtil.getDateInString(
									DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
						} else {
							temp = replace(temp, "$TO_DATE$", "");
						}
						temp = replace(temp, "$HOURS$", outageVO.getDuration().toString());
						emailVO.setMsgbody(temp);
						emailVO.setSubject(sub);
						emailVOs.add(emailVO);
					}
				}

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}

		return emailVOs;
	}

	public EmailVO getReplaceEmailTemplate(OutageSearchVO outageSearchVO, OutageVO outageVO, String type)
			throws OutageException {
		EmailVO emailVO = new EmailVO();
		try {
			if (outageSearchVO.getOutagetype().equalsIgnoreCase(IReportConstants.OUTAGE_CONFIRM)) {
				String temp = EmailTemplateConstants.OUTAGE_CONFIRM_TEMPLATE_AE;

				temp = replace(temp, "$AREA$", outageVO.getSubstationname());
				temp = replace(temp, "$FROM_DATE$",
						DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
				// temp = replace(temp, "$TO_DATE$",
				// DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
				// outageVO.getEndDateTime()));
				if (outageVO.getEndDateTime() != null) {
					temp = replace(temp, "$TO_DATE$", DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
							outageVO.getEndDateTime()));
				} else {
					temp = replace(temp, "$TO_DATE$", "");
				}
				temp = replace(temp, "$HOURS$", outageVO.getDuration().toString());

			} else if (outageSearchVO.getOutagetype().equalsIgnoreCase(IReportConstants.APPROVAL_COMPLETE)) {
				String temp = EmailTemplateConstants.OUTAGE_APPROVED_TEMPLATE_JE;
				temp = replace(temp, "$AREA$", outageVO.getSubstationname());
				temp = replace(temp, "$FROM_DATE$",
						DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
				// temp = replace(temp, "$TO_DATE$",
				// DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
				// outageVO.getEndDateTime()));
				if (outageVO.getEndDateTime() != null) {
					temp = replace(temp, "$TO_DATE$", DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
							outageVO.getEndDateTime()));
				} else {
					temp = replace(temp, "$TO_DATE$", "");
				}
				temp = replace(temp, "$HOURS$", outageVO.getDuration().toString());

			} else if (outageSearchVO.getOutagetype().equalsIgnoreCase(IReportConstants.OUTAGE_REJECT)) {
				String temp = EmailTemplateConstants.OUTAGE_REJECT_TEMPLATE_JE;
				temp = replace(temp, "$AREA$", outageVO.getSubstationname());
				temp = replace(temp, "$FROM_DATE$",
						DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
				// temp = replace(temp, "$TO_DATE$",
				// DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
				// outageVO.getEndDateTime()));
				if (outageVO.getEndDateTime() != null) {
					temp = replace(temp, "$TO_DATE$", DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
							outageVO.getEndDateTime()));
				} else {
					temp = replace(temp, "$TO_DATE$", "");
				}
				temp = replace(temp, "$HOURS$", outageVO.getDuration().toString());
			} else if (outageSearchVO.getOutagetype().equalsIgnoreCase(IReportConstants.OUTAGE_COMPLETED)) {
				String temp = EmailTemplateConstants.OUTAGE_COMPLETED_TEMPLATE;
				temp = replace(temp, "$AREA$", outageVO.getSubstationname());
				temp = replace(temp, "$FROM_DATE$",
						DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getStartDateTime()));
				temp = replace(temp, "$TO_DATE$",
						DateUtil.getDateInString(DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS, outageVO.getEndDateTime()));
				temp = replace(temp, "$HOURS$", outageVO.getDuration().toString());

			} else if (outageSearchVO.getOutagetype().equalsIgnoreCase(IReportConstants.CREW_TASK_ASSIGNED)) {

			} else if (outageSearchVO.getOutagetype().equalsIgnoreCase(IReportConstants.CREW_TASK_STARTED)) {

			} else if (outageSearchVO.getOutagetype().equalsIgnoreCase(IReportConstants.CREW_TASK_COMPLETED)) {

			}

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}

		return emailVO;
	}

	String replace(String message, String key, String value) {

		if (!StringUtil.hasValue(message))
			return message;
		if (!StringUtil.hasValue(key))
			return message;

		if (message.contains(key.toUpperCase())) {
			if (StringUtil.hasValue(value))
				message = message.replace(key.toUpperCase(), value);
			else
				message = message.replace(key.toUpperCase(), "");

		} else if (message.contains(key)) {
			if (StringUtil.hasValue(value))
				message = message.replace(key, value);
			else
				message = message.replace(key, "");

		}
		return message;
	}

	public String getReplaceCriticalErrorMsg(OutageVO outageVO, String connectionnos) throws OutageException {
		String errorstr = "";
		try {
			errorstr = "This $substation$/$Feeder$/$Transformer$ is having critical loads (Connection Nos- $connectionno$). Hence outage cannot be created within the next 10 days. Please plan later.";
			errorstr = replace(errorstr, "$substation$", outageVO.getSubstationname());
			errorstr = replace(errorstr, "$Feeder$", outageVO.getFeedername());
			errorstr = replace(errorstr, "$Transformer$", outageVO.getTransformername());
			errorstr = replace(errorstr, "$connectionno$", connectionnos);

		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}

		return errorstr;
	}

	public OutageHistory copyOutageHistoryFromVOToModel(OutageHistoryVO outageHistoryVO) throws OutageException {

		String methodName = "copyOutageHistoryFromVOToModel";
		OutageHistory outageHistory = new OutageHistory();

		try {

			copyObject(outageHistoryVO, outageHistory, methodName, new OutageException(), logger, false);

		} catch (Exception e) {
			throw new OutageException(e);
		}

		return outageHistory;
	}

	public OutageHistoryVO copyOutageHistoryFromModelToVO(OutageHistory outageHistory) throws OutageException {

		String methodName = "copyOutageHistoryFromModelToVO";
		OutageHistoryVO outageHistoryVO = new OutageHistoryVO();

		try {
			copyObject(outageHistory, outageHistoryVO, methodName, new OutageException(), logger, false);

		} catch (Exception e) {
			throw new OutageException(e);
		}

		return outageHistoryVO;
	}

	public List<OutageHistoryVO> copyOutageHistoryDataFromModelToVOs(List<OutageHistory> outageHistories)
			throws OutageException {
		String methodName = "copyOutageHistoryDataFromModelToVOs";

		List<OutageHistoryVO> outageHistoryVOs = new ArrayList();
		OutageHistoryVO outageHistoryVO = null;
		OutageHistory outageHistory = null;
		try {
			for (int i = 0; i < outageHistories.size(); i++) {
				outageHistory = outageHistories.get(i);
				outageHistoryVO = copyOutageHistoryFromModelToVO(outageHistory);
				outageHistoryVO.setCreatedByName(outageHistoryVO.getCreatedbyname());
				outageHistoryVOs.add(outageHistoryVO);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		return outageHistoryVOs;
	}

	public CrewTask copyDataFromJobOrderVOToJobOrder(CrewTaskVO crewTaskVO) throws OutageException {

		String methodName = "copyDataFromJobOrderVOToJobOrder";

		CrewTask crewTask = new CrewTask();
		try {
			// BeanUtils.copyProperties(jobOrderVO, jobOrder);
			copyObject(crewTaskVO, crewTask, methodName, new OutageException(), logger, true);
		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}

		return crewTask;
	}

	public CrewTaskVO copyDataFromJobOrderToJobOrderVO(CrewTask crewTask) throws OutageException {

		String methodName = "copyDataFromJobOrderToJobOrderVO";

		CrewTaskVO crewTaskVO = new CrewTaskVO();
		try {
			// BeanUtils.copyProperties(jobOrder, jobOrderVO);
			copyObject(crewTask, crewTaskVO, methodName, new OutageException(), logger, true);
		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}

		return crewTaskVO;
	}

	public List<CrewTaskVO> copyDataFromJobOrdersToJobOrderVOS(List<CrewTask> crewTasks) throws OutageException {
		List<CrewTaskVO> crewTaskVOs = new ArrayList<CrewTaskVO>();
		try {
			for (Iterator it = crewTasks.iterator(); it.hasNext();) {
				CrewTask crewTask = (CrewTask) it.next();
				crewTaskVOs.add(copyDataFromJobOrderToJobOrderVO(crewTask));
			}
		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		return crewTaskVOs;
	}

	public OutageExtensionRequest copyOutageExtensionRequestDataFromVOToModel(
			OutageExtensionRequestVO outageExtensionRequestVO) throws OutageException {

		String methodName = "copyOutageExtensionRequestDataFromModelToVO";

		OutageExtensionRequest outageExtensionRequest = new OutageExtensionRequest();
		try {
			// BeanUtils.copyProperties(jobOrderVO, jobOrder);
			copyObject(outageExtensionRequestVO, outageExtensionRequest, methodName, new OutageException(), logger,
					true);
		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}

		return outageExtensionRequest;
	}

	public OutageExtensionRequestVO copyOutageExtensionRequestDataFromModelToVO(
			OutageExtensionRequest outageExtensionRequest) throws OutageException {

		String methodName = "copyOutageExtensionRequestDataFromModelToVO";

		OutageExtensionRequestVO outageExtensionRequestVO = new OutageExtensionRequestVO();
		try {
			// BeanUtils.copyProperties(jobOrder, jobOrderVO);
			copyObject(outageExtensionRequest, outageExtensionRequestVO, methodName, new OutageException(), logger,
					true);
		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}

		return outageExtensionRequestVO;
	}

	public List<OutageExtensionRequestVO> copyOutageExtensionRequestDataFromModelsToVOS(
			List<OutageExtensionRequest> outageExtensionRequests) throws OutageException {
		List<OutageExtensionRequestVO> outageExtensionRequestVOs = new ArrayList<OutageExtensionRequestVO>();
		try {
			for (Iterator it = outageExtensionRequests.iterator(); it.hasNext();) {
				OutageExtensionRequest outageExtensionRequest = (OutageExtensionRequest) it.next();
				outageExtensionRequestVOs.add(copyOutageExtensionRequestDataFromModelToVO(outageExtensionRequest));
			}
		} catch (Exception e) {
			logger.error(e);
			throw new OutageException(e);
		}
		return outageExtensionRequestVOs;
	}

}
