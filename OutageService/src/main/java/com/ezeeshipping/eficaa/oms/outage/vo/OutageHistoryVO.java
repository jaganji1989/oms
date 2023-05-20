package com.ezeeshipping.eficaa.oms.outage.vo;

import java.util.Date;

import com.ezeeshipping.eficaa.oms.core.BaseVO;

public class OutageHistoryVO extends BaseVO{
	 private int id;
	    private Integer outageid;
	    private String code;
	    private String description;
	    private String createdbyname;
	    public OutageHistoryVO() {
	    	
	    }
		public OutageHistoryVO( Integer outageid, String code, String description,Integer tenantid,Date createddate,String createdbyname) {
			super();
			this.outageid = outageid;
			this.code = code;
			this.description = description;
			this.createdbyname = createdbyname;
			super.setTenantid(tenantid);
			super.setCreateddate(createddate);
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public Integer getOutageid() {
			return outageid;
		}
		public void setOutageid(Integer outageid) {
			this.outageid = outageid;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getCreatedbyname() {
			return createdbyname;
		}
		public void setCreatedbyname(String createdbyname) {
			this.createdbyname = createdbyname;
		}


}
