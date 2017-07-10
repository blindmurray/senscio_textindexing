/* different types of queries (phrase, etc)
 * file upload
 	* drag and drop, choose directory to save in
 		* https://www.sitepoint.com/html5-file-drag-and-drop/
 		* https://css-tricks.com/drag-and-drop-file-uploading/
 		* #fail
 	* warning when saving to place that has file of same name
 * renaming files (searching titles)
 * HTML HTML HTML HTML HTML (make stuff pretty)
 * gina's number 774-285-1474 
 * transfer data from node js to html/javascript
 * duplicated files?? only w/ other index
 * add instructions for search
 	* don't use punctuation 
 	* search for key words only
 	* if using extensions, list with periods, NO SPACES
 	* maybe use drop down menu instead
 * weird thing-search received times=which number search it is
 */
import java.io.*;
import java.net.*;
import java.util.ArrayList;
 
public class Server_Socket {
 
private static int port =  1221;
public static ServerSocket ssock = null;
public static String line;
public static DataInputStream is;
public static PrintStream os;
	 
@SuppressWarnings("deprecation")
public static void main(String[] args) {
	
	try {
		ssock = new ServerSocket(port);
		System.out.println("SockServer waiting for connections on 1221...");
		while( true ){ 
			try {
				 Socket csock = ssock.accept();
				 System.out.println("incoming...");
				 is = new DataInputStream(csock.getInputStream());
				 os = new PrintStream(csock.getOutputStream());
				 ArrayList<String> stuff = new ArrayList<String>();
				 String message = "";
				 while(true){
					line = is.readLine();
					System.out.println(line);
					String[] ar = line.split("~s@");
					line = ar[0];
					String ext = "";
					if(ar.length>1){
						ext = ar[1];
						System.out.println(ext);
					}
					
					if(!line.isEmpty()){
						String indexDir = "/Users/Gina/Documents/OneDrive/txt_index";

						try {  
							//The string you are searching for in the files							
							System.out.println(line);
							//Call Searcher class to search for the string
							Searcher s = new Searcher();
							stuff = s.searchIndex(line, indexDir, 20, ext);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						message = "Search: " + line + "<br>";
						for(String st: stuff){
							message = message + st + "<br>";
						}
						}
					//put reading info functionality here
					 
					os.println(message);
					line = "";
					//see above line, use to return info
					//when it gets to server, alert results
					//later change to new web page instead
					System.out.println("sys - " + "\n"+ message);
					if (line.equals("bye")){ 
						//csock.close();	
						break;
					} 
				 }	
			}
			catch (IOException e) {
				System.out.println(e);
				}
			}
		} 
	catch (IOException e) {
		System.out.println(e);
	}   	
} //main	

 } // class socksrvr