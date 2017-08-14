import java.io.File;
import java.io.IOException;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class Index{
	
public static void main(String[] args) throws IOException, TikaException, SAXException {

	Indexer indexer = null;

	//Create index calls Indexer class
	File indexDirFile = new File(LuceneConstants.indexDir);
	TextFileFilter.clear(indexDirFile);
	Indexer.createIndex(LuceneConstants.indexDir, LuceneConstants.dataDir, indexer);
}

}
