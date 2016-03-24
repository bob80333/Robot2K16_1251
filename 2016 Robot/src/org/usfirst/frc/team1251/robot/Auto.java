package org.usfirst.frc.team1251.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * Created by Eric on 3/10/2016.
 */
public class Auto {
    public static int autoLoopCounter;
    public static void onAutoInit(){
        Robot.collectorArm.set(DoubleSolenoid.Value.kForward);
        autoLoopCounter = 1;
    }

    public static void onAutoPeriodic(){
        /*if (Auto.goneDownDefense){
            Auto.loopsSinceCrossed++;
        }
        if (!Autonomous.crossed){
            Autonomous.crossDefenses(defense);
        =}*/
        if (Robot.testAuto) {
            //Runs the visionThread code
            if (Robot.visionThread.isAlive()) {
                Robot.visionThread.notify();
                Robot.visionThread.run();
            } else {
                Robot.visionThread.run();
            }
            // targetDataArrays = vision.getTargetData();

            //unpack data into 2 single dimension arrays
            Robot.distancesToTarget = Robot.targetDataArrays[0];
            Robot.anglesToTarget = Robot.targetDataArrays[1];
            //choose two lowest angles, and then choose the lower angled one b/c it will have less total  distance



        }else{
            if (autoLoopCounter <= 125){
                Robot.driveBase.tankDrive(0.8, 0.8);
            }

        }
        autoLoopCounter++;
    }
}
