package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import android.util.Log;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@Autonomous(name = "Bio Meet 1 Far", group = "Linear Opmode")
public class BioAutoMeetOneFar extends LinearOpMode {
    BiobuzzConstants bio = new BiobuzzConstants();
    RevColorSensorV3 f2;
    NormalizedRGBA f2c;
    RevColorSensorV3 r2;
    NormalizedRGBA r2c;
    RevColorSensorV3 l2;
    NormalizedRGBA l2c;

    private ElapsedTime runtime = new ElapsedTime();

    Pose2D startPosefar = new Pose2D(DistanceUnit.INCH, -7, 70, AngleUnit.DEGREES, 180);
    Pose2D intakeWaypoint1 = new Pose2D(DistanceUnit.INCH, -24, 45, AngleUnit.DEGREES, 180);
    Pose2D intakeWaypoint2 = new Pose2D(DistanceUnit.INCH, -48, -24, AngleUnit.DEGREES, 180);

    Pose2D intakePreLine = new Pose2D(DistanceUnit.INCH, -56.5, -42, AngleUnit.DEGREES, 180);
    Pose2D intakePostLine = new Pose2D(DistanceUnit.INCH, -56.5, -45, AngleUnit.DEGREES, 180);
    Pose2D intakeLine = new Pose2D(DistanceUnit.INCH, -56.5, -54, AngleUnit.DEGREES, 180);
    Pose2D intakeLineSide = new Pose2D(DistanceUnit.INCH, -55.5, -64, AngleUnit.DEGREES, 90);
    Pose2D intakeLineSideWaypoint = new Pose2D(DistanceUnit.INCH, -48, -60, AngleUnit.DEGREES, 45);






    Pose2D farshootPose = new Pose2D(DistanceUnit.INCH, -7, 60, AngleUnit.DEGREES, 180);
    Pose2D farshootPoseWaypoint = new Pose2D(DistanceUnit.INCH, -40, 15, AngleUnit.DEGREES, 0);
    Pose2D farshootPoseWaypoint2 = new Pose2D(DistanceUnit.INCH, -40, 25, AngleUnit.DEGREES, 0);

    Pose2D farPark = new Pose2D(DistanceUnit.INCH, -60, 43, AngleUnit.DEGREES, 0);



    double drivetimeout = 0;
    double wait = 0;
    double loopcount = 0;
    double checktime = 0;

    double lsp = 0;
    boolean TUNING = true;
    @Override
    public void runOpMode() {
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


        bio.fl.setPower(0);
        bio.fr.setPower(0);
        bio.bl.setPower(0);
        bio.br.setPower(0);

        //  bio.flywheel.setVelocity(0); BRING BACK LATER
        bio.intakef.setPower(0);
        bio.transfer.setPower(0);


        bio.configureOdo(true); //false


        // Reset the runtime
        runtime.reset();
        //region - Hardware Map




        //     OREOBeta oreoBeta = new OREOBeta(drivetrain,this);
        OREOAlpha oreoAlpha = new OREOAlpha(bio,this,hardwareMap);
        bio.configureOdo(true);
        bio.navreset(true,true);
        bio.odo.setPosition(startPosefar);        //endregion
        bio.odo.update();




        telemetry.addData("Odo x: ", bio.odo.getPosition().getX(DistanceUnit.INCH));
        telemetry.addData("Odo y: ", bio.odo.getPosition().getY(DistanceUnit.INCH));
        telemetry.addData("Odo z: ", bio.odo.getPosition().getHeading(AngleUnit.DEGREES));
        telemetry.addData("Status: ", "Ready");
        telemetry.update();
        bio.hood.setPosition(.81);
        //was .81
        bio.turret.setPosition(0.00175667*0+0.5022);
        waitForStart();

        telemetry.addData("Odo x: ",bio.odo.getPosition().getX(DistanceUnit.INCH));
        telemetry.addData("Odo y: ", bio.odo.getPosition().getY(DistanceUnit.INCH));
        telemetry.addData("Odo z: ", bio.odo.getPosition().getHeading(AngleUnit.DEGREES));
        telemetry.addData("Status: ", "Ready");
        telemetry.update();
        bio.odo.setPosition(startPosefar);


        bio.flywheel.setVelocity(2535.0/ 60 * 28);
        bio.hood.setPosition(.81);
        sleep(6000);
        bio.transfer.setPower(1);
        bio.intakef.setPower(1);
        bio.intakeb.setPower(1);

        sleep(3000);
        bio.intakef.setPower(-1);
        bio.intakeb.setPower(-1);

        oreoAlpha.PerformanceSpline(5000, startPosefar, intakeWaypoint1,intakeWaypoint2,intakePreLine);
//sleep(1000);
        // oreo.RushingSpline(5000, flower2, flower1,shootPose);
        //  oreo.RushingSpline(4500, flower2,farshootPoseWaypoint,farshootPose);
        bio.intakef.setPower(1);
        bio.transfer.setPower(0);
        bio.intakeb.setPower(0);

        oreoAlpha.PerformanceSpline(3000,intakePreLine,intakeLine);
        oreoAlpha.PerformanceSpline(3000,intakeLine,intakePostLine);
        oreoAlpha.PerformanceSpline(2000,intakePostLine,intakeLineSideWaypoint,intakeLineSide);
        //oreoAlpha.PerformanceSpline(3000,intakeLineSide,intakePostLine);

      //  bio.intakef.setPower(0);
        oreoAlpha.PerformanceSpline(4000,intakeLineSide,intakeWaypoint2,intakeWaypoint1,farshootPose);
        bio.transfer.setPower(1);
        bio.intakef.setPower(1);
        bio.intakeb.setPower(1);
        sleep(3000);
        bio.transfer.setPower(0);
        bio.intakeb.setPower(-1);
        bio.intakef.setPower(0);
        oreoAlpha.PerformanceSpline(5000,farshootPose,farPark);
        bio.transfer.setPower(0);
        bio.intakeb.setPower(0);
        bio.intakef.setPower(0);
//sleep(1000);
        //  oreo.RushingSpline(5000, startPose, waypoint1,parkPos);
        // sleep(1000);
    }

    //region - Drive







    public void Launch(Pose2D launchpos, double time) {
        bio.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bio.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bio.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bio.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
/*
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

 */
    }



    public void Turret(double target, String color) {
        /*
        }
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

         */
    }




}
