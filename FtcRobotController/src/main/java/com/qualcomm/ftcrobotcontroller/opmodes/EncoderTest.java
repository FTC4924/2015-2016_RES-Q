package com.qualcomm.ftcrobotcontroller.opmodes;

import android.provider.Telephony;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.robocol.Telemetry;

/**
 * Created by Bob on 1/23/2016.
 */
public class EncoderTest extends OpMode {

    DcMotor leftMotor;
    DcMotor rightMotor;

    @Override
    public void init() {

        leftMotor = hardwareMap.dcMotor.get("leftMotor");
        rightMotor = hardwareMap.dcMotor.get("rightMotor");
    }

    @Override
    public void loop() {

        if (leftMotor.getCurrentPosition() >= 1000) {

            leftMotor.setPower(0.0d);

        } else {

            leftMotor.setPower(0.5d);
        }

        if (rightMotor.getCurrentPosition() >= 1000) {

            rightMotor.setPower(0.0d);

        } else {

            rightMotor.setPower(0.5d);
        }

        telemetry.addData("Left Encoder Value: ", leftMotor.getCurrentPosition());
        telemetry.addData("Right Encoder Value: ", rightMotor.getCurrentPosition());
    }
}
