package com.qualcomm.ftcrobotcontroller;

/**
 * Created by 4924_Users on 10/18/2015.
 */
public class DrivePathSegment {
    public float LeftSideDistance;
    public float RightSideDistance;
    public float Angle;
    public float leftPower;
    public float rightPower;
    public float delayTime;
    public boolean isTurn = false;
    public boolean isDelay = false;

    public DrivePathSegment() {}

    public DrivePathSegment(float left, float right, float power) {
        LeftSideDistance = left;
        RightSideDistance = right;
        leftPower = power;
        rightPower = power;
        isTurn = false;
        isDelay = false;
    }

    public DrivePathSegment(float angle, float power) {
        Angle = angle;
        leftPower = power;
        rightPower = power;
        isTurn = true;
        isDelay = false;
    }

    public DrivePathSegment(float timeDelay) {
        delayTime = timeDelay;
        isDelay = true;
    }
}
