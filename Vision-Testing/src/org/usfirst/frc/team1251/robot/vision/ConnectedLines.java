package org.usfirst.frc.team1251.robot.vision;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Eric Engelhart
 * Makes it easier to have lists for connected lines without huge nested things
 */
public class ConnectedLines {
	
	private List<Line> connectedLines;
	public ConnectedLines(){
		connectedLines = new ArrayList<>();
	}
	
	public Line get(int index){
		return connectedLines.get(index);
	}
	
	public void add(Line line){
		connectedLines.add(line);
	}

}
