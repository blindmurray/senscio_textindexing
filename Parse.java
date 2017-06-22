import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.iwork.IWorkPackageParser;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class Parse {

public static void parse(String filePath) throws IOException, FileNotFoundException, TikaException, SAXException{
		
	//detecting the file type
	BodyContentHandler handler = new BodyContentHandler(-1);
	Metadata metadata = new Metadata();
	FileInputStream inputstream = new FileInputStream(filePath);
	ParseContext pcontext = new ParseContext();

	//PDF parser
	Parser parser = new AutoDetectParser(); 
	parser.parse(inputstream, handler, metadata,pcontext);

	//getting the content of the document
	String content = handler.toString();
		
	//call TXT class to create the .txt document
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
		
    //call TXT class to create the .txt document
    TXT.createTXT(content, filePath);
	
}
public static void parseMS(String filePath) throws IOException, TikaException, SAXException {
	
	//detecting the file type
	BodyContentHandler handler = new BodyContentHandler();
    Metadata metadata = new Metadata();
    FileInputStream inputstream = new FileInputStream(filePath);
    ParseContext pcontext = new ParseContext();
	    
    //iWorks parser
    OOXMLParser parser = new OOXMLParser();
    parser.parse(inputstream, handler, metadata,pcontext);
	    
    //getting the content of the document
    String content = handler.toString();
		
    //call TXT class to create the .txt document
    TXT.createTXT(content, filePath);
	
}
 }