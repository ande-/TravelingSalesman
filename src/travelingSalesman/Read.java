package travelingSalesman;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * The Read class simply imports the data from a file
 * @author andreahoug
 *
 */
public class Read {
	private List<City> cities = new ArrayList<City>();
	
	/**
	 * Constructor reads the data, creates a bunch of cities and puts them in a list
	 * @param fileName the file with city name, latitude, and longitude
	 */
	public Read(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			
			String line = reader.readLine();
			while(line != null) {
				//the first line of the file is the heading so go to the next
				if(cities.isEmpty()) line = reader.readLine();
				
				String[] cityInfo = line.split(",");
				City city = new City(Double.parseDouble(cityInfo[1]), Double.parseDouble(cityInfo[2]), cityInfo[0]);
				cities.add(city);

				line = reader.readLine();
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		//NumberFormatException is a runtime exception
	}
	
	/**
	 * getter for the cities
	 * @return all the cities
	 */
	
	public List<City> getCities() {
			return cities;
	}
	
}
