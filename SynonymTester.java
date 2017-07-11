import java.util.ArrayList;

public class SynonymTester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Searcher s = new Searcher();
		try {
			ArrayList<String> results = s.searchIndex("declaration", "C:\\MICHELLE\\txt_index", 10, "");
			//prints out the results that contain "contract"
			System.out.print(results);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
