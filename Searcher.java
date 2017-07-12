import java.io.IOException;
import java.net.URL;
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

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;

public class Searcher{

	IndexSearcher indexSearcher;
	QueryParser queryParser;
	Query query2;
	IndexReader indexReader;

	public Searcher(){

	}
	public ArrayList<String> searchIndex(String searchString, String indexDir, int num, String extensions) throws Exception {
		searchString = searchString.toLowerCase();
		String[] terms = searchString.split(" ");
		//use OR in parentheses for each term's synonyms
		String queryString = "";
		for (String term: terms){
			ArrayList<String> synonyms = synonymfind(term);
			queryString += "(" + term;
			if(synonyms.size()>0){
				for(String syn: synonyms){
					queryString += " OR " + syn;
				}
			}
			queryString += ") AND ";
		}
		
		queryString = queryString.substring(0, queryString.length()-5);
		System.out.println(queryString);
		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
		QueryParser qp = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer());	

		Query query = qp.parse(queryString);
		booleanQuery.add(query, BooleanClause.Occur.MUST);
		//Create Lucene searcher. It searches over a single IndexReader.
		IndexSearcher searcher = createSearcher(indexDir);

		//Search indexed contents using search term
		TopDocs foundDocs = searchInContent(searcher, booleanQuery, num);

		//Total found documents

		ArrayList<String> results = new ArrayList<String>();
		results.add("Total Results: "+ foundDocs.totalHits +"\n");
		//Print out the path of files which have searched term
		System.out.println(extensions.length());
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
		System.out.println(results);
		return results;
	}

	private static TopDocs searchInContent(IndexSearcher searcher, BooleanQuery.Builder booleanQuery, int num) throws Exception{
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
	public ArrayList<String> synonymfind(String synword) {
		ArrayList<String> syns = new ArrayList<String>();
		try {

	        URL url = new URL("file", null, "WordNet/2.1/dict");


	        Dictionary dict = new Dictionary(url);
	        try {
	            dict.open();
	        } catch (IOException ex) {
	            ex.printStackTrace();

	        }

	        IIndexWord idxWord = dict.getIndexWord(synword, POS.NOUN);
	        for(int i = 0; i<idxWord.getWordIDs().size(); i++){
	        	IWordID wordID = idxWord.getWordIDs().get(i);
	        	IWord word = dict.getWord(wordID);
	        	ISynset synset = word.getSynset();
		        for (IWord w : synset.getWords()) {
		            if(!w.getLemma().equals(synword)){
		        		syns.add(w.getLemma());
		            }
		        }
	        }
	        
	        idxWord = dict.getIndexWord(synword, POS.ADJECTIVE);
	        for(int i = 0; i<idxWord.getWordIDs().size(); i++){
	        	IWordID wordID = idxWord.getWordIDs().get(i);
	        	IWord word = dict.getWord(wordID);
	        	ISynset synset = word.getSynset();
		        for (IWord w : synset.getWords()) {
		            if(!w.getLemma().equals(synword)){
		        		syns.add(w.getLemma());
		            }
		        }
	        }
	        
	        idxWord = dict.getIndexWord(synword, POS.ADVERB);
	        for(int i = 0; i<idxWord.getWordIDs().size(); i++){
	        	IWordID wordID = idxWord.getWordIDs().get(i);
	        	IWord word = dict.getWord(wordID);
	        	ISynset synset = word.getSynset();
		        for (IWord w : synset.getWords()) {
		            if(!w.getLemma().equals(synword)){
		        		syns.add(w.getLemma());
		            }
		        }
	        }//Adding Related Words to List of Related Words
	        
	        idxWord = dict.getIndexWord(synword, POS.VERB);
	        for(int i = 0; i<idxWord.getWordIDs().size(); i++){
	        	IWordID wordID = idxWord.getWordIDs().get(i);
	        	IWord word = dict.getWord(wordID);
	        	ISynset synset = word.getSynset();
		        for (IWord w : synset.getWords()) {
		            if(!w.getLemma().equals(synword)){
		        		syns.add(w.getLemma());
		            }
		        }
	        }

	    } catch (Exception e) {
	    }
	    return syns;
	}
}