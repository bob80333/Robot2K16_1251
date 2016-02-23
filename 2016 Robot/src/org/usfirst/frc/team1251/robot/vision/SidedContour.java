package org.usfirst.frc.team1251.robot.vision;

public class SidedContour {
	private Line left;
	private Line right;
	private Contour cont;
	public SidedContour(Line line, Contour cont, boolean isRight){
		if (isRight)
			this.right = line;
		else
			this.left = line;
		this.cont = cont;
	}
	public Line getLeft() {
		return left;
	}
	public void setLeft(Line left) {
		this.left = left;
	}
	public Line getRight() {
		return right;
	}
	public void setRight(Line right) {
		this.right = right;
	}
	public Contour getCont() {
		return cont;
	}

}
