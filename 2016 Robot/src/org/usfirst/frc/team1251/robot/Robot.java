package org.usfirst.frc.team1251.robot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Joystick.RumbleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



import org.apache.commons.math3.util.MathUtils;
import org.usfirst.frc.team1251.robot.vision.Contour;


import org.usfirst.frc.team1251.robot.vision.Vision;

/**
 * This is the main code for team 1251's 2016 robot.
 * Do not make any edits to this code without explicit permission from 
 * Jared Pilewski, Eric Engelhart, Mr.Elich, or Juan Coto
 */

public class Robot extends IterativeRobot {

	public static RobotDrive driveBase;
	public static Joystick driveController, operatorController;
	public static Victor mCollector, mShooter;
	public static Compressor compressor;
	public static Solenoid collectorArm, shooterHood;
	public static DigitalInput ballDetect;
	public static Encoder shooterSpeed;
	public static AnalogPotentiometer Pot;
	public static PIDController Pid;
	public static String armPosition="down", hoodPosition="down", shooterSpeedDisplayed ="off";
	public static double lRev=0, rRev=0, lAxis, rAxis;
	public static boolean detect;
	public static boolean testAuto = true;
    public static Vision vision;
    public static boolean isShooting = false;
	public static Thread visionThread;
	public static double[] anglesToTarget = {};
	public static double[] distancesToTarget = {};
	public static double[][] targetDataArrays = new double[2][];
    public static double averageJoystickRight;
    public static double averageJoystickLeft;
    public static final double PI = Math.PI;
    public static boolean isNegativeLeft = false;
    public static boolean isNegativeRight = false;
	public static int autoLoopCounter;
    public static AnalogGyro vGyro;
    public static ADXRS450_Gyro hGyro;
    public static boolean isVisionTargeting = false;
	public static final double /** Changeable constant values */
			revSpeed = 0.5,	//Drive rev speed
			k_RPM1 = 1000, 	//Low RPM speed
			k_RPM2 = 2000,	//Mid 1 RPM speed
			k_RPM3 = 3000, 	//Mid 2 RPM speed
			k_RPM4 = 4000,	//High RPM speed

            k_TOLERANCE = 0.05;
    public static final int k_valuesToAverage = 5; // number of values to average from the driver input
    public static double[] joystickListRight = new double[k_valuesToAverage];
    public static double[] joystickListLeft = new double[k_valuesToAverage];
    int location = -1;
    int defense = -1;
	
    public void robotInit() {    	
    	//Drive base using PWM 0, 1, 2, 3
    	driveBase = new RobotDrive(0, 1, 2, 3);
    	
    	//Joystick for the driver and operator
    	driveController = new Joystick(0);
    	operatorController = new Joystick(1);
    	
    	//Compressor
    	compressor = new Compressor();
    	
    	//Solenoids using pneumatics slot 0, 1
    	collectorArm = new Solenoid(0);
    	shooterHood = new Solenoid(1);
    	
    	//Button detector using DIO 0
    	ballDetect = new DigitalInput(0); 
    	
    	//Motors using PWM 4, 5
    	mCollector = new Victor(4);
    	mShooter = new Victor(5);
    	mShooter.setInverted(true);
    	
    	//Encoder using DIO 1, 2, 3
    	shooterSpeed = new Encoder(2, 3, true, EncodingType.k2X);
    	
    	//Potentiometer using analog 3
    	Pot = new AnalogPotentiometer(3, 360, 0);
    	
    	//PID decelerations
    	shooterSpeed.setDistancePerPulse(1.5);
		shooterSpeed.setPIDSourceType(PIDSourceType.kRate);
		Pot.setPIDSourceType(PIDSourceType.kDisplacement);
		Pid = new PIDController(0.05, 0.005, 0.5, shooterSpeed, mShooter);

        Scanner scan = null;
        try {
            // read in usb 1
            scan = new Scanner(new File("/media/sda1/robot.conf"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String data = null;
        if (scan != null) {
            data = scan.nextLine();
            // check for location or defense
            if (data.contains("location=")){
                location = Integer.parseInt(data.replaceAll("location=", ""));
            }else if(data.contains("defense=")){
                defense = Integer.parseInt(data.replaceAll("defense=", ""));
            }
        }
        try {
            // read in usb 2
            scan = new Scanner(new File("/media/sdb1/robot.conf"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (scan != null) {
            data = scan.nextLine();
            // check for location or defense
            if (data.contains("location=")){
                location = Integer.parseInt(data.replaceAll("location=", ""));
            }else if(data.contains("defense=")){
                defense = Integer.parseInt(data.replaceAll("defense=", ""));
            }
        }


        vision = new Vision();
		visionThread = new Thread(vision, "Vision-Tracking");

        vGyro = new AnalogGyro(0);
        hGyro= new ADXRS450_Gyro();
        vGyro.calibrate();
        hGyro.calibrate();
    }

    public void autonomousInit() {

      //  eventManager.add(AutoInitListener.class, autoInitListener);
        //eventManager.fireEvent(AutoInitEvent.class);


    	visionThread.run();
    	autoLoopCounter = 1;

    }
    
    public void autonomousPeriodic() {

        /*eventManager.add(AutoPeriodicListener.class, autoPeriodicListener);
        eventManager.fireEvent(AutoPeriodicEvent.class);


    	autoLoopCounter++;
        if (Autonomous.goneDownDefense){
            Autonomous.loopsSinceCrossed++;
        }
        //if (!//Autonomous.crossed){
            //Autonomous.crossDefenses(defense);
        //}*/
        if (testAuto) {
            //Runs the visionThread code
            if (visionThread.isAlive()) {
                visionThread.notify();
                visionThread.run();
            } else {
                visionThread.run();
            }
           // targetDataArrays = vision.getTargetData();
            
            //unpack data into 2 single dimension arrays
            distancesToTarget = targetDataArrays[0];
            anglesToTarget = targetDataArrays[1];
            //choose two lowest angles, and then choose the lower angled one b/c it will have less total  distance



        }else{
            driveBase.tankDrive(.70, .77);
        }

    }
    
    public void teleopInit() {
    	Pid.disable();
    	compressor.start();
    }

    public void teleopPeriodic() {
    	//Declaring variable to detector button
    	detect = ballDetect.get();
    	
    	//Declaring variables to driver axis
    	lAxis = driveController.getRawAxis(1);
    	rAxis = driveController.getRawAxis(3);

        // move values over 1 & add the values to the average
 
    	
        averageJoystickLeft = 0;
        averageJoystickRight = 0;
        for (int i = 0; i < k_valuesToAverage - 1; i++){
            joystickListLeft[i] = joystickListLeft[i+1];
            joystickListRight[i] = joystickListRight[i+1];
            averageJoystickLeft += joystickListLeft[i];
            averageJoystickRight += joystickListRight[i];
        }
        // add the new joystick input and add it to the average as well
        joystickListLeft[k_valuesToAverage-1] = -lAxis;
        joystickListRight[k_valuesToAverage-1] = -rAxis;
        if (-lAxis < 0){
        	averageJoystickLeft -= Math.pow(-lAxis, 2.0);
        	
        }else if (-lAxis >= 0){
        	averageJoystickLeft += Math.pow(lAxis, 2.0);
        }
        
        if (-rAxis < 0){
        	averageJoystickRight -= Math.pow(rAxis, 2.0);
        	
        }else if (-rAxis >= 0){
        	averageJoystickRight += Math.pow(-rAxis, 2.0);
        }
        if (averageJoystickRight > 0.5 && averageJoystickLeft < -0.5){
        	averageJoystickRight *= 0.7;
        	averageJoystickLeft *= 0.7;
        }
        
        averageJoystickLeft /= k_valuesToAverage;
        averageJoystickRight /= k_valuesToAverage;

        driveBase.tankDrive(averageJoystickLeft, averageJoystickRight);
    	//Collector arm up and down
        if (driveController.getRawButton(2)) { //up
        	collectorArm.set(true);
        	armPosition = "Up";
        }
        if (driveController.getRawButton(1)) { //down
        	collectorArm.set(false);
        	armPosition = "Down";
        }
        
        //Hood up and down
        if (operatorController.getPOV() == 0) {
        	shooterHood.set(true);
        	hoodPosition = "Up";
        }
        else if (operatorController.getPOV() == 180) {
            shooterHood.set(false);
            hoodPosition = "Down";
        }

        //Shooter method
        

        //Shooter multi-RPM
        if (operatorController.getRawButton(5)) { //Low
        	Pid.setSetpoint(((k_RPM1/60)*360)*1.5);
        	Pid.enable();
        	shooterSpeedDisplayed = "Low";
        	if (operatorController.getRawButton(2)) {
            	mCollector.set(1);
            }
        	else {
        		mCollector.set(0);
        	}
        	isShooting = true;
        }
        else if (operatorController.getRawButton(6)) { //Medium 1
        	Pid.setSetpoint(((k_RPM2/60)*360)*1.5);
        	Pid.enable();
        	shooterSpeedDisplayed = "Medium 1";
        	if (operatorController.getRawButton(2)) {
            	mCollector.set(1);
            }
        	else {
        		mCollector.set(0);
        	}
        	isShooting = true;
        }
        else if (operatorController.getRawButton(7)) { //Medium 2
        	Pid.setSetpoint(((k_RPM3/60)*360)*1.5);
        	Pid.enable();
        	shooterSpeedDisplayed = "Medium 2";
        	if (operatorController.getRawButton(2)) {
            	mCollector.set(1);
            }
        	else {
        		mCollector.set(0);
        	}
        	isShooting = true;
        }
        else if (operatorController.getRawButton(8)) { //High
        	Pid.setSetpoint(((k_RPM4/60)*360)*1.5);
        	Pid.enable();
        	shooterSpeedDisplayed = "High";
        	if (operatorController.getRawButton(2)) {
            	mCollector.set(1);
            }
        	else {
        		mCollector.set(0);
        	}
        	isShooting = true;
        }
        else { //PID off
        	Pid.setSetpoint(0);
        	Pid.disable();
        	shooterSpeedDisplayed = "off";
        	isShooting = false;
        }
        if (detect && isShooting){
        	detect = false;
        }
        //Collector detection 
        if (operatorController.getRawButton(3) && !detect) { //for intake
        	mCollector.set(1.0);
        }
        else if (operatorController.getRawButton(1)) { //for outtake 
            mCollector.set(-1.0);
        }
        else { //Collector off
        	mCollector.set(0);
        }
        
        
        //SmartDashboard information
        SmartDashboard.putNumber("Motor Revolutions/M ", ((shooterSpeed.getRate()/1.5)/360)*60);
        SmartDashboard.putNumber("Joystick D-pad ", driveController.getPOV());
        SmartDashboard.putString("Arm position ", armPosition);
        SmartDashboard.putString("Hood Position ", hoodPosition);
        SmartDashboard.putString("Shooter setting ", shooterSpeedDisplayed);
        SmartDashboard.putNumber("Location", location);
    }   
    
    public void disabledInit() {
    	compressor.stop();
    }
    
    public void disabledPeriodic(){
    	driveController.setRumble(RumbleType.kLeftRumble, 100);
    	driveController.setRumble(RumbleType.kRightRumble, 100);
    }

    /**
     * the angle is normalized from -π to +π
     * @param angle the angle to be normalized in radians
     * @return normalized angle
     */
   public static double normalizeAngle(double angle){
        // normalize angle to [-π, π]
        return MathUtils.normalizeAngle(angle, 0.0);
    }
}