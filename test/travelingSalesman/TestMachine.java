package travelingSalesman;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;

public class TestMachine {

	
	@Test
	public void testGetDistance() {
		City city1 = new City(60.00, 40.00, "city1");
		City city2 = new City(20.00, 10.00, "city2");
		assertEquals("distance from city1 to city2 should be 50", 50.00, city1.calculateDistance(city2), 0);
	}
	
	@Test
	public void testRead() {
		Read reader = new Read("cities.txt");
		List<City> list = reader.getCities();
		assertEquals("first city in list should be Albany", "Albany (home base)", list.get(0).getName());
		assertEquals("latitude should be read in correctly", 42.67, list.get(0).getLatitude(), 0);
		assertEquals("longitude should be read in correctly", 73.75, list.get(0).getLongitude(), 0);
		assertEquals("second city on list should be Albuquerque", "Albuquerque", list.get(1).getName());
		assertEquals("last city on list should be Wilmington", "Wilmington", list.get(list.size()-1).getName());
	}

	@Test
	public void testPickCities() {
		List<City> cities = Machine.pickCities(10);
		assertEquals("Should be 11 chosen cities", 11, cities.size());
		assertEquals("First should be Albany", "Albany (home base)", cities.get(0).getName());
		assertFalse("Albany should not be elsewhere on list", "Albany (home base)".equals(cities.get(cities.size()-1).getName()));
	}


}
