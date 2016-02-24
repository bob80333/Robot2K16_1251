package org.usfirst.frc.team1251.robot.vision;

/**
 * This code finds angles & distances to the target.
 */
import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Vision implements Runnable{

	private List<Contour> contours = new ArrayList<>();
	private final double cameraCenterX = 360.0;
	private final double cameraCenterY = 640.0;
	private static double anglesToTarget[] = {};
	private static double distancesToTarget[] = {};
	private static boolean lockTargetsPressed;
	private static boolean fireButtonPressed;
	
	public void lockTargets(){
		updateDataFromNetwork();
		
		findAngleToCamera();
	}
	
	/**
	 * Gets the data from network tables
	 * TODO: Make a good name for the network contours report
	 */
	private void updateDataFromNetwork(){
		if(NetworkTable.getTable("GRIP") != null){
			for (int i = 0; i < NetworkTable.getTable("GRIP").getNumberArray("myContoursReport/area", new double[0]).length; i++){
				contours.add(new Contour(getNetwork("myContoursReport/area", i), getNetwork("myContoursReport/centerX", i),
						getNetwork("myContoursReport/centerY", i), getNetwork("myContoursReport/width", i),
						getNetwork("myContoursReport/height", i), getNetwork("myContoursReport/solidity", i)));
			}
		}
	}
	
	private void findAngleToCamera(){
		double targetCenterX = 0.0;
		double targetCenterY = 0.0;
		for(int i = 0; i < contours.size(); i++){
			targetCenterX = contours.get(i).getCenterX();
			targetCenterY = contours.get(i).getCenterY();
			anglesToTarget[i] = Math.tan((targetCenterX - cameraCenterX)/(distancesToTarget[i]));
		}
	}
	
	
	private double getNetwork(String str, int i){
		return NetworkTable.getTable("GRIP").getNumberArray("str", new double[0])[i];
	}

	@Override
	public void run() {
		if (lockTargetsPressed){
			lockTargets();
		}else if (fireButtonPressed){
			// write code to fire the shooter here
		}else{
			try {
				wait();
			} catch (InterruptedException e) {

			}
		}
	}
}
