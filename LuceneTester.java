//slashes counting as word break?? http://www.unicode.org/reports/tr29/
//could we have a program constantly running on the server listening for new/updated files
	//when received, it could re-index, that way people don't have to actually do it
	//or people can add new files through the website
	//not sure how they would update/edit files tho
	//also why is the index locked.... that's dumb bc if you just check for new/updated
	// files and then add them to the index wouldn't that be a lot faster
//need to incorporate searching for phrases -- multitermquery (in Searcher class??)
	//spantermquery, see https://lucene.apache.org/core/6_2_0/core/org/apache/lucene/search/spans/SpanQuery.html
	//pdfs and .doc seemed fine, docx and excels couldn't be opened
// such, that, the, their, then, there, these, they, this, to, was, will, with
//querying phrases like "new york" as one phrase and not two words
// without disrupting normal separate word querying
//can use boosts to favor whole phrase together over the indiv. words in separate places
	//this would be a lot to account for -- especially with 15 word searches or something
import java.io.File;
import java.util.Scanner;

public class LuceneTester {
@SuppressWarnings("resource")

public static void main(String[] args) throws Exception {

    //Input path of location for the index
	//String indexDir = "/Users/Gina/Documents/OneDrive/txt_index";
    String indexDir = "/var/www/library/index";
	//Input path of location for the directory that has all of the files
	//String dataDir = "/Users/Gina/Documents/OneDrive/txt_data";
    String dataDir = "/var/www/library/Internal Document Repository";
	Indexer indexer = null;
	   
	File indexDirFile = new File(indexDir);
	   
	/*Call clean.java class to clear all of the files created in the index 
	 * directory from previous runs
	 */
	Scanner sc = new Scanner(System.in);
	System.out.println("Would you like to re-index the repository? (yes or no)");
	
	if(sc.nextLine().equals("yes")){
		TextFileFilter.clear(indexDirFile);
		Indexer.createIndex(indexDir, dataDir, indexer);
	}

	//Call the Indexer.java file and create an indexer
	Searcher search = new Searcher();
	//The string you are searching for in the files
	boolean a = true;
	while (a == true) {
		System.out.println("What would you like to search for? If you would like to stop, please type 'q'.");
		String querystr = sc.nextLine();
        Scanner m = new Scanner(System.in);
		if(querystr.equalsIgnoreCase("q")){
            a=false;
        }
        else{
        	System.out.println("How many hits would you like to see?");
        	int n = m.nextInt();
        	search.searchIndex(querystr, indexDir, n);
        }

    }
   }
   
}