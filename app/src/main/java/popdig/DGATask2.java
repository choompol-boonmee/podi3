package popdig;

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
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.TextField;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfFormField;

public class DGATask2 extends PopupAction {
	String vo = "vo:DGATask";
	public DGATask2(JPopupMenu pop, TextFileInfo tfi, String lb) {
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

