package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class ClearToEnd extends PopupAction {
	public ClearToEnd(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
	}
    public void action() {
		int i = tfinf.text.getCaretPosition();
		int j = tfinf.text.getText().length();
		tfinf.text.replaceRange("", i, j);
    }   
}

