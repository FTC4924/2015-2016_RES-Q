package com.qualcomm.ftcrobotcontroller;

/**
 * Created by 4924_Users on 11/3/2015.
 */
public class SirHammerEncoderTargets {
    public int LeftFrontTarget;
    public int LeftRearTarget;
    public int RightFrontTarget;
    public int RightRearTarget;

    public SirHammerEncoderTargets(int left, int right) {
        LeftFrontTarget = left;
        LeftRearTarget = left;
        RightFrontTarget = right;
        RightRearTarget = right;
    }

    public SirHammerEncoderTargets(int leftFront, int leftRear, int rightFront, int rightRear) {
        LeftFrontTarget = leftFront;
        LeftRearTarget = leftRear;
        RightFrontTarget = rightFront;
        RightRearTarget = rightRear;
    }
}
