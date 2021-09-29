package com.fubon.robot.batch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.fubon.robot.batch.Tasklet.FailureSendMail;
import com.fubon.robot.batch.Tasklet.ReaderRobotReasult;
import com.fubon.robot.batch.Tasklet.SuccessNotify;
import com.fubon.robot.batch.Tasklet.TruncateRobotTableTasklet;
import com.fubon.robot.batch.batch.SystemFileSettinService;
import com.fubon.robot.batch.itemreader.CheckWriteJobDataReader;
import com.fubon.robot.batch.processor.AnswerProcessor;
import com.fubon.robot.batch.robot.data.Callinglistbot;

@Configuration
@EnableBatchProcessing
public class WriteListToRobtConfig{
	public static final String WRITE_JOB_DATA = "WriteJobData";

	public static final String JOB_B_MAIL_NAME = "Batch-B";
	
	public static final String JOB_B_ROBOT_DATA_SIZE = "DATASIZE";

//    @Autowired
//    private JobBuilderFactory jobBuijlderFactory;

	@Autowired
	private StepBuilderFactory stepBuildFactory;

	@Autowired
	FailureSendMail failureSendMail;

	@Autowired
	ReaderRobotReasult readerRobotReasult;

	@Autowired
	CheckWriteJobDataReader checkWriteJobDataReader;

	@Autowired
	AnswerProcessor answerProcessor;

	@Autowired
	TruncateRobotTableTasklet truncateRobotTableTasklet;
	
	@Autowired
	SuccessNotify successNotify;
	
	@Autowired
	SystemFileSettinService systemFileSettinService;
	
	/**
	 * 讀取 資料 判斷 結果
	 * 
	 * 
	 * @return
	 */
	@Bean
	public Flow readerRobotFlow() {
		return new FlowBuilder<Flow>("readerRobotFlow")
				.start(readerRobotListStep()) // 讀取 資料 使用一個TASKLET
				.next(checkActionResult()) // 確認狀態 判斷 資料 STEP
				.next(clearRobotTableStep()) // 最後清除資料
				.from(checkActionResult()).on(ExitStatus.FAILED.getExitCode()).to(writerFailureStep())
				.from(clearRobotTableStep()).on(ExitStatus.COMPLETED.getExitCode()).to(writeSuccessNotifyStep()) // 且寄送MAIL
				.end();
	}

	/**
	 * TODO 抓取所有名單 後
	 * 
	 * @return
	 */
	@Bean(name = "checkActionResult")
	public Step checkActionResult() {
		return stepBuildFactory.get("checkActionResult").<Callinglistbot, Callinglistbot>chunk(10)
				.reader(checkWriteJobDataReader) // 讀取 上個 資料來源
				.processor(answerProcessor) // 處理 重複 名單
//				.taskExecutor(new SimpleAsyncTaskExecutor()) //TODO 切割 不同的 內容 可能影響 最終統計數字?
//				.throttleLimit(systemFileSettinService.getSysFileSetting().getNumberOfThreads())
				.build();
	}

	/**
	 * 抓取全部 筆數
	 * 
	 * @return
	 */
	@Bean(name = "readerRobotListStep")
	public Step readerRobotListStep() {
		return stepBuildFactory.get("readerRobotListStep").tasklet(readerRobotReasult).build();
	}
	/**
	 * 清除 機器人所有資料
	 * @return
	 */
	@Bean(name = "clearRobotTableStep")
	public Step clearRobotTableStep() {
		return stepBuildFactory.get("clearRobotTableStep").tasklet(truncateRobotTableTasklet).build();
	}

	// ==================================================================================
	// 失敗部分
	/**
	 * 失敗傳送MAIL
	 * 
	 * @return
	 */
	@Bean(name = "writerFailureStep")
	public Step writerFailureStep() {
		return stepBuildFactory.get("writerFailureStep").tasklet(failureSendMail).build();
	}

	/**
	 * 成功寄送 Mail
	 * 
	 * @return
	 */
	@Bean(name = "writeSuccessNotifyStep")
	public Step writeSuccessNotifyStep() {
		return stepBuildFactory.get("writeSuccessNotifyStep").tasklet(successNotify).build();
	}
	
	

	
}
