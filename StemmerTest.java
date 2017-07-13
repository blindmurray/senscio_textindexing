import org.tartarus.snowball.ext.EnglishStemmer;

public class StemmerTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EnglishStemmer stemmer = new EnglishStemmer();
		stemmer.setCurrent("independent");
		stemmer.stem();
		System.out.println(stemmer.getCurrent());
	}

}
