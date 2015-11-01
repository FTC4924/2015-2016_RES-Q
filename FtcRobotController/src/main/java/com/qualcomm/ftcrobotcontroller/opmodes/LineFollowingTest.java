package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by 4924_Users on 10/31/2015.
 */
public class LineFollowingTest extends OpMode {

    public enum State {
        STATE_FOLLOW_LINE,
        STATE_STOP,
    }

    private FourWheelDrivePowerLevels zeroPowerLevels = new FourWheelDrivePowerLevels(0.0f, 0.0f);
    private ElapsedTime elapsedTimeForCurrentState = new ElapsedTime();
    final double WHITE_THRESHOLD = 0.5f;

    OpticalDistanceSensor lineDetector;
    DcMotor frontLeftMotor;
    DcMotor backLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backRightMotor;
    DcMotor arm;
    Servo servo1;
    Servo servo2;

    private State currentState;

    @Override
    public void init() {

        lineDetector = hardwareMap.opticalDistanceSensor.get("lineDetector");
        frontRightMotor = hardwareMap.dcMotor.get("frontrightMotor");
        backRightMotor = hardwareMap.dcMotor.get("backrightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontleftMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backleftMotor");
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void start() {

        SetCurrentState(State.STATE_FOLLOW_LINE);
    }

    @Override
    public void loop() {

        telemetry.addData("0", String.format("%4.1f ", elapsedTimeForCurrentState.time()) + currentState.toString());
        telemetry.addData("Has Beacon Been Reached: ", beaconIsReached());

        switch (currentState) {

            case STATE_FOLLOW_LINE:

                if (beaconIsReached()) {

                    TurnOffAllDriveMotors();
                    SetCurrentState(State.STATE_STOP);
                }

                if (isOnWhiteLine()) {

                    setPowerLevelsForLineFollowing(0.0f, 0.4f); //Left

                } else {

                    setPowerLevelsForLineFollowing(0.4f, 0.0f); //Right
                }

                break;

            case STATE_STOP:

                TurnOffAllDriveMotors();
        }
    }

    public void setPowerLevelsForLineFollowing(float leftPower, float rightPower) {

        frontLeftMotor.setPower(leftPower);
        backLeftMotor.setPower(leftPower);
        frontRightMotor.setPower(rightPower);
        backRightMotor.setPower(rightPower);
    }

    public boolean beaconIsReached() {
        //TODO Use an actual test for this
        return elapsedTimeForCurrentState.time() >= 2.0f;
    }

    private void TurnOffAllDriveMotors() {
        SetDriveMotorPowerLevels(zeroPowerLevels);
    }

    private void SetDriveMotorPowerLevels(FourWheelDrivePowerLevels levels) {

        frontRightMotor.setPower(levels.frontLeft);
        backRightMotor.setPower(levels.backLeft);
        frontLeftMotor.setPower(levels.backRight);
        backLeftMotor.setPower(levels.frontRight);
    }

    public void SetCurrentState(State newState) {
        elapsedTimeForCurrentState.reset();
        currentState = newState;
    }

    private boolean isOnWhiteLine() {
        return lineDetector.getLightDetected() > WHITE_THRESHOLD;
    }
}