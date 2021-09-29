package com.fubon.robot.batch.Tasklet;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fubon.robot.batch.SampleBatchApplication;
import com.fubon.robot.batch.batch.SysSeting;
import com.fubon.robot.batch.batch.SystemFileSettinService;
import com.fubon.robot.batch.config.WriteListToRobtConfig;
import com.fubon.robot.batch.thread.post.DateUtils;
import com.fubon.robot.batch.thread.post.MailSendBox;
import com.fubon.robot.batch.writer.RobotWriter;

@Component
@StepScope
@Transactional
public class FailureSendMail implements Tasklet {
	@Autowired
	SystemFileSettinService systemFileSettinService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		contribution.setExitStatus(ExitStatus.FAILED);

		String batchName = (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
				.getExecutionContext().get(SampleBatchApplication.JOB_MAIL_NAME);

		String batchStartTime = (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
				.getExecutionContext().get(SampleBatchApplication.JOB_START_TIME);

		// 1 取得 相關MAIL 資訊從設定檔
		SysSeting sysSeting = systemFileSettinService.getSysFileSetting();
		// 2寄送人位置
		String[] sendUserMailAddress = sysSeting.getMailAddrIt().split(",");

		for (String senduser : sendUserMailAddress) {
			MailSendBox box = new MailSendBox(senduser, "robotUser", sysSeting.getMailServerAddress(),
					sysSeting.getMailServerPort(), "smtp", "電銷系統電銷系統整合AI機器人 " + batchName + "執行失敗",
					"管理員您好!! \r\n 電銷系統電銷系統整合AI機器人" + batchName + "執行失敗 \r\n 開始時間:" +batchStartTime +"\r\n 結束時間" + DateUtils.getNowTime()
							+ "\r\n 失敗原因 : "+chunkContext.getStepContext().getStepExecution().getJobExecution().getAllFailureExceptions() 
							+ "\r\n 發生意外錯誤而結束詳細原因可以查看同層 資料夾內 BatchLog ",
					sysSeting.getMailSendUser(), sysSeting.getMailSendPwd());
			box.sendMail();
		}

		return RepeatStatus.FINISHED;
	}

}
