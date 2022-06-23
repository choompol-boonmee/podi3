package popdig;

import javax.swing.text.Highlighter.*;

public class TokenInfo {
	public String label;
	public int startOffset, endOffset;
	public HighlightPainter paint;
	public int type, stmPos, inList;
	public static final int TYPE_IRI = 1;
	public static final int TYPE_DATA = 2;
	public TokenInfo(String l, int st, int en, int pos, HighlightPainter p, int tp) {
		label = l;
		startOffset = st;
		endOffset = en;
		paint = p;
		stmPos = pos;
		type = tp;
	}
}

