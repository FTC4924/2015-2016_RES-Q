package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by 4924_Users on 9/20/2015.
 */
public class BucketArmReader {
    public static BucketArmMotorInputs GetBucketArmInputs(Gamepad gamepad1, Gamepad gamepad2) {
        BucketArmMotorInputs inputs = new BucketArmMotorInputs();
        inputs.throttle = -gamepad2.left_stick_y;
        return inputs;
    }
}
