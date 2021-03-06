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
	 * ?????? ?????? ?????? ??????
	 * 
	 * 
	 * @return
	 */
	@Bean
	public Flow readerRobotFlow() {
		return new FlowBuilder<Flow>("readerRobotFlow")
				.start(readerRobotListStep()) // ?????? ?????? ????????????TASKLET
				.next(checkActionResult()) // ???????????? ?????? ?????? STEP
				.next(clearRobotTableStep()) // ??????????????????
				.from(checkActionResult()).on(ExitStatus.FAILED.getExitCode()).to(writerFailureStep())
				.from(clearRobotTableStep()).on(ExitStatus.COMPLETED.getExitCode()).to(writeSuccessNotifyStep()) // ?????????MAIL
				.end();
	}

	/**
	 * TODO ?????????????????? ???
	 * 
	 * @return
	 */
	@Bean(name = "checkActionResult")
	public Step checkActionResult() {
		return stepBuildFactory.get("checkActionResult").<Callinglistbot, Callinglistbot>chunk(10)
				.reader(checkWriteJobDataReader) // ?????? ?????? ????????????
				.processor(answerProcessor) // ?????? ?????? ??????
//				.taskExecutor(new SimpleAsyncTaskExecutor()) //TODO ?????? ????????? ?????? ???????????? ???????????????????
//				.throttleLimit(systemFileSettinService.getSysFileSetting().getNumberOfThreads())
				.build();
	}

	/**
	 * ???????????? ??????
	 * 
	 * @return
	 */
	@Bean(name = "readerRobotListStep")
	public Step readerRobotListStep() {
		return stepBuildFactory.get("readerRobotListStep").tasklet(readerRobotReasult).build();
	}
	/**
	 * ?????? ?????????????????????
	 * @return
	 */
	@Bean(name = "clearRobotTableStep")
	public Step clearRobotTableStep() {
		return stepBuildFactory.get("clearRobotTableStep").tasklet(truncateRobotTableTasklet).build();
	}

	// ==================================================================================
	// ????????????
	/**
	 * ????????????MAIL
	 * 
	 * @return
	 */
	@Bean(name = "writerFailureStep")
	public Step writerFailureStep() {
		return stepBuildFactory.get("writerFailureStep").tasklet(failureSendMail).build();
	}

	/**
	 * ???????????? Mail
	 * 
	 * @return
	 */
	@Bean(name = "writeSuccessNotifyStep")
	public Step writeSuccessNotifyStep() {
		return stepBuildFactory.get("writeSuccessNotifyStep").tasklet(successNotify).build();
	}
	
	

	
}
