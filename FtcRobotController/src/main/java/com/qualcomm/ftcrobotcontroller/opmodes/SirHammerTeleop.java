package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.BucketArmMotorInputs;
import com.qualcomm.ftcrobotcontroller.BucketArmPowerLevel;
import com.qualcomm.ftcrobotcontroller.BucketArmReader;
import com.qualcomm.ftcrobotcontroller.BucketPowerCalculator;
import com.qualcomm.ftcrobotcontroller.DriverArcadeInputs;
import com.qualcomm.ftcrobotcontroller.DriverReader;
import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.ftcrobotcontroller.DriveMotorPowerCalculator;
import com.qualcomm.ftcrobotcontroller.ServoAngleCalculator;
import com.qualcomm.ftcrobotcontroller.ServoAngles;
import com.qualcomm.ftcrobotcontroller.ServoInputs;
import com.qualcomm.ftcrobotcontroller.ServoInputsReader;
import com.qualcomm.ftcrobotcontroller.SpinnerInputs;
import com.qualcomm.ftcrobotcontroller.SpinnerMotorPowerLevel;
import com.qualcomm.ftcrobotcontroller.SpinnerPowerCalculator;
import com.qualcomm.ftcrobotcontroller.SpinnerReader;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by 4924_Users on 9/17/2015.
 */
public class SirHammerTeleop extends OpMode {
    DcMotor frontLeftMotor;
    DcMotor backLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backRightMotor;
    DcMotor leftBucketMotor;
    DcMotor rightBucketMotor;
    DcMotor spinnerMotor;

    Servo pinServo;
    Servo flapServo;

    ServoAngles servoAngles = new ServoAngles();

    @Override
    public void init() {

        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.REVERSE);

        pinServo = hardwareMap.servo.get("pinServo");
        flapServo = hardwareMap.servo.get("flapServo");
    }

    @Override
    public void loop() {

        // drive the motors
        DriverArcadeInputs inputs = DriverReader.GetDriverArcadeInputs(gamepad1, gamepad2);
        FourWheelDrivePowerLevels levels = DriveMotorPowerCalculator.CalculatePowerForArcadeInputs(inputs);
        SetDriveMotorPowerLevels(levels);

        ReadAndSetBucketArmMotors();
        ReadAndSetSpinnerMotor();
        ReadAndSetServos();

        // do some telemetry
        telemetry.addData("Text", "*** Robot Data***");
        if (servoAngles.PinAngle==ServoAngleCalculator.PIN_UP_ANGLE)
            telemetry.addData("pin", "pin:  UP");
        else
            telemetry.addData("pin", "pin: DN");
        //telemetry.addData("drivepower", String.format("pwr: %1|%2|%3|%4" + levels.frontLeft) );
    }

    private void ReadAndSetServos() {
        // raise/lower the pin, the flaps, run the sweeper???
        // we have to keep a variable with current servo angles, since we want to keep the
        // same angle if no inputs that cause us to change servos happen
        ServoInputs servoInputs = ServoInputsReader.GetServoInputs(gamepad1, gamepad2);
        ServoAngleCalculator.UpdateServoAngles(servoInputs, servoAngles);
        SetServoAngles(servoAngles);
    }

    private void ReadAndSetSpinnerMotor() {
        // turn the spinner
        SpinnerInputs spinnerInputs = SpinnerReader.GetSpinnerInputs(gamepad1, gamepad2);
        SpinnerMotorPowerLevel spinnerMotorPowerLevel = SpinnerPowerCalculator.Calculate(spinnerInputs);
        SetSpinnerMotorPowerLevel(spinnerMotorPowerLevel);
    }

    private void ReadAndSetDriveMotors() {
    }

    private void ReadAndSetBucketArmMotors() {
        // raise/lower the bucket arm
        BucketArmMotorInputs bucketInputs = BucketArmReader.GetBucketArmInputs(gamepad1, gamepad2);
        BucketArmPowerLevel bucketLevels = BucketPowerCalculator.Calculate(bucketInputs);
        SetBucketArmPowerLevels(bucketLevels);
    }

    private void SetDriveMotorPowerLevels(FourWheelDrivePowerLevels levels) {
        frontLeftMotor.setPower(levels.frontLeft);
        backLeftMotor.setPower(levels.backLeft);
        backRightMotor.setPower(levels.backRight);
        frontRightMotor.setPower(levels.frontRight);
    }

    private void SetBucketArmPowerLevels(BucketArmPowerLevel levels) {
        leftBucketMotor.setPower(levels.power);
        rightBucketMotor.setPower(levels.power);
    }

    private void SetSpinnerMotorPowerLevel(SpinnerMotorPowerLevel level) {
        spinnerMotor.setPower(level.power);
    }

    private void SetServoAngles(ServoAngles angles) {
        pinServo.setPosition(angles.PinAngle);
        flapServo.setPosition(angles.FlapAngle);
    }
}
