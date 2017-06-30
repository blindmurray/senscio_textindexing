import java.io.File;
import java.io.FileFilter;


public class TextFileFilter implements FileFilter {

public boolean accept(File pathname) {
	return pathname.getName().toLowerCase().endsWith(".txt");
}

public static void clear(File folder) {
	File[] files = folder.listFiles();
	if(files!=null) {
		for(File f: files) {
			if(f.isDirectory()) {
				clear(f);
			} else {
				f.delete();
			}
		}
	}
}
}
