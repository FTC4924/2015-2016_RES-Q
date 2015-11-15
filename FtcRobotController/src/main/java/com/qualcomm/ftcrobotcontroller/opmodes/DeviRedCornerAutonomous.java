package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;
import com.qualcomm.ftcrobotcontroller.EncoderTargets;
import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by 4924_Users on 10/23/2015.
 */
public class DeviRedCornerAutonomous extends OpMode {

    public enum State {
        STATE_INITIAL,
        STATE_DRIVE_TO_BEACON,
        STATE_FOLLOW_LINE,
        STATE_DEPLOY_CLIMBERS,
        STATE_STOP
    }

    public ElapsedTime elapsedGameTime = new ElapsedTime();
    private FourWheelDrivePowerLevels zeroPowerLevels = new FourWheelDrivePowerLevels(0.0f, 0.0f);
    private ElapsedTime elapsedTimeForCurrentState = new ElapsedTime();
    private EncoderTargets zeroEncoderTargets = new EncoderTargets(0, 0);
    final int COUNTS_PER_REVOLUTION = 1120;
    final double WHEEL_DIAMETER = 4.5f;
    final double GEAR_RATIO = 24.0f/16.0f;
    final double WHITE_THRESHOLD = 0.05f;
    double countsPerInch;
    static final int ENCODER_TARGET_MARGIN = 10;

    DcMotor frontLeftMotor;
    DcMotor frontRightMotor;
    OpticalDistanceSensor lineDetector;
    TouchSensor bumper;

    private State currentState;
    private int currentPathSegmentIndex;
    private DrivePathSegment[] currentPath;
    EncoderTargets currentEncoderTargets = zeroEncoderTargets;

    final DrivePathSegment[] beaconPath = {
            //new DrivePathSegment(  6.0f,   6.0f, 0.5f),
            new DrivePathSegment(8.0f, 8.0f, 0.9f),
            new DrivePathSegment(-8.0f, 8.0f, 0.5f),  // Left
            new DrivePathSegment(140.0f, 140.0f, 0.9f),  // Forward
            new DrivePathSegment(-5.0f, 5.0f, 0.5f),
            new DrivePathSegment(22.0f, 22.0f, 0.9f)
    };

    public void SetCurrentState(State newState) {

        elapsedTimeForCurrentState.reset();
        currentState = newState;
    }

    @Override
    public void init() {

        frontRightMotor = hardwareMap.dcMotor.get("frontrightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontleftMotor");
        lineDetector = hardwareMap.opticalDistanceSensor.get("lineDetector");
        //bumper = hardwareMap.touchSensor.get("bumper");
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);

        countsPerInch = (COUNTS_PER_REVOLUTION / (Math.PI * WHEEL_DIAMETER)) * GEAR_RATIO;
    }

    @Override
    public void start() {

        elapsedGameTime.reset();

        SetCurrentState(State.STATE_INITIAL);
    }

    @Override
    public void loop() {

        telemetry.addData("0", String.format("%4.1f ", elapsedTimeForCurrentState.time()) + currentState.toString());

        switch (currentState) {

            case STATE_INITIAL:

                if (encodersAtZero()) {

                    startPath(beaconPath);
                    SetCurrentState(State.STATE_DRIVE_TO_BEACON);
                    telemetry.addData("1", String.format("L %5d - R %5d ", getLeftPosition(),
                            getRightPosition()));
                }

                break;

            case STATE_DRIVE_TO_BEACON: // Follow beaconPath until last segment is completed

                if (pathComplete()) {

                    TurnOffAllDriveMotors();
                    runWithoutEncoders();
                    SetCurrentState(State.STATE_FOLLOW_LINE);      // Next State:
                }

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

            case STATE_STOP:

                TurnOffAllDriveMotors();
        }

        telemetry.addData("Left: ", currentEncoderTargets.LeftTarget);
        telemetry.addData("Right: ", currentEncoderTargets.RightTarget);
        telemetry.addData("White Line: ", isOnWhiteLine());
        telemetry.addData("ODS Reading: ",  lineDetector.getLightDetected());
        SetEncoderTargets();
    }

    private boolean isOnWhiteLine() {
        return lineDetector.getLightDetected() > WHITE_THRESHOLD;
    }

    private boolean encodersAtZero() {

        return ((Math.abs(getLeftPosition()) < 5) && (Math.abs(getRightPosition()) < 5));
    }

    private int getRightPosition() {

        return frontRightMotor.getCurrentPosition();
    }

    private int getLeftPosition() {

        return frontLeftMotor.getCurrentPosition();
    }

    private void startPath(DrivePathSegment[] path) {

        currentPath = path;
        currentPathSegmentIndex = 0;
        setEncoderTargetsToCurrentPosition();
        UseRunToPosition();
        startSeg();
    }

    private void setEncoderTargetsToCurrentPosition() {

        currentEncoderTargets.LeftTarget = getLeftPosition();
        currentEncoderTargets.RightTarget = getRightPosition();
    }

    public void UseRunToPosition() {

        setDriveMode(DcMotorController.RunMode.RUN_TO_POSITION);
    }

    public void runWithoutEncoders() {

        setDriveMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
    }

    public void setDriveMode(DcMotorController.RunMode mode) {

        if (frontLeftMotor.getChannelMode() != mode)
            frontRightMotor.setChannelMode(mode);
        if (frontRightMotor.getChannelMode() != mode)
            frontLeftMotor.setChannelMode(mode);
    }

    private void startSeg() {

        int Left;
        int Right;

        if (currentPath != null) {

            Left  = (int)(currentPath[currentPathSegmentIndex].LeftSideDistance * countsPerInch);
            Right = (int)(currentPath[currentPathSegmentIndex].RightSideDistance * countsPerInch);
            addEncoderTarget(Left, Right);
            FourWheelDrivePowerLevels powerLevels =
                    new FourWheelDrivePowerLevels(currentPath[currentPathSegmentIndex].Power,
                            currentPath[currentPathSegmentIndex].Power);
            SetDriveMotorPowerLevels(powerLevels);

            currentPathSegmentIndex++;
        }
    }

    void addEncoderTarget(int leftEncoderAdder, int rightEncoderAdder) {

        currentEncoderTargets.LeftTarget += leftEncoderAdder;
        currentEncoderTargets.RightTarget += rightEncoderAdder;
    }

    private void SetDriveMotorPowerLevels(FourWheelDrivePowerLevels levels) {

        frontRightMotor.setPower(levels.frontLeft);
        frontLeftMotor.setPower(levels.backRight);
    }

    private boolean pathComplete() {
        // Wait for this Segement to end and then see what's next.
        if (moveComplete()) {
            // Start next Segement if there is one.
            if (currentPathSegmentIndex < currentPath.length)
            {
                startSeg();
            }
            else  // Otherwise, stop and return done
            {
                currentPath = null;
                currentPathSegmentIndex = 0;
                TurnOffAllDriveMotors();
                return true;
            }
        }
        return false;
    }

    private boolean moveComplete() {

        return ((Math.abs(getLeftPosition() - currentEncoderTargets.LeftTarget) < ENCODER_TARGET_MARGIN) &&
               (Math.abs(getRightPosition() - currentEncoderTargets.RightTarget) < ENCODER_TARGET_MARGIN));
    }

    private void TurnOffAllDriveMotors() {
        SetDriveMotorPowerLevels(zeroPowerLevels);
    }

    private void SetEncoderTargets() {
        frontLeftMotor.setTargetPosition(currentEncoderTargets.LeftTarget);
        frontRightMotor.setTargetPosition(currentEncoderTargets.RightTarget);
    }

    public boolean beaconIsReached() {

        return bumper.isPressed();
    }

    public void setPowerLevelsForLineFollowing(float leftPower, float rightPower) {

        frontLeftMotor.setPower(leftPower);
        frontRightMotor.setPower(rightPower);
    }
}