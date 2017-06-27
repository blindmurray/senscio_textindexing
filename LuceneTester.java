//utf-encoding?? -- pdfs, docs (the ones that aren't encoded in utf 8) are not showing up properly
//slashes counting as word break?? http://www.unicode.org/reports/tr29/
//gina's branch does not have client class
	//pull request from within branch??
//eventual connect servlet with jar file on server, index all files there
	//?: where is the index for those files? extra folder on server?
	//connect with eric + cathy's web server
//parsed pdf returns nonsense, but its corresponding txt file has actual words
	//same goes for .doc files
//maybe we shouldn't create a txt file for each -- takes space and causes confusion for users
	//just use the string from parsing? is it possible to index using that?
	//or maybe create the file and then delete?
//could we have a program constantly running on the server listening for new/updated files
	//when received, it could re-index, that way people don't have to actually do it
	//or people can add new files through the website
	//not sure how they would update/edit files tho
	//also why is the index locked.... that's dumb bc if you just check for new/updated
	// files and then add them to the index wouldn't that be a lot faster
//need to incorporate searching for phrases -- multitermquery (in Searcher class??)
	//spantermquery, see https://lucene.apache.org/core/6_2_0/core/org/apache/lucene/search/spans/SpanQuery.html
//PARSING CORRUPTS FILES??? THE FILES FOR WHICH TXT FILES WERE CREATED CAN'T BE OPENED
	//pdfs and .doc seemed fine, docx and excels couldn't be opened
//stop words: a, an, and, are, as, at, be, but, by, for, if, in, into, is, it, no, not, of, on, or
// such, that, the, their, then, there, these, they, this, to, was, will, with
//querying phrases like "new york" as one phrase and not two words
// without disrupting normal separate word querying
//can use boosts to favor whole phrase together over the indiv. words in separate places
	//this would be a lot to account for -- especially with 15 word searches or something
import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class LuceneTester {
public static void main(String[] args) throws Exception {
	
	/*String user = "root";
    String password = "s3nsci0";
    String host = "10.0.55.90";
    int port=22;
    ChannelSftp sftpChannel = null;
    //String remoteFile="10.0.55.90/var/www/Library/Internal%20Document%20Repository/";

    try{
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
        System.out.println("Establishing Connection...");
        session.connect();
            System.out.println("Connection established.");
        System.out.println("Crating SFTP Channel.");
        sftpChannel = (ChannelSftp) session.openChannel("sftp");
        sftpChannel.connect();
        System.out.println("SFTP Channel created.");
        }
    catch(Exception e){System.err.print(e);}
*/
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
	clean.deleteFolderContents(indexDirFile);
	   
	//Call the Indexer.java file and create an indexer
	Indexer.createIndex(indexDir, dataDir, indexer);
	Searcher search = new Searcher();
	   
	//The string you are searching for in the files
    Scanner sc = new Scanner(System.in);
    String querystr = sc.nextLine();
	   
	//Call Searcher class to search for the string
	search.searchIndex(querystr, indexDir);
   }
   
}