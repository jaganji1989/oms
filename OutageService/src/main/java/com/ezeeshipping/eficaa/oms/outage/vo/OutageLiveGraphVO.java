package com.ezeeshipping.eficaa.oms.outage.vo;

import com.ezeeshipping.eficaa.oms.core.BaseVO;

public class OutageLiveGraphVO extends BaseVO{
	private String areaType;
	private Integer tenantid;
	private Integer areaId;
	private Integer countOfOutages;
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
	public Integer getCountOfOutages() {
		return countOfOutages;
	}
	public void setCountOfOutages(Integer countOfOutages) {
		this.countOfOutages = countOfOutages;
	}
	
	
}
