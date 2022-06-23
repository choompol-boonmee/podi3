package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Map;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.geom.*;
import java.awt.Shape;
import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.text.*;
import java.awt.Desktop;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;
import java.util.Arrays;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.multipdf.PDFCloneUtility;
import org.apache.pdfbox.pdmodel.interactive.annotation.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.rendering.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataSetHtml extends ReportExecute {
	String vo = "vo:DataSetReport";
	String fileref = "ชื่อไฟล์อิเล็กทรอนิกส์";

	public DataSetHtml(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
		initGen();
	}
	int recsCnt = 0;
	boolean bSub = true;
	Map<String,String> hLabel = new HashMap<>();

	static class ReportGen {
		String gen(Map<String,String> hP) { return ""; }
	}

	Map<String,ReportGen> hGen;

	String[][] tags = {
		{"pdfOrg","เอกสารออกแบบ" },
		{"pdfCln","เอกสารเปล่า" },
		{"rsmPic","ออกแบบCCTS" },
		{"sendPic","UMM การส่งข้อมูล" },
		{"reqPic","UMM การขอข้อมูล" },
		{"rsmXml","ข้อมูลXML" },
		{"rsmJson","ข้อมูล JSON" },
		{"rsmJsLD","ข้อมูล JSON-LD" },
		{"rsmCsv","ข้อมูล CSV" },
		{"rsmXsd","ข้อมูล XMLSchema" },
		{"wsdlSend","WSDL ข้อกำหนดการส่งข้อมูล"  },
		{"wsdlQuery","WSDL ข้อกำหนดการขอข้อมูล" },
		{"oaiSend","OpenAPI ส่งข้อมูล" },
		{"oaiQuery","OpenAPI ขอข้อมูล" },
		{"oaiKey","OpenAPI ApiKey" },
		{"oaiAuth","OpenAPI OAuth2" },
		{"webJsLD","JSON-LD : มาตรฐานข้อมูลบนเว็บ" },
		{"webRDFa","RDFa : มาตรฐานข้อมูลบนเว็บ" },
		{"webMicDT","MicDT : มาตรฐานข้อมูลบนเว็บ" },
		{"docDSIG","ลายมือชื่อ XMLSignature" },
		{"docENC","XMLEncryption รักษาความลับ" },
	};
	String[][] tags2 = {
		{"fileCsv","CSV" },
		{"fileJson","JSON" },
		{"fileXml","XML" },
	};
	String[][] tags3 = {
		{"pict","CCL ABIE" },
		{"ramXsd","XMLShema" },
	};

	void copy(File fin, File fou) {
		byte[] buf = new byte[8*1024];
		int l;
		try {
			FileInputStream fis = new FileInputStream(fin);
			FileOutputStream fos = new FileOutputStream(fou);
			while((l=fis.read(buf,0,buf.length))>0) { fos.write(buf,0,l); }
			fos.close();
			fis.close();
		} catch(Exception z) {
			z.printStackTrace();
		}
	}

	String genA(Map<String,String> hM) {

		StringBuffer html = new StringBuffer();
		QueryExecute q0;

		String param = hM.get("vp:param");
System.out.println("param: "+ param);
		q0 = PopiangUtil.query0(param
			+" vp:param(*) ?a. ?a vp:prod ?pd; vp:label ?lb;vp:note ?nt.");
		String[] pds = q0.gets("?pd");
		String[] lbs = q0.gets("?lb");
		String[] nts = q0.gets("?nt");
		for(int i=0; i<pds.length; i++) {
			System.out.println("  "+i+":"+pds[i]+", "+lbs[i]+", "+nts[i]);
		}
		
		String srcNm = src.replace(":","-");
		String srcDir = src.substring(0,src.indexOf(":"));
		String prf = srcDir.substring(0,srcDir.length()-2);
		System.out.println("gen A: "+prf+" ds:"+srcDir+" nm:"+srcNm);
		try {
			//=================== DOCUMENT BEGIN 
			q0 = PopiangUtil.query0(src+" vp:documents(*) ?a . ?a ?b ?c .");
			String[] aa = q0.gets("?a");
			String[] bb = q0.gets("?b");
			String[] cc = q0.gets("?c");
			List<String> aI = new ArrayList<>();
			Map<String,Map<String,String>> hIPO = new HashMap<>();
			for(int i=0; i<bb.length; i++) {
				Map<String,String> hPO = hIPO.get(aa[i]);
				if(hPO==null) {
					hPO = new HashMap<>();
					hIPO.put(aa[i],hPO);
					aI.add(aa[i]);
				}
				hPO.put(bb[i], cc[i]);
			}
			for(String a : aI) {
				Map<String,String> hPO = hIPO.get(a);
				String name = hPO.get("vp:name");
				String den = hPO.get("vp:DEN");
//				System.out.println("name: "+ name+ " den:"+den);
				html.append("<hr>\n");
				html.append("<b>"+name+"</b><br>\n");
				for(String[] tag : tags) {
					String f1 = hPO.get("vp:"+tag[0]);
					String in = f1.substring(5);
					String nm = f1.substring(f1.lastIndexOf("/")+1);
					String ou = sPjDir+"/"+nm;
//					System.out.println("in:"+in+" ou:"+ou);
					File fin = new File(PopiangDigital.workDir+"/res/"+in);
					File fou = new File(PopiangDigital.workDir+"/res/"+ou);
					copy(fin,fou);
					html.append("&nbsp;&nbsp;&nbsp; <a href='"+nm+"'>"+tag[1]+"</a><br>\n");
				}
			}
			html.append("<hr>\n");
			//=================== DOCUMENT END
 

			//=================== QDTLIST BEGIN 
			q0 = PopiangUtil.query0(src+" vp:qdtList(*) ?a . ?a ?b ?c .");
			aa = q0.gets("?a");
			bb = q0.gets("?b");
			cc = q0.gets("?c");
			aI = new ArrayList<>();
			hIPO = new HashMap<>();
			for(int i=0; i<bb.length; i++) {
				Map<String,String> hPO = hIPO.get(aa[i]);
				if(hPO==null) {
					hPO = new HashMap<>();
					hIPO.put(aa[i],hPO);
					aI.add(aa[i]);
				}
				hPO.put(bb[i], cc[i]);
			}
			html.append("<hr>\n");
			html.append("CODE LIST\n");
			html.append("<hr>\n");
			for(String a : aI) {
				Map<String,String> hPO = hIPO.get(a);
				String name = hPO.get("vp:name");
				String den = hPO.get("vp:DEN");
//				System.out.println("name: "+ name+ " den:"+den);
				html.append("<hr>\n");
				html.append("<b>"+name+"</b><br>\n");
				for(String[] tag : tags2) {
					String f1 = hPO.get("vp:"+tag[0]);
					if(f1==null) continue;
					String in = f1.substring(5);
					String nm = f1.substring(f1.lastIndexOf("/")+1);
					String ou = sPjDir+"/"+nm;
//					System.out.println("in:"+in+" ou:"+ou);
					File fin = new File(PopiangDigital.workDir+"/res/"+in);
					File fou = new File(PopiangDigital.workDir+"/res/"+ou);
					copy(fin,fou);
					html.append("&nbsp;&nbsp;&nbsp; <a href='"+nm+"'>"+tag[1]+"</a><br>\n");
				}
			}
			html.append("<hr>\n");
			//=================== QDTLIST END

			//=================== REFERENCE BEGIN 
			q0 = PopiangUtil.query0(src+" vp:reference(*) ?a . ?a ?b ?c .");
			aa = q0.gets("?a");
			bb = q0.gets("?b");
			cc = q0.gets("?c");
			aI = new ArrayList<>();
			hIPO = new HashMap<>();
			for(int i=0; i<bb.length; i++) {
				Map<String,String> hPO = hIPO.get(aa[i]);
				if(hPO==null) {
					hPO = new HashMap<>();
					hIPO.put(aa[i],hPO);
					aI.add(aa[i]);
				}
				hPO.put(bb[i], cc[i]);
			}
			html.append("<hr>\n");
			html.append("ABIE\n");
			html.append("<hr>\n");
			for(String a : aI) {
				Map<String,String> hPO = hIPO.get(a);
				String name = hPO.get("vp:name");
				String den = hPO.get("vp:DEN");
//				System.out.println("name: "+ name+ " den:"+den);
				html.append("<hr>\n");
				html.append("<b>"+den+"</b><br>\n");
				for(String[] tag : tags3) {
					String f1 = hPO.get("vp:"+tag[0]);
					if(f1==null) continue;
					String in = f1.substring(5);
					String nm = f1.substring(f1.lastIndexOf("/")+1);
					String ou = sPjDir+"/"+nm;
//					System.out.println("in:"+in+" ou:"+ou);
					File fin = new File(PopiangDigital.workDir+"/res/"+in);
					File fou = new File(PopiangDigital.workDir+"/res/"+ou);
					copy(fin,fou);
					html.append("&nbsp;&nbsp;&nbsp; <a href='"+nm+"'>"+tag[1]+"</a><br>\n");
				}
			}
			html.append("<hr>\n");
			//=================== QDTLIST END

		} catch(Exception z) {
			z.printStackTrace();
		}
		return html.toString();
	}

	String genB(Map<String,String> hM) {
		QueryExecute q0;
		StringBuffer html = new StringBuffer();
		String param = hM.get("vp:param");
		String label = hM.get("vp:label");
		String prop = hM.get("vp:prop");
		System.out.println("prop: "+ prop);

		title(label);
		html.append("<table style='width:100%'> <tr> <td>"
			+ "<div style='background-color:green; width:100%'> <center>"
			+ "<h2 style='color:white;'>"
			+ label
			+ " </h2> </center> </div> </td> </tr> </table>");

		q0 = PopiangUtil.query0(param
			+" vp:param(*) ?a. ?a vp:prod ?pd; vp:label ?lb;vp:note ?nt.");
		String[] pds = q0.gets("?pd");
		String[] lbs = q0.gets("?lb");
		String[] nts = q0.gets("?nt");
		
		String srcNm = src.replace(":","-");
		String srcDir = src.substring(0,src.indexOf(":"));
		String prf = srcDir.substring(0,srcDir.length()-2);
		try {
			//=================== DOCUMENT BEGIN 
			q0 = PopiangUtil.query0(src+" "+prop+"(*) ?a . ?a ?b ?c .");
			String[] aa = q0.gets("?a");
			String[] bb = q0.gets("?b");
			String[] cc = q0.gets("?c");
			List<String> aI = new ArrayList<>();
			Map<String,Map<String,String>> hIPO = new HashMap<>();
			for(int i=0; i<bb.length; i++) {
				Map<String,String> hPO = hIPO.get(aa[i]);
				if(hPO==null) {
					hPO = new HashMap<>();
					hIPO.put(aa[i],hPO);
					aI.add(aa[i]);
				}
				hPO.put(bb[i], cc[i]);
			}
			for(String a : aI) {
				Map<String,String> hPO = hIPO.get(a);
				String name = hPO.get("vp:name");
				String den = hPO.get("vp:DEN");
				if(name==null) name = den;
//				System.out.println("name: "+ name+ " den:"+den);

				if(name.matches("[0-9]+\\..*")) name = name.substring(name.indexOf(".")+1);

				int cnt = 0;
				for(int i=0; i<pds.length; i++) {
					String pd = pds[i];
					if(!pd.startsWith("vp:")) continue;
					pd = pd.substring(3);
					String lb = lbs[i];
					String f1 = hPO.get("vp:"+pd);
					if(f1==null) continue;
					cnt++;
				}
				if(cnt==0) continue;

				html.append("<hr>\n");
				html.append("<b>"+name+"</b><br>\n");
				page1(name);

				for(int i=0; i<pds.length; i++) {
					String pd = pds[i];
					if(!pd.startsWith("vp:")) continue;
					pd = pd.substring(3);
					String lb = lbs[i];
					String f1 = hPO.get("vp:"+pd);
					if(f1==null) continue;
					String in = f1.substring(5);
					String nm = f1.substring(f1.lastIndexOf("/")+1);
					String ou = sPjDir+"/"+nm;
					File fin = new File(PopiangDigital.workDir+"/res/"+in);
					File fou = new File(PopiangDigital.workDir+"/res/"+ou);
					copy(fin,fou);

					para2(lb);
					int maxline = 40;
					if(nm.endsWith(".pdf")) {
						try {
							PDDocument pddoc = PDDocument.load(fou);
							PDFRenderer pdfrnd = new PDFRenderer(pddoc);
							PDPage pg = pddoc.getDocumentCatalog().getPages().get(0);
							BufferedImage img = pdfrnd.renderImageWithDPI(0, 300, ImageType.RGB);
							pddoc.close();
							int wd = img.getWidth();
							int hg = img.getHeight();
							if(wd>hg) {
								BufferedImage dest = new BufferedImage(hg, wd, img.getType());
								Graphics2D graphics2D = dest.createGraphics();
								graphics2D.translate((hg - wd) / 2, (hg - wd) / 2);
								graphics2D.rotate(Math.PI / 2, hg / 2, wd / 2);
								graphics2D.drawRenderedImage(img, null);
								int m = hg;
								hg = wd; wd = m;
								img = dest;
							}
							int wd2 = 400;
							int hg2 = hg * wd2 / wd;
							picture(img, wd2, hg2);
						} catch(Exception x) {
							x.printStackTrace();
						}
						para(fileref+": "+ou);
					} else if(nm.endsWith(".png")) {
						BufferedImage img = ImageIO.read(fou);
						int wd = img.getWidth();
						int hg = img.getHeight();
						int wd2 = (wd<300)? wd : 300;
						int hg2 = hg * wd2 / wd;

						pp = doc.createParagraph();
						pp.setStyle("Caption");
						rr = pp.createRun();
						try {
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							ImageIO.write(img, "JPG", baos);
							baos.flush();
							InputStream is = new ByteArrayInputStream(baos.toByteArray());
							baos.close();
							rr.addPicture(is, XWPFDocument.PICTURE_TYPE_JPEG, "new"
								, Units.toEMU(wd2), Units.toEMU(hg2));
							is.close();
						} catch(Exception x) {
						}
						rr.addBreak();
						para(fileref+": "+ou);
					} else {
						BufferedReader br = new BufferedReader(new InputStreamReader(
							new FileInputStream(fou)));
						String line;
						pp = doc.createParagraph();
						pp.setStyle("IntenseQuote");
						pp.setIndentationFirstLine(0);
						for(int j=0; (line=br.readLine())!=null ; j++) {
							if(j>=maxline) {
								rr.addBreak();
								rr = pp.createRun();
								rr.setText("...to be continued");
								break;
							}
							if(j>0) rr.addBreak();
							rr = pp.createRun();
							rr.setText(line);
						}
						rr.addBreak();
						rr = pp.createRun();
						rr.setText(fileref+": "+ou);
					}
					pp.setBorderBottom(Borders.SINGLE);
					para("");

					html.append("&nbsp;&nbsp;&nbsp; <a href='"+nm+"'>"+lb+"</a><br>\n");
				}
			}
			html.append("<hr>\n");
			//=================== DOCUMENT END
		} catch(Exception z) {
			z.printStackTrace();
		}
		return html.toString();
	}

	void initGen()  {
		hGen = new HashMap<>();
		hGen.put("A", new ReportGen() {
			String gen(Map<String,String> hM) {
				return genA(hM);
			}
		});
		hGen.put("B", new ReportGen() {
			String gen(Map<String,String> hM) {
				return genB(hM);
			}
		});
	}

	String src;
	String sDir;
	String sPjDir;
	File fPjDir;

    public void action() {
		String vid = PopiangDigital.getObject(vo);
		init();
		if(maxid<0) return;
		int id = maxid;
		id++;
		String rid = pref+":"+id;
System.out.println("Data Set Report: "+rid);
		QueryExecute q0;

		String nm = PopiangUtil.query0(vid+" vp:name ?nm .").get();
		String tm = PopiangUtil.query0(vid+" vp:template ?tm .").get();
		if(tm==null) tm = "file:temp/sarabun8.docx";
		String sb = PopiangUtil.query0(vid+" vp:subscript ?b .").get();
		if("no".equals(sb)) bSub = true;
		src = PopiangUtil.query0(vid+" vp:source ?src .").get();

		sDir = pref.substring(0,pref.length()-2);
		sPjDir = sDir+"/"+pref+"/"+pref+"-"+id;
		fPjDir = new File(PopiangDigital.workDir+"/res/"+sPjDir);
		if(!fPjDir.exists()) fPjDir.mkdirs();
		try {
			ftemp = new File(PopiangDigital.workDir +"/res/"+tm.substring(5));
			doc = new XWPFDocument(new FileInputStream(ftemp));
			while(doc.getBodyElements().size()>0) doc.removeBodyElement(0);
			paraCnt = 0;

			String[] ss;
			paraCnt = 0;
			String[] pat = PopiangUtil.query0(src+" vp:order(*) ?a .").gets("?a");

			q0 = PopiangUtil.query0(vid+" vp:report(*) ?a . ?a ?b ?c .");
			String[] aa = q0.gets("?a");
			String[] bb = q0.gets("?b");
			String[] cc = q0.gets("?c");
			List<String> aI = new ArrayList<>();
			Map<String,Map<String,String>> hIPO = new HashMap<>();
			for(int i=0; i<bb.length; i++) {
				Map<String,String> hPO = hIPO.get(aa[i]);
				if(hPO==null) {
					hPO = new HashMap<>();
					hIPO.put(aa[i],hPO);
					aI.add(aa[i]);
				}
				hPO.put(bb[i], cc[i]);
			}

			StringBuffer html = new StringBuffer();
			for(String a : aI) {
				Map<String,String> hPO = hIPO.get(a);
				String act = hPO.get("vp:act");
				ReportGen rg = hGen.get(act);
				System.out.println("I:"+act+" : "+ (rg!=null));
				if(rg==null) continue;
				String rt = rg.gen(hPO);
				html.append(rt);
			}
			String word = sPjDir+"/report.docx";
			File fword = new File(PopiangDigital.workDir+"/res/"+word);
System.out.println("write report: "+ word);
			try (FileOutputStream fos = new FileOutputStream(fword)) {
				doc.write(fos);
			} catch(Exception z) {
				z.printStackTrace();
			}
			html.append("<table style='width:100%'> <tr> <td>"
				+ "<div style='background-color:#92a8d1; width:100%'> <center>"
				+ "<h2 style='color:white;'>"
				+ "<a href='report.docx'>report.docx</a><br>"
				+ " </h2> </center> </div> </td> </tr> </table>");

			String file = sPjDir+"/index.html";
			BufferedWriter bw = new BufferedWriter(new FileWriter(
				PopiangDigital.workDir+"/res/"+file));
			bw.write("<head>\n");
			bw.write("<meta charset=\"UTF-8\">");
			bw.write("</head>\n");
			bw.write("<body>\n");
			bw.write(html.toString());
			bw.write("</body>\n");
			bw.write("</html>\n");
			bw.close();

			String r = getNewRdf(pref+":"+id, "vo:Reported");
			String g = getClass().getName();
			r = r.replace("___NAME___","CREATE HTML")
				.replace("___FILE___","file:"+file)
				.replace("___REPORTER___"    ,g);
			insert(r);

		} catch(Exception z) {
			z.printStackTrace();
		}
	}

	void makeWord(String v) {
		recsCnt++;
		if(recsCnt>100) return;
		try {
			QueryExecute qe;
			String tp = PopiangUtil.query0(v+" a ?tp .").get();

			if(tp.equals("vo:Note")) {
				String de = PopiangUtil.query0(v+" vp:desc ?de .").get();
				String[] notes = PopiangUtil.query0(v+" vp:notes(*) ?a").gets("?a");

				de = de.replace("___LABEL1___", hLabel.get("___LABEL1___"));

				String tt0 = PopiangUtil.query0(v+" vp:title ?a").get();
				String tt1 = PopiangUtil.query0(v+" vp:title1 ?a").get();
				String tt2 = PopiangUtil.query0(v+" vp:title2 ?a").get();
				String tt3 = PopiangUtil.query0(v+" vp:title3 ?a").get();
				String tt4 = PopiangUtil.query0(v+" vp:title4 ?a").get();
				String pg0 = PopiangUtil.query0(v+" vp:page ?a").get();
				String ind = PopiangUtil.query0(v+" vp:indent ?a").get();
				int ind0 = 1000;
				try { ind0 = Integer.parseInt(ind); } catch(Exception x) {}

				String[] lns = de.split("\n");
				PopiangUtil.ThaiWord tw = new PopiangUtil.ThaiWord();
				for(int i=0; i<lns.length; i++) {
					if(lns[i].length()==0) {
						tw.add("___BLANKLINE___");
					} else if(lns[i].matches("^[0-9]{1,2}\\. .*")) {
						tw.add("___BLANKLINE___");
						tw.setLine(lns[i]+" ");
						while(tw.check1()) ;
					} else if(lns[i].startsWith("- ")) {
						tw.add("___BLANKLINE___");
						tw.setLine(lns[i]+" ");
						while(tw.check1()) ;
					} else {
						tw.setLine(lns[i]+" ");
						while(tw.check1()) ;
					}
				}
				if("new".equals(pg0)) { page0(); paraCnt=0; }

				if(tt0!=null && tt0.length()>0) { if(paraCnt>0) para0("", "Normal", 0, false); title(tt0); }
				if(tt1!=null && tt1.length()>0) { if(paraCnt>0) para0("", "Normal", 0, false); para1(tt1); }
				if(tt2!=null && tt2.length()>0) { if(paraCnt>0) para0("", "Normal", 0, false); para2(tt2); }
				if(tt3!=null && tt3.length()>0) { if(paraCnt>0) para0("", "Normal", 0, false); para3(tt3); }
				if(tt4!=null && tt4.length()>0) { if(paraCnt>0) para0("", "Normal", 0, false); para4(tt4); }
		
				para(ind0, "");
				String[] wds = tw.getText();
				int cnt = 0;
				for(int i=0; i<wds.length; i++) {
					if(wds[i].indexOf("___BLANKLINE___")>=0) {
						para(1000, "");
					} else if(wds[i]!=null && wds[i].length()>0) {
						add(wds[i]);
						cnt++;
					} else {
					}
				}
				if(bSub && cnt>0) sub(v);
				if(cnt>0) paraCnt++;

				for(int i=0; i<notes.length; i++) {
					makeWord(notes[i]);
				}
			} else if(tp.equals("vo:File")) {
				String file = PopiangUtil.query0(v+" vp:file ?a").get();
				File f = new File(PopiangDigital.workDir+"/res/"+file.substring(5));
				textFile(f);
			} else if(tp.equals("vo:Text")) {
				String txt = PopiangUtil.query0(v+" vp:desc ?a").get();
				text0(txt);
			} else if(tp.equals("vo:Image")) {
				String de = PopiangUtil.query0(v+" vp:desc ?de .").get();
				String file = PopiangUtil.query0(v+" vp:image ?a").get();
				if(file==null) throw new Exception("image not found");
				String pf = PopiangDigital.workDir+"/res/"+file.substring(5);
				File fimg = new File(pf);
				if(!fimg.exists()) throw new Exception("image not found");

				BufferedImage img = ImageIO.read(fimg);
				int wd2 = 400;
				int wd = img.getWidth();
				int hg = img.getHeight();
				int hg2 = hg * wd2 / wd;
//				para("");
				picture(img, wd2, hg2);
				if(de.length()>0) { rr.addBreak(); rr.setText(de); }

				if(bSub) sub(v);

			} else {
			}
		} catch(Exception z) {
		}
		recsCnt--;
	}
}

