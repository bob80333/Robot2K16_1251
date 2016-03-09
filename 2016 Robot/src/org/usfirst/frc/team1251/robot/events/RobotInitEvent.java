package org.usfirst.frc.team1251.robot.events;

public class RobotInitEvent extends Event {

	@Override
	public String getAction() {
		return "Run when robotInit is run in main robot code, but only once, when turning on the robot";
	}

}
