package com.fubon.robot.batch.itemreader;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fubon.robot.batch.Data.repository.TsysVariableRepository;
import com.fubon.robot.batch.config.RederListJobConfig;
import com.fubon.robot.batch.robot.evdata.ReadListDTO;
import com.fubon.robot.batch.robot.evdata.TBusCustomer;

@Component
@StepScope
@Transactional
public class RrecentCalledReader extends StepExecutionListenerSupport implements ItemReader<TBusCustomer> {

	private List<TBusCustomer> evoiceInputDatas = new ArrayList<TBusCustomer>();

	@Autowired
	TsysVariableRepository tsysVariableRepository;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		evoiceInputDatas.addAll(
				((ReadListDTO) stepExecution.getJobExecution()
				.getExecutionContext()
				.get(RederListJobConfig.JOB_EXECUTION_EVOICE_DATA_RESULT)).getData());

	}
	/**
	 * synchronized 用意為 為了非同步 
	 * https://www.itread01.com/content/1547911811.html
	 * 這種方式並不支援重啟操作。當執行失敗的時候並沒有儲存當前的狀態資料導致無法知道哪些資料已經讀取，哪些資料未讀取。
	*/
	@Override
	public synchronized TBusCustomer read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		List<TBusCustomer> all = evoiceInputDatas;
		if (all.isEmpty()) {
			return null;
		}
		return all.remove(0);
	}
}
