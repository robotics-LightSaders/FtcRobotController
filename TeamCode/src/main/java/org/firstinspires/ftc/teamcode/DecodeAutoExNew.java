//package org.firstinspires.ftc.teamcode;
//
//import android.graphics.Color;
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
//import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
//
//@Autonomous(name = "AutoExNew", group = "Linear Opmode")
//@Disabled
//public class DecodeAutoExNew extends LinearOpMode {
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
//    private volatile boolean threadon = true;
//    private Thread TurThr;
//
//    double drivetimeout = 0;
//    double switchtime = 0;
//    boolean canscan = true;
//
//    double wait = 0;
//    double loopcount = 0;
//
//    double pos1 = dec.susanlaunch1;
//    double pos2 = dec.susanlaunch2;
//    double pos3 = dec.susanlaunch3;
//    String order321 = "None";
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
//        dec.camrot = hardwareMap.get(Servo.class, "camrot");
//
//        dec.intakedos = hardwareMap.get(CRServo.class, "intakedos");
//
//        dec.susan = hardwareMap.get(Servo.class, "susan");
//        dec.susanenc = hardwareMap.get(AnalogInput.class, "senc");
//
//        dec.turret = hardwareMap.get(CRServo.class, "turret");
//        dec.oq = hardwareMap.get(OctoQuadFWv3.class, "octoquad");
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
//        dec.initOQ();
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
//                dec.turret.setPower(0);
//            } else if (gamepad1.b) {
//                dec.teamcolor = "Red";
//                dec.goal = dec.Rgoal;
//                dec.turret.setPower(0);
//            }
//
//            dec.tpos = dec.oq.readAllEncoderData().positions[1];
//
//            telemetry.addData("Team Color: ", dec.teamcolor);
//            telemetry.addData("Turret Target: ", dec.ttar);
//            telemetry.addData("Turret At: ", dec.tpos);
//            telemetry.update();
//
//            if (dec.teamcolor.matches("Red")) {
//                dec.ttar = dec.turret90;
//            } else {
//                dec.ttar = dec.turret270; //here?
//            }
//
//            if (Math.abs(dec.tpos - dec.ttar) > 100) {
//                dec.turretpower = 1;
//            } else if (Math.abs(dec.tpos - dec.ttar) > dec.ttol) {
//                if (Math.abs(dec.tpos - dec.ttar) > 7) {
//                    dec.turretpower = Range.clip((0.09 + (0.0000005 * Math.pow(Math.abs(dec.tpos - dec.ttar),
//                            3))), 0, 1);
//                } else {
//                    dec.turretpower = 0.08;
//                }
//            } else {
//                dec.turretpower = 0;
//            }
//
//            if (gamepad1.y) {
//                if (dec.tpos > (dec.ttar + dec.ttol)) {
//                    dec.turret.setPower(dec.turretpower);
//                } else if (dec.tpos < (dec.ttar - dec.ttol)) {
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
//        dec.susan.setPosition(dec.susanpast3);
//        dec.hood.setPosition(dec.hooddown);
//        dec.camrot.setPosition(dec.camclose);
//        dec.turret.setPower(0);
//
//        dec.configureOdo(true);
//        dec.odo.setPosition(dec.odoorigin);
//        //endregion
//
//        navreset(true, true);
//
//        if (dec.teamcolor.matches("Red")) {
//            dec.odo.setPosition(dec.Rex);
//        } else {
//            dec.odo.setPosition(dec.Bex);
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
//        runtime.reset();
//
//        if (dec.teamcolor.matches("Red")) {
//            dec.odo.setPosition(dec.Rex);
//            dec.goal = dec.Rgoal;
//        } else {
//            dec.odo.setPosition(dec.Bex);
//            dec.goal = dec.Bgoal;
//        }
//
//        TurThr = new Thread(() -> {
//            while (threadon && opModeIsActive()) {  // Double-check with opMode active
//                Turret(0);
//
//                dec.odo.update();
//
//                dec.usePos.x = dec.odo.getPosition().getX(DistanceUnit.INCH);
//                dec.usePos.y = dec.odo.getPosition().getY(DistanceUnit.INCH);
//                dec.usePos.z = dec.odo.getPosition().getHeading(AngleUnit.DEGREES);
//
//                if (dec.teamcolor.matches("Red")) {
//                    FW(dec.goalDC(dec.usePos, dec.Rgoal), dec.odo.getPosition().getX(DistanceUnit.INCH), true);
//                } else {
//                    FW(dec.goalDC(dec.usePos, dec.Bgoal), dec.odo.getPosition().getX(DistanceUnit.INCH), true);
//                }
//
//                try {
//                    Thread.sleep(10);  // Check every Xms; 20
//                } catch (InterruptedException e) {
//                    // Ignore; we'll check flag next loop
//                }
//            }
//            // Cleanup: Stop motor when thread exits
//            dec.turret.setPower(0);
//        });
//
//        TurThr.start();
//
//        while (opModeIsActive() && (loopcount < 1)) {
//            if (dec.teamcolor.matches("Red")) {
//                dec.odo.update();
//
//                Cycle(dec.Rlaunch3, 800, 1, dec.teamcolor, false); //1000, 700
//
//                Cycle(dec.Rlaunch3, 2000, 2, dec.teamcolor, false);
//
//                Cycle(dec.Rlaunch3i, 2000, 0, dec.teamcolor, true); //true true
//
//                dec.intake.setPower(0);
//
//                //add secret tunnel pickup here
//                sleep(5000);
//
//                Park(dec.Rexe);
//            } else {
//                dec.odo.update();
//
//                Cycle(dec.Blaunch3, 800, 1, dec.teamcolor, false);
//
//                Cycle(dec.Blaunch3a, 2000, 2, dec.teamcolor, false); //true
//
//                Cycle(dec.Blaunch3i, 2000, 0, dec.teamcolor, true);
//
//                dec.intake.setPower(0);
//                dec.turret.setPower(0);//here
//
//                sleep(5000);
//
//                Park(dec.Bexe);
//            }
//
//            loopcount = loopcount + 1;
//        }
//
//        threadon = false;
//        if (TurThr != null && TurThr.isAlive()) {
//            TurThr.interrupt();  // Break any sleep
//            try {
//                TurThr.join(100);  // Wait up to 100ms for clean exit
//            } catch (InterruptedException e) {
//                // Ignore; proceed to let OpMode end
//            }
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
//            dec.zP = Range.clip((Math.abs(dec.zE) / 90), -0.6, 0.6);
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
//            if ((target == dec.RexGi) || (target == dec.BexGi)) {
//                dec.XPOWER = dec.XPOWER * 1.4;
//                dec.YPOWER = dec.YPOWER * 1.5; //1.3
//            } else {
//                dec.XPOWER = dec.XPOWER * 1.8;
//            }
//        }
//
//        Log.d("DriveCheck", "Dir Pwrs = " + dec.XPOWER + ", " + dec.YPOWER + ", " + dec.ZPOWER);
//
//        if (!max) {
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
//        dec.odo.update();
//
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
//        dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//        dec.fl.setPower(-0.12);
//        dec.fr.setPower(-0.1);
//        dec.bl.setPower(0.1);
//        dec.br.setPower(0.17);
//
//        sleep(50);
//
//        DriveStop();
//    }
//
//    public void Cycle(Pose2D launch, double LdriveT, double intakenum, String color, boolean gate) {
//        Pose2D preintake;
//        Pose2D intakepos;
//        Pose2D gato;
//
//        if (color.matches("Red")) {
//            if (intakenum == 1) {
//                preintake = dec.Rpreintake2;
//                intakepos = dec.Rintake2;
//                gato = dec.RexG;
//            } else if (intakenum == 2) {
//                preintake = dec.Rpreintake3;
//                intakepos = dec.Rintake3;
//                gato = dec.RexGi;
//            } else {
//                preintake = dec.Rexe; //RexGi
//                intakepos = dec.Rexe; //RexGi
//                gato = dec.odoorigin;
//            }
//        } else {
//            if (intakenum == 1) {
//                preintake = dec.Bpreintake2;
//                intakepos = dec.Bintake2;
//                gato = dec.BexG;
//            } else if (intakenum == 2) {
//                preintake = dec.Bpreintake3;
//                intakepos = dec.Bintake3;
//                gato = dec.BexGi;
//            } else {
//                preintake = dec.Bexe; //BexGi
//                intakepos = dec.Bexe; //BexGi
//                gato = dec.odoorigin;
//            }
//        }
//
//        Launch(launch, LdriveT);
//
//        sleep(400);
//
//        dec.turret.setPower(0);
//        //dec.hood.setPosition(dec.hoodmid);
//        dec.intake.setPower(1);
//        dec.susan.setPosition(dec.susanpos1);
//
//        if (intakenum == 2) {
//            drivetimeout = runtime.milliseconds() + 500;
//        } else if (intakenum == 1) {
//            if (color.matches("Red")) {
//                drivetimeout = runtime.milliseconds() + 1400; //2000
//            } else {
//                drivetimeout = runtime.milliseconds() + 1400; //1200
//            }
//        } else {
//            drivetimeout = runtime.milliseconds() + 2100; //1400, 2000
//        }
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(preintake.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(preintake.getY(DistanceUnit.INCH)) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(preintake.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            if (intakenum == 0) {
//                Drive(preintake, 1, 200, true, true);
//            } else {
//                Drive(preintake, 7, 200, true, true);
//            }
//
//            /*FW(0, false);
//
//            if (intakenum == 2) { //0
//                if (color.matches("Red")) {
//                    Turret(dec.turret270); //1
//                } else {
//                    Turret(dec.turret90);
//                }
//            } else if ((intakenum == 0) && color.matches("Blue")) {
//                Turret(1);
//            }*/
//        }
//
//        dec.susan.setPosition(dec.susanpos1);
//        dec.transfer.setPower(0);
//
//        /*if (intakenum == 2) {
//            DriveHold(true);
//            DriveStop(true);
//        } else {*/
//            DriveHold(); //false
//            //DriveStop(false);
//        //}
//
//        dec.turret.setPower(0);
//        Pos(preintake);
//
//        if (!gate) {
//            drivetimeout = runtime.milliseconds() + 2000; //1600 2200, 1400
//            while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(intakepos.getX(DistanceUnit.INCH) -
//                    dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(intakepos.getY(DistanceUnit.INCH)) -
//                    dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(intakepos.getHeading(AngleUnit.DEGREES) -
//                    dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//                Drive(intakepos, 5, 200, false, false);
//
//                IntakeSort(false); //test new w/ has vs not, compared to by color
//                /*if ((drivetimeout - 50) < runtime.milliseconds()) { //600
//                    dec.susan.setPosition(dec.susanpos2);
//                } else if ((drivetimeout - 1600) < runtime.milliseconds()) {
//                    dec.susan.setPosition(dec.susanpos1);
//                }
//
//                if (intakenum == 2) { //0
//                    if (color.matches("Red")) {
//                        Turret(dec.turret270); //1
//                    } else {
//                        Turret(dec.turret90);
//                    }
//                }*/
//            }
//
//            /*if (intakenum == 2) {
//                DriveStop(true);
//            } else {*/
//                DriveStop(); //false
//            //}
//
//            dec.turret.setPower(0);
//            Pos(intakepos);
//
//            //dec.susan.setPosition(dec.susanpast3);
//
//            if (gato != dec.odoorigin) {
//                drivetimeout = runtime.milliseconds() + 1200;
//                while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(gato.getX(DistanceUnit.INCH) -
//                        dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(gato.getY(DistanceUnit.INCH)) -
//                        dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(gato.getHeading(AngleUnit.DEGREES) -
//                        dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//                    if (intakenum == 1) {
//                        Drive(gato, 5, 200, false, false);
//                    } else {
//                        Drive(gato, 5, 200, true, false);
//                    }
//
//                    /*if (intakenum == 2) { //0
//                        if (color.matches("Red")) {
//                            Turret(dec.turret270); //1
//                        } else {
//                            Turret(dec.turret90);
//                        }
//                    }*/
//                }
//
//                dec.susan.setPosition(dec.susanpos3);
//                dec.turret.setPower(0);
//
//
//                if (intakenum == 1) {
//
//                    drivetimeout = runtime.milliseconds() + 1000; //
//
//                    while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(preintake.getX(DistanceUnit.INCH) -
//                            dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(preintake.getY(DistanceUnit.INCH)) -
//                            dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(preintake.getHeading(AngleUnit.DEGREES) -
//                            dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//
//                        Drive(preintake, 7, 200, true, true);
//
//
//                /*FW(0, false);
//
//                if (intakenum == 2) { //0
//                    if (color.matches("Red")) {
//                        Turret(dec.turret270); //1
//                    } else {
//                        Turret(dec.turret90);
//                    }
//                } else if ((intakenum == 0) && color.matches("Blue")) {
//                    Turret(1);
//                }*/
//                    }
//                }
//
//                DriveStop();
//                if (intakenum == 1){
//                    Pos(preintake);
//                }else {
//                    Pos(gato);
//                }
//            }
//        }
//    }
//
//    public void Park(Pose2D end) {
//        drivetimeout = runtime.milliseconds() + 600; //1000, 1200, 700
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((Math.abs(end.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(end.getY(DistanceUnit.INCH) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(end.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
//            Drive(end, 1, 200, true, true); //15 false true, 5
//
//            /*if (dec.taxon.getTotalRotation() > 50) {
//                dec.turret.setPower(1);
//            }
//
//            //dec.autoend = dec.odo.getPosition();*/
//        }
//
//        DriveHold();
//        //DriveStop();
//        Pos(end);
//
//        //dec.autoend = dec.odo.getPosition();
//
//        /*if (dec.teamcolor.matches("Blue")) {
//            drivetimeout = runtime.milliseconds() + 1500; //200, 400, 800
//        } else {
//            drivetimeout = runtime.milliseconds() + 100; //700, 0
//        }*/
//
//        dec.intake.setPower(0);
//        dec.transfer.setPower(0);
//        dec.susan.setPosition(dec.susanpos1);
//
//        /*while ((dec.taxon.getTotalRotation() > 100) && opModeIsActive() && (runtime.milliseconds() < drivetimeout)) {
//            dec.turret.setPower(1);
//
//            dec.autoend = dec.odo.getPosition();
//
//            dec.odo.update();
//        }*/
//
//        dec.turret.setPower(0);
//
//        dec.odo.update();
//
//        //dec.autoend = dec.odo.getPosition();
//    }
//    //endregion
//
//    public void Launch(Pose2D launchpos, double time) {
//        dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//        drivetimeout = runtime.milliseconds() + time;
//        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && (((Math.abs(launchpos.getX(DistanceUnit.INCH) -
//                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(launchpos.getY(DistanceUnit.INCH) -
//                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(launchpos.getHeading(AngleUnit.DEGREES) -
//                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25)) || (dec.launch.getVelocity() <
//                (dec.launchCalc(dec.GD(dec.odo.getPosition(), dec.goal), true))))) {
//            Drive(launchpos, 7, 200, true, true);
//            IntakeSort(false);
//        }
//
//        DriveHold();
//        Pos(launchpos);
//
//        //todo add scan obelisk and get pattern on first launch
//
//        if (dec.susan.getPosition() < 0.2) {
//            dec.susan.setPosition(dec.susanpid3);
//
//            sleep(600); //less? todo
//        } else if (dec.susan.getPosition() < 0.6) {
//            dec.susan.setPosition(dec.susanpid3);
//
//            sleep(400); //less? todo
//        } else {
//            dec.susan.setPosition(dec.susanpid3);
//
//            sleep(100);
//        }
//
//        dec.transfer.setPower(1);
//
//        sleep(400); //less? todo
//
//        if ((Math.abs(dec.launch.getVelocity() - dec.LC(dec.goalDC(dec.usePos, dec.goal), dec.odo.getPosition().
//                getX(DistanceUnit.INCH), true)) < 50) && (Math.abs(dec.turret.getPower()) < 0.112)) {
//            wait = wait + 1;
//        }
//
//        //add if sort loop here, else = below
//        dec.susan.setPosition(dec.susanlaunch2); //1
//
//        sleep(500); //200, less? todo
//
//        dec.susan.setPosition(dec.susanlaunch1);
//
//        sleep(100);
//
//        //end loop before this
//        dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//        dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//        dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//        dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//    }
//
//    public void Turret(double target) {
//        dec.tpos = dec.oq.readAllEncoderData().positions[1];
//
//        if (target != 0) {
//            dec.ttar = target;
//        } else {
//            if (Math.abs(dec.setTurretAngle(dec.odo.getPosition(), dec.goal, dec.teamcolor) - dec.tpos) > dec.ttol) {
//                dec.ttar = dec.setTurretAngle(dec.odo.getPosition(), dec.goal, dec.teamcolor);
//            } else {
//                dec.ttar = dec.tpos;
//            }
//        }
//
//        dec.ttar = dec.ttar + 5; //2 todo
//
//        if (Math.abs(dec.tpos - dec.ttar) > 100) {
//            dec.turretpower = 1;
//        } else if (Math.abs(dec.tpos - dec.ttar) > dec.ttol) {
//            if (Math.abs(dec.tpos - dec.ttar) > 7) {
//                dec.turretpower = Range.clip((0.12 + (0.0000005 * Math.pow(Math.abs(dec.tpos - dec.ttar),
//                        3))), 0, 1); //0.09, 11
//            } else {
//                dec.turretpower = 0.09; //0.08
//            }
//        } else {
//            dec.turretpower = 0;
//        }
//
//        if ((dec.terror < Math.abs(dec.ttar - dec.tpos)) && (Math.abs(dec.ttar - dec.tpos) > 30)) {
//            dec.turretpower = Range.clip((dec.turretpower - (0.04 * (dec.terror - Math.abs(dec.ttar -
//                    dec.tpos)))), -1, 1);
//        }
//
//        if ((dec.tpos > (dec.ttar + dec.ttol))) {
//            dec.turret.setPower(dec.turretpower);
//        } else if ((dec.tpos < (dec.ttar - dec.ttol))) {
//            dec.turret.setPower(-dec.turretpower);
//        } else {
//            dec.turret.setPower(0);
//        }
//
//        dec.terror = Math.abs(dec.ttar - dec.tpos);
//    }
//
//    public void IntakeSort(boolean check) {
//        f2c = f2.getNormalizedColors();
//        r2c = r2.getNormalizedColors();
//        l2c = l2.getNormalizedColors();
//
//        dec.hsvF = new float[3];
//        Color.RGBToHSV((int) (f2c.red * 255), (int) (f2c.green * 255), (int) (f2c.blue * 255), dec.hsvF);
//        dec.hughf = dec.hsvF[0];
//        dec.brianf = dec.hsvF[2];
//
//        dec.hsvR = new float[3];
//        Color.RGBToHSV((int) (r2c.red * 255), (int) (r2c.green * 255), (int) (r2c.blue * 255), dec.hsvR);
//        dec.hughr = dec.hsvR[0];
//        dec.brianr = dec.hsvR[2];
//
//        dec.hsvL = new float[3];
//        Color.RGBToHSV((int) (l2c.red * 255), (int) (l2c.green * 255), (int) (l2c.blue * 255), dec.hsvL);
//        dec.hughl = dec.hsvL[0];
//        dec.brianl = dec.hsvL[2];
//
//        if ((f2.getDistance(DistanceUnit.CM) < 4)) { //(dec.brianf > 0) && (dec.hughf >= 120), 125
//            if ((f2c.green / f2c.red) > dec.gratio) {
//                dec.revf = "Green";
//            } else {
//                dec.revf = "Purple";
//            }
//        } else {
//            dec.revf = "None";
//        }
//
//        if ((dec.brianr > 0) && (dec.hughr > 125)) { //>= 120?, (r2.getDistance(DistanceUnit.CM) < 5)
//            if ((r2c.green / r2c.red) > dec.gratio) {
//                dec.revr = "Green";
//            } else {
//                dec.revr = "Purple";
//            }
//        } else {
//            dec.revr = "None";
//        }
//
//        if ((dec.brianl > 0) && (dec.hughl > 125)) { //>= 120?, (l2.getDistance(DistanceUnit.CM) < 4)
//            if ((l2c.green / l2c.red) > dec.gratio) {
//                dec.revl = "Green";
//            } else {
//                dec.revl = "Purple";
//            }
//        } else {
//            dec.revl = "None";
//        }
//
//        if (dec.canscan || check) {
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
//
//            Log.d("Sorting", "F, R, L: " + dec.FC + ", " + dec.RC + ", " + dec.LC);
//        }
//
//        dec.susanV = dec.susanenc.getVoltage();
//        dec.susandegreesat = (dec.susanV - dec.susanminV) / dec.susanVrange * dec.susanrange;
//
//        if ((dec.lastdiff >= dec.stol) && (Math.abs(dec.susandegreesat - dec.susandegreesto) < dec.stol)) {
//            dec.canscan = true;
//            dec.intakescan = runtime.milliseconds() + 100;
//        }
//
//        if (Math.abs(dec.susandegreesat - dec.susandegreesto) < dec.stol) { //2?
//            dec.canscan = true;
//            if (!dec.FC.matches("None")) {
//                if ((!dec.RC.matches("None")) && (!dec.LC.matches("None"))) {
//                    dec.canscan = true;
//                } else if (dec.intakescan < runtime.milliseconds()) {
//                    if (dec.susan.getPosition() < 0.2) {
//                        if (dec.LC.matches("None")) {
//                            dec.susan.setPosition(dec.susanpos2);
//                            dec.canscan = false;
//                        } else if (dec.RC.matches("None")) {
//                            dec.susan.setPosition(dec.susanpos3);
//                            dec.canscan = false;
//                        }
//                    } else if (dec.susan.getPosition() < 0.6) {
//                        if (dec.LC.matches("None")) {
//                            dec.susan.setPosition(dec.susanpos3);
//                            dec.canscan = false;
//                        } else if (dec.RC.matches("None")) {
//                            dec.susan.setPosition(dec.susanpos1);
//                            dec.canscan = false;
//                        }
//                    } else {
//                        if (dec.LC.matches("None")) {
//                            dec.susan.setPosition(dec.susanpos1);
//                            dec.canscan = false;
//                        } else if (dec.RC.matches("None")) {
//                            dec.susan.setPosition(dec.susanpos2);
//                            dec.canscan = false;
//                        }
//                    }
//                }
//            }
//        }
//
//        dec.susandegreesto = dec.susan.getPosition() * dec.susanrange;
//        dec.lastdiff = Math.abs(dec.susandegreesat - dec.susandegreesto);
//    }
//
//    public void FW(double dist, double x, boolean on) {
//        double distance;
//
//        if (on) {
//            distance = dist;
//
//            dec.hood.setPosition(dec.LC(distance, x, false));
//        } else {
//            distance = 21;
//
//            dec.hood.setPosition(dec.hoodmid);
//        }
//
//        dec.launch.setVelocity(dec.LC(distance, x, true));
//
//        dec.launch.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(dec.PIDFc.p,
//                dec.PIDFc.i, dec.PIDFc.d, dec.PIDFc.f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
//        ));
//
//        Log.d("FW", "Dist: " + dist);
//        Log.d("FW", "X: " + x);
//        Log.d("FW", "Target: " + (dec.LC(distance, x, true) / 28 * 60));
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
//}
