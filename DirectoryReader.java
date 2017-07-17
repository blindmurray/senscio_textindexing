import java.io.File;
import java.util.Date;

public class DirectoryReader {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File folder = new File("/Users/Gina/Documents/Files/txt_data");
		String html = "<ul id=\"expList\">";
		html = listFilesForFolder(folder, html);
		System.out.println(html);
	}
	//constructs html ul from reading folder directory
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
				html += "<li>" + "<span onclick=\"triggerSelect(this.id)\" id= \"" + fileEntry.getPath() + "\">" + fileEntry.getName() + "</span>";
				html += "<ul>";
				html = listFilesForFolder(fileEntry, html);
				html+= "</li>";
				}
				else{
					html += "<li>" + "<span onclick=\"triggerSelect(this.id)\" id= \"" + fileEntry.getPath() + "\">" + fileEntry.getName() + "</span>"+ "</li>";
				}
			} 
		}
		html += "</ul>";
		return html;
	}
	//if run in a while loop, checks for file changes
	public static Date directoryChangeCheck(File f, Date lastcheck){
		Date modDate = new Date(f.lastModified());
		if(modDate.after(lastcheck)){
			String html = "<ul id=\"expList\">";
			html = listFilesForFolder(f, html);
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