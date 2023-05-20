/**
 * 
 */
package com.ezeeshipping.eficaa.oms.outage.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ezeeshipping.eficaa.oms.outage.model.OutageV2;

/**
 * @author Dell
 *
 */
public interface OutageRepositoryV2 extends CrudRepository<OutageV2, Integer> {
	@Query("SELECT a FROM Outage a WHERE a.outageName=:outageName")
	List<OutageV2> fetchOutages(@Param("outageName") String outageName);

	OutageV2 findById(int id);

	List<OutageV2> findAll();

	@Query("SELECT a FROM OutageV2 a WHERE a.outageId=:outageId")
	List<OutageV2> fetchOutagesByOutageId(@Param("outageId") String outageId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE tbl_outage o set confirmmailts =:confirmmailts where o.id = :id", nativeQuery = true)
	void updateConfirmmailts(@Param("confirmmailts") Date confirmmailts, @Param("id") Integer id);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE tbl_outage o set confirmsmsts =:confirmsmsts where o.id = :id", nativeQuery = true)
	void updateConfirmsmsts(@Param("confirmsmsts") Date confirmsmsts, @Param("id") Integer id);
	@Transactional
	@Modifying
	@Query(value = "UPDATE tbl_outage o set approvemailts =:approvemailts where o.id = :id", nativeQuery = true)
	void updateApprovemailts(@Param("approvemailts") Date approvemailts, @Param("id") Integer id);

	@Transactional
	@Modifying
	@Query(value = "UPDATE tbl_outage o set approvesmsts =:approvesmsts where o.id = :id", nativeQuery = true)
	void updateApprovesmsts(@Param("approvesmsts") Date approvesmsts, @Param("id") Integer id);
	
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE tbl_outage o set reschedulemailts =:reschedulemailts,approvemailts=null,rejectmailts=null where o.id = :id", nativeQuery = true)
	void updateReschedulemailts(@Param("reschedulemailts") Date reschedulemailts, @Param("id") Integer id);

	@Transactional
	@Modifying
	@Query(value = "UPDATE tbl_outage o set reschedulesmsts =:reschedulesmsts,approvesmsts=null,rejectsmsts=null where o.id = :id", nativeQuery = true)
	void updateReschedulesmsts(@Param("reschedulesmsts") Date reschedulesmsts, @Param("id") Integer id);
	
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE tbl_outage o set rejectmailts =:rejectmailts where o.id = :id", nativeQuery = true)
	void updateRejectmailts(@Param("rejectmailts") Date rejectmailts, @Param("id") Integer id);

	@Transactional
	@Modifying
	@Query(value = "UPDATE tbl_outage o set rejectsmsts =:rejectsmsts where o.id = :id", nativeQuery = true)
	void updateRejectsmsts(@Param("rejectsmsts") Date rejectsmsts, @Param("id") Integer id);

	@Transactional
	@Modifying
	@Query(value = "UPDATE tbl_outage o set completemailts =:completemailts where o.id = :id", nativeQuery = true)
	void updateCompletemailts(@Param("completemailts") Date completemailts, @Param("id") Integer id);

	@Transactional
	@Modifying
	@Query(value = "UPDATE tbl_outage o set completesmsts =:completesmsts where o.id = :id", nativeQuery = true)
	void updateCompletesmsts(@Param("completesmsts") Date completesmsts, @Param("id") Integer id);

	@Transactional
	@Modifying
	@Query(value = "UPDATE tbl_outage o set  rejectsmsts =null,rejectmailts=null,reschedulesmsts =null,reschedulemailts=null,approvesmsts=null,approvemailts=null where o.id = :id", nativeQuery = true)
	void updateRescheduleRequest( @Param("id") Integer id);
	
}
