package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.ftcrobotcontroller.RobotCommandQueue;
import com.qualcomm.ftcrobotcontroller.SirHammerAutonomousSensors;
import com.qualcomm.ftcrobotcontroller.SirHammerCommandQueueBuilder;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import static com.qualcomm.ftcrobotcontroller.opmodes.SirHammerAutonomous.CenterColumnPosition.*;

/**
 * Created by 4924_Users on 9/27/2015.
 */
public class SirHammerAutonomous extends OpMode {

    public enum CenterColumnPosition { UNDETECTED, ONE, TWO, THREE }

    RobotCommandQueue commandQueue;

    DcMotor frontLeftMotor;
    DcMotor backLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backRightMotor;
    SirHammerAutonomousSensors sensors;
    CenterColumnPosition goalPosition;

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

        commandQueue = SirHammerCommandQueueBuilder.Build();
    }

    @Override
    public void loop() {
        commandQueue.Execute();
        telemetry.addData("CMD", ": " + commandQueue.CurrentCommandDescription());
    }

    private void SetDriveMotorPowerLevels(FourWheelDrivePowerLevels levels) {
        frontLeftMotor.setPower(levels.frontLeft);
        backLeftMotor.setPower(levels.backLeft);
        backRightMotor.setPower(levels.backRight);
        frontRightMotor.setPower(levels.frontRight);
    }
}
