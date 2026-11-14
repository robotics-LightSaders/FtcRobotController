/*
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;



@TeleOp(name="PIDControllerTest", group="FTC")
public class PIDControllerTest extends LinearOpMode {

    private DcMotorEx launch; // Use DcMotorEx for velocity control
    private ElapsedTime runtime = new ElapsedTime();

    // PID gains - these will need to be tuned for your specific robot
    private double kP = 0.015; // Proportional gain
    private double kI = 0.007;   // Integral gain
    private double kD = 0.0;   // Derivative gain
    private double kF = 0.0005;   // Feed-forward gain (optional but recommended for velocity control)

    private double targetVelocity = 0; // Desired flywheel velocity in ticks/sec
    private double integralSum = 0;
    private double lastError = 0;

    @Override
    public void runOpMode() {
        launch = hardwareMap.get(DcMotorEx.class, "launch");

        // Set the motor to run using encoders for velocity control
        launch.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // Example: Set target velocity based on a gamepad button
            if (gamepad1.a) {
                targetVelocity = 1500; // Example target velocity
            } else if (gamepad1.b) {
                targetVelocity = 0; // Stop the flywheel
            }

            if (gamepad1.dpad_up) {
                kP = kP + .0001;
            } else if (gamepad1.dpad_down) {
                kP = kP - .0001;
            }
            if (gamepad1.y) {
                kI = kI + .00001;
            } else if (gamepad1.x) {
                kI = kI - .00001;
            }

            if (gamepad1.dpad_right) {
                kD = kD + .00001;
            } else if (gamepad1.dpad_left) {
                kD = kD - .00001;
            }
            if (gamepad1.right_bumper) {
                kF = kF + .000001;
            } else if (gamepad1.left_bumper) {
                kF = kF - .000001;
            }

            // Get current velocity from the motor's encoder
            double currentVelocity = launch.getVelocity();

            // Calculate error
            double error = targetVelocity - currentVelocity;

            // Calculate integral sum
            integralSum += error * runtime.seconds();

            // Calculate derivative term
            double derivative = (error - lastError) / runtime.seconds();

            // Calculate PID output
            double output = (kP * error) + (kI * integralSum) + (kD * derivative) + (kF * targetVelocity);

            if (output > 1) {
                output = 1;
            } else if (output < 0){
                output = 0;
            }
            // Set motor power based on PID output
            //launch.setPower(output);
            launch.setVelocityPIDFCoefficients(kP,kI, kD, kF);

            // Update last error and reset runtime for next iteration
            lastError = error;
            runtime.reset();

            telemetry.addData("Target Velocity", targetVelocity);
            telemetry.addData("Current Velocity", currentVelocity);
            telemetry.addData("Error", error);
            telemetry.addData("Motor Power", output);
            telemetry.addData("kP",kP);
            telemetry.addData("kI",kI);
            telemetry.addData("kD",kD);
            telemetry.addData("kF",kF);


            telemetry.update();
        }
    }
}
*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

//@Disabled
@TeleOp
public class BiobuzzPIDControllerTest extends LinearOpMode {
    BiobuzzConstants bio = new BiobuzzConstants();

    private ElapsedTime runtime = new ElapsedTime();

    // Copy your PIDF Coefficients here
    //  private double kP = 0.0; // Proportional gain // .015
    //   private double kI = 0.0;   // Integral gain //0.007
    //  private double kD = 0.0;   // Derivative gain //0.0
    //  private double kF = 0.0;   // Feed-forward gain (optional but recommended for velocity control) //0.0005

    double vel = 2535; //2750, 3550

    //these are the coefficients found, they should work at multiple velocitys.
    //set the velocity with           myMotor.setVelocity(targetvelocity);//9.95     0.075     10.5    13.4
   // public static PIDFCoefficients MOTOR_VELO_PID = new PIDFCoefficients(150, 0, 0, 14.13); //60, 13.91
    public static PIDFCoefficients MOTOR_VELO_PID_MAX = new PIDFCoefficients(144, 0, 0.5, 13.28); //hood @ 0.7, 55, 13.56

    double p = 67; //60
    double i = 0;
    double d = 0;
    double f = 15.2; //13.91
    double t = 600; //300, 200
    double stime = 0;
    boolean s = false;
    boolean s1 = false;

    boolean manual = true;

    double starttime = 0;
    double lasttp = 0;
    double tstart = 0;
    double tv = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        // SETUP MOTOR //
        // Change my id
        DcMotorEx myMotor = hardwareMap.get(DcMotorEx.class, "flywheel");


        bio.fl = hardwareMap.get(DcMotor.class, "fl");
        bio.fr = hardwareMap.get(DcMotor.class, "fr");
        bio.br = hardwareMap.get(DcMotor.class, "br");
        bio.bl = hardwareMap.get(DcMotor.class, "bl");



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

       // bio.flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    //    bio.flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(0, 0, 0, 0));



        // Reverse as appropriate
        // myMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Turns on bulk reading
        for (LynxModule module : hardwareMap.getAll(LynxModule.class)) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        // RUE limits max motor speed to 85% by default
        // Raise that limit to 100%
        MotorConfigurationType motorConfigurationType = myMotor.getMotorType().clone();
        motorConfigurationType.setAchieveableMaxRPMFraction(1.0);
        myMotor.setMotorType(motorConfigurationType);

        // Turn on RUN_USING_ENCODER
        myMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set PIDF Coefficients with voltage compensated feedforward value

        /*
        myMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
                MOTOR_VELO_PID.p, MOTOR_VELO_PID.i, MOTOR_VELO_PID.d,
                MOTOR_VELO_PID.f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
        ));*/

        // Insert whatever other initialization stuff you do here
        bio.intakef.setPower(0);
        bio.transfer.setPower(0);
        bio.hood.setPosition(bio.hooddown);
       // bio.hood.setPosition(0.38); down pos add later

        bio.configureOdo(true);
        bio.odo.setPosition(bio.odoorigin);

        //dec.initTaxon();


        waitForStart();

        if (isStopRequested()) return;

        while (!isStopRequested()) {
            bio.odo.update();




            if (gamepad2.right_trigger > 0.2) {
                bio.intakef.setPower(gamepad2.right_trigger);
            } else if (gamepad2.left_trigger > 0.2) {
                bio.intakef.setPower(-gamepad2.left_trigger);
            } else if (!gamepad2.y) {
                bio.intakef.setPower(0);
            }

            if (gamepad2.right_bumper) {
                bio.intakeb.setPower(1);
            } else if (gamepad2.left_bumper) {
                bio.intakeb.setPower(-1);
            } else if (!gamepad2.y)  {
                bio.intakeb.setPower(0);
            }

            if (gamepad2.y) {
                bio.transfer.setPower(1);
                bio.intakef.setPower(1);
                bio.intakeb.setPower(1);

            } else {
                bio.transfer.setPower(0);

            }











            /*if (gamepad1.right_stick_button && (mantime < runtime.milliseconds())) {
                if (manual) {
                    manual = false;
                } else {
                    manual = true;
                }

                mantime = runtime.milliseconds() + 500;
            }*

            /*dec.taxon.update();

            if (gamepad1.right_trigger > 0.2) {
                dec.taxon.setTargetRotation(dec.taxon.getTargetRotation() + 10);
            } else if (gamepad1.left_trigger > 0.2 ){
                dec.taxon.setTargetRotation(dec.taxon.getTargetRotation() - 10);
            }

            dec.div = 60;
            dec.min = 1;
            dec.z3 = 5;

            if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) > 120) { //200, 75, 90, 100
                dec.turretpwrcoeff = 12.5; //10
            } else if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) > dec.ttol) {
                if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) < 30) {
                    if (Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) > dec.z3) {
                        dec.div = 20;
                    }
                } else {
                    dec.div = dec.div / 2; //4?
                }

                dec.turretpwrcoeff = Range.clip((Math.abs(dec.taxon.getTotalRotation() -
                        dec.taxon.getTargetRotation()) / dec.div), dec.min, 10); //1, 1.5, 1.2

                if (((Math.abs(dec.taxon.getTotalRotation() - dec.taxon.getTargetRotation()) < dec.z3))) { //>, 20
                    dec.turretpwrcoeff = Range.clip((dec.turretpwrcoeff / 2), 1, 5); //dec.odo.getPosition()
                }
            } else {
                dec.turretpwrcoeff = 0;
            }

            if (dec.launchy && (dec.angvel > 1)) {
                dec.turretpwrcoeff = dec.turretpwrcoeff * 2;
            }

            dec.turretpower = Range.clip((dec.tminp * dec.turretpwrcoeff), 0.08, 1); //0.09, 7

            if (dec.taxon.getTotalRotation() > (dec.taxon.getTargetRotation() + dec.ttol)) {
                dec.turret.setPower(dec.turretpower);
            } else if (dec.taxon.getTotalRotation() < (dec.taxon.getTargetRotation() - dec.ttol)) {
                dec.turret.setPower(-dec.turretpower);
            } else {
                dec.turret.setPower(0);
            }*/

            if (manual) {
                if (gamepad2.b) {
                    bio.hood.setPosition(bio.hood.getPosition() + 0.005);
                } else if (gamepad2.x) {
                    bio.hood.setPosition(bio.hood.getPosition() - 0.005);
                }



                if (bio.hood.getPosition() > bio.hooddown) {
                    bio.hood.setPosition(bio.hooddown);
                } else if (bio.hood.getPosition() < bio.hoodup) {
                    bio.hood.setPosition(bio.hoodup);
                }


            }



            if (bio.odo.getPosition().getY(DistanceUnit.INCH) > 0) {
                bio.side = "Back";
            } else {
                bio.side = "Audience";
            }

            if (bio.teamColor == "red") {
                if (bio.side == "Back") {
                    bio.goal = bio.redGoalBack;

                } else {
                    bio.goal = bio.redGoalAud;

                }
            } else {
                if (bio.side == "Back") {
                    bio.goal = bio.blueGoalBack;
                } else {
                    bio.goal = bio.blueGoalAud;
                }
            }

            bio.distToGoalTot = bio.goalDC(bio.odo.getPosition(), bio.goal);

            /* testing

            if (!manual) {
                if ((dec.goaldistanceT > 112)) {
                    dec.hood.setPosition(0.7);
                    vel = (2.5 * dec.goaldistanceT) + 3237.5;
                    p = 144;
                    i = 0;
                    d = 0.75;
                    f = 13.28;
                } else {
                    dec.hood.setPosition(Range.clip(((0.00723 * dec.goaldistanceT) - 0.311), 0, 0.6));
                    vel = (10.482 * dec.goaldistanceT) + 1799.3;
                    p = 150;
                    i = 0;
                    d = 0;
                    f = 14.13;
                }
            } */

            if (manual) {
                if (gamepad2.dpad_up) {
                    vel = vel + 5;
                } else if (gamepad2.dpad_down) {
                    vel = vel - 5;
                }

                if (vel < 0) {
                    vel = 0;
                } else if (vel > (5800)) {
                    vel = 5800;
                }

                if (gamepad1.dpad_up) {
                    p = p + 0.05;
                } else if (gamepad1.dpad_down) {
                    p = p - 0.05;
                }


                /*
                if (gamepad1.dpad_right) {
                    i = i + 0.0001;
                } else if (gamepad1.dpad_left) {
                    i = i - 0.0001;
                }

                if (gamepad1.y) {
                    d = d + 0.001;
                } else if (gamepad1.a) {
                    d = d - 0.001;
                }

                 */

                if (gamepad1.b) {
                    f = f + 0.01;
                } else if (gamepad1.x) {
                    f = f - 0.01;
                }
            }

            myMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
                    p, i, d, f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
            ));

            /* MARK

            if (gamepad2.dpad_right) {
                myMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
                        MOTOR_VELO_PID.p, MOTOR_VELO_PID.i, MOTOR_VELO_PID.d,
                        MOTOR_VELO_PID.f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
                ));
            } else if (gamepad2.dpad_left) {
                myMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
                        MOTOR_VELO_PID_MAX.p, MOTOR_VELO_PID_MAX.i, MOTOR_VELO_PID_MAX.d,
                        MOTOR_VELO_PID_MAX.f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
                ));
            }

            */

            // Do your opmode stuff
            // 28 tics = 1 rev
            // 500 tics = 1071 rpm
            // 1000 tics = 2143 rpm
            // 1500 tics = 3214 rpm
            // 100 tics = 46.7 rpm
            myMotor.setVelocity(vel / 60 * 28);

            double Rpm = myMotor.getVelocity() / 28 * 60;


            telemetry.addData("Manual: ", manual);

            telemetry.addData("p", p);
            telemetry.addData("i", i);
            telemetry.addData("d", d);
            telemetry.addData("f", f);
            telemetry.addData("time", t);

            /*telemetry.addData("Turret Target: ", dec.taxon.getTargetRotation());
            telemetry.addData("Turret At: ", dec.taxon.getTotalRotation());*/

            telemetry.addData("velocity tps", myMotor.getVelocity());
            telemetry.addData("velocity rpm", Rpm);
            telemetry.addData("velocity target", vel);
            telemetry.addData("Hood Pos: ", bio.hood.getPosition());
            telemetry.addData("Distance: ", bio.distToGoalTot);
            telemetry.addLine(String.format("OdoPos: %6.2f %6.2f %6.2f", bio.odo.getPosition().getX(DistanceUnit.INCH),
                    bio.odo.getPosition().getY(DistanceUnit.INCH), (bio.odo.getPosition().getHeading(AngleUnit.DEGREES))));

            telemetry.update();
        }
    }
}




