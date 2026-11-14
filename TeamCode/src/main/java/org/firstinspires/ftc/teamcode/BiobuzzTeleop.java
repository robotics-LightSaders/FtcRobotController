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

import android.util.Log;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
//import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;
//made by Maya on 8/19/24
//A teleop for the mecaum drive teleop
//

@Disabled
@TeleOp(name = "Biobuzz Teleop", group = "Teleop")

public class BiobuzzTeleop extends LinearOpMode {

    BiobuzzConstants bio = new BiobuzzConstants();



    //region Devices




    //region Variables
    ElapsedTime runTime = new ElapsedTime();

 //   private AHRS navx_device;

    double wallServoCurrentPos = 0;

    double speedModifier = 1;

    double resetAngle = 0;

    // Motor vars
    double x = 0;
    double y = 0;
    double z = 0;
double maxAccelAng =Math.toRadians(400);
double maxDeAccelAng = Math.toRadians(500);
//all in radians,and angular measurement
double PIDResponse = 4;
    double kP = Math.pow(PIDResponse,2)/maxAccelAng;
    double kD = PIDResponse/maxDeAccelAng;
    // PID variables

double lastHeadingTime = 0;
double headingTime;
double angVelo;
double lastAngVelo;
double maxVelo = 0;
double maxAccel;

double angAccel = 0;
double lastAngAccel = 0;
double lastHeading = 0;
double deaccel = 0;
double headingDelta;
double jump360 = 0;
double currentHeading;
double odoVelo;
DcMotorEx intake;
DcMotorEx flywheel;
DcMotor transfer;

    @Override
    public void runOpMode() {

        //region Hardware Mapping

        intake = hardwareMap.get(DcMotorEx.class,"intake");
        flywheel = hardwareMap.get(DcMotorEx.class,"flywheel");
        transfer = hardwareMap.get(DcMotorEx.class,"transfer");

        BiobuzzMecanumDrive drivetrain = new BiobuzzMecanumDrive(hardwareMap);
        //main motors




        //endregion


     drivetrain.navreset(true,true);
     drivetrain.odoUpdate();
        waitForStart();


        // Reset the runtime
        runTime.reset();


    //    resetAngle = -navx_device.getYaw();

        while (opModeIsActive()) {
drivetrain.odoUpdate();
             telemetry.addData("X",drivetrain.odoPose().getX(DistanceUnit.INCH));
            telemetry.addData("Y",drivetrain.odoPose().getY(DistanceUnit.INCH));
            telemetry.addData("H",drivetrain.odoPose().getHeading(AngleUnit.DEGREES));

//region -Braking
            if (gamepad1.a){
    drivetrain.setPower(-gamepad1.left_stick_x,-gamepad1.left_stick_y,(float)drivetrain.headingAlignment(drivetrain.odoPose().getHeading(AngleUnit.DEGREES),runTime.milliseconds()));
drivetrain.setBrake();
}else {
    drivetrain.setPower(-gamepad1.left_stick_x, -gamepad1.left_stick_y, -gamepad1.right_stick_x);
    drivetrain.setFloat();
}
            //endregion


            telemetry.addData("Heading",currentHeading);
            telemetry.addData("Heading Power",+drivetrain.headingAlignment(drivetrain.odoPose().getHeading(AngleUnit.DEGREES),runTime.milliseconds()));
           drivetrain.CalculateMetrics(runTime.milliseconds(),drivetrain.odo.getHeadingVelocity(UnnormalizedAngleUnit.DEGREES));
            headingTime=runTime.milliseconds();
            // Calculate error

            telemetry.update();
        }
    }
}

// end class

