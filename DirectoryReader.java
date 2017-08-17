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
			System.out.println(fileEntry.getName() + fileEntry.isDirectory());
			if(fileEntry.isDirectory()){
				System.out.print(((fileEntry.toString().startsWith(LuceneConstants.dataDir + "/public"))));
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
					html += "<ul>";
					if(count==0){
						html += "<li class=\"folder\"><span onclick=\"triggerSelect(this.id)\" id= \"" + fileEntry.getPath() + "\">" + fileEntry.getName() + "</span>";
					}
					else{
						html += "<li>" + "<span onclick=\"triggerSelect(this.id)\" id= \"" + fileEntry.getPath() + "\">" + fileEntry.getName() + "</span>";
					}
					
					html = listFilesForFolder(fileEntry, html, email);
					html+= "</li>";
					html += "</ul>";
				}
				
				else if(!fileEntry.isHidden()){
					html+= "<li class =\"file\"><span onclick=\"triggerSelect(this.id)\" id= \"" + fileEntry.getPath() + "\">" + fileEntry.getName() + "</span></li>";
				}
				
			}
			else if(fileEntry.isDirectory() && !fileEntry.isHidden()){
				for(File file : fileEntry.listFiles()){
					if(file.isDirectory()&&checkPermission(file.getPath(), email)){
						System.out.println("hello here" + file.getName());
						int count = 0;
						for(File f : file.listFiles()){
							if(f.isDirectory()){
								count++;
							}
						}
						html += "<ul>";
						if(count==0){
							html += "<li class=\"folder\">" + "<span onclick=\"triggerSelect(this.id)\" id= \"" + file.getPath() + "\">" + file.getName() + "</span>";
						}
						else{
							html += "<li>" + "<span onclick=\"triggerSelect(this.id)\" id= \"" + file.getPath() + "\">" + file.getName() + "</span>";
						}
						
						html = listFilesForFolder(file, html, email);
						html+= "</li>";
						html += "</ul>";
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
		else if(!email.isEmpty()){
			String url = "jdbc:mysql://10.0.55.100:3306/";
			String username = "ibisua";
			String password = "ibisua";
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(url, username, password);
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT `" + email + "` FROM indexer.permissions WHERE `folderpath` = '" + path + "'");
			if(rs.absolute(1) && rs.getInt(email)>=1){
				return true;
			}
			else{
				return false;
			}
		}
		return false;
	}
	public static boolean checkEditPermission(String path, String email) throws ClassNotFoundException, SQLException{
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
			File f = new File(path);
			ResultSet rs;
			if(f.isDirectory()){
				rs = st.executeQuery("SELECT `" + email + "` FROM indexer.permissions WHERE `folderpath` = '" + path + "'");
				if(rs.absolute(1) && rs.getInt(email)>=2){
					return true;
				}
				else{
					return false;
				}
			}
			else{
				rs = st.executeQuery("SELECT `" + email + "` FROM indexer.files WHERE `filepath` = '" + path + "'");
				if(rs.absolute(1) && rs.getString("owner").equals(email)){
					return true;
				}
				else{
					return false;
				}
			}
			
		}
	}
}