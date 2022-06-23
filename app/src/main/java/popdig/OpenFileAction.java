package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Desktop;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class OpenFileAction extends PopupAction {
	String url;
	public OpenFileAction(JPopupMenu pop, TextFileInfo tfi, String lb, String u) {
		super(pop, tfi, lb);
		url = u;
	}
    public void action() {
		try {
			String f1 = PopiangDigital.workDir+"/res/"+url.substring(5);
			File f = new File(f1);
			Desktop dt = Desktop.getDesktop();
			dt.open(f);
		} catch(Exception z) {
			z.printStackTrace();
		}
    }   
}

