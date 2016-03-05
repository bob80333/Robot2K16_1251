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
    public static void crossDefenses(int defense){
        switch (defense) {
            case -1:
                // it couldn't find the defense we are crossing
                // just sit there stupidly
            case 0:
                // this is the default, just crosses the defense
                if (!goneDownDefense && !(loopsSinceCrossed > 10)) {
                    Robot.driveBase.tankDrive(0.7, 0.7);
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



            case 2:



        }

    }
}
