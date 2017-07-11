import java.io.File;

public class DirectoryReader {
	static String html = "";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File folder = new File("C:/MICHELLE/txtfiles");
		listFilesForFolder(folder);
		html += "";
		System.out.println(html);
	}

	public static void listFilesForFolder(File folder) {
		html += "<ul>";
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				html += "<li>" + fileEntry.getName();
				listFilesForFolder(fileEntry);
				html+= "</li>";
			} 
		}
		html += "</ul>";
	}
}