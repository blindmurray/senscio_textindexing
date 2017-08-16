import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DirectoryReader {
	public static void main(String[] args) throws Exception {
		String html = "<ul id=\"expList\">";
		html = listFilesForFolder(new File(LuceneConstants.dataDir), html, "michelle@sensciosystems.com");
		System.out.println(html);
	}
	//constructs html ul from reading folder directory
	public static String listFilesForFolder(File folder, String html, String email) throws ClassNotFoundException, SQLException {
		for (File fileEntry : folder.listFiles()) {
			if(fileEntry.isDirectory()){
				System.out.print(fileEntry.getName() + ((fileEntry.toString().startsWith(LuceneConstants.dataDir + "/public"))));
				System.out.println(checkPermission(fileEntry.getPath(), email) && !fileEntry.isHidden());
			}
			if((fileEntry.toString().startsWith(LuceneConstants.dataDir + "/public"))||(fileEntry.isDirectory() && checkPermission(fileEntry.getPath(), email) && !fileEntry.isHidden())){
				if (fileEntry.isDirectory() && !fileEntry.isHidden()) {
					int count = 0;
					for(File file : fileEntry.listFiles()){
						if(file.isDirectory()){
							count++;
						}
					}
					if(count==0){
						html += "<li class=\"folder\">" + "<span onclick=\"triggerSelect(this.id)\" id= \"" + fileEntry.getPath() + "\">" + fileEntry.getName() + "</span>";
					}
					else{
						html += "<li>" + "<span onclick=\"triggerSelect(this.id)\" id= \"" + fileEntry.getPath() + "\">" + fileEntry.getName() + "</span>";
					}
					html += "<ul>";
					html = listFilesForFolder(fileEntry, html, email);
					html+= "</li>";
					html += "</ul>";
				}
				
				else if(!fileEntry.isHidden()){
					html+= "<li class =\"file\">" +  fileEntry.getName() + "</li>";
				}
				
			}
			else if(fileEntry.isDirectory() && !fileEntry.isHidden()){
				for(File file : fileEntry.listFiles()){
					if(file.isDirectory()){
						System.out.println("hello here" + file.getName());
						html = listFilesForFolder(file, html, email);
					}
				}
			}
		}
		return html;
	}
	public static boolean checkPermission(String path, String email) throws ClassNotFoundException, SQLException{
		if(path.startsWith(LuceneConstants.dataDir + "/public")){
			return true;
		}
		else{
			String url = "jdbc:mysql://10.0.55.100:3306/";
			String username = "ibisua";
			String password = "ibisua";
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(url, username, password);
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT `" + email + "` FROM indexer.permissions WHERE `folderpath` = '" + path + "'");
			rs.absolute(1);
			System.out.println("SELECT `" + email + "` FROM indexer.permissions WHERE `folderpath` = '" + path + "': " + rs.getInt(email));
			if(rs.absolute(1) && rs.getInt(email)>=1){
				return true;
			}
			else{
				return false;
			}
		}
	}
}