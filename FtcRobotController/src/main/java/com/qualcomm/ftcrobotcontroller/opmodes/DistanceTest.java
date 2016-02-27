package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;
import com.qualcomm.ftcrobotcontroller.EncoderTargets;
import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by 4924_Users on 12/1/2015.
 */
public class DistanceTest extends OpMode {

    public enum State {
        STATE_INITIAL,
        STATE_DRIVE_TO_MOUNTAIN,
        STATE_CLIMB_MOUNTAIN,
        STATE_STOP
    }

    public ElapsedTime elapsedGameTime = new ElapsedTime();
    private FourWheelDrivePowerLevels zeroPowerLevels = new FourWheelDrivePowerLevels(0.0f, 0.0f);
    private ElapsedTime elapsedTimeForCurrentState = new ElapsedTime();
    private EncoderTargets zeroEncoderTargets = new EncoderTargets(0, 0);
    final int COUNTS_PER_REVOLUTION = 1120;
    final double WHEEL_DIAMETER = 4.5f;
    final double GEAR_RATIO = 24.0f/16.0f;
    double countsPerInch;
    static final int ENCODER_TARGET_MARGIN = 10;
    final float TURNING_ANGLE_MARGINE = 2.0f;
    static final float CALIBRATION_FACTOR = 1.414f;

    DcMotor frontLeftMotor;
    DcMotor frontRightMotor;
    GyroSensor turningGyro;

    final DrivePathSegment[] mountainPath = {

            new DrivePathSegment(36.0f, 36.0f, 0.9f)
    };

    private State currentState;
    private int currentPathSegmentIndex = 0;
    private DrivePathSegment[] currentPath = mountainPath;
    DrivePathSegment segment = currentPath[currentPathSegmentIndex];
    EncoderTargets currentEncoderTargets = zeroEncoderTargets;

    public void SetCurrentState(State newState) {

        elapsedTimeForCurrentState.reset();
        currentState = newState;
    }

    @Override
    public void init() {

        frontRightMotor = hardwareMap.dcMotor.get("frontrightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontleftMotor");
        turningGyro = hardwareMap.gyroSensor.get("gyroSensor");

        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);

        countsPerInch = (COUNTS_PER_REVOLUTION / (Math.PI * WHEEL_DIAMETER)) * GEAR_RATIO * CALIBRATION_FACTOR;

        turningGyro.calibrate();
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

                if (encodersAtZero() && !turningGyro.isCalibrating()) {

                    startPath(mountainPath);
                    SetCurrentState(State.STATE_DRIVE_TO_MOUNTAIN);
                    telemetry.addData("1", String.format("L %5d - R %5d ", getLeftPosition(),
                            getRightPosition()));
                }

                break;

            case STATE_DRIVE_TO_MOUNTAIN: // Follow mountainPath until last segment is completed

                if (pathComplete()) {

                    TurnOffAllDriveMotors();
                    runWithoutEncoders();
                    SetCurrentState(State.STATE_STOP);      // Next State:
                }

                break;

            case STATE_CLIMB_MOUNTAIN:

                if (elapsedTimeForCurrentState.time() >= 3.0f) {

                    TurnOffAllDriveMotors();
                    SetCurrentState(State.STATE_STOP);

                } else {

                    setClimbingPowerLevels();
                }

                break;

            case STATE_STOP:

                TurnOffAllDriveMotors();

                break;
        }

        telemetry.addData("Left: ", currentEncoderTargets.LeftTarget);
        telemetry.addData("Right: ", currentEncoderTargets.RightTarget);
        telemetry.addData("Heading: ", turningGyro.getHeading());
        SetEncoderTargets();
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

        segment = currentPath[currentPathSegmentIndex];

        int Left;
        int Right;

        if (currentPath != null) {

            if (segment.isTurn) {

                runWithoutEncoders();

                if (segment.Angle > 0) {

                    FourWheelDrivePowerLevels powerLevels =
                            new FourWheelDrivePowerLevels(segment.leftPower, 0.0f);
                    SetDriveMotorPowerLevels(powerLevels);

                } else {

                    FourWheelDrivePowerLevels powerLevels =
                            new FourWheelDrivePowerLevels(0.0f, segment.leftPower);
                    SetDriveMotorPowerLevels(powerLevels);
                }

            } else {

                Left  = (int)(segment.LeftSideDistance * countsPerInch);
                Right = (int)(segment.RightSideDistance * countsPerInch);
                addEncoderTarget(Left, Right);
                FourWheelDrivePowerLevels powerLevels =
                        new FourWheelDrivePowerLevels(segment.leftPower, segment.leftPower);
                SetDriveMotorPowerLevels(powerLevels);
            }

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
            if (currentPathSegmentIndex < currentPath.length) {

                TurnOffAllDriveMotors();
                startSeg();

            } else {

                currentPath = null;
                currentPathSegmentIndex = 0;
                TurnOffAllDriveMotors();
                return true;
            }
        }

        return false;
    }

    private boolean linearMoveComplete() {

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

    public boolean turnComplete() {

        return segment.Angle <= turningGyro.getHeading() + TURNING_ANGLE_MARGINE &&
                segment.Angle >= turningGyro.getHeading() - TURNING_ANGLE_MARGINE;
    }

    public boolean moveComplete() {

        if (segment.isTurn) {

            return turnComplete();

        } else {

            return linearMoveComplete();
        }
    }

    public void setClimbingPowerLevels() {

        frontLeftMotor.setPower(-0.4d);
        frontRightMotor.setPower(-0.4d);
    }
}
