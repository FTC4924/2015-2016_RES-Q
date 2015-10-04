package com.qualcomm.ftcrobotcontroller.RobotCommands;

import com.qualcomm.ftcrobotcontroller.RobotCommands.RobotCommand;

/**
 * Created by 4924_Users on 10/3/2015.
 */
public class MoveCommandWithEncoders extends RobotCommand {
    private double moveDistanceInInches;
    static final int encoderCount = 1000;
    static final double wheelRadiusInInches = 3.0;

    public MoveCommandWithEncoders(double distance) {
        moveDistanceInInches = distance;
    }

     @Override
    public void Execute() {
        if (!started) {
            // build and send encoder command
            int encoderCountNeeded = EncoderCountForDistance();

        }
    }

    @Override
    public boolean IsFinished() {
        return moveIsComplete();
    }

    private boolean moveIsComplete() {
        // read encoder to see if count has been reached
        return false;
    }

    private int EncoderCountForDistance() {
        double circumference = 2.0 * Math.PI * wheelRadiusInInches;
        double revolutionsRequiredForDistance = moveDistanceInInches / circumference;
        int encoderCountRequiredForDistance = (int)(revolutionsRequiredForDistance * encoderCount);

        return encoderCountRequiredForDistance;
    }
}
