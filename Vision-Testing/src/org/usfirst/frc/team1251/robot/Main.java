package org.usfirst.frc.team1251.robot;

import org.usfirst.frc.team1251.robot.vision.Vision;

public class Main {
	private static boolean lockTargetsPressed = true;
	private static boolean fireButtonPressed = false;
	public static void main(String[] args){
		Vision vision = new Vision();
		vision.lockTargets();
	}
}
