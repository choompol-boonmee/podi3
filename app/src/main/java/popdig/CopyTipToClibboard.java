package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.awt.datatransfer.*;
import java.awt.Toolkit;

public class CopyTipToClibboard extends PopupAction {
	public CopyTipToClibboard(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
	}
    public void action() {
		if(tfinf.wind.lastTip==null) return;
		StringSelection stringSelection = new StringSelection(tfinf.wind.lastTip);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
    }   

}

