import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

/**
 * The Searcher class instantiates an IndexSearcher, QueryParser, IndexReader, and an array. The Searcher
 * class makes it possible for users to search the index. The "public" folder is public to all people viewing
 * the website. Otherwise, the Searcher class will check permissions.
 * @author Gina
 *
 */
public class Searcher{
	IndexSearcher indexSearcher;
	QueryParser queryParser;
	IndexReader indexReader;
	final POS[] pos = {POS.ADJECTIVE, POS.ADVERB, POS.NOUN, POS.VERB};
	
	/**
	 * The searchIndex function is triggered from the Server_Socket. It will search the indexed files for relevant
	 * search results, taking into account synonyms and givng a boost if the search term matches a key term or a
	 * file name. 
	 * @param json JSON object sent from the searcher with information about the particular query.
	 * @param indexDir The directory where the index of the files are stored.
	 * @param number Number of hits
	 * @param email Email of the user
	 * @return An ArrayList of strings with the search results in HTML
	 * @throws Exception
	 */
	public ArrayList<String> searchIndex(JSONObject json, String indexDir, String number, String email) throws Exception {

		String searchString = json.getString("searchterm").toLowerCase();       //convert searchterm to lowercase
		String[] terms = searchString.split(" ");                               //get individual words in searchString
		String queryString = "";                                                //use OR in parentheses for each term's synonyms
		for (String term: terms){                                               //find the synonyms
			ArrayList<String> synonyms = synonymfind(term);
			queryString += "(" + term;
			if(synonyms.size()>0){
				for(String syn: synonyms){
					queryString += " OR " + syn;
				}
			}
			queryString += ") AND ";
		}
		queryString = queryString.substring(0, queryString.length()-5);         //remove the last "AND"
		String[] fields = {Constants.CONTENTS, Constants.FILE_NAME, Constants.FILE_TOKENS};
		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();         //construct booleanquery
		HashMap<String,Float> boosts = new HashMap<String,Float>();             //assigns more weight/importance to titles
		boosts.put(Constants.CONTENTS, 1.0f);
		boosts.put(Constants.FILE_NAME, 2.0f);
		boosts.put(Constants.FILE_TOKENS, 2.0f);

		//multifieldqueryparser in order to search in title and contents of files
		MultiFieldQueryParser qp = new MultiFieldQueryParser(fields, new StandardAnalyzer(), boosts);

		Query query = qp.parse(queryString);
		booleanQuery.add(query, BooleanClause.Occur.MUST);
		IndexSearcher searcher = createSearcher(indexDir);                      //Create Lucene searcher. It searches over a single IndexReader.
		TopDocs found = searchInContent(searcher, booleanQuery, 20);    		//Search indexed contents using search term
		TopDocs foundDocs;
		if(number.equals("")){
			foundDocs = searchInContent(searcher,booleanQuery, 20);
		}
		else{
			int num = Integer.parseInt(number);
			if(found.totalHits < num){
				foundDocs = searchInContent(searcher,booleanQuery, found.totalHits);
			}
			else{
				foundDocs = searchInContent(searcher,booleanQuery, num);
			}
		}
		//Creates an Array for the results and an Array and counter for the total hits.
		ArrayList<String> results = new ArrayList<String>();
		ArrayList<String> res = new ArrayList<String>();
		int counter = 0;

		//Gets user input dates and extensions
		String dateFrom = json.getString("dateFrom").replace("-", "");
		String dateTo = json.getString("dateTo").replace("-", "");
		String extensions = json.getString("exten");

		//Only add files that satisfy the extension and last modified date requirements
		if(!extensions.equals("")){
			String[] exts = extensions.split("\\.");
			for (ScoreDoc sd : foundDocs.scoreDocs) {
				Document d = searcher.doc(sd.doc);
				File temp = new File(d.get(Constants.FILE_PATH));
				if(temp.getPath().startsWith(Constants.dataDir + "/public") || checkPermission(temp.getParent(), email)){
					for(String ext: exts){
						if(ext.equals(FileNames.getExtension(d.get(Constants.FILE_NAME)))){
							//If no dates specified
							if(dateFrom.length()==0 || dateTo.length()==0){
								String s = changePath(d);
								counter++;
								results.add("<a href=\""+ s  + "\"> <img src=\"/images/download.jpg\"> </a>&nbsp&nbsp <b>"+ d.get(Constants.FILE_NAME) + "</b>\n <br> &nbsp" + "<i>"+d.get(Constants.FILE_PREVIEW)+ "</i>\n <br>");
							}
							//Check if it is between those dates
							else{
								File f = new File(d.get(Constants.FILE_PATH));
								LocalDate ldt = Instant.ofEpochMilli(f.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate();
								String date = Integer.toString(ldt.getYear());
								if(ldt.getMonthValue() < 10){
									date += "0";
								}
								date += Integer.toString(ldt.getMonthValue()) + ldt.getDayOfMonth();
								if(date.compareTo(dateFrom)>=0 && date.compareTo(dateTo)<=0){
									String s = changePath(d);
									counter++;
									results.add("<a href=\""+ s  + "\"> <img src=\"/images/download.jpg\"> </a>&nbsp&nbsp <b>"+ d.get(Constants.FILE_NAME) + "</b>\n <br> &nbsp" + "<i>"+d.get(Constants.FILE_PREVIEW)+ "</i>\n <br>");
								}
							}
						}
					}
				}
			}
		}
		//If there isn't an extension requirement
		else{
			for(ScoreDoc sd: foundDocs.scoreDocs){
				Document d = searcher.doc(sd.doc);
				File temp = new File(d.get(Constants.FILE_PATH));
				//Checks if the user has permission to view those files
				if(temp.getPath().startsWith(Constants.dataDir + "/public") || checkPermission(temp.getParent(), email)){
					if(dateFrom.length()==0 || dateTo.length()==0){
						String s = changePath(d);
						counter++;
						results.add("<a href=\""+ s  + "\"> <img src=\"/images/download.jpg\"> </a>&nbsp&nbsp <b>"+ d.get(Constants.FILE_NAME) + "</b>\n <br> &nbsp" + "<i>"+d.get(Constants.FILE_PREVIEW)+ "</i>\n <br>");
					}
					else{
						File f = new File(d.get(Constants.FILE_PATH));
						LocalDate ldt = Instant.ofEpochMilli(f.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate();
						String date = Integer.toString(ldt.getYear());
						if(ldt.getMonthValue() < 10){
							date += "0";
						}
						date += Integer.toString(ldt.getMonthValue()) + ldt.getDayOfMonth();
						if(date.compareTo(dateFrom)>=0 && date.compareTo(dateTo)<=0){
							String s = changePath(d);
							counter++;
							results.add("<a href=\""+ s  + "\"> <img src=\"/images/download.jpg\"> </a>&nbsp&nbsp <b>"+ d.get(Constants.FILE_NAME) + "</b>\n <br> &nbsp" + "<i>"+d.get(Constants.FILE_PREVIEW)+ "</i>\n <br>");
						}
					}
				}
			}
		}
		res.add("Total Results: "+ counter +"\n");
		res.addAll(results);
		return res;
	}
	/**
	 * Creates the hits relevant to the search results
	 * @param searcher Searcher
	 * @param booleanQuery Query
	 * @param num Number of hits to render
	 * @return TopDocs with hits
	 * @throws Exception
	 */
	private static TopDocs searchInContent(IndexSearcher searcher, BooleanQuery.Builder booleanQuery, int num) throws Exception{
		TopDocs hits = searcher.search(booleanQuery.build(), num);              //Search the index
		return hits;
	}
	/**
	 * Creates a Searcher to be used.
	 * @param indexDir The path of the index that is being searched
	 * @return Searcher object
	 * @throws IOException
	 */
	private static IndexSearcher createSearcher(String indexDir) throws IOException {
		Directory dir = FSDirectory.open(Paths.get(indexDir));
		IndexReader reader = DirectoryReader.open(dir);         //Interface for accessing a point-in-time view of a Lucene index
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}
	
	/**
	 * Changes the path of the document by removing part of its path. This function is used later on
	 * in the download on the repository page.
	 * @param d The Docuement whose file path is being edited. 
	 * @return
	 */
	private static String changePath(Document d){
		StringBuffer sBuffer = new StringBuffer(d.get(Constants.FILE_PATH));
		String str= sBuffer.toString().replace(Constants.dataDir, "/files");
		return str;
	}
	/**
	 * Finds synonyms using the WordNet database
	 * @param synword Word that needs synonyms 
	 * @return ArrayList of synonyms
	 */
	public ArrayList<String> synonymfind(String synword) {
		ArrayList<String> syns = new ArrayList<String>();
		try {
			URL url = new URL("file", null, "WordNet/2.1/dict");//open dictionary
			Dictionary dict = new Dictionary(url);
			try {
				dict.open();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			//Add synonyms to list
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
	
	/**
	 * Checks if the user has permission to view the file by checking the MySQL database
	 * @param path Filepath attempting to be viewed
	 * @param email Email of the user
	 * @return Boolean true/false
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static boolean checkPermission(String path, String email) throws ClassNotFoundException, SQLException{
		//All users by default have access to the public database
		if(path.startsWith(Constants.dataDir + "/public")){
			return true;
		}
		//If the email field isn't left empty
		else if(!email.isEmpty()){
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(Constants.url, Constants.username, Constants.password);
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT `" + email + "` FROM indexer.permissions WHERE `folderpath` = '" + path + "'");
			if(rs.absolute(1) && rs.getInt(email)>=1){
				return true;
			}
			else{
				return false;
			}
		}
		//If the email field is left empty
		else{
		return false;
		}
	}
}