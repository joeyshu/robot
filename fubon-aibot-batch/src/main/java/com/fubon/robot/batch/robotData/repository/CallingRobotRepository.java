package com.fubon.robot.batch.robotData.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fubon.robot.batch.robot.data.Callinglistbot;
@Repository
public interface CallingRobotRepository extends JpaRepository<Callinglistbot, Long>{

}
