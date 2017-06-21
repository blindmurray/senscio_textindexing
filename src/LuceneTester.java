import java.io.File;
import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class LuceneTester {
   
   public static void main(String[] args) throws Exception {
	   //Input path of location for the index
	   String indexDir = "/Users/Gina/Documents/OneDrive/txt_index";
	   
	   //Input path of location for the directory that has all of the files
	   String dataDir = "/Users/Gina/Documents/OneDrive/txt_data";
	   Indexer indexer = null;
	   
	   File indexDirFile = new File(indexDir);
	   
	   /*Call clean.java class to clear all of the files created in the index 
	    * directory from previous runs
	    */
	   clean.deleteFolderContents(indexDirFile);
	   
	   //Call the Indexer.java file and create an indexer
	   Indexer.createIndex(indexDir, dataDir, indexer);
	  
	   Searcher search = new Searcher();
	   
	   //The string you are searching for in the files
	   String querystr = "n";
	   
	   //Call Searcher class to search for the string
	   search.searchIndex(querystr, indexDir);
   }
   
}