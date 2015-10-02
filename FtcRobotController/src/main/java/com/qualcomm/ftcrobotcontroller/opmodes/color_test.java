package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

/**
 * Created by 4924_Users on 10/2/2015.
 */
public class color_test extends OpMode {
    ColorSensor colorSensor;
    @Override
    public void init() {
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
    }

    @Override
    public void loop() {
        int red_team;
        int blue_team;
        colorSensor.enableLed(false);
        red_team = colorSensor.red();
        blue_team = colorSensor.blue();
        telemetry.addData("red", "team:  " + red_team );
        telemetry.addData("blue", "team:  " + blue_team );
        telemetry.addData("Text", "*** Robot Data***");

    }
}
