package myNewProject;

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
			URL url = new URL("https://raw.githubusercontent.com/robchin89/Agile/master/gedFile.ged");
			//URL url = new URL("https://raw.githubusercontent.com/robchin89/test/master/TestBigamy.ged");
			//URL url = new URL("https://raw.githubusercontent.com/robchin89/test/master/TestBigamyDeath.ged");
			//URL url = new URL("https://raw.githubusercontent.com/robchin89/test/master/Family-3-22-Feb-2016.ged"); //testing bigamy
			Scanner fileReader = new Scanner(url.openStream());


			//File gedFile = new File("~/../../gedFile.ged");
			//Scanner fileReader = new Scanner(gedFile);

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

				//add divorce
				if(level.equals("1") && tag.equals("DIV")){
					level = fileReader.next();
					tag = fileReader.next();
					value = fileReader.nextLine().trim();
					if(level.equals("2") && tag.equals("DATE")){
						Date divorce = parseDate(value);
						family.wife.divorce = divorce;
						family.husband.divorce = divorce;
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
				System.out.println(individual);
				individual.checkBirthBeforeDeath();
				individual.checkMarriageBeforeDeath();
				individual.checkBirthBeforeMarriage(); //check birth before marriage
			}

			System.out.println("\nFamilies:");
			//Families.sort(null);

			for(int i = 0; i < Families.size();i++){
				System.out.println(Families.get(i)); 

				//CHECK BIGAMY START
				for(int j = 0; j < Families.size(); j++){
					if (i!=j && (Families.get(i).husband == Families.get(j).husband)){
						//wife of family i death before husband married j's wife
						if(i!=j && Families.get(i).wife.death != null && Families.get(j).wife.marriage != null && Families.get(i).wife.death.before(Families.get(j).wife.marriage)){
							System.out.println("No Bigamy (Widowed)");
						}else{
							//wife of family j death before husband married j's wife
							if(i!=j&&Families.get(j).wife.death != null && Families.get(i).wife.marriage != null && Families.get(j).wife.death.before(Families.get(i).wife.marriage)){
								System.out.println("No Bigamy (Widowed)");
							}else
							{
								System.out.println("There's Bigamy with " + Families.get(i).husband.name +" Bigamy with " + Families.get(i).wife.name +" and "+ Families.get(j).wife.name + "\n");

							}
						}

					}
					else
					{
						if (i!=j && (Families.get(i).wife == Families.get(j).wife)){
							if(i!=j && Families.get(i).husband.death != null && Families.get(j).husband.marriage != null && Families.get(i).husband.death.before(Families.get(j).husband.marriage)){
								System.out.println("No Bigamy (Widowed)");
							}
							else
							{
								//wife of family j death before husband married j's wife
								if(i!=j&&Families.get(j).wife.death != null && Families.get(i).husband.marriage != null && Families.get(j).husband.death.before(Families.get(i).husband.marriage)){
									System.out.println("No Bigamy (Widowed)");
								}
								else
								{

									System.out.println("There's Bigamy with " + Families.get(i).wife.name +" Bigamy with " + Families.get(i).husband.name + " and " + Families.get(j).husband.name + "\n");

								}
							}
						}
					}
				}
				// CHECK BIGAMY END

			}
			System.out.println("\n");
			System.out.println("Birthday List(next 30 days):");
			ListBirthday(Individuals);
		}
		catch(IOException ex) {
			// there was some connection problem, or the file did not exist on the server,
			// or your URL was not in the right format.
			// think about what to do now, and put it here.
			ex.printStackTrace(); // for now, simply output it.
		}
    } 

	private static <list> void ListBirthday(List<Person> individuals){
			for(int i = 0; i < individuals.size();i++){
			Date bdt = individuals.get(i).birthday;
			Date ddt = individuals.get(i).death;
			String nm = individuals.get(i).name;
			if ((ddt == null)){
				if(daycompare(bdt)){
					SimpleDateFormat dt = new SimpleDateFormat("EEE, dd MMM"); 
					System.out.println("Name: " + nm + "  " + "Birthdate: " + dt.format(bdt));
				};
			}
		}

	}

	public static boolean daycompare(Date birthday){
		int thisyear = Calendar.getInstance().get(Calendar.YEAR);
		Calendar birthdate = Calendar.getInstance();
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		today.set(Calendar.HOUR_OF_DAY, 0);
		birthdate.setTime(birthday);
		birthdate.set(Calendar.YEAR, thisyear);
		if(birthdate.before(today))
			birthdate.set(Calendar.YEAR, thisyear + 1);

		//today.add(Calendar.DATE,  -1);
		Calendar next30day = Calendar.getInstance();
		next30day.set(Calendar.HOUR, 0);
		next30day.set(Calendar.MINUTE, 0);
		next30day.set(Calendar.SECOND, 0);
		next30day.set(Calendar.HOUR_OF_DAY, 0);
		next30day.set(Calendar.MILLISECOND, 0);
		next30day.add(Calendar.DATE, 30);
		
		if(birthdate.before(next30day) && !today.after(birthdate)){
			//System.out.println(birthdate.getTime());
			//System.out.println(today.getTime());
			//System.out.println(next30day.getTime());
		return true;
		}
		return false;

	}


}