package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class AddRdfIdToList extends PopupAction {
	public AddRdfIdToList(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
	}
    public void action() {
		System.out.println("id:"+ tfinf.wind.lastTip);
		String id = tfinf.wind.lastTip;
		String vo = PopiangUtil.query0(id+" a ?a").get();
		System.out.println("vo:"+ vo);
		tfinf.wind.add2Ids(vo, id);
    }   

}

