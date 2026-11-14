package org.firstinspires.ftc.teamcode;

import android.util.Log;
import android.util.Size;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp(name = "MaxVelTest", group = "TeleOp")
@Disabled
public class MaxVelTest extends OpMode {
    DecodeConstants dec = new DecodeConstants();


    private ElapsedTime runtime = new ElapsedTime();
    double looptime = 0;

    double x;
    double y;
    double z;
    double lastvel = 0;
    double robovel = 0;

    @Override
    public void init() {
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

        runtime.reset();
        looptime = runtime.milliseconds();
    }

    public void loop() {
        telemetry.addData("Loop Time: ", runtime.milliseconds() - looptime);
        looptime = runtime.milliseconds();

        dec.odo.update();


        //region - Drive
        x = gamepad1.right_stick_x;
        y = -gamepad1.left_stick_y;
        z = gamepad1.left_stick_x;

        dec.flpower = Range.clip(y + x + z, -1, 1);
        dec.frpower = Range.clip(y - x - z, -1, 1);
        dec.blpower = Range.clip(y + x - z, -1, 1);
        dec.brpower = Range.clip(y - x + z, -1, 1);

        dec.fl.setPower(dec.flpower);
        dec.fr.setPower(dec.frpower);
        dec.bl.setPower(dec.blpower);
        dec.br.setPower(dec.brpower);
        //endregion



        telemetry.addLine(String.format("OdoPos: %6.2f %6.2f %6.2f (+90)", dec.odo.getPosition().getX(DistanceUnit.INCH),
                dec.odo.getPosition().getY(DistanceUnit.INCH), (dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) + 90));

        telemetry.update();

        robovel = Math.sqrt(Math.pow(dec.odo.getVelX(DistanceUnit.INCH), 2) + Math.pow(dec.odo.getVelY(DistanceUnit.INCH), 2));
        if (robovel > lastvel) {
            lastvel = robovel;
            Log.d("Vel", "Velocity New:" + lastvel);
        }

    }   // end method runOpMode()

    public void stop() {
        Log.d("Vel", "Max Velocity:" + lastvel);
    }



}
