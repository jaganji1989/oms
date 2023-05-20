package com.ezeeshipping.eficaa.oms.outage.vo;

import com.ezeeshipping.eficaa.oms.core.BaseVO;

public class OnGoingAffectedCustomerVO extends BaseVO{
	private String areaType;
	private Integer tenantid;
	private Integer areaId;
	private Integer categoryWiseCount;
	private Integer commercial;
	private Integer industrials;
	private Integer others;
	private Integer domestic;

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
	
	
	public Integer getCommercial() {
		return commercial;
	}
	public void setCommercial(Integer commercial) {
		this.commercial = commercial;
	}
	public Integer getAreaId() {
		return areaId;
	}
	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}
	public Integer getCategoryWiseCount() {
		return categoryWiseCount;
	}
	public void setCategoryWiseCount(Integer categoryWiseCount) {
		this.categoryWiseCount = categoryWiseCount;
	}
	public Integer getIndustrials() {
		return industrials;
	}
	public void setIndustrials(Integer industrials) {
		this.industrials = industrials;
	}
	public Integer getOthers() {
		return others;
	}
	public void setOthers(Integer others) {
		this.others = others;
	}
	public Integer getDomestic() {
		return domestic;
	}
	public void setDomestic(Integer domestic) {
		this.domestic = domestic;
	}
	
	
	
	
	
}
