package myNewProject;

public class Person implements Comparable {

	public String uniqueId;
	
	public String name;
	
	public String toString(){
		return "Person ID: "+uniqueId+" Name: "+name;
	}

	@Override
	public int compareTo(Object o) {
		return this.uniqueId.compareTo(((Person)o).uniqueId);
	}
}
