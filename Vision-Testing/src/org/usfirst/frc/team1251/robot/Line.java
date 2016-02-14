package org.usfirst.frc.team1251.robot;

/**
 * 
 * @author Eric Engelhart
 * I made this class because I needed a basic Line object that used ints, not floats or doubles
 * The lines are used for representing the lines from GRIP to figure out which ones are targets
 */
public class Line {
	private Point point1;
	private Point point2;
	public Line(Point point1, Point point2){
		this.point1 = point1;
		this.point2 = point2;
	}
	
	public Point getPoint1() {
		return point1;
	}
	
	public Point getPoint2() {
		return point2;
	}
	
	public boolean hasPoint(Point point){
		// return statement that does the stuff instead of an if #advanced
		return (point1.equals(point) || point2.equals(point));
	}

}
