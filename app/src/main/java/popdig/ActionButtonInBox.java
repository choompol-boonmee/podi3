package popdig;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import org.apache.log4j.Logger;

public class ActionButtonInBox implements ActionListener {
	static Logger log = Logger.getLogger(ActionButtonInBox.class);
	static int virt_key[] = {
		  KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5
		, KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9, KeyEvent.VK_B
		, KeyEvent.VK_E, KeyEvent.VK_G, KeyEvent.VK_K, KeyEvent.VK_M ,KeyEvent.VK_N
	};
	static String vk_label[] = {
		  "1", "2", "3", "4", "5"
		, "6", "7", "8", "9", "B"
		, "E", "G", "K", "M", "N"
	};
	static int vk_cnt = 0;
	static int ctrl = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

	String name;
	JButton butt;

	public static HashMap<KeyStroke, Action> actionMap;
	public static void setActionMap(HashMap<KeyStroke,Action> am) { actionMap = am; }

	ActionButtonInBox(String nm) { name = nm; }
	public void actionPerformed(ActionEvent av) {
		action();
	}    
	public void action() {}
	void addTo(Container box) {
		int ii = vk_cnt++;
		butt = new JButton(name+"("+vk_label[ii]+")");
		butt.setMargin(new Insets(0, 0, 0, 0)); 
		butt.setAlignmentX(Component.CENTER_ALIGNMENT);
		box.add(butt);
		butt.addActionListener(this);
		KeyStroke ks = KeyStroke.getKeyStroke(virt_key[ii], ctrl);
		actionMap.put(ks, new AbstractAction("action"+vk_cnt) { @Override
			public void actionPerformed(ActionEvent e) { action(); } });
	}    

	public static class ActionTestFrame extends ActionButtonInBox {
		String txt;
		ActionTestFrame(String n, String tx) { super(n); txt = tx; }
		public void action() {
log.info("TEST FRAME");
/*
			TextFileInfo tinf = getTextFileInfo();
			if(tinf==null) return;
			String rid = genId();
			if(rid==null) return;
			int c = tinf.text.getCaretPosition();
			String ins = rid+txt;
			tinf.text.insert(ins, tinf.text.getCaretPosition());
			tinf.text.setCaretPosition(c+ins.length());
			tinf.text.requestFocus();
*/
		}
    }

}    


