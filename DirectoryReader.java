import java.io.File;
import java.util.Date;

public class DirectoryReader {
	static String html = "";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File folder = new File("/Users/Gina/Documents/OneDrive/txt_data");
		html += "<ul id=\"expList\">";
		listFilesForFolder(folder, html);
		System.out.println(html);
	}

	public static void listFilesForFolder(File folder, String html) {
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				int count = 0;
				for(File files :fileEntry.listFiles()){
					if(files.isDirectory()){
						count++;
					}
				}
				if(count>0){
				html += "<li>" + fileEntry.getName();
				html += "<ul>";
				listFilesForFolder(fileEntry, html);
				html+= "</li>";
				}
				else{
					html += "<li>" + fileEntry.getName() + "</li>";
				}
			} 
		}
		html += "</ul>";
	}

	public static Date directoryChangeCheck(File f, Date lastcheck){
		Date modDate = new Date(f.lastModified());
		if(modDate.after(lastcheck)){
			String html = "<ul id=\"expList\">";
			listFilesForFolder(f, html);
			System.out.println(html);
			return new Date();
		}
		for (File fileEntry : f.listFiles()) {
			if (fileEntry.isDirectory()) {
				directoryChangeCheck(fileEntry, lastcheck);
			} 
		}
		return new Date();
	}
}