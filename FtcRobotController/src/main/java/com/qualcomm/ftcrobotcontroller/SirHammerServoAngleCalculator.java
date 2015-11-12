package com.qualcomm.ftcrobotcontroller;

/**
 * Created by 4924_Users on 9/20/2015.
 */
public class SirHammerServoAngleCalculator {

    public static final float FLAP_CLOSED_ANGLE = 0.0f;
    public static final float FLAP_OPEN_ANGLE = 0.42f;
    public static final float PIN_UP_ANGLE = 0.075f;
    public static final float PIN_DOWN_ANGLE = 0.0f;
    public static final float KICKSTAND_DOCKED_ANGLE = 1.0f;
    public static final float KICKSTAND_EXTENDED_ANGLE = 0.0f;
    public static final float BACK_LEFT_ARM_LOWERED_ANGLE = 0.6f;
    public static final float BACK_LEFT_ARM_RAISED_ANGLE = 0.0f;
    public static final float DUNK_ARM_DOCKED_ANGLE = 0.7f;
    public static final float DUNK_ARM_DUNKING_ANGLE = 0.0f;

    public static void UpdateServoAngles(SirHammerServoInputs inputs, SirHammerServoAngles servoAngles) {

        if (inputs.CloseFlap)
            servoAngles.FlapAngle = FLAP_CLOSED_ANGLE;
        if (inputs.OpenFlap)
            servoAngles.FlapAngle = FLAP_OPEN_ANGLE;
        if (inputs.LowerPin)
            servoAngles.PinAngle = PIN_DOWN_ANGLE;
        if (inputs.RaisePin)
            servoAngles.PinAngle = PIN_UP_ANGLE;
        if (inputs.ExtendKickStand)
            servoAngles.KickStandAngle = KICKSTAND_EXTENDED_ANGLE;
        if (inputs.DockKickStand)
            servoAngles.KickStandAngle = KICKSTAND_DOCKED_ANGLE;
        if (inputs.LowerBackLeftArm)
            servoAngles.BackLeftArmAngle = BACK_LEFT_ARM_LOWERED_ANGLE;
        if (inputs.RaiseBackLeftArm)
            servoAngles.BackLeftArmAngle = BACK_LEFT_ARM_RAISED_ANGLE;
        if (inputs.RaiseDunkingArm)
            servoAngles.DunkingArmAngle = DUNK_ARM_DUNKING_ANGLE;
        if (inputs.LowerDunkingArm)
            servoAngles.DunkingArmAngle = DUNK_ARM_DOCKED_ANGLE;
    }
}
