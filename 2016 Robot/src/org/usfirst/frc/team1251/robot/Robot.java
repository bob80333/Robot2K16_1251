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
	Solenoid collectorArm, shooterHood;
	DigitalInput collectorDetect;
	Encoder Encoder;
	AnalogPotentiometer Pot;
	PIDController Pid;
	double lRev=0, rRev=0, lAxis, rAxis;
	boolean detect;
	
	final double /** Changeable constant values */
			revSpeed = 0.5,	//Drive rev speed
			k_RPM1 = 1000, 	//Low RPM speed
			k_RPM2 = 2000,	//Mid 1 RPM speed
			k_RPM3 = 3000, 	//Mid 2 RPM speed
			k_RPM4 = 4000;	//High RPM speed
	
    public void robotInit() {    	
    	//Drive base using PWM 0, 1, 2, 3
    	driveBase = new RobotDrive(0, 1, 2, 3);
    	
    	//Joystick for the driver and operator
    	driveController = new Joystick(0);
    	operatorController = new Joystick(1);
    	
    	//Declaring variables to driver axis
    	lAxis = driveController.getRawAxis(0);
    	rAxis = driveController.getRawAxis(1);
    	
    	//Solenoids using pneumatics slot 0, 1
    	collectorArm = new Solenoid(0);
    	shooterHood = new Solenoid(1);
    	
    	//Button detector using DIO 0
    	collectorDetect = new DigitalInput(0); 
    	
    	//Motors using PWM 4, 5
    	mCollector = new Victor(4);
    	mShooter = new Victor(5);
    	
    	//Encoder using DIO 0, 1, 2
    	Encoder = new Encoder(1, 2, true, EncodingType.k2X);
    	
    	//Potentiometer using analog 3
    	Pot = new AnalogPotentiometer(3, 180, -0.27);
    	
    	//PID decelerations
    	Encoder.setDistancePerPulse(1.5);
    	Encoder.setPIDSourceType(PIDSourceType.kRate);
    	Pot.setPIDSourceType(PIDSourceType.kDisplacement);
    	Pid = new PIDController(0, 0, 0, Encoder, mShooter);
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
    	//collector detection
    	detect = collectorDetect.get();
    	
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
        if (driveController.getRawButton(3)) { //up
        	collectorArm.set(true);
        }
        if (driveController.getRawButton(2)) { //down
        	collectorArm.set(false);
        }
        
        //Hood up and down
        if (operatorController.getPOV() == 0) {
        	shooterHood.set(true);
        }
        else if (operatorController.getPOV() == 180) {
        	shooterHood.set(false);
        }
        
        //Shooter multi-RPM
        if (operatorController.getRawButton(5)) { //Low
        	Pid.setSetpoint(((k_RPM1/60)*360)*1.5);
        	Pid.enable();
        	if (operatorController.getRawButton(2) && detect) {
            	mCollector.set(1);
            }
        	else {
        		mCollector.set(0);
        	}
        }
        else if (operatorController.getRawButton(6)) { //Medium 1
        	Pid.setSetpoint(((k_RPM2/60)*360)*1.5);
        	Pid.enable();
        	if (operatorController.getRawButton(2) && detect) {
            	mCollector.set(1);
            }
        	else {
        		mCollector.set(0);
        	}
        }
        else if (operatorController.getRawButton(6)) { //Medium 2
        	Pid.setSetpoint(((k_RPM3/60)*360)*1.5);
        	Pid.enable();
        	if (operatorController.getRawButton(2) && detect) {
            	mCollector.set(1);
            }
        	else {
        		mCollector.set(0);
        	}
        }
        else if (operatorController.getRawButton(6)) { //High
        	Pid.setSetpoint(((k_RPM4/60)*360)*1.5);
        	Pid.enable();
        	if (operatorController.getRawButton(2) && detect) {
            	mCollector.set(1);
            }
        	else {
        		mCollector.set(0);
        	}
        }
        else { //PID off
        	Pid.setSetpoint(0);
        	Pid.disable();
        }
        
        //Collector detection 
        if (operatorController.getRawButton(3) && !detect) { //for intake
        	mCollector.set(0.5);
        }
        else if (operatorController.getRawButton(1)) { //for outtake 
            mCollector.set(-0.5);
        }
        else { //Collector off
        	mCollector.set(0);
        }
        
        
        //SmartDashboard information
        SmartDashboard.putNumber("Motor Revolutions/M ", ((Encoder.getRate()/1.5)/360)*60);
    }   
}