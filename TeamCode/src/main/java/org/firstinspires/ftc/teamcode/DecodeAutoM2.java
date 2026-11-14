//package org.firstinspires.ftc.teamcode;
//
//import android.util.Log;
//import android.util.Size;
//
//import com.qualcomm.hardware.lynx.LynxModule;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.hardware.AnalogInput;
//import com.qualcomm.robotcore.hardware.CRServo;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
//import com.qualcomm.robotcore.hardware.PIDFCoefficients;
//import com.qualcomm.robotcore.hardware.Servo;
//import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
//import com.qualcomm.robotcore.util.ElapsedTime;
//import com.qualcomm.robotcore.util.Range;
//
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
//import org.firstinspires.ftc.robotcore.external.navigation.Position;
//import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
//import org.firstinspires.ftc.vision.VisionPortal;
//import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
//import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
//
//import java.util.List;
//
//@Disabled
//@Autonomous(name = "AutoM2", group = "Linear Opmode")
//public class DecodeAutoM2 extends LinearOpMode {
//    DecodeConstants dec = new DecodeConstants();
//
//    private ElapsedTime runtime = new ElapsedTime();
//
//    private AprilTagProcessor aprilTag;
//    private VisionPortal visionPortal;
//
//    private Position cameraPosition = new Position(DistanceUnit.INCH,
//            0, 0, 7, 0); //6.2, 4.6 to start
//    private YawPitchRollAngles cameraOrientation = new YawPitchRollAngles(AngleUnit.DEGREES,
//            0, -90, 0, 0);
//
//    public static PIDFCoefficients PID = new PIDFCoefficients(9.95, 0.075, 10.5, 13.4);
//
//    double drivetimeout = 0;
//
//    double pos1 = dec.susanlaunch1;
//    double pos2 = dec.susanlaunch2;
//    double pos3 = dec.susanlaunch3;
//
//    double switchtime = 0;
//
//    double ed0;
//    double ed1;
//    double ed2;
//    double cte1;
//    double cte2;
//
//    @Override
//    public void runOpMode() {
//        //region - Hardware Map
//        dec.fl = hardwareMap.get(DcMotor.class, "fl");
//        dec.fr = hardwareMap.get(DcMotor.class, "fr");
//        dec.bl = hardwareMap.get(DcMotor.class, "bl");
//        dec.br = hardwareMap.get(DcMotor.class, "br");
//
//        dec.launch = hardwareMap.get(DcMotorEx.class, "launch");
//
//        dec.intake = hardwareMap.get(DcMotor.class, "intake");
//        dec.transfer = hardwareMap.get(DcMotor.class, "transfer");
//
//        dec.hood = hardwareMap.get(Servo.class, "hood");
//        dec.susan = hardwareMap.get(Servo.class, "susan");
//        dec.camrot = hardwareMap.get(Servo.class, "camrot");
//
//        dec.intakedos = hardwareMap.get(CRServo.class, "intakedos");
//
//        dec.turret = hardwareMap.get(CRServo.class, "turret");
//        dec.turretenc = hardwareMap.get(AnalogInput.class, "tenc");
//
//        dec.odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");
//
//        dec.f0 = hardwareMap.digitalChannel.get("f0");
//        dec.f1 = hardwareMap.digitalChannel.get("f1");
//        dec.r0 = hardwareMap.digitalChannel.get("r0");
//        dec.r1 = hardwareMap.digitalChannel.get("r1");
//        dec.l0 = hardwareMap.digitalChannel.get("l0");
//        dec.l1 = hardwareMap.digitalChannel.get("l1");
//
//        dec.f2 = hardwareMap.get(NormalizedColorSensor.class, "f2");
//        dec.r2 = hardwareMap.get(NormalizedColorSensor.class, "r2");
//        dec.l2 = hardwareMap.get(NormalizedColorSensor.class, "l2");
//
//        dec.fl.setDirection(DcMotor.Direction.REVERSE);
//        dec.bl.setDirection(DcMotor.Direction.REVERSE);
//
//        dec.transfer.setDirection(DcMotor.Direction.REVERSE);
//        dec.intake.setDirection(DcMotor.Direction.REVERSE);
//
//        dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//        dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//        dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//        dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//
//        dec.bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        dec.br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//
//        dec.bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        dec.br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//
//        dec.initTaxon();
//
//        // Turns on bulk reading
//        for (LynxModule module : hardwareMap.getAll(LynxModule.class)) {
//            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
//        }
//
//        // RUE limits max motor speed to 85% by default
//        // Raise that limit to 100%
//        MotorConfigurationType motorConfigurationType = dec.launch.getMotorType().clone();
//        motorConfigurationType.setAchieveableMaxRPMFraction(1.0);
//        dec.launch.setMotorType(motorConfigurationType);
//
//        // Turn on RUN_USING_ENCODER
//        dec.launch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//
//        // Set PIDF Coefficients with voltage compensated feedforward value
//        dec.launch.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
//                PID.p, PID.i, PID.d, PID.f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
//        ));
//
//        initAprilTag();
//
//        while (!gamepad1.a) {
//            if (gamepad1.x) {
//                dec.teamcolor = "Blue";
//                dec.goal = dec.Bgoal;
//            } else if (gamepad1.b) {
//                dec.teamcolor = "Red";
//                dec.goal = dec.Rgoal;
//            }
//
//            dec.taxon.update();
//
//            telemetry.addData("Team Color: ", dec.teamcolor);
//            telemetry.addData("Turret Target: ", dec.taxon.getTargetRotation());
//            telemetry.addData("Turret At: ", dec.taxon.getTotalRotation());
//            telemetryAprilTag();
//            telemetry.update();
//
//            if (dec.teamcolor.matches("Red")) {
//                dec.taxon.setTargetRotation(dec.turret90); //find obelisk?
//            } else {
//                dec.taxon.setTargetRotation(dec.turret270);
//            }
//
//            if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) > 200) {
//                dec.turretpwrcoeff = 10;
//            } else {
//                dec.turretpwrcoeff = Range.clip((Math.abs(dec.taxon.getTotalRotation() -
//                        dec.taxon.getTargetRotation()) / 50), 1, 10); //25, with /2 for distance
//            }
//
//            dec.turretpower = Range.clip((dec.tminp * dec.turretpwrcoeff), 0.08, 1); //0.09?
//
//            if (gamepad1.y) {
//                if (dec.taxon.getTotalRotation() > (dec.taxon.getTargetRotation() + dec.ttol)) {
//                    dec.turret.setPower(dec.turretpower);
//                } else if (dec.taxon.getTotalRotation() < (dec.taxon.getTargetRotation() - dec.ttol)) {
//                    dec.turret.setPower(-dec.turretpower);
//                } else {
//                    dec.turret.setPower(0);
//                }
//            } else {
//                dec.turret.setPower(0);
//            }
//        }
//
//        dec.fl.setPower(0);
//        dec.fr.setPower(0);
//        dec.bl.setPower(0);
//        dec.br.setPower(0);
//
//        dec.launch.setVelocity(0);
//
//        dec.intake.setPower(0);
//        dec.intakedos.setPower(0);
//        dec.transfer.setPower(0);
//
//        dec.susan.setPosition(dec.susanauto3); //pos3?
//        dec.hood.setPosition(dec.hooddown);
//        dec.camrot.setPosition(dec.camlong);
//        dec.turret.setPower(0);
//
//        dec.configureOdo(true);
//        dec.odo.setPosition(dec.odoorigin);
//
//        dec.LAUNCH = dec.odoorigin;
//        //endregion
//
//        navreset(true, true);
//
//        //odo.setPosition(new Pose2D(DistanceUnit.INCH, robopos.x, robopos.y, AngleUnit.DEGREES, robopos.z + 90));
//        if (dec.teamcolor.matches("Red")) {
//            dec.odo.setPosition(dec.Rstart);
//        } else {
//            dec.odo.setPosition(dec.Bstart);
//        }
//
//        dec.odo.update();
//
//        telemetry.addData("Odo x: ", dec.odo.getPosition().getX(DistanceUnit.INCH));
//        telemetry.addData("Odo y: ", dec.odo.getPosition().getY(DistanceUnit.INCH));
//        telemetry.addData("Odo z: ", dec.odo.getPosition().getHeading(AngleUnit.DEGREES));
//        telemetry.addData("Status: ", "Ready");
//        telemetry.update();
//
//        waitForStart();
//
//        telemetryAprilTag();
//        telemetry.update();
//
//        runtime.reset();
//
//        if (dec.teamcolor.matches("Red")) {
//            dec.odo.setPosition(dec.Rstart);
//            dec.goal = dec.Rgoal;
//        } else {
//            dec.odo.setPosition(dec.Bstart);
//            dec.goal = dec.Bgoal;
//        }
//
//        dec.launch.setVelocity(dec.launchVelCalc(200, true) / 60 * 28); //140
//        dec.hood.setPosition(dec.launchHoodCalc(140, true));
//
//        Log.d("Launch", "Target0: " + (dec.launchVelCalc(140, true) / 60 * 28));
//        Log.d("Launch", "Actual0: " + dec.launch.getVelocity());
//
//        Pos(dec.odo.getPosition());
//
//        if (!aprilTag.getDetections().isEmpty()) { //what do i do if else?
//            try {
//                dec.goalcode0 = TagHelper.getFirst(aprilTag);
//            } catch (IndexOutOfBoundsException e) {
//                Log.d("CamE", "NO CAMERA (0)");
//            }
//
//            if (aprilTag.getDetections().size() > 1) {
//                try {
//                    dec.goalcode1 = aprilTag.getDetections().get(1);
//                } catch (IndexOutOfBoundsException e) {
//                    Log.d("CamE", "NO CAMERA (1)");
//                }
//            }
//
//            if (dec.goalcode0 != null) {
//                try {
//                    if (dec.goalcode0.metadata.name.contains("Obelisk")) {
//                        dec.ob = dec.goalcode0;
//                    } else if (dec.goalcode1 != null) {
//                        try {
//                            if (dec.goalcode1.metadata.name.contains("Obelisk")) {
//                                dec.ob = dec.goalcode1;
//                            }
//                        } catch (NullPointerException e) {
//                            Log.d("CamE", "Lost OB0");
//                        }
//                    }
//                } catch (NullPointerException e) {
//                    Log.d("CamE", "Lost OB1");
//                }
//            }
//        }
//
//        if (dec.ob.id == 21) {
//            dec.order = "gpp";
//        } else if (dec.ob.id == 22) {
//            dec.order = "pgp";
//        } else {
//            dec.order = "ppg";
//        }
//
//        if (dec.teamcolor.matches("Red")) {
//            dec.odo.update();
//
//            dec.launch.setVelocity(dec.launchVelCalc((dec.goalD(dec.odo.getPosition(), dec.goal, true) + 11.5), true) / 60 * 28); //11
//            dec.hood.setPosition(dec.launchHoodCalc(dec.goalD(dec.odo.getPosition(), dec.goal, true), true));
//
//            Log.d("Launch", "Target1: " + (dec.launchVelCalc(dec.goalD(dec.odo.getPosition(), dec.goal, true), true) / 60 * 28));
//            Log.d("Launch", "Actual1: " + dec.launch.getVelocity());
//
//            /*drivetimeout = runtime.milliseconds() + 1500;
//            while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && (((Math.abs(dec.Rstart.getX(DistanceUnit.INCH) -
//                    dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.Rstart.getY(DistanceUnit.INCH) -
//                    dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.Rstart.getHeading(AngleUnit.DEGREES) -
//                    dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25)) || ((dec.taxon.getTotalRotation() > (dec.taxon.
//                    getTargetRotation() + dec.ttol)) || (dec.taxon.getTotalRotation() < (dec.taxon.getTargetRotation() - dec.ttol)))
//                    || (dec.launch.getVelocity() < ((dec.launchVelCalc(dec.goalD(dec.odo.getPosition(), dec.goal, true),
//                    true) / 60 * 28) - 200)))) {
//                Drive(dec.Rstart, 5, 200);
//                Turret();
//
//                dec.launch.setVelocity(dec.launchVelCalc(dec.goalD(dec.odo.getPosition(), dec.goal, true), true) / 60 * 28);
//                dec.hood.setPosition(dec.launchHoodCalc(dec.goalD(dec.odo.getPosition(), dec.goal, true), true));
//
//                Log.d("Launch", "Target: " + dec.launchVelCalc(dec.goalD(dec.odo.getPosition(), dec.goal, true), true));
//                Log.d("Launch", "Actual: " + dec.launch.getVelocity());
//            }
//
//            DriveStop();
//            //Reloc(odo.getPosition());
//            Pos(dec.Rstart);
//
//            dec.intake.setPower(0);
//
//            if (dec.order.matches("gpp")) {
//                pos1 = dec.susanlaunch3;
//                pos2 = dec.susanlaunch2;
//                pos3 = dec.susanlaunch1;
//            } else if (dec.order.matches("pgp")) {
//                pos1 = dec.susanlaunch2;
//                pos2 = dec.susanlaunch3;
//                pos3 = dec.susanlaunch1;
//            } else {
//                pos1 = dec.susanlaunch1;
//                pos2 = dec.susanlaunch2;
//                pos3 = dec.susanlaunch3;
//            }
//
//            dec.turret.setPower(0);
//
//            dec.susan.setPosition(pos1);
//
//            if (pos1 == dec.susanlaunch3) {
//                dec.transfer.setPower(1);
//            } else if (pos1 == dec.susanlaunch2){
//                sleep(400);
//
//                dec.transfer.setPower(1);
//            } else {
//                sleep(800);
//
//                dec.transfer.setPower(1);
//            }
//
//            sleep(750);
//
//            dec.susan.setPosition(pos2);
//
//            sleep(750);
//
//            dec.susan.setPosition(pos3);
//
//            sleep(750);*/
//
//            /*Intake(1);
//
//            dec.susan.setPosition(dec.susanpos1);
//
//            drivetimeout = runtime.milliseconds() + 1500;
//            while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(dec.Rpreintake1.getX(DistanceUnit.INCH) -
//                    dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(dec.Rpreintake1.getY(DistanceUnit.INCH)) -
//                    dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.Rpreintake1.getHeading(AngleUnit.DEGREES) -
//                    dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//                Drive(dec.Rpreintake1, 5, 200);
//
//                dec.launch.setVelocity(dec.launchVelCalc(0, false));
//                dec.hood.setPosition(dec.launchHoodCalc(0, false));
//
//                Log.d("Launch", "Target: " + dec.launchVelCalc(0, false));
//                Log.d("Launch", "Actual: " + dec.launch.getVelocity());
//            }
//
//            dec.transfer.setPower(0);
//
//            DriveStop();
//            //Reloc(odo.getPosition());
//            Pos(dec.Rpreintake1);
//
//            drivetimeout = runtime.milliseconds() + 2000;
//            while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(dec.Rintake1.getX(DistanceUnit.INCH) -
//                    dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(dec.Rintake1.getY(DistanceUnit.INCH)) -
//                    dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.Rintake1.getHeading(AngleUnit.DEGREES) -
//                    dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//                Drive(dec.Rintake1, 40, 200);
//
//                IntakeSort();
//            }
//
//            DriveStop();
//            //Reloc(odo.getPosition());
//            Pos(dec.Rintake1);
//
//            dec.susan.setPosition(dec.susanpos3);*/
//
//            if (dec.order.matches("ppg")) {
//                ed1 = -3.5; //-4
//                ed2 = -3.5; //-1
//                cte1 = 6;
//                cte2 = 4.5; //2
//            } else if (dec.order.matches("pgp")) {
//                ed1 = 0; //0.5
//                ed2 = 0.5;
//                cte1 = 6; //6.25
//                cte2 = 2;
//            } else {
//                ed1 = 0;
//                ed2 = 0.5;
//                cte1 = 6;
//                cte2 = 2;
//            }
//
//            Cycle(dec.Rstart, 3000, 1, dec.teamcolor, dec.firstorder, false, ed1, cte1); //0, 6
//
//            Cycle(dec.Rlaunch1, 2500, 2, dec.teamcolor, "ppg", true, ed2, cte2); //0.5, 2
//
//            //Cycle (dec.Rlaunch2, 1500, 3, dec.teamcolor, "pgg");
//
//            Launch(dec.Rlaunch2, dec.order, "pgp", 1500, false, -0.5, 8); //1, 12
//
//            Park(dec.Rpark2);
//        } else {
//            dec.odo.update();
//
//            dec.launch.setVelocity(dec.launchVelCalc((dec.goalD(dec.odo.getPosition(), dec.goal, true) + 11.5), true) / 60 * 28); //11
//            dec.hood.setPosition(dec.launchHoodCalc(dec.goalD(dec.odo.getPosition(), dec.goal, true), true));
//
//            Log.d("Launch", "Target1: " + (dec.launchVelCalc(dec.goalD(dec.odo.getPosition(), dec.goal, true), true) / 60 * 28));
//            Log.d("Launch", "Actual1: " + dec.launch.getVelocity());
//
//            if (dec.order.matches("ppg")) {
//                ed0 = 3;
//                ed1 = 0;
//                ed2 = -4.5;
//                cte1 = -4; //-6
//                cte2 = -23;
//            } else if (dec.order.matches("pgp")) {
//                ed0 = 5;
//                ed1 = -3;
//                ed2 = -6;
//                cte1 = -5;
//                cte2 = -27.5; //-29
//            } else {
//                ed0 = 5;
//                ed1 = 0;
//                ed2 = -6;
//                cte1 = -5;
//                cte2 = -29;
//            }
//
//            Cycle(dec.Bstart, 3000, 1, dec.teamcolor, dec.firstorder, true, ed0, cte1); //7.5, 7
//
//            Cycle(dec.Blaunch1, 2500, 2, dec.teamcolor, "ppg", true, ed1, -2); //0.5, 6
//
//            Launch(dec.Blaunch2, dec.order, "pgp", 1500, false, ed2, cte2); //-4, 32
//
//            Park(dec.Bpark2);
//        }
//    }
//
//    //region - Drive
//    public void navreset(boolean reset_opods, boolean recal_opods) {
//        if (reset_opods) {
//            dec.odo.resetPosAndIMU(); //resets the position to 0 and recalibrates the IMU
//        }
//
//        if (recal_opods) {
//            dec.odo.recalibrateIMU(); //recalibrates the IMU without resetting position
//        }
//    }
//
//    public void Drive(Pose2D target, double halo, double turnstart) {
//        dec.odo.update();
//
//        Log.d("DriveCheck", "Current = " + dec.odo.getPosition().getX(DistanceUnit.INCH) + ", " +
//                dec.odo.getPosition().getY(DistanceUnit.INCH) + ", " + dec.odo.getPosition().getHeading(AngleUnit.DEGREES));
//
//        dec.yto = target.getY(DistanceUnit.INCH);
//        dec.xto = target.getX(DistanceUnit.INCH);
//        dec.zto = target.getHeading(AngleUnit.DEGREES);
//
//        Log.d("DriveCheck", "Target = " + dec.xto + ", " + dec.yto + ", " + dec.zto);
//
//        dec.yE = dec.yto - dec.odo.getPosition().getY(DistanceUnit.INCH);
//        dec.xE = dec.xto - dec.odo.getPosition().getX(DistanceUnit.INCH);
//        dec.zE = dec.zto - dec.odo.getPosition().getHeading(AngleUnit.DEGREES);
//
//        double distance = Math.sqrt(Math.pow(dec.xE, 2) + Math.pow(dec.yE, 2));
//
//        if (dec.zE > 180) {
//            dec.zE = dec.zE - 360;
//        } else if (dec.zE < -180) {
//            dec.zE = dec.zE + 360;
//        }
//
//        Log.d("DriveCheck", "Error = " + dec.xE + ", " + dec.yE + ", " + dec.zE);
//
//        double theta = (Math.atan(dec.yE / dec.xE)) * (180 / Math.PI);
//
//        if (dec.xE < 0) {
//            if (dec.yE > 0) {
//                theta = theta + 180;
//            } else {
//                theta = theta - 180;
//            }
//        }
//
//        double H = dec.odo.getPosition().getHeading(AngleUnit.DEGREES);
//
//        Log.d("DriveCheck", "Theta = " + theta + ", Heading = " + H);
//
//        //region - faster?
//        /*if (Math.abs(yE) > halo) {
//            yP = yE / Math.abs(yE);
//        } else {
//            yP = yE / halo;
//        }
//
//        if (Math.abs(xE) > halo) {
//            xP = xE / Math.abs(xE);
//        } else {
//            xP = xE / halo;
//        }
//
//        if (Math.abs(zE) > 20) {
//            zP = zE / Math.abs(zE);
//        } else {
//            zP = zE / 20;
//        }*/
//        //endregion
//
//        if (Math.abs(dec.yE) > halo) {
//            dec.yP = Range.clip((Math.abs(dec.yE) / halo), -1, 1);
//        } else {
//            dec.yP = Range.clip((Math.abs(dec.yE) / (2 * halo)), -1, 1);
//        }
//
//        if (Math.abs(dec.xE) > halo) {
//            dec.xP = Range.clip((Math.abs(dec.xE) / halo), -1, 1);
//        } else {
//            dec.xP = Range.clip((Math.abs(dec.xE) / (2 * halo)), -1, 1);
//        }
//
//        if (distance < turnstart) {
//            dec.zP = Range.clip((Math.abs(dec.zE) / 90), -0.6, 0.6); //og limit 0.5
//        } else {
//            dec.zP = 0;
//        }
//
//        dec.YPOWER = dec.yP * Math.sin(Math.toRadians(theta - H));
//        dec.XPOWER = dec.xP * Math.cos(Math.toRadians(H - theta));
//
//        if (dec.zE > 0) {
//            dec.ZPOWER = dec.zP;
//        } else {
//            dec.ZPOWER = -dec.zP;
//        }
//
//        if (Math.abs(dec.YPOWER) < 0.05) {
//            dec.YPOWER = 0;
//        }
//
//        if (Math.abs(dec.XPOWER) < 0.05) {
//            dec.XPOWER = 0;
//        }
//
//        if (Math.abs(dec.ZPOWER) < 0.05) {
//            dec.ZPOWER = 0;
//        }
//
//        Log.d("DriveCheck", "Dir Pwrs = " + dec.XPOWER + ", " + dec.YPOWER + ", " + dec.ZPOWER);
//
//        dec.flpower = Range.clip(dec.YPOWER + dec.XPOWER - dec.ZPOWER, -1, 1);
//        dec.frpower = Range.clip(dec.YPOWER - dec.XPOWER + dec.ZPOWER, -1, 1);
//        dec.blpower = Range.clip(dec.YPOWER - dec.XPOWER - dec.ZPOWER, -1, 1);
//        dec.brpower = Range.clip(dec.YPOWER + dec.XPOWER + dec.ZPOWER, -1, 1);
//
//        dec.fl.setPower(dec.flpower);
//        dec.fr.setPower(dec.frpower);
//        dec.bl.setPower(dec.blpower);
//        dec.br.setPower(dec.brpower);
//
//        Log.d("DriveCheck", "Motor Powers = FL: " + dec.flpower + ", FR: " + dec.frpower +
//                ", BL: " + dec.blpower + ", BR: " + dec.brpower);
//    }
//
//    //region - Spline TODO
//    public void Spline(Pose2D start, Pose2D p1, Pose2D p2, Pose2D end) {
//        double p0x = start.getX(DistanceUnit.INCH);
//        double p0y = start.getY(DistanceUnit.INCH);
//        double p1x = p1.getX(DistanceUnit.INCH);
//        double p1y = p1.getY(DistanceUnit.INCH);
//        double p2x = p2.getX(DistanceUnit.INCH);
//        double p2y = p2.getY(DistanceUnit.INCH);
//        double p3x = end.getX(DistanceUnit.INCH);
//        double p3y = end.getY(DistanceUnit.INCH);
//
//        double t = 0; //placeholder
//        double t0 = 0;
//        double t1 = tnext(true, t0, start, p1);
//        double t2 = tnext(true, t1, p1, p2);
//        double t3 = tnext(true, t2, p2, end);
//
//        double A1x = (p0x * ((t1 - t) / (t1 - t0))) + (p1x * ((t - t0) / (t3 - t2)));
//        double A2x = (p1x * ((t2 - t) / (t2 - t1))) + (p2x * ((t - t1) / (t2 - t1)));
//        double A3x = (p2x * ((t3 - t) / (t3 - t2))) + (p3x * ((t - t2) / (t3 - t2)));
//
//        double B1x = (A1x * ((t2 - t) / (t2 - t0))) + (A2x * ((t - t0) / (t2 - t0)));
//        double B2x = (A2x * ((t3 - t) / (t3 - t1))) + (A3x * ((t - t1) / (t3 - t1)));
//
//        double Cx = (B1x * ((t2 - t) / (t2 - t1))) + (B2x * ((t - t1) / (t2 - t1)));
//
//        double A1y = (p0y * ((t1 - t) / (t1 - t0))) + (p1y * ((t - t0) / (t3 - t2)));
//        double A2y = (p1y * ((t2 - t) / (t2 - t1))) + (p2y * ((t - t1) / (t2 - t1)));
//        double A3y = (p2y * ((t3 - t) / (t3 - t2))) + (p3y * ((t - t2) / (t3 - t2)));
//
//        double B1y = (A1y * ((t2 - t) / (t2 - t0))) + (A2y * ((t - t0) / (t2 - t0)));
//        double B2y = (A2y * ((t3 - t) / (t3 - t1))) + (A3y * ((t - t1) / (t3 - t1)));
//
//        double Cy = (B1y * ((t2 - t) / (t2 - t1))) + (B2y * ((t - t1) / (t2 - t1)));
//    }
//
//    public double tnext(boolean xy, double tat, Pose2D at, Pose2D next) {
//        double alpha = 0.5; //uniform = 0, centripetal = 0.5, chordal = 1
//        double tdist;
//
//        if (xy) {
//            tdist = Math.sqrt(Math.pow((next.getX(DistanceUnit.INCH) - at.getX(DistanceUnit.INCH)), 2) +
//                    Math.pow((next.getY(DistanceUnit.INCH) - at.getY(DistanceUnit.INCH)), 2));
//        } else {
//            tdist = Math.sqrt(Math.pow((next.getHeading(AngleUnit.DEGREES) - at.getHeading(AngleUnit.DEGREES)), 2));
//        }
//
//        return Math.pow(tdist, alpha) + tat;
//    }
//
//    public void tinc(double tnext, double tat, double stepcount) {
//        double segment = tnext - tat;
//        double step = segment / stepcount;
//    }
//    //endregion
//
//    public void Pos(Pose2D poscheck) {
//        Log.d("PositionCheck", "Target (x, y, z): " + poscheck.getX(DistanceUnit.INCH) + ", " +
//                poscheck.getY(DistanceUnit.INCH) + ", " + poscheck.getHeading(AngleUnit.DEGREES));
//        Log.d("PositionCheck", "Current (x, y, z): " + dec.odo.getPosition().getX(
//                DistanceUnit.INCH) + ", " + dec.odo.getPosition().getY(DistanceUnit.INCH) + ", " +
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES));
//    }
//
//    public void DriveStop() {
//        dec.fl.setPower(0);
//        dec.fr.setPower(0);
//        dec.bl.setPower(0);
//        dec.br.setPower(0);
//    }
//
//    public void DriveHold(boolean forward) {
//        if (forward) {
//            dec.fl.setPower(0.2);
//            dec.fr.setPower(0.2);
//            dec.bl.setPower(0.2);
//            dec.br.setPower(0.2);
//        } else {
//            dec.fl.setPower(-0.2);
//            dec.fr.setPower(-0.2);
//            dec.bl.setPower(-0.2);
//            dec.br.setPower(-0.2);
//        }
//
//        sleep(10); //50
//
//        DriveStop();
//    }
//
//    public void Cycle(Pose2D launch, double LdriveT, double intakenum, String color, String inorder,
//                      boolean hshift, double ED, double cte) {
//        Pose2D preintake;
//        Pose2D intakepos;
//
//        if (color.matches("Red")) {
//            if (intakenum == 1) {
//                preintake = dec.Rpreintake1;
//                intakepos = dec.Rintake1;
//            } else if (intakenum == 2) {
//                preintake = dec.Rpreintake2;
//                intakepos = dec.Rintake2;
//            } else {
//                preintake = dec.Rpreintake3;
//                intakepos = dec.Rintake3;
//            }
//        } else {
//            if (intakenum == 1) {
//                preintake = dec.Bpreintake1;
//                intakepos = dec.Bintake1;
//            } else if (intakenum == 2) {
//                preintake = dec.Bpreintake2;
//                intakepos = dec.Bintake2;
//            } else {
//                preintake = dec.Bpreintake3;
//                intakepos = dec.Bintake3;
//            }
//        }
//
//        Launch(launch, dec.order, inorder, LdriveT, hshift, ED, cte);
//
//        Intake(1);
//
//        dec.susan.setPosition(dec.susanauto1); //pos1
//
//        if (intakenum < 2) {
//            drivetimeout = runtime.milliseconds() + 1500;
//        } else {
//            drivetimeout = runtime.milliseconds() + 2000;
//        }
//
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(preintake.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(preintake.getY(DistanceUnit.INCH)) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(preintake.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(preintake, 5, 200);
//
//            dec.launch.setVelocity(dec.launchVelCalc(0, false) / 60 * 28);
//            dec.hood.setPosition(dec.launchHoodCalc(0, false));
//
//            Log.d("Launch", "Target2: " + (dec.launchVelCalc(0, false) / 60 * 28));
//            Log.d("Launch", "Actual2: " + dec.launch.getVelocity());
//        }
//
//        dec.transfer.setPower(0);
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(preintake);
//
//        double halo = 50;
//        if (dec.teamcolor.matches("Blue")) {
//            halo = 57;
//        }
//        drivetimeout = runtime.milliseconds() + 2200; //2000
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(intakepos.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(intakepos.getY(DistanceUnit.INCH)) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(intakepos.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(intakepos, halo, 20); //(35 + (5 * intakenum)), 200
//
//            IntakeSort();
//        }
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(intakepos);
//
//        sleep(150);
//
//        dec.susan.setPosition(dec.susanauto3); //pos3?
//    }
//
//    public void Park(Pose2D end) {
//        drivetimeout = runtime.milliseconds() + 1500;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((Math.abs(end.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(end.getY(DistanceUnit.INCH) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(end.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(end, 5, 200);
//
//            if (dec.taxon.getTotalRotation() > 50) {
//                dec.turret.setPower(1);
//            }
//        }
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(end);
//
//        //add reset
//        if (dec.teamcolor.matches("Blue")) {
//            drivetimeout = runtime.milliseconds() + 500;
//        } else {
//            drivetimeout = runtime.milliseconds() + 1000;
//        }
//
//        Intake(0);
//        dec.transfer.setPower(0);
//        dec.susan.setPosition(dec.susanpos1); //auto1?
//
//        while ((dec.taxon.getTotalRotation() > 50) && opModeIsActive() && (runtime.milliseconds() < drivetimeout)) {
//            dec.turret.setPower(1); //-?
//        }
//
//        dec.turret.setPower(0);
//    }
//    //endregion
//
//    public void Launch(Pose2D launchpos, String qorder, String order321, double time, boolean hoodshift, double exd, double ext) {
//        dec.susan.setPosition(dec.susanauto3);
//
//        if ((launchpos != dec.Rstart) && (launchpos != dec.Bstart)) {
//            Intake(1);
//        }
//
//        dec.launch.setVelocity(dec.launchVelCalc((dec.goalD(dec.odo.getPosition(), dec.goal, true) + exd), true) / 60 * 28);
//        dec.hood.setPosition(dec.launchHoodCalc(dec.goalD(dec.odo.getPosition(), dec.goal, true), true));
//
//        if (hoodshift) {
//            if (dec.teamcolor.matches("Blue") /*&& order321.matches("ppg")*/) {
//                dec.hood.setPosition(dec.hood.getPosition() + 0.05);
//            } else {
//                dec.hood.setPosition(dec.hood.getPosition() + 0.02);
//            }
//        }
//
//        Log.d("Launch", "Target3: " + (dec.launchVelCalc(dec.goalD(dec.odo.getPosition(), dec.goal, true), true) / 60 * 28));
//        Log.d("Launch", "Actual3: " + dec.launch.getVelocity());
//
//        drivetimeout = runtime.milliseconds() + time;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && (((Math.abs(launchpos.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(launchpos.getY(DistanceUnit.INCH) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(launchpos.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25)) || ((dec.taxon.getTotalRotation() > (dec.taxon.
//                getTargetRotation() + dec.ttol)) || (dec.taxon.getTotalRotation() < (dec.taxon.getTargetRotation() - dec.ttol)))
//                || (dec.launch.getVelocity() < ((dec.launchVelCalc(dec.goalD(dec.odo.getPosition(), dec.goal, true),
//                true) / 60 * 28) - 50)))) { //100
//            Drive(launchpos, 7, 200); //5
//            Turret(ext);
//
//            dec.launch.setVelocity(dec.launchVelCalc((dec.goalD(dec.odo.getPosition(), dec.goal, true) + exd), true) / 60 * 28);
//            dec.hood.setPosition(dec.launchHoodCalc(dec.goalD(dec.odo.getPosition(), dec.goal, true), true));
//
//            Log.d("Launch", "Diff2: " + (dec.launch.getVelocity() - (dec.launchVelCalc(dec.goalD(dec.odo.getPosition(),
//                            dec.goal, true), true) / 60 * 28)));
//
//            Log.d("Launch", "Target6: " + (dec.launchVelCalc(dec.goalD(dec.odo.getPosition(), dec.goal, true), true) / 60 * 28));
//            Log.d("Launch", "Actual6: " + dec.launch.getVelocity());
//
//            /*if (runtime.milliseconds() > (drivetimeout - 500)) {
//                dec.susan.setPosition(dec.susanlaunch3);
//            }*/
//
//            telemetry.addData("RPM: ", (dec.launch.getVelocity() * 60 / 28));
//            telemetry.update();
//        }
//
//        //dec.susan.setPosition(dec.susanlaunch3);
//
//        Log.d("Launch", "Diff1: " + (dec.launch.getVelocity() - (dec.launchVelCalc(dec.goalD(dec.odo.getPosition(),
//                dec.goal, true), true) / 60 * 28)));
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(launchpos);
//
//        dec.intake.setPower(0);
//        dec.turret.setPower(0);
//
//        if (hoodshift) {
//            if (dec.teamcolor.matches("Blue") && order321.matches("pgp") && qorder.matches("pgp")) {
//                dec.hood.setPosition(dec.hood.getPosition() + 0.05);
//            } else {
//                dec.hood.setPosition(dec.hood.getPosition() + 0.02);
//            }
//        }
//
//        if ((qorder.matches("gpp") && order321.matches("gpp")) || (qorder.matches("pgp") &&
//                order321.matches("pgp")) || (qorder.matches("ppg") && order321.matches("ppg"))) {
//            pos1 = dec.susanlaunch3;
//            pos2 = dec.susanlaunch2;
//            pos3 = dec.susanlaunch1;
//        } else if ((qorder.matches("gpp") && order321.matches("pgp")) || (qorder.matches("pgp") &&
//                order321.matches("gpp"))) {
//            pos1 = dec.susanlaunch2;
//            pos2 = dec.susanlaunch3;
//            pos3 = dec.susanlaunch1;
//        } else if ((qorder.matches("gpp") && order321.matches("ppg")) || (qorder.matches("ppg") &&
//                order321.matches("gpp"))) {
//            pos1 = dec.susanlaunch1;
//            pos2 = dec.susanlaunch2;
//            pos3 = dec.susanlaunch3;
//        } else if ((qorder.matches("pgp") && order321.matches("ppg")) || (qorder.matches("ppg") &&
//                order321.matches("pgp"))) {
//            pos1 = dec.susanlaunch3;
//            pos2 = dec.susanlaunch1;
//            pos3 = dec.susanlaunch2;
//        }
//
//        if (pos1 == dec.susanlaunch3) {
//            dec.transfer.setPower(1);
//
//            sleep(100);
//
//            if (dec.teamcolor.matches("Blue") && qorder.matches("pgp") && order321.matches("pgp")) {
//                sleep(100);
//            }
//        } else {
//            dec.turret.setPower(0);
//        }
//
//        dec.susan.setPosition(pos1);
//
//        if (dec.teamcolor.matches("Blue")) {
//            dec.transfer.setPower(-0.5);
//        }
//
//        if (pos1 == dec.susanlaunch3) {
//            dec.transfer.setPower(1);
//        } else if (pos1 == dec.susanlaunch2){
//            sleep(200); //400
//
//            dec.transfer.setPower(1);
//        } else {
//            sleep(400); //800
//
//            dec.transfer.setPower(1);
//        }
//
//        if (exd > 0) {
//            sleep(1200); //1000
//        } else {
//            sleep(800); //1000
//        }
//
//        if ((pos1 == dec.susanlaunch3) && (pos2 == dec.susanlaunch1)) {
//            dec.transfer.setPower(-0.5);
//
//            dec.susan.setPosition(pos2);
//
//            sleep(600); //1000
//
//            dec.transfer.setPower(1);
//        } else {
//            dec.susan.setPosition(pos2);
//        }
//
//        sleep(1200); //1000
//
//        dec.susan.setPosition(pos3);
//
//        sleep(1000);
//    }
//
//    public void Turret(double extra) {
//        dec.taxon.update();
//
//        if (!aprilTag.getDetections().isEmpty()) {
//            try {
//                dec.goalcode0 = TagHelper.getFirst(aprilTag);
//            } catch (IndexOutOfBoundsException e) {
//                Log.d("CamE", "NO CAMERA (0)");
//            }
//
//            if (aprilTag.getDetections().size() > 1) {
//                try {
//                    dec.goalcode1 = aprilTag.getDetections().get(1);
//                } catch (IndexOutOfBoundsException e) {
//                    Log.d("CamE", "NO CAMERA (1)");
//                }
//            }
//        }
//
//        if (dec.goalcode0 != null) {
//            try {
//                if (dec.goalcode0.metadata.name.contains("Obelisk")) {
//                    if (dec.goalcode1 != null) {
//                        /*if ((dec.teamcolor.matches("Red") && (dec.goalcode0.id == 20)) || (dec.teamcolor.
//                                matches("Blue") && (dec.goalcode0.id == 24))) {*/
//                            dec.goalcode = dec.goalcode1;
//                        //}
//                    }
//                } else {
//                    /*if ((dec.teamcolor.matches("Red") && (dec.goalcode0.id == 20)) || (dec.teamcolor.
//                            matches("Blue") && (dec.goalcode0.id == 24))) {*/
//                        dec.goalcode = dec.goalcode0;
//                    //}
//                }
//            } catch (NullPointerException e) {
//                Log.d("CamE", "Lost GC0");
//            }
//        }
//
//        if (dec.goalcode != null) {
//            Log.d("Turret", "ID: " + dec.goalcode.id);
//        }
//
//        if (aprilTag.getDetections().isEmpty() || (Math.abs(dec.setTurretAngle(dec.odo.getPosition(),
//                dec.goal, dec.teamcolor) - dec.taxon.getTotalRotation()) > 30)) {//100
//            dec.taxon.setTargetRotation(dec.setTurretAngle(dec.odo.getPosition(), dec.goal,
//                    dec.teamcolor) + dec.camtaroffset + extra);
//
//            Log.d("Turret", "Manual");
//            Log.d("Turret", "Cam Offset: " + dec.camtaroffset);
//        } else if (!(dec.goalcode == null)) {
//            Log.d("Turret", "QR Adjust");
//
//            if (((dec.FA2G(dec.odo.getPosition(), dec.goal) < 55) && dec.teamcolor.matches("Red")) ||
//                    ((dec.FA2G(dec.odo.getPosition(), dec.goal) > 55) && dec.teamcolor.matches("Blue"))) {
//                dec.center = (2.14806 * dec.FA2G(dec.odo.getPosition(), dec.goal)) + (119.0786 / (dec.FA2G(
//                        dec.odo.getPosition(), dec.goal) - 2.97963)) + 190.35734;
//
//                Log.d("Turret", "Center Calc 1: " + dec.FA2G(dec.odo.getPosition(), dec.goal));
//            } else {
//                dec.center = ((-0.0000018711211) * Math.pow(dec.FA2G(dec.odo.getPosition(), dec.goal), 6)) +
//                        (0.00074689381 * Math.pow(dec.FA2G(dec.odo.getPosition(), dec.goal), 5)) - (0.12314495 *
//                        Math.pow(dec.FA2G(dec.odo.getPosition(), dec.goal), 4)) + (10.730059 * Math.pow(dec.
//                        FA2G(dec.odo.getPosition(), dec.goal), 3)) - (520.81658 * Math.pow(dec.FA2G(dec.odo.
//                        getPosition(), dec.goal), 2)) + (13343.2365 * dec.FA2G(dec.odo.getPosition(),
//                        dec.goal)) - 140594.244;
//
//                Log.d("Turret", "Center Calc 2: " + dec.FA2G(dec.odo.getPosition(), dec.goal));
//            }
//
//            Log.d("Turret", "Goal Code Center: " + dec.goalcode.center.x);
//            Log.d("Turret", "Desired Center: " + dec.center);
//
//            double off;
//            if (dec.teamcolor.matches("Red")) {
//                off = 0;
//            } else {
//                off = 10;
//            }
//
//            if ((dec.goalcode.center.x) < (dec.center - dec.ctol)) {
//                dec.taxon.setTargetRotation(dec.taxon.getTotalRotation() + ((dec.goalcode.center.x -
//                        dec.center + 10) / 2) + extra); //1.5
//            } else if ((dec.goalcode.center.x) > (dec.center + dec.ctol)) {
//                dec.taxon.setTargetRotation(dec.taxon.getTotalRotation() + ((dec.goalcode.center.x -
//                        dec.center + off) / 2) + extra); //1.5
//            } else {
//                dec.taxon.setTargetRotation(dec.taxon.getTotalRotation());
//            }
//
//            dec.camtaroffset = (dec.taxon.getTargetRotation() - (dec.setTurretAngle(dec.odo.getPosition(),
//                    dec.goal, dec.teamcolor))) / 1.5;
//        } else {
//            dec.taxon.setTargetRotation(dec.taxon.getTotalRotation());
//        }
//
//        if (dec.taxon.getTargetRotation() > 995) {
//            dec.taxon.setTargetRotation(995);
//        }
//
//        Log.d("Turret", "Turret Target: " + dec.taxon.getTargetRotation());
//        Log.d("Turret", "Turret At: " + dec.taxon.getTotalRotation());
//
//        if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) > 200) {
//            dec.turretpwrcoeff = 10;
//        } else {
//            dec.turretpwrcoeff = Range.clip((Math.abs(dec.taxon.getTotalRotation() -
//                    dec.taxon.getTargetRotation()) / 25), 1, 10); //20
//        }
//
//        /*if (dec.goalD(dec.odo.getPosition(), dec.goal, true) > 108) {
//            dec.turretpwrcoeff = dec.turretpwrcoeff / 2;
//        }*/
//
//        dec.turretpower = Range.clip((dec.tminp * dec.turretpwrcoeff), 0.09, 1); //0.1
//
//        if (dec.taxon.getTotalRotation() > (dec.taxon.getTargetRotation() + dec.ttol)) {
//            dec.turret.setPower(dec.turretpower);
//        } else if (dec.taxon.getTotalRotation() < (dec.taxon.getTargetRotation() - dec.ttol)) {
//            dec.turret.setPower(-dec.turretpower);
//        } else {
//            dec.turret.setPower(0);
//        }
//
//        Log.d("Turret", "Power: " + dec.turret.getPower());
//    }
//
//    public void Intake(double speed) {
//        dec.intake.setPower(speed);
//        dec.intakedos.setPower(0.5 * speed / (Math.abs(speed) + 0.000001));
//    }
//
//    public void IntakeSort() {
//        dec.f2c = dec.f2.getNormalizedColors();
//        dec.r2c = dec.r2.getNormalizedColors();
//        dec.l2c = dec.l2.getNormalizedColors();
//
//        if ((dec.f2c.blue >= 0.004) && (dec.f2c.green > 0.004)) {
//            if ((dec.f2c.green / dec.f2c.red) > 1.7) {
//                dec.revf = "Green";
//                Log.d("SC", "RFG");
//            } else {
//                dec.revf = "Purple";
//                Log.d("SC", "RFP");
//            }
//        } else {
//            dec.revf = "None";
//            Log.d("SC", "RFN");
//        }
//
//        if ((dec.r2c.blue / dec.r2c.red) > dec.bratio) {
//            if ((dec.r2c.green / dec.r2c.red) > dec.gratio) {
//                dec.revr = "Green";
//                Log.d("SC", "RRG");
//            } else {
//                dec.revr = "Purple";
//                Log.d("SC", "RRP");
//            }
//        } else {
//            dec.revr = "None";
//            Log.d("SC", "RRN");
//        }
//
//        if ((dec.l2c.blue / dec.l2c.red) > dec.bratio) {
//            if ((dec.l2c.green / dec.l2c.red) > dec.gratio) {
//                dec.revl = "Green";
//                Log.d("SC", "RLG");
//            } else {
//                dec.revl = "Purple";
//                Log.d("SC", "RLP");
//            }
//        } else {
//            dec.revl = "None";
//            Log.d("SC", "RLN");
//        }
//
//        if (dec.f0.getState() || dec.revf.matches("Purple")) {
//            dec.FC = "Purple";
//        } else if (dec.f1.getState() || dec.revf.matches("Green")) {
//            dec.FC = "Green";
//        } else {
//            dec.FC = "None";
//        }
//
//        if (dec.r0.getState() || dec.revr.matches("Purple")) {
//            dec.RC = "Purple";
//        } else if (dec.r1.getState() || dec.revr.matches("Green")) {
//            dec.RC = "Green";
//        } else {
//            dec.RC = "None";
//        }
//
//        if (dec.l0.getState() || dec.revl.matches("Purple")) {
//            dec.LC = "Purple";
//        } else if (dec.l1.getState() || dec.revl.matches("Green")) {
//            dec.LC = "Green";
//        } else {
//            dec.LC = "None";
//        }
//
//        Log.d("Sorting", "F, R, L: " + dec.FC + ", " + dec.RC + ", " + dec.LC);
//
//        boolean second = false;
//
//        if (switchtime < runtime.milliseconds()) {
//            if ((!dec.FC.matches("None")) || second) {
//                Log.d("Sorting", "1");
//                if ((dec.susan.getPosition() < 0.2 /*== dec.susanpos1) || (dec.susan.getPosition() == dec.susanlaunch1*/)) {
//                    Log.d("Sorting", "2");
//                    if (dec.LC.matches("None")) {
//                        Log.d("Sorting", "Sorting Times1: " + switchtime + ", " + runtime.milliseconds());
//                        if ((switchtime + 150) < runtime.milliseconds()) { //100
//                            dec.susan.setPosition(dec.susanpos2);
//                            switchtime = runtime.milliseconds() + 500;
//                            Log.d("Sorting", "3");
//                            second = true;
//                        }
//                    } else if (dec.RC.matches("None")) {
//                        Log.d("Sorting", "Sorting Times2: " + switchtime + ", " + runtime.milliseconds());
//                        if ((switchtime + 150) < runtime.milliseconds()) {
//                            dec.susan.setPosition(dec.susanauto3); //pos3?
//                            switchtime = runtime.milliseconds() + 500;
//                            Log.d("Sorting", "4");
//                            second = false;
//                        }
//                    }
//                } else if ((dec.susan.getPosition() < 0.6 /*== dec.susanpos2) || (dec.susan.getPosition() == dec.susanlaunch2*/)) {
//                    Log.d("Sorting", "5");
//                    if (dec.LC.matches("None")) {
//                        Log.d("Sorting", "Sorting Times3: " + switchtime + ", " + runtime.milliseconds());
//                        if ((switchtime + 150) < runtime.milliseconds()) {
//                            dec.susan.setPosition(dec.susanauto3); //pos3?
//                            switchtime = runtime.milliseconds() + 500;
//                            Log.d("Sorting", "6");
//                            second = false;
//                        }
//                    } else if (dec.RC.matches("None")) {
//                        Log.d("Sorting", "Sorting Times4: " + switchtime + ", " + runtime.milliseconds());
//                        if ((switchtime + 150) < runtime.milliseconds()) {
//                            dec.susan.setPosition(dec.susanauto1); //pos1
//                            switchtime = runtime.milliseconds() + 500;
//                            Log.d("Sorting", "7");
//                            second = false;
//                        }
//                    }
//                } else /*if ((dec.susan.getPosition() == dec.susanpos3) || (dec.susan.getPosition() == dec.susanlaunch3))*/ {
//                    if (dec.RC.matches("None")) {
//                        Log.d("Sorting", "Sorting Times5: " + switchtime + ", " + runtime.milliseconds());
//                        if ((switchtime + 150) < runtime.milliseconds()) {
//                            dec.susan.setPosition(dec.susanpos2);
//                            switchtime = runtime.milliseconds() + 500;
//                            Log.d("Sorting", "8");
//                            second = true;
//                        }
//                    } else if (dec.LC.matches("None")) {
//                        Log.d("Sorting", "Sorting Times6: " + switchtime + ", " + runtime.milliseconds());
//                        if ((switchtime + 150) < runtime.milliseconds()) {
//                            dec.susan.setPosition(dec.susanauto1); //pos1
//                            switchtime = runtime.milliseconds();
//                            Log.d("Sorting", "9");
//                            second = false;
//                        }
//                    }
//                }/* else {
//                    switchtime = runtime.milliseconds();
//                    Log.d("Sorting", "11");
//                }*/
//            } else if (dec.RC.matches("None") && dec.LC.matches("None")) {
//                Log.d("Sorting", "Sorting Times7: " + switchtime + ", " + runtime.milliseconds());
//                if ((switchtime + 150) < runtime.milliseconds()) {
//                    dec.susan.setPosition(dec.susanauto1); //pos1
//                    switchtime = runtime.milliseconds();
//                    Log.d("Sorting", "10");
//                    second = false;
//                }
//            } else if (!dec.RC.matches("None")) {
//                Log.d("Sorting", "Sorting Times8: " + switchtime + ", " + runtime.milliseconds());
//                if ((switchtime + 150) < runtime.milliseconds()) {
//                    dec.susan.setPosition(dec.susanauto3); //pos3?
//                    switchtime = runtime.milliseconds();
//                    Log.d("Sorting", "11");
//                    second = false;
//                }
//            }
//        }
//    }
//
//    private void initAprilTag() {
//
//        // Create the AprilTag processor.
//        aprilTag = new AprilTagProcessor.Builder()
//
//                // The following default settings are available to un-comment and edit as needed.
//                //.setDrawAxes(false)
//                //.setDrawCubeProjection(false)
//                //.setDrawTagOutline(true)
//                //.setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
//                //.setTagLibrary(AprilTagGameDatabase.getCenterStageTagLibrary())
//                .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)
//                .setCameraPose(cameraPosition, cameraOrientation)
//
//                // == CAMERA CALIBRATION ==
//                // If you do not manually specify calibration parameters, the SDK will attempt
//                // to load a predefined calibration for your camera.
//                //.setLensIntrinsics(578.272, 578.272, 402.145, 221.506)
//                .setLensIntrinsics(641.5, 641.5, 320, 240)
//                // ... these parameters are fx, fy, cx, cy.
//
//                .build();
//
//        // Adjust Image Decimation to trade-off detection-range for detection-rate.
//        // eg: Some typical detection data using a Logitech C920 WebCam
//        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
//        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
//        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second (default)
//        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second (default)
//        // Note: Decimation can be changed on-the-fly to adapt during a match.
//        //aprilTag.setDecimation(3);
//
//        // Create the vision portal by using a builder.
//        VisionPortal.Builder builder = new VisionPortal.Builder();
//
//        // Set the camera (webcam vs. built-in RC phone camera).
//        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
//
//        // Choose a camera resolution. Not all cameras support all resolutions.
//        builder.setCameraResolution(new Size(640, 480));
//
//        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
//        //builder.enableLiveView(true);
//
//        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
//        //builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);
//
//        // Choose whether or not LiveView stops if no processors are enabled.
//        // If set "true", monitor shows solid orange screen if no processors enabled.
//        // If set "false", monitor shows camera view without annotations.
//        //builder.setAutoStopLiveView(false);
//
//        // Set and enable the processor.
//        builder.addProcessor(aprilTag);
//
//        // Build the Vision Portal, using the above settings.
//        visionPortal = builder.build();
//
//        // Disable or re-enable the aprilTag processor at any time.
//        //visionPortal.setProcessorEnabled(aprilTag, true);
//    }
//
//    private void telemetryAprilTag() {
//        try {
//            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
//
//            if (dec.goalcode != null) {
//                telemetry.addData("Goal Code Center X: ", dec.goalcode.center.x);
//            }
//
//            telemetry.addData("# AprilTags Detected", currentDetections.size());
//
//            // Step through the list of detections and display info for each one.
//            for (AprilTagDetection detection : currentDetections) {
//                if (detection.metadata != null) {
//                    telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
//                    // Only use tags that don't have Obelisk in them
//                    if (!detection.metadata.name.contains("Obelisk")) {
//                        dec.campos.x = detection.robotPose.getPosition().x;
//                        dec.campos.y = detection.robotPose.getPosition().y;
//                        dec.campos.z = detection.robotPose.getOrientation().getYaw(AngleUnit.DEGREES);
//
//                        telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f ", dec.campos.x, dec.campos.y, dec.campos.z));
//
//                    /*telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)",
//                            detection.robotPose.getPosition().x,
//                            detection.robotPose.getPosition().y,
//                            detection.robotPose.getPosition().z));
//                    telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)",
//                            detection.robotPose.getOrientation().getPitch(AngleUnit.DEGREES),
//                            detection.robotPose.getOrientation().getRoll(AngleUnit.DEGREES),
//                            detection.robotPose.getOrientation().getYaw(AngleUnit.DEGREES)));*/
//                    }
//                } else {
//                    telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
//                    telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
//                }
//            }   // end for() loop
//
//            // Add "key" information to telemetry
//        /*telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
//        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");*/
//        } catch (NullPointerException e) {
//            Log.d("CamE", "telemetry Error");
//        }
//    }
//}
