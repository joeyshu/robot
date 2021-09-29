/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fubon.robot.batch;

import java.util.logging.Logger;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.fubon.robot.batch.DataConfing.DataBaseConfig;
import com.fubon.robot.batch.DataConfing.EvoiceDataBaseConfig;
import com.fubon.robot.batch.Service.CustomJobFactory;
import com.fubon.robot.batch.config.BatchProperties;
import com.fubon.robot.batch.config.SpringDatasourceProperties;
import com.fubon.robot.batch.log.LogSetting;

@EnableAutoConfiguration(exclude = { EvoiceDataBaseConfig.class,DataBaseConfig.class })
@EnableConfigurationProperties({ EvoiceDataBaseConfig.class,DataBaseConfig.class, 
								 BatchProperties.class,SpringDatasourceProperties.class })
@ComponentScan
@SpringApplicationConfiguration
public class SampleBatchApplication implements CommandLineRunner {
	
	public static final String JOB_MAIL_NAME = "MAILTITLENAME";
	
	public static final String JOB_START_TIME = "JOBSTARTTIME";
	
	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	@Qualifier("readDataToRobotJobFactory")
	CustomJobFactory readDataToRobotJobFactory;
	
	@Autowired
	@Qualifier("writeerDataToEvoiceJobFactory")
	CustomJobFactory writeerDataToEvoiceJobFactory;
	
	private static Logger logger = LogSetting.geterLoger(Logger.getLogger("SampleBatchApplication"));
	

	public static void main(String[] args) throws Exception {
		SpringApplication app = new SpringApplication(SampleBatchApplication.class);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO: check args
		JobParametersBuilder paramBuilder = new JobParametersBuilder();
		paramBuilder.addLong("start date", System.currentTimeMillis());

		Job batchJob = getBatchJob(args[0]);

		jobLauncher.run(batchJob, paramBuilder.toJobParameters());
	}

	private Job getBatchJob(String jobType) {
		// TODO if not found jobType

		Job batchJob = null;
		if ("readerJob".equals(jobType)) {
			
			batchJob = readDataToRobotJobFactory.getJob();

		} else if ("writerJob".equals(jobType)) {
			
			batchJob = writeerDataToEvoiceJobFactory.getJob();
		} else {
			logger.info("輸入參數錯誤 結束 批次---"+jobType);
		}
		
		return batchJob;
	}
}
