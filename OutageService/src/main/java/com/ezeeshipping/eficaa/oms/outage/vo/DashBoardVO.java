package com.ezeeshipping.eficaa.oms.outage.vo;

import java.util.Date;

public class DashBoardVO {
	private int id;
	private String status;
	private Short isPlanned;
	private Date outageStartTime;
	private Date outageEndTime;
	private Integer tenantid;

	

	public Integer getTenantid() {
		return tenantid;
	}

	public void setTenantid(Integer tenantid) {
		this.tenantid = tenantid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Short getIsPlanned() {
		return isPlanned;
	}

	public void setIsPlanned(Short isPlanned) {
		this.isPlanned = isPlanned;
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

}
