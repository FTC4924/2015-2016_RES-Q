package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by 4924_Users on 10/9/2015.
 */
public class AutonomousArmReader {
    public static AutonomousArmInputs GetAutoArmInputs(Gamepad gamepad1, Gamepad gamepad2) {
        AutonomousArmInputs inputs = new AutonomousArmInputs();
        float throttle;
        throttle = 0.0f;
        if (gamepad1.a)
            throttle = 0.5f;
        if (gamepad1.y)
            throttle = -0.5f;
        inputs.throttle = throttle;
        return inputs;
    }

}
