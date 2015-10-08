package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.ftcrobotcontroller.SpinnerInputs;
import com.qualcomm.ftcrobotcontroller.SpinnerMotorPowerLevel;

/**
 * Created by 4924_Users on 9/20/2015.
 */
public class SpinnerPowerCalculator {
    public static SpinnerMotorPowerLevel Calculate(SpinnerInputs inputs) {
        SpinnerMotorPowerLevel level = new SpinnerMotorPowerLevel();

        if (inputs.throttle > 0.5) {
            level.power = 1.0f;
        } else {
            level.power = 0.25f;
        }

        return level;
    }
}
