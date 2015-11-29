package com.qualcomm.ftcrobotcontroller;

/**
 * Created by 4924_Users on 10/31/2015.
 */
public class LegacyDrivePathSegment extends DrivePathSegment {

    public float Time;
    public float LeftRearDistance;
    public float RightRearDistance;

    public LegacyDrivePathSegment() {}

    public LegacyDrivePathSegment(float left, float right, float power, float time) {
        LeftSideDistance = left;
        LeftRearDistance = left;
        RightSideDistance = right;
        RightRearDistance = right;
        Power = power;
        Time = time;
    }

    public LegacyDrivePathSegment(float leftFront, float leftRear,
                                  float rightFront, float rightRear,
                                  float power, float time) {
        LeftSideDistance = leftFront;
        LeftRearDistance = leftRear;
        RightSideDistance = rightFront;
        RightRearDistance = rightRear;
        Power = power;
        Time = time;
    }
}
