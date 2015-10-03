package com.qualcomm.ftcrobotcontroller;

/**
 * Created by 4924_Users on 9/17/2015.
 */
public class FourWheelDrivePowerLevels {
    public float frontLeft;
    public float frontRight;
    public float backLeft;
    public float backRight;

    public FourWheelDrivePowerLevels() {}

    public FourWheelDrivePowerLevels(float frontLeft, float frontRight, float backLeft, float backRight) {
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
    }

    public FourWheelDrivePowerLevels(float left, float right) {
        this.frontLeft = left;
        this.backLeft = left;
        this.frontRight = right;
        this.backRight = right;
    }
}
