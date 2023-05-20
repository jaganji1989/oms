package com.ezeeshipping.eficaa.oms.outage.repository;

import org.springframework.data.repository.CrudRepository;

import com.ezeeshipping.eficaa.oms.outage.model.OutageExtensionRequest;

public interface OutageExtensionRepository  extends CrudRepository<OutageExtensionRequest, Integer>{
	OutageExtensionRequest findById(int id);
}
