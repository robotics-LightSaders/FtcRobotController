package org.firstinspires.ftc.teamcode;

import android.util.Log;
import android.util.Size;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
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
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp(name = "SusanTest", group = "TeleOp")
@Disabled
public class SusanTest extends OpMode {

    DecodeConstants dec = new DecodeConstants();

    RevColorSensorV3 f2;
    NormalizedRGBA f2c;
    RevColorSensorV3 r2;
    NormalizedRGBA r2c;
    RevColorSensorV3 l2;
    NormalizedRGBA l2c;


    private ElapsedTime runtime = new ElapsedTime();
    double looptime = 0;

    double x;
    double y;
    double z;


    double switchtime = 0;
    double launchtime = -2000;
    double rapidtime = 0;

    double intakemovetime = 1000;//800
    double movetime = 800;
    double longmovetime = 1100;

    double launchDelay = 2000; //2000

    double transfertime = 0;

    double preptime = 0;

    @Override
    public void init() {
        dec.fl = hardwareMap.get(DcMotor.class, "fl");
        dec.fr = hardwareMap.get(DcMotor.class, "fr");
        dec.bl = hardwareMap.get(DcMotor.class, "bl");
        dec.br = hardwareMap.get(DcMotor.class, "br");


        dec.intake = hardwareMap.get(DcMotor.class, "intake");
        dec.transfer = hardwareMap.get(DcMotor.class, "transfer");
        dec.launch = hardwareMap.get(DcMotorEx.class, "launch");


        dec.susan = hardwareMap.get(Servo.class, "susan");


        dec.intakedos = hardwareMap.get(CRServo.class, "intakedos");


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


        // Turns on bulk reading
        for (LynxModule module : hardwareMap.getAll(LynxModule.class)) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        // RUE limits max motor speed to 85% by default
        // Raise that limit to 100%


        // Turn on RUN_USING_ENCODER


        // Set PIDF Coefficients with voltage compensated feedforward value

        dec.fl.setPower(0);
        dec.fr.setPower(0);
        dec.bl.setPower(0);
        dec.br.setPower(0);


        dec.intake.setPower(0);
        dec.intakedos.setPower(0);
        dec.transfer.setPower(0);
        dec.launch.setPower(0);
        dec.transferPower = 0;

        dec.susan.setPosition(dec.susanpos1);


        runtime.reset();
        looptime = runtime.milliseconds();
    }


    public void loop() {
        //region - Updates
        //telemetry.addData("Loop Time: ", runtime.milliseconds() - looptime);
        //looptime = runtime.milliseconds();


        f2c = f2.getNormalizedColors();
        r2c = r2.getNormalizedColors();
        l2c = l2.getNormalizedColors();
        //endregion


        if (dec.roboheading > 180) {
            dec.roboheading = dec.roboheading - 360;
        } else if (dec.roboheading < (-180)) {
            dec.roboheading = dec.roboheading + 360;
        }


        //endregion


        //region - Color
        if ((f2.getDistance(DistanceUnit.CM) < 4)) {//dec.f2c.green > .0004 && dec.f2c.red > .0004
            if ((f2c.green / f2c.red) > dec.gratio) {
                dec.revf = "Green";
            } else {
                dec.revf = "Purple";
            }
        } else {
            dec.revf = "None";
        }

        if ((r2.getDistance(DistanceUnit.CM) < 5)) {
            if ((r2c.green / r2c.red) > dec.gratio) {
                dec.revr = "Green";
            } else {
                dec.revr = "Purple";
            }
        } else {
            dec.revr = "None";
        }

        if ((l2.getDistance(DistanceUnit.CM) < 4)) {
            if ((l2c.green / l2c.red) > dec.gratio) {
                dec.revl = "Green";
            } else {
                dec.revl = "Purple";
            }
        } else {
            dec.revl = "None";
        }

        //(gamepad2.a || gamepad2.b || gamepad2.x || dec.intaking) && (launchtime < runtime.milliseconds())
        if (((gamepad1.a || dec.intaking)) /*&& ((dec.scantime + 300) < runtime.milliseconds())*/ && (((dec.susan.getPosition() < (dec.susanpos1 + .01)) && (dec.susan.getPosition() > (dec.susanpos1 - .01))) || ((dec.susan.getPosition() < (dec.susanpos2 + .01)) && (dec.susan.getPosition() > (dec.susanpos2 - .01))) ||
                ((dec.susan.getPosition() < (dec.susanpos3 + .01)) && (dec.susan.getPosition() > (dec.susanpos3 - .01))) || ((dec.susan.getPosition() < (dec.susanlaunch1 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch1 - .01))) ||
                ((dec.susan.getPosition() < (dec.susanlaunch2 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch2 - .01))) || ((dec.susan.getPosition() < (dec.susanlaunch3 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch3 - .01))))) {

            dec.scantime = runtime.milliseconds();

            if (dec.f0.getState() || dec.revf.matches("Purple")) {
                telemetry.addData("F Color: ", "Purple");

                dec.FC = "Purple";

            } else if (dec.f1.getState() || dec.revf.matches("Green")) {
                telemetry.addData("F Color: ", "Green");
                dec.FC = "Green";
            } else {
                telemetry.addData("F No Color", "");
                dec.FC = "None";
            }


            if (dec.r0.getState() || dec.revr.matches("Purple")) {
                telemetry.addData("R Color: ", "Purple");
                dec.RC = "Purple";
            } else if (dec.r1.getState() || dec.revr.matches("Green")) {
                telemetry.addData("R Color: ", "Green");
                dec.RC = "Green";
            } else {
                telemetry.addData("R No Color", "");
                dec.RC = "None";
            }

            if (dec.l0.getState() || dec.revl.matches("Purple")) {
                telemetry.addData("L Color: ", "Purple");
                dec.LC = "Purple";
            } else if (dec.l1.getState() || dec.revl.matches("Green")) {
                telemetry.addData("L Color: ", "Green");
                dec.LC = "Green";
            } else {
                telemetry.addData("L No Color", "");
                dec.LC = "None";
            }
        } else {
            telemetry.addData("-------No Dice-------", "");
        }

        telemetry.addData("brushlandf purple", dec.f0.getState() );
        telemetry.addData("brushlandf green", dec.f1.getState() );
        telemetry.addData("revf red", f2c.red);
        telemetry.addData("revf green", f2c.green);
        telemetry.addData("revf blue", f2c.blue);
        telemetry.addData("revf dis",f2.getDistance(DistanceUnit.CM));
        telemetry.addData("revf sees", dec.revf);
        telemetry.addData("brushlandr purple", dec.r0.getState() );
        telemetry.addData("brushlandr green", dec.r1.getState() );
        telemetry.addData("revr red", r2c.red);
        telemetry.addData("revr green", r2c.green);
        telemetry.addData("revr blue", r2c.blue);
        telemetry.addData("revr dis",r2.getDistance(DistanceUnit.CM));
        telemetry.addData("revr sees", dec.revr);
        telemetry.addData("brushlandl purple", dec.l0.getState() );
        telemetry.addData("brushlandl green", dec.l1.getState() );
        telemetry.addData("revl red", l2c.red);
        telemetry.addData("revl green", l2c.green);
        telemetry.addData("revl blue", l2c.blue);
        telemetry.addData("revl dis",l2.getDistance(DistanceUnit.CM));
        telemetry.addData("revl sees", dec.revl);
        telemetry.addData("Susan pos", dec.susan.getPosition());
        telemetry.addData("scantime", dec.scantime);


        //endregion

        //region - Driving
        x = gamepad1.right_stick_x;
        y = -gamepad1.left_stick_y;
        z = gamepad1.left_stick_x;

     //   dec.flpower = Range.clip(y + x + z, -1, 1);
       // dec.frpower = Range.clip(y - x - z, -1, 1);
  //      dec.blpower = Range.clip(y + x - z, -1, 1);
  //      dec.brpower = Range.clip(y - x + z, -1, 1);

        dec.fl.setPower(dec.flpower);
        dec.fr.setPower(dec.frpower);
        dec.bl.setPower(dec.blpower);
        dec.br.setPower(dec.brpower);

        if ((gamepad1.left_trigger > 0.25) && (gamepad1.right_trigger > 0.25)) {
            dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            telemetry.addData("-------BRAKE!!!-------", "");
        } else {
            dec.fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            dec.fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            dec.bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            dec.br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

            telemetry.addData("GOOO!!!", "");
        }
        //endregion


        //endregion

        //region - Needed?
        //region - Auto Goal Align
        /*if (gamepad1.a) {
            if (Math.abs(newheading - dec.fangle) > 5) {
                if (newheading > dec.fangle) {
                    dec.flpower = Range.clip((newheading - dec.fangle) / 90, 0.2, 1);
                    dec.frpower = Range.clip((dec.fangle - newheading) / 90, -1, -0.2);
                    dec.blpower = Range.clip((newheading - dec.fangle) / 90, 0.2, 1);
                    dec.brpower = Range.clip((dec.fangle - newheading) / 90, -1, -0.2);
                } else {
                    dec.flpower = Range.clip((dec.fangle - newheading) / 90, -1, -0.2);
                    dec.frpower = Range.clip((newheading - dec.fangle) / 90, 0.2, 1);
                    dec.blpower = Range.clip((dec.fangle - newheading) / 90, -1, -0.2);
                    dec.brpower = Range.clip((newheading - dec.fangle) / 90, 0.2, 1);
                }
            } else {
                dec.flpower = 0;
                dec.frpower = 0;
                dec.blpower = 0;
                dec.brpower = 0;
            }

            dec.fl.setPower(dec.flpower);
            dec.fr.setPower(dec.frpower);
            dec.bl.setPower(dec.blpower);
            dec.br.setPower(dec.brpower);
        }*/
        //endregion
        //endregion?

        //region - Intake (Uno y Dos)
        if (gamepad2.right_trigger > 0.2) {
            dec.intake.setPower(gamepad2.right_trigger);
            dec.intakedos.setPower(0.5); //0.25
        } else if (gamepad2.left_trigger > 0.2) {
            dec.intake.setPower(-gamepad2.left_trigger);
            dec.intakedos.setPower(-0.5); //-0.25
        } else {
            dec.intake.setPower(0);
            dec.intakedos.setPower(0);
        }


        if (gamepad1.y) {

            telemetry.addData("FC", dec.FC);
            telemetry.addData("RC", dec.RC);
            telemetry.addData("LC", dec.LC);


            if (((dec.susan.getPosition() < (dec.susanpos1 - .01)) && (dec.susan.getPosition() > (dec.susanpos1 + .01))) && ((dec.susan.getPosition() < (dec.susanpos2 - .01)) && (dec.susan.getPosition() > (dec.susanpos2 + .01))) &&
                    ((dec.susan.getPosition() < (dec.susanpos3 - .01)) && (dec.susan.getPosition() > (dec.susanpos3 + .01))) && ((dec.susan.getPosition() < (dec.susanlaunch1 - .01)) && (dec.susan.getPosition() > (dec.susanlaunch1 + .01))) &&
                    ((dec.susan.getPosition() < (dec.susanlaunch2 - .01)) && (dec.susan.getPosition() > (dec.susanlaunch2 + .01))) && ((dec.susan.getPosition() < (dec.susanlaunch3 - .01)) && (dec.susan.getPosition() > (dec.susanlaunch3 + .01)))) {
                dec.susan.setPosition(dec.susanlaunch1);

                rapidtime = runtime.milliseconds() + 500; //500? //was 200
                Log.d("Launch", "12");
            }

            //  dec.intaking = false;

            dec.transferPower = 1;
            dec.transfer.setPower(1);
            dec.launch.setPower(.6);
            Log.d("Transfer", "ON 2");

            Log.d("Launch", "1");
            Log.d("Susan", String.valueOf(dec.susan.getPosition()));
            Log.d("Launch Time LT", String.valueOf(launchtime));
            Log.d("Launch Time RPD", String.valueOf(rapidtime));
            Log.d("Launch Time RNT", String.valueOf(runtime.milliseconds()));


            if (launchtime > runtime.milliseconds()) {
                telemetry.addData("launchtime", "paused");
            }

            if ((launchtime < runtime.milliseconds()) && (rapidtime < runtime.milliseconds())) {
                Log.d("Launch", "2");

                telemetry.addData("launchtime", "GO");

                if (dec.FC.matches("None")) {
                    if (dec.RC.matches("None")) {
                        if (!dec.LC.matches("None")) {
                            if ((dec.susan.getPosition() == dec.susanpos1) || (dec.susan.getPosition() ==
                                    dec.susanlaunch1)) {
                                dec.susan.setPosition(dec.susanlaunch2);
                                launchtime = runtime.milliseconds() + movetime;
                                dec.launched = true;

                                Log.d("Launch", "3");
                            } else if ((dec.susan.getPosition() == dec.susanpos2) || (dec.susan.getPosition() ==
                                    dec.susanlaunch2)) {
                                dec.susan.setPosition(dec.susanlaunch3);
                                launchtime = runtime.milliseconds() + movetime;
                                dec.launched = true;

                                Log.d("Launch", "4");
                            } else if ((dec.susan.getPosition() == dec.susanpos3) || (dec.susan.getPosition() ==
                                    dec.susanlaunch3)) {
                                dec.susan.setPosition(dec.susanlaunch1);
                                launchtime = runtime.milliseconds() + longmovetime;
                                dec.launched = true;

                                Log.d("Launch", "5");
                            } else {
                                dec.susan.setPosition(dec.susanlaunch1);
                                rapidtime = runtime.milliseconds() + 500; //500?

                                Log.d("Launch", "6");
                            }
                        }
                    } else {
                        if ((((dec.susan.getPosition() < (dec.susanpos1 + .01)) && (dec.susan.getPosition() > (dec.susanpos1 - .01)))) || (((dec.susan.getPosition() < (dec.susanlaunch1 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch1 - .01))))) {
                            dec.susan.setPosition(dec.susanlaunch3);
                            launchtime = runtime.milliseconds() + longmovetime;
                            dec.launched = true;

                            Log.d("Launch", "7");
                        } else if ((((dec.susan.getPosition() < (dec.susanpos2 + .01)) && (dec.susan.getPosition() > (dec.susanpos2 - .01)))) || (((dec.susan.getPosition() < (dec.susanlaunch2 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch2 - .01))))) {
                            dec.susan.setPosition(dec.susanlaunch1);
                            launchtime = runtime.milliseconds() + movetime;
                            dec.launched = true;

                            Log.d("Launch", "8");
                        } else if ((((dec.susan.getPosition() < (dec.susanpos3 + .01)) && (dec.susan.getPosition() > (dec.susanpos3 - .01)))) || (((dec.susan.getPosition() < (dec.susanlaunch3 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch3 - .01))))) {
                            dec.susan.setPosition(dec.susanlaunch2);
                            launchtime = runtime.milliseconds() + movetime;
                            dec.launched = true;

                            Log.d("Launch", "9");
                        } else {
                            dec.susan.setPosition(dec.susanlaunch1);
                            rapidtime = runtime.milliseconds() + 500; //500?

                            Log.d("Launch", "10");
                        }
                    }
                } else {
                    launchtime = runtime.milliseconds();
                    dec.launched = true;
                    Log.d("Launch", "11");
                    Log.d("f color is", dec.FC);
                    if (((((dec.susan.getPosition() < (dec.susanpos1 + .01)) && (dec.susan.getPosition() > (dec.susanpos1 - .01)))) || (((dec.susan.getPosition() < (dec.susanlaunch1 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch1 - .01)))))) {
                        dec.susan.setPosition(dec.susanlaunch1);
                        launchtime = runtime.milliseconds() + longmovetime;
                        dec.launched = true;

                        Log.d("Launch", "13");
                    } else if (((((dec.susan.getPosition() < (dec.susanpos2 + .01)) && (dec.susan.getPosition() > (dec.susanpos2 - .01)))) || (((dec.susan.getPosition() < (dec.susanlaunch2 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch2 - .01)))))) {
                        dec.susan.setPosition(dec.susanlaunch2);
                        launchtime = runtime.milliseconds() + movetime;
                        dec.launched = true;

                        Log.d("Launch", "14");
                    } else if (((((dec.susan.getPosition() < (dec.susanpos3 + .01)) && (dec.susan.getPosition() > (dec.susanpos3 - .01)))) || (((dec.susan.getPosition() < (dec.susanlaunch3 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch3 - .01)))))) {
                        dec.susan.setPosition(dec.susanlaunch3);
                        launchtime = runtime.milliseconds() + movetime;
                        dec.launched = true;

                        Log.d("Launch", "15");
                    } else {
                        dec.susan.setPosition(dec.susanlaunch1);
                        rapidtime = runtime.milliseconds() + 500; //500?

                        Log.d("Launch", "16");
                    }
                }


            }
            //}

            Log.d("Time", "Timer: " + ((launchtime + 500) < runtime.milliseconds()));
/*
            if (((launchtime + 2000) > runtime.milliseconds())) {
                dec.transferPower = 1;
                dec.transfer.setPower(1);
                dec.launch.setPower(.4);

                Log.d("Transfer", "Transfer On");
            } else {
                dec.launched = false;
               // Log.d("Transfer", "Transfer Off");
            }*/
        } else if
        (((launchtime + launchDelay) > runtime.milliseconds())) {
            dec.transferPower = 1;
            dec.transfer.setPower(1);
            dec.launch.setPower(.6);

            Log.d("Transfer", "Transfer On");
        } else {
            dec.launched = false;
            // Log.d("Transfer", "Transfer Off");
        }


        //telemetry.addData("Launched: ", dec.launched);
        //endregion

        //region - Auto Intaking
        //telemetry.addData("ST - RT: ", (switchtime - runtime.milliseconds()));

        if (/*(Math.abs(dec.intake.getPower()) > 0.2) &&*/(!dec.susanprep) && (dec.transferPower == 0) && dec.intaking && (switchtime < runtime.milliseconds())) {

            Log.d("Sorting", "0");

            dec.purpleLaunched = false;
            dec.greenLaunched = false;
            dec.intake.setPower(1);
            if (!dec.FC.matches("None")) {
                Log.d("Sorting", "1");
                if ((((dec.susan.getPosition() < (dec.susanpos1 + .01)) && (dec.susan.getPosition() > (dec.susanpos1 - .01)))) || (dec.susan.getPosition() == dec.susanlaunch1)) {
                    Log.d("Sorting", "2");
                    if (dec.LC.matches("None")) {
                        dec.susan.setPosition(dec.susanpos2);
                        switchtime = runtime.milliseconds() + intakemovetime;
                        Log.d("Sorting", "3");
                    } else if (dec.RC.matches("None")) {
                        dec.susan.setPosition(dec.susanpos3);
                        switchtime = runtime.milliseconds() + intakemovetime;
                        Log.d("Sorting", "4");
                    }
                } else if ((((dec.susan.getPosition() < (dec.susanpos2 + .01)) && (dec.susan.getPosition() > (dec.susanpos2 - .01)))) || (dec.susan.getPosition() == dec.susanlaunch2)) {
                    Log.d("Sorting", "5");
                    if (dec.LC.matches("None")) {
                        dec.susan.setPosition(dec.susanpos3);
                        switchtime = runtime.milliseconds() + intakemovetime;
                        Log.d("Sorting", "6");
                    } else if (dec.RC.matches("None")) {
                        dec.susan.setPosition(dec.susanpos1);
                        switchtime = runtime.milliseconds() + intakemovetime;
                        Log.d("Sorting", "7");
                    }
                } else if ((((dec.susan.getPosition() < (dec.susanpos3 + .01)) && (dec.susan.getPosition() > (dec.susanpos3 - .01)))) || (dec.susan.getPosition() == dec.susanlaunch3)) {
                    if (dec.RC.matches("None")) {
                        dec.susan.setPosition(dec.susanpos2);
                        switchtime = runtime.milliseconds() + intakemovetime;
                        Log.d("Sorting", "8");
                    } else if (dec.LC.matches("None")) {
                        dec.susan.setPosition(dec.susanpos1);
                        switchtime = runtime.milliseconds();
                        Log.d("Sorting", "9");
                    }
                } else {
                    //dec.susan.setPosition(dec.susanpos1);
                    switchtime = runtime.milliseconds();
                    Log.d("Sorting", "11");
                }
            } else if (dec.RC.matches("None") && dec.LC.matches("None")) {
                dec.susan.setPosition(dec.susanpos1);
                switchtime = runtime.milliseconds();
                Log.d("Sorting", "10");
            } else {
                if ((((dec.susan.getPosition() < (dec.susanlaunch1 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch1 - .01)))))
                {
                    dec.susan.setPosition(dec.susanpos1);
                    Log.d("Sorting", "21");
                }else if((((dec.susan.getPosition() < (dec.susanlaunch2 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch2 - .01))))){
                    dec.susan.setPosition(dec.susanpos2);
                    Log.d("Sorting", "22");
                }else if((((dec.susan.getPosition() < (dec.susanlaunch3 + .01)) && (dec.susan.getPosition() > (dec.susanlaunch3 - .01))))){
                    dec.susan.setPosition(dec.susanpos3);
                    Log.d("Sorting", "23");
                }
            }
        } else {
            dec.intake.setPower(1);// was .4 .3
            Log.d("Sorting","20");
            Log.d("NotSort","");
           /* */
        }
        //endregion

        //region - Manual
        if (gamepad2.dpad_left) {
            if (dec.intaking) {
                dec.susan.setPosition(dec.susanpos1);
            } else {
                dec.susan.setPosition(dec.susanlaunch1);
            }
        } else if (gamepad2.dpad_down) {
            if (dec.intaking) {
                dec.susan.setPosition(dec.susanpos2);
            } else {
                dec.susan.setPosition(dec.susanlaunch2);
            }
        } else if (gamepad2.dpad_right) {
            if (dec.intaking) {
                dec.susan.setPosition(dec.susanpos3);
            } else {
                dec.susan.setPosition(dec.susanlaunch3);
            }
        } else if (gamepad2.dpad_up) {
            if (dec.susan.getPosition() < 0.2) {
                dec.susan.setPosition(dec.susanpos1);
            } else if (dec.susan.getPosition() < 0.6) {
                dec.susan.setPosition(dec.susanpos2);
            } else {
                dec.susan.setPosition(dec.susanpos3);
            }
        }
        //endregion
        //endregion

        //region - Manual Transfer
        if (!dec.launched) {
            if (Math.abs(gamepad2.left_stick_y) > 0.5) {
                dec.transfer.setPower(-gamepad2.left_stick_y);
                dec.transferPower = -gamepad2.left_stick_y;
                Log.d("Transfer","stick ON");
            } else {

                dec.transferPower = 0;
                dec.transfer.setPower(0);
                dec.launch.setPower(0);
                Log.d("Transfer","Off 2");
              // dec.intaking = true;

            }

        }
        //endregion

        //region - Launch Mode
        if (gamepad1.a) {
            dec.launchy = true;
            dec.susanprep = true;
            dec.firstlaunch = true;
           // dec.intaking = false;

        }


        if ((dec.susanprep) && preptime < runtime.milliseconds() && !(((dec.susan.getPosition() < (dec.susanbtw12 + .01)) && (dec.susan.getPosition() > (dec.susanbtw12 - .01))) || ((dec.susan.getPosition() < (dec.susanbtw23 + .01)) && (dec.susan.getPosition() > (dec.susanbtw23 - .01))))) {


            Log.d("Prep Time PRP",String.valueOf(preptime));
            Log.d("Prep Time RNT",String.valueOf(runtime.milliseconds()));
            Log.d("Prep FC",dec.FC);
            Log.d("Prep RC",dec.RC);
            Log.d("Prep LC",dec.LC);
            Log.d("Prep Susan Pos",String.valueOf(dec.susan.getPosition()));
            Log.d("Prep", "1");

            if ((dec.FC.matches("Purple") && dec.RC.matches("Green")) ||
                    (dec.FC.matches("Green") && dec.RC.matches("Purple"))) {
                Log.d("Prep", "2");
                if (dec.FC.matches("Green")) {
                    Log.d("Prep", "3");
                    dec.green = "Right";
                    dec.purple = "Left";//swapped
                } else {
                    if(dec.RC.matches("Green")) {
                        Log.d("Prep", "4");
                        dec.green = "Left";
                    }
                    if (dec.FC.matches("Purple")) { //was lc now rc
                        Log.d("Prep", "5");
                        dec.purple = "Right"; //swapped
                    } else {
                        Log.d("Prep", "6");
                        dec.purple = "Right";
                    }
                }





                if ((dec.susan.getPosition() < (dec.susanpos1 + .01)) && (dec.susan.getPosition() > (dec.susanpos1 - .01))) {
                    Log.d("Prep", "7");
                    if ((dec.LC == "Purple" && dec.RC == "Green") || (dec.LC == "Green" && dec.RC == "Purple")) {
                        dec.susan.setPosition(dec.susanbtw23);
                        Log.d("Prep","25");
                        if(dec.FC == "Purple"){
                            dec.purple2 = "Back";
                            Log.d("Prep","45");
                        } else if(dec.FC == "Green"){
                            dec.green2 = "Back";
                            Log.d("Prep","46");
                        }


                    } else {
                        dec.susan.setPosition(dec.susanbtw12);
                        Log.d("Prep","26");
                        if(dec.FC == "Purple"){
                            dec.purple = "Left";
                            Log.d("Prep","29");
                            if(dec.LC == "Green"){
                                dec.green = "Right";
                                Log.d("Prep","30");

                            }
                        } else if (dec.FC == "Green") {
                            dec.green = "Left";
                            Log.d("Prep","31");

                            if(dec.LC == "Purple"){
                                dec.purple = "Right";
                                Log.d("Prep","32");

                            }

                        }
                        if(dec.RC == "Purple"){
                            dec.purple2 = "Back";
                            Log.d("Prep","47");
                        } else if(dec.RC == "Green"){
                            dec.green2 = "Back";
                            Log.d("Prep","48");
                        }



                    }

                    // dec.susan.setPosition(dec.susanbtw31);
                } else if ((dec.susan.getPosition() < (dec.susanpos2 + .01)) && (dec.susan.getPosition() > (dec.susanpos2 - .01))) {
                    Log.d("Prep", "8");
                    dec.susan.setPosition(dec.susanbtw12);
                    if(dec.LC == "Purple"){
                        dec.purple2 = "Back";
                        Log.d("Prep","49");
                    } else if(dec.LC == "Green"){
                        dec.green2 = "Back";
                        Log.d("Prep","50");
                    }
                } else if ((dec.susan.getPosition() < (dec.susanpos3 + .01)) && (dec.susan.getPosition() > (dec.susanpos3 - .01))) {
                    Log.d("Prep", "9");
                    dec.susan.setPosition(dec.susanbtw23);
                    if(dec.LC == "Purple"){
                        dec.purple2 = "Back";
                        Log.d("Prep","51");
                    } else if(dec.LC == "Green"){
                        dec.green2 = "Back";
                        Log.d("Prep","52");
                    }
                }
            } else if ((dec.FC.matches("Purple") && dec.LC.matches("Green")) ||
                    (dec.FC.matches("Green") && dec.LC.matches("Purple"))) {
                Log.d("Prep", "10");
                if (dec.FC.matches("Green")) {
                    Log.d("Prep", "11");
                    dec.green = "Left";
                    dec.purple = "Right";
                } else {
                    Log.d("Prep", "12");
                    dec.green = "Right";
                    dec.purple = "Left";
                }



                if ((dec.susan.getPosition() < (dec.susanpos1 + .01)) && (dec.susan.getPosition() > (dec.susanpos1 - .01))) {
                    Log.d("Prep", "13");
                    dec.susan.setPosition(dec.susanbtw12);
                    if(dec.RC == "Purple"){
                        dec.purple2 = "Back";
                        Log.d("Prep","53");
                    } else if(dec.RC == "Green"){
                        dec.green2 = "Back";
                        Log.d("Prep","54");
                    }
                } else if ((dec.susan.getPosition() < (dec.susanpos2 + .01)) && (dec.susan.getPosition() > (dec.susanpos2 - .01))) {
                    Log.d("Prep", "14");
                    dec.susan.setPosition(dec.susanbtw23);
                    if(dec.RC == "Purple"){
                        dec.purple2 = "Back";
                        Log.d("Prep","55");
                    } else if(dec.RC == "Green"){
                        dec.green2 = "Back";
                        Log.d("Prep","56");
                    }
                } else if ((dec.susan.getPosition() < (dec.susanpos3 + .01)) && (dec.susan.getPosition() > (dec.susanpos3 - .01))) {
                    Log.d("Prep", "15");
                    if ((dec.LC == "Purple" && dec.RC == "Green") || (dec.LC == "Green" && dec.RC == "Purple")) {
                        dec.susan.setPosition(dec.susanbtw12);
                        Log.d("Prep","27");
                        if(dec.FC == "Purple"){
                            dec.purple2 = "Back";
                            Log.d("Prep","57");
                        } else if(dec.FC == "Green"){
                            dec.green2 = "Back";
                            Log.d("Prep","58");
                        }
                    } else {
                        dec.susan.setPosition(dec.susanbtw23);
                        Log.d("Prep","28");
                        if(dec.LC == "Purple"){
                            dec.purple2 = "Back";
                            Log.d("Prep","60");
                        } else if(dec.LC == "Green"){
                            dec.green2 = "Back";
                            Log.d("Prep","61");
                        }
                    }


                    // dec.susan.setPosition(dec.susanbtw31);
                }
            } else if ((dec.RC.matches("Purple") && dec.LC.matches("Green")) ||
                    (dec.RC.matches("Green") && dec.LC.matches("Purple"))) {
                Log.d("Prep", "16");
                if (dec.RC.matches("Green")) {
                    Log.d("Prep", "17");
                    dec.green = "Left";
                    if (dec.FC == "Purple"){
                    dec.purple = "Right";
                    } else if (dec.FC == "None") {
                        dec.purple = "Right 2";
                    }


                } else if (dec.RC.matches("Purple")){
                    Log.d("Prep", "33");//
                    dec.purple = "Left";
                    if (dec.FC == "Green"){
                        dec.green = "Right";
                    } else if (dec.FC == "None") {
                        dec.green = "Right 2";
                    }

                }else {
                    Log.d("Prep", "18");
                    dec.green = "Right";
                    dec.purple = "Left";
                }

                if ((dec.susan.getPosition() < (dec.susanpos1 + .01)) && (dec.susan.getPosition() > (dec.susanpos1 - .01))) {
                    Log.d("Prep", "19");
                    dec.susan.setPosition(dec.susanbtw23);
                    if ((dec.LC == "Green") && (dec.RC == "Purple")){
                        dec.green = "Left";
                        dec.purple = "Right";
                        Log.d("Prep", "41");

                    } else if ((dec.LC == "Purple") && (dec.RC == "Green")) {
                        dec.green = "Right";
                        dec.purple = "Left";
                        Log.d("Prep","35");
                    }
                    if(dec.FC == "Purple"){
                        dec.purple2 = "Back";
                        Log.d("Prep","62");
                    } else if(dec.FC == "Green"){
                        dec.green2 = "Back";
                        Log.d("Prep","63");
                    }
                } else if ((dec.susan.getPosition() < (dec.susanpos2 + .01)) && (dec.susan.getPosition() > (dec.susanpos2 - .01))) {
                    Log.d("Prep", "20");
                    if ((dec.LC == "Purple" && dec.FC == "Green") || (dec.LC == "Green" && dec.FC == "Purple")) {
                        dec.susan.setPosition(dec.susanbtw23);
                        Log.d("Prep","22");
                        if ((dec.FC == "Green") && (dec.LC == "Purple")){
                            dec.green = "Left";
                            dec.purple = "Right";
                            Log.d("Prep", "36");

                        } else if ((dec.FC == "Purple") && (dec.LC == "Green")) {
                            dec.green = "Right";
                            dec.purple = "Left";
                            Log.d("Prep","37");
                        }
                        if(dec.RC == "Purple"){
                            dec.purple2 = "Back";
                            Log.d("Prep","64");
                        } else if(dec.RC == "Green"){
                            dec.green2 = "Back";
                            Log.d("Prep","65");
                        }

                    } else {
                        dec.susan.setPosition(dec.susanbtw12);
                        Log.d("Prep","23");
                        Log.d("Prep",dec.FC);
                        Log.d("Prep", dec.RC);
                        Log.d("Prep",dec.LC);

                        if ((dec.FC == "Green") && (dec.RC == "Purple")){
                            dec.green = "Left";
                            dec.purple = "Right";
                            Log.d("Prep", "34");

                            if(dec.LC == "Purple"){
                                dec.purple2 = "Back";
                                Log.d("Prep","66");
                            } else if(dec.LC == "Green"){
                                dec.green2 = "Back";
                                Log.d("Prep","67");
                            }

                        } else if ((dec.FC == "Purple") && (dec.RC == "Green")) {
                            dec.green = "Right";
                            dec.purple = "Left";
                            Log.d("Prep","42");
                            if(dec.LC == "Purple"){
                                dec.purple2 = "Back";
                                Log.d("Prep","68");
                            } else if(dec.LC == "Green"){
                                dec.green2 = "Back";
                                Log.d("Prep","69");
                            }
                        } else if (dec.FC == "None"){
                            Log.d("Prep", "43");
                            if (dec.RC == "Purple"){
                                dec.purple = "Left";
                                dec.green = "Right 2";
                                Log.d("Prep", "44");
                            } else if (dec.RC == "Green"){
                                dec.green = "Left";
                                dec.purple = "Right 2";
                                Log.d("Prep","38");

                            }


                        }


                    }

                    //dec.susan.setPosition(dec.susanbtw31);
                } else if ((dec.susan.getPosition() < (dec.susanpos3 + .01)) && (dec.susan.getPosition() > (dec.susanpos3 - .01))) {
                    Log.d("Prep", "21");
                    dec.susan.setPosition(dec.susanbtw12);
                    if ((dec.LC == "Green") && (dec.RC == "Purple")){
                        dec.green = "Left";
                        dec.purple = "Right";
                        Log.d("Prep", "39");

                    } else if ((dec.LC == "Purple") && (dec.RC == "Green")) {
                        dec.green = "Right";
                        dec.purple = "Left";
                        Log.d("Prep","40");
                    }
                    if(dec.FC == "Purple"){
                        dec.purple2 = "Back";
                        Log.d("Prep","70");
                    } else if(dec.FC == "Green"){
                        dec.green2 = "Back";
                        Log.d("Prep","71");
                    }
                }
            }


            dec.susanprep = false;
            preptime = runtime.milliseconds() + 1500;
        }else{
            Log.d("Time PRP",String.valueOf(preptime));
            Log.d("Time RNT",String.valueOf(runtime.milliseconds()));
        }

        //endregion
        //launch green
        if ((gamepad1.b)) {

            telemetry.addData("LaunchGreen FC", dec.FC);
            telemetry.addData("LaunchGreen RC", dec.RC);
            telemetry.addData("LaunchGreen LC", dec.LC);




            //    if (((dec.susan.getPosition() < (dec.susanpos1 - .01)) && (dec.susan.getPosition() > (dec.susanpos1+.01))) && ((dec.susan.getPosition() < (dec.susanpos2 - .01)) && (dec.susan.getPosition() > (dec.susanpos2+.01))) &&
            //          ((dec.susan.getPosition() < (dec.susanpos3 - .01)) && (dec.susan.getPosition() > (dec.susanpos3+.01))) && ((dec.susan.getPosition() < (dec.susanlaunch1 - .01)) && (dec.susan.getPosition() > (dec.susanlaunch1+.01))) &&
            //        ((dec.susan.getPosition() < (dec.susanlaunch2 - .01)) && (dec.susan.getPosition() > (dec.susanlaunch2+.01))) && ((dec.susan.getPosition() < (dec.susanlaunch3 - .01)) && (dec.susan.getPosition() > (dec.susanlaunch3+.01)))) {
            //   dec.susan.setPosition(dec.susanlaunch1);

            //            rapidtime = runtime.milliseconds() + 200; //500?
            //          Log.d("Launch","12");
            //    }

            dec.transfer.setPower(1);
            dec.launch.setPower(.6);

            Log.d("LaunchGreen", "1");
            Log.d("Susan", String.valueOf(dec.susan.getPosition()));
            Log.d("Time LT", String.valueOf(launchtime));
            Log.d("Time RPD", String.valueOf(rapidtime));
            Log.d("Time RNT", String.valueOf(runtime.milliseconds()));

            if (launchtime > runtime.milliseconds()) {
                telemetry.addData("launchtime", "paused");
            }

            if ((launchtime < runtime.milliseconds()) && (rapidtime < runtime.milliseconds())) {
                Log.d("LaunchGreen", "2");

                telemetry.addData("launchtime", "GO");

                if (((dec.susan.getPosition() < (dec.susanbtw12 + .02)) && (dec.susan.getPosition() > (dec.susanbtw12 - .02)))) {

                    launchtime = runtime.milliseconds();
                    dec.launched = true;
                    Log.d("LaunchGreen", "11");

                    if ((dec.green == "Right")) {
                        dec.susan.setPosition(dec.susanlaunch2); // switch switch
                        launchtime = runtime.milliseconds() + longmovetime;
                        dec.launched = true;
                        dec.greenLaunched = true;

                        Log.d("LaunchGreen", "13");
                    } else if (dec.green == "Left") {
                        dec.susan.setPosition(dec.susanlaunch1); //switch switch
                        launchtime = runtime.milliseconds() + movetime;
                        dec.launched = true;
                        dec.greenLaunched = true;

                        Log.d("LaunchGreen", "14");
                    } else if(dec.green == "Right 2"){
                        dec.susan.setPosition(dec.susanlaunch3); //switch switch
                        launchtime = runtime.milliseconds() + longmovetime;
                        dec.launched = true;
                        dec.greenLaunched = true;

                        Log.d("LaunchGreen", "24");

                    }else {
                        dec.susan.setPosition(dec.susanbtw12);
                        rapidtime = runtime.milliseconds() + 500; //500?
                        telemetry.addData("no","green");
                        Log.d("LaunchGreen", "16");
                    }


                } else if ((dec.susan.getPosition() < (dec.susanbtw23 + .02)) && (dec.susan.getPosition() > (dec.susanbtw23 - .02))) {
                    launchtime = runtime.milliseconds();
                    dec.launched = true;
                    Log.d("LaunchGreen", "20");

                    if ((dec.green == "Right")) {
                        dec.susan.setPosition(dec.susanlaunch3); //
                        launchtime = runtime.milliseconds() + longmovetime;
                        dec.launched = true;
                        dec.greenLaunched = true;

                        Log.d("LaunchGreen", "21");
                    } else if (dec.green == "Left") {
                        dec.susan.setPosition(dec.susanlaunch2); //
                        launchtime = runtime.milliseconds() + movetime;
                        dec.launched = true;
                        dec.greenLaunched = true;

                        Log.d("LaunchGreen", "22");
                    } else {
                        dec.susan.setPosition(dec.susanbtw23);
                        rapidtime = runtime.milliseconds() + 500; //500?
                        telemetry.addData("no","green");
                        Log.d("LaunchGreen", "23");
                    }
                } else if (dec.purpleLaunched) {
                    Log.d("LaunchGreen", "30");
                    if(!(dec.purple == "Right 2")) {
                        if (((dec.susan.getPosition() < (dec.susanlaunch1 + .02)) && (dec.susan.getPosition() > (dec.susanlaunch1 - .02))) || ((dec.susan.getPosition() < (dec.susanlaunch3 + .02)) && (dec.susan.getPosition() > (dec.susanlaunch3 - .02)))) {
                            dec.susan.setPosition(dec.susanlaunch2);
                            launchtime = runtime.milliseconds() + movetime;
                            dec.launched = true;
                            dec.greenLaunched = true;

                            Log.d("LaunchGreen", "27");
                        } else if (((dec.susan.getPosition() < (dec.susanlaunch2 + .02)) && (dec.susan.getPosition() > (dec.susanlaunch2 - .02)))) {
                            if (dec.green == "Right") {
                                dec.susan.setPosition(dec.susanlaunch3);
                                launchtime = runtime.milliseconds() + movetime;
                                dec.launched = true;
                                dec.greenLaunched = true;

                                Log.d("LaunchGreen", "28");
                            } else if (dec.green == "Left") {
                                dec.susan.setPosition(dec.susanlaunch1);
                                launchtime = runtime.milliseconds() + movetime;
                                dec.launched = true;
                                dec.greenLaunched = true;

                                Log.d("LaunchGreen", "29");

                            }


                        }

                    } else {
                        dec.susan.setPosition(dec.susanlaunch1);
                        launchtime = runtime.milliseconds() + movetime;
                        dec.launched = true;
                        dec.greenLaunched = true;

                        Log.d("LaunchGreen", "31");
                    }

                } else {
                    Log.d("LaunchGreen","26");
                }


            }


            Log.d("Launch", "Timer: " + ((launchtime + 500) < runtime.milliseconds()));


            //

        }else if
        (((launchtime + launchDelay) > runtime.milliseconds())) {
            dec.transferPower = 1;
            dec.transfer.setPower(1);
            dec.launch.setPower(.6);

            Log.d("Transfer", "Transfer On");
        } else {
            dec.launched = false;
            // Log.d("Transfer", "Transfer Off");
        }

        if ((gamepad1.x)) {

            telemetry.addData("LaunchPurple FC", dec.FC);
            telemetry.addData("LaunchPurple RC", dec.RC);
            telemetry.addData("LaunchPurple LC", dec.LC);




            //    if (((dec.susan.getPosition() < (dec.susanpos1 - .01)) && (dec.susan.getPosition() > (dec.susanpos1+.01))) && ((dec.susan.getPosition() < (dec.susanpos2 - .01)) && (dec.susan.getPosition() > (dec.susanpos2+.01))) &&
            //          ((dec.susan.getPosition() < (dec.susanpos3 - .01)) && (dec.susan.getPosition() > (dec.susanpos3+.01))) && ((dec.susan.getPosition() < (dec.susanlaunch1 - .01)) && (dec.susan.getPosition() > (dec.susanlaunch1+.01))) &&
            //        ((dec.susan.getPosition() < (dec.susanlaunch2 - .01)) && (dec.susan.getPosition() > (dec.susanlaunch2+.01))) && ((dec.susan.getPosition() < (dec.susanlaunch3 - .01)) && (dec.susan.getPosition() > (dec.susanlaunch3+.01)))) {
            //   dec.susan.setPosition(dec.susanlaunch1);

            //            rapidtime = runtime.milliseconds() + 200; //500?
            //          Log.d("Launch","12");
            //    }

            dec.transfer.setPower(1);
            dec.launch.setPower(.6);

            Log.d("LaunchPurple", "1");
            Log.d("Susan", String.valueOf(dec.susan.getPosition()));
            Log.d("Time LT", String.valueOf(launchtime));
            Log.d("Time RPD", String.valueOf(rapidtime));
            Log.d("Time RNT", String.valueOf(runtime.milliseconds()));

            if (launchtime > runtime.milliseconds()) {
                telemetry.addData("launchtime", "paused");
            }

            if ((launchtime < runtime.milliseconds()) && (rapidtime < runtime.milliseconds())) {
                Log.d("LaunchPurple", "2");

                telemetry.addData("launchtime", "GO");

                if ((dec.susan.getPosition() < (dec.susanbtw12 + .02)) && (dec.susan.getPosition() > (dec.susanbtw12 - .02))) {

                    launchtime = runtime.milliseconds();
                    dec.launched = true;
                    Log.d("LaunchPurple", "11");

                    if ((dec.purple == "Right")) {
                        dec.susan.setPosition(dec.susanlaunch2); // switch switch
                        launchtime = runtime.milliseconds() + longmovetime;
                        dec.launched = true;
                        dec.purpleLaunched = true;

                        Log.d("LaunchPurple", "13");
                    } else if (dec.purple == "Left") {
                        dec.susan.setPosition(dec.susanlaunch1); //switch switch
                        launchtime = runtime.milliseconds() + movetime;
                        dec.launched = true;
                        dec.purpleLaunched = true;

                        Log.d("LaunchPurple", "14");
                    } else if(dec.purple == "Right 2"){
                        dec.susan.setPosition(dec.susanlaunch3); //switch switch
                        launchtime = runtime.milliseconds() + longmovetime;
                        dec.launched = true;
                        dec.purpleLaunched = true;

                        Log.d("LaunchPurple", "24");

                    }else {
                        dec.susan.setPosition(dec.susanbtw12);
                        rapidtime = runtime.milliseconds() + 500; //500?
                        telemetry.addData("no","purple");
                        Log.d("LaunchPurple", "16");
                    }


                } else if ((dec.susan.getPosition() < (dec.susanbtw23 + .02)) && (dec.susan.getPosition() > (dec.susanbtw23 - .02))) {
                    launchtime = runtime.milliseconds();
                    dec.launched = true;
                    Log.d("LaunchPurple", "20");

                    if ((dec.purple == "Right")) {
                        dec.susan.setPosition(dec.susanlaunch3); //
                        launchtime = runtime.milliseconds() + longmovetime;
                        dec.launched = true;
                        dec.purpleLaunched = true;

                        Log.d("LaunchPurple", "21");
                    } else if (dec.purple == "Left") {
                        dec.susan.setPosition(dec.susanlaunch2); //
                        launchtime = runtime.milliseconds() + movetime;
                        dec.launched = true;
                        dec.purpleLaunched = true;

                        Log.d("LaunchPurple", "22");
                    } else {
                        dec.susan.setPosition(dec.susanbtw23);
                        rapidtime = runtime.milliseconds() + 500; //500?
                        telemetry.addData("no","purple");
                        Log.d("LaunchPurple", "23");
                    }
                } else if (dec.greenLaunched) {
                    Log.d("LaunchPurple", "30");

                    if (!(dec.green == "Right 2")) {
                        if (((dec.susan.getPosition() < (dec.susanlaunch1 + .02)) && (dec.susan.getPosition() > (dec.susanlaunch1 - .02))) || ((dec.susan.getPosition() < (dec.susanlaunch3 + .02)) && (dec.susan.getPosition() > (dec.susanlaunch3 - .02)))) {
                            dec.susan.setPosition(dec.susanlaunch2);
                            launchtime = runtime.milliseconds() + movetime;
                            dec.launched = true;
                            dec.purpleLaunched = true;

                            Log.d("LaunchPurple", "27");
                        } else if (((dec.susan.getPosition() < (dec.susanlaunch2 + .02)) && (dec.susan.getPosition() > (dec.susanlaunch2 - .02)))) {
                            if (dec.purple == "Right") {
                                dec.susan.setPosition(dec.susanlaunch3);
                                launchtime = runtime.milliseconds() + movetime;
                                dec.launched = true;
                                dec.purpleLaunched = true;

                                Log.d("LaunchPurple", "28");
                            } else if (dec.purple == "Left") {
                                dec.susan.setPosition(dec.susanlaunch1);
                                launchtime = runtime.milliseconds() + movetime;
                                dec.launched = true;
                                dec.purpleLaunched = true;

                                Log.d("LaunchPurple", "29");

                            }


                        }

                    } else {
                        dec.susan.setPosition(dec.susanlaunch1);
                        launchtime = runtime.milliseconds() + movetime;
                        dec.launched = true;
                        dec.purpleLaunched = true;

                        Log.d("LaunchPurple", "31");
                    }

                } else if (dec.purpleLaunched){
                    Log.d("LaunchPurple","32");
                    if (((dec.susan.getPosition() < (dec.susanlaunch1 + .02)) && (dec.susan.getPosition() > (dec.susanlaunch1 - .02))) || ((dec.susan.getPosition() < (dec.susanlaunch3 + .02)) && (dec.susan.getPosition() > (dec.susanlaunch3 - .02)))) {
                       // dec.susan.setPosition(dec.susanlaunch2);
                      //  launchtime = runtime.milliseconds() + movetime;
                      //  dec.launched = true;
                      //  dec.purpleLaunched = true;
                            //find way to temp stop transfer
                        Log.d("LaunchPurple", "33");/*
                        if(!dec.transferCycle){
                            transfertime = runtime.milliseconds() + 1000;

                        }
                         dec.transferCycle = true;
                         dec.transfer.setPower(0);

                        if(transfertime < runtime.milliseconds()){
                            if (((dec.susan.getPosition() < (dec.susanlaunch1 + .02)) && (dec.susan.getPosition() > (dec.susanlaunch1 - .02)))){
                                dec.susan.setPosition(dec.susanbtw);
                            }
                        }
                        */


                    } else if (((dec.susan.getPosition() < (dec.susanlaunch2 + .02)) && (dec.susan.getPosition() > (dec.susanlaunch2 - .02)))) {
                        if (dec.purple == "Right") {
                            dec.susan.setPosition(dec.susanlaunch3);
                            launchtime = runtime.milliseconds() + movetime;
                            dec.launched = true;
                            dec.purpleLaunched = true;

                            Log.d("LaunchPurple", "34");
                        } else if (dec.purple == "Left") {
                            dec.susan.setPosition(dec.susanlaunch1);
                            launchtime = runtime.milliseconds() + movetime;
                            dec.launched = true;
                            dec.purpleLaunched = true;

                            Log.d("LaunchPurple", "35");

                        }


                    }

                }else {
                    Log.d("LaunchPurple","26");
                }

            }


            Log.d("Launch", "Timer: " + ((launchtime + 500) < runtime.milliseconds()));


            //

        }else if
        (((launchtime + launchDelay) > runtime.milliseconds())) {
            dec.transferPower = 1;
            dec.transfer.setPower(1);
            dec.launch.setPower(.6);

            Log.d("Transfer", "Transfer On");
        } else {
            dec.launched = false;
            // Log.d("Transfer", "Transfer Off");
        }


        telemetry.addData("green is", dec.green);
        telemetry.addData("purple is", dec.purple);
        telemetry.addData("purple 2 is", dec.purple2);
        telemetry.addData("green 2 is", dec.green2);

        telemetry.update();


    }


}
