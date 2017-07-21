/* different types of queries (phrase, etc)
 * check for same name files when saving to place that has file of same name
 * add instructions for search
 	* don't use punctuation 
 	* search for key words only
 	* if using extensions, list with periods, NO SPACES
 	* maybe use drop down menu instead for extensions
 * spellcheck
 * show part of file where the searched word is, like google does
 * extract document after search, provide link
 * give option for exact phrase search (use quotes??)
 * at some point, go through and add try catches so the program keeps running in case of error
 * folder change ability + alert to reread directory and send html to js
 * alert if only one date entered, or if first is after second
 * search by created date
 * put in metadata key words when uploading (who uploaded, upload date)
 * folder permissions
 * plurals (stemming), reindex with word stems
 * scanned documents
 * comment searchnode code
 */
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import org.apache.tika.exception.TikaException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xml.sax.SAXException;

public class Server_Socket {

	private static int port =  1221;
	public static ServerSocket ssock = null;
	public static String line;
	public static DataInputStream is;
	public static PrintStream os;
	public static Date lastcheck = new Date();
	static String indexDir = "/Users/Gina/Documents/Files/GitHub/senscio_textindexing/txt_index";
	//static String indexDir = "/var/www/library/index";
	static String dataDir = "/Users/Gina/Documents/Files/GitHub/senscio_textindexing/test-project/files";
	//static String dataDir = "/var/www/library/Internal Document Repository";
	static Indexer indexer = null;
	static File indexDirFile = new File(indexDir);
	static String html = "<ul id=\"expList\">";

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws TikaException, SAXException {

		try {
			ssock = new ServerSocket(port);
			System.out.println("SockServer waiting for connections on 1221...");
			while(true){ 
				try {
					//creates socket and input/output streams
					Socket csock = ssock.accept();
					System.out.println("incoming...");
					is = new DataInputStream(csock.getInputStream());
					os = new PrintStream(csock.getOutputStream());
					ArrayList<String> stuff = new ArrayList<String>();
					String message = "";
					//listen for message from node.js
					while(true){
						boolean check = true;
						String string = null;
						//tries to read message
						line = is.readLine();
						System.out.println(line);
						JSONObject json = new JSONObject(line);
						//if there is a message, check what kind of message 
						if(!line.isEmpty()){
							//if the message is a search
							if(json.getString("id").equals("search")){
								try {  			
									//Call Searcher class to search for the string
									Searcher s = new Searcher();
									stuff = s.searchIndex(json, indexDir, 20);
								} catch (Exception e) {
									e.printStackTrace();
								}
								message = "Search: " + json.getString("searchterm") + "<br>";
								for(String st: stuff){
									message = message + st + "<br>";
								}
								//send results back to node.js
								os.println(message);
								line = "";
								System.out.println("Search Results" + "\n"+ message);
							}
							//if there was a file upload
							else if(json.getString("id").equals("upload")){
									File[] files = new File(json.getString("path_new")).listFiles();
									//check if uploaded file has same name as another in the same folder
									for(int i = 0; i < json.getJSONArray("filepaths").length(); i++){
										for (File file : files) {
											string = json.getJSONArray("filepaths").getString(i);
											String fpath = file.toString();
											fpath = fpath.replace("\\", "/");
											if(fpath.equals(string)){
												check = false;
											}
										}
									//return results to node.js
									if (check){
										os.println("no duplicates");
									}
									else{
										os.println("ERROR. Please rename file to avoid duplicate names");
									}
								}
							}
							else if(json.getString("id").equals("tree")){
								String tree = DirectoryReader.listFilesForFolder(new File(dataDir), "<ul id=\"expList\">");
								os.println(tree);
								
							}
							else if(json.getString("id").equals("saved")){
								for(int i = 0; i < json.getJSONArray("filepaths").length(); i++){
									string = json.getJSONArray("filepaths").getString(i);
									UpdateIndex.updateIndex(string, indexDir);
								}
								os.println("Indexed!");
							}
							else if(json.getString("id").equals("addFolder")){
								String path = json.getString("filepaths");
								if(path.equals("")){
								    File dir = new File(dataDir + "/" + json.getString("name"));
								    String s = dir.toString();
					        		for (int j = 0; j < s.length(); j++){
					        		    char c = s.charAt(j);        
					        		    if(c == ' '){
					        		    	s = s.replace(c, '_');
					        		    }
					        		}
					        		File f1 = new File (s);
						        	dir.renameTo(f1);
								    dir.mkdir();
									String tree = DirectoryReader.listFilesForFolder(new File(dataDir), "<ul id=\"expList\">");
									os.println(tree);
								}
								else{
									File dir = new File(dataDir + "/" + json.getString("name"));
								    String s = dir.toString();
					        		for (int j = 0; j < s.length(); j++){
					        		    char c = s.charAt(j);        
					        		    if(c == ' '){
					        		    	s = s.replace(c, '_');
					        		    }
					        		}
					        		File f1 = new File (s);
						        	dir.renameTo(f1);
						        	dir.mkdir();
									String tree = DirectoryReader.listFilesForFolder(new File(dataDir), "<ul id=\"expList\">");
									os.println(tree);
								}
							}
							
							//this will re-read the folders to change the expanding tree
							else if(json.getString("id").equals("folderchange")){
								String html = "<ul id=\"expList\">";
								html = listFilesForFolder(new File(dataDir), html);
								File uploadhtml = new File("test-project/upload.html");
								Document doc = Jsoup.parse(uploadhtml, "UTF-8");
								Element exptree = doc.select("#exptree").first();
								exptree.html(html);
						}
						}
						if (line.equals("bye")){ 
							csock.close();	
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
	} //end main
	//same as in directory reader, will eventually move to here
	public static String listFilesForFolder(File folder, String html) {
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				int count = 0;
				for(File files :fileEntry.listFiles()){
					if(files.isDirectory()){
						count++;
					}
				}
				if(count>0){
				html += "<li>" + "<span>" + fileEntry.getName() + "</span>";
				html += "<ul>";
				html = listFilesForFolder(fileEntry, html);
				html+= "</li>";
				}
				else{
					html += "<li>" + "<span>" + fileEntry.getName() + "</span>"+ "</li>";
				}
			} 
		}
		html += "</ul>";
		return html;
	}
}