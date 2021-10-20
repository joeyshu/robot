package com.fubon.robot.batch.writer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fubon.robot.batch.Data.repository.TTmActivityRepository;
import com.fubon.robot.batch.batch.SysSeting;
import com.fubon.robot.batch.batch.SystemFileSettinService;
import com.fubon.robot.batch.robot.data.Callinglistbot;
import com.fubon.robot.batch.robot.evdata.TBusCustomer;
import com.fubon.robot.batch.robotData.repository.CallingRobotRepository;

/**
 * 存取資料庫 一次存放
 * 
 * @author dell5490
 *
 */
@Component
@StepScope
@Transactional
public class RobotWriter extends StepExecutionListenerSupport implements ItemWriter<TBusCustomer> {

	public static final String successCount = "jobSuccessCount";

	@Autowired
	SystemFileSettinService systemFileSettinService;

	private StepExecution stepExecution;

	@Autowired
	TTmActivityRepository tTmActivityRepository;

	@Autowired
	CallingRobotRepository callingRobotRepository;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		super.beforeStep(stepExecution);
		this.stepExecution = stepExecution;
	}

	@Override
	public synchronized void write(List<? extends TBusCustomer> items) throws Exception {
		long chenId = 0;
		if (stepExecution.getJobExecution().getExecutionContext().get(successCount) != null) {
			chenId = ((Integer) stepExecution.getJobExecution().getExecutionContext().get(successCount)).intValue() + 1;
		} else {
			chenId = 1;
		}
		for (int i = 0; i < items.size(); i++) {
			TBusCustomer t = items.get(i);
			long d = (long) (i + chenId);
			Callinglistbot bot = new Callinglistbot();
			bot.setRecordId(d);
			bot.setContactInfo(t.getCustomer_148());
			bot.setRecordType(2L);
			bot.setContactInfoType(4L);
			bot.setRecordStatus(1L);
			bot.setCallResult(28L);
			bot.setAttempt(0L);
			bot.setDailyFrom(28800L);
			bot.setDailyTill(64800L);
			bot.setTzDbid(112L);
			bot.setGend(sexCheck(t));
			bot.setCustname(t.getCustomerName());
			bot.setMp(t.getCustomer_148());
			bot.setProjectDetail(t.getCustomer_138());
			bot.setUuid(t.getCustomerId());
			bot.setChainId(d);
			bot.setChainN(0L);
			callingRobotRepository.save(bot);
		}
		if (stepExecution.getJobExecution().getExecutionContext().get(successCount) != null) {

			int count = ((Integer) stepExecution.getJobExecution().getExecutionContext().get(successCount)).intValue();
			stepExecution.getJobExecution().getExecutionContext().put(successCount, items.size() + count);
		} else {
			stepExecution.getJobExecution().getExecutionContext().put(successCount, items.size());
		}

		// System.out.println("塞入機器人的筆數為" + items.size());
		// 將成功寫入筆數紀錄

	}

	private String sexCheck(TBusCustomer t) {
		String idSex = "";
		try {
			idSex = Character.toString(t.getCustomer_28().charAt(1));
			if ("1".equals(idSex)) {
				idSex = "0";
			} else if ("2".equals(idSex)) {
				idSex = "1";
			} else {
				idSex = "U";
			}
		} catch (Exception e) {
			System.out.println("性別轉換發生錯誤");
		}
		return idSex;
	}

}
