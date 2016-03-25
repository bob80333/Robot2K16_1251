package org.usfirst.frc.team1251.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Created by Eric on 3/10/2016.
 */
public class Teleop {

    static double lAxis;
    static double rAxis;
    static double averageJoystickLeft;
    static double averageJoystickRight;
    static double[] joystickListLeft = new double[Robot.k_valuesToAverage];
    static double[] joystickListRight = new double[Robot.k_valuesToAverage];
    static boolean detect;
    static boolean isShooting = false;
    static int teleopLoopCounter;

    public static void teleopInit(){
        Robot.Pid.disable();
        Robot.compressor.start();
        teleopLoopCounter = 1;
    }

    public static void teleopPeriodic(){
//Declaring variable to detector button
        detect = !Robot.ballDetect.get();

        //Declaring variables to driver axis
        lAxis = -Robot.driveController.getRawAxis(1);
        rAxis = -Robot.driveController.getRawAxis(3);

        // move values over 1 & add the values to the average


        averageJoystickLeft = 0;
        averageJoystickRight = 0;
        for (int i = 0; i < Robot.k_valuesToAverage - 1; i++){
            joystickListLeft[i] = joystickListLeft[i+1];
            joystickListRight[i] = joystickListRight[i+1];
            averageJoystickLeft += joystickListLeft[i];
            averageJoystickRight += joystickListRight[i];
        }
        // add the new joystick input and add it to the average as well
        joystickListLeft[Robot.k_valuesToAverage-1] = lAxis;
        joystickListRight[Robot.k_valuesToAverage-1] = rAxis;
        if (lAxis < 0){
            averageJoystickLeft -= Math.pow(lAxis, 2.0);

        }else if (lAxis >= 0){
            averageJoystickLeft += Math.pow(lAxis, 2.0);
        }

        if (rAxis < 0){
            averageJoystickRight -= Math.pow(rAxis, 2.0);

        }else if (rAxis >= 0){
            averageJoystickRight += Math.pow(rAxis, 2.0);
        }
        if (((averageJoystickRight > 0.5 && averageJoystickLeft < -0.5) || (averageJoystickRight < -0.5 && averageJoystickLeft > 0.5)) && !Robot.driveController.getRawButton(6)){
            averageJoystickRight *= 0.7;
            averageJoystickLeft *= 0.7;
        }
        
        if (!Robot.driveController.getRawButton(6)){
        averageJoystickLeft *= 0.8;
        averageJoystickRight *= 0.8;
        }else{
        	averageJoystickLeft *= 0.95;
        	averageJoystickRight *= 0.95;
        }

        averageJoystickLeft /= Robot.k_valuesToAverage;
        averageJoystickRight /= Robot.k_valuesToAverage;

        Robot.driveBase.tankDrive(averageJoystickLeft, averageJoystickRight);
        //Collector arm up and down
        if (Robot.driveController.getRawButton(4)) { //up
            Robot.collectorArm.set(Value.kForward );
            Robot.armPosition = "Up";
        }
        else if (Robot.driveController.getRawButton(3)) { //down
            Robot.collectorArm.set(Value.kReverse);
            Robot.armPosition = "Down";
        }

        //Hood up and down
        if (Robot.operatorController.getPOV() == 0) {
            Robot.shooterHood.set(true);
            Robot.hoodPosition = "Up";
        }
        else if (Robot.operatorController.getPOV() == 180) {
            Robot.shooterHood.set(false);
            Robot.hoodPosition = "Down";
        }

        //Shooter method


        //Shooter multi-RPM
        if (Robot.operatorController.getRawButton(5)) { //Low
        	//Robot.mShooter.set(.7);
            Robot.Pid.setSetpoint(Robot.k_RPM1);
            //Robot.Pid.setPID(0.0005, 0.004, 0, 0);
            
            Robot.Pid.enable();
            Robot.shooterSpeedDisplayed = "Low";
            if (Robot.operatorController.getRawButton(2)) {
                Robot.mCollector.set(1);
            }
            else {
                Robot.mCollector.set(0);
            }
            isShooting = true;
        }
        else if (Robot.operatorController.getRawButton(6)) { //Medium 1
        	//Robot.mShooter.set(0.78);
            Robot.Pid.setSetpoint(Robot.k_RPM2);
            //Robot.Pid.setPID(0.0009, 0, 0, 0);
            Robot.Pid.enable();
            Robot.shooterSpeedDisplayed = "Medium 1";
            if (Robot.operatorController.getRawButton(2)) {
                Robot.mCollector.set(1);
            }
            else {
                Robot.mCollector.set(0);
            }
            isShooting = true;
        }
        else if (Robot.operatorController.getRawButton(7)) { //Medium 2
        	//Robot.mShooter.set(1);
            Robot.Pid.setSetpoint(Robot.k_RPM3);
            //Robot.Pid.setPID(0.0004, 0.004, 0.0001, 0);
            Robot.Pid.enable();
            Robot.shooterSpeedDisplayed = "Medium 2";
            if (Robot.operatorController.getRawButton(2)) {
                Robot.mCollector.set(1);
            }
            else {
                Robot.mCollector.set(0);
            }
            isShooting = true;
        }
        else if (Robot.operatorController.getRawButton(8)) { //High
        	//Robot.mShooter.set(1);
            Robot.Pid.setSetpoint(Robot.k_RPM4);
            //Robot.Pid.setPID(0.001, 0.004, 0, 1);
            Robot.Pid.enable();
            Robot.shooterSpeedDisplayed = "High";
            if (Robot.operatorController.getRawButton(2)) {
                Robot.mCollector.set(1);
            }
            else {
                Robot.mCollector.set(0);
            }
            isShooting = true;
        }
        else { //PID off;
            Robot.Pid.setSetpoint(0);
        	//Robot.mShooter.set(0);
            Robot.Pid.disable();
            Robot.shooterSpeedDisplayed = "off";
            isShooting = false;
        }
        if (detect && isShooting){
            detect = false;
        }
        //Collector detection
        if (Robot.operatorController.getRawButton(1) && !detect) { //for intake
            Robot.mCollector.set(1.0);
        }
        else if (Robot.operatorController.getRawButton(3)) { //for outtake
            Robot.mCollector.set(-1.0);
        }
        else { //Collector off
            Robot.mCollector.set(0);
        }
 
        //SmartDashboard information
        SmartDashboard.putNumber("Motor Revolutions/M ", ((Robot.shooterSpeed.getRate()/1.5)/360)*60);
        SmartDashboard.putNumber("Joystick D-pad ", Robot.driveController.getPOV());
        SmartDashboard.putString("Arm position ", Robot.armPosition);
        SmartDashboard.putString("Hood Position ", Robot.hoodPosition);
        SmartDashboard.putString("Shooter setting ", Robot.shooterSpeedDisplayed);
        SmartDashboard.putNumber("Location", Robot.location);
        SmartDashboard.putNumber("Shooter RPM", Robot.shooterSpeed.getRate());
        SmartDashboard.putData("PID", Robot.Pid);

        teleopLoopCounter++;
    }
}
