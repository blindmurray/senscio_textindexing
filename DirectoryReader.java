import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DirectoryReader {
	// constructs html ul from reading folder directory
	public static String listFilesForFolder(File folder, String html, String email)
			throws ClassNotFoundException, SQLException {
		for (File fileEntry : folder.listFiles()) {
			System.out.println(fileEntry.getName() + fileEntry.isDirectory());
			if (fileEntry.isDirectory()) {
				System.out.print(((fileEntry.toString().startsWith(LuceneConstants.dataDir + "/public"))));
				System.out.println(checkPermission(fileEntry.getPath(), email) && !fileEntry.isHidden());
			}
			if ((fileEntry.toString().startsWith(LuceneConstants.dataDir + "/public")) || (fileEntry.isDirectory()
					&& checkPermission(fileEntry.getPath(), email) && !fileEntry.isHidden())) {
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

				else if (!fileEntry.isHidden()) {
					if (TXT.getExtension(fileEntry.toString()).equals("mp3|aac|mpeg|aiff|wav|mpg")) {
						html += "<li class =\"audio\"><span onclick=\"triggerSelect(this.id)\" id= \""
								+ fileEntry.getPath() + "\">" + fileEntry.getName() + "</span></li>";
					} else if (TXT.getExtension(fileEntry.toString()).equals("rar|zip")) {
						html += "<li class =\"compressed\"><span onclick=\"triggerSelect(this.id)\" id= \""
								+ fileEntry.getPath() + "\">" + fileEntry.getName() + "</span></li>";
					} else if (TXT.getExtension(fileEntry.toString()).equals("jpg|jpeg|png|gif")) {
						html += "<li class =\"img\"><span onclick=\"triggerSelect(this.id)\" id= \""
								+ fileEntry.getPath() + "\">" + fileEntry.getName() + "</span></li>";
					} else if (TXT.getExtension(fileEntry.toString()).equals("mp4|mv|mpg|mpeg|mpeg2|avi")) {
						html += "<li class =\"video\"><span onclick=\"triggerSelect(this.id)\" id= \""
								+ fileEntry.getPath() + "\">" + fileEntry.getName() + "</span></li>";
					} else {
						html += "<li class =\"file\"><span onclick=\"triggerSelect(this.id)\" id= \""
								+ fileEntry.getPath() + "\">" + fileEntry.getName() + "</span></li>";
					}
				}
			} else if (fileEntry.isDirectory() && !fileEntry.isHidden()) {
				for (File file : fileEntry.listFiles()) {
					if (file.isDirectory() && checkPermission(file.getPath(), email)) {
						System.out.println("hello here" + file.getName());
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
	
	public static String listFilesForFolderR(File folder, String html, String email)
			throws ClassNotFoundException, SQLException {
		for (File fileEntry : folder.listFiles()) {
			System.out.println(fileEntry.getName() + fileEntry.isDirectory());
			if (fileEntry.isDirectory()) {
				System.out.print(((fileEntry.toString().startsWith(LuceneConstants.dataDir + "/public"))));
				System.out.println(checkPermission(fileEntry.getPath(), email) && !fileEntry.isHidden());
			}
			if ((fileEntry.toString().startsWith(LuceneConstants.dataDir + "/public")) || (fileEntry.isDirectory()
					&& checkPermission(fileEntry.getPath(), email) && !fileEntry.isHidden())) {
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

					html = listFilesForFolderR(fileEntry, html, email);
					html += "</li>";
					html += "</ul>";
				}

				else if (!fileEntry.isHidden()) {
					if (TXT.getExtension(fileEntry.toString()).equals("mp3|aac|mpeg|aiff|wav|mpg")) {
						html += "<li class =\"audio\"><span onclick=\"triggerSelect(this.id)\" id= \""
								+ fileEntry.getPath() + "\">" + fileEntry.getName() + "</span></li>";
					} else if (TXT.getExtension(fileEntry.toString()).equals("rar|zip")) {
						html += "<li class =\"compressed\"><span onclick=\"triggerSelect(this.id)\" id= \""
								+ fileEntry.getPath() + "\">" + fileEntry.getName() + "</span></li>";
					} else if (TXT.getExtension(fileEntry.toString()).equals("jpg|jpeg|png|gif")) {
						html += "<li class =\"img\"><span onclick=\"triggerSelect(this.id)\" id= \""
								+ fileEntry.getPath() + "\">" + fileEntry.getName() + "</span></li>";
					} else if (TXT.getExtension(fileEntry.toString()).equals("mp4|mv|mpg|mpeg|mpeg2|avi")) {
						html += "<li class =\"video\"><span onclick=\"triggerSelect(this.id)\" id= \""
								+ fileEntry.getPath() + "\">" + fileEntry.getName() + "</span></li>";
					} else {
						html += "<li class =\"file\"><span onclick=\"triggerSelect(this.id)\" id= \""
								+ fileEntry.getPath() + "\">" + fileEntry.getName() + "</span></li>";
					}
				}
			} else if (fileEntry.isDirectory() && !fileEntry.isHidden()) {
				for (File file : fileEntry.listFiles()) {
					if (file.isDirectory() && checkPermission(file.getPath(), email)) {
						System.out.println("hello here" + file.getName());
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

						html = listFilesForFolderR(file, html, email);
						html += "</li>";
						html += "</ul>";
					}
				}
			}
		}
		return html;
	}

	public static boolean checkPermission(String path, String email) throws ClassNotFoundException, SQLException {
		if (path.startsWith(LuceneConstants.dataDir + "/public")) {
			return true;
		} else if (!email.isEmpty()) {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(LuceneConstants.url, LuceneConstants.username,
					LuceneConstants.password);
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(
					"SELECT `" + email + "` FROM indexer.permissions WHERE `folderpath` = '" + path + "'");
			if (rs.absolute(1) && rs.getInt(email) >= 1) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static boolean checkEditPermission(String path, String email) throws ClassNotFoundException, SQLException {
		if (email.equals("")) {
			return false;
		} else {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(LuceneConstants.url, LuceneConstants.username,
					LuceneConstants.password);
			Statement st = conn.createStatement();
			File f = new File(path);
			ResultSet rs;
			if (f.isDirectory()) {
				rs = st.executeQuery(
						"SELECT `" + email + "` FROM indexer.permissions WHERE `folderpath` = '" + path + "';");
				rs.absolute(1);
				System.out.println(rs.getInt(email));
				if (rs.absolute(1) && rs.getInt(email) == 2) {
					return true;
				} else {
					return false;
				}
			} else {
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