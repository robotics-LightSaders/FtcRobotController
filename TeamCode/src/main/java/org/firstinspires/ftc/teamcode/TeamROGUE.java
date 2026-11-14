/*
Copyright (c) 2023 FIRST

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of FIRST nor the names of its contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.Servo;

//made by Maya on 8/19/24
//A teleop for the mecaum drive teleop
//
@TeleOp(name = "Team ROGUE", group = "Teleop")
@Disabled

public class TeamROGUE extends LinearOpMode {

    //static CCConstants CC_Constants = new CCConstants();

    //region Devices


    DcMotor backLeftMotor;
    DcMotor backRightMotor;
    DcMotor frontLeftMotor;
    DcMotor frontRightMotor;

    Servo lift;
    Servo claw;


    //region Variables
    ElapsedTime runTime = new ElapsedTime();

 //   private AHRS navx_device;

    double wallServoCurrentPos = 0;

    double fast = .75;
    double mid = .5;
    double slow = .25;

    double speedModifier = mid;

    String Speed = "mid";

    double currentposL;
    double liftspeed = .0005;

    double resetAngle = 0;

    boolean clawopen = true;
    boolean canclaw = true;



    boolean firstloop = true;

    // Motor vars
    double x = 0;
    double y = 0;
    double z = 0;


    @Override
    public void runOpMode() {

        //region Hardware Mapping



        //main motors
        frontLeftMotor = hardwareMap.get(DcMotor.class, "fl");
        frontRightMotor = hardwareMap.get(DcMotor.class, "fr");
        backRightMotor = hardwareMap.get(DcMotor.class, "br");
        backLeftMotor = hardwareMap.get(DcMotor.class, "bl");

        lift = hardwareMap.get(Servo.class, "lift");
        claw = hardwareMap.get(Servo.class,"claw");



        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);






        waitForStart();


        // Reset the runtime
        runTime.reset();


    //    resetAngle = -navx_device.getYaw();

        while (opModeIsActive()) {

            //region first loop
            if(firstloop){
                lift.setPosition(1);
                claw.setPosition(.15);
                firstloop = false;
            }

            //endregion

            //region Driving


            y = -gamepad1.left_stick_y; //
            z = gamepad1.left_stick_x; //
            x = gamepad1.right_stick_x;


            frontLeftMotor.setPower((Range.clip((y + x + z), -1, 1)) * speedModifier);
            frontRightMotor.setPower((Range.clip((y + -x + -z), -1, 1)) * speedModifier);
            backLeftMotor.setPower((Range.clip((y + x + -z), -1, 1)) * speedModifier);
            backRightMotor.setPower((Range.clip((y + -x + z), -1, 1)) * speedModifier);

            if (gamepad1.dpad_down) {
                speedModifier = slow;
                Speed = "Slow";

                frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            } else if (gamepad1.dpad_right) {
                speedModifier = mid;
                Speed = "Mid";

                frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            } else if(gamepad1.dpad_up){
                speedModifier = fast;
                Speed = "Fast";

                frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            }

            //endregion

            //region - Lift


            if (gamepad2.right_stick_y > .175){
                if (lift.getPosition() < 1){
                    lift.setPosition(lift.getPosition() + liftspeed);
                }
            } else if (gamepad2.right_stick_y < -.175){
                if(lift.getPosition() > .8){
                    lift.setPosition(lift.getPosition() - liftspeed);
                }
            }

            if(gamepad2.dpad_up){
                liftspeed = .0008;
            }

            if(gamepad2.dpad_right){
                liftspeed = .0005;
            }

            if(gamepad2.dpad_down){
                liftspeed = .0002;
            }


            //endregion

            //claw



            if (gamepad2.a && canclaw){
                canclaw = false;
                if (clawopen){
                    claw.setPosition(.48);
                    clawopen = false;
                } else {
                    claw.setPosition(.15);
                    clawopen = true;
                }
            }


            if (gamepad2.b && canclaw){
                canclaw = false;
                if (clawopen){
                    claw.setPosition(.38);
                    clawopen = false;
                } else {
                    claw.setPosition(.15);
                    clawopen = true;
                }
            }

            if (gamepad2.y && canclaw){
                canclaw = false;
                if (clawopen){
                    claw.setPosition(.45);
                    clawopen = false;
                } else {
                    claw.setPosition(.15);
                    clawopen = true;
                }
            }

            if (gamepad2.left_stick_y > .2 && claw.getPosition() <.48){
                claw.setPosition(claw.getPosition() + .0005);
            }

            if(gamepad2.left_stick_y < -.2 && claw.getPosition() > .15){
                claw.setPosition(claw.getPosition() - .0005);
            }



            if (!gamepad2.a && !gamepad2.b && !gamepad2.y && !gamepad2.x){
                canclaw = true;
            }

            //endregion






            //outside of while loop
            //turn all motors off
            frontLeftMotor.setPower(0);
            frontRightMotor.setPower(0);
            backLeftMotor.setPower(0);
            backRightMotor.setPower(0);

            //region - Telemetry

            telemetry.addData("Speed =", Speed);
            telemetry.addData("Lift Position =",lift.getPosition());
            telemetry.addData("Claw Position =", claw.getPosition());

            telemetry.update();
            //endregion
        }
    }
}

// end class

