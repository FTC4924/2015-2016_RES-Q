package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by 4924_Users on 2/6/2016.
 */
public class BumperServoTest extends OpMode {

    Servo bumperServo;
    ElapsedTime delayTime;
    public float bumperServoAngle = 0.0f;

    @Override
    public void init() {

        bumperServo = hardwareMap.servo.get("servo1");
        delayTime.reset();
        bumperServo.setPosition(bumperServoAngle);
    }

    @Override
    public void loop() {

        if (gamepad1.a && delayTime.time() >= 1.0f) {

            bumperServoAngle += 0.2;
        }

        if (gamepad1.b && delayTime.time() >= 1.0f) {

            bumperServoAngle -= 0.2;
        }

        Range.clip(bumperServoAngle, 0.0f, 1.0f);
        bumperServo.setPosition(bumperServoAngle);
        telemetry.addData("Bumper Servo Angle: ", bumperServoAngle);
    }
}
