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

public class ReportExecute extends PopupAction {
	String vo = "vo:Report";
	public ReportExecute(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
	}

	public XWPFDocument doc;
	public XWPFParagraph pp;
	public XWPFRun rr;

	public File ftemp;
	public String fn, pref, dir;
	public int paraCnt = 0;
	public boolean bNewPage = false;

	public void page0() { bNewPage = true; }
	public void page1(String tx) { para0(tx, "Heading1", 0, true); }
	public void page2(String tx) { para0(tx, "Heading2", 0, true); }
	public void page3(String tx) { para0(tx, "Heading3", 0, true); }
	public void page4(String tx) { para0(tx, "Heading4", 0, true); }
	public void title(String tx) { para0(tx, "Title", 0, false); }
	public void para1(String tx) { para0(tx, "Heading1", 0, false); }
	public void para2(String tx) { para0(tx, "Heading2", 0, false); }
	public void para3(String tx) { para0(tx, "Heading3", 0, false); }
	public void para4(String tx) { para0(tx, "Heading4", 0, false); }
	public void para(String tx) { para0(tx, "Normal", 0, false); }
	public void para(int in, String tx) { para0(tx, "Normal", in, false); }
	public void add(String tx) { rr = pp.createRun(); rr.setText(tx); }
	public void sub(String tx) { rr = pp.createRun(); rr.setText(tx);
		rr.setSubscript(VerticalAlign.SUBSCRIPT); }

	int maxid = -1;

	public void init() {
		fn = tfinf.path.toFile().getName();
		pref = fn.substring(0,fn.indexOf("."));
		if(!pref.matches("[a-z]+[0-9A-Z]{2}")) return;
		dir = pref.substring(0,pref.length()-2);
		maxid = tfinf.text.maxId();
	}

	public String wrap(String tx) { 
		return tx.replace("\n","").replace("\r",""); //.replace(" ","");
	}

	public void textFile(File f) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			String line;
			pp = doc.createParagraph();
			pp.setStyle("IntenseQuote");
			rr = pp.createRun();
			for(int i=0; (line=br.readLine())!=null; i++) {
				if(i>0) rr.addBreak();
				rr.setText(line);
			}
		} catch(Exception z) {
		}
	}

	public void text0(String tx) {
		String[] lns = tx.split("\n");
		pp = doc.createParagraph();
		pp.setStyle("IntenseQuote");
		rr = pp.createRun();
		for(int i=0; i<lns.length; i++) {
			if(i>0) rr.addBreak();
			rr.setText(lns[i]);
		}
	}
	public void para0(String tx, String sty, int ind, boolean pg) {
		if(pg) paraCnt = 0;
		pp = doc.createParagraph();
		pp.setStyle(sty);
		if(pg || bNewPage) pp.setPageBreak(true);
		bNewPage = false;
		if(ind>0) pp.setIndentationFirstLine(ind);
		rr = pp.createRun();
		rr.setText(tx);
	}

	public void picture(BufferedImage img, int wd, int hg) {
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
				, Units.toEMU(wd), Units.toEMU(hg));
			is.close();
		} catch(Exception x) {
		}
	}

	File fOut;
	String fileOut;

	public void setFile(int id, String ext) {
		String fnm = pref+"-"+id;
		String sp = ""+tfinf.path;
		sp = sp.substring(0,sp.lastIndexOf("/rdf")) +"/res/"+dir+"/"+pref+"/"+fnm+ext;
		fOut = new File(sp);
		if(!fOut.getParentFile().exists()) fOut.getParentFile().mkdirs();
		fileOut = "file:"+dir+"/"+pref+"/"+fnm+ext;
	}

	public File save(int id, String vnm) {
		try {
			setFile(id, ".docx");
			try (FileOutputStream fos = new FileOutputStream(fOut)) {
				doc.write(fos);
			} catch(Exception z) {
			}
			System.out.println("file write sp: "+fOut);
			String r = getNewRdf(pref+":"+id, "vo:Reported");
			String g = getClass().getName();
			r = r.replace("___NAME___",vnm)
			.replace("___FILE___",fileOut)
			.replace("___REPORTER___",g);
			insert(r);
			return fOut;
		} catch(Exception z) {
			z.printStackTrace();
		}
		return null;
	}

    public void action() {
	}
}

