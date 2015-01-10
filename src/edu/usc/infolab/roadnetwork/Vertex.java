package edu.usc.infolab.roadnetwork;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Vertex implements IGeoPoint {

	public Vertex(long id, double lat, double lng) {
		this.id = id;
		this.point = new GeoPoint(lat, lng);
	}

	private void calculateInOut() {
		synchronized (syncRoot) {
			if (outEdges == null) {
				int edgeSize = adjacentEdges.size();
				outEdges = new LinkedList<Edge>();
				inEdges = new LinkedList<Edge>();
				for (int i = 0; i < edgeSize; i++) {
					if (adjacentEdges.get(i).getStart() == this) {
						outEdges.add(adjacentEdges.get(i));
					} else {
						inEdges.add(adjacentEdges.get(i));
					}
				}
			}
		}

	}

	long id;

	public long getId() {
		return id;
	}

	IGeoPoint point;

	public double getLat() {
		return point.getLat();
	}

	public double getLng() {
		return point.getLng();
	}

	private final Object syncRoot = new Object();
	private List<Edge> adjacentEdges = new ArrayList<Edge>();

	private List<Edge> outEdges = null;
	private List<Edge> inEdges = null;

	public List<Edge> getOutEdges() {
		if (outEdges == null) {
			calculateInOut();
		}
		return outEdges;
	}

	public List<Edge> getInEdges() {
		if (inEdges == null) {
			calculateInOut();
		}
		return inEdges;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Vertex) {
			return ((Vertex) obj).getId() == this.getId();
		}
		return false;
	}

	public int hashCode() {
		return (int) this.getId() ^ (int) (this.getId() >> 32);
	}

	public void RegisterEdge(Edge e) {
		synchronized (syncRoot) {
			this.adjacentEdges.add(e);
		}
	}

	public IGeoPoint ToPoint() {
		return this.point;
	}

	public IGeoPoint getPoint() {
		return this.point;
	}

	public String toString() {
		return String.format("Vertex %d:(%f, %f)", this.getId(), this.getLng(),
				this.getLat());
	}
}
