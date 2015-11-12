package com.qualcomm.ftcrobotcontroller;

/**
 * Created by 4924_Users on 9/20/2015.
 */
public class SirHammerServoAngles {
    public float PinAngle;
    public float FlapAngle;
    public float KickStandAngle;
    public float BackLeftArmAngle;
    public float DunkingArmAngle;

    public SirHammerServoAngles() {
        PinAngle = SirHammerServoAngleCalculator.PIN_UP_ANGLE;
        FlapAngle = SirHammerServoAngleCalculator.FLAP_OPEN_ANGLE;
        KickStandAngle = SirHammerServoAngleCalculator.KICKSTAND_DOCKED_ANGLE;
        BackLeftArmAngle = SirHammerServoAngleCalculator.BACK_LEFT_ARM_RAISED_ANGLE;
        DunkingArmAngle = SirHammerServoAngleCalculator.DUNK_ARM_DOCKED_ANGLE;
    }
}
