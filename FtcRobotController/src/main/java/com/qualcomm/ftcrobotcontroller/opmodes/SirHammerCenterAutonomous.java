package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;
import com.qualcomm.ftcrobotcontroller.EncoderTargets;
import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.ftcrobotcontroller.SirHammerAutonomousSensors;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;

import static com.qualcomm.ftcrobotcontroller.opmodes.SirHammerCenterAutonomous.CenterColumnPosition.*;
import static com.qualcomm.ftcrobotcontroller.opmodes.SirHammerCenterAutonomous.State.*;

/**
 * Created by 4924_Users on 9/27/2015.
 */
public class SirHammerCenterAutonomous extends OpMode {

    // A list of system States.
    public enum State
    {
        STATE_INITIAL,
        STATE_DRIVE_TO_READ_IR,
        STATE_READING_IR,
        STATE_DRIVING_TO_POSITION,
        STATE_DRIVING_TO_TOUCH_CENTER,
        STATE_RAISING_ARM,
        STATE_LOWERING_ARM,
        STATE_POSITIONING_FOR_KICKSTAND,
        STATE_KICKSTAND_MOVE_AND_TURN,
        STATE_STOP
    }

    public enum CenterColumnPosition { UNDETECTED, ONE, TWO, THREE }
    private FourWheelDrivePowerLevels zeroPowerLevels = new FourWheelDrivePowerLevels(0.0f, 0.0f);
    private EncoderTargets zeroEncoderTargets = new EncoderTargets(0, 0);
    public ElapsedTime elapsedGameTime = new ElapsedTime();
    private ElapsedTime elapsedTimeForCurrentState = new ElapsedTime();

    // Define driving paths as pairs of relative wheel movements in inches (left,right) plus speed %
    // Note: this is a dummy path, and is NOT likely to actually work with YOUR robot.
    final DrivePathSegment[] centerReadingPath = {
            new DrivePathSegment(  20.0f, 20.0f, 0.8f)
    };
    final DrivePathSegment[] position1Path = {
            new DrivePathSegment( -5.0f, -5.0f, 0.8f),      // backup
            new DrivePathSegment(-14.1f,14.1f, 0.8f),         // should be left 90 degree turn
            new DrivePathSegment(10.0f,  10.0f, 0.8f),      // forward
            new DrivePathSegment(14.1f, -14.1f, 0.8f)         // should be right 90 degree turn
    };
    final DrivePathSegment[] touchCenterPath = {
            new DrivePathSegment( 5.0f, 5.0f, 0.4f),      // ease forward
    };
    final DrivePathSegment[] kickstandSetupPath = {
            new DrivePathSegment( -5.0f, -5.0f, 0.8f),      // backup
            new DrivePathSegment(5.0f,-5.0f, 0.8f),         // turn
            new DrivePathSegment(10.0f,  10.0f, 0.8f),      // forward
            new DrivePathSegment(-5.0f, 5.0f, 0.8f)         // turn
    };
    final DrivePathSegment[] kickstandTurnAndPullPath = {
            new DrivePathSegment( 15.0f, 15.0f, 0.8f),
            new DrivePathSegment(10.0f,-10.0f, 0.8f)
    };
    final double COUNTS_PER_INCH = 116.279f ;    // Number of encoder counts per inch of wheel travel.

    DcMotor frontLeftMotor;
    DcMotor backLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backRightMotor;
    DcMotorController leftController;
    DcMotorController rightController;

    SirHammerAutonomousSensors sensors;
    CenterColumnPosition goalPosition;
    EncoderTargets currentEncoderTargets = zeroEncoderTargets;
    int loopCountSinceLastSwitchToWriteMode = 0;
    DcMotorController.DeviceMode leftDeviceMode;
    DcMotorController.DeviceMode rightDeviceMode;

    private State currentState;
    private int currentPathSegmentIndex;
    private DrivePathSegment[] currentPath;
    private CenterColumnPosition detectedCenterPosition = CenterColumnPosition.UNDETECTED;
    public State GetCurrentState() { return currentState; }
    public void SetCurrentState(State newState) {
        elapsedTimeForCurrentState.reset();
        currentState = newState;
    }
    private int lastLeftEncoderReading;
    private int lastRightEncoderReading;
    static final int ENCODERMARGIN = 10;
    static final int LOOPSBEFOREWRITEMODE = 17;
    private DcMotorController wheelController;

    @Override
    public void init() {
        goalPosition = UNDETECTED;

        leftController = hardwareMap.dcMotorController.get("leftController");
        rightController = hardwareMap.dcMotorController.get("rightController");

        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.REVERSE);
        sensors = new SirHammerAutonomousSensors();

        lastLeftEncoderReading = 0;
        lastRightEncoderReading = 0;

        TurnOffAllDriveMotors();
        SetEncoderTargets();
        UseRunToPosition();
    }

    @Override
    public void start()
    {
        elapsedGameTime.reset();
        SetCurrentState(STATE_INITIAL);
        SwitchToReadMode();
    }

    @Override
    public void loop() {
        telemetry.addData("0", String.format("%4.1f ", elapsedTimeForCurrentState.time()) + currentState.toString());

        // Execute the current state.  Each STATE's case code does the following:
        // 1: Look for an EVENT that will cause a STATE change
        // 2: If an EVENT is found, take any required ACTION, and then set the next STATE
        //   else
        // 3: If no EVENT is found, do processing for the current STATE and send TELEMETRY data for STATE.
        switch (currentState)
        {
            case STATE_INITIAL:         // Stay in this state until encoders are both Zero.
                if (encodersAtZero())
                {
                    if (allowedToWrite()) {
                        startPath(centerReadingPath);
                        SetCurrentState(State.STATE_DRIVE_TO_READ_IR);
                    }       // otherwise wait for a later cycle
                }
                else
                {
                    // Display Diagnostic data for this state.
                    telemetry.addData("1", String.format("L %5d - R %5d ", getLeftPosition(),
                            getRightPosition() ));
                }
                break;


            case STATE_DRIVE_TO_READ_IR: // Follow path until last segment is completed
                if (pathComplete())
                {
                    ReadCenterPosition();
                    SetCurrentState(State.STATE_READING_IR);      // Next State:
                }
                break;

            case STATE_READING_IR:
                switch (detectedCenterPosition) {
                    case ONE:
                        if (allowedToWrite()) {
                            startPath(position1Path);
                            SetCurrentState(State.STATE_DRIVING_TO_POSITION);
                        }
                        break;
                    case TWO:
                        if (allowedToWrite()) {
                            startPath(position1Path);
                            SetCurrentState(State.STATE_DRIVING_TO_POSITION);
                        }
                        break;
                    case THREE:
                        if (allowedToWrite()) {
                            startPath(position1Path);
                            SetCurrentState(State.STATE_DRIVING_TO_POSITION);
                        }
                        break;
                    default:
                        ReadCenterPosition();
                        break;
                }
                break;

            case STATE_DRIVING_TO_POSITION:
                if (pathComplete())
                {
                    if (allowedToWrite()) {
                        startPath(touchCenterPath);
                        SetCurrentState(State.STATE_DRIVING_TO_TOUCH_CENTER);      // Next State:
                    }
                }
                break;
            case STATE_DRIVING_TO_TOUCH_CENTER:
                if (pathComplete())
                {
                    if (allowedToWrite()) {
                        TurnOffAllDriveMotors();
                        SetCurrentState(State.STATE_STOP);      // Next State:
                    }
                }
                break;
            case STATE_RAISING_ARM:
                SetCurrentState(STATE_LOWERING_ARM);
                break;
            case STATE_LOWERING_ARM:
                if (allowedToWrite()) {
                    startPath(kickstandSetupPath);
                    SetCurrentState(STATE_POSITIONING_FOR_KICKSTAND);
                }
                break;
            case STATE_POSITIONING_FOR_KICKSTAND:
                if (pathComplete()) {
                    if (allowedToWrite()) {
                        LowerKickstandArm();
                        startPath(kickstandTurnAndPullPath);
                        SetCurrentState(STATE_KICKSTAND_MOVE_AND_TURN);
                    }
                }
                break;
            case STATE_KICKSTAND_MOVE_AND_TURN:
                if (pathComplete()) {
                    if (allowedToWrite()) {
                        TurnOffAllDriveMotors();
                        SetCurrentState(STATE_STOP);
                    }
                }
                break;
            case STATE_STOP:
                if (allowedToWrite()) {
                    TurnOffAllDriveMotors();
                }
                break;
        }
        if (allowedToWrite())
            SetEncoderTargets();

        if (loopCountSinceLastSwitchToWriteMode % LOOPSBEFOREWRITEMODE == 0){
            SwitchToWriteMode();
        }

        // Every 17 loops, switch to read mode so we can read data from the NXT device.
        // Only necessary on NXT devices.
        if (loopCountSinceLastSwitchToWriteMode >= 5) {
            if (leftController.getMotorControllerDeviceMode() == DcMotorController.DeviceMode.READ_ONLY) {
                lastLeftEncoderReading = frontLeftMotor.getCurrentPosition();
                leftController.setMotorControllerDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
                loopCountSinceLastSwitchToWriteMode = 0;
            }
            if (rightController.getMotorControllerDeviceMode() == DcMotorController.DeviceMode.READ_ONLY) {
                lastRightEncoderReading = frontRightMotor.getCurrentPosition();
                rightController.setMotorControllerDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
                loopCountSinceLastSwitchToWriteMode = 0;
            }
        }

        // Update the current devModes
        // leftDeviceMode = leftController.getMotorControllerDeviceMode();
        // rightDeviceMode = rightController.getMotorControllerDeviceMode();
        loopCountSinceLastSwitchToWriteMode++;
    }

    private void SwitchToReadMode() {
        leftController.setMotorControllerDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
        rightController.setMotorControllerDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
    }

    private void SwitchToWriteMode() {
        leftController.setMotorControllerDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
        rightController.setMotorControllerDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
    }

    private void LowerKickstandArm() {
        // TODO - make this talk to the servo
    }
    
    private void ReadCenterPosition() {
        // TODO - actually detect a position
        detectedCenterPosition = CenterColumnPosition.ONE;
    }

    public void UseRunToPosition()
    {
        setDriveMode(DcMotorController.RunMode.RUN_TO_POSITION);
    }

    public void UseConstantSpeed()
    {
        setDriveMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
    }

    public void setDriveMode(DcMotorController.RunMode mode)
    {
        // Ensure the motors are in the correct mode.
        //if (frontLeftMotor.getChannelMode() != mode)
            frontLeftMotor.setChannelMode(mode);
        //if (backLeftMotor.getChannelMode() != mode)
            backLeftMotor.setChannelMode(mode);
        //if (frontRightMotor.getChannelMode() != mode)
            frontRightMotor.setChannelMode(mode);
        //if (backRightMotor.getChannelMode() != mode)
            backRightMotor.setChannelMode(mode);
    }

    /*
    Begin the first leg of the path array that is passed in.
    Calls startSeg() to actually load the encoder targets.
 */
    private void startPath(DrivePathSegment[] path)
    {
        currentPath = path;
        currentPathSegmentIndex = 0;
        //setEncoderTargetsToCurrentPosition();
        UseRunToPosition();
        startSeg();             // Execute the current (first) Leg
    }

    private boolean pathComplete()
    {
        // Wait for this Segement to end and then see what's next.
        if (moveComplete())
        {
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

    /*
        Starts the current leg of the current path.
        Must call startPath() once before calling this
        Each leg adds the new relative movement onto the running encoder totals.
        By not reading and using the actual encoder values, this avoids accumulating errors.
        Increments the leg number after loading the current encoder targets
     */
    private void startSeg()
    {
        int Left;
        int Right;

        if (currentPath != null)
        {
            // Load up the next motion based on the current segemnt.
            Left  = (int)(currentPath[currentPathSegmentIndex].LeftSideDistance * COUNTS_PER_INCH);
            Right = (int)(currentPath[currentPathSegmentIndex].RightSideDistance * COUNTS_PER_INCH);
            addEncoderTarget(Left, Right);
            FourWheelDrivePowerLevels powerLevels =
                    new FourWheelDrivePowerLevels(currentPath[currentPathSegmentIndex].Power,
                            currentPath[currentPathSegmentIndex].Power);
            SetDriveMotorPowerLevels(powerLevels);

            currentPathSegmentIndex++;
        }
    }

    //--------------------------------------------------------------------------
    // addEncoderTarget( LeftEncoder, RightEncoder);
    // Sets relative Encoder Position.  Offset current targets with passed data
    //--------------------------------------------------------------------------
    void addEncoderTarget(int leftEncoderAdder, int rightEncoderAdder) {

        currentEncoderTargets.LeftTarget += leftEncoderAdder;
        currentEncoderTargets.RightTarget += rightEncoderAdder;
    }

    //private void setEncoderTargetsToCurrentPosition() {
    //    //	get and set the encoder targets
    //    currentEncoderTargets.LeftTarget = getLeftPosition();
    //    currentEncoderTargets.RightTarget = getRightPosition();
    //}

    // Return Left Encoder count
    private int getLeftPosition() {
        // return currentEncoderTargets.LeftTarget;
        // return frontLeftMotor.getCurrentPosition();
        return lastLeftEncoderReading;
    }

    // Return Right Encoder count
    private int getRightPosition() {
        // return currentEncoderTargets.RightTarget;
        // return frontRightMotor.getCurrentPosition();
        return lastRightEncoderReading;
    }

    // Return true if motors have both reached the desired encoder target
    private boolean moveComplete() {
        //  return (!mLeftMotor.isBusy() && !mRightMotor.isBusy());
        // return (elapsedTimeForCurrentState.time() >= 10.0f);
        return ((Math.abs(getLeftPosition() - currentEncoderTargets.LeftTarget) < ENCODERMARGIN) &&
               (Math.abs(getRightPosition() - currentEncoderTargets.RightTarget) < ENCODERMARGIN));
    }

    // If the device is in either of these two modes, the op mode is allowed to write to the HW.
    private boolean allowedToWrite(){
        return ((leftController.getMotorControllerDeviceMode() == DcMotorController.DeviceMode.WRITE_ONLY) &&
                (rightController.getMotorControllerDeviceMode() == DcMotorController.DeviceMode.WRITE_ONLY));
    }

    //--------------------------------------------------------------------------
    // encodersAtZero()
    // Return true if both encoders read zero (or close)
    //--------------------------------------------------------------------------
    private boolean encodersAtZero()
    {
        return ((Math.abs(getLeftPosition()) < 5) && (Math.abs(getRightPosition()) < 5));
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

    private void SetDriveMotorPowerLevels(FourWheelDrivePowerLevels levels) {
        frontLeftMotor.setPower(levels.frontLeft);
        backLeftMotor.setPower(levels.backLeft);
        backRightMotor.setPower(levels.backRight);
        frontRightMotor.setPower(levels.frontRight);
    }
}
