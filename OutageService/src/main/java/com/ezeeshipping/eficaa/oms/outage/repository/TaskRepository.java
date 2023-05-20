package com.ezeeshipping.eficaa.oms.outage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ezeeshipping.eficaa.oms.outage.model.CrewTask;



public interface TaskRepository extends CrudRepository<CrewTask, Integer> {

	CrewTask findById(int id);
	
	@Query("select cr from CrewTask cr where cr.outageid =:outageid")
	List<CrewTask> findCrewTaskByOutageId(@Param("outageid") Integer outageid);
	
	
}
