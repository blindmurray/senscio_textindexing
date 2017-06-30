import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class Parse {
	//autodetects file type and call TXT class to create txt file

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
}