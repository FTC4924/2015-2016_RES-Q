package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;
import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.ArrayList;

/**
 * Created by 4924_Users on 2/7/2016.
 */
public class DeviBeaconBaseTest extends AutonomousBase {

    public DrivePathSegment[] beaconPath = {

            new DrivePathSegment(105.0f, 105.0f, 0.9f),
            new DrivePathSegment(315.0f, 0.7f),
            new DrivePathSegment(8.0f, 8.0f, 0.9f)
    };

    @Override
    public void loop() {

        rightsideservo.setPosition(1.0d);
        gateServo.setPosition(0.5d);
        ziplinerTripper.setPosition(0.5d);
        deliveryBelt.setPosition(0.5d);

        switch (currentState) {

            case STATE_INITIAL:

                if (!turningGyro.isCalibrating()) {

                    startPath(beaconPath);
                    transitionToNextState();
                    telemetry.addData("1", String.format("L %5d - R %5d ", getLeftPosition(),
                            getRightPosition()));
                }

                break;

            case STATE_DRIVE_TO_BEACON: // Follow mountainPath until last segment is completed

                if (pathComplete()) {

                    //bumperServo.setPosition(BUMPER_DEPLOYED_ANGLE);
                    TurnOffAllDriveMotors();
                    runWithoutEncoders();
                    transitionToNextState();
                }

                break;

            case STATE_APPROACH_BEACON:

                if (bumper.isPressed()) {

                    TurnOffAllDriveMotors();
                    runWithoutEncoders();
                    transitionToNextState();

                } else {

                    FourWheelDrivePowerLevels powerLevels =
                            new FourWheelDrivePowerLevels(0.5f, 0.5f);
                    SetDriveMotorPowerLevels(powerLevels);
                }

                break;

            case STATE_DEPLOY_CLIMBERS:

                if (elapsedTimeForCurrentState.time() >= 2.0f) {

                    climberDeployer.setPosition(1.0f);
                    transitionToNextState();

                } else {

                    climberDeployer.setPosition(0.0f);
                }

            case STATE_STOP:

                TurnOffAllDriveMotors();
                frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
                frontLeftMotor.setDirection(DcMotor.Direction.FORWARD);

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

    @Override
    public void addStates() {

        stateList.add(State.STATE_INITIAL);
        stateList.add(State.STATE_DRIVE_TO_BEACON);
        stateList.add(State.STATE_APPROACH_BEACON);
        stateList.add(State.STATE_DEPLOY_CLIMBERS);
        stateList.add(State.STATE_STOP);
    }
}
