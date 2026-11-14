//package org.firstinspires.ftc.teamcode;
//
//import android.util.Log;
//import android.util.Size;
//
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.Servo;
//import com.qualcomm.robotcore.util.ElapsedTime;
//import com.qualcomm.robotcore.util.Range;
//
//import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
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
//import java.util.ArrayList;
//import java.util.List;
//
//@Disabled
//@Autonomous(name = "DecodeTestAuto", group = "Linear Opmode")
//public class DecodeTestAuto extends LinearOpMode {
//    private ElapsedTime runtime = new ElapsedTime();
//
//    DecodeConstants dec = new DecodeConstants();
//
//    private Position cameraPosition = new Position(DistanceUnit.INCH,
//            0, 0, 16.16, 0); //5.84, 7.94, 16.16
//    private YawPitchRollAngles cameraOrientation = new YawPitchRollAngles(AngleUnit.DEGREES,
//            0, -109, 0, 0); //need to put yaw adjustment as servo rotates?
//
//    private AprilTagProcessor aprilTag;
//    private VisionPortal visionPortal;
//
//    Position campos = new Position(DistanceUnit.INCH, 0, 0, 0, 0);
//    Position robopos = new Position(DistanceUnit.INCH, 0, 0, 0, 0);
//
//    Servo camrot;
//
//    double camRangle = 0;
//    double camFangle = 0;
//    double autoangle = 0;
//    double targetangle = 0;
//
//    double drivetimeout = 0;
//    double rottime = 0;
//
//    double xtrackE = 0;
//    double ytrackE = 0;
//    double ztrackE = 0;
//    //endregion
//
//    double xd = 0;
//    double yd = 0;
//    double totd = 0;
//    double maxvel = 78; //MiracleChild in/sec
//    double drivetime = 0;
//
//    Pose2D T11 = new Pose2D(DistanceUnit.INCH, -71, 47.5, AngleUnit.DEGREES, 55);
//    Pose2D T12 = new Pose2D(DistanceUnit.INCH, 0, 71, AngleUnit.DEGREES, 0);
//    Pose2D T13 = new Pose2D(DistanceUnit.INCH, 71, 47.5, AngleUnit.DEGREES, -55);
//    Pose2D T14 = new Pose2D(DistanceUnit.INCH, 71, -47.5, AngleUnit.DEGREES, -125);
//    Pose2D T15 = new Pose2D(DistanceUnit.INCH, 0, -71, AngleUnit.DEGREES, 0);
//    Pose2D T16 = new Pose2D(DistanceUnit.INCH, -71, -47.5, AngleUnit.DEGREES, 125);
//
//    Pose2D spline1 = new Pose2D(DistanceUnit.INCH, 25, -3, AngleUnit.DEGREES, -45);
//    Pose2D spline2 = new Pose2D(DistanceUnit.INCH, 30, -5, AngleUnit.DEGREES, -90);
//    Pose2D spline3 = new Pose2D(DistanceUnit.INCH, 25, -7, AngleUnit.DEGREES, -135);
//    Pose2D spline4 = new Pose2D(DistanceUnit.INCH, 5, -12, AngleUnit.DEGREES, -135);
//    Pose2D spline5 = new Pose2D(DistanceUnit.INCH, 0, -14, AngleUnit.DEGREES, -90);
//    Pose2D spline6 = new Pose2D(DistanceUnit.INCH, 5, -16, AngleUnit.DEGREES, -45);
//    Pose2D splineend = new Pose2D(DistanceUnit.INCH, 25, -21, AngleUnit.DEGREES, 0);
//
//    Pose2D w1 = new Pose2D(DistanceUnit.INCH, 0, -24, AngleUnit.DEGREES, -135);
//    Pose2D w2 = new Pose2D(DistanceUnit.INCH, 0, -48, AngleUnit.DEGREES, 135);
//    Pose2D w3 = new Pose2D(DistanceUnit.INCH, 0, -72, AngleUnit.DEGREES, 0);
//
//    @Override
//    public void runOpMode() {
//        initAprilTag();
//
//        //region - Hardware Map
//        dec.fl = hardwareMap.get(DcMotor.class, "fl");
//        dec.fr = hardwareMap.get(DcMotor.class, "fr");
//        dec.bl = hardwareMap.get(DcMotor.class, "bl");
//        dec.br = hardwareMap.get(DcMotor.class, "br");
//
//        //camrot = hardwareMap.get(Servo.class, "rot");
//
//        dec.odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");
//
//        dec.fl.setDirection(DcMotor.Direction.REVERSE);
//        dec.bl.setDirection(DcMotor.Direction.REVERSE);
//
//        dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//        dec.fl.setPower(0);
//        dec.fr.setPower(0);
//        dec.bl.setPower(0);
//        dec.br.setPower(0);
//
//        //camrot.setPosition(0.91); //0.51 = middle
//
//        dec.configureOdo(true);
//        //endregion
//
//        /*while ((!gamepad1.a) && aprilTag.getDetections().isEmpty()) {
//            if (camrot.getPosition() > 0.1) {
//                camrot.setPosition(camrot.getPosition() - 0.0001);
//            } else {
//                camrot.setPosition(0.91);
//            }
//        }*/
//
//        telemetryAprilTag();
//
//        //lower servo position cw, higher is ccw
//        //180/0.395 = 455.696 --> 0.395 = servo travel for 180 degrees, so 455.696 converts servo position to angle
//        //camRangle = (0.51 - camrot.getPosition()) * 455.696;
//
//        robopos.z = campos.z + camRangle;
//        //9.86 = hypotenuse of offsets, angle between robot and camera = 63 degrees
//        robopos.x = campos.x - (9.86 * Math.cos(Math.toRadians(robopos.z + 63))); //5.84 = x offset
//        robopos.y = campos.y - (9.86 * Math.sin(Math.toRadians(robopos.z + 63))); //7.94 = y offset
//        //may need to subtract 1.5 from y?
//
//        camFangle = robopos.z - camRangle; //odo.getPosition().getHeading(AngleUnit.DEGREES)
//
//        telemetry.addLine(String.format("RoboPos: %6.2f %6.2f %6.2f", robopos.x, robopos.y, robopos.z));
//
//        navreset(true, true);
//
//        //odo.setPosition(new Pose2D(DistanceUnit.INCH, robopos.x, robopos.y, AngleUnit.DEGREES, robopos.z + 90));
//        dec.odo.setPosition(dec.odoorigin);
//        dec.odo.update();
//
//        telemetry.addData("Odo x: ", dec.odo.getPosition().getX(DistanceUnit.INCH));
//        telemetry.addData("Odo y: ", dec.odo.getPosition().getY(DistanceUnit.INCH));
//        telemetry.addData("Odo z: ", dec.odo.getPosition().getHeading(AngleUnit.DEGREES));
//        telemetry.addData("Status: ", "Ready");
//        telemetry.update();
//
//        waitForStart();
//        runtime.reset();
//
//        BoxTest();
//
//        /*drivetimeout = runtime.milliseconds() + 2500;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((Math.abs(20 -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(0 -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(0 -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES))) > 0.25)) {
//            Drive(new Pose2D(DistanceUnit.INCH, 20, 0, AngleUnit.DEGREES, 0), 7, 200);
//            //add brake mode braking in halo, but float rest of time, add button in teleop too
//        }
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(new Pose2D(DistanceUnit.INCH, 20, 0, AngleUnit.DEGREES, 0));*/
//
//        /*ArrayList<Pose2D> spline = new ArrayList<>();
//        spline.add(spline1);
//        spline.add(spline2);
//        spline.add(spline3);
//        spline.add(spline4);
//        spline.add(spline5);
//        spline.add(spline6);
//
//        SplineJagged(6, spline, splineend, 7);*/
//
//        //Spline();
//
//        /*drivetimeout = runtime.milliseconds() + 2000;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(odoorigin.getX(DistanceUnit.INCH) -
//                odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(odoorigin.getY(DistanceUnit.INCH)) -
//                odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(odoorigin.getHeading(AngleUnit.DEGREES) -
//                odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            XYZ(odoorigin, 5, 1);
//        }
//
//        DriveStop();
//        Reloc(odo.getPosition());
//        Pos(odoorigin);
//
//        sleep(1000);
//
//        drivetimeout = runtime.milliseconds() + 2000;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(linetest.getX(DistanceUnit.INCH) -
//                odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(linetest.getY(DistanceUnit.INCH)) -
//                odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(linetest.getHeading(AngleUnit.DEGREES) -
//                odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            XYZ(linetest, 3, 1);
//        }
//
//        DriveStop();
//        Reloc(odo.getPosition());
//        Pos(linetest);*/
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
//    public void Reloc(Pose2D at) {
//        if (at.getY(DistanceUnit.INCH) > 0) {
//            if (at.getX(DistanceUnit.INCH) > 24) {
//                targetangle = T13.getHeading(AngleUnit.DEGREES);
//            } else if (at.getX(DistanceUnit.INCH) < -24) {
//                targetangle = T11.getHeading(AngleUnit.DEGREES);
//            } else {
//                targetangle = T12.getHeading(AngleUnit.DEGREES);
//            }
//        } else {
//            if (at.getX(DistanceUnit.INCH) > 24) {
//                targetangle = T14.getHeading(AngleUnit.DEGREES);
//            } else if (at.getX(DistanceUnit.INCH) < -24) {
//                targetangle = T16.getHeading(AngleUnit.DEGREES);
//            } else {
//                targetangle = T15.getHeading(AngleUnit.DEGREES);
//            }
//        }
//
//        autoangle = 0.51 - (targetangle / 455.696);
//        //camrot.setPosition(autoangle);
//        rottime = runtime.milliseconds() + 100;
//
//        while ((aprilTag.getDetections().isEmpty()) && (runtime.milliseconds() < rottime)) {
//            idle();
//        }
//
//        telemetryAprilTag();
//        telemetry.update();
//
//        //lower servo position cw, higher is ccw
//        //180/0.395 = 455.696 --> 0.395 = servo travel for 180 degrees, so 455.696 converts servo position to angle
//        //camRangle = (0.51 - camrot.getPosition()) * 455.696;
//
//        robopos.z = campos.z + camRangle;
//        //9.86 = hypotenuse of offsets, angle between robot and camera = 63 degrees
//        robopos.x = campos.x - (9.86 * Math.cos(Math.toRadians(robopos.z + 63))); //5.84 = x offset
//        robopos.y = campos.y - (9.86 * Math.sin(Math.toRadians(robopos.z + 63))); //7.94 = y offset
//
//        camFangle = robopos.z - camRangle; //odo.getPosition().getHeading(AngleUnit.DEGREES)
//
//        xtrackE = dec.odo.getPosition().getX(DistanceUnit.INCH) - robopos.x;
//        ytrackE = dec.odo.getPosition().getY(DistanceUnit.INCH) - robopos.y;
//        ztrackE = dec.odo.getPosition().getHeading(AngleUnit.DEGREES) - robopos.z;
//
//        Log.d("Error", "X Error: " + xtrackE);
//        Log.d("Error", "Y Error: " + ytrackE);
//        Log.d("Error", "Z Error: " + ztrackE);
//
//        if (xtrackE > 2) {
//            Log.d("Error", "X ERROR OF " + xtrackE + " INCHES");
//        }
//
//        if (ytrackE > 2) {
//            Log.d("Error", "Y ERROR OF " + ytrackE + " INCHES");
//        }
//
//        if (ztrackE > 2) {
//            Log.d("Error", "Z ERROR OF " + ztrackE + " INCHES");
//        }
//
//        dec.odo.setPosition(new Pose2D(DistanceUnit.INCH, robopos.x, robopos.y, AngleUnit.DEGREES, robopos.z + 90));
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
//    public void SplineJagged(int pointcount, ArrayList<Pose2D> waypoints, Pose2D end, double EndRad) {
//        int onpoint = 0;
//
//        while (onpoint < pointcount) {
//            /*odo.update();
//            xd = Math.abs(waypoints.get(onpoint).getX(DistanceUnit.INCH) - odo.getPosition().getX(DistanceUnit.INCH));
//            yd = Math.abs(waypoints.get(onpoint).getY(DistanceUnit.INCH) - odo.getPosition().getY(DistanceUnit.INCH));
//            totd = Math.sqrt(Math.pow(xd, 2) + Math.pow(yd, 2));
//            drivetime = totd / maxvel;
//
//            drivetimeout = runtime.milliseconds() + drivetime + 100;*/
//            while (/*(runtime.milliseconds() < drivetimeout) &&*/ opModeIsActive() && ((xd > 1) || (yd > 1) ||
//                    (Math.abs(waypoints.get(onpoint).getHeading(AngleUnit.DEGREES) - dec.odo.getPosition().getHeading(
//                            AngleUnit.DEGREES)) > 1))) {
//                Drive(waypoints.get(onpoint), 1, 200);
//            }
//
//            Pos(waypoints.get(onpoint));
//
//            onpoint = onpoint + 1;
//        }
//
//        dec.odo.update();
//        xd = Math.abs(end.getX(DistanceUnit.INCH) - dec.odo.getPosition().getX(DistanceUnit.INCH));
//        yd = Math.abs(end.getY(DistanceUnit.INCH) - dec.odo.getPosition().getY(DistanceUnit.INCH));
//        totd = Math.sqrt(Math.pow(xd, 2) + Math.pow(yd, 2));
//        drivetime = totd / maxvel;
//
//        drivetimeout = runtime.milliseconds() + drivetime + 100;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((xd > 0.25) || (yd > 0.25) ||
//                (Math.abs(end.getHeading(AngleUnit.DEGREES) - dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(end, EndRad, 200);
//        }
//
//        DriveStop();
//        Pos(end);
//    }
//
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
//    public void BoxTest() {
//        drivetimeout = runtime.milliseconds() + 2500;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((Math.abs(dec.linetest1.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.linetest1.getY(DistanceUnit.INCH) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.linetest1.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(dec.linetest1, 7, 200);
//        }
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(dec.linetest1);
//
//        sleep(250);
//
//        drivetimeout = runtime.milliseconds() + 2500;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((Math.abs(dec.linetest2.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.linetest2.getY(DistanceUnit.INCH) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.linetest2.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(dec.linetest2, 7, 200);
//        }
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(dec.linetest2);
//
//        sleep(250);
//
//        drivetimeout = runtime.milliseconds() + 2500;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((Math.abs(dec.linetest3.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.linetest3.getY(DistanceUnit.INCH) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.linetest3.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(dec.linetest3, 7, 200);
//        }
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(dec.linetest3);
//
//        sleep(250);
//
//        drivetimeout = runtime.milliseconds() + 2500;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((Math.abs(dec.linetest4.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.linetest4.getY(DistanceUnit.INCH) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.linetest4.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(dec.linetest4, 7, 200);
//        }
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(dec.linetest4);
//
//        sleep(250);
//
//        drivetimeout = runtime.milliseconds() + 2500;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((Math.abs(dec.linetest5.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.linetest5.getY(DistanceUnit.INCH) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.linetest5.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(dec.linetest5, 7, 200);
//        }
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(dec.linetest5);
//
//        sleep(250);
//
//        drivetimeout = runtime.milliseconds() + 2500;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((Math.abs(dec.odoorigin.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.odoorigin.getY(DistanceUnit.INCH) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(dec.odoorigin.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(dec.odoorigin, 7, 200);
//        }
//
//        DriveStop();
//        //Reloc(odo.getPosition());
//        Pos(dec.odoorigin);
//    }
//    //endregion
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
//
//    }   // end method initAprilTag()
//
//    private void telemetryAprilTag() {
//
//        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
//        telemetry.addData("# AprilTags Detected", currentDetections.size());
//
//        // Step through the list of detections and display info for each one.
//        for (AprilTagDetection detection : currentDetections) {
//            if (detection.metadata != null) {
//                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
//                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)",
//                        detection.robotPose.getPosition().x,
//                        detection.robotPose.getPosition().y,
//                        detection.robotPose.getPosition().z));
//                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)",
//                        detection.robotPose.getOrientation().getPitch(AngleUnit.DEGREES),
//                        detection.robotPose.getOrientation().getRoll(AngleUnit.DEGREES),
//                        detection.robotPose.getOrientation().getYaw(AngleUnit.DEGREES)));
//
//                campos.x = detection.robotPose.getPosition().x;
//                campos.y = detection.robotPose.getPosition().y;
//                campos.z = detection.robotPose.getOrientation().getYaw(AngleUnit.DEGREES);
//
//            } else {
//                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
//                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
//            }
//        }   // end for() loop
//
//        // Add "key" information to telemetry
//        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
//        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
//
//    }   // end method telemetryAprilTag()
//}
