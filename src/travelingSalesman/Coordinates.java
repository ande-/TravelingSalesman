package travelingSalesman;
/**
 * Coordinates class
 * @author andreahoug
 *
 */

public class Coordinates {
	
	private double latitude;
	private double longitude;
	
	public Coordinates(double lat, double lon) {
		latitude = lat;
		longitude = lon;
	}
	
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
}
