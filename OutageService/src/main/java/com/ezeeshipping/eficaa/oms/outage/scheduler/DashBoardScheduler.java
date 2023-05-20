package com.ezeeshipping.eficaa.oms.outage.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ezeeshipping.eficaa.oms.constants.IOmsConstants;
import com.ezeeshipping.eficaa.oms.core.logging.AppLogger;
import com.ezeeshipping.eficaa.oms.outage.dashBoardServices.DashBoardService;
import com.ezeeshipping.eficaa.oms.outage.services.OutageException;
import com.ezeeshipping.eficaa.oms.outage.services.OutageService;
import com.ezeeshipping.eficaa.oms.outage.vo.IndicesVO;
import com.ezeeshipping.eficaa.oms.outage.vo.LatestOutagesVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OnGoingAffectedCustomerVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageCountVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageLiveGraphVO;

@Component
public class DashBoardScheduler {
	@Autowired
	  private DashBoardService dashBoardService;
	  private static final AppLogger logger = AppLogger.getLogger(OutageScheduler.class);
	  
	  @Scheduled(cron = "0 */03 * ? * *")
	  public void getOutageCount() throws OutageException
	  {
		  OutageCountVO outageCountVO=new OutageCountVO();
		  try {
			  List<OutageCountVO> outageCountVOs =	dashBoardService.getOutageCount(outageCountVO);
		} catch (Exception e) {
			logger.error(e);
			throw new OutageException();
		}
	  }
	  
	  @Scheduled(cron = "0 */03 * ? * *")
	  public void getOutageLiveGraphData() throws OutageException
	  {
		  OutageLiveGraphVO outageLiveGraphVO=new OutageLiveGraphVO();
		  try {
			  List<OutageLiveGraphVO> outageLiveGraphVOs =	dashBoardService.getOutageLiveGraphData(outageLiveGraphVO);
		} catch (Exception e) {
			logger.error(e);
			throw new OutageException();
		}
	  }
	  
	  @Scheduled(cron = "0 */03 * ? * *")
	  public void getIndices() throws OutageException
	  {
		  IndicesVO indicesVO=new IndicesVO();
		  try {
			  List<IndicesVO> indicesVOs =	dashBoardService.getIndices(indicesVO);
		} catch (Exception e) {
			logger.error(e);
			throw new OutageException();
		}
	  }
	  
	  @Scheduled(cron = "0 */03 * ? * *")
	  public void onGoingAffectedCustomer() throws OutageException
	  {
		  OnGoingAffectedCustomerVO onGoingAffectedCustomerVO=new OnGoingAffectedCustomerVO();
		  try {
			  List<OnGoingAffectedCustomerVO> onGoingAffectedCustomerVOs =	dashBoardService.onGoingAffectedCustomer(onGoingAffectedCustomerVO);
		} catch (Exception e) {
			logger.error(e);
			throw new OutageException();
		}
	  }
	  
	  @Scheduled(cron = "0 */03 * ? * *")
	  public void getLatestOutageDetails() throws OutageException
	  {
		  LatestOutagesVO latestOutagesVO=new LatestOutagesVO();
		  try {
			  List<LatestOutagesVO> latestOutagesVOs =	dashBoardService.getLatestOutageDetails(latestOutagesVO);
		} catch (Exception e) {
			logger.error(e);
			throw new OutageException();
		}
	  }
	  
}
