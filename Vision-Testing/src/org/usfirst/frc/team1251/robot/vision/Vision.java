package org.usfirst.frc.team1251.robot.vision;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Vision {
	
	private List<Line> lines= new ArrayList<>();
	private List<Target> targets = new ArrayList<>();
	// + or - this value is the margin of error between two points to be considered 'connected'
	private final int errorMargin = 5;
	double[] lineHeights = {};
	double[] lineAngles = {};
	double[] lineX1s = {};
	double[] lineY1s = {};
	double[] lineX2s = {};
	double[] lineY2s = {};
	
	public void findTargets(){
		updateArraysFromNetwork();
		updateTablesAndList();
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
		}
	}
	
	private void findLineConnections(){
		int forwardIndex = 1;
		int lastForwardIndex = 0;
		int backwardIndex = lines.size() - 2;
		int lastBackwardIndex = lines.size() - 1;
		for (int i = 1; i < lines.size(); i++){
			
			if (!(lines.get(forwardIndex).getPoint1().getX() > lines.get(lastForwardIndex).getPoint1().getX() + errorMargin)  
					&& !(lines.get(forwardIndex).getPoint1().getX() < lines.get(lastForwardIndex).getPoint1().getX() - errorMargin)
					&& !(lines.get(forwardIndex).getPoint1().getY() > lines.get(lastForwardIndex).getPoint1().getY() + errorMargin)
					&& !(lines.get(forwardIndex).getPoint1().getY() < lines.get(lastForwardIndex).getPoint1().getY() - errorMargin)){
				
				lines.get(forwardIndex).setPoint1ConnectedIndex(lastForwardIndex);
				lines.get(lastForwardIndex).setPoint2ConnectedIndex(forwardIndex);
				
			}else if (!(lines.get(forwardIndex).getPoint2().getX() > lines.get(lastForwardIndex).getPoint2().getX() + errorMargin)  
					&& !(lines.get(forwardIndex).getPoint2().getX() < lines.get(lastForwardIndex).getPoint2().getX() - errorMargin)
					&& !(lines.get(forwardIndex).getPoint2().getY() > lines.get(lastForwardIndex).getPoint2().getY() + errorMargin)
					&& !(lines.get(forwardIndex).getPoint2().getY() < lines.get(lastForwardIndex).getPoint2().getY() - errorMargin)){
				
				lines.get(forwardIndex).setPoint2ConnectedIndex(lastForwardIndex);
				lines.get(lastForwardIndex).setPoint1ConnectedIndex(forwardIndex);
			}
			forwardIndex++;
			
			if (!(lines.get(backwardIndex).getPoint1().getX() > lines.get(lastBackwardIndex).getPoint1().getX() + errorMargin)  
					&& !(lines.get(backwardIndex).getPoint1().getX() < lines.get(lastBackwardIndex).getPoint1().getX() - errorMargin)
					&& !(lines.get(backwardIndex).getPoint1().getY() > lines.get(lastBackwardIndex).getPoint1().getY() + errorMargin)
					&& !(lines.get(backwardIndex).getPoint1().getY() < lines.get(lastBackwardIndex).getPoint1().getY() - errorMargin)){
				
				lines.get(backwardIndex).setPoint1ConnectedIndex(lastBackwardIndex);
				lines.get(lastBackwardIndex).setPoint2ConnectedIndex(backwardIndex);
				
			}else if (!(lines.get(backwardIndex).getPoint2().getX() > lines.get(lastBackwardIndex).getPoint2().getX() + errorMargin)  
					&& !(lines.get(backwardIndex).getPoint2().getX() < lines.get(lastBackwardIndex).getPoint2().getX() - errorMargin)
					&& !(lines.get(backwardIndex).getPoint2().getY() > lines.get(lastBackwardIndex).getPoint2().getY() + errorMargin)
					&& !(lines.get(backwardIndex).getPoint2().getY() < lines.get(lastBackwardIndex).getPoint2().getY() - errorMargin)){
				
				lines.get(backwardIndex).setPoint2ConnectedIndex(lastBackwardIndex);
				lines.get(lastBackwardIndex).setPoint1ConnectedIndex(backwardIndex);
			}
			backwardIndex--;
			if (forwardIndex > backwardIndex){
				break;
			}
		}
	}
	
}
