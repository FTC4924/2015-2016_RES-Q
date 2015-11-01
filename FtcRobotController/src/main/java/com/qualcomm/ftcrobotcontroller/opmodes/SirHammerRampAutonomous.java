package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.EncoderTargets;
import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.ftcrobotcontroller.LegacyDrivePathSegment;
import com.qualcomm.ftcrobotcontroller.SirHammerAutonomousSensors;
import com.qualcomm.ftcrobotcontroller.SirHammerServoAngleCalculator;
import com.qualcomm.ftcrobotcontroller.SirHammerServoAngles;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.Servo;

import static com.qualcomm.ftcrobotcontroller.opmodes.SirHammerRampAutonomous.State.*;

/**
 * Created by 4924_Users on 10/31/2015.
 */
public class SirHammerRampAutonomous extends OpMode {

    // A list of system States.
    public enum State
    {
        STATE_INITIAL,
        STATE_DRIVE_DOWN_RAMP,
        STATE_SQUARE_TO_GOAL,
        STATE_DUMPING_AUTONOMOUS_BALL_AND_GRABBING_GOAL,
        STATE_DRIVING_TO_PARKING,
        STATE_STOP
    }

    private FourWheelDrivePowerLevels zeroPowerLevels = new FourWheelDrivePowerLevels(0.0f, 0.0f);
    private EncoderTargets zeroEncoderTargets = new EncoderTargets(0, 0);
    public ElapsedTime elapsedGameTime = new ElapsedTime();
    private ElapsedTime elapsedTimeForCurrentState = new ElapsedTime();
    private ElapsedTime elapsedTimeForPathSegment = new ElapsedTime();

    // Define driving paths as pairs of relative wheel movements in inches (left,right) plus speed %
    // Note: this is a dummy path, and is NOT likely to actually work with YOUR robot.
    final LegacyDrivePathSegment[] rampPath = {
            new LegacyDrivePathSegment(  90.0f, 90.0f, 1.0f, 4.0f)
    };
    final LegacyDrivePathSegment[] squaringPath = {
            new LegacyDrivePathSegment( 0.0f, 0.0f, 0.8f, 3.0f)
    };
    final LegacyDrivePathSegment[] dumpingBallPath = {
            new LegacyDrivePathSegment( 0.0f, 0.0f, 0.0f, 3.0f)
    };
    final LegacyDrivePathSegment[] drivingToParking = {
            new LegacyDrivePathSegment( 0.0f, 0.0f, 0.5f, 3.0f),
            new LegacyDrivePathSegment( 0.0f, 0.0f, 0.5f, 7.0f)
    };

    final double COUNTS_PER_INCH = 116.279f ;    // Number of encoder counts per inch of wheel travel.

    DcMotor frontLeftMotor;
    DcMotor backLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backRightMotor;
    Servo pinServo;
    Servo flapServo;
    Servo kickStandServo;
    Servo backLeftArmServo;
    Servo backDunkServo;

    EncoderTargets currentEncoderTargets = zeroEncoderTargets;

    SirHammerServoAngles servoAngles = new SirHammerServoAngles();

    private State currentState;
    private int currentPathSegmentIndex;
    private LegacyDrivePathSegment[] currentPath;
    public void SetCurrentState(State newState) {
        elapsedTimeForCurrentState.reset();
        currentState = newState;
    }
    private float segmentTime = 0.0f;

    @Override
    public void init() {
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");

        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);

        pinServo = hardwareMap.servo.get("pinServo");
        flapServo = hardwareMap.servo.get("flapServo");
        kickStandServo = hardwareMap.servo.get("kickStandArmServo");
        backLeftArmServo = hardwareMap.servo.get("backLeftArmServo");
        backDunkServo = hardwareMap.servo.get("backDunkServo");
        kickStandServo.setPosition(SirHammerServoAngleCalculator.KICKSTAND_DOCKED_ANGLE);
        backLeftArmServo.setPosition(SirHammerServoAngleCalculator.BACK_LEFT_ARM_RAISED_ANGLE);
        backDunkServo.setPosition(SirHammerServoAngleCalculator.DUNK_ARM_DOCKED_ANGLE);

        TurnOffAllDriveMotors();

        SetEncoderTargets();
        UseRunToPosition();

    }

    @Override
    public void start()
    {
        elapsedGameTime.reset();
        SetCurrentState(STATE_INITIAL);
        //SwitchToReadMode();
    }

    @Override
    public void loop() {
        // Execute the current state.  Each STATE's case code does the following:
        // 1: Look for an EVENT that will cause a STATE change
        // 2: If an EVENT is found, take any required ACTION, and then set the next STATE
        //   else
        // 3: If no EVENT is found, do processing for the current STATE and send TELEMETRY data for STATE.
        switch (currentState)
        {
            case STATE_INITIAL:         // Stay in this state until encoders are both Zero.
                startPath(rampPath);
                SetCurrentState(State.STATE_DRIVE_DOWN_RAMP);
                break;

            case STATE_DRIVE_DOWN_RAMP: // Follow path until last segment is completed
                if (pathComplete())
                {
                    startPath(squaringPath);
                    SetCurrentState(State.STATE_SQUARE_TO_GOAL);      // Next State:
                }
                break;

            case STATE_SQUARE_TO_GOAL:
                if (pathComplete())
                {
                    startPath(dumpingBallPath);
                    dumpAutonomousBall();
                    grabScoringTube();
                    SetCurrentState(STATE_DUMPING_AUTONOMOUS_BALL_AND_GRABBING_GOAL);      // Next State:
                }
                break;

            case STATE_DUMPING_AUTONOMOUS_BALL_AND_GRABBING_GOAL:
                if (pathComplete()) {
                    dockAutonomousBallArm();
                    releaseScoringTube();
                    startPath(drivingToParking);
                    SetCurrentState(STATE_DRIVING_TO_PARKING);
                }
                break;

            case STATE_DRIVING_TO_PARKING:
                if (pathComplete()) {
                    TurnOffAllDriveMotors();
                    SetCurrentState(STATE_STOP);
                }
                break;

            case STATE_STOP:
                TurnOffAllDriveMotors();
                break;
        }

        SetEncoderTargets();
        SetServoAngles();

        telemetry.addData("State: ", currentState);
    }

    private void SetServoAngles() {
        backDunkServo.setPosition(servoAngles.DunkingArmAngle);
        backLeftArmServo.setPosition(servoAngles.BackLeftArmAngle);
    }

    private void dumpAutonomousBall() {
        servoAngles.DunkingArmAngle = SirHammerServoAngleCalculator.DUNK_ARM_DUNKING_ANGLE;
    }

    private void dockAutonomousBallArm() {
        servoAngles.DunkingArmAngle = SirHammerServoAngleCalculator.DUNK_ARM_DOCKED_ANGLE;
    }

    private void grabScoringTube() {
        servoAngles.BackLeftArmAngle= SirHammerServoAngleCalculator.BACK_LEFT_ARM_LOWERED_ANGLE;
    }

    private void releaseScoringTube() {
        servoAngles.BackLeftArmAngle= SirHammerServoAngleCalculator.BACK_LEFT_ARM_RAISED_ANGLE;
    }

    private void LowerKickstandArm() {
        servoAngles.KickStandAngle = SirHammerServoAngleCalculator.KICKSTAND_EXTENDED_ANGLE;
    }

    private void RaiseKickstandArm() {
        servoAngles.KickStandAngle = SirHammerServoAngleCalculator.KICKSTAND_DOCKED_ANGLE;
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
    private void startPath(LegacyDrivePathSegment[] path)
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
            elapsedTimeForPathSegment.reset();
            // Load up the next motion based on the current segemnt.
            Left  = (int)(currentPath[currentPathSegmentIndex].LeftSideDistance * COUNTS_PER_INCH);
            Right = (int)(currentPath[currentPathSegmentIndex].RightSideDistance * COUNTS_PER_INCH);
            segmentTime = currentPath[currentPathSegmentIndex].Time;
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

    // Return true if motors have both reached the desired encoder target
    private boolean moveComplete() {
        //  return (!mLeftMotor.isBusy() && !mRightMotor.isBusy());
        return (elapsedTimeForPathSegment.time() >= segmentTime);
        //return ((Math.abs(getLeftPosition() - currentEncoderTargets.LeftTarget) < ENCODERMARGIN) &&
        //       (Math.abs(getRightPosition() - currentEncoderTargets.RightTarget) < ENCODERMARGIN));
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
