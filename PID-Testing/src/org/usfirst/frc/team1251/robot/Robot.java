package org.usfirst.frc.team1251.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Dedicated PID testing */

public class Robot extends IterativeRobot {
	
	Joystick cMain, cSecond;
	Victor mShooter;
	DigitalInput dArm, intake;
	//Compressor compressor;
	//Solenoid sArm;
	Encoder eShooter;
	AnalogPotentiometer pot;
	PIDController pidShooter;
	Double hRPM, lRPM;
	String armPosition;
	
    public void robotInit() {
    	//RPM variables
    	hRPM = 5000.0;
    	lRPM = 2500.0;
    	
    	//Joysticks
   	cMain = new Joystick(0);
    	cSecond = new Joystick(1);
    	
    	//victors
    	mShooter = new Victor(2);
   	
    	//Buttons
    	dArm = new DigitalInput(3);
    	intake = new DigitalInput(4);
    	
    	//pneumatics
    	//compressor = new Compressor();
    	//sArm = new Solenoid(0, 1);
    	
    	//encoder / potentiometer
    	eShooter = new Encoder(1, 2, true, EncodingType.k2X);
    	pot = new AnalogPotentiometer(2, 180, -0.27);
    	eShooter.setDistancePerPulse(1.5);
    	//pid
    	eShooter.setPIDSourceType(PIDSourceType.kRate);
    	pot.setPIDSourceType(PIDSourceType.kDisplacement);
    	pidShooter = new PIDController(0, 0, 0, eShooter, mShooter);

    }
    
    public void autonomousInit() {

    }

    public void autonomousPeriodic() {
    	
    }
    
    public void teleopInit() {
    	pidShooter.setPID(0.00005, 0.0000005, 0.005);
    }
    
    public void teleopPeriodic() {
    	
    	//button for arm position detection
    	if (cMain.getRawButton(4) && dArm.get() == true)  {
    		eShooter.reset();
    		if (dArm.get() == true) {
    			mShooter.set(0.5);
    		}
    		else {
    			mShooter.set(0.0);
    		}
    	}
    	else if (cMain.getRawButton(1) && dArm.get() == false) {
    		eShooter.reset();
    		if (eShooter.getDistance() < 120) {
    			mShooter.set(0.5);
    		}
    		else {
    			mShooter.set(0.0);
    		}
    	}
    	
    	if (dArm.get() == true) {
    		armPosition = "Up";
    	}
    	else {
    		armPosition = "down";
    	}
    	
    	SmartDashboard.putNumber("Motor Recolutions/M ", ((eShooter.getRate()/1.5)/360)*60);
    	SmartDashboard.putNumber("Motor Counts/S ", eShooter.getRate());
    	SmartDashboard.putString("Arm Position ", armPosition);
    	SmartDashboard.putNumber("POT ", pot.get());
    }
    
    public void disabledInit(){
    	
    }
    
}