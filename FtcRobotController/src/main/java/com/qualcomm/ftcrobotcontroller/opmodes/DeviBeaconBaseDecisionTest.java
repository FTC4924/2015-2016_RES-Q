package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;
import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by William on 4/7/2016.
 */
public abstract class DeviBeaconBaseDecisionTest extends AutonomousBase {

    public DrivePathSegment[] objectivePath = {

            new DrivePathSegment(105.0f, 105.0f, 0.9f),
            new DrivePathSegment(315.0f, 0.7f),
            new DrivePathSegment(8.0f, 8.0f, 0.9f)
    };

    private float COLOR_THRESHOLD = 2.0f;
    private boolean isCloseToBeacon = false;
    static final float CLIMBER_ARM_DEPLOYED_ANGLE = 0.0f;
    static final float CLIMBER_ARM_FOLDED_ANGLE = 1.0f;
    final float CLIMBER_ARM_WAIT_TIME = 4.0f;
    float climberArmAngle = 1.0f;
    final float CLIMBER_ARM_ANGLE_INCREMENTATION = 0.005f;

    @Override
    public void loop() {

        initServos();

        switch (currentState) {

            case STATE_INITIAL:

                if (!turningGyro.isCalibrating() && elapsedGameTime.time() >= 5.0f) {

                    startPath(objectivePath);
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

                /*if (currentPathSegmentIndex == objectivePath.length - 1) {

                    isCloseToBeacon = true;
                }*/

                if (pathIsBlocked() && !isCloseToBeacon) {

                    pausedStateIndex = stateIndex;
                    SetCurrentState(State.STATE_WAIT);
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

            case STATE_STOP:

                TurnOffAllDriveMotors();
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

                if (elapsedTimeForCurrentSegment.time() >= 2.0f && elapsedGameTime.time() >= 8.0f) {

                    SetCurrentState(State.STATE_CHANGE_PATH);
                }

                break;

            case STATE_CHANGE_PATH:

                if (!newPathSet) {

                    float distanceToNewTarget;

                    if (isStartingOnWall()) {

                        if (isRobotOnRedAlliance()) {

                            distanceToNewTarget = (float) (15.0f - (frontRightMotor.getCurrentPosition() / countsPerInch));

                            objectivePath = new DrivePathSegment[] {

                                    new DrivePathSegment(distanceToNewTarget, distanceToNewTarget, 1.0f),
                                    new DrivePathSegment(47.0f, 0.7f),
                                    new DrivePathSegment(25.0f, 25.0f, 1.0f),
                                    new DrivePathSegment(47.0f, 0.7f),
                                    new DrivePathSegment(25.0f, 25.0f, 1.0f),
                                    new DrivePathSegment(47.0f, 0.7f)
                            };

                        } else {

                            distanceToNewTarget = (float) (15.0f - (frontLeftMotor.getCurrentPosition() / countsPerInch));

                            objectivePath = new DrivePathSegment[] {

                                    new DrivePathSegment(distanceToNewTarget, distanceToNewTarget, 1.0f),
                                    new DrivePathSegment(317.0f, 0.7f),
                                    new DrivePathSegment(25.0f, 35.0f, 1.0f),
                                    new DrivePathSegment(317.0f, 0.7f),
                                    new DrivePathSegment(25.0f, 35.0f, 1.0f),
                                    new DrivePathSegment(317.0f, 0.7f)
                            };
                        }

                    } else {

                        distanceToNewTarget = (float) (40.0f - (frontLeftMotor.getCurrentPosition() / countsPerInch));

                        if (isRobotOnRedAlliance()) {

                            objectivePath = new DrivePathSegment[] {

                                    new DrivePathSegment(0.0f, 0.7f),
                                    new DrivePathSegment(distanceToNewTarget, distanceToNewTarget, 1.0f),
                                    new DrivePathSegment(92.0f, 0.7f),
                                    new DrivePathSegment(20.0f, 20.0f, 1.0f),
                                    new DrivePathSegment(92.0f, 0.7f),
                                    new DrivePathSegment(20.0f, 20.0f, 1.0f),
                                    new DrivePathSegment(92.0f, 0.7f)
                            };

                        } else {

                            objectivePath = new DrivePathSegment[] {

                                    new DrivePathSegment(0.0f, 0.7f),
                                    new DrivePathSegment(distanceToNewTarget, distanceToNewTarget, 1.0f),
                                    new DrivePathSegment(272.0f, 0.7f),
                                    new DrivePathSegment(20.0f, 20.0f, 1.0f),
                                    new DrivePathSegment(272.0f, 0.7f),
                                    new DrivePathSegment(20.0f, 20.0f, 1.0f),
                                    new DrivePathSegment(272.0f, 0.7f)
                            };
                        }
                    }

                    newPathSet = true;
                    startPath(objectivePath);
                }

                if (pathComplete()) {

                    TurnOffAllDriveMotors();
                    runWithoutEncoders();
                    SetCurrentState(State.STATE_CLIMB_MOUNTAIN);
                }

                break;

            case STATE_CLIMB_MOUNTAIN:

                backBumperServo.setPosition(0.0d);

                if (elapsedTimeForCurrentState.time() <= 5.0f) {

                    frontLeftMotor.setPower(1.0d);
                    frontRightMotor.setPower(1.0d);

                } else {

                    frontLeftMotor.setPower(0.0d);
                    frontRightMotor.setPower(0.0d);
                    SetCurrentState(State.STATE_STOP);
                }

                break;
        }

        SetEncoderTargets();
        addTelemetry();

        if (elapsedGameTime.time() >= 30.0f) {

            TurnOffAllDriveMotors();
            runWithoutEncoders();
            finalTime = elapsedGameTime.time();
            SetCurrentState(State.STATE_STOP);
        }

        if (elapsedGameTime.time() >= 2.0f) {

            mustacheMotorAngle = 0.5f;
        }
    }

    private boolean pathIsBlocked() {

        return sharpIRSensor.getDistance() <= 50.0f;
    }

    @Override
    public void addStates() {

        stateList.add(State.STATE_INITIAL);
        stateList.add(State.STATE_DRIVE_TO_BEACON);
        stateList.add(State.STATE_APPROACH_BEACON);
        stateList.add(State.STATE_DEPLOY_CLIMBERS);
        stateList.add(State.STATE_STOP);
        stateList.add(State.STATE_WAIT);
        stateList.add(State.STATE_CHANGE_PATH);
        stateList.add(State.STATE_CLIMB_MOUNTAIN);
    }

    @Override
    public void setReversedMotor() {

        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    abstract boolean isRobotOnRedAlliance();

    abstract boolean isStartingOnWall();
}
