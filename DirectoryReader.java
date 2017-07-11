import java.io.File;
import java.util.ArrayList;

public class DirectoryReader {
static String html = "<html><head><title>filedirectory</title></head><body>";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File folder = new File("C:/pictures");
		listFilesForFolder(folder, "");
		html += "</body></html>";
		System.out.println(html);
	}


public static void listFilesForFolder(File folder, String spaces) {
	html += "<ul>";
	//spaces += "    ";
	for (File fileEntry : folder.listFiles()) {
        if (fileEntry.isDirectory()) {
        	html += "<li>" + fileEntry.getName() + "</li>";
        	//System.out.println(spaces + fileEntry.getName());
            listFilesForFolder(fileEntry, spaces);
        } 
        //else {
          //  System.out.println(fileEntry.getName());
        //}
	}
	html += "</ul>";
    //spaces = spaces.substring(0, spaces.length()-4);
}
}
