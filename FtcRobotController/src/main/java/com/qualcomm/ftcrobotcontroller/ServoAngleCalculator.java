package com.qualcomm.ftcrobotcontroller;

/**
 * Created by 4924_Users on 9/20/2015.
 */
public class ServoAngleCalculator {

    public static final float FLAP_CLOSED_ANGLE = 170.0f;
    public static final float FLAP_OPEN_ANGLE = 80.0f;
    public static final float PIN_UP_ANGLE = 90.0f;
    public static final float PIN_DOWN_ANGLE = 180.0f;

    public static void UpdateServoAngles(ServoInputs inputs, ServoAngles servoAngles) {

        if (inputs.CloseFlap)
            servoAngles.FlapAngle = FLAP_CLOSED_ANGLE;
        if (inputs.OpenFlap)
            servoAngles.FlapAngle = FLAP_OPEN_ANGLE;
        if (inputs.LowerPin)
            servoAngles.PinAngle = PIN_DOWN_ANGLE;
        if (inputs.RaisePin)
            servoAngles.PinAngle = PIN_UP_ANGLE;

    }
}
