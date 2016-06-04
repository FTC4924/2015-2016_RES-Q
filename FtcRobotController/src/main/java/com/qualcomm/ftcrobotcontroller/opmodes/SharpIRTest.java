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

        irSensor = new SharpIR10To150(hardwareMap.analogInput.get("sharpIR"));
    }

    @Override
    public void loop() {

        telemetry.addData("Sharp IR Distance: ", irSensor.getDistance());
        telemetry.addData("Sharp IR Value: ", irSensor.getRawDistance());
        telemetry.addData("Is Path Blocked", pathIsBlocked());
    }

    private boolean pathIsBlocked() {

        return irSensor.getDistance() <= 50.0f;
    }
}
