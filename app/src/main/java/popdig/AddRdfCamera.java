package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class AddRdfCamera extends PopupAction {
	String voSub;
	public AddRdfCamera(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
		voSub = "vo:Cam";
	}
    public void action() {
		String latlon = PopiangUtil.getLatlongCB();
//System.out.println("latlon:"+latlon);
		if(latlon==null || latlon.length()==0) return;
		String utm = PopiangUtil.latlon2utm(latlon);
//System.out.println("utm:"+utm);
		String ddtt = PopiangUtil.timestamp();
		List<String> aCam0 = PopiangUtil.getVO(voSub);
		String pnt0 = PopiangDigital.getObject("vo:Point");
//System.out.println("pnt0:"+pnt0);
		if(pnt0!=null) {
			String utm0 = PopiangUtil.query0(pnt0+" vp:utm ?a").get();
			if(utm0!=null) {
//System.out.println("last point: "+ pnt0+" "+utm0);
				double[] dutm0 = PopiangUtil.txt2dbl(utm);
				double[] dutm1 = PopiangUtil.txt2dbl(utm0);
				double dy = dutm1[1] - dutm0[1];
				double dx = dutm1[0] - dutm0[0];
				double dz = Math.sqrt(dy*dy + dx*dx);
				double an = Math.atan2(dx, dy);
				int de = (int)Math.toDegrees(an);
				if(de<0) de += 360;
				String ad = String.format("%ddeg", de);
				String dm = String.format("%dm", (int)dz);
				Pattern p1 = Pattern.compile(".*vp:northAngle.+'([0-9]+deg)'.*");
				Pattern p2 = Pattern.compile(".*vp:maxCover.+'([0-9]+m)'.*");
//System.out.println("  dz:"+dm+" an:"+ad);
				List<String> aCam1 = new ArrayList<>();
				for(int i=0; i<aCam0.size(); i++) {
					String line = aCam0.get(i);
					Matcher m1 = p1.matcher(line);
					Matcher m2 = p2.matcher(line);
					if(m1.find()) {
//						System.out.println(" from: "+line);
//						System.out.println(" got : "+m1.group(1));
						line = line.replace(m1.group(1), ad);
//						System.out.println(" to  : "+line);
						aCam1.add(line);
					} else if(m2.find()) {
//						System.out.println(" from: "+line);
//						System.out.println(" got : "+m2.group(1));
						line = line.replace(m2.group(1), dm);
//						System.out.println(" to  : "+line);
						aCam1.add(line);
					} else {
						aCam1.add(line);
					}
				}
				aCam0 = aCam1;
			}
		}
        String r = getNewRdf(aCam0, voSub);
        if(r==null || r.length()==0) return;
		String rid = tfinf.text.newid;
		PopiangDigital.setObject(voSub, rid);
		r = r.replace("___GPS___",latlon).replace("___UTM___",utm);
        insert(r);
    }   
}

