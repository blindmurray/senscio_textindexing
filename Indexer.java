import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
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

public class Indexer {
private IndexWriter writer;

   public Indexer(String indexDir) throws IOException {
      //this directory will contain the indexes
      Directory indexDirectory = FSDirectory.open(Paths.get(indexDir));

      //create the indexer
      Analyzer analyzer = new StandardAnalyzer();
      IndexWriterConfig conf = new IndexWriterConfig(analyzer);
      writer = new IndexWriter(indexDirectory, conf);  
      }


public void close() throws CorruptIndexException, IOException {
	writer.close();
}

private Document getDocument(File file) throws IOException {
	Document document = new Document();
	try (InputStream stream = Files.newInputStream(file.toPath())) {
		//index file path
		Field filePathField = new StringField(LuceneConstants.FILE_PATH, file.getAbsolutePath(), Field.Store.YES);
		
		//index file contents
        String content = new String(Files.readAllBytes(file.toPath()),"UTF-8");
        content = content.replaceAll("[^\\p{Graph}\n\r\t ]", "");

        Field contentField = new TextField(LuceneConstants.CONTENTS, content, Field.Store.YES);
		
		//index file name
		Field fileNameField = new StringField(LuceneConstants.FILE_NAME, file.getName(), Field.Store.YES);

		document.add(contentField);
		document.add(fileNameField);
		document.add(filePathField);

		return document;
	}
}
	
private void indexFile(File file) throws IOException {
	Document document = getDocument(file); //call getDocument method
	writer.addDocument(document);

}


static void createIndex(String indexDir, String dataDir, Indexer indexer) throws IOException, TikaException, SAXException {
    indexer = new Indexer(indexDir);
    int numIndexed;
    long startTime = System.currentTimeMillis();	
    //Calls method to write the index
    numIndexed = indexer.writeIndex(dataDir, new TextFileFilter());
    long endTime = System.currentTimeMillis();
    indexer.close();
    //Checks to see how many files are indexed and time taken
    System.out.println(numIndexed+" File indexed, time taken: "
       + (endTime-startTime) +" ms");		
 }

public int writeIndex(String dataDirPath, FileFilter filter) throws IOException, TikaException, SAXException {
	//get all files in the data directory
	File[] files = new File(dataDirPath).listFiles();
	File convertedFile = null;
	
	//Check for file type and call appropriate method to convert the file.
	for (int i = 0; i < files.length; i++) {
		File file = files[i];
		System.out.println(file.toString());
		//will not index files with these extensions
		if(TXT.getExtension(file.toString()).matches("zip|graffle|java|jar|mp4|mp3|dat|msg|xlw|mpp|xml|jpg|jpeg|png|vsd")){

		}
		//Recalls the Indexer class if the file is a Directory
		else if(file.isDirectory()){
        	writeIndex(file.toString(),new TextFileFilter());
        }
		//Will index file if file is a .txt file
        else if(!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)){
        	indexFile(file);
        }
		//Will convert file by calling Parse class if file is a iWorks file
        else if(TXT.getExtension(file.toString()).matches("pages|key|numbers")){
       	 Parse.parseIWORKS(file.toString());
       	 convertedFile = new File(TXT.editExtension(file.toString()));
       	 if(!convertedFile.isDirectory() && !convertedFile.isHidden() && convertedFile.exists() && convertedFile.canRead() && filter.accept(convertedFile)){
           	 indexFile(file);
            }
       	 convertedFile.delete();

        }
        else if(TXT.getExtension(file.toString()).matches("xls|xlsx")){
          	 Parse.parseIWORKS(file.toString());
          	 convertedFile = new File(TXT.editExtension(file.toString()));
          	 if(!convertedFile.isDirectory() && !convertedFile.isHidden() && convertedFile.exists() && convertedFile.canRead() && filter.accept(convertedFile)){
              	 indexFile(file);
               }
          	 convertedFile.delete();

           }
		//Will parse all other files
		else{
       	 Parse.parse(file.toString());
       	 convertedFile = new File(TXT.editExtension(file.toString()));
       	 if(!convertedFile.isDirectory() && !convertedFile.isHidden() && convertedFile.exists() && convertedFile.canRead() && filter.accept(convertedFile)){
           	 indexFile(file);
            }
       	 convertedFile.delete();
        }
	}
	return writer.numDocs();

}
}