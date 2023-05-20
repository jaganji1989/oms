/**
 * 
 */
package com.ezeeshipping.eficaa.oms.outage.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ezeeshipping.eficaa.oms.constants.IOmsConstants;
import com.ezeeshipping.eficaa.oms.core.logging.AppLogger;
import com.ezeeshipping.eficaa.oms.outage.services.OutageException;
import com.ezeeshipping.eficaa.oms.outage.services.OutageService;


/**
 * @author Dell
 *
 */
/**
 * @author Dell
 *
 */
@Component
public class OutageScheduler {
	
  @Autowired
  private OutageService outageService;
  private static final AppLogger logger = AppLogger.getLogger(OutageScheduler.class);
  
  //@Scheduled(cron = "0 */3 * ? * *")
  public void confirmOutageMailScheduler() throws OutageException
  {
	  try {
		System.out.println("confirmOutageMailScheduler");
		Boolean  b =	outageService.confirmOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_MAIL);
	} catch (Exception e) {
		logger.error(e);
		throw new OutageException();
	}
  }
 //@Scheduled(cron = "0 */3 * ? * *")
  public void confirmOutageSmsScheduler() throws OutageException
  {
	  try {
		System.out.println("confirmOutageSmsScheduler");
		Boolean  b =	outageService.confirmOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_SMS);
	} catch (Exception e) {
		logger.error(e);
		throw new OutageException();
	}
  }
  
 // @Scheduled(cron = "0 */3 * ? * *")
  public void approveOutageSmsScheduler() throws OutageException
  {
	  try {
		System.out.println("approveOutageSmsScheduler");
		Boolean  b =	outageService.approveOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_SMS);
	} catch (Exception e) {
		logger.error(e);
		throw new OutageException();
	}
  }
 // @Scheduled(cron = "0 */3 * ? * *")
  public void approveOutageMailScheduler() throws OutageException
  {
	  try {
		System.out.println("approveOutageMailScheduler");
		Boolean  b =	outageService.approveOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_MAIL);
	} catch (Exception e) {
		logger.error(e);
		throw new OutageException();
	}
  }
  
  //@Scheduled(cron = "0 */3 * ? * *")
  public void rejectOutageMailScheduler() throws OutageException
  {
	  try {
		System.out.println("rejectOutageMailScheduler");
		Boolean  b =	outageService.rejectOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_MAIL);
	} catch (Exception e) {
		logger.error(e);
		throw new OutageException();
	}
  }
  
  //@Scheduled(cron = "0 */3 * ? * *")
  public void rejectOutageSmsScheduler() throws OutageException
  {
	  try {
		System.out.println("rejectOutageSmsScheduler");
		Boolean  b =	outageService.rejectOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_SMS);
	} catch (Exception e) {
		logger.error(e);
		throw new OutageException();
	}
  }
  //@Scheduled(cron = "0 */3 * ? * *")
  public void rescheduleOutageSmsScheduler() throws OutageException
  {
	  try {
		System.out.println("rescheduleOutageSmsScheduler");
		Boolean  b =	outageService.rescheduleOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_SMS);
	} catch (Exception e) {
		logger.error(e);
		throw new OutageException();
	}
  }
//  @Scheduled(cron = "0 */3 * ? * *")
  public void rescheduleOutageMailScheduler() throws OutageException
  {
	  try {
		System.out.println("rescheduleOutageMailScheduler");
		Boolean  b =	outageService.rescheduleOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_MAIL);
	} catch (Exception e) {
		logger.error(e);
		throw new OutageException();
	}
  }
 // @Scheduled(cron = "0 */3 * ? * *")
  public void completeOutageMailScheduler() throws OutageException
  {
	  try {
		System.out.println("completeOutageMailScheduler");
	Boolean  b =	outageService.completeOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_MAIL);
	} catch (Exception e) {
		logger.error(e);
		throw new OutageException();
	}
  }
 //@Scheduled(cron = "0 */3 * ? * *")
  public void completeOutageSmsScheduler() throws OutageException
  {
	  try {
		System.out.println("completeOutageSmsScheduler");
		Boolean  b =	outageService.completeOutageScheduler(IOmsConstants.NOTIFICATION_TYPE_SMS);
	} catch (Exception e) {
		logger.error(e);
		throw new OutageException();
	}
  } 
 // @Scheduled(cron = "0 */3 * ? * *")
  public void crewPlannedMailScheduler() throws OutageException
  {
	  try {
		System.out.println("completeOutageSmsScheduler");
		Boolean  b =	outageService.crewPlannedScheduler(IOmsConstants.NOTIFICATION_TYPE_MAIL);
	} catch (Exception e) {
		logger.error(e);
		throw new OutageException();
	}
  }
  
  //@Scheduled(cron = "0 */3 * ? * *")
  public void crewPlannedSmsScheduler() throws OutageException
  {
	  try {
		System.out.println("completeOutageSmsScheduler");
		Boolean  b =	outageService.crewPlannedScheduler(IOmsConstants.NOTIFICATION_TYPE_SMS);
	} catch (Exception e) {
		logger.error(e);
		throw new OutageException();
	}
  }
}
