package org.firstinspires.ftc.teamcode;

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
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@Autonomous(name = "SummerDrive", group = "Linear Opmode")
@Disabled
public class DecodeSummerDrive extends LinearOpMode {
    DecodeConstants dec = new DecodeConstants();

    private ElapsedTime runtime = new ElapsedTime();

    double drivetimeout = 0;
    double targetX = 0;
    double targetY = 0;
    double targetZ = 0;
    Pose2D target = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);

    @Override
    public void runOpMode() {
        //region - Hardware Map
        dec.fl = hardwareMap.get(DcMotor.class, "fl");
        dec.fr = hardwareMap.get(DcMotor.class, "fr");
        dec.bl = hardwareMap.get(DcMotor.class, "bl");
        dec.br = hardwareMap.get(DcMotor.class, "br");

        dec.odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");

        dec.fl.setDirection(DcMotor.Direction.REVERSE);
        dec.bl.setDirection(DcMotor.Direction.REVERSE);

        dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        dec.bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        dec.br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        dec.bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        dec.br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        dec.fl.setPower(0);
        dec.fr.setPower(0);
        dec.bl.setPower(0);
        dec.br.setPower(0);

        dec.configureOdo(true);
        dec.odo.setPosition(dec.odoorigin);
        //endregion

        navreset(true, true);
        dec.odo.update();

        telemetry.addData("Odo x: ", dec.odo.getPosition().getX(DistanceUnit.INCH));
        telemetry.addData("Odo y: ", dec.odo.getPosition().getY(DistanceUnit.INCH));
        telemetry.addData("Odo z: ", dec.odo.getPosition().getHeading(AngleUnit.DEGREES));
        telemetry.addData("Status: ", "Ready");
        telemetry.update();

        waitForStart();

        runtime.reset();
        while (opModeIsActive()) {
            if (gamepad1.a && ((((Math.abs(target.getX(DistanceUnit.INCH) - dec.odo.getPosition().getX(DistanceUnit.INCH)) >
                    0.25)) || (Math.abs(target.getY(DistanceUnit.INCH)) - dec.odo.getPosition().getY(DistanceUnit.INCH)) >
                    0.25) || (Math.abs(target.getHeading(AngleUnit.DEGREES) - dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 0.25))) {
                Drive(target, 7, 200);
            } else {
                DriveStop();
            }


            Pos(target);

            //Y axis
            if (gamepad1.dpadDownWasPressed()) {
                targetY = targetY - 5;
            }
            if (gamepad1.dpadUpWasPressed()) {
                targetY = targetY + 5;
            }
            //X axis
            if (gamepad1.dpadLeftWasPressed()) {
                targetX = targetX - 5;
            }
            if (gamepad1.dpadRightWasPressed()) {
                targetX = targetX + 5;
            }
            //Heaidng
            if (gamepad1.rightBumperWasPressed()) {
                targetZ = targetZ - 15;
            }
            if (gamepad1.leftBumperWasPressed()) {
                targetZ = targetZ + 15;
            }
            target = new Pose2D(DistanceUnit.INCH, targetX, targetY, AngleUnit.DEGREES, targetZ);
            telemetry.addData("Target X, Y, Z ", target.getX(DistanceUnit.INCH) + ", " + target.getY(
                    DistanceUnit.INCH) + ", " + target.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Robot Pos x, y, z",dec.odo.getPosition().getX(DistanceUnit.INCH)+", "+dec.odo.getPosition().getY(DistanceUnit.INCH)+", "+dec.odo.getPosition().getHeading(AngleUnit.DEGREES));
            telemetry.update();

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

    public void Drive(Pose2D target, double halo, double turnstart) {
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
            dec.zP = Range.clip((Math.abs(dec.zE) / 90), -0.6, 0.6);
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

        Log.d("DriveCheck", "Dir Pwrs = " + dec.XPOWER + ", " + dec.YPOWER + ", " + dec.ZPOWER);

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
        dec.odo.update();

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
    //endregion
}
