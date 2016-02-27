package com.qualcomm.ftcrobotcontroller.opmodes;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

/**
 * Created by 4924_Users on 2/20/2016.
 */
public class ColorSensorTest extends OpMode {

    ColorSensor colorSensor;

    @Override
    public void init() {

        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        colorSensor.enableLed(false);
    }

    @Override
    public void loop() {

        telemetry.addData("Red: ", colorSensor.red());
        telemetry.addData("Blue: ", colorSensor.blue());
        telemetry.addData("Green: ", colorSensor.green());
    }
}
