package com.ezeeshipping.eficaa.oms.outage.dashBoardServices;

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

public interface DashBoardService {

	
	
	public  List<OutageCountVO> getOutageCount(OutageCountVO outageCountVO) throws DashBoardException;
	
	public List<OutageLiveGraphVO> getOutageLiveGraphData(OutageLiveGraphVO outageLiveGraphVO) throws DashBoardException ;
	
	public List<IndicesVO> getIndices(IndicesVO indicesVO) throws DashBoardException;
	
	public List<OnGoingAffectedCustomerVO> onGoingAffectedCustomer(OnGoingAffectedCustomerVO onGoingAffectedCustomerVO) throws DashBoardException ;

	public List<LatestOutagesVO> getLatestOutageDetails(LatestOutagesVO latestOutagesVO) throws DashBoardException ;
}
