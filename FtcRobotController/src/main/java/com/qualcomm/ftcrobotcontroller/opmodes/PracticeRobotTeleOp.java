package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by 4924_Users on 6/3/2016.
 */
public class PracticeRobotTeleOp extends OpMode {

    DcMotor frontLeftMotor;
    DcMotor backLeftMotor;
    DcMotor backRightMotor;
    DcMotor frontRightMotor;
    PracticeRobotPowerLevels PowerLevels = new PracticeRobotPowerLevels();

    boolean isStrafingLeft = false;
    boolean isStrafingRight = false;

    @Override
    public void init() {

        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");

        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {

        isStrafingLeft = gamepad1.left_bumper;
        isStrafingRight = gamepad1.right_bumper;

        if (isStrafingLeft || isStrafingRight) {

            setPowerForMecanumStrafe();

        } else {

            setPowerForTankDrive();
        }

        setMotorPowerLevels(PowerLevels);
    }

    public void setMotorPowerLevels(PracticeRobotPowerLevels PowerLevels) {

        frontLeftMotor.setPower(PowerLevels.frontLeftPower);
        backLeftMotor.setPower(PowerLevels.backLeftPower);
        backRightMotor.setPower(PowerLevels.backRightPower);
        frontRightMotor.setPower(PowerLevels.frontRightPower);
    }

    public void setPowerForTankDrive() {

        PowerLevels.frontLeftPower = gamepad1.left_stick_y;
        PowerLevels.backLeftPower = gamepad1.left_stick_y;
        PowerLevels.backRightPower = gamepad1.right_stick_y;
        PowerLevels.frontRightPower = gamepad1.right_stick_y;
    }

    public void setPowerForMecanumStrafe() {

        if (isStrafingLeft) {

            PowerLevels.frontLeftPower = 0.5f;
            PowerLevels.backLeftPower = -0.5f;
            PowerLevels.backRightPower = 0.5f;
            PowerLevels.frontRightPower = -0.5f;
        }

        if (isStrafingRight) {

            PowerLevels.frontLeftPower = -0.5f;
            PowerLevels.backLeftPower = 0.5f;
            PowerLevels.backRightPower = -0.5f;
            PowerLevels.frontRightPower = 0.5f;
        }
    }
}