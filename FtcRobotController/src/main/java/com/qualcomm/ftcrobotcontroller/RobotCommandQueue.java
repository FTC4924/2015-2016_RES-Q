package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.robotcore.robocol.Command;

import java.util.List;

/**
 * Created by 4924_Users on 10/3/2015.
 */
public class RobotCommandQueue {
    private List<RobotCommand> CommandQueue;
    private RobotCommand currentCommand;

    public void Execute() {
        if (currentCommand == null)
            currentCommand = CommandQueue.get(0);
        if (currentCommand.IsFinished())
            currentCommand = currentCommand.NextCommand;
        currentCommand.Execute();
    }
}
