package edu.usc.infolab.gridindex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.usc.infolab.base.Constants;
import edu.usc.infolab.roadnetwork.Edge;
import edu.usc.infolab.roadnetwork.IGeoPoint;
import edu.usc.infolab.roadnetwork.MBR;

public class GridEdge {

	public GridEdge(Collection<Edge> edges, MBR mbr, double cellSize) {
		nCol = (int) (Math.ceil(mbr.width() / cellSize));
		nRow = (int) (Math.ceil(mbr.height() / cellSize));
		this.cellSize = cellSize;
		this.mbr = mbr;
		buildIndex(edges);
	}

	private void buildIndex(Collection<Edge> edges) {
		// insert edges into the grid
		for (Edge e : edges) {
			List<Integer> ids = getCells(e);
			for (int j = 0; j < ids.size(); j++) {
				List<Edge> list = dict.get(ids.get(j));
				if (list == null) {
					list = new ArrayList<Edge>();
					dict.put(ids.get(j), list);
				}
				list.add(e);
			}
		}
	}

	private int getCell(IGeoPoint p) {
		int row = getRow(p.getLat());
		int col = getColumn(p.getLng());
		return row * nCol + col;
	}

	// / Get the cells that might contain Edge e
	private List<Integer> getCells(Edge e) {
		return getCells(e.getMBR());
	}

	private List<Integer> getCells(MBR mbr) {
		List<Integer> rst = new ArrayList<Integer>();
		int c1 = getCell(mbr.topLeft());
		int c2 = getCell(mbr.bottomRight());
		int c1col = c1 % nCol;
		int c2col = c2 % nCol;

		int c1row = (c1 - c1col) / nCol;
		int c2row = (c2 - c2col) / nCol;

		int ncol = c2col - c1col + 1;
		int nrow = c2row - c1row + 1;
		for (int i = 0; i < nrow; i++) {
			for (int j = 0; j < ncol; j++) {
				rst.add(c1col + j + (c1row + i) * nCol);
			}
		}
		return rst;
	}

	// / Given a longitude, get its column index
	private int getColumn(double lng) {
		if (lng <= mbr.minLng()) {
			return 0;
		}
		if (lng >= mbr.maxLng()) {
			return nCol - 1;
		}
		return (int) ((lng - mbr.minLng()) / cellSize);
	}

	// / Given a latitude, get its row index
	private int getRow(double lat) {
		// Debug.Assert(lat >= mbr.MinLat && lat <= mbr.MaxLat);
		if (lat <= mbr.minLat()) {
			return 0;
		}
		if (lat >= mbr.maxLat()) {
			return nRow - 1;
		}
		return (int) ((lat - mbr.minLat()) / cellSize);
	}

	// / Get the edge with a distance roughly lower than radius from point p
	public HashSet<Edge> rangeQuery(IGeoPoint p, double radius) {
		HashSet<Edge> result = new HashSet<Edge>();
		List<Integer> cands = null;
		// get mbr
		double d_radius = radius * Constants.D_PER_M; // radius in degree
		double minLat, minLng, maxLat, maxLng;
		minLng = p.getLng() - d_radius;
		maxLng = p.getLng() + d_radius;
		minLat = p.getLat() - d_radius;
		maxLat = p.getLat() + d_radius;
		MBR rect = new MBR(minLng, minLat, maxLng, maxLat);
		cands = getCells(rect);
		int cands_count = cands.size();
		for (int i = 0; i < cands_count; i++) {
			List<Edge> edges = dict.get(cands.get(i));
			if (edges != null) {
				int count = edges.size();
				for (int j = 0; j < count; j++) {
					if (edges.get(j).distFrom(p) <= radius) {
						result.add(edges.get(j));
					}

				}
			}
		}
		return result;
	}

	public Edge nearestNeighbor(IGeoPoint p) {
		double maxRadius = 200, curRadius = 25;
		HashSet<Edge> cands = new HashSet<Edge>();
		Edge nn = null;
		while (curRadius < maxRadius && cands.isEmpty()) {
			cands = rangeQuery(p, curRadius);
			curRadius *= 2;
		}
		// get nn
		double minDist = Double.MAX_VALUE;
		for (Edge e : cands) {
			double curDist = e.distFrom(p);
			if (curDist < minDist) {
				minDist = curDist;
				nn = e;
			}
		}
		return nn;
	}

	// / Get the edge with a distance lower than radius from point p
	public HashSet<Edge> rangeQuery(MBR mbr) {
		throw new UnsupportedOperationException();
	}

	private HashMap<Integer, List<Edge>> dict = new HashMap<Integer, List<Edge>>();

	private final int nCol;
	private final int nRow;
	private final double cellSize;
	private final MBR mbr;
}
