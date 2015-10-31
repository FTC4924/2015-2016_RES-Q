package com.qualcomm.ftcrobotcontroller;

/**
 * Created by 4924_Users on 10/31/2015.
 */
public class LegacyDrivePathSegment extends DrivePathSegment {

    public float Time;

    public LegacyDrivePathSegment(float left, float right, float power, float time) {
        LeftSideDistance = left;
        RightSideDistance = right;
        Power = power;
        Time = time;
    }
}
