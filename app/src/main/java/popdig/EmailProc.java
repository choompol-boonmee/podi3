package popdig;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.io.*;
import javax.swing.*;
import java.util.*;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.Base64;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.imap.IMAPFolder;

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
import org.apache.log4j.Logger;
import java.util.regex.*;

public class EmailProc {

	static Logger log = Logger.getLogger(EmailProc.class);

	SimpleDateFormat fmfn = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", new Locale("uk","UK"));

	Hashtable<String,byte[]> hsHead;
	int nMsgChkTm = 60; // get message count every ? second
	int nMsgChkMax = 20; // stay use count

	int msgcnt = 0;

	volatile boolean bImap = false;
	volatile int nCntFromLastMsg = 0;
	int nMaxFromLastMsg = 1000;
	IMAPFolder folder = null;
//	Folder folder = null;

	File fBaseDir, fUser;
	Object objSync1 = new Object();
	String replyProc = null;


	public void startEmailThread() {
			fBaseDir = new File(PopiangDigital.workDir+"/.eml");
			if(!fBaseDir.exists()) fBaseDir.mkdirs();
			fUser = new File(fBaseDir+"/memb");
System.out.println("===============1 Start Email:"+ PopiangDigital.sRecvEmail);
			new Thread() { public void run() {
				while(true) {
					bImap = true;
//System.out.println("  imap fetch");
					Thread fet = new Thread() { public void run() { imapFetchThread(); } };
					Thread idl = new Thread() { public void run() { imapIdleThread(); } };
					Thread prc = new Thread() { public void run() { procParseThread(); } };
					fet.start();
					idl.start();
					prc.start();
					try {
						fet.join();
						idl.join();
						prc.join();
					} catch(Exception z) {}
				}
			} }.start();
	}
	public void imapFetchThread() {
		int cnt = 0;
		while(bImap) {
System.out.println("  Imap "+ ++cnt);
//log.info("ImapFetch: "+ ++cnt);
			folder = getEmailFolder();
			if(folder==null || !folder.isOpen()) {
				try { Thread.sleep(5000); } catch(Exception y) {}
				continue;
			}
			int msgCnt = 0;
			try {
				msgCnt = folder.getMessageCount();
				nCntFromLastMsg = 0;
			} catch(Exception z) {
				try { Thread.sleep(5000); } catch(Exception y) {}
				continue;
			}
			if(msgCnt>0) procMess();
			folder.addMessageCountListener(new MessageCountListener() {
				public void messagesAdded(MessageCountEvent ev) {
					nCntFromLastMsg = 0;
					procMess();
				}
				public void messagesRemoved(MessageCountEvent ev) {
					nCntFromLastMsg = 0;
				}
			});
			for(int c=0; c<nMsgChkMax; c++) {
				try {
					//Thread.sleep(1 * 60 * 1000);
					Thread.sleep(nMsgChkTm * 1000);
					msgCnt = folder.getMessageCount();
					nCntFromLastMsg = 0;
//					System.out.println("MSG: "+ msgCnt);
					//command.execute(command.line2exec("alive"));
				} catch(Exception z) {
					bImap = false;
					break;
				}
			}
	
			try {
				folder.close(true);
				System.out.println("==== CLOSE EMAIL FOLDER ====");
			} catch(Exception z) {
				System.exit(10);
			}
		}
	}

	public void imapIdleThread() {
		int cnt = 0;
		while(bImap) {
//log.info("ImapIdel: "+ ++cnt);
			try { Thread.sleep(1000); } catch(Exception y) {}
			nCntFromLastMsg++;
			if(nCntFromLastMsg>=nMaxFromLastMsg) {
				bImap = false;
//				System.out.println("MAX COUNT FROM LAST MAIL MESSAGE "+ nCntFromLastMsg);
			}
			//if (!folder.isOpen()) folder = getEmailFolder();
			try { folder.idle(); } catch(Exception x) {}
		}
	}

/*
	public void imapfetch() {
System.out.println("=======  IMAP FETCH ");
		new Thread() { public void run() {

			while(true) {
//System.out.println("IMAP FETCH: 1");
				folder = getEmailFolder();
//System.out.println("IMAP FETCH: 1.5 "+folder);
				if(folder==null || !folder.isOpen()) {
					try { Thread.sleep(5000); } catch(Exception y) {}
					continue;
				}
//System.out.println("IMAP FETCH: 2");
				int msgCnt = 0;
				try {
					msgCnt = folder.getMessageCount();
					nCntFromLastMsg = 0;
				} catch(Exception z) {
					try { Thread.sleep(5000); } catch(Exception y) {}
					continue;
				}
System.out.println("IMAP FETCH: 3 "+msgCnt);
				if(msgCnt>0) procMess();
System.out.println("folder: "+ folder);
				folder.addMessageCountListener(new MessageCountListener() {
					public void messagesAdded(MessageCountEvent ev) {
						nCntFromLastMsg = 0;
System.out.println("4.1 msg added");
						procMess();
					}
					public void messagesRemoved(MessageCountEvent ev) {
						nCntFromLastMsg = 0;
					}
				});
System.out.println("4.2 read msg");
				//for(int c=0; c<20; c++) {
				for(int c=0; c<nMsgChkMax; c++) {
					try {
						//Thread.sleep(1 * 60 * 1000);
						Thread.sleep(nMsgChkTm * 1000);
						msgCnt = folder.getMessageCount();
						nCntFromLastMsg = 0;
						System.out.println("MSG: "+ msgCnt);
						//command.execute(command.line2exec("alive"));
					} catch(Exception z) {
						System.exit(10);
						break;
					}
				}
		
				try {
					folder.close(true);
					System.out.println("==== CLOSE EMAIL FOLDER ====");
				} catch(Exception z) {
					System.exit(10);
				}
			}
		} }.start();
		for (;;) {
			try { Thread.sleep(1000); } catch(Exception y) {}
			nCntFromLastMsg++;
			if(nCntFromLastMsg>=nMaxFromLastMsg) {
				System.out.println("MAX COUNT FROM LAST MAIL MESSAGE "+ nCntFromLastMsg);
				System.exit(10);
			}
			//if (!folder.isOpen()) folder = getEmailFolder();
			try { folder.idle(); } catch(Exception x) {}
		}
	}
*/

	public void procMess() {
		if(folder==null) return;
		try {
			Message[] msgs = folder.getMessages();
			for(int i=0; i<msgs.length; i++) {
				nCntFromLastMsg = 0;
				try {
					Message msg = msgs[i];
					Date recvDate = msg.getReceivedDate();
					String sRecvDate = fmfn.format(recvDate);
					Address[] from = msg.getFrom();
					String sFrom = from[0].toString();
					int i1 = sFrom.indexOf("<");
					int i2 = sFrom.indexOf(">");
					if(i1>0 && i2>i1) sFrom = sFrom.substring(i1+1, i2);

//System.out.println("FROM: "+ sFrom+" = "+ PopiangDigital.sRecvEmail);
					if(!sFrom.toUpperCase().equals(PopiangDigital.sRecvEmail.toUpperCase())) {

						InetAddress addr = InetAddress.getByName(PopiangDigital.sImap);
						String sFN = sRecvDate + "_"+sFrom+".jlm";

						File fFP = new File(fBaseDir + "/inb/"+ sRecvDate.substring(0,8));
						File fFNt = new File(fBaseDir+ "/temp.jml");
						File fFN = new File(fFP+ "/"+ sFN);
	
						Hashtable<String,String> hsProp;
						Hashtable<String,List<String>> hsText;
						Hashtable<String,List<byte[]>> hbStrm;
						hsProp = new Hashtable<>();
						hsText = new Hashtable<>();
						hbStrm = new Hashtable<>();
						// EMAIL PARSING
						hsProp.put("RECV", sRecvDate);
						parseEmail(msg, hsProp, hsText, hbStrm);
						FileOutputStream fos = new FileOutputStream(fFNt);
						ObjectOutputStream oos = new ObjectOutputStream(fos);
						oos.writeObject(hsProp);
						oos.writeObject(hsText);
						oos.writeObject(hbStrm);
						oos.close();
	
						if(!fFP.exists()) fFP.mkdirs();
						fFNt.renameTo(fFN);
					}
	
					//System.out.println(msg.getMessageNumber()+" : "+ msg.getSubject()+" d:"+fFN
					//	+" : "+ fFN.exists()+" : "+ fFNt.exists());
	
					try {
						msg.setFlag(Flags.Flag.DELETED, true);
					} catch(javax.mail.MessageRemovedException x) {
					}
				} catch(Exception z) {
					bImap = false;
					log.error("001",z);
				}
			}
			folder.expunge();
		} catch(MessagingException mex) {
		}
	}

	IMAPFolder getEmailFolder()  {
//	Folder getEmailFolder()  {
		try {
			Properties propImap = System.getProperties();
			propImap.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			propImap.put("mail.imap.socketFactory.fallback", "false");
			propImap.put("mail.imap.socketFactory.port", "993");
			propImap.put("mail.imaps.ssl.trust", "*");
			Session session = Session.getInstance(propImap, null);
			Store store = session.getStore("imap");
// System.out.println("imap:"+PopiangDigital.sImap+" em:"+PopiangDigital.sRecvEmail+" pw:"+PopiangDigital.sPassWord);
//			store.connect(imap, email, pass);
			store.connect(PopiangDigital.sImap, PopiangDigital.sRecvEmail, PopiangDigital.sPassWord);
			folder = (IMAPFolder) store.getFolder("Inbox");
//			folder = store.getFolder("Inbox");
			if (folder == null || !folder.exists()) {
				System.out.println("Invalid folder");
				return null;
			}
//System.out.println("IMAP ..2");
			folder.open(Folder.READ_WRITE);
		} catch(Exception x) {
//			log.info(x);
			System.out.println("ER-01: "+ x);
			x.printStackTrace();
			folder = null;
		}
		return folder;
	}

	void parseEmail(Message msg, Hashtable<String,String> hsProp
		, Hashtable<String,List<String>> hsText
		, Hashtable<String,List<byte[]>> hbStrm) {
	
		try {
				
			hsText.put("TXT", new ArrayList<>());
			hsText.put("RTXT", new ArrayList<>());
			hsText.put("RPDF", new ArrayList<>());
			hsText.put("RJPG", new ArrayList<>());
			hsText.put("RGIF", new ArrayList<>());
			hsText.put("RPNG", new ArrayList<>());
			hsText.put("RXLS", new ArrayList<>());
			hsText.put("RXML", new ArrayList<>());
			hsText.put("RHTM", new ArrayList<>());
			hsText.put("RRDF", new ArrayList<>());
			hsText.put("RZIP", new ArrayList<>());
	
			hbStrm.put("JPG", new ArrayList<>());
			hbStrm.put("PNG", new ArrayList<>());
			hbStrm.put("GIF", new ArrayList<>());
			hbStrm.put("PDF", new ArrayList<>());
			hbStrm.put("XLS", new ArrayList<>());
			hbStrm.put("XML", new ArrayList<>());
			hbStrm.put("TXT", new ArrayList<>());
			hbStrm.put("HTM", new ArrayList<>());
			hbStrm.put("JAR", new ArrayList<>());
			hbStrm.put("ZIP", new ArrayList<>());
	
			String sSubj = msg.getSubject();
			Address[] from = msg.getFrom();
			String sFrom = from[0].toString();
			Date recvDate = msg.getReceivedDate();
			String sRecv = fmfn.format(recvDate);
			Address[] replyTo = msg.getReplyTo();
			String sReply = replyTo[0].toString();
			hsProp.put("SUBJ", sSubj);
			hsProp.put("FROM", sFrom);
			hsProp.put("RECV", sRecv);
			hsProp.put("REPLY", sReply);
	
			Object content = msg.getContent();
			if(content instanceof Multipart) {
				Multipart mp = (Multipart) content;
//System.out.println("2.---- multiple parts");
				parseMultipart(mp, hsProp, hsText, hbStrm);
			}
			else if(content instanceof String) {
				String txt = (String) content;
//System.out.println("1.--- String");
				hsText.get("TXT").add(txt);
/*
			} else if(content instanceof BASE64DecoderStream) {
				BASE64DecoderStream base64 = (BASE64DecoderStream) content;
				parseBase64(base64, hbStrm);
*/
			} else {
			}
		} catch(Exception x) {
		}
	}
	
/*
	void parseBase64(BASE64DecoderStream strm, Hashtable<String,List<byte[]>> hbStrm) {
		try {
			//byte[] buff = new byte[4096];
			int len;
			byte[] buf = new byte[4096];
	
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while((len=strm.read(buf,0,buf.length))>0) {
				baos.write(buf, 0, len);
			}
			byte[] buff = baos.toByteArray();
			if(hsHead==null) {
				hsHead = new Hashtable<>();
				byte[] hdPNG = { (byte)0x89, (byte)0x50, (byte)0x4e, (byte)0x47, (byte)0x0d, (byte)0x0a };
				byte[] hdJPG = { (byte)0xff, (byte)0xd8 };
				byte[] hdJPg = { (byte)0xd8, (byte)0xff };
				byte[] hdGIF7 = "GIF87a".getBytes("UTF-8");
				byte[] hdGIF9 = "GIF87a".getBytes("UTF-8");
				byte[] hdPDF7 = "%PDF-1.7".getBytes("UTF-8");
				hsHead.put("PNG", hdPNG);
				hsHead.put("JPG", hdJPG);
				hsHead.put("JPG.2", hdJPg);
				hsHead.put("GIF.7", hdGIF7);
				hsHead.put("GIF.9", hdGIF9);
				hsHead.put("PDF.7", hdPDF7);
			}
			Enumeration<String> keys = hsHead.keys();
			int difCnt = 0;
			while(keys.hasMoreElements()) {
				String key = keys.nextElement();
				byte[] head = hsHead.get(key);
				if(buff.length<head.length) continue;
				for(; difCnt<head.length; difCnt++)
					if(head[difCnt]!=buff[difCnt]) break;
				if(difCnt==head.length) {
					int i1 = key.indexOf(".");
					if(i1>0) key = key.substring(0,i1);
					List<byte[]> baAtt = hbStrm.get(key);
					if(baAtt!=null) {
						baAtt.add(buff);
						//System.out.println("===== FOUND ADD: "+ key+" : "+ baAtt);
					}
				}
			}
		} catch(Exception x) {
		}
	}
*/

	String getCharSet(String type) {
		String charset = "UTF-8";
		int i1;
		if((i1=type.indexOf("CHARSET="))>=0) {
			charset = type.substring(i1+8);
			if(charset.equals("ISO-8859-1")) {
				charset = "TIS-620";
			}
			else if(charset.equals("WINDOWS-874")) {
				charset = "TIS-620";
			}
		}
		return charset;
	}
	
//	byte[] buff = new byte[4096*4096];
	byte[] buff = new byte[1024*1024];

	void parseMultipart(Multipart mp
		, Hashtable<String,String> hsProp
		, Hashtable<String,List<String>> hsText
		, Hashtable<String,List<byte[]>> hbStrm) {
	
		String line;
		ByteArrayOutputStream baos;
		int len;
		int len0 = 0;
		try {
			for (int j=0; j < mp.getCount(); j++) {
				Part part = mp.getBodyPart(j);
				String disposition = part.getDisposition();
				String fname = part.getFileName();
				String fname0 = fname==null? null : fname.toUpperCase();
				if(part instanceof MimeBodyPart) {
					MimeBodyPart mime = (MimeBodyPart) part;
					String type = mime.getContentType().toUpperCase();
					len0 = 0;
//System.out.println("type===="+type);
					if(type.startsWith("TEXT/PLAIN")) {
						InputStream is = mime.getInputStream();
						String charset = getCharSet(type);
						baos = new ByteArrayOutputStream();
						while((len=is.read(buff,0,buff.length))>0) {
							len0 += len;
//							log.info("attached read PDF: "+ len0);
							baos.write(buff,0,len);
						}
						byte[] bres = baos.toByteArray();

						try {
							String txt = new String(bres, charset);
							txt = txt.trim();
							if(txt.length()>0) {
								if(txt.startsWith("<div dir=")) {
									//log.warning("===== TXT2: "+ txt);
								}
								//aText.add(txt);
								hsText.get("TXT").clear();
								hsText.get("TXT").add(txt);
							}
//System.out.println("TEXT:>>> "+txt);
						} catch(Exception z) {
//							System.out.println("charset: "+ charset);
//							z.printStackTrace();
						}
						hbStrm.get("TXT").add(bres);
					}
					else if(type.startsWith("TEXT/HTML")) {
						String charset = getCharSet(type);
						baos = new ByteArrayOutputStream();
						InputStream is = mime.getInputStream();
						while((len=is.read(buff,0,buff.length))>0) {
							len0 += len;
//							log.info("attached read PDF: "+ len0);
							baos.write(buff,0,len);
						}
						byte[] bres = baos.toByteArray();

						hbStrm.get("HTM").add(bres);

						String html = new String(bres, charset);
						html = html.trim();
						if(hsText.get("TXT").size()==0)
							hsText.get("TXT").add(html);

//System.out.println("HTML:>>> "+html);
						String txt = Jsoup.parse(html).text();
						if(txt.length()>0) {
							if(txt.startsWith("<div dir=")) {
								//log.warning("===== TXT2: "+ txt);
							}
							if(hsText.get("TXT").size()==0)
							hsText.get("TXT").add(txt);
						}
						hbStrm.get("TXT").add(bres);
					}
					else if( ( type.startsWith("APPLICATION/PDF")
						|| type.startsWith("APPLICATION/X-PDF"))) {
						baos = new ByteArrayOutputStream();
						InputStream is = mime.getInputStream();
						len0 = 0;
						while((len=is.read(buff,0,buff.length))>0) {
							len0 += len;
							try {
//								log.info("attached read PDF: "+ len0);
								baos.write(buff,0,len);
							} catch(Exception z) {
							z.printStackTrace();
								bImap = false;
								break;
							}
						}
						byte[] bres = baos.toByteArray();
						hbStrm.get("PDF").add(bres);
System.out.println("ATTACHED PDF: "+bres.length);
					}
					else if( type.startsWith("IMAGE/JPEG") ) {
						baos = new ByteArrayOutputStream();
						InputStream is = mime.getInputStream();
						while((len=is.read(buff,0,buff.length))>0) {
							len0 += len;
//							log.info("attached read JPEG: "+ len0);
							baos.write(buff,0,len);
						}
						byte[] bres = baos.toByteArray();
						hbStrm.get("JPG").add(bres);
					}
					else if( type.startsWith("IMAGE/PNG") ) {
						baos = new ByteArrayOutputStream();
						InputStream is = mime.getInputStream();
						while((len=is.read(buff,0,buff.length))>0) {
							len0 += len;
							log.info("attached read PNG: "+ len0);
							baos.write(buff,0,len);
						}
						byte[] bres = baos.toByteArray();
						hbStrm.get("PNG").add(bres);
System.out.println("ATTACHED PNG1: "+bres.length);
					}
					else if( type.startsWith("IMAGE/GIF") ) {
						baos = new ByteArrayOutputStream();
						InputStream is = mime.getInputStream();
						while((len=is.read(buff,0,buff.length))>0) {
							len0 += len;
							log.info("attached read GIF: "+ len0);
							baos.write(buff,0,len);
						}
						byte[] bres = baos.toByteArray();
						hbStrm.get("GIF").add(bres);
					}
					else if( type.startsWith("IMAGE/JPG") ) {
						baos = new ByteArrayOutputStream();
						InputStream is = mime.getInputStream();
						while((len=is.read(buff,0,buff.length))>0) {
							len0 += len;
							log.info("attached read JPG: "+ len0);
							baos.write(buff,0,len);
						}
						byte[] bres = baos.toByteArray();
						hbStrm.get("JPG").add(bres);
					}
					else if( type.startsWith("APPLICATION/XML")
							&& disposition!=null && fname!=null && fname0.endsWith(".XML")) {
						baos = new ByteArrayOutputStream();
						InputStream is = mime.getInputStream();
						while((len=is.read(buff,0,buff.length))>0) {
							len0 += len;
							log.info("attached read XML: "+ len0);
							baos.write(buff,0,len);
						}
						byte[] bres = baos.toByteArray();
						hbStrm.get("XML").add(bres);
					}
					else if(type.startsWith("MULTIPART/ALTERNATIVE")) {
						Multipart mp2 = (Multipart) mime.getContent();
						parseMultipart(mp2, hsProp, hsText, hbStrm);
					}
					else if(type.startsWith("MULTIPART/RELATED")) {
						Multipart mp2 = (Multipart) mime.getContent();
						parseMultipart(mp2, hsProp, hsText, hbStrm);
					}
					else if(type.startsWith("MULTIPART/RELATIVE")) {
						Multipart mp2 = (Multipart) mime.getContent();
						parseMultipart(mp2, hsProp, hsText, hbStrm);
					}
					else if(type.startsWith("APPLICATION/PKCS7-SIGNATURE")) {
						//log.info("Digital Signature");
					}
					else if(fname!=null && fname.toUpperCase().endsWith("JPG")) {
						baos = new ByteArrayOutputStream();
						InputStream is = mime.getInputStream();
						while((len=is.read(buff,0,buff.length))>0) baos.write(buff,0,len);
						byte[] bres = baos.toByteArray();
						hbStrm.get("JPG").add(bres);
					}
					else if(fname!=null && fname.toUpperCase().endsWith("PNG")) {
						baos = new ByteArrayOutputStream();
						InputStream is = mime.getInputStream();
						while((len=is.read(buff,0,buff.length))>0) baos.write(buff,0,len);
						byte[] bres = baos.toByteArray();
						hbStrm.get("PNG").add(bres);
System.out.println("ATTACHED PNG: "+bres.length);
					}
					else if(fname!=null && fname.toUpperCase().endsWith("GIF")) {
						baos = new ByteArrayOutputStream();
						InputStream is = mime.getInputStream();
						while((len=is.read(buff,0,buff.length))>0) baos.write(buff,0,len);
						byte[] bres = baos.toByteArray();
						hbStrm.get("GIF").add(bres);
					}
					else if(fname!=null && fname.toUpperCase().endsWith("PDF")) {
						System.out.println("PDF fname2:"+fname);
						baos = new ByteArrayOutputStream();
						InputStream is = mime.getInputStream();
						while((len=is.read(buff,0,buff.length))>0) baos.write(buff,0,len);
						byte[] bres = baos.toByteArray();
						hbStrm.get("PDF").add(bres);
					}
					else if(fname!=null && fname.toUpperCase().endsWith("XLSX")) {
						baos = new ByteArrayOutputStream();
						InputStream is = mime.getInputStream();
						while((len=is.read(buff,0,buff.length))>0) baos.write(buff,0,len);
						byte[] bres = baos.toByteArray();
						hbStrm.get("XLS").add(bres);
					}
					else if(fname!=null && fname.toUpperCase().endsWith("JAR")) {
						baos = new ByteArrayOutputStream();
						InputStream is = mime.getInputStream();
						while((len=is.read(buff,0,buff.length))>0) baos.write(buff,0,len);
						byte[] bres = baos.toByteArray();
						hbStrm.get("JAR").add(bres);
					}
					else if(fname!=null && fname.toUpperCase().endsWith("ZIP")) {
						baos = new ByteArrayOutputStream();
						InputStream is = mime.getInputStream();
						while((len=is.read(buff,0,buff.length))>0) baos.write(buff,0,len);
						byte[] bres = baos.toByteArray();
						hbStrm.get("ZIP").add(bres);
					}
					else {
					}
				}
			}
		} catch(Exception x) {
			bImap = false;
			log.error("002",x);
		}
	}

	void procParseThread() {
		int cnt = 0;
		File fInbDir = new File(fBaseDir+"/inb");
		while(bImap) {
//log.info("=========================== Proc Parse: "+ ++cnt);
			try {
				if(!fInbDir.exists()) { Thread.sleep(2000); continue; }
				String[] dates = fInbDir.list();
				if(dates.length==0) { Thread.sleep(2000); continue; }
				Arrays.sort(dates);
				for(int i=0; i<dates.length; i++) {
					if(dates[i].length()!=8) {
						continue;
					}
					File fDate = new File(fInbDir + "/"+ dates[i]);
					String[] times = fDate.list();
					Arrays.sort(times);
					for(int j=0; j<times.length; j++) {
						File fEml = new File(fDate+"/"+times[j]);
						String emltm = times[j];
						if(emltm.length()>24) {
							emltm = emltm.substring(0,19);
							try (InputStream is = new FileInputStream(fEml)) {
								ObjectInputStream ois = new ObjectInputStream(is);
	
								@SuppressWarnings("unchecked")
								Hashtable<String,String> hsProp =
								  (Hashtable<String,String>) ois.readObject();
								@SuppressWarnings("unchecked")
								Hashtable<String,List<String>> hsText =
								  (Hashtable<String,List<String>>) ois.readObject();
								@SuppressWarnings("unchecked")
								Hashtable<String,List<byte[]>> hbStrm =
								  (Hashtable<String,List<byte[]>>) ois.readObject();
/*
System.out.println("GETSTRM: "+ hbStrm.size());
Enumeration<String> keys = hbStrm.keys();
while(keys.hasMoreElements()) {
	String key = keys.nextElement();
	List<byte[]> abaStrm = hbStrm.get(key);
	System.out.println("   "+key+":"+ abaStrm.size());
}
*/
								String from = getFrom(hsProp);
								if(!fUser.exists()) fUser.mkdirs();
								File fEmIn = new File(fUser+"/"+from+"/inbox");
								if(!fEmIn.exists()) fEmIn.mkdirs();
								File fEmOut = new File(fUser+"/"+from+"/outbox");
								if(!fEmOut.exists()) fEmOut.mkdirs();
								File fEmInf = new File(fUser+"/"+from+"/info");
								if(!fEmInf.exists()) fEmInf.mkdirs();
	
								File fEmTo = new File(fEmIn+"/"+fEml.getName());
								fEml.renameTo(fEmTo);
	
								procEmail(emltm, hsProp, hsText, hbStrm);
							} catch(Exception z) {
								log.error("003", z);
							}
						}
						//System.out.println("DELETE ======================================");
						fEml.delete();
					}
					String[] files = fDate.list();
					if(files!=null && files.length==0) {
					//if(fDate.list().length==0) {
						//System.out.println("DELETE FOLDER");
						fDate.delete();
					}
				}
			} catch(Exception z) {
				log.error("004", z);
			}
		}
	}

	String getSubj(Hashtable<String,String> hsProp) {
		String subj = hsProp.get("SUBJ");
		if(subj==null) subj = "";
		subj = subj.toUpperCase();
		while(subj.length()>0) {
			subj = subj.trim();
			if(subj.startsWith("RE: ")) { subj = subj.substring(4); continue; }
			if(subj.startsWith("RE:")) { subj = subj.substring(3); continue; }
			break;
		}
		return subj;
	}
	String getFrom(Hashtable<String,String> hsProp) {
		String sFrom = hsProp.get("FROM");
		sFrom = sFrom.toUpperCase();
		int i1 = sFrom.indexOf("<");
		int i2 = sFrom.lastIndexOf(">");
		if(i1>0 && i2>i1) { sFrom = sFrom.substring(i1+1, i2); }
		return sFrom;
	}

	void procEmail(String emltm, Hashtable<String,String> hsProp
					   , Hashtable<String,List<String>> hsText
					   , Hashtable<String,List<byte[]>> hbStrm) {
		try {
			String subj = getSubj(hsProp);
			String from = getFrom(hsProp);
			log.info("PROC EMAIL: "+ subj+" "+from);
//			String subj0 = hsProp.get("SUBJ");
	
			execEmail(subj, from, hsProp, hsText, hbStrm);
			List<String> aReply = hsText.get("RTXT");
			String replyMsg = null;
			if(aReply==null || aReply.size()==0) {
				replyMsg = "NO REPLY MESSAGE";
			} else {
				StringBuilder stb = new StringBuilder();
				for(String rep : aReply) { stb.append(rep); stb.append("\r\n"); }
				replyMsg = stb.toString();
			}
			String replyTo = hsProp.get("REPLY");
//			log.info("REPLY "+replyMsg + " to "+ replyTo);

			//if(!replyTo.equals(email)) {
			if(!replyTo.equals(PopiangDigital.sRecvEmail)) {
				sendMail(replyTo, subj, replyMsg, hsText);
			}
		} catch(Exception z) {
			log.error("005", z);
		}
	}
	
	void sendMail(String replyTo, String subj, String replyMsg, Hashtable<String,List<String>> hsText) {
		try {
			String rto = ""+replyTo;
			if(rto.indexOf("Delivery")>=0) return;
			String[] emls = replyTo.split(",");
			if(emls.length<=0) return;

			replyTo = emls[0];

			System.out.println("SEND TO "+replyTo+" with:"+subj);
			Properties propSmtp = System.getProperties();
			propSmtp.put("mail.smtp.host", PopiangDigital.sSmtp);
			propSmtp.put("mail.smtp.socketFactory.port", "465");
			propSmtp.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			propSmtp.put("mail.smtp.ssl.trust", "*");
			propSmtp.put("mail.smtp.auth", "true");
			propSmtp.put("mail.smtp.port", "465");
	
			Session session = Session.getInstance(propSmtp);
			MimeMessage replyMessage = new MimeMessage(session);
			replyMessage.setFrom(new InternetAddress(PopiangDigital.sRecvEmail));

			InternetAddress[] aReplyTo = new InternetAddress[emls.length];
			for(int i=0; i<emls.length; i++) {
				aReplyTo[i] = new InternetAddress(emls[i]);
			}

//			replyMessage.setSubject("RE: "+subj);
System.out.println("reply: "+ subj);
			replyMessage.setSubject(subj);
			replyMessage.setReplyTo(aReplyTo);
	
			String[] types = new String[] {"RRDF", "RZIP", "RPDF", "RXLS", "RJPG"};
			int cnt = 0;
			List<List<String>> aaReply = new ArrayList<>();
			for(int i=0; i<types.length; i++) {
				List<String> lst = hsText.get(types[i]);
				aaReply.add(lst);
				if(lst!=null) cnt += lst.size();
			}
			if(cnt==0) {
				//System.out.println("   MSG: "+ replyMsg);
				replyMessage.setContent(replyMsg, "text/plain; charset=utf-8");
			} else {
	
				Multipart mp1 = new MimeMultipart();
				MimeBodyPart textPart = new MimeBodyPart();
				textPart.setContent(replyMsg, "text/plain; charset=utf-8");
				mp1.addBodyPart(textPart);
				replyMessage.setContent(mp1);
	
				for(int i=0; i<aaReply.size(); i++) {
					for(int j=0; aaReply.get(i)!=null && j<aaReply.get(i).size(); j++) {
						MimeBodyPart attachment1 = new MimeBodyPart();
						String file = aaReply.get(i).get(j);
						attachment1.attachFile(file);
						mp1.addBodyPart(attachment1);
					}
				}
			}
			Transport t = session.getTransport("smtp");
			try {
				t.connect(PopiangDigital.sRecvEmail, PopiangDigital.sPassWord);
				t.sendMessage(replyMessage, aReplyTo);
			} catch(Exception x) {
				log.error("006", x);
			} finally {
				t.close();
			}

		} catch(Exception z) {
			log.error("007", z);
		}
	}
	void sendEMail(String replyTo, String subj, String replyMsg, Hashtable<String,List<String>> hsText) {
		try {
			String rto = ""+replyTo;
			if(rto.indexOf("Delivery")>=0) return;
			String[] emls = replyTo.split(",");
			if(emls.length<=0) return;

			replyTo = emls[0];

			if(subj.startsWith("RE: ")) subj = subj.substring(4);
			System.out.println("SEND TO "+replyTo+" with:"+subj);
			Properties propSmtp = System.getProperties();
			propSmtp.put("mail.smtp.host", PopiangDigital.sSmtp);
			propSmtp.put("mail.smtp.socketFactory.port", "465");
			propSmtp.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			propSmtp.put("mail.smtp.ssl.trust", "*");
			propSmtp.put("mail.smtp.auth", "true");
			propSmtp.put("mail.smtp.port", "465");
	
			Session session = Session.getInstance(propSmtp);
			MimeMessage replyMessage = new MimeMessage(session);
			replyMessage.setFrom(new InternetAddress(PopiangDigital.sRecvEmail));

			InternetAddress[] aReplyTo = new InternetAddress[emls.length];
			for(int i=0; i<emls.length; i++) {
				aReplyTo[i] = new InternetAddress(emls[i]);
			}

//			replyMessage.setSubject("RE: "+subj);
//System.out.println("reply: "+ subj);
			replyMessage.setSubject(subj);
			replyMessage.setReplyTo(aReplyTo);
	
			String[] types = new String[] {"RRDF", "RZIP", "RPDF", "RXLS", "RJPG"};
			int cnt = 0;
			List<List<String>> aaReply = new ArrayList<>();
			for(int i=0; i<types.length; i++) {
				List<String> lst = hsText.get(types[i]);
				aaReply.add(lst);
				if(lst!=null) cnt += lst.size();
			}
			if(cnt==0) {
				//System.out.println("   MSG: "+ replyMsg);
				replyMessage.setContent(replyMsg, "text/plain; charset=utf-8");
			} else {
	
				Multipart mp1 = new MimeMultipart();
				MimeBodyPart textPart = new MimeBodyPart();
				textPart.setContent(replyMsg, "text/plain; charset=utf-8");
				mp1.addBodyPart(textPart);
				replyMessage.setContent(mp1);
	
				for(int i=0; i<aaReply.size(); i++) {
					for(int j=0; aaReply.get(i)!=null && j<aaReply.get(i).size(); j++) {
						MimeBodyPart attachment1 = new MimeBodyPart();
						String file = aaReply.get(i).get(j);
						attachment1.attachFile(file);
						mp1.addBodyPart(attachment1);
					}
				}
			}
			Transport t = session.getTransport("smtp");
			try {
				t.connect(PopiangDigital.sRecvEmail, PopiangDigital.sPassWord);
				t.sendMessage(replyMessage, aReplyTo);
			} catch(Exception x) {
				log.error("006", x);
			} finally {
				t.close();
			}

		} catch(Exception z) {
			log.error("007", z);
		}
	}

	String getCommand(Hashtable<String,List<String>> hsText) {
		String txt = null;
		List<String> aText = hsText.get("TXT");
		for(int i=0; i<aText.size(); i++) {
			int ii;
			String txt0 = aText.get(i);
			if((ii=txt0.indexOf("..."))>=0) {
				txt = txt0.substring(0, ii).trim();
				break;
			}
			if((ii=txt0.indexOf("___"))>=0) {
				txt = txt0.substring(0, ii).trim();
				break;
			}
			if((ii=txt0.indexOf("นับถือ"))>=0) {
				txt = txt0.substring(0, ii).trim();
				break;
			}
		}
		return txt;
	}

	boolean exists(String em) {
		if(em==null) return false;
		File d = new File(fUser+"/"+em+"/info/");
		if(!d.exists()) return false;
		return true;
	}
		
	void saveinfo(String from, String para, String txt) {
		try {
			if(txt==null) return;
			if(from.indexOf("MAILER")>=0) return;
			File d = new File(fUser+"/"+from+"/info/");
			if(!d.exists()) d.mkdirs();
			File f = new File(fUser+"/"+from+"/info/"+para+".txt");
			//System.out.println("user:"+ f.getAbsolutePath());
			//System.out.println("text:"+txt.length()+"["+txt+"]");
			FileOutputStream fos = new FileOutputStream(f);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			bw.write(txt);
			bw.close();
		} catch(Exception z) {
			log.error("007", z);
		}
	}
	
	String readinfo(String from, String para) {
		try {
			Path pth = Paths.get(fUser+"/"+from+"/info/"+para+".txt");
			byte[] buf = Files.readAllBytes(pth);
			String ss = new String(buf, "UTF-8");
			return ss;
		} catch(Exception z) {
//			log.error("008", z);
		}
		return "";
	}

	Pattern empat = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$" , Pattern.CASE_INSENSITIVE);
	String[] aTag = {"NAME","ENAME","STDID","MOBILE","SUBSCRIBE"};
	String[] aVars = {"MSG","REPO","LAB","CLIP","KEY"};
	String[] aStrmType = { "TXT","JPG","PNG","GIF","PDF","XLS","XML","HTM","JAR","ZIP" };
	Map<String,String> mTag;

	void execEmail(String subj, String from, Hashtable<String,String> hsProp
					   , Hashtable<String,List<String>> hsText
					   , Hashtable<String,List<byte[]>> hbStrm) {
		//bShowLog = true;
		String txt = getCommand(hsText);
		List<String> aReply = hsText.get("RTXT");
/*
		List<String> aPdfReply = hsText.get("RPDF");
		List<String> aRdfReply = hsText.get("RRDF");
		List<String> aZipReply = hsText.get("RZIP");
		List<String> aJpgReply = hsText.get("RJPG");
		List<byte[]> baaPdf = hbStrm.get("PDF");
*/
		String ns;
		int i1,i2;
		String own = PopiangDigital.sEmail.toUpperCase();
		subj = subj.replaceAll("^\\s+", "").replaceAll("\\s+$", "");

		if((i1=subj.indexOf("="))>0 && (txt==null || txt.length()==0)) {
			txt = subj.substring(i1+1).trim();
			subj = subj.substring(0,i1).trim();
		}
	
		if((i1=subj.indexOf("<="))>0 && own.equals(from)) {
			String emrep = subj.substring(i1+2);
			subj = subj.substring(0,i1);
			emrep = emrep.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
			subj = subj.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
			if(empat.matcher(emrep).find()) {
				from = emrep;
			} else {
				aReply.add("โปรดใส่ อีเมล์แอดเดรส หลัง '<=' ");
			}
		}
		if((i1=subj.indexOf(":"))>0) {
			txt = subj.substring(i1+1);
			subj = subj.substring(0,i1);
			txt = txt.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
			subj = subj.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
		}

		String rpy = hsProp.get("REPLY");
//		log.info("REPLY:"+ rpy);

		String vars = null;
		for(String v : aVars) {
			if(subj.startsWith(v) && (ns=subj.substring(v.length()))!=null
			 && Pattern.matches("[0-9]+", ns) ) {
				vars = subj;
				break;
			}
		}
		if(mTag==null) {
			mTag = new HashMap<>();
			for(String tg: aTag) {
				mTag.put(tg, tg);
			}
		}
		String tg = mTag.get(subj);
		
		if(tg!=null || vars!=null) {
			if(txt==null) {
				txt = readinfo(from, subj);
				aReply.add("ได้ '"+subj+"' = '"+txt+"' แล้วค่ะ");
			} else {
				boolean oksave = true;
				String otx = readinfo(own, subj);
				if(otx!=null) {
//					System.out.println("OWNER: "+ own+ " Tx:"+otx+" sj:"+subj);
					if(!from.equals(own) && otx.indexOf("___STOP___")>=0) oksave = false;
				}
				if(oksave) {
					saveinfo(from, subj, txt);
					aReply.add("ได้ '"+subj+"' = '"+txt+"' แล้วค่ะ");
				} else {
					aReply.add("หยุดรับ "+subj+" แล้วค่ะ");
				}
			}
		} else if(subj.startsWith("ขออนุญาต") && subj.length()>12 && subj.length()<100) {
			if(txt==null) {
				aReply.add("ไม่สามารถอนุญาตได้เนื่องจากข้อมูลไม่ครบถ้วน\nกรุณาใส่ข้อมูลอย่างน้อยดังนี้ \n บรรทัดแรกว่า 'ชื่อ:' ตามด้วยชื่อของท่าน \nและจบบรรทัดสุดท้ายด้วย 'นับถือ'");
			} else {
				aReply.add("อนุญาตให้ท่าน" + subj.substring(8)+ "ได้\n"+txt+"\n\nลงนามโดย\nนายกเทศมนตรี");
			}
		} else if(subj.equals("EDIT")) {
System.out.println("EDIT: "+ txt);
			String rp = PopiangDigital.webserver.access.reserveAccess(from, txt, "", "3H", "");
System.out.println("REPLY: "+ rp);
			aReply.add(rp);
		} else if(subj.startsWith("FORM")) {
			List<byte[]> bL = hbStrm.get("PDF");
			if(bL.size()>0) {
				byte[] bPdf = bL.get(0);
				aReply.add("ได้แบบฟอร์มแล้วค่ะ "+bPdf.length);
			} else {
				aReply.add("กรุณาแนบแบบฟอร์มด้วยค่ะ");
			}
		} else if(subj.startsWith("EXAM") && (ns=subj.substring(4))!=null) {
			if( Pattern.matches("[0-9]+", ns) ) {
				String exm = readinfo(own, "EXAM");
				String nm = readinfo(from, "NAME");
				String id = readinfo(from, "STDID");
				if(nm==null || nm.length()==0) {
					aReply.add("กรุณาแจ้งชื่อ ด้วยหัวเรื่อง 'NAME' และใส่ชื่อ ตามด้วย '...' หรือ '___'");
				} else if(id==null || id.length()==0) {
					aReply.add("กรุณาแจ้งรหัสนักศึกษา ด้วยหัวเรื่อง 'STDID' และใส่รหัสนักศึกษา ตามด้วย '...' หรือ '___'");
				} else if(txt!=null && exm!=null) {
					if(exm.equals("START") ) {
						String qz = readinfo(own, "QUIZ"+ns);
						if(qz==null || qz.length()==0) {
							aReply.add(subj+"\n===========\nNO QUIZ");
						} else {
							saveinfo(from, subj, txt);
							aReply.add(subj+"\n===========\n"+txt+"");
						}
					} else {
						aReply.add(subj+" 'ไม่อยู่ในช่วงเวลาทำข้อสอบ'");
					}
				} else {
					aReply.add(subj+" PLEASE PUT END WITH '...' หรือ '___'");
				}
			} else {
				if(txt==null) {
					if(from.equals(own)) {
						String exam = readinfo(own, "EXAM");
						aReply.add(exam);
					} else {
						StringBuffer buf = new StringBuffer();
						for(int j=1; j<30; j++) {
							String qx = readinfo(own, "QUIZ"+j);
							if(qx==null || qx.length()==0) break;
							String as = readinfo(from, "EXAM"+j);
							String mk = readinfo(from, "MARK"+j);
							String ax = (as!=null && as.length()>0)? ""+as.length() : ".";
							if(mk.length()>0) ax += " :"+mk;
							buf.append("  Q"+j+"="+ax+"\n");
						}
						aReply.add(buf.toString());
					}
				} else if(txt.equals("START") && from.equals(own)) {
					saveinfo(own, subj, txt);
					aReply.add(subj+" '"+txt+"'");
				} else if(txt.equals("STOP") && from.equals(own)) {
					saveinfo(own, subj, txt);
					aReply.add(subj+" '"+txt+"'");
				} else if(txt.equals("CLEAR") && from.equals(own)) {
System.out.println("CLEAR..1");
					StringBuffer buf = new StringBuffer();
					for(int i=1; i<20; i++) {
System.out.println("QUIZ.."+i);
						String qz = readinfo(own, "QUIZ"+i);
						if(qz==null || qz.length()==0) break;
						buf.append("remove QUIZ"+i+"\n");
						saveinfo(own, "QUIZ"+i, "");
						String[] memb = fUser.list();
						int c = 0;
						for(int j=0; j<memb.length; j++) {
							String em = memb[j];
System.out.println(" MEMB.."+j+":"+em);
							if(!empat.matcher(em).find()) continue;
							String nm = readinfo(em, "NAME");
							String id = readinfo(em, "STDID");
							if(id==null || id.length()==0 || nm==null || nm.length()==0) continue;
							c++;
							saveinfo(em, "EXAM"+i, "");
							saveinfo(em, "MARK"+i, "");
							buf.append("   "+c+": "+em+" : "+nm+" : "+id+"\n");
						}
					}
					String ret = buf.toString();
					saveinfo(own, subj, ret);
					aReply.add(ret);
				} else if(txt.equals("LIST") && from.equals(own)) {
					String[] memb = fUser.list();
					StringBuilder buf = new StringBuilder();
					int c = 0;
					for(int i=0; i<memb.length; i++) {
						String em = memb[i];
						if(!empat.matcher(em).find()) continue;
						String nm = readinfo(em, "NAME");
						String id = readinfo(em, "STDID");
						if(id==null || id.length()==0 || nm==null || nm.length()==0) continue;
						c++;
						buf.append(c+": "+em+" : "+nm+" : "+id+"\n");
					}
					aReply.add(subj+"-CLASS LIST\n"+buf);
				} else {
					String exam = readinfo(own, "EXAM");
					aReply.add("Please say 'START' or 'STOP', not is "+exam);
				}
			}
		} else if(subj.startsWith("QUIZ")
			&& (ns=subj.substring(4))!=null && Pattern.matches("[0-9]+", ns) ) {
			String exm = readinfo(own, "EXAM");
			if(from.equals(own)) {
				if(txt==null || txt.length()==0) {
					String qz = readinfo(own, subj);
					if(qz==null || qz.length()==0) {
						aReply.add(subj+"\nYou need to put text");
					} else {
						aReply.add(subj+"\n"+qz);
					}
				} else {
					saveinfo(own, subj, txt);
					aReply.add(subj+"\n====================\n"+txt);

					String[] memb = fUser.list();
					StringBuilder buf = new StringBuilder();
					buf.append(rpy);
					for(int i=0; i<memb.length; i++) {
						String em = memb[i];
						if(!empat.matcher(em).find()) continue;
						String nm = readinfo(em, "NAME");
						String id = readinfo(em, "STDID");
						if(id==null||id.length()==0||nm==null||nm.length()==0) continue;
						buf.append(","+em);
					}
log.info("SEND TO\n"+buf.toString()+"\n");
					hsProp.put("REPLY", buf.toString());
				}
			} else {
				aReply.add("NOT ALLOWED");
			}
		} else if(subj.equals("TOALL")) {
			String exm = readinfo(own, "EXAM");
			if(from.equals(own)) {
				if(txt==null || txt.length()==0) {
					aReply.add(subj+"\nYou need to put text");
				} else {
					aReply.add(txt);

					String[] memb = fUser.list();
					StringBuilder buf = new StringBuilder();
					buf.append(rpy);
					for(int i=0; i<memb.length; i++) {
						String em = memb[i];
						if(!empat.matcher(em).find()) continue;
						buf.append(","+em);
					}
log.info("SEND TO\n"+buf.toString()+"\n");
					hsProp.put("REPLY", buf.toString());
				}
			} else {
				aReply.add("NOT ALLOWED");
			}
		} else if(subj.startsWith("MARK")) {
			if(own.equals(from)) {
				if(txt==null) txt = "CLIP";
				String[] memb = fUser.list();
				Map<String,String> id2em = new HashMap<>();
				List<String> ids = new ArrayList<>();
				int mx = 0;
				for(int i=0; i<memb.length; i++) {
					String em = memb[i];
					if(!empat.matcher(em).find()) continue;
					String id = readinfo(em, "STDID");
					String nm = readinfo(em, "NAME");
					if(id==null||id.length()==0||nm==null||nm.length()==0) continue;
					for(int mm=mx+1; mm<20;mm++) {
						String x = readinfo(em, txt+mm);
						System.out.println(" x:"+txt+"-"+mm+" : "+readinfo(em, txt+mm));
						if(x==null || x.length()==0) {
							mx = mm -1;
							break;
						}
					}
System.out.println("id: "+ id+" : "+em+" : mx: "+ mx);
					id2em.put(id,em);
					ids.add(id);
				}
				String[] ida = ids.toArray(new String[ids.size()]);
				Arrays.sort(ida);
				
				StringBuilder buf = new StringBuilder();
				for(String id: ida) {
					String em = id2em.get(id);
					String nm = readinfo(em, "NAME");
					if(id==null || id.length()==0 || nm==null || nm.length()==0) continue;
					buf.append(em+": "+id+" : "+nm+" : ");
					for(int j=1; j<=mx; j++) {
						String dt = readinfo(em, txt+j);
						dt = dt.replace("\n","").replace("\r","");
						buf.append("_|_"+dt);
					}
					buf.append("\n");
				}
				aReply.add(txt+"\n"+buf);
			} else {
				aReply.add("NOT ALLOWED");
			}
		} else if(subj.startsWith("ANS") && (ns=subj.substring(3))!=null) {
			if(own.equals(from)) {
				String qz = readinfo(own, "QUIZ"+ns);
				if(ns!=null && ns.length()>0 && (qz==null || qz.length()==0)) {
					aReply.add(subj+"\nNO QUIZ");
				} else if(!Pattern.matches("[0-9]+", ns) ) {
					String[] memb = fUser.list();

					Map<String,String> id2em = new HashMap<>();
					List<String> ids = new ArrayList<>();
					for(int i=0; i<memb.length; i++) {
						String em = memb[i];
						if(!empat.matcher(em).find()) continue;
						String id = readinfo(em, "STDID");
						String nm = readinfo(em, "NAME");
						if(id==null||id.length()==0||nm==null||nm.length()==0) continue;
						id2em.put(id,em);
						ids.add(id);
					}
					String[] ida = ids.toArray(new String[ids.size()]);
					Arrays.sort(ida);

					StringBuilder buf = new StringBuilder();
					for(String id: ida) {
						String em = id2em.get(id);
						String nm = readinfo(em, "NAME");
						if(id==null || id.length()==0 || nm==null || nm.length()==0) continue;
						buf.append(em+": "+id+" : "+nm+" : ");
						for(int j=1; j<20; j++) {
							String qx = readinfo(own, "QUIZ"+j);
							if(qx==null || qx.length()==0) break;
							String as = readinfo(em, "EXAM"+j);
							String mk = readinfo(em, "MARK"+j);
							String ax = (as!=null && as.length()>0)? ""+as.length() : ".";
							if(mk.length()>0) ax += " :"+mk;
							else ax += " ##";
							buf.append("  Q"+j+"="+ax);
						}
						buf.append("\n");
					}
					aReply.add(subj+"-ANS\n"+buf);
				} else if(txt!=null && txt.length()>0) {
					try {
						ByteArrayInputStream bais = new ByteArrayInputStream(txt.getBytes());
						BufferedReader br = new BufferedReader(new InputStreamReader(bais));
						String line;
						StringBuffer buf = new StringBuffer();
						String ans = null;
						while( (line=br.readLine())!=null ) {
							if(line.startsWith("QUIZ:ANS")) {
								ans = line.substring(8).trim();
								if(!ns.equals(ans)) {
									aReply.add("ANS does not match '"+ns+"'<>'"+ans+"'");
									break;
								}
							}
							if(ans==null || ans.length()==0) continue;
							if(!line.startsWith("==== ")) continue;

							Pattern patt = Pattern.compile("==== \\[(.*)\\] \\[(.*)\\] \\[(.*)\\] \\[(.*)\\].*");
							Matcher match = patt.matcher(line);
							int cnt = 0;
/*
							while(match.find()) {
								cnt++;
							}
*/
							String em = null, mk = null;
							while(match.find()) {
								cnt++;
								String a1 = match.group(1);
								String a2 = match.group(2);
								String a3 = match.group(3);
								String a4 = match.group(4);
								em = a1;
								mk = a4;
							}
							if(exists(em) && mk!=null) saveinfo(em, "MARK"+ans, mk);
							buf.append(line+":"+ans+" em:"+em+" mk:"+mk+"\n");
System.out.println("LINE:"+ line);
System.out.println("    : cnt:"+cnt);
						}
						aReply.add(buf.toString());
					} catch(Exception z) {
						aReply.add("ERROR: "+z);
					}
				} else {
					String[] memb = fUser.list();
					Map<String,String> id2em = new HashMap<>();
					List<String> ids = new ArrayList<>();
					for(int i=0; i<memb.length; i++) {
						String em = memb[i];
						if(!empat.matcher(em).find()) continue;
						String id = readinfo(em, "STDID");
						String nm = readinfo(em, "NAME");
						if(id==null||id.length()==0||nm==null||nm.length()==0) continue;
						id2em.put(id,em);
						ids.add(id);
					}
					String[] ida = ids.toArray(new String[ids.size()]);
					Arrays.sort(ida);

					StringBuilder buf = new StringBuilder();
					buf.append("QUIZ:"+subj+"\n");
					for(String id: ida) {
						String em = id2em.get(id);
						String nm = readinfo(em, "NAME");
						int ii = nm.indexOf(" ");
						if(ii>0) nm = nm.substring(0,ii);
						if(nm.length()>15) nm = nm.substring(0,15);
						String tx = readinfo(em, "EXAM"+ns);
						String mk = readinfo(em, "MARK"+ns);
						if(id==null||id.length()==0||nm==null||nm.length()==0) continue;
						buf.append("\n");
						buf.append("==== ["+em+"] ["+nm+"] ["+id+"] ["+mk+"]\n");
						buf.append(tx+"\n");
					}
					aReply.add(buf.toString());
				}
			} else {
				aReply.add("NOT ALLOWED");
			}
		} else if("STD".equals(subj)) {
			boolean bOK = true;
			if(txt!=null && txt.startsWith("EXAM")) bOK = false;
			String[] memb = fUser.list();

			Map<String,String> id2em = new HashMap<>();
			List<String> ids = new ArrayList<>();
			for(int i=0; i<memb.length; i++) {
				String em = memb[i];
				if(!empat.matcher(em).find()) continue;
				String id = readinfo(em, "STDID");
				String nm = readinfo(em, "NAME");
				if(id==null||id.length()==0||nm==null||nm.length()==0) continue;
				id2em.put(id,em);
				ids.add(id);
			}
			String[] ida = ids.toArray(new String[ids.size()]);
			Arrays.sort(ida);

			StringBuilder buf = new StringBuilder();
			for(String id: ida) {
				String em = id2em.get(id);
				if(!empat.matcher(em).find()) continue;
				if(own.equals(em)) continue;
				String nm = readinfo(em, "NAME");
				String tx = readinfo(em, txt);
				if(!bOK) {
					if(tx!=null) { tx = ""+tx.length(); } else tx = "0";
				}
				buf.append(id+" : "+em+" : "+nm+" : "+tx+"\n");
			}
			aReply.add(subj+"-"+txt+"\n"+buf);

		} else if("INFO".equals(subj)) {
			boolean bOK = true;
			if(txt!=null && txt.startsWith("EXAM")) bOK = false;
			String[] memb = fUser.list();
			StringBuilder buf = new StringBuilder();
			if(txt==null) txt = "";
			i1 = txt.indexOf(":");
			if(i1>0) {
				String txt1 = txt.substring(0,i1);
				String txt2 = txt.substring(i1+1);
				i1 = txt2.indexOf("-");
				if(i1>0) {
					String p1 = txt2.substring(0,i1);
					String p2 = txt2.substring(i1+1);
					try { i1 = Integer.parseInt(p1); } catch(Exception z) { i1=-1; }
					try { i2 = Integer.parseInt(p2); } catch(Exception z) { i2=-1; }
					if(i1>0 && i2>0) {
						buf.append("\nTXT1="+txt1+" p1:"+i1+" p2:"+i2);
						for(int j=i1; j<=i2; j++) {
							int c = 0, w = 0;
							StringBuffer nms = new StringBuffer();
							for(int i=0; i<memb.length; i++) {
								String em = memb[i];
								if(!empat.matcher(em).find()) continue;
								String tx = readinfo(em, txt1+j);
								if(em.equals(own)) {
									
								}
								String nm = readinfo(em, "NAME");
								String id = readinfo(em, "STDID");
								String te = readinfo(em, "MOBILE");
//System.out.println(i+" : "+ em);
								if(tx!=null && tx.length()>0) {
									c++;
								}
								else if(nm!=null && nm.length()>0 && id!=null && id.length()>0
									&& te!=null && te.length()>0 ) {
									w++;
									if(nms.length()>0) nms.append(", ");
									nms.append(nm);
								}
							}
							String op = c>w && nms.length()>0 ? " ("+nms.toString()+")" : "";
							buf.append("\n"+txt1+j+" = "+c+op);
						}
					}
//				} else {
//					buf.append("TXT2="+txt);
				}
			} else {
//System.out.println("memb:"+i+": "+em+" em:"+empat.matcher(em).find()+" own:"+own.equals(em));
				StringBuffer top = new StringBuffer();
				for(int i=0; i<memb.length; i++) {
					String em = memb[i];
					if(!empat.matcher(em).find()) continue;
					String tx = readinfo(em, txt);
					if(em.equals(own)) {
						top.append(tx);
						continue;
					}
					String nm = readinfo(em, "NAME");
					if(!bOK) {
						if(tx!=null) { tx = ""+tx.length(); } else tx = "0";
					}
					buf.append(em+" "+nm+":"+tx+"\n");
				}
				if(top.length()>0) {
					buf.append("===============================\n");
					buf.append(top);
				}
			}
			aReply.add(subj+"-"+txt+"\n"+buf);
		} else if(subj.equals("TOSUB")) {
			String exm = readinfo(own, "EXAM");
			if(from.equals(own)) {
				if(txt==null || txt.length()==0) {
					aReply.add(subj+"\nYou need to put text");
				} else {
for(String st : aStrmType) {
	List<byte[]> s = hbStrm.get(st);
	System.out.println(st+" = "+s.size());
}
					aReply.add(txt);

					String[] memb = fUser.list();
					StringBuilder buf = new StringBuilder();
					buf.append(rpy);
					for(int i=0; i<memb.length; i++) {
						String em = memb[i];
						if(!empat.matcher(em).find()) continue;
						String sub = readinfo(em, "SUBSCRIBE");
						if(sub==null || sub.length()==0 || sub.equals("NO")) continue;
						buf.append(","+em);
					}
log.info("SEND TO\n"+buf.toString()+"\n");
					hsProp.put("REPLY", buf.toString());
				}
			} else {
				aReply.add("NOT ALLOWED");
			}
		} else {
			System.out.println("SUBJ:"+subj);
			aReply.add("หัวเรื่องไม่ถูกต้องค่ะ");
		}
	}
}

