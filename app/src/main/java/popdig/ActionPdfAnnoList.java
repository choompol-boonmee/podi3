package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import javax.swing.tree.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.interactive.annotation.*;
import org.apache.pdfbox.cos.*;
import org.apache.pdfbox.rendering.*;
import org.apache.pdfbox.pdmodel.common.*;
import org.apache.log4j.Logger;

public class ActionPdfAnnoList extends PopupAction {
	static Logger log = Logger.getLogger(PopiangWindow.class);

	String vid;
	public ActionPdfAnnoList(JPopupMenu pop, TextFileInfo tfi, String lb, String i) {
		super(pop, tfi, lb);
		vid = i;
	}
    public void action() {
System.out.println("PDF ANNOTATE: "+vid);

		DefaultMutableTreeNode node = PopiangDigital.wind.getFolderNode();
		if(node==null) return;
		String rdfs = PopiangDigital.workDir+"/rdf/";
		String prfx = PopiangDigital.wind.getNodePrefix0(node);
		String mx2 = PopiangDigital.wind.getRdfIdMax0(prfx);
		String fnm = prfx+mx2;
		TreePath path = new TreePath(node.getPath());
		PopiangDigital.wind.tree.expandPath(path);
		File rdf0 = new File(rdfs+prfx+"/"+fnm+".ttl");
		try {
			Path pth = Paths.get(rdf0.getAbsolutePath());

			String ff = PopiangUtil.query0(vid+" vp:file ?a").get();
			File f = new File(PopiangDigital.workDir+"/res/"+ff.substring(5));
			System.out.println("vid: "+ vid+" ff:"+ ff+" :"+f.exists());

			List<String> aAtom = new ArrayList<>();
			List<String> aAtomTx = new ArrayList<>();
			Map<String,String> hAtom = new HashMap<>();
			List<DocCclElem> aE1 = new ArrayList<>();

			PDDocument pddoc = PDDocument.load(f);
			PDFRenderer pdfrnd = new PDFRenderer(pddoc);
			Iterable<PDPage> allPages = pddoc.getDocumentCatalog().getPages();
			int pno = 0;
			for(PDPage pg : allPages) {
				PDRectangle prct = pg.getMediaBox();
				float llx = prct.getLowerLeftX();
				float lly = prct.getLowerLeftY();
				float urx = prct.getUpperRightX();
				float ury = prct.getUpperRightY();
				pno++;
				List<PDAnnotation> aAnn = pg.getAnnotations();
				if(aAnn.size()==0) continue;
System.out.println("page:"+pno+"  "+llx+","+lly+"  "+urx+","+ury);
				int ano = 0;
//				BufferedImage bi = pdfrnd.renderImageWithDPI(pno, 300, ImageType.RGB);
				for(PDAnnotation ann: aAnn) {
					String txt = ann.getContents();
					String typ = ann.getSubtype();
					PDRectangle rct = ann.getRectangle();
					if("FreeText".equals(typ)) {
						llx = rct.getLowerLeftX();
						lly = rct.getLowerLeftY();
						urx = rct.getUpperRightX();
						ury = rct.getUpperRightY();
//						log.info("    "+ano+": ["+ txt+"] "+llx+","+lly+"  "+urx+","+ury);
						ano++;
						if(txt.startsWith("a")) {
							aE1.add(new DocCclElem(pno, ano, txt, tfinf));
							String aid = txt;
							int i1 = aid.indexOf("=");
							if(i1>0) aid = aid.substring(0,i1);
							String id0 = "p"+pno+aid;
							String x = getNewRdf(fnm+":"+id0, "vo:CCL");
							aAtom.add(id0);
							aAtomTx.add(txt);
							hAtom.put(id0,x);
							log.info("  "+id0+" = "+x);
						}
					} else if("Highlight".equals(typ)) {
						log.info("    "+ano+": Highlight");
					} else if("Square".equals(typ)) {
						log.info("    "+ano+": Square");
					} else {
						log.info("    "+ano+" :"+typ);
					}
				}
			}
			pddoc.close();

			StringBuffer buf = new StringBuffer(PopiangUtil.blankRdf(fnm));
			for(int i=0; i<aAtom.size(); i++) {
				String s = aAtom.get(i);
				String x = aAtomTx.get(i);
				String r = hAtom.get(s);
				r = r.replace("___NAME___", vid+" "+s);
				int i1 = x.indexOf("=");
				if(i1>0) {
					String y = x.substring(i1+1);
					y = y.replace("\n","");
					String[] zs = y.split(",");
					StringBuffer bf = new StringBuffer("(");
System.out.print("x="+s+" (");
					for(int j=0; j<zs.length; j++) {
						if(j>0) bf.append(" ");
						bf.append(zs[j]);
if(j>0) System.out.print(" ");
System.out.print(zs[j]);
					}
					bf.append(")");
System.out.println(")");
					r = r.replace("()", bf.toString());
				}
				buf.append(r);
			}
			Files.write(pth, buf.toString().getBytes());

			SwingUtilities.invokeLater(new Runnable() { public void run() {
				PopiangDigital.wind.populateTreeNode(node, new File(rdfs+prfx));
				((DefaultTreeModel) PopiangDigital.wind.tree.getModel()).reload();
				TreePath path = new TreePath(node.getFirstLeaf().getPath());
				PopiangDigital.wind.tree.setSelectionPath(path);
			}});
		} catch(Exception z) {
			z.printStackTrace();
		}

System.out.println("PDF ANNOTATE 2");

    }   
}

