package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Desktop;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class OpenRdfToIRI extends PopupAction {
	String iri;
	public OpenRdfToIRI(JPopupMenu pop, TextFileInfo tfi, String lb, String id) {
		super(pop, tfi, lb);
		iri = id;
	}
    public void action() {
		tfinf.wind.showIRI(iri);
		PopiangDigital.aIriTrace.add(iri);
		PopiangDigital.wind.goback.setEnabled(true);
	}

/*
	public void showIRI(String iri) {
		try {
			String[] wds = iri.split(":");
			if(wds.length!=2) return;
			String rdfnm = wds[0];
			String rdfid = wds[1];
			File file;
			if(rdfnm.matches("^[a-z]+[0-9A-Z]{2}")) {
				String fld = rdfnm.substring(0,rdfnm.length()-2);
				file = new File(PopiangDigital.workDir+"/rdf/"+fld+"/"+rdfnm+".ttl");
			} else {
				file = new File(PopiangDigital.workDir+"/rdf/etc/"+rdfnm+".ttl");
			}
			if(file==null) return;
			final TextFileInfo tfi = tfinf.wind.getTextFileInfo(file);
			SwingUtilities.invokeLater(new Runnable() { public void run() {
				if(tfi.text.getTokens()==null) {
					tfi.text.setFirstIri(iri);
				} else {
					for(TokenInfo tif : tfi.text.getTokens()) {
						if(tif.label.equals(iri) && tif.stmPos==1) {
							tfi.text.setCaretPosition(tif.startOffset);
							try {
								int y = tfi.text.yForLineContaining(tif.startOffset);
								tfi.pane.getVerticalScrollBar().setValue(y);
								tfi.text.select(tif.startOffset, tif.endOffset);
							} catch(Exception z) {}
							break;
						}
					}
				}
			}});
		} catch(Exception x) {}
    }   
*/
}

