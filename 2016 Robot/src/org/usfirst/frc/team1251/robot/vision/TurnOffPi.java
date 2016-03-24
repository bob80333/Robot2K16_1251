package org.usfirst.frc.team1251.robot.vision;

import edu.wpi.first.wpilibj.command.Command;

import java.io.IOException;

/**
 * Created by Eric on 3/24/2016.
 */
public class TurnOffPi extends Command {
    private boolean isDone = true;
    @Override
    protected void initialize() {
    }

    @Override
    protected void execute() {
        isDone = false;
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("ssh -l ubuntu -w ubuntu@ubuntu.lan sudo shutdown 0");
        } catch (IOException e) {
            e.printStackTrace();
            //shit
        }
        isDone = true;
    }

    @Override
    protected boolean isFinished() {
        return isDone;
    }

    @Override
    protected void end() {
        isDone = false;
    }

    @Override
    protected void interrupted() {
        isDone = false;
        //really?
    }
}
