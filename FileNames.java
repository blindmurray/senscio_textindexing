import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
/**
 * The FileNames class is a resource of functions to handle different file formats. It allows manipulations of
 * the file format in terms of extension-editing and file-renaming.
 * @author Gina
 *
 */
public class FileNames {

	/**
	 * Given a file and its contents, the class creates a txt file with a given filepath and the file's contents
	 * @param content The contents to be stored in the text document
	 * @param filePath The path of the file to be made.
	 */
	public static void createTXT(String content, String filePath) {
		File f = null;
		try {
			// Calls editExtension method
			filePath = editExtension(filePath);
			f = new File(filePath);

			// Tries to create new file in the system and writes the contents to the file
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write(content);
			bw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Edits the extension of a file to a ".txt"
	 * @param file The file that needs its extension edited
	 * @return String with a ".txt" extension
	 */
	public static String editExtension(String file) {
		// Splits file path by "/"
		String[] tokens = file.split("/");

		// Split name of document if there are more than one "."
		String[] name = tokens[tokens.length - 1].split("\\.");
		int exlen = (name[name.length - 1]).length();
		file = file.substring(0, file.length() - exlen);
		file = file + "txt";
		return file;
	}

	/**
	 * Gets the extension of the file
	 * @param file The file whose extension is being retrieved
	 * @return The extension
	 */
	public static String getExtension(String file) {
		// replaces previous extension with a .txt extension
		String[] tokens = file.split("/");
		String[] name = tokens[tokens.length - 1].split("\\.");
		String ext = name[name.length - 1];
		return ext;
	}

	/**
	 * Gets the name of the file without the path
	 * @param file The file whose name is being retrieved
	 * @return The name of the file without the path
	 */
	public static String getFileName(String file) {
		String[] tokens = file.split("\\\\");
		String name = tokens[tokens.length - 1];
		return name;
	}
	/**
	 * Checks if there are any files with duplicate names
	 * @param filename File name that its checking
	 * @param path File path that it is checking in
	 * @return A file name that does not have any duplicates
	 */
	public static String duplicateCheck(String filename, String path) {
		boolean check = true;
		File[] files = new File(path).listFiles();
		// check if uploaded file has same name as another in the same folder
		for (int i = 0; i < files.length; i++) {
			for (File file : files) {
				String fname = file.getName();
				if (fname.equals(filename)) {
					check = false;
				}
			}
			// returns filename if no duplicates
			if (check) {
				return filename;
			} else {
				// changes filename and rechecks for duplicates
				filename = changeName(filename);
				duplicateCheck(filename, path);
			}
		}
		return filename;
	}
	/**
	 * Changes the name of the file by adding an extension to it.
	 * @param f The file whose name needs to be edited
	 * @return Edited file name
	 */
	public static String changeName(String f) {
		// if there is a duplicate name, adds (1) or adds 1 to version number
		String ext = FileNames.getExtension(f);
		f = f.substring(0, f.length() - ext.length() - 1);
		if (f.contains("(")) {
			String noparen = f.substring(0, f.length() - 1);
			String[] vnum = noparen.split("(");
			String vnum2 = vnum[vnum.length - 1];
			int vint;
			try {
				vint = Integer.parseInt(vnum2);
			} catch (NumberFormatException e) {
				return f + "(1)" + ext;
			} catch (NullPointerException e) {
				return f + "(1)" + ext;
			}
			vint++;
			String newname = noparen.substring(0, noparen.length() - vnum2.length()) + "(" + vint + ")." + ext;
			return newname;
		} else {
			return f + "(1)." + ext;
		}
	}
}