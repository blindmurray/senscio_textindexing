import java.io.File;

public class DirectoryReader {
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
}