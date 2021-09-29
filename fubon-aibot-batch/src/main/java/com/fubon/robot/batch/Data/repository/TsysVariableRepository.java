package com.fubon.robot.batch.Data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fubon.robot.batch.robot.evdata.TSysVariable;
@Repository
public interface TsysVariableRepository extends JpaRepository<TSysVariable, String> {
	
	
}
