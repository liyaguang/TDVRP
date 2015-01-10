package edu.usc.infolab.roadnetwork;

import java.util.List;

import edu.usc.infolab.base.Constants;
import edu.usc.infolab.roadnetwork.outwraper.SegIdxObject;
import edu.usc.infolab.roadnetwork.outwraper.TypeObject;
import edu.usc.infolab.util.Utility;

/// Driving Route
public class Polyline implements ILine {
	public static class ProjectionResult {
		IGeoPoint p = null;
		int type;
		int segId;

		public IGeoPoint getPoint() {
			return this.p;
		}

		public int getType() {
			return this.type;
		}

		public int getSegId() {
			return this.segId;
		}

		public ProjectionResult(IGeoPoint p, int type, int segId) {
			// TODO Auto-generated constructor stub
			this.p = p;
			this.type = type;
			this.segId = segId;
		}

		public ProjectionResult(IGeoPoint p, int type) {
			// TODO Auto-generated constructor stub
			this.p = p;
			this.type = type;
			this.segId = -1;
		}
	}

	public MBR getMBR() {
		double minLat = Double.POSITIVE_INFINITY, minLng = Double.POSITIVE_INFINITY;
		double maxLat = Double.NEGATIVE_INFINITY, maxLng = Double.NEGATIVE_INFINITY;
		int pointCount = points.size();
		for (int i = 0; i < pointCount; i++) {
			minLat = Math.min(minLat, points.get(i).getLat());
			minLng = Math.min(minLng, points.get(i).getLng());
			maxLat = Math.max(maxLat, points.get(i).getLat());
			maxLng = Math.max(maxLng, points.get(i).getLng());
		}
		MBR mbr = new MBR(minLng, minLat, maxLng, maxLat);
		return mbr;
	}

	public double getLength() {
		return getLength(false);
	}

	private double getLength(boolean isPrecise) {
		double tmpLen = 0;
		for (int i = 0; i < this.points.size() - 1; i++) {
			if (isPrecise) {
				tmpLen += GeoPoint.GetPreciseDistance(points.get(i),
						points.get(i + 1));
			} else {
				tmpLen += GeoPoint
						.GetDistance(points.get(i), points.get(i + 1));
			}
		}
		return tmpLen;
	}

	public Polyline(List<IGeoPoint> points) {
		this.points = points;
	}

	// public static int projectFrom(IGeoPoint start, IGeoPoint end, IGeoPoint
	// p,
	// IGeoPoint result) {
	// int type = 0;
	// double vY = end.getLat() - start.getLat();
	// double vX = end.getLng() - start.getLng();
	// double wY = p.getLat() - start.getLat();
	// double wX = p.getLng() - start.getLng();
	//
	// // LAT,LNG Error
	// double vY_m = vY * Constants.M_PER_LAT; //
	// double vX_m = vX * Constants.M_PER_LNG; //
	// double wY_m = wY * Constants.M_PER_LAT;
	// double wX_m = wX * Constants.M_PER_LNG;
	//
	// double bY, bX;
	//
	// double c1 = wY_m * vY_m + wX_m * vX_m;
	// double c2 = vY_m * vY_m + vX_m * vX_m;
	//
	// GeoPoint.INVALID();
	//
	// if (c1 <= 0) {
	// // when the given point is left of the source point
	// // result = start;
	// result.setLat(start.getLat());
	// result.setLng(start.getLng());
	// } else if (c2 <= c1) {
	// // when the given point is right of the target point
	// // result = end;
	// result.setLat(end.getLat());
	// result.setLng(end.getLng());
	// } else // between the source point and target point
	// {
	// double b = c1 / c2;
	// bY = start.getLat() + b * vY;
	// bX = start.getLng() + b * vX;
	// // result = new GeoPoint(bY, bX);
	// result.setLat(bY);
	// result.setLng(bX);
	// }
	// type = (short) (c1 / c2);
	// return type;
	// }

	public static ProjectionResult projectFrom(IGeoPoint start, IGeoPoint end,
			IGeoPoint p) {
		int type = 0;
		IGeoPoint result = null;
		double vY = end.getLat() - start.getLat();
		double vX = end.getLng() - start.getLng();
		double wY = p.getLat() - start.getLat();
		double wX = p.getLng() - start.getLng();

		// LAT,LNG Error
		double vY_m = vY * Constants.M_PER_LAT; //
		double vX_m = vX * Constants.M_PER_LNG; //
		double wY_m = wY * Constants.M_PER_LAT;
		double wX_m = wX * Constants.M_PER_LNG;

		double bY, bX;

		double c1 = wY_m * vY_m + wX_m * vX_m;
		double c2 = vY_m * vY_m + vX_m * vX_m;

		GeoPoint.INVALID();

		if (c1 <= 0) {
			// when the given point is left of the source point
			// result = start;
			result = start;
		} else if (c2 <= c1) {
			// when the given point is right of the target point
			// result = end;
			result = end;
		} else // between the source point and target point
		{
			double b = c1 / c2;
			bY = start.getLat() + b * vY;
			bX = start.getLng() + b * vX;
			result = new GeoPoint(bY, bX);
		}
		type = (short) (c1 / c2);
		return new ProjectionResult(result, type, 0);
	}

	// Get projection from certain point
	public ProjectionResult projectFrom(IGeoPoint p) {
		int type = -1;
		double minDist = Double.POSITIVE_INFINITY;
		IGeoPoint result = GeoPoint.INVALID();
		int segIdx = 0;
		for (int i = 0; i < this.points.size() - 1; i++) {
			ProjectionResult projectionResult = Polyline.projectFrom(
					points.get(i), points.get(i + 1), p);
			double tmpDist = GeoPoint.GetDistance2(projectionResult.getPoint(),
					p);
			if (tmpDist <= minDist) {
				if (projectionResult.getType() == 0 || type != 0) {
					// good projection is true or tmpType==0
					type = projectionResult.getType();
					minDist = tmpDist;
					result = projectionResult.getPoint();
					segIdx = i;
				}
			}
			// break;
		}
		return new ProjectionResult(result, type, segIdx);
	}

	public double distFrom(IGeoPoint p, TypeObject typeObj) {
		return Math.sqrt(dist2From(p, typeObj));
	}

	public double dist2From(IGeoPoint p, TypeObject typeObj) {
		ProjectionResult projectResult = projectFrom(p);
		return GeoPoint.GetDistance2(projectResult.getPoint(), p);
	}

	// / Get the distance from a point to this polyline
	public double distFrom(GeoPoint p) {
		TypeObject typeObj = new TypeObject(0);
		return distFrom(p, typeObj);
	}

	// / Distance from p to the end of the polyline(by this route)
	public double endDistFrom(GeoPoint p, TypeObject typeObj) {
		ProjectionResult projectResult = projectFrom(p);
		typeObj.type = projectResult.getType();
		double distance = GeoPoint.GetDistance(p,
				points.get(projectResult.getSegId() + 1));
		for (int i = projectResult.getSegId() + 1; i < points.size() - 1; i++) {
			distance += GeoPoint.GetDistance(points.get(i), points.get(i + 1));
		}
		return distance;
	}

	// / Get the distance between the projections on the polyline
	public double distOnLine(GeoPoint from, GeoPoint to) {
		GeoPoint fromProject = GeoPoint.INVALID(), toProject = GeoPoint
				.INVALID();
		ProjectionResult fromResult, toResult;
		fromResult = projectFrom(from);
		toResult = projectFrom(to);
		double distance = 0;
		// Debug.Assert(fromType == 0 && toType == 0);
		if (fromResult.getSegId() == toResult.getSegId()) {
			distance = GeoPoint.GetDistance(fromProject, toProject);
		} else {
			distance = GeoPoint.GetDistance(fromProject,
					points.get(fromResult.getSegId() + 1));
			for (int i = fromResult.getSegId() + 1; i < toResult.getSegId(); i++) {
				distance += GeoPoint.GetDistance(points.get(i),
						points.get(i + 1));
			}
			distance += GeoPoint.GetDistance(points.get(toResult.getSegId()),
					toProject);
		}
		// distance+=GeoPoint.GetDistance(fromProject,)
		return distance;
	}

	public static double distFrom(GeoPoint start, GeoPoint end, GeoPoint p) {
		ProjectionResult projectResult = projectFrom(start, end, p);
		double distance = 0;
		distance = GeoPoint.GetDistance(p, projectResult.getPoint());
		return distance;
	}

	// / Calculate the cosine value with line p1,p2
	public double cosWith(IGeoPoint p1, IGeoPoint p2) {
		double wY, wX;
		double vY, vX;
		ProjectionResult projectResult = projectFrom(p1);
		IGeoPoint start = GeoPoint.INVALID(), end = GeoPoint.INVALID();
		start = this.points.get(projectResult.getSegId());
		end = this.points.get(projectResult.getSegId() + 1);
		vY = Utility.refineDoubleZero(end.getLat() - start.getLat());
		vX = Utility.refineDoubleZero(end.getLng() - start.getLng());
		wY = Utility.refineDoubleZero(p2.getLat() - p1.getLat());
		wX = Utility.refineDoubleZero(p2.getLng() - p1.getLng());
		double sum = vY * wY + vX * wX;
		double result = sum
				/ Math.sqrt(1.0 * (vY * vY + vX * vX) * (wY * wY + wX * wX));
		return result;
	}

	public IGeoPoint predict(IGeoPoint start, double distance) {
		IGeoPoint target = this.points.get(this.points.size() - 1);
		ProjectionResult projectResult = projectFrom(start);
		int segIdx = projectResult.getSegId();
		double currentDistance = 0;
		while (segIdx < this.points.size() - 1) {
			double length = GeoPoint.GetDistance(start, points.get(segIdx + 1));
			if (currentDistance + length >= distance) {
				double leftLength = distance - currentDistance;
				double ratio = leftLength / length;
				double lat = start.getLat() + ratio
						* (points.get(segIdx + 1).getLat() - start.getLat());
				double lng = start.getLng() + ratio
						* (points.get(segIdx + 1).getLng() - start.getLng());
				target = new GeoPoint(lat, lng);
				break;
			} else {
				currentDistance += length;
				start = points.get(segIdx + 1);
				++segIdx;
			}
		}
		return target;
	}

	// private double length = -1;
	private List<IGeoPoint> points;

	public List<IGeoPoint> getPoints() {
		return points;
	}

	public int getCount() {
		return this.points.size();
	}

	private MBR mbr = null;

	public MBR MBR() {
		if (mbr == null) {
			mbr = getMBR();
		}
		return mbr;
	}

	@Override
	public IGeoPoint getStart() {
		// TODO Auto-generated method stub
		IGeoPoint start = null;
		if (points.size() > 0) {
			start = points.get(0);
		}
		return start;
	}

	@Override
	public IGeoPoint getEnd() {
		// TODO Auto-generated method stub
		IGeoPoint end = null;
		if (points.size() > 0) {
			end = points.get(points.size() - 1);
		}
		return end;
	}

	@Override
	public double distFrom(IGeoPoint p) {
		// TODO Auto-generated method stub
		return 0;
	}
}
