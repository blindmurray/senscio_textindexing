import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class UpdateIndex extends Indexer{

public UpdateIndex(String indexDir) throws IOException {
		super(indexDir);
		// TODO Auto-generated constructor stub
}
public static void updateIndex(String filePathAdd, String indexDir) throws IOException, TikaException, SAXException{
	//this directory will contain the indexer
	Directory indexDirectory = FSDirectory.open(Paths.get(indexDir));
	
	//create the indexer
	StandardAnalyzer analyzer = new StandardAnalyzer();
	IndexWriterConfig conf = new IndexWriterConfig(analyzer);
	conf.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
	writer = new IndexWriter(indexDirectory, conf); 
	int num = writeIndex(filePathAdd,  new TextFileFilter());
	writer.close();
	System.out.println(num);
	
}


}
