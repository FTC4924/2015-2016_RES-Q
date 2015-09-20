package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.robotcore.hardware.Gamepad;

import static com.qualcomm.ftcrobotcontroller.ServoInputs.FlapStates.FlapClosed;
import static com.qualcomm.ftcrobotcontroller.ServoInputs.PinStates.PinDown;

/**
 * Created by 4924_Users on 9/20/2015.
 */
public class ServoInputsReader {
    public static ServoInputs GetServoInputs(Gamepad gamepad1, Gamepad gamepad2) {
        ServoInputs inputs = new ServoInputs();

        // TODO Calculate servo states from inputs

        inputs.CurrentFlapState = FlapClosed;
        inputs.CurrentPinState = PinDown;

        return inputs;
    }
}
