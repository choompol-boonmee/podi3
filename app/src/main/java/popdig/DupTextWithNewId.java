package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class DupTextWithNewId extends PopupAction {
	public DupTextWithNewId(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
	}
	String last;
    public void action() {

		TextFileInfo tinf = tfinf;
			int p1 = tinf.text.getSelectionStart();
			int p2 = tinf.text.getSelectionEnd();
			String s = tinf.text.getSelectedText();
			if(s==null || s.length()==0) {
				if(last==null || last.length()==0) return;
				s = last;
			}
			last = s;
			int i1 = s.indexOf(' ');
			int i2 = s.indexOf('\t');
			if(i1<0 && i2<0) return;
			int i3 = (i1<0)? i2 : ((i1<i2)? i1 : i2);
			String id0 = s.substring(0,i3);
			i1 = id0.indexOf(':');
			if(i1<0) return;
			id0 = id0.substring(0,i1);
			String fn = tinf.path.toFile().getName();
			String prf0 = fn.substring(0,fn.indexOf("."));
			if(!id0.equals(prf0)) return;
			String rid = tinf.text.genId();
			s = rid + s.substring(i3);
			int len = tinf.text.getText().length();
			tinf.text.insert(s, len);
			tinf.text.setCaretPosition(len+s.length());
			tinf.text.requestFocus();
    }   
}

