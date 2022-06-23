package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.awt.image.BufferedImage;
import org.apache.log4j.Logger;
import javax.imageio.ImageIO;
import java.awt.*;

public class ActionTrimImage extends PopupAction {
	static Logger log = Logger.getLogger(ActionTrimImage.class);

	public ActionTrimImage(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
	}
    public void action() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(PopiangDigital.fWork);
		int result = fileChooser.showOpenDialog(null);
		if(result == JFileChooser.APPROVE_OPTION) {
			File fImg = fileChooser.getSelectedFile();
			try {
				BufferedImage bim = ImageIO.read(fImg);
				int wd0 = bim.getWidth();
				int hg0 = bim.getHeight();
				log.info("wd:"+wd0+" hg:"+hg0);

				int dx = wd0 / 20;
				int dy = hg0 / 20;

				boolean flag;

				//   
				// ============= CHECK LEFT BORDER =============
				flag = false ;
				int leftBD = -1;
				do { 
					leftBD++;
					for(int h=0; h<bim.getHeight(); h++) {
                                       if(bim.getRGB(leftBD,h) != Color.white.getRGB()) {
                                           flag = true;
                                           break;
                                       }
					}
					if(leftBD>=bim.getWidth()) flag = true;
				} while(!flag);
				if(leftBD>dx) leftBD -= dx;

				//
				// ============= CHECK RIGHT BORDER =============
				flag = false;
				int rightBD = bim.getWidth();
				do {
					rightBD--;
					for(int h=0; h<bim.getHeight(); h++) {
                                       if(bim.getRGB(rightBD,h) != Color.white.getRGB()) {
                                           flag = true;
                                           break;
                                       }
					}
					if(rightBD<0) flag = true;
				} while(!flag);
				if(bim.getWidth()-rightBD>dx) rightBD += dx;

				//
				// ============= CHECK TOP BORDER =============
				flag = false;
				int upBD = -1;
				do {
					upBD++;
					for(int c=0; c<bim.getWidth(); c++) {
                                       if(bim.getRGB(c,upBD) != Color.white.getRGB()) {
                                           flag = true;
                                           break;
                                       }
					}
					if(upBD>=bim.getHeight()) flag = true;
				} while(!flag);
				if(upBD>dy) upBD -= dy;

				//
				// ============= CHECK DOWN BORDER =============
				flag = false;
				int downBD = bim.getHeight();
				do {
					downBD--;
					for(int c=0; c<bim.getWidth(); c++) {
                                       if(bim.getRGB(c,downBD) != Color.white.getRGB()) {
                                           flag = true;
                                           break;
                                       }
					}
					if(downBD<0) flag = true;
				} while(!flag);
				if(bim.getHeight()-downBD>dy) downBD += dy;

				wd0 = rightBD - leftBD;
				hg0 = downBD - upBD;

log.info("wd:"+ wd0+ " hg:"+ hg0);
				JFileChooser jfc = new JFileChooser();
				jfc.setCurrentDirectory(PopiangDigital.fWork);
				jfc.setDialogTitle("Choose a directory to save your file: "); 
				int returnValue = jfc.showSaveDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File fl = jfc.getSelectedFile();
//					String file = jfc.getSelectedFile().getAbsolutePath();
//					if(!file.endsWith(".docx")) file += ".jpg";
//					BufferedImage img1 = new BufferedImage(wd0, hg0 , BufferedImage.TYPE_INT_ARGB);

					BufferedImage img1 = new BufferedImage(wd0, hg0 , bim.getType());
					Graphics2D g2 = img1.createGraphics();
					g2.drawImage(bim, 0,0,wd0,hg0, leftBD,upBD,rightBD,downBD, null);
					g2.dispose();
					ImageIO.write(img1, "JPG", fl);
System.out.println("WRITE FILE: "+ fl);
           		}   
			} catch(Exception z) {
				z.printStackTrace();
			}
		}
   }   
}

