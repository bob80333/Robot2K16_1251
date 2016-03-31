package org.usfirst.frc.team1251.robot;

/**
 * Created by Eric on 3/31/2016.
 */
public class RobotRotator {

    public static final double angleMargin = 0.5;
    public static final double k_turnPercentage = 0.002;
    public static double startingAngle = 0;
    public static int currentLoop = 0;
    public static boolean turningCompleted = false;
    public static boolean turningFirstLoop = true;
    public static boolean interfereWithDriving = false;

    public RobotRotator(boolean interfereWithDriving){
        this.interfereWithDriving = interfereWithDriving;
    }

    public void changeDrivetrainAngle(double changeInDegrees, int loopsToTake){
        changeAbsoluteDrivetrainAngle(changeInDegrees + Robot.hGyro.getAngle(), loopsToTake);
    }

    public void changeAbsoluteDrivetrainAngle(double degrees, int loopsToTake){
        if (turningFirstLoop) {
            currentLoop = 1;
            turningFirstLoop = false;
            turningCompleted = false;
        }
        if (!turningCompleted){

            if (!(degrees > Robot.hGyro.getAngle() + angleMargin && degrees < Robot.hGyro.getAngle() - angleMargin)) {
                Robot.driveBase.tankDrive(((loopsToTake / currentLoop) * ((degrees - Robot.hGyro.getAngle())) * k_turnPercentage), -((loopsToTake / currentLoop) * ((degrees - Robot.hGyro.getAngle())) * k_turnPercentage));
            }else{
                turningCompleted = true;
            }
        }
        if (currentLoop == loopsToTake){
            turningCompleted = false;
            turningFirstLoop = true;
        }
        currentLoop++;
    }

    public void interfereWithDriving(boolean interfere){
        this.interfereWithDriving = interfere;
    }

    public boolean rotationIsDone(){
        return turningCompleted;
    }

    public boolean isInterfering(){
        return interfereWithDriving;
    }
}
