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

@Autonomous(name = "Bio Meet 1 Close", group = "Linear Opmode")
public class BioAutoMeetOne extends LinearOpMode {
    BiobuzzConstants bio = new BiobuzzConstants();
    RevColorSensorV3 f2;
    NormalizedRGBA f2c;
    RevColorSensorV3 r2;
    NormalizedRGBA r2c;
    RevColorSensorV3 l2;
    NormalizedRGBA l2c;

    private ElapsedTime runtime = new ElapsedTime();

    Pose2D startPose = new Pose2D(DistanceUnit.INCH, -10, -64, AngleUnit.DEGREES, 90);
    Pose2D intakePreLine = new Pose2D(DistanceUnit.INCH, -56.5, -42, AngleUnit.DEGREES, 180);
    Pose2D intakePostLine = new Pose2D(DistanceUnit.INCH, -56.5, -45, AngleUnit.DEGREES, 180);
    Pose2D intakeLine = new Pose2D(DistanceUnit.INCH, -56.5, -54, AngleUnit.DEGREES, 180);
    Pose2D parkWaypoint = new Pose2D(DistanceUnit.INCH, -39, -31.5, AngleUnit.DEGREES, 180);
    Pose2D parkPos = new Pose2D(DistanceUnit.INCH, -52, 28, AngleUnit.DEGREES, 180);
    Pose2D intakeLineSide = new Pose2D(DistanceUnit.INCH, -55.5, -64, AngleUnit.DEGREES, 90);
    Pose2D intakeLineSideWaypoint = new Pose2D(DistanceUnit.INCH, -48, -60, AngleUnit.DEGREES, 45);


    Pose2D flower1 = new Pose2D(DistanceUnit.INCH, -39, -31.5, AngleUnit.DEGREES, 90);
    Pose2D flower2 = new Pose2D(DistanceUnit.INCH, -60, -24, AngleUnit.DEGREES, 90);
    Pose2D farFlower1 = new Pose2D(DistanceUnit.INCH, -24, 57, AngleUnit.DEGREES, 0);

    Pose2D shootPose = new Pose2D(DistanceUnit.INCH, -16, -55, AngleUnit.DEGREES, 0);
    Pose2D farshootPose = new Pose2D(DistanceUnit.INCH, -12, 48, AngleUnit.DEGREES, 0);
    Pose2D farshootPoseWaypoint = new Pose2D(DistanceUnit.INCH, -40, 15, AngleUnit.DEGREES, 0);
    Pose2D farshootPoseWaypoint2 = new Pose2D(DistanceUnit.INCH, -40, 25, AngleUnit.DEGREES, 0);

    Pose2D farPark = new Pose2D(DistanceUnit.INCH, -60, 40, AngleUnit.DEGREES, 0);


    Pose2D startPose2 = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);
    Pose2D waypoint11 = new Pose2D(DistanceUnit.INCH, 0, 24, AngleUnit.DEGREES, 0);
    Pose2D startPose12 = new Pose2D(DistanceUnit.INCH, 40, 24, AngleUnit.DEGREES, 0);
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
        OREOAlpha oreoAlpha = new OREOAlpha(bio, this, hardwareMap);
        bio.configureOdo(true);
        bio.navreset(true, true);

        //MARK

        bio.odo.setPosition(startPose);        //endregion

        bio.odo.update();



        telemetry.addData("Odo x: ", bio.odo.getPosition().getX(DistanceUnit.INCH));
        telemetry.addData("Odo y: ", bio.odo.getPosition().getY(DistanceUnit.INCH));
        telemetry.addData("Odo z: ", bio.odo.getPosition().getHeading(AngleUnit.DEGREES));
        telemetry.addData("Status: ", "Ready");
        telemetry.update();
        bio.hood.setPosition(bio.hoodautostart);
        bio.turret.setPosition(bio.turretautostart);


        waitForStart();

        telemetry.addData("Odo x: ", bio.odo.getPosition().getX(DistanceUnit.INCH));
        telemetry.addData("Odo y: ", bio.odo.getPosition().getY(DistanceUnit.INCH));
        telemetry.addData("Odo z: ", bio.odo.getPosition().getHeading(AngleUnit.DEGREES));
        telemetry.addData("Status: ", "Ready");
        telemetry.update();
        bio.odo.setPosition(startPose);


        bio.flywheel.setVelocity(bio.flywheelautostart);
        bio.hood.setPosition(bio.hoodautostart);
        sleep(3000);
        bio.transfer.setPower(1);
        bio.intakef.setPower(1);
        bio.intakeb.setPower(1);

        sleep(3000);
        oreoAlpha.PerformanceSpline(3500, startPose, intakePreLine);
//sleep(1000);
        // oreo.RushingSpline(5000, flower2, flower1,shootPose);
        //  oreo.RushingSpline(4500, flower2,farshootPoseWaypoint,farshootPose);
        bio.intakef.setPower(1);
        bio.transfer.setPower(0);
        bio.intakeb.setPower(0);

        oreoAlpha.PerformanceSpline(1000, intakePreLine, intakeLine);
        oreoAlpha.PerformanceSpline(1000, intakeLine, intakePostLine);
        oreoAlpha.PerformanceSpline(1500, intakePostLine, intakeLineSideWaypoint, intakeLineSide);
        //oreoAlpha.PerformanceSpline(3000,in,intakePostLine);

        //to prevent nectar
        bio.intakef.setPower(0);
        bio.intakeb.setPower(-1);

        oreoAlpha.PerformanceSpline(3000, intakePostLine, startPose);
        sleep(1000);
        bio.transfer.setPower(1);
        bio.intakef.setPower(1);
        bio.intakeb.setPower(0);
        sleep(3000);

        bio.transfer.setPower(0);
        bio.intakeb.setPower(-1);
        bio.intakef.setPower(0);
        oreoAlpha.PerformanceSpline(6500, startPose, parkWaypoint, parkPos);
        bio.transfer.setPower(0);
        bio.intakeb.setPower(0);
        bio.intakef.setPower(0);
//sleep(1000);
        //  oreo.RushingSpline(5000, startPose, waypoint1,parkPos);
        // sleep(1000);
    }






}
