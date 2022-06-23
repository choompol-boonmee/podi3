package popdig;

import java.util.*;

public class CCLibrary implements Comparable<CCLibrary> {
		int lv;
		String id, name, den, biz, card, type, den2;
		String DT,DTQ,PT,PTQ,OT,OTQ, tag;
		List<CCLibrary> chd = new ArrayList<>();
		public int compareTo(CCLibrary ccl) {
			return den2.compareTo(ccl.den2);
		}
		public CCLibrary clone() {
			CCLibrary ccl = new CCLibrary();
			ccl.lv=lv; ccl.id=id; ccl.name=name; ccl.den=den; ccl.biz=biz;
			ccl.card=card; ccl.type=type; ccl.den2=den2;
			ccl.DT=DT; ccl.DTQ=DTQ; ccl.PT=PT; ccl.PTQ=PTQ; ccl.OT=OT; ccl.OTQ=OTQ;
			return ccl;
		}
}

