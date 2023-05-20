/**
 * 
 */
package com.ezeeshipping.eficaa.oms.outage.vo;

/**
 * @author Dell
 *
 */
public class OutageNotificationVO {
	 private String outageId;
	 private String outageName;
	 private boolean  isPlanned;
	 private String networkElementType;
	 private String networkElementUID;
	 private String status;
	 private boolean  isRescheduled;
	 private OutageVO outageVO;
	 public OutageNotificationVO() {
		 
	 }
	 

	public OutageNotificationVO(String outageId, String outageName, String networkElementType, String networkElementUID,
			String status, OutageVO outageVO) {
		super();
		this.outageId = outageId;
		this.outageName = outageName;
		this.networkElementType = networkElementType;
		this.networkElementUID = networkElementUID;
		this.status = status;
		this.outageVO = outageVO;
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

	public boolean isPlanned() {
		return isPlanned;
	}

	public void setPlanned(boolean isPlanned) {
		this.isPlanned = isPlanned;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isRescheduled() {
		return isRescheduled;
	}

	public void setRescheduled(boolean isRescheduled) {
		this.isRescheduled = isRescheduled;
	}

	public OutageVO getOutageVO() {
		return outageVO;
	}

	public void setOutageVO(OutageVO outageVO) {
		this.outageVO = outageVO;
	}
	 
	 
}
