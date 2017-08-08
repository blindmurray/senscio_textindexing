import java.io.File;
import java.io.IOException;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class Index{
	
public static void main(String[] args) throws IOException, TikaException, SAXException {
	String indexDir = "/Users/Gina/Documents/Files/GitHub/senscio_textindexing/txt_index";
	//String indexDir = "/Users/linjiang/Documents/GitHub/senscio_textindexing/txt_index";
	//static String indexDir = "/var/www/library/index";
	String dataDir = "/Users/Gina/Documents/Files/GitHub/senscio_textindexing/test-project/files";
	//String dataDir = "/Users/linjiang/Documents/GitHub/senscio_textindexing/test-project/files";

	//static String dataDir = "/var/www/library/Internal Document Repository";
	
	Indexer indexer = null;
	  
	//Create index calls Indexer class
	File indexDirFile = new File(indexDir);
	TextFileFilter.clear(indexDirFile);
	Indexer.createIndex(indexDir, dataDir, indexer);
}

}
