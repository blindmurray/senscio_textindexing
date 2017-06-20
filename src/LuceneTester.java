import java.io.File;
import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class LuceneTester {
   
   public static void main(String[] args) throws IOException, TikaException, SAXException, ParseException {
	   String indexDir = "/Users/Gina/Documents/OneDrive/txt_index";
	   String dataDir = "/Users/Gina/Documents/OneDrive/txt_data";
	   Indexer indexer = null;
	   
	   File indexDirFile = new File(indexDir);
	   clean.deleteFolderContents(indexDirFile);
	   Indexer.createIndex(indexDir, dataDir, indexer);
	  
	   Searcher search = new Searcher();
	   String querystr = "Meeting";
	   search.searchIndex(querystr, indexDir);
   }
   
}