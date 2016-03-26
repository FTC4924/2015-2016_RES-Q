package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;
import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by 4924_Users on 2/7/2016.
 */
public class DeviBeaconBaseTest extends AutonomousBase {

    public DrivePathSegment[] beaconPath = {

            new DrivePathSegment(105.0f, 105.0f, 0.9f),
            new DrivePathSegment(315.0f, 0.7f),
            new DrivePathSegment(8.0f, 8.0f, 0.9f)
    };

    public boolean isRobotOnRedAlliance = false;
    private float COLOR_THRESHOLD = 2.0f;
    private boolean isCloseToBeacon = false;
    static final float CLIMBER_ARM_DEPLOYED_ANGLE = 0.0f;
    static final float CLIMBER_ARM_FOLDED_ANGLE = 1.0f;
    final float CLIMBER_ARM_WAIT_TIME = 4.0f;
    float climberArmAngle = 1.0f;
    final float CLIMBER_ARM_ANGLE_INCREMENTATION = 0.005f;

    @Override
    public void loop() {

        colorSensor.enableLed(false);
        initServos();

        switch (currentState) {

            case STATE_INITIAL:

                if (!turningGyro.isCalibrating() && elapsedGameTime.time() >= 5.0f) {

                    startPath(beaconPath);
                    transitionToNextState();
                    telemetry.addData("1", String.format("L %5d - R %5d ", getLeftPosition(),
                            getRightPosition()));
                    bumperServo.setPosition(BUMPER_DEPLOYED_ANGLE);
                }

                break;

            case STATE_DRIVE_TO_BEACON: // Follow mountainPath until last segment is completed

                if (pathComplete()) {

                    TurnOffAllDriveMotors();
                    runWithoutEncoders();
                    transitionToNextState();
                }

                if (elapsedGameTime.time() >= 22) {

                    isCloseToBeacon = true;
                }

                if (pathIsBlocked() && !isCloseToBeacon) {

                    pausedStateIndex = stateIndex;
                    stateIndex = stateList.size() - 2;
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

                if (climberArmAngle <= CLIMBER_ARM_DEPLOYED_ANGLE) {

                    if (elapsedTimeForCurrentState.time() >= CLIMBER_ARM_WAIT_TIME) {

                        climberDeployer.setPosition(CLIMBER_ARM_FOLDED_ANGLE);
                        SetCurrentState(State.STATE_STOP);
                    }

                } else {

                    climberArmAngle -= CLIMBER_ARM_ANGLE_INCREMENTATION;
                    climberArmAngle = Range.clip(climberArmAngle, 0.0f, 1.0f);
                    climberDeployer.setPosition(climberArmAngle);
                }

                break;

            case STATE_READ_BEACON:

                telemetry.addData("Red: ", isRed());
                telemetry.addData("Blue: ", isBlue());

                if (isRobotOnRedAlliance) {

                    if (isRed()) {


                    }

                } else {

                    if (isBlue()) {


                    }
                }

            case STATE_STOP:

                TurnOffAllDriveMotors();
                frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
                frontLeftMotor.setDirection(DcMotor.Direction.FORWARD);

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

        return sharpIRSensor.getDistance() <= 50.0f;
    }

    public boolean isRed() {

        return colorSensor.red() >= COLOR_THRESHOLD;
    }

    public boolean isBlue() {

        return colorSensor.blue() >= COLOR_THRESHOLD;
    }

    @Override
    public void addStates() {

        stateList.add(State.STATE_INITIAL);
        stateList.add(State.STATE_DRIVE_TO_BEACON);
        stateList.add(State.STATE_APPROACH_BEACON);
        stateList.add(State.STATE_DEPLOY_CLIMBERS);
        stateList.add(State.STATE_READ_BEACON);
        stateList.add(State.STATE_STOP);
        stateList.add(State.STATE_WAIT);
    }

    @Override
    public void setReversedMotor() {

        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
    }
}
