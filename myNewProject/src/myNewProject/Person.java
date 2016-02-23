package myNewProject;

import java.util.Date;

public class Person implements Comparable {

	public String uniqueId;
	
	public String name;
	
	public Date birthday;
	
	public Date death;
	
	public Date marriage; 
	
	public Date divorce;
	
	public String spouse;
	
	public String toString(){
		String ret = "Person ID: " + uniqueId + " Name: " + name + "Birth: " + birthday;
		if(death != null){
			ret += " Death: " + death;
		}
		if(marriage != null){
			ret += " Married: " + marriage;
		}
		return ret;
	}

	public void checkBirthBeforeDeath(){
		if(birthday != null && death != null && death.before(birthday)){
			System.out.println(name + " died before birth!");
		}
	}
	
	public void checkMarriageBeforeDeath(){
		if(death != null && marriage != null && death.before(marriage)){
			System.out.println(name + " married before birth!");
		}
	}
	

	
	//check birth before marriage
	public void checkBirthBeforeMarriage()
	{
		if(birthday != null && marriage != null && birthday.before(marriage)){
			System.out.println(name + " birth before marriage!");
		}
	}
	
	@Override
	public int compareTo(Object o) {
		return this.uniqueId.compareTo(((Person)o).uniqueId);
	}
}
