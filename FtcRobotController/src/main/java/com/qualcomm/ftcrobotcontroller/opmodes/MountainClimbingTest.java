package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by 4924_Users on 11/14/2015.
 */
public class MountainClimbingTest extends OpMode {


    DcMotor frontLeftMotor;
    DcMotor frontRightMotor;
    private ElapsedTime timer = new ElapsedTime();

    @Override
    public void init() {

        frontRightMotor = hardwareMap.dcMotor.get("frontrightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontleftMotor");
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {

        if (timer.time() >= 9.0f) {

            frontLeftMotor.setPower(0.0d);
            frontRightMotor.setPower(0.0d);

        } else {

            frontLeftMotor.setPower(-0.4d);
            frontRightMotor.setPower(-0.4d);
        }
    }
}
