import java.io.File;
import java.util.Scanner;

public class runProgram {
	public static void main(String[] args) throws Exception {
	Scanner s = new Scanner(null);
		
		
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
