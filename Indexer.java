import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang.StringUtils;
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
import com.google.common.base.CharMatcher;


public class Indexer {

public static IndexWriter writer;
public static void reindex() throws IOException, TikaException, SAXException{
        Indexer indexer = null;

        //Creates new index
        File indexDirFile = new File(LuceneConstants.indexDir);
        TextFileFilter.clear(indexDirFile);
        createIndex(LuceneConstants.indexDir, LuceneConstants.dataDir, indexer);
}

public Indexer(String indexDir) throws IOException {
        //this directory will contain the indexes
        Directory indexDirectory = FSDirectory.open(Paths.get(LuceneConstants.indexDir));

        //create the indexer
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
        conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        writer = new IndexWriter(indexDirectory, conf);
}

public static Document getDocument(File newFile, File oldFile) throws IOException {
        Document document = new Document();
        try (InputStream stream = Files.newInputStream(newFile.toPath())) {

                String content = new String(Files.readAllBytes(newFile.toPath()));

                //Converts content to ASCII format
                content = CharMatcher.ASCII.retainFrom(content);

                //Replaces gibberish content with proper spacing
                content = content.replaceAll("[^\\p{Graph}\n\r\t ]", "");
                content = content.replaceAll("[\\t\\n\\r]", " ");

                //Creates the google-like blurb for search results
                String blurb;
                System.out.println(content.length());
                if(content.length()<149){
                        blurb = "..."+StringUtils.substring(content, 0, content.length())+"...";
                }
                else{
                        blurb = "..."+StringUtils.substring(content, 200, 450)+"...";
                }
                //Converts content to all lowercase
                content = content.toLowerCase();

                System.out.println(blurb);
                //Creates file Path field, extension field, concent field, and file name field
                Field previewField = new StringField(LuceneConstants.FILE_PREVIEW, blurb, Field.Store.YES);
                Field filePathField = new StringField(LuceneConstants.FILE_PATH, oldFile.getAbsolutePath(), Field.Store.YES);
                Field extField = new TextField(LuceneConstants.FILE_EXT, TXT.getExtension(oldFile.toString()), Field.Store.YES);
                Field contentField = new TextField(LuceneConstants.CONTENTS, content, Field.Store.YES);
                Field fileNameField = new StringField(LuceneConstants.FILE_NAME, oldFile.getName(), Field.Store.YES);

                //Adds them to document objects
                document.add(previewField);
                document.add(contentField);
                document.add(extField);
                document.add(fileNameField);
                document.add(filePathField);

                //Returns document object
                return document;
        }
}
public static Document getDocument(File newFile, String tokens, File oldFile) throws IOException {
        Document document = new Document();
        try (InputStream stream = Files.newInputStream(newFile.toPath())) {

                String content = new String(Files.readAllBytes(newFile.toPath()));

                //Converts content to ASCII format
                content = CharMatcher.ASCII.retainFrom(content);

                //Replaces gibberish content with proper spacing
                content = content.replaceAll("[^\\p{Graph}\n\r\t ]", "");
                content = content.replaceAll("[\\t\\n\\r]", " ");

                //Converts content to all lowercase
                content = content.toLowerCase();

                String blurb;
                if(content.length()<149){
                        blurb = "..."+StringUtils.substring(content, 0, content.length())+"...";
                }
                else{
                        blurb = "..."+StringUtils.substring(content, 200, 450)+"...";
                }
                System.out.println(blurb);
                //Creates file Path field, extension field, concent field, and file name field
                Field previewField = new StringField(LuceneConstants.FILE_PREVIEW, blurb, Field.Store.YES);
                Field terms = new StringField(LuceneConstants.FILE_TOKENS, tokens, Field.Store.YES);
                Field filePathField = new StringField(LuceneConstants.FILE_PATH, oldFile.getAbsolutePath(), Field.Store.YES);
                Field extField = new TextField(LuceneConstants.FILE_EXT, TXT.getExtension(oldFile.toString()), Field.Store.YES);
                Field contentField = new TextField(LuceneConstants.CONTENTS, content, Field.Store.YES);
                Field fileNameField = new StringField(LuceneConstants.FILE_NAME, oldFile.getName(), Field.Store.YES);

                //Adds them to document objects
                document.add(previewField);
                document.add(terms);
                document.add(contentField);
                document.add(extField);
                document.add(fileNameField);
                document.add(filePathField);

                //Returns document object
                return document;
        }
}

public static void indexFile(File newFile, File oldFile ) throws IOException {
        System.out.println("Indexing "+ oldFile.getCanonicalPath());
        Document document = getDocument(newFile, oldFile); //call getDocument method
        writer.addDocument(document); //adds document to indexWriter
}

public static void indexFile(File newFile, String tokens, File oldFile ) throws IOException {
        System.out.println("Indexing "+ oldFile.getCanonicalPath());
        Document document = getDocument(newFile, tokens, oldFile); //call getDocument method
        writer.addDocument(document); //adds document to indexWriter
}

public static void createIndex(String indexDir, String dataDir, Indexer indexer) throws IOException, TikaException, SAXException {
        indexer = new Indexer(indexDir);
        int numIndexed;
        long startTime = System.currentTimeMillis();

        //Calls method to write the index
        numIndexed = Indexer.writeIndex(dataDir, new TextFileFilter());
        long endTime = System.currentTimeMillis();

        //Checks to see how many files are indexed and time taken
        indexer.close();
        System.out.println(numIndexed+" File indexed, time taken: "     + (endTime-startTime) +" ms");
}

public static void addIndex(String indexDir, String dataDir, Indexer indexer) throws IOException, TikaException, SAXException {
        indexer = new Indexer(indexDir);
        int numIndexed;
        long startTime = System.currentTimeMillis();

        //Calls method to write the index
        numIndexed = Indexer.writeIndex(dataDir, new TextFileFilter());
        long endTime = System.currentTimeMillis();

        //Checks to see how many files are indexed and time taken
        indexer.close();
        System.out.println(numIndexed+" File indexed, time taken: "     + (endTime-startTime) +" ms");
}

public void close() throws CorruptIndexException, IOException {
         writer.close();
}

public static int writeIndex(String dataDirPath, FileFilter filter) throws IOException, TikaException, SAXException {
        //get all files in the data directory
        File[] files = new File(dataDirPath).listFiles();
        File convertedFile = null;

        //Check for file type and call appropriate method to convert the file.
        for (int i = 0; i < files.length; i++) {
                File file = files[i];

                //Recalls the Indexer class if the file is a Directory
                if(file.isDirectory()){
                        writeIndex(file.toString(),new TextFileFilter());
                }

                //Will index file if file is a .txt file
                else if(!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file) && TXT.getExtension(file.toString()).equals("txt")){
                        indexFile(file, file);
                }

                //Will convert file by calling Parse class if file is not .txt
                else if(TXT.getExtension(file.toString()).matches("pdf|html|rtf|odf|ods|odt|csv|xlsx|xls|pps|doc|docx|ppt|pptx|pages|key|numbers")){
                        Parse.parse(file.toString());
                        convertedFile = new File(TXT.editExtension(file.toString()));
                        if(!convertedFile.isDirectory() && !convertedFile.isHidden() && convertedFile.exists() && convertedFile.canRead() && filter.accept(convertedFile)){
                                indexFile(convertedFile, file);
                                convertedFile.delete();
                        }
                }
                else{
                }
        }
        return writer.numDocs();
        }
}