package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class AddRdfAction extends PopupAction {
	String voSub;
	public AddRdfAction(JPopupMenu pop, TextFileInfo tfi, String lb, String vo) {
		super(pop, tfi, lb);
		voSub = vo;
	}
    public void action() {
        String r = getNewRdf(voSub);
        if(r==null || r.length()==0) return;
        String rid = tfinf.text.newid;
        PopiangDigital.setObject(voSub, rid);
        insert(r);
    }   

}

