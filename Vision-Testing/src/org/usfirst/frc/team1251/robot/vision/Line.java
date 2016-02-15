package org.usfirst.frc.team1251.robot.vision;

/**
 * 
 * @author Eric Engelhart
 * I made this class because I needed a basic Line object that supported angle and angle as well as points
 */
public class Line {
	
	private Point point1;
	private Point point2;
	private double angle;
	private double length;
	
	public Line(Point point1, Point point2, double angle, double length){
		this.point1 = point1;
		this.point2 = point2;
		this.angle = angle;
		this.length = length;
	}
	
	public Point getPoint1() {
		return point1;
	}
	
	public Point getPoint2() {
		return point2;
	}
	
	public double getAngle() {
		return angle;
	}

	public double getLength() {
		return length;
	}

	public boolean hasPoint(Point point){
		// return statement that does the stuff instead of an if #advanced
		return (point1.equals(point) || point2.equals(point));
	}

}
