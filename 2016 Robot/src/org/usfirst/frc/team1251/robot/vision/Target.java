package org.usfirst.frc.team1251.robot.vision;

/**
 * 
 * @author Eric Engelhart
 * An object to represent targets
 */
public class Target {
	private double width;
	private double height;
	private double area;
	private double centerX;
	private double centerY;
    private double actualDistance;
    private Line left;
    private Line right;
	
	public Target (double width, double height, double area, double centerX, double centerY, double actualDistance, Line left, Line right){
		this.width = width;
		this.height = height;
		this.area = area;
		this.centerX = centerX;
		this.centerY = centerY;
		this.actualDistance = actualDistance;
		this.left = left;
		this.right = right;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public double getArea() {
		return area;
	}

	public double getCenterX() {
		return centerX;
	}

	public double getCenterY() {
		return centerY;
	}

	public double getActualDistance() {
		return actualDistance;
	}

	public Line getLeft() {
		return left;
	}

	public Line getRight() {
		return right;
	}


}
