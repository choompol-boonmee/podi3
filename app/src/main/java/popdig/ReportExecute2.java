package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
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

public class ReportExecute2 extends ReportExecute {
	String vo = "vo:Report";
	public ReportExecute2(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
	}

    public void action() {
		String vid = PopiangDigital.getObject(vo);
		if(vid==null) {
			vid = PopiangDigital.getObject("vo:Note");
		}
		if(vid==null) return;

		QueryExecute q0 = PopiangUtil.query0(vid+" vp:notes(*) ?a .");
		String[] vs = q0.gets("?a");
		String nm = PopiangUtil.query0(vid+" vp:name ?a").get();
		if(nm==null) nm = "no name";

		String tm = PopiangUtil.query0(vid+" vp:template ?a").get();
		if(tm==null) tm = "file:temp/sarabun8.docx";

		String sb = PopiangUtil.query0(vid+" vp:subscript ?a").get();
		if("no".equals(sb)) bSub = false;

		init();
		if(maxid<0) return;
		int id = maxid;

		try {

			ftemp = new File(PopiangDigital.workDir +"/res/"+tm.substring(5));
			doc = new XWPFDocument(new FileInputStream(ftemp));
			while(doc.getBodyElements().size()>0) doc.removeBodyElement(0);
			paraCnt = 0;

			for(String v : vs) {
				makeWord(v);
			}
			id++;
			File f= save(id, nm);
			if(f!=null) Desktop.getDesktop().open(f);
		} catch(Exception z) {
		}

	}
	int recsCnt = 0;
	boolean bSub = true;

	void makeWord(String v) {
		recsCnt++;
		if(recsCnt>100) return;
		try {
			QueryExecute qe = PopiangUtil.query0(v+" a ?tp ; vp:desc ?a .");
			String tp = qe.get("?tp");
			String de = qe.get("?a");
			if(de==null || tp==null) {
				recsCnt--;
				return;
			}

			if(tp.equals("vo:Note")) {
				String[] notes = PopiangUtil.query0(v+" vp:notes(*) ?a").gets("?a");

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
			} else if(tp.equals("vo:Image")) {
				String file = PopiangUtil.query0(v+" vp:image ?a").get();
				if(file==null) throw new Exception("image not found");
				String pf = PopiangDigital.workDir+"/res/"+file.substring(5);
				File fimg = new File(pf);
				if(!fimg.exists()) throw new Exception("image not found");

				String tt0 = PopiangUtil.query0(v+" vp:title ?a").get();
				String tt1 = PopiangUtil.query0(v+" vp:title1 ?a").get();
				String tt2 = PopiangUtil.query0(v+" vp:title2 ?a").get();
				String tt3 = PopiangUtil.query0(v+" vp:title3 ?a").get();
				String tt4 = PopiangUtil.query0(v+" vp:title4 ?a").get();
				String pg0 = PopiangUtil.query0(v+" vp:page ?a").get();
				String ind = PopiangUtil.query0(v+" vp:indent ?a").get();
				if(tt0!=null && tt0.length()>0) { if(paraCnt>0) para0("", "Normal", 0, false); title(tt0); }
				if(tt1!=null && tt1.length()>0) { if(paraCnt>0) para0("", "Normal", 0, false); para1(tt1); }
				if(tt2!=null && tt2.length()>0) { if(paraCnt>0) para0("", "Normal", 0, false); para2(tt2); }
				if(tt3!=null && tt3.length()>0) { if(paraCnt>0) para0("", "Normal", 0, false); para3(tt3); }
				if(tt4!=null && tt4.length()>0) { if(paraCnt>0) para0("", "Normal", 0, false); para4(tt4); }

				BufferedImage img = ImageIO.read(fimg);
				int wd2 = 320;
				int wd = img.getWidth();
				int hg = img.getHeight();
				int hg2 = hg * wd2 / wd;
//				para("");
//				if(de.length()>0) add(de);
				picture(img, wd2, hg2);
				if(de.length()>0) {
					rr.addBreak();
					rr.setText(de);
					if(bSub) sub(v);
				}

				int ind0 = 1000;
				try { ind0 = Integer.parseInt(ind); } catch(Exception x) {}

				String[] notes = PopiangUtil.query0(v+" vp:notes(*) ?a . ?a vp:desc ?de .").gets("?de");
				for(int j=0; j<notes.length; j++) {
					de = notes[j];

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
				}
			} else {
/*
				String[] notes = PopiangUtil.query0(v+" vp:notes(*) ?a").gets("?a");

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
*/
			}
		} catch(Exception z) {
		}
		recsCnt--;
	}
}

