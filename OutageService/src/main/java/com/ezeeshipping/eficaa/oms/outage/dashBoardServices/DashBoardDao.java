package com.ezeeshipping.eficaa.oms.outage.dashBoardServices;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ezeeshipping.eficaa.oms.admin.model.Transformer;
import com.ezeeshipping.eficaa.oms.commons.vo.OutageSearchVO;
import com.ezeeshipping.eficaa.oms.constants.IOmsConstants;
import com.ezeeshipping.eficaa.oms.core.BaseDao;
import com.ezeeshipping.eficaa.oms.core.logging.AppLogger;
import com.ezeeshipping.eficaa.oms.core.utils.DateUtil;
import com.ezeeshipping.eficaa.oms.core.utils.NumberUtil;
import com.ezeeshipping.eficaa.oms.core.utils.StringUtil;
import com.ezeeshipping.eficaa.oms.outage.model.CrewTask;
import com.ezeeshipping.eficaa.oms.outage.model.Outage;
import com.ezeeshipping.eficaa.oms.outage.model.OutageExtensionRequest;
import com.ezeeshipping.eficaa.oms.outage.model.OutageHistory;
import com.ezeeshipping.eficaa.oms.outage.model.OutageV2;
import com.ezeeshipping.eficaa.oms.outage.vo.DashBoardVO;
import com.ezeeshipping.eficaa.oms.outage.vo.IndicesVO;
import com.ezeeshipping.eficaa.oms.outage.vo.LatestOutagesVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OnGoingAffectedCustomerVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageCountVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageLiveGraphVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageNotificationVO;
import com.ezeeshipping.eficaa.oms.outage.vo.OutageVO;

@Repository
public class DashBoardDao extends BaseDao {
	private static final AppLogger logger = AppLogger.getLogger(DashBoardDao.class);

	
	public List<OutageCountVO> getOutageCount(OutageCountVO outageCountVO) throws DashBoardException {
		List<OutageCountVO> outageCountVOS = new ArrayList();
		SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.DATEFORMAT_YYYYMMDDHHMMSS);
		try {
			String qry = "SELECT count(outage.id) , "
					+ "  (SELECT count(outage.id) as plannedOutage FROM tbl_crewtask  left outer join  tbl_outage outage on outage.id = tbl_crewtask.outageid "
					+ " where  outage.isPlanned=1 and tbl_crewtask.outageid is not null and tbl_crewtask.status='WORK IN PROGRESS'  and  tbl_crewtask.tenantid=1) "
					+ " as plannedOutage,   "
					+ " (SELECT count(outage.id) as unplannedOutage FROM tbl_crewtask  left outer join  tbl_outage outage on outage.id = tbl_crewtask.outageid  "
					+ " where  outage.isPlanned=0 and tbl_crewtask.outageid is not null and tbl_crewtask.status='WORK IN PROGRESS'  and  tbl_crewtask.tenantid=1) "
					+ " as unplannedOutage  " + " FROM tbl_crewtask as crewtask"
					+ " left outer join  tbl_outage outage on outage.id = crewtask.outageid"
					+ "  where crewtask.outageid is not null and crewtask.status='WORK IN PROGRESS' ";

			if (NumberUtil.isNotNullOrZero(outageCountVO.getTenantid())) {
				qry += " and  crewtask.tenantid=" + outageCountVO.getTenantid();
			} else {
				qry += " and  crewtask.tenantid=1";
			}

			System.out.println("qry" + qry);
			logger.debug("qry" + qry);
			Query query = emnative.createNativeQuery(qry);
			List result = query.getResultList();
			System.out.println("result" + result.size());
			if (result != null && result.size() > 0) {
				for (int i = 0; i < result.size(); i++) {
					Object[] row = (Object[]) result.get(i);

					outageCountVO = new OutageCountVO();
					outageCountVO.setTotalOutages(NumberUtil.getInt(row[0]));
					outageCountVO.setPlannedOutages(NumberUtil.getInt(row[1]));
					outageCountVO.setUnPlannedOutages(NumberUtil.getInt(row[2]));
					outageCountVOS.add(outageCountVO);
				}
			}
			emnative.close();

		} catch (Exception e) {
			throw new DashBoardException(e);
		}

		return outageCountVOS;
	}

	public List<OutageLiveGraphVO> getOutageLiveGraphData(OutageLiveGraphVO outageLiveGraphVO) throws DashBoardException {
		List<OutageLiveGraphVO> outageLiveGraphVOs = new ArrayList();
		SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.DATEFORMAT_YYYYMMDDHHMMSS);
		try {
			String qry = "SELECT count(outage.id) , "
					+ "  (SELECT count(outage.id) as plannedOutage FROM tbl_crewtask  left outer join  tbl_outage outage on outage.id = tbl_crewtask.outageid "
					+ " where  outage.isPlanned=1 and tbl_crewtask.outageid is not null and tbl_crewtask.status='WORK IN PROGRESS'  and  tbl_crewtask.tenantid=1) "
					+ " as plannedOutage,   "
					+ " (SELECT count(outage.id) as unplannedOutage FROM tbl_crewtask  left outer join  tbl_outage outage on outage.id = tbl_crewtask.outageid  "
					+ " where  outage.isPlanned=0 and tbl_crewtask.outageid is not null and tbl_crewtask.status='WORK IN PROGRESS'  and  tbl_crewtask.tenantid=1) "
					+ " as unplannedOutage  " + " FROM tbl_crewtask as crewtask"
					+ " left outer join  tbl_outage outage on outage.id = crewtask.outageid"
					+ "  where crewtask.outageid is not null and crewtask.status='WORK IN PROGRESS' ";

			if (NumberUtil.isNotNullOrZero(outageLiveGraphVO.getTenantid())) {
				qry += " and  crewtask.tenantid=" + outageLiveGraphVO.getTenantid();
			} else {
				qry += " and  crewtask.tenantid=1";
			}

			System.out.println("qry" + qry);
			logger.debug("qry" + qry);
			Query query = emnative.createNativeQuery(qry);
			List result = query.getResultList();
			System.out.println("result" + result.size());
			if (result != null && result.size() > 0) {
				for (int i = 0; i < result.size(); i++) {
					Object[] row = (Object[]) result.get(i);
					outageLiveGraphVO = new OutageLiveGraphVO();
					outageLiveGraphVO.setCountOfOutages(NumberUtil.getInt(row[0]));
					outageLiveGraphVOs.add(outageLiveGraphVO);
				}
			}
			emnative.close();

		} catch (Exception e) {
			throw new DashBoardException(e);
		}

		return outageLiveGraphVOs;
	}

	public List<OnGoingAffectedCustomerVO> onGoingAffectedCustomer(OnGoingAffectedCustomerVO onGoingAffectedCustomerVO)
			throws DashBoardException {
		List<OnGoingAffectedCustomerVO> onGoingAffectedCustomerVOs = new ArrayList();
		SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.DATEFORMAT_YYYYMMDDHHMMSS);

		try {
			onGoingAffectedCustomerVO.setCategoryWiseCount(0);
			String qry = "SELECT count(meter.id ),  count(meter.id ) as metercount "

					+ " FROM tbl_crewtask as crewtask"
					+ " left outer join  tbl_outage outage on outage.id = crewtask.outageid"

					+ " left outer join tbl_transformer transformer on transformer.id = outage.transformer"
					+ " left outer join  tbl_connection connection on connection.transformerid=transformer.id "
					+ " left outer join  tbl_customer customer on customer.id=connection.customerid  "
					+ " left outer join  tbl_meter meter on meter.customerid=customer.id "
					+ "  where crewtask.outageid is not null and crewtask.status='WORK IN PROGRESS' and customer.id  is not null and connection.category='R'";

			if (NumberUtil.isNotNullOrZero(onGoingAffectedCustomerVO.getTenantid())) {
				qry += " and  crewtask.tenantid=" + onGoingAffectedCustomerVO.getTenantid();
			} else {
				qry += " and  crewtask.tenantid=1";
			}

			System.out.println("qry" + qry);
			logger.debug("qry" + qry);
			Query query = emnative.createNativeQuery(qry);
			List result = query.getResultList();

			if (result != null && result.size() > 0) {
				for (int i = 0; i < result.size(); i++) {
					Object[] row = (Object[]) result.get(i);
					onGoingAffectedCustomerVO.setDomestic(NumberUtil.getInt(row[0]));
					onGoingAffectedCustomerVO
							.setCategoryWiseCount(onGoingAffectedCustomerVO.getCategoryWiseCount()
									+ onGoingAffectedCustomerVO.getDomestic());
				}
			}

			qry = "SELECT count(meter.id ),  count(meter.id ) as metercount "

					+ " FROM tbl_crewtask as crewtask"
					+ " left outer join  tbl_outage outage on outage.id = crewtask.outageid"

					+ " left outer join tbl_transformer transformer on transformer.id = outage.transformer"
					+ " left outer join  tbl_connection connection on connection.transformerid=transformer.id "
					+ " left outer join  tbl_customer customer on customer.id=connection.customerid  "
					+ " left outer join  tbl_meter meter on meter.customerid=customer.id "
					+ "  where crewtask.outageid is not null and crewtask.status='WORK IN PROGRESS' and customer.id  is not null and connection.category='C'";

			if (NumberUtil.isNotNullOrZero(onGoingAffectedCustomerVO.getTenantid())) {
				qry += " and  crewtask.tenantid=" + onGoingAffectedCustomerVO.getTenantid();
			} else {
				qry += " and  crewtask.tenantid=1";
			}

			System.out.println("qry" + qry);
			logger.debug("qry" + qry);
			Query query1 = emnative.createNativeQuery(qry);
			List result1 = query1.getResultList();

			if (result1 != null && result1.size() > 0) {
				for (int i = 0; i < result1.size(); i++) {
					Object[] row = (Object[]) result1.get(i);
					onGoingAffectedCustomerVO.setCommercial(NumberUtil.getInt(row[0]));
					onGoingAffectedCustomerVO
							.setCategoryWiseCount(onGoingAffectedCustomerVO.getCategoryWiseCount()
									+ onGoingAffectedCustomerVO.getCommercial());
				}
			}

			qry = "SELECT count(meter.id ),  count(meter.id ) as metercount "

					+ " FROM tbl_crewtask as crewtask"
					+ " left outer join  tbl_outage outage on outage.id = crewtask.outageid"

					+ " left outer join tbl_transformer transformer on transformer.id = outage.transformer"
					+ " left outer join  tbl_connection connection on connection.transformerid=transformer.id "
					+ " left outer join  tbl_customer customer on customer.id=connection.customerid  "
					+ " left outer join  tbl_meter meter on meter.customerid=customer.id "
					+ "  where crewtask.outageid is not null and crewtask.status='WORK IN PROGRESS' and customer.id  is not null and connection.category='A'";

			if (NumberUtil.isNotNullOrZero(onGoingAffectedCustomerVO.getTenantid())) {
				qry += " and  crewtask.tenantid=" + onGoingAffectedCustomerVO.getTenantid();
			} else {
				qry += " and  crewtask.tenantid=1";
			}

			System.out.println("qry" + qry);
			logger.debug("qry" + qry);
			Query query2 = emnative.createNativeQuery(qry);
			List result2 = query2.getResultList();

			if (result2 != null && result2.size() > 0) {
				for (int i = 0; i < result2.size(); i++) {
					Object[] row = (Object[]) result2.get(i);
					onGoingAffectedCustomerVO.setOthers(NumberUtil.getInt(row[0]));
					onGoingAffectedCustomerVO
							.setCategoryWiseCount(onGoingAffectedCustomerVO.getCategoryWiseCount()
									+ onGoingAffectedCustomerVO.getOthers());
				}
			}

			qry = "SELECT count(meter.id ),  count(meter.id ) as metercount "

					+ " FROM tbl_crewtask as crewtask"
					+ " left outer join  tbl_outage outage on outage.id = crewtask.outageid"

					+ " left outer join tbl_transformer transformer on transformer.id = outage.transformer"
					+ " left outer join  tbl_connection connection on connection.transformerid=transformer.id "
					+ " left outer join  tbl_customer customer on customer.id=connection.customerid  "
					+ " left outer join  tbl_meter meter on meter.customerid=customer.id "
					+ "  where crewtask.outageid is not null and crewtask.status='WORK IN PROGRESS' and customer.id  is not null and connection.category='T'";

			if (NumberUtil.isNotNullOrZero(onGoingAffectedCustomerVO.getTenantid())) {
				qry += " and  crewtask.tenantid=" + onGoingAffectedCustomerVO.getTenantid();
			} else {
				qry += " and  crewtask.tenantid=1";
			}

			System.out.println("qry" + qry);
			logger.debug("qry" + qry);
			Query query3 = emnative.createNativeQuery(qry);
			List result3 = query3.getResultList();

			if (result3 != null && result3.size() > 0) {
				for (int i = 0; i < result3.size(); i++) {
					Object[] row = (Object[]) result3.get(i);
					onGoingAffectedCustomerVO.setOthers(NumberUtil.getInt(row[0]));
					onGoingAffectedCustomerVO
							.setCategoryWiseCount(onGoingAffectedCustomerVO.getCategoryWiseCount()
									+ onGoingAffectedCustomerVO.getOthers());

				}
			}
			onGoingAffectedCustomerVOs.add(onGoingAffectedCustomerVO);
			emnative.close();

		} catch (Exception e) {
			throw new DashBoardException(e);
		}

		return onGoingAffectedCustomerVOs;
	}

	public List<IndicesVO> getIndices(IndicesVO indicesVO) throws DashBoardException {
		List<IndicesVO> indicesVOs = new ArrayList();
		SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.DATEFORMAT_YYYYMMDDHHMMSS);
		Integer totalNoOfCustomerInteruption = 0;
		Integer totalNoOfCustomers = 0;
		Integer duration = 0;
		Integer totalNoOfMeters = 0;
		try {
			String qry = "SELECT count(customer.id ),  sum(TIMEDIFF(endtime,starttime) ) , count(meter.customerid) "

					+ " FROM tbl_crewtask as crewtask"
					+ " left outer join  tbl_outage outage on outage.id = crewtask.outageid"

					+ " left outer join tbl_transformer transformer on transformer.id = outage.transformer"
					+ " left outer join  tbl_connection connection on connection.transformerid=transformer.id "
					+ " left outer join  tbl_customer customer on customer.id=connection.customerid  "
					+ " left outer join  tbl_meter meter on meter.customerid=customer.id "
					+ "  where crewtask.outageid is not null and crewtask.status='WORK IN PROGRESS' and customer.id  is not null";

			if (NumberUtil.isNotNullOrZero(indicesVO.getTenantid())) {
				qry += " and  crewtask.tenantid=" + indicesVO.getTenantid();
			} else {
				qry += " and  crewtask.tenantid=1";
			}

			System.out.println("qry" + qry);
			logger.debug("qry" + qry);
			Query query = emnative.createNativeQuery(qry);
			List result = query.getResultList();

			if (result != null && result.size() > 0) {
				for (int i = 0; i < result.size(); i++) {
					Object[] row = (Object[]) result.get(i);
					totalNoOfCustomerInteruption = totalNoOfCustomerInteruption + NumberUtil.getInt(row[0]);
					duration = duration + NumberUtil.getInt(row[1]);
					totalNoOfMeters = totalNoOfMeters + NumberUtil.getInt(row[2]);
				}
			}

			String qry1 = "SELECT count(customer.id ) ,  sum(TIMEDIFF(endtime,starttime) ) , count(meter.customerid) "

					+ " FROM tbl_crewtask as crewtask"
					+ " left outer join  tbl_outage outage on outage.id = crewtask.outageid"
					+ " left outer join  tbl_feeder feeder on feeder.id=outage.feeder "
					+ " left outer join tbl_transformer transformer on transformer.id = outage.transformer"
					+ " left outer join  tbl_connection connection on connection.transformerid=transformer.id "
					+ " left outer join  tbl_customer customer on customer.id=connection.customerid  "
					+ " left outer join  tbl_meter meter on meter.customerid=customer.id "
					+ "  where crewtask.outageid is not null and crewtask.status='WORK IN PROGRESS' and customer.id  is not null";

			if (NumberUtil.isNotNullOrZero(indicesVO.getTenantid())) {
				qry1 += " and  crewtask.tenantid=" + indicesVO.getTenantid();
			} else {
				qry1 += " and  crewtask.tenantid=1";
			}

			System.out.println("qry1" + qry1);
			logger.debug("qry1" + qry1);
			Query query1 = emnative.createNativeQuery(qry1);
			List result1 = query1.getResultList();

			if (result1 != null && result1.size() > 0) {
				for (int i = 0; i < result1.size(); i++) {
					Object[] row = (Object[]) result1.get(i);
					totalNoOfCustomerInteruption = totalNoOfCustomerInteruption + NumberUtil.getInt(row[0]);
					duration = duration + NumberUtil.getInt(row[1]);
					totalNoOfMeters = totalNoOfMeters + NumberUtil.getInt(row[2]);
				}
			}

			String qry2 = "SELECT count(customer.id )  ,  sum(TIMEDIFF(endtime,starttime) ) , count(meter.customerid) "

					+ " FROM tbl_crewtask as crewtask"
					+ " left outer join  tbl_outage outage on outage.id = crewtask.outageid"
					+ " left outer join  tbl_substation substation on substation.id=outage.substation "
					+ " left outer join  tbl_feeder feeder on feeder.id=outage.feeder "
					+ " left outer join tbl_transformer transformer on transformer.id = outage.transformer"
					+ " left outer join  tbl_connection connection on connection.transformerid=transformer.id "
					+ " left outer join  tbl_customer customer on customer.id=connection.customerid  "
					+ " left outer join  tbl_meter meter on meter.customerid=customer.id "
					+ "  where crewtask.outageid is not null and crewtask.status='WORK IN PROGRESS' and customer.id  is not null";

			if (NumberUtil.isNotNullOrZero(indicesVO.getTenantid())) {
				qry2 += " and  crewtask.tenantid=" + indicesVO.getTenantid();
			} else {
				qry2 += " and  crewtask.tenantid=1";
			}

			System.out.println("qry2" + qry2);
			logger.debug("qry2" + qry2);
			Query query2 = emnative.createNativeQuery(qry2);
			List result2 = query2.getResultList();

			if (result2 != null && result2.size() > 0) {
				for (int i = 0; i < result2.size(); i++) {
					Object[] row = (Object[]) result2.get(i);
					totalNoOfCustomerInteruption = totalNoOfCustomerInteruption + NumberUtil.getInt(row[0]);
					duration = duration + NumberUtil.getInt(row[1]);
					totalNoOfMeters = totalNoOfMeters + NumberUtil.getInt(row[2]);
				}
			}

			String qryForNumberOfCustomer = "SELECT count(customer.id ),  count(customer.id )  as cus  "

					+ " FROM tbl_customer as customer"
					+ " left outer join tbl_connection connection  on  connection.customerid=customer.id"
					+ " left outer join tbl_transformer transformer on transformer.id=connection.transformerid"
					+ " left outer join  tbl_feeder feeder on feeder.id=transformer.feederid "
					+ " left outer join tbl_substation substation on substation.id = feeder.substationid"
					+ " left outer join tbl_subdivision subdivison on  subdivison.id = substation.subdivisionid"
					+ " left outer join tbl_division division on division.id = subdivison.divisionid" + " where 1=1 "
					+ " ";

			if (NumberUtil.isNotNullOrZero(indicesVO.getTenantid())) {
				qryForNumberOfCustomer += " and  customer.tenantid=" + indicesVO.getTenantid();
			} else {
				qryForNumberOfCustomer += " and  customer.tenantid=1";
			}

			if (StringUtil.isNotNullOrEmpty(indicesVO.getAreaType())) {
//	        	 qry += " and  extenionRequest.isActive=" + outageExtensionRequest.getReasonType();
				if (indicesVO.getAreaType().equalsIgnoreCase("SUB STATION")) {
					qryForNumberOfCustomer += " and  substation.id=" + indicesVO.getAreaId();

				}
				if (indicesVO.getAreaType().equalsIgnoreCase("DIVISION")) {
					qryForNumberOfCustomer += " and  division.id=" + indicesVO.getAreaId();

				}
				if (indicesVO.getAreaType().equalsIgnoreCase("SUB DIVISION")) {
					qryForNumberOfCustomer += " and  subdivison.id=" + indicesVO.getAreaId();

				}
			}

			System.out.println("qry2" + qryForNumberOfCustomer);
			logger.debug("qry2" + qryForNumberOfCustomer);
			Query queryForNumberOfCustomer = emnative.createNativeQuery(qryForNumberOfCustomer);
			List resultNumberOfCustomer = queryForNumberOfCustomer.getResultList();

			if (resultNumberOfCustomer != null && resultNumberOfCustomer.size() > 0) {
				for (int i = 0; i < resultNumberOfCustomer.size(); i++) {
					Object[] row = (Object[]) resultNumberOfCustomer.get(i);
					totalNoOfCustomers = totalNoOfCustomers + NumberUtil.getInt(row[0]);
				}
			}

			System.out.println("totalNoOfCustomers--" + totalNoOfCustomers);
			System.out.println("totalNoOfCustomerInteruption--" + totalNoOfCustomerInteruption);
			indicesVO.setSaifi(totalNoOfCustomerInteruption / totalNoOfCustomers);
			System.out.println("indicesVO.getSAIFI()--" + indicesVO.getSaifi());
			System.out.println("duration--" + duration);
			indicesVO.setSaidi(duration / totalNoOfCustomers);
			System.out.println("indicesVO.getSAIDI()--" + indicesVO.getSaidi());
			if(NumberUtil.isNotNullOrZero(indicesVO.getSaifi())) {
				indicesVO.setCaidi(indicesVO.getSaidi() / indicesVO.getSaifi());
			}else {
				indicesVO.setCaidi(indicesVO.getSaidi());
			}
			if(NumberUtil.isNotNullOrZero(totalNoOfCustomerInteruption)) {
			indicesVO.setCaifi(totalNoOfMeters / totalNoOfCustomerInteruption);
			}else {
				indicesVO.setCaifi(totalNoOfMeters );
			}
			indicesVOs.add(indicesVO);
			emnative.close();

		} catch (Exception e) {
			throw new DashBoardException(e);
		}

		return indicesVOs;
	}

	public List<LatestOutagesVO> getLatestOutageDetails(LatestOutagesVO latestOutagesVO) throws DashBoardException {
		List<LatestOutagesVO> latestOutagesVOs = new ArrayList();
		SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.DATEFORMAT_YYYYMMDDHHMMSS);

		try {
			String qry = "SELECT division.name as divisionname,  subdivison.name as subdivisonname, "
					+ " substation.name as substationname, count(transformer.id) as tid, "
					+ " division , subdivision , substation"

					+ " FROM tbl_crewtask as crewtask"
					+ " left outer join  tbl_outage outage on outage.id = crewtask.outageid"

					+ " left outer join tbl_transformer transformer on transformer.id = outage.transformer"
					+ " left outer join  tbl_feeder feeder on feeder.id=transformer.feederid "
					+ " left outer join tbl_substation substation on substation.id = feeder.substationid"
					+ " left outer join tbl_subdivision subdivison on  subdivison.id = substation.subdivisionid"
					+ " left outer join tbl_division division on division.id = subdivison.divisionid"
					+ "  where crewtask.outageid is not null and crewtask.status='WORK IN PROGRESS'";

			if (NumberUtil.isNotNullOrZero(latestOutagesVO.getTenantid())) {
				qry += " and  crewtask.tenantid=" + latestOutagesVO.getTenantid();
			} else {
				qry += " and  crewtask.tenantid=1";
			}
			qry +=" GROUP BY division.name  ,  subdivison.name  ,substation.name,division , subdivision , substation";

			System.out.println("qry" + qry);
			logger.debug("qry" + qry);
			Query query = emnative.createNativeQuery(qry);
			List result = query.getResultList();

			if (result != null && result.size() > 0) {
				for (int i = 0; i < result.size(); i++) {
//					latestOutagesVO.setAreaStrCount("");
					Object[] row = (Object[]) result.get(i);
					String divisionName = StringUtil.getString(row[0]);
					String subdivisionName = StringUtil.getString(row[1]);
					String substationName = StringUtil.getString(row[2]);
					Integer count = NumberUtil.getInt(row[3]);
					if (StringUtil.isNotNullOrEmpty(divisionName)) {
//						latestOutagesVO.setd(divisionName);
					}
					if (StringUtil.isNotNullOrEmpty(subdivisionName)) {
							latestOutagesVO.setSubDivision(subdivisionName);
					}
					latestOutagesVO.setDtr(NumberUtil.getInt(row[3]));
					latestOutagesVO.setAreaId(NumberUtil.getInt(row[6]));
					latestOutagesVO.setAreaType("SUB STATION");
					latestOutagesVOs.add(latestOutagesVO);
				}
			}

			emnative.close();

		} catch (Exception e) {
			throw new DashBoardException(e);
		}

		return latestOutagesVOs;
	}
}
