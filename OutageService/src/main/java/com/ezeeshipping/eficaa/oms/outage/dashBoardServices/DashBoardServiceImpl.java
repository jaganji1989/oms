package com.ezeeshipping.eficaa.oms.outage.dashBoardServices;

import java.math.BigDecimal;
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
public class DashBoardServiceImpl extends BaseServiceImpl implements DashBoardService {

	private DashBoardDao dashBoardDao;
	

	@Autowired
	EntityManagerFactory emf;

	private static final AppLogger logger = AppLogger.getLogger(DashBoardServiceImpl.class);

	@Autowired
	public DashBoardServiceImpl(DashBoardDao dashBoardDao 
			) {

		this.dashBoardDao = dashBoardDao;
		
	}

	
	public List<OutageCountVO> getOutageCount(OutageCountVO outageCountVO) throws DashBoardException {
		List<OutageCountVO> outageCountVOS = new ArrayList();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		try {
			outageCountVOS = dashBoardDao.getOutageCount(outageCountVO);
			for(int i=0;i<outageCountVOS.size();i++) {
				System.out.println("outageLiveGraphVOs.size()" + outageCountVOS.size());
				OutageCountVO dummyoutageCountVO=outageCountVOS.get(i);
				String jsondata = httpClientUtil.postRequest(dummyoutageCountVO, ApiPortConstant.SAVE_OUTAGE_COUNT);
				System.out.println("jsondata" + jsondata);

			}
		} catch (Exception e) {
			logger.error(e);
		}
		return outageCountVOS;
	}
	
	public List<OutageLiveGraphVO> getOutageLiveGraphData(OutageLiveGraphVO outageLiveGraphVO) throws DashBoardException {
		List<OutageLiveGraphVO> outageLiveGraphVOs = new ArrayList();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		try {
			outageLiveGraphVOs = dashBoardDao.getOutageLiveGraphData(outageLiveGraphVO);
			for(int i=0;i<outageLiveGraphVOs.size();i++) {
				System.out.println("outageLiveGraphVOs.size()" + outageLiveGraphVOs.size());
				OutageLiveGraphVO dummyoutageLiveGraphVO=outageLiveGraphVOs.get(i);
				String jsondata = httpClientUtil.postRequest(dummyoutageLiveGraphVO, ApiPortConstant.SAVE_OUTAGE_LIVE_GRAPH);
				System.out.println("jsondata" + jsondata);

			}
		} catch (Exception e) {
			logger.error(e);
		}
		return outageLiveGraphVOs;
	}

	public List<IndicesVO> getIndices(IndicesVO indicesVO) throws DashBoardException {
		List<IndicesVO> indicesVOs = new ArrayList();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		try {
			indicesVOs = dashBoardDao.getIndices(indicesVO);
			for(int i=0;i<indicesVOs.size();i++) {
				System.out.println("indicesVOs.size()" + indicesVOs.size());
				IndicesVO dummyindicesVO=indicesVOs.get(i);
				String jsondata = httpClientUtil.postRequest(dummyindicesVO, ApiPortConstant.SAVE_INDICES);
				System.out.println("jsondata" + jsondata);

			}
		} catch (Exception e) {
			logger.error(e);
		}
		return indicesVOs;
	}
	public List<OnGoingAffectedCustomerVO> onGoingAffectedCustomer(OnGoingAffectedCustomerVO onGoingAffectedCustomerVO) throws DashBoardException {
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		List<OnGoingAffectedCustomerVO> onGoingAffectedCustomerVOs = new ArrayList();
		try {
			onGoingAffectedCustomerVOs = dashBoardDao.onGoingAffectedCustomer(onGoingAffectedCustomerVO);
			for(int i=0;i<onGoingAffectedCustomerVOs.size();i++) {
				System.out.println("onGoingAffectedCustomerVOs.size()" + onGoingAffectedCustomerVOs.size());
				OnGoingAffectedCustomerVO dummyonGoingAffectedCustomerVO=onGoingAffectedCustomerVOs.get(i);
				String jsondata = httpClientUtil.postRequest(dummyonGoingAffectedCustomerVO, ApiPortConstant.SAVE_ONGOING_EFFECTED_CUSTOMER);
				System.out.println("jsondata" + jsondata);

			}
		} catch (Exception e) {
			logger.error(e);
		}
		return onGoingAffectedCustomerVOs;
	}
	
	public List<LatestOutagesVO> getLatestOutageDetails(LatestOutagesVO latestOutagesVO) throws DashBoardException {
		List<LatestOutagesVO> latestOutagesVOs = new ArrayList();
		ObjectMapper objectMapper = new ObjectMapper();
		HTTPClientUtil httpClientUtil = new HTTPClientUtil();
		try {
			latestOutagesVOs = dashBoardDao.getLatestOutageDetails(latestOutagesVO);
			for(int i=0;i<latestOutagesVOs.size();i++) {
				System.out.println("latestOutagesVOs.size()" + latestOutagesVOs.size());
				LatestOutagesVO dummyLatestOutagesVO=latestOutagesVOs.get(i);
				String jsondata = httpClientUtil.postRequest(dummyLatestOutagesVO, ApiPortConstant.SAVE_LATEST_OUTAGES_DETAILS);
				System.out.println("jsondata" + jsondata);

			}

		} catch (Exception e) {
			logger.error(e);
		}
		return latestOutagesVOs;
	}

}
