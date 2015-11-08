package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.battel_tank_servo_angles;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by 4924_Users on 10/16/2015.
 */
public class battel_tank extends OpMode {

    DcMotor frontrightmotor;
    DcMotor frontleftmotor;
    //DcMotor backrightmotor;
    //DcMotor backleftmotor;
    DcMotor armmotor;
    Servo servo1;
    Servo servo2;
    ElapsedTime time;
    ElapsedTime servo_time;
    battel_tank_servo_angles servo_angles;
    static final float DELAY = 1.0f;
    boolean reversed;
        public battel_tank(){

        }

    @Override
    public void init(){
        frontrightmotor = hardwareMap.dcMotor.get("frontrightMotor");
        frontleftmotor = hardwareMap.dcMotor.get("frontleftMotor");
        //backrightmotor = hardwareMap.dcMotor.get("backrightMotor");
        //backleftmotor = hardwareMap.dcMotor.get("backleftMotor");
        armmotor = hardwareMap.dcMotor.get("arm");
        servo1 = hardwareMap.servo.get("servo1");
        servo2 = hardwareMap.servo.get("servo2");
        servo_angles = new battel_tank_servo_angles();
        frontleftmotor.setDirection(DcMotor.Direction.REVERSE);
        //backleftmotor.setDirection(DcMotor.Direction.REVERSE);

        time = new ElapsedTime();
        time.reset();
        servo_time = new ElapsedTime();
        servo_time.reset();
        servo2.setPosition(0.50f);
        servo_angles.servo2 = 0.5f;
    }

    @Override
    public void loop(){
        if (gamepad1.right_bumper && (time.time() > DELAY)) {
            reversed = !reversed;
            time.reset();
        }

        if (gamepad2.x && (servo_time.time() > DELAY)){
            servo_angles.servo1 = servo_angles.servo1 + .25f;
            servo_time.reset();
        }

        if (gamepad2.b && (servo_time.time() > DELAY)){
            servo_angles.servo1 = servo_angles.servo1 - .25f;
            servo_time.reset();
        }

        if (gamepad2.y){
            servo_angles.servo2 = 0.6f;
        }
        else {
            if (gamepad2.a) {
                servo_angles.servo2 = 0.3f;
            } else {
                servo2.setPosition(0.50f);
            }
        }

        servo_angles.servo1 = Range.clip(servo_angles.servo1, 0.00f, 1.00f);
        servo_angles.servo2 = Range.clip(servo_angles.servo2, 0.00f, 1.00f);

        servo1.setPosition(servo_angles.servo1);
        servo2.setPosition(servo_angles.servo2);

        float frontright = -gamepad1.right_stick_y;
        float frontleft = -gamepad1.left_stick_y;
        //float backright = -gamepad1.right_stick_y;
        //float backleft = -gamepad1.left_stick_y;
        float accelerator = gamepad1.right_trigger;
        float arm = gamepad2.right_stick_y;

        frontright = Range.clip(frontright, -1, 1);
        frontleft = Range.clip(frontleft, -1, 1);
        //backright = Range.clip(backright, -1, 1);
        //backleft = Range.clip(backleft, -1, 1);


        frontright = frontright * accelerator;
        frontleft = frontleft * accelerator;
        //backright = backright * accelerator;
        //backleft = backleft * accelerator;

        frontright = (float)scaleInput(frontright);
        frontleft = (float)scaleInput(frontleft);
        //backright = (float)scaleInput(backright);
        //backleft = (float)scaleInput(backleft);

        if(reversed){
            frontright = gamepad1.left_stick_y;
            frontleft = gamepad1.right_stick_y;
            //backright = gamepad1.left_stick_y;
            //backleft = gamepad1.right_stick_y;
            accelerator = gamepad1.right_trigger;

            frontright = Range.clip(frontright, -1, 1);
            frontleft = Range.clip(frontleft, -1, 1);
            //backright = Range.clip(backright, -1, 1);
            //backleft = Range.clip(backleft, -1, 1);

            frontright = frontright * accelerator;
            frontleft = frontleft * accelerator;
            //backright = backright * accelerator;
            //backleft = backleft * accelerator;
        }

        frontrightmotor.setPower(frontright);
        frontleftmotor.setPower(frontleft);
        //backrightmotor.setPower(backright);
        //backleftmotor.setPower(backleft);
        armmotor.setPower(arm);

        telemetry.addData("frontright", "frontright");
        telemetry.addData("frontleft", "frontleft");
        //telemetry.addData("backright", "backright");
        //telemetry.addData("backleft", "backleft");
        telemetry.addData("arm", "arm");
        telemetry.addData("servo1", servo_angles.servo1);
        telemetry.addData("servo2", servo_angles.servo2);
        telemetry.addData("time", time.time());
        telemetry.addData("servo_time", servo_time.time());
        if(reversed){
            telemetry.addData("reversed", "yes");
        }
        else {
            telemetry.addData("reversed", "no");
        }
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