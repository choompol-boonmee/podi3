package popdig;

import io.undertow.Undertow;
import io.undertow.server.handlers.resource.PathResourceManager;
import java.nio.file.Paths;
import static io.undertow.Handlers.resource;
import io.undertow.server.*;
import io.undertow.util.*;
import io.undertow.server.handlers.PathHandler;
import io.undertow.Handlers;
import java.util.Calendar;
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
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import io.undertow.util.MimeMappings;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.query.*;
import org.apache.commons.io.FileUtils;

public class WebDataAccessHandler implements HttpHandler {
	boolean bInit = false;
	Map<String,String> hOrg;
	String sSub;
	String nmsp = "http://dga.tueng.org/rdf/or#";
	SimpleDateFormat datefm = new SimpleDateFormat("yyyyMMddHHmmssSSS");

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

	void init() {
		if(bInit) return;
		try {
			sSub = PopiangDigital.sSub;
			String orf = sSub+"/rdf/org.ttl";
			Model mo = ModelFactory.createDefaultModel();
			mo.read(new FileInputStream(orf), null, "TTL");
			String sqry = ""
+"PREFIX org:        <http://www.w3.org/ns/org#> "
+"PREFIX rdfs:       <http://www.w3.org/2000/01/rdf-schema#> "
+"PREFIX or: <http://dga.tueng.org/rdf/dnm1G#> "
+"SELECT ?a ?b WHERE { ?a rdfs:label ?b . }"
+"";
			hOrg = new Hashtable<String,String>();
			List<Map<String,String>> aMap = PopiangUtil.sparql0(mo, sqry);
			for(int i=0; i<aMap.size(); i++) {
				Map<String,String> hM = aMap.get(i);
				hOrg.put(hM.get("a"), hM.get("b"));
			}
			
			bInit = true;
		} catch(Exception z) {
			z.printStackTrace();
		}
	}

	void readValue(HttpServerExchange ex) throws Exception {

		String orgid = "";
		for(Entry<String,Deque<String>> ent : ex.getQueryParameters().entrySet()) {
			String k = ent.getKey();
			String v = ent.getValue().getFirst();
			if(k.equals("orid")) {
				orgid = v;
				break;
			}
		}
		System.out.println("READING: "+orgid);
		File fDir = new File(sSub+"/data/"+orgid);
		String ln = "";
		if(fDir.exists()) {
			File[] flist = fDir.listFiles();
			for(int i=0; i<flist.length; i++) {
				File fDat = new File(flist[i]+"/value.txt");
				String name = flist[i].getName();
				if(!fDat.exists()) continue;
				Path filePath = Path.of(fDat.getAbsolutePath());
				String content = Files.readString(filePath);
				content = content.trim();
//System.out.println(i+": "+ fDat+" : "+content+" name:"+ name);
//System.out.println("     "+name+" = ["+ content+"]");
				if(ln.length()>0) ln += "\n";
				ln += name+"="+content;
			}
		}
		ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
		ex.getResponseSender().send(ln);
	}

	public String reserveAccess(String email, String orid0, String name, String time, String org) {
		init();
		try {
			orid0 = orid0.toLowerCase();
			System.out.println("RESERV ACCESS FOR: "+ email + " for "+ orid0);
			Path filePath = Path.of(sSub+"/temp/orgreq-yes.txt");
			String content = Files.readString(filePath);

			File access = new File(sSub+"/access");
//			String tok = datefm.format(Calendar.getInstance().getTime());
			Calendar c = Calendar.getInstance();
			String tok = datefm.format(c.getTime());
			if(time.equals("3H")) {
				c.add(Calendar.HOUR, 3);
			} else if(time.equals("1D")) {
				c.add(Calendar.DATE, 1);
			} else if(time.equals("7D")) {
				c.add(Calendar.DATE, 7);
			} else if(time.equals("1M")) {
				c.add(Calendar.MONTH, 1);
			} else {
				c.add(Calendar.DATE, 1);
			}
			String end = datefm.format(c.getTime());

//			Calendar ttt = Calender.getInstance();
//			String tok = datefm.format(now.getTime());

			if(!access.exists()) access.mkdirs();

			String oid = orid0.substring(orid0.lastIndexOf("#")+1);
			String link = "https://dga.tueng.org/acc/"+tok+"/sv-"+oid+".html";
			String test = "http://localhost:6002/acc/"+tok+"/sv-"+oid+".html";

			FileOutputStream fos = new FileOutputStream(sSub+"/access/"+tok+".ttl");
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));
			pw.println("@prefix ac: <http://dga.tueng.org/rdf/ac#> .");
			pw.println("@prefix vp: <http://dga.tueng.org/rdf/vp#> .");
			pw.println("");
			pw.println("ac:"+tok+"  a  vp:Access ;");
			pw.println("vp:orgId  <"+orid0+"> ;");
			pw.println("vp:orgName  '"+ hOrg.get(orid0)+ "' ;");
			pw.println("vp:editor   '"+ name + "' ;");
			pw.println("vp:editorOrg   '"+ org + "' ;");
			pw.println("vp:email    '"+email+"' ;");
			pw.println("vp:time     '"+time+"' ;");
			pw.println("vp:start     '"+tok+"' ;");
			pw.println("vp:end     '"+end+"' ;");
			pw.println("vp:link		'"+link+"' .");
			pw.println("");
			pw.flush();
			pw.close();

			File fsrc = new File(sSub+"/access/"+tok+".ttl");
System.out.println("FTOK: "+ fsrc);
			File fdir = new File(sSub+"/auth/"+tok.substring(0,8));
			File fdst = new File(sSub+"/auth/"+tok.substring(0,8)+"/"+tok+".ttl");
			if(!fdir.exists()) fdir.mkdirs();
			FileUtils.copyFile(fsrc, fdst);

System.out.println("==== orid0: "+ orid0+" : "+ hOrg.get(orid0));
			content = content.replace("___ORGID___", orid0);
			content = content.replace("___ORGNAME___", hOrg.get(orid0));
			content = content.replace("___EDITOR___", name);
			content = content.replace("___ORG___", org);
			content = content.replace("___EMAIL___", email);
			content = content.replace("___TIME___", time);
			content = content.replace("___EDITLINK___", link);
			content = content.replace("___TESTLINK___", test);

			return content;
		} catch(Exception z) {
			z.printStackTrace();
		}
		return "";
	}

	boolean access(HttpServerExchange ex) throws Exception {
		String rel = ex.getRelativePath();
//System.out.println("1."+rel);
		if(!rel.startsWith("/")) return false;
//System.out.println("2."+rel);
		int i1 = rel.indexOf("/", 1);
		if(i1!=18) return false;
		String tok = rel.substring(1,i1);
		String pth = rel.substring(i1);
		File ftok = new File(sSub+"/access/"+tok+".ttl");

		System.out.println("TOK: "+ tok);
		System.out.println("ftok: "+ ftok+ " : "+ ftok.exists());
		System.out.println("ACCESS: "+ pth);

		if(ftok.exists()) {
			Model mo = ModelFactory.createDefaultModel();
			mo.read(new FileInputStream(ftok), null, "TTL");
			String sqry = ""
+"PREFIX ac:       <http://dga.tueng.org/rdf/ac#> "
+"PREFIX vp:       <http://dga.tueng.org/rdf/vp#> "
+"SELECT ?a ?b ?c ?start ?end ?email WHERE {"
+" ?a a ?b . "
+" ?a vp:orgId ?c . "
+" ?a vp:start  ?start . "
+" ?a vp:email  ?email . "
+" ?a vp:end  ?end . "
+"}";
			List<Map<String,String>> aMap = PopiangUtil.sparql0(mo, sqry);
			if(aMap.size()!=1) return false;
			String start = aMap.get(0).get("start");
			String end = aMap.get(0).get("end");
			String eml = aMap.get(0).get("email");
			String oid = aMap.get(0).get("c");
			String curr = datefm.format(Calendar.getInstance().getTime());
			if( (i1=oid.lastIndexOf("/"))<=0 ) return false;
			String oid0 = oid.substring(i1+1).replace("#","-");
			System.out.println("ORGID: "+ oid);
			System.out.println("ORGID: "+ oid0);
//			System.out.println("START: "+ start);
//			System.out.println("CURRE: "+ curr);
//			System.out.println("EXPIR: "+ end);
//			System.out.println("DIFF: "+ curr.compareTo(end));

			if(curr.compareTo(end)>0) {
				System.out.println("ลบสิทธิ์: "+ ftok.getAbsolutePath());
				ftok.delete();
			}

			if (ex.isInIoThread()) { ex.dispatch(this); return true; }
			ex.startBlocking();

			File f = new File(sSub+pth);
			if(f.exists()) {
				Path filePath = Path.of(sSub+pth);
				String content = Files.readString(filePath);
				content = content.replace("___EMAIL___", PopiangDigital.sRecvEmail);
				MimeMappings mimap = MimeMappings.DEFAULT;
				String type = mimap.getMimeType(rel.substring(rel.lastIndexOf(".")+1));
				ex.getResponseHeaders().put(Headers.CONTENT_TYPE, type);
				ex.getResponseSender().send(content);
			} else {
//System.out.println("===== PATH: "+ pth);
				if(pth.equals("/save")) {
					String res = "";
					String orgid = "";
					List<String> aK = new ArrayList<>();
					List<String> aV = new ArrayList<>();
					for(Entry<String,Deque<String>> ent : ex.getQueryParameters().entrySet()) {
						String k = ent.getKey();
						String v = ent.getValue().getFirst();
						if(k.equals("orid")) {
							orgid = v;
						}
						if(!k.startsWith("sv")) continue;
						aK.add(k);
						aV.add(v);
						if(res.length()>0) res+="\n";
						res += k + "=" + v;
					}
					if(!oid0.equals(orgid)) {
						ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
						ex.getResponseSender().send("");
						return true;
					}
System.out.println("SAVE: "+ tok);
System.out.println("TIME: "+ curr);
					for(int i=0; i<aK.size(); i++) {
						String sv = aK.get(i);
						String va = aV.get(i);
//System.out.println("or0:"+ oid0+" or:"+ orgid+" sv:"+aK.get(i)+" va:"+aV.get(i));
//						if(!oid0.equals(orgid)) continue;
						File fDir = new File(sSub+"/data/"+orgid+"/"+sv+"/"+curr.substring(0,8));
						if(!fDir.exists()) fDir.mkdirs();
						File fDat = new File(sSub+"/data/"+orgid+"/"+sv+"/"+curr.substring(0,8)+"/"+curr+".ttl");
System.out.println("fDir: "+ fDir);
						File fVal = new File(sSub+"/data/"+orgid+"/"+sv+"/value.txt");

						FileOutputStream fos = new FileOutputStream(fDat);
						PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));
						pw.println("@prefix dt: <http://dga.tueng.org/rdf/dt#> .");
						pw.println("@prefix ac: <http://dga.tueng.org/rdf/ac#> .");
						pw.println("@prefix vp: <http://dga.tueng.org/rdf/vp#> .");
						pw.println("");
						pw.println("dt:"+curr+"  a  vp:Data ;");
						pw.println("vp:token   <ac:"+tok+"> ;");
						pw.println("vp:email   '"+eml+"' ;");
						pw.println("vp:service   '"+ sv + "' ;");
						pw.println("vp:value  '"+ va + "' .");
						pw.println("");
						pw.flush();
						pw.close();

						File fsrc = fDat;
						File fdir = new File(sSub+"/action/"+curr.substring(0,8));
						File fdst = new File(sSub+"/action/"+curr.substring(0,8)+"/"+orgid+"_"+sv+"_"+curr+".ttl");
						if(!fdir.exists()) fdir.mkdirs();
						FileUtils.copyFile(fsrc, fdst);

						fos = new FileOutputStream(fVal);
						pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));
						pw.println(va);
						pw.close();

					}
					ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
					ex.getResponseSender().send(res);
				} else if(pth.equals("/read")) {
					readValue(ex);
/*
					String orgid = "";
					for(Entry<String,Deque<String>> ent : ex.getQueryParameters().entrySet()) {
						String k = ent.getKey();
						String v = ent.getValue().getFirst();
						if(k.equals("orid")) {
							orgid = v;
							break;
						}
					}
					System.out.println("READING: "+orgid);
					File fDir = new File(sSub+"/data/"+orgid);
					String ln = "";
					if(fDir.exists()) {
						File[] flist = fDir.listFiles();
						for(int i=0; i<flist.length; i++) {
							File fDat = new File(flist[i]+"/value.txt");
							String name = flist[i].getName();
							if(!fDat.exists()) continue;
							Path filePath = Path.of(fDat.getAbsolutePath());
							String content = Files.readString(filePath);
							content = content.trim();
System.out.println(i+": "+ fDat+" : "+content+" name:"+ name);
System.out.println("     "+name+" = ["+ content+"]");
							if(ln.length()>0) ln += "\n";
							ln += name+"="+content;
						}
					}
					ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
					ex.getResponseSender().send(ln);
*/
				}
			}
			return true;
		} else {
			return false;
		}
	}

	boolean accessRequest(HttpServerExchange ex) throws Exception {
		String rel = ex.getRelativePath();
		if(rel.startsWith("/editreq/")) {
			String orid = rel.substring(9);
			if(orid.startsWith("or-")) {
				String orid0 = nmsp+orid.substring(3);
				String ornm = hOrg.get(orid0);

				if (ex.isInIoThread()) { ex.dispatch(this); return true; }
				String ddtt = datefm.format(Calendar.getInstance().getTime());
				Builder builder = FormParserFactory.builder();
				final FormDataParser formDataParser = builder.build().createParser(ex);
				if (formDataParser == null) {
					Path filePath = Path.of(sSub+"/temp/orgreg.html");
					String content = Files.readString(filePath);
					content = content.replace("___ORGID___", orid0);
					content = content.replace("___ORGNAME___", ornm);
					content = content.replace("___RECVEMAIL___", PopiangDigital.sRecvEmail);
					content = content.replace("___SUBJECT___", "EDIT="+orid0);
					ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
					ex.getResponseSender().send(content);
				} else {
					ex.startBlocking();
					FormData formData = formDataParser.parseBlocking();
					String val = null, name=null, org=null, email=null, time=null;
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
						} else if("time".equals(data)) {
							time = val;
						}
					}
					if(name!=null && org!=null && email!=null && time!=null) {
						Path filePath = Path.of(sSub+"/temp/orgreq-yes.html");
						String content = Files.readString(filePath);
						content = content.replace("___ORGID___", orid0);
						content = content.replace("___ORGNAME___", ornm);
						content = content.replace("___EDITOR___", name);
						content = content.replace("___ORG___", org);
						content = content.replace("___EMAIL___", email);
						content = content.replace("___TIME___", time);
						content = content.replace("___RECVEMAIL___", PopiangDigital.sRecvEmail);
						content = content.replace("___SUBJECT___", "EDIT="+orid0);

						ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
						ex.getResponseSender().send(content);

						content = reserveAccess(email, orid0, name, time, org);
			
						final Hashtable<String,List<String>> hsText = new Hashtable<>();
						final String em=email, sb="ลิงค์แก้ไขข้อมูล "+ornm, ms=content;
						new Thread() {
							@Override
							public void run() {
								PopiangDigital.email.sendEMail(em, sb, ms, hsText);
							}} .start();

					} else {
						Path filePath = Path.of(sSub+"/temp/orgreq-no.html");
						String content = Files.readString(filePath);
						ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
						ex.getResponseSender().send(content);
					}
				}

				return true;
			}
		}
		return false;
	}
	boolean sendFile(HttpServerExchange ex) throws Exception {
		String rel = ex.getRelativePath();
		String sub = PopiangDigital.sSub;
		String pth = sub+rel;
		File file = new File(pth);
		if(file.exists()) {
			if (ex.isInIoThread()) { ex.dispatch(this); return true; }
			ex.startBlocking();
			Path filePath = Path.of(pth);
			String content = Files.readString(filePath);
			content = content.replace("___EMAIL___", PopiangDigital.sRecvEmail);
			MimeMappings mimap = MimeMappings.DEFAULT;
			String type = mimap.getMimeType(rel.substring(rel.lastIndexOf(".")+1));
			ex.getResponseHeaders().put(Headers.CONTENT_TYPE, type);
			ex.getResponseSender().send(content);
			return true;
		}
		return false;
	}
	@Override
	public void handleRequest(final HttpServerExchange ex) throws Exception {
		init();
		if(sendFile(ex)) return;
		if(accessRequest(ex)) return;
		if(access(ex)) return;
		String rel = ex.getRelativePath();
		if(rel.startsWith("/read")) readValue(ex);

		ex.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
		ex.getResponseSender().send("<script>window.location.href='/';</script>");
	}
}


