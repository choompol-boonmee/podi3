package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

public class ActionMergePdf extends PopupAction {
	List<String> aVoIds;
	public ActionMergePdf(JPopupMenu pop, TextFileInfo tfi, String lb, List<String> ids) {
		super(pop, tfi, lb);
		aVoIds = ids;
	}
    public void action() {
		String fns = tfinf.path.toFile().getName();
		String pref = fns.substring(0,fns.indexOf("."));
		if(!pref.matches("[a-z]+[0-9A-Z]{2}")) return;

		List<String> rdfs = PopiangUtil.getVO("vo:Pdf");
        String r = getNewRdf(rdfs, "vo:Pdf");
        if(r==null || r.length()==0) return;
		String rid = tfinf.text.newid;
		String dir = pref.substring(0,pref.length()-2);
		String ext = ".pdf";
		String sp = ""+tfinf.path;
		String fnm = rid.replace(":","-");
		sp = sp.substring(0,sp.lastIndexOf("/rdf"))+"/res/"+dir+"/"+pref+"/"+fnm+ext;
		r = r.replace("___FILE___", "file:"+dir+"/"+pref+"/"+fnm+ext);

		try {
			PDFMergerUtility pdfMerger = new PDFMergerUtility();
			pdfMerger.setDestinationFileName(sp);
			StringBuffer fn = new StringBuffer();
			for(String id : aVoIds) {
				String f = PopiangUtil.query0(id+" vp:file ?a").get();
				if(!f.startsWith("file:")) continue;
				File fl = new File(PopiangDigital.workDir+"/res/"+f.substring(5));
				System.out.println("f: "+ fl+" : "+ fl.exists());
				fn.append("_"+fl.getName());
				pdfMerger.addSource(fl);
			}
			r = r.replace("___NAME___", fn.toString());
			pdfMerger.mergeDocuments(null);
		} catch(Exception x) {}
	

		insert(r);
		PopiangDigital.setObject("vo:Pdf", rid);
    }   
}

