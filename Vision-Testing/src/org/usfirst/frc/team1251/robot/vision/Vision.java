package org.usfirst.frc.team1251.robot.vision;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Vision {
	
	private List<Line> lines= new ArrayList<>();
	private List<Target> targets = new ArrayList<>();
	private List<List> connections = new ArrayList<List>();
	private Hashtable<Line, Integer> groupOfLine = new Hashtable<Line, Integer>();
	// + or - this value is the margin of error between two points to be considered 'connected'
	private final int errorMargin = 5;
	double[] lineHeights = {};
	double[] lineAngles = {};
	double[] lineX1s = {};
	double[] lineY1s = {};
	double[] lineX2s = {};
	double[] lineY2s = {};
	
	public void lockTargets(){
		updateArraysFromNetwork();
		updateTablesAndList();
		findLineConnections();
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
		// make temporary variables so that we don't keep accessing the lines array
		// efficiency is key, we don't want to affect the rest of the robot program's speed
		Line forwardLine = lines.get(forwardIndex);
		Line lastForwardLine = lines.get(lastForwardIndex);
		
		Line backwardLine = lines.get(backwardIndex);
		Line lastBackwardLine = lines.get(lastBackwardIndex);
		for (int i = 0; i < lines.size(); i++){ 
			
			if (!(forwardLine.getPoint1().getX() > lines.get(lastForwardIndex).getPoint1().getX() + errorMargin)  
					&& !(forwardLine.getPoint1().getX() < lines.get(lastForwardIndex).getPoint1().getX() - errorMargin)
					&& !(forwardLine.getPoint1().getY() > lines.get(lastForwardIndex).getPoint1().getY() + errorMargin)
					&& !(forwardLine.getPoint1().getY() < lines.get(lastForwardIndex).getPoint1().getY() - errorMargin)){
				
				forwardLine.setPoint1ConnectedIndex(lastForwardIndex);
				lastForwardLine.setPoint2ConnectedIndex(forwardIndex);
				if (groupOfLine.containsKey(lastForwardLine)){
					connections.get(groupOfLine.get(forwardLine)).add(forwardLine);
					groupOfLine.put(forwardLine, groupOfLine.get(lastForwardLine));
				}else {
					groupOfLine.put(forwardLine, connections.size());
					connections.add(new ArrayList<Line>());
					connections.get(connections.size()-1).add(forwardLine);
				}
				
			}else if (!(forwardLine.getPoint2().getX() > lastForwardLine.getPoint2().getX() + errorMargin)  
					&& !(forwardLine.getPoint2().getX() < lastForwardLine.getPoint2().getX() - errorMargin)
					&& !(forwardLine.getPoint2().getY() > lastForwardLine.getPoint2().getY() + errorMargin)
					&& !(forwardLine.getPoint2().getY() < lastForwardLine.getPoint2().getY() - errorMargin)){
				
				forwardLine.setPoint2ConnectedIndex(lastForwardIndex);
				lastForwardLine.setPoint1ConnectedIndex(forwardIndex);
				if (groupOfLine.containsKey(lastForwardLine)){
					connections.get(groupOfLine.get(lastForwardLine)).add(forwardLine);
					groupOfLine.put(forwardLine, groupOfLine.get(lastForwardLine));
				}else {
					groupOfLine.put(forwardLine, connections.size());
					connections.add(new ArrayList<Line>());
					connections.get(connections.size()-1).add(forwardLine);
				}
			}
			forwardIndex++;
			
			if (!(backwardLine.getPoint1().getX() > lastBackwardLine.getPoint1().getX() + errorMargin)  
					&& !(backwardLine.getPoint1().getX() < lastBackwardLine.getPoint1().getX() - errorMargin)
					&& !(backwardLine.getPoint1().getY() > lastBackwardLine.getPoint1().getY() + errorMargin)
					&& !(backwardLine.getPoint1().getY() < lastBackwardLine.getPoint1().getY() - errorMargin)){
				
				backwardLine.setPoint1ConnectedIndex(lastBackwardIndex);
				lastBackwardLine.setPoint2ConnectedIndex(backwardIndex);
				
				if (groupOfLine.containsKey(lastBackwardLine)){
					connections.get(groupOfLine.get(lastBackwardLine)).add(backwardLine);
					groupOfLine.put(backwardLine, groupOfLine.get(lastBackwardLine));
				}else {
					groupOfLine.put(forwardLine, connections.size());
					connections.add(new ArrayList<Line>());
					connections.get(connections.size()-1).add(forwardLine);
				}
			}else if (!(backwardLine.getPoint2().getX() > lastBackwardLine.getPoint2().getX() + errorMargin)  
					&& !(backwardLine.getPoint2().getX() < lastBackwardLine.getPoint2().getX() - errorMargin)
					&& !(backwardLine.getPoint2().getY() > lastBackwardLine.getPoint2().getY() + errorMargin)
					&& !(backwardLine.getPoint2().getY() < lastBackwardLine.getPoint2().getY() - errorMargin)){
				
				backwardLine.setPoint2ConnectedIndex(lastBackwardIndex);
				lastBackwardLine.setPoint1ConnectedIndex(backwardIndex);
				
				if (groupOfLine.containsKey(lastForwardLine)){
					connections.get(groupOfLine.get(lastBackwardLine)).add(backwardLine);
					groupOfLine.put(backwardLine, groupOfLine.get(lastBackwardLine));
				}else {
					groupOfLine.put(backwardLine, connections.size());
					connections.add(new ArrayList<Line>());
					connections.get(connections.size()-1).add(backwardLine);
				}
			}
			backwardIndex--;
			if (forwardIndex > backwardIndex){
				break;
			}
			forwardLine = lines.get(forwardIndex);
			lastForwardLine = forwardLine;
			
			backwardLine = lines.get(backwardIndex);
			lastBackwardLine = backwardLine;
			}
		}
	
	private void findTargets(){
		// calculated based on the other 2 factors
		// numberOfConnections is used this way:
		// more connections up to the max number of lines in a target, at which point it decreases to 0 over time.
		// how well do the connections fit the right ratios for length & angle?
		int[] targetLikelihood = {};
		int[] numberOfConnections = {};
		// how well do the connections fit the right ratios for length & angle?
		int[] connectionQuality = {};
		for (int i = 0;i < lines.size(); i++){
			for (List<Line> list : connections){
				for (Line line : list){
					if (numberOfConnections[groupOfLine.get(line)] > 0){
						numberOfConnections[groupOfLine.get(line)]++;
					}else{
						numberOfConnections[groupOfLine.get(line)] = 1;
					}
					
				}
				connectionQuality[connections.indexOf(list)] = 100;
			}
		}
		
		for (int i = 0; i < connections.size(); i++){
			// scary equation
			// 25x - (25x^2)/(16) = a percentage from number of lines, but will give negative so we use math.max to return 0 or the number
			// then we average them and round into an integer
			targetLikelihood[i] = Math.round(Math.round(Math.max(0, (25*numberOfConnections[i] - 25*Math.pow(numberOfConnections[i], 2)/16)) + (connectionQuality[i])/2));
		}
	}
}
