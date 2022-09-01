package popdig;

import io.undertow.Undertow;
import io.undertow.server.handlers.resource.PathResourceManager;
import java.nio.file.Paths;
import static io.undertow.Handlers.resource;
import io.undertow.server.*;
import io.undertow.util.*;
import io.undertow.server.handlers.PathHandler;
import io.undertow.Handlers;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Deque;
import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.FormParserFactory.Builder;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionAttachmentHandler;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionCookieConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.Headers;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.xml.bind.DatatypeConverter;
import io.undertow.server.handlers.resource.Resource;
import org.apache.log4j.Logger;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.*;
import java.util.Map.Entry;

public class WebServer {

	static Logger log = Logger.getLogger(WebServer.class);

	SimpleDateFormat datefm = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
	SimpleDateFormat datetm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public WebDataAccessHandler access;

	String ht2 = 
		"<head>\n"+
		"<title>ลงทะเบียนเข้าร่วมสัมมนา</title>\n"+
		"<meta charset='UTF-8'>\n"+
		"<meta name='viewport' content='width=device-width, initial-scale=1.0'>\n"+
		"<link rel='stylesheet' href='https://unpkg.com/@picocss/pico@latest/css/pico.min.css'>\n"+
		"</head>\n"+
		"<body>\n"+
		"<div>\n"+
		"<h3>ลงทะเบียนเข้าร่วมการสัมมนา CIM Study Forum</h3>\n"+
		"หลังจากลงทะเบียนจะมีอีเมล์ส่งไปหาท่านพร้อมลิงค์เข้าร่วมงานสัมมนา<br>\n"+
		"</div>\n"+
		"<hr>"+
		"<form action='#' method='post' accept-charset='utf-8' enctype='multipart/form-data'>\n"+
		"    <label for='ชื่อผู้เข้าร่วมสัมมนา'>\n"+
		"      ชื่อผู้เข้าร่วมสัมมนา\n"+
		"      <input type='text' id='name' name='name' placeholder='ชื่อ' required>\n"+
		"    </label>\n"+
		"    <label for='ชื่อหน่วยงาน'>\n"+
		"      ชื่อหน่วยงาน\n"+
		"      <input type='text' id='org' name='org' placeholder='ชื่อหน่วยงาน' required>\n"+
		"    </label>\n"+
		"  <label for='email'>Email address</label>\n"+
		"  <input type='email' id='email' name='email' placeholder='Email address' required>\n"+
		"  <small>We'll never share your email with anyone else.</small>\n"+
		"  <button type='submit'>ลงทะเบียน</button>\n"+
					"<div>\n"+
					"<a href='/'>กลับสู่หน้าหลัก</a>\n"+
					"</div>\n"+
		"</form>\n"+
		"<hr>\n"+
		"</body>\n"+
		"</html>\n"+
	"";

	int getCount() {
		Path psubs = Path.of(getSurveyDir()+"subscribe/");
		File fsubs = psubs.toFile();
		if(!fsubs.exists()) fsubs.mkdirs();
		Path pcnt = Path.of(psubs+"/cnt.txt");
		File fcnt = pcnt.toFile();
		int cnt = 0;
		if(fcnt.exists()) {
			try {
				cnt = Integer.parseInt(Files.readString(pcnt, StandardCharsets.UTF_8));
			} catch(Exception z) {
				z.printStackTrace();
			}
		}
		cnt++;
		try {
			Files.writeString(pcnt, ""+cnt, StandardOpenOption.CREATE);
		} catch(Exception z) {
			z.printStackTrace();
		}
		return cnt;
	}

	//==================================================
	//=============== Email subscription BEGIN =========
	HttpHandler subscribe = new HttpHandler() {
		@Override
		public void handleRequest(final HttpServerExchange ex) throws Exception {
			if (ex.isInIoThread()) { ex.dispatch(this); return; }
			String ddtt = datefm.format(Calendar.getInstance().getTime());
			Builder builder = FormParserFactory.builder();
			final FormDataParser formDataParser = builder.build().createParser(ex);
			if (formDataParser == null) {
System.out.println("no form data");
				ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
				ex.getResponseSender().send(ht2);
			} else {
System.out.println("form data");
				ex.startBlocking();
				FormData formData = formDataParser.parseBlocking();
				String val = null, name=null, org=null, email=null;
				for (String data : formData) {
					val = null;
					for (FormData.FormValue formValue : formData.get(data)) {
						val = formValue.getValue();
						val = toUTF8(val);
					}
					if("name".equals(data)) {
						name = val;
					} else if("org".equals(data)) {
						org = val;
					} else if("email".equals(data)) {
						email = val;
					}
				}
				if(name!=null && org!=null && email!=null) {
					String semi = "https://cim.tueng.org/seminar-join.html";
					final String sub = "ตอบรับลงทะเบียนเข้าร่วมการสัมมนา CIM Study Forum";
					final String msg = "ตอบรับลงทะเบียนเข้าร่วมการสัมมนา CIM Study Forum\n"+
						"ชื่อผู้เข้าร่วม: "+ name +"\n"+
						"หน่วยงาน : "+ org + "\n"+
						"EMAIL : "+ email + "\n"+
						"ลิงค์เข้าสัมมนา : "+ semi +"\n"+
					"";

					String ts = datefm.format(Calendar.getInstance().getTime());
					String dd = ts.substring(0,8);
					String rdf = ""+
						"@prefix csf: <http://popiang.com/rdf/subscribe#> .\n"+
						"@prefix vo: <http://popiang.com/rdf/vo#> .\n"+
						"@prefix vp: <http://popiang.com/rdf/vp#> .\n"+
						"\n"+
						"csf:"+ts+" a vo:Subscribe ; \n"+
						"vp:name '"+ name + "' ; \n"+
						"vp:org '"+ org + "' ; \n"+
						"vp:email '"+ email +"' ; \n"+
						"vp:time '"+ datetm.format(Calendar.getInstance().getTime())+"' ; \n"+
						"vp:desc '''\n"+
						"''' .\n"+
					"";
					String surv = getSurveyDir()+"subscribe/"+dd+"/";
					File fsurv = new File(surv);
					if(!fsurv.exists()) fsurv.mkdirs();
//					String fn = surv+"subscribe-"+ts+".rdf";
					String fn = surv+"subscribe-"+ts+".txt";
					Path psub = Path.of(fn);
					try {
						Files.writeString(psub, rdf, StandardOpenOption.CREATE);
					} catch(Exception z) {
						z.printStackTrace();
					}
System.out.println(surv);
System.out.println(fn);
System.out.println(rdf);

					final Hashtable<String,List<String>> hsText = new Hashtable<>();
					int cnt = getCount();
					final String em=email, sb=sub, ms=msg;
					new Thread() {
						@Override
						public void run() {
							PopiangDigital.email.sendEMail(em, sb, ms, hsText);
						}} .start();
				}
				String rt1 = 
					"<head>\n"+
					"<title>ขอบคุณที่ลงทะเบียนเข้าร่วมสัมมนา</title>\n"+
					"<meta charset='UTF-8'>\n"+
					"<meta name='viewport' content='width=device-width, initial-scale=1.0'>\n"+
					"<link rel='stylesheet' href='https://unpkg.com/@picocss/pico@latest/css/pico.min.css'>\n"+
					"</head>\n"+
					"<body>\n"+
					"<div>\n"+
					"<h3>ขอบคุณที่ลงทะเบียนเข้าร่วมการสัมมนา CIM Study Forum</h3>\n"+
					"กรุณาเช็คอีเมล์ของท่าน (หากท่านใช้ gmail และไม่มีอีเมล์ส่งไปถึง ลองเช็คจดหมายขยะ หรือ SPAM)<br>\n"+
					"ท่านจะได้รับอีเมล์ข้อมูลรายละเอียดเกี่ยวกับงานสัมมนาตอบกลับอัตโนมัติ<br>\n"+
					"</div>\n"+
					"<div>\n"+
					"<a href='/'>กลับสู่หน้าหลัก</a>\n"+
					"</div>\n"+
				"";
				ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
				ex.getResponseSender().send(rt1);
			}
		}
	};
	//=============== Email subscription END =================
	//========================================================

	//==================================================
	//=============== Email survey BEGIN =========
	HttpHandler survey = new HttpHandler() {
		@Override
		public void handleRequest(final HttpServerExchange ex) throws Exception {
			if (ex.isInIoThread()) { ex.dispatch(this); return; }
			String ddtt = datefm.format(Calendar.getInstance().getTime());
			Builder builder = FormParserFactory.builder();
			final FormDataParser formDataParser = builder.build().createParser(ex);
			if (formDataParser == null) {
System.out.println("no form data");
				ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
				String ht3 = ht2.replace("CIM Study Forum", "DOING BUSINESS PORTAL");
				ex.getResponseSender().send(ht3);
			} else {
System.out.println("form data");
				ex.startBlocking();
				FormData formData = formDataParser.parseBlocking();
				String val = null, name=null, org=null, email=null;
				for (String data : formData) {
					val = null;
					for (FormData.FormValue formValue : formData.get(data)) {
						val = formValue.getValue();
						val = toUTF8(val);
					}
					if("name".equals(data)) {
						name = val;
					} else if("org".equals(data)) {
						org = val;
					} else if("email".equals(data)) {
						email = val;
					}
				}
				if(name!=null && org!=null && email!=null) {
					String semi = "https://dga.tueng.org/seminar-join.html";
					final String sub = "ตอบรับลงทะเบียนเข้าร่วมการสัมมนา";
					final String msg = "ตอบรับลงทะเบียนเข้าร่วมการสัมมนา\n"+
						"ชื่อผู้เข้าร่วม: "+ name +"\n"+
						"หน่วยงาน : "+ org + "\n"+
						"EMAIL : "+ email + "\n"+
						"ลิงค์เข้าสัมมนา : "+ semi +"\n"+
					"";

					String ts = datefm.format(Calendar.getInstance().getTime());
					String rdf = ""+
						"@prefix csf: <http://popiang.com/rdf/subscribe#> .\n"+
						"@prefix vo: <http://popiang.com/rdf/vo#> .\n"+
						"@prefix vp: <http://popiang.com/rdf/vp#> .\n"+
						"\n"+
						"csf:"+ts+" a vo:Subscribe ; \n"+
						"vp:name '"+ name + "' ; \n"+
						"vp:org '"+ org + "' ; \n"+
						"vp:email '"+ email +"' ; \n"+
						"vp:time '"+ datetm.format(Calendar.getInstance().getTime())+"' ; \n"+
						"vp:desc '''\n"+
						"''' .\n"+
					"";
					String dd = ts.substring(0,8);
					String surv = getSurveyDir()+"subscribe/"+dd+"/";
					File fsurv = new File(surv);
					if(!fsurv.exists()) fsurv.mkdirs();
//					String fn = surv+"subscribe-"+ts+".rdf";
					String fn = surv+"subscribe-"+ts+".txt";
					Path psub = Path.of(fn);
					try {
						Files.writeString(psub, rdf, StandardOpenOption.CREATE);
					} catch(Exception z) {
						z.printStackTrace();
					}
System.out.println(surv);
System.out.println(fn);
System.out.println(rdf);

					final Hashtable<String,List<String>> hsText = new Hashtable<>();
					int cnt = getCount();
					final String em=email, sb=sub, ms=msg;
					new Thread() {
						@Override
						public void run() {
							PopiangDigital.email.sendMail(em, sb, ms, hsText);
						}} .start();
				}
				String rt1 = 
					"<head>\n"+
					"<title>ขอบคุณที่ลงทะเบียนเข้าร่วมสัมมนา</title>\n"+
					"<meta charset='UTF-8'>\n"+
					"<meta name='viewport' content='width=device-width, initial-scale=1.0'>\n"+
					"<link rel='stylesheet' href='https://unpkg.com/@picocss/pico@latest/css/pico.min.css'>\n"+
					"</head>\n"+
					"<body>\n"+
					"<div>\n"+
					"<h3>ขอบคุณที่ลงทะเบียนเข้าร่วมการสัมมนา DOING BUSINESS PORTAL</h3>\n"+
					"ท่านจะได้รับอีเมล์ข้อมูลรายละเอียดเกี่ยวกับงานสัมมนา<br>\n"+
					"</div>\n"+
					"<div>\n"+
					"<a href='/'>กลับสู่หน้าหลัก</a>\n"+
					"</div>\n"+
				"";
				ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
				ex.getResponseSender().send(rt1);
			}
		}
	};
	//=============== Email Survey END =================
	//========================================================

	HttpHandler subscribeList = new HttpHandler() {
		String head = ""+
			"<head>\n"+
			"<title>Subscribe List</title>\n"+
			"<meta charset='UTF-8'>\n"+
			"<meta name='viewport' content='width=device-width, initial-scale=1.0'>\n"+
			"<link rel='stylesheet' href='https://unpkg.com/@picocss/pico@latest/css/pico.classless.min.css'>\n"+
			"</head>\n"+
		"";
		@Override
		public void handleRequest(final HttpServerExchange ex) throws Exception {
			StringBuffer pg = new StringBuffer();
			pg.append("<html>");
			pg.append(head);
			pg.append("<body>");
			String surv = getSurveyDir()+"subscribe/";
			File fsurv = new File(surv);
			String[] aDir = fsurv.list();
			List<String> lDir = new ArrayList<>();
			for(int i=0; i<aDir.length; i++) {
				if(aDir[i].length()!=8) continue;
				File fDir = new File(fsurv+"/"+aDir[i]);
				if(!fDir.isDirectory()) continue;
				lDir.add(aDir[i]);
System.out.println("       "+fDir+" : "+ fDir.exists());
			}

		try {

			String[] sDir = lDir.toArray(new String[lDir.size()]);
			Arrays.sort(sDir);
			Hashtable<String,String> hReg = new Hashtable<>();
			for(int i=0; i<sDir.length; i++) {
System.out.println("DIR: "+ sDir[i]);
				File fDay = new File(fsurv+"/"+sDir[i]);
				String[] regs = fDay.list();
				for(int j=0; j<regs.length; j++) {
					Path pth = Paths.get(surv, sDir[i], regs[j]);
System.out.println("  "+j+": "+ pth+" : "+ pth.toFile().exists());
					if(!pth.toFile().exists()) continue;
					Stream<String> sline = Files.lines(pth);
					List<String> lLine = sline.collect(Collectors.toList());
					if(lLine.size()<8) continue;
					int i1,i2;
					// 5:name 6:org 7:email 8:time
					String name = lLine.get(5);
					if((i1=name.indexOf("'"))>0 && (i2=name.indexOf("'",i1+2))>0) { name = name.substring(i1+1, i2); }
					String org = lLine.get(6);
					if((i1=org.indexOf("'"))>0 && (i2=org.indexOf("'",i1+2))>0) { org = org.substring(i1+1, i2); }
					String mail = lLine.get(7);
					if((i1=mail.indexOf("'"))>0 && (i2=mail.indexOf("'",i1+2))>0) { mail = mail.substring(i1+1, i2); }
					
					hReg.put(mail, name);
				}
			}
			for(Entry<String,String> ent : hReg.entrySet()) {
				pg.append("\n"+ent.getValue()+"&lt;"+ent.getKey()+"&gt;"+"<br>");
			}
		} catch(Exception x) {
			x.printStackTrace();
		}

			pg.append("</body></html>");
			ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
			ex.getResponseSender().send(pg.toString());

		}
	};

	HttpHandler upload = new HttpHandler() {

		protected Session getSession(HttpServerExchange exchange) {
			SessionConfig sc = exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
			SessionManager sm = exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
			if (sc == null || sm == null) {
				log.info("sc:"+sc+"  sm:"+sm);
				return null;
			}
			Session session = sm.getSession(exchange, sc);
			if (session == null) {
				session = sm.createSession(exchange, sc);
				List<String> list = new ArrayList<>();
				session.setAttribute("LIST", list);
			}
			return session;
		}

		String title = "แจ้งเบาะแส";

		String head = ""+
			"<head>\n"+
			"<title>แจ้งเบาะแส</title>\n"+
			"<meta charset='UTF-8'>\n"+
			"<meta name='viewport' content='width=device-width, initial-scale=1.0'>\n"+
//			"<link rel='stylesheet' href='https://unpkg.com/@picocss/pico@latest/css/pico.min.css'>\n"+
			"<link rel='stylesheet' href='https://unpkg.com/@picocss/pico@latest/css/pico.classless.min.css'>\n"+
//			"<link rel='stylesheet' href='https://unpkg.com/@picocss/pico@latest/css/pico.fluid.classless.min.css'>\n"+
			"</head>\n"+
		"";

		String bd1 = ""+
			"<h3><a href='/'>ความจริงจะปรากฏ</a></h3>\n"+
			"<h1>แจ้งเบาะแส</h1>\n"+
		"";

		String bd2 = ""+

			"<form action='/up' method='post' accept-charset='utf-8' enctype='multipart/form-data'>\n"+
			"<textarea name='word' rows='2' cols='50'></textarea>\n"+
			"<button type='submit' name='wordBT'>เพิ่มข้อความ</button>\n"+
			"<input type='file' name='pict'>\n"+
			"<button type='submit' name='pictBT'>เพิ่มรูป</button>\n"+
			"<button type='submit' name='clearBT'>เคลียทั้งหมด</button>\n"+
			"<button type='submit' name='finishBT'>ตัดสินใจแจ้งเบาะแส</button>\n"+
			"</form>\n"+

		"";

		@Override
		public void handleRequest(final HttpServerExchange ex) throws Exception {
			StringBuffer page0 = new StringBuffer();
			page0.append("<html>\n");
			page0.append(head);
			page0.append("<body>\n");

//			String doc = "UPLOAD";
			Session sess = getSession(ex);
			if (ex.isInIoThread()) {
				ex.dispatch(this);
				return;
			}

			List<String> list = (List<String>) sess.getAttribute("LIST");

			Builder builder = FormParserFactory.builder();
			final FormDataParser formDataParser = builder.build().createParser(ex);
			String ddtt = datefm.format(Calendar.getInstance().getTime());
			if (formDataParser == null) {
			} else {
				ex.startBlocking();
				FormData formData = formDataParser.parseBlocking();
				String val = null;
				for (String data : formData) {
					if("wordBT".equals(data)) {
						if(val!=null && val.length()>0) {
							list.add(val);
						}
					} else if("pictBT".equals(data)) {
						if(val!=null && val.length()>0 && val.startsWith("data:image")) {
							list.add(val);
						}
					} else if("clearBT".equals(data)) {
						list.clear();
					} else if("finishBT".equals(data)) {
						if(list.size()>0) {
							StringBuffer pg = new StringBuffer();
							pg.append("<html>\n");
							pg.append(head);
							pg.append("<body>\n");
							pg.append(bd1);
							for(int i=0; i<list.size(); i++) {
								String itm = list.get(i);
								if(itm.startsWith("data:image")) {
									pg.append("<div><img src='"+ itm + "'></div>");
								} else {
									pg.append("<div>"+itm+"</div>\n");
								}
							}
							pg.append("</body>\n");
							pg.append("</html>\n");

							try {
								File file = new File(page+"/"+ddtt+".html");
								BufferedWriter bw = new BufferedWriter(
									new OutputStreamWriter(new FileOutputStream(file)));
								bw.write(pg.toString());
								bw.close();
							} catch(Exception z) {
							}
							page0.append("<script>alert('ความจริงหมายเลข "+ddtt
								+" รอการกลั่นกรอง', 'ผู้กลั่นกรอง');\n"
								+ "location.href = '/';\n"
								+ "</script>\n");
						} else {
							page0.append("<script>"
								+ "location.href = '/';\n"
								+ "</script>\n");
						}
						list.clear();
					}
					for (FormData.FormValue formValue : formData.get(data)) {
						if (formValue.isFile()) {
							File file = formValue.getFile();
							try {
								BufferedImage bim = ImageIO.read(file);
								if(bim!=null) {
									int wd = bim.getWidth(null);
									int hg = bim.getHeight(null);
									int w0 = 400;
									int w1 = wd, h1 = hg;
									if(wd>w0) {
										w1 = wd * w0 / wd;
										h1 = hg * w0 / wd;
									}
									BufferedImage img1 = new BufferedImage(w1, h1
										, BufferedImage.TYPE_INT_RGB);
									Graphics2D g2 = img1.createGraphics();
									g2.drawImage(bim, 0,0,w1,h1, 0,0,wd,hg, null);
									g2.dispose();
									File fimg = new File("image.jpg");
									ImageIO.write(img1, "JPG", fimg);
									ByteArrayOutputStream baos = new ByteArrayOutputStream();
									ImageIO.write(img1, "JPG", baos);
									byte[] buff = baos.toByteArray();
									String imgs = "data:image/jpeg;base64," 
										+ DatatypeConverter.printBase64Binary(buff);
									val = imgs;
								}
							} catch(Exception z) {
								z.printStackTrace();
							}
							// process file here: formValue.getFile();
						} else {
							String txt = formValue.getValue();
							if(txt!=null && txt.length()>0) {
								byte[] bb = new byte[txt.length()];
								for(int i=0; i<bb.length; i++) {
									bb[i] = (byte) txt.charAt(i);
								}
								txt = new String(bb,"UTF-8");
								val = txt;
							}
						}
					}
				}
			}
			page0.append(bd1);
			for(int i=0; i<list.size(); i++) {
				String itm = list.get(i);
				if(itm.startsWith("data:image")) {
					page0.append("<img src='"+ itm + "'><br>");
				} else {
					page0.append("<p>"+itm+"</p>\n");
				}
			}
			page0.append(bd2);
			page0.append("</body>\n");
			page0.append("</html>\n");
			ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
			ex.getResponseSender().send(page0.toString());
		}
	};
	String toUTF8(String txt) {
		try {
			byte[] bb = new byte[txt.length()];
			for(int i=0; i<bb.length; i++) {
				bb[i] = (byte) txt.charAt(i);
			}
			return new String(bb,"UTF-8");
		} catch(Throwable z) {
			return null;
		}
	}

	Undertow server;
	HttpHandler fact = new HttpHandler() {
		@Override
		public void handleRequest(final HttpServerExchange ex) throws Exception {
//			File fact = new File(PopiangDigital.workDir+"/pg");
			File fact = new File(page);
			String[] list = fact.list();
			java.util.Arrays.sort(list, java.util.Collections.reverseOrder());
			StringBuffer buf = new StringBuffer();
			buf.append("<html><body>\n");
			for(int i=0; i<list.length; i++) {
				if(list[i].length()!=24) continue;
				buf.append("<a href='/"+list[i]+"'>"+list[i]+"</a><br>\n");
			}
			buf.append("</body></html>\n");
			ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
			ex.getResponseSender().send(buf.toString());
		}
	};

	String getSurveyDir() {
		String vid = PopiangDigital.sSurvey;
		String d2 = vid.substring(0,vid.indexOf(":"));
		String d1 = d2.substring(0,d2.length()-2);
		String d3 = vid.replace(":","-");
		String dox = PopiangDigital.workDir + "/res/"+ d1+"/"+d2+"/"+d3+"/";
System.out.println("SURVEY DIR: "+ dox);
		return dox;
	}

	HttpHandler handa = new HttpHandler() {
		@Override
		public void handleRequest(final HttpServerExchange ex) throws Exception {
			String did = ex.getRelativePath();
			String rt = "?";
			String tp = "text/html";
			int i1;
			if (ex.isInIoThread()) { ex.dispatch(this); return; }
			if(!did.startsWith("/")) {
				rt = "NOT START WITH /"+did;
				ex.getResponseHeaders().put(Headers.CONTENT_TYPE, tp);
				ex.getResponseSender().send(rt);
			} else if((i1=did.indexOf("-"))>0) {
				String dir = did.substring(1,i1);
				String fid = did.substring(i1+1);
				if(fid.endsWith(".pdf") && fid.length()==7) {
					fid = fid.substring(0,3);
					String dox = getSurveyDir();
					String d4 = dox+dir+"/"+dir+"-"+fid+"/"+dir+"-"+fid+".pdf";
					File fPdf = new File(d4);
System.out.println("file: "+ fPdf+" : "+ fPdf.exists());
					rt = d4+" : "+ fPdf.exists();
					if(fPdf.exists()) {
						ex.startBlocking();
						tp = "application/pdf";
						ex.getResponseHeaders().put(Headers.CONTENT_TYPE, tp);
						final OutputStream outputStream = ex.getOutputStream();
						final InputStream inputStream = new FileInputStream(fPdf);
						byte[] buf = new byte[8192];
						int c;
						while ((c = inputStream.read(buf, 0, buf.length)) > 0) {
						    outputStream.write(buf, 0, c);
						    outputStream.flush();
						}
						outputStream.close();
						inputStream.close();
					} else {
						rt = "NO FILE1: "+did;
						ex.getResponseHeaders().put(Headers.CONTENT_TYPE, tp);
						ex.getResponseSender().send(rt);
					}
				} else if(fid.length()!=3) {
					rt = "ID IS WRONG: "+did;
					ex.getResponseHeaders().put(Headers.CONTENT_TYPE, tp);
					ex.getResponseSender().send(rt);
				} else {
					String vid = PopiangDigital.sSurvey;
					String d2 = vid.substring(0,vid.indexOf(":"));
					String d1 = d2.substring(0,d2.length()-2);
					String d3 = vid.replace(":","-");
					String dox = PopiangDigital.workDir + "/res/"+ d1+"/"+d2+"/"+d3+"/"+dir;
					String d4 = dox+"/"+dir+"-"+fid+"/"+dir+"-"+fid+".pdf";
					File fPdf = new File(d4);
					rt = d4+" : "+ fPdf.exists();
					if(fPdf.exists()) {
						rt = "HAS FILE";
					} else {
						rt = "NO FILE2: "+did;
					}
					ex.getResponseHeaders().put(Headers.CONTENT_TYPE, tp);
					ex.getResponseSender().send(rt);
				}
			}
		}
	};

	HttpHandler hand = new HttpHandler() {
		@Override
		public void handleRequest(final HttpServerExchange ex) throws Exception {
			File file = new File(html+"/view360.html");
			Deque<String> res = ex.getQueryParameters().get("path");
			if(file.exists() && res.size()>0) {
				String url = "http://localhost:8080/res/"+res.getFirst();
log.info("res: "+url);
				String doc = new String(Files.readAllBytes(
					Paths.get(html+"/view360.html")),"UTF-8");
				ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
//				String jpg = "http://localhost:8080/res/dnm/dnm0S/dnm0S-2.jpeg";
				doc = doc.replace("___JPEG___",url);
				ex.getResponseSender().send(doc);
			} else {
				ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/text");
				ex.getResponseSender().send("Hello World");
			}
		}
	};

	String respath;
	String html;
	String page;

	public void startServer(String addr, int port) {
		respath = PopiangDigital.workDir+"/res";
		page = PopiangDigital.workDir+"/pg";
		if(PopiangDigital.sWeb!=null) {
			if(PopiangDigital.sWeb.startsWith("/")) {
				page = PopiangDigital.sWeb;
			} else {
				page = PopiangDigital.workDir+"/"+PopiangDigital.sWeb;
			}
		}
System.out.println("WebDir PAGE: "+ page);
		html = respath+"/html";

		PathHandler path = Handlers.path();

		SessionManager sessionManager = new InMemorySessionManager("SESSION_MANAGER");
		SessionCookieConfig sessionConfig = new SessionCookieConfig();
		SessionAttachmentHandler sessionAttachmentHandler = 
			new SessionAttachmentHandler(sessionManager, sessionConfig);
		sessionAttachmentHandler.setNext(path);

		path.addPrefixPath("/up", upload);
		path.addPrefixPath("/srv", hand);
		path.addPrefixPath("/D", handa);
		path.addPrefixPath("/fact", fact);
		path.addPrefixPath("/subscribe", subscribe);
		path.addPrefixPath("/subscribeList", subscribeList);
		path.addPrefixPath("/survey", survey);
		path.addPrefixPath("/res", resource(
			new PathResourceManager(Paths.get(respath), 100))
				.setDirectoryListingEnabled(true));
		access = new WebDataAccessHandler();
		path.addPrefixPath("/acc", access);

		// static web page at root '/'
		PathResourceManager rt = new PathResourceManager(Paths.get(page), 100) {
			@Override
			public Resource getResource(String p) {
				String ddtt = datefm.format(Calendar.getInstance().getTime());
				log.info(ddtt+": "+ p);
				return super.getResource(p);
			}
		};
		path.addPrefixPath("/", resource(rt)
					.setDirectoryListingEnabled(true));

//				new PathResourceManager(Paths.get(page), 100))

		server = Undertow.builder()
//			.addHttpListener(port, "0.0.0.0")
			.addHttpListener(port, addr)
			.setHandler(path)
			.setHandler(sessionAttachmentHandler).build();
		server.start();
	}
}

