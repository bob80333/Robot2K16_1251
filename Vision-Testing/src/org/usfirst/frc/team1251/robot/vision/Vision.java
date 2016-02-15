package org.usfirst.frc.team1251.robot.vision;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Vision {
	
	private Hashtable<Point, Line> startingPoints;
	private Hashtable<Point, Line> endingPoints;
	private List<Line> lines= new ArrayList<>();
	private List<Target> targets = new ArrayList<>();
	private Point lastPoint1;
	private Point lastPoint2;
	private Point lastPoint1Backwards;
	private Point lastPoint2Backwards;
	double[] lineHeights = {};
	double[] lineAngles = {};
	double[] lineX1s = {};
	double[] lineY1s = {};
	double[] lineX2s = {};
	double[] lineY2s = {};
	
	public void findTargets(){
		updateArraysFromNetwork();
		updateTablesAndList();
		lastPoint1 = lines.get(0).getPoint1();
		lastPoint2 = lines.get(0).getPoint2();
		lastPoint1Backwards = lines.get(lines.size()-1).getPoint1();
		lastPoint2Backwards = lines.get(lines.size()-1).getPoint2();
		int forwardIndex = 1;
		int backwardIndex = lines.size()-2;
		for (int i = 1; i < lines.size()/2; i++){
			if (!(lines.get(forwardIndex).getPoint1().getX() > lastPoint1.getX() + 5)){
				
			}
		}
	}
	
	/**
	 * Gets the data from network tables when the lines report is called "myLinesReport"
	 * TODO: Make a good name for the network lines report
	 */
	private void updateArraysFromNetwork(){
		lineHeights = NetworkTable.getTable("myLinesReport").getNumberArray("height", lineHeights);
		lineAngles = NetworkTable.getTable("myLinesReport").getNumberArray("angle", lineAngles);
		lineX1s = NetworkTable.getTable("myLinesReport").getNumberArray("x1", lineX1s);
		lineY1s = NetworkTable.getTable("myLinesReport").getNumberArray("y1", lineY1s);
		lineX2s = NetworkTable.getTable("myLinesReport").getNumberArray("x2", lineX2s);
		lineY2s = NetworkTable.getTable("myLinesReport").getNumberArray("y2", lineY2s);
	}
	
	/**
	 * Puts data from the arrays to the data structures used to process the data
	 */
	private void updateTablesAndList(){
		for (int i = 0; i < lineX1s.length; i++){
			lines.add(new Line (new Point(lineX1s[i],  lineY1s[i]), new Point(lineX2s[i],  lineY2s[i]), lineAngles[i], lineHeights[i]));
			startingPoints.put(new Point(lineX1s[i],  lineY1s[i]), lines.get(i));
			endingPoints.put(new Point(lineX2s[i],  lineY2s[i]), lines.get(i));
		}
	}
	
}
