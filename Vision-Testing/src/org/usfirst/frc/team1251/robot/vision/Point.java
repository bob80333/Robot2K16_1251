package org.usfirst.frc.team1251.robot.vision;
/**
 * 
 * @author Eric Engelhart
 * I made this class because I needed a basic point object for my lines
 */
public class Point {
	private double X;
	private double Y;
	
	public Point (double X, double Y){
		this.X = X;
		this.Y = Y;
	}

	public double getX() {
		return X;
	}

	public double getY() {
		return Y;
	}
	

}
