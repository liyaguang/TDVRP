package edu.usc.infolab.roadnetwork;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.usc.infolab.base.Constants;
import edu.usc.infolab.base.StringUtils;
import edu.usc.infolab.gridindex.GridEdge;
import edu.usc.infolab.gridindex.GridPoint;

public class Graph {

	private HashMap<Long, Edge> edges;

	public HashMap<Long, Edge> getEdges() {
		return edges;
	}

	private HashMap<Long, Vertex> vertices;

	public HashMap<Long, Vertex> getVertices() {
		return vertices;
	}

	private GridEdge edgeIndex = null;

	public GridEdge getEdgeIndex() {
		if (this.edgeIndex == null) {
			this.edgeIndex = new GridEdge(edges.values(), mbr, edgeCellSize);
		}
		return edgeIndex;
	}

	private GridPoint vertexIndex;
	private MBR mbr;
	private double edgeCellSize = 100 * Constants.D_PER_M;
	private double vertexCellSize = 100 * Constants.D_PER_M;

	public Graph(String vertexFile, String edgeFile, String geometryFile)
			throws NumberFormatException, IOException {
		long beginTimeStamp = System.currentTimeMillis();
		loadVertices(vertexFile);
		System.out.printf("Vertex:%dms\n",
				(System.currentTimeMillis() - beginTimeStamp));
		loadEdges(edgeFile);
		System.out.printf("Edge:%dms\n",
				(System.currentTimeMillis() - beginTimeStamp));

		if (geometryFile != null) {
			loadGeometry(geometryFile);
		}
		System.out.printf("Gem:%dms\n",
				(System.currentTimeMillis() - beginTimeStamp));
		// Console.ReadLine();
		buildRNIndex();
		System.out.printf("Index:%dms\n",
				(System.currentTimeMillis() - beginTimeStamp));

	}

	public Graph(String vertexFile, String edgeFile)
			throws NumberFormatException, IOException {
		long beginTimeStamp = System.currentTimeMillis();
		loadVertices(vertexFile);
		System.out.printf("Vertex:%dms\n",
				(System.currentTimeMillis() - beginTimeStamp));
		loadEdges(edgeFile);
		System.out.printf("Edge:%dms\n",
				(System.currentTimeMillis() - beginTimeStamp));
		System.out.printf("Gem:%dms\n",
				(System.currentTimeMillis() - beginTimeStamp));
		// Console.ReadLine();
		buildRNIndex();
		System.out.printf("Index:%dms\n",
				(System.currentTimeMillis() - beginTimeStamp));
	}

	// / Get the edge with a distance roughly lower than radius from point p
	public HashSet<Edge> rangeQuery(IGeoPoint p, double radius) {
		return this.getEdgeIndex().rangeQuery(p, radius);
	}

	// / Get the edge with a distance roughly lower than radius from point p
	public HashSet<Edge> rangeQuery(GeoPoint p, double radius,
			double maxRadius, int minSize) {
		HashSet<Edge> result = null;
		while (radius <= maxRadius
				&& (result == null || result.size() <= minSize)) {
			result = rangeQuery(p, radius);
			radius *= 2;
		}
		return result;
	}

	/**
	 * Get the nearest edge of point p
	 * 
	 * @param p
	 * @return
	 */
	public Edge nearestEdge(IGeoPoint p) {
		Edge nn = this.edgeIndex.nearestNeighbor(p);
		return nn;
	}

	public HashSet<Edge> rangeQuery(GeoPoint p, double radius, double maxRadius) {
		return rangeQuery(p, radius, maxRadius, 0);
	}

	// / Get the vertex with a mbr
	public HashSet<Vertex> vertexRangeQuery(MBR rect) {
		return this.vertexIndex.RangeQuery(rect);
	}

	// / Get the vertex with a mbr
	public HashSet<Vertex> vertexRangeQuery(GeoPoint p, double radius) {
		double minLat, minLng, maxLat, maxLng;
		double d_radius = radius * Constants.D_PER_M; // radius in degree
		minLng = p.getLng() - d_radius;
		maxLng = p.getLng() + d_radius;
		minLat = p.getLat() - d_radius;
		maxLat = p.getLat() + d_radius;
		MBR rect = new MBR(minLng, minLat, maxLng, maxLat);
		return this.vertexIndex.RangeQuery(rect);
	}

	private void loadVertices(String fileName) throws NumberFormatException,
			IOException {
		this.mbr = MBR.EMPTY();
		// id,lng,lat
		vertices = new HashMap<Long, Vertex>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(fileName)));
		String Line = new String();
		while ((Line = reader.readLine()) != null) {
			// List<String> fields = StringUtils.SimpleSplit(Line, ',');
			String[] fields = Line.split(",");
			long id = Long.valueOf(fields[0]);
			double lat = Double.valueOf(fields[2]);
			double lng = Double.valueOf(fields[1]);
			Vertex v = new Vertex(id, lat, lng);
			vertices.put(id, v);
			this.mbr.include(new GeoPoint(lat, lng));
		}
		reader.close();
	}

	private void loadEdges(String fileName) throws NumberFormatException,
			IOException {
		edges = new HashMap<Long, Edge>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(fileName)));
		String Line = new String();
		while ((Line = reader.readLine()) != null) {
			// List<String> fields = StringUtils.SimpleSplit(Line, '\t');
			String[] fields = Line.split(",");
			assert fields.length == 5;
			long id = Long.valueOf(fields[0]);
			long startId = Long.valueOf(fields[3]);
			long endId = Long.valueOf(fields[4]);
			Vertex start = getVertices().get(startId);
			Vertex end = getVertices().get(endId);
			Edge e = new Edge(id, start, end);
			edges.put(id, e);
			start.RegisterEdge(e);
			end.RegisterEdge(e);
		}
		reader.close();
	}

	// / Load geometry information of the edge
	private void loadGeometry(String fileName) throws NumberFormatException,
			IOException {
		FileInputStream dataInputStream = new FileInputStream(fileName);
		InputStreamReader read = new InputStreamReader(dataInputStream);
		BufferedReader reader = new BufferedReader(read);
		String Line = new String();
		while ((Line = reader.readLine()) != null) {
			String[] fields = Line.split("\\t", 2);
			// ///List<String> fields= StringUtils.SimpleSplit(Line, '\t');
			long edgeId = Long.valueOf(fields[0]);
			Edge e = (Edge) this.edges.get(edgeId);
			if (e != null) {
				e.setGeoString(fields[1]);
			}
		}
		reader.close();
		read.close();
		dataInputStream.close();
	}

	// / Build grid index for road network
	private void buildRNIndex() {
		this.edgeIndex = new GridEdge(edges.values(), mbr, edgeCellSize);
		this.vertexIndex = new GridPoint(vertices.values(), mbr, vertexCellSize);
	}

}
