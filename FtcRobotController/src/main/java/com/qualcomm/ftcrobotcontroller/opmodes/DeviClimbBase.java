package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;
import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by 4924_Users on 2/7/2016.
 */
public class DeviClimbBase extends AutonomousBase {

    static float climbingTime = 8.0f;
    static boolean isCloseToMountain = false;

    public DrivePathSegment[] mountainPath = {

            new DrivePathSegment(20.0f, 20.0f, 0.9f),
            new DrivePathSegment(315.0f, 0.7f),
            new DrivePathSegment(35.0f, 35.0f, 0.9f),
            new DrivePathSegment(-50.0f, 0.7f)
    };

    @Override
    public void loop() {

        climberDeployer.setPosition(1.0d);
        initServos();

        switch (currentState) {

            case STATE_INITIAL:

                if (!turningGyro.isCalibrating() && elapsedGameTime.time() >= 5.0f) {

                    startPath(mountainPath);
                    transitionToNextState();
                    telemetry.addData("1", String.format("L %5d - R %5d ", getLeftPosition(),
                            getRightPosition()));
                }

                break;

            case STATE_DRIVE_TO_MOUNTAIN: // Follow mountainPath until last segment is completed

                if (pathComplete()) {

                    backBumperServo.setPosition(0.0d);                    TurnOffAllDriveMotors();
                    runWithoutEncoders();
                    transitionToNextState();      // Next State:
                }

                if (elapsedGameTime.time() >= 22) {

                    isCloseToMountain = true;
                }

                if (pathIsBlocked() && !isCloseToMountain) {

                    pausedStateIndex = stateIndex;
                    stateIndex = State.STATE_WAIT.ordinal();
                    transitionToNextState();
                }

                break;

            case STATE_CLIMB_MOUNTAIN:

                if (elapsedTimeForCurrentState.time() >= climbingTime) {

                    TurnOffAllDriveMotors();
                    transitionToNextState();
                    finalTime = elapsedGameTime.time();


                } else {

                    setClimbingPowerLevels();
                }

                break;

            case STATE_STOP:

                TurnOffAllDriveMotors();
                collectMotor.setPower(0.0f);
                frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
                frontLeftMotor.setDirection(DcMotor.Direction.FORWARD);
                telemetry.addData("Final Time: ", finalTime);

                break;

            case STATE_WAIT:

                TurnOffAllDriveMotors();

                if (!pathIsBlocked()) {

                    stateIndex = pausedStateIndex - 1;
                    transitionToNextState();

                    FourWheelDrivePowerLevels powerLevels =
                            new FourWheelDrivePowerLevels(segment.leftPower, segment.rightPower);
                    SetDriveMotorPowerLevels(powerLevels);
                }

                break;
        }

        SetEncoderTargets();
        addTelemetry();

        if (elapsedGameTime.time() >= 30.0f) {

            TurnOffAllDriveMotors();
            runWithoutEncoders();
            SetCurrentState(State.STATE_STOP);
        }

        if (elapsedGameTime.time() >= 2.0f) {

            mustacheMotorAngle = 0.5f;
        }
    }

    private boolean pathIsBlocked() {

        //return sharpIRSensor.getDistance() <= 50.0f;
        return false;
    }

    public void setClimbingPowerLevels() {

        frontLeftMotor.setPower(1.0d);
        frontRightMotor.setPower(1.0d);
    }

    @Override
    public void addStates() {

        stateList.add(AutonomousBase.State.STATE_INITIAL);
        stateList.add(AutonomousBase.State.STATE_DRIVE_TO_MOUNTAIN);
        stateList.add(AutonomousBase.State.STATE_CLIMB_MOUNTAIN);
        stateList.add(AutonomousBase.State.STATE_STOP);
        stateList.add(State.STATE_WAIT);
    }

    @Override
    public void setReversedMotor() {

        frontLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
    }
}
