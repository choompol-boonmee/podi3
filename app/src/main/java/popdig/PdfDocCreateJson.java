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

public class PdfDocCreateJson extends PdfDocCreateXml {
	String vo = "vo:PdfDoc";
	public PdfDocCreateJson(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
	}

	void writeA(String id, BufferedWriter bw, int lv) {
		try {
			QueryExecute q0;
			q0 = PopiangUtil.query0(id+" vp:child(*) ?ch .");
			String[] chd = q0.gets("?ch");
			q0 = PopiangUtil.query0(id
				+" vp:DEN ?den ; vp:name ?nm; vp:BusinessTerm ?bt; vp:Cardinality ?ca .");
			String ca = q0.get("?ca");
			if(chd.length>0) {
				if(lv<6) writeG(id, bw, lv, ca);
			} else {
				q0 = PopiangUtil.query0(id+" vp:DEN ?den ; vp:name ?nm; vp:BusinessTerm ?bt .");
				String tg = PopiangUtil.xmlNDR(q0.get("?den"));
				String bt = q0.get("?bt");
				String nm = q0.get("?nm");
				if(tg==null || tg.length()==0) tg = id.replace(":","-");
				String tg0 = "\""+tg+"\" : ";
				String tg1 = "";
//				String cm0 = "\"_comments\" : \""+ca+" : "+nm+" : "+bt+" : "+id+"\",";
//				if(bShowCmt) { tabW(lv,bw); bw.write(cm0); bw.newLine(); }
				int rep = ca.endsWith("*")? 2 : 1;
				for(int i=0; i<rep; i++) {
					if(i>0) { bw.write(","); bw.newLine();
					}
					tabW(lv,bw);
					bw.write(tg0); bw.write("\"...\""); bw.write(tg1);
				}
			}
		} catch(Exception x) {
		}
	}

	void writeG(String id, BufferedWriter bw, int lv, String ca) {
		try {
			QueryExecute q0;
			q0 = PopiangUtil.query0(id+" vp:DEN ?den ; vp:name ?nm; vp:BusinessTerm ?bt .");
			String den = q0.get("?den");
			String bt = q0.get("?bt");
			String nm = q0.get("?nm");
			String tag = PopiangUtil.xmlNDR(den);
			if(tag.length()==0) { tag = id.replace(":","-"); }
			String tag0 = "\""+tag+"\" : {";
			String tag1 = "}";
//			String cmt = "\"_comments\" : \""+nm+" : "+bt+" : "+id+"\", ";
//			if(bShowCmt) { tabW(lv,bw); bw.write(cmt); bw.newLine(); }
			int rep = ca.endsWith("*")? 2 : 1;
			for(int r=0; r<rep; r++) {
				if(r>0) { bw.write(","); bw.newLine(); }
				tabW(lv,bw); bw.write(tag0);	bw.newLine();
				q0 = PopiangUtil.query0(id+" vp:child(*) ?ch .");
				String[] chd = q0.gets("?ch");
				for(int i=0; i<chd.length; i++) {
					if(i>0) { bw.write(","); bw.newLine(); }
					writeA(chd[i], bw, lv+1);
				}
				bw.newLine();
				tabW(lv,bw);
				bw.write(tag1);
			}
		} catch(Exception x) {
		}
	}

    public void action() {
		String vid = PopiangDigital.getObject(vo);
		init();
		if(maxid<0) return;
		int id = maxid;
		id++;
		String vnm = PopiangUtil.query0(vid+" vp:name ?name .").get();
		String show = PopiangUtil.query0(vid+" vp:cmt ?cmt .").get();
		String spce = PopiangUtil.query0(vid+" vp:space ?spc .").get();
		try { spc = Integer.parseInt(spce); } catch(Exception x){}
		if("no".equals(show)) bShowCmt = false;
		setFile(id, ".json");
		try (FileOutputStream fos = new FileOutputStream(fOut)) {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write("{");
			writeG(vid, bw, 0, "1..1");
			bw.write("}");
			bw.close();
		} catch(Exception x) {
		}
		String r = getNewRdf(pref+":"+id, "vo:File");
		r = r.replace("___FILE___", fileOut).replace("___NAME___",vnm);
		insert(r);
	}
}

