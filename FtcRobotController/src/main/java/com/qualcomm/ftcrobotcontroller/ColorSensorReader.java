package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by 4924_Users on 10/3/2015.
 */
public class ColorSensorReader {
    public static ColorValue GetColorValue(ColorSensor colorSensor) {
        ColorValue inputs = new ColorValue();
        inputs.blue_team = colorSensor.blue();
        inputs.red_team = colorSensor.red();
        return inputs;
    }
}
