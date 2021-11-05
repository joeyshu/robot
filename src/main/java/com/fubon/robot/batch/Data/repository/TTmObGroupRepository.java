package com.fubon.robot.batch.Data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fubon.robot.batch.robot.evdata.TTmObGroup;

@Repository
public interface TTmObGroupRepository extends JpaRepository<TTmObGroup, String>{
	@Query("select t from TTmObGroup t where t.isDelete != 'Y' and t.obGroupNo = :obGroupNo and t.activityVariableKey = :activityVariableKey ")
	public List<TTmObGroup> findObgroup(@Param("obGroupNo") String obGroupNo ,@Param("activityVariableKey") String  activityVariableKey);
	
}
