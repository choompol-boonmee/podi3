package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class AddRdfClassObject extends PopupAction {
	public AddRdfClassObject(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
	}
    public void action() {
		String cls = JOptionPane.showInputDialog("Please input Class");
		if(cls==null || cls.length()==0) return;
		cls = cls.trim();
		if(cls.length()==0) return;
		cls = "vo:"+cls;
        String r = getNewRdf(cls);
        if(r==null || r.length()==0) return;
        String rid = tfinf.text.newid;
        PopiangDigital.setObject(cls, rid);
        insert(r);
    }   

}

