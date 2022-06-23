package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class QueryAction extends PopupAction {
	public QueryAction(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
	}
    public void action() {
		TextFileInfo tinf = tfinf;
		String str = tinf.text.getText();
		QueryExecute qe = new QueryExecute();
		qe.init(str);
System.out.println("QE1:"+ str);

		if(qe.aMain.size()!=1) return;
		PopiangUtil.readAllRdfModel();
		Map<String,String> map = qe.aMain.get(0);
		qe.query(map);
System.out.println("QE2:"+ map);

		String rid = tfinf.text.genId();
		if(rid==null) return;
		int len = tinf.text.getText().length();
		String txt = "\n"+rid+"\tvp:sum\t(\n"+qe.qe.state+"). \n";
		tinf.text.insert(txt, len);
    }   
}

