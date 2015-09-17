package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by 4924_Users on 9/17/2015.
 */
public class DriverReader {
    public static DriverInputs GetDriverInputs(Gamepad gamepad1,Gamepad gamepad2){
        DriverInputs inputs = new DriverInputs();
        float x;
        float y;
        y = -gamepad1.left_stick_y;
        x = gamepad1.left_stick_x;

        inputs.throttle = y;
        inputs.direction = x;


        return inputs;
    }
}
