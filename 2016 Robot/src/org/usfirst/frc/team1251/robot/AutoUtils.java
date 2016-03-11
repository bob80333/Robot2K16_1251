package org.usfirst.frc.team1251.robot;

import org.usfirst.frc.team1251.robot.vision.Contour;

/**
 * Created by Eric on 3/10/2016.
 */
public class AutoUtils {

    /**
     * Created by Eric on 3/5/2016.
     **/

        public static boolean goneUpDefense = false;
        public static boolean goingUpDefense = false;
        public static boolean goneDownDefense = false;
        public static boolean goingDownDefense = false;
        public static int loopsSinceCrossed = 0;
        public static final double crossingAngle = 10.0;
        public static boolean crossed = false;
        public static boolean targetLockedSuccessfully = false;
        public static final double angleMargin = 0.5;
        public static final double k_turnPercentage = 0.002;

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
            switch (location) {
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
            if (!targetLockedSuccessfully){
                if ((Robot.hGyro.getAngle() > Robot.hGyro.getAngle() + target.getAngle() + angleMargin)
                        || (Robot.hGyro.getAngle() < Robot.hGyro.getAngle() + target.getAngle() - angleMargin)){
                    Robot.driveBase.tankDrive((-Robot.hGyro.getAngle() + target.getAngle()) * k_turnPercentage,
                            (Robot.hGyro.getAngle() + target.getAngle()) * k_turnPercentage);
                }else{
                    targetLockedSuccessfully = true;
                }
            }
        }
    }

