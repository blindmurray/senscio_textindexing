import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FixDirectory {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		readToSQL(LuceneConstants.dataDir);
		System.out.println("Database connected!");
	}

	public static String space(String dir) {
		File[] files = new File(dir).listFiles();

		// Check for file type and call appropriate method to convert the file.
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				String s = f.toString();
				for (int j = 0; j < s.length(); j++) {
					char c = s.charAt(j);
					if (c == ' ') {
						s = s.replace(c, '_');
					}
				}
				File f1 = new File(s);
				f.renameTo(f1);
				System.out.println(f.toString());
				space(f.toString());
			}

			else {
				String s = f.toString();
				for (int j = 0; j < s.length(); j++) {
					char c = s.charAt(j);
					if (c == ' ') {
						s = s.replace(c, '_');
					}
				}
				File f1 = new File(s);
				f.renameTo(f1);
				System.out.println(f.toString());
			}
		}
		return null;
	}

	public static String readToSQL(String dir) throws SQLException, ClassNotFoundException {
		File[] files = new File(dir).listFiles();
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(LuceneConstants.url, LuceneConstants.username,
				LuceneConstants.password);
		Statement st = conn.createStatement();
		// Check for file type and call appropriate method to convert the file.
		for (File f : files) {
			System.out.println(f.getName());
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
		return null;
	}
}
