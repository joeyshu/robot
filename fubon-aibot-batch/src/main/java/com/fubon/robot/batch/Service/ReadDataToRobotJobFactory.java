package com.fubon.robot.batch.Service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class ReadDataToRobotJobFactory implements CustomJobFactory {

	@Autowired
	JobBuilderFactory jobBuilderFactory;

	@Autowired
	@Qualifier("readerFlow")
	Flow readerFlow;

	@Autowired
	@Qualifier("filterFlow")
	Flow filterFlow;

	
	
	
	
	@Override
	public Job getJob() {
		return jobBuilderFactory.get("readerJob")
				.start(readerFlow) // 讀取 資料
				.next(filterFlow) // 過濾 
				//.next() //並寫入
				.end()
				.build();
	}

}
