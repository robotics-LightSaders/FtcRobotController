package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;


@Autonomous(name = "Spline Test", group = "Linear Opmode")
@Disabled
public class SplineTest extends LinearOpMode {
    DecodeConstants dec = new DecodeConstants();

    private ElapsedTime runtime = new ElapsedTime();



    double halo = 0;
    double u = 0;
    double uIncrement = .01;
    double splinePower = 1;
    double angleDif = 60;
    //TODO
    double lastVelo = 0;
    double lastTimeVelo = 0;
    Pose2D lastPoseVelo = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);
    double maxVelo = 88;
    double splineSectionLength1 =29.801132849164418;
    double splineSectionLength2 =22.013084407042925;
    double splineSectionLength3 =30.037063946840842;
    double currentSplineSectionLength = 0.0000000000000000000001;
    double tracingOffset;
    double maxDeltaR=0;

    double uSplineConversion = 0;
double deltaDistRobot = 0;
double newTracingDist = 5;
double oldTracingDist = 0;
    // In inches
    double uMin = .005;
    double uMax = .025;
    double uMinSlow = .005;
    double uMaxSlow = .025;
    double splinePowTop = 220;//210
    double splinePoweBot = 160;//150

    int splinePhase = 0;

    Pose2D splineOutput;
    Pose2D startPoint = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);
    Pose2D waypoint1 = new Pose2D(DistanceUnit.INCH, 12, 27, AngleUnit.DEGREES, 0);
    Pose2D waypoint2 = new Pose2D(DistanceUnit.INCH, 28, 13, AngleUnit.DEGREES, 0);
    Pose2D endPoint = new Pose2D(DistanceUnit.INCH, 40, 40, AngleUnit.DEGREES, 0);

    double FLP = 0;
    double FRP = 0;
    double BRP = 0;
    double BLP = 0;
    double minPower = 0.17;
    double shutoffPower = 0.05;
    //TODO
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

        SplineSequence(5500, startPoint,waypoint1,waypoint2,endPoint);
        sleep(2000);
        SplineSequence(5500, endPoint,waypoint2,waypoint1,startPoint);

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

    public void Drive(Pose2D target, double halo, double turnstart, double powerMod) {
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

        //region - faster?
        /*if (Math.abs(yE) > halo) {
            yP = yE / Math.abs(yE);
        } else {
            yP = yE / halo;
        }

        if (Math.abs(xE) > halo) {
            xP = xE / Math.abs(xE);
        } else {
            xP = xE / halo;
        }

        if (Math.abs(zE) > 20) {
            zP = zE / Math.abs(zE);
        } else {
            zP = zE / 20;
        }*/
        //endregion

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
            dec.zP = Range.clip((Math.abs(dec.zE) / 60), -1, 1); //0.6 //90, 1
        } else {
            dec.zP = 0;
        }

        dec.YPOWER = dec.yP * Math.sin(Math.toRadians(theta - H)) * 1.05;
        dec.XPOWER = dec.xP * Math.cos(Math.toRadians(H - theta)) * 1.2; //1.1

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

        FLP = powerMod * dec.flpower;
        FRP = powerMod * dec.frpower;
        BRP = powerMod * dec.brpower;
        BLP = powerMod * dec.blpower;

        if ((Math.abs(FLP)>shutoffPower)&&(Math.abs(FLP)<minPower)){
            if (FLP>0){
                FLP = minPower;
            }else{
                FLP=-minPower;
            }
        }else if(Math.abs(FLP) < shutoffPower){
            FLP=0;
        }
        if ((Math.abs(FRP)>shutoffPower)&&(Math.abs(FRP)<minPower)){
            if (FRP>0){
                FRP = minPower;
            }else{
                FRP=-minPower;
            }
        }else if(Math.abs(FRP) < shutoffPower){
            FRP=0;
        }
        if ((Math.abs(BRP)>shutoffPower)&&(Math.abs(BRP)<minPower)){
            if (BRP>0){
                BRP = minPower;
            }else{
                BRP=-minPower;
            }
        }else if(Math.abs(BRP) < shutoffPower){
            BRP=0;
        }
        if ((Math.abs(BLP)>shutoffPower)&&(Math.abs(BLP)<minPower)){
            if (BLP>0){
                BLP = minPower;
            }else{
                BLP=-minPower;
            }
        }else if(Math.abs(BLP) < shutoffPower){
            BLP=0;
        }

        dec.fl.setPower(FLP);
        dec.fr.setPower(FRP);
        dec.bl.setPower(BLP);
        dec.br.setPower(BRP);

        Log.d("DriveCheck", "Mod Pwrs = FL: " + FLP + ", FR: " + FRP +
                ", BL: " + BLP + ", BR: " + BRP);
        Log.d("DriveCheck", "Motor Powers = FL: " + dec.flpower + ", FR: " + dec.frpower +
                ", BL: " + dec.blpower + ", BR: " + dec.brpower);
    }

    //region - Spline TODO

    public void SplineDrive() {
        while (u != 1 && ((((Math.abs((splineOutput).getX(DistanceUnit.INCH) - dec.odo.getPosition().getX(DistanceUnit.INCH)) >
                1)) || (Math.abs(splineOutput.getY(DistanceUnit.INCH)) - dec.odo.getPosition().getY(DistanceUnit.INCH)) >
                1) || (Math.abs(splineOutput.getHeading(AngleUnit.DEGREES) - dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 5))) {
            //SPline Math
            //     Drive(splineOutput,0,200);
            u = u + .01;

        }

    }

    public void SplineSequence(double drivetimeout, Pose2D point1, Pose2D point2, Pose2D point3, Pose2D point4) {
double splineStartTime = runtime.milliseconds();
        Log.d("Checkpoint","CHECKPOINT");

        //while !end
        u = 0;
        splinePhase = 0;
        uIncrement = 0.01;
        while ((runtime.milliseconds() < (splineStartTime + drivetimeout)) && opModeIsActive() /*&& (u < 1)*/ && (((Math.abs((point4).getX(DistanceUnit.INCH) - dec.odo.getPosition().getX(DistanceUnit.INCH)) >
                0.25) || (Math.abs(point4.getY(DistanceUnit.INCH)) - dec.odo.getPosition().getY(DistanceUnit.INCH) >
                0.25)) || (Math.abs(point4.getHeading(AngleUnit.DEGREES) - dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) > 1))) {
//changed tolerance from .25 to .1
            if (splinePhase == 0) {
                //  SplineMath(startPoint, waypoint1, waypoint2, endPoint, u, splinePhase);
                u = u + uIncrement;
                halo = 0;
                if (u >= 1) {
                    splinePhase = 1;
                    u = 0;
                }
            } else if (splinePhase == 1) {
                // SplineMath(startPoint, waypoint1, waypoint2, endPoint, u, splinePhase);
                u = u + uIncrement;
                halo = 0;
                if (u >= 1) {
                    splinePhase = 2;
                    u = 0;
                }
                //1st part
            } else if (splinePhase == 2) {
                //  SplineMath(startPoint, waypoint1, waypoint2, endPoint, u, splinePhase);
                u = u + uIncrement;
                //TODO
               // halo = (7 * u);
                halo = 2;
                //changed halo from u to 3
                if (u >= 1) {
                    splinePhase = 3;
                    u = 1;
                }
            }

            SplineMath(point1, point2, point3, point4, u, splinePhase);
            Drive(splineOutput, halo, 200, splinePower);
            Log.d("Spline", "U " + u);
            Log.d("Spline", "spline phase: " + splinePhase);
            Log.d("Spline", "runtime: " + runtime.milliseconds());
            Log.d("Spline", "start time +drive timeout: " + (splineStartTime+drivetimeout));
            Log.d("TracingDist","TracingDistError"+Math.hypot(dec.odo.getPosition().getX(DistanceUnit.INCH)-splineOutput.getX(DistanceUnit.INCH),
                    dec.odo.getPosition().getY(DistanceUnit.INCH)-splineOutput.getY(DistanceUnit.INCH) ));
if (splinePhase==0){
    currentSplineSectionLength = splineSectionLength1;
} else if (splinePhase==1) {
    currentSplineSectionLength = splineSectionLength2;

} else if (splinePhase==2) {
    currentSplineSectionLength = splineSectionLength3;
}
        }

        DriveStop();
        Pos(point4);
        Log.d("Checkpoint", "final checkpoint");
        Log.d("Spline", " final U " + u);
        Log.d("Spline", "final spline phase: " + splinePhase);
        Log.d("Spline", " finalruntime: " + runtime.milliseconds());
        Log.d("Spline", " finalstart time +drive timeout: " + (splineStartTime+drivetimeout));

    }
    //1st part-if v=0, do spline math for first third
    //{u incrememts, if u is 1, v = 1 and goes to 2nd part

    //2nd part else if v=1, do spline math for second part, increment u, if u = 1, v =2

    //3rd part if v=2, do spline math for last part, same as above.


    //power calculator 220-|ma-mp|/160 then multiply to drive


    //drive


    public void SplineMath(Pose2D start, Pose2D p1, Pose2D p2, Pose2D end, double U, int phase) {

        double q0x = start.getX(DistanceUnit.INCH);
        double q0y = start.getY(DistanceUnit.INCH);
        double q0h = start.getHeading(AngleUnit.DEGREES);
        double q1x = p1.getX(DistanceUnit.INCH);
        double q1y = p1.getY(DistanceUnit.INCH);
        double q1h = p1.getHeading(AngleUnit.DEGREES);
        double q2x = p2.getX(DistanceUnit.INCH);
        double q2y = p2.getY(DistanceUnit.INCH);
        double q2h = p2.getHeading(AngleUnit.DEGREES);
        double q3x = end.getX(DistanceUnit.INCH);
        double q3y = end.getY(DistanceUnit.INCH);
        double q3h = end.getHeading(AngleUnit.DEGREES);


        double m1 = Math.toDegrees(Math.atan((q1y - q0y) / (q1x - q0x)));
        double m2 = Math.toDegrees(Math.atan((q2y - q1y) / (q2x - q1x)));
        double m3 = Math.toDegrees(Math.atan((q3y - q2y) / (q3x - q2x)));
        double mp = (1.5 * Math.toRadians(m2)) - (.5 * Math.toRadians(m1)); //1.2 & 0.2
        double ma = (1.5 * Math.toRadians(m3)) - (.5 * Math.toRadians(m2));
        double mi;
        double mo;
        double d01 = Math.sqrt(Math.pow((q1x - q0x), 2) + Math.pow((q1y - q0y), 2));
        double d23 = Math.sqrt(Math.pow((q3x - q2x), 2) + Math.pow((q3y - q2y), 2));

        double qpx = q0x + d01 * Math.cos(mp);
        double qpy = q0y + d01 * Math.sin(mp);
        double qph = q0h + Math.pow(Math.E, -10);
        double qax = q3x + d23 * Math.cos(ma);
        double qay = q3y + d23 * Math.sin(ma);
        double qah = q3h + Math.pow(Math.E, -10);

        double p0x;
        double p0y;
        double p0h;
        double p1x;
        double p1y;
        double p1h;
        double p2x;
        double p2y;
        double p2h;
        double p3x;
        double p3y;
        double p3h;

        if (phase == 0) {
            p0x = qpx;
            p0y = qpy;
            p0h = qph;
            p1x = q0x;
            p1y = q0y;
            p1h = q0h;
            p2x = q1x;
            p2y = q1y;
            p2h = q1h;
            p3x = q2x;
            p3y = q2y;
            p3h = q2h;
            mi = m1;
            mo = m2;
        } else if (phase == 1) {
            p0x = q0x;
            p0y = q0y;
            p0h = q0h;
            p1x = q1x;
            p1y = q1y;
            p1h = q1h;
            p2x = q2x;
            p2y = q2y;
            p2h = q2h;
            p3x = q3x;
            p3y = q3y;
            p3h = q3h;
            if (U <= .5) {
                mi = m1;
                mo = m2;
            } else {
                mi = m2;
                mo = m3;
            }
        } else {
            p0x = q1x;
            p0y = q1y;
            p0h = q1h;
            p1x = q2x;
            p1y = q2y;
            p1h = q2h;
            p2x = q3x;
            p2y = q3y;
            p2h = q3h;
            p3x = qax;
            p3y = qay;
            p3h = qah;
            mi = m2;
            mo = m3;
        }

        double t0 = 0;
        double t1 = tnext(true, t0, start, p1);
        double t2 = tnext(true, t1, p1, p2);
        double t3 = tnext(true, t2, p2, end);

        double t1h = tnext(false, t0, start, p1);
        double t2h = tnext(false, t1, p1, p2);
        double t3h = tnext(false, t2, p2, end);

        double t = t1 + (U * (t2 - t1));
        double th = t1h + (U * (t2h - t1h));

        double A1x = (p0x * ((t1 - t) / ((t1 - t0) + Math.pow(Math.E, -10)))) + (p1x * ((t - t0) / ((t1 - t0) + Math.pow(Math.E, -10))));
        double A2x = (p1x * ((t2 - t) / ((t2 - t1) + Math.pow(Math.E, -10))) + (p2x * ((t - t1) / ((t2 - t1) + Math.pow(Math.E, -10)))));
        double A3x = (p2x * ((t3 - t) / ((t3 - t2) + Math.pow(Math.E, -10)))) + (p3x * ((t - t2) / ((t3 - t2) + Math.pow(Math.E, -10))));

        double B1x = (A1x * ((t2 - t) / ((t2 - t0) + Math.pow(Math.E, -10))) + (A2x * ((t - t0) / ((t2 - t0) + Math.pow(Math.E, -10)))));
        double B2x = (A2x * ((t3 - t) / ((t3 - t1) + Math.pow(Math.E, -10)))) + (A3x * ((t - t1) / ((t3 - t1) + Math.pow(Math.E, -10))));

        double Cx = (B1x * ((t2 - t) / ((t2 - t1) + Math.pow(Math.E, -10)))) + (B2x * ((t - t1) / ((t2 - t1) + Math.pow(Math.E, -10))));

        double A1y = (p0y * ((t1 - t) / ((t1 - t0) + Math.pow(Math.E, -10)))) + (p1y * ((t - t0) / ((t1 - t0) + Math.pow(Math.E, -10))));
        double A2y = (p1y * ((t2 - t) / ((t2 - t1) + Math.pow(Math.E, -10))) + (p2y * ((t - t1) / ((t2 - t1) + Math.pow(Math.E, -10)))));
        double A3y = (p2y * ((t3 - t) / ((t3 - t2) + Math.pow(Math.E, -10)))) + (p3y * ((t - t2) / ((t3 - t2) + Math.pow(Math.E, -10))));

        double B1y = (A1y * ((t2 - t) / ((t2 - t0) + Math.pow(Math.E, -10))) + (A2y * ((t - t0) / ((t2 - t0) + Math.pow(Math.E, -10)))));
        double B2y = (A2y * ((t3 - t) / ((t3 - t1) + Math.pow(Math.E, -10)))) + (A3y * ((t - t1) / ((t3 - t1) + Math.pow(Math.E, -10))));

        double Cy = (B1y * ((t2 - t) / ((t2 - t1) + Math.pow(Math.E, -10)))) + (B2y * ((t - t1) / ((t2 - t1) + Math.pow(Math.E, -10))));

        double A1h = (p0h * ((t1h - th) / ((t1h - t0) + Math.pow(Math.E, -10)))) + (p1h * ((th - t0) / ((t1h - t0) + Math.pow(Math.E, -10))));
        double A2h = (p1h * ((t2h - th) / ((t2h - t1h) + Math.pow(Math.E, -10))) + (p2h * ((th - t1h) / ((t2h - t1h) + Math.pow(Math.E, -10)))));
        double A3h = (p2h * ((t3h - th) / ((t3h - t2h) + Math.pow(Math.E, -10)))) + (p3h * ((th - t2h) / ((t3h - t2h) + Math.pow(Math.E, -10))));

        double B1h = (A1h * ((t2h - th) / ((t2h - t0) + Math.pow(Math.E, -10))) + (A2h * ((th - t0) / ((t2h - t0) + Math.pow(Math.E, -10)))));
        double B2h = (A2h * ((t3h - th) / ((t3h - t1h) + Math.pow(Math.E, -10)))) + (A3h * ((th - t1h) / ((t3h - t1h) + Math.pow(Math.E, -10))));

        double Ch = (B1h * ((t2h - th) / ((t2h - t1h) + Math.pow(Math.E, -10)))) + (B2h * ((th - t1h) / ((t2h - t1h) + Math.pow(Math.E, -10))));

        Log.d("Velocity", "Mo " + mo);
        Log.d("Velocity", "Mi " + mi);
        Log.d("Velocity", "Mo - Mi (< 60 = full speed) " + Math.abs(mo - mi));
        double enddist = Math.hypot(dec.odo.getPosition().getX(DistanceUnit.INCH)-end.getX(DistanceUnit.INCH),dec.odo.getPosition().getY(DistanceUnit.INCH)-end.getY(DistanceUnit.INCH));



        if ((U > 0.85) || (U < 0.15)) {

            if ((splinePhase == 3) &&  (U > 0.85)) {
                splinePower = (Math.abs((splinePowTop - Math.abs(mo - mi)) / (splinePoweBot)));

                Log.d("EndTendency", "spline pow" + splinePower);
                Log.d("EndTendency", "fl pow" + dec.fl.getPower());
                Log.d("EndTendency", "fr pow" + dec.fr.getPower());
                Log.d("EndTendency", "bl pow" + dec.bl.getPower());
                Log.d("EndTendency", "br pow" + dec.br.getPower());
                Log.d("Endtendency", "Velo " + GetVelo(dec.odo.getPosition(), runtime.milliseconds()));
                Log.d("EndTendency", "end dist" + enddist);
            }else{
                if (Math.abs(mo - mi) < angleDif) {
                    splinePower = 1;
                } else {
                    splinePower = (Math.abs((splinePowTop - Math.abs(mo - mi)) / splinePoweBot));
                }
            }
        } else {
            splinePower = 1;
        }
        //uIncrement = splinePower / 58; //65, 50, 52, 55, 57




          uIncrement = uMin + ((GetVelo(dec.odo.getPosition(), runtime.milliseconds()) / maxVelo) * uMax);

//uIncrement = uSplineConversion;

        Log.d("Velocity", "Velocitym " + GetVelo(dec.odo.getPosition(), runtime.milliseconds()));
        Log.d("Velocity", "Spline Power " + splinePower);
        Log.d("Velocity", "U increment " + uIncrement);
        splineOutput = new Pose2D(DistanceUnit.INCH, Cx, Cy, AngleUnit.DEGREES, Ch);

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
        dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        dec.fl.setPower(0);
        dec.fr.setPower(0);
        dec.bl.setPower(0);
        dec.br.setPower(0);
        sleep(500);
        dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public double GetVelo(Pose2D currentPos, double currentTime) {

        double deltaX = Math.abs(currentPos.getX(DistanceUnit.INCH) - lastPoseVelo.getX(DistanceUnit.INCH));
        double deltaY = Math.abs(currentPos.getY(DistanceUnit.INCH) - lastPoseVelo.getY(DistanceUnit.INCH));
        double deltaT = Math.abs(currentTime - lastTimeVelo);
        newTracingDist = deltaDistRobot+oldTracingDist;
        Log.d("Conversion","newTrace"+newTracingDist);
        Log.d("Conversion","oldTrace"+oldTracingDist);
        Log.d("Conversion","conversion"+uSplineConversion);
        Log.d("Conversion"," max delta robot Dist"+maxDeltaR);
if(deltaDistRobot>maxDeltaR){
    maxDeltaR = deltaDistRobot;
}

        uSplineConversion =( (100*newTracingDist)/currentSplineSectionLength);



        lastPoseVelo = currentPos;
        lastTimeVelo = currentTime;
        oldTracingDist = newTracingDist;



        double velocity = 1000 * (Math.abs(Math.hypot(deltaX, deltaY)) / deltaT);
        deltaDistRobot = Math.abs(Math.hypot(deltaX,deltaY));
        return velocity;
    }
    //endregion
}
