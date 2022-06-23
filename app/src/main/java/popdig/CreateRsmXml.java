package popdig;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.Stack;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.geom.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.crypto.SecretKey;
import org.apache.xml.security.encryption.XMLCipher;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.interfaces.*;

import org.apache.xml.security.encryption.XMLCipher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.keycloak.saml.processing.core.util.*;
import javax.xml.namespace.QName;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.*;
import javax.xml.crypto.dsig.spec.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.*;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.apache.pdfbox.pdmodel.interactive.annotation.*;

public class CreateRsmXml {
	List<CCLibrary> aCCL;
	public CreateRsmXml(List<CCLibrary> c) {
		aCCL = c;
/*
		for(int i=0; i<aCCL.size(); i++) {
			CCLibrary ccl = aCCL.get(i);
			if(ccl.tag==null) {
				if("ABIE".equals(ccl.type)) {
					if(ccl.lv==0) ccl.tag = PopiangUtil.xmlNDR(ccl.den2);
					else ccl.tag = "";
				} else if("ASBIE".equals(ccl.type) && i<aCCL.size()-1) {
					CCLibrary ccl2 = aCCL.get(i+1);
					String tag = ccl.den2 + " "+ ccl2.den2;
					ccl.tag = PopiangUtil.xmlNDR(tag);
				} else if("BBIE".equals(ccl.type)) {
					ccl.tag = PopiangUtil.xmlNDR(ccl.den2);
				}
			}
		}
*/
	}
	String sp = "  ";
	String ind(int n) {
		StringBuffer bf = new StringBuffer();
		for(int i=0; i<n; i++) {
			bf.append(sp);
		}
		return bf.toString();
	}
	public void createXmlFile(File fout, String rsm, String ram) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fout)));
			bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			Stack<String> sTag = new Stack<>();
			for(int i=0; i<aCCL.size(); i++) {
				CCLibrary ccl = aCCL.get(i);
				if(ccl.tag==null || ccl.tag.length()==0) continue;
				String prx = "ram"; if(ccl.lv<=1) prx = "rsm";
				while(ccl.lv<sTag.size()) {
					bw.write(sTag.pop());
				}
				if("BBIE".equals(ccl.type)) {
					bw.write(ind(ccl.lv)+"<"+prx+":"+ccl.tag+">...</"+prx+":"+ccl.tag+">\n");
				} else {
					if(i==0) {
						bw.write(ind(ccl.lv)+"<"+prx+":"+ccl.tag
							+" xmlns:rsm=\""+rsm+"\"\n"
							+" xmlns:ram=\""+ram+"\"\n"
							+">\n");
					} else {
						bw.write(ind(ccl.lv)+"<"+prx+":"+ccl.tag+">\n");
					}
					sTag.push(ind(ccl.lv)+"</"+prx+":"+ccl.tag+">\n");
				}
			}
			while(sTag.size()>0) {
				bw.write(sTag.pop());
			}
			bw.close();
		} catch(Exception x) {
		}
	}

	public void createCsvFile(File fout) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fout)));
			int cnt = 0;
			for(int i=0; i<aCCL.size(); i++) {
				CCLibrary ccl = aCCL.get(i);
				if(ccl.type.equals("BBIE")==false) continue;
				if(cnt>0) bw.write(",");
				bw.write("\""+ccl.biz+"\"");
				cnt++;
			}
			bw.write("\n");
			bw.close();
		} catch(Exception x) {
			x.printStackTrace();
		}
	}
	public void createJsonFile(File fout) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fout)));
			Stack<String> sTag = new Stack<>();
			Stack<CCLibrary> sCcl = new Stack<>();
			bw.write("{");
			int lv0 = -1;
			for(int i=0; i<aCCL.size(); i++) {
				CCLibrary ccl = aCCL.get(i);
				if(ccl.tag==null || ccl.tag.length()==0) continue;
				String prx = "ram"; if(ccl.lv<=1) prx = "rsm";
				int re = sTag.size() - ccl.lv;
				while(re-->0) {
					lv0 = sCcl.pop().lv;
					bw.write("\n");
					bw.write(sTag.pop());
				}
				if(ccl.lv==sTag.size()) {
					if(ccl.lv==lv0) bw.write(",");
					bw.write("\n");
				}
				if("BBIE".equals(ccl.type)) {
					bw.write(ind(ccl.lv+1)+"\""+ccl.tag+"\" : \"...\"");
				} else {
					if(i==0) {
						bw.write(ind(ccl.lv+1)
							+ "\""+ccl.tag+"\" : {");
					} else {
						bw.write(ind(ccl.lv+1)
							+ "\""+ccl.tag+"\" : {");
					}
					sTag.push(ind(ccl.lv+1)+"}");
					sCcl.push(ccl);
				}
				lv0 = ccl.lv;
			}
			while(sTag.size()>0) {
				bw.write("\n");
				bw.write(sTag.pop());
			}
			bw.write("\n}\n");
			bw.close();
		} catch(Exception x) {
			x.printStackTrace();
		}
	}
	public void createJsonLDFile(File fout, String rsm, String ram) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fout)));
			Stack<String> sTag = new Stack<>();
			Stack<CCLibrary> sCcl = new Stack<>();
			bw.write("{\n"
				   + "  \"@context\" : {\n"
				   + "    \"rsm\" : \""+rsm+"\",\n"
				   + "    \"ram\" : \""+ram+"\"\n"
				   + "  },"
			);
			int lv0 = -1;
			for(int i=0; i<aCCL.size(); i++) {
				CCLibrary ccl = aCCL.get(i);
				if(ccl.tag==null || ccl.tag.length()==0) continue;
				String prx = "ram"; if(ccl.lv<=1) prx = "rsm";
				int re = sTag.size() - ccl.lv;
				while(re-->0) {
					lv0 = sCcl.pop().lv;
					bw.write("\n");
					bw.write(sTag.pop());
				}
				if(ccl.lv==sTag.size()) {
					if(ccl.lv==lv0) bw.write(",");
					bw.write("\n");
				}
				if("BBIE".equals(ccl.type)) {
					bw.write(ind(ccl.lv+1)+"\""+prx+":"+ccl.tag+"\" : \"...\"");
				} else {
					if(i==0) {
						bw.write(ind(ccl.lv+1)
							+ "\""+prx+":"+ccl.tag+"\" : {");
					} else {
						bw.write(ind(ccl.lv+1)
							+ "\""+prx+":"+ccl.tag+"\" : {");
					}
					sTag.push(ind(ccl.lv+1)+"}");
					sCcl.push(ccl);
				}
				lv0 = ccl.lv;
			}
			while(sTag.size()>0) {
				bw.write("\n");
				bw.write(sTag.pop());
			}
			bw.write("\n}\n");
			bw.close();
		} catch(Exception x) {
			x.printStackTrace();
		}
	}
	public void createXsdFile(CCLibrary ccl, File fout, String rsm, String ram, String qdt, String udt) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fout)));
			bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
//			CCLibrary ccl = aCCL.get(0);
			bw.write("<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n");
			bw.write("  xmlns:ccts=\"urn:un:unece:uncefact:documentation:standard:"
				+ "CoreComponentsTechnicalSpecification:2\"\n");
			bw.write("  xmlns:rsm=\""+rsm+"\"\n");
			bw.write("  targetNamespace=\""+rsm+"\"\n");
			bw.write("  xmlns:ram=\""+ram+"\"\n");
			bw.write(">\n");
			bw.write("  <xsd:element name=\""+ccl.tag+"\" type=\"rsm:"+ccl.tag+"Type\">\n");
			bw.write("    <xsd:annotation>\n");
			bw.write("      <xsd:documentation xml:lang=\"en\">\n");
			bw.write("      </xsd:documentation>\n");
			bw.write("    </xsd:annotation>\n");
			bw.write("  </xsd:element>\n");
			bw.write("  <xsd:complexType name=\""+ccl.tag+"Type\">\n");
			bw.write("    <xsd:sequence>\n");
			for(int i=0; i<ccl.chd.size(); i++) {
				CCLibrary ccl2 = ccl.chd.get(i);
				bw.write("      <xsd:element name=\""+ccl2.tag+"\" type=\"ram:"
					+ ccl2.tag+"Type\" minOccors=\"1\">\n");
				bw.write("        <xsd:annotation>\n");
				bw.write("          <xsd:documentation xml:lang=\"en\">\n");
				bw.write("          </xsd:documentation>\n");
				bw.write("        </xsd:annotation>\n");
				bw.write("      </xsd:element>\n");
			}
			bw.write("    </xsd:sequence>\n");
			bw.write("  </xsd:complexType>\n");
			bw.write("</xsd:schema>\n");
			bw.close();
		} catch(Exception x) {
			x.printStackTrace();
		}
	}
	public void createRamXsdFile(CCLibrary ccl, File fout, String ram, String qdt, String udt) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fout)));
			bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			bw.write("<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n");
			bw.write("  xmlns:ccts=\"urn:un:unece:uncefact:documentation:standard:"
				+ "CoreComponentsTechnicalSpecification:2\"\n");
			bw.write("  targetNamespace=\""+ram+"\"\n");
			bw.write("  xmlns:ram=\""+ram+"\"\n");
			bw.write(">\n");
			String tag = ccl.OTQ+"_ "+ccl.OT;
			tag = PopiangUtil.xmlNDR(tag);
			bw.write("  <xsd:element name=\""+tag+"\" type=\"ram:"+tag+"Type\">\n");
			bw.write("    <xsd:annotation>\n");
			bw.write("      <xsd:documentation xml:lang=\"en\">\n");
			bw.write("      </xsd:documentation>\n");
			bw.write("    </xsd:annotation>\n");
			bw.write("  </xsd:element>\n");
			bw.write("  <xsd:complexType name=\""+tag+"Type\">\n");
			bw.write("    <xsd:sequence>\n");
			for(int i=0; i<ccl.chd.size(); i++) {
				CCLibrary ccl2 = ccl.chd.get(i);
				bw.write("      <xsd:element name=\""+ccl2.tag+"\" type=\"ram:"
					+ ccl2.tag+"Type\" minOccors=\"1\">\n");
				bw.write("        <xsd:annotation>\n");
				bw.write("          <xsd:documentation xml:lang=\"en\">\n");
				bw.write("          </xsd:documentation>\n");
				bw.write("        </xsd:annotation>\n");
				bw.write("      </xsd:element>\n");
			}
			bw.write("    </xsd:sequence>\n");
			bw.write("  </xsd:complexType>\n");
			bw.write("</xsd:schema>\n");
			bw.close();
		} catch(Exception x) {
			x.printStackTrace();
		}
	}
	public void createWsdlSend(File fout, String rsm) {
		try {
			CCLibrary ccl = aCCL.get(0);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fout)));
			bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			bw.write("<wsdl:definitions\n");
			bw.write("  xmlns:rsm=\""+rsm+"\"\n");
			bw.write("  xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\"\n");
			bw.write("  xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"\n");
			bw.write("  targetNamespace=\""+rsm+"Send\"\n");
			bw.write("  >\n");
			bw.write("  <wsdl:import namespace=\""+rsm+"\"/>\n");
			bw.write("  <wsdl:message name=\""+ccl.tag+"\">\n");
			bw.write("    <wsdl:part element=\"rsm:"+ccl.tag+"\" name=\"body\"/>\n");
			bw.write("  </wsdl:message>\n");
			bw.write("  <wsdl:message name=\"Acknowledge\">\n");
			bw.write("    <wsdl:part element=\"rsm:Acknowledge\" name=\"body\"/>\n");
			bw.write("  </wsdl:message>\n");
			bw.write("  <wsdl:portType name=\""+ccl.tag+"Send_PortType\">\n");
			bw.write("    <wsdl:input message=\""+ccl.tag+"\"/>\n");
			bw.write("    <wsdl:output message=\"Acknowledge\"/>\n");
			bw.write("  </wsdl:portType>\n");
			bw.write("  <wsdl:binding \n");
			bw.write("    name=\""+ccl.tag+"Send_Binding\"\n");
			bw.write("    type=\""+ccl.tag+"Send_PortType\"\n");
			bw.write("    >\n");
			bw.write("    <soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>\n");
			bw.write("    <wsdl:operation name=\""+ccl.tag+"Send\">\n");
			bw.write("      <wsdl:input message=\""+ccl.tag+"\">\n");
			bw.write("        <wsdl:body parts=\"body\" use=\"literal\"/>\n");
			bw.write("      </wsdl:input>\n");
			bw.write("      <wsdl:output message=\"Acknowledge\">\n");
			bw.write("        <wsdl:body parts=\"body\" use=\"literal\"/>\n");
			bw.write("      </wsdl:output>\n");
			bw.write("    </wsdl:operation>\n");
			bw.write("  </wsdl:binding>\n");
			bw.write("</wsdl:definitions>\n");
			bw.close();
		} catch(Exception x) {
			x.printStackTrace();
		}
	}
	public void createWsdlQuery(File fout, String rsm) {
		try {
			CCLibrary ccl = aCCL.get(0);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fout)));
			bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			bw.write("<wsdl:definitions\n");
			bw.write("  xmlns:rsm=\""+rsm+"\"\n");
			bw.write("  xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\"\n");
			bw.write("  xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"\n");
			bw.write("  targetNamespace=\""+rsm+"Query\"\n");
			bw.write("  >\n");
			bw.write("  <wsdl:import namespace=\""+rsm+"\"/>\n");
			bw.write("  <wsdl:message name=\"Query\">\n");
			bw.write("    <wsdl:part element=\"rsm:Query\" name=\"body\"/>\n");
			bw.write("  </wsdl:message>\n");
			bw.write("  <wsdl:message name=\""+ccl.tag+"\">\n");
			bw.write("    <wsdl:part element=\"rsm:"+ccl.tag+"\" name=\"body\"/>\n");
			bw.write("  </wsdl:message>\n");
			bw.write("  <wsdl:portType name=\""+ccl.tag+"Query_PortType\">\n");
			bw.write("    <wsdl:input message=\"Query\"/>\n");
			bw.write("    <wsdl:output message=\""+ccl.tag+"\"/>\n");
			bw.write("  </wsdl:portType>\n");
			bw.write("  <wsdl:binding \n");
			bw.write("    name=\""+ccl.tag+"Query_Binding\"\n");
			bw.write("    type=\""+ccl.tag+"Query_PortType\"\n");
			bw.write("    >\n");
			bw.write("    <soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>\n");
			bw.write("    <wsdl:operation name=\""+ccl.tag+"Query\">\n");
			bw.write("      <wsdl:input message=\"Query\">\n");
			bw.write("        <wsdl:body parts=\"body\" use=\"literal\"/>\n");
			bw.write("      </wsdl:input>\n");
			bw.write("      <wsdl:output message=\""+ccl.tag+"\">\n");
			bw.write("        <wsdl:body parts=\"body\" use=\"literal\"/>\n");
			bw.write("      </wsdl:output>\n");
			bw.write("    </wsdl:operation>\n");
			bw.write("  </wsdl:binding>\n");
			bw.write("</wsdl:definitions>\n");
			bw.close();
		} catch(Exception x) {
		}
	}
	public void createOaiYaml(File fout, String rsm, String ep, String op, String ac, boolean fg, int au) {
		try {
			CCLibrary ccl = aCCL.get(0);

			String req = ccl.tag;
			String res = ac;
			if(!fg) {
				String tmp = req;
				req = res;
				res = tmp;
			}
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fout)));
			bw.write("openapi: 3.0.0\n");
			bw.write("servers:\n");
			bw.write("- url: "+ep+"\n");
			bw.write("  description: "+op+" Data "+ccl.den2+"\n");
			bw.write("paths:\n");
			bw.write("  '/"+ccl.tag+op+"':\n");
			bw.write("    post:\n");
			bw.write("      summary: "+op+" "+ccl.den2+"\n");
			bw.write("      operationId: "+op+ccl.tag+"\n");
			switch(au) {
			case 1:
				bw.write("      security:\n");
				bw.write("        - ApiKeyAuth:[]\n");
				break;
			case 2:
				bw.write("      security:\n");
				bw.write("        - OAuth2:\n");
				bw.write("          - read\n");
				bw.write("          - write\n");
				break;
			case 3:
				break;
			case 4:
				break;
			}
			bw.write("      requestBody: \n");
			bw.write("        description: "+op+" Data "+ccl.tag+"\n");
			bw.write("        required: true\n");
			bw.write("        content:\n");
			bw.write("          application/json:\n");
			bw.write("            schema:\n");
			bw.write("              $ref: '#/components/schemas/"+req+"'\n");
			bw.write("      responses:\n");
			bw.write("        '200':\n");
			bw.write("          description: successful operation\n");
			bw.write("          content:\n");
			bw.write("            application/json:\n");
			bw.write("              schema:\n");
			bw.write("                $ref: '#/components/schemas/"+res+"'\n");
			bw.write("        '400':\n");
			bw.write("          description: Invalid ID supplied\n");
			bw.write("        '404':\n");
			bw.write("          description: Operation data not found\n");
			bw.write("        '405':\n");
			bw.write("          description: Validation exception\n");
			bw.write("components:\n");
			switch(au) {
			case 1:
				bw.write("  securitySchemes:\n");
				bw.write("    ApiKeyAuth:\n");
				bw.write("      type: apiKey\n");
				bw.write("      in: header\n");
				bw.write("      name: X-API-Key\n");
				break;
			case 2:
				bw.write("  securitySchemes:\n");
				bw.write("    OAuth2:\n");
				bw.write("      type: oauth2\n");
				bw.write("      flows:\n");
				bw.write("        authorizationCode\n");
				bw.write("          authorizationUrl: "+ep+"/oauth/authorize\n");
				bw.write("          tokenUrl: "+ep+"/oauth/token\n");
				bw.write("          scopes:\n");
				bw.write("            read: Grants read access\n");
				bw.write("            write: Grants write access\n");
				break;
			case 3:
				break;
			case 4:
				break;
			}
			bw.write("  schemas:\n");
			bw.write("    "+ac+":\n");
			bw.write("      type: object\n");
			bw.write("      properties:\n");
			bw.write("        name:\n");
			bw.write("          type: string\n");
			bw.write("    "+ccl.tag+":\n");
			bw.write("      type: object\n");
			bw.write("      properties:\n");
			for(int i=1; i<aCCL.size(); i++) {
				ccl = aCCL.get(i);
				if("BBIE".equals(ccl.type)) {
					bw.write("    "+ind(ccl.lv*2)+ccl.tag+":\n");
					bw.write("    "+ind(ccl.lv*2+1)+"type: string\n");
				} else if("ASBIE".equals(ccl.type)) {
					bw.write("    "+ind(ccl.lv*2)+ccl.tag+":\n");
					bw.write("    "+ind(ccl.lv*2+1)+"type: object\n");
					bw.write("    "+ind(ccl.lv*2+1)+"properties:\n");
				}
			}
			bw.close();
		} catch(Exception x) {
			x.printStackTrace();
		}
	}
	public void createWebLDFile(File fout, String rsm, String ram) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fout)));
			Stack<String> sTag = new Stack<>();
			Stack<CCLibrary> sCcl = new Stack<>();
			bw.write("<!DOCTYPE html>\n");
			bw.write("<html>\n");
			bw.write("<head>\n");
			bw.write("<title>EMBED DATA IN WEB SITE USING JSON-LD</title>\n");
			bw.write("<script type=\"application/ld+json\">\n");
			bw.write("{\n"
				   + "  \"@context\" : {\n"
				   + "    \"rsm\" : \""+rsm+"\",\n"
				   + "    \"ram\" : \""+ram+"\"\n"
				   + "  },"
			);
			int lv0 = -1;
			for(int i=0; i<aCCL.size(); i++) {
				CCLibrary ccl = aCCL.get(i);
				if(ccl.tag==null || ccl.tag.length()==0) continue;
//				if(ccl.lv>1) continue;
				String prx = "ram"; if(ccl.lv<=1) prx = "rsm";
				int re = sTag.size() - ccl.lv;
				while(re-->0) {
					lv0 = sCcl.pop().lv;
					bw.write("\n");
					bw.write(sTag.pop());
				}
				if(ccl.lv==sTag.size()) {
					if(ccl.lv==lv0) bw.write(",");
					bw.write("\n");
				}
				if("BBIE".equals(ccl.type)) {
					bw.write(ind(ccl.lv+1)+"\""+prx+":"+ccl.tag+"\" : \"...\"");
				} else {
					if(i==0) {
						bw.write(ind(ccl.lv+1)
							+ "\""+prx+":"+ccl.tag+"\" : {");
					} else {
						bw.write(ind(ccl.lv+1)
							+ "\""+prx+":"+ccl.tag+"\" : {");
					}
					sTag.push(ind(ccl.lv+1)+"}");
					sCcl.push(ccl);
				}
				lv0 = ccl.lv;
			}
			while(sTag.size()>0) {
				bw.write("\n");
				bw.write(sTag.pop());
			}
			bw.write("\n}\n");
			bw.write("</script>\n");
			bw.write("</head>\n");
			bw.write("<body>\n");
			bw.write("EMBED DATA IN WEB SITE USING JSON-LD\n");
			bw.write("<body>\n");
			bw.close();
		} catch(Exception x) {
			x.printStackTrace();
		}
	}
	public void createWebRDFa(File fout, String rsm, String ram) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fout)));
			Stack<String> sTag = new Stack<>();
			Stack<CCLibrary> sCcl = new Stack<>();
			bw.write("<!DOCTYPE html>\n");
			bw.write("<html>\n");
			bw.write("<head>\n");
			bw.write("</head>\n");
			bw.write("<body>\n");
			bw.write("EMBED DATA IN WEB SITE USING RDFa\n");
			int lv0 = -1;
			for(int i=0; i<aCCL.size(); i++) {
				CCLibrary ccl = aCCL.get(i);
				if(ccl.tag==null || ccl.tag.length()==0) continue;
				String prx = "ram"; if(ccl.lv<=1) prx = "rsm";
				int re = sTag.size() - ccl.lv;
				while(re-->0) {
					lv0 = sCcl.pop().lv;
					bw.write("\n");
					bw.write(sTag.pop());
				}
				if(ccl.lv==sTag.size()) {
					bw.write("\n");
				}
				if("BBIE".equals(ccl.type)) {
					bw.write(ind(ccl.lv+1)
						+"<span property=\""+prx+":"+ccl.tag+"\">"+ccl.biz+"</span>\n");
				} else {
					if(i==0) {
						bw.write(ind(ccl.lv+1)
							+ "<p prefix=\"rsm: "+rsm+"\" prefix=\"ram: "+ram+"\" typeof=\""
								+prx+":"+ccl.tag+"\">\n");
					} else {
						bw.write(ind(ccl.lv+1)
							+ "<p typeof=\""+prx+":"+ccl.tag+"\">\n");
					}
					sTag.push(ind(ccl.lv+1)+"</p>");
					sCcl.push(ccl);
				}
				lv0 = ccl.lv;
			}
			while(sTag.size()>0) {
				bw.write("\n");
				bw.write(sTag.pop());
			}
			bw.write("<body>\n");
			bw.close();
		} catch(Exception x) {
			x.printStackTrace();
		}
	}
	public void createWebMicroData(File fout, String rsm, String ram) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fout)));
			Stack<String> sTag = new Stack<>();
			Stack<CCLibrary> sCcl = new Stack<>();
			bw.write("<!DOCTYPE html>\n");
			bw.write("<html>\n");
			bw.write("<head>\n");
			bw.write("</head>\n");
			bw.write("<body>\n");
			bw.write("EMBED DATA IN WEB SITE USING RDFa\n");
			int lv0 = -1;
			for(int i=0; i<aCCL.size(); i++) {
				CCLibrary ccl = aCCL.get(i);
				if(ccl.tag==null || ccl.tag.length()==0) continue;
				String scp = ram; if(ccl.lv<=1) scp = rsm;
				int re = sTag.size() - ccl.lv;
				while(re-->0) {
					lv0 = sCcl.pop().lv;
					bw.write("\n");
					bw.write(sTag.pop());
				}
				if(ccl.lv==sTag.size()) {
					bw.write("\n");
				}
				if("BBIE".equals(ccl.type)) {
					bw.write(ind(ccl.lv+1)
						+"<span itemprop=\""+ccl.tag+"\">"+ccl.biz+"</span>\n");
				} else {
					if(i==0) {
						bw.write(ind(ccl.lv+1)
							+ "<p itemscope itemtype=\""+scp+ccl.tag+"\">\n");
					} else {
						bw.write(ind(ccl.lv+1)
							+ "<p itemscope itemtype=\""+scp+ccl.tag+"\">\n");
					}
					sTag.push(ind(ccl.lv+1)+"</p>");
					sCcl.push(ccl);
				}
				lv0 = ccl.lv;
			}
			while(sTag.size()>0) {
				bw.write("\n");
				bw.write(sTag.pop());
			}
			bw.write("<body>\n");
			bw.close();
		} catch(Exception x) {
			x.printStackTrace();
		}
	}
	public void createDocENC(File fout, File fin) {
		try {
			Document document = XMLUtil.getDocument(fin.getAbsolutePath());
			Element e = document.getDocumentElement();
			SecretKey secretKey = SecretKeyUtil.getSecretKey("AES");
			Document encryptedDoc = XMLUtil.encryptDocument(document, secretKey,XMLCipher.AES_128);
			XMLUtil.saveDocumentTo(encryptedDoc, fout.getAbsolutePath());
		} catch(Exception x) {
			x.printStackTrace();
		}
	}
	public void createDocDSIG(File fout, File fin) {
		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			Document doc = null;
			try (FileInputStream fis = new FileInputStream(fin)) {
				doc = dbf.newDocumentBuilder().parse(fis);
			}
        
			// Create a RSA KeyPair
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair kp = kpg.generateKeyPair();
        
			// Create a DOMSignContext and specify the RSA PrivateKey and
			// location of the resulting XMLSignature's parent element
			DOMSignContext dsc = new DOMSignContext
			(kp.getPrivate(), doc.getDocumentElement());
      
			// Create a DOM XMLSignatureFactory that will be used to generate the
			// enveloped signature
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

			// Create a Reference to the enveloped document (in this case we are
			// signing the whole document, so a URI of "" signifies that) and
			// also specify the SHA256 digest algorithm and the ENVELOPED Transform.
			Reference ref = fac.newReference
				("", fac.newDigestMethod(DigestMethod.SHA256, null),
				List.of
				(fac.newTransform
				(Transform.ENVELOPED, (TransformParameterSpec) null)),
				null, null);

			// Create the SignedInfo
			SignedInfo si = fac.newSignedInfo
				(fac.newCanonicalizationMethod
				(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
				(C14NMethodParameterSpec) null),
				fac.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null),
				List.of(ref));

			// Create a KeyValue containing the RSA PublicKey that was generated
			KeyInfoFactory kif = fac.getKeyInfoFactory();
			KeyValue kv = kif.newKeyValue(kp.getPublic());

			// Create a KeyInfo and add the KeyValue to it
			KeyInfo ki = kif.newKeyInfo(List.of(kv));

			// Create the XMLSignature (but don't sign it yet)
			XMLSignature signature = fac.newXMLSignature(si, ki);

			// Marshal, generate (and sign) the enveloped signature
			signature.sign(dsc);

			// output the resulting document
			OutputStream os = new FileOutputStream(fout);

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.transform(new DOMSource(doc), new StreamResult(os));

		} catch(Exception x) {
			x.printStackTrace();
		}
	}
}
 
