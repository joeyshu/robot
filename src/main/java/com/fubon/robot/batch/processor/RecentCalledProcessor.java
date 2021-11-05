package com.fubon.robot.batch.processor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

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
import com.fubon.robot.batch.log.LogSetting;
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
	

	private static Logger logger = LogSetting.geterLoger(Logger.getLogger("RecentCalledProcessor"));
	
	
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
	 * 準備資料
	 * 
	 * @param stepExecution
	 */
	@BeforeStep
	public void prepareData(StepExecution stepExecution) {
		failureSize = (Integer) stepExecution.getJobExecution().getExecutionContext()
				.get(RederListJobConfig.JOB_A_BATCH_FAILURE_SIZE);

		// 系統設定
		sysSeting = systemFileSettinService.getSysFileSetting();

		// 虛擬user
		List<String> userCodes = Arrays.asList(sysSeting.getRobotUserNumber().split(","));
		List<TSysUser> users = tSysUserRepository.findByUserCodeIn(userCodes);
		for (TSysUser user : users) {
			robotUsers.add(user.getUserId());
		}
		// 機器人DB 連線位置
		dataSource = new DriverManagerDataSource();
		dataSource.setUrl(sysSeting.getAibotDBServerAddress());
		dataSource.setUsername(sysSeting.getAibotDBServerAccount());
		dataSource.setPassword(sysSeting.getAibotDBServerPwd());
		dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	}
	/**
	 * 完成後統計筆數。
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
	 * 處理成功 +1 失敗 
	 */
	@Override
	public TBusCustomer process(TBusCustomer item) throws Exception {
		boolean exclude = checkRecentCalled(item);
		
		if (exclude) {
			failureSize = new Integer(failureSize.intValue()+1);
			return null;
		}
		successSize = new Integer(successSize.intValue()+1);
		// 傳回給後續 writer (RobotWriter) 寫入資料庫
		return item;
	}

	protected boolean checkRecentCalled(TBusCustomer item) throws SQLException {
		// 1 -------------機器人與CSR 有結果EV儲存過的撥打紀錄 -----------------------
		boolean isUserCalled = checkEvCalling(item);
		logger.info("機器人與CSR 有結果EV儲存過的撥打紀錄  isUserCalled  ="+isUserCalled+ " id = "+item.getCustomerId());
		// 2 ---- 確認未來 是否有派給 其他USER 且未撥打的
		boolean isUserCalling = checkAfterListNonUserCalling(item);
		logger.info("確認未來 是否有派給 其他USER 且未撥打的  isUserCalling  ="+isUserCalling+ " id = "+item.getCustomerId());
		// 3 -- 重複名單 
		if( checkCustomerRecent.contains(item.getCustomer_148())) {
			logger.info("重複的電話號碼  CustomerRecent  ="+checkCustomerRecent.contains(item.getCustomer_148())+ " Phone Nubmer = "+item.getCustomer_148());
			return true;
		} else {
			checkCustomerRecent.add(item.getCustomer_148());
		}
		if (isUserCalled || isUserCalling) {
			return true;
		}
		return false;
	}

	/**
	 * 確認 Evoice 是否有撥打過
	 * 
	 * @param item
	 * @return
	 */
	private boolean checkEvCalling(TBusCustomer item) {
		// 抓取 時間 設定 時間
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
	 * 確認之後 的未撥打的名單 是否也在未來要撥打 且不是派給機器人的
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
