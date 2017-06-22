import java.io.File;
import java.io.InputStream;

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
	String querystr = "the Meeting";
	   
	//Call Searcher class to search for the string
	search.searchIndex(querystr, indexDir);
   }
   
}