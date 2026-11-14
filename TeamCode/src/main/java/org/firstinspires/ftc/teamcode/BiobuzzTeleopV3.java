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


import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;
//
@Disabled
@TeleOp(name = "Biobuzz Teleop V3", group = "Teleop")

public class BiobuzzTeleopV3 extends OpMode {

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

    double x;
    double y;
    double z;

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
    public void init() {
        //region Hardware Map

        bio.fl = hardwareMap.get(DcMotor.class, "fl");
        bio.fr = hardwareMap.get(DcMotor.class, "fr");
        bio.br = hardwareMap.get(DcMotor.class, "br");
        bio.bl = hardwareMap.get(DcMotor.class, "bl");

        bio.flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");

        bio.intakef = hardwareMap.get(DcMotorEx.class, "intakef");
        bio.transfer = hardwareMap.get(DcMotorEx.class, "transfer");

        bio.turret = hardwareMap.get(Servo.class, "turret");
        bio.hood = hardwareMap.get(Servo.class, "hood");
        bio.intakeb = hardwareMap.get(CRServo.class, "intakeb");

      //  bio.limelight = hardwareMap.get(Limelight3A.class, "limelight");
        bio.odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");

        bio.fl.setDirection(DcMotorSimple.Direction.REVERSE);
        bio.bl.setDirection(DcMotorSimple.Direction.REVERSE);
        bio.intakef.setDirection(DcMotorSimple.Direction.REVERSE);
        bio.transfer.setDirection(DcMotorSimple.Direction.REVERSE);
        bio.turret.setDirection(Servo.Direction.REVERSE);


        bio.flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bio.flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(0, 0, 0, 0));


        // Turns on bulk reading
        for (LynxModule module : hardwareMap.getAll(LynxModule.class)) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        // RUE limits max motor speed to 85% by default
        // Raise that limit to 100%
        MotorConfigurationType motorConfigurationType = bio.flywheel.getMotorType().clone();
        motorConfigurationType.setAchieveableMaxRPMFraction(1.0);
        bio.flywheel.setMotorType(motorConfigurationType);

        // Turn on RUN_USING_ENCODER
        bio.flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set PIDF Coefficients with voltage compensated feedforward value
        bio.flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
                bio.PIDF.p, bio.PIDF.i, bio.PIDF.d, bio.PIDF.f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
        ));

        //     limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
        //     limelight.start();
        //endregion

        //      LLResult result = limelight.getLatestResult();

        bio.fl.setPower(0);
        bio.fr.setPower(0);
        bio.bl.setPower(0);
        bio.br.setPower(0);

        bio.flywheel.setVelocity(0);
        bio.intakef.setPower(0);
        bio.transfer.setPower(0);


        bio.configureOdo(false);


        // Reset the runtime
        runTime.reset();
    }

    public void init_loop() {
        //pick color, side etc here, set auto position
    }

    public void loop() {

        //region - Odo

        bio.odo.update();

        bio.angvel = Math.abs(bio.odo.getHeadingVelocity(UnnormalizedAngleUnit.DEGREES));
        bio.xvel = Math.abs(bio.odo.getVelX(DistanceUnit.INCH));
        bio.yvel = Math.abs(bio.odo.getVelY(DistanceUnit.INCH));




        //endregion

            /*

            if (result != null && result.isValid()) {
                for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {
                    double tagId = tag.getFiducialId();
                    double bearing = tag.getTargetXDegrees(); // Horizontal offset (bearing) in degrees

                    telemetry.addData("Tag ID", tagId);
                    telemetry.addData("Bearing (tx)", "%.2f degrees", bearing);
                }
            }*/

        //region - Driving
        x = gamepad1.left_stick_x;
        y = -gamepad1.left_stick_y;
        z = gamepad1.right_stick_x;


        bio.flpower = Range.clip(y + x + z, -1, 1);
        bio.frpower = Range.clip(y - x - z, -1, 1);
        bio.blpower = Range.clip(y - x + z, -1, 1);
        bio.brpower = Range.clip(y + x - z, -1, 1);

        bio.fl.setPower(bio.flpower);
        bio.fr.setPower(bio.frpower);
        bio.bl.setPower(bio.blpower);
        bio.br.setPower(bio.brpower);



        if (((gamepad1.left_trigger > 0.25) && (gamepad1.right_trigger > 0.25)) || bio.launching) {
            bio.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            bio.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            bio.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            bio.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        } else {
            bio.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            bio.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            bio.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            bio.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);


        }
        //endregion

        //region - Intake
        if (gamepad2.right_trigger > 0.2) {
            bio.intakef.setPower(gamepad2.right_trigger);
        } else if (gamepad2.left_trigger > 0.2) {
            bio.intakef.setPower(-gamepad2.left_trigger);
        } else {
            bio.intakef.setPower(0);
        }

        if (gamepad2.right_bumper) {
            bio.intakeb.setPower(1);
        } else if (gamepad2.left_bumper) {
            bio.intakeb.setPower(-1);
        } else {
            bio.intakeb.setPower(0);
        }

        //endregion

        // region - Launching VERY ROUGH

        if (gamepad2.y) {
            bio.transfer.setPower(1);
            bio.intakef.setPower(1);
            bio.flywheel.setPower(.06); //test speed for general launch
        } else {
            bio.transfer.setPower(0);
            bio.flywheel.setPower(.045); //idle speed
        }
//endregion


        //region - Distance Calc
        if (AllianceIsRed) {
            if (bio.odo.getPosition().getY(DistanceUnit.INCH) > 0) {
                bio.goal = bio.redGoalBack;
            } else {
                bio.goal = bio.redGoalAud;
            }
        } else {
            if (bio.odo.getPosition().getY(DistanceUnit.INCH) > 0) {
                bio.goal = bio.blueGoalBack;
            } else {
                bio.goal = bio.blueGoalAud;
            }
        }

        distToGoalTot = bio.goalDC(bio.odo.getPosition(), bio.goal);


        TargetFlywheelSpeed = LinearInterpolationCalibration.interpolateVelo(distToGoalTot);
        targetHoodAngle = LinearInterpolationCalibration.interpolateHood(distToGoalTot);
//endregion

        //region - Telemetry
        telemetry.addLine(String.format("OdoPos: %6.2f %6.2f %6.2f (-90)", bio.odo.getPosition().getX(DistanceUnit.INCH),
                bio.odo.getPosition().getY(DistanceUnit.INCH), (bio.odo.getPosition().getHeading(AngleUnit.DEGREES)) - 90));
        telemetry.addData("Turret Pos", bio.turret.getPosition());

        telemetry.update();

        //endregion


        /*

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
        double turretPos = turretMin + ((angle - turretMinAngle) / (turretMaxAngle - turretMinAngle)) * turretRange;
        //x/1 = y/turretrange

        turret.setPosition(turretPos);

    }

         */
    }

}


// end class

