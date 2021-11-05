package com.fubon.robot.batch.thread.post;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.fubon.robot.batch.log.LogSetting;
import com.fubon.robot.batch.robot.evdata.TBusCustomer;
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
		logger.log(Level.ALL, "====== 開始執行電文CE6220R發送 =====");
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
				String isBlack = requestXmlByBlackListCE6220R(variable.getVariableValue(), doc.asXML(), useMW,
						Datasource, customerId, type);

				return isBlack;
			} catch (Exception e) {
				logger.log(Level.WARNING, "連線發生錯誤", e);
			}
		}

		return "";
	}

	/**
	 * 配合新甄審 發送二次電文 add by Tom CEW434R
	 * 
	 * @param customerId
	 * @param useMW
	 * @param type
	 * @return
	 * @throws ClassNotFoundException
	 */
	public String processMessageNew(String customerId, String useMW, String type, String stano, TSysVariable variable)
			throws ClassNotFoundException {
//		log.debug("====== 開始執行電文發送 =====");

		// 發送查詢禁呼名單電文, 比對該名單客戶是否為禁呼名單, 並且回傳頁面
		if (MESSAGETYPE_BLACK.equalsIgnoreCase(type)) {
			try {
				// 取得文件發送服務
//				SysVariableService variableService = (SysVariableService) SysSpringContextProvider.getSpringBean("variableService");
//
//				// 取得環境變數定義好的電文位置
//				TSysVariable variable = variableService.findById(BLACK_VARIABLE_ID_TWO);

				SAXReader reader = new SAXReader();
				Document doc = reader.read(this.getClass().getResourceAsStream("/MessageXml/CEW434R.xml"));
				// 修改為客戶身資料
				// 身份證字號
				Node node = doc.selectSingleNode("/Tx/TxBody/CUSID");
				node.setText(customerId);

				// 資料來源
				// node = doc.selectSingleNode("/Tx/TxBody/TYPE");
				// node.setText(datasource);

				Node node2 = doc.selectSingleNode("/Tx/TxHead/HSTANO");

				node2.setText(stano);

				logger.log(Level.INFO, "傳送電文內容:" + doc.asXML());

				// 執行查詢
				String isBlack = requestXmlByBlackListNew(variable.getVariableValue(), doc.asXML(), useMW);

				return isBlack;
			} catch (DocumentException e) {
				logger.log(Level.INFO, "傳送電文內容:" + e.getMessage());
			}
		}
		return "";
	}

	// 配合新甄審系統 發送新電文 add by Tom 2016-04-11
	/**
	 * 傳送xml做查詢是否為黑名單 是否NO TM
	 * 
	 * @param urlStr
	 * @param xmlString
	 */
	public String requestXmlByBlackListNew(String urlStr, String xmlString, String useMW) {

		InputStream input = null;
		java.io.ByteArrayOutputStream out = null;
		String resultXml = "";
		String isBlack = "N";
		String outputName = "";//
		try {
			// String utf8String = new String(xmlString.getBytes("big5"),
			// "UTF-8");
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
			// 將xml數據發送到位置服務
			urlCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;");

			IOUtils.write(xmlString.getBytes("utf-8"), urlCon.getOutputStream());
			String result = IOUtils.toString(urlCon.getInputStream(), "utf-8");
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new java.io.StringReader(result));

//				log.debug("接收電文內容:");
//				log.debug(formatXML(doc.asXML()));

			Node node = doc.selectSingleNode("/Tx/TxHead/HERRID");
			if (node == null) {
				outputName = "MW回傳空值";
				return outputName;
			} else if (!node.getStringValue().equals("0000")) {
				outputName = "MW回應錯誤:" + node.getStringValue();
				return outputName;
			}

			Node ELNFLG = doc.selectSingleNode("/Tx/TxBody/ELNFLG");
			String eLNFLG = ELNFLG.getStringValue();
			if ("Y".equals(eLNFLG) && useMW.equals("Y")) {
				outputName = "新徵審有進件紀錄不可外撥";
				isBlack = "Y";
			}

			// 取得是否有回傳訊息,如果有代表沒有該客戶
			logger.log(Level.INFO, "回傳訊息=" + outputName);
			// log.debug("是否為黑名單=" + isBlack);

		} catch (Exception e) {
			outputName = "MW失敗,Message=" + e.getMessage();
			isBlack = "ConnectError";
			logger.log(Level.WARNING, "======遠端連線XML失敗=====", e);
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
				String isBlack =requestXmlByBlackListLM052671(variable.getVariableValue(), doc.asXML(), useMW,
						Datasource, customerId, type);

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

	/**
	 * 傳送xml做查詢信用卡是否為已開卡不可外撥
	 * 
	 * @param urlStr
	 * @param xmlString
	 */
	public String requestXmlByCredit(String urlStr, String xmlString) {
		InputStream input = null;
		java.io.ByteArrayOutputStream out = null;
		String resultXml = "";
		String isBlack = "N";
		String outputName = "";//
		try {
			// String utf8String = new String(xmlString.getBytes("big5"),
			// "UTF-8");
			// byte[] xmlData = utf8String.getBytes();

			// ==============正式接收新戶電文==========

			URL url = new URL(urlStr);
			HttpURLConnection urlCon = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
			// URLConnection urlCon = url.openConnection();
			urlCon.setConnectTimeout(2000);
			urlCon.setDoOutput(true);
			urlCon.setDoInput(true);
			urlCon.setUseCaches(false);
			urlCon.setRequestMethod("POST");
			// 將xml數據發送到位置服務
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

			// =============正式接收新戶開卡電文==========

			// 判斷TxHead標籤
			Node node = doc.selectSingleNode("/Tx/TxHead/HERRID");
			String herrid = node.getStringValue().trim();
			// (1)是否為有效卡 : Crdcde (0 : 正常) 非0 訊息:非有效卡，不可撥打
			if (herrid != null) {
				logger.info("該卡回傳訊息herrid=" + herrid);
				if ("0000".equals(herrid)) {
					Node Crdcdenode = doc.selectSingleNode("/Tx/TxBody/Crdcde");
					String Crdcde = Crdcdenode.getStringValue();
					if (Crdcde != null) {
						Crdcde = Crdcde.trim();
					}
					if ("0".equals(Crdcde)) {
						// outputName = "有效卡";
						isBlack = "N";
						Node Cdopc1node = doc.selectSingleNode("/Tx/TxBody/Cdopc1");
						String Cdopc1 = Cdopc1node.getStringValue();
						if (Cdopc1 != null) {
							Cdopc1 = Cdopc1.trim();
						}
						if (!"N".equals(Cdopc1)) {
							outputName = "已開卡";
							isBlack = "N";
						} else {
							outputName = "未開卡";
							isBlack = "Y";
						}
					} else {
						isBlack = "Y";
						outputName = "非有效卡";
					}
				}
			} else {
				outputName = "herrid=null";
			}

			// 顯示是否開卡
			logger.info("是否已開卡=" + outputName);
		} catch (Exception e) {
			isBlack = "ConnectError";
			outputName = "ConnectError";
			logger.log(Level.WARNING, "======遠端連線XML失敗=====", e);

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
		return outputName;// outputName
	}

	/*
	 * 執行新戶開卡電文發送
	 * 
	 * @param customerId 客戶身份證ID
	 * 
	 * @param type 電文種類
	 * 
	 * @param useMW 是否使用信用卡電文
	 * 
	 * @param key 信用卡活動編號
	 * 
	 * @param datasource 活動類型
	 * 
	 * @return
	 */
	public TBusCustomer processMessageForCredit(TBusCustomer customer, String type, String useMW, String key,
			String datasource, TSysVariable variable, String stano, String isBlack) {
//		log.debug("====== 開始執行信用卡電文發送 =====");
		// 發送查詢禁呼名單電文, 比對該名單客戶是否為禁呼名單, 並且回傳頁面
		if (MESSAGETYPE_BLACK.equalsIgnoreCase(type)) {
			try {
				// 取得文件發送服務
//				SysVariableService variableService = (SysVariableService) SysSpringContextProvider.getSpringBean("variableService");
//
//				// 取得環境變數定義好的電文位置
//				TSysVariable variable = variableService.findById(BLACK_VARIABLE_ID);

				// 取得本地端己存放好的XML樣本
//				String rootPath = ServletActionContext.getServletContext().getRealPath("/");
//				String xmlFilePath = rootPath + "MessageXml\\CE6123R.xml";
//				File f = new File(xmlFilePath);
//				SAXReader reader = new SAXReader();
//				Document doc = reader.read(f);

				SAXReader reader = new SAXReader();
				Document doc = reader.read(this.getClass().getResourceAsStream("/MessageXml/CE6123R.xml"));

				logger.info("載入信用卡電文範例檔:");
				logger.info(doc.asXML());

				// 修改為客戶卡號
				// 卡號
				Node node = doc.selectSingleNode("/Tx/TxBody/Crdno");
				int i = 1;
				boolean isnovalue = false;
				boolean EnableCall = false; // 未開卡
				boolean EnableCall2 = false; // 已開卡
				String ReturnMsg = "";
				while (i <= 9 && !isnovalue) {
					switch (i) {
					case 1:
						if (customer.getCustomer_104() != null) {
							node.setText(customer.getCustomer_104());
						} else {
							isnovalue = true;
						}
						break;
					case 2:
						if (customer.getCustomer_108() != null) {
							node.setText(customer.getCustomer_108());
						} else {
							isnovalue = true;
						}
						break;
					case 3:
						if (customer.getCustomer_112() != null) {
							node.setText(customer.getCustomer_112());
						} else {
							isnovalue = true;
						}
						break;
					case 4:
						if (customer.getCustomer_116() != null) {
							node.setText(customer.getCustomer_116());
						} else {
							isnovalue = true;
						}
						break;
					case 5:
						if (customer.getCustomer_120() != null) {
							node.setText(customer.getCustomer_120());
						} else {
							isnovalue = true;
						}
						break;
					case 6:
						if (customer.getCustomer_124() != null) {
							node.setText(customer.getCustomer_124());
						} else {
							isnovalue = true;
						}
						break;
					case 7:
						if (customer.getCustomer_128() != null) {
							node.setText(customer.getCustomer_128());
						} else {
							isnovalue = true;
						}
						break;
					case 8:
						if (customer.getCustomer_132() != null) {
							node.setText(customer.getCustomer_132());
						} else {
							isnovalue = true;
						}
						break;
					case 9:
						if (customer.getCustomer_136() != null) {
							node.setText(customer.getCustomer_136());
						} else {
							isnovalue = true;
						}
						break;
					}
					Node node2 = doc.selectSingleNode("/Tx/TxHead/HSTANO");
					// String stime = "" +
					// ApDateTime.getNowDateTime("yyyyMMddHHmmssSSS");
					// stime = stime.substring(8, 15);

					node2.setText(stano);
					// 查詢方式(1:含持卡人、持卡人歸戶資料及持卡人附卡資料2﹕含持卡人及持卡人附卡人資料
					// 3：含持卡人所有正常卡及持卡人相關正常附卡 4：持卡人ECard正卡 5：目前持卡狀態)

					logger.info("傳送電文內容:");
					logger.info(doc.asXML());
					logger.info("客戶卡號=" + node.getText());
					if (node.getText().equals("0000000000")) { // 遮蔽卡，視為非有效卡
						isBlack = "非有效卡";
					} else {
						// 執行查詢
						isBlack = requestXmlByCredit(variable.getVariableValue(), doc.asXML());
					}
					ReturnMsg = isBlack;
					if (isBlack.equals("未開卡")) {
						EnableCall = true;
						logger.info("偵測到未開卡 EnableCall=" + EnableCall);

					} else if (isBlack.equals("已開卡") || isBlack.equals("非有效卡")) {
						if (isBlack.equals("已開卡")) {
							EnableCall2 = true;
							logger.info("偵測到已開卡 EnableCall=" + EnableCall2);
						}
						if (!isnovalue) {
							switch (i) {
							case 1:
								customer.setCustomer_101("0000000000");
								customer.setCustomer_102("0000000000");
								customer.setCustomer_103("0000000000");
								customer.setCustomer_104("0000000000");
								break;
							case 2:
								customer.setCustomer_105("0000000000");
								customer.setCustomer_106("0000000000");
								customer.setCustomer_107("0000000000");
								customer.setCustomer_108("0000000000");
								break;
							case 3:
								customer.setCustomer_109("0000000000");
								customer.setCustomer_110("0000000000");
								customer.setCustomer_111("0000000000");
								customer.setCustomer_112("0000000000");
								break;
							case 4:
								customer.setCustomer_113("0000000000");
								customer.setCustomer_114("0000000000");
								customer.setCustomer_115("0000000000");
								customer.setCustomer_116("0000000000");
								break;
							case 5:
								customer.setCustomer_117("0000000000");
								customer.setCustomer_118("0000000000");
								customer.setCustomer_119("0000000000");
								customer.setCustomer_120("0000000000");
								break;
							case 6:
								customer.setCustomer_121("0000000000");
								customer.setCustomer_122("0000000000");
								customer.setCustomer_123("0000000000");
								customer.setCustomer_124("0000000000");
								break;
							case 7:
								customer.setCustomer_125("0000000000");
								customer.setCustomer_126("0000000000");
								customer.setCustomer_127("0000000000");
								customer.setCustomer_128("0000000000");
								break;
							case 8:
								customer.setCustomer_129("0000000000");
								customer.setCustomer_130("0000000000");
								customer.setCustomer_131("0000000000");
								customer.setCustomer_132("0000000000");
								break;
							case 9:
								customer.setCustomer_133("0000000000");
								customer.setCustomer_134("0000000000");
								customer.setCustomer_135("0000000000");
								customer.setCustomer_136("0000000000");
								break;
							}
						}

					}
					i++;
				}
				if (customer.getCustomer_54() != null) {
					customer.setCustomer_54(customer.getCustomer_54().replaceAll(" ", ""));
				}
				if (customer.getCustomer_58() != null) {
					customer.setCustomer_58(customer.getCustomer_58().replaceAll(" ", ""));
				}
				if (customer.getCustomer_147() != null) {
					customer.setCustomer_147(customer.getCustomer_147().replaceAll(" ", ""));
				}
				if (customer.getCustomer_148() != null) {
					customer.setCustomer_148(customer.getCustomer_148().replaceAll(" ", ""));
				}
				if (customer.getCustomer_149() != null) {
					customer.setCustomer_149(customer.getCustomer_149().replaceAll(" ", ""));
				}
				logger.log(Level.INFO, "卡片狀態1:" + EnableCall + " 卡片狀態2:" + EnableCall2);
				if (!EnableCall) {
					if (EnableCall2) { // 如果有已開卡
						customer.setProposerId("-1");
					} else // 如果沒有已開卡，則應該都是無效卡
					{
						customer.setProposerId("-2");
					}
				} else {
					customer.setProposerId("0"); // add by Tom 2016-10-04
													// 如果可外撥，將判斷狀態改為正常。
				}
				return customer;
			} catch (Exception e) {
				customer.setProposerId("-3");
				logger.log(Level.INFO, "執行信用卡電文發送失敗!!", e);
				return customer;
			}
		}

		return customer;
	}

	/**
	 * 執行挽卡電文發送
	 * 
	 * @param customerId 客戶身份證ID
	 * @param type       電文種類
	 * @param useMW      是否使用信用卡電文
	 * @param key        信用卡活動編號
	 * @param datasource 活動類型
	 * @return
	 */
	public String processMessageForCreditCE6121R(TBusCustomer customer, String type, String useMW, String key,
			String projectNO, TSysVariable variable, String stano) {
//		log.debug("====== 開始執行信用卡挽卡電文發送 =====");
		String isBlack = "N";
		// 發送查詢禁呼名單電文, 比對該名單客戶是否為禁呼名單, 並且回傳頁面
		if (MESSAGETYPE_BLACK.equalsIgnoreCase(type)) {
			try {
				// 取得文件發送服務
				SAXReader reader = new SAXReader();
				Document doc = reader.read(this.getClass().getResourceAsStream("/MessageXml/CE6121R.xml"));
				logger.info("載入信用卡電文範例檔:" + doc.asXML());
				// 修改為客戶卡號
				// 卡號
				Node accid = doc.selectSingleNode("/Tx/TxBody/Accid");
				accid.setText(customer.getCustomer_28());

				Node hstano = doc.selectSingleNode("/Tx/TxHead/HSTANO");
				// String stime = "" +
				// ApDateTime.getNowDateTime("yyyyMMddHHmmssSSS");
				// stime = stime.substring(8, 15);

//				StanoUtil stanoUtil = new StanoUtil();
//				String Stano = stanoUtil.GetMWStano();

				hstano.setText(stano);

				Node projectNo = doc.selectSingleNode("/Tx/TxBody/ProjectNo");
				projectNo.setText(projectNO); // JiaJia 2015-08-11 活動編號

				Node groupNo = doc.selectSingleNode("/Tx/TxBody/GroupNo");
				if (customer.getCustomer_137() != null)
					groupNo.setText(customer.getCustomer_137()); // JiaJia
																	// 2015-07-16
																	// 記得要改成需要欄位
																	// //群組代號
				// log.debug("記得要改回來");

				logger.log(Level.INFO, "傳送電文內容:" + doc.asXML());

				// 執行查詢
				isBlack =requestXmlByCreditCE6121R(variable.getVariableValue(), doc.asXML());
				// if (isBlack.equals("未開卡")) {
				// }

				return isBlack;
			} catch (DocumentException e) {
				logger.log(Level.INFO, "執行信用卡電文發送失敗!!", e);
			}
		}

		return isBlack;
	}

	/**
	 * 傳送CE6121R xml做查詢信用卡挽卡用戶是否符合資格不可外撥
	 * 
	 * @param urlStr
	 * @param xmlString
	 */
	public String requestXmlByCreditCE6121R(String urlStr, String xmlString) {
		InputStream input = null;
		java.io.ByteArrayOutputStream out = null;
		String resultXml = "";
		String isBlack = "N";
		String outputName = "";//
		try {
			// String utf8String = new String(xmlString.getBytes("big5"),
			// "UTF-8");
			// byte[] xmlData = utf8String.getBytes();

			// ==============正式接收挽卡電文==========
			URL url = new URL(urlStr);
			HttpURLConnection urlCon = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
			urlCon.setConnectTimeout(2000);
			urlCon.setDoOutput(true);
			urlCon.setDoInput(true);
			urlCon.setUseCaches(false);
			urlCon.setRequestMethod("POST");
			// 將xml數據發送到位置服務
			urlCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;");
			IOUtils.write(xmlString.getBytes("utf-8"), urlCon.getOutputStream());
			String result = IOUtils.toString(urlCon.getInputStream(), "utf-8");
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new java.io.StringReader(result));
			// ==============正式接收挽卡電文==========
			logger.log(Level.INFO, "接收電文內容:" + formatXML(doc.asXML()));
			// 判斷TxHead標籤
			Node node = doc.selectSingleNode("/Tx/TxHead/HERRID");
			String herrid = node.getStringValue().trim();

			if (herrid != null) {
				if ("0000".equals(herrid)) {
					Node Cusflag = doc.selectSingleNode("/Tx/TxBody/Cusflag");
					String cusflag = Cusflag.getStringValue().trim();
					if (cusflag.equals("Y")) {
						outputName = "符合挽卡資格";
					} else {
						outputName = "未符合挽卡資格";
						isBlack = "Y";
					}

				} else {
					Node Emsgtxt = doc.selectSingleNode("/Tx/TxBody/EMSGTXT");
					String emsgtxt = Emsgtxt.getStringValue().trim();
					outputName = emsgtxt;
					isBlack = "Y";
				}
			} else {
				outputName = "herrid=null";
				isBlack = "Y";
			}
			// 顯示是否符合挽卡資格
			logger.log(Level.INFO,"是否符合挽卡資格=" + outputName);

		} catch (Exception e) {
			isBlack = "ConnectError";
			outputName = "ConnectError";
//			log.error("======遠端連線XML失敗=====", e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (Exception ex) {
//				log.error("======遠端連線XML失敗=====", ex);
			}
		}
		return outputName;// outputName
	}

	public String formatXML(String retStr) throws Exception {
		String res = null;
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(retStr);
		} catch (DocumentException e) {
			logger.log(Level.INFO, "XML格式化失敗!!", e);
			return null;
		}
		// 格式化XML
		OutputFormat format = new OutputFormat();
		// 設置元素是否有子節點都輸出
		format.setExpandEmptyElements(true);
		// 設置不輸出XML聲明
		format.setSuppressDeclaration(true);

		OutputStream outputStream = new ByteArrayOutputStream();
		XMLWriter writer = new XMLWriter(outputStream, format);
		writer.write(doc);
		writer.close();
		res = outputStream.toString();
		return res;
	}
}
