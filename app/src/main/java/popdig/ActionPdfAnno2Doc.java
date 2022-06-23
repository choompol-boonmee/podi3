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
import org.apache.jena.rdf.model.*;

public class ActionPdfAnno2Doc extends PopupAction {
	static Logger log = Logger.getLogger(PopiangWindow.class);

	String vid;
	public ActionPdfAnno2Doc(JPopupMenu pop, TextFileInfo tfi, String lb, String i) {
		super(pop, tfi, lb);
		vid = i;
	}

	List<DocCclElem> getCclElemFromPdf(File f) {
		List<DocCclElem> aE1 = new ArrayList<>();
		Map<String,String> hAn = new HashMap<>();
		try {
			PDDocument pddoc = PDDocument.load(f);
			PDFRenderer pdfrnd = new PDFRenderer(pddoc);
			Iterable<PDPage> allPages = pddoc.getDocumentCatalog().getPages();
			int pno = 0;
			for(PDPage pg : allPages) {
				pno++;
				List<PDAnnotation> aAnn = pg.getAnnotations();
				if(aAnn.size()==0) continue;
				int ano = 0;
				for(PDAnnotation ann: aAnn) {
					String txt = ann.getContents();
					String typ = ann.getSubtype();
					PDRectangle rct = ann.getRectangle();
					if("FreeText".equals(typ)) {
						ano++;
						if(txt.startsWith("a")) {
							DocCclElem ccl = new DocCclElem(pno, ano, txt, tfinf);
							if(hAn.get(ccl.iri)!=null) {
								System.out.println("####### DUPLICATE "+ccl.iri+ " page "+ pno);
							}
							hAn.put(ccl.iri,ccl.iri);
							aE1.add(ccl);
						}
					}
				}
			}
			pddoc.close();
			boolean bLoop = true;
			while(bLoop) {
				bLoop = false;
				for(int i=0; i<aE1.size()-1; i++) {
					DocCclElem t1 = aE1.get(i);
					DocCclElem t2 = aE1.get(i+1);
					if(t1.aord>t2.aord) {
						aE1.set(i, t2);
						aE1.set(i+1, t1);
						bLoop = true;
					}
				}
			}
		} catch(Exception z) {
			z.printStackTrace();
		}
		return aE1;
	}

	Map<String,Map<String,String>> hElemProp;
	String type;

	String analyze() {
System.out.println("################## START ANALYSIS");
		hElemProp = new HashMap<>();
//		String tp = PopiangUtil.query0(vid+" a ?a").get();
		int i1,i2;
		String fnm = null;
		if(false) {
//		if("vo:PdfDoc".equals(type) && (i1=vid.indexOf(":"))>0) {
			fnm = vid.substring(0, i1);
			String dnm = vid.substring(0, i1-2);
			File f = new File(PopiangDigital.workDir+"/rdf/"+dnm+"/"+fnm+".ttl");
			System.out.println("fnm:"+fnm+" dnm:"+dnm+" f:"+f+" :"+f.exists());
			try {
				Model model = ModelFactory.createDefaultModel();
				model.read(new FileInputStream(f), null, "TTL");
				StmtIterator iter = model.listStatements();
				while (iter.hasNext()) {
					Statement stmt = iter.nextStatement();
					String sub = stmt.getSubject().toString();
					String pre = stmt.getPredicate().toString();
					RDFNode ob0 = stmt.getObject();
					String obj = stmt.getObject().toString();
					String s1="",s2="",s3="";
					if((i1=sub.lastIndexOf("/"))>0 && (i2=sub.indexOf("#"))>0) {
						s1 = sub.substring(i1+1).replace("#",":");
					}
					if((i1=pre.lastIndexOf("/"))>0 && (i2=pre.indexOf("#"))>0) {
						s2 = pre.substring(i1+1).replace("#",":");
						if("22-rdf-syntax-ns:type".equals(s2)) s2 = "a";
					}
					if(ob0.isLiteral()) {
						Literal ob1 = ob0.asLiteral();
						String typ = ob1.getDatatypeURI();
						if(typ.endsWith("#decimal")) {
							s3 = obj.substring(0, obj.indexOf("^^"));
						} else if(typ.endsWith("#integer")) {
							s3 = obj.substring(0, obj.indexOf("^^"));
						} else if(typ.endsWith("#string")) {
							if(obj.indexOf("\n")<0) {
								s3 = "'"+obj+"'";
							} else {
								s3 = "'''"+obj+"'''";
							}
						}
					} else if((i1=obj.lastIndexOf("/"))>0 && (i2=obj.indexOf("#"))>0) {
						s3 = obj.substring(i1+1).replace("#",":");
					} else {
						s3 = "("+obj+")";
					}
					if(s1.length()>0 && s1.length()<20 && !ob0.isAnon() && s2.startsWith("vp:")) {
						Map<String,String> hProp = hElemProp.get(s1);
						if(hProp==null) {
							hProp = new HashMap<>();
							hElemProp.put(s1, hProp);
						}
						hProp.put(s2, s3);
//						System.out.println("["+s1+"] ["+s2+"] ["+s3+"]");
					}
				}
			} catch(Exception z) {
			}
		}
		return fnm;
	}

    public void action() {

		type = PopiangUtil.query0(vid+" a ?a").get();

		String file = null;
		String output = PopiangUtil.query0(vid+" vp:output ?a").get();
		TextFileInfo tinf = null;
		if(type.equals("vo:PdfDoc")) {
			output = vid.substring(0,vid.indexOf(":"));
			String prf = output.substring(0, output.length()-2);
			file = "rdf/"+prf+"/"+output+".ttl";
			tinf = PopiangDigital.wind.hTabPane.get(file);
			if(tinf!=null) {
System.out.println("  PATH: "+tinf.path);
			}
			int tabcnt = PopiangDigital.wind.tabbedPane.getTabCount();
System.out.println("vo:PdfDoc = "+file);
			for(int i=0; i<tabcnt; i++) {
				String tt = PopiangDigital.wind.tabbedPane.getTitleAt(i);
				System.out.println("  "+i+":"+tt+" : "+tt.equals(file));
			}
		}
		if(output==null || output.length()<3) return;

		String rdfs = PopiangDigital.workDir+"/rdf/";
		File fdir = new File(rdfs+output);
		String prfx = "", fnm = "";
		if(output.matches("[a-z]{3,5}")) {
			prfx = output;
			fdir = new File(rdfs+output);
			String mx2 = PopiangDigital.wind.getRdfIdMax0(output);
			fnm = output + mx2;
		} else if(output.matches("[a-z]{3,5}[0-9A-Z]{2}")) {
			prfx = output.substring(0, output.length()-2);
			fdir = new File(rdfs+output.substring(0, output.length()-2));
			fnm = output;
		} else {
			return;
		}
		System.out.println("output: "+ fdir+" : "+ fdir.exists());
		if(!fdir.exists()) fdir.mkdirs();

System.out.println("prfx: "+ output);
System.out.println("fnm: "+ fnm);
//if(true) return;

		DefaultMutableTreeNode node = PopiangDigital.wind.getFolderNode();
		if(node==null) return;

		File rdf0 = new File(rdfs+prfx+"/"+fnm+".ttl");
		try {
			Path pth = Paths.get(rdf0.getAbsolutePath());

			String fnm0 = analyze();

			String ff = PopiangUtil.query0(vid+" vp:file ?a").get();
			File f = new File(PopiangDigital.workDir+"/res/"+ff.substring(5));
			String px1 = vid.substring(0,vid.indexOf(":"));
//System.out.println("ID: "+ vid+" ff:"+ ff+" :"+f.exists());

			List<DocCclElem> aE1 = getCclElemFromPdf(f);
//System.out.println("get collection");
			if(aE1.size()<2) return;

			DocCclElem e0 = aE1.get(0);
//			System.out.println("E1: den: "+ e0.den);
//			System.out.println("E1: biz: "+ e0.biz);

			Map<String,DocCclElem> hE1 = new HashMap<>();
			for(int i=0; i<aE1.size(); i++) {
				DocCclElem cc = aE1.get(i);
				hE1.put(cc.iri, cc);
			}
			for(int i=0; i<aE1.size(); i++) {
				DocCclElem cc = aE1.get(i);
//System.out.println("CC:["+ cc.iri+ "] : "+ (hE1.get(cc.iri)!=null));
				for(int j=0; j<cc.aChild.size(); j++) {
					String id2 = cc.aChild.get(j);
					String card = cc.aCard.get(j);
					DocCclElem cc2 = hE1.get(id2);
//System.out.println("  "+j+":["+ id2 + "]="+(cc2!=null));
					if(cc2!=null) {
						cc2.parent = cc;
						cc2.card = card;
						cc.aElem.add(cc2);
					} else {
					}
				}
			}

			String dsc,v0;
			String[] aProp = { 
				"vp:name", "vp:seqNo","vp:DEN","vp:BusinessTerm","vp:Cardinality" };
			StringBuffer buf = new StringBuffer(PopiangUtil.blankRdf(fnm,e0.biz,px1));
			buf.append("\n");

			buf.append(fnm+":1  a  vo:PdfDoc; ");
			Map<String,String> hProp = hElemProp.get(fnm0+":1");
			for(String p : aProp) {
				String v = "''";
				if(p.equals("vp:name")) v = "'"+e0.biz+"'";
				if(p.equals("vp:DEN")) v = "'"+e0.den+"'";
				if(p.equals("vp:BusinessTerm")) v = "'"+e0.biz+"'";
				if(p.equals("vp:Cardinality")) v = "'1..1'";
				if(hProp!=null && (v0=hProp.get(p))!=null) v = v0;
				buf.append(p+"  "+v+" ;\n");
			}
			int ccc = 0;
			if(e0.aElem.size()>0) {
				buf.append("vp:child (\n  ");
				for(int j=0; j<e0.aElem.size(); j++) {
					DocCclElem cc = e0.aElem.get(j);
					if(ccc%5==0 && ccc>0) buf.append("\n  ");
					else if(ccc>0) buf.append(" ");
					buf.append(fnm+":"+cc.iri);
					ccc++;
				}
				buf.append(") ;\n");
			}
			buf.append("vp:fileId "+vid+";\n");
			buf.append("vp:file '"+ ff +"';\n");
			dsc = "'''\n'''";
			if(hProp!=null && (v0=hProp.get("vp:desc"))!=null) dsc = v0;
			buf.append("vp:desc "+dsc+ " .\n");
			buf.append("\n");

			for(int i=1; i<aE1.size(); i++) {
				DocCclElem cc = aE1.get(i);
				hProp = hElemProp.get(fnm0+":"+cc.iri);
//System.out.println(" prop of "+fnm+":"+cc.iri+" is "+hProp);
				buf.append(fnm+":"+cc.iri+"  a  vo:DocElem; ");
				for(String p : aProp) {
					String v = "''";
					if(p.equals("vp:name")) v = "'"+cc.biz+"'";
					if(p.equals("vp:DEN")) v = "'"+cc.den+"'";
					if(p.equals("vp:BusinessTerm")) v = "'"+cc.biz+"'";
					if(p.equals("vp:Cardinality")) v = "'"+cc.card+"'";
					if(hProp!=null && (v0=hProp.get(p))!=null) v = v0;
					buf.append(p+"  "+v+" ;\n");
				}
				if(cc.aElem.size()>0) {
					buf.append("vp:child (");
					for(int j=0; j<cc.aElem.size(); j++) {
						DocCclElem cc2 = cc.aElem.get(j);
						if(j%5==0 && j>0) buf.append("\n  ");
						else if(j>0) buf.append(" ");
						buf.append(fnm+":"+cc2.iri);
					}
					buf.append(") ;\n");
				}
				buf.append("vp:pdfDoc "+fnm+":1 ;\n");
				dsc = "'''\n'''";
				buf.append("vp:desc "+dsc+ " .\n");
			}
			Files.write(pth, buf.toString().getBytes());
			PopiangUtil.readRdfModel(rdf0);

			final TextFileInfo tfinfo = tinf;
			Thread.sleep(500);
			SwingUtilities.invokeLater(new Runnable() { public void run() {
				if(tfinfo!=null) {
					try {
						byte[] buf = Files.readAllBytes(tfinfo.path);
						String actual = new String(buf,"UTF-8");
						tfinfo.text.setText(actual);
						tfinfo.text.setDirty(false);
						tfinfo.text.discardAllEdits();
					} catch(Exception z) {
						z.printStackTrace();
					}
				} else {
					PopiangDigital.wind.populateAllTreeNode();
				}
//				PopiangDigital.wind.populateTreeNode(node, new File(rdfs+prfx));
//				((DefaultTreeModel) PopiangDigital.wind.tree.getModel()).reload();
//				TreePath path = new TreePath(node.getFirstLeaf().getPath());
//				PopiangDigital.wind.tree.setSelectionPath(path);
			}});
			
		} catch(Exception z) {
			z.printStackTrace();
		}
    }   
}

