package popdig;

import java.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import java.util.TreeMap;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.query.*;
import java.io.*;
import java.util.regex.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
 

public class QueryExecute {
	static Logger log = Logger.getLogger(QueryExecute.class);

	JFileChooser jfc = new JFileChooser(PopiangDigital.fWork);

	String qry, cmd0="";
	public List<Map<String,String>> aRes0;
	public Map<String,String> map0;
	List<Map<String,String>> aMap;

	public static Map<String,QueryExecute> mExec = new HashMap<>();
	public static Map<String,Map<String,String>> mQry;
	public QueryExecute qe = this;
	public String state = "";
	public List<Map<String,String>> aMain = new ArrayList<>();
	public String[] aCall;
	public QueryExecute[][] aaQE;

	Map<String,String> checkParam(String[] para0, int i1, int i2) {
		try {
			String[] para1 = new String[para0.length];
	
			String[] wds = qe.cmd0.split(" ");
			for(String op : wds) {
				String[] ops = op.split(":");
				if(ops.length!=2) continue;
				for(int i=0; i<para0.length; i++) 
					if(ops[0].equals(para0[i])) para1[i] = ops[1];
			}
			int pc = 0;
			for(String pa : para1) if(pa!=null) pc++;
			if(pc<para0.length) {
				state += "ERROR: Please provide para ";
				for(String pa : para0) state += " "+pa+":?";
				state += ".\n";
			}
		
			if(qe.aRes0!=null) {
				if(qe.aRes0.size()==0) {
					state += "ERROR: no data found \n";
				} else {
					Map<String,String> map = qe.aRes0.get(0);
					for(int i=i1; i<=i2; i++) {
						if(map.get(para1[i])==null) {
							state += "ERROR: please provide 'thai':? parameter\n";
						}
					}
				}
			}
			if(state.length()==0) {
				Map<String,String> mPara = new HashMap<>();
				for(int i=0; i<para0.length; i++) {
					mPara.put(para0[i], para1[i]);
				}
				return mPara;
			}
		} catch(Exception z) {
			z.printStackTrace();
		}
		return null;
	}

	static {
		mExec.put("task1", new QueryExecute() { public void exec() { task1(); } });
		mExec.put("talk1", new QueryExecute() { public void exec() { talk1(); } });
		mExec.put("word1", new QueryExecute() { public void exec() { word1(); } });
		mExec.put("word2", new QueryExecute() { public void exec() { word1(); } });
		mExec.put("excel1", new QueryExecute() { public void exec() { excel1(); } });
	}

	public Map<String,String> init(String s) {
		qry = s;
		try {
			PopiangUtil.readAllRdfModel();
			ByteArrayInputStream bais = new ByteArrayInputStream(qry.getBytes("UTF-8"));
			Model mo = ModelFactory.createDefaultModel();
			mo.read(bais, null, "TTL");

			String q0 = " ?iri a vo:Query . "
			+ " OPTIONAL { ?iri vp:type  ?typ . } "
			+ " OPTIONAL { ?iri vp:exec  ?exc . } "
			+ " OPTIONAL { ?iri vp:opts  ?opt . } "
			+ " OPTIONAL { ?iri vp:show  ?shw . } "
			+ " OPTIONAL { ?iri vp:call  ?cal . } "
			+ " ?iri vp:query ?qry . "
                    ;    
			List<Map<String,String>> maps = PopiangUtil.spQry(mo, q0, ""); 
			aMain = new ArrayList<>();
			mQry = new HashMap<>();
			for(int i=0; i<maps.size(); i++) {
				Map<String,String> map = maps.get(i);
				String iri = map.get("?iri");
				String typ = map.get("?typ");
				String qry = map.get("?qry");
				String cal = map.get("?cal");
				if(qry==null || qry.length()==0) continue;
				mQry.put(iri, map);
				if(typ==null || typ.length()==0 || typ.equals("main")) {
					aMain.add(map);
				} else if(typ.startsWith("sub")) {
					map.put("?sub", typ.substring(3).trim());
				}
			}

		} catch(Exception z) {
			z.printStackTrace();
		}
		if(aMain!=null && aMain.size()>0) {
			return aMain.get(0);
		}
		return null;
	}

	public void query(Map<String,String> map) {
		try {
			String qry = map.get("?qry");
			String sub = map.get("?sub");
			String opts = "";
			if(map.get("?opt")!=null) opts = map.get("?opt");
			map0 = map;
			aRes0 = PopiangUtil.spQry(PopiangUtil.allModel, qry, opts);
			String scal = map.get("?cal");
			QueryExecute executer = null;
			if(scal!=null) {
				aCall = scal.split(" ");
				aaQE = new QueryExecute[aRes0.size()][aCall.length];
				for(int h=0; h<aCall.length; h++) {
					String cal = aCall[h];
//System.out.println("cal: "+ cal);
					Map<String,String> qmap = null;
					if((qmap=mQry.get(cal))!=null) {
System.out.println("qmap: "+ qmap);
						for(int i=0; i<aRes0.size(); i++) {
							Map<String,String> rec = aRes0.get(i);
							Map<String,String> qmap1 = new HashMap<>();
							String q0 = qmap.get("?qry");
							String sb = qmap.get("?sub");
							if(sb==null) continue;
							String[] sbs = sb.split(" ");
							q0 = q0.trim();
							if(sbs.length>0) {
								for(int j=0; j<sbs.length; j++) {
									String[] vars = sbs[j].split(":");
									if(vars.length!=2) continue;
									String sst = rec.get(vars[0]);
									q0 = q0.replace(vars[1], sst);
								}
							}
							for(String k : qmap.keySet()) {
								if(k.equals("?qry")) qmap1.put(k, q0);
								else qmap1.put(k, qmap.get(k));
							}
							String q1 = qmap1.get("?qry");
							aaQE[i][h] = new QueryExecute();
							aaQE[i][h].query(qmap1);
						}
					}
				}
			}
			if(sub!=null) {
System.out.println("qry: "+ qry);
				return;
			}
			String exc = "";
			boolean bDone = false;
			if((exc=map.get("?exc"))!=null) {
				String[] excs = exc.split(" ");
				if(excs.length>=1) {
					if((executer=mExec.get(excs[0]))!=null) {
						qe = executer;
//log.info("EXEC: "+ exc);
						qe.map0 = map;
						qe.cmd0 = exc;
						qe.aRes0 = aRes0;
						qe.exec();
						bDone = true;
					}
				}
			}
			if(!bDone) exec();
		} catch(Exception z) {
			z.printStackTrace();
		}
	}

	public void exec() {
		exec0();
	}
	public void exec0() {
		Map<String,String> map = qe.map0;
		String cmd = qe.cmd0;
		List<Map<String,String>> aRes = qe.aRes0;
		String shw = map.get("?shw");
		Map<String, String> treeMap = null;
		StringBuffer buf = new StringBuffer();
		int cnt = 0;
		List<String> aKey = new ArrayList<>();
		if(aRes!=null) {
		for(Map<String,String> res : aRes) {
			if(treeMap==null) {
				treeMap = new TreeMap<>(res);
				Map<String,String> mKey = new HashMap<>();
				for(String k : treeMap.keySet()) mKey.put(k,k);
				if(shw!=null) {
					String[] wds = shw.split(" ");
					for(String k : wds) if(mKey.get(k)!=null) aKey.add(k);
				}
				if(aKey.size()==0) {
					for(String k : treeMap.keySet()) aKey.add(k);
				}
			}
			cnt++;
//			buf.append(cnt+": ");
			buf.append("("+cnt+" ");
			int i=0;
			int max = 100;
			for(String key : aKey) {
//				if(i++>0) buf.append(", ");
				if(i++>0) buf.append(" ");
				String dt = res.get(key);
				if(dt==null) {
					dt = "";
				} else {
					int len = dt.length();
					int i1,i2;
					if((i1=dt.indexOf(":"))>0 && i1<=6) {
						String ln = dt.substring(i1+1);
						if(Pattern.matches("[0-9]+", ln)) {
						} else {
							dt = dt.replace("\n","__");
							dt = dt.replace("\r\n","__");
							if(dt.length()>max) dt = dt.substring(0,max-2)+"..("+len+")";
							if(dt.indexOf("'")>=0) {
								if(dt.indexOf("\"")>=0) {
									dt = "'''"+dt+"'''";
								} else {
									dt = "\""+dt+"\"";
								}
							} else {
								dt = "'"+dt+"'";
							}
						}
					} else {
						dt = dt.replace("\n","__");
						dt = dt.replace("\r\n","__");
						if(dt.length()>max) dt = dt.substring(0,max-2)+"..("+len+")";
						if(dt.indexOf("'")>=0) {
							if(dt.indexOf("\"")>=0) {
								dt = "'''"+dt+"'''";
							} else {
								dt = "\""+dt+"\"";
							}
						} else {
							dt = "'"+dt+"'";
						}
					}
				}
				buf.append(dt);
			}
			buf.append(")\n");
		}
		}
		state += buf.toString();
	}

	double minx=-1,miny=-1,maxx=-1,maxy=-1,xx,yy, dxx, dyy;
	Map<String,List<double[]>> hPlc = new HashMap<>();
	List<String> aId = new ArrayList<>();
	List<String> aName = new ArrayList<>();

	public int size() {
		if(aRes0==null) return 0;
		return aRes0.size();
	}

	public String get() {
		if(size()==0) return null;
		for(Map.Entry<String,String> e : aRes0.get(0).entrySet()) {
			return e.getValue();
		}
		return null;
	}

	public String get(int i) {
		int s = size();
		if(i>=s) return null;
		for(Map.Entry<String,String> e : aRes0.get(i).entrySet()) {
			return e.getValue();
		}
		return null;
	}

	public String get(String k) {
		if(size()==0) return null;
		return aRes0.get(0).get(k);
	}

	public String get(int i, String k) {
		int s = size();
		if(i>=s) return null;
		return aRes0.get(i).get(k);
	}

	public String[] gets(String k) {
		int s = size();
		String[] r = new String[s];
		for(int i=0; i<s; i++) {
			r[i] = aRes0.get(i).get(k);
		}
		return r;
	}

	public void task1() {
		state = "";
		String[] para0 = {"id","name","utm"};
		Map<String,String> mPara = checkParam(para0, 0,2);
		qe.minx=qe.miny=qe.maxx=qe.maxy=-1;
		qe.hPlc = new HashMap<>();
		qe.aId = new ArrayList<>();
		qe.aName = new ArrayList<>();
//		qe.hPlc.clear();
//		qe.aId.clear();
//		qe.aName.clear();
		for(Map<String,String> map : qe.aRes0) {
			String id = map.get(mPara.get("id"));
			String name = map.get(mPara.get("name"));
			String utm = map.get(mPara.get("utm"));
			String[] wds = utm.split(",");
			qe.xx = qe.yy = -1;
			try { qe.xx = Double.parseDouble(wds[0].replace(" ",""));}
			catch(Exception z) {z.printStackTrace();}
			try { qe.yy = Double.parseDouble(wds[1].replace(" ",""));}
			catch(Exception z) {z.printStackTrace();}
			if(qe.xx<0 || qe.yy<0) continue;
			if(qe.minx<0) qe.minx = qe.xx;
			if(qe.miny<0) qe.miny = qe.yy;
			if(qe.maxx<0) qe.maxx = qe.xx;
			if(qe.maxy<0) qe.maxy = qe.yy;
			if(qe.xx<qe.minx) qe.minx = qe.xx;
			if(qe.xx>qe.maxx) qe.maxx = qe.xx;
			if(qe.yy<qe.miny) qe.miny = qe.yy;
			if(qe.yy>qe.maxy) qe.maxy = qe.yy;
			List<double[]> aCO = qe.hPlc.get(id);
			if(aCO==null) {
				aCO = new ArrayList<>();
				qe.hPlc.put(id, aCO);
				qe.aId.add(id);
				qe.aName.add(name);
			}
			double[] ccs = {xx,yy};
			aCO.add(ccs);
			state += id+" "+name+" "+utm;
			state += "\n";
		}
		qe.dxx = qe.maxx - qe.minx;
		qe.dyy = qe.maxy - qe.miny;
		state += "x: "+ qe.minx + " - "+ qe.maxx + "\n";
		state += "y: "+ qe.miny + " - "+ qe.maxy + "\n";
		final Enumeration<String> strEnum = Collections.enumeration(qe.hPlc.keySet());
		while(strEnum.hasMoreElements()) {
			String key = strEnum.nextElement();
			state += "name:"+ key + "\n";
			List<double[]> aCO = qe.hPlc.get(key);
			for(double[] dd : aCO) {
				state += "   "+dd[0]+","+dd[1]+"\n";
			}
		}
	}

	public void word1() {
		state = "";
		String[] para0 = {"temp","thai","thai0","eng","eng0"};
		Map<String,String> mPara = checkParam(para0, 1,4);
		File tmp = new File(PopiangDigital.workDir+"/res/temp/"+mPara.get("temp")+".docx");
		if(mPara==null || !tmp.exists()) {
			if(!tmp.exists()) state += "ERROR: template "+tmp+" does not exist\n";
			if(mPara==null) state += "ERROR: wrong parameter\n";
			exec0();
			return;
		}
		try {
			XWPFDocument wordDoc = new XWPFDocument(new FileInputStream(tmp));
			XWPFParagraph pp;
			XWPFRun rr;

			pp = wordDoc.createParagraph();
			pp.setStyle("Heading1");
			rr = pp.createRun();
			rr.setText("List of Organization to Interview");
			rr.addBreak();

			int cnt = 0;
			for(Map<String,String> map : qe.aRes0) {
				String thai = map.get(mPara.get("thai"));
				String tha0 = map.get(mPara.get("thai0"));
				String eng1 = map.get(mPara.get("eng"));
				String eng0 = map.get(mPara.get("eng0"));
				String tab = "    ";
				cnt++;

				pp = wordDoc.createParagraph();
				pp.setStyle("Normal");
				rr = pp.createRun();
				rr.setText(cnt+". Organization Name: "+ eng1+ " ("+eng0+")");
				rr.addBreak();

				rr = pp.createRun();
				rr.setText(tab+"Thai name: "+thai+ " ("+tha0+")");
				rr.addBreak();
				rr.setText(tab+"Interviewee Name (eng): ");
				rr.addBreak();
				rr.setText(tab+"Interviewee Name (thai): ");
				rr.addBreak();
				rr.setText(tab+"Interview Date: ");
				rr.addBreak();
				rr.setText(tab+"Role : ");
				rr.addBreak();
			}

			jfc.setDialogTitle("Choose a directory to save your file: ");
			int returnValue = jfc.showSaveDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String file = jfc.getSelectedFile().getAbsolutePath();
				if(!file.endsWith(".docx")) file += ".docx";
   	        	try (FileOutputStream fos = new FileOutputStream(file)) {
					wordDoc.write(fos);
				}
			}
		} catch(Exception z) {
			z.printStackTrace();
		}
		exec0();
	}

	public void excel1() {
		state = "";
		String[] para0 = {"temp","thai","thai0","eng","eng0"};
		Map<String,String> mPara = checkParam(para0, 1,4);
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Interview List");

			int rw = 1;
			int cnt = 0;
			for(Map<String,String> map : qe.aRes0) {
				String thai = map.get(mPara.get("thai"));
				String tha0 = map.get(mPara.get("thai0"));
				String eng1 = map.get(mPara.get("eng"));
				String eng0 = map.get(mPara.get("eng0"));
				String tab = "    ";
				cnt++;
				Row row = sheet.createRow(rw+cnt);
				Cell cell;
				cell = row.createCell(1);
				cell.setCellValue(eng1);
				cell = row.createCell(2);
				cell.setCellValue(eng0);
				cell = row.createCell(3);
				cell.setCellValue(thai);
				cell = row.createCell(4);
				cell.setCellValue(tha0);
			}

			jfc.setDialogTitle("Choose a directory to save your file: xlsx");
			int returnValue = jfc.showSaveDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String file = jfc.getSelectedFile().getAbsolutePath();
				if(!file.endsWith(".xlsx")) file += ".xlsx";
   	        	try (FileOutputStream fos = new FileOutputStream(file)) {
					workbook.write(fos);
				}
			}
		} catch(Exception z) {
			z.printStackTrace();
		}
		exec0();
	}

	public void talk1() {
		state = "";
		String[] para0 = {"temp","who","say"};
		Map<String,String> mPara = checkParam(para0, 1,2);
		File tmp = new File(PopiangDigital.workDir+"/res/temp/"+mPara.get("temp")+".docx");
		if(mPara==null || !tmp.exists()) {
			if(!tmp.exists()) state += "ERROR: template "+tmp+" does not exist\n";
			if(mPara==null) state += "ERROR: wrong parameter\n";
			exec0();
			return;
		}
		try {
			XWPFDocument wordDoc = new XWPFDocument(new FileInputStream(tmp));
			XWPFParagraph pp;
			XWPFRun rr;

			pp = wordDoc.createParagraph();
			pp.setStyle("Heading1");
			rr = pp.createRun();
			rr.setText("Meeting Transcript");
			rr.addBreak();

			int cnt = 0;
			for(Map<String,String> map : qe.aRes0) {
				String who = map.get(mPara.get("who"));
				String say = map.get(mPara.get("say"));
				String tab = "    ";
				cnt++;

				pp = wordDoc.createParagraph();
				pp.setStyle("Normal");
				rr = pp.createRun();
				rr.setText("Who: "+who);
				rr.addBreak();
String[] says = say.split("\n\n");
//System.out.print("SAY: "+ says.length+" ");
for(String s : says) {
	String[] ss = s.split("\n");
	s = s.replace("\n","");
				rr.setText(s);
				rr.addBreak();
//	System.out.print(" "+s.length()+":"+ss.length);
}
//System.out.println();
			}

			jfc.setDialogTitle("Choose a directory to save your file: ");
			int returnValue = jfc.showSaveDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String file = jfc.getSelectedFile().getAbsolutePath();
				if(!file.endsWith(".docx")) file += ".docx";
   	        	try (FileOutputStream fos = new FileOutputStream(file)) {
					wordDoc.write(fos);
				}
			}
		} catch(Exception z) {
			z.printStackTrace();
		}
		exec0();
	}

}

