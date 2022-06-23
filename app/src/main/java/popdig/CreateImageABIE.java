package popdig;

import java.awt.*;
import java.io.File;

public class CreateImageABIE {
	public CreateImageABIE() {
	}
	public void drawABIE(File f, CCLibrary ccl) {
	}
/*
	public void drawRSM(String fname, Vector<String[]> abiev
		) {
		try {
			BufferedImage bi0 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
			Graphics2D g20 = bi0.createGraphics();

			Point fgsz = new Point(100,10);

			Rectangle2D rct = textRect(g20, entryName(abiev.get(0)));
			if(rct.getWidth()>fgsz.x) fgsz.x = (int)rct.getWidth() * 12 / 10;
			fgsz.y += vnm.y;

			for(int j=1; j<abiev.size(); j++) {
				if(!"BBIE".equals(abiev.get(j)[1])) continue;
				String name = "<<BBIE>>" + entryName(abiev.get(j));
				rct = textRect(g20, name);
				if(rct.getWidth()>fgsz.x) fgsz.x = (int)rct.getWidth() * 12 / 10;
				fgsz.y += vnm.y;
			}
			fgsz.y += vnm.y;

			Point fg1sz = new Point(100, vnm.y * 2);
			for(int j=1; j<abiev.size(); j++) {
				if(!"ASBIE".equals(abiev.get(j)[1])) continue;
				String name = "<<ASBIE>>"+entryName(abiev.get(j));
				rct = textRect(g20, name);
				if(rct.getWidth()>fg1sz.x) fg1sz.x = (int)rct.getWidth() * 12 / 10;
			}

			Point fg2sz = new Point(100, vnm.y * 2);
			int asbiecnt = 0;
			for(int j=1; j<abiev.size(); j++) {
				if(!"ASBIE".equals(abiev.get(j)[1])) continue;
				asbiecnt ++;
				String name = assoEntryName(abiev.get(j));
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
			drawText(g2d, "<<RSM>>", pkg.x/2, vnm.y, CENTER);
			drawText(g2d, entryName(abiev.get(0)), pkg.x/2, vnm.y*2, CENTER);

			int cnt = 0;
			for(int j=1; j<abiev.size(); j++) {
				if(!"BBIE".equals(abiev.get(j)[1])) continue;
				cnt ++;
				String name = "<<BBIE>>" + entryName(abiev.get(j));
				drawText(g2d, name, mg * 3, vnm.y*(2+cnt) + vnm.y/2, LEFT);
			}

			int dhg = 1;
			if(asbiecnt>0) dhg = (fgsz.y + vnm.y) / asbiecnt;
			if(dhg>(vnm.y*2)) dhg = vnm.y * 2;

			int dwd = 10;
			if(asbiecnt>0) dwd = (fgsz.x*13/10*3/2 - (mg+fgsz.x*13/10) - 20) / asbiecnt;

			asbiecnt = 0;
			for(int j=1; j<abiev.size(); j++) {
				if(!"ASBIE".equals(abiev.get(j)[1])) continue;

				String name0 = entryName(abiev.get(j));
				drawText(g2d, "<<ASBIE>>" + "[" + abiev.get(j)[13] + "]"
					, fgsz.x * 13/10 * 3 / 2 + fg1sz.x/2
					, vnm.y * asbiecnt * 4 + vnm.y
					, CENTER);
				drawText(g2d, name0
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

				String name = assoEntryName(abiev.get(j));
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
//				rct = textRect(g20, name);
//				if(rct.getWidth()>fg2sz.x) fg2sz.x = (int)rct.getWidth() * 12 / 10;
			}

			g2d.dispose();

			File file = new File(fname);
			ImageIO.write(bufferedImage, "png", file);

		} catch(Exception exg1) {
System.out.println("ex2:" + exg1);
		}
	}
*/
}
 
