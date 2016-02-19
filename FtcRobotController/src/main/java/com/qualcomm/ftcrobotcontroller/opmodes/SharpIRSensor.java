package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.HardwareDevice;

/**
 * Created by 4924_Users on 2/19/2016.
 */
public abstract class SharpIRSensor implements HardwareDevice {

    public abstract double getDistance();

    public abstract int getRawDistance();

    @Override
    public String getDeviceName() {
        return null;
    }

    @Override
    public String getConnectionInfo() {
        return null;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void close() {

    }
}
