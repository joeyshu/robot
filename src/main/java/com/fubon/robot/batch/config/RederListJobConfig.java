package com.fubon.robot.batch.config;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.fubon.robot.batch.Tasklet.FailureSendMail;
import com.fubon.robot.batch.Tasklet.GetRobots;
import com.fubon.robot.batch.Tasklet.ReaderTBus;
import com.fubon.robot.batch.Tasklet.SuccessNotify;
import com.fubon.robot.batch.batch.SystemFileSettinService;
import com.fubon.robot.batch.itemreader.CheckCreditListReader;
import com.fubon.robot.batch.itemreader.CheckListReader;
import com.fubon.robot.batch.itemreader.CheckTmListReader;
import com.fubon.robot.batch.itemreader.RrecentCalledReader;
import com.fubon.robot.batch.processor.BlackListCheckForCreditProcessor;
import com.fubon.robot.batch.processor.BlackListCheckForTmProcessor;
import com.fubon.robot.batch.processor.BlackListCheckProcessor;
import com.fubon.robot.batch.processor.BlackListProcessListener;
import com.fubon.robot.batch.processor.RecentCalledProcessor;
import com.fubon.robot.batch.processor.RecentCalledProcessorListener;
import com.fubon.robot.batch.robot.evdata.TBusCustomer;
import com.fubon.robot.batch.writer.RobotWriter;
import com.fubon.robot.batch.writer.WhiteListWriter;

@Configuration
@EnableBatchProcessing
public class RederListJobConfig {

	public static final String JOB_EXECUTION_EVOICE_DATA_PARAM = "readerList";
	
	public static final String JOB_EXECUTION_EVOICE_DATA_RESULT = "readerResult";

	public static final String JOB_EXECUTION_ROBOT_USERS = "userList";

	public static final String JOB_BLACK_VARIABLE_ID = "blacKVariableID";
	
	public static final String JOB_A_BATCH_CATH_SIZE = "aBatchALLList";
	
	public static final String JOB_A_BATCH_SUCCESS_SIZE = "aBatchSuccessList";
	
	public static final String JOB_A_BATCH_FAILURE_SIZE = "aBatchFailureList";
	
	public static final String JOB_A_MAIL_NAME =  "Batch-A";
	
	public static final String JOB_A_ACTIVITY_LIST = "ACTIVITYID";
	
	public static final String JOB_A_SYSVARIABLE = "SYSVARIABLE";
	
	@Autowired
	SystemFileSettinService systemFileSettinService;

	@Autowired
	private StepBuilderFactory stepBuildFactory;

	@Autowired
	ReaderTBus readerTBus;

	@Autowired
	FailureSendMail failureSendMail;

	@Autowired
	GetRobots getRobots;

	@Autowired
	CheckListReader checkListReader;

	@Autowired
	RrecentCalledReader rrecentCalledReader;

	@Autowired
	BlackListCheckProcessor blackListCheckProcessor;
	
	@Autowired
	BlackListCheckForCreditProcessor blackListCheckForCreditProcessor;
	
	@Autowired
	BlackListCheckForTmProcessor blackListCheckForTmProcessor;

	@Autowired
	RecentCalledProcessor recentCalledProcessor;

	@Autowired
	BlackListProcessListener blackListProcessListener;

	@Autowired
	RecentCalledProcessorListener recentCalledProcessorListener;
	
	@Autowired
	CheckTmListReader checkTmListReader;
	
	@Autowired
	CheckCreditListReader CheckCreditListReader;
	
	
	@Autowired
	RobotWriter robotWriter;

	@Autowired
	WhiteListWriter WhiteListWriter;

	@Autowired
	SuccessNotify successNotify;
	
	
	/**
	 * ?????? ???????????? ??????
	 * 
	 * @return
	 */
	@Bean
	public Flow readerFlow() {
		return new FlowBuilder<Flow>("readerFlow")
				.start(getRobotUsersStep()) // ?????????????????????
				.next(readerListStep())
				// .next(step)
				// ???????????? ??????
				.from(getRobotUsersStep()).on(ExitStatus.FAILED.getExitCode()).to(failureStep())
				.from(readerListStep()).on(ExitStatus.FAILED.getExitCode()).to(failureStep()).end();
	}

	/**
	 * ????????????
	 * 
	 * @return
	 */
	@Bean
	public Flow filterFlow() {
		return new FlowBuilder<Flow>("filterFlow")
				.start(filterByBlockedList())
				.next(filterByBlockedListForTm())
				.next(filterByBlockedListForCredit())
				.next(filterByRecentCalledList())
				.from(filterByRecentCalledList()).on(ExitStatus.COMPLETED.getExitCode()).to(successNotify())
				.from(filterByRecentCalledList()).on(ExitStatus.FAILED.getExitCode()).to(failureStep())
				.from(filterByBlockedList()).on(ExitStatus.FAILED.getExitCode()).to(failureStep())
				.from(filterByBlockedListForCredit()).on(ExitStatus.FAILED.getExitCode()).to(failureStep())
				.from(filterByBlockedListForTm()).on(ExitStatus.FAILED.getExitCode()).to(failureStep())
				.end();
	}
	
	
//	@Bean
//	public Flow robotWriterFlow() {
//		return new FlowBuilder<Flow>("robotWriterFlow")
//				.start(filterByBlockedList()) //TODO TASK Leat
//				.from(filterByBlockedList()).on(ExitStatus.COMPLETED.getExitCode()).to(successNotify())
//				.from(filterByBlockedList()).on(ExitStatus.FAILED.getExitCode()).to(failureStep())
//				.end();
//	}
	
	@Bean(name = "filterByBlockedListForCredit")
	public Step filterByBlockedListForCredit(){
		return stepBuildFactory.get("filterByBlockedListForCredit")
				.<TBusCustomer, TBusCustomer>chunk(10)
				.reader(CheckCreditListReader) // ?????? ?????? ????????????
				.processor(blackListCheckForCreditProcessor) // ?????? ????????????
				.writer(WhiteListWriter) // ??????????????????
				.listener(blackListProcessListener) // ????????????????????????
				.taskExecutor(new SimpleAsyncTaskExecutor())
				.throttleLimit(systemFileSettinService.getSysFileSetting().getNumberOfThreads())
				.build();
	}
	
	
	@Bean(name = "filterByBlockedListForTm")
	public Step filterByBlockedListForTm(){
		return stepBuildFactory.get("filterByBlockedListForTm")
				.<TBusCustomer, TBusCustomer>chunk(10)
				.reader(checkTmListReader) // ?????? ?????? ????????????
				.processor(blackListCheckForTmProcessor) // ?????? ????????????
				.writer(WhiteListWriter) // ??????????????????
				.listener(blackListProcessListener) // ????????????????????????
				.taskExecutor(new SimpleAsyncTaskExecutor())
				.throttleLimit(systemFileSettinService.getSysFileSetting().getNumberOfThreads())
				.build();
	}
	

	/**
	 * ?????????????????????
	 * 
	 * @return
	 */
	@Bean(name = "filterByBlockedList")
	public Step filterByBlockedList() {
		return stepBuildFactory.get("filterByBlockedList")
				.<TBusCustomer, TBusCustomer>chunk(10)
				.reader(checkListReader) // ?????? ?????? ????????????
				.processor(blackListCheckProcessor) // ?????? ????????????
				.writer(WhiteListWriter) // ??????????????????
				.listener(blackListProcessListener) // ????????????????????????
				.taskExecutor(new SimpleAsyncTaskExecutor())
				.throttleLimit(systemFileSettinService.getSysFileSetting().getNumberOfThreads())
				.build();
	}

	
	
	
	@Bean(name = "filterByRecentCalledList")
	public Step filterByRecentCalledList() {
		return stepBuildFactory.get("filterByRecentCalledList")
				.<TBusCustomer, TBusCustomer>chunk(10)
				.reader(rrecentCalledReader) // ?????? ?????? ????????????
				.processor(recentCalledProcessor) // ?????? ?????? ??????
				.listener(recentCalledProcessorListener) // ????????????????????????
				.writer(robotWriter) // ?????????????????? TODO ???????????? FLOW
//				.taskExecutor(new SimpleAsyncTaskExecutor()) 
//				.throttleLimit(systemFileSettinService.getSysFileSetting().getNumberOfThreads())
				.build();
	}
	
	
	
	/**
	 * ????????????????????? user ??????
	 * 
	 * @return
	 */
	@Bean(name = "getRobotUsersStep")
	public Step getRobotUsersStep() {
		return stepBuildFactory.get("getRobotUsersStep").tasklet(getRobots).build();
	}

	/**
	 * ???????????? ??????
	 * 
	 * @return
	 */
	@Bean(name = "readerListStep")
	public Step readerListStep() {
		return stepBuildFactory.get("readerListStep").tasklet(readerTBus).build();
	}

	/**
	 * ?????????IT ?????? ???????????????
	 * 
	 * @return
	 */
	@Bean(name = "successNotifyStep")
	public Step successNotify() {
		return stepBuildFactory.get("successNotifyStep").tasklet(successNotify).build();
	}

	/**
	 * ???????????? Mail
	 * 
	 * @return
	 */
	@Bean(name = "failureStep")
	public Step failureStep() {
		return stepBuildFactory.get("onFailed").tasklet(failureSendMail).build();
	}

//	@Override
//	@Autowired
//	public void setDataSource(@Qualifier("evoiceDbDataSource") DataSource dataSource) {
//		// TODO Auto-generated method stub
//		super.setDataSource(dataSource);
//	}
	
}
