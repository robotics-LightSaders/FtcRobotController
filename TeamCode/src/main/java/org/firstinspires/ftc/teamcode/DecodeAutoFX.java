package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import android.util.Log;
import android.util.Size;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "AutoFX", group = "Linear Opmode")
@Disabled
public class DecodeAutoFX extends LinearOpMode {
    DecodeConstants dec = new DecodeConstants();

    RevColorSensorV3 f2;
    NormalizedRGBA f2c;
    RevColorSensorV3 r2;
    NormalizedRGBA r2c;
    RevColorSensorV3 l2;
    NormalizedRGBA l2c;

    private ElapsedTime runtime = new ElapsedTime();

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    private Position cameraPosition = new Position(DistanceUnit.INCH,
            0, 0, 7, 0); //6.2, 4.6 to start
    private YawPitchRollAngles cameraOrientation = new YawPitchRollAngles(AngleUnit.DEGREES,
            0, -90, 0, 0);

    private volatile boolean threadon = true;
    private Thread TurThr;

    double drivetimeout = 0;
    double wait = 0;
    double loopcount = 0;
    double checktime = 0;

    double lsp = 0;

    @Override
    public void runOpMode() {
        //region - Hardware Map
        dec.fl = hardwareMap.get(DcMotor.class, "fl");
        dec.fr = hardwareMap.get(DcMotor.class, "fr");
        dec.bl = hardwareMap.get(DcMotor.class, "bl");
        dec.br = hardwareMap.get(DcMotor.class, "br");

        dec.launch = hardwareMap.get(DcMotorEx.class, "launch");

        dec.intake = hardwareMap.get(DcMotor.class, "intake");
        dec.transfer = hardwareMap.get(DcMotor.class, "transfer");

        dec.hood = hardwareMap.get(Servo.class, "hood");
        dec.camrot = hardwareMap.get(Servo.class, "camrot");

        dec.intakedos = hardwareMap.get(CRServo.class, "intakedos");

        dec.susan = hardwareMap.get(Servo.class, "susan");
        dec.susanenc = hardwareMap.get(AnalogInput.class, "senc");

        dec.turret = hardwareMap.get(CRServo.class, "turret");
        dec.oq = hardwareMap.get(OctoQuadFWv3.class, "octoquad");

        dec.odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");

        dec.f0 = hardwareMap.digitalChannel.get("f0");
        dec.f1 = hardwareMap.digitalChannel.get("f1");
        dec.r0 = hardwareMap.digitalChannel.get("r0");
        dec.r1 = hardwareMap.digitalChannel.get("r1");
        dec.l0 = hardwareMap.digitalChannel.get("l0");
        dec.l1 = hardwareMap.digitalChannel.get("l1");

        f2 = hardwareMap.get(RevColorSensorV3.class, "f2");
        r2 = hardwareMap.get(RevColorSensorV3.class, "r2");
        l2 = hardwareMap.get(RevColorSensorV3.class, "l2");

        dec.fl.setDirection(DcMotor.Direction.REVERSE);
        dec.bl.setDirection(DcMotor.Direction.REVERSE);

        dec.transfer.setDirection(DcMotor.Direction.REVERSE);
        dec.intake.setDirection(DcMotor.Direction.REVERSE);

        dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        dec.bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        dec.br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        dec.bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        dec.br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        dec.initOQ();

        dec.turret.setPower(0);

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
                dec.PIDFf.p, dec.PIDFf.i, dec.PIDFf.d, dec.PIDFf.f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
        ));

        while (!gamepad1.a) {
            if (gamepad1.x) {
                dec.teamcolor = "Blue";
                dec.goal = dec.Bgoal;
                dec.turret.setPower(0);
            } else if (gamepad1.b) {
                dec.teamcolor = "Red";
                dec.goal = dec.Rgoal;
                dec.turret.setPower(0);
            }

            dec.tpos = dec.oq.readAllEncoderData().positions[1];

            telemetry.addData("Team Color: ", dec.teamcolor);
            telemetry.addData("Turret Target: ", dec.ttar);
            telemetry.addData("Turret At: ", dec.tpos);
            telemetry.update();

            if (dec.teamcolor.matches("Red")) {
                dec.ttar = dec.setTurretAngle(dec.Rstart, dec.goal, "Red");
            } else {
                dec.ttar = dec.setTurretAngle(dec.Bstart, dec.goal, "Blue");
            }

            if (Math.abs(dec.tpos - dec.ttar) > 100) {
                dec.turretpower = 1;
            } else if (Math.abs(dec.tpos - dec.ttar) > dec.ttol) {
                if (Math.abs(dec.tpos - dec.ttar) > 7) {
                    dec.turretpower = Range.clip((0.09 + (0.0000005 * Math.pow(Math.abs(dec.tpos - dec.ttar),
                            3))), 0, 1);
                } else {
                    dec.turretpower = 0.08;
                }
            } else {
                dec.turretpower = 0;
            }

            if (gamepad1.y) {
                if (dec.tpos > (dec.ttar + dec.ttol)) {
                    dec.turret.setPower(dec.turretpower);
                } else if (dec.tpos < (dec.ttar - dec.ttol)) {
                    dec.turret.setPower(-dec.turretpower);
                } else {
                    dec.turret.setPower(0);
                }
            } else {
                dec.turret.setPower(0);
            }
        }

        dec.fl.setPower(0);
        dec.fr.setPower(0);
        dec.bl.setPower(0);
        dec.br.setPower(0);

        dec.launch.setVelocity(0);

        dec.intake.setPower(0);
        dec.intakedos.setPower(0);
        dec.transfer.setPower(0);

        dec.susan.setPosition(dec.susanpos3);
        dec.susandegreesto = dec.susanpos3 * dec.susanrange;

        dec.hood.setPosition(0.38); //0.77
        dec.camrot.setPosition(dec.camlong);
        dec.turret.setPower(0);

        dec.configureOdo(true);
        dec.odo.setPosition(dec.odoorigin);
        //endregion

        navreset(true, true);

        if (dec.teamcolor.matches("Red")) {
            dec.odo.setPosition(dec.Rstart);
        } else {
            dec.odo.setPosition(dec.Bstart);
        }

        dec.odo.update();

        telemetry.addData("Odo x: ", dec.odo.getPosition().getX(DistanceUnit.INCH));
        telemetry.addData("Odo y: ", dec.odo.getPosition().getY(DistanceUnit.INCH));
        telemetry.addData("Odo z: ", dec.odo.getPosition().getHeading(AngleUnit.DEGREES));
        telemetry.addData("Status: ", "Ready");
        telemetry.update();

        waitForStart();

        TurThr = new Thread(() -> {
            while (threadon && opModeIsActive()) {  // Double-check with opMode active
                Turret(0, dec.teamcolor);

                dec.odo.update();

                dec.usePos.x = dec.odo.getPosition().getX(DistanceUnit.INCH);
                dec.usePos.y = dec.odo.getPosition().getY(DistanceUnit.INCH);
                dec.usePos.z = dec.odo.getPosition().getHeading(AngleUnit.DEGREES);

                if (dec.teamcolor.matches("Red")) {
                    FW(dec.goalDC(dec.usePos, dec.Rgoal), dec.odo.getPosition().getX(DistanceUnit.INCH), true);
                } else {
                    FW((dec.goalDC(dec.usePos, dec.Bgoal) + 4.5), dec.odo.getPosition().getX(DistanceUnit.INCH), true); //7, 5
                }

                try {
                    Thread.sleep(10);  // Check every Xms; 20
                } catch (InterruptedException e) {
                    // Ignore; we'll check flag next loop
                }
            }
            // Cleanup: Stop motor when thread exits
            dec.turret.setPower(0);
        });

        TurThr.start();

        while (opModeIsActive() && (loopcount < 1)) {
            runtime.reset();

            if (dec.teamcolor.matches("Red")) {
                dec.odo.setPosition(dec.Rstart);
                dec.goal = dec.Rgoal;
            } else {
                dec.odo.setPosition(dec.Bstart);
                dec.goal = dec.Bgoal;
            }

            if (dec.teamcolor.matches("Red")) {
                dec.odo.update();

                Cycle(dec.Rstart, 1000, 2, dec.teamcolor); //1100

                Cycle(dec.Rlaunch1f, 1400, 1, dec.teamcolor); //1600

                Cycle(dec.Rlaunch1f, 1300, 0, dec.teamcolor); //1400, 1350

                Cycle(dec.Rlaunch1f, 1400, 1.5, dec.teamcolor); //1

                Cycle(dec.Rlaunch1f, 1400, 1, dec.teamcolor); //0

                Cycle(dec.Rlaunch1f, 1400, 0, dec.teamcolor); //1

                if (runtime.milliseconds() < 28500) {
                    Launch(dec.Rlaunch1f, 1050); //850, 700, 800
                }

                Park(dec.RsabC1a);
            } else {
                dec.odo.update();

                Cycle(dec.Bstart, 1100, 2, dec.teamcolor);

                Cycle(dec.Blaunch1bf, 1450, 1, dec.teamcolor);

                Cycle(dec.Blaunch1bf, 1300, 0, dec.teamcolor); //1400

                Cycle(dec.Blaunch1bf, 1350, 1, dec.teamcolor); //1400

                Cycle(dec.Blaunch1bf, 1350, 1, dec.teamcolor); //1400 0

                Cycle(dec.Blaunch1bf, 1300, 0, dec.teamcolor); //1400 1

                if (runtime.milliseconds() < 27500) { //28000
                    Launch(dec.Blaunch1b, 1100); //1050
                }

                Park(dec.BsabC1);
            }

            loopcount = loopcount + 1;
        }

        threadon = false;
        if (TurThr != null && TurThr.isAlive()) {
            TurThr.interrupt();  // Break any sleep
            try {
                TurThr.join(100);  // Wait up to 100ms for clean exit
            } catch (InterruptedException e) {
                // Ignore; proceed to let OpMode end
            }
        }
    }

    //region - Drive
    public void navreset(boolean reset_opods, boolean recal_opods) {
        if (reset_opods) {
            dec.odo.resetPosAndIMU(); //resets the position to 0 and recalibrates the IMU
        }

        if (recal_opods) {
            dec.odo.recalibrateIMU(); //recalibrates the IMU without resetting position
        }
    }

    public void Drive(Pose2D target, double halo, double turnstart, boolean xx, boolean max) {
        dec.odo.update();

        Log.d("DriveCheck", "Current = " + dec.odo.getPosition().getX(DistanceUnit.INCH) + ", " +
                dec.odo.getPosition().getY(DistanceUnit.INCH) + ", " + dec.odo.getPosition().getHeading(AngleUnit.DEGREES));

        dec.yto = target.getY(DistanceUnit.INCH);
        dec.xto = target.getX(DistanceUnit.INCH);
        dec.zto = target.getHeading(AngleUnit.DEGREES);

        Log.d("DriveCheck", "Target = " + dec.xto + ", " + dec.yto + ", " + dec.zto);

        dec.yE = dec.yto - dec.odo.getPosition().getY(DistanceUnit.INCH);
        dec.xE = dec.xto - dec.odo.getPosition().getX(DistanceUnit.INCH);
        dec.zE = dec.zto - dec.odo.getPosition().getHeading(AngleUnit.DEGREES);

        double distance = Math.sqrt(Math.pow(dec.xE, 2) + Math.pow(dec.yE, 2));

        if (dec.zE > 180) {
            dec.zE = dec.zE - 360;
        } else if (dec.zE < -180) {
            dec.zE = dec.zE + 360;
        }

        Log.d("DriveCheck", "Error = " + dec.xE + ", " + dec.yE + ", " + dec.zE);

        double theta = (Math.atan(dec.yE / dec.xE)) * (180 / Math.PI);

        if (dec.xE < 0) {
            if (dec.yE > 0) {
                theta = theta + 180;
            } else {
                theta = theta - 180;
            }
        }

        double H = dec.odo.getPosition().getHeading(AngleUnit.DEGREES);

        Log.d("DriveCheck", "Theta = " + theta + ", Heading = " + H);

        if (Math.abs(dec.yE) > halo) {
            dec.yP = Range.clip((Math.abs(dec.yE) / halo), -1, 1);
        } else {
            dec.yP = Range.clip((Math.abs(dec.yE) / (2 * halo)), -1, 1);
        }

        if (Math.abs(dec.xE) > halo) {
            dec.xP = Range.clip((Math.abs(dec.xE) / halo), -1, 1);
        } else {
            dec.xP = Range.clip((Math.abs(dec.xE) / (2 * halo)), -1, 1);
        }

        if (distance < turnstart) {
            dec.zP = Range.clip((Math.abs(dec.zE) / 90), -0.6, 0.6); //og limit 0.5
        } else {
            dec.zP = 0;
        }

        dec.YPOWER = dec.yP * Math.sin(Math.toRadians(theta - H));
        dec.XPOWER = dec.xP * Math.cos(Math.toRadians(H - theta));

        if (dec.zE > 0) {
            dec.ZPOWER = dec.zP;
        } else {
            dec.ZPOWER = -dec.zP;
        }

        if (Math.abs(dec.YPOWER) < 0.05) {
            dec.YPOWER = 0;
        }

        if (Math.abs(dec.XPOWER) < 0.05) {
            dec.XPOWER = 0;
        }

        if (Math.abs(dec.ZPOWER) < 0.05) {
            dec.ZPOWER = 0;
        }

        if (xx) {
            dec.XPOWER = dec.XPOWER * 1.8;
        }

        Log.d("DriveCheck", "Dir Pwrs = " + dec.XPOWER + ", " + dec.YPOWER + ", " + dec.ZPOWER);

        if (!max) {
            dec.YPOWER = dec.YPOWER / 2.5;
            dec.ZPOWER = dec.ZPOWER * 1.5;
        }

        dec.flpower = Range.clip(dec.YPOWER + dec.XPOWER - dec.ZPOWER, -1, 1);
        dec.frpower = Range.clip(dec.YPOWER - dec.XPOWER + dec.ZPOWER, -1, 1);
        dec.blpower = Range.clip(dec.YPOWER - dec.XPOWER - dec.ZPOWER, -1, 1);
        dec.brpower = Range.clip(dec.YPOWER + dec.XPOWER + dec.ZPOWER, -1, 1);

        dec.fl.setPower(dec.flpower);
        dec.fr.setPower(dec.frpower);
        dec.bl.setPower(dec.blpower);
        dec.br.setPower(dec.brpower);

        Log.d("DriveCheck", "Motor Powers = FL: " + dec.flpower + ", FR: " + dec.frpower +
                ", BL: " + dec.blpower + ", BR: " + dec.brpower);
    }

    //region - Spline TODO
    public void Spline(Pose2D start, Pose2D p1, Pose2D p2, Pose2D end) {
        double p0x = start.getX(DistanceUnit.INCH);
        double p0y = start.getY(DistanceUnit.INCH);
        double p1x = p1.getX(DistanceUnit.INCH);
        double p1y = p1.getY(DistanceUnit.INCH);
        double p2x = p2.getX(DistanceUnit.INCH);
        double p2y = p2.getY(DistanceUnit.INCH);
        double p3x = end.getX(DistanceUnit.INCH);
        double p3y = end.getY(DistanceUnit.INCH);

        double t = 0; //placeholder
        double t0 = 0;
        double t1 = tnext(true, t0, start, p1);
        double t2 = tnext(true, t1, p1, p2);
        double t3 = tnext(true, t2, p2, end);

        double A1x = (p0x * ((t1 - t) / (t1 - t0))) + (p1x * ((t - t0) / (t3 - t2)));
        double A2x = (p1x * ((t2 - t) / (t2 - t1))) + (p2x * ((t - t1) / (t2 - t1)));
        double A3x = (p2x * ((t3 - t) / (t3 - t2))) + (p3x * ((t - t2) / (t3 - t2)));

        double B1x = (A1x * ((t2 - t) / (t2 - t0))) + (A2x * ((t - t0) / (t2 - t0)));
        double B2x = (A2x * ((t3 - t) / (t3 - t1))) + (A3x * ((t - t1) / (t3 - t1)));

        double Cx = (B1x * ((t2 - t) / (t2 - t1))) + (B2x * ((t - t1) / (t2 - t1)));

        double A1y = (p0y * ((t1 - t) / (t1 - t0))) + (p1y * ((t - t0) / (t3 - t2)));
        double A2y = (p1y * ((t2 - t) / (t2 - t1))) + (p2y * ((t - t1) / (t2 - t1)));
        double A3y = (p2y * ((t3 - t) / (t3 - t2))) + (p3y * ((t - t2) / (t3 - t2)));

        double B1y = (A1y * ((t2 - t) / (t2 - t0))) + (A2y * ((t - t0) / (t2 - t0)));
        double B2y = (A2y * ((t3 - t) / (t3 - t1))) + (A3y * ((t - t1) / (t3 - t1)));

        double Cy = (B1y * ((t2 - t) / (t2 - t1))) + (B2y * ((t - t1) / (t2 - t1)));
    }

    public double tnext(boolean xy, double tat, Pose2D at, Pose2D next) {
        double alpha = 0.5; //uniform = 0, centripetal = 0.5, chordal = 1
        double tdist;

        if (xy) {
            tdist = Math.sqrt(Math.pow((next.getX(DistanceUnit.INCH) - at.getX(DistanceUnit.INCH)), 2) +
                    Math.pow((next.getY(DistanceUnit.INCH) - at.getY(DistanceUnit.INCH)), 2));
        } else {
            tdist = Math.sqrt(Math.pow((next.getHeading(AngleUnit.DEGREES) - at.getHeading(AngleUnit.DEGREES)), 2));
        }

        return Math.pow(tdist, alpha) + tat;
    }

    public void tinc(double tnext, double tat, double stepcount) {
        double segment = tnext - tat;
        double step = segment / stepcount;
    }
    //endregion

    public void Pos(Pose2D poscheck) {
        Log.d("PositionCheck", "Target (x, y, z): " + poscheck.getX(DistanceUnit.INCH) + ", " +
                poscheck.getY(DistanceUnit.INCH) + ", " + poscheck.getHeading(AngleUnit.DEGREES));
        Log.d("PositionCheck", "Current (x, y, z): " + dec.odo.getPosition().getX(
                DistanceUnit.INCH) + ", " + dec.odo.getPosition().getY(DistanceUnit.INCH) + ", " +
                dec.odo.getPosition().getHeading(AngleUnit.DEGREES));
    }

    public void DriveStop() {
        dec.fl.setPower(0);
        dec.fr.setPower(0);
        dec.bl.setPower(0);
        dec.br.setPower(0);
    }

    public void DriveHold() {
        dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        dec.fl.setPower(-0.12);
        dec.fr.setPower(-0.1);
        dec.bl.setPower(0.1);
        dec.br.setPower(0.17);

        sleep(50);

        DriveStop();
    }

    public void Cycle(Pose2D launch, double LdriveT, double intakenum, String color) {
        Pose2D preintake;
        Pose2D intakepos;
        Pose2D post;
        Pose2D reintake;

        if (color.matches("Red")) {
            reintake = dec.RsabC3;
        } else {
            reintake = dec.BsabC3;
        }

        if (color.matches("Red")) {
            if ((intakenum > 0) && (intakenum < 2)) {
                if (intakenum == 1) {
                    preintake = dec.RsabC1; //CHANGE??
                    intakepos = dec.RsabC2x; //c2
                } else {
                    preintake = dec.RsabC15; //CHANGE??
                    intakepos = dec.RsabC25x; //c2
                }

                post = dec.RsabC1; //CHANGE??
            } else if (intakenum == 2) {
                preintake = dec.Rpreintake1x;
                intakepos = dec.Rintake1;
                post = dec.odoorigin;
            } else {
                preintake = dec.RFXcp; //RexfCa
                intakepos = dec.RFXc; //RexfCb
                post = dec.odoorigin;
            }
        } else {
            if (intakenum == 1) {
                preintake = dec.BsabC1;
                intakepos = dec.BsabC2;
                post = dec.BsabC1;
            } else if (intakenum == 2) {
                preintake = dec.Bpreintake1;
                intakepos = dec.Bintake1;
                post = dec.odoorigin;
            } else {
                preintake = dec.BFXcp; //BexfCa
                intakepos = dec.BFXc; //BexfCb
                post = dec.BFXcp; //dec.odoorigin
            }
        }

        Launch(launch, LdriveT);

        dec.intake.setPower(1);
        dec.susan.setPosition(dec.susanpos1);
        dec.susandegreesto = dec.susanpos1 * dec.susanrange;

        dec.lastdiff = 0;
        dec.canscan = false;
        dec.donescan = false;

        if (/*color.matches("Blue") &&*/ (intakenum != 2)) {
            drivetimeout = runtime.milliseconds() + 400; //1000, 800, 600, 500
        } else {
            drivetimeout = runtime.milliseconds() + 900; //1200, 800, 1000
        }
        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(preintake.getX(DistanceUnit.INCH) -
                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(preintake.getY(DistanceUnit.INCH)) -
                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(preintake.getHeading(AngleUnit.DEGREES) -
                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
            Drive(preintake, 1, 200, true, true); //7 //CHANGED
            IntakeSort(false);
        }

        dec.susan.setPosition(dec.susanpos1);
        dec.susandegreesto = dec.susanpos1 * dec.susanrange;
        dec.transfer.setPower(0);

        dec.lastdiff = 0;
        dec.canscan = false;
        dec.donescan = false;

        DriveHold();
        Pos(preintake);

        if (intakenum == 2) {
            drivetimeout = runtime.milliseconds() + 1200; //1600, 1200, 1100
        } else if (intakenum == 0) {
            if (color.matches("Red")) {
                drivetimeout = runtime.milliseconds() + 400; //2000, 800, 600
            } else {
                drivetimeout = runtime.milliseconds() + 400; //800, 400, 450
            }
        } else {
            if (color.matches("Red")) {
                drivetimeout = runtime.milliseconds() + 400; //900
            } else {
                drivetimeout = runtime.milliseconds() + 400; //400
            }
        }
        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(intakepos.getX(DistanceUnit.INCH) -
                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(intakepos.getY(DistanceUnit.INCH)) -
                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(intakepos.getHeading(AngleUnit.DEGREES) -
                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
            if (post == dec.odoorigin) {
                if (intakenum == 0) {
                    Drive(intakepos, 5, 15, true, true); //200 false false
                } else {
                    Drive(intakepos, 2, 200, false, false); //5
                }
            } else {
                Drive(intakepos, 5, 200, false, true);
            }

            IntakeSort(false);
        }

        DriveStop();
        Pos(intakepos);

        if (post != dec.odoorigin) {
            dec.lastdiff = 0;
            dec.canscan = false;
            dec.donescan = false;
            dec.susandegreesto = dec.susan.getPosition();
            drivetimeout = runtime.milliseconds() + 200; //800, 400, 300
            while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(preintake.getX(DistanceUnit.INCH) -
                    dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(preintake.getY(DistanceUnit.INCH)) -
                    dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(preintake.getHeading(AngleUnit.DEGREES) -
                    dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
                Drive(preintake, 1, 200, false, true);
                IntakeSort(false);
            }

            DriveHold();
            Pos(preintake);

            dec.lastdiff = 0;
            dec.canscan = false;
            dec.donescan = false;
            dec.susandegreesto = dec.susan.getPosition();
            drivetimeout = runtime.milliseconds() + 300; //800, 400
            while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((((Math.abs(reintake.getX(DistanceUnit.INCH) -
                    dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25)) || (Math.abs(reintake.getY(DistanceUnit.INCH)) -
                    dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(reintake.getHeading(AngleUnit.DEGREES) -
                    dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
                Drive(reintake, 5, 200, false, true);
                IntakeSort(false);
            }

            DriveStop();
            Pos(reintake);
        }
    }

    public void Park(Pose2D end) {
        drivetimeout = runtime.milliseconds() + 400; //300, 500
        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && ((Math.abs(end.getX(DistanceUnit.INCH) -
                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(end.getY(DistanceUnit.INCH) -
                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(end.getHeading(AngleUnit.DEGREES) -
                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
            Drive(end, 30, 200, false, true); //15  5
        }

        DriveStop();
        Pos(end);

        dec.intake.setPower(0);
        dec.transfer.setPower(0);
        dec.susan.setPosition(dec.susanpos1);
        dec.susandegreesto = dec.susanpos1 * dec.susanrange;
        dec.odo.update();
    }
    //endregion

    public void Launch(Pose2D launchpos, double time) {
        dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        dec.susan.setPosition(dec.susanpos3); //pid3?
        dec.susandegreesto = dec.susanpos3 * dec.susanrange;

        dec.lastdiff = 0;
        dec.canscan = false;
        dec.donescan = false;

        drivetimeout = runtime.milliseconds() + time;
        while ((runtime.milliseconds() < drivetimeout) && opModeIsActive() && (((Math.abs(launchpos.getX(DistanceUnit.INCH) -
                dec.odo.getPosition().getX(DistanceUnit.INCH)) > 0.25) || (Math.abs(launchpos.getY(DistanceUnit.INCH) -
                dec.odo.getPosition().getY(DistanceUnit.INCH)) > 0.25) || (Math.abs(launchpos.getHeading(AngleUnit.DEGREES) -
                dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25)) || (dec.launch.getVelocity() <
                (dec.launchCalc(dec.GD(dec.odo.getPosition(), dec.goal), true))))) {
            Drive(launchpos, 14, 200, true, true); //10 changed halo from 7
            IntakeSort(false);
        }

        DriveHold();
        Pos(launchpos);

        if (dec.susan.getPosition() < 0.7) { //0.8
            lsp = dec.susan.getPosition();
            dec.susan.setPosition(dec.susanpos3); //pid3?
            dec.susandegreesto = dec.susanpos3 * dec.susanrange;

            if (lsp > 0.3) {
                sleep(200);
            } else {
                sleep(400);
            }
        } else {
            dec.susan.setPosition(dec.susanpos3); //pid3?
            dec.susandegreesto = dec.susanpos3 * dec.susanrange;
        }

        dec.intake.setPower(-0.5);
        dec.transfer.setPower(1);

        sleep(200); //400

        if ((Math.abs(dec.launch.getVelocity() - dec.LC(dec.goalDC(dec.usePos, dec.goal), dec.odo.getPosition().
                getX(DistanceUnit.INCH), true)) < 100) || (Math.abs(dec.turret.getPower()) < 0.14)) { //&&?
            wait = wait + 1;

            Log.d("FW", "FWE: " + Math.abs(dec.launch.getVelocity() - dec.LC(dec.goalDC(dec.usePos,
                    dec.goal), dec.odo.getPosition().getX(DistanceUnit.INCH), true)));
        }

        Log.d("FW", "launch");

        dec.susan.setPosition(dec.susanlaunch2); //2
        dec.susandegreesto = dec.susanlaunch2 * dec.susanrange;

        sleep(400);

        dec.susan.setPosition(dec.susanlaunch1); //2
        dec.susandegreesto = dec.susanlaunch1 * dec.susanrange;
        dec.intake.setPower(1);

        dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        sleep(400); //800
    }

    public void Turret(double target, String color) {
        dec.tpos = dec.oq.readAllEncoderData().positions[1];

        if (target != 0) {
            dec.ttar = target;
        } else {
            if (Math.abs(dec.setTurretAngle(dec.odo.getPosition(), dec.goal, dec.teamcolor) - dec.tpos) > dec.ttol) {
                dec.ttar = dec.setTurretAngle(dec.odo.getPosition(), dec.goal, dec.teamcolor);
            } else {
                dec.ttar = dec.tpos;
            }
        }

        if (runtime.milliseconds() < 26000) { //check todo
            if (color.matches("Red")) {
                dec.ttar = dec.ttar + 4; //8, 10, 5 todo
            } else {
                dec.ttar = dec.ttar + 3; //7, 5, 4
            }
        } else {
            dec.ttar = dec.ttar + 5; //8, 6
        }

        if (Math.abs(dec.tpos - dec.ttar) > 100) {
            dec.turretpower = 1;
        } else if (Math.abs(dec.tpos - dec.ttar) > dec.ttol) {
            if (Math.abs(dec.tpos - dec.ttar) > 7) {
                if (color.matches("Red")) {
                    dec.turretpower = Range.clip((0.12 + (0.0000005 * Math.pow(Math.abs(dec.tpos - dec.ttar),
                            3))), 0, 1); //0.09, 0.12, 0.11
                } else {
                    dec.turretpower = Range.clip((0.1 + (0.0000005 * Math.pow(Math.abs(dec.tpos - dec.ttar),
                            3))), 0, 1); //0.11
                }
            } else {
                dec.turretpower = 0.09; //0.08
            }
        } else {
            dec.turretpower = 0;
        }

        if ((dec.terror < Math.abs(dec.ttar - dec.tpos)) && (Math.abs(dec.ttar - dec.tpos) > 30)) {
            dec.turretpower = Range.clip((dec.turretpower - (0.04 * (dec.terror - Math.abs(dec.ttar -
                    dec.tpos)))), -1, 1);
        }

        if ((dec.tpos > (dec.ttar + dec.ttol))) {
            dec.turret.setPower(dec.turretpower);
        } else if ((dec.tpos < (dec.ttar - dec.ttol))) {
            dec.turret.setPower(-dec.turretpower);
        } else {
            dec.turret.setPower(0);
        }

        dec.terror = Math.abs(dec.ttar - dec.tpos);
    }

    public void IntakeSort(boolean check) {
        f2c = f2.getNormalizedColors();
        r2c = r2.getNormalizedColors();
        l2c = l2.getNormalizedColors();

        dec.hsvF = new float[3];
        Color.RGBToHSV((int) (f2c.red * 255), (int) (f2c.green * 255), (int) (f2c.blue * 255), dec.hsvF);
        dec.hughf = dec.hsvF[0];
        dec.brianf = dec.hsvF[2];

        dec.hsvR = new float[3];
        Color.RGBToHSV((int) (r2c.red * 255), (int) (r2c.green * 255), (int) (r2c.blue * 255), dec.hsvR);
        dec.hughr = dec.hsvR[0];
        dec.brianr = dec.hsvR[2];

        dec.hsvL = new float[3];
        Color.RGBToHSV((int) (l2c.red * 255), (int) (l2c.green * 255), (int) (l2c.blue * 255), dec.hsvL);
        dec.hughl = dec.hsvL[0];
        dec.brianl = dec.hsvL[2];

        if ((f2.getDistance(DistanceUnit.CM) < 4)) { //(dec.brianf > 0) && (dec.hughf >= 120), 125
            if ((f2c.green / f2c.red) > dec.gratio) {
                dec.revf = "Green";
            } else {
                dec.revf = "Purple";
            }
        } else {
            dec.revf = "None";
        }

        if ((dec.brianr > 0) && (dec.hughr > 125)) { //>= 120?, (r2.getDistance(DistanceUnit.CM) < 5)
            if ((r2c.green / r2c.red) > dec.gratio) {
                dec.revr = "Green";
            } else {
                dec.revr = "Purple";
            }
        } else {
            dec.revr = "None";
        }

        if ((dec.brianl > 0) && (dec.hughl > 125)) { //>= 120?, (l2.getDistance(DistanceUnit.CM) < 4)
            if ((l2c.green / l2c.red) > dec.gratio) {
                dec.revl = "Green";
            } else {
                dec.revl = "Purple";
            }
        } else {
            dec.revl = "None";
        }

        if (dec.canscan || check) {
            if (dec.f0.getState() || dec.revf.matches("Purple")) {
                dec.FC = "Purple";
            } else if (dec.f1.getState() || dec.revf.matches("Green")) {
                dec.FC = "Green";
            } else {
                dec.FC = "None";
            }

            if (dec.r0.getState() || dec.revr.matches("Purple")) {
                dec.RC = "Purple";
            } else if (dec.r1.getState() || dec.revr.matches("Green")) {
                dec.RC = "Green";
            } else {
                dec.RC = "None";
            }

            if (dec.l0.getState() || dec.revl.matches("Purple")) {
                dec.LC = "Purple";
            } else if (dec.l1.getState() || dec.revl.matches("Green")) {
                dec.LC = "Green";
            } else {
                dec.LC = "None";
            }

            Log.d("Sorting", "F, R, L: " + dec.FC + ", " + dec.RC + ", " + dec.LC);
            dec.donescan = true;
        } else {
            dec.donescan = false;
        }

        dec.susanV = dec.susanenc.getVoltage();
        dec.susandegreesat = (dec.susanV - dec.susanminV) / dec.susanVrange * dec.susanrange;

        if ((dec.lastdiff >= dec.stol) && (Math.abs(dec.susandegreesat - dec.susandegreesto) < dec.stol)) {
            dec.canscan = true;
            //dec.intakescan = runtime.milliseconds() + 50; //100
        }

        Log.d("Susan", "Susan degrees diff: " + Math.abs(dec.susandegreesat - dec.susandegreesto));

        if (Math.abs(dec.susandegreesat - dec.susandegreesto) < 30) { //2, stol
            dec.canscan = true;
            if ((!dec.FC.matches("None")) && dec.donescan) {
                if ((!dec.RC.matches("None")) && (!dec.LC.matches("None"))) {
                    dec.canscan = true;
                } else if (dec.intakescan < runtime.milliseconds()) {
                    if (dec.susan.getPosition() < 0.32) { //0.2
                        if (dec.LC.matches("None")) {
                            dec.susan.setPosition(dec.susanpos2);
                            dec.susandegreesto = dec.susanpos2 * dec.susanrange;
                            dec.canscan = false;
                            dec.donescan = false;
                        } else if (dec.RC.matches("None")) {
                            dec.susan.setPosition(dec.susanpos3);
                            dec.susandegreesto = dec.susanpos3 * dec.susanrange;
                            dec.canscan = false;
                            dec.donescan = false;
                        }
                    } else if (dec.susan.getPosition() < 0.7) { //0.6
                        if (dec.LC.matches("None")) {
                            dec.susan.setPosition(dec.susanpos3);
                            dec.susandegreesto = dec.susanpos3 * dec.susanrange;
                            dec.canscan = false;
                            dec.donescan = false;
                        } else if (dec.RC.matches("None")) {
                            dec.susan.setPosition(dec.susanpos1);
                            dec.susandegreesto = dec.susanpos1 * dec.susanrange;
                            dec.canscan = false;
                            dec.donescan = false;
                        }
                    } else {
                        if (dec.LC.matches("None")) {
                            dec.susan.setPosition(dec.susanpos1);
                            dec.susandegreesto = dec.susanpos1 * dec.susanrange;
                            dec.canscan = false;
                            dec.donescan = false;
                        } else if (dec.RC.matches("None")) {
                            dec.susan.setPosition(dec.susanpos2);
                            dec.susandegreesto = dec.susanpos2 * dec.susanrange;
                            dec.canscan = false;
                            dec.donescan = false;
                        }
                    }

                    dec.intakescan = runtime.milliseconds() + 350; //100, 250
                }
            }
        } else if (dec.intakescan < runtime.milliseconds()) {
            dec.canscan = false;
            dec.donescan = false;
            dec.susanat = (dec.susanV - dec.susanminV) / dec.susanVrange;
            dec.intakescan = runtime.milliseconds() + 100; //100

            Log.d("Susan", "Susanat: " + dec.susanat);

            if (dec.susanat < 0.4) { //0.32
                dec.susan.setPosition(dec.susanpos1);
                dec.susandegreesto = dec.susanpos1 * dec.susanrange;
            } else if (dec.susanat < 0.78) { //0.7
                dec.susan.setPosition(dec.susanpos2);
                dec.susandegreesto = dec.susanpos2 * dec.susanrange;
            } else {
                dec.susan.setPosition(dec.susanpos3);
                dec.susandegreesto = dec.susanpos3 * dec.susanrange;
            }
        }

        //dec.susandegreesto = dec.susan.getPosition() * dec.susanrange;
        dec.lastdiff = Math.abs(dec.susandegreesat - dec.susandegreesto);
    }

    public void FW(double dist, double x, boolean on) {
        double distance;

        if (on) {
            distance = dist - 27;

            dec.hood.setPosition(dec.LC(distance, x, false));
        } else {
            distance = 21;

            dec.hood.setPosition(dec.hoodmid);
        }

        dec.launch.setVelocity(dec.LC(distance, x, true));

        //only close PID, use to be far if x > 20 with old PID, new lobbier far shooting = close PID?
        /*if (x > 20) {
            dec.launch.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(dec.PIDFf.p,
                    dec.PIDFf.i, dec.PIDFf.d, dec.PIDFf.f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
            ));
        } else {*/
            dec.launch.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(dec.PIDFf.p,
                    dec.PIDFf.i, dec.PIDFf.d, dec.PIDFf.f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
            ));
        //}

        Log.d("FW", "Dist: " + dist);
        Log.d("FW", "X: " + x);
        Log.d("FW", "Target: " + (dec.LC(distance, x, true) / 28 * 60));
        Log.d("FW", "Actual: " + (dec.launch.getVelocity() / 28 * 60));
    }
}
