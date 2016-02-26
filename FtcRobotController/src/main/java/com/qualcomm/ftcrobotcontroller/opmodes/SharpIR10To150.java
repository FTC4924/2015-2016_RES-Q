package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogInputController;
import com.qualcomm.robotcore.hardware.I2cController;

/**
 * Created by 4924_Users on 2/19/2016.
 */
public class SharpIR10To150 extends SharpIRSensor {

    private AnalogInput input;
    private final int MINIMUM_DISTANCE_CM = 0;
    private final int MAXIMUM_DISTANCE_CM = 30;
    private final int MINIMUM_DISTANCE = 0;
    private final int MAXIMUM_DISTANCE = 1023;
    private final double CALIBRATION_FACTOR = 1.3d;
    private final double CENTIMETER_CONVERSION_RATE = (MAXIMUM_DISTANCE - MINIMUM_DISTANCE) / (MAXIMUM_DISTANCE_CM - MINIMUM_DISTANCE_CM);

    public SharpIR10To150(AnalogInput analogInput) {

        input = analogInput;
    }

    @Override
    public double getDistance() {

        int reversedScaleDistance = MAXIMUM_DISTANCE - (input.getValue() - MINIMUM_DISTANCE);

        return (double) reversedScaleDistance / CENTIMETER_CONVERSION_RATE * CALIBRATION_FACTOR;
    }

    @Override
    public int getRawDistance() {

        return input.getValue();
    }
}