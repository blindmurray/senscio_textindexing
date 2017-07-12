import java.io.File;

public class DirectoryReader {
	static String html = "";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File folder = new File("/Users/Gina/Documents/OneDrive/txt_data");
		html += "<ul id=\"expList\">";
		listFilesForFolder(folder);
		System.out.println(html);
	}

	public static void listFilesForFolder(File folder) {
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				html += "<li>" + fileEntry.getName();
				html += "<ul>";
				listFilesForFolder(fileEntry);
				html+= "</li>";
			} 
		}
		html += "</ul>";
	}
}