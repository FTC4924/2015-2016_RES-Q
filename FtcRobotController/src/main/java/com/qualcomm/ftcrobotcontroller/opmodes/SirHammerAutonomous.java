package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;
import com.qualcomm.ftcrobotcontroller.EncoderTargets;
import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.ftcrobotcontroller.SirHammerAutonomousSensors;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;

import static com.qualcomm.ftcrobotcontroller.opmodes.SirHammerAutonomous.CenterColumnPosition.*;
import static com.qualcomm.ftcrobotcontroller.opmodes.SirHammerAutonomous.State.*;

/**
 * Created by 4924_Users on 9/27/2015.
 */
public class SirHammerAutonomous extends OpMode {

    // A list of system States.
    public enum State
    {
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
    public enum CenterColumnPosition { UNDETECTED, ONE, TWO, THREE }
    private FourWheelDrivePowerLevels zeroPowerLevels = new FourWheelDrivePowerLevels(0.0f, 0.0f);
    private EncoderTargets zeroEncoderTargets = new EncoderTargets(0, 0);
    public ElapsedTime elapsedGameTime = new ElapsedTime();
    private ElapsedTime elapsedTimeForCurrentState = new ElapsedTime();

    // Define driving paths as pairs of relative wheel movements in inches (left,right) plus speed %
    // Note: this is a dummy path, and is NOT likely to actually work with YOUR robot.
    final DrivePathSegment[] mBeaconPath = {
            new DrivePathSegment(  0.0f,  3.0f, 0.2f),  // Left
            new DrivePathSegment( 60.0f, 60.0f, 0.9f),  // Forward
            new DrivePathSegment(  1.0f,  0.0f, 0.2f),  // Left
    };

    final DrivePathSegment[] mMountainPath = {
            new DrivePathSegment(  0.0f, -3.0f, 0.2f),  // Left Rev
            new DrivePathSegment(-30.0f,-30.0f, 0.9f),  // Backup
            new DrivePathSegment(-16.0f,  0.0f, 0.7f),  // Right Rev
            new DrivePathSegment( 10.0f, 10.0f, 0.3f),  // Forward
    };

    final double COUNTS_PER_INCH = 240 ;    // Number of encoder counts per inch of wheel travel.

    DcMotor frontLeftMotor;
    DcMotor backLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backRightMotor;
    SirHammerAutonomousSensors sensors;
    CenterColumnPosition goalPosition;
    EncoderTargets currentEncoderTargets = zeroEncoderTargets;

    private State currentState;
    private int currentPathSegmentIndex;
    private DrivePathSegment[] currentPath;

    public State GetCurrentState() { return currentState; }
    public void SetCurrentState(State newState) {
        elapsedTimeForCurrentState.reset();
        currentState = newState;
    }

    @Override
    public void init() {
        goalPosition = UNDETECTED;

        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.REVERSE);
        sensors = new SirHammerAutonomousSensors();

        TurnOffAllDriveMotors();
        SetEncoderTargets();
    }

    @Override
    public void start()
    {
        TurnOffAllDriveMotors();
        UseRunToPosition();
        elapsedGameTime.reset();
        SetCurrentState(STATE_INITIAL);
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
                    startPath(mBeaconPath);                 // Action: Load path to beacon
                    SetCurrentState(State.STATE_DRIVE_TO_BEACON);  // Next State:
                }
                else
                {
                    // Display Diagnostic data for this state.
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
 /*
            case STATE_LOCATE_LINE:     // Rotate until white tape is detected
                if (mLight.getLightDetected() > WHITE_THRESHOLD)
                {
                    setDriveSpeed(0.0, 0.0);                // Action: Stop rotation
                    SetCurrentState(State.STATE_FOLLOW_LINE);      // Next State:
                }
                else
                {
                    // Display Diagnostic data for this state.
                    telemetry.addData("1", String.format("%4.2f of %4.2f ",
                            mLight.getLightDetected(),
                            WHITE_THRESHOLD ));
                }
                break;

            case STATE_FOLLOW_LINE:     // Track line until wall is reached
                if (mDistance.getLightDetected() > RANGE_THRESHOLD)
                {
                    useConstantPower();                     // Apply constant power to motors
                    setDriveSpeed(0.2, 0.2);                // Action: Drive Forward
                    SetCurrentState(State.STATE_SQUARE_TO_WALL);   // Next State:
                }
                else
                {
                    // Steer left ot right
                    if (mLight.getLightDetected() > WHITE_THRESHOLD)
                    {
                        setDriveSpeed(0.2, 0.0);            // Scan Right
                        telemetry.addData("1", String.format("%4.2f --> %7d : %7d (%4.2f)" ,
                                mLight.getLightDetected(),
                                getLeftPosition(), getRightPosition(),
                                mDistance.getLightDetected() ));
                    }
                    else
                    {
                        setDriveSpeed(0.0, 0.2);            // Scan Left
                        telemetry.addData("1", String.format("%4.2f <-- %7d : %7d (%4.2f)",
                                mLight.getLightDetected(),
                                getLeftPosition(), getRightPosition(),
                                mDistance.getLightDetected() ));
                    }
                }
                break;

            case STATE_SQUARE_TO_WALL:     // Push up against wall for 1 second
                if (mStateTime.time() > 1.0)
                {
                    setDriveSpeed(0.0, 0.0);                // Action: Stop pushing
                    mServo.setPosition(CLIMBER_DEPLOY);     // Action:  Start deploying climbers.
                    SetCurrentState(State.STATE_DEPLOY_CLIMBERS);  // Next State:
                }
                break;

            case STATE_DEPLOY_CLIMBERS:     // wait 2 seconds while servos move and deposit climbers
                if (mStateTime.time() > 2.0)
                {
                    mServo.setPosition(CLIMBER_RETRACT);    // Put servo into "starting position"
                    startPath(mMountainPath);               // Action: Load path to Mountain
                    SetCurrentState(State.STATE_DRIVE_TO_MOUNTAIN);// Next State:
                }
                break;

            case STATE_DRIVE_TO_MOUNTAIN: // Follow path until last segment is completed
                if (pathComplete())
                {
                    useConstantPower();                     // Action: Switch to constant Power
                    setDrivePower(0.5, 0.5);                // Action: Start Driving forward at 50 Power
                    newState(State.STATE_CLIMB_MOUNTAIN);   // Next State:
                }
                else
                {
                    // Display Diagnostic data for this state.
                    telemetry.addData("1", String.format("%d of %d. L %5d:%5d - R %5d:%5d ",
                            mCurrentSeg, mCurrentPath.length,
                            mLeftEncoderTarget, getLeftPosition(),
                            mRightEncoderTarget, getRightPosition()));
                }
                break;

            case STATE_CLIMB_MOUNTAIN:   // Drive up mountain for 5 seconds
                if (mStateTime.time() > 5.0)
                {
                    useConstantPower();                     // Switch to constant Power
                    setDrivePower(0, 0);                    // Set target speed to zero
                    SetCurrentState(State.STATE_STOP);             // Next State:
                }
                else
                {
                    // Display Diagnostic data for this state.
                    telemetry.addData("1", String.format("L %5d - R %5d ", getLeftPosition(),
                            getRightPosition() ));
                }
                break;
*/
            case STATE_STOP:
                break;
        }
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
        setEncoderTargetsToCurrentPosition();
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
    void addEncoderTarget(int leftEncoderAdder, int rightEncoderAdder)
    {
        currentEncoderTargets.LeftTarget += leftEncoderAdder;
        currentEncoderTargets.RightTarget += rightEncoderAdder;
    }

    private void setEncoderTargetsToCurrentPosition()
    {
        //	get and set the encoder targets
        currentEncoderTargets.LeftTarget = getLeftPosition();
        currentEncoderTargets.RightTarget = getRightPosition();
    }

    // Return Left Encoder count
    private int getLeftPosition()
    {
        return currentEncoderTargets.LeftTarget;
        //return frontLeftMotor.getCurrentPosition();
    }

    // Return Right Encoder count
    private int getRightPosition()
    {
        return currentEncoderTargets.RightTarget;
        //return frontRightMotor.getCurrentPosition();
    }

    // Return true if motors have both reached the desired encoder target
    private boolean moveComplete()
    {
        //  return (!mLeftMotor.isBusy() && !mRightMotor.isBusy());
        return (elapsedTimeForCurrentState.time() >= 2.0f);
        // return ((Math.abs(getLeftPosition() - currentEncoderTargets.LeftTarget) < 10) &&
        //        (Math.abs(getRightPosition() - currentEncoderTargets.RightTarget) < 10));
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
