package com.fubon.robot.batch.batch;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fubon.robot.batch.config.BatchProperties;
import com.fubon.robot.batch.thread.post.StringBase64Util;

@Service
public class SystemFileSettinService {

	@Autowired
	BatchProperties batchProperties;

	public SysSeting getSysFileSetting() {
		Path reportPath = Paths.get(batchProperties.getSystemPropPath());
		SysSeting dataSeting = new SysSeting();
		openFileChek(reportPath);
		Properties prop = new Properties();
		byte[] pribytes;
		try {
			pribytes = Files.readAllBytes(reportPath);
			ByteArrayInputStream byteinputFile = new ByteArrayInputStream(pribytes);
			prop.load(byteinputFile);
			
			dataSeting.setMailAddrUser(prop.getProperty("messageDelivery-mail-onFinished"));
			
			dataSeting.setMailAddrIt(prop.getProperty("messageDelivery-mail-onError"));
			
			dataSeting.setRowQuantity(Integer.parseInt(prop.getProperty("batchAlpha-numberOfDataByBatch")));
			
			dataSeting.setRobotUserNumber(prop.getProperty("batchAlpha-aibotStaffCodeList"));
			
			dataSeting.setDedupingOutboundByEventsInDaystypeOfTelemarketingOfCreditLoans(Integer.parseInt(prop.getProperty("batchAlpha-deduping-outboundByEventsInDays-typeOfTelemarketingOfCreditLoans")));
			
			dataSeting.setDedupingOutboundByAibotInDays(Integer.parseInt(prop.getProperty("batchAlpha-deduping-outboundByAibotInDays")));
			
			dataSeting.setMailServerAddress(prop.getProperty("messageDelivery-mail-server-address"));
			
			dataSeting.setMailServerPort(prop.getProperty("messageDelivery-mail-server-port"));
			
			dataSeting.setNumberOfThreads(Integer.parseInt(prop.getProperty("batchAlpha-numberOfThreads")));
			
			dataSeting.setMailSendUser(StringBase64Util.getBase64DecoderSting(prop.getProperty("messageDelivery-mail-send-user")));
			
			dataSeting.setMailSendPwd(StringBase64Util.getBase64DecoderSting(prop.getProperty("messageDelivery-mail-send-pwd")));
			
			dataSeting.setAibotDBServerAddress(StringBase64Util.getBase64DecoderSting(prop.getProperty("aibotDB-server-address")));
			
			dataSeting.setAibotDBServerPort(StringBase64Util.getBase64DecoderSting(prop.getProperty("aibotDB-server-port")));
			
			dataSeting.setAibotDBServerAccount(StringBase64Util.getBase64DecoderSting(prop.getProperty("aibotDB-server-account")));
			
			dataSeting.setAibotDBServerPwd(StringBase64Util.getBase64DecoderSting(prop.getProperty("aibotDB-server-pwd")));
	
			
			
			// TODO return to Object
		} catch (IOException e) {
			System.out.println("報表資料夾確認發生失敗。" + e.getMessage());
		}

		return dataSeting;
	}

	private static void openFileChek(Path reportPath) {
		if (Files.notExists(reportPath)) {
			try {
				Files.createDirectories(reportPath.getParent());
			} catch (IOException e) {
				System.out.println("報表資料夾確認發生失敗。" + e.getMessage());
			}
		}

		if (Files.notExists(reportPath)) {
			try {
				Files.createFile(reportPath);
			} catch (IOException e) {
				System.out.println("報表資料夾確認發生失敗。" + e.getMessage());
			}
		}

	}
}
