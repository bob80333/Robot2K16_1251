
package org.usfirst.frc.team1252.robot;

import edu.wpi.first.wpilibj.*;

/**
 * Main code...
 */

public class Robot extends IterativeRobot {

	Compressor compressor;
	Solenoid collector;
	
    public void robotInit() {
    	collector = new Solenoid(0);
    }

    public void autonomousInit() {

    }
    
    public void autonomousPeriodic() {

    }
    
    public void teleopInit() {
    	
    }

    public void teleopPeriodic() {
        
    }

    public void testPeriodic() {
    
    }
    
}