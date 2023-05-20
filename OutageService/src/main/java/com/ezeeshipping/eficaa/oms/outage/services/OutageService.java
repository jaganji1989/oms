package com.ezeeshipping.eficaa.oms.outage.services;

import java.util.Date;
import java.util.List;

import com.ezeeshipping.eficaa.oms.commons.vo.OutageSearchVO;
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
  
public interface OutageService {

	public List<OutageVO> searchOutage(OutageSearchVO outageSearchVO) throws OutageException;

	public OutageVO saveOutage(OutageVO outageVO) throws OutageException;

	public OutageVO findOutagev1(int id) throws OutageException;

	public OutageVO approvedOutage(OutageVO outageVO) throws OutageException;

	public OutageVO confirmOutage(OutageVO outageVO) throws OutageException;

	public OutageVO rejectOutage(OutageVO outageVO) throws OutageException;

	public OutageVO outagePlanned(OutageSearchVO outageSearchVO) throws OutageException;

	public OutageVO outageComplete(OutageSearchVO outageSearchVO) throws OutageException;

	public OutageVO workinProgressOutage(OutageSearchVO outageSearchVO) throws OutageException;

	public List<OutageVO> findAllOutage() throws OutageException;

	public List<OutageDetailResponseV2VO> searchOutageV2(OutageSearchVO outageSearchVO) throws OutageException;

	public OutageResponseVO saveOutageV2(OutageV2VO outageV2VO) throws OutageException;

	public OutageResponseVO updateOutageV2(OutageV2VO outageV2VO) throws OutageException;

	public OutageDetailResponseV2VO findOutageV2(String outageId) throws OutageException;

	public OutageResponseVO approvedOutageV2(OutageV2VO outageV2VO) throws OutageException;

	public OutageResponseVO confirmOutageV2(OutageV2VO outageV2VO) throws OutageException;

	public OutageResponseVO cancellOutageV2(OutageV2VO outageV2VO) throws OutageException;

	public OutageResponseVO rejectOutageV2(OutageV2VO outageV2VO) throws OutageException;

	public OutageResponseVO rescheduletOutageV2(OutageV2VO outageV2VO) throws OutageException;

	public OutageResponseVO sendMailToStaff(OutageNotificationVO outageNotificationVO) throws OutageException;

	public OutageResponseVO sendMailToCustomer(OutageNotificationVO outageNotificationVO) throws OutageException;

	public OutageResponseVO sendSmsToStaff(OutageNotificationVO outageNotificationVO) throws OutageException;

	public OutageResponseVO sendSmsToCustomer(OutageNotificationVO outageNotificationVO) throws OutageException;

	public OutageResponseVO outageCompleteV2(OutageV2VO outageV2VO) throws OutageException;

	public List<OutageHistoryVO> searchOutageHistory(OutageHistoryVO outageHistoryVO) throws OutageException;

	public CrewTaskVO completedCrewTask(CrewTaskVO crewTaskVO) throws OutageException;

	public CrewTaskVO workinProgressCrewTask(CrewTaskVO crewTaskVO) throws OutageException;

	public CrewTaskVO plannedCrewTask(CrewTaskVO crewTaskVO) throws OutageException;

	public CrewTaskVO saveCrewTask(CrewTaskVO crewTaskVO) throws OutageException;

	public List<CrewTaskVO> searchCrewTask(CrewTaskVO crewTaskVO) throws OutageException;

	public CrewTaskVO findCrewTask(int id) throws OutageException;

	public OutageExtensionRequestVO findOutageExtensionRequest(int id) throws OutageException;

	public List<OutageExtensionRequestVO> getOutageExtensionRequest(OutageExtensionRequestVO outageExtensionRequestVO)
			throws OutageException;

	public OutageExtensionRequestVO saveOutageExtensionRequest(OutageExtensionRequestVO outageExtensionRequestVO)
			throws OutageException;

	public OutageExtensionRequestVO approveOutageExtensionRequest(OutageExtensionRequestVO outageExtensionRequestVO)
			throws OutageException;

	public OutageExtensionRequestVO rejectOutageExtensionRequest(OutageExtensionRequestVO outageExtensionRequestVO)
			throws OutageException;

	public Boolean confirmOutageMailSend(OutageExtensionRequestVO outageExtensionRequestVO) throws OutageException;

	public Boolean confirmOutageScheduler(String notificationtype) throws OutageException;

	public Boolean approveOutageScheduler(String notificationtype) throws OutageException;

	public Boolean rejectOutageScheduler(String notificationtype) throws OutageException;

	public Boolean rescheduleOutageScheduler(String notificationtype) throws OutageException;

	public Boolean completeOutageScheduler(String notificationtype) throws OutageException;
	
	public Boolean crewPlannedScheduler(String notificationtype) throws OutageException;
	
	
}
