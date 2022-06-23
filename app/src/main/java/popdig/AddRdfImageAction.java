package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class AddRdfImageAction extends PopupAction {
	String voSub;
	public AddRdfImageAction(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
		voSub = "vo:Image";
	}
    public void action() {
		String sp = ""+tfinf.path;
		if(!sp.endsWith(".ttl")) return;

		Transferable cnt = Toolkit.getDefaultToolkit()
			.getSystemClipboard().getContents(null);
		if(cnt==null || !cnt.isDataFlavorSupported(DataFlavor.imageFlavor)) return;

        String r = getNewRdf(voSub);
        if(r==null || r.length()==0) return;
		String rid = tfinf.text.newid;
		String rdfnm = rid.substring(0,rid.indexOf(":"));
		String dirnm = rdfnm.substring(0, rdfnm.length()-2);
		String imfn = rid.replace(":","-");

		Image img = null;
		String size = "", imf="";
		try {
			img = (Image) cnt.getTransferData(DataFlavor.imageFlavor);
			int wd = img.getWidth(null);
			int hg = img.getHeight(null);
			size = wd+","+hg;
			BufferedImage img1 = new BufferedImage(wd, hg, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = img1.createGraphics();
			g2.drawLine(0,0,100,100);
			g2.drawImage(img, 0,0,wd,hg, 0,0,wd,hg, null);
			g2.dispose();
			sp = sp.substring(0,sp.lastIndexOf("/rdf"))
				+"/res/"+dirnm+"/"+rdfnm+"/"+imfn+".jpg";
			imf = "file:"+dirnm+"/"+rdfnm+"/"+imfn+".jpg";
			File f = new File(sp);
			if(!f.getParentFile().exists()) f.getParentFile().mkdirs();
			ImageIO.write(img1, "JPG", f);
		} catch(Exception z) {
			z.printStackTrace();
			return;
		}

        PopiangDigital.setObject(voSub, rid);
		r = r.replace("___IMAGE___",imf).replace("___SIZE___",size);
System.out.println(r);
		insert(r);
    }   

}

