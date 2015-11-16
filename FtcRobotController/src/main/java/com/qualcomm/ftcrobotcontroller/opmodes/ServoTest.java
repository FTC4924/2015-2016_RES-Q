package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by 4924_Users on 11/16/2015.
 */
public class ServoTest extends OpMode {

    Servo climberDeployer;
    double servoAngle = 0.0d;
    ElapsedTime buttonPressTime = new ElapsedTime();
    final float DELAY = 0.2f;

    @Override
    public void init() {

        climberDeployer = hardwareMap.servo.get("servo5");
        climberDeployer.setPosition(0.0d);
    }

    @Override
    public void loop() {

        if (gamepad2.y && (buttonPressTime.time() > DELAY)){
            servoAngle += 0.1f;
            climberDeployer.setPosition(servoAngle);
            buttonPressTime.reset();
        }

        if (gamepad2.x && (buttonPressTime.time() > DELAY)){
            servoAngle -= 0.1f;
            climberDeployer.setPosition(servoAngle);
            buttonPressTime.reset();
        }
    }
}
