package edu.usc.infolab.roadnetwork;

public interface ILine {
	public IGeoPoint getStart();

	public IGeoPoint getEnd();

	public MBR getMBR();
	
	public double distFrom(IGeoPoint p);
}
