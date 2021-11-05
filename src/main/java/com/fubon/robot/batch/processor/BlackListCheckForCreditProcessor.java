package com.fubon.robot.batch.processor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.aspectj.bridge.MessageUtil;
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
import com.fubon.robot.batch.Data.repository.TTmObGroupRepository;
import com.fubon.robot.batch.Data.repository.TsysVariableRepository;
import com.fubon.robot.batch.batch.SystemFileSettinService;
import com.fubon.robot.batch.config.RederListJobConfig;
import com.fubon.robot.batch.log.LogSetting;
import com.fubon.robot.batch.robot.evdata.TBusCustomer;
import com.fubon.robot.batch.robot.evdata.TSysUser;
import com.fubon.robot.batch.robot.evdata.TSysVariable;
import com.fubon.robot.batch.robot.evdata.TTmActivity;
import com.fubon.robot.batch.robot.evdata.TTmObGroup;
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
public class BlackListCheckForCreditProcessor implements ItemProcessor<TBusCustomer, TBusCustomer> {

	private static Logger logger = LogSetting.geterLoger(Logger.getLogger("BlackListCheckForCreditProcessor"));
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
	TTmObGroupRepository tTmObGroupRepository;

	@Autowired
	SystemFileSettinService systemFileSettinService;

	private static final String BLACK_VARIABLE_ID = "6850acd3-a541-48e5-9d9b-96c93cd34e64";

	private TSysVariable var = new TSysVariable();

	private List<String> robotUsers = new ArrayList<String>();

	private Integer successSize = 0;

	private Integer failureSize = 0;

	private StepExecution stepExecution;

	private static final String creditMiffyCard = "miffyCard";

	private static final String creditClearCard = "clearCard";

	private static final String creditPayCard = "payCard";

	private static final String creditNewCustomerCard = "newCustomerCard";

	// 電文結果為Elong禁呼(不進入自動結案功能) add by Tom 2016-01-08
	boolean specialBlack = false;
	// 禁呼名單判斷
	boolean checkIsBlack = false;
	// 電文查詢連線判斷
	boolean checkIsConnect = true;
	// 電文查詢錯誤判斷
	boolean checkIsErrorMsg = false;

	boolean checkHaveRightCard = false;

	List<TTmObGroup> group = new ArrayList<TTmObGroup>();

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
			String creditType = getCreditCardType(activity, item);
			logger.info("creditType == " + creditType);

			if (!checkCredit(activity)) {
				return item;
			}

			if (StringUtils.isBlank(creditType)) {
				return item;
			}
			if (creditClearCard.equals(creditType)) {
				return item;
			}
			if (creditPayCard.equals(creditType)) {
				return item;
			}
			String variableKey = "";
			if (group.size() > 0) {
				variableKey = group.get(0).getActivityVariableKey();
			}

			boolean exclude = checkForbiddenCall(item, activity, creditType, variableKey);

			if (exclude) {
				// 失敗紀錄筆數
				// TODO
//			((RobotCalledListCounter)stepExecution.getJobExecution().getExecutionContext().get("RobotCalledListCounter")).countFailure();;
				failureSize = new Integer(failureSize.intValue() + 1);

				return null;
			}

		}
		// 成功紀錄筆數
		successSize = new Integer(successSize.intValue() + 1);
		return item;
	}

	public boolean checkCredit(TTmActivity activity) {
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
		if ("4aff636d-9133-46f5-9c1a-4138677181f2".equalsIgnoreCase(variableType)) {
			return true;
		}

		return false;
	}

	public String getCreditCardType(TTmActivity activity, TBusCustomer item) {
		Object sysVariAble = stepExecution.getJobExecution().getExecutionContext()
				.get(RederListJobConfig.JOB_A_SYSVARIABLE);
		HashMap<String, TSysVariable> idlist = new HashMap<String, TSysVariable>();
		TSysVariable variable = null;
		if (sysVariAble == null) {
			variable = tsysVariableRepository.findOne(activity.getActivityCatalog());
			idlist.put(variable.getVariableId(), variable);
			stepExecution.getJobExecution().getExecutionContext().put(RederListJobConfig.JOB_A_SYSVARIABLE, idlist);
		} else {
			idlist = (HashMap<String, TSysVariable>) sysVariAble;
			if (idlist.containsKey(activity.getActivityCatalog())) {
				variable = idlist.get(activity.getActivityCatalog());
			} else {
				variable = tsysVariableRepository.findOne(activity.getActivityCatalog());
				idlist.put(variable.getVariableId(), variable);
				stepExecution.getJobExecution().getExecutionContext().put(RederListJobConfig.JOB_A_SYSVARIABLE, idlist);
			}
		}

		Set<String> variableIds = idlist.keySet();

		if ("4aff636d-9133-46f5-9c1a-4138677181f2".equalsIgnoreCase(variable.getVariableType())) {

			if (variableIds.contains("35c1bfae-3f9f-45b6-b3fa-09d863dc1c63")
					|| variableIds.contains("f76e9562-d916-4f6d-beac-935be598bced")) {
				group = tTmObGroupRepository.findObgroup(item.getCustomer_137(), variable.getVariableKey());
				return creditMiffyCard;
			}
			if (variableIds.contains("460afa30-2f28-41b7-9c9c-1e7a946a62d6")) {
				return creditClearCard;
			}
			if (variableIds.contains("85dafc12-edf6-4bb7-9bc5-06b434492eb8")
					|| variableIds.contains("061b534a-30ec-499f-861b-ac05fd9bc0d4")) {
				return creditPayCard;
			}

			if (variableIds.contains("3ead19ae-0b3f-4ef8-af7f-16f5b82b9d9f")) {
				return creditNewCustomerCard;
			}
			return "";
		} else {
			return "";
		}

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

	protected boolean checkForbiddenCall(TBusCustomer item, TTmActivity activity, String type, String groupkey)
			throws Exception {
		processMessage(activity, type, item.getCustomer_28(), "Y", "", var, item, groupkey);

		logger.log(Level.INFO, "checkIsConnect = " + checkIsConnect + "checkIsErrorMsg =" + checkIsErrorMsg);
//		if (checkIsConnect || checkIsErrorMsg) {
//			return true;
//		}
		logger.log(Level.INFO, "checkIsBlack = " + checkIsBlack + "checkHaveRightCard =" + checkIsErrorMsg
				+ "specialBlack=" + specialBlack);
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
			TSysVariable var, TBusCustomer customer, String groupKey) throws Exception {

		// 禁呼號碼 取號
		String seq = getSEQNo();

		String isBlack = "";
		ProcessMessageUtil processMessageUtil = new ProcessMessageUtil();

		if (creditMiffyCard.equals(type)) {
			isBlack = processMessageUtil.processMessageForCreditCE6121R(customer, ProcessMessageUtil.MESSAGETYPE_BLACK,
					"", "", groupKey, var, seq);
			if (StringUtils.isNotBlank(isBlack)) {
				if ("未符合挽卡資格".equalsIgnoreCase(isBlack)) {
					checkIsBlack = true;
					logger.info("未符合挽卡資格");
				} else if ("符合挽卡資格".equalsIgnoreCase(isBlack)) {
					checkIsBlack = false;
					logger.info("符合挽卡資格");
				} else if ("BadMessage".equalsIgnoreCase(isBlack)) {
					logger.info("電文查詢反回錯誤訊息!!");
					checkIsErrorMsg = true;
				} else if ("ConnectError".equalsIgnoreCase(isBlack)) {
					logger.info("電文連接失敗!!");
					checkIsConnect = false;
				} else {
					checkIsBlack = true;
					logger.info("isBlack=" + isBlack);
				}
			}
		}

		if (creditNewCustomerCard.equals(type)) {
			TBusCustomer customerforcredit = processMessageUtil.processMessageForCredit(customer,
					ProcessMessageUtil.MESSAGETYPE_BLACK, "", "", activity.getDataSource(), var, seq, isBlack);
			if (customerforcredit.getProposerId().equals("-1")) {
				logger.info("此名單為已開卡，不需外撥");
				checkHaveRightCard = true;
			} else if (customerforcredit.getProposerId().equals("-2")) {
				logger.info("此名單皆為無效卡，不需外撥");
				isBlack = "無正常卡";
				checkHaveRightCard = true;
			} else if (customerforcredit.getProposerId().equals("-3")) {
				logger.log(Level.WARNING, "電文連接失敗!!");
				return "ConnectError";
			} else {
				tBusCustomerRepository.save(customerforcredit);
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
