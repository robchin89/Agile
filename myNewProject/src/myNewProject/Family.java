package myNewProject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Years;

public class Family implements Comparable{

	public String uniqueId;
	
	public Person husband;
	
	public Person wife;
	
	public List<Person> children;
	
	public Date divorce;
	
	public Date marriage;
	
	public String toString(){
		return "Family ID: "+ uniqueId +" Husband: "+husband.name+" Wife: "+wife.name;
	}
	

	@Override
	public int compareTo(Object o) {
		return this.uniqueId.compareTo(((Family)o).uniqueId);
	}
	
	
	public void checkDivorceBeforeDeath(){
		if(divorce != null){
			if((husband.death != null && divorce.after(husband.death)) || (wife.death != null && divorce.after(wife.death))){
				System.out.println("Divorce occurred after death of partner");
			} 
		}
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
						if(children.get(j).birthday != null && ithBirthday != null){
							if(children.get(j).birthday.equals(ithBirthday)){
								matchingBirthdays++;
							}
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
	
	//US37 - List Survivors
	public static <list> void ListSurvivors(List<Family> Families, List<Person> Individuals){
		for (int i = 0; i < Families.size();i++){
			Date hdt = Families.get(i).husband.death;
			Date wdt =  Families.get(i).wife.death;
			if (!(hdt == null)) {
				if((wdt==null)){
					//List survivors including spouse
					String wnm = Families.get(i).wife.name;
					for (int y=0; y< Families.get(i).children.size();y++ ){
						String cnm2 = Families.get(i).children.get(y).name;
						System.out.println("Surviving children " + cnm2);
					}
					System.out.println("Surviving spouse: "+wnm);
				}
				else{
					//List survivors excluding wife.
					
					for (int y=0; y< Families.get(i).children.size();y++ ){
						String cnm2 = Families.get(i).children.get(y).name;
						System.out.println("Surviving children " + cnm2);
					}
					
				}
			}
		}
		
	}
	
	//US30-Surviving couple
	public static <list> void ListSurvingCouples(List<Family> Families, List<Person> Individuals){
		for (int i = 0; i < Families.size();i++){
			Date hdt = Families.get(i).husband.death;
			Date wdt =  Families.get(i).wife.death;
			
			if ((hdt== null)) {
				if((wdt==null)){
					//List surviving couples name
					String h = Families.get(i).husband.name;
					String w = Families.get(i).wife.name;
	     			System.out.println("Living couples: "+h + "-" + w);
				}
			}				
		}
	}
		
		
	
	
	public void printChildrenByAge(){
		System.out.println("\nChildren by age:");
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		ArrayList<Integer> nullBirthdays = new ArrayList<Integer>();
		for(int i = 0; i < children.size(); i++){
			int oldest = Integer.MIN_VALUE;
			int oldestIndex = -1;
			if(children.get(i).birthday != null){
				for(int j = 0; j < children.size(); j++){
					if(!indexes.contains(j) && children.get(j).getAge() > oldest){
						oldest = children.get(j).getAge();
						oldestIndex = j;
					}
				}
				indexes.add(oldestIndex);
				System.out.println(children.get(oldestIndex) + " Age: " +children.get(oldestIndex).getAge());
			
				//Sprint 3 - check if children birthday is before parent's marriage date
			
				if(husband.marriage != null){
					if(children.get(oldestIndex).birthday.before(husband.marriage)){
						System.out.println("-Child was born before parents were married");
					}
				}
			
				//end
			
				//Sprint 3 - check if child's bday is before any parent death
				if(husband.death != null){ 
					if(children.get(oldestIndex).birthday.after(husband.death)){		
						System.out.println("-Child born after death of father");
					}			
				}	
				if(wife.death != null){ 
					if(children.get(oldestIndex).birthday.after(wife.death)){	
						System.out.println("-Child born after death of mother");
					}			
				}
				//end
			}else{
				nullBirthdays.add(i);
			}
		}
		for(int i = 0; i < nullBirthdays.size(); i++){
			System.out.println(children.get(nullBirthdays.get(i)) + " has no birthdate, cannot determine age.");
		}
	}
	
	public void checkMaxSiblings(){
		if(this.children.size() >= 15){
			System.out.println("Too many siblings: " + this.children.size());
		}
	}
	
	//US12 - Check that the parents should not be too told. 
		public  Boolean checkParentsAge(){
			
			
			return false;
		}
		
		public void uniqueChildrenNames(){
			for(int i = 0; i < children.size(); i++){
				for(int j = 0; j < children.size(); j++){
					if(i != j){
						if(children.get(i).name.equals(children.get(j).name)&& children.get(i).birthday.equals(children.get(j).birthday)){
							System.out.println("Duplicate Child: "+children.get(i).name+" and "+children.get(j).name + " in family "+uniqueId+" have the same name and birthday.");
						}
					}
				}
			}
		}

	public void checkLargeAgeDifference(){
		if(marriage != null && husband.birthday != null && wife.birthday != null){
			DateTime jodaMarriage = new DateTime(marriage);
			int husbandAgeAtMarriage = Years.yearsBetween(new DateTime(husband.birthday), jodaMarriage).getYears();
			System.out.println("Husband age at marriage: "+husbandAgeAtMarriage);
			int wifeAgeAtMarriage = Years.yearsBetween(new DateTime(wife.birthday), jodaMarriage).getYears();
			System.out.println("Wife age at marriage: "+wifeAgeAtMarriage);
			
			if((husbandAgeAtMarriage) > wifeAgeAtMarriage*2){
				System.out.println(husband.name + " was more than twice as old as " + wife.name + " during marriage");
			}
			
			if((wifeAgeAtMarriage) > husbandAgeAtMarriage*2){
				System.out.println(wife.name + " was more than twice as old as " + husband.name + " during marriage ");
			}		
		}
	}
	
	//US 32 List Multiple Births
	public void listMultipleBirths(){
		ArrayList<Integer> multipleBirths = new ArrayList<Integer>();
		ArrayList<Integer> birthIndices;
		for(int i = 0; i < children.size(); i++){
			birthIndices = new ArrayList<Integer>();
			for(int j = 0; j < children.size(); j++){
				if(i != j && children.get(i).birthday != null && children.get(j).birthday != null){
					if(!multipleBirths.contains(j) && children.get(i).birthday.equals(children.get(j).birthday)){
						birthIndices.add(j);
						multipleBirths.add(j);
					}
				}
			}
			if(!birthIndices.isEmpty()){
				multipleBirths.add(i);
				System.out.println("Family "+uniqueId+ " has multiple births. The following children are born on the same day:");
				System.out.println(children.get(i));
				for(int j = 0; j < birthIndices.size(); j++){
					System.out.println(children.get(birthIndices.get(j)));
				}
			}
		}
	}
	
	public boolean sameUniqueId(Family family){
		if(uniqueId.equals(family.uniqueId)){
			return true;
		}
		return false;
	}
	
}
