package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;
import com.qualcomm.ftcrobotcontroller.EncoderTargets;
import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by 4924_Users on 10/23/2015.
 */
public class DeviRedCornerAutonomous extends OpMode {

    public enum State {
        STATE_INITIAL,
        STATE_DRIVE_TO_BEACON,
        STATE_LOCATE_LINE,
        STATE_FOLLOW_LINE,
        STATE_SQUARE_TO_WALL,
        STATE_DEPLOY_CLIMBERS,
        STATE_DRIVE_TO_MOUNTAIN,
        STATE_CLIMB_MOUNTAIN,
        STATE_STOP,
    }

    public ElapsedTime elapsedGameTime = new ElapsedTime();
    private FourWheelDrivePowerLevels zeroPowerLevels = new FourWheelDrivePowerLevels(0.0f, 0.0f);
    private ElapsedTime elapsedTimeForCurrentState = new ElapsedTime();
    private EncoderTargets zeroEncoderTargets = new EncoderTargets(0, 0);
    final int COUNTS_PER_REVOLUTION = 1024;
    final double WHEEL_DIAMETER = 5.0f;
    final double GEAR_RATIO = 1.0f;
    double countsPerInch;

    DcMotor leftChainedMotor;
    DcMotor leftGearedMotor;
    DcMotor rightChainedMotor;
    DcMotor rightGearedMotor;

    private State currentState;
    private int currentPathSegmentIndex;
    private DrivePathSegment[] currentPath;
    EncoderTargets currentEncoderTargets = zeroEncoderTargets;

    final DrivePathSegment[] mBeaconPath = {
            new DrivePathSegment(  0.0f,  3.0f, 0.2f),  // Left
            new DrivePathSegment( 60.0f, 60.0f, 0.9f),  // Forward
            new DrivePathSegment(  1.0f,  0.0f, 0.2f),  // Left
    };

    public void SetCurrentState(State newState) {
        elapsedTimeForCurrentState.reset();
        currentState = newState;
    }

    @Override
    public void init() {

        rightChainedMotor = hardwareMap.dcMotor.get("rightChainedMotor");
        rightGearedMotor = hardwareMap.dcMotor.get("rightGearedMotor");
        leftChainedMotor = hardwareMap.dcMotor.get("leftChainedMotor");
        leftGearedMotor = hardwareMap.dcMotor.get("leftGearedMotor");
        rightChainedMotor.setDirection(DcMotor.Direction.REVERSE);
        rightGearedMotor.setDirection(DcMotor.Direction.REVERSE);

        countsPerInch = COUNTS_PER_REVOLUTION / Math.PI * WHEEL_DIAMETER;
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

                    startPath(mBeaconPath);
                    SetCurrentState(State.STATE_DRIVE_TO_BEACON);

                } else {

                    telemetry.addData("1", String.format("L %5d - R %5d ", getLeftPosition(),
                            getRightPosition() ));
                }
                break;

            case STATE_DRIVE_TO_BEACON: // Follow path until last segment is completed
                if (pathComplete())
                {
                    //mLight.enableLed(true);                 // Action: Enable Light Sensor
                    //setDriveSpeed(-0.1, 0.1);               // Action: Start rotating left
                    SetCurrentState(State.STATE_LOCATE_LINE);      // Next State:
                }
                else
                {
                    // Display Diagnostic data for this state.
                    //telemetry.addData("1", String.format("%d of %d. L %5d:%5s - R %5d:%5d ",
                    //        mCurrentSeg, mCurrentPath.length,
                    //       mLeftEncoderTarget, getLeftPosition(),
                    //        mRightEncoderTarget, getRightPosition()));
                }
                break;
        }
    }

    private boolean encodersAtZero() {

        return ((Math.abs(getLeftPosition()) < 5) && (Math.abs(getRightPosition()) < 5));
    }

    private int getRightPosition() {

        return currentEncoderTargets.RightTarget;
    }

    private int getLeftPosition() {

        return currentEncoderTargets.LeftTarget;
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

    public void setDriveMode(DcMotorController.RunMode mode) {

        //TODO Find an explanation for this commented-out code
        //if (frontLeftMotor.getChannelMode() != mode)
        rightChainedMotor.setChannelMode(mode);
        //if (backLeftMotor.getChannelMode() != mode)
        rightGearedMotor.setChannelMode(mode);
        //if (frontRightMotor.getChannelMode() != mode)
        leftChainedMotor.setChannelMode(mode);
        //if (backRightMotor.getChannelMode() != mode)
        leftGearedMotor.setChannelMode(mode);
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

        rightChainedMotor.setPower(levels.frontLeft);
        rightGearedMotor.setPower(levels.backLeft);
        leftChainedMotor.setPower(levels.backRight);
        leftGearedMotor.setPower(levels.frontRight);
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
                UseConstantSpeed();
                return true;
            }
        }
        return false;
    }

    private boolean moveComplete() {
        //  return (!mLeftMotor.isBusy() && !mRightMotor.isBusy());
        return (elapsedTimeForCurrentState.time() >= 2.0f);
        // return ((Math.abs(getLeftPosition() - currentEncoderTargets.LeftTarget) < 10) &&
        //        (Math.abs(getRightPosition() - currentEncoderTargets.RightTarget) < 10));
    }

    private void TurnOffAllDriveMotors() {
        SetDriveMotorPowerLevels(zeroPowerLevels);
    }

    public void UseConstantSpeed()
    {
        setDriveMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
    }
}