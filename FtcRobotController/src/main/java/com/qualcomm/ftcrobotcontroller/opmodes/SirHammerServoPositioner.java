package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by 4924_Users on 10/2/2015.
 */
public class SirHammerServoPositioner extends OpMode {

    Servo pinServo;
    float pinServoAngle;

    @Override
    public void init() {
        pinServo = hardwareMap.servo.get("pinServo");
        pinServoAngle = 0.0f;
    }

    @Override
    public void loop() {

        if (gamepad1.left_bumper)
            pinServoAngle -= 0.1;
        if (gamepad1.right_bumper)
            pinServoAngle += 0.1;

        pinServoAngle = Range.clip(pinServoAngle, 0.0f, 1.0f);
        pinServo.setPosition(pinServoAngle);

        telemetry.addData("angle", "angle" + String.format("%.2f", pinServoAngle));
    }
}
