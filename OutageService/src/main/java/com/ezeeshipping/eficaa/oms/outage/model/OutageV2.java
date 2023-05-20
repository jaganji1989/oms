/**
 * 
 */
package com.ezeeshipping.eficaa.oms.outage.model;

import java.time.Duration;
import java.util.Date;
import java.util.Timer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Formula;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Dell
 *
 */
@Entity
@Table(name = "tbl_outage")
@NoArgsConstructor
public class OutageV2 implements java.io.Serializable {
	private int id;
	private String outageId;
	private String outageName;
	private String reason;
	private Integer tenantId;
	private String userName;
	private String processId;
	// private Integer createdby;
	
	private Short sourceSystem;
	private Short isPlanned;
	private String reasonType;
	private String networkElementType;
	private String networkElementUID;
	private Date outageStartTime;
	private Date outageEndTime;
	private String duration;
	private String status;
	private Integer approvedby;
	private Date approvedDate;
	private Integer lastupdatedby;
	private Date createddate;
	private Date lastupdateddate;
	private String approverRemarks;
	private String taskId;
	private Integer division;
	private Integer feeder;
	private Integer transformer;
	private Integer substation;
	private Integer subdivision;
	private String divisionname;
	private String substationname;
	private String subdivisionname;
	private String feedername;
	private String transformername;
	private Integer crewid;
    private Integer isRescheduled;
    private Integer closerTaskId;
    private Date confirmmailts;
    private Date confirmsmsts;
    private Date approvemailts;
    private Date approvesmsts;
    private Date reschedulemailts;
    private Date reschedulesmsts;
    private Date rejectmailts;
    private Date rejectsmsts;
    private Date completemailts;
    private Date completesmsts;
	@Id
	@GeneratedValue
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "outageid", nullable = false)

	public String getOutageId() {
		return outageId;
	}

	public void setOutageId(String outageId) {
		this.outageId = outageId;
	}

//	 @NotEmpty
	// @Size(min = 3, message = "OutageId should have at least 3 characters")
	// @Size(max = 15, message = "OutageId maximum 15 characters only allowed")
	@Column(name = "name", nullable = false)

	// @NotEmpty
	public String getOutageName() {
		return outageName;
	}

	public void setOutageName(String outageName) {
		this.outageName = outageName;
	}

	@Column(name = "reason")
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Column(name = "tenantid", nullable = false)

	public Integer getTenantId() {
		return tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}

	@Column(name = "username")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "processid", nullable = false)
	// @NotEmpty
	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	/*
	 * @Column(name = "createdby", updatable = false) public Integer getCreatedby()
	 * { return createdby; }
	 * 
	 * public void setCreatedby(Integer createdby) { this.createdby = createdby; }
	 */

	@Column(name = "lastupdatedby")
	public Integer getLastupdatedby() {
		return lastupdatedby;
	}

	public void setLastupdatedby(Integer lastupdatedby) {
		this.lastupdatedby = lastupdatedby;
	}

	@Column(name = "sourcesystem", nullable = false)

	public Short getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(Short sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	@Column(name = "reasontype")
	public String getReasonType() {
		return reasonType;
	}

	@Column(name = "isplanned", nullable = false)
	public Short getIsPlanned() {
		return isPlanned;
	}

	public void setIsPlanned(Short isPlanned) {
		this.isPlanned = isPlanned;
	}

	public void setReasonType(String reasonType) {
		this.reasonType = reasonType;
	}

	@Column(name = "networkelementtype", nullable = false)
	// @NotEmpty
	public String getNetworkElementType() {
		return networkElementType;
	}

	public void setNetworkElementType(String networkElementType) {
		this.networkElementType = networkElementType;
	}

	@Column(name = "networkelementuid", nullable = false)
	// @NotEmpty
	public String getNetworkElementUID() {
		return networkElementUID;
	}

	public void setNetworkElementUID(String networkElementUID) {
		this.networkElementUID = networkElementUID;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "starttime", nullable = false)
	public Date getOutageStartTime() {
		return outageStartTime;
	}

	public void setOutageStartTime(Date outageStartTime) {
		this.outageStartTime = outageStartTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "endtime", nullable = false)
	public Date getOutageEndTime() {
		return outageEndTime;
	}

	public void setOutageEndTime(Date outageEndTime) {
		this.outageEndTime = outageEndTime;
	}

	@Column(name = "duration")
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	@Column(name = "status", nullable = false)

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "approvedby", updatable = false)
	public Integer getApprovedby() {
		return approvedby;
	}

	public void setApprovedby(Integer approvedby) {
		this.approvedby = approvedby;
	}

	@Column(name = "approveddate", updatable = false)
	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	@Column(name = "createddate", updatable = false)
	public Date getCreateddate() {
		return createddate;
	}

	public void setCreateddate(Date createddate) {
		this.createddate = createddate;
	}

	@Column(name = "lastupdateddate")
	public Date getLastupdateddate() {
		return lastupdateddate;
	}

	public void setLastupdateddate(Date lastupdateddate) {
		this.lastupdateddate = lastupdateddate;
	}

	@Column(name = "approverremarks")
	public String getApproverRemarks() {
		return approverRemarks;
	}

	public void setApproverRemarks(String approverRemarks) {
		this.approverRemarks = approverRemarks;
	}

	@Column(name = "taskid")
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	@Column(name = "division")
	public Integer getDivision() {
		return division;
	}

	public void setDivision(Integer division) {
		this.division = division;
	}
	@Column(name = "feeder")
	public Integer getFeeder() {
		return feeder;
	}

	public void setFeeder(Integer feeder) {
		this.feeder = feeder;
	}
	@Column(name = "transformer")
	public Integer getTransformer() {
		return transformer;
	}

	public void setTransformer(Integer transformer) {
		this.transformer = transformer;
	}
	@Column(name = "substation")
	public Integer getSubstation() {
		return substation;
	}

	public void setSubstation(Integer substation) {
		this.substation = substation;
	}
	@Column(name = "subdivision")
	public Integer getSubdivision() {
		return subdivision;
	}

	public void setSubdivision(Integer subdivision) {
		this.subdivision = subdivision;
	}
	@Formula("(SELECT division.name FROM tbl_division division WHERE division.id = division)")
	public String getDivisionname() {
		return divisionname;
	}

	public void setDivisionname(String divisionname) {
		this.divisionname = divisionname;
	}
	@Formula("(SELECT substation.name FROM tbl_substation substation WHERE substation.id = substation)")
	public String getSubstationname() {
		return substationname;
	}

	public void setSubstationname(String substationname) {
		this.substationname = substationname;
	}
	@Formula("(SELECT substation.name FROM tbl_subdivision substation WHERE substation.id = subdivision)")
	public String getSubdivisionname() {
		return subdivisionname;
	}

	public void setSubdivisionname(String subdivisionname) {
		this.subdivisionname = subdivisionname;
	}
	@Formula("(SELECT fdr.name FROM tbl_feeder fdr WHERE fdr.id = feeder)")
	public String getFeedername() {
		return feedername;
	}

	public void setFeedername(String feedername) {
		this.feedername = feedername;
	}
	@Formula("(SELECT trs.name FROM tbl_transformer trs WHERE trs.id = transformer)")
	public String getTransformername() {
		return transformername;
	}

	public void setTransformername(String transformername) {
		this.transformername = transformername;
	}
	@Formula("(SELECT crew.id FROM tbl_crew crew WHERE crew.areaid = substation and crew.areatype = 'SUB STATION' LIMIT 1)")
	public Integer getCrewid() {
		return crewid;
	}

	public void setCrewid(Integer crewid) {
		this.crewid = crewid;
	}
	@Column(name = "isreschedule")
	public Integer getIsRescheduled() {
		return isRescheduled;
	}

	public void setIsRescheduled(Integer isRescheduled) {
		this.isRescheduled = isRescheduled;
	}
	@Formula("(SELECT crewtask.id FROM tbl_crewtask crewtask WHERE crewtask.outageid = id and crewtask.status = 'PROPOSED_CLOSURE' LIMIT 1)")
	public Integer getCloserTaskId() {
		return closerTaskId;
	}

	public void setCloserTaskId(Integer closerTaskId) {
		this.closerTaskId = closerTaskId;
	}
	@Column(name = "confirmmailts", updatable = false)
	public Date getConfirmmailts() {
		return confirmmailts;
	}

	public void setConfirmmailts(Date confirmmailts) {
		this.confirmmailts = confirmmailts;
	}
	@Column(name = "confirmsmsts" ,updatable = false)
	public Date getConfirmsmsts() {
		return confirmsmsts;
	}

	public void setConfirmsmsts(Date cofirmsmsts) {
		this.confirmsmsts = confirmsmsts;
	}
	@Column(name = "approvemailts",updatable = false)
	public Date getApprovemailts() {
		return approvemailts;
	}

	public void setApprovemailts(Date approvemailts) {
		this.approvemailts = approvemailts;
	}
	@Column(name = "approvesmsts",updatable = false)
	public Date getApprovesmsts() {
		return approvesmsts;
	}

	public void setApprovesmsts(Date approvesmsts) {
		this.approvesmsts = approvesmsts;
	}
	@Column(name = "reschedulemailts",updatable = false)
	public Date getReschedulemailts() {
		return reschedulemailts;
	}

	public void setReschedulemailts(Date reschedulemailts) {
		this.reschedulemailts = reschedulemailts;
	}
	@Column(name = "reschedulesmsts",updatable = false)
	public Date getReschedulesmsts() {
		return reschedulesmsts;
	}

	public void setReschedulesmsts(Date reschedulesmsts) {
		this.reschedulesmsts = reschedulesmsts;
	}
	@Column(name = "rejectmailts",updatable = false)
	public Date getRejectmailts() {
		return rejectmailts;
	}

	public void setRejectmailts(Date rejectmailts) {
		this.rejectmailts = rejectmailts;
	}
	@Column(name = "rejectsmsts",updatable = false)
	public Date getRejectsmsts() {
		return rejectsmsts;
	}

	public void setRejectsmsts(Date rejectsmsts) {
		this.rejectsmsts = rejectsmsts;
	}
	@Column(name = "completemailts",updatable = false)
	public Date getCompletemailts() {
		return completemailts;
	}

	public void setCompletemailts(Date completemailts) {
		this.completemailts = completemailts;
	}
	@Column(name = "completesmsts",updatable = false)
	public Date getCompletesmsts() {
		return completesmsts;
	}

	public void setCompletesmsts(Date completesmsts) {
		this.completesmsts = completesmsts;
	}
	
	
}
