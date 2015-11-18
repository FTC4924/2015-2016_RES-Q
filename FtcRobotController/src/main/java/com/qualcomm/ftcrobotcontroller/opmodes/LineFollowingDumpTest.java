package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by 4924_Users on 11/16/2015.
 */
public class LineFollowingDumpTest extends OpMode {

    public enum State {
        STATE_INITIAL,
        STATE_FOLLOW_LINE,
        STATE_DEPLOY_CLIMBERS,
        STATE_STOP
    }

    DcMotor frontLeftMotor;
    DcMotor frontRightMotor;
    Servo climberDeployer;
    OpticalDistanceSensor lineDetector;
    TouchSensor bumper;

    final double WHITE_THRESHOLD = 0.1f;

    private State currentState;
    private ElapsedTime elapsedTimeForCurrentState = new ElapsedTime();
    private FourWheelDrivePowerLevels zeroPowerLevels = new FourWheelDrivePowerLevels(0.0f, 0.0f);

    @Override
    public void init() {

        frontRightMotor = hardwareMap.dcMotor.get("frontrightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontleftMotor");
        climberDeployer = hardwareMap.servo.get("servo5");
        lineDetector = hardwareMap.opticalDistanceSensor.get("lineDetector");
        bumper = hardwareMap.touchSensor.get("bumper");
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        climberDeployer.setPosition(0.0d);

        SetCurrentState(State.STATE_INITIAL);
    }

    @Override
    public void loop() {

        switch (currentState) {

            case STATE_INITIAL:

                SetCurrentState(State.STATE_FOLLOW_LINE);

                break;

            case STATE_FOLLOW_LINE:

                if (beaconIsReached()) {

                    TurnOffAllDriveMotors();
                    SetCurrentState(State.STATE_DEPLOY_CLIMBERS);
                }

                if (isOnWhiteLine()) {

                    setPowerLevelsForLineFollowing(0.35f, 0.0f);

                } else {

                    setPowerLevelsForLineFollowing(0.0f, 0.35f);
                }

                break;

            case STATE_DEPLOY_CLIMBERS:

                if (climbersHaveBeenDeployed()) {

                    climberDeployer.setPosition(0.0d);
                    SetCurrentState(State.STATE_STOP);

                } else {

                    climberDeployer.setPosition(1.0d);
                }

                break;

            case STATE_STOP:

                break;
        }
    }

    public void SetCurrentState(State newState) {

        elapsedTimeForCurrentState.reset();
        currentState = newState;
    }

    public boolean beaconIsReached() {
        return bumper.isPressed();
    }

    public void setPowerLevelsForLineFollowing(float leftPower, float rightPower) {

        frontLeftMotor.setPower(leftPower);
        frontRightMotor.setPower(rightPower);
    }

    private void TurnOffAllDriveMotors() {

        SetDriveMotorPowerLevels(zeroPowerLevels);
    }

    private boolean isOnWhiteLine() {
        return lineDetector.getLightDetected() > WHITE_THRESHOLD;
    }

    private void SetDriveMotorPowerLevels(FourWheelDrivePowerLevels levels) {

        frontRightMotor.setPower(levels.frontRight);
        frontLeftMotor.setPower(levels.frontLeft);
    }

    public boolean climbersHaveBeenDeployed() {

        return elapsedTimeForCurrentState.time() >= 4;
    }
}
