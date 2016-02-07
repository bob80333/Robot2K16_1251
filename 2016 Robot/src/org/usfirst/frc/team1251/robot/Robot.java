
package org.usfirst.frc.team1251.robot;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**Code for prototype testing */

public class Robot extends IterativeRobot {
	
	/**The Following code is used to switch values depending on the controller used
	 X-BOX: cLeft=1, cRight=5, cShoot=3, cIntake=5 |
	 LOGITECH: cLeft=1, cRight=3, cShoot=1, cIntake=5 | 
	**/
	
	//Left and Right stick on the controller
	int cLeft = 1;
	int cRight = 3;
	
	//Button inputs for shooting and disabling the drive rev   
	int cShoot = 1;
	int cSpeed = 6;
	
	//Multiplier for max speed and acceleration rate
	double sMulti = 1.0;
	double accel = 0.05;
	
	//variables for drive acceleration
	double lLimiter, rLimiter, lSpeed, rSpeed;
	
	//variables for testing if the controller is negative
	int lNegative, rNegative;
	
	//variables for physical parts
	RobotDrive mainDrive;
	Joystick controller, cStick;
	Victor shooter;
	DoubleSolenoid solenoid;
	Compressor compressor;
	Encoder eWheel;
	
	//pid variables
	double P = 0.00003;
	double I = 0.0026;
	double D = 0.0002;
	PIDController pid;
	
	int autoLoopCounter = 0;
	
	AnalogGyro gyroHorizontal;
	AnalogGyro gyroVertical;
	
	boolean rotatingRobot = false;
	
	boolean goneUpObstacle = false;
	boolean goneDownObstacle = false;
	boolean autoLoopCountReset = false;
	
	double desiredAngle;
	//change for different rotation speeds
	final double fastRotate = 0.8;
	final double slowRotate = 0.2;
	//change for different cutoff speeds between fastRotate and slowRotate
	//to get those near perfect turns
	final double fastRotateCutoff = 5;
	final double slowRotateCutoff = 0.3;
	
	double initialVerticalValue = 0;
	
	boolean actuallyRotating = false;
	
	
    public void robotInit() {
    	//initialize the drive base
    	compressor = new Compressor();
    	mainDrive = new RobotDrive(1, 0);
    	
    	//initialize the controller for the robot
    	controller = new Joystick(0);
    	cStick = new Joystick(1);
    	
    	//initialize the victor
    	shooter = new Victor (2);
    	solenoid = new DoubleSolenoid (0,1);
    	
    	//base values for the drive acceleration 
    	lLimiter = 0.0;
    	rLimiter = 0.0;
    	
    	//initialize the encoder and pid
    	eWheel = new  Encoder(1, 2, true, EncodingType.k4X);
    	eWheel.setPIDSourceType(PIDSourceType.kRate);
    	eWheel.setDistancePerPulse(400);
    	pid = new PIDController(P, I, D, eWheel, shooter);
    	
    	//invert the shooter
    	shooter.setInverted(true);
    	gyroHorizontal = new AnalogGyro(0);
    	gyroVertical = new AnalogGyro(1);
    	gyroHorizontal.calibrate();
    	gyroVertical.calibrate();
    	
    	
    	
    	
    }
    
    public void autonomousInit() { 
    	//desiredAngle = rotateRobot(-90);
    	desiredAngle = gyroHorizontal.getAngle();
    	rotatingRobot = true;
    	autoLoopCounter = 0;
    	goneUpObstacle = false;
    	goneDownObstacle = false;
    	autoLoopCountReset = false;
    	initialVerticalValue = gyroVertical.getAngle();
    	
    }
    
    public void autonomousPeriodic() { //empty
    	SmartDashboard.putBoolean("Rotating Robot", rotatingRobot);
    	SmartDashboard.putNumber("Horizontal Gyro", gyroHorizontal.getAngle());
    	SmartDashboard.putNumber("Desired Angle", desiredAngle);
    	SmartDashboard.putNumber("Vertical Gyro", gyroVertical.getAngle());
    	SmartDashboard.putBoolean("Gone up obstacle", goneUpObstacle);
    	SmartDashboard.putBoolean("Gone down obstacle", goneDownObstacle);
    	SmartDashboard.putBoolean("auto loop counter reset", autoLoopCountReset);
    	SmartDashboard.putNumber("Number of auto loops", autoLoopCounter);
    	SmartDashboard.putBoolean("rotating", actuallyRotating);
    	SmartDashboard.putBoolean("in range of being flat", (gyroVertical.getAngle() > (-initialVerticalValue - 1) && gyroVertical.getAngle() < (initialVerticalValue + 1)));
    	if (gyroVertical.getAngle() > (initialVerticalValue + 10))
    		goneUpObstacle = true;
    	else if(gyroVertical.getAngle() < (initialVerticalValue - 10) && goneUpObstacle)
    		goneDownObstacle = true;
    	if (goneUpObstacle && goneDownObstacle && (gyroVertical.getAngle() > (-initialVerticalValue - 2.5) && gyroVertical.getAngle() < (initialVerticalValue + 2.5)) && rotatingRobot && !autoLoopCountReset){
    		autoLoopCounter = 0;
    		autoLoopCountReset = true;
    	}
    	if (goneUpObstacle && goneDownObstacle && (gyroVertical.getAngle() > (-initialVerticalValue - 2.5) && gyroVertical.getAngle() < (initialVerticalValue + 2.5)) && rotatingRobot && autoLoopCounter > 70){
    		actuallyRotating = true;
    		
    	}
    	autoLoopCounter++;
    	if (actuallyRotating && rotatingRobot){
    		rotateDrivetrain();
    	}
    }
    
    public void teleopInit(){
    	//enable pid and set it's speed
    	
    	pid.enable();
    	pid.setSetpoint(1000);
    	compressor.setClosedLoopControl(true);
    
    	}

    public void teleopPeriodic() {
    	
       	//check if the joysticks are negative
    	if (isNegative(controller.getRawAxis(cLeft)))
    		lNegative = -1;
    	else
    		lNegative = 1;
    	if (isNegative(controller.getRawAxis(cRight)))
    		rNegative = -1;
    	else
    		rNegative = 1;
    	
    	//set the limiter amount 
    	if (controller.getRawAxis(cLeft) > lLimiter)
    		lLimiter = lLimiter + accel;		
    	else 
    		lLimiter = lLimiter - accel;
    	
    	if (controller.getRawAxis(cRight) > rLimiter)
    		rLimiter = rLimiter + accel;		
    	else
    		rLimiter = rLimiter - accel;
    	
    	//if a button is pressed then the rev-up is deactivated
    	if(controller.getRawButton(cSpeed)){
        	lSpeed = controller.getRawAxis(cLeft)* sMulti;
        	rSpeed = controller.getRawAxis(cRight)* sMulti;
        	mainDrive.tankDrive(-lSpeed, -rSpeed);
    	}
    	else {
    		//else this formula is used for drivebase rev-up
        	lSpeed = lNegative*(Math.pow(controller.getRawAxis(cLeft), 2))* sMulti * Math.abs(lLimiter);
        	rSpeed = rNegative*(Math.pow(controller.getRawAxis(cRight), 2))* sMulti * Math.abs(rLimiter);
    		mainDrive.tankDrive(-lSpeed, -rSpeed);
    	}
    	
    	//enable or disable pid if the button is or isn't pressed
    	if (controller.getRawButton(cShoot))
    		pid.enable();
    		
        	
    	else
        	pid.disable();
    	
    	
    	
    	//smart dashboard information
    	SmartDashboard.putNumber("Wheel rpm: ", eWheel.getRate()/120);
    	SmartDashboard.putNumber("shooter: ", shooter.get());
    	SmartDashboard.putData("PID: ", pid);
    	SmartDashboard.putNumber("Gyro", gyroHorizontal.getRate());
    	
    	if (controller.getRawButton(2)){
    		solenoid.set(Value.kReverse);
    	}else{
    		solenoid.set(Value.kForward);
    	}
    }
    
    public void testInit(){
    	desiredAngle = gyroHorizontal.getAngle();
    }
    
    public void testPeriodic() { //empty
    	// make all the freshies go 'wow'
    	mainDrive.tankDrive(controller.getRawAxis(cLeft), controller.getRawAxis(cRight));
    	rotateDrivetrain();
    }
    
    public void disabledInit(){
    	pid.disable();
    	compressor.setClosedLoopControl(false);
    
    	}
    
    
    public boolean isNegative(double d) {
        return Double.compare(d, 0.0) < 0;
   }

	public double rotateRobot(double changeInDegrees){
		double currentAngle = gyroHorizontal.getAngle();
		double desiredAngle;
		desiredAngle = currentAngle + changeInDegrees;
		rotatingRobot = true;
		return desiredAngle;
	}
	
	/**
	 * rotates using the gyro to get very close to a desired angle
	 * Change constants in the variables section to calibrate when changing drivetrains
	 */
	public void rotateDrivetrain(){
		if (gyroHorizontal.getAngle() < desiredAngle - 0.5 && (gyroHorizontal.getAngle() - desiredAngle > -fastRotateCutoff)){
			mainDrive.tankDrive(fastRotate, -fastRotate);
		}else if (gyroHorizontal.getAngle() > desiredAngle + 0.5 && (gyroHorizontal.getAngle() - desiredAngle > fastRotateCutoff)){
			mainDrive.tankDrive(-fastRotate, fastRotate);		
		}else if (gyroHorizontal.getAngle() < desiredAngle - 0.2 && (gyroHorizontal.getAngle() - desiredAngle > -slowRotateCutoff)){
			mainDrive.tankDrive(slowRotate, -slowRotate);
		}else if (gyroHorizontal.getAngle() > desiredAngle + 0.2 && (gyroHorizontal.getAngle() - desiredAngle > slowRotateCutoff)){
			mainDrive.tankDrive(-slowRotate, slowRotate);
		}else {
			rotatingRobot = false;
			mainDrive.drive(0, 0);
		}
	}
    
}


