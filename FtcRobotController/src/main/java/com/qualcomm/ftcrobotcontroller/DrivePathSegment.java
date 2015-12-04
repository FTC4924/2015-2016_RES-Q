package com.qualcomm.ftcrobotcontroller;

/**
 * Created by 4924_Users on 10/18/2015.
 */
public class DrivePathSegment {
    public float LeftSideDistance;
    public float RightSideDistance;
    public float Angle;
    public float Power;
    public boolean isTurn = false;
    public boolean isDelay = false;
    public float delayTime;

    public DrivePathSegment() {}

    public DrivePathSegment(float left, float right, float power) {
        LeftSideDistance = left;
        RightSideDistance = right;
        Power = power;
        isTurn = false;
        isDelay = false;
    }

    public DrivePathSegment(float angle, float power) {
        Angle = angle;
        Power = power;
        isTurn = true;
        isDelay = false;
    }

    public DrivePathSegment(float timeDelay) {
        delayTime = timeDelay;
        isDelay = true;
    }
}
