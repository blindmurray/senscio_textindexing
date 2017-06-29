import java.io.*;
import java.net.*;
import java.nio.file.Paths;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

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

@SuppressWarnings("serial")
public class Client extends JPanel implements ActionListener{
	JTextArea incoming;
	JTextField outgoing;
	BufferedReader reader;
	PrintWriter writer;
	Socket sock;
	JScrollPane qScroller;
	JPanel mainPanel;
	JFrame frame;
	JButton searchButton;
	final int bufSize = 16384;
	JTextField textField;
	String errStr;
	double duration, seconds;
	File file;
	IndexSearcher indexSearcher;
	QueryParser queryParser;
	Query query2;
	IndexReader indexReader;


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Client client = new Client();
		client.start();
	}

	public void start(){
		frame = new JFrame("Search Page");
		mainPanel = new JPanel();
		incoming = new JTextArea(15, 40);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		outgoing = new JTextField(20);
		searchButton = new JButton("Send");
		searchButton.addActionListener(new searchButtonListener());
		outgoing.addKeyListener(new EnterListener());
		mainPanel.add(qScroller);
		mainPanel.add(outgoing);
		mainPanel.add(searchButton);
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		frame.setSize(600, 500);
		frame.setVisible(true);
		mainPanel.setBackground(Color.BLACK);
	}
public class searchButtonListener implements ActionListener{

public void actionPerformed(ActionEvent event){
	try{
		incoming.append("-----------------------------------\n");
		String indexDir = "/MICHELLE/txt_index";
		String dataDir = "/MICHELLE/txt_data";
		Indexer indexer = null;
			   
		File indexDirFile = new File(indexDir);
			   
		/*Call clean.java class to clear all of the files created in the index 
		 * directory from previous runs
		 */
		TextFileFilter.clear(indexDirFile);
			   
		//Call the Indexer.java file and create an indexer
		Indexer.createIndex(indexDir, dataDir, indexer);
			   
		//The string you are searching for in the files
		String querystr = outgoing.getText();
		   
		//Call Searcher class to search for the string
		searchIndex(querystr, indexDir);
	} catch (Exception ex){
		ex.printStackTrace();
	}
	scrollToBottom(qScroller);
	outgoing.setText("");
	outgoing.requestFocus();	
}
}
public class EnterListener implements KeyListener{

public void keyPressed(KeyEvent e) {		  
}
@Override

public void keyReleased(KeyEvent arg0) {

	// TODO Auto-generated method stub
	if (arg0.getKeyCode()==KeyEvent.VK_ENTER){ 
		try{
			incoming.append("-----------------------------------\n");
			String indexDir = "/MICHELLE/txt_index";
			String dataDir = "/MICHELLE/txt_data";
			Indexer indexer = null;
				   
			File indexDirFile = new File(indexDir);
			   
			/*Call clean.java class to clear all of the files created in the index 
			 * directory from previous runs
			 */
			TextFileFilter.clear(indexDirFile);
				   
			//Call the Indexer.java file and create an indexer
			Indexer.createIndex(indexDir, dataDir, indexer);
				   
			//The string you are searching for in the files
			String querystr = outgoing.getText();
				   
			//Call Searcher class to search for the string
			searchIndex(querystr, indexDir);
		} catch (Exception ex){
			ex.printStackTrace();
		}
	
		scrollToBottom(qScroller);
		outgoing.setText("");
		outgoing.requestFocus();	
	}
}

@Override
public void keyTyped(KeyEvent arg0) {
	// TODO Auto-generated method stub
}
}

private void scrollToBottom(JScrollPane scrollPane) {
    JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
    AdjustmentListener downScroller = new AdjustmentListener() {
        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            Adjustable adjustable = e.getAdjustable();
            adjustable.setValue(adjustable.getMaximum());
            verticalBar.removeAdjustmentListener(this);
        }
    };
    verticalBar.addAdjustmentListener(downScroller);
}

public void searchIndex(String searchString, String indexDir) throws Exception {
	String index = indexDir;
	//Create Lucene searcher. It search over a single IndexReader.
	IndexSearcher searcher = createSearcher(index);
	
	//Search indexed contents using search term
	TopDocs foundDocs = searchInContent(searchString, searcher);
         
	//Total found documents
	incoming.append("Total Results: " + foundDocs.totalHits +"\n");
         
	//Print out the path of files which have searched term
	for (ScoreDoc sd : foundDocs.scoreDocs) {
		Document d = searcher.doc(sd.doc);
		incoming.append("Path : "+ d.get(LuceneConstants.FILE_NAME) + ", Score : " + sd.score+"\n");
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

public void actionPerformed(ActionEvent e) {
	// TODO Auto-generated method stub
}

  /**
   * Reads data from the input channel and writes to the output stream
   */
}