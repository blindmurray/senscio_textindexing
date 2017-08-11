import java.io.File;
public class FixDirectory {

	public static void main(String[] args){
		space(LuceneConstants.dataDir);
	}
		public static String space(String dir){
			File[] files = new File(dir).listFiles();
				
			//Check for file type and call appropriate method to convert the file.
			for (int i = 0; i < files.length; i++) {
					File f = files[i];
		        	if (f.isDirectory()){
		        		String s = f.toString();
		        		for (int j = 0; j < s.length(); j++){
		        		    char c = s.charAt(j);        
		        		    if(c == ' '){
		        		    	s = s.replace(c, '_');
		        		    }
		        		}
		        	File f1 = new File (s);
		        	f.renameTo(f1);
		        	System.out.println(f.toString());
		        		space(f.toString());
		        	}
		        	
		        	else{
			        		String s = f.toString();
			        		for (int j = 0; j < s.length(); j++){
			        		    char c = s.charAt(j);        
			        		    if(c == ' '){
			        		    	s = s.replace(c, '_');
			        		    }
			        		}
			        	File f1 = new File (s);
			        	f.renameTo(f1);
			        	System.out.println(f.toString());
			        	
		        	}
			}
		return null;
		}
	}
