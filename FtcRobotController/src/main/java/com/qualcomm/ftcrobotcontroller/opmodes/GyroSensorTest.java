package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * Created by 4924_Users on 11/29/2015.
 */
public class GyroSensorTest extends OpMode {

    GyroSensor gyroSensor;
    int xVal, yVal, zVal = 0;
    int heading = 0;

    @Override
    public void init() {

        gyroSensor = hardwareMap.gyroSensor.get("gyro");
        gyroSensor.calibrate();
    }

    @Override
    public void loop() {
        // get the x, y, and z values (rate of change of angle).
        xVal = gyroSensor.rawX();
        yVal = gyroSensor.rawY();
        zVal = gyroSensor.rawZ();

        // get the heading info.
        // the Modern Robotics' gyro sensor keeps
        // track of the current heading for the Z axis only.
        heading = gyroSensor.getHeading();

        telemetry.addData("1. x", String.format("%03d", xVal));
        telemetry.addData("2. y", String.format("%03d", yVal));
        telemetry.addData("3. z", String.format("%03d", zVal));
        telemetry.addData("4. h", String.format("%03d", heading));
    }
}
