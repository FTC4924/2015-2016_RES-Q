package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by 4924_Users on 9/13/2015.
 */
public class BaseTankDriveTest extends OpMode {
    DcMotor leftFrontMotor;
    DcMotor rightFrontMotor;
    DcMotor leftBackMotor;
    DcMotor rightBackMotor;

    @Override
    public void init() {
        leftFrontMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        rightFrontMotor=hardwareMap.dcMotor.get("frontRightMotor");
        leftBackMotor = hardwareMap.dcMotor.get("backLeftMotor");
        rightBackMotor = hardwareMap.dcMotor.get("backRightMotor");
    }

    @Override
    public void loop() {
        //doubleleft
    }
}
