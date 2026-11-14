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
//@Autonomous(name = "AutoM1", group = "Linear Opmode")
//public class DecodeAutoM1 extends LinearOpMode {
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
//    double maxvel = 78; //MiracleChild in/sec
//    double drivetime = 0;
//
//    double goaldistanceT = 0;
//    double goaldistanceX = 0;
//    double goaldistanceY = 0;
//
//    String order = "gpp";
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
//        while (!gamepad1.a) {
//            if (gamepad1.x) {
//                dec.teamcolor = "Blue";
//            } else if (gamepad1.b) {
//                dec.teamcolor = "Red";
//            }
//
//            telemetry.addData("Team Color: ", dec.teamcolor);
//            telemetry.update();
//
//            dec.taxon.update();
//            dec.taxon.setTargetRotation(dec.turretmiddle);
//
//            dec.turretpwrcoeff = Range.clip(Math.abs(dec.taxon.getTotalRotation() -
//                    dec.taxon.getTargetRotation()) / 150, 1, 7);
//            dec.turretpower = Range.clip((dec.tminp * dec.turretpwrcoeff), 0.16, 1);
//
//            if (dec.taxon.getTotalRotation() > (dec.taxon.getTargetRotation() + dec.ttol)) {
//                dec.turret.setPower(-dec.turretpower);
//            } else if (dec.taxon.getTotalRotation() < (dec.taxon.getTargetRotation() - dec.ttol)) {
//                dec.turret.setPower(dec.turretpower);
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
//        dec.susan.setPosition(dec.susanpos1);
//        dec.hood.setPosition(dec.hooddown);
//        dec.camrot.setPosition(dec.camlong);
//        dec.turret.setPower(0);
//
//        dec.configureOdo(true);
//        dec.odo.setPosition(dec.odoorigin);
//
//        initAprilTag();
//
//        dec.LAUNCH = dec.odoorigin;
//        //endregion
//
//        //navreset(true, true);
//
//        //odo.setPosition(new Pose2D(DistanceUnit.INCH, robopos.x, robopos.y, AngleUnit.DEGREES, robopos.z + 90));
//        if (dec.teamcolor.matches("Red")) {
//            dec.odo.setPosition(dec.Rstart);
//        } else {
//            dec.odo.setPosition(dec.Bstart);
//        }
//
//        /*dec.odo.update();
//
//        telemetry.addData("Odo x: ", dec.odo.getPosition().getX(DistanceUnit.INCH));
//        telemetry.addData("Odo y: ", dec.odo.getPosition().getY(DistanceUnit.INCH));
//        telemetry.addData("Odo z: ", dec.odo.getPosition().getHeading(AngleUnit.DEGREES));*/
//        telemetry.addData("Status: ", "Ready");
//        telemetry.update();
//
//        waitForStart();
//        runtime.reset();
//
//        if (dec.teamcolor.matches("Red")) {
//            dec.odo.setPosition(dec.Rstart);
//        } else {
//            dec.odo.setPosition(dec.Bstart);
//        }
//
//        sleep(500);
//
//        LaunchS();
//
//        dec.intakedos.setPower(1);
//        dec.susan.setPosition(0);
//
//        if (dec.teamcolor.matches("Red")) {
//            drivetimeout = runtime.milliseconds() + 1000;
//            while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((Math.abs(dec.Rlaunch.getX(DistanceUnit.INCH) -
//                    dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.Rlaunch.getY(DistanceUnit.INCH)) -
//                    dec.odo.getPosition().getY(DistanceUnit.INCH) > 0.25) || (Math.abs(dec.Rlaunch.getHeading(AngleUnit.DEGREES) -
//                    dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//                Drive(dec.Rlaunch, 10, 200);
//            }
//
//            DriveStop();
//            //Reloc(odo.getPosition());
//            Pos(dec.Rlaunch);
//
//            sleep(3000);
//
//            dec.transfer.setPower(1);
//
//            sleep(2000);
//
//            dec.susan.setPosition(dec.susanpos1);
//
//            sleep(3000);
//
//            dec.susan.setPosition(dec.susanpos2);
//
//            sleep(3000);
//
//            dec.susan.setPosition(dec.susanpos3);
//
//            sleep(3000);
//
//            drivetimeout = runtime.milliseconds() + 2000;
//            while (opModeIsActive() && (runtime.milliseconds() < drivetimeout) && (((Math.abs(dec.Rlaunch.getX(DistanceUnit.INCH) -
//                    dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.Rlaunch.getY(DistanceUnit.INCH)) -
//                    dec.odo.getPosition().getY(DistanceUnit.INCH) > 0.25) || (Math.abs(dec.Rlaunch.getHeading(AngleUnit.DEGREES) -
//                    dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25)) || (Math.abs(dec.taxon.getTargetRotation() -
//                    dec.taxon.getTotalRotation()) > dec.ttol))) {
//                Drive(dec.Rpark1, 10, 200);
//
//                dec.transfer.setPower(0);
//
//                dec.taxon.update();
//                dec.taxon.setTargetRotation(0);
//
//                dec.turretpwrcoeff = Range.clip(Math.abs(dec.taxon.getTotalRotation() -
//                        dec.taxon.getTargetRotation()) / 150, 1, 7);
//                dec.turretpower = Range.clip((dec.tminp * dec.turretpwrcoeff), 0.16, 1);
//
//                if (dec.taxon.getTotalRotation() > (dec.taxon.getTargetRotation() + dec.ttol)) {
//                    dec.turret.setPower(-dec.turretpower);
//                } else if (dec.taxon.getTotalRotation() < (dec.taxon.getTargetRotation() - dec.ttol)) {
//                    dec.turret.setPower(dec.turretpower);
//                } else {
//                    dec.turret.setPower(0);
//                }
//            }
//
//            DriveStop();
//            //Reloc(odo.getPosition());
//            Pos(dec.Rpark1);
//
//            dec.fl.setPower(-0.5);
//            dec.fr.setPower(0.5);
//            dec.bl.setPower(0.5);
//            dec.br.setPower(-0.5);
//
//            sleep(1000);
//
//            dec.fl.setPower(0);
//            dec.fr.setPower(0);
//            dec.bl.setPower(0);
//            dec.br.setPower(0);
//
//        } else {
//            drivetimeout = runtime.milliseconds() + 1000;
//            while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((Math.abs(dec.Rlaunch.getX(DistanceUnit.INCH) -
//                    dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.Rlaunch.getY(DistanceUnit.INCH)) -
//                    dec.odo.getPosition().getY(DistanceUnit.INCH) > 0.25) || (Math.abs(dec.Rlaunch.getHeading(AngleUnit.DEGREES) -
//                    dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//                Drive(dec.Blaunch, 10, 200);
//            }
//
//            DriveStop();
//            //Reloc(odo.getPosition());
//            Pos(dec.Blaunch);
//
//            sleep(3000);
//
//            dec.transfer.setPower(1);
//
//            sleep(2000);
//
//            dec.susan.setPosition(dec.susanpos1);
//
//            sleep(3000);
//
//            dec.susan.setPosition(dec.susanpos2);
//
//            sleep(2000);
//
//            dec.susan.setPosition(dec.susanpos3);
//
//            sleep(2000);
//
//            drivetimeout = runtime.milliseconds() + 2500;
//            while (opModeIsActive() && (runtime.milliseconds() < drivetimeout) && (((Math.abs(dec.Rlaunch.getX(DistanceUnit.INCH) -
//                    dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.Rlaunch.getY(DistanceUnit.INCH)) -
//                    dec.odo.getPosition().getY(DistanceUnit.INCH) > 0.25) || (Math.abs(dec.Rlaunch.getHeading(AngleUnit.DEGREES) -
//                    dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25)) || (Math.abs(dec.taxon.getTargetRotation() -
//                    dec.taxon.getTotalRotation()) > dec.ttol))) {
//                Drive(dec.Bpark1, 10, 200);
//
//                dec.transfer.setPower(0);
//
//                dec.taxon.update();
//                dec.taxon.setTargetRotation(0);
//
//                dec.turretpwrcoeff = Range.clip(Math.abs(dec.taxon.getTotalRotation() -
//                        dec.taxon.getTargetRotation()) / 150, 1, 7);
//                dec.turretpower = Range.clip((dec.tminp * dec.turretpwrcoeff), 0.16, 1);
//
//                if (dec.taxon.getTotalRotation() > (dec.taxon.getTargetRotation() + dec.ttol)) {
//                    dec.turret.setPower(-dec.turretpower);
//                } else if (dec.taxon.getTotalRotation() < (dec.taxon.getTargetRotation() - dec.ttol)) {
//                    dec.turret.setPower(dec.turretpower);
//                } else {
//                    dec.turret.setPower(0);
//                }
//            }
//
//            DriveStop();
//            //Reloc(odo.getPosition());
//            Pos(dec.Bpark1);
//
//            dec.fl.setPower(0.5);
//            dec.fr.setPower(-0.5);
//            dec.bl.setPower(-0.5);
//            dec.br.setPower(0.5);
//
//            sleep(1500);
//
//            dec.fl.setPower(0);
//            dec.fr.setPower(0);
//            dec.bl.setPower(0);
//            dec.br.setPower(0);
//        }
//
//        LaunchReset();
//
//        //launcher on & turret and hood to position
//        //Turret();
//        /*dec.transfer.setPower(0);
//        dec.susan.setPosition(dec.susanpos1);
//
//        LaunchP(dec.qrcode, "gpp", 2500);
//
//        dec.intake.setPower(1);
//
//        drivetimeout = runtime.milliseconds() + 2500;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(dec.intake1.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(dec.intake1.getY(DistanceUnit.INCH)) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.intake1.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(dec.intake1, 5, 200);
//            Turret();
//            //add susan spinning as intake happens
//        }
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(dec.intake1);
//
//        LaunchP(dec.qrcode, "ppg", 2500);
//
//        drivetimeout = runtime.milliseconds() + 2500;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(dec.pintake2.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(dec.pintake2.getY(DistanceUnit.INCH)) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.pintake2.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(dec.pintake2, 5, 200);
//            Turret();
//        }
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(dec.pintake2);
//
//        dec.intake.setPower(1);
//
//        drivetimeout = runtime.milliseconds() + 2500;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(dec.intake2.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(dec.intake2.getY(DistanceUnit.INCH)) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.intake2.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(dec.intake2, 5, 200);
//            Turret();
//            //add susan spinning as intake happens
//        }
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(dec.intake2);
//
//        LaunchP(dec.qrcode, "pgp", 2500);
//
//        drivetimeout = runtime.milliseconds() + 2500;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(dec.pintake3.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(dec.pintake3.getY(DistanceUnit.INCH)) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.pintake3.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(dec.pintake3, 5, 200);
//            Turret();
//        }
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(dec.pintake3);
//
//        dec.intake.setPower(1);
//
//        drivetimeout = runtime.milliseconds() + 2500;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(dec.intake3.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(dec.intake3.getY(DistanceUnit.INCH)) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.intake3.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(dec.intake3, 5, 200);
//            Turret();
//            //add susan spinning as intake happens
//        }
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(dec.intake3);
//
//        LaunchP(dec.qrcode, "pgp", 2500);
//
//        drivetimeout = runtime.milliseconds() + 2500;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(dec.intake3.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(dec.intake3.getY(DistanceUnit.INCH)) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.intake3.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(dec.intake3, 5, 200);
//            Turret();
//        }
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(dec.intake3);*/
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
//    /*
//        public void Reloc(Pose2D at) {
//            if (at.getY(DistanceUnit.INCH) > 0) {
//                if (at.getX(DistanceUnit.INCH) > 24) {
//                    targetangle = T13.getHeading(AngleUnit.DEGREES);
//                } else if (at.getX(DistanceUnit.INCH) < -24) {
//                    targetangle = T11.getHeading(AngleUnit.DEGREES);
//                } else {
//                    targetangle = T12.getHeading(AngleUnit.DEGREES);
//                }
//            } else {
//                if (at.getX(DistanceUnit.INCH) > 24) {
//                    targetangle = T14.getHeading(AngleUnit.DEGREES);
//                } else if (at.getX(DistanceUnit.INCH) < -24) {
//                    targetangle = T16.getHeading(AngleUnit.DEGREES);
//                } else {
//                    targetangle = T15.getHeading(AngleUnit.DEGREES);
//                }
//            }
//
//            autoangle = 0.51 - (targetangle / 455.696);
//            camrot.setPosition(autoangle);
//            rottime = runtime.milliseconds() + 100;
//
//            while ((aprilTag.getDetections().isEmpty()) && (runtime.milliseconds() < rottime)) {
//                idle();
//            }
//
//            telemetryAprilTag();
//            telemetry.update();
//
//            //lower servo position cw, higher is ccw
//            //180/0.395 = 455.696 --> 0.395 = servo travel for 180 degrees, so 455.696 converts servo position to angle
//            camRangle = (0.51 - camrot.getPosition()) * 455.696;
//
//            robopos.z = campos.z + camRangle;
//            //9.86 = hypotenuse of offsets, angle between robot and camera = 63 degrees
//            robopos.x = campos.x - (9.86 * Math.cos(Math.toRadians(robopos.z + 63))); //5.84 = x offset
//            robopos.y = campos.y - (9.86 * Math.sin(Math.toRadians(robopos.z + 63))); //7.94 = y offset
//
//            camFangle = robopos.z - camRangle; //odo.getPosition().getHeading(AngleUnit.DEGREES)
//
//            xtrackE = dec.odo.getPosition().getX(DistanceUnit.INCH) - robopos.x;
//            ytrackE = dec.odo.getPosition().getY(DistanceUnit.INCH) - robopos.y;
//            ztrackE = dec.odo.getPosition().getHeading(AngleUnit.DEGREES) - robopos.z;
//
//            Log.d("Error", "X Error: " + xtrackE);
//            Log.d("Error", "Y Error: " + ytrackE);
//            Log.d("Error", "Z Error: " + ztrackE);
//
//            if (xtrackE > 2) {
//                Log.d("Error", "X ERROR OF " + xtrackE + " INCHES");
//            }
//
//            if (ytrackE > 2) {
//                Log.d("Error", "Y ERROR OF " + ytrackE + " INCHES");
//            }
//
//            if (ztrackE > 2) {
//                Log.d("Error", "Z ERROR OF " + ztrackE + " INCHES");
//            }
//
//            dec.odo.setPosition(new Pose2D(DistanceUnit.INCH, robopos.x, robopos.y, AngleUnit.DEGREES, robopos.z + 90));
//        }
//    */
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
//        }
//
//        Log.d("DriveCheck", "Error = " + dec.xE + ", " + dec.yE + ", " + dec.zE);
//
//        double theta = (Math.atan(dec.yE / dec.xE)) * (180/Math.PI);
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
//    //endregion
//
//    public void LaunchS() {
//        dec.launch.setPower(0.71); //0.72
//        dec.hood.setPosition(0.4); //0.46
//    }
//
//    public void LaunchReset() {
//        dec.launch.setPower(0);
//        dec.hood.setPosition(dec.hooddown);
//    }
//
//    public void LaunchP(double qorder, String ipattern, double time) {
//        drivetimeout = runtime.milliseconds() + time;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(dec.launchpos.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(dec.launchpos.getY(DistanceUnit.INCH)) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.launchpos.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(dec.launchpos, 5, 200);
//            Turret();
//        }
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(dec.launchpos);
//
//        dec.intake.setPower(0);
//
//        double pos1 = dec.susanpos1;
//        double pos2 = dec.susanpos2;
//        double pos3 = dec.susanpos3;
//
//        if (((qorder == 0) && (ipattern.matches("gpp"))) || ((qorder == 1) &&
//                (ipattern.matches("pgp"))) || ((qorder == 2) && (ipattern.matches("ppg")))) {
//            pos1 = dec.susanpos1;
//            pos2 = dec.susanpos2;
//            pos3 = dec.susanpos3;
//        } else if (((qorder == 0) && (ipattern.matches("pgp"))) || ((qorder == 1) &&
//                (ipattern.matches("gpp")))) {
//            pos1 = dec.susanpos2;
//            pos2 = dec.susanpos1;
//            pos3 = dec.susanpos3;
//        } else if (((qorder == 0) && (ipattern.matches("ppg"))) || ((qorder == 2) &&
//                (ipattern.matches("gpp")))) {
//            pos1 = dec.susanpos3;
//            pos2 = dec.susanpos2;
//            pos3 = dec.susanpos1;
//        } else if (((qorder == 1) && (ipattern.matches("ppg"))) || ((qorder == 2) &&
//                (ipattern.matches("pgp")))) {
//            pos1 = dec.susanpos1;
//            pos2 = dec.susanpos3;
//            pos3 = dec.susanpos2;
//        }
//
//        dec.susan.setPosition(pos1);
//
//        sleep(250);
//
//        dec.transfer.setPower(1);
//
//        sleep(250);
//
//        dec.susan.setPosition(pos2);
//
//        sleep(500);
//
//        dec.susan.setPosition(pos3);
//
//        sleep(500);
//
//        dec.transfer.setPower(0);
//    }
//
//    public void Turret() {
//        double lastvolt = dec.turretenc.getVoltage();
//
//        if (dec.teamcolor.matches("Red")) {
//            dec.goal = dec.Rgoal;
//        } else {
//            dec.goal = dec.Bgoal;
//        }
//
//        double fangle = Math.atan((dec.goal.getY(DistanceUnit.INCH) - dec.odo.getPosition().getY(
//                DistanceUnit.INCH)) / (dec.goal.getX(DistanceUnit.INCH) - dec.odo.getPosition().getX(
//                DistanceUnit.INCH)));
//        double newRheading = dec.odo.getHeading(AngleUnit.DEGREES) - 90;
//        double turtarangle = fangle - newRheading;
//
//        if (Math.abs(dec.turretenc.getVoltage() - lastvolt) > 1) {
//            if (dec.turretenc.getVoltage() < 1) {
//                dec.rev = dec.rev - 1;
//            } else {
//                dec.rev = dec.rev + 1;
//            }
//        }
//
//        dec.turdegpos = (dec.rev + (dec.turretenc.getVoltage() / dec.turretenc.getMaxVoltage())) * dec.turdegperrev;
//
//        if (turtarangle > dec.turdegpos) {
//            dec.turret.setPower(1);
//        } else if (turtarangle < dec.turdegpos) {
//            dec.turret.setPower(-1);
//        } else {
//            dec.turret.setPower(0);
//        }
//    }
//
//    /*
//    public void Hood() {
//        double lastvolt = dec.turretenc.getVoltage();
//
//        if (Math.abs(dec.turretenc.getVoltage() - lastvolt) > 1) {
//            if (dec.turretenc.getVoltage() < 1) {
//                dec.rev = dec.rev - 1;
//            } else {
//                dec.rev = dec.rev + 1;
//            }
//        }
//
//        dec.turdegpos = (dec.rev + (dec.turretenc.getVoltage() / dec.turretenc.getMaxVoltage())) * dec.turdegperrev;
//
//        if (turtarangle > dec.turdegpos) {
//            dec.turret.setPower(1);
//        } else if (turtarangle < dec.turdegpos) {
//            dec.turret.setPower(-1);
//        } else {
//            dec.turret.setPower(0);
//        }
//    }
//     */
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
//
//        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
//
//        if (dec.goalcode != null) {
//            telemetry.addData("Goal Code Center X: ", dec.goalcode.center.x);
//        }
//
//        telemetry.addData("# AprilTags Detected", currentDetections.size());
//
//        // Step through the list of detections and display info for each one.
//        for (AprilTagDetection detection : currentDetections) {
//            if (detection.metadata != null) {
//                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
//                // Only use tags that don't have Obelisk in them
//                if (!detection.metadata.name.contains("Obelisk")) {
//                    dec.campos.x = detection.robotPose.getPosition().x;
//                    dec.campos.y = detection.robotPose.getPosition().y;
//                    dec.campos.z = detection.robotPose.getOrientation().getYaw(AngleUnit.DEGREES);
//
//                    telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f ", dec.campos.x, dec.campos.y, dec.campos.z));
//
//                    /*telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)",
//                            detection.robotPose.getPosition().x,
//                            detection.robotPose.getPosition().y,
//                            detection.robotPose.getPosition().z));
//                    telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)",
//                            detection.robotPose.getOrientation().getPitch(AngleUnit.DEGREES),
//                            detection.robotPose.getOrientation().getRoll(AngleUnit.DEGREES),
//                            detection.robotPose.getOrientation().getYaw(AngleUnit.DEGREES)));*/
//                }
//            } else {
//                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
//                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
//            }
//        }   // end for() loop
//
//        // Add "key" information to telemetry
//        /*telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
//        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");*/
//
//    }
//}
