package com.ezeeshipping.eficaa.oms.outage.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ezeeshipping.eficaa.oms.core.BaseObject;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Entity
@Table(name = "tbl_outage_history")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OutageHistory extends BaseObject implements java.io.Serializable {
	private int id;
    private Integer outageid;
    private String code;
    private String description;
    private String createdbyname;
    
    @Id
	@GeneratedValue
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Column(name = "outageid")

	public Integer getOutageid() {
		return outageid;
	}
	public void setOutageid(Integer outageid) {
		this.outageid = outageid;
	}
	@Column(name = "code")

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Column(name = "description")

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name = "createdbyname")
	public String getCreatedbyname() {
		return createdbyname;
	}
	public void setCreatedbyname(String createdbyname) {
		this.createdbyname = createdbyname;
	}
    
    
}
