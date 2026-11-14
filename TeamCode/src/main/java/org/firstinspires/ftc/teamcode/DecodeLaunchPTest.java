//package org.firstinspires.ftc.teamcode;
//
//import android.util.Log;
//import android.util.Size;
//
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.AnalogInput;
//import com.qualcomm.robotcore.hardware.CRServo;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.hardware.PIDFCoefficients;
//import com.qualcomm.robotcore.hardware.Servo;
//import com.qualcomm.robotcore.util.ElapsedTime;
//import com.qualcomm.robotcore.util.Range;
//
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
//@Disabled
//@TeleOp(name = "LaunchP", group = "TeleOp")
////@Disabled
//public class DecodeLaunchPTest extends OpMode {
//    DecodeConstants dec = new DecodeConstants();
//
//    private ElapsedTime runtime = new ElapsedTime();
//    double looptimer = 0;
//    double looptime = 0;
//    double lasttick = 0;
//    double tickchange = 0;
//    double tickspersec = 0;
//
//    double ticksperrev = 28;
//    double Lpower = 0;
//    double deltaMP = 0;
//    double targetRPM = 0;
//    double actualRPM = 0;
//    double atRPMcoeff = 60 / ticksperrev;
//    double P = 0.000172; //range from 1/5800 (0.000172) to 1/580 (0.00172)
//
//    @Override
//    public void init() {
//        dec.launch = hardwareMap.get(DcMotorEx.class, "launch");
//        dec.transfer = hardwareMap.get(DcMotor.class, "transfer");
//
//        dec.hood = hardwareMap.get(Servo.class, "hood");
//        dec.susan = hardwareMap.get(Servo.class, "susan");
//
//        dec.intake = hardwareMap.get(DcMotor.class, "intake");
//        dec.intakedos = hardwareMap.get(CRServo.class, "intakedos");
//
//        dec.f0 = hardwareMap.digitalChannel.get("f0");
//        dec.f1 = hardwareMap.digitalChannel.get("f1");
//
//        dec.transfer.setDirection(DcMotor.Direction.REVERSE);
//        dec.intake.setDirection(DcMotor.Direction.REVERSE);
//
//        dec.launch.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        dec.launch.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        //dec.launch.setVelocityPIDFCoefficients(pidf.p, pidf.i, pidf.d, pidf.f);
//        //dec.launch.setTargetPositionTolerance(5); // In ticks
//
//        dec.launch.setPower(Lpower);
//        dec.intakedos.setPower(0);
//        dec.intake.setPower(0);
//        dec.transfer.setPower(0);
//
//        runtime.reset();
//        looptimer = runtime.milliseconds();
//        lasttick = dec.launch.getCurrentPosition();
//    }
//
//    public void loop() {
//        looptime = runtime.milliseconds() - looptimer;
//        telemetry.addData("Loop Time: ", looptime);
//        looptimer = runtime.milliseconds();
//
//        tickchange = dec.launch.getCurrentPosition() - lasttick;
//        telemetry.addData("Tick Change: ", tickchange);
//        lasttick = dec.launch.getCurrentPosition();
//
//        tickspersec = tickchange / (looptime / 1000);
//        telemetry.addData("Ticks/Sec: ", tickspersec);
//
//        if (gamepad1.left_stick_y < -0.1) {
//            P = P + 0.00001;
//        } else if (gamepad1.left_stick_y > 0.1) {
//            P = P - 0.00001;
//        }
//        telemetry.addData("P: ", P);
//
//        if (gamepad1.right_stick_y < -0.1) {
//            targetRPM = targetRPM + 10;
//        } else if (gamepad1.right_stick_y > 0.1) {
//            targetRPM = targetRPM - 10;
//        }
//        telemetry.addData("Target RPM: ", targetRPM);
//
//        actualRPM = atRPMcoeff * tickspersec;
//        deltaMP = P * (targetRPM - actualRPM);
//        telemetry.addData("Actual RPM: ", actualRPM);
//        telemetry.addData("Delta Power: ", deltaMP);
//
//        dec.launch.setPower(dec.launch.getPower() + deltaMP);
//        telemetry.addData("Launch Power: ", dec.launch.getPower());
//
//        if (gamepad1.right_stick_button) {
//            dec.intakedos.setPower(1);
//            dec.intake.setPower(1);
//        } else if (gamepad1.left_stick_button) {
//            dec.intakedos.setPower(0);
//            dec.intake.setPower(0);
//        }
//
//        if (gamepad1.dpad_down) {
//            dec.susan.setPosition(dec.susanpos1);
//        } else if (gamepad1.dpad_left) {
//            dec.susan.setPosition(dec.susanpos2);
//        } else if (gamepad1.dpad_up) {
//            dec.susan.setPosition(dec.susanpos3);
//        } /*else if (gamepad1.dpad_right) {
//            dec.susan.setPosition(dec.susantest);
//        }*/
//
//        if (gamepad1.right_trigger > 0.1) {
//            dec.susan.setPosition(dec.susan.getPosition() + 0.002);
//        } else if (gamepad1.left_trigger > 0.1) {
//            dec.susan.setPosition(dec.susan.getPosition() - 0.002);
//        }
//
//        if (gamepad1.y) {
//            dec.transfer.setPower(1);
//        } else if (gamepad1.a) {
//            dec.transfer.setPower(0);
//        }
//
//        if (gamepad1.x) {
//            dec.hood.setPosition(dec.hood.getPosition() + 0.01);
//        } else if (gamepad1.b) {
//            dec.hood.setPosition(dec.hood.getPosition() - 0.01);
//        }
//
//        /*if (dec.f0.getState()) {
//            telemetry.addData("Color Seen: ", "Purple");
//        } else if (dec.f1.getState()) {
//            telemetry.addData("Color Seen: ", "Green");
//        } else {
//            telemetry.addData("Color Not Seen", "");
//        }*/
//
//        telemetry.addData("Hood Pos: ", dec.hood.getPosition());
//        telemetry.addData("Susan Pos: ", dec.susan.getPosition());
//        telemetry.addData("IntakeDos Power: ", dec.intakedos.getPower());
//        telemetry.addData("Transfer Power: ", dec.transfer.getPower());
//        telemetry.update();
//    }
//}
