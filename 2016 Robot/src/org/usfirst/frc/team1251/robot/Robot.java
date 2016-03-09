package org.usfirst.frc.team1251.robot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;


import org.apache.commons.math3.util.MathUtils;
import org.usfirst.frc.team1251.robot.events.*;
import org.usfirst.frc.team1251.robot.events.listeners.*;
import org.usfirst.frc.team1251.robot.vision.Vision;

/**
 * This is the main code for team 1251's 2016 robot.
 * Do not make any edits to this code without explicit permission from 
 * Jared Pilewski, Eric Engelhart, Mr.Elich, or Juan Coto
 */

public class Robot extends IterativeRobot {

    public static RobotDrive driveBase;
    public static Joystick driverController, operatorController;
    public static Victor mCollector, mShooter;
    public static Compressor compressor;
    public static Solenoid collectorArm, shooterHood;
    public static DigitalInput ballDetect;
    public static Encoder shooterSpeed;
    public static AnalogPotentiometer Pot;
    public static PIDController Pid;
    public static String armPosition = "down", hoodPosition = "down", shooterSpeedDisplayed = "off";
    public static boolean detect;
    public static boolean testAuto = true;
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
    public static final double /** Changeable constant values */
            revSpeed = 0.5,    //Drive rev speed
            k_RPM1 = 1000,    //Low RPM speed
            k_RPM2 = 2000,    //Mid 1 RPM speed
            k_RPM3 = 3000,    //Mid 2 RPM speed
            k_RPM4 = 4000,    //High RPM speed
            k_TOLERANCE = 0.05;
    public static final int k_valuesToAverage = 5; // number of values to average from the driver input
    public static int location = -1;
    public static int defense = -1;
    public static RobotInitListener robotInitListener;
    public static AutoInitListener autoInitListener;
    public static AutoPeriodicListener autoPeriodicListener;
    public static TeleopInitListener teleopInitListener;
    public static TeleopPeriodicListener teleopPeriodicListener;
    public static DisabledInitListener disabledInitListener;
    public static DisabledPeriodicListener disabledPeriodicListener;
    public static TestInitListener testInitListener;
    public static TestPeriodicListener testPeriodicListener;

    public static EventManager eventManager;

    public void robotInit() {
        // event manager
        // does all the events
        eventManager = new EventManager();
        eventManager.add(RobotInitListener.class, robotInitListener);
        eventManager.fireEvent(RobotInitEvent.class);
        //Drive base using PWM 0, 1, 2, 3
        driveBase = new RobotDrive(0, 1, 2, 3);

        //Joystick for the driver and operator
        driverController = new Joystick(0);
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
            if (data.contains("location=")) {
                location = Integer.parseInt(data.replaceAll("location=", ""));
            } else if (data.contains("defense=")) {
                defense = Integer.parseInt(data.replaceAll("defense=", ""));
            }
        }
        try {
            // read in usb 2
            scan = new Scanner(new File("/media/sda2/robot.conf"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (scan != null) {
            data = scan.nextLine();
            // check for location or defense
            if (data.contains("location=")) {
                location = Integer.parseInt(data.replaceAll("location=", ""));
            } else if (data.contains("defense=")) {
                defense = Integer.parseInt(data.replaceAll("defense=", ""));
            }
        }


        vision = new Vision();
        visionThread = new Thread(vision, "Vision-Tracking");

        vGyro = new AnalogGyro(0);
        hGyro = new ADXRS450_Gyro();
        vGyro.calibrate();
        hGyro.calibrate();
    }

    public void autonomousInit() {
        eventManager.add(AutoInitListener.class, autoInitListener);
        eventManager.fireEvent(AutoInitEvent.class);
        visionThread.run();
        autoLoopCounter = 1;
    }

    public void autonomousPeriodic() {
        eventManager.add(AutoPeriodicListener.class, autoPeriodicListener);
        eventManager.fireEvent(AutoPeriodicEvent.class);
        autoLoopCounter++;
        if (Autonomous.goneDownDefense) {
            Autonomous.loopsSinceCrossed++;
        }
        if (!Autonomous.crossed) {
            Autonomous.crossDefenses(defense);
        }
        if (testAuto) {
            //Runs the visionThread code
            if (visionThread.isAlive()) {
                visionThread.notify();
                visionThread.run();
            } else {
                visionThread.run();
            }
            targetDataArrays = vision.getTargetData();

            //unpack data into 2 single dimension arrays
            distancesToTarget = targetDataArrays[0];
            anglesToTarget = targetDataArrays[1];
            //choose two lowest angles, and then choose the lower angled one b/c it will have less total  distance


        } else {
            driveBase.tankDrive(.70, .77);
        }
    }

    public void teleopInit() {
        Pid.disable();
        compressor.start();
    }

    public void teleopPeriodic() {
        eventManager.add(TeleopPeriodicListener.class, teleopPeriodicListener);
        eventManager.fireEvent(TeleopPeriodicEvent.class);

    }


    
    public void disable() {
    	compressor.stop();
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

    public static void setShooter(double speed){
        Pid.setSetpoint(speed);
        Pid.setPercentTolerance(0.1);
    }
}