package edu.usc.infolab.util;

public enum ShapeType {
	NullShape(0), Point(1), PolyLine(3), Polygon(5), MultiPoint(8), PointZ(11), PolyLineZ(
			13), PolygonZ(15), MultiPointZ(18), PointM(21), PolyLineM(23);

	ShapeType(Integer val) {
		this.value = val;
	}

	private Integer value;

	public Integer getValue() {
		return value;
	}

}
