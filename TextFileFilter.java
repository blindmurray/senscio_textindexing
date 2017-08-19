import java.io.File;
import java.io.FileFilter;
/**
 * Extends the FileFilter class.
 * @author Gina
 *
 */
public class TextFileFilter implements FileFilter {
	/**
	 * Determines if the file is a .txt file
	 */
	public boolean accept(File pathname) {
		return pathname.getName().toLowerCase().endsWith(".txt");
	}

	/**
	 * Clears all files and folders from a directory.
	 * @param folder
	 */
	public static void clear(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					clear(f);
				} else {
					f.delete();
				}
			}
		}
	}
}
