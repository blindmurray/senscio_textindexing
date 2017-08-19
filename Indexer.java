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
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import com.google.common.base.CharMatcher;
/**The Indexer class is an implementation of the Lucene API that stores the information extracted from various
 * document types and stores them to be further searched using the Tika API. This class makes it possible to create 
 * an index, delete a document from the index, and add a document to the index. 
 * @author Gina
 *
 */
public class Indexer {
	
	public static IndexWriter writer;
	
	/*
	 * Can be used to reindex the database.
	 */
	public static void main(String[] args) throws IOException, TikaException, SAXException {
		Indexer.reindex(Constants.indexDir, Constants.dataDir);
	}
	
	/**
	 * Indexer will instantiate the appropriate objects to open an IndexWriter and create 
	 * an appendable index. 
	 * @param indexDir Path of the folder where the index will be stored
	 * @throws IOException
	 */
	public Indexer(String indexDir) throws IOException {
		// This directory will contain the indexes.
		Directory indexDirectory = FSDirectory.open(Paths.get(indexDir));

		// create the indexer
		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig conf = new IndexWriterConfig(analyzer);
		conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		writer = new IndexWriter(indexDirectory, conf);
	}
	
	/**
	 * writeIndex will create an index given a path to the data directory and a file filter type.
	 * This is class will properly index the file depending on its file type.
	 * @param dataDir Path of folder with all of the indexable files
	 * @param filter Filter
	 * @throws IOException
	 * @throws TikaException
	 * @throws SAXException
	 */
	public static void writeIndex(String dataDir, FileFilter filter)
			throws IOException, TikaException, SAXException {
		// Get all files in the data directory stored in a FileArray
		File[] files = new File(dataDir).listFiles();
		File convertedFile = null;

		// Check for file type and call appropriate method to convert the file.
		for (int i = 0; i < files.length; i++) {
			File file = files[i];

			// Recalls the Indexer class if the file is a Directory
			if (file.isDirectory()) {
				writeIndex(file.toString(), new TextFileFilter());
			}

			// Will index file if file is a .txt file
			else if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)
					&& FileNames.getExtension(file.toString()).equals("txt")) {
				indexFile(file, file);
			}

			/* Will convert file by calling Parse class if file is not .txt document but is a readable document with
			 * extensions including but not limited to pdf, html, doc(x), ppt(x), and xls(x).
			 */
			
			else if (FileNames.getExtension(file.toString())
					.matches("pdf|html|rtf|odf|ods|odt|csv|xlsx|xls|pps|doc|docx|ppt|pptx|pages|key|numbers")) {
				Parse.parse(file.toString());
				convertedFile = new File(FileNames.editExtension(file.toString()));
				if (!convertedFile.isDirectory() && !convertedFile.isHidden() && convertedFile.exists()
						&& convertedFile.canRead() && filter.accept(convertedFile)) {
					indexFile(convertedFile, file);
					convertedFile.delete();
				}
			} 
			/*
			 * Other file types can also be stored in the repository. Please note that only documents with the 
			 * appropriate mime type in the node.js can be downloaded. However, all else can be stored. 
			 */
			else {
				Parse.parse(file.toString());
				convertedFile = new File(FileNames.editExtension(file.toString()));
				if (!convertedFile.isDirectory() && !convertedFile.isHidden() && convertedFile.exists()
						&& convertedFile.canRead() && filter.accept(convertedFile)) {
					indexFileNot(convertedFile, file);
					convertedFile.delete();
				}
			}
		}
	}
	
	/**
	 * reindex will clear the existing index and create a new index given an index directory location
	 * and data directory location.
	 * @param indexDir Path of where the index will be stored
	 * @param dataDir Path of folder with all of the indexable files
	 * @throws IOException
	 * @throws TikaException
	 * @throws SAXException
	 */
	public static void reindex(String indexDir, String dataDir) throws IOException, TikaException, SAXException {
		Indexer indexer = null;
		File indexDirFile = new File(indexDir);
		TextFileFilter.clear(indexDirFile);
		createIndex(indexDir, dataDir, indexer);
	}

	/**
	 * Given a document type with the txt extension and the original document, getDocument will create a Document to be
	 * added into the index.
	 * @param newFile File with the extension .txt
	 * @param oldFile Original file
	 * @return Returns a document to be added to the index
	 * @throws IOException
	 */
	public static Document getDocument(File newFile, File oldFile) throws IOException {
		Document document = new Document();
		try (InputStream stream = Files.newInputStream(newFile.toPath())) {
			// Read content using the bytes of the path of the converted text file
			String content = new String(Files.readAllBytes(newFile.toPath()));

			// Converts content to ASCII format
			content = CharMatcher.ASCII.retainFrom(content);

			// Replaces gibberish content with proper spacing
			content = content.replaceAll("[^\\p{Graph}\n\r\t ]", "");
			content = content.replaceAll("[\\t\\n\\r]", " ");

			// Creates the google-like blurb for search results
			String blurb;
			if (content.length() < 149) {
				blurb = "..." + StringUtils.substring(content, 0, content.length()) + "...";
			} else {
				blurb = "..." + StringUtils.substring(content, 200, 450) + "...";
			}
			// Converts content to all lowercase
			content = content.toLowerCase();

			// Creates file Path field, extension field, content field, and file name field
			Field previewField = new StringField(Constants.FILE_PREVIEW, blurb, Field.Store.YES);
			Field filePathField = new StringField(Constants.FILE_PATH, oldFile.getAbsolutePath(), Field.Store.YES);
			Field extField = new TextField(Constants.FILE_EXT, FileNames.getExtension(oldFile.toString()), Field.Store.YES);
			Field contentField = new TextField(Constants.CONTENTS, content, Field.Store.NO);
			Field fileNameField = new StringField(Constants.FILE_NAME, oldFile.getName(), Field.Store.YES);

			// Adds them to document objects
			document.add(previewField);
			document.add(contentField);
			document.add(extField);
			document.add(fileNameField);
			document.add(filePathField);

			// Returns document object
			return document;
		}
	}
	
	/**
	 * Given a document type with the txt extension, the original document, and key words that could be used to identify the document,
	 * getDocument will create a Document to be
	 * added into the index.
	 * @param newFile File with the extension .txt
	 * @param oldFile Original file
	 * @param tokens Key terms to be added into the index relative to the file
	 * @return Returns a document to be added to the index
	 * @throws IOException
	 */
	public static Document getDocument(File newFile, String tokens, File oldFile) throws IOException {
		Document document = new Document();
		try (InputStream stream = Files.newInputStream(newFile.toPath())) {

			String content = new String(Files.readAllBytes(newFile.toPath()));

			// Converts content to ASCII format
			content = CharMatcher.ASCII.retainFrom(content);

			// Replaces gibberish content with proper spacing
			content = content.replaceAll("[^\\p{Graph}\n\r\t ]", "");
			content = content.replaceAll("[\\t\\n\\r]", " ");

			// Converts content to all lowercase
			content = content.toLowerCase();

			String blurb;
			if (content.length() < 149) {
				blurb = "..." + StringUtils.substring(content, 0, content.length()) + "...";
			} else {
				blurb = "..." + StringUtils.substring(content, 200, 450) + "...";
			}
			
			// Creates file Path field, extension field, content field, and file name field
			Field previewField = new StringField(Constants.FILE_PREVIEW, blurb, Field.Store.YES);
			Field terms = new StringField(Constants.FILE_TOKENS, tokens, Field.Store.YES);
			Field filePathField = new StringField(Constants.FILE_PATH, oldFile.getAbsolutePath(),
					Field.Store.YES);
			Field extField = new TextField(Constants.FILE_EXT, FileNames.getExtension(oldFile.toString()),
					Field.Store.YES);
			Field contentField = new TextField(Constants.CONTENTS, content, Field.Store.NO);
			Field fileNameField = new StringField(Constants.FILE_NAME, oldFile.getName(), Field.Store.YES);

			// Adds them to document objects
			document.add(previewField);
			document.add(terms);
			document.add(contentField);
			document.add(extField);
			document.add(fileNameField);
			document.add(filePathField);

			// Returns document object
			return document;
		}
	}
	
	/**
	 * Given a document type with the txt extension, the original document that is NOT a readable document (such 
	 * as pdf or docx) getDocument will create a Document to be added into the index.
	 * @param newFile File with the extension .txt
	 * @param oldFile Original file
	 * @return Returns a document to be added to the index
	 * @throws IOException
	 */
	public static Document getDocumentNot(File newFile, File oldFile) throws IOException {
		Document document = new Document();
		try (InputStream stream = Files.newInputStream(newFile.toPath())) {

			String content = "";

			// Creates file Path field, extension field, concent field, and file name field
			Field previewField = new StringField(Constants.FILE_PREVIEW, content, Field.Store.YES);
			Field filePathField = new StringField(Constants.FILE_PATH, oldFile.getAbsolutePath(),
					Field.Store.YES);
			Field extField = new TextField(Constants.FILE_EXT, FileNames.getExtension(oldFile.toString()),
					Field.Store.YES);
			Field contentField = new TextField(Constants.CONTENTS, content, Field.Store.NO);
			Field fileNameField = new StringField(Constants.FILE_NAME, oldFile.getName(), Field.Store.YES);

			// Adds them to document objects
			document.add(previewField);
			document.add(contentField);
			document.add(extField);
			document.add(fileNameField);
			document.add(filePathField);

			// Returns document object
			return document;
		}
	}
	
	/**
	 * Given a document type with the txt extension, the original document that is NOT a readable document (such 
	 * as pdf or docx), and key words that could be used to identify the document, getDocument will create a 
	 * Document to be added into the index.
	 * @param newFile File with the extension .txt
	 * @param oldFile Original file
	 * @param tokens Key terms to be added into the index relative to the file
	 * @return Returns a document to be added to the index
	 * @throws IOException
	 */
	public static Document getDocumentNot(File newFile, String tokens, File oldFile) throws IOException {
		Document document = new Document();
		try (InputStream stream = Files.newInputStream(newFile.toPath())) {

			String content = "";
			// Creates file Path field, extension field, content field, and file
			// name field
			Field previewField = new StringField(Constants.FILE_PREVIEW, content, Field.Store.YES);
			Field terms = new StringField(Constants.FILE_TOKENS, tokens, Field.Store.YES);
			Field filePathField = new StringField(Constants.FILE_PATH, oldFile.getAbsolutePath(),
					Field.Store.YES);
			Field extField = new TextField(Constants.FILE_EXT, FileNames.getExtension(oldFile.toString()),
					Field.Store.YES);
			Field contentField = new TextField(Constants.CONTENTS, content, Field.Store.NO);
			Field fileNameField = new StringField(Constants.FILE_NAME, oldFile.getName(), Field.Store.YES);

			// Adds them to document objects
			document.add(previewField);
			document.add(terms);
			document.add(contentField);
			document.add(extField);
			document.add(fileNameField);
			document.add(filePathField);

			// Returns document object
			return document;
		}
	}
	/**
	 * Delete Index will delete a given file from the index.
	 * @param file File that needs to be deleted from the index
	 */
	public static void deleteIndex(String file) {

		try {
			Term term = new Term(Constants.FILE_PATH, file);
			writer.deleteDocuments(term);
			writer.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * IndexFile will call the getDocument method to create the document object and then add it to the index.
	 * @param newFile File with the extension .txt
	 * @param oldFile Original file
	 * @throws IOException
	 */
	public static void indexFile(File newFile, File oldFile) throws IOException {
		System.out.println("Indexing " + oldFile.getCanonicalPath());
		Document document = getDocument(newFile, oldFile); // call getDocument
		// method
		writer.addDocument(document); // adds document to indexWriter
	}

	/**
	 * IndexFile will call the getDocument method to create the document object and then add it to the index.
	 * @param newFile File with the extension .txt
	 * @param oldFile Original file
	 * @param tokens Key terms to be added into the index relative to the file
	 * @throws IOException
	 */
	public static void indexFile(File newFile, String tokens, File oldFile) throws IOException {
		System.out.println("Indexing " + oldFile.getCanonicalPath());
		Document document = getDocument(newFile, tokens, oldFile); // call
		// getDocument
		// method
		writer.addDocument(document); // adds document to indexWriter
	}
	
	/**
	 * IndexFile will call the getDocument method to create the document object and then add it to the index.
	 * This function is called when the object is not a readable document (such as a pdf or docx).
	 * @param newFile File with the extension .txt
	 * @param oldFile Original file
	 * @throws IOException
	 */
	public static void indexFileNot(File newFile, File oldFile) throws IOException {
		System.out.println("Indexing " + oldFile.getCanonicalPath());
		Document document = getDocumentNot(newFile, oldFile); // call
		// getDocument
		// method
		writer.addDocument(document); // adds document to indexWriter
	}

	/**
	 * IndexFile will call the getDocument method to create the document object and then add it to the index.
	 * This function is called when the object is not a readable document (such as a pdf or docx).
	 * @param newFile File with the extension .txt
	 * @param oldFile Original file
	 * @param tokens Key terms to be added into the index relative to the file
	 * @return Returns a document to be added to the index
	 * @throws IOException
	 */
	public static void indexFileNot(File newFile, String tokens, File oldFile) throws IOException {
		System.out.println("Indexing " + oldFile.getCanonicalPath());
		Document document = getDocumentNot(newFile, tokens, oldFile); // call
		// getDocument
		// method
		writer.addDocument(document); // adds document to indexWriter
	}

	/**
	 * createIndex will create an index given the indexDirectory, dataDirecotry and an indexer
	 * @param indexDir Path where the index will be stored
	 * @param dataDir Path where all of the indexable files are stored
	 * @param indexer An indexer
	 * @throws IOException
	 * @throws TikaException
	 * @throws SAXException
	 */
	public static void createIndex(String indexDir, String dataDir, Indexer indexer)
			throws IOException, TikaException, SAXException {
		indexer = new Indexer(indexDir);

		// Calls method to write the index
		Indexer.writeIndex(dataDir, new TextFileFilter());

		// Checks to see how many files are indexed and time taken
		indexer.close();
	}
	
	/**
	 * Closes the IndexWriter.
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}

}