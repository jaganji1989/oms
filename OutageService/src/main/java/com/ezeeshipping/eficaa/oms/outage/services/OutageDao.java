package com.ezeeshipping.eficaa.oms.outage.services;

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
public class OutageDao extends BaseDao {
	private static final AppLogger logger = AppLogger.getLogger(OutageDao.class);

	public List<Outage> searchOutage(OutageSearchVO outageVO) throws OutageException {
		List<Outage> list = new ArrayList();
		try {
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			List<Predicate> predicates = new ArrayList<>();

			CriteriaQuery<Outage> criteriaQuery = criteriaBuilder.createQuery(Outage.class);
			Root rootTransitOrder = criteriaQuery.from(Outage.class);
			if (NumberUtil.isNotNullOrZero(outageVO.getId())) {
				predicates.add(criteriaBuilder.equal(rootTransitOrder.get("id"), outageVO.getId()));
			}
			if (StringUtil.isNotNullOrEmpty(outageVO.getName())) {
				predicates.add(criteriaBuilder.equal(rootTransitOrder.get("outageName"), outageVO.getName()));
			}
			if (StringUtil.isNotNullOrEmpty(outageVO.getStatus())) {
				predicates.add(criteriaBuilder.equal(rootTransitOrder.get("status"), outageVO.getStatus()));
			}
			if (outageVO.getStartDateTimeFrom() != null && outageVO.getStartDateTimeTo() != null) {
				predicates.add(criteriaBuilder.between(rootTransitOrder.get("startDateTime"),
						outageVO.getStartDateTimeFrom(), outageVO.getStartDateTimeTo()));

			}

			if (outageVO.getEndDateTimeFrom() != null && outageVO.getEndDateTimeTo() != null) {
				predicates.add(criteriaBuilder.between(rootTransitOrder.get("endDateTime"),
						outageVO.getEndDateTimeFrom(), outageVO.getEndDateTimeTo()));

			}

			criteriaQuery.where(predicates.toArray(new Predicate[0]));
			TypedQuery<Outage> query = em.createQuery(criteriaQuery);

			list = query.getResultList();
			em.close();

		} catch (Exception e) {
			throw new OutageException(e);
		}

		return list;
	}

	public List<OutageV2> searchOutagev2(OutageSearchVO outageVO) throws OutageException {
		List<OutageV2> list = new ArrayList();
		try {
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			List<Predicate> predicates = new ArrayList<>();

			CriteriaQuery<OutageV2> criteriaQuery = criteriaBuilder.createQuery(OutageV2.class);
			Root rootTransitOrder = criteriaQuery.from(OutageV2.class);
			if (NumberUtil.isNotNullOrZero(outageVO.getId())) {
				predicates.add(criteriaBuilder.equal(rootTransitOrder.get("id"), outageVO.getId()));
			}
			if (StringUtil.isNotNullOrEmpty(outageVO.getName())) {
				predicates.add(criteriaBuilder.equal(rootTransitOrder.get("outageName"), outageVO.getName()));
			}
			if (StringUtil.isNotNullOrEmpty(outageVO.getOutageId())) {
				predicates.add(criteriaBuilder.equal(rootTransitOrder.get("outageId"), outageVO.getOutageId()));
			}
			if (StringUtil.isNotNullOrEmpty(outageVO.getStatus())) {
				predicates.add(criteriaBuilder.equal(rootTransitOrder.get("status"), outageVO.getStatus()));
			}
			if (outageVO.getStartDateTimeFrom() != null && outageVO.getStartDateTimeTo() != null) {
				predicates.add(criteriaBuilder.between(rootTransitOrder.get("outageStartTime"),
						outageVO.getStartDateTimeFrom(), outageVO.getStartDateTimeTo()));

			}

			if (outageVO.getEndDateTimeFrom() != null && outageVO.getEndDateTimeTo() != null) {
				predicates.add(criteriaBuilder.between(rootTransitOrder.get("outageEndTime"),
						outageVO.getEndDateTimeFrom(), outageVO.getEndDateTimeTo()));

			}
			logger.debug("outageVO.getIsPlanned(####)" + outageVO.getIsPlanned());
			if (NumberUtil.isNotNull(outageVO.getIsPlanned()) && outageVO.getIsPlanned() != 2) {
				logger.debug("outageVO.getIsPlanned(####)" + outageVO.getIsPlanned());
				predicates.add(criteriaBuilder.equal(rootTransitOrder.get("isPlanned"), outageVO.getIsPlanned()));
			}
			if (NumberUtil.isNotNull(outageVO.getTenantid())) {
				predicates.add(criteriaBuilder.equal(rootTransitOrder.get("tenantId"), outageVO.getTenantid()));
			}
			if (NumberUtil.isNotNull(outageVO.getSubdivision())) {
				predicates.add(criteriaBuilder.equal(rootTransitOrder.get("subdivision"), outageVO.getSubdivision()));
			}
			if (NumberUtil.isNotNull(outageVO.getSubstation())) {
				predicates.add(criteriaBuilder.equal(rootTransitOrder.get("substation"), outageVO.getSubstation()));
			}
			if (NumberUtil.isNotNull(outageVO.getDivision())) {
				predicates.add(criteriaBuilder.equal(rootTransitOrder.get("division"), outageVO.getDivision()));
			}
			criteriaQuery.where(predicates.toArray(new Predicate[0]));
			TypedQuery<OutageV2> query = em.createQuery(criteriaQuery);

			list = query.getResultList();
			em.close();

		} catch (Exception e) {
			throw new OutageException(e);
		}

		return list;
	}

	public List<OutageHistory> getOutageHistory(OutageHistory outageHistory) throws OutageException {
		List<OutageHistory> outageHistories = new ArrayList();
		try {
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			List<Predicate> predicates = new ArrayList<>();
			CriteriaQuery<OutageHistory> criteriaQuery = criteriaBuilder.createQuery(OutageHistory.class);
			Root root = criteriaQuery.from(OutageHistory.class);
			if (NumberUtil.isNotNullOrZero(outageHistory.getId())) {
				predicates.add(criteriaBuilder.equal(root.get("id"), outageHistory.getId()));
			}

			if (NumberUtil.isNotNullOrZero(outageHistory.getTenantid())) {
				predicates.add(criteriaBuilder.equal(root.get("tenantid"), outageHistory.getTenantid()));
			}
			if (NumberUtil.isNotNullOrZero(outageHistory.getOutageid())) {
				predicates.add(criteriaBuilder.equal(root.get("outageid"), outageHistory.getOutageid()));
			}

			criteriaQuery.where(predicates.toArray(new Predicate[0]));
			TypedQuery<OutageHistory> query = em.createQuery(criteriaQuery);

			outageHistories = query.getResultList();
			em.close();

		} catch (Exception e) {
			throw new OutageException(e);
		}

		return outageHistories;
	}

	public List<CrewTask> searchJobOrder(CrewTask crewTask) throws OutageException {
		List<CrewTask> crewTasks = new ArrayList();
		try {
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			List<Predicate> predicates = new ArrayList<>();

			CriteriaQuery<CrewTask> criteriaQuery = criteriaBuilder.createQuery(CrewTask.class);
			Root root = criteriaQuery.from(CrewTask.class);
			if (NumberUtil.isNotNullOrZero(crewTask.getId())) {
				predicates.add(criteriaBuilder.equal(root.get("id"), crewTask.getId()));
			}
			if (NumberUtil.isNotNullOrZero(crewTask.getTenantid())) {
				predicates.add(criteriaBuilder.equal(root.get("tenantid"), crewTask.getTenantid()));
			}
			if (NumberUtil.isNotNullOrZero(crewTask.getOutageid())) {
				predicates.add(criteriaBuilder.equal(root.get("outageid"), crewTask.getOutageid()));
			}
			criteriaQuery.where(predicates.toArray(new Predicate[0]));
			TypedQuery<CrewTask> query = em.createQuery(criteriaQuery);

			crewTasks = query.getResultList();
			em.close();

		} catch (Exception e) {
			throw new OutageException(e);
		}

		return crewTasks;
	}

	public List<OutageExtensionRequest> getOutageExtensionRequest(OutageExtensionRequest outageExtensionRequest)
			throws OutageException {
		List<OutageExtensionRequest> outageExtensionRequests = new ArrayList();
		try {
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			List<Predicate> predicates = new ArrayList<>();
			CriteriaQuery<OutageExtensionRequest> criteriaQuery = criteriaBuilder
					.createQuery(OutageExtensionRequest.class);
			Root root = criteriaQuery.from(OutageExtensionRequest.class);
			if (NumberUtil.isNotNullOrZero(outageExtensionRequest.getId())) {
				predicates.add(criteriaBuilder.equal(root.get("id"), outageExtensionRequest.getId()));
			}

			if (NumberUtil.isNotNullOrZero(outageExtensionRequest.getTenantid())) {
				predicates.add(criteriaBuilder.equal(root.get("tenantid"), outageExtensionRequest.getTenantid()));
			}
			if (NumberUtil.isNotNullOrZero(outageExtensionRequest.getOutageId())) {
				predicates.add(criteriaBuilder.equal(root.get("outageId"), outageExtensionRequest.getOutageId()));
			}
			if (NumberUtil.isNotNullOrZero(outageExtensionRequest.getTaskId())) {
				predicates.add(criteriaBuilder.equal(root.get("taskId"), outageExtensionRequest.getTaskId()));
			}
			if (StringUtil.isNotNullOrEmpty(outageExtensionRequest.getStatus())) {
				predicates.add(criteriaBuilder.equal(root.get("status"), outageExtensionRequest.getStatus()));
			}
			if (StringUtil.isNotNullOrEmpty(outageExtensionRequest.getReasonType())) {
				predicates.add(criteriaBuilder.equal(root.get("reasonType"), outageExtensionRequest.getReasonType()));
			}
			criteriaQuery.where(predicates.toArray(new Predicate[0]));
			TypedQuery<OutageExtensionRequest> query = em.createQuery(criteriaQuery);

			outageExtensionRequests = query.getResultList();
			em.close();

		} catch (Exception e) {
			throw new OutageException(e);
		}

		return outageExtensionRequests;
	}

	public List<OutageExtensionRequest> searchOutageExtensionRequestByQuery(
			OutageExtensionRequest outageExtensionRequest) throws OutageException {
		List<OutageExtensionRequest> extensionRequests = new ArrayList();
		try {
			String qry = "SELECT extenionRequest.id,extenionRequest.outageId" + " ,extenionRequest.taskId,"
					+ " extenionRequest.remarks,extenionRequest.reasonType,extenionRequest.status ,"
					+ " extenionRequest.revisedEnd,extenionRequest.approverRemarks,"
					+ " outage.outageId as uid ,crewtask.crewname" // ,crew.name as crewname
					+ " FROM OutageExtensionRequest extenionRequest"
					+ " left outer join Outage outage on outage.id =extenionRequest.outageId"
					+ " left outer join CrewTask crewtask on crewtask.id =extenionRequest.taskId"
//					+ " left outer join Crew crewname on crewname.id =crewtask.crewid"
//					+ " left outer join User user on user.id =extenionRequest.createdby"
			;

			// + " where transformer.tenantid=" + transformer.getTenantid();
			if (NumberUtil.isNotNullOrZero(outageExtensionRequest.getTenantid())) {
				qry += " where  extenionRequest.tenantid=" + outageExtensionRequest.getTenantid();
			}
			if (NumberUtil.isNotNullOrZero(outageExtensionRequest.getId())) {
				qry += " and  extenionRequest.id=" + outageExtensionRequest.getId();
			}
			if (NumberUtil.isNotNullOrZero(outageExtensionRequest.getOutageId())) {
				qry += " and  extenionRequest.outageId=" + outageExtensionRequest.getOutageId();
			}
			if (NumberUtil.isNotNullOrZero(outageExtensionRequest.getTaskId())) {
				qry += " and  extenionRequest.taskId=" + outageExtensionRequest.getTaskId();
			}

			if (StringUtil.isNotNullOrEmpty(outageExtensionRequest.getStatus())) {
				qry += " and  extenionRequest.status='" + outageExtensionRequest.getStatus() + "'";
			}
			if (StringUtil.isNotNullOrEmpty(outageExtensionRequest.getReasonType())) {
				qry += " and  extenionRequest.reasonType='" + outageExtensionRequest.getReasonType() + "'";
			}
			if (StringUtil.isNotNullOrEmpty(outageExtensionRequest.getAreatype())) {
//			        	 qry += " and  extenionRequest.isActive=" + outageExtensionRequest.getReasonType();
				if (outageExtensionRequest.getAreatype().equalsIgnoreCase("SUB STATION")) {
					qry += " and  outage.substation=" + outageExtensionRequest.getAreaid();
				}
				if (outageExtensionRequest.getAreatype().equalsIgnoreCase("DIVISION")) {
					qry += " and  outage.division=" + outageExtensionRequest.getAreaid();
				}
				if (outageExtensionRequest.getAreatype().equalsIgnoreCase("SUB DIVISION")) {
					qry += " and  outage.subdivision=" + outageExtensionRequest.getAreaid();
				}
			}

			Query query = em.createQuery(qry);
			List result = query.getResultList();
			System.out.println("result" + result.size());
			if (result != null && result.size() > 0) {
				for (int i = 0; i < result.size(); i++) {
					Object[] row = (Object[]) result.get(i);
					outageExtensionRequest = new OutageExtensionRequest();
					outageExtensionRequest.setId(NumberUtil.getInt(row[0]));
					outageExtensionRequest.setOutageId(NumberUtil.getInt(row[1]));
					outageExtensionRequest.setTaskId(NumberUtil.getInt(row[2]));
					outageExtensionRequest.setRemarks(StringUtil.getString(row[3]));
					outageExtensionRequest.setReasonType(StringUtil.getString(row[4]));
					outageExtensionRequest.setStatus(StringUtil.getString(row[5]));
					outageExtensionRequest.setRevisedEnd(row[6] != null ? (Date) row[6] : null);
					outageExtensionRequest.setApproverRemarks(StringUtil.getString(row[7]));
					outageExtensionRequest.setOutageUID(StringUtil.getString(row[8]));
					outageExtensionRequest.setCrewName(StringUtil.getString(row[9]));
//					outageExtensionRequest.setCreatedByName(StringUtil.getString(row[10]));
//					

					extensionRequests.add(outageExtensionRequest);
				}
			}
			em.close();

		} catch (Exception e) {
			throw new OutageException(e);
		}

		return extensionRequests;
	}

	public List<OutageV2> searchOutageV2ByQuery(OutageSearchVO outageSearchVO) throws OutageException {
		List<OutageV2> outageV2s = new ArrayList();

		try {
			String qry = "SELECT outage.id,outage.outageid," + "outage.name,"
					+ " outage.tenantid,outage.username,outage.processid ," + " outage.sourcesystem,outage.isplanned,"
					+ " outage.reasontype,outage.networkelementtype,outage.networkelementuid,"
					+ " outage.starttime,outage.endtime,outage.duration,outage.status,"
					+ " division.name as divisionname,transformer.name as transformername,substation.name as substationname,"
					+ " feeder.name as feedername,subdivision.name as subdivisionname, "
					+ " (SELECT group_concat(crewtask.status separator ', ') as crewtaskstatus from tbl_crewtask crewtask where crewtask.outageid = outage.id group by crewtask.outageid) as crewtaskstatus,"
					+ " outage.reason" + " FROM tbl_outage outage"
					// + " left outer join tbl_crewtask crewtask on crewtask.outageid =outage.id"
					+ " left outer join tbl_division division on division.id = outage.division"
					+ " left outer join tbl_substation substation on substation.id = outage.substation"
					+ " left outer join tbl_subdivision subdivision on subdivision.id = outage.subdivision"
					+ " left outer join tbl_feeder feeder on feeder.id = outage.feeder"
					+ " left outer join tbl_transformer transformer on transformer.id = outage.transformer";

			// + " where transformer.tenantid=" + transformer.getTenantid();
			if (NumberUtil.isNotNullOrZero(outageSearchVO.getTenantid())) {
				qry += " where  outage.tenantid=" + outageSearchVO.getTenantid();
			} else {
				qry += " where  outage.tenantid=1";
			}
			if (NumberUtil.isNotNullOrZero(outageSearchVO.getId())) {
				qry += " and  outage.id=" + outageSearchVO.getId();
			}
			if (StringUtil.isNotNullOrEmpty(outageSearchVO.getOutageId())) {
				qry += " and  outage.outageid='" + outageSearchVO.getOutageId() + "'";
			}
			if (StringUtil.isNotNullOrEmpty(outageSearchVO.getName())) {
				qry += " and  outage.name='" + outageSearchVO.getName() + "'";
			}
			if (StringUtil.isNotNullOrEmpty(outageSearchVO.getStatus())) {
				qry += " and  outage.status='" + outageSearchVO.getStatus() + "'";
			}

			if (outageSearchVO.getStartDateTimeFrom() != null && outageSearchVO.getStartDateTimeTo() != null) {
				qry += " and  outage.status='" + outageSearchVO.getStatus() + "'";
			}

			if (outageSearchVO.getEndDateTimeFrom() != null && outageSearchVO.getEndDateTimeTo() != null) {
				qry += " and  outage.status='" + outageSearchVO.getStatus() + "'";
			}
			if (NumberUtil.isNotNull(outageSearchVO.getIsPlanned()) && outageSearchVO.getIsPlanned() != 2) {
				qry += " and  outage.isplanned=" + outageSearchVO.getIsPlanned();
			}
			if (NumberUtil.isNotNull(outageSearchVO.getSubdivision())) {
				qry += " and  outage.subdivision=" + outageSearchVO.getSubdivision();
			}
			if (NumberUtil.isNotNull(outageSearchVO.getSubstation())) {
				qry += " and  outage.substation=" + outageSearchVO.getSubstation();
			}
			if (NumberUtil.isNotNull(outageSearchVO.getDivision())) {
				qry += " and  outage.division=" + outageSearchVO.getDivision();
			}

			if (outageSearchVO.isProposeClosure() == true) {
				// qry += " and crewtask.id is not null and crewtask.status='PROPOSED_CLOSURE'
				// ";
			}
			System.out.println("qry" + qry);
			logger.debug("qry" + qry);
			// Query query = em.createQuery(qry);
			Query query = emnative.createNativeQuery(qry);
			List result = query.getResultList();
			System.out.println("result" + result.size());
			if (result != null && result.size() > 0) {
				for (int i = 0; i < result.size(); i++) {
					Object[] row = (Object[]) result.get(i);
					OutageV2 outageV2 = new OutageV2();
					outageV2.setId(NumberUtil.getInt(row[0]));
					outageV2.setOutageId(StringUtil.getString(row[1]));
					outageV2.setOutageName(StringUtil.getString(row[2]));
					outageV2.setTenantId(NumberUtil.getInt(row[3]));
					outageV2.setUserName(StringUtil.getString(row[4]));
					outageV2.setProcessId(StringUtil.getString(row[5]));
					// outageV2.setSourceSystem(StringUtil.getString(row[3]));
					outageV2.setIsPlanned(NumberUtil.getShort(row[7]));
					outageV2.setReasonType(StringUtil.getString(row[8]));
					outageV2.setNetworkElementType(StringUtil.getString(row[9]));
					outageV2.setNetworkElementUID(StringUtil.getString(row[10]));
					outageV2.setOutageStartTime(row[11] != null ? (Date) row[11] : null);
					outageV2.setOutageEndTime(row[12] != null ? (Date) row[12] : null);
					outageV2.setDuration(StringUtil.getString(row[13]));
					outageV2.setStatus(StringUtil.getString(row[14]));
					outageV2.setDivisionname(StringUtil.getString(row[15]));
					outageV2.setTransformername(StringUtil.getString(row[16]));
					outageV2.setSubstationname(StringUtil.getString(row[17]));
					outageV2.setFeedername(StringUtil.getString(row[18]));
					outageV2.setSubdivisionname(StringUtil.getString(row[19]));
					if (outageSearchVO.isProposeClosure() == true) {
						String crewtaskstatus = StringUtil.getString(row[20]);
						if (StringUtil.isNotNullOrEmpty(crewtaskstatus)) {
							if (crewtaskstatus.contains("PLANNED") || crewtaskstatus.contains("WORK IN PROGRESS")
									|| crewtaskstatus.contains("DRAFT") || crewtaskstatus.contains("COMPLETED")) {

							} else {
								outageV2s.add(outageV2);
							}
						} else {
							// outageV2s.add(outageV2);
						}
					} else {
						outageV2s.add(outageV2);
					}
					outageV2.setReason(StringUtil.getString(row[21]));
					logger.debug("outageV2s.size" + outageV2s.size());
					logger.debug("outageV2s.size" + outageV2.getTransformername());
				}
			}
			emnative.close();

		} catch (Exception e) {
			throw new OutageException(e);
		}

		return outageV2s;
	}

	public List<CrewTask> searchCrewTaskByQuery(CrewTask crewTask) throws OutageException {
		List<CrewTask> crewTasks = new ArrayList();
		CrewTask crewTask2 = new CrewTask();
		try {

			String qry = "SELECT crewtask.id as crewtaskid,crewtask.name,"
					+ " crewtask.tenantid,crewtask.plannedstartdate,crewtask.plannedenddate ,"
					+ " crewtask.jobdetails,crewtask.status,"
					+ " division.name as divisionname,transformer.name as transformername,substation.name as substationname,"
					+ " feeder.name as feedername,subdivision.name as subdivisionname, "
					+ " division.id as divisionid,transformer.id as transformerid,substation.id as substationid,"
					+ " feeder.id as feederid,crew.name as crewname,crewtask.actualstartdate, "
					+ " crewtask.crewid,crewtask.mdmscheck,crewtask.outageid,subdivision.id as subdivisionid,"
					+ " crewtask.lastupdatedby,user.username" + " FROM tbl_crewtask crewtask"
					+ " left outer join tbl_outage outage on outage.id = crewtask.outageid"
					+ " left outer join tbl_division division on division.id = outage.division"
					+ " left outer join tbl_substation substation on substation.id = outage.substation"
					+ " left outer join tbl_subdivision subdivision on subdivision.id = outage.subdivision"
					+ " left outer join tbl_feeder feeder on feeder.id = outage.feeder"
					+ " left outer join tbl_transformer transformer on transformer.id = outage.transformer"
					+ " left outer join tbl_crew crew on crew.id = crewtask.crewid"
					+ " left outer join tbl_user user on user.id = crewtask.lastupdatedby";

			// + " where transformer.tenantid=" + transformer.getTenantid();
			if (NumberUtil.isNotNullOrZero(crewTask.getTenantid())) {
				qry += " where  crewtask.tenantid=" + crewTask.getTenantid();
			} else {
				qry += " where  crewtask.tenantid=1";
			}
			if (NumberUtil.isNotNullOrZero(crewTask.getId())) {
				qry += " and  crewtask.id=" + crewTask.getId();
			}
			if (NumberUtil.isNotNullOrZero(crewTask.getOutageid())) {
				qry += " and  crewtask.outageid=" + crewTask.getOutageid();
			}
			if (NumberUtil.isNotNullOrZero(crewTask.getCrewid())) {
				qry += " and  crewtask.crewid=" + crewTask.getCrewid();
			}
			if (NumberUtil.isNotNull(crewTask.getSubdivision())) {
				qry += " and  outage.subdivision=" + crewTask.getSubdivision();
			}
			if (NumberUtil.isNotNull(crewTask.getSubstation())) {
				qry += " and  outage.substation=" + crewTask.getSubstation();
			}
			if (NumberUtil.isNotNull(crewTask.getDivision())) {
				qry += " and  outage.division=" + crewTask.getDivision();
			}

			/*
			 * if (NumberUtil.isNotNull(crewTask.getSubdivision())) { qry +=
			 * " and  subdivision.id=" + crewTask.getSubdivision(); } if
			 * (NumberUtil.isNotNull(crewTask.getSubstation())) { qry +=
			 * " and  substation.id=" + crewTask.getSubstation(); } if
			 * (NumberUtil.isNotNull(crewTask.getDivision())) { qry += " and  division.id="
			 * + crewTask.getDivision(); }
			 */

			Query query = emnative.createNativeQuery(qry);
			System.out.println("qry-->" + qry);
			List result = query.getResultList();
			System.out.println("result" + result.size());
			if (result != null && result.size() > 0) {
				for (int i = 0; i < result.size(); i++) {
					Object[] row = (Object[]) result.get(i);
					crewTask2 = new CrewTask();
					crewTask2.setId(NumberUtil.getInt(row[0]));
					crewTask2.setName(StringUtil.getString(row[1]));
					// crewTask2.setCrewname(StringUtil.getString(row[2]));
					crewTask2.setTenantid(NumberUtil.getInt(row[2]));
					crewTask2.setPlannedStartDate(row[3] != null ? (Date) row[3] : null);
					crewTask2.setPlannedEndDate(row[4] != null ? (Date) row[4] : null);
					crewTask2.setJobDetails(StringUtil.getString(row[5]));
					crewTask2.setStatus(StringUtil.getString(row[6]));
					crewTask2.setDivisionname(StringUtil.getString(row[7]));
					crewTask2.setTransformername(StringUtil.getString(row[8]));
					crewTask2.setSubstationname(StringUtil.getString(row[9]));
					crewTask2.setFeedername(StringUtil.getString(row[10]));
					crewTask2.setSubdivisionname(StringUtil.getString(row[11]));
					crewTask2.setDivision(NumberUtil.getInt(row[12]));
					crewTask2.setTransformer(NumberUtil.getInt(row[13]));
					crewTask2.setSubstation(NumberUtil.getInt(row[14]));
					crewTask2.setFeeder(NumberUtil.getInt(row[15]));
					crewTask2.setCrewname(StringUtil.getString(row[16]));
					crewTask2.setActualStartDate(row[17] != null ? (Date) row[17] : null);
					crewTask2.setCrewid(NumberUtil.getInt(row[18]));
					crewTask2.setMdmscheck(NumberUtil.getInt(row[19]));
					crewTask2.setOutageid(NumberUtil.getInt(row[20]));
					crewTask2.setLastupdatedby(NumberUtil.getInt(row[21]));
					crewTask2.setLastUpdatedByName(StringUtil.getString(row[22]));
					crewTasks.add(crewTask2);
				}
			}
			emnative.close();

		} catch (Exception e) {
			throw new OutageException(e);
		}

		return crewTasks;
	}

	public List<OutageNotificationVO> searchOutageSchedulerByQuery(String status, String notificationtype)
			throws OutageException {
		List<OutageNotificationVO> notificationVOs = new ArrayList();
		OutageNotificationVO outageNotificationVO = null;
		try {
			String qry = "SELECT outage.id,outage.outageid," + "outage.name,"
					+ " outage.tenantid,outage.username,outage.processid ," + " outage.sourcesystem,outage.isplanned,"
					+ " outage.reasontype,outage.networkelementtype,outage.networkelementuid,"
					+ " outage.starttime,outage.endtime,outage.duration,outage.status,"
					+ " division.name as divisionname,transformer.name as transformername,substation.name as substationname,"
					+ " feeder.name as feedername,subdivision.name as subdivisionname, "
					+ " (SELECT group_concat(crewtask.status separator ', ') as crewtaskstatus from tbl_crewtask crewtask where crewtask.outageid = outage.id group by crewtask.outageid) as crewtaskstatus,"
					+ " outage.reason,outage.isreschedule,transformer.id as trid" + " FROM tbl_outage outage"
					// + " left outer join tbl_crewtask crewtask on crewtask.outageid =outage.id"
					+ " left outer join tbl_division division on division.id = outage.division"
					+ " left outer join tbl_substation substation on substation.id = outage.substation"
					+ " left outer join tbl_subdivision subdivision on subdivision.id = outage.subdivision"
					+ " left outer join tbl_feeder feeder on feeder.id = outage.feeder"
					+ " left outer join tbl_transformer transformer on transformer.id = outage.transformer";

			if (StringUtil.isNotNullOrEmpty(status)) {
				qry += " where  outage.status='" + status + "'";
			}
			if (status.equalsIgnoreCase(IOmsConstants.CONFIRM_STATUS_DOMAIN)) {
				if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_MAIL)) {
					qry += " and  outage.confirmmailts is null";
				} else if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_SMS)) {
					qry += " and  outage.confirmsmsts is null";
				}
			} else if (status.equalsIgnoreCase(IOmsConstants.APPROVE_STATUS_DOMAIN)) {
				if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_MAIL)) {
					qry += " and  outage.approvemailts is null";
				} else if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_SMS)) {
					qry += " and  outage.approvesmsts is null";
				}
			} else if (status.equalsIgnoreCase(IOmsConstants.REJECT_STATUS_DOMAIN)) {
				if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_MAIL)) {
					qry += " and  outage.rejectmailts is null";
				} else if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_SMS)) {
					qry += " and  outage.rejectsmsts is null";
				}
			} else if (status.equalsIgnoreCase(IOmsConstants.RESCHEDULE_STATUS_DOMAIN)) {
				if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_MAIL)) {
					qry += " and  outage.reschedulemailts is null";
				} else if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_SMS)) {
					qry += " and  outage.reschedulesmsts is null";
				}
			} else if (status.equalsIgnoreCase(IOmsConstants.COMPLETE_STATUS_DOMAIN)) {
				if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_MAIL)) {
					qry += " and  outage.completemailts is null";
				} else if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_SMS)) {
					qry += " and  outage.completesmsts is null";
				}
			} else if (status.equalsIgnoreCase(IOmsConstants.CANCELL_STATUS_DOMAIN)) {
				if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_MAIL)) {
					// qry += " and outage.confirmmailts is null";
				} else if (notificationtype.equalsIgnoreCase(IOmsConstants.NOTIFICATION_TYPE_SMS)) {
					// qry += " and outage.confirmmailts is null";
				}
			}
			Query query = emnative.createNativeQuery(qry);
			logger.debug("query" + qry);
			List result = query.getResultList();
			System.out.println("result" + result.size());
			if (result != null && result.size() > 0) {
				for (int i = 0; i < result.size(); i++) {
					Object[] row = (Object[]) result.get(i);
					OutageVO outageVO = new OutageVO();
					outageVO.setId(NumberUtil.getInt(row[0]));
					outageVO.setOutageId(StringUtil.getString(row[1]));
					outageVO.setOutageName(StringUtil.getString(row[2]));
					outageVO.setTenantId(NumberUtil.getInt(row[3]));
					outageVO.setUserName(StringUtil.getString(row[4]));
					outageVO.setProcessId(StringUtil.getString(row[5]));
					if (NumberUtil.getShort(row[7]) != null && NumberUtil.getShort(row[7]) > 0) {
						outageVO.setIsPlanned(true);
					} else {
						outageVO.setIsPlanned(false);
					}
					outageVO.setReasonType(StringUtil.getString(row[8]));
					outageVO.setNetworkElementType(StringUtil.getString(row[9]));
					outageVO.setNetworkElementUID(StringUtil.getString(row[10]));
					outageVO.setOutageStartTime(row[11] != null ? (Date) row[11] : null);
					outageVO.setOutageEndTime(row[12] != null ? (Date) row[12] : null);
					outageVO.setStartDateTime(row[11] != null ? (Date) row[11] : null);
					outageVO.setEndDateTime(row[12] != null ? (Date) row[12] : null);
					outageVO.setDuration(StringUtil.getString(row[13]));
					outageVO.setStatus(StringUtil.getString(row[14]));
					outageVO.setDivisionname(StringUtil.getString(row[15]));
					outageVO.setTransformername(StringUtil.getString(row[16]));
					outageVO.setSubstationname(StringUtil.getString(row[17]));
					outageVO.setFeedername(StringUtil.getString(row[18]));
					outageVO.setSubdivisionname(StringUtil.getString(row[19]));
					outageVO.setReason(StringUtil.getString(row[21]));
					if (NumberUtil.isNotNullOrZero(NumberUtil.getInt(row[22]))) {
						outageVO.setRescheduled(true);
					} else {
						outageVO.setRescheduled(false);
					}
					outageVO.setTransformer(NumberUtil.getInt(row[23]));
					outageNotificationVO = new OutageNotificationVO(outageVO.getOutageId(), outageVO.getOutageName(),
							outageVO.getNetworkElementType(), outageVO.getNetworkElementUID(), outageVO.getStatus(),
							outageVO);
					notificationVOs.add(outageNotificationVO);
				}
			}
			emnative.close();

		} catch (Exception e) {
			throw new OutageException(e);
		}

		return notificationVOs;
	}

	
}
