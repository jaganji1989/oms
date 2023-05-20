package com.ezeeshipping.eficaa.oms.outage.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Formula;

import com.ezeeshipping.eficaa.oms.core.BaseObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "tbl_crewtask")

public class CrewTask extends BaseObject implements java.io.Serializable {
	private int id;

	private String name;
	private Integer fieldstaffid;
	private Integer outageid;
	private Integer mdmscheck;
	private Integer crewid;
	private Date plannedStartDate;
	private Date plannedEndDate;
	private String jobDetails;
	private String status;
	private Date actualStartDate;
	private Date actualEndDate;
	private String crewRemarks;
	private String crewname;
	private Integer division;
	private Integer feeder;
	private Integer transformer;
	private Integer substation;
	private Integer section;
	private Integer subdivision;
	private Integer outagedurations;
	private String divisionname;
	private String substationname;
	private String subdivisionname;
	private String feedername;
	private String transformername;

	public CrewTask() {
0	}

	public CrewTask(Date plannedStartDate, Date plannedEndDate, String jobDetails) {
		this.plannedStartDate = plannedStartDate;
		this.plannedEndDate = plannedEndDate;
		this.jobDetails = jobDetails;
	}

	@Id
	@GeneratedValue
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "fieldstaffid")
	public Integer getFieldstaffid() {
		return fieldstaffid;
	}

	public void setFieldstaffid(Integer fieldstaffid) {
		this.fieldstaffid = fieldstaffid;
	}

	@Column(name = "outageid")
	public Integer getOutageid() {
		return outageid;
	}

	public void setOutageid(Integer outageid) {
		this.outageid = outageid;
	}

	@Column(name = "crewid")

	public Integer getCrewid() {
		return crewid;
	}

	public void setCrewid(Integer crewid) {
		this.crewid = crewid;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "plannedstartdate")

	public Date getPlannedStartDate() {
		return plannedStartDate;
	}

	public void setPlannedStartDate(Date plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "plannedenddate")
	public Date getPlannedEndDate() {
		return plannedEndDate;
	}

	public void setPlannedEndDate(Date plannedEndDate) {
		this.plannedEndDate = plannedEndDate;
	}

	@Column(name = "jobdetails")
	public String getJobDetails() {
		return jobDetails;
	}

	public void setJobDetails(String jobDetails) {
		this.jobDetails = jobDetails;
	}

	public String getStatus() {
		return status;
	}

	@Column(name = "status")

	public void setStatus(String status) {
		this.status = status;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "actualstartdate")
	public Date getActualStartDate() {
		return actualStartDate;
	}

	public void setActualStartDate(Date actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "actualenddate")
	public Date getActualEndDate() {
		return actualEndDate;
	}

	public void setActualEndDate(Date actualEndDate) {
		this.actualEndDate = actualEndDate;
	}

	@Column(name = "crewremarks")

	public String getCrewRemarks() {
		return crewRemarks;
	}

	public void setCrewRemarks(String crewRemarks) {
		this.crewRemarks = crewRemarks;
	}

	@Formula("(SELECT tbl_crew.name FROM tbl_crew  WHERE tbl_crew.id = crewid)")
	public String getCrewname() {
		return crewname;
	}

	public void setCrewname(String crewname) {
		this.crewname = crewname;
	}
	/*
	 * @Formula("(SELECT tbl_substation.substation.name FROM tbl_outage   left outer join tbl_substation  on tbl_substation.id =tbl_outage.substation where tbl_outage.id = outageid)"
	 * ) public String getSubstationname() { return substationname; }
	 * 
	 * public void setSubstationname(String substationname) { this.substationname =
	 * substationname; }
	 */

	@Column(name = "mdmscheck")
	public Integer getMdmscheck() {
		return mdmscheck;
	}

	@Formula("(SELECT outage.outageduration FROM tbl_outage outage WHERE outage.id = outageid)")
	public Integer getOutagedurations() {
		return outagedurations;
	}

	public void setOutagedurations(Integer outagedurations) {
		this.outagedurations = outagedurations;
	}

	public void setMdmscheck(Integer mdmscheck) {
		this.mdmscheck = mdmscheck;
	}

	@Formula("(SELECT outage.subdivision FROM tbl_outage outage " + " WHERE outage.id = outageid)")
	public Integer getSubdivision() {
		return subdivision;
	}

	public void setSubdivision(Integer subdivision) {
		this.subdivision = subdivision;
	}

	@Formula("(SELECT division.name FROM tbl_outage outage "
			+ " left outer join tbl_division division on division.id = outage.division"
			+ " WHERE outage.id = outageid)")
	public String getDivisionname() {
		return divisionname;
	}

	public void setDivisionname(String divisionname) {
		this.divisionname = divisionname;
	}

	@Formula("(SELECT substation.name FROM tbl_outage outage "
			+ " left outer join tbl_substation substation on substation.id = outage.substation"
			+ " WHERE outage.id = outageid)")
	public String getSubstationname() {
		return substationname;
	}

	public void setSubstationname(String substationname) {
		this.substationname = substationname;
	}

	@Formula("(SELECT subdivisions.name FROM tbl_outage outage "
			+ " left outer join tbl_subdivision subdivisions on subdivisions.id = outage.subdivision"
			+ " WHERE outage.id = outageid)")
	public String getSubdivisionname() {
		return subdivisionname;
	}

	public void setSubdivisionname(String subdivisionname) {
		this.subdivisionname = subdivisionname;
	}

	@Formula("(SELECT fdr.name FROM tbl_outage outage " + " left outer join tbl_feeder fdr on fdr.id = outage.feeder"
			+ " WHERE outage.id = outageid)")
	public String getFeedername() {
		return feedername;
	}

	public void setFeedername(String feedername) {
		this.feedername = feedername;
	}

	@Formula("(SELECT outage.division FROM tbl_outage outage " + " WHERE outage.id = outageid)")
	public Integer getDivision() {
		return division;
	}

	public void setDivision(Integer division) {
		this.division = division;
	}

	@Formula("(SELECT outage.feeder FROM tbl_outage outage " + " WHERE outage.id = outageid)")
	public Integer getFeeder() {
		return feeder;
	}

	public void setFeeder(Integer feeder) {
		this.feeder = feeder;
	}

	@Formula("(SELECT outage.transformer FROM tbl_outage outage " + " WHERE outage.id = outageid)")
	public Integer getTransformer() {
		return transformer;
	}

	public void setTransformer(Integer transformer) {
		this.transformer = transformer;
	}

	@Formula("(SELECT outage.substation FROM tbl_outage outage " + " WHERE outage.id = outageid)")
	public Integer getSubstation() {
		return substation;
	}

	public void setSubstation(Integer substation) {
		this.substation = substation;
	}

	public void setTransformername(String transformername) {
		this.transformername = transformername;
	}

	@Formula("(SELECT tr.name FROM tbl_outage outage "
			+ " left outer join tbl_transformer tr on tr.id = outage.transformer" + " WHERE outage.id = outageid)")
	public String getTransformername() {
		return transformername;
	}

}
