package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@Disabled
@TeleOp(name = "TeleOpM1", group = "TeleOp")
//@Disabled
public class DecodeTeleOpM1 extends OpMode {

    DecodeConstants dec = new DecodeConstants();

    public static PIDFCoefficients PID = new PIDFCoefficients(9.95, 0.075, 10.5, 13.4);

    private ElapsedTime runtime = new ElapsedTime();
    double looptime = 0;

    double x;
    double y;
    double z;

    double tpr = 28;
    double maxrpm = 5800;
    double maxspeed = maxrpm * tpr;

    String firstcolor = "None";
    String secondcolor = "None";
    String thirdcolor = "None";

    double newheading = 0;
    double goaldistanceT = 0;
    double goaldistanceX = 0;
    double goaldistanceY = 0;

    double ticksperrev = 28;
    double actualRPM = 0;
    double tickspersec = 0;
    double tickchange = 0;
    double atRPMcoeff = 60 / ticksperrev;

    double launchpower = 0.6;

    double g1dtime = 0;

    @Override
    public void init() {
        dec.fl = hardwareMap.get(DcMotor.class, "fl");
        dec.fr = hardwareMap.get(DcMotor.class, "fr");
        dec.bl = hardwareMap.get(DcMotor.class, "bl");
        dec.br = hardwareMap.get(DcMotor.class, "br");

        dec.launch = hardwareMap.get(DcMotorEx.class, "launch");

        dec.intake = hardwareMap.get(DcMotor.class, "intake");
        dec.transfer = hardwareMap.get(DcMotor.class, "transfer");

        dec.hood = hardwareMap.get(Servo.class, "hood");
        dec.susan = hardwareMap.get(Servo.class, "susan");

        dec.intakedos = hardwareMap.get(CRServo.class, "intakedos");

        dec.turret = hardwareMap.get(CRServo.class, "turret");
        dec.turretenc = hardwareMap.get(AnalogInput.class, "tenc");

        dec.odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");

        dec.f0 = hardwareMap.digitalChannel.get("f0");
        dec.f1 = hardwareMap.digitalChannel.get("f1");
        dec.r0 = hardwareMap.digitalChannel.get("r0");
        dec.r1 = hardwareMap.digitalChannel.get("r1");

        dec.fl.setDirection(DcMotor.Direction.REVERSE);
        dec.bl.setDirection(DcMotor.Direction.REVERSE);

        dec.transfer.setDirection(DcMotor.Direction.REVERSE);
        dec.intake.setDirection(DcMotor.Direction.REVERSE);

        dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        dec.bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        dec.br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        dec.bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        dec.br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        dec.taxon = new RTPAxon(dec.turret, dec.turretenc, RTPAxon.Direction.FORWARD);
        dec.taxon.setMaxPower(dec.tmaxp);
        dec.taxon.setRtp(false);

        // Turns on bulk reading
        for (LynxModule module : hardwareMap.getAll(LynxModule.class)) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        // RUE limits max motor speed to 85% by default
        // Raise that limit to 100%
        MotorConfigurationType motorConfigurationType = dec.launch.getMotorType().clone();
        motorConfigurationType.setAchieveableMaxRPMFraction(1.0);
        dec.launch.setMotorType(motorConfigurationType);

        // Turn on RUN_USING_ENCODER
        dec.launch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set PIDF Coefficients with voltage compensated feedforward value
        dec.launch.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
                PID.p, PID.i, PID.d, PID.f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
        ));

        dec.fl.setPower(0);
        dec.fr.setPower(0);
        dec.bl.setPower(0);
        dec.br.setPower(0);

        dec.launch.setVelocity(0);

        dec.intake.setPower(0);
        dec.intakedos.setPower(0);
        dec.transfer.setPower(0);

        dec.susan.setPosition(dec.susanpos1);
        dec.hood.setPosition(dec.hooddown);
        dec.turret.setPower(0);

        dec.configureOdo(true);
        dec.odo.setPosition(new Pose2D(DistanceUnit.INCH, 63, 9, AngleUnit.DEGREES, 90));
        newheading = dec.odo.getPosition().getHeading(AngleUnit.DEGREES) + 180;

        runtime.reset();
        looptime = runtime.milliseconds();
    }

    public void loop() {
        telemetry.addData("Loop Time: ", runtime.milliseconds() - looptime);
        looptime = runtime.milliseconds();

        dec.odo.update();
        dec.taxon.update();

        if (gamepad1.b) {
            dec.goal = dec.Rgoal;
            dec.teamcolor = "Red";
        } else if (gamepad1.x) {
            dec.goal = dec.Bgoal;
            dec.teamcolor = "Blue";
            dec.odo.setPosition(new Pose2D(DistanceUnit.INCH, dec.odo.getPosition().getX(DistanceUnit.INCH),
                    (dec.odo.getPosition().getY(DistanceUnit.INCH) - 18), AngleUnit.DEGREES, dec.odo.
                    getPosition().getHeading(AngleUnit.DEGREES)));
        }

        if (dec.odo.getPosition().getHeading(AngleUnit.DEGREES) > 0) {
            newheading = dec.odo.getPosition().getHeading(AngleUnit.DEGREES) - 180;
        } else {
            newheading = dec.odo.getPosition().getHeading(AngleUnit.DEGREES) + 180;
        }

        goaldistanceX = dec.goal.getX(DistanceUnit.INCH) - dec.odo.getPosition().getX(DistanceUnit.INCH);
        goaldistanceY = dec.goal.getY(DistanceUnit.INCH) - dec.odo.getPosition().getY(DistanceUnit.INCH);
        goaldistanceT = Math.sqrt(Math.pow(goaldistanceX, 2) + Math.pow(goaldistanceY, 2));

        x = gamepad1.right_stick_x;
        y = -gamepad1.left_stick_y;
        z = gamepad1.left_stick_x;

        dec.flpower = Range.clip(y + x + z, -1, 1);
        dec.frpower = Range.clip(y - x - z, -1, 1);
        dec.blpower = Range.clip(y + x - z, -1, 1);
        dec.brpower = Range.clip(y - x + z, -1, 1);

        dec.fl.setPower(dec.flpower);
        dec.fr.setPower(dec.frpower);
        dec.bl.setPower(dec.blpower);
        dec.br.setPower(dec.brpower);

        telemetry.addData("Team Color: ", dec.teamcolor);

        dec.fangle = Math.atan((dec.goal.getY(DistanceUnit.INCH) - dec.odo.getPosition().getY(
                DistanceUnit.INCH)) / (dec.goal.getX(DistanceUnit.INCH) - dec.odo.getPosition().getX(
                DistanceUnit.INCH))) * (180 / Math.PI) + 90; //-90

        telemetry.addData("Angle to Goal: ", dec.fangle);

        if (gamepad1.a) {
            if (Math.abs(newheading - dec.fangle) > 5) {
                if (newheading > dec.fangle) {
                    dec.flpower = Range.clip((newheading - dec.fangle) / 90, 0.2, 1);
                    dec.frpower = Range.clip((dec.fangle - newheading) / 90, -1, -0.2);
                    dec.blpower = Range.clip((newheading - dec.fangle) / 90, 0.2, 1);
                    dec.brpower = Range.clip((dec.fangle - newheading) / 90, -1, -0.2);
                } else {
                    dec.flpower = Range.clip((dec.fangle - newheading) / 90, -1, -0.2);
                    dec.frpower = Range.clip((newheading - dec.fangle) / 90, 0.2, 1);
                    dec.blpower = Range.clip((dec.fangle - newheading) / 90, -1, -0.2);
                    dec.brpower = Range.clip((newheading - dec.fangle) / 90, 0.2, 1);
                }
            } else {
                dec.flpower = 0;
                dec.frpower = 0;
                dec.blpower = 0;
                dec.brpower = 0;
            }

            dec.fl.setPower(dec.flpower);
            dec.fr.setPower(dec.frpower);
            dec.bl.setPower(dec.blpower);
            dec.br.setPower(dec.brpower);
        }

        if (gamepad1.y) {
            dec.odo.setPosition(new Pose2D(DistanceUnit.INCH, 63, 0, AngleUnit.DEGREES, 90));
        }

        /*if (((gamepad1.left_stick_y < -0.5) || (gamepad1.left_stick_y > 0.5)) && ((runtime.milliseconds() - intaketime) > 200)) {
            intaketime = runtime.milliseconds();

            if (gamepad1.left_stick_y > 0.5) {
                inspeed = inspeed + 1;
            } else if (gamepad1.left_stick_y < -0.5) {
                inspeed = inspeed - 1;
            }

            if (inspeed < -1) {
                inspeed = -1;
            } else if (inspeed > 1) {
                inspeed = 1;
                dec.susan.setPosition(dec.susanpos1);
            }
        }

        dec.intake.setPower(inspeed);
        dec.intakedos.setPower(inspeed);*/

        dec.intake.setPower(-gamepad2.left_stick_y);

        if (gamepad2.b) {
            dec.susan.setPosition(dec.susanpos1);
        } else if (gamepad2.a) {
            dec.susan.setPosition(dec.susanpos2);
        } else if (gamepad2.x) {
            dec.susan.setPosition(dec.susanpos3);
        } else if (gamepad2.y) {
            dec.susan.setPosition(dec.susanbtw31);
        } else if (gamepad2.left_trigger > 0.25) {
            dec.susan.setPosition(dec.susanpre1);
        }

        if (dec.intake.getPower() > 0) {
            dec.intakedos.setPower(1);
        } else if (dec.intake.getPower() < 0) {
            dec.intakedos.setPower(-1);
        } else {
            dec.intakedos.setPower(1);
        }

        /*if (dec.susan.getPosition() == dec.susanpos1) {
            if (dec.f0.getState()) {
                telemetry.addData("F Color: ", "Purple");
                firstcolor = "Purple";
            } else if (dec.f1.getState()) {
                telemetry.addData("F Color: ", "Green");
                firstcolor = "Green";
            } else {
                telemetry.addData("F No Color", "");
                firstcolor = "None";
            }

            if (!firstcolor.matches("None")) {
                dec.susan.setPosition(dec.susanpos2);
            }
        } else if (dec.susan.getPosition() == dec.susanpos2) {
            if (dec.f0.getState()) {
                telemetry.addData("F Color: ", "Purple");
                secondcolor = "Purple";
            } else if (dec.f1.getState()) {
                telemetry.addData("F Color: ", "Green");
                secondcolor = "Green";
            } else {
                telemetry.addData("F No Color", "");
                secondcolor = "None";
            }

            if (!secondcolor.matches("None")) {
                dec.susan.setPosition(dec.susanpos3);
            }
        } else if (dec.susan.getPosition() == dec.susanpos3) {
            if (dec.f0.getState()) {
                telemetry.addData("F Color: ", "Purple");
                thirdcolor = "Purple";
            } else if (dec.f1.getState()) {
                telemetry.addData("F Color: ", "Green");
                thirdcolor = "Green";
            } else {
                telemetry.addData("F No Color", "");
                thirdcolor = "None";
            }

            /*if (!thirdcolor.matches("None")) {
                dec.intake.setPower(0);
                dec.intakedos.setPower(-1);
            }
        } else {
            telemetry.addData("N/A Color", "");
        }*/

        if (dec.r0.getState()) {
            telemetry.addData("R Color: ", "Purple");
        } else if (dec.r1.getState()) {
            telemetry.addData("R Color: ", "Green");
        } else {
            telemetry.addData("R No Color", "");
        }

        telemetry.addData("First Color: ", firstcolor);
        telemetry.addData("Second Color: ", secondcolor);
        telemetry.addData("Third Color: ", thirdcolor);

        /*if (gamepad2.a) {
            g2atime = runtime.milliseconds() + 200;

            if (thirdcolor.matches("Green")) {
                dec.susan.setPosition(dec.susanpos3);
            } else if (secondcolor.matches("Green")) {
                dec.susan.setPosition(dec.susanpos2);
            } else if (firstcolor.matches("Green")) {
                dec.susan.setPosition(dec.susanpos1);
            }

            launch = true;
        } else if (gamepad2.x) {
            g2xtime = runtime.milliseconds() + 200;

            if (thirdcolor.matches("Purple")) {
                dec.susan.setPosition(dec.susanpos3);
            } else if (secondcolor.matches("Purple")) {
                dec.susan.setPosition(dec.susanpos2);
            } else if (firstcolor.matches("Purple")) {
                dec.susan.setPosition(dec.susanpos1);
            }

            launch = true;
        } else if (gamepad2.b) {
            g2btime = runtime.milliseconds() + 200;

            if (dec.susan.getPosition() == dec.susanpos1) {
                dec.susan.setPosition(dec.susanpos2);
            } else if (dec.susan.getPosition() == dec.susanpos2) {
                dec.susan.setPosition(dec.susanpos3);
            } else if (dec.susan.getPosition() == dec.susanpos3) {
                dec.susan.setPosition(dec.susanpos1);
            } else {
                dec.susan.setPosition(dec.susanpos3);
            }

            launch = true;
        }*/

        /*if (gamepad2.a) {
            g2atime = runtime.milliseconds() + 200;

            dec.susan.setPosition(dec.susanpos3);

            launch = true;
        } else if (gamepad2.x) {
            g2xtime = runtime.milliseconds() + 200;

            dec.susan.setPosition(dec.susanpos2);

            launch = true;
        } else if (gamepad2.b) {
            g2btime = runtime.milliseconds() + 200;

            dec.susan.setPosition(dec.susanpos1);

            launch = true;
        }*/

        if (goaldistanceT < 62.5) {
            if (goaldistanceT > 52.5) {
                dec.hood.setPosition(0);
            } else if (goaldistanceT > 47.5) {
                dec.hood.setPosition(0.11);
            } else if (goaldistanceT > 42.5) {
                dec.hood.setPosition(0.16);
            } else if (goaldistanceT > 32.5) {
                dec.hood.setPosition(0.18);
            } else if (goaldistanceT > 20) {
                dec.hood.setPosition(0.13);
            } else {
                dec.hood.setPosition(0);
            }

            dec.launch.setPower(launchpower);
        } else if (goaldistanceT < 82.5) {
            if (goaldistanceT > 77.5) {
                dec.hood.setPosition(0.18);
            } else if (goaldistanceT > 72.5) {
                dec.hood.setPosition(0.16);
            } else if (goaldistanceT > 67.5) {
                dec.hood.setPosition(0.32);
            } else {
                dec.hood.setPosition(0.28);
            }

            dec.launch.setPower(launchpower + 0.05);
        } else {
            dec.launch.setPower(launchpower + 0.1);
            dec.hood.setPosition(0.46);
        }

        if (gamepad1.dpad_up && (g1dtime < runtime.milliseconds())) {
            launchpower = launchpower + 0.05;
            g1dtime = runtime.milliseconds() + 200;
        } else if (gamepad1.dpad_down && (g1dtime < runtime.milliseconds())) {
            launchpower = launchpower - 0.05;
            g1dtime = runtime.milliseconds() + 200;
        }

        telemetry.addData("Launch Power: ", launchpower);

        tickspersec = tickchange / (looptime / 1000);
        actualRPM = atRPMcoeff * tickspersec;
        telemetry.addData("Actual RPM: ", actualRPM);

        /*if (((g2atime > runtime.milliseconds()) || (g2xtime > runtime.milliseconds()) ||
                (g2btime > runtime.milliseconds())) && launch) {
            launchtime = runtime.milliseconds() + 1000;
            launching = true;
            dec.transfer.setPower(1);
            launch = false;
        }

        if (launching && (launchtime > runtime.milliseconds())) {
            launching = false;
        }*/

        if (gamepad2.dpad_up) {
            dec.transfer.setPower(1);
        } else if (gamepad2.dpad_down) {
            dec.transfer.setPower(-1);
        } else /*if (!launching)*/ {
            dec.transfer.setPower(0);
        }

        if (gamepad2.left_bumper) {
            dec.susan.setPosition(dec.susan.getPosition() + 0.005);
        } else if (gamepad2.right_stick_button) {
            dec.susan.setPosition(dec.susan.getPosition() - 0.005);
        }

        dec.taxon.setTargetRotation(dec.turretmiddle);

        dec.turretpwrcoeff = Range.clip(Math.abs(dec.taxon.getTotalRotation() -
                dec.taxon.getTargetRotation()) / 150, 1, 7);
        dec.turretpower = Range.clip((dec.tminp * dec.turretpwrcoeff), 0.16, 1);

        if (dec.taxon.getTotalRotation() > (dec.taxon.getTargetRotation() + dec.ttol)) {
            dec.turret.setPower(-dec.turretpower);
        } else if (dec.taxon.getTotalRotation() < (dec.taxon.getTargetRotation() - dec.ttol)) {
            dec.turret.setPower(dec.turretpower);
        } else {
            dec.turret.setPower(0);
        }

        /*if (gamepad1.right_stick_button) {
            dec.intake.setPower(0);
            g1rsb = true;
            g1rsbtime = runtime.milliseconds();
            //launcher on
        }

        if (g1rsb) {
            dec.susan.setPosition(dec.susanpos1);

            if ((g1rsbtime + 250) > runtime.milliseconds()) {
                dec.transfer.setPower(1);

                if ((g1rsbtime + 500) > runtime.milliseconds()) {
                    dec.susan.setPosition(dec.susanpos2);

                    if ((g1rsbtime + 1000) > runtime.milliseconds()) {
                        dec.susan.setPosition(dec.susanpos2);

                        if ((g1rsbtime + 1250) > runtime.milliseconds()) {
                            dec.transfer.setPower(0);
                            g1rsb = false;
                            dec.intake.setPower(1);
                        }
                    }
                }
            }
        }*/

        telemetry.addData("Susan Pos: ", dec.susan.getPosition());
        telemetry.addLine(String.format("OdoPos: %6.2f %6.2f %6.2f (-90)", dec.odo.getPosition().getX(DistanceUnit.INCH),
                dec.odo.getPosition().getY(DistanceUnit.INCH), (dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) - 90));
        telemetry.update();
    }
}
