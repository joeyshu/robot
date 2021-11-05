package com.fubon.robot.batch.Tasklet;

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fubon.robot.batch.SampleBatchApplication;
import com.fubon.robot.batch.Data.repository.TBusCustomerRepository;
import com.fubon.robot.batch.batch.SystemFileSettinService;
import com.fubon.robot.batch.config.RederListJobConfig;
import com.fubon.robot.batch.config.WriteListToRobtConfig;
import com.fubon.robot.batch.robot.data.Callinglistbot;
import com.fubon.robot.batch.robot.data.RobotDataDTO;
import com.fubon.robot.batch.robotData.repository.CallingRobotRepository;
import com.fubon.robot.batch.thread.post.DateUtils;

@Component
@StepScope
@Transactional
public class ReaderRobotReasult implements Tasklet {

	@Autowired
	TBusCustomerRepository respository;

	@Autowired
	SystemFileSettinService systemFileSettinService;

	@Autowired
	CallingRobotRepository callingRobotRepository;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		//B批次開始時間
		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
		.put(SampleBatchApplication.JOB_START_TIME, DateUtils.getNowTime());
		
		//抓取資料 
		List<Callinglistbot> list = callingRobotRepository.findAll();
		
		RobotDataDTO dto = new RobotDataDTO();
		
		
		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
		.put(WriteListToRobtConfig.JOB_B_ROBOT_DATA_SIZE,list.size());
		
		
		dto.setData(list);
		//存放MAIL 成功失敗的批次名稱
		
		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
		.put(SampleBatchApplication.JOB_MAIL_NAME, WriteListToRobtConfig.JOB_B_MAIL_NAME);
		
		//存放
		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
				.put(WriteListToRobtConfig.WRITE_JOB_DATA, dto);
		
		return RepeatStatus.FINISHED;
	}

}
