package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.geom.*;
import java.awt.Shape;
import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.text.*;
import java.awt.Desktop;

public class ViewExecute1Action extends PopupAction {
	String vo = "vo:View";
	public ViewExecute1Action(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
	}

	public double[] dlt;	// left top of the map in meter UTM
	public double[] drb;	// right bottom of the map in UTM
	public int[] dim;		// width and height of the image

	public List<Shape> aPlace;
	public List<int[]> aPlaceOrg;

	public List<Shape> aNode;
	public List<int[]> aNodeOrg;

	public List<String> aPlaceId;
	public List<String> aPlaceName;
	public Map<String,List<double[]>> hPlacePnts;

	public List<String> aCableId;
	public List<String> aCableName;
	public Map<String,List<double[]>> hCablePnts;

	public List<String> aNodeId;
	public List<String> aNodeName;
	public Map<String,List<double[]>> hNodePnts;

	public List<Shape> aCam = new ArrayList<>();
	public List<Shape> aBod = new ArrayList<>();

	public List<int[]> aCamOrg = new ArrayList<>();
	public List<String> aCamId = new ArrayList<>();
	public List<String> aCamName = new ArrayList<>();

	public List<Shape> aPoe = new ArrayList<>();
	public List<Double> aPoeLen = new ArrayList<>();

	public Map<String,QueryExecute> hNode = new HashMap<>();
	public Map<String,QueryExecute> hCam = new HashMap<>();
	public Map<String,QueryExecute> hPrd = new HashMap<>();

	public double ww;		//  width in meter
	public double aa;		// 	multiply factor
	public List<Shape> aCable;
	public List<int[]> aCableOrg;

	public boolean bMV = false;	// transform the map
	public int mvx = -1, mvy = -1;
	public double rot = 0, sca = 1;
	public BufferedImage imgMap;
	public BufferedImage imgRes;

	Color colNode = new Color(0,0,153);
	Color colCam0 = new Color(76,153,0);
	Color colCam1 = new Color(0,153,0);
	Color colCam2 = new Color(0,153,76);
	Color colCam3 = new Color(0,102,102);
	Color colCam4 = new Color(250,51,102);
	Color colPlace = new Color(153,0,153);
	Color colCable = new Color(153,0,0);

	public String showPlcNm;
	public String showNode;
	public String showCamId;
	public String showPoe;

	public String vnm;		// view name
	public String pid;		// picture id
	public String sz;		// picture size

	public String floor;

    public void action() {
		String rid = genId();
		if(rid==null) return;
		String id = PopiangDigital.getObject(vo);
		retrieve(id);
		collectCam();
		collectCables();
		drawImage();
		save(imgRes, rid);
	}

	public String genId() {
		String rid = tfinf.text.genId();
		if(rid==null) return null;
		return rid;
	}

	public void retrieve(String id) {
System.out.println("======== ID: "+ id);

		PopiangUtil.readAllRdfModel();

		String qr;
		QueryExecute q0;

		qr = id+" vp:name ?a ; vp:picmap ?b .\n";
		q0 = PopiangUtil.query0(qr);
		vnm = q0.get("?a");
		pid = q0.get("?b");

		qr = pid+" vp:size ?size ; vp:image ?img .\n";
		q0 = PopiangUtil.query0(qr);
		sz = q0.get("?size");
		String im = q0.get("?img");

		q0 = PopiangUtil.query0(id+" vp:focus ?foc .\n");
		String fo = q0.get("?foc");
				
		q0 = PopiangUtil.query0(id+" vp:showPlaceName ?a .\n");
		showPlcNm = q0.get();

		q0 = PopiangUtil.query0(id+" vp:showNode ?a .\n");
		showNode = q0.get();

		q0 = PopiangUtil.query0(id+" vp:showCamId ?a .\n");
		showCamId = q0.get();

		q0 = PopiangUtil.query0(id+" vp:showPoe ?a .\n");
		showPoe = q0.get();

		q0 = PopiangUtil.query0(id+" vp:floor ?a .");
		floor = q0.get();

		qr = pid+" vp:corner(*)/vp:utm ?a .\n";
		q0 = PopiangUtil.query0(qr);
		String lt = q0.get(0, "?a");
		String rb = q0.get(1, "?a");

		q0 = PopiangUtil.queryId("qry0B.qry", "dnm0E:74", id);
		aPlaceId = q0.qe.aId;
		aPlaceName = q0.qe.aName;
		hPlacePnts = q0.qe.hPlc;

		q0 = PopiangUtil.queryId("qry0D.qry", "dnm0E:74", id);
		aCableId = q0.qe.aId;
		aCableName = q0.qe.aName;
		hCablePnts = q0.qe.hPlc;

		q0 = PopiangUtil.queryId("qry0E.qry", "dnm0E:74", id);
		aNodeId = q0.qe.aId;
		aNodeName = q0.qe.aName;
		hNodePnts = q0.qe.hPlc;

		dlt = PopiangUtil.txt2dbl(lt);
		drb = PopiangUtil.txt2dbl(rb);
		dim = PopiangUtil.txt2int(sz);

		System.out.println("  vnm:"+vnm+" pic:"+pid);
		System.out.println("  sz:"+sz+" im:"+im);
		System.out.println("  left top: "+ dlt[0]+"  "+dlt[1]);
		System.out.println("  right bottom: "+ drb[0]+"  "+drb[1]);
		System.out.println("  size: "+ dim[0]+"  "+dim[1]);

		File f = new File(PopiangDigital.workDir+"/res/"+im.substring(5));
		System.out.println("  im:"+f.getAbsolutePath()+":"+f.exists());

		try {
			imgMap = ImageIO.read(f);

			ww = drb[0] - dlt[0];
			aa = (double) dim[0] / ww;


			aPlace = new ArrayList<>();
			aPlaceOrg = new ArrayList<>();
			for(int i=0; i<aPlaceId.size(); i++) {
				List<double[]> aPnts = hPlacePnts.get(aPlaceId.get(i));
				double[] d1=null,d0=null;
				int x1,y1,x2=0,y2=0,x0=0,y0=0,x3=0,y3=0,xe=0,ye=0;

				GeneralPath edge = new GeneralPath();
				for(int j=0; j<aPnts.size(); j++) {
					d0 = d1;
					d1 = aPnts.get(j);
					x2 = (int) (aa * (d1[0] - dlt[0]));
					y2 = dim[1] - (int) (aa * (d1[1] - drb[1]));
					x3 += x2;
					y3 += y2;
					if(j==0) {
						x0 = x2; y0 = y2;
						edge.moveTo(x0, y0);
						continue;
					} else if(j==1) {
						xe = x2; ye = y2;
					}
					if(j>0) {
						edge.lineTo(x2, y2);
					}
					x1 = (int) (aa * (d0[0] - dlt[0]));
					y1 = dim[1] - (int) (aa * (d0[1] - drb[1]));
				}
				edge.closePath();
				aPlace.add(edge);

				x3 /= aPnts.size();
				y3 /= aPnts.size();
				aPlaceOrg.add(new int[] {x3,y3});

				if(aPlaceId.get(i).equals(fo)) {
					double dy = ye - y0;
					double dx = xe - x0;
					double dz = Math.sqrt(dx*dx + dy*dy);
					double at = Math.atan2(dy, dx);
					bMV = true;
					mvx = x3;
					mvy = y3;
					rot = -at;
					sca = dim[0]/dz * 3/4;
				}
			}

			aNode = new ArrayList<>();
			aNodeOrg = new ArrayList<>();
			for(int i=0; i<aNodeId.size(); i++) {
				double[] d1=null,d0=null;
				d1 = hNodePnts.get(aNodeId.get(i)).get(0);
				double dx = (aa * (d1[0] - dlt[0]));
				double dy = (double)dim[1] - (aa * (d1[1] - drb[1]));
				Shape cc = new Ellipse2D.Double(dx, dy, 5, 5);
				aNode.add(cc);
				aNodeOrg.add(new int[]{(int)dx,(int)dy});
			}

		} catch(Exception z) {
		}
	}

	void collectCam() {
		QueryExecute q0;
		try {

	System.out.println("NODE CNT: "+ aNodeId.size());
			int camCnt = 0;
			for(int i=0; i<aNodeId.size(); i++) {
				int[] nds = aNodeOrg.get(i);
				String nodeid = aNodeId.get(i);
				q0 = PopiangUtil.query0(nodeid+" vp:name ?name ;"
					+ " vp:gps ?gps ;"
					+ " vp:utm ?utm .");
				hNode.put(nodeid, q0);

				String nodeutm = q0.get("?utm");
				double[] dnodeutm = PopiangUtil.txt2dbl(nodeutm);

				q0 = PopiangUtil.query0("?camid vp:node "+aNodeId.get(i)+" .");
				String[] camids = q0.gets("?camid");
		System.out.println("  :"+ nodeid + " : "+ camids.length);
				for(String cid : camids) {
//		System.out.println("  "+cid);
					camCnt++;
					double[] d1=null,d0=null;
					q0 = PopiangUtil.query0(cid
						+" vp:name ?name ;"
						+" vp:node ?node ;"
						+" vp:product ?prodId ;"
						+" vp:floor ?floor ;"
						+" vp:northAngle ?norAng ;"
						+" vp:maxCover ?maxCov ;"
						+" vp:gps ?gps ;"
						+" vp:utm ?utm ;"
						+" vp:upAngle ?upAng ;"
						+" vp:attachTo ?attach .\n");

					String camutm = q0.get("?utm");
					double[] dcamutm = PopiangUtil.txt2dbl(camutm);
					String fl = q0.get("?floor");
					if(floor!=null && !floor.equals(fl)) continue;
					aCamId.add(cid);
					aCamName.add(q0.get("?name"));
					hCam.put(cid, q0);
					String utm = q0.get("?utm");
//		System.out.println("    "+utm);
					d1 = PopiangUtil.txt2dbl(utm);
//		System.out.println("    "+d1);
					double dx = (aa * (d1[0] - dlt[0]));
					double dy = (double)dim[1] - (aa * (d1[1] - drb[1]));

					String na = q0.get("?norAng");
					String di = q0.get("?maxCov");
					String pa = null;
					aCamOrg.add(new int[]{(int)dx, (int)dy});

					String pi = q0.get("?prodId");
//		System.out.println("   !!! "+ pi);
					if(pi!=null && (q0=hPrd.get(pi))==null) {
						q0 = PopiangUtil.query0(pi+" vp:camType ?type ;"
							+ " vp:coverDistance ?dist ;"
							+ " vp:panAngle ?panAng .\n");
						hPrd.put(pi, q0);
					}
					if(di==null) di = q0.get("?dist");
					pa = q0.get("?panAng");
//		System.out.println("    >>> " + na+" di:"+di);


					if(na!=null && di!=null && pa!=null) {
						double dna = -1, dis = -1, dpa = -1;
						Shape camshp = null;
						Shape cambod = null;
						if(na.endsWith("deg") && di.endsWith("m")) {
							try {
								dna=Double.parseDouble(na.substring(0,na.length()-3));
								dis=Double.parseDouble(di.substring(0,di.length()-1));
								dpa=Double.parseDouble(pa.substring(0,pa.length()-3));
							} catch(Exception z){}
//		System.out.println("    >>> dna:" + dna+" dis:"+dis+" dpa:"+dpa);
							if(dna>-359 && dna<360 && dis>1) {
								double dnar, dns, dew;
								int p0x,p0y, p1x,p1y, p2x,p2y, p3x,p3y;

								GeneralPath bod = new GeneralPath();
								GeneralPath shp = new GeneralPath();
	
								dnar = Math.toRadians(dna);

								double camln = 1;
								dns = aa * Math.cos(dnar) * camln;
								dew = aa * Math.sin(dnar) * camln;
								bod.moveTo(dx,dy);
								bod.lineTo(dx+(int)dew, dy-(int)dns);
								dns = aa * Math.cos(dnar+Math.PI) * camln;
								dew = aa * Math.sin(dnar+Math.PI) * camln;
								bod.moveTo(dx,dy);
								bod.lineTo(dx+(int)dew, dy-(int)dns);
	
//								dns = aa * Math.cos(dnar) * dis;
//								dew = aa * Math.sin(dnar) * dis;
//								shp.moveTo(dx,dy);
//								shp.lineTo(dx+(int)dew, dy-(int)dns);
	
								dnar = Math.toRadians(dna-dpa/2);
								dns = aa * Math.cos(dnar) * dis;
								dew = aa * Math.sin(dnar) * dis;
								shp.moveTo(dx,dy);
								shp.lineTo(dx+(int)dew, dy-(int)dns);
								p0x = (int)dx+(int)dew;
								p0y = (int)dy-(int)dns;
	
								dnar = Math.toRadians(dna-dpa/6);
								dns = aa * Math.cos(dnar) * dis;
								dew = aa * Math.sin(dnar) * dis;
//								shp.moveTo(dx,dy);
//								shp.lineTo(dx+(int)dew, dy-(int)dns);
								p1x = (int)dx+(int)dew;
								p1y = (int)dy-(int)dns;
	
								dnar = Math.toRadians(dna+dpa/6);
								dns = aa * Math.cos(dnar) * dis;
								dew = aa * Math.sin(dnar) * dis;
//								shp.moveTo(dx,dy);
//								shp.lineTo(dx+(int)dew, dy-(int)dns);
								p2x = (int)dx+(int)dew;
								p2y = (int)dy-(int)dns;
	
								dnar = Math.toRadians(dna+dpa/2);
								dns = aa * Math.cos(dnar) * dis;
								dew = aa * Math.sin(dnar) * dis;
								shp.moveTo(dx,dy);
								shp.lineTo(dx+(int)dew, dy-(int)dns);
								p3x = (int)dx+(int)dew;
								p3y = (int)dy-(int)dns;
	
								shp.moveTo(p0x, p0y);
								shp.curveTo(p1x, p1y, p2x, p2y, p3x, p3y);
	
								camshp = shp;
								cambod = bod;
//		System.out.println("  ang:"+ dna+" dns:"+dns+" dew:"+dew);
							}
							Shape cc = new Ellipse2D.Double(dx, dy, 20, 20);
							Shape bo = new Ellipse2D.Double(dx, dy, 3, 3);
							if(camshp!=null) cc = camshp;
							if(cambod!=null) bo = cambod;
							aCam.add(cc);
							aBod.add(bo);
						} else {
							Shape cc = new Ellipse2D.Double(dx, dy, 8, 8);
							Shape bo = new Ellipse2D.Double(dx, dy, 3, 3);
							aCam.add(cc);
							aBod.add(bo);
						}

						GeneralPath poe = new GeneralPath();
						poe.moveTo(dx,dy);
						poe.lineTo(nds[0],nds[1]);
						aPoe.add(poe);

						double lenx = dcamutm[0] - dnodeutm[0];
						double leny = dcamutm[1] - dnodeutm[1];
						double len = Math.sqrt( lenx*lenx + leny*leny );
						aPoeLen.add(len);
						
					}
				}
			}
	System.out.println("CAMERA CNT: "+ camCnt+" : "+aCam.size()+" : "+aPoe.size());
		} catch(Exception z) {
			z.printStackTrace();
		}
    }   

	void collectCables() {
		try {
//		System.out.println("CAM CNT: "+ aCam.size());

			aCable = new ArrayList<>();
			aCableOrg = new ArrayList<>();
			for(int i=0; i<aCableId.size(); i++) {
				List<double[]> aPnts = hCablePnts.get(aCableId.get(i));
				GeneralPath cable = new GeneralPath();
				double[] d1=null,d0=null;
				int x1,y1,x2=0,y2=0,x0=0,y0=0,x3=0,y3=0;
				for(int j=0; j<aPnts.size(); j++) {
					d0 = d1;
					d1 = aPnts.get(j);
					x2 = (int) (aa * (d1[0] - dlt[0]));
					y2 = dim[1] - (int) (aa * (d1[1] - drb[1]));
					x3 += x2;
					y3 += y2;
					if(j==0) {
						x0 = x2; y0 = y2;
						cable.moveTo(x0, y0);
						continue;
					} else {
						cable.lineTo(x2, y2);
					}
					x1 = (int) (aa * (d0[0] - dlt[0]));
					y1 = dim[1] - (int) (aa * (d0[1] - drb[1]));
				}
				x3 /= aPnts.size();
				y3 /= aPnts.size();
				aCable.add(cable);
				aCableOrg.add(new int[]{x3,y3});
			}

		} catch(Exception z) {
			z.printStackTrace();
		}
    } 

	SimpleDateFormat datefm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	BufferedImage drawImage() {
		try {
			imgRes = new BufferedImage(dim[0], dim[1] 
						, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = imgRes.createGraphics();

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);

			AffineTransform oldtrans = g2.getTransform();
			AffineTransform trans = new AffineTransform();

			if(bMV) {
				trans.setToIdentity();
				trans.translate(dim[0]/2, dim[1]/2);
				trans.scale(sca,sca);
				trans.rotate(rot);
				trans.translate(-mvx, -mvy);

				Font font = new Font(null, Font.PLAIN, 10);    
				AffineTransform fonttran = new AffineTransform();
				fonttran.rotate(-rot, 0, 0);
				Font font2 = font.deriveFont(fonttran);
				g2.setTransform(trans);
				g2.setFont(font2);
			}
			g2.drawImage(imgMap, 0,0,dim[0],dim[1], 0,0,dim[0],dim[1], null);
			g2.setPaint(Color.white);
			AlphaComposite a00 = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 1.0f);
			AlphaComposite al2 = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.8f);
			AlphaComposite alp = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.5f);
			g2.setComposite(alp);
			g2.fillRect(0,0, dim[0],dim[1]);
			g2.setComposite(a00);

			g2.setColor(colPlace);
			g2.setStroke(new BasicStroke(1));
			for(int i=0; i<aPlaceId.size(); i++) {
				g2.draw(aPlace.get(i));
				int[] org = aPlaceOrg.get(i);
				if(showPlcNm!=null && showPlcNm.indexOf("no")>=0) ;
				else 
					PopiangUtil.drawString(g2, aPlaceName.get(i), org[0], org[1]);
			}
			g2.setColor(colCam0);
//			g2.setPaint(Color.white);
			g2.setStroke(new BasicStroke(1));
			for(int i=0; i<aCam.size(); i++) {
				g2.setColor(colCam0);
				g2.setStroke(new BasicStroke(1));
				g2.draw(aCam.get(i));

				g2.setPaint(colCam2);
				g2.setStroke(new BasicStroke(5));
				g2.draw(aBod.get(i));

				if(showPoe!=null && showPoe.indexOf("no")>=0) ;
				else {
					g2.setPaint(colCam4);
					g2.setStroke(new BasicStroke(1));
					g2.draw(aPoe.get(i));
				}

//				g2.setComposite(al2);
//				g2.fill(aCam.get(i));
//				g2.setComposite(a00);

				if(showCamId!=null && showCamId.indexOf("no")>=0) ;
				else {
					g2.setPaint(colCam3);
					int[] org = aCamOrg.get(i);
					PopiangUtil.drawString(g2
						, aCamId.get(i), org[0], org[1], 1);
				}
			}
			g2.setColor(colCable);
			g2.setStroke(new BasicStroke(4));
			for(int i=0; i<aCableId.size(); i++) {
				g2.draw(aCable.get(i));
				int[] org = aCableOrg.get(i);
				PopiangUtil.drawString(g2, aCableName.get(i), org[0], org[1]);
			}
			if(showNode!=null && showNode.indexOf("no")>=0) ;
			else {
				g2.setColor(colNode);
				g2.setStroke(new BasicStroke(4));
				for(int i=0; i<aNodeId.size(); i++) {
					g2.draw(aNode.get(i));
					int[] org = aNodeOrg.get(i);
					PopiangUtil.drawString(g2, aNodeId.get(i), org[0], org[1], 1);
				}
			}

			trans.setToIdentity();
			g2.setTransform(oldtrans);
			g2.dispose();

			return imgRes;
		} catch(Exception x) {
		}
		return null;
	}

	void save(BufferedImage img1, String rid) {
		try {
			String rdfnm = rid.substring(0,rid.indexOf(":"));
			String dirnm = rdfnm.substring(0, rdfnm.length()-2);
			String imfn = rid.replace(":","-");

			String sp = ""+tfinf.path;
			sp = sp.substring(0,sp.lastIndexOf("/rdf"))
				+"/res/"+dirnm+"/"+rdfnm+"/"+imfn+".jpg";
			String rs = "file:"+dirnm+"/"+rdfnm+"/"+imfn+".jpg";

			File f = new File(sp);
			if(!f.getParentFile().exists()) f.getParentFile().mkdirs();
			ImageIO.write(img1, "JPG", f);

			String rdftxt = "\ta\tvo:Image;\tvp:name\t'"+vnm+"' ;\n"+
							"vp:date\t'___DATETIME___' ;\n"+
							"vp:size\t'___SIZE___' ;\n"+
							"vp:image\t'___IMAGE___' .\n";
			int c = tfinf.text.getCaretPosition();
			java.util.Date dt = Calendar.getInstance().getTime();
			String ddtt = datefm.format(dt);
			String ins = rid + rdftxt.replace("___IMAGE___",rs)
				.replace("___DATETIME___",ddtt).replace("___SIZE___",sz);
			tfinf.text.insert(ins, tfinf.text.getCaretPosition());
			tfinf.text.setCaretPosition(c+ins.length());
			tfinf.text.requestFocus();

		    Desktop dsk = Desktop.getDesktop();
		    dsk.open(f);

		} catch(Exception z) {
			z.printStackTrace();
		}
    }   

}

