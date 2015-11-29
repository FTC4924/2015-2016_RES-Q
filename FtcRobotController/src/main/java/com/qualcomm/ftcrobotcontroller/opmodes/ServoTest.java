package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by 4924_Users on 11/16/2015.
 */
public class ServoTest extends OpMode {

    Servo climberDeployer;
    Servo rightTriggerArmServo;
    Servo continuousServo;
    Servo ballRotatorServo;
    Servo leftTriggerArmServo;

    double climberDeployerServoAngle = 0.0d;
    double ballRotatorServoAngle = 0.0d;
    double leftTriggerArmServoAngle = 0.0d;
    double rightTriggerArmServoAngle = 0.0d;

    ElapsedTime buttonPressTime = new ElapsedTime();
    final float DELAY = 0.2f;

    @Override
    public void init() {

        leftTriggerArmServo = hardwareMap.servo.get("servo1");
        continuousServo = hardwareMap.servo.get("servo2");
        rightTriggerArmServo = hardwareMap.servo.get("servo3");
        ballRotatorServo = hardwareMap.servo.get("servo4");
        climberDeployer= hardwareMap.servo.get("servo5");
        climberDeployer.setPosition(0.0d);
        continuousServo.setPosition(0.5f);
    }

    @Override
    public void loop() {

        continuousServo.setPosition(0.5f);

        if (gamepad1.y && (buttonPressTime.time() > DELAY)){
            climberDeployerServoAngle += 0.1f;
            climberDeployerServoAngle = Range.clip(climberDeployerServoAngle, 0.0d, 1.0d);
            buttonPressTime.reset();
        }

        if (gamepad1.a && (buttonPressTime.time() > DELAY)){
            climberDeployerServoAngle -= 0.1f;
            climberDeployerServoAngle = Range.clip(climberDeployerServoAngle, 0.0d, 1.0d);
            buttonPressTime.reset();
        }

        if (gamepad1.x && (buttonPressTime.time() > DELAY)){
            ballRotatorServoAngle += 0.1f;
            ballRotatorServoAngle = Range.clip(ballRotatorServoAngle, 0.0d, 1.0d);
            buttonPressTime.reset();
        }

        if (gamepad1.b && (buttonPressTime.time() > DELAY)){
            ballRotatorServoAngle -= 0.1f;
            ballRotatorServoAngle = Range.clip(ballRotatorServoAngle, 0.0d, 1.0d);
            buttonPressTime.reset();
        }

        if (gamepad1.dpad_left && (buttonPressTime.time() > DELAY)){
            leftTriggerArmServoAngle += 0.1f;
            leftTriggerArmServoAngle = Range.clip(leftTriggerArmServoAngle, 0.0d, 1.0d);
            buttonPressTime.reset();
        }

        if (gamepad1.dpad_right && (buttonPressTime.time() > DELAY)){
            leftTriggerArmServoAngle -= 0.1f;
            leftTriggerArmServoAngle = Range.clip(leftTriggerArmServoAngle, 0.0d, 1.0d);
            buttonPressTime.reset();
        }

        if (gamepad1.dpad_up && (buttonPressTime.time() > DELAY)){
            rightTriggerArmServoAngle += 0.1f;
            rightTriggerArmServoAngle = Range.clip(rightTriggerArmServoAngle, 0.0d, 1.0d);
            buttonPressTime.reset();
        }

        if (gamepad1.dpad_down && (buttonPressTime.time() > DELAY)){
            rightTriggerArmServoAngle -= 0.1f;
            rightTriggerArmServoAngle = Range.clip(rightTriggerArmServoAngle, 0.0d, 1.0d);
            buttonPressTime.reset();
        }

        climberDeployer.setPosition(climberDeployerServoAngle);
        ballRotatorServo.setPosition(ballRotatorServoAngle);
        leftTriggerArmServo.setPosition(leftTriggerArmServoAngle);
        rightTriggerArmServo.setPosition(rightTriggerArmServoAngle);

        telemetry.addData("Climber Deployer Angle: ", climberDeployerServoAngle);
    }
}
