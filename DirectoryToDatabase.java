import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DirectoryToDatabase will take a file path and add it to the MySQL database while updating 
 * permissions to it. The main method can be called if you would like to re-read the contents
 * into the SQL database
 * @author Gina
 *
 */
public class DirectoryToDatabase {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		readToSQL(Constants.dataDir);
		System.out.println("Database connected!");
	}

	/**
	 * Reads the directory into the MySQL database
	 * @param dir Path of the directory that needs to be read into mySQL
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void readToSQL(String dir) throws SQLException, ClassNotFoundException {
		//Connects to the database
		File[] files = new File(dir).listFiles();
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(Constants.url, Constants.username, Constants.password);
		Statement st = conn.createStatement();
		
		// Check for file type and call appropriate method to convert the file and add is to the database.
		for (File f : files) {
			if (f.isDirectory()) {
				ResultSet rs = st
						.executeQuery("SELECT * FROM indexer.permissions WHERE folderpath = '" + f.getPath() + "'");
				if (!rs.first()) {
					st.executeUpdate("INSERT INTO indexer.permissions (`folderpath`) VALUES ('" + f.getPath() + "');");
				}
				rs.close();
				readToSQL(f.getPath());
			} else {
				ResultSet rs = st.executeQuery("SELECT * FROM indexer.files WHERE filepath = '" + f.getPath() + "'");
				if (!rs.first()) {
					st.executeUpdate("INSERT INTO indexer.files (`filepath`) VALUES ('" + f.getPath() + "');");
				}
				rs.close();
			}
		}
		conn.close();
	}
}
