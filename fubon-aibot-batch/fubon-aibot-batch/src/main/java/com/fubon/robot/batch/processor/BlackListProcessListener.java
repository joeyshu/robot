package com.fubon.robot.batch.processor;

import java.util.Calendar;
import java.util.UUID;

import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fubon.robot.batch.Data.repository.TBusCustomerRepository;
import com.fubon.robot.batch.Data.repository.TTmActivityRepository;
import com.fubon.robot.batch.Data.repository.TTmHistoryResultRepository;
import com.fubon.robot.batch.robot.evdata.TBusCustomer;
import com.fubon.robot.batch.robot.evdata.TTmHistoryResult;
import com.fubon.robot.batch.thread.post.DateUtils;

/**
 * 抓到禁呼後 更新狀態 處理。
 * 
 * @author dell5490
 *
 */
@Component
@StepScope
@Transactional
public class BlackListProcessListener implements ItemProcessListener<TBusCustomer, TBusCustomer> {

	@Autowired
	TTmActivityRepository tTmActivityRepository;

	@Autowired
	TBusCustomerRepository tBusCustomerRepository;

	@Autowired
	TTmHistoryResultRepository tTmHistoryResultRepository;

	@Override
	public void beforeProcess(TBusCustomer item) {
		// TODO get user id
	}

	@Override
	public void afterProcess(TBusCustomer item, TBusCustomer result) {
		if (result == null) { // 如果是黑名單或信用卡無有效卡 add by Tom 黑名單或信用卡無有效卡則直接結案 (補充 Elong不結案)
			item.setCustomerStatus("217"); // 新增狀態
			item.setLastConnPath("Z系統自動結案"); // 加入通話結果
			item.setCallingTime(DateUtils.getNowTime()); // 取得現在時間

			// TODO 找到 自動結案的 SQL
			String result_id = tTmActivityRepository.findAutoCloseResultId(item.getActivityId());
			if (result_id != null) {
				item.setResultId(result_id); // 通話結果ID
			} else { // 找不到通話結果
				item.setResultId("無系統自動結案通話結果");
			}
			tBusCustomerRepository.saveAndFlush(item);
			Calendar c = Calendar.getInstance();
			String CALL_RESULT_ID = UUID.randomUUID().toString();
			TTmHistoryResult entity = new TTmHistoryResult();
			entity.setResultId(result_id);
			entity.setCallResultId(CALL_RESULT_ID);
			entity.setCustName(item.getCustomerName());
			entity.setCustId(item.getCustomerId());
			entity.setCsrCode(item.getReceiveUserCode());
			entity.setResultFullpath("Z系統自動結案");
			entity.setCallingTime(getYMDHMS(c));
			entity.setCsrName(item.getReceiveUserName());
			entity.setCsrId(item.getReceiveUserId());
			entity.setActivityId(item.getActivityId());
			entity.setCustomerStatus("217");
			
			
			tTmHistoryResultRepository.save(entity);
		}

	}

	@Override
	public void onProcessError(TBusCustomer item, Exception e) {
		// TODO Auto-generated method stub

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
