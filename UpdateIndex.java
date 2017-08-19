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
/**
 * Update index will add files into the index. It extends the Indexer class.
 * @author Gina
 *
 */
public class UpdateIndex extends Indexer {

	public UpdateIndex(String indexDir) throws IOException {
		super(indexDir);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Will re-open the IndexWriter and call writeIndexUpdate to add the file to the index.
	 * @param filePathAdd The path of the file to be indexed
	 * @param indexDir The location of the index Directory
	 * @throws IOException
	 * @throws TikaException
	 * @throws SAXException
	 */
	public static void updateIndex(String filePathAdd, String indexDir)
			throws IOException, TikaException, SAXException {
		// this directory will contain the indexer
		Directory indexDirectory = FSDirectory.open(Paths.get(indexDir));

		// create the indexer
		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig conf = new IndexWriterConfig(analyzer);
		conf.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
		writer = new IndexWriter(indexDirectory, conf);
		writeIndexUpdate(filePathAdd, new TextFileFilter());
		writer.close();
	}
	/**
	 * Will re-open the IndexWriter and call writeIndexUpdate to add the file to the index with its key terms.
	 * @param filePathAdd The path of the file to be indexed
	 * @param tokens The key terms to be associated with the document
	 * @param indexDir The location of the index Directory
	 * @throws IOException
	 * @throws TikaException
	 * @throws SAXException
	 */
	public static void updateIndex(String filePathAdd, String tokens, String indexDir)
			throws IOException, TikaException, SAXException {
		// This directory will contain the indexer
		Directory indexDirectory = FSDirectory.open(Paths.get(indexDir));

		// Create the indexer
		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig conf = new IndexWriterConfig(analyzer);
		conf.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
		writer = new IndexWriter(indexDirectory, conf);
		writeIndexUpdate(filePathAdd, tokens, new TextFileFilter());
		writer.close();
	}

	/**
	 * writeIndexUpdate will create one index given a path to the file path and a file filter type.
	 * This is class will properly index the file depending on its file type.
	 * @param dir Path of file that needs to be added to the index
	 * @param filter Filter
	 * @throws IOException
	 * @throws TikaException
	 * @throws SAXException
	 */
	public static void writeIndexUpdate(String dir, FileFilter filter) throws IOException, TikaException, SAXException {

		File convertedFile = null;
		File file = new File(dir);
		
		// Will index file if file is a .txt file
		if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)
				&& FileNames.getExtension(file.toString()).equals("txt")) {
			indexFile(file, file);
		}

		// Will convert file by calling Parse class if file is not .txt
		else if (FileNames.getExtension(file.toString())
				.matches("pdf|html|rtf|odf|ods|odt|xlsx|xls|pps|doc|docx|ppt|pptx|pages|key|numbers")) {
			Parse.parse(file.toString());
			convertedFile = new File(FileNames.editExtension(file.toString()));
			if (!convertedFile.isDirectory() && !convertedFile.isHidden() && convertedFile.exists()
					&& convertedFile.canRead() && filter.accept(convertedFile)) {
				indexFile(convertedFile, file);
				convertedFile.delete();
			}
		} 

		/* Will convert file by calling Parse class if file is not .txt document but is a readable document with
		 * extensions including but not limited to pdf, html, doc(x), ppt(x), and xls(x).
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
	/**
	 * writeIndexUpdate will create one index given a path to the file path and a file filter type.
	 * This is class will properly index the file depending on its file type.
	 * @param dir Path of file that needs to be added to the index
	 * @param tokens Key terms to associate with the file
	 * @param filter Filter
	 * @throws IOException
	 * @throws TikaException
	 * @throws SAXException
	 */
	public static void writeIndexUpdate(String dir, String tokens, FileFilter filter)
			throws IOException, TikaException, SAXException {
		
		File convertedFile = null;
		File file = new File(dir);

		// Will index file if file is a .txt file
		if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)
				&& FileNames.getExtension(file.toString()).equals("txt")) {
			indexFile(file, tokens, file);
		}

		// Will convert file by calling Parse class if file is not .txt
		else if (FileNames.getExtension(file.toString())
				.matches("pdf|html|rtf|odf|ods|odt|xlsx|xls|pps|doc|docx|ppt|pptx|pages|key|numbers")) {
			Parse.parse(file.toString());
			convertedFile = new File(FileNames.editExtension(file.toString()));
			if (!convertedFile.isDirectory() && !convertedFile.isHidden() && convertedFile.exists()
					&& convertedFile.canRead() && filter.accept(convertedFile)) {
				indexFile(convertedFile, tokens, file);
				convertedFile.delete();
			}
		} 

		/* Will convert file by calling Parse class if file is not .txt document but is a readable document with
		 * extensions including but not limited to pdf, html, doc(x), ppt(x), and xls(x).
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