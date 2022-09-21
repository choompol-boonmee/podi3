package popdig;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.Insets;
import java.awt.*;
import java.net.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileSystemView;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.FileLocation;

import javax.imageio.ImageIO;

import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import java.io.*;
import java.nio.channels.FileChannel;
import javax.swing.UIManager.LookAndFeelInfo;

import java.net.URL;
import java.text.*;
import java.nio.file.Files;
import java.nio.file.Path;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import org.fife.rsta.ui.*;
import org.fife.rsta.ui.search.*;
import javax.swing.text.BadLocationException;
import java.awt.Toolkit;
import org.fife.ui.rtextarea.ToolTipSupplier;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import java.io.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.query.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.Toolkit;
import java.util.HashMap;
import org.fife.ui.rsyntaxtextarea.folding.FoldManager;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import java.util.regex.*;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import java.util.*;
import java.util.stream.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.interactive.annotation.*;
import org.apache.pdfbox.cos.*;
import org.apache.pdfbox.rendering.*;
import org.apache.pdfbox.pdmodel.common.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import java.awt.geom.*;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.interfaces.*;

import org.apache.xml.security.encryption.XMLCipher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.keycloak.saml.processing.core.util.*;
import javax.xml.namespace.QName;


public class PopiangWindow implements SearchListener, ToolTipSupplier {

	static Logger log = Logger.getLogger(PopiangWindow.class);

	private FileSystemView fileSystemView;

	private File currentFile;

	private JPanel gui;

	public JTree tree;
	public DefaultTreeModel treeModel;

	JTabbedPane tabbedPane;
	Hashtable<String,TextFileInfo> hTabPane = new Hashtable<>();

	private ListSelectionListener listSelectionListener;
	private boolean cellSizesSet = false;
	private int rowIconPadding = 6;

	private FindDialog findDialog;
	private ReplaceDialog replaceDialog;
	private StatusBar statusBar;

//	File ownDir = null, qryDir = null;
//	DefaultMutableTreeNode ownNode = null, qryNode = null;
	DefaultMutableTreeNode selectedNode = null;
	DefaultMutableTreeNode root = null;

	static File curDir;

	HashMap<KeyStroke, Action> actionMap = new HashMap<KeyStroke, Action>();

	public TextFileInfo getTextFileInfo() {
		int id = tabbedPane.getSelectedIndex();
		if(id<0) return null;
		String ttl = tabbedPane.getTitleAt(id);
		if(ttl==null) return null;
		TextFileInfo tinf = hTabPane.get(ttl);
		if(tinf==null) return null;
		return tinf;
	}

	class ActionImageTrim extends ActionButtonInBox {
		ActionImageTrim(String n) { super(n); }
		public void action() {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(PopiangDigital.fWork);
			int result = fileChooser.showOpenDialog(null);
			if(result == JFileChooser.APPROVE_OPTION) {
				File fImg = fileChooser.getSelectedFile();
				try {
					BufferedImage bim = ImageIO.read(fImg);
					int wd0 = bim.getWidth();
					int hg0 = bim.getHeight();
					log.info("wd:"+wd0+" hg:"+hg0);

					int dx = wd0 / 20;
					int dy = hg0 / 20;

					boolean flag;

					//   
					// ============= CHECK LEFT BORDER =============
					flag = false ;
					int leftBD = -1;
					do { 
						leftBD++;
						for(int h=0; h<bim.getHeight(); h++) {
							if(bim.getRGB(leftBD,h) != Color.white.getRGB()) {
								flag = true;
								break;
							}
						}
						if(leftBD>=bim.getWidth()) flag = true;
					} while(!flag);
					if(leftBD>dx) leftBD -= dx;

					//
					// ============= CHECK RIGHT BORDER =============
					flag = false;
					int rightBD = bim.getWidth();
					do {
						rightBD--;
						for(int h=0; h<bim.getHeight(); h++) {
                                        if(bim.getRGB(rightBD,h) != Color.white.getRGB()) {
                                            flag = true;
                                            break;
                                        }
						}
						if(rightBD<0) flag = true;
					} while(!flag);
					if(bim.getWidth()-rightBD>dx) rightBD += dx;

					//
					// ============= CHECK TOP BORDER =============
					flag = false;
					int upBD = -1;
					do {
						upBD++;
						for(int c=0; c<bim.getWidth(); c++) {
                                        if(bim.getRGB(c,upBD) != Color.white.getRGB()) {
                                            flag = true;
                                            break;
                                        }
						}
						if(upBD>=bim.getHeight()) flag = true;
					} while(!flag);
					if(upBD>dy) upBD -= dy;

					//
					// ============= CHECK DOWN BORDER =============
					flag = false;
					int downBD = bim.getHeight();
					do {
						downBD--;
						for(int c=0; c<bim.getWidth(); c++) {
                                        if(bim.getRGB(c,downBD) != Color.white.getRGB()) {
                                            flag = true;
                                            break;
                                        }
						}
						if(downBD<0) flag = true;
					} while(!flag);
					if(bim.getHeight()-downBD>dy) downBD += dy;

					wd0 = rightBD - leftBD;
					hg0 = downBD - upBD;

log.info("wd:"+ wd0+ " hg:"+ hg0);
					JFileChooser jfc = new JFileChooser();
					jfc.setCurrentDirectory(PopiangDigital.fWork);
					jfc.setDialogTitle("Choose a directory to save your file: "); 
					int returnValue = jfc.showSaveDialog(null);
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						File fl = jfc.getSelectedFile();
//						String file = jfc.getSelectedFile().getAbsolutePath();
//						if(!file.endsWith(".docx")) file += ".jpg";
//						BufferedImage img1 = new BufferedImage(wd0, hg0 , BufferedImage.TYPE_INT_ARGB);

						BufferedImage img1 = new BufferedImage(wd0, hg0 , bim.getType());
						Graphics2D g2 = img1.createGraphics();
						g2.drawImage(bim, 0,0,wd0,hg0, leftBD,upBD,rightBD,downBD, null);
						g2.dispose();
						ImageIO.write(img1, "JPG", fl);
System.out.println("WRITE FILE: "+ fl);
            		}   
				} catch(Exception z) {
					z.printStackTrace();
				}
			}
		}
	}

	int ctrl;
	int shift;
	Box box = Box.createVerticalBox();
	List<String> aBox = new ArrayList<>();
	Map<String,RecordedVocabId> hBox = new HashMap<>();

	Map<String,List<String>> haVoIds = new HashMap<>();

	public void add2Ids(String vo, String id) {
		List<String> aVoIds = haVoIds.get(vo);
		if(aVoIds==null) {
			aVoIds = new ArrayList<>();
			haVoIds.put(vo, aVoIds);
		}
		aVoIds.add(id);
		StringBuffer tt = new StringBuffer("<html>"+id+":"+vo);
		for(String s: aVoIds) tt.append("<br> - "+s);
		tt.append("</html>");
		RecordedVocabId rvi = hBox.get(vo);
		rvi.setToolTipText(tt.toString());
		rvi.setLabel();
	}
	public void clearIds(String vo) {
		System.out.println("clear all vo:"+vo);
		haVoIds.remove(vo);
		RecordedVocabId rvi = hBox.get(vo);
		rvi.setLabel();
	}
	public void copyIds(String vo) {
		List<String>aVoIds = haVoIds.get(vo);
		if(aVoIds==null) return;
		StringBuffer sb = new StringBuffer("");
		for(String i: aVoIds) sb.append(i+"\n");
		StringSelection stringSelection = new StringSelection(sb.toString());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}
	public void copyId(String id) {
		StringSelection stringSelection = new StringSelection(id);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}

	public void showIRI(String iri) {
		try {
			String[] wds = iri.split(":");
			if(wds.length!=2) return;
			String rdfnm = wds[0];
			String rdfid = wds[1];
			File file;
			if(rdfnm.matches("^[a-z]+[0-9A-Z]{2}")) {
				String fld = rdfnm.substring(0,rdfnm.length()-2);
				file = new File(PopiangDigital.workDir+"/rdf/"+fld+"/"+rdfnm+".ttl");
			} else {
				file = new File(PopiangDigital.workDir+"/rdf/etc/"+rdfnm+".ttl");
			}
			if(file==null) return;
			final TextFileInfo tfi = getTextFileInfo(file);
			SwingUtilities.invokeLater(new Runnable() { public void run() {
				if(tfi.text.getTokens()==null) {
					tfi.text.setFirstIri(iri);
//					tfi.text.setFirstFold(true);
				} else {
					for(TokenInfo tif : tfi.text.getTokens()) {
						if(tif.label.equals(iri) && tif.stmPos==1) {
							tfi.text.setCaretPosition(tif.startOffset);
							try {
								int y = tfi.text.yForLineContaining(tif.startOffset);
								tfi.pane.getVerticalScrollBar().setValue(y);
								tfi.text.select(tif.startOffset, tif.endOffset);
							} catch(Exception z) {}
							break;
						}
					}
				}
				goback.setEnabled(true);
				updateGui();
			}});
		} catch(Exception x) {}
    }   
	void pasteId(String id) {
		int i = tabbedPane.getSelectedIndex();
		if(i<0) return;
		String ttl = tabbedPane.getTitleAt(i);
		TextFileInfo tinf = hTabPane.get(ttl);
		int pos = tinf.text.getCaretPosition();
		System.out.println("id: "+ id);
		tinf.text.insert(id, pos);
	}
	class RecordedVocabId extends JButton {
		String vo;
		String id;
		JPopupMenu pop;
		RecordedVocabId(String v, String i) {
			vo = v;
			id = i;
			setMargin(new Insets(0, 0, 0, 0));
			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if(e.getModifiers() == MouseEvent.BUTTON3_MASK) { leftClick(e); } } });
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent a) { action(a); } });
		}
		void action(ActionEvent a) {
			PopiangDigital.aIriTrace.add(id);
			goback.setEnabled(true);
			showIRI(id);
		}
		void setLabel() {
			List<String> aVoIds = haVoIds.get(vo);
			if(aVoIds==null) setText(id+":"+vo);
			else setText(id+":"+vo+":"+aVoIds.size());
		}
		void setId(String i) { id = i; setLabel(); }
		void leftClick(MouseEvent e) {
			pop = new JPopupMenu();
	
			if(true) {
				JMenuItem pasteId = new JMenuItem("Paste '"+id+"'");
				pasteId.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) { pasteId(id); }});
				pop.add(pasteId);
			}
			List<String> aVoIds = haVoIds.get(vo);
			int i = tabbedPane.getSelectedIndex();
			if(i>=0) {
				String ttl = tabbedPane.getTitleAt(i);
				TextFileInfo tfinf = hTabPane.get(ttl);
				if(tfinf!=null) {
					if("vo:Pdf".equals(vo)) {
						if(aVoIds!=null && aVoIds.size()>0) {
							new ActionMergePdf(pop, tfinf, "Merge PDF files", aVoIds);
						}
					}
					if("vo:Pdf".equals(vo) || "vo:PdfDoc".equals(vo)) {
//						new ActionPdfAnnoDocCcl(pop, tfinf, "Extract Annotation", id);
						new ActionPdfAnno2Doc(pop, tfinf, "Extract to PdfDoc", id);
					}
					if("vo:PdfDoc".equals(vo)) {
						new PdfDocCreateXml(pop, tfinf, "CCTS XML Generate");
						new PdfDocCreateJson(pop, tfinf, "CCTS JSON Generate");
					}
					if("vo:DataSet".equals(vo)) {
						new DataSet2StdA(pop, tfinf, "Build Data Set");
					}
					if("vo:DataSetReport".equals(vo)) {
						new DataSetHtml(pop, tfinf, "Data Set Html");
					}
					if("vo:DataSetReport".equals(vo)) {
						new DataSetReport(pop, tfinf, "Data Set Report");
					}
				}
			}
			if(aVoIds==null || !aVoIds.contains(id)) {
				JMenuItem add2Ids = new JMenuItem("Add to List");
				add2Ids.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) { add2Ids(vo, id); }});
				pop.add(add2Ids);
			}

			JMenuItem copyId = new JMenuItem("Copy ID");
			copyId.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { copyId(id); }});
			pop.add(copyId);
			if(aVoIds!=null && aVoIds.size()>0) {
				JMenuItem copyIds = new JMenuItem("Copy List");
				copyIds.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) { copyIds(vo); }});
				pop.add(copyIds);
			}
			if(aVoIds!=null && aVoIds.size()>0) {
				JMenuItem clearIds = new JMenuItem("Clear List");
				clearIds.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) { clearIds(vo); }});
				pop.add(clearIds);
			}

			if(aVoIds!=null && aVoIds.size()>0) {
				for(String id : aVoIds) {
					JMenuItem idmenu = new JMenuItem("- "+id);
					idmenu.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							PopiangDigital.aIriTrace.add(id);
							goback.setEnabled(true);
							showIRI(id); }});
					String nm = PopiangUtil.query0(id+" vp:name ?a").get();
					idmenu.setToolTipText(nm);
					pop.add(idmenu);
				}
			}
			if(vo.startsWith("vo:View")) {
				int idx = tabbedPane.getSelectedIndex();
				String tt = tabbedPane.getTitleAt(idx);
				TextFileInfo tfin = hTabPane.get(tt);
				new ViewExecute1Action(pop, tfin, "Excute");
			}
			if(vo.startsWith("vo:Report")) {
				int idx = tabbedPane.getSelectedIndex();
				String tt = tabbedPane.getTitleAt(idx);
				TextFileInfo tfin = hTabPane.get(tt);
				new ReportExecute1(pop, tfin, "Report 1: Camera");
				new ReportExecute2(pop, tfin, "Report 2: Note");
			}
			if(vo.startsWith("vo:Note")) {
				int idx = tabbedPane.getSelectedIndex();
				String tt = tabbedPane.getTitleAt(idx);
				TextFileInfo tfin = hTabPane.get(tt);
				new ReportExecute2(pop, tfin, "Report 2: Note");
			}
			if(vo.startsWith("vo:DGATask")) {
				int idx = tabbedPane.getSelectedIndex();
				String tt = tabbedPane.getTitleAt(idx);
				TextFileInfo tfin = hTabPane.get(tt);
				new DGATask1(pop, tfin, "DGA Task 1");
				new DGATask2(pop, tfin, "DGA Task 2");
			}
			pop.show(e.getComponent(), e.getX(), e.getY());
		}
	}


	boolean bAdd = false;
	public void updateGui() {
		SwingUtilities.invokeLater(new Runnable() { public void run() {
			if(PopiangDigital.aIriTrace.size()>0) goback.setEnabled(true);
			else goback.setEnabled(false);
			Map<String,String> hObj = PopiangDigital.hObject;
			bAdd = false;
			hObj.forEach((k, id) -> {
				RecordedVocabId rvi = hBox.get(k);
				if(rvi==null) {
					rvi = new RecordedVocabId(k, id);
					aBox.add(k);
					hBox.put(k, rvi);
					box.add(rvi);
					rvi.setToolTipText("<html>"+id+":"+k+"</html>");
					rvi.setAlignmentX(Component.LEFT_ALIGNMENT);
					bAdd = true;
				}
				rvi.setId(id);
				rvi.repaint();
			});
			if(bAdd) {
				box.validate();
			}
	    } }); 
	}

	JButton goback = new JButton("goback IRI");

	public Container getGui() {
        if (gui==null) {

			initSearchDialogs();

			gui = new JPanel(new BorderLayout());
			fileSystemView = FileSystemView.getFileSystemView();

			showFind = new ShowFindDialogAction();
			showReplace = new ShowReplaceDialogAction();
			gotoLine = new GoToLineAction();

			root = new DefaultMutableTreeNode();
			treeModel = new DefaultTreeModel(root);

			TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent tse){
					selectedNode = (DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
					showChildren(selectedNode);
				}
			};

			populateAllTreeNode();

			tree = new JTree(treeModel);
			tree.setRootVisible(false);
			tree.addTreeSelectionListener(treeSelectionListener);
			tree.setCellRenderer(new FileTreeCellRenderer());
			tree.setFont(new Font(PopiangDigital.sFontName, 0, PopiangDigital.iFontSize));
			JScrollPane treeScroll = new JScrollPane(tree);
			ToolTipManager.sharedInstance().registerComponent(tree);

			tree.setVisibleRowCount(15);
			Dimension preferredSize = treeScroll.getPreferredSize();
			Dimension widePreferred = new Dimension( 150, (int)preferredSize.getHeight());
			treeScroll.setPreferredSize( widePreferred );


			tabbedPane = new JTabbedPane();
            tabbedPane.setBorder(new EmptyBorder(0,0,0,0));
			tabbedPane.setPreferredSize(new Dimension(300,200));

            gui.add(treeScroll, BorderLayout.WEST);
            gui.add(tabbedPane, BorderLayout.CENTER);

//			gui.setTransferHandler(handler);

			ActionButtonInBox bt;
			ctrl = getToolkit().getMenuShortcutKeyMask();
			shift = InputEvent.SHIFT_MASK;

			ActionButtonInBox.setActionMap(actionMap);

			goback = new JButton("goback IRI");
			goback.setEnabled(false);
			goback.setMargin(new Insets(0, 0, 0, 0));
			goback.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					int i1;
					if((i1=PopiangDigital.aIriTrace.size())>0) {
						String iri = PopiangDigital.aIriTrace.get(i1-1);
						if(i1>1) PopiangDigital.aIriTrace.remove(i1-1);
						System.out.println("GO BACK: "+i1+" : "+ iri);
						showIRI(iri);
						if(i1==1) goback.setEnabled(false);
					}
				}});
			box.add(goback);

			JScrollPane sclPane = new JScrollPane(box);
			sclPane.setPreferredSize(new Dimension(150,100));
			gui.add(sclPane, BorderLayout.EAST);

			statusBar = new StatusBar();
			gui.add(statusBar, BorderLayout.SOUTH);

			KeyStroke keyS = KeyStroke.getKeyStroke(KeyEvent.VK_S, ctrl);
			actionMap.put(keyS, new AbstractAction("actionS") { @Override
				public void actionPerformed(ActionEvent e) { saveFile(); } });
			KeyStroke keyI = KeyStroke.getKeyStroke(KeyEvent.VK_I, ctrl);
			actionMap.put(keyI, new AbstractAction("actionI") { @Override
				public void actionPerformed(ActionEvent e) { newId(); } });
			KeyStroke keyPl = KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ctrl);
			actionMap.put(keyPl, new AbstractAction("actionPl") { @Override
				public void actionPerformed(ActionEvent e) { addRdfFileAction(); } });
			KeyStroke keyN = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ctrl);
			actionMap.put(keyN, new AbstractAction("actionN") { @Override
				public void actionPerformed(ActionEvent e) { delRdfFileAction(); } });
			KeyStroke keyW = KeyStroke.getKeyStroke(KeyEvent.VK_W, ctrl);
			actionMap.put(keyW, new AbstractAction("actionW") { @Override
				public void actionPerformed(ActionEvent e) { clsRdfFileAction(); } });
			KeyStroke keyO = KeyStroke.getKeyStroke(KeyEvent.VK_O, ctrl);
			actionMap.put(keyO, new AbstractAction("actionO") { @Override
				public void actionPerformed(ActionEvent e) { foldAll(); } });

			KeyStroke key1 = KeyStroke.getKeyStroke(KeyEvent.VK_1, ctrl);
			actionMap.put(key1, new AbstractAction("action1") { @Override
				public void actionPerformed(ActionEvent e) { popupMenu(); } });

			KeyStroke key2 = KeyStroke.getKeyStroke(KeyEvent.VK_2, ctrl);
			actionMap.put(key2, new AbstractAction("action2") { @Override
				public void actionPerformed(ActionEvent e) { addClassDialog(); } });

			KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
			kfm.addKeyEventDispatcher( new KeyEventDispatcher() {@Override
				public boolean dispatchKeyEvent(KeyEvent e) {
					KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
					if ( actionMap.containsKey(keyStroke) ) {
						final Action a = actionMap.get(keyStroke);
						final ActionEvent ae = new ActionEvent(e.getSource(), e.getID(), null );
						a.actionPerformed(ae);
					}
					return false;
				}});
        }
        return gui;
    }

	
	void addClassDialog() {
		String cls = JOptionPane.showInputDialog("Please input a value");
		System.out.println("CLS:"+cls);
	}

/*
	private TransferHandler handler = new TransferHandler() {
		public boolean canImport(TransferHandler.TransferSupport support) {
			if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				return false;
			}
			boolean copySupported = (COPY & support.getSourceDropActions()) == COPY;
			if (!copySupported) {
				return false;
			}
			support.setDropAction(MOVE);
			return true;
		}
		public boolean importData(TransferHandler.TransferSupport support) {
			if (!canImport(support)) {
				return false;
			}
			Transferable t = support.getTransferable();
			try {
				java.util.List<File> l =
					(java.util.List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
				for (File f : l) {
					String fn = f.getName();
					if(fn.toUpperCase().endsWith(".PDF")) {
						dropPdf(f);
					} else {
					}
				}
			} catch (UnsupportedFlavorException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
			return true;
		}
	};

	void dropPdf(File f) {
		System.out.println("DROP PDF: "+f);
		try {
			PDDocument pddoc = PDDocument.load(f);
			PDFRenderer pdfrnd = new PDFRenderer(pddoc);
			Iterable<PDPage> allPages = pddoc.getDocumentCatalog().getPages();
			int pno = 0;
			for(PDPage pg : allPages) {
				PDRectangle prct = pg.getMediaBox();
				float llx = prct.getLowerLeftX();
				float lly = prct.getLowerLeftY();
				float urx = prct.getUpperRightX();
				float ury = prct.getUpperRightY();
				pno++;
				List<PDAnnotation> aAnn = pg.getAnnotations();
				if(aAnn.size()==0) continue;
				log.info("page:"+pno+"  "+llx+","+lly+"  "+urx+","+ury);
				int ano = 0;
//				BufferedImage bi = pdfrnd.renderImageWithDPI(pno, 300, ImageType.RGB);
				for(PDAnnotation ann: aAnn) {
					ano++;
					String txt = ann.getContents();
					String typ = ann.getSubtype();
					PDRectangle rct = ann.getRectangle();
					if("FreeText".equals(typ)) {
						llx = rct.getLowerLeftX();
						lly = rct.getLowerLeftY();
						urx = rct.getUpperRightX();
						ury = rct.getUpperRightY();
						log.info("    "+ano+": ["+ txt+"] "+llx+","+lly+"  "+urx+","+ury);
					} else if("Highlight".equals(typ)) {
						log.info("    "+ano+": Highlight");
					} else if("Square".equals(typ)) {
						log.info("    "+ano+": Square");
					} else {
						log.info("    "+ano+" :"+typ);
					}
				}
			}
			pddoc.close();
		} catch(Exception z) {
			z.printStackTrace();
		}
	}
*/

	void populateAllTreeNode() {
		root.removeAllChildren();
//		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
//		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
		for (File infile : PopiangDigital.aRdfFold) {
			String nm = infile.getName();
			if(nm.startsWith(".")) continue;
			if(!infile.isDirectory()) continue;
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(infile);
			root.add( node );
			populateTreeNode(node, infile);
		}
	}

	void populateTreeNode(DefaultMutableTreeNode node, File infile) {
		node.removeAllChildren();
		File[] files = fileSystemView.getFiles(infile, true);
		Arrays.sort(files, Collections.reverseOrder());
		for (File file : files) {
//log.info(" file node:" + file);
			node.add(new DefaultMutableTreeNode(file));
		}
	}

	AbstractAction showFind, showReplace, gotoLine;
	public String lastTip = "";
	int lastX, lastY;
	JComponent lastComp;

	public String getToolTipText(RTextArea textArea, MouseEvent e) {
		TurtleTextArea tta = (TurtleTextArea) textArea;
		Point at = e.getPoint();
		int off = textArea.viewToModel(at);
		String tip = tta.getToolTipText(off);
		if(tip!=null) {
			if(tip.startsWith("'http://") && tip.endsWith("'")) {
				tip = tip.substring(1,tip.length()-1);
			} else if(tip.startsWith("\"http://") && tip.endsWith("\"")) {
				tip = tip.substring(1,tip.length()-1);
			} else if(tip.startsWith("'https://") && tip.endsWith("'")) {
				tip = tip.substring(1,tip.length()-1);
			} else if(tip.startsWith("\"https://") && tip.endsWith("\"")) {
				tip = tip.substring(1,tip.length()-1);
			} else if(tip.startsWith("'file:") && tip.endsWith("'")) {
				tip = tip.substring(1,tip.length()-1);
			} else if(tip.length()>20) {
				tip = tip.substring(0,20)+"...";
			}
		}
		lastTip = tip;
		lastX = at.x;
		lastY = at.y;
		lastComp = tta;
		String[] wds;
		if(tip!=null && tip.matches("[0-9A-Za-z]+:[0-9A-Z-a-z]*")
		  && (wds=tip.split(":"))!=null && wds.length==2) {
			String id = tip;
//System.out.println("... tip menu: "+ id);
			String vo = PopiangUtil.query0(id + " a ?vo .").get();
			String nm = PopiangUtil.query0(id + " vp:name ?a .").get();
//System.out.println("   vo:"+vo+" nm:"+nm);
			if(vo==null && nm==null) {
			} else if(vo==null && nm!=null) {
				tip = id + " = "+nm;
			} else if(vo!=null && nm==null) {
				PopiangDigital.setObject(vo, id);
				tip = id + "("+vo+")";
			} else if(vo!=null && nm!=null) {
				PopiangDigital.setObject(vo, id);
				tip = id + "("+vo+") = "+nm;
			}
		}

		return tip;
	}

    public void showRootFile() {
        tree.setSelectionInterval(0,0);
    }

/*
    private TreePath findTreePath(File find) {
        for (int ii=0; ii<tree.getRowCount(); ii++) {
            TreePath treePath = tree.getPathForRow(ii);
            Object object = treePath.getLastPathComponent();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)object;
            File nodeFile = (File)node.getUserObject();
System.out.println("findTreePath: "+ nodeFile);

            if (nodeFile==find) {
                return treePath;
            }
        }
        return null;
    }
*/

	public TextFileInfo getTextFileInfo(File file) {
		TextFileInfo tinf = null;
		try {
			String d0 = curDir.getAbsolutePath();
			String d1 = file.getAbsolutePath();
			String tn = d1.substring(d0.length()+1);
			if((tinf=hTabPane.get(tn))==null) {
				String fs = file.getAbsolutePath();
				Path fileName = Paths.get(fs);
				byte[] buf = Files.readAllBytes(fileName);
				String actual = new String(buf,"UTF-8");
				tinf = new TextFileInfo();
				tinf.wind = this;

				TurtleTextArea tta = newTurtleTextArea(tinf);
				tta.setText(actual);
				tta.setDirty(false);
				tta.discardAllEdits();
				tta.setFirstFold(true);
				tta.setToolTipSupplier(PopiangWindow.this);
				tta.setSelectedTextColor(Color.red);
				RTextScrollPane tasp = new RTextScrollPane(tta);
				tabbedPane.add(tasp, tn);
				int idx = tabbedPane.indexOfComponent(tasp);
				tabbedPane.setTabComponentAt(idx, getTitlePanel(tabbedPane, tasp, tn));
				tabbedPane.setSelectedIndex(idx);
	
				tinf.text = tta;
				tinf.pane = tasp;
				tinf.path = fileName;

				DropFilesInRdf drp = new DropFilesInRdf(tinf);
				tinf.text.setTransferHandler(drp);

				hTabPane.put(tn, tinf);
			} else {
				int idx = tabbedPane.indexOfComponent(tinf.pane);
				tabbedPane.setSelectedIndex(idx);
			}
		} catch(Exception z) {
		}
		return tinf;
	}

	private void showChildren(final DefaultMutableTreeNode node) {
//		tree.setEnabled(false);

		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
			@Override
			public Void doInBackground() {
				File file = (File) node.getUserObject();
				if (file.isDirectory()) {
/*
					//File[] files = fileSystemView.getFiles(file, true); //!!
					File[] files = fileSystemView.getFiles(file, false); //!!
System.out.println("show children 3:"+files.length+" : "+node.isLeaf());
					Arrays.sort(files);
					if (node.isLeaf()) {
						for (File child : files) {
System.out.println("show children 4:"+child);
							publish(child);
						}
					}
*/
				} else {
					String fn = file.getName();
					if(fn.endsWith(".ttl") || fn.endsWith(".qry")) {
						TextFileInfo tfinf = getTextFileInfo(file);
					}
				}
   				return null;
			}

/*
			@Override
			protected void process(List<File> chunks) {
System.out.println("show children 5:");
				for (File child : chunks) {
					System.out.println(".."+child);
					node.add(new DefaultMutableTreeNode(child));
				}
			}
			@Override
			protected void done() {
System.out.println("show children 7:");
				tree.setEnabled(true);
			}
*/
        };
		worker.execute();
    }

	void populate(JPopupMenu popMenu, TextFileInfo tfinf) {
		int i1;
		popMenu.removeAll();
		if(lastTip==null) {
		} else if(lastTip.startsWith("http:") || lastTip.startsWith("https:")) {
			new OpenLinkAction(popMenu, tfinf, "Open Link", lastTip);
		} else if(lastTip.startsWith("file:")) {
			new OpenFileAction(popMenu, tfinf, "Open File", lastTip);
			new BrowseFileAction(popMenu, tfinf, "Browse File", lastTip);
			new Browse360Action(popMenu, tfinf, "Browse 360", lastTip);
		} else if((i1=lastTip.indexOf(":"))>0) {
			new OpenRdfToIRI(popMenu, tfinf, "Goto IRI", lastTip);
		}
		new AddRdfClassObject(popMenu, tfinf, "Add Class Object");
		new AddRdfAction(popMenu, tfinf, "Add URL", "vo:URL");
		new AddRdfIdToList(popMenu, tfinf, "Add ID to List");
		new AddRdfAction(popMenu, tfinf, "Add Note", "vo:Note");
		new AddRdfImageAction(popMenu, tfinf, "Add Image");
		new AddRdfPointOnEarth(popMenu, tfinf, "Add Point");
		new AddRdfPointOnEarth(popMenu, tfinf, "Add Node", "vo:Node");
		new AddRdfCamera(popMenu, tfinf, "Add Camera");
		new AddRdfAction(popMenu, tfinf, "Add Place", "vo:Place");
		new AddRdfPrefix(popMenu, tfinf, "Add Prefix");
		new ClearToEnd(popMenu, tfinf, "Clear2End");
		new DupTextWithNewId(popMenu, tfinf, "Duplicate");
		new AddRdfAction(popMenu, tfinf, "Add Query", "vo:Query");
		new QueryAction(popMenu, tfinf, "Execute");
		new AddRdfTitle(popMenu, tfinf, "Add Title");
		new CopyTipToClibboard(popMenu, tfinf, "Copy Tip");
		new FixThaiSpell(popMenu, tfinf, "Fix Thai");

		popMenu.addSeparator();
		popMenu.add(showFind);
		popMenu.add(showReplace);
		popMenu.add(gotoLine);
	}

	TurtleTextArea newTurtleTextArea(TextFileInfo tfi) {
		return new TurtleTextArea(tfi) {
			@Override
			public JPopupMenu getPopupMenu() {
				int i1;
				JPopupMenu popMenu = super.getPopupMenu();
				populate(popMenu, tfinf);
				return popMenu;
			}
		};
	}

	private JPanel getTitlePanel(final JTabbedPane tabbedPane
		, RTextScrollPane panel, String title) {
		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		titlePanel.setOpaque(false);
		JLabel titleLbl = new JLabel(title);
		titleLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		titlePanel.add(titleLbl);
		return titlePanel;
	}

	JFrame f;
	Toolkit getToolkit() {
		return f.getToolkit();
	}

	public void proc(File dir) {
		log.setLevel(Level.INFO);
		log.info("=========== PROC =========");

		curDir = dir;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
					UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);
				} catch(Exception weTried) {
				}
				String title = PopiangDigital.sName
					+" ("+PopiangDigital.sPrefix+") "
					+PopiangDigital.sEmail;
				f = new JFrame(title);
				PopiangDigital.frame = f;
				//f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				f.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent we) { 
						checkToClose();
					}
				});

				f.setContentPane(getGui());

				try {
					URL urlBig = getClass().getResource("fb-icon-32x32.png");
					URL urlSmall = getClass().getResource("fb-icon-16x16.png");
					ArrayList<Image> images = new ArrayList<Image>();
					images.add( ImageIO.read(urlBig) );
					images.add( ImageIO.read(urlSmall) );
					f.setIconImages(images);
				} catch(Exception weTried) {}

				f.pack();
				f.setLocationByPlatform(true);
				f.setMinimumSize(f.getSize());
				f.setVisible(true);

				showRootFile();
			}
		});
	}

	void checkToClose() {
		int cnt = tabbedPane.getTabCount();
		int cDirt = 0;
		for(int i=0; i<cnt; i++) {
			String tt = tabbedPane.getTitleAt(i);
			TextFileInfo tfin = hTabPane.get(tt);
			if(tfin.text.isDirty()) cDirt++;
		}
		String ObjButtons[] = {"Yes","No"};
		int PromptResult = JOptionPane.showOptionDialog(null, 
			(cDirt==0? "" : cDirt + " files modified\n")+ 
			"Are you sure you want to exit?", 
			"Close Confirmation ", 
			JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, 
		ObjButtons,ObjButtons[1]);
		if(PromptResult==0) {
			System.exit(0);          
		}
	}

	private void initSearchDialogs() {
		findDialog = new FindDialog(f, this);
		replaceDialog = new ReplaceDialog(f, this);
		SearchContext context = findDialog.getSearchContext();
		replaceDialog.setSearchContext(context);
	}

	private class GoToLineAction extends AbstractAction {

		GoToLineAction() {
			super("Go To Line...");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, c));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
			if (findDialog.isVisible()) {
				findDialog.setVisible(false);
			}
			if (replaceDialog.isVisible()) {
				replaceDialog.setVisible(false);
			}
			//GoToDialog dialog = new GoToDialog(RSTAUIDemoApp.this);
			GoToDialog dialog = new GoToDialog(f);
			int idx = tabbedPane.getSelectedIndex();
			String tt = tabbedPane.getTitleAt(idx);
			TextFileInfo tfin = hTabPane.get(tt);
			dialog.setMaxLineNumberAllowed(tfin.text.getLineCount());
			dialog.setVisible(true);
			int line = dialog.getLineNumber();
			if (line>0) {
				try {
					tfin.text.setCaretPosition(tfin.text.getLineStartOffset(line-1));
				} catch (BadLocationException ble) { // Never happens
					UIManager.getLookAndFeel().provideErrorFeedback(tfin.text);
					ble.printStackTrace();
				}
			}
			} catch(Exception z) {}
		}

	}

	@Override
	public String getSelectedText() {
		try {
			int idx = tabbedPane.getSelectedIndex();
			String tt = tabbedPane.getTitleAt(idx);
			TextFileInfo tfin = hTabPane.get(tt);
			return tfin.text.getSelectedText();
		} catch(Exception z) { return null; }
	}

	private class ShowFindDialogAction extends AbstractAction {

		ShowFindDialogAction() {
			super("Find...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (replaceDialog.isVisible()) {
				replaceDialog.setVisible(false);
			}
			findDialog.setVisible(true);
		}

	}

	private class ShowReplaceDialogAction extends AbstractAction {

		ShowReplaceDialogAction() {
			super("Replace...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (findDialog.isVisible()) {
				findDialog.setVisible(false);
			}
			replaceDialog.setVisible(true);
		}

	}

	JLabel label;

	DefaultMutableTreeNode getFolderNode() {
		TreePath trpth = tree.getSelectionPath();
		TreePath tp0=null, tp1=null, tp2=null;
		while(trpth!=null) {
			tp2=tp1; tp1=tp0; tp0=trpth;
			trpth = trpth.getParentPath();
		}
		if(tp1==null) return null;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp1.getLastPathComponent();
		return node;
	}

	String getNodePrefix0(DefaultMutableTreeNode node) {
		String sele = node.toString();
		String rdfs = PopiangDigital.workDir+"/rdf/";
		String sel0 = sele.substring(rdfs.length());
//System.out.println("sele: "+ sele);
//System.out.println("rdfs: "+ rdfs);
		int i1 = sel0.indexOf("/");
//System.out.println(" i: "+ i1);
		if(i1>0) sel0 = sel0.substring(0,i1);
//System.out.println("sel0: "+ sel0);
		return sel0;
	}
	String getNodePrefix1(DefaultMutableTreeNode node) {
		String sele = node.toString();
		String rdfs = PopiangDigital.workDir+"rdf/";
		String sel0 = sele.substring(rdfs.length());
System.out.println("sele: "+ sele);
System.out.println("rdfs: "+ rdfs);
		int i1 = sel0.indexOf("/");
System.out.println(" i: "+ i1);
		if(i1>0) sel0 = sel0.substring(0,i1);
System.out.println("sel0: "+ sel0);
		return sel0;
	}

	String getRdfIdMax0(String prfx) {
//System.out.println("prfx: "+ prfx);
		String rdfs = PopiangDigital.workDir+"/rdf/";
		int ln = prfx.length();
		File fdir = new File(rdfs+prfx);
		String mx = "00", mx2="";
//System.out.println("RDF MAX: "+ fdir);
		if(fdir!=null) {
			for(String nm : fdir.list()) {
				String ord = nm.substring(ln,ln+2);
				if(!ord.matches("[0-9A-Z][0-9A-Z]")) continue;
				if(ord.compareTo(mx)>0) mx = ord;
			}
			mx2 = PopiangUtil.nextMax(mx);
		} else {
			mx2 = "01";
		}
		return mx2;
	}

/*
	String getRdfIdMax1(String prfx) {
//System.out.println("prfx: "+ prfx);
		String rdfs = PopiangDigital.workDir+"/rdf/";
		int ln = prfx.length();
		File fdir = new File(rdfs+prfx);
		String mx = "00";
//System.out.println("RDF MAX: "+ fdir);
		for(String nm : fdir.list()) {
//System.out.println("    NAM: "+ nm);
			String ord = nm.substring(ln,ln+2);
			if(!ord.matches("[0-9A-Z][0-9A-Z]")) continue;
			if(ord.compareTo(mx)>0) mx = ord;
		}
		String mx2 = PopiangUtil.nextMax(mx);
		return mx2;
	}
*/

	String blankRdf(String fnm) {
		String base = PopiangDigital.sBaseIRI;
		String pre = "@prefix "+fnm+": <"+base+"/"+fnm+"#> .\n"
			+"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n"
			+"@prefix vo: <"+base+"/vo#> .\n"
			+"@prefix vp: <"+base+"/vp#> .\n\n";

		String str = pre+ fnm+":0		rdf:title  \"\" .\n";
		return str;
	}

	void addRdfFileAction() {
		DefaultMutableTreeNode node = getFolderNode();
		if(node==null) return;
		String rdfs = PopiangDigital.workDir+"/rdf/";
		String prfx = getNodePrefix0(node);
		String mx2 = getRdfIdMax0(prfx);
		String fnm = prfx+mx2;

		TreePath path = new TreePath(node.getPath());
		tree.expandPath(path);
		File rdf0 = new File(rdfs+prfx+"/"+fnm+".ttl");

		String ObjButtons[] = {"Yes","No"};
		int PromptResult = JOptionPane.showOptionDialog(null, 
			"Do you want to add a new file "+ rdf0.getName()+" ?", 
			"Add File Confirmation ", 
			JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, 
		ObjButtons,ObjButtons[1]);
		if(PromptResult!=0) { return; }

		try {
			label.setForeground(Color.BLACK);
			label.setText("Add file "+mx2);

			Path pth = Paths.get(rdf0.getAbsolutePath());

			String str = blankRdf(fnm);
			Files.write(pth, str.getBytes());
		} catch(Exception z) {
		}
		populateTreeNode(node, new File(rdfs+prfx));
		((DefaultTreeModel) tree.getModel()).reload();
		path = new TreePath(node.getFirstLeaf().getPath());
		tree.setSelectionPath(path);
	}

	void delRdfFileAction() {
		TreePath trpth = tree.getSelectionPath();
		TreePath tp0=null, tp1=null, tp2=null;
		while(trpth!=null) {
			tp2=tp1; tp1=tp0; tp0=trpth;
			trpth = trpth.getParentPath();
		}
//		System.out.println("path0 : " + tp1);
		if(tp1==null || tp2==null) return;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp2.getLastPathComponent();
		DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) tp1.getLastPathComponent();

System.out.println("node1: "+ node1);
System.out.println("node: "+ node);
		File fdir = new File(node1.toString());
		String sele = node.toString();
		String rdfs = PopiangDigital.workDir+"/rdf/";
		String sel0 = sele.substring(rdfs.length());
		int i1 = sel0.indexOf("/");
		if(i1>0) sel0 = sel0.substring(0,i1);
System.out.println("sel0: "+ sel0);
		if("etc".equals(sel0)) return;
		String prfx = sel0;

System.out.println("curdir: "+ fdir);

		int id = tabbedPane.getSelectedIndex();
		if(id<0) return;
		String ttl = tabbedPane.getTitleAt(id);
		TextFileInfo tinf = hTabPane.get(ttl);
		int dialogResult = JOptionPane.showConfirmDialog (null
			, "Delete File ","Warning",JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){
			ttl = ttl.substring(4);
//			String tx = tinf.text.getText();
System.out.println("ttl:"+ttl);
			tinf.path.toFile().delete();
			hTabPane.remove(ttl);
			tabbedPane.remove(tinf.pane);

//			PopiangDigital.wind.populateAllTreeNode();
			populateTreeNode(node1, fdir);
			((DefaultTreeModel) tree.getModel()).reload();
			TreePath path = new TreePath(node1.getPath());
			tree.expandPath(path);

			log.info("=========== DELETE FILE ========= "+ttl);
			label.setForeground(Color.BLACK);
			label.setText("Del file");
		} else {
			return;
		}
	}

	void clsRdfFileAction() {
			int id = tabbedPane.getSelectedIndex();
			if(id<0) return;
			String ttl = tabbedPane.getTitleAt(id);
			TextFileInfo tinf = hTabPane.get(ttl);
			if(tinf.text.isDirty()) {
				int dialogResult = JOptionPane.showConfirmDialog (null, 
					"This doc has been modified\n"+
					"Do you want to save ?",
					"Modified",JOptionPane.YES_NO_OPTION);
				if(dialogResult == JOptionPane.YES_OPTION){
					String tx = tinf.text.getText();
					try {
						Files.write(tinf.path, tx.getBytes("UTF-8"));
					} catch(IOException x) {
					}
					hTabPane.remove(ttl);
					tabbedPane.remove(tinf.pane);
				} else if(dialogResult == JOptionPane.NO_OPTION){
					hTabPane.remove(ttl);
					tabbedPane.remove(tinf.pane);
				} else {
				}
			} else {
				hTabPane.remove(ttl);
				tabbedPane.remove(tinf.pane);
			}
	}
	class StatusBar extends JPanel {


		StatusBar() {
			JPanel x = new JPanel();
			setLayout(new BorderLayout());
			x.setLayout(new FlowLayout(FlowLayout.LEFT));
			add(x, BorderLayout.LINE_START);

			label = new JLabel("Ready");
			label.setPreferredSize(new Dimension(100,20));
			add(label, BorderLayout.CENTER);

			JButton btAdd = new JButton("+");
			x.add(btAdd);
			btAdd.setMargin(new Insets(0, 0, 0, 0));
			btAdd.addActionListener(new ActionListener(){ public void
				actionPerformed(ActionEvent a) { addRdfFileAction(); }});

/*
			JButton btQry = new JButton("+Q");
			x.add(btQry);
			btQry.setMargin(new Insets(0, 0, 0, 0));
			btQry.addActionListener(new ActionListener(){ public void
				actionPerformed(ActionEvent a) { addQryFileAction(); }});
*/

			JButton btDel = new JButton("-");
			x.add(btDel);
			btDel.setMargin(new Insets(0, 0, 0, 0));
			btDel.addActionListener(new ActionListener(){ public void
				actionPerformed(ActionEvent a) { delRdfFileAction(); }});

			JButton btSave = new JButton("save(s)");
			x.add(btSave);
			btSave.setMargin(new Insets(0, 0, 0, 0));
			btSave.addActionListener(new ActionListener(){ public void
				actionPerformed(ActionEvent a) { saveFile(); }});

/*
			JButton btMod = new JButton("model(m)");
			x.add(btMod);
			btMod.setMargin(new Insets(0, 0, 0, 0));
			btMod.addActionListener(new ActionListener(){ public void
				actionPerformed(ActionEvent a) { PopiangUtil.readAllModel(); }});
*/

			JButton btCls = new JButton("close(w)");
			x.add(btCls);
			btCls.setMargin(new Insets(0, 0, 0, 0));
			btCls.addActionListener(new ActionListener(){ public void
				actionPerformed(ActionEvent a) { clsRdfFileAction(); }});

			JButton btNew = new JButton("newid(i)");
			x.add(btNew);
			btNew.setMargin(new Insets(0, 0, 0, 0));
			btNew.addActionListener(new ActionListener(){ public void
				actionPerformed(ActionEvent a) { newId(); }});

			JButton btFld = new JButton("fold(o)");
			x.add(btFld);
			btFld.setMargin(new Insets(0, 0, 0, 0));
			btFld.addActionListener(new ActionListener(){ public void
				actionPerformed(ActionEvent a) { foldAll(); }});

			JButton btGIF = new JButton("egif");
			x.add(btGIF);
			btGIF.setMargin(new Insets(0, 0, 0, 0));
			btGIF.addActionListener(new ActionListener(){ public void
				actionPerformed(ActionEvent a) { egif1(); }});

			JButton btENC = new JButton("enc");
			x.add(btENC);
			btENC.setMargin(new Insets(0, 0, 0, 0));
			btENC.addActionListener(new ActionListener(){ public void
				actionPerformed(ActionEvent a) { enc1(); }});

/*
			JButton btGit = new JButton("git(p)");
			x.add(btGit);
			btGit.setMargin(new Insets(0, 0, 0, 0));
			btGit.addActionListener(new ActionListener(){ public void
				actionPerformed(ActionEvent a) { pushGit(); }});

			JButton btEmp = new JButton("emp(e)");
			x.add(btEmp);
			btEmp.setMargin(new Insets(0, 0, 0, 0));
			btEmp.addActionListener(new ActionListener(){ public void
				actionPerformed(ActionEvent a) { addEmpRdf(); }});

			JButton btAtt = new JButton("att(t)");
			x.add(btAtt);
			btAtt.setMargin(new Insets(0, 0, 0, 0));
			btAtt.addActionListener(new ActionListener(){ public void
				actionPerformed(ActionEvent a) { PopiangUtil.attendReset(); }});

			JButton btRep2 = new JButton("repo(2)");
			x.add(btRep2);
			btRep2.setMargin(new Insets(0, 0, 0, 0));
			btRep2.addActionListener(new ActionListener(){ public void
				actionPerformed(ActionEvent a) { makeAttendReport(); }});

			JButton btScan = new JButton("scan(3)");
			x.add(btScan);
			btScan.setMargin(new Insets(0, 0, 0, 0));
			btScan.addActionListener(new ActionListener(){ public void
				actionPerformed(ActionEvent a) { makeScanFile(); }});

			JButton btRfr = new JButton("refr(s)");
			x.add(btRfr);
			btRfr.setMargin(new Insets(0, 0, 0, 0));
			btRfr.addActionListener(new ActionListener(){ public void
				actionPerformed(ActionEvent a) { treeRefresh(); }});

			JButton btXls = new JButton("xls");
			x.add(btXls);
			btXls.setMargin(new Insets(0,0,0,0));
			btXls.addActionListener(new ActionListener() { public void
				actionPerformed(ActionEvent a) { excelRead(); }});

			if(PopiangDigital.sNodeType==PopiangDigital.mainNode) {

				JButton btJoin = new JButton("join(j)");
				x.add(btJoin);
				btJoin.setMargin(new Insets(0, 0, 0, 0));
				btJoin.addActionListener(new ActionListener(){ public void
					actionPerformed(ActionEvent a) { gitJoin(); }});
			}
*/

		}

		void setLabel(String l) {
			label.setForeground(Color.BLACK);
			label.setText(l);
			//this.label.setText(label);
		}
	}

	void enc1() {
		try {
		String xmlFile = "employee.xml";
		String encryptedFile = "encrypted.xml";
		String decryptedFile = "decrypted.xml";

		Document document = XMLUtil.getDocument(xmlFile);
		Element e = document.getDocumentElement();

		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048);
		KeyPair keyPair = generator.generateKeyPair();
		RSAPrivateKey priv = (RSAPrivateKey) keyPair.getPrivate();
		RSAPublicKey pub = (RSAPublicKey) keyPair.getPublic();

		SecretKey secretKey = SecretKeyUtil.getSecretKey("AES");

//		System.out.println("prx:"+e.getPrefix()+" uri:"+e.getNamespaceURI()+" lo:"+e.getLocalName());
//		QName qname = (e.getPrefix()!=null)? 
//			new QName(e.getNamespaceURI(), e.getLocalName(), e.getPrefix())
//			: new QName(e.getNamespaceURI(), e.getLocalName());
//		XMLEncryptionUtil.encryptElementInDocument(document, pub, secretKey, 0, qname, true);

/*
		XMLEncryptionUtil.encryptElement(document, e, pub, secretKey, 0);
		XMLUtil.saveDocumentTo(document, encryptedFile);
		Document decryptedDoc = XMLUtil.getDocument(encryptedFile);
		XMLEncryptionUtil.decryptElementInDocument(decryptedDoc, priv);
		XMLUtil.saveDocumentTo(decryptedDoc, decryptedFile);
*/

		Document encryptedDoc = XMLUtil.encryptDocument(document, secretKey,
		XMLCipher.AES_128);
		XMLUtil.saveDocumentTo(encryptedDoc, encryptedFile);

		Document decryptedDoc = XMLUtil.decryptDocument(encryptedDoc,
		secretKey, XMLCipher.AES_128);
		XMLUtil.saveDocumentTo(decryptedDoc, decryptedFile);

/*
*/
		System.out.println("Done");
		} catch(Exception z) {
			z.printStackTrace();
		}
	}

	void egif1() {
		THEGifProc proc = new THEGifProc();
		proc.gen(PopiangDigital.workDir);
	}

	void excelRead() {
		log.info("read excel");
		File xls = new File(PopiangDigital.workDir+"/res/xlsx/tnw01-2021-07-03.xlsx");
		try {
			FileInputStream inputStream = new FileInputStream(xls);
			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();
         
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					CellType type = cell.getCellType();
					if(type==CellType.STRING) {
						System.out.print(cell.getStringCellValue());
					} else if(type==CellType.BOOLEAN) {
						System.out.print(cell.getBooleanCellValue());
					} else if(type==CellType.NUMERIC && 
						HSSFDateUtil.isCellDateFormatted(cell)) {
						java.util.Date date = cell.getDateCellValue();
						System.out.print(date);
					} else if(type==CellType.NUMERIC) {
						System.out.print(cell.getNumericCellValue());
					}
/*
					switch (type) {
					case CellType.STRING:
						break;
					case CellType.BOOLEAN:
						break;
					case CellType.NUMERIC:
						break;
					}
*/
					System.out.print(" - ");
				}
				System.out.println();
			}
         
			workbook.close();
			inputStream.close();
		} catch(Exception x) {
x.printStackTrace();
		}
	}

	void treeRefresh() {
		populateAllTreeNode();
		((DefaultTreeModel) tree.getModel()).reload();
	}

	String[][] saPref;

	void makeEmpList() {
		log.info("making employee list");
		PopiangUtil.makeStaffList();
	}

	void readAllModel() {
		try {
log.info("BASE: "+ PopiangDigital.sBaseIRI);
			Enumeration<String> ePrf = PopiangDigital.hRdfModelInfo.keys();
			allModel = ModelFactory.createDefaultModel();
			List<String[]> sapref = new ArrayList<>();
			while(ePrf.hasMoreElements()) {
				String key = ePrf.nextElement();
				RdfModelInfo rmi = PopiangDigital.hRdfModelInfo.get(key);
				sapref.add(new String[] {rmi.sPrefix, rmi.sBaseIRI});
				allModel.add(rmi.model);
			}
			saPref = new String[sapref.size()][];
			saPref = sapref.toArray(saPref);
		} catch(Exception z) {
			log.error(z);
		}
	}

	void pushGit() {
		log.info("push: "+ PopiangDigital.sNodeType);
		if(PopiangDigital.sNodeType==PopiangDigital.mainNode) {
			log.info("url: "+ PopiangDigital.sMainRepo);
			try {
				PopiangUtil.gitPushMain(PopiangDigital.sMainRepo);
			} catch(Exception z) {
				log.error(z);
			}
		} else if(PopiangDigital.sNodeType==PopiangDigital.workNode) {
			log.info("url: "+ PopiangDigital.sMainRepo);
			log.info("own: "+ PopiangDigital.sWorkRepo);
			try {
				PopiangUtil.gitPushWork(PopiangDigital.sMainRepo
					, PopiangDigital.sWorkRepo, PopiangDigital.sPrefix);
			} catch(Exception z) {
				log.error(z);
			}
		}
	}

	
	JPopupMenu popMenu;

	void popupMenu() {
		SwingUtilities.invokeLater(new Runnable() { public void run() {
			if(popMenu==null) { popMenu = new JPopupMenu(); }
			int id = tabbedPane.getSelectedIndex();
			String ttl = tabbedPane.getTitleAt(id);
			TextFileInfo tinf = hTabPane.get(ttl);
			populate(popMenu, tinf);
			Point pnt = tinf.text.getCaret().getMagicCaretPosition();
			if(pnt==null) {
				pnt = MouseInfo.getPointerInfo().getLocation();
				popMenu.show(null, (int)pnt.getX(), (int)pnt.getY());
			} else {
				popMenu.show(tinf.text, (int)pnt.getX(), (int)pnt.getY());
			}
			popMenu.requestFocus();
			popMenu.requestFocusInWindow();
		}});
	}

	String readRdf(String rdfn, String id1, String id2) {
		if(rdfn.matches("[a-z]+[0-9A-Z][0-9A-Z]\\.qry")) {
			try {
			String fn = rdfn.substring(0,rdfn.length()-4);
			String dn = fn.substring(0,fn.length()-2);
			String f1 = PopiangDigital.workDir+"/rdf/"+dn+"/"+rdfn;
			String c1 = new String ( Files.readAllBytes( Paths.get(f1) ) );
			c1 = c1.replace(id1,id2);
			return c1;
			} catch(Exception z) {}
		}
		return null;
	}

	public void drawString(Graphics2D g2, String tx, int x, int y) {
		FontMetrics fm = g2.getFontMetrics();
		int x0 = x - fm.stringWidth(tx) / 2;
		int y0 = y + fm.getAscent() - (fm.getAscent() + fm.getDescent()) / 2;
		g2.drawString(tx, x0, y0);
	}

	public void drawString(Graphics2D g2, String tx, int x, int y, int s) {
		FontMetrics fm = g2.getFontMetrics();
		int x0 = x - fm.stringWidth(tx) / 2;
		int y0 = y + fm.getAscent() - (fm.getAscent() + fm.getDescent()) / 2;
		y0 += s * (fm.getAscent() + fm.getDescent());
		g2.drawString(tx, x0, y0);
	}

/*
	String qprf = "@prefix qr: <http://popiang.com/rdf/qr#> .\n"
		+"@prefix vo: <http://popiang.com/rdf/vo#> .\n"
		+"@prefix vp: <http://popiang.com/rdf/vp#> .\n"
		+"qr:1	a	vo:Query;\n"
		+"vp:query	'''\n";
	String qsuf = "''' .\n";

	QueryExecute query0(String qr) {
		return PopiangUtil.query0(qr);
	}

	QueryExecute queryId(String qrf, String id0, String id1) {
		QueryExecute qr = new QueryExecute();
		Map<String,String> m = qr.init(readRdf(qrf,id0,id1));
		qr.query(m);
		return qr;
	}
*/

	double[] txt2dbl(String s) {
		String[] wds = s.split(",");
		double[] r = new double[wds.length];
		for(int i=0; i<r.length; i++) 
			try { r[i] = Double.parseDouble(wds[i].trim()); } catch(Exception z) {r[i]=-1;}
		return r;
	}

	int[] txt2int(String s) {
		String[] wds = s.split(",");
		int[] r = new int[wds.length];
		for(int i=0; i<r.length; i++) 
			try { r[i] = Integer.parseInt(wds[i].trim()); } catch(Exception z) {r[i]=-1;}
		return r;
	}

	SimpleDateFormat datefm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat datefn = new SimpleDateFormat("yyyyMMdd_HHmmss");

	public void addTextNewId(TextFileInfo tinf, String rdftxt) {
		String clp = "";
		Transferable cnt = Toolkit.getDefaultToolkit()
			.getSystemClipboard().getContents(null);
		if (cnt != null && cnt.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				clp = (String)cnt.getTransferData(DataFlavor.stringFlavor);
			} catch(Exception z) {}
		}
		java.util.Date dt = Calendar.getInstance().getTime();
		String ddtt = datefm.format(dt);
		String rid = genId();
		if(rid==null) return;
		int c = tinf.text.getCaretPosition();
		rdftxt = rdftxt.replace("___CLIP___",clp);
		String ins = rid + rdftxt.replace("___DATETIME___",ddtt);
		tinf.text.insert(ins, tinf.text.getCaretPosition());
		tinf.text.setCaretPosition(c+ins.length());
		tinf.text.requestFocus();
	}

	void saveFile() {
		int id = tabbedPane.getSelectedIndex();
		if(id<0) return;
		String ttl = tabbedPane.getTitleAt(id);
		TextFileInfo tinf = hTabPane.get(ttl);
		if(tinf.text.isDirty()) {
			String tx = tinf.text.getText();
			try {
				Files.write(tinf.path, tx.getBytes("UTF-8"));
			} catch(IOException x) { }
			String rt = PopiangUtil.readRdfModel(tinf.path.toFile());
			if(rt!=null) {
				try {
					int ln = Integer.parseInt(rt.substring(0,rt.indexOf(",")));
					tinf.text.setCaretPosition(
						tinf.text.getLineStartOffset(ln-1));
				} catch (Exception ble) { // Never happens
				}
				label.setText(rt);
				label.setForeground(Color.RED);
			}
			else {
				label.setForeground(Color.BLACK);
				label.setText("OK");
			}
			tinf.text.setDirty(false);
			log.info("SAVE FILEx: "+ tinf.path);
		}
	}

	void foldAll() {
		int id = tabbedPane.getSelectedIndex();
		if(id<0) return;
		String ttl = tabbedPane.getTitleAt(id);
		TextFileInfo tinf = hTabPane.get(ttl);
		FoldManager fm = tinf.text.getFoldManager();
		int fc = fm.getFoldCount();
		int cc = 0;

		for(int i=0; i<fc; i++) {
			Fold fold = fm.getFold(i);
			if(fold.isCollapsed()) cc++;
		}
		boolean bCO = (cc<fc)? true : false;
		for(int i=0; i<fc; i++) {
			Fold fold = fm.getFold(i);
			fold.setCollapsed(bCO);
		}
	}

	void addEmpRdf() {
		int id = tabbedPane.getSelectedIndex();
		if(id<0) return;
		String ttl = tabbedPane.getTitleAt(id);
		TextFileInfo tinf = hTabPane.get(ttl);
		String tx = PopiangUtil.makeStaffList(); 
		if(tx!=null && tx.length()>0) {
			tinf.text.insert(tx, tinf.text.getCaretPosition());
		}
	}

	public String genId() {
		String newid = null;
		int id = tabbedPane.getSelectedIndex();
		if(id<0) return null;
		String ttl = tabbedPane.getTitleAt(id);
		if(ttl==null) return null;
		TextFileInfo tinf = hTabPane.get(ttl);
		if(tinf==null) return null;
		String fn = tinf.path.toFile().getName();
		String com = fn.substring(0,fn.indexOf("."));
		List<TokenInfo> tks =  tinf.text.getTokens();
		int maxid = 0;
		String pref = com;
		for(TokenInfo tki : tks) {
			if(tki.stmPos!=1) continue;
			try {
				if(pref==null) pref = tki.label.substring(0,tki.label.indexOf(":"));
				int ii = Integer.parseInt(tki.label.substring(pref.length()+1));
				if(ii>maxid) maxid = ii;
			} catch(Exception z) {}
//			log.info("GENID   "+tki.label+" pos:"+tki.stmPos);
		}
		newid = pref+":"+(maxid+1);
		return newid;
	}
	
	void newId() {
		TextFileInfo tinf = getTextFileInfo();
		if(tinf==null) return;
		String fn = tinf.path.toFile().getName();
		String com = fn.substring(0,fn.indexOf("."));

		String newid = null;
		List<TokenInfo> tks =  tinf.text.getTokens();
//		log.info("NEW ID: "+ tks.size());
		int maxid = 0;
		String pref = com;
		for(TokenInfo tki : tks) {
			if(tki.stmPos!=1) continue;
			try {
				if(pref==null) pref = tki.label.substring(0,tki.label.indexOf(":"));
				int ii = Integer.parseInt(tki.label.substring(pref.length()+1));
				if(ii>maxid) maxid = ii;
			} catch(Exception z) {}
//			log.info("   "+tki.label+" pos:"+tki.stmPos);
		}
		newid = pref+":"+(maxid+1);
//		log.info("NEWID: "+ newid);
		tinf.text.insert(newid, tinf.text.getCaretPosition());
		StringSelection stringSelection = new StringSelection(newid);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}
	String PREFIXS = ""
			+ "PREFIX vp:   <http://ipthailand.go.th/rdf/voc-pred#> "
			+ "PREFIX vo:   <http://ipthailand.go.th/rdf/voc-obj#> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
	;

	int fontSize(int lv) { return 20 - lv * 2; }
	int normSize = 16;

	Model allModel = null;



	@Override
	public void searchEvent(SearchEvent e) {

		SearchEvent.Type type = e.getType();
		SearchContext context = e.getSearchContext();
		SearchResult result = null;

		switch (type) {
			default: // Prevent FindBugs warning later
			case MARK_ALL:
				try {
					int idx = tabbedPane.getSelectedIndex();
					String tt = tabbedPane.getTitleAt(idx);
					TextFileInfo tfin = hTabPane.get(tt);
					result = SearchEngine.markAll(tfin.text, context);
				} catch(Exception x) {}
				break;
			case FIND:
				try {
					int idx = tabbedPane.getSelectedIndex();
					String tt = tabbedPane.getTitleAt(idx);
					TextFileInfo tfin = hTabPane.get(tt);
					result = SearchEngine.find(tfin.text, context);
					if (!result.wasFound() || result.isWrapped()) {
						UIManager.getLookAndFeel().provideErrorFeedback(tfin.text);
					}
				} catch(Exception z) {}
				break;
			case REPLACE:
				try {
					int idx = tabbedPane.getSelectedIndex();
					String tt = tabbedPane.getTitleAt(idx);
					TextFileInfo tfin = hTabPane.get(tt);
					result = SearchEngine.replace(tfin.text, context);
					if (!result.wasFound() || result.isWrapped()) {
						UIManager.getLookAndFeel().provideErrorFeedback(tfin.text);
					}
				} catch(Exception z) {}
				break;
			case REPLACE_ALL:
				try {
					int idx = tabbedPane.getSelectedIndex();
					String tt = tabbedPane.getTitleAt(idx);
					TextFileInfo tfin = hTabPane.get(tt);
					result = SearchEngine.replaceAll(tfin.text, context);
					JOptionPane.showMessageDialog(null, result.getCount() +
						" occurrences replaced.");
				} catch(Exception z) {}
				break;
		}

		String text;
		if (result!=null && result.wasFound()) {
			text = "Text found; occurrences marked: " + result.getMarkedCount();
		}
		else if (type==SearchEvent.Type.MARK_ALL) {
			if (result!=null && result.getMarkedCount()>0) {
				text = "Occurrences marked: " + result.getMarkedCount();
			}
			else {
				text = "";
			}
		}
		else {
			text = "Text not found";
		}
		statusBar.setLabel(text);

	}

/*
	private void addItem(Action a, ButtonGroup bg, JMenu menu) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(a);
		bg.add(item);
		menu.add(item);
	}
*/

	class FileTreeCellRenderer extends DefaultTreeCellRenderer {

		private FileSystemView fileSystemView;

		private JLabel label;

		FileTreeCellRenderer() {
			label = new JLabel();
			label.setOpaque(true);
			label.setFont(new Font(PopiangDigital.sFontName
				, 0, PopiangDigital.iFontSize));

			fileSystemView = FileSystemView.getFileSystemView();
//System.out.println("new Cell Render");
		}

		@Override
		public Component getTreeCellRendererComponent( JTree tree, Object value
			, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

			int i1;
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			File file = (File)node.getUserObject();
			if(file!=null) {
//				label.setIcon(fileSystemView.getSystemIcon(file));
				String nm = fileSystemView.getSystemDisplayName(file);
				if(nm!=null && (i1=nm.indexOf("."))>0) nm = nm.substring(0,i1);
				RdfModelInfo rmi = PopiangDigital.hRdfModelInfo.get(nm);
				label.setText(nm);
				if(rmi!=null) {
					rmi.label = label;
					rmi.tcr = this;
					rmi.node = node;
					if(rmi.title!=null) {
						nm += ": "+rmi.title;
						label.setText(nm);
						label.setToolTipText(rmi.title);
					}
				} else {
					label.setToolTipText(file.getPath());
				}
			}

//System.out.println("... compo 2: "+file);
			if (selected) {
				label.setBackground(backgroundSelectionColor);
				label.setForeground(textSelectionColor);
			} else {
				label.setBackground(backgroundNonSelectionColor);
				label.setForeground(textNonSelectionColor);
			}
			return label;
		}
	}

/*
	void fixMisSpell() {
		int id = tabbedPane.getSelectedIndex();
		if(id<0) return;
		String ttl = tabbedPane.getTitleAt(id);
		TextFileInfo tinf = hTabPane.get(ttl);
		String tx = tinf.text.getText();
		log.info("LEN: "+ tx.length());
		Map<String,String> tokens = new HashMap<String,String>();
		for(String[] wds : aMisSpell) tokens.put(wds[0],wds[1]);
		String patts = "(" + StringUtils.join(tokens.keySet(), "|") + ")";
		Pattern patt = Pattern.compile(patts);
		StringBuffer sb = new StringBuffer();
		Matcher match = patt.matcher(tx);
		int cnt = 0;
		while(match.find()) {
			cnt++;
			String old = match.group(1);
			String thg = tokens.get(old);
			log.info(cnt+" replace '"+ old + "' with '"+thg+"'");
			match.appendReplacement(sb, thg);
		}
		match.appendTail(sb);
		tinf.text.setText(sb.toString());
	}
	String[][] aMisSpell = {
		{"ด าเนิน","ดำเนิน"},
		{"ดาเนิน","ดำเนิน"},
		{"คาแนะนา","คำแนะนำ"},
		{"คา้น","ค้าน"},
		{"คาขอ","คำขอ"},
		{"จาเป็น","จำเป็น"},
		{"ท่ี","ที่"},
		{"เพ่ือ","เพื่อ"},
		{"เม่ือ","เมื่อ"},
		{"แนะนา","แนะนำ"},
		{"กากับ","กำกับ"},
		{"สานัก","สำนัก"},
		{"ส านัก","สำนัก"},
		{"กาหนด","กำหนด"},
		{"ทาการ","ทำการ"},
		{"ทาให้","ทำให้"},
		{"กระทา","กระทำ"},
		{"ทานอง","ทำนอง"},
		{"ทางาน","ทำงาน"},
		{"ทาสัญญา","ทำสัญญา"},
		{"ทาตาม","ทำตาม"},
		{"ทาได้","ทำได้"},
		{"สามารถทา","สามารถทำ"},
		{"ทาลาย","ทำลาย"},
		{"จัดทา","จัดทำ"},
		{"จัดท า","จัดทำ"},
		{"ทาการ","ทำการ"},
		{"ทาความ","ทำความ"},
		{"ทานิติกรรม","ทำนิติกรรม"},
		{"ทาหน้าที่","ทำหน้าที่"},
		{"ทาไว้","ทำไว้"},
		{"เคร่ือง","เครื่อง"},
		{"อ่ืน","อื่น"},
		{"สาเนา","สำเนา"},
		{"คาปรึกษา","คำปรึกษา"},
		{"สารวจ","สำรวจ"},
		{"ข้ึน","ขึ้น"},
		{"ส่ี","สี่"},
		{"เก่ียว","เกี่ยว"},
		{"ท้ัง","ทั้ง"},
		{"น้ี","นี้"},
		{"นามา","นำมา"},
		{"รอ้ง","ร้อง"},
		{"เปดิ","เปิด"},
		{"ตาแหน่ง","ตำแหน่ง"},
		{"จานวน","จำนวน"},
		{"หนา้ที่","หน้าที่"},
		{"อานาจ","อำนาจ"},
		{"หรอื","หรือ"},
		{"ขอ้","ข้อ"},
		{"เกบ็","เก็บ"},
		{"เอยีด","เอียด"},
		{"สาคัญ","สำคัญ"},
		{"คาอธิบาย","คำอธิบาย"},
		{"คานึง","คำนึง"},
		{"สาหรับ","สำหรับ"},
		{"ดาเนนิ","ดำเนิน"},
		{"ตอ้ง","ต้อง"},
		{"ถงึ","ถึง"},
		{"ถงึ","ถึง"},
		{"คาสั่ง","คำสั่ง"},
	};
*/

    void makeAttendReport() {
        int id = tabbedPane.getSelectedIndex();
        if(id<0) return;
        String ttl = tabbedPane.getTitleAt(id);
        TextFileInfo tinf = hTabPane.get(ttl);
		System.out.println("============ PATH: "+tinf.path);
		System.out.println("============ FILE: "+tinf.path.toFile());
		PopiangUtil.attendReport(tinf.path.toFile());
	}

    void makeScanFile() {
        int id = tabbedPane.getSelectedIndex();
        if(id<0) return;
        String ttl = tabbedPane.getTitleAt(id);
        TextFileInfo tinf = hTabPane.get(ttl);
		System.out.println("============ PATH: "+tinf.path);
		System.out.println("============ FILE: "+tinf.path.toFile());
		PopiangUtil.makeScanFile(tinf.path.toFile());
	}

}


