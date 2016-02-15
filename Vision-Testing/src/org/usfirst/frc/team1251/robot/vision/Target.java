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
	private double topY;
	private double bottomY;
	private double leftX;
	private double rightX;
	
	public Target (double width, double height, double topY, double bottomY, double leftX, double rightX){
		this.width = width;
		this.height = height;
		this.area = width * height;
		this.centerX = (leftX + rightX)/2;
		this.centerY = (topY + bottomY) / 2;
		this.topY = topY;
		this.bottomY = bottomY;
		this.leftX = leftX;
		this.rightX = rightX;
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

	public double getTopY() {
		return topY;
	}

	public double getBottomY() {
		return bottomY;
	}

	public double getLeftX() {
		return leftX;
	}

	public double getRightX() {
		return rightX;
	}
}
