package org.usfirst.frc.team1251.robot.vision;

/**
 * This code finds angles & distances to the target.
 */
import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Vision implements Runnable{

	private List<Contour> contours = new ArrayList<>();
	private final double cameraCenterX = 640.0;
	private static double[] anglesToTarget = {};
	private static double[] distancesToTarget = {};
	//TODO: Change the distance values to better, newer test data
	private double[][] distanceTable = {{1.0, 1.0, 2.0, 2.0, 3.0, 3.0, 4.0, 4.0, 5.0, 5.0, 6.0, 6.0, 7.0, 7.0, 8.0, 8.0, 9.0, 9.0, 10.0, 10.0}, 
										{35000.0, 31000.0, 15470.0, 15300.0, 9040.0, 8880.0, 4140.0, 3980.0, 2170.0, 2060.0, 1540.0, 1470.0, 1110.0, 1040.0, 740.0, 680.0, 580.0, 530.0, 430.0, 390.0}};
	private static boolean lockTargetsPressed = true;
	private static boolean fireButtonPressed;
	
	public void lockTargets(){
		System.out.println("lock targets starting");
		updateDataFromNetwork();
		System.out.println("network data updated");
		findDistanceToCamera();
		System.out.println("network data updated");
		findAngleToCamera();
		System.out.println("network data updated");
		logCalculatedData();
		System.out.println("network data updated");
	}
	
	/**
	 * Gets the data from network tables
	 * TODO: Make a good name for the network contours report
	 */
	private void updateDataFromNetwork(){
		//if(NetworkTable.getTable("GRIP") != null){
		int i = 0;
		NetworkTable grip = NetworkTable.getTable("GRIP/myContoursReport");
		grip.initialize();
		System.out.println(grip.isConnected());
		System.out.println(grip.isPersistent("myContoursReport"));
		System.out.println(grip.isServer());
		System.out.println(grip.getNumberArray("area", new double[0]).length);
			for (double contour : grip.getNumberArray("area", new double[0])){
				System.out.println(grip.getNumberArray("area", new double[0]));
				contours.add(new Contour(grip.getNumberArray("area", new double[0])[i], grip.getNumberArray("centerX", new double[0])[i],
						grip.getNumberArray("centerY", new double[0])[i], grip.getNumberArray("width", new double[0])[i],
						grip.getNumberArray("height", new double[0])[i], grip.getNumberArray("solidity", new double[0])[i]));
				System.out.println("getting data");
				i++;
			}
		//}
	}
	
	private void findDistanceToCamera(){
		double yIntercept = 0.0;
		for (int i = 0; i < contours.size(); i++){
			int previousJ = 0;
			for (int j = 1; j < distanceTable.length; j++){
				if (contours.get(i).getArea() > distanceTable[1][j]
						&& contours.get(i).getArea() < distanceTable[1][previousJ]){
					yIntercept = -(distanceTable[1][j] - distanceTable[1][previousJ] * distanceTable[1][j]) + distanceTable[0][previousJ];
					distancesToTarget[i] = 1/(distanceTable[1][j] - distanceTable[1][previousJ]) * (contours.get(i).getArea() - yIntercept);
					previousJ++;
				}
			}
		}
		System.out.println(distancesToTarget.length);
	}
	
	private void findAngleToCamera(){
		double targetCenterX = 0.0;
		for(int i = 0; i < contours.size(); i++){
			targetCenterX = contours.get(i).getCenterX();
			anglesToTarget[i] = Math.tan((targetCenterX - cameraCenterX)/(distancesToTarget[i]));
		}
	}
	
	private void logCalculatedData(){
		for (int i = 0; i < distancesToTarget.length; i++){
			System.out.println("The distance for "+ i +" is: " + distancesToTarget[i]);
		}
		for (int i  = 0; i < anglesToTarget.length; i++){
			System.out.println("The angle for " + i + " is: " + anglesToTarget[i]);
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
		/*}else{
			try {
				wait();
			} catch (InterruptedException e) {

			}*/
		}
	}
}
