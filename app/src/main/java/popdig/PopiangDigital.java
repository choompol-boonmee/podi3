package popdig;

import java.io.*;
import java.util.*;
import java.net.ServerSocket;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import javax.swing.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xssf.usermodel.*;
import org.apache.pdfbox.pdmodel.PDDocument;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class PopiangDigital {

	public static File fNode, fWork;
	public static String workDir;
	public static File fPrv, fPub;
	public static String sPrv="id_rsa", sPub="id_rsa.pub";
	public static String sEmail="", sPrefix="", sName="", sQuery="";
	public static boolean bGui = false;
	public static boolean bEmail = false;
	public static String sMainRepo="", sWorkRepo="", sBaseIRI="";
	public static String sRecvEmail="", sPassWord="", sImap="", sSmtp="";
	public static String sFontName="";
	public static int iFontSize=10;
	public static File[] aRdfFold;
	public static JFrame frame;
	public static Hashtable<String,RdfModelInfo> hRdfModelInfo;

	public static String sNodeType;
	public static final String mainNode = "mainNode";
	public static final String workNode = "workNode";
	public static String sOsName, sUserName, sUserHome, sTimeZone;
	public static boolean bAttend = false;
	public static String sAttendID = "";
	public static String sCom = "com";

	public static boolean bWindows = false, bMacos = false, bLinux = false;
	public static boolean bWebServer = false;
	public static PopiangWindow wind;
	public static String sWeb = "";
	public static String sSub = "";

	public static Map<String,String> hObject = new Hashtable<>();
//	public static int webPort = 8080;
	public static String webAddr = "0.0.0.0";
	public static int webPort = -1;
	public static WebServer webserver;
	public static List<String> aIriTrace = new ArrayList<>();

	public static String sSurvey;

	public static void setObject(String vo, String id) {
		if(vo==null || !vo.startsWith("vo:")) return;
		hObject.put(vo, id);
		if(wind!=null) wind.updateGui();
	}
	public static void clearObject(String vo) {
		hObject.put(vo,null);
		if(wind!=null) wind.updateGui();
	}
	public static void clearObject() {
		hObject.clear();
		if(wind!=null) wind.updateGui();
	}
	public static String getObject(String vo) {
		return hObject.get(vo);
	}
	public static EmailProc email = new EmailProc();
		
	public void proc(String[] args) {
		PopiangUtil.nodeInit();
		PopiangUtil.readNodeTtl();
		PopiangUtil.walkRtfTtl();
		if(bEmail) {
//			new EmailProc().startEmailThread();
			email.startEmailThread();
		}
		if(PopiangDigital.bGui) {
			wind = new PopiangWindow();
			wind.proc(new File(workDir));
		}
		if(PopiangDigital.bAttend) {
			PopiangUtil.runAttend();
		}
		if(PopiangDigital.bWebServer) {
			try {
				webserver = new WebServer();
//				webPort = new ServerSocket(0).getLocalPort();
//				webserver.startServer(PopiangDigital.webPort);
				webserver.startServer(PopiangDigital.webAddr, PopiangDigital.webPort);
			} catch(Exception x) {
				x.printStackTrace();
			}
		}
		if(PopiangDigital.sSurvey!=null) {
			String sv = PopiangDigital.sSurvey;
			System.out.println("survey:"+ sv);
		}
	}
}

