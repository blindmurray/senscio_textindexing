import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONObject;
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
	String dataDir = "/Users/Gina/Documents/Files/GitHub/senscio_textindexing/test-projects";
	final POS[] pos = {POS.ADJECTIVE, POS.ADVERB, POS.NOUN, POS.VERB};
	public Searcher(){
	}
	public ArrayList<String> searchIndex(JSONObject json, String indexDir, int num) throws Exception {
		//convert searchterm to lowercase
		String searchString = json.getString("searchterm").toLowerCase();
		//get individual words in searchString
		String[] terms = searchString.split(" ");
		//use OR in parentheses for each term's synonyms
		String queryString = "";
		for (String term: terms){
			//find the synonyms
			ArrayList<String> synonyms = synonymfind(term);
			queryString += "(" + term;
			if(synonyms.size()>0){
				for(String syn: synonyms){
					queryString += " OR " + syn;
				}
			}
			queryString += ") AND ";
		}
		//remove the last "AND"
		queryString = queryString.substring(0, queryString.length()-5);
		System.out.println(queryString);
		String[] fields = {LuceneConstants.CONTENTS, LuceneConstants.FILE_NAME, LuceneConstants.FILE_TOKENS};
		//construct booleanquery
		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
		//assigns more weight/importance to titles
		HashMap<String,Float> boosts = new HashMap<String,Float>();
		boosts.put(LuceneConstants.CONTENTS, 1.0f);
		boosts.put(LuceneConstants.FILE_NAME, 2.0f);
		boosts.put(LuceneConstants.FILE_TOKENS, 2.5f);
		
		//multifieldqueryparser in order to search in title and contents of files
		MultiFieldQueryParser qp = new MultiFieldQueryParser(fields, new StandardAnalyzer(), boosts);	

		Query query = qp.parse(queryString);
		booleanQuery.add(query, BooleanClause.Occur.MUST);
		//Create Lucene searcher. It searches over a single IndexReader.
		IndexSearcher searcher = createSearcher(indexDir);

		//Search indexed contents using search term
		TopDocs foundDocs = searchInContent(searcher, booleanQuery, num);

		ArrayList<String> results = new ArrayList<String>();
		results.add("Total Results: "+ foundDocs.totalHits +"\n");
		//gets user input dates and extensions
		String dateFrom = json.getString("dateFrom").replace("-", "");
		String dateTo = json.getString("dateTo").replace("-", "");
		String extensions = json.getString("exten");
		//only add files that satisfy the extension and last modified date requirements
		if(!extensions.equals("")){
			String[] exts = extensions.split("\\.");
			System.out.print(exts[0]);
			for (ScoreDoc sd : foundDocs.scoreDocs) {
				Document d = searcher.doc(sd.doc);
				for(String ext: exts){
					if(ext.equals(TXT.getExtension(d.get(LuceneConstants.FILE_NAME)))){
						//if no dates specified
						if(dateFrom.length()==0 || dateTo.length()==0){
							StringBuffer sBuffer = new StringBuffer(d.get(LuceneConstants.FILE_PATH));
						    sBuffer = sBuffer.delete(0,68);
						    String s = sBuffer.toString().replaceAll(" ", "%25");
							results.add("File Name : "+ d.get(LuceneConstants.FILE_NAME) + ", Score : " + sd.score + "\n" + "<a href=\""+ s  + "\">      Download</a>" );
						}
						//check if it is between those dates
						else{
							File f = new File(d.get(LuceneConstants.FILE_PATH));
							LocalDate ldt = Instant.ofEpochMilli(f.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate();
							String date = Integer.toString(ldt.getYear());
							if(ldt.getMonthValue() < 10){
								date += "0";
							}
							date += Integer.toString(ldt.getMonthValue()) + ldt.getDayOfMonth();
							if(date.compareTo(dateFrom)>=0 && date.compareTo(dateTo)<=0){
								StringBuffer sBuffer = new StringBuffer(d.get(LuceneConstants.FILE_PATH));
							    sBuffer = sBuffer.delete(0,68);
							    String s = sBuffer.toString().replaceAll(" ", "%25");
								results.add("File Name : "+ d.get(LuceneConstants.FILE_NAME) + ", Score : " + sd.score + "\n" + "<a href=\""+ s  + "\">      Download</a>" );
							}
						}
					}
				}
			}
		}
		//if there isn't an extension requirement
		else{
			for(ScoreDoc sd: foundDocs.scoreDocs){
				Document d = searcher.doc(sd.doc);
				if(dateFrom.length()==0 || dateTo.length()==0){
					StringBuffer sBuffer = new StringBuffer(d.get(LuceneConstants.FILE_PATH));
				    sBuffer = sBuffer.delete(0,68);
				    String s = sBuffer.toString().replaceAll(" ", "%25");
					results.add("File Name : "+ d.get(LuceneConstants.FILE_NAME) + ", Score : " + sd.score + "\n" + "<a href=\""+ s  + "\">      Download</a>" );
				}
				else{
					File f = new File(d.get(LuceneConstants.FILE_PATH));
					LocalDate ldt = Instant.ofEpochMilli(f.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate();
					String date = Integer.toString(ldt.getYear());
					if(ldt.getMonthValue() < 10){
						date += "0";
					}
					date += Integer.toString(ldt.getMonthValue()) + ldt.getDayOfMonth();
					if(date.compareTo(dateFrom)>=0 && date.compareTo(dateTo)<=0){
						StringBuffer sBuffer = new StringBuffer(d.get(LuceneConstants.FILE_PATH));
					    sBuffer = sBuffer.delete(0,68);
					    String s = sBuffer.toString().replaceAll(" ", "%25");
						results.add("File Name : "+ d.get(LuceneConstants.FILE_NAME) + ", Score : " + sd.score + "\n" + "<a href=\""+ s  + "\">      Download</a>" );
					}
				}
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
	//find synonyms using wordnet database
	public ArrayList<String> synonymfind(String synword) {
		ArrayList<String> syns = new ArrayList<String>();
		try {
			//open dictionary
			URL url = new URL("file", null, "WordNet/2.1/dict");
			Dictionary dict = new Dictionary(url);
			try {
				dict.open();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			//add synonyms to list
			for(int x = 0; x < 4; x++){
				IIndexWord idxWord = dict.getIndexWord(synword, pos[x]);
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
			}
		} 
		catch (Exception e) {
		}
		return syns;
	}
}