package com.fubon.robot.batch.thread.post;

import java.io.IOException;
import java.util.logging.Logger;

import com.fubon.robot.batch.log.LogSetting;

import sun.misc.BASE64Decoder;

public class StringBase64Util {
	//解碼器不用一直NEW
	private static final BASE64Decoder decoder = new BASE64Decoder();

	private static Logger logger = LogSetting.geterLoger(Logger.getLogger("StringBase64Util"));

	public static String getBase64DecoderSting(String data) {

		try {
			data = new String(decoder.decodeBuffer(data));
		} catch (IOException e) {
			logger.info(e.getMessage());
		}

		return data;
	}
}
