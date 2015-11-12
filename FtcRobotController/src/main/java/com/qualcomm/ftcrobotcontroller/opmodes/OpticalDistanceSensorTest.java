package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

/**
 * Created by 4924_Users on 10/25/2015.
 */
public class OpticalDistanceSensorTest extends OpMode {

    OpticalDistanceSensor lineDetector;

    final double WHITE_THRESHOLD = 0.5;

    @Override
    public void init() {

        lineDetector = hardwareMap.opticalDistanceSensor.get("lineDetector");
    }

    @Override
    public void loop() {

        telemetry.addData("1", String.format("%4.2f of %4.2f ",
                lineDetector.getLightDetected(),
                WHITE_THRESHOLD));
    }
}
