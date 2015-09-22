package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.ftcrobotcontroller.SpinnerInputs;
import com.qualcomm.ftcrobotcontroller.SpinnerMotorPowerLevel;

/**
 * Created by 4924_Users on 9/20/2015.
 */
public class SpinnerPowerCalculator {
    public static SpinnerMotorPowerLevel Calculate(SpinnerInputs inputs) {
        SpinnerMotorPowerLevel level = new SpinnerMotorPowerLevel();

        level.power = inputs.throttle;

        return level;
    }
}
