import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class TXT{
	public static void createTXT(String content, String filePath){
		File f = null;
		boolean bool = false;
		try {
			// calls editExtension method
			filePath = editExtension(filePath);
			f = new File(filePath);

			// tries to create new file in the system
			bool = f.createNewFile();

			// prints
			System.out.println("File created: "+bool);

			FileOutputStream fos = new FileOutputStream(f);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

			bw.write(content);
			bw.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static String editExtension(String file){
		//Splits file path by "/"
		String[] tokens = file.split("/");

		//Split name of document if there are more than one "."
		String[] name = tokens[tokens.length-1].split("\\.");
		int exlen = (name[name.length -1]).length();
		file = file.substring(0, file.length()-exlen);
		file = file + "txt";
		return file;
	}
	public static String getExtension(String file){
		//replaces previous extension with a .txt extension
		String[] tokens = file.split("/");
		String[] name = tokens[tokens.length-1].split("\\.");
		String ext = name[name.length -1];
		return ext;
	}
	public static String getFileName(String file){
		String[] tokens = file.split("\\\\");
		String name = tokens[tokens.length -1];
		return name;
	}
}