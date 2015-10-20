package com.qualcomm.ftcrobotcontroller;

/**
 * Created by 4924_Users on 10/9/2015.
 */
public class AutonomousArmPowerCalculate {
    public static AutonomousArmPowerLevel Calculate(AutonomousArmInputs inputs) {
        AutonomousArmPowerLevel level = new AutonomousArmPowerLevel();
        level.power = inputs.throttle;
        return level;
    }
}
