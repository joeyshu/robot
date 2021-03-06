package com.fubon.robot.batch.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogSetting {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private static final String LOG_FILE_SUFFIX = ".log";

	private synchronized static String getLogFilePath() {

		StringBuffer logFilePath = new StringBuffer();

		logFilePath.append("./BatchLog");
		File file = new File(logFilePath.toString());
		if (!file.exists()) {
			file.mkdir();
		}
		logFilePath.append(File.separatorChar);
		logFilePath.append(sdf.format(new Date()));
		logFilePath.append(LOG_FILE_SUFFIX);
		return logFilePath.toString();
	}

	public synchronized static Logger geterLoger(Logger logger) {
		return setLoggerHanlder(logger, Level.ALL);
	}

	public synchronized static Logger setLoggerHanlder(Logger logger, Level level) {
		FileHandler fileHandler = null;
		try {
			fileHandler = new FileHandler(getLogFilePath(), true);

			fileHandler.setFormatter(new SimpleFormatter());
			fileHandler.setEncoding("UTF-8");
			logger.addHandler(fileHandler);
			logger.setLevel(level);

		} catch (SecurityException e) {
			logger.severe(populateExceptionStackTrace(e));
		} catch (IOException e) {
			logger.severe(populateExceptionStackTrace(e));
		}
		return logger;
	}

	private synchronized static String populateExceptionStackTrace(Exception e) {
		StringBuilder sb = new StringBuilder();
		sb.append(e.toString()).append("\n");
		for (StackTraceElement elem : e.getStackTrace()) {
			sb.append("\tat ").append(elem).append("\n");
		}
		return sb.toString();
	}
}
