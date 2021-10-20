package com.fubon.robot.batch.processor;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fubon.robot.batch.Data.repository.TBusCustomerRepository;
import com.fubon.robot.batch.Data.repository.TTmActivityCallResultRepository;
import com.fubon.robot.batch.Data.repository.TTmActivityRepository;
import com.fubon.robot.batch.Data.repository.TTmHistoryResultRepository;
import com.fubon.robot.batch.log.LogSetting;
import com.fubon.robot.batch.robot.data.Callinglistbot;
import com.fubon.robot.batch.robot.evdata.TBusCustomer;
import com.fubon.robot.batch.robot.evdata.TTmActivity;
import com.fubon.robot.batch.robot.evdata.TTmActivityCallResult;
import com.fubon.robot.batch.robot.evdata.TTmHistoryResult;
import com.fubon.robot.batch.thread.post.DateUtils;

/**
 * 處理接聽動作
 * 
 * @author dell5490
 *
 */
@Component
@StepScope
@Transactional
public class AnswerProcessor implements ItemProcessor<Callinglistbot, Callinglistbot> {
	private static Logger logger = LogSetting.geterLoger(Logger.getLogger("AnswerProcessor"));

	@Autowired
	TBusCustomerRepository tBusCustomerRepository;

	@Autowired
	TTmActivityCallResultRepository tTmActivityCallResultRepository;

	@Autowired
	TTmActivityRepository tTmActivityRepository;

	@Autowired
	TTmHistoryResultRepository tTmHistoryResultRepository;

	@Override
	public Callinglistbot process(Callinglistbot item) throws Exception {
		// 只有 是 33 跟不是 33的 與沒有處理的。
		if (Long.valueOf(33L).equals(item.getCallResult())) {
			answerCall(item);
		// 28 不處理
		} else if(Long.valueOf(28L).equals(item.getCallResult())) {
			logger.info("有沒有完成的撥打 UUID =" + item.getUuid());
		} else {
			telMissed(item);
		}
		return item;
	}

	public void telMissed(Callinglistbot item) {
		TBusCustomer cust = tBusCustomerRepository.findOne(item.getUuid());
		TTmActivity activity = tTmActivityRepository.findOne(cust.getActivityId());
		List<TTmActivityCallResult> list = tTmActivityCallResultRepository
				.getActivityRootResultList(activity.getResultId());
		TTmActivityCallResult selectResult = returnActionResult(list, "B01");
		if (selectResult != null) {
			// 更新 名單並且新增 結果
			if ("Y".equalsIgnoreCase(selectResult.getIsClosed())) {
				// 更新為已結案
				addCallResult(cust, selectResult, "215");
			} else {
				// 一訪聯系中 --- 交付對應
				addCallResult(cust, selectResult, "211");
			}
		} else {
			logger.info("發生對應不到的 檔案  名單 ID= " + cust.getCustomerId() + "  客戶姓名 = " + cust.getCustomerName());
		}
	}

	/**
	 * 處理 33 號已回應的資料
	 * 
	 * @param item
	 */
	public void answerCall(Callinglistbot item) {
		TBusCustomer cust = tBusCustomerRepository.findOne(item.getUuid());
		TTmActivity activity = tTmActivityRepository.findOne(cust.getActivityId());
		List<TTmActivityCallResult> list = tTmActivityCallResultRepository
				.getActivityRootResultList(activity.getResultId());
		TTmActivityCallResult selectResult = null;
		boolean isBlack = false;
		if ("A1".equals(item.getActionResult())) {
			selectResult = returnActionResult(list, "D01");
		} else if ("A2".equals(item.getActionResult())) {
			selectResult = returnActionResult(list, "D01");
		} else if ("A3".equals(item.getActionResult())) {
			isBlack = true;
			selectResult = returnActionResult(list, "G");
		} else if ("A4".equals(item.getActionResult())) {
			selectResult = returnActionResult(list, "B02");
		} else if ("A5".equals(item.getActionResult())) {
			selectResult = returnActionResult(list, "E01");
		} else if ("A6".equals(item.getActionResult())) {
			selectResult = returnActionResult(list, "E09");
		} else if ("A7".equals(item.getActionResult())) {
			selectResult = returnActionResult(list, "D01");
		} else if ("A8".equals(item.getActionResult())) {
			selectResult = returnActionResult(list, "D01");
		} else if ("A9".equals(item.getActionResult())) {
			selectResult = returnActionResult(list, "D01");
		}

		if (selectResult != null) {
			if (isBlack) {
				addCallResult(cust, selectResult, "217");
			} else {
				// 更新 名單並且新增 結果
				if ("Y".equalsIgnoreCase(selectResult.getIsClosed())) {
					// 更新為已結案
					addCallResult(cust, selectResult, "215");
				} else {
					// 一訪聯系中 --- 交付對應
					addCallResult(cust, selectResult, "211");
				}
			}
		} else {
			logger.info("發生對應不到的 檔案  名單 ID= " + cust.getCustomerId() + "  客戶姓名 = " + cust.getCustomerName());
		}

		// TBusCustomer cust = tBusCustomerRepository.findOne(item.getCalluuid());
	}
	
	private void addCallResult(TBusCustomer cust, TTmActivityCallResult selectResult, String status) {
		cust.setCustomerStatus(status); // 已結案
		if ("217".equals(status)) {
			cust.setLastConnPath("Z系統自動結案");
		} else {
			cust.setLastConnPath(selectResult.getResultName()); // 加入通話結果
		}
		cust.setCallingTime(DateUtils.getNowTime()); // 取得現在時間
		logger.info("tBusCustomerRepository 名單 ID= " + cust.getCustomerId() + "  客戶姓名 = " + cust.getCustomerName());
		tBusCustomerRepository.save(cust);
		Calendar c = Calendar.getInstance();
		String CALL_RESULT_ID = UUID.randomUUID().toString();
		TTmHistoryResult entity = new TTmHistoryResult();
		entity.setResultId(selectResult.getResultId());
		entity.setCallResultId(CALL_RESULT_ID);
		entity.setCustName(cust.getCustomerName());
		entity.setCustId(cust.getCustomerId());
		entity.setCsrCode(cust.getReceiveUserCode());
		if ("217".equals(status)) {
			entity.setResultFullpath("Z系統自動結案");
		} else {
			entity.setResultFullpath(selectResult.getResultName());
		}
		entity.setCallingTime(getYMDHMS(c));
		entity.setCsrName(cust.getReceiveUserName());
		entity.setCsrId(cust.getReceiveUserId());
		entity.setActivityId(cust.getActivityId());
		entity.setCustomerStatus(status);
		logger.info(" tTmHistoryResultRepository 名單 ID= " + cust.getCustomerId() + "  客戶姓名 = " + cust.getCustomerName());
		tTmHistoryResultRepository.save(entity);
		
		
		tBusCustomerRepository.flush();
		tTmHistoryResultRepository.flush();
	}

	private TTmActivityCallResult returnActionResult(List<TTmActivityCallResult> list, String resulteCode) {
		TTmActivityCallResult selectResult = null;
		for (TTmActivityCallResult result : list) {
			if (StringUtils.containsIgnoreCase(result.getResultName(), resulteCode)) {
				return result;
			}
		}
		return selectResult;
	}


	// 2016.11.08 min insert start(取得系統當前時間)
	public static String getYMDHMS(Calendar c) { // 輸出格式製作
		int[] a = { c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),
				c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND),
				c.get(Calendar.MILLISECOND) };
		StringBuffer sb = new StringBuffer();
		sb.append(a[0]);
		if (a[1] < 9) {
			sb.append("-0" + (a[1] + 1));
		} // 加 1 才會得到實際月份
		else {
			sb.append("-" + (a[1] + 1));
		}
		if (a[2] < 10) {
			sb.append("-0" + (a[2]));
		} else {
			sb.append("-" + (a[2]));
		}
		if (a[3] < 10) {
			sb.append(" 0" + (a[3]));
		} else {
			sb.append(" " + (a[3]));
		}
		if (a[4] < 10) {
			sb.append(":0" + a[4]);
		} else {
			sb.append(":" + a[4]);
		}
		if (a[5] < 10) {
			sb.append(":0" + a[5]);
		} else {
			sb.append(":" + a[5]);
		}
		sb.append("." + a[6]);
		return sb.toString();
	}
}
