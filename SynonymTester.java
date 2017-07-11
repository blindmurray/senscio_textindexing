import java.util.ArrayList;

public class SynonymTester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Searcher s = new Searcher();
		s.synonymfind("smart");
		try {
			ArrayList<String> results = s.searchIndex("application machine", "C:\\MICHELLE\\txt_index", 10, "");
			System.out.print(results);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
