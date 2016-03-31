package org.usfirst.frc.team1251.robot;

import org.usfirst.frc.team1251.robot.vision.Contour;

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

    public static void approachTarget(int location) {

        switch (location)

        {
            case -1:
                // no location given, try to find the target
                double lowestAngleTarget = Robot.PI + 1; //init with impossible number
                double secondLowestAngleTarget = Robot.PI + 1; //init with impossible number
                // find lowest angle difference target
                for (Contour contour : Robot.contours) {
                    if (Math.abs(Robot.normalizeAngle(contour.getAngle())) < Math.abs(contour.getAngle())) {
                        secondLowestAngleTarget = lowestAngleTarget;
                        lowestAngleTarget = contour.getAngle();
                    }
                }


                // do targeting stuff here
            case 0:
                // we are the spybot


            case 1:
                // we are in position 2


            case 2:
                // we are in position 3


            case 3:
                // we are in position 4


            case 4:
                // we are in position 5


        }

    }


    public static void adjustRobotAngle(Contour target){
        adjuster.changeDrivetrainAngle(target.getAngle(), 70);
    }

    public static void changeDrivetrainAbsoluteAngle(double degrees, int loopsToTake){

    }

    public static void crossPort(){
        if (Robot.autoLoopCounter < 5){
            Robot.driveBase.tankDrive(0.8, 0.8);
        }else if (Robot.autoLoopCounter < 10){
            Robot.driveBase.tankDrive(0.1, 0.1);
            Robot.mCollector.set(1);
        }else if (Robot.autoLoopCounter < 20){
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

