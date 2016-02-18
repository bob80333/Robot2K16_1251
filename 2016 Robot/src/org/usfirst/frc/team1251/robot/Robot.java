
package org.usfirst.frc.team1251.robot;

import edu.wpi.first.wpilibj.*;

/**
 * Main code...
 */

public class Robot extends IterativeRobot {
	Joystick mainController, altController;
	Victor mIntake, mShooter;
	Compressor compressor;
	Solenoid collectorArm;
	DigitalInput collectorDetect;
	Boolean detect;
	
    public void robotInit() {
    	mainController = new Joystick(0);
    	altController = new Joystick(1);
    	collectorArm = new Solenoid(0);
    	collectorDetect = new DigitalInput(3);
    	mIntake = new Victor(2);
    	mShooter = new Victor(3);
    	detect = false;
    }

    public void autonomousInit() {

    }
    
    public void autonomousPeriodic() {

    }
    
    public void teleopInit() {
    	
    }

    public void teleopPeriodic() {
        if (mainController.getRawButton(1)) {
        	collectorArm.set(true);
        }
        if (mainController.getRawButton(2)) {
        	collectorArm.set(false);
        }
        
        if (mainController.getRawButton(4) && detect == false) {
        	if (collectorDetect.get() == false) {
        	mIntake.set(0.5);
        	}
        	else {
        		mIntake.set(0);
        		detect = true;
        	}
        }
        else if ((mainController.getRawButton(3) && detect == true)) {
        	if (collectorDetect.get() == true) {
            	mIntake.set(0.5);
            	}
            	else {
            		mIntake.set(0);
            		detect = false;
            	}
        	
        }
        
        
    }

    public void testPeriodic() {
    
    }
    
}