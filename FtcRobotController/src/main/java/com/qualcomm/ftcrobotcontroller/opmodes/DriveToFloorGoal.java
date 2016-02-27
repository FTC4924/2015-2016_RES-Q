package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by 4924_Users on 2/27/2016.
 */
public class DriveToFloorGoal extends AutonomousBase {

    public DrivePathSegment[] floorGoalPath = {

            new DrivePathSegment(70.0f, 70.0f, 0.9f),
    };

    @Override
    public void loop() {

        climberDeployer.setPosition(1.0d);
        initServos();

        switch (currentState) {

            case STATE_INITIAL:

                if (!turningGyro.isCalibrating()) {

                    startPath(floorGoalPath);
                    transitionToNextState();
                    telemetry.addData("1", String.format("L %5d - R %5d ", getLeftPosition(),
                            getRightPosition()));
                }

                break;

            case STATE_MOVE_TO_FLOOR_GOAL:

                if (pathComplete()) {

                    TurnOffAllDriveMotors();
                    transitionToNextState();
                }

                break;

            case STATE_STOP:

                TurnOffAllDriveMotors();

                break;
        }
    }

    @Override
    public void addStates() {

        stateList.add(State.STATE_INITIAL);
        stateList.add(State.STATE_MOVE_TO_FLOOR_GOAL);
        stateList.add(State.STATE_STOP);
    }

    @Override
    public void setReversedMotor() {

        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
    }
}
