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
        Robot.visionThread.start();
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
            if (!Robot.visionThread.isAlive()) {
                Robot.visionThread.start();
            }



        }else{
            if (autoLoopCounter <= 125){
                Robot.driveBase.tankDrive(0.8, 0.8);
            }

        }
        autoLoopCounter++;
    }
}
