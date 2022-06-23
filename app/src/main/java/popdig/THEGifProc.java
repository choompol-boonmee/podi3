package popdig;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.eventfilesystem.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
import java.awt.geom.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.util.Units;

public class THEGifProc {

	String clmpath = "out/codelist/";
	String idmpath = "out/identifierlist/";
	String ummpath = "out/umm/";
	String ssmpath = "out/share/";
	String rsmpath = "out/rsm/";
	String wsdlpath = "out/wsdl/";
	String smppath = "out/src/xml/";
	String jsonpath = "out/src/json/";
	String jvpath = "out/src/java/";
	String modpath = "out/src/";
	String distpath = "out/dist/";
	String bielibpath = "out/bielib/";

	String cctsurn = "urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2";

	String npf = "urn:th:gov:egif:";
	String drf = ":standard";
	String ver = ":1.0";

	String codepath = "codes/";

	Vector<String[]> bie = new Vector<String[]>();
	Vector<String[]> qdt = new Vector<String[]>();
	Vector<String[]> rsm = new Vector<String[]>();
	Vector<String[]> bp = new Vector<String[]>();
	Vector<String[]> bt = new Vector<String[]>();

	Hashtable<String,String[]> abie = new Hashtable<String,String[]>();

	Hashtable<String,Vector<String[]>> bied = new Hashtable<String,Vector<String[]>>();
	Vector<Vector<String[]>> rsmd = new Vector<Vector<String[]>>();
	Vector<String> rsmn = new Vector<String>();
	Hashtable<String,Integer> rsmi = new Hashtable<String,Integer>();

	Hashtable<String,Hashtable<String,String[]>> btd
		= new Hashtable<String,Hashtable<String,String[]>>();

	public String qdtName(String id) {
		//System.out.println("qdt size:"+ qdt.size());
		for(int i=0; i<qdt.size(); i++) {
			String[] words = qdt.get(i);
			if(words.length<4) continue;
			if(id.equals(words[0])) {
				return words[3];
			}
		}
//		System.out.println("id??:"+id);
		return "????";
	}

	public void qdtIDList(String id, String[] wds) {
		try {
			String agentId =wds[4];
			String listId = id;
			String name = wds[3];

			File path = new File(codepath);
			if(!path.exists()) path.mkdirs();
			String codefile = path+"/QT"+id+".txt";
			FileInputStream fis = new FileInputStream(codefile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			Element elem0, elem1, elem2=null, elem3=null, elem4=null, elem5=null;
			Element elem6=null, elem7=null, elem8=null, elem9=null, elem10=null, elem11=null;
			elem0 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:schema");
			document.appendChild(elem0);

			elem0.setAttribute("targetNamespace", npf+"identifier"+ drf +pack(name)+ver);
			elem0.setAttribute("xmlns:ids"+agentId+id+pack(name), npf+"identifier"+ drf +pack(name)+ver);
			elem0.setAttribute("xmlns:ccts"
				, "urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2");
			elem0.setAttribute("elementFormDefault", "qualified");
			elem0.setAttribute("attributeFormDefault", "unqualified");
			elem0.setAttribute("version","1.0");

			elem1 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:simpleType");
			elem1.setAttribute("name", pack(name)+"Type");
			elem0.appendChild(elem1);
			elem2 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:restriction");
			elem2.setAttribute("base", "xsd:token");
			elem1.appendChild(elem2);

			String line;
			while( (line=br.readLine())!=null ) {
				String[] words = line.split("\t");
				if(words.length<2 || words[0].length()==0 || words[1].length()==0) continue;
				elem3 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:enumeration");
				elem2.appendChild(elem3);
				elem3.setAttribute("value", words[0]);
				elem4 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:annotation");
				elem3.appendChild(elem4);
				elem5 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:documentation");
				elem4.appendChild(elem5);
				elem5.setAttribute("xml:lang", "th");
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:Name");
				elem5.appendChild(elem6);
				elem6.appendChild(document.createTextNode(words[1]));
			}
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.STANDALONE, "no");
			xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			File dir = new File(idmpath);
			if(!dir.exists()) dir.mkdirs();
			String filename = dir+"/" + pack(name) +"ID.xml";
			FileOutputStream fos = new FileOutputStream(filename);
			xformer.transform(new DOMSource(document), new StreamResult(fos));
			fos.close();
		} catch(Exception ex) {
			System.out.println("ex5:"+ex);
		}
	}

	public void qdtCodeList(String id, String[] wds) {
		try {
			String agentId =wds[4];
			String listId = id;
			String name = wds[3];

			File path = new File(codepath);
			if(!path.exists()) path.mkdirs();
			String codefile = path+"/QT"+id+".txt";
			FileInputStream fis = new FileInputStream(codefile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			Element elem0, elem1, elem2=null, elem3=null, elem4=null, elem5=null;
			Element elem6=null, elem7=null, elem8=null, elem9=null, elem10=null, elem11=null;
			elem0 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:schema");
			document.appendChild(elem0);
			elem0.setAttribute("targetNamespace", npf+"codelist"+ drf +pack(name)+ver);
			elem0.setAttribute("xmlns:clm"+agentId+id+pack(name)
				, npf+"codelist"+ drf +pack(name)+ver);
			elem0.setAttribute("xmlns:ccts", cctsurn);
			elem0.setAttribute("elementFormDefault", "qualified");
			elem0.setAttribute("attributeFormDefault", "unqualified");
			elem0.setAttribute("version", ver);

			elem1 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:simpleType");
			elem1.setAttribute("name", pack(name)+"Type");
			elem0.appendChild(elem1);
			elem2 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:restriction");
			elem2.setAttribute("base", "xsd:token");
			elem1.appendChild(elem2);

			String line;
			while( (line=br.readLine())!=null ) {
				String[] words = line.split("\t");
				if(words.length<2 || words[0].length()==0 || words[1].length()==0) continue;
				elem3 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:enumeration");
				elem2.appendChild(elem3);
				elem3.setAttribute("value", words[0]);
				elem4 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:annotation");
				elem3.appendChild(elem4);
				elem5 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:documentation");
				elem4.appendChild(elem5);
				elem5.setAttribute("xml:lang", "th");
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:Name");
				elem5.appendChild(elem6);
				elem6.appendChild(document.createTextNode(words[1]));
			}
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.STANDALONE, "no");
			xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			File dir = new File(clmpath);
			if(!dir.exists()) dir.mkdirs();
			String filename = dir+"/" + pack(name) +"Code.xml";
			FileOutputStream fos = new FileOutputStream(filename);
			xformer.transform(new DOMSource(document), new StreamResult(fos));
			fos.close();
		} catch(Exception ex) {
			System.out.println("ex6:"+ex);
		}
	}

	public void proc1(String[] filelist) throws Exception {
		String line;
		Vector<String[]> work;
		String[] words;
//System.out.println("Step 1.1");
		for(int i=0; i<filelist.length; i++) {
//System.out.println("Step 1.2");
			if(!filelist[i].endsWith(".txt")) continue;
//System.out.println("Step 1.4:" + filelist[i]);
			FileInputStream fis = new FileInputStream(filelist[i]);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
//System.out.println("Step 1.6");
			line=br.readLine();
//System.out.println("Step 1.7");
			words = line.split("\t");
//System.out.println("Step 1.8");
			line=br.readLine();
			if(words.length<1) continue;
			work = null;
			if("Reusable Information Entity".equals(words[0])) {
				work = bie;
			} else if("Qualified Data Type".equals(words[0])) {
				work = qdt;
			} else if("Root Schema".equals(words[0])) {
				work = rsm;
			} else if("Business Process".equals(words[0])) {
				work = bp;
			} else if("Business Transaction".equals(words[0])) {
				work = bt;
			}
//System.out.println("Step 1.3");
			if(work==null) continue;
			while( (line=br.readLine())!=null) {
				words = line.split("\t");
				if(words.length<3) continue;
				work.add(words);
			}
		}
	}

	public String[] extract(HSSFSheet sheet, int row) {
		HSSFRow hssfrow = sheet.getRow(row);
		int lastcell = hssfrow.getLastCellNum();
//System.out.println("\tlast cell:" + lastcell);
//		String[] words = new String[lastcell+1];
		String[] words = new String[21];
//System.out.print("\t");
		for(int i=0; i<words.length; i++) {
			String data = "";
			HSSFCell hssfcell = null;
			try {
				hssfcell = hssfrow.getCell((short) (i));
				data = hssfcell.getStringCellValue();
			} catch(Exception ex) {
				try {
					data = ""+ (int) hssfcell.getNumericCellValue();
				} catch(Exception e) { }
			}
			words[i] = data;
//			System.out.print(words[i]+", ");
		}
		return words;
	}

	public void proc2(String[] filelist) throws Exception {
		String line;
		Vector<String[]> work;
		String[] words;
		for(int i=0; i<filelist.length; i++) {
			if(!filelist[i].endsWith(".xls")) continue;
			InputStream myxls = new FileInputStream(filelist[i]);
			HSSFWorkbook wb = new HSSFWorkbook(myxls);
			int no = wb.getNumberOfSheets();
			for(int c=0; c<no; c++) {
				HSSFSheet sheet = wb.getSheetAt(c);
				int lastrow = sheet.getLastRowNum();
				words = extract(sheet, 0);
				work = null;
//System.out.println("sheet:"+ words[0] + ":" +lastrow);
				if("Reusable Information Entity".equals(words[0])) {
					work = bie;
				} else if("Qualified Data Type".equals(words[0])) {
					work = qdt;
				} else if("Root Schema".equals(words[0])) {
					work = rsm;
				} else if("Business Process".equals(words[0])) {
					work = bp;
				} else if("Business Transaction".equals(words[0])) {
					work = bt;
				} else {
 					continue;
				}
				for(int r=2; r<=lastrow ; r++) {
					words = extract(sheet, r);
					words[20] = (c+1)+"---"+(r+1)+"---"+filelist[i];
					if(bie==work) {
						if(checkBIE(c, r, words)==false)
							continue;
					} else if(qdt==work) {
						if(checkQDT(c, r, words)==false)
							continue;
					} else if(rsm==work) {
						if(checkBIE(c, r, words)==false)
							continue;
					} else if(bp==work) {
					} else if(bt==work) {
						if(checkBT(c, r, words)==false)
							continue;
					}
					work.add(words);
				}
			}
		}
//System.out.println("step 2.1");
		for(int r=0; r<bie.size(); r++) {
			String[] wds = bie.get(r);
			if(!"ASBIE".equals(wds[1])) continue;
			String asbie = wds[11]+"___" + wds[12];
			String[] wdas = null;
			if((wdas=abie.get(asbie))==null) {
				String[] ids = wds[20].split("---");
System.out.println("s:"+ ids[0] +" r:"+ ids[1] + ": ? absent of ABIE for "+ asbie);
			} else {
				if(wdas[0].substring(0,1).equals(wds[0].substring(0,1))==false) {
//System.out.println("cross\t:"+ wds[0] + " used " + wdas[0] +"("+asbie+")");
				}
			}
		}
		String btid = "";
//		String[] words;
		for(int i=0; i<bt.size(); i++) {
			words = bt.get(i);
			if("BT".equals(words[1])) {
				btid = words[0];
				btd.put(btid, new Hashtable<String,String[]>());
				btd.get(btid).put("DEF",words);
			} else {
				btd.get(btid).put(words[1],words);
			}
		}
	}

	public boolean checkBT(int c, int r, String[] words) throws Exception {
		if(words[0].length()!=4) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:0 ::: You need ID");
			return false;
		}
		int type = 0;
		if("BT".equals(words[1])) type = 1;
		if("RQA".equals(words[1])) type = 2;
		if("RSA".equals(words[1])) type = 3;
		if("RQM".equals(words[1])) type = 4;
		if("RSM".equals(words[1])) type = 5;
		if("RQD".equals(words[1])) type = 6;
		if("RSD".equals(words[1])) type = 7;
		if(type==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:1 ::: You need type "
				+"'BT','RQA','RSA','RQM','RSM','RQD' or 'RSD'");
			return false;
		}
		if(type==1 && words[2].length()==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:2 ::: You need Business Transaction Thai name");
		}
		if(type==1 && words[3].length()==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:3 ::: You need Business Transaction English name");
		}
		if(type>=2 && type<=7 && words[4].length()==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:4 ::: You need Thai name");
		}
		if(type>=2 && type<=7 && words[5].length()==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:5 ::: You need English name");
		}
		return true;
	}

	public void readXls(File[] filelist) throws Exception {
		String line;
		Vector<String[]> work;
		String[] words;
		for(int i=0; i<filelist.length; i++) {
			if(!filelist[i].getName().endsWith(".xls")) continue;
			InputStream myxls = new FileInputStream(filelist[i]);
			HSSFWorkbook wb = new HSSFWorkbook(myxls);
			int no = wb.getNumberOfSheets();
			for(int c=0; c<no; c++) {
				HSSFSheet sheet = wb.getSheetAt(c);
				int lastrow = sheet.getLastRowNum();
				words = extract(sheet, 0);
				work = null;
//System.out.println("sheet:"+ words[0] + ":" +lastrow);
				if("Reusable Information Entity".equals(words[0])) {
					work = bie;
				} else if("Qualified Data Type".equals(words[0])) {
					work = qdt;
				} else if("Root Schema".equals(words[0])) {
					work = rsm;
				} else if("Business Process".equals(words[0])) {
					work = bp;
				} else if("Business Transaction".equals(words[0])) {
					work = bt;
				} else {
 					continue;
				}
				for(int r=2; r<=lastrow ; r++) {
					words = extract(sheet, r);
					words[20] = (c+1)+"---"+(r+1)+"---"+filelist[i];
					if(bie==work) {
						if(checkBIE(c, r, words)==false)
							continue;
					} else if(qdt==work) {
						if(checkQDT(c, r, words)==false)
							continue;
					} else if(rsm==work) {
						if(checkBIE(c, r, words)==false)
							continue;
					} else if(bp==work) {
					} else if(bt==work) {
						if(checkBT(c, r, words)==false)
							continue;
					}
					work.add(words);
				}
			}
		}
//System.out.println("step 2.1");
		for(int r=0; r<bie.size(); r++) {
			String[] wds = bie.get(r);
			if(!"ASBIE".equals(wds[1])) continue;
			String asbie = wds[11]+"___" + wds[12];
			String[] wdas = null;
			if((wdas=abie.get(asbie))==null) {
				String[] ids = wds[20].split("---");
System.out.println("s:"+ ids[0] +" r:"+ ids[1] + ": ? absent of ABIE for "+ asbie);
			} else {
				if(wdas[0].substring(0,1).equals(wds[0].substring(0,1))==false) {
//System.out.println("cross\t:"+ wds[0] + " used " + wdas[0] +"("+asbie+")");
				}
			}
		}
		String btid = "";
//		String[] words;
		for(int i=0; i<bt.size(); i++) {
			words = bt.get(i);
			if("BT".equals(words[1])) {
				btid = words[0];
				btd.put(btid, new Hashtable<String,String[]>());
				btd.get(btid).put("DEF",words);
			} else {
				btd.get(btid).put(words[1],words);
			}
		}
	}

	public boolean checkQDT(int c, int r, String[] words) throws Exception {
		if(words[0].length()!=4) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:0 ::: You need ID");
			return false;
		}
		int type = 0;
		if("Code list".equals(words[1])) type = 1;
		if("ID list".equals(words[1])) type = 2;
		if("schema".equals(words[1])) type = 3;
		if(type==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:1 ::: You need type 'Code list','ID list' or 'schema'");
			return false;
		}
		if(words[2].length()==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:2 ::: You need Thai name");
		}
		if(words[3].length()==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:3 ::: You need English name");
		}
		if(type==1 && new File(codepath+"QT"+words[0]+".txt").exists()==false) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:10 ::: You need code list for " + words[0]);
		}
		return true;
	}

	public boolean checkBIE(int c, int r, String[] words) throws Exception {
		if(words[0].length()!=4) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:0 ::: You need ID");
			return false;
		}
		int type = 0;
		if("ABIE".equals(words[1])) type = 1;
		if("BBIE".equals(words[1])) type = 2;
		if("ASBIE".equals(words[1])) type = 3;
		if(type==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:1 ::: You need type 'ABIE','ASBIE' or 'BBIE'");
			return false;
		}
		if(type==1 && words[3].length()>0 && words[5].length()>0) {
			String abienm = words[3] + "___" + words[5];
			if(abie.get(abienm)==null) {
				abie.put(abienm, words);
			} else {
				String[] olds = abie.get(abienm)[20].split("---");
				System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:2-3 :"+abienm+": repeated 'ABIE' to " 
					+"s:"+olds[0] +" r:"+olds[1]);
			}
		}
		if(type==1 && words[2].length()==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:2 ::: You need type 'OCQ'");
		}
		if(type==1 && words[3].length()==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:3 ::: You need type 'OCQ(E)'");
		}
		if(type==1 && words[4].length()==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:4 ::: You need type 'OCT'");
		}
		if(type==1 && words[5].length()==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:5 ::: You need type 'OCT(E)'");
		}
		if(type==2 && words[8].length()==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:8 ::: You need type 'PTQ'");
		}
		if(type==2 && words[9].length()==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:9 ::: You need type 'PTQ(E)'");
		}
		if(type==2 && words[10].length()==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:10 ::: You need type 'Type defined'");
		}

		if(type==3 && words[8].length()==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:8 ::: You need type 'PTQ'");
		}
		if(type==3 && words[9].length()==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:9 ::: You need type 'PTQ(E)'");
		}
		if(type==3 && words[11].length()==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:11 ::: You need type 'ASOCQ'");
		}
		if(type==3 && words[12].length()==0) {
			System.out.println("s:"+(c+1) +" r:"+(r+1) +" c:12 ::: You need type 'ASOC'");
		}
		return true;
	}

	public void procBIE() throws Exception {
		String[] words;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();

		Element elem0, elem1, elem2=null, elem3=null, elem4=null, elem5=null;
		Element elem6=null, elem7=null, elem8=null, elem9=null, elem10=null, elem11=null;

		elem0 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:schema");
		document.appendChild(elem0);
		elem0.setAttribute("targetNamespace"
			, "urn:th:gov:egif:data:standard:ReusableAggregateBusinessInformationEntity:1");
		elem0.setAttribute("xmlns:ram"
			, "urn:th:gov:egif:data:standard:ReusableAggregateBusinessInformationEntity:1");
		elem0.setAttribute("xmlns:ccts"
			, "urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2");
		elem0.setAttribute("xmlns:udt", "urn:th:gov:egif:data:standard:UnqualifiedDataType:1");
		elem0.setAttribute("xmlns:qdt", "urn:th:gov:egif:data:standard:QualifiedDataType:1");
		elem0.setAttribute("elementFormDefault", "qualified");
		elem0.setAttribute("attributeFormDefault", "unqualified");
		elem0.setAttribute("version","1.0");

		String oct = "", ocq = "", name = "";
		String thaioct = "", thaiocq = "";
		for(int i=0; i<bie.size(); i++) {
			words = bie.get(i);
			if(words[1].equals("ABIE")) {
				oct = words[5];
				ocq = words[3];
				name = buildTagName(words[3], words[5], "","","");
				bied.put(name, new Vector<String[]>());
				elem1 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:complexType");
				elem1.setAttribute("name", name+"Type");
				elem2 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:sequence");
				elem0.appendChild(elem1);
				elem3 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:annotation");
				elem4 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:document");
				elem4.setAttribute("xml:lang", "en");
				elem3.appendChild(elem4);
				elem5 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:UniqueID");
				elem5.appendChild(document.createTextNode("THIE00"+words[0]));
				elem4.appendChild(elem5);
				elem5 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:Acronym");
				elem5.appendChild(document.createTextNode(words[1]));
				elem4.appendChild(elem5);
				String entry = words[3]+"_ "+words[5];
				elem5 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:DictionaryEntryName");
				elem5.appendChild(document.createTextNode(entry));
				elem4.appendChild(elem5);
				elem5 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:Version");
				elem5.appendChild(document.createTextNode("1.0"));
				elem4.appendChild(elem5);
				elem5 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:ObjectClassTerm");
				elem5.appendChild(document.createTextNode(words[5]));
				elem4.appendChild(elem5);
				elem5 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:ObjectClassQualifierTerm");
				elem5.appendChild(document.createTextNode(words[3]));
				elem4.appendChild(elem5);
				elem1.appendChild(elem3);
				elem1.appendChild(elem2);
			} else if(words[1].equals("BBIE") && elem2!=null) {
				elem3 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:element");
//				String ename = pack(words[7])+pack(words[9]);
				String ename = buildTagName(words[7], words[9], words[10], "","");
				String tname = pack(words[6])+pack(words[8]);
				bied.get(name).add(new String[] {ename, null, tname, words[0]});
				String type = words[10];
				if(ename.endsWith(type)) type = "";
				elem3.setAttribute("name", ename);
				if(words!=null && words.length>14 && words[14]!=null && words[14].length()>0) {
//System.out.println("qdt:"+words[14]);
					String qType = qdtName(words[14]);
					if("????".equals(qType)) {
System.out.println(words[0]+":\tunfound qdt:\t"+words[14]);
					} else {
						Integer ii = qdtru.get(words[14]);
						if(ii==null) {
							qdtru.put(words[14], Integer.valueOf(1));
						} else {
//							System.out.println("QDT reuse:\t"+(ii+1)+"\t"+words[14]+"\t"+qType);
							qdtru.put(words[14], Integer.valueOf(ii + 1));
						}
						if(words[0].substring(0,1).equals(words[14].substring(0,1))==false) {
							ii = qdtrux.get(words[14]);
							if(ii==null) {
								qdtrux.put(words[14], Integer.valueOf(1));
							} else {
//								System.out.println("QDT cross reuse:\t"+(ii+1)+"\t"+words[14]+"\t"+qType);
								qdtrux.put(words[14], Integer.valueOf(ii + 1));
							}
						}
					}
					elem3.setAttribute("type", "qdt:"+pack(qType)+ words[10]+"Type");
				} else {
//System.out.println("udt:"+words[0]+":"+words.length);
					elem3.setAttribute("type", "udt:"+words[10]+"Type");
				}
				elem2.appendChild(elem3);
				elem4 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:annotation");
				elem5 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:document");
				elem5.setAttribute("xml:lang", "en");
				elem4.appendChild(elem5);
				elem3.appendChild(elem4);

				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:UniqueID");
				elem6.appendChild(document.createTextNode("THIE00"+words[0]));
				elem5.appendChild(elem6);
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:Acronym");
				elem6.appendChild(document.createTextNode(words[1]));
				elem5.appendChild(elem6);
				String entry = ocq+"_ "+oct;
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:DictionaryEntryName");
				elem6.appendChild(document.createTextNode(entry));
				elem5.appendChild(elem6);
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:Version");
				elem6.appendChild(document.createTextNode("1.0"));
				elem5.appendChild(elem6);
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:ObjectClassTerm");
				elem6.appendChild(document.createTextNode(oct));
				elem5.appendChild(elem6);
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:ObjectClassQualifierTerm");
				elem6.appendChild(document.createTextNode(ocq));
				elem5.appendChild(elem6);

				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:PropertyTerm");
				elem6.appendChild(document.createTextNode(words[9]));
				elem5.appendChild(elem6);
				if(words[7]!=null && words[7].length()>0) {
					elem6 = document.createElementNS(
						"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
						, "ccts:PropertyQualifierTerm");
					elem6.appendChild(document.createTextNode(words[7]));
					elem5.appendChild(elem6);
				}

			} else if(words[1].equals("ASBIE")) {
				elem3 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:element");
//				String ename = pack(words[7])+pack(words[9]);
				String ename = buildTagName(words[7], words[9], words[10], words[11], words[12]);
//				String aname = pack(words[11])+pack(words[12]);
				String aname = buildTagName(words[11], words[12],"","","");
				String tname = pack(words[6])+pack(words[8]);
				bied.get(name).add(new String[] {ename, aname, tname, words[0]});

				String type = words[10];
				if(ename.endsWith(type)) type = "";
				elem3.setAttribute("name", ename);
				String asbie = "ASBIEXX";
//				asbie = pack(words[11]) + pack(words[12]);
				asbie = buildTagName(words[11], words[12],"","","");
				elem3.setAttribute("type", "ram:"+asbie+"Type");
				elem2.appendChild(elem3);

				elem4 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:annotation");
				elem5 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:document");
				elem5.setAttribute("xml:lang", "en");
				elem4.appendChild(elem5);
				elem3.appendChild(elem4);

				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:UniqueID");
				elem6.appendChild(document.createTextNode("THIE00"+words[0]));
				elem5.appendChild(elem6);
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:Acronym");
				elem6.appendChild(document.createTextNode(words[1]));
				elem5.appendChild(elem6);
				String entry = ocq+"_ "+oct;
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:DictionaryEntryName");
				elem6.appendChild(document.createTextNode(entry));
				elem5.appendChild(elem6);
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:Version");
				elem6.appendChild(document.createTextNode("1.0"));
				elem5.appendChild(elem6);
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:ObjectClassTerm");
				elem6.appendChild(document.createTextNode(oct));
				elem5.appendChild(elem6);
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:ObjectClassQualifierTerm");
				elem6.appendChild(document.createTextNode(ocq));
				elem5.appendChild(elem6);

				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:PropertyTerm");
				elem6.appendChild(document.createTextNode(words[9]));
				elem5.appendChild(elem6);
				if(words[7]!=null && words[7].length()>0) {
					elem6 = document.createElementNS(
						"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
						, "ccts:PropertyQualifierTerm");
					elem6.appendChild(document.createTextNode(words[7]));
					elem5.appendChild(elem6);
				}

			}
		}

		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.setOutputProperty(OutputKeys.STANDALONE, "no");
		xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		File dir = new File(ssmpath);
		if(!dir.exists()) dir.mkdirs();
		String filename = dir+"/" + "ReusableAggregateBusinessInformationEntity.xml";
		FileOutputStream fos = new FileOutputStream(filename);
		xformer.transform(new DOMSource(document), new StreamResult(fos));
		fos.close();
	}

	public void procRSM() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = null;

		Vector<Document> xmldoc = new Vector<Document>();
		Vector<String> xmlfile = new Vector<String>();

		String[] words;
		String oct = "", ocq = "";
		Element elem0=null, elem1=null, elem2=null, elem3=null, elem4=null, elem5=null;
		Element elem6=null, elem7=null, elem8=null, elem9=null, elem10=null, elem11=null;

		for(int i=0; i<rsm.size(); i++) {
			words = rsm.get(i);
			if(words[1].equals("ABIE")) {

				ocq = words[3];
				oct = words[5];
				String name = buildTagName(ocq, oct,"","","");
				rsmd.add(new Vector<String[]>());
				rsmi.put(words[0], rsmn.size());
				rsmn.add(name);

				document = builder.newDocument();
				xmldoc.add(document);
				xmlfile.add(name);

				elem0 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:schema");
				document.appendChild(elem0);
				elem0.setAttribute("targetNamespace"
					, "urn:th:gov:egif:data:standard:"+name+":1");
				elem0.setAttribute("xmlns:ram"
					, "urn:th:gov:egif:data:standard:ReusableAggregateBusinessInformationEntity:1");
				elem0.setAttribute("xmlns:ccts"
					, "urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2");
				elem0.setAttribute("xmlns:udt", "urn:th:gov:egif:data:standard:UnqualifiedDataType:1");
				elem0.setAttribute("xmlns:qdt", "urn:th:gov:egif:data:standard:QualifiedDataType:1");
				elem0.setAttribute("elementFormDefault", "qualified");
				elem0.setAttribute("attributeFormDefault", "unqualified");
				elem0.setAttribute("version","1.0");

				elem1 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:complexType");
				elem1.setAttribute("name", name+"Type");
				elem2 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:sequence");
				elem0.appendChild(elem1);
				elem3 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:annotation");
				elem4 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:document");
				elem4.setAttribute("xml:lang", "en");
				elem3.appendChild(elem4);

				elem5 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:UniqueID");
				elem5.appendChild(document.createTextNode("THRS00"+words[0]));
				elem4.appendChild(elem5);

				elem5 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:Acronym");
				elem5.appendChild(document.createTextNode(words[1]));
				elem4.appendChild(elem5);
				String entry = words[3]+"_ "+words[5];
				elem5 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:DictionaryEntryName");
				elem5.appendChild(document.createTextNode(entry));
				elem4.appendChild(elem5);
				elem5 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:Version");
				elem5.appendChild(document.createTextNode("1.0"));
				elem4.appendChild(elem5);
				elem5 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:ObjectClassTerm");
				elem5.appendChild(document.createTextNode(words[5]));
				elem4.appendChild(elem5);
				elem5 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:ObjectClassQualifierTerm");
				elem5.appendChild(document.createTextNode(words[3]));
				elem4.appendChild(elem5);

				elem4 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:ObjectClassQualifierTerm");

				elem4 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:import");
				elem4.setAttribute("namespace", "urn:th:gov:egif:data:standard:UnqualifiedDataType:1");
				elem4.setAttribute("schemaLocation"
					, "http://egif.tu-rac.com/gov/egif/data/standard/UnqualifiedDataType_1.xsd");
				elem1.appendChild(elem4);

				elem4 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:import");
				elem4.setAttribute("namespace", "urn:th:gov:egif:data:standard:QualifiedDataType:1");
				elem4.setAttribute("schemaLocation"
					, "http://egif.tu-rac.com/gov/egif/data/standard/QualifiedDataType_1.xsd");
				elem1.appendChild(elem4);

				elem4 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:import");
				elem4.setAttribute("namespace"
					, "urn:th:gov:egif:data:standard:ReusableAggregateBusinessInformationEntity:1");
				elem4.setAttribute("schemaLocation"
					, "http://egif.tu-rac.com/gov/egif/data/standard/ReusableAggregateBusinessInformationEntity_1.xsd");
				elem1.appendChild(elem4);

				elem1.appendChild(elem3);
				elem1.appendChild(elem2);

			} else if(words[1].equals("BBIE") && elem2!=null) {
				elem3 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:element");
//				String ename = pack(words[7])+pack(words[9]);
				String ename = buildTagName(words[7], words[9], words[10], "","");
				String tname = pack(words[6])+pack(words[8]);
				rsmd.get(rsmd.size()-1).add(new String[] {ename, null, tname, words[0]});
				String type = words[10];
				if(ename.endsWith(type)) type = "";
				elem3.setAttribute("name", ename);
				if(words!=null && words.length>14 && words[14]!=null && words[14].length()>0) {
//System.out.println("qdt:"+words[14]);
					String qType = qdtName(words[14]);
					if("????".equals(qType)) {
System.out.println(words[0]+":\tunfound qdt:\t"+words[14]);
					} else {
						Integer ii = qdtru.get(words[14]);
						if(ii==null) {
							qdtru.put(words[14], Integer.valueOf(1));
						} else {
//							System.out.println("QDT reuse:\t"+(ii+1)+"\t"+words[14]+"\t"+qType);
							qdtru.put(words[14], Integer.valueOf(ii + 1));
						}
						if(words[0].substring(0,1).equals(words[14].substring(0,1))==false) {
							ii = qdtrux.get(words[14]);
							if(ii==null) {
								qdtrux.put(words[14], Integer.valueOf(1));
							} else {
//								System.out.println("QDT cross reuse:\t"+(ii+1)+"\t"+words[14]+"\t"+qType);
								qdtrux.put(words[14], Integer.valueOf(ii + 1));
							}
						}
					}
					elem3.setAttribute("type", "qdt:"+pack(qType)+ words[10]+"Type");
				} else {
//System.out.println("udt:"+words[0]+":"+words.length);
					elem3.setAttribute("type", "udt:"+words[10]+"Type");
				}
				elem2.appendChild(elem3);
				elem4 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:annotation");
				elem5 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:document");
				elem5.setAttribute("xml:lang", "en");
				elem4.appendChild(elem5);
				elem3.appendChild(elem4);

				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:UniqueID");
				elem6.appendChild(document.createTextNode("THIE00"+words[0]));
				elem5.appendChild(elem6);
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:Acronym");
				elem6.appendChild(document.createTextNode(words[1]));
				elem5.appendChild(elem6);
				String entry = ocq+"_ "+oct;
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:DictionaryEntryName");
				elem6.appendChild(document.createTextNode(entry));
				elem5.appendChild(elem6);
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:Version");
				elem6.appendChild(document.createTextNode("1.0"));
				elem5.appendChild(elem6);
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:ObjectClassTerm");
				elem6.appendChild(document.createTextNode(oct));
				elem5.appendChild(elem6);
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:ObjectClassQualifierTerm");
				elem6.appendChild(document.createTextNode(ocq));
				elem5.appendChild(elem6);

				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:PropertyTerm");
				elem6.appendChild(document.createTextNode(words[9]));
				elem5.appendChild(elem6);
				if(words[7]!=null && words[7].length()>0) {
					elem6 = document.createElementNS(
						"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
						, "ccts:PropertyQualifierTerm");
					elem6.appendChild(document.createTextNode(words[7]));
					elem5.appendChild(elem6);
				}

			} else if(words[1].equals("ASBIE")) {
				elem3 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:element");
//				String ename = pack(words[7])+pack(words[9]);
				String ename = buildTagName(words[7], words[9], words[10], words[11], words[12]);
				String tname = pack(words[6])+pack(words[8]);
				String type = words[10];
//				String aname = pack(words[11])+pack(words[12]);
				String aname = buildTagName(words[11], words[12],"","","");

				rsmd.get(rsmd.size()-1).add(new String[] {ename, aname, tname, words[0]});

				if(ename.endsWith(type)) type = "";
				elem3.setAttribute("name", ename);
				String asbie = "ASBIEXX";
//				asbie = pack(words[11]) + pack(words[12]);
				asbie = buildTagName(words[11], words[12],"","","");
				elem3.setAttribute("type", "ram:"+asbie+"Type");
				elem2.appendChild(elem3);

				elem4 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:annotation");
				elem5 = document.createElementNS("http://www.w3.org/2001/XMLSchema","xsd:document");
				elem5.setAttribute("xml:lang", "en");
				elem4.appendChild(elem5);
				elem3.appendChild(elem4);

				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:UniqueID");
				elem6.appendChild(document.createTextNode("THIE00"+words[0]));
				elem5.appendChild(elem6);
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:Acronym");
				elem6.appendChild(document.createTextNode(words[1]));
				elem5.appendChild(elem6);
				String entry = ocq+"_ "+oct;
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:DictionaryEntryName");
				elem6.appendChild(document.createTextNode(entry));
				elem5.appendChild(elem6);
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:Version");
				elem6.appendChild(document.createTextNode("1.0"));
				elem5.appendChild(elem6);
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:ObjectClassTerm");
				elem6.appendChild(document.createTextNode(oct));
				elem5.appendChild(elem6);
				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:ObjectClassQualifierTerm");
				elem6.appendChild(document.createTextNode(ocq));
				elem5.appendChild(elem6);

				elem6 = document.createElementNS(
					"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
					, "ccts:PropertyTerm");
				elem6.appendChild(document.createTextNode(words[9]));
				elem5.appendChild(elem6);
				if(words[7]!=null && words[7].length()>0) {
					elem6 = document.createElementNS(
						"urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2"
						, "ccts:PropertyQualifierTerm");
					elem6.appendChild(document.createTextNode(words[7]));
					elem5.appendChild(elem6);
				}

			}
		}

		for(int i=0; i<xmldoc.size(); i++) {
			String name = xmlfile.get(i);
			document = xmldoc.get(i);
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.STANDALONE, "no");
			xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			File dir = new File(rsmpath);
			if(!dir.exists()) dir.mkdirs();
			String filename = dir+"/" + name +".xml";
			FileOutputStream fos = new FileOutputStream(filename);

			xformer.transform(new DOMSource(document), new StreamResult(fos));
			fos.close();
		}

	}

	public String pack(String tag) {
		if(tag==null) return "";
		String[] tags = tag.split(" ");
		tag = "";
		for(int i=0;i<tags.length; i++) {
			tag += tags[i];
		}
		tags = tag.split("-");
		tag = "";
		for(int i=0;i<tags.length; i++) {
			tag += tags[i];
		}
		return tag;
	}

	public void bieXML(Document document, Element parent, String aname) throws Exception {
//System.out.println("step 4.6.1:"+aname);
		Vector<String[]> det = bied.get(aname);
		if(det==null) return;
		for(int i=0; i<det.size(); i++) {
//System.out.println("step 4.6.2");
			String[] elems = det.get(i);
//System.out.println("step 4.6.3:" + elems);
//System.out.println("name:"+elems[0]+":"+elems[1]);
			Element elem0 = null;
			try {
			elem0 = document.createElementNS(
				"urn:th:gov:egif:data:standard:ReusableAggregateBusinessInformationEntity:1"
				, "ram:" + elems[0]);
			} catch(Exception x) {
				System.out.println("error 001:" + x + ":" + elems[0]);
			}
//System.out.println("step 4.6.4:" + elem0);
			parent.appendChild(elem0);
//System.out.println("step 4.6.5:" + parent);
			if(elems[1]==null || elems[1].length()==0) {
//System.out.println("step 4.6.6:");
				if(!elems[0].endsWith("Code") && !elems[0].endsWith("Amount")
					&& !elems[0].endsWith("Date") && !elems[0].endsWith("Measure")
					&& !elems[0].endsWith("ID") && !elems[0].endsWith("Indicator")
					)
					elem0.setAttribute("languageCode", "tha");
				elem0.appendChild(document.createTextNode(elems[2]));
			} else {
//System.out.println("step 4.6.7:"+elems[1]);
				bieXML(document, elem0, elems[1]);
			}
//System.out.println("step 4.6.8");
		}
	}

	public void xmlRSM() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Element elem0, elem1, elem2=null, elem3=null, elem4=null, elem5=null;

		for(int i=0; i<rsmn.size(); i++) {
			Document document = builder.newDocument();
			String name = rsmn.get(i);

			File dir = new File(smppath);
			if(!dir.exists()) dir.mkdirs();
			String filename = dir+"/sample_"+name+".xml";

			elem0 = document.createElementNS("urn:th:gov:egif:data:standard:"+name+":1.0"
				, "rsm:" + name);
			elem0.setAttribute("xmlns:ram"
				, "urn:th:gov:egif:data:standard:ReusableAggregateBusinessInformationEntity:1");
			elem0.setAttribute("xmlns:udt", "urn:th:gov:egif:data:standard:UnqualifiedDataType:1");
			elem0.setAttribute("xmlns:qdt", "urn:th:gov:egif:data:standard:QualifiedDataType:1");
			document.appendChild(elem0);
			Vector<String[]> memv = rsmd.get(i);
			for(int j=0; j<memv.size(); j++) {
				String[] elems = memv.get(j);
				elem1 = document.createElementNS("urn:th:gov:egif:data:standard:"
					+name+":1.0", "rsm:" + elems[0]);
				elem0.appendChild(elem1);
				if(elems[1]==null || elems[1].length()==0) {
					if(!elems[0].endsWith("Code") && !elems[0].endsWith("Amount")
						&& !elems[0].endsWith("Date") && !elems[0].endsWith("Measure")
						&& !elems[0].endsWith("ID") && !elems[0].endsWith("Indicator")
						)
						elem1.setAttribute("languageCode", "tha");
					elem1.appendChild(document.createTextNode(elems[2]));
				} else {
					bieXML(document, elem1, elems[1]);
				}
			}
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.STANDALONE, "no");
			xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			FileOutputStream fos = new FileOutputStream(filename);
			xformer.transform(new DOMSource(document), new StreamResult(fos));
			fos.close();
		}
	}

	public void jsonRSM() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Element elem0, elem1, elem2=null, elem3=null, elem4=null, elem5=null;

		for(int i=0; i<rsmn.size(); i++) {
			Document document = builder.newDocument();
			String name = rsmn.get(i);

			File dir = new File(jsonpath);
			if(!dir.exists()) dir.mkdirs();
			String filename = dir+"/sample_"+name+".xml";

			elem0 = document.createElementNS("urn:th:gov:egif:data:standard:"+name+":1.0"
				, "rsm:" + name);
			elem0.setAttribute("xmlns:ram"
				, "urn:th:gov:egif:data:standard:ReusableAggregateBusinessInformationEntity:1");
			elem0.setAttribute("xmlns:udt", "urn:th:gov:egif:data:standard:UnqualifiedDataType:1");
			elem0.setAttribute("xmlns:qdt", "urn:th:gov:egif:data:standard:QualifiedDataType:1");
			document.appendChild(elem0);
			Vector<String[]> memv = rsmd.get(i);
			for(int j=0; j<memv.size(); j++) {
				String[] elems = memv.get(j);
				elem1 = document.createElementNS("urn:th:gov:egif:data:standard:"
					+name+":1.0", "rsm:" + elems[0]);
				elem0.appendChild(elem1);
				if(elems[1]==null || elems[1].length()==0) {
					if(!elems[0].endsWith("Code") && !elems[0].endsWith("Amount")
						&& !elems[0].endsWith("Date") && !elems[0].endsWith("Measure")
						&& !elems[0].endsWith("ID") && !elems[0].endsWith("Indicator")
						)
						elem1.setAttribute("languageCode", "tha");
					elem1.appendChild(document.createTextNode(elems[2]));
				} else {
					bieXML(document, elem1, elems[1]);
				}
			}
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.STANDALONE, "no");
			xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			FileOutputStream fos = new FileOutputStream(filename);
			xformer.transform(new DOMSource(document), new StreamResult(fos));
			fos.close();
		}
	}

	public void bpWSDL(String recv, int rsmord) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();

		String name = rsmn.get(rsmord);

		File dir = new File(wsdlpath);
		if(!dir.exists()) dir.mkdirs();
		String filename = dir + "/wsdl_" + pack(recv) + ".xml";

		Element elem0, elem1, elem2=null, elem3=null, elem4=null, elem5=null;

		elem0 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/"
			, "wsdl:definitions");
		document.appendChild(elem0);
		elem0.setAttribute("targetNamespace"
			, "urn:th:gov:egif:data:standard:wsdl:"+pack(recv)+":1.0");
		elem0.setAttribute("xmlns:tns"
			, "urn:th:gov:egif:data:standard:wsdl:"+pack(recv)+":1.0");
		elem0.setAttribute("xmlns:soap", "http://schemas.xmlsoap.org/wsdl/soap/");
		elem0.setAttribute("xmlns:rsm"
			, "urn:th:gov:egif:data:standard:"+name+":1.0");

		elem1 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:import");
		elem1.setAttribute("namespace", "urn:th:gov:egif:data:standard:"+name+":1.0");
		elem0.appendChild(elem1);

		elem1 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:message");
		elem1.setAttribute("name", pack(recv)+"Request");
		elem0.appendChild(elem1);
		elem2 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:part");
		elem2.setAttribute("element", "rsm:"+name);
		elem2.setAttribute("name", "body");
		elem1.appendChild(elem2);

		elem1 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:message");
		elem1.setAttribute("name", pack(recv)+"Response");
		elem0.appendChild(elem1);
		elem2 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:part");
		elem2.setAttribute("element", "Acknowledgement");
		elem2.setAttribute("name", "body");
		elem1.appendChild(elem2);

		elem1 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:portType");
		elem1.setAttribute("name", pack(recv)+"_PortType");
		elem0.appendChild(elem1);
		elem2 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:operation");
		elem2.setAttribute("name", pack(recv));
		elem1.appendChild(elem2);
		elem3 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:input");
		elem3.setAttribute("message", pack(recv)+"Request");
		elem2.appendChild(elem3);
		elem3 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:output");
		elem3.setAttribute("message", pack(recv)+"Response");
		elem2.appendChild(elem3);

		elem1 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:binding");
		elem1.setAttribute("name", pack(recv)+"_Binding");
		elem1.setAttribute("type", pack(recv)+"_PortType");
		elem0.appendChild(elem1);

		elem2 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/soap/", "soap:binding");
		elem2.setAttribute("style", "document");
		elem2.setAttribute("transport", "http://schemas.xmlsoap.org/soap/http");
		elem1.appendChild(elem2);

		elem3 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:operation");
		elem3.setAttribute("name", pack(recv));
		elem1.appendChild(elem3);
		elem4 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:input");
		elem4.setAttribute("message", pack(recv)+"Request");
		elem3.appendChild(elem4);
		elem5 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:body");
		elem5.setAttribute("parts", "body");
		elem5.setAttribute("use", "literal");
		elem4.appendChild(elem5);

		elem4 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:output");
		elem4.setAttribute("message", pack(recv)+"Response");
		elem3.appendChild(elem4);
		elem5 = document.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:body");
		elem5.setAttribute("parts", "body");
		elem5.setAttribute("use", "literal");
		elem4.appendChild(elem5);

		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.setOutputProperty(OutputKeys.STANDALONE, "no");
		xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		FileOutputStream fos = new FileOutputStream(filename);
		xformer.transform(new DOMSource(document), new StreamResult(fos));
		fos.close();
	}

	int port = 10000;

	public void rmiServerGen(String dir, String name, String rsmid, String to, String inpid
		, String selfhost, String peerhost, boolean bReq) 
		throws Exception {
		int rsmord = rsmi.get(rsmid);
		FileOutputStream fos = new FileOutputStream(dir+"/"+name+"Rmi.java");
		PrintWriter out = new PrintWriter(fos);

		Vector<String[]> reqmsg = getMsgInfo(inpid);
		Vector<String[]> resmsg = getMsgInfo(rsmid);
//		int reqdp = calcMaxIndent(reqmsg);
//		String reqmsgnm = rsmn.get(rsmi.get(rsmid));

		int resdp = calcMaxIndent(resmsg);
//		String resmsgnm = rsmn.get(rsmi.get(resid));

		out.println("package com.dabos.rmisrv;");
		out.println("");
		out.println("import java.rmi.*;");
		out.println("import java.util.*;");
		out.println("import java.net.*;");
		out.println("import java.io.*;");
		out.println("import java.text.*;");
		out.println("import java.sql.*;");
		out.println("import javax.xml.xpath.*;");
		out.println("import java.rmi.server.*;");
		out.println("import java.rmi.registry.*;");
		out.println("import org.w3c.dom.*;");
		out.println("import org.xml.sax.*;");
		out.println("import javax.xml.transform.*;");
		out.println("import javax.xml.transform.dom.*;");
		out.println("import javax.xml.transform.stream.*;");
		out.println("import javax.xml.parsers.*;");
		out.println("import com.dabos.engine.lib.*;");
		out.println("import javax.crypto.*;");
		out.println("import javax.xml.parsers.*;");
		out.println("import java.security.*;");
		out.println("import javax.xml.crypto.dsig.*;");
		out.println("import javax.xml.crypto.dsig.dom.*;");
		out.println("import javax.xml.crypto.dsig.spec.*;");
		out.println("import javax.xml.crypto.dsig.keyinfo.*;");
		out.println("import javax.xml.transform.*;");
		out.println("import javax.xml.transform.dom.*;");
		out.println("import javax.xml.transform.stream.*;");
		out.println("import javax.crypto.spec.*;");

		out.println("");
		out.println("public class " + name + "Rmi extends UnicastRemoteObject  ");
		out.println("	implements com.dabos.engine.lib.WebServiceRmi, Runnable {");
		out.println("	");
		out.println("	static DecimalFormat frm6 = new DecimalFormat(\"000000\");");
		out.println("	int ordno = 0;");
		out.println("	int msgid = 0;");
		out.println("	public Vector<Hashtable<String,Object>> worklog;");
		out.println("	public Hashtable<String,String> config;");
		out.println("	");
		out.println("	public " + name + "Rmi(int port) throws RemoteException {");
		out.println("		super(port);");
		out.println("		config = new Hashtable<String,String>();");
		out.println("		");
		out.println("		config.put(\"self-url\", \"http://" + selfhost + "/" + name + "/service\");");
		out.println("		config.put(\"peer-url\", \"http://" + peerhost + "/" + to  +"/service\");");
		out.println("		config.put(\"self-p12\", \"" + selfhost + ".p12\");");
		out.println("		config.put(\"peer-cer\", \"" + peerhost + ".cer\");");
		out.println("		config.put(\"jdbc-url\", \"jdbc:hsqldb:file:db"+ name +"/db1\");");
		out.println("		config.put(\"jdbc-usr\", \"sa\");");
		out.println("		config.put(\"jdbc-pw\", \"\");");
		out.println("		config.put(\"self-name\", \"" + selfhost + "\");");
		out.println("		config.put(\"peer-name\", \"" + peerhost + "\");");
		out.println("		config.put(\"uddi-url\", \"\");");
		out.println("		config.put(\"ldap-url\", \"\");");
		out.println("		config.put(\"auto-rpy\", \"X\");");
		out.println("		config.put(\"jdbc-drv\", \"org.hsqldb.jdbcDriver\");");
		out.println("		config.put(\"char-set\", \"\");");

		out.println("		try {");
		out.println("			FileInputStream fis = new FileInputStream(\"" + name + ".cfg\");");
		out.println("			ObjectInputStream ois = new ObjectInputStream(fis);");
		out.println("			Hashtable<String,String> savecfg = (Hashtable<String,String>) ois.readObject();");
		out.println("			ois.close();");
		out.println("			Enumeration keys = savecfg.keys();");
		out.println("			String key, t1;");
		out.println("			while( keys.hasMoreElements() ) {");
		out.println("				key = (String) keys.nextElement();");
		out.println("				config.put(key, savecfg.get(key));");
		out.println("			}");
		out.println("		} catch(Exception ex) {}");
		out.println("		try {");
		out.println("			FileOutputStream fos = new FileOutputStream(\"" + name + ".cfg\");");
		out.println("			ObjectOutputStream oos = new ObjectOutputStream(fos);");
		out.println("			oos.writeObject(config);");
		out.println("			oos.close();");
		out.println("		} catch(Exception ex) {}");
		out.println("		worklog = new Vector<Hashtable<String,Object>>();");
		out.println("	}");
		out.println("");

		out.println("	public byte[] doWebService(byte[] xmldoc) throws RemoteException {");
		out.println("		byte[] res = new byte[0];");
		out.println("		try {");
		out.println("			String msgId = null;");
		out.println("			String relateId = null;");
		out.println("			String replyto = null;");
		out.println("			Element body = null;");
		out.println("			");

		out.println("			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();");
		out.println("			factory.setNamespaceAware(true);");
		out.println("			DocumentBuilder builder = factory.newDocumentBuilder();");
		out.println("			Transformer xformer = TransformerFactory.newInstance().newTransformer();");
		out.println("			xformer.setOutputProperty(OutputKeys.STANDALONE, \"no\");");
		out.println("			xformer.setOutputProperty(OutputKeys.ENCODING, \"UTF-8\");");
		out.println("			");

		out.println("			try {");
		out.println("				ByteArrayInputStream bais = new ByteArrayInputStream(xmldoc);");
		out.println("				Document reqdoc = builder.parse(bais);");
		out.println("				");
		out.println("				XPathParser qParse = new XPathParser();");
		out.println("				String env = \"http://www.w3.org/2003/05/soap-envelope\";");
		out.println("				String wsa = \"http://www.w3.org/2005/08/addressing\";");
		out.println("				qParse.addPrefixNS(\"env\", env);");
		out.println("				qParse.addPrefixNS(\"wsa\", wsa);");

		out.println("				XPath xpath = XPathFactory.newInstance().newXPath();");
		out.println("				xpath.setNamespaceContext(qParse);");

		out.println("				Object obj = null;");
		out.println("				NodeList ndlist = null;");
		out.println("				Element idelem = null;");
//		out.println("				xpath = XPathFactory.newInstance().newXPath();");
//		out.println("				xpath.setNamespaceContext(qParse);");

		out.println("				String bdpth = \"/env:Envelope/env:Body\";");
		out.println("				obj = xpath.evaluate(bdpth, reqdoc, XPathConstants.NODESET);");
		out.println("				ndlist = (NodeList) obj;");
		out.println("				body = (Element) ndlist.item(0);");
		out.println("				body = (Element) body.getFirstChild();");

		out.println("				String idpath = \"/env:Envelope/env:Header/wsa:MessageID\";");
		out.println("				obj = xpath.evaluate(idpath, reqdoc, XPathConstants.NODESET);");
		out.println("				ndlist = (NodeList) obj;");
		out.println("				idelem = (Element) ndlist.item(0);");
		out.println("				msgId = idelem.getFirstChild().getNodeValue();");

		out.println("				try {");
		out.println("				String relpath = \"/env:Envelope/env:Header/wsa:RelatesTo\";");
		out.println("				obj = xpath.evaluate(relpath, reqdoc, XPathConstants.NODESET);");
		out.println("				ndlist = (NodeList) obj;");
		out.println("				idelem = (Element) ndlist.item(0);");
		out.println("				relateId = idelem.getFirstChild().getNodeValue();");
		out.println("				} catch(Exception ex) {}");

		out.println("				try {");
		out.println("				String replypath = \"/env:Envelope/env:Header/wsa:ReplyTo/wsa:Address\";");
		out.println("				obj = xpath.evaluate(replypath, reqdoc, XPathConstants.NODESET);");
//		out.println("	System.out.println(\"obj:\"+obj);");
		out.println("				ndlist = (NodeList) obj;");
//		out.println("	System.out.println(\"ndlist:\" + ndlist);");
		out.println("				idelem = (Element) ndlist.item(0);");
//		out.println("	System.out.println(\"idelem:\" + idelem);");
		out.println("				replyto = idelem.getFirstChild().getNodeValue();");
		out.println("	System.out.println(\"replyto:\" + replyto);");
		out.println("				} catch(Exception ex) {}");
		out.println("		} catch(Exception ex2) {}");

//		out.println("			System.out.println(\"=== \" + idelem.getFirstChild().getNodeValue());");

//		out.println("			System.out.println(\"parsing\");");
//		out.println("			System.out.write(xmldoc, 0, xmldoc.length);");
//		out.println("			System.out.flush();");
//		out.println("			System.out.println(\"msgId:\"+ msgId);");
//		out.println("			System.out.println(\"relateId:\"+ relateId);");

		out.println("			String msgid = null;");
		out.println("			String flowtype = null;");
		out.println("			String soaptype = null;");
		out.println("			if(relateId==null) {");
		out.println("				ordno++;");
		out.println("				Hashtable<String,Object> work = new Hashtable<String,Object>();");
		out.println("				work.put(\"ID\", frm6.format(ordno));");
		out.println("				work.put(\"TYPE\", \"REQ\");");
		out.println("				work.put(\"TIME\", new java.util.Date());");
		out.println("				work.put(\"STATUS\", \"Rq\");");
		out.println("				work.put(\"RELID\",  msgId);");
		out.println("				work.put(\"ReplyTo\",  replyto);");
		out.println("	System.out.println(\"replyto2:\" + replyto);");
		out.println("				worklog.add(work);");
		out.println("				msgid = frm6.format(ordno);");
		out.println("				flowtype = \"REQ\";");
		out.println("				soaptype = \"SRQ\";");
		out.println("			} else {");
		out.println("				for(int c=0; c<worklog.size(); c++) {");
		out.println("					Hashtable<String,Object> work = worklog.get(c);");
		out.println("					if(relateId.equals(work.get(\"ID\"))) {");
		out.println("						work.put(\"STATUS\", \"Ed\");");
		out.println("						flowtype = \"RES\";");
		out.println("						soaptype = \"SRS\";");
		out.println("						msgid = relateId;");
		out.println("						break;");
		out.println("					}");
		out.println("				}");
		out.println("			}");

//		out.println("	System.out.println(\"body:\"+ body +\" msg:\" + msgid);");
		out.println("			if(msgid!=null && body!=null) {");
		out.println("				File logxml = new File(\"log" + name + "/\");");
		out.println("				if(!logxml.exists()) logxml.mkdirs();");
		out.println("				FileOutputStream fos = new FileOutputStream(");
		out.println("					logxml + \"/\" + msgid+ \"-\" + flowtype + \".xml\");");
		out.println("				xformer.transform(new DOMSource(body), new StreamResult(fos));");
		out.println("				fos.close();");
		out.println("				fos = new FileOutputStream(");
		out.println("					logxml + \"/\" + msgid+ \"-\" + soaptype + \".xml\");");
		out.println("				fos.write(xmldoc, 0, xmldoc.length);");
		out.println("				fos.close();");
		out.println("			}");

		// Acknowledge
		out.println("			");
		out.println("			ByteArrayOutputStream baos = new ByteArrayOutputStream();");
		out.println("			Document document = builder.newDocument();");
		out.println("			Element elem0, elem1, elem2=null;");
		out.println("			String rsm = \"urn:th:gov:egif:codelist:standard:Acknowledgement:1.0\";");
		out.println("			String ram = \"urn:th:gov:egif:data:standard:ReusableAggregateBusinessInformationEntity:1\";");
		out.println("			elem0 = document.createElementNS(rsm, \"rsm:Acknowledgement\");");
		out.println("			elem0.setAttribute(\"xmlns:ram\", ram);");
		out.println("			document.appendChild(elem0);");
		out.println("			elem1 = document.createElementNS(rsm, \"rsm:AcknowledgementDocument\");");
		out.println("			elem0.appendChild(elem1);");
		out.println("			elem2 = document.createElementNS(ram, \"ram:ID\");");
		out.println("			elem2.appendChild(document.createTextNode(\"X\"));");
		out.println("			elem1.appendChild(elem2);");
		out.println("			elem2 = document.createElementNS(ram, \"ram:AcknowledgementStatusCode\");");
		out.println("			elem2.appendChild(document.createTextNode(\"2\"));");
		out.println("			elem1.appendChild(elem2);");

//		out.println("			Transformer xformer = TransformerFactory.newInstance().newTransformer();");
//		out.println("			xformer.setOutputProperty(OutputKeys.STANDALONE, \"no\");");
//		out.println("			xformer.setOutputProperty(OutputKeys.ENCODING, \"UTF-8\");");
		out.println("			xformer.transform(new DOMSource(document), new StreamResult(baos));");

		out.println("			res = baos.toByteArray();");
//		out.println("			System.out.println(\"output:\"+res.length);");

		out.println("			");

		out.println("			ResponseEngine engine = new ResponseEngine();");
		out.println("			engine.setReqFile(\"log" + name + "/\" + msgid+ \"-\" + flowtype + \".xml\");");
		out.println("			if(\"1\".equals(config.get(\"auto-rpy\")) && relateId==null)");
		out.println("				engine.setReplyToID(msgid);");
		out.println("			engine.start();");
/*
		out.println("			if(\"1\".equals(config.get(\"auto-rpy\")) && relateId==null) {");
		out.println("				ResponseEngine engine = new ResponseEngine();");
		out.println("				engine.setReqFile(\"log" + name + "/\" + msgid+ \"-\" + flowtype + \".xml\");");
		out.println("				engine.setReplyToID(msgid);");
		out.println("				engine.start();");
		out.println("				");
		out.println("			}");
*/
		out.println("		} catch(Exception ex) {");
		out.println("			ex.printStackTrace();");
		out.println("		}");
		out.println("		return res;");
		out.println("	}");

		out.print("	String[] req_p = new String[" + reqmsg.size() + "];");
		out.print("	String[] res_p = new String[" + resmsg.size() + "];");

		out.println("	String[][] req_a = {");
		for(int i=0; i<reqmsg.size(); i++) {
			if(i>0) out.print(",");
			out.print("	{");
			String[] words = reqmsg.get(i);
			out.print("\"" + i + "\"");
			for(int c=1; c<(words.length-1); c++) {
				out.print(", \"" + words[c] + "\"");
			}
			out.println("}");
		}
		out.println("};");
		out.println("	String[][] res_a = {");
		for(int i=0; i<resmsg.size(); i++) {
			if(i>0) out.print(",");
			out.print("	{");
			String[] words = resmsg.get(i);
			out.print("\"" + i + "\"");
			for(int c=1; c<(words.length-1); c++) {
				out.print(", \"" + words[c] + "\"");
			}
			out.println("}");
		}
		out.println("};");

		out.println("// ================ Response Engine ==================");
		out.println("class ResponseEngine extends Thread {");

		out.println("	String reqFile;");
		out.println("	public void setReqFile(String file) {");
		out.println("	System.out.println(\"========== file:\" + file);");
		out.println("		reqFile = file;");
		out.println("	}");
		out.println("	");
		out.println("	String replyToID;");
		out.println("	public void setReplyToID(String id) {");
		out.println("		replyToID = id;");
		out.println("	}");
		out.println("	");
		out.println("	public void run() {");
		out.println("		try { Thread.sleep(1000); } catch(Exception ex) {}");
		out.println("		System.out.println(\"============  send back auto ============\");");
		out.println("		try {");
		out.println("			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();");
		out.println("			factory.setNamespaceAware(true);");
		out.println("			DocumentBuilder builder = factory.newDocumentBuilder();");
		out.println("			Document document = builder.parse(reqFile);");
		out.println("			");
		out.println("			String nd; Object opath; Element elm;");

		out.println("			// ============= get data to from request ============");

		if(bReq)
			out.println("			String rsm = \"urn:th:gov:egif:data:standard:"+ rsmn.get(rsmi.get(inpid)) + ":1.0\";");
		else
			out.println("			String rsm = \"urn:th:gov:egif:data:standard:"+ rsmn.get(rsmi.get(inpid)) + ":1.0\";");
		out.println("			String ram = \"urn:th:gov:egif:data:standard:ReusableAggregateBusinessInformationEntity:1\";");
		out.println("			XPathParser qParse = new XPathParser();");
		out.println("			qParse.addPrefixNS(\"rsm\", rsm);");
		out.println("			qParse.addPrefixNS(\"ram\", ram);");
		out.println("			XPath xpath = XPathFactory.newInstance().newXPath();");
		out.println("			xpath.setNamespaceContext(qParse);");
		out.println("			String dec = null, dd;");
		out.println("			int ix ,iy;");

		out.println("			Hashtable<String,Vector<String>> fldToDb = new Hashtable<String,Vector<String>>();");
		out.println("			Hashtable<String,String> recvData = new Hashtable<String,String>();");

		out.println("			System.out.println(\"file:\" + reqFile);");
		out.println("			System.out.println(\"rsm:\" + rsm);");

		if(bReq) {
		out.println("			for(int i=0; i<res_a.length; i++) {");
		out.println("				if((dec=config.get(\"resField\"+i))!=null) {");
		out.println("					String[] uds = dec.split(\":\");");
		out.println("					if(uds.length<2) continue;");
		out.println("					Vector<String> tbv = fldToDb.get(uds[0]);");
		out.println("					if(tbv==null) {");
		out.println("						tbv = new Vector<String>();");
		out.println("						fldToDb.put(uds[0], tbv);");
		out.println("					}");
		out.println("					tbv.add(uds[1]);");
		out.println("					try {");
		out.println("						opath = xpath.evaluate(req_a[i][4], document, XPathConstants.NODESET);");
		out.println("System.out.println(\"tag:\"+ res_a[i][4]);");
		out.println("						elm = (Element)((NodeList)opath).item(0);");
		out.println("System.out.println(\"elm:\"+ elm);");
		out.println("						String dt = elm.getFirstChild().getNodeValue();");
		out.println("						String fld = uds[0]+\":\"+uds[1];");
		out.println("						recvData.put(fld, dt);");
		out.println("					} catch(Exception x5) {");
		out.println("System.out.println(\"xxx:\"+ x5);");
		out.println("					}");
		out.println("				}");
		out.println("			}");
		} else {
		out.println("			for(int i=0; i<req_a.length; i++) {");
		out.println("				if((dec=config.get(\"reqField\"+i))!=null) {");
		out.println("					String[] uds = dec.split(\":\");");
		out.println("					if(uds.length<2) continue;");
		out.println("					Vector<String> tbv = fldToDb.get(uds[0]);");
		out.println("					if(tbv==null) {");
		out.println("						tbv = new Vector<String>();");
		out.println("						fldToDb.put(uds[0], tbv);");
		out.println("					}");
		out.println("					tbv.add(uds[1]);");
		out.println("					try {");
		out.println("						opath = xpath.evaluate(req_a[i][4], document, XPathConstants.NODESET);");
		out.println("System.out.println(\"tag:\"+ req_a[i][4]);");
		out.println("						elm = (Element)((NodeList)opath).item(0);");
		out.println("System.out.println(\"elm:\"+ elm);");
		out.println("						String dt = elm.getFirstChild().getNodeValue();");
		out.println("						String fld = uds[0]+\":\"+uds[1];");
		out.println("						recvData.put(fld, dt);");
		out.println("					} catch(Exception x5) {");
		out.println("System.out.println(\"xxx:\"+ x5);");
		out.println("					}");
		out.println("				}");
		out.println("			}");
		}

		out.println("			");
		out.println("			Enumeration keys = fldToDb.keys();");
		out.println("			while( keys.hasMoreElements()) {");
		out.println("				String tbnm = (String) keys.nextElement();");
		out.println("				Vector<String> tbv = fldToDb.get(tbnm);");
		out.println("				String sql1 = \"insert into \" + tbnm + \" (\";");
		out.println("				String sql2 = \"\";");
		out.println("				for(int i=0; i<tbv.size(); i++) {");
		out.println("					if(i>0) { sql1 += \", \"; sql2 += \", \"; }");
		out.println("					String fld = tbv.get(i);");
		out.println("					sql1 += fld;");
		out.println("					sql2 += \"'\" + recvData.get(tbnm+\":\"+fld) + \"'\";");
		out.println("				}");
		out.println("				sql1 += \") values (\";");
		out.println("				sql1 += sql2 + \")\";");
		out.println("				System.out.println(sql1);");
		out.println("				try {");
//		out.println("					Class.forName(\"org.hsqldb.jdbcDriver\" );");
		out.println("					Class.forName(config.get(\"jdbc-drv\"));");
		out.println("					Connection conn = DriverManager.getConnection("
				+							" config.get(\"jdbc-url\"), config.get(\"jdbc-usr\"), config.get(\"jdbc-pw\"));");
		out.println("					try {");
		out.println("						Statement st = conn.createStatement();");
		out.println("						st.executeUpdate(sql1);");
		out.println("					} catch(Exception x6) {");
		out.println("						System.out.println(\"err:\" + x6);");
		out.println("					}");
		out.println("					conn.close();");
		out.println("				} catch(Exception x5) {");
		out.println("					System.out.println(\"err:\" + x5);");
		out.println("				}");
		out.println("			}");
		out.println("			");
		out.println("			if(replyToID==null || replyToID.length()==0) return;");
//		out.println("System.out.println(\"field to db:\"+ fldToDb.size());");

		out.println("			for(int i=0; i<res_a.length; i++) {");
		out.println("				if((dec=config.get(\"resField\"+i))!=null) {");
		out.println("					if(dec.startsWith(\"${req\") && (ix=dec.indexOf(\"}\"))>4) {");
		out.println("						try {");
		out.println("							int id = Integer.parseInt(dec.substring(5,ix));");
		out.println("							opath = xpath.evaluate(req_a[id][4], document, XPathConstants.NODESET);");
		out.println("							elm = (Element)((NodeList)opath).item(0);");
		out.println("							res_p[i] = elm.getFirstChild().getNodeValue();");
		out.println("						} catch(Exception x5) {}");
		out.println("					}");
		out.println("					if(dec.startsWith(\"select \")) {");
		out.println("						try {");
		out.println("							while( (ix=dec.indexOf(\"${req\"))>0 && (iy=dec.indexOf(\"}\"))>0) {");
		out.println("								int id = Integer.parseInt(dec.substring(ix+5,iy));");
		out.println("								opath = xpath.evaluate(req_a[id][4], document, XPathConstants.NODESET);");
		out.println("								elm = (Element)((NodeList)opath).item(0);");
		out.println("								dd = elm.getFirstChild().getNodeValue();");
		out.println("								dec = dec.substring(0,ix) + dd + dec.substring(iy+1);");
		out.println("							}");
		out.println("							res_p[i] = dec;");
//		out.println("							Class.forName(\"org.hsqldb.jdbcDriver\" );");
		out.println("							Class.forName(config.get(\"jdbc-drv\"));");
		out.println("							Connection conn = DriverManager.getConnection("
				+								" config.get(\"jdbc-url\"), config.get(\"jdbc-usr\"), config.get(\"jdbc-pw\"));");
		out.println("							try {");
		out.println("								Statement st = conn.createStatement();");
		out.println("								ResultSet rs = st.executeQuery(dec);");
		out.println("								if(rs.next()) {");
		out.println("									res_p[i] = rs.getString(1);");
		out.println("									String charset = config.get(\"char-set\");");
		out.println("									if(charset!=null && charset.length()>0)");
		out.println("										res_p[i] = new String(res_p[i].getBytes(), charset);");
		out.println("								} else {");
		out.println("									res_p[i] = \"no data found\";");
		out.println("								}");
		out.println("							} catch(Exception ex2) {");
		out.println("								res_p[i] = \"err:\" + ex2;");
		out.println("							}");
		out.println("							conn.close();");
		out.println("						} catch(Exception x5) {");
		out.println("							res_p[i] = \"err:\" + x5;");
		out.println("						}");
		out.println("					}");
		out.println("				}");
		out.println("			}");

//		out.println("			for(int i=0; i<res_p.length; i++) {");
//		out.println("				if(res_p[i]!=null) System.out.println(i + \":\" + res_p[i]);");
//		out.println("			}");

		out.println("			// ============= send back the data ============");
		out.println("			String rsmns = \"urn:th:gov:egif:data:standard:"+ rsmn.get(rsmi.get(rsmid)) +":1.0\";");
		out.println("			String ramns = \"urn:th:gov:egif:data:standard:ReusableAggregateBusinessInformationEntity:1\";");
		out.println("			");

		out.println("			Document document2 = builder.newDocument();");
		out.println("			Element[] elems = new Element["+(resdp+2)+"];");
		out.println("			for(int i=0; i<res_a.length; i++) {");
		out.println("				String[] prefs = res_a[i][4].split(\"/\");");
		out.println("				int lv = prefs.length - 1;");
		out.println("				String ns = (lv>2)? ramns : rsmns;");
		out.println("				String pf = (lv>2)? \"ram:\" : \"rsm:\";");
		out.println("				elems[lv] = document2.createElementNS(ns, pf+res_a[i][1]);");
		out.println("				if(i==0) {");
		out.println("					elems[lv].setAttribute(\"xmlns:ram\", ramns);");
		out.println("					elems[lv].setAttribute(\"xmlns:rsm\", rsmns);");
		out.println("					document2.appendChild(elems[lv]);");
		out.println("				} else {");
		out.println("					elems[lv-1].appendChild(elems[lv]);");
		out.println("				}");
		out.println("				if(!\"ABIE\".equals(res_a[i][3])) {");
		out.println("					if(res_p[i]!=null)");
		out.println("						elems[lv].appendChild(document2.createTextNode(res_p[i]));");
		out.println("				}");
		out.println("			");
		out.println("			}");

		out.println("			Transformer xformer = TransformerFactory.newInstance().newTransformer();");
		out.println("			xformer.setOutputProperty(OutputKeys.STANDALONE, \"no\");");
		out.println("			xformer.setOutputProperty(OutputKeys.ENCODING, \"UTF-8\");");
		out.println("			ByteArrayOutputStream baos = new ByteArrayOutputStream();");
		out.println("			xformer.transform(new DOMSource(document2), new StreamResult(baos));");
		out.println("			byte[] reqdoc = baos.toByteArray();");
		out.println("			");
//		out.println("			System.out.println(\"size:\" + reqdoc.length);");
		out.println("			getObject(\"setRelateToID\", replyToID);");
//		out.println("			System.out.println(\"set relate to:\" + replyToID);");
		out.println("			doWSCall(reqdoc);");
//		out.println("			System.out.println(\"called:\");");

		out.println("		} catch(Exception ex) {");
		out.println("			System.out.println(\"error in engine:\"+ ex);");
		out.println("		}");
		out.println("		");

		out.println("		");
		out.println("	}");
		out.println("}");

		out.println("	String setRelateToID = null;");
		out.println("	");
		out.println("	public byte[] doWSCall(byte[] xmldoc) throws RemoteException {");
		out.println("		byte[] res = new byte[0];");
		out.println("		try {");
		out.println("			");
		out.println("			ByteArrayInputStream bais = new ByteArrayInputStream(xmldoc);");

		out.println("			String flowtype = null;");
		out.println("			String soaptype = null;");
		out.println("			String msgID = null;");
		out.println("			String replyto = null;");
		out.println("			if(setRelateToID!=null) {");
		out.println("				Hashtable<String, Object> curr = null;");
		out.println("				for(int i=0; i<worklog.size(); i++) {");
		out.println("					Hashtable<String, Object> work = worklog.get(i);");
		out.println("					if(setRelateToID.equals(work.get(\"ID\"))) curr = work;");
		out.println("				}");
		out.println("				if(curr!=null) {");
		out.println("					flowtype = \"RES\";");
		out.println("					soaptype = \"SRS\";");
		out.println("					msgID = setRelateToID;");
		out.println("					setRelateToID = (String) curr.get(\"RELID\");");
		out.println("					replyto = (String) curr.get(\"ReplyTo\");");
		out.println("					curr.put(\"ENDTIME\", new java.util.Date());");
		out.println("					curr.put(\"STATUS\", \"Rs\");");
		out.println("				}");
		out.println("			} else {");
		out.println("				msgid++;");
		out.println("				flowtype = \"REQ\";");
		out.println("				soaptype = \"SRQ\";");
		out.println("				Hashtable<String,Object> work = new Hashtable<String,Object>();");
		out.println("				msgID = frm6.format(msgid);");
		out.println("				work.put(\"ID\", msgID);");
		out.println("				work.put(\"TYPE\", \"REQ\");");
		out.println("				work.put(\"TIME\", new java.util.Date());");
		out.println("				work.put(\"STATUS\", \"S1\");");
		out.println("				worklog.add(work);");
		out.println("			}");

		out.println("			File logxml = new File(\"log" + name + "/\");");
		out.println("			if(!logxml.exists()) logxml.mkdirs();");
		out.println("			FileOutputStream fos = new FileOutputStream(");
		out.println("				logxml + \"/\" + msgID+ \"-\" + flowtype + \".xml\");");
		out.println("			fos.write(xmldoc, 0, xmldoc.length);");
		out.println("			fos.close();");

		out.println("			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();");
		out.println("			factory.setNamespaceAware(true);");
		out.println("			DocumentBuilder builder = factory.newDocumentBuilder();");
		out.println("			Document document = builder.newDocument();");
		out.println("			Element elem1, elem2, elem3, elem4, elem5;");

		// CHM 20090506 --- begin
		out.println("			String env = \"http://www.w3.org/2003/05/soap-envelope\";");
		out.println("			String wsa = \"http://www.w3.org/2005/08/addressing\";");
		out.println("			String wsse = \"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\";");
		out.println("			String saml = \"urn:oasis:names:tc:SAML:1.0:assertion\";");
		out.println("			String wsu = \"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\";");
		out.println("			String wsse11 = \"http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsd\";");
		out.println("			String wsrm = \"http://docs.oasis-open.org/ws-rx/wsrm/200702\";");

		out.println("			elem1 = document.createElementNS(env, \"env:Envelope\");");
		out.println("			elem1.setAttribute(\"xmlns:env\", env);");
		out.println("			elem1.setAttribute(\"xmlns:wsa\", wsa);");
		out.println("			elem1.setAttribute(\"xmlns:wsse\", wsse);");
		out.println("			elem1.setAttribute(\"xmlns:saml\", saml);");
		out.println("			elem1.setAttribute(\"xmlns:wsu\", wsu);");
		out.println("			elem1.setAttribute(\"xmlns:wsse11\", wsse11);");
		out.println("			elem1.setAttribute(\"xmlns:wsrm\", wsrm);");
		// CHM 20090506 --- end

		out.println("			document.appendChild(elem1);");
		out.println("			elem2 = document.createElementNS(env, \"env:Header\");");
		out.println("			elem1.appendChild(elem2);");
		out.println("			elem3 = document.createElementNS(wsa, \"wsa:MessageID\");");
		out.println("			elem3.appendChild(document.createTextNode(msgID));");
		out.println("			elem2.appendChild(elem3);");

		out.println("			String sendto = config.get(\"peer-url\");");
		out.println("			elem3 = document.createElementNS(wsa, \"wsa:To\");");
		out.println("			elem3.appendChild(document.createTextNode(sendto));");
		out.println("			elem2.appendChild(elem3);");

		out.println("			if(setRelateToID!=null) {");
		out.println("				sendto = replyto;");
		out.println("		System.out.println(\"send back to :\" + sendto);");
		out.println("		System.out.println(\"It relates to :\" + setRelateToID);");
		out.println("				elem3 = document.createElementNS(wsa, \"wsa:RelatesTo\");");
		out.println("				elem3.appendChild(document.createTextNode(setRelateToID));");
		out.println("				elem2.appendChild(elem3);");
		out.println("				setRelateToID = null;");
		out.println("			} else {");
		out.println("				elem3 = document.createElementNS(wsa, \"wsa:ReplyTo\");");
		out.println("				elem2.appendChild(elem3);");
		out.println("				elem4 = document.createElementNS(wsa, \"wsa:Address\");");
		out.println("				elem4.appendChild(document.createTextNode(config.get(\"self-url\")));");
		out.println("				elem3.appendChild(elem4);");
		out.println("			}");

		// CHM 20090506 --- begin
		out.println("			elem3 = document.createElementNS(wsse, \"wsse:Security\");");
//		out.println("			elem3.setAttribute(\"s11:mustUnderstand\", \"1\");");
		out.println("			elem2.appendChild(elem3);");
		out.println("			elem4 = document.createElementNS(saml, \"saml:Assertion\");");
		out.println("			elem3.appendChild(elem4);");
		out.println("			elem4.setAttribute(\"MajorVersion\", \"1\");");
		out.println("			elem4.setAttribute(\"MinorVersion\", \"1\");");
		out.println("			elem4 = document.createElementNS(wsse, \"wsse:SecurityTokenReference\");");
		out.println("			elem3.appendChild(elem4);");
		out.println("			elem4.setAttribute(\"wsu:Id\", \"STR1\");");
		out.println("			elem4.setAttribute(\"wsse11:TokenType\", \"http://docs.oasis-open.org/wss/oasis-wss-saml-tokenprofile-1.1#SAMLV1.1\");");
		out.println("			Element signelm = elem3;");

		out.println("			elem5 = document.createElementNS(wsse, \"wsse:KeyIdentifier\");");
		out.println("			elem4.appendChild(elem5);");
		out.println("			elem5.setAttribute(\"ValueType\", \"http://docs.oasis-open.org/wss/oasis-wss-saml-tokenprofile-1.0#SAMLAssertionID\");");
		out.println("			elem5.appendChild(document.createTextNode(\"_a75adf55-01d7-40cc-929f-dbd8372ebdfc\"));");

		out.println("			elem3 = document.createElementNS(wsrm, \"wsrm:CreateSequence\");");
		out.println("			elem2.appendChild(elem3);");
		out.println("			elem4 = document.createElementNS(wsrm, \"wsrm:AcksTo\");");
		out.println("			elem4.appendChild(document.createTextNode(config.get(\"self-url\")));");
		out.println("			elem3.appendChild(elem4);");
		out.println("			elem4 = document.createElementNS(wsrm, \"wsrm:Expires\");");
		out.println("			java.util.Date dd = new java.util.Date();");
		out.println("			elem4.appendChild(document.createTextNode(\"\"+dd));");
		out.println("			elem3.appendChild(elem4);");
		out.println("			elem4 = document.createElementNS(wsrm, \"wsrm:Offer\");");
		out.println("			elem3.appendChild(elem4);");
		out.println("			elem5 = document.createElementNS(wsrm, \"wsrm:Identifier\");");
		out.println("			elem4.appendChild(document.createTextNode(config.get(\"self-url\")));");
		out.println("			elem4.appendChild(elem5);");
		out.println("			elem5 = document.createElementNS(wsrm, \"wsrm:Endpoint\");");
		out.println("			elem4.appendChild(document.createTextNode(config.get(\"peer-url\")));");
		out.println("			elem4.appendChild(elem5);");

		out.println("			elem2 = document.createElementNS(env, \"env:Body\");");
		out.println("			elem2.setAttribute(\"ID\", \"Body\");");
		out.println("			Element encbody = elem2;");
		// CHM 20090506 --- end

		out.println("			elem1.appendChild(elem2);");
		out.println("			Transformer xformer = TransformerFactory.newInstance().newTransformer();");
		out.println("			xformer.setOutputProperty(OutputKeys.STANDALONE, \"no\");");
		out.println("			xformer.setOutputProperty(OutputKeys.ENCODING, \"UTF-8\");");

		out.println("			Document inpdoc = builder.parse(bais);");
		out.println("			xformer.transform(new DOMSource(inpdoc), new DOMResult(elem2));");

		out.println("			");

		// CHM 20090506 --- begin
		out.println("			");
		out.println("			KeyStore ks = KeyStore.getInstance(\"PKCS12\");");
		out.println("			FileInputStream keyis = new FileInputStream(\"ws1.egif.tu-rac.com.p12\");");
		out.println("			ks.load(keyis, \"ws1.egif.tu-rac.com-password\".toCharArray());");
		out.println("			String alias = \"\";");
		out.println("			Enumeration e0 = ks.aliases();");
		out.println("			while(e0.hasMoreElements()) alias = \"\" + e0.nextElement();");
		out.println("System.out.println(\"key:\"+ alias);");
		out.println("			PrivateKey prvKey = (PrivateKey) ks.getKey(alias, \"ws1.egif.tu-rac.com-password\".toCharArray());");
		out.println("			XMLSignatureFactory sigfac = XMLSignatureFactory.getInstance();");
		out.println("");
		out.println("			Reference ref = sigfac.newReference(\"#Body\"");
		out.println("				, sigfac.newDigestMethod(DigestMethod.SHA1, null));");
		out.println("			SignedInfo si = sigfac.newSignedInfo(");
		out.println("			sigfac.newCanonicalizationMethod(");
		out.println("				CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS");
		out.println("				, (C14NMethodParameterSpec) null)");
		out.println("				, sigfac.newSignatureMethod(SignatureMethod.RSA_SHA1, null)");
		out.println("				, Collections.singletonList(ref));");
		out.println("");
		out.println("			java.security.cert.Certificate[] certs = ");
		out.println("				(java.security.cert.Certificate[]) ks.getCertificateChain(alias);");
		out.println("			PublicKey pblKey = certs[0].getPublicKey();");

		out.println("			KeyInfoFactory kif = sigfac.getKeyInfoFactory();");
		out.println("			KeyValue kv = kif.newKeyValue(pblKey);");
		out.println("			KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));");
		out.println("//			KeyInfo ki = null;");
		out.println("");
		out.println("			XMLSignature sig = sigfac.newXMLSignature(si, ki);");
		out.println("");
		out.println("			DOMSignContext sigContext = new DOMSignContext(prvKey, signelm);");
		out.println("			sigContext.putNamespacePrefix(XMLSignature.XMLNS, \"ds\");");
		out.println("			sig.sign(sigContext);");
		// CHM 20090506 --- end

//		out.println("			URL wsurl = new URL(config.get(\"peer-url\"));");
		out.println("			URL wsurl = new URL(sendto);");
		out.println("			URLConnection conn = wsurl.openConnection();");
		out.println("			HttpURLConnection httpcon = (HttpURLConnection) conn;");
		out.println("			httpcon.setDoInput(true);");
		out.println("			httpcon.setDoOutput(true);");
		out.println("			httpcon.setUseCaches(false);");
		out.println("			httpcon.setRequestMethod(\"POST\");");
		out.println("			OutputStream wsreq = httpcon.getOutputStream();");
		out.println("			xformer.transform(new DOMSource(document), new StreamResult(wsreq));");

		out.println("			fos = new FileOutputStream(");
		out.println("				logxml + \"/\" + msgID+ \"-\" + soaptype + \".xml\");");
		out.println("			xformer.transform(new DOMSource(document), new StreamResult(fos));");
		out.println("			fos.close();");

		out.println("			");
		out.println("			InputStream wsres = httpcon.getInputStream();");
		out.println("			int dt;");
		out.println("			while( (dt=wsres.read())>=0) ;");

		// CHM 20090506 --- begin
		out.println("			encrypt(encbody, certs[0]);");
		out.println("System.out.println(\"signelm:\"+ signelm);");
		out.println("System.out.println(\"sigContext:\"+ sigContext);");

		out.println("			fos = new FileOutputStream(");
		out.println("				logxml + \"/\" + msgID+ \"-\" + soaptype + \"_1.xml\");");
		out.println("			xformer.transform(new DOMSource(document), new StreamResult(fos));");
		out.println("			fos.close();");

		out.println("			decrypt(encbody, prvKey);");
		out.println("			fos = new FileOutputStream(");
		out.println("				logxml + \"/\" + msgID+ \"-\" + soaptype + \"_2.xml\");");
		out.println("			xformer.transform(new DOMSource(document), new StreamResult(fos));");
		out.println("			fos.close();");

		// CHM 20090506 --- end
		
		out.println("		} catch(Exception ex) {");
		out.println("			ex.printStackTrace();");
		out.println("		}");
		out.println("		return res;");
		out.println("	}");

		// CHM 20090506 --- begin
		out.println("	public static void encrypt(Element elem, java.security.cert.Certificate cert) throws Exception {");
//		out.println("		NodeList list = document.getElementsByTagName(elem);");
//		out.println("		for(int e=0; e<list.getLength(); e++) {");
//		out.println("			org.w3c.dom.Node maine = list.item(e);");
		out.println("			Document document = elem.getOwnerDocument();");
		out.println("			org.w3c.dom.Node maine = elem;");
		out.println("			NodeList mains = maine.getChildNodes();");
		out.println("			String content = new String();");
		out.println("			for(int i=0; i<mains.getLength(); i++) {");
		out.println("				StringWriter writer = new StringWriter();");
		out.println("				Source source = new DOMSource(mains.item(i));");
		out.println("				Transformer xformer = TransformerFactory.newInstance().newTransformer();");
		out.println("				xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,\"yes\");");
		out.println("				xformer.setOutputProperty(OutputKeys.ENCODING, \"UTF-8\");");
		out.println("				xformer.transform(source, new StreamResult(writer));");
		out.println("				content += writer.toString();");
		out.println("			}");
		out.println("			while(maine.hasChildNodes()) {");
		out.println("				maine.removeChild(maine.getFirstChild());");
		out.println("			}");
		out.println("			Element xenc = document.createElement(\"EncryptedData\");");
		out.println("			xenc.setAttribute(\"xmlns\",\"http://www.w3.org/2001/04/xmlenc#\");");
		out.println("			xenc.setAttribute(\"xmlns:ds\",\"http://www.w3.org/2000/09/xmldsig#\");");
		out.println("			xenc.setAttribute(\"Type\",\"http://www.w3.org/2001/04/xmlenc#Content\");");
		out.println("			Element meth = document.createElement(\"EncryptionMethod\");");
		out.println("			meth.setAttribute(\"Algorithm\",\"http://www.w3.org/2001/04/xmlenc#tripledes-cbc\");");
		out.println("			xenc.appendChild(meth);");
		out.println("			Element keyinfo = document.createElement(\"ds:KeyInfo\");");
		out.println("			xenc.appendChild(keyinfo);");
		out.println("			Element enckey = document.createElement(\"EncryptedKey\");");
		out.println("			keyinfo.appendChild(enckey);");
		out.println("			Element keymeth = document.createElement(\"EncryptionMethod\");");
		out.println("			keymeth.setAttribute(\"Algorithm\", \"http://www.w3.org/2001/04/xmlenc#rsa-1_5\");");
		out.println("			enckey.appendChild(keymeth);");
		out.println("			Element keydata = document.createElement(\"CipherData\");");
		out.println("			enckey.appendChild(keydata);");
		out.println("			Element keyvalue = document.createElement(\"CipherValue\");");
		out.println("			keydata.appendChild(keyvalue);");
		out.println("			Element cipherdata = document.createElement(\"CipherData\");");
		out.println("			xenc.appendChild(cipherdata);");
		out.println("			Element ciphervalue = document.createElement(\"CipherValue\");");
		out.println("			cipherdata.appendChild(ciphervalue);");
		out.println("			maine.appendChild(xenc);");
		out.println("");
		out.println("			byte[] salt = {1,1,1,1,1,1,1,1};");
		out.println("			javax.crypto.spec.IvParameterSpec iv;");
		out.println("			iv = new javax.crypto.spec.IvParameterSpec(salt);");
		out.println("			KeyGenerator keygen = KeyGenerator.getInstance(\"DESede\");");
		out.println("			SecretKey desedeKey = keygen.generateKey();");
		out.println("			Cipher desedeCipher = Cipher.getInstance(\"DESede/CBC/PKCS5Padding\");");
		out.println("			desedeCipher.init(Cipher.ENCRYPT_MODE, desedeKey, iv);");
		out.println("			byte[] ciphertext = desedeCipher.doFinal(content.getBytes(\"UTF-8\"));");
		out.println("");
		out.println("			ByteArrayOutputStream baos = new ByteArrayOutputStream();");
		out.println("			baos.write(salt, 0, salt.length);");
		out.println("			baos.write(ciphertext, 0, ciphertext.length);");
		out.println("			ciphertext = baos.toByteArray();");
		out.println("");
//		out.println("			String cipherstring = new BASE64Encoder().encode(ciphertext);");
		out.println("			String cipherstring = new String(ecms.libs.StringUtility.Base64Coder.encode(ciphertext));");

		out.println("");
		out.println("			byte[] enckeyb = desedeKey.getEncoded();");
//		out.println("			String keywrap = new BASE64Encoder().encode(enckeyb);");
		out.println("			String keywrap = new String(ecms.libs.StringUtility.Base64Coder.encode(enckeyb));");
		out.println("");
		out.println("			Cipher rsaCipher = Cipher.getInstance(\"RSA/ECB/PKCS1Padding\");");
		out.println("			rsaCipher.init(Cipher.ENCRYPT_MODE, cert.getPublicKey());");
		out.println("			byte[] keycipher = rsaCipher.doFinal(enckeyb);");
//		out.println("			String key1 = new BASE64Encoder().encode(keycipher);");
		out.println("			String key1 = new String(ecms.libs.StringUtility.Base64Coder.encode(keycipher));");
		out.println("");
		out.println("			keyvalue.appendChild(document.createTextNode(key1));");
		out.println("			ciphervalue.appendChild(document.createTextNode(cipherstring));");
//		out.println("		}");
		out.println("	}");
		out.println("");

		out.println("	public static void decrypt(Element ebody, PrivateKey privateKey) throws Exception {");

		out.println("		Document document = ebody.getOwnerDocument();");
		out.println("		Element encnode = (Element) ebody.getFirstChild();");
		out.println("		Element decnode = (Element) ebody;");
		out.println("		String tagName = decnode.getTagName();");
		out.println("		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(\"DESede\");");
		out.println("");
		out.println("		NodeList values = decnode.getElementsByTagName(\"CipherValue\");");
		out.println("		String val1 = ((org.w3c.dom.Text)(values.item(0).getFirstChild())).getWholeText();");
		out.println("		String val2 = ((org.w3c.dom.Text)(values.item(1).getFirstChild())).getWholeText();");
		out.println("");
		out.println("		Cipher rsaCipher = Cipher.getInstance(\"RSA/ECB/PKCS1Padding\");");
		out.println("		rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);");
//		out.println("		byte[] keyclear = rsaCipher.doFinal(new BASE64Decoder().decodeBuffer(val1));");
		out.println("		byte[] keyclear = rsaCipher.doFinal(ecms.libs.StringUtility.Base64Coder.decode(val1));");

		out.println("		DESedeKeySpec keyspec = new DESedeKeySpec(keyclear);");
		out.println("		SecretKey desedeKey2 = keyfactory.generateSecret(keyspec);");
		out.println("");

		out.println("		Cipher desedeCipher = Cipher.getInstance(\"DESede/CBC/PKCS5Padding\");");
//		out.println("		byte[] cipher = new BASE64Decoder().decodeBuffer(val2);");
		out.println("		byte[] cipher = ecms.libs.StringUtility.Base64Coder.decode(val2);");

		out.println("		byte[] salt = new byte[8];");
		out.println("		for(int x=0; x<8; x++) salt[x] = cipher[x];");
		out.println("		ByteArrayOutputStream baos = new ByteArrayOutputStream();");
		out.println("		baos.write(cipher, 8, cipher.length-8);");
		out.println("		cipher = baos.toByteArray();");
		out.println("");
		out.println("		javax.crypto.spec.IvParameterSpec iv = new javax.crypto.spec.IvParameterSpec(salt);");
		out.println("		desedeCipher.init(Cipher.DECRYPT_MODE, desedeKey2, iv);");
		out.println("		byte[] cleartext1 = desedeCipher.doFinal(cipher);");
		out.println("		String content = new String(cleartext1,\"UTF-8\");");
		out.println("");
		out.println("		InputStream is = new ByteArrayInputStream(content.getBytes(\"UTF-8\"));");
		out.println("		StreamSource ss = new StreamSource(is);");

		out.println("		DOMResult dr = new DOMResult(ebody);");
		out.println("		Transformer xform = TransformerFactory.newInstance().newTransformer();");
		out.println("		xform.transform(ss, dr);");
		out.println("		ebody.removeChild(ebody.getFirstChild());");
		out.println("	}");
		// CHM 20090506 --- end

		out.println("	");
		out.println("	public Vector<Hashtable<String,Object>> history(String id) throws RemoteException {");
		out.println("		return worklog;");
		out.println("	}");
		out.println("	");
		out.println("	public Object putObject(String id, String name, Object obj) throws RemoteException {");
		out.println("		if(\"request-data\".equals(id)) {");
		out.println("			try {");
		out.println("				String[] reqpr = (String[]) obj;");

		out.println("				String dec = null, dd;");
		out.println("				int ix ,iy;");
		out.println("				for(int i=0; i<reqpr.length; i++) {");
		out.println("					if((dec=config.get(\"reqField\"+i))!=null) {");
		out.println("						if(dec.startsWith(\"${req\") && (ix=dec.indexOf(\"}\"))>4) {");
		out.println("							try {");
		out.println("								int id0 = Integer.parseInt(dec.substring(5,ix));");
		out.println("								reqpr[i] = reqpr[id0];");
		out.println("							} catch(Exception x5) {");
		out.println("							}");
		out.println("						}");
		out.println("						if(dec.startsWith(\"select \")) {");
		out.println("							try {");
		out.println("								while( (ix=dec.indexOf(\"${req\"))>0 && (iy=dec.indexOf(\"}\"))>0) {");
		out.println("									int id0 = Integer.parseInt(dec.substring(ix+5,iy));");
		out.println("									dd = reqpr[id0];");
		out.println("									dec = dec.substring(0,ix) + dd + dec.substring(iy+1);");
		out.println("								}");
		out.println("								reqpr[i] = dec;");

//		out.println("								Class.forName(\"org.hsqldb.jdbcDriver\" );");
		out.println("								Class.forName(config.get(\"jdbc-drv\"));");
		out.println("								Connection conn = DriverManager.getConnection("
				+			"								config.get(\"jdbc-url\"), config.get(\"jdbc-usr\"), config.get(\"jdbc-pw\"));");
		out.println("								try {");
		out.println("									Statement st = conn.createStatement();");
		out.println("									ResultSet rs = st.executeQuery(dec);");
		out.println("									if(rs.next()) {");
		out.println("										reqpr[i] = rs.getString(1);");
		out.println("										String charset = config.get(\"char-set\");");
		out.println("										if(charset!=null && charset.length()>0)");
		out.println("											res_p[i] = new String(res_p[i].getBytes(), charset);");
		out.println("									} else {");
		out.println("										reqpr[i] = \"no data found\";");
		out.println("									}");
		out.println("								} catch(Exception ex2) {");
		out.println("									reqpr[i] = null;");
		out.println("								}");
		out.println("								conn.close();");

		out.println("							} catch(Exception x5) {}");
		out.println("						}");
		out.println("					}");
		out.println("				}");

		out.println("				return reqpr;");
		out.println("			} catch(Exception ex) {");
		out.println("				return new String[] {\"request para error:\" + ex};");
		out.println("			}");
		out.println("		}");
		out.println("		return null;");
		out.println("	}");
		out.println("	");
		out.println("	public Object getObject(String id, String name) throws RemoteException {");
		out.println("		byte[] xdmy = new byte[0];");

		out.println("		if(\"sql-query\".equals(id)) {");
		out.println("			Vector<String[]> rsset = null;");
		out.println("			try {");
//		out.println("				Class.forName(\"org.hsqldb.jdbcDriver\" );");
		out.println("				Class.forName(config.get(\"jdbc-drv\"));");
		out.println("				Connection conn = DriverManager.getConnection("
					  + "					config.get(\"jdbc-url\"), config.get(\"jdbc-usr\"), config.get(\"jdbc-pw\"));");
		out.println("				System.out.println(\"query:\" + name);");
		out.println("				Statement st = conn.createStatement();");
		out.println("				ResultSet rs = st.executeQuery(name);");
		out.println("				java.sql.ResultSetMetaData meta = rs.getMetaData();");
		out.println("				int colCnt = meta.getColumnCount();");
		out.println("				if(colCnt>0) {");
		out.println("					rsset = new Vector<String[]>();");
		out.println("					String[] colnms = new String[colCnt];");
		out.println("					for(int i=0; i<colCnt; i++) {");
		out.println("						colnms[i] = meta.getColumnName(i+1);");
		out.println("					}");
		out.println("					rsset.add(colnms);");
		out.println("					for(int r=0; r<20 && rs.next(); r++) {");
		out.println("						String[] datas = new String[colCnt];");
		out.println("						for(int c=0; c<colCnt; c++) {");
		out.println("							datas[c] = rs.getString(c+1);");
		out.println("							String charset = config.get(\"char-set\");");
		out.println("							if(charset!=null && charset.length()>0)");
		out.println("								datas[c] = new String(datas[c].getBytes(), charset);");
		out.println("						}");
		out.println("						rsset.add(datas);");
		out.println("					}");
		out.println("				}");
		out.println("				conn.close();");
		out.println("			} catch(Exception ex) {");
		out.println("				System.out.println(\"error:\" + ex);");
		out.println("			}");
		out.println("			return rsset;");
		out.println("		}");

		out.println("		if(\"sql-update\".equals(id)) {");
		out.println("			try {");
		out.println("				Class.forName(config.get(\"jdbc-drv\"));");
//		out.println("				Class.forName(\"org.hsqldb.jdbcDriver\" );");
		out.println("				Connection conn = DriverManager.getConnection("
					  + "					config.get(\"jdbc-url\"), config.get(\"jdbc-usr\"), config.get(\"jdbc-pw\"));");
		out.println("				System.out.println(\"update:\" + name);");
		out.println("				Statement st = conn.createStatement();");
		out.println("				int ud = st.executeUpdate(name);");
		out.println("				conn.close();");
		out.println("				return \"updated:\" + ud;");
		out.println("			} catch(Exception ex) {");
		out.println("				System.out.println(\"error:\" + ex);");
		out.println("				return \"update error:\" + ex;");
		out.println("			}");
		out.println("		}");

		out.println("		if(\"reply-data\".equals(id)) {");
		out.println("			String[] res_p = new String[res_a.length];");
		out.println("			System.out.println(\"===========  reply:\" + name);");
		out.println("			try {");
		out.println("				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();");
		out.println("				factory.setNamespaceAware(true);");
		out.println("				DocumentBuilder builder = factory.newDocumentBuilder();");
		out.println("				String nd; Object opath; Element elm;");
		out.println("				XPath xpath = XPathFactory.newInstance().newXPath();");
		out.println("				String rsm = \"urn:th:gov:egif:data:standard:"+ rsmn.get(rsmi.get(inpid)) + ":1.0\";");
		out.println("				String ram = \"urn:th:gov:egif:data:standard:ReusableAggregateBusinessInformationEntity:1\";");
		out.println("				XPathParser qParse = new XPathParser();");
		out.println("				qParse.addPrefixNS(\"rsm\", rsm);");
		out.println("				qParse.addPrefixNS(\"ram\", ram);");
		out.println("				xpath.setNamespaceContext(qParse);");
		out.println("				Document oldreq = builder.parse(\"log" + name + "/\"+ name +\".xml\");");

		out.println("				String dec = null, dd;");
		out.println("				int ix ,iy;");
		out.println("				for(int i=0; i<res_a.length; i++) {");
		out.println("					if((dec=config.get(\"resField\"+i))!=null) {");
		out.println("						if(dec.startsWith(\"${req\") && (ix=dec.indexOf(\"}\"))>4) {");
		out.println("							try {");
		out.println("								int id0 = Integer.parseInt(dec.substring(5,ix));");
		out.println("								opath = xpath.evaluate(req_a[id0][4], oldreq, XPathConstants.NODESET);");
		out.println("								elm = (Element)((NodeList)opath).item(0);");
		out.println("								res_p[i] = elm.getFirstChild().getNodeValue();");
		out.println("							} catch(Exception x5) {");
		out.println("							}");
		out.println("						}");
		out.println("						if(dec.startsWith(\"select \")) {");
		out.println("							try {");
		out.println("								while( (ix=dec.indexOf(\"${req\"))>0 && (iy=dec.indexOf(\"}\"))>0) {");
		out.println("									int id0 = Integer.parseInt(dec.substring(ix+5,iy));");
		out.println("									opath = xpath.evaluate(req_a[id0][4], oldreq, XPathConstants.NODESET);");
		out.println("									elm = (Element)((NodeList)opath).item(0);");
		out.println("									dd = elm.getFirstChild().getNodeValue();");
		out.println("									dec = dec.substring(0,ix) + dd + dec.substring(iy+1);");
		out.println("								}");
		out.println("								res_p[i] = dec;");
		out.println("								Connection conn = DriverManager.getConnection("
				+			"								config.get(\"jdbc-url\"), config.get(\"jdbc-usr\"), config.get(\"jdbc-pw\"));");
		out.println("								try {");
		out.println("									Statement st = conn.createStatement();");
		out.println("									ResultSet rs = st.executeQuery(dec);");
		out.println("									if(rs.next()) {");
		out.println("										res_p[i] = rs.getString(1);");
		out.println("										String charset = config.get(\"char-set\");");
		out.println("										if(charset!=null && charset.length()>0)");
		out.println("											res_p[i] = new String(res_p[i].getBytes(), charset);");
		out.println("									} else {");
		out.println("										res_p[i] = \"no data found\";");
		out.println("									}");
		out.println("								} catch(Exception ex2) {");
		out.println("								}");
		out.println("								conn.close();");
		out.println("							} catch(Exception x5) {}");
		out.println("						}");
		out.println("					}");
		out.println("				}");

		out.println("			} catch(Exception ex) {");
		out.println("				res_p[0] = \"reply error:\" + ex;");
		out.println("			}");
		out.println("			return res_p;");
		out.println("		}");

		out.println("		if(\"test-db\".equals(id)) {");
		out.println("			try {");
		out.println("				Class.forName(config.get(\"jdbc-drv\"));");
//		out.println("				Class.forName(\"org.hsqldb.jdbcDriver\" );");
		out.println("				Connection conn = DriverManager.getConnection("
					  + "					config.get(\"jdbc-url\"), config.get(\"jdbc-usr\"), config.get(\"jdbc-pw\"));");
		out.println("				System.out.println(\"conn:\" + conn);");
		out.println("				conn.close();");
		out.println("				return \"db OK\";");
		out.println("			} catch(Exception ex) {");
		out.println("				System.out.println(\"error:\" + ex);");
		out.println("				return \"db ERROR:\" + ex;");
		out.println("			}");
		out.println("		}");

		out.println("		if(\"setRelateToID\".equals(id)) {");
		out.println("			setRelateToID = name;");
		out.println("			return xdmy;");
		out.println("		}");
		out.println("		if(id.startsWith(\"set-\")) {");
		out.println("			config.put(id.substring(4), name);");
		out.println("			return xdmy;");
		out.println("		}");
		out.println("		if(id.startsWith(\"save-config\")) {");
		out.println("			try {");
		out.println("			FileOutputStream fos = new FileOutputStream(\"" + name + ".cfg\");");
		out.println("			ObjectOutputStream oos = new ObjectOutputStream(fos);");
		out.println("			oos.writeObject(config);");
		out.println("			oos.close();");
		out.println("			} catch(Exception x1) {");
		out.println("			}");
		out.println("			return xdmy;");
		out.println("		}");
		out.println("		if(\"config\".equals(id)) {");
		out.println("			try {");
		out.println("				ByteArrayOutputStream baos = new ByteArrayOutputStream();");
		out.println("				ObjectOutputStream oos = new ObjectOutputStream(baos);");
		out.println("				oos.writeObject(config);");
		out.println("				oos.flush();");
		out.println("				byte[] cfg = baos.toByteArray();");
		out.println("				return cfg;");
		out.println("			} catch(Exception x1) {}");
		out.println("		}");
		out.println("		byte[] res = new byte[0];");
		out.println("		if(id.length()>0) {");
		out.println("			File xmlfile = new File(\"log" + name + "/\"+ id + \"-\" + name +\".xml\");");
		out.println("			if(xmlfile.exists()) {");
		out.println("				try {");
		out.println("					res = new byte[(int) xmlfile.length()];");
		out.println("					FileInputStream fis = new FileInputStream(xmlfile);");
		out.println("					fis.read(res, 0, res.length);");
		out.println("					fis.close();");
		out.println("				} catch(Exception x) { }");
		out.println("				System.out.println(\"exist:\"+xmlfile+\"  len:\"+ res.length);");
		out.println("			} else {");
		out.println("				System.out.println(\"no exist:\"+xmlfile);");
		out.println("			}");
		out.println("		}");
		out.println("		return res;");
		out.println("	}");
		out.println("	");

		out.println("	public void run() {");
		out.println("		try {");
		out.println("			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));");
		out.println("			String line;");
		out.println("			while( (line=br.readLine())!=null) {");
		out.println("				if(\"\".equals(line)) {");
		out.println("					System.out.println(\"=== show status (\"  + worklog.size() + \")\");");
		out.println("					for(int i=0; i<worklog.size(); i++) {");
		out.println("						System.out.println(\"\\t\" + (i+1) +\":\"");
		out.println("							+ worklog.get(i).get(\"ID\")");
		out.println("								+ \", \" + worklog.get(i).get(\"TYPE\")");
		out.println("								+ \", \" + worklog.get(i).get(\"TIME\")");
		out.println("								+ \", \" + worklog.get(i).get(\"RELID\")");
		out.println("								+ \", \" + worklog.get(i).get(\"STATUS\")");
		out.println("								);");
		out.println("					}");
		out.println("				} else {");
		out.println("					System.out.println(\"!!! not found:\"+ line);");
		out.println("				}");
		out.println("			}");
		out.println("		} catch (Exception ex) {");
		out.println("			ex.printStackTrace();");
		out.println("		}");
		out.println("	}");
		out.println("	");
		out.println("	public static void main(String[] args) {");
		out.println("		try {");
		out.println("			int port = " + port +";");
		out.println("			for(int i=0; i<args.length; i++) {");
		out.println("				if(\"-port\".equals(args[i])) {");
		out.println("					i++;");
		out.println("					port = Integer.parseInt(args[i]);");
		out.println("				}");
		out.println("			}");
		out.println("			" + name + "Rmi rmiService = new " + name + "Rmi("+ port +");");
		out.println("			Registry rmiRegistry =  LocateRegistry.createRegistry("+ port +");");
		out.println("			rmiRegistry.bind(\"" + name + "Rmi\", rmiService);");
		out.println("			new Thread(rmiService).start();");
		out.println("		} catch (Exception ex) {");
		out.println("			ex.printStackTrace();");
		out.println("		}");
		out.println("	}");
		out.println("}");
		out.close();
	}

	public String[] getLineById(Vector<String[]> linev, String id) {
		for(int i=0; i<linev.size(); i++) {
			if(id.equals(linev.get(i)[0])) return linev.get(i);
		}
		return null;
	}

	public String getTagName(String fnm) {
		String[] fnms = fnm.split("_");
		String tagName = "";
		for(int i=0; i<fnms.length; i++) {
			tagName += "/" + (i>1? "ram:" : "rsm:") + fnms[i];
		}
		return tagName;
	}

	String thaiTagName(String ptq, String pt) {
		if(ptq==null || ptq.length()==0) return pt;
		return pt + "(" + ptq + ")";
	}

	public void getBIEInfo(String pref, String abie, Vector<String[]> infov) {
		Vector<String[]> det = bied.get(abie);
		if(det==null) {
			return;
		}
		for(int i=0; i<det.size(); i++) {
			String[] elems = det.get(i);
			String[] wds = getLineById(bie, elems[3]);
			String fnm = (pref.length()==0)? elems[0] : pref + "_" + elems[0];
			if(elems[1]==null || elems[1].length()==0) {
				infov.add(new String[] {pref, elems[0], thaiTagName(wds[6], wds[8]), wds[10]
					, getTagName(fnm), fnm});
			} else {
				infov.add(new String[] {pref + "_" + abie, elems[0], thaiTagName(wds[6], wds[8])
					, "ABIE", getTagName(fnm), fnm});
				getBIEInfo(pref + "_" + elems[0], elems[1], infov);
			}
		}
	}

	// 0: prefix by _, 1: node name, 2: thai name, 3: type
	public Vector<String[]> getMsgInfo(String rsmid) throws Exception {
		Vector<String[]> rs = new Vector<String[]>();
		int rsmord = rsmi.get(rsmid);
		String name = rsmn.get(rsmord);
		String[] msg = getLineById(rsm, rsmid);
		rs.add(new String[] {"", name, thaiTagName(msg[2],msg[4]), "ABIE", "/rsm:" + name, name});
		Vector<String[]> memv = rsmd.get(rsmord);
		for(int j=0; j<memv.size(); j++) {
			String[] elems = memv.get(j);
			String[] wds = getLineById(rsm, elems[3]);
			String fnm = (name.length()==0)? elems[0] : name + "_" + elems[0];
			if(elems[1]==null || elems[1].length()==0) {
				rs.add(new String[] {name, elems[0], thaiTagName(wds[6], wds[8]), wds[10]
					, getTagName(fnm), fnm});
			} else {
				rs.add(new String[] {name, elems[0], thaiTagName(wds[6], wds[8]), "ABIE"
					, getTagName(fnm), fnm});
				getBIEInfo(name+"_"+elems[0], elems[1], rs);
			}
		}
		return rs;
	}

	int calcMaxIndent(Vector<String[]> msgv) {
		int dp = 0;
		for(int i=0; i<msgv.size(); i++) {
			String[] words = msgv.get(i);
			String[] pref = words[0].split("_");
			if(pref.length>dp) dp = pref.length;
		}
		return dp;
	}

	public void wsServletGen(String dir, String name, String rsmid, String to, String inpid) 
		throws Exception {

		FileOutputStream fos = new FileOutputStream(dir+"/"+name+".java");
		PrintWriter out = new PrintWriter(fos);
		int rsmord = rsmi.get(rsmid);

		out.println("package com.dabos.ws;");
		out.println("");
		out.println("import java.util.*;");
		out.println("import java.io.*;");
		out.println("import javax.servlet.http.*;");
		out.println("import javax.servlet.*;");
		out.println("import java.rmi.*;");
		out.println("import com.dabos.engine.lib.*;");
		out.println("");
		out.println("public class " + name +" extends HttpServlet {");
		out.println("");
		out.println("	public void doGet(HttpServletRequest request, HttpServletResponse response)");
		out.println("		throws IOException, ServletException {");
		out.println("		try {");
		out.println("			int d;");
		out.println("");
		out.println("			InputStream is = request.getInputStream();");
		out.println("			ByteArrayOutputStream baos = new ByteArrayOutputStream();");
		out.println("			while((d=is.read())>=0) { baos.write(d); }");
		out.println("			byte[] inpdata = baos.toByteArray();");
		out.println("			");

		out.println("			ServletContext context = getServletContext();");
		out.println("			String rmiurl = context.getInitParameter(\"rmiurl\");");

		out.println("			WebServiceRmi rmi = (WebServiceRmi) Naming.lookup(rmiurl);");
		out.println("			byte[] outdata = rmi.doWebService(inpdata );");

		out.println("			response.setHeader(\"Cache-Control\", \"no-cache\");");
		out.println("			response.setDateHeader(\"Expires\",0);");
		out.println("			response.setHeader(\"Cache-control\", \"must-revalidate\"); ");
		out.println("			response.setContentType (\"text/xml\");");
		out.println("			ServletOutputStream out = response.getOutputStream();");
		out.println("			out.write(outdata, 0, outdata.length);");
		out.println("		} catch(Exception e) {");
		out.println("			response.setContentType(\"text/html\");");
		out.println("			PrintWriter out = response.getWriter();");
		out.println("			out.println(\"error:\" + e);");
		out.println("		}");
		out.println("	}");
		out.println("");
		out.println("	public void doPost(HttpServletRequest request, HttpServletResponse response)");
		out.println("		throws IOException, ServletException {");
		out.println("		doGet(request, response);");
		out.println("	}");
		out.println("");
		out.println("}");
		out.close();

		fos = new FileOutputStream(dir+"/LoadXML.java");
		out = new PrintWriter(fos);
		out.println("package com.dabos.ws;");
		out.println("");
		out.println("import java.util.*;");
		out.println("import java.io.*;");
		out.println("import javax.servlet.http.*;");
		out.println("import javax.servlet.*;");
		out.println("import java.rmi.*;");
		out.println("import com.dabos.engine.lib.*;");
		out.println("");
		out.println("public class LoadXML extends HttpServlet {");
		out.println("	public void doGet(HttpServletRequest request, HttpServletResponse response)");
		out.println("		throws IOException, ServletException {");
		out.println("		try {");
		out.println("			String id = request.getParameter(\"id\");");
		out.println("			String tp = request.getParameter(\"tp\");");
		out.println("			");
		out.println("			ServletContext context = getServletContext();");
		out.println("			String rmiurl = context.getInitParameter(\"rmiurl\");");
		out.println("			WebServiceRmi rmi = (WebServiceRmi) Naming.lookup(rmiurl);");

		out.println("			byte[] outdata = (byte[]) rmi.getObject(id, tp);");
//		out.println("			response.setContentType(\"text/html\");");
//		out.println("			PrintWriter out = response.getWriter();");
//		out.println("			out.println(\"out:\"+outdata.length);");

		out.println("			response.setContentType(\"text/xml\");");
		out.println("			OutputStream out = response.getOutputStream();");
		out.println("			out.write(outdata, 0 , outdata.length);");

		out.println("			out.flush();");
		out.println("		} catch(Exception ex) {");
		out.println("			response.setContentType(\"text/html\");");
		out.println("			PrintWriter out = response.getWriter();");
		out.println("			out.println(\"Error:\"+ ex);");
		out.println("		}");
		out.println("	}");
		out.println("}");
		out.close();
	}

	public void pageJspGen(String dir, String name, String rsmid, String to
		, String resid, boolean bReq) 
		throws Exception {

		// ====================== application.jsp =======================
		PrintWriter out = new PrintWriter(dir+"/application.jsp", "UTF-8");

		// 0: prefix by _, 1: node name, 2: thai name, 3: type, 4:tag
		Vector<String[]> reqmsg = getMsgInfo(rsmid);
		int reqdp = calcMaxIndent(reqmsg);
		String reqmsgnm = rsmn.get(rsmi.get(rsmid));

		Vector<String[]> resmsg = getMsgInfo(resid);
		int resdp = calcMaxIndent(resmsg);
		String resmsgnm = rsmn.get(rsmi.get(resid));

		out.println("<%@ page contentType='text/html; charset=UTF-8'%>");
		out.println("<%@ page import='java.io.*, java.util.*, javax.xml.parsers.*'%>");
		out.println("<%@ page import='org.w3c.dom.*, javax.xml.transform.*'%>");
		out.println("<%@ page import='javax.xml.transform.dom.*, javax.xml.transform.stream.*'%>");
		out.println("<%@ page import='com.dabos.engine.lib.*, java.rmi.*, java.sql.*'%>");
		out.println("<%@ page import='javax.xml.xpath.*, org.xml.sax.*,javax.xml.parsers.*'%>");
		out.println("");
		out.println("<% request.setCharacterEncoding(\"UTF-8\"); %>");
		out.println("");

		out.println("<html><head>");
		out.println("<%");
		out.println("String msg = null;");
		out.println("");

		out.println("ServletContext context = getServletContext();");
		out.println("String rmiurl = context.getInitParameter(\"rmiurl\");");

		out.println("Vector<Hashtable<String,Object>> hist = null;");
		out.println("WebServiceRmi rmi = null;");
		out.println("Hashtable<String,String> xconfig = null;");
		out.println("try {");
		out.println("	rmi = (WebServiceRmi) Naming.lookup(rmiurl);");
		out.println("	hist = rmi.history(null);");
		out.println("	byte[] cfg = (byte[]) rmi.getObject(\"config\", \"read\");");
		out.println("	ByteArrayInputStream bais = new ByteArrayInputStream(cfg);");
		out.println("	ObjectInputStream ois = new ObjectInputStream(bais);");
		out.println("	xconfig = (Hashtable<String,String>) ois.readObject();");
		out.println("} catch(Exception ex3) { }");

		out.println("String messageID = request.getParameter(\"messageID\");");
		out.println("if(messageID==null) messageID = \"\";");

		out.print("String[] req_p = {");
		for(int i=0; i<reqmsg.size(); i++) {
			String[] words = reqmsg.get(i);
			if(i%20==0) { out.println(""); out.print("	"); }
			if(i>0) out.print(", ");
			out.print("\"\"");
		}
		out.println("};");
		out.print("String[] res_p = {");
		for(int i=0; i<resmsg.size(); i++) {
			String[] words = resmsg.get(i);
			if(i%20==0) { out.println(""); out.print("	"); }
			if(i>0) out.print(", ");
			out.print("\"\"");
		}
		out.println("};");

		out.println("String[][] req_a = {");
		for(int i=0; i<reqmsg.size(); i++) {
			if(i>0) out.print(",");
			out.print("	{");
			String[] words = reqmsg.get(i);
			out.print("\"" + i + "\"");
			for(int c=1; c<(words.length-1); c++) {
				out.print(", \"" + words[c] + "\"");
			}
			out.println("}");
		}
		out.println("};");
		out.println("String[][] res_a = {");
		for(int i=0; i<resmsg.size(); i++) {
			if(i>0) out.print(",");
			out.print("	{");
			String[] words = resmsg.get(i);
			out.print("\"" + i + "\"");
			for(int c=1; c<(words.length-1); c++) {
				out.print(", \"" + words[c] + "\"");
			}
			out.println("}");
		}
		out.println("};");

		out.println("if(rmi!=null) {");
		out.println("try {");
		out.println("	XPathParser qParse = new XPathParser();");
		out.println("	String env = \"http://www.w3.org/2003/05/soap-envelope\";");
		out.println("	String wsa = \"http://www.w3.org/2005/08/addressing\";");
		out.println("	String rsm = \"urn:th:gov:egif:data:standard:"+ reqmsgnm + ":1.0\";");
		out.println("	String ram = \"urn:th:gov:egif:data:standard:ReusableAggregateBusinessInformationEntity:1\";");
		out.println("	qParse.addPrefixNS(\"env\", env);");
		out.println("	qParse.addPrefixNS(\"wsa\", wsa);");
		out.println("	qParse.addPrefixNS(\"rsm\", rsm);");
		out.println("	qParse.addPrefixNS(\"ram\", ram);");

		out.println("	XPathParser qParse2 = new XPathParser();");
		out.println("	rsm = \"urn:th:gov:egif:data:standard:"+ resmsgnm + ":1.0\";");
		out.println("	qParse2.addPrefixNS(\"env\", env);");
		out.println("	qParse2.addPrefixNS(\"wsa\", wsa);");
		out.println("	qParse2.addPrefixNS(\"rsm\", rsm);");
		out.println("	qParse2.addPrefixNS(\"ram\", ram);");
		out.println("	");

		out.println("	for(int i=0; i<req_p.length; i++) {");
		out.println("		req_p[i] = request.getParameter(\"reqField\" + i);");
		out.println("		if(req_p[i]==null) req_p[i] = \"\";");
/*
		out.println("		String fld = null;");
		out.println("		if((fld=xconfig.get(\"reqField\"+i))!=null && fld.length()>0) {");
		out.println("			req_p[i] = fld;");
		out.println("		}");
*/
		out.println("	}");

		out.println("	String[] reqpa = (String[]) rmi.putObject(\"request-data\", null, req_p);");

		out.println("	for(int i=0; i<req_p.length; i++) {");
		out.println("		if(i<reqpa.length && reqpa[i]!=null && reqpa[i].length()>0) {");
		out.println("			req_p[i] = reqpa[i];");
		out.println("		}");
		out.println("	}");

		out.println("	for(int i=0; i<res_p.length; i++) {");
		out.println("		res_p[i] = request.getParameter(\"resField\" + i);");
		out.println("		if(res_p[i]==null) res_p[i] = \"\";");
		out.println("	}");

		out.println("");
		out.println("byte[] reqdoc = new byte[0];");
		out.println("byte[] resdoc = new byte[0];");

		// request process begin ==========================
		out.println("");
		out.println("if(rmi!=null && request.getParameter(\"REQBT\")!=null) {");
		out.println("	messageID= \"\";");
		out.println("	try {");
		out.println("		String rsmns = \"urn:th:gov:egif:data:standard:"+reqmsgnm+":1.0\";");
		out.println("		String ramns = \"urn:th:gov:egif:data:standard:ReusableAggregateBusinessInformationEntity:1\";");
		out.println("		");
		out.println("		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();");
		out.println("		factory.setNamespaceAware(true);");
		out.println("		DocumentBuilder builder = factory.newDocumentBuilder();");
 		out.print("		Element ");
		for(int i=0; i<=(reqdp+1); i++) if(i==0) out.print("elem0"); else out.print(", elem" + i);
		out.println(";");
		out.println("		");

		out.println("		Document document2 = builder.newDocument();");
		out.println("		Element[] elems = new Element["+(reqdp+2)+"];");
		out.println("		for(int i=0; i<req_a.length; i++) {");
		out.println("			String[] prefs = req_a[i][4].split(\"/\");");
		out.println("			int lv = prefs.length - 1;");
		out.println("			String ns = (lv>2)? ramns : rsmns;");
		out.println("			String pf = (lv>2)? \"ram:\" : \"rsm:\";");
		out.println("			elems[lv] = document2.createElementNS(ns, pf+req_a[i][1]);");
		out.println("			if(i==0) {");
		out.println("				elems[lv].setAttribute(\"xmlns:ram\", ramns);");
		out.println("				elems[lv].setAttribute(\"xmlns:rsm\", rsmns);");
		out.println("				document2.appendChild(elems[lv]);");
		out.println("			} else {");
		out.println("				elems[lv-1].appendChild(elems[lv]);");
		out.println("			}");
		out.println("			if(!\"ABIE\".equals(req_a[i][3])) {");
		out.println("				elems[lv].appendChild(document2.createTextNode(req_p[i]));");
		out.println("			}");
		out.println("		}");
		out.println("		Transformer xformer = TransformerFactory.newInstance().newTransformer();");
		out.println("		xformer.setOutputProperty(OutputKeys.STANDALONE, \"no\");");
		out.println("		xformer.setOutputProperty(OutputKeys.ENCODING, \"UTF-8\");");
		out.println("		ByteArrayOutputStream baos = new ByteArrayOutputStream();");
		out.println("		xformer.transform(new DOMSource(document2), new StreamResult(baos));");
		out.println("		reqdoc = baos.toByteArray();");
		out.println("		");
		out.println("		resdoc = rmi.doWSCall(reqdoc);");
		out.println("		hist = rmi.history(null);");
		out.println("	} catch(Exception ex) {");
		out.println("		msg = \"error:\" + ex;");
		out.println("	}");
							// request process end ==========================
		out.println("} else if(rmi!=null && request.getParameter(\"PROC1\")!=null && messageID.length()>0) {");
		out.println("	String[] paras = (String[]) rmi.getObject(\"reply-data\", messageID + \"-REQ\");");
		out.println("	msg = \"reply:\" + paras.length;");
		out.println("	for(int i=0; i<paras.length; i++) {");
		out.println("		if(paras[i]!=null) ");
		out.println("			res_p[i] = paras[i];");
//		out.println("		msg += \"[\" + i +\":\"+ paras[i] + \"]\";");
		out.println("	}");
		out.println("} else if(rmi!=null && request.getParameter(\"RESBT\")!=null && messageID.length()>0) {");
		out.println("	");
		out.println("	try {");
		out.println("		String rsmns = \"urn:th:gov:egif:data:standard:"+resmsgnm+":1.0\";");
		out.println("		String ramns = \"urn:th:gov:egif:data:standard:ReusableAggregateBusinessInformationEntity:1\";");
		out.println("		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();");
		out.println("		factory.setNamespaceAware(true);");
		out.println("		DocumentBuilder builder = factory.newDocumentBuilder();");
		out.print   ("		Element ");
		for(int i=0; i<=(resdp+1); i++) if(i==0) out.print("elem0"); else out.print(", elem" + i);
		out.println(";");
		out.println("");

		out.println("		Document document2 = builder.newDocument();");
		out.println("		Element[] elems = new Element["+(resdp+2)+"];");
		out.println("		for(int i=0; i<res_a.length; i++) {");
		out.println("			String[] prefs = res_a[i][4].split(\"/\");");
		out.println("			int lv = prefs.length - 1;");
		out.println("			String ns = (lv>2)? ramns : rsmns;");
		out.println("			String pf = (lv>2)? \"ram:\" : \"rsm:\";");
		out.println("			elems[lv] = document2.createElementNS(ns, pf+res_a[i][1]);");
		out.println("			if(i==0) {");
		out.println("				elems[lv].setAttribute(\"xmlns:ram\", ramns);");
		out.println("				elems[lv].setAttribute(\"xmlns:rsm\", rsmns);");
		out.println("				document2.appendChild(elems[lv]);");
		out.println("			} else {");
		out.println("				elems[lv-1].appendChild(elems[lv]);");
		out.println("			}");
		out.println("			if(!\"ABIE\".equals(res_a[i][3])) {");
		out.println("				elems[lv].appendChild(document2.createTextNode(res_p[i]));");
		out.println("			}");
		out.println("		");
		out.println("		}");

		out.println("		Transformer xformer = TransformerFactory.newInstance().newTransformer();");
		out.println("		xformer.setOutputProperty(OutputKeys.STANDALONE, \"no\");");
		out.println("		xformer.setOutputProperty(OutputKeys.ENCODING, \"UTF-8\");");
		out.println("		ByteArrayOutputStream baos = new ByteArrayOutputStream();");
		out.println("		xformer.transform(new DOMSource(document2), new StreamResult(baos));");
		out.println("		reqdoc = baos.toByteArray();");
		out.println("		");
		out.println("		rmi.getObject(\"setRelateToID\", messageID);");
		out.println("		messageID= \"\";");
		out.println("		resdoc = rmi.doWSCall(reqdoc);");
		out.println("		hist = rmi.history(null);");
		out.println("		");
		out.println("	} catch(Exception ex) {");
		out.println("		msg = \"error:\" + ex;");
		out.println("	}");
		out.println("	int sl = -1;");
		out.println("	for(int i=0; hist!=null && i<hist.size(); i++) {");
		out.println("		if(messageID.equals(hist.get(i).get(\"ID\"))) {");
		out.println("			sl = i;");
		out.println("		}");
		out.println("	}");
		out.println("	if(sl<0) messageID = \"\";");
		out.println("} else if(messageID.length()>0) {");

		out.println("		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();");
		out.println("		factory.setNamespaceAware(true);");
		out.println("		DocumentBuilder builder = factory.newDocumentBuilder();");
		out.println("		String nd; Object opath; Element elm;");
		out.println("		XPath xpath = XPathFactory.newInstance().newXPath();");
		out.println("		xpath.setNamespaceContext(qParse);");
		out.println("		byte[] obj = (byte[]) rmi.getObject(messageID, \"REQ\");");
		out.println("		if(obj!=null) {");
		out.println("			ByteArrayInputStream bais = new ByteArrayInputStream(obj);");
		out.println("			Document oldreq = builder.parse(bais);");
		out.println("			");
		out.println("			for(int i=0; i<req_a.length; i++) {");
		out.println("				try {");
		out.println("					opath = xpath.evaluate(req_a[i][4], oldreq, XPathConstants.NODESET);");
		out.println("					elm = (Element)((NodeList)opath).item(0);");
		out.println("					req_p[i] = elm.getFirstChild().getNodeValue();");
		out.println("				} catch(Exception x5) {}");
		out.println("			}");
		out.println("		// xml data retrieve");
		out.println("		}");

		out.println("		byte[] obj2 = (byte[]) rmi.getObject(messageID, \"RES\");");
		out.println("		if(obj2!=null) {");
		out.println("			xpath = XPathFactory.newInstance().newXPath();");
		out.println("			xpath.setNamespaceContext(qParse2);");
		out.println("			ByteArrayInputStream bais2 = new ByteArrayInputStream(obj2);");
		out.println("			Document oldres = builder.parse(bais2);");
		out.println("			// xml data retrieve");
		out.println("			for(int i=0; i<res_a.length; i++) {");
		out.println("				try {");
		out.println("					opath = xpath.evaluate(res_a[i][4], oldres, XPathConstants.NODESET);");
		out.println("					elm = (Element)((NodeList)opath).item(0);");
		out.println("					res_p[i] = elm.getFirstChild().getNodeValue();");
		out.println("				} catch(Exception x5) {}");
		out.println("			}");
		out.println("		}");
		out.println("	}");
		out.println("} catch(Exception x3) {}");
		out.println("}");

		out.println("%>");

		out.println("<script language='JavaScript'>");
		out.println("function changeMsgID(id) {");
		out.println("	document.all.messageID.value = id;");
		out.println("	document.all.mainForm.submit();");
		out.println("}");
		out.println("function openxml(id, tp) {");
		out.print("	window.open('loadxml/?id='+ id + '&tp=' + tp, '_blank', ");
		out.println("'menubar=0,width=650,height=450, resizable=yes,scrollbars=1');");
		out.println("}");

		out.println("</script>");

		out.println("</head><body>");
		out.println("<form action='application.jsp' method='post' name='mainForm' id='mainForm'>");
		out.println("<input type='hidden' name='messageID' value='<%=messageID%>' size=8>");
		out.println("<table cellspacing=0 cellpadding=0><tr height=15>");
		out.println("<td width=10 bgcolor='#0000ff'onClick=\"changeMsgID('');\"/>");
		out.println("<td><a href='application.jsp'>H</a></td>");
		out.println("<td><a href='admin.jsp'>A</a></td>");
		out.println("<td><a href='config.jsp'>.</a></td>");
		out.println("<td><%=msg%></td>");
		out.println("</tr></table>");
		out.println("<table cellspacing='0' cellpadding='0' border='1'>");
		out.println("<tr><td width='100' valign='top'>");

		// left bar show history
		out.println("<%");
		out.println("		out.print(\"<table border=0 cellspacing=0 cellpadding=0>\");");
		out.println("		String status = \"\";");
		out.println("		int i = (hist==null)? 0 : hist.size()-1;");
		out.println("		for(; hist!=null && i>=0; i--) {");
		out.println("			out.print(\"<tr>\");");
		out.println("			out.print(\"<td>\");");
		out.println("			String mid = (String) hist.get(i).get(\"ID\");");
		out.println("			if(messageID.equals(mid)) {");
		out.println("				out.println(\"<b>\");");
		out.println("				out.println(hist.get(i).get(\"ID\"));");
		out.println("				out.println(\"</b>\");");
		out.println("				status = (String) hist.get(i).get(\"STATUS\");");
		out.println("			} else {");
		out.println("				out.println(\"<a href=\\\"javascript:changeMsgID('\"+mid+\"');\\\">\");");
		out.println("				out.println(hist.get(i).get(\"ID\"));");
		out.println("				out.println(\"</a>\");");
		out.println("			}");
		
		out.println("			out.println(hist.get(i).get(\"STATUS\"));");
		out.println("			out.print(\"</td>\");");
		out.println("			out.print(\"<td>\");");
		out.println("			out.print(\"<table border=0 cellspacing=0 cellpadding=0>\");");
		out.println("			out.print(\"<tr height=7>\");");
		out.print("			out.print(\"<td width=5 bgcolor='#ff8080' onClick=\");");
		out.println("			out.print(\"\\\"openxml('\" + mid + \"','REQ');\\\">\");");
		out.println("			out.print(\"</td><td width=1/>\");");
		out.print("			out.print(\"<td width=5 bgcolor='#80ff80' \");");
		out.println("			out.print(\"onClick=\\\"openxml('\" + mid + \"','SRQ');\\\">\");");
		out.println("			out.print(\"</td><td width=1/>\");");
//		out.println("			out.print(\"<td width=5 bgcolor='#8080ff'>\");");
//		out.println("			out.print(\"</td><td width=1/>\");");
		out.println("			out.print(\"</tr>\");");

		out.println("			out.print(\"<tr height=1/>\");");

		out.println("			out.print(\"<tr height=7>\");");
		out.print("			out.print(\"<td width=5 bgcolor='#ff0000' \");");
		out.println("			out.print(\"onClick=\\\"openxml('\" + mid + \"','RES');\\\">\");");
		out.println("			out.print(\"</td><td width=1/>\");");
		out.print("			out.print(\"<td width=5 bgcolor='#00ff00' \");");
		out.println("			out.print(\"onClick=\\\"openxml('\" + mid + \"','SRS');\\\">\");");
		out.println("			out.print(\"</td><td width=1/>\");");
//		out.println("			out.print(\"<td width=5 bgcolor='#0000ff'>\");");
//		out.println("			out.print(\"</td><td width=1/>\");");
		out.println("			out.print(\"</tr>\");");

		out.println("			out.print(\"</table>\");");
		out.println("			out.print(\"</td>\");");
		out.println("			out.print(\"<tr>\");");
		out.println("		}");
		out.println("		out.print(\"</table>\");");
		out.println("%>");

		out.println("</td><td width='500'>");
		// request table begin ========================
		out.println("<table cellspacing='0' cellpadding='0' border='1'>");
		out.print("<tr height='1'>");
		for(int i=0; i<(reqdp+1); i++) {
			out.println("<td width='30' bgcolor='#ffa0a0'></td>");
		}
		out.print("<td width='400'></td><tr>");
		if(bReq)
			out.println("<tr><td colspan="+(reqdp+2)+" align=right>"
				+"<input type='submit' value='REQUEST' name='REQBT'>"
				+"</td></tr>");
		for(int i=0; i<reqmsg.size(); i++) {
			String[] words = reqmsg.get(i);
			String[] elmpth = words[5].split("_");
			out.print("<tr>");
			int c=0;
			for(; c<elmpth.length-1; c++) {
				out.println("<td></td>");
			}
			out.println("<td colspan="+(reqdp-c+2)+">");
			out.println(words[2]);
			if(!"ABIE".equals(words[3])) {
				out.println("&nbsp;&nbsp;&nbsp; <input type='text' name='reqField" + i 
					+"' value='<%=req_p[" + i + "]%>' size=30>");
			}
			out.println("</td>");
			out.println("</tr>");
		}
		out.println("</table>");
		// request table end ========================

		// response table begin ========================
		out.println("<table cellspacing='0' cellpadding='0' border='1'>");
		out.print("<tr height='1'>");
		for(int i=0; i<(resdp+1); i++) {
			out.println("<td width='30' bgcolor='#ffa0a0'></td>");
		}
		out.print("<td width='400'></td><tr>");
		if(!bReq) {
			out.println("<% if(status.equals(\"Rq\")) {%>");
			out.println("<tr><td colspan="+(resdp+2)+" align=right>"
				+"<input type='submit' value='PROC1' name='PROC1'>"
				+"<input type='submit' value='REPLY' name='RESBT'>"
				+"</td></tr>");
			out.println("<% } %>");
		}
		for(int i=0; i<resmsg.size(); i++) {
			String[] words = resmsg.get(i);
			String[] elmpth = words[5].split("_");
			out.print("<tr>");
			int c=0;
			for(; c<elmpth.length-1; c++) {
				out.println("<td></td>");
			}
			out.println("<td colspan="+(resdp-c+2)+">");
			out.println(words[2]);
			if(!"ABIE".equals(words[3]))
				out.println("&nbsp;&nbsp;&nbsp; <input type='text' name='resField" + i 
					+"' value='<%=res_p["+ i +"]%>' size=30>");
			out.println("</td>");
			out.println("</tr>");
		}
		out.println("</table>");
		// response table end ========================
		out.println("</td></tr></table>");

		out.println("</form>");
		out.println("</body></html>");
		out.close();

		// ====================== config.jsp =======================
		out = new PrintWriter(dir+"/config.jsp", "UTF-8");
		out.println("<%@ page contentType='text/html; charset=UTF-8'%>");
		out.println("<%@ page import='java.io.*, java.util.*, javax.xml.parsers.*'%>");
		out.println("<%@ page import='java.sql.*'%>");
		out.println("<%@ page import='org.w3c.dom.*, javax.xml.transform.*'%>");
		out.println("<%@ page import='javax.xml.transform.dom.*, javax.xml.transform.stream.*'%>");
		out.println("<%@ page import='com.dabos.engine.lib.*, java.rmi.*'%>");
		out.println("<%@ page import='javax.xml.xpath.*, org.xml.sax.*,javax.xml.parsers.*'%>");
		out.println("<% request.setCharacterEncoding(\"UTF-8\"); %>");
		out.println("");

		out.println("<%!");
		out.println("String screen(String s) {");
		out.println("		String r = null;");
		out.println("		if(s==null) return s;");
		out.println("		char[] cc = s.toCharArray();");
		out.println("		int cn = 0;");
		out.println("		for(int i=0; i<cc.length; i++) {");
		out.println("			if(cc[i]=='\\'') cn++;");
		out.println("		}");
		out.println("		if(cn==0) return s;");
		out.println("		char[] cs = new char[cc.length+cn*5];");
		out.println("		for(int i=0,j=0; i<cc.length; i++,j++) {");
		out.println("			if(cc[i]=='\\'') { cs[j++] = '&'; cs[j++] = '#'; cs[j++] = '3'; cs[j++] = '9'; cs[j++] = ';'; }");
		out.println("			else cs[j] = cc[i];");
		out.println("		}");
		out.println("		return new String(cs);");
		out.println("}");
		out.println("%>");

		out.println("<html><head>");
		out.println("<%");

		out.print("			String ");
		for(int i=0; i<reqmsg.size(); i++) {
			String[] words = reqmsg.get(i);
			if(i>0) out.print(", ");
			out.print("p_req" + i +"=\"\"");
		}
		out.println(";");
		out.print("			String ");
		for(int i=0; i<resmsg.size(); i++) {
			String[] words = resmsg.get(i);
			if(i>0) out.print(", ");
			out.print("p_res" + i +"=\"\"");
		}
		out.println(";");

		out.println("String msg = null;");
		out.println("ServletContext context = getServletContext();");
		out.println("String rmiurl = context.getInitParameter(\"rmiurl\");");
		out.println("Hashtable<String,String> xconfig = null;");
		out.println("String sqltext = request.getParameter(\"sqltext\");");
		out.println("if(sqltext==null) sqltext = \"\";");
		out.println("Vector<String[]> rsset = null;");
		
		out.println("byte[] cfg = null;");

		out.println("try {");
		out.println("	WebServiceRmi rmi = (WebServiceRmi) Naming.lookup(rmiurl);");
		out.println("	cfg = (byte[]) rmi.getObject(\"config\", \"read\");");
		out.println("	ByteArrayInputStream bais = new ByteArrayInputStream(cfg);");
		out.println("	ObjectInputStream ois = new ObjectInputStream(bais);");
		out.println("	xconfig = (Hashtable<String,String>) ois.readObject();");

		out.println("	if(request.getParameter(\"saveBT\")!=null) {");
		out.println("		rmi.getObject(\"set-self-url\", request.getParameter(\"self-url\"));");
		out.println("		rmi.getObject(\"set-peer-url\", request.getParameter(\"peer-url\"));");
		out.println("		rmi.getObject(\"set-self-p12\", request.getParameter(\"self-p12\"));");
		out.println("		rmi.getObject(\"set-peer-cer\", request.getParameter(\"peer-cer\"));");
		out.println("		rmi.getObject(\"set-jdbc-url\", request.getParameter(\"jdbc-url\"));");
		out.println("		rmi.getObject(\"set-jdbc-usr\", request.getParameter(\"jdbc-usr\"));");
		out.println("		rmi.getObject(\"set-jdbc-pw\", request.getParameter(\"jdbc-pw\"));");
//		out.println("		rmi.getObject(\"set-self-name\", request.getParameter(\"self-name\"));");
//		out.println("		rmi.getObject(\"set-peer-name\", request.getParameter(\"peer-name\"));");
		out.println("		rmi.getObject(\"set-auto-rpy\", request.getParameter(\"auto-rpy\"));");
		out.println("		rmi.getObject(\"set-jdbc-drv\", request.getParameter(\"jdbc-drv\"));");
		out.println("		rmi.getObject(\"set-char-set\", request.getParameter(\"char-set\"));");
		out.println("		");
		for(int i=0; i<reqmsg.size(); i++) {
			String[] words = reqmsg.get(i);
			out.println("		p_req" + i +" = request.getParameter(\"reqField" + i + "\");");
			out.println("		if(p_req" + i +"==null) p_req" + i + " = \"\";");
			out.println("		rmi.getObject(\"set-reqField"+ i +"\", p_req"+ i +");");
		}

		for(int i=0; i<resmsg.size(); i++) {
			String[] words = resmsg.get(i);
			out.println("		p_res" + i +" = request.getParameter(\"resField" + i + "\");");
			out.println("		if(p_res" + i +"==null) p_res" + i + " = \"\";");
			out.println("		rmi.getObject(\"set-resField"+ i +"\", p_res"+ i +");");
		}

		out.println("		rmi.getObject(\"save-config\", null);");
		out.println("		cfg = (byte[]) rmi.getObject(\"config\", \"read\");");
		out.println("		bais = new ByteArrayInputStream(cfg);");
		out.println("		ois = new ObjectInputStream(bais);");
		out.println("		xconfig = (Hashtable<String,String>) ois.readObject();");
		out.println("	}");

//		out.println("	Class.forName(config.get(\"jdbc-drv\"));");
//		out.println("	Class.forName(\"org.hsqldb.jdbcDriver\" );");
		out.println("	if(request.getParameter(\"connBT\")!=null) {");
		out.println("		msg = (String) rmi.getObject(\"test-db\", null);");
		out.println("	}");

		out.println("	if(request.getParameter(\"queryBT\")!=null) {");
		out.println("		rsset = (Vector<String[]>) rmi.getObject(\"sql-query\", sqltext);");
		out.println("	}");

		out.println("	if(request.getParameter(\"updateBT\")!=null) {");
		out.println("		msg = \"\" + rmi.getObject(\"sql-update\", sqltext);");
		out.println("	}");

		out.println("} catch(Exception ex2) {");
		out.println("	msg=\"error:\" + ex2;");
		out.println("}");

		out.println("%>");
		out.println("</head><body>");
		out.println("<form action='config.jsp' method='post' name='mainForm' id='mainForm'>");
		out.println("<table cellspacing=0 cellpadding=0><tr height=15>");
		out.println("<tr><td width=90></td><td width=400></td></tr>");
		out.println("<tr><td colspan=2 align=right>");
		out.println(" <a href='application.jsp'>H</a> <a href='admin.jsp'>A</a> <a href='config.jsp'>.</a>");
		out.println("[<%=msg%>]");
		out.println("<input type='submit' name='saveBT' value='SAVE'>");
		out.println("<input type='submit' name='connBT' value='Conn'>");
		out.println("<input type='submit' name='queryBT' value='Query'>");
		out.println("<input type='submit' name='updateBT' value='Update'>");
		out.println("</td></tr>");

		out.println("<tr><td>self-url</td><td><input type='text' "
			+"name='self-url' value='<%=xconfig.get(\"self-url\")%>' size=80></td></tr>");
		out.println("<tr><td>peer-url</td><td><input type='text' "
			+"name='peer-url' value='<%=xconfig.get(\"peer-url\")%>' size=80></td></tr>");
		out.println("<tr><td>self-p12</td><td><input type='text' "
			+"name='self-p12' value='<%=xconfig.get(\"self-p12\")%>' size=25> &nbsp; peer-cer "
			+"<input type='text' name='peer-cer' value='<%=xconfig.get(\"peer-cer\")%>' size=25>"
			+"</td></tr>");
		out.println("<tr><td>jdbc-url</td><td><input type='text' "
			+"name='jdbc-url' value='<%=xconfig.get(\"jdbc-url\")%>' size=40> &nbsp; jdbc-usr "
			+"<input type='text' name='jdbc-usr' value='<%=xconfig.get(\"jdbc-usr\")%>' size=4>"
			+"&nbsp; passwd"
			+"<input type='text' name='jdbc-pw' value='<%=xconfig.get(\"jdbc-pw\")%>' size=4>"
			+"</td></tr>");
//		out.println("<tr><td>self-name</td><td><input type='text' "
//			+"name='self-name' value='<%=xconfig.get(\"self-name\")%>' size=20>"
		out.println("<tr><td>jdbc-drv</td><td><input type='text' "
			+"name='jdbc-drv' value='<%=xconfig.get(\"jdbc-drv\")%>' size=35>"
			+"&nbsp; char-set "
			+"<input type='text' name='char-set' value='<%=xconfig.get(\"char-set\")%>' size=5>"
			+" auto "
			+"<input type='text' name='auto-rpy' value='<%=xconfig.get(\"auto-rpy\")%>' size=1>"
			+"</td></tr>");
//		out.println("<tr><td>uddi-url</td><td><input type='text' "
//			+"name='uddi-url' value='<%=xconfig.get(\"uddi-url\")%>' size=80></td></tr>");
//		out.println("<tr><td>ldap-url</td><td><input type='text' "
//			+"name='ldap-url' value='<%=xconfig.get(\"ldap-url\")%>' size=80></td></tr>");
		out.println("<tr><td colspan=2>");
//		out.println("<input type='text' name='sqltext' value='<%=sqltext%>' size=70>");
		out.println("<textarea row=4 cols='70' name='sqltext'><%=sqltext%></textarea>");
		out.println("</td></tr>");
		out.println("</table>");

		out.println("<table cellspacing='0' cellpadding='0' border='1'>");
		out.print("<tr height='1'>");
		for(int i=0; i<(reqdp+1); i++) {
			out.println("<td width='30' bgcolor='#ffa0a0'></td>");
		}
		out.print("<td width='400'></td><tr>");
		for(int i=0; i<reqmsg.size(); i++) {
			String[] words = reqmsg.get(i);
			String[] elmpth = words[5].split("_");
			out.print("<tr>");
			int c=0;
			for(; c<elmpth.length-1; c++) {
				out.println("<td></td>");
			}
			out.println("<td colspan="+(reqdp-c+2)+">");
			out.println(words[2]);
			if(!"ABIE".equals(words[3])) {
				out.println("<% p_req" + i +" = xconfig.get(\"reqField"+ i +"\"); %>");
				out.println("<% if(p_req" + i +"==null) p_req" + i +" = \"\"; %>");
				out.println("(req"+i+")&nbsp;&nbsp;&nbsp; "
					+"<input type='text' name='reqField"+ i +"' value='<%=screen(p_req"+ i +")%>' size=30>");
			}
			out.println("</td>");
			out.println("</tr>");
		}
		out.println("</table>");

		out.println("<table cellspacing='0' cellpadding='0' border='1'>");
		out.print("<tr height='1'>");
		for(int i=0; i<(resdp+1); i++) {
			out.println("<td width='30' bgcolor='#ffa0a0'></td>");
		}
		out.print("<td width='400'></td><tr>");
		for(int i=0; i<resmsg.size(); i++) {
			String[] words = resmsg.get(i);
			String[] elmpth = words[5].split("_");
			out.print("<tr>");
			int c=0;
			for(; c<elmpth.length-1; c++) {
				out.println("<td></td>");
			}
			out.println("<td colspan="+(resdp-c+2)+">");
			out.println(words[2]);
			if(!"ABIE".equals(words[3])) {
				out.println("<% p_res" + i +" = xconfig.get(\"resField"+ i +"\"); %>");
				out.println("<% if(p_res" + i +"==null) p_res" + i +" = \"\"; %>");
				out.println("(res"+i+")&nbsp;&nbsp;&nbsp; "
					+"<input type='text' name='resField"+ i +"' value='<%=screen(p_res"+ i +")%>' size=30>");
			}
			out.println("</td>");
			out.println("</tr>");
		}
		out.println("</table>");

		out.println("</form>");

		out.println("<%if(rsset!=null && rsset.size()>0) {%>");
		out.println("<table border=1 cellspacing=0 cellpadding=0>");
		out.println("<%	for(int i=0; i<rsset.size(); i++) { String[] wds = rsset.get(i); %>");
		out.println("<tr>");
		out.println("<%		for(int c=0; c<wds.length; c++) {%>");
		out.println("<td><%=wds[c]%></td>");
		out.println("<%		}%>");
		out.println("</tr>");
		out.println("<%	}%>");
		out.println("</table>");
		out.println("<%}%>");

		out.println("</body></html>");

		out.close();

		// ====================== admin.jsp =======================
		out = new PrintWriter(dir+"/admin.jsp", "UTF-8");
		out.println("<%@ page contentType='text/html; charset=UTF-8'%>");
		out.println("<%@ page import='java.io.*, java.util.*, javax.xml.parsers.*'%>");
		out.println("<%@ page import='java.sql.*'%>");
		out.println("<%@ page import='org.w3c.dom.*, javax.xml.transform.*'%>");
		out.println("<%@ page import='javax.xml.transform.dom.*, javax.xml.transform.stream.*'%>");
		out.println("<%@ page import='com.dabos.engine.lib.*, java.rmi.*'%>");
		out.println("<%@ page import='javax.xml.xpath.*, org.xml.sax.*,javax.xml.parsers.*'%>");
		out.println("<% request.setCharacterEncoding(\"UTF-8\"); %>");
		out.println("");

		out.println("<%!");
		out.println("String screen(String s) {");
		out.println("		String r = null;");
		out.println("		if(s==null) return s;");
		out.println("		char[] cc = s.toCharArray();");
		out.println("		int cn = 0;");
		out.println("		for(int i=0; i<cc.length; i++) {");
		out.println("			if(cc[i]=='\\'') cn++;");
		out.println("		}");
		out.println("		if(cn==0) return s;");
		out.println("		char[] cs = new char[cc.length+cn*5];");
		out.println("		for(int i=0,j=0; i<cc.length; i++,j++) {");
		out.println("			if(cc[i]=='\\'') { cs[j++] = '&'; cs[j++] = '#'; cs[j++] = '3'; cs[j++] = '9'; cs[j++] = ';'; }");
		out.println("			else cs[j] = cc[i];");
		out.println("		}");
		out.println("		return new String(cs);");
		out.println("}");
		out.println("%>");

		out.println("<html><head>");
		out.println("<%");

		out.print("			String ");
		for(int i=0; i<reqmsg.size(); i++) {
			String[] words = reqmsg.get(i);
			if(i>0) out.print(", ");
			out.print("p_req" + i +"=\"\"");
		}
		out.println(";");
		out.print("			String ");
		for(int i=0; i<resmsg.size(); i++) {
			String[] words = resmsg.get(i);
			if(i>0) out.print(", ");
			out.print("p_res" + i +"=\"\"");
		}
		out.println(";");

		out.println("String msg = null;");
		out.println("ServletContext context = getServletContext();");
		out.println("String rmiurl = context.getInitParameter(\"rmiurl\");");
		out.println("Hashtable<String,String> xconfig = null;");
		out.println("String sqltext = request.getParameter(\"sqltext\");");
		out.println("if(sqltext==null) sqltext = \"\";");
		out.println("Vector<String[]> rsset = null;");
		
		out.println("byte[] cfg = null;");

		out.println("try {");
		out.println("	WebServiceRmi rmi = (WebServiceRmi) Naming.lookup(rmiurl);");
		out.println("	cfg = (byte[]) rmi.getObject(\"config\", \"read\");");
		out.println("	ByteArrayInputStream bais = new ByteArrayInputStream(cfg);");
		out.println("	ObjectInputStream ois = new ObjectInputStream(bais);");
		out.println("	xconfig = (Hashtable<String,String>) ois.readObject();");

		out.println("	if(request.getParameter(\"saveBT\")!=null) {");
		out.println("		rmi.getObject(\"set-self-url\", request.getParameter(\"self-url\"));");
		out.println("		rmi.getObject(\"set-peer-url\", request.getParameter(\"peer-url\"));");
		out.println("		rmi.getObject(\"set-self-p12\", request.getParameter(\"self-p12\"));");
		out.println("		rmi.getObject(\"set-peer-cer\", request.getParameter(\"peer-cer\"));");
		out.println("		rmi.getObject(\"set-jdbc-url\", request.getParameter(\"jdbc-url\"));");
		out.println("		rmi.getObject(\"set-jdbc-usr\", request.getParameter(\"jdbc-usr\"));");
		out.println("		rmi.getObject(\"set-jdbc-pw\", request.getParameter(\"jdbc-pw\"));");
//		out.println("		rmi.getObject(\"set-self-name\", request.getParameter(\"self-name\"));");
		out.println("		rmi.getObject(\"set-jdbc-drv\", request.getParameter(\"jdbc-drv\"));");
//		out.println("		rmi.getObject(\"set-peer-name\", request.getParameter(\"peer-name\"));");
		out.println("		rmi.getObject(\"set-char-set\", request.getParameter(\"char-set\"));");
		out.println("		rmi.getObject(\"set-auto-rpy\", request.getParameter(\"auto-rpy\"));");
		out.println("		");

		out.println("		rmi.getObject(\"save-config\", null);");
		out.println("		cfg = (byte[]) rmi.getObject(\"config\", \"read\");");
		out.println("		bais = new ByteArrayInputStream(cfg);");
		out.println("		ois = new ObjectInputStream(bais);");
		out.println("		xconfig = (Hashtable<String,String>) ois.readObject();");
		out.println("	}");

//		out.println("	Class.forName(\"org.hsqldb.jdbcDriver\" );");
		out.println("	if(request.getParameter(\"connBT\")!=null) {");
		out.println("		msg = (String) rmi.getObject(\"test-db\", null);");
		out.println("	}");

		out.println("	if(request.getParameter(\"queryBT\")!=null) {");
		out.println("		rsset = (Vector<String[]>) rmi.getObject(\"sql-query\", sqltext);");
		out.println("	}");

		out.println("	if(request.getParameter(\"updateBT\")!=null) {");
		out.println("		msg = \"\" + rmi.getObject(\"sql-update\", sqltext);");
		out.println("	}");

		out.println("} catch(Exception ex2) {");
		out.println("	msg=\"error:\" + ex2;");
		out.println("}");

		out.println("%>");
		out.println("</head><body>");
		out.println("<form action='admin.jsp' method='post' name='mainForm' id='mainForm'>");
		out.println("<table cellspacing=0 cellpadding=0><tr height=15>");
		out.println("<tr><td width=90></td><td width=400></td></tr>");
		out.println("<tr><td colspan=2 align=right>");
		out.println(" <a href='application.jsp'>H</a> <a href='admin.jsp'>A</a> <a href='config.jsp'>.</a>");
		out.println("[<%=msg%>]");
		out.println("<input type='submit' name='saveBT' value='SAVE'>");
		out.println("<input type='submit' name='connBT' value='Conn'>");
		out.println("<input type='submit' name='queryBT' value='Query'>");
		out.println("<input type='submit' name='updateBT' value='Update'>");
		out.println("</td></tr>");

		out.println("<tr><td>self-url</td><td><input type='text' "
			+"name='self-url' value='<%=xconfig.get(\"self-url\")%>' size=80></td></tr>");
		out.println("<tr><td>peer-url</td><td><input type='text' "
			+"name='peer-url' value='<%=xconfig.get(\"peer-url\")%>' size=80></td></tr>");
		out.println("<tr><td>self-p12</td><td><input type='text' "
			+"name='self-p12' value='<%=xconfig.get(\"self-p12\")%>' size=25> &nbsp; peer-cer "
			+"<input type='text' name='peer-cer' value='<%=xconfig.get(\"peer-cer\")%>' size=25>"
			+"</td></tr>");
		out.println("<tr><td>jdbc-url</td><td><input type='text' "
			+"name='jdbc-url' value='<%=xconfig.get(\"jdbc-url\")%>' size=40> &nbsp; jdbc-usr "
			+"<input type='text' name='jdbc-usr' value='<%=xconfig.get(\"jdbc-usr\")%>' size=4>"
			+"&nbsp; passwd"
			+"<input type='text' name='jdbc-pw' value='<%=xconfig.get(\"jdbc-pw\")%>' size=4>"
			+"</td></tr>");
//		out.println("<tr><td>self-name</td><td><input type='text' "
//			+"name='self-name' value='<%=xconfig.get(\"self-name\")%>' size=20>"
		out.println("<tr><td>jdbc-drv</td><td><input type='text' "
			+"name='jdbc-drv' value='<%=xconfig.get(\"jdbc-drv\")%>' size=35>"
			+"&nbsp; char-set "
			+"<input type='text' name='char-set' value='<%=xconfig.get(\"char-set\")%>' size=5>"
			+" auto "
			+"<input type='text' name='auto-rpy' value='<%=xconfig.get(\"auto-rpy\")%>' size=1>"
			+"</td></tr>");
//		out.println("<tr><td>uddi-url</td><td><input type='text' "
//			+"name='uddi-url' value='<%=xconfig.get(\"uddi-url\")%>' size=80></td></tr>");
//		out.println("<tr><td>ldap-url</td><td><input type='text' "
//			+"name='ldap-url' value='<%=xconfig.get(\"ldap-url\")%>' size=80></td></tr>");
		out.println("<tr><td colspan=2>");
//		out.println("<input type='text' name='sqltext' value='<%=sqltext%>' size=70>");
		out.println("<textarea row=4 cols='70' name='sqltext'><%=sqltext%></textarea>");
		out.println("</td></tr>");
		out.println("</table>");
		out.println("</form>");

		out.println("<%if(rsset!=null && rsset.size()>0) {%>");
		out.println("<table border=1 cellspacing=0 cellpadding=0>");
		out.println("<%	for(int i=0; i<rsset.size(); i++) { String[] wds = rsset.get(i); %>");
		out.println("<tr>");
		out.println("<%		for(int c=0; c<wds.length; c++) {%>");
		out.println("<td><%=wds[c]%></td>");
		out.println("<%		}%>");
		out.println("</tr>");
		out.println("<%	}%>");
		out.println("</table>");
		out.println("<%}%>");

		out.println("</body></html>");

		out.close();
	
	}

	public void bpReqModuleGen(String btid, String cname, String rsmid, String to, String inpid) 
		throws Exception {
		int rsmord = rsmi.get(rsmid);
		String rname = rsmn.get(rsmord);
		String path = modpath + "BT"+btid +"/reqmod/";

		File dir = new File(path);
		if(!dir.exists()) dir.mkdirs();
		File jspdir = new File(path + "src-docroot");
		if(!jspdir.exists()) jspdir.mkdirs();
		File srvdir = new File(path + "src-servlet/");
		if(!srvdir.exists()) srvdir.mkdirs();
		File rmidir = new File(path + "rmi-server/");
		if(!rmidir.exists()) rmidir.mkdirs();
		File libdir = new File(path + "lib-server/");
		if(!libdir.exists()) libdir.mkdirs();
		String selfhost = btd.get(btid).get("RQM")[6];
		String peerhost = btd.get(btid).get("RSM")[6];
		rmiServerGen(""+rmidir, cname, rsmid, to, inpid, selfhost, peerhost, true);
		pageJspGen(""+jspdir, cname, rsmid, to, inpid, true);
		wsServletGen(""+srvdir, cname, rsmid, to, inpid);
		copyJarLib(""+libdir);
		configFile(path, cname, selfhost);
	}

	public void configFile(String dir, String name, String selfhost) throws Exception {
		FileOutputStream fos = new FileOutputStream(dir + "/build.xml");
		PrintWriter out = new PrintWriter(fos);

		out.println("<!--");
		out.println("CLASSPATH=StudentDataRequesterRmi.jar;egifsupport.jar;.");
			out.println("rmi_server: java com.dabos.rmisrv."+name+"Rmi");
		out.println("servlet: com.dabos.ws."+name);
		out.println("-->");

		out.println("<project name='online' default='package' basedir='.'>");
		out.println("	<property name='prjname' value='"+name+"'/>");

		out.println("	<target name='clean'>");
		out.println("		<delete dir='servlet-build'/>");
		out.println("		<delete dir='rmi-build'/>");
		out.println("		<delete file='${prjname}.war'/>");
		out.println("		<delete file='${prjname}Rmi.jar'/>");
		out.println("	</target>");

		out.println("	<target name='init'>");
		out.println("		<mkdir dir='rmi-build'/>");
		out.println("		<mkdir dir='src-docroot/WEB-INF/classes'/>");
		out.println("	</target>");

		out.println("	<path id='rmi.path'>");
		out.println("		<fileset dir='lib-server'>");
		out.println("			<include name='**/*.jar'/>");
		out.println("		</fileset>");
		out.println("		<fileset dir='../../../../lib'>");
		out.println("			<include name='**/*.jar'/>");
		out.println("		</fileset>");
		out.println("	</path>");

		out.println("	<target name='rmi-build'>");
		out.println("		<javac srcdir='rmi-server' destdir='rmi-build'");
		out.println("			classpathref='rmi.path'>");
		out.println("			<include name='**/*.java'/>");
		out.println("		</javac>");
		out.println("	</target>");

		out.println(" 	<target name='servlet-build'>");
		out.println("		<javac srcdir='src-servlet' destdir='src-docroot/WEB-INF/classes'");
		out.println("			classpathref='rmi.path'>");
		out.println("			<include name='**/*.java'/>");
		out.println("		</javac>");
		out.println("	</target>");

		out.println("	<target name='rmi-run' depends='package'>");
		out.println("		<java jar='${prjname}Rmi.jar' fork='true'>");
		out.println("			<classpath>");
		out.println("                <path refid='rmi.path'/>");
		out.println("			</classpath>");
		out.println("		</java>");
		out.println("	</target>");

		out.println("	<target name='package' depends='init, rmi-build, servlet-build'>");
		out.println("		<copy todir='src-docroot/WEB-INF/lib'>");
		out.println("			<fileset dir='lib-server'/>");
		out.println("		</copy>");
		out.println("		<jar jarfile='${prjname}.war' basedir='src-docroot'/>");
		out.println("		<jar destfile='${prjname}Rmi.jar' basedir='rmi-build'>");
		out.println("			<manifest>");
		out.println("				<attribute name='Main-Class' value='com.dabos.rmisrv."+name+"Rmi'/>");
		out.println("			</manifest>");
		out.println("		</jar>");
		out.println("		<copy file='${prjname}.war' todir='../../../dist/war'/>");
		out.println("		<copy file='${prjname}Rmi.jar' todir='../../../dist/rmi'/>");
		out.print("		<copy file='${prjname}.war' ");
		out.println(         "todir='D:/Java/javaweb/hosts/" + selfhost + "/upload'/>");
//		out.print("		<copy file='${prjname}Rmi.jar' ");
//		out.println(         "todir='C:/Program Files/Java/jre6/lib/ext'/>");
		out.println("	</target>");
		out.println("</project>");
		out.close();

		File webdir = new File(dir + "/src-docroot/WEB-INF/");
		if(!webdir.exists()) webdir.mkdirs();
		fos = new FileOutputStream(dir + "/src-docroot/WEB-INF/web.xml");
		out = new PrintWriter(fos);
		out.println("<?xml version='1.0' encoding='ISO-8859-1'?>");
		out.println("<!DOCTYPE web-app");
		out.println("	PUBLIC '-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN'");
		out.println("		'http://java.sun.com/dtd/web-app_2_3.dtd'>");
		out.println("<web-app>");
		out.println("");

		out.println("<welcome-file-list>");
		out.println("<welcome-file>application.jsp</welcome-file>");
		out.println("</welcome-file-list>");
		out.println("");

		out.println("<context-param>");
		out.println("<param-name>rmiurl</param-name>");
		out.println("<param-value>//127.0.0.1:" + port +"/" + name + "Rmi</param-value>");
		out.println("</context-param>");
		out.println("");

		out.println("	<servlet>");
		out.println("		<servlet-name>service</servlet-name>");
		out.println("		<servlet-class>com.dabos.ws."+name+"</servlet-class>");
		out.println("	</servlet>");
		out.println("	<servlet-mapping>");
		out.println("		<servlet-name>service</servlet-name>");
		out.println("		<url-pattern>/service</url-pattern>");
		out.println("	</servlet-mapping>");
		out.println("	");

		out.println("	<servlet>");
		out.println("		<servlet-name>loadxml</servlet-name>");
		out.println("		<servlet-class>com.dabos.ws.LoadXML</servlet-class>");
		out.println("	</servlet>");
		out.println("	<servlet-mapping>");
		out.println("		<servlet-name>loadxml</servlet-name>");
		out.println("		<url-pattern>/loadxml/*</url-pattern>");
		out.println("	</servlet-mapping>");
		out.println("	");

		out.println("	<servlet>");
		out.println("		<servlet-name>default</servlet-name>");
		out.println("		<servlet-class>org.apache.catalina.servlets.DefaultServlet</servlet-class>");
		out.println("		<init-param>");
		out.println("			<param-name>debug</param-name>");
		out.println("			<param-value>0</param-value>");
		out.println("		</init-param>");
		out.println("		<init-param>");
		out.println("			<param-name>listings</param-name>");
		out.println("			<param-value>true</param-value>");
		out.println("		</init-param>");
		out.println("		<load-on-startup>1</load-on-startup>");
		out.println("	</servlet>");
		out.println("	<servlet-mapping>");
		out.println("		<servlet-name>default</servlet-name>");
		out.println("		<url-pattern>/</url-pattern>");
		out.println("	</servlet-mapping>");
		out.println("");
		out.println("	<servlet>");
		out.println("		<servlet-name>jsp</servlet-name> ");
		out.println("		<servlet-class>org.apache.jasper.servlet.JspServlet</servlet-class> ");
		out.println("		<init-param>");
		out.println("			<param-name>logVerbosityLevel</param-name> ");
		out.println("			<param-value>WARNING</param-value> ");
		out.println("		</init-param>");
		out.println("		<init-param>");
		out.println("			<param-name>fork</param-name> ");
		out.println("			<param-value>false</param-value> ");
		out.println("		</init-param>");
		out.println("		<load-on-startup>3</load-on-startup> ");
		out.println("	</servlet>");
		out.println("	<servlet-mapping>");
		out.println("		<servlet-name>jsp</servlet-name> ");
		out.println("		<url-pattern>*.jsp</url-pattern> ");
		out.println("	</servlet-mapping>");

		out.println("</web-app>");
		out.close();
	}

	public void copyJarLib(String dir) throws Exception {
		FileInputStream fis = new FileInputStream("support/egifsupport.jar");
		FileOutputStream fos = new FileOutputStream(dir + "/egifsupport.jar");
		int dt;
		while( (dt=fis.read())>=0 ) fos.write(dt);
		fos.close();
		fis.close();
	}

	public void bpResModuleGen(String btid, String cname, String rsmid, String to, String inpid) 
		throws Exception {
		int rsmord = rsmi.get(rsmid);
		String rname = rsmn.get(rsmord);
		String path = modpath + "BT"+btid +"/resmod/";

		File dir = new File(path);
		if(!dir.exists()) dir.mkdirs();
		File jspdir = new File(path + "src-docroot/");
		if(!jspdir.exists()) jspdir.mkdirs();
		File srvdir = new File(path + "src-servlet/");
		if(!srvdir.exists()) srvdir.mkdirs();
		File rmidir = new File(path + "rmi-server/");
		if(!rmidir.exists()) rmidir.mkdirs();
		File libdir = new File(path + "lib-server/");
		if(!libdir.exists()) libdir.mkdirs();
		String selfhost = btd.get(btid).get("RSM")[6];
		String peerhost = btd.get(btid).get("RQM")[6];
		rmiServerGen(""+rmidir, cname, rsmid, to, inpid, selfhost, peerhost, false);
		pageJspGen(""+jspdir, cname, inpid, to, rsmid, false);
		wsServletGen(""+srvdir, cname, rsmid, to, inpid);
		copyJarLib(""+libdir);
		configFile(path, pack(cname), selfhost);
	}

	public void bpWSCaller(String cname, int rsmord) throws Exception {

		String rname = rsmn.get(rsmord);
		String progname = pack(cname) + "XMLCreator";

		File dir = new File(jvpath);
		if(!dir.exists()) dir.mkdirs();
		String filename = dir+"/" + progname+".java";
		FileOutputStream fos = new FileOutputStream(filename);
		PrintWriter out = new PrintWriter(fos);

		out.println("package mict;");
		out.println("");
		out.println("import java.io.*;");
		out.println("import java.util.*;");
		out.println("import javax.xml.parsers.*;");
		out.println("import org.w3c.dom.*;");
		out.println("import javax.xml.transform.*;");
		out.println("import javax.xml.transform.dom.*;");
		out.println("import javax.xml.transform.stream.*;");
		out.println("");
		out.println("public class " + progname + " { ");

		out.println("\tpublic Document " + rname + "CreateXML() throws Exception { ");

		out.println("\t\tDocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();");
		out.println("\t\tfactory.setNamespaceAware(true);");
		out.println("\t\tDocumentBuilder builder = factory.newDocumentBuilder();");
		out.println("\t\tDocument document = builder.newDocument();");
		out.println("\t\tElement elem0, elem1, elem2=null, elem3=null, elem4=null, elem5=null;");

		out.println(
			"\t\telem0 = document.createElementNS(\"urn:th:gov:egif:codelist:standard:"
				+rname+":1.0\"");
		out.println("\t\t, \"rsm:" + rname +"\");");
		out.println("\t\telem0.setAttribute(\"xmlns:ram\"");
		out.println("\t\t, \"urn:th:gov:egif:data:standard:ReusableAggregateBusinessInformationEntity:1\");");
		out.println("\t\telem0.setAttribute(\"xmlns:udt\", \"urn:th:gov:egif:data:standard:UnqualifiedDataType:1\");");
		out.println("\t\telem0.setAttribute(\"xmlns:qdt\", \"urn:th:gov:egif:data:standard:QualifiedDataType:1\");");
		out.println("\t\tdocument.appendChild(elem0);");

		Vector<String[]> rv = rsmd.get(rsmord);
		for(int i=0; i<rv.size(); i++) {
			String[] elems = rv.get(i);
			out.println("\t\telem1 = document.createElementNS(\"urn:th:gov:egif:codelist:standard:"+rname+":1.0\"");
			out.println("\t\t, \"rsm:"+ elems[0]+"\");");
			out.println("\t\telem0.appendChild(elem1);");
			if(elems[1]==null || elems[1].length()==0) {
				out.println("\t\telem1.appendChild(document.createTextNode(\""+elems[2]+"\"));");
			} else {
				out.println("");
				bieJavaXML(2, out, elems[1]);
			}
		}

		out.println("");
		out.println("");
		out.println("\t\treturn document;");
		out.println("\t}");

		out.println("\tpublic static void main(String[] args) throws Exception { ");
		out.println("\t\t" + progname + " dataobj = new "+ progname+"();");
		out.println("\t\tDocument document = dataobj."+ rname+"CreateXML();");
		out.println("\t\tTransformer xformer = TransformerFactory.newInstance().newTransformer();");
		out.println("\t\txformer.setOutputProperty(OutputKeys.STANDALONE, \"no\");");
		out.println("\t\txformer.setOutputProperty(OutputKeys.ENCODING, \"UTF-8\");");
		out.println("\t\tOutputStream os = System.out;");
		out.println("\t\txformer.transform(new DOMSource(document), new StreamResult(os));");
		out.println("\t\tos.close();");
		out.println("\t}");

		out.println("}");

		out.close();
	}

	public void bieJavaXML(int lv, PrintWriter out, String abie) throws Exception {
		Vector<String[]> det = bied.get(abie);
		if(det==null) return;
		for(int i=0; i<det.size(); i++) {
			String[] elems = det.get(i);
			out.println("\t\telem" + lv +" = document.createElementNS(");
			out.println(
				"\t\t\"urn:th:gov:egif:data:standard:ReusableAggregateBusinessInformationEntity:1\"");
			out.println("\t\t, \"ram:"+elems[0]+"\");");
			out.println("\t\telem" + (lv-1) +".appendChild(elem"+lv+");");
			if(elems[1]==null || elems[1].length()==0) {
				out.println("\t\telem" + lv 
					+ ".appendChild(document.createTextNode(\""+elems[2]+"\"));");
				out.println("");
			} else {
				out.println("");
				bieJavaXML(lv+1, out, elems[1]);
			}
		}
	}

	public void procBT() throws Exception {
		String btid = "";

		File dir = new File(distpath);
		if(!dir.exists()) dir.mkdirs();
		File wardir = new File(distpath+"war/");
		if(!wardir.exists()) wardir.mkdirs();
		File rmidir = new File(distpath+"rmi/");
		if(!rmidir.exists()) rmidir.mkdirs();

System.out.println("procBT");
		PrintWriter out = new PrintWriter(new FileOutputStream(distpath+"/run-script.txt"));
		Enumeration<String> k = btd.keys();
		String rsmid, send, recv,inpid;
		int rsmord;
		for(; k.hasMoreElements(); ) {
			btid = k.nextElement();
System.out.println("  "+btid);
			rsmid = btd.get(btid).get("RQD")[19];
			send = btd.get(btid).get("RQM")[5];
			recv = btd.get(btid).get("RSM")[5];
			inpid = btd.get(btid).get("RSD")[19];
			String selfhost = btd.get(btid).get("RQM")[6];
			String peerhost = btd.get(btid).get("RSM")[6];
			rsmord = rsmi.get(rsmid);
			bpWSCaller(send, rsmord);
			bpWSDL(recv, rsmord);
//			bpReqModuleGen(btid, pack(send), rsmid, pack(recv), inpid);
			port++;
			out.println("## =============================");
			out.println("## ====== request engine");
			out.println("http://" + selfhost +"/"+pack(send)+"/");
			out.println("java -jar " + pack(send) +"Rmi.jar");
			out.println("java com.dabos.rmisrv." + pack(send)+"Rmi");

			rsmid = btd.get(btid).get("RSD")[19];
			send = btd.get(btid).get("RSM")[5];
			recv = btd.get(btid).get("RQM")[5];
			inpid = btd.get(btid).get("RQD")[19];
			try {
						rsmord = rsmi.get(rsmid);
			} catch(Exception xx) {}
			bpWSCaller(send, rsmord);
			bpWSDL(recv, rsmord);
//			bpResModuleGen(btid, pack(send), rsmid, pack(recv), inpid);
			port++;
			out.println("## ====== response engine");
			out.println("http://" + peerhost +"/"+pack(send)+"/");
			out.println("java -jar " + pack(send) +"Rmi.jar");
			out.println("java com.dabos.rmisrv." + pack(send)+"Rmi");
			out.println("");
		}
		out.close();
	}

	String hex(int i) {
		int a = i % 16;
		int b = i / 16;
		char[] hh = new char[2];
		hh[0] = (b<10)? (char)((int)'0'+b) : (char)((int)'a'+(b-10));
		hh[1] = (a<10)? (char)((int)'0'+a) : (char)((int)'a'+(a-10));
		return new String(hh);
	}

	public String u2rtf(String x) throws Exception {
		byte[] bt = x.getBytes("TIS-620");
		String r = "";
		for(int i=0; i<bt.length; i++) {
			int c = 0x0ff & bt[i];
			if(c<127) {
				r += new String(bt, i, 1);
			} else {
				r += "\\'" + hex(c);
			}
		}
		return r;
	}

	String boolExp(String d) throws Exception {
		if("1".equals(d)) {
			return "YES";
		}
		return "NO";
	}

	String timeExp(String d) throws Exception {
		if(d.endsWith("S")) {
			return d.substring(0, d.length()-1) + " วินาที";
		} else if(d.endsWith("M")) {
			return d.substring(0, d.length()-1) + " นาที";
		} else if(d.endsWith("H")) {
			return d.substring(0, d.length()-1) + " ชั่วโมง";
		} else if(d.endsWith("D")) {
			return d.substring(0, d.length()-1) + " วัน";
		}
		return d+" ?";
	}

	public void procUMM() throws Exception {
		String btid = "";
		Enumeration<String> k = btd.keys();
		for(; k.hasMoreElements(); ) {
			btid = k.nextElement();

			File dir = new File(ummpath);
			if(!dir.exists()) dir.mkdirs();

			String fnm = dir+"/fig/";
			File dfnm = new File(fnm);
			if(dfnm.exists()==false) dfnm.mkdirs();

			String tnm = pack(btd.get(btid).get("DEF")[2]);
			String trqa = pack(btd.get(btid).get("RQA")[4]);
			String trsa = pack(btd.get(btid).get("RSA")[4]);
			String trqm = pack(btd.get(btid).get("RQM")[4]);
			String trsm = pack(btd.get(btid).get("RSM")[4]);
			String trqd = pack(btd.get(btid).get("RQD")[4]);
			String trsd = pack(btd.get(btid).get("RSD")[4]);
			String enm = pack(btd.get(btid).get("DEF")[3]);
			String erqa = pack(btd.get(btid).get("RQA")[5]);
			String ersa = pack(btd.get(btid).get("RSA")[5]);
			String erqm = pack(btd.get(btid).get("RQM")[5]);
			String ersm = pack(btd.get(btid).get("RSM")[5]);
			String erqd = pack(btd.get(btid).get("RQD")[5]);
			String ersd = pack(btd.get(btid).get("RSD")[5]);

			String isConcurrent = boolExp(btd.get(btid).get("DEF")[6]);
			String timeToPerform = timeExp(btd.get(btid).get("DEF")[14]);
			String businessTransactionType = btd.get(btid).get("DEF")[8];
			String isSecureTransportRequired = boolExp(btd.get(btid).get("DEF")[7]);

			String timeToRespond = timeExp(btd.get(btid).get("RQM")[17]);
			String retryCount = btd.get(btid).get("RQM")[18];

			String IsAuthorizationRequired = boolExp(btd.get(btid).get("RQM")[11]);
			String isNonRepudiationRequired = boolExp(btd.get(btid).get("RQM")[12]);
			String isNonRepudiationReceiptRequired = boolExp(btd.get(btid).get("RQM")[13]);
			String timeToAcknowledgeReceipt = timeExp(btd.get(btid).get("RQM")[14]);
			String timeToAcknowledgeProcessing = timeExp(btd.get(btid).get("RQM")[15]);
			String isIntelligibleCheckRequired = boolExp(btd.get(btid).get("RQM")[16]);
			String isConfidential = boolExp(btd.get(btid).get("RQD")[9]);
			String isTamperProof = boolExp(btd.get(btid).get("RQD")[10]);
			String isAuthenticated = boolExp(btd.get(btid).get("RQD")[11]);

			String IsAuthorizationRequiredX = boolExp(btd.get(btid).get("RSM")[11]);
			String isNonRepudiationRequiredX = boolExp(btd.get(btid).get("RSM")[12]);
			String isNonRepudiationReceiptRequiredX = boolExp(btd.get(btid).get("RSM")[13]);
			String timeToAcknowledgeReceiptX = timeExp(btd.get(btid).get("RSM")[14]);
			String timeToAcknowledgeProcessingX = timeExp(btd.get(btid).get("RSM")[15]);
			String isIntelligibleCheckRequiredX = boolExp(btd.get(btid).get("RSM")[16]);
			String isConfidentialX = boolExp(btd.get(btid).get("RSD")[9]);
			String isTamperProofX = boolExp(btd.get(btid).get("RSD")[10]);
			String isAuthenticatedX = boolExp(btd.get(btid).get("RSD")[11]);

			drawFig1(fnm+btid+"-fig1.png", tnm, trqa, trsa);
			drawFig2(fnm+btid+"-fig2.png", tnm, trqa, trsa);
			drawFig3(fnm+btid+"-fig3.png", tnm, trqa, trsa);
			drawFig4(fnm+btid+"-fig4.png", tnm);
			drawFig5(fnm+btid+"-fig5.png", tnm, trqa, trsa, trqm,trsm, trqd,trsd);
			drawFig6(fnm+btid+"-fig6.png", trqd);
			drawFig6(fnm+btid+"-fig7.png", trsd);

			drawFig1(fnm+btid+"-fig1-e.png", enm, erqa, ersa);
			drawFig2(fnm+btid+"-fig2-e.png", enm, erqa, ersa);
			drawFig3(fnm+btid+"-fig3-e.png", enm, erqa, ersa);
			drawFig4(fnm+btid+"-fig4-e.png", enm);
			drawFig5(fnm+btid+"-fig5-e.png", enm, erqa, ersa, erqm, ersm, erqd, ersd);
			drawFig6(fnm+btid+"-fig6-e.png", erqd);
			drawFig6(fnm+btid+"-fig7-e.png", ersd);

			//=================================================================
			//=================================================================
			//============================ BEGIN

			FileInputStream pis;
			String imf;
			XWPFParagraph pp;
			XWPFRun rr;

			String doc1 = dir+"/"+btid+"-UMM-report.docx";
			XWPFDocument doc = new XWPFDocument();

			pp = doc.createParagraph();
			pp.setStyle("Normal");
			rr = pp.createRun();

			rr.setText("<<BusinessCollaborationModel>>");
			rr.addBreak();

			rr.setText("name: "+ enm);
			rr.addBreak();

			rr.setText("   name: "+ tnm);
			rr.addBreak();

			rr.setText("   baseURN: urn:th:gov:egif:identifier:standard:MICT:"+ enm);
			rr.addBreak();
			rr.addBreak();

			rr = pp.createRun();
			rr.setText("1.<<BusinessDomainView>>");
			rr.addBreak();

			rr.setText("   name: "+ tnm);
			rr.addBreak();
			rr.setText("   name: "+ enm);
			rr.addBreak();
			rr.setText("   baseURN: urn:th:gov:egif:identifier:standard:MICT:BusinessDomainView:"+enm);
			rr.addBreak();
			rr.addBreak();

			rr = pp.createRun();
			rr.setText("2.<<BusinessRequirementView>>");
			rr.addBreak();
			rr.setText("   name: "+ tnm);
			rr.addBreak();
			rr.setText("   name: "+ enm);
			rr.addBreak();
			rr.setText("   baseURN: urn:th:gov:egif:identifier:standard:MICT:BusinessRequirementView:"+enm);
			rr.addBreak();
			rr.addBreak();

			rr = pp.createRun();
			rr.setText("2.1 <<CollaborationRequirementView>>"); rr.addBreak();
			rr.setText("   name: "+ tnm); rr.addBreak();
			rr.setText("   name: "+ enm); rr.addBreak();
			rr.setText("   baseURN: urn:th:gov:egif:identifier:standard:MICT:BusinessRequirementView:"+enm);
			rr.addBreak(); rr.addBreak();

			imf = btid+"-fig1.png";
			rr.addPicture(new FileInputStream(fnm+imf)
				, XWPFDocument.PICTURE_TYPE_PNG, imf, Units.toEMU(400), Units.toEMU(220));

			rr = pp.createRun();
			rr.setText("2.1.1 <<BusinessCollaborationUseCase>>");
			rr.addBreak(); rr.setText("   name: "+ tnm);
			rr.addBreak(); rr.setText("   name: "+ enm);
			rr.addBreak(); rr.setText("      definition: "+ tnm);
			rr.addBreak(); rr.setText("      beginsWhen: "+ "เมื่อ"+trqa+"ต้องการส่ง"+trqd);
			rr.addBreak(); rr.setText("      precondition: เมื่อ"+trqa+"มี"+trqd+"พร้อมส่ง");
			rr.addBreak(); rr.setText("      endsWhen: เมื่อ"+trsa+"ได้รับและตอบรับ"+trqd);
			rr.addBreak(); rr.setText("      postCondition: เมื่อ"+trsa+"ได้รับและเสร็จสิ้นกระบวนการ");
			rr.addBreak(); rr.setText("      exceptions: คือข้อมูลไม่ถูกต้อง หรือ ไม่สามารถเชื่อมโยงได้");
			rr.addBreak(); rr.setText("      actions: การดำเนินการที่เกี่ยวข้อง");
			rr.addBreak(); rr.addBreak();

			rr = pp.createRun();
			rr.setText("2.1.2 <<BusinessTransactionUseCase>>");
			rr.addBreak(); rr.setText("   name: "+ tnm);
			rr.addBreak(); rr.setText("   name: "+ enm);
			rr.addBreak(); rr.setText("      definition: "+ tnm);
			rr.addBreak(); rr.setText("      beginsWhen: "+ "เมื่อ"+trqa+"ต้องการส่ง"+trqd);
			rr.addBreak(); rr.setText("      precondition: เมื่อ"+trqa+"มี"+trqd+"พร้อมส่ง");
			rr.addBreak(); rr.setText("      endsWhen: เมื่อ"+trsa+"ได้รับและตอบรับ"+trqd);
			rr.addBreak(); rr.setText("      postCondition: เมื่อ"+trsa+"ได้รับและเสร็จสิ้นกระบวนการ");
			rr.addBreak(); rr.setText("      exceptions: คือข้อมูลไม่ถูกต้อง หรือ ไม่สามารถเชื่อมโยงได้");
			rr.addBreak(); rr.setText("      actions: การดำเนินการที่เกี่ยวข้อง");
			rr.addBreak(); rr.addBreak();

			rr = pp.createRun();
			rr.setText("2.1.3 <<AuthorizeRole>>");
			rr.addBreak(); rr.setText("   name: "+ trqa);
			rr.addBreak(); rr.setText("   name: "+ erqa);
			rr.addBreak(); rr.addBreak();

			rr = pp.createRun();
			rr.setText("2.1.4 <<AuthorizeRole>>");
			rr.addBreak(); rr.setText("   name: "+ trsa);
			rr.addBreak(); rr.setText("   name: "+ ersa);
			rr.addBreak(); rr.addBreak();

			rr = pp.createRun();
			rr.setText("2.2 <<TransactionRequirementView>>"); rr.addBreak();
			rr.setText("   name: "+ tnm); rr.addBreak();
			rr.setText("   name: "+ enm); rr.addBreak();
			rr.setText("   baseURN: urn:th:gov:egif:identifier:standard:MICT:BusinessRequirementView:"+enm);
			rr.addBreak(); rr.addBreak();

			imf = btid+"-fig2.png";
			rr.addPicture(new FileInputStream(fnm+imf)
				, XWPFDocument.PICTURE_TYPE_PNG, imf, Units.toEMU(400), Units.toEMU(220));

			rr.addBreak(); rr.addBreak();


			rr = pp.createRun();
			rr.setText("2.2.1 <<BusinessTransactionUseCase>>");
			rr.addBreak(); rr.setText("   name: "+ tnm);
			rr.addBreak(); rr.setText("   name: "+ enm);
			rr.addBreak(); rr.setText("      definition: "+ tnm);
			rr.addBreak(); rr.setText("      beginsWhen: "+ "เมื่อ"+trqa+"ต้องการส่ง"+trqd);
			rr.addBreak(); rr.setText("      precondition: เมื่อ"+trqa+"มี"+trqd+"พร้อมส่ง");
			rr.addBreak(); rr.setText("      endsWhen: เมื่อ"+trsa+"ได้รับและตอบรับ"+trqd);
			rr.addBreak(); rr.setText("      postCondition: เมื่อ"+trsa+"ได้รับและเสร็จสิ้นกระบวนการ");
			rr.addBreak(); rr.setText("      exceptions: คือข้อมูลไม่ถูกต้อง หรือ ไม่สามารถเชื่อมโยงได้");
			rr.addBreak(); rr.setText("      actions: การดำเนินการที่เกี่ยวข้อง");
			rr.addBreak(); rr.addBreak();

			rr = pp.createRun();
			rr.setText("2.2.2 <<AuthorizeRole>>");
			rr.addBreak(); rr.setText("   name: "+ trqa);
			rr.addBreak(); rr.setText("   name: "+ erqa);
			rr.addBreak(); rr.addBreak();

			rr = pp.createRun();
			rr.setText("2.2.3 <<AuthorizeRole>>");
			rr.addBreak(); rr.setText("   name: "+ trsa);
			rr.addBreak(); rr.setText("   name: "+ ersa);
			rr.addBreak(); rr.addBreak();

			rr = pp.createRun();
			rr.setText("2.3 <<CollaborationRealizationView>>"); rr.addBreak();
			rr.setText("   name: "+ tnm); rr.addBreak();
			rr.setText("   name: "+ enm); rr.addBreak();
			rr.setText("   baseURN: urn:th:gov:egif:identifier:standard:MICT:CollaborationRealizationView:"+enm);
			rr.addBreak(); rr.addBreak();

			rr = pp.createRun();
			rr.setText("3 <<BusinessTransactionView>>"); rr.addBreak();
			rr.setText("   name: "+ tnm); rr.addBreak();
			rr.setText("   name: "+ enm); rr.addBreak();
			rr.setText("   baseURN: urn:th:gov:egif:identifier:standard:MICT:BusinessTransactionView:"+enm);
			rr.addBreak(); rr.addBreak();

			imf = btid+"-fig2.png";
			rr.addPicture(new FileInputStream(fnm+imf)
				, XWPFDocument.PICTURE_TYPE_PNG, imf, Units.toEMU(400), Units.toEMU(220));
			rr.addBreak(); rr.addBreak();

			rr = pp.createRun();
			rr.setText("3.1 <<BusinessChoreographyView>>"); rr.addBreak();
			rr.setText("   name: "+ tnm); rr.addBreak();
			rr.setText("   name: "+ enm); rr.addBreak();
			rr.setText("   baseURN: urn:th:gov:egif:identifier:standard:MICT:BusinessChoreographyView:"+enm);
			rr.addBreak(); rr.addBreak();

			imf = btid+"-fig4.png";
			rr.addPicture(new FileInputStream(fnm+imf)
				, XWPFDocument.PICTURE_TYPE_PNG, imf, Units.toEMU(280), Units.toEMU(220));
			rr.addBreak(); rr.addBreak();

			rr = pp.createRun();
			rr.setText(" <<BusinessTransactionActivity>>"); rr.addBreak();
			rr.setText("   name: "+ tnm); rr.addBreak();
			rr.setText("   name: "+ enm); rr.addBreak();
			rr.setText("      timeToPerform: "+ timeToPerform);
			rr.setText("       isConcurrent: "+ isConcurrent);
			rr.addBreak(); rr.addBreak();

			rr = pp.createRun();
			rr.setText("3.2 <<BusinessInteractionView>>"); rr.addBreak();
			rr.setText("   name: "+ tnm); rr.addBreak();
			rr.setText("   name: "+ enm); rr.addBreak();
			rr.setText("   baseURN: urn:th:gov:egif:identifier:standard:MICT:BusinessInteractionView:"+enm);
			rr.addBreak(); rr.addBreak();

			imf = btid+"-fig5.png";
			rr.addPicture(new FileInputStream(fnm+imf)
				, XWPFDocument.PICTURE_TYPE_PNG, imf, Units.toEMU(400), Units.toEMU(220));
			rr.addBreak(); rr.addBreak();

			rr = pp.createRun();
			rr.setText("3.3 <<BusinessInformationView>>"); rr.addBreak();
			rr.setText("   name: "+ tnm); rr.addBreak();
			rr.setText("   name: "+ enm); rr.addBreak();
			rr.setText("   baseURN: urn:th:gov:egif:identifier:standard:MICT:BusinessInformationView:"+enm);
			rr.addBreak(); rr.addBreak();

			rr = pp.createRun();
			rr.setText("3.3.1 "+trqm); rr.addBreak();
			imf = btid+"-fig6.png";
			rr.addPicture(new FileInputStream(fnm+imf)
				, XWPFDocument.PICTURE_TYPE_PNG, imf, Units.toEMU(280), Units.toEMU(220));
			rr.addBreak(); rr.addBreak();

			rr = pp.createRun();
			rr.setText("3.3.2 "+trsm); rr.addBreak();
			imf = btid+"-fig7.png";
			rr.addPicture(new FileInputStream(fnm+imf)
				, XWPFDocument.PICTURE_TYPE_PNG, imf, Units.toEMU(280), Units.toEMU(220));
			rr.addBreak(); rr.addBreak();

/*
before 3.3.1
			out.println("\\par   <<BusinessTransaction>>");
			out.println("\\par      name: " + u2rtf(tnm));
			out.println("\\par      name: " + u2rtf(enm));
			out.println("\\par         businessTransactionType: " + u2rtf(businessTransactionType));
			out.println("\\par         isSecureTransportRequired: " + u2rtf(isSecureTransportRequired));
			out.println("\\par\\par");

			out.println("\\par   <<RequestingBusinessActivity>>");
			out.println("\\par      name: " + u2rtf(trqm));
			out.println("\\par      name: " + u2rtf(erqm));
			out.println("\\par         timeToRespond: " + u2rtf(timeToRespond));
			out.println("\\par         retryCount: " + u2rtf(retryCount));
			out.println("\\par         IsAuthorizationRequired: " + u2rtf(IsAuthorizationRequired));
			out.println("\\par         isNonRepudiationRequired: " + u2rtf(isNonRepudiationRequired));
			out.println("\\par         isNonRepudiationReceiptRequired: " + u2rtf(isNonRepudiationReceiptRequired));
			out.println("\\par         timeToAcknowledgeReceipt: " + u2rtf(timeToAcknowledgeReceipt));
			out.println("\\par         timeToAcknowledgeProcessing: " + u2rtf(timeToAcknowledgeProcessing));
			out.println("\\par         isIntelligibleCheckRequired: " + u2rtf(isIntelligibleCheckRequired));
			out.println("\\par\\par");

			out.println("\\par   <<RespondingBusinessActivity>>");
			out.println("\\par      name: " + u2rtf(trsm));
			out.println("\\par      name: " + u2rtf(ersm));
			out.println("\\par         IsAuthorizationRequired: " + u2rtf(IsAuthorizationRequiredX));
			out.println("\\par         isNonRepudiationRequired: " + u2rtf(isNonRepudiationRequiredX));
			out.println("\\par         isNonRepudiationReceiptRequired: " + u2rtf(isNonRepudiationReceiptRequiredX));
			out.println("\\par         timeToAcknowledgeReceipt: " + u2rtf(timeToAcknowledgeReceiptX));
			out.println("\\par         timeToAcknowledgeProcessing: " + u2rtf(timeToAcknowledgeProcessingX));
			out.println("\\par         isIntelligibleCheckRequired: " + u2rtf(isIntelligibleCheckRequiredX));
			out.println("\\par\\par");

after 3.3.1 pict
			out.println("\\par   <<InformationEntity>>");
			out.println("\\par      name: " + u2rtf(trqd));
			out.println("\\par      name: " + u2rtf(erqd));
			out.println("\\par         isConfidential: " + u2rtf(isConfidentialX));
			out.println("\\par         isTamperProof: " + u2rtf(isTamperProofX));
			out.println("\\par         isAuthenticated: " + u2rtf(isAuthenticatedX));
			out.println("\\par\\par");

			out.println("{\\par\\b 3.3.2" + trsm +"}\\par");
			out.print("{\\*\\shppict {\\pict \\pngblip ");
			pis = new FileInputStream(fnm+btid+"-fig7.png");
			while( (x=pis.read())>=0 ) {out.print(hex(x));}
			pis.close();out.print("}}");
			out.println("\\par\\par");

after 3.3.2 pict
			out.println("\\par   <<InformationEntity>>");
			out.println("\\par      name: " + u2rtf(trsd));
			out.println("\\par      name: " + u2rtf(ersd));
			out.println("\\par         isConfidential: " + u2rtf(isConfidentialX));
			out.println("\\par         isTamperProof: " + u2rtf(isTamperProofX));
			out.println("\\par         isAuthenticated: " + u2rtf(isAuthenticatedX));
			out.println("\\par\\par");

			out.println("}");
			out.close();
*/
			try (FileOutputStream fos = new FileOutputStream(doc1)) {
				doc.write(fos);
			}

			//============================ END
			//=================================================================
			//=================================================================



/*
			//=================================================================
			//=================================================================
			//============================ BEGIN

			String filename = dir+"/"+btid+"-UMM-report.rtf";
			FileOutputStream fos = new FileOutputStream(filename);
			PrintWriter out = new PrintWriter(fos);
			int x;

			out.println("{\\rtf");

			out.println("{\\par\\b<<BusinessCollaborationModel>>}");
			out.println("\\par   name: " + u2rtf(tnm));
			out.println("\\par   name: " + u2rtf(enm));
			out.println("\\par   baseURN: " 
				+ "urn:th:gov:egif:identifier:standard:MICT:" + u2rtf(enm));
			out.println("\\par\\par");

			out.println("{\\par\\b 1.<<BusinessDomainView>>}");
			out.println("\\par   name: " + u2rtf(tnm));
			out.println("\\par   name: " + u2rtf(enm));
			out.println("\\par   baseURN: " 
				+ "urn:th:gov:egif:identifier:standard:MICT:BusinessDomainView:" + u2rtf(enm));
			out.println("\\par\\par");

			out.println("{\\par\\b 2.<<BusinessRequirementView>>}");
			out.println("\\par   name: " + u2rtf(tnm));
			out.println("\\par   name: " + u2rtf(enm));
			out.println("\\par   baseURN: " 
				+ "urn:th:gov:egif:identifier:standard:MICT:BusinessRequirementView:" + u2rtf(enm));
			out.println("\\par\\par");

			out.println("{\\par\\b 2.1.<<CollaborationRequirementView>>}");
			out.println("\\par   name: " + u2rtf(tnm));
			out.println("\\par   name: " + u2rtf(enm));
			out.println("\\par   baseURN: " 
				+ "urn:th:gov:egif:identifier:standard:MICT:BusinessRequirementView:" + u2rtf(enm));

			out.print("{\\*\\shppict {\\pict \\pngblip ");
			pis = new FileInputStream(fnm+btid+"-fig1.png");
			while( (x=pis.read())>=0 ) {out.print(hex(x));}
			pis.close();out.print("}}");
			out.println("\\par\\par");

			out.println("{\\par\\b 2.1.1 <<BusinessCollaborationUseCase>>}");
			out.println("\\par   name: " + u2rtf(tnm));
			out.println("\\par   name: " + u2rtf(enm));
			out.println("\\par      definition: " + u2rtf(tnm));
			out.println("\\par      beginsWhen: " + u2rtf("เมื่อ"+trqa+"ต้องการส่ง"+trqd));
			out.println("\\par      precondition: " + u2rtf("เมื่อ"+trqa+"มี"+trqd+"พร้อมส่ง"));
			out.println("\\par      endsWhen: " + u2rtf("เมื่อ"+trsa+"ได้รับและตอบรับ"+trqd));
			out.println("\\par      postCondition: " + u2rtf("เมื่อ"+trsa+"ได้รับและเสร็จสิ้นกระบวนการ"));
			out.println("\\par      exceptions: " + u2rtf("คือข้อมูลไม่ถูกต้อง หรือ ไม่สามารถเชื่อมโยงได้"));
			out.println("\\par      actions: " + u2rtf("การดำเนินการที่เกี่ยวข้อง"));
			out.println("\\par\\par");

			out.println("{\\par\\b 2.1.2 <<BusinessTransactionUseCase>>}");
			out.println("\\par   name: " + u2rtf(tnm));
			out.println("\\par   name: " + u2rtf(enm));
			out.println("\\par      definition: " + u2rtf(tnm));
			out.println("\\par      beginsWhen: " + u2rtf("เมื่อ"+trqa+"ต้องการส่ง"+trqd));
			out.println("\\par      precondition: " + u2rtf("เมื่อ"+trqa+"มี"+trqd+"พร้อมส่ง"));
			out.println("\\par      endsWhen: " + u2rtf("เมื่อ"+trsa+"ได้รับและตอบรับ"+trqd));
			out.println("\\par      postCondition: " + u2rtf("เมื่อ"+trsa+"ได้รับและเสร็จสิ้นกระบวนการ"));
			out.println("\\par      exceptions: " + u2rtf("คือข้อมูลไม่ถูกต้อง หรือ ไม่สามารถเชื่อมโยงได้"));
			out.println("\\par      actions: " + u2rtf("การดำเนินการที่เกี่ยวข้อง"));
			out.println("\\par\\par");

			out.println("{\\par\\b 2.1.3 <<AuthorizeRole>>}");
			out.println("\\par   name: " + u2rtf(trqa));
			out.println("\\par   name: " + u2rtf(erqa));
			out.println("{\\par\\b 2.1.4<<AuthorizeRole>>}");
			out.println("\\par   name: " + u2rtf(trsa));
			out.println("\\par   name: " + u2rtf(ersa));
			out.println("\\par\\par");

			/// ===========2.2
			out.println("{\\par\\b 2.2 <<TransactionRequirementView>>}");
			out.println("\\par   name: " + u2rtf(tnm));
			out.println("\\par   name: " + u2rtf(enm));
			out.println("\\par   baseURN: " 
				+ "urn:th:gov:egif:identifier:standard:MICT:BusinessRequirementView:" + u2rtf(enm));

			out.print("{\\*\\shppict {\\pict \\pngblip ");
			pis = new FileInputStream(fnm+btid+"-fig2.png");
			while( (x=pis.read())>=0 ) {out.print(hex(x));}
			pis.close();out.print("}}");
			out.println("\\par\\par");

			out.println("{\\par\\b 2.2.1 <<BusinessTransactionUseCase>>}");
			out.println("\\par   name: " + u2rtf(tnm));
			out.println("\\par   name: " + u2rtf(enm));
			out.println("\\par      definition: " + u2rtf(tnm));
			out.println("\\par      beginsWhen: " + u2rtf("เมื่อ"+trqa+"ต้องการส่ง"+trqd));
			out.println("\\par      precondition: " + u2rtf("เมื่อ"+trqa+"มี"+trqd+"พร้อมส่ง"));
			out.println("\\par      endsWhen: " + u2rtf("เมื่อ"+trsa+"ได้รับและตอบรับ"+trqd));
			out.println("\\par      postCondition: " + u2rtf("เมื่อ"+trsa+"ได้รับและเสร็จสิ้นกระบวนการ"));
			out.println("\\par      exceptions: " + u2rtf("คือข้อมูลไม่ถูกต้อง หรือ ไม่สามารถเชื่อมโยงได้"));
			out.println("\\par      actions: " + u2rtf("การดำเนินการที่เกี่ยวข้อง"));
			out.println("\\par\\par");

			out.println("{\\par\\b 2.2.2 <<AuthorizeRole>>}");
			out.println("\\par   name: " + u2rtf(trqa));
			out.println("\\par   name: " + u2rtf(erqa));
			out.println("\\par\\par");

			out.println("{\\par\\b 2.2.3<<AuthorizeRole>>}");
			out.println("\\par   name: " + u2rtf(trsa));
			out.println("\\par   name: " + u2rtf(ersa));
			out.println("\\par\\par");

			/// ===========2.3
			out.println("{\\par\\b 2.3 <<CollaborationRealizationView>>}");
			out.println("\\par   name: " + u2rtf(tnm));
			out.println("\\par   name: " + u2rtf(enm));
			out.println("\\par   baseURN: " 
				+ "urn:th:gov:egif:identifier:standard:MICT:BusinessRequirementView:" + u2rtf(enm));
			out.println("\\par\\par");

			out.println("{\\par\\b 3.<<BusinessTransactionView>>}");
			out.println("\\par   name: " + u2rtf(tnm));
			out.println("\\par   name: " + u2rtf(enm));
			out.println("\\par   baseURN: " 
				+ "urn:th:gov:egif:identifier:standard:MICT:BusinessRequirementView:" + u2rtf(enm));
			out.println("\\par\\par");

			out.print("{\\*\\shppict {\\pict \\pngblip ");
			pis = new FileInputStream(fnm+btid+"-fig3.png");
			while( (x=pis.read())>=0 ) {out.print(hex(x));}
			pis.close();out.print("}}");
			out.println("\\par\\par");

			out.println("{\\par\\b 3.1.<<Business Choreography View>>}");
			out.println("\\par   name: " + u2rtf(tnm));
			out.println("\\par   name: " + u2rtf(enm));
			out.println("\\par   baseURN: " 
				+ "urn:th:gov:egif:identifier:standard:MICT:BusinessRequirementView:" + u2rtf(enm));
			out.println("\\par\\par");

			out.print("{\\*\\shppict {\\pict \\pngblip ");
			pis = new FileInputStream(fnm+btid+"-fig4.png");
			while( (x=pis.read())>=0 ) {out.print(hex(x));}
			pis.close();out.print("}}");
			out.println("\\par\\par");

			out.println("\\par   <<BusinessTransactionActivity>>");
			out.println("\\par      name: " + u2rtf(tnm));
			out.println("\\par      name: " + u2rtf(enm));
			out.println("\\par         timeToPerform: " + u2rtf(timeToPerform));
			out.println("\\par         isConcurrent: " + u2rtf(isConcurrent));
			out.println("\\par\\par");

			out.println("{\\par\\b 3.2.<<Business Interaction View>>}");
			out.println("\\par   name: " + u2rtf(tnm));
			out.println("\\par   name: " + u2rtf(enm));
			out.println("\\par   baseURN: " 
				+ "urn:th:gov:egif:identifier:standard:MICT:BusinessRequirementView:" + u2rtf(enm));
			out.println("\\par\\par");

			out.print("{\\*\\shppict {\\pict \\pngblip ");
			pis = new FileInputStream(fnm+btid+"-fig5.png");
			while( (x=pis.read())>=0 ) {out.print(hex(x));}
			pis.close();out.print("}}");
			out.println("\\par\\par");

			out.println("\\par   <<BusinessTransaction>>");
			out.println("\\par      name: " + u2rtf(tnm));
			out.println("\\par      name: " + u2rtf(enm));
			out.println("\\par         businessTransactionType: " + u2rtf(businessTransactionType));
			out.println("\\par         isSecureTransportRequired: " + u2rtf(isSecureTransportRequired));
			out.println("\\par\\par");

			out.println("\\par   <<RequestingBusinessActivity>>");
			out.println("\\par      name: " + u2rtf(trqm));
			out.println("\\par      name: " + u2rtf(erqm));
			out.println("\\par         timeToRespond: " + u2rtf(timeToRespond));
			out.println("\\par         retryCount: " + u2rtf(retryCount));
			out.println("\\par         IsAuthorizationRequired: " + u2rtf(IsAuthorizationRequired));
			out.println("\\par         isNonRepudiationRequired: " + u2rtf(isNonRepudiationRequired));
			out.println("\\par         isNonRepudiationReceiptRequired: " + u2rtf(isNonRepudiationReceiptRequired));
			out.println("\\par         timeToAcknowledgeReceipt: " + u2rtf(timeToAcknowledgeReceipt));
			out.println("\\par         timeToAcknowledgeProcessing: " + u2rtf(timeToAcknowledgeProcessing));
			out.println("\\par         isIntelligibleCheckRequired: " + u2rtf(isIntelligibleCheckRequired));
			out.println("\\par\\par");

			out.println("\\par   <<RespondingBusinessActivity>>");
			out.println("\\par      name: " + u2rtf(trsm));
			out.println("\\par      name: " + u2rtf(ersm));
			out.println("\\par         IsAuthorizationRequired: " + u2rtf(IsAuthorizationRequiredX));
			out.println("\\par         isNonRepudiationRequired: " + u2rtf(isNonRepudiationRequiredX));
			out.println("\\par         isNonRepudiationReceiptRequired: " + u2rtf(isNonRepudiationReceiptRequiredX));
			out.println("\\par         timeToAcknowledgeReceipt: " + u2rtf(timeToAcknowledgeReceiptX));
			out.println("\\par         timeToAcknowledgeProcessing: " + u2rtf(timeToAcknowledgeProcessingX));
			out.println("\\par         isIntelligibleCheckRequired: " + u2rtf(isIntelligibleCheckRequiredX));
			out.println("\\par\\par");

			out.println("{\\par\\b 3.3.<<Business Information View>>}");
			out.println("\\par   name: " + u2rtf(tnm));
			out.println("\\par   name: " + u2rtf(enm));
			out.println("\\par   baseURN: " 
				+ "urn:th:gov:egif:identifier:standard:MICT:BusinessRequirementView:" + u2rtf(enm));
			out.println("\\par\\par");

			out.println("{\\par\\b 3.3.1" + trqm +"}\\par");
			out.println("\\par\\par");
			out.print("{\\*\\shppict {\\pict \\pngblip ");
			pis = new FileInputStream(fnm+btid+"-fig6.png");
			while( (x=pis.read())>=0 ) {out.print(hex(x));}
			pis.close();out.print("}}");
			out.println("\\par\\par");

			out.println("\\par   <<InformationEntity>>");
			out.println("\\par      name: " + u2rtf(trqd));
			out.println("\\par      name: " + u2rtf(erqd));
			out.println("\\par         isConfidential: " + u2rtf(isConfidentialX));
			out.println("\\par         isTamperProof: " + u2rtf(isTamperProofX));
			out.println("\\par         isAuthenticated: " + u2rtf(isAuthenticatedX));
			out.println("\\par\\par");

			out.println("{\\par\\b 3.3.2" + trsm +"}\\par");
			out.print("{\\*\\shppict {\\pict \\pngblip ");
			pis = new FileInputStream(fnm+btid+"-fig7.png");
			while( (x=pis.read())>=0 ) {out.print(hex(x));}
			pis.close();out.print("}}");
			out.println("\\par\\par");

			out.println("\\par   <<InformationEntity>>");
			out.println("\\par      name: " + u2rtf(trsd));
			out.println("\\par      name: " + u2rtf(ersd));
			out.println("\\par         isConfidential: " + u2rtf(isConfidentialX));
			out.println("\\par         isTamperProof: " + u2rtf(isTamperProofX));
			out.println("\\par         isAuthenticated: " + u2rtf(isAuthenticatedX));
			out.println("\\par\\par");

			out.println("}");
			out.close();

			//============================ END
			//=================================================================
			//=================================================================
*/
		}
	}

	int mg = 4, cpw=35;

	public void drawFig3(String fname, String nm, String rqa, String rsa) {
		try {
			Point pkg = new Point(mg+600,300);
			Point uco = new Point(15,30);
			Point ucn = new Point(20,50);
			Rectangle us1 = new Rectangle(mg+180, mg+50, 250, 80);
			Rectangle us2 = new Rectangle(mg+180, mg+190, 250, 80);
			BufferedImage bufferedImage = new BufferedImage(
				pkg.x+2*mg, pkg.y+2*mg, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = bufferedImage.createGraphics();

			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, pkg.x+2*mg, pkg.y+2*mg);
			g2d.setColor(Color.black);
			g2d.drawRect(mg, mg, pkg.x, pkg.y);
			g2d.drawLine(mg, mg+cpw, pkg.x+mg, mg+cpw);
			g2d.drawString("<<Collaboration Realization View>>", vnm.x, vnm.y);
			g2d.drawString(nm, vnm.x, vnm.y+14);

			String lb = "<<BusinessCollaborationRealization>>";
			g2d.drawArc(us1.x, us1.y, us1.width, us1.height, 0, 360);
			drawText(g2d, lb, us1.x+us1.width/2, us1.y+uco.y);
			drawText(g2d, nm, us1.x+us1.width/2, us1.y+ucn.y);

			lb = "<<BusinessCollaborationUseCase>>";
			g2d.drawArc(us2.x, us2.y, us2.width, us2.height, 0, 360);
			drawText(g2d, lb, us2.x+us2.width/2, us2.y+uco.y);
			drawText(g2d, nm, us2.x+us2.width/2, us2.y+ucn.y);

			g2d.drawLine(us1.x+us1.width/2, us1.y+us1.height, us1.x+us1.width/2, us2.y);
			int y2 = (us1.y+us1.height + us2.y)/2;
			drawText(g2d, "<<realize>>", us1.x+us1.width/2, y2);
			Point apt = new Point(us1.x+us1.width/2, us2.y);
			drawArrow(g2d, apt, BOTTOM);

			g2d.drawLine(mg+100, us1.y+us1.height, mg+100, us2.y);
			drawText(g2d, "<<mapTo>>", mg+100, y2);
			apt = new Point(mg+100, us2.y);
			drawArrow(g2d, apt, BOTTOM);

			g2d.drawLine(mg+520, us1.y+us1.height, mg+520, us2.y);
			drawText(g2d, "<<mapTo>>", mg+520, y2);
			apt = new Point(mg+520, us2.y);
			drawArrow(g2d, apt, BOTTOM);

			drawActor(g2d, mg+100, mg+70);
			drawText(g2d, rqa, mg+100, mg+70+60);
			g2d.drawLine(mg+100+15, mg+70+20, us1.x, mg+70+20);
			apt = new Point(us1.x, mg+70+20);
			drawArrow(g2d, apt, RIGHT);

			drawActor(g2d, mg+520, mg+70);
			drawText(g2d, rsa, mg+520, mg+70+60);
			g2d.drawLine(mg+520-15, mg+70+20, us1.x+us1.width, mg+70+20);
			apt = new Point(us1.x+us1.width, mg+70+20);
			drawArrow(g2d, apt, LEFT);

			drawActor(g2d, mg+520, mg+210);
			drawText(g2d, rsa, mg+520, mg+210+60);
			g2d.drawLine(mg+520-15, mg+210+20, us1.x+us1.width, mg+210+20);
			apt = new Point(us1.x+us1.width, mg+210+20);
			drawArrow(g2d, apt, LEFT);

			drawActor(g2d, mg+100, mg+210);
			drawText(g2d, rqa, mg+100, mg+210+60);
			g2d.drawLine(mg+100+15, mg+210+20, us1.x, mg+210+20);
			apt = new Point(us1.x, mg+210+20);
			drawArrow(g2d, apt, RIGHT);

			g2d.dispose();
			File file = new File(fname);
			ImageIO.write(bufferedImage, "png", file);
		} catch(Exception exg1) {
System.out.println("ex7:" + exg1);
		}
	}

	public void drawFig1(String fname, String nm, String rqa, String rsa) {
		try {
			Point pkg = new Point(mg+600,300);
			Point uco = new Point(15,30);
			Point ucn = new Point(20,50);
			Rectangle us1 = new Rectangle(mg+180, mg+50, 250, 80);
			Rectangle us2 = new Rectangle(mg+180, mg+190, 250, 80);
			BufferedImage bufferedImage = new BufferedImage(
				pkg.x+2*mg, pkg.y+2*mg, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = bufferedImage.createGraphics();

			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, pkg.x+2*mg, pkg.y+2*mg);
			g2d.setColor(Color.black);
			g2d.drawRect(mg, mg, pkg.x, pkg.y);
			g2d.drawLine(mg, mg+cpw, pkg.x+mg, mg+cpw);
			g2d.drawString("<<Collaboration Requirement View>>", vnm.x, vnm.y);
			g2d.drawString(nm, vnm.x, vnm.y+14);

			String lb = "<<BusinessCollaborationUseCase>>";
			g2d.drawArc(us1.x, us1.y, us1.width, us1.height, 0, 360);
			drawText(g2d, lb, us1.x+us1.width/2, us1.y+uco.y);
			drawText(g2d, nm, us1.x+us1.width/2, us1.y+ucn.y);

			lb = "<<BusinessTransactionUseCase>>";
			g2d.drawArc(us2.x, us2.y, us2.width, us2.height, 0, 360);
			drawText(g2d, lb, us2.x+us2.width/2, us2.y+uco.y);
			drawText(g2d, nm, us2.x+us2.width/2, us2.y+ucn.y);

			g2d.drawLine(us1.x+us1.width/2, us1.y+us1.height, us1.x+us1.width/2, us2.y);
			int y2 = (us1.y+us1.height + us2.y)/2;
			drawText(g2d, "<<include>>", us1.x+us1.width/2, y2);
			Point apt = new Point(us1.x+us1.width/2, us2.y);
			drawArrow(g2d, apt, BOTTOM);

			g2d.drawLine(mg+100, us1.y+us1.height, mg+100, us2.y);
			drawText(g2d, "<<mapTo>>", mg+100, y2);
			apt = new Point(mg+100, us2.y);
			drawArrow(g2d, apt, BOTTOM);

			g2d.drawLine(mg+520, us1.y+us1.height, mg+520, us2.y);
			drawText(g2d, "<<mapTo>>", mg+520, y2);
			apt = new Point(mg+520, us2.y);
			drawArrow(g2d, apt, BOTTOM);

			drawActor(g2d, mg+100, mg+70);
			drawText(g2d, rqa, mg+100, mg+70+60);
			g2d.drawLine(mg+100+15, mg+70+20, us1.x, mg+70+20);
			apt = new Point(us1.x, mg+70+20);
			drawArrow(g2d, apt, RIGHT);

			drawActor(g2d, mg+520, mg+70);
			drawText(g2d, rsa, mg+520, mg+70+60);
			g2d.drawLine(mg+520-15, mg+70+20, us1.x+us1.width, mg+70+20);
			apt = new Point(us1.x+us1.width, mg+70+20);
			drawArrow(g2d, apt, LEFT);

			drawActor(g2d, mg+520, mg+210);
			drawText(g2d, rsa, mg+520, mg+210+60);
			g2d.drawLine(mg+520-15, mg+210+20, us1.x+us1.width, mg+210+20);
			apt = new Point(us1.x+us1.width, mg+210+20);
			drawArrow(g2d, apt, LEFT);

			drawActor(g2d, mg+100, mg+210);
			drawText(g2d, rqa, mg+100, mg+210+60);
			g2d.drawLine(mg+100+15, mg+210+20, us1.x, mg+210+20);
			apt = new Point(us1.x, mg+210+20);
			drawArrow(g2d, apt, RIGHT);

			g2d.dispose();
			File file = new File(fname);
			ImageIO.write(bufferedImage, "png", file);
		} catch(Exception exg1) {
System.out.println("ex8:" + exg1);
		}
	}

	public void drawFig2(String fname, String nm, String rqa, String rsa) {
		try {
			Point pkg = new Point(mg+600,180);
			Point uco = new Point(15,30);
			Point ucn = new Point(20,50);
			Rectangle us1 = new Rectangle(mg+180, mg+50, 250, 80);
			Rectangle us2 = new Rectangle(mg+180, mg+190, 250, 80);
			BufferedImage bufferedImage = new BufferedImage(
				pkg.x+2*mg, pkg.y+2*mg, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = bufferedImage.createGraphics();

			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, pkg.x+2*mg, pkg.y+2*mg);
			g2d.setColor(Color.black);
			g2d.drawRect(mg, mg, pkg.x, pkg.y);
			g2d.drawLine(mg, mg+cpw, pkg.x+mg, mg+cpw);
			g2d.drawString("<<Transaction Requirement View>>", vnm.x, vnm.y);
			g2d.drawString(nm, vnm.x, vnm.y+14);

			String lb = "<<BusinessTransactionUseCase>>";
			g2d.drawArc(us1.x, us1.y, us1.width, us1.height, 0, 360);
			drawText(g2d, lb, us1.x+us1.width/2, us1.y+uco.y);
			drawText(g2d, nm, us1.x+us1.width/2, us1.y+ucn.y);

			drawActor(g2d, mg+100, mg+70);
			drawText(g2d, rqa, mg+100, mg+70+60);
			g2d.drawLine(mg+100+15, mg+70+20, us1.x, mg+70+20);
			Point apt = new Point(us1.x, mg+70+20);
			drawArrow(g2d, apt, RIGHT);

			drawActor(g2d, mg+520, mg+70);
			drawText(g2d, rsa, mg+520, mg+70+60);
			g2d.drawLine(mg+520-15, mg+70+20, us1.x+us1.width, mg+70+20);
			apt = new Point(us1.x+us1.width, mg+70+20);
			drawArrow(g2d, apt, LEFT);

			g2d.dispose();
			File file = new File(fname);
			ImageIO.write(bufferedImage, "png", file);
		} catch(Exception exg1) {
System.out.println("ex9:" + exg1);
		}
	}

	public void drawFig4(String fname, String nm) {
		try {
			Point pkg = new Point(mg+450,350);
			Point uco = new Point(15,30);
			Point ucn = new Point(20,50);
			Rectangle us1 = new Rectangle(mg+180, mg+140, 250, 80);
			BufferedImage bufferedImage = new BufferedImage(
				pkg.x+2*mg, pkg.y+2*mg, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = bufferedImage.createGraphics();

			int cvl = 20, dst = 80, ccr = 13, bw = 3;

			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, pkg.x+2*mg, pkg.y+2*mg);
			g2d.setColor(Color.black);
			g2d.drawRect(mg, mg, pkg.x, pkg.y);
			g2d.drawLine(mg, mg+cpw, pkg.x+mg, mg+cpw);
			g2d.drawString("<<BusinessCollaborationProtocol>>", vnm.x, vnm.y);
			g2d.drawString(nm, vnm.x, vnm.y+14);

			String lb = "<<BusinessTransactionActivity>>";

			drawRoundRect(g2d, us1, cvl);
			drawText(g2d, lb, us1.x+us1.width/2, us1.y+uco.y);
			drawText(g2d, nm, us1.x+us1.width/2, us1.y+ucn.y);

			g2d.fillArc(us1.x+us1.width/2-ccr, us1.y-dst, ccr*2, ccr*2, 0, 360);
			g2d.drawLine(us1.x+us1.width/2, us1.y-dst+ccr*2, us1.x+us1.width/2, us1.y);
			Point apt = new Point(us1.x+us1.width/2, us1.y);
			drawArrow(g2d, apt, BOTTOM);
			drawText(g2d, "[start]", us1.x+us1.width/2, us1.y-dst/2+ccr);

			g2d.drawArc(us1.x+us1.width/2-ccr, us1.y+us1.height+dst-ccr, ccr*2, ccr*2, 0, 360);
			g2d.fillArc(us1.x+us1.width/2-ccr+bw
				, us1.y+us1.height+dst-ccr+bw, (ccr-bw)*2, (ccr-bw)*2, 0, 360);
			g2d.drawLine(us1.x+us1.width/2, us1.y+us1.height
				, us1.x+us1.width/2, us1.y+us1.height+dst-ccr);
			apt = new Point(us1.x+us1.width/2, us1.y+us1.height+dst-ccr);
			drawArrow(g2d, apt, BOTTOM);
			drawText(g2d, "[success]", us1.x+us1.width/2, us1.y+us1.height+dst/2);

			g2d.drawArc(mg+40, us1.y+us1.height/2-ccr, ccr*2, ccr*2, 0, 360);
			g2d.fillArc(mg+40+bw, us1.y+us1.height/2-ccr+bw, (ccr-bw)*2, (ccr-bw)*2, 0, 360);
			g2d.drawLine(us1.x, us1.y+us1.height/2, mg+40+ccr*2, us1.y+us1.height/2);
			apt = new Point(mg+40+ccr*2, us1.y+us1.height/2);
			drawArrow(g2d, apt, LEFT);
			drawText(g2d, "[failure]", (us1.x+mg+40+ccr*2)/2, us1.y+us1.height/2);

			g2d.dispose();
			File file = new File(fname);
			ImageIO.write(bufferedImage, "png", file);
		} catch(Exception exg1) {
System.out.println("ex10:" + exg1);
		}
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

	Point vnm = new Point(mg+10, mg+15);

	public void drawFig6(String fname, String rqd) {
		try {
			Point pkg = new Point(mg+300,280);
			BufferedImage bufferedImage = new BufferedImage(
				pkg.x+2*mg, pkg.y+2*mg, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = bufferedImage.createGraphics();

			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, pkg.x+2*mg, pkg.y+2*mg);
			g2d.setColor(Color.black);
			g2d.drawRect(mg, mg, pkg.x, pkg.y);
			g2d.drawLine(mg, mg+cpw, pkg.x+mg, mg+cpw);
			g2d.drawString("<<Business Information View>> ", vnm.x, vnm.y);
			g2d.drawString(rqd, vnm.x, vnm.y+12);

			Rectangle pc1 = new Rectangle(mg+30, mg+cpw+30, 220, 60);
			drawRoundRect(g2d, pc1, 0);
			g2d.drawLine(pc1.x, pc1.y+cpw, pc1.x+pc1.width, pc1.y+cpw);
			drawText(g2d, "<<InformationEnvelope>>", pc1.x+pc1.width/2, pc1.y+14);
			drawText(g2d, rqd, pc1.x+pc1.width/2, pc1.y+26);

			Rectangle pc2 = new Rectangle(mg+30, mg+cpw+150, 220, 60);
			drawRoundRect(g2d, pc2, 0);
			g2d.drawLine(pc2.x, pc2.y+cpw, pc2.x+pc2.width, pc2.y+cpw);
			drawText(g2d, "<<InformationEntity>>", pc2.x+pc2.width/2, pc2.y+14);
			drawText(g2d, rqd, pc2.x+pc2.width/2, pc2.y+26);

			g2d.drawLine(pc1.x+pc1.width/2, pc1.y+pc1.height, pc1.x+pc1.width/2, pc2.y);

			g2d.dispose();
			File file = new File(fname);
			ImageIO.write(bufferedImage, "png", file);
		} catch(Exception exg1) {
System.out.println("ex12:" + exg1);
		}
	}

	public void drawFig5(String fname, String nm, String rqa, String rsa
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
			File file = new File(fname);
			ImageIO.write(bufferedImage, "png", file);
		} catch(Exception exg1) {
System.out.println("ex13:" + exg1);
		}
	}

	public static int TOP = 1;
	public static int LEFT = 2;
	public static int RIGHT = 3;
	public static int BOTTOM = 4;
	public static int CENTER = 4;

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

	public Rectangle2D textRect(Graphics2D g2d, String txt) {
			FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
			return metrics.getStringBounds(txt, g2d);
	}

	public void drawActor(Graphics2D g2d, int x, int y) {
			g2d.drawArc(x-8, y-8, 16, 16, 0, 360);
			g2d.drawLine(x, y+8, x, y+28);
			g2d.drawLine(x-10, y+12, x+10, y+12);
			g2d.drawLine(x, y+28, x-10, y+28+12);
			g2d.drawLine(x, y+28, x+10, y+28+12);
	}

	public String buildTagName(String ptq, String pt, String rt, String asq, String ast) {
		if("Binary Object".equals(rt)) rt = "BinaryObject";
		if("Text".equals(rt)) rt = "";
		if(pt.endsWith(rt)) rt = "";
		String t1 = pack(ptq) + pack(pt);
		String t2 = pack(asq) + pack(ast);
		if(t2.startsWith(t1)) t1 = "";
		if(t1.endsWith(t2)) t2 = "";
		return t1 + t2 + rt;
	}

	public void procQDT() throws Exception {
		for(int i=0; i<qdt.size(); i++) {
			String[] words = qdt.get(i);
//			System.out.println("QDT:"+words[0]);
			if("Code list".equals(words[1])) {
				qdtCodeList(words[0], words);
			} else if("ID list".equals(words[1])) {
				qdtIDList(words[0], words);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void proc3() {
		Hashtable<String,Integer> octh = new Hashtable<String,Integer>();
		Enumeration<String> key = abie.keys();

		int abie_cnt = 0;
		while(key.hasMoreElements()) {
			String xx = key.nextElement();
			String[] anm = xx.split("___");
			if(anm.length<2) continue;
			abie_cnt++;
//			System.out.println("abie:\t:"+anm[0]+"_\t"+anm[1]);
			Integer ii = octh.get(anm[1]);
			if(ii==null) {
				octh.put(anm[1], Integer.valueOf(1));
			} else {
				octh.put(anm[1], Integer.valueOf(ii + 1));
			}
		}

		int acc_cnt = 0;
		key = octh.keys();
		for(int i=0; key.hasMoreElements(); i++) {
			acc_cnt++;
			String xx = key.nextElement();
			if(octh.get(xx)==1) continue;
			System.out.println((i+1)+"\t:"+xx+"\t"+ octh.get(xx));
		}

		System.out.println("acc count \t:"+acc_cnt);
		System.out.println("abie count \t:"+abie_cnt);

		System.out.println("-----------------------------");
		Hashtable<String,Integer> qdth = new Hashtable<String,Integer>();
		int qdt_cnt = 0;

		for(int i=0; i<qdt.size(); i++) {
			String[] words = qdt.get(i);
			if(words.length<4) continue;
			qdt_cnt++;
			Integer ii = qdth.get(words[3]);
			if(ii==null) {
//				if("Education Level".equals(words[3]))
//					System.out.println((i+1)+":\t"+words[0]+":\t"+words[3]);
				qdth.put(words[3], Integer.valueOf(1));
			} else {
				System.out.println((i+1)+":"+(ii+1)+"\t"+words[0]+":\t"+words[3]);
				qdth.put(words[3], Integer.valueOf(ii + 1));
			}
		}

		System.out.println("-----------------------------");
		key = qdth.keys();
		for(int i=0; key.hasMoreElements(); i++) {
			String xx = key.nextElement();
			if(qdth.get(xx)==1) continue;
			System.out.println((i+1)+"\t:"+xx+"\t"+ qdth.get(xx));
		}

		System.out.println("------------QDT reuse -----------------");
		key = qdtru.keys();
		for(int i=0; key.hasMoreElements(); i++) {
			String xx = key.nextElement();
//			if(qdtru.get(xx)==1) continue;
			System.out.println((i+1)+"\t:"+xx+"\t"+ qdtru.get(xx));
		}

		System.out.println("------------QDT cross reuse -----------------");
		key = qdtrux.keys();
		for(int i=0; key.hasMoreElements(); i++) {
			String xx = key.nextElement();
//			if(qdtrux.get(xx)==1) continue;
			System.out.println((i+1)+"\t:"+xx+"\t"+ qdtrux.get(xx));
		}

		System.out.println("number of qdt:\t"+ qdt.size());
	}

	Hashtable<String,Integer> qdtru = new Hashtable<String,Integer>();
	Hashtable<String,Integer> qdtrux = new Hashtable<String,Integer>();

	public void gen(String dir) {
		try {
			System.out.println("THEGifProc: "+dir);
			clmpath = dir+"/egif/out/codelist/";
			idmpath = dir+"/egif/out/identifierlist/";
			ummpath = dir+"/egif/out/umm/";
			ssmpath = dir+"/egif/out/share/";
			rsmpath = dir+"/egif/out/rsm/";
			wsdlpath = dir+"/egif/out/wsdl/";
			smppath = dir+"/egif/out/src/xml/";
			jsonpath = dir+"/egif/out/src/json/";
			jvpath = dir+"/egif/out/src/java/";
			modpath = dir+"/egif/out/src/";
			distpath = dir+"/egif/out/dist/";
			bielibpath = dir+"/egif/out/bielib/";
			codepath = dir+"/egif/in/codes/";
			File fdir = new File(dir+"/egif/in");
			File[] flist = fdir.listFiles();
			readXls(flist);

			procBIE();	// build BIE xml schema
			procQDT();	//System.out.println("step 3: build codelist schema");
			procRSM();	//System.out.println("step 4: build root schema");
			xmlRSM();	//System.out.println("step 5: build root sample XML");
			jsonRSM();	//System.out.println("step 5: build root sample XML");
			procBT();	//System.out.println("step 6: buz transact"); // analyze Business Transaction
			procUMM();	//System.out.println("step 7: UMM"); // build UMM RTF document
			BIELibrary();	// System.out.println("
			RSMLibrary();	// System.out.println("
			BIELibraryThai();	// System.out.println("
			RSMLibraryThai();	// System.out.println("

//			proc3();	//System.out.println("step 6: reuse analyze");
		} catch(Exception z) {
			z.printStackTrace();
		}
		System.out.println("Finish EGIF");
	}

public static void main(String[] args) throws Exception {
	boolean doall = false, bieschema=false, qdtschema=false, rsmschema=false;
	boolean rsmsample=false, wsdldoc=false, ummdoc=false;

//	IEProc003 ieproc = new IEProc003();
	THEGifProc ieproc = new THEGifProc();
	File dir = new File(".");
	String[] list = dir.list();	System.out.println("step 1");
	ieproc.proc2(list);	//System.out.println("step 2");
	ieproc.procBIE();	// build BIE xml schema
//	ieproc.procQDT();	//System.out.println("step 3: build codelist schema");
	ieproc.procRSM();	//System.out.println("step 4: build root schema");
	ieproc.xmlRSM();	//System.out.println("step 5: build root sample XML");
	ieproc.jsonRSM();	//System.out.println("step 5: build root sample XML");
	ieproc.procBT();	//System.out.println("step 6: buz transact"); // analyze Business Transaction
	ieproc.procUMM();	//System.out.println("step 7: UMM"); // build UMM RTF document

	ieproc.BIELibrary();	// System.out.println("
	ieproc.RSMLibrary();	// System.out.println("
	ieproc.BIELibraryThai();	// System.out.println("
	ieproc.RSMLibraryThai();	// System.out.println("

//	ieproc.proc3();	//System.out.println("step 6: reuse analyze");
}

	public void BIELibrary() throws Exception {
		String[] words;

		File dir = new File(bielibpath);
		if(!dir.exists()) dir.mkdirs();

		String fnm = dir+"/fig/";
		File dfnm = new File(fnm);
		if(dfnm.exists()==false) dfnm.mkdirs();

System.out.println("==== BIE library");

		Vector<Vector<String[]>> abiev = new Vector<Vector<String[]>>();
		Vector<String[]> currv = null;
		for(int i=0; i<bie.size(); i++) {
			words = bie.get(i);
			if("ABIE".equals(words[1])) {
				currv = new Vector<String[]>();
				abiev.add(currv);
				currv.add(words);
			} else if(currv!=null) {
				currv.add(words);
			}
		}

		for(int i=0; i<abiev.size(); i++) {
			currv = abiev.get(i);
			drawABIE(fnm+ currv.get(0)[0] + ".png", currv);
		}

		FileInputStream pis;
		String imf;
		XWPFParagraph pp;
		XWPFRun rr;

		String doc1 = bielibpath +"/BIE-Library.docx";
		XWPFDocument doc = new XWPFDocument();

		pp = doc.createParagraph();
		pp.setStyle("Normal");
		rr = pp.createRun();
		rr.setText("National Standardized Data Set"); rr.addBreak();

		pp = doc.createParagraph();
		rr = pp.createRun();
		rr.setText("BIE Library Library"); rr.addBreak();

		pp = doc.createParagraph();
		rr = pp.createRun();
		rr.setText("List of ABIE"); rr.addBreak();

		for(int i=0; i<abiev.size(); i++) {
			currv = abiev.get(i);
			rr = pp.createRun();
			rr.setText((i+1) +". "+ currv.get(0)[3] + "_ " + currv.get(0)[5]);
			rr.addBreak();
		}

		for(int i=0; i<abiev.size(); i++) {
			currv = abiev.get(i);
			pp = doc.createParagraph();
			pp.setPageBreak(true);
			rr = pp.createRun();
			rr.setText((i+1) +". "+ currv.get(0)[3] + "_ " + currv.get(0)[5]+ "  (RSM)"); rr.addBreak();
			rr.setText("   "+currv.get(0)[17]);

			rr.addBreak();
			imf = currv.get(0)[0]+".png";
			rr.addPicture(new FileInputStream(fnm+imf)
				, XWPFDocument.PICTURE_TYPE_PNG, imf, Units.toEMU(400), Units.toEMU(220));

			boolean bbiefg = false, asbiefg = false;
			int bbiecnt = 0, asbiecnt = 0;

			rr.addBreak();
			rr.addBreak();
			for(int j=1; j<currv.size(); j++) {
				if(!"BBIE".equals(currv.get(j)[1])) continue;
				if(bbiecnt==0) { rr.addBreak(); rr.setText("   List of Basic BIE"); rr.addBreak(); }
				bbiecnt++;
				rr.addBreak();
				rr.setText("   "+bbiecnt+".");
				if(currv.get(j)[7].length()>0) {
					rr.setText("   "+ currv.get(j)[7] + "_ ");
				}
				rr.setText(currv.get(j)[9]+" (BBIE)");
				rr.addBreak();
				rr.setText("      "+currv.get(j)[17]);
			}

			rr.addBreak();
			for(int j=1; j<currv.size(); j++) {
				if(!"ASBIE".equals(currv.get(j)[1])) continue;
				if(asbiecnt==0) { rr.addBreak(); rr.setText("   List of Association BIE"); rr.addBreak(); }
				asbiecnt++;
				rr.addBreak();
				rr.setText("   "+asbiecnt+".");
				if(currv.get(j)[7].length()>0) {
					rr.setText(currv.get(j)[7] + "_ ");
				}
				rr.setText(currv.get(j)[9]+" (ASBIE)");
				rr.addBreak();
				rr.setText("      "+currv.get(j)[17]);
			}
		}
		try (FileOutputStream fos = new FileOutputStream(doc1)) {
			doc.write(fos);
		}
		/*
		String filename = bielibpath +"/BIE-Library.rtf";
		FileOutputStream fos = new FileOutputStream(filename);
		PrintWriter out = new PrintWriter(fos);
		FileInputStream pis;
		int x;

		out.println("{\\rtf");

		out.println("{\\par\\b National Standardized Data Set}");
		out.println("{\\par\\b BIE library}");
		out.println("\\par");

		out.println("{\\par\\b List of ABIE}");
		for(int i=0; i<abiev.size(); i++) {
			currv = abiev.get(i);
			out.println("{\\par\\b " +(i+1) +". "+ currv.get(0)[3] + "_ " + currv.get(0)[5] + "}");
		}

		for(int i=0; i<abiev.size(); i++) {
			currv = abiev.get(i);
			out.println("{\\page");
			out.println("{\\b " +(i+1) +". "+ currv.get(0)[3] + "_ " + currv.get(0)[5] + "\\tab (ABIE)}");

			out.println("\\par\\par \\tab " + currv.get(0)[17]);

			out.println("\\par\\par");
			out.print("{\\*\\shppict {\\pict \\pngblip ");
			pis = new FileInputStream(fnm+ currv.get(0)[0] + ".png");
			while( (x=pis.read())>=0 ) {out.print(hex(x));}
			pis.close();out.print("}}");
			out.println("\\par");

			boolean bbiefg = false, asbiefg = false;
			int bbiecnt = 0, asbiecnt = 0;

			for(int j=1; j<currv.size(); j++) {
				if(!"BBIE".equals(currv.get(j)[1])) continue;
				if(bbiecnt==0) out.println("\\par \\par {\\b List of Basic BIE}");
				bbiecnt++;
				out.println("\\par\\tab " + bbiecnt + ".");
				if(currv.get(j)[7].length()>0) {
					out.println(currv.get(j)[7] + "_ ");
				}
				out.println(currv.get(j)[9] + "\\tab (BBIE)");
				out.println("\\par \\tab \\tab " + currv.get(j)[17]);
			}

			for(int j=1; j<currv.size(); j++) {
				if(!"ASBIE".equals(currv.get(j)[1])) continue;
				if(asbiecnt==0) out.println("\\par \\par {\\b List of Association BIE}");
				asbiecnt++;
				out.println("\\par\\tab " + asbiecnt + ".");
				if(currv.get(j)[7].length()>0) {
					out.println(currv.get(j)[7] + "_ ");
				}
				out.println(currv.get(j)[9] + "\\tab (ASBIE)");
				out.println("\\par \\tab \\tab " + currv.get(j)[17]);
			}

			out.println("}");
		}

		out.println("\\par\\par");
		out.println("}");
		out.close();
		*/
	}

	String entryName(String[] wds) {
		String den;
		if("ABIE".equals(wds[1])) {
			den = "";
			if(wds[3].length()>0) {
				den += wds[3] + "_ ";
			}
			den += wds[5];
			return den;
		} else if("BBIE".equals(wds[1])) {
			den = "";
			if(wds[7].length()>0) {
				den += wds[7] + "_ ";
			}
			den += wds[9];
			den += ": " + wds[10];
			den += " [" + wds[13] + "]";
			return den;
		} else if("ASBIE".equals(wds[1])) {
			den = "";
			if(wds[7].length()>0) {
				den += wds[7] + "_ ";
			}
			den += wds[9];
			return den;
		} else {
			return "?";
		}
	}

	String assoEntryName(String[] wds) {
		String den;
		if("ASBIE".equals(wds[1])) {
			den = "";
			if(wds[11].length()>0) {
				den += wds[11] + "_ ";
			}
			den += wds[12];
			return den;
		} else {
			return "?";
		}
//		return "x:" + wds[3] + ".";
	}

	public void drawABIE(String fname, Vector<String[]> abiev
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
			drawText(g2d, "<<ABIE>>", pkg.x/2, vnm.y, CENTER);
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
System.out.println("ex1:" + exg1);
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

	public void RSMLibrary() throws Exception {
		String[] words;

		File dir = new File(bielibpath);
		if(!dir.exists()) dir.mkdirs();

		String fnm = dir+"/fig/";
		File dfnm = new File(fnm);
		if(dfnm.exists()==false) dfnm.mkdirs();

System.out.println("==== RSM library");

		Vector<Vector<String[]>> abiev = new Vector<Vector<String[]>>();
		Vector<String[]> currv = null;
		for(int i=0; i<rsm.size(); i++) {
			words = rsm.get(i);
			if("ABIE".equals(words[1])) {
				currv = new Vector<String[]>();
				abiev.add(currv);
				currv.add(words);
			} else if(currv!=null) {
				currv.add(words);
			}
		}

		for(int i=0; i<abiev.size(); i++) {
			currv = abiev.get(i);
			drawRSM(fnm+ currv.get(0)[0] + ".png", currv);
		}

		FileInputStream pis;
		String imf;
		XWPFParagraph pp;
		XWPFRun rr;

		String doc1 = bielibpath +"/RSM-Library.docx";
		XWPFDocument doc = new XWPFDocument();

		pp = doc.createParagraph();
		pp.setStyle("Normal");
		rr = pp.createRun();
		rr.setText("Document Data Set"); rr.addBreak();

		pp = doc.createParagraph();
		rr = pp.createRun();
		rr.setText("RSM Library"); rr.addBreak();

		for(int i=0; i<abiev.size(); i++) {
			currv = abiev.get(i);
			rr = pp.createRun();
			rr.setText((i+1) +". "+ currv.get(0)[3] + "_ " + currv.get(0)[5]);
			rr.addBreak();
		}

		for(int i=0; i<abiev.size(); i++) {
			currv = abiev.get(i);
			pp = doc.createParagraph();
			pp.setPageBreak(true);
			rr = pp.createRun();
			rr.setText((i+1) +". "+ currv.get(0)[3] + "_ " + currv.get(0)[5]+ "  (RSM)"); rr.addBreak();
			rr.setText("   "+currv.get(0)[17]);

			rr.addBreak();
			imf = currv.get(0)[0]+".png";
			rr.addPicture(new FileInputStream(fnm+imf)
				, XWPFDocument.PICTURE_TYPE_PNG, imf, Units.toEMU(400), Units.toEMU(220));

			boolean bbiefg = false, asbiefg = false;
			int bbiecnt = 0, asbiecnt = 0;

			rr.addBreak();
			rr.addBreak();
			for(int j=1; j<currv.size(); j++) {
				if(!"BBIE".equals(currv.get(j)[1])) continue;
				if(bbiecnt==0) { rr.addBreak(); rr.setText("   List of Basic BIE"); rr.addBreak(); }
				bbiecnt++;
				rr.addBreak();
				rr.setText("   "+bbiecnt+".");
				if(currv.get(j)[7].length()>0) {
					rr.setText("   "+ currv.get(j)[7] + "_ ");
				}
				rr.setText(currv.get(j)[9]+" (BBIE)");
				rr.addBreak();
				rr.setText("      "+currv.get(j)[17]);
			}

			rr.addBreak();
			for(int j=1; j<currv.size(); j++) {
				if(!"ASBIE".equals(currv.get(j)[1])) continue;
				if(asbiecnt==0) { rr.addBreak(); rr.setText("   List of Association BIE"); rr.addBreak(); }
				asbiecnt++;
				rr.addBreak();
				rr.setText("   "+asbiecnt+".");
				if(currv.get(j)[7].length()>0) {
					rr.setText(currv.get(j)[7] + "_ ");
				}
				rr.setText(currv.get(j)[9]+" (ASBIE)");
				rr.addBreak();
				rr.setText("      "+currv.get(j)[17]);
			}
		}
		try (FileOutputStream fos = new FileOutputStream(doc1)) {
			doc.write(fos);
		}

/*
		String filename = bielibpath +"/RSM-Library.rtf";
		FileOutputStream fos = new FileOutputStream(filename);
		PrintWriter out = new PrintWriter(fos);
		FileInputStream pis;
		int x;

		out.println("{\\rtf");

		out.println("{\\par\\b Document Data Set}");
		out.println("{\\par\\b RSM library}");
		out.println("\\par");

		out.println("{\\par\\b List of ABIE}");
		for(int i=0; i<abiev.size(); i++) {
			currv = abiev.get(i);
			out.println("{\\par\\b " +(i+1) +". "+ currv.get(0)[3] + "_ " + currv.get(0)[5] + "}");
		}

		for(int i=0; i<abiev.size(); i++) {
			currv = abiev.get(i);
			out.println("{\\page");
			out.println("{\\b " +(i+1) +". "+ currv.get(0)[3] + "_ " 
				+ currv.get(0)[5] + "\\tab (RSM)}");

			out.println("\\par\\par \\tab " + currv.get(0)[17]);

			out.println("\\par\\par");
			out.print("{\\*\\shppict {\\pict \\pngblip ");
			pis = new FileInputStream(fnm+ currv.get(0)[0] + "x.png");
			while( (x=pis.read())>=0 ) {out.print(hex(x));}
			pis.close();out.print("}}");
			out.println("\\par");

			boolean bbiefg = false, asbiefg = false;
			int bbiecnt = 0, asbiecnt = 0;

			for(int j=1; j<currv.size(); j++) {
				if(!"BBIE".equals(currv.get(j)[1])) continue;
				if(bbiecnt==0) out.println("\\par \\par {\\b List of Basic BIE}");
				bbiecnt++;
				out.println("\\par\\tab " + bbiecnt + ".");
				if(currv.get(j)[7].length()>0) {
					out.println(currv.get(j)[7] + "_ ");
				}
				out.println(currv.get(j)[9] + "\\tab (BBIE)");
				out.println("\\par \\tab \\tab " + currv.get(j)[17]);
			}

			for(int j=1; j<currv.size(); j++) {
				if(!"ASBIE".equals(currv.get(j)[1])) continue;
				if(asbiecnt==0) out.println("\\par \\par {\\b List of Association BIE}");
				asbiecnt++;
				out.println("\\par\\tab " + asbiecnt + ".");
				if(currv.get(j)[7].length()>0) {
					out.println(currv.get(j)[7] + "_ ");
				}
				out.println(currv.get(j)[9] + "\\tab (ASBIE)");
				out.println("\\par \\tab \\tab " + currv.get(j)[17]);
			}

			out.println("}");
		}

		out.println("\\par\\par");
		out.println("}");
		out.close();
*/
	}

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

	public void BIELibraryThai() throws Exception {
		String[] words;

		File dir = new File(bielibpath);
		if(!dir.exists()) dir.mkdirs();

		String fnm = dir+"/fig/";
		File dfnm = new File(fnm);
		if(dfnm.exists()==false) dfnm.mkdirs();

System.out.println("==== BIE library (Thai)");

		Vector<Vector<String[]>> abiev = new Vector<Vector<String[]>>();
		Vector<String[]> currv = null;
		for(int i=0; i<bie.size(); i++) {
			words = bie.get(i);
			if("ABIE".equals(words[1])) {
				currv = new Vector<String[]>();
				abiev.add(currv);
				currv.add(words);
			} else if(currv!=null) {
				currv.add(words);
			}
		}

		for(int i=0; i<abiev.size(); i++) {
			currv = abiev.get(i);
			drawABIEThai(fnm+ currv.get(0)[0] + ".png", currv);
		}

		String filename = bielibpath +"/BIE-Library-Thai.rtf";
		FileOutputStream fos = new FileOutputStream(filename);
		PrintWriter out = new PrintWriter(fos);
		FileInputStream pis;
		int x;

		out.println("{\\rtf");

		out.println("{\\par\\b ชุดข้อมูลมาตรฐานของประเทศ}");
		out.println("{\\par\\b (BIE library)}");
		out.println("\\par");

		out.println("{\\par\\b รายการชุดข้อมูล}");
		for(int i=0; i<abiev.size(); i++) {
			currv = abiev.get(i);
			out.println("{\\par\\b " +(i+1) +". "+ currv.get(0)[2] + "_ " + currv.get(0)[4] + "");
			out.println("\\tab (" + currv.get(0)[3] + "_ " + currv.get(0)[5] + ")}");
		}

		for(int i=0; i<abiev.size(); i++) {
			currv = abiev.get(i);
			out.println("{\\page");
			out.println("{\\b " +(i+1) +". "+ currv.get(0)[2] + "_ " + currv.get(0)[4] + "}");
			out.println("{\\b \\tab (" + currv.get(0)[3] + "_ " + currv.get(0)[5] + ")}");

			out.println("\\par\\par \\tab " + currv.get(0)[16]);

			out.println("\\par\\par");
			out.print("{\\*\\shppict {\\pict \\pngblip ");
			pis = new FileInputStream(fnm+ currv.get(0)[0] + ".png");
			while( (x=pis.read())>=0 ) {out.print(hex(x));}
			pis.close();out.print("}}");
			out.println("\\par");

			boolean bbiefg = false, asbiefg = false;
			int bbiecnt = 0, asbiecnt = 0;

			for(int j=1; j<currv.size(); j++) {
				if(!"BBIE".equals(currv.get(j)[1])) continue;
				if(bbiecnt==0) out.println("\\par \\par {\\b รายการข้อมูลพื้นฐาน}");
				bbiecnt++;

				out.println("\\par\\tab " + bbiecnt + ".");
				if(currv.get(j)[6].length()>0) {
					out.println(currv.get(j)[6] + "_ ");
				}
				out.println(currv.get(j)[8]);

				out.println("\\tab (");
				if(currv.get(j)[7].length()>0) {
					out.println(currv.get(j)[7] + "_ ");
				}
				out.println(currv.get(j)[9] + ")");

				out.println("\\par \\tab \\tab " + currv.get(j)[16]);
			}

			for(int j=1; j<currv.size(); j++) {
				if(!"ASBIE".equals(currv.get(j)[1])) continue;
				if(asbiecnt==0) out.println("\\par \\par {\\b รายการข้อมูลที่เป็นชุด}");
				asbiecnt++;

				out.println("\\par\\tab " + asbiecnt + ".");
				if(currv.get(j)[6].length()>0) {
					out.println(currv.get(j)[6] + "_ ");
				}
				out.println(currv.get(j)[8]);

				out.println("\\tab (");
				if(currv.get(j)[7].length()>0) {
					out.println(currv.get(j)[7] + "_ ");
				}
				out.println(currv.get(j)[9] + ")");

				out.println("\\par \\tab \\tab " + currv.get(j)[16]);
			}

			out.println("}");
		}

		out.println("\\par\\par");
		out.println("}");
		out.close();
	}

	String entryNameThai(String[] wds) {
		String den;
		if("ABIE".equals(wds[1])) {
			den = "";
			if(wds[2].length()>0) {
				den += wds[2] + "_ ";
			}
			den += wds[4];
			return den;
		} else if("BBIE".equals(wds[1])) {
			den = "";
			if(wds[6].length()>0) {
				den += wds[6] + "_ ";
			}
			den += wds[8];
			den += ": " + wds[10];
			den += " [" + wds[13] + "]";
			return den;
		} else if("ASBIE".equals(wds[1])) {
			den = "";
			if(wds[6].length()>0) {
				den += wds[6] + "_ ";
			}
			den += wds[8];
			return den;
		} else {
			return "?";
		}
	}

	String assoEntryNameThai(String[] wds) {
		String den;
		if("ASBIE".equals(wds[1])) {
			den = "";
			if(wds[11].length()>0) {
				den += wds[11] + "_ ";
			}
			den += wds[12];
			return den;
		} else {
			return "?";
		}
//		return "x:" + wds[3] + ".";
	}

	public void drawABIEThai(String fname, Vector<String[]> abiev
		) {
		try {
			BufferedImage bi0 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
			Graphics2D g20 = bi0.createGraphics();

			Point fgsz = new Point(100,10);

			Rectangle2D rct = textRect(g20, entryNameThai(abiev.get(0)));
			if(rct.getWidth()>fgsz.x) fgsz.x = (int)rct.getWidth() * 12 / 10;
			fgsz.y += vnm.y;

			for(int j=1; j<abiev.size(); j++) {
				if(!"BBIE".equals(abiev.get(j)[1])) continue;
				String name = "<<BBIE>>" + entryNameThai(abiev.get(j));
				rct = textRect(g20, name);
				if(rct.getWidth()>fgsz.x) fgsz.x = (int)rct.getWidth() * 12 / 10;
				fgsz.y += vnm.y;
			}
			fgsz.y += vnm.y;

			Point fg1sz = new Point(100, vnm.y * 2);
			for(int j=1; j<abiev.size(); j++) {
				if(!"ASBIE".equals(abiev.get(j)[1])) continue;
				String name = "<<ASBIE>>"+entryNameThai(abiev.get(j));
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
			drawText(g2d, "<<ABIE>>", pkg.x/2, vnm.y, CENTER);
			drawText(g2d, entryNameThai(abiev.get(0)), pkg.x/2, vnm.y*2, CENTER);

			int cnt = 0;
			for(int j=1; j<abiev.size(); j++) {
				if(!"BBIE".equals(abiev.get(j)[1])) continue;
				cnt ++;
				String name = "<<BBIE>>" + entryNameThai(abiev.get(j));
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

				String name0 = entryNameThai(abiev.get(j));
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

				String name = assoEntryNameThai(abiev.get(j));
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
System.out.println("ex3:" + exg1);
		}
	}

	public void RSMLibraryThai() throws Exception {
		String[] words;

		File dir = new File(bielibpath);
		if(!dir.exists()) dir.mkdirs();

		String fnm = dir+"/fig/";
		File dfnm = new File(fnm);
		if(dfnm.exists()==false) dfnm.mkdirs();

System.out.println("==== RSM library (Thai)");

		Vector<Vector<String[]>> abiev = new Vector<Vector<String[]>>();
		Vector<String[]> currv = null;
		for(int i=0; i<rsm.size(); i++) {
			words = rsm.get(i);
			if("ABIE".equals(words[1])) {
				currv = new Vector<String[]>();
				abiev.add(currv);
				currv.add(words);
			} else if(currv!=null) {
				currv.add(words);
			}
		}

		for(int i=0; i<abiev.size(); i++) {
			currv = abiev.get(i);
			drawRSMThai(fnm+ currv.get(0)[0] + "x.png", currv);
		}

		FileInputStream pis;
		String imf;
		XWPFParagraph pp;
		XWPFRun rr;

		String doc1 = bielibpath +"/RSM-Library-Thai.docx";
		XWPFDocument doc = new XWPFDocument();

		pp = doc.createParagraph();
		pp.setStyle("Normal");
		rr = pp.createRun();
		rr.setText("เอกสารอิเล็กทรอนิกส์ (RSM library)"); rr.addBreak();

		pp = doc.createParagraph();
		rr = pp.createRun();
		rr.setText("รายการเอกสาร"); rr.addBreak();

		for(int i=0; i<abiev.size(); i++) {
			currv = abiev.get(i);
			rr = pp.createRun();
			rr.setText((i+1) +". "+ currv.get(0)[2] + "_ " + currv.get(0)[4]);
			rr.setText(" ("+ currv.get(0)[3] + "_ " + currv.get(0)[5]+")");
			rr.addBreak();
		}

		for(int i=0; i<abiev.size(); i++) {
			currv = abiev.get(i);
			pp = doc.createParagraph();
			pp.setPageBreak(true);
			rr = pp.createRun();
			rr.setText((i+1) +". "+ currv.get(0)[2] + "_ " + currv.get(0)[4]+ "  (RSM)");
			rr.setText(" ("+ currv.get(0)[3] + "_ " + currv.get(0)[5]+ ")");
			rr.addBreak();

			rr.addBreak();
			imf = currv.get(0)[0]+"x.png";
			rr.addPicture(new FileInputStream(fnm+imf)
				, XWPFDocument.PICTURE_TYPE_PNG, imf, Units.toEMU(400), Units.toEMU(220));

			boolean bbiefg = false, asbiefg = false;
			int bbiecnt = 0, asbiecnt = 0;

			rr.addBreak();
			
			pp = doc.createParagraph();
			rr = pp.createRun();
			for(int j=1; j<currv.size(); j++) {
				if(!"BBIE".equals(currv.get(j)[1])) continue;
				if(bbiecnt==0) { rr.addBreak(); rr.setText("รายการข้อมูลพื้นฐาน"); rr.addBreak(); }
				bbiecnt++;
				rr.addBreak();
				rr.setText("   "+bbiecnt+".");
				if(currv.get(j)[6].length()>0) {
					rr.setText("   "+ currv.get(j)[6] + "_ ");
				}
				rr.setText(currv.get(j)[8]+" (");
				if(currv.get(j)[7].length()>0) {
					rr.setText("   "+ currv.get(j)[7] + "_ ");
				}
				rr.setText(currv.get(j)[9]+") (BBIE)");
				rr.addBreak();
				rr.setText("      "+currv.get(j)[16]);
			}

			pp = doc.createParagraph();
			rr = pp.createRun();
			for(int j=1; j<currv.size(); j++) {
				if(!"ASBIE".equals(currv.get(j)[1])) continue;
				if(asbiecnt==0) { rr.addBreak(); rr.setText("รายการข้อมูลชุด"); rr.addBreak(); }
				asbiecnt++;
				rr.addBreak();
				rr.setText("   "+asbiecnt+".");
				if(currv.get(j)[6].length()>0) {
					rr.setText(currv.get(j)[6] + "_ ");
				}
				rr.setText(currv.get(j)[8]+" (");
				if(currv.get(j)[7].length()>0) {
					rr.setText(currv.get(j)[7] + "_ ");
				}
				rr.setText(currv.get(j)[9]+") (ASBIE)");
				rr.addBreak();
				rr.setText("      "+currv.get(j)[16]);
			}
		}
		try (FileOutputStream fos = new FileOutputStream(doc1)) {
			doc.write(fos);
		}
/*
		String filename = bielibpath +"/RSM-Library-Thai.rtf";
		FileOutputStream fos = new FileOutputStream(filename);
		PrintWriter out = new PrintWriter(fos);
		FileInputStream pis;
		int x;

		out.println("{\\rtf");

		out.println("{\\par\\b เอกสารอิเล็กทรอนิกส์}");
		out.println("{\\par\\b (RSM library)}");
		out.println("\\par");

		out.println("{\\par\\b รายการเอกสาร}");
		for(int i=0; i<abiev.size(); i++) {
			currv = abiev.get(i);
			out.println("{\\par\\b " +(i+1) +". "+ currv.get(0)[2] + "_ " + currv.get(0)[4] + "");
			out.println("\\tab (" + currv.get(0)[3] + "_ " + currv.get(0)[5] + ")}");
		}

		for(int i=0; i<abiev.size(); i++) {
			currv = abiev.get(i);
			out.println("{\\page");
			out.println("{\\b " +(i+1) +". "+ currv.get(0)[2] + "_ " + currv.get(0)[4] + "}");
			out.println("{\\b \\tab (" + currv.get(0)[3] + "_ " + currv.get(0)[5] + ")}");

			out.println("\\par\\par \\tab " + currv.get(0)[16]);

			out.println("\\par\\par");
			out.print("{\\*\\shppict {\\pict \\pngblip ");
			pis = new FileInputStream(fnm+ currv.get(0)[0] + "x.png");
			while( (x=pis.read())>=0 ) {out.print(hex(x));}
			pis.close();out.print("}}");
			out.println("\\par");

			boolean bbiefg = false, asbiefg = false;
			int bbiecnt = 0, asbiecnt = 0;

			for(int j=1; j<currv.size(); j++) {
				if(!"BBIE".equals(currv.get(j)[1])) continue;
				if(bbiecnt==0) out.println("\\par \\par {\\b รายการข้อมูลพื้นฐาน}");
				bbiecnt++;

				out.println("\\par\\tab " + bbiecnt + ".");
				if(currv.get(j)[6].length()>0) {
					out.println(currv.get(j)[6] + "_ ");
				}
				out.println(currv.get(j)[8]);

				out.println("\\tab (");
				if(currv.get(j)[7].length()>0) {
					out.println(currv.get(j)[7] + "_ ");
				}
				out.println(currv.get(j)[9] + ")");

				out.println("\\par \\tab \\tab " + currv.get(j)[16]);
			}

			for(int j=1; j<currv.size(); j++) {
				if(!"ASBIE".equals(currv.get(j)[1])) continue;
				if(asbiecnt==0) out.println("\\par \\par {\\b รายการข้อมูลที่เป็นชุด}");
				asbiecnt++;

				out.println("\\par\\tab " + asbiecnt + ".");
				if(currv.get(j)[6].length()>0) {
					out.println(currv.get(j)[6] + "_ ");
				}
				out.println(currv.get(j)[8]);

				out.println("\\tab (");
				if(currv.get(j)[7].length()>0) {
					out.println(currv.get(j)[7] + "_ ");
				}
				out.println(currv.get(j)[9] + ")");

				out.println("\\par \\tab \\tab " + currv.get(j)[16]);
			}

			out.println("}");
		}

		out.println("\\par\\par");
		out.println("}");
		out.close();
*/

	}

	public void drawRSMThai(String fname, Vector<String[]> abiev
		) {
		try {
			BufferedImage bi0 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
			Graphics2D g20 = bi0.createGraphics();

			Point fgsz = new Point(100,10);

			Rectangle2D rct = textRect(g20, entryNameThai(abiev.get(0)));
			if(rct.getWidth()>fgsz.x) fgsz.x = (int)rct.getWidth() * 12 / 10;
			fgsz.y += vnm.y;

			for(int j=1; j<abiev.size(); j++) {
				if(!"BBIE".equals(abiev.get(j)[1])) continue;
				String name = "<<BBIE>>" + entryNameThai(abiev.get(j));
				rct = textRect(g20, name);
				if(rct.getWidth()>fgsz.x) fgsz.x = (int)rct.getWidth() * 12 / 10;
				fgsz.y += vnm.y;
			}
			fgsz.y += vnm.y;

			Point fg1sz = new Point(100, vnm.y * 2);
			for(int j=1; j<abiev.size(); j++) {
				if(!"ASBIE".equals(abiev.get(j)[1])) continue;
				String name = "<<ASBIE>>"+entryNameThai(abiev.get(j));
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
			drawText(g2d, entryNameThai(abiev.get(0)), pkg.x/2, vnm.y*2, CENTER);

			int cnt = 0;
			for(int j=1; j<abiev.size(); j++) {
				if(!"BBIE".equals(abiev.get(j)[1])) continue;
				cnt ++;
				String name = "<<BBIE>>" + entryNameThai(abiev.get(j));
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

				String name0 = entryNameThai(abiev.get(j));
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

				String name = assoEntryNameThai(abiev.get(j));
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
System.out.println("ex4:" + exg1);
		}
	}
}

