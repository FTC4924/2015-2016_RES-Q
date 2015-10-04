package com.qualcomm.ftcrobotcontroller.RobotCommands;

/**
 * Created by 4924_Users on 10/3/2015.
 */
public class RobotCommand {
    public String Description;
    public RobotCommand NextCommand;
    public void Execute() {}
    public boolean IsFinished() {return false;}
    public boolean started;
}
