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

public class DataSetReport extends ReportExecute {
	String vo = "vo:DataSetReport";
	public DataSetReport(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
		initGen();
	}
	int recsCnt = 0;
	boolean bSub = true;
	Map<String,String> hLabel = new HashMap<>();

	static class ReportGen {
		void gen(Map<String,String> hP) { }
	}

	Map<String,ReportGen> hGen;

	void genA(Map<String,String> hM) {
		QueryExecute q0;

		System.out.println("gen A");
		String prop, note, page, file;

		prop = hM.get("vp:prop");
		note = hM.get("vp:doc");
		page = hM.get("vp:page");
		file = hM.get("vp:file");

		System.out.println(" prop: "+ prop);
		System.out.println(" note: "+note);
		System.out.println(" page: "+page);
		System.out.println(" file: "+file);

		try {
			q0 = PopiangUtil.query0(src+" "+prop+"(*) ?a . ?a vp:DEN ?den ; vp:name ?nm ; "+file+" ?f .");
			String[] nms = q0.gets("?nm");
			String[] dns = q0.gets("?den");
			String[] fls = q0.gets("?f");
			for(int i=0; i<nms.length; i++) {
				File fPdf = new File(PopiangDigital.workDir+"/res/"+fls[i].substring(5));
				System.out.println(i+" : "+ nms[i]+" : "+dns[i]+" : "+fPdf.exists());
	
//if(i>2) break;
				hLabel = new HashMap<>();
				hLabel.put("___LABEL1___", nms[i]);

				makeWord(note);
				PDDocument pddoc = PDDocument.load(fPdf);
				PDFRenderer pdfrnd = new PDFRenderer(pddoc);
				Iterable<PDPage> allPages = pddoc.getDocumentCatalog().getPages();
				int pno = 0;
				for(PDPage pg : allPages) {
					makeWord(page);
					BufferedImage img = pdfrnd.renderImageWithDPI(pno, 300, ImageType.RGB);
					System.out.println(pno+" : "+img.getWidth()+" : "+img.getHeight());
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
					rr.addBreak();
					pno++;
					rr.setText("หน้าที่ "+pno);
				}
				pddoc.close();
			}
		} catch(Exception z) {
			z.printStackTrace();
		}
	}
	void genB(Map<String,String> hM) {
		QueryExecute q0;

		System.out.println("gen B");
		String prop, note, page, file;

		prop = hM.get("vp:prop");
		note = hM.get("vp:doc");
		page = hM.get("vp:page");
		file = hM.get("vp:file");

		System.out.println(" prop: "+ prop);
		System.out.println(" note: "+note);
		System.out.println(" page: "+page);
		System.out.println(" file: "+file);

		try {
			q0 = PopiangUtil.query0(src+" "+prop+"(*) ?a . ?a vp:DEN ?den ; vp:name ?nm ; "+file+" ?f .");
			String[] nms = q0.gets("?nm");
			String[] dns = q0.gets("?den");
			String[] fls = q0.gets("?f");
			for(int i=0; i<nms.length; i++) {
				File fPdf = new File(PopiangDigital.workDir+"/res/"+fls[i].substring(5));
				System.out.println(i+" : "+ nms[i]+" : "+dns[i]+" : "+fPdf.exists());
	
				hLabel = new HashMap<>();
				hLabel.put("___LABEL1___", nms[i]);

				makeWord(note);
				PDDocument pddoc = PDDocument.load(fPdf);
				PDFRenderer pdfrnd = new PDFRenderer(pddoc);
				Iterable<PDPage> allPages = pddoc.getDocumentCatalog().getPages();
				int pno = 0;
				for(PDPage pg : allPages) {
					makeWord(page);
					BufferedImage img = pdfrnd.renderImageWithDPI(pno, 300, ImageType.RGB);
					System.out.println(pno+" : "+img.getWidth()+" : "+img.getHeight());
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
					rr.addBreak();
					pno++;
					rr.setText("หน้าที่ "+pno);
				}
				pddoc.close();
			}
		} catch(Exception z) {
			z.printStackTrace();
		}
	}

	void initGen()  {
		hGen = new HashMap<>();
		hGen.put("A", new ReportGen() {
			void gen(Map<String,String> hM) {
				genA(hM);
			}
		});
		hGen.put("B", new ReportGen() {
			void gen(Map<String,String> hM) {
				genB(hM);
			}
		});
	}

	String src;

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

		try {
			String[] ss;

			String target, note, page, file;

//			String[] pat = {"vo:A","vo:B"};

			ftemp = new File(PopiangDigital.workDir +"/res/"+tm.substring(5));
			doc = new XWPFDocument(new FileInputStream(ftemp));
			while(doc.getBodyElements().size()>0) doc.removeBodyElement(0);
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
			for(String a : aI) {
				Map<String,String> hPO = hIPO.get(a);
				String act = hPO.get("vp:act");
				ReportGen rg = hGen.get(act);
				System.out.println("I:"+act+" : "+ (rg!=null));
				if(rg==null) continue;
				rg.gen(hPO);
			}
/*
			for(int k=0; k<pat.length; k++) {

				q0 = PopiangUtil.query0(vid+" vp:report(*) ?a . ?a a "+pat[k]+" ; vp:prop ?tg ;"
					+ " vp:doc ?nt ; vp:page ?pg ; vp:file ?f .");

				System.out.println("<<<<< "+pat[k]+" >>>>>");
				target = q0.get("?tg");
				note = q0.get("?nt");
				page = q0.get("?pg");
				file = q0.get("?f");

				System.out.println(" target: "+ target);
				System.out.println(" note: "+note);
				System.out.println(" page: "+page);
				System.out.println(" file: "+file);

				q0 = PopiangUtil.query0(src+" "+target+"(*) ?a . ?a vp:DEN ?den ; vp:name ?nm ; "+file+" ?f .");
				String[] nms = q0.gets("?nm");
				String[] dns = q0.gets("?den");
				String[] fls = q0.gets("?f");
				for(int i=0; i<nms.length; i++) {
					File fPdf = new File(PopiangDigital.workDir+"/res/"+fls[i].substring(5));
					System.out.println(i+" : "+ nms[i]+" : "+dns[i]+" : "+fPdf.exists());
	
if(i>2) break;
					hLabel = new HashMap<>();
					hLabel.put("___LABEL1___", nms[i]);

					makeWord(note);
					PDDocument pddoc = PDDocument.load(fPdf);
					PDFRenderer pdfrnd = new PDFRenderer(pddoc);
					Iterable<PDPage> allPages = pddoc.getDocumentCatalog().getPages();
					int pno = 0;
					for(PDPage pg : allPages) {
						makeWord(page);
						BufferedImage img = pdfrnd.renderImageWithDPI(pno, 300, ImageType.RGB);
						System.out.println(pno+" : "+img.getWidth()+" : "+img.getHeight());
						int wd = img.getWidth();
						int hg = img.getHeight();
						int wd2 = 400;
						int hg2 = hg * wd2 / wd;
						picture(img, wd2, hg2);
						rr.addBreak();
						pno++;
						rr.setText("หน้าที่ "+pno);
					}
					pddoc.close();
				}
			}
*/

			id++;
			File f= save(id, nm);
			if(f!=null) Desktop.getDesktop().open(f);
/*
			for(String v : cnt) { makeWord(v); }
*/
		} catch(Exception z) {
			z.printStackTrace();
		}
/*
		StringBuffer rs = new StringBuffer(rid+" a vo:Reported; vp:name '"+nm+"' ;\n");
		for(int i=0; i<cnt.length; i++) {
			System.out.println((i+1)+" : "+ cnt[i]);
		}

		String sDir = pref.substring(0,pref.length()-2);
		String sPjDir = sDir+"/"+pref+"/"+pref+"-"+id;
		File fPjDir = new File(PopiangDigital.workDir+"/res/"+sPjDir);

		rs.append("vp:desc '''\n''' .\n");
		insert(rs.toString());
*/
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

