package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.awt.Point;
import java.util.*;
import java.util.regex.*;
import java.awt.datatransfer.*;
import org.fife.ui.rtextarea.*;
//import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.*;

public class DropFilesInRdf extends RTATextTransferHandler {
	public TextFileInfo tfinf;
	TransferHandler oldhandler;
	public DropFilesInRdf(TextFileInfo tf) {
		tfinf = tf;
		oldhandler = tf.text.getTransferHandler();
	}
	public String getNewRdf(String rid, String file, String nm, String vo) {
		List<String> aLN = PopiangUtil.getVO(vo);
		StringBuffer bb = new StringBuffer(rid + " a "+vo+" ;\t");
		for(String l : aLN) { bb.append(l+"\n"); }    
		String ddtt = PopiangUtil.timestamp();
		String r = bb.toString().replace("___FILE___",file)
			.replace("___DATETIME___",ddtt).replace("___NAME___",nm)
			;
		PopiangDigital.setObject(vo, rid);
		return r;
	}
	public String getNewRdfXlsx(String rid, String file, String nm, String vo) {
		List<String> aLN = PopiangUtil.getVO(vo);
		StringBuffer bb = new StringBuffer(rid + " a "+vo+" ;\t");
		for(String l : aLN) { bb.append(l+"\n"); }    
		String ddtt = PopiangUtil.timestamp();
		StringBuffer sht = new StringBuffer("(");
		StringBuffer sum = new StringBuffer("(");
		StringBuffer cls = new StringBuffer();
		int sumc = 0;
		for(int i=0; i<aSheet.size(); i++) {
			sht.append("\n  ");
			SheetInfo si = aSheet.get(i);
			sht.append("("+(i+1));
			sht.append(" '"+si.name+"'");
			sht.append(" "+si.lastRow);
			if(si.rdfId>0) {
				sht.append(" "+pref+":"+si.rdfId);
				sht.append(" '"+si.den+"'");

				String[] wds = si.rdfA.split("\n");
				for(int j=0; j<wds.length; j++) {
					wds[j] = wds[j].replace(";","").replace(".","");
					wds[j] = wds[j].trim();
				}
				if(wds.length>=2) {
					cls.append(pref+":"+si.rdfId+"  "+wds[0]+" ; ");
					cls.append("vp:name '"+si.name+"' ;\n");
					for(int j=1; j<wds.length; j++) {
						cls.append(wds[j]+" ;\n");
					}
					cls.append("vp:file '"+file+"' ;\n");
					cls.append("vp:fileCSV 'file:"+si.fileCsv+"' ;\n");
					cls.append("vp:fileJSON 'file:"+si.fileJson+"' ;\n");
					cls.append("vp:fileXML 'file:"+si.fileXml+"' ;\n");
					cls.append("vp:desc '''\n''' .\n");
				}
				if(sumc>0 && sumc%5==0) sum.append("\n  ");
				else if(sumc>0) sum.append(" ");
				sum.append(pref+":"+si.rdfId);
				sumc++;
			}
			sht.append(")");
		}
		sht.append(")");
		sum.append(")");
		bb.append(cls);
		String r = bb.toString().replace("___FILE___",file)
			.replace("___DATETIME___",ddtt).replace("___NAME___",nm)
			.replace("'___SHEETS___'", sht.toString())
			.replace("'___CODELIST___'", sum.toString());
		PopiangDigital.setObject(vo, rid);
		return r;
	}
	public String getNewRdf(String rid, String file, String nm) {
		List<String> aLN = PopiangUtil.getVO("vo:File");
		StringBuffer bb = new StringBuffer(rid + " a vo:File ;\t");
		for(String l : aLN) { bb.append(l+"\n"); }    
		String ddtt = PopiangUtil.timestamp();
		String r = bb.toString().replace("___FILE___",file)
			.replace("___DATETIME___",ddtt).replace("___NAME___",nm);
		PopiangDigital.setObject("vo:File", rid);
		return r;
	}
	public void insert(String r) {
		int c = tfinf.text.getCaretPosition();
		tfinf.text.insert(r, tfinf.text.getCaretPosition());
		tfinf.text.setCaretPosition(c+r.length());
		tfinf.text.requestFocus();
	}

	List<SheetInfo> aSheet;

	class SheetInfo {
		String name, rdfA;
		String vo,den;
		String fileCsv,fileJson,fileXml;
		int lastRow,firstRow,firstCol=-1;
		int rdfId;
		List<String> aRdfP = new ArrayList<>();
		List<Integer> aRdfR = new ArrayList<>();
		List<Integer> aRdfC = new ArrayList<>();
	}

	void readExcel(File fp) {
		aSheet = new ArrayList<>();
		try {
			Pattern p = Pattern.compile("([A-Z]+)([0-9])+");
			InputStream myxls = new FileInputStream(fp);
			XSSFWorkbook wb = new XSSFWorkbook(myxls);
			int no = wb.getNumberOfSheets();
			for(int c=0; c<no; c++) {
				XSSFSheet sheet = wb.getSheetAt(c);
				SheetInfo si = new SheetInfo();
				si.name = sheet.getSheetName();
				si.lastRow = sheet.getLastRowNum();
				Map<CellAddress,XSSFComment> hCmt = sheet.getCellComments();
				for (Map.Entry<CellAddress, XSSFComment> e : hCmt.entrySet()) {
					String rw = e.getKey().toString();
					Matcher m = p.matcher(rw);
					String s = e.getValue().getString().getString();
					if(m.find()) {
						String g1 = m.group(1);
						String g2 = m.group(2);
						if(g1.length()!=1) continue;
						int b = g1.charAt(0) - 'A';
						int a = -1;
						try { a=Integer.parseInt(g2)-1; } catch(Exception x) {}
						if(a<0) continue;
						int i1 = s.indexOf("vo:Code"),i2;
						if(s.startsWith("a ") && i1>0) {
							si.rdfA = s;
							si.rdfA = s.replace(".",";");
							si.vo = "vo:Code";
							i1 = s.indexOf("'");
							if(i1>0 && (i2=s.indexOf("'",i1+1))>0) {
								si.den = s.substring(i1+1,i2);
							}
						} else if(s.startsWith("vp:")) {
							if(s.equals("vp:code")) si.firstRow = a;
							if(s.equals("vp:code")) si.firstCol = b;
							si.aRdfP.add(s);
							si.aRdfR.add(a);
							si.aRdfC.add(b);
						}
					}
				}
				if(si.rdfA!=null && si.aRdfP.size()>0 && si.firstCol>=0) {
//System.out.println("  "+si.name+" fr:"+si.firstRow+" fc:"+si.firstCol);
					int rid = rdfID + 1;
					int cnt = 0;
					si.fileCsv = dir+"/"+pref+"/"+pref+"-"+rid+".csv";
					si.fileJson = dir+"/"+pref+"/"+pref+"-"+rid+".json";
					si.fileXml = dir+"/"+pref+"/"+pref+"-"+rid+".xml";
					File fCsv = new File(PopiangDigital.workDir+"/res/"+si.fileCsv);
					if(!fCsv.getParentFile().exists()) fCsv.getParentFile().mkdirs();
					File fJson = new File(PopiangDigital.workDir+"/res/"+si.fileJson);
					File fXml = new File(PopiangDigital.workDir+"/res/"+si.fileXml);
					try {
						String name = "XXX";
						name = PopiangUtil.xmlNDR(si.den);
						BufferedWriter bwCsv = new BufferedWriter(new FileWriter(fCsv));
						BufferedWriter bwJson = new BufferedWriter(new FileWriter(fJson));
						BufferedWriter bwXml = new BufferedWriter(new FileWriter(fXml));
						bwJson.write("[");
						bwXml.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
						bwXml.write("<xsd:schema\n");
						bwXml.write("  xmlns:ccts=\"urn:un:unece:uncefact:documentation:"
							+"standard:CoreComponentsTechnicalSpecification:2\"\n");
						bwXml.write("  targetNamespace=\"___IRIBASE___"+":1.0\"\n");
						bwXml.write("  xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n");
						bwXml.write("  >\n");

						bwXml.write("  <xsd:simpleType name=\""+name+"\">\n");
						bwXml.write("    <xsd:restriction base=\"xsd:token\">\n");

						int jcnt=0;
						for(int s=si.firstRow; s<=si.lastRow; s++) {
							XSSFRow row = sheet.getRow(s);
							XSSFCell cel = null;
							String scel = null;
							try { cel=row.getCell(si.firstCol);} catch(Exception z) {}
							try { scel=cel.getStringCellValue();} catch(Exception z) {}
							if(scel==null || scel.length()==0)
								try { int n=(int)cel.getNumericCellValue();
									scel=n>0?""+n:""; } catch(Exception z) {}
							if(scel==null || scel.length()==0) break;
							if(jcnt>0) bwJson.write(",\n");
							else bwJson.write("\n");
							jcnt++;
							bwJson.write("{");
							String prop=null, value=null;
							for(int cc=0; cc<si.aRdfC.size(); cc++) {
								cel = null; scel=null;
								try { cel=row.getCell(si.aRdfC.get(cc));} catch(Exception z) {}
								if(cel==null) break;
								try { scel=cel.getStringCellValue();} catch(Exception z) {}
								if(scel==null || scel.length()==0)
									try { int n=(int)cel.getNumericCellValue();
										scel=n>0?""+n:""; } catch(Exception z) {}
								if(scel==null || scel.length()==0)
									try { Object o=cel.getDateCellValue();
										scel = o==null? null : ""+o; } catch(Exception z) {}
								if(scel==null) continue;
								cnt++;
								String prp = si.aRdfP.get(cc);
								if(cc>0) bwCsv.write(",");
								bwCsv.write("\""+scel+"\"");
								if(scel!=null) {
									if(cc>0) bwJson.write(",\n");
									else bwJson.write("\n");
									bwJson.write("\""+prp+"\" : \""+scel+"\"");
									if(prp.equals("vp:code")) value = scel;
									if(prp.equals("vp:name")) prop = scel;
								}
							}
							bwCsv.newLine();
							bwJson.write("\n}");
							if(prop!=null && value!=null) {
								bwXml.write("      <xsd:enumeration value=\""+value+"\">\n");
								bwXml.write("        <xsd:annotation>\n");
								bwXml.write("          <xsd:documentation xml:lang=\"th\">\n");
								bwXml.write("            <ccts:Name>"+prop+"</ccts:Name>\n");
								bwXml.write("          </xsd:documentation>\n");
								bwXml.write("        </xsd:annotation>\n");
								bwXml.write("      </xsd:enumeration>\n");
							}
						}
						bwCsv.close();
						bwJson.write("]");
						bwJson.close();

						bwXml.write("    </xsd:restriction>\n");
						bwXml.write("  </xsd:simpleType>\n");
						bwXml.write("</xsd:schema>");
						bwXml.close();
					} catch(Exception z) {
z.printStackTrace();
					}
					if(cnt>0) {
						si.rdfId = rid;
						rdfID = rid;
					}
				}
				aSheet.add(si);
			}
		} catch(Exception x) {
			x.printStackTrace();
		}
	}

	int rdfID;
	String pref, dir;

	public void action(List<File> l) {
		try {
			String fn = tfinf.path.toFile().getName();
			String com = fn.substring(0,fn.indexOf("."));
			pref = com;
			dir = pref.substring(0,pref.length()-2);
			if(!pref.matches("[a-z]+[0-9A-Z]{2}")) return;
			rdfID = tfinf.text.maxId();
			byte[] buf = new byte[1024*64];
			for (File f : l) {
				int i1;
				String ifn = f.getName();
				rdfID++;
				int rid = rdfID;
				String ext = "";
				if((i1=ifn.lastIndexOf("."))>0) ext = ifn.substring(i1).toLowerCase();
				String fnm = pref+"-"+rid;
				String sp = ""+tfinf.path;
				sp = sp.substring(0,sp.lastIndexOf("/rdf"))
					+"/res/"+dir+"/"+pref+"/"+fnm+ext;
				String rs = "file:"+dir+"/"+pref+"/"+fnm+ext;
				String r = "";
				if(ext.equals(".pdf")) {
					r = getNewRdf(pref+":"+rid, rs, ifn, "vo:Pdf");
				} else if(ext.equals(".xlsx")) {
					readExcel(f);
					r = getNewRdfXlsx(pref+":"+rid, rs, ifn, "vo:MSExcel");
				} else {
					r = getNewRdf(pref+":"+rid, rs, ifn);
				}
				try {
					File fo = new File(sp);
					if(!fo.getParentFile().exists()) fo.getParentFile().mkdirs();
					FileInputStream fis = new FileInputStream(f);
					FileOutputStream fos = new FileOutputStream(fo);
					while((i1=fis.read(buf,0,buf.length))>0) {
						fos.write(buf,0,i1);
					}
					fos.close();
					fis.close();
				} catch(Exception z) {
					z.printStackTrace();
					rdfID--;
				}
				insert(r);
			}
		} catch(Exception z){
			z.printStackTrace();
		}
	}
	public boolean canImport(TransferHandler.TransferSupport support) {
		boolean rt = super.canImport(support);
		if(rt) return rt;
		if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			return false;
		}
		boolean copySupported = (COPY & support.getSourceDropActions()) == COPY;
		if (!copySupported) {
			return false;
		}
		support.setDropAction(MOVE);
		return true;
	}
	public boolean importData(TransferHandler.TransferSupport support) {
		boolean rt = super.importData(support);
		if(rt) return rt;
		if (!canImport(support)) {
			return false;
		}
		try {
			Transferable t = support.getTransferable();
			final java.util.List<File> l =
				(java.util.List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
			SwingUtilities.invokeLater(new Runnable() { public void run() {
				action(l);
			}});
		} catch (UnsupportedFlavorException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}

