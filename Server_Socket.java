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

/**
 * The Server Socket allows the Java script to talk to the Node.js. The socket requires a valid port to open up.
 * To complete the socket request, make sure to run the node file. The server on the Java side recognizes the 
 * request based on the "id" given in the JSON object on the Node.js side.
 * @author Gina
 *
 */
public class Server_Socket {

	private static int port = 1221;
	public static ServerSocket ssock = null;
	public static String line;
	public static DataInputStream is;
	public static PrintStream os;
	public static Date lastcheck = new Date();
	static Indexer indexer = null;
	static String html = "<ul id=\"expList\">";

	/**
	 * Whenever the server socket is run, it will remain open unless the connection is interrupted or the 
	 * Node.js sends back "bye"
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Class.forName("com.mysql.jdbc.Driver");
		try {
			ssock = new ServerSocket(port);
			System.out.println("SockServer waiting for connections on 1221...");
			while (true) {
				try {
					// Creates socket and input/output streams
					Socket csock = ssock.accept();
					System.out.println("incoming...");
					is = new DataInputStream(csock.getInputStream());
					BufferedReader lines = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					os = new PrintStream(csock.getOutputStream());
					ArrayList<String> stuff = new ArrayList<String>();
					String message = "";
					String email = "";
					
					// Listen for message from node.js
					while (true) {
						
						// Tries to read message
						line = lines.readLine();
						System.out.println(line);
						JSONObject json = new JSONObject(line);
						
						// If there is a message, check what kind of message
						if (!line.isEmpty()) {
							
							// Search
							if (json.getString("id").equals("search")) {
								email = json.getString("email");
								try {
									// Call Searcher class to search for the string
									Searcher s = new Searcher();
									stuff = s.searchIndex(json, Constants.indexDir, json.getString("num"), email);
								} catch (Exception e) {
									e.printStackTrace();
								}
								message = "Search: " + json.getString("searchterm") + "<br>";
								for (String st : stuff) {
									message = message + st + "<br>";
								}
								// Send results back to node.js
								os.println(message);
								line = "";
							}
							// Adds user to database
							else if (json.getString("id").equals("signIn")) {
								Connection conn = DriverManager.getConnection(Constants.url, Constants.username, Constants.password);
								String idToken = json.getString("idtoken");
								GoogleIdToken.Payload payLoad = IdTokenVerifierAndParser.getPayload(idToken);

								String userName = (String) payLoad.get("name");
								email = payLoad.getEmail();
								
								// if it is a senscio email, save into database
								if (email.length() > 19
										&& email.substring(email.length() - 19).equals("@sensciosystems.com")) {
									try {
										Statement st = conn.createStatement();
										ResultSet rs = st.executeQuery(
												"SELECT * FROM indexer.account WHERE email = '" + email + "';");
										if (!rs.first()) {
											st.executeUpdate("ALTER TABLE indexer.permissions ADD `" + email
													+ "` TINYINT(1) NOT NULL DEFAULT 0;");
											st.executeUpdate("INSERT INTO indexer.account (email, name) VALUES ('"
													+ email + "', '" + userName + "');");
											;
											st.executeUpdate("UPDATE indexer.permissions SET `" + email
													+ "` = 1 WHERE folderpath LIKE '" + Constants.dataDir
													+ "/Public%';");
											os.println("done");
										}
										rs.close();

									} catch (SQLException e) {
										throw new IllegalStateException("Cannot connect the database!", e);
									}
								} else {
									os.print("Not a Senscio email.");
								}
								conn.close();
							}
							// if there was a file upload
							else if (json.getString("id").equals("upload")) {
								JSONArray filepaths = json.getJSONArray("filepaths");
								email = json.getString("email");
								String pathnew = json.getString("path_new");
								for (int x = 0; x < filepaths.length(); x++) {
									/*For each file, check for duplicates, then move to destination 
									 * folder and add to index
									 */
									String filepath = filepaths.getString(x);
									File file = new File(filepath);
									String filename = FileNames.duplicateCheck(file.getName(), pathnew);
									Files.move(Paths.get(filepath), Paths.get(pathnew + "/" + filename),
											StandardCopyOption.REPLACE_EXISTING);
									UpdateIndex.updateIndex(pathnew + "/" + filename, json.getString("terms"),
											Constants.indexDir);
									Connection conn = DriverManager.getConnection(Constants.url,
											Constants.username, Constants.password);
									Statement st = conn.createStatement();
									st.executeUpdate("INSERT INTO indexer.files (filepath, owner) VALUES ('" + filepath
											+ "', '" + email + "');");
								}
								os.println("Indexed!");
							}
							// Creates tree according to permissions
							else if (json.getString("id").equals("tree")) {
								email = json.getString("email");
								File file = new File(Constants.dataDir);
								// writes new html tree
								String tree = DirectoryReader.listFilesForFolder(file, html, email);
								// return to nodejs
								os.println(tree);
							}
							// Downloads file
							else if (json.getString("id").equals("download")) {
								String file = json.getString("path");
								StringBuffer sBuffer = new StringBuffer(file);
								String str= sBuffer.toString().replace(Constants.dataDir, "/files");
								os.println(str);
							}
							// Creates folder and adds to database
							else if (json.getString("id").equals("addFolder")) {
								String path = json.getString("filepaths");
								if (!new File(path).isDirectory()) {
									os.println("You cannot create a folder under a file.");
								} else {
									path += "/" + json.getString("name");
									email = json.getString("email");
									if (path.equals("")) {
										path = Constants.dataDir + "/" + json.getString("name");
									}
									// replace spaces with underscores
									for (int j = 0; j < path.length(); j++) {
										char c = path.charAt(j);
										if (c == ' ') {
											path = path.replace(c, '_');
										}
									}
									Path dir = Paths.get(path);
									// Creates folder
									dir = Files.createDirectory(dir);
									// Redo tree so that user can see new folder
									Connection conn = DriverManager.getConnection(Constants.url,
											Constants.username, Constants.password);
									Statement st = conn.createStatement();
									st.executeUpdate(
											"INSERT INTO indexer.permissions (`folderpath`) VALUES ('" + path + "');");
									String permissions = json.getString("permissions");
									if (!permissions.equals("")) {
										String[] per = permissions.split("\\s+");
										for (String e : per) {
											st.executeUpdate("UPDATE indexer.permissions SET `" + e
													+ "` = 1 WHERE folderpath = '" + path + "';");
										}
									}
									st.executeUpdate("UPDATE indexer.permissions SET `" + email
											+ "` = 2 WHERE folderpath = '" + path + "';");
									String tree = DirectoryReader.listFilesForFolder(new File(Constants.dataDir),
											"<ul id=\"expList\">", email);
									os.println(tree);
									conn.close();
								}
							}
							// Returns people with access to a file or folder
							else if (json.getString("id").equals("viewpermissions")) {
								File f = new File(json.getString("path"));
								String path = json.getString("path");
								String html = "";
								if(path.toLowerCase().contains((Constants.dataDir + "/public").toLowerCase())){
									 html += "Everyone has access";
								}
								else{
								if (!f.isDirectory()) {
									path = f.getParent();
								}
								Connection conn = DriverManager.getConnection(Constants.url,
										Constants.username, Constants.password);
								Statement st = conn.createStatement();
								ResultSet rs = st.executeQuery(
										"SELECT * FROM indexer.permissions WHERE folderpath = '" + path + "';");
								ResultSetMetaData metadata = rs.getMetaData();
								int columnCount = metadata.getColumnCount();
								rs.first();
								html += "The following people have access to " + f.getName() + ":\n";
								for (int x = 3; x <= columnCount; x++) {
									if (rs.getInt(columnCount) >= 1) {
										html += metadata.getColumnName(x) + "\n";
									}
								}
								}
								os.println(html);
							}
							// Edit permissions of folders
							else if (json.getString("id").equals("share")) {
								Connection conn = DriverManager.getConnection(Constants.url,
										Constants.username, Constants.password);
								Statement st = conn.createStatement();
								if (!new File(json.getString("path")).isDirectory()) {
									os.println("You cannot share a file.");
								} else if (DirectoryReader.checkEditPermission(json.getString("path"),
										json.getString("email"))) {
									String[] exclude = json.getString("exclude").split("\\s+");
									for (String e : exclude) {
										ResultSet rs = st.executeQuery(
												"SELECT * FROM indexer.account WHERE email = '" + e + "';");
										if (rs.next()) {
											st.executeUpdate("UPDATE indexer.permissions SET `" + e
													+ "` = 0 WHERE folderpath = '" + json.getString("path") + "';");
										}
									}
									String[] include = json.getString("include").split("\\s+");
									for (String e : include) {
										ResultSet rs = st.executeQuery(
												"SELECT * FROM indexer.account WHERE email = '" + e + "';");
										if (rs.next()) {
											st.executeUpdate("UPDATE indexer.permissions SET `" + e
													+ "` = 1 WHERE folderpath = '" + json.getString("path") + "';");
										}
									}
									st.executeUpdate("UPDATE indexer.permissions SET `" + email
											+ "` = 2 WHERE folderpath = '" + json.getString("path") + "';");
									os.println("Permissions changed!");
								} else {
									os.println("You do not have permission to share this folder.");
								}
								conn.close();
							}
							// Delete file if user has permission
							else if (json.getString("id").equals("deleteFile")) {
								email = json.getString("email");
								File file = new File(json.getString("filepath"));
								if (json.getString("email").equals("")) {
									os.println("You do not have permission to delete this file.");
								} else if (DirectoryReader.checkEditPermission(file.getPath(),
										json.getString("email"))) {
									if (!file.isDirectory() || (file.isDirectory() && file.list().length == 0)) {
										Path path = Paths.get(json.getString("filepath"));
										Files.deleteIfExists(path);
										Connection conn = DriverManager.getConnection(Constants.url,
												Constants.username, Constants.password);
										Statement st = conn.createStatement();
										if (file.isDirectory()) {
											st.executeUpdate("DELETE FROM indexer.permissions WHERE folderpath = '"
													+ path + "';");
										} else {
											Indexer.deleteIndex(file.getPath());
											st.executeUpdate(
													"DELETE FROM indexer.files WHERE filepath = '" + path + "';");
										}
										String tree = DirectoryReader.listFilesForFolder(
												new File(Constants.dataDir), "<ul id=\"expList\">", email);
										os.println(tree);
									} else {
										os.println("You can only delete a folder if it is empty.");
									}
								} else {
									os.println("You do not have permission to delete this file.");
								}
							}
						}
						if (line.equals("bye")) {
							csock.close();
							break;
						}
					}
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

}