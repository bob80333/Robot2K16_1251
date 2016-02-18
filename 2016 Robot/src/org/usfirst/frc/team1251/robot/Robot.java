
package org.usfirst.frc.team1251.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Main code...
 */

public class Robot extends IterativeRobot {
	RobotDrive driveBase;
	Joystick mainController, altController;
	Victor mIntake, mShooter;
	Compressor compressor;
	Solenoid collectorArm;
	DigitalInput collectorDetect;
	Encoder Encoder;
	AnalogPotentiometer Pot;
	PIDController Pid;
	
	double lRev, rRev, revSpeed, lAxis, rAxis, k_RPM1, k_RPM2;
	boolean detect;
	
    public void robotInit() {
    	driveBase = new RobotDrive(0, 1, 2, 3);
    	mainController = new Joystick(0);
    	altController = new Joystick(1);
    	collectorArm = new Solenoid(0);
    	collectorDetect = new DigitalInput(3);
    	mIntake = new Victor(4);
    	mShooter = new Victor(5);
    	detect = false;
    	lRev = 0;
    	rRev = 0;
    	lAxis = mainController.getRawAxis(0);
    	rAxis = mainController.getRawAxis(1);
    	revSpeed = 0.5;
    	Encoder = new Encoder(1, 2, true, EncodingType.k2X);
    	Pot = new AnalogPotentiometer(2, 180, -0.27);
    	Encoder.setDistancePerPulse(1.5);
    	Encoder.setPIDSourceType(PIDSourceType.kRate);
    	Pot.setPIDSourceType(PIDSourceType.kDisplacement);
    	Pid = new PIDController(0, 0, 0, Encoder, mShooter);
    	k_RPM1 = 2500;
    	k_RPM2 = 5000;
    }

    public void autonomousInit() {

    }
    
    public void autonomousPeriodic() {

    }
    
    public void teleopInit() {
    	Pid.setPID(0.00005, 0.0000005, 0.005);
    	Pid.disable();
    }

    public void teleopPeriodic() {
    	
    	if (lAxis != 0) {
    		lRev += revSpeed;
    	}
    	if (rAxis != 0) {
    		rRev += revSpeed;
    	}
    	driveBase.tankDrive(lAxis * Math.abs(lRev), rAxis * Math.abs(rRev));
    	
    	
        if (mainController.getRawButton(1)) {
        	collectorArm.set(true);
        }
        if (mainController.getRawButton(2)) {
        	collectorArm.set(false);
        }
        
        if (altController.getRawButton(6)) {
        	Pid.setSetpoint(((k_RPM1/60)*360)*1.5);
        	Pid.enable();
        }
        else if (altController.getRawButton(7)) {
        	Pid.setSetpoint(((k_RPM1/60)*360)*1.5);
        	Pid.enable();
        }
        else {
        	Pid.setSetpoint(0);
        	Pid.disable();
        }
        if (altController.getRawButton(7)) {
        	Pid.enable();
        }
        else {
        	Pid.disable();
        }
        
        if (mainController.getRawButton(4) && !detect) {
        	if (!collectorDetect.get()) {
        	mIntake.set(0.5);
        	}
        	else {
        		mIntake.set(0);
        		detect = true;
        	}
        }
        else if ((mainController.getRawButton(3) && detect)) {
        	if (!collectorDetect.get()) {
            	mIntake.set(0.5);
            	}
            	else {
            		mIntake.set(0);
            		detect = false;
            	}
        }
        
        SmartDashboard.putNumber("Motor Recolutions/M ", ((Encoder.getRate()/1.5)/360)*60);
        
    }

    public void testPeriodic() {
    
    }
    
}