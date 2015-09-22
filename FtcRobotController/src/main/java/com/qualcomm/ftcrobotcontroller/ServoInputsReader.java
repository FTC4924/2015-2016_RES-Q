package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by 4924_Users on 9/20/2015.
 */
public class ServoInputsReader {
    public static ServoInputs GetServoInputs(Gamepad gamepad1, Gamepad gamepad2) {
        ServoInputs inputs = new ServoInputs();

        inputs.RaisePin = gamepad2.start;
        inputs.LowerPin = gamepad2.back;

        inputs.OpenFlap = gamepad2.left_bumper;
        inputs.CloseFlap = gamepad2.right_bumper;

        return inputs;
    }
}
