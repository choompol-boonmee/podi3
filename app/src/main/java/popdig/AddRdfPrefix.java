package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class AddRdfPrefix extends PopupAction {
	String voSub;
	public AddRdfPrefix(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
	}
    public void action() {
			String fn = tfinf.path.toFile().getName();
			String com = fn.substring(0,fn.indexOf("."));
			StringBuffer buf = new StringBuffer();
			List<TokenInfo> tks =  tfinf.text.getTokens();
			Map<String,String> mPref = new HashMap<>();
			String base = PopiangDigital.sBaseIRI;
			List<String> aPrf = new ArrayList<>();
			for(TokenInfo tki : tks) {
				int i1,i2;
				if(tki.type==TokenInfo.TYPE_IRI 
					&& (i1=tki.label.indexOf(":"))>0) {
					String prf = tki.label.substring(0,i1);
					if(mPref.get(prf)==null) {
						aPrf.add(prf);
						mPref.put(prf,prf);
					}
				}
			}
			HashMap<String,String> mPrf = new HashMap<>();
			mPrf.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
			mPrf.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
			String[] prfs = aPrf.toArray(new String[aPrf.size()]);
			Arrays.sort(prfs);
			for(String prf : prfs) {
				String iri = mPrf.get(prf);
				if(iri!=null) {
					buf.append("@prefix "+prf+": <"+iri+"> .\n");
				} else {
					buf.append("@prefix "+prf+": <"+base+"/"+prf+"#> .\n");
				}
			}
			buf.append("\n");
			String itxt = buf.toString();
			tfinf.text.insert(itxt, 0);
			tfinf.text.setCaretPosition(itxt.length());
			tfinf.text.requestFocus();
    }   
}

