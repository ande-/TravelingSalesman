package travelingSalesman;
/**
 * City class stores information about cities, including latitude and longitude, 
 * which are inherited from Coordinates, the name and the x and y positions on the map
 * @author andreahoug
 *
 */
public class City extends Coordinates {
	private String name;
	private int yposition;
	private int xposition;
	
	/**
	 * Constructor
	 * @param lat
	 * @param lon
	 * @param name
	 */
	public City(double lat, double lon, String name) {
		super(lat, lon);
		this.name = name;
		setypos();
		setxpos();
	}
	
	public String getName() {
		return name;
	}
	/**
	 * Calculates the distance from this city to another.
	 * Assumes 1 degree longitude equals 1 degree latitude and a flat earth
	 * @param to the city we're finding the distance to
	 * @return distance
	 */
	
	public double calculateDistance(City to) {
		double x = getLatitude()-to.getLatitude();
		double y = getLongitude()-to.getLongitude();
		return Math.sqrt(x*x+y*y);
	}
	
	//functions to determine where the city is located in the map
	//these are kind of arbitrary calculations based on the size of the map
	private void setypos() {
		yposition = (int) Math.round((1/getLatitude())*30000)-450;
	}
	
	public int getypos() {
		return yposition;
	}
	
	private void setxpos() {
		xposition = (int) Math.round((1/getLongitude())*120000)-700 ;
	}
	
	public int getxpos() {
		return xposition;
	}
}
