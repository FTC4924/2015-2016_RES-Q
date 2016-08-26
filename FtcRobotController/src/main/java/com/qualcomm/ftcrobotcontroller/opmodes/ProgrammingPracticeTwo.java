package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;


/**
 * Created by 4924_Users on 5/28/2016.
 */
public class ProgrammingPracticeTwo extends OpMode {

    DcMotor practiceMotor;
    double motorPower = 0.0d;

    @Override
    public void init()
    {
        practiceMotor = hardwareMap.dcMotor.get("practiceMotor");
    }

    @Override
    public void loop()
    {
        Range.clip(motorPower, -1.0d, 1.0d);

        if (gamepad1.right_bumper) {

            motorPower = -gamepad1.right_trigger;
        } else {
            motorPower = gamepad1.right_trigger;
        }

        practiceMotor.setPower(motorPower);
    }
}