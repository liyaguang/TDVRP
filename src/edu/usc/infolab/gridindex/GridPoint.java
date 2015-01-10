package edu.usc.infolab.gridindex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.usc.infolab.roadnetwork.*;

public class GridPoint {

	public GridPoint(Collection<Vertex> vertices, MBR mbr, double cellSize) {
		nCol = (int) (Math.ceil(mbr.width() / cellSize));
		nRow = (int) (Math.ceil(mbr.height() / cellSize));
		this.cellSize = cellSize;
		this.mbr = mbr;
		buildIndex(vertices);
	}

	private int getCell(IGeoPoint p) {
		int row = getRow(p.getLat());
		int col = getColumn(p.getLng());
		return row * nCol + col;
	}

	// / Given a longitude, get its column index
	private int getColumn(double lng) {
		// Debug.Assert(lng >= mbr.MinLng && lng <= mbr.MaxLng);
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

	private void buildIndex(Collection<Vertex> vertices) {
		// insert edges into the grid
		for (Vertex v : vertices) {
			// List<int> ids = getCells(v.ToPoint());
			int id = getCell(v.ToPoint());
			List<Vertex> list = dict.get(id);
			if (list == null) {
				list = new ArrayList<Vertex>();
				dict.put(id, list);
			}
			list.add(v);
		}
	}

	public HashSet<Vertex> RangeQuery(MBR rect) {
		HashSet<Vertex> result = new HashSet<Vertex>();
		List<Integer> cands = getCells(rect);
		int cands_count = cands.size();
		for (int i = 0; i < cands_count; i++) {
			List<Vertex> vertices = dict.get(cands.get(i));
			if (vertices != null) {
				for (Vertex v : vertices) {
					if (rect.cover(v.ToPoint())) {
						result.add(v);
					}
				}
			}
		}
		return result;
	}

	private HashMap<Integer, List<Vertex>> dict = new HashMap<Integer, List<Vertex>>();

	private final int nCol;
	private final int nRow;
	private final double cellSize;
	private final MBR mbr;
}
