package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;

/**
 * Created by William on 2/24/2016.
 */
public class SharpIRTest extends OpMode {

    SharpIR10To150 irSensor;

    @Override
    public void init() {

        irSensor = new SharpIR10To150(hardwareMap.analogInput.get("irSensor"));
    }

    @Override
    public void loop() {

        telemetry.addData("Sharp IR Value: ", irSensor.getDistance());
    }
}
