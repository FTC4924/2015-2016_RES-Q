package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.robocol.Telemetry;

import java.util.List;

import static com.qualcomm.ftcrobotcontroller.opmodes.ColorSensorDriver.*;

/**
 * Created by 4924_Users on 9/25/2015.
 */
public class SensorTest extends OpMode {

    ColorSensor colorReader;
    TouchSensor touchSensor;
    OpticalDistanceSensor distanceSensor;
    DcMotor testMotor;

    @Override
    public void init() {
        colorReader = hardwareMap.colorSensor.get("ColorSensor");
        touchSensor = hardwareMap.touchSensor.get("TouchSensor");
        distanceSensor = hardwareMap.opticalDistanceSensor.get("DistanceSensor");
        distanceSensor.enableLed(true);
        testMotor = hardwareMap.dcMotor.get("TestMotor");
    }

    @Override
    public void loop() {

        Boolean advance1000 = false;
        Boolean advance2000 = false;

        advance1000 = gamepad1.a;
        advance1000 = gamepad1.b;



        String touch;

        if (touchSensor.isPressed())
            touch = "Pressed";
        else
            touch = "Not pressed";

        float red;
        float green;
        float blue;

        red = colorReader.red();
        green = colorReader.green();
        blue = colorReader.blue();

        telemetry.addData("Color1", "red:  " + String.format("%.2f", red));
        telemetry.addData("Color2", "green:  " + String.format("%.2f", green));
        telemetry.addData("Color3",  "blue: " + String.format("%.2f", blue));
        telemetry.addData("Light", "light: " + String.format("%.2f", distanceSensor.getLightDetected()));
        telemetry.addData("touch", "touch: " + touch);

    }
}
