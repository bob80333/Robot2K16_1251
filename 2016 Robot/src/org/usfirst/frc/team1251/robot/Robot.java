package org.usfirst.frc.team1251.robot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.apache.commons.math3.util.MathUtils;

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
	public static DoubleSolenoid collectorArm;
	public static Solenoid shooterHood;
	public static DigitalInput ballDetect;
	public static CameraServer tempCam;
	public static Encoder shooterSpeed;
	public static AnalogPotentiometer Pot;
	public static PIDController Pid;
	public static String armPosition="down", hoodPosition="down", shooterSpeedDisplayed ="off";
	public static boolean testAuto = false;
	public static Thread visionThread;
	public static double[] centerXs;
	public static double[] areas;
	public static double[][] targetDataArrays = new double[2][];
    public static final double PI = Math.PI;
	public static int autoLoopCounter;
    public static AnalogGyro vGyro;
    public static ADXRS450_Gyro hGyro;
    public static boolean isVisionTargeting = false;
    public static boolean isCameraStarted = false;
    public static int lastTracked = 150;
    public static final int cameraX = 350;
    public static boolean isTracking = false;
	public static final double /** Changeable constant values */
			revSpeed = 0.5,	//Drive rev speed
			k_RPM1 = 15950, 	//Low RPM speed
			k_RPM2 = 16750,	//Mid 1 RPM speed
			k_RPM3 = 18250, 	//Mid 2 RPM speed
			k_RPM4 = 25000,	//High RPM speed

            k_TOLERANCE = 0.05;
    public static final int k_valuesToAverage = 5;// number of values to average from the driver input
    public static final int camErrorPercent = 6;
    public static final int stageTwoErrorPercent = 10;
    public static int location = -1;
    public static int defense = -1;
    public static boolean lockTargets = true;
    public static boolean fireButton = false;
    double largestArea = 0;
    int largeAreaIndex;

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

        AutoUtils.adjuster = new RobotRotator(true);
    	
    	//PID decelerations
    	shooterSpeed.setDistancePerPulse(1.5);
		shooterSpeed.setPIDSourceType(PIDSourceType.kRate);
		Pot.setPIDSourceType(PIDSourceType.kDisplacement);
		Pid = new PIDController(0.0000001, 0.0000000009, 0.0001, shooterSpeed, mShooter, 0.001);
	
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
    	
		if (operatorController.getRawButton(2)) {
			centerXs = grip.getNumberArray("targeting/centerX", new double[0]);
			areas = grip.getNumberArray("targeting/area", new double[0]);

			if (areas.length != 0) {
				for (int i = 0; i < areas.length; i++) {
					if (areas[i] > largestArea) {
						largestArea = areas[i];
						largeAreaIndex = i;
					}
				}
			}
			if (centerXs.length > 0) {
				System.out.println("Center X: " + centerXs[largeAreaIndex]);
				System.out.println("Percentage Off: "
						+ (centerXs[largeAreaIndex] - cameraX) / 400);
			}
			if (centerXs.length != 0) {
				if (centerXs[largeAreaIndex] > cameraX
						+ (cameraX * 0.01 * stageTwoErrorPercent)) {
					isTracking = true;
					driveBase.tankDrive(-0.56, 0.56);
					System.out.println("Turning Left");
					SmartDashboard.putBoolean("Vision: ", false);
				} else if (centerXs[largeAreaIndex] < cameraX
						- (cameraX * 0.01 * stageTwoErrorPercent)) {
					isTracking = true;
					driveBase.tankDrive(0.49, -0.49);
					System.out.println("Turning Right");
					SmartDashboard.putBoolean("Vision: ", false);
				}else if (centerXs[largeAreaIndex] > cameraX
						+ (cameraX * 0.01 * camErrorPercent)) {
					isTracking = true;
					driveBase.tankDrive(-0.54, 0.54);
					System.out.println("Turning Left");
					SmartDashboard.putBoolean("Vision: ", false);
				} else if (centerXs[largeAreaIndex] < cameraX
						- (cameraX * 0.01 * camErrorPercent)) {
					isTracking = true;
					driveBase.tankDrive(0.475, -0.475);
					System.out.println("Turning Right");
					SmartDashboard.putBoolean("Vision: ", false); 
					}else {
					isTracking = false;
					SmartDashboard.putBoolean("Vision: ", true);
				}
				lastTracked  = 0;
			}else{
				isTracking = false;
			}
			lastTracked  = 0;
		}else
		{
			isTracking = false;
			if (lastTracked > 150){
			SmartDashboard.putBoolean("Vision: ", false);
			}
		}
    	
    	Teleop.teleopPeriodic();
    	lastTracked++;
    }   
    
    public void disabledInit() {
    	compressor.stop();
        Disabled.onDisabledInit();
    }
    private final NetworkTable grip = NetworkTable.getTable("GRIP");
    
    public void disabledPeriodic(){
    	
        Disabled.onDisabledPeriodic();

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
   
   public static void waitVision(){
	  try {
		visionThread.wait();
	} catch (InterruptedException e) {
		
	}
   }
}