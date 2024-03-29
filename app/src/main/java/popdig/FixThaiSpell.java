package popdig;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.apache.log4j.Logger;
import org.apache.commons.lang3.StringUtils;

public class FixThaiSpell extends PopupAction {
	static Logger log = Logger.getLogger(FixThaiSpell.class);
	public FixThaiSpell(JPopupMenu pop, TextFileInfo tfi, String lb) {
		super(pop, tfi, lb);
	}
    public void action() {
		fixMisSpell();
    }   
	void fixMisSpell() {
		TextFileInfo tinf = tfinf;
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

		{"ขอมูลเปด","ข้อมูลเปิด"},
		{"วิเคราะห", "วิเคราะห์"},
		{"สราง", "สร้าง"},
		{"อยาง", "อย่าง"},
		{"เกณฑ", "เกณฑ์"},
		{"เจา", "เจ้า"},
		{"ขอมูล", "ข้อมูล"},
		{"ให", "ให้"},
		{"เปด", "เปิด"},
		{"หนวย", "หน่วย"},
		{"ผ ู", "ผู้"},
		{"เบอร", "เบอร์"},
		{"หมวดหมู", "หมวดหมู่"},
		{"ลิงก", "ลิงก์"},
		{"ใชประโยชน", "ใช้ประโยชน์"},
		{"องคกร", "องค์กร"},
		{"ดาน", "ด้าน"},
		{"ตาง", "ต่าง"},
		{"เชน", "เช่น"},
		{"แกปญหา", "แก้ปัญหา"},
		{"แมนยํา", "แม่นยำ"},
		{"ไปใช", "ไปใช้"},
		{"แบง", "แบ่ง"},
		{"เปน", "เป็น"},
		{"สวน", "ส่วน"},
		{"รับฟง", "รับฟัง"},
		{"ผูรบั ผิดชอบ", "ผู้รับผิดชอบ"},
		{"ใชงาน", "ใช้งาน"},
		{"ศูนย", "ศูนย์"},
		{"อยาง", "อย่าง"},
		{"มปี ระสิทธิภาพ", "มีประสิทธิภาพ"},
		{"รวม", "ร่วม"},
		{"สงผลให", "ส่งผลให้"},
		{"เขา", "เข้า"},
		{"เอื้อตอ", "เอื้อต่อ"},
		{"เจา", "เจ้า"},
		{"หนา", "หน้า"},
		{"ผลลัพธ", "ผลลัพธ์"},
		{"ไอรแลนด", "ไอร์แลนด์"},
		{"แตละเมือง", "แต่ละเมือง"},
		{"คอรรัปชัน", "คอร์รัปชัน"},
		{"เว็บไซต", "เว็บไซต์"},
		{"รายจาย", "รายจ่าย"},
		{"ประจําป", "ประจำปี"},
		{"แหลง", "แหล่ง"},
		{"ผาน", "ผ่าน"},
		{"สวน", "ส่วน"},
		{"ตอง", "ต้อง"},
		{"วาเป็น", "ว่าเป็น"},
		{"ผูมี ", "ผู้"},
		{"อยใู น", "อยู่ใน"},
		{"ขอมลู", "ข้อมูล"},
		{"จากแหลง", "จากแหล่ง"},
		{"แลว ตอง", "แล้วต้อง"},
		{"แสดงไว", "แสดงไว้"},
		{"หมวดหมู", "หมวดหมู่"},
		{"แกไข", "แก้ไข"},
		{"ปญหา", "ปัญหา"},
		{"ดา นต่าง", "ด้านต่าง"},
		{"กลุม", "กลุ่ม"},
		{"ผใู ช", "ผู้ใช้"},
		{"คาใชจาย", "ค่าใช้จ่าย"},
		{"หรือนอย", "หรือน้อย"},
		{"ตองการ", "ต้องการ"},
		{"ผูใช", "ผู้ใช้"},
		{"ไมขึ้น", "ไม่ขึ้น"},
		{"แพลตฟอรม", "แพลตฟอร์ม"},
		{"ไมจํากัด", "ไม่จำกัด"},
		{"ถูกตอง", "ถูกต้อง"},
		{"อย่างนอย", "อย่างน้อย"},
		{"ครบถวน", "ครบถ้วน"},
		{"ใ หสอดคลอง", "ให้สอดคล้อง"},
		{"คาวาง", "ค่าว่าง"},
		{"พรอมเปิดเรยี บรอ ยแลว", "พร้อมเปิดเรียบร้อยแล้ว"},
		{"ประโยชนแก", "ประโยชน์แก่"},
		{"ไดแก", "ได้แก่"},
		{"รัฐธรรมนูญแหง", "รัฐธรรมนูญแห่ง"},
		{"ขอบขาย", "ขอบข่าย"},
		{"ความวา", "ความว่า"},
		{"ที่ใช", "ที่ใช่"},
		{"รูเร่ือง", "รู้เรื่อง"},
		{"ขอเท็จจริง", "ข้อเท็จจริง"},
		{"ไมวาการ", "ไม่ว่าการ"},
		{"ทําได", "ทำได้"},
		{"ดวยเครื่อง", "ด้วยเครื่อง"},
		{"เผยแพร", "เผยแพร่"},
		{"อางอิง", "อ้างอิง"},
		{"เห็นวา", "เห็นว่า"},
		{"ใหญ", "ใหญ่"},
		{"ไดวา", "ได้ว่า"},
		{"สัมพันธ", "สัมพันธ์"},
		{"ดังกลาว", "ดังกล่าว"},
		{"ขางตน", "ข้างต้น"},
		{"แลว", "แล้ว"},
		{"หนว ย", "หน่วย"},
		{"ขาวกรอง", "ข่าวกรอง"},
		{"วาดวย", "ว่าด้วย"},
		{"แหงชาติ", "แห่งชาติ"},
		{"มิได", "มิได้"},
		{"เวนแต", "เว้นแต่"},
		{"แหงรฐั", "แห่งรัฐ"},
		{"ประโยชน", "ประโยชน์"},
		{"ปจจัย", "ปัจจัย"},
		{"วาดวย", "ว่าด้วย"},
		{"ขาวสาร", "ข่าวสาร"},
		{"ไดงาย", "ได้ง่าย"},
		{"อานไดดวย", "อ่านได้ด้วย"},
		{"ไมมี", "ไม่มี"},
		{"แลว", "แล้ว"},
		{"ดวย", "ด้วย"},
		{"ขอตกลง", "ข้อตกลง"},
		{"เฝาระวัง", "เฝ้าระวัง"},
		{"พรอมใช", "พร้อมใช้"},
		{"มั่นใจวา", "มั่นใจว่า"},
		{"ตอเนื่อง", "ต่อเนื่อง"},
		{"อุปกรณ", "อุปกรณ์"},
		{"สามารถใช", "สามารถใช้"},
		{"เดิมได", "เดิมได้"},
		{"ให้อยู", "ให้อยู่"},
		{"ขอ มูล", "ข้อมูล"},
		{"ความถ่ใี นการ", "ความถี่ในการ"},
		{"เผยแพรแ ละ", "เผยแพร่"},
		{"อย่างนอยปละ", "อย่างน้อยปีละ"},
		{"ความนาเชื่อถือ", "ความน่าเชื่อถือ"},
		{"ผูใชข้อมูล", "ผู้ใช้ข้อมูล"},
		{"การใชง าน", "การใช้งาน"},
		{"เผยแพรแ ละ", "เผยแพร่"},
		{"ตอคุณภาพ", "ต่อคุณภาพ"},
		{"การอนมุ ัติ", "การอนุมัติ"},
		{"ความพรอม", "ความพร้อม"},
		{"ขอตกลง", "ข้อตกลง"},
		{"เฝาระวัง", "เฝ้าระวัง"},
		{"ตอเนื่อง", "ต่อเนื่อง"},
		{"กูคืน", "กู้คืน"},
		{"เฝา", "เฝ้า"},
		{"ให้อยูใน", "ให้อยู่ใน"},
		{"ขอกําหนด", "ข้อกำหนด"},
		{"คอรรปั ชัน", "คอร์รัปชัน"},
		{"ไดอ ยาง", "ได้อย่าง"},
		{"สงเสริม", "ส่งเสริม"},
		{"ประโยชน์ตอ", "ประโยชน์ต่อ"},
		{"ตอยอด", "ต่อยอด"},
		{"ได", "ได้"},
		{"แตละ", "แต่ละ"},
		{"ปองกัน", "ป้องกัน"},
		{"ใชใน", "ใช้ใน"},
		{"คุณคา", "คุณค่า"},
		{"ไว", "ไว้"},
		{"กอให้เกิด", "ก่อให้เกิด"},
		{"ระบุวา", "ระบุว่า"},
		{"การคา", "การค้า"},
		{"ไดร ับ", "ได้รับ"},
		{"ไวลวงหน้า", "ไว้ล่วงหน้า"},
		{"ทรพั ยสิน", "ทรัพย์สิน"},
		{"ปญญา", "ปัญญา"},
		{"เกี่ยวของ", "เกี่ยวข้อง"},
		{"อาจกอ", "อาจก่อ"},
		{"ในหัวขอ", "ในหัวข้อ"},
		{"ปจจุบัน", "ปัจจุบัน"},
		{"ปรับแตง", "ปรับแต่ง"},
		{"อยูใน", "อยู่ใน"},
		{"เทาที่", "เท่าที่"},
		{"คนหา", "ค้นหา"},
		{"ใช งาน", "ใช้งาน"},
		{"ได้งาย", "ได้ง่าย"},
		{"โดยไม", "โดยไม่"},
		{"ใช้งานตอได้", "ใช้งานต่อได้"},
		{"ไมเลือก", "ไม่เลือก"},
		{"จําเป็นตอการ", "จำเป็นต่อการ"},
		{"การใชข้อมูล", "การใช้ข้อมูล"},
		{"กวางขวาง", "กว้างขวาง"},
		{"แตยัง", "แต่ยัง"},
		{"เผยวา", "เผยว่า"},
		{"ทราบวา", "ทราบว่า"},
		{"ใดบาง", "ใดบ้าง"},
		{"ยังไม่มีแตต องการ", "ยังไม่มีแต่ต้องการ"},
		{"แตต้องเป็น", "แต่ต้องเป็น"},
		{"จัดจาง", "จัดจ้าง"},
		{"ดา นต่าง", "ด้านต่าง"},
		{"วาข้อมูล", "ว่าข้อมูล"},
		{"เผยกอน", "เผยก่อน"},
		{"ให้คา", "ให้ค่า"},
		{"ได้งาย", "ได้ง่าย"},
		{"คณุ คา", "คุณค่า"},
		{"คณุ ภาพ", "คุณภาพ"},
		{"อยูใน", "อยู่ใน"},
		{"ให้แนใจวา", ""},
		{"คุมครอง", "คุ้มครอง"},
		{"ต้องไม", "ต้องไม่"},
		{"ไมได้", "ไม่ได้"},
		{"ภายใต", "ภายใต้"},
		{"วัตถุประสงค", "วัตถุประสงค์"},
		{"ทางออม", "ทางอ้อม"},
		{"ปกปอง", "ปกป้อง"},
		{"ขนสง", "ขนส่ง"},
		{"ที่อยู", "ที่อยู่"},
		{"วันเดือนป", "วันเดือนปี"},
		{"ไมสามารถ", "ไม่สามารถ"},
		{"ผิดพลาดนอยมาก", "ผิดพลาดน้อยมาก"},
		{"ที่นาเชื่อถือ", "ที่น่าเชื่อถือ"},
		{"สอดคลอง", "สอดคล้อง"},
		{"ยังไมพร้อม", "ยังไม่พร้อม"},
		{"การคา", "การค้า"},
		{"ขอจํากัด", "ข้อจำกัด"},
		{"ไมเสีย", "ไม่เสีย"},
		{"เปาหมาย", "เป้าหมาย"},
		{"แพรหลาย", "แพร่หลาย"},
		{"ไดส ะดวก", "ได้สะดวก"},
		{"พรอมเปิด", "พร้อมเปิด"},
		{"เรยี บรอ ย", "เรียบร้อย"},
		{"ขอความ", "ข้อความ"},
		{"กอนที่", "ก่อนที่"},
		{"ไมใหเ กิด", "ไม่ให้เกิด"},
		{"เหตุการณ", "เหตุการณ์"},
		{"ยอนกลับ", "ย้อนกลับ"},
		{"อย่างนอยปละ", "อย่างน้อยปีละ"},
		{"ผูใช", "ผู้ใช้"},
		{"ทสี่ งผล", "ที่ส่งผล"},
		{"อยูใน", "อยู่ใน"},
		{"ใหม", "ใหม่"},
		{"โปรงใส", "โปร่งใส"},
		{"กออาชญา", "ก่ออาชญา"},
		{"เสนทาง", "เส้นทาง"},
		{"แวดลอม", "แวดล้อม"},
		{"อยูอาศัย", "อยู่อาศัย"},
		{"ติดตอธุรกิจ", "ติดต่อธุรกิจ"},
		{"ทองเที่ยว", "ท่องเที่ยว"},
		{"ชวงเวลา", "ช่วงเวลา"},
		{"แนวโนม", "แนวโน้ม"},
		{"บอยครั้ง", "บ่อยครั้ง"},
		{"ขนสง", "ขนส่ง"},
		{"สินคา", "สินค้า"},
		{"เสนทาง", "เส้นทาง"},
		{"คาโดยสาร", "ค่าโดยสาร"},
		{"ภูมิศาสตร", "ภูมิศาสตร์"},
		{"อยูวา", "อยู่ว่า"},
		{"ลาชา", "ล่าช้า"},
		{"หรือไม", "หรือไม่"},
		{"วาเคย", "ว่าเคย"},
		{"ยอนหลัง", "ย้อนหลัง"},
//่		{"ป", ""},
		{"คมุ คา", "คุ้มค่า"},
		{"กระตุน", "กระตุ้น"},
		{"ตอการ", "ต่อการ"},
		{"แหงสหัสวรรษ", "แห่งสหัสวรรษ"},
		{"เปาหมาย", "เป้าหมาย"},
		{"ฟลด", "ฟิลด์"},
		{"ตอเน่ือง", "ต่อเนื่อง"},
		{"สมบูรณ", "สมบูรณ์"},
		{"ชวยให้", "ช่วยให้"},
		{"ได้งาย", "ได้ง่าย"},
		{"ต้องไม", "ต้องไม่"},
		{"ตอได้", "ต่อได้"},
		{"การคา", "การค้า"},
		{"ผูใชข อมูล", "ผ้ใช้ข้อมูล"},
		{"ท่ใี ช", "ที่ใช้"},
		{"คาดวา", "คาดว่า"},
		{"ใชข้อมูล", "ใช้ข้อมูล"},
		{"ถายทอด", "ถ่ายทอด"},
		{"องคความรู", "องค์ความรู้"},
		{"สะทอน", "สะท้อน"},
		{"ดา นต่าง", "ด้านต่าง"},
		{"ได้งาย", "ได้ง่าย"},
		{"ท่มี ีคา", "ที่มีค่า"},
		{"ได้งาย", "ได้ง่าย"},
		{"จะชวย", "จะช่วย"},
		{"ต้องนอย", "ต้องน้อย"},
		{"จนกวา", "จนกว่า"},
		{"แหล่งท่ไี ดมา", "แหล่งที่ได้มา"},
		{"แมกระทั่ง", "แม้กระทั่ง"},
		{"เรียบรอย", "เรียบร้อย"},
		{"ทรัพยสิน", "ทรัพย์สิน"},
		{"ไมควร", "ไม่ควร"},
		{"ความวา", "ความว่า"},
		{"ไมแสดง", "ไม่แสดง"},
		{"ไฟล", "ไฟล์"},
		{"แหง", "แห่ง"},
		{"อย่างนอยปละ", "อย่างน้อยปีละ"},
		{"ใช้งานตอได้", "ใช้งานต่อได้"},
		{"ชองทาง", "ช่องทาง"},
		{"พรอมทั้ง", "พร้อมทั้ง"},
		{"แขงขัน", "แข่งขัน"},
		{"มูลคา", "มูลค่า"},
		{"ตอภาค", "ต่อภาค"},
		{"ใชเพื่อ", "ใช้เพื่อ"},
		{"ที่มีอยู", "ที่มีอยู่"},
		{"ใชชุดข้อมูล", "ใช้ชุดข้อมูล"},
		{"ผูเชี่ยวชาญ", "ผู้เชี่ยวชาญ"},
		{"๑๐ ป", "๑๐ ปี"},
		{"ผูติดตอ", "ผู้ติดต่อ"},
		{"องคการมหาชน", "องค์การมหาชน"},
		{"พรอมทั้ง", "พร้อมทั้ง"},
		{"จัดสง", "จัดส่ง"},
		{"ชองทาง", "ช่องทาง"},
		{"องคความรู", "องค์ความรู้"},
		{"กลยุทธ", "กลยุทธ์"},
		{"ถายทอด", "ถ่ายทอด"},
		{"ผูใชข อมูล", "ผู้ใช้ข้อมูล"},
		{"ผูบริหาร", "ผู้บริหาร"},
		{"คาดวา", "คาดว่า"},
		{"ชวยสร้าง", "ช่วยสร้าง"},
		{"น า", "นำ"},
		{"ท า", "ทำ"},
		{"อ า", "อำ"},
		{"ส า", "สำ"},
		{"จ า", "จำ"},
		{"ก า", "กำ"},
		{"ว ่า", "ว่า"},
		{"กี ่", "กี่"},
		{"ข ่า", "ข่า"},
		{"กล ่าว", "กล่าว"},
		{"ท า", "ทำ"},
		{"ค า", "คำ"},
		{"กลุ ่ม", "กลุ่ม"},
		{"ที ่", "ที่"},
		{"อื ่น", "อื่น"},
		{"เปลี ่ยน", "เปลี่ยน"},
		{"ช ่อ", "ช่อ"},
		{"ชื ่", "ชื่"},
	} ;
}

