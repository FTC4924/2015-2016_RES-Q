package com.qualcomm.ftcrobotcontroller;

/**
 * Created by 4924_Users on 9/20/2015.
 */
public class BucketPowerCalculator {
    public static BucketArmPowerLevel Calculate(BucketArmMotorInputs inputs) {
        BucketArmPowerLevel levels = new BucketArmPowerLevel();
        float power = BucketArmReader.GetBucketArmInputs();
        //TODO Ask about how to pass the gamepads
        levels.power = power;

        return levels;
    }
}
