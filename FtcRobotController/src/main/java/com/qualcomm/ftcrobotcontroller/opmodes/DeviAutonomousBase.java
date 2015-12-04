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
public abstract class DeviAutonomousBase extends OpMode {

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

    final DrivePathSegment[] emptyPath = {

            new DrivePathSegment(0.0f, 0.0f, 0.0f)
    };

    private State currentState;
    private int currentPathSegmentIndex = 0;
    private DrivePathSegment[] currentPath = emptyPath;
    DrivePathSegment segment = currentPath[currentPathSegmentIndex];
    EncoderTargets currentEncoderTargets = zeroEncoderTargets;

    public void SetCurrentState(State newState) {

        elapsedTimeForCurrentState.reset();
        currentState = newState;
    }
}
