package com.ezeeshipping.eficaa.oms.outage.vo;

import com.ezeeshipping.eficaa.oms.core.BaseVO;

public class LatestOutagesVO extends BaseVO{
	private String areaType;
	private Integer tenantid;
	private Integer areaId;
	private String circle;
	private Integer dtr;
	private String section;
	private String subDivision;
	
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
	public String getCircle() {
		return circle;
	}
	public void setCircle(String circle) {
		this.circle = circle;
	}
	
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public String getSubDivision() {
		return subDivision;
	}
	public void setSubDivision(String subDivision) {
		this.subDivision = subDivision;
	}
	public Integer getDtr() {
		return dtr;
	}
	public void setDtr(Integer dtr) {
		this.dtr = dtr;
	}
	
	
	

}
