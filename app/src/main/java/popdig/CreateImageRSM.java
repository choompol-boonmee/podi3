package popdig;

import java.awt.*;
import java.io.File;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.geom.*;

public class CreateImageRSM {
	File fPng;
	CCLibrary ccl;
	String tag = "RSM";
	public CreateImageRSM(File f, CCLibrary c, String t) {
		fPng = f;
		ccl = c;
		tag = t;
	}

	static int mg = 4, cpw=35;
	Point vnm = new Point(mg+10, mg+15);
	public static int TOP = 1;
	public static int LEFT = 2;
	public static int RIGHT = 3;
	public static int BOTTOM = 4;
	public static int CENTER = 4;

	public void create() {
		try {
			BufferedImage bi0 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
			Graphics2D g20 = bi0.createGraphics();

			Point fgsz = new Point(100,10);

			Rectangle2D rct = textRect(g20, ccl.den2);
			if(rct.getWidth()>fgsz.x) fgsz.x = (int)rct.getWidth() * 12 / 10;
			fgsz.y += vnm.y;

			for(int j=0; j<ccl.chd.size(); j++) {
				CCLibrary ccl2 = ccl.chd.get(j);
				if(!"BBIE".equals(ccl2.type)) continue;
				String name = "<<BBIE>>" + ccl2.den2;
				rct = textRect(g20, name);
				if(rct.getWidth()>fgsz.x) fgsz.x = (int)rct.getWidth() * 12 / 10;
				fgsz.y += vnm.y;
			}
			fgsz.y += vnm.y;

			Point fg1sz = new Point(100, vnm.y * 2);
			for(int j=0; j<ccl.chd.size(); j++) {
				CCLibrary ccl2 = ccl.chd.get(j);
				if(!"ASBIE".equals(ccl2.type)) continue;
				String name = "<<ASBIE>>"+ ccl2.den2;
				rct = textRect(g20, name);
				if(rct.getWidth()>fg1sz.x) fg1sz.x = (int)rct.getWidth() * 12 / 10;
			}

			Point fg2sz = new Point(100, vnm.y * 2);
			int asbiecnt = 0;
			for(int j=0; j<ccl.chd.size(); j++) {
				CCLibrary ccl2 = ccl.chd.get(j);
				if(!"ASBIE".equals(ccl2.type)) continue;
				asbiecnt ++;
				String name = ccl2.den2;
				rct = textRect(g20, name);
				if(rct.getWidth()>fg2sz.x) fg2sz.x = (int)rct.getWidth() * 12 / 10;
			}

			Point pkg = new Point(fgsz.x * 13/10, fgsz.y + vnm.y);
			Point frm = new Point(fgsz.x * 13/10, fgsz.y + vnm.y);
			if(asbiecnt>0) {
				frm.x += frm.x / 2 + fg2sz.x + fg1sz.x;
				if(asbiecnt * (vnm.y*4) > frm.y) {
					frm.y = asbiecnt * (vnm.y*4);
				}
			}

			BufferedImage bufferedImage = new BufferedImage(
				frm.x+2*mg, frm.y+2*mg, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = bufferedImage.createGraphics();

			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, frm.x+2*mg, frm.y+2*mg);
			g2d.setColor(Color.black);

			g2d.drawRect(mg, mg, fgsz.x * 13/10, fgsz.y + vnm.y);
			g2d.drawLine(mg, mg+vnm.y*2+3, pkg.x+mg, mg+vnm.y*2+3);
			drawText(g2d, "<<"+tag+">>", pkg.x/2, vnm.y, CENTER);
			String name0 = ccl.den2;
			drawText(g2d, name0, pkg.x/2, vnm.y*2, CENTER);

			int cnt = 0;
			for(int j=1; j<ccl.chd.size(); j++) {
				CCLibrary ccl2 = ccl.chd.get(j);
				if(!"BBIE".equals(ccl2.type)) continue;
				cnt ++;
				String name = "<<BBIE>>" + ccl2.den2;
				drawText(g2d, name, mg * 3, vnm.y*(2+cnt) + vnm.y/2, LEFT);
			}

			int dhg = 1;
			if(asbiecnt>0) dhg = (fgsz.y + vnm.y) / asbiecnt;
			if(dhg>(vnm.y*2)) dhg = vnm.y * 2;

			int dwd = 10;
			if(asbiecnt>0) dwd = (fgsz.x*13/10*3/2 - (mg+fgsz.x*13/10) - 20) / asbiecnt;

			asbiecnt = 0;
			for(int j=0; j<ccl.chd.size(); j++) {
				CCLibrary ccl2 = ccl.chd.get(j);
				if(!"ASBIE".equals(ccl2.type)) continue;
				drawText(g2d, "<<ASBIE>>" + "[" + ccl2.card + "]"
					, fgsz.x * 13/10 * 3 / 2 + fg1sz.x/2
					, vnm.y * asbiecnt * 4 + vnm.y
					, CENTER);
				drawText(g2d, ccl2.den2
					, fgsz.x * 13/10 * 3 / 2 + fg1sz.x/2
					, vnm.y * asbiecnt * 4 + vnm.y*2 - vnm.y/3
					, CENTER);

				g2d.drawLine(fgsz.x * 13/10 * 3 / 2 - dwd * asbiecnt
					, vnm.y * asbiecnt * 4 + vnm.y*2 
					, fgsz.x * 13/10 * 3 / 2 + fg1sz.x
					, vnm.y * asbiecnt * 4 + vnm.y*2 
					);

				Point xpnt = new Point(mg + fgsz.x * 13/10, mg + dhg/2 + asbiecnt * dhg);
				drawDiamon(g2d, xpnt, LEFT);
				g2d.drawLine(xpnt.x, xpnt.y
					, fgsz.x * 13/10 * 3 / 2 - dwd * asbiecnt
					, xpnt.y);

				g2d.drawLine(fgsz.x * 13/10 * 3 / 2 - dwd * asbiecnt
					, vnm.y * asbiecnt * 4 + vnm.y*2
					, fgsz.x * 13/10 * 3 / 2 - dwd * asbiecnt
					, xpnt.y);

				Point apt = new Point(fgsz.x * 13/10 * 3 / 2 + fg1sz.x
					, vnm.y * asbiecnt * 4 + vnm.y*2);
				drawArrow(g2d, apt, RIGHT);

				String name = ccl2.den2;
				g2d.drawRect(fgsz.x * 13/10 * 3 / 2 + fg1sz.x
					, vnm.y * asbiecnt * 4
					, fg2sz.x, vnm.y * 3);
				g2d.drawLine(fgsz.x * 13/10 * 3 / 2 + fg1sz.x
					, vnm.y * asbiecnt * 4 + vnm.y * 2
					, fgsz.x * 13/10 * 3 / 2 + fg2sz.x + fg1sz.x
					, vnm.y * asbiecnt * 4 + vnm.y * 2);
				drawText(g2d, "<<ABIE>>"
					, fgsz.x * 13/10 * 3 / 2 + fg2sz.x/2 + fg1sz.x
					, vnm.y * asbiecnt * 4 + vnm.y - vnm.y/4
					, CENTER);
				drawText(g2d, name
					, fgsz.x * 13/10 * 3 / 2 + fg2sz.x/2 + fg1sz.x
					, vnm.y * asbiecnt * 4 + vnm.y*2 - vnm.y/2
					, CENTER);

				asbiecnt ++;
/*
//				rct = textRect(g20, name);
//				if(rct.getWidth()>fg2sz.x) fg2sz.x = (int)rct.getWidth() * 12 / 10;
*/
			}

			g2d.dispose();

			ImageIO.write(bufferedImage, "png", fPng);

		} catch(Exception exg1) {
System.out.println("ex2:" + exg1);
		}
	}
	public void drawArrow(Graphics2D g2d, Point cp, int dir) {
		Point of1 = new Point(0,0);
		Point of2 = new Point(0,0);
		if(dir==TOP) { of1.x = -10; of2.x = 10; of1.y = 10; of2.y = 10; }
		if(dir==LEFT) { of1.x = 10; of2.x = 10; of1.y = -10; of2.y = 10; }
		if(dir==RIGHT) { of1.x = -10; of2.x = -10; of1.y = -10; of2.y = 10; }
		if(dir==BOTTOM) { of1.x = -10; of2.x = 10; of1.y = -10; of2.y = -10; }
		g2d.drawLine(cp.x, cp.y, cp.x+of1.x, cp.y+of1.y);
		g2d.drawLine(cp.x, cp.y, cp.x+of2.x, cp.y+of2.y);
	}

	public void drawText(Graphics2D g2d, String txt, int x, int y) {
			FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
			int wd = metrics.stringWidth(txt);
			g2d.drawString(txt, x-wd/2, y);
	}

	public void drawText(Graphics2D g2d, String txt, int x, int y, int direction) {
		if(direction==CENTER) {
			FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
			int wd = metrics.stringWidth(txt);
			g2d.drawString(txt, x-wd/2, y);
		} else {
			g2d.drawString(txt, x, y);
		}
	}
	public void drawDiamon(Graphics2D g2d, Point cp, int dir) {
		Point of1 = new Point(8, 4);
		Point of2 = new Point(16, 0);
		Point of3 = new Point(8,-4);
		int[] xs = {cp.x, cp.x+8, cp.x+16, cp.x+8};
		int[] ys = {cp.y, cp.y+4, cp.y, cp.y-4};
		Polygon plg = new Polygon(xs, ys, 4);
		g2d.fill(plg);
	}
	public Rectangle2D textRect(Graphics2D g2d, String txt) {
			FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
			return metrics.getStringBounds(txt, g2d);
	}
}
 
