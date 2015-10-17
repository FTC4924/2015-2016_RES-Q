package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.AutonomousArmInputs;
import com.qualcomm.ftcrobotcontroller.AutonomousArmPowerCalculate;
import com.qualcomm.ftcrobotcontroller.AutonomousArmPowerLevel;
import com.qualcomm.ftcrobotcontroller.AutonomousArmReader;
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
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by 4924_Users on 9/17/2015.
 */
public class SirHammerTeleop extends OpMode {
    DcMotor frontLeftMotor;
    DcMotor backLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backRightMotor;
    DcMotor leftMainArmMotor;
    DcMotor rightMainArmMotor;
    DcMotor spinnerMotor;
    DcMotor autoArmMotor;

    Servo pinServo;
    Servo flapServo;
    Servo kickStandServo;

    ServoAngles servoAngles = new ServoAngles();

    IrSeekerSensor irSensor;

    @Override
    public void init() {

        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        autoArmMotor = hardwareMap.dcMotor.get("autoArmMotor");
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.REVERSE);

        leftMainArmMotor = hardwareMap.dcMotor.get("leftMainArmMotor");
        rightMainArmMotor = hardwareMap.dcMotor.get("rightMainArmMotor");
        leftMainArmMotor.setDirection(DcMotor.Direction.REVERSE);

        spinnerMotor = hardwareMap.dcMotor.get("spinnerMotor");

        pinServo = hardwareMap.servo.get("pinServo");
        flapServo = hardwareMap.servo.get("flapServo");
        kickStandServo = hardwareMap.servo.get("kickStandArmServo");
        kickStandServo.setPosition(ServoAngleCalculator.KICKSTAND_DOCKED_ANGLE);

        irSensor = hardwareMap.irSeekerSensor.get("irSensor");
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
        ReadAndSetAutonomousArm();

        if (irSensor.signalDetected()) {
            telemetry.addData("angle", irSensor.getAngle());
            telemetry.addData("strength", irSensor.getStrength());
        }

        // do some telemetry
        DisplayTelemetry();
    }

    private void DisplayTelemetry() {
        telemetry.addData("Text", "*** Robot Data***");
        if (servoAngles.PinAngle== ServoAngleCalculator.PIN_UP_ANGLE)
            telemetry.addData("pin", ": UP");
        else
            telemetry.addData("pin", ": DN");
        if (servoAngles.KickStandAngle==ServoAngleCalculator.KICKSTAND_DOCKED_ANGLE)
            telemetry.addData("kick", ": DOCKED");
        else
            telemetry.addData("kick", ": EXTENDED");
        if (servoAngles.FlapAngle == ServoAngleCalculator.FLAP_OPEN_ANGLE)
            telemetry.addData("flap", ": OPEN");
        else
            telemetry.addData("flap", ": CLOSED");
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

    private void ReadAndSetAutonomousArm() {
        AutonomousArmInputs armInputs = AutonomousArmReader.GetAutoArmInputs(gamepad1, gamepad2);
        AutonomousArmPowerLevel armPowerLevel = AutonomousArmPowerCalculate.Calculate(armInputs);
        SetAutonomousArmPowerLevel(armPowerLevel);
    }

    private void ReadAndSetBucketArmMotors() {
        // raise/lower the bucket arm
        BucketArmMotorInputs bucketInputs = BucketArmReader.GetBucketArmInputs(gamepad1, gamepad2);
        BucketArmPowerLevel bucketLevels = BucketPowerCalculator.Calculate(bucketInputs);
        SetBucketArmPowerLevels(bucketLevels);
    }

    private void SetDriveMotorPowerLevels(FourWheelDrivePowerLevels levels) {
        frontLeftMotor.setPower(Range.clip(levels.frontLeft, -1.0, 1.0));
        backLeftMotor.setPower(Range.clip(levels.backLeft, -1.0, 1.0));
        backRightMotor.setPower(Range.clip(levels.backRight, -1.0, 1.0));
        frontRightMotor.setPower(Range.clip(levels.frontRight, -1.0, 1.0));
    }

    private void SetBucketArmPowerLevels(BucketArmPowerLevel levels) {
        leftMainArmMotor.setPower(levels.power);
        rightMainArmMotor.setPower(levels.power);
    }

    private void SetSpinnerMotorPowerLevel(SpinnerMotorPowerLevel level) {
        spinnerMotor.setPower(level.power);
    }

    private void SetAutonomousArmPowerLevel(AutonomousArmPowerLevel level) {
        autoArmMotor.setPower(level.power);
    }

    private void SetServoAngles(ServoAngles angles) {
        pinServo.setPosition(angles.PinAngle);
        kickStandServo.setPosition(angles.KickStandAngle);
        flapServo.setPosition(angles.FlapAngle);
    }
}
