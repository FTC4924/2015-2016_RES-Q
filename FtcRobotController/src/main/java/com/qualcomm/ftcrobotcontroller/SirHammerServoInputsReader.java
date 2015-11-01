package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by 4924_Users on 9/20/2015.
 */
public class SirHammerServoInputsReader {
    public static SirHammerServoInputs GetServoInputs(Gamepad gamepad1, Gamepad gamepad2) {
        SirHammerServoInputs inputs = new SirHammerServoInputs();

        inputs.RaisePin = gamepad2.y;
        inputs.LowerPin = gamepad2.a;

        inputs.OpenFlap = gamepad2.left_bumper;
        inputs.CloseFlap = gamepad2.right_bumper;

        inputs.DockKickStand = gamepad2.x;
        inputs.ExtendKickStand = gamepad2.b;

        inputs.LowerBackLeftArm = gamepad2.right_bumper;
        inputs.RaiseBackLeftArm = gamepad2.left_bumper;

        return inputs;
    }
}
