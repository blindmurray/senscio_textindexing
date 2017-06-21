import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class TXT{
	
public static void createTXT(String content, String filePath){
<<<<<<< HEAD
	
	File f = null;
	      boolean bool = false;
	      try {
	    	 filePath = editExtension(filePath);
	    	 System.out.println(filePath);
	         // create new file
	    	  f = new File(filePath);
	         // tries to create new file in the system
	         bool = f.createNewFile();
	         // prints
	         System.out.println("File created: "+bool);
=======
	File f = null;
	boolean bool = false;
	try {
		// calls editExtension method
		filePath = editExtension(filePath);
		System.out.println(filePath);
>>>>>>> 54c0ab9556e285be32c3de7810662e76eda18c95
	         
		// create new file
		f = new File(filePath);
	         
		// tries to create new file in the system
		bool = f.createNewFile();
	         
		// prints
		System.out.println("File created: "+bool);
	         
		FileOutputStream fos = new FileOutputStream(f);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	         
		bw.write(content);
		bw.close();
	         
	}
	catch(Exception e) {
		e.printStackTrace();
	}
}

public static String editExtension(String file){
    //Splits file path by "/"
	String[] tokens = file.split("/");
	
	//Split name of document if there are more than one "."
    String[] name = tokens[tokens.length-1].split("\\.");
    String docName = null;
    
    //Find the document's name without the file
    for(int i=0;i<tokens.length;i++){
    	if(i==0){
    		file = tokens[i];
    	}
    	else if(i==tokens.length-1){
    		for(int n=0; n<name.length;n++){
    			if(n==name.length-1){
    				break;
    			}
    			else if(n==0&&name.length==2){
    				docName=name[0];
    			}
    			else if(n==0){
    				docName=name[0];
    			}
    			else if(n==name.length-2){
    				docName = docName +name[n];
    			}
    			else{
    				docName=docName + name[n]+".";
    			}
    		}
    		file = file + "/"+ docName +".txt";
    	    break;
    	    }
    	else{
    		file = file +"/"+ tokens[i];
    	}
    }
    return file;
}

public static String getExtension(String file){
	
	//replaces previous extension with a .txt extension
    String[] tokens = file.split("/");
    String[] name = tokens[tokens.length-1].split("\\.");
    String ext = null;
    for(int i=0;i<tokens.length;i++){
    	if(i==tokens.length-1){
    		for(int n=0; n<name.length;n++){
    			if(n==name.length-1)
    				ext = name[name.length-1];
    		}
    	}
    }
    return ext;
	}
}