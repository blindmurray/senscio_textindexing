import java.io.File;
import java.util.Date;

public class DirectoryReader {
	//constructs html ul from reading folder directory
	public static String listFilesForFolder(File folder, String html) {
		for (File fileEntry : folder.listFiles()) {
			
			if (fileEntry.isDirectory() && !fileEntry.isHidden()) {
				int count = 0;
				for(File file : fileEntry.listFiles()){
					if(file.isDirectory()){
						count++;
					}
				}
					if(count==0){
						html += "<li class=\"folder\">" + "<span onclick=\"triggerSelect(this.id)\" id= \"" + fileEntry.getPath() + "\">" + fileEntry.getName() + "</span>";
						html += "<ul>";
						html = listFilesForFolder(fileEntry, html);
						html+= "</li>";
					}
					else{
						html += "<li>" + "<span onclick=\"triggerSelect(this.id)\" id= \"" + fileEntry.getPath() + "\">" + fileEntry.getName() + "</span>";
						html += "<ul>";
						html = listFilesForFolder(fileEntry, html);
						html+= "</li>";
					}
			}
			else if(!fileEntry.isHidden()){
				html+= "<li class =\"file\">" +  fileEntry.getName() + "</li>";
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
			html += "</ul>";
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