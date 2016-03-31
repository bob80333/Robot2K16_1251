package org.usfirst.frc.team1251.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Created by Eric on 3/24/2016.
 */
public class Disabled {
    public static void onDisabledInit(){
        Robot.visionThread.start();
    }

    public static void onDisabledPeriodic(){
        SmartDashboard.putData("Turn Off Pi", Robot.turnOffPi);
        if(!Robot.visionThread.isAlive()){
            Robot.visionThread.start();
        }
        if (Robot.vision.isFoundData()){
            NetworkTable.getTable("It Works").putBoolean("It works", true);
            SmartDashboard.putBoolean("works", true);
        }else{
            SmartDashboard.putBoolean("works", false);
        }
    }
}
