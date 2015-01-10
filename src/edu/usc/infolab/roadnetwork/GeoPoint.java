package edu.usc.infolab.roadnetwork;

import java.text.MessageFormat;

public class GeoPoint implements IGeoPoint {

	public GeoPoint(double lat, double lng) {
		this.lat = (int) (lat * DIVISOR);
		this.lng = (int) (lng * DIVISOR);
	}

	public boolean isValid() {
		boolean result = true;
		if (this.getLat() == -1 && this.getLng() == -1) {
			result = false;
		}
		return result;
	}

	// Get the approximated distance
	public static double GetDistance(IGeoPoint p1, IGeoPoint p2) {
		return Math.sqrt(GetDistance2(p1, p2));
	}

	// Get the square of actual distance, expected to be a little faster...
	public static double GetDistance2(IGeoPoint p1, IGeoPoint p2) {
		double height = Math.abs(p2.getLat() - p1.getLat()) * M_PER_LAT; // 110km
																			// per
																			// latitude
		double width = Math.abs(p2.getLng() - p1.getLng()) * M_PER_LNG; // 70km
																		// per
																		// longitude
		// System.out.println("width="+width+",height="+height);
		return height * height + width * width;
	}

	public static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	// Get the precise distance between to geo points
	public static double GetPreciseDistance(IGeoPoint pA, IGeoPoint pB) {
		double latA = pA.getLat(), lngA = pA.getLng();
		double latB = pB.getLat(), lngB = pB.getLng();
		double radLatA = rad(latA);
		double radLatB = rad(latB);
		double a = radLatA - radLatB;
		double b = rad(lngA) - rad(lngB);
		double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLatA) * Math.cos(radLatB)
				* Math.pow(Math.sin(b / 2), 2)));
		distance = distance * 6378137.0;
		distance = (int) (distance * 10000) / 10000;
		return distance;
	}

	/**
	 * Get the square of actual distance, expected to be a little faster...
	 * 
	 * @Title: GetDistance2
	 * @Description:
	 * @param latitudeA
	 * @param longitudeA
	 * @param latitudeB
	 * @param longitudeB
	 * @return double
	 * @throws
	 */
	public static double GetDistance(Double latitudeA, Double longitudeA,
			Double latitudeB, Double longitudeB) {
		double height = Math.abs(latitudeA - latitudeB) * M_PER_LAT; // 110km
																		// per
																		// latitude
		double width = Math.abs(longitudeA - longitudeB) * M_PER_LNG; // 70km
																		// per
																		// longitude
		return height * height + width * width;
	}

	/**
	 * Get the precise distance between to geo points
	 * 
	 * @Title: GetPreciseDistance
	 * @Description:
	 * @param latitudeA
	 * @param longitudeA
	 * @param latitudeB
	 * @param longitudeB
	 * @return double
	 * @throws
	 */
	public static double GetPreciseDistance(Double latitudeA,
			Double longitudeA, Double latitudeB, Double longitudeB) {
		double radLatA = rad(latitudeA);
		double radLatB = rad(latitudeB);
		double a = radLatA - radLatB;
		double b = rad(longitudeA) - rad(longitudeB);
		double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLatA) * Math.cos(radLatB)
				* Math.pow(Math.sin(b / 2), 2)));
		distance = distance * 6378137.0;
		distance = (int) (distance * 10000) / 10000;
		return distance;
	}

	public String ToString() {
		return (new MessageFormat("({0},{1})")).format(new Object[] { getLng(),
				getLat() });
	}

	public final static int M_PER_LAT = 110000;
	public final static int M_PER_LNG = 70000;
	private final double DIVISOR = 10000000;

	public static GeoPoint INVALID() {
		return new GeoPoint(-1, -1);
	}

	public void setINVALID() {
		this.lat = -1;
		this.lng = -1;
	}

	private int lat;
	private int lng;

	public double getLat() {
		return (double) (lat / DIVISOR);
	}

	private void setLat(double lat) {
		this.lat = (int) (lat * DIVISOR);
	}

	public double getLng() {
		return (double) (lng / DIVISOR);
	}

	private void setLng(double lng) {
		this.lng = (int) (lng * DIVISOR);
	}

}
