package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.ftcrobotcontroller.SirHammerAutonomousSensors;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import static com.qualcomm.ftcrobotcontroller.opmodes.SirHammerAutonomous.CenterColumnPosition.*;
import static com.qualcomm.ftcrobotcontroller.opmodes.SirHammerAutonomous.RobotStates.*;

/**
 * Created by 4924_Users on 9/27/2015.
 */
public class SirHammerAutonomous extends OpMode {

    public enum RobotStates { MOVING_TO_READ_CENTER_POSITION,
        READING_CENTER_POSITION, MOVING_TO_POSITION_1,
        MOVING_TO_POSITION_2, MOVING_TO_POSITION_3, RAISING_AUTONOMOUS_ARM,
        LOWERING_AUTONOMOUS_ARM, MOVING_TO_KICKSTAND }

    public enum CenterColumnPosition { UNDETECTED, ONE, TWO, THREE }

    RobotStates state;

    DcMotor frontLeftMotor;
    DcMotor backLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backRightMotor;
    SirHammerAutonomousSensors sensors;
    CenterColumnPosition goalPosition;

    @Override
    public void init() {
        state = MOVING_TO_READ_CENTER_POSITION;
        goalPosition = UNDETECTED;

        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.REVERSE);
        sensors = new SirHammerAutonomousSensors();
    }

    @Override
    public void loop() {
        switch (state) {
            case MOVING_TO_READ_CENTER_POSITION:
                FourWheelDrivePowerLevels findingPower = new FourWheelDrivePowerLevels(0.5f, 0.5f);
                SetDriveMotorPowerLevels(findingPower);
                break;
            case READING_CENTER_POSITION:
                // drive until the ODS detects the tape
                break;
            case MOVING_TO_POSITION_1:
                // execute a sequence of commands
                break;
            case MOVING_TO_POSITION_2:
                break;
            case MOVING_TO_POSITION_3:
                break;
            case RAISING_AUTONOMOUS_ARM:
                break;
            case LOWERING_AUTONOMOUS_ARM:
                break;
            case MOVING_TO_KICKSTAND:
                break;
            default:
                break;
        }
    }

    private void SetDriveMotorPowerLevels(FourWheelDrivePowerLevels levels) {
        frontLeftMotor.setPower(levels.frontLeft);
        backLeftMotor.setPower(levels.backLeft);
        backRightMotor.setPower(levels.backRight);
        frontRightMotor.setPower(levels.frontRight);
    }
}
