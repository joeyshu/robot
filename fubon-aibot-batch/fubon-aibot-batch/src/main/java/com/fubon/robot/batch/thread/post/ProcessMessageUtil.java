package com.fubon.robot.batch.thread.post;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.fubon.robot.batch.log.LogSetting;
import com.fubon.robot.batch.robot.evdata.TSysVariable;

//@Component
public class ProcessMessageUtil {

	public static String MESSAGETYPE_BLACK = "BLACK"; // 適法性電文
	public static String MESSAGETYPE_SPEC = "SPEC"; // 特定電文

	private static Logger logger = LogSetting.geterLoger(Logger.getLogger("ProcessMessageUtil"));

	/**
	 * processMessage(CEW432R)拆分400部分
	 * 
	 * @author terry
	 * @since 2018/03/08
	 * 
	 * @param customerId 客戶身份證ID
	 * @param type       電文種類
	 * @param useMW      是否使用電銷電文
	 * @param key        電銷活動編號
	 * @param datasource 活動類型
	 * @return
	 * @throws ClassNotFoundException
	 */
	public String processMessageCE6220R(String customerId, String type, String useMW, String key, String datasource,
			String stano, TSysVariable variable) throws ClassNotFoundException {
		logger.log(Level.ALL,"====== 開始執行電文CE6220R發送 =====");
		// 發送查詢禁呼名單電文, 比對該名單客戶是否為禁呼名單, 並且回傳頁面
		if (MESSAGETYPE_BLACK.equalsIgnoreCase(type)) {
			try {
				SAXReader reader = new SAXReader();
				Document doc = reader.read(this.getClass().getResourceAsStream("/MessageXml/CE6220R.xml"));
				// 修改為客戶身資料
				// 身份證字號
				Node node = doc.selectSingleNode("/Tx/TxBody/PIN");
				node.setText(customerId);

				// 資料來源
				String Datasource = datasource;

				Node node2 = doc.selectSingleNode("/Tx/TxHead/HSTANO");

				node2.setText(stano);

				node = doc.selectSingleNode("/Tx/TxBody/TYPE");
				node.setText("5");
				logger.log(Level.INFO, "傳送電文內容:" + doc.asXML());
				// 執行查詢 TODO 正式將註解打開
				String isBlack = requestXmlByBlackListCE6220R(variable.getVariableValue(), doc.asXML(), useMW,Datasource, customerId, type);

				return isBlack;
			} catch (Exception e) {
				logger.log(Level.WARNING, "連線發生錯誤", e);
			}
		}

		return "";
	}

	/**
	 * 傳送xml至400做查詢是否為黑名單 是否NO TM
	 * 
	 * @param urlStr
	 * @param xmlString
	 */
	public String requestXmlByBlackListCE6220R(String urlStr, String xmlString, String useMW, String Datasource,
			String CustId, String type) {

		InputStream input = null;
		java.io.ByteArrayOutputStream out = null;
		String resultXml = "";
		String isBlack = "N";
		String outputName = "";//
		try {
			// String utf8String = new String(xmlString.getBytes("big5"), "UTF-8");
			// byte[] xmlData = utf8String.getBytes();

			// ==============正式接收電文==========

			URL url = new URL(urlStr);
			HttpURLConnection urlCon = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
			// URLConnection urlCon = url.openConnection();
			urlCon.setConnectTimeout(2000);
			urlCon.setDoOutput(true);
			urlCon.setDoInput(true);
			urlCon.setUseCaches(false);
			urlCon.setRequestMethod("POST");
			// 將xml數據發送到位置服務;
			urlCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;");
			IOUtils.write(xmlString.getBytes("utf-8"), urlCon.getOutputStream());
			String result = IOUtils.toString(urlCon.getInputStream(), "utf-8");
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new java.io.StringReader(result));
			logger.log(Level.INFO, "接收電文內容:" + doc.asXML());
			Node node = doc.selectSingleNode("/Tx/TxHead/HERRID");
			if (node == null) {
				outputName = "MW回傳空值";
				return outputName;
			} else if (node.getStringValue().equalsIgnoreCase("v814")
					|| node.getStringValue().equalsIgnoreCase("v803")) {// 2018-10-04 AddByWillie 電文回應修改
				outputName = "AS/400 NO TM 註記不可外撥";// temp 有可能要修改
				return outputName;
			} else if (!node.getStringValue().equals("0000")) {
				outputName = "MW回應錯誤:" + node.getStringValue();
				return outputName;
			}

			// Node aNTMFLG = doc.selectSingleNode("/Tx/TxBody/ANTMFLG");
			Node aNTMFLG = doc.selectSingleNode("/Tx/TxBody/TMSTS_FLAG");
			String ANTMFLG = aNTMFLG.getStringValue().trim();
//			String ANTMFLG = StringUtils.isEmpty(aNTMFLG.getStringValue().trim()) ? "Y" : "N";

			if ("N".equals(ANTMFLG)) {
				outputName = "AS/400 NO TM 註記不可外撥";
				isBlack = "Y";
			} else {
				if (Datasource.startsWith("信用卡") && useMW.equals("Y")) {
					// Node aCRDSTS = doc.selectSingleNode("/Tx/TxBody/ACRDSTS");
					Node aCRDSTS = doc.selectSingleNode("/Tx/TxBody/CARD_HOLD_STS");
					String ACRDSTS = "3".equals(aCRDSTS.getStringValue().trim()) ? "Y" : "N";
					if (!"Y".equals(ACRDSTS)) {
						outputName = "AS/400 無正常卡不可外撥";
						isBlack = "Y";
					}
				}
			}
			logger.log(Level.INFO, "回傳訊息=" + outputName);
			// 取得是否有回傳訊息,如果有代表沒有該客戶
			logger.log(Level.INFO, "是否為黑名單=" + isBlack);
		} catch (Exception e) {
			outputName = "MW失敗,Message=" + e.getMessage();
			isBlack = "ConnectError";
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (Exception ex) {
				logger.log(Level.WARNING, "======遠端連線XML失敗=====", ex);
			}
		}

		return outputName;
	}

	/**
	 * processMessage(CEW432R)拆分390部分
	 * 
	 * @author terry
	 * @since 2018/03/08
	 * 
	 * @param customerId 客戶身份證ID
	 * @param type       電文種類
	 * @param useMW      是否使用電銷電文
	 * @param key        電銷活動編號
	 * @param datasource 活動類型
	 * @return
	 * @throws ClassNotFoundException
	 */
	public String processMessageLM052671(String customerId, String type, String useMW, String key, String datasource,
			String stano, TSysVariable variable) throws ClassNotFoundException {
//		log.debug("====== 開始執行電文LM052671發送 =====");
		// 發送查詢禁呼名單電文, 比對該名單客戶是否為禁呼名單, 並且回傳頁面
		if (MESSAGETYPE_BLACK.equalsIgnoreCase(type)) {
			try {
				SAXReader reader = new SAXReader();
				Document doc = reader.read(this.getClass().getResourceAsStream("/MessageXml/LM052671.xml"));

				Node node = doc.selectSingleNode("/Tx/TxBody/CUST_NO");
				node.setText(customerId);

				// 資料來源
				String Datasource = datasource;

				Node node2 = doc.selectSingleNode("/Tx/TxHead/HSTANO");
				node2.setText(stano);
				logger.log(Level.INFO, "傳送電文內容:" + doc.asXML());
				// 執行查詢 TODO 正式 將註解打開!!!!!!!!!!!!!!!!!
				String isBlack = requestXmlByBlackListLM052671(variable.getVariableValue(), doc.asXML(), useMW,Datasource, customerId, type);

				return isBlack;
			} catch (Exception e) {
				logger.log(Level.WARNING, "電文傳送發生錯誤", e);
			}
		}

		return "";
	}

	/**
	 * 傳送xml至390做查詢是否為黑名單 是否NO TM
	 * 
	 * @param urlStr
	 * @param xmlString
	 */
	public String requestXmlByBlackListLM052671(String urlStr, String xmlString, String useMW, String Datasource,
			String CustId, String type) {

		InputStream input = null;
		java.io.ByteArrayOutputStream out = null;
		String resultXml = "";
		String isBlack = "N";
		String outputName = "";//
		try {
			// String utf8String = new String(xmlString.getBytes("big5"), "UTF-8");
			// byte[] xmlData = utf8String.getBytes();

			// ==============正式接收電文==========

			URL url = new URL(urlStr);
			HttpURLConnection urlCon = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
			// URLConnection urlCon = url.openConnection();
			urlCon.setConnectTimeout(2000);
			urlCon.setDoOutput(true);
			urlCon.setDoInput(true);
			urlCon.setUseCaches(false);
			urlCon.setRequestMethod("POST");
			// 將xml數據發送到位置服務;
			urlCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;");
			// urlCon.setRequestProperty("Content-length",
			// String.valueOf(xmlData.length));
			// urlCon.setRequestProperty("Accept-Charset", "UTF-8");

			IOUtils.write(xmlString.getBytes("utf-8"), urlCon.getOutputStream());
			String result = IOUtils.toString(urlCon.getInputStream(), "utf-8");
			// log.debug("電文連結= "+inputString);
			// input = IOUtils.toInputStream("utf-8");
			//
			// byte[] rResult;
			// out = new java.io.ByteArrayOutputStream();
			// byte[] bufferByte = new byte[256];
			// int l = -1;
			// int downloadSize = 0;
			// while ((l = input.read(bufferByte)) > -1) {
			// downloadSize += l;
			// out.write(bufferByte, 0, l);
			// out.flush();
			// }
			//
			// rResult = out.toByteArray();
			// resultXml = new String(rResult, "UTF-8");
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new java.io.StringReader(result));
			// ====== close for debug end

			// /*for debug start */
//			 String rootPath =
//			 ServletActionContext.getServletContext().getRealPath("/");
//			 String xmlFilePath = rootPath + "MessageXml\\LM052671_Result.xml";
//			 File f = new File(xmlFilePath);
//			 SAXReader reader = new SAXReader();
//			 Document doc = reader.read(f);
			// //for debug end
			//
			Node node = doc.selectSingleNode("/Tx/TxHead/HERRID");
			if (node == null) {
				outputName = "MW回傳空值";
				return outputName;
			} else if (!node.getStringValue().equals("0000") && !node.getStringValue().equals("2214")
					&& !node.getStringValue().equals("7001")) {
				outputName = "MW回應錯誤:" + node.getStringValue();
				return outputName;
			}

			// Node lifeeTMFLG = doc.selectSingleNode("/Tx/TxBody/CUST_NO");
			// String lifeTMFLG = lifeeTMFLG.getStringValue().trim();
			List<Node> tmssstatus = new ArrayList<Node>();
			tmssstatus = doc.selectNodes("/Tx/TxBody/TxRepeat/WA-X-STATUS");
			int tmflg = 0;
			tmflg = tmssstatus.size();
			String tmflg1 = "";
			int tmfinalflg = 0;
			for (int i = 0; i < tmssstatus.size(); i++) {
				if (tmssstatus.get(i).getStringValue().trim().equals("")
						|| tmssstatus.get(i).getStringValue().trim().equals("07")) {
					tmfinalflg = 1;
				} else {
					tmfinalflg = 0;
					break;
				}
			}

			if (tmflg == 1) {
				tmflg1 = doc.selectSingleNode("/Tx/TxBody/TxRepeat/WA-X-STATUS").getStringValue();
			}

			if (node.getStringValue().equals("2214") || node.getStringValue().equals("7001")) {
			} else {
				// Node oNTMFLG = doc.selectSingleNode("/Tx/TxBody/ONTMFLG");
				Node oNTMFLG = doc.selectSingleNode("/Tx/TxBody/NO_TM_FLG");
				String ONTMFLG = oNTMFLG.getStringValue().trim();
				ONTMFLG = StringUtils.isEmpty(ONTMFLG) ? "E" : ONTMFLG;

				if ("N".equals(ONTMFLG)) {
					outputName = "OS/390  NO TM 註記不可外撥";
					isBlack = "Y";
				} else if (Datasource.startsWith("存款") && (tmfinalflg == 1)) {
					outputName = "OS/390 存款結清不可外撥";
					isBlack = "Y";
				}
//			else if (Datasource.startsWith("舊戶"))
//			{
//				outputName = "OS/390 信貸結清不可外撥";
//						isBlack = "Y";
//			}
				else {
					if (Datasource.startsWith("存款")) {
						// Node oSAVFLG = doc.selectSingleNode("/Tx/TxBody/OSAVFLG");
						Node oSAVFLG = doc.selectSingleNode("/Tx/TxBody/PB_CLS_FLG");
						String OSAVFLG = oSAVFLG.getStringValue().trim();
						OSAVFLG = StringUtils.isEmpty(OSAVFLG) ? "E" : OSAVFLG;
						if ("Y".equals(OSAVFLG)) {
							outputName = "OS/390 存款結清不可外撥";
							isBlack = "Y";
						}
					} else if (Datasource.startsWith("信貸")) {
						// Node oCDTFLG = doc.selectSingleNode("/Tx/TxBody/OCDTFLG");
						Node oCDTFLG = doc.selectSingleNode("/Tx/TxBody/BY_CLS_FLG");
						String OCDTFLG = oCDTFLG.getStringValue().trim();
						OCDTFLG = StringUtils.isEmpty(OCDTFLG) ? "E" : OCDTFLG;
						if ("Y".equals(OCDTFLG)) {
							outputName = "OS/390 信貸結清不可外撥";
							isBlack = "Y";
						}

					} else if (Datasource.startsWith("房貸")) {
						// Node oMRGFLG = doc.selectSingleNode("/Tx/TxBody/OMRGFLG");
						Node oMRGFLG = doc.selectSingleNode("/Tx/TxBody/B1_CLS_FLG");
						String OMRGFLG = oMRGFLG.getStringValue().trim();
						OMRGFLG = StringUtils.isEmpty(OMRGFLG) ? "E" : OMRGFLG;
						if ("Y".equals(OMRGFLG)) {
							outputName = "OS/390 房貸結清不可外撥";
							isBlack = "Y";
						}
					} else if (Datasource.startsWith("留貸")) {
						// Node oSABFLG = doc.selectSingleNode("/Tx/TxBody/OSABFLG");
						Node oSABFLG = doc.selectSingleNode("/Tx/TxBody/BO_CLS_FLG");
						String OSABFLG = oSABFLG.getStringValue().trim();
						OSABFLG = StringUtils.isEmpty(OSABFLG) ? "E" : OSABFLG;
						if ("Y".equals(OSABFLG)) {
							outputName = "OS/390 留貸結清不可外撥";
							isBlack = "Y";
						}
					} else if (Datasource.startsWith("就貸")) {
						// Node oSTDFLG = doc.selectSingleNode("/Tx/TxBody/OSTDFLG");
						Node oSTDFLG = doc.selectSingleNode("/Tx/TxBody/BW_CLS_FLG");
						String OSTDFLG = oSTDFLG.getStringValue().trim();
						OSTDFLG = StringUtils.isEmpty(OSTDFLG) ? "E" : OSTDFLG;
						if ("Y".equals(OSTDFLG)) {
							outputName = "OS/390 就貸結清不可外撥";
							isBlack = "Y";
						}
					}
				}

			}
			// 取得是否有回傳訊息,如果有代表沒有該客戶
			logger.log(Level.INFO, "是否為黑名單=" + isBlack);
			logger.log(Level.INFO, "電文接收內容" + doc.asXML());
		} catch (Exception e) {
			outputName = "MW失敗,Message=" + e.getMessage();
			isBlack = "ConnectError";

		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (Exception ex) {

				logger.log(Level.WARNING, "電文接收發生錯誤", ex);
			}
		}

		return outputName;
	}

}
