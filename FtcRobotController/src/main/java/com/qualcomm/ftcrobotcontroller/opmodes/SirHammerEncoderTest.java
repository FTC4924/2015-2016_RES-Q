package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by 4924_Users on 10/10/2015.
 */
public class SirHammerEncoderTest extends OpMode {

    private DcMotor testMotor;
    int targetPosition = 0;
    ElapsedTime time;

    static final int FIRST_POSITION = 2500;
    static final int SECOND_POSITION = 5000;
    static final float DELAY = 1.0f;

    @Override
    public void init() {
        testMotor = hardwareMap.dcMotor.get("frontRightMotor");
        testMotor.setTargetPosition(0);
        testMotor.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        testMotor.setChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);
        time = new ElapsedTime();
        time.reset();
    }

    @Override
    public void loop() {


        if (gamepad1.a && (time.time() > DELAY)) {
            targetPosition  += FIRST_POSITION;
            testMotor.setChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);
            testMotor.setTargetPosition(targetPosition);
            testMotor.setPower(0.5f);
            time.reset();
        }
        if (gamepad1.b && (time.time() > DELAY)) {
            targetPosition += SECOND_POSITION;
            testMotor.setChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);
            testMotor.setTargetPosition(targetPosition);
            testMotor.setPower(0.5f);
            time.reset();
        }

        if (gamepad1.x) {
            testMotor.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
            testMotor.setTargetPosition(0);
        }

        telemetry.addData("target:", targetPosition);
    }
}
