package org.usfirst.frc.team1251.robot;
/**
 * 
 * @author Eric Engelhart
 * I made this class because I needed a basic point object that used ints, not floats or doubles
 * I needed the points for lines to use to figure out what lines from the pi were actually targets
 */
public class Point {
	private int X;
	private int Y;
	
	public Point (int X, int Y){
		this.X = X;
		this.Y = Y;
	}

	public int getX() {
		return X;
	}

	public int getY() {
		return Y;
	}
	

}
