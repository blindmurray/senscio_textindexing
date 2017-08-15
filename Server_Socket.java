/* different types of queries (phrase, etc)
 * try tomcat server connect directly to javascript
 * add instructions for search
 	* don't use punctuation 
 	* search for key words only
 	* if using extensions, list with periods, NO SPACES
 	* maybe use drop down menu instead for extensions
 * spellcheck
 * give option for exact phrase search (use quotes??)
 * at some point, go through and add try catches so the program keeps running in case of error
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
import java.sql.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.apache.tika.exception.TikaException;
import org.json.JSONObject;
import org.xml.sax.SAXException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

public class Server_Socket {

	private static int port =  1221;
	public static ServerSocket ssock = null;
	public static String line;
	public static DataInputStream is;
	public static PrintStream os;
	public static Date lastcheck = new Date();
	static Indexer indexer = null;
	static File indexDirFile = new File(LuceneConstants.indexDir);
	static String html = "<ul id=\"expList\">";

	public static void main(String[] args) throws Exception {
		String url = "jdbc:mysql://10.0.55.100:3306/";
        String username = "ibisua";
        String password = "ibisua";
        Class.forName("com.mysql.jdbc.Driver");
		try {
			ssock = new ServerSocket(port);
			System.out.println("SockServer waiting for connections on 1221...");
        	Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connecting database...");
			while(true){ 
				try {
					//creates socket and input/output streams
					Socket csock = ssock.accept();
					System.out.println("incoming...");
					is = new DataInputStream(csock.getInputStream());
					BufferedReader lines = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					os = new PrintStream(csock.getOutputStream());
					ArrayList<String> stuff = new ArrayList<String>();
					String message = "";
					
					//listen for message from node.js
					while(true){
						//tries to read message
						line = lines.readLine();
						System.out.println(line);
						JSONObject json = new JSONObject(line);
						//if there is a message, check what kind of message 
						if(!line.isEmpty()){
							//if the message is a search
							if(json.getString("id").equals("search")){
								try {  			
									//Call Searcher class to search for the string
									Searcher s = new Searcher();
									stuff = s.searchIndex(json, LuceneConstants.indexDir, json.getString("num"));
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
							else if(json.getString("id").equals("signIn")){
								
					            String idToken = json.getString("idtoken");
					            System.out.println(idToken.length());
					            GoogleIdToken.Payload payLoad = IdTokenVerifierAndParser.getPayload(idToken);
					            String userName = (String) payLoad.get("name");
					            String email = payLoad.getEmail();
					            System.out.println("User name: " + userName);
					            System.out.println("User email: " + email);
					            //if it is a senscio email, save into database
					            if(email.length()>19 && email.substring(email.length()-19).equals("@sensciosystems.com")){
						            try{
						            	System.out.println("Database connected!");
						                Statement st = conn.createStatement();
						                Statement n = conn.createStatement();
						                ResultSet rs = st.executeQuery("SELECT * FROM indexer.account WHERE email = '" + email + "'");
						                if(!rs.next()){
						                	st.executeUpdate("INSERT INTO indexer.account (`email`, `name`) VALUES ('"+ email + "', '" + userName + "')");
						                	n.executeQuery("ALTER TABLE `indexer`.`permissions` ADD" + email + "BINARY(1) NOT NULL DEFAULT 0;");
						                }
						                rs.close();

						            } catch (SQLException e) {
						                throw new IllegalStateException("Cannot connect the database!", e);
						            }
					            }
					            else{
					            	os.print("1");
					            }
							}
							//if there was a file upload
							else if(json.getString("id").equals("upload")){
								JSONArray filepaths = json.getJSONArray("filepaths");
								String pathnew = json.getString("path_new");
								for(int x = 0; x< filepaths.length(); x++){
									//for each file, check for duplicates, then move to destination folder and add to index
									String filepath = filepaths.getString(x);
									File file = new File(filepath);
									String filename = duplicateCheck(file.getName(), pathnew);
									Files.move(Paths.get(filepath), Paths.get(pathnew + "/" + filename), StandardCopyOption.REPLACE_EXISTING);
									UpdateIndex.updateIndex(pathnew + "/" + filename, json.getString("terms"), LuceneConstants.indexDir);
								}
								os.println("Indexed!");
							}
							else if(json.getString("id").equals("tree")){
								File file = new File(LuceneConstants.dataDir);
								//writes new html tree
								String tree = DirectoryReader.listFilesForFolder(file, html);
								//return to nodejs
								os.println(tree);
							}
							else if(json.getString("id").equals("addFolder")){
								String path = json.getString("filepaths");
								System.out.println(path);
								if(path.equals("")){
									 path = LuceneConstants.dataDir + "/" + json.getString("name");
								}
								//replace spaces with underscores
					        	for (int j = 0; j < path.length(); j++){
					       		    char c = path.charAt(j);        
					       		    if(c == ' '){
					       		    	path = path.replace(c, '_');
					       		    }
					      		}
					        	System.out.println("renamed");
					        	Path dir = Paths.get(path);
					        	//creates folder
					        	dir = Files.createDirectory(dir);
							    //redo tree so that user can see new folder
					        	System.out.println(dir);
								String tree = DirectoryReader.listFilesForFolder(new File(LuceneConstants.dataDir), "<ul id=\"expList\">");
								Statement st = conn.createStatement();
								st.executeUpdate("INSERT INTO indexer.permissions (`folderpath`) VALUES ('" + path + "');");
								os.println(tree);								
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
	}
	//end main
	public static String duplicateCheck(String filename, String path){
		boolean check = true;
		File[] files = new File(path).listFiles();
		//check if uploaded file has same name as another in the same folder
		for(int i = 0; i < files.length; i++){
			for (File file : files) {
				String fname = file.getName();
				if(fname.equals(filename)){
					check = false;
				}
			}
		//returns filename if no duplicates
			if (check){
				return filename;
			}
			else{
				//changes filename and rechecks for duplicates
				filename = changeName(filename);
				duplicateCheck(filename, path);
			}
		}
		return filename;
	}
	public static String changeName(String f){
		//if there is a duplicate name, adds (1) or adds 1 to version number
		String ext = TXT.getExtension(f);
		f = f.substring(0, f.length()-ext.length()-1);
		if(f.contains("(")){
			String noparen = f.substring(0, f.length() - 1);
		    String[] vnum = noparen.split("(");
		    String vnum2 = vnum[vnum.length-1];
		    int vint;
		        try { 
		        	vint = Integer.parseInt(vnum2);
		        } catch(NumberFormatException e) { 
		        	return f + "(1)" + ext;
		        } catch(NullPointerException e) {
		        	return f + "(1)" + ext;
		        }
		        vint++;
		        String newname = noparen.substring(0, noparen.length()-vnum2.length()) + "(" + vint + ")." + ext;
		        return newname;
		    }
		else{
			return f + "(1)." + ext;
		}
		
		
	}
}