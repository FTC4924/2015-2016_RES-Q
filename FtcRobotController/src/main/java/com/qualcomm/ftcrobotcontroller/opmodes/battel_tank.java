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
    DcMotor armmotor;
    DcMotor winchmotor;
    DcMotor collectmotor;

    Servo backmidservo; //leftsideservo is a 180
    Servo backrightservo; //backrightservo is a 180
    Servo servo3; //servo3 is a 180
    Servo climers; //frontrightservo is a 180
    Servo servo5; //servo5 is a continuis
    Servo gateservo; //gateservo is a 180
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
        winchmotor = hardwareMap.dcMotor.get("winch");
        armmotor = hardwareMap.dcMotor.get("arm");
        collectmotor = hardwareMap.dcMotor.get("collection");
        backmidservo = hardwareMap.servo.get("servo1");
        backrightservo = hardwareMap.servo.get("servo2");
        servo3 = hardwareMap.servo.get("servo3");
        climers = hardwareMap.servo.get("servo4");
        servo5 = hardwareMap.servo.get("servo5");
        gateservo = hardwareMap.servo.get("servo6");
        servo_angles = new battel_tank_servo_angles();
        frontleftmotor.setDirection(DcMotor.Direction.REVERSE);

        time = new ElapsedTime();
        time.reset();
        servo_time = new ElapsedTime();
        servo_time.reset();
        servo_angles.backrightservo = 0.95f;
        servo_angles.servo3 = 0.50f;
        servo_angles.climers = 1.00f;
        servo_angles.servo5 = 0.50f;
        servo_angles.gateservo = 0.50f;
    }

    @Override
    public void loop(){
        if (gamepad1.right_bumper && (time.time() > DELAY)){
            reversed = !reversed;
            time.reset();
        }

        if (gamepad1.dpad_left){
            servo_angles.servo5 = 1.00f;
        }else{
            if (gamepad1.dpad_right){
                    servo_angles.servo5 = 0.00f;
            }else{
                servo_angles.servo5 = 0.50f;
            }
        }

        if (gamepad1.dpad_up && (servo_time.time() > DELAY)){
            servo_angles.backmidservo = 1.00f;
            servo_time.reset();
        }

        if (gamepad1.dpad_down && (servo_time.time() > DELAY)){
            servo_angles.backmidservo = 0.00f;
            servo_time.reset();
        }

        if (gamepad1.a && (servo_time.time() > DELAY)){
            servo_angles.climers = 1.00f;
            servo_time.reset();
        }

        if (gamepad1.b && (servo_time.time() > DELAY)){
            servo_angles.climers = 0.80f;
            servo_time.reset();
        }

        if (gamepad1.y && (servo_time.time() > DELAY)){
            servo_angles.climers = 0.00f;
            servo_time.reset();
        }

        if (gamepad2.a && (servo_time.time() > DELAY)){
            servo_angles.backrightservo = 0.00f;
            servo_time.reset();
        }

        if (gamepad2.b && (servo_time.time() > DELAY)){
            servo_angles.backrightservo = 0.50f;
            servo_time.reset();
        }

        if (gamepad2.y && (servo_time.time() > DELAY)){
            servo_angles.backrightservo = 0.95f;
            servo_time.reset();
        }

        if (gamepad2.dpad_left && (servo_time.time() > DELAY)){
            servo_angles.gateservo = 0.00f;
        }else {
            if (gamepad2.dpad_right && (servo_time.time() > DELAY)){
                servo_angles.gateservo = 1.00f;
            }else {
                servo_angles.gateservo = 0.50f;
            }
        }

        if (gamepad2.dpad_up && (servo_time.time() > DELAY)){
            servo_angles.servo3 = 0.00f;
        }

        if (gamepad2.dpad_down && (servo_time.time() > DELAY)){
            servo_angles.servo3 = 1.00f;
        }

        servo_angles.servo3 = Range.clip(servo_angles.servo3, 0.0f, 1.0f);
        servo_angles.climers = Range.clip(servo_angles.climers, 0.0f, 1.0f);
        servo_angles.servo5 = Range.clip(servo_angles.servo5, 0.0f, 1.0f);

        backmidservo.setPosition(servo_angles.backmidservo);
        backrightservo.setPosition(servo_angles.backrightservo);
        servo3.setPosition(servo_angles.servo3);
        climers.setPosition(servo_angles.climers);
        servo5.setPosition(servo_angles.servo5);
        gateservo.setPosition(servo_angles.gateservo);

        float frontright = -gamepad1.right_stick_y;
        float frontleft = -gamepad1.left_stick_y;
        float accelerator = gamepad1.right_trigger;
        float accelerator2 = gamepad2.right_trigger;
        float accelerator3 = gamepad1.left_trigger;
        float winch = gamepad2.right_stick_y;
        float arm = gamepad2.left_stick_y;
        boolean collect1 = gamepad2.right_bumper;
        boolean collect2 = gamepad2.left_bumper;
        float colllectpower1 = 0.00f;

        if (collect1){
            colllectpower1 = 1.00f;
        }else {
            if (collect2){
                colllectpower1 = -1.00f;
            }
        }

        frontright = Range.clip(frontright, -1.0f, 1.0f);
        frontleft = Range.clip(frontleft, -1.0f, 1.0f);
        arm = Range.clip(arm, -0.7f, 0.7f);                //Negative is for raising, positive is for lowering
        winch = Range.clip(winch, -1.00f, 1.00f);

        frontright = frontright * accelerator;
        frontleft = frontleft * accelerator;
        arm = arm * accelerator2;
        winch = winch * accelerator3;

        frontright = (float)scaleInput(frontright);
        frontleft = (float)scaleInput(frontleft);

        if(reversed){
            frontright = gamepad1.left_stick_y;
            frontleft = gamepad1.right_stick_y;
            accelerator = gamepad1.right_trigger;

            frontright = frontright * accelerator;
            frontleft = frontleft * accelerator;

            frontright = Range.clip(frontright, -1.0f, 1.0f);
            frontleft = Range.clip(frontleft, -1.0f, 1.0f);
        }

        frontrightmotor.setPower(frontright);
        frontleftmotor.setPower(frontleft);
        winchmotor.setPower(winch);
        armmotor.setPower(arm);
        collectmotor.setPower(colllectpower1);

        telemetry.addData("frontright", frontright);
        telemetry.addData("frontleft", frontleft);
        telemetry.addData("arm", arm);
        telemetry.addData("winch", winch);
        telemetry.addData("servo1", servo_angles.backmidservo);
        telemetry.addData("servo2", servo_angles.backrightservo);
        telemetry.addData("servo3", servo_angles.servo3);
        telemetry.addData("servo4", servo_angles.climers);
        telemetry.addData("servo5", servo_angles.servo5);
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