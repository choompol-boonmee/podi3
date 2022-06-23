package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Desktop;
import java.io.*;
import java.util.*;
import java.net.*;
import java.util.regex.*;

public class BrowseFileAction extends PopupAction {
	String url;
	public BrowseFileAction(JPopupMenu pop, TextFileInfo tfi, String lb, String u) {
		super(pop, tfi, lb);
		url = u;
	}
    public void action() {
		try {
			String f1 = "http://localhost:"+PopiangDigital.webPort+"/res/"+url.substring(5);
System.out.println("browse : "+ f1);
//			File f = new File(f1);
			Desktop dt = Desktop.getDesktop();
			dt.browse(new URI(f1));
		} catch(Exception z) {
			z.printStackTrace();
		}
    }   
}

