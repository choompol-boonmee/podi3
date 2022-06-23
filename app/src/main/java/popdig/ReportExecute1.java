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

public class ReportExecute1 extends ReportExecute {
	String vo = "vo:Report";
	public ReportExecute1(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
	}

    public void action() {
		String vid = PopiangDigital.getObject(vo);
		System.out.println("REPORT: "+vid);
		QueryExecute q0 = PopiangUtil.query0(vid+" vp:views(*) ?a .");
		String[] vs = q0.gets("?a");

		// prepare file names
		fn = tfinf.path.toFile().getName();
		pref = fn.substring(0,fn.indexOf("."));
		if(!pref.matches("[a-z]+[0-9A-Z]{2}")) return;
		dir = pref.substring(0,pref.length()-2);
		int id = tfinf.text.maxId();

		for(String v : vs) {
			System.out.println(v);

			try {

			// prepare data

			ViewExecute1Action vi = new ViewExecute1Action(null,null,null);
			vi.retrieve(v);
			vi.collectCam();
			vi.collectCables();
			BufferedImage img1 = vi.drawImage();


			// make new doc
			ftemp = new File(PopiangDigital.workDir +"/res/temp/sarabun2.docx");
			doc = new XWPFDocument(new FileInputStream(ftemp));
			while(doc.getBodyElements().size()>0) doc.removeBodyElement(0);

			page1(vi.vnm);

			int wd = vi.dim[0] * 450 / vi.dim[0];
			int hg = vi.dim[1] * 450 / vi.dim[0];
			picture(img1, wd, hg);
			para("");

			StringBuffer bf;
			para(""+vi.vnm);
			add("ประกอบด้วยโหนด (node) หรือจุดศูนย์กลางการต่อกล้อง") ;
			add("จำนวน "+vi.aNode.size()+" จุด");
			add("โดยมีกล้องรวมทั้งสิ้น "+vi.aCam.size()+" ตัว");
			add("ดังแสดงในภาพข้างบน");
			add("สีน้ำเงินแสดงข้อมูลโหนด และสีน้ำเงินแสดงข้อมูลกล้อง");

			para("");
			para("ข้อมูลแต่ละโหนด จำนวน "+ 
				vi.aNode.size() + " โหนดมีรายละเอียดดังต่อไปนี้");

			for(int i=0; i<vi.aNode.size(); i++) {
				String nid = vi.aNodeId.get(i);
				QueryExecute qx = vi.hNode.get(nid);
				para("");
				para(""+(i+1)+". NODEID:" + nid);
				para(1000, "ชื่อโหนด: "+ qx.get("?name"));
				para(1000, "พิกัดเส้นรุ้งเส้นแวง: "+ qx.get("?gps"));
				para(1000, "พิกัด UTM(เมตร) : "+ qx.get("?utm"));
			}

			para("");
			para("");
			para("ข้อมูลกล้องจำนวน "
				+ vi.aCam.size() + " ตัว มีรายละเอียดดังต่อไปนี้");
			for(int i=0; i<vi.aCam.size(); i++) {
				String cid = vi.aCamId.get(i);
				QueryExecute qx = vi.hCam.get(cid);
				para("");
				para(""+(i+1)+". รหัสกล้อง :" + cid +" (camID)");
				para(1000, "ชื่อกล้อง: "+ qx.get("?name"));
				para(1000, "ต่อกับโหนด: "+ qx.get("?node"));
				para(1000, "ชนิดกล้อง: "+ qx.get("?prodId"));
				para(1000, "ชั้นที่: "+ qx.get("?floor"));
				para(1000, "มุมกล้อง(แนวราบ) : "+ qx.get("?norAng")+" (จากทิศเหนือตามเข็มนาฬิกา)");
				para(1000, "ระยะตรวจจับหวังผล : "+ qx.get("?maxCov"));
				para(1000, "พิกัดเส้นรุ้งเส้นแวง: "+ qx.get("?gps"));
				para(1000, "พิกัด UTM(เมตร) : "+ qx.get("?utm"));
				para(1000, "มุมก้มเงย : "+ qx.get("?upAng"));
				para(1000, "การติดตั้ง : "+ qx.get("?attach"));
				double dlen = vi.aPoeLen.get(i);
				double dlen3 = dlen * 5 / 2;
				String slen = String.format("%.2f", dlen);
				String slen3 = String.format("%.2f", dlen3);
if(dlen3>100) System.out.println("CAM: "+ cid +" : "+ slen3);
				para(1000, "ระยะจากโหนดถึงกล้อง : "+ slen+" เมตร");
				para(1000, "ความยาวสาย POE : "+ slen3+" เมตร");
			}

			id++;
			save(id, vi.vnm);

			} catch(Exception z) {
				z.printStackTrace();
			}

		}
	}
}

