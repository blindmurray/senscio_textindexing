import java.io.File;
import java.util.Scanner;
/*	import java.io.InputStream;
	import com.jcraft.jsch.ChannelSftp;
	import com.jcraft.jsch.JSch;
	import com.jcraft.jsch.Session;
*/

public class LuceneTester {
public static void main(String[] args) throws Exception {

    //Input path of location for the index
	String indexDir = "/Users/Gina/Documents/OneDrive/txt_index";
    //String indexDir = "/var/www/library/index";
	//Input path of location for the directory that has all of the files
	String dataDir = "/Users/Gina/Documents/OneDrive/txt_data";
    //String dataDir = "/var/www/library/Internal Document Repository";
	Indexer indexer = null;
	   
	File indexDirFile = new File(indexDir);

	System.out.println("Do you need to re-index the files? (y/n))");	
	Scanner scan = new Scanner(System.in);
	if (scan.nextLine().equals("y")){
	clean.deleteFolderContents(indexDirFile);
	Indexer.createIndex(indexDir, dataDir, indexer);
	}
	
	//Ask for the string you are searching for in the files
	 while (true) {
		 System.out.println("What would you like to search for? If you do not want to search for anything, please type 'quit'");
		 Scanner scanner = new Scanner(System.in);
		 Searcher search = new Searcher();
		 String querystr = scanner.nextLine();
		 if(querystr.equals("quit")){
			 break;
		 }
		 else{
			 search.searchIndex(querystr, indexDir);  //Call Searcher class to search for the string
		 }
	 }
   }
   
}