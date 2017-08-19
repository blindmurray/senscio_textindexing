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
/**
 * The Parse class will parse files to retrieve their content and metadata. In addition, it calls
 * the TXT class to create a temporary .txt file with the same name for future purposes.
 * @author Gina
 *
 */
public class Parse {
	/**
	 * Parses the files contents and metadata as well as detecting file type and creating a .txt file.
	 * @param filePath Path of the file that needs to be parsed
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws TikaException
	 * @throws SAXException
	 */

	public static void parse(String filePath) throws IOException, FileNotFoundException, TikaException, SAXException {

		// Detecting the file type
		BodyContentHandler handler = new BodyContentHandler(-1);
		Metadata metadata = new Metadata();
		FileInputStream inputstream = new FileInputStream(filePath);
		ParseContext pcontext = new ParseContext();

		// PDF parser
		Parser parser = new AutoDetectParser();
		parser.parse(inputstream, handler, metadata, pcontext);

		// Getting the content of the document
		String content = handler.toString();

		// Call TXT class to create the .txt document
		FileNames.createTXT(content, filePath);
	}
}