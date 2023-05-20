package com.ezeeshipping.eficaa.oms.outage.repository;

import org.springframework.data.repository.CrudRepository;

import com.ezeeshipping.eficaa.oms.outage.model.OutageHistory;


public interface OutageHistoryRepository  extends CrudRepository<OutageHistory, Integer> {

}
