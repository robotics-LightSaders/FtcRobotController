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
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;
//
@Disabled
@TeleOp(name = "Biobuzz Teleop", group = "Teleop")

public class BiobuzzTeleopV2 extends LinearOpMode {

      BiobuzzConstants bio = new BiobuzzConstants();


    //region Devices


    //region Variables
    ElapsedTime runTime = new ElapsedTime();

    //   private AHRS navx_device;


    double speedModifier = 1;

    double resetAngle = 0;

    // Motor vars


    //all in radians,and angular measurement

    // PID variables

    double headingTime;

    double currentHeading;
    DcMotorEx intakef;
    CRServo intakeb;

    DcMotorEx flywheel;
    DcMotor transfer;
    Servo turret;
    Servo hood;
    Limelight3A limelight;

    int frontIntakeOn;
    int backIntakeOn;
    double MaxFlywheelSpeed = 1500;
    double MinFlywheelSpeed = 1200;
    double TargetFlywheelSpeed = 0;
    double distToGoalX;
    double distToGoalY;
    double distToGoalTot;

    boolean AllianceIsRed = false;
    double turretMin = 0;
    double turretMax = .5;
    //the turrets maximum rotation angle in degrees
    double turretMaxRotation = 360;
double turretMinAngle = -180;
double turretMaxAngle = 180;
    double targetHoodAngle;

    @Override
    public void runOpMode() {

        //region Hardware Mapping

        intakef = hardwareMap.get(DcMotorEx.class, "intakef");
        intakeb = hardwareMap.get(CRServo.class, "intakeb");
        turret = hardwareMap.get(Servo.class, "turret");
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        transfer = hardwareMap.get(DcMotorEx.class, "transfer");
        hood = hardwareMap.get(Servo.class, "hood");
        BiobuzzMecanumDrive drivetrain = new BiobuzzMecanumDrive(hardwareMap);
        //main motors
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(0, 0, 0, 0));
        turret.setDirection(Servo.Direction.REVERSE);
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
        limelight.start();
        //endregion

        LLResult result = limelight.getLatestResult();

        drivetrain.navreset(true, true);
        drivetrain.odoUpdate();
        waitForStart();


        // Reset the runtime
        runTime.reset();


        while (opModeIsActive()) {
            drivetrain.odoUpdate();
            telemetry.addData("X", drivetrain.odoPose().getX(DistanceUnit.INCH));
            telemetry.addData("Y", drivetrain.odoPose().getY(DistanceUnit.INCH));
            telemetry.addData("H", drivetrain.odoPose().getHeading(AngleUnit.DEGREES));

            if (result != null && result.isValid()) {
                for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {
                    double tagId = tag.getFiducialId();
                    double bearing = tag.getTargetXDegrees(); // Horizontal offset (bearing) in degrees

                    telemetry.addData("Tag ID", tagId);
                    telemetry.addData("Bearing (tx)", "%.2f degrees", bearing);
                }
            }

//region -Braking
            if (gamepad1.a) {
                drivetrain.setPower(-gamepad1.left_stick_x, -gamepad1.left_stick_y, (float) drivetrain.headingAlignment(drivetrain.odoPose().getHeading(AngleUnit.DEGREES), runTime.milliseconds()));
                drivetrain.setBrake();
            } else {
                drivetrain.setPower(-gamepad1.left_stick_x, -gamepad1.left_stick_y, -gamepad1.right_stick_x);
                drivetrain.setFloat();
            }
            //endregion
//region -Intake
            if (gamepad2.right_trigger > .5) {
                frontIntakeOn = 1;
            } else if (gamepad2.a) {
                frontIntakeOn = 1;
            } else if (gamepad2.right_bumper) {
                frontIntakeOn = -1;

            } else {
                frontIntakeOn = 0;
            }

            if (gamepad2.left_trigger > .5) {
                backIntakeOn = 1;
            } else if (gamepad2.right_bumper) {
                backIntakeOn = -1;

            } else {
                backIntakeOn = 0;
            }
            intakef.setPower(frontIntakeOn);
            intakeb.setPower(backIntakeOn);
//endregion
// region -Transfer
            //hold A to shoot
            if (gamepad2.a) {
                transfer.setPower(1);
            } else {
                transfer.setPower(0);
            }
//endregion
// region -Dist To Goal Calc
//Distance calculations towards goal
            //if we are red and odo is above the centre x axis, then we find the distance to
            // the far side goal, otherwise we find the distance to the audience side goal
            if (AllianceIsRed) {

                if (drivetrain.odoPose().getY(DistanceUnit.INCH) > 0) {
                    distToGoalX = drivetrain.odoPose().getX(DistanceUnit.INCH) - bio.redGoalBack.getX(DistanceUnit.INCH);
                    distToGoalY = drivetrain.odoPose().getY(DistanceUnit.INCH) - bio.redGoalBack.getY(DistanceUnit.INCH);
                    distToGoalTot = Math.hypot(distToGoalX, distToGoalY);
                } else {
                    distToGoalX = drivetrain.odoPose().getX(DistanceUnit.INCH) - bio.redGoalAud.getX(DistanceUnit.INCH);
                    distToGoalY = drivetrain.odoPose().getY(DistanceUnit.INCH) - bio.redGoalAud.getY(DistanceUnit.INCH);
                    distToGoalTot = Math.hypot(distToGoalX, distToGoalY);
                }

            }
            if (!AllianceIsRed) {

                if (drivetrain.odoPose().getY(DistanceUnit.INCH) > 0) {
                    distToGoalX = drivetrain.odoPose().getX(DistanceUnit.INCH) - bio.blueGoalBack.getX(DistanceUnit.INCH);
                    distToGoalY = drivetrain.odoPose().getY(DistanceUnit.INCH) - bio.blueGoalBack.getY(DistanceUnit.INCH);
                    distToGoalTot = Math.hypot(distToGoalX, distToGoalY);
                } else {
                    distToGoalX = drivetrain.odoPose().getX(DistanceUnit.INCH) - bio.blueGoalAud.getX(DistanceUnit.INCH);
                    distToGoalY = drivetrain.odoPose().getY(DistanceUnit.INCH) - bio.blueGoalAud.getY(DistanceUnit.INCH);
                    distToGoalTot = Math.hypot(distToGoalX, distToGoalY);
                }

            }

            TargetFlywheelSpeed = LinearInterpolationCalibration.interpolateVelo(distToGoalTot);
            targetHoodAngle = LinearInterpolationCalibration.interpolateHood(distToGoalTot);
//endregion

            //Sets the flyhwheel and hood to target based on distance
            flywheel.setVelocity(TargetFlywheelSpeed);
            hood.setPosition(targetHoodAngle);

            turretSet(90);


            telemetry.addData("Heading", currentHeading);
            telemetry.addData("Heading Power", +drivetrain.headingAlignment(drivetrain.odoPose().getHeading(AngleUnit.DEGREES), runTime.milliseconds()));
            drivetrain.CalculateMetrics(runTime.milliseconds(), drivetrain.odo.getHeadingVelocity(UnnormalizedAngleUnit.DEGREES));
            headingTime = runTime.milliseconds();
            // Calculate error

            telemetry.update();
        }
    }

    public void turretSet(double angle) {
        angle = Math.max(-180, Math.min(angle, 180));

        angle = angle / turretMaxRotation;
        double turretRange = turretMax - turretMin;
        //turret max roatation is the degrees srange of the turret,
        // while we use half the full range to calculate how far to scale the position
       // double turretPos = turretMin + ((angle + turretMaxRotation/2) / turretMaxRotation) * turretRange;
       double   turretPos = turretMin + ((angle - turretMinAngle) / (turretMaxAngle - turretMinAngle)) * turretRange;
        //x/1 = y/turretrange

        turret.setPosition(turretPos);

    }
}

// end class

