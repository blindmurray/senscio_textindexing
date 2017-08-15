import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DirectoryReader {
	//constructs html ul from reading folder directory
	public static String listFilesForFolder(File folder, String html, String email) throws ClassNotFoundException, SQLException {

		for (File fileEntry : folder.listFiles()) {
		if(email == null && fileEntry.toString().startsWith(LuceneConstants.dataDir + "/public")){
				if (fileEntry.isDirectory() && !fileEntry.isHidden()) {
					int count = 0;
					for(File file : fileEntry.listFiles()){
						if(file.isDirectory()){
							count++;
						}
					}
						if(count==0){
							html += "<li class=\"folder\">" + "<span onclick=\"triggerSelect(this.id)\" id= \"" + fileEntry.getPath() + "\">" + fileEntry.getName() + "</span>";
							html += "<ul>";
							html = listFilesForFolder(fileEntry, html, email);
							html+= "</li>";
						}
						else{
							html += "<li>" + "<span onclick=\"triggerSelect(this.id)\" id= \"" + fileEntry.getPath() + "\">" + fileEntry.getName() + "</span>";
							html += "<ul>";
							html = listFilesForFolder(fileEntry, html, email);
							html+= "</li>";
						}
				}
				else if(!fileEntry.isHidden()){
					html+= "<li class =\"file\">" +  fileEntry.getName() + "</li>";
					}
		}
		else{
			if(checkPermission(fileEntry.toString(), email) && fileEntry.isDirectory() && !fileEntry.isHidden()){
				if (fileEntry.isDirectory() && !fileEntry.isHidden()) {
					int count = 0;
					for(File file : fileEntry.listFiles()){
						if(file.isDirectory()){
							count++;
						}
					}
						if(count==0){
							html += "<li class=\"folder\">" + "<span onclick=\"triggerSelect(this.id)\" id= \"" + fileEntry.getPath() + "\">" + fileEntry.getName() + "</span>";
							html += "<ul>";
							html = listFilesForFolder(fileEntry, html, email);
							html+= "</li>";
						}
						else{
							html += "<li>" + "<span onclick=\"triggerSelect(this.id)\" id= \"" + fileEntry.getPath() + "\">" + fileEntry.getName() + "</span>";
							html += "<ul>";
							html = listFilesForFolder(fileEntry, html, email);
							html+= "</li>";
						}
				}
				else if(!fileEntry.isHidden()){
					html+= "<li class =\"file\">" +  fileEntry.getName() + "</li>";
					}
				}
			}
		}
		html += "</ul>";
		return html;
	}

	public static boolean checkPermission(String path, String email) throws ClassNotFoundException, SQLException{
		if(path.startsWith(LuceneConstants.dataDir + "/Public")){
			return true;
		}
		else{
			String url = "jdbc:mysql://10.0.55.100:3306/";
	        String username = "ibisua";
	        String password = "ibisua";
	        Class.forName("com.mysql.jdbc.Driver");
	        Connection conn = DriverManager.getConnection(url, username, password);
	        System.out.println("Connecting database...");
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT " + email + " FROM indexer.permissions WHERE folderpath = '" + path + "'");
			if(rs.getInt(1)==1){
				return true;
			}
			else{
				return false;
			}
		}
	}
}