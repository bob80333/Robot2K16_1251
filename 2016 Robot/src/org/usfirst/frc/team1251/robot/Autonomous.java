package org.usfirst.frc.team1251.robot;
import org.usfirst.frc.team1251.robot.Robot;
/**
 * Created by Eric on 3/5/2016.
 */
public class Autonomous {
    public static boolean goneUpDefense = false;
    public static boolean goingUpDefense = false;
    public static boolean goneDownDefense = false;
    public static boolean goingDownDefense = false;
    public static int loopsSinceCrossed = 0;
    public static final double crossingAngle = 10.0;
    public static boolean crossed = false;
    public static boolean targetLockedSuccessfully = false;
    public static void crossDefenses(int defense){
        switch (defense) {
            case -1:
                // it couldn't find the defense we are crossing
                // just sit there stupidly
            case 0:
                // this is the default, just crosses the defense
                if (!goneDownDefense && !(loopsSinceCrossed > 10)) {
                    Robot.driveBase.tankDrive(0.8, 0.8);
                } else{
                    crossed = true;

                }
                if (Robot.vGyro.getAngle() > crossingAngle){
                    goingUpDefense = true;
                }else if (Robot.vGyro.getAngle() < crossingAngle){
                    goingDownDefense = true;
                }else if (Robot.vGyro.getAngle() > -1 && Robot.vGyro.getAngle() < 1){
                    if (goingUpDefense){
                        goneUpDefense = true;
                    }else if (goingDownDefense){
                        goneDownDefense = true;
                    }
                }



            case 1:
            // cross the teeter-totters


            case 2:
            // open the gateway


        }

    }

    public static void approachTarget(int location){
        switch (location) {
            case -1:
                // no location given, try to find the target
                double lowestAngleTarget = Robot.PI + 1; //init with impossible number
                int lowestAngleTargetIndex = -1; // impossible
                double secondLowestAngleTarget = Robot.PI + 1; //init with impossible number
                int secondLowestAngleTargetIndex = -1; // impossible
                // find lowest angle difference target
                for (int i = 0; i < Robot.anglesToTarget.length; i++) {
                    if (Math.abs(Robot.normalizeAngle(Robot.anglesToTarget[i])) < Math.abs(lowestAngleTarget)) {
                        secondLowestAngleTarget = lowestAngleTarget;
                        secondLowestAngleTargetIndex = lowestAngleTargetIndex;
                        lowestAngleTarget = Robot.anglesToTarget[i];
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

    public static void adjustRobotAngle(){
        if (!targetLockedSuccessfully){

        }
    }
}
