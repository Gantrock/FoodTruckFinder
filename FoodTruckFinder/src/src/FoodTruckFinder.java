package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FoodTruckFinder {
	public static void main(String[] args) {
		final int NUM_TRUCKS = 10;
		TreeSet<FoodTruck> trucks = new TreeSet<>();
		TruckHelper helper = new TruckHelper();
		trucks = helper.connectAPI();
		System.out.println("Your system is set to timezone: " + 
				ZonedDateTime.now().getZone());
		if(trucks.size() < 1) {
			System.out.println("Sorry, no trucks are open right now");
		} else {
			helper.truckLoop(NUM_TRUCKS, trucks);
		}

	}
	
}

/**
 * A helper class to keep methods out of main, this class would ideally actually be
 *  several different classes.
 * @author Redacted
 *
 */
class TruckHelper {
	//class level variable denoting the maximum name
	int maxName = 0;
	
	public TreeSet<FoodTruck> connectAPI() {
		JSONArray jArray;
		//TreeSet maintains ordering of FoodTruck objects
		TreeSet<FoodTruck> pod = new TreeSet<>();
   		try {
   			//connect to API with a GET request
			URL url = new URL("http://data.sfgov.org/resource/bbb8-hzi6.json");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			/*create a new BufferedReader using the HttpURLConnection from the API
			 *  call 
			 */
			BufferedReader rd = 
					new BufferedReader(new InputStreamReader(conn.getInputStream()));
			JSONParser jsonParser = new JSONParser();
			//using the JSONParse to create an JSONArray of JSONObjects
			jArray = (JSONArray) jsonParser.parse(rd);
			//fills the TreeSet with FoodTrucks
			pod = parseJSON(jArray);
			rd.close();

		} catch (Exception e) {
			//Prints the actual error
			System.out.println(e.toString());
			//Prints a less than helpful version of the error
			System.out.println(e.getMessage());
		}
   		return pod;
	}
	
	/**
	 * Parses the JSONArray and creates a TreeSet of FoodTrucks
	 * @param jArray a JSONArray containing data from the API call
	 * @return pod, a TreeSet filled with FoodTruck objects that meet our criteria
	 */
	public TreeSet<FoodTruck> parseJSON(JSONArray jArray) {
		JSONObject jObj;
		String name = "";
		String location = "";
		String dayOpen;
		//Splitting Strings to get time/minute require arrays
		int[] openTime = new int[2];
		int[] closeTime = new int[2];
		//Gets the local time according to the system clock
		LocalDateTime time = LocalDateTime.now();
		int localHour = time.getHour();
		TreeSet<FoodTruck> pod = new TreeSet<>();
		
		//Iterate through every element of jArray and if they fit criteria add to pod
		for(int i = 0; i < jArray.size(); i++) {
			//temporary JSONObject to hold element from jArray
			jObj = (JSONObject) jArray.get(i);
			
			name = jObj.get("applicant").toString();
			location = jObj.get("location").toString();
			//string for day is converted to Upper case to facilitate comparison
			dayOpen = jObj.get("dayofweekstr").toString().toUpperCase();
			
			//temp String that holds the unformatted time
			String tempTime = (String)jObj.get("start24");
			//fills the openArray with hours at index 0 and minutes at index 1
			openTime = splitTime(tempTime);
			
			tempTime = (String)jObj.get("end24");
			//fills the openArray with hours at index 0 and minutes at index 1
			closeTime = splitTime(tempTime);
			/*If local time is after opening time and before closing time and the 
			 * days are equal add to set
			 */
			if(openTime[0] <= localHour && closeTime[0] > localHour && 
					dayOpen.equals(time.getDayOfWeek().toString())) {
				//truck = new FoodTruck(name, location);
				if(name.length() > maxName) {maxName = name.length();};
				pod.add(new FoodTruck(name, location));
			}
		}
		return pod;
	}
	/**
	 * Splits the time String into Integers for hours and minutes
	 * @param time, A String version of the time
	 * @return result, an array containing hours at index 0 and minutes at index 1
	 */
	public int[] splitTime(String time) {
		String[] hourAndMinute = new String[2];
		int[] result = new int[2];
		
		//Split into String array
		hourAndMinute = time.split(":", 2);
		//Parses the string versions of Hour and Minutes into Integers
		result[0] = Integer.parseInt(hourAndMinute[0]);
		result[1] = Integer.parseInt(hourAndMinute[1]);

		return result;
	}
	
	//Non-formatted loop kept for posterity and potential future use.
	/*public void truckLoop(int trucksPerPage, TreeSet<FoodTruck> trucks) {
		Scanner inp = new Scanner(System.in);
		int count = 0;
		String endLoop = "";
		Iterator<FoodTruck> itr = trucks.iterator();
		System.out.println("NAME" + " \t " + "LOCATION");
		do{
			System.out.println(itr.next());
			count++;
			if(count == 10) {
				count = 0;
				System.out.println("Next page? Q to exit.");
				endLoop = inp.nextLine().toUpperCase();
			}
		}while(itr.hasNext() && !endLoop.equals("Q"));
		inp.close();
	}*/
	
	/**
	 * Iterates through every item in the TreeSet trucks and prints them to formatted
	 * output.
	 * @param trucksPerPage The number of trucks to display per page.
	 * @param trucks The TreeSet of FoodTrucks
	 */
	public void truckLoop(int trucksPerPage, TreeSet<FoodTruck> trucks) {
		Scanner inp = new Scanner(System.in);
		//maintains track of how many items we have displayed.
		int count = 0;
		//obtain the line separator on a system basis.
		String newLine = System.getProperty("line.separator");
		//increment maxName so the max size name doesn't run into the location
		maxName++;
		//string to be used in dynamically formatting
		String format = "%-" + maxName + "s%" +"s" + newLine;
		Iterator<FoodTruck> itr = trucks.iterator();
		System.out.printf(format, "NAME", "LOCATION");
		/*while the iterator has another item print it.
		 *Once the user reaches the maximum number of FoodTrucks displayed ask for
		 *input before going to the next page.
		 */
		while(itr.hasNext()){
			FoodTruck temp = itr.next();
			String name = temp.getName();
			String loc = temp.getLoc();
			System.out.printf(format, name, loc);
			count++;
			if(count == 10) {
				count = 0;
				System.out.println("Next page?");
				inp.nextLine().toUpperCase();
			}
		}
		//close Scanner to prevent memory leaks
		inp.close();
	}	
	
}

/**
 * Object designed to facilitate the sorting of Food Trucks alphabetically while
 *  also tying location to the name.
 * @author Redacted
 *
 */
class FoodTruck implements Comparable<FoodTruck>{
	String name;
	String loc;
	
	public FoodTruck(String theName, String theLoc) {
		name = theName;
		loc = theLoc;
	}
	
	public String getName(){
		return name;
	}
	
	public String getLoc(){
		return loc;
	}
	
	public String toString(){
		return(name + "\t " + loc);
	}

	public int compareTo(FoodTruck theOther) {
		return name.compareTo(theOther.getName());
	}		
	
}
