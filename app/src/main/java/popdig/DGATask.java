package popdig;

/*
set PATH="C:\Program Files (x86)\Java\jdk1.7.0_45\bin";%PATH%
set PATH=c:\apache-ant-1.8.4\bin;%PATH%
set CLASSPATH=lib\GenPlanDocx.jar;lib\commons-io-2.4.jar;lib\commons-logging-1.1.3.jar;lib\xmlgraphics-commons-1.4.jar;lib\docx4j-nightly-20131031.jar;lib\slf4j-api-1.7.5.jar;lib\slf4j-simple-1.7.5.jar;lib\commons-lang-2.6.jar;lib\jakarta-poi-1.10.0-dev-20030222.jar;lib\jfreechart-1.0.0-rc1.jar;lib\log4j-1.2.17.jar;geo1\gt-shapefile-10.3.jar

set CLASSPATH=lib\GenPlanDocx.jar;lib\commons-io-2.4.jar;lib\commons-logging-1.1.3.jar;lib\xmlgraphics-commons-1.4.jar;lib\docx4j-nightly-20131031.jar;lib\slf4j-api-1.7.5.jar;lib\slf4j-simple-1.7.5.jar;lib\commons-lang-2.6.jar;lib\jakarta-poi-1.10.0-dev-20030222.jar;lib\jfreechart-1.0.0-rc1.jar;lib\log4j-1.2.17.jar;geo2\gt-shapefile-10.3.jar;geo2\
set CLASSPATH=lib\GenPlanDocx.jar;lib\commons-io-2.4.jar;lib\commons-logging-1.1.3.jar;lib\xmlgraphics-commons-1.4.jar;lib\docx4j-nightly-20131031.jar;lib\slf4j-api-1.7.5.jar;lib\slf4j-simple-1.7.5.jar;lib\commons-lang-2.6.jar;lib\jakarta-poi-1.10.0-dev-20030222.jar;lib\jfreechart-1.0.0-rc1.jar;lib\log4j-1.2.17.jar;lib\gt-shapefile-10.3.jar
cd \SmartMOL01\sth01
GenPlanDocs.bat

cd ..\sth01
javac -encoding UTF-8 -d . GenPlanDocx.java
jar cvf lib\GenPlanDocx.jar -r org
java -Xss32m org.edipa.plan.GenPlanDocx

del ..\SmartMOL\src\org\edipa\gen\*.java
copy java\*.java ..\SmartMOL\src\org\edipa\gen\.

del ..\SmartMOL\dist\SmartMOL\clslist\*.xml
copy clslist\*.xml ..\SmartMOL\dist\SmartMOL\clslist\.

del ..\SmartMOL\dist\src\clslist\*.xml
copy clslist\*.xml ..\SmartMOL\dist\src\clslist\.

cd ../SmartMOL
ant wrap
dist\SmartMOL\bin\edipa-engine.bat

cd ../LibWebService
ant
cd ../SmartMOL
ant wrap
dist\SmartMOL\bin\edipa-engine.bat

export CLASSPATH=.:lib/commons-io-2.4.jar:lib/commons-logging-1.1.3.jar:lib/xmlgraphics-commons-1.4.jar:lib/docx4j-nightly-20131031.jar:lib/slf4j-api-1.7.5.jar:lib/slf4j-simple-1.7.5.jar:lib/commons-lang-2.6.jar:lib/jakarta-poi-1.10.0-dev-20030222.jar:lib/jfreechart-1.0.0-rc1.jar

export CLASSPATH=.:lib/commons-io-2.4.jar:lib/commons-logging-1.1.3.jar:lib/xmlgraphics-commons-1.4.jar:lib/docx4j-nightly-20131031.jar:lib/slf4j-api-1.7.5.jar:lib/slf4j-simple-1.7.5.jar:lib/commons-lang-2.6.jar:lib/jakarta-poi-1.10.0-dev-20030222.jar:lib/jfreechart-1.0.0-rc1.jar:lib/log4j-1.2.17.jar

java -Dlog4j.configuration=log4j.xml org.edipa.plan.GenPlanDocx
java -Dlog4j.configuration=log4j.properties org.edipa.plan.GenPlanDocx
cp java/* ../SmartMol01/SmartMOL/src/org/edipa/gen/.
*/

/*
import org.docx4j.*;
import org.docx4j.jaxb.*;
import org.docx4j.openpackaging.*;
import org.docx4j.openpackaging.io.*;
import org.docx4j.openpackaging.packages.*;
import org.docx4j.wml.*;
import java.io.*;
import java.util.*;
import java.net.*;
import javax.xml.bind.*;
import org.apache.commons.lang.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
import java.awt.geom.*;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.eventfilesystem.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.eventfilesystem.*;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import java.math.*;
import org.docx4j.wml.PPrBase.NumPr;
import java.util.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;
import com.google.zxing.*;
import com.google.zxing.common.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.eventfilesystem.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfStamper;
import java.security.*;
import java.security.cert.*;
import java.security.spec.*;
import java.util.concurrent.*;
import java.text.*;
*/
import java.awt.Desktop;
import javax.swing.*;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.Image;
import com.google.zxing.*;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.decoder.*;
import com.google.zxing.client.j2se.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import com.itextpdf.text.pdf.PushbuttonField;
import com.itextpdf.text.Rectangle;
//import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.TextField;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfFormField;

public class DGATask extends PopupAction {
	String vo = "vo:DGATask";
	public DGATask(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
	}
	public void action() {
		File fFont = new File(PopiangDigital.workDir + "/res/font/SOV_KhianKhao.ttf");
		System.out.println("ACTION");
		String vid = PopiangDigital.getObject(vo);
		System.out.println(" vid:"+vid);
		try {
			String dest = "test1.pdf";
			String out = "test2.pdf";

			File fDrf = new File(dest);
			File fOut = new File(out);
			File fQR = new File( "qrcodetemp.png");
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(fDrf));
			document.open();
			document.add(new Paragraph(" "));
			document.close();

			FileOutputStream outputStream = new FileOutputStream(fOut);
			PdfReader pdfReader = new PdfReader(new FileInputStream(fDrf));
			PdfStamper stamper = new PdfStamper(pdfReader, outputStream);

			Rectangle newRect = new Rectangle(231.67f, 108.0f, 395.67f, 197.0f);
			String newFldName = "ListBox1";
			int pg = 1;

			TextField txtField = new TextField(stamper.getWriter(), newRect, newFldName);
			txtField.setTextColor(BaseColor.BLACK);
			txtField.setBackgroundColor(BaseColor.WHITE);
			txtField.setBorderColor(BaseColor.BLACK);
			txtField.setFieldName(newFldName);
			txtField.setAlignment(0); //LEFT
			txtField.setBorderStyle(0); //SOLID
			txtField.setBorderWidth(1.0F); //THIN
			txtField.setVisibility(TextField.VISIBLE);
			txtField.setRotation(0); //None
			txtField.setBox(newRect);

			//PdfArray opt = new PdfArray();
			List<String> ListBox_ItemDisplay = new ArrayList<String>();
			ListBox_ItemDisplay.add("One");
			ListBox_ItemDisplay.add("Two");
			ListBox_ItemDisplay.add("Three");
			ListBox_ItemDisplay.add("Four");
			ListBox_ItemDisplay.add("Five");
			List<String> ListBox_ItemValue = new ArrayList<String>();
			ListBox_ItemValue.add("1X");
			ListBox_ItemValue.add("2X");
			ListBox_ItemValue.add("3X");
			ListBox_ItemValue.add("4X");
			ListBox_ItemValue.add("5X");
			txtField.setOptions(txtField.getOptions() | TextField.MULTISELECT);
			ArrayList<Integer> selIndex = new ArrayList<Integer>();
			//List<String> selValues = new ArrayList<String>();
			selIndex.add(1); // SELECT #1 (index)
			selIndex.add(3); // SELECT #3 (index)
			txtField.setChoices(ListBox_ItemDisplay.toArray(new String[0]));
			txtField.setChoiceExports(ListBox_ItemValue.toArray(new String[0]));
			txtField.setChoiceSelections(selIndex);
			// vvv--- Add this line as a fix
			txtField.setVisibleTopChoice(0);
			// ^^^--- Add this line as a fix
			PdfFormField listField = txtField.getListField();
			stamper.addAnnotation(listField, pg);
/*
*/

			BaseFont bf = BaseFont.createFont(fFont.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			PdfContentByte cb;

			cb = stamper.getOverContent(1);

			PushbuttonField button = new PushbuttonField(
				stamper.getWriter(), new Rectangle(36, 700, 72, 730), "post");
			button.setText("POST");
			button.setBackgroundColor(new GrayColor(0.7f));
			button.setVisibility(PushbuttonField.VISIBLE_BUT_DOES_NOT_PRINT);

			
/*
			cb.setLineWidth(1.2f);
			cb.moveTo(0, 0);
			cb.lineTo(100, 600);
			cb.stroke();
*/
			
			String pgid = "1234567890";

			MultiFormatWriter writer = new MultiFormatWriter();
			Hashtable hints = new Hashtable();
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
			MatrixToImageWriter.writeToFile(
				writer.encode(pgid, BarcodeFormat.QR_CODE, 400, 400, hints ), "png",  fQR);

			BufferedImage img = ImageIO.read(fQR);
			Image image = Image.getInstance(img, null);
			cb.addImage(image, 100, 0, 0, 100, 0, 740);

			cb.setFontAndSize(bf, 20);
			cb.beginText();
			cb.setTextMatrix(100, 600);
			cb.showText("PPPP");
			cb.endText();

			cb.setFontAndSize(bf, 20);
			cb.beginText();
			cb.setTextMatrix(200, 600);
			cb.showText("ภาษาไทย");
			cb.endText();


			stamper.close();
			outputStream.flush();
			outputStream.close();

			Desktop.getDesktop().open(fOut);

			File fQout = new File("qrout.jpeg");

			PDDocument pddoc = PDDocument.load(fOut);
			PDFRenderer renderer = new PDFRenderer(pddoc);
			BufferedImage img2 = renderer.renderImage(0);
			BufferedImage img3 = img2.getSubimage(8, 8, 90, 90);
			ImageIO.write(img3, "JPEG", fQout);
			pddoc.close();

			BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
//                    ImageIO.read( new FileInputStream(fQR)))));
                    ImageIO.read( new FileInputStream(fQout)))));
			Result result = new MultiFormatReader().decode(binaryBitmap);
			String qr = result.getText();
System.out.println("read: "+ qr );

			Desktop.getDesktop().open(fQout);

		} catch(Exception z) {
z.printStackTrace();
		}
	}

}

//==========================================
/*
	final static SimpleDateFormat fmSt2 = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);

	static org.docx4j.wml.ObjectFactory factory = new org.docx4j.wml.ObjectFactory(); 

	public static void main(String[] args) throws Exception {
		new ITDabosNotebook().process();
	}
	public void process() {
		System.out.println("Process");
		try {
			File outdir = new File("docout");
			if(!outdir.exists()) outdir.mkdirs();
			File fForm = new File("form/test-form5.pdf");
			File odoc = new File(outdir+"/drnum-notepad.pdf");
			File fQR = new File( outdir+"/qrcodetemp.png");

			
			int wd = 196;
			int hg = 304;

			String dt = fmSt2.format(new Date());
			String nm = "Dr.CHOOMPOL BOONMEE";
			String em = "choompol.boonmee@gmail.com";
			String id = "3100601641284_" + dt;
			int p = 0;
			int mxp = 5;
			int mxc = 4;

			FileOutputStream outputStream = new FileOutputStream(odoc);
			PdfReader pdfReader = new PdfReader(new FileInputStream(fForm));
			PdfStamper stamper = new PdfStamper(pdfReader, outputStream);
			BaseFont bf = BaseFont.createFont("conf/ms-mincho.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			PdfContentByte cb;
			
			for(int g=1; g<=mxp; g++) {
				cb = stamper.getOverContent(g);
				
				for(int i=0; i<=mxc; i++) {
					for(int j=1; j>=0; j--) {
						cb.setLineWidth(0.2f);
						cb.moveTo(i*wd+1, j*hg);
						cb.lineTo(i*wd+1, (j+1) *hg);
						cb.stroke();
					}
				}
				
				for(int i=0; i<mxc; i++) {
					for(int j=2; j>=0; j--) {
						cb.setLineWidth(0.2f);
						cb.moveTo(i*wd+1, 1+j*hg);
						cb.lineTo(i*wd+1 + wd, 1+j*hg);
						cb.stroke();
					}
				}

				for(int j=2; j>=1; j--) {
					for(int i=0; i<mxc; i++) {
						// start page
						p++;
						int pgno = g;
						pgno += (2-j) * (mxp * mxc);
						pgno += i * mxp;

						String pgid = id + "_"+pgno;
						MultiFormatWriter writer = new MultiFormatWriter();
						Hashtable hints = new Hashtable();
						hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
						MatrixToImageWriter.writeToFile(
							writer.encode(pgid, BarcodeFormat.QR_CODE, 400, 400, hints ), "png",  fQR);
						BufferedImage img = ImageIO.read(fQR);
						com.lowagie.text.Image image = com.lowagie.text.Image.getInstance(img, null);
						cb.addImage(image, 45, 0, 0, 50, i*wd + 5-3, 2+j*hg - 65 -4);

						cb.beginText();
						cb.setFontAndSize(bf, 9);
						cb.setTextMatrix(i*wd + 45, 2+j*hg - 30-4);
						cb.showText(pgid);
						cb.endText();

						cb.beginText();
						cb.setFontAndSize(bf, 11);
						cb.setTextMatrix(i*wd + 45, 2+j*hg - 40-4);
						cb.showText(em);
						cb.endText();

						cb.beginText();
						cb.setFontAndSize(bf, 11);
						cb.setTextMatrix(i*wd + 70, 2+j*hg - 55-4);
						cb.showText("長岡悠久ライオンズ");
						cb.endText();

						int x,y;

						int ww = 8;
						x = i*wd + 4;
						y = (j-1)*hg +4;
						
						cb.setLineWidth(0.05f);
						cb.moveTo(x, y);
						cb.lineTo(x+8, y);
						cb.lineTo(x, y+8);
						cb.lineTo(x+8, y+8);
						cb.lineTo(x, y);
						cb.fill();
						
						x = i*wd + 22 - 5;
						y = (j-1)*hg + 5;
						cb.setFontAndSize(bf, 8);
						for(int c=0; c<15; c++) {
							cb.ellipse(x+c*10, y, x+c*10+8, y+8);
							cb.stroke();
							cb.beginText();
							cb.setTextMatrix(x+c*10+2, y+2);
							char n = (char) ((c<9)? '0'+(c+1) : 'A' + (c-9));
							cb.showText(""+ n);
							cb.endText();
						}

						cb.setFontAndSize(bf, 10);
						cb.beginText();
						cb.setTextMatrix(x+15*10+2, y+2-2);
						cb.showText("P"+ pgno);
						cb.endText();

						x = i*wd + wd - ww - 2;
						y = (j-1)*hg + 4;
						cb.setLineWidth(0.05f);
						cb.moveTo(x, y);		// 1
						cb.lineTo(x+4, y+8);	// 2
						cb.lineTo(x+8, y+0);	// 3
						cb.lineTo(x+0, y+4);	// 4
						cb.lineTo(x+8, y+8);	// 5
						cb.lineTo(x+4, y+0);	// 6
						cb.lineTo(x+0, y+8);	// 7
						cb.lineTo(x+8, y+4);	// 8
						cb.lineTo(x+0, y+0);	// 9
						cb.fill();
						
						for(int r=0; r<=15; r++) {
							cb.beginText();
							cb.setFontAndSize(bf, 8);
							x = i*wd + 2;
							y = j*hg - 64 - r * 15;
							if(r<15) {
								cb.setTextMatrix(x+1, y-10);
								char n = (char) ((r<9)? '0'+(r+1) : 'A' + (r-9));
								cb.showText(""+ n);
								cb.endText();
							}

							cb.setLineWidth(0.05f);
							cb.moveTo(x, y);
							cb.lineTo(x+194, y);
							cb.stroke();

							if(r<15) {
								cb.ellipse(5+x+180, y-1, 5+x+180-6, y-1-6);
								cb.ellipse(5+x+180, y-1-7, 5+x+180-6, y-1-6-7);
								cb.ellipse(5+x+180+8-1, y-1, 5+x+180-6+8-1, y-1-6);
								cb.ellipse(5+x+180+8-1, y-1-7, 5+x+180-6+8-1, y-1-6-7);
								cb.stroke();
							}
						}
						

						// end page

					}
				}
			}
			stamper.close();
			outputStream.flush();
			outputStream.close();
			Desktop.getDesktop().open(odoc);
		} catch(Exception x) {
		}
	}
*/

