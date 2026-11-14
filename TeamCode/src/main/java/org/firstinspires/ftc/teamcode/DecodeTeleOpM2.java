//package org.firstinspires.ftc.teamcode;
//
//import android.util.Log;
//import android.util.Size;
//
//import com.qualcomm.hardware.lynx.LynxModule;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.AnalogInput;
//import com.qualcomm.robotcore.hardware.CRServo;
//import com.qualcomm.robotcore.hardware.ColorSensor;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
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
//@TeleOp(name = "TeleOpM2", group = "TeleOp")
//@Disabled
//public class DecodeTeleOpM2 extends OpMode {
//
//    DecodeConstants dec = new DecodeConstants();
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
//    double actualRPM = 0;
//    double g1dtime = 0;
//    double g1xbtime = 0;
//
//    double switchtime = 0;
//    double launchtime = 0;
//    double rapidtime = 0;
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
//        dec.odo.update();
//
//        telemetry.addData("Color: ", dec.teamcolor);
//        telemetry.addData("Pos: ", Math.round(dec.odo.getPosition().getX(DistanceUnit.INCH)) + ", " +
//                Math.round(dec.odo.getPosition().getY(DistanceUnit.INCH)) + ", " + Math.round(dec.odo.
//                getPosition().getHeading(AngleUnit.DEGREES)));
//    }
//
//    public void loop() {
//        //region - Updates
//        //telemetry.addData("Loop Time: ", runtime.milliseconds() - looptime);
//        //looptime = runtime.milliseconds();
//
//        dec.odo.update();
//        dec.taxon.update();
//
//        dec.f2c = dec.f2.getNormalizedColors();
//        dec.r2c = dec.r2.getNormalizedColors();
//        dec.l2c = dec.l2.getNormalizedColors();
//        //endregion
//
//        //region - Camera Localization
//        telemetryAprilTag();
//
//        dec.roboheading = dec.campos.z - dec.getTurretAngle(dec.taxon.getTotalRotation() - dec.toffset);
//
//        if (dec.roboheading > 180) {
//            dec.roboheading = dec.roboheading - 360;
//        } else if (dec.roboheading < (-180)) {
//            dec.roboheading = dec.roboheading + 360;
//        }
//
//        dec.robopos.x = dec.campos.x + (dec.turretRad * Math.sin(Math.toRadians(dec.TurFangle(dec.taxon.
//                getTotalRotation(), dec.toffset, dec.odo.getPosition()))));
//        dec.robopos.y = (dec.campos.y - (dec.turretRad * Math.cos(Math.toRadians(dec.TurFangle(dec.taxon.
//                getTotalRotation(), dec.toffset, dec.odo.getPosition()))))) * 1.04; //multiplier
//        dec.robopos.z = dec.roboheading;
//        //endregion
//
//        //region - Odo Reset
//        if (gamepad1.y) {
//            dec.odo.setPosition(new Pose2D(DistanceUnit.INCH, dec.robopos.x, dec.robopos.y, AngleUnit.DEGREES, dec.robopos.z));
//        }
//        //endregion
//
//        //region - Team Color
//        if (gamepad2.b) {
//            dec.goal = dec.Rgoal;
//            dec.teamcolor = "Red";
//        } else if (gamepad2.x) {
//            dec.goal = dec.Bgoal;
//            dec.teamcolor = "Blue";
//        }
//
//        telemetry.addData("Team Color: ", dec.teamcolor);
//        //endregion
//
//        //region - Color
//        if ((dec.f2c.blue > 0.004) && (dec.f2c.green > 0.004)) {
//            if ((dec.f2c.green / dec.f2c.red) > 1.7) {
//                dec.revf = "Green";
//            } else {
//                dec.revf = "Purple";
//            }
//        } else {
//            dec.revf = "None";
//        }
//
//        if ((dec.r2c.blue / dec.r2c.red) > dec.bratio) {
//            if ((dec.r2c.green / dec.r2c.red) > dec.gratio) {
//                dec.revr = "Green";
//            } else {
//                dec.revr = "Purple";
//            }
//        } else {
//            dec.revr = "None";
//        }
//
//        if ((dec.l2c.blue / dec.l2c.red) > dec.bratio) {
//            if ((dec.l2c.green / dec.l2c.red) > dec.gratio) {
//                dec.revl = "Green";
//            } else {
//                dec.revl = "Purple";
//            }
//        } else {
//            dec.revl = "None";
//        }
//
//        //(gamepad2.a || gamepad2.b || gamepad2.x || dec.intaking) && (launchtime < runtime.milliseconds())
//        if (gamepad1.a || dec.intaking) {
//            /*telemetry.addData("RevF: ", dec.revf);
//            telemetry.addData("RevR: ", dec.revr);
//            telemetry.addData("RevL: ", dec.revl);
//
//            if (dec.f0.getState()) {
//                telemetry.addData("BLF: ", "Purple");
//            } else if (dec.f1.getState()) {
//                telemetry.addData("BLF: ", "Green");
//            } else {
//                telemetry.addData("BLF: ", "None");
//            }
//
//            if (dec.r0.getState()) {
//                telemetry.addData("BLR: ", "Purple");
//            } else if (dec.r1.getState()) {
//                telemetry.addData("BLR: ", "Green");
//            } else {
//                telemetry.addData("BLR: ", "None");
//            }
//
//            if (dec.l0.getState()) {
//                telemetry.addData("BLL: ", "Purple");
//            } else if (dec.l1.getState()) {
//                telemetry.addData("BLL: ", "Green");
//            } else {
//                telemetry.addData("BLL: ", "None");
//            }*/
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
//        } else {
//            telemetry.addData("-------No Dice-------", "");
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
//        if ((gamepad1.left_trigger > 0.25) && (gamepad1.right_trigger > 0.25)) {
//            dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//            dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//            dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//            dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//            telemetry.addData("-------BRAKE!!!-------", "");
//        } else {
//            dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//            dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//            dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//            dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//
//            telemetry.addData("GOOO!!!", "");
//        }
//        //endregion
//
//        //region - Cam Rot
//        if (dec.goalD(dec.odo.getPosition(), dec.goal, true) < dec.camlim) { //false
//            dec.camrot.setPosition(dec.camclose);
//        } else {
//            dec.camrot.setPosition(dec.camlong);
//        }
//        //endregion
//
//        //region - Needed?
//        //region - Auto Goal Align
//        /*if (gamepad1.a) {
//            if (Math.abs(newheading - dec.fangle) > 5) {
//                if (newheading > dec.fangle) {
//                    dec.flpower = Range.clip((newheading - dec.fangle) / 90, 0.2, 1);
//                    dec.frpower = Range.clip((dec.fangle - newheading) / 90, -1, -0.2);
//                    dec.blpower = Range.clip((newheading - dec.fangle) / 90, 0.2, 1);
//                    dec.brpower = Range.clip((dec.fangle - newheading) / 90, -1, -0.2);
//                } else {
//                    dec.flpower = Range.clip((dec.fangle - newheading) / 90, -1, -0.2);
//                    dec.frpower = Range.clip((newheading - dec.fangle) / 90, 0.2, 1);
//                    dec.blpower = Range.clip((dec.fangle - newheading) / 90, -1, -0.2);
//                    dec.brpower = Range.clip((newheading - dec.fangle) / 90, 0.2, 1);
//                }
//            } else {
//                dec.flpower = 0;
//                dec.frpower = 0;
//                dec.blpower = 0;
//                dec.brpower = 0;
//            }
//
//            dec.fl.setPower(dec.flpower);
//            dec.fr.setPower(dec.frpower);
//            dec.bl.setPower(dec.blpower);
//            dec.br.setPower(dec.brpower);
//        }*/
//        //endregion
//        //endregion?
//
//        //region - Intake (Uno y Dos)
//        if (gamepad2.right_trigger > 0.2) {
//            dec.intake.setPower(gamepad2.right_trigger);
//            dec.intakedos.setPower(0.5); //0.25
//        } else if (gamepad2.left_trigger > 0.2) {
//            dec.intake.setPower(-gamepad2.left_trigger);
//            dec.intakedos.setPower(-0.5); //-0.25
//        } else {
//            dec.intake.setPower(0);
//            dec.intakedos.setPower(0);
//        }
//        //endregion
//
//        //region - Flywheel
//        telemetry.addData("Xg: ", dec.goalD(dec.odo.getPosition(), dec.goal, true));
//        telemetry.addData("Velocity in RPM =", dec.launch.getVelocity() * 60 / 28);
//        //telemetry.addData("Hood Position", dec.hoodpos);
//
//        dec.velocityT = dec.launchVelCalc(dec.goalD(dec.odo.getPosition(), dec.goal, true),
//                dec.launchy) / 60 * 28;
//
//        dec.launch.setVelocity(dec.velocityT + dec.launchvel);
//        dec.hood.setPosition(dec.launchHoodCalc(dec.goalD(dec.odo.getPosition(), dec.goal, true), dec.launchy));
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
//        telemetry.addData("Launch Vel: ", dec.launchvel);
//        //endregion
//
//        //region - RPM Calc
//        actualRPM = dec.launch.getVelocity() / 28 * 60;
//        //telemetry.addData("RPM: ", actualRPM);
//        //telemetry.addData("Ticks: ", dec.launch.getVelocity());
//        //endregion
//
//        ///
//        //region - Susan
//        /*
//        //region - Auto Launching
//        if (dec.canlaunch) {
//            if (gamepad2.b) {
//                dec.LAUNCH = dec.odo.getPosition();
//
//                if (launchtime < runtime.milliseconds()) {
//                    if (!dec.FC.matches("Green")) {
//                        if (dec.RC.matches("Green")) {
//                            if ((dec.susan.getPosition() == dec.susanpos1) || (dec.susan.getPosition() ==
//                                    dec.susanlaunch1)) {
//                                dec.susan.setPosition(dec.susanlaunch3);
//                                launchtime = runtime.milliseconds() + 1000;
//                                dec.launched = true;
//                            } else if ((dec.susan.getPosition() == dec.susanpos2) || (dec.susan.getPosition() ==
//                                    dec.susanlaunch2)) {
//                                dec.susan.setPosition(dec.susanlaunch1);
//                                launchtime = runtime.milliseconds() + 500;
//                                dec.launched = true;
//                            } else if ((dec.susan.getPosition() == dec.susanpos3) || (dec.susan.getPosition() ==
//                                    dec.susanlaunch3)) {
//                                dec.susan.setPosition(dec.susanlaunch2);
//                                launchtime = runtime.milliseconds() + 500;
//                                dec.launched = true;
//                            }
//                        } else if (dec.LC.matches("Green")) {
//                            if ((dec.susan.getPosition() == dec.susanpos1) || (dec.susan.getPosition() ==
//                                    dec.susanlaunch1)) {
//                                dec.susan.setPosition(dec.susanlaunch2);
//                                launchtime = runtime.milliseconds() + 500;
//                                dec.launched = true;
//                            } else if ((dec.susan.getPosition() == dec.susanpos2) || (dec.susan.getPosition() ==
//                                    dec.susanlaunch2)) {
//                                dec.susan.setPosition(dec.susanlaunch3);
//                                launchtime = runtime.milliseconds() + 500;
//                                dec.launched = true;
//                            } else if ((dec.susan.getPosition() == dec.susanpos3) || (dec.susan.getPosition() ==
//                                    dec.susanlaunch3)) {
//                                dec.susan.setPosition(dec.susanlaunch1);
//                                launchtime = runtime.milliseconds() + 1000;
//                                dec.launched = true;
//                            }
//                        } else {
//                            dec.launched = false;
//                        }
//                    } else {
//                        launchtime = runtime.milliseconds();
//                    }
//                } else {
//                    dec.launched = false;
//                }
//
//                //region - Fancy
//                /*dec.transfer.setPower(1);
//
//                if (dec.firstlaunch) {
//                    if (dec.green.matches("Left")) {
//                        if (dec.susan.getPosition() == dec.susanbtw12) {
//                            dec.susan.setPosition(dec.susanpos2);
//                            dec.full2 = false;
//                            dec.firstlaunch = false;
//                        } else if (dec.susan.getPosition() == dec.susanbtw23) {
//                            dec.susan.setPosition(dec.susanpos3);
//                            dec.full3 = false;
//                            dec.firstlaunch = false;
//                        } else if (dec.susan.getPosition() == dec.susanbtw31) {
//                            dec.susan.setPosition(dec.susanpos1);
//                            dec.full1 = false;
//                            dec.firstlaunch = false;
//                        }
//                    } else if (dec.green.matches("Right")) {
//                        if (dec.susan.getPosition() == dec.susanbtw12) {
//                            dec.susan.setPosition(dec.susanpos1);
//                            dec.full2 = false;
//                            dec.firstlaunch = false;
//                        } else if (dec.susan.getPosition() == dec.susanbtw23) {
//                            dec.susan.setPosition(dec.susanpos2);
//                            dec.full3 = false;
//                            dec.firstlaunch = false;
//                        } else if (dec.susan.getPosition() == dec.susanbtw31) {
//                            dec.susan.setPosition(dec.susanpos3);
//                            dec.full1 = false;
//                            dec.firstlaunch = false;
//                        }
//                    }
//                } else {
//                    if (dec.RC.matches("Green")) {
//                        if (dec.susan.getPosition() == dec.susanpos1) {
//                            dec.susan.setPosition(dec.susanpos3);
//                        } else if (dec.susan.getPosition() == dec.susanpos2) {
//                            dec.susan.setPosition(dec.susanpos1);
//                        } else if (dec.susan.getPosition() == dec.susanpos3) {
//                            dec.susan.setPosition(dec.susanpos2);
//                        }
//                    } else if (dec.LC.matches("Green")) {
//                        if (dec.susan.getPosition() == dec.susanpos1) {
//                            dec.susan.setPosition(dec.susanpos2);
//                        } else if (dec.susan.getPosition() == dec.susanpos2) {
//                            dec.susan.setPosition(dec.susanpos3);
//                        } else if (dec.susan.getPosition() == dec.susanpos3) {
//                            dec.susan.setPosition(dec.susanpos1);
//                        }
//                    }
//                }*
//                //endregion
//            } else if (gamepad2.x) {
//                dec.LAUNCH = dec.odo.getPosition();
//
//                if (launchtime < runtime.milliseconds()) {
//                    if (!dec.FC.matches("Purple")) {
//                        if (dec.RC.matches("Purple")) {
//                            if ((dec.susan.getPosition() == dec.susanpos1) || (dec.susan.getPosition() ==
//                                    dec.susanlaunch1)) {
//                                dec.susan.setPosition(dec.susanlaunch3);
//                                launchtime = runtime.milliseconds() + 1000;
//                                dec.launched = true;
//                            } else if ((dec.susan.getPosition() == dec.susanpos2) || (dec.susan.getPosition() ==
//                                    dec.susanlaunch2)) {
//                                dec.susan.setPosition(dec.susanlaunch1);
//                                launchtime = runtime.milliseconds() + 500;
//                                dec.launched = true;
//                            } else if ((dec.susan.getPosition() == dec.susanpos3) || (dec.susan.getPosition() ==
//                                    dec.susanlaunch3)) {
//                                dec.susan.setPosition(dec.susanlaunch2);
//                                launchtime = runtime.milliseconds() + 500;
//                                dec.launched = true;
//                            }
//                        } else if (dec.LC.matches("Purple")) {
//                            if ((dec.susan.getPosition() == dec.susanpos1) || (dec.susan.getPosition() ==
//                                    dec.susanlaunch1)) {
//                                dec.susan.setPosition(dec.susanlaunch2);
//                                launchtime = runtime.milliseconds() + 500;
//                                dec.launched = true;
//                            } else if ((dec.susan.getPosition() == dec.susanpos2) || (dec.susan.getPosition() ==
//                                    dec.susanlaunch2)) {
//                                dec.susan.setPosition(dec.susanlaunch3);
//                                launchtime = runtime.milliseconds() + 500;
//                                dec.launched = true;
//                            } else if ((dec.susan.getPosition() == dec.susanpos3) || (dec.susan.getPosition() ==
//                                    dec.susanlaunch3)) {
//                                dec.susan.setPosition(dec.susanlaunch1);
//                                launchtime = runtime.milliseconds() + 1000;
//                                dec.launched = true;
//                            }
//                        } else {
//                            dec.launched = false;
//                        }
//                    } else {
//                        launchtime = runtime.milliseconds();
//                    }
//                } else {
//                    dec.launched = false;
//                }
//
//                //region - Fancy
//                /*dec.transfer.setPower(1);
//
//                if (dec.firstlaunch) {
//                    if (dec.purple.matches("Left")) {
//                        if (dec.susan.getPosition() == dec.susanbtw12) {
//                            dec.susan.setPosition(dec.susanpos2);
//                            dec.full2 = false;
//                            dec.firstlaunch = false;
//                        } else if (dec.susan.getPosition() == dec.susanbtw23) {
//                            dec.susan.setPosition(dec.susanpos3);
//                            dec.full3 = false;
//                            dec.firstlaunch = false;
//                        } else if (dec.susan.getPosition() == dec.susanbtw31) {
//                            dec.susan.setPosition(dec.susanpos1);
//                            dec.full1 = false;
//                            dec.firstlaunch = false;
//                        }
//                    } else if (dec.purple.matches("Right")) {
//                        if (dec.susan.getPosition() == dec.susanbtw12) {
//                            dec.susan.setPosition(dec.susanpos1);
//                            dec.full2 = false;
//                            dec.firstlaunch = false;
//                        } else if (dec.susan.getPosition() == dec.susanbtw23) {
//                            dec.susan.setPosition(dec.susanpos2);
//                            dec.full3 = false;
//                            dec.firstlaunch = false;
//                        } else if (dec.susan.getPosition() == dec.susanbtw31) {
//                            dec.susan.setPosition(dec.susanpos3);
//                            dec.full1 = false;
//                            dec.firstlaunch = false;
//                        }
//                    }
//                } else {
//                    if (dec.RC.matches("Purple")) {
//                        if (dec.susan.getPosition() == dec.susanpos1) {
//                            dec.susan.setPosition(dec.susanpos3);
//                        } else if (dec.susan.getPosition() == dec.susanpos2) {
//                            dec.susan.setPosition(dec.susanpos1);
//                        } else if (dec.susan.getPosition() == dec.susanpos3) {
//                            dec.susan.setPosition(dec.susanpos2);
//                        }
//                    } else if (dec.LC.matches("Purple")) {
//                        if (dec.susan.getPosition() == dec.susanpos1) {
//                            dec.susan.setPosition(dec.susanpos2);
//                        } else if (dec.susan.getPosition() == dec.susanpos2) {
//                            dec.susan.setPosition(dec.susanpos3);
//                        } else if (dec.susan.getPosition() == dec.susanpos3) {
//                            dec.susan.setPosition(dec.susanpos1);
//                        }
//                    }
//                }*
//                //endregion
//            } else*/
//        if (gamepad2.a) {
//            dec.LAUNCH = dec.odo.getPosition();
//
//            if ((dec.susan.getPosition() != dec.susanpos1) && (dec.susan.getPosition() != dec.susanpos2) &&
//                    (dec.susan.getPosition() != dec.susanpos3) && (dec.susan.getPosition() != dec.susanlaunch1) &&
//                    (dec.susan.getPosition() != dec.susanlaunch2) && (dec.susan.getPosition() != dec.susanlaunch3)) {
//                dec.susan.setPosition(dec.susanlaunch1);
//
//                rapidtime = runtime.milliseconds() + 200; //500?
//            }
//
//            dec.transfer.setPower(1);
//
//            Log.d("Launch", "1");
//
//            if ((launchtime < runtime.milliseconds()) && (rapidtime < runtime.milliseconds())) {
//                Log.d("Launch", "2");
//
//                if (dec.FC.matches("None")) {
//                    if (dec.RC.matches("None")) {
//                        if (!dec.LC.matches("None")) {
//                            if ((dec.susan.getPosition() == dec.susanpos1) || (dec.susan.getPosition() ==
//                                    dec.susanlaunch1)) {
//                                dec.susan.setPosition(dec.susanlaunch2);
//                                launchtime = runtime.milliseconds() + 500;
//                                dec.launched = true;
//
//                                Log.d("Launch", "3");
//                            } else if ((dec.susan.getPosition() == dec.susanpos2) || (dec.susan.getPosition() ==
//                                    dec.susanlaunch2)) {
//                                dec.susan.setPosition(dec.susanlaunch3);
//                                launchtime = runtime.milliseconds() + 500;
//                                dec.launched = true;
//
//                                Log.d("Launch", "3");
//                            } else if ((dec.susan.getPosition() == dec.susanpos3) || (dec.susan.getPosition() ==
//                                    dec.susanlaunch3)) {
//                                dec.susan.setPosition(dec.susanlaunch1);
//                                launchtime = runtime.milliseconds() + 1000;
//                                dec.launched = true;
//
//                                Log.d("Launch", "3");
//                            } else {
//                                dec.susan.setPosition(dec.susanlaunch1);
//                                rapidtime = runtime.milliseconds() + 200; //500?
//
//                                Log.d("Launch", "4");
//                            }
//                        }
//                    } else {
//                        if ((dec.susan.getPosition() == dec.susanpos1) || (dec.susan.getPosition() ==
//                                dec.susanlaunch1)) {
//                            dec.susan.setPosition(dec.susanlaunch3);
//                            launchtime = runtime.milliseconds() + 1000;
//                            dec.launched = true;
//
//                            Log.d("Launch", "3");
//                        } else if ((dec.susan.getPosition() == dec.susanpos2) || (dec.susan.getPosition() ==
//                                dec.susanlaunch2)) {
//                            dec.susan.setPosition(dec.susanlaunch1);
//                            launchtime = runtime.milliseconds() + 500;
//                            dec.launched = true;
//
//                            Log.d("Launch", "3");
//                        } else if ((dec.susan.getPosition() == dec.susanpos3) || (dec.susan.getPosition() ==
//                                dec.susanlaunch3)) {
//                            dec.susan.setPosition(dec.susanlaunch2);
//                            launchtime = runtime.milliseconds() + 500;
//                            dec.launched = true;
//
//                            Log.d("Launch", "3");
//                        } else {
//                            dec.susan.setPosition(dec.susanlaunch1);
//                            rapidtime = runtime.milliseconds() + 200; //500?
//
//                            Log.d("Launch", "4");
//                        }
//                    }
//                } else {
//                    launchtime = runtime.milliseconds();
//                    dec.launched = true;
//
//                    Log.d("Launch", "3");
//                }
//
//                //region - Pos Based
//                    /*if ((dec.susan.getPosition() == dec.susanpos1) || (dec.susan.getPosition() == dec.susanbtw12)) {
//                        dec.susan.setPosition(dec.susanpos2);
//                        launchtime = runtime.milliseconds() + 300;
//
//                        Log.d("Launch", "3");
//                    } else if ((dec.susan.getPosition() == dec.susanpos2) || (dec.susan.getPosition() == dec.susanbtw23)) {
//                        dec.susan.setPosition(dec.susanpos3);
//                        launchtime = runtime.milliseconds() + 300;
//
//                        Log.d("Launch", "3");
//                    } else if ((dec.susan.getPosition() == dec.susanpos3) || (dec.susan.getPosition() == dec.susanbtw31)) {
//                        dec.susan.setPosition(dec.susanpos1);
//                        launchtime = runtime.milliseconds() + 300;
//
//                        Log.d("Launch", "3");
//                    }*/
//                //endregion
//            }
//            //}
//
//            Log.d("Launch", "Timer: " + ((launchtime + 500) < runtime.milliseconds()));
//
//            if ((launchtime + 2000) > runtime.milliseconds()) {
//                dec.transfer.setPower(1);
//
//                Log.d("Launch", "Transfer On");
//            } else {
//                dec.launched = false;
//            }
//        }
//
//        //telemetry.addData("Launched: ", dec.launched);
//        //endregion
//
//        //region - Auto Intaking
//        //telemetry.addData("ST - RT: ", (switchtime - runtime.milliseconds()));
//
//        if (/*(Math.abs(dec.intake.getPower()) > 0.2) &&*/ dec.intaking && (switchtime < runtime.milliseconds())) {
//            Log.d("Sorting", "0");
//            if (!dec.FC.matches("None")) {
//                Log.d("Sorting", "1");
//                if ((dec.susan.getPosition() == dec.susanpos1) || (dec.susan.getPosition() == dec.susanlaunch1)) {
//                    Log.d("Sorting", "2");
//                    if (dec.LC.matches("None")) {
//                        dec.susan.setPosition(dec.susanpos2);
//                        switchtime = runtime.milliseconds() + 500;
//                        Log.d("Sorting", "3");
//                    } else if (dec.RC.matches("None")) {
//                        dec.susan.setPosition(dec.susanpos3);
//                        switchtime = runtime.milliseconds() + 500;
//                        Log.d("Sorting", "4");
//                    }
//                } else if ((dec.susan.getPosition() == dec.susanpos2) || (dec.susan.getPosition() == dec.susanlaunch2)) {
//                    Log.d("Sorting", "5");
//                    if (dec.LC.matches("None")) {
//                        dec.susan.setPosition(dec.susanpos3);
//                        switchtime = runtime.milliseconds() + 500;
//                        Log.d("Sorting", "6");
//                    } else if (dec.RC.matches("None")) {
//                        dec.susan.setPosition(dec.susanpos1);
//                        switchtime = runtime.milliseconds() + 500;
//                        Log.d("Sorting", "7");
//                    }
//                } else if ((dec.susan.getPosition() == dec.susanpos3) || (dec.susan.getPosition() == dec.susanlaunch3)) {
//                    if (dec.RC.matches("None")) {
//                        dec.susan.setPosition(dec.susanpos2);
//                        switchtime = runtime.milliseconds() + 500;
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
//            }
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
//        //endregion
//
//        //region - Manual Transfer
//        if (!dec.launched) {
//            if (Math.abs(gamepad2.left_stick_y) > 0.2) {
//                dec.transfer.setPower(-gamepad2.left_stick_y);
//            } else {
//                dec.transfer.setPower(0);
//            }
//        }
//        //endregion
//
//        //region - Launch Mode
//        if (gamepad1.a) {
//            dec.launchy = true;
//            dec.susanprep = true;
//            dec.firstlaunch = true;
//            dec.intaking = false;
//
//            dec.LAUNCH = dec.odo.getPosition();
//        }
//
//        dec.launchdist = Math.sqrt(Math.pow((dec.LAUNCH.getX(DistanceUnit.INCH) - dec.odo.getPosition().
//                getX(DistanceUnit.INCH)), 2) + Math.pow((dec.LAUNCH.getY(DistanceUnit.INCH) - dec.odo.
//                getPosition().getY(DistanceUnit.INCH)), 2));
//
//        if (dec.launchdist > 10) {
//            dec.launchy = false;
//            dec.intaking = true;
//            dec.LAUNCH = dec.notlaunch;
//            //switchtime = runtime.milliseconds() + 500;
//
//            dec.transfer.setPower(0);
//            //dec.susan.setPosition(dec.susanpos1);
//        } else {
//            dec.intaking = false;
//        }
//
//        if (dec.susanprep) {
//            if ((dec.FC.matches("Purple") && dec.RC.matches("Green")) ||
//                    (dec.FC.matches("Green") && dec.RC.matches("Purple"))) {
//                if (dec.FC.matches("Green")) {
//                    dec.green = "Right";
//                    dec.purple = "Left";
//                } else {
//                    dec.green = "Left";
//
//                    if (dec.LC.matches("Purple")) {
//                        dec.purple = "Left";
//                    } else {
//                        dec.purple = "Right";
//                    }
//                }
//
//                if (dec.susan.getPosition() == dec.susanpos1) {
//                    dec.susan.setPosition(dec.susanbtw31);
//                } else if (dec.susan.getPosition() == dec.susanpos2) {
//                    dec.susan.setPosition(dec.susanbtw12);
//                } else if (dec.susan.getPosition() == dec.susanpos3) {
//                    dec.susan.setPosition(dec.susanbtw23);
//                }
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
//                if (dec.susan.getPosition() == dec.susanpos1) {
//                    dec.susan.setPosition(dec.susanbtw12);
//                } else if (dec.susan.getPosition() == dec.susanpos2) {
//                    dec.susan.setPosition(dec.susanbtw23);
//                } else if (dec.susan.getPosition() == dec.susanpos3) {
//                    dec.susan.setPosition(dec.susanbtw31);
//                }
//            } else if ((dec.RC.matches("Purple") && dec.LC.matches("Green")) ||
//                    (dec.RC.matches("Green") && dec.LC.matches("Purple"))) {
//                if (dec.RC.matches("Green")) {
//                    dec.green = "Left";
//                    dec.purple = "Right";
//                } else {
//                    dec.green = "Right";
//                    dec.purple = "Left";
//                }
//
//                if (dec.susan.getPosition() == dec.susanpos1) {
//                    dec.susan.setPosition(dec.susanbtw23);
//                } else if (dec.susan.getPosition() == dec.susanpos2) {
//                    dec.susan.setPosition(dec.susanbtw31);
//                } else if (dec.susan.getPosition() == dec.susanpos3) {
//                    dec.susan.setPosition(dec.susanbtw12);
//                }
//            }
//
//            dec.susanprep = false;
//        }
//        //endregion
//
//        //region - Angle to Goal
//        telemetry.addData("Angle to Goal: ", dec.FA2G(dec.odo.getPosition(), dec.goal));
//        //endregion
//
//        //region - Save QR Code
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
//                    }
//                } else {
//                    dec.goalcode = dec.goalcode0;
//                }
//            } catch (NullPointerException e) {
//                Log.d("CamE", "Lost GC0");
//            }
//        }
//        //endregion
//
//        //region - Cam Center Adjust
//        if (g1xbtime < runtime.milliseconds()) {
//            if (gamepad1.b) {
//                dec.cxoffset = dec.cxoffset + 20; //10
//                g1xbtime = runtime.milliseconds() + 200;
//            } else if (gamepad1.x) {
//                dec.cxoffset = dec.cxoffset - 20; //10
//                g1xbtime = runtime.milliseconds() + 200;
//            }
//        }
//
//        telemetry.addData("Center Target Offset: ", dec.cxoffset);
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
//            //dec.taxon.forceResetTotalRotation();
//            dec.turret.setPower(0);
//        }
//
//        telemetry.addData("T Offset: ", dec.toffset);
//
//        if (gamepad1.dpad_left && dec.fixing) {
//            //dec.taxon.forceResetTotalRotation();
//            dec.taxon.changeTargetRotation(-25);
//        }
//
//        if (dec.fixing) {
//            telemetry.addData("-------FIXING TURRET-------", "");
//
//            if (dec.taxon.getTotalRotation() > dec.taxon.getTargetRotation()) {
//                dec.turret.setPower(1);
//            } else {
//                dec.turret.setPower(0);
//                //dec.taxon.forceResetTotalRotation();
//            }
//        } else {
//            telemetry.addData("Turret Fixed", "");
//        }
//        //endregion
//
//        //region - Turret
//        /*telemetry.addData("Turret Field Angle: ", dec.TurFangle(dec.taxon.getTotalRotation(),
//                dec.toffset, dec.odo.getPosition()));*/
//
//        if (!dec.fixing) {
//            if (aprilTag.getDetections().isEmpty() || (Math.abs(dec.setTurretAngle(dec.odo.getPosition(),
//                    dec.goal, dec.teamcolor) - dec.taxon.getTotalRotation()) > 100)) {
//                dec.taxon.setTargetRotation(dec.setTurretAngle(dec.odo.getPosition(), dec.goal, dec.
//                        teamcolor) + dec.toffset + dec.camtaroffset + dec.cxoffset);
//            } else if (!(dec.goalcode == null)) {
//                if (dec.FA2G(dec.odo.getPosition(), dec.goal) < 55) {
//                    dec.center = (2.14806 * dec.FA2G(dec.odo.getPosition(), dec.goal)) + (119.0786 / (dec.FA2G(
//                            dec.odo.getPosition(), dec.goal) - 2.97963)) + 190.35734;
//                } else {
//                    dec.center = ((-0.0000018711211) * Math.pow(dec.FA2G(dec.odo.getPosition(), dec.goal), 6)) +
//                            (0.00074689381 * Math.pow(dec.FA2G(dec.odo.getPosition(), dec.goal), 5)) - (0.12314495 *
//                            Math.pow(dec.FA2G(dec.odo.getPosition(), dec.goal), 4)) + (10.730059 * Math.pow(dec.
//                            FA2G(dec.odo.getPosition(), dec.goal), 3)) - (520.81658 * Math.pow(dec.FA2G(dec.odo.
//                            getPosition(), dec.goal), 2)) + (13343.2365 * dec.FA2G(dec.odo.getPosition(),
//                            dec.goal)) - 140544.244; //140564.244
//                }
//
//                if ((dec.goalcode.center.x) < (dec.center - dec.ctol)) {
//                    dec.taxon.setTargetRotation(dec.taxon.getTotalRotation() + ((dec.goalcode.center.x -
//                            dec.center + dec.cxoffset) / 3));
//                } else if ((dec.goalcode.center.x) > (dec.center + dec.ctol)) {
//                    dec.taxon.setTargetRotation(dec.taxon.getTotalRotation() + ((dec.goalcode.center.x -
//                            dec.center + dec.cxoffset) / 3));
//                } else {
//                    dec.taxon.setTargetRotation(dec.taxon.getTotalRotation());
//                }
//
//                dec.camtaroffset = dec.taxon.getTargetRotation() - (dec.setTurretAngle(dec.odo.
//                        getPosition(), dec.goal, dec.teamcolor) + dec.toffset);
//            } else {
//                dec.taxon.setTargetRotation(dec.taxon.getTotalRotation());
//            }
//
//            if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) > 200) {
//                dec.turretpwrcoeff = 10;
//            } else {
//                dec.turretpwrcoeff = Range.clip((Math.abs(dec.taxon.getTotalRotation() -
//                        dec.taxon.getTargetRotation()) / 25), 1, 10); //20
//            }
//
//            if (dec.goalD(dec.odo.getPosition(), dec.goal, true) > 108) {
//                dec.turretpwrcoeff = dec.turretpwrcoeff / 2;
//            }
//
//            dec.turretpower = Range.clip((dec.tminp * dec.turretpwrcoeff), 0.08, 1); //0.09?
//
//            if (dec.launchy) {
//                if (dec.taxon.getTotalRotation() > (dec.taxon.getTargetRotation() + dec.ttol)) {
//                    dec.turret.setPower(dec.turretpower);
//                } else if (dec.taxon.getTotalRotation() < (dec.taxon.getTargetRotation() - dec.ttol)) {
//                    dec.turret.setPower(-dec.turretpower);
//                } else {
//                    dec.turret.setPower(0);
//                }
//            } else {
//                dec.turret.setPower(0);
//            }
//        }
//
//        telemetry.addData("Turret Target: ", dec.taxon.getTargetRotation());
//        telemetry.addData("Turret At 1: ", dec.taxon.getTotalRotation());
//        //endregion
//
//        //region - Telemetry
//        //telemetry.addData("Susan Pos: ", dec.susan.getPosition());
//        telemetry.addLine(String.format("OdoPos: %6.2f %6.2f %6.2f (-90)", dec.odo.getPosition().getX(DistanceUnit.INCH),
//                dec.odo.getPosition().getY(DistanceUnit.INCH), (dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) - 90));
//        telemetry.addLine(String.format("Robo Pos (Cam): %6.2f %6.2f %6.2f ", dec.robopos.x, dec.robopos.y, dec.robopos.z));
//
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
//        if (dec.goalcode != null) {
//            telemetry.addData("Goal Code Center X: ", dec.goalcode.center.x);
//        }
//
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
//        /*telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
//        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");*/
//
//    }
//}
