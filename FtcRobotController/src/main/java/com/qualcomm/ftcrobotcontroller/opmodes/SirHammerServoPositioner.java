package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by 4924_Users on 10/2/2015.
 */
public class SirHammerServoPositioner extends OpMode {

    Servo testedServo;
    float servoAngle;
    ElapsedTime time = new ElapsedTime();
    boolean delayActive;
    float delayInterval = 0.1f;
    float angleIncrement = 0.01f;

    @Override
    public void init() {
        testedServo = hardwareMap.servo.get("flapServo");
        servoAngle = 0.0f;
        time.reset();
        delayActive=false;
    }

    @Override
    public void loop() {

        if (delayActive)
            if (time.time() >= delayInterval)
                delayActive = false;

        if (!delayActive) {
            if (gamepad1.left_bumper)
                servoAngle -= angleIncrement;
            if (gamepad1.right_bumper)
                servoAngle += angleIncrement;
            servoAngle = Range.clip(servoAngle, 0.0f, 1.0f);
            testedServo.setPosition(servoAngle);
            delayActive = true;
            time.reset();
        }
        telemetry.addData("angle", "angle" + String.format("%.2f", servoAngle));
    }
}
