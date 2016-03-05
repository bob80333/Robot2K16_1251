package org.usfirst.frc.team1251.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.apache.commons.math3.util.MathUtils;
import org.usfirst.frc.team1251.robot.vision.Vision;

/**
 * This is the main code for team 1251's 2016 robot.
 * Do not make any edits to this code without explicit permission from 
 * Jared Pilewski, Eric Engelhart, Mr.Elich, or Juan Coto
 */

public class Robot extends IterativeRobot {

	private RobotDrive driveBase;
	private Joystick driveController, operatorController;
	private Victor mCollector, mShooter;
	private Compressor compressor;
	private Solenoid collectorArm, shooterHood;
	private DigitalInput ballDetect;
	private Encoder shooterSpeed;
	private AnalogPotentiometer Pot;
	private PIDController Pid;
	private String armPosition="down", hoodPosition="down", shooterSpeedDisplayed ="off";
	private double lRev=0, rRev=0, lAxis, rAxis;
	private boolean detect;
	private boolean testAuto = true;
    private Vision vision;
	private Thread visionThread;
	private double[] anglesToTarget = {};
	private double[] distancesToTarget = {};
	private double[][] targetDataArrays = new double[2][];
    private double averageJoystickRight;
    private double averageJoystickLeft;
    private final double PI = Math.PI;
	
	private final double /** Changeable constant values */
			revSpeed = 0.5,	//Drive rev speed
			k_RPM1 = 1000, 	//Low RPM speed
			k_RPM2 = 2000,	//Mid 1 RPM speed
			k_RPM3 = 3000, 	//Mid 2 RPM speed
			k_RPM4 = 4000;	//High RPM speed
    private final int k_valuesToAverage = 5; // number of values to average from the driver input
    private double[] joystickListRight = new double[k_valuesToAverage];
    private double[] joystickListLeft = new double[k_valuesToAverage];
	
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
    	
    	//Encoder using DIO 1, 2, 3
    	shooterSpeed = new Encoder(2, 3, true, EncodingType.k2X);
    	
    	//Potentiometer using analog 3
    	Pot = new AnalogPotentiometer(3, 360, 0);
    	
    	//PID decelerations
    	shooterSpeed.setDistancePerPulse(1.5);
		shooterSpeed.setPIDSourceType(PIDSourceType.kRate);
		Pot.setPIDSourceType(PIDSourceType.kDisplacement);
		Pid = new PIDController(0.05, 0.005, 0.5, shooterSpeed, mShooter);

		vision = new Vision();
		visionThread = new Thread(vision, "Vision-Tracking");
    }

    public void autonomousInit() {
    	visionThread.run();
    }
    
    public void autonomousPeriodic() {
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
            double lowestAngleTarget = PI + 1; //init with impossible number
            int lowestAngleTargetIndex = -1; // impossible
            double secondLowestAngleTarget = PI + 1; //init with impossible number
            int secondLowestAngleTargetIndex = -1; // yet again, impossible
            for (int i = 0; i < anglesToTarget.length; i++) {
                if (Math.abs(normalizeAngle(anglesToTarget[i])) < Math.abs(lowestAngleTarget)) {
                    secondLowestAngleTarget = lowestAngleTarget;
                    secondLowestAngleTargetIndex = lowestAngleTargetIndex;
                    lowestAngleTarget = anglesToTarget[i];
                    lowestAngleTargetIndex = i;
                }
            }

            if (lowestAngleTargetIndex == -1) {
                // assume no targets found/processed
            } else if (secondLowestAngleTargetIndex == -1) {
                // assume only 1 target found/processed
                // proceed but use only the lowest target
            } else {
                // do targeting stuff here
            }


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
    	
    	/*//Drive rev up
    	if (lAxis > lRev) {
    		lRev += revSpeed;
    	}
    	else {
    		lRev -= revSpeed;
    	}
    	if (rAxis > rRev) {
    		rRev += revSpeed;
    	}
    	else {
    		rRev -= revSpeed;
    	}
    	
    	//Drive base movement
    	driveBase.tankDrive(lAxis * Math.abs(lRev), rAxis * Math.abs(rRev));
    	*/
        // reset averages
        averageJoystickLeft = 0;
        averageJoystickRight = 0;
        // move values over 1 & add the values to the average
        for (int i = 0; i < k_valuesToAverage - 1; i++){
            joystickListLeft[i] = joystickListLeft[i+1];
            joystickListRight[i] = joystickListRight[i+1];
            averageJoystickLeft += joystickListLeft [i];
            averageJoystickRight += joystickListRight[i];
        }
        // add the new joystick input and add it to the average as well
        joystickListLeft[k_valuesToAverage-1] = -lAxis;
        joystickListRight[k_valuesToAverage-1] = -rAxis;
        averageJoystickLeft += joystickListLeft [k_valuesToAverage-1];
        averageJoystickRight += joystickListRight[k_valuesToAverage-1];
        // average out all the added values
        averageJoystickLeft = averageJoystickLeft / (double) k_valuesToAverage;
        averageJoystickRight = averageJoystickRight / (double) k_valuesToAverage;
        // move the robot with those averages
        driveBase.tankDrive(averageJoystickLeft, averageJoystickRight);
    	//Collector arm up and down
        if (driveController.getRawButton(3)) { //up
        	collectorArm.set(true);
        	armPosition = "Up";
        }
        if (driveController.getRawButton(2)) { //down
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
        
        //Shooter multi-RPM
        if (operatorController.getRawButton(5)) { //Low
        	Pid.setSetpoint(((k_RPM1/60)*360)*1.5);
        	Pid.enable();
        	shooterSpeedDisplayed = "Low";
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
        	shooterSpeedDisplayed = "Medium 1";
        	if (operatorController.getRawButton(2) && detect) {
            	mCollector.set(1);
            }
        	else {
        		mCollector.set(0);
        	}
        }
        else if (operatorController.getRawButton(7)) { //Medium 2
        	Pid.setSetpoint(((k_RPM3/60)*360)*1.5);
        	Pid.enable();
        	shooterSpeedDisplayed = "Medium 2";
        	if (operatorController.getRawButton(2) && detect) {
            	mCollector.set(1);
            }
        	else {
        		mCollector.set(0);
        	}
        }
        else if (operatorController.getRawButton(8)) { //High
        	Pid.setSetpoint(((k_RPM4/60)*360)*1.5);
        	Pid.enable();
        	shooterSpeedDisplayed = "High";
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
        	shooterSpeedDisplayed = "off";
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
        SmartDashboard.putNumber("Motor Revolutions/M ", ((shooterSpeed.getRate()/1.5)/360)*60);
        SmartDashboard.putNumber("Joystick D-pad ", driveController.getPOV());
        SmartDashboard.putString("Arm position ", armPosition);
        SmartDashboard.putString("Hood Position ", hoodPosition);
        SmartDashboard.putString("Shooter setting ", shooterSpeedDisplayed);
    }   
    
    public void disable() {
    	compressor.stop();
    }

    /**
     * the angle is normalized from -π to +π
     * @param angle the angle to be normalized in radians
     * @return normalized angle
     */
    public double normalizeAngle(double angle){
        // normalize angle to [-π, π]
        return MathUtils.normalizeAngle(angle, 0.0);
    }
}