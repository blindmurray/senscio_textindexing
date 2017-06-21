import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.parser.iwork.IWorkPackageParser;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.parser.odf.OpenDocumentParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.rtf.RTFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class Parse {

public static void parsePDF(String filePath) throws IOException, FileNotFoundException, TikaException, SAXException{
		
	//detecting the file type
	BodyContentHandler handler = new BodyContentHandler(-1);
	Metadata metadata = new Metadata();
	FileInputStream inputstream = new FileInputStream(filePath);
	ParseContext pcontext = new ParseContext();

	//PDF parser
	PDFParser pdfparser = new PDFParser(); 
	pdfparser.parse(inputstream, handler, metadata,pcontext);

	//getting the content of the document
	String content = handler.toString();
		
	//creating text file
	System.out.print("Creating file ");
		
	//call TXT class to create the .txt document
	TXT.createTXT(content, filePath);
}

public static void parseMSOffice(String filePath) throws IOException, TikaException, SAXException {
	      
	//detecting the file type
	BodyContentHandler handler = new BodyContentHandler();
	Metadata metadata = new Metadata();
	FileInputStream inputstream = new FileInputStream(filePath);
	ParseContext pcontext = new ParseContext();
      
	//OOXml parser
	OOXMLParser  msofficeparser = new OOXMLParser (); 
	msofficeparser.parse(inputstream, handler, metadata,pcontext);
	      
	//getting the content of the document
	String content = handler.toString();

	//creating text file
	System.out.print("Creating file ");
		
	//call TXT class to create the .txt document
	TXT.createTXT(content, filePath);
}

public static void parseHTML(String filePath) throws IOException, TikaException, SAXException {
	
	//detecting the file type
	BodyContentHandler handler = new BodyContentHandler();
    Metadata metadata = new Metadata();
    FileInputStream inputstream = new FileInputStream(filePath);
    ParseContext pcontext = new ParseContext();
	    
    //Html parser
    HtmlParser htmlparser = new HtmlParser();
    htmlparser.parse(inputstream, handler, metadata,pcontext);
	    
    //getting the content of the document
    String content = handler.toString();
	  
    //creating text file
    System.out.print("Creating file ");
    TXT.createTXT(content, filePath);
}

public static void parseIWORKS(String filePath) throws IOException, TikaException, SAXException {
		
	//detecting the file type
	BodyContentHandler handler = new BodyContentHandler();
    Metadata metadata = new Metadata();
    FileInputStream inputstream = new FileInputStream(filePath);
    ParseContext pcontext = new ParseContext();
	    
    //iWorks parser
    IWorkPackageParser iWorkPackageParser = new IWorkPackageParser();
    iWorkPackageParser.parse(inputstream, handler, metadata,pcontext);
	    
    //getting the content of the document
    String content = handler.toString();
	    
    //creating text file
    System.out.print("Creating file ");
		
    //call TXT class to create the .txt document
    TXT.createTXT(content, filePath);
	
}

public static void parseRTF(String filePath) throws IOException, TikaException, SAXException {
	
	//detecting the file type
	BodyContentHandler handler = new BodyContentHandler();
	Metadata metadata = new Metadata();
	FileInputStream inputstream = new FileInputStream(filePath);
	ParseContext pcontext = new ParseContext();
	    
	//RTF parser
	RTFParser rtfparser = new RTFParser();
	rtfparser.parse(inputstream, handler, metadata,pcontext);
	    
	//getting the content of the document
	String content = handler.toString();
		    
	//creating text file
	System.out.print("Creating file ");
			
	//call TXT class to create the .txt document
	TXT.createTXT(content, filePath);
}

public static void parseOpenOffice(String filePath) throws IOException, TikaException, SAXException {
	
	//detecting the file type
	BodyContentHandler handler = new BodyContentHandler();
	Metadata metadata = new Metadata();
	FileInputStream inputstream = new FileInputStream(filePath);
	ParseContext pcontext = new ParseContext();
    
	//OpenOffice parser
	OpenDocumentParser odparser = new OpenDocumentParser();
	odparser.parse(inputstream, handler, metadata,pcontext);
    
	//getting the content of the document
	String content = handler.toString();
    
	//creating text file
	System.out.print("Creating file ");
    	
	//call TXT class to create the .txt document
	TXT.createTXT(content, filePath);
}
}