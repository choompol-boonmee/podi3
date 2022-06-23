package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class AddRdfTitle extends PopupAction {
	public AddRdfTitle(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
	}
    public void action() {
		String fn = tfinf.path.toFile().getName();
		String com = fn.substring(0,fn.indexOf("."));
		String r = com+":0  rdf:title  ''' ... ''' .\n";
        insert(r);
    }   

}

