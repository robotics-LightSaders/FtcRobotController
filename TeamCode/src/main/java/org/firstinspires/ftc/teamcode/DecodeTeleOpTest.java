//package org.firstinspires.ftc.teamcode;
//
//import android.util.Log;
//import android.util.Size;
//
//import com.qualcomm.hardware.rev.RevColorSensorV3;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.AnalogInput;
//import com.qualcomm.robotcore.hardware.CRServo;
//import com.qualcomm.robotcore.hardware.ColorSensor;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.hardware.DigitalChannel;
//import com.qualcomm.robotcore.hardware.I2cDevice;
//import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
//import com.qualcomm.robotcore.hardware.PIDFCoefficients;
//import com.qualcomm.robotcore.hardware.Servo;
//import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration;
//import com.qualcomm.robotcore.util.ElapsedTime;
//import com.qualcomm.robotcore.util.Range;
//
//import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.Position;
//import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
//import org.firstinspires.ftc.vision.VisionPortal;
//import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
//import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
//
//import java.util.List;
//
//@TeleOp(name = "Test", group = "TeleOp")
////@Disabled
//public class DecodeTeleOpTest extends OpMode {
//    DecodeConstants dec = new DecodeConstants();
//
//    PIDFCoefficients pidf = new PIDFCoefficients(10, 0.1, 2, 0.5);
//
//    RevColorSensorV3 f2;
//    RevColorSensorV3 r2;
//    RevColorSensorV3 l2;
//
//    private AprilTagProcessor aprilTag;
//    private VisionPortal visionPortal;
//
//    private Position cameraPosition = new Position(DistanceUnit.INCH,
//            0, 0, 0, 0);
//    private YawPitchRollAngles cameraOrientation = new YawPitchRollAngles(AngleUnit.DEGREES,
//            0, -90, 0, 0);
//
//    private ElapsedTime runtime = new ElapsedTime();
//    double looptime = 0;
//
//    double x;
//    double y;
//    double z;
//
//    double intaketime = 0;
//    double inspeed = 0;
//
//    double launchtime = 0;
//    double fwspeed = 0;
//    double tpr = 28;
//    double maxrpm = 5800;
//    double maxspeed = maxrpm * tpr;
//
//    double lastvel = 0;
//    double robovel = 0;
//
//    double p = 0.02;
//    double i = 0.0005;
//    double d = 0.0025;
//    double mp = 0.16;
//    double t = 17;
//
//    boolean g2a = false;
//    double g2atime = 0;
//    String firstcolor = "None";
//    String secondcolor = "None";
//    String thirdcolor = "None";
//
//    @Override
//    public void init() {
//        dec.fl = hardwareMap.get(DcMotor.class, "fl");
//        dec.fr = hardwareMap.get(DcMotor.class, "fr");
//        dec.bl = hardwareMap.get(DcMotor.class, "bl");
//        dec.br = hardwareMap.get(DcMotor.class, "br");
//
//        dec.launch = hardwareMap.get(DcMotorEx.class, "launch");
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
//        dec.f2 = hardwareMap.get(NormalizedColorSensor.class, "f2");
//        dec.r2 = hardwareMap.get(NormalizedColorSensor.class, "r2");
//        dec.l2 = hardwareMap.get(NormalizedColorSensor.class, "l2");
//
//        dec.fl.setDirection(DcMotor.Direction.REVERSE);
//        dec.bl.setDirection(DcMotor.Direction.REVERSE);
//
//        dec.transfer.setDirection(DcMotor.Direction.REVERSE);
//        dec.intake.setDirection(DcMotor.Direction.REVERSE);
//
//        dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//        dec.bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        dec.br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//
//        dec.bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        dec.br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//
//        //dec.launch.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        //dec.launch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        //dec.launch.setVelocityPIDFCoefficients(pidf.p, pidf.i, pidf.d, pidf.f);
//        //dec.launch.setTargetPositionTolerance(5); // In ticks
//
//        dec.fl.setPower(0);
//        dec.fr.setPower(0);
//        dec.bl.setPower(0);
//        dec.br.setPower(0);
//
//        dec.launch.setPower(0);
//        dec.intake.setPower(0);
//        dec.intakedos.setPower(0);
//        dec.transfer.setPower(0);
//
//        //dec.camrot.setPosition(0.5);
//
//        dec.taxon = new RTPAxon(dec.turret, dec.turretenc, RTPAxon.Direction.REVERSE);
//        dec.taxon.setMaxPower(dec.tmaxp);
//        dec.taxon.setRtp(false);
//        /*dec.haxon.setPidCoeffs(p, i, d);
//        dec.haxon.isAtTarget(t);*/
//
//        dec.turret.setPower(0);
//
//        dec.configureOdo(true);
//        dec.odo.setPosition(dec.odoorigin);
//
//        initAprilTag();
//
//        runtime.reset();
//        looptime = runtime.milliseconds();
//    }
//
//    public void loop() {
//        telemetry.addData("Loop Time: ", runtime.milliseconds() - looptime);
//        looptime = runtime.milliseconds();
//
//        dec.odo.update();
//        dec.taxon.update();
//
//        dec.f2c = dec.f2.getNormalizedColors();
//        dec.r2c = dec.r2.getNormalizedColors();
//        dec.l2c = dec.l2.getNormalizedColors();
//
//        telemetry.addData("F dist", "%.3f", f2.getDistance(DistanceUnit.CM));
//
//        telemetry.addData("F Red", "%.3f", dec.f2c.red);
//        telemetry.addData("F Green", "%.3f", dec.f2c.green);
//        telemetry.addData("F Blue", "%.3f", dec.f2c.blue);
//
//        telemetry.addData("R Red", "%.3f", dec.r2c.red);
//        telemetry.addData("R Green", "%.3f", dec.r2c.green);
//        telemetry.addData("R Blue", "%.3f", dec.r2c.blue);
//
//        telemetry.addData("L Red", "%.3f", dec.l2c.red);
//        telemetry.addData("L Green", "%.3f", dec.l2c.green);
//        telemetry.addData("L Blue", "%.3f", dec.l2c.blue);
//
//        if ((dec.f2c.blue > 0.004) && (dec.f2c.green > 0.004)) {
//            if ((dec.f2c.green / dec.f2c.red) > 1.7) {
//                telemetry.addData("Frv: Green", "");
//            } else {
//                telemetry.addData("Frv: Purple", "");
//            }
//        } else {
//            telemetry.addData("Frv: None", "");
//        }
//
//        if ((dec.r2c.blue / dec.r2c.red) > 1.6) {
//            if ((dec.r2c.green / dec.r2c.red) > 2.5) {
//                telemetry.addData("Rrv: Green", "");
//            } else {
//                telemetry.addData("Rrv: Purple", "");
//            }
//        } else {
//            telemetry.addData("Rrv: None", "");
//        }
//
//        if ((dec.l2c.blue / dec.l2c.red) > 1.6) {
//            if ((dec.l2c.green / dec.l2c.red) > 2.5) {
//                telemetry.addData("Lrv: Green", "");
//            } else {
//                telemetry.addData("Lrv: Purple", "");
//            }
//        } else {
//            telemetry.addData("Lrv: None", "");
//        }
//
//        if (gamepad1.b) {
//            dec.goal = dec.Rgoal;
//            dec.teamcolor = "Red";
//        } else if (gamepad1.x) {
//            dec.goal = dec.Bgoal;
//            dec.teamcolor = "Blue";
//        }
//
//        //region - Drive
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
//        //endregion
//
//        //region - Intake, Susan, Color
//        if ((gamepad1.right_stick_button || gamepad1.left_stick_button) && ((runtime.milliseconds() - intaketime) > 200)) {
//            intaketime = runtime.milliseconds();
//
//            if (gamepad1.right_stick_button) {
//                inspeed = inspeed + 0.25;
//            } else if (gamepad1.left_stick_button) {
//                inspeed = inspeed - 0.25;
//            }
//
//            if (inspeed < -1) {
//                inspeed = -1;
//            } else if (inspeed > 1) {
//                inspeed = 1;
//            }
//
//            dec.intake.setPower(inspeed);
//            dec.intakedos.setPower(inspeed);
//        }
//
//        dec.susanV = dec.susanenc.getVoltage();
//        dec.susandegreesat = (dec.susanV - dec.susanminV) / dec.susanVrange * dec.susanrange;
//        dec.susandegreesto = dec.susan.getPosition() * dec.susanrange;
//        telemetry.addData("Susan Max V?: ", dec.susanenc.getMaxVoltage());
//        telemetry.addData("Susan V: ", dec.susanV);
//        telemetry.addData("Susan Degrees At: ", dec.susandegreesat);
//        telemetry.addData("Susan Degrees To: ", dec.susandegreesto);
//
//        if (gamepad2.dpad_left) {
//            dec.susan.setPosition(dec.susan.getPosition() + 0.002);
//        } else if (gamepad2.dpad_right) {
//            dec.susan.setPosition(dec.susan.getPosition() - 0.002);
//        }
//
//        if (gamepad1.dpad_down) {
//            dec.susan.setPosition(dec.susanpos1);
//        } else if (gamepad1.dpad_left) {
//            dec.susan.setPosition(dec.susanpos2);
//        } else if (gamepad1.dpad_up) {
//            dec.susan.setPosition(dec.susanpos3);
//        } else if (gamepad1.dpad_right) {
//            dec.susan.setPosition(dec.susanbtw31);
//        }
//
//        /*telemetry.addData("F0 State: ", dec.f0.getState());
//        telemetry.addData("F1 State: ", dec.f1.getState());
//        telemetry.addData("R0 State: ", dec.r0.getState());
//        telemetry.addData("R1 State: ", dec.r1.getState());*/
//
//        if (dec.f0.getState()) {
//            telemetry.addData("Fbl Color: ", "Purple");
//            firstcolor = "Purple";
//        } else if (dec.f1.getState()) {
//            telemetry.addData("Fbl Color: ", "Green");
//            firstcolor = "Green";
//        } else {
//            telemetry.addData("Fbl No Color", "");
//            firstcolor = "None";
//        }
//
//        if (dec.r0.getState()) {
//            telemetry.addData("Rbl Color: ", "Purple");
//            secondcolor = "Purple";
//        } else if (dec.r1.getState()) {
//            telemetry.addData("Rbl Color: ", "Green");
//            secondcolor = "Green";
//        } else {
//            telemetry.addData("Rbl No Color", "");
//            secondcolor = "None";
//        }
//
//        if (dec.l0.getState()) {
//            telemetry.addData("Lbl Color: ", "Purple");
//            thirdcolor = "Purple";
//        } else if (dec.l0.getState()) {
//            telemetry.addData("Lbl Color: ", "Green");
//            thirdcolor = "Green";
//        } else {
//            telemetry.addData("Lbl No Color", "");
//            thirdcolor = "None";
//        }
//
//        if (gamepad2.a && ((g2atime + 200) > runtime.milliseconds())) {
//            g2atime = runtime.milliseconds();
//            if (g2a) {
//                g2a = false;
//            } else {
//                g2a = true;
//                dec.susan.setPosition(dec.susanpos1);
//            }
//        }
//
//        //telemetry.addData("G2A: ", g2a);
//
//        if ((dec.intake.getPower() > 0) && g2a) {
//            if (dec.f0.getState() || dec.f1.getState()) {
//                if ((!dec.r0.getState()) && (!dec.r1.getState())) {
//                    dec.susan.setPosition(dec.susanpos2);
//                    thirdcolor = "None";
//                } else {
//                    dec.susan.setPosition(dec.susanpos3);
//                    if (dec.r0.getState()) {
//                        thirdcolor = "Purple";
//                    } else if (dec.r1.getState()) {
//                        thirdcolor = "Green";
//                    } else {
//                        thirdcolor = "None";
//                    }
//                }
//            } else {
//                thirdcolor = "None";
//            }
//        } else {
//            thirdcolor = "N/A";
//        }
//
//        /*telemetry.addData("First Color: ", firstcolor);
//        telemetry.addData("Second Color: ", secondcolor);
//        telemetry.addData("Third Color: ", thirdcolor);*/
//        //endregion
//
//        //region - Launcher, Hood, Turret
//        if (((gamepad1.left_trigger > 0.1) || (gamepad1.right_trigger > 0.1)) && ((runtime.milliseconds() - launchtime) > 200)) {
//            launchtime = runtime.milliseconds();
//
//            if (gamepad1.right_trigger > 0.1) {
//                fwspeed = fwspeed + 0.05;
//            } else if (gamepad1.left_trigger > 0.1) {
//                fwspeed = fwspeed - 0.05;
//            }
//
//            if (fwspeed < 0) {
//                fwspeed = 0;
//            } else if (fwspeed > 1) {
//                fwspeed = 1;
//            }
//        }
//
//        //dec.launch.setVelocity(fwspeed * maxspeed);
//        dec.launch.setPower(fwspeed);
//
//        if (/*fwspeed > 0*/ gamepad2.a) {
//            dec.transfer.setPower(1);
//        } else if (gamepad2.y) {
//            dec.transfer.setPower(-1);
//        } else {
//            dec.transfer.setPower(0);
//        }
//
//        if (gamepad2.x) {
//            dec.hood.setPosition(dec.hood.getPosition() + 0.01);
//        } else if (gamepad2.b) {
//            dec.hood.setPosition(dec.hood.getPosition() - 0.01);
//        }
//
//        /*if (gamepad1.dpad_right) {
//            dec.taxon.changeTargetRotation(10);
//        } else if (gamepad1.dpad_left) {
//            dec.taxon.changeTargetRotation(-10);
//        }*/
//
//        /*if (gamepad1.dpad_up) {
//            dec.camrot.setPosition(dec.camrot.getPosition() + 0.001);
//        } else if (gamepad1.dpad_down) {
//            dec.camrot.setPosition(dec.camrot.getPosition() - 0.001);
//        }*/
//
//        dec.fangle = Math.abs(Math.atan((dec.goal.getY(DistanceUnit.INCH) - dec.odo.getPosition().getY(
//                DistanceUnit.INCH)) / (dec.goal.getX(DistanceUnit.INCH) - dec.odo.getPosition().getX(
//                DistanceUnit.INCH))) * (180 / Math.PI));
//
//        //region - Turret
//        dec.goalangle = Math.atan((dec.goal.getX(DistanceUnit.INCH) - dec.odo.getPosition().getX(DistanceUnit.INCH)) /
//                (dec.goal.getY(DistanceUnit.INCH) - dec.odo.getPosition().getY(DistanceUnit.INCH))) * 180 / Math.PI;
//
//        if (dec.teamcolor.matches("Blue")) {
//            dec.goalangle = dec.goalangle - 180;
//        }
//
//        dec.TFangle = dec.getTurretAngle(dec.taxon.getTotalRotation()) + dec.odo.getPosition().getHeading(AngleUnit.DEGREES);
//
//        if (dec.TFangle > 180) {
//            dec.TFangle = dec.TFangle - 360;
//        } else if (dec.TFangle < -180) {
//            dec.TFangle = dec.TFangle + 360;
//        }
//
//        dec.Gangle = -dec.goalangle;
//
//        if (dec.Gangle < dec.tdmin) {
//            dec.GTangle = dec.tdmin;
//        } else if (dec.Gangle > dec.tdmax) {
//            dec.GTangle = dec.tdmax;
//        } else {
//            dec.GTangle = dec.Gangle;
//        }
//
//        dec.Tangle = dec.Gangle - dec.odo.getPosition().getHeading(AngleUnit.DEGREES);
//
//        if (dec.Tangle > 360) {
//            dec.Tangle = dec.Tangle - 360;
//        } else if (dec.Tangle < 0) {
//            dec.Tangle = dec.Tangle + 360;
//        }
//
//        if (dec.Tangle < dec.tdmin) {
//            dec.Tangle = dec.tdmin;
//        } else if (dec.Tangle > dec.tdmax) {
//            dec.Tangle = dec.tdmax;
//        }
//
//        if (!aprilTag.getDetections().isEmpty()) {
//            if (!TagHelper.getFirst(aprilTag).metadata.name.contains("Obelisk")) {
//                dec.goalcode0 = TagHelper.getFirst(aprilTag);
//            } else if (aprilTag.getDetections().size() > 1) {
//                dec.goalcode0 = aprilTag.getDetections().get(1);
//            } else {
//                dec.goalcode0 = null;
//            }
//
//            if (!(dec.goalcode0 == null)) {
//                if (dec.fangle < 55) {
//                    dec.center = (2.14806 * dec.fangle) + (119.0786 / (dec.fangle - 2.97963)) + 190.35734;
//                } else {
//                    dec.center = ((-0.0000018711211) * Math.pow(dec.fangle, 6)) + (0.00074689381 *
//                            Math.pow(dec.fangle, 5)) - (0.12314495 * Math.pow(dec.fangle, 4)) + (10.730059 *
//                            Math.pow(dec.fangle, 3)) - (520.81658 * Math.pow(dec.fangle, 2)) + (13343.2365 *
//                            dec.fangle) - 140564.244;
//                }
//
//                if ((dec.goalcode0.center.x) < (dec.center - dec.ctol)) {
//                    dec.taxon.setTargetRotation(dec.taxon.getTotalRotation() + ((dec.goalcode0.center.x - dec.center) / 3));
//                } else if ((dec.goalcode0.center.x) > (dec.center + dec.ctol)) {
//                    dec.taxon.setTargetRotation(dec.taxon.getTotalRotation() + ((dec.goalcode0.
//                            center.x - dec.center) / 3));
//                } else {
//                    dec.taxon.setTargetRotation(dec.taxon.getTotalRotation());
//                    //dec.coffset = dec.setTurretAngle(dec.Tangle) - dec.taxon.getTotalRotation();
//                }
//
//                telemetry.addData("Center: ", dec.goalcode0.center.x);
//                telemetry.addData("Target Center: ", dec.center);
//                telemetry.addData("Target Turret: ", dec.taxon.getTargetRotation());
//                telemetry.addData("Turret At: ", dec.taxon.getTotalRotation());
//
//                if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) > 200) {
//                    dec.turretpwrcoeff = 10;
//                } else {
//                    dec.turretpwrcoeff = Range.clip((Math.abs(dec.taxon.getTotalRotation() -
//                            dec.taxon.getTargetRotation()) / 20), 1, 10);
//                }
//
//                dec.turretpower = Range.clip((dec.tminp * dec.turretpwrcoeff), 0.06, 0.14); //0.09?
//
//                if (gamepad1.y) {
//                    if (dec.taxon.getTotalRotation() > (dec.taxon.getTargetRotation() + dec.ttol)) {
//                        dec.turret.setPower(dec.turretpower);
//                    } else if (dec.taxon.getTotalRotation() < (dec.taxon.getTargetRotation() - dec.ttol)) {
//                        dec.turret.setPower(-dec.turretpower);
//                    } else {
//                        dec.turret.setPower(0);
//                    }
//                } else {
//                    dec.turret.setPower(0);
//                }
//            }
//        } else {
//            dec.turret.setPower(0);
//        }
//        //endregion
//
//        //endregion
//
//        /*if (gamepad2.dpad_up) {
//            mp = mp + 0.01;
//        } else if (gamepad2.dpad_down) {
//            mp = mp - 0.01;
//        }
//
//        if (mp > 1) {
//            mp = 1;
//        } else if (mp < 0) {
//            mp = 0;
//        }
//
//        if (gamepad2.left_stick_button) {
//            t = t + 0.1;
//        } else if (gamepad2.right_stick_button) {
//            t = t - 0.1;
//        }
//
//        if (t < 0) {
//            t = 0;
//        }*/
//
//        dec.goaldistanceX = dec.Bgoal.getX(DistanceUnit.INCH) - dec.odo.getPosition().getX(DistanceUnit.INCH);
//        dec.goaldistanceY = dec.Bgoal.getY(DistanceUnit.INCH) - dec.odo.getPosition().getY(DistanceUnit.INCH);
//        dec.goaldistanceT = Math.sqrt(Math.pow(dec.goaldistanceX, 2) + Math.pow(dec.goaldistanceY, 2));
//
//        telemetry.addData("Goal Dist: ", dec.goaldistanceT);
//
//        if (gamepad1.a) {
//            if (dec.goaldistanceT < 53.7) { //36 + 17.7
//                //dec.camrot.setPosition(0.36);
//            } else {
//                //dec.camrot.setPosition(0.43);
//            }
//        }
//
//        /*telemetry.addData("Camrot: ", dec.camrot.getPosition());
//        telemetry.addData("Team Color: ", dec.teamcolor);*/
//        telemetry.addData("F Angle: ", dec.fangle);
//        /*telemetry.addData("New Robot Heading (-90): ", dec.newRheading);
//        telemetry.addData("TurTarAngle: ", dec.turtarangle);
//        telemetry.addData("TurToAngle: ", dec.turtoangle);
//        //telemetry.addData("TPwrCoeff: ", dec.turretpwrcoeff);
//        telemetry.addData("TPwr: ", dec.turretpower);
//        /*telemetry.addData("MP: ", mp);
//        telemetry.addData("T: ", t);*/
//        //telemetry.addData("Turret Servo Position", dec.taxon.getCurrentAngle());
//        /*telemetry.addData("Turret Total Rotation", dec.taxon.getTotalRotation());
//        telemetry.addData("Turret Target Rotation", dec.taxon.getTargetRotation());*/
//        telemetry.addData("Hood Pos: ", dec.hood.getPosition());
//        telemetry.addData("Susan Pos: ", dec.susan.getPosition());
//        /*telemetry.addData("Launch Power: ", dec.launch.getPower());
//        telemetry.addData("Intake Power: ", dec.intake.getPower());
//        telemetry.addData("IntakeDos Power: ", dec.intakedos.getPower());
//        //telemetry.addData("Transfer Power: ", dec.transfer.getPower());*/
//        telemetry.addLine(String.format("OdoPos: %6.2f %6.2f %6.2f (+90)", dec.odo.getPosition().getX(DistanceUnit.INCH),
//                dec.odo.getPosition().getY(DistanceUnit.INCH), (dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) + 90));
//        telemetryAprilTag();
//
//        dec.robopos.x = dec.campos.x + (dec.camlenstorobocenter * Math.cos(dec.fangle));
//        dec.robopos.y = dec.campos.y + (dec.camlenstorobocenter * Math.sin(dec.fangle));
//        dec.robopos.z = dec.campos.z;
//
//        telemetry.addLine(String.format("Cam-Robo Pos: %6.2f %6.2f %6.2f ", dec.robopos.x, dec.robopos.y, dec.robopos.z));
//
//        telemetry.update();
//
//        robovel = Math.sqrt(Math.pow(dec.odo.getVelX(DistanceUnit.INCH), 2) + Math.pow(dec.odo.getVelY(DistanceUnit.INCH), 2));
//        if (robovel > lastvel) {
//            lastvel = robovel;
//            Log.d("Vel", "Velocity New:" + lastvel);
//        }
//
//    }   // end method runOpMode()
//
//    public void stop() {
//        Log.d("Vel", "Max Velocity:" + lastvel);
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
//        telemetry.addData("# AprilTags Detected", currentDetections.size());
//
//        // Step through the list of detections and display info for each one.
//        for (AprilTagDetection detection : currentDetections) {
//            if (detection.metadata != null) {
//                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
//                // Only use tags that don't have Obelisk in them
//                if (!detection.metadata.name.contains("Obelisk")) {
//                    dec.campos.x = detection.robotPose.getPosition().x;
//                    dec.campos.y = detection.robotPose.getPosition().y;
//                    dec.campos.z = detection.robotPose.getOrientation().getYaw(AngleUnit.DEGREES);
//
//                    telemetry.addLine(String.format("Center %6.1f %6.1f ", detection.center.x, detection.center.y));
//
//                    telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f ", dec.campos.x, dec.campos.y, dec.campos.z));
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
//    }
//}
