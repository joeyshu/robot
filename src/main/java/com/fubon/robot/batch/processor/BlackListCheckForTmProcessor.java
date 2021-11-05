package com.fubon.robot.batch.processor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
public class BlackListCheckForTmProcessor implements ItemProcessor<TBusCustomer, TBusCustomer> {

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

		TTmActivity activity = getActivity(item.getActivityId());

		if ("Y".equalsIgnoreCase(activity.getBlackNameOpen())) {
			if (!checktm(activity)) {
				return item;
			}
			boolean exclude = checkForbiddenCall(item, activity);
			if (exclude) {
				// 失敗紀錄筆數
				failureSize = new Integer(failureSize.intValue() + 1);
				return null;
			}
		}
		// 成功紀錄筆數
		successSize = new Integer(successSize.intValue() + 1);

		return item;
	}

	public boolean checktm(TTmActivity activity) {
		Object sysVariAble = stepExecution.getJobExecution().getExecutionContext()
				.get(RederListJobConfig.JOB_A_SYSVARIABLE);
		String variableType = "";
		if (sysVariAble == null) {
			HashMap<String, TSysVariable> idlist = new HashMap<String, TSysVariable>();
			TSysVariable variable = tsysVariableRepository.findOne(activity.getActivityCatalog());
			variableType = variable.getVariableType();
			idlist.put(variable.getVariableId(), variable);
			stepExecution.getJobExecution().getExecutionContext().put(RederListJobConfig.JOB_A_SYSVARIABLE, idlist);
		} else {
			HashMap<String, TSysVariable> idlist = (HashMap<String, TSysVariable>) sysVariAble;
			if (idlist.containsKey(activity.getActivityCatalog())) {
				variableType = idlist.get(activity.getActivityCatalog()).getVariableType();
			} else {
				TSysVariable variable = tsysVariableRepository.findOne(activity.getActivityCatalog());
				idlist.put(variable.getVariableId(), variable);
				variableType = variable.getVariableType();
				stepExecution.getJobExecution().getExecutionContext().put(RederListJobConfig.JOB_A_SYSVARIABLE, idlist);
			}
		}
		if ("85dc4478-75ae-4ff5-9363-0bb204a3f630".equalsIgnoreCase(variableType)) {
			return true;
		}

		return false;
	}

	public TTmActivity getActivity(String activityId) {
		Object actitityList = stepExecution.getJobExecution().getExecutionContext()
				.get(RederListJobConfig.JOB_A_ACTIVITY_LIST);
		if (actitityList == null) {
			TTmActivity activity = tTmActivityRepository.findOne(activityId);
			HashMap<String, TTmActivity> idlist = new HashMap<String, TTmActivity>();
			idlist.put(activityId, activity);
			stepExecution.getJobExecution().getExecutionContext().put(RederListJobConfig.JOB_A_ACTIVITY_LIST, idlist);
			return activity;
		} else {
			HashMap<String, TTmActivity> idlist = (HashMap<String, TTmActivity>) actitityList;
			if (idlist.containsKey(activityId)) {
				return idlist.get(activityId);
			} else {
				TTmActivity act = tTmActivityRepository.findOne(activityId);
				idlist.put(activityId, act);
				stepExecution.getJobExecution().getExecutionContext().put(RederListJobConfig.JOB_A_ACTIVITY_LIST,
						idlist);
				return act;
			}
		}
	}

	/**
	 * 完成後統計筆數
	 * 
	 * @param stepExecution
	 */
	@AfterStep
	public void setingSuccessAndFailureSize(StepExecution stepExecution) {

		Object synchronizedCount = stepExecution.getJobExecution().getExecutionContext()
				.get(RederListJobConfig.JOB_A_BATCH_SUCCESS_SIZE);
		if (synchronizedCount == null) {
			stepExecution.getJobExecution().getExecutionContext().put(RederListJobConfig.JOB_A_BATCH_SUCCESS_SIZE,
					successSize);
		} else {
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
		// 是否有有效卡
		boolean checkHaveRightCard = false;

		String isBlack = processMessage(activity, "tm", item.getCustomer_28(), "Y", "", var);
		if (StringUtils.isNotBlank(isBlack)) {
			if (checkBlackWord(isBlack)) { // 代表該名單為禁呼名單
				checkIsBlack = true;
				if (checkIsNotBalck(isBlack)) {
					specialBlack = true;
				}
			} else if ("BadMessage".equalsIgnoreCase(isBlack)) {
				logger.log(Level.ALL, "發生電文錯誤");
				checkIsErrorMsg = true;
			} else if ("ConnectError".equalsIgnoreCase(isBlack)) {
				logger.log(Level.ALL, "電文連線失敗");
				checkIsConnect = false;
			} else {
				logger.log(Level.ALL, "發現電錯誤的內文");
				checkIsErrorMsg = true;
			}
			if (checkIsConnect || checkIsErrorMsg) {
				return true;
			}
		}
		if ((checkIsBlack || checkHaveRightCard) && (!specialBlack)) {
			return true;
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

		String isBlack = "";
		ProcessMessageUtil processMessageUtil = new ProcessMessageUtil();
		if ((isBlack.equalsIgnoreCase("") || isBlack.equalsIgnoreCase("Y")) && type.equalsIgnoreCase("tm")) { // 電銷活動發查新徵審
			if (processMessageUtil.processMessageNew(customerId, useMW, ProcessMessageUtil.MESSAGETYPE_BLACK, seq, var)
					.equals("新徵審有進件紀錄不可外撥")) {
				isBlack = "新徵審有進件紀錄不可外撥";
			}
		}

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

}
