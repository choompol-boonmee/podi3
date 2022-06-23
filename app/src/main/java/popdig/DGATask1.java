package popdig;

import java.awt.Desktop;
import javax.swing.*;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;
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
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.TextField;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfFormField;

public class DGATask1 extends PopupAction {
	static SimpleDateFormat datefm = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");

	String vo = "vo:DGATask";
	public DGATask1(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
	}
	public void action() {
		String rt = act1();
		if(rt!=null) {
			System.out.println("msg: "+ rt);
		}
	}
	public String act1() {
//		File fFont = new File(PopiangDigital.workDir + "/res/font/SOV_KhianKhao.ttf");
		File fFont = new File(PopiangDigital.workDir + "/res/font/13ThaiFonts/THSarabun.ttf");
		System.out.println("ACTION");
		String vid = PopiangDigital.getObject(vo);
		if(vid==null || vid.length()==0) return "NO VID";
		System.out.println(" vid:"+vid);
		QueryExecute q0;

		String sta = PopiangUtil.query0(vid+" vp:status ?a .").get();
		if(!"on".equals(sta)) return "Status is not ON";

		String oid = PopiangUtil.query0(vid+" vp:targ ?a .").get();
		if(oid==null || oid.length()==0) return "NO SURVEY LIST";

		String prf = PopiangUtil.query0(vid+" vp:pref ?a .").get();
		if(prf==null || prf.length()==0) return "NO PREFIX";

		q0 = PopiangUtil.query0(vid+" vp:form ?a . "+
			" ?a vp:item(*) ?b . "+
			" ?b vp:tx ?x . "+
			" ?b vp:tp ?p . "+
			" ?b vp:at ?o . "+
			" ?b vp:fn ?f . "
		);
		String[] qz = q0.gets("?x");
		String[] tp = q0.gets("?p");
		String[] at = q0.gets("?o");
		String[] fn = q0.gets("?f");
		if(qz==null) return "NO QUESTION";
		System.out.println("QZ:"+ qz.length);
		for(int j=0; j<qz.length; j++) {
			System.out.println((j+1)+": "+ qz[j]);
		}

		q0 = PopiangUtil.query0(vid+" vp:targ ?a . "+
			" ?a vp:org(*) ?b . "+
			" ?b vp:min ?m . "+
			" ?b vp:dep ?d . "+
			" ?b vp:srv ?s . "
		);
		String[] min = q0.gets("?m");
		String[] dep = q0.gets("?d");
		String[] srv = q0.gets("?s");
		if(qz==null) return "NO TARGET ORG";
		System.out.println("TARGET:"+ qz.length);

		String d2 = vid.substring(0,vid.indexOf(":"));
		String d1 = d2.substring(0,d2.length()-2);
		String d3 = vid.replace(":","-");

		String dir = PopiangDigital.workDir + "/res/"+ d1+"/"+d2+"/"+d3+"/"+prf;
System.out.println("dir:"+dir);
		File fdir = new File(dir);
		if(!fdir.exists()) fdir.mkdirs();

		for(int i=0; i<min.length; i++) {
			String fid = PopiangUtil.formID(i);

			File fBck = new File("back.pdf");
			File fQR = new File( "qrcodetemp.png");

			File dDir = new File(fdir+"/"+prf+"-"+fid);
			if(!dDir.exists()) dDir.mkdirs();

			File fDoc = new File(dDir+"/"+prf+"-"+fid+".pdf");
			String ts = datefm.format(Calendar.getInstance().getTime());
			File fDox = new File(dDir+"/"+prf+"-"+fid+"_G"+ts+".pdf");

			String uid = "http://dga.tueng.org/D/"+prf+"-"+fid;

			System.out.println(fid+": "+ fDoc);

			try {

				Document document = new Document();
				PdfWriter.getInstance(document, new FileOutputStream(fBck));
				document.open();
				document.add(new Paragraph(" "));
				document.close();

				FileOutputStream outputStream = new FileOutputStream(fDoc);
				PdfReader pdfReader = new PdfReader(new FileInputStream(fBck));
				PdfStamper stamper = new PdfStamper(pdfReader, outputStream);

				BaseFont bf = BaseFont.createFont(fFont.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				PdfContentByte cb = stamper.getOverContent(1);


				for(int j=0; j<qz.length; j++) {
//System.out.println("FM "+j+": "+ qz[j]+" tp:"+tp[j]+" at:"+at[j]+" fn:"+fn[j]);
					int i1,i2;
					if((i1=at[j].indexOf(","))<=0) {
System.out.println("    at error: "+ at[j]);
						continue;
					}
					int x=-1, y=-1, f=-1;
					try {
						x = Integer.parseInt(at[j].substring(0,i1));
						y = Integer.parseInt(at[j].substring(i1+1));
						f = Integer.parseInt(fn[j]);
					} catch(Throwable _x) { 
						_x.printStackTrace();
						continue;
					}
//System.out.println("  tp:"+tp[j]+" x:"+x+" y:"+y+" f:"+f);
					if(x<=0 || y<=0 || f<=0) continue;

					if("0".equals(tp[j])) {
						cb.setFontAndSize(bf, f);
						cb.beginText();
						cb.setTextMatrix(x, y);
						cb.showText(qz[j]);
						cb.endText();
//System.out.println("    title "+j+": "+ qz[j]+" tp:"+tp[j]+" at:"+at[j]+" fn:"+fn[j]);
					} else if("1".equals(tp[j])) {
						cb.setFontAndSize(bf, f);
						cb.beginText();
						cb.setTextMatrix(x, y);
						cb.showText(qz[j]);
						cb.endText();
//System.out.println("    quiz  "+j+": "+ qz[j]+" tp:"+tp[j]+" at:"+at[j]+" fn:"+fn[j]);
					} else if("q".equals(tp[j])) {

//System.out.println("    QR x:"+x+" y:"+y+" f:"+f);
						MultiFormatWriter writer = new MultiFormatWriter();
						Hashtable hints = new Hashtable();
						hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
						MatrixToImageWriter.writeToFile(
							writer.encode(uid, BarcodeFormat.QR_CODE, 400, 400, hints ), "png",  fQR);

						BufferedImage img = ImageIO.read(fQR);
						Image image = Image.getInstance(img, null);
						cb.addImage(image, f, 0, 0, f, x, y);

					} else if("u".equals(tp[j])) {
						cb.setFontAndSize(bf, f);
						cb.beginText();
						cb.setTextMatrix(x, y);
						cb.showText(uid);
						cb.endText();
					} else if("m".equals(tp[j])) {
						cb.setFontAndSize(bf, f);
						cb.beginText();
						cb.setTextMatrix(x, y);
						cb.showText(min[i]);
						cb.endText();
					} else if("d".equals(tp[j])) {
						cb.setFontAndSize(bf, f);
						cb.beginText();
						cb.setTextMatrix(x, y);
						cb.showText(dep[i]);
						cb.endText();
					} else if("s".equals(tp[j])) {
						cb.setFontAndSize(bf, f);
						cb.beginText();
						cb.setTextMatrix(x, y);
						cb.showText(srv[i]);
						cb.endText();
					} else {
					}
				}

				stamper.close();
				outputStream.flush();
				outputStream.close();

				byte[] buf = new byte[1024*16];
				int l;
				FileInputStream fis = new FileInputStream(fDoc);
				FileOutputStream fos = new FileOutputStream(fDox);
				while((l=fis.read(buf,0,buf.length))>0) fos.write(buf,0,l);
				fos.close();
				fis.close();

			} catch(Exception z) {
				z.printStackTrace();
			}
		}

		int lno = tfinf.text.getLineCount();
		return null;
	}

/*
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

			BaseFont bf = BaseFont.createFont(fFont.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			PdfContentByte cb;

			cb = stamper.getOverContent(1);

			PushbuttonField button = new PushbuttonField(
				stamper.getWriter(), new Rectangle(36, 700, 72, 730), "post");
			button.setText("POST");
			button.setBackgroundColor(new GrayColor(0.7f));
			button.setVisibility(PushbuttonField.VISIBLE_BUT_DOES_NOT_PRINT);

			
			cb.setLineWidth(1.2f);
			cb.moveTo(0, 0);
			cb.lineTo(100, 600);
			cb.stroke();
			
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
*/

}

