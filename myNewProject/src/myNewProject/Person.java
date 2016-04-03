package myNewProject;

import java.util.Date;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.Years;

public class Person implements Comparable {

	public String uniqueId;
	
	public String name;
	
	public String gender;
	
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
			System.out.println(name + " died before married!");
		}
	}
	
	
	//check birth before marriage
	public void checkBirthBeforeMarriage(){
		if(birthday != null && marriage != null && marriage.before(birthday)){
			System.out.println(name + " married before birth!");
		}
	}
	
	//check less than 150
	
	public void checkLessThan150()
	{
		DateTime jodadeath = new DateTime(death);
		DateTime jodabirth = new DateTime(birthday);
		DateTime today = new DateTime();
		
		if(death !=null)
		{
			if(Years.yearsBetween(jodabirth, jodadeath).getYears() > 150 )
			{
			//System.out.println(name + " greater than 150 years old!"+today+" Age: " + jodadeath + " - " + jodabirth + " = " +  (Years.yearsBetween(jodabirth, jodadeath).getYears()));
			System.out.println(name + " greater than 150 years old! Age: " + Years.yearsBetween(jodabirth, jodadeath).getYears());
			};
		}
		else
		{	
			if ((Years.yearsBetween(jodabirth, today)).getYears() > 150)
			{
			//System.out.println(name + " greater than 150 years old! Age: " + today + " - " + jodabirth + " = " + ((Years.yearsBetween(jodabirth, today).getYears())));	
			System.out.println(name + " greater than 150 years old! Age: " + Years.yearsBetween(jodabirth, today).getYears());
			};	
		}
				
			
	}
	
	
	
	public String getLastName(){
		String[] names = name.split(" ");
		return names[names.length-1].substring(1, names[names.length-1].length()-1);
	}
	
	public int getAge(){
		Date currentDate = new Date();
		long time = currentDate.getTime() - birthday.getTime();
		//31557600000 milliseconds in a year, assume 365.25 is a year to include leap years
		return (int)Math.floor(time/3.15576e+10);
	}
	
	@Override
	public int compareTo(Object o) {
		return this.uniqueId.compareTo(((Person)o).uniqueId);
	}
	
	public void checkLivingSingle(){
		DateTime jodabirth = new DateTime(birthday);
		DateTime now = new DateTime();
		Years age = Years.yearsBetween(jodabirth, now);
		if(age.getYears() > 30 && marriage == null && divorce == null){
			System.out.println(name + " is living single");
		}
	}
	
}
