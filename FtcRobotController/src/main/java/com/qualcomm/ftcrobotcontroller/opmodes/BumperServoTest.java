package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by 4924_Users on 2/6/2016.
 */
public class BumperServoTest extends OpMode {

    ElapsedTime delayTime;
    public float bumperServoAngle = 0.0f;
    Servo bumperServo;
    Servo rightsideservo;
    Servo climberDeployer;
    Servo ziplinerTripper;
    Servo deliveryBelt;
    Servo gateServo;

    @Override
    public void init() {

        delayTime = new ElapsedTime();
        delayTime.reset();

        rightsideservo = hardwareMap.servo.get("servo2");
        climberDeployer = hardwareMap.servo.get("servo4");
        ziplinerTripper = hardwareMap.servo.get("servo5");
        deliveryBelt = hardwareMap.servo.get("servo3");
        bumperServo = hardwareMap.servo.get("servo1");
        gateServo = hardwareMap.servo.get("servo6");

        bumperServo.setPosition(bumperServoAngle);
        climberDeployer.setPosition(1.0d);
        ziplinerTripper.setPosition(0.5d);
        deliveryBelt.setPosition(0.5d);
        rightsideservo.setPosition(0.0d);
        gateServo.setPosition(0.0d);
    }

    @Override
    public void loop() {

        if (gamepad1.a && delayTime.time() >= 1.0f) {

            bumperServoAngle += 0.1d;
            delayTime.reset();
        }

        if (gamepad1.b && delayTime.time() >= 1.0f) {

            bumperServoAngle -= 0.1d;
            delayTime.reset();
        }

        Range.clip(bumperServoAngle, 0.0f, 1.0f);
        bumperServo.setPosition(bumperServoAngle);
        climberDeployer.setPosition(1.0d);
        ziplinerTripper.setPosition(0.5d);
        deliveryBelt.setPosition(0.5d);
        rightsideservo.setPosition(0.0d);
        gateServo.setPosition(0.0d);
        telemetry.addData("Bumper Servo Angle: ", bumperServoAngle);
    }
}
