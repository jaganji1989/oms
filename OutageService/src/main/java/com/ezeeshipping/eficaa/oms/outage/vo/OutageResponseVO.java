/**
 * 
 */
package com.ezeeshipping.eficaa.oms.outage.vo;

/**
 * @author Dell
 *
 */
public class OutageResponseVO {
	private String outageId;
	private String statusCode;
	private String statusDesc;
	private String processId;
    private Short sourceSystem;
    private boolean isPlanned; 
    private String taskId;
	public OutageResponseVO() {
	}
	public String getOutageId() {
		return outageId;
	}
	public void setOutageId(String outageId) {
		this.outageId = outageId;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
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
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	
	
}
