import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher{
	IndexSearcher indexSearcher;
	QueryParser queryParser;
	Query query2;
	IndexReader indexReader;

public void searchIndex(String searchString, String indexDir) throws Exception {
	String index = indexDir;
	//Create Lucene searcher. It search over a single IndexReader.
	IndexSearcher searcher = createSearcher(index);
	
	//Search indexed contents using search term
	TopDocs foundDocs = searchInContent(searchString, searcher);
         
	//Total found documents
	System.out.println("Total Results: " + foundDocs.totalHits);
         
	//Print out the path of files which have searched term
	for (ScoreDoc sd : foundDocs.scoreDocs) {
		Document d = searcher.doc(sd.doc);
		System.out.println("Path : "+ d.get(LuceneConstants.FILE_NAME) + ", Score : " + sd.score);
	}
}
     
private static TopDocs searchInContent(String textToFind, IndexSearcher searcher) throws Exception{
	//Create search query
	QueryParser qp = new QueryParser(LuceneConstants.CONTENTS, new WhitespaceAnalyzer());
	Query query = qp.parse("+"+textToFind);
	
	//Search the index
	TopDocs hits = searcher.search(query, 10);
	return hits;
}
 
private static IndexSearcher createSearcher(String index) throws IOException {
	Directory dir = FSDirectory.open(Paths.get(index));
         
	//Interface for accessing a point-in-time view of a Lucene index
	IndexReader reader = DirectoryReader.open(dir);
         
	//Index searcher
	IndexSearcher searcher = new IndexSearcher(reader);
	return searcher;
}
}