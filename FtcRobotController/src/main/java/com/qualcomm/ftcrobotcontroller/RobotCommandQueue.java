package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.ftcrobotcontroller.RobotCommands.RobotCommand;

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

    public String CurrentCommandDescription() {
        if (currentCommand == null)
            return "";
        return currentCommand.Description;
    }
}
