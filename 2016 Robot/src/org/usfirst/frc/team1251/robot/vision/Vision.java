package org.usfirst.frc.team1251.robot.vision;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Vision implements Runnable{
	
	private List<Line> lines= new ArrayList<>();
	private List<Line> targetSides = new ArrayList<>();
	private static List<Target> targets = new ArrayList<>();
	private List<Contour> contours = new ArrayList<>();
	private List<SidedContour> sidedContours = new ArrayList<>();
	private Hashtable<Line, Integer> targetSideIndexes = new Hashtable();
	private Hashtable<Line, Integer> contourIndex = new Hashtable();
	private Hashtable<Integer, Boolean> targetHasLeftSide = new Hashtable();
	private Hashtable<Integer, Boolean> targetHasRightSide = new Hashtable();
	private Hashtable<Line, Boolean> lineIsRightSide = new Hashtable();
	private Hashtable<Line, Boolean> lineIsLeftSide = new Hashtable();
	private Hashtable<Contour, Boolean> sidedContourExists = new Hashtable();
	private Hashtable<Contour, Integer> sidedContourIndex = new Hashtable();
	// + or - this value is the margin of error between two points to be considered 'connected'
	private final int errorMargin = 5;
	private final int angleMargin = 1;
	private final int heightMargin = 15;
	
	private static boolean lockTargetsPressed;
	private static boolean fireButtonPressed;
	
	public void lockTargets(){
		updateDataFromNetwork();
		findLineConnections();
		findTargets();
	}
	
	/**
	 * Gets the data from network tables when the lines report is called "myLinesReport"
	 * TODO: Make a good name for the network lines report
	 */
	private void updateDataFromNetwork(){
		if(NetworkTable.getTable("GRIP") != null){
			for (int i = 0; i < NetworkTable.getTable("GRIP").getNumberArray("myLinesReport/angle", new double[0]).length; i++){
				lines.add(new Line (new Point(getNetwork("myLinesReport/x1", i),  getNetwork("myLinesReport/y1", i)),
						new Point(getNetwork("myLinesReport/x2", i),  getNetwork("myLinesReport/y2", i)), 
						getNetwork("myLinesReport/angle", i), getNetwork("myLinesReport/length", i)));
			}
			for (int i = 0; i < NetworkTable.getTable("GRIP").getNumberArray("myContoursReport/area", new double[0]).length; i++){
				contours.add(new Contour(getNetwork("myContoursReport/area", i), getNetwork("myContoursReport/centerX", i),
						getNetwork("myContoursReport/centerY", i), getNetwork("myContoursReport/width", i),
						getNetwork("myContoursReport/height", i), getNetwork("myContoursReport/solidity", i)));
			}
		}
	}
	
	private void findLineConnections(){
		Line lastLine = lines.get(0);
		Line currentLine;
		for(int i = 1; i < lines.size(); i++){
			currentLine = lines.get(i);
			if (!(currentLine.getPoint1().getX() > lastLine.getPoint2().getX() - errorMargin)  
					&& !(currentLine.getPoint1().getX() < lastLine.getPoint2().getX() + errorMargin)
					&& !(currentLine.getPoint1().getY() > lastLine.getPoint2().getY() - errorMargin)
					&& !(currentLine.getPoint1().getY() < lastLine.getPoint2().getY() + errorMargin)
					&& !(currentLine.getAngle() < lastLine.getAngle() + angleMargin)
					&& !(currentLine.getAngle() > lastLine.getAngle() - angleMargin)){
				lines.remove(lastLine);
				lines.remove(currentLine);
				lines.add(new Line(lastLine.getPoint1(), currentLine.getPoint2(),
						(lastLine.getAngle() + currentLine.getAngle())/2, lastLine.getLength() + currentLine.getLength()));
				lastLine = lines.get(lines.size() - 1);
			}else{
				lastLine = currentLine;
			}
			
		}
	}
	
	private void findTargets(){
		for (int i = 0; i < contours.size(); i++){
			Contour cont = contours.get(i);
			for (Line line : lines){
				if (!(line.getLength() < cont.getHeight() + heightMargin)
						&& !(line.getLength() > cont.getHeight() - heightMargin)
						&& !(line.getAngle() < 0)){
					for(Line line2 : targetSides){
						if(targetSideIndexes.containsKey(line2)){
							if(targetHasLeftSide.get(targetSideIndexes.get(line2))
								&& !targetHasRightSide.get(targetSideIndexes.get(line2))
								&& line.getPoint1().getX() > line2.getPoint1().getX() + errorMargin
								&& line.getPoint2().getX() > line2.getPoint2().getX() + errorMargin){
								targetSides.add(line);
								targetSideIndexes.put(line, targetSides.size() - 1);
								targetHasRightSide.put(targetSides.size() - 1, true);
								lineIsRightSide.put(line, true);
								lineIsLeftSide.put(line, false);
								targetHasLeftSide.put(targetSides.size() - 1, true);
								targetHasRightSide.replace(targetSideIndexes.get(line2), true);
								contourIndex.put(line, i);
							}else if(targetHasRightSide.get(targetSideIndexes.get(line2))
									&& !targetHasLeftSide.get(targetSideIndexes.get(line2))
									&& line.getPoint1().getX() < line2.getPoint1().getX() - errorMargin
									&& line.getPoint2().getX() < line2.getPoint2().getX() - errorMargin){
								targetSides.add(line);
								targetSideIndexes.put(line, targetSides.size() - 1);
								targetHasRightSide.put(targetSides.size() - 1, true);
								targetHasLeftSide.put(targetSides.size() - 1, true);
								targetHasLeftSide.replace(targetSideIndexes.get(line2), true);
								lineIsRightSide.put(line, false);
								lineIsLeftSide.put(line, true);
								contourIndex.put(line, i);
							}
						}
					}
					if (!(line.getPoint1().getX() < (cont.getCenterX() - cont.getWidth()/2) - errorMargin)
							&& !(line.getPoint1().getX() > cont.getCenterX() - cont.getWidth()/2 + errorMargin)){
						targetSides.add(line);
						targetHasLeftSide.put(targetSides.size() - 1, true);
						targetHasRightSide.put(targetSides.size() - 1, false);
						lineIsLeftSide.put(line, true);
						lineIsRightSide.put(line, false);
						contourIndex.put(line, i);
					}else if (!(line.getPoint2().getX() < (cont.getCenterX() + cont.getWidth()/2) - errorMargin)
							&& !(line.getPoint2().getX() > cont.getCenterX() + cont.getWidth()/2 + errorMargin)){
						targetSides.add(line);
						targetHasLeftSide.put(targetSides.size() - 1, false);
						targetHasRightSide.put(targetSides.size() - 1, true);
						lineIsLeftSide.put(line, false);
						lineIsRightSide.put(line, true);
						contourIndex.put(line, i);
					}
				}
			}
		}
		for (Line line : targetSides){
			if (contourIndex.containsKey(line)){
				if (sidedContourExists.get(contours.get(contourIndex.get(line)))){
					if(sidedContours.get(sidedContourIndex.get(contours.get(contourIndex.get(line)))).getLeft() != null){
						sidedContours.get(sidedContourIndex.get(contours.get(contourIndex.get(line)))).setRight(line);
					}else if (sidedContours.get(sidedContourIndex.get(contours.get(contourIndex.get(line)))).getRight() != null){
						sidedContours.get(sidedContourIndex.get(contours.get(contourIndex.get(line)))).setLeft(line);
					}
				}else {
					if (lineIsRightSide.get(line)){
						sidedContours.add(new SidedContour(line, contours.get(contourIndex.get(line)), true));
						sidedContourExists.put(contours.get(contourIndex.get(line)), true);
						sidedContourIndex.put(contours.get(contourIndex.get(line)), sidedContours.size() - 1);
					}else if (lineIsLeftSide.get(line)){
						sidedContours.add(new SidedContour(line, contours.get(contourIndex.get(line)), false));
						sidedContourExists.put(contours.get(contourIndex.get(line)), true);
						sidedContourIndex.put(contours.get(contourIndex.get(line)), sidedContours.size() - 1);
					}
				}
			}
		}
		for (SidedContour sidedContour : sidedContours){
			// we don't have the math for getting distance quite yet
			targets.add(new Target(sidedContour.getCont().getWidth(), sidedContour.getCont().getHeight(),
					sidedContour.getCont().getArea(), sidedContour.getCont().getCenterX(),
					sidedContour.getCont().getCenterY(), 0.0,
					sidedContour.getLeft(), sidedContour.getRight()));
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
		}
	}
}
