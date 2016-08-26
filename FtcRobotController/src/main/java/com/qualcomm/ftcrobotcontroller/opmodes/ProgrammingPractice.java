package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by 4924_Users on 5/27/2016.
 */
public class ProgrammingPractice extends OpMode {

    DcMotor practiceMotor;
    Servo practiceServo;
    double motorPower = 0.0d;
    double servoAngle = 0.0d;

    @Override
    public void init() {

        practiceMotor = hardwareMap.dcMotor.get("practiceMotor");
        practiceServo = hardwareMap.servo.get("practiceServo");
    }

    @Override
    public void loop() {

        motorPower = -gamepad1.left_stick_y;
        Range.clip(motorPower, -1.0d, 1.0d);

        if (gamepad1.a) {

            servoAngle = 0.1d;
        }

        if (gamepad1.b) {

            servoAngle = 0.9d;
        }

        if (gamepad1.x) {
            servoAngle = 0.4;
        }

        Range.clip(servoAngle, 0.0d, 1.0d);

        practiceMotor.setPower(motorPower);
        practiceServo.setPosition(servoAngle);
    }

    public int add(int numberOne, int numberTwo) {

        return numberOne + numberTwo;
    }
}