package com.fubon.robot.batch.processor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fubon.robot.batch.Data.repository.TBusBlackListRepository;
import com.fubon.robot.batch.Data.repository.TBusCustomerRepository;
import com.fubon.robot.batch.Data.repository.TSysUserRepository;
import com.fubon.robot.batch.Data.repository.TTmActivityRepository;
import com.fubon.robot.batch.Data.repository.TTmHistoryResultRepository;
import com.fubon.robot.batch.Data.repository.TsysVariableRepository;
import com.fubon.robot.batch.batch.SystemFileSettinService;
import com.fubon.robot.batch.config.RederListJobConfig;
import com.fubon.robot.batch.log.LogSetting;
import com.fubon.robot.batch.robot.evdata.Blacklist;
import com.fubon.robot.batch.robot.evdata.TBusCustomer;
import com.fubon.robot.batch.robot.evdata.TSysUser;
import com.fubon.robot.batch.robot.evdata.TSysVariable;
import com.fubon.robot.batch.robot.evdata.TTmActivity;
import com.fubon.robot.batch.thread.post.ProcessMessageUtil;

/**
 * 處理禁呼 .....
 * 
 * @author dell5490
 *
 */
@Component
@StepScope
@Transactional
public class BlackListCheckProcessor implements ItemProcessor<TBusCustomer, TBusCustomer> {
	private static final String blackListStatusReview = "4";
	private static final String blackListStatusReviewReturn = "6";
	private static final String blackListStatusCconfirmReturn = "7";

	private static Logger logger = LogSetting.geterLoger(Logger.getLogger("BlackListCheckProcessor"));
	//
	@Autowired
	TBusBlackListRepository tBusBlackListRepository;

	@Autowired
	TBusCustomerRepository tBusCustomerRepository;

	@Autowired
	TTmActivityRepository tTmActivityRepository;

	@Autowired
	TsysVariableRepository tsysVariableRepository;

	@Autowired
	TTmHistoryResultRepository tTmHistoryResultRepository;

	@Autowired
	TSysUserRepository tSysUserRepository;

	@Autowired
	SystemFileSettinService systemFileSettinService;

	private static final String BLACK_VARIABLE_ID = "6850acd3-a541-48e5-9d9b-96c93cd34e64";

	private TSysVariable var = new TSysVariable();

	private List<String> robotUsers = new ArrayList<String>();

	private Integer successSize = 0;

	private Integer failureSize = 0;
	
	private StepExecution stepExecution;

	/**
	 * 準備資料
	 * 
	 * @param stepExecution
	 */
	@BeforeStep
	public void prepareData(StepExecution stepExecution) {
		
		this.stepExecution = stepExecution;

		String repeatCycle = systemFileSettinService.getSysFileSetting().getRobotUserNumber();
		List<String> userCodes = Arrays.asList(repeatCycle.split(","));
		var = tsysVariableRepository.findOne(BLACK_VARIABLE_ID);
		List<TSysUser> users = tSysUserRepository.findByUserCodeIn(userCodes);
		for (TSysUser user : users) {
			robotUsers.add(user.getUserId());
		}
	}

	@Override
	public TBusCustomer process(TBusCustomer item) throws Exception {

		TTmActivity activity = tTmActivityRepository.findOne(item.getActivityId());

		boolean exclude = isInBlacklist(item) || checkForbiddenCall(item, activity);
		if (exclude) {
			// 失敗紀錄筆數
			//TODO 
//			((RobotCalledListCounter)stepExecution.getJobExecution().getExecutionContext().get("RobotCalledListCounter")).countFailure();;
			failureSize = new Integer(failureSize.intValue() + 1);
			
			return null;
		}
		// 成功紀錄筆數
		successSize = new Integer(successSize.intValue() + 1);
		return item;
	}
	/**
	 * 完成後統計筆數
	 * @param stepExecution
	 */
	@AfterStep
	public  void setingSuccessAndFailureSize(StepExecution stepExecution) {
		
		Object	synchronizedCount =	stepExecution.getJobExecution().getExecutionContext().get(RederListJobConfig.JOB_A_BATCH_SUCCESS_SIZE);
		if( synchronizedCount == null) {
			stepExecution.getJobExecution().getExecutionContext().put(RederListJobConfig.JOB_A_BATCH_SUCCESS_SIZE,
					successSize);
		}else {
			stepExecution.getJobExecution().getExecutionContext().put(RederListJobConfig.JOB_A_BATCH_SUCCESS_SIZE,
					successSize);
		}

		stepExecution.getJobExecution().getExecutionContext().put(RederListJobConfig.JOB_A_BATCH_FAILURE_SIZE,
				failureSize);
	}

	protected boolean checkForbiddenCall(TBusCustomer item, TTmActivity activity) throws Exception {
		// 電文結果為Elong禁呼(不進入自動結案功能) add by Tom 2016-01-08
		boolean specialBlack = false;
		// 禁呼名單判斷
		boolean checkIsBlack = false;
		// 電文查詢連線判斷
		boolean checkIsConnect = true;
		// 電文查詢錯誤判斷
		boolean checkIsErrorMsg = false;
	
		String isBlack = processMessage(activity, "tm", item.getCustomer_28(), "Y", "", var);
		if (StringUtils.isNotBlank(isBlack)) {

			if (checkBlackWord(isBlack)) { // 代表該名單為禁呼名單
				checkIsBlack = true;
				if (checkIsNotBalck(isBlack)) {
					checkIsBlack = false;
				}
			} else if ("BadMessage".equalsIgnoreCase(isBlack)) {
				logger.log(Level.ALL, "發生電文錯誤");
				checkIsErrorMsg = true;
			} else if ("ConnectError".equalsIgnoreCase(isBlack)) {
				logger.log(Level.ALL, "電文連線失敗");
				checkIsConnect = true;
			} else {
				logger.log(Level.ALL, "發現電錯誤的內文");
				checkIsErrorMsg = true;
			}

			if (checkIsErrorMsg || checkIsConnect) {
				return true;
			}
			return checkIsBlack;
		}
		return false;
	}

	private String getSEQNo() {
		BigDecimal seq = tBusCustomerRepository.getListForbiddenCallSequence();
		String sReturnSTANO = "";
		if (seq != null) {
			sReturnSTANO = seq.toString();
		} else {
			Random r1 = new Random();
			int i = r1.nextInt(10000000);
			String str1 = String.format("%07d", i);
			sReturnSTANO = str1;
		}
		return sReturnSTANO;
	}

	public String processMessage(TTmActivity activity, String type, String customerId, String useMW, String key,
			TSysVariable var) throws Exception {
		// 禁呼號碼 取號
		String seq = getSEQNo();
		
//		Thread.sleep(1000);
		
		String isBlack = "";
		ProcessMessageUtil processMessageUtil = new ProcessMessageUtil();
		String blackMsgType = activity.getBlackMsgType();

		if ((isBlack.equalsIgnoreCase("") || isBlack.equalsIgnoreCase("Y")) && blackMsgType.contains("390")) {
			isBlack = processMessageUtil.processMessageLM052671(customerId, ProcessMessageUtil.MESSAGETYPE_BLACK, useMW,
					key, activity.getDataSource(), seq, var);
		}

		if ((isBlack.equalsIgnoreCase("") || isBlack.equalsIgnoreCase("Y")) && blackMsgType.contains("400")) {
			isBlack = processMessageUtil.processMessageCE6220R(customerId, ProcessMessageUtil.MESSAGETYPE_BLACK, useMW,
					key, activity.getDataSource(), seq, var);
		}

//		if((isBlack.equalsIgnoreCase("") || isBlack.equalsIgnoreCase("Y")) && type.equalsIgnoreCase("tm")){	//電銷活動發查新徵審
//			if (MU.processMessageNew(customerId, useMW, MessageUtil.MESSAGETYPE_BLACK).equals("新徵審有進件紀錄不可外撥")){
//				isBlack = "新徵審有進件紀錄不可外撥";
//			}
//		}

		return isBlack;
	}

	public boolean checkIsNotBalck(String checkWord) { // 例外不結案，但為黑名單的案例
		boolean checkValue = false;
		String[] BlackWord = { "Xnnn: Middle ware異常", "E000: 全無回應", "E001: AS/400無回應", "E002: OS/390無回應",
				"ELOAN有進件紀錄不可外撥", "新徵審有進件紀錄不可外撥" };
		for (int i = 0; i < BlackWord.length; i++) {
			if (checkWord.equals(BlackWord[i])) {
				checkValue = true;
			}
		}
		return checkValue;
	}

	public boolean checkBlackWord(String checkWord) { // 統一檢查所有AS400黑名單回傳訊息 by
		// Tom 2015-12-21
		boolean checkValue = false;
		String[] BlackWord = { "Xnnn: Middle ware異常", "E000: 全無回應", "E001: AS/400無回應", "E002: OS/390無回應",
				"AS/400 NO TM 註記不可外撥", "OS/390  NO TM 註記不可外撥", "ELOAN有進件紀錄不可外撥", "新徵審有進件紀錄不可外撥", "AS/400 無正常卡不可外撥",
				"OS/390 存款結清不可外撥", "OS/390 信貸結清不可外撥", "OS/390 房貸結清不可外撥", "OS/390 留貸結清不可外撥", "OS/390 就貸結清不可外撥" };
		for (int i = 0; i < BlackWord.length; i++) {
			if (checkWord.equals(BlackWord[i])) {
				checkValue = true;
			}
		}
		return checkValue;
	}

	protected boolean isInBlacklist(TBusCustomer item) {
		Blacklist blacklistCheck = tBusBlackListRepository.findByIdentityIdAndDeleteMarkNotAndDeleteMarkNotAndStatusIn(
				item.getCustomer_28(), "Y", "y",
				Arrays.asList(blackListStatusReview, blackListStatusReviewReturn, blackListStatusCconfirmReturn));
		return blacklistCheck != null;
	}
	
}
