package edu.usc.infolab.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.usc.infolab.roadnetwork.Edge;
import edu.usc.infolab.roadnetwork.GeoPoint;
import edu.usc.infolab.roadnetwork.Graph;
import edu.usc.infolab.roadnetwork.IGeoPoint;
import edu.usc.infolab.roadnetwork.Vertex;

public class SensorQueryTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String vertexFile = "data/subNodes.csv";
		String edgeFile = "data/subEdges.csv";
		String sensorFile = "data/sub-sensors.csv";
		IGeoPoint query = new GeoPoint(34.02995, -118.25658);
		try {
			Graph g = new Graph(vertexFile, edgeFile);
			List<Vertex> sensors = parseSensorFile(sensorFile);
			for (Vertex sensor : sensors) {
				Edge e = g.nearestEdge(sensor);
				if (e != null) {
					double dist = e.distFrom(sensor);
					System.out.println(String.format("%f, %s, %s", dist,
							sensor.toString(), e.toString()));
				}
			}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static List<Vertex> parseSensorFile(String sensorFile) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(sensorFile)));
		String line = null;
		List<Vertex> vertices = new ArrayList<Vertex>();
		while ((line = br.readLine()) != null) {
			String[] fields = line.split(",");
			assert fields.length == 4;
			long id = Long.valueOf(fields[0]);
			double lng = Double.valueOf(fields[1]);
			double lat = Double.valueOf(fields[2]);
			Vertex v = new Vertex(id, lat, lng);
			vertices.add(v);
		}
		br.close();
		return vertices;
	}

}
