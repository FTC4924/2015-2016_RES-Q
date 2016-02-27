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

/**
 * Created by 4924_Users on 10/23/2015.
 */

//Thanks to team #2818 for the base template of this autonomous program.
//We created the turning and delay DrivePathSegment variants.

public class DeviClimbBase extends OpMode {

    public enum State {
        STATE_INITIAL,
        STATE_DRIVE_TO_MOUNTAIN,
        STATE_CLIMB_MOUNTAIN,
        STATE_STOP
    }

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
    static final float BUMPER_FOLDED_ANGLE = 0.6f;
    static final float CLIMBER_ARM_FOLDED_ANGLE = 1.0f;
    static float climbingTime = 8.0f;
    int turnStartValueLeft;
    int turnStartValueRight;

    DcMotor frontLeftMotor;
    DcMotor frontRightMotor;
    DcMotor collectmotor;
    Servo leftsideservo; //leftsideservo is a 180
    Servo rightsideservo; //rightsideservo is a
    Servo climberDeployer; //frontrightservo is a 180
    Servo ziplinerTripper;
    Servo deliveryBelt;
    Servo bumperServo;
    Servo gateServo;
    GyroSensor turningGyro;
    TouchSensor bumper;

    public DrivePathSegment[] mountainPath = {

            new DrivePathSegment(20.0f, 20.0f, 0.9f),
            new DrivePathSegment(315.0f, 0.7f),
            new DrivePathSegment(35.0f, 35.0f, 0.9f),
            new DrivePathSegment(-50.0f, 0.7f)
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
        bumperServo = hardwareMap.servo.get("servo1");
        rightsideservo = hardwareMap.servo.get("servo2");
        deliveryBelt = hardwareMap.servo.get("servo3");             //continuous
        climberDeployer = hardwareMap.servo.get("servo4");
        ziplinerTripper = hardwareMap.servo.get("servo5");          //continuous
        gateServo = hardwareMap.servo.get("servo6");                //continuous?
        turningGyro = hardwareMap.gyroSensor.get("gyroSensor");
        bumper = hardwareMap.touchSensor.get("bumper");
        collectmotor = hardwareMap.dcMotor.get("collection");

        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);

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

        collectmotor.setPower(-1.0f);
        elapsedGameTime.reset();
        SetCurrentState(State.STATE_INITIAL);
    }

    @Override
    public void loop() {

        rightsideservo.setPosition(1.0d);

        switch (currentState) {

            case STATE_INITIAL:

                if (!turningGyro.isCalibrating()) {

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
                    SetCurrentState(State.STATE_CLIMB_MOUNTAIN);      // Next State:
                }

                break;

            case STATE_CLIMB_MOUNTAIN:

                if (elapsedTimeForCurrentState.time() >= climbingTime) {

                    TurnOffAllDriveMotors();
                    SetCurrentState(State.STATE_STOP);

                } else {

                    setClimbingPowerLevels();
                }

                break;

            case STATE_STOP:

                TurnOffAllDriveMotors();
                collectmotor.setPower(0.0f);
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

    private void addTelemetry() {
        telemetry.addData("State Time: ", String.format("%4.1f ", elapsedTimeForCurrentState.time()) + currentState.toString());
        telemetry.addData("Elapsed Time: ", String.format("%4.1f ", elapsedGameTime.time()));
        /*telemetry.addData("Left: ", currentEncoderTargets.LeftTarget);
        telemetry.addData("Right: ", currentEncoderTargets.RightTarget);
        telemetry.addData("Heading: ", turningGyro.getHeading());*/
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

        elapsedTimeForCurrentSegment.reset();

        if (currentPath != null) {

            if (segment.isTurn) {

                turnStartValueLeft = getLeftPosition();
                turnStartValueRight = getRightPosition();

                runWithoutEncoders();
                double currentAngle = turningGyro.getHeading();

                if (counterclockwiseTurnNeeded(currentAngle)) {

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

                    UseRunToPosition();

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

    public void setClimbingPowerLevels() {

        frontLeftMotor.setPower(0.6d);
        frontRightMotor.setPower(0.6d);
    }

    private boolean counterclockwiseTurnNeeded(double currentAngle) {

        telemetry.addData("Angle: ", currentAngle);

        if (currentAngle < Math.abs(segment.Angle)) {

            return (Math.abs(segment.Angle) - currentAngle) >= 180.0f;
        }

        return (currentAngle - Math.abs(segment.Angle)) <= 180.0f;
    }
}