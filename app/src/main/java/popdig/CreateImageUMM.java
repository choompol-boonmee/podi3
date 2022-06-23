package popdig;

import java.awt.*;
import java.io.File;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.geom.*;

public class CreateImageUMM {
	File fPng;
	CCLibrary ccl;
	public CreateImageUMM(File f, CCLibrary c) {
		fPng = f;
		ccl = c;
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
		} catch(Exception exg1) {
		}
	}

	public void drawFig5(File fout, String nm, String rqa, String rsa
		, String rqm, String rsm, String rqd, String rsd) {
		try {
			Point pkg = new Point(mg+650,420);
			BufferedImage bufferedImage = new BufferedImage(
				pkg.x+2*mg, pkg.y+2*mg, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = bufferedImage.createGraphics();

			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, pkg.x+2*mg, pkg.y+2*mg);
			g2d.setColor(Color.black);
			g2d.drawRect(mg, mg, pkg.x, pkg.y);
			g2d.drawLine(mg, mg+cpw, pkg.x+mg, mg+cpw);
			g2d.drawString("<<Business Transaction>> ", vnm.x, vnm.y);
			g2d.drawString(nm, vnm.x, vnm.y+14);

			// left side
			Rectangle pk1 = new Rectangle(mg+15, mg+cpw+15, 310, 350);
			g2d.drawRect(pk1.x, pk1.y, pk1.width, pk1.height);
			g2d.drawLine(pk1.x, pk1.y+cpw, pk1.x+pk1.width, pk1.y+cpw);
			drawText(g2d, "<<BusinessTransactionSwimLane>>", pk1.x+pk1.width/2, pk1.y+15);
			drawText(g2d, rqa, pk1.x+pk1.width/2, pk1.y+30);

			Rectangle pc1 = new Rectangle(pk1.x+50, pk1.y+100, 220, 60);
			drawRoundRect(g2d, pc1, 20);
			drawText(g2d, "<<RequestingBusinessActivity>>", pc1.x+pc1.width/2, pc1.y+20);
			drawText(g2d, rqm, pc1.x+pc1.width/2, pc1.y+40);

			Rectangle pc2 = new Rectangle(pk1.x+50, pk1.y+250, 220, 60);
			drawRoundRect(g2d, pc2, 0);
			drawText(g2d, "<<RequestingInformationEnvelope>>", pc2.x+pc2.width/2, pc2.y+20);
			drawText(g2d, rqd, pc2.x+pc2.width/2, pc2.y+40);

			g2d.drawLine(pc1.x+pc1.width/2, pc1.y+pc1.height, pc1.x+pc1.width/2, pc2.y);
			Point a0 = new Point(pc1.x+pc1.width/2, pc2.y);
			drawArrow(g2d, a0, BOTTOM);

			g2d.drawLine(pc1.x+pc1.width/2, pc1.y-30, pc1.x+pc1.width/2, pc1.y);
			Point p0 = new Point(pc1.x+pc1.width/2, pc1.y-30);
			drawStartPoint(g2d, p0, TOP);

			g2d.drawLine(pc1.x+pc1.width/4+10, pc1.y+pc1.height
				, pc1.x+pc1.width/4+10, pc1.y+pc1.height+20);
			Point p1 = new Point(pc1.x+pc1.width/4+10, pc1.y+pc1.height+20);
			drawEndPoint(g2d, p1, BOTTOM);
			drawText(g2d, "success", p1.x, p1.y+30);

			g2d.drawLine(pc1.x+pc1.width/4-25, pc1.y+pc1.height
				, pc1.x+pc1.width/4-25, pc1.y+pc1.height+40);
			Point p2 = new Point(pc1.x+pc1.width/4-25, pc1.y+pc1.height+40);
			drawEndPoint(g2d, p2, BOTTOM);
			drawText(g2d, "failure", pc1.x+pc1.width/4-25, pc1.y+pc1.height+40+30);

			// right side
			Rectangle pk2 = new Rectangle(mg+350, mg+cpw+15, 280, 350);
			g2d.drawRect(pk2.x, pk2.y, pk2.width, pk2.height);
			g2d.drawLine(pk2.x, pk2.y+cpw, pk2.x+pk2.width, pk2.y+cpw);
			drawText(g2d, "<<BusinessTransactionSwimLane>>", pk2.x+pk2.width/2, pk2.y+15);
			drawText(g2d, rsa, pk2.x+pk2.width/2, pk2.y+30);

			pc1 = new Rectangle(pk2.x+30, pk2.y+100, 220, 60);
			drawRoundRect(g2d, pc1, 0);
			drawText(g2d, "<<RespondingInformationEnvelope>>", pc1.x+pc1.width/2, pc1.y+20);
			drawText(g2d, rsd, pc1.x+pc1.width/2, pc1.y+40);
//			g2d.drawLine(pk2.x+50, pk2.y+130, pk1.x+50+220, pk2.y+130);
			g2d.drawLine(pc1.x, pk2.y+130, pk1.x+50+220, pk2.y+130);
			drawArrow(g2d, new Point(pk1.x+50+220, pk2.y+130), LEFT);

			pc2 = new Rectangle(pk2.x+30, pk2.y+250, 220, 60);
			drawRoundRect(g2d, pc2, 20);
			drawText(g2d, "<<RespondingBusinessActivity>>", pc2.x+pc2.width/2, pc2.y+20);
			drawText(g2d, rsm, pc2.x+pc2.width/2, pc2.y+40);
			g2d.drawLine(pc1.x, pk2.y+250+30, pk1.x+50+220, pk2.y+250+30);
			drawArrow(g2d, new Point(pc1.x, pk2.y+250+30), RIGHT);

			g2d.drawLine(pc1.x+pc1.width/2, pc1.y+pc1.height, pc1.x+pc1.width/2, pc2.y);
			a0 = new Point(pc1.x+pc1.width/2, pc1.y+pc1.height);
			drawArrow(g2d, a0, TOP);


			g2d.dispose();
			ImageIO.write(bufferedImage, "png", fout);
		} catch(Exception exg1) {
System.out.println("ex13:" + exg1);
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
	public void drawRoundRect(Graphics2D g2d, Rectangle us1, int cvl) {
			g2d.drawArc(us1.x, us1.y, cvl*2, cvl*2, 90, 90);
			g2d.drawArc(us1.x, us1.y+us1.height-cvl*2, cvl*2, cvl*2, 180, 90);
			g2d.drawArc(us1.x+us1.width-cvl*2, us1.y+us1.height-cvl*2, cvl*2, cvl*2, 270, 90);
			g2d.drawArc(us1.x+us1.width-cvl*2, us1.y, cvl*2, cvl*2, 0, 90);
			g2d.drawLine(us1.x+cvl, us1.y, us1.x+us1.width-cvl, us1.y);
			g2d.drawLine(us1.x+cvl, us1.y+us1.height, us1.x+us1.width-cvl, us1.y+us1.height);
			g2d.drawLine(us1.x, us1.y+cvl, us1.x, us1.y+us1.height-cvl);
			g2d.drawLine(us1.x+us1.width, us1.y+cvl, us1.x+us1.width, us1.y+us1.height-cvl);
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
	public void drawStartPoint(Graphics2D g2d, Point pnt, int dir) {
		int ccr = 8;
		Point s = new Point(0,0);
		if(dir==TOP) s.y -= ccr;
		if(dir==BOTTOM) s.y += ccr;
		if(dir==LEFT) s.x -= ccr;
		if(dir==RIGHT) s.x += ccr;
		g2d.fillArc(pnt.x-ccr+s.x, pnt.y-ccr+s.y, ccr*2, ccr*2, 0, 360);
	}

	public void drawEndPoint(Graphics2D g2d, Point pnt, int dir) {
		int ccr = 8;
		Point s = new Point(0,0);
		if(dir==TOP) s.y -= ccr;
		if(dir==BOTTOM) s.y += ccr;
		if(dir==LEFT) s.x -= ccr;
		if(dir==RIGHT) s.x += ccr;
		g2d.drawArc(pnt.x-ccr+s.x, pnt.y-ccr+s.y, ccr*2, ccr*2, 0, 360);
		int bw = 3;
		g2d.fillArc(pnt.x-ccr+s.x+bw, pnt.y-ccr+s.y+bw, (ccr-bw)*2, (ccr-bw)*2, 0, 360);
	}
}
 
