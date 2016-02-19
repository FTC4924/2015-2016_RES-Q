package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.hardware.ModernRoboticsUsbDeviceInterfaceModule;

/**
 * Created by 4924_Users on 2/19/2016.
 */
public class SharpIR10To150 extends SharpIRSensor {

    private final ModernRoboticsUsbDeviceInterfaceModule USB_MODULE;
    private final int MODULE_PORT;
    private final int MINIMUM_DISTANCE = 10;
    private final int MAXIMUM_DISTANCE = 150;
    private final double CENTIMETER_CONVERSION_RATE = 1023 / (MAXIMUM_DISTANCE - MINIMUM_DISTANCE);

    public SharpIR10To150(ModernRoboticsUsbDeviceInterfaceModule deviceInterfaceModule, int physicalPort) {

        USB_MODULE = deviceInterfaceModule;
        MODULE_PORT = physicalPort;
    }

    @Override
    public double getDistance() {

        return (double) MINIMUM_DISTANCE + this.USB_MODULE.getAnalogInputValue(this.MODULE_PORT) / CENTIMETER_CONVERSION_RATE;
    }

    @Override
    public int getRawDistance() {

        return this.USB_MODULE.getAnalogInputValue(this.MODULE_PORT);
    }
}