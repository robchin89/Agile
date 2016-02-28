package myNewProject;

import java.util.Date;
import java.util.List;

public class Family implements Comparable{

	public String uniqueId;
	
	public Person husband;
	
	public Person wife;
	
	public List<Person> children;
	
	public String toString(){
		return "Family ID: "+uniqueId+" Husband: "+husband.name+" Wife: "+wife.name;
	}
	

	@Override
	public int compareTo(Object o) {
		return this.uniqueId.compareTo(((Family)o).uniqueId);
	}
	
	//Check for more than 4 children having the same birth date
	public Boolean checkMultipleBirths(){
		//If there are any children check if 5 or more have have the same birthday
		if(children != null){
			//Check to see if there are more than 4 children
			if(children.size() > 4){
				//Check the first child's birthday against all of the others
				for(int i = 0; i < children.size(); i++){
					int matchingBirthdays = 0;
					//Grab the birthday of the ith child
					Date ithBirthday = children.get(i).birthday;
					//Check rest of the birthdays against ith child
					for(int j = 0; j < children.size(); j++){
						if(children.get(j).birthday.equals(ithBirthday)){
							matchingBirthdays++;
						}
					}
					//System.out.println("Matching Birthdays: "+matchingBirthdays);
					//If more than 4 children share the same birthday return false
					if(matchingBirthdays > 4){
						return false;
					}
				}
			}
		}
		return true;
	}
	
	//Check for the correct surname
	public Boolean checkMaleLastNames(){
		//Determine the Family Name from the father
		String famName = husband.getLastName();
		//System.out.println(famName);
		//If there are any children check the male last name for the Family Name
		if(children != null){
			for(int i = 0; i < children.size(); i++){
				//System.out.println(family.children.get(i).gender);
				if(children.get(i).gender.equals("M")){
					//If the child's last name does not match the Family Name return false
					if(!children.get(i).getLastName().equals(famName)){
						return false;
					}
				}
			}
		}
		return true;
	}
}
