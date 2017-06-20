import java.io.File;

public class clean {
	public static void deleteFolderContents(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) {
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolderContents(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	}
}
