package edu.usc.infolab.lib;

public class GeoPoint {
	public double lat;
	public double lng;

	public GeoPoint(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public String toString() {
		return String.format("%.6f,%.6f", this.lat, this.lng);
	}
}
