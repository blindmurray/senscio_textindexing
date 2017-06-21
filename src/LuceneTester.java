import java.io.File;
import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class LuceneTester {
   
   public static void main(String[] args) throws Exception {
	   String indexDir = "/MICHELLE/txt_index";
	   String dataDir = "/MICHELLE/txt_data";
	   Indexer indexer = null;
	   
	   File indexDirFile = new File(indexDir);
	   clean.deleteFolderContents(indexDirFile);
	   Indexer.createIndex(indexDir, dataDir, indexer);
	  
	   Searcher search = new Searcher();
	   String querystr = "Sales";
	   search.searchIndex(querystr, indexDir);
   }
   
}