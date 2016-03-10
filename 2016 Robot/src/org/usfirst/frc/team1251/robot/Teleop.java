package org.usfirst.frc.team1251.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team1251.robot.events.listeners.TeleopInitListener;
import org.usfirst.frc.team1251.robot.events.listeners.TeleopPeriodicListener;

/**
 * Created by Eric on 3/8/2016.
 */
public class Teleop implements TeleopInitListener, TeleopPeriodicListener {
    double averageJoystickLeft;
    double averageJoystickRight;
    double[] joystickListLeft = new double[Robot.k_valuesToAverage];
    double[] joystickListRight = new double[Robot.k_valuesToAverage];
    double lAxis;
    double rAxis;

    @Override
    public void onTeleopInit() {

    }

    @Override
    public void onTeleopPeriodic() {
        //Declaring variable to detector button
        Robot.detect = Robot.ballDetect.get();

        //Declaring variables to driver axis
        lAxis = -Robot.driverController.getRawAxis(1);
        rAxis = -Robot.driverController.getRawAxis(3);

        // move values over 1 & add the values to the average


        averageJoystickLeft = 0;
        averageJoystickRight = 0;
        for (int i = 0; i < Robot.k_valuesToAverage - 1; i++) {
            joystickListLeft[i] = joystickListLeft[i + 1];
            joystickListRight[i] = joystickListRight[i + 1];
            averageJoystickLeft += joystickListLeft[i];
            averageJoystickRight += joystickListRight[i];
        }
        // add the new joystick input and add it to the average as well
        joystickListLeft[Robot.k_valuesToAverage - 1] = -lAxis;
        joystickListRight[Robot.k_valuesToAverage - 1] = -rAxis;
        if (lAxis < 0) {
            averageJoystickLeft -= Math.pow(lAxis, 2.0);

        } else if (lAxis >= 0) {
            averageJoystickLeft += Math.pow(lAxis, 2.0);
        }

        if (rAxis < 0) {
            averageJoystickRight -= Math.pow(rAxis, 2.0);

        } else if (rAxis >= 0) {
            averageJoystickRight += Math.pow(rAxis, 2.0);
        }
        if (averageJoystickRight > 0.5 && averageJoystickLeft < -0.5) {
            averageJoystickRight *= 0.7;
            averageJoystickLeft *= 0.7;
        }

        averageJoystickLeft /= Robot.k_valuesToAverage;
        averageJoystickRight /= Robot.k_valuesToAverage;

        Robot.driveBase.tankDrive(averageJoystickLeft, averageJoystickRight);
        //Collector arm up and down
        if (Robot.driverController.getRawButton(3)) { //up
            Robot.collectorArm.set(true);
            Robot.armPosition = "Up";
        }
        if (Robot.driverController.getRawButton(2)) { //down
            Robot.collectorArm.set(false);
            Robot.armPosition = "Down";
        }

        //Hood up and down
        if (Robot.operatorController.getPOV() == 0) {
            Robot.shooterHood.set(true);
            Robot.hoodPosition = "Up";
        } else if (Robot.operatorController.getPOV() == 180) {
            Robot.shooterHood.set(false);
            Robot.hoodPosition = "Down";
        }


        //Shooter multi-RPM
        if (Robot.operatorController.getRawButton(5)) { //Low
            Robot.Pid.setSetpoint(((Robot.k_RPM1 / 60) * 360) * 1.5);
            Robot.Pid.enable();
            Robot.shooterSpeedDisplayed = "Low";
            if (Robot.operatorController.getRawButton(2) && Robot.detect) {
                Robot.mCollector.set(1);
            } else {
                Robot.mCollector.set(0);
            }

        } else if (Robot.operatorController.getRawButton(6)) { //Medium 1
            Robot.Pid.setSetpoint(((Robot.k_RPM2 / 60) * 360) * 1.5);
            Robot.Pid.enable();
            Robot.shooterSpeedDisplayed = "Medium 1";
            if (Robot.operatorController.getRawButton(2) && Robot.detect) {
                Robot.mCollector.set(1);
            } else {
                Robot.mCollector.set(0);
            }
        } else if (Robot.operatorController.getRawButton(7)) { //Medium 2
            Robot.Pid.setSetpoint(((Robot.k_RPM3 / 60) * 360) * 1.5);
            Robot.Pid.enable();
            Robot.shooterSpeedDisplayed = "Medium 2";
            if (Robot.operatorController.getRawButton(2) && Robot.detect) {
                Robot.mCollector.set(1);
            } else {
                Robot.mCollector.set(0);
            }
        } else if (Robot.operatorController.getRawButton(8)) { //High
            Robot.Pid.setSetpoint(((Robot.k_RPM4 / 60) * 360) * 1.5);
            Robot.Pid.enable();
            Robot.shooterSpeedDisplayed = "High";
            if (Robot.operatorController.getRawButton(2) && Robot.detect) {
                Robot.mCollector.set(1);
            } else {
                Robot.mCollector.set(0);
            }
        } else { //PID off
            Robot.Pid.setSetpoint(0);
            Robot.Pid.disable();
            Robot.shooterSpeedDisplayed = "off";
        }

        //Collector detection
        if (Robot.operatorController.getRawButton(3) && !Robot.detect) { //for intake
            Robot.mCollector.set(0.5);
        } else if (Robot.operatorController.getRawButton(1)) { //for outtake
            Robot.mCollector.set(-0.5);
        } else { //Collector off
            Robot.mCollector.set(0);
        }


        //SmartDashboard information
        SmartDashboard.putNumber("Motor Revolutions/M ", ((Robot.shooterSpeed.getRate() / 1.5) / 360) * 60);
        SmartDashboard.putNumber("Joystick D-pad ", Robot.driverController.getPOV());
        SmartDashboard.putString("Arm position ", Robot.armPosition);
        SmartDashboard.putString("Hood Position ", Robot.hoodPosition);
        SmartDashboard.putString("Shooter setting ", Robot.shooterSpeedDisplayed);
        SmartDashboard.putNumber("Location", Robot.location);
    }
}
