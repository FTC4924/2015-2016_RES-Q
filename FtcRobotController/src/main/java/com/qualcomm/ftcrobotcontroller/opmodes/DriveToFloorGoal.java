package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by 4924_Users on 2/27/2016.
 */
public class DriveToFloorGoal extends OpMode {

    DcMotor frontLeftMotor;
    DcMotor frontRightMotor;
    ElapsedTime elapsedGameTime = new ElapsedTime();

    @Override
    public void init() {

        frontRightMotor = hardwareMap.dcMotor.get("frontrightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontleftMotor");
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void start() {

        elapsedGameTime.reset();
    }

    @Override
    public void loop() {

        if (elapsedGameTime.time() <= 5.0f) {

            frontLeftMotor.setPower(0.9f);
            frontRightMotor.setPower(0.9f);

        } else {

            frontLeftMotor.setPower(0.0f);
            frontRightMotor.setPower(0.0f);
        }
    }
}
