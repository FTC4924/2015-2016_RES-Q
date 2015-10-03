package com.qualcomm.ftcrobotcontroller;

/**
 * Created by 4924_Users on 9/20/2015.
 */
public class ServoAngles {
    public float PinAngle;
    public float FlapAngle;

    public ServoAngles() {
        PinAngle = ServoAngleCalculator.PIN_UP_ANGLE;
        FlapAngle = ServoAngleCalculator.FLAP_CLOSED_ANGLE;
    }
}
