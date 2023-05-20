/**
 * 
 */
package com.ezeeshipping.eficaa.oms.outage.vo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.ezeeshipping.eficaa.oms.admin.vo.TransformerVO;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author Dell
 *
 */
public class OutageDetailResponseV2VO {
	private int id;
	private String outageId;
	private String outageName;
	private String reason;
	private Integer tenantId;
	private String userName;
	private String processId;
	private Short sourceSystem;
	private boolean isPlanned;
	private String reasonType;
	private String networkElementType;
	private String networkElementUID;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "IST")
	private Date outageStartTime;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "IST")
	private Date outageEndTime;
	private String status;
	// private String action;
	private String duration;
	// private Integer userId;
	private String divisionname;
	private String sectionname;
	private String substationname;
	private String subdivisionname;
	private String feedername;
	private String transformername;
	private String taskId;
	private Integer division;
	private Integer feeder;
	private Integer transformer;
	private Integer substation;
	private Integer section;
	private Integer subdivision;
	private boolean isRescheduled;
	private String approverremarks;
	 private Set<CrewTaskVO> crewTaskVOs = new HashSet(0);
	public OutageDetailResponseV2VO() {

	}

	public String getOutageId() {
		return outageId;
	}

	public void setOutageId(String outageId) {
		this.outageId = outageId;
	}

	public String getOutageName() {
		return outageName;
	}

	public void setOutageName(String outageName) {
		this.outageName = outageName;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Integer getTenantId() {
		return tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public Short getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(Short sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public boolean getIsPlanned() {
		return isPlanned;
	}

	public void setIsPlanned(boolean isPlanned) {
		this.isPlanned = isPlanned;
	}

	public String getReasonType() {
		return reasonType;
	}

	public void setReasonType(String reasonType) {
		this.reasonType = reasonType;
	}

	public String getNetworkElementType() {
		return networkElementType;
	}

	public void setNetworkElementType(String networkElementType) {
		this.networkElementType = networkElementType;
	}

	public String getNetworkElementUID() {
		return networkElementUID;
	}

	public void setNetworkElementUID(String networkElementUID) {
		this.networkElementUID = networkElementUID;
	}

	public Date getOutageStartTime() {
		return outageStartTime;
	}

	public void setOutageStartTime(Date outageStartTime) {
		this.outageStartTime = outageStartTime;
	}

	public Date getOutageEndTime() {
		return outageEndTime;
	}

	public void setOutageEndTime(Date outageEndTime) {
		this.outageEndTime = outageEndTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/*
	 * public String getAction() { return action; }
	 * 
	 * public void setAction(String action) { this.action = action; }
	 */

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSectionname() {
		return sectionname;
	}

	public void setSectionname(String sectionname) {
		this.sectionname = sectionname;
	}

	public String getSubstationname() {
		return substationname;
	}

	public void setSubstationname(String substationname) {
		this.substationname = substationname;
	}

	public String getSubdivisionname() {
		return subdivisionname;
	}

	public void setSubdivisionname(String subdivisionname) {
		this.subdivisionname = subdivisionname;
	}

	public String getFeedername() {
		return feedername;
	}

	public void setFeedername(String feedername) {
		this.feedername = feedername;
	}

	public String getTransformername() {
		return transformername;
	}

	public void setTransformername(String transformername) {
		this.transformername = transformername;
	}

	public void setPlanned(boolean isPlanned) {
		this.isPlanned = isPlanned;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Integer getDivision() {
		return division;
	}

	public void setDivision(Integer division) {
		this.division = division;
	}

	public Integer getFeeder() {
		return feeder;
	}

	public void setFeeder(Integer feeder) {
		this.feeder = feeder;
	}

	public Integer getTransformer() {
		return transformer;
	}

	public void setTransformer(Integer transformer) {
		this.transformer = transformer;
	}

	public Integer getSubstation() {
		return substation;
	}

	public void setSubstation(Integer substation) {
		this.substation = substation;
	}

	public Integer getSection() {
		return section;
	}

	public void setSection(Integer section) {
		this.section = section;
	}

	public Integer getSubdivision() {
		return subdivision;
	}

	public void setSubdivision(Integer subdivision) {
		this.subdivision = subdivision;
	}

	public String getDivisionname() {
		return divisionname;
	}

	public void setDivisionname(String divisionname) {
		this.divisionname = divisionname;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isRescheduled() {
		return isRescheduled;
	}

	public void setRescheduled(boolean isRescheduled) {
		this.isRescheduled = isRescheduled;
	}

	public Set<CrewTaskVO> getCrewTaskVOs() {
		return crewTaskVOs;
	}

	public void setCrewTaskVOs(Set<CrewTaskVO> crewTaskVOs) {
		this.crewTaskVOs = crewTaskVOs;
	}

	public String getApproverremarks() {
		return approverremarks;
	}

	public void setApproverremarks(String approverremarks) {
		this.approverremarks = approverremarks;
	}



	
}
