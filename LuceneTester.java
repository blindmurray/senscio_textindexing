import java.io.File;

public class LuceneTester {
public static void main(String[] args) throws Exception {
	//Input path of location for the index
	String indexDir = "/MICHELLE/txt_index";
	   
	//Input path of location for the directory that has all of the files
	String dataDir = "/MICHELLE/txt_data";
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
	String querystr = "the Meeting";
	   
	//Call Searcher class to search for the string
	search.searchIndex(querystr, indexDir);
   }
   
}