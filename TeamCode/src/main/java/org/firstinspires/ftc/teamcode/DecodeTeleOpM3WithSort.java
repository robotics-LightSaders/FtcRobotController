//package org.firstinspires.ftc.teamcode;
//
//import android.graphics.Color;
//import android.util.Log;
//import android.util.Size;
//
//import com.qualcomm.hardware.lynx.LynxModule;
//import com.qualcomm.hardware.rev.RevColorSensorV3;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.AnalogInput;
//import com.qualcomm.robotcore.hardware.CRServo;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
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
//@TeleOp(name = "TeleOpM3WithSort", group = "TeleOp")
//@Disabled
//public class DecodeTeleOpM3WithSort extends OpMode {
//
//    DecodeConstants dec = new DecodeConstants();
//
//    RevColorSensorV3 f2;
//    NormalizedRGBA f2c;
//    RevColorSensorV3 r2;
//    NormalizedRGBA r2c;
//    RevColorSensorV3 l2;
//    NormalizedRGBA l2c;
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
//    private ElapsedTime runtime = new ElapsedTime();
//    double looptime = 0;
//
//    double x;
//    double y;
//    double z;
//
//    double g1dtime = 0;
//
//    double switchtime = 0;
//    double launchtime = -2000;
//    double rapidtime = 0;
//    double r_num =0;
//    double l_num =0;
//    double f_num =0;
//    double r_sat =0;
//    double l_sat =0;
//    double f_sat =0;
//    double r_bri =0;
//    double l_bri =0;
//    double f_bri =0;
//    double intakemovetime = 1400;//1000//800
//    double movetime = 200; //500 //800
//    double longmovetime = 400; //800 //1100
//    double colormovetime = 600;
//    double colorlongmovetime = 800;
//
//    double checktime = 1000;
//
//    double launchDelay = 2000; //2000
//
//    double transfertime = 0;
//
//    double preptime = 0;
//
//    boolean a2 = true;
//    double a2time = 0;
//
//    boolean transferon = false;
//
//    double colortime = 0;
//
//    boolean firstloop = true;
//
//    @Override
//    public void init() {
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
//        dec.park = hardwareMap.get(Servo.class,"park");
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
//        dec.transferPower = 0;
//
//        //dec.susan.setPosition(dec.susanpos1);
//        //dec.hood.setPosition(dec.hooddown);
//        //dec.camrot.setPosition(dec.camlong);
//        dec.turret.setPower(0);
//
//        dec.configureOdo(true);
//
//        //dec.odo.setPosition(dec.odoorigin);
//
//        initAprilTag();
//
//        dec.LAUNCH = dec.odoorigin;
//
//        runtime.reset();
//        looptime = runtime.milliseconds();
//    }
//
//    public void init_loop() {
//        if (gamepad1.x) {
//            dec.teamcolor = "Blue";
//            dec.goal = dec.Bgoal;
//            dec.odo.setPosition(dec.Bpark2);
//        } else if (gamepad1.b) {
//            dec.teamcolor = "Red";
//            dec.goal = dec.Rgoal;
//            dec.odo.setPosition(dec.Rpark2);
//        }
//
//        if ((dec.autoend.getX(DistanceUnit.INCH) != dec.odoorigin.getX(DistanceUnit.INCH)) &&
//                (dec.autoend.getY(DistanceUnit.INCH) != dec.odoorigin.getY(DistanceUnit.INCH)) &&
//                (dec.autoend.getHeading(AngleUnit.DEGREES) != dec.odoorigin.getHeading(AngleUnit.DEGREES))) {
//            dec.odo.setPosition(dec.autoend);
//
//            telemetry.addData("Override", "");
//        } else {
//            if (dec.teamcolor.matches("Red")) {
//                dec.odo.setPosition(dec.Rpark2);
//            } else {
//                dec.odo.setPosition(dec.Bpark2);
//
//            }
//        }
//
//        dec.odo.update();
//
//        telemetry.addData("Color: ", dec.teamcolor);
//        telemetry.addData("Pos: ", Math.round(dec.odo.getPosition().getX(DistanceUnit.INCH)) + ", " +
//                Math.round(dec.odo.getPosition().getY(DistanceUnit.INCH)) + ", " + Math.round(dec.odo.
//                getPosition().getHeading(AngleUnit.DEGREES)));
//    }
//
//    public void loop() {
//
//        if (firstloop){
//            dec.susan.setPosition(dec.susanpos1);
//            dec.parkPOS = dec.parkUp;
//            firstloop = false;
//        }
//
//        //region - Loop Time
//        telemetry.addData("Loop Time: ", runtime.milliseconds() - looptime);
//        looptime = runtime.milliseconds();
//
//        dec.angvel = (dec.odo.getHeading(AngleUnit.DEGREES) - dec.lastang) / looptime;
//        dec.lastang = dec.odo.getHeading(AngleUnit.DEGREES);
//        telemetry.addData("Ang Vel: ", dec.angvel);
//        //endregion
//
//        //region - Odo Reset
//        if (gamepad1.y) {
//            dec.robopos = dec.RP(dec.campos, dec.taxon.getTotalRotation(), dec.toffset, dec.odo.getPosition());
//            dec.odo.setPosition(new Pose2D(DistanceUnit.INCH, dec.robopos.x, dec.robopos.y, AngleUnit.DEGREES, dec.robopos.z));
//        }
//        //endregion
//
//        //region - Team Color
//        if (gamepad1.a && (colortime < runtime.milliseconds())) {
//            colortime = runtime.milliseconds() + 250;
//            if (dec.teamcolor.matches("Blue")) {
//                dec.goal = dec.Rgoal;
//                dec.teamcolor = "Red";
//            } else {
//                dec.goal = dec.Bgoal;
//                dec.teamcolor = "Blue";
//            }
//        }
//
//        telemetry.addData("Team Color: ", dec.teamcolor);
//        //endregion
//
//        ///
//        //region - Color
//        //  if (dec.intaking) { //mark
//        f2c = f2.getNormalizedColors();
//        r2c = r2.getNormalizedColors();
//        l2c = l2.getNormalizedColors();
//        //   }
//        // region - Color HSV
//        //right
//        float [] hsvR = new float[3];
//       Color.RGBToHSV(
//                (int) (r2c.red * 255),
//                (int) (r2c.green * 255),
//                (int) (r2c.blue * 255),
//               hsvR
//                );
//       r_num = hsvR[0];
//       r_sat = hsvR[1];
//       r_bri = hsvR[2];
//       // left
//        float [] hsvL = new float[3];
//        Color.RGBToHSV(
//                (int) (l2c.red * 255),
//                (int) (l2c.green * 255),
//                (int) (l2c.blue * 255),
//                hsvL
//        );
//        l_num = hsvL[0];
//        l_sat = hsvL[1];
//        l_bri = hsvL[2];
//        //front
//        float [] hsvF = new float[3];
//        Color.RGBToHSV(
//                (int) (f2c.red * 255),
//                (int) (f2c.green * 255),
//                (int) (f2c.blue * 255),
//                hsvF
//        );
//        f_num = hsvF[0];
//        f_sat = hsvF[1];
//        f_bri = hsvF[2];
//
//        //   if (dec.intaking) { //mark
//        if (f2.getDistance(DistanceUnit.CM) < 4) {//(f_bri > 0) && (f_num > 125)
//            if ((f2c.green / f2c.red) > dec.gratio) {
//                dec.revf = "Green";
//            } else {
//                dec.revf = "Purple";
//            }
//        } else {
//            dec.revf = "None";
//        }
//
//        if ((r_bri > 0) && (r_num > 125)) {
//            if ((r2c.green / r2c.red) > dec.gratio) {
//                dec.revr = "Green";
//            } else {
//                dec.revr = "Purple";
//            }
//        } else {
//            dec.revr = "None";
//        }
//
//        /*if (r_num >= 100) {
//            if (r_num >= 180) {
//                dec.revr = "Purple";
//            } else {
//                dec.revr = "Green";
//            }
//        } else {
//            dec.revr = "None";
//        }*/
//
//        if ((l_bri > 0) && (l_num > 125)) {
//            if ((l2c.green / l2c.red) > dec.gratio) {
//                dec.revl = "Green";
//            } else {
//                dec.revl = "Purple";
//            }
//        } else {
//            dec.revl = "None";
//        }
//
//        //(gamepad2.a || gamepad2.b || gamepad2.x || dec.intaking) && (launchtime < runtime.milliseconds())
//        if (((gamepad2.y || dec.intaking || gamepad2.a || dec.transferCycle)) && (((dec.susan.getPosition() < (dec.susanpos1 + .01)) && (dec.susan.getPosition() > (dec.susanpos1 - .01))) || ((dec.susan.getPosition() < (dec.susanpos2 + .01)) && (dec.susan.getPosition() > (dec.susanpos2 - .01))) ||
//                ((dec.susan.getPosition() < (dec.susanpos3 + .01)) && (dec.susan.getPosition() > (dec.susanpos3 - .01))) || ((dec.susan.getPosition() < (dec.susanlaunch1 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch1 - .01))) ||
//                ((dec.susan.getPosition() < (dec.susanlaunch2 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch2 - .01))) || ((dec.susan.getPosition() < (dec.susanlaunch3 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch3 - .01))))) { //1.a // was 2 y and dec intaking
//            dec.scantime = runtime.milliseconds();
//
//            if (((checktime < runtime.milliseconds()))) {
//                Log.d("Launch", "Not seeing");
//                checktime = runtime.milliseconds() + 2000;
//                if (((dec.susan.getPosition() < (dec.susanlaunch1 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch1 - .01)))) {
//                    dec.susan.setPosition(dec.susanpos1);
//                } else if (((dec.susan.getPosition() < (dec.susanlaunch2 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch2 - .01)))) {
//                    dec.susan.setPosition(dec.susanpos2);
//                } else if (((dec.susan.getPosition() < (dec.susanlaunch3 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch3 - .01)))) {
//                    dec.susan.setPosition(dec.susanpos3);
//                }
//            }
//
//            if (dec.f0.getState() || dec.revf.matches("Purple")) {
//                telemetry.addData("F Color: ", "Purple");
//                dec.FC = "Purple";
//            } else if (dec.f1.getState() || dec.revf.matches("Green")) {
//                telemetry.addData("F Color: ", "Green");
//                dec.FC = "Green";
//            } else {
//                telemetry.addData("F No Color", "");
//                dec.FC = "None";
//            }
//
//            if (dec.r0.getState() || dec.revr.matches("Purple")) {
//                telemetry.addData("R Color: ", "Purple");
//                dec.RC = "Purple";
//            } else if (dec.r1.getState() || dec.revr.matches("Green")) {
//                telemetry.addData("R Color: ", "Green");
//                dec.RC = "Green";
//            } else {
//                telemetry.addData("R No Color", "");
//                dec.RC = "None";
//            }
//
//            if (dec.l0.getState() || dec.revl.matches("Purple")) {
//                telemetry.addData("L Color: ", "Purple");
//                dec.LC = "Purple";
//            } else if (dec.l1.getState() || dec.revl.matches("Green")) {
//                telemetry.addData("L Color: ", "Green");
//                dec.LC = "Green";
//            } else {
//                telemetry.addData("L No Color", "");
//                dec.LC = "None";
//            }
//
//            if ((launchtime < runtime.milliseconds()) && (rapidtime < runtime.milliseconds())) {
//                Log.d("Launch", "Color Check");
//                Log.d("Launch FC", dec.FC);
//                Log.d("Launch RC", dec.RC);
//                Log.d("Launch LC", dec.LC);
//            }
//        }
//        //endregion
//
//        //region - Driving
//        x = gamepad1.right_stick_x;
//        y = -gamepad1.left_stick_y;
//        z = gamepad1.left_stick_x;
//
//        dec.flpower = Range.clip(y + x + z, -1, 1);
//        dec.frpower = Range.clip(y - x - z, -1, 1);
//        dec.blpower = Range.clip(y + x - z, -1, 1);
//        dec.brpower = Range.clip(y - x + z, -1, 1);
//
//        dec.fl.setPower(dec.flpower);
//        dec.fr.setPower(dec.frpower);
//        dec.bl.setPower(dec.blpower);
//        dec.br.setPower(dec.brpower);
//
//        if (((gamepad1.left_trigger > 0.25) && (gamepad1.right_trigger > 0.25)) || dec.launchy) {
//            dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//            dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//            dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//            dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//            //telemetry.addData("-------BRAKE!!!-------", "");
//        } else {
//            dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//            dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//            dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//            dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//
//            //telemetry.addData("GOOO!!!", "");
//        }
//        //endregion
//
//        //region - Park
//        dec.park.setPosition(dec.parkPOS);
//
//        if (gamepad1.right_stick_button && !dec.parkCheck) {
//           dec.parkCheck = true;
//            if (!dec.parkON) {
//                dec.parkPOS = dec.parkDown;
//                dec.parkON = true;
//            }else {
//                dec.parkPOS = dec.parkUp;
//                dec.parkON = false;
//            }
//        }
//
//        if (!gamepad1.right_stick_button){
//            dec.parkCheck = false;
//        }
//
//
//       /* if (gamepad1.right_stick_button){
//            dec.parkPOS = dec.parkPOS + .01;
//        } else if (gamepad1.left_stick_button) {
//            dec.parkPOS = dec.parkPOS - .01;
//        }*/
//        //endregion
//
//        //region - Cam Rot
//        if (dec.goalDC(dec.usePos, dec.goal) < dec.camlim) { //false, dec.odo.getPosition()
//            dec.camrot.setPosition(dec.camclose);
//        } else if (dec.camrot.getPosition() != dec.camlong) {
//            dec.camrot.setPosition(dec.camlong);
//        }
//        //endregion
//
//        //region - Intake (Uno y Dos)
//        if (gamepad2.right_trigger > 0.2) {
//            dec.intake.setPower(gamepad2.right_trigger);
//            //dec.intakedos.setPower(0.5); //0.25
//        } else if (gamepad2.left_trigger > 0.2) {
//            dec.intake.setPower(-gamepad2.left_trigger);
//            //dec.intakedos.setPower(-0.5); //-0.25
//        } else {
//            dec.intake.setPower(0);
//            //dec.intakedos.setPower(0);
//        }
//        //endregion
//
//        //region - Flywheel
//        if (gamepad1.left_stick_button) {
//            telemetry.addData("Xg: ", dec.goalDC(dec.usePos, dec.goal)); //dec.odo.getPosition()
//            telemetry.addData("Velocity in RPM =", dec.launch.getVelocity() * 60 / 28);
//        }
//        //telemetry.addData("Hood Position", dec.hoodpos);
//
//        //if (dec.goalDC(dec.usePos, dec.goal)) //diff between old and new pos > x, recalc, if not, no need if not moved
//        dec.velocityT = dec.launchVelCalc(dec.goalDC(dec.usePos, dec.goal),
//                dec.launchy) / 60 * 28; //dec.odo.getPosition()
//
//        dec.launch.setVelocity(dec.velocityT + dec.launchvel);
//        dec.hood.setPosition(dec.launchHoodCalc(dec.goalDC(dec.usePos, dec.goal),
//                dec.launchy) + dec.hoffset); //dec.odo.getPosition()
//
//        if (dec.launchy && (Math.abs(dec.launch.getVelocity() - dec.velocityT) < 200)) {
//            dec.canlaunch = true;
//            //dec.launched = false;
//        } else {
//            dec.canlaunch = false;
//        }
//        //endregion
//
//        //region - Manual Flywheel Adjust
//        if (g1dtime < runtime.milliseconds()) {
//            if (gamepad1.dpad_up) {
//                dec.launchvel = dec.launchvel + 12; //when multiplied by 60/28 later ~=2, causes ~25 rpm change
//                g1dtime = runtime.milliseconds() + 200;
//            } else if (gamepad1.dpad_down) {
//                dec.launchvel = dec.launchvel - 12;
//                g1dtime = runtime.milliseconds() + 200;
//            }
//        }
//
//        if (dec.launchvel != 0) {
//            telemetry.addData("Launch Vel: ", dec.launchvel);
//        }
//        //endregion
//
//        ///
//        //region - Susan
//        if (gamepad2.a) { //2.y
//            dec.LAUNCH = dec.odo.getPosition();
//
//
//            if (((dec.susan.getPosition() < (dec.susanpos1 - 0.01)) && (dec.susan.getPosition() > (dec.susanpos1
//                    + 0.01))) && ((dec.susan.getPosition() < (dec.susanpos2 - 0.01)) && (dec.susan.getPosition() >
//                    (dec.susanpos2 + 0.01))) && ((dec.susan.getPosition() < (dec.susanpos3 - 0.01)) && (dec.susan.
//                    getPosition() > (dec.susanpos3 + 0.01))) && ((dec.susan.getPosition() < (dec.susanlaunch1 -
//                    0.01)) && (dec.susan.getPosition() > (dec.susanlaunch1 + 0.01))) && ((dec.susan.getPosition()
//                    < (dec.susanlaunch2 - 0.01)) && (dec.susan.getPosition() > (dec.susanlaunch2 + 0.01))) &&
//                    ((dec.susan.getPosition() < (dec.susanlaunch3 - .01)) && (dec.susan.getPosition() >
//                            (dec.susanlaunch3 + 0.01)))) {
//                dec.susan.setPosition(dec.susanlaunch1);
//
//                rapidtime = runtime.milliseconds() + 500; //500? //was 200
//                Log.d("Launch", "12");
//            }
//            if (Math.abs(gamepad2.left_stick_y) < 0.2) {
//                dec.transfer.setPower(1);
//                dec.transferPower = 1;
//                transferon = true;
//            }
//
//
//            // if (dec.greenLaunched || dec.purpleLaunched) {
//
//            if ((launchtime < runtime.milliseconds()) && (rapidtime < runtime.milliseconds())) {
//                Log.d("Launch", "2");
//                telemetry.addData("launchtime", "GO");
//
//                if (dec.FC.matches("None")) {
//                    if (dec.RC.matches("None")) {
//                        if (!dec.LC.matches("None")) {
//                            if ((((dec.susan.getPosition() < (dec.susanpos1 + 0.01)) && (dec.susan.getPosition() >
//                                    (dec.susanpos1 - 0.01)))) || (((dec.susan.getPosition() < (dec.susanlaunch1 +
//                                    0.01)) && (dec.susan.getPosition() > (dec.susanlaunch1 - 0.01))))) {
//                                dec.susan.setPosition(dec.susanlaunch2);
//                                launchtime = runtime.milliseconds() + movetime;
//                                dec.launched = true;
//
//                                Log.d("Launch", "3");
//                            } else if ((((dec.susan.getPosition() < (dec.susanpos2 + 0.01)) && (dec.susan.getPosition() >
//                                    (dec.susanpos2 - 0.01)))) || (((dec.susan.getPosition() < (dec.susanlaunch2 +
//                                    0.01)) && (dec.susan.getPosition() > (dec.susanlaunch2 - 0.01))))) {
//                                dec.susan.setPosition(dec.susanlaunch3);
//                                launchtime = runtime.milliseconds() + movetime;
//                                dec.launched = true;
//
//                                Log.d("Launch", "4");
//                            } else if ((((dec.susan.getPosition() < (dec.susanpos3 + 0.01)) && (dec.susan.getPosition() >
//                                    (dec.susanpos3 - 0.01)))) || (((dec.susan.getPosition() < (dec.susanlaunch3 +
//                                    0.01)) && (dec.susan.getPosition() > (dec.susanlaunch3 - 0.01))))) {
//                                dec.susan.setPosition(dec.susanlaunch1);
//                                launchtime = runtime.milliseconds() + longmovetime;
//                                dec.launched = true;
//
//                                Log.d("Launch", "5");
//                            } else {
//                                dec.susan.setPosition(dec.susanlaunch1);
//                                rapidtime = runtime.milliseconds() + 500; //500?
//
//                                Log.d("Launch", "6");
//                            }
//                        }
//                    } else {
//                        if ((((dec.susan.getPosition() < (dec.susanpos1 + 0.01)) && (dec.susan.getPosition() >
//                                (dec.susanpos1 - 0.01)))) || (((dec.susan.getPosition() < (dec.susanlaunch1 +
//                                0.01)) && (dec.susan.getPosition() > (dec.susanlaunch1 - 0.01))))) {
//                            dec.susan.setPosition(dec.susanlaunch3);
//                            launchtime = runtime.milliseconds() + longmovetime;
//                            dec.launched = true;
//
//                            Log.d("Launch", "7");
//                        } else if ((((dec.susan.getPosition() < (dec.susanpos2 + 0.01)) && (dec.susan.getPosition()
//                                > (dec.susanpos2 - 0.01)))) || (((dec.susan.getPosition() < (dec.susanlaunch2 + 0.01))
//                                && (dec.susan.getPosition() > (dec.susanlaunch2 - 0.01))))) {
//                            dec.susan.setPosition(dec.susanlaunch1);
//                            launchtime = runtime.milliseconds() + movetime;
//                            dec.launched = true;
//
//                            Log.d("Launch", "8");
//                        } else if ((((dec.susan.getPosition() < (dec.susanpos3 + 0.01)) && (dec.susan.getPosition()
//                                > (dec.susanpos3 - 0.01)))) || (((dec.susan.getPosition() < (dec.susanlaunch3 + 0.01))
//                                && (dec.susan.getPosition() > (dec.susanlaunch3 - 0.01))))) {
//                            dec.susan.setPosition(dec.susanlaunch2);
//                            launchtime = runtime.milliseconds() + movetime;
//                            dec.launched = true;
//
//                            Log.d("Launch", "9");
//                        } else {
//                            dec.susan.setPosition(dec.susanlaunch1);
//                            rapidtime = runtime.milliseconds() + 500; //500?
//
//                            Log.d("Launch", "10");
//                        }
//                    }
//                } else {
//                    launchtime = runtime.milliseconds();
//                    dec.launched = true;
//                    Log.d("Launch", "11");
//                    Log.d("f color is", dec.FC);
//                    if (((((dec.susan.getPosition() < (dec.susanpos1 + 0.01)) && (dec.susan.getPosition() >
//                            (dec.susanpos1 - 0.01)))) || (((dec.susan.getPosition() < (dec.susanlaunch1 + 0.01))
//                            && (dec.susan.getPosition() > (dec.susanlaunch1 - 0.01)))))) {
//                        dec.susan.setPosition(dec.susanlaunch1);
//                        launchtime = runtime.milliseconds() + longmovetime;
//                        dec.launched = true;
//
//                        Log.d("Launch", "13");
//                    } else if (((((dec.susan.getPosition() < (dec.susanpos2 + 0.01)) && (dec.susan.getPosition() >
//                            (dec.susanpos2 - 0.01)))) || (((dec.susan.getPosition() < (dec.susanlaunch2 + .01)) &&
//                            (dec.susan.getPosition() > (dec.susanlaunch2 - 0.01)))))) {
//                        dec.susan.setPosition(dec.susanlaunch2);
//                        launchtime = runtime.milliseconds() + movetime;
//                        dec.launched = true;
//
//                        Log.d("Launch", "14");
//                    } else if (((((dec.susan.getPosition() < (dec.susanpos3 + 0.01)) && (dec.susan.getPosition() >
//                            (dec.susanpos3 - 0.01)))) || (((dec.susan.getPosition() < (dec.susanlaunch3 + 0.01)) &&
//                            (dec.susan.getPosition() > (dec.susanlaunch3 - 0.01)))))) {
//                        dec.susan.setPosition(dec.susanlaunch3);
//                        launchtime = runtime.milliseconds() + movetime;
//                        dec.launched = true;
//
//                        Log.d("Launch", "15");
//                    } else {
//                        dec.susan.setPosition(dec.susanlaunch1);
//                        rapidtime = runtime.milliseconds() + 500; //500?
//
//                        Log.d("Launch", "16");
//                    }
//                }
//            }
//           /* } else {
//                if (((dec.susan.getPosition() < (dec.susanpos3 + 0.01)) && (dec.susan.getPosition() >
//                        (dec.susanpos3 - 0.01))) || ((dec.susan.getPosition() < (dec.susanbtw23 + 0.01)) && (dec.susan.getPosition() >
//                        (dec.susanbtw23 - 0.01)))){
//                    dec.susan.setPosition(dec.susanlaunch3);
//                    launchtime = runtime.milliseconds() + movetime;
//                    dec.launched = true;
//
//                    Log.d("Launch", "17");
//
//                } else if (((dec.susan.getPosition() < (dec.susanlaunch3 + 0.01)) && (dec.susan.getPosition() >
//                        (dec.susanlaunch3 - 0.01))) || ((dec.susan.getPosition() < (dec.susanpos2 + 0.01)) && (dec.susan.getPosition() >
//                        (dec.susanpos2 - 0.01)))) {
//                    dec.susan.setPosition(dec.susanlaunch2);
//                    launchtime = runtime.milliseconds() + movetime;
//                    dec.launched = true;
//
//                    Log.d("Launch", "18");
//                } else if (((dec.susan.getPosition() < (dec.susanlaunch2 + 0.01)) && (dec.susan.getPosition() >
//                        (dec.susanlaunch2 - 0.01))) || ((dec.susan.getPosition() < (dec.susanpos1 + 0.01)) && (dec.susan.getPosition() >
//                        (dec.susanpos1 - 0.01))) || ((dec.susan.getPosition() < (dec.susanbtw12 + 0.01)) && (dec.susan.getPosition() >
//                        (dec.susanbtw12 - 0.01))) ) {
//                    dec.susan.setPosition(dec.susanlaunch1);
//                    launchtime = runtime.milliseconds() + movetime;
//                    dec.launched = true;
//
//                    Log.d("Launch", "19");
//                }
//
//            } */
//        } /*else if (((launchtime + launchDelay) > runtime.milliseconds())) {
//            dec.transferPower = 1;
//            dec.transfer.setPower(1);
//
//            Log.d("Transfer", "Transfer On");
//        } else {
//            dec.launched = false;
//        }*/
//        //endregion
//
//        //region - Auto Intaking
//        if (/*(Math.abs(dec.intake.getPower()) > 0.2) && (!dec.susanprep) && */!dec.launchy /*(dec.transferPower == 0)*/ && dec.intaking && (switchtime < runtime.milliseconds())) {
//
//            Log.d("Sorting", "0");
//
//            dec.purpleLaunched = false;
//            dec.greenLaunched = false;
//           // dec.transferCycle = false;
//            // dec.susanprep = false;
//
//            /*if ((Math.abs(gamepad2.right_trigger) < 0.1) && (Math.abs(gamepad2.left_trigger) < 0.1)) {
//                dec.intake.setPower(1);
//            }*/
//
//            if (!dec.FC.matches("None")) {
//                Log.d("Sorting", "1");
//                if ((((dec.susan.getPosition() < (dec.susanpos1 + .01)) && (dec.susan.getPosition() > (dec.susanpos1 - .01)))) || (dec.susan.getPosition() == dec.susanlaunch1)) {
//                    Log.d("Sorting", "2");
//                    if (dec.LC.matches("None")) {
//                        dec.susan.setPosition(dec.susanpos2);
//                        switchtime = runtime.milliseconds() + intakemovetime;
//                        Log.d("Sorting", "3");
//                    } else if (dec.RC.matches("None")) {
//                        dec.susan.setPosition(dec.susanpos3);
//                        switchtime = runtime.milliseconds() + intakemovetime;
//                        Log.d("Sorting", "4");
//                    }
//                } else if ((((dec.susan.getPosition() < (dec.susanpos2 + .01)) && (dec.susan.getPosition() > (dec.susanpos2 - .01)))) || (dec.susan.getPosition() == dec.susanlaunch2)) {
//                    Log.d("Sorting", "5");
//                    if (dec.LC.matches("None")) {
//                        dec.susan.setPosition(dec.susanpos3);
//                        switchtime = runtime.milliseconds() + intakemovetime;
//                        Log.d("Sorting", "6");
//                    } else if (dec.RC.matches("None")) {
//                        dec.susan.setPosition(dec.susanpos1);
//                        switchtime = runtime.milliseconds() + intakemovetime;
//                        Log.d("Sorting", "7");
//                    }
//                } else if ((((dec.susan.getPosition() < (dec.susanpos3 + .01)) && (dec.susan.getPosition() > (dec.susanpos3 - .01)))) || (dec.susan.getPosition() == dec.susanlaunch3)) {
//                    if (dec.RC.matches("None")) {
//                        dec.susan.setPosition(dec.susanpos2);
//                        switchtime = runtime.milliseconds() + intakemovetime;
//                        Log.d("Sorting", "8");
//                    } else if (dec.LC.matches("None")) {
//                        dec.susan.setPosition(dec.susanpos1);
//                        switchtime = runtime.milliseconds();
//                        Log.d("Sorting", "9");
//                    }
//                } else {
//                    //dec.susan.setPosition(dec.susanpos1);
//                    switchtime = runtime.milliseconds();
//                    Log.d("Sorting", "11");
//                }
//            } else if (dec.RC.matches("None") && dec.LC.matches("None")) {
//                dec.susan.setPosition(dec.susanpos1);
//                switchtime = runtime.milliseconds();
//                Log.d("Sorting", "10");
//            } else {
//                if ((((dec.susan.getPosition() < (dec.susanlaunch1 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch1 - .01))) || ((dec.susan.getPosition() < (dec.susanbtw12 + .01)) && (dec.susan.getPosition() > (dec.susanbtw12 - .01))) )) {
//                    dec.susan.setPosition(dec.susanpos1);
//                    Log.d("Sorting", "21");
//                } else if ((((dec.susan.getPosition() < (dec.susanlaunch2 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch2 - .01))))) {
//                    dec.susan.setPosition(dec.susanpos2);
//                    Log.d("Sorting", "22");
//                } else if ((((dec.susan.getPosition() < (dec.susanlaunch3 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch3 - .01)))|| ((dec.susan.getPosition() < (dec.susanbtw23 + .01)) && (dec.susan.getPosition() > (dec.susanbtw23 - .01))) ))  {
//                    dec.susan.setPosition(dec.susanpos3);
//                    Log.d("Sorting", "23");
//                }
//            }
//        } else {
//            if ((Math.abs(gamepad2.right_trigger) < 0.1) && (Math.abs(gamepad2.left_trigger) < 0.1)) {
//                dec.intake.setPower(0.3);// was .4 .3
//            }
//            Log.d("Sorting", "20");
//            Log.d("NotSort", "");
//            /* */
//        }
//        //endregion
//
//        //region - Manual
//        if (gamepad2.dpad_left) {
//            if (dec.intaking) {
//                dec.susan.setPosition(dec.susanpos1);
//            } else {
//                dec.susan.setPosition(dec.susanlaunch1);
//            }
//        } else if (gamepad2.dpad_down) {
//            if (dec.intaking) {
//                dec.susan.setPosition(dec.susanpos2);
//            } else {
//                dec.susan.setPosition(dec.susanlaunch2);
//            }
//        } else if (gamepad2.dpad_right) {
//            if (dec.intaking) {
//                dec.susan.setPosition(dec.susanpos3);
//            } else {
//                dec.susan.setPosition(dec.susanlaunch3);
//            }
//        } else if (gamepad2.dpad_up) {
//            if (dec.susan.getPosition() < 0.2) {
//                dec.susan.setPosition(dec.susanpos1);
//            } else if (dec.susan.getPosition() < 0.6) {
//                dec.susan.setPosition(dec.susanpos2);
//            } else {
//                dec.susan.setPosition(dec.susanpos3);
//            }
//        }
//        //endregion
//
//        //region - Manual Transfer
//      //  if (!dec.launched) {
//            if (Math.abs(gamepad2.left_stick_y) > 0.2) {
//                dec.transfer.setPower(-gamepad2.left_stick_y);
//                dec.transferPower = -gamepad2.left_stick_y;
//
//            } /*else if (!transferon && !dec.launchy) { //marky, need launchy?
//                dec.transfer.setPower(0);
//                dec.transferPower = 0;
//            }*/
//       // }
//        //endregion
//
//        //region - Launch Mode
//        if (gamepad2.y && (a2time < runtime.milliseconds())) { //2.a //1.a //mark
//            a2time = runtime.milliseconds() + 1000;
//            Log.d("a2", "3");
//
//            if (a2) {
//                dec.launchy = true;
//                dec.firstlaunch = true;
//                dec.intaking = false; //mark
//
//                dec.LAUNCH = dec.odo.getPosition();
//
//                a2 = false;
//
//                Log.d("a2", "1");
//            } else {
//                dec.launchy = false;
//                dec.intaking = true;
//                dec.LAUNCH = dec.notlaunch;
//                dec.transfer.setPower(0);
//                transferon = false;
//                dec.transferPower = 0;
//                a2 = true;
//                Log.d("a2", "2");
//
//            }
//        }
//
//        dec.launchdist = Math.sqrt(Math.pow((dec.LAUNCH.getX(DistanceUnit.INCH) - dec.odo.getPosition().
//                getX(DistanceUnit.INCH)), 2) + Math.pow((dec.LAUNCH.getY(DistanceUnit.INCH) - dec.odo.
//                getPosition().getY(DistanceUnit.INCH)), 2)); //slow?
//
//        /*if (dec.launchdist > 10) {
//            dec.launchy = false;
//            dec.intaking = true;
//            dec.LAUNCH = dec.notlaunch;
//            //switchtime = runtime.milliseconds() + 500;
//
//            dec.transfer.setPower(0);
//            dec.transferPower = 0;
//            //dec.susan.setPosition(dec.susanpos1);
//        } else {
//            dec.intaking = false;
//        }*/
//        //endregion
//
//        //region - Save QR Code
//        if (dec.launchy && (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) < 20)) {
//            telemetryAprilTag();
//        }
//
//        if (!aprilTag.getDetections().isEmpty()) {
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
//        }
//
//        if (dec.goalcode0 != null) {
//            try {
//                if (dec.goalcode0.metadata.name.contains("Obelisk")) {
//                    if (dec.goalcode1 != null) {
//                        dec.goalcode = dec.goalcode1;
//                        dec.goaltime = runtime.milliseconds();
//                    }
//                } else {
//                    dec.goalcode = dec.goalcode0;
//                    dec.goaltime = runtime.milliseconds();
//                }
//            } catch (NullPointerException e) {
//                Log.d("CamE", "Lost GC0");
//            }
//        }
//
//        if ((runtime.milliseconds() - dec.goaltime) < 1000) {
//            dec.usePos.x = dec.goalcode.robotPose.getPosition().x; // gives error on get pos on a null object reference
//            dec.usePos.y = dec.goalcode.robotPose.getPosition().y;
//            dec.usePos.z = dec.goalcode.robotPose.getOrientation().getYaw(AngleUnit.DEGREES);
//        } else {
//            dec.usePos.x = dec.odo.getPosition().getX(DistanceUnit.INCH);
//            dec.usePos.y = dec.odo.getPosition().getY(DistanceUnit.INCH);
//            dec.usePos.z = dec.odo.getPosition().getHeading(AngleUnit.DEGREES);
//        }
//        //endregion
//
//        //region - Manual Hood
//        if (gamepad1.x) {
//            dec.hood.setPosition(dec.hood.getPosition() - 0.01);
//        } else if (gamepad1.b) {
//            dec.hood.setPosition(dec.hood.getPosition() + 0.01);
//        }
//
//        dec.hoffset = dec.hood.getPosition() - dec.launchHoodCalc(dec.goalDC(dec.usePos, dec.goal), dec.launchy); //dec.odo.getPosition()
//
//        if (gamepad1.left_stick_button) {
//            telemetry.addData("Hood Pos: ", dec.hood.getPosition());
//        }
//
//        if (dec.hoffset != 0) {
//            telemetry.addData("Hood Offset: ", dec.hoffset);
//        }
//        //endregion
//
//        //region - Reset Turret
//        if (gamepad1.dpad_right && (g1dtime < runtime.milliseconds())) {
//            g1dtime = runtime.milliseconds() + 250;
//
//            if (dec.fixing) {
//                dec.fixing = false;
//                dec.toffset = dec.taxon.getTotalRotation();
//            } else {
//                dec.fixing = true;
//                dec.taxon.setTargetRotation(0);
//            }
//
//            dec.turret.setPower(0);
//        }
//
//        if (dec.toffset != 0) {
//            telemetry.addData("T Offset: ", dec.toffset);
//        }
//
//        if (gamepad1.dpad_left && dec.fixing) {
//            dec.taxon.changeTargetRotation(-25);
//        }
//
//        if (dec.fixing) {
//            //telemetry.addData("-------FIXING TURRET-------", "");
//
//            if (dec.taxon.getTotalRotation() > dec.taxon.getTargetRotation()) {
//                dec.turret.setPower(1);
//            } else {
//                dec.turret.setPower(0);
//            }
//        }
//        //endregion
//
//        //region - Turret & Updates
//        dec.odo.update();
//        dec.taxon.update();
//
//        if (!dec.fixing) {
//            if (!dec.launchy) {
//                dec.taxon.setTargetRotation(dec.turretmiddle);
//            } else {
//                if (Math.abs(dec.setTurretAngle(dec.odo.getPosition(), dec.goal, dec.teamcolor) - dec.taxon.
//                        getTotalRotation()) > dec.ttol) { //100
//                    dec.taxon.setTargetRotation(dec.setTurretAngle(dec.odo.getPosition(), dec.goal, dec.
//                            teamcolor) + dec.toffset /*+ dec.camtaroffset + dec.cxoffset*/);
//                } else {
//                    dec.taxon.setTargetRotation(dec.taxon.getTotalRotation());
//                }
//            }
//
//            if (dec.launchy) {
//                dec.div = 60; //60, 60 is smooth, 10 is violent but 60 is too slow when bumped after arrived
//                dec.min = 1;
//                dec.z3 = 5;
//            } else {
//                dec.div = 40;
//                dec.min = 1.2;
//                dec.z3 = 10;
//            }
//
//            if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) > 120) { //200, 75, 90, 100
//                dec.turretpwrcoeff = 12.5; //10
//            } else if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) > dec.ttol) {
//                if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) < 30) {
//                    if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) > dec.z3) {
//                        dec.div = 20;
//                    }
//                } else {
//                    dec.div = dec.div / 2; //4?
//                }
//
//                dec.turretpwrcoeff = Range.clip((Math.abs(dec.taxon.getTotalRotation() -
//                        dec.taxon.getTargetRotation()) / dec.div), dec.min, 10); //1, 1.5, 1.2
//
//                if ((dec.goalDC(dec.usePos, dec.goal) > 108) || ((Math.abs(dec.taxon.getTotalRotation() -
//                        dec.taxon.getTargetRotation()) < dec.z3) /*&& dec.launchy*/)) { //>, 20
//                    dec.turretpwrcoeff = Range.clip((dec.turretpwrcoeff / 2), 1, 5); //dec.odo.getPosition()
//                }
//            } else {
//                dec.turretpwrcoeff = 0;
//            }
//
//            if (dec.launchy && (dec.angvel > 1)) {
//                dec.turretpwrcoeff = dec.turretpwrcoeff * 2;
//            }
//
//            dec.turretpower = Range.clip((dec.tminp * dec.turretpwrcoeff), 0.08, 1); //0.09, 7
//
//            if (dec.taxon.getTotalRotation() > (dec.taxon.getTargetRotation() + dec.ttol)) {
//                dec.turret.setPower(dec.turretpower);
//            } else if (dec.taxon.getTotalRotation() < (dec.taxon.getTargetRotation() - dec.ttol)) {
//                dec.turret.setPower(-dec.turretpower);
//            } else {
//                dec.turret.setPower(0);
//            }
//        }
//
//        //if (gamepad1.left_stick_button) {
//            telemetry.addData("Turret Target: ", dec.taxon.getTargetRotation());
//            telemetry.addData("Turret At: ", dec.taxon.getTotalRotation());
//            telemetry.addData("Turret Power: ", dec.turret.getPower());
//        //}
//        //endregion
//
//        //region - Susan Prep
//        if (gamepad2.right_stick_button) {
//            dec.susanprep = true;
//            Log.d("Prep Sus", "True");
//        }
//        if ((dec.susanprep)  && ((preptime < runtime.milliseconds()) || dec.transferCycle) && (!(((dec.susan.getPosition() <
//                (dec.susanbtw12 + 0.01)) && (dec.susan.getPosition() > (dec.susanbtw12 - 0.01))) ||
//                ((dec.susan.getPosition() < (dec.susanbtw23 + 0.01)) && (dec.susan.getPosition() >
//                        (dec.susanbtw23 - 0.01))))) && (((dec.susan.getPosition() < (dec.susanpos1 + 0.01)) && (dec.susan.getPosition() >
//                (dec.susanpos1 - 0.01))) || ((dec.susan.getPosition() < (dec.susanpos2 + 0.01)) && (dec.susan.getPosition() >
//                (dec.susanpos2 - 0.01))) || ((dec.susan.getPosition() < (dec.susanpos3 + 0.01)) && (dec.susan.getPosition() >
//                (dec.susanpos3 - 0.01))) || dec.transferCycle)) {
//
//            Log.d("Prep Time PRP", String.valueOf(preptime));
//            Log.d("Prep Time RNT", String.valueOf(runtime.milliseconds()));
//            Log.d("Prep FC", dec.FC);
//            Log.d("Prep RC", dec.RC);
//            Log.d("Prep LC", dec.LC);
//            Log.d("Prep Susan Pos pre", String.valueOf(dec.susan.getPosition()));
//            Log.d("Prep", "1");
//            Log.d("Prep Button", String.valueOf(gamepad2.right_stick_button));
//            Log.d("Prep Transfer Cycle", String.valueOf(dec.transferCycle));
//
//            dec.green = "None";
//            dec.purple = "None";
//            dec.purple2 = "None";
//            dec.green2 = "None";
//
//            if ((dec.FC.matches("Purple") && dec.RC.matches("Green")) ||
//                    (dec.FC.matches("Green") && dec.RC.matches("Purple"))) {
//                Log.d("Prep", "2");
//
//                if (dec.FC.matches("Green")) {
//                    Log.d("Prep", "3");
//                    dec.green = "Right";
//                    dec.purple = "Left";//swapped
//                } else {
//                    if (dec.RC.matches("Green")) {
//                        Log.d("Prep", "4");
//                        dec.green = "Left";
//                    }
//
//                    if (dec.FC.matches("Purple")) { //was lc now rc
//                        Log.d("Prep", "5");
//                        dec.purple = "Right"; //swapped
//                    } else {
//                        Log.d("Prep", "6");
//                        dec.purple = "Right";
//                    }
//                }
//
//                if ((dec.susan.getPosition() < (dec.susanpos1 + 0.01)) && (dec.susan.getPosition() >
//                        (dec.susanpos1 - 0.01))) {
//                    Log.d("Prep", "7");
//                    if ((dec.LC == "Purple" && dec.RC == "Green") || (dec.LC == "Green" && dec.RC == "Purple")) {
//                        dec.susan.setPosition(dec.susanbtw23);
//                        Log.d("Prep", "25");
//                        //added this
//                        if (dec.LC == "Purple"){
//                            dec.purple = "Left";
//                            dec.green = "Right";
//                            Log.d("Prep", "73");
//                        } else if (dec.LC == "Green"){
//                            dec.green = "Left";
//                            dec.purple = "Right";
//                            Log.d("Prep","74");
//                        }
//                        if (dec.FC == "Purple") {
//                            dec.purple2 = "Back";
//                            Log.d("Prep", "45");
//                        } else if (dec.FC == "Green") {
//                            dec.green2 = "Back";
//                            Log.d("Prep", "46");
//                        }
//                    } else {
//                        dec.susan.setPosition(dec.susanbtw12);
//                        Log.d("Prep", "26");
//                        if (dec.FC == "Purple") {
//                            dec.purple = "Left";
//                            Log.d("Prep", "29");
//                            if (dec.LC == "Green") {
//                                dec.green = "Right";
//                                Log.d("Prep", "30");
//
//                            }
//                        } else if (dec.FC == "Green") {
//                            dec.green = "Left";
//                            Log.d("Prep", "31");
//
//                            if (dec.LC == "Purple") {
//                                dec.purple = "Right";
//                                Log.d("Prep", "32");
//
//                            }
//                        }
//
//                        if (dec.RC == "Purple") {
//                            dec.purple2 = "Back";
//                            Log.d("Prep", "47");
//                        } else if (dec.RC == "Green") {
//                            dec.green2 = "Back";
//                            Log.d("Prep", "48");
//                        }
//                    }
//                } else if ((dec.susan.getPosition() < (dec.susanpos2 + 0.01)) && (dec.susan.getPosition() >
//                        (dec.susanpos2 - 0.01))) {
//                    Log.d("Prep", "8");
//                    dec.susan.setPosition(dec.susanbtw12);
//                    if (dec.LC == "Purple") {
//                        dec.purple2 = "Back";
//                        Log.d("Prep", "49");
//                    } else if (dec.LC == "Green") {
//                        dec.green2 = "Back";
//                        Log.d("Prep", "50");
//                    }
//                } else if ((dec.susan.getPosition() < (dec.susanpos3 + 0.01)) && (dec.susan.getPosition() >
//                        (dec.susanpos3 - 0.01))) {
//                    Log.d("Prep", "9");
//                    dec.susan.setPosition(dec.susanbtw23);
//                    if (dec.LC == "Purple") {
//                        dec.purple2 = "Back";
//                        Log.d("Prep", "51");
//                    } else if (dec.LC == "Green") {
//                        dec.green2 = "Back";
//                        Log.d("Prep", "52");
//                    }
//                }
//            } else if ((dec.FC.matches("Purple") && dec.LC.matches("Green")) ||
//                    (dec.FC.matches("Green") && dec.LC.matches("Purple"))) {
//                Log.d("Prep", "10");
//                if (dec.FC.matches("Green")) {
//                    Log.d("Prep", "11");
//                    dec.green = "Left";
//                    dec.purple = "Right";
//                } else {
//                    Log.d("Prep", "12");
//                    dec.green = "Right";
//                    dec.purple = "Left";
//                }
//
//                if ((dec.susan.getPosition() < (dec.susanpos1 + 0.01)) && (dec.susan.getPosition() >
//                        (dec.susanpos1 - 0.01))) {
//                    Log.d("Prep", "13");
//                    dec.susan.setPosition(dec.susanbtw12);
//                    if (dec.RC == "Purple") {
//                        dec.purple2 = "Back";
//                        Log.d("Prep", "53");
//                    } else if (dec.RC == "Green") {
//                        dec.green2 = "Back";
//                        Log.d("Prep", "54");
//                    }
//                } else if ((dec.susan.getPosition() < (dec.susanpos2 + 0.01)) && (dec.susan.getPosition() >
//                        (dec.susanpos2 - 0.01))) {
//                    Log.d("Prep", "14");
//                    dec.susan.setPosition(dec.susanbtw23);
//                    if (dec.RC == "Purple") {
//                        dec.purple2 = "Back";
//                        Log.d("Prep", "55");
//                    } else if (dec.RC == "Green") {
//                        dec.green2 = "Back";
//                        Log.d("Prep", "56");
//                    }
//                } else if ((dec.susan.getPosition() < (dec.susanpos3 + 0.01)) && (dec.susan.getPosition() >
//                        (dec.susanpos3 - 0.01))) {
//                    Log.d("Prep", "15");
//                    if ((dec.LC == "Purple" && dec.RC == "Green") || (dec.LC == "Green" && dec.RC == "Purple")) {
//                        dec.susan.setPosition(dec.susanbtw12);
//                        Log.d("Prep", "27");
//                        if (dec.LC == "Green") {
//                            dec.purple = "Right";
//                            dec.green = "Left";
//
//                            Log.d("Prep", "72");
//                        }
//                        if (dec.LC == "Purple") { //edited
//                            dec.purple = "Left";
//                            dec.green = "Right";
//
//                            Log.d("Prep", "72");
//                        }
//                        if (dec.FC == "Purple") {
//                            dec.purple2 = "Back";
//                            Log.d("Prep", "57");
//                        } else if (dec.FC == "Green") {
//                            dec.green2 = "Back";
//                            Log.d("Prep", "58");
//                        }
//                    } else {
//                        dec.susan.setPosition(dec.susanbtw23);
//                        Log.d("Prep", "28");
//                        if (dec.LC == "Purple") {
//                            dec.purple2 = "Back";
//                            Log.d("Prep", "60");
//                        } else if (dec.LC == "Green") {
//                            dec.green2 = "Back";
//                            Log.d("Prep", "61");
//                        }
//                    }
//                }
//            } else if ((dec.RC.matches("Purple") && dec.LC.matches("Green")) ||
//                    (dec.RC.matches("Green") && dec.LC.matches("Purple"))) {
//                Log.d("Prep", "16");
//                if (dec.RC.matches("Green")) {
//                    Log.d("Prep", "17");
//                    dec.green = "Left";
//                    if (dec.FC == "Purple") {
//                        dec.purple = "Right";
//                    } else if (dec.FC == "None") {
//                        dec.purple = "Right 2";
//                    }
//                } else if (dec.RC.matches("Purple")) {
//                    Log.d("Prep", "33");
//                    dec.purple = "Left";
//                    if (dec.FC == "Green") {
//                        dec.green = "Right";
//                    } else if (dec.FC == "None") {
//                        dec.green = "Right 2";
//                    }
//                } else {
//                    Log.d("Prep", "18");
//                    dec.green = "Right";
//                    dec.purple = "Left";
//                }
//
//                if ((dec.susan.getPosition() < (dec.susanpos1 + 0.01)) && (dec.susan.getPosition() >
//                        (dec.susanpos1 - 0.01))) {
//                    Log.d("Prep", "19");
//                    dec.susan.setPosition(dec.susanbtw23);
//                    if ((dec.LC == "Green") && (dec.RC == "Purple")) {
//                        dec.green = "Left";
//                        dec.purple = "Right";
//                        Log.d("Prep", "41");
//                    } else if ((dec.LC == "Purple") && (dec.RC == "Green")) {
//                        dec.green = "Right";
//                        dec.purple = "Left";
//                        Log.d("Prep", "35");
//                    }
//
//                    if (dec.FC == "Purple") {
//                        dec.purple2 = "Back";
//                        Log.d("Prep", "62");
//                    } else if (dec.FC == "Green") {
//                        dec.green2 = "Back";
//                        Log.d("Prep", "63");
//                    }
//                } else if ((dec.susan.getPosition() < (dec.susanpos2 + 0.01)) && (dec.susan.getPosition() >
//                        (dec.susanpos2 - 0.01))) {
//                    Log.d("Prep", "20");
//                    if ((dec.LC == "Purple" && dec.FC == "Green") || (dec.LC == "Green" && dec.FC == "Purple")) {
//                        dec.susan.setPosition(dec.susanbtw23);
//                        Log.d("Prep", "22");
//                        if ((dec.FC == "Green") && (dec.LC == "Purple")) {
//                            dec.green = "Left";
//                            dec.purple = "Right";
//                            Log.d("Prep", "36");
//                        } else if ((dec.FC == "Purple") && (dec.LC == "Green")) {
//                            dec.green = "Right";
//                            dec.purple = "Left";
//                            Log.d("Prep", "37");
//                        }
//
//                        if (dec.RC == "Purple") {
//                            dec.purple2 = "Back";
//                            Log.d("Prep", "64");
//                        } else if (dec.RC == "Green") {
//                            dec.green2 = "Back";
//                            Log.d("Prep", "65");
//                        }
//                    } else {
//                        dec.susan.setPosition(dec.susanbtw12);
//                        Log.d("Prep", "23");
//                        Log.d("Prep", dec.FC);
//                        Log.d("Prep", dec.RC);
//                        Log.d("Prep", dec.LC);
//
//                        if ((dec.FC == "Green") && (dec.RC == "Purple")) {
//                            dec.green = "Left";
//                            dec.purple = "Right";
//                            Log.d("Prep", "34");
//
//                            if (dec.LC == "Purple") {
//                                dec.purple2 = "Back";
//                                Log.d("Prep", "66");
//                            } else if (dec.LC == "Green") {
//                                dec.green2 = "Back";
//                                Log.d("Prep", "67");
//                            }
//                        } else if ((dec.FC == "Purple") && (dec.RC == "Green")) {
//                            dec.green = "Right";
//                            dec.purple = "Left";
//                            Log.d("Prep", "42");
//                            if (dec.LC == "Purple") {
//                                dec.purple2 = "Back";
//                                Log.d("Prep", "68");
//                            } else if (dec.LC == "Green") {
//                                dec.green2 = "Back";
//                                Log.d("Prep", "69");
//                            }
//                        } else if (dec.FC == "None") {
//                            Log.d("Prep", "43");
//                            if (dec.RC == "Purple") {
//                                dec.purple = "Left";
//                                dec.green = "Right 2";
//                                Log.d("Prep", "44");
//                            } else if (dec.RC == "Green") {
//                                dec.green = "Left";
//                                dec.purple = "Right 2";
//                                Log.d("Prep", "38");
//                            }
//                        }
//                    }
//                } else if ((dec.susan.getPosition() < (dec.susanpos3 + 0.01)) && (dec.susan.getPosition() >
//                        (dec.susanpos3 - 0.01))) {
//                    Log.d("Prep", "21");
//                    dec.susan.setPosition(dec.susanbtw12);
//                    if ((dec.LC == "Green") && (dec.RC == "Purple")) {
//                        dec.green = "Left";
//                        dec.purple = "Right";
//                        Log.d("Prep", "39");
//                    } else if ((dec.LC == "Purple") && (dec.RC == "Green")) {
//                        dec.green = "Right";
//                        dec.purple = "Left";
//                        Log.d("Prep", "40");
//                    }
//
//                    if (dec.FC == "Purple") {
//                        dec.purple2 = "Back";
//                        Log.d("Prep", "70");
//                    } else if (dec.FC == "Green") {
//                        dec.green2 = "Back";
//                        Log.d("Prep", "71");
//                    }
//                }
//            }
//
//            Log.d("Prep Check Purple", dec.purple);
//            Log.d("Prep Check Green", dec.green);
//            Log.d("Prep Check Purple 2", dec.purple2);
//            Log.d("Prep Check Green 2", dec.green2);
//
//            Log.d("Prep Sus", "False");
//            dec.susanprep = false;
//            dec.transferCycle = false;
//            preptime = runtime.milliseconds() + 2500;
//        } else {
//            Log.d("Time PRP", String.valueOf(preptime));
//            Log.d("Time RNT", String.valueOf(runtime.milliseconds()));
//            Log.d("Susan Pos", String.valueOf(dec.susan.getPosition()));
//        }
//        //endregion
//
//        //region - Launch Green
//        if ((gamepad2.b)) {
//            telemetry.addData("LaunchGreen FC", dec.FC);
//            telemetry.addData("LaunchGreen RC", dec.RC);
//            telemetry.addData("LaunchGreen LC", dec.LC);
//
//            Log.d("LaunchGreen", "1");
//            Log.d("Susan", String.valueOf(dec.susan.getPosition()));
//            Log.d("Time LT", String.valueOf(launchtime));
//            Log.d("Time RPD", String.valueOf(rapidtime));
//            Log.d("Time RNT", String.valueOf(runtime.milliseconds()));
//
//            if (launchtime > runtime.milliseconds()) {
//                telemetry.addData("launchtime", "paused");
//            }
//
//            if (Math.abs(gamepad2.left_stick_y) < 0.2 && !dec.transferCycle) {
//
//                dec.transfer.setPower(1); //marky
//                transferon = true;
//            }
//            if ((launchtime < runtime.milliseconds()) && (rapidtime < runtime.milliseconds())) {
//
//                Log.d("LaunchGreen", "2");
//                telemetry.addData("launchtime", "GO");
//
//                if (((dec.susan.getPosition() < (dec.susanbtw12 + 0.02)) && (dec.susan.getPosition() >
//                        (dec.susanbtw12 - 0.02)))) {
//                    launchtime = runtime.milliseconds();
//                    dec.launched = true;
//                    Log.d("LaunchGreen", "11");
//
//                    if ((dec.green == "Right")) {
//                        dec.susan.setPosition(dec.susanlaunch2); // switch switch
//                        launchtime = runtime.milliseconds() + colorlongmovetime;
//                        dec.launched = true;
//                        dec.greenLaunched = true;
//
//                        Log.d("LaunchGreen", "13");
//                    } else if (dec.green == "Left") {
//                        dec.susan.setPosition(dec.susanlaunch1); //switch switch
//                        launchtime = runtime.milliseconds() + colormovetime;
//                        dec.launched = true;
//                        dec.greenLaunched = true;
//
//                        Log.d("LaunchGreen", "14");
//                    } else if (dec.green == "Right 2") {
//                        dec.susan.setPosition(dec.susanlaunch3); //switch switch
//                        launchtime = runtime.milliseconds() + colorlongmovetime;
//                        dec.launched = true;
//                        dec.greenLaunched = true;
//
//                        Log.d("LaunchGreen", "24");
//                    } else {
//                        dec.susan.setPosition(dec.susanbtw12);
//                        rapidtime = runtime.milliseconds() + 500; //500?
//                        telemetry.addData("no", "green");
//                        Log.d("LaunchGreen", "16");
//                    }
//                } else if ((dec.susan.getPosition() < (dec.susanbtw23 + 0.02)) && (dec.susan.getPosition() >
//                        (dec.susanbtw23 - 0.02))) {
//                    launchtime = runtime.milliseconds();
//                    dec.launched = true;
//                    Log.d("LaunchGreen", "20");
//
//                    if ((dec.green == "Right")) {
//                        dec.susan.setPosition(dec.susanlaunch3); //
//                        launchtime = runtime.milliseconds() + colorlongmovetime;
//                        dec.launched = true;
//                        dec.greenLaunched = true;
//
//                        Log.d("LaunchGreen", "21");
//                    } else if (dec.green == "Left") {
//                        dec.susan.setPosition(dec.susanlaunch2); //
//                        launchtime = runtime.milliseconds() + colormovetime;
//                        dec.launched = true;
//                        dec.greenLaunched = true;
//
//                        Log.d("LaunchGreen", "22");
//                    } else {
//                        dec.susan.setPosition(dec.susanbtw23);
//                        rapidtime = runtime.milliseconds() + 500; //500?
//                        telemetry.addData("no", "green");
//                        Log.d("LaunchGreen", "23");
//                    }
//                } else if (dec.purpleLaunched) {
//                    Log.d("LaunchGreen", "30");
//                    if (!(dec.purple == "Right 2")) {
//                        if (((dec.susan.getPosition() < (dec.susanlaunch1 + 0.02)) && (dec.susan.getPosition() >
//                                (dec.susanlaunch1 - 0.02))) || ((dec.susan.getPosition() < (dec.susanlaunch3 + 0.02))
//                                && (dec.susan.getPosition() > (dec.susanlaunch3 - 0.02)))) {
//                            dec.susan.setPosition(dec.susanlaunch2);
//                            launchtime = runtime.milliseconds() + colormovetime;
//                            dec.launched = true;
//                            dec.greenLaunched = true;
//
//                            Log.d("LaunchGreen", "27");
//                        } else if (((dec.susan.getPosition() < (dec.susanlaunch2 + 0.02)) && (dec.susan.getPosition()
//                                > (dec.susanlaunch2 - 0.02)))) {
//                            if (dec.green == "Right") {
//                                dec.susan.setPosition(dec.susanlaunch3); //swapped from 3, 1
//                                launchtime = runtime.milliseconds() + colormovetime;
//                                dec.launched = true;
//                                dec.greenLaunched = true;
//
//                                Log.d("LaunchGreen", "28");
//                            } else if (dec.green == "Left") {
//                                dec.susan.setPosition(dec.susanlaunch1); //swapped from 1, 3
//                                launchtime = runtime.milliseconds() + colormovetime;
//                                dec.launched = true;
//                                dec.greenLaunched = true;
//
//                                Log.d("LaunchGreen", "29");
//                            }
//                        }
//                    } else {
//                        dec.susan.setPosition(dec.susanlaunch1);
//                        launchtime = runtime.milliseconds() + colormovetime;
//                        dec.launched = true;
//                        dec.greenLaunched = true;
//
//                        Log.d("LaunchGreen", "31");
//                    }
//                } else if (dec.greenLaunched) {
//                    Log.d("LaunchGreen", "32");
//                    if (((dec.susan.getPosition() < (dec.susanlaunch1 + 0.02)) && (dec.susan.getPosition() >
//                            (dec.susanlaunch1 - 0.02))) || ((dec.susan.getPosition() < (dec.susanlaunch3 + 0.02))
//                            && (dec.susan.getPosition() > (dec.susanlaunch3 - 0.02)))) {
//                        if(dec.transferCycle) {
//                            dec.susanprep = true;
//                            Log.d("LaunchPuprle", "Prepped");
//                        }
//                        dec.transferCycle = true;
//                        dec.transfer.setPower(0);
//                        launchtime = runtime.milliseconds() + 1000;
//
//
//                        //add here
//                        Log.d("LaunchGreen", "33");
//                    } else if (((dec.susan.getPosition() < (dec.susanlaunch2 + 0.02)) && (dec.susan.getPosition()
//                            > (dec.susanlaunch2 - 0.02)))) {
//                        if (dec.green == "Right") {
//                            dec.susan.setPosition(dec.susanlaunch3);
//                            launchtime = runtime.milliseconds() + colormovetime;
//                            dec.launched = true;
//                            dec.greenLaunched = true;
//
//                            Log.d("LaunchGreen", "34");
//                        } else if (dec.green == "Left") {
//                            dec.susan.setPosition(dec.susanlaunch1);
//                            launchtime = runtime.milliseconds() + colormovetime;
//                            dec.launched = true;
//                            dec.greenLaunched = true;
//
//                            Log.d("LaunchGreen", "35");
//                        }
//                    }
//                } else {
//                    Log.d("LaunchGreen", "26");
//                }
//            }
//
//            Log.d("Launch", "Timer: " + ((launchtime + 500) < runtime.milliseconds()));
//        } /*else if (((launchtime + launchDelay) > runtime.milliseconds())) {
//            dec.transferPower = 1;
//            dec.transfer.setPower(1);
//
//            Log.d("Transfer", "Transfer On");
//        } else {
//            dec.launched = false;
//        }*/
//        //endregion
//
//        //region - Launch Purple
//        if ((gamepad2.x)) {
//            telemetry.addData("LaunchPurple FC", dec.FC);
//            telemetry.addData("LaunchPurple RC", dec.RC);
//            telemetry.addData("LaunchPurple LC", dec.LC);
//
//            Log.d("LaunchPurple", "1");
//            Log.d("Susan", String.valueOf(dec.susan.getPosition()));
//            Log.d("Time LT", String.valueOf(launchtime));
//            Log.d("Time RPD", String.valueOf(rapidtime));
//            Log.d("Time RNT", String.valueOf(runtime.milliseconds()));
//
//            if (launchtime > runtime.milliseconds()) {
//                telemetry.addData("launchtime", "paused");
//            }
//            if (Math.abs(gamepad2.left_stick_y) < 0.2 && !dec.transferCycle) {
//
//                dec.transfer.setPower(1); //marky
//                transferon = true;
//            }
//
//
//            if ((launchtime < runtime.milliseconds()) && (rapidtime < runtime.milliseconds())) {
//
//                Log.d("LaunchPurple", "2");
//                telemetry.addData("launchtime", "GO");
//
//                if ((dec.susan.getPosition() < (dec.susanbtw12 + 0.02)) && (dec.susan.getPosition() >
//                        (dec.susanbtw12 - 0.02))) {
//                    launchtime = runtime.milliseconds();
//                    dec.launched = true;
//                    Log.d("LaunchPurple", "11");
//
//                    if ((dec.purple == "Right")) {
//                        dec.susan.setPosition(dec.susanlaunch2); // switch switch
//                        launchtime = runtime.milliseconds() + colorlongmovetime;
//                        dec.launched = true;
//                        dec.purpleLaunched = true;
//
//                        Log.d("LaunchPurple", "13");
//                    } else if (dec.purple == "Left") {
//                        dec.susan.setPosition(dec.susanlaunch1); //switch switch
//                        launchtime = runtime.milliseconds() + colormovetime;
//                        dec.launched = true;
//                        dec.purpleLaunched = true;
//
//                        Log.d("LaunchPurple", "14");
//                    } else if (dec.purple == "Right 2") {
//                        dec.susan.setPosition(dec.susanlaunch3); //switch switch
//                        launchtime = runtime.milliseconds() + colorlongmovetime;
//                        dec.launched = true;
//                        dec.purpleLaunched = true;
//
//                        Log.d("LaunchPurple", "24");
//                    } else {
//                        dec.susan.setPosition(dec.susanbtw12);
//                        rapidtime = runtime.milliseconds() + 500; //500?
//                        telemetry.addData("no", "purple");
//                        Log.d("LaunchPurple", "16");
//                    }
//                } else if ((dec.susan.getPosition() < (dec.susanbtw23 + 0.02)) && (dec.susan.getPosition() >
//                        (dec.susanbtw23 - 0.02))) {
//                    launchtime = runtime.milliseconds();
//                    dec.launched = true;
//                    Log.d("LaunchPurple", "20");
//
//                    if ((dec.purple == "Right")) {
//                        dec.susan.setPosition(dec.susanlaunch3); //
//                        launchtime = runtime.milliseconds() + colorlongmovetime;
//                        dec.launched = true;
//                        dec.purpleLaunched = true;
//
//                        Log.d("LaunchPurple", "21");
//                    } else if (dec.purple == "Left") {
//                        dec.susan.setPosition(dec.susanlaunch2); //
//                        launchtime = runtime.milliseconds() + colormovetime;
//                        dec.launched = true;
//                        dec.purpleLaunched = true;
//
//                        Log.d("LaunchPurple", "22");
//                    } else {
//                        dec.susan.setPosition(dec.susanbtw23);
//                        rapidtime = runtime.milliseconds() + 500; //500?
//                        telemetry.addData("no", "purple");
//                        Log.d("LaunchPurple", "23");
//                    }
//                } else if (dec.greenLaunched) {
//                    Log.d("LaunchPurple", "30");
//
//                    if (!(dec.green == "Right 2")) {
//                        if (((dec.susan.getPosition() < (dec.susanlaunch1 + 0.02)) && (dec.susan.getPosition() >
//                                (dec.susanlaunch1 - 0.02))) || ((dec.susan.getPosition() < (dec.susanlaunch3 +
//                                0.02)) && (dec.susan.getPosition() > (dec.susanlaunch3 - 0.02)))) {
//                            dec.susan.setPosition(dec.susanlaunch2);
//                            launchtime = runtime.milliseconds() + colormovetime;
//                            dec.launched = true;
//                            dec.purpleLaunched = true;
//
//                            Log.d("LaunchPurple", "27");
//                        } else if (((dec.susan.getPosition() < (dec.susanlaunch2 + 0.02)) && (dec.susan.getPosition()
//                                > (dec.susanlaunch2 - 0.02)))) {
//                            if (dec.purple == "Right") {
//                                dec.susan.setPosition(dec.susanlaunch3); //Correct for some
//                                launchtime = runtime.milliseconds() + colormovetime;
//                                dec.launched = true;
//                                dec.purpleLaunched = true;
//
//                                Log.d("LaunchPurple", "28");
//                            } else if (dec.purple == "Left") {
//                                dec.susan.setPosition(dec.susanlaunch1); //swapped from 1, 3
//                                launchtime = runtime.milliseconds() + colormovetime;
//                                dec.launched = true;
//                                dec.purpleLaunched = true;
//
//                                Log.d("LaunchPurple", "29");
//                            }
//                        }
//                    } else {
//                        dec.susan.setPosition(dec.susanlaunch1);
//                        launchtime = runtime.milliseconds() + colormovetime;
//                        dec.launched = true;
//                        dec.purpleLaunched = true;
//
//                        Log.d("LaunchPurple", "31");
//                    }
//                } else if (dec.purpleLaunched) {
//                    Log.d("LaunchPurple", "32");
//                    if (((dec.susan.getPosition() < (dec.susanlaunch1 + 0.02)) && (dec.susan.getPosition() >
//                            (dec.susanlaunch1 - 0.02))) || ((dec.susan.getPosition() < (dec.susanlaunch3 + 0.02))
//                            && (dec.susan.getPosition() > (dec.susanlaunch3 - 0.02)))) {
//                        if(dec.transferCycle) {
//                            dec.susanprep = true;
//                            Log.d("LaunchPuprle", "Prepped");
//                        }
//                        dec.transferCycle = true;
//                        dec.transfer.setPower(0);
//                        launchtime = runtime.milliseconds() + 1000;
//
//
//                        /*
//                        if(!dec.transferCycle) {
//                            transfertime = runtime.milliseconds() + 800;
//                        }
//                        dec.transferCycle = true;
//                        dec.transferPower = 0;
//
//                        if (transfertime < runtime.milliseconds()){
//                            if(((dec.susan.getPosition() < (dec.susanlaunch1 + 0.02)) && (dec.susan.getPosition() >
//                                    (dec.susanlaunch1 - 0.02)))){
//                                dec.susan.setPosition(dec.susanbtw23);
//
//                            } else if(((dec.susan.getPosition() < (dec.susanlaunch3 + 0.02)) && (dec.susan.getPosition() >
//                                    (dec.susanlaunch3 - 0.02)))){
//                                dec.susan.setPosition(dec.susanbtw12);
//
//                            }
//
//                        }
//
//
//                                        */
//
//                        //add here
//
//                        Log.d("LaunchPurple", "33");
//                    } else if (((dec.susan.getPosition() < (dec.susanlaunch2 + 0.02)) && (dec.susan.getPosition()
//                            > (dec.susanlaunch2 - 0.02)))) {
//                        if (dec.purple == "Right") {
//                            dec.susan.setPosition(dec.susanlaunch3);
//                            launchtime = runtime.milliseconds() + colormovetime;
//                            dec.launched = true;
//                            dec.purpleLaunched = true;
//
//                            Log.d("LaunchPurple", "34");
//                        } else if (dec.purple == "Left") {
//                            dec.susan.setPosition(dec.susanlaunch1);
//                            launchtime = runtime.milliseconds() + colormovetime;
//                            dec.launched = true;
//                            dec.purpleLaunched = true;
//
//                            Log.d("LaunchPurple", "35");
//                        }
//                    } /*else if((transfertime + 500) < runtime.milliseconds()){
//                            dec.transferCycle = false;
//                        }*/
//
//                } else {
//                    Log.d("LaunchPurple", "26");
//                }
//            }
//
//            Log.d("Launch", "Timer: " + ((launchtime + 500) < runtime.milliseconds()));
//        } /*else if (((launchtime + launchDelay) > runtime.milliseconds())) {
//            dec.transferPower = 1;
//            dec.transfer.setPower(1);
//
//            Log.d("Transfer", "Transfer On");
//        } else {
//            dec.launched = false;
//        }*/
//        //endregion
//
//        //region - Telemetry
//        telemetry.addData("FC", dec.FC);
//        telemetry.addData("RC", dec.RC);
//        telemetry.addData("LC", dec.LC);
//
//        telemetry.addData("green is", dec.green);
//        telemetry.addData("purple is", dec.purple);
//        telemetry.addData("purple 2 is", dec.purple2);
//        telemetry.addData("green 2 is", dec.green2);
//        telemetry.addData("RColor Value", r_num);
//        telemetry.addData("LColor Value", l_num);
//        telemetry.addData("FColor Value", f_num);
//        telemetry.addData("RColor Sat", r_sat);
//        telemetry.addData("LColor Sat", l_sat);
//        telemetry.addData("FColor Sat", f_sat);
//
//        telemetry.addData("RColor Bri", r_bri);
//        telemetry.addData("LColor Bri", l_bri);
//        telemetry.addData("FColor Bri", f_bri);
//
//
//
//        telemetry.addData("Susan Pos is", dec.susan.getPosition());
//
//        telemetry.addData("Park POS", dec.parkPOS);
//
//        telemetry.addData("revf r", f2c.red);
//        telemetry.addData("revf g", f2c.green);
//        telemetry.addData("revf b", f2c.blue);
//        telemetry.addData("revr r", r2c.red);
//        telemetry.addData("revr g", r2c.green);
//        telemetry.addData("revr b", r2c.blue);
//        telemetry.addData("revf dis", f2.getDistance(DistanceUnit.CM));
//        telemetry.addData("brushlandf purple",dec.f0.getState());
//        telemetry.addData("brushlandf green", dec.f1.getState());
//        telemetry.addData("brushlandr purple",dec.r0.getState());
//        telemetry.addData("brushlandr green", dec.r1.getState());
//
//
//        telemetry.addLine(String.format("OdoPos: %6.2f %6.2f %6.2f (-90)", dec.odo.getPosition().getX(DistanceUnit.INCH),
//                dec.odo.getPosition().getY(DistanceUnit.INCH), (dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) - 90));
//        telemetry.addLine(String.format("Robo Pos (Cam): %6.2f %6.2f %6.2f ", dec.robopos.x, dec.robopos.y, dec.robopos.z));
//        telemetry.update();
//        //endregion
//    }
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
//        /*if (dec.goalcode != null) {
//            telemetry.addData("Goal Code Center X: ", dec.goalcode.center.x);
//        }
//
//        telemetry.addData("# AprilTags Detected", currentDetections.size());*/
//
//        // Step through the list of detections and display info for each one.
//        for (AprilTagDetection detection : currentDetections) {
//            if (detection.metadata != null) {
//                //telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
//                // Only use tags that don't have Obelisk in them
//                if (!detection.metadata.name.contains("Obelisk")) {
//                    dec.campos.x = detection.robotPose.getPosition().x;
//                    dec.campos.y = detection.robotPose.getPosition().y;
//                    dec.campos.z = detection.robotPose.getOrientation().getYaw(AngleUnit.DEGREES);
//
//                    //telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f ", dec.campos.x, dec.campos.y, dec.campos.z));
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
//            }/* else {
//                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
//                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
//            }*/
//        }   // end for() loop
//
//        // Add "key" information to telemetry
//        /*telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
//        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");*/
//
//    }
//}
