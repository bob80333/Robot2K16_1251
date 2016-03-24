package org.usfirst.frc.team1251.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Created by Eric on 3/24/2016.
 */
public class Disabled {
    public static void onDisabledInit(){

    }

    public static void onDisabledPeriodic(){
        SmartDashboard.putData("Turn Off Pi", Robot.turnOffPi);
    }
}
