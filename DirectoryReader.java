import java.io.File;
import java.util.ArrayList;

public class DirectoryReader {
static String html = "";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File folder = new File("C:/MICHELLE/txtfiles");
		listFilesForFolder(folder, "");
		html += "";
		System.out.println(html);
	}


public static void listFilesForFolder(File folder, String spaces) {
	html += "<ul>";
	//spaces += "    ";
	for (File fileEntry : folder.listFiles()) {
        if (fileEntry.isDirectory()) {
        	html += "<li>" + fileEntry.getName();
        	//System.out.println(spaces + fileEntry.getName());
            listFilesForFolder(fileEntry, spaces);
            html+= "</li>";
        } 
        //else {
          //  System.out.println(fileEntry.getName());
        //}
	}
	html += "</ul>";
    //spaces = spaces.substring(0, spaces.length()-4);
}
}
