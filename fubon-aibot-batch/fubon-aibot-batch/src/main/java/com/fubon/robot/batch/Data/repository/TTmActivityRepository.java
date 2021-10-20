package com.fubon.robot.batch.Data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fubon.robot.batch.robot.evdata.TTmActivity;
@Repository
public interface TTmActivityRepository extends JpaRepository<TTmActivity, String> {
	
	
	@Query(nativeQuery = true,value = "SELECT result_id FROM t_tm_activity_call_result WHERE result_name LIKE '%系統自動結案%' and is_delete is null "
			+ "START WITH RESULT_PID= ( SELECT result_id FROM T_tm_activity tta WHERE "
			+ "activity_id = :activeId ) CONNECT BY RESULT_PID=prior RESULT_ID")
	public String findAutoCloseResultId(@Param("activeId") String activeId);
}
