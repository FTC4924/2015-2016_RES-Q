package com.qualcomm.ftcrobotcontroller.RobotCommands;

import com.qualcomm.ftcrobotcontroller.RobotCommands.RobotCommand;

/**
 * Created by 4924_Users on 10/3/2015.
 */
public class TurnCommand extends RobotCommand {
    private double turnAngleInDegrees;   // positive means clockwise, negative counter-clockwise

    public TurnCommand(double angle) {
        turnAngleInDegrees = angle;
    }

    @Override
    public void Execute() {
        if (!started) {
            // build and send a movement command
        }
    }

    @Override
    public boolean IsFinished() {
        return MoveIsFinished();
    }

    private boolean MoveIsFinished() {
        return false;
    }
}
