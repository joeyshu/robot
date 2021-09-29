package com.fubon.robot.batch.itemreader;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fubon.robot.batch.config.WriteListToRobtConfig;
import com.fubon.robot.batch.log.LogSetting;
import com.fubon.robot.batch.robot.data.Callinglistbot;
import com.fubon.robot.batch.robot.data.RobotDataDTO;
@Component
@StepScope
@Transactional
public class CheckWriteJobDataReader extends StepExecutionListenerSupport implements ItemReader<Callinglistbot> {

	private static Logger logger = LogSetting.geterLoger(Logger.getLogger("CheckWriteJobDataReader"));

	private RobotDataDTO robotInputData;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		robotInputData = (RobotDataDTO) stepExecution.getJobExecution().getExecutionContext()
				.get(WriteListToRobtConfig.WRITE_JOB_DATA);

	}

	@Override
	public synchronized Callinglistbot read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		List<Callinglistbot> all = robotInputData.getData();

		if (all.isEmpty()) {
			logger.log(Level.ALL, "沒有找到可匯入資料 資料總數 為 0 結束");
			return null;
		}
		return all.remove(0);
	}
}
