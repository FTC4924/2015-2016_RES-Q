package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by 4924_Users on 9/20/2015.
 */
public class SpinnerReader {
    public static SpinnerInputs GetSpinnerInputs(Gamepad gamepad1, Gamepad gamepad2) {
        SpinnerInputs inputs = new SpinnerInputs();
        float throttle;

        throttle = gamepad2.right_trigger - gamepad2.left_trigger;
        inputs.throttle = throttle;

        return inputs;
    }
}
