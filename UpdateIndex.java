import java.io.File;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class UpdateIndex {

public static void updateIndex(String filePath, String indexDir) throws IOException, TikaException, SAXException{
	File writeFile = new File(filePath + "/write.lock");
	writeFile.delete();
	add(filePath, indexDir);
}
public static void add(String filePath, String indexDir) throws IOException, TikaException, SAXException{
	Indexer indexer = null;
	Indexer.addIndex(indexDir, filePath, indexer);
	System.out.println("New Document(s) Added");
	
}



}
