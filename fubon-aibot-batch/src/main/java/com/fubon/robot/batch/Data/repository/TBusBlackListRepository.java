package com.fubon.robot.batch.Data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fubon.robot.batch.robot.evdata.Blacklist;

@Repository
public interface TBusBlackListRepository extends JpaRepository<Blacklist, String> {
	
	public Blacklist findByIdentityIdAndDeleteMarkNotAndDeleteMarkNotAndStatusIn(String identityId, String deleteMark,
			String deleteMarklowCase, List<String> status);

}
