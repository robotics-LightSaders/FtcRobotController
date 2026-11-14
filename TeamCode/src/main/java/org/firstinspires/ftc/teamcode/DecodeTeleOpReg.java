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
//import com.qualcomm.robotcore.hardware.Gamepad;
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
//import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
//import org.firstinspires.ftc.vision.VisionPortal;
//import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
//import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
//
//import java.util.List;
//
//@TeleOp(name = "TO - Regionals", group = "TeleOp")
//@Disabled
//public class DecodeTeleOpReg extends OpMode {
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
//    private ElapsedTime runtime = new ElapsedTime();
//
//    double x;
//    double y;
//    double z;
//
//    double g1dtime = 0;
//    double switchtime = 0;
//    double launchtime = -2000;
//    double rapidtime = 0;
//
//    double colormovetime = 600;
//    double colorlongmovetime = 800;
//    double preptime = 0;
//    double sortTransferTime = 0;
//    double endLaunchTime;
//
//    boolean a2 = true;
//    double a2time = 0;
//    boolean transferon = false;
//    double colortime = 0;
//    boolean firstloop = true;
//    boolean reloc = false;
//    boolean isPrep;
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
//        dec.camrot = hardwareMap.get(Servo.class, "camrot");
//        dec.park = hardwareMap.get(Servo.class, "park");
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
//        //dec.initTaxon();
//        dec.initOQ();
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
//                dec.PIDFc.p, dec.PIDFc.i, dec.PIDFc.d, dec.PIDFc.f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
//        ));
//
//        dec.fl.setPower(0);
//        dec.fr.setPower(0);
//        dec.bl.setPower(0);
//        dec.br.setPower(0);
//
//        dec.launch.setVelocity(0);
//        dec.intake.setPower(0);
//        dec.transfer.setPower(0);
//        dec.transferPower = 0;
//        dec.susanV = dec.susanenc.getVoltage();
//        dec.turret.setPower(0);
//
//        dec.configureOdo(false);
//        initAprilTag();
//
//        runtime.reset();
//    }
//
//    public void init_loop() {
//        if (gamepad1.x) {
//            dec.teamcolor = "Blue";
//            dec.goal = dec.Bgoal;
//        } else if (gamepad1.b) {
//            dec.teamcolor = "Red";
//            dec.goal = dec.Rgoal;
//        }
//
//        if (gamepad1.y) {
//            if (dec.teamcolor.matches("Red")) {
//                dec.autoend = dec.Rexe;
//            } else {
//                dec.autoend = dec.Bexe;
//            }
//        } else if (gamepad1.a) {
//            if (dec.teamcolor.matches("Red")) {
//                dec.autoend = dec.RsabC1;
//            } else {
//                dec.autoend = dec.BsabC1;
//            }
//        }
//
//        if (gamepad1.left_trigger > 0.2) {
//            if (dec.teamcolor.matches("Red")) {
//                dec.odo.setPosition(dec.autoend);
//            } else {
//                dec.odo.setPosition(dec.autoend);
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
//        if (firstloop) {
//            dec.susan.setPosition(dec.susanpos1);
//            dec.parkPOS = dec.parkUp;
//            firstloop = false;
//        }
//
//        //region - Velocities
//        dec.angvel = Math.abs(dec.odo.getHeadingVelocity(UnnormalizedAngleUnit.DEGREES));
//        dec.xvel = Math.abs(dec.odo.getVelX(DistanceUnit.INCH));
//        dec.yvel = Math.abs(dec.odo.getVelY(DistanceUnit.INCH));
//        //endregion
//
//        //region - Odo Reset
//        if (gamepad1.y && reloc && (dec.turret.getPower() < 0.1) && (dec.angvel < 1) && (dec.xvel < 1) && (dec.yvel < 1)) { //AV < 2?
//            dec.robopos = dec.RP(dec.campos, dec.tpos, dec.toffset, dec.odo.getPosition());
//            dec.odo.setPosition(new Pose2D(DistanceUnit.INCH, dec.robopos.x, dec.robopos.y, AngleUnit.DEGREES, dec.robopos.z));
//        }
//
//        if (((dec.odo.getPosition().getX(DistanceUnit.INCH) > 20) && (dec.newdist < 3)) || //5?
//                (dec.odo.getPosition().getX(DistanceUnit.INCH) <= 20)) {
//            reloc = true;
//        } else {
//            reloc = false;
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
//        //region - Color
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
//        if ((gamepad2.y || dec.intaking || gamepad2.a || dec.presort || dec.transferCycle) && dec.canscan) {
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
//        if (switchtime < runtime.milliseconds()) {
//            if ((!dec.LC.matches("None")) && (!dec.RC.matches("None")) && (!dec.FC.matches("None"))) {
//                dec.balls = 3;
//                //dec.susan.setPosition(dec.susanpast3);
//                //dec.canscan = false;
//            } else if (((!dec.FC.matches("None")) && (!dec.RC.matches("None"))) || ((!dec.FC.matches("None"))
//                    && (!dec.LC.matches("None"))) || ((!dec.LC.matches("None")) && (!dec.RC.matches("None")))) {
//                dec.balls = 2;
//            } else if ((!dec.LC.matches("None")) || (!dec.RC.matches("None")) || (!dec.FC.matches("None"))) {
//                dec.balls = 1;
//            } else {
//                dec.balls = 0;
//            }
//        } else {
//            dec.balls = dec.preballs;
//        }
//
//        if ((dec.preballs == 2) && (dec.balls == 3)) {
//            dec.full = true;
//            dec.firstfull = true;
//        }
//
//        if (dec.full && dec.firstfull) {
//            gamepad1.rumble(1, 1, 500);
//            gamepad2.rumble(1, 1, 500);
//
//            dec.firstfull = false;
//            dec.fulltime = runtime.milliseconds() + 500;
//        }
//
//        if (runtime.milliseconds() > dec.fulltime) {
//            dec.full = false;
//        }
//
//        dec.preballs = dec.balls;
//
//        dec.susanV = dec.susanenc.getVoltage();
//        dec.susandegreesat = (dec.susanV - dec.susanminV) / dec.susanVrange * dec.susanrange;
//
//        if ((dec.lastdiff >= dec.stol) && (Math.abs(dec.susandegreesat - dec.susandegreesto) < dec.stol)) {
//            dec.canscan = true;
//            dec.intakescan = runtime.milliseconds() + 100;
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
//            if (((gamepad1.left_trigger > 0.25) && (gamepad1.right_trigger > 0.25)) || (dec.launchy &&
//                    (dec.goalDC(dec.usePos, dec.goal) > dec.shootmin))) {
//                gamepad1.rumble(1, 1, Gamepad.RUMBLE_DURATION_CONTINUOUS); //0.5
//            } else if (!dec.full) {
//                gamepad1.rumble(0, 0, 0);
//            }
//        } else {
//            dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//            dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//            dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//            dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//
//            if (!dec.full) {
//                gamepad1.rumble(0, 0, 0);
//            }
//        }
//        //endregion
//
//        //region - Park
//        dec.park.setPosition(dec.parkPOS);
//
//        if (gamepad1.right_stick_button && !dec.parkCheck) {
//            dec.parkCheck = true;
//            if (!dec.parkON) {
//                dec.parkPOS = dec.parkDown;
//                dec.parkON = true;
//            } else {
//                dec.parkPOS = dec.parkUp;
//                dec.parkON = false;
//            }
//        }
//
//        if (!gamepad1.right_stick_button) {
//            dec.parkCheck = false;
//        }
//        //endregion
//
//        //region - Cam Rot
//        if (dec.goalDC(dec.usePos, dec.goal) < dec.camlim) {
//            dec.camrot.setPosition(dec.camclose);
//        } else if (dec.camrot.getPosition() != dec.camlong) {
//            dec.camrot.setPosition(dec.camlong);
//        }
//        //endregion
//
//        //region - Intake
//        if (gamepad2.right_trigger > 0.2) {
//            dec.intake.setPower(gamepad2.right_trigger);
//        } else if (gamepad2.left_trigger > 0.2) {
//            dec.intake.setPower(-gamepad2.left_trigger);
//        } else {
//            dec.intake.setPower(0);
//        }
//        //endregion
//
//        //region - Launch Mode
//        if (dec.launchy && (dec.goalDC(dec.usePos, dec.goal) > dec.shootmin)) {
//            gamepad2.rumble(1, 1, Gamepad.RUMBLE_DURATION_CONTINUOUS); //0.5
//        } else if (!dec.full) {
//            gamepad2.rumble(0, 0, 0);
//        }
//
//        if (dec.goalDC(dec.usePos, dec.goal) < dec.shootmin) {
//            dec.tooclose = true;
//        } else {
//            dec.tooclose = false;
//        }
//
//        if ((gamepad2.y || gamepad2.a || gamepad2.right_stick_button) && (a2time < runtime.milliseconds())) { //2.a, 1.a
//            a2time = runtime.milliseconds() + 500; //1000
//
//            if (a2) {
//                dec.launchy = true;
//                dec.firstlaunch = true;
//                dec.intaking = false;
//                a2 = false;
//                if (!dec.isSort) {
//                    dec.susan.setPosition(dec.susanpast3);
//                }
//            } else {
//                dec.launchy = false;
//                dec.intaking = true;
//                dec.transfer.setPower(0);
//                transferon = false;
//                dec.transferPower = 0;
//                a2 = true;
//                dec.isSort = false;
//
//                if (dec.balls < 3) {
//                    if (dec.susan.getPosition() < 0.35) {
//                        dec.susan.setPosition(dec.susanpos1);
//                    } else if (dec.susan.getPosition() < 0.72) {
//                        dec.susan.setPosition(dec.susanpos2);
//                    } else {
//                        dec.susan.setPosition(dec.susanpos3);
//                        Log.d("check","pos31");
//
//                    }
//                }
//            }
//        }
//
//        if (dec.launchy && (Math.abs(dec.susandegreesat - dec.susandegreesto) < dec.stol) && !dec.isSort) { //((a2time - 400) < runtime.milliseconds())
//            dec.transfer.setPower(1);
//        }
//        //endregion
//
//        //region - Flywheel
//        if (dec.launchy) {
//            dec.hood.setPosition(dec.LC(dec.goalDC(dec.usePos, dec.goal), dec.odo.getPosition().
//                    getX(DistanceUnit.INCH), false) + dec.hoffset);
//
//            if (dec.hood.getPosition() > dec.hoodup) {
//                dec.hood.setPosition(dec.hoodup);
//            } else if (dec.hood.getPosition() < dec.hooddown) {
//                dec.hood.setPosition(dec.hooddown);
//            }
//
//            dec.launch.setVelocity(dec.LC(dec.goalDC(dec.usePos, dec.goal), dec.odo.getPosition().
//                    getX(DistanceUnit.INCH), true) + dec.launchvel);
//            dec.velocityT = dec.LC(dec.goalDC(dec.usePos, dec.goal), dec.odo.getPosition().
//                    getX(DistanceUnit.INCH), true);
//
//            //telemetry.addData("Dist: ", dec.goalDC(dec.usePos, dec.goal) + dec.turretRad);
//
//            //far PID too or only close?
//            //if (dec.odo.getPosition().getX(DistanceUnit.INCH) < 20) {
//                dec.launch.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
//                        dec.PIDFc.p, dec.PIDFc.i, dec.PIDFc.d, dec.PIDFc.f * 12 / hardwareMap.voltageSensor.
//                        iterator().next().getVoltage()));
//            /*} else {
//                dec.launch.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
//                        dec.PIDFf.p, dec.PIDFf.i, dec.PIDFf.d, dec.PIDFf.f * 12 / hardwareMap.voltageSensor.
//                        iterator().next().getVoltage()));
//            }*/
//        } else {
//            dec.hood.setPosition(dec.hoodmid);
//            dec.launch.setVelocity(2500 / 60 * 28); //2000
//
//            dec.launch.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
//                    dec.PIDFc.p, dec.PIDFc.i, dec.PIDFc.d, dec.PIDFc.f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
//            ));
//        }
//
//        if (dec.launchy && (Math.abs(dec.launch.getVelocity() - (dec.velocityT + dec.launchvel)) < 100) && (Math.abs(dec.turret.
//                getPower()) < 0.3)) { //0.1, 0.15, 0.2
//            if (dec.odo.getPosition().getX(DistanceUnit.INCH) > 20) {
//                if ((a2time + 10) < runtime.milliseconds()) { //1000
//                    dec.canlaunch = true;
//                } else {
//                    dec.canlaunch = false;
//                }
//            } else {
//                if ((a2time + 5) < runtime.milliseconds()) { //500
//                    dec.canlaunch = true;
//                } else {
//                    dec.canlaunch = false;
//                }
//            }
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
//        //region - Launch Unsort
//        if (gamepad2.a && dec.launchy) {
//            dec.rapido = true;
//        } else if ((endLaunchTime < runtime.milliseconds()) && (!dec.launchy)) {
//            dec.rapido = false;
//
//            if (dec.resetballs) {
//                dec.resetballs = false;
//            }
//        }
//
//        if (dec.rapido && (dec.balls == 0)) {
//            dec.rapido = false;
//        }
//
//        if (dec.rapido && dec.canlaunch && (Math.abs(dec.susandegreesat - dec.susandegreesto) < dec.stol) &&
//                (!dec.tooclose)) { //40?
//            dec.full = false;
//
//            if (!dec.isSort) {
//                dec.susan.setPosition(dec.susanlaunch1);
//                endLaunchTime = runtime.milliseconds() + 5; //600
//                dec.resetballs = true;
//            } else if (endLaunchTime < runtime.milliseconds()) {
//                if ((Math.abs(dec.susan.getPosition() - dec.susanpast3) < 0.01) || (Math.abs(dec.susan.getPosition()
//                        - dec.susanpos3) < 0.01) || (Math.abs(dec.susan.getPosition() - dec.susanlaunch3) < 0.01)) {
//                    dec.susan.setPosition(dec.susanlaunch1);
//                    endLaunchTime = runtime.milliseconds() + 5; //600
//                    dec.resetballs = true;
//                } else {
//                    dec.susan.setPosition(dec.susanlaunch3);
//                    endLaunchTime = runtime.milliseconds() + 5; //600
//                    dec.resetballs = true;
//                }
//            }
//        }
//        //endregion
//
//        //region - Auto Intaking
//        if ((!dec.launchy) && (Math.abs(dec.susandegreesat - dec.susandegreesto) < dec.stol)) {
//            dec.purpleLaunched = false;
//            dec.greenLaunched = false;
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
//
//        /*telemetry.addData("Susan V: ", dec.susanV);
//        telemetry.addData("Susan Degrees At: ", dec.susandegreesat);
//        telemetry.addData("Susan Degrees To: ", dec.susandegreesto);*/
//        //endregion
//
//        //region - Manual Susan
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
//        if (Math.abs(gamepad2.left_stick_y) > 0.2) {
//            dec.transfer.setPower(-gamepad2.left_stick_y);
//            dec.transferPower = -gamepad2.left_stick_y;
//        }
//        //endregion
//
//        //region - Save QR Code
//        if ((dec.launchy && (Math.abs(dec.turret.getPower()) < 0.1)) || (gamepad1.y && !dec.launchy)) {
//            telemetryAprilTag();
//
//            dec.robopos = dec.RP(dec.campos, dec.tpos, dec.toffset, dec.odo.getPosition());
//
//            dec.newdist = Math.sqrt(Math.pow((dec.robopos.x - dec.odo.getPosition().getX(DistanceUnit.INCH)),
//                    2) + Math.pow((dec.robopos.y - dec.odo.getPosition().getY(DistanceUnit.INCH)), 2));
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
//            try {
//                dec.usePos.x = dec.goalcode.robotPose.getPosition().x; // gives error on get pos on a null object reference
//                dec.usePos.y = dec.goalcode.robotPose.getPosition().y;
//                dec.usePos.z = dec.goalcode.robotPose.getOrientation().getYaw(AngleUnit.DEGREES);
//            } catch (NullPointerException e) {
//                Log.d("CamE", "Lost Pos");
//            }
//        } else {
//            dec.usePos.x = dec.odo.getPosition().getX(DistanceUnit.INCH);
//            dec.usePos.y = dec.odo.getPosition().getY(DistanceUnit.INCH);
//            dec.usePos.z = dec.odo.getPosition().getHeading(AngleUnit.DEGREES);
//        }
//        //endregion
//
//        //region - Manual Hood
//        if (gamepad1.x) {
//            dec.hoffset = dec.hoffset - 0.005; //0.001
//        } else if (gamepad1.b) {
//            dec.hoffset = dec.hoffset + 0.005;
//        }
//
//        if (dec.hoffset != 0) {
//            telemetry.addData("Hood Offset: ", dec.hoffset);
//        }
//        //endregion
//
//        //region - Turret & Updates
//        dec.odo.update();
//        dec.tpos = dec.oq.readAllEncoderData().positions[1];
//
//        if (!dec.launchy) {
//            dec.ttar = dec.turretmiddle + dec.toffset;
//        } else {
//            if (Math.abs(dec.setTurretAngle(dec.odo.getPosition(), dec.goal, dec.teamcolor) - dec.tpos) > dec.ttol) { //100
//                if (dec.odo.getPosition().getX(DistanceUnit.INCH) > 20) {
//                    if (dec.teamcolor.matches("Red")) {
//                        dec.ttar = dec.setTurretAngle(dec.odo.getPosition(), dec.goal, dec.teamcolor)
//                                + dec.toffset + 8;
//                    } else {
//                        dec.ttar = dec.setTurretAngle(dec.odo.getPosition(), dec.goal, dec.teamcolor)
//                                + dec.toffset - 8;
//                    }
//                } else {
//                    dec.ttar = dec.setTurretAngle(dec.odo.getPosition(), dec.goal, dec.teamcolor) + dec.toffset;
//                }
//            } else {
//                dec.ttar = dec.tpos;
//            }
//        }
//
//        dec.ttar = dec.ttar + 2; //needed? todo
//
//        if (Math.abs(dec.tpos - dec.ttar) > 100) { //200, 75, 90, 100, 120
//            dec.turretpower = 1;
//        } else if (Math.abs(dec.tpos - dec.ttar) > dec.ttol) {
//            if (Math.abs(dec.tpos - dec.ttar) > 7) {
//                dec.turretpower = Range.clip((0.11 + (0.0000005 * Math.pow(Math.abs(dec.tpos - dec.ttar),
//                        3))), 0, 1); //0.09
//            } else { //steeper bowl todo
//                dec.turretpower = 0.09; //0.08
//            }
//        } else {
//            dec.turretpower = 0;
//        }
//
//        if ((dec.terror < Math.abs(dec.ttar - dec.tpos)) && (Math.abs(dec.ttar - dec.tpos) > 30) && dec.launchy) {
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
//        //endregion
//
//        //region - Susan Prep
//        if (gamepad2.right_stick_button) {
//            dec.transfer.setPower(0);
//            isPrep = true;
//
//            if (!dec.susanprep && !dec.isSort) {
//                sortTransferTime = runtime.milliseconds(); //200
//            }
//
//            dec.isSort = true;
//
//        }
//
//        if ((sortTransferTime < runtime.milliseconds()) && isPrep) {
//
//
//            if ((Math.abs(dec.susan.getPosition() - dec.susanpast3) < 0.01) && (endLaunchTime < runtime.milliseconds())) {
//                dec.susan.setPosition(dec.susanpos3);
//                Log.d("check","pos32");
//                dec.presort = true;
//            }
//
//            if (sortTransferTime + 400 < runtime.milliseconds()) {
//                dec.susanprep = true;
//                dec.presort = false;
//            }
//
//            isPrep = false;
//        }
//
//        if ((dec.susanprep) && ((preptime < runtime.milliseconds()) || dec.transferCycle) && (!((Math.abs(dec.
//                susan.getPosition() - dec.susanbtw12) < 0.01) || (Math.abs(dec.susan.getPosition() - dec.susanbtw23)
//                < 0.01))) && ((Math.abs(dec.susan.getPosition() - dec.susanpos1) < 0.01) || (Math.abs(dec.susan.
//                getPosition() - dec.susanpos2) < 0.01) || (Math.abs(dec.susan.getPosition() - dec.susanpos3) <
//                0.01) || (Math.abs(dec.susan.getPosition() - dec.susanpast3) < 0.01) || dec.transferCycle)) {
//
//
//
//            dec.green = "None";
//            dec.purple = "None";
//            dec.purple2 = "None";
//            dec.green2 = "None";
//
//            if ((dec.FC.matches("Purple") && dec.RC.matches("Green")) ||
//                    (dec.FC.matches("Green") && dec.RC.matches("Purple"))) {
//                if (dec.FC.matches("Green")) {
//                    dec.green = "Right";
//                    dec.purple = "Left";//swapped
//                } else {
//                    if (dec.RC.matches("Green")) {
//                        dec.green = "Left";
//                    }
//
//                    if (dec.FC.matches("Purple")) { //was lc now rc
//                        dec.purple = "Right"; //swapped
//                    } else {
//                        dec.purple = "Right";
//                    }
//                }
//
//                if ((Math.abs(dec.susan.getPosition() - dec.susanpos1) < 0.01)) {
//                    if (((dec.LC.matches("Purple") && dec.RC.matches("Green")) || (dec.LC.matches("Green")
//                            && dec.RC.matches("Purple"))) && (endLaunchTime < runtime.milliseconds())) {
//                        dec.susan.setPosition(dec.susanbtw23);
//                        if (dec.LC.matches("Purple")) {
//                            dec.purple = "Left";
//                            dec.green = "Right";
//                        } else if (dec.LC.matches("Green")) {
//                            dec.green = "Left";
//                            dec.purple = "Right";
//                        }
//                        if (dec.FC.matches("Purple")) {
//                            dec.purple2 = "Back";
//                        } else if (dec.FC.matches("Green")) {
//                            dec.green2 = "Back";
//                        }
//                    } else if (endLaunchTime < runtime.milliseconds()) {
//                        dec.susan.setPosition(dec.susanbtw12);
//                        if (dec.FC.matches("Purple")) {
//                            dec.purple = "Left";
//                            if (dec.LC.matches("Green")) {
//                                dec.green = "Right";
//                            }
//                        } else if (dec.FC.matches("Green")) {
//                            dec.green = "Left";
//                            if (dec.LC.matches("Purple")) {
//                                dec.purple = "Right";
//                            }
//                        }
//
//                        if (dec.RC.matches("Purple")) {
//                            dec.purple2 = "Back";
//                        } else if (dec.RC.matches("Green")) {
//                            dec.green2 = "Back";
//                        }
//                    }
//                } else if ((Math.abs(dec.susan.getPosition() - dec.susanpos2) < 0.01) && (endLaunchTime < runtime.milliseconds())) {
//                    dec.susan.setPosition(dec.susanbtw12);
//                    if (dec.LC.matches("Purple")) {
//                        dec.purple2 = "Back";
//                    } else if (dec.LC.matches("Green")) {
//                        dec.green2 = "Back";
//                    }
//                } else if ((Math.abs(dec.susan.getPosition() - dec.susanpos3) < 0.01) && (endLaunchTime < runtime.milliseconds())) {
//                    dec.susan.setPosition(dec.susanbtw23);
//                    if (dec.LC.matches("Purple")) {
//                        dec.purple2 = "Back";
//                    } else if (dec.LC.matches("Green")) {
//                        dec.green2 = "Back";
//                    }
//                }
//
//            } else if ((dec.FC.matches("Purple") && dec.LC.matches("Green")) ||
//                    (dec.FC.matches("Green") && dec.LC.matches("Purple"))) {
//                if (dec.FC.matches("Green")) {
//                    dec.green = "Left";
//                    dec.purple = "Right";
//                } else {
//                    dec.green = "Right";
//                    dec.purple = "Left";
//                }
//
//                if ((Math.abs(dec.susan.getPosition() - dec.susanpos1) < 0.01) && (endLaunchTime < runtime.milliseconds())) {
//                    dec.susan.setPosition(dec.susanbtw12);
//                    if (dec.RC.matches("Purple")) {
//                        dec.purple2 = "Back";
//                    } else if (dec.RC.matches("Green")) {
//                        dec.green2 = "Back";
//                    }
//                } else if ((Math.abs(dec.susan.getPosition() - dec.susanpos2) < 0.01) && (endLaunchTime < runtime.milliseconds())) {
//                    dec.susan.setPosition(dec.susanbtw23);
//                    if (dec.RC.matches("Purple")) {
//                        dec.purple2 = "Back";
//                    } else if (dec.RC.matches("Green")) {
//                        dec.green2 = "Back";
//                    }
//                } else if ((Math.abs(dec.susan.getPosition() - dec.susanpos3) < 0.01)) {
//                    if (((dec.LC.matches("Purple") && dec.RC.matches("Green")) || (dec.LC.matches("Green")
//                            && dec.RC.matches("Purple"))) && (endLaunchTime < runtime.milliseconds())) {
//                        dec.susan.setPosition(dec.susanbtw12);
//                        if (dec.LC.matches("Green")) {
//                            dec.purple = "Right";
//                            dec.green = "Left";
//                        }
//                        if (dec.LC.matches("Purple")) {
//                            dec.purple = "Left";
//                            dec.green = "Right";
//                        }
//                        if (dec.FC.matches("Purple")) {
//                            dec.purple2 = "Back";
//                        } else if (dec.FC.matches("Green")) {
//                            dec.green2 = "Back";
//                        }
//                    } else if (endLaunchTime < runtime.milliseconds()) {
//                        dec.susan.setPosition(dec.susanbtw23);
//                        if (dec.LC.matches("Purple")) {
//                            dec.purple2 = "Back";
//                        } else if (dec.LC.matches("Green")) {
//                            dec.green2 = "Back";
//                        }
//                    }
//                }
//            } else if ((dec.RC.matches("Purple") && dec.LC.matches("Green")) ||
//                    (dec.RC.matches("Green") && dec.LC.matches("Purple"))) {
//                if (dec.RC.matches("Green")) {
//                    dec.green = "Left";
//                    if (dec.FC.matches("Purple")) {
//                        dec.purple = "Right";
//                    } else if (dec.FC.matches("None")) {
//                        dec.purple = "Right 2";
//                    }
//                } else if (dec.RC.matches("Purple")) {
//                    dec.purple = "Left";
//                    if (dec.FC.matches("Green")) {
//                        dec.green = "Right";
//                    } else if (dec.FC.matches("None")) {
//                        dec.green = "Right 2";
//                    }
//                } else {
//                    dec.green = "Right";
//                    dec.purple = "Left";
//                }
//
//                if ((Math.abs(dec.susan.getPosition() - dec.susanpos1) < 0.01) && (endLaunchTime < runtime.milliseconds())) {
//                    dec.susan.setPosition(dec.susanbtw23);
//                    if ((dec.LC.matches("Green")) && (dec.RC.matches("Purple"))) {
//                        dec.green = "Left";
//                        dec.purple = "Right";
//                    } else if ((dec.LC.matches("Purple")) && (dec.RC.matches("Green"))) {
//                        dec.green = "Right";
//                        dec.purple = "Left";
//                    }
//
//                    if (dec.FC.matches("Purple")) {
//                        dec.purple2 = "Back";
//                    } else if (dec.FC.matches("Green")) {
//                        dec.green2 = "Back";
//                    }
//                } else if ((Math.abs(dec.susan.getPosition() - dec.susanpos2) < 0.01)) {
//                    if (((dec.LC.matches("Purple") && dec.FC.matches("Green")) || (dec.LC.matches("Green")
//                            && dec.FC.matches("Purple"))) && (endLaunchTime < runtime.milliseconds())) {
//                        dec.susan.setPosition(dec.susanbtw23);
//                        if ((dec.FC.matches("Green")) && (dec.LC.matches("Purple"))) {
//                            dec.green = "Left";
//                            dec.purple = "Right";
//                        } else if ((dec.FC.matches("Purple")) && (dec.LC.matches("Green"))) {
//                            dec.green = "Right";
//                            dec.purple = "Left";
//                        }
//
//                        if (dec.RC.matches("Purple")) {
//                            dec.purple2 = "Back";
//                        } else if (dec.RC.matches("Green")) {
//                            dec.green2 = "Back";
//                        }
//                    } else if (endLaunchTime < runtime.milliseconds()) {
//                        dec.susan.setPosition(dec.susanbtw12);
//
//                        if ((dec.FC.matches("Green")) && (dec.RC.matches("Purple"))) {
//                            dec.green = "Left";
//                            dec.purple = "Right";
//
//                            if (dec.LC.matches("Purple")) {
//                                dec.purple2 = "Back";
//                            } else if (dec.LC.matches("Green")) {
//                                dec.green2 = "Back";
//                            }
//                        } else if ((dec.FC.matches("Purple")) && (dec.RC.matches("Green"))) {
//                            dec.green = "Right";
//                            dec.purple = "Left";
//                            if (dec.LC.matches("Purple")) {
//                                dec.purple2 = "Back";
//                            } else if (dec.LC.matches("Green")) {
//                                dec.green2 = "Back";
//                            }
//                        } else if (dec.FC.matches("None")) {
//                            if (dec.RC.matches("Purple")) {
//                                dec.purple = "Left";
//                                dec.green = "Right 2";
//                            } else if (dec.RC.matches("Green")) {
//                                dec.green = "Left";
//                                dec.purple = "Right 2";
//                            }
//                        }
//                    }
//                } else if ((Math.abs(dec.susan.getPosition() - dec.susanpos3) < 0.01) && (endLaunchTime < runtime.milliseconds())) {
//                    dec.susan.setPosition(dec.susanbtw12);
//                    if (dec.LC.matches("Green") && dec.RC.matches("Purple")) {
//                        dec.green = "Left";
//                        dec.purple = "Right";
//                    } else if (dec.LC.matches("Purple") && dec.RC.matches("Green")) {
//                        dec.green = "Right";
//                        dec.purple = "Left";
//                    }
//
//                    if (dec.FC.matches("Purple")) {
//                        dec.purple2 = "Back";
//                    } else if (dec.FC.matches("Green")) {
//                        dec.green2 = "Back";
//                    }
//                }
//            }
//
//            dec.susanprep = false;
//            dec.transferCycle = false;
//            preptime = runtime.milliseconds() + 2500;
//        }
//        //endregion
//
//        //region - Launch Green
//        if (gamepad2.b && dec.launchy && (!dec.tooclose)) {
//            dec.full = false;
//            Log.d("check","1");
//
//            if (Math.abs(gamepad2.left_stick_y) < 0.2 && !dec.transferCycle) {
//                dec.transfer.setPower(1);
//                transferon = true;
//            }
//
//            if ((launchtime < runtime.milliseconds()) && (rapidtime < runtime.milliseconds())) {
//                Log.d("check","2");
//                if ((Math.abs(dec.susan.getPosition() - dec.susanbtw12) < 0.02)) {
//                    launchtime = runtime.milliseconds();
//                    dec.launched = true;
//                    if ((dec.green.matches("Right")) && (endLaunchTime < runtime.milliseconds())) {
//                        dec.susan.setPosition(dec.susanlaunch2); // switch switch
//                        launchtime = runtime.milliseconds() + colorlongmovetime;
//                        dec.launched = true;
//                        dec.greenLaunched = true;
//                    } else if ((dec.green.matches("Left")) && (endLaunchTime < runtime.milliseconds())) {
//                        dec.susan.setPosition(dec.susanlaunch1); //switch switch
//                        launchtime = runtime.milliseconds() + colormovetime;
//                        dec.launched = true;
//                        dec.greenLaunched = true;
//                    } else if ((dec.green.matches("Right 2")) && (endLaunchTime < runtime.milliseconds())) {
//                        dec.susan.setPosition(dec.susanlaunch3); //switch switch
//                        launchtime = runtime.milliseconds() + colorlongmovetime;
//                        dec.launched = true;
//                        dec.greenLaunched = true;
//                    } else if (endLaunchTime < runtime.milliseconds()) {
//                        dec.susan.setPosition(dec.susanbtw12);
//                        rapidtime = runtime.milliseconds() + 500;
//
//                    }
//                } else if ((Math.abs(dec.susan.getPosition() - dec.susanbtw23) < 0.02)) {
//                    launchtime = runtime.milliseconds();
//                    dec.launched = true;
//                    if ((dec.green.matches("Right")) && (endLaunchTime < runtime.milliseconds())) {
//                        dec.susan.setPosition(dec.susanlaunch3);
//                        launchtime = runtime.milliseconds() + colorlongmovetime;
//                        dec.launched = true;
//                        dec.greenLaunched = true;
//                    } else if ((dec.green.matches("Left")) && (endLaunchTime < runtime.milliseconds())) {
//                        dec.susan.setPosition(dec.susanlaunch2);
//                        launchtime = runtime.milliseconds() + colormovetime;
//                        dec.launched = true;
//                        dec.greenLaunched = true;
//                    } else if (endLaunchTime < runtime.milliseconds()) {
//                        dec.susan.setPosition(dec.susanbtw23);
//                        rapidtime = runtime.milliseconds() + 500;
//                    }
//
//                } else if (dec.purpleLaunched) {
//                    if (!(dec.purple.matches("Right 2"))) {
//                        if (((Math.abs(dec.susan.getPosition() - dec.susanlaunch1) < 0.01) || (Math.abs(dec.susan.
//                                getPosition() - dec.susanlaunch3) < 0.01)) && (endLaunchTime < runtime.milliseconds())) {
//                            dec.susan.setPosition(dec.susanlaunch2);
//                            launchtime = runtime.milliseconds() + colormovetime;
//                            dec.launched = true;
//                            dec.greenLaunched = true;
//                        } else if (Math.abs(dec.susan.getPosition() - dec.susanlaunch2) < 0.01) {
//                            if ((dec.green.matches("Right")) && (endLaunchTime < runtime.milliseconds())) {
//                                dec.susan.setPosition(dec.susanlaunch3); //swapped from 3, 1
//                                launchtime = runtime.milliseconds() + colormovetime;
//                                dec.launched = true;
//                                dec.greenLaunched = true;
//                            } else if ((dec.green.matches("Left")) && (endLaunchTime < runtime.milliseconds())) {
//                                dec.susan.setPosition(dec.susanlaunch1); //swapped from 1, 3
//                                launchtime = runtime.milliseconds() + colormovetime;
//                                dec.launched = true;
//                                dec.greenLaunched = true;
//                            }
//                        }
//                    } else if (endLaunchTime < runtime.milliseconds()) {
//                        dec.susan.setPosition(dec.susanlaunch1);
//                        launchtime = runtime.milliseconds() + colormovetime;
//                        dec.launched = true;
//                        dec.greenLaunched = true;
//                    }
//                } else if (dec.greenLaunched) {
//                    if ((Math.abs(dec.susan.getPosition() - dec.susanlaunch1) < 0.01) || (Math.abs(dec.susan.
//                            getPosition() - dec.susanlaunch3) < 0.01)) {
//                        if (dec.transferCycle) {
//                            dec.susanprep = true;
//                        }
//                        dec.transferCycle = true;
//                        dec.transfer.setPower(0);
//                        launchtime = runtime.milliseconds() + 1000;
//                    } else if (Math.abs(dec.susan.getPosition() - dec.susanlaunch2) < 0.01) {
//                        if ((dec.green.matches("Right")) && (endLaunchTime < runtime.milliseconds())) {
//                            dec.susan.setPosition(dec.susanlaunch3);
//                            launchtime = runtime.milliseconds() + colormovetime;
//                            dec.launched = true;
//                            dec.greenLaunched = true;
//                        } else if ((dec.green.matches("Left")) && (endLaunchTime < runtime.milliseconds())) {
//                            dec.susan.setPosition(dec.susanlaunch1);
//                            launchtime = runtime.milliseconds() + colormovetime;
//                            dec.launched = true;
//                            dec.greenLaunched = true;
//                        }
//                    }
//                } else {
//                    Log.d("check","FAIL");
//                }
//            }
//        }
//        //endregion
//
//        //region - Launch Purple
//        if (gamepad2.x && dec.launchy && (!dec.tooclose)) {
//            dec.full = false;
//
//            if (Math.abs(gamepad2.left_stick_y) < 0.2 && !dec.transferCycle) {
//                dec.transfer.setPower(1);
//                transferon = true;
//            }
//
//            if ((launchtime < runtime.milliseconds()) && (rapidtime < runtime.milliseconds())) {
//                if (Math.abs(dec.susan.getPosition() - dec.susanbtw12) < 0.01) {
//                    launchtime = runtime.milliseconds();
//                    dec.launched = true;
//                    if ((dec.purple.matches("Right")) && (endLaunchTime < runtime.milliseconds())) {
//                        dec.susan.setPosition(dec.susanlaunch2); // switch switch
//                        launchtime = runtime.milliseconds() + colorlongmovetime;
//                        dec.launched = true;
//                        dec.purpleLaunched = true;
//                    } else if ((dec.purple.matches("Left")) && (endLaunchTime < runtime.milliseconds())) {
//                        dec.susan.setPosition(dec.susanlaunch1); //switch switch
//                        launchtime = runtime.milliseconds() + colormovetime;
//                        dec.launched = true;
//                        dec.purpleLaunched = true;
//                    } else if ((dec.purple.matches("Right 2")) && (endLaunchTime < runtime.milliseconds())) {
//                        dec.susan.setPosition(dec.susanlaunch3); //switch switch
//                        launchtime = runtime.milliseconds() + colorlongmovetime;
//                        dec.launched = true;
//                        dec.purpleLaunched = true;
//                    } else if (endLaunchTime < runtime.milliseconds()) {
//                        dec.susan.setPosition(dec.susanbtw12);
//                        rapidtime = runtime.milliseconds() + 500;
//                    }
//                } else if (Math.abs(dec.susan.getPosition() - dec.susanbtw23) < 0.01) {
//                    launchtime = runtime.milliseconds();
//                    dec.launched = true;
//                    if ((dec.purple.matches("Right")) && (endLaunchTime < runtime.milliseconds())) {
//                        dec.susan.setPosition(dec.susanlaunch3);
//                        launchtime = runtime.milliseconds() + colorlongmovetime;
//                        dec.launched = true;
//                        dec.purpleLaunched = true;
//                    } else if ((dec.purple.matches("Left")) && (endLaunchTime < runtime.milliseconds())) {
//                        dec.susan.setPosition(dec.susanlaunch2);
//                        launchtime = runtime.milliseconds() + colormovetime;
//                        dec.launched = true;
//                        dec.purpleLaunched = true;
//                    } else if (endLaunchTime < runtime.milliseconds()) {
//                        dec.susan.setPosition(dec.susanbtw23);
//                        rapidtime = runtime.milliseconds() + 500;
//                    }
//                } else if (dec.greenLaunched) {
//                    if (!(dec.green.matches("Right 2"))) {
//                        if (((Math.abs(dec.susan.getPosition() - dec.susanlaunch1) < 0.01) || (Math.abs(dec.susan.
//                                getPosition() - dec.susanlaunch3) < 0.01)) && (endLaunchTime < runtime.milliseconds())) {
//                            dec.susan.setPosition(dec.susanlaunch2);
//                            launchtime = runtime.milliseconds() + colormovetime;
//                            dec.launched = true;
//                            dec.purpleLaunched = true;
//                        } else if ((Math.abs(dec.susan.getPosition() - dec.susanlaunch2) < 0.01)) {
//                            if ((dec.purple.matches("Right")) && (endLaunchTime < runtime.milliseconds())) {
//                                dec.susan.setPosition(dec.susanlaunch3); //Correct for some
//                                launchtime = runtime.milliseconds() + colormovetime;
//                                dec.launched = true;
//                                dec.purpleLaunched = true;
//                            } else if ((dec.purple.matches("Left")) && (endLaunchTime < runtime.milliseconds())) {
//                                dec.susan.setPosition(dec.susanlaunch1); //swapped from 1, 3
//                                launchtime = runtime.milliseconds() + colormovetime;
//                                dec.launched = true;
//                                dec.purpleLaunched = true;
//                            }
//                        }
//                    } else if (endLaunchTime < runtime.milliseconds()) {
//                        dec.susan.setPosition(dec.susanlaunch1);
//                        launchtime = runtime.milliseconds() + colormovetime;
//                        dec.launched = true;
//                        dec.purpleLaunched = true;
//                    }
//                } else if (dec.purpleLaunched) {
//                    if ((Math.abs(dec.susan.getPosition() - dec.susanlaunch1) < 0.01) || (Math.abs(dec.susan.
//                            getPosition() - dec.susanlaunch3) < 0.01)) {
//                        if (dec.transferCycle) {
//                            dec.susanprep = true;
//                        }
//                        dec.transferCycle = true;
//                        dec.transfer.setPower(0);
//                        launchtime = runtime.milliseconds() + 1000;
//                    } else if (Math.abs(dec.susan.getPosition() - dec.susanlaunch2) < 0.01) {
//                        if ((dec.purple.matches("Right")) && (endLaunchTime < runtime.milliseconds())) {
//                            dec.susan.setPosition(dec.susanlaunch3);
//                            launchtime = runtime.milliseconds() + colormovetime;
//                            dec.launched = true;
//                            dec.purpleLaunched = true;
//                        } else if ((dec.purple.matches("Left")) && (endLaunchTime < runtime.milliseconds())) {
//                            dec.susan.setPosition(dec.susanlaunch1);
//                            launchtime = runtime.milliseconds() + colormovetime;
//                            dec.launched = true;
//                            dec.purpleLaunched = true;
//                        }
//                    }
//                }
//            }
//        }
//        //endregion
//
//        //region - Telemetry
//        telemetry.addData("FC", dec.FC);
//        telemetry.addData("RC", dec.RC);
//        telemetry.addData("LC", dec.LC);
//
//        telemetry.addData("Susan at",dec.susan.getPosition());
//
//        telemetry.addData("green is", dec.green);
//        telemetry.addData("purple is", dec.purple);
//        telemetry.addData("purple 2 is", dec.purple2);
//        telemetry.addData("green 2 is", dec.green2);
//
//        telemetry.addData("Turret Target: ", dec.ttar);
//        telemetry.addData("Turret At: ", dec.tpos);
//        telemetry.addData("Flywheel Speed: ", (dec.launch.getVelocity() * 60 / 28));
//        telemetry.addData("Flywheel Target: ", (dec.velocityT * 60 / 28));
//        telemetry.addLine(String.format("OdoPos: %6.2f %6.2f %6.2f (-90)", dec.odo.getPosition().getX(DistanceUnit.INCH),
//                dec.odo.getPosition().getY(DistanceUnit.INCH), (dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) - 90));
//
//        if (dec.lastCam != dec.robopos) {
//            telemetry.addLine(String.format("Robo Pos (Cam): %6.2f %6.2f %6.2f ", dec.robopos.x, dec.robopos.y, dec.robopos.z));
//        }
//        dec.lastCam = dec.robopos;
//
//        telemetry.update();
//        //endregion
//    }
//
//    private void initAprilTag() {
//        aprilTag = new AprilTagProcessor.Builder()
//                .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)
//                .setCameraPose(cameraPosition, cameraOrientation)
//                .build();
//        VisionPortal.Builder builder = new VisionPortal.Builder();
//        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
//        builder.setCameraResolution(new Size(640, 480));
//        builder.addProcessor(aprilTag);
//        visionPortal = builder.build();
//    }
//
//    private void telemetryAprilTag() {
//        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
//
//        for (AprilTagDetection detection : currentDetections) {
//            if (detection.metadata != null) {
//                if (!detection.metadata.name.contains("Obelisk")) {
//                    dec.campos.x = detection.robotPose.getPosition().x;
//                    dec.campos.y = detection.robotPose.getPosition().y;
//                    dec.campos.z = detection.robotPose.getOrientation().getYaw(AngleUnit.DEGREES);
//                }
//            }
//        }
//    }
//}
