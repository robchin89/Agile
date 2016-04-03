package myNewProject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Years;

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
			//URL url = new URL("https://raw.githubusercontent.com/robchin89/Agile/master/gedFile.ged");
			
			//URL url = new URL("https://raw.githubusercontent.com/robchin89/test/master/TestBigamy.ged");
			//URL url = new URL("https://raw.githubusercontent.com/robchin89/test/master/TestBigamyDeath.ged");
			//URL url = new URL("https://raw.githubusercontent.com/robchin89/test/master/Family-3-22-Feb-2016.ged"); //testing bigamy
			
			//URL url = new URL("https://raw.githubusercontent.com/robchin89/test/master/gedFile2"); //testing 150 years old
			
			//Scanner fileReader = new Scanner(url.openStream());
			

			//String filePath = new File("").getAbsolutePath();
			//System.out.println
			//File gedFile = new File("\\Stevens\\Agile\\gedFileTest.ged");// local test file.
			
			File gedFile = new File("./gedFile.ged");// local test file.
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
				
				if(level.equals("1") && tag.equals("SEX")){
					person.gender = value;
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
						family.marriage = marriage;
					}
				}

				//add divorce
				if(level.equals("1") && tag.equals("DIV")){
					level = fileReader.next();
					tag = fileReader.next();
					value = fileReader.nextLine().trim();
					if(level.equals("2") && tag.equals("DATE")){
						Date divorce = parseDate(value);
						family.wife.divorce = divorce;
						family.husband.divorce = divorce;
						family.divorce = divorce;
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
			System.out.println("\nIndividuals:");
			//Individuals.sort(null);

			for(int i = 0; i < Individuals.size();i++){
				Person individual = Individuals.get(i);
				System.out.println("\n" + individual + " Age: " + individual.getAge());
				
				// Birth Before Death
				individual.checkBirthBeforeDeath();
				
				// Marriage before Death
				individual.checkMarriageBeforeDeath();
				
				// Birth before Marriage
				individual.checkBirthBeforeMarriage(); 
				
				// Check less than 150 years old
				individual.checkLessThan150();
				
				// Check if living single (Over 30 and never married)
				individual.checkLivingSingle();
			}
			
			//Check Unique Name and Birthdate Start
			checkSameNameBirthdate(Individuals);
			//Check Unique Name and Birthdate End
			
			System.out.println("\nFamilies:");
			//Families.sort(null);

			for(int i = 0; i < Families.size();i++){
				
				System.out.println("\n" + Families.get(i)); 

				//CHECK BIGAMY START
				for(int j = 0; j < Families.size(); j++){
					if (i!=j && (Families.get(i).husband == Families.get(j).husband)){
						//wife of family i death before husband married j's wife
						if(Families.get(i).wife.death != null && Families.get(j).wife.marriage != null && Families.get(i).wife.death.before(Families.get(j).wife.marriage)){
							//
						}else{
							//wife of family j death before husband married j's wife
							if(Families.get(j).wife.death != null && Families.get(i).wife.marriage != null && Families.get(j).wife.death.before(Families.get(i).wife.marriage)){
								//
							}else
							{
								System.out.println("There's Bigamy with " + Families.get(i).husband.name +" Bigamy with " + Families.get(i).wife.name +" and "+ Families.get(j).wife.name);

							}
						}

					}
					else
					{
						if (i!=j && (Families.get(i).wife == Families.get(j).wife)){
							if(Families.get(i).husband.death != null && Families.get(j).husband.marriage != null && Families.get(i).husband.death.before(Families.get(j).husband.marriage)){
								//
							}
							else
							{
								//wife of family j death before husband married j's wife
								if(Families.get(j).wife.death != null && Families.get(i).husband.marriage != null && Families.get(j).husband.death.before(Families.get(i).husband.marriage)){
									//
								}
								else
								{

									System.out.println("There's Bigamy with " + Families.get(i).wife.name +" Bigamy with " + Families.get(i).husband.name + " and " + Families.get(j).husband.name);

								}
							}
						}
					}
				}
				// CHECK BIGAMY END
				
				//CHECK MULTIPLE BIRTHS START
				if(!Families.get(i).checkMultipleBirths()){
					System.out.println("More than 4 children have the same birth date in family "+Families.get(i).uniqueId + ".");
				}
				//CHECK MULTIPLE BIRTHS END
				
				//CHECK SURNAME START
				if(!Families.get(i).checkMaleLastNames()){
					System.out.println("All the males in family " + Families.get(i).uniqueId + " do not have the same last name.");
				}
				//CHECK SURNAME END
				
				//ORDER SIBLINGS BY AGE START
				Families.get(i).printChildrenByAge();
				//ORDER SIBLINGS BY AGE END
			
				//Sprint 2 US 15 - No more than 15 siblings
				Families.get(i).checkMaxSiblings();
				
				//Sprint 2 US 06 - No divorce after death
				Families.get(i).checkDivorceBeforeDeath();
				
				//Check Unique Children Names Start
				Families.get(i).uniqueChildrenNames();
				//Check Unique Children Names End
				
				// Sprint 3 US 34 - Large Age Differences
				Families.get(i).checkLargeAgeDifference();
			}
			
			//sprint 1 US38 - list of up coming Birthday
			System.out.println("\n");
			System.out.println("US38 - Birthday List(next 30 days):");
			System.out.println("-----------------------------");
			ListBirthday(Individuals);
			//sprint 1 US39 - list of marriage anniversaries 
			System.out.println("\n");
			System.out.println("US39 - Marriage Anniversary List(next 30 days):");
			System.out.println("---------------------------------");
			ListAnniversary(Families);
		
			//sprint 2 US37 - list of recent survivors
			System.out.println("\n");
			System.out.println("US37 - List of Survivors(last 30 days)");
			System.out.println("---------------------------------");
			Family.ListSurvivors(Families, Individuals);
			
			//List of surviving couples
			System.out.println("\n");
			System.out.println("US30 - List of married couples alive");
			System.out.println("---------------------------------");
			Family.ListSurvingCouples(Families, Individuals);
			
			//Sprint 3
			//List of parents not too old.
			System.out.println("\n");
			System.out.println("US12 - List of parents not too old");
			System.out.println("---------------------------------");
			ListParentNotOld(Families);
			
			//List of sibling spaceing.
			System.out.println("\n");
			System.out.println("US13 - List of sibling space");
			System.out.println("---------------------------------");
			ListSiblingSpace(Families);
			
			//sprint 2 US29 - list deceased
			System.out.println("\n");
			System.out.println("US29 - Deceased List:");
			System.out.println("-----------------------------");
			ListDeceased(Individuals);
			
		}
		catch(IOException ex) {
			// there was some connection problem, or the file did not exist on the server,
			// or your URL was not in the right format.
			// think about what to do now, and put it here.
			ex.printStackTrace(); // for now, simply output it.
		}
    } 
	
double idiff = 0;
static DateTime MaxBirthday;
static String ChildName;
private static<list> void ListSiblingSpace(List<Family> Families){
		
		for(int i = 0; i < Families.size();i++){
			if(Families.get(i).children != null){
				if((Families.get(i).children.size() > 1)){
					for(int y = 0; y < (Families.get(i).children.size()); y++){
						String sName = (Families.get(i).children.get(y).name);
						DateTime ithBirthday = new DateTime((Families.get(i).children.get(y).birthday));
						if(!((MaxBirthday)==null)){
							
							if(Months.monthsBetween(MaxBirthday, ithBirthday).getMonths() < 8 &&
									Months.monthsBetween(MaxBirthday, ithBirthday).getMonths() > -8)
							{
								System.out.println( "Difference between birthdate of sibling " + " " + sName +" and " + ChildName + " is " 
										+ Months.monthsBetween(MaxBirthday, ithBirthday).getMonths() + " --- " + MaxBirthday + "--- " + ithBirthday + " " + "These Birthdates are not valid as spacing is incorrect");
							}
						
							if(Days.daysBetween(MaxBirthday, ithBirthday).getDays() < 2 &&
									Days.daysBetween(MaxBirthday, ithBirthday).getDays() > -2)
							{
								System.out.println( "Difference between Birthday of sibling " + " " + sName +" and " + ChildName + " is " 
										+ Days.daysBetween(MaxBirthday, ithBirthday).getDays() + " --- " + MaxBirthday + "--- " + ithBirthday + " " + "Birthday needs to be atleast 2 days apart.");
							}
						}
						MaxBirthday = new DateTime(ithBirthday);
						ChildName = sName;
						}
					
					}
				}
			}
		}
	

public static double smallestElement(double[] alist){
	double min = alist[0];
	for (int x=0; x < alist.length; x++){
		if (alist[x] < min) min= alist[x];
	}
	return min;
}


	private static<list> void ListParentNotOld(List<Family> Families){
		
		for(int i = 0; i < Families.size();i++){
			if(Families.get(i).children != null){
				if((Families.get(i).children.size() > 1)){
					for(int y = 0; y < (Families.get(i).children.size()); y++){
						Date ithBirthday = (Families.get(i).children.get(y).birthday);
						DateTime MaxBirthday = new DateTime(ithBirthday);
						DateTime HusbandBirthday = new DateTime(Families.get(i).husband.birthday);
						DateTime WifeBirthday = new DateTime(Families.get(i).husband.birthday);
						String HusbandName = Families.get(i).husband.name;
						String WifeName = Families.get(i).wife.name;
						String cName = Families.get(i).children.get(y).name;
						PrintParents2Old(HusbandName,WifeName,cName,MaxBirthday, HusbandBirthday,WifeBirthday);
					}
				}
			}
		}
	}
	
	
	private static void PrintParents2Old(String hName, String wName, String cName, DateTime mxdate, DateTime hDate, DateTime wDate){
		if(Years.yearsBetween(hDate,mxdate).getYears() > 80 ){
			System.out.println("Difference between child "
					+ "" +cName + " and " +hName +
					" is "+  Years.yearsBetween(mxdate, hDate).getYears() + "  : Father is too old");
		}
		if(Years.yearsBetween(hDate,mxdate).getYears() > 60 ){
			System.out.println("Difference between child "
					+ "" +cName + " and " +wName +
					" is "+  Years.yearsBetween(mxdate, wDate).getYears() + "  : Wife is too old");
		}
	}

	private static <list> void ListBirthday(List<Person> individuals){
			for(int i = 0; i < individuals.size();i++){
			Date bdt = individuals.get(i).birthday;
			Date ddt = individuals.get(i).death;
			String nm = individuals.get(i).name;
			if ((ddt == null)){
				if(daycompare(bdt)){
					SimpleDateFormat dt = new SimpleDateFormat("dd MMM"); 
					System.out.println("Name: " + nm + "  " + "Birthdate: " + dt.format(bdt));
					System.out.println("-------------------------------------------------------");
				};
			}
		}

	}
	
	private static <list> void ListDeceased(List<Person> individuals){
		for(int i = 0; i < individuals.size();i++){
		//Date bdt = individuals.get(i).birthday;
		Date ddt = individuals.get(i).death;
		String nm = individuals.get(i).name;
		if ((ddt != null)){
			
				SimpleDateFormat dt = new SimpleDateFormat("dd MMM"); 
				System.out.println("Name: " + nm + "  " + "Death: " + dt.format(ddt));
				//System.out.println("-------------------------------------------------------");
			
		}
	}

}
	
	private static <list> void ListAnniversary(List<Family> Families){
		SimpleDateFormat dt = new SimpleDateFormat("dd MMM"); 
		for(int i = 0; i < Families.size();i++){
			Date mdt = Families.get(i).husband.marriage;
			Date mwdt = Families.get(i).wife.marriage;
			Date hdt = Families.get(i).husband.death;
			Date wdt =  Families.get(i).wife.death;
			String hnm = Families.get(i).husband.name;
			String wnm = Families.get(i).wife.name;
		if ((hdt == null) || (wdt==null)) {
			if(!(mdt==null) || !(mwdt==null)){
			if(daycompare(mdt)){
					System.out.println("Husband: " + hnm);
					System.out.println("Wife: " + wnm );
					System.out.println("Marriage Anniversary: " + dt.format(mdt));
					System.out.println("---------------------------------------");
				}
			}
		}

		}
	}


	public static boolean daycompare(Date impDates){
		int thisyear = Calendar.getInstance().get(Calendar.YEAR);
		Calendar importantDates = Calendar.getInstance();
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		today.set(Calendar.HOUR_OF_DAY, 0);
		importantDates.setTime(impDates);
		importantDates.set(Calendar.YEAR, thisyear);
		if(importantDates.before(today))
			importantDates.set(Calendar.YEAR, thisyear + 1);

		//today.add(Calendar.DATE,  -1);
		Calendar next30day = Calendar.getInstance();
		next30day.set(Calendar.HOUR, 0);
		next30day.set(Calendar.MINUTE, 0);
		next30day.set(Calendar.SECOND, 0);
		next30day.set(Calendar.HOUR_OF_DAY, 0);
		next30day.set(Calendar.MILLISECOND, 0);
		next30day.add(Calendar.DATE, 30);
		
		//today.add(Calendar.DATE,  -1);
		Calendar last30day = Calendar.getInstance();
		last30day.set(Calendar.HOUR, 0);
		last30day.set(Calendar.MINUTE, 0);
		last30day.set(Calendar.SECOND, 0);
		last30day.set(Calendar.HOUR_OF_DAY, 0);
		last30day.set(Calendar.MILLISECOND, 0);
		last30day.add(Calendar.DATE, 30);
		
		if(importantDates.before(next30day) && !today.after(importantDates)){
			return true;
		}
		return false;
	}
	
	public static void checkSameNameBirthdate(List<Person> individuals){
		for(int i = 0; i < individuals.size(); i++){
			for( int j = 0; j < individuals.size(); j++){
				if( i != j){
					if(individuals.get(i).sameNameBirthdate(individuals.get(j))){
						System.out.println("Duplicate person: "+individuals.get(i).name+" ("+individuals.get(i).uniqueId+") and "+individuals.get(j).name+" ("+individuals.get(j).uniqueId+") have the same name and birthday.");
					}
				}
			}
		}
		
	}
}
