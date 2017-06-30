//need to receive search text in searchnode, then send to server
//see below
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
						System.out.println("true");
						String indexDir = "/Users/Gina/Documents/OneDrive/txt_index";
						String querystr= null;
						try {  
							//The string you are searching for in the files
							querystr = line;								
							System.out.println(querystr);
							//Call Searcher class to search for the string
							Searcher s = new Searcher();
							stuff = s.searchIndex(querystr, indexDir, 20);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for(String st: stuff){
							message = message + st;
						}
						}
					//put reading info functionality here
					os.println("fromserver " + message); 	
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