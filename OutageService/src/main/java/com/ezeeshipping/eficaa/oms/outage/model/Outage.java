package com.ezeeshipping.eficaa.oms.outage.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Formula;

import com.ezeeshipping.eficaa.oms.core.BaseObject;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "tbl_outage")
@NoArgsConstructor
@ToString
public class Outage  implements java.io.Serializable {
	private int id;
	private String outageName;
	private String reason;
	private String outageId;
	private Date startDateTime;
	//private Date endDateTime;
	private Integer division;
	private Integer feeder;
	private Integer transformer;
	private Integer substation;
	private Integer subdivision;
	private Integer section;
	private String status;
	//private Integer outageduration;
	private String divisionname;
	private String sectionname;
	private String substationname;
	private String approverremarks;
	private Integer approvedby;
	private Date approvedDate;
	
	private String subdivisionname;
	private String reasontype;
	private String feedername;
	private String transformername;
	private Integer tenantId;
	private String userName;
	private Short sourceSystem;
	private Short isPlanned;
	private String processId;
	private String networkElementType;
	private String networkElementUID;
	private String duration;
	

//	private Integer createdby;
//    private Date createddate;
//    private Date lastupdateddate;
//    private Integer lastupdatedby;
	
	private Integer crewid;
    private String taskId;
	@Id
	@GeneratedValue
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "name")
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "starttime")
	public Date getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	/*@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "endtime")
	public Date getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime;
	}*/

//@Column(name = "region")
//public Integer getRegion() {
//	return region;
//}
//
//public void setRegion(Integer region) {
//	this.region = region;
//}
//@Column(name = "circle")
//
//public Integer getCircle() {
//	return circle;
//}
//
//public void setCircle(Integer circle) {
//	this.circle = circle;
//}
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

	@Column(name = "status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "section")
	public Integer getSection() {
		return section;
	}

	public void setSection(Integer section) {
		this.section = section;
	}

	/*@Column(name = "outageduration")
	public Integer getOutageduration() {
		return outageduration;
	}

	public void setOutageduration(Integer outageduration) {
		this.outageduration = outageduration;
	}*/

	@Formula("(SELECT division.name FROM tbl_division division WHERE division.id = division)")

	public String getDivisionname() {
		return divisionname;
	}

	public void setDivisionname(String divisionname) {
		this.divisionname = divisionname;
	}

	@Formula("(SELECT section.name FROM tbl_section section WHERE section.id = section)")

	public String getSectionname() {
		return sectionname;
	}

	public void setSectionname(String sectionname) {
		this.sectionname = sectionname;
	}

	@Formula("(SELECT substation.name FROM tbl_substation substation WHERE substation.id = substation)")

	public String getSubstationname() {
		return substationname;
	}

	public void setSubstationname(String substationname) {
		this.substationname = substationname;
	}

	@Column(name = "approverremarks")

	public String getApproverremarks() {
		return approverremarks;
	}

	public void setApproverremarks(String approverremarks) {
		this.approverremarks = approverremarks;
	}

	@Column(name = "approvedby")

	public Integer getApprovedby() {
		return approvedby;
	}

	public void setApprovedby(Integer approvedby) {
		this.approvedby = approvedby;
	}

	@Column(name = "approveddate")

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	@Column(name = "subdivision")

	public Integer getSubdivision() {
		return subdivision;
	}

	public void setSubdivision(Integer subdivision) {
		this.subdivision = subdivision;
	}

	@Formula("(SELECT substation.name FROM tbl_subdivision substation WHERE substation.id = subdivision)")

	public String getSubdivisionname() {
		return subdivisionname;
	}

	public void setSubdivisionname(String subdivisionname) {
		this.subdivisionname = subdivisionname;
	}

	@Column(name = "reasontype")

	public String getReasontype() {
		return reasontype;
	}

	public void setReasontype(String reasontype) {
		this.reasontype = reasontype;
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

	@Column(name = "outageid")
	public String getOutageId() {
		return outageId;
	}

	public void setOutageId(String outageId) {
		this.outageId = outageId;
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

	@Column(name = "sourcesystem", nullable = false)

	public Short getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(Short sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	@Column(name = "isplanned", nullable = false)
	public Short getIsPlanned() {
		return isPlanned;
	}

	public void setIsPlanned(Short isPlanned) {
		this.isPlanned = isPlanned;
	}
	
	@Column(name = "processid",nullable = false)
	// @NotEmpty
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	
	@Column(name = "networkelementtype",nullable = false)
	//@NotEmpty
	public String getNetworkElementType() {
		return networkElementType;
	}

	public void setNetworkElementType(String networkElementType) {
		this.networkElementType = networkElementType;
	}

	@Column(name = "networkelementuid",nullable = false)
	//@NotEmpty
	public String getNetworkElementUID() {
		return networkElementUID;
	}

	public void setNetworkElementUID(String networkElementUID) {
		this.networkElementUID = networkElementUID;
	}
	@Column(name = "duration")
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}
//	@Column(name = "createdby")
//
//	public Integer getCreatedby() {
//		return createdby;
//	}
//	public void setCreatedby(Integer createdby) {
//		this.createdby = createdby;
//	}
//	@Column(name = "createddate")
//
//	public Date getCreateddate() {
//		return createddate;
//	}
//	public void setCreateddate(Date createddate) {
//		this.createddate = createddate;
//	}
//	@Column(name = "lastupdatedby")
//
//	public Integer getLastupdatedby() {
//		return lastupdatedby;
//	}
//	public void setLastupdatedby(Integer lastupdatedby) {
//		this.lastupdatedby = lastupdatedby;
//	}
//	@Column(name = "lastupdateddate")
//
//	public Date getLastupdateddate() {
//		return lastupdateddate;
//	}
//	public void setLastupdateddate(Date lastupdateddate) {
//		this.lastupdateddate = lastupdateddate;
//	}
	@Column(name = "taskid")
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	@Formula("(SELECT crew.id FROM tbl_crew crew WHERE crew.areaid = substation and crew.areatype = 'SUB STATION'  LIMIT 1)")
	public Integer getCrewid() {
		return crewid;
	}

	public void setCrewid(Integer crewid) {
		this.crewid = crewid;
	}
	
	

}
