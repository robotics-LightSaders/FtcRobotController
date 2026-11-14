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

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Disabled
@TeleOp
public class PIDControllerTest extends LinearOpMode {
    DecodeConstants dec = new DecodeConstants();

    private ElapsedTime runtime = new ElapsedTime();

    // Copy your PIDF Coefficients here
    //  private double kP = 0.0; // Proportional gain // .015
    //   private double kI = 0.0;   // Integral gain //0.007
    //  private double kD = 0.0;   // Derivative gain //0.0
    //  private double kF = 0.0;   // Feed-forward gain (optional but recommended for velocity control) //0.0005

    double vel = 3200; //2750, 3550

    //these are the coefficients found, they should work at multiple velocitys.
    //set the velocity with           myMotor.setVelocity(targetvelocity);//9.95     0.075     10.5    13.4
    public static PIDFCoefficients MOTOR_VELO_PID = new PIDFCoefficients(150, 0, 0, 14.13); //60, 13.91
    public static PIDFCoefficients MOTOR_VELO_PID_MAX = new PIDFCoefficients(144, 0, 0.5, 13.28); //hood @ 0.7, 55, 13.56

    double p = 150; //60
    double i = 0;
    double d = 0;
    double f = 14.13; //13.91
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
        DcMotorEx myMotor = hardwareMap.get(DcMotorEx.class, "launch");

        dec.hood = hardwareMap.get(Servo.class, "hood");
        dec.susan = hardwareMap.get(Servo.class, "susan");
        dec.intakedos = hardwareMap.get(CRServo.class, "intakedos");
        dec.intake = hardwareMap.get(DcMotor.class, "intake");
        dec.transfer = hardwareMap.get(DcMotor.class, "transfer");
        dec.odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");
        dec.turret = hardwareMap.get(CRServo.class, "turret");
        dec.turretenc = hardwareMap.get(AnalogInput.class, "tenc");
        dec.susanenc = hardwareMap.get(AnalogInput.class, "senc");
        dec.oq = hardwareMap.get(OctoQuadFWv3.class, "octoquad");

        dec.transfer.setDirection(DcMotor.Direction.REVERSE);
        dec.intake.setDirection(DcMotor.Direction.REVERSE);

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
        myMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
                MOTOR_VELO_PID.p, MOTOR_VELO_PID.i, MOTOR_VELO_PID.d,
                MOTOR_VELO_PID.f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
        ));

        // Insert whatever other initialization stuff you do here
        dec.intake.setPower(0);
        dec.transfer.setPower(0);
        dec.hood.setPosition(0.38); //dec.hooddown, 0.77 or 0
        dec.susan.setPosition(dec.susanpos1);

        dec.configureOdo(true);
        dec.odo.setPosition(dec.Rstart);

        //dec.initTaxon();
        dec.initOQ();
        dec.tpos = dec.oq.readAllEncoderData().positions[1];
        dec.turret.setPower(0);
        dec.susanV = dec.susanenc.getVoltage();

        waitForStart();

        if (isStopRequested()) return;

        while (!isStopRequested()) {
            dec.odo.update();
            dec.tpos = ((-0.365551) * (dec.oq.readAllEncoderData().positions[1])) + 394;

            if (gamepad1.right_trigger > 0.2) {
                dec.intake.setPower(gamepad1.right_trigger);
            } else {
                dec.intake.setPower(0);
            }

            if (Math.abs(gamepad2.left_stick_x) > 0.2) {
                dec.turret.setPower(gamepad2.left_stick_x);
            } else {
                dec.turret.setPower(0);
            }

            if ((lasttp == 0) && (dec.turret.getPower() != 0)) {
                tstart = dec.tpos;
                starttime = runtime.milliseconds();
            }

            if ((lasttp != 0) && (dec.turret.getPower() == 0)) {
                tv = (dec.tpos - tstart) / ((runtime.milliseconds() - starttime) / 1000);
            }

            lasttp = dec.turret.getPower();

            telemetry.addData("Turret Pos", dec.tpos);
            telemetry.addData("Turret Vel (degrees/sec)", tv);

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
                    dec.hood.setPosition(dec.hood.getPosition() + 0.01);
                } else if (gamepad2.x) {
                    dec.hood.setPosition(dec.hood.getPosition() - 0.01);
                }

                if (dec.hood.getPosition() < dec.hooddown) {
                    dec.hood.setPosition(dec.hooddown);
                } else if (dec.hood.getPosition() > dec.hoodup) {
                    dec.hood.setPosition(dec.hoodup);
                }

                if (gamepad2.y) {
                    t = t + 5;
                } else if (gamepad2.a) {
                    t = t - 5;
                }
            }

            if (Math.abs(gamepad1.right_stick_y) > 0.5) {
                dec.susan.setPosition(dec.susanpos1);
            } else if (Math.abs(gamepad1.left_stick_y) > 0.5) {
                dec.susan.setPosition(dec.susanpos2);
            } else if (gamepad1.left_stick_button) {
                dec.susan.setPosition(dec.susanpid3); //past3, pos3?
                stime = runtime.milliseconds() + (t / 2);
                s = true;
            } else if (!s1 && !s && (((stime + (t * 4)) < runtime.milliseconds()))) {
                dec.susan.setPosition(dec.susanpos3);
                dec.transfer.setPower(0);
            }

            if ((stime < runtime.milliseconds()) && s) {
                dec.transfer.setPower(1);
                s = false;
                s1 = true;
            }

            if (((stime + t) < runtime.milliseconds()) && s1) {
                dec.susan.setPosition(dec.susanlaunch1); //3
                s1 = false;
            }

            dec.goaldistanceX = dec.Rgoal.getX(DistanceUnit.INCH) - dec.odo.getPosition().getX(DistanceUnit.INCH);
            dec.goaldistanceY = dec.Rgoal.getY(DistanceUnit.INCH) - dec.odo.getPosition().getY(DistanceUnit.INCH);
            dec.goaldistanceT = Math.sqrt(Math.pow(dec.goaldistanceX, 2) + Math.pow(dec.goaldistanceY, 2));

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
            }

            if (manual) {
                if (gamepad1.right_bumper) {
                    vel = vel + 5;
                } else if (gamepad1.left_bumper) {
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

                if (gamepad1.b) {
                    f = f + 0.01;
                } else if (gamepad1.x) {
                    f = f - 0.01;
                }
            }

            myMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
                    p, i, d, f * 12 / hardwareMap.voltageSensor.iterator().next().getVoltage()
            ));

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

            // Do your opmode stuff
            // 28 tics = 1 rev
            // 500 tics = 1071 rpm
            // 1000 tics = 2143 rpm
            // 1500 tics = 3214 rpm
            // 100 tics = 46.7 rpm
            myMotor.setVelocity(vel / 60 * 28);

            double Rpm = myMotor.getVelocity() / 28 * 60;

            dec.susanV = dec.susanenc.getVoltage();
            dec.susandegreesat = (dec.susanV - dec.susanminV) / dec.susanVrange * dec.susanrange;
            dec.susandegreesto = dec.susan.getPosition() * dec.susanrange;

            telemetry.addData("Susan V: ", dec.susanV);
            telemetry.addData("Susan Degrees At: ", dec.susandegreesat);
            telemetry.addData("Susan Degrees To: ", dec.susandegreesto);

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
            telemetry.addData("Hood Pos: ", dec.hood.getPosition());
            telemetry.addData("Distance: ", dec.goaldistanceT);
            telemetry.addLine(String.format("OdoPos: %6.2f %6.2f %6.2f (+90)", dec.odo.getPosition().getX(DistanceUnit.INCH),
                    dec.odo.getPosition().getY(DistanceUnit.INCH), (dec.odo.getPosition().getHeading(AngleUnit.DEGREES)) + 90));

            telemetry.update();
        }
    }
}




