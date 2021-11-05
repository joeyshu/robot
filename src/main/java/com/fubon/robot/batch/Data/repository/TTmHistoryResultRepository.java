package com.fubon.robot.batch.Data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fubon.robot.batch.robot.evdata.TTmHistoryResult;

@Repository
public interface TTmHistoryResultRepository extends JpaRepository<TTmHistoryResult, String> {
	
	
	public List<TTmHistoryResult> findByCallingTimeBetweenAndCustIdAndCustomerStatusNot(String startDate,String endDate,String custId,String status);
	
	
	public List<TTmHistoryResult> findByCallingTimeBetweenAndCustIdAndCustomerStatusNotInAndCsrIdIn(String startDate,String endDate,String custId,List<String> status,List<String> csrId);


	public List<TTmHistoryResult> findByCallingTimeBetweenAndCustIdAndCustomerStatusNotInAndCsrIdNotIn(String startDate,String endDate,String custId,List<String> status,List<String> csrId);

}
