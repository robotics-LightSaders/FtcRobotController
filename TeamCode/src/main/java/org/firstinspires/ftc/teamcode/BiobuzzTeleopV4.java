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


import com.qualcomm.hardware.lynx.LynxModule;
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
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;
//

@TeleOp(name = "Biobuzz Meet 1", group = "Teleop")

public class BiobuzzTeleopV4 extends OpMode {

    BiobuzzConstants bio = new BiobuzzConstants();


    //region Devices


    //region Variables
    ElapsedTime runTime = new ElapsedTime();

    //   private AHRS navx_device;


    double speedModifier = 1;

    double resetAngle = 0;
    double Tinc = .001;

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

    double distToGoalX;
    double distToGoalY;
boolean firstLoop = true;
Pose2D bluePos;
    Pose2D redPos;
    MatchLog log;   // match recorder (read only): /sdcard/FIRST/matchlogs


 //   boolean AllianceIsRed = false;

 //   double turretMin = 0;
 //   double turretMax = .5;
    //the turrets maximum rotation angle in degrees
  //  double turretMaxRotation = 360;
 //   double turretMinAngle = -180;
 //   double turretMaxAngle = 180;


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
        bio.intakeb.setDirection(DcMotorSimple.Direction.REVERSE);

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


        bio.configureOdo(false); //false


        // Reset the runtime
        runTime.reset();
        bluePos = new Pose2D(DistanceUnit.INCH,-bio.odo.getPosition().getX(DistanceUnit.INCH)+6,-bio.odo.getPosition().getY(DistanceUnit.INCH)+9,(AngleUnit.DEGREES),(bio.odo.getPosition().getHeading(AngleUnit.DEGREES)+176)%360);
        redPos = bio.odo.getPosition();
        log = new MatchLog(hardwareMap, gamepad1, gamepad2, "teleop");   // match recorder (read only)
            }

    public void init_loop() {
        //pick color, side etc here, set auto position
    //    bio.turret.setPosition(bio.t0);

        if (gamepad1.aWasPressed()){
            bio.teamColor = "Red";
            bio.odo.setPosition(redPos);
        } else if (gamepad1.bWasPressed()){
            bio.teamColor = "Blue";
            bio.odo.setPosition(bluePos);
        }

        if (gamepad1.x){
            bio.odo.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));
        }



        telemetry.addData("team color", bio.teamColor);
        telemetry.addData("Pos: ", Math.round(bio.odo.getPosition().getX(DistanceUnit.INCH)) + ", " +
                Math.round(bio.odo.getPosition().getY(DistanceUnit.INCH)) + ", " + Math.round(bio.odo.
                getPosition().getHeading(AngleUnit.DEGREES)));
        telemetry.addData("Turret Pos", bio.turret.getPosition());

        telemetry.update();
    }

    public void loop() {
if (firstLoop){
    firstLoop = false;
    bio.turret.setPosition(bio.t0);
}
        // region - Team Color

        if (gamepad1.a){
            bio.teamColor = "Red";
        } else if (gamepad1.b){
            bio.teamColor = "Blue";
        }

        //endregion

        //region - Corner Relocalize
        if (gamepad1.xWasPressed()){

            if (bio.teamColor == "Red") { //CHANGE
                bio.odo.setPosition(new Pose2D(DistanceUnit.INCH, -60, 72.84, AngleUnit.DEGREES, 272));
            //-62,78
            } else {
                bio.odo.setPosition(new Pose2D(DistanceUnit.INCH, 68.3, -65.19, AngleUnit.DEGREES, 92));
            }

        }
        //endregion

        //region - Odo

        bio.odo.update();

        bio.angvel = Math.abs(bio.odo.getHeadingVelocity(UnnormalizedAngleUnit.DEGREES));
        bio.xvel = Math.abs(bio.odo.getVelX(DistanceUnit.INCH));
        bio.yvel = Math.abs(bio.odo.getVelY(DistanceUnit.INCH));

if (gamepad1.xWasPressed()){}
       ;

        //endregion

        //region - Limelight
            /*

            if (result != null && result.isValid()) {
                for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {
                    double tagId = tag.getFiducialId();
                    double bearing = tag.getTargetXDegrees(); // Horizontal offset (bearing) in degrees

                    telemetry.addData("Tag ID", tagId);
                    telemetry.addData("Bearing (tx)", "%.2f degrees", bearing);
                }
            }*/
        //endregion

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

     //   telemetry.addData("Launchy", bio.launching);

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
        } else if (!gamepad2.y) {
            bio.intakef.setPower(0);
        }

        if (gamepad2.right_bumper) {
            bio.intakeb.setPower(1);
            bio.transfer.setPower(.4);
        } else if (gamepad2.left_bumper) {
            bio.intakeb.setPower(-1);
            bio.transfer.setPower(-.4);
        } else if (!gamepad2.y) {
            bio.intakeb.setPower(0);
            bio.transfer.setPower(0);
        }

        //endregion

        // region - Launching

        if (gamepad2.y) {
            bio.transfer.setPower(1);
            bio.intakef.setPower(1);
            bio.intakeb.setPower(1);

        }
//endregion

        //region - Turret

        /* this code makes it easy to find specific values

        if(gamepad2.dpad_up){
            Tinc = .005;
        }else if(gamepad2.dpad_down){
            Tinc = .001;
        }



        if (gamepad2.dpad_left){
            bio.turret.setPosition(bio.turret.getPosition() + Tinc);
        } else if(gamepad2.dpad_right){
            bio.turret.setPosition(bio.turret.getPosition() - Tinc);
        }

         */

        if (gamepad2.a  && !bio.tPress){

            bio.tPress = true;

            if (!bio.tTracking){

                bio.tTracking = true;


            } else {

                bio.tTracking = false;

            }

        }
        if (bio.tTracking){
            bio.turret.setPosition(bio.tTarget( ((bio.tAng(bio.turret.getPosition()))) , bio.odo.getPosition(), bio.goal, bio.offsett, bio.side));

            if (bio.distToGoalTot<35){
                bio.flywheel.setVelocity((bio.targetFlywheelSpeed-35) / 60 * 28);

            }else {
                bio.flywheel.setVelocity(bio.targetFlywheelSpeed / 60 * 28);

            }
            bio.hood.setPosition(bio.targetHoodAngle);
            gamepad2.rumble(Gamepad.RUMBLE_DURATION_CONTINUOUS);


        } else {
            bio.turret.setPosition(bio.t0);
            bio.flywheel.setVelocity(bio.idleSpeed  / 60 * 28);
            bio.hood.setPosition(bio.hooddown);
            gamepad2.rumble(0);


        }


       if (!gamepad2.a){
           bio.tPress = false;
       }






        //endregion

        //region - Distance Calc

        if (bio.odo.getPosition().getY(DistanceUnit.INCH) > 0) {
            bio.side = "Back";
        } else {
            bio.side = "Audience";
        }

        if (bio.teamColor=="Red") {
            if (bio.side == "Back") {
                bio.goal = bio.redGoalBack;

            } else {
                bio.goal = bio.redGoalAud;

            }
        } else {
            if (bio.side == "Back") {
                bio.goal = bio.blueGoalBack;
            } else {
                bio.goal = bio.blueGoalAud;
            }
        }

        telemetry.addData("Goalx ",bio.goal.getX(DistanceUnit.INCH));
        telemetry.addData("Goaly ",bio.goal.getY(DistanceUnit.INCH));

        bio.distToGoalTot = bio.goalDC(bio.odo.getPosition(), bio.goal);

        bio.targetFlywheelSpeed = bio.interpolateVelo((bio.distToGoalTot + bio.offsetf));
     //   bio.targetFlywheelSpeed = 2200;
        bio.targetHoodAngle = bio.interpolateHood(bio.distToGoalTot + bio.offsetf);
//endregion

        //region - Offsets

        if (gamepad2.dpadUpWasPressed()){
            bio.offsetf = bio.offsetf + 1;
        } else if(gamepad2.dpadDownWasPressed()){
            bio.offsetf = bio.offsetf - 1;
        }

        if (gamepad2.dpadRightWasPressed()){
            bio.offsett = bio.offsett + 3;
        }
        if(gamepad2.dpadLeftWasPressed()){
            bio.offsett = bio.offsett - 3;
        }

        if (gamepad2.xWasPressed()){
            bio.offsetf = 0;
            bio.offsett = 0;
        }

        //endregion

        //region - Telemetry
        telemetry.addLine(String.format("OdoPos: %6.2f %6.2f %6.2f ", bio.odo.getPosition().getX(DistanceUnit.INCH),
                bio.odo.getPosition().getY(DistanceUnit.INCH), (bio.odo.getPosition().getHeading(AngleUnit.DEGREES))));
        telemetry.addData("side", bio.side);
        telemetry.addData("color", bio.teamColor);
        telemetry.addData("dist to goal", bio.distToGoalTot);
        telemetry.addData("target flywheel vel (RPM)", bio.targetFlywheelSpeed);
        telemetry.addData("target hood pos", bio.targetHoodAngle);
        telemetry.addData("Distance Offset (IN)", bio.offsetf);
        telemetry.addData("Turret Offset (DEG)", bio.offsett);
      //  telemetry.addData("Turret Pos", bio.turret.getPosition());
    //    telemetry.addData("Turret Ang", bio.tAng(bio.turret.getPosition()));
    //    telemetry.addData("Goal Ang", bio.goalAng(bio.odo.getPosition(),bio.goal, bio.side));
        telemetry.addData("target goal", bio.goal);
      //  telemetry.addData("Turret Goal Ang", bio.tGoalAng(bio.tAng(bio.turret.getPosition()), bio.odo.getPosition(),bio.goal, bio.side));
      //  telemetry.addData("Turret Goal Pos", bio.tTarget(bio.tAng(bio.turret.getPosition()), bio.odo.getPosition(),bio.goal, bio.side));
        telemetry.update();

        //endregion

        //region - Logs
        if (log != null) {   // match recorder (read only)
            log.put("target_rpm", bio.targetFlywheelSpeed);
            log.put("dist_in", bio.distToGoalTot);
            log.put("offset_dist_in", bio.offsetf);
            log.put("offset_turret_deg", bio.offsett);
            log.put("aiming", bio.tTracking ? 1 : 0);
            log.put("red", "Red".equals(bio.teamColor) ? 1 : 0);
            log.row();
        }
        //endregion





    }

    @Override
    public void stop() {   // match recorder (read only): writes its file
        if (log != null) log.close();
    }
}
// end class

