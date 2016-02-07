package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;
import com.qualcomm.ftcrobotcontroller.EncoderTargets;
import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;

/**
 * Created by 4924_Users on 2/6/2016.
 */
/*
public class AutonomousBase extends OpMode {

    public enum State {
        STATE_INITIAL,
        STATE_STOP
    }

    public ArrayList<State> stateList = new ArrayList<State>();
    int stateIndex = 0;
    public ElapsedTime elapsedGameTime = new ElapsedTime();
    private FourWheelDrivePowerLevels zeroPowerLevels = new FourWheelDrivePowerLevels(0.0f, 0.0f);
    private ElapsedTime elapsedTimeForCurrentState = new ElapsedTime();
    private ElapsedTime elapsedTimeForCurrentSegment = new ElapsedTime();
    private EncoderTargets zeroEncoderTargets = new EncoderTargets(0, 0);
    final int COUNTS_PER_REVOLUTION = 1120;
    final double WHEEL_DIAMETER = 4.5f;
    final double GEAR_RATIO = 24.0f/16.0f;
    double countsPerInch;
    double mustacheMotorAngle = 0.0d;
    static final int ENCODER_TARGET_MARGIN = 10;
    static final float TURNING_ANGLE_MARGIN = 2.0f;
    static final float CALIBRATION_FACTOR = 1.414f;
    int turnStartValueLeft;
    int turnStartValueRight;

    DcMotor frontLeftMotor;
    DcMotor frontRightMotor;
    Servo rightsideservo; //rightsideservo is a
    Servo climberDeployer; //frontrightservo is a 180
    Servo ziplinerTripper;
    Servo deliveryBelt;
    Servo bumperServo;
    Servo gateServo;
    GyroSensor turningGyro;
    TouchSensor bumper;

    private State currentState;
    private int currentPathSegmentIndex = 0;
    private DrivePathSegment[] currentPath = beaconPath;
    DrivePathSegment segment = currentPath[currentPathSegmentIndex];
    EncoderTargets currentEncoderTargets = zeroEncoderTargets;

    public void SetCurrentState(State newState) {

        //elapsedTimeForCurrentState.reset();
        //currentState = newState;
    }

    @Override
    public void init() {

        frontRightMotor = hardwareMap.dcMotor.get("frontrightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontleftMotor");
        bumperServo = hardwareMap.servo.get("servo1");
        rightsideservo = hardwareMap.servo.get("servo2");
        deliveryBelt = hardwareMap.servo.get("servo3");             //continuous
        climberDeployer = hardwareMap.servo.get("servo4");
        ziplinerTripper = hardwareMap.servo.get("servo5");          //continuous
        gateServo = hardwareMap.servo.get("servo6");                //continuous?
        turningGyro = hardwareMap.gyroSensor.get("gyroSensor");
        bumper = hardwareMap.touchSensor.get("bumper");

        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);

        countsPerInch = (COUNTS_PER_REVOLUTION / (Math.PI * WHEEL_DIAMETER)) * GEAR_RATIO * CALIBRATION_FACTOR;

        turningGyro.calibrate();

        rightsideservo.setPosition(1.0d);
        climberDeployer.setPosition(1.0d);
        gateServo.setPosition(0.5d);
        ziplinerTripper.setPosition(0.5d);
        deliveryBelt.setPosition(0.5d);
    }

    @Override
    public void start() {

        elapsedGameTime.reset();
        SetCurrentState(State.STATE_INITIAL);
    }

    @Override
    public void loop() {

        rightsideservo.setPosition(1.0d);
        gateServo.setPosition(0.5d);
        ziplinerTripper.setPosition(0.5d);
        deliveryBelt.setPosition(0.5d);

        switch (currentState) {

            case STATE_INITIAL:

                if (!turningGyro.isCalibrating()) {

                    startPath(beaconPath);
                    SetCurrentState(State.STATE_DRIVE_TO_BEACON);
                    telemetry.addData("1", String.format("L %5d - R %5d ", getLeftPosition(),
                            getRightPosition()));
                }

                break;

            case STATE_DRIVE_TO_BEACON: // Follow mountainPath until last segment is completed

                if (pathComplete()) {

                    //bumperServo.setPosition(BUMPER_DEPLOYED_ANGLE);
                    TurnOffAllDriveMotors();
                    runWithoutEncoders();
                    SetCurrentState(State.STATE_APPROACH_BEACON);      // Next State:
                }

                break;

            case STATE_DEPLOY_CLIMBERS:

                if (elapsedTimeForCurrentState.time() >= 2.0f) {

                    SetCurrentState(State.STATE_STOP);

                } else {

                }

            case STATE_STOP:

                TurnOffAllDriveMotors();
                frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
                frontLeftMotor.setDirection(DcMotor.Direction.FORWARD);

                break;
        }

        SetEncoderTargets();
        addTelemetry();

        if (elapsedGameTime.time() >= 30.0f) {

            TurnOffAllDriveMotors();
            runWithoutEncoders();
            SetCurrentState(State.STATE_STOP);
        }

        if (elapsedGameTime.time() >= 2.0f) {

            mustacheMotorAngle = 0.5f;
        }
    }

    @Override
    public void stop() {

        frontLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
    }

    private void addTelemetry() {

        telemetry.addData("L Target: ", currentEncoderTargets.LeftTarget);
        telemetry.addData("L Pos: ", getLeftPosition());
        telemetry.addData("R Target: ", currentEncoderTargets.RightTarget);
        telemetry.addData("R Pos: ", getRightPosition());
        telemetry.addData("State: ", currentState);
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
        useRunUsingEncoders();
        startSeg();
    }

    private void setEncoderTargetsToCurrentPosition() {

        currentEncoderTargets.LeftTarget = getLeftPosition();
        currentEncoderTargets.RightTarget = getRightPosition();
    }

    public void useRunUsingEncoders() {

        setDriveMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
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

        elapsedTimeForCurrentSegment.reset();

        if (currentPath != null) {

            if (segment.isTurn) {

                turnStartValueLeft = getLeftPosition();
                turnStartValueRight = getRightPosition();

                runWithoutEncoders();

                if (segment.Angle < 0) {

                    FourWheelDrivePowerLevels powerLevels =
                            new FourWheelDrivePowerLevels(segment.Power, 0.0f);
                    SetDriveMotorPowerLevels(powerLevels);

                } else {

                    FourWheelDrivePowerLevels powerLevels =
                            new FourWheelDrivePowerLevels(0.0f, segment.Power);
                    SetDriveMotorPowerLevels(powerLevels);
                }

            } else {

                if (segment.isDelay) {

                    runWithoutEncoders();

                    FourWheelDrivePowerLevels powerLevels =
                            new FourWheelDrivePowerLevels(0.0f, 0.0f);
                    SetDriveMotorPowerLevels(powerLevels);

                } else {

                    useRunUsingEncoders();

                    Left  = (int)(segment.LeftSideDistance * countsPerInch);
                    Right = (int)(segment.RightSideDistance * countsPerInch);
                    addEncoderTarget(Left, Right);
                    FourWheelDrivePowerLevels powerLevels =
                            new FourWheelDrivePowerLevels(segment.Power, segment.Power);
                    SetDriveMotorPowerLevels(powerLevels);
                }
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
        if (segmentComplete()) {
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

        int leftPosition = getLeftPosition();
        int leftTarget = currentEncoderTargets.LeftTarget;
        int rightPosition = getRightPosition();
        int rightTarget = currentEncoderTargets.RightTarget;

        return (isPositionClose(leftPosition, leftTarget) &&
                isPositionClose(rightPosition, rightTarget)) ||
                (isPastTarget(leftPosition, leftTarget) &&
                        isPastTarget(rightPosition, rightTarget));
    }

    private boolean isPositionClose(int position, int target) {

        return Math.abs(position - target) < ENCODER_TARGET_MARGIN;
    }

    private boolean isPastTarget(int position, int target) {

        return position > target;
    }

    private void TurnOffAllDriveMotors() {
        SetDriveMotorPowerLevels(zeroPowerLevels);
    }

    private void SetEncoderTargets() {
        frontLeftMotor.setTargetPosition(currentEncoderTargets.LeftTarget);
        frontRightMotor.setTargetPosition(currentEncoderTargets.RightTarget);
    }

    public boolean turnComplete() {

        return Math.abs(segment.Angle) <= turningGyro.getHeading() + TURNING_ANGLE_MARGIN &&
                Math.abs(segment.Angle) >= turningGyro.getHeading() - TURNING_ANGLE_MARGIN;
    }

    public boolean segmentComplete() {

        if (segment.isTurn) {

            return turnComplete();

        } else {

            if (segment.isDelay) {

                return delayComplete();

            } else {

                return linearMoveComplete();
            }
        }
    }

    private boolean delayComplete() {

        return elapsedTimeForCurrentSegment.time() >= segment.delayTime;
    }

    private void transitionToNextState() {

        stateIndex++;
        SetCurrentState(stateList.get(stateIndex));
    }

}

*/