package com.fubon.robot.batch.Tasklet;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fubon.robot.batch.Data.repository.TBusCustomerRepository;
import com.fubon.robot.batch.batch.SystemFileSettinService;
import com.fubon.robot.batch.config.RederListJobConfig;
import com.fubon.robot.batch.robot.evdata.ReadListDTO;
import com.fubon.robot.batch.robot.evdata.RobotsDTO;
import com.fubon.robot.batch.robot.evdata.TBusCustomer;
import com.fubon.robot.batch.robot.evdata.TSysUser;

/**
 * 1 依照有效時間 + 設定USER 抓取 2 DistributeTime 排序依照 3 存放紀錄 >> 到記憶體。
 * 
 * @author dell5490
 *
 */
@Component
@StepScope
@Transactional
public class ReaderTBus implements Tasklet {

	private static final String findListStatusUnCall = "210";
	private static final String findListStatusUnFinish = "216";

	@Autowired
	TBusCustomerRepository respository;

	@Autowired
	SystemFileSettinService systemFileSettinService;

	private static List<TBusCustomer> personList;

	/** 
	 * TODO reader setting
	 * 抓取 全部虛擬user
	 */
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		RobotsDTO sysFileRobotUsers = (RobotsDTO) chunkContext.getStepContext().getStepExecution().getJobExecution()
				.getExecutionContext().get(RederListJobConfig.JOB_EXECUTION_ROBOT_USERS);
		List<TSysUser> setingQueryUsers = sysFileRobotUsers.getRobotDatas();
		ReadListDTO redlistData = new ReadListDTO();
		
		//查出多個人
		ArrayList<TBusCustomer> allData = new ArrayList<TBusCustomer>();
		ArrayList<String>  receiveUserIds = new ArrayList<String>();
 		for (TSysUser user : setingQueryUsers) {
 			receiveUserIds.add(user.getUserId());
		}
		//多個人一次查詢 
		List<TBusCustomer> readerList = respository.findListNonShot(receiveUserIds,
				Arrays.asList(findListStatusUnCall, findListStatusUnFinish),
				new Date(System.currentTimeMillis()).toString(),
				new PageRequest(0, systemFileSettinService.getSysFileSetting().getRowQuantity())); // 最大數量筆數設定。
		
		redlistData.setData(readerList);
		
		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
				.put(RederListJobConfig.JOB_EXECUTION_EVOICE_DATA_PARAM, redlistData);

		// 抓取筆數
		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
				.put(RederListJobConfig.JOB_A_BATCH_CATH_SIZE, allData.size());
		
		
		

		return RepeatStatus.FINISHED;
	}

}
