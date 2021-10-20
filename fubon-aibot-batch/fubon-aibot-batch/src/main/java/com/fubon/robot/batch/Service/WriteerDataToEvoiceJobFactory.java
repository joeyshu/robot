package com.fubon.robot.batch.Service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class WriteerDataToEvoiceJobFactory implements CustomJobFactory {

	@Autowired
	JobBuilderFactory jobBuilderFactory;

	@Autowired
	@Qualifier("readerRobotFlow")
	Flow readerFlow;


	@Override
	public Job getJob() {
		return jobBuilderFactory.get("WriterJob")
				.start(readerFlow) // 讀取 資料 寫入 資浪 
				.end().build();
	}

}
