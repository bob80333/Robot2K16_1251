
package org.usfirst.frc.team1251.robot;

import java.util.List;

import org.usfirst.frc.team1251.robot.vision.Vision;

import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	private static boolean lockTargetsPressed = false;
	private static boolean fireButtonPressed = false;
	Thread thread = new Thread(new Vision());
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {


    }
    
	/**
	 * This function is run once when the robot switches to autonomous
	 * */
    public void autonomousInit() {
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	if(thread.isAlive()){
    		thread.run();
    	}
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
}
