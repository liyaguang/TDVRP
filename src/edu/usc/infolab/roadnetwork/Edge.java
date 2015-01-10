package edu.usc.infolab.roadnetwork;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.UnavailableException;

import edu.usc.infolab.base.StringUtils;
import edu.usc.infolab.roadnetwork.outwraper.SegIdxObject;
import edu.usc.infolab.roadnetwork.outwraper.TypeObject;

public class Edge implements ILine {

	long id;

	public long getId() {
		return id;
	}

	Vertex start;

	Vertex end;

	double length = -1;

	// / Polyline that represents the geometry of the edge
	private Polyline geo = null;

	private String geoString = null;

	public MBR getMBR() {
		return getGeo().MBR();
	}

	public Polyline getGeo() {
		if (geo == null) {
			// for multiple
			synchronized (syncRoot) {
				if (geo == null) {
					List<IGeoPoint> points = new ArrayList<IGeoPoint>();
					if (StringUtils.IsNullOrEmpty(geoString)) {
						points.add(this.start.ToPoint());
						points.add(this.end.ToPoint());
					} else {
						List<String> fields = StringUtils.SimpleSplit(
								geoString, '\t');
						for (int i = 0; i < fields.size(); i += 2) {
							double lat = Double.valueOf(fields.get(i));
							double lng = Double.valueOf(fields.get(i + 1));
							points.add(new GeoPoint(lat, lng));
						}
						this.geoString = null;
					}
					geo = new Polyline(points);
				}
			}
		}
		return geo;
	}

	public void setGeo(Polyline geo) {
		this.geo = geo;
	}

	public String getGeoString() {
		return geoString;
	}

	public void setGeoString(String geoString) {
		this.geoString = geoString;
	}

	private double getLength() {
		// double len = GeoPoint.GetDistance(start.ToPoint(), end.ToPoint());
		double len = getGeo().getLength();
		return len;
	}

	public double length() {
		if (length < 0) {
			synchronized (syncRoot) {
				if (length < 0) {
					length = getLength();
				}
			}
		}
		return length;
	}

	private final Object syncRoot = new Object();
	private List<Edge> outEdges = null;

	public List<Edge> getOutEdges() {
		if (outEdges == null) {
			// thread safe
			outEdges = this.end.getOutEdges();
		}
		return outEdges;

	}

	private List<Edge> inEdges = null;

	public List<Edge> getInEdges() {
		if (inEdges == null) {
			// thread safe
			inEdges = this.start.getInEdges();
		}
		return inEdges;
	}

	public Edge(long id, Vertex start, Vertex end) {
		this.id = id;
		this.start = start;
		this.end = end;
	}

	public Edge(long id, Vertex start, Vertex end, double length) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.length = length;
	}

	public Edge(long id, Vertex start, Vertex end, double length,
			double speedLimit, int type) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.length = length;
	}

	// / Get the distance from a point to the edge
	public double dist2From(IGeoPoint p) {
		TypeObject typeObj = new TypeObject(0);
		return getGeo().dist2From(p, typeObj);
	}

	public double dist2From(IGeoPoint p, TypeObject typeObj) {
		return getGeo().dist2From(p, typeObj);
	}

	// / Get the distance from a point to the edge
	public double distFrom(IGeoPoint p) {
		return Math.sqrt(dist2From(p));
	}

	public double distFrom(IGeoPoint p, TypeObject typeObj) {
		return Math.sqrt(dist2From(p, typeObj));
	}

	//
	// public IGeoPoint predict(GeoPoint start, double distance) {
	// return this.getGeo().predict(start, distance);
	// }
	//
	// public double endDistFrom(GeoPoint p, TypeObject typeObj) {
	// return this.getGeo().endDistFrom(p, typeObj);
	// }
	//
	// // / Predict the position from start after distance on this route
	// public double distOnLine(GeoPoint from, GeoPoint to) {
	// return this.getGeo().distOnLine(from, to);
	// }

	// / Calculate the cosine value with line p1,p2
	public double cosWith(GeoPoint p1, GeoPoint p2) {
		return getGeo().cosWith(p1, p2);
	}

	public boolean equals(Object obj) {
		boolean result = false;
		if (obj != null && obj instanceof Edge) {
			result = ((Edge) obj).getId() == this.getId();
		}
		return result;
	}

	public int hashCode() {
		return (int) this.getId() ^ (int) (this.getId() >> 32);
	}

	public String toString() {
		return String.format("Edge %d: %s -> %s", this.getId(),
				this.start.toString(), this.end.toString());
	}

	@Override
	public IGeoPoint getStart() {
		// TODO Auto-generated method stub
		return this.start;
	}

	@Override
	public IGeoPoint getEnd() {
		// TODO Auto-generated method stub
		return this.end;
	}

}
