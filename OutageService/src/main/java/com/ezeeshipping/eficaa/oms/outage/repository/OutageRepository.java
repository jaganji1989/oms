package com.ezeeshipping.eficaa.oms.outage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ezeeshipping.eficaa.oms.outage.model.Outage;



public interface OutageRepository extends CrudRepository<Outage, Integer> {

	 @Query("SELECT a FROM Outage a WHERE a.outageName=:outageName")
	    List<Outage> fetchOutages(@Param("outageName") String outageName);
	  
	 Outage findById(int id);
	 List<Outage> findAll();
	 
	 @Query("SELECT a FROM Outage a WHERE a.outageId=:outageId")
	 List<Outage> fetchOutagesByOutageId(@Param("outageId") String outageId);
}
