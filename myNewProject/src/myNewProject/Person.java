package myNewProject;

import java.util.Date;

public class Person implements Comparable {

	public String uniqueId;
	
	public String name;
	
	public Date birthday;
	
	public Date death;
	
	
	public String toString(){
		return "Person ID: " + uniqueId + " Name: " + name + "Birth: " + birthday + "Death: " + death;
	}

	public void checkBirthDeath(){
		if(birthday != null && death != null && death.before(birthday)){
			System.out.println("Died before birth!");
		}
	}
	
	@Override
	public int compareTo(Object o) {
		return this.uniqueId.compareTo(((Person)o).uniqueId);
	}
}
