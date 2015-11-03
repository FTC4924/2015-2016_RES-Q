package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;
import com.qualcomm.ftcrobotcontroller.EncoderTargets;
import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by 4924_Users on 10/23/2015.
 */
public class DeviRedCornerAutonomous extends OpMode {

    public enum State {
        STATE_INITIAL,
        STATE_DRIVE_TO_BEACON,
        STATE_FOLLOW_LINE,
        STATE_SQUARE_TO_WALL,
        STATE_DEPLOY_CLIMBERS,
        STATE_DRIVE_TO_MOUNTAIN,
        STATE_CLIMB_MOUNTAIN,
        STATE_STOP,
    }

    public ElapsedTime elapsedGameTime = new ElapsedTime();
    private FourWheelDrivePowerLevels zeroPowerLevels = new FourWheelDrivePowerLevels(0.0f, 0.0f);
    private OpticalDistanceSensor lineDetector;
    private ElapsedTime elapsedTimeForCurrentState = new ElapsedTime();
    private EncoderTargets zeroEncoderTargets = new EncoderTargets(0, 0);
    final int COUNTS_PER_REVOLUTION = 1120;
    final double WHEEL_DIAMETER = 4.5f;
    final double GEAR_RATIO = 24.0f/16.0f;
    final double WHITE_THRESHOLD = 0.05f;
    double countsPerInch;
    static final int ENCODER_TARGET_MARGIN = 20;

    DcMotor frontLeftMotor;
    DcMotor backLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backRightMotor;

    private State currentState;
    private int currentPathSegmentIndex;
    private DrivePathSegment[] currentPath;
    EncoderTargets currentEncoderTargets = zeroEncoderTargets;

    final DrivePathSegment[] mBeaconPath = {
            new DrivePathSegment(  0.0f,  17.0f, 0.5f),  // Left
            new DrivePathSegment( 150.0f, 150.0f, 0.9f)  // Forward
    };

    final DrivePathSegment[] startOnLinePath = {
            new DrivePathSegment(  0.0f,  10.0f, 0.5f)  // Left
    };

    public void SetCurrentState(State newState) {
        elapsedTimeForCurrentState.reset();
        currentState = newState;
    }

    @Override
    public void init() {

        //lineDetector = hardwareMap.opticalDistanceSensor.get("lineDetector");
        frontRightMotor = hardwareMap.dcMotor.get("frontrightMotor");
        backRightMotor = hardwareMap.dcMotor.get("backrightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontleftMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backleftMotor");
        lineDetector = hardwareMap.opticalDistanceSensor.get("lineDetector");
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);

        countsPerInch = (COUNTS_PER_REVOLUTION / (Math.PI * WHEEL_DIAMETER)) * GEAR_RATIO;

       // TurnOffAllDriveMotors();
       // SetEncoderTargets();
    }

    @Override
    public void start() {

        elapsedGameTime.reset();

        SetCurrentState(State.STATE_INITIAL);
       // UseRunToPosition();
    }

    @Override
    public void loop() {

        telemetry.addData("0", String.format("%4.1f ", elapsedTimeForCurrentState.time()) + currentState.toString());

        switch (currentState) {

            case STATE_INITIAL:

                    startPath(mBeaconPath);
                    SetCurrentState(State.STATE_DRIVE_TO_BEACON);
                    telemetry.addData("1", String.format("L %5d - R %5d ", getLeftPosition(),
                            getRightPosition() ));

                break;

            case STATE_DRIVE_TO_BEACON: // Follow mBeaconPath until last segment is completed
                if (pathComplete() || isOnWhiteLine())
                {
                    //lineDetector.enableLed(true);                 // Action: Enable Light Sensor
                    //setDriveSpeed(-0.1, 0.1);               // Action: Start rotating left
                    //startPath(locateLinePath);
                    TurnOffAllDriveMotors();
                    startPath(startOnLinePath);
                    SetCurrentState(State.STATE_FOLLOW_LINE);      // Next State:
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

            case STATE_FOLLOW_LINE:

                if (beaconIsReached()) {

                    TurnOffAllDriveMotors();
                    SetCurrentState(State.STATE_STOP);
                }

                if (isOnWhiteLine()) {

                    setPowerLevelsForLineFollowing(0.0f, 0.5f);

                } else {

                    setPowerLevelsForLineFollowing(0.5f, 0.0f);
                    /*telemetry.addData("1", String.format("%4.2f of %4.2f ",
                            lineDetector.getLightDetected(),
                            WHITE_THRESHOLD ));*/
                }

                break;

            case STATE_STOP:

                //if (pathComplete()) {

                   // TurnOffAllDriveMotors();
                   // telemetry.addData("StopComplete", "Stop Complete");
                //}

                TurnOffAllDriveMotors();
        }

        telemetry.addData("Left: ", currentEncoderTargets.LeftTarget);
        telemetry.addData("Right: ", currentEncoderTargets.RightTarget);
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

    public void setDriveMode(DcMotorController.RunMode mode) {

        if (frontLeftMotor.getChannelMode() != mode)
            frontRightMotor.setChannelMode(mode);
        if (backLeftMotor.getChannelMode() != mode)
            backRightMotor.setChannelMode(mode);
        if (frontRightMotor.getChannelMode() != mode)
            frontLeftMotor.setChannelMode(mode);
        if (backRightMotor.getChannelMode() != mode)
            backLeftMotor.setChannelMode(mode);
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
        backRightMotor.setPower(levels.backLeft);
        frontLeftMotor.setPower(levels.backRight);
        backLeftMotor.setPower(levels.frontRight);
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

    private void TurnOffAllDriveMotors() {
        SetDriveMotorPowerLevels(zeroPowerLevels);
    }

    private void SetEncoderTargets() {
        frontLeftMotor.setTargetPosition(currentEncoderTargets.LeftTarget);
        backLeftMotor.setTargetPosition(currentEncoderTargets.LeftTarget);
        frontRightMotor.setTargetPosition(currentEncoderTargets.RightTarget);
        backRightMotor.setTargetPosition(currentEncoderTargets.RightTarget);
    }

    public void UseConstantSpeed()
    {
        setDriveMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
    }

    public boolean beaconIsReached() {
        //TODO Use an actual test for this
        return elapsedTimeForCurrentState.time() >= 4.0f;
    }

    public void setPowerLevelsForLineFollowing(float leftPower, float rightPower) {

        frontLeftMotor.setPower(leftPower);
        backLeftMotor.setPower(leftPower);
        frontRightMotor.setPower(rightPower);
        backRightMotor.setPower(rightPower);
    }
}