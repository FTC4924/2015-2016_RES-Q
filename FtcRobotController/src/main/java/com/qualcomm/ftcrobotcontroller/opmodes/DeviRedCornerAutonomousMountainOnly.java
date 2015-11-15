package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;
import com.qualcomm.ftcrobotcontroller.EncoderTargets;
import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by 4924_Users on 11/14/2015.
 */
public class DeviRedCornerAutonomousMountainOnly extends OpMode {

    final DrivePathSegment[] mountainPath = {

            new DrivePathSegment(0.0f, 12.0f, 0.5f),  // Left
            new DrivePathSegment(96.0f, 96.0f, 0.9f),  // Forward
            new DrivePathSegment(40.0f, 0.0f, 0.5f),
            new DrivePathSegment(-40.0f, -40.0f, 0.5f)
    };
                                                                                    //(E) rules
    public enum State {
        STATE_INITIAL,
        STATE_DRIVE_TO_MOUNTAIN,
        STATE_CLIMB_MOUNTAIN,
        STATE_STOP
    }

    public ElapsedTime elapsedGameTime = new ElapsedTime();
    private EncoderTargets zeroEncoderTargets = new EncoderTargets(0, 0);
    private FourWheelDrivePowerLevels zeroPowerLevels = new FourWheelDrivePowerLevels(0.0f, 0.0f);
    private ElapsedTime elapsedTimeForCurrentState = new ElapsedTime();
    double countsPerInch;

    final int COUNTS_PER_REVOLUTION = 1120;
    final double WHEEL_DIAMETER = 4.5f;
    final double GEAR_RATIO = 24.0f/16.0f;
    static final int ENCODER_TARGET_MARGIN = 10;

    DcMotor frontLeftMotor;
    DcMotor frontRightMotor;

    private State currentState;
    private int currentPathSegmentIndex;
    private DrivePathSegment[] currentPath;
    EncoderTargets currentEncoderTargets = zeroEncoderTargets;

    @Override
    public void init() {

        frontRightMotor = hardwareMap.dcMotor.get("frontrightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontleftMotor");
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

        switch (currentState) {

            case STATE_INITIAL:

                if (encodersAtZero()) {

                    startPath(mountainPath);
                    SetCurrentState(State.STATE_DRIVE_TO_MOUNTAIN);
                    telemetry.addData("1", String.format("L %5d - R %5d ", getLeftPosition(),
                            getRightPosition()));
                }

                break;

            case STATE_DRIVE_TO_MOUNTAIN:

                if (pathComplete()) {

                    TurnOffAllDriveMotors();
                    runWithoutEncoders();
                    SetCurrentState(State.STATE_CLIMB_MOUNTAIN);
                }

                break;

            case STATE_CLIMB_MOUNTAIN:

                if (elapsedTimeForCurrentState.time() >= 8.0f) {

                    TurnOffAllDriveMotors();
                    SetCurrentState(State.STATE_STOP);

                } else {

                    setClimbingPowerLevels();
                }

                break;

            case STATE_STOP:

                TurnOffAllDriveMotors();
        }

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

    public void SetCurrentState(State newState) {
        elapsedTimeForCurrentState.reset();
        currentState = newState;
    }

    private void TurnOffAllDriveMotors() {
        SetDriveMotorPowerLevels(zeroPowerLevels);
    }

    private void SetDriveMotorPowerLevels(FourWheelDrivePowerLevels levels) {

        frontRightMotor.setPower(levels.frontLeft);
        frontLeftMotor.setPower(levels.backRight);
    }

    private void setEncoderTargetsToCurrentPosition() {

        currentEncoderTargets.LeftTarget = getLeftPosition();
        currentEncoderTargets.RightTarget = getRightPosition();
    }

    public void UseRunToPosition() {

        setDriveMode(DcMotorController.RunMode.RUN_TO_POSITION);
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

    public void setDriveMode(DcMotorController.RunMode mode) {

        if (frontLeftMotor.getChannelMode() != mode)
            frontRightMotor.setChannelMode(mode);
        if (frontRightMotor.getChannelMode() != mode)
            frontLeftMotor.setChannelMode(mode);
    }

    void addEncoderTarget(int leftEncoderAdder, int rightEncoderAdder) {

        currentEncoderTargets.LeftTarget += leftEncoderAdder;
        currentEncoderTargets.RightTarget += rightEncoderAdder;
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
                //UseConstantSpeed();
                return true;
            }
        }
        return false;
    }

    private boolean moveComplete() {
        //  return (!mLeftMotor.isBusy() && !mRightMotor.isBusy());
        //return (elapsedTimeForCurrentState.time() >= 2.0f); For testing use only
        return ((Math.abs(getLeftPosition() - currentEncoderTargets.LeftTarget) < ENCODER_TARGET_MARGIN) &&
                (Math.abs(getRightPosition() - currentEncoderTargets.RightTarget) < ENCODER_TARGET_MARGIN));
    }

    private void SetEncoderTargets() {
        frontLeftMotor.setTargetPosition(currentEncoderTargets.LeftTarget);
        frontRightMotor.setTargetPosition(currentEncoderTargets.RightTarget);
    }

    public void runWithoutEncoders() {

        setDriveMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
    }

    public void setClimbingPowerLevels() {

        frontLeftMotor.setPower(-0.4d);
        frontRightMotor.setPower(-0.4d);
    }
}
