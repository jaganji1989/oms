package com.ezeeshipping.eficaa.oms.outage.vo;

import com.ezeeshipping.eficaa.oms.core.BaseVO;

public class IndicesVO extends BaseVO{
	private String areaType;
	private Integer tenantid;
	private Integer areaId;
	private Integer saidi;
	private Integer saifi;
	private Integer caidi;
	private Integer caifi;
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
	public Integer getSaidi() {
		return saidi;
	}
	public void setSaidi(Integer saidi) {
		this.saidi = saidi;
	}
	public Integer getSaifi() {
		return saifi;
	}
	public void setSaifi(Integer saifi) {
		this.saifi = saifi;
	}
	public Integer getCaidi() {
		return caidi;
	}
	public void setCaidi(Integer caidi) {
		this.caidi = caidi;
	}
	public Integer getCaifi() {
		return caifi;
	}
	public void setCaifi(Integer caifi) {
		this.caifi = caifi;
	}
	
	
	
	
}
