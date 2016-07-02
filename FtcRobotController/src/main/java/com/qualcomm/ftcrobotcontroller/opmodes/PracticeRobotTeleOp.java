package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * Created by 4924_Users on 6/3/2016.
 */
public class PracticeRobotTeleOp extends OpMode {

    DcMotor frontLeftMotor;
    DcMotor backLeftMotor;
    DcMotor backRightMotor;
    DcMotor frontRightMotor;
    GyroSensor Gyro;
    PracticeRobotPowerLevels PowerLevels = new PracticeRobotPowerLevels();

    final float CURVE_POWER_ADJUSTMENT = 0.2f;
    final float GYRO_ANGLE_MARGINE = 1.0f;
    final float BASE_HOLONOMIC_DRIVE_POWER = 0.5f;

    float steadyAngle = 0.0f;
    boolean isStrafingLeft = false;
    boolean isStrafingRight = false;

    @Override
    public void init() {

        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        Gyro = hardwareMap.gyroSensor.get("Gyro");

        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
        backRightMotor.setDirection(DcMotor.Direction.FORWARD);
        Gyro.calibrate();
    }

    @Override
    public void loop() {

        isStrafingLeft = gamepad1.left_bumper;
        isStrafingRight = gamepad1.right_bumper;

        if (isStrafingLeft || isStrafingRight) {

            setPowerForMecanumStrafe();

        } else {

            setPowerForTankDrive();
            steadyAngle = Gyro.getHeading();
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

            PowerLevels.frontLeftPower = BASE_HOLONOMIC_DRIVE_POWER;
            PowerLevels.backLeftPower = -BASE_HOLONOMIC_DRIVE_POWER;
            PowerLevels.backRightPower = BASE_HOLONOMIC_DRIVE_POWER;
            PowerLevels.frontRightPower = -BASE_HOLONOMIC_DRIVE_POWER;

            adjustPowerForHolonomicCurve();
        }

        if (isStrafingRight) {

            PowerLevels.frontLeftPower = -BASE_HOLONOMIC_DRIVE_POWER;
            PowerLevels.backLeftPower = BASE_HOLONOMIC_DRIVE_POWER;
            PowerLevels.backRightPower = -BASE_HOLONOMIC_DRIVE_POWER;
            PowerLevels.frontRightPower = BASE_HOLONOMIC_DRIVE_POWER;

            adjustPowerForHolonomicCurve();
        }
    }

    public void adjustPowerForHolonomicCurve() {

        if (isStrafingLeft) {

            if (Gyro.getHeading() < steadyAngle - GYRO_ANGLE_MARGINE) {

                PowerLevels.backLeftPower -= CURVE_POWER_ADJUSTMENT;
            }

            if (Gyro.getHeading() > steadyAngle + GYRO_ANGLE_MARGINE) {

                PowerLevels.frontLeftPower += CURVE_POWER_ADJUSTMENT;
            }
        }

        if (isStrafingRight) {

            if (Gyro.getHeading() < steadyAngle - GYRO_ANGLE_MARGINE) {

                PowerLevels.frontRightPower += CURVE_POWER_ADJUSTMENT;
            }

            if (Gyro.getHeading() > steadyAngle + GYRO_ANGLE_MARGINE) {

                PowerLevels.backRightPower -= CURVE_POWER_ADJUSTMENT;
            }
        }
    }
}