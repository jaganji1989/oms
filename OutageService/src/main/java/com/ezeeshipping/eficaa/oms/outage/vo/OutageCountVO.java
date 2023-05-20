package com.ezeeshipping.eficaa.oms.outage.vo;

import com.ezeeshipping.eficaa.oms.core.BaseVO;

public class OutageCountVO extends BaseVO{
	private String areaType;
	private Integer tenantid;
	private Integer areaId;
	private Integer totalOutages;
	private Integer plannedOutages;
	private Integer unPlannedOutages;
	public String getAreaType() {
		return areaType;
	}
	public void setAreaType(String areaType) {
		this.areaType = areaType;
	}
	public Integer getTenantid() {
		return tenantid;
	}
	public void setTenantid(Integer tenantid) {
		this.tenantid = tenantid;
	}
	public Integer getAreaId() {
		return areaId;
	}
	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}
	public Integer getTotalOutages() {
		return totalOutages;
	}
	public void setTotalOutages(Integer totalOutages) {
		this.totalOutages = totalOutages;
	}
	public Integer getPlannedOutages() {
		return plannedOutages;
	}
	public void setPlannedOutages(Integer plannedOutages) {
		this.plannedOutages = plannedOutages;
	}
	public Integer getUnPlannedOutages() {
		return unPlannedOutages;
	}
	public void setUnPlannedOutages(Integer unPlannedOutages) {
		this.unPlannedOutages = unPlannedOutages;
	}
	
	
	


}
