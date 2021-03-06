package org.usfirst.frc.team1251.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

/**
 * Created by Eric on 3/10/2016.
 */
public class AutoUtils {

    /**
     * Created by Eric on 3/5/2016.
     **/
    static RobotRotator adjuster;
    public static boolean goneUpDefense = false;
    public static boolean goingUpDefense = false;
    public static boolean goneDownDefense = false;
    public static boolean goingDownDefense = false;
    public static int loopsSinceCrossed = 0;
    public static final double crossingAngle = 10.0;
    public static boolean crossed = false;
    public static final double angleMargin = 0.5;

    public static void crossDefenses(int defense) {
        switch (defense) {
            case -1:
                // it couldn't find the defense we are crossing
                // just sit there stupidly
            case 0:
                // this is the default, just crosses the defense
                if (!goneDownDefense && !(loopsSinceCrossed > 10)) {
                    Robot.driveBase.tankDrive(0.8, 0.8);
                } else {
                    crossed = true;

                }
                if (Robot.vGyro.getAngle() > crossingAngle) {
                    goingUpDefense = true;
                } else if (Robot.vGyro.getAngle() < crossingAngle) {
                    goingDownDefense = true;
                } else if (Robot.vGyro.getAngle() > -1 && Robot.vGyro.getAngle() < 1) {
                    if (goingUpDefense) {
                        goneUpDefense = true;
                    } else if (goingDownDefense) {
                        goneDownDefense = true;
                    }
                }


            case 1:
                // cross the teeter-totters


            case 2:
                // open the gateway


        }
    }

    public static void changeDrivetrainAbsoluteAngle(double degrees, int loopsToTake){

    }

    public static void crossPort(){
        if (Robot.autoLoopCounter < 25){
            Robot.driveBase.tankDrive(0.6, 0.6);
            Robot.collectorArm.set(Value.kReverse);
        }else if (Robot.autoLoopCounter < 30){
            Robot.driveBase.tankDrive(0, 0);
            Robot.mCollector.set(-1);
        }else if (Robot.autoLoopCounter < 50){
        	Robot.collectorArm.set(Value.kForward);
            Robot.driveBase.tankDrive(0.85, 0.85);
        }else if (Robot.autoLoopCounter < 100){
            Robot.driveBase.tankDrive(0.85, 0.85);
            Robot.mCollector.set(0);
        }else{
            Robot.mCollector.set(0);
            Robot.driveBase.tankDrive(0, 0);
        }
    }
}

