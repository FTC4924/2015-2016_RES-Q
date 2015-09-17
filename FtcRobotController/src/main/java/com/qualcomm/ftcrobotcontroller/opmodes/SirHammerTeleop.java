package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DriverInputs;
import com.qualcomm.ftcrobotcontroller.DriverReader;
import com.qualcomm.ftcrobotcontroller.MotorPwerLvl;
import com.qualcomm.ftcrobotcontroller.PowerCal;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by 4924_Users on 9/17/2015.
 */
public class SirHammerTeleop extends OpMode {
    DcMotor frontLeftMotor;
    DcMotor backLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backRightMotor;

    @Override
    public void init() {

    }

    @Override
    public void loop() {
        // drive the motors
        DriverInputs inputs = DriverReader.GetDriverInputs(gamepad1, gamepad2);
        MotorPwerLvl levels = PowerCal.CalcPwerLvls(inputs);
        SetMotorPowerLevels(levels);

        // control the bucket arm
    }

    private void SetMotorPowerLevels(MotorPwerLvl levels) {
        frontLeftMotor.setPower(levels.frontLeft);
        backLeftMotor.setPower(levels.backLeft);
        backRightMotor.setPower(levels.backRight);
        frontRightMotor.setPower(levels.frontRight);
    }
}
