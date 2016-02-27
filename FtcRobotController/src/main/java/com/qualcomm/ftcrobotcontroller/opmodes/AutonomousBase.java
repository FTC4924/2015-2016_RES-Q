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

public abstract class AutonomousBase extends OpMode {

    public enum State {
        STATE_INITIAL,
        STATE_DRIVE_TO_BEACON,
        STATE_APPROACH_BEACON,
        STATE_DEPLOY_CLIMBERS,
        STATE_DRIVE_TO_MOUNTAIN,
        STATE_CLIMB_MOUNTAIN,
        STATE_MOVE_TO_FLOOR_GOAL,
        STATE_STOP
    }

    public ArrayList<State> stateList = new ArrayList<State>();
    int stateIndex = 0;
    public ElapsedTime elapsedGameTime = new ElapsedTime();
    public FourWheelDrivePowerLevels zeroPowerLevels = new FourWheelDrivePowerLevels(0.0f, 0.0f);
    public ElapsedTime elapsedTimeForCurrentState = new ElapsedTime();
    public ElapsedTime elapsedTimeForCurrentSegment = new ElapsedTime();
    public EncoderTargets zeroEncoderTargets = new EncoderTargets(0, 0);
    final int COUNTS_PER_REVOLUTION = 1120;
    final double WHEEL_DIAMETER = 4.5f;
    final double GEAR_RATIO = 24.0f/16.0f;
    double countsPerInch;
    double mustacheMotorAngle = 0.0d;
    static final int ENCODER_TARGET_MARGIN = 10;
    static final float TURNING_ANGLE_MARGIN = 2.0f;
    static final float CALIBRATION_FACTOR = 1.414f;
    static final float CLIMBER_ARM_DEPLOYED_ANGLE = 0.0f;
    static final float CLIMBER_ARM_FOLDED_ANGLE = 1.0f;
    static final float BUMPER_DEPLOYED_ANGLE = 0.0f;
    static final float BUMPER_FOLDED_ANGLE = 0.6f;
    static final float LEFT_MOTOR_POWER_ADJUST = -0.1f;
    int turnStartValueLeft;
    int turnStartValueRight;

    DcMotor frontLeftMotor;
    DcMotor frontRightMotor;
    DcMotor collectMotor;
    Servo rightsideservo; //rightsideservo is a
    Servo climberDeployer; //frontrightservo is a 180
    Servo ziplinerTripper;
    Servo deliveryBelt;
    Servo bumperServo;
    Servo gateServo;
    GyroSensor turningGyro;
    TouchSensor bumper;

    public State currentState;
    public int currentPathSegmentIndex = 0;
    public DrivePathSegment[] currentPath;
    DrivePathSegment segment;
    EncoderTargets currentEncoderTargets = zeroEncoderTargets;

    public void SetCurrentState(State newState) {

        elapsedTimeForCurrentState.reset();
        currentState = newState;
    }

    @Override
    public void init() {

        frontRightMotor = hardwareMap.dcMotor.get("frontrightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontleftMotor");
        collectMotor = hardwareMap.dcMotor.get("collection");
        bumperServo = hardwareMap.servo.get("servo1");
        rightsideservo = hardwareMap.servo.get("servo2");
        deliveryBelt = hardwareMap.servo.get("servo3");             //continuous
        climberDeployer = hardwareMap.servo.get("servo4");
        ziplinerTripper = hardwareMap.servo.get("servo5");          //continuous
        gateServo = hardwareMap.servo.get("servo6");                //continuous?
        turningGyro = hardwareMap.gyroSensor.get("gyroSensor");
        bumper = hardwareMap.touchSensor.get("bumper");

        setReversedMotor();

        countsPerInch = (COUNTS_PER_REVOLUTION / (Math.PI * WHEEL_DIAMETER)) * GEAR_RATIO * CALIBRATION_FACTOR;

        turningGyro.calibrate();

        rightsideservo.setPosition(1.0d);
        climberDeployer.setPosition(CLIMBER_ARM_FOLDED_ANGLE);
        gateServo.setPosition(0.5d);
        ziplinerTripper.setPosition(0.5d);
        deliveryBelt.setPosition(0.5d);
        bumperServo.setPosition(BUMPER_FOLDED_ANGLE);
    }

    @Override
    public void start() {

        elapsedGameTime.reset();
        SetCurrentState(State.STATE_INITIAL);
        //collectMotor.setPower(1.0f);
        addStates();
    }

    @Override
    public void loop() {

    }

    @Override
    public void stop() {

        frontLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
    }

    public void addTelemetry() {

        telemetry.addData("L Target: ", currentEncoderTargets.LeftTarget);
        telemetry.addData("L Pos: ", getLeftPosition());
        telemetry.addData("R Target: ", currentEncoderTargets.RightTarget);
        telemetry.addData("R Pos: ", getRightPosition());
        telemetry.addData("State: ", currentState);
    }

    public int getRightPosition() {

        return frontRightMotor.getCurrentPosition();
    }

    public int getLeftPosition() {

        return frontLeftMotor.getCurrentPosition();
    }

    public void startPath(DrivePathSegment[] path) {

        currentPath = path;
        currentPathSegmentIndex = 0;
        setEncoderTargetsToCurrentPosition();
        useRunUsingEncoders();
        startSeg();
    }

    public void setEncoderTargetsToCurrentPosition() {

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

    public void startSeg() {

        segment = currentPath[currentPathSegmentIndex];

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

                    int moveCounts  = (int)(segment.LeftSideDistance * countsPerInch);
                    float power = segment.Power;

                    useRunUsingEncoders();
                    addEncoderTarget(moveCounts, moveCounts);

                    if (moveCounts < 0) {

                        power *= -1;
                    }

                    FourWheelDrivePowerLevels powerLevels =
                            new FourWheelDrivePowerLevels(power, power);
                    SetDriveMotorPowerLevels(powerLevels);
                }
            }

            currentPathSegmentIndex++;
        }
    }

    public void addEncoderTarget(int leftEncoderAdder, int rightEncoderAdder) {

        currentEncoderTargets.LeftTarget += leftEncoderAdder;
        currentEncoderTargets.RightTarget += rightEncoderAdder;
    }

    public void SetDriveMotorPowerLevels(FourWheelDrivePowerLevels levels) {

        frontRightMotor.setPower(levels.frontRight);
        frontLeftMotor.setPower(levels.frontLeft + LEFT_MOTOR_POWER_ADJUST);
    }

    public boolean pathComplete() {
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

    public boolean linearMoveComplete() {

        int leftPosition = getLeftPosition();
        int leftTarget = currentEncoderTargets.LeftTarget;
        int rightPosition = getRightPosition();
        int rightTarget = currentEncoderTargets.RightTarget;

        return (isPositionClose(leftPosition, leftTarget, segment.LeftSideDistance) &&
                isPositionClose(rightPosition, rightTarget, segment.LeftSideDistance)) ||
                (isPastTarget(leftPosition, leftTarget, segment.LeftSideDistance) &&
                        isPastTarget(rightPosition, rightTarget, segment.LeftSideDistance));
    }

    public boolean isPositionClose(int position, int target, float distanceToMove) {

        if (distanceToMove < 0) {

            return position - target < ENCODER_TARGET_MARGIN;
        }

        return target - position < ENCODER_TARGET_MARGIN;
    }

    public boolean isPastTarget(int position, int target, float distanceToMove) {

        if (distanceToMove < 0) {

            return position < target;
        }

        return position > target;
    }

    public void TurnOffAllDriveMotors() {
        SetDriveMotorPowerLevels(zeroPowerLevels);
    }

    public void SetEncoderTargets() {
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

    public boolean delayComplete() {

        return elapsedTimeForCurrentSegment.time() >= segment.delayTime;
    }

    public void transitionToNextState() {

        stateIndex++;
        SetCurrentState(stateList.get(stateIndex));
    }

    public void initServos() {

        rightsideservo.setPosition(1.0d);
        gateServo.setPosition(0.5d);
        ziplinerTripper.setPosition(0.5d);
        deliveryBelt.setPosition(0.5d);
    }

    public abstract void addStates();

    public abstract void setReversedMotor();
}