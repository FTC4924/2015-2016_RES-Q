package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
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
    private OpticalDistanceSensor lineDetector;
    private ElapsedTime elapsedTimeForCurrentState = new ElapsedTime();
    final double WHITE_THRESHOLD = 0.5f;

    DcMotor frontLeftMotor;
    DcMotor backLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backRightMotor;

    private State currentState;

    @Override
    public void init() {

        frontRightMotor = hardwareMap.dcMotor.get("frontrightMotor");
        backRightMotor = hardwareMap.dcMotor.get("backrightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontleftMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backleftMotor");
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {

        switch (currentState) {

            case STATE_FOLLOW_LINE:

                if (beaconIsReached()) {

                    TurnOffAllDriveMotors();
                    SetCurrentState(State.STATE_STOP);
                }

                if (isOnWhiteLine()) {

                    setPowerLevelsForLineFollowing(0.0f, 0.3f);

                } else {

                    setPowerLevelsForLineFollowing(0.3f, 0.0f);
                    /*telemetry.addData("1", String.format("%4.2f of %4.2f ",
                            lineDetector.getLightDetected(),
                            WHITE_THRESHOLD ));*/
                }

                break;

            case STATE_STOP:

                //if (pathComplete()) {

                // TurnOffAllDriveMotors();
                // telemetry.addData("StopComplete", "Stop Complete");
                //}

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
