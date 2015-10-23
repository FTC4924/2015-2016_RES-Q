package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by 4924_Users on 9/17/2015.
 */
public class DriverReader {
    public static DriverArcadeInputs GetDriverArcadeInputs(Gamepad gamepad1, Gamepad gamepad2){
        DriverArcadeInputs inputs = new DriverArcadeInputs();
        float x;
        float y;
        y = -gamepad1.left_stick_y;
        x = gamepad1.left_stick_x;

        inputs.throttle = y;
        inputs.direction = x;

        return inputs;
    }

    public static DriverTankDriveInputs GetDriverTankInputs(Gamepad gamepad1, Gamepad gamepad2) {
        DriverTankDriveInputs inputs = new DriverTankDriveInputs();
        float left;
        float right;
        float accelerator;

        left = -gamepad1.left_stick_y;
        right = -gamepad1.right_stick_y;
        accelerator = gamepad1.right_trigger;

        inputs.left = left;
        inputs.right = right;
        inputs.accelerator = accelerator;

        return inputs;
    }
}
