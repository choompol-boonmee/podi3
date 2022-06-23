package popdig;

import java.util.*;

public class DocCclElem {
	String raw,a,rest="",iri,error,def,den,biz,card;
	int page, anno, dord, aord;
	boolean bHasChild = false, bError = false;
	TextFileInfo tfinf;
	List<String> aChild, aCard;
	List<DocCclElem> aElem = new ArrayList<>();
	DocCclElem parent;

	public DocCclElem(int pno, int ano, String txt, TextFileInfo ti) {
		raw = txt;
		page = pno;
		anno = ano;
		dord = pno * 1000 + ano;
		tfinf = ti;
		analyze();
	}
	void analyze() {
		aChild = new ArrayList<>();
		aCard = new ArrayList<>();
		StringBuffer er = new StringBuffer();
		
		String[] rws = raw.split(":");
		den = ""; biz = "";
		def = rws[0];
		if(rws.length>1) { den = rws[1].trim(); }
		if(rws.length>2) { biz = rws[2].trim(); }

		int i1 = def.indexOf("=");
		int i2;
		if(i1>0) {
			a = def.substring(0,i1);
			rest = def.substring(i1+1);
			rest = rest.replace(" ","").replace("\n","").replace("\r","").replace("\t","");
			if(rest.length()==0) { bError = true; er.append("no child"); }
			bHasChild = true;
		} else {
			a = def;
		}
		a = a.trim();
		int o = -1;
		try { o = Integer.parseInt(a.substring(1)); } catch(Exception z) {}
		if(o<=0) { bError = true; er.append("wrong number"); }
		aord = page * 1000 + o;

		iri = "p"+page+a;
		if(bHasChild) {
			String[] wds = rest.split(",");
//System.out.println("a="+wds.length+"::"+raw);
			for(int i=0; i<wds.length; i++) {
				String mo = "0..1";
				String wd = wds[i];
				if(wd.matches(".*\\*$")) {
					mo = "0..*";
					wd = wd.substring(0,wd.length()-1);
				}
				if(wd.matches(".*\\+$")) {
					mo = "1..*";
					wd = wd.substring(0,wd.length()-1);
				}
				String[] sss = wd.split("-");
				if(sss.length==1) {
					String aa = sss[0];
					if(aa.startsWith("a")) aa = "p"+page+aa;
					aChild.add(aa);
					aCard.add(mo);
				} else if(sss.length==2) {
					int pg = page;
					int j1=1,j2=0;
					if(sss[0].startsWith("p") && sss[1].startsWith("p")) {
						if((j1=sss[0].indexOf("a"))>0 && (j2=sss[1].indexOf("a"))>0) {
							String pg1 = sss[0].substring(1,j1);
							String pg2 = sss[1].substring(1,j2);
							int pp = -1;
							try { pp=Integer.parseInt(pg1); } catch(Exception x) {}
							if(pg1.equals(pg2) && pp>0) {
								j1++;
								pg = pp;
							} else { return; }
						} else { return; }
					} else if(sss[0].startsWith("a") && sss[1].startsWith("a")) {
					} else { return; }
					i1=-1; i2=-1;
					try { i1=Integer.parseInt(sss[0].substring(j1));} catch(Exception z){}
					try { i2=Integer.parseInt(sss[1].substring(j1));} catch(Exception z){}
					if(i1>0 && i2>=i1) {
//System.out.println(" ii:"+wd+" :"+i1+", "+i2);
						for(int j=i1; j<=i2; j++) {
							aChild.add("p"+pg+"a"+j);
							aCard.add(mo);
						}
					}
				}
//System.out.println("  "+sss.length+" : "+ wd);
			}
		}
	}
}

