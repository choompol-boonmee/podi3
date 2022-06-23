package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class AddRdfPointOnEarth extends PopupAction {
	String voSub;
	public AddRdfPointOnEarth(JPopupMenu pop, TextFileInfo tfi, String lb) {
		this(pop, tfi, lb, "vo:Point");
	}
	public AddRdfPointOnEarth(JPopupMenu pop, TextFileInfo tfi, String lb, String vo) {
		super(pop, tfi, lb);
		voSub = vo;
	}
    public void action() {
		String r = getNewRdf(voSub);
		if(r==null || r.length()==0) return;
		String latlon = PopiangUtil.getLatlongCB();
		if(latlon==null || latlon.length()==0) return;
		String rid = tfinf.text.newid;
		PopiangDigital.setObject(voSub, rid);
		String utm = PopiangUtil.latlon2utm(latlon);
		r = r.replace("___GPS___",latlon).replace("___UTM___",utm);
		insert(r);
    }   
}

