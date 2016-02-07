package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;
import com.qualcomm.ftcrobotcontroller.EncoderTargets;
import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by 4924_Users on 2/7/2016.
 */
public class DeviClimbBaseTest extends AutonomousBase {

    static float climbingTime = 8.0f;

    public DrivePathSegment[] mountainPath = {

            new DrivePathSegment(20.0f, 20.0f, 0.9f),
            new DrivePathSegment(315.0f, 0.7f),
            new DrivePathSegment(35.0f, 35.0f, 0.9f),
            new DrivePathSegment(-50.0f, 0.7f)
    };

    private State currentState;
    private int currentPathSegmentIndex = 0;
    private DrivePathSegment[] currentPath = mountainPath;
    DrivePathSegment segment = currentPath[currentPathSegmentIndex];
    EncoderTargets currentEncoderTargets = zeroEncoderTargets;

    @Override
    public void loop() {

        rightsideservo.setPosition(1.0d);
        climberDeployer.setPosition(1.0d);
        gateServo.setPosition(0.5d);
        ziplinerTripper.setPosition(0.5d);
        deliveryBelt.setPosition(0.5d);

        switch (currentState) {

            case STATE_INITIAL:

                if (!turningGyro.isCalibrating()) {

                    startPath(mountainPath);
                    transitionToNextState();
                    telemetry.addData("1", String.format("L %5d - R %5d ", getLeftPosition(),
                            getRightPosition()));
                }

                break;

            case STATE_DRIVE_TO_MOUNTAIN: // Follow mountainPath until last segment is completed

                if (pathComplete()) {

                    TurnOffAllDriveMotors();
                    runWithoutEncoders();
                    transitionToNextState();      // Next State:
                }

                break;

            case STATE_CLIMB_MOUNTAIN:

                if (elapsedTimeForCurrentState.time() >= climbingTime) {

                    TurnOffAllDriveMotors();
                    transitionToNextState();

                } else {

                    setClimbingPowerLevels();
                }

                break;

            case STATE_STOP:

                TurnOffAllDriveMotors();
                collectMotor.setPower(0.0f);
                frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
                frontLeftMotor.setDirection(DcMotor.Direction.FORWARD);

                break;
        }

        SetEncoderTargets();
        addTelemetry();

        if (elapsedGameTime.time() >= 30.0f) {

            TurnOffAllDriveMotors();
            runWithoutEncoders();
            transitionToNextState();
        }

        if (elapsedGameTime.time() >= 2.0f) {

            mustacheMotorAngle = 0.5f;
        }
    }

    public void setClimbingPowerLevels() {

        frontLeftMotor.setPower(0.6d);
        frontRightMotor.setPower(0.6d);
    }

    @Override
    public void addStates() {

        stateList.add(AutonomousBase.State.STATE_INITIAL);
        stateList.add(AutonomousBase.State.STATE_DRIVE_TO_MOUNTAIN);
        stateList.add(AutonomousBase.State.STATE_CLIMB_MOUNTAIN);
        stateList.add(AutonomousBase.State.STATE_STOP);
    }
}
