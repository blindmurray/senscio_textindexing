//need to receive search text in searchnode, then send to server
/* different types of queries (phrase, etc)
 * file upload
 * 		drag and drop, choose directory to save in
 * renaming files (searching titles)
 * narrow down by extension
 * HTML HTML HTML HTML HTML
 * gina's number 774-285-1474 
 * output of data -- get it to print out on webpage, in html rather than alert
 * transfer data from node js to html/javascript
 * formidable to process forms for file upload
 * 
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
					if(!line.isEmpty()){
						String indexDir = "txt_index";

						try {  
							//The string you are searching for in the files							
							System.out.println(line);
							//Call Searcher class to search for the string
							Searcher s = new Searcher();
							stuff = s.searchIndex(line, indexDir, 20);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for(String st: stuff){
							message = message + st + "\n";
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