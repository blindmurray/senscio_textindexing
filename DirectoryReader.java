import java.io.File;
import java.util.Date;

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
				int count = 0;
				for(File files :fileEntry.listFiles()){
					if(files.isDirectory()){
						count++;
					}
				}
				if(count>0){
				html += "<li>" + "<span>" + fileEntry.getName() + "</span>";
				html += "<ul>";
				listFilesForFolder(fileEntry);
				html+= "</li>";
				}
				else{
					html += "<li>" + "<span>" + fileEntry.getName() + "</span>"+ "</li>";
				}
			} 
		}
		html += "</ul>";
	}

	public static Date directoryChangeCheck(File f, Date lastcheck){
		Date modDate = new Date(f.lastModified());
		if(modDate.after(lastcheck)){
			String html = "<ul id=\"expList\">";
			listFilesForFolder(f);
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