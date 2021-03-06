package com.fubon.robot.batch.processor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fubon.robot.batch.Data.repository.TBusCustomerRepository;
import com.fubon.robot.batch.Data.repository.TSysUserRepository;
import com.fubon.robot.batch.Data.repository.TTmHistoryResultRepository;
import com.fubon.robot.batch.Tasklet.ReaderTBus;
import com.fubon.robot.batch.batch.SysSeting;
import com.fubon.robot.batch.batch.SystemFileSettinService;
import com.fubon.robot.batch.config.RederListJobConfig;
import com.fubon.robot.batch.robot.evdata.TBusCustomer;
import com.fubon.robot.batch.robot.evdata.TSysUser;
import com.fubon.robot.batch.robot.evdata.TTmHistoryResult;
import com.fubon.robot.batch.thread.post.DateUtils;

@Component
@StepScope
@Transactional
public class RecentCalledProcessor implements ItemProcessor<TBusCustomer, TBusCustomer> {

	private static final String findListStatusUnCall = "210";
	private static final String findListStatusUnFinish = "216";

	@Autowired
	TBusCustomerRepository tBusCustomerRepository;

	@Autowired
	TTmHistoryResultRepository tTmHistoryResultRepository;

	@Autowired
	TSysUserRepository tSysUserRepository;

	@Autowired
	SystemFileSettinService systemFileSettinService;

	private DriverManagerDataSource dataSource;

	private List<String> robotUsers = new ArrayList<String>();

	private SysSeting sysSeting;

	private Integer successSize = 0;

	private Integer failureSize = 0;
	
	private static final List<String> checkCustomerRecent =  new ArrayList<String>();
	

	/**
	 * ????????????
	 * 
	 * @param stepExecution
	 */
	@BeforeStep
	public void prepareData(StepExecution stepExecution) {
		failureSize = (Integer) stepExecution.getJobExecution().getExecutionContext()
				.get(RederListJobConfig.JOB_A_BATCH_FAILURE_SIZE);

		// ????????????
		sysSeting = systemFileSettinService.getSysFileSetting();

		// ??????user
		List<String> userCodes = Arrays.asList(sysSeting.getRobotUserNumber().split(","));
		List<TSysUser> users = tSysUserRepository.findByUserCodeIn(userCodes);
		for (TSysUser user : users) {
			robotUsers.add(user.getUserId());
		}
		// ?????????DB ????????????
		dataSource = new DriverManagerDataSource();
		dataSource.setUrl(sysSeting.getAibotDBServerAddress());
		dataSource.setUsername(sysSeting.getAibotDBServerAccount());
		dataSource.setPassword(sysSeting.getAibotDBServerPwd());
		dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	}
	/**
	 * ????????????????????????
	 * @param stepExecution
	 */
	@AfterStep
	public void setingSuccessAndFailureSize(StepExecution stepExecution) {
		stepExecution.getJobExecution().getExecutionContext().put(RederListJobConfig.JOB_A_BATCH_SUCCESS_SIZE,
				successSize);

		stepExecution.getJobExecution().getExecutionContext().put(RederListJobConfig.JOB_A_BATCH_FAILURE_SIZE,
				failureSize);
	}
	/**
	 * ???????????? +1 ?????? 
	 */
	@Override
	public TBusCustomer process(TBusCustomer item) throws Exception {
		boolean exclude = checkRecentCalled(item);
		
		if (exclude) {
			failureSize = new Integer(failureSize.intValue()+1);
			return null;
		}
		successSize = new Integer(successSize.intValue()+1);
		// ??????????????? writer (RobotWriter) ???????????????
		return item;
	}

	protected boolean checkRecentCalled(TBusCustomer item) throws SQLException {
		// 1 -------------????????????CSR ?????????EV???????????????????????? -----------------------
		boolean isUserCalled = checkEvCalling(item);

		// 2 ---- ???????????? ??????????????? ??????USER ???????????????
		boolean isUserCalling = checkAfterListNonUserCalling(item);
		
		// 3 -- ???????????? 
		
		if( checkCustomerRecent.contains(item.getCustomer_28())) {
			return true;
		} else {
			checkCustomerRecent.add(item.getCustomer_28());
		}
		if (isUserCalled || isUserCalling) {
			return true;
		}
		return false;
	}

	/**
	 * ?????? Evoice ??????????????????
	 * 
	 * @param item
	 * @return
	 */
	private boolean checkEvCalling(TBusCustomer item) {
		// ?????? ?????? ?????? ??????
		Integer repeatCycle = systemFileSettinService.getSysFileSetting().getDedupingOutboundByEventsInDaystypeOfTelemarketingOfCreditLoans();
		
		Integer repeatCycleB = systemFileSettinService.getSysFileSetting().getDedupingOutboundByAibotInDays();
		
		String endDate = DateUtils.getNowTime();
		
		String startDate = DateUtils.addDay(new Date(), DateUtils.DATE_FULL_STR, -repeatCycle);
		
		String startDateB = DateUtils.addDay(new Date(), DateUtils.DATE_FULL_STR, -repeatCycleB);
		
		List<TTmHistoryResult> reuslt = tTmHistoryResultRepository
				.findByCallingTimeBetweenAndCustIdAndCustomerStatusNotInAndCsrIdIn(startDate, endDate, item.getCustomerId(),
						Arrays.asList(findListStatusUnCall, findListStatusUnFinish),robotUsers);
		
		
		List<TTmHistoryResult> reusltB = tTmHistoryResultRepository
				.findByCallingTimeBetweenAndCustIdAndCustomerStatusNotInAndCsrIdNotIn(startDateB, endDate, item.getCustomerId(),
						Arrays.asList(findListStatusUnCall, findListStatusUnFinish),robotUsers);
		
		int repeat = reuslt.size() + reusltB.size();
		
		
		if (repeat > 0) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * ???????????? ????????????????????? ??????????????????????????? ???????????????????????????
	 * 
	 * @param item
	 * @return
	 */
	private boolean checkAfterListNonUserCalling(TBusCustomer item) {
		String noewDate = DateUtils.getNowTime(DateUtils.DATE_SMALL_STR);
		List<TBusCustomer> reuslt = tBusCustomerRepository.findByUserFutureList(noewDate, item.getCustomer_28(),
				robotUsers);
		if (reuslt.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

}
