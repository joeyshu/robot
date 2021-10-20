package com.fubon.robot.batch.Data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fubon.robot.batch.robot.evdata.TTmActivityCallResult;
@Repository
public interface TTmActivityCallResultRepository extends JpaRepository<TTmActivityCallResult, String> {

//	由上至下的 查詢
//	select * from T_TM_ACTIVITY_CALL_RESULT t 
//
//	start with t.result_id = '2c9149794eba37ea014edde507d9613f'
//	connect by prior t.result_id = t.result_pid
	@Query(value = "	select * from T_TM_ACTIVITY_CALL_RESULT t  start with t.result_id = :resulId "
			+ " connect by prior t.result_id = t.result_pid  ", nativeQuery = true)
	public List<TTmActivityCallResult> getActivityRootResultList(@Param("resulId") String resulId);

}
