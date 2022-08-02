package popdig;

import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.io.*;
import javax.swing.*;
import java.util.*;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
//import com.sun.mail.util.BASE64DecoderStream;
import java.net.InetAddress;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
//import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.Key;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.util.Objects;

import com.google.common.base.Splitter;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.query.*;

import java.util.*;
import javax.mail.*;
import javax.mail.event.*;
import javax.mail.internet.*;
import java.io.*;
//import com.sun.mail.imap.*;
import org.jsoup.Jsoup;

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
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.log4j.Logger;
import org.dom4j.Document;  
import org.dom4j.DocumentHelper;  
import org.dom4j.io.OutputFormat;  
import org.dom4j.io.XMLWriter;
import java.util.Collections;
import java.util.regex.*;
import java.util.stream.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.util.Units;
//import org.apache.poi.xwpf.usermodel.Document;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class PopiangUtil {

	static Logger log;

	public static void nodeInit() {
		setWorkDir();
		setLogging();

		PopiangDigital.sOsName = System.getProperty("os.name");
		PopiangDigital.sUserName = System.getProperty("user.name");
		PopiangDigital.sUserHome = System.getProperty("user.home");
		PopiangDigital.sTimeZone = System.getProperty("user.timezone");

		log.info("os.name: "+ PopiangDigital.sOsName);
		log.info("user.name: "+ PopiangDigital.sUserName);
		log.info("user.home: "+ PopiangDigital.sUserHome);
		log.info("user.timezone: "+ PopiangDigital.sTimeZone);
		if(PopiangDigital.sOsName.startsWith("Windows")) PopiangDigital.bWindows = true;
		if(PopiangDigital.sOsName.startsWith("Mac")) PopiangDigital.bMacos = true;
		if(PopiangDigital.sOsName.startsWith("Linux")) PopiangDigital.bLinux = true;

		if(!PopiangDigital.fNode.exists()) {
			log.info("CONFIG DIALOG");
			PopiangConfig conf = new PopiangConfig();
			conf.config();
			log.info("============== CONFIG ==============");
			return;
		} else {
		}
	}

	public static void setWorkDir() {
		try {
System.out.println("workdir...");
			File fd = new File(PopiangDigital.class.getProtectionDomain()
				.getCodeSource().getLocation().toURI().getPath());
			fd = fd.getParentFile();
			String jpath = fd.getAbsolutePath();
			String cond1 = "app/build/libs";
			String cond2 = "bin";
			String cond3 = "app/build/classes/java";
System.out.println(fd);
			if(jpath.endsWith(cond1)) { // developing
				PopiangDigital.workDir = jpath.substring(0, 
					jpath.length()-cond1.length())+"work";
			} else if(jpath.endsWith(cond2)) { // runtime
				String wd = jpath.substring(0, jpath.length()-cond2.length());
				if(wd.endsWith("/")) wd = wd.substring(0,wd.length()-1);
				PopiangDigital.workDir = wd;
			} else if(jpath.endsWith(cond3)) { // build
				PopiangDigital.workDir = jpath.substring(0, 
					jpath.length()-cond3.length())+"work";
			} else { // unknown
				PopiangDigital.workDir = new File(".").getAbsolutePath();
			}
			PopiangDigital.fWork = new File(PopiangDigital.workDir);
			File cfg = new File(PopiangDigital.workDir+"/.cfg/");
			if(!cfg.exists()) cfg.mkdirs();
			PopiangDigital.fPrv = new File(PopiangDigital.workDir+"/.cfg/"+PopiangDigital.sPrv);
			PopiangDigital.fPub = new File(PopiangDigital.workDir+"/.cfg/"+PopiangDigital.sPub);
			PopiangDigital.fNode = new File(PopiangDigital.workDir+"/.cfg/node.ttl");

			File fGit = new File(PopiangDigital.workDir+"/.git");
			if(!fGit.exists()) {
				System.out.println("GIT INIT at "+fGit.getAbsolutePath());
				runGit("git", "init");
			}
			
System.out.println("node init");

		} catch(Exception z) {
			z.printStackTrace();
		}
	}

	public static void setLogging() {
		try {

//System.out.println("log ..1");
			ConfigurationBuilder<BuiltConfiguration> builder
				 = ConfigurationBuilderFactory.newConfigurationBuilder();
			builder.setStatusLevel(Level.INFO);
			builder.setStatusLevel(Level.DEBUG);
			builder.addProperty("basePath", PopiangDigital.workDir+"/.log");
			AppenderComponentBuilder console = builder.newAppender("console", "Console"); 
			console.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
			LayoutComponentBuilder lay = builder.newLayout("PatternLayout");
			lay.addAttribute("pattern", "[%-5level] %d{yyyyMMdd-HHmmss}-%c{1}: %msg%n");
			console.add(lay);
			builder.add(console);

//System.out.println("log ..2");
			LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
			    .addAttribute("pattern", "%d [%t] %-5level: %msg%n");
			ComponentBuilder policy = builder.newComponent("Policies")
				.addComponent(builder.newComponent("TimeBasedTriggeringPolicy")
					.addAttribute("interval", "1")
					.addAttribute("modulate", "true"));

//System.out.println("log ..3");
			AppenderComponentBuilder appenderBuilder = builder.newAppender("rolling", "RollingFile")
			    .addAttribute("fileName", "${basePath}/rolling.log")
			    .addAttribute("filePattern", "${basePath}/rolling-%d{MM-dd-yy}.log.gz")
			    .add(layoutBuilder)
			    .addComponent(policy);
			builder.add(appenderBuilder);

//System.out.println("log ..4");
			RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.INFO);
			rootLogger.add(builder.newAppenderRef("console"));
			rootLogger.add(builder.newAppenderRef("rolling"));
			rootLogger.addAttribute("additivity", false);
			builder.add(rootLogger);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			builder.writeXmlConfiguration(baos);
			String xml = new String(baos.toByteArray());
			//builder.writeXmlConfiguration(System.out);
			Document doc = DocumentHelper.parseText(xml);  
			StringWriter sw = new StringWriter();  
			OutputFormat format = OutputFormat.createPrettyPrint();  
			XMLWriter xw = new XMLWriter(sw, format);  
			xw.write(doc);  
System.out.println(sw.toString());

			LoggerContext ctx = Configurator.initialize(builder.build());
System.out.println("ctx init");

//			LoggerContext context = (LoggerContext) LogManager.getContext(false);
//			context.setConfigLocation(new File(workDir+"/.cfg/log4j2.xml").toURI());

			ctx.updateLoggers();
System.out.println("ctx update");

			log = Logger.getLogger(PopiangUtil.class);
System.out.println("getLogger");

		} catch(Exception z) {
			z.printStackTrace();
		}
	}

	public final static String defaultBaseUrl = "http://popiang.com/rdf";

	public static void readNodeTtl() {
		try {

System.out.println("================================================");
log.info("NODE.TTL"+PopiangDigital.fNode.getAbsolutePath());
			String ct = new String(Files.readAllBytes(
				Paths.get(PopiangDigital.fNode.getAbsolutePath())),"UTF-8");
log.info(ct);

			org.apache.jena.query.ARQ.init();
			Model mo = ModelFactory.createDefaultModel();
			mo.read(new FileInputStream(PopiangDigital.fNode), null, "TTL");

			String sqry = ""
				+ "PREFIX vp: 	<"+defaultBaseUrl+"/voc-pred#>"
				+ "PREFIX vo: 	<"+defaultBaseUrl+"/voc-obj#>"
				+ "PREFIX nd:	<"+defaultBaseUrl+"/nd#>"
				+ " SELECT ?c ?d ?e ?g ?h ?i ?k ?l ?m ?n ?o ?p ?j ?q ?r ?nt ?qry ?u ?s WHERE { "
				+ " ?a a vo:PopiNode . "
				+ " ?a vp:type ?nt . "
				+ " ?a vp:owner ?f . "
				+ " ?a vp:prefix ?h . "
				+ " ?a vp:mainrep ?n . "
				+ " ?a vp:workrep ?o . "
				+ " ?a vp:baseiri ?p . "
				+ " ?f vp:email ?g . "
				+ " ?f vp:name ?i . "

				+ " OPTIONAL { ?a vp:channel ?b . "
				+ " ?b vp:email ?c . "
				+ " ?b vp:passwd ?d . "
				+ " ?b vp:imap ?e . "
				+ " ?b vp:smtp ?m . "
				+ " } "

				+ " OPTIONAL { ?a vp:gui ?j . "
				+ " ?j vp:fontName ?k . "
				+ " ?j vp:fontSize ?l . "
  				+ "	} "

				+ " OPTIONAL { ?a vp:attend ?q . "
				+ " ?q vp:id ?r . "
  				+ "	} "

				+ " OPTIONAL { ?a vp:webAddr ?ad . "
  				+ "	} "

				+ " OPTIONAL { ?a vp:webPort ?u . "
  				+ "	} "

				+ " OPTIONAL { ?a vp:survey ?s . "
  				+ "	} "

				+ " OPTIONAL { ?a vp:query ?qry . } "

  				+ "	} "
				;

			List<Map<String,String>> aMap = sparql0(mo, sqry);
log.info("MAP: "+ aMap.size());
			if(aMap.size()==1) {
				PopiangDigital.sNodeType = PopiangDigital.mainNode;
				Map<String,String> map = aMap.get(0);
				String nt = map.get("nt");
				if(nt.indexOf("#MainNode")>0)
					PopiangDigital.sNodeType = PopiangDigital.mainNode;
				else PopiangDigital.sNodeType = PopiangDigital.workNode;
				log.info("NODETYPE: "+ PopiangDigital.sNodeType);
				PopiangDigital.sRecvEmail = map.get("c");
				PopiangDigital.sPassWord = map.get("d");
				PopiangDigital.sImap = map.get("e");
				PopiangDigital.sSmtp = map.get("m");
				if(PopiangDigital.sRecvEmail!=null && PopiangDigital.sPassWord!=null
					&& PopiangDigital.sImap!=null && PopiangDigital.sSmtp!=null) {
					PopiangDigital.bEmail = true;
				}
				PopiangDigital.sEmail = map.get("g");
log.info("OWNER EMAIL: "+ PopiangDigital.sEmail);
				PopiangDigital.sPrefix = map.get("h");
				PopiangDigital.sName = map.get("i");
				PopiangDigital.sMainRepo = map.get("n");
				PopiangDigital.sWorkRepo = map.get("o");
				PopiangDigital.sBaseIRI = map.get("p");
				PopiangDigital.sSurvey = map.get("s");
System.out.println("========= BASE: "+ PopiangDigital.sBaseIRI);
				PopiangDigital.sQuery = map.get("qry");
log.info("QUERY: "+ map.get("qry"));
				if(map.get("j")!=null) { // gui
					PopiangDigital.bGui = true;
					PopiangDigital.sFontName = map.get("k");
					try { PopiangDigital.iFontSize = Integer.parseInt(map.get("l"));
						} catch(Exception x) {}
				}
				if(map.get("q")!=null) { // attend
					PopiangDigital.bAttend = true;
					PopiangDigital.sAttendID = map.get("r");
				}
				String u;
				if((u=map.get("u"))!=null) {
					System.out.println("webPort: "+ u);
					try {
						int p = Integer.parseInt(u);
						System.out.println("PORT: "+ p);
						if(p>1000 && p<60000) {
System.out.println("WEB SERVER AT PORT: "+ p);
							PopiangDigital.bWebServer = true;
							PopiangDigital.webPort = p;
						}
					} catch(Exception z) {
						System.out.println("PORT ERROR");
					}
				} else {
					System.out.println("NO URL: ");
				}
				String ad;
				if((ad=map.get("ad"))!=null) {
					System.out.println("webAddr: "+ ad);
					PopiangDigital.webAddr = ad;
				}
				log.info("recv: "+ PopiangDigital.sRecvEmail);
				log.info("pass: "+ PopiangDigital.sPassWord);
				log.info("imap: "+ PopiangDigital.sImap);
				log.info("smtp: "+ PopiangDigital.sSmtp);
				log.info("mail: "+ PopiangDigital.sEmail);
				log.info("pref: "+ PopiangDigital.sPrefix);
				log.info("name: "+ PopiangDigital.sName);
				log.info("font name: "+ PopiangDigital.sFontName);
				log.info("font size: "+ PopiangDigital.iFontSize);
				log.info("mainrep: "+ PopiangDigital.sMainRepo);
				log.info("workrep: "+ PopiangDigital.sWorkRepo);
				log.info("baseiri: "+ PopiangDigital.sBaseIRI);
			} else {
			}

			log.info("prefix: "+ PopiangDigital.sPrefix);
			File own = new File(PopiangDigital.workDir+"/rdf/"+PopiangDigital.sPrefix);
			if(!own.exists()) own.mkdirs();
//			File qry = new File(PopiangDigital.workDir+"/rdf/"+PopiangDigital.sQuery);
//			if(!qry.exists()) qry.mkdirs();
//			log.info("own dir: "+ own.getAbsolutePath()+" : "+ own.exists());

		} catch(Exception z) {
			z.printStackTrace();
		}
	}

	public static void gitInit(Path directory) throws IOException, InterruptedException {
		runCommand(directory, "git", "init");
	}

	public static void gitFetch(Path directory) throws IOException, InterruptedException {
		runCommand(directory, "git", "fetch");
	}

	public static void gitPull(Path directory) throws IOException, InterruptedException {
		runCommand(directory, "git", "pull");
	}

	public static void gitStage(Path directory) throws IOException, InterruptedException {
		runCommand(directory, "git", "add", "-A");
	}

	public static void gitCommit(Path directory, String message) throws IOException, InterruptedException {
		runCommand(directory, "git", "commit", "-m", message);
	}

	public static void gitClone(Path directory, String originUrl) throws IOException, InterruptedException {
		runCommand(directory.getParent(), "git", "clone", originUrl, directory.getFileName().toString());
	}


	public static void gitPush(Path directory) throws IOException, InterruptedException {
		runCommand(directory, "git", "push", "origin", "main");
	}

	public static void gitPushMain0(String url) throws IOException, InterruptedException {
		runGit("git", "config", "pull.rebase", "true");
		runGit("git", "config", "rebase.autoStash", "true");

		runGit("git", "add", ".");
		runGit("git", "commit", "-m", "\"verx\"");

		runGit("git", "branch", "main");
		runGit("git", "checkout", "main");
		runGit("git", "remote", "remove", "upstream");
		runGit("git", "remote", "add", "upstream", url);
		runGit("git", "pull", "upstream", "main");

		runGit("git", "remote", "remove", "origin");
		runGit("git", "remote", "add", "origin", url);
		runGit("git", "push", "origin", "main");
	}

	public static void gitPushMain(String url) throws IOException, InterruptedException {
		runGit("git", "config", "pull.rebase", "true");
		runGit("git", "config", "rebase.autoStash", "true");

		runGit("git", "add", ".");
		runGit("git", "commit", "-m", "\"verx\"");

		runGit("git", "checkout", "main");
		runGit("git", "remote", "remove", "upstream");
		runGit("git", "remote", "add", "upstream", url);
		runGit("git", "pull", "upstream", "main");

		runGit("git", "remote", "remove", "origin");
		runGit("git", "remote", "add", "origin", url);
		runGit("git", "push", "origin", "main");
	}

	public static void gitJoinWork2Main(String mnrepo, String wkrepo, String branch) 
		 throws IOException, InterruptedException {

		runGit("git", "config", "pull.rebase", "true");
		runGit("git", "config", "rebase.autoStash", "true");

log.info("JOIN: pull from "+ branch);
		runGit("git", "branch", branch);
		runGit("git", "checkout", branch);
		runGit("git", "remote", "remove", "upstream");
		runGit("git", "remote", "add", "upstream", wkrepo);
//		runGit("git", "fetch", "upstream", branch);
//		runGit("git", "branch", "-f", branch, "upstream", branch);
		runGit("git", "pull", "upstream", branch);

log.info("JOIN: remove upstream "+ branch);
		runGit("git", "remote", "remove", "upstream");

log.info("JOIN: pull from main : "+mnrepo);
//		runGit("git", "branch", "main");
		runGit("git", "checkout", "main");
		runGit("git", "remote", "remove", "origin");
		runGit("git", "remote", "add", "origin", mnrepo);
		runGit("git", "pull", "origin", "main");

		runGit("git", "rebase", branch);

	}

	public static void gitPushWork0(String url, String own, String prf)
		 throws IOException, InterruptedException {

log.info("PUSH-UPSTREAM: "+ url);
		runGit("git", "config", "pull.rebase", "true");
		runGit("git", "config", "rebase.autoStash", "true");
		runGit("git", "branch", "main");
		runGit("git", "checkout", "main");
		runGit("git", "remote", "remove", "upstream");
		runGit("git", "remote", "add", "upstream", url);
		runGit("git", "pull", "upstream", "main");

log.info("OWN-ORIGIN: "+ own);
		runGit("git", "remote", "remove", "origin");
		runGit("git", "remote", "add", "origin", own);
		runGit("git", "push", "origin", "main");

		runGit("git", "branch", prf);
		runGit("git", "checkout", prf);
		runGit("git", "pull", "origin", prf);
		runGit("git", "push", "origin", prf);
	}

	public static void gitPushWork(String mn, String own, String prf)
		 throws IOException, InterruptedException {

log.info("PUSH-PREFIX: "+ prf);
		runGit("git", "checkout", prf);
		runGit("git", "config", "pull.rebase", "true");
		runGit("git", "config", "rebase.autoStash", "true");
		runGit("git", "add", ".");
		runGit("git", "commit", "-m", "\"verx\"");

log.info("PUSH-UPSTREAM: "+ mn);
		runGit("git", "checkout", "main");
		runGit("git", "remote", "remove", "upstream");
		runGit("git", "remote", "add", "upstream", mn);
		runGit("git", "pull", "upstream", "main");

log.info("OWN-ORIGIN: "+ own);
		runGit("git", "remote", "remove", "origin");
		runGit("git", "remote", "add", "origin", own);
		runGit("git", "push", "origin", "main");

		runGit("git", "checkout", prf);
		runGit("git", "pull", "origin", prf);
		runGit("git", "rebase", "origin/main");
		runGit("git", "push", "origin", prf);
	}

	public static void runGit(String... cmd) {
		try {
			ProcessBuilder pb = new ProcessBuilder().
				command(cmd).directory(PopiangDigital.fWork);
			Map<String,String> env = pb.environment();
			env.put("GIT_SSH_COMMAND", "ssh -o IdentitiesOnly=yes -i "
				+ PopiangDigital.workDir+"/.cfg/id_rsa");
			Process p = pb.start();
			StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");
			StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
			outputGobbler.start();
			errorGobbler.start();
			int exit = p.waitFor();
			errorGobbler.join();
			outputGobbler.join();
		} catch(Exception a) {
			log.error("007", a);
		}
	}

	public static void runCommand(Path directory, String... command) throws IOException, InterruptedException {
		Objects.requireNonNull(directory, "directory");
		if (!Files.exists(directory)) {
			throw new RuntimeException("can't run command in non-existing directory '" + directory + "'");
		}
		ProcessBuilder pb = new ProcessBuilder()
				.command(command)
				.directory(directory.toFile());
		Map<String, String> env = pb.environment();
		//env.put("GIT_SSH_COMMAND", "ssh -o IdentitiesOnly=yes -i id_rsa -F /dev/null");
		env.put("GIT_SSH_COMMAND", "ssh -o IdentitiesOnly=yes -i id_rsa");
		Process p = pb.start();
		StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");
		StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
		outputGobbler.start();
		errorGobbler.start();
		int exit = p.waitFor();
		errorGobbler.join();
		outputGobbler.join();
	}

	private static class StreamGobbler extends Thread {
		private final InputStream is;
		private final String type;
		private StreamGobbler(InputStream is, String type) {
			this.is = is;
			this.type = type;
		}
		@Override
		public void run() {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
				String line;
				while ((line = br.readLine()) != null) {
//					System.out.println(type + "> " + line);
					log.info(type + "> " + line);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public static void generateKeyPair(String email) {
		try {

			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(2048);
			KeyPair keyPair = generator.generateKeyPair();
			RSAPrivateKey priv = (RSAPrivateKey) keyPair.getPrivate();
			RSAPublicKey pub = (RSAPublicKey) keyPair.getPublic();

			Base64.Encoder encoder = Base64.getEncoder();
			String code = "-----BEGIN RSA PRIVATE KEY-----\n";          
			String codenew = encoder.encodeToString(priv.getEncoded());
			//String codenew =Base64.encodeBase64String(priv.getEncoded());
			String myOutput = ""; 
			for (String substring : Splitter.fixedLength(64).split(codenew)) { 
				myOutput += substring + "\n"; 
			}
			code += myOutput.substring(0, myOutput.length() - 1);
			code += "\n-----END RSA PRIVATE KEY-----";                          
//			System.out.println(code);
//			JOptionPane.showMessageDialog(null, code); 
			try {
	            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rw-------");
				FileAttribute<Set<PosixFilePermission>> fileAttributes = 
					PosixFilePermissions.asFileAttribute(permissions);
				PopiangDigital.fPrv.delete();
				Files.createFile(Paths.get(PopiangDigital.fPrv.getAbsolutePath()), fileAttributes);
			} catch(UnsupportedOperationException z) {
				System.out.println("POSIX ERROR");
			}
//			JOptionPane.showMessageDialog(null, PopiangDigital.fPrv.getAbsolutePath()); 
			Files.write(Paths.get(PopiangDigital.fPrv.getAbsolutePath()), code.getBytes());
			PopiangDigital.fPrv.setWritable(false);
 
			RSAPublicKey rsaPublicKey = (RSAPublicKey) pub;
			ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(byteOs);
			dos.writeInt("ssh-rsa".getBytes().length);
			dos.write("ssh-rsa".getBytes());
			dos.writeInt(rsaPublicKey.getPublicExponent().toByteArray().length);
			dos.write(rsaPublicKey.getPublicExponent().toByteArray());
			dos.writeInt(rsaPublicKey.getModulus().toByteArray().length);
			dos.write(rsaPublicKey.getModulus().toByteArray());
			//String publicKeyEncoded = new String( Base64.encodeBase64(byteOs.toByteArray()));
			//Base64.Encoder encoder = Base64.getEncoder();
			String publicKeyEncoded = encoder.encodeToString(byteOs.toByteArray());
            String pubStr = "ssh-rsa " + publicKeyEncoded + " "+email;
			Files.write(Paths.get(PopiangDigital.fPub.getAbsolutePath()), pubStr.getBytes(StandardCharsets.UTF_8));

		} catch(Exception z) {
			z.printStackTrace();
		}
	}

	public static void setKeyPair() {
		try {

			PopiangDigital.fPrv = new File(PopiangDigital.workDir+"/.cfg/"+PopiangDigital.sPrv);
			PopiangDigital.fPub = new File(PopiangDigital.workDir+"/.cfg/"+PopiangDigital.sPub);
			if(PopiangDigital.fPrv.exists()) return;

			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(2048);
			KeyPair keyPair = generator.generateKeyPair();
			RSAPrivateKey priv = (RSAPrivateKey) keyPair.getPrivate();
			RSAPublicKey pub = (RSAPublicKey) keyPair.getPublic();

			Base64.Encoder encoder = Base64.getEncoder();
			String code = "-----BEGIN RSA PRIVATE KEY-----\n";          
			String codenew = encoder.encodeToString(priv.getEncoded());
			//String codenew =Base64.encodeBase64String(priv.getEncoded());
			String myOutput = ""; 
			for (String substring : Splitter.fixedLength(64).split(codenew)) { 
				myOutput += substring + "\n"; 
			}
			code += myOutput.substring(0, myOutput.length() - 1);
			code += "\n-----END RSA PRIVATE KEY-----";                          
			System.out.println(code);
			JOptionPane.showMessageDialog(null, code); 
			try {
	            Set<PosixFilePermission> permissions 
					= PosixFilePermissions.fromString("rw-------");
				FileAttribute<Set<PosixFilePermission>> fileAttributes 
					= PosixFilePermissions.asFileAttribute(permissions);
				Files.createFile(Paths.get(
					PopiangDigital.fPrv.getAbsolutePath()), fileAttributes);
			} catch(UnsupportedOperationException z) {
				System.out.println("POSIX ERROR");
			}
			JOptionPane.showMessageDialog(null, PopiangDigital.fPrv.getAbsolutePath()); 
			Files.write(Paths.get(PopiangDigital.fPrv.getAbsolutePath()), code.getBytes());
			PopiangDigital.fPrv.setWritable(false);
 
			RSAPublicKey rsaPublicKey = (RSAPublicKey) pub;
			ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(byteOs);
			dos.writeInt("ssh-rsa".getBytes().length);
			dos.write("ssh-rsa".getBytes());
			dos.writeInt(rsaPublicKey.getPublicExponent().toByteArray().length);
			dos.write(rsaPublicKey.getPublicExponent().toByteArray());
			dos.writeInt(rsaPublicKey.getModulus().toByteArray().length);
			dos.write(rsaPublicKey.getModulus().toByteArray());
			//String publicKeyEncoded = new String( Base64.encodeBase64(byteOs.toByteArray()));
			//Base64.Encoder encoder = Base64.getEncoder();
			String publicKeyEncoded = encoder.encodeToString(byteOs.toByteArray());
            String pubStr = "ssh-rsa " + publicKeyEncoded + " user";
			Files.write(Paths.get(PopiangDigital.fPub.getAbsolutePath()), pubStr.getBytes(StandardCharsets.UTF_8));

		} catch(Exception z) {
			z.printStackTrace();
		}
	}

	static List<String[]> sparql(Model mo, String sqry) {
		List<String[]> aRet = new ArrayList<>();
		int i1 = sqry.indexOf("SELECT");
		int i2 = sqry.indexOf("WHERE");
		if(i1<0 || i2<=i1) { return aRet; }
		String vars = sqry.substring(i1+6, i2).trim();
		String[] vrs = vars.split(" ");
		if(vrs.length<=0) { return aRet; }
		String[] fds = new String[vrs.length];
		for(int i=0; i<vrs.length; i++) 
			if(!vrs[i].startsWith("?")) return aRet; 
			else fds[i] = vrs[i].substring(1);
		//System.out.println("vars: "+ vars);

		Query qry = QueryFactory.create(sqry);
		QueryExecution qex = QueryExecutionFactory.create(qry, mo);
		//System.out.print("VAR: "); for(String v: fds) System.out.print(" "+v); System.out.println();
		try {
			ResultSet rs = qex.execSelect();
			while( rs.hasNext() ) {
				QuerySolution soln = rs.nextSolution();
				String[] res = new String[fds.length];
				for(int i=0; i<fds.length; i++) {
					try {
						res[i] = soln.get(fds[i]).toString();
						if((i1=res[i].indexOf("^^"))>0) res[i] = res[i].substring(0,i1);
					} catch(Exception x) {}
				}
				aRet.add(res);
			}
		} catch(Exception y) {
			y.printStackTrace();
		}
		return aRet;
	}

	static List<Map<String,String>> sparql0(Model mo, String sqry) {
		List<String[]> aRet = new ArrayList<>();
		List<Map<String,String>> aMap = new ArrayList<>();
		int i1 = sqry.indexOf("SELECT");
		int i2 = sqry.indexOf("WHERE");
		if(i1<0 || i2<=i1) { return aMap; }
		String vars = sqry.substring(i1+6, i2).trim();
		String[] vrs = vars.split(" ");
		if(vrs.length<=0) { return aMap; }
		String[] fds = new String[vrs.length];
		for(int i=0; i<vrs.length; i++) 
			if(!vrs[i].startsWith("?")) return aMap; 
			else fds[i] = vrs[i].substring(1);
		//System.out.println("vars: "+ vars);

		Query qry = QueryFactory.create(sqry);
		QueryExecution qex = QueryExecutionFactory.create(qry, mo);
		//System.out.print("VAR: "); for(String v: fds) System.out.print(" "+v); System.out.println();
		try {
			ResultSet rs = qex.execSelect();
			while( rs.hasNext() ) {
				QuerySolution soln = rs.nextSolution();
				String[] res = new String[fds.length];
				Map<String,String> map = new HashMap<>();
				for(int i=0; i<fds.length; i++) {
					try {
						res[i] = soln.get(fds[i]).toString();
						if((i1=res[i].indexOf("^^"))>0) res[i] = res[i].substring(0,i1);
						map.put(fds[i],res[i]);
					} catch(Exception x) {}
				}
				aRet.add(res);
				aMap.add(map);
			}
		} catch(Exception y) {
			y.printStackTrace();
		}
		return aMap;
	}

	public static void walkRtfTtl() {
		File curDir = PopiangDigital.fWork;
		File rdf = new File(curDir.getAbsolutePath()+"/rdf");
		if(!rdf.exists()) rdf.mkdirs();
		List<File> aRt = new ArrayList<>();
		for(File f : rdf.listFiles()) if(f.isDirectory()) aRt.add(f);
		File[] roots = aRt.toArray(new File[aRt.size()]);
		
		Arrays.sort(roots);
		PopiangDigital.aRdfFold = roots;
		PopiangDigital.hRdfModelInfo = new Hashtable<>();
		readAllModel();
	}
	public static String readRdfModel(File fMod) {
		String fnm = fMod.getName();
		int i1;
		if((i1=fnm.indexOf("."))>0) fnm = fnm.substring(0,i1);
		RdfModelInfo rdfif = PopiangDigital.hRdfModelInfo.get(fnm);
		if(rdfif==null) {
			rdfif = new RdfModelInfo();
			PopiangDigital.hRdfModelInfo.put(fnm, rdfif);
		}
		String r = rdfif.analyze(fMod);
		return r;
	}
	public static void readAllModel() {
		File[] roots = PopiangDigital.aRdfFold;
		for(int i=0; i<roots.length; i++) {
			File[] faRdf = roots[i].listFiles();
			for(int r=0; r<faRdf.length; r++) {
				File fRdf = faRdf[r];
				if(!fRdf.getName().endsWith(".ttl")) continue;
				String rt = readRdfModel(faRdf[r]);
				if(rt!=null) log.info("RDFERR : "+faRdf[r].getName()+" : "+rt);
			}
		}
	}

	public static void runAttend() {
		try {
			String[] cmd = null;
			if(PopiangDigital.bWindows) {
				cmd = new String[] {PopiangDigital.workDir+"/bin/windows/gorecv.exe",""};
			} else if(PopiangDigital.bMacos) {
				cmd = new String[] {PopiangDigital.workDir+"/bin/darwin/gorecv",""};
			} else if(PopiangDigital.bLinux) {
				cmd = new String[] {PopiangDigital.workDir+"/bin/linux/gorecv",""};
			} else {
				log.info("UNKNOW OS PLATFORM");
				return;
			}

			while(PopiangDigital.bAttend) {

				File fID = new File(PopiangDigital.workDir+"/.cfg/ATTENDID");
				Files.write(Paths.get(fID.getAbsolutePath()), cmd[1].getBytes());

				cmd[1] = PopiangDigital.sAttendID;
				log.info("COMMAND LINE: "+ cmd[0]);
				ProcessBuilder pb = new ProcessBuilder().
					command(cmd).directory(PopiangDigital.fWork);
				Map<String,String> env = pb.environment();
				env.put("ATTENDID", PopiangDigital.sAttendID);
				Process p = pb.start();
				StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");
				StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
				outputGobbler.start();
				errorGobbler.start();
				log.info("JAVA START GO RECV...");
				int exit = p.waitFor();
				log.info("JAVA END GO RECV...");
				errorGobbler.join();
				outputGobbler.join();
			}

		} catch(Exception a) {
			a.printStackTrace();
		}
	}

	public static Model allModel = null;
	public static String[][] saPref;

    public static List<String[]> query(String sel) {
            readAllRdfModel();
			return query(allModel, sel, "");
	}

	public static List<String[]> query(Model mod, String sel, String op) {
        try {

            sel = sel.replace("(*)","/rdf:rest*/rdf:first");

            Map<String,String> tokens = new HashMap<String,String>();
            for(String[] wds : saPref) {
//				log.info(wds[0]+": "+wds[1]);
				tokens.put(wds[0]+":",wds[1]);
			}
            tokens.put("rdf:", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

            String sPattPrf = "("+StringUtils.join(tokens.keySet(),"|")+")";
//log.info("patt: "+ sPattPrf);

            Pattern patPrf = Pattern.compile(sPattPrf);
            Pattern patVar = Pattern.compile("(\\?[A-Za-z0-9]+)");

            Matcher varMat = patVar.matcher(sel);
            List<String> aVar = new ArrayList<>();
            while(varMat.find()) { aVar.add(varMat.group(1)); }

            Matcher prfMat = patPrf.matcher(sel);
            List<String> aPrf = new ArrayList<>();
            while(prfMat.find()) { aPrf.add(prfMat.group(1)); }
            List<String> var2 = aVar.stream()
                .distinct().collect(Collectors.toList());
            List<String> prf2 = aPrf.stream()
                .distinct().collect(Collectors.toList());

            StringBuilder sb = new StringBuilder();
            for(String prf : prf2) {
                sb.append(" PREFIX "+ prf + " <"+tokens.get(prf)+"> ");
            }
            sb.append(" SELECT ");
            for(String var : var2) {
                sb.append(" "+var);
            }
            sb.append(" WHERE { ");
            sb.append(sel);
            sb.append(" } ");
			sb.append(" "+op+" ");

//log.info("QUERY:"+ sb.toString());
            int l = PopiangDigital.sBaseIRI.length();
            List<String[]> res = PopiangUtil.sparql(mod, sb.toString());
            for(String[] r : res) {
                for(int i=0; i<r.length; i++) {
                    if(r[i]!=null && r[i].startsWith(PopiangDigital.sBaseIRI)) {
                        r[i] = r[i].substring(l+1).replace("#",":");
					}
                }
            }
            return res;

        } catch(Exception z) {
            log.error("001",z);
        }
        return null;
    }

    public static List<Map<String,String>> spQry(String sel) {
            readAllRdfModel();
			return spQry(allModel, sel, "");
	}

	public static List<Map<String,String>> spQry(Model mod, String sel, String op) {
		String sb0 = "";
        try {

            sel = sel.replace("(*)","/rdf:rest*/rdf:first");

//			getAllPref();
//log.info("saPref: "+ saPref.length);
            Map<String,String> tokens = new HashMap<String,String>();
            for(String[] wds : saPref) tokens.put(wds[0]+":",wds[1]);
            tokens.put("rdf:", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

            String sPattPrf = "("+StringUtils.join(tokens.keySet(),"|")+")";
//log.info("patt: "+ sPattPrf);

            Pattern patPrf = Pattern.compile(sPattPrf);
            Pattern patVar = Pattern.compile("(\\?[A-Za-z0-9]+)");

            Matcher varMat = patVar.matcher(sel);
            List<String> aVar = new ArrayList<>();
            while(varMat.find()) { aVar.add(varMat.group(1)); }

            Matcher prfMat = patPrf.matcher(sel);
            List<String> aPrf = new ArrayList<>();
            while(prfMat.find()) { aPrf.add(prfMat.group(1)); }
            List<String> var2 = aVar.stream()
                .distinct().collect(Collectors.toList());
            List<String> prf2 = aPrf.stream()
                .distinct().collect(Collectors.toList());

            StringBuilder sb = new StringBuilder();
            for(String prf : prf2) {
                sb.append(" PREFIX "+ prf + " <"+tokens.get(prf)+"> ");
            }
            sb.append(" SELECT ");
            for(String var : var2) {
                sb.append(" "+var);
            }
            sb.append(" WHERE { ");
            sb.append(sel);
            sb.append(" } ");
			sb.append(" "+op+" ");

//log.info("QUERYx\n"+sb.toString());

			sb0 = sb.toString();
            int l = PopiangDigital.sBaseIRI.length();
            List<String[]> res = PopiangUtil.sparql(mod, sb.toString());
//log.info("SPQRY RES: "+ res.size());
            for(String[] r : res) {
                for(int i=0; i<r.length; i++) {
                    if(r[i]!=null && r[i].startsWith(PopiangDigital.sBaseIRI)) {
                        r[i] = r[i].substring(l+1).replace("#",":");
					}
                }
            }
			List<Map<String,String>> mRes = new ArrayList<>();
			for(String[] r : res) {
				Map<String,String> map = new HashMap<>();
				for(int i=0; i<var2.size(); i++) {
					String a = r[i];
					if(a!=null) {
						a = a.replaceAll("^\\s+", "");
						a = a.replaceAll("\\s+$", "");
					}
					map.put(var2.get(i), a);
				}
				mRes.add(map);
			}
            return mRes;

        } catch(Exception z) {
System.out.println("qry: "+ sb0);
            log.error("001",z);
        }
        return null;
    }

    public static void readAllRdfModel() {
        try {
//log.info("BASE: "+ PopiangDigital.sBaseIRI);
            Enumeration<String> ePrf = PopiangDigital.hRdfModelInfo.keys();
            allModel = ModelFactory.createDefaultModel();
            List<String[]> sapref = new ArrayList<>();
            while(ePrf.hasMoreElements()) {
                String key = ePrf.nextElement();
                RdfModelInfo rmi = PopiangDigital.hRdfModelInfo.get(key);
//				log.info("rmi: "+ rmi.fRdf);
                sapref.add(new String[] {rmi.sPrefix, rmi.sBaseIRI});
                allModel.add(rmi.model);
            }    
            saPref = new String[sapref.size()][];
            saPref = sapref.toArray(saPref);
        } catch(Exception z) { 
            log.error("002",z);
        }    
    }    

    public static void getAllPref() {
        try {
log.info("BASE: "+ PopiangDigital.sBaseIRI);
            Enumeration<String> ePrf = PopiangDigital.hRdfModelInfo.keys();
            allModel = ModelFactory.createDefaultModel();
            List<String[]> sapref = new ArrayList<>();
            while(ePrf.hasMoreElements()) {
                String key = ePrf.nextElement();
                RdfModelInfo rmi = PopiangDigital.hRdfModelInfo.get(key);
//				log.info("rmi: "+ rmi.fRdf);
                sapref.add(new String[] {rmi.sPrefix, rmi.sBaseIRI});
            }    
            saPref = new String[sapref.size()][];
            saPref = sapref.toArray(saPref);
        } catch(Exception z) { 
            log.error("002",z);
        }    
    }

    public static void readAllRdfModel0() {
        try {
log.info("BASE: "+ PopiangDigital.sBaseIRI);
            Enumeration<String> ePrf = PopiangDigital.hRdfModelInfo.keys();
            allModel = ModelFactory.createDefaultModel();
            List<String[]> sapref = new ArrayList<>();
            while(ePrf.hasMoreElements()) {
                String key = ePrf.nextElement();
                RdfModelInfo rmi = PopiangDigital.hRdfModelInfo.get(key);
                sapref.add(new String[] {rmi.sPrefix, rmi.sBaseIRI});
            }    
            saPref = new String[sapref.size()][];
            saPref = sapref.toArray(saPref);
        } catch(Exception z) { 
            log.error("003",z);
        }    
    }    

	public static void regWithEmailById(String subj, String from
						, Hashtable<String,String> hsProp
                        , Hashtable<String,List<String>> hsText
                        , Hashtable<String,List<byte[]>> hbStrm) {

		List<String> aReply = hsText.get("RTXT");

		log.info("REGISTER WITH EMAIL BY ID: "+subj);
		int i1;
		if((i1=subj.indexOf(":"))<0) {
			aReply.add("โปรดใส่เลขประจำตัวหลัง:");
			return;
		}
		String id = subj.substring(i1+1);
		List<String> aPdfReply = hsText.get("RPDF");
		List<String> aRdfReply = hsText.get("RRDF");
		List<String> aZipReply = hsText.get("RZIP");
		List<String> aJpgReply = hsText.get("RJPG");
		List<byte[]> baaPdf = hbStrm.get("PDF");
		String eml = from.toLowerCase();
        
        String spq = ""
            + " ?a vp:คือ  vo:เจ้าหน้าที่ . "
            + " ?a vp:EmpCard  '"+ id +"' . "
			+ " ?a vp:ThFName ?b . "
			+ " ?a vp:ThLName ?c . "
        ;
		log.info("ID SPQ: "+ spq);
        List<String[]> vals = query(spq);
		log.info("ID RES: "+ vals);

		List<String> aText = hsText.get("TXT");
		log.info("TEXT: "+ aText.size());
		log.info("QUERY: "+ spq);

		String attID = PopiangDigital.sAttendID;

		if(vals.size()==1) {
			String[] wds = vals.get(0);
			String rdfID = wds[0];
			String fname = wds[1];
			String lname = wds[2];
			rdfID = rdfID.replace("#",":");
			String msg = ""
				+ "ขอบคุณสำหรับการลงทะเบียน\n"
				+ "คุณ "+fname+" "+lname+"\n"
				+ "https://dip.popiang.com/attend/"+attID+"/"+rdfID+"\n"
				+ "โปรดคลิกเพื่อเข้างาน\n"
			;
			aReply.add(msg);
		} else if(vals.size()==0) {
			String msg = ""
				+ "ไม่พบอีเมล์ของท่านในฐานข้อมูล\n"
				+ "โปรดติดต่อเจ้าหน้าที่\n"
			;
			aReply.add(msg);
		} else {
			String msg = ""
				+ "อาจมีความผิดพลาดในการลงทะเบียนของท่าน\n"
				+ "โปรดติดต่อเจ้าหน้าที่\n"
			;
			aReply.add(msg);
		}

	}

	public static void saveRegist(String eml, String sbj, String rdfid) {
		try {
			Calendar cal = Calendar.getInstance();
			Date date = cal.getTime();
			SimpleDateFormat date8 = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat datefm = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat timefm = new SimpleDateFormat("HH:mm:ss");
			SimpleDateFormat stmpfm = new SimpleDateFormat("HHmmss");
			String today = date8.format(date);
			String sdate = datefm.format(date);
			String stime = timefm.format(date);
			String sstmp = stmpfm.format(date);
			File fReg = new File(PopiangDigital.workDir+"/rdf/rg/rg"+today+".ttl");
			log.info("TODAY: "+ today);
			log.info("FILE: "+ fReg.getAbsolutePath());
			log.info("EMAIL REG: "+eml);
			log.info("SUBJ REG: "+sbj);
			File fDir = fReg.getParentFile();
			log.info("FILE: "+ fDir.getAbsolutePath());
			if(!fDir.exists()) fDir.mkdirs();

			if(!fReg.exists()) {
				String pref = ""
				+ "@prefix vp: <http://ipthailand.go.th/rdf/itdep/voc-pred#> .\n"
				+ "@prefix vo: <http://ipthailand.go.th/rdf/itdep/voc-obj#> .\n"
				+ "@prefix com01: <http://ipthailand.go.th/rdf/itdep/com01#> .\n"
				+ "@prefix rg"+today+": <http://ipthailand.go.th/rdf/itdep/rg"+today+"#> .\n\n";

				Files.write(Paths.get(fReg.getAbsolutePath()), pref.getBytes());
			}
			OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(fReg, true), "UTF-8");
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			pw.println("rg"+today+":"+sstmp+"   vp:คือ    vo:ลงทะเบียน  ;");
			pw.println("                    vp:ใคร     "+rdfid+" ;");
			pw.println("                    vp:วันที่     '"+sdate+"' ;");
			pw.println("                    vp:เวลา     '"+stime+"' ;");
			pw.println("                    vp:อีเมล์     '"+eml+"' ;");
			pw.println("                    vp:หัวเรื่อง     '"+sbj+"' ");
			pw.println(".");

			pw.close();
		} catch(Exception z) {
			log.error("เกิดข้อผิดพลากขณะลงทะเบียน", z);
		}
	}
	
	public static void regWithEmailByUser(String subj, String from
						, Hashtable<String,String> hsProp
                        , Hashtable<String,List<String>> hsText
                        , Hashtable<String,List<byte[]>> hbStrm) {

		List<String> aReply = hsText.get("RTXT");

		log.info("REGISTER WITH EMAIL BY ID: "+subj);
		int i1;
		if((i1=subj.indexOf(":"))<0) {
			aReply.add("โปรดใส่เลขประจำตัวหลัง:");
			return;
		}
		
		String user = subj.substring(i1+1).toLowerCase();
		user = user.trim();
		user = user.replace("<","").replace(">","");
		String eml = from.toLowerCase();
		
		List<String> aPdfReply = hsText.get("RPDF");
		List<String> aRdfReply = hsText.get("RRDF");
		List<String> aZipReply = hsText.get("RZIP");
		List<String> aJpgReply = hsText.get("RJPG");
		List<byte[]> baaPdf = hbStrm.get("PDF");
		
        
        String spq = ""
            + " ?a vp:คือ  vo:เจ้าหน้าที่ . "
            + " ?a vp:UserName  '"+ user +"' . "
			+ " ?a vp:ThFName ?b . "
			+ " ?a vp:ThLName ?c . "
        ;
		log.info("ID SPQ: "+ spq);
        List<String[]> vals = query(spq);
		log.info("ID RES: "+ vals);

		List<String> aText = hsText.get("TXT");
		log.info("TEXT: "+ aText.size());
		log.info("QUERY: "+ spq);

		String attID = PopiangDigital.sAttendID;

		if(vals.size()==1) {
			String[] wds = vals.get(0);
			String rdfID = wds[0];
			String fname = wds[1];
			String lname = wds[2];
			rdfID = rdfID.replace("#",":");
			String msg = ""
				+ "ยินดีต้อนรับคุณ  "+fname+" "+lname+"\n"
				+ "เข้าสู่ระบบบันทึกเวลาออนไลน์ของกรมทรัพย์สินทางปัญญา\n"
				+ "โปรดคลิกลิ้งเพื่อยืนยันการลงทะเบียน\n\n"
				+ "https://dip.popiang.com/attend/"+attID+"/"+rdfID+"/"+fname+"/"+lname+"\n"
			;
			aReply.add(msg);
			saveRegist(eml, subj, rdfID);

		} else if(vals.size()==0) {
			String msg = ""
				+ "ไม่พบ user ของท่านในฐานข้อมูล\n"
				+ "โปรดติดต่อเจ้าหน้าที่\n"
			;
			aReply.add(msg);
		} else {
			String msg = ""
				+ "อาจมีความผิดพลาดในการลงทะเบียนของท่าน\n"
				+ "โปรดติดต่อเจ้าหน้าที่\n"
			;
			aReply.add(msg);
		}

	}

	public static void regWithEmail(String subj, String from, Hashtable<String,String> hsProp
                        , Hashtable<String,List<String>> hsText
                        , Hashtable<String,List<byte[]>> hbStrm) {

		log.info("REGISTER WITH EMAIL");
		List<String> aReply = hsText.get("RTXT");
		List<String> aPdfReply = hsText.get("RPDF");
		List<String> aRdfReply = hsText.get("RRDF");
		List<String> aZipReply = hsText.get("RZIP");
		List<String> aJpgReply = hsText.get("RJPG");
		List<byte[]> baaPdf = hbStrm.get("PDF");
		String eml = from.toLowerCase();
        
        String spq = ""
            + " ?a vp:คือ  vo:เจ้าหน้าที่ . "
            + " ?a vp:EMail  '"+ eml +"' . "
        ;
        List<String[]> vals = query(spq);

		List<String> aText = hsText.get("TXT");
		log.info("TEXT: "+ aText.size());
		log.info("QUERY: "+ spq);
		log.info("RESNO: "+ vals.size());

		String attID = PopiangDigital.sAttendID;

		if(vals.size()==1) {
			String rdfID = vals.get(0)[0];
			rdfID = rdfID.replace("#",":");
			String msg = ""
				+ "https://dip.popiang.com/attend/"+attID+"/"+rdfID+"\n"
				+ "โปรดคลิกเพื่อเข้างาน\n"
			;
			aReply.add(msg);
		} else if(vals.size()==0) {
			String msg = ""
				+ "ไม่พบอีเมล์ของท่านในฐานข้อมูล\n"
				+ "โปรดติดต่อเจ้าหน้าที่\n"
			;
			aReply.add(msg);
		} else {
			String msg = ""
				+ "อาจมีความผิดพลาดในการลงทะเบียนของท่าน\n"
				+ "โปรดติดต่อเจ้าหน้าที่\n"
			;
			aReply.add(msg);
		}

	}

	public static String makeStaffList() {
		File fTxt = new File(PopiangDigital.workDir+"/res/dip-emp.txt");
		try {
			log.info("EMP:"+fTxt.getAbsolutePath()+" : "+ fTxt.exists());
			BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(fTxt)));
			String line;
			int i=0;
			
			String tx = "";

			List<String> srvs = new ArrayList<>();
			while((line=br.readLine())!=null) {
				String[] wds = line.split("\t");
				if(wds.length<10) continue;
				String empid = wds[0];
				String fngid = wds[1];
				String title = wds[2];
				String thfst = wds[3];
				String thlst = wds[4];
				String enfst = wds[5];
				String enlst = wds[6];
				String cid = wds[7];
				String email = wds[8];
				String user = wds[9];
				cid = cid.replace(" ","").trim();
				email = email.toLowerCase();
				String emsrv = email.substring(email.indexOf("@")+1);

				i++;
				tx += "com01:"+i+"   vp:คือ   vo:เจ้าหน้าที่ ; \n";
				tx += "              vp:EmpCode  '"+empid+"' ;\n";
				tx += "              vp:FingerPrintID '"+fngid+"' ;\n";
				tx += "              vp:TitleID  '"+title+"' ;\n";
				tx += "              vp:ThFName  '"+thfst+"' ;\n";
				tx += "              vp:ThLName  '"+thlst+"' ;\n";
				tx += "              vp:EnFName  '"+enfst+"' ;\n";
				tx += "              vp:EnLName  '"+enlst+"' ;\n";
				tx += "              vp:EmpCard  '"+cid+"' ;\n";
				tx += "              vp:EMail    '"+email+"' ;\n";
				tx += "              vp:UserName '"+user+"' .\n\n";
				
				System.out.println(i+": "+ cid+"   name: "+ thfst+" "+thlst);
				srvs.add(emsrv);
			}
			List<String> srv2 = srvs.stream().sorted().collect(Collectors.toList());     
			Map<String, Long> map = new HashMap<>();
			srvs.forEach(e -> map.put(e, map.getOrDefault(e, 0L) + 1L));
			map.entrySet().stream().forEach(e-> System.out.println(e));
			return tx;
		} catch(Exception z) {
		}
		return null;
	}

	public static void attendReset() {
		try {
			File fAtt = new File(PopiangDigital.workDir+"/.cfg/ATTENDID");
			boolean a = fAtt.delete();
			log.info("ATTEND RESET: "+ a);
		} catch(Exception z) {
			log.error("004", z);
		}
	}

	public static void attendReport(File f) {
		try {
			log.info("ATTEND REPORT");
            File tmp = new File(PopiangDigital.workDir+"/res/template-cover-toc1.docx");
            File out = new File(PopiangDigital.workDir+"/.out/doc/repo2.docx");
            File par = out.getParentFile();
            if(!par.exists()) par.mkdirs();

			log.info("ATTEND REPORT 2");

            XWPFDocument doc = new XWPFDocument(new FileInputStream(tmp));
//            doc.removeBodyElement(0);

			readAllRdfModel();
			Model mo = ModelFactory.createDefaultModel();
			mo.read(new FileInputStream(f), null, "TTL");
			String spq = ""
				+ " ?a vp:คือ ?b . "
				+ " ?a vp:ใคร ?c . "
				+ " ?a vp:วันที่ ?d . "
				+ " ?a vp:เวลา ?e . "
				+ " ?a vp:พิกัด ?f  . "
				+ " ?a vp:ภาพ ?g  . "
			;

			List<String[]> vals = query(mo, spq, " ORDER BY ?e ");
			for(String[] val : vals) {
				String spq2 = ""
					+ " "+ val[2] + " vp:FingerPrintID ?a . "
					+ " "+ val[2] + " vp:ThFName ?b . "
					+ " "+ val[2] + " vp:ThLName ?c . "
				;
				List<String[]> subs = query(allModel, spq2, "");
				if(subs.size()!=1) continue;
System.out.println("ROW: "+ val[0]+" ev:"+val[1]);
				String[] sub = subs.get(0);
				String rdf = val[0];
				String evt = val[1];
				String who = val[2]; // rdfid
				String day = val[3];
				String tim = val[4];
				String gps = val[5];
				String pic = val[6];
				String fng = sub[0];
				String nam = sub[1]+" "+sub[2];
System.out.println(rdf+" "+evt+" "+who+" "+day+" "+pic);
				pic = pic.substring(1);
				File fImg = new File(PopiangDigital.workDir+"/"+pic);

				XWPFParagraph pp;
				XWPFRun rr;

				pp = doc.createParagraph();
				pp.setStyle("Normal");
				rr = pp.createRun();
				rr.setText("รหัสเหตุการ "+rdf);
				rr.addBreak();
				rr.setText("เหุตการ "+evt);
				rr.addBreak();
				rr.setText("เวลา "+ tim);
				rr.addBreak();
				rr.setText("ตำแหน่งพิกัด "+ gps);
				rr.addBreak();
				rr.setText("ไอดีลายนิ้วมือ "+fng);
				rr.addBreak();
				rr.setText("ชื่อ "+nam);
				rr.addBreak();
				rr.addPicture(new FileInputStream(fImg)
					, XWPFDocument.PICTURE_TYPE_JPEG, fImg+""
					, Units.toEMU(400), Units.toEMU(250));
				rr.addBreak();
				

			}

            log.info("temp file: "+ tmp);
            log.info("..."+tmp.exists());

            try (FileOutputStream fos = new FileOutputStream(out)) {
                doc.write(fos);
            }    

		} catch(Exception z) {
			log.error("005", z);
		}
	}

	public static void makeScanFile(File f) {
		try {

			String date = "000000";
			System.out.println("==========================================================");
			String fn = f.getName();
			fn = fn.substring(0,fn.indexOf(".ttl"))+".txt";
			
			System.out.println("file:"+fn);
			
			File fScan = new File(".out/"+fn);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fScan)));
			
			StringBuffer scanfile = new StringBuffer();
			
			readAllRdfModel();
			Model mo = ModelFactory.createDefaultModel();
			mo.read(new FileInputStream(f), null, "TTL");
			String spq = ""
				+ " ?a vp:คือ ?b . "
				+ " ?a vp:ใคร ?c . "
				+ " ?a vp:วันที่ ?d . "
				+ " ?a vp:เวลา ?e . "
				+ " ?a vp:พิกัด ?f  . "
				+ " ?a vp:ภาพ ?g  . "
			;

			List<String[]> vals = query(mo, spq, " ORDER BY ?e ");
			for(String[] val : vals) {
				String spq2 = ""
					+ " "+ val[2] + " vp:FingerPrintID ?a . "
					+ " "+ val[2] + " vp:ThFName ?b . "
					+ " "+ val[2] + " vp:ThLName ?c . "
				;
				date = val[3];
				date = date.replace("-","").substring(2);
				String time = val[4];
				time = time.replace(":","");
				if(date.length()==6) {
					date = date.substring(4,6)+date.substring(2,4)+date.substring(0,2);
				}
				if(time.length()==6) {
					time = time.substring(0,4);
				}
				
				List<String[]> subs = query(allModel, spq2, "");
				/*
				System.out.print(
					"ID:"+val[0]
					+" : เหตุการ "+ val[1]
					+" RDFID "+ val[2]
					+" DATE:"+val[3]
					+" TIME:"+val[4]
					+" GPS: "+val[5]
				);
				*/
				String fingerID = "";
				for(String[] sub : subs) {
					fingerID = sub[0];
					/*
					System.out.print(" : "+sub[0]+" : "+sub[1] + " : "+ sub[2]);
					*/
				}
				String line = fingerID+" "+date+" "+time;
				System.out.println("LINE:"+line);
				bw.write(line);
				bw.newLine();
				scanfile.append(line+"\n");
			}
			bw.close();
		} catch(Exception z) {
			log.error("006", z);
		}
	}

    public static String getClipBoard() {
        String clp = "";
        Transferable cnt = Toolkit.getDefaultToolkit()
            .getSystemClipboard().getContents(null);
        if (cnt != null && cnt.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                clp = (String)cnt.getTransferData(DataFlavor.stringFlavor);
            } catch(Exception z) {}
        }
		return clp;
    }

    public static String getLatlongCB() {
        String clp = getClipBoard();
        String[] wds = clp.replace(" ","").split(",");
        double lat = -1, lon = -1;
        try { lat = Double.parseDouble(wds[0]); } catch(Exception z) {}
        try { lon = Double.parseDouble(wds[1]); } catch(Exception z) {}
        if(lat>0 && lon>0) {
            clp = String.format("%.5f, %.5f", lat, lon);
        } else {
			clp = "";
		}
        return clp;
    }

    public static String latlon2utm(String clp) {
        String utm = "";
        String[] wds = clp.replace(" ","").split(",");
        double lat = -1, lon = -1;
        try { lat = Double.parseDouble(wds[0]); } catch(Exception z) {}
        try { lon = Double.parseDouble(wds[1]); } catch(Exception z) {}
        if(lat>0 && lon>0) {
            utm = latlon2utm(lat,lon);
        }
        return utm;
    }

    public static String latlon2utm(double lat, double lon) {
        String rt = "";
        double E5 = lat;
        double F5 = lon;
        double C12 = 6378137;
        double C13 = 6356752.31424518;
        double C15 = Math.sqrt(C12*C12 - C13*C13) / C12;
        double C16 = Math.sqrt(C12*C12 - C13*C13) / C13;
        double C17 = C16 * C16;
        double C18 = Math.pow(C12,2)/C13;
        char C21 = 'N';
        double G5 = F5*Math.PI/180.0;
        double H5 = E5*Math.PI/180.0;
        double I5 = 47;
        double J5 = 6*I5-183;
        double K5 = G5-((J5*Math.PI)/180);
        double N5 = Math.atan((Math.tan(H5))/Math.cos(K5))-H5;
        double L5 = Math.cos(H5)*Math.sin(K5);
        double M5 = 0.5*Math.log((1+L5)/(1-L5));
        double O5 = C18/Math.pow(1+C17*Math.pow(Math.cos(H5),2.0),0.5)*0.9996;
        double P5 = (C17/2.0)*Math.pow(M5,2)*Math.pow(Math.cos(H5),2);
        double V5 = (3.0/4.0)*C17;
        double Q5 = Math.sin(2.0*H5);
        double S5 = H5+(Q5/2.0);
        double R5 = Q5*Math.pow(Math.cos(H5),2);
        double W5 = (5.0/3.0)*Math.pow(V5,2);
        double T5 = ((3.0*S5)+R5)/4.0;
        double X5 = (35/27)*Math.pow(V5,3.0);
        double U5 = 5*T5+R5*Math.pow(Math.cos(H5),2)/3.0;
        double Y5 = 0.9996*C18*(H5-(V5*S5)+(W5*T5)-(X5*U5));
        double AD5 = N5*O5*(1+P5)+Y5;
        double AC5 = M5*O5*(1+P5/3)+500000;
        rt = String.format("%.2f, %.2f", AC5, AD5);
        return rt;
    }

    public static List<String> getVO(String vo) {
        List<String> aCam0 = new ArrayList<>();
        File fvo = new File(PopiangDigital.workDir+"/rdf/etc/vo.ttl");
        try (BufferedReader br = new BufferedReader(new FileReader(fvo))) {
            String line;
            boolean bCam = false;
            while((line=br.readLine())!=null) {
                if(line.startsWith(vo)) {
                    bCam = true;
                } else if(bCam) {
                    Pattern pat = Pattern.compile("(vo:[0-9A-Za-z]+)");
                    Matcher mat = pat.matcher(line);
                    if(mat.find()) {
                        int i1 = mat.start();
                        int i2 = mat.end();
                        String vox = line.substring(i1,i2);
                        String id = ""; 
                        if((id=PopiangDigital.getObject(vox))!=null) {
                            line = line.replace(vox, id);
                        } else {
                            line = line.replace(vox, "''");
                        }
                    }
                    aCam0.add(line);
                    if(line.endsWith(".")) {
                        break;
                    }
                }
            }
        } catch (IOException ex) {
        }
		return aCam0;
	}

	static SimpleDateFormat datefm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static String timestamp() {
		return datefm.format(Calendar.getInstance().getTime());
	}

	static String qprf = "@prefix qr: <http://popiang.com/rdf/qr#> .\n"
		+"@prefix vo: <http://popiang.com/rdf/vo#> .\n"
		+"@prefix vp: <http://popiang.com/rdf/vp#> .\n"
		+"qr:1  a   vo:Query;\n"
		+"vp:query  '''\n";
	static String qsuf = "''' .\n";

	public static QueryExecute query0(String qr) {
		String qr0 = qprf + qr + qsuf;
//System.out.println("qr="+qr0);
		QueryExecute q0 = new QueryExecute();
		Map<String,String> m0 = q0.init(qr0);
//System.out.println("mp="+m0);
		try {
			q0.query(m0);
		} catch(Exception z) {
System.out.println("Q0: "+ qr0);
			z.printStackTrace();
		}
		return q0;
    }

/*
	public static double[] txt2dbl(String s) {
		String[] wds = s.split(",");
		double[] r = new double[wds.length];
		for(int i=0; i<r.length; i++) 
			try { r[i] = Double.parseDouble(wds[i].trim()); } catch(Exception z) {r[i]=-1;}
		return r;
	}

	public static int[] txt2int(String s) {
		String[] wds = s.split(",");
		int[] r = new int[wds.length];
		for(int i=0; i<r.length; i++) 
			try { r[i] = Integer.parseInt(wds[i].trim()); } catch(Exception z) {r[i]=-1;}
		return r;
	}
*/
	public static String readRdf(String rdfn, String id1, String id2) {
		if(rdfn.matches("[a-z]+[0-9A-Z][0-9A-Z]\\.qry")) {
			try {
			String fn = rdfn.substring(0,rdfn.length()-4);
			String dn = fn.substring(0,fn.length()-2);
			String f1 = PopiangDigital.workDir+"/rdf/"+dn+"/"+rdfn;
			String c1 = new String ( Files.readAllBytes( Paths.get(f1) ) );
			c1 = c1.replace(id1,id2);
			return c1;
			} catch(Exception z) {}
		}
		return null;
	}

	public static QueryExecute queryId(String qrf, String id0, String id1) {
		QueryExecute qr = new QueryExecute();
		Map<String,String> m = qr.init(readRdf(qrf,id0,id1));
		qr.query(m);
		return qr;
	}

	public static double[] txt2dbl(String s) {
		String[] wds = s.split(",");
		double[] r = new double[wds.length];
		for(int i=0; i<r.length; i++)
			try { r[i] = Double.parseDouble(wds[i].trim()); } catch(Exception z) {r[i]=-1;}
		return r;
    }

	public static int[] txt2int(String s) {
		String[] wds = s.split(",");
		int[] r = new int[wds.length];
		for(int i=0; i<r.length; i++)
			try { r[i] = Integer.parseInt(wds[i].trim()); } catch(Exception z) {r[i]=-1;}
		return r;
	}

	public static void drawString(Graphics2D g2, String tx, int x, int y) {
		FontMetrics fm = g2.getFontMetrics();
		int x0 = x - fm.stringWidth(tx) / 2;
		int y0 = y + fm.getAscent() - (fm.getAscent() + fm.getDescent()) / 2;
		g2.drawString(tx, x0, y0);
	}

	public static void drawString(Graphics2D g2, String tx, int x, int y, int s) {
		FontMetrics fm = g2.getFontMetrics();
		int x0 = x - fm.stringWidth(tx) / 2;
		int y0 = y + fm.getAscent() - (fm.getAscent() + fm.getDescent()) / 2;
		y0 += s * (fm.getAscent() + fm.getDescent());
		g2.drawString(tx, x0, y0);
	}

public static class ThaiWord {
	String sr_i = "[\\u0E34\\u0E35\\u0E36\\u0E37]";
	String sr_w = "[\\u0E48\\u0E49\\u0E4A\\u0E4B]";
	String sr_u = "[\\u0E38\\u0E39\\u0E3A]";
	String sr_r	= "[\\u0E4C\\u0E4E]";
	String sr_e	= "[\\u0E40\\u0E41]";
	String sr_o = "[\\u0E42\\u0E43\\u0E44]";
	String sr_a = "\\u0E30";
	String sr_h = "\\u0E31";
	String sr_aa = "\\u0E32";
	String sr_m = "\\u0E4D";
	String sr_mm = "\\u0E33";

	String cns = "[ก-ฮ]";
	String cnoh = "[อห]{0,1}[ก-ฮ]";
	String cnoy = "[อย]{0,1}[ก-ฮ]";
	String cnol = "[ก-สฬ]";
	
	String sr_iw = "("+sr_i+"|"+sr_w+")";
	String sym = "[A-Za-z0-9\\!\\\"\\#\\$\\%\\&\\'\\(\\)\\*\\+\\,\\-\\.\\/\\:\\;\\<\\>\\=\\?\\@\\[\\]\\{\\}\\\\\\^\\_\\`\\~]+";

	String tx1, tx0, txp, canBeAddLater;
	String[] aWord;

	String[] resv = {
		"คลี่" ,"คลาย" ,"สถานะ" ,"สถานการณ์","คุ้มครอง","ระบบ","ฮาร์ดแวร์","ใหญ่"
		,"มาตรการ" ,"พ.ศ." ,"ปฏิบัติ" ,"วัน","ทรัพยากร","อย่าง","วงจร","บุคคล"
		,"ซึ่ง" ,"ปัญญาประดิษฐ์" ,"ซอฟต์แวร์","กล้อง","ออกแบบ","ประมวล","ออกแบบ"
		,"วัตถุประสงค์","เจตนารมณ์","ของ","ประ","โครง","สร้าง","บริหาร","ตรวจ","พฤติ"
		,"ปลอด","อนาคต","มกราคม","กุมภาพันธ์","มีนาคม","เมษายน","พฤษภาคม","มิถุนายน"
		,"กรกฎาคม","สิงหาคม","กันยายน","ตุลาคม","พฤศจิกายน","ธันวาคม",
	};
	String[][] pats = {
		{"("+sr_e+cns+"{1,2}"+sr_w+"*"+sr_aa+sr_a+")","n"},
		{"("+sr_e+cns+"{1,2}"+sr_w+"*"+sr_aa+")","n"},
		{"("+cns+sr_u+sr_w+"*)", "y"},
		{"("+cns+sr_w+"*"+sr_u+")", "y"},
		{"("+cns+"{1,1}"+sr_r+")", "n"},
		{"("+cns+sr_w+"{0,1}"+sr_aa+")", "y"},
		{"("+sr_e+cns+"\\u0E47"+sr_w+"*"+cns+")", "y"},
		{"("+sr_e+cns+sr_i+sr_w+"*"+cns+")", "y"},
		{"("+sr_e+cns+sr_w+"*"+sr_i+cns+")", "y"},
		{"("+sr_o+cns+sr_w+"*"+")", "y"},
		{"("+cns+"{1,1}"+sr_i+sr_w+"*"+")", "y"},
		{"("+cns+sr_h+sr_w+"*"+")", "y"},
		{"("+cnoh+sr_w+"*"+"ว"+cnol+")", "n"},
		{"("+cns+sr_m+sr_aa+")", "n"},
		{"("+sr_e+cns+sr_a+")", "n"},
		{"("+cnoh+sr_mm+")", "n"},
		{"("+sr_e+cns+sr_w+"*)", "y"},
		{"("+cns+sr_h+sr_w+"*)", "n"},
		{"("+cns+"{1,1}"+sr_a+")", "n"},
		{"("+cns+"{1,1}"+sr_w+"อ"+")", "y"},
		{"("+cns+"รร"+")", "y"},
		{"("+cns+"\\u0E47)","n"}, // ก็
		{"("+cns+sr_w+")","n"}, // ก่
//		{"("+cns+sr_w+"*"+"อ"+")", "y"},
		{sym, "n"},
		{"("+cns+")", "n"},
	};

	Matcher m;

	List<String> aWd = new ArrayList<>();

	void setLine(String l) {
		tx1 = l;
		tx0 = null;
		txp = null;
	}

	Pattern[] aPat;

	void add(String s) { aWd.add(s); }

	boolean check1() {
		if(aPat==null) {
			aPat = new Pattern[pats.length];
			for(int i=0; i<aPat.length; i++) {
				aPat[i] = Pattern.compile(pats[i][0]);
			}
		}
		canBeAddLater = "n";
		String rv = null;
		for(String s : resv) { if(tx1.startsWith(s)) rv = s; }
		if(tx1.length()==0) {
			if(txp!=null) {
//				System.out.println(" z: "+txp);
				txp = null;
			}
			return false;
		} else if(rv!=null) {
			tx0 = tx1.substring(0, rv.length());
			tx1 = tx1.substring(rv.length());
		} else {
			boolean bPat = false;
			for(int i=0; i<aPat.length; i++) {
				if((m=aPat[i].matcher(tx1))!=null && m.find() && m.start()==0) {
					tx0 = tx1.substring(m.start(), m.end());
					tx1 = tx1.substring(m.end());
					canBeAddLater = pats[i][1];
					bPat = true;
					if(tx0.matches(cns)) {
						if(txp!=null) {
							txp += tx0;
							tx0 = null;
						}
					}
					break;
				}
			}
			if(!bPat) {
				tx0 = tx1.substring(0,1);
				tx1 = tx1.substring(1);
			}
		}
		if(txp!=null) {
			aWd.add(txp);
//			System.out.println(" v: "+txp);
			txp = null;
		}
		if(canBeAddLater.equals("y")) {
			txp = tx0;
		} else if(tx0!=null) {
			aWd.add(tx0);
//			System.out.println(" x: "+tx0);
		} else {
		}
		return true;
	}
	String[] getText() {
		String[] aWD = aWd.toArray(new String[aWd.size()]);
		for(int i=1; i<aWD.length-1; i++) {
			if(aWD[i-1].matches(cns+sr_w+"?") && aWD[i].matches("อ") && aWD[i+1].matches(cnol)) {
				aWD[i] = aWD[i-1]+aWD[i]+aWD[i+1];
				aWD[i-1] = ""; aWD[i+1] = "";
			} else if(aWD[i].matches("[งมลนณดถรบมยๆ]")) {
				int j=i-1; while(j>0 && aWD[j].length()==0) { j--; }
				aWD[j] = aWD[j]+aWD[i]; aWD[i] = "";
			} else if(aWD[i].matches("[คขหอกษสผปทพจตฉ]")) {
				int j=i+1; while(j<aWD.length-1 && aWD[j].length()==0) { j++; }
				aWD[j] = aWD[i]+aWD[j]; aWD[i] = "";
			}
			
		}
		return aWD;
	}
}

	static char[] cDigi = new String("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

	public static String formID(int j) {
		int nn = cDigi.length;
		int mx = nn*nn*nn;
		if(nn>=mx) return "XXX";
		int i1 = j%nn;
		int j1 = j/nn;
		int i2 = j1%nn;
		int j2 = j1/nn;
		int i3 = j2%nn;
//		System.out.println("i1:"+i1+ " j1:"+j1+" i2:"+i2+" j2:"+j2+" i3:"+i3);
//		System.out.println("i1:"+cDigi[i1]+ " i2:"+cDigi[i2]+" i3:"+cDigi[i3]);
		String id = new String(new char[] {cDigi[i3],cDigi[i2],cDigi[i1]});
		return id;
	}

	public static int formID(String id) {
		int nn = cDigi.length;
		char[] chs = id.toCharArray();
		if(chs.length!=3) return -1;
		int i1=-1,i2=-1,i3=-1;
		for(int i=0; i<cDigi.length; i++) if(chs[0]==cDigi[i]) i1 = i;
		for(int i=0; i<cDigi.length; i++) if(chs[1]==cDigi[i]) i2 = i;
		for(int i=0; i<cDigi.length; i++) if(chs[2]==cDigi[i]) i3 = i;
		int ii = i1*nn*nn + i2*nn + i3;
		System.out.println("  >> id:"+i1+","+i2+","+i3);
		return ii;
	}

	public static String nextMax(String old) {
		char[] nxt = old.toCharArray();
		boolean dg2 = true;
		for(char x : cDigi) {
			if(x>nxt[1]) {
				nxt[1] = x;
				dg2 = false;
				break;
			}
		}
		if(dg2) {
			for(char y: cDigi) {
				if(y>nxt[0]) {
					nxt[0] = y;
					nxt[1] = cDigi[0];
					dg2 = false;
					break;
				}
			}
		}
		return new String(nxt);
	}

	public static String blankRdf(String fnm) {
		return blankRdf(fnm, "");
	}
	public static String blankRdf(String fnm, String nm) {
		return blankRdf(fnm, nm, null);
	}
	public static String blankRdf(String fnm, String nm, String px1) {
		String base = PopiangDigital.sBaseIRI;
		String pre = "@prefix "+fnm+": <"+base+"/"+fnm+"#> .\n"
			+"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n"
			+"@prefix vo: <"+base+"/vo#> .\n"
			+"@prefix vp: <"+base+"/vp#> .\n";
		if(px1!=null && px1.length()>0) {
			pre += "@prefix "+px1+": <"+base+"/"+px1+"#> .\n";
		}
		pre += "\n";

		String str = pre+ fnm+":0		rdf:title  \""+nm+"\" .\n";
		return str;
	}
	public static void packArray(String[] wds) {
		for(int i=0; i<wds.length; i++) {
			if(wds[i].length()==0) {
				for(int j=i+1; j<wds.length; j++) {
					if(wds[j].length()>0) {
						wds[i] = wds[j];
						wds[j] = "";
						break;
					}
				}
			}
		}
	}
	public static String xmlNDR(String d) {
		String[] wds = d.replace("."," ").replace("_"," ").replace("-","").split(" ");
		boolean bLoop = true;
		for(int c=0; c<100 && bLoop; c++) {
			bLoop = false;
			packArray(wds);
			for(int i=0; i<wds.length-1; i++) {
				if(wds[i+1].equals("Details")) { wds[i+1] = ""; bLoop=true; }
				if(wds[i].equals(wds[i+1])) { wds[i+1] = ""; bLoop=true; }
				if(i+3<wds.length && wds[i].equals(wds[i+2]) && wds[i+1].equals(wds[i+3])) {
					wds[i+2]=""; wds[i+3]=""; bLoop=true; }
				if(i+5<wds.length && wds[i].equals(wds[i+3]) && wds[i+1].equals(wds[i+4])
					&& wds[i+2].equals(wds[i+5])) {
					wds[i+3]=wds[i+4]=wds[i+5]=""; bLoop=true; }
				if(i+7<wds.length && wds[i].equals(wds[i+4]) && wds[i+1].equals(wds[i+5])
					&& wds[i+2].equals(wds[i+6]) && wds[i+3].equals(wds[i+7])) {
					wds[i+4]=wds[i+5]=wds[i+6]=wds[i+7]=""; bLoop=true; }
			}
		}
		StringBuffer b = new StringBuffer();
		for(String s : wds) b.append(s);
		d = b.toString();
		if(d.endsWith("Text")) d = d.substring(0,d.length()-4);
		return d;
	}
}

