package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by 4924_Users on 10/16/2015.
 */
public class battel_tank extends OpMode {

    DcMotor frontrightmotor;
    DcMotor frontleftmotor;
    DcMotor backrightmotor;
    DcMotor backleftmotor;

    public battel_tank(){

    }

    @Override
    public void init(){
        frontrightmotor = hardwareMap.dcMotor.get("frontrightMotor");
        frontleftmotor = hardwareMap.dcMotor.get("frontleftMotor");
        backrightmotor = hardwareMap.dcMotor.get("backrightMotor");
        backleftmotor = hardwareMap.dcMotor.get("backleftMotor");
        frontleftmotor.setDirection(DcMotor.Direction.REVERSE);
        backleftmotor.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop(){
        float frontright = -gamepad1.right_stick_y;
        float frontleft = -gamepad1.left_stick_y;
        float backright = -gamepad1.right_stick_y;
        float backleft = -gamepad1.left_stick_y;

        frontright = Range.clip(frontright, -1, 1);
        frontleft = Range.clip(frontleft, -1, 1);
        backright = Range.clip(backright, -1, 1);
        backleft = Range.clip(backleft, -1, 1);

        frontright = (float)scaleInput(frontright);
        frontleft = (float)scaleInput(frontleft);
        backright = (float)scaleInput(backright);
        backleft = (float)scaleInput(backleft);

        frontrightmotor.setPower(frontright);
        frontleftmotor.setPower(frontleft);
        backrightmotor.setPower(backright);
        backleftmotor.setPower(backleft);

        telemetry.addData("frontright", frontright);
        telemetry.addData("frontleft", "frontleft");
        telemetry.addData("backright", "backright");
        telemetry.addData("backleft", "backleft");
    }

    @Override
    public void stop() {

    }

    double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

        int index = (int) (dVal * 16.0);

        if (index < 0) {
            index = -index;
        }

        if (index > 16) {
            index = 16;
        }

        double dScale = 0.0;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        return dScale;
    }
}
