package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by 4924_Users on 1/24/2016.
 */
public class SecondLeftReverseTest extends OpMode {

    DcMotor leftMotor;
    DcMotor rightMotor;

    @Override
    public void init() {

        leftMotor = hardwareMap.dcMotor.get("leftMotor");
        rightMotor = hardwareMap.dcMotor.get("rightMotor");
        leftMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {

        leftMotor.setPower(0.5d);
        rightMotor.setPower(0.5d);
    }
}
