package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.awt.Desktop;
import java.net.URLEncoder;

public class OpenLinkAction extends PopupAction {
	String url;
	public OpenLinkAction(JPopupMenu pop, TextFileInfo tfi, String lb, String u) {
		super(pop, tfi, lb);
		url = u;
	}
    public void action() {
		try {
System.out.println("url: "+ url);
//			String url0 = URLEncoder.encode(url, "UTF-8");
//			URI uri = new URI(url0);
			URL url0 = new URL(url);
			String p0 = url0.getPath();
//			p0 = p0.substring(1);
			String p1 = URLEncoder.encode(p0, "UTF-8");
			p1 = p1.replace("%2F","/");
			int i1 = url.indexOf(p0);
			String u0 = url.substring(0,i1);
System.out.println("u0:" + u0);
System.out.println("p0:"+ p0);
System.out.println("p1:"+ p1);
			URI uri2 = new URL(u0+p1).toURI();
System.out.println("uri2:"+ uri2);
			URI uri = new URL(url).toURI();
			Desktop dt = Desktop.getDesktop();
			dt.browse(uri2);
		} catch(Exception z) {
z.printStackTrace();
		}
    }   
}

