import java.io.File;
import java.util.Date;

public class DirectoryReader {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File folder = new File("C:/MICHELLE");
		String html = "<ul id=\"expList\">";
		html = listFilesForFolder(folder, html);
		System.out.println(html);
	}

	public static String listFilesForFolder(File folder, String html) {
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
				html = listFilesForFolder(fileEntry, html);
				html+= "</li>";
				}
				else{
					html += "<li>" + "<span>" + fileEntry.getName() + "</span>"+ "</li>";
				}
			} 
		}
		html += "</ul>";
		return html;
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