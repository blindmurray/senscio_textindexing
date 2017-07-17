/* different types of queries (phrase, etc)
 * file upload
 * drag and drop, choose directory to save in (dropdown folder hierarchy)
 * https://www.sitepoint.com/html5-file-drag-and-drop/
 * https://css-tricks.com/drag-and-drop-file-uploading/
 * #fail
 * warning when saving to place that has file of same name
 * renaming files (searching titles) https://www.w3schools.com/nodejs/nodejs_filesystem.asp
 * add instructions for search
 * don't use punctuation 
 * search for key words only
 * if using extensions, list with periods, NO SPACES
 * maybe use drop down menu instead for extensions
 * weird thing-search received times=which number search it is
 * spellcheck
 * show part of file where the searched word is, like google does
 * give option for exact phrase search (use quotes??)
 * DirectoryReader -- need to trigger it if someone updates a folder
 * at some point, go through and add try catches so the program keeps running in case of error
 * folder change ability + alert to reread directory and send html to js
 * client.write not working for file upload
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
	static String indexDir = "C:/MICHELLE/txt_index";
	//static String indexDir = "/Users/Gina/Documents/Files/txt_index";
	//static String indexDir = "/var/www/library/index";
	static String dataDir = "C:/MICHELLE/txt_data";
	//static String dataDir = "/Users/Gina/Documents/Files/txt_data";
	//static String dataDir = "/var/www/library/Internal Document Repository";
	static Indexer indexer = null;
	static File indexDirFile = new File(indexDir);


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
						JSONObject json = new JSONObject(line);
						if(!line.isEmpty()){
							if(json.getString("id").equals("search")){
								try {  			
									//Call Searcher class to search for the string
									Searcher s = new Searcher();
									stuff = s.searchIndex(json, indexDir, 20);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								message = "Search: " + json.getString("searchterm") + "<br>";
								for(String st: stuff){
									message = message + st + "<br>";
								}

								//put reading info functionality here

								os.println(message);
								line = "";
								//see above line, use to return info
								//when it gets to server, alert results
								//later change to new web page instead
								System.out.println("Search Results" + "\n"+ message);

							}
							else if(json.getString("id").equals("upload")){
								try {
									boolean check = true;
									String string = null;
									File[] files = new File(json.getString("pathway")).listFiles();
									for(int i = 0; i < json.getJSONArray("filepaths").length(); i++){
										for (File file : files) {
											string = json.getJSONArray("filepaths").getString(i);
											String fpath = file.toString();
											fpath = fpath.replace("\\", "/");
											if(fpath.equals(string)){
												check = false;
											}
										}
									}
									if (check == true){
										UpdateIndex.updateIndex(string, indexDir);
										os.println("no duplicates");
									}
									else{
										os.println("ERROR. Please rename file to avoid duplicate names");
									}
								} catch (TikaException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (SAXException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								os.println("done");
								System.out.println("indexed");
							}
							//when we implement folder changing/adding/deleting on the website
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

	public static void directoryChangeCheck(File f){
		Date modDate = new Date(f.lastModified());
		if(modDate.after(lastcheck)){
			String html = "<ul id=\"expList\">";
			listFilesForFolder(new File(dataDir), html);
			System.out.println(html);
			lastcheck = new Date();
			return;
		}
		for (File fileEntry : f.listFiles()) {
			if (fileEntry.isDirectory()) {
				directoryChangeCheck(fileEntry);
			} 
		}
	}
} // class socksrvr