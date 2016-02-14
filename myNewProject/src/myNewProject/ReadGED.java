package myNewProject;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ReadGED {

    public static void main(String[] args) {
    	
    	try {
	    	File gedFile = new File("~/../../gedFile.ged");
	        Scanner fileReader = new Scanner(gedFile);
	
	        String[] tags = {"INDI", "NAME","SEX","BIRT","DEAT","FAMC","FAMS","FAM","MARR","HUSB","WIFE","CHIL","DIV","DATE","HEAD","TRLR","NOTE"};
	        String level;
	        String tag;
	        Boolean validTag;
	        String value;

	        while(fileReader.hasNext()){
	            //Read in level
	            level = fileReader.next();
	
	            //Read tag
	            tag = fileReader.next();
	
	            //Read in the rest of the line
	            value = fileReader.nextLine();
	
	            //Print out the information
	            System.out.println(level+" "+tag+" "+value);
	            System.out.println("Level: " + level);
	
	            //Check for invalid tag
	            validTag = false;
	            for(int i = 0; i < tags.length; i++){
	                if(tag.equals(tags[i])){
	                    validTag = true;
	                }
	            }
	            if(!validTag){
	                tag = "Invalid tag";
	            }
	            System.out.println("Tag: " + tag);
	        }
	        
	        // Done reading
	        fileReader.close();
    	}
    	catch(IOException ex) {
    		   // there was some connection problem, or the file did not exist on the server,
    		   // or your URL was not in the right format.
    		   // think about what to do now, and put it here.
    		   ex.printStackTrace(); // for now, simply output it.
    		}
        
    } 
    
}