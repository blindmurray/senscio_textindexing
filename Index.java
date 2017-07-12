import java.io.File;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class Index{
	
public static void main(String[] args) throws IOException, TikaException, SAXException {
	//String indexDir = "C:/MICHELLE/txt_index";
    String indexDir = "/var/www/library/index";
	//String dataDir = "C:/MICHELLE/txt_data";
    String dataDir = "/var/www/library/Internal Document Repository";
	Indexer indexer = null;
	   
	File indexDirFile = new File(indexDir);
	TextFileFilter.clear(indexDirFile);
	Indexer.createIndex(indexDir, dataDir, indexer);
}

}
