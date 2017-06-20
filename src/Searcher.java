import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.Iterator;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
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

	public void searchIndex(String searchString, String indexDir) throws IOException, ParseException {
		System.out.println("Searching for '" + searchString + "'");
		Directory indexDirectory = FSDirectory.open(Paths.get(indexDir));
	    indexReader = DirectoryReader.open(indexDirectory);
	    indexSearcher = new IndexSearcher(indexReader);
        queryParser = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer());
        query2 = queryParser.parse(searchString);
		showSearchResults(query2);
   }
	@SuppressWarnings("unchecked")
	public void showSearchResults(Query query) throws IOException{
		TopDocs hits = indexSearcher.search(query, 100);
        ScoreDoc[] document = hits.scoreDocs;
        
		System.out.println("Total no of hits for content: " + hits.totalHits);
        for(int i = 0;i <document.length;i++)
        {                 
            Document doc = indexSearcher.doc(document[i].doc);      
            String filePath = doc.get("fullpath");                                      
            System.out.println(filePath);
        }
	}
		
	public Document getDocument(ScoreDoc scoreDoc)  throws CorruptIndexException, IOException {
		      return indexSearcher.doc(scoreDoc.doc);	
		   }
}