package org.usfirst.frc.team1251.robot.events.listeners;

import org.usfirst.frc.team1251.robot.events.Event;

/**
 * Created by Eric on 3/8/2016.
 */
public class DisabledPeriodicEvent extends Event {
    @Override
    public String getAction() {
        return "Runs periodically while robot is disabled";
    }
}
