package myNewProject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.lang.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ReadGED {

	private static Date parseDate(String date){
		try{
        	DateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        	return df.parse(date);
		} catch(ParseException e){
        		System.out.println("Could not parse date!");
        }
		return null;
	}
	
    public static void main(String[] args) {
    	//test
    	try {
	    	File gedFile = new File("~/../../gedFile.ged");
	        Scanner fileReader = new Scanner(gedFile);
	
	        String[] tags = {"INDI", "NAME","SEX","BIRT","DEAT","FAMC","FAMS","FAM","MARR","HUSB","WIFE","CHIL","DIV","DATE","HEAD","TRLR","NOTE"};
	        String level;
	        String tag;
	        Boolean validTag = null;
	        String value;

	        List<Person> Individuals = new ArrayList<Person>();
	        List<Family> Families = new ArrayList<Family>();
	        
	        Family family = null;
	        Person person = null;
	        
	        boolean lastIndividual = true;
	        
	        while(fileReader.hasNext()){
	            //Read in level
	            level = fileReader.next();
	
	            //Read tag
	            tag = fileReader.next();
	
	            //Read in the rest of the line - get rid of leading space.
	            value = fileReader.nextLine().trim();
	            
	            //Print out the information
	            System.out.println(level + " " + tag + " " + value);
	            System.out.println("Level: " + level);
	
	            if(level.equals("0") && value.equals("INDI")){
	            	if(person == null){
	            		person = new Person();
	            		person.uniqueId = tag;
	            	} else {
	            		Individuals.add(person);
	            		person = new Person();
	            		person.uniqueId = tag;
	            	}
	            }
	            
	            if(level.equals("1") && tag.equals("NAME")){
	            	person.name = value;
	            }
	            
	            if(level.equals("1") && tag.equals("BIRT")){
		            level = fileReader.next();
		            tag = fileReader.next();
		            value = fileReader.nextLine().trim();
		            if(level.equals("2") && tag.equals("DATE")){
		            	person.birthday = parseDate(value);
		            }
	            }

	            if(level.equals("1") && tag.equals("DEAT") && value.equals("Y")){
		            level = fileReader.next();
		            tag = fileReader.next();
		            value = fileReader.nextLine().trim();
		            if(level.equals("2") && tag.equals("DATE")){
		            	person.death = parseDate(value);
		            }
	            }
	            
	            if(level.equals("0") && value.equals("FAM")){
	            	if(lastIndividual){
	            		Individuals.add(person);
	            		lastIndividual = false;
	            	}
	            	
	            	if(family == null){
	            		family = new Family();
	            		family.uniqueId = tag;
	            		family.children = new ArrayList<Person>();
	            	} else {
	            		Families.add(family);
	            		family = new Family();
	            		family.uniqueId = tag;
	            		family.children = new ArrayList<Person>();
	            	}
	            }
	            
	            if(tag.equals("WIFE") || tag.equals("CHIL") || tag.equals("HUSB")){
	            	int end = value.lastIndexOf("@");
	            	String uniqueId = value.substring(2, end);
	            	int arrayIndex = Integer.parseInt(uniqueId) - 1;
	            	if(tag.equals("WIFE")){
	            		family.wife = Individuals.get(arrayIndex);
	            	} else if(tag.equals("CHIL")){
	            		family.children.add(Individuals.get(arrayIndex));
	            	} else {
	            		family.husband = Individuals.get(arrayIndex);
	            	}
	            }
	            
	            if(level.equals("1") && tag.equals("MARR")){
	            	level = fileReader.next();
		            tag = fileReader.next();
		            value = fileReader.nextLine().trim();
		            if(level.equals("2") && tag.equals("DATE")){
		            	Date marriage = parseDate(value);
		            	family.wife.marriage = marriage;
		            	family.husband.marriage = marriage;
		            }
	            }
	            
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
	        
	        Families.add(family);
	        // Done reading
	        fileReader.close();
	        System.out.println("Individuals:");
	        //Individuals.sort(null);
	        
	        for(int i = 0; i < Individuals.size();i++){
	        	Person individual = Individuals.get(i);
	        	System.out.println(individual);
	        	individual.checkBirthBeforeDeath();
	        	individual.checkMarriageBeforeDeath();
	        }
	        
	        System.out.println("Families:");
	        //Families.sort(null);
	        for(int i = 0; i < Families.size();i++){
	        	System.out.println(Families.get(i));
	        }
    	}
    	catch(IOException ex) {
    		   // there was some connection problem, or the file did not exist on the server,
    		   // or your URL was not in the right format.
    		   // think about what to do now, and put it here.
    		   ex.printStackTrace(); // for now, simply output it.
    		}
        
    } 
    
}