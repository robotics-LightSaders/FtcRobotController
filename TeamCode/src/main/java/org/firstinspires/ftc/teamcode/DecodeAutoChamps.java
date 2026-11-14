//package org.firstinspires.ftc.teamcode;
//
//import android.util.Log;
//import android.util.Size;
//
//import com.qualcomm.hardware.lynx.LynxModule;
//import com.qualcomm.hardware.rev.RevColorSensorV3;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.hardware.AnalogInput;
//import com.qualcomm.robotcore.hardware.CRServo;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.hardware.NormalizedRGBA;
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
//@Autonomous(name = "AutoChamps", group = "Linear Opmode")
//public class DecodeAutoChamps extends LinearOpMode {
//    DecodeConstants dec = new DecodeConstants();
//
//    RevColorSensorV3 f2;
//    NormalizedRGBA f2c;
//    RevColorSensorV3 r2;
//    NormalizedRGBA r2c;
//    RevColorSensorV3 l2;
//    NormalizedRGBA l2c;
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
//    double drivetimeout = 0;
//
//    double pos1 = dec.susanlaunch1;
//    double pos2 = dec.susanlaunch2;
//    double pos3 = dec.susanlaunch3;
//
//    double switchtime = 0;
//
//    double halo = 0;
//
//    boolean first = false;
//    boolean sort = false;
//    double tar = 0;
//    boolean dos;
//    double minus = 3;
//    boolean canscan = true;
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
//        f2 = hardwareMap.get(RevColorSensorV3.class, "f2");
//        r2 = hardwareMap.get(RevColorSensorV3.class, "r2");
//        l2 = hardwareMap.get(RevColorSensorV3.class, "l2");
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
//        dec.turret.setPower(0);
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
//                dec.PIDFf.p, dec.PIDFf.i, dec.PIDFf.d, dec.PIDFf.f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
//        ));
//
//        initAprilTag();
//
//        while (!gamepad1.a) {
//            if (gamepad1.x) {
//                dec.teamcolor = "Blue";
//                dec.goal = dec.Bgoal;
//
//                dec.initTaxon();
//                dec.turret.setPower(0);
//            } else if (gamepad1.b) {
//                dec.teamcolor = "Red";
//                dec.goal = dec.Rgoal;
//
//                dec.initTaxon();
//                dec.turret.setPower(0);
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
//            /*if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) > 200) {
//                dec.turretpwrcoeff = 10;
//            } else {
//                dec.turretpwrcoeff = Range.clip((Math.abs(dec.taxon.getTotalRotation() -
//                        dec.taxon.getTargetRotation()) / 50), 1, 10); //25, with /2 for distance
//            }
//
//            dec.turretpower = Range.clip((dec.tminp * dec.turretpwrcoeff), 0.08, 1); //0.09?*/
//
//            dec.div = 60;
//
//            if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) > 120) {
//                dec.turretpwrcoeff = 12.5;
//            } else if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) > dec.ttol) {
//                if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) < 30) {
//                    if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) > 5) {
//                        dec.div = 20;
//                    }
//                } else {
//                    dec.div = dec.div / 2;
//                }
//
//                dec.turretpwrcoeff = Range.clip((Math.abs(dec.taxon.getTotalRotation() -
//                        dec.taxon.getTargetRotation()) / dec.div), 1, 10);
//
//                if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) < 5) {
//                    dec.turretpwrcoeff = Range.clip((dec.turretpwrcoeff / 2), 1, 5);
//                }
//            } else {
//                dec.turretpwrcoeff = 0;
//            }
//
//            dec.turretpower = Range.clip((dec.tminp * dec.turretpwrcoeff), 0.08, 1);
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
//        dec.susan.setPosition(dec.susanpos3);
//        dec.hood.setPosition(dec.hoodfar);
//        dec.camrot.setPosition(dec.camlong);
//        dec.turret.setPower(0);
//
//        dec.configureOdo(true);
//        dec.odo.setPosition(dec.odoorigin);
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
//        FW(dec.GD(dec.odo.getPosition(), dec.goal), true);
//
//        //region - Scan AprilTag
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
//        //endregion
//
//        if (dec.teamcolor.matches("Red")) {
//            dec.odo.update();
//
//            Cycle(dec.Rstart, 2400, 1, dec.teamcolor, dec.firstorder); //3000, 2300
//
//            Cycle(dec.Rlaunch1, 2500, 2, dec.teamcolor, "ppg"); //1000?
//
//            //Cycle (dec.Rlaunch2, 1500, 3, dec.teamcolor, "pgg");
//
//            if (dec.order.matches("ppg")) {
//                Launch(dec.Rlaunch2, dec.order, "pgp", 1700, 940, 0); //1500, 935
//            } else if (dec.order.matches("pgp")) {
//                Launch(dec.Rlaunch2, dec.order, "pgp", 1500, 928, 5); //1500, 935 0, 930, 925
//            } else {
//                Launch(dec.Rlaunch2, dec.order, "pgp", 1500, 985, 5); //945, 955, 965, 975
//            }
//
//            Park(dec.Rpark2);
//        } else {
//            dec.odo.update();
//
//            Cycle(dec.Bstart, 3000, 1, dec.teamcolor, dec.firstorder); //1000?
//
//            Cycle(dec.Blaunch1, 2500, 2, dec.teamcolor, "ppg"); //1000?
//
//            if (dec.order.matches("pgp")) {
//                Launch(dec.Blaunch2, dec.order, "pgp", 1500, 42, 3); //1000?, 0, 40 0
//            } else if (dec.order.matches("ppg")) {
//                Launch(dec.Blaunch2, dec.order, "pgp", 1700, 54, 13); //42 3, 5, 10, 53 17, 54 1500, 60 1600, 56 19, 53
//            } else {
//                Launch(dec.Blaunch2, dec.order, "pgp", 1700, 46, 6); //55 13
//            }
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
//    public void Drive(Pose2D target, double halo, double turnstart, boolean xx, boolean max) {
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
//        if (xx) {
//            dec.XPOWER = dec.XPOWER * 1.8; //2, 1.5, 1.7
//        }
//
//        Log.d("DriveCheck", "Dir Pwrs = " + dec.XPOWER + ", " + dec.YPOWER + ", " + dec.ZPOWER);
//
//        if (!max) {
//            //dec.XPOWER = dec.XPOWER / 2.5;
//            dec.YPOWER = dec.YPOWER / 2.5;
//            dec.ZPOWER = dec.ZPOWER * 1.5;
//        }
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
//    public void DriveHold() {
//        dec.fl.setPower(-0.1);
//        dec.fr.setPower(-0.1);
//        dec.bl.setPower(0.1);
//        dec.br.setPower(0.1);
//
//        sleep(10); //50
//
//        DriveStop();
//    }
//
//    public void Cycle(Pose2D launch, double LdriveT, double intakenum, String color, String inorder) {
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
//        //dec.intake.setPower(0.3);
//
//        if (launch == dec.Rstart) {
//            tar = 976; //970, 980
//            minus = 12; //1, 3, 5, 7, 10
//        } else if (launch == dec.Rlaunch1) {
//            if (dec.order.matches("ppg")) {
//                tar = 946; //960, 955, 950, 940, 946, 944
//                minus = 13;
//            } else if (dec.order.matches("gpp")) {
//                tar = 959; //946, 950, 953, 956
//                minus = 16; //13, 15, 17, 21, 15
//            } else {
//                tar = 949; //960, 955, 950, 940, 946, 950, 948
//                minus = 12; //1, 3, 6, 8, 10
//            }
//        } else if (launch == dec.Bstart) {
//            if (dec.order.matches("pgp")) {
//                tar = 57; //55
//                minus = 0;
//            } else {
//                tar = 55; //62, 59
//                minus = 4;
//            }
//        }else if (launch == dec.Blaunch1) {
//            if (dec.order.matches("pgp")) {
//                tar = 102;
//                minus = 0;
//            } else if (dec.order.matches("ppg")) {
//                tar = 96; //102, 105, 103, 99, 102, 100
//                minus = 6; //0, 3
//            } else {
//                tar = 89; //96
//                minus = 6;
//            }
//        } else {
//            tar = 0;
//            minus = 0;
//            /*else {
//            tar = 880; //0, 900, 880, 875
//
//            if (launch == dec.Rlaunch2) {
//                minus = 12; //3, 6
//            } else {
//            minus = 0;
//            }
//        }*/
//        }
//
//        Launch(launch, dec.order, inorder, LdriveT, tar, minus); //add afix in tar to account for 1/3 of robo angle?
//
//        sleep(400); //200?
//
//        dec.turret.setPower(0);
//        dec.hood.setPosition(dec.hoodmid);
//        dec.intake.setPower(1);
//        dec.susan.setPosition(dec.susanpos1);
//
//        if (intakenum < 2) {
//            drivetimeout = runtime.milliseconds() + 1050; //1800, 1400, 1200, 1000, 1100
//        } else {
//            if (dec.order.matches("ppg") && (launch == dec.Blaunch1)) {
//                drivetimeout = runtime.milliseconds() + 2150; //1900, 2050
//            } else {
//                drivetimeout = runtime.milliseconds() + 2150; //2000, 1600, 1800, 1900, 2200, 2100
//            }
//        }
//
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(preintake.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(preintake.getY(DistanceUnit.INCH)) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(preintake.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            if (launch == dec.Rlaunch1) {
//                Drive(preintake, 10, 200, true, true); //2, false, 20
//            } else {
//                Drive(preintake, 7, 200, true, true); //2, false
//            }
//
//            FW(0, false);
//        }
//
//        dec.susan.setPosition(dec.susanpos1);
//        dec.transfer.setPower(0);
//
//        DriveHold();
//        DriveStop();
//        Pos(preintake);
//
//        if (dec.order.matches("ppg")) {
//            halo = 10; //50
//        } else {
//            halo = 5;
//        }
//
//        first = true;
//        sort = false;
//
//        drivetimeout = runtime.milliseconds() + 2200; //2200, 2500, 2800, 3000, 3200, 2000
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(intakepos.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(intakepos.getY(DistanceUnit.INCH)) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(intakepos.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(intakepos, halo, 200, false, false); //20, 50 true
//
//            /*if ((f2.getDistance(DistanceUnit.CM) < 4) && first && (runtime.milliseconds() > (drivetimeout - 2000))) { //1800
//                sort = true;
//                first = false;
//            }
//
//            if (sort) {*/
//            IntakeSort();
//            //}
//
//            if (dec.order.matches("pgp") && (launch == dec.Rlaunch1)) {
//                Turret(985); //965, 975
//            }
//        }
//
//        DriveStop();
//        Pos(intakepos);
//
//        sort = false;
//
//        //sleep(150);
//
//        dec.susan.setPosition(dec.susanpos3);
//    }
//
//    public void Park(Pose2D end) {
//        drivetimeout = runtime.milliseconds() + 1500; //1000?
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((Math.abs(end.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(end.getY(DistanceUnit.INCH) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(end.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(end, 15, 200, false, true); //5
//
//            if (dec.taxon.getTotalRotation() > 50) { //bigger number to not break hardstop?
//                dec.turret.setPower(1);
//            }
//
//            dec.autoend = dec.odo.getPosition(); //does this work?
//        }
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(end);
//
//        dec.autoend = dec.odo.getPosition();
//
//        //add reset
//        if (dec.teamcolor.matches("Blue")) {
//            drivetimeout = runtime.milliseconds() + 200; //500, 300
//        } else {
//            drivetimeout = runtime.milliseconds() + 700; //500
//        }
//
//        dec.intake.setPower(0);
//        dec.transfer.setPower(0);
//        dec.susan.setPosition(dec.susanpos1); //auto1?
//
//        while ((dec.taxon.getTotalRotation() > 100) && opModeIsActive() && (runtime.milliseconds() < drivetimeout)) {
//            dec.turret.setPower(1);
//
//            dec.autoend = dec.odo.getPosition();
//        }
//
//        dec.turret.setPower(0);
//
//        dec.autoend = dec.odo.getPosition();
//    }
//    //endregion
//
//    public void Launch(Pose2D launchpos, String qorder, String order321, double time, double Target, double less) {
//        dec.susan.setPosition(dec.susanpos3);
//
//        /*if ((launchpos != dec.Rstart) && (launchpos != dec.Bstart)) {
//            dec.intake.setPower(1);
//        }*/
//
//        FW((dec.GD(dec.odo.getPosition(), dec.goal) - less), true);
//
//        drivetimeout = runtime.milliseconds() + time;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && (((Math.abs(launchpos.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(launchpos.getY(DistanceUnit.INCH) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(launchpos.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25)) || ((dec.taxon.getTotalRotation() > (dec.taxon.
//                getTargetRotation() + dec.ttol)) || (dec.taxon.getTotalRotation() < (dec.taxon.getTargetRotation() - dec.ttol)))
//                || (dec.launch.getVelocity() < (dec.launchCalc(dec.GD(dec.odo.getPosition(), dec.goal), true))))) { //100
//            if (launchpos == dec.Rlaunch2) {
//                Drive(launchpos, 30, 200, false, true); //20, 25
//            } else if (launchpos == dec.Blaunch2) {
//                Drive(launchpos, 20, 200, true, true); //30, 25, 17
//            } else {
//                Drive(launchpos, 7, 200, true, true); //2?
//            }
//
//            Turret(Target);
//
//            FW((dec.GD(dec.odo.getPosition(), dec.goal) - less), true);
//        }
//
//        if ((launchpos == dec.Rlaunch2) || (launchpos == dec.Blaunch2) ||
//                ((launchpos == dec.Blaunch1) && (dec.order.matches("ppg")))) {
//            dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//            dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//            dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//            dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//            sleep(200);
//
//            dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//            dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//            dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//            dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//        }
//
//        DriveStop();
//        Pos(launchpos);
//
//        Log.d("Turret", "Target1: " + dec.taxon.getTargetRotation());
//        Log.d("Turret", "Current1: " + dec.taxon.getTotalRotation());
//
//        dec.turret.setPower(0);
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
//        Log.d("Check", "0");
//
//        if (pos1 == dec.susanlaunch1) {
//            dec.susan.setPosition(pos1);
//
//            sleep(500);
//        }
//
//        if (pos1 != dec.susanlaunch2) {
//            dec.transfer.setPower(1);
//
//            sleep(100);
//
//            if (launchpos == dec.Blaunch2) {
//                sleep(900); //300
//                sleep(900); //300
//            }
//        }
//
//        Log.d("Check", "1");
//
//        dec.susan.setPosition(pos1);
//
//        /*if (dec.teamcolor.matches("Blue")) {
//            dec.transfer.setPower(-0.5);
//        }*/
//
//        if (pos1 == dec.susanlaunch2) {
//            sleep(200);
//
//            dec.transfer.setPower(1);
//
//            Log.d("Check", "1a");
//        } else if (pos1 == dec.susanpos1) {
//            sleep(400); //800
//
//            dec.transfer.setPower(1);
//
//            Log.d("Check", "1b");
//        } else {
//            sleep(200);
//        }
//
//        Log.d("Check", "2");
//
//        if (!dec.order.matches("pgp")) {
//            sleep(250);
//        } else {
//            sleep(500);
//        }
//
//        if ((pos1 == dec.susanlaunch3) && (pos2 == dec.susanlaunch1)) {
//            dec.transfer.setPower(-0.5);
//
//            dec.susan.setPosition(pos2);
//
//            sleep(400); //600
//
//            dec.transfer.setPower(1);
//
//            if (pos1 == dec.susanlaunch3) {
//                sleep(200);
//            }
//
//            Log.d("Check", "2a");
//        } else {
//            dec.susan.setPosition(pos2);
//
//            Log.d("Check", "2b");
//        }
//
//        Log.d("Check", "3");
//
//        if (!dec.order.matches("pgp")) {
//            sleep(700);
//        } else {
//            sleep(500); //400
//        }
//
//        dec.susan.setPosition(pos3);
//
//        Log.d("Check", "4");
//
//        sleep(800); //600
//
//        Log.d("Check", "5");
//    }
//
//    public void Turret(double target) {
//        dec.taxon.update();
//
//        if (target == 0) {
//            if (Math.abs(dec.setTurretAngle(dec.odo.getPosition(), dec.goal, dec.teamcolor) - dec.taxon.
//                    getTotalRotation()) > dec.ttol) {
//                dec.taxon.setTargetRotation(dec.setTurretAngle(dec.odo.getPosition(), dec.goal, dec.teamcolor));
//            } else {
//                dec.taxon.setTargetRotation(dec.taxon.getTotalRotation());
//            }
//        } else {
//            dec.taxon.setTargetRotation(target);
//        }
//
//        dec.div = 60;
//
//        if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) > 120) {
//            dec.turretpwrcoeff = 12.5;
//        } else if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) > dec.ttol) {
//            if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) < 30) {
//                if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) > 5) {
//                    dec.div = 8; //20, 15, 12
//                }
//            } else {
//                dec.div = dec.div / 4; //2, 4, 6
//            }
//
//            dec.turretpwrcoeff = Range.clip((Math.abs(dec.taxon.getTotalRotation() -
//                    dec.taxon.getTargetRotation()) / dec.div), 1, 10);
//
//            if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) < 5) {
//                dec.turretpwrcoeff = Range.clip((dec.turretpwrcoeff / 2), 1, 5);
//            }
//        } else {
//            dec.turretpwrcoeff = 0;
//        }
//
//        dec.turretpower = Range.clip((dec.tminp * dec.turretpwrcoeff), 0.08, 1);
//
//        if (dec.taxon.getTotalRotation() > (dec.taxon.getTargetRotation() + dec.ttol)) {
//            dec.turret.setPower(dec.turretpower);
//        } else if (dec.taxon.getTotalRotation() < (dec.taxon.getTargetRotation() - dec.ttol)) {
//            dec.turret.setPower(-dec.turretpower);
//        } else {
//            dec.turret.setPower(0);
//        }
//
//        Log.d("Turret", "Target: " + dec.taxon.getTargetRotation());
//        Log.d("Turret", "Current: " + dec.taxon.getTotalRotation());
//        Log.d("Turret", "Power: " + dec.turret.getPower());
//    }
//
//    public void IntakeSort() {
//        f2c = f2.getNormalizedColors();
//        r2c = r2.getNormalizedColors();
//        l2c = l2.getNormalizedColors();
//
//        if ((f2.getDistance(DistanceUnit.CM) < 4)) {
//            if ((f2c.green / f2c.red) > dec.gratio) {
//                dec.revf = "Green";
//            } else {
//                dec.revf = "Purple";
//            }
//        } else {
//            dec.revf = "None";
//        }
//
//        if ((r2.getDistance(DistanceUnit.CM) < 5)) {
//            if ((r2c.green / r2c.red) > dec.gratio) {
//                dec.revr = "Green";
//            } else {
//                dec.revr = "Purple";
//            }
//        } else {
//            dec.revr = "None";
//        }
//
//        if ((l2.getDistance(DistanceUnit.CM) < 4)) {
//            if ((l2c.green / l2c.red) > dec.gratio) {
//                dec.revl = "Green";
//            } else {
//                dec.revl = "Purple";
//            }
//        } else {
//            dec.revl = "None";
//        }
//
//        if (canscan) {
//            if (dec.f0.getState() || dec.revf.matches("Purple")) {
//                dec.FC = "Purple";
//            } else if (dec.f1.getState() || dec.revf.matches("Green")) {
//                dec.FC = "Green";
//            } else {
//                dec.FC = "None";
//            }
//
//            if (dec.r0.getState() || dec.revr.matches("Purple")) {
//                dec.RC = "Purple";
//            } else if (dec.r1.getState() || dec.revr.matches("Green")) {
//                dec.RC = "Green";
//            } else {
//                dec.RC = "None";
//            }
//
//            if (dec.l0.getState() || dec.revl.matches("Purple")) {
//                dec.LC = "Purple";
//            } else if (dec.l1.getState() || dec.revl.matches("Green")) {
//                dec.LC = "Green";
//            } else {
//                dec.LC = "None";
//            }
//        }
//
//        Log.d("Sorting", "F, R, L: " + dec.FC + ", " + dec.RC + ", " + dec.LC);
//
//        //boolean second = false;
//
//        if ((switchtime - 100) < runtime.milliseconds()) {
//            canscan = true;
//
//            /*if ((!dec.FC.matches("None")) || second) {
//                Log.d("Sorting", "1");
//                if ((dec.susan.getPosition() < 0.2)) {
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
//                } else if ((dec.susan.getPosition() < 0.6)) {
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
//                } else {
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
//                }
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
//            }*/
//            if (!dec.FC.matches("None")) {
//                if ((Math.abs(dec.susan.getPosition() - dec.susanpos1) < 0.01) || (Math.abs(dec.susan.getPosition()
//                        - dec.susanlaunch1) < 0.01)) {
//                    if (dec.LC.matches("None")) {
//                        if ((switchtime + 250) < runtime.milliseconds()) { //150, 200
//                            dec.susan.setPosition(dec.susanpos2);
//                            switchtime = runtime.milliseconds() + 500;
//                            canscan = false;
//                        }
//                    } else if (dec.RC.matches("None")) {
//                        if ((switchtime + 250) < runtime.milliseconds()) {
//                            dec.susan.setPosition(dec.susanpos3); //pos3?
//                            switchtime = runtime.milliseconds() + 500;
//                            canscan = false;
//                        }
//                    }
//                } else if ((Math.abs(dec.susan.getPosition() - dec.susanpos2) < 0.01) || (Math.abs(dec.susan.getPosition()
//                        - dec.susanlaunch2) < 0.01)) {
//                    if (dec.LC.matches("None")) {
//                        if ((switchtime + 250) < runtime.milliseconds()) {
//                            dec.susan.setPosition(dec.susanpos3);
//                            switchtime = runtime.milliseconds() + 500;
//                            canscan = false;
//                        }
//                    } else if (dec.RC.matches("None")) {
//                        if ((switchtime + 250) < runtime.milliseconds()) {
//                            dec.susan.setPosition(dec.susanpos1);
//                            switchtime = runtime.milliseconds() + 500;
//                            canscan = false;
//                        }
//                    }
//                } else if ((Math.abs(dec.susan.getPosition() - dec.susanpos3) < 0.01) || (Math.abs(dec.susan.getPosition()
//                        - dec.susanlaunch3) < 0.01)) {
//                    if (dec.RC.matches("None")) {
//                        if ((switchtime + 250) < runtime.milliseconds()) { //200
//                            dec.susan.setPosition(dec.susanpos2);
//                            switchtime = runtime.milliseconds() + 500;
//                            canscan = false;
//                        }
//                    } else if (dec.LC.matches("None")) {
//                        if ((switchtime + 250) < runtime.milliseconds()) {
//                            dec.susan.setPosition(dec.susanpos1);
//                            switchtime = runtime.milliseconds();
//                            canscan = false;
//                        }
//                    }/* else {
//                        dec.susan.setPosition(dec.susanpos3);
//                        switchtime = runtime.milliseconds();
//                    }
//                } else {
//                    switchtime = runtime.milliseconds();*/
//                }
//            } else if (dec.RC.matches("None") && dec.LC.matches("None")) {
//                if ((switchtime + 250) < runtime.milliseconds()) {
//                    dec.susan.setPosition(dec.susanpos1);
//                    switchtime = runtime.milliseconds();
//                    canscan = false;
//                }
//            } else if (!dec.RC.matches("None")) {
//                if ((switchtime + 250) < runtime.milliseconds()) {
//                    dec.susan.setPosition(dec.susanpos3);
//                    switchtime = runtime.milliseconds();
//                    canscan = false;
//                }
//            } /*else {
//                if ((Math.abs(dec.susan.getPosition() - dec.susanbtw12) < 0.01) || (Math.abs(dec.susan.getPosition()
//                        - dec.susanlaunch1) < 0.01)) {
//                    dec.susan.setPosition(dec.susanpos1);
//                } else if (Math.abs(dec.susan.getPosition() - dec.susanlaunch2) < 0.01) {
//                    dec.susan.setPosition(dec.susanpos2);
//                } else if ((Math.abs(dec.susan.getPosition() - dec.susanbtw23) < 0.01) || (Math.abs(dec.susan.getPosition()
//                        - dec.susanlaunch3) < 0.01)) {
//                    dec.susan.setPosition(dec.susanpos3);
//                }
//            }*/
//        }
//    }
//
//    public void FW(double dist, boolean on) {
//        double distance;
//
//        if (on) {
//            distance = dist;
//
//            dec.hood.setPosition(dec.launchCalc(distance, false));
//        } else {
//            distance = 21;
//
//            dec.hood.setPosition(dec.hoodmid);
//        }
//
//        dec.launch.setVelocity(dec.launchCalc(distance, true));
//
//        if (distance > 112) {
//            dec.launch.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
//                    dec.PIDFf.p, dec.PIDFf.i, dec.PIDFf.d, dec.PIDFf.f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
//            ));
//        } else {
//            dec.launch.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
//                    dec.PIDFc.p, dec.PIDFc.i, dec.PIDFc.d, dec.PIDFc.f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
//            ));
//        }
//
//        Log.d("FW", "Dist: " + dist);
//        Log.d("FW", "Target: " + (dec.launchCalc(distance, true) / 28 * 60));
//        Log.d("FW", "Actual: " + (dec.launch.getVelocity() / 28 * 60));
//    }
//
//    private void initAprilTag() {
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
