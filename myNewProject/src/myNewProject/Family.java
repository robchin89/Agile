package myNewProject;

import java.util.List;

public class Family implements Comparable{

	public String uniqueId;
	
	public Person husband;
	
	public Person wife;
	
	public List<Person> children;
	
	public String toString(){
		return "Family ID: "+uniqueId+" Husband: "+husband+" Wife: "+wife;
	}
	

	@Override
	public int compareTo(Object o) {
		return this.uniqueId.compareTo(((Family)o).uniqueId);
	}
}
