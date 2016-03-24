package org.usfirst.frc.team1251.robot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;


import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.vision.USBCamera;

import org.apache.commons.math3.util.MathUtils;
import org.usfirst.frc.team1251.robot.vision.Contour;


import org.usfirst.frc.team1251.robot.vision.TurnOffPi;
import org.usfirst.frc.team1251.robot.vision.Vision;

/**
 * This is the main code for team 1251's 2016 robot.
 * Do not make any edits to this code without explicit permission from 
 * Jared Pilewski, Eric Engelhart, Mr.Elich, or Juan Coto
 */

public class Robot extends IterativeRobot {

    public static Command turnOffPi;
	public static RobotDrive driveBase;
	public static Joystick driveController, operatorController;
	public static Victor mCollector, mShooter;
	public static Compressor compressor;
	public static DoubleSolenoid collectorArm;
	public static Solenoid shooterHood;
	public static DigitalInput ballDetect;
	public static CameraServer tempCam;
	public static Encoder shooterSpeed;
	public static AnalogPotentiometer Pot;
	public static PIDController Pid;
	public static String armPosition="down", hoodPosition="down", shooterSpeedDisplayed ="off";
	public static boolean testAuto = false;
    public static Vision vision;
	public static Thread visionThread;
	public static double[] anglesToTarget = {};
	public static double[] distancesToTarget = {};
	public static double[][] targetDataArrays = new double[2][];
    public static final double PI = Math.PI;
	public static int autoLoopCounter;
    public static AnalogGyro vGyro;
    public static ADXRS450_Gyro hGyro;
    public static boolean isVisionTargeting = false;
    public static boolean isCameraStarted = false;
	public static final double /** Changeable constant values */
			revSpeed = 0.5,	//Drive rev speed
			k_RPM1 = 15750, 	//Low RPM speed
			k_RPM2 = 18750,	//Mid 1 RPM speed
			k_RPM3 = 24100, 	//Mid 2 RPM speed
			k_RPM4 = 25000,	//High RPM speed

            k_TOLERANCE = 0.05;
    public static final int k_valuesToAverage = 5; // number of values to average from the driver input
    public static int location = -1;
    public static int defense = -1;
    public static List<Contour> contours = new ArrayList<>();

    public void robotInit() {    	
    	//Drive base using PWM 0, 1, 2, 3
    	driveBase = new RobotDrive(0, 1, 2, 3);
    	//Joystick for the driver and operator
    	driveController = new Joystick(0);
    	operatorController = new Joystick(1);
    	
    	//Compressor
    	compressor = new Compressor();
    	
    	//Solenoids using pneumatics slot 0, 1, 2, 3
    	collectorArm = new DoubleSolenoid(0, 1);
    	shooterHood = new Solenoid(2, 3);
    	
    	//Button detector using DIO 0
    	ballDetect = new DigitalInput(0); 
    	
    	//Motors using PWM 4, 5
    	mCollector = new Victor(4);
    	mCollector.setInverted(true);
    	mShooter = new Victor(5);
    	mShooter.setInverted(true);
    	
    	//Encoder using DIO 1, 2, 3
    	shooterSpeed = new Encoder(2, 1, true, EncodingType.k2X);
    	
    	//Potentiometer using analog 3
    	Pot = new AnalogPotentiometer(3, 360, 0);
    	
    	//PID decelerations
    	shooterSpeed.setDistancePerPulse(1.5);
		shooterSpeed.setPIDSourceType(PIDSourceType.kRate);
		Pot.setPIDSourceType(PIDSourceType.kDisplacement);
		Pid = new PIDController(0.0005, 0.004, 0.0001, shooterSpeed, mShooter, 0.001);
	
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

        turnOffPi = new TurnOffPi();
        vision = new Vision();
		visionThread = new Thread(vision, "Vision-Tracking");

        vGyro = new AnalogGyro(0);
        hGyro= new ADXRS450_Gyro();
        vGyro.calibrate();
        hGyro.calibrate();
    }

    public void autonomousInit() {

      Auto.onAutoInit();

    }
    
    public void autonomousPeriodic() {
        Auto.onAutoPeriodic();
    }
    
    public void teleopInit() {
    	Teleop.teleopInit();
    }

    public void teleopPeriodic() {
    	Teleop.teleopPeriodic();
    }   
    
    public void disabledInit() {
    	compressor.stop();
    }
    
    public void disabledPeriodic(){
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