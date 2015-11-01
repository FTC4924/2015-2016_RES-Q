package com.qualcomm.ftcrobotcontroller;

/**
 * Created by 4924_Users on 10/18/2015.
 */
public class DrivePathSegment {
    public float LeftSideDistance;
    public float RightSideDistance;
    public float Power;

    public DrivePathSegment() {}

    public DrivePathSegment(float left, float right, float power) {
        LeftSideDistance = left;
        RightSideDistance = right;
        Power = power;
    }
}
