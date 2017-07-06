import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
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

	public Searcher(){

	}
	public ArrayList<String> searchIndex(String searchString, String indexDir, int num, String extensions) throws Exception {
		searchString = searchString.toLowerCase();
		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
		QueryParser qp = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer());	

		Query query = qp.parse(searchString);
		booleanQuery.add(query, BooleanClause.Occur.MUST);
		//Create Lucene searcher. It searches over a single IndexReader.
		IndexSearcher searcher = createSearcher(indexDir);

		//Search indexed contents using search term
		TopDocs foundDocs = searchInContent(searchString, searcher, booleanQuery, num);

		//Total found documents

		ArrayList<String> results = new ArrayList<String>();
		results.add("Total Results: "+ foundDocs.totalHits +"\n");
		//Print out the path of files which have searched term
		if(!extensions.equals("")){
			String[] exts = extensions.split("\\.");
			System.out.print(exts[0]);
			for (ScoreDoc sd : foundDocs.scoreDocs) {
				Document d = searcher.doc(sd.doc);
				for(String ext: exts){
					if(ext.equals(TXT.getExtension(d.get(LuceneConstants.FILE_NAME)))){
						results.add("Path : "+ d.get(LuceneConstants.FILE_NAME) + ", Score : " + sd.score + "\n");
						//System.out.println("Path : "+ d.get(LuceneConstants.FILE_NAME) + ", Score : " + sd.score + "\n");
					}
				}
			}
		}
		else{
			for(ScoreDoc sd: foundDocs.scoreDocs){
				Document d = searcher.doc(sd.doc);
				results.add("Path : "+ d.get(LuceneConstants.FILE_NAME) + ", Score : " + sd.score + "\n");
			}
		}
		return results;
	}

	private static TopDocs searchInContent(String textToFind, IndexSearcher searcher, BooleanQuery.Builder booleanQuery, int num) throws Exception{
		//Search the index
		TopDocs hits = searcher.search(booleanQuery.build(), num);
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