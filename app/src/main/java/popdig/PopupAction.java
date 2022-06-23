package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public abstract class PopupAction implements ActionListener {
	JPopupMenu menu;
	public TextFileInfo tfinf;
	public PopupAction(JPopupMenu pop, TextFileInfo tf) {
		this(pop, tf, "Add URL");
	}
	public PopupAction(JPopupMenu pop, TextFileInfo tf, String label) {
		menu = pop;
		tfinf = tf;
		if(pop!=null) {
        	JMenuItem addurl = new JMenuItem(label);
	        pop.add(addurl);
			addurl.addActionListener(this);
		}
	}
	public String getNewRdf(String vo) {
		List<String> aURL = PopiangUtil.getVO(vo);
		String r = getNewRdf(aURL, vo);
		return r;
	}
	public String getNewRdf(String rid, String vo) {
		List<String> aURL = PopiangUtil.getVO(vo);
		String r = getNewRdf(rid, aURL, vo);
		return r;
	}
	public String getNewRdf(List<String> aURL, String vo) {
		String rid = tfinf.text.genId();
		if(rid==null) return null;
		String r = getNewRdf(rid, aURL, vo);
		return r;
	}
	public String getNewRdf(String rid, List<String> aURL, String vo) {
		StringBuffer bb = new StringBuffer(rid + " a "+vo+" ;\t");
		for(String l : aURL) { bb.append(l+"\n"); }    
		String clp = PopiangUtil.getClipBoard();
		String ddtt = PopiangUtil.timestamp();
		String r = bb.toString().replace("___CLIPBOARD___",clp)
			.replace("___DATETIME___",ddtt);
		PopiangDigital.setObject(vo, rid);
		return r;
	}
	public void insert(String r) {
		SwingUtilities.invokeLater(new Runnable() { public void run() {

			int c = tfinf.text.getCaretPosition();

			try {
				int line = tfinf.text.getLineOfOffset(c);
				int start = tfinf.text.getLineStartOffset(line);
				int end = tfinf.text.getLineEndOffset(line);
				int cnt = end - start - 1;

				int pos = tfinf.text.getCaretPosition();
				int po2 = c+r.length();
				int i1 = r.lastIndexOf("\"");
				int i2 = r.lastIndexOf("'");
				int i3 = r.lastIndexOf("\"\"\"");
				int i4 = r.lastIndexOf("'''");
				int i5 = r.indexOf("' ;");
				int i6 = r.indexOf("\" ;");
				if(i6>=0) {
					po2 = c + i6;
				} else if(i5>=0) {
					po2 = c + i5;
				} else if(i4>=0) {
					po2 = c + i4;
				} else if(i3>=0) {
					po2 = c + i3;
				} else if(i2>=0) {
					po2 = c + i2;
				} else if(i1>=0) {
					po2 = c + i1;
				}
				tfinf.text.insert(r, pos);
				tfinf.text.setCaretPosition(po2);
				tfinf.text.requestFocus();

			} catch(Exception z) {
			}
		}});
	}
	public abstract void action();
	public void actionPerformed(ActionEvent a) {
		new Thread() { public void run() { action(); } }.start();
//		SwingUtilities.invokeLater(new Runnable() { public void run() {action(); }});
	}
}

