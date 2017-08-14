import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
public class FixDirectory {

	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		readToSQL(LuceneConstants.dataDir);
	}
		public static String space(String dir){
			File[] files = new File(dir).listFiles();
				
			//Check for file type and call appropriate method to convert the file.
			for (int i = 0; i < files.length; i++) {
					File f = files[i];
		        	if (f.isDirectory()){
		        		String s = f.toString();
		        		for (int j = 0; j < s.length(); j++){
		        		    char c = s.charAt(j);        
		        		    if(c == ' '){
		        		    	s = s.replace(c, '_');
		        		    }
		        		}
		        	File f1 = new File (s);
		        	f.renameTo(f1);
		        	System.out.println(f.toString());
		        		space(f.toString());
		        	}
		        	
		        	else{
			        		String s = f.toString();
			        		for (int j = 0; j < s.length(); j++){
			        		    char c = s.charAt(j);        
			        		    if(c == ' '){
			        		    	s = s.replace(c, '_');
			        		    }
			        		}
			        	File f1 = new File (s);
			        	f.renameTo(f1);
			        	System.out.println(f.toString());
			        	
		        	}
			}
		return null;
		}
		public static String readToSQL(String dir) throws SQLException, ClassNotFoundException{
			File[] files = new File(dir).listFiles();
			String url = "jdbc:mysql://10.0.55.100:3306/";
	        String username = "ibisua";
	        String password = "ibisua";
	        Connection conn = DriverManager.getConnection(url, username, password);
        	System.out.println("Database connected!");
            Statement st = conn.createStatement();
	    
			Class.forName("com.mysql.jdbc.Driver");

		
			//Check for file type and call appropriate method to convert the file.
			for (int i = 0; i < files.length; i++) {
					File f = files[i];
		        	if (f.isDirectory()){
		        		st.executeUpdate("INSERT INTO indexer.permissions (`folderpath`) VALUES ('" + f.getPath() + "');");
		        		if(f.getName().equals("Public")){
		        			st.executeUpdate("");
		        		}
		        		readToSQL(f.getPath());
		        	}
			}
			conn.close();
		return null;
		}
}
