package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.Stack;
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
import java.util.Arrays;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.multipdf.PDFCloneUtility;
import org.apache.pdfbox.pdmodel.common.*;
import org.apache.pdfbox.pdmodel.interactive.annotation.*;
import org.apache.pdfbox.cos.*;
import org.apache.pdfbox.rendering.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class DataSet2StdA extends ReportExecute {
	String vo = "vo:DataSet";
	public DataSet2StdA(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
	}

	boolean bShowCmt = true;
	int spc = 2;

	void walkBbie(String id, List<CCLibrary> aCCL, int lv) {
		try {
			QueryExecute q0;
			q0 = PopiangUtil.query0(id
				+" vp:DEN ?den ; vp:name ?nm; vp:BusinessTerm ?bt; vp:Cardinality ?ca .");
			String ca = q0.get("?ca");

			CCLibrary ccl = new CCLibrary();
			ccl.id = id;
			ccl.lv = lv;
			ccl.den = q0.get("?den");
if(ccl.den==null) System.out.println("NO DEN2 : "+id);
			ccl.biz = q0.get("?bt");
			ccl.name = q0.get("?nm");
			ccl.card = ca;
			aCCL.add(ccl);

			q0 = PopiangUtil.query0(id+" vp:child(*) ?ch .");
			String[] chd = q0.gets("?ch");
			if(chd.length>0) {
				// ASBIE
				ccl.type = "ASBIE";
				// ABIE
				if(lv<8) walkAbie(id, aCCL, lv, ca);
			} else {
				// BBIE
				ccl.type = "BBIE";
			}
		} catch(Exception x) {
		}
	}

	void walkAbie(String id, List<CCLibrary> aCCL, int lv, String ca) {
		try {
			QueryExecute q0;
			q0 = PopiangUtil.query0(id+" vp:DEN ?den ; vp:name ?nm; vp:BusinessTerm ?bt .");
			CCLibrary ccl = new CCLibrary();
			ccl.id = id;
			ccl.lv = lv;
			ccl.name = q0.get("?nm");
			ccl.den = q0.get("?den");
if(ccl.den==null) System.out.println("NO DEN : "+id);
			ccl.biz = q0.get("?bt");
			ccl.card = ca;
			ccl.type = "ABIE";
			aCCL.add(ccl);
			q0 = PopiangUtil.query0(id+" vp:child(*) ?ch .");
			String[] chd = q0.gets("?ch");
			for(int i=0; i<chd.length; i++) {
				walkBbie(chd[i], aCCL, lv+1);
			}
		} catch(Exception x) {
		}
	}

	String sDT = "Code,ID,Date,Date Time,Quantity,Text,Numeric,Amount,Indicator"
		+",Measure,Binary Object";

	String uriUDT,uriQDT,uriRAM,uriRSM,uriCLM;

	void createCodeList(File fClmCsv, File fClmJson, File fClmXml, String[] cds, String[] nms, String pk, String q) {
		try {
//			System.out.println("code list: "+ cds.length);
//			System.out.println("name list: "+ nms.length);
			BufferedWriter bwC,bwJ,bwX;
			bwC = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fClmCsv)));
			bwJ = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fClmJson)));
			bwX = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fClmXml)));
			bwX.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			bwX.write("<xsd:schema\n");
			bwX.write("  xmlns:ccts=\"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2\"\n");
			bwX.write("  targetNamespace=\""+pk+"\"");
			bwX.write("  xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n");
			bwX.write("  >\n");
			bwX.write("  <xsd:simpleType name=\""+q+"\">\n");
			bwX.write("    <xsd:restriction base=\"xsd:token\">\n");

			bwJ.write("[");

			for(int i=0; i<cds.length && i<nms.length; i++) {

				bwX.write("      <xsd:enumeration value=\""+cds[i]+"\">\n");
				bwX.write("        <xsd:annotation>\n");
				bwX.write("          <xsd:documentation xml:lang=\"th\">\n");
				bwX.write("            <ccts:Name>"+nms[i]+"</ccts:Name>\n");
				bwX.write("          </xsd:documentation>\n");
				bwX.write("        </xsd:annotation>\n");
				bwX.write("      </xsd:enumeration>\n");

				if(i>0) bwJ.write(",");
				bwJ.write("\n");
				bwJ.write("  {\n");
				bwJ.write("    \"vp:code\" : \""+ cds[i]+ "\",\n");
				bwJ.write("    \"vp:name\" : \""+ nms[i]+ "\"\n");
				bwJ.write("  }");

				bwC.write("\""+cds[i]+"\",\""+nms[i]+"\"\n");

			}
			bwJ.write("\n");
			bwJ.write("]");

			bwX.write("    </xsd:restriction>\n");
			bwX.write("  </xsd:simpleType>\n");

			bwX.write("</xsd:schema>\n");
			bwC.close();
			bwJ.close();
			bwX.close();
		} catch(Exception x) {
			x.printStackTrace();
		}
	}

	void copyCodeList(File icsv, File ocsv, File ijs, File ojs, File ix, File ox, String pk) {
		try {
			String line;
			BufferedWriter bw;
			BufferedReader br;

			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ocsv)));
			br = new BufferedReader(new InputStreamReader(new FileInputStream(icsv)));
			while((line=br.readLine())!=null) {
				bw.write(line);
				bw.newLine();
			}
			br.close();
			bw.close();

			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ojs)));
			br = new BufferedReader(new InputStreamReader(new FileInputStream(ijs)));
			while((line=br.readLine())!=null) {
				bw.write(line);
				bw.newLine();
			}
			br.close();
			bw.close();

			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ox)));
			br = new BufferedReader(new InputStreamReader(new FileInputStream(ix)));
			while((line=br.readLine())!=null) {
				line = line.replace("___IRIBASE___",pk);
				bw.write(line);
				bw.newLine();
			}
			br.close();
			bw.close();
		} catch(Exception x) {
			x.printStackTrace();
		}
	}

    public void action() {

		String vid = PopiangDigital.getObject(vo);
		init();
		if(maxid<0) return;
		int id = maxid;
		id++;

		String sDir = pref.substring(0,pref.length()-2);
		String sPjDir = sDir+"/"+pref+"/"+pref+"-"+id;
		File fPjDir = new File(PopiangDigital.workDir+"/res/"+sPjDir);
System.out.println("project dir: "+ fPjDir);
		if(!fPjDir.exists()) fPjDir.mkdirs();

		QueryExecute q0;
		q0 = PopiangUtil.query0(vid+" vp:name ?name; vp:uriUDT ?udt; vp:uriQDT ?qdt; "
			+ "vp:uriRAM ?ram; vp:uriRSM ?rsm; vp:uriCLM ?clm .");
		String vnm = q0.get("?name");
		uriUDT = q0.get("?udt");
		uriQDT = q0.get("?qdt");
		uriRAM = q0.get("?ram");
		uriRSM = q0.get("?rsm");
		uriCLM = q0.get("?clm");

		StringBuffer rs = new StringBuffer(pref+":"+id+" a vo:DataSetResult; vp:name 'DataSetResult "+vnm+"' ;\n");

//		String vnm = PopiangUtil.query0(vid+" vp:name ?name .").get();
		q0 = PopiangUtil.query0(vid+" vp:qdtList(*) ?cd . ?cd vp:DEN ?de . ?cd vp:name ?nm . ?cd a ?tp .");
		String[] cdls = q0.gets("?cd");
		String[] denn = q0.gets("?de");
		String[] nams = q0.gets("?nm");
		String[] typs = q0.gets("?tp");

		//============ QDT LIST ============
		Map<String,String> hQDT = new HashMap<>();
		StringBuffer cl = new StringBuffer();
		for(int i=0; i<cdls.length; i++) {
			String d = cdls[i];
			String q = PopiangUtil.xmlNDR(denn[i]);
			String clmCsv = sPjDir+"/CLM/"+q+".csv";
			String clmJson = sPjDir+"/CLM/"+q+".json";
			String clmXml = sPjDir+"/CLM/"+q+".xml";
			File fClmCsv = new File(PopiangDigital.workDir+"/res/"+clmCsv);
			File fClmJson = new File(PopiangDigital.workDir+"/res/"+clmJson);
			File fClmXml = new File(PopiangDigital.workDir+"/res/"+clmXml);
			String pk = uriCLM.replace("<package>",q);
			if("vo:Code".equals(typs[i])) {
				q0 = PopiangUtil.query0(d+" vp:fileCSV ?c . "+d+" vp:fileJSON ?j . "+d+" vp:fileXML ?x .");
				if(!fClmXml.getParentFile().exists()) fClmXml.getParentFile().mkdirs();
				File ic = new File(PopiangDigital.workDir+"/res/"+q0.get("?c").substring(5));
				File ij = new File(PopiangDigital.workDir+"/res/"+q0.get("?j").substring(5));
				File ix = new File(PopiangDigital.workDir+"/res/"+q0.get("?x").substring(5));
				copyCodeList(ic,fClmCsv, ij,fClmJson, ix, fClmXml, pk);
				cl.append("  [ vp:id "+cdls[i]+" ;\n"
					+ "    vp:DEN '"+denn[i]+"' ;\n"
					+ "    vp:name '"+nams[i]+"' ;\n"
					+ "    vp:fileCsv 'file:"+ clmCsv +"' ;\n"
					+ "    vp:fileJson 'file:"+ clmJson +"' ;\n"
					+ "    vp:fileXml 'file:"+ clmXml +"' ;\n"
					+ "    vp:type '"+q+"'\n"
					+ "    ]\n");
			} else if("vo:QDT".equals(typs[i]) && denn[i].endsWith("Code")) {
				q0 = PopiangUtil.query0(d+" vp:codeList(*) ?cd . ?cd vp:code ?c . ?cd vp:name ?n .");
				String[] cds = q0.gets("?c");
				String[] nms = q0.gets("?n");
				createCodeList(fClmCsv, fClmJson, fClmXml, cds, nms, pk, q);
				if(!fClmXml.getParentFile().exists()) fClmXml.getParentFile().mkdirs();
				cl.append("  [ vp:id "+cdls[i]+" ;\n"
					+ "    vp:DEN '"+denn[i]+"' ;\n"
					+ "    vp:fileCsv 'file:"+ clmCsv +"' ;\n"
					+ "    vp:fileJson 'file:"+ clmJson +"' ;\n"
					+ "    vp:fileXml 'file:"+ clmXml +"' ;\n"
					+ "    vp:name '"+nams[i]+"' ;\n"
					+ "    vp:type '"+q+"'\n"
					+ "    ]\n");
//			} else if("vo:QDT".equals(typs[i])) {
			} else {
				cl.append("  [ vp:id "+cdls[i]+" ;\n"
					+ "    vp:DEN '"+denn[i]+"' ;\n"
					+ "    vp:name '"+nams[i]+"' ;\n"
					+ "    vp:type '"+typs[i]+"'\n"
					+ "    ]\n");
			}
			hQDT.put(denn[i],cdls[i]);
		}

		StringBuffer cm = new StringBuffer();
		StringBuffer er = new StringBuffer();
		StringBuffer fm = new StringBuffer();
		String[] docs = PopiangUtil.query0(vid+" vp:docList(*) ?doc .").gets("?doc");

		List<CCLibrary> aAbie = new ArrayList<>();
		List<List<CCLibrary>> aDocs = new ArrayList<>();
		List<CCLibrary> aRSM = new ArrayList<>();
		List<CCLibrary> aRAM = new ArrayList<>();
		List<String> aABIE0 = new ArrayList<>();
		Map<String,List<CCLibrary>> haRAM = new HashMap<>();
		List<CCLibrary> aABIE2 = new ArrayList<>();
		File[] afPdf = new File[docs.length];

		//============ ALL FORM ANALYSIS ============
		for(int i=0; i<docs.length; i++) {
			List<CCLibrary> aCCL = new ArrayList<>();
			aDocs.add(aCCL);

			String sPdf = PopiangUtil.query0(docs[i]+" vp:file ?f").get();
			if(!sPdf.startsWith("file:")) continue;
			afPdf[i] = new File(PopiangDigital.workDir+"/res/"+sPdf.substring(5));
			if(!afPdf[i].exists()) continue;

			walkAbie(docs[i], aCCL, 0, "1..1");
			String fnm = docs[i].replace(":","-");

			String note = sPjDir+"/DOC/"+fnm+"_note.txt";
			File fNote = new File(PopiangDigital.workDir+"/res/"+note);
			if(!fNote.getParentFile().exists()) fNote.getParentFile().mkdirs();

			try {
				BufferedWriter bwNote = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fNote)));
				int i1,i2,i3;
System.out.println("RSM: "+ docs[i]+" "+sPdf);

				//========== FORM ANALYSIS ===========
				Stack<String[]> stAbie = new Stack<>();
				for(int j=0; j<aCCL.size(); j++) {
					// CCL
					CCLibrary ccl = aCCL.get(j);
					while(ccl.lv<stAbie.size()) {
						stAbie.pop();
					}
					String[] dens = ccl.den.split("\\.");
					if("ABIE".equals(ccl.type)) {
						// ABIE BEGIN
						if(dens.length==1) {
							if((i1=dens[0].indexOf("_"))>0) {
								ccl.OTQ = dens[0].substring(0, i1);
								ccl.OT = dens[0].substring(i1+1).trim();
								ccl.PT = "Details";
							} else {
								er.append("  ('A1c' "+docs[i]+" "+ccl.id+" '"+ccl.den+"')\n");
							}
						} else if(dens.length==2) {
							if((i1=dens[1].indexOf("_"))>0) {
								ccl.OTQ = dens[1].substring(0, i1);
								ccl.OT = dens[1].substring(i1+1).trim();
								ccl.PT = "Details";
							} else if((i1=dens[0].indexOf("_"))>0) {
								ccl.OTQ = dens[0].substring(0, i1);
								ccl.OT = dens[0].substring(i1+1).trim();
								ccl.PT = "Details";
							} else {
								er.append("  ('A1d' "+docs[i]+" "+ccl.id+" '"+ccl.den+"')\n");
							}
						} else {
							System.out.println("Ax:"+ccl.den+" :"+ccl.id);
							er.append("  ('AX' "+docs[i]+" "+ccl.id+" '"+ccl.den+"')\n");
						}
						// ABIE END
					} else if("ASBIE".equals(ccl.type)) {
						// ASBIE BEGIN
						String[] obj = stAbie.peek();
						ccl.OTQ = obj[0];
						ccl.OT = obj[1];
						if(dens.length==1) {
							if((i1=dens[0].indexOf("_"))>0) {
								ccl.DTQ = ccl.PTQ = dens[0].substring(0,i1);
								ccl.DT = ccl.PT = dens[0].substring(i1+1);
							} else {
								er.append("  ('A1b' "+docs[i]+" "+ccl.id+" '"+ccl.den+"')\n");
							}
						} else if(dens.length==2) {
							if((i1=dens[1].indexOf("_"))>0) {
								if((i2=dens[0].indexOf("_"))>0) {
									ccl.PTQ = dens[0].substring(0,i2);
									ccl.PT = dens[0].substring(i2+1);
									ccl.DTQ = dens[1].substring(0,i1);
									ccl.DT = dens[1].substring(i1+1);
								} else {
									ccl.PT = dens[0];
									ccl.DTQ = dens[1].substring(0,i1);
									ccl.DT = dens[1].substring(i1+1);
								}
							} else if(dens[1].indexOf("Details")>0) {
								ccl.DTQ = ccl.PTQ = dens[0].substring(0,i1);
								ccl.DT = ccl.PT = dens[0].substring(i1+1);
							} else {
								er.append("  ('A1b' "+docs[i]+" "+ccl.id+" '"+ccl.den+"')\n");
							}
						} else {
							System.out.println("Ax:"+ccl.den+" :"+ccl.id);
							er.append("  ('AX' "+docs[i]+" "+ccl.id+" '"+ccl.den+"')\n");
						}
						// ASBIE END
					} else if("BBIE".equals(ccl.type)) {
						// BBIE BEGIN
						String[] obj = stAbie.peek();
						ccl.OTQ = obj[0];
						ccl.OT = obj[1];
						if(dens.length==1) {
							ccl.PT = dens[0].trim();
							ccl.DT = "Text";
							ccl.DTQ = "";
						} else if(dens.length==2) {
							String[] qds = dens[0].split("_");
							if(qds.length==1) {
								ccl.PT = qds[0];
							} else if(qds.length==2) {
								ccl.PT = qds[1];
								ccl.PTQ = qds[0];
							}
							String[] wds = dens[1].split("_");
							if(wds.length==1) {
								String dt = wds[0].trim();
								if(!sDT.contains(dt)) {
									er.append("  ('B2a' "+docs[i]+" "+ccl.id+" '"+ccl.den+"' '"+dt+"')\n");
								} else {
									ccl.DT = dt;
									ccl.DTQ = "";
								}
							} else if(wds.length==2) {
								String dt = wds[1].trim();
								if(!sDT.contains(dt)) {
									er.append("  ('B3a' "+docs[i]+" "+ccl.id+" '"+ccl.den+"')\n");
								} else {
									ccl.DT = dt; ccl.DTQ = wds[0].trim();
									String qdt = ccl.DTQ+"_ "+ccl.DT;
									if(hQDT.get(qdt)==null) {
										cm.append("  ("+docs[i]+" "+ccl.id+" '"+ccl.DTQ+"' '"+ccl.DT+"')\n");
									}
								}
							} else {
								er.append("  ('BX' "+docs[i]+" "+ccl.id+" '"+ccl.den+"')\n");
							}
						} else {
						}
						// BBIE END
					}
					if(ccl.OTQ!=null) ccl.OTQ = ccl.OTQ.trim();
					if(ccl.OT!=null) ccl.OT = ccl.OT.trim();
					if(ccl.PTQ!=null) ccl.PTQ = ccl.PTQ.trim();
					if(ccl.PT!=null) ccl.PT = ccl.PT.trim();
					if(ccl.DTQ!=null) ccl.DTQ = ccl.DTQ.trim();
					if(ccl.DT!=null) ccl.DT = ccl.DT.trim();
					bwNote.write(ccl.id+", "+ccl.type+", "+ccl.lv+"="+stAbie.size()+", "+ccl.den+"\n"
						+"  otq:"+ccl.OTQ+" ot:"+ccl.OT+" ptq:"+ccl.PTQ+" pt:"+ccl.PT
						+" dtq:"+ccl.DTQ+" dt:"+ccl.DT+"\n");
					if("ABIE".equals(ccl.type)) {
						String[] ss = {ccl.OTQ,ccl.OT};
						stAbie.push(ss);
					}
				}
				bwNote.close();

				//============ MAKE ABIE CHILDREN OF ONE FORM ============
				for(int j=0; j<aCCL.size(); j++) {
					if("ABIE".equals(aCCL.get(j).type)) {
						CCLibrary ccl = aCCL.get(j);
						ccl.den2 = ccl.OTQ+"_ "+ccl.OT;
						aAbie.add(ccl);
						for(int k=j+1; k<aCCL.size(); k++) {
							CCLibrary ccl2 = aCCL.get(k);
							if("ABIE".equals(ccl2.type)) {
							} else if(ccl.lv+1==ccl2.lv) {
								String ptq = (ccl2.PTQ!=null && ccl2.PTQ.length()>0)? ccl2.PTQ+"_ " : "";
								String pt = ccl2.PT;
								String dtq = (ccl2.DTQ!=null && ccl2.DTQ.length()>0)? ccl2.DTQ+"_ " : "";
								ccl2.den2 = ptq + ccl2.PT + ". "+dtq + ccl2.DT;
								ccl.chd.add(ccl2);
							} else if(ccl2.lv<=ccl.lv) {
								break;
							}
						}
					}
				}

			} catch(Exception z) {
				z.printStackTrace();
			}
			//============== END ONE FORM ANALYSIS ================
			q0 = PopiangUtil.query0(docs[i]+" vp:name ?nm; vp:DEN ?den .");
			String name = q0.get("?nm");
			String den = q0.get("?den");
			fm.append("  [vp:id "+docs[i]+"; vp:name '"+name+"' ;\n"
			        + "     vp:fileCcl 'file:"+note+"';\n"
			        + "       ]\n");
		}

		//========= MAKE TAG NAME =========
		for(int j=0; j<aDocs.size(); j++) {
			List<CCLibrary> aDoc = aDocs.get(j);
			for(int i=0; i<aDoc.size(); i++) {
				CCLibrary ccl = aDoc.get(i);
				if(ccl.tag==null) {
					if("ABIE".equals(ccl.type)) {
						if(ccl.lv==0) ccl.tag = PopiangUtil.xmlNDR(ccl.den2);
						else ccl.tag = "";
					} else if("ASBIE".equals(ccl.type) && i<aDoc.size()-1) {
						CCLibrary ccl2 = aDoc.get(i+1);
						String tag = ccl.den2 + " "+ ccl2.den2;
						ccl.tag = PopiangUtil.xmlNDR(tag);
					} else if("BBIE".equals(ccl.type)) {
						ccl.tag = PopiangUtil.xmlNDR(ccl.den2);
					}
				}
			}
		}

		//=========== DIVIDE ALL CCL INTO RSM AND RAM ===========
		for(int i=0; i<aAbie.size(); i++) {
			CCLibrary ccl = aAbie.get(i);
			if(ccl.lv==0) aRSM.add(ccl);
			else aRAM.add(ccl);
		}
		CCLibrary[] rsms = aRSM.toArray(new CCLibrary[aRSM.size()]);
		CCLibrary[] rams = aRAM.toArray(new CCLibrary[aRAM.size()]);
		Arrays.sort(rsms);
		Arrays.sort(rams);

		//=========== COLLECT DUBLICATE ABIE ===============
		for(int j=0; j<rams.length; j++) {
			CCLibrary ccl = rams[j];
			List<CCLibrary> aCCL = haRAM.get(ccl.den2);
			if(aCCL==null) {
				aCCL = new ArrayList<>();
				haRAM.put(ccl.den2, aCCL);
				aABIE0.add(ccl.den2);
			}
			aCCL.add(ccl);
		}

		//=========== COLLECT DUBLICATE ABIE ===============
		for(int j=0; j<aABIE0.size(); j++) {
			String abie = aABIE0.get(j);
			List<CCLibrary> aL = haRAM.get(abie);
			CCLibrary ABIE = aL.get(0).clone();
			aABIE2.add(ABIE);
			Map<String,String> hM = new HashMap<>();
			for(int k=0; k<aL.size(); k++) {
				CCLibrary c2 = aL.get(k);
				for(int l=0; l<c2.chd.size(); l++) {
					CCLibrary c3 = c2.chd.get(l);
					if(hM.get(c3.den2)!=null) continue;
					hM.put(c3.den2, c3.den2);
					ABIE.chd.add(c3);
				}
			}
		}

		//========== CONCLUDE ANALYSIS ===========
		String fms = fm.toString();
		if(fms.length()>0) {
			rs.append("#\n");
			rs.append("#======= DOCUMENT ANALYSIS ========\n");
			rs.append("vp:analysis (\n"+fms+");\n");
		}

		//========== CONCLUDE RAM ===========
		StringBuffer sbie = new StringBuffer();
		CreateRsmXml ram = new CreateRsmXml(aABIE2);
		for(int j=0; j<aABIE2.size(); j++) {
			CCLibrary ccl = aABIE2.get(j);
			String xml = PopiangUtil.xmlNDR(ccl.den2);
			String ramPng = sPjDir+"/RAM/"+xml+".png";
			File fRamPng = new File(PopiangDigital.workDir+"/res/"+ramPng);
			if(!fRamPng.getParentFile().exists()) fRamPng.getParentFile().mkdirs();
			CreateImageRSM png = new CreateImageRSM(fRamPng, ccl, "ABIE");
			png.create();

			String ramXsd = sPjDir+"/RAM/"+xml+"-xsd.xml";
			File fRamXsd = new File(PopiangDigital.workDir+"/res/"+ramXsd);
			ram.createRamXsdFile(ccl, fRamXsd, uriRAM, uriQDT, uriUDT);

			sbie.append("  [ vp:DEN '"+ccl.den2+"' ;\n"
					  + "    vp:pict 'file:"+ramPng+"' ;\n"
					  + "    vp:ramXsd 'file:"+ramXsd+"'\n"
					  + "      ]\n");
		}
		String sbies = sbie.toString();
		if(sbies.length()>0) {
			rs.append("#\n");
			rs.append("#======= REFERENCE ABIE ========\n");
			rs.append("vp:reference (\n"+sbies+");\n");
		}

		//========== CONCLUDE RSM ===========
		StringBuffer msg = new StringBuffer();
		for(int j=0; j<aRSM.size(); j++) {

			CCLibrary ccl = aRSM.get(j);
			List<CCLibrary> aDoc = aDocs.get(j);

			String xml = PopiangUtil.xmlNDR(ccl.den2);

			String rsmPng = sPjDir+"/RSM/"+xml+".png";
			File fRsmPng = new File(PopiangDigital.workDir+"/res/"+rsmPng);
			if(!fRsmPng.getParentFile().exists()) fRsmPng.getParentFile().mkdirs();
			CreateImageRSM png = new CreateImageRSM(fRsmPng, ccl, "RSM");
			png.create();

			CreateRsmXml rsm = new CreateRsmXml(aDoc);
			String rsm0 = uriRSM.replace("<package>",xml);

			String rsmXml = sPjDir+"/RSM/"+xml+".xml";
			File fRsmXml = new File(PopiangDigital.workDir+"/res/"+rsmXml);
			rsm.createXmlFile(fRsmXml, rsm0, uriRAM);

			String rsmJson = sPjDir+"/RSM/"+xml+".json";
			File fRsmJson = new File(PopiangDigital.workDir+"/res/"+rsmJson);
			rsm.createJsonFile(fRsmJson);

			String rsmJsLD = sPjDir+"/RSM/"+xml+"LD.json";
			File fRsmJsLD = new File(PopiangDigital.workDir+"/res/"+rsmJsLD);
			rsm.createJsonLDFile(fRsmJsLD, rsm0, uriRAM);

			String rsmCsv = sPjDir+"/RSM/"+xml+".csv";
			File fRsmCsv = new File(PopiangDigital.workDir+"/res/"+rsmCsv);
			rsm.createCsvFile(fRsmCsv);

			String rsmXsd = sPjDir+"/RSM/"+xml+"-xsd.xml";
			File fRsmXsd = new File(PopiangDigital.workDir+"/res/"+rsmXsd);
			rsm.createXsdFile(aDoc.get(0), fRsmXsd, rsm0, uriRAM, uriQDT, uriUDT);

			String wsdlSend = sPjDir+"/RSM/"+xml+"-wsdlSend.xml";
			File fWsdlSend = new File(PopiangDigital.workDir+"/res/"+wsdlSend);
			rsm.createWsdlSend(fWsdlSend, rsm0);

			String wsdlQuery = sPjDir+"/RSM/"+xml+"-wsdlQuery.xml";
			File fWsdlQuery = new File(PopiangDigital.workDir+"/res/"+wsdlQuery);
			rsm.createWsdlQuery(fWsdlQuery, rsm0);

			//======= CLONE PDF =======
			String pdfOrg = sPjDir+"/RSM/"+xml+"-PdfOrg.pdf";
			File fPdfOrg = new File(PopiangDigital.workDir+"/res/"+pdfOrg);
			String pdfMod = sPjDir+"/RSM/"+xml+"-PdfMod.pdf";
			File fPdfMod = new File(PopiangDigital.workDir+"/res/"+pdfMod);
			String pdfCln = sPjDir+"/RSM/"+xml+"-PdfCln.pdf";
			File fPdfCln = new File(PopiangDigital.workDir+"/res/"+pdfCln);
			try {
				PDDocument idoc = PDDocument.load(afPdf[j]);
				PDDocument odoc = new PDDocument();
				PDDocument mdoc = new PDDocument();
				PDDocument cdoc = new PDDocument();
				odoc.getDocument().setVersion(idoc.getDocument().getVersion());
				odoc.setDocumentInformation(idoc.getDocumentInformation());
				odoc.getDocumentCatalog().setViewerPreferences(idoc.getDocumentCatalog().getViewerPreferences());
				mdoc.getDocument().setVersion(idoc.getDocument().getVersion());
				mdoc.setDocumentInformation(idoc.getDocumentInformation());
				mdoc.getDocumentCatalog().setViewerPreferences(idoc.getDocumentCatalog().getViewerPreferences());
				cdoc.getDocument().setVersion(idoc.getDocument().getVersion());
				cdoc.setDocumentInformation(idoc.getDocumentInformation());
				cdoc.getDocumentCatalog().setViewerPreferences(idoc.getDocumentCatalog().getViewerPreferences());
				PDFCloneUtility cloner = new PDFCloneUtility(odoc);
				PDFCloneUtility mclone = new PDFCloneUtility(mdoc);
				PDFCloneUtility cclone = new PDFCloneUtility(cdoc);
				int ip = 0;
				for (PDPage op : idoc.getPages()) {
					List<PDAnnotation> aAnn = op.getAnnotations();
					if(aAnn.size()==0) break;
					COSDictionary pd = (COSDictionary) cloner.cloneForNewDocument(op);
					COSDictionary md = (COSDictionary) mclone.cloneForNewDocument(op);
					COSDictionary cd = (COSDictionary) cclone.cloneForNewDocument(op);
					odoc.addPage(new PDPage(pd));

					PDPage mpg = new PDPage(md);
					mdoc.addPage(mpg);
//					List<PDAnnotation> aAnm = mpg.getAnnotations();
//					aAnm.clear();
					for(PDAnnotation ann: aAnn) {
						String typ = ann.getSubtype();
						if("FreeText".equals(typ)) {
							PDRectangle rct = ann.getRectangle();
							float llx = rct.getLowerLeftX();
							float lly = rct.getLowerLeftY();
							String txt = ann.getContents();

							if(txt.startsWith("a")) {
								String[] txts = txt.split(":");
								String cmd = txts[0].trim();
							}
						}
					}
/*
					PDPageContentStream cos = new PDPageContentStream(mdoc, mpg);
					cos.setFont(PDType1Font.HELVETICA, 14);
					cos.beginText();
					cos.newLineAtOffset(100, 100);
					cos.showText("BBBBBBBBBBB");
					cos.endText();
*/

					PDPage cpg = new PDPage(cd);
					aAnn = cpg.getAnnotations();
					aAnn.clear();
//					cpg.setAnnotations(null);
					cdoc.addPage(cpg);

//					mdoc.addPage(new PDPage(md));
//					odoc.addPage(page);
//					ip++;
//					if(ip>=2) break;
				}
				fPdfOrg.delete();
				mdoc.save(fPdfMod);
				mdoc.close();
				odoc.save(fPdfOrg);
				odoc.close();
				cdoc.save(fPdfCln);
				cdoc.close();
				idoc.close();
			} catch(Exception z) {}

			String ep = "https://api.moe.go.th/service";

			String oaiSend = sPjDir+"/RSM/"+xml+"-oaiSend.yaml";
			File fOaiSend = new File(PopiangDigital.workDir+"/res/"+oaiSend);
			rsm.createOaiYaml(fOaiSend, rsm0, ep, "Send", "Acknowledge",true,0);

			String oaiQuery = sPjDir+"/RSM/"+xml+"-oaiQuery.yaml";
			File fOaiQuery = new File(PopiangDigital.workDir+"/res/"+oaiQuery);
			rsm.createOaiYaml(fOaiQuery, rsm0, ep, "Query", "Request",false,0);

			String oaiKey = sPjDir+"/RSM/"+xml+"-oaiKey.yaml";
			File fOaiKey = new File(PopiangDigital.workDir+"/res/"+oaiKey);
			rsm.createOaiYaml(fOaiKey, rsm0, ep, "Send", "Acknowledge",true,1);

			String oaiAuth = sPjDir+"/RSM/"+xml+"-oaiAuth.yaml";
			File fOaiAuth = new File(PopiangDigital.workDir+"/res/"+oaiAuth);
			rsm.createOaiYaml(fOaiAuth, rsm0, ep, "Send", "Acknowledge",true,2);

			String sendPng = sPjDir+"/RSM/"+xml+"_Send.png";
			File fSendPng = new File(PopiangDigital.workDir+"/res/"+sendPng);
//			if(!fSendPng.getParentFile().exists()) fSendPng.getParentFile().mkdirs();

			String reqPng = sPjDir+"/RSM/"+xml+"_Request.png";
			File fReqPng = new File(PopiangDigital.workDir+"/res/"+reqPng);

			String webJsLD = sPjDir+"/RSM/"+xml+"WebLD.html";
			File fWebJsLD = new File(PopiangDigital.workDir+"/res/"+webJsLD);
			rsm.createWebLDFile(fWebJsLD, rsm0, uriRAM);

			String webRDFa = sPjDir+"/RSM/"+xml+"WebRDFa.html";
			File fWebRDFa = new File(PopiangDigital.workDir+"/res/"+webRDFa);
			rsm.createWebRDFa(fWebRDFa, rsm0, uriRAM);

			String webMicDT = sPjDir+"/RSM/"+xml+"WebMicDT.html";
			File fWebMicDT = new File(PopiangDigital.workDir+"/res/"+webMicDT);
			rsm.createWebMicroData(fWebMicDT, rsm0, uriRAM);

			String docDSIG = sPjDir+"/RSM/"+xml+"DSIG.xml";
			File fDocDSIG = new File(PopiangDigital.workDir+"/res/"+docDSIG);
			rsm.createDocDSIG(fDocDSIG, fRsmXml);

			String docENC = sPjDir+"/RSM/"+xml+"ENC.xml";
			File fDocENC = new File(PopiangDigital.workDir+"/res/"+docENC);
			rsm.createDocENC(fDocENC, fRsmXml);

			CreateImageUMM umm = new CreateImageUMM(null, ccl);

			umm.drawFig5(fSendPng, xml+"_SendData"
				, xml+"_DataSender",xml+"_DataReceiver"
				, xml+"_SenderSystem",xml+"_Receiver"
				, xml,"ReceiveAcknowledge");

			umm.drawFig5(fReqPng, xml+"_RequestData"
				, xml+"_DataRequester",xml+"_DataProvider"
				, xml+"_RequesterSystem",xml+"_Provider"
				, xml+"Request", xml);

			msg.append("  [ vp:DEN '"+ccl.den2+"' ;\n"
					 + "    vp:name '"+ccl.biz+"' ;\n"
					 + "    vp:pdfOrg 'file:"+pdfOrg+"' ;\n"
					 + "    vp:pdfMod 'file:"+pdfMod+"' ;\n"
					 + "    vp:pdfCln 'file:"+pdfCln+"' ;\n"
					 + "    vp:rsmPic 'file:"+rsmPng+"' ;\n"
					 + "    vp:sendPic 'file:"+sendPng+"' ;\n"
					 + "    vp:reqPic 'file:"+reqPng+"' ;\n"
					 + "    vp:elemCnt "+aDoc.size()+" ;\n"
					 + "    vp:rsmXml 'file:"+rsmXml+"' ;\n"
					 + "    vp:rsmJson 'file:"+rsmJson+"' ;\n"
					 + "    vp:rsmJsLD 'file:"+rsmJsLD+"' ;\n"
					 + "    vp:rsmCsv 'file:"+rsmCsv+"' ;\n"
					 + "    vp:rsmXsd 'file:"+rsmXsd+"' ;\n"
					 + "    vp:wsdlSend 'file:"+wsdlSend+"' ;\n"
					 + "    vp:wsdlQuery 'file:"+wsdlQuery+"' ;\n"
					 + "    vp:oaiSend 'file:"+oaiSend+"' ;\n"
					 + "    vp:oaiQuery 'file:"+oaiQuery+"' ;\n"
					 + "    vp:oaiKey 'file:"+oaiKey+"' ;\n"
					 + "    vp:oaiAuth 'file:"+oaiAuth+"' ;\n"
					 + "    vp:webJsLD 'file:"+webJsLD+"' ;\n"
					 + "    vp:webRDFa 'file:"+webRDFa+"' ;\n"
					 + "    vp:webMicDT 'file:"+webMicDT+"' ;\n"
					 + "    vp:docDSIG 'file:"+docDSIG+"' ;\n"
					 + "    vp:docENC 'file:"+docENC+"'\n"
					 + "      ]\n");
		}
		String msgs = msg.toString();
		if(msgs.length()>0) {
			rs.append("#\n");
			rs.append("#======= DOCUMENT RSM ========\n");
			rs.append("vp:documents (\n"+msgs+");\n");
		}

		String ers = er.toString();
		if(ers.length()>0) {
			rs.append("#\n");
			rs.append("#======= ERROR =======\n");
			rs.append("vp:error (\n"+ers+");\n");
		}

		String cms = cm.toString();
		if(cms.length()>0) {
			rs.append("#\n");
			rs.append("#======= MAPPING ERROR =======\n");
			rs.append("vp:mapError(\n"+cms+");\n");
		}

		String cls = cl.toString();
		if(cls.length()>0) {
			rs.append("#\n");
			rs.append("#======= CODE LIST =======\n");
			rs.append("vp:qdtList(\n"+cls+");\n");
		}

		rs.append("vp:desc '''\n''' .\n");
		insert(rs.toString());
	}
}

