package org.usfirst.frc.team1251.robot;

import org.usfirst.frc.team1251.robot.events.listeners.AutoInitListener;
import org.usfirst.frc.team1251.robot.events.listeners.AutoPeriodicListener;

/**
 * Created by Eric on 3/9/2016.
 */
public class Auto implements AutoInitListener, AutoPeriodicListener{
    int autoLoopCounter;
    @Override
    public void onAutoInit() {
        //Robot.visionThread.run();
        autoLoopCounter = 1;
    }

    @Override
    public void onAutoPeriodic() {
        // check every 20 loops for new image data
        if (autoLoopCounter%20 > 0 && Robot.testAuto){
            Robot.contours = Robot.vision.getContours();
        }

        if (AutoUtilities.goneDownDefense) {
            AutoUtilities.loopsSinceCrossed++;
        }
        if (!AutoUtilities.crossed) {
            AutoUtilities.crossDefenses(Robot.defense);
        }
        if (Robot.testAuto) {
            //Runs the visionThread code
            if (Robot.visionThread.isAlive()) {
                Robot.visionThread.notify();
                Robot.visionThread.run();
            } else {
                Robot.visionThread.run();
            }

        } else {
            // competition code right here
            if (autoLoopCounter <= 230){
                Robot.driveBase.tankDrive(0.6, 0.6);
            }

        }
        autoLoopCounter++;
    }
}
