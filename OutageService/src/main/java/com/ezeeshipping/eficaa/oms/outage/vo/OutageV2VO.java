/**
 * 
 */
package com.ezeeshipping.eficaa.oms.outage.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author Dell
 *
 */
public class OutageV2VO {
	 private int id;
	 private String outageId;
	 private String outageName;
	 private String reason;
	 private Integer tenantId;
	 private String userName;
	 private String processId;
	 private Short sourceSystem;
	 private boolean  isPlanned;
	 private String reasonType;
	 private String networkElementType;
	 private String networkElementUID;
	 private String networkElementStatus;
	  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "IST")
	 private Date outageStartTime;
	  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "IST")
	 private Date outageEndTime;
	 private String status;
	 private String action;
	 private String duration;
	// private Integer userId;
	 private String approverremarks;
	 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "IST")
	 private Date timeStamp;
	 private String taskId;
	 private String jsonData;
	 private boolean  isRescheduled;
	 private Integer closerTaskId;
	public OutageV2VO() {
    }
	
	public OutageV2VO(String outageId, String outageName, String reason, Integer tenantId, String userName,
			String processId, Short sourceSystem, boolean isPlanned, String reasonType, String networkElementType,
			String networkElementUID, String networkElementStatus, Date outageStartTime, Date outageEndTime,
			String status, String action, String duration, String approverremarks) {
		super();
		this.outageId = outageId;
		this.outageName = outageName;
		this.reason = reason;
		this.tenantId = tenantId;
		this.userName = userName;
		this.processId = processId;
		this.sourceSystem = sourceSystem;
		this.isPlanned = isPlanned;
		this.reasonType = reasonType;
		this.networkElementType = networkElementType;
		this.networkElementUID = networkElementUID;
		this.networkElementStatus = networkElementStatus;
		this.outageStartTime = outageStartTime;
		this.outageEndTime = outageEndTime;
		this.status = status;
		this.action = action;
		this.duration = duration;
		this.approverremarks = approverremarks;
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
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOutageId() {
		return outageId;
	}

	public void setOutageId(String outageId) {
		this.outageId = outageId;
	}

	



	public String getApproverremarks() {
		return approverremarks;
	}

	public void setApproverremarks(String approverremarks) {
		this.approverremarks = approverremarks;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNetworkElementStatus() {
		return networkElementStatus;
	}

	public void setNetworkElementStatus(String networkElementStatus) {
		this.networkElementStatus = networkElementStatus;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public boolean getIsPlanned() {
		return isPlanned;
	}

	public void setIsPlanned(boolean isPlanned) {
		this.isPlanned = isPlanned;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	public void setPlanned(boolean isPlanned) {
		this.isPlanned = isPlanned;
	}

	public boolean isRescheduled() {
		return isRescheduled;
	}

	public void setRescheduled(boolean isRescheduled) {
		this.isRescheduled = isRescheduled;
	}

	public Integer getCloserTaskId() {
		return closerTaskId;
	}

	public void setCloserTaskId(Integer closerTaskId) {
		this.closerTaskId = closerTaskId;
	}
	
	
	
	
}
