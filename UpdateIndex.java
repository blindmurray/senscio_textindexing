import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
	int num = writeIndexUpdate(filePathAdd,  new TextFileFilter());
	writer.close();
	System.out.println(num);
}
public static void updateIndex(String filePathAdd, String tokens, String indexDir) throws IOException, TikaException, SAXException{
	//this directory will contain the indexer
	Directory indexDirectory = FSDirectory.open(Paths.get(indexDir));
	
	//create the indexer
	StandardAnalyzer analyzer = new StandardAnalyzer();
	IndexWriterConfig conf = new IndexWriterConfig(analyzer);
	conf.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
	writer = new IndexWriter(indexDirectory, conf); 
	int num = writeIndexUpdate(filePathAdd, tokens, new TextFileFilter());
	writer.close();
	System.out.println(num);
}

public static int writeIndexUpdate(String dataDirPath, FileFilter filter) throws IOException, TikaException, SAXException{
		File convertedFile = null;
		
		//Check for file type and call appropriate method to convert the file.
		File file = new File(dataDirPath);
			//Will index file if file is a .txt file
			if(!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file) && TXT.getExtension(file.toString()).equals("txt")){
				indexFile(file, file);
			}
		
			//Will convert file by calling Parse class if file is not .txt
			else if(TXT.getExtension(file.toString()).matches("pdf|html|rtf|odf|ods|odt|xlsx|xls|pps|doc|docx|ppt|pptx|pages|key|numbers")){
				Parse.parse(file.toString());
				convertedFile = new File(TXT.editExtension(file.toString()));
				if(!convertedFile.isDirectory() && !convertedFile.isHidden() && convertedFile.exists() && convertedFile.canRead() && filter.accept(convertedFile)){
					indexFile(convertedFile, file);
					convertedFile.delete();
				}
			}	
			else{
			}
		return writer.numDocs();
	}

public static int writeIndexUpdate(String dataDirPath, String tokens, FileFilter filter) throws IOException, TikaException, SAXException{
	File convertedFile = null;
	
	//Check for file type and call appropriate method to convert the file.
	File file = new File(dataDirPath);
		
		//Will index file if file is a .txt file
		if(!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file) && TXT.getExtension(file.toString()).equals("txt")){
			indexFile(file, tokens, file);
		}
	
		//Will convert file by calling Parse class if file is not .txt
		else if(TXT.getExtension(file.toString()).matches("pdf|html|rtf|odf|ods|odt|xlsx|xls|pps|doc|docx|ppt|pptx|pages|key|numbers")){
			Parse.parse(file.toString());
			convertedFile = new File(TXT.editExtension(file.toString()));
			if(!convertedFile.isDirectory() && !convertedFile.isHidden() && convertedFile.exists() && convertedFile.canRead() && filter.accept(convertedFile)){
				indexFile(convertedFile,tokens, file);
				convertedFile.delete();
			}
		}	
	return writer.numDocs();
	}
}