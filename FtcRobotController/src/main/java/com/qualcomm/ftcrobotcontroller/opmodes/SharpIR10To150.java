package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.AnalogInput;

/**
 * Created by 4924_Users on 2/19/2016.
 */
public class SharpIR10To150 extends SharpIRSensor {

    private AnalogInput input;
    private final double MINIMUM_DISTANCE = 20.0d;
    private final double MAXIMUM_DISTANCE = 100.0d;
    private final double MINIMUM_VALUE = 100.0d;
    private final double MAXIMUM_VALUE = 361.0d;
    private final double CALIBRATION_FACTOR = 1.3d;
    private final double CENTIMETER_CONVERSION_RATE = (MAXIMUM_DISTANCE - MINIMUM_DISTANCE) / (MAXIMUM_VALUE - MINIMUM_VALUE);

    public SharpIR10To150(AnalogInput analogInput) {

        input = analogInput;
    }

    @Override
    public double getDistance() {

        double reversedValue = (double) input.getValue() * CENTIMETER_CONVERSION_RATE;

        return (MAXIMUM_DISTANCE - reversedValue) + MINIMUM_DISTANCE;
    }

    @Override
    public int getRawDistance() {

        return input.getValue();
    }
}