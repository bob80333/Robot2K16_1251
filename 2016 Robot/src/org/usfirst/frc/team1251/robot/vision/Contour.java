package org.usfirst.frc.team1251.robot.vision;

public class Contour {
	private double area;
	private double centerX;
	private double centerY;
	private double width;
	private double height;
	private double solidity;
	
	public Contour (double area, double centerX, double centerY, double width, double height, double solidity){
		this.area = area;
		this.centerX = centerX;
		this.centerY = centerY;
		this.width = width;
		this.height = height;
		this.solidity = solidity;
	}

	public double getArea() {
		return area;
	}

	public double getHeight() {
		return height;
	}

	public double getCenterX() {
		return centerX;
	}

	public double getCenterY() {
		return centerY;
	}

	public double getWidth() {
		return width;
	}
}
