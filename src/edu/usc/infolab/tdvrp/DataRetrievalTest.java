package edu.usc.infolab.tdvrp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.management.Query;

import edu.usc.infolab.lib.GeoPoint;

public class DataRetrievalTest {
	private static final String logFileName = "data/history.txt";

	public static void main(String[] argv) throws IOException {
		List<GeoPoint> points = getNodes();
		// genDistFile(points);
		int startTime = 12, endTime = 36;
		String outFileName = "data/dynamic_dist.txt";
		// String outFileName = "data/dist.txt";
		// genDistFile(points, startTime, endTime, outFileName);
		// genDistFile(points, startTime, endTime, outFileName);
	}

	// public static void genStaticDistFile(List<GeoPoint> points)
	// throws IllegalStateException, IOException {
	// String logFileName = "data/history.txt";
	// String outFileName = "data/dist.txt";
	// String day = "Tuesday";
	// int time = 30;
	// int size = points.size();
	// PrintWriter pr = new PrintWriter(outFileName, "UTF-8");
	// PrintWriter logWriter = new PrintWriter(logFileName, "UTF-8");
	// for (int s = 0; s < size; ++s) {
	// for (int e = 0; e < size; ++e) {
	// String travelTime = "0";
	// if (s != e) {
	// String result = QueryBuilder.getResult(points.get(s),
	// points.get(e), time, day);
	// logWriter.println(String.format("n%d->n%d\t%s", s, e,
	// result));
	// String[] fs = result.split("@");
	// if (fs.length > 1) {
	// travelTime = fs[0]
	// .substring(fs[0].lastIndexOf(';') + 1);
	// travelTime = travelTime.substring(0,
	// travelTime.indexOf('-'));
	//
	// }
	// }
	// pr.write(String.format("%.3f", Double.parseDouble(travelTime)));
	// if (e != size - 1) {
	// pr.write("\t");
	// }
	// }
	// pr.println();
	// }
	// logWriter.close();
	// pr.close();
	// }

	// public static void genDistFile(List<GeoPoint> points, int startTime,
	// int endTime, String outFileName) throws IllegalStateException,
	// IOException {
	// String day = "Tuesday";
	// int size = points.size();
	// PrintWriter pr = new PrintWriter(outFileName, "UTF-8");
	// PrintWriter logWriter = new PrintWriter(new FileOutputStream(
	// logFileName, true));
	// pr.println(String.format("%d\t%d\t%d", size, startTime, endTime));
	// for (int s = 0; s < size; ++s) {
	// for (int e = 0; e < size; ++e) {
	// System.out.println(s + "->" + e);
	// String travelTime = "0";
	// for (int t = startTime; t <= endTime; ++t) {
	// if (s != e)
	// try {
	// String result = QueryBuilder.getResult(
	// points.get(s), points.get(e), t, day);
	// logWriter.println(String.format("n%d->n%d@%d\t%s",
	// s, e, t, result));
	// if (result != null) {
	// String[] fs = result.split("@");
	// if (fs.length > 1) {
	// travelTime = fs[0].substring(fs[0]
	// .lastIndexOf(';') + 1);
	// travelTime = travelTime.substring(0,
	// travelTime.indexOf('-'));
	// }
	// }
	// } catch (Exception e2) {
	// // TODO: handle exception
	// System.out.println(String.format(
	// "Error at: %d->%d@%d", s, e, t));
	// }
	// pr.println(String.format("%d\t%d\t%d\t%.3f", s, e, t,
	// Double.parseDouble(travelTime)));
	// }
	// }
	// }
	// logWriter.close();
	// pr.close();
	// }

	public static double[][][] readStaticDistArray(String filename, int times)
			throws IOException {
		// read distance array
		List<String> lines = Files.readAllLines(Paths.get(filename),
				StandardCharsets.UTF_8);
		int count = lines.size();
		String[] fs = lines.get(0).split("\t"); // meta data of dynamic array
		assert fs.length == 3;
		int N = Integer.parseInt(fs[0]);
		double[][][] array = new double[N][N][times];
		int startTime = Integer.parseInt(fs[1]), endTime = Integer
				.parseInt(fs[2]);
		assert startTime == endTime;
		for (int i = 1; i < count; ++i) {
			fs = lines.get(i).split("\t");
			int s = Integer.parseInt(fs[0]), e = Integer.parseInt(fs[1]);
			double travelTime = Double.parseDouble(fs[3]);
			for (int t = 0; t < times; ++t) {
				array[s][e][t] = travelTime;
			}
		}
		return array;
	}

	public static double[][][] readDistArray(String filename)
			throws IOException {
		// read distance array
		List<String> lines = Files.readAllLines(Paths.get(filename),
				StandardCharsets.UTF_8);
		int count = lines.size();
		String[] fs = lines.get(0).split("\t"); // meta data of dynamic array
		assert fs.length == 3;
		int N = Integer.parseInt(fs[0]);
		int startTime = Integer.parseInt(fs[1]), endTime = Integer
				.parseInt(fs[2]);
		double[][][] array = new double[N][N][endTime - startTime + 1];
		// initial value is 0
		for (int i = 1; i < count; ++i) {
			fs = lines.get(i).split("\t");
			int s = Integer.parseInt(fs[0]), e = Integer.parseInt(fs[1]);
			int t = Integer.parseInt(fs[2]);
			double travelTime = Double.parseDouble(fs[3]);
			array[s][e][t - startTime] = travelTime;
		}
		return array;
	}

	public static List<GeoPoint> getNodes() throws IOException {
		String nodeFileName = "data/Nodes.csv";
		String sampledNodeFileName = "data/sample_nodes.csv";
		PrintWriter pw = new PrintWriter(sampledNodeFileName, "UTF-8");
		List<String> lines = Files.readAllLines(Paths.get(nodeFileName),
				StandardCharsets.UTF_8);
		// get sample points
		List<GeoPoint> points = new ArrayList<GeoPoint>();
		int stepSize = 500, count = 10;
		assert lines.size() > stepSize * count;
		for (int i = 0; i < count; ++i) {
			String line = lines.get(i * stepSize);
			String[] fields = line.split(",");
			double lat = Double.parseDouble(fields[1]);
			double lng = Double.parseDouble(fields[2]);
			GeoPoint point = new GeoPoint(lat, lng);
			points.add(point);
			pw.println(String.format("n%d,%s", i, point.toString()));
		}
		pw.close();
		return points;
	}
}
