import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * DirecotryReader creates the inner HTML by reading through the data Directory file and creating a collapsible 
 * and expandable list. The directory email also talks to the MySQL database to check whether the user making the
 * request to view the file structure has permission to view it. Directory Reader will NOT show hidden files
 * such as DS_Store
 * @author Gina
 *
 */
public class DirectoryReader {
	/**
	 * Constructs the html of an unordered list to view in browser. Warning: the id of each list object
	 * is its literal path. For security reasons, ensure that they id cannot be viewed in Inspect Element.
	 * @param folder Folder path to be parsed and read
	 * @param html Beginning html
	 * @param email Email of the user
	 * @return HTML of an unordered list with the file structure
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static String listFilesForFolder(File folder, String html, String email)
			throws ClassNotFoundException, SQLException {
		
		for (File fileEntry : folder.listFiles()) {
			/*Checks whether the file is located in the public directory or if the person has permission to
			 * view the file.
			 */

			if ((fileEntry.toString().startsWith(Constants.dataDir + "/public")) || (fileEntry.isDirectory()
					&& checkPermission(fileEntry.getPath(), email) && !fileEntry.isHidden())) {
				/*If the file is a folder, it will check whether there are sub-directories and adjust the 
				 * html accordingly.
				 */
				if (fileEntry.isDirectory() && !fileEntry.isHidden()) {
					int count = 0;
					for (File file : fileEntry.listFiles()) {
						if (file.isDirectory()) {
							count++;
						}
					}
					html += "<ul>";
					if (count == 0) {
						html += "<li class=\"folder\"><span onclick=\"triggerSelect(this.id)\" id= \""
								+ fileEntry.getPath() + "\">" + fileEntry.getName() + "</span>";
					} else {
						html += "<li>" + "<span onclick=\"triggerSelect(this.id)\" id= \"" + fileEntry.getPath() + "\">"
								+ fileEntry.getName() + "</span>";
					}
					html = listFilesForFolder(fileEntry, html, email);
					html += "</li>";
					html += "</ul>";
				}
				/*If the file is not a folder and is not hidden, it will check file type and 
				 * determine the appropriate image to place in the file structure.
				 */
				else if (!fileEntry.isHidden()) {
					if (FileNames.getExtension(fileEntry.toString()).equals("mp3|aac|mpeg|aiff|wav|mpg")) {
						html += "<li class =\"audio\"><span onclick=\"triggerSelect(this.id)\" id= \""
								+ fileEntry.getPath() + "\">" + fileEntry.getName() + "</span></li>";
					} else if (FileNames.getExtension(fileEntry.toString()).equals("rar|zip")) {
						html += "<li class =\"compressed\"><span onclick=\"triggerSelect(this.id)\" id= \""
								+ fileEntry.getPath() + "\">" + fileEntry.getName() + "</span></li>";
					} else if (FileNames.getExtension(fileEntry.toString()).equals("jpg|jpeg|png|gif")) {
						html += "<li class =\"img\"><span onclick=\"triggerSelect(this.id)\" id= \""
								+ fileEntry.getPath() + "\">" + fileEntry.getName() + "</span></li>";
					} else if (FileNames.getExtension(fileEntry.toString()).equals("mp4|mv|mpg|mpeg|mpeg2|avi")) {
						html += "<li class =\"video\"><span onclick=\"triggerSelect(this.id)\" id= \""
								+ fileEntry.getPath() + "\">" + fileEntry.getName() + "</span></li>";
					} else {
						html += "<li class =\"file\"><span onclick=\"triggerSelect(this.id)\" id= \""
								+ fileEntry.getPath() + "\">" + fileEntry.getName() + "</span></li>";
					}
				}
			} 
			/*
			 * If the user does not have access to the folder, but could potentially have access to
			 * other folders, it will check and print the appropriate html. The user will not be able to view
			 * the folder that they do not have access to or its contents unless there is a folder that
			 * they can view. In that case, they can view that folder and its contents.
			 */
			else if (fileEntry.isDirectory() && !fileEntry.isHidden()) {
				for (File file : fileEntry.listFiles()) {
					if (file.isDirectory() && checkPermission(file.getPath(), email)) {
						int count = 0;
						for (File f : file.listFiles()) {
							if (f.isDirectory()) {
								count++;
							}
						}
						html += "<ul>";
						if (count == 0) {
							html += "<li class=\"folder\">" + "<span onclick=\"triggerSelect(this.id)\" id= \""
									+ file.getPath() + "\">" + file.getName() + "</span>";
						} else {
							html += "<li>" + "<span onclick=\"triggerSelect(this.id)\" id= \"" + file.getPath() + "\">"
									+ file.getName() + "</span>";
						}

						html = listFilesForFolder(file, html, email);
						html += "</li>";
						html += "</ul>";
					}
				}
			}
		}
		return html;
	}

	/**
	 * Checks whether the person with the affiliated email has access to view the folder
	 * @param path Path of the directory they are attempting to view
	 * @param email Email of the person logged-in
	 * @return boolean true/false
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static boolean checkPermission(String path, String email) throws ClassNotFoundException, SQLException {
		if (path.startsWith(Constants.dataDir + "/public")) {
			return true;
		} else if (!email.isEmpty()) {
			//Connects to the database
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(Constants.url, Constants.username, Constants.password);
			Statement st = conn.createStatement();
			
			//Checks whether the user has permission to view the folder
			ResultSet rs = st.executeQuery("SELECT `" + email + "` FROM indexer.permissions WHERE `folderpath` = '" + path + "';");
			rs.absolute(1);
			if (rs.absolute(1) && rs.getInt(email) >= 1) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	/**
	 * Checks whether the person with the affiliated email has access to edit the folder or file. Only
	 * owners of the folder can edit permissions.
	 * @param path Path of the directory they are attempting to change permissions to
	 * @param email Email of the person logged-in
	 * @return boolean true/false
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */

	public static boolean checkEditPermission(String path, String email) throws ClassNotFoundException, SQLException {
		if (email.equals("")) {
			return false;
		} 
		else {
			//connects to the database
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(Constants.url, Constants.username, Constants.password);
			Statement st = conn.createStatement();
			File f = new File(path);
			ResultSet rs;
			
			//Checks whether the user is the owner
			if (f.isDirectory()) {
				rs = st.executeQuery("SELECT `" + email + "` FROM indexer.permissions WHERE `folderpath` = '" + path + "';");
				rs.absolute(1);
				if (rs.absolute(1) && rs.getInt(email) == 2) {
					return true;
				} else {
					return false;
				}
			} 
			//Checks whether the user is the owner of the files
			else {
				rs = st.executeQuery("SELECT `owner` FROM indexer.files WHERE `filepath` = '" + path + "';");
				rs.absolute(1);
				String owner = rs.getString("owner");
				if (owner != null) {
					if (rs.absolute(1) && rs.getString("owner").equals(email)) {
						return true;
					} else {
						return false;
					}
				}
				return false;
			}
		}
	}
}