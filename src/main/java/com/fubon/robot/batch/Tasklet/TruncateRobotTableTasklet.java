package com.fubon.robot.batch.Tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fubon.robot.batch.robotData.repository.CallingRobotRepository;

@Component
@StepScope
@Transactional
public class TruncateRobotTableTasklet implements Tasklet{
	@Autowired
	CallingRobotRepository callingRobotRepository;
	
	
	@Override
	@Transactional
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		//清除 所有 資料
		callingRobotRepository.deleteAll();
		return RepeatStatus.FINISHED;
	}
	
}
