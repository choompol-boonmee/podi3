package popdig;

import java.io.*;
import java.util.Hashtable;
import javax.swing.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.query.*;
import org.apache.log4j.Logger;
import org.apache.jena.ttl.turtle.TurtleParseException;
import javax.swing.tree.*;

public class RdfModelInfo {
	public String sPrefix, title="";
	public File fDir,fRdf;
	public Model model;
	public String sBaseIRI;
	public JLabel label;
//	public FileTreeCellRenderer ftcr;
	public DefaultTreeCellRenderer tcr;
	public DefaultMutableTreeNode node;

	static Logger log = Logger.getLogger(PopiangWindow.class);

	public Hashtable<String,String> hId2Nm;

	public String analyze(File f) {
		try {
			fRdf = f;
			fDir = f.getParentFile();
			sPrefix = f.getName();
			sPrefix = sPrefix.substring(0,sPrefix.indexOf("."));
			sBaseIRI = PopiangDigital.sBaseIRI +"/"+ sPrefix + "#";
			model = ModelFactory.createDefaultModel();
log.info("RDF35: "+fRdf);
			model.read(new FileInputStream(fRdf), null, "TTL");
			int c = 0;
			hId2Nm = new Hashtable<>();
			StmtIterator iter = model.listStatements();
			while (iter.hasNext()) {
				Statement stmt = iter.nextStatement();
				String sub = stmt.getSubject().toString();
				String obj = stmt.getObject().toString();
				String pre = stmt.getPredicate().toString();
				String s1="",s2="",s3="";
				int i1,i2,i3;
				if((i1=sub.lastIndexOf("/"))>0 && (i2=sub.indexOf("#"))>0) {
					s1 = sub.substring(i1+1);
				}
				if((i1=pre.lastIndexOf("/"))>0 && (i2=pre.indexOf("#"))>0) {
					s2 = pre.substring(i1+1);
				}
				if(s1.length()>0 && s2.equals("vp#name")) {
					hId2Nm.put(s1.replace("#",":"), obj);
//					log.info("   "+s1+","+s2+","+obj);
				}
				if(s1.endsWith("#0") && s2.indexOf("title")>0) {
					title = obj;
//					log.info("   "+s1+":"+s1.endsWith("#0")+","+s2+","+obj+" l:"+label);
					if(label!=null) {
						label.setText(sPrefix+": "+title);
						label.setToolTipText(title);
						PopiangDigital.wind.treeModel.reload(node);
//						label.validate();
//						tcr.validate();
//						PopiangDigital.wind.tree.repaint();
//						PopiangDigital.wind.tree.validate();
//						((DefaultTreeModel)tree.getModel()).reload(node);

					}
				}
			}
//log.info("     c:"+ c);
		} catch(TurtleParseException z) {
System.out.println("err1");
			int ln = -1, cn = -1;
			String ms = "", lno="",cno="";
			String[] wds = z.getMessage().split("\n");
			if(wds.length>0) {
				int i1,i2,i3,i4,i5;
				i1 = wds[0].indexOf("line ");
				i2 = wds[0].indexOf(",");
				i3 = wds[0].indexOf("column ");
				i4 = wds[0].indexOf(".");
				if(i1<0) i1 = wds[0].indexOf("Line ");
				if(i4<0) i4 = wds[0].indexOf(":");
				if(i1>=0 && i2>i1 && i3>i2 && i4>i3) {
					lno = wds[0].substring(i1+5, i2);
					cno = wds[0].substring(i3+7, i4);
					try { ln = Integer.parseInt(lno); } catch(Exception y){}
					try { cn = Integer.parseInt(cno); } catch(Exception y){}
				}
				if(wds[0].startsWith("Lexical")) {
					ms = "Lexical";
				} else if(wds[0].startsWith("Encountered")) {
					if(i1>0) { ms = wds[0].substring(12+2,i1-5).trim(); }
					else { ms = wds[0]; }
				} else {
					if((i5=wds[0].indexOf("Unresolved"))>0) {
//System.out.println("UNRES: "+ i1+","+i2+"="+lno+","+ln+" i3:"+i3+","+i4);
						ms = wds[0].substring(i5);
					} else {
						if(i1>0) { ms = wds[0].substring(0,i1).trim(); }
						else { ms = wds[0]; }
					}
				}
			}
			return ln+","+cn+": "+ ms;
		} catch(FileNotFoundException x) {
			System.out.println("err2: "+ x);
		} catch(org.apache.jena.riot.RiotException x) {
			System.out.println("err3: "+ x);
		}
		return null;
	}
}

