
package org.usfirst.frc.team1251.robot;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**Code for prototype testing */

public class Robot extends IterativeRobot {
	
	/**The Following code is used to switch values depending on the controller used
	 X-BOX: cLeft=1, cRight=5, cShoot=3, cIntake=5 |
	 LOGITECH: cLeft=1, cRight=3, cShoot=1, cIntake=5 | 
	**/
	int cLeft = 1;
	int cRight = 3;

	int cShoot = 1;
	int cIntake = 5;
	int cSpeed = 6;
	
	double sMulti = 1.0;
	double accel = 0.05;
	/**--------------------------**/
	double lLimiter, rLimiter, lSpeed, rSpeed;
	
	int lNegative, rNegative;
	RobotDrive mainDrive;
	Joystick controller, cStick;
	Victor shooter;
	Encoder eWheel;
	
	double P = 0.00003;
	double I = 0.0026;
	double D = 0.0002;
	
	PIDController pid;
	
	
    public void robotInit() {
    	mainDrive = new RobotDrive(1, 0);
    	
    	controller = new Joystick(0);
    	cStick = new Joystick(1);
    	
    	shooter = new Victor (2);
    	
    	lLimiter = 0.0;
    	rLimiter = 0.0;
    	
    	eWheel = new  Encoder(1, 2, true, EncodingType.k4X);
    	eWheel.setPIDSourceType(PIDSourceType.kRate);
    	pid = new PIDController(P, I, D, eWheel, shooter);
    	
    	shooter.setInverted(true);
    	
    }
    
    public void autonomousInit() { //empty
    	//Temporarily blank
    }
    
    public void autonomousPeriodic() { //empty
    	//Temporarily blank
    }
    
    public void teleopInit(){
    	pid.enable();
    	pid.setSetpoint(7000);
    }

    public void teleopPeriodic() {
    	
    	if (isNegative(controller.getRawAxis(cLeft)))
    		lNegative = -1;
    	else
    		lNegative = 1;
    	if (isNegative(controller.getRawAxis(cRight)))
    		rNegative = -1;
    	else
    		rNegative = 1;
    	if (controller.getRawAxis(cLeft) > lLimiter)
    		lLimiter = lLimiter + accel;		
    	else 
    		lLimiter = lLimiter - accel;
    	
    	if (controller.getRawAxis(cRight) > rLimiter)
    		rLimiter = rLimiter + accel;		
    	else
    		rLimiter = rLimiter - accel;
    	
    	if(controller.getRawButton(cSpeed)){
        	lSpeed = controller.getRawAxis(cLeft)* sMulti;
        	rSpeed = controller.getRawAxis(cRight)* sMulti;
        	mainDrive.tankDrive(-lSpeed, -rSpeed);
    	}
    	else {
        	lSpeed = lNegative*(Math.pow(controller.getRawAxis(cLeft), 2))* sMulti * Math.abs(lLimiter);
        	rSpeed = rNegative*(Math.pow(controller.getRawAxis(cRight), 2))* sMulti * Math.abs(rLimiter);
    		mainDrive.tankDrive(-lSpeed, -rSpeed);
    	}
    	
    	
    	if (controller.getRawButton(cShoot))
    		pid.enable();
        	
    	else
        	pid.disable();
    	
    	SmartDashboard.putNumber("Wheel rpm: ", eWheel.getRate());
    	SmartDashboard.putNumber("shooter: ", shooter.get());
    	
    }
    
    public void testPeriodic() { //empty
    	//Temporarily unused
    }
    
    public void disabledInit(){
    	pid.disable();
    }
    
    public static boolean isNegative(double d) {
        return Double.compare(d, 0.0) < 0;
   }
    
}
