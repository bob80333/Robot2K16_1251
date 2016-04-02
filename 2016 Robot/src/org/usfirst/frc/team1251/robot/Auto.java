package org.usfirst.frc.team1251.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

/**
 * Created by Eric on 3/10/2016.
 */
public class Auto {
    public static int autoLoopCounter;
    public static void onAutoInit(){
        Robot.collectorArm.set(Value.kForward);
        autoLoopCounter = 1;
    }

    public static void onAutoPeriodic(){
        /*if (Auto.goneDownDefense){
            Auto.loopsSinceCrossed++;
        }
        if (!Autonomous.crossed){
            Autonomous.crossDefenses(defense);
        =}*/
            if (autoLoopCounter <= 125){
                Robot.driveBase.tankDrive(0.8, 0.8);
            }
        	//AutoUtils.crossPort();
        	//Robot.mCollector.set(-1);
        autoLoopCounter++;
    }
}
