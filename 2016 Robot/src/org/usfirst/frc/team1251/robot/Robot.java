package org.usfirst.frc.team1251.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is the main code for team 1251's 2016 robot.
 * Do not make any edits to this code without explicit permission from 
 * Jared Pilewski, Eric Engelhart, Mr.Elich, or Juan Coto
 */

public class Robot extends IterativeRobot {
	RobotDrive driveBase;
	Joystick driveController, operatorController;
	Victor mCollector, mShooter;
	Compressor compressor;
	Solenoid collectorArm;
	DigitalInput collectorDetect;
	Encoder Encoder;
	AnalogPotentiometer Pot;
	PIDController Pid;
	double lRev, rRev, revSpeed, lAxis, rAxis, k_RPM1, k_RPM2;
	boolean detect;
	
    public void robotInit() {
    	/** Changeable variable values*/
    	revSpeed = 0.5;
    	k_RPM1 = 2500;
    	k_RPM2 = 5000;
    	
    	
    	//Drive base using PWM 0, 1, 2, 3
    	driveBase = new RobotDrive(0, 1, 2, 3);
    	
    	//Joystick for the driver and operator
    	driveController = new Joystick(0);
    	operatorController = new Joystick(1);
    	
    	//Solenoids using pneumatics slot 1, 2
    	collectorArm = new Solenoid(0);
    	
    	//Button detector using DIO 0
    	collectorDetect = new DigitalInput(0);
    	
    	//Motors using PWM 4, 5
    	mCollector = new Victor(4);
    	mShooter = new Victor(5);
    	
    	//Declaring variables to driver axis
    	lAxis = driveController.getRawAxis(0);
    	rAxis = driveController.getRawAxis(1);
    	
    	//Encoder using DIO 0, 1, 2
    	Encoder = new Encoder(1, 2, true, EncodingType.k2X);
    	//Potentiometer using analog 3
    	Pot = new AnalogPotentiometer(3, 180, -0.27);
    	
    	//PID decelerations
    	Encoder.setDistancePerPulse(1.5);
    	Encoder.setPIDSourceType(PIDSourceType.kRate);
    	Pot.setPIDSourceType(PIDSourceType.kDisplacement);
    	Pid = new PIDController(0, 0, 0, Encoder, mShooter);
    	
    	//Boolean variables
    	detect = false;
    	
    	//Double variables
    	lRev = 0;
    	rRev = 0;
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
    	//Drive rev up
    	if (lAxis != 0) {
    		lRev += revSpeed;
    	}
    	if (rAxis != 0) {
    		rRev += revSpeed;
    	}
    	
    	//Drive base movement
    	driveBase.tankDrive(lAxis * Math.abs(lRev), rAxis * Math.abs(rRev));
    	
    	//Collector arm up and down
        if (driveController.getRawButton(1)) {
        	collectorArm.set(true);
        }
        if (driveController.getRawButton(2)) {
        	collectorArm.set(false);
        }
        
        //Shooter multi-RPM
        if (operatorController.getRawButton(6)) {
        	Pid.setSetpoint(((k_RPM1/60)*360)*1.5);
        	Pid.enable();
        }
        else if (operatorController.getRawButton(7)) {
        	Pid.setSetpoint(((k_RPM1/60)*360)*1.5);
        	Pid.enable();
        }
        else {
        	Pid.setSetpoint(0);
        	Pid.disable();
        }
        
        //Collector detection 
        if (driveController.getRawButton(4) && !detect) {
        	if (!collectorDetect.get()) {
        	mCollector.set(0.5);
        	}
        	else {
        		mCollector.set(0);
        		detect = true;
        	}
        }
        else if ((driveController.getRawButton(3) && detect)) {
        	if (!collectorDetect.get()) {
            	mCollector.set(0.5);
            	}
            	else {
            		mCollector.set(0);
            		detect = false;
            	}
        }
        
        //SmartDashboard information
        SmartDashboard.putNumber("Motor Revolutions/M ", ((Encoder.getRate()/1.5)/360)*60);
    }   
}