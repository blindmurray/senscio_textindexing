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
import org.json.JSONObject;
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

		Class.forName("com.mysql.jdbc.Driver");
		try {
			ssock = new ServerSocket(port);
			System.out.println("SockServer waiting for connections on 1221...");
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
					String email = "";
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
								email = json.getString("email");
								try {  			
									//Call Searcher class to search for the string
									Searcher s = new Searcher();
									stuff = s.searchIndex(json, LuceneConstants.indexDir, json.getString("num"), email);
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
								Connection conn = DriverManager.getConnection(LuceneConstants.url, LuceneConstants.username, LuceneConstants.password);
								System.out.println("Connecting database...");
								String idToken = json.getString("idtoken");
								System.out.println(idToken.length());
								GoogleIdToken.Payload payLoad = IdTokenVerifierAndParser.getPayload(idToken);

								String userName = (String) payLoad.get("name");
								email = payLoad.getEmail();
								System.out.println("User name: " + userName);
								System.out.println("User email: " + email);
								//if it is a senscio email, save into database
								if(email.length()>19 && email.substring(email.length()-19).equals("@sensciosystems.com")){
									try{
										System.out.println("Database connected!");
										Statement st = conn.createStatement();
										ResultSet rs = st.executeQuery("SELECT * FROM indexer.account WHERE email = '" + email + "';");
										if(!rs.first()){
											st.executeUpdate("ALTER TABLE indexer.permissions ADD `" + email + "` TINYINT(1) NOT NULL DEFAULT 0;");
											st.executeUpdate("INSERT INTO indexer.account (email, name) VALUES ('" + email + "', '" + userName + "');");; 
											st.executeUpdate("UPDATE indexer.permissions SET `" + email + "` = 1 WHERE folderpath LIKE '" + LuceneConstants.dataDir + "/Public%';");
											os.println("done");
										}
										rs.close();


									} catch (SQLException e) {
										throw new IllegalStateException("Cannot connect the database!", e);
									}
								}
								else{
									os.print("Not a Senscio email.");
								}
								conn.close();
							}
							//if there was a file upload
							else if(json.getString("id").equals("upload")){
								JSONArray filepaths = json.getJSONArray("filepaths");
								email = json.getString("email");
								String pathnew = json.getString("path_new");
								for(int x = 0; x< filepaths.length(); x++){
									//for each file, check for duplicates, then move to destination folder and add to index
									String filepath = filepaths.getString(x);
									File file = new File(filepath);
									String filename = duplicateCheck(file.getName(), pathnew);
									Files.move(Paths.get(filepath), Paths.get(pathnew + "/" + filename), StandardCopyOption.REPLACE_EXISTING);
									UpdateIndex.updateIndex(pathnew + "/" + filename, json.getString("terms"), LuceneConstants.indexDir);
									Connection conn = DriverManager.getConnection(LuceneConstants.url, LuceneConstants.username, LuceneConstants.password);
									Statement st = conn.createStatement();
									st.executeUpdate("INSERT INTO indexer.files (filepath, owner) VALUES ('" + filepath + "', '" + email + "');");
								}
								os.println("Indexed!");
							}
							else if(json.getString("id").equals("tree")){
								email = json.getString("email");
								File file = new File(LuceneConstants.dataDir);
								//writes new html tree
								String tree = DirectoryReader.listFilesForFolder(file, html, email);
								//return to nodejs
								os.println(tree);
							}
							else if(json.getString("id").equals("autofillarray")){
								JSONObject array = new JSONObject();
								Connection conn = DriverManager.getConnection(LuceneConstants.url, LuceneConstants.username, LuceneConstants.password);
								Statement st = conn.createStatement();
								ResultSet rs = st.executeQuery("SELECT `email` FROM indexer.account;");
								ArrayList<String> al = new ArrayList<String>();
								while(rs.next()){
									al.add(rs.getString("email"));
								}
								JSONArray emails = new JSONArray(al);
								array.put("array", emails);
								String data = array.toString();
								System.out.println(data);
								os.println(data);
							}
							else if(json.getString("id").equals("addFolder")){
								String path = json.getString("filepaths");
								email = json.getString("email");
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
								Connection conn = DriverManager.getConnection(LuceneConstants.url, LuceneConstants.username, LuceneConstants.password);
								Statement st = conn.createStatement();
								st.executeUpdate("INSERT INTO indexer.permissions (`folderpath`) VALUES ('" + path + "');");
								String permissions = json.getString("permissions");
								System.out.println(permissions);
								if(!permissions.equals("")){
									String[] per = permissions.split("\\s+");

									for(String e: per){
										st.executeUpdate("UPDATE indexer.permissions SET `" + e + "` = 1 WHERE folderpath = '" + path + "';");
									}
								}
								st.executeUpdate("UPDATE indexer.permissions SET `" + email + "` = 2 WHERE folderpath = '" + path + "';");
								String tree = DirectoryReader.listFilesForFolder(new File(LuceneConstants.dataDir), "<ul id=\"expList\">", email);
								os.println(tree);							
								conn.close();
							}
							else if(json.getString("id").equals("viewpermissions")){
								File f = new File(json.getString("path"));
								String path = json.getString("path");
								if(!f.isDirectory()){
									path = f.getParent();
								}
								Connection conn = DriverManager.getConnection(LuceneConstants.url, LuceneConstants.username, LuceneConstants.password);
								Statement st = conn.createStatement();
								ResultSet rs = st.executeQuery("SELECT * FROM indexer.permissions WHERE folderpath = '" + path + "';");
								ResultSetMetaData metadata = rs.getMetaData();
								int columnCount = metadata.getColumnCount();
								rs.first();
								String html = "The following people have access to " + f.getName() + ":\n";
								for(int x = 3; x<=columnCount; x++){
									if(rs.getInt(columnCount) >=1){
										html += metadata.getColumnName(x) + "\n";
									}
								}
								os.println(html);
							}
							else if(json.getString("id").equals("share")){
								Connection conn = DriverManager.getConnection(LuceneConstants.url, LuceneConstants.username, LuceneConstants.password);
								Statement st = conn.createStatement();
								if(!new File(json.getString("path")).isDirectory()){
									os.println("You cannot share a file.");
								}
								else if(DirectoryReader.checkEditPermission(json.getString("path"), json.getString("email"))){
									String[] exclude = json.getString("exclude").split("\\s+");
									for(String e: exclude){
										ResultSet rs = st.executeQuery("SELECT * FROM indexer.account WHERE email = '" + e + "';");
										if(rs.next()){
											st.executeUpdate("UPDATE indexer.permissions SET `" + e + "` = 0 WHERE folderpath = '" + json.getString("path") + "';");
										}
									}
									String[] include = json.getString("include").split("\\s+");
									for(String e: include){
										ResultSet rs = st.executeQuery("SELECT * FROM indexer.account WHERE email = '" + e + "';");
										if(rs.next()){
										st.executeUpdate("UPDATE indexer.permissions SET `" + e + "` = 1 WHERE folderpath = '" + json.getString("path") + "';");
										}
									}
									st.executeUpdate("UPDATE indexer.permissions SET `" + email + "` = 2 WHERE folderpath = '" + json.getString("path") + "';");
									os.println("Permissions changed!");
								}
								else{
									os.println("You do not have permission to share this folder.");
								}
								conn.close();
							}
							else if(json.getString("id").equals("deleteFile")){
								email = json.getString("email");
								File file = new File (json.getString("filepath"));
								if(json.getString("email").equals("")){
									os.println("You do not have permission to delete this file.");
								}
								else if(DirectoryReader.checkEditPermission(file.getPath(), json.getString("email"))){
									if(!file.isDirectory() || (file.isDirectory()&&file.list().length == 0)){
										Path path = Paths.get(json.getString("filepath"));
										Files.deleteIfExists(path);
										Connection conn = DriverManager.getConnection(LuceneConstants.url, LuceneConstants.username, LuceneConstants.password);
										Statement st = conn.createStatement();
										if(file.isDirectory()){
											st.executeUpdate("DELETE FROM indexer.permissions WHERE folderpath = '" + path + "';");
										}
										else{
											Indexer.deleteIndex(file.getPath());
											st.executeUpdate("DELETE FROM indexer.files WHERE filepath = '" + path + "';");
										}
										String tree = DirectoryReader.listFilesForFolder(new File(LuceneConstants.dataDir), "<ul id=\"expList\">", email);
										os.println(tree);
									}
									else{
										os.println("You can only delete a folder if it is empty.");
									}
								}
								else{
									os.println("You do not have permission to delete this file.");
								}
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