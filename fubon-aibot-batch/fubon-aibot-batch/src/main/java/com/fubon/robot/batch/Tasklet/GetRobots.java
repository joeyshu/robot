package com.fubon.robot.batch.Tasklet;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fubon.robot.batch.SampleBatchApplication;
import com.fubon.robot.batch.Data.repository.TSysUserRepository;
import com.fubon.robot.batch.batch.SysSeting;
import com.fubon.robot.batch.batch.SystemFileSettinService;
import com.fubon.robot.batch.config.RederListJobConfig;
import com.fubon.robot.batch.robot.evdata.RobotsDTO;
import com.fubon.robot.batch.robot.evdata.TSysUser;
import com.fubon.robot.batch.thread.post.DateUtils;

@Component
@StepScope
@Transactional
public class GetRobots implements Tasklet {

	@Autowired
	SystemFileSettinService systemFileSettinService;

	@Autowired
	TSysUserRepository userRepository;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		SysSeting seting = systemFileSettinService.getSysFileSetting();
		
		//A批次 開始時間
		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
		.put(SampleBatchApplication.JOB_START_TIME, DateUtils.getNowTime());
		
		List<String> robotUserCodes = Arrays.asList(seting.getRobotUserNumber().split(","));
		List<TSysUser> robotUsers = userRepository.findByUserCodeIn(robotUserCodes);
		RobotsDTO robotsDTO = new RobotsDTO(robotUsers);
		
		//E-voice 才可撥電話 但虛擬USER 為  USER 設定的 我想應該不會有不是 該部門無法撥出的情況。 而且撥出為  機器人非虛擬USER
		
//		List<Map> departs = departService.findAllChildDeptById("d4f9048a-9c21-4324-84f8-d4036093e583");//電銷部門ID	for PRD
//		for(int i=0;i<departs.size();i++){
//			if(departs.get(i).toString().indexOf(depId) != -1){
//				String d = "1";			}
//		}
//		
//		//接著檢查是否是信用卡部門 By Luke ----start----
//
//		departs = departService.findAllChildDeptById("8138e023-c9a5-46df-b469-dde801ebdecc");//信用卡部門ID	for PRD
//		for(int i=0;i<departs.size();i++){
//			if(departs.get(i).toString().indexOf(depId) != -1){
//				String d = "2";
//			}
//		}	
		//設定抓取名稱
		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
		.put(SampleBatchApplication.JOB_MAIL_NAME, RederListJobConfig.JOB_A_MAIL_NAME);
		
		
		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
				.put(RederListJobConfig.JOB_EXECUTION_ROBOT_USERS, robotsDTO);
		return RepeatStatus.FINISHED;
	}

}
